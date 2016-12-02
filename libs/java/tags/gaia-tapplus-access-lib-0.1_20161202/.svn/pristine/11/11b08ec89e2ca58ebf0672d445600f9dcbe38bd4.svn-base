/*******************************************************************************
 * Copyright (c) 2016 European Space Agency.
 ******************************************************************************/
import java.io.BufferedReader;
import java.util.List;

import esac.archive.gacs.cl.tapclient.TapPlusConnection;

/**
 * Testing class with some examples of how to use the TapPlusConnection class
 * It will log in, upload a table, do a synchronous and an asynchronous query
 * and logout. Check for private tables is done to show the user schema
 * 
 * @author jsalgado
 *
 */
public class TestTapGacs {

	/**
	 * Parameters needed: 
	 * -u <user> 
	 * -p <password> 
	 * -f <votable to be upload> 
	 * -t <name of the table in user schema>
	 * -r <name of the column where ra is located>
	 * -d <name of the column where dec is located>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Parsing arguments
		String username = "";
		String password = "";
		String fileToBeUploaded = "";
		String uploadedTableName = "";
		String privateTableName = "";
		String raColumn = "";
		String decColumn = "";

		int length = args.length;
		for (int i = 0; i < length; i++) {

			if (args[i].equals("-u") || args[i].equals("--user")) {
				// Reading username
				i++;
				username = args[i];

			} else if (args[i].equals("-p") || args[i].equals("--password")) {
				// Reading password
				i++;
				password = args[i];

			} else if (args[i].equals("-f") || args[i].equals("--file")) {
				// Reading file Name
				i++;
				fileToBeUploaded = args[i];
			} else if (args[i].equals("-t") || args[i].equals("--table")) {
				// Reading file Name
				i++;
				uploadedTableName = args[i];

			} else if (args[i].equals("-r") || args[i].equals("--raColumn")) {
				// Reading file Name
				i++;
				raColumn = args[i];

			} else if (args[i].equals("-d") || args[i].equals("--decColumn")) {
				// Reading file Name
				i++;
				decColumn = args[i];

			} else
				System.err.println("Unrecognized parameter");
		}

		// Example
		TapPlusConnection tapPlusConnection = new TapPlusConnection(
				"https://gea.esac.esa.int/tap-server/");
		try {
			// Example of login. This tapPlusConnection is now logged
			System.out.println("================== Login ===================");
			tapPlusConnection.login(username, password);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// Example discovering tables
			System.out.println("========== Get private tables =============");
			List<String> privateTablesResult = tapPlusConnection
					.getPrivateTables();
			
			if(privateTablesResult.size()>0){
				privateTableName=privateTablesResult.get(0);
			}
			printList(privateTablesResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(!uploadedTableName.isEmpty()){
			try {
				// Example uploading table
				System.out.println("================== Upload =================");
				System.out.println("tableName:" + uploadedTableName);
				System.out.println("fileToBeUploaded:" + fileToBeUploaded);
	
				List<String> uploadResult = tapPlusConnection.uploadTable(
						uploadedTableName, raColumn, decColumn, fileToBeUploaded);
				printList(uploadResult);
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			// Example discovering tables
			System.out.println("=========== Get private tables ===========");
			List<String> privateTablesResult = tapPlusConnection
					.getPrivateTables();
			printList(privateTablesResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(!privateTableName.isEmpty()){
			try {
				// Example of a synchronous-authenticated query
				System.out.println("==== Synchronous/authenticated query =====");
				String query = "SELECT+TOP+10+*+FROM+"+ privateTableName;
	
				BufferedReader reader = tapPlusConnection.querySynchronous(query,
						"csv");
				String line = "";
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				tapPlusConnection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			// Example of a asynchronous-authenticated query
			System.out.println("=== Asynchronous/authenticated query ====");
			String query = "SELECT+TOP+10+*+FROM+"+ privateTableName;
			String jobId = tapPlusConnection.queryAsynchronous(query,
					"csv");

			// You can check the status of the job using this method
			System.out.println(jobId+" "+tapPlusConnection.getJobStatus(jobId));

			// It waits in a loop until the job is completed
			String resultsLocation = tapPlusConnection.waitForJob(jobId);

			// You can confirm the status of the job using this method
			System.out.println(jobId+" "+tapPlusConnection.getJobStatus(jobId));

			BufferedReader readerResults = tapPlusConnection
					.getResults(resultsLocation);
			String line = "";
			while ((line = readerResults.readLine()) != null) {
				System.out.println(line);
			}
			
			//Thread.sleep(5000);
			// Remove the job
			if(tapPlusConnection.deleteJob(jobId)){
				System.out.println("Async job "+jobId+" deleted");
			}else{
				System.out.println("Could not delete job "+jobId);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(!uploadedTableName.isEmpty()){
			try {
				// Example deleting table
				System.out.println("========= Delete table =================");
				tapPlusConnection.deleteTable(uploadedTableName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			// Example discovering tables
			System.out.println("========= Get private tables ===========");
			List<String> privateTablesResult = tapPlusConnection
					.getPrivateTables();
			printList(privateTablesResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// Logout from session
			System.out.println("============= Log out ==================");
			tapPlusConnection.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printList(List<String> stringArray) {
		try {
			for (String line : stringArray)
				System.out.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
