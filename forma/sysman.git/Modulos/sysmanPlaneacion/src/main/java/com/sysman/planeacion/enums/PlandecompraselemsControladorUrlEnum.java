/*
 * PlandecompraselemsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PlandecompraselemsControladorUrlEnum {

    /**
     * 545002 getSubproyectosPagTodosPorCompaniaQuery
     */
    URL8405("PLANDECOMPRASELEMSCONTROLADORURL8405", "545002"),

    /**
     * 4037 getAnosPagConPlanAprobadoQuery
     */
    URL8943("PLANDECOMPRASELEMSCONTROLADORURL8943", "4037"),

    /**
     * 34020 getFuenterecursosNombrePorCompaniaAnoQuery
     */
    URL9473("PLANDECOMPRASELEMSCONTROLADORURL9473", "34020"),

    /**
     * 71006 getDependenciasresponsablePagTodosPorDependenciaQuery
     */
    URL10308("PLANDECOMPRASELEMSCONTROLADORURL10308", "71006"),

    /**
     * 62058 getDependenciasPagActivasConResponsablesQuery
     */
    URL11422("PLANDECOMPRASELEMSCONTROLADORURL11422", "62058"),

    /**
     * 45044
     * getPlanespresupuestalesPagCuentasConMovimientoPorNaturalezaYAnoQuery
     */
    URL12104("PLANDECOMPRASELEMSCONTROLADORURL12104", "45044"),

    /**
     * 4039 updateAnosPlanAprobado
     */
    URL4269("PLANDECOMPRASELEMSCONTROLADORURL4269", "4039"),

    /**
     * 542005 updatePlandecomprasValorProgramadoRubro
     */
    URL133380("PLANDECOMPRASELEMSCONTROLADORURL133380", "542005"),
    
    /**
     * 34020 getFuenterecursosNombrePorCompaniaAnoCodigoRubroQuery
     */
    URL34064("PLANDECOMPRASELEMSCONTROLADORURL34064", "34064"),
    
    /**
     * 13045 getReferenciaPorFuenteQuery
     */
    URL13045("PLANDECOMPRASELEMSCONTROLADORURL13045", "13045"),
    
    /**
     * 20078 getCentrocostosPagGetccostoporaniorubrofuenterecQuery
     */
    URL20078("PLANDECOMPRASELEMSCONTROLADORURL13045", "20078"),
    
    /**
     * 23056 getAuxiliaresPagGetauxiliaraniorubrofrecursoccostoQuery
     */
    URL23056("PLANDECOMPRASELEMSCONTROLADORURL13045", "23056"),
    /**
     * 45069
     * getPlanespresupuestalesValidarAuxiliaresQuery
     */
    URL45069("PLANDECOMPRASELEMSCONTROLADORURL45069", "45069")
    
    ;

    private final String key;
    private final String value;

    private PlandecompraselemsControladorUrlEnum(String key, String value) {
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
