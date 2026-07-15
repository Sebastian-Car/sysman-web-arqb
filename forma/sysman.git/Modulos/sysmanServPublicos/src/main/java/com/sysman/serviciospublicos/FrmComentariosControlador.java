/*-
 * FrmComentariosControlador.java
 *
 * 1.0
 * 
 * 25/10/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.FrmComentariosControladorEnum;
import com.sysman.serviciospublicos.enums.FrmComentariosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Formulario que permite el ingreso y control de los comentarios en
 * la consulta de facturación.
 *
 * @version 1.0, 25/10/2016
 * @author vmolano
 * 
 * @author eamaya
 * @version 2, 25/05/2017 Cambió de la extension por BeanBaseModal
 * 
 * @author amonroy
 * @version 3, 30/05/2017 Se adiciona el metodo
 * actualizarComentarios() para actualizar los comentarios asociados a
 * un codigo de ruta especifico. Se realiza el proceso de Refactoring
 * al adicionar este metodo
 * 
 */
@ManagedBean
@ViewScoped
public class FrmComentariosControlador extends BeanBaseModal
{
    /**
     * 
     */
    private String comentariosActuales;

    /**
     * 
     */
    private String comentarioAnterior;

    /**
     * 
     */
    private String anoActual;

    /**
     * 
     */
    private String periodoActual;

    /**
     * 
     */
    private Map<String, Object> ridFactura;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmComentariosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmComentariosControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_COMENTARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                comentarioAnterior = SysmanFunciones
                                .nvlStr((String) parametrosEntrada
                                                .get("comentario"), "");
                anoActual = parametrosEntrada.get("ano").toString();
                periodoActual = parametrosEntrada.get("periodo").toString();
                ridFactura = (Map<String, Object>) parametrosEntrada
                                .get("ridFactura");
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {
        abrirFormulario();
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Actualiza el valor del campo "COMENTARIOS" en la informacion de
     * la factura del usuario con los datos diligenciados en este
     * formulario
     */
    private void actualizarComentarios(String comentarios)
    {
        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmComentariosControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> params = new TreeMap<>();
        params.put(FrmComentariosControladorEnum.PARAM0.getValue(),
                        comentarios);
        params.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        params.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        Parameter parameter = new Parameter();
        parameter.setFields(params);

        try
        {
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            params,
                            ridFactura);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btAceptar en la vista
     *
     *
     */
    public void oprimirbtAceptar()
    {
        // <CODIGO_DESARROLLADO>

        if (!"".equals(comentariosActuales))
        {

            String usuario = SessionUtil.getUser().getCodigo();
            String anoPeriodo = anoActual + periodoActual;

            StringBuilder sb = new StringBuilder();

            sb.append(comentarioAnterior);
            sb.append("\n");

            if (!comentarioAnterior.contains(anoPeriodo))
            {
                sb.append(anoPeriodo);
                sb.append("\n");
            }
            try
            {
                sb.append(usuario + "-" + SysmanFunciones.convertirAFechaCadena(
                                new Date(), "dd/MM/YYYY hh:mm:ss a"));
            }
            catch (ParseException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            sb.append("\n");
            sb.append(comentariosActuales);

            RequestContext.getCurrentInstance().closeDialog(sb.toString());
            actualizarComentarios(sb.toString());
        }
        else
        {
            RequestContext.getCurrentInstance().closeDialog("");
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btCancelar en la vista
     *
     *
     */
    public void oprimirbtCancelar()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance()
                        .closeDialog(comentarioAnterior);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public String getComentariosActuales()
    {
        return comentariosActuales;
    }

    public void setComentariosActuales(String comentariosActuales)
    {
        this.comentariosActuales = comentariosActuales;
    }

}
