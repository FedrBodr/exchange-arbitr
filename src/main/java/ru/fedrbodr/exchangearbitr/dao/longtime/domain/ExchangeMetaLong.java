package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;

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
	/** be careful with https://www.cryptopia.co.nz/Exchange?market= as params need symbol in this format LINDA_BTC */
	public static ExchangeMetaLong CRYPTOPIA = new ExchangeMetaLong(7, "CRYPTOPIA", "https://www.cryptopia.co.nz", "https://www.cryptopia.co.nz/Exchange?market=", "&referrer=FedrBodr");
	public static ExchangeMetaLong KUNA = new ExchangeMetaLong(8, "KUNA", "https://kuna.io/", "https://kuna.io/markets/", "");
	public static ExchangeMetaLong LIVECOIN = new ExchangeMetaLong(9, "LIVECOIN", "https://www.livecoin.net", "https://www.livecoin.net/en/trade/index?currencyPair=", "&from=Livecoin-Vv9RaReE");


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

	public ExchangeMetaLong(ExchangeMeta exchangeMeta) {
		this.id = exchangeMeta.getId();
		this.exchangeName = exchangeMeta.getExchangeName();
		this.exchangeUrl = exchangeMeta.getExchangeUrl();
		this.symbolMarketUrl = exchangeMeta.getSymbolMarketUrl();
		this.refParam = exchangeMeta.getRefParam();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getExchangeUrl() {
		return exchangeUrl;
	}

	public void setExchangeUrl(String exchangeUrl) {
		this.exchangeUrl = exchangeUrl;
	}

	public String getSymbolMarketUrl() {
		return symbolMarketUrl;
	}

	public void setSymbolMarketUrl(String symbolMarketUrl) {
		this.symbolMarketUrl = symbolMarketUrl;
	}

	public String getRefParam() {
		return refParam;
	}

	public void setRefParam(String refParam) {
		this.refParam = refParam;
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

	@Override
	public String toString() {
		return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
	}
}
