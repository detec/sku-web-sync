package com.malbi.sync.sku.model;

public class DbRowData {

	private int parentId;
	private int nodeId;
	private int isGroup;
	private int isPlanGroup;

	public DbRowData(int parentId, int nodeId, int isGroup, int isPlanGroup) {
		this.parentId = parentId;
		this.nodeId = nodeId;
		this.isGroup = isGroup;
		this.isPlanGroup = isPlanGroup;
	}

	public int getParentId() {
		return parentId;
	}

	public int getNodeId() {
		return nodeId;
	}

	public int getIsGroup() {
		return isGroup;
	}

	public int getIsPlanGroup() {
		return isPlanGroup;
	}

	@Override
	public String toString() {
		return "parentId:" + parentId + ", nodeId:" + nodeId + ", isGroup:" + isGroup + ", isPlanGroup:" + isPlanGroup;
	}
}
