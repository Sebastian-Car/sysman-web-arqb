/*-
 * TarifaImpresion.java
 *
 * 1.0
 *
 * 20/09/2016
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.TarifaImpresionEnum;
import com.sysman.serviciospublicos.enums.TarifaImpresionUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
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
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario peridoImpesion que permite generar el
 * reporte de tarifas de los servicios de Acueducto, Alcantarillado y
 * Aseo.
 *
 * @version 5, 22/09/2016 09:40:12 -- Modificado por jlozano
 * @author jlozano
 * 
 * @modified jguerrero
 * @version 2. 16/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class TarifaImpresion extends BeanBaseModal {
    /**
     * Controlador del formulario peridoImpesion que permite generar
     * el reporte de tarifas de los servicios de Acueducto,
     * Alcantarillado y Aseo para un año, periodo y tarifa
     * seleccionados.
     */

    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Titulo del formulario
     */
    private String titulo;
    /**
     * Anio seleccionado para generar el reporte
     */
    private String ano;
    /**
     * Mes seleccionado para generar el reporte
     */
    private String mes;
    /**
     * Tarifa seleccionada para generar el reporte
     */
    private String tarifa;
    /**
     * Indicador para incluir o no tasas en el reporte
     */
    private String incluirTasa;
    /**
     * Archivo generado que contiene el reporte
     */
    private StreamedContent archivoDescarga;

    private HashMap<String, Object> reemplazos = new HashMap<>();
    private HashMap<String, Object> parametros2 = new HashMap<>();

    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parTarifa = "PR_TARIFA";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parUnidad = "PR_UNIDAD";

    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String formatoCalidad = "FORMATO CALIDAD";
    /**
     * Parametro que contiene el nombre del servicio para el que se
     * genera el reporte
     */
    private String nombreServicio;
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String reemplazarAcueducto = "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String cambiarAcueducto = "CAMBIAR NOMBRE SERVICIO ACUEDUCTO";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parPorcentaje = "PR_PORCENTAJE";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parTitulo1 = "PR_TITULO1";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parTitulo2 = "PR_TITULO2";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parTitulo3 = "PR_TITULO3";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parTitulo4 = "PR_TITULO4";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parVisibleTarifa = "PR_VISIBLETARIFA";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parVisibleSeparacion = "PR_VISIBLESEPARACION";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parVisibleDescuento = "PR_VISIBLEDESCUENTO";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parVisibleTramo = "PR_VISIBLETRAMO";
    /**
     * Nombre de parametro enviado para generar el reporte
     */
    private String parVisibleTdi = "PR_VISIBLETDI";
    /**
     * Nombre de parametro enviado para generar el reporte
     * 
     */

    private List<Registro> listaAno;
    private List<Registro> listaMes;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de tarifas disponible para seleccionar
     */
    private List<Registro> listaTipoTarifa;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Creates a new instance of TarifaImpresion
     */
    public TarifaImpresion() {
        super();
        compania = SessionUtil.getCompania();
        titulo = TarifaImpresionEnum.PARAM2.getValue();
        ano = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = StringUtils.leftPad(String.valueOf(
                        Calendar.getInstance().get(Calendar.MONTH) + 1), 2,
                        '0'); // mes
                              // por
                              // defecto
                              // (mes
                              // actual)
        try {
            numFormulario = GeneralCodigoFormaEnum.TARIFA_IMPRESION.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaTipoTarifa();

        cargarListaAno();

        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaTipoTarifa() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TarifaImpresionEnum.PARAM0.getValue(),
                        SessionUtil.getModulo());

        try {
            listaTipoTarifa = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifaImpresionUrlEnum.URL7881
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifaImpresionUrlEnum.URL8638
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifaImpresionUrlEnum.URL8961
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que genera el reporte para el anio, periodo y tarifa
     * seleccionados. Dependiendo de la tarifa seleccionada llama al
     * metodo que genera el respectivo reporte.
     *
     * @param formato
     * Formato en el que se genera el reporte
     */
    private void imprimirReporte(ReportesBean.FORMATOS formato) {
        try {
            switch (tarifa) {
            case "1": // Reporte de Acueducto
                generarReporteAcueducto(formato);
                break;
            case "2": // Reporte de alcantarillado
                generarReporteAlcantarillado(formato);
                break;
            case "3": // Reporte de aseo
                generarReporteAseo(formato);
                break;
            default:
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1660")); // No
                                                                             // se
                                                                             // ha
                                                                             // seleccionado
                                                                             // tarifa
            }
        }
        catch (NamingException | SQLException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * Metodo que se ejecuta al oprimir el boton Imprimir. Llama al
     * metodo que verifica los datos ingresados, en caso de que este
     * retorne true llama al metodo imprimir reporte.
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        imprimirReporte(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera el reporte de tarifas para Acueducto
     *
     * @param formato
     * Formato en el que se genera el reporte
     * @throws NamingException
     * @throws SQLException
     */
    private void generarReporteAcueducto(ReportesBean.FORMATOS formato) {
        try {

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            formatoCalidad,
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO"))) {

                archivoDescarga = null;
                nombreServicio = ejbSysmanUtil.consultarParametro(compania,
                                reemplazarAcueducto, SessionUtil.getModulo(),
                                new Date(), true);

                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                cambiarAcueducto,
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {
                    parametros2.put(parTarifa, nombreServicio);
                    parametros2.put(parUnidad, "Kw");
                }
                else {
                    parametros2.put(parTarifa, "Acueducto");
                    parametros2.put(parUnidad, "m3");
                }
                generarReporte(formato,
                                "001095TarifaAcueductoCOS");
            }
            else {
                archivoDescarga = null;
                nombreServicio = ejbSysmanUtil.consultarParametro(compania,
                                reemplazarAcueducto, SessionUtil.getModulo(),
                                new Date(), false);

                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                cambiarAcueducto,
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {
                    parametros2.put(parTarifa, nombreServicio);
                    parametros2.put(parUnidad, "Kw");
                }
                else {
                    parametros2.put(parTarifa, "Acueducto");
                    parametros2.put(parUnidad, "m3");
                }

                generarReporte(formato,
                                "001089TarifaAcueducto");
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que genera el reporte de tarifas para Alcantarillado
     *
     * @param formato
     * Formato en el que se genera el reporte
     * @throws NamingException
     * @throws SQLException
     */
    private void generarReporteAlcantarillado(ReportesBean.FORMATOS formato)
                    throws NamingException, SQLException {
        try {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            formatoCalidad,
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO"))) {

                archivoDescarga = null;
                generarReporte(formato,
                                "001099TarifaAlcantarilladoCOS");
            }
            else {
                archivoDescarga = null;
                String unidades;

                nombreServicio = ejbSysmanUtil.consultarParametro(compania,
                                "NOMBRE SERVICIO A REMPLAZAR ALCANTARILLADO",
                                SessionUtil.getModulo(),
                                new Date(), false);

                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "CAMBIAR NOMBRE SERVICIO ALCANTARILLADO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {
                    unidades = SysmanFunciones
                                    .nvlStr(ejbSysmanUtil.consultarParametro(
                                                    compania,
                                                    "UNIDAD PARA ALCANTARILLADO",
                                                    SessionUtil.getModulo(),
                                                    new Date(), true), "m3");
                    parametros2.put(parTarifa,
                                    "Estadísticas de consumo para "
                                        + nombreServicio);
                    parametros2.put(parUnidad, unidades);
                    parametros2.put(parPorcentaje, "% sobre "
                        + unidades.substring(0, unidades.length() > 5 ? 5
                            : unidades.length())
                        + ".");
                }
                else {
                    parametros2.put(parTarifa, "Tarifas Alcantarillado");
                    parametros2.put(parUnidad, "m3");
                    parametros2.put(parPorcentaje, "% sobre acued.");
                }
                generarReporte(formato,
                                "001092TarifaAlcantarillado");
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que genera el reporte de tarifas para Aseo
     *
     * @param formato
     * Formato en el que se genera el reporte
     * @throws NamingException
     * @throws SQLException
     */
    private void generarReporteAseo(ReportesBean.FORMATOS formato) {

        try {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            formatoCalidad,
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO"))) {
                archivoDescarga = null;

                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MANEJA RES/ 351",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {

                    parametros2.put(parTitulo1, "BARRIDO Y LIMPIEZA");

                    parametros2.put(parTitulo2,
                                    "TRATAMIENTO Y DISP. FINAL");

                    parametros2.put(parTitulo3, "COMERCIALIZACIÓN");

                    parametros2.put(parTitulo4, "RECOLECCIÓN Y TRANSPORTE");

                }
                else {
                    parametros2.put(parTitulo1, "UNICO");

                    parametros2.put(parTitulo2, "DOMICILIARIO");

                    parametros2.put(parTitulo3, "BARRIDO");

                    parametros2.put(parTitulo4, "CONSUMO");
                }

                generarReporte(formato, "001102TarifaAseoCOS");
            }
            else {
                archivoDescarga = null;
                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MANEJA RES/ 351",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {
                    parametros2.put(parTitulo1, "BARRIDO Y LIMPIEZA");

                    parametros2.put(parTitulo2,
                                    "TRATAMIENTO Y DISPOSICION FINAL");

                    parametros2.put(parTitulo3, "COMER. Y MANEJO DEL RECAUDO");

                    parametros2.put(parTitulo4, "RECOLECCION Y TRANSPORTE");
                    parametros2.put(parVisibleTramo, -1);
                }
                else {
                    parametros2.put(parTitulo1, "UNICO");

                    parametros2.put(parTitulo2, "DOMICILIARIO");

                    parametros2.put(parTitulo3, "BARRIDO");

                    parametros2.put(parTitulo4, "CONSUMO");
                    parametros2.put(parVisibleTramo, 0);
                }
                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MUESTRA TARIFA DESHABITADO INFORME ASEO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {
                    parametros2.put(parVisibleTarifa, -1);
                }
                else {
                    parametros2.put(parVisibleTarifa, 0);
                }
                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MUESTRA SEPARACION EN LA FUENTE INFORME ASEO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {

                    parametros2.put(parVisibleSeparacion, -1);
                }
                else {
                    parametros2.put(parVisibleSeparacion, 0);
                }
                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MUESTRA DESCUENTO NO PUERTA PUERTA INFORME ASEO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {

                    parametros2.put(parVisibleDescuento, -1);
                }
                else {
                    parametros2.put(parVisibleDescuento, 0);
                }
                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "MUESTRA TDI INFORME ASEO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO"))) {

                    parametros2.put(parVisibleTdi, -1);
                }
                else {
                    parametros2.put(parVisibleTdi, 0);
                }
                generarReporte(formato, "001093TarifaAseo");
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que se ejecuta al oprimir el boton Impresora. Llama al
     * metodo que verifica los datos ingresados, en caso de que este
     * retorne true llama al metodo imprimir reporte.
     */
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        imprimirReporte(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al oprimir el boton Excel. Llama al
     * metodo que verifica los datos ingresados, en caso de que este
     * retorne true llama al metodo imprimir reporte.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        imprimirReporte(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        mes = StringUtils.leftPad(mes, 2, '0');
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera el reporte de tarifas para el anio,periodo y
     * servicio seleccionado
     *
     * @param formato
     * Formato en que se genera el reporte
     * @param nombreInforme
     * Nombre del reporte que se va a generar
     */
    public void generarReporte(ReportesBean.FORMATOS formato,
        String nombreInforme) {
        archivoDescarga = null;

        reemplazos.put("ano", ano);
        reemplazos.put("mes", mes);
        reemplazos.put("tarifa",
                        Boolean.valueOf(incluirTasa) ? -1 : 0);

        parametros2.put("PR_ANO", ano);
        parametros2.put("PR_MES", StringUtils.capitalize(
                        new DateFormatSymbols()
                                        .getMonths()[Integer.parseInt(mes)
                                            - 1]));

        Reporteador.resuelveConsulta(nombreInforme,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros2);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreInforme, parametros2,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            Logger.getLogger(TarifaImpresion.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarTipoTarifa() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getTarifa() {
        return tarifa;
    }

    public void setTarifa(String tarifa) {
        this.tarifa = tarifa;
    }

    public String getIncluirTasa() {
        return incluirTasa;
    }

    public void setIncluirTasa(String incluirTasa) {
        this.incluirTasa = incluirTasa;
    }

    public List<Registro> getListaTipoTarifa() {
        return listaTipoTarifa;
    }

    public void setListaTipoTarifa(List<Registro> listaTipoTarifa) {
        this.listaTipoTarifa = listaTipoTarifa;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
