package ru.fedrbodr.exchangearbitr.view;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.*;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.BulletForkFilterRepository;
import ru.fedrbodr.exchangearbitr.services.TgBulletConfigService;
import ru.fedrbodr.exchangearbitr.services.UserService;
import ru.fedrbodr.exchangearbitr.services.impl.ExchangeMetaLongService;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
@Scope("view")
@Slf4j
public class NotificationController {
	@Autowired
	private UserService userService;
	@Autowired
	private BulletForkFilterRepository bulletForkFilterRepository;
	@Autowired
	private TgBulletConfigService tgBulletConfigService;
	@Autowired
	private ExchangeMetaLongService exchangeMetaLongService;
	private TgBulletConfig tgBulletConfig;
	/** We buy  */
	private ExchangeMetaLong buyExchange;
	/** We sell */
	private ExchangeMetaLong sellExchange;
	private SymbolLong symbol;
	private Double minDeposit = Double.valueOf(0);
	private Double minProfit = new Double(0.5);

	@PostConstruct
	public void init(){
		User currentUserOrNull = userService.getCurrentUserOrNull();
		if(currentUserOrNull != null){
			User currentUser = currentUserOrNull;
			tgBulletConfig = currentUserOrNull.getBulletConfig();
			if(tgBulletConfig == null){
				tgBulletConfig = new TgBulletConfig();
				tgBulletConfig.setUuid(UUID.randomUUID());
				tgBulletConfigService.save(tgBulletConfig);
				currentUser.setBulletConfig(tgBulletConfig);

			}
			if(tgBulletConfig.getUuid()==null){
				tgBulletConfig.setUuid(UUID.randomUUID());
				tgBulletConfigService.save(tgBulletConfig);
				currentUser.setBulletConfig(tgBulletConfig);
			}
			userService.save(currentUser);
		}
	}

	public String createNewNotification() {
		List<BulletForkFilter> filters = tgBulletConfig.getFilters();
		if(CollectionUtils.isEmpty(filters)){
			filters = new LinkedList<>();
			tgBulletConfig.setFilters(filters);
		}
		BulletForkFilter bulletForkFilter = new BulletForkFilter(exchangeMetaLongService.getPersistencedExchangeMetaLong(buyExchange.getId()), sellExchange, symbol,minDeposit,minProfit);
		bulletForkFilterRepository.save(bulletForkFilter);
		filters.add(bulletForkFilter);
		tgBulletConfig.setFilters(filters);
		tgBulletConfigService.save(tgBulletConfig);
		return "notifications.xhtml";
	}

	public String getDeleteFilter(String id) {

		return "notifications.xhtml";
	}

	public void remove(BulletForkFilter filter) {
		try {
			tgBulletConfig.getFilters().remove(filter);
			tgBulletConfigService.save(tgBulletConfig);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public TgBulletConfig getTgBulletConfig() {
		return userService.getCurrentUserOrNull().getBulletConfig();
	}

	public void setBuyExchange(ExchangeMetaLong buyExchange) {
		this.buyExchange = buyExchange;
	}

	public ExchangeMetaLong getBuyExchange() {
		return buyExchange;
	}

	public void setSellExchange(ExchangeMetaLong sellExchange) {
		this.sellExchange = sellExchange;
	}

	public ExchangeMetaLong getSellExchange() {
		return sellExchange;
	}

	public SymbolLong getSymbol() {
		return symbol;
	}

	public void setSymbol(SymbolLong symbol) {
		this.symbol = symbol;
	}

	public Double getMinDeposit() {
		return minDeposit;
	}

	public void setMinDeposit(Double minDeposit) {
		this.minDeposit = minDeposit;
	}

	public Double getMinProfit() {
		return minProfit;
	}

	public void setMinProfit(Double minProfit) {
		this.minProfit = minProfit;
	}
}
