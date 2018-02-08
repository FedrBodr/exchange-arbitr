package ru.fedrbodr.exchangearbitr.services.exchanges;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexCurrencyInfo;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;
/**
 * Universal symbol format is BTC-BCN Poloniex is BTC_BCN
 *
 * */
@Service
@Slf4j
public class PoloniexExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;
	private PoloniexMarketDataServiceRaw poloniexMarketDataServiceRaw = (PoloniexMarketDataServiceRaw)
			(ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName())).getMarketDataService();
	private Map<String, PoloniexCurrencyInfo> poloniexCurrencyMap;

	@PostConstruct
	private void init(){
		/*TODO refactor this with aop*/
		log.info(PoloniexExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		try {
			poloniexCurrencyMap = poloniexMarketDataServiceRaw.getPoloniexCurrencyInfo();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		/*TODO refactor this with aop*/
		log.info(PoloniexExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		/* TODO maybe rewrite to use poloniexMarketDataServiceRaw.getAllPoloniexTickers();*/
		JSONObject json = getNewJsonObject("https://poloniex.com/public?command=returnTicker");
		Iterator<String> marketNameIterator = json.keys();
		List<MarketPosition> marketPositions = new ArrayList<>();

		while (marketNameIterator.hasNext()) {
			String poloniexMarketName = marketNameIterator.next();
			String[] splitSybol = poloniexMarketName.split("_");
			UniSymbol uniSymbol = symbolService.getOrCreateNewSymbol(splitSybol[0],splitSybol[1]);
			JSONObject jsonObject = json.getJSONObject(poloniexMarketName);

			marketPositions.add(new MarketPosition(ExchangeMeta.POLONIEX, uniSymbol,
					jsonObject.getBigDecimal("last"), jsonObject.getBigDecimal("lowestAsk"), jsonObject.getBigDecimal("highestBid"),
					isSymbolPairActive(uniSymbol)));
		}
		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositions));
		marketPositionFastRepository.flush();
		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}

	private boolean isSymbolPairActive(UniSymbol uniSymbol) {
		PoloniexCurrencyInfo poloniexBaseSymbol = poloniexCurrencyMap.get(uniSymbol.getBaseName());
		PoloniexCurrencyInfo poloniexQuoteSymbol = poloniexCurrencyMap.get(uniSymbol.getQuoteName());

		if(poloniexBaseSymbol.isDisabled() || poloniexBaseSymbol.isDelisted() || poloniexBaseSymbol.isFrozen() ||
				poloniexQuoteSymbol.isDisabled() || poloniexQuoteSymbol.isDelisted() || poloniexQuoteSymbol.isFrozen()){
			return false;
		}else{
			return true;
		}
	}


}
