package sysman.util.consumo;

public interface IAuditoria {
	
	org.slf4j.Logger LOGI = org.slf4j.LoggerFactory.getLogger(IAuditoria.class);
	/**
	 * Metodo para ejecutar el procesador de auditoria AuditoriaProcesador. El
	 * contexto del procesador de auditoria es PojoAuditoria. No debe Generar
	 * Excepcion y debe ejecutarse como multitarea ej: consumo minimo del procesador
	 * 
	 * auditoria.setModoMultitarea(true); auditoria.setContexto(pojoAudit);
	 * auditoria.ejecutar();
	 * 
	 */
	public void ejecutarAuditoria();
	/**
	 * Metodo para obtener los datos que deben quedar registrados
	 * como el valor actual en el procesador. Recomendacion: crear campos de clase
	 * para leer estos campos en el metodo ejecutarAuditoria
	 */
	public void obtenerValorActual(Object... datos);
	/**
	 * Metodo para obtener los datos previos al proceso que deben quedar registrados
	 * como el valor anterior en el procesador. Recomendacion: crear campos de clase
	 * para leer estos campos en el metodo ejecutarAuditoria
	 */
	public void obtenerValoresAnteriores(Object... datos);
}
