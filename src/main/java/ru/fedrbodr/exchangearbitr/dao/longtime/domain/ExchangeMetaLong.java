package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ToString
public class ExchangeMetaLong implements Serializable {
	public static ExchangeMetaLong BITTREX = new ExchangeMetaLong(1,"BITTREX", "https://bittrex.com", "https://bittrex.com/Market/Index?MarketName=", "");
	public static ExchangeMetaLong POLONIEX = new ExchangeMetaLong(2, "POLONIEX", "https://poloniex.com/", "https://poloniex.com/exchange/#", "");
	/** be careful with https://www.coinexchange.io/market/ as params need syboli in this format LTC/BTC*/
	public static ExchangeMetaLong COINEXCHANGE = new ExchangeMetaLong(3, "COINEXCHANGE", "https://www.coinexchange.io", "https://www.coinexchange.io/market/", "?r=96594b96");
	public static ExchangeMetaLong BINANCE = new ExchangeMetaLong(4, "BINANCE", "https://www.binance.com/", "https://www.binance.com/trade.html?symbol=", "");
	public static ExchangeMetaLong HITBTC = new ExchangeMetaLong(5, "HIT-BTC", "https://hitbtc.com", "https://hitbtc.com/exchange/", "?ref_id=5a87e613af10d");
	public static ExchangeMetaLong KUCOIN = new ExchangeMetaLong(6, "KUCOIN", "www.kucoin.com", "https://www.kucoin.com/#/trade.pro/", "");
	public static ExchangeMetaLong btctrade = new ExchangeMetaLong(7, "KUCOIN", "www.kucoin.com", "https://www.kucoin.com/#/trade.pro/", "");


	@Id
	private int id;
	@Column(name = "exchange_name")
	private String exchangeName;
	@Column(name = "exchange_url")
	private String exchangeUrl;
	@Column(name = "symbol_market_url")
	private String symbolMarketUrl;
	@Column(name = "ref_param")
	private String refParam;

	public int getId() {
		return id;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public String getSymbolMarketUrl() {
		return symbolMarketUrl;
	}

	public String getRefParam() {
		return refParam;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExchangeMetaLong that = (ExchangeMetaLong) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}
}
