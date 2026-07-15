/*-
 * ProgramaciondegastosControlador.java
 *
 * 1.0
 *
 * 17/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.ejb.EjbCGRCeroRemote;
import com.sysman.cgr.enums.ProgramaciondegastosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite generar el plano de la programacion de gastos en
 * CGR
 *
 * @version 1.0, 17/08/2017
 * @author jrodriguezr Se completa la funcionalidad del formulario se
 * realiza el llamado al EJB y Se realiza la refactorizacion a DSS.
 * 
 * @author ybecerra
 * @version 2, 03/10/2017, proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped
public class ProgramaciondegastosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Valor del atributo correspondiente a periodo seleccionado en el
     * formulario
     */
    private String periodo;
    /*
     * 
     */
    private String codigocontaduria;

    /**
     * Valor del atributo correspondiente a anio
     */
    private String anoTrabajo;
    /**
     * Valor del atributo correspondiente al campo Codigo COntaduria
     */
    private String codigoDetalle;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de objetos pertenecientes al combo ano
     */
    private List<Registro> listaAnoTrabajo;

    @EJB
    private EjbCGRCeroRemote ejbCGRCero;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ProgramaciondegastosControlador
     */
    public ProgramaciondegastosControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoDetalle = SessionUtil.getCompaniaIngreso()
                        .getCodigoContaduria();
        try {
            numFormulario = GeneralCodigoFormaEnum.PROGRAMACIONDEGASTOS_CONTROLADOR
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
        cargarListaAnoTrabajo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    public void mensajesInicioModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * Carga la lista listaAnoTrabajo
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramaciondegastosControladorUrlEnum.URL5384
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Generar Plano en la vista
     */
    public void oprimirGenerarPlano() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            archivoDescarga = JsfUtil
                            .getArchivoDescarga(JsfUtil.serializarPlano(
                                            ejbCGRCero.generarPlanoProgramacionGastos(
                                                            compania,
                                                            Integer.parseInt(
                                                                            anoTrabajo),
                                                            Integer.parseInt(
                                                                            periodo),
                                                            codigoDetalle,
                                                            false)),
                                            "Programacion De Gastos.txt");

        }
        catch (NumberFormatException | JRException | IOException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {

            String archivoPlano = ejbCGRCero.generarPlanoProgramacionGastos(
                            compania,
                            Integer.parseInt(
                                            anoTrabajo),
                            Integer.parseInt(
                                            periodo),
                            codigoDetalle,
                            true);

            String separadorRegistros = System.getProperty("line.separator");
            String separadorColumnas = "\t";
            String nombreHoja = "Gastos";
            String nombreDocumento = "Programación de Gastos";
            archivoDescarga = JsfUtil.armarExcel(archivoPlano,
                            separadorRegistros,
                            separadorColumnas, nombreHoja, nombreDocumento);

        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoTrabajo
     */
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable anoTrabajo
     *
     * @return anoTrabajo
     */
    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    /**
     * Asigna la variable anoTrabajo
     *
     * @param anoTrabajo
     * Variable a asignar en anoTrabajo
     */
    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    /**
     * Retorna la variable codigoDetalle
     *
     * @return codigoDetalle
     */
    public String getCodigoDetalle() {
        return codigoDetalle;
    }

    /**
     * Asigna la variable codigoDetalle
     *
     * @param codigoDetalle
     * Variable a asignar en codigoDetalle
     */
    public void setCodigoDetalle(String codigoDetalle) {
        this.codigoDetalle = codigoDetalle;
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

    /**
     * Retorna la lista listaAnoTrabajo
     *
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     *
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * @return the codigocontaduria
     */
    public String getCodigocontaduria() {
        return codigocontaduria;
    }

    /**
     * @param codigocontaduria
     * the codigocontaduria to set
     */
    public void setCodigocontaduria(String codigocontaduria) {
        this.codigocontaduria = codigocontaduria;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
