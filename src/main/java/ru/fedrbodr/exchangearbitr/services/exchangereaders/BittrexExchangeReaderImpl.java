package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexCurrency;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexMarketSummary;
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPosition;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;
import ru.fedrbodr.exchangearbitr.xchange.custom.ExchangeProxy;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils.bittrexToUniCurrencyName;

/**
 * Bittrex ExchangeMeta1 markect names  format now is main inner format ETH-BTC
 * */
@Service
@Slf4j
public class BittrexExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;
	@Autowired
	private Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeProxyMap;
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
		/* TODO refactor this with aop for all init methods */
		Date starDate = new Date();
		log.info(BittrexExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		currencyMap = new HashMap<>();
		try {
			BittrexCurrency[] bittrexCurrencies = getMarketDataService().getBittrexCurrencies();
			for (BittrexCurrency bittrexCurrency : bittrexCurrencies) {
				currencyMap.put(bittrexCurrency.getCurrency(), bittrexCurrency);
			}
		}catch (Exception e){
			log.error("ExchangeArbitr " + e.getMessage(), e);
		}

		/*JSONObject json = getNewJsonObject("https://bittrex.com/api/v2.0/pub/Markets/GetMarketSummaries");
		JSONArray result = json.getJSONArray("result");
		for(int i = result.length()-1; i>=0; i--) {
			JSONObject market = result.getJSONObject(i).getJSONObject("Market");
			symbolService.getOrCreateNewSymbol(
					bittrexToUniCurrencyName(market.getString("BaseCurrency")),
					bittrexToUniCurrencyName(market.getString("MarketCurrency")));
		}*/
		/* TODO refactor this with aop */
		log.info(BittrexExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	private BittrexMarketDataServiceRaw getMarketDataService() {
		return (BittrexMarketDataServiceRaw) (exchangeMetaToExchangeProxyMap.get(ExchangeMeta.BITTREX).getNextExchange().getMarketDataService());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException, ParseException {
		ArrayList<BittrexMarketSummary> bittrexMarketSummaries = getMarketDataService().getBittrexMarketSummaries();
		List<MarketPosition> marketPositionList = new ArrayList<>();

		for (BittrexMarketSummary bittrexMarketSummary : bittrexMarketSummaries) {
			String[] split = bittrexMarketSummary.getMarketName().split("-");
			Symbol symbol = symbolService.getOrCreateNewSymbol(
					bittrexToUniCurrencyName(split[0]), // "BaseCurrency"
					bittrexToUniCurrencyName(split[1]));//"MarketCurrency"

			MarketPosition marketPosition = new MarketPosition(
					ExchangeMeta.BITTREX,
					symbol,
					bittrexMarketSummary.getLast(),
					bittrexMarketSummary.getBid(),
					bittrexMarketSummary.getAsk(),
					isSymbolActive(split[0],split[1]));
			marketPosition.setExchangeTimeStamp(convert(bittrexMarketSummary.getTimeStamp()));

			marketPositionList.add(marketPosition);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
	}

	/*Instead currencyMap can used market.getBoolean("IsActive") but now this as is maybe refactor to universal solution*/
	private boolean isSymbolActive(String baseName, String quoteName) {
		BittrexCurrency baseSymbol = currencyMap.get(baseName);
		BittrexCurrency quoteSymbol = currencyMap.get(quoteName);

		if(baseSymbol.isActive() && quoteSymbol.isActive()){
			return true;
		}else{
			return false;
		}
	}
}