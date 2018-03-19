package ru.fedrbodr.exchangearbitr.view.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.SymbolLongRepository;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@Service
public class SymbolConverter implements Converter {
	// Init ---------------------------------------------------------------------------------------
	@Autowired
	private SymbolLongRepository symbolLongRepository;

	// Actions ------------------------------------------------------------------------------------

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		// Convert the unique String representation of Foo to the actual Foo object.
		return symbolLongRepository.findById(Long.valueOf(value));
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		// Convert the Foo object to its unique String representation.

		return value==null ? "" : String.valueOf(((SymbolLong) value).getId());
	}

}
