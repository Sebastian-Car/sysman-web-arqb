/*-
 * ListadoSinMedicionAgrupadoControlador.java
 *
 * 1.0
 *
 * 30/09/2016
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
import com.sysman.serviciospublicos.enums.ListadoSinMedicionAgrupadoControladorEnum;
import com.sysman.serviciospublicos.enums.ListadoSinMedicionAgrupadoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el reporte "Problemas de medicion"
 *
 * @version 1.0, 30/09/2016
 * @author jlozano
 * 
 * @author eamaya
 * @version 2.0 06/06/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 * 
 */
@ManagedBean
@ViewScoped
public class ListadoSinMedicionAgrupadoControlador extends BeanBaseModal {
    /**
     * Controlador que permite generar el reporte
     * "Problemas de medicion" para un anio, periodo, intervalo de
     * problemas e intervalo de ciclos seleccionados
     *
     */

    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Indicador para filtrar los registros que afectaron consumo
     */
    private String verConsumo;
    /**
     * Ciclo inicial seleccionado para generar el reporte
     */
    private String cicloInicial;
    /**
     * Problema inicial seleccionado para generar el reporte
     */
    private String problemaInicial;
    /**
     * Problema inicial seleccionado para generar el reporte
     */
    private String problemaFinal;
    /**
     * Periodo seleccionado para generar el reporte
     */
    private String periodo;
    /**
     * Anio seleccionado para generar el reporte
     */
    private String ano;
    /**
     * Ciclo final seleccionado para generar el reporte
     */
    private String cicloFinal;
    /**
     * Atributo que contiene la llave filtroObservaciones
     */
    private String filtroObservaciones = "filtroObservaciones";
    /**
     * Atributo que contiene la llave NUMERO
     */
    private String numero = "NUMERO";
    /**
     * Atributo que contiene la llave CODIGO
     */
    private String codigo = "CODIGO";
    /**
     * Archivo generado para el reporte
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaPeriodo;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCiclo;
    private RegistroDataModelImpl listaCmbProblemaInicial;
    private RegistroDataModelImpl listaCmbProblemaFinal;
    private RegistroDataModelImpl listaCiclo1;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Creates a new instance of ListadoSinMedicionAgrupadoControlador
     */
    public ListadoSinMedicionAgrupadoControlador() {
        super();
        compania = SessionUtil.getCompania();
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        periodo = "01";
        verConsumo = "true";

        try {
            // 1128
            numFormulario = GeneralCodigoFormaEnum.LISTADO_SIN_MEDICION_AGRUPADO_CONTROLADOR
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
        cargarListaPeriodo();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        cargarListaCmbProblemaInicial();
        cargarListaCmbProblemaFinal();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1128-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name Me.VerConsumo =
         * True End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaPeriodo() {
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);

            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoSinMedicionAgrupadoControladorUrlEnum.URL5235
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoSinMedicionAgrupadoControladorUrlEnum.URL5910
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
                                        ListadoSinMedicionAgrupadoControladorUrlEnum.URL6290
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numero);
    }

