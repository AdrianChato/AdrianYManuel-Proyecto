package dam.primero.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dam.primero.modelos.Usuario;

public class UsuarioDao extends JdbcDao {

	public UsuarioDao() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean validar(String usuario, String clave) {
		boolean resultado = false;

		Connection conn;
		try {
			conn = this.getConnection();

			PreparedStatement stmt = conn.prepareStatement("SELECT clave FROM usuarios WHERE usuario = ?");
			stmt.setString(1, usuario);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				resultado = clave.equals(rs.getString("clave"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultado;
	}

	// Método para obtener la lista de usuarios
	public List<Usuario> obtenerUsuarios() throws SQLException {

		List<Usuario> usuarios = new ArrayList<Usuario>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = this.getConnection(); // Método para obtener la conexión
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT usuario FROM usuarios");

			while (rs.next()) {
				Usuario u = new Usuario();
				u.setNombreUsuario(rs.getString("usuario"));
				usuarios.add(u);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}

		return usuarios; // Devuelve la lista de usuarios
	}

	public Usuario getDetalleUsuario(String nombre) {
		Usuario usuario = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = this.getConnection(); // Método para obtener la conexión
			String query = "SELECT * FROM usuarios WHERE usuario = ?";
			stmt = conn.prepareStatement(query);			   
			stmt.setString(1, nombre);
			rs = stmt.executeQuery();
			if (rs.next()) {
				usuario = new Usuario();
				usuario.setNombreUsuario(rs.getString("usuario"));
				usuario.setClave(rs.getString("clave"));
				usuario.setDireccion(rs.getString("direccion"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return usuario;
	}

}
