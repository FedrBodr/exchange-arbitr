package ru.fedrbodr.exchangearbitr.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;

public interface SymbolPairRepository extends JpaRepository<SymbolPair, Long>, CrudRepository<SymbolPair, Long> {
	@Cacheable("symbolByName")
	SymbolPair findByName(String name);
}
