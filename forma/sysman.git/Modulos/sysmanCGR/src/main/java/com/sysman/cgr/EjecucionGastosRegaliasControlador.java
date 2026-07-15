/*-
 * EjecucionGastosRegaliasControlador.java
 *
 * 1.0
 *
 * 18/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.ejb.EjbCGRCeroRemote;
import com.sysman.cgr.enums.EjecucionGastosRegaliasControladorUrlEnum;
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
 * Clase que permite generar el archivo plano de la ejecucion de
 * gastos regalias
 *
 * @version 1.0, 18/08/2017
 * @author jrodriguezr
 */
@ManagedBean
@ViewScoped
public class EjecucionGastosRegaliasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Valor del atributo correspondiente a anticipos
     */
    private boolean anticipos;
    /**
     * Valor del atributo correspondiente a trimestre
     */
    private String trimestre;
    /**
     * Valor del atributo correspondiente a anio
     */
    private String anio;
    /**
     * Valor del atributo correspondiente a codigo Contaduria
     */
    private String codigoContaduria;
    /**
     * Valor del atributo correspondiente a codigo SGR
     */
    private String codigoSGR;
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
     * Lista de objetos pertenecientes al combo anio
     */
    private List<Registro> listaAnoTrabajo;

    @EJB
    private EjbCGRCeroRemote ejbCGRCero;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EjecucionGastosRegaliasControlador
     */
    public EjecucionGastosRegaliasControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoContaduria = SessionUtil.getCompaniaIngreso()
                        .getCodigoContaduria();
        try {
            // 1417
            numFormulario = GeneralCodigoFormaEnum.EJECUCION_GASTOS_REGALIAS_CONTROLADOR
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

    public void mensajesInicioModal() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
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
                                                            EjecucionGastosRegaliasControladorUrlEnum.URL5092
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
     * Metodo ejecutado al oprimir el boton GenerarPlano en la vista
     */
    public void oprimirGenerarPlano() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            archivoDescarga = JsfUtil
                            .getArchivoDescarga(JsfUtil.serializarPlano(
                                            ejbCGRCero.generarPlanoEjecucionGastosRegalias(
                                                            compania,
                                                            Integer.parseInt(
                                                                            anio),
                                                            Integer.parseInt(
                                                                            trimestre),
                                                            codigoContaduria,
                                                            anticipos,
                                                            codigoSGR, false)),
                                            "Ejecucion de Gastos.txt");
        }
        catch (NumberFormatException | JRException | IOException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
     *
     *
     */
    public void oprimirEnviarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String archivoPlano = generarPlano(true);
        String separadorRegistros = System.getProperty("line.separator");
        String separadorColumnas = "\t";
        String nombreHoja = "EJECUCIÓN GASTOS REGALIAS";
        String nombreDocumento = "EJECUCIÓN GASTOS REGALIAS";
        archivoDescarga = JsfUtil.armarExcel(archivoPlano, separadorRegistros,
                        separadorColumnas, nombreHoja, nombreDocumento);

        // </CODIGO_DESARROLLADO>
    }

    private String generarPlano(boolean exportarExcel) {

        try {
            return ejbCGRCero.generarPlanoEjecucionGastosRegalias(compania,
                            Integer.parseInt(anio), Integer.parseInt(trimestre),
                            codigoContaduria, anticipos, codigoSGR,
                            exportarExcel);
        }
        catch (NumberFormatException | SystemException e)

        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
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
     * Retorna la variable anticipos
     *
     * @return anticipos
     */
    public boolean getAnticipos() {
        return anticipos;
    }

    /**
     * Asigna la variable anticipos
     *
     * @param anticipos
     * Variable a asignar en anticipos
     */
    public void setAnticipos(boolean anticipos) {
        this.anticipos = anticipos;
    }

    /**
     * Retorna la variable trimestre
     *
     * @return trimestre
     */
    public String getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     *
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    /**
     * Retorna la variable anio
     *
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     *
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable codigoContaduria
     *
     * @return codigoContaduria
     */
    public String getCodigoContaduria() {
        return codigoContaduria;
    }

    /**
     * Asigna la variable codigoContaduria
     *
     * @param codigoContaduria
     * Variable a asignar en codigoContaduria
     */
    public void setCodigoContaduria(String codigoContaduria) {
        this.codigoContaduria = codigoContaduria;
    }

    /**
     * Retorna la variable codigoSGR
     *
     * @return codigoSGR
     */
    public String getCodigoSGR() {
        return codigoSGR;
    }

    /**
     * Asigna la variable codigoSGR
     *
     * @param codigoSGR
     * Variable a asignar en codigoSGR
     */
    public void setCodigoSGR(String codigoSGR) {
        this.codigoSGR = codigoSGR;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
