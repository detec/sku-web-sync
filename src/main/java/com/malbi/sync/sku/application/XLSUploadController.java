package com.malbi.sync.sku.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.malbi.sync.sku.converter.Exception2String;
import com.malbi.sync.sku.xls.XlsxSource;

@Named("XLSUpload")
@ViewScoped
public class XLSUploadController implements Serializable {

	private static final long serialVersionUID = -8603992260673524815L;

	private static final String CONSTANT_MESSAGES = "messages";

	private File outputFile;

	boolean uploadedXLSValidationResult;

	@Inject // it is created at postconstruct
	private ISessionManager sessionManager;

	private boolean fileUploaded = false;

	private Part httpPart;

	private String fileContent;

	private String exceptionString;

	public Part getHttpPart() {
		return httpPart;
	}

	public void setHttpPart(Part httpPart) {
		this.httpPart = httpPart;
	}

	@PostConstruct
	public void init() {

		// Here we must initialize Login bean and session
		String username = SessionBean.getUserName();
		if (username.isEmpty()) {
			return;
		}

	}

	public void upload() {
		StringBuilder log = new StringBuilder();

		try {
			this.outputFile = File.createTempFile("SKU_BASE_1C", ".xls");
			this.outputFile.deleteOnExit();

		} catch (IOException e) {
			log.append(Exception2String.printStackTrace(e));
			this.fileUploaded = false;
			return;
		}

		byte[] buffer = new byte[1024];
		int bytesRead = 0;

		try (OutputStream outputStream = new FileOutputStream(outputFile);
				InputStream inputStream = httpPart.getInputStream();) {

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			this.fileUploaded = true;
		}
		//
		catch (IOException e) {
			log.append(Exception2String.printStackTrace(e));
			this.fileUploaded = false;
			return;
		}

		if (!log.toString().isEmpty()) {
			this.exceptionString = log.toString();
			FacesMessage msg = new FacesMessage("Ошибки при загрузке файла на сервер", this.exceptionString);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	// This this the main method
	public void checkXLSFile() {
		StringBuilder log = new StringBuilder();

		this.upload();
		if (!this.fileUploaded) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Закачка файла SKU_BASE_1C.xls",
					"Ошибка закачки!");
			FacesContext.getCurrentInstance().addMessage(CONSTANT_MESSAGES, message);
			return;
		}

		XlsxSource xSource = sessionManager.getxSource();
		xSource.setXlsFile(this.outputFile);
		// reading file into some linked list with rows.
		try {
			xSource.initData();
		} catch (Exception e) {
			log.append(e.getMessage());
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Закачка файла SKU_BASE_1C.xls",
					log.toString());
			FacesContext.getCurrentInstance().addMessage(CONSTANT_MESSAGES, message);
			return;
		}

		// Validation of rows.
		this.uploadedXLSValidationResult = xSource.validateInternal();
		if (!this.uploadedXLSValidationResult) {
			this.exceptionString = xSource.getValidationErrorLog();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Проверка файла SKU_BASE_1C.xls",
					this.exceptionString);
			FacesContext.getCurrentInstance().addMessage(CONSTANT_MESSAGES, message);
			return;
		}

	}

	public String goToXLSProcessor() {
		return "/xlsprocessor.xhtml?faces-redirect=true";
	}

	public String getExceptionString() {
		return exceptionString;
	}

	public void setExceptionString(String exceptionString) {
		this.exceptionString = exceptionString;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public boolean isUploadedXLSValidationResult() {
		return uploadedXLSValidationResult;
	}

	public void setUploadedXLSValidationResult(boolean uploadedXLSValidationResult) {
		this.uploadedXLSValidationResult = uploadedXLSValidationResult;
	}

	public boolean isFileUploaded() {
		return fileUploaded;
	}

	public void setFileUploaded(boolean fileUploaded) {
		this.fileUploaded = fileUploaded;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
}
