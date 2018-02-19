package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;

public interface LimitOrderService {

	void readConvertCalcAndSaveUniOrders(SymbolPair symbolPair, ExchangeMeta exchangeMeta, String host, Integer  port);

}
