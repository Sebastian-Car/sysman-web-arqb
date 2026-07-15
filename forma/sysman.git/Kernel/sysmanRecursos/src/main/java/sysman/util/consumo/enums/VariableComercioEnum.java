package sysman.util.consumo.enums;


/**
 * Enumerado para identificar las variables que se actualizan en los procesos al
 * subir datos de deudas a procesos judiciales desde otros modulos
 * 
 * @param name
 */
public enum VariableComercioEnum {

	//Variables Generales
	/**
	 * idCompania
	 */
	IDCOMPANIA("idCompania"),
	/**
	 * nombre
	 */
	NOMBRE("nombre"),
	/**
	 * direccion
	 */
	DIRECCION("direccion"),
	
	/**
	 * tipoIdentificacion
	 */
	TIPOIDENTIFICACION("tipoIdentificacion"),
	/**
	 * telefono
	 */
	TELEFONO("telefono"),
	/**
	 * identificacion
	 */
	IDENTIFICACION("identificacion"),
	/**
	 * vigenciaInicial
	 */
	VIGENCIAINICIAL("vigenciaInicial"),
	/**
	 * vigenciaFinal
	 */
	VIGENCIAFINAL("vigenciaFinal"),
	/**
	 * placaEstablecimiento
	 */
	PLACA_ESTABLECIMIENTO("placaEstablecimiento"),
	/**
	 * razonSocial
	 */
	RAZON_SOCIAL("razonSocial"),
	/**
	 * municipio
	 */
	MUNICIPIO("municipio"),
	/**
	 * departamento
	 */
	DEPARTAMENTO("departamento"),
	
	/**
	 * inicial
	 */
	INICIAL("Inicial"),
	/**
	 * final
	 */
	FINAL("Final"),
	/**
	 * recalcula
	 */
	RECALCULA("recalcula"),
	
	
	;
	private final String name;

	private VariableComercioEnum(String name) {
		this.name = name;
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}