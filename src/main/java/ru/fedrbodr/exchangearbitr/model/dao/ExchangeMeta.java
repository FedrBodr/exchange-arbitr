package ru.fedrbodr.exchangearbitr.model.dao;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Slf4j
@ToString
public class ExchangeMeta implements Serializable {
	public static ExchangeMeta BITTREX = new ExchangeMeta("BITTREX", "https://bittrex.com", 1);
	public static ExchangeMeta POLONIEX = new ExchangeMeta("POLONIEX", "https://poloniex.com/", 2);
	public static ExchangeMeta COINEXCHANGE = new ExchangeMeta("COINEXCHANGE", "https://www.coinexchange.io", 3);
	public static ExchangeMeta BINANCE = new ExchangeMeta("BINANCE", "https://www.binance.com/", 4);

	@Id
	private int id;
	private String exchangeName;
	private String exchangeUrl;

	public ExchangeMeta(String exchangeName, String exchangeUrl, int id) {
		this.exchangeName = exchangeName;
		this.exchangeUrl = exchangeUrl;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getExchangeName() {
		return exchangeName;
	}
}
