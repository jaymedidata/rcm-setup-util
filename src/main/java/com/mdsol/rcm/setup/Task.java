package com.mdsol.rcm.setup;

import lombok.Getter;

public enum Task {
	/**
	 * Send MAuth Signed Request
	 */
	SEND_REQUEST("send_req"),
	
	/**
	 * Subscribe to Archon Topics
	 */
	ARCHON_SUB("archon_subscription"),
	
	/**
	 * Create Building Blocks
	 */
	CREATE_BUILDING_BLOCK("create_building_block");
	
	@Getter
	private String taskName;
	
	private Task(String taskName) {
		this.taskName = taskName;
	}
	
}
