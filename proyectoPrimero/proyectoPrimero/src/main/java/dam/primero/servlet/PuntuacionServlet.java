package dam.primero.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import dam.primero.dao.PuntuacionDAO;
import dam.primero.modelos.Puntuacion;

import java.io.IOException;
import java.util.List;

public class PuntuacionServlet extends HttpServlet {

    private PuntuacionDAO dao;

    @Override
    public void init() throws ServletException {
        try {
            dao = new PuntuacionDAO();
        } catch (Exception e) {
            throw new ServletException("Error al inicializar DAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("action");

        if (accion == null) {
            resp.setContentType("text/plain");
            resp.getWriter().println("⚠️ Parámetro 'action' requerido. Usa ?action=listar o ?action=detalle");
            return;
        }

        switch (accion.toLowerCase()) {
            case "detalle":
                verDetalle(req, resp);
                break;
            case "listar":
                listar(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción GET no válida: " + accion);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("action");

        if (accion == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro 'action' requerido.");
            return;
        }

        switch (accion.toLowerCase()) {
            case "registrar":
                registrar(req, resp);
                break;
            case "actualizar":
                actualizar(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción POST no válida: " + accion);
        }
    }

    private void registrar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int idParticipante = Integer.parseInt(req.getParameter("idParticipante"));
            int idPrueba = Integer.parseInt(req.getParameter("idPrueba"));
            double puntuacion = Double.parseDouble(req.getParameter("puntuacion"));

            Puntuacion p = new Puntuacion(idParticipante, idPrueba, puntuacion);
            dao.registrarPuntuacion(p);

            resp.getWriter().println("✅ Puntuación registrada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "❌ Error al registrar puntuación.");
        }
    }

    private void actualizar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int idParticipante = Integer.parseInt(req.getParameter("idParticipante"));
            int idPrueba = Integer.parseInt(req.getParameter("idPrueba"));
            double nuevaPuntuacion = Double.parseDouble(req.getParameter("puntuacion"));

            Puntuacion p = new Puntuacion(idParticipante, idPrueba, nuevaPuntuacion);
            dao.actualizarPuntuacion(p);

            resp.getWriter().println("✅ Puntuación actualizada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "❌ Error al actualizar puntuación.");
        }
    }

    private void verDetalle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int idParticipante = Integer.parseInt(req.getParameter("idParticipante"));
            int idPrueba = Integer.parseInt(req.getParameter("idPrueba"));

            Puntuacion p = dao.obtenerPuntuacion(idParticipante, idPrueba);

            if (p != null) {
                resp.getWriter().println("📋 Detalle → Participante: " + p.getParticipanteId() +
                        " | Prueba: " + p.getPruebaId() +
                        " | Puntuación: " + p.getPuntuacion());
            } else {
                resp.getWriter().println("⚠️ No se encontró puntuación.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "❌ Error al obtener detalle.");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int idPrueba = Integer.parseInt(req.getParameter("idPrueba"));
            String mejor = req.getParameter("mejor"); // puede ser null, "MAXIMO" o "MINIMO"

            List<Puntuacion> lista = dao.listarPuntuacionesPorPrueba(idPrueba, mejor);

            for (Puntuacion p : lista) {
                resp.getWriter().println("👤 Participante: " + p.getParticipanteId() +
                        " | Puntuación: " + p.getPuntuacion());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "❌ Error al listar puntuaciones.");
        }
    }
}
