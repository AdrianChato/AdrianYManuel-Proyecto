package dam.primero.modelos;

public class Participante {

	private int id;
	private String nombre;
	private String apellido;
	private int edad;
	private int equipoId;

	public Participante(int id, String nombre, String apellido, int edad, int equipoId) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.edad = edad;
		this.equipoId = equipoId;
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public int getEquipoId() {
		return equipoId;
	}

	public void setEquipoId(int equipoId) {
		this.equipoId = equipoId;
	}

}
