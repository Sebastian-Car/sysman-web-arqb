package sysman.util.consumo.enums;

/**
 * Enumerado para identificar las variables que se actualizan en los procesos al
 * subir datos de deudas a procesos judiciales desde otros modulos
 * 
 * @param name
 */
public enum VariableDeudaEnum {
	//Variables Generales
	/**
	 * idCompania
	 */
	IDCOMPANIA("idCompania"),
	/**
	 * direccion
	 */
	DIRECCION("direccion"),
	/**
	 * nit
	 */
	NIT("nit"),
	/**
	 * nombre
	 */
	NOMBRE("nombre"),
	/**
	 * vigenciaInicial
	 */
	VIGENCIAINICIAL("vigenciaInicial"),
	/**
	 * vigenciaFinal
	 */
	VIGENCIAFINAL("vigenciaFinal"),
	
	/**
	 * inicial
	 */
	INICIAL("Inicial"),
	/**
	 * final
	 */
	FINAL("Final"),
	
	//Variables de Impuesto Predial
	
	/**
	 * codCatastral
	 */
	CODCATASTRAL("codCatastral"),
	/**
	 * codEquivalente
	 */
	CODEQUIVALENTE("codEquivalente"),
	/**
	 * total
	 */
	TOTAL("total"),
	/**
	 * matriculaInmobiliaria
	 */
	MATRICULAIMOBILIARIA("matriculaInmobiliaria"),
	
	/**
	 * barrio
	 */
	BARRIO("barrio"),
	
	
	/**
	 * barrio
	 */
	TELEFONO("telefono"),
	
	
	// Variables de Impuestos Declarados
	
	
	
	;
	private final String name;

	private VariableDeudaEnum(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
