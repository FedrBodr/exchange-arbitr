package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPosition;

public interface MarketPositionRepository extends JpaRepository<MarketPosition, Long>, CrudRepository<MarketPosition, Long> {

}
