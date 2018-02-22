package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Data
public class ForkPK implements Serializable{
	/*@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;*/
	@ManyToOne
	@JoinColumn(name = "buy_exchange_id")
	private ExchangeMetaLong buyExchangeMeta;
	@ManyToOne
	@JoinColumn(name = "sell_exchange_id")
	private ExchangeMetaLong sellExchangeMeta;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private SymbolLong symbol;




}
