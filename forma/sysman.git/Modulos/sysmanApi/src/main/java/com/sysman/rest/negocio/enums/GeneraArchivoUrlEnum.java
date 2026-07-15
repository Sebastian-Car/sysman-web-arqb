package com.sysman.rest.negocio.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */

public enum GeneraArchivoUrlEnum {

    /**
     * 1008003 getAutclasesolicitudDatosClaseSolicitudQuery
     */
    URL1008003("CONSULTACLASES", "1008003"),

    /**
     * 210130 getPersonalesIdEmpleadoConHistoricoQuery
     */
    URL210130("CONSULTAEMPLEADOS", "210130"),

    /**
     * 210134 getPersonalesIdEmpleadoAnoConHistoricoQuery
     */
    URL210134("CONSULTAEMPLEADOSANO", "210134"),

    /**
     * 59011 getCompaniasDataAutenticacionQuery
     */
    URL59011("CONSULTACOMPANIA", "59011"),
    /**
     * 104064 getModeloplantillasTodosportipocodigoyfechaQuery
     */
    URL104064("DATOSPLANTILLA", "104064"),

    /**
     * 67002 getParametrosGeneracionListaParametrosQuery
     */
    URL67002("PARAMETRO", "67002"),

    /**
     * 141136 getParametrosGeneracionListaParametrosQuery
     */
    URL141136("DEVOLUTIVO", "141136"),

    /**
     * 14194 getTercerosTodosTercerosQuery
     */
    URL14194("TERCERO", "14194"),

    /**
     * 661065 getSffacturaFacturasWsCajicaQuery
     */
    URL661065("SF_FACTURA", "661065"),

    /**
     * 55005 getCuentabancosCuentaBancoAsobancariaQuery
     */
    URL55005("CUENTABANCOS", "55005"),

    /**
     * 665027 getCuentabancosCuentaBancoAsobancariaQuery
     */
    URL665027("SF_TIPO_COBRO", "665027"),

    /**
     * 72098 getComprobantescntTodosEntreTercerosYTipoQuery
     */
    URL72098("COMPROBANTE_CNT", "72098"),

    /**
     * URL39092 getDetallescomprobantecntEntreTercerosYTipoQuery
     */
    URL39092("DETALLE_COMPROBANTE_CNT", "39092"),

    /**
     * URL72099 insertComprobantescntCorpoboyacaServicio
     */
    URL72099("COMPROBANTE_CNT", "72099"),

    /**
     * URL72100 updateComprobantescntCorpoboyacaServicio
     */
    URL72100("COMPROBANTE_CNT", "72100"),

    /**
     * URL72100 updateComprobantescntCorpoboyacaServicio
     */
    URL7200D("COMPROBANTE_CNT", "7200D"),
    /**
     * URL39093 insertDetallescomprobantecntCorpoboyacaServicio
     */
    URL39093("DETALLE_COMPROBANTE_CNT", "39093"),

    /**
     * URL39094 updateDetallescomprobantecntCorpoboyacaServicio
     */

    URL39094("DETALLE_COMPROBANTE_CNT", "39094"),

    /**
     * URL39094 deleteDetallescomprobantecnt
     */

    URL39099("DETALLE_COMPROBANTE_CNT", "39099"),

    /**
     * URL39094 updateDetallescomprobantecntCorpoboyacaServicio
     */
    URL39095("GENEROS", "192001"),
    /**
     * URL39096 updateDetallescomprobantecntCorpoboyacaServicio
     */
    URL39096("PERIODOS", "471075"),
    /**
     * URL39097 updateDetallescomprobantecntCorpoboyacaServicio
     * <<<<<<< HEAD
     */
    URL39097("PERIODOS", "471076"),
    /**
     * URL39097 updateDetallescomprobantecntCorpoboyacaServicio
     */
    URL39098("TIPOTRAMITES", "997003"),

    /*
     * URL72103 updateComprobantescntCorpoboyacaTextos
     */
    URL72103("COMPROBANTE_CNT", "72103"),

