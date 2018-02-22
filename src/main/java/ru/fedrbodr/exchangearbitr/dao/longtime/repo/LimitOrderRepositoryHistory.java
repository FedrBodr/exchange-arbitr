package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.UniLimitOrderHistory;

public interface LimitOrderRepositoryHistory extends JpaRepository<UniLimitOrderHistory, Long>, CrudRepository<UniLimitOrderHistory, Long> {

}
