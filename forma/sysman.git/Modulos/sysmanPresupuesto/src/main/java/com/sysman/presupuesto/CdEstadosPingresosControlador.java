/*-
 * CdEstadosPingresosControlador.java
 *
 * 1.0
 * 
 * 28/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.CdEstadosPingresosControladorEnum;
import com.sysman.presupuesto.enums.CdEstadosPingresosControladorUrlEnum;
import com.sysman.presupuesto.enums.FrmCaEjecucionPasControladorEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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
 *
 * @version 1.0, 28/11/2017
 * @author jcrodriguez,Migracion de formulario en access
 * CD_ESTADOSPINGRESOS a CdEstadosPingresosControlador web y del
 * reporte CD_ESTADOSPINGRESOS a 001527CDESTADOSPINGRESOS web,
 * creacion de controlador y forma, creacion de dss para los combos
 * grande y sencillos.Controlador que descarga un reporte del estado
 * de la situacion presupuestaria
 */
@ManagedBean
@ViewScoped
public class CdEstadosPingresosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * variable cadena que almacena la cuenta inicial
     */
    private String cuentaInicial;
    /**
     * variable cadena que almancea la cuenta final
     */
    private String cuentaFinal;
    /**
     * variable cadena que almacena el ano seleccionado del combo ano
     */
    private String ano;
    /**
     * variable cadena que almacena el mes seleccionado del combo mes
     */
    private String mes;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que almacena la lista de anos
     */
    private List<Registro> listaAno;
    /**
     * variable que almacena la lista de cuentas
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * variable que almacena la lista de cuentas
     */
    private RegistroDataModelImpl listaCuentaFinal;
    /**
     * Atributo que contiene el valor asignado al nombre de la cuenta
     * inicial por la cual se va a filtrar el reporte
     */
    private String nombreCuentaInicial;
    /**
     * Atributo que contiene el valor asignado al nombre de la cuenta
     * final por la cual se va a filtrar el reporte
     */
    private String nombreCuentaFinal;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de CdEstadosPingresosControlador
     */
    public CdEstadosPingresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CD_ESTADOSPINGRESOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
        abrirFormulario();
        cargarListaAno();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
    }

    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CdEstadosPingresosControladorUrlEnum.URL13622
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
     * Metodo ejecutado al cambiar el control Ano metodo que al
     * cambiar el ano actualiza el combo de cuenta inicial y final
     * 
     */
    public void cambiarAno() {
        cuentaInicial = null;
        cuentaFinal = null;
        nombreCuentaInicial = null;
        nombreCuentaFinal = null;
    }

    /**
     * 
     * Carga la lista listaCuentaInicial
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CdEstadosPingresosControladorUrlEnum.URL11959
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "C");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaFinal
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CdEstadosPingresosControladorUrlEnum.URL12000
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "C");
        param.put(FrmCaEjecucionPasControladorEnum.PARAM0.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            SessionUtil.getModulo(), new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        nombreCuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
        cuentaFinal = null;
        nombreCuentaFinal = null;

        cargarListaCuentaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        nombreCuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * metodo que contiene la logica para generar el reporte en
     * formato pdf o excel
     * 
     * @param formato
     */
    private void generarReporte(FORMATOS formato) {
        try {

            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("ano", ano);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("mes", mes);
            reemplazar.put("naturaleza", "C");

            parametros.put("PR_ANO", ano);
            parametros.put("PR_MES", ejbSysmanUtil
                            .mostrarNombreDeMes(Integer.parseInt(mes)));

            parametros.put("PR_NIT", SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_AHORA", new Date());
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_DEPARTAMENTOCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDepartamento()
                                            .toUpperCase());

            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",
                            SysmanFunciones.nvlStr(
                                            getParametro("NOMBRE REPRESENTANTE LEGAL",
                                                            true),
                                            ""));

            parametros.put("PR_CEDULA_REPRESENTANTE_LEGAL",
                            SysmanFunciones.nvlStr(
                                            getParametro("CEDULA REPRESENTANTE LEGAL",
                                                            true),
                                            ""));

            parametros.put("PR_POLIZA_REPRESENTANTE_LEGAL",
                            SysmanFunciones.nvlStr(
                                            getParametro("POLIZA REPRESENTANTE LEGAL",
                                                            true),
                                            ""));
            Reporteador.resuelveConsulta(
                            CdEstadosPingresosControladorEnum.REPORTE001527
                                            .getValue(),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_ASEGURADORA_REPRESENTANTE_LEGAL",
                            SysmanFunciones.nvlStr(
                                            getParametro("ASEGURADORA REPRESENTANTE LEGAL",
                                                            true),
                                            ""));

            parametros.put("PR_VALOR_ASEGURADO_REPRESENTANTE_LEGAL",
                            Double.parseDouble(
                                            SysmanFunciones.nvlStr(
                                                            getParametro("VALOR ASEGURADO REPRESENTANTE LEGAL",
                                                                            true),
                                                            "0")));

            parametros.put("PR_NOMBRE_ENCARGADO_AREA_INFORMES_CONTRALORIA",
                            SysmanFunciones.nvlStr(
                                            getParametro("NOMBRE ENCARGADO AREA INFORMES CONTRALORIA",
                                                            true),
                                            ""));

            parametros.put("PR_CARGO_ENCARGADO_AREA_INFORMES_CONTRALORIA",
                            SysmanFunciones.nvlStr(
                                            getParametro("CARGO ENCARGADO AREA INFORMES CONTRALORIA",
                                                            true),
                                            ""));

            parametros.put("PR_CEDULA_ENCARGADO_AREA_INFORMES_CONTRALORIA",
                            SysmanFunciones.nvlStr(
                                            getParametro("CEDULA ENCARGADO AREA INFORMES CONTRALORIA",
                                                            true),
                                            ""));
            parametros.put("PR_VENCIMIENTO_POLIZA_REPRESENTANTE_LEGAL",
                            SysmanFunciones.nvlStr(
                                            getParametro("VENCIMIENTO POLIZA REPRESENTANTE LEGAL",
                                                            true),
                                            ""));

            archivoDescarga = JsfUtil
                            .exportarStreamed(
                                            CdEstadosPingresosControladorEnum.REPORTE001527
                                                            .getValue(),
                                            parametros,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            formato);
        }
        catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
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
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     * 
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     * 
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     * 
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public String getNombreCuentaInicial() {
        return nombreCuentaInicial;
    }

    public void setNombreCuentaInicial(String nombreCuentaInicial) {
        this.nombreCuentaInicial = nombreCuentaInicial;
    }

    public String getNombreCuentaFinal() {
        return nombreCuentaFinal;
    }

    public void setNombreCuentaFinal(String nombreCuentaFinal) {
        this.nombreCuentaFinal = nombreCuentaFinal;
    }

}
