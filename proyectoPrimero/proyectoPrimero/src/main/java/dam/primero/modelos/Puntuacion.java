package dam.primero.modelos;

public class Puntuacion {

	private int participanteId;
	private int pruebaId;
	private double puntuacion;

	public Puntuacion(int participanteId, int pruebaId, double puntuacion) {
		this.participanteId = participanteId;
		this.pruebaId = pruebaId;
		this.puntuacion = puntuacion;
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
}
