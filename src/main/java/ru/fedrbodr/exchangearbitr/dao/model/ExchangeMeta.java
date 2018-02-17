package ru.fedrbodr.exchangearbitr.dao.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ToString
public class ExchangeMeta implements Serializable {
	public static ExchangeMeta BITTREX = new ExchangeMeta(1,"BITTREX", "https://bittrex.com", "https://bittrex.com/Market/Index?MarketName=", "");
	public static ExchangeMeta POLONIEX = new ExchangeMeta(2, "POLONIEX", "https://poloniex.com/", "https://poloniex.com/exchange/#", "");
	/** be careful with https://www.coinexchange.io/market/ as params need syboli in this format LTC/BTC*/
	public static ExchangeMeta COINEXCHANGE = new ExchangeMeta(3, "COINEXCHANGE", "https://www.coinexchange.io", "https://www.coinexchange.io/market/", "?r=96594b96");
	public static ExchangeMeta BINANCE = new ExchangeMeta(4, "BINANCE", "https://www.binance.com/", "https://www.binance.com/trade.html?symbolPair=", "&ref=21325688");
	public static ExchangeMeta HITBTC = new ExchangeMeta(5, "HIT-BTC", "https://hitbtc.com", "https://hitbtc.com/exchange/", "?ref_id=5a87e613af10d");

	@Id
	private int id;
	private String exchangeName;
	private String exchangeUrl;
	private String symbolMarketUrl;
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
		ExchangeMeta that = (ExchangeMeta) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}
}
