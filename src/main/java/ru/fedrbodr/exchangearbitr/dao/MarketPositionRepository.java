package ru.fedrbodr.exchangearbitr.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.model.MarketPosition;

public interface MarketPositionRepository extends JpaRepository<MarketPosition, Long>, CrudRepository<MarketPosition, Long> {
	public MarketPosition findByMarketName(String name);
}
