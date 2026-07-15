/*-
 * SuscriptorubicacionsControlador.java
 *
 * 1.0
 *
 * 03/11/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.SuscriptorubicacionsControladorEnum;
import com.sysman.serviciospublicos.enums.SuscriptorubicacionsControladorUrlEnum;

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
 * Clase migrada para generar el informe de los suscriptores por
 * ubicacion
 *
 * @version 1.0, 03/11/2016
 * @author ybecerra
 * 
 * @author asana
 * @version 2.0 16/06/2017
 * Se realiza refactoring
 */
@ManagedBean
@ViewScoped
public class SuscriptorubicacionsControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa en la aplicacion
     */
    private final String modulo;

    /**
     * Constante definida para almacenar la cadena "CODIGO" se llama
     * en los metodos cargarlistaUsoIni , cargarListaUsoFin,
     * seleccionarFilaUsoIni,seleccionarFilaUsoFin
     */
    private final String cod;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo seleccionado en el combo ciclo
     * del formulario
     */
    private String ciclo;
    /**
     * Atributo que almacena el codigo seleccionado de uso del combo
     * uso inicial del formulario
     */
    private String usoInicial;
    /**
     * Atributo que almacena el codigo seleccionado del uso del combo
     * uso final del formulario
     */
    private String usoFinal;
    /**
     * Atributo que almacena el codigo del periodo seleccionado en el
     * combo periodo del formulario
     */
    private String periodo;
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
     * Atributo de la clase que almacena la lista del combo ciclo del
     * formulario
     */
    private List<Registro> listaCiclo;
    /**
     * Atributo de la clase que almacena la lista del combo periodo
     * del formulario
     */
    private List<Registro> listaperiodo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo de la clase que almacena los registros del combo uso
     * inicial del formulario
     */
    private RegistroDataModelImpl listaUsoIni;
    /**
     * Atributo de la clase que almacena los registros del combo uso
     * final del formulario
     */

    private RegistroDataModelImpl listaUsoFin;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCeroRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SuscriptorubicacionsControlador
     */
    public SuscriptorubicacionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cod = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.SUSCRIPTORUBICACIONS_CONTROLADOR
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
        cargarListaCiclo();
        cargarListaUsoIni();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
        /*
         * FR1178-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name
         * Forms!IDENTIFICACION!ANO = Year(Date) End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SuscriptorubicacionsControladorUrlEnum.URL6037
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
     * Carga la lista listaUsoIni
     *
     */
    public void cargarListaUsoIni() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SuscriptorubicacionsControladorUrlEnum.URL6416
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaUsoIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    /**
     *
     * Carga la lista listaUsoFin
     *
     */
    public void cargarListaUsoFin() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SuscriptorubicacionsControladorUrlEnum.URL7086
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SuscriptorubicacionsControladorEnum.USOINICIAL.getValue(),
                        usoInicial);

        listaUsoFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    /**
     *
     * Carga la lista listaperiodo
     *
     */
    public void cargarListaperiodo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SuscriptorubicacionsControladorEnum.CICLO.getValue(), ciclo);

        try {
            listaperiodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SuscriptorubicacionsControladorUrlEnum.URL7592
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    public void generarInforme(ReportesBean.FORMATOS formato) {

        String reporte = "001213UbSuscriptores";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", "'" + ciclo + "'");
            reemplazar.put("ano", service.buscarEnListaObj(periodo, "PERIODO",
                            "ANO", listaperiodo));
            reemplazar.put("periodo", "'" + periodo + "'");
            reemplazar.put("usoInicial", "'" + usoInicial + "'");
            reemplazar.put("usoFinal", "'" + usoFinal + "'");

            String nombrePeriodo = ejbServiciosPublicosCeroRemote
                            .asignarNombrePeriodo(compania, Integer.parseInt(
                                            service.buscarEnListaObj(periodo,
                                                            "PERIODO",
                                                            "ANO", listaperiodo)
                                                            .toString()),
                                            periodo, "");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBREPERIODO", nombrePeriodo);

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Ciclo
     *
     *
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaperiodo();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsoIni
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUsoIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usoInicial = registroAux.getCampos().get(cod).toString();
        usoFinal = null;
        cargarListaUsoFin();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsoFin
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUsoFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usoFinal = registroAux.getCampos().get(cod).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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
     * Retorna la variable usoInicial
     *
     * @return usoInicial
     */
    public String getUsoInicial() {
        return usoInicial;
    }

    /**
     * Asigna la variable usoInicial
     *
     * @param usoInicial
     * Variable a asignar en usoInicial
     */
    public void setUsoInicial(String usoInicial) {
        this.usoInicial = usoInicial;
    }

    /**
     * Retorna la variable usoFinal
     *
     * @return usoFinal
     */
    public String getUsoFinal() {
        return usoFinal;
    }

    /**
     * Asigna la variable usoFinal
     *
     * @param usoFinal
     * Variable a asignar en usoFinal
     */
    public void setUsoFinal(String usoFinal) {
        this.usoFinal = usoFinal;
    }

    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
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
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaperiodo
     *
     * @return listaperiodo
     */
    public List<Registro> getListaperiodo() {
        return listaperiodo;
    }

    /**
     * Asigna la lista listaperiodo
     *
     * @param listaperiodo
     * Variable a asignar en listaperiodo
     */
    public void setListaperiodo(List<Registro> listaperiodo) {
        this.listaperiodo = listaperiodo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaUsoIni() {
        return listaUsoIni;
    }

    public RegistroDataModelImpl getListaUsoFin() {
        return listaUsoFin;
    }

    public void setListaUsoFin(RegistroDataModelImpl listaUsoFin) {
        this.listaUsoFin = listaUsoFin;
    }

    public void setListaUsoIni(RegistroDataModelImpl listaUsoIni) {
        this.listaUsoIni = listaUsoIni;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
