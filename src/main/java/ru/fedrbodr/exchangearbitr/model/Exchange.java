package ru.fedrbodr.exchangearbitr.model;

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
public class Exchange implements Serializable {
	public static Exchange BITTREX = new Exchange("BITTREX", "https://bittrex.com", 1);
	public static Exchange POLONIEX = new Exchange("POLONIEX", "https://poloniex.com/", 2);
	public static Exchange COINEXCHANGE = new Exchange("COINEXCHANGE", "https://www.coinexchange.io", 3);

	@Id
	private int id;
	private String exchangeName;
	private String exchangeUrl;

	public Exchange(String exchangeName, String exchangeUrl, int id) {
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