    public void cargarListaCmbProblemaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoSinMedicionAgrupadoControladorUrlEnum.URL7127
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        "AFR");

        listaCmbProblemaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaCmbProblemaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoSinMedicionAgrupadoControladorUrlEnum.URL8100
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.CLASE.getName(),
                        "AFR");

        param.put(ListadoSinMedicionAgrupadoControladorEnum.PARAM0.getValue(),
                        problemaInicial);

        listaCmbProblemaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaCiclo1() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoSinMedicionAgrupadoControladorUrlEnum.URL9053
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ListadoSinMedicionAgrupadoControladorEnum.PARAM1.getValue(),
                        cicloInicial);

        listaCiclo1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numero);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo que se ejecuta al oprimir el boton Impresora. Verifica
     * el valor del parametro MODELO INFORME SIN MEDICION Y MEDIDORES
     * PROBLEMA, si su valor es "YOPAL" genera el informe
     * "001123LSinMedicionEntreYOP" de lo contrario genera el reporte
     * "001118LSinMedicionEntre". Genera el reporte en formato PDF
     */
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String informe = "";
        String nombreInforme;
        try {
            informe = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MODELO INFORME SIN MEDICION Y MEDIDORES PROBLEMA",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ("".equals(informe)) {
            informe = "NORMAL";
        }

        if ("YOPAL".equals(informe))

        {
            nombreInforme = "001123LSinMedicionEntreYOP";
        }
        else

        {
            nombreInforme = "001118LSinMedicionEntre";
        }

        generarReporte(nombreInforme, ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al oprimir el boton Excel. Verifica el
     * valor del parametro MODELO INFORME SIN MEDICION Y MEDIDORES
     * PROBLEMA, si su valor es "YOPAL" genera el informe
     * "001123LSinMedicionEntreYOP" de lo contrario genera el reporte
     * "001118LSinMedicionEntre". Genera el reporte en formato EXCEL97
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO> , MEDIDOR AS "Medidor"
        archivoDescarga = null;
        String nombreInforme;

        String informe = "";
        try {
            informe = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MODELO INFORME SIN MEDICION Y MEDIDORES PROBLEMA",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ("".equals(informe)) {
            informe = "NORMAL";
        }

        if ("YOPAL".equals(informe))

        {
            nombreInforme = "001123LSinMedicionEntreYOP";
        }
        else

        {
            nombreInforme = "001118LSinMedicionEntre";
        }

        generarReporte(nombreInforme, ReportesBean.FORMATOS.EXCEL97);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera el reporte. Verifica si el indicador
     * verConsumo esta activo o no y aniade el filtro correspondiente
     * a la consulta de la caul se toman los datos
     *
     * @param nombreInforme
     * Nombre del reporte a generar
     * @param formato
     * Formato en que se genera el reporte
     */
    private void generarReporte(String nombreInforme,
        ReportesBean.FORMATOS formato) {

        archivoDescarga = null;

        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        if (Boolean.valueOf(verConsumo)) {
            reemplazos.put(filtroObservaciones,
                            " <> 'NORMAL' ");
        }
        else {
            reemplazos.put(filtroObservaciones,
                            " = 'NORMAL' ");
        }
        reemplazos.put("cicloInicial", cicloInicial);
        reemplazos.put("cicloFinal", cicloFinal);
        reemplazos.put("ano", ano);
        reemplazos.put("periodo", periodo);
        reemplazos.put("problemaInicial", problemaInicial);
        reemplazos.put("problemaFinal", problemaFinal);

        parametros.put("PR_CICLO", cicloInicial);
        parametros.put("PR_CICLO1", cicloFinal);

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
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cicloInicial = new BigDecimal(
                        registroAux.getCampos().get(numero).toString())
                                        .toString();

        cicloFinal = "";

        cargarListaCiclo1();
    }

    public void seleccionarFilaCmbProblemaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        problemaInicial = registroAux.getCampos().get(codigo).toString();

        cargarListaCmbProblemaFinal();
    }

    public void seleccionarFilaCmbProblemaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        problemaFinal = registroAux.getCampos().get(codigo).toString();
    }

    public void seleccionarFilaCiclo1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cicloFinal = new BigDecimal(
                        registroAux.getCampos().get(numero).toString())
                                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getVerConsumo() {
        return verConsumo;
    }

    public void setVerConsumo(String verConsumo) {
        this.verConsumo = verConsumo;
    }

    public String getCicloInicial() {
        return cicloInicial;
    }

    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    public String getProblemaInicial() {
        return problemaInicial;
    }

    public void setProblemaInicial(String problemaInicial) {
        this.problemaInicial = problemaInicial;
    }

    public String getProblemaFinal() {
        return problemaFinal;
    }

    public void setProblemaFinal(String problemaFinal) {
        this.problemaFinal = problemaFinal;
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

    public String getCicloFinal() {
        return cicloFinal;
    }

    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
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
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaCmbProblemaInicial() {
        return listaCmbProblemaInicial;
    }

    public void setListaCmbProblemaInicial(
        RegistroDataModelImpl listaCmbProblemaInicial) {
        this.listaCmbProblemaInicial = listaCmbProblemaInicial;
    }

    public RegistroDataModelImpl getListaCmbProblemaFinal() {
        return listaCmbProblemaFinal;
    }

    public void setListaCmbProblemaFinal(
        RegistroDataModelImpl listaCmbProblemaFinal) {
        this.listaCmbProblemaFinal = listaCmbProblemaFinal;
    }

    public RegistroDataModelImpl getListaCiclo1() {
        return listaCiclo1;
    }

    public void setListaCiclo1(RegistroDataModelImpl listaCiclo1) {
        this.listaCiclo1 = listaCiclo1;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
