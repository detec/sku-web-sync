package com.malbi.sync.sku.application;

import java.io.File;
import java.io.FileNotFoundException;
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

import com.malbi.sync.sku.xls.XlsxSource;

@Named("XLSUpload")
@ViewScoped
public class XLSUploadController implements Serializable {

	@PostConstruct
	public void init() {

		// Here we must initialize Login bean and session
		String username = SessionBean.getUserName();
		if (username.isEmpty()) {
			return;
		}

	}

	public void upload() {
		StringBuffer log = new StringBuffer();

		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			this.outputFile = File.createTempFile("SKU_BASE_1C", ".xls");

		} catch (IOException e) {
			log.append(e.getMessage());
			this.fileUploaded = false;
			return;
		}

		// reading upload content.
		try {
			inputStream = file.getInputStream();
		} catch (IOException e) {
			log.append(e.getMessage());
			this.fileUploaded = false;
			return;
		}

		// flushing input stream to server file copy
		try {
			outputStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			log.append(e.getMessage());
			this.fileUploaded = false;
			return;
		}

		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		try {
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			log.append(e.getMessage());

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e1) {

					log.append(e1.getMessage());
				}
			}
			this.fileUploaded = false;
			return;
		}

		// closing streams
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				log.append(e.getMessage());
				this.fileUploaded = false;
				return;
			}
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				log.append(e.getMessage());
				this.fileUploaded = false;
				return;
			}
		}

		this.fileUploaded = true;

		if (!log.toString().isEmpty()) {
			this.ExceptionString = log.toString();
			FacesMessage msg = new FacesMessage("Ошибки при загрузке файла на сервер", this.ExceptionString);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		return;

	}

	// This this the main method
	public void checkXLSFile() {
		StringBuffer log = new StringBuffer();

		this.upload();
		if (!this.fileUploaded) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Закачка файла SKU_BASE_1C.xls",
					"Ошибка закачки!");
			FacesContext.getCurrentInstance().addMessage("messages", message);
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
			FacesContext.getCurrentInstance().addMessage("messages", message);
			return;
		}

		// Validation of rows.
		this.uploadedXLSValidationResult = xSource.validateInternal();
		if (!this.uploadedXLSValidationResult) {
			this.ExceptionString = xSource.getValidationErrorLog();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Проверка файла SKU_BASE_1C.xls",
					this.ExceptionString);
			FacesContext.getCurrentInstance().addMessage("messages", message);
			return;
		}

	}

	public String goToXLSProcessor() {
		return "/xlsprocessor.xhtml?faces-redirect=true";
	}

	private Part file;
	private String fileContent;

	private String ExceptionString;

	public String getExceptionString() {
		return ExceptionString;
	}

	public void setExceptionString(String exceptionString) {
		ExceptionString = exceptionString;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	private static final long serialVersionUID = -8603992260673524815L;

	File outputFile;

	boolean uploadedXLSValidationResult;

	public boolean isUploadedXLSValidationResult() {
		return uploadedXLSValidationResult;
	}

	public void setUploadedXLSValidationResult(boolean uploadedXLSValidationResult) {
		this.uploadedXLSValidationResult = uploadedXLSValidationResult;
	}

	boolean fileUploaded = false;

	public boolean isFileUploaded() {
		return fileUploaded;
	}

	public void setFileUploaded(boolean fileUploaded) {
		this.fileUploaded = fileUploaded;
	}

	@Inject // it is created at postconstruct
	private ISessionManager sessionManager;

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
}
