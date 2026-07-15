/*-
 * ConstantesWorkflowEnum.java
 *
 * 1.0
 * 
 * 6/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.session.utl;

/**
 * Enumeracion que permite clasificar cada uno de los parametros de
 * sesion utilizados en el modulo de <code>WORKFLOW</code>.
 * 
 * @version 1.0, 6/06/2018
 * @author pespitia
 *
 */
public enum ConstantesWorkflowEnum {

    PR_ARCHIVODESCARGA("PR_ARCHIVODESCARGA"),

    PR_RID_TRAMITE("PR_RID_TRAMITE"),

    S_DOC_ESTANDAR("S_DOC_ESTANDAR");

    private final String value;

    private ConstantesWorkflowEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
