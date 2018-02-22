package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;

public interface SymbolRepository extends JpaRepository<Symbol, Long>, CrudRepository<Symbol, Long> {
	@Cacheable("symbolByName")
	Symbol findByName(String name);
}
