package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.BulletForkFilter;

public interface BulletForkFilterRepository extends JpaRepository<BulletForkFilter, Long>, CrudRepository<BulletForkFilter, Long> {
}
