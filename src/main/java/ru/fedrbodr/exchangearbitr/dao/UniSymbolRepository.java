package ru.fedrbodr.exchangearbitr.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;

public interface UniSymbolRepository extends JpaRepository<UniSymbol, Long>, CrudRepository<UniSymbol, Long> {
	@Cacheable("symbolByName")
	UniSymbol findByName(String name);
}
