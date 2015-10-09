package com.malbi.sync.sku.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;

import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DialogueChanges;
import com.malbi.sync.sku.service.SKUService;

@ManagedBean(name = "DBSKUGroupsController")
@ViewScoped
public class DBSKUGroupsController implements Serializable {

	public String goToSKUProcessor() {
		return "/skuprocessor.xhtml?faces-redirect=true";
	}

	public void test() {
		System.out.println("Alive");
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
			int parentId = t.getParent().getKey();
			result = service.addNewGroup(parentId, t);
			if (!result) {
				String receivedLog = service.getErrorLog();
				// append carriage return if error message is not empty.
				log.append(receivedLog + ((receivedLog.length() == 0) ? "" : "\n"));
			}
		});

		// after all operations.
		if (log.toString() != null) {
			this.ExceptionString = log.toString();
		} else {
			returnAddress = "/skuprocessor.xhtml?faces-redirect=true";
		}
		return returnAddress;
	}

	@PostConstruct
	public void init() {
		refreshData();
	}

	public void refreshData() {
		SKUService service = new SKUService();
		Map<Integer, String> skuGroupMap = service.getSkuGroupMap();
		StringBuffer log = new StringBuffer();
		if (service.getErrorLog() != null) {
			log.append(service.getErrorLog());
		}

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
		// log.append("\n" + service.getErrorLog());
		if (log.toString() != null) {
			this.ExceptionString = log.toString();
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

	@ManagedProperty(value = "#{loginBean}")
	private LoginBean sessionManager;

	public LoginBean getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(LoginBean sessionManager) {
		this.sessionManager = sessionManager;
	}

	private static final long serialVersionUID = 3971794466485136396L;

}
