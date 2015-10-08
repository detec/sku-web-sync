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
import com.malbi.sync.sku.service.SKUService;

@ManagedBean(name = "DBSKUGroupsController")
@ViewScoped
public class DBSKUGroupsController implements Serializable {

	@PostConstruct
	public void init() {
		refreshData();
	}

	public void refreshData() {
		SKUService service = new SKUService();
		Map<Integer, String> skuGroupMap = service.getSkuGroupMap();
		this.ExceptionString = service.getErrorLog();

		List<Changes> groupUpdate = this.sessionManager.getxSource().getGroupUpdates(skuGroupMap);
		// for(Changes c: changes) {
		// if(c.getBefore()==null) {
		// addList.add(c);
		// } else {
		// updateList.add(c);
		// }
		// }
		groupUpdate.stream().forEach(t -> {
			if (t.getBefore() == null) {
				addDBGroupList.add(t);
			} else {
				updateDBGroupList.add(t);
			}
		});
	}

	private List<Changes> addDBGroupList = new ArrayList<>();

	public List<Changes> getAddDBGroupList() {
		return addDBGroupList;
	}

	public void setAddDBGroupList(List<Changes> addDBGroupList) {
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
