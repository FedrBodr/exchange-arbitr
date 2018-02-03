package ru.fedrbodr.exchangearbitr.services.exchanges;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.dao.Exchange;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.dao.Symbol;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.MarketSummaryService;
import ru.fedrbodr.exchangearbitr.utils.MarketNamesUtils;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

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
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private MarketSummaryService marketSummaryService;

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		JSONObject json = getNewJsonObject("https://poloniex.com/public?command=returnTicker");
		Iterator<String> marketNameIterator = json.keys();
		List<MarketPosition> marketPositions = new ArrayList<>();

		while (marketNameIterator.hasNext()) {
			String poloniexMarketName = marketNameIterator.next();
			Symbol symbol = marketSummaryService.getOrCreateNewSymbol(MarketNamesUtils.convertPoloniexToUniversalMarketName(poloniexMarketName));
			JSONObject jsonObject = json.getJSONObject(poloniexMarketName);
			MarketPosition marketPosition = new MarketPosition(Exchange.POLONIEX, symbol, jsonObject.getBigDecimal("last"));
			marketPositions.add(marketPosition);
		}
		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositions));
		marketPositionFastRepository.flush();
		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}


}
