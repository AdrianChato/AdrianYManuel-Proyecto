package dam.primero.dao;

import dam.primero.modelos.*;
import dam.primero.dao.JdbcDao;

import java.sql.*;
import java.util.*;

public class PuntuacionDAO {

	private final JdbcDao jdbc;

    public PuntuacionDAO() throws Exception {
        this.jdbc = new JdbcDao();
    }

    public void registrarPuntuacion(Puntuacion p) throws SQLException {
        String sql = "REPLACE INTO Participante_Prueba (ID_Participante, ID_Prueba, Puntuacion) VALUES (?, ?, ?)";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getParticipanteId());
            stmt.setInt(2, p.getPruebaId());
            stmt.setDouble(3, p.getPuntuacion());
            stmt.executeUpdate();
        }
    }

    public List<Puntuacion> listarPuntuacionesPorPrueba(int idPrueba, String mejorPuntuacion) throws SQLException {
        List<Puntuacion> lista = new ArrayList<>();

        String sql;
        if ("MAXIMO".equalsIgnoreCase(mejorPuntuacion)) {
            sql = "SELECT * FROM Participante_Prueba WHERE ID_Prueba = ? ORDER BY Puntuacion DESC LIMIT 1";
        } else if ("MINIMO".equalsIgnoreCase(mejorPuntuacion)) {
            sql = "SELECT * FROM Participante_Prueba WHERE ID_Prueba = ? ORDER BY Puntuacion ASC LIMIT 1";
        } else {
            sql = "SELECT * FROM Participante_Prueba WHERE ID_Prueba = ?";
        }

        try (Connection conn = jdbc.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrueba);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Puntuacion(
                    rs.getInt("ID_Participante"),
                    rs.getInt("ID_Prueba"),
                    rs.getDouble("Puntuacion")
                ));
            }
        }

        return lista;
    }

    public Puntuacion obtenerPuntuacion(int idParticipante, int idPrueba) throws SQLException {
        String sql = "SELECT * FROM Participante_Prueba WHERE ID_Participante = ? AND ID_Prueba = ?";
        try (Connection conn = jdbc.getConnection();
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

    public void actualizarPuntuacion(Puntuacion p) throws SQLException {
        registrarPuntuacion(p); 
    }

    public double calcularMediaParticipante(int idParticipante) throws SQLException {
        String sql = "SELECT AVG(Puntuacion) AS media FROM Participante_Prueba WHERE ID_Participante = ?";
        try (Connection conn = jdbc.getConnection();
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
        String sqlParticipantes = "SELECT ID_Participante FROM Participante WHERE ID_Equipo = ?";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = jdbc.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlParticipantes)) {
            stmt.setInt(1, idEquipo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("ID_Participante"));
            }
        }

        double total = 0;
        int count = 0;
        for (int id : ids) {
            double media = calcularMediaParticipante(id);
            total += media;
            count++;
        }

        double mediaGrupo = count > 0 ? total / count : 0;

        while (count < 5) {
            total += mediaGrupo;
            count++;
        }

        return count > 0 ? total / count : 0;
    }
}