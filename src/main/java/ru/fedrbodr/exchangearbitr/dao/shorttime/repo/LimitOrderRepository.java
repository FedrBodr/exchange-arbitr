package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.knowm.xchange.dto.Order;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;

import java.util.List;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.ORDER_LIST_CACHE;

public interface LimitOrderRepository extends JpaRepository<UniLimitOrder, Long>, CrudRepository<UniLimitOrder, Long> {
	@Cacheable(ORDER_LIST_CACHE)
	@Transactional(readOnly = true)
	List<UniLimitOrder> findFirst60ByExchangeMetaAndSymbolAndType(
			ExchangeMeta exchangeMeta, Symbol symbol, Order.OrderType type);

	@Transactional
	void deleteByExchangeMetaAndSymbol(
			ExchangeMeta exchangeMeta, Symbol symbol);

	@Transactional(readOnly = true)
	UniLimitOrder findFirstByExchangeMetaAndSymbol(
			ExchangeMeta exchangeMeta, Symbol symbol);

}
