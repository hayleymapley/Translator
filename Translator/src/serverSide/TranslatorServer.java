package serverSide;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class TranslatorServer {

	/**
	 * Runs the server
	 */
	public static void main(String[] args) throws IOException {

		int port = 9090;
		ServerSocket listener = new ServerSocket(port);
		System.out.println("----- Server started on " + port + " -----");

		try {
			while (true) {
				Socket socket = listener.accept();
				try {
					BufferedReader in =
							new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String token = in.readLine();

					PrintWriter out = 
							new PrintWriter(socket.getOutputStream(), true);
					System.out.println("A new client request has been received at " + socket);

					if (checkWordExists(token.toLowerCase())) {
						
						ArrayList<String> translation = translate(token.toLowerCase());
						
						for (String s : translation) {
							out.println(s);
						}
						
					} else {
						
						out.println(token + " does not exist in the database");
					}
					
				} finally {
					
					socket.close();
				}
			}
			 
		} finally {
			
			listener.close();
		}
	}

	public static boolean checkWordExists(String token) {
		
		boolean res = true;

		try {
			String databaseUser = "mapleyhayl";
			String databaseUserPass = "pass123";

			Class.forName("org.postgresql.Driver");
			Connection connection = null;

			String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+databaseUser+"_jdbc";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);

			Statement s = connection.createStatement();

			ResultSet result = s.executeQuery("SELECT spanish FROM english_spanish WHERE english = '" + token + "'");

			if (!result.next()) {
				res = false;
			}
			result.close();
			connection.close();
		} catch (Exception e) {

			System.out.println("CheckWordExists Error:" + e.toString());
			res = false;
		}

		return res;
	}

	public static ArrayList<String> translate(String token) {
		
		ArrayList<String> result = new ArrayList<>();
		
		try {
			String databaseUser = "mapleyhayl";
			String databaseUserPass = "pass123";

			Class.forName("org.postgresql.Driver");
			Connection connection = null;

			String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+databaseUser+"_jdbc";
			connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);

			Statement s = connection.createStatement();

			ResultSet translation = s.executeQuery("SELECT spanish FROM english_spanish WHERE english = '" + token + "'");

			while (translation.next()) {
				result.add(translation.getString("spanish"));
			}
			
			translation.close();
			connection.close();
			
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
}
