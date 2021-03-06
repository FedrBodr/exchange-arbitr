package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;

public interface ExchangeMetaLongRepository extends JpaRepository<ExchangeMetaLong, Long>, CrudRepository<ExchangeMetaLong, Long> {

	@Cacheable("ExchangeMetaLongCache")
	ExchangeMetaLong findById(int id);

}
