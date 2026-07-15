/*-
 * MedidoresporedadesControlador.java
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
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.MedidoresporedadesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para ejecutar la funcion RevisaMedRepetidos y generar
 * el informe de medidores por edades
 *
 * @version 1.0, 25/10/2016
 * @author ybecerra
 * 
 * @author eamaya
 * @version 2.0, Proceso de Refactoring, Manejo de EJBs y correcciones
 * SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class MedidoresporedadesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa a la aplicacion
     */
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que valida si el DgInconsistencias se hace visible o
     * no en el formulario
     */
    private boolean dialogoInconsistencias;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de MedidoresporedadesControlador
     */
    public MedidoresporedadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.MEDIDORESPOREDADES_CONTROLADOR
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (!revisaMedRepetidos()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1776"));
            dialogoInconsistencias = true;
        }
        else {
            dialogoInconsistencias = false;
            generarInforme(FORMATOS.PDF);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar en la vista
     *
     *
     */
    public void oprimirCancelar() {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
    }
    // </METODOS_BOTONES>

    public boolean revisaMedRepetidos() {
        boolean medidorRepetido = false;
        Registro rsCuentaMedidor;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            rsCuentaMedidor = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            MedidoresporedadesControladorUrlEnum.URL5612
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            medidorRepetido = true;
            if (rsCuentaMedidor != null) {
                medidorRepetido = false;

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return medidorRepetido;

    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            String reporte = "001171MedidoresEdades";
            String condicion;

            String param = ejbSysmanUtil.consultarParametro(compania,
                            "EXCLUIR RETIRADOS EN INFORME MEDIDORES POR EDADES",
                            modulo, new Date(), false);

            HashMap<String, Object> reemplazar = new HashMap<>();

            HashMap<String, Object> parametro = new HashMap<>();
            if (param == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1777"));
                dialogoInconsistencias = false;
                return;
            }
            else if ("SI".equals(param)) {
                condicion = " AND NVL(SP_USUARIO.ESTADO,'A') NOT IN('R')";

            }
            else {
                condicion = "";
            }

            reemplazar.put("condicion", condicion);
            if (!revisaMedRepetidos()) {
                Date fechaActual = new Date();

                String nombreExcel = "Repetidos" + SysmanFunciones
                                .convertirAFechaCadena(fechaActual)
                                .replace("/", "")
                    + "_"
                    + SysmanFunciones.convertirAHoraCadena(fechaActual).replace(
                                    ":",
                                    "");

                Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                                reemplazar, parametro);
                ByteArrayInputStream reporteP = JsfUtil.serializarReporte(
                                reporte, parametro,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

                String strSql = Reporteador.resuelveConsulta(
                                "800062MedidoresPorEdades",
                                Integer.parseInt(modulo), reemplazar);
                ByteArrayInputStream excelP = JsfUtil.serializarHojaDatos(
                                strSql, ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL);

                ByteArrayInputStream[] reportes = { reporteP, excelP };
                String[] nombreReportes = { reporte + ".pdf",
                                            nombreExcel + ".xlsx" };

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                reportes, nombreReportes);
            }
            else {
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(modulo), reemplazar,
                                parametro);

                archivoDescarga = JsfUtil.exportarStreamed(reporte,
                                parametro, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
        }
        catch (JRException | IOException | SysmanException | SQLException
                        | DRException | ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control DgInconsistencias
     *
     *
     */
    public void cambiarDgInconsistencias() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DgInconsistencias en la vista
     *
     *
     */
    public void aceptarDgInconsistencias() {
        // <CODIGO_DESARROLLADO>

        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * DgInconsistencias en la vista
     *
     *
     */
    public void cancelarDgInconsistencias() {
        // <CODIGO_DESARROLLADO>
        dialogoInconsistencias = false;

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isDialogoInconsistencias() {
        return dialogoInconsistencias;
    }

    public void setDialogoInconsistencias(boolean dialogoInconsistencias) {
        this.dialogoInconsistencias = dialogoInconsistencias;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
