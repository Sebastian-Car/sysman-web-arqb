/*-
 * MicromedicionhistoriasControlador.java
 *
 * 1.0
 * 
 * 22/11/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoNAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.MicromedicionhistoriasControladorEnum;
import com.sysman.serviciospublicos.enums.MicromedicionhistoriasControladorUrlEnum;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Clase que controla la vista del formulario de hist�ricos de
 * micromedici�n.
 *
 * @version 1.0, 22/11/2016
 * @author vmolano
 * 
 * @version 2.0, 09/06/2017
 * @author asana
 * Se realiza refactoring
 */
@ManagedBean
@ViewScoped
public class MicromedicionhistoriasControlador extends BeanBaseContinuoNAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable que recibe por flash el ciclo del suscriptor actual.
     */
    private String cicloActual;

    /**
     * Variable que recibe por flash el c�digo de ruta del suscriptor
     * actual.
     */
    private String codigoRuta;

    /**
     * Variable que recibe por flash el ano del suscriptor actual.
     */
    private String anoActual;

    /**
     * Variable que recibe por flash el periodo del suscriptor actual.
     */
    private String periodoActual; 
    /**
     * Crea una nueva instancia de MicromedicionhistoriasControlador
     */
    public MicromedicionhistoriasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.MICROMEDICIONHISTORIAS_CONTROLADOR.getCodigo();
            validarPermisos();        

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                cicloActual = parametrosEntrada.get("ciclo").toString();
                codigoRuta = parametrosEntrada.get("codigoRuta").toString();
                anoActual = parametrosEntrada.get("ano").toString();
                periodoActual = parametrosEntrada.get("periodo").toString();
            }      
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

        tabla = MicromedicionhistoriasControladorEnum.TABLA.getValue();
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(MicromedicionhistoriasControladorUrlEnum.URL7826.getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(MicromedicionhistoriasControladorEnum.CICLO.getValue(), cicloActual);
        parametrosListado.put(MicromedicionhistoriasControladorEnum.RUTA.getValue(), codigoRuta);
        parametrosListado.put(MicromedicionhistoriasControladorEnum.ANO.getValue(), anoActual);
        parametrosListado.put(MicromedicionhistoriasControladorEnum.PERIODO.getValue(), periodoActual);
        registro = new Registro();      
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }
}