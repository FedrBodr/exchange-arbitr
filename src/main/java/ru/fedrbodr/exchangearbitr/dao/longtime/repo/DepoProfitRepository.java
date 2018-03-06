package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.DepoProfit;

import java.util.List;

public interface DepoProfitRepository extends JpaRepository<DepoProfit, Long>{
	List<DepoProfit> findAllByForkId(Long forkId);
}
