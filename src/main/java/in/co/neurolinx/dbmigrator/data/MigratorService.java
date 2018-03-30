package in.co.neurolinx.dbmigrator.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import in.co.neurolinx.dbmigrator.data.entities.Database;
import in.co.neurolinx.dbmigrator.data.entities.Tables;
import in.co.neurolinx.dbmigrator.db.DBHandler;

public class MigratorService implements DatabaseProvider {
	private final static String TAG = MigratorService.class.getSimpleName();
	DBHandler handler;
	Connection con;
	PreparedStatement ps;
	ResultSet rs;
	int count = 0;

	public static String DATABASE_URL = "";
	public static String DATABASE_USERNAME = "";
	public static String DATABSE_PASSWORD = "";

	/**
	 * This method will migrate data from one database to another
	 * 
	 * @param remoteDB
	 * @param remoteTable
	 * @param localDB
	 * @param localTable
	 * @param destinationFields
	 * @return
	 * @throws SQLException
	 */
	public boolean migrateFromLocal(Database remoteDB, Tables remoteTable, Database localDB, Tables localTable,
			Set<String> destinationFields) throws SQLException {
		System.out.println("MigrateToLocal: migrating");

		handler = new DBHandler(DATABASE_URL, DATABASE_USERNAME, DATABSE_PASSWORD);
		con = handler.getConnection();

		StringBuilder parameterBuilder = new StringBuilder();

		// build parameters for query
		for (int k = 0; k < destinationFields.size(); k++) {
			if (k == (destinationFields.size() - 1)) {
				parameterBuilder.append(destinationFields.toArray()[k]);
			} else {
				parameterBuilder.append(destinationFields.toArray()[k] + ",");
			}
		}

		try {
			String migrateToLocalQuery = "insert into " + remoteDB.toString() + "." + remoteTable.toString() + "("
					+ parameterBuilder + ") select " + parameterBuilder + " from " + localDB.toString() + "."
					+ localTable.toString();
			ps = con.prepareStatement(migrateToLocalQuery);

			count = ps.executeUpdate();
			System.out.println("ps " + ps.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBHandler.close(rs, ps);
		}

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Database> databaseNames() {

		handler = new DBHandler(DATABASE_URL, DATABASE_USERNAME, DATABSE_PASSWORD);
		con = handler.getConnection();
		List<Database> databaseList = new ArrayList<>();

		String getDatabaseQuery = "show databases";

		try {
			ps = con.prepareStatement(getDatabaseQuery);
			rs = ps.executeQuery();

			System.out.println(TAG + " databaseNames ps : " + ps.toString());
			while (rs.next()) {
				Database database = new Database();
				database.setName(rs.getString("Database"));

				databaseList.add(database);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHandler.close(rs, ps);
		}
		return databaseList;
	}

	@Override
	public List<Tables> tablesNames(String databaseName) {

		handler = new DBHandler(DATABASE_URL, DATABASE_USERNAME, DATABSE_PASSWORD);
		con = handler.getConnection();
		List<Tables> tablesList = new ArrayList<>();

		try {
			// prepareStatement for setting database
			ps = con.prepareStatement("use " + databaseName);
			rs = ps.executeQuery();

			// query for getting tables from above selected database
			String getTablesQuery = "show tables";
			ps = con.prepareStatement(getTablesQuery);
			rs = ps.executeQuery();

			System.out.println(TAG + " tablesNames ps : " + ps.toString());

			while (rs.next()) {
				Tables tables = new Tables();
				tables.setNames(rs.getString("Tables_in_" + databaseName));
				tablesList.add(tables);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBHandler.close(rs, ps);
		}

		return tablesList;
	}

	@Override
	public List<Tables> tableFields(String databaseName, String tableName) {
		handler = new DBHandler(DATABASE_URL, DATABASE_USERNAME, DATABSE_PASSWORD);
		con = handler.getConnection();
		List<Tables> tableFieldsList = new ArrayList<>();

		String getTableFieldsQuery = "desc " + tableName;

		try {
			// prepareStatement for setting database
			ps = con.prepareStatement("use " + databaseName);
			rs = ps.executeQuery();

			ps = con.prepareStatement(getTableFieldsQuery);
			rs = ps.executeQuery();

			System.out.println(TAG + " tableFields ps : " + ps.toString());
			while (rs.next()) {
				Tables tables = new Tables();
				tables.setFields(rs.getString("Field"));
				tableFieldsList.add(tables);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBHandler.close(rs, ps);
		}
		return tableFieldsList;
	}

}
