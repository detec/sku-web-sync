package com.malbi.sync.sku.model;

public class XlsRowData {

	private int rowNo;
	private int skuCode;
	private String skuName;
	private String skuGroup;
	private int skuGroupCode;
	private String business;
	private String subGroup;
	private int primaryGroup;
	private int businessSort;
	private int subGroupSort;
	private int groupSort;

	public XlsRowData(int rowNo, int skuCode, String skuName, String skuGroup, int skuGroupCode, String business,
			String subGroup, int primaryGroup, int businessSort, int subGroupSort, int groupSort) {
		this.rowNo = rowNo;
		this.skuCode = skuCode;
		this.skuName = skuName;
		this.skuGroup = skuGroup;
		this.skuGroupCode = skuGroupCode;
		this.business = business;
		this.subGroup = subGroup;
		this.primaryGroup = primaryGroup;
		this.businessSort = businessSort;
		this.subGroupSort = subGroupSort;
		this.groupSort = groupSort;
	}

	public int getRowNo() {
		return rowNo;
	}

	public int getSkuCode() {
		return skuCode;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getSkuGroup() {
		return skuGroup;
	}

	public int getSkuGroupCode() {
		return skuGroupCode;
	}

	public String getBusiness() {
		return business;
	}

	public String getSubGroup() {
		return subGroup;
	}

	public int getPrimaryGroup() {
		return primaryGroup;
	}

	public int getBusinessSort() {
		return businessSort;
	}

	public int getSubGroupSort() {
		return subGroupSort;
	}

	public int getGroupSort() {
		return groupSort;
	}

	public boolean isEmpty() {
		if (skuCode == 0 && skuName.isEmpty() && skuGroup.isEmpty() && skuGroupCode == 0 && business.isEmpty()
				&& subGroup.isEmpty() && primaryGroup == 0 && businessSort == 0 && subGroupSort == 0
				&& groupSort == 0) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "rowNo: " + rowNo + ", skuCode: " + skuCode + ", skuName: " + skuName + ", skuGroup: " + skuGroup
				+ ", skuGroupCode: " + skuGroupCode + ", business: " + business + ", subGroup: " + subGroup
				+ ", primaryGroup: " + primaryGroup + ", businessSort: " + businessSort + ", subGroupSort: "
				+ subGroupSort + ", groupSort: " + groupSort;
	}
}
