package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork;

import java.util.List;

public interface ForkRepository extends JpaRepository<Fork, Long>, CrudRepository<Fork, Long> {
	Fork findFirstByBuyExchangeMetaIdAndSellExchangeMetaIdAndSymbolIdOrderByIdDesc(Integer buyExchangeMetaId, Integer sellExchangeMetaId, Long symbolId);
	@Query("select min(f.timestamp), min(f.forkWindowId) from Fork f where f.forkWindowId = :forkWindowId and f.buyExchangeMeta.id = :buyExchangeMetaId " +
			"and f.sellExchangeMeta.id=:sellExchangeMetaId and f.symbol.id=:symbolId")
	List<Object> selectTimeStampGroupLimit1(@Param("forkWindowId") Long forkWindowId,
											@Param("sellExchangeMetaId") Integer sellExchangeMetaId,
											@Param("buyExchangeMetaId") Integer buyExchangeMetaId,
											@Param("symbolId") Long symbolId);
}
