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
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.services.exchangereaders.CoinexchangeExchangeReaderImpl;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;

@Service
@Slf4j
public class CoinexchangeMarketDataService implements MarketDataService {
	public CoinexchangeMarketDataService() {
	}

	private HashMap<Integer, SymbolPair> coinexchangeIdToSymbolPair;
	private HashMap<SymbolPair, Integer> symbolPairToCoinexchangeMarketId;
	@Autowired
	private SymbolService symbolService;

	@PostConstruct
	public void init() throws IOException {
		/*TODO refactor this with aop for all init methods*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();

		coinexchangeIdToSymbolPair = new HashMap();
		symbolPairToCoinexchangeMarketId = new HashMap();
		JSONArray markets = getNewJsonObject("https://www.coinexchange.io/api/v1/getmarkets").getJSONArray("result");
		markets.forEach(item -> {
			JSONObject obj = (JSONObject) item;
			// IT IS CORECT NAMES FORMAT like in org.knowm.xchange.currency.Currency
			SymbolPair symbolPair = symbolService.getOrCreateNewSymbol(
					obj.getString("BaseCurrencyCode"),
					obj.getString("MarketAssetCode"));

			int coinexchangeMarketID = obj.getInt("MarketID");
			coinexchangeIdToSymbolPair.put(coinexchangeMarketID, symbolPair);

			symbolPairToCoinexchangeMarketId.put(symbolPair, coinexchangeMarketID);
		});
		/*TODO refactor this with aop*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
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
		JSONObject sellBuyOrders = getNewJsonObject("https://www.coinexchange.io/api/v1/getorderbook?market_id="+
				symbolPairToCoinexchangeMarketId.get(symbolService.getOrCreateNewSymbol(currencyPair.counter.getSymbol(),currencyPair.base.getSymbol())))
				.getJSONObject("result");

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
		return coinexchangeIdToSymbolPair;
	}
}
