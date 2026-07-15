/*-
 * ResumenRecConceptoBancoControlador.java
 *
 * 1.0
 * 
 * 13/12/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.ResumenRecConceptoBancoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma ResumenRecConceptoBanco asociado al
 * formulario "Recaudos por Concepto y Banco".
 *
 * @version 1.0, 14/12/2016
 * @author yrojas
 * 
 * @author eamaya
 * @version 2.0, 15/06/2017, Proceso de Refactoring y cambio de
 * SYSDATE por new Date()
 * 
 */
@ManagedBean
@ViewScoped

public class ResumenRecConceptoBancoControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el c�digo de la
     * compania en la cual inicio sesion el usuario. El valor de esta
     * constante es asignado en el constructor a la variable de sesi�n
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo asociado a la fecha inicial del formulario y que es
     * usado para la generaci�n del reporte correspondiente.
     */
    private Date fechaInicial;

    /**
     * Atributo asociado a la fecha final del formulario y que es
     * usado para la generaci�n del reporte correspondiente.
     */
    private Date fechaFinal;

    /**
     * Atributo asociado al banco inicial del formulario y que es
     * usado para la generaci�n del reporte correspondiente.
     */
    private String bancoInicial;

    /**
     * Atributo asociado al banco final del formulario y que es usado
     * para la generaci�n del reporte correspondiente.
     */
    private String bancoFinal;

    /**
     * Atributo asociado al check del formulario y que es usado para
     * determinar la forma en la que se genera el informe.
     */
    private boolean incluirAbonos;

    /**
     * Atributo asociado a la descarga de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * banco inicial.
     */
    private List<Registro> listacmbBancoInicial;
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * banco final.
     */
    private List<Registro> listacmbBancoFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ResumenRecConceptoBancoControlador
     */
    public ResumenRecConceptoBancoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESUMEN_REC_CONCEPTO_BANCO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despu�s de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListacmbBancoInicial();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        fechaInicial = new Date();
        fechaFinal = new Date();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbBancoInicial
     *
     */
    public void cargarListacmbBancoInicial() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listacmbBancoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenRecConceptoBancoControladorUrlEnum.URL5310
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
     * Carga la lista listacmbBancoFinal
     *
     */
    public void cargarListacmbBancoFinal() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.BANCO.getName(), bancoInicial);

            listacmbBancoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenRecConceptoBancoControladorUrlEnum.URL5867
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * M�todo que genera reporte de acuerdo a un formato recibido.
     * 
     * @param formato
     * Par�metro que determina la extensi�n y formato del reporte que
     * se va a generar
     */
    public void generarReporte(FORMATOS formato) {
        String reporte = "";

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("fechaInicial", formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal", formatearFecha(fechaFinal));

            reemplazar.put("bancoInicial", "'" + bancoInicial + "'");
            reemplazar.put("bancoFinal", "'" + bancoFinal + "'");

            parametros.put("PR_FECHAINICIAL", convertirFecha(fechaInicial));
            parametros.put("PR_FECHAFINAL", convertirFecha(fechaFinal));
            parametros.put("PR_BANCOINICIAL", bancoInicial);
            parametros.put("PR_BANCOFINAL", bancoFinal);

            if (incluirAbonos) {
                reporte = "001279LRecaudoBancoConceptoAbono";
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }

            else {
                reporte = "001285LRecaudoBancoConceptoDRec";
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(
                            ResumenRecConceptoBancoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(
                            ResumenRecConceptoBancoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * M�todo que al ser ejecutado determina que el reporte a generar
     * ser� creado con formato PDF.
     */
    public void oprimirPdf() {
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
    }

    /**
     * M�todo que al ser ejecutado determina que el reporte a generar
     * ser� creado con formato de Excel.
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
    }

    /**
     * Metodo ejecutado al cambiar el control cmbBancoInicial
     * 
     */
    public void cambiarcmbBancoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListacmbBancoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * M�todo que formatea una fecha convirti�ndola en una fecha de
     * tipo (DD/MM/YYYY).
     * 
     * @param fecha
     * Par�metro de tipo DATE a formatear.
     * @return Fecha formateada
     */
    public String formatearFecha(Date fecha) {
        return SysmanFunciones.formatearFecha(fecha);
    }

    /**
     * M�todo que formatea una fecha convirti�ndola en una cadena de
     * texto.
     * 
     * @param fecha
     * Par�metro de tipo DATE a convertir.
     * @return Fecha en formato "DD/MM/YYYY"
     */
    public String convertirFecha(Date fecha) {
        String fechaStr = " ";
        try {
            fechaStr = SysmanFunciones.convertirAFechaCadena(fecha);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return fechaStr;
    }

    /**
     * M�todo que retorna la variable fechaInicial
     * 
     * @return Variable de la fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * M�todo que asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * M�todo que retorna la variable fechaFinal
     * 
     * @return Variable de la fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * M�todo que asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * M�todo que retorna la variable bancoInicial
     * 
     * @return Variable del bancoInicial
     */
    public String getBancoInicial() {
        return bancoInicial;
    }

    /**
     * M�todo que asigna la variable bancoInicial
     * 
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial) {
        this.bancoInicial = bancoInicial;
    }

    /**
     * M�todo que retorna la variable bancoFinal
     * 
     * @return Variable del bancoFinal
     */
    public String getBancoFinal() {
        return bancoFinal;
    }

    /**
     * M�todo que asigna la variable bancoFinal
     * 
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal) {
        this.bancoFinal = bancoFinal;
    }

    /**
     * M�todo que retorna la variable booleana incluirAbonos
     * 
     * @return Variable de incluirAbonos
     */
    public boolean isIncluirAbonos() {
        return incluirAbonos;
    }

    /**
     * M�todo que asigna la variable booleana incluirAbonos
     * 
     * @param incluirAbonos
     * Variable a asignar en incluirAbonos
     */
    public void setIncluirAbonos(boolean incluirAbonos) {
        this.incluirAbonos = incluirAbonos;
    }

    /**
     * M�todo que retorna la variable archivoDescarga
     * 
     * @return Variable de archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * M�todo que asigna la variable archivoDescarga
     * 
     * @param archivoDescarga
     * Variable a asignar en archivoDescarga
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * Retorna la lista listacmbBancoInicial
     * 
     * @return listacmbBancoInicial
     */
    public List<Registro> getListacmbBancoInicial() {
        return listacmbBancoInicial;
    }

    /**
     * Asigna la lista listacmbBancoInicial
     * 
     * @param listacmbBancoInicial
     * Variable a asignar en listacmbBancoInicial
     */
    public void setListacmbBancoInicial(List<Registro> listacmbBancoInicial) {
        this.listacmbBancoInicial = listacmbBancoInicial;
    }

    /**
     * Retorna la lista listacmbBancoFinal
     * 
     * @return listacmbBancoFinal
     */
    public List<Registro> getListacmbBancoFinal() {
        return listacmbBancoFinal;
    }

    /**
     * Asigna la lista listacmbBancoFinal
     * 
     * @param listacmbBancoFinal
     * Variable a asignar en listacmbBancoFinal
     */
    public void setListacmbBancoFinal(List<Registro> listacmbBancoFinal) {
        this.listacmbBancoFinal = listacmbBancoFinal;
    }
}
