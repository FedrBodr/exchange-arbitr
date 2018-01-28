package ru.fedrbodr.exchangearbitr.model;

public enum Exchange {
	BITTREX("https://bittrex.com", 1),POLONIEX("https://bittrex.com", 2);

	private final String exchangeName;
	private final long id;

	Exchange(String exchangeName, long id) {
		this.exchangeName = exchangeName;
		this.id = id;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public long getId() {
		return id;
	}
}
