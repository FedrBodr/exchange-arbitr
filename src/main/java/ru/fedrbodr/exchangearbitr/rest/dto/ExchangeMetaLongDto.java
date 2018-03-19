package ru.fedrbodr.exchangearbitr.rest.dto;

import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
public class ExchangeMetaLongDto implements Serializable {
	private int id;
	private String exchangeName;
	private String exchangeUrl;
	private String symbolMarketUrl;
	private String refParam;

	public ExchangeMetaLongDto(ExchangeMeta exchangeMeta) {
		this.id = exchangeMeta.getId();
		this.exchangeName = exchangeMeta.getExchangeName();
		this.exchangeUrl = exchangeMeta.getExchangeUrl();
		this.symbolMarketUrl = exchangeMeta.getSymbolMarketUrl();
		this.refParam = exchangeMeta.getRefParam();
	}

	public ExchangeMetaLongDto(ExchangeMetaLong exchangeMeta) {
		this.id = exchangeMeta.getId();
		this.exchangeName = exchangeMeta.getExchangeName();
		this.exchangeUrl = exchangeMeta.getExchangeUrl();
		this.symbolMarketUrl = exchangeMeta.getSymbolMarketUrl();
		this.refParam = exchangeMeta.getRefParam();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getExchangeUrl() {
		return exchangeUrl;
	}

	public void setExchangeUrl(String exchangeUrl) {
		this.exchangeUrl = exchangeUrl;
	}

	public String getSymbolMarketUrl() {
		return symbolMarketUrl;
	}

	public void setSymbolMarketUrl(String symbolMarketUrl) {
		this.symbolMarketUrl = symbolMarketUrl;
	}

	public String getRefParam() {
		return refParam;
	}

	public void setRefParam(String refParam) {
		this.refParam = refParam;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExchangeMetaLongDto that = (ExchangeMetaLongDto) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
