package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;

public interface SymbolLongRepository extends JpaRepository<SymbolLong, Long>, CrudRepository<SymbolLong, Long> {
	@Cacheable("symbolLongByName")
	SymbolLong findByName(String name);

	@Cacheable("symbolLongById")
	SymbolLong findById(Long id);
}
