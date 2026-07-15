/*-
 * RegistroAprComPagControlador.java
 *
 * 1.0
 *
 * 08/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.RegistroAprComPagControladorEnum;
import com.sysman.cgr.enums.RegistroAprComPagControladorUrlEnum;
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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite gernerar el informe LIBRO APROPIACIONES,
 * COMPROMISOS, OBLIGACIONES Y PAGOS
 *
 * @version 1.0, 08/03/2017
 * @author jguerrero
 * @version 2.0 16/08/2017
 * @modifiedby jrodriguezr Se elimina la conexion y se ajusta el
 * manejo de excepciones
 * @version 3, 29/08/2017
 * @modifiedby <strong>jrodriguezr </strong>Se refactoriza el código
 * SQL de las listas para utilizar DSS. También los llamados a
 * funciones, procedimientos y métodos de la clase Acciones a llamados
 * a EJB. Textos al archivo properties. Cambio el numero del
 * formulario al enumerado.
 */
@ManagedBean
@ViewScoped
public class RegistroAprComPagControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Valor del atributo correspondiente a
     */
    private boolean checkIndicador;
    /**
     * Valor del atributo correspondiente a
     */
    private boolean checkConSaldos;
    /**
     * Valor del atributo correspondiente a
     */
    private String cuentaInicial;
    /**
     * Valor del atributo correspondiente a
     */
    private String cuentaFinal;
    /**
     * Valor del atributo correspondiente a
     */
    private String mesInicial;
    /**
     * Valor del atributo correspondiente a
     */
    private String mesFinal;
    /**
     * Valor del atributo correspondiente a
     */
    private String ano;
    /**
     * Valor del atributo correspondiente a
     */
    private String nivel;
    /**
     * Valor del atributo correspondiente a
     */
    private String nombreMesInicial;
    /**
     * Valor del atributo correspondiente a
     */
    private String nombreMesFinal;

    private Map<String, Object> parametrosInforme;

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
     *
     */
    private List<Registro> listaMesInicial;
    /**
     *
     */
    private List<Registro> listaMesFinal;
    /**
     *
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     *
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     *
     */
    private RegistroDataModelImpl listaCuentaFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RegistroAprComPagControlador
     */
    public RegistroAprComPagControlador() {
        super();
        compania = SessionUtil.getCompania();
        checkIndicador = true;

        ano = Integer.toString(SysmanFunciones.ano(new Date()));
        mesInicial = mesFinal = Integer
                        .toString(SysmanFunciones.mes(new Date()));
        nivel = "99";

        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTRO_APR_COM_PAG_CONTROLADOR
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
        cargarListaMesInicial();
        cargarListaMesFinal();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
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
     * Carga la lista listaMesInicial
     */
    public void cargarListaMesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroAprComPagControladorUrlEnum.URL6474
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
     */
    public void cargarListaMesFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);
        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroAprComPagControladorUrlEnum.URL7055
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaAno
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroAprComPagControladorUrlEnum.URL7684
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista CuentaInicial
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroAprComPagControladorUrlEnum.URL8028
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCuentaFinal
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroAprComPagControladorUrlEnum.URL9002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(RegistroAprComPagControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton generarPdf en la vista
     */
    public void oprimirgenerarPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton generarExcel en la vista
     */
    public void oprimirgenerarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control MesInicial
     */
    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        mesFinal = null;
        nombreMesInicial = mesInicial;
        cargarListaMesFinal();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control MesFinal
     *
     */
    public void cambiarMesFinal() {
        // <CODIGO_DESARROLLADO>
        nombreMesFinal = mesFinal;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Ano
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>

        cargarListaMesInicial();
        cargarListaCuentaInicial();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control nivel
     */
    public void cambiarnivel() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(nivel)) {
            nivel = "99";
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
        cuentaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        cuentaFinal = null;
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
        cuentaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable checkIndicador
     *
     * @return checkIndicador
     */
    public boolean getCheckIndicador() {
        return checkIndicador;
    }

    /**
     * Asigna la variable checkIndicador
     *
     * @param checkIndicador
     * Variable a asignar en checkIndicador
     */
    public void setCheckIndicador(boolean checkIndicador) {
        this.checkIndicador = checkIndicador;
    }

    /**
     * Retorna la variable checkConSaldos
     *
     * @return checkConSaldos
     */
    public boolean getCheckConSaldos() {
        return checkConSaldos;
    }

    /**
     * Asigna la variable checkConSaldos
     *
     * @param checkConSaldos
     * Variable a asignar en checkConSaldos
     */
    public void setCheckConSaldos(boolean checkConSaldos) {
        this.checkConSaldos = checkConSaldos;
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
     * Retorna la variable nivel
     *
     * @return nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Asigna la variable nivel
     *
     * @param nivel
     * Variable a asignar en nivel
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Retorna la variable nombreMesInicial
     *
     * @return nombreMesInicial
     */
    public String getNombreMesInicial() {
        return nombreMesInicial;
    }

    /**
     * Asigna la variable nombreMesInicial
     *
     * @param nombreMesInicial
     * Variable a asignar en nombreMesInicial
     */
    public void setNombreMesInicial(String nombreMesInicial) {
        this.nombreMesInicial = nombreMesInicial;
    }

    /**
     * Retorna la variable nombreMesFinal
     *
     * @return nombreMesFinal
     */
    public String getNombreMesFinal() {
        return nombreMesFinal;
    }

    /**
     * Asigna la variable nombreMesFinal
     *
     * @param nombreMesFinal
     * Variable a asignar en nombreMesFinal
     */
    public void setNombreMesFinal(String nombreMesFinal) {
        this.nombreMesFinal = nombreMesFinal;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void genInforme(FORMATOS formato) {
        archivoDescarga = null;
        String reporte = "001439REGISTROAPRCOMPAG6224";
        String consulta = checkConSaldos ? "001440REGISTROAPRCOMPAG6224"
            : "001439REGISTROAPRCOMPAG6224";

        Map<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("ano", ano);
        reemplazar.put("mesInicial", mesInicial);
        reemplazar.put("mesFinal", mesFinal);
        reemplazar.put("codigoInicial", cuentaInicial);
        reemplazar.put("codigoFinal", cuentaFinal);
        reemplazar.put("nivel", nivel);

        traerParamentrosNiveles();
        parametrosEncabezadoInforme();
        generarTituloPeriodo();
        parametrosInforme.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        parametrosInforme.put("PR_VISIBLE_ID", checkIndicador);

        Reporteador.resuelveConsulta(consulta,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametrosInforme);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametrosInforme,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void traerParamentrosNiveles() {
        parametrosInforme = new HashMap<>();
        try {
            parametrosInforme.put("PR_NIVEL_1I", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 1I",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));

            parametrosInforme.put("PR_NIVEL_1F", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 1F",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));
            parametrosInforme.put("PR_NIVEL_2I", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 2I",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));
            parametrosInforme.put("PR_NIVEL_2F", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 2F",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));

            parametrosInforme.put("PR_NIVEL_3I", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 3I",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));
            parametrosInforme.put("PR_NIVEL_3F", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 3F",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));

            parametrosInforme.put("PR_NIVEL_4I", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 4I",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));
            parametrosInforme.put("PR_NIVEL_4F", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 4F",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));

            parametrosInforme.put("PR_NIVEL_5I", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 5I",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));
            parametrosInforme.put("PR_NIVEL_5F", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 5F",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));

            parametrosInforme.put("PR_NIVEL_6I", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 6I",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));
            parametrosInforme.put("PR_NIVEL_6F", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NIVEL 6F",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "''"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void parametrosEncabezadoInforme() {
        try {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "SECCION EN INFORMES RESOLUCION 036",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                                            "NO"))) {

                String conSeccion = SysmanFunciones.nvl(
                                ejbSysmanUtil.consultarParametro(
                                                compania, "SECCION 036",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                "''").toString();

                if (!SysmanFunciones.validarVariableVacio(conSeccion)) {
                    parametrosInforme.put("PR_CONSECCION", conSeccion);
                    parametrosInforme.put("PR_LBLSECCION", "SECCION");
                }
                String unidad = SysmanFunciones.nvl(
                                ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "UNIDAD EJECUTORA 036",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                "''").toString();

                if (!SysmanFunciones.validarVariableVacio(unidad)) {
                    parametrosInforme.put("PR_CONUNIDAD", unidad);
                    parametrosInforme.put("PR_LBLUNIDAD", "UNIDAD EJECUTORA");
                }
                String regional = SysmanFunciones.nvl(
                                ejbSysmanUtil.consultarParametro(
                                                compania, "REGIONAL 036",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                "''").toString();
                if (!SysmanFunciones.validarVariableVacio(regional)) {
                    parametrosInforme.put("PR_CONREGIONAL", regional);
                    parametrosInforme.put("PR_LBLREGIONAL", "REGIONAL");
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void generarTituloPeriodo() {
        String nombrePeriodoInicial = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        String nombrePeriodoFinal = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesFinal)];

        if (nombrePeriodoFinal.equals(nombrePeriodoInicial)) {
            parametrosInforme.put("PR_PERIODO", nombrePeriodoFinal);
        }
        else {
            String titulo = SysmanFunciones.concatenar("De ",
                            nombrePeriodoInicial, " a ",
                            nombrePeriodoFinal);

            parametrosInforme.put("PR_PERIODO", titulo);
        }
    }

}
