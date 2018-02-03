package ru.fedrbodr.exchangearbitr.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.model.dao.Exchange;

public interface ExchangeRepository extends JpaRepository<Exchange, Long>, CrudRepository<Exchange, Long> {

	Exchange findById(int id);

}
