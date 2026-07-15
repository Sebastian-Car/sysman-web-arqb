/*-
 * DepreciarComponenteControlador.java
 *
 * 1.0
 * 
 * 24/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import com.sysman.almacen.enums.DepreciarComponenteControladorUrlEnum;
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
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
/**
 * Genera reporte de depreciación por componente.
 *
 * @version 1.0, 24/09/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  DepreciarComponenteControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    //<DECLARAR_ATRIBUTOS>
    private String anio;
    private String mes;
    private String componenteInicial;
    private String componenteFinal;
    private String componenteNombreInicial;
    private String componenteNombreFinal;
    private boolean detallado;
    private String modulo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    /**
     * lista año
     */
    private List<Registro> listaanio;
    /**
     * lista mes
     */
    private List<Registro> listames;
    /**
     * lista componente inicial
     */
    private RegistroDataModelImpl listacomponenteFinal;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista componente final
     */
    private RegistroDataModelImpl listacomponenteInicial;
    
    @EJB
    private EjbSysmanUtil ejbSysmanUtil;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de DepreciarComponenteControlador
     */
    public DepreciarComponenteControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.DEPRECIARCOMPONENTES_CONTROLADOR.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
        cargarListaanio();
        cargarListames();
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListacomponenteInicial();
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
     * Carga la lista listaanio
     *
     */
    public void cargarListaanio(){
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DepreciarComponenteControladorUrlEnum.URL3271
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            Logger.getLogger(DepreciacionMesDependenciaControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     * Carga la lista listames
     *
     */
    public void cargarListames(){
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DepreciarComponenteControladorUrlEnum.URL3272
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            Logger.getLogger(DepreciacionMesDependenciaControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }
    
    public void cambiaranio() {
        cargarListames();
    }
    /**
     * 
     * Carga la lista listacomponenteFinal
     */
    public void cargarListacomponenteFinal(){
        
        
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepreciarComponenteControladorUrlEnum.URL3273
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOINICIAL", componenteInicial);

        listacomponenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());
        
    }
    /**
     * 
     * Carga la lista listacomponenteInicial
     *
     */
    public void cargarListacomponenteInicial(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepreciarComponenteControladorUrlEnum.URL3270
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacomponenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());
        
        
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     */
    public void oprimirExcel() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;  
        
        generarInforme(FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * en la vista
     *
     */
    public void oprimirAceptar() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;  
        
        generarInforme(FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacomponenteInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacomponenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        componenteInicial= SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()), "").toString();
        componenteNombreInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
        cargarListacomponenteFinal();
    }
    public void seleccionarFilacomponenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        componenteFinal= SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()), "").toString();
        componenteNombreFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
    }
    
    //</METODOS_COMBOS_GRANDES>
    
    public void generarInforme(FORMATOS formato) {
        
        try {
            
            
            
            String consulta;
            
            consulta = detallado? "001897DepreciarMesComponentesGrupoNIIF" : "001911DepreciarMesComponentes";
            
            String ultimoDia = SysmanFunciones.convertirAFechaCadena(
                            SysmanFunciones.ultimoDiaDate(SysmanFunciones
                                            .convertirAFecha("01/" + mes + "/"
                                                + anio)),
                            "dd/MM/yyyy");
            
        Map<String, Object> reemplazar = new HashMap<>();
        
        reemplazar.put("compania", compania);
        reemplazar.put("elementoInicial", componenteInicial);
        reemplazar.put("elementoFinal", componenteFinal);
        reemplazar.put("ultimoDia", ultimoDia);
        reemplazar.put("agrupacion", ejbSysmanUtil.consultarParametro(compania,
                        "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),true));
        
        String sql = Reporteador.resuelveConsulta(consulta, Integer.parseInt(modulo), reemplazar);
        
        Map<String, Object> parametro = new HashMap<>();
        
        parametro.put("PR_NOMBRE_CONTADOR", ejbSysmanUtil.consultarParametro(compania,
                        "NOMBRE CONTADOR", modulo, new Date(),
                        true));
        parametro.put("PR_ALMACENISTA", ejbSysmanUtil.consultarParametro(compania,
                        "ALMACENISTA", modulo, new Date(),
                        true));
        parametro.put("PR_MES", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                                            .parseInt(mes)].toUpperCase());
        parametro.put("PR_ANO", anio);
        parametro.put("PR_STRSQL", sql);
        
        archivoDescarga = JsfUtil.exportarStreamed(consulta, parametro, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | SystemException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return  anio
     */
    public String getAnio() {
        return anio;
    }
    /**
     * Asigna la variable  anio
     * 
     * @param  anio
     * Variable a asignar en  anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }
    /**
     * Retorna la variable mes
     * 
     * @return  mes
     */
    public String getMes() {
        return mes;
    }
    /**
     * Asigna la variable  mes
     * 
     * @param  mes
     * Variable a asignar en  mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }
    /**
     * Retorna la variable componenteInicial
     * 
     * @return  componenteInicial
     */
    public String getComponenteInicial() {
        return componenteInicial;
    }
    /**
     * Asigna la variable  componenteInicial
     * 
     * @param  componenteInicial
     * Variable a asignar en  componenteInicial
     */
    public void setComponenteInicial(String componenteInicial) {
        this.componenteInicial = componenteInicial;
    }
    /**
     * Retorna la variable componenteFinal
     * 
     * @return  componenteFinal
     */
    public String getComponenteFinal() {
        return componenteFinal;
    }
    /**
     * Asigna la variable  componenteFinal
     * 
     * @param  componenteFinal
     * Variable a asignar en  componenteFinal
     */
    public void setComponenteFinal(String componenteFinal) {
        this.componenteFinal = componenteFinal;
    }
    /**
     * Retorna la variable componenteNombreInicial
     * 
     * @return  componenteNombreInicial
     */
    public String getComponenteNombreInicial() {
        return componenteNombreInicial;
    }
    /**
     * Asigna la variable  componenteNombreInicial
     * 
     * @param  componenteNombreInicial
     * Variable a asignar en  componenteNombreInicial
     */
    public void setComponenteNombreInicial(String componenteNombreInicial) {
        this.componenteNombreInicial = componenteNombreInicial;
    }
    /**
     * Retorna la variable componenteNombreFinal
     * 
     * @return  componenteNombreFinal
     */
    public String getComponenteNombreFinal() {
        return componenteNombreFinal;
    }
    /**
     * Asigna la variable  componenteNombreFinal
     * 
     * @param  componenteNombreFinal
     * Variable a asignar en  componenteNombreFinal
     */
    public void setComponenteNombreFinal(String componenteNombreFinal) {
        this.componenteNombreFinal = componenteNombreFinal;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
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
     * Variable a asignar en  listaanio
     */
    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }
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
     * Variable a asignar en  listames
     */
    public void setListames(List<Registro> listames) {
        this.listames = listames;
    }
    /**
     * Retorna la lista listacomponenteFinal
     * 
     * @return listacomponenteFinal
     */
    public RegistroDataModelImpl getListacomponenteFinal() {
        return listacomponenteFinal;
    }
    /**
     * Asigna la lista listacomponenteFinal
     * 
     * @param listacomponenteFinal
     * Variable a asignar en  listacomponenteFinal
     */
    public void setListacomponenteFinal(RegistroDataModelImpl listacomponenteFinal) {
        this.listacomponenteFinal = listacomponenteFinal;
    }
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listacomponenteInicial
     * 
     * @return listacomponenteInicial
     */
    public RegistroDataModelImpl getListacomponenteInicial() {
        return listacomponenteInicial;
    }
    /**
     * Asigna la lista listacomponenteInicial
     * 
     * @param listacomponenteInicial
     * Variable a asignar en  listacomponenteInicial
     */
    public void setListacomponenteInicial(RegistroDataModelImpl listacomponenteInicial) {
        this.listacomponenteInicial = listacomponenteInicial;
    }
    public boolean isDetallado() {
        return detallado;
    }
    public void setDetallado(boolean detallado) {
        this.detallado = detallado;
    }
    
    
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
