package ru.fedrbodr.exchangearbitr.service;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import ru.fedrbodr.exchangearbitr.model.MarketPosition;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;

public class ExchangeReaderTest {
	@Test
	public void readAllSoutOneBittrexMarketPosition() throws IOException, JSONException, ParseException {
		// SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

		JSONObject json = new JSONObject(IOUtils.toString(new URL("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries"), Charset.forName("UTF-8")));
		JSONArray result = json.getJSONArray("result");


		for(int i = result.length()-1; i>0; i--) {
			JSONObject marketPositionJsonObject = result.getJSONObject(i);
			JSONObject market = marketPositionJsonObject.getJSONObject("Market");

			MarketPosition marketPosition = new MarketPosition();
			/*marketPosition.setMarketName(market.getString("MarketName"));
			JSONObject summary = marketPositionJsonObject.getJSONObject("Summary");
			marketPosition.setPrice(summary.getDouble("Last"));
			marketPosition.setPrimaryCurrencyName(market.getString("BaseCurrency"));
			marketPosition.setSecondaryCurrencyName(market.getString("MarketCurrency"));*/
			/*marketPosition.setTimeStamp(LocalDateTime.parse(summary.getString("TimeStamp")));*/
			System.out.println(marketPosition);
		}
	}
}