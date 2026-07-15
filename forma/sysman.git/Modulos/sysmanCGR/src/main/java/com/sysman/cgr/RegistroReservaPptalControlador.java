/*-
 * RegistroReservaPptalControlador.java
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
import com.sysman.cgr.enums.RegistroReservaPptalControladorEnum;
import com.sysman.cgr.enums.RegistroReservaPptalControladorUrlEnum;
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
 *
 * @version 1.0, 08/03/2017
 * @author spina
 * @version 2.0 16/08/2017
 * @modifiedby jrodriguezr Se elimina la conexion y se ajusta el
 * manejo de excepciones
 * @version 3, 30/08/2017
 * @modifiedby <strong>jrodriguezr </strong> Se refactoriza el código
 * SQL de las listas para utilizar DSS. También los llamados a
 * funciones, procedimientos y métodos de la clase Acciones a llamados
 * a EJB. Textos al archivo properties. Cambio el numero del
 * formulario al enumerado.
 */
@ManagedBean
@ViewScoped
public class RegistroReservaPptalControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * valor obtenido en el combo del formulario
     */
    private String codigoInicial;
    /**
     * valor obtenido en el combo del formulario
     */
    private String codigoFinal;
    /**
     */
    private String anio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     */
    private RegistroDataModelImpl listaCodigoFinal;

    /** captura el valor del mes inicial */
    private int mes;

    /** captura el valor del mes final */
    private int mesFinal;

    /** caputura el valor del campo nivel en el formulario */
    private int nivel;

    private boolean muestraCodigo;
    private boolean conSaldos;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Atributo usado para descargar contenidos de archivos
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RegistroReservaPptalControlador
     */
    public RegistroReservaPptalControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTRO_RESERVA_PPTAL_CONTROLADOR
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
     *
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroReservaPptalControladorUrlEnum.URL5693
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarAno() {
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    /**
     *
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroReservaPptalControladorUrlEnum.URL6230
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroReservaPptalControladorUrlEnum.URL7424
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(RegistroReservaPptalControladorEnum.CUENTAINICIAL.getValue(),
                        codigoInicial);
        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton enviarexcel en la vista
     */
    public void oprimirenviarexcel() {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formatos) {
        if (mes > mesFinal) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2905"));
            return;
        }
        try {
            String parametroSec = ejbSysmanUtil.consultarParametro(compania,
                            "SECCION EN INFORMES RESOLUCION 036", modulo,
                            new Date(), true);
            String conSeccion;
            String lblSeccion;
            String conUnidad;
            String lblUnidad;
            String conRegional;
            String lblRegional;
            if ("SI".equals(parametroSec)) {
                conSeccion = ejbSysmanUtil.consultarParametro(
                                compania,
                                "SECCION 036", modulo, new Date(), true);
                if (!SysmanFunciones.validarVariableVacio(conSeccion)) {
                    lblSeccion = "SECCION";
                }
                else {
                    lblSeccion = "";
                }

                conUnidad = ejbSysmanUtil.consultarParametro(
                                compania,
                                "UNIDAD EJECUTORA 036", modulo,
                                new Date(), true);
                if (!SysmanFunciones.validarVariableVacio(conUnidad)) {
                    lblUnidad = "UNIDAD EJECUTORA";
                }
                else {
                    lblUnidad = "";
                }
                conRegional = ejbSysmanUtil.consultarParametro(compania,
                                "REGIONAL 036", modulo, new Date(), true);
                if (!SysmanFunciones.validarVariableVacio(conRegional)) {
                    lblRegional = "REGIONAL";
                }
                else {
                    lblRegional = "";
                }
            }
            else {
                lblSeccion = "";
                lblUnidad = "";
                lblRegional = "";
                conSeccion = "";
                conUnidad = "";
                conRegional = "";
            }

            archivoDescarga = null;
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("nivel", nivel);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("mes1", mesFinal);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_LBLSECCION_CAPTION",
                            lblSeccion);
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_LBLUNIDAD_CAPTION",
                            lblUnidad);
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_LBLREGIONAL_CAPTION",
                            lblRegional);
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_CONSECCION",
                            conSeccion);
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_CONUNIDAD",
                            conUnidad);
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_CONREGIONAL",
                            conRegional);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_MES",
                            mes);
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_NMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                            .toUpperCase());
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_MES1",
                            mesFinal);
            parametros.put("PR_FORMS_REGISTRORESERVAPPTAL_036_NMES1",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]
                                            .toUpperCase());

            parametros.put("PR_NIVEL_1I",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 1I", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_1F",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 1F", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_2I",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 2I", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_2F",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 2F", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_3I",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 3I", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_3F",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 3F", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_4I",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 4I", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_4F",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 4F", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_5I",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 5I", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_5F",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 5F", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_6I",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 6I", modulo, new Date(),
                                            true));
            parametros.put("PR_NIVEL_6F",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NIVEL 6F", modulo, new Date(),
                                            true));

            Reporteador.resuelveConsulta("001440REGISTRORESERVAPPTAL",
                            Integer.valueOf(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001440REGISTRORESERVAPPTAL", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        codigoFinal = null;
        cargarListaCuentaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

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
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     *
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isMuestraCodigo() {
        return muestraCodigo;
    }

    public void setMuestraCodigo(boolean muestraCodigo) {
        this.muestraCodigo = muestraCodigo;
    }

    public boolean isConSaldos() {
        return conSaldos;
    }

    public void setConSaldos(boolean conSaldos) {
        this.conSaldos = conSaldos;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
