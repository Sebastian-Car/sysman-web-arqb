/*-
 * RptevaluacionabdControlador.java
 *
 * 1.0
 * 
 * 27/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que ejecuta acciones para para imprimir reportes para
 * evaluacion de desempe�o *
 * 
 * @version 1.0, 27/02/2017
 * @author jcrodriguez
 */
@ManagedBean
@ViewScoped
public class RptevaluacionabdControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable que almacenara la carpeta incial (numero) seleccionada
     * en la listaCarpetaInicial
     */
    private String carpetaInicial;
    /**
     * variable que almacenara la carpeta (numero) final seleccionada
     * en la listaCarpetaFinal
     */
    private String carpetaFinal;
    /**
     * variable que almacena el tipo de formato seleccionado
     */
    private String tipoFormato;
    /**
     * variable que almacena o guarda la cedula inicial del usuario
     */
    private String cedulaInicial;
    /**
     * variable que almacena o guarda la cedula final del usuario
     */
    private String cedulaFinal;
    /**
     * varible que almacena el nombre inicial de la carpeta inicial
     * seleccionada o la cedula inicial seleccionada
     */
    private String nombreCInicial;
    /**
     * varible que almacena el nombre final de la carpeta final
     * seleccionada o la cedula final seleccionada
     */
    private String nombreCFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * muestra un listado de carpetas por usuario
     */
    private RegistroDataModel listaCarpetaInicial;
    /**
     * muestra un listado de carpetas por usuario
     */
    private RegistroDataModel listaCarpetaFinal;
    /**
     * muestra un listado de cedulas por usuario
     */
    private RegistroDataModel listaCedulaInicial;
    /**
     * muestra un listado de cedulas por usuario
     */
    private RegistroDataModel listaCedulaFinal;
    /**
     * variables estaticas o constantes
     */
    private static final String NUMEROCARPETA = "NUMEROCARPETA";
    private static final String NOMBRECOMPLETO = "NOMBRECOMPLETO";
    private static final String NUMERO_DCTO = "NUMERO_DCTO";

    /**
     * Crea una nueva instancia de RptevaluacionabdControlador
     */

    public RptevaluacionabdControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1315;
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
        cargarListaCarpetaInicial();
        cargarListaCarpetaFinal();
        cargarListaCedulaInicial();
        cargarListaCedulaFinal();
        abrirFormulario();
        carpetaFinal = "";
        carpetaInicial = "";
        tipoFormato = "";
        nombreCInicial = "";
        nombreCFinal = "";
        cedulaInicial = "";
        cedulaFinal = "";

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1315-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCarpetaInicial con las columnas numero de
     * carpeta, numero de documento numbre completo del usaurio
     */
    public void cargarListaCarpetaInicial() {
        listaCarpetaInicial = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1315_nuevo:TBCB4321", "SELECT NUMEROCARPETA," +
                            "   NUMERO_DCTO," +
                            "   APELLIDO1 ||' '||APELLIDO2 ||' '|| NOMBRES AS NOMBRECOMPLETO"
                            +
                            "  FROM NAT_DATOS_PERSONALES" +
                            " WHERE NUMEROCARPETA IS NOT NULL " +
                            (!SysmanFunciones
                                            .validarVariableVacio(cedulaInicial)
                                                ? " AND NUMERO_DCTO >='"
                                                    + cedulaInicial + "'"
                                                : "")
                            +
                            " ORDER BY NUMEROCARPETA ",
                        true, NUMEROCARPETA);
    }

    /**
     * 
     * Carga la lista listaCarpetaFinal con las columnas numero de
     * carpeta, numero de documento numbre completo del usaurio
     */
    public void cargarListaCarpetaFinal() {
        listaCarpetaFinal = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1315_nuevo:TBCB4322", "SELECT " +
                            "     NUMEROCARPETA, " +
                            "     NUMERO_DCTO, " +
                            "     APELLIDO1 || ' ' || APELLIDO2 || ' ' || NOMBRES AS NOMBRECOMPLETO "
                            +
                            " FROM " +
                            "     NAT_DATOS_PERSONALES " +
                            " WHERE " +
                            "     NUMEROCARPETA IS Not NULL " +
                            (!SysmanFunciones
                                            .validarVariableVacio(cedulaInicial)
                                                ? " AND NUMERO_DCTO >='"
                                                    + cedulaInicial + "'"
                                                : "")
                            +
                            " ORDER BY " +
                            "     NUMEROCARPETA",
                        true, NUMEROCARPETA);
    }

    /**
     * 
     * Carga la lista listaCedulaInicial con las columnas numero de
     * documento numbre completo del usaurio
     */
    public void cargarListaCedulaInicial() {
        listaCedulaInicial = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1315_nuevo:TBCB4324",
                        "SELECT DISTINCT NUMERO_DCTO," +
                            "   NUMEROCARPETA," +
                            "   (NOMBRES" +
                            "    || ' '" +
                            "   || APELLIDO1" +
                            "  || ' '" +
                            "   || APELLIDO2 ) NOMBRECOMPLETO" +
                            " FROM NAT_DATOS_PERSONALES" +
                            (!SysmanFunciones
                                            .validarVariableVacio(cedulaInicial)
                                                ? " WHERE NUMERO_DCTO >='"
                                                    + cedulaInicial + "'"
                                                : "")
                            +
                            " ORDER BY NUMEROCARPETA",
                        true, NUMERO_DCTO);
    }

    /**
     * 
     * Carga la lista listaCedulaFinal con las columnas numero de
     * documento numbre completo del usaurio
     */
    public void cargarListaCedulaFinal() {
        listaCedulaFinal = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1315_nuevo:TBCB4325",
                        "SELECT DISTINCT NUMERO_DCTO," +
                            "   NUMEROCARPETA," +
                            "   (NOMBRES" +
                            "   || ' '" +
                            "   || APELLIDO1" +
                            "   || ' '" +
                            "   || APELLIDO2 ) NOMBRECOMPLETO" +
                            " FROM NAT_DATOS_PERSONALES" +
                            (!SysmanFunciones.validarVariableVacio(cedulaFinal)
                                ? " WHERE NUMERO_DCTO >='" + cedulaFinal + "'"
                                : "")
                            +
                            " ORDER BY NUMEROCARPETA",
                        true, NUMERO_DCTO);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     */
    public void oprimirPdf() {
        archivoDescarga = null;
        if (validarVacios()) {
            generaReporte(FORMATOS.PDF);
        }
    }

    public void seleccionarReporte(Map<String, Object> reemplazar,
        Map<String, Object> parametros, String reporte, String idiomas) {
        parametros.put("PR_FORMATO", idioma.getString(idiomas)
                        .replace("s$formato$s", tipoFormato));
        parametros.put("PR_REPORTE", reporte);
        reemplazar.put("formato", tipoFormato);
        reemplazar.put("carpetaInicial", carpetaInicial);
        reemplazar.put("carpetaFinal", carpetaFinal);

    }

    private void addParametroRemplazosReporte(Map<String, Object> reemplazar,
        Map<String, Object> parametros) {
        switch (tipoFormato) {
        case "A":
            seleccionarReporte(reemplazar, parametros, "001431RhvEvaluacionA",
                            "TB_TB2868");
            break;
        case "B":
            seleccionarReporte(reemplazar, parametros, "001433RhvEvaluacionB",
                            "TB_TB2868");
            break;
        case "D":
            seleccionarReporte(reemplazar, parametros, "001434RhvEvaluacionD",
                            "TB_TB2872");
            break;

        default:
            break;
        }

    }

    private void generaReporte(FORMATOS formato) {
        String reporte = "";
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            addParametroRemplazosReporte(reemplazar, parametros);
            reporte = parametros.get("PR_REPORTE").toString();
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta("001431RhvEvaluacionA",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarVacios() {
        if (!SysmanFunciones.validarVariableVacio(carpetaInicial)
            || !SysmanFunciones.validarVariableVacio(carpetaFinal)
            || !SysmanFunciones.validarVariableVacio(tipoFormato)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarVacios()) {
            generaReporte(FORMATOS.EXCEL);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        carpetaInicial = (String) registroAux.getCampos().get(NUMEROCARPETA);
        nombreCInicial = (String) registroAux.getCampos().get(NOMBRECOMPLETO);
        cedulaInicial = (String) registroAux.getCampos().get(NUMERO_DCTO);
        cargarListaCedulaInicial();
        carpetaFinal = "";
        nombreCFinal = "";
        cedulaFinal = "";
        cargarListaCarpetaFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        carpetaFinal = (String) registroAux.getCampos().get(NUMEROCARPETA);
        nombreCFinal = (String) registroAux.getCampos().get(NOMBRECOMPLETO);
        cedulaFinal = (String) registroAux.getCampos().get(NUMERO_DCTO);
        cargarListaCedulaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCedulaInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedulaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedulaInicial = (String) registroAux.getCampos().get(NUMERO_DCTO);
        carpetaInicial = (String) registroAux.getCampos().get(NUMEROCARPETA);
        nombreCInicial = (String) registroAux.getCampos().get(NOMBRECOMPLETO);
        cargarListaCarpetaInicial();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCedulaFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedulaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedulaFinal = (String) registroAux.getCampos().get(NUMERO_DCTO);
        carpetaFinal = (String) registroAux.getCampos().get(NUMEROCARPETA);
        nombreCFinal = (String) registroAux.getCampos().get(NOMBRECOMPLETO);
        cargarListaCarpetaFinal();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable carpetaInicial
     * 
     * @return carpetaInicial
     */
    public String getCarpetaInicial() {
        return carpetaInicial;
    }

    /**
     * Asigna la variable carpetaInicial
     * 
     * @param carpetaInicial
     * Variable a asignar en carpetaInicial
     */
    public void setCarpetaInicial(String carpetaInicial) {
        this.carpetaInicial = carpetaInicial;
    }

    /**
     * Retorna la variable carpetaFinal
     * 
     * @return carpetaFinal
     */
    public String getCarpetaFinal() {
        return carpetaFinal;
    }

    /**
     * Asigna la variable carpetaFinal
     * 
     * @param carpetaFinal
     * Variable a asignar en carpetaFinal
     */
    public void setCarpetaFinal(String carpetaFinal) {
        this.carpetaFinal = carpetaFinal;
    }

    /**
     * Retorna la variable tipoFormato
     * 
     * @return tipoFormato
     */
    public String getTipoFormato() {
        return tipoFormato;
    }

    /**
     * Asigna la variable tipoFormato
     * 
     * @param tipoFormato
     * Variable a asignar en tipoFormato
     */
    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    /**
     * Retorna la variable cedulaInicial
     * 
     * @return cedulaInicial
     */
    public String getCedulaInicial() {
        return cedulaInicial;
    }

    /**
     * Asigna la variable cedulaInicial
     * 
     * @param cedulaInicial
     * Variable a asignar en cedulaInicial
     */
    public void setCedulaInicial(String cedulaInicial) {
        this.cedulaInicial = cedulaInicial;
    }

    /**
     * Retorna la variable cedulaFinal
     * 
     * @return cedulaFinal
     */
    public String getCedulaFinal() {
        return cedulaFinal;
    }

    /**
     * Asigna la variable cedulaFinal
     * 
     * @param cedulaFinal
     * Variable a asignar en cedulaFinal
     */
    public void setCedulaFinal(String cedulaFinal) {
        this.cedulaFinal = cedulaFinal;
    }

    /**
     * Retorna la variable nombreCInicial
     * 
     * @return nombreCInicial
     */
    public String getNombreCInicial() {
        return nombreCInicial;
    }

    /**
     * Asigna la variable nombreCInicial
     * 
     * @param nombreCInicial
     * Variable a asignar en nombreCInicial
     */
    public void setNombreCInicial(String nombreCInicial) {
        this.nombreCInicial = nombreCInicial;
    }

    /**
     * Retorna la variable nombreCFinal
     * 
     * @return nombreCFinal
     */
    public String getNombreCFinal() {
        return nombreCFinal;
    }

    /**
     * Asigna la variable nombreCFinal
     * 
     * @param nombreCFinal
     * Variable a asignar en nombreCFinal
     */
    public void setNombreCFinal(String nombreCFinal) {
        this.nombreCFinal = nombreCFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCarpetaInicial
     * 
     * @return listaCarpetaInicial
     */
    public RegistroDataModel getListaCarpetaInicial() {
        return listaCarpetaInicial;
    }

    /**
     * Asigna la lista listaCarpetaInicial
     * 
     * @param listaCarpetaInicial
     * Variable a asignar en listaCarpetaInicial
     */
    public void setListaCarpetaInicial(RegistroDataModel listaCarpetaInicial) {
        this.listaCarpetaInicial = listaCarpetaInicial;
    }

    /**
     * Retorna la lista listaCarpetaFinal
     * 
     * @return listaCarpetaFinal
     */
    public RegistroDataModel getListaCarpetaFinal() {
        return listaCarpetaFinal;
    }

    /**
     * Asigna la lista listaCarpetaFinal
     * 
     * @param listaCarpetaFinal
     * Variable a asignar en listaCarpetaFinal
     */
    public void setListaCarpetaFinal(RegistroDataModel listaCarpetaFinal) {
        this.listaCarpetaFinal = listaCarpetaFinal;
    }

    /**
     * Retorna la lista listaCedulaInicial
     * 
     * @return listaCedulaInicial
     */
    public RegistroDataModel getListaCedulaInicial() {
        return listaCedulaInicial;
    }

    /**
     * Asigna la lista listaCedulaInicial
     * 
     * @param listaCedulaInicial
     * Variable a asignar en listaCedulaInicial
     */
    public void setListaCedulaInicial(RegistroDataModel listaCedulaInicial) {
        this.listaCedulaInicial = listaCedulaInicial;
    }

    /**
     * Retorna la lista listaCedulaFinal
     * 
     * @return listaCedulaFinal
     */
    public RegistroDataModel getListaCedulaFinal() {
        return listaCedulaFinal;
    }

    /**
     * Asigna la lista listaCedulaFinal
     * 
     * @param listaCedulaFinal
     * Variable a asignar en listaCedulaFinal
     */
    public void setListaCedulaFinal(RegistroDataModel listaCedulaFinal) {
        this.listaCedulaFinal = listaCedulaFinal;
    }

    public String getCompania() {
        return compania;
    }

}
