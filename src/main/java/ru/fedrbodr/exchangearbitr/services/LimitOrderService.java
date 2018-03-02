package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;

public interface LimitOrderService {

	void readConvertCalcAndSaveUniOrders(Symbol symbol, ExchangeMeta exchangeMeta) throws InterruptedException;

}
