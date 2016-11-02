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

import com.malbi.sync.sku.converter.Exception2String;
import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.XlsRowData;
import com.malbi.sync.sku.service.SKUService;

@Named(value = "XLSProcessorController")
@RequestScoped
public class XLSProcessorController implements Serializable {

	private static final long serialVersionUID = -4146325243351405003L;

	@Inject
	private SKUService service;

	@Inject
	private ISessionManager sessionManager;

	private List<Changes> skuRename = new ArrayList<>();

	private List<Changes> doesNotExist = new ArrayList<>();

	private Map<Integer, String> skuMap = new HashMap<>();

	private String exceptionString = "";

	private boolean isCommitPrepared = false;

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
			FacesMessage msg = new FacesMessage("Ошибки при изменении XLS-файла", Exception2String.printStackTrace(e));
			addFacesMessage(msg);

		}
		return returnAddress;
	}

	public String goToDBProcessor() {
		return "/skugroupsprocessor.xhtml?faces-redirect=true";
	}

	public void removeXLSRows() {

		// corrected 24.12.2015.
		this.doesNotExist.stream().filter(t -> t.isChecked()).forEach(t -> {
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
			String skuName = t.getAfter();
			int key = t.getId();
			int rowId = this.sessionManager.getxSource().getArrayIdOfXlsRowDataBySkuCode(key);
			XlsRowData rowData = this.sessionManager.getxSource().getRows().get(rowId);
			rowData.setSkuName(skuName);
		});
		refreshIsCommitPrepared();
	}

	@PostConstruct // it doesn't work as needed.
	public void init() {
		refreshData();
	}

	public void refreshData() {
		StringBuilder log = new StringBuilder();

		List<XlsRowData> rows = this.sessionManager.getxSource().getRows();

		this.skuMap = service.getSkuMap();
		// check if there are errors.
		appendLog(log);

		// Let's not fill second table if database connection failed.
		if (this.skuMap.isEmpty()) {
			showFacesException(log);
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

		showFacesException(log);

	}

	private void showFacesException(StringBuilder log) {

		if (!log.toString().isEmpty()) {
			this.exceptionString = log.toString();
			FacesMessage msg = new FacesMessage("Ошибка работы с базой", this.exceptionString

			// , "Test"

			);
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

	private void appendLog(StringBuilder log) {
		String receivedLog = service.getErrorLog();
		// append carrige return if error message is not empty.
		log.append(receivedLog + (receivedLog.isEmpty() ? "" : "\n"));
	}

	public List<Changes> getSkuRename() {
		return this.skuRename;
	}

	public void setSkuRename(List<Changes> skuRename) {
		this.skuRename = skuRename;
	}

	public List<Changes> getDoesNotExist() {
		return doesNotExist;
	}

	public void setDoesNotExist(List<Changes> doesNotExist) {
		this.doesNotExist = doesNotExist;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public SKUService getService() {
		return service;
	}

	public void setService(SKUService service) {
		this.service = service;
	}

	public Map<Integer, String> getSkuMap() {
		return skuMap;
	}

	public void setSkuMap(Map<Integer, String> skuMap) {
		this.skuMap = skuMap;
	}

	public String getExceptionString() {
		return exceptionString;
	}

	public void setExceptionString(String exceptionString) {
		this.exceptionString = exceptionString;
	}

	public boolean isCommitPrepared() {
		return isCommitPrepared;
	}

	public void setCommitPrepared(boolean isCommitPrepared) {
		this.isCommitPrepared = isCommitPrepared;
	}

}
