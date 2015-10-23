package com.malbi.sync.sku.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.hibernate.validator.constraints.NotEmpty;

import com.malbi.sync.sku.xls.XlsxSource;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

	// add default constructor
	public LoginBean() {

	}

	public String doLogin() {

		String errormessage = "";
		String key = this.username + this.password;

		if (this.username == null && this.password == null) {
			errormessage = "Имя пользователя и пароль не переданы!";
		}

		else if (!userList.contains(key)) {

			errormessage = "Имя пользователя и/или пароль неверны!";
			loggedIn = false;
		}

		else {
			// we don't need messages about duplicates.
			initializeApplicationSession();

			// put username when authenticating manually
			HttpSession session = SessionBean.getSession();
			session.setAttribute("username", username);

			return "/xlsupload.xhtml?faces-redirect=true";

		}

		// Set login ERROR
		FacesMessage msg = new FacesMessage("Ошибка аутентификации", errormessage);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(null, msg);
		return "";

	}

	// this method will be used to call both from this bean and from
	// XLSUploadcontroller
	public void initializeApplicationSession() {

		// Here we must initialize Login bean and session
		String username = SessionBean.getUserName();
		if (username.isEmpty()) {
			return;
		}

		this.xSource = new XlsxSource();
		this.loggedIn = true;

	}

	public String logout() {
		this.loggedIn = false;
		this.password = "";
		this.xSource = new XlsxSource();

		HttpSession session = SessionBean.getSession();
		session.invalidate();

		return "/login.xhtml?faces-redirect=true";
	}

	@NotEmpty(message = "Введите имя пользователя!")
	private String username;

	@NotEmpty(message = "Введите пароль!")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// Let it be combination of username and password
	private List<String> userList = new ArrayList<String>(Arrays.asList("usermalbi39", "adminadmin1981"));

	private Object CurrentObject;

	public Object getCurrentObject() {
		return CurrentObject;
	}

	public void setCurrentObject(Object currentObject) {
		CurrentObject = currentObject;
	}

	private boolean loggedIn;

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	private XlsxSource xSource = new XlsxSource();

	public XlsxSource getxSource() {
		return xSource;
	}

	public void setxSource(XlsxSource xSource) {
		this.xSource = xSource;
	}

	private static final long serialVersionUID = 7125135135382108493L;

}
