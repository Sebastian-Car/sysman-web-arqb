package sysman.util.consumo.enums;

public enum EnumParametros {
	
	CODCOMPANIA("CODCOMPANIA"),
	CODENTIDAD("CODENTIDAD"),
	CODPROCESO("CODPROCESO"),
	USUARIO("USUARIO"),
	EQUIPO("EQUIPO"),
	AUDITABLE("auditable"),
	SERV_CONEXION("validarConexionAuditoria"),
	SERV_INSRT_AUDIT("insertarAuditoria"),
	SERV_OBTENER_PROCE("validarProcesoAuditable"),
	SERV_INFORME_AUDIT("informeProcesosAuditados"),
	;

	private final String key;

	private EnumParametros(String key) {
		this.key = key;
	}

	public String getValue() {
		return key;
	}
}
