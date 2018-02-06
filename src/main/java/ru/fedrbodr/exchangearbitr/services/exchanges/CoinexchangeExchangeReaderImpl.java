package ru.fedrbodr.exchangearbitr.services.exchanges;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.MarketSummaryService;
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
	private MarketSummaryService marketSummaryService;
	private Map<Integer, UniSymbol> coinexchangeIdToMarketSummaryMap;

	@PostConstruct
	private void init(){
		/*TODO refactor this with aop for all init methods*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		coinexchangeIdToMarketSummaryMap = new ConcurrentHashMap<>();
		try {
			JSONArray markets = getNewJsonObject(" https://www.coinexchange.io/api/v1/getmarkets").getJSONArray("result");
			markets.forEach(item -> {
				JSONObject obj = (JSONObject) item;
				UniSymbol uniSymbol = marketSummaryService.getOrCreateNewSymbol(
						obj.getString("BaseCurrencyCode")+ "-" + obj.getString("MarketAssetCode"),
						obj.getString("BaseCurrencyCode"),
						obj.getString("MarketAssetCode"));
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
			MarketPosition marketPosition = new MarketPosition(
					ExchangeMeta.COINEXCHANGE,
					getUnifiedMarketSummary(jsonObject.getInt("MarketID")),
					jsonObject.getBigDecimal("LastPrice"));

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
