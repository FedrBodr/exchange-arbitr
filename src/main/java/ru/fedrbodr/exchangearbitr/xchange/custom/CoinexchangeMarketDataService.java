package ru.fedrbodr.exchangearbitr.xchange.custom;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.services.exchangereaders.CoinexchangeExchangeReaderImpl;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;

@Service
@Slf4j
public class CoinexchangeMarketDataService implements MarketDataService {
	@Value("#{'${proxy.list}'.split(',')}")
	private List<String> proxyHostPortList;
	private List<Proxy> proxyList;
	private Integer lastUsedProxyIndex = 0;

	private HashMap<Integer, Symbol> coinexchangeIdToSymbol;
	private HashMap<Symbol, Integer> symbolToCoinexchangeMarketId;
	@Autowired
	private SymbolService symbolService;

	@PostConstruct
	public void init() throws IOException {
		proxyList = new ArrayList<>();
		try {
			for (String proxyHostAndPort : proxyHostPortList) {
				String[] split = proxyHostAndPort.split(":");
				proxyList.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(split[0], Integer.parseInt(split[1]))));
			}
		}catch (Exception e){
			throw new IllegalArgumentException("Proxy list in incorrect format! Must be proxy.list: 185.128.215.224:8000,193.93.60.95:8000,193.93.60.236:8000");
		}

		/*TODO refactor this with aop for all init methods*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();

		initMArketCurrencyToSymolMap();
		/*TODO refactor this with aop*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	private void initMArketCurrencyToSymolMap() throws IOException {
		coinexchangeIdToSymbol = new HashMap();
		symbolToCoinexchangeMarketId = new HashMap();
		JSONArray markets = getNewJsonObject("https://www.coinexchange.io/api/v1/getmarkets").getJSONArray("result");
		markets.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			// IT IS CORECT NAMES FORMAT like in org.knowm.xchange.currency.Currency
			Symbol symbol = symbolService.getOrCreateNewSymbol(
					obj.getString("BaseCurrencyCode"),
					obj.getString("MarketAssetCode"));

			int coinexchangeMarketID = obj.getInt("MarketID");
			coinexchangeIdToSymbol.put(coinexchangeMarketID, symbol);

			symbolToCoinexchangeMarketId.put(symbol, coinexchangeMarketID);
		});
	}

	@Override
	public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws IOException {
		throw new NotImplementedException();
	}

	/**
	 * for example see https://www.coinexchange.io/api/v1/getorderbook?market_id=111
	 * */
	@Override
	public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args) throws IOException {
		Proxy proxy = null;
		synchronized (lastUsedProxyIndex) {
			proxy = proxyList.get(lastUsedProxyIndex);
			if (lastUsedProxyIndex < proxyList.size() - 1) {
				lastUsedProxyIndex++;
			} else {
				lastUsedProxyIndex = 0;
			}
		}
		JSONObject orderBookResponce = getNewJsonObject("https://www.coinexchange.io/api/v1/getorderbook?market_id=" +
				symbolToCoinexchangeMarketId.get(symbolService.getOrCreateNewSymbol(currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode())), proxy);
		if(orderBookResponce.isNull("result")){
			log.info("Not correct result for www.coinexchange.io/api/v1/getorderbook?market_id={} for currencyPair ={}",
					symbolToCoinexchangeMarketId.get(symbolService.getOrCreateNewSymbol(currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode())),
					currencyPair);
		}
		JSONObject sellBuyOrders = orderBookResponce.getJSONObject("result");

		JSONArray sellOrders = sellBuyOrders.getJSONArray("SellOrders");
		JSONArray buyOrders = sellBuyOrders.getJSONArray("BuyOrders");

		List<LimitOrder> asks = convertToUniLimitOrdersList(sellOrders);
		List<LimitOrder> bids = convertToUniLimitOrdersList(buyOrders);
		OrderBook orderBook = new OrderBook(new Date(), asks, bids);

		return orderBook;
	}

	private static List<LimitOrder> convertToUniLimitOrdersList(JSONArray orders) {
		List<LimitOrder> limitOrders = new ArrayList<>();

		orders.forEach(item -> {
			JSONObject order = (JSONObject) item;

			Order.OrderType orderType;

			if("sell".equals(order.getString("Type"))){
				orderType = Order.OrderType.ASK;
			}else if("buy".equals(order.getString("Type"))){
				orderType = Order.OrderType.BID;
			}else{
				throw new UnknownFormatConversionException("Don`t known Coinexchange order type");
			}

			BigDecimal originalAmount = order.getBigDecimal("Quantity");
			BigDecimal limitPrice = order.getBigDecimal("Price");

			LimitOrder limitOrder = new LimitOrder(orderType, originalAmount, null, "0", new Date(), limitPrice);

			limitOrders.add(limitOrder);

		});

		return limitOrders;
	}

	@Override
	public Trades getTrades(CurrencyPair currencyPair, Object... args) throws IOException {
		throw new NotImplementedException();
	}

	public HashMap getCoinexchangeIdToMarketSummaryMap() {
		return coinexchangeIdToSymbol;
	}
}
