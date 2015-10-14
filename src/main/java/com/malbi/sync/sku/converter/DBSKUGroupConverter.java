package com.malbi.sync.sku.converter;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.service.SKUService;

//@FacesConverter(forClass = DBSKUGroup.class, value = "DBSKUGroupConverter")
@Named("DBSKUGroupConverter")
@SessionScoped
public class DBSKUGroupConverter implements Converter, Serializable {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		if (submittedValue == null || submittedValue.isEmpty()) {
			return null;
		}

		int id = 0;
		try {
			id = Integer.valueOf(submittedValue).intValue();

		} catch (NumberFormatException e) {
			// throw new ConverterException(
			// FacesMessage msg = new FacesMessage(String.format("%s не является
			// корректным ID группы SKU", submittedValue)), e);

			FacesMessage msg = new FacesMessage(
					String.format("%s не является корректным ID группы SKU", submittedValue), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

		// DBSKUGroup returnValue = new DBSKUGroup(id);
		// SKUService service = new SKUService();
		DBSKUGroup returnValue = service.getDBSKUGroupById(id);
		if (!service.getErrorLog().isEmpty()) {
			FacesMessage msg = new FacesMessage(String.format("Ошибка запрашивания в БД группы SKU %s ", id),
					service.getErrorLog());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		// boolean check = service.getDBSKUgroups().contains(returnValue);

		return returnValue;

	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		if (modelValue == null) {
			return "";
		}

		if (modelValue instanceof DBSKUGroup) {
			DBSKUGroup object = (DBSKUGroup) modelValue;
			// return String.valueOf(object.getKey());
			return Integer.toString(object.getId());
		} else {

			// let's try to do nothing and return empty string
			return "";

			// throw new ConverterException(
			// new FacesMessage(String.format("%s не является корректным
			// экземпляром DBSKUGroup", modelValue)));
		}
	}

	@Inject
	// transient, otherwise WELD-001413: The bean declares a passivating scope
	// but has a non-passivation-capable dependency
	transient private SKUService service;

	private static final long serialVersionUID = -3513173740780653398L;

}
