package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * Representing currency pair short names
 * </p>
 * <p>
 * Reference currency names look there org.knowm.xchange.currency.Currency
 * </p>
 *
 * */
@Entity
@Table(name = "symbol")
@Data
@NoArgsConstructor
public class Symbol implements Serializable {
	@Id
	@GeneratedValue
	private long id;
	@Column(unique=true)
	private String name;
	@Column(name = "base_name")
	private String baseName;
	@Column(name = "quote_name")
	private String quoteName;

	public Symbol(String name, String baseName, String secondaryCurrencyName) {
		this.name = name;
		this.baseName = baseName;
		this.quoteName = secondaryCurrencyName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Symbol that = (Symbol) o;
		return id == that.id &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, name);
	}
}
