package ru.fedrbodr.exchangearbitr.services;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
			/*marketPosition.setName(market.getString("MarketName"));
			JSONObject summary = marketPositionJsonObject.getJSONObject("Summary");
			marketPosition.setPrice(summary.getDouble("Last"));
			marketPosition.setBaseName(market.getString("BaseCurrency"));
			marketPosition.setQuoteName(market.getString("MarketCurrency"));*/
			/*marketPosition.setTimeStamp(LocalDateTime.parse(summary.getString("TimeStamp")));*/
			log.info(marketPosition.toString());
		}
	}
}