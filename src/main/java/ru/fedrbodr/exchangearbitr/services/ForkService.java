package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;

import java.util.Date;
import java.util.List;

public interface ForkService {
	List<ForkInfo> getCurrentForks();

	void determineAndPersistForks(long lastOrdersLoadingTime);

	List<Fork> calcForks(long lastOrdersLoadingTime, List<DepoFork> depoForks, Date currentForkDetectedTime);
}
