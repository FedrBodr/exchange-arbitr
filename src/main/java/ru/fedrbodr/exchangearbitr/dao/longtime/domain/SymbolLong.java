package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Entity
@Data
@NoArgsConstructor
public class SymbolLong implements Serializable {
	@Id
	@GeneratedValue
	private long id;
	@Column(unique=true)
	private String name;
	@Column(name = "base_name")
	private String baseName;
	@Column(name = "quote_name")
	private String quoteName;

	public SymbolLong(String name, String baseName, String quoteName) {
		this.name = name;
		this.baseName = baseName;
		this.quoteName = quoteName;
	}

	public SymbolLong(Symbol symbol) {
		this.name = symbol.getName();
		this.baseName = symbol.getBaseName();
		this.quoteName = symbol.getQuoteName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SymbolLong that = (SymbolLong) o;
		return id == that.id &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, name);
	}
}
