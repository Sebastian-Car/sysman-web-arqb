/*-
 * FrmFlujoDeEfectivoControlador.java
 *
 * 1.0
 * 
 * 25 abr. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrmFlujoDeEfectivoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera el reporte de flujo de efectivo
 *
 * @version 1.0, 25/04/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmFlujoDeEfectivoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el mes final seleccionado en la vista
     */
    private String mesFinal;
    /**
     * Atribut que almacena el anio seleccioando en la vista
     */
    private String anio;
    /**
     * Atributo que almacena el mes inicial seleccionado en la vista
     */
    private String mesInicial;
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
     * Lista que carga el mes final
     */
    private List<Registro> listaMesFinal;
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;
    /**
     * Lista que carga el mes inical
     */
    private List<Registro> listaMesInicial;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmFlujoDeEfectivoControlador
     */
    public FrmFlujoDeEfectivoControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mesInicial = Integer.toString(SysmanFunciones.mes(new Date()));
        mesFinal = Integer.toString(SysmanFunciones.mes(new Date()));
        try {
            numFormulario = 2061;
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
        cargarListaAnio();
        cargarListaMesInicial();
        cargarListaMesFinal();
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

    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmFlujoDeEfectivoControladorUrlEnum.URL4133
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMesInicial
     *
     */
    public void cargarListaMesInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmFlujoDeEfectivoControladorUrlEnum.URL4626
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMesFinal
     *
     */
    public void cargarListaMesFinal() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put("ANIO", anio);
            param.put("MESINICIAL", mesInicial);

            listaMesFinal = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmFlujoDeEfectivoControladorUrlEnum.URL5078
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton ImprimirExcel en la vista
     *
     *
     */
    public void oprimirImprimirExcel() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    private void generarExcel() {

        Map<String, Object> reemplazos = new TreeMap<>();
        Map<String, Object> parametros = new TreeMap<>();

        reemplazos.put("compania", compania);
        reemplazos.put("anio", anio);
        reemplazos.put("mesInicial", mesInicial);
        reemplazos.put("mesFinal", mesFinal);

        Reporteador.resuelveConsulta("002378FlujoEfectivoGobNar",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos,
                        parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed("002378FlujoEfectivoGobNar",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     */
    public void cambiarAnio() {

        mesInicial = "";
        mesFinal = "";
        cargarListaMesInicial();
    }

    /**
     * Metodo ejecutado al cambiar el control MesInicial
     * 
     */
    public void cambiarMesInicial() {

        cargarListaMesFinal();
        mesFinal = "";

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public String getMesFinal() {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
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
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Asigna el valor ha archivoDescarga
     * 
     * @param archivoDescarga
     * 
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaMesFinal
     * 
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     * 
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }

    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaMesInicial
     * 
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     * 
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
