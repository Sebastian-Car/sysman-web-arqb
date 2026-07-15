/*-
 * ListadoActaSuspencion.java
 *
 * 1.0
 *
 * 28/09/2016
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.ListadoActaSuspencionEnum;
import com.sysman.serviciospublicos.enums.ListadoActaSuspencionUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el reporte Listado de usuarios con
 * acta de suspencion
 *
 * @version 1.0, 28/09/2016
 * @author jlozano
 * 
 * @version 2, 06/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio código formulario y actualización de
 * ConnectorPool
 */

@ManagedBean
@ViewScoped
public class ListadoActaSuspencion extends BeanBaseModal {
    /**
     *
     * Controlador que permite generar el reporte Listado de usuarios
     * con acta de suspencion para un ciclo, anio y periodo
     * seleccionados
     *
     */

    private final String compania;

    private final String consCodigoRuta;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Codigo del subscriptor inicial
     */
    private String codigoInicial;
    /**
     * Codigo del subscriptor final
     */
    private String codigoFinal;
    /**
     * Ciclo selecionado para generar el reporte
     */
    private String ciclo;
    /**
     * Periodo selecionado para generar el reporte
     */
    private String periodo;
    /**
     * Anio seleccionado para generar el reporte
     */
    private String ano;
    /**
     * Condicion de filtro para las consulta del reporte y de las
     * listas listaCodigoInicial y listaCodigoInicial. En caso de que
     * el ciclo sea igual a "Todos" la condicion queda vacia, de lo
     * contrario la condicion queda AND SP_USUARIO.CICLO = ciclo
     */
    private String condicion;
    /**
     * Archivo generado para el reporte
     */
    private StreamedContent archivoDescarga;
    private String todos = "T";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de periodos disponibles para seleccionar
     */
    private List<Registro> listaCmbPeriodo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de codigos iniciales disponibles para seleccionar
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista de codigos finales disponibles para seleccionar
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Lista de ciclos disponibles para seleccionar
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Lista de anios disponibles para seleccionar
     */
    private List<Registro> listaCmbAno;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Creates a new instance of ListadoActaSuspencion
     */
    public ListadoActaSuspencion() {
        super();
        compania = SessionUtil.getCompania();
        ano = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        periodo = StringUtils.leftPad(String.valueOf(
                        Calendar.getInstance().get(Calendar.MONTH) + 1), 2,
                        '0');
        condicion = "";
        consCodigoRuta = "CODIGORUTA";
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_ACTA_SUSPENCION
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

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCmbPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaCiclo();
        cargarListaCmbAno();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoActaSuspencionUrlEnum.URL6363
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoRuta);

    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoActaSuspencionUrlEnum.URL7450
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(ListadoActaSuspencionEnum.PARAM0.getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoRuta);

    }

    public void cargarListaCmbPeriodo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaCmbPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoActaSuspencionUrlEnum.URL8368
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaCmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoActaSuspencionUrlEnum.URL9222
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoActaSuspencionUrlEnum.URL9995
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo que se ejecuta al presionar el boton CmdInformeSus.
     * Verifica si el ciclo, codigo inicial o codigo final son vacios
     * o nulos y en caso de que lo sean les asigna un valor por
     * defecto, finalmente genera el reporte en formato PDF.
     */
    public void oprimirCmdInformeSus() {
        // <CODIGO_DESARROLLADO>
        generarReporte("001107InfActaSuspension", ReportesBean.FORMATOS.PDF,
                        true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al presionar el boton CmdInformeSus.
     * Verifica si el ciclo, codigo inicial o codigo final son vacios
     * o nulos y en caso de que lo sean les asigna un valor por
     * defecto, finalmente genera el reporte en formato EXCEL.
     */
    public void oprimirComando21() {
        // <CODIGO_DESARROLLADO>
        generarReporte("001107InfActaSuspension", ReportesBean.FORMATOS.EXCEL,
                        true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera el reporte
     *
     * @param nombreInforme
     * Nombre del reporte que se va a generar
     * @param formato
     * Formato en el que se va a generar el reporte
     * @param visibleEncabezado
     * Indicador de visibilidad del encabezado del reporte para las
     * paginas mayores a 1
     */
    private void generarReporte(String nombreInforme,
        ReportesBean.FORMATOS formato, boolean visibleEncabezado) {
        archivoDescarga = null;
        String titulo = "";
        int periodos = 0;
        try {
            titulo = SysmanFunciones.nvlStr(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "TITULO ACTA DE SUSPENSION",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            "");
            periodos = Integer.parseInt(SysmanFunciones.nvl(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "PERIODOS DE ATRASO MODIFICA TIT. ACTA SUSPENSION",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            1000).toString());
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        if (!todos.equals(ciclo)) {
            condicion = " AND SP_USUARIO.CICLO = " + ciclo;
        }
        else {
            condicion = "";
        }
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        reemplazos.put("ano", ano);
        reemplazos.put("periodo", periodo);
        reemplazos.put("ciclo", condicion);

        parametros.put("PR_TITULO", titulo);
        parametros.put("PR_PERIODOSATRASO", periodos);
        parametros.put("PR_VISIBLEENCABEZADO", visibleEncabezado);

        Reporteador.resuelveConsulta(nombreInforme,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreInforme, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(consCodigoRuta).toString();
        codigoFinal = "";
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(consCodigoRuta).toString();
    }

    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        codigoInicial = registroAux.getCampos().get("CODIGOINICIAL").toString();
        codigoFinal = registroAux.getCampos().get("CODIGOFINAL").toString();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCmbPeriodo() {
        return listaCmbPeriodo;
    }

    public void setListaCmbPeriodo(List<Registro> listaCmbPeriodo) {
        this.listaCmbPeriodo = listaCmbPeriodo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public List<Registro> getListaCmbAno() {
        return listaCmbAno;
    }

    public void setListaCmbAno(List<Registro> listaCmbAno) {
        this.listaCmbAno = listaCmbAno;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
