package com.malbi.sync.sku.application;

import com.malbi.sync.sku.xls.XlsxSource;

/**
 * we need it to test JSF controllers.
 * 
 * @author Andrii Duplyk
 *
 */
public interface ISessionManager {

	public XlsxSource getxSource();

	public void setxSource(XlsxSource xSource);
}
