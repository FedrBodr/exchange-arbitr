package ru.fedrbodr.exchangearbitr.services.exchanges;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.ExchangeMetaRepository;
import ru.fedrbodr.exchangearbitr.dao.ExchangeUniSymbolMetaRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.dao.*;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;

@Service
@Slf4j
public class CoinexchangeExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private ExchangeUniSymbolMetaRepository exchangeUniSymbolMetaRepository;
	@Autowired
	private SymbolService symbolService;
	private Map<Integer, UniSymbol> coinexchangeIdToMarketSummaryMap;
	@Autowired
	private ExchangeMetaRepository exchangeRepository;
	private boolean doGrabbing = false;
	Date startPreviousCall;
	Date startPreviousBitrixCall;

	@PostConstruct
	private void init(){
		/* TODO move preinit to more convenient place ? */
		exchangeRepository.save(ExchangeMeta.BITTREX);
		exchangeRepository.save(ExchangeMeta.COINEXCHANGE);
		exchangeRepository.save(ExchangeMeta.POLONIEX);
		exchangeRepository.save(ExchangeMeta.BINANCE);
		exchangeRepository.flush();
		/*TODO refactor this with aop for all init methods*/

		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		coinexchangeIdToMarketSummaryMap = new ConcurrentHashMap<>();
		try {
			JSONArray markets = getNewJsonObject(" https://www.coinexchange.io/api/v1/getmarkets").getJSONArray("result");
			markets.forEach(item -> {
				JSONObject obj = (JSONObject) item;
				/* IT IS CORECT NAMES FORMAT like in org.knowm.xchange.currency.Currency*/
				UniSymbol uniSymbol = symbolService.getOrCreateNewSymbol(
						obj.getString("BaseCurrencyCode"),
						obj.getString("MarketAssetCode"));

				exchangeUniSymbolMetaRepository.save(new ExchangeUniSymbolMeta(ExchangeMeta.COINEXCHANGE, uniSymbol, obj.getBoolean("Active")));

				int coinexchangeMarketID = obj.getInt("MarketID");
				coinexchangeIdToMarketSummaryMap.put(coinexchangeMarketID, uniSymbol);
			});
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		/*TODO refactor this with aop*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		JSONArray marketPositionsArray = getNewJsonObject(" https://www.coinexchange.io/api/v1/getmarketsummaries").getJSONArray("result");
		List<MarketPosition> marketPositions = new ArrayList<>();

		marketPositionsArray.forEach(item -> {
			JSONObject jsonObject = (JSONObject) item;
			UniSymbol symbol = getUnifiedMarketSummary(jsonObject.getInt("MarketID"));
			ExchangeUniSymbolPK exchangeUniSymbolPK = new ExchangeUniSymbolPK(ExchangeMeta.COINEXCHANGE, symbol);
			MarketPosition marketPosition = new MarketPosition(
					ExchangeMeta.COINEXCHANGE,
					symbol,
					jsonObject.getBigDecimal("LastPrice"),
					exchangeUniSymbolMetaRepository.findByExchangeUniSymbolPK(exchangeUniSymbolPK).isAcvtive());

			marketPositions.add(marketPosition);
		});

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositions));
		marketPositionFastRepository.flush();
		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}

	private UniSymbol getUnifiedMarketSummary(int coinexchangeMarketID) {
		UniSymbol uniSymbol = coinexchangeIdToMarketSummaryMap.get(coinexchangeMarketID);
		if(uniSymbol ==null){
			log.info("Found Market Summary at coinexchange for non existen uniSymbol");
			/* init all coinexchange markets summary again*/
			this.init();
		}
		return uniSymbol;
	}


}
