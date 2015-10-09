package com.malbi.sync.sku.converter;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.malbi.sync.sku.model.DBSKUGroup;

@FacesConverter(forClass = DBSKUGroup.class, value = "DBSKUGroupConverter")
@RequestScoped
public class DBSKUGroupConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		if (submittedValue == null || submittedValue.isEmpty()) {
			return null;
		}

		try {
			int id = Integer.valueOf(submittedValue).intValue();
			return new DBSKUGroup(id);
		} catch (NumberFormatException e) {
			throw new ConverterException(
					new FacesMessage(String.format("%s не является корректным ID группы SKU", submittedValue)), e);
		}

	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		if (modelValue == null) {
			return "";
		}

		if (modelValue instanceof DBSKUGroup) {
			DBSKUGroup object = (DBSKUGroup) modelValue;
			// return String.valueOf(object.getKey());
			return Integer.toString(object.getKey());
		} else {

			// let's try to do nothing and return empty string
			return "";

			// throw new ConverterException(
			// new FacesMessage(String.format("%s не является корректным
			// экземпляром DBSKUGroup", modelValue)));
		}
	}

}
