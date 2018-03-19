package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.TgBulletConfig;

import java.util.List;
import java.util.UUID;

@Repository
public interface TgBulletConfigRepository extends JpaRepository<TgBulletConfig, Long>, CrudRepository<TgBulletConfig, Long> {

	TgBulletConfig findOneByUuid(UUID uuid);

	List<TgBulletConfig> findByTgChatIdNotNull();
}
