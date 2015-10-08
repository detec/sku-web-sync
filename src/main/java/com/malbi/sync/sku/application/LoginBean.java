package com.malbi.sync.sku.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.hibernate.validator.constraints.NotEmpty;

import com.malbi.sync.sku.xls.XlsxSource;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 4615292844760430977L;

	// add default constructor
	public LoginBean() {

	}

	public String doLogin() {

		String key = this.username + this.password;

		if (!userList.contains(key)) {

			// Set login ERROR
			FacesMessage msg = new FacesMessage("Имя пользователя и/или пароль неверны!");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);

			// we don't need messages about duplicates.
			this.xSource = new XlsxSource();
			// To to login page
			// return "/login.xhtml";
			return "/login.xhtml?faces-redirect=true";
		}

		else {
			loggedIn = true;
			return "/main.xhtml";

		}
	}

	public String logout() {
		this.loggedIn = false;
		this.password = "";
		this.xSource = new XlsxSource();
		return "login";
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

	// private DBSource dbSource;
}
