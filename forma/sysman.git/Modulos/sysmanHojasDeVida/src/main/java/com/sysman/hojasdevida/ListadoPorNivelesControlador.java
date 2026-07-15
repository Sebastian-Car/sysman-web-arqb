/*-
 * ListadoPorNivelesControlador.java
 *
 * 1.0
 * 
 * 24/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.ListadoPorNivelesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
 *  Clase que permite obtener el informe o reporte calificaciones definitivas
 *  por niveles  
 *
 * @version 1.0, 24/02/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class  ListadoPorNivelesControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    //<DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado a la carpeta inicial en la
     * forma del formulario.
     */
    private String carpetaInicial;
    /**
     * Atributo que contiene el valor asignado a la carpeta final en la
     * forma del formulario.
     */
    private String carpetaFinal;
    
    /**
     * Atributo que contiene el valor asignado a la cedula inicial en la
     * forma del formulario.
     */
    private String cedulaInicial;
    /**
     * Atributo que contiene el valor asignado a la cedula final en la
     * forma del formulario.
     */
    private String cedulaFinal;
    /**
     * Atributo que contiene el valor asignado a fecha inicial en la
     * forma del formulario.
     */
    private Date fechaInicial;
    /**
     * Atributo que contiene el valor asignado a la fecha final en la
     * forma del formulario.
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo Carpeta Inicial.
     */
    private RegistroDataModelImpl listaCmbCarpetaInicial;
    /**
     * Lista que contiene los detalles del combo Carpeta Final.
     */
    private RegistroDataModelImpl listaCmbCarpetaFinal;
    
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;
    /**
     * Crea una nueva instancia de ListadoPorNivelesControlador
     */
    public ListadoPorNivelesControlador() {
        super();
        compania = SessionUtil.getCompania();
        fechaInicial = fechaFinal = new Date();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_POR_NIVELES_CONTROLADOR
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
    public void inicializar(){
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbCarpetaInicial(); 
        cargarListaCmbCarpetaFinal(); 
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
        abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCmbCarpetaInicial
     *
     */
    public void cargarListaCmbCarpetaInicial(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoPorNivelesControladorUrlEnum.URL6502
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCmbCarpetaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());
    }
    /**
     * 
     * Carga la lista listaCmbCarpetaFinal
     *
     */
    public void cargarListaCmbCarpetaFinal(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoPorNivelesControladorUrlEnum.URL8754
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),cedulaInicial);

        listaCmbCarpetaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());
    }
    
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdPrint
     * en la vista
     *
     *
     */
    public void oprimirCmdPrint() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;
        generarReporte(FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }
    
    
    
    private void generarReporte(FORMATOS formato) {
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte ="001425RptPromEval";
            ejbHojasDeVidaCero.actualizarConsecutivoEvaluacion(compania,
                            cedulaInicial, cedulaFinal, fechaInicial,
                            fechaFinal, SessionUtil.getUser().getCodigo());
            reemplazar.put("compania", compania);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("cedulaInicial", cedulaInicial);
            reemplazar.put("cedulaFinal", cedulaFinal);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            (HashMap<String, Object>) reemplazar,
                            (HashMap<String, Object>) parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCarpetaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCarpetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedulaInicial = registroAux.getCampos().get(GeneralParameterEnum.NUMERO_DCTO.getName()).toString();
        carpetaInicial = registroAux.getCampos().get("NUMEROCARPETA").toString();
        carpetaFinal= cedulaFinal=null;
        cargarListaCmbCarpetaFinal();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCarpetaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCarpetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedulaFinal = registroAux.getCampos().get(GeneralParameterEnum.NUMERO_DCTO.getName()).toString();
        carpetaFinal = registroAux.getCampos().get("NUMEROCARPETA").toString();
    }

   
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable carpetaInicial
     * 
     * @return  carpetaInicial
     */
    public String getCarpetaInicial() {
        return carpetaInicial;
    }
    /**
     * Asigna la variable  carpetaInicial
     * 
     * @param  carpetaInicial
     * Variable a asignar en  carpetaInicial
     */
    public void setCarpetaInicial(String carpetaInicial) {
        this.carpetaInicial = carpetaInicial;
    }
    /**
     * Retorna la variable carpetaFinal
     * 
     * @return  carpetaFinal
     */
    public String getCarpetaFinal() {
        return carpetaFinal;
    }
    /**
     * Asigna la variable  carpetaFinal
     * 
     * @param  carpetaFinal
     * Variable a asignar en  carpetaFinal
     */
    public void setCarpetaFinal(String carpetaFinal) {
        this.carpetaFinal = carpetaFinal;
    }
    
    
    /**
     * Retorna la variable cedulaInicial
     * 
     * @return  cedulaInicial
     */
    public String getCedulaInicial() {
        return cedulaInicial;
    }
    /**
     * Asigna la variable  cedulaInicial
     * 
     * @param  cedulaInicial
     * Variable a asignar en  cedulaInicial
     */
    public void setCedulaInicial(String cedulaInicial) {
        this.cedulaInicial = cedulaInicial;
    }
    /**
     * Retorna la variable cedulaFinal
     * 
     * @return  cedulaFinal
     */
    public String getCedulaFinal() {
        return cedulaFinal;
    }
    /**
     * Asigna la variable  cedulaFinal
     * 
     * @param  cedulaFinal
     * Variable a asignar en  cedulaFinal
     */
    public void setCedulaFinal(String cedulaFinal) {
        this.cedulaFinal = cedulaFinal;
    }
    /**
     * Retorna la variable fechaInicial
     * 
     * @return  fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }
    /**
     * Asigna la variable  fechaInicial
     * 
     * @param  fechaInicial
     * Variable a asignar en  fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }
    /**
     * Retorna la variable fechaFinal
     * 
     * @return  fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }
    /**
     * Asigna la variable  fechaFinal
     * 
     * @param  fechaFinal
     * Variable a asignar en  fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }
    
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    
    
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaCmbCarpetaInicial
     * 
     * @return listaCmbCarpetaInicial
     */
    public RegistroDataModelImpl getListaCmbCarpetaInicial() {
        return listaCmbCarpetaInicial;
    }
    /**
     * Asigna la lista listaCmbCarpetaInicial
     * 
     * @param listaCmbCarpetaInicial
     * Variable a asignar en  listaCmbCarpetaInicial
     */
    public void setListaCmbCarpetaInicial(RegistroDataModelImpl listaCmbCarpetaInicial) {
        this.listaCmbCarpetaInicial = listaCmbCarpetaInicial;
    }
    /**
     * Retorna la lista listaCmbCarpetaFinal
     * 
     * @return listaCmbCarpetaFinal
     */
    public RegistroDataModelImpl getListaCmbCarpetaFinal() {
        return listaCmbCarpetaFinal;
    }
    /**
     * Asigna la lista listaCmbCarpetaFinal
     * 
     * @param listaCmbCarpetaFinal
     * Variable a asignar en  listaCmbCarpetaFinal
     */
    public void setListaCmbCarpetaFinal(RegistroDataModelImpl listaCmbCarpetaFinal) {
        this.listaCmbCarpetaFinal = listaCmbCarpetaFinal;
    }
    //</SET_GET_LISTAS_COMBO_GRANDE>
    
}
