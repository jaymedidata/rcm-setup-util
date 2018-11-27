package com.mdsol.rcm.setup;

import lombok.Getter;

@Getter
public class SetupConfig {
	// Archon Subscription URLs
	public static final String ARCHON_SUB_URL_STUDIES = "archon.topic.studies";
	public static final String ARCHON_SUB_URL_STUDY_ENVIRONMENT_SITES = "archon.topic.study_environment_sites";
	public static final String ARCHON_SUB_URL_STUDY_ENVIRONMENT_COUNTRIES = "archon.topic.study_environment_countries";
	public static final String ARCHON_SUB_URL_CONFIGIRATION_TYPES = "archon.topic.configuration_types";
	public static final String ARCHON_SUB_URL_CONFIGIRATION_TYPE_ROLES = "archon.topic.configuration_type_roles";
	public static final String ARCHON_SUB_URL_STUDY_ENVIRONMENT_ASSIGNMENTS = "archon.topic.study_environment_assignments";
	public static final String ARCHON_SUB_URL_CLIENT_DIVISION_SCHEME_ASSIGNMENTS = "archon.topic.client_division_scheme_assignments";
	
//	// Configuration Type Roles API
//	public static final String CONFIGURATION_TYPE_ROLES_API = "bb.configuration_type_roles.api";
//	
//	// Permissions API
//	public static final String PERMISSIONS_API = "bb.permissions.api";
	
	
	//Dalton API Host
	public static final String DALTON_API_HOST = "dalton.api.host";
	
	// AWS config file property keys
	public static final String PROP_KEY_STUDIES = "prop.key.studies";
	public static final String PROP_KEY_STUDY_ENVIRONMENT_SITES = "prop.key.study_environment_sites";
	public static final String PROP_KEY_STUDY_ENVIRONMENT_COUNTRIES = "prop.key.study_environment_countries";
	public static final String PROP_KEY_CONFIGIRATION_TYPES = "prop.key.configuration_types";
	public static final String PROP_KEY_CONFIGIRATION_TYPE_ROLES = "prop.key.configuration_type_roles";
	public static final String PROP_KEY_STUDY_ENVIRONMENT_ASSIGNMENTS = "prop.key.study_environment_assignments";
	public static final String PROP_KEY_CLIENT_DIVISION_SCHEME_ASSIGNMENTS = "prop.key.client_division_scheme_assignments";
	
	
}
