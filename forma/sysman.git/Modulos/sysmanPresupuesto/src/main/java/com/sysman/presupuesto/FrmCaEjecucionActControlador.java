/*-
 * FrmCaEjecucionActControlador.java
 *
 * 1.0
 * 
 * 5/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto;

import java.io.IOException;
import java.util.Date;
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

import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
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
import com.sysman.presupuesto.enums.FrmCaEjecucionActControladorEnum;
import com.sysman.presupuesto.enums.FrmCaEjecucionActControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;

/**
 * Formulario que genera el reporte de la ejecución activa por año,
 * mes y cuentas
 *
 * @version 1.0, 05/09/2017, Proceso de Migracion ACCESS a WEB,
 * refactoring DSS y manejo de EJBs
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmCaEjecucionActControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase el numero de modulo en el cual
     * inicio sesion el usuario
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el numero de mes seleccionado en la vista
     */
    private String mes;
    /**
     * Variable que almacena el numero de anio seleccionado en la
     * vista
     */
    private String anio;
    /**
     * Variable que almacena el nombre del rubro inicial
     */
    private String nombreCuentaInicial;
    /**
     * Variable que almacena el nombre del rubro final
     */
    private String nombreCuentaFinal;
    /**
     * Variable que almacena el numero del rubro inicial
     */
    private String cuentaInicial;
    /**
     * Variable que almacena el nombre del rubro final
     */
    private String cuentaFinal;

    /**
     * Variable que almacena el valor del indicador de codigo
     * equivalente de la vista
     */
    private boolean codigoEquivalente;

    /**
     * Variable que almacena el valor del parametro FIRMA1 EN INFORMES
     * DE EJECUCION
     */
    private String firmaUnoInfEjec;
    /**
     * Variable que almacena el valor del parametro FIRMA2 EN INFORMES
     * DE EJECUCION
     */

    private String firmaDosInfEjec;
    /**
     * Variable que almacena el valor del parametro FIRMA1 EN
     * RESOLUCION 036
     */
    private String firmaUnoRes;
    /**
     * Variable que almacena el valor del parametro FIRMA1 EN
     * RESOLUCION 036
     */
    private String firmaDosRes;

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
     * Lista que despliega los meses
     */
    private List<Registro> listames;
    /**
     * Lista que despliega los anios
     */
    private List<Registro> listaanio;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que despiebla los rubros presupuestales iniciales
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * Lista que despiebla los rubros presupuestales finales
     */
    private RegistroDataModelImpl listaCuentaFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmCaEjecucionActControlador
     */
    public FrmCaEjecucionActControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = Integer.toString(SysmanFunciones.mes(new Date()));
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_CAEJECUCION_ACT_CONTROLADOR
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

        cargarListaanio();
        cargarListames();
        cargarListaCuentaInicial();
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
        try {
            firmaUnoInfEjec = ejbSysmanUtl.consultarParametro(compania,
                            "FIRMA1 EN INFORMES DE EJECUCION", modulo,
                            new Date(),
                            false);

            firmaDosInfEjec = ejbSysmanUtl.consultarParametro(compania,
                            "FIRMA2 EN INFORMES DE EJECUCION", modulo,
                            new Date(),
                            false);

            firmaUnoRes = ejbSysmanUtl.consultarParametro(compania,
                            "FIRMA1 EN RESOLUCION 036", modulo, new Date(),
                            false);

            firmaDosRes = ejbSysmanUtl.consultarParametro(compania,
                            "FIRMA2 EN RESOLUCION 036", modulo, new Date(),
                            false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaanio
     *
     */
    public void cargarListaanio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCaEjecucionActControladorUrlEnum.URL9461
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
     * Carga la lista listames
     *
     */
    public void cargarListames() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        try {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCaEjecucionActControladorUrlEnum.URL9032
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
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCaEjecucionActControladorUrlEnum.URL7610
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "C");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
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
                                        FrmCaEjecucionActControladorUrlEnum.URL8643
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        param.put(FrmCaEjecucionActControladorEnum.CODIGOINICIAL.getValue(),
                        cuentaInicial);

        param.put(GeneralParameterEnum.NATURALEZA.getName(), "C");

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    private void generarReporte(FORMATOS formato) {
        archivoDescarga = null;
        String reporte;
        try {

            if (codigoEquivalente) {
                reporte = "001472INFCAEJECUCIONACTCodEquiv";
            }
            else {
                reporte = "001471INFCAEJECUCIONACT";
            }

            Map<String, Object> reemplazos = new TreeMap<>();
            reemplazos.put("compania", compania);
            reemplazos.put("mes", mes);
            reemplazos.put("anio", anio);
            reemplazos.put("cuentaInicial", cuentaInicial);
            reemplazos.put("cuentaFinal", cuentaFinal);

            Map<String, Object> parametros = new TreeMap<>();
            parametros.put("PR_ANO", anio);

            parametros.put("PR_MES",
                            ejbSysmanUtl.mostrarNombreDeMes(
                                            Integer.parseInt(mes)));

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_FIRMA1_EN_INFORMES_DE_EJECUCION",
                            firmaUnoInfEjec);

            parametros.put("PR_FIRMA2_EN_INFORMES_DE_EJECUCION",
                            firmaDosInfEjec);

            parametros.put("PR_FIRMA1_EN_RESOLUCION_036", firmaUnoRes);

            parametros.put("PR_FIRMA2_EN_RESOLUCION_036", firmaDosRes);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton PDF en la vista
     *
     */
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control anio
     * 
     */
    public void cambiaranio() {
        // <CODIGO_DESARROLLADO>

        cargarListames();
        cargarListaCuentaInicial();
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
        cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreCuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
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
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreCuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoEquivalente
     * 
     * @return codigoEquivalente
     */

    public boolean isCodigoEquivalente() {
        return codigoEquivalente;
    }

    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCodigoEquivalente(boolean codigoEquivalente) {
        this.codigoEquivalente = codigoEquivalente;
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

    /**
     * Retorna la variable nombreCuentaInicial
     * 
     * @return nombreCuentaInicial
     */
    public String getNombreCuentaInicial() {
        return nombreCuentaInicial;
    }

    /**
     * Asigna la variable nombreCuentaInicial
     * 
     * @param nombreCuentaInicial
     * Variable a asignar en nombreCuentaInicial
     */
    public void setNombreCuentaInicial(String nombreCuentaInicial) {
        this.nombreCuentaInicial = nombreCuentaInicial;
    }

    /**
     * Retorna la variable nombreCuentaFinal
     * 
     * @return nombreCuentaFinal
     */
    public String getNombreCuentaFinal() {
        return nombreCuentaFinal;
    }

    /**
     * Asigna la variable nombreCuentaFinal
     * 
     * @param nombreCuentaFinal
     * Variable a asignar en nombreCuentaFinal
     */
    public void setNombreCuentaFinal(String nombreCuentaFinal) {
        this.nombreCuentaFinal = nombreCuentaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getFirmaUnoInfEjec() {
        return firmaUnoInfEjec;
    }

    public void setFirmaUnoInfEjec(String firmaUnoInfEjec) {
        this.firmaUnoInfEjec = firmaUnoInfEjec;
    }

    public String getFirmaDosInfEjec() {
        return firmaDosInfEjec;
    }

    public void setFirmaDosInfEjec(String firmaDosInfEjec) {
        this.firmaDosInfEjec = firmaDosInfEjec;
    }

    public String getFirmaUnoRes() {
        return firmaUnoRes;
    }

    public void setFirmaUnoRes(String firmaUnoRes) {
        this.firmaUnoRes = firmaUnoRes;
    }

    public String getFirmaDosRes() {
        return firmaDosRes;
    }

    public void setFirmaDosRes(String firmaDosRes) {
        this.firmaDosRes = firmaDosRes;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listames
     * 
     * @return listames
     */
    public List<Registro> getListames() {
        return listames;
    }

    /**
     * Asigna la lista listames
     * 
     * @param listames
     * Variable a asignar en listames
     */
    public void setListames(List<Registro> listames) {
        this.listames = listames;
    }

    /**
     * Retorna la lista listaanio
     * 
     * @return listaanio
     */
    public List<Registro> getListaanio() {
        return listaanio;
    }

    /**
     * Asigna la lista listaanio
     * 
     * @param listaanio
     * Variable a asignar en listaanio
     */
    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
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
}
