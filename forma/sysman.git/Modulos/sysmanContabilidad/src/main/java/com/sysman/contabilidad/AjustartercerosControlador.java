/*-
 * AjustartercerosControlador.java
 *
 * 1.0
 * 
 * 31/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.AjustarTercerosControladorEnum;
import com.sysman.contabilidad.enums.AjustarTercerosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @version 1.0, 31/08/2017
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class AjustartercerosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AjustartercerosControlador
     */
    public AjustartercerosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.AJUSTARTERCERO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>

        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     * 
     * Este metodo se encarga de ejecutar el DSS 14114 el cual es un
     * Update a la tabla terceros. Este Update, se encarga de disparar
     * un trigger el cual ajugara el nombre y el digito de
     * virificacion de los terceros
     *
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AjustarTercerosControladorUrlEnum.URL4326
                                                        .getValue());
        Map<String, Object> fields = new TreeMap<>();
        fields.put(AjustarTercerosControladorEnum.KEY_COMPANIA.getValue(),
                        compania);

        Parameter parameter = new Parameter();
        parameter.setFields(fields);

        try {
            int actualizadas = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            String mensaje = idioma.getString("TB_TB3503");
            mensaje = mensaje
                            .replace(AjustarTercerosControladorEnum.REEMPLAZOTERCERO
                                            .getValue(),
                                            Integer.toString(actualizadas));
            JsfUtil.agregarMensajeInformativo(mensaje);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
