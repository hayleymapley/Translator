package serverSide;

import java.io.*;
import java.util.*;
import java.sql.*;

public class TranslatorDBPopulator {

	private final static String DATA_FILE = "src/resources/englishspanish.txt";
	private static final String regex = "\t";
	
	private boolean populateDB() {
		
		boolean res = true;
		
		try {
			String databaseUser = "mapleyhayl";
			String databaseUserPass = "pass123";
			
			Class.forName("org.postgresql.Driver");
			
			Connection connection = null;
			
			String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+databaseUser+"_jdbc";
			
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
			
			Statement s = connection.createStatement();
			
			//read data from file
			String fileName = DATA_FILE;
			File file = new File(fileName);
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine()) {
				
				String line = scanner.nextLine();
				
				String[] tokens = line.split(regex);
				
				String english = tokens[0];
				String spanish = tokens[1];
				
				String englishChanged = english.replace("'", "''");
				String spanishChanged = spanish.replace("'", "''");
				
				String sql = "INSERT INTO english_spanish VALUES ('"+ englishChanged+"', '" + spanishChanged + "')";
				s.executeUpdate(sql);
			}
			
			connection.close();
			scanner.close();
			
		} catch(Exception e) {
			
			System.out.println("Population Error: " + e.toString());
            res = false;
		}
		
		return res;
	}
	
	public static void main(String[] args) {
		
		TranslatorDBPopulator populator = new TranslatorDBPopulator();
		
		if (populator.populateDB()) {
			
			System.out.println("DB population successful");
		} else {
			
			System.out.println("DB population failed");
		}
	}

}
