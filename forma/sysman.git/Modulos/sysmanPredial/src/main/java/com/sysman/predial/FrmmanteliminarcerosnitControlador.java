/*-
 * FrmmanteliminarcerosnitControlador.java
 *
 * 1.0
 *
 * 15/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.FrmmanteliminarcerosnitControladorUrlEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * El controlador tiene como proposito eliminar ceros de la derecha
 * del documento IGAC
 *
 * @version 1.0, 15/02/2017
 * @author jcrodriguez
 * @version 2.0, 05/07/2017=>Refactoring y depuracion del controlador
 * @author jcrodriguez
 */
@ManagedBean
@ViewScoped
public class FrmmanteliminarcerosnitControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Crea una nueva instancia de FrmmanteliminarcerosnitControlador
     */
    public FrmmanteliminarcerosnitControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMMANTELIMINARCEROSNIT_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmmanteliminarcerosnitControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
    public void inicializar()
    {
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdAceptar()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        UrlBean url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmmanteliminarcerosnitControladorUrlEnum.URL5323.getValue());
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        param.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
        Parameter parameter = new Parameter();
        parameter.setFields(param);
        try
        {
            int actualizar = requestManager.update(url.getUrl(), url.getMetodo(), parameter);

            if (actualizar > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2831"));
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2832"));
            }
        }
        catch (SystemException e)
        {

            Logger.getLogger(FrmmanteliminarcerosnitControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirCmdCancelar()
    {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
    }

    public String getCompania()
    {
        return compania;
    }

}
