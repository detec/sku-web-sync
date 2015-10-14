package com.malbi.sync.sku.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DialogueChanges;
import com.malbi.sync.sku.service.SKUService;

@Named(value = "DBSKUGroupsController")
@RequestScoped
public class DBSKUGroupsController implements Serializable {

	public String goToSKUProcessor() {
		return "/skuprocessor.xhtml?faces-redirect=true";
	}

	public String applyChanges() {
		String returnAddress = "";

		SKUService service = new SKUService();
		StringBuffer log = new StringBuffer();
		// rename checked groups
		this.updateDBGroupList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			result = service.renameGroup(t);
			if (!result) {
				String receivedLog = service.getErrorLog();
				// append carrige return if error message is not empty.
				log.append(receivedLog + ((receivedLog.length() == 0) ? "" : "\n"));
			}
		});

		this.addDBGroupList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			int parentId = t.getParent().getId();
			result = service.addNewGroup(parentId, t);
			if (!result) {
				String receivedLog = service.getErrorLog();
				// append carriage return if error message is not empty.
				log.append(receivedLog + ((receivedLog.length() == 0) ? "" : "\n"));
			}
		});

		// after all operations.
		if (!log.toString().isEmpty()) {
			this.ExceptionString = log.toString();
			FacesMessage msg = new FacesMessage("Ошибка работы с базой", this.ExceptionString);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} else {
			returnAddress = "/skuprocessor.xhtml?faces-redirect=true";
		}
		return returnAddress;
	}

	@PostConstruct
	public void init() {
		refreshData();
	}

	public void appendLogAtRefresh(SKUService service, StringBuffer log) {
		if (!log.toString().isEmpty()) {
			log.append(service.getErrorLog());
		}
	}

	public void refreshData() {
		SKUService service = new SKUService();
		Map<Integer, String> skuGroupMap = service.getSkuGroupMap();
		StringBuffer log = new StringBuffer();
		appendLogAtRefresh(service, log);

		List<Changes> groupUpdate = this.sessionManager.getxSource().getGroupUpdates(skuGroupMap);
		groupUpdate.stream().forEach(t -> {
			if (t.getBefore() == null) {
				addDBGroupList.add(new DialogueChanges(t));
			} else {
				updateDBGroupList.add(t);
			}
		});

		this.selectGroupsList.addAll(service.getDBSKUgroups());
		// catch error messages
		appendLogAtRefresh(service, log);

		if (!log.toString().isEmpty()) {
			this.ExceptionString = log.toString();
			FacesMessage msg = new FacesMessage("Ошибка работы с базой", this.ExceptionString);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	private List<DBSKUGroup> selectGroupsList = new ArrayList<DBSKUGroup>();

	public List<DBSKUGroup> getSelectGroupsList() {
		return selectGroupsList;
	}

	public void setSelectGroupsList(List<DBSKUGroup> selectGroupsList) {
		this.selectGroupsList = selectGroupsList;
	}

	private List<DialogueChanges> addDBGroupList = new ArrayList<>();

	public List<DialogueChanges> getAddDBGroupList() {
		return addDBGroupList;
	}

	public void setAddDBGroupList(List<DialogueChanges> addDBGroupList) {
		this.addDBGroupList = addDBGroupList;
	}

	private List<Changes> updateDBGroupList = new ArrayList<>();

	public List<Changes> getUpdateDBGroupList() {
		return updateDBGroupList;
	}

	public void setUpdateDBGroupList(List<Changes> updateDBGroupList) {
		this.updateDBGroupList = updateDBGroupList;
	}

	private String ExceptionString;

	public String getExceptionString() {
		return ExceptionString;
	}

	public void setExceptionString(String exceptionString) {
		ExceptionString = exceptionString;
	}

	@Inject
	private LoginBean sessionManager;

	public LoginBean getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(LoginBean sessionManager) {
		this.sessionManager = sessionManager;
	}

	private static final long serialVersionUID = 3971794466485136396L;

}
