package ru.fedrbodr.exchangearbitr.service.impl;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.MarketPosition;
import ru.fedrbodr.exchangearbitr.service.ExchangeWorker;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BittrexExchangeWorkerImpl implements ExchangeWorker {
	@Autowired
	private MarketPositionRepository marketPositionRepository;

	public void readAndSaveMarketPositions() throws IOException, JSONException {
		JSONObject json = new JSONObject(IOUtils.toString(new URL("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries"), Charset.forName("UTF-8")));
		JSONArray result = json.getJSONArray("result");
		List<MarketPosition> marketPositions = new ArrayList<>();
		for(int i = result.length()-1; i>0; i--) {
			JSONObject marketPositionJsonObject = result.getJSONObject(i);
			JSONObject market = marketPositionJsonObject.getJSONObject("Market");

			MarketPosition marketPosition = new MarketPosition();
			marketPosition.setMarketName(market.getString("MarketName"));
			JSONObject summary = marketPositionJsonObject.getJSONObject("Summary");
			marketPosition.setPrice(summary.getDouble("Last"));
			marketPosition.setPrimaryCurrencyName(market.getString("BaseCurrency"));
			marketPosition.setSecondaryCurrencyName(market.getString("MarketCurrency"));
			marketPosition.setTimeStamp(LocalDateTime.parse(summary.getString("TimeStamp")));
			marketPositions.add(marketPosition);
		}

		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}
}
