package ru.fedrbodr.exchangearbitr.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * Representing symbol
 * </p>
 * <p>
 * Reference currency names look there org.knowm.xchange.currency.Currency
 * </p>
 *
 * */
@Data
@NoArgsConstructor
public class SymbolLongDto implements Serializable {
	private long id;
	private String name;
	private String baseName;
	private String quoteName;

	public SymbolLongDto(SymbolLong symbol) {
		this.id = symbol.getId();
		this.name = symbol.getName();
		this.baseName = symbol.getBaseName();
		this.quoteName = symbol.getQuoteName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SymbolLongDto that = (SymbolLongDto) o;
		return id == that.id &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
