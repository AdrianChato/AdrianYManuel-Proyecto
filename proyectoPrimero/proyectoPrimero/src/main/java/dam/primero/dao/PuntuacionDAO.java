package dam.primero.dao;

import dam.primero.modelos.Puntuacion;

import java.sql.*;
import java.util.*;

public class PuntuacionDAO extends JdbcDao {
	
	private JdbcDao jdbcDao;  // Instancia de JdbcDao

    // Constructor que pasa JdbcDao
	
    
	
    public PuntuacionDAO() throws Exception {
        super();
    }

    public PuntuacionDAO(JdbcDao jdbcDao) throws Exception {
		super();
		this.jdbcDao = new JdbcDao();
	}

	// Registro o modificación
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

    // Listado y búsqueda por prueba, ordenado dinámicamente
    public List<Puntuacion> listarPuntuacionesPorPrueba(int idPrueba) throws SQLException {
        List<Puntuacion> lista = new ArrayList<>();

        // Obtener si la prueba es MAXIMO o MINIMO
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

    // Obtener una puntuación concreta
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

    // Media de un participante
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

    // Media ajustada del equipo
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
        suma += mediaReal * (5 - reales); // añadir media para los que faltan

        return total > 0 ? suma / total : 0;
    }

    // Buscar puntuaciones de un participante
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
    
    public List<Puntuacion> listarTodasLasPuntuaciones() {
        List<Puntuacion> lista = new ArrayList<>();

        String sql = "SELECT " +
                     "pa.ID_Participante, " +
                     "CONCAT(pa.Nombre, ' ', pa.Apellido) AS NombreParticipante, " +
                     "e.Nombre_Equipo, " +
                     "pr.ID_Prueba, " +
                     "pr.Nombre_Prueba, " +
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
                String nombrePrueba = rs.getString("Nombre_Prueba");
                String mejorPuntuacion = rs.getString("Mejor_Puntuacion");
                double puntuacion = rs.getDouble("Puntuacion");

                // Aquí deberías adaptar a tu clase Puntuacion según su constructor o setters
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
