package com.mdsol.rcm.setup;

import lombok.Getter;

public enum BuildingBlocks {

	ACCESS_SITE_ETMF("access_site_etmf"),
	ADMINISTER_ALL_ASPECTS_OF_RCM("administer_all_aspects_of_rcm"),
	AUDIT_DOCUMENT_AREAS("audit_document_areas"),
	INSPECT_DOCUMENT_AREA("inspect_document_areas"),
	MANAGE_CONTENT_AREAS_DOCUMENT_FOLDER_SETTINGS_AND_CHANGE_REQUESTS("manage_content_areas_document_folder_settings_and_change_requests"),
	MANAGE_ETMF_STRUCTURE_CONTENT_AND_DATA("manage_etmf_structure_content_and_data"),
	UPLOAD_SITE_DOCUMENTS_TO_ETMFS("upload_site_documents_to_etmfs"),
	WORK_WITH_DOCUMENT_AND_TASKS("work_with_documents_and_tasks"),
	WORK_WITH_DOCUMENTS_TASKS_MIGRATIONS_AND_FOLDER_MANAGEMENT("work_with_documents_tasks_migrations_and_folder_management");
	
	@Getter
	private String bbName;
	
	private BuildingBlocks(String bbName) {
		this.bbName = bbName;
	}
	
}
