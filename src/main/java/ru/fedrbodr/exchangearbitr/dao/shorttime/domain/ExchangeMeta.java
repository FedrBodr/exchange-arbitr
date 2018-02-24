package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="exchange_meta")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ToString
public class ExchangeMeta implements Serializable {
	public static ExchangeMeta BITTREX = new ExchangeMeta(1,"BITTREX", "https://bittrex.com", "https://bittrex.com/Market/Index?MarketName=", "");
	public static ExchangeMeta POLONIEX = new ExchangeMeta(2, "POLONIEX", "https://poloniex.com/", "https://poloniex.com/exchange/#", "");
	/** be careful with https://www.coinexchange.io/market/ as params need syboli in this format LTC/BTC*/
	public static ExchangeMeta COINEXCHANGE = new ExchangeMeta(3, "COINEXCHANGE", "https://www.coinexchange.io", "https://www.coinexchange.io/market/", "?r=96594b96");
	public static ExchangeMeta BINANCE = new ExchangeMeta(4, "BINANCE", "https://www.binance.com/", "https://www.binance.com/trade.html?symbol=", "");
	public static ExchangeMeta HITBTC = new ExchangeMeta(5, "HIT-BTC", "https://hitbtc.com", "https://hitbtc.com/exchange/", "?ref_id=5a87e613af10d");
	public static ExchangeMeta KUCOIN = new ExchangeMeta(6, "KUCOIN", "www.kucoin.com", "https://www.kucoin.com/#/trade.pro/", "");
	public static ExchangeMeta btctrade = new ExchangeMeta(7, "KUCOIN", "www.kucoin.com", "https://www.kucoin.com/#/trade.pro/", "");


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

	public String getExchangeUrl() {
		return exchangeUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExchangeMeta that = (ExchangeMeta) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}
}
