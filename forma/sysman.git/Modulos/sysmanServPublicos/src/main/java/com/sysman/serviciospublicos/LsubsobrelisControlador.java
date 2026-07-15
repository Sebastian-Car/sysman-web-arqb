/*-
 * LsubsobrelisControlador.java
 *
 * 1.0
 *
 * 01/11/2016
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
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LsubsobrelisControladorEnum;
import com.sysman.serviciospublicos.enums.LsubsobrelisControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario lsubsobrelis, el cual sirve para generar
 * reportes de subsidios y sobreprecios
 *
 * @version 1.0, 01/11/2016
 * @author NGOMEZ
 * 
 * @author eamaya
 * @version 2.0, 08/06/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class LsubsobrelisControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que identifica el valor del check verSoloResumen
     */
    private boolean verSoloResumen;
    /**
     * Atributo que identifica el valor del check verSepararFijo
     */
    private boolean verSepararFijo;
    /**
     * Atributo que identifica el valor del check indDane
     */
    private boolean indDane;
    /**
     * Atributo que identifica el valor del check totales
     */
    private boolean totales;
    /**
     * Atributo que identifica el valor del check cargoFijo
     */
    private boolean cargoFijo;
    /**
     * Atributo que identifica el valor del check verAseoComponentes
     */
    private boolean verAseoComponentes;
    /**
     * Atributo que identifica el valor del combo cicloInicial
     */
    private String cicloInicial;
    /**
     * Atributo que identifica el valor del combo cicloFinal
     */
    private String cicloFinal;
    /**
     * Atributo que identifica el valor del campo codigoInicial
     */
    private String codigoInicial;
    /**
     * Atributo que identifica el valor del campo codigoFinal
     */
    private String codigoFinal;
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
    /**
     * RegistroDataModel del combo ciclo
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * RegistroDataModel del combo cicloF
     */
    private RegistroDataModelImpl listacmcicloF;

    /**
     * RegistroDataModel del combo Codigo Inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * RegistroDataModel del combo Codigo Final
     */
    private RegistroDataModelImpl listaCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Constante que representa el campo del registro NUMERO
     */

    private final String numeroCons;

    /**
     * Atributo que identifica si el check verSoloResumen esta visible
     */
    private boolean verSoloResumenVisible;
    /**
     * Atributo que identifica si el check verAseoComponentes esta
     * visible
     */
    private boolean verAseoComponentesVisible;
    /**
     * Atributo que identifica si el check indDaneVisible esta visible
     */
    private boolean indDaneVisible;

    /**
     * Atributo que identifica si el boton PDF esta visible
     */
    private boolean pdfVisible;

    /**
     * Constante que representa la cadena FIjo
     */
    private final String fijoCons;

    /**
     * Constante que representa la cadena Consumo
     */
    private final String consumoCons;

    /**
     * Constante que representa la cadena Sin Medicion
     */
    private final String sinMedicionCons;

    /**
     * Constante que representa la cadena Total
     */
    private final String totalCons;

    /**
     * Constante que representa la cadena SUBFIJOACU
     */
    private final String subfijoacuCons;

    /**
     * Constante que representa la cadena SUBCONSUMOACU
     */
    private final String subconsumoacuCons;

    /**
     * Constante que representa la cadena SUBSINMEDICACU
     */
    private final String subsinmedicacuCons;

    /**
     * Constante que representa la cadena TOTALSUBACU
     */
    private final String totalsubacuCons;

    /**
     * Constante que representa la cadena SUBFIJOALC
     */
    private final String subfijoalcCons;

    /**
     * Constante que representa la cadena SUBCONSUMOALC
     */
    private final String subconsumoalcCons;

    /**
     * Constante que representa la cadena SUBSINMEDIALC
     */
    private final String subsinmedialcCons;

    /**
     * Constante que representa la cadena TOTALSUBALC
     */
    private final String totalsubalcCons;

    /**
     * Constante que representa la cadena SUBSASEO
     */
    private final String subsaseoCons;

    /**
     * Constante que representa la cadena SUBBARRIDOASEO
     */
    private final String subbarridoaseoCons;

    /**
     * Constante que representa la cadena SUBTRATAFINALASEO
     */
    private final String subtratafinalaseoCons;

    /**
     * Constante que representa la cadena SUBCOMERASEO
     */
    private final String subcomeraseoCons;

    /**
     * Constante que representa la cadena SUBRECOLECASEO
     */
    private final String subrecolecaseoCons;

    /**
     * Constante que representa la cadena SUBTEXECEASEO
     */
    private final String subtexeceaseoCons;

    /**
     * Constante que representa la cadena SOBFIJOACU
     */
    private final String sobfijoacuCons;

    /**
     * Constante que representa la cadena SOBCONSUMOACU
     */
    private final String sobconsumoacuCons;

    /**
     * Constante que representa la cadena SOBSMEDIACU
     */
    private final String sobsmediacuCons;

    /**
     * Constante que representa la cadena TOTALSOBACU
     */
    private final String totalsobacuCons;

    /**
     * Constante que representa la cadena SOBDIJOALCAN
     */
    private final String sobdijoalcanCons;

    /**
     * Constante que representa la cadena SOBCONSUMOALC
     */
    private final String sobconsumoalcCons;

    /**
     * Constante que representa la cadena SOBSINMEDIALC
     */
    private final String sobsinmedialcCons;

    /**
     * Constante que representa la cadena TOTALSOBALC
     */
    private final String totalsobalcCons;

    /**
     * Constante que representa la cadena SOBASEO
     */
    private final String sobaseoCons;

    /**
     * Constante que representa la cadena SOBBARRIDOASEO
     */
    private final String sobbarridoaseoCons;

    /**
     * Constante que representa la cadena SOBTRATAFINALASEO
     */
    private final String sobtratafinalaseoCons;

    /**
     * Constante que representa la cadena SOBCOMERASEO
     */
    private final String sobcomeraseoCons;

    /**
     * Constante que representa la cadena SOBRECOLECASEO
     */
    private final String sobrecolecaseoCons;

    /**
     * Constante que representa la cadena SOBTEXECEASEO
     */
    private final String sobtexeceaseoCons;

    /**
     * Constante que representa la cadena TOTALSUBSIDIOS
     */
    private final String totalsubsidiosCons;

    /**
     * Constante que representa la cadena TOTALSOBRE
     */
    private final String totalsobreCons;

    /**
     * Crea una nueva instancia de LsubsobrelisControlador
     */
    public LsubsobrelisControlador() {
        super();
        compania = SessionUtil.getCompania();
        numeroCons = "NUMERO";
        fijoCons = "Fijo";
        consumoCons = "Consumo";
        sinMedicionCons = "Sin Medici�n";
        totalCons = "Total";

        subfijoacuCons = "SUBFIJOACU";
        subconsumoacuCons = "SUBCONSUMOACU";
        subsinmedicacuCons = "SUBSINMEDICACU";
        totalsubacuCons = "TOTALSUBACU";
        subfijoalcCons = "SUBFIJOALC";
        subconsumoalcCons = "SUBCONSUMOALC";
        subsinmedialcCons = "SUBSINMEDIALC";
        totalsubalcCons = "TOTALSUBALC";
        subsaseoCons = "SUBSASEO";
        subbarridoaseoCons = "SUBBARRIDOASEO";
        subtratafinalaseoCons = "SUBTRATAFINALASEO";
        subcomeraseoCons = "SUBCOMERASEO";
        subrecolecaseoCons = "SUBRECOLECASEO";
        subtexeceaseoCons = "SUBTEXECEASEO";
        sobfijoacuCons = "SOBFIJOACU";
        sobconsumoacuCons = "SOBCONSUMOACU";
        sobsmediacuCons = "SOBSMEDIACU";
        totalsobacuCons = "TOTALSOBACU";
        sobdijoalcanCons = "SOBDIJOALCAN";
        sobconsumoalcCons = "SOBCONSUMOALC";
        sobsinmedialcCons = "SOBSINMEDIALC";
        totalsobalcCons = "TOTALSOBALC";
        sobaseoCons = "SOBASEO";
        sobbarridoaseoCons = "SOBBARRIDOASEO";
        sobtratafinalaseoCons = "SOBTRATAFINALASEO";
        sobcomeraseoCons = "SOBCOMERASEO";
        sobrecolecaseoCons = "SOBRECOLECASEO";
        sobtexeceaseoCons = "SOBTEXECEASEO";
        totalsubsidiosCons = "TOTALSUBSIDIOS";
        totalsobreCons = "TOTALSOBRE";

        try {
            numFormulario = GeneralCodigoFormaEnum.LSUBSOBRELIS_CONTROLADOR
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
        cargarListaCiclo();
        cargarListacmcicloF();
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
        pdfVisible = true;
        try {
            indDaneVisible = indDane = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA CODIGO DANE PARA INFORMES",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobrelisControladorUrlEnum.URL13098
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);
    }

    /**
     *
     * Carga la lista listacmcicloF
     *
     */
    public void cargarListacmcicloF() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobrelisControladorUrlEnum.URL13761
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(LsubsobrelisControladorEnum.PARAM0.getValue(),
                        cicloInicial);

        listacmcicloF = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);

    }

    /**
     * 
     * Carga la lista cargarListaCodigoInicial
     *
     */

    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobrelisControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobrelisControladorEnum.PARAM1.getValue(),
                        cicloInicial);
        param.put(LsubsobrelisControladorEnum.PARAM2.getValue(),
                        cicloFinal);
        // cambiar parametros
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());

    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobrelisControladorUrlEnum.URL003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobrelisControladorEnum.PARAM1.getValue(),
                        cicloInicial);
        param.put(LsubsobrelisControladorEnum.PARAM2.getValue(),
                        cicloFinal);
        param.put(LsubsobrelisControladorEnum.PARAM3.getValue(),
                        codigoInicial);
        // cambiar parametros
        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (verAseoComponentes) {
            generarExcel();
        }
        else {
            determinarReporte(FORMATOS.EXCEL);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        determinarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control VerSepararFijo
     *
     *
     */
    public void cambiarVerSepararFijo() {
        // <CODIGO_DESARROLLADO>
        verAseoComponentesVisible = verSepararFijo;

        if (verAseoComponentesVisible) {
            totales = false;
            cargoFijo = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control totales
     *
     */
    public void cambiartotales() {
        // <CODIGO_DESARROLLADO>
        if (totales) {
            verSepararFijo = false;
            cargoFijo = false;
            verAseoComponentesVisible = false;
            verAseoComponentes = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CargoFijo
     *
     */
    public void cambiarCargoFijo() {
        // <CODIGO_DESARROLLADO>
        if (cargoFijo) {
            verSepararFijo = false;
            totales = false;
            verAseoComponentesVisible = false;
            verAseoComponentes = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control VerAseoComponentes
     *
     */
    public void cambiarVerAseoComponentes() {
        // <CODIGO_DESARROLLADO>
        pdfVisible = !verAseoComponentes;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cicloInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(numeroCons), "")
                        .toString();
        cicloFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(numeroCons), "")
                        .toString();

        codigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get("CODIGOINICIAL"),
                        "").toString();
        codigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get("CODIGOFINAL"),
                        "").toString();

        cargarListaCodigoInicial();
        cargarListacmcicloF();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmcicloF
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmcicloF(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cicloFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(numeroCons), "")
                        .toString();
        codigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get("CODIGOFINAL"),
                        " ").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGORUTA"), "")
                        .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGORUTA"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable verSoloResumen
     *
     * @return verSoloResumen
     */
    public boolean isVerSoloResumen() {
        return verSoloResumen;
    }

    /**
     * Asigna la variable verSoloResumen
     *
     * @param verSoloResumen
     * Variable a asignar en verSoloResumen
     */
    public void setVerSoloResumen(boolean verSoloResumen) {
        this.verSoloResumen = verSoloResumen;
    }

    /**
     * Retorna la variable verSepararFijo
     *
     * @return verSepararFijo
     */
    public boolean isVerSepararFijo() {
        return verSepararFijo;
    }

    /**
     * Asigna la variable verSepararFijo
     *
     * @param verSepararFijo
     * Variable a asignar en verSepararFijo
     */
    public void setVerSepararFijo(boolean verSepararFijo) {
        this.verSepararFijo = verSepararFijo;
    }

    /**
     * Retorna la variable indDane
     *
     * @return indDane
     */
    public boolean isIndDane() {
        return indDane;
    }

    /**
     * Asigna la variable indDane
     *
     * @param indDane
     * Variable a asignar en indDane
     */
    public void setIndDane(boolean indDane) {
        this.indDane = indDane;
    }

    /**
     * Retorna la variable totales
     *
     * @return totales
     */
    public boolean isTotales() {
        return totales;
    }

    /**
     * Asigna la variable totales
     *
     * @param totales
     * Variable a asignar en totales
     */
    public void setTotales(boolean totales) {
        this.totales = totales;
    }

    /**
     * Retorna la variable cargoFijo
     *
     * @return cargoFijo
     */
    public boolean isCargoFijo() {
        return cargoFijo;
    }

    /**
     * Asigna la variable cargoFijo
     *
     * @param cargoFijo
     * Variable a asignar en cargoFijo
     */
    public void setCargoFijo(boolean cargoFijo) {
        this.cargoFijo = cargoFijo;
    }

    /**
     * Retorna la variable verAseoComponentes
     *
     * @return verAseoComponentes
     */
    public boolean isVerAseoComponentes() {
        return verAseoComponentes;
    }

    /**
     * Asigna la variable verAseoComponentes
     *
     * @param verAseoComponentes
     * Variable a asignar en verAseoComponentes
     */
    public void setVerAseoComponentes(boolean verAseoComponentes) {
        this.verAseoComponentes = verAseoComponentes;
    }

    /**
     * Retorna la variable cicloInicial
     *
     * @return cicloInicial
     */
    public String getCicloInicial() {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     *
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     *
     * @return cicloFinal
     */
    public String getCicloFinal() {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     *
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
    }

    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isVerSoloResumenVisible() {
        return verSoloResumenVisible;
    }

    public void setVerSoloResumenVisible(boolean verSoloResumenVisible) {
        this.verSoloResumenVisible = verSoloResumenVisible;
    }

    public boolean isVerAseoComponentesVisible() {
        return verAseoComponentesVisible;
    }

    public void setVerAseoComponentesVisible(
        boolean verAseoComponentesVisible) {
        this.verAseoComponentesVisible = verAseoComponentesVisible;
    }

    public boolean isIndDaneVisible() {
        return indDaneVisible;
    }

    public void setIndDaneVisible(boolean indDaneVisible) {
        this.indDaneVisible = indDaneVisible;
    }

    public boolean isPdfVisible() {
        return pdfVisible;
    }

    public void setPdfVisible(boolean pdfVisible) {
        this.pdfVisible = pdfVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listacmcicloF
     *
     * @return listacmcicloF
     */
    public RegistroDataModelImpl getListacmcicloF() {
        return listacmcicloF;
    }

    /**
     * Asigna la lista listacmcicloF
     *
     * @param listacmcicloF
     * Variable a asignar en listacmcicloF
     */
    public void setListacmcicloF(RegistroDataModelImpl listacmcicloF) {
        this.listacmcicloF = listacmcicloF;
    }

    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Proceso en que se genera el reporte de la pestania uno
     *
     * @param formato
     * Formato en que se desea generar el reporte
     *
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        archivoDescarga = null;
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", cicloInicial);
            reemplazar.put("cicloFinal", cicloFinal);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("soloIndicador", "SI".equals(SysmanFunciones.nvl(

                            ejbSysmanUtil.consultarParametro(compania,
                                            "INFORME DE SUBSIDIOS CON SOLO INDICADOR",
                                            SessionUtil.getModulo(), new Date(),
                                            false)

            ,
                            "NO"))
                                ? " AND (SP_USUARIO.SUBSIDIO<>0 OR SP_USUARIO.SOBREPRECIO<>0) "
                                : "");
            reemplazar.put("calculoSuspendidos",
                            "SI".equals(SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "PERMITE CALCULO SUSPENDIDOS",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            "NO"))
                                                ? " AND SP_USUARIO.ESTADO NOT IN ('R') "
                                                : " AND SP_USUARIO.ESTADO NOT IN ('R','S') ");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_VIGILADOPOR", SessionUtil.getCompaniaIngreso()
                            .getRutaVigiladoPor());
            parametros.put("PR_FORMS_LSUBSOBRE_CICLO", cicloInicial);
            parametros.put("PR_FORMS_LSUBSOBRE_CMCICLOF", cicloFinal);
            parametros.put("PR_DANE", indDane);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMBRE_GERENTE", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE GERENTE",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            ""));
            parametros.put("PR_NOMBRE_FIRMA_INFORME_SUBSIDIOS",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE FIRMA INFORME SUBSIDIOS",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            ""));
            parametros.put("PR_CARGO_FIRMA_INFORME_SUBSIDIOS",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "CARGO FIRMA INFORME SUBSIDIOS",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            ""));

            if ("Comprimidos".equals(reporte)) {
                ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
                String[] nombresArchivos = new String[2];
                String reporteUno = "001199LSubSobreDiscResumenDos";
                String reporteDos = "001197LSubSobreDiscResumen";
                Reporteador.resuelveConsulta(reporteUno,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                salidas[0] = JsfUtil.serializarReporte(
                                reporteUno, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
                Reporteador.resuelveConsulta(reporteDos,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                salidas[1] = JsfUtil.serializarReporte(
                                reporteDos, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

                if (formato == ReportesBean.FORMATOS.PDF) {
                    nombresArchivos[0] = "001199LSubSobreDiscResumenDos.pdf";
                    nombresArchivos[1] = "001197LSubSobreDiscResumen.pdf";
                }
                else {
                    nombresArchivos[0] = "001199LSubSobreDiscResumenDos.xlsx";
                    nombresArchivos[1] = "001197LSubSobreDiscResumen.xlsx";
                }

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas, nombresArchivos);

            }
            else {
                Reporteador.resuelveConsulta(
                                "001207LSubSobreResumenConDane".equals(reporte)
                                    ? "001206LSubSobreDiscResumenConDane"
                                    : reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                archivoDescarga = JsfUtil.exportarStreamed(reporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }

        }
        catch (OutOfMemoryError ex) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1773"));
            Logger.getLogger(LsubsobrelisControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SQLException | JRException | IOException
                        | DRException | SystemException | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + " "
                                + ex.getMessage());
            Logger.getLogger(LsubsobrelisControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Determina el nombre del reporte cuando se maneja DANE
     *
     * @return Nombre de reporte a descargar
     */
    public String determinarReporteDane() {
        archivoDescarga = null;
        String rta;
        if (!verSepararFijo && !indDane) {
            rta = "001200LSubSobreResumen";
        }
        else if (verSepararFijo && indDane) {
            rta = "001206LSubSobreDiscResumenConDane";
        }
        else if (verSepararFijo) {
            rta = "001197LSubSobreDiscResumen";
        }
        else {
            rta = "001207LSubSobreResumenConDane";
        }
        return rta;
    }

    /**
     * Determina el nombre del reporte cuando no se maneja DANE
     *
     * @return Nombre de reporte a descargar
     */
    public String determinarReporteSinDane() {
        archivoDescarga = null;
        String rta;
        if (cargoFijo) {
            rta = "001196LSubSobreCargofijoDiscResumen";
        }
        else if (!verSepararFijo) {
            if (totales) {
                rta = "001201LSubSobreResumenTotales";
            }
            else {
                rta = "001200LSubSobreResumen";
            }
        }
        else {
            rta = "Comprimidos";
        }
        return rta;
    }

    /**
     * Determina si se maneja DANE y genera el informe segun el
     * formato ingresado como parametro
     *
     * @param formato
     */
    public void determinarReporte(FORMATOS formato) {
        archivoDescarga = null;
        try {
            if ("SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA CODIGO DANE PARA INFORMES",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            "NO"))) {

                genInforme(formato, determinarReporteDane());
            }
            else {
                genInforme(formato, determinarReporteSinDane());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Generar Excel
     *
     * @param formato
     */
    public void generarExcel() {
        archivoDescarga = null;
        try (Workbook workbook = new XSSFWorkbook();) {
            String parSubi = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "INFORME DE SUBSIDIOS CON SOLO INDICADOR",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            "NO");

            Sheet sheet = workbook.createSheet("Hoja1");

            Font fontNegrita = workbook.createFont();
            fontNegrita.setFontHeightInPoints((short) 11);
            fontNegrita.setFontName("Calibri");
            fontNegrita.setBold(true);

            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setFontName("Calibri");
            font.setBold(false);

            CellStyle estiloTituloCuadro = workbook.createCellStyle();
            estiloTituloCuadro.setAlignment(CellStyle.ALIGN_CENTER);
            estiloTituloCuadro.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            estiloTituloCuadro.setFont(fontNegrita);

            CellStyle estiloTitulo = workbook.createCellStyle();
            estiloTitulo.setAlignment(CellStyle.ALIGN_CENTER);
            estiloTitulo.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            estiloTitulo.setFont(fontNegrita);

            CellStyle estiloTotales = workbook.createCellStyle();
            estiloTotales.setAlignment(CellStyle.ALIGN_RIGHT);
            estiloTotales.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            estiloTotales.setFont(fontNegrita);
            estiloTotales.setDataFormat(workbook.createDataFormat()
                            .getFormat("#,##0.00"));

            CellStyle estiloTextoTotales = workbook.createCellStyle();
            estiloTextoTotales.setAlignment(CellStyle.ALIGN_LEFT);
            estiloTextoTotales.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            estiloTextoTotales.setFont(fontNegrita);

            CellStyle estiloContenido = workbook.createCellStyle();
            estiloContenido.setAlignment(CellStyle.ALIGN_RIGHT);
            estiloContenido.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            estiloContenido.setFont(font);
            estiloContenido.setDataFormat(workbook.createDataFormat()
                            .getFormat("#,##0.00"));
            estiloContenido.setBorderBottom(CellStyle.BORDER_THIN);
            estiloContenido.setBorderLeft(CellStyle.BORDER_THIN);
            estiloContenido.setBorderRight(CellStyle.BORDER_THIN);
            estiloContenido.setBorderTop(CellStyle.BORDER_THIN);

            CellStyle estiloTextoContenido = workbook.createCellStyle();
            estiloTextoContenido.setAlignment(CellStyle.ALIGN_LEFT);
            estiloTextoContenido
                            .setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            estiloTextoContenido.setFont(font);
            estiloTextoContenido.setBorderBottom(CellStyle.BORDER_THIN);
            estiloTextoContenido.setBorderLeft(CellStyle.BORDER_THIN);
            estiloTextoContenido.setBorderRight(CellStyle.BORDER_THIN);
            estiloTextoContenido.setBorderTop(CellStyle.BORDER_THIN);

            Row fila;
            Cell celda;
            CellRangeAddress reg;

            fila = sheet.createRow(0);
            reg = new CellRangeAddress(0, 0, 0, 31);
            sheet.addMergedRegion(reg);
            celda = fila.createCell(0);
            celda.setCellStyle(estiloTitulo);
            celda.setCellValue(
                            "EMPRESA DE ACUEDUCTO ALCANTARILLADO Y ASEO DE YOPAL");

            fila = sheet.createRow(1);
            reg = new CellRangeAddress(1, 1, 0, 31);
            sheet.addMergedRegion(reg);
            celda = fila.createCell(0);
            celda.setCellStyle(estiloTitulo);
            celda.setCellValue(
                            "Resumen Subsidios y sobreprecios");

            fila = sheet.createRow(2);
            reg = new CellRangeAddress(2, 2, 0, 31);
            sheet.addMergedRegion(reg);
            celda = fila.createCell(0);
            celda.setCellStyle(estiloTitulo);
            celda.setCellValue(
                            "Del ciclo " + cicloInicial + " al " + cicloFinal);

            sheet.createRow(3);
            sheet.createRow(4);
            sheet.createRow(5);

            reg = new CellRangeAddress(4, 5, 0, 0);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(0);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Estrato");
            sheet.autoSizeColumn(0);

            reg = new CellRangeAddress(4, 5, 1, 1);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(1);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Uso");
            sheet.autoSizeColumn(1);

            reg = new CellRangeAddress(4, 4, 2, 5);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(2);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Subsidio Acueducto");

            celda = sheet.getRow(5).createCell(2);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(fijoCons);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(2);

            celda = sheet.getRow(5).createCell(3);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(consumoCons);
            sheet.autoSizeColumn(3);

            celda = sheet.getRow(5).createCell(4);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(sinMedicionCons);
            sheet.autoSizeColumn(4);

            celda = sheet.getRow(5).createCell(5);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(totalCons);
            sheet.autoSizeColumn(5);

            reg = new CellRangeAddress(4, 4, 6, 9);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(6);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Subsidio Alcantarillado");

            celda = sheet.getRow(5).createCell(6);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(fijoCons);
            sheet.autoSizeColumn(6);

            celda = sheet.getRow(5).createCell(7);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(consumoCons);
            sheet.autoSizeColumn(7);

            celda = sheet.getRow(5).createCell(8);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(sinMedicionCons);
            sheet.autoSizeColumn(8);

            celda = sheet.getRow(5).createCell(9);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(totalCons);
            sheet.autoSizeColumn(9);

            reg = new CellRangeAddress(4, 4, 10, 15);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(10);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Subsidio Aseo");

            celda = sheet.getRow(5).createCell(10);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Aseo");
            sheet.autoSizeColumn(10);

            celda = sheet.getRow(5).createCell(11);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Barrido y Limpieza");
            sheet.autoSizeColumn(11);

            celda = sheet.getRow(5).createCell(12);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Tratamiento y Disposicion Final");
            sheet.autoSizeColumn(12);

            celda = sheet.getRow(5).createCell(13);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Comercio y manejo del recaudo");
            sheet.autoSizeColumn(13);

            celda = sheet.getRow(5).createCell(14);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Recoleccion y transorte");
            sheet.autoSizeColumn(14);

            celda = sheet.getRow(5).createCell(15);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Tramo exedente");
            sheet.autoSizeColumn(15);

            reg = new CellRangeAddress(4, 4, 16, 19);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(16);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Sobreprecio Acueducto");

            celda = sheet.getRow(5).createCell(16);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(fijoCons);
            sheet.autoSizeColumn(16);

            celda = sheet.getRow(5).createCell(17);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(consumoCons);
            sheet.autoSizeColumn(17);

            celda = sheet.getRow(5).createCell(18);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(sinMedicionCons);
            sheet.autoSizeColumn(18);

            celda = sheet.getRow(5).createCell(19);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(totalCons);
            sheet.autoSizeColumn(19);

            reg = new CellRangeAddress(4, 4, 20, 23);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(20);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Sobreprecio Alcantarillado");

            celda = sheet.getRow(5).createCell(20);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(fijoCons);
            sheet.autoSizeColumn(20);

            celda = sheet.getRow(5).createCell(21);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(consumoCons);
            sheet.autoSizeColumn(21);

            celda = sheet.getRow(5).createCell(22);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(sinMedicionCons);
            sheet.autoSizeColumn(22);

            celda = sheet.getRow(5).createCell(23);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue(totalCons);
            sheet.autoSizeColumn(23);

            reg = new CellRangeAddress(4, 4, 24, 29);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(24);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Sobreprecio Aseo");

            celda = sheet.getRow(5).createCell(24);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Aseo");
            sheet.autoSizeColumn(24);

            celda = sheet.getRow(5).createCell(25);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Barrido y Limpieza");
            sheet.autoSizeColumn(25);

            celda = sheet.getRow(5).createCell(26);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Tratamiento y Disposicion Final");
            sheet.autoSizeColumn(26);

            celda = sheet.getRow(5).createCell(27);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Comercio y manejo del recaudo");
            sheet.autoSizeColumn(27);

            celda = sheet.getRow(5).createCell(28);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Recoleccion y transorte");
            sheet.autoSizeColumn(28);

            celda = sheet.getRow(5).createCell(29);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Tramo exedente");
            sheet.autoSizeColumn(29);

            reg = new CellRangeAddress(4, 5, 30, 30);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(30);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Total Subsidios");
            sheet.autoSizeColumn(30);

            reg = new CellRangeAddress(4, 5, 31, 31);
            sheet.addMergedRegion(reg);
            celda = sheet.getRow(4).createCell(31);
            celda.setCellStyle(estiloTituloCuadro);
            celda.setCellValue("Total Sobreprecio");
            sheet.autoSizeColumn(31);

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplazar.put("cicloInicial", cicloInicial);
            reemplazar.put("cicloFinal", cicloFinal);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("parSubi", parSubi);

            Map<String, Object> parametro = new TreeMap<>();
            parametro.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            List<Registro> usos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LsubsobrelisControladorUrlEnum.URL4164
                                                                            .getValue())
                                            .getUrl(), parametro));

            int auxFila = 6;
            for (Registro registroUsos : usos) {
                reemplazar.put("uso", registroUsos.getCampos().get("CODIGO"));
                Reporteador.resuelveConsulta("001200LSubSobreResumenXls",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                List<Registro> lista = service
                                .getListado(ConectorPool.ESQUEMA_SYSMAN,
                                                parametros.get("PR_STRSQL")
                                                                .toString());
                for (Registro registro : lista) {
                    fila = sheet.createRow(auxFila);
                    celda = fila.createCell(0);
                    celda.setCellStyle(estiloTextoContenido);
                    celda.setCellValue(
                                    registro.getCampos().get("ESTRATO")
                                                    .toString());
                    celda = fila.createCell(1);
                    celda.setCellStyle(estiloTextoContenido);
                    celda.setCellValue(
                                    registro.getCampos().get("USO").toString());
                    celda = fila.createCell(2);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(subfijoacuCons)
                                                    .toString()));
                    celda = fila.createCell(3);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(subconsumoacuCons)
                                                    .toString()));
                    celda = fila.createCell(4);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(
                                    Double.parseDouble(registro.getCampos()
                                                    .get(subsinmedicacuCons)
                                                    .toString()));
                    celda = fila.createCell(5);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(totalsubacuCons)
                                                    .toString()));
                    celda = fila.createCell(6);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(subfijoalcCons)
                                                    .toString()));
                    celda = fila.createCell(7);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(subconsumoalcCons)
                                                    .toString()));
                    celda = fila.createCell(8);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(subsinmedialcCons)
                                                    .toString()));
                    celda = fila.createCell(9);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(totalsubalcCons)
                                                    .toString()));
                    celda = fila.createCell(10);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(subsaseoCons)
                                                    .toString()));
                    celda = fila.createCell(11);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(
                                    Double.parseDouble(registro.getCampos()
                                                    .get(subbarridoaseoCons)
                                                    .toString()));
                    celda = fila.createCell(12);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(registro.getCampos()
                                    .get(subtratafinalaseoCons)
                                    .toString()));
                    celda = fila.createCell(13);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(subcomeraseoCons)
                                                    .toString()));
                    celda = fila.createCell(14);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(
                                    Double.parseDouble(registro.getCampos()
                                                    .get(subrecolecaseoCons)
                                                    .toString()));
                    celda = fila.createCell(15);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(subtexeceaseoCons)
                                                    .toString()));
                    celda = fila.createCell(16);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobfijoacuCons)
                                                    .toString()));
                    celda = fila.createCell(17);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobconsumoacuCons)
                                                    .toString()));
                    celda = fila.createCell(18);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobsmediacuCons)
                                                    .toString()));
                    celda = fila.createCell(19);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(totalsobacuCons)
                                                    .toString()));
                    celda = fila.createCell(20);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobdijoalcanCons)
                                                    .toString()));
                    celda = fila.createCell(21);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobconsumoalcCons)
                                                    .toString()));
                    celda = fila.createCell(22);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobsinmedialcCons)
                                                    .toString()));
                    celda = fila.createCell(23);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(totalsobalcCons)
                                                    .toString()));
                    celda = fila.createCell(24);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobaseoCons)
                                                    .toString()));
                    celda = fila.createCell(25);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(
                                    Double.parseDouble(registro.getCampos()
                                                    .get(sobbarridoaseoCons)
                                                    .toString()));
                    celda = fila.createCell(26);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(registro.getCampos()
                                    .get(sobtratafinalaseoCons)
                                    .toString()));
                    celda = fila.createCell(27);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobcomeraseoCons)
                                                    .toString()));
                    celda = fila.createCell(28);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(
                                    Double.parseDouble(registro.getCampos()
                                                    .get(sobrecolecaseoCons)
                                                    .toString()));
                    celda = fila.createCell(29);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(sobtexeceaseoCons)
                                                    .toString()));
                    celda = fila.createCell(30);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(
                                    Double.parseDouble(registro.getCampos()
                                                    .get(totalsubsidiosCons)
                                                    .toString()));
                    celda = fila.createCell(31);
                    celda.setCellStyle(estiloContenido);
                    celda.setCellValue(Double.parseDouble(
                                    registro.getCampos().get(totalsobreCons)
                                                    .toString()));
                    auxFila++;
                }

                if (!lista.isEmpty()) {
                    fila = sheet.createRow(auxFila);
                    celda = fila.createCell(1);
                    celda.setCellStyle(estiloTextoTotales);
                    celda.setCellValue("SUBTOTAL");

                    HashMap<String, Object> param = new HashMap<>();

                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);

                    param.put(LsubsobrelisControladorEnum.PARAM1.getValue(),
                                    cicloInicial);

                    param.put(LsubsobrelisControladorEnum.PARAM2.getValue(),
                                    cicloFinal);

                    param.put(LsubsobrelisControladorEnum.PARAM3.getValue(),
                                    codigoInicial);

                    param.put(LsubsobrelisControladorEnum.PARAM4.getValue(),
                                    codigoFinal);

                    param.put(LsubsobrelisControladorEnum.PARAM5.getValue(),
                                    registroUsos.getCampos().get("CODIGO"));

                    param.put(LsubsobrelisControladorEnum.PARAM6.getValue(),
                                    parSubi);

                    Registro totalesSub = null;

                    totalesSub = RegistroConverter
                                    .toRegistro(requestManager
                                                    .get(UrlServiceUtil
                                                                    .getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    LsubsobrelisControladorUrlEnum.URL6969
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                                    param));

                    celda = fila.createCell(2);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(
                                    Double.parseDouble(SysmanFunciones
                                                    .nvl(totalesSub
                                                                    .getCampos()
                                                                    .get(subfijoacuCons),
                                                                    0)
                                                    .toString()));

                    celda = fila.createCell(3);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subconsumoacuCons),
                                                                    0)
                                                    .toString()));

                    celda = fila.createCell(4);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subsinmedicacuCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(5);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(totalsubacuCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(6);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(
                                    Double.parseDouble(SysmanFunciones
                                                    .nvl(totalesSub
                                                                    .getCampos()
                                                                    .get(subfijoalcCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(7);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subconsumoalcCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(8);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subsinmedialcCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(9);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(totalsubalcCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(10);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(
                                    Double.parseDouble(SysmanFunciones
                                                    .nvl(totalesSub
                                                                    .getCampos()
                                                                    .get(subsaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(11);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subbarridoaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(12);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subtratafinalaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(13);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subcomeraseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(14);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subrecolecaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(15);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(subtexeceaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(16);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(
                                    Double.parseDouble(SysmanFunciones
                                                    .nvl(totalesSub
                                                                    .getCampos()
                                                                    .get(sobfijoacuCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(17);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobconsumoacuCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(18);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobsmediacuCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(19);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(totalsobacuCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(20);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobdijoalcanCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(21);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobconsumoalcCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(22);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobsinmedialcCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(23);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(totalsobalcCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(24);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double.parseDouble(SysmanFunciones
                                    .nvl(
                                                    totalesSub.getCampos()
                                                                    .get(sobaseoCons),
                                                    0)
                                    .toString()));
                    celda = fila.createCell(25);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobbarridoaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(26);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobtratafinalaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(27);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobcomeraseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(28);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobrecolecaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(29);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(sobtexeceaseoCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(30);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(Double
                                    .parseDouble(SysmanFunciones
                                                    .nvl(totalesSub.getCampos()
                                                                    .get(totalsubsidiosCons),
                                                                    0)
                                                    .toString()));
                    celda = fila.createCell(31);
                    celda.setCellStyle(estiloTotales);
                    celda.setCellValue(
                                    Double.parseDouble(SysmanFunciones
                                                    .nvl(totalesSub
                                                                    .getCampos()
                                                                    .get(totalsobreCons),
                                                                    0)
                                                    .toString()));

                    auxFila++;

                }

            }

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(LsubsobrelisControladorEnum.PARAM1.getValue(),
                            cicloInicial);

            param.put(LsubsobrelisControladorEnum.PARAM2.getValue(),
                            cicloFinal);

            param.put(LsubsobrelisControladorEnum.PARAM3.getValue(),
                            codigoInicial);

            param.put(LsubsobrelisControladorEnum.PARAM4.getValue(),
                            codigoFinal);

            param.put(LsubsobrelisControladorEnum.PARAM6.getValue(),
                            parSubi);

            Registro totalesF = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            LsubsobrelisControladorUrlEnum.URL6666
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            if (totalesF != null) {
                fila = sheet.createRow(auxFila);
                celda = fila.createCell(1);
                celda.setCellStyle(estiloTextoTotales);
                celda.setCellValue("TOTAL");
                celda = fila.createCell(2);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subfijoacuCons)
                                                .toString()));
                celda = fila.createCell(3);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subconsumoacuCons)
                                                .toString()));
                celda = fila.createCell(4);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subsinmedicacuCons)
                                                .toString()));
                celda = fila.createCell(5);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(totalsubacuCons)
                                                .toString()));
                celda = fila.createCell(6);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subfijoalcCons)
                                                .toString()));
                celda = fila.createCell(7);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subconsumoalcCons)
                                                .toString()));
                celda = fila.createCell(8);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subsinmedialcCons)
                                                .toString()));
                celda = fila.createCell(9);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(totalsubalcCons)
                                                .toString()));
                celda = fila.createCell(10);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subsaseoCons)
                                                .toString()));
                celda = fila.createCell(11);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subbarridoaseoCons)
                                                .toString()));
                celda = fila.createCell(12);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subtratafinalaseoCons)
                                                .toString()));
                celda = fila.createCell(13);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subcomeraseoCons)
                                                .toString()));
                celda = fila.createCell(14);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subrecolecaseoCons)
                                                .toString()));
                celda = fila.createCell(15);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(subtexeceaseoCons)
                                                .toString()));
                celda = fila.createCell(16);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobfijoacuCons)
                                                .toString()));
                celda = fila.createCell(17);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobconsumoacuCons)
                                                .toString()));
                celda = fila.createCell(18);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobsmediacuCons)
                                                .toString()));
                celda = fila.createCell(19);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(totalsobacuCons)
                                                .toString()));
                celda = fila.createCell(20);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobdijoalcanCons)
                                                .toString()));
                celda = fila.createCell(21);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobconsumoalcCons)
                                                .toString()));
                celda = fila.createCell(22);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobsinmedialcCons)
                                                .toString()));
                celda = fila.createCell(23);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(totalsobalcCons)
                                                .toString()));
                celda = fila.createCell(24);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double
                                .parseDouble(totalesF.getCampos()
                                                .get(sobaseoCons)
                                                .toString()));
                celda = fila.createCell(25);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobbarridoaseoCons)
                                                .toString()));
                celda = fila.createCell(26);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobtratafinalaseoCons)
                                                .toString()));
                celda = fila.createCell(27);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobcomeraseoCons)
                                                .toString()));
                celda = fila.createCell(28);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobrecolecaseoCons)
                                                .toString()));
                celda = fila.createCell(29);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(sobtexeceaseoCons)
                                                .toString()));
                celda = fila.createCell(30);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(totalsubsidiosCons)
                                                .toString()));
                celda = fila.createCell(31);
                celda.setCellStyle(estiloTotales);
                celda.setCellValue(Double.parseDouble(
                                totalesF.getCampos().get(totalsobreCons)
                                                .toString()));
            }
            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(fileOut.toByteArray()),
                            "Archivo Salida.xlsx");

        }
        catch (IOException | JRException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

}
