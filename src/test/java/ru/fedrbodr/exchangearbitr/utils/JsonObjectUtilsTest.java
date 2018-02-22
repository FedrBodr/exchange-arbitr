package ru.fedrbodr.exchangearbitr.utils;

import com.jsoniter.JsonIterator;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.services.exchangereaders.BittrexExchangeReaderImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils.bittrexToUniCurrencyName;

@Slf4j
public class JsonObjectUtilsTest {

	@Test
	public void getNewJsonObject() throws IOException {
		int n = 2;
		/* try json JSONObject */
		log.info("Start {} calls by JSONObject", n);
		Date starDate = new Date();
		for (int j = 0; j < n; j++) {
			JSONObject json = JsonObjectUtils.getNewJsonObject("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries");
			JSONArray result = json.getJSONArray("result");
			for(int i = result.length()-1; i>=0; i--) {
				JSONObject market = result.getJSONObject(i).getJSONObject("Market");
				getOrCreateNewSymbol(bittrexToUniCurrencyName(market.getString("BaseCurrency")),
						bittrexToUniCurrencyName(market.getString("MarketCurrency")));
			}
		}
		log.info("Calls by JSONObject end, execution time: {}", new Date().getTime() - starDate.getTime());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/* try json iter */
		log.info("Start {} calls by JsonIter", n);
		starDate = new Date();
		for (int j = 0; j < n; j++) {

			InputStream inputStream = new URL("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries").openConnection().getInputStream();
			JsonIterator parse = JsonIterator.parse(inputStream, 8192);
			HashMap<String, Object> read = (HashMap<String, Object>) parse.read();
			ArrayList<HashMap> result = (ArrayList<HashMap>) read.get("result");
			for (HashMap hashMap : result) {
				HashMap market = (HashMap) hashMap.get("Market");
				String baseCurrency = (String) market.get("BaseCurrency");
				String marketCurrency = (String) market.get("MarketCurrency");
				getOrCreateNewSymbol(baseCurrency, marketCurrency);
			}
		}
		log.info("Calls by JsonIter: {}", new Date().getTime() - starDate.getTime());
	}

	@Test
	public void getNewJsonObject1() throws IOException {
		int n = 10;
		Date starDate = new Date();
		/* try json iter */
		log.info("Start {} calls by JsonIter", n);
		starDate = new Date();
		for (int j = 0; j < n; j++) {

			InputStream inputStream = new URL("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries").openConnection().getInputStream();
			JsonIterator parse = JsonIterator.parse(inputStream, 8192);
			HashMap<String, Object> read = (HashMap<String, Object>) parse.read();
			ArrayList<HashMap> result = (ArrayList<HashMap>) read.get("result");
			for (HashMap hashMap : result) {
				HashMap market = (HashMap) hashMap.get("Market");
				String baseCurrency = (String) market.get("BaseCurrency");
				String marketCurrency = (String) market.get("MarketCurrency");
				getOrCreateNewSymbol(baseCurrency, marketCurrency);
			}
		}
		log.info(BittrexExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());

	}

	private Symbol getOrCreateNewSymbol(String baseCurrency, String marketCurrency) {
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}