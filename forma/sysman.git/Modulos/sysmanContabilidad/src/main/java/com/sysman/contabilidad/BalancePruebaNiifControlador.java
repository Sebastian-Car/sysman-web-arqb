package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalancePruebaNiifControladorEnum;
import com.sysman.contabilidad.enums.BalancePruebaNiifControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*-
 * BalancePruebaNiifControlador.java
 *
 * 1.0
 *
 * 27/04/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 27/04/2017
 * @author jsforero
 *
 * @version 2, 15/08/2017
 * @modifiedby jrodriguezr Se refactoriza el código SQL de las listas
 * para utilizar DSS. También los llamados a funciones, procedimientos
 * y métodos de la clase Acciones a llamados a EJB. Textos al archivo
 * properties. Cambio el número del formulario al enumerado. Se
 * realiza rediseño del formulario debido a que el indicador Entre
 * Meses no aplica ningún filtro en la consulta del informe. Se migra
 * el reporte y se eliminan etiquetas y campos que no se hacen
 * visibles en
 */
@ManagedBean
@ViewScoped
public class BalancePruebaNiifControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;

    private String codigoFinal;

    private String mesInicial;

    private String mesFinal;

    private int ano;
    private String digitos;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnoTrabajo;

    private List<Registro> listaMesTrabajo;

    private List<Registro> listaMesTrabajoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCodigoInicial;

    private RegistroDataModelImpl listaCodigoFinal;

    private boolean meses;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de BalancePruebaNiifControlador
     */
    private StreamedContent archivoDescarga;

    public BalancePruebaNiifControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {

            numFormulario = GeneralCodigoFormaEnum.BALANCE_PRUEBA_NIIF_CONTROLADOR
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

        cargarListaAno();
        cargarListaMesTrabajo();
        cargarListaMesTrabajoFinal();
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
        ano = SysmanFunciones.ano(new Date());
        mesInicial = Integer.toString(SysmanFunciones.mes(new Date()));
        digitos = "6";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        UrlBean urlListA = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalancePruebaNiifControladorUrlEnum.URL5994
                                                        .getValue());
        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListA.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        UrlBean urlList = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalancePruebaNiifControladorUrlEnum.URL5995
                                                        .getValue());
        try {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(urlList.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesTrabajoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(GeneralParameterEnum.NUMERO.name(), mesInicial);
        UrlBean urlList = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalancePruebaNiifControladorUrlEnum.URL5996
                                                        .getValue());
        try {
            listaMesTrabajoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(urlList.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalancePruebaNiifControladorUrlEnum.URL3385
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista CodigoFinal
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalancePruebaNiifControladorUrlEnum.URL4721
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(BalancePruebaNiifControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);
        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        archivoDescarga = null;
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("mesTrabajo", mesInicial);
            reemplazar.put("mesTrabajo-1", Integer.parseInt(mesInicial) - 1);
            reemplazar.put("digitos", digitos);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            String companiaNiif = ejbSysmanUtil.consultarParametro(compania,
                            "COMPAÑIA EQUIVALENTE NIIF",
                            SessionUtil.getModulo(),
                            new Date(), true);
            reemplazar.put("companiaNiif", companiaNiif);

            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta("001446BalancePruebaNiif",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_ANOTRABAJO", Integer.toString(ano));
            parametros.put("PR_MESTRABAJO",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesInicial)]
                                                            .toUpperCase());
            parametros.put("PR_FIRMA_CONTABLE_1",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA CONTABLE 1",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_CARGO_CONTABLE_1",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO CONTABLE 1",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_DOCUMENTO_CONTABLE_1",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "DOCUMENTO CONTABLE 1",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_FIRMA_CONTABLE_2",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA CONTABLE 2",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_CARGO_CONTABLE_2",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO CONTABLE 2",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_DOCUMENTO_CONTABLE_2",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "DOCUMENTO CONTABLE 2",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_FIRMA_CONTABLE_3",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA CONTABLE 3",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_CARGO_CONTABLE_3",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO CONTABLE 3",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_DOCUMENTO_CONTABLE_3",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "DOCUMENTO CONTABLE 3",
                                            SessionUtil.getModulo(),
                                            new Date(), true));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001446BalancePruebaNiif", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        cargarListaCodigoFinal();
        codigoFinal = null;
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    public void cambiarAnoTrabajo() {
        cargarListaCodigoInicial();
        // cargarListaCodigoFinal();
    }

    public void cambiarmeses() {
        mesFinal = mesInicial;
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isMeses() {
        return meses;
    }

    public void setMeses(boolean meses) {
        this.meses = meses;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    public List<Registro> getListaMesTrabajoFinal() {
        return listaMesTrabajoFinal;
    }

    public void setListaMesTrabajoFinal(List<Registro> listaMesTrabajoFinal) {
        this.listaMesTrabajoFinal = listaMesTrabajoFinal;
    }
}
