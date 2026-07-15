package sysman.util.consumo.pojo;

import java.util.List;

public class RespuestaCuerpoConsultarAud {
	private List<RespuestaHistoricosAud> datos;
	/**
	 * @return the nominas
	 */
	public List<RespuestaHistoricosAud> getDatos() {
		return datos;
	}
	/**
	 * @param nominas the nominas to set
	 */
	public void setDatos(List<RespuestaHistoricosAud> datos) {
		this.datos = datos;
	}
}
