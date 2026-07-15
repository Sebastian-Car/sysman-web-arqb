/*-
 * LecturaCriticaInfControlador.java
 *
 * 1.0
 *
 * 20/10/2016
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.LecturaCriticaInfControladorEnum;
import com.sysman.serviciospublicos.enums.LecturaCriticaInfControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * Controlador que permite generar el reporte de lectura critica.
 *
 * @version 1.0, 20/10/2016
 * @author jrodriguezr
 * @version 2, 17/05/2017 jrodriguezr Se refactoriza el c�digo SQL
 * de las listas para utilizar DSS. Tambi�n los llamados a
 * funciones, procedimientos y m�todos de la clase Acciones a
 * llamados a EJB. Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class LecturaCriticaInfControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que representa el valor que toma el indicador de
     * facturacion en sitio
     */
    private boolean sitio;
    /**
     * Variable que representa el ciclo seleccionado por el usuario
     */
    private String ciclo;
    /**
     * Variable que representa el codigo Inicial por el cual se filtra
     * el reporte.
     */
    private String codigoInicial;
    /**
     * Variable que representa el codigo Final por el cual se filtra
     * el reporte.
     */
    private String codigoFinal;
    /**
     * Variable que representa la opcion seleccionada por el usuario
     * en la lista de observaciones.
     */
    private String observaciones;
    /**
     * Variable que representa el porcentaje menor ingresado por el
     * usuario.
     */
    private String porcMenor;
    /**
     * Variable que representa el porcentaje mayor ingresado por el
     * usuario.
     */
    private String porcMayor;
    /**
     * Variable que representa el consumo menor ingresado por el
     * usuario.
     */
    private String consMenor;
    /**
     * Variable que representa el consumo menor ingresado por el
     * usuario.
     */
    private String consMayor;
    /**
     * Variable que representa el consumo ingresado por el usuario.
     */
    private String consumo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Constante a nivel de clase que almacena la cadena "CODIGORUTA"
     */
    private final String codigoRutaConstante;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que se muestra en el combo Observaciones
     */
    private List<Registro> listaobservaciones;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de objetos pertenecientes al combo ciclo
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Lista de objetos pertenecientes al combo Codigo Inicial.
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista de objetos pertenecientes al combo Codigo Final.
     */
    private RegistroDataModelImpl listaCodigoFinal;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LecturaCriticaInfControlador
     */
    public LecturaCriticaInfControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoRutaConstante = GeneralParameterEnum.CODIGORUTA.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.LECTURA_CRITICA_INF_CONTROLADOR
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
        cargarListaobservaciones();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
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
        consMenor = "40";
        consMayor = "40";
        porcMenor = "65";
        porcMayor = "35";
        observaciones = "Superior al promedio";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Metodo que realiza la carga de los elementos de la lista
     * Observaciones.
     */
    public void cargarListaobservaciones() {
        try {
            listaobservaciones = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LecturaCriticaInfControladorUrlEnum.URL6694
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCiclo
     *
     * Metodo que realiza la carga de los elementos de la lista Ciclo.
     */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LecturaCriticaInfControladorUrlEnum.URL7150
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     *
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LecturaCriticaInfControladorUrlEnum.URL7690
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaConstante);
    }

    /**
     *
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LecturaCriticaInfControladorUrlEnum.URL8319
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(LecturaCriticaInfControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);
        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaConstante);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite generar el reporte con los filtros y
     * formatos seleccionados.
     *
     * @param formato
     * Formato en el cual se genera el reporte.
     */
    private void generaInforme(FORMATOS formato) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String reporte = "001155CriticaConsumoDesv";
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("observaciones", observaciones);
            reemplazar.put("txtCnsMenor", consMenor);
            reemplazar.put("txtCnsMayor", consMayor);
            reemplazar.put("txtPorMenor", porcMenor);
            reemplazar.put("txtPorMayor", porcMayor);
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LecturaCriticaInfControladorUrlEnum.URL214030
                                                                            .getValue())
                                            .getUrl(), param));
            String anio = (reg == null)
                || (reg.getCampos().get(
                                GeneralParameterEnum.ANO.getName()) == null)
                                    ? ""
                                    : reg.getCampos()
                                                    .get(GeneralParameterEnum.ANO
                                                                    .getName())
                                                    .toString();
            String periodo = (reg == null)
                || (reg.getCampos().get(
                                GeneralParameterEnum.PERIODO.getName()) == null)
                                    ? ""
                                    : reg.getCampos()
                                                    .get(GeneralParameterEnum.PERIODO
                                                                    .getName())
                                                    .toString();
            String nombrePeriodo = ejbServiciosPublicosCero
                            .asignarNombrePeriodo(compania,
                                            Integer.parseInt(anio), periodo,
                                            null);
            reemplazar.put("observaciones", observaciones);
            reemplazar.put("txtCnsMayor", consMayor);
            reemplazar.put("txtCnsMenor", consMenor);
            reemplazar.put("txtPorMayor", porcMayor);
            reemplazar.put("txtPorMenor", porcMenor);
            reemplazar.put("consumo", sitio ? consumo : "0");

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_NOMBREPERIODO", nombrePeriodo);
            parametros.put("PR_INDSITIO", sitio);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control sitio
     */
    public void cambiarIndSitio() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()),
                        "")
                        .toString();

        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(LecturaCriticaInfControladorEnum.CODIGOINICIAL
                                                        .getValue()),
                                        "")
                        .toString();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOFINAL"), "")
                        .toString();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
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
        codigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(codigoRutaConstante), "")
                        .toString();
        cargarListaCodigoFinal();
        codigoFinal = null;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(codigoRutaConstante), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable sitio
     *
     * @return sitio
     */
    public boolean getSitio() {
        return sitio;
    }

    /**
     * Asigna la variable sitio
     *
     * @param sitio
     * Variable a asignar en sitio
     */
    public void setSitio(boolean sitio) {
        this.sitio = sitio;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
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
     * Retorna la variable observaciones
     *
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Asigna la variable observaciones
     *
     * @param observaciones
     * Variable a asignar en observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Retorna la variable porcMenor
     *
     * @return porcMenor
     */
    public String getPorcMenor() {
        return porcMenor;
    }

    /**
     * Asigna la variable porcMenor
     *
     * @param porcMenor
     * Variable a asignar en porcMenor
     */
    public void setPorcMenor(String porcMenor) {
        this.porcMenor = porcMenor;
    }

    /**
     * Retorna la variable porcMayor
     *
     * @return porcMayor
     */
    public String getPorcMayor() {
        return porcMayor;
    }

    /**
     * Asigna la variable porcMayor
     *
     * @param porcMayor
     * Variable a asignar en porcMayor
     */
    public void setPorcMayor(String porcMayor) {
        this.porcMayor = porcMayor;
    }

    /**
     * Retorna la variable consMenor
     *
     * @return consMenor
     */
    public String getConsMenor() {
        return consMenor;
    }

    /**
     * Asigna la variable consMenor
     *
     * @param consMenor
     * Variable a asignar en consMenor
     */
    public void setConsMenor(String consMenor) {
        this.consMenor = consMenor;
    }

    /**
     * Retorna la variable consMayor
     *
     * @return consMayor
     */
    public String getConsMayor() {
        return consMayor;
    }

    /**
     * Asigna la variable consMayor
     *
     * @param consMayor
     * Variable a asignar en consMayor
     */
    public void setConsMayor(String consMayor) {
        this.consMayor = consMayor;
    }

    /**
     * Retorna la variable consumo
     *
     * @return consumo
     */
    public String getConsumo() {
        return consumo;
    }

    /**
     * Asigna la variable consumo
     *
     * @param consumo
     * Variable a asignar en consumo
     */
    public void setConsumo(String consumo) {
        this.consumo = consumo;
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
     * Retorna la lista listaobservaciones
     *
     * @return listaobservaciones
     */
    public List<Registro> getListaobservaciones() {
        return listaobservaciones;
    }

    /**
     * Asigna la lista listaobservaciones
     *
     * @param listaobservaciones
     * Variable a asignar en listaobservaciones
     */
    public void setListaobservaciones(List<Registro> listaobservaciones) {
        this.listaobservaciones = listaobservaciones;
    }

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
}
