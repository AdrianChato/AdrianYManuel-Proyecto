package dam.primero.modelos;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    public static Connection getConnection(ServletContext context) throws SQLException {
        Properties props = (Properties) context.getAttribute("db.properties");

        if (props == null) {
            throw new SQLException("Configuración de base de datos no encontrada en ServletContext");
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}
