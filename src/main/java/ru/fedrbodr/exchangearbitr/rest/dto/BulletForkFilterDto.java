package ru.fedrbodr.exchangearbitr.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.BulletForkFilter;

import java.util.Objects;

@Data
@NoArgsConstructor
public class BulletForkFilterDto {
	private Long id;
	/** Where we buy */
	private ExchangeMetaLongDto buyExchangeMeta;
	/** Where we sell */
	private ExchangeMetaLongDto sellExchangeMeta;
	private SymbolLongDto symbol;
	private Double minDeposit;
	private Double minProfit;

	public BulletForkFilterDto(BulletForkFilter filter) {
		this.id = filter.getId();
		this.buyExchangeMeta = new ExchangeMetaLongDto(filter.getBuyExchangeMeta());
		this.sellExchangeMeta = new ExchangeMetaLongDto(filter.getSellExchangeMeta());
		this.symbol = new SymbolLongDto(filter.getSymbol());
		this.minDeposit = filter.getMinDeposit();
		this.minProfit = filter.getMinProfit();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BulletForkFilterDto)) return false;
		BulletForkFilterDto that = (BulletForkFilterDto) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
