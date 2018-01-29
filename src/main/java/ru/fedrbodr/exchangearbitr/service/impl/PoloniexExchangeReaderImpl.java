package ru.fedrbodr.exchangearbitr.service.impl;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.Exchange;
import ru.fedrbodr.exchangearbitr.model.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.MarketSummary;
import ru.fedrbodr.exchangearbitr.service.ExchangeReader;
import ru.fedrbodr.exchangearbitr.service.MarketSummaryService;
import ru.fedrbodr.exchangearbitr.utils.MarketUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class PoloniexExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketSummaryService marketSummaryService;

	public void readAndSaveMarketPositions() throws IOException, JSONException {
		JSONObject json = new JSONObject(IOUtils.toString(new URL(" https://poloniex.com/public?command=returnTicker"), Charset.forName("UTF-8")));
		Iterator<String> marketNameIterator = json.keys();
		List<MarketPosition> marketPositions = new ArrayList<>();

		while (marketNameIterator.hasNext()) {
			String poloniexMarketName = marketNameIterator.next();
			MarketSummary marketSummary = marketSummaryService.getOrCreateNewMarketSummary(MarketUtils.convertPoloniexToUniversalMarketName(poloniexMarketName));

			MarketPosition marketPosition = new MarketPosition();
			marketPosition.setMarketSummary(marketSummary);

			JSONObject jsonObject = json.getJSONObject(poloniexMarketName);

			marketPosition.setPrice(jsonObject.getDouble("last"));
			marketPosition.setDbSaveTime(LocalDateTime.now());
			marketPosition.setExchangeId(Exchange.POLONIEX.getId());
			marketPositions.add(marketPosition);
		}

		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}


}
