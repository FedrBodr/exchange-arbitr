package ru.fedrbodr.exchangearbitr.service;

import org.json.JSONException;

import java.io.IOException;

/*
* Some logic with specific exchane site like read and save to db last prices
* */
public interface ExchangeReader {
	void readAndSaveMarketPositions() throws IOException, JSONException;
}
