package ru.fedrbodr.exchangearbitr.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFast;

public interface MarketPositionFastRepository extends JpaRepository<MarketPositionFast, Long>, CrudRepository<MarketPositionFast, Long> {

}
