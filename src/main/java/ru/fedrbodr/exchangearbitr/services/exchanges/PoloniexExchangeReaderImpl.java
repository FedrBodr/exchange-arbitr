package ru.fedrbodr.exchangearbitr.services.exchanges;

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
import ru.fedrbodr.exchangearbitr.utils.MarketNamesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;

@Service
public class PoloniexExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketSummaryService marketSummaryService;

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		JSONObject json = getNewJsonObject("https://poloniex.com/public?command=returnTicker");
		Iterator<String> marketNameIterator = json.keys();
		List<MarketPosition> marketPositions = new ArrayList<>();

		while (marketNameIterator.hasNext()) {
			String poloniexMarketName = marketNameIterator.next();
			Symbol symbol = marketSummaryService.getOrCreateNewMarketSummary(MarketNamesUtils.convertPoloniexToUniversalMarketName(poloniexMarketName));
			JSONObject jsonObject = json.getJSONObject(poloniexMarketName);
			MarketPosition marketPosition = new MarketPosition(Exchange.POLONIEX, symbol, jsonObject.getDouble("last"));
			marketPositions.add(marketPosition);
		}

		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}


}
