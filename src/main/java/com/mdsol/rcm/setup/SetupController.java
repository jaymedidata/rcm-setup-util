package com.mdsol.rcm.setup;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class SetupController {

	public static void main(String[] args) {
		String envProp = args[0];
		String mauthPath = args[1];
		String task = args[2];
		
		SetupService service = new SetupService();
		InputStream input = null;
		Properties prop = new Properties();
		
		try {
			input = new FileInputStream(envProp);
			prop.load(input);
		} catch (Exception e) {
			System.out.println("Error occurred during reading configuration file: " + e.getMessage());
			System.exit(0);
		}

		if (task.equalsIgnoreCase(Task.ARCHON_SUB.getTaskName())) {
			if (args.length != 4) {
				System.out.println("Invalid command, required arguments: env, task, subscription name");
				System.exit(0);
			}
			String subName = args[3];
			service.subToArchon(prop, subName, mauthPath);
			System.out.println("A SQS_ARN file has been generated");
		}

		if (task.equalsIgnoreCase(Task.CREATE_BUILDING_BLOCK.getTaskName())) {
			if (args.length != 4) {
				System.out.println(
						"Invalid command, required arguments: env, task, app uuid, app configuration type uuid");
				System.exit(0);
			}

			String ctUuid = args[3];
			service.generateBuildingBlocks(prop, mauthPath, ctUuid);
		}
		
		if (task.equalsIgnoreCase(Task.SEND_REQUEST.getTaskName())) {
			String apiUrl = args[2];
			service.sendApiRequest(mauthPath, apiUrl);
		}

		System.out.println("Task Completed");
	}

}
