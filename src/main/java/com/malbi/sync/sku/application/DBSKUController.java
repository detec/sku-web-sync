package com.malbi.sync.sku.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.SKUGroupChanges;
import com.malbi.sync.sku.service.SKUService;

@Named(value = "DBSKUController")
@RequestScoped
public class DBSKUController implements Serializable {

	public void appendLog(SKUService service, StringBuffer log) {
		String receivedLog = service.getErrorLog();
		// append carriage return if error message is not empty.
		log.append(receivedLog + ((receivedLog.length() == 0) ? "" : "\n"));
	}

	public String goToXLSDownload() {
		return "/xlsdownload.xhtml?faces-redirect=true";
	}

	public String applyChanges() {
		String returnAddress = "";

		StringBuffer log = new StringBuffer();

		// move checked SKU
		this.updateList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			result = service.moveSkuToAnotherGroup(t);
			if (!result) {
				appendLog(service, log);
			}
		});

		// add checked SKU
		this.addList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			result = service.addSkuToDB(t);
			if (!result) {
				appendLog(service, log);
			}
		});

		// remove checked SKU
		this.removeList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			result = service.deleteSku(t);
			if (!result) {
				appendLog(service, log);
			}
		});
		// after all operations.
		if (!log.toString().isEmpty()) {

			this.ExceptionString = log.toString();
			FacesMessage msg = new FacesMessage("Ошибки при операциях с базы данных", this.ExceptionString);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} else {
			returnAddress = "/xlsdownload.xhtml?faces-redirect=true";
		}
		return returnAddress;
	}

	public void appendLogAtRefresh(SKUService service, StringBuffer log) {
		if (!log.toString().isEmpty()) {
			log.append(service.getErrorLog());
		}
	}

	public void changeGroupAll() {
		this.addList.forEach(t -> t.setChecked(true));
	}

	public void refreshData() {

		List<SKUGroupChanges> skuChanges = this.sessionManager.getxSource().getSKUUpdatesDBGroups();

		// for (Changes c : skuChanges)
		//
		// {
		// if (c.getBefore() == null) {
		// addList.add(c);
		// } else if (c.getAfter() == null) {
		// removeList.add(c);
		// } else {
		// updateList.add(c);
		// }
		// }
		skuChanges.stream().forEach(t -> {

			// this is obviously equal to null in older version.
			DBSKUGroup comparison = new DBSKUGroup();

			if (t.getBefore().equals(comparison)) {
				this.addList.add(t);
			} else if (t.getAfter().equals(comparison)) {
				this.removeList.add(t);
			} else {
				this.updateList.add(t);
			}

		});

		// after all operations.
		if (!this.sessionManager.getxSource().getExceptionString().isEmpty()) {

			// this.ExceptionString = log.toString();
			this.ExceptionString = this.sessionManager.getxSource().getExceptionString();
			FacesMessage msg = new FacesMessage("Ошибки при операциях с базы данных", this.ExceptionString);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);

		}

	}

	@PostConstruct
	public void init() {
		refreshData();
	}

	List<SKUGroupChanges> updateList = new ArrayList<SKUGroupChanges>();
	List<SKUGroupChanges> addList = new ArrayList<SKUGroupChanges>();
	List<SKUGroupChanges> removeList = new ArrayList<SKUGroupChanges>();

	public List<SKUGroupChanges> getUpdateList() {
		return updateList;
	}

	public void setUpdateList(List<SKUGroupChanges> updateList) {
		this.updateList = updateList;
	}

	public List<SKUGroupChanges> getAddList() {
		return addList;
	}

	public void setAddList(List<SKUGroupChanges> addList) {
		this.addList = addList;
	}

	public List<SKUGroupChanges> getRemoveList() {
		return removeList;
	}

	public void setRemoveList(List<SKUGroupChanges> removeList) {
		this.removeList = removeList;
	}

	private String ExceptionString;

	public String getExceptionString() {
		return ExceptionString;
	}

	public void setExceptionString(String exceptionString) {
		ExceptionString = exceptionString;
	}

	@Inject
	private ISessionManager sessionManager;

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	private static final long serialVersionUID = 2396746074529297511L;

	@Inject
	SKUService service;

}
