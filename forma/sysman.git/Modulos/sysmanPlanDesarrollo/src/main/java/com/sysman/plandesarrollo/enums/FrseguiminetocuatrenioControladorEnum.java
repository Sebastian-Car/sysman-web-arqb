/**
 * 
 */
package com.sysman.plandesarrollo.enums;

/**
 * @author dcastiblanco
 *
 */
public enum FrseguiminetocuatrenioControladorEnum {
	
     CONSECUTIVO("CONSECUTIVO"),
	
	INICIATIVA_ESTRATEGICA("INICIATIVA_ESTRATEGICA"),
	
	SECTOR_INVERSION("SECTOR_INVERSION"),
	
	PROGRAMA("PROGRAMA"),
	
	META_PRODUCTO("META_PRODUCTO"),
	
	DEPENDENCIA("DEPENDENCIA"),
	
	DESCRIPCION("DESCRIPCION"),
	
	META("META"),
	
	MET_IND("MET_IND"),
	
	META_FISICA_ESPERADA("META_FISICA_ESPERADA"),
	
	META_FISICA_EJECUTADA("META_FISICA_EJECUTADA"),
	
	PORCENTAJE_EJECUCION("PORCENTAJE_EJECUCION"),
	
	META_FINANCIERA("META_FINANCIERA"),
	
	COMPROMETIDO_VIGENCIA("COMPROMETIDO_VIGENCIA"),
	
	AVANCE_COMPROMETIDO("AVANCE_COMPROMETIDO"),
	
	PAGADO_VIGENCIA("PAGADO_VIGENCIA"),
	
	AVANCE_PAGADO("AVANCE_PAGADO");

	private final String value;

    private FrseguiminetocuatrenioControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}