package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Symbol {
	@Id
	@GeneratedValue
	private long id;
	@Column(unique=true)
	private String name;
	private String baseName;
	private String quoteName;

	public Symbol(String name, String baseName, String quoteName) {
		this.name = name;
		this.baseName = baseName;
		this.quoteName = quoteName;
	}
}