    /**
     * URL72104 updateComprobantescntCorpoboyacaAnulado
     */

    URL72104("COMPROBANTE_CNT", "72104"),
    
    /**
     * URL16213procesadorCargarPlanContable
     */
   URL16213("PLAN_CONTABLE", "16213"),
   
   /**
    * URL16214procesadorCargarPlanContable
    */
  URL16214("PLAN_CONTABLE", "16214"),
	
	
	URL141146("DEVOLUTIVO", "141146"),
	
	/**
	 * URL58004 getAplicacionesRutapormoduloQuery
	 */
	URL58004("APLICACIONES", "58004"),
	
	/**
	 * URL1032005 getTiposTodosIdYNombreQuery
	 */
	URL1032005("TIPOS","1032005"),
	
	/**
     * URL39093 insertDetallescomprobantecntCorpoboyacaServicio
     */
    URL16219("PLAN_CONTABLE", "16219"),
    /**
     * URL661074 getSffacturaGetconceptosconfigfacturaQuery
     */
    URL661074("SF_FACTURA","661074"),
    /**
     * URL6200G getDependenciasPaginaQuery
     */
    URL62111("DEPENDENCIA", "62111"),
    
    /**
     * URL16225 getplancontableclasecuentaye
     */
    URL16225("DEPENDENCIA", "16225"),
    
    /**
     * URL34007 getFuenterecursosTodosPorAnoactualQuery
     */
    URL34007("FUENTE_RECURSOS", "34007"),
    
    /**
     * URL20024 getCentrocostosTodosPorCompaniaAnoActualNombreCodigoQuery
     */
    URL20024("CENTRO_COSTOS","20024"),
    
    /**
     * URL23014 getAuxiliaresTodosPorCompaniaAnoActualNombreCodigoQuery
     */
    URL23014("AUXILIAR","23014"),
    
    /**
     * getReferenciasTodosporanoactualQuery
     */
    URL13049("AUXILIAR","13049"),
    
    /**
     * URL219005 getSaldoauxcontableListsaldoauxcontableQuery
     */
    URL219005("SALDO_AUX_CONTABLE", "219005"),
    /**
     * tercero paginado para arq D
     */
    URL14195("TERCEROSPAG", "14195"),
    
    URL14196("TERCEROSNUM", "14196"),
    /**
     * tercero paginado con nit incial en adelante
     */
    URL14048("TERCEROSPAG", "14048"),
    
    URL14049("TERCEROSNUM", "14049"),
    /**
     * cuentas cartera para arq D
     */
    URL16209("CUENTASCARTERAINICIAL", "16209"),
    
    URL16210("CUENTASCARTERAINICIAL", "16210"),
    /**
     * cuentas cartera final para arq D
     */
    URL16207("CUENTASCARTERAFINAL", "16207"),
    
    URL16208("CUENTASCARTERAFINAL", "16208"),
    
    /**
     * 14121 getTercerosTodosPorNombreQuery
     */
    URL14121("TERCERO", "14121"),
	
	URL665010("FORMATOTIPOCOBRO665010", "665010"),
	
	URL665023("FORMATOTIPOCOBRO665023", "665023"),
	/**
     * 13051 getReferenciasPoraniopaginadoQuery
     */
	URL13051("REFERENCIASPORANIOPAGINADO13051", "13051"),
	/**
     * 20085 getCentrocostosCentrocostoxpaginaQuery
     */
	URL20085("CENTROCOSTOXPAGINA20085", "20085"),
	/**
     * 34075 getFuenterecursosFuenterecursoxpaginaQuery
     */
	URL34075("FUENTERECURSOXPAGINAQUERY340755", "34075"),
	/**
     * 23062 getAuxiliaresAuxiliaresxpaginaQuery
     */
	URL23062("AUXILIARESXPAGINAQUERY23062", "23062");


    private final String key;
    private final String value;

    private GeneraArchivoUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
