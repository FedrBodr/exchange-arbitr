package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPosition;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.ExchangeMetaRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;
import ru.fedrbodr.exchangearbitr.xchange.custom.CoinexchangeMarketDataService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;

@Service
@Slf4j
public class CoinexchangeExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private CoinexchangeMarketDataService marketDataService;
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private ExchangeMetaRepository exchangeRepository;
	private Map<String, Boolean> currencyActivityMap;

	@PostConstruct
	private void init() {
		/* TODO move preinit to more convenient place ? */
		exchangeRepository.save(ExchangeMeta.BITTREX);
		exchangeRepository.save(ExchangeMeta.COINEXCHANGE);
		exchangeRepository.save(ExchangeMeta.POLONIEX);
		exchangeRepository.save(ExchangeMeta.BINANCE);
		exchangeRepository.save(ExchangeMeta.HITBTC);
		exchangeRepository.save(ExchangeMeta.KUCOIN);
		exchangeRepository.flush();
		/*TODO refactor this with aop for all init methods*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		currencyActivityMap = new HashMap<>();
		try {
			JSONArray currencies = getNewJsonObject("https://www.coinexchange.io/api/v1/getcurrencies").getJSONArray("result");
			for (Object currency : currencies) {
				JSONObject currencyJsonObj = (JSONObject) currency;
				currencyActivityMap.put(currencyJsonObj.getString("TickerCode"), "online".equals(currencyJsonObj.getString("WalletStatus")));
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		/*TODO refactor this with aop*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		JSONArray marketPositionsArray = getNewJsonObject("https://www.coinexchange.io/api/v1/getmarketsummaries").getJSONArray("result");
		List<MarketPosition> marketPositions = new ArrayList<>();

		marketPositionsArray.forEach(item -> {
			JSONObject jsonObject = (JSONObject) item;
			Symbol symbol = getUnifiedMarketSummary(jsonObject.getInt("MarketID"));
			MarketPosition marketPosition = new MarketPosition(
					ExchangeMeta.COINEXCHANGE,
					symbol,
					jsonObject.getBigDecimal("LastPrice"),
					jsonObject.getBigDecimal("AskPrice"),
					jsonObject.getBigDecimal("BidPrice"),
					isSymbolActive(symbol));

			marketPositions.add(marketPosition);
		});

		/*marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();*/
		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositions));
		marketPositionFastRepository.flush();
	}

	private boolean isSymbolActive(Symbol symbol) {
		if (currencyActivityMap.get(symbol.getQuoteName()) != null && currencyActivityMap.get(symbol.getQuoteName())
				&& currencyActivityMap.get(symbol.getBaseName()) != null && currencyActivityMap.get(symbol.getBaseName())) {
			return true;
		}
		return false;
	}

	private Symbol getUnifiedMarketSummary(int coinexchangeMarketID) {
		Symbol symbol = (Symbol) marketDataService.getCoinexchangeIdToMarketSummaryMap().get(coinexchangeMarketID);
		if (symbol == null) {
			log.info("Found Market Summary at coinexchange for non existen symbol");
			/* init all coinexchange markets summary again*/
			this.init();
		}
		return symbol;
	}


}
