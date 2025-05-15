package dam.primero.servlet;

import dam.primero.dao.JdbcDao;
import dam.primero.dao.PuntuacionDAO;
import dam.primero.modelos.Puntuacion;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class PuntuacionServlet extends HttpServlet {

    private TemplateEngine templateEngine;

    @Override
	public void init() throws ServletException {
		System.out.println("En init");
		ServletContext servletContext = getServletContext();
		JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
		WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

	}


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("En get");

        ServletContext servletContext = getServletContext();
        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
        IServletWebExchange webExchange = application.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());
        response.setContentType("text/html;charset=UTF-8");

        String pathInfo = request.getServletPath(); 
        System.out.println("Path info: " + pathInfo);

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equalsIgnoreCase("/principal")) {
            templateEngine.process("principal", context, response.getWriter());

        } else if (pathInfo.equalsIgnoreCase("/listado")) {
            try {
                
                PuntuacionDAO dao = new PuntuacionDAO();

                
                List<Puntuacion> puntuaciones = dao.listarTodasLasPuntuaciones();  

                
                context.setVariable("puntuaciones", puntuaciones);

                
                templateEngine.process("listado", context, response.getWriter());
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener las puntuaciones.");
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (pathInfo.equalsIgnoreCase("/modificar")) {
            templateEngine.process("modificar", context, response.getWriter());

        } else if (pathInfo.equalsIgnoreCase("/registrar-participante")) {
            templateEngine.process("registrar-participante", context, response.getWriter());

        } else if (pathInfo.equalsIgnoreCase("/buscar")) {
            
            try {
                String idParam = request.getParameter("idPrueba");
                String filtro = request.getParameter("filtro");

                if (idParam == null || idParam.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro idPrueba");
                    return;
                }

                int idPrueba = Integer.parseInt(idParam);
                PuntuacionDAO dao = new PuntuacionDAO();
                List<Puntuacion> puntuaciones = dao.listarPuntuacionesPorPrueba(idPrueba);

                context.setVariable("puntuaciones", puntuaciones);
                templateEngine.process("listado", context, response.getWriter());

            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al buscar puntuaciones.");
            } catch (Exception e) {
                throw new ServletException("Error inicializando el DAO", e);
            }

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no válida: " + pathInfo);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath(); 
        System.out.println("Servlet path: " + path);

        ServletContext servletContext = getServletContext();
        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
        IServletWebExchange webExchange = application.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        switch (path) {
            case "/procesar-prueba":
                boolean correcto = altaPrueba(request, response, context);
                if (correcto) {
                    response.sendRedirect(request.getContextPath() + "/principal");
                } else {
                    context.setVariable("error", true);
                    templateEngine.process("registrar-participante", context, response.getWriter());
                }
                break;
            	case "/registrar-puntuacion":
                registrarNuevaPuntuacion(request, response);
                break;

            default:
                
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no válida: " + path);
        }
    }
    private boolean altaPrueba(HttpServletRequest request, HttpServletResponse response, WebContext context)
            throws ServletException, IOException {

        String nombreEquipo       = request.getParameter("equipo");
        String nombreParticipante = request.getParameter("participante");
        String nombrePrueba       = request.getParameter("prueba");
        String puntuacionTxt      = request.getParameter("puntuacion");

        try {
            PuntuacionDAO dao = new PuntuacionDAO();

           
            Integer equipoId       = dao.obtenerEquipoIdPorNombre(nombreEquipo);
            Integer participanteId = dao.obtenerParticipanteIdPorNombre(nombreParticipante);
            Integer pruebaId       = dao.obtenerPruebaIdPorNombre(nombrePrueba);

            
            if (equipoId == null || participanteId == null || pruebaId == null) {
                context.setVariable("mensaje", "Equipo, participante o prueba inexistentes.");
                return false;
            }

            
            String sqlCheck = "SELECT 1 FROM Participante WHERE ID_Participante=? AND ID_Equipo=?";
            JdbcDao jdbc = new JdbcDao(); 
            try (Connection c = jdbc.getConnection();  
                 PreparedStatement st = c.prepareStatement(sqlCheck)) {
                st.setInt(1, participanteId);
                st.setInt(2, equipoId);
                if (!st.executeQuery().next()) {
                    context.setVariable("mensaje", "El participante no pertenece al equipo indicado.");
                    return false;
                }
            }

            
            double puntuacion = Double.parseDouble(puntuacionTxt);
            Puntuacion p = new Puntuacion(participanteId, pruebaId, puntuacion);
            dao.registrarPuntuacion(p);

            context.setVariable("mensaje", "¡Puntuación guardada correctamente!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            context.setVariable("mensaje", "Error registrando la puntuación.");
            return false;
        }
    }
    private void registrarNuevaPuntuacion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
        	String apellidoParticipante = request.getParameter("apellidoParticipante");
        	int edadParticipante = Integer.parseInt(request.getParameter("edadParticipante"));
            String nombreParticipante = request.getParameter("nombreParticipante");
            String nombreEquipo = request.getParameter("nombreEquipo");
            String nombrePrueba = request.getParameter("nombrePrueba");
            double puntuacion = Double.parseDouble(request.getParameter("puntuacion"));

            PuntuacionDAO dao = new PuntuacionDAO();  
            boolean exito = dao.insertarPuntuacion(nombreParticipante, apellidoParticipante, edadParticipante, nombreEquipo, nombrePrueba, puntuacion);


            if (exito) {
                response.sendRedirect(request.getContextPath() + "/listado");
            } else {
                response.sendRedirect(request.getContextPath() + "/registrar-participante?error=true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/registrar-participante?error=true");
        }
    }


}
