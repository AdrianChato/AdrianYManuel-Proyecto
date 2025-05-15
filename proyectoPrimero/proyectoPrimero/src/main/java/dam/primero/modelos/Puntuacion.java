package dam.primero.modelos;

public class Puntuacion {

	private int participanteId;
	private int pruebaId;
	private double puntuacion;
	private String nombreEquipo;
	private String nombreParticipante;
	private String nombrePrueba;
	private String mejorPuntuacion;

	public Puntuacion(int participanteId, int pruebaId, double puntuacion) {
		this.participanteId = participanteId;
		this.pruebaId = pruebaId;
		this.puntuacion = puntuacion;
	}

	public Puntuacion() {
		// TODO Auto-generated constructor stub
	}

	
	public String getNombrePrueba() {
		return nombrePrueba;
	}

	public int getParticipanteId() {
		return participanteId;
	}

	public void setParticipanteId(int participanteId) {
		this.participanteId = participanteId;
	}

	public int getPruebaId() {
		return pruebaId;
	}

	public void setPruebaId(int pruebaId) {
		this.pruebaId = pruebaId;
	}

	public double getPuntuacion() {
		return puntuacion;
	}

	public void setPuntuacion(double puntuacion) {
		this.puntuacion = puntuacion;
	}

	public String getNombreEquipo() {
		return nombreEquipo;
	}

	public void setNombreEquipo(String nombreEquipo) {
		this.nombreEquipo = nombreEquipo;
	}
	public String getNombreParticipante() {
	    return nombreParticipante;
	}

	public void setNombreParticipante(String nombreParticipante) {
	    this.nombreParticipante = nombreParticipante;
	}

	public void setNombrePrueba(String nombrePrueba) {
		
		this.nombrePrueba = nombrePrueba;
		
	}

	public String getMejorPuntuacion() {
		return mejorPuntuacion;
	}

	public void setMejorPuntuacion(String mejorPuntuacion) {
		this.mejorPuntuacion = mejorPuntuacion;
	}

	
}
