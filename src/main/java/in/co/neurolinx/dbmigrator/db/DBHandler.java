package in.co.neurolinx.dbmigrator.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHandler {
	private static DBHandler instance;
	private Connection con;

	public DBHandler(String url,String username,String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); 
			con = DriverManager.getConnection(url,username,password);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

//	public static DBHandler getInstance() {
//		if (instance == null) {
//			instance = new DBHandler();
//		}
//
//		return instance;
//	}

	public Connection getConnection() {
		return con;
	}
	
	public static void close(ResultSet rs, PreparedStatement ps)
	{
	    if (rs!=null)
	    {
	        try
	        {
	            rs.close();

	        }
	        catch(SQLException e)
	        {
	            System.out.println("The result set cannot be closed.");
	        }
	    }
	    if (ps != null)
	    {
	        try
	        {
	            ps.close();
	        } catch (SQLException e)
	        {
	        	System.out.println("The statement cannot be closed.");
	        }
	    }
	}
}
