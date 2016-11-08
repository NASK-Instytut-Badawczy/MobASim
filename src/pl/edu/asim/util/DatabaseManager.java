/* file DBTools.java */
package pl.edu.asim.util;

import java.sql.Connection;

public class DatabaseManager {

	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String protocol = "jdbc:derby:";
	private static String name = ASimProperties
			.getProperty("asim.database.name");
	private static String user = ASimProperties
			.getProperty("asim.database.user");
	private static String gUIuser = ASimProperties
			.getProperty("asim.database.gui_user");
	private static String password = ASimProperties
			.getProperty("asim.database.password");
	// private static String home =
	// ASimProperties.getProperty("derby.system.home");

	private static Connection conServer = null;
	private static Connection conModeler = null;

	// private static boolean connected = false;

	private DatabaseManager() {
	}

	private static DatabaseManager INSTANCE;

	/**
	 * @return
	 */
	public static DatabaseManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DatabaseManager();
		// if (connected) {
		// ;
		// } else {
		// INSTANCE.connectDatabase();
		// connected = true;
		// }
		return INSTANCE;
	}

	/*
	 * private static void createDatabase() {
	 * 
	 * try { Class.forName(driver).newInstance();
	 * 
	 * Connection conn = null; Properties props = new Properties();
	 * props.put("user", user); props.put("password", password);
	 * 
	 * conn = DriverManager.getConnection(protocol + name + ";create=true",
	 * props);
	 * 
	 * Statement s = conn.createStatement();
	 * 
	 * s.execute("CREATE SCHEMA ASIM_SIMULATOR");
	 * s.execute("CREATE SCHEMA ASIM_MODELER"); s.close();
	 * 
	 * System.out.println("ASim: Created database " + name);
	 * 
	 * conn.setAutoCommit(false);
	 * 
	 * conn.commit(); conn.close();
	 * System.out.println("Committed transaction and closed connection");
	 * 
	 * } catch (Throwable e) { System.out.println("exception thrown:");
	 * 
	 * if (e instanceof SQLException) { printSQLError((SQLException)e); } else {
	 * e.printStackTrace(); } } }
	 * 
	 * // public Connection getConnection() { // return con; // }
	 * 
	 * public static Connection connectDatabaseServer() {
	 * 
	 * if (conServer != null) { return conServer; }
	 * 
	 * try { Class.forName(driver).newInstance();
	 * 
	 * Properties props = new Properties(); props.put("user", user);
	 * props.put("password", password);
	 * 
	 * if (conServer == null) conServer = DriverManager.getConnection(protocol +
	 * name, props); conServer.setAutoCommit(true); return conServer;
	 * 
	 * } catch (Throwable e) { System.out.println("exception thrown:");
	 * 
	 * if (e instanceof SQLException) { printSQLError((SQLException)e); } else {
	 * e.printStackTrace(); } } return null; }
	 * 
	 * public static Connection connectDatabaseModeler() {
	 * 
	 * if (conModeler != null) { return conModeler; }
	 * 
	 * try { Class.forName(driver).newInstance();
	 * 
	 * Properties props = new Properties(); props.put("user", gUIuser);
	 * props.put("password", password);
	 * 
	 * if (conModeler == null) conModeler = DriverManager.getConnection(protocol
	 * + name, props); conModeler.setAutoCommit(true); return conModeler;
	 * 
	 * } catch (Throwable e) { System.out.println("exception thrown:");
	 * 
	 * if (e instanceof SQLException) { printSQLError((SQLException)e); } else {
	 * e.printStackTrace(); } } return null; }
	 * 
	 * public static void printSQLError(SQLException e) { while (e != null) {
	 * e.printStackTrace(); e = e.getNextException(); } }
	 * 
	 * public static void closeConnection() { try { if(conModeler!=null)
	 * conModeler.close(); if(conServer!=null) conServer.close(); } catch
	 * (Throwable e) { System.out.println("exception thrown:");
	 * 
	 * if (e instanceof SQLException) { printSQLError((SQLException)e); } else {
	 * e.printStackTrace(); } } }
	 * 
	 * public static BigInteger addSimulation(String name, String description,
	 * String address) { BigInteger result = null; try {
	 * 
	 * Statement sInsert = conServer.createStatement();
	 * 
	 * sInsert.executeUpdate(
	 * "insert into SIMULATION(SIMULATOR_NAME,SIMULATOR_DESCRIPTION,SIMULATOR_ADDRESS) values ('"
	 * + name + "','" + description + "','" + address + "')",
	 * Statement.RETURN_GENERATED_KEYS);
	 * 
	 * ResultSet rs = sInsert.getGeneratedKeys(); rs.next(); result = new
	 * BigInteger(rs.getString(1)); sInsert.close();
	 * 
	 * } catch (Throwable e) { System.out.println("exception thrown:");
	 * 
	 * if (e instanceof SQLException) {
	 * DatabaseManager.printSQLError((SQLException)e); } else {
	 * e.printStackTrace(); } } return result; }
	 * 
	 * public void databaseLog(BigInteger simulationId, String fatherName,
	 * String type, java.sql.Timestamp time, String value) { try {
	 * 
	 * Statement sInsert = conServer.createStatement();
	 * 
	 * sInsert.executeUpdate(
	 * "insert into LOGGER(FATHER_NAME,SIMULATION_ID,TYPE,TIME,VALUE) values ('"
	 * + fatherName + "','" + simulationId.intValue() + "','" + type + "','" +
	 * time.toString() + "','" + value + "')", Statement.RETURN_GENERATED_KEYS);
	 * sInsert.close();
	 * 
	 * } catch (Throwable e) { System.out.println("exception thrown:");
	 * 
	 * if (e instanceof SQLException) {
	 * DatabaseManager.printSQLError((SQLException)e); } else {
	 * e.printStackTrace(); } } }
	 * 
	 * public static void main(String[] args) {
	 * DatabaseManager.createDatabase(); }
	 */
}
