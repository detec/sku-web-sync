package com.malbi.sync.sku.application;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.XlsRowData;
import com.malbi.sync.sku.service.SKUService;

@Named(value = "XLSProcessorController")
@RequestScoped
public class XLSProcessorController implements Serializable {

	// stub method to create instance of class.
	public void processAgainstDB() {
		refreshData();

	}

	public String commitXLSChanges() {

		// first we rename in file
		renameInXLS();

		// then we remove rows in file
		removeXLSRows();

		String returnAddress = "";
		try {
			this.sessionManager.getxSource().updateXlsSource();
			returnAddress = "/skugroupsprocessor.xhtml?faces-redirect=true";
		} catch (IOException e) {
			FacesMessage msg = new FacesMessage("Ошибки при изменении XLS-файла", e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);

			// this.ExceptionString = e.getMessage();
		}
		return returnAddress;
	}

	public String goToDBProcessor() {
		return "/skugroupsprocessor.xhtml?faces-redirect=true";
	}

	public void removeXLSRows() {

		skuRename.stream().filter(t -> t.isChecked()).forEach(t -> {
			int key = t.getId();
			int rowId = this.sessionManager.getxSource().getArrayIdOfXlsRowDataBySkuCode(key);
			this.sessionManager.getxSource().getRows().remove(rowId);
		});

		refreshIsCommitPrepared();
	}

	public void refreshIsCommitPrepared() {
		int sizeRename = skuRename.stream().filter(t -> t.isChecked()).collect(Collectors.toList()).size();
		int sizeRemove = doesNotExist.stream().filter(t -> t.isChecked()).collect(Collectors.toList()).size();
		this.isCommitPrepared = (sizeRename + sizeRemove > 0) ? true : false;
	}

	public void renameInXLS() {

		skuRename.stream().filter(t -> t.isChecked()).forEach(t -> {
			String SKUName = t.getAfter();
			int key = t.getId();
			int rowId = this.sessionManager.getxSource().getArrayIdOfXlsRowDataBySkuCode(key);
			XlsRowData rowData = this.sessionManager.getxSource().getRows().get(rowId);
			rowData.setSkuName(SKUName);
		});
		refreshIsCommitPrepared();
	}

	@PostConstruct // it doesn't work as needed.
	public void init() {
		refreshData();
	}

	public void refreshData() {
		StringBuffer log = new StringBuffer();

		List<XlsRowData> rows = this.sessionManager.getxSource().getRows();
		SKUService service = new SKUService();
		this.skuMap = service.getSkuMap();
		// check if there are errors.
		// this.ExceptionString = service.getErrorLog();
		appendLog(service, log);

		// Let's not fill second table if database connection failed.
		if (this.skuMap.size() == 0) {
			return;
		}
		for (XlsRowData xls : rows) {
			int skuCode = xls.getSkuCode();
			String xlsSKUName = xls.getSkuName();

			String dbSkuName = this.skuMap.get(skuCode);
			if (dbSkuName == null) {
				doesNotExist.add(new Changes(skuCode, xlsSKUName, "<не существует>"));
			} else if (!dbSkuName.equals(xlsSKUName)) {
				skuRename.add(new Changes(skuCode, xlsSKUName, dbSkuName));
			}
		}

		if (!log.toString().isEmpty()) {
			this.ExceptionString = log.toString();
			FacesMessage msg = new FacesMessage("Ошибка работы с базой", this.ExceptionString);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	public void appendLog(SKUService service, StringBuffer log) {
		String receivedLog = service.getErrorLog();
		// append carrige return if error message is not empty.
		log.append(receivedLog + ((receivedLog.length() == 0) ? "" : "\n"));
	}

	List<Changes> skuRename = new ArrayList<Changes>();

	public List<Changes> getSkuRename() {
		// return skuRename;
		// refreshData();
		return this.skuRename;
	}

	public void setSkuRename(List<Changes> skuRename) {
		this.skuRename = skuRename;
	}

	List<Changes> doesNotExist = new ArrayList<Changes>();

	public List<Changes> getDoesNotExist() {
		return doesNotExist;
	}

	public void setDoesNotExist(List<Changes> doesNotExist) {
		this.doesNotExist = doesNotExist;
	}

	@Inject
	private LoginBean sessionManager;

	public LoginBean getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(LoginBean sessionManager) {
		this.sessionManager = sessionManager;
	}

	Map<Integer, String> skuMap = new HashMap<>();

	public Map<Integer, String> getSkuMap() {
		return skuMap;
	}

	public void setSkuMap(Map<Integer, String> skuMap) {
		this.skuMap = skuMap;
	}

	private String ExceptionString;

	public String getExceptionString() {
		return ExceptionString;
	}

	public void setExceptionString(String exceptionString) {
		ExceptionString = exceptionString;
	}

	private boolean isCommitPrepared = false;

	public boolean isCommitPrepared() {
		return isCommitPrepared;
	}

	public void setCommitPrepared(boolean isCommitPrepared) {
		this.isCommitPrepared = isCommitPrepared;
	}

	private static final long serialVersionUID = -4146325243351405003L;
}
