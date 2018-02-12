package ru.fedrbodr.exchangearbitr.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * <p>
 * Representing symbolPair
 * </p>
 * <p>
 * Reference currency names look there org.knowm.xchange.currency.Currency
 * </p>
 *
 * */
@Entity
@Data
@NoArgsConstructor
public class SymbolPair implements Serializable {
	@Id
	@GeneratedValue
	private long id;
	@Column(unique=true)
	private String name;
	private String baseName;
	private String quoteName;

	public SymbolPair(String name, String baseName, String secondaryCurrencyName) {
		this.name = name;
		this.baseName = baseName;
		this.quoteName = secondaryCurrencyName;
	}
}
