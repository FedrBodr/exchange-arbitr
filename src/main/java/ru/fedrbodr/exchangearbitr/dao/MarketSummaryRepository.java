package ru.fedrbodr.exchangearbitr.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.model.MarketSummary;

public interface MarketSummaryRepository extends JpaRepository<MarketSummary, Long>, CrudRepository<MarketSummary, Long> {
	@Cacheable("marketSummariesByName")
	MarketSummary findByName(String name);
}
