package ru.fedrbodr.exchangearbitr.services.exchanges;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ru.fedrbodr.exchangearbitr.utils.JsonObjectUtils.getNewJsonObject;
/**
 * Universal symbol format is BTC-BCN Poloniex is BTC_BCN
 *
 * */
@Service
public class PoloniexExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		JSONObject json = getNewJsonObject("https://poloniex.com/public?command=returnTicker");
		Iterator<String> marketNameIterator = json.keys();
		List<MarketPosition> marketPositions = new ArrayList<>();

		while (marketNameIterator.hasNext()) {
			String poloniexMarketName = marketNameIterator.next();
			String[] splitSybol = poloniexMarketName.split("_");
			UniSymbol uniSymbol = symbolService.getOrCreateNewSymbol(splitSybol[0]+"-"+splitSybol[1],splitSybol[0],splitSybol[1]);
			JSONObject jsonObject = json.getJSONObject(poloniexMarketName);
			/* TODO maybe true maybe not exactly but when i try to found active status for symbol - i did not found anything */
			MarketPosition marketPosition = new MarketPosition(ExchangeMeta.POLONIEX, uniSymbol, jsonObject.getBigDecimal("last"), true);
			marketPositions.add(marketPosition);
		}
		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositions));
		marketPositionFastRepository.flush();
		marketPositionRepository.save(marketPositions);
		marketPositionRepository.flush();
	}


}
