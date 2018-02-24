package ru.fedrbodr.exchangearbitr.services;

public interface ForkService {
	void determineAndPersistForks(long lastOrdersLoadingTime);

}
