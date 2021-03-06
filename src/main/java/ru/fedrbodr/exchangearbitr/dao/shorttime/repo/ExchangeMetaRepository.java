package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;

public interface ExchangeMetaRepository extends JpaRepository<ExchangeMeta, Long>, CrudRepository<ExchangeMeta, Long> {

	ExchangeMeta findById(int id);

}
