package ru.fedrbodr.exchangearbitr.services;

import org.json.JSONException;

import java.io.IOException;

/*
* Some logic with specific exchane site like read and save to db last prices
* */
public interface ExchangeReader {
	void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException;
}
