package com.malbi.sync.sku.util;

import com.malbi.sync.sku.application.ISessionManager;
import com.malbi.sync.sku.xls.XlsxSource;

public class MockSessionManager implements ISessionManager {

	private XlsxSource xSource;

	@Override
	public void setxSource(XlsxSource xSource) {
		this.xSource = xSource;
	}

	@Override
	public XlsxSource getxSource() {
		return this.xSource;
	}

}
