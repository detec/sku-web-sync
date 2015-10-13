package com.malbi.sync.sku.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

@ManagedBean(name = "XLSDownloadController")
@RequestScoped
public class XLSDownloadController implements Serializable {

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

		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);

			OutputStream responseOutputStream = response.getOutputStream();
			int bytes;
			while ((bytes = fileInputStream.read()) != -1) {
				responseOutputStream.write(bytes);
			}
			fileInputStream.close();
			responseOutputStream.close();

		} catch (IOException e) {
			FacesMessage msg = new FacesMessage("Ошибка скачивания изменённого xls-файла: \n" + e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	private static final long serialVersionUID = 2829805868397655132L;

	@ManagedProperty(value = "#{loginBean}")
	private LoginBean sessionManager;

	public LoginBean getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(LoginBean sessionManager) {
		this.sessionManager = sessionManager;
	}

}
