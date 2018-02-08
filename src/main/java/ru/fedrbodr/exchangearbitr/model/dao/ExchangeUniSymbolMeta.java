package ru.fedrbodr.exchangearbitr.model.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Info about the symbol at the exchange like active, tax and etc.
 *
 * */
@Entity
@Data
@NoArgsConstructor
public class ExchangeUniSymbolMeta implements Serializable {
	@EmbeddedId
	protected ExchangeUniSymbolPK exchangeUniSymbolPK;
	private boolean acvtive;

	public ExchangeUniSymbolMeta(ExchangeMeta exchangeMeta, UniSymbol uniSymbol, boolean acvtive) {
		this.exchangeUniSymbolPK = new ExchangeUniSymbolPK(exchangeMeta,uniSymbol);
		this.acvtive = acvtive;
	}
}