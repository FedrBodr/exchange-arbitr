package ru.fedrbodr.exchangearbitr.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.model.Symbol;

public interface MarketSummaryRepository extends JpaRepository<Symbol, Long>, CrudRepository<Symbol, Long> {
	@Cacheable("marketSummariesByName")
	Symbol findByName(String name);
}
