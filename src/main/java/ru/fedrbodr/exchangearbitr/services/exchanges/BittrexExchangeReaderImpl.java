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
import ru.fedrbodr.exchangearbitr.model.Symbol;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.MarketSummaryService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;

/**
 * Bittrex Exchange markect names  format now is main inner format ETH-BTC
 * */
@Service
@Slf4j
public class BittrexExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketSummaryService marketSummaryService;

	@PostConstruct
	private void init() throws IOException {
		/*TODO refactor this with aop for all init methods*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		JSONObject json = getNewJsonObject("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries");
		JSONArray result = json.getJSONArray("result");
		List<MarketPosition> marketPositions = new ArrayList<>();
		for(int i = result.length()-1; i>0; i--) {
			JSONObject marketPositionJsonObject = result.getJSONObject(i);
			JSONObject market = marketPositionJsonObject.getJSONObject("Market");
			marketSummaryService.getOrCreateNewSymbol(market.getString("MarketName"));
		}
		/*TODO refactor this with aop*/
		log.info(CoinexchangeExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		JSONObject json = getNewJsonObject("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries");
		JSONArray result = json.getJSONArray("result");
		List<MarketPosition> marketPositions = new ArrayList<>();
		for(int i = result.length()-1; i>0; i--) {
			JSONObject marketPositionJsonObject = result.getJSONObject(i);
			JSONObject market = marketPositionJsonObject.getJSONObject("Market");
			JSONObject summary = marketPositionJsonObject.getJSONObject("Summary");

			Symbol symbol = marketSummaryService.getOrCreateNewSymbol(market.getString("MarketName"));
			MarketPosition marketPosition = new MarketPosition(Exchange.BITTREX, symbol, summary.getDouble("Last"));
			marketPosition.setTimeStamp(LocalDateTime.parse(summary.getString("TimeStamp")));

			marketPositions.add(marketPosition);
		}

		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}
}
