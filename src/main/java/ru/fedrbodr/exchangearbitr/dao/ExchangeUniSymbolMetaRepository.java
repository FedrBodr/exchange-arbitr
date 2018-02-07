package ru.fedrbodr.exchangearbitr.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeUniSymbolMeta;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeUniSymbolPK;

public interface ExchangeUniSymbolMetaRepository extends JpaRepository<ExchangeUniSymbolMeta, ExchangeUniSymbolPK>,
		CrudRepository<ExchangeUniSymbolMeta, ExchangeUniSymbolPK> {

	public ExchangeUniSymbolMeta findByExchangeUniSymbolPK(ExchangeUniSymbolPK pk);
}
