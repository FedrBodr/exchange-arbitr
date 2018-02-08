package ru.fedrbodr.exchangearbitr.services.exchanges;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		JSONObject json = getNewJsonObject("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries");
		JSONArray result = json.getJSONArray("result");
		List<MarketPosition> marketPositions = new ArrayList<>();
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
		List<MarketPositionFast> marketPositionFastList = new ArrayList<>();
		for(int i = result.length()-1; i>0; i--) {
			JSONObject marketPositionJsonObject = result.getJSONObject(i);
			JSONObject market = marketPositionJsonObject.getJSONObject("Market");
			JSONObject summary = marketPositionJsonObject.getJSONObject("Summary");

			UniSymbol uniSymbol = symbolService.getOrCreateNewSymbol(
					bittrexToUniCurrencyName(market.getString("BaseCurrency")),
					bittrexToUniCurrencyName(market.getString("MarketCurrency")));

			MarketPosition marketPosition = new MarketPosition(ExchangeMeta.BITTREX, uniSymbol, summary.getBigDecimal("Last"), market.getBoolean("IsActive"));
			marketPosition.setExchangeTimeStamp(convert(summary.getString("TimeStamp")));

			marketPositionList.add(marketPosition);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
		marketPositionRepository.save(marketPositionList);
		marketPositionRepository.flush();
	}
}