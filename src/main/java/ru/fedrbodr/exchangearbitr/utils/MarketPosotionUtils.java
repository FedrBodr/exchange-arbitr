package ru.fedrbodr.exchangearbitr.utils;

import ru.fedrbodr.exchangearbitr.dao.model.MarketPosition;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPositionFast;

import java.util.ArrayList;
import java.util.List;

public class MarketPosotionUtils {
	public static List<MarketPositionFast> convertMarketPosotionListToFast(List<MarketPosition> positionList) {
		List<MarketPositionFast> marketPositionFastList = new ArrayList<>();
		positionList.forEach(item -> {
			marketPositionFastList.add(new MarketPositionFast(item));
		});
		return marketPositionFastList;
	}
}
