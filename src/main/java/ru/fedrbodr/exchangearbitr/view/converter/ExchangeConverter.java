package ru.fedrbodr.exchangearbitr.view.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;
import ru.fedrbodr.exchangearbitr.services.impl.ExchangeMetaLongService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@Service
public class ExchangeConverter implements Converter {

	// Init ---------------------------------------------------------------------------------------
	@Autowired
	private ExchangeMetaLongService exchangeMetaLongService;

	// Actions ------------------------------------------------------------------------------------

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		// Convert the unique String representation of Foo to the actual Foo object.
		return exchangeMetaLongService.getPersistencedExchangeMetaLong(Integer.valueOf(value));
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		// Convert the Foo object to its unique String representation.
		return String.valueOf(((ExchangeMetaLong) value).getId());
	}

}
