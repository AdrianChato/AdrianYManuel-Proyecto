package dam.primero.dao;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;


public class JdbcDao {
	private String url;
	private String user;
	private String password;

	public JdbcDao() throws Exception {
		Properties props = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");
		props.load(input);

		url = props.getProperty("db.url");
		user = props.getProperty("db.user");
		password = props.getProperty("db.password");

	}

	
	// Método para establecer la conexión
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

}
