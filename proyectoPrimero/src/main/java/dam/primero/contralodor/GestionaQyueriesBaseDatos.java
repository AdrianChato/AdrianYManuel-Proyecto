package dam.primero.contralodor;

import dam.primero.dao.UsuarioDao;

public class GestionaQyueriesBaseDatos {

	public static void main(String[] args) {

		UsuarioDao dao;
		try {
			dao = new UsuarioDao();
			System.out.println (dao.getDetalleUsuario("pepep"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
