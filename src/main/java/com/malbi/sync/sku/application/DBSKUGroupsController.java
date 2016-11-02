package com.malbi.sync.sku.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DialogueChanges;
import com.malbi.sync.sku.service.SKUService;

@Named(value = "DBSKUGroupsController")
@RequestScoped
public class DBSKUGroupsController implements Serializable {

	private static final long serialVersionUID = 3971794466485136396L;

	private static final String CONSTANT_DB_ERROR = "Ошибка работы с базой";

	@Inject
	private SKUService service;

	@Inject
	private ISessionManager sessionManager;

	private String exceptionString;

	private List<Changes> updateDBGroupList = new ArrayList<>();

	private List<DBSKUGroup> selectGroupsList = new ArrayList<>();

	private List<DialogueChanges> addDBGroupList = new ArrayList<>();

	public String goToSKUProcessor() {
		return "/skuprocessor.xhtml?faces-redirect=true";
	}

	public String applyChanges() {
		String returnAddress = "";

		StringBuilder log = new StringBuilder();

		// 09.11.2015, Andrei Duplik
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		this.addDBGroupList.stream().filter(t -> t.isChecked()).forEach(t -> {
			Set<ConstraintViolation<DialogueChanges>> constraintViolations = validator.validate(t);

			Iterator<ConstraintViolation<DialogueChanges>> iterator = constraintViolations.iterator();

			if (!constraintViolations.isEmpty()) {
				while (iterator.hasNext()) {
					ConstraintViolation<DialogueChanges> dialogueChangesViolation = iterator.next();

					String propertyPath = dialogueChangesViolation.getPropertyPath().toString();
					String message = dialogueChangesViolation.getMessage();
					log.append("В строке с id " + Integer.toString(t.getId()) + " для поля " + propertyPath
							+ " выведено сообщение об ошибке проверки " + message);
				}

			}

		});

		// if we have errors - quit.
		if (!log.toString().isEmpty()) {
			this.exceptionString = log.toString();
			FacesMessage msg = new FacesMessage(CONSTANT_DB_ERROR, this.exceptionString);
			addFacesMessage(msg);

			return returnAddress;
		}
		// 09.11.2015, Andrei Duplik

		// rename checked groups
		this.updateDBGroupList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			result = service.renameGroup(t);
			appendLog(result, log);
		});

		this.addDBGroupList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			// we can also add to top-level group.
			// There is group Итого, id 1005. If none is selected - what to do?
			int parentId = t.getParent().getId();
			result = service.addNewGroup(parentId, t);
			appendLog(result, log);
		});

		// after all operations.
		if (!log.toString().isEmpty()) {
			this.exceptionString = log.toString();
			FacesMessage msg = new FacesMessage(CONSTANT_DB_ERROR, this.exceptionString);
			addFacesMessage(msg);

		} else {
			returnAddress = "/skuprocessor.xhtml?faces-redirect=true";
		}
		return returnAddress;
	}

	private void appendLog(boolean result, StringBuilder log) {
		if (!result) {
			String receivedLog = service.getErrorLog();
			// append carriage return if error message is not empty.
			log.append(receivedLog + (receivedLog.isEmpty() ? "" : "\n"));
		}
	}

	@PostConstruct
	public void init() {
		refreshData();
	}

	private void appendLogAtRefresh(StringBuilder log) {
		if (!log.toString().isEmpty()) {
			log.append(service.getErrorLog());
		}
	}

	public void refreshData() {

		StringBuilder log = new StringBuilder();

		List<Changes> groupUpdate = this.sessionManager.getxSource().getGroupUpdates();
		groupUpdate.stream().forEach(t -> {
			if (t.getBefore() == null) {
				// 09.11.2015, Andrei Duplik
				// will add parent group Итого, as it is the only top level
				// group
				DialogueChanges dialogueChanges = new DialogueChanges(t);
				// by default, we are adding top level group
				dialogueChanges.setParent(new DBSKUGroup(1005, "Итого"));
				addDBGroupList.add(dialogueChanges);
			} else {
				updateDBGroupList.add(t);
			}
		});

		this.selectGroupsList.addAll(service.getDBSKUgroups());
		// catch error messages
		appendLogAtRefresh(log);

		if (!log.toString().isEmpty()) {
			this.exceptionString = log.toString();
			FacesMessage msg = new FacesMessage(CONSTANT_DB_ERROR, this.exceptionString);
			addFacesMessage(msg);
		}
	}

	private void addFacesMessage(FacesMessage msg) {
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			fc.addMessage(null, msg);
		}
	}

	public List<DBSKUGroup> getSelectGroupsList() {
		return selectGroupsList;
	}

	public void setSelectGroupsList(List<DBSKUGroup> selectGroupsList) {
		this.selectGroupsList = selectGroupsList;
	}

	public List<DialogueChanges> getAddDBGroupList() {
		return addDBGroupList;
	}

	public void setAddDBGroupList(List<DialogueChanges> addDBGroupList) {
		this.addDBGroupList = addDBGroupList;
	}

	public List<Changes> getUpdateDBGroupList() {
		return updateDBGroupList;
	}

	public void setUpdateDBGroupList(List<Changes> updateDBGroupList) {
		this.updateDBGroupList = updateDBGroupList;
	}

	public String getExceptionString() {
		return exceptionString;
	}

	public void setExceptionString(String exceptionString) {
		this.exceptionString = exceptionString;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public SKUService getService() {
		return service;
	}

	public void setService(SKUService service) {
		this.service = service;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

}
