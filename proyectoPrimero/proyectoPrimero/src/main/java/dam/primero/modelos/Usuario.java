package dam.primero.modelos;

import java.util.Objects;

public class Usuario {
	private String nombreUsuario;
	private String clave;
	private String direccion;

	public Usuario() {
		super();  
		// TODO Auto-generated constructor stub
	}

	public Usuario(String nombreUsuario, String clave, String direccion) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.clave = clave;
		this.direccion = direccion;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Override
	public String toString() {
		return "Usuario [nombreUsuario=" + nombreUsuario + ", clave=" + clave + ", direccion=" + direccion + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(nombreUsuario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(nombreUsuario, other.nombreUsuario);
	}
	
	
}
