package dam.primero.servlet;

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
		
      System.out.println();

        String pathInfo = request.getServletPath(); // devuelve /principal, /buscar, etc.
        System.out.println("Path info: " + pathInfo);

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equalsIgnoreCase("/principal")) {
            templateEngine.process("principal", context, response.getWriter());

        } else if (pathInfo.equalsIgnoreCase("/listado")) {
            templateEngine.process("listado", context, response.getWriter());

        } else if (pathInfo.equalsIgnoreCase("/modificar")) {
            templateEngine.process("modificar", context, response.getWriter());

        } else if (pathInfo.equalsIgnoreCase("/registrar-participante")) {
            templateEngine.process("registrar-participante", context, response.getWriter());

        } else if (pathInfo.equalsIgnoreCase("/buscar")) {
            // Procesar búsqueda
            try {
                String idParam = request.getParameter("idPrueba");
                String filtro = request.getParameter("filtro");

                if (idParam == null || idParam.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro idPrueba");
                    return;
                }

                int idPrueba = Integer.parseInt(idParam);
                PuntuacionDAO dao = new PuntuacionDAO();
                List<Puntuacion> puntuaciones = dao.listarPuntuacionesPorPrueba(idPrueba, filtro);

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		String pathInfo = request.getPathInfo(); // Ejemplo: /listarUsuarios o null
		System.out.println(pathInfo);
		ServletContext servletContext = getServletContext();
		JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
		IServletWebExchange webExchange = application.buildExchange(request, response);
		WebContext context = new WebContext(webExchange, request.getLocale());

		switch (pathInfo) {
		case "/procesar-prueba":
			// Lógica para listar usuarios
			boolean correcto = altaPrueba(request, response, context);
			if (correcto) {
				context.setVariable("error", false);
				templateEngine.process("index", context, response.getWriter());
			} else {
				context.setVariable("error", true);
				templateEngine.process("login", context, response.getWriter());

			}
			break;
		default:
			// Ruta no reconocida
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no válida: " + path);
		}
	}
	List[] altaPrueba(HttpServletRequest request, HttpServletResponse response, WebContext context)
			throws ServletException, IOException {

		String usuario = request.getParameter("puntuaciones");
		String clave = request.getParameter("listado");
		Puntuacion p = new Puntuacion(0, 0, 0);
		List correcto [];

		try {
			PuntuacionDAO dao = new PuntuacionDAO();
			correcto = dao.registrarPuntuacion(p);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return correcto;
	}
}
