package dam.primero.dao;

import dam.primero.modelos.Puntuacion;

import java.sql.*;
import java.util.*;

public class PuntuacionDAO extends JdbcDao {
	
	private JdbcDao jdbcDao;  

  
	
    
	
    public PuntuacionDAO() throws Exception {
        super();
    }

    public PuntuacionDAO(JdbcDao jdbcDao) throws Exception {
		super();
		this.jdbcDao = new JdbcDao();
	}

	
    public void registrarPuntuacion(Puntuacion p) throws SQLException {
        String sql = "REPLACE INTO Puntuacion (ID_Participante, ID_Prueba, Puntuacion) VALUES (?, ?, ?)";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getParticipanteId());
            stmt.setInt(2, p.getPruebaId());
            stmt.setDouble(3, p.getPuntuacion());
            stmt.executeUpdate();
        }
    }

    
    public List<Puntuacion> listarPuntuacionesPorPrueba(int idPrueba) throws SQLException {
        List<Puntuacion> lista = new ArrayList<>();

        
        String tipoOrden = "DESC";
        String sqlTipo = "SELECT Mejor_Puntuacion FROM Prueba WHERE ID_Prueba = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlTipo)) {
            stmt.setInt(1, idPrueba);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && "MINIMO".equalsIgnoreCase(rs.getString("Mejor_Puntuacion"))) {
                tipoOrden = "ASC";
            }
        }

        String sql = """
            SELECT 
                p.ID_Participante,
                p.Nombre,
                e.Nombre_Equipo,
                pt.Puntuacion,
                pt.ID_Prueba
            FROM Puntuacion pt
            JOIN Participante p ON pt.ID_Participante = p.ID_Participante
            JOIN Equipo e ON p.ID_Equipo = e.ID_Equipo
            WHERE pt.ID_Prueba = ?
            ORDER BY pt.Puntuacion """ + tipoOrden;

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrueba);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Puntuacion puntuacion = new Puntuacion(
                    rs.getInt("ID_Participante"),
                    rs.getInt("ID_Prueba"),
                    rs.getDouble("Puntuacion")
                );
                puntuacion.setNombreParticipante(rs.getString("Nombre"));
                puntuacion.setNombreEquipo(rs.getString("Nombre_Equipo"));
                lista.add(puntuacion);
            }
        }
        return lista;
    }

    
    public Puntuacion obtenerPuntuacion(int idParticipante, int idPrueba) throws SQLException {
        String sql = "SELECT * FROM Puntuacion WHERE ID_Participante = ? AND ID_Prueba = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParticipante);
            stmt.setInt(2, idPrueba);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Puntuacion(idParticipante, idPrueba, rs.getDouble("Puntuacion"));
            }
        }
        return null;
    }

    
    public double calcularMediaParticipante(int idParticipante) throws SQLException {
        String sql = "SELECT AVG(Puntuacion) AS media FROM Puntuacion WHERE ID_Participante = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParticipante);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("media");
            }
        }
        return 0;
    }

    
    public double calcularMediaEquipo(int idEquipo) throws SQLException {
        List<Integer> idsParticipantes = new ArrayList<>();
        String sqlParticipantes = "SELECT ID_Participante FROM Participante WHERE ID_Equipo = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlParticipantes)) {
            stmt.setInt(1, idEquipo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                idsParticipantes.add(rs.getInt("ID_Participante"));
            }
        }

        double suma = 0;
        int reales = 0;
        for (int id : idsParticipantes) {
            double media = calcularMediaParticipante(id);
            suma += media;
            reales++;
        }

        double mediaReal = reales > 0 ? suma / reales : 0;

        int total = Math.max(reales, 5);
        suma += mediaReal * (5 - reales); 

        return total > 0 ? suma / total : 0;
    }

    
    public List<Puntuacion> buscarPorParticipante(int idParticipante) throws SQLException {
        List<Puntuacion> lista = new ArrayList<>();
        String sql = """
            SELECT pt.ID_Prueba, pt.Puntuacion, p.Nombre_Prueba
            FROM Puntuacion pt
            JOIN Prueba p ON pt.ID_Prueba = p.ID_Prueba
            WHERE pt.ID_Participante = ?
            """;

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParticipante);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Puntuacion p = new Puntuacion(idParticipante, rs.getInt("ID_Prueba"), rs.getDouble("Puntuacion"));
                p.setNombrePrueba(rs.getString("Nombre_Prueba"));
                lista.add(p);
            }
        }
        return lista;
    }
    public Integer obtenerEquipoIdPorNombre(String nombreEquipo) throws SQLException {
        String sql = "SELECT ID_Equipo FROM Equipo WHERE Nombre_Equipo = ?";
        try (Connection c = this.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, nombreEquipo);
            ResultSet rs = st.executeQuery();
            return rs.next() ? rs.getInt("ID_Equipo") : null;
        }
    }

    public Integer obtenerParticipanteIdPorNombre(String nombreParticipante) throws SQLException {
        String sql = "SELECT ID_Participante FROM Participante WHERE Nombre = ?";
        try (Connection c = this.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, nombreParticipante);
            ResultSet rs = st.executeQuery();
            return rs.next() ? rs.getInt("ID_Participante") : null;
        }
    }

    public Integer obtenerPruebaIdPorNombre(String nombrePrueba) throws SQLException {
        String sql = "SELECT ID_Prueba FROM Prueba WHERE Nombre_Prueba = ?";
        try (Connection c = this.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, nombrePrueba);
            ResultSet rs = st.executeQuery();
            return rs.next() ? rs.getInt("ID_Prueba") : null;
        }
    }
    
    public boolean insertarPuntuacion(String nombreParticipante, String apellidoParticipante, int edadParticipante, String nombreEquipo, String nombrePrueba, double puntuacion) {
        try (Connection conn = this.getConnection()) {

            
            String sqlEquipo = "INSERT IGNORE INTO Equipo (Nombre_Equipo) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlEquipo)) {
                stmt.setString(1, nombreEquipo);
                stmt.executeUpdate();
            }

            
            int idEquipo = 0;
            String sqlSelectEquipo = "SELECT ID_Equipo FROM Equipo WHERE Nombre_Equipo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSelectEquipo)) {
                stmt.setString(1, nombreEquipo);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idEquipo = rs.getInt("ID_Equipo");
                    }
                }
            }

            
            String sqlParticipante = "INSERT IGNORE INTO Participante (Nombre, Apellido, Edad, ID_Equipo) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlParticipante)) {
                stmt.setString(1, nombreParticipante);
                stmt.setString(2, apellidoParticipante);
                stmt.setInt(3, edadParticipante);
                stmt.setInt(4, idEquipo);
                stmt.executeUpdate();
            }

            
            int idParticipante = 0;
            String sqlSelectParticipante = "SELECT ID_Participante FROM Participante WHERE Nombre = ? AND Apellido = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSelectParticipante)) {
                stmt.setString(1, nombreParticipante);
                stmt.setString(2, apellidoParticipante);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idParticipante = rs.getInt("ID_Participante");
                    }
                }
            }

            
            String sqlPrueba = "INSERT IGNORE INTO Prueba (Nombre_Prueba) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPrueba)) {
                stmt.setString(1, nombrePrueba);
                stmt.executeUpdate();
            }

            
            int idPrueba = 0;
            String sqlSelectPrueba = "SELECT ID_Prueba FROM Prueba WHERE Nombre_Prueba = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSelectPrueba)) {
                stmt.setString(1, nombrePrueba);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idPrueba = rs.getInt("ID_Prueba");
                    }
                }
            }

            
            String sqlPuntuacion = "INSERT INTO Puntuacion (ID_Participante, ID_Prueba, Puntuacion) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPuntuacion)) {
                stmt.setInt(1, idParticipante);
                stmt.setInt(2, idPrueba);
                stmt.setDouble(3, puntuacion);
                stmt.executeUpdate();
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Puntuacion> listarTodasLasPuntuaciones() {
        List<Puntuacion> lista = new ArrayList<>();

        String sql = "SELECT " +
                     "pa.ID_Participante, " +
                     "CONCAT(pa.Nombre, ' ', pa.Apellido) AS NombreParticipante, " +
                     "e.Nombre_Equipo, " +
                     "pr.ID_Prueba, " +
                     "pr.Nombre_Prueba AS NombrePrueba, " +
                     "pr.Mejor_Puntuacion, " +
                     "pu.Puntuacion " +
                     "FROM Puntuacion pu " +
                     "JOIN Participante pa ON pu.ID_Participante = pa.ID_Participante " +
                     "JOIN Prueba pr ON pu.ID_Prueba = pr.ID_Prueba " +
                     "JOIN Equipo e ON pa.ID_Equipo = e.ID_Equipo " +
                     "ORDER BY pr.ID_Prueba";

        try (Connection con = this.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idParticipante = rs.getInt("ID_Participante");
                String nombreParticipante = rs.getString("NombreParticipante");
                String nombreEquipo = rs.getString("Nombre_Equipo");
                int idPrueba = rs.getInt("ID_Prueba");
                String nombrePrueba = rs.getString("NombrePrueba");
                String mejorPuntuacion = rs.getString("Mejor_Puntuacion");
                double puntuacion = rs.getDouble("Puntuacion");

                
                Puntuacion p = new Puntuacion();
                p.setParticipanteId(idParticipante);
                p.setNombreParticipante(nombreParticipante);
                p.setNombreEquipo(nombreEquipo);
                p.setPruebaId(idPrueba);
                p.setNombrePrueba(nombrePrueba);
                p.setMejorPuntuacion(mejorPuntuacion);
                p.setPuntuacion(puntuacion);

                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
