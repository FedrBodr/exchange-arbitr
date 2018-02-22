package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;

public interface MarketPositionFastRepository extends JpaRepository<MarketPositionFast, Long>, CrudRepository<MarketPositionFast, Long> {
}
