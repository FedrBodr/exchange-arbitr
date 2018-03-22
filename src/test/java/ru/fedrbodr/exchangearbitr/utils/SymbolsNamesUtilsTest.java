package ru.fedrbodr.exchangearbitr.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFastPK;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SymbolsNamesUtilsTest {
	@Mock
	private Appender mockAppender;
	//Captor is genericised with ch.qos.logback.classic.spi.LoggingEvent
	@Captor
	private ArgumentCaptor<LoggingEvent> captorLoggingEvent;
	//I've cheated a little here and added the mockAppender to the root logger
	//It's not quite necessary but it also shows you how it can be done
	@Before
	public void setup() {
		final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.addAppender(mockAppender);
	}
	//Always have this teardown otherwise we can stuff up our expectations.
	@After
	public void teardown() {
		final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.detachAppender(mockAppender);
	}

	private final Symbol symbol = new Symbol("BTC-ZEC","BTC", "ZEC");

	@Test
	public void determineUrlToSymbolMarket() {
		MarketPositionFast binanceMarketPosition = getSampleMarketPositionFor(ExchangeMeta.BINANCE);
		MarketPositionFast bittrexMarketPosition = getSampleMarketPositionFor(ExchangeMeta.BITTREX);
		MarketPositionFast coinexchangeMarketPosition = getSampleMarketPositionFor(ExchangeMeta.COINEXCHANGE);
		MarketPositionFast poloniexMarketPosition = getSampleMarketPositionFor(ExchangeMeta.POLONIEX);

		assertEquals("https://www.binance.com/trade.html?symbol=ZEC_BTC", SymbolsNamesUtils.determineUrlToSymbolMarket(binanceMarketPosition));
		assertEquals("https://bittrex.com/Market/Index?MarketName=BTC-ZEC", SymbolsNamesUtils.determineUrlToSymbolMarket(bittrexMarketPosition));
		assertEquals("https://www.coinexchange.io/market/ZEC/BTC", SymbolsNamesUtils.determineUrlToSymbolMarket(coinexchangeMarketPosition));
		assertEquals("https://poloniex.com/exchange/#BTC_ZEC", SymbolsNamesUtils.determineUrlToSymbolMarket(poloniexMarketPosition));

		/* try on non existing exchange */
		poloniexMarketPosition.getMarketPositionFastPK().setExchangeMeta(new ExchangeMeta(559, "FED", "https://www.FED.com/", "https://www.FED.com/symbol=", "",true, true));
		assertEquals(null, SymbolsNamesUtils.determineUrlToSymbolMarket(poloniexMarketPosition));
		//Now verify our logging interactions
		verify(mockAppender).doAppend(captorLoggingEvent.capture());
		//Having a genricised captor means we don't need to cast
		final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
		//Check log level is correct
		assertThat(loggingEvent.getLevel(), is(Level.ERROR));
		//Check the message being logged is correct
		assertThat(loggingEvent.getFormattedMessage(), is("Can not determine url to symbol market for exchange FED"));
	}

	@Test
	public void binanceCurrencyConvertNameTest() {
		String knownBinanceBitcoinCachShortName = "BCC";
		String uniBitcoinCachShortName = "BCH";

		assertEquals("BCC", SymbolsNamesUtils.uniToBinanceCurrencyName(uniBitcoinCachShortName));
		assertEquals("BCH", SymbolsNamesUtils.binanceToUniCurrencyName(knownBinanceBitcoinCachShortName));
	}

	private MarketPositionFast getSampleMarketPositionFor(ExchangeMeta exchangeMeta) {
		MarketPositionFast marketPosition = new MarketPositionFast();
		MarketPositionFastPK marketPositionPK = new MarketPositionFastPK(exchangeMeta, symbol);
		marketPosition.setMarketPositionFastPK(marketPositionPK);
		return marketPosition;
	}
}