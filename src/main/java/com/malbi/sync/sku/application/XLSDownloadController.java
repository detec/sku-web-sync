package com.malbi.sync.sku.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.malbi.sync.sku.converter.Exception2String;

@Named(value = "XLSDownloadController")
@RequestScoped
public class XLSDownloadController implements Serializable {

	private static final long serialVersionUID = 2829805868397655132L;

	@Inject
	private ISessionManager sessionManager;

	public void doDonwload() {

		// Get the FacesContext
		FacesContext facesContext = FacesContext.getCurrentInstance();

		// Get HTTP response
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

		// Set response headers
		response.reset(); // Reset the response in the first place

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=SKU_BASE_1C.xls");
		File file = this.sessionManager.getxSource().getXlsFile();
		response.setContentLength((int) file.length());

		int bytes;
		try (FileInputStream fileInputStream = new FileInputStream(file);
				OutputStream responseOutputStream = response.getOutputStream();) {
			while ((bytes = fileInputStream.read()) != -1) {
				responseOutputStream.write(bytes);
			}

		} catch (IOException e) {
			FacesMessage msg = new FacesMessage("Ошибка скачивания изменённого xls-файла",
					Exception2String.printStackTrace(e));
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

}
