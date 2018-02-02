package ru.fedrbodr.exchangearbitr.services;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

/*
* Some logic with specific exchane site like read and save to db last prices
* */
public interface ExchangeReader {
	void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException, ParseException;
}
