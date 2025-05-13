package dam.primero.modelos;

import java.sql.Date;

public class Prueba {

	private int id;
	private String nombre;
	private String descripcion;
	private Date fecha;
	private String mejorPuntuacion;

	public Prueba(int id, String nombre, String descripcion, Date fecha, String mejorPuntuacion) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fecha = fecha;
		this.mejorPuntuacion = mejorPuntuacion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getMejorPuntuacion() {
		return mejorPuntuacion;
	}

	public void setMejorPuntuacion(String mejorPuntuacion) {
		this.mejorPuntuacion = mejorPuntuacion;
	}

}
