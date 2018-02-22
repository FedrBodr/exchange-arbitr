package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork;

public interface ForkRepository extends JpaRepository<Fork, Long>, CrudRepository<Fork, Long> {

}
