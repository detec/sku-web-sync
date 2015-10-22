package com.malbi.sync.sku.application;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@WebFilter(filterName = "AuthFilter", urlPatterns = { "*.xhtml" })
public class AuthorizationFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public AuthorizationFilter() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest reqt = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession ses = reqt.getSession(false);

		String reqURI = reqt.getRequestURI();

		// Let's split conditions into separate parts
		boolean itIsLoginpage = (reqURI.indexOf("/login.xhtml") >= 0);
		boolean therIsUsername = (ses != null && ses.getAttribute("username") != null);

		if (itIsLoginpage || therIsUsername) {

			chain.doFilter(request, response);
		} else {
			resp.sendRedirect(reqt.getContextPath() + "/login.xhtml");
		}

	}

}
