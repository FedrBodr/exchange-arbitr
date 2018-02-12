package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexCurrency;
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPosition;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;
import static ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils.bittrexToUniCurrencyName;

/**
 * Bittrex ExchangeMeta markect names  format now is main inner format ETH-BTC
 * */
@Service
@Slf4j
public class BittrexExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;
	private BittrexMarketDataServiceRaw bittrexMarketDataServiceRaw = (BittrexMarketDataServiceRaw)
			(ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName())).getMarketDataService();
	private Map<String, BittrexCurrency> currencyMap;

	private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>(){
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

		}
	};

	public Date convert(String source) throws ParseException{
		return dateFormat.get().parse(source);
	}

	@PostConstruct
	private void init() throws IOException {
		/*TODO refactor this with aop for all init methods*/
		log.info(BittrexExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		currencyMap = new HashMap<>();
		BittrexCurrency[] bittrexCurrencies = bittrexMarketDataServiceRaw.getBittrexCurrencies();
		for (BittrexCurrency bittrexCurrency : bittrexCurrencies) {
			currencyMap.put(bittrexCurrency.getCurrency(), bittrexCurrency);
		}

		JSONObject json = getNewJsonObject("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries");
		JSONArray result = json.getJSONArray("result");
		for(int i = result.length()-1; i>0; i--) {
			JSONObject market = result.getJSONObject(i).getJSONObject("Market");
			symbolService.getOrCreateNewSymbol(
					bittrexToUniCurrencyName(market.getString("BaseCurrency")),
					bittrexToUniCurrencyName(market.getString("MarketCurrency")));
		}
		/*TODO refactor this with aop*/
		log.info(BittrexExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException, ParseException {
		JSONObject json = getNewJsonObject("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries");
		JSONArray result = json.getJSONArray("result");
		List<MarketPosition> marketPositionList = new ArrayList<>();

		for(int i = result.length()-1; i>0; i--) {
			JSONObject marketPositionJsonObject = result.getJSONObject(i);
			JSONObject market = marketPositionJsonObject.getJSONObject("Market");
			JSONObject summary = marketPositionJsonObject.getJSONObject("Summary");

			SymbolPair symbolPair = symbolService.getOrCreateNewSymbol(
					bittrexToUniCurrencyName(market.getString("BaseCurrency")),
					bittrexToUniCurrencyName(market.getString("MarketCurrency")));
			MarketPosition marketPosition = new MarketPosition(
					ExchangeMeta.BITTREX,
					symbolPair,
					summary.getBigDecimal("Last"),
					summary.getBigDecimal("Bid"),
					summary.getBigDecimal("Ask"),
					isSymbolPairActive(market.getString("BaseCurrency"),market.getString("MarketCurrency")));
			marketPosition.setExchangeTimeStamp(convert(summary.getString("TimeStamp")));

			marketPositionList.add(marketPosition);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
		marketPositionRepository.save(marketPositionList);
		marketPositionRepository.flush();
	}

	/*Instead currencyMap can used market.getBoolean("IsActive") but now this as is maybe refactor to universal solution*/
	private boolean isSymbolPairActive(String baseName, String quoteName) {
		BittrexCurrency baseSymbol = currencyMap.get(baseName);
		BittrexCurrency quoteSymbol = currencyMap.get(quoteName);

		if(baseSymbol.isActive() && quoteSymbol.isActive()){
			return true;
		}else{
			return false;
		}
	}
}