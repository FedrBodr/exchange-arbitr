package ru.fedrbodr.exchangearbitr.services.exchanges;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.Exchange;
import ru.fedrbodr.exchangearbitr.model.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.MarketSummary;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.MarketSummaryService;
import ru.fedrbodr.exchangearbitr.utils.MarketNamesUtils;

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
	private MarketSummaryService marketSummaryService;
	private Map<Integer, MarketSummary> coinexchangeIdToMarketSummaryMap;

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
				MarketSummary marketSummary = marketSummaryService.getOrCreateNewMarketSummary(
						MarketNamesUtils.convertCoinexchangeToUniversalMarketName(obj.getString("BaseCurrencyCode"), obj.getString("MarketAssetCode")));
				int coinexchangeMarketID = obj.getInt("MarketID");
				coinexchangeIdToMarketSummaryMap.put(coinexchangeMarketID, marketSummary);
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
					Exchange.COINEXCHANGE,
					getUnifiedMarketSummary(jsonObject.getInt("MarketID")),
					jsonObject.getDouble("LastPrice"));

			marketPositions.add(marketPosition);
		});

		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}

	private MarketSummary getUnifiedMarketSummary(int coinexchangeMarketID) {
		MarketSummary marketSummary = coinexchangeIdToMarketSummaryMap.get(coinexchangeMarketID);
		if(marketSummary==null){
			log.info("Found Market Summary at coinexchange for non existen marketSummary");
			/* init all coinexchange markets summary again*/
			this.init();
		}
		return marketSummary;
	}


}
