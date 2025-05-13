package dam.primero.servlet;

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
import java.util.*;

import dam.primero.dao.UsuarioDao;
import dam.primero.modelos.Usuario;

public class MiServlet extends HttpServlet {

	private static final long serialVersionUID = 2051990309999713971L;
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
		// Datos de ejemplo: lista de nombres

		String pathInfo = request.getPathInfo(); // Ejemplo: /listarUsuarios o null
		
		if (pathInfo == null || pathInfo.trim().isEmpty() || pathInfo.trim().equalsIgnoreCase("/login")) {
			// Redirigir a la página de login
			templateEngine.process("login", context, response.getWriter());
		} else {
			// Dividimos por segmentos
		    String[] partes = pathInfo.substring(1).split("/");
		    String accion = partes[0]; // ej: "detalleUsuario"
		    String parametro1 = partes.length > 1 ? partes[1] : null;
		    
		    System.out.println("Servlet invocado. accion: " + accion);

		    switch (accion) {
			case "listarUsuarios":
				List<Usuario> usuarios = this.getListaUsuarios(request, response, context);
				// Guardamos los usuarios para que lo tenga el frontend
				context.setVariable("usuarios", usuarios);
				// Redirigimos a listaUsuarios.html
				templateEngine.process("listaUsuarios", context, response.getWriter());
				break;
				
			case "detalleUsuario":
		        Usuario usuario = this.getDetalleUsuario(parametro1);
		        context.setVariable("usuario", usuario);
		        templateEngine.process("detalleUsuario", context, response.getWriter());
		        break;
		        
			case "index":
				templateEngine.process("index", context, response.getWriter());
				break;
				
			default:
				// Ruta no reconocida
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no válida: " + pathInfo);
			}
		}
	}

	boolean validaUsuarioYClave(HttpServletRequest request, HttpServletResponse response, WebContext context)
			throws ServletException, IOException {

		String usuario = request.getParameter("usuario");
		String clave = request.getParameter("clave");
		boolean correcto = false;

		try {
			UsuarioDao dao = new UsuarioDao();
			correcto = dao.validar(usuario, clave);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return correcto;
	}

	private List<Usuario> getListaUsuarios(HttpServletRequest request, HttpServletResponse response, WebContext context)
			throws ServletException, IOException {
		// Aquí deberías implementar la lógica para obtener la lista de usuarios de la
		// base de datos
		List<Usuario> usuarios = new ArrayList<Usuario>();
		try {
			UsuarioDao dao = new UsuarioDao();
			usuarios = dao.obtenerUsuarios();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return usuarios;
	}
	
	private Usuario getDetalleUsuario(String nombre)
	{
		Usuario u= null;
		try {
			UsuarioDao dao = new UsuarioDao();
			u = dao.getDetalleUsuario(nombre);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;
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
		case "/validaUsuario":
			// Lógica para listar usuarios
			boolean correcto = validaUsuarioYClave(request, response, context);
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
}
