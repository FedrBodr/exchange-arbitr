package ru.fedrbodr.exchangearbitr.utils;

import ru.fedrbodr.exchangearbitr.model.dao.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;

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
