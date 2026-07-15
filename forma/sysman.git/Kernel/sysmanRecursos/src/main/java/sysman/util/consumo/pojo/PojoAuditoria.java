package sysman.util.consumo.pojo;

import java.util.Map;
import javax.persistence.EntityManager;

public class PojoAuditoria {
	/**
	 * Codigo del proceso
	 */
	private String codproceso;
	/**
	 * Codigo de la compania
	 */
	private String codCompania;
	/**
	 * Codigo entidad
	 */
	private String codEntidad;
	/**
	 * Usuario que realiza la operacion
	 */
	private String usuario;
	/**
	 * Direccion ip desde donde se ejecuta la accion a auditar
	 */
	private String ip;
	/**
	 * Objeto que permite llevar referencia de los valores auditados a los que se
	 * relaciona
	 */
	private String referencia;
	/**
	 * Nombre del equipo desde donde se ejecuta la accion a auditar
	 */
	private String equipo;
	/**
	 * Accion que se realiza en auditoria (CREAR, BORRAR, ACTUALIZAR)
	 */
	private String accionAuditar;
	/**
	 * Colecccion que permite almacenar por medio de clave valor los objetos a
	 * auditar ejemplo: para auditar el proceso de logueo de usuarios no es
	 * necesario enviar valAnterior ni valActual, basta con registrar los otros
	 * datos. Para el caso de las opciones de menu basta unicamente con registrar en
	 * el valActual el <b>codMenu</b>, la <b>ruta</b>, el <b>modulo</b> y
	 * finalmente para registrar los datos de otros procesos se envia tanto en
	 * valAnterior como valActual el valor del <b>nombre</b> del campo y su
	 * <b>valor</b>.
	 */
	private Map<String, Object> valAnterior;
	private Map<String, Object> valActual;
	/**
	 * Objeto para manejo de entitymanager
	 */
	private EntityManager entidad;
	private EntityManager entidadConsultas;
	/**
	 * Objeto que permite validar si se obtiene datos de valor anterior
	 */
	private boolean asignaValAnterior;
	/**
	 * Objeto que maneja los datos de id de registro de entrada para busqueda
	 */
	private String idReg;
	
	public String getCodproceso() {
		return codproceso;
	}
	public void setCodproceso(String codproceso) {
		this.codproceso = codproceso;
	}
	public String getCodCompania() {
		return codCompania;
	}
	public void setCodCompania(String codCompania) {
		this.codCompania = codCompania;
	}
	public String getCodEntidad() {
		return codEntidad;
	}
	public void setCodEntidad(String codEntidad) {
		this.codEntidad = codEntidad;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getEquipo() {
		return equipo;
	}
	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}
	public String getAccionAuditar() {
		return accionAuditar;
	}
	public void setAccionAuditar(String accionAuditar) {
		this.accionAuditar = accionAuditar;
	}
	public Map<String, Object> getValAnterior() {
		return valAnterior;
	}
	public void setValAnterior(Map<String, Object> valAnterior) {
		this.valAnterior = valAnterior;
	}
	public Map<String, Object> getValActual() {
		return valActual;
	}
	public void setValActual(Map<String, Object> valActual) {
		this.valActual = valActual;
	}
	public EntityManager getEntidad() {
		return entidad;
	}
	public void setEntidad(EntityManager entidad) {
		this.entidad = entidad;
	}
	public EntityManager getEntidadConsultas() {
		return entidadConsultas;
	}
	public void setEntidadConsultas(EntityManager entidadConsultas) {
		this.entidadConsultas = entidadConsultas;
	}
	public boolean isAsignaValAnterior() {
		return asignaValAnterior;
	}
	public void setAsignaValAnterior(boolean asignaValAnterior) {
		this.asignaValAnterior = asignaValAnterior;
	}
	public String getIdReg() {
		return idReg;
	}
	public void setIdReg(String idReg) {
		this.idReg = idReg;
	}
}
