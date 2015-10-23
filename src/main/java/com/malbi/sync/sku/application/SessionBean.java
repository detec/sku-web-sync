package com.malbi.sync.sku.application;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionBean {

	public static HttpSession getSession() {
		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	public static String getUserName() {
		// HttpSession session = (HttpSession)
		// FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//
		//
		// if (session != null) {
		// Object attribute = session.getAttribute("username");
		// if (attribute != null) {
		// username = attribute.toString();
		// }
		// }

		String remoteuser = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();

		return remoteuser;
	}

}
