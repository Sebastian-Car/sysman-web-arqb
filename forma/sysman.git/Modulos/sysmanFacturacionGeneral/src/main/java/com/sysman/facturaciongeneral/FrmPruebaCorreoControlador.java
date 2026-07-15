/*-
 * FrmPruebaCorreoControlador.java
 *
 * 1.0
 * 
 * 22/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.RespuestaApi;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Formulario que realiza las pruebas de correo
 *
 * @version 1.0, 22/12/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmPruebaCorreoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String nitCompania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que alamacena el correo de destinatario
     */
    private String destinatario;

    /**
     * Variable que almacena la url del servicio de FRIDA
     */

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmPruebaCorreoControlador
     */
    public FrmPruebaCorreoControlador() {
        super();
        compania = SessionUtil.getCompania();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        try {

            // 2225
            numFormulario = GeneralCodigoFormaEnum.FRM_PRUEBA_CORREO_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
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

        if (nitCompania.contains("-")) {
            int fin = nitCompania.indexOf("-");
            nitCompania = nitCompania.substring(0, fin);
        }

        /*
         * FR2225-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Dim arg As Integer Dim Res As String Dim rs As String Dim
         * strUrl As String Set db = CurrentDb strUrl =
         * Nz(par("URL SERVICIO RES"), "") Me!txtcontribuyente =
         * getNitCompany() '--- Se valida que el parametro este
         * configurado If strUrl = "" Then MsgBox
         * "Asegurese de configurar el parametro URL SERVICIO RES",
         * vbExclamation, "Stefanini Sysman" Exit Sub End If End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Enviar en la vista
     *
     *
     */
    public void oprimirEnviar() {

        try {
            String url = ejbSysmanUtil.consultarParametro(compania,
                            "URL SERVICIO REST", "69", new Date(), false);

            if (SysmanFunciones.validarVariableVacio(url)) {
                JsfUtil.agregarMensajeAlerta(
                                "Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else {
                String respuesta;
                APIFrida api = new APIFrida();

                respuesta = api.postContribuyentePruebaCorreo(nitCompania,
                                url,
                                destinatario);

                Gson gson = new Gson();

                RespuestaApi respuestaApi = gson.fromJson(
                                respuesta,
                                RespuestaApi.class);

                JsfUtil.agregarMensajeAlerta(respuestaApi.getMensaje());

            }

        }
        catch (SystemException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable destinatario
     * 
     * @return destinatario
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Asigna la variable destinatario
     * 
     * @param destinatario
     * Variable a asignar en destinatario
     */
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
