/*-
 * LecturasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 6/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 07/06/2017
 * @author jrodrigueza
 *
 */
public enum LecturasControladorUrlEnum {
    /**
     * 213152 getSpusuariosLecturasAforadorQuery
     */
    URL44488("LECTURASCONTROLADOR44488", "213152"),
    /**
     * 213153 updateSpusuariosLecturaAforador
     */
    URL44688("LECTURASCONTROLADOR44688", "213153"),
    /**
     * 238002 getSpusuarioproblemaSolucionQuery
     */
    URL43965("LECTURASCONTROLADOR43965", "238002"),
    /**
     * 238003 getSpusuarioproblemaAforoPeriodoAnteriorQuery
     */
    URL53967("LECTURASCONTROLADOR53967", "238003"),
    /**
     * 234006 getSpproblemaPagTodosPorClaseProblemaQuery
     */
    URL52461("LECTURASCONTROLADOR52461", "234006"),
    /**
     * 362003 getSpaforadoresPagPorCompaniaQuery
     */
    URL54263("LECTURASCONTROLADOR54263", "362003"),
    /**
     * 213165 getSpusuariosPagPorCicloYEstadoQuery
     */
    URL54463("LECTURASCONTROLADOR54463", "213165"),
    /**
     * 23800C insertSpusuarioproblema
     */
    URL88966("LECTURASCONTROLADOR88966", "23800C"),
    /**
     * 238004 updateSpusuarioproblemaCambiarProblema
     */
    URL92063("LECTURASCONTROLADOR92063", "238004"),
    /**
     * 23800D deleteSpusuarioproblema
     */
    URL94766("LECTURASCONTROLADOR94766", "23800D"),
    /**
     * 213219 getSpusuariosCodigoDeRutaInicialQuery
     */
    URL365115("LECTURASCONTROLADOR365115", "213219");

    private final String key;
    private final String value;

    private LecturasControladorUrlEnum(String key,
        String value) {
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
