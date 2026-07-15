/*-
 * FrmCaEjecucionPasControlador.java
 *
 * 1.0
 * 
 * 05/09/2017
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
import com.sysman.presupuesto.enums.FrmCaEjecucionPasControladorEnum;
import com.sysman.presupuesto.enums.FrmCaEjecucionPasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

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
 * Clase que genera el reporte de la ejecución pasiva por año, mes y cuentas
 *
 * @version 1.0, 05/09/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class  FrmCaEjecucionPasControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    
    private final String modulo;
   
    //<DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado al check para la utilizacion 
     * del codigo equivalente
     */
    private boolean indicador;
    /**
     * Atributo que contiene el valor asignado a la cuenta inicial 
     * por la cual se va a filtrar el reporte
     */
    private String cuentaInicial;
    /**
     * Atributo que contiene el valor asignado a la cuenta final 
     * por la cual se va a filtrar el reporte
     */
    private String cuentaFinal;
    /**
     * Atributo que contiene el valor asignado al mes 
     * por la cual se va a filtrar el reporte
     */
    private String mes;
    /**
     * Atributo que contiene el valor asignado al anio 
     * por la cual se va a filtrar el reporte
     */
    private String anio;
    /**
     * Atributo que contiene el valor asignado al nombre de la cuenta inicial 
     * por la cual se va a filtrar el reporte
     */
    private String nombreCuentaInicial;
    /**
     * Atributo que contiene el valor asignado al nombre de la cuenta final 
     * por la cual se va a filtrar el reporte
     */
    private String nombreCuentaFinal;
    /**
     * Atributo que contiene el valor asignado al nombre del mes 
     * por la cual se va a filtrar el reporte
     */
    private String nombreMes;
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
     * Lista que contiene los años
     */
    private List<Registro> listaAno;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del combo cuenta inicial */
    private RegistroDataModelImpl listaCuentaInicial;
    
    /** Lista que contiene los detalles del combo cuenta final */
    private RegistroDataModelImpl listaCuentaFinal;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    
    /**
     * Crea una nueva instancia de FrmCaEjecucionPasControlador
     */
    public FrmCaEjecucionPasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario=GeneralCodigoFormaEnum.FRMCAEJECUCIONPAS_CONTROLADOR.getCodigo();
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
        cargarListaAno();
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial(); 
        cargarListaCuentaFinal();
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
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        
        try {
            listaAno= RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmCaEjecucionPasControladorUrlEnum.URL13622
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
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCaEjecucionPasControladorUrlEnum.URL11959
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCaEjecucionPasControladorUrlEnum.URL12000
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
        param.put(FrmCaEjecucionPasControladorEnum.PARAM0.getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pantalla
     * en la vista
     *
     */
    public void oprimirPantalla() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;
        generarReporte(FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     */
    public void oprimirExcel() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;
        generarReporte(FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }
    
    private void generarReporte(FORMATOS formato){
        try {
            String reporte;
            if(indicador){
                reporte="001474INFCAEJECUCIONPASCodEquiv";
            }else{
                reporte="001473INFCAEJECUCIONPAS";
            }
            
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();


            reemplazar.put("compania", compania);
            reemplazar.put("anio", anio);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("mes", mes);
            
            parametros.put("PR_FIRMA1_INFORMES", consultarParametro("FIRMA1 EN INFORMES DE EJECUCION"));
            parametros.put("PR_FIRMA2_INFORMES", consultarParametro("FIRMA2 EN INFORMES DE EJECUCION"));
            parametros.put("PR_FIRMA1_RESOLUCION", consultarParametro("FIRMA1 EN RESOLUCION 036"));
            parametros.put("PR_FIRMA2_RESOLUCION", consultarParametro("FIRMA2 EN RESOLUCION 036"));
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_MES", ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes)).toUpperCase());
            parametros.put("PR_ANO", anio);

            Reporteador.resuelveConsulta("001473INFCAEJECUCIONPAS",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }catch (FileNotFoundException ex){
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
        }
        catch (JRException | IOException | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    
    public String consultarParametro(String nombre) throws SystemException{
        return ejbSysmanUtil.consultarParametro(compania, nombre, modulo, new Date(), false);
    }
    
    
    //</METODOS_BOTONES>
    
    //<METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        //<CODIGO_DESARROLLADO>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_CAMBIAR>

    
    //<METODOS_COMBOS_GRANDES>
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
        cuentaInicial= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCuentaInicial= registroAux.getCampos().get("NOMBRE").toString();
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
        cuentaFinal= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCuentaFinal = registroAux.getCampos().get("NOMBRE").toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indicador
     * 
     * @return  indicador
     */
    public boolean getIndicador() {
        return indicador;
    }
    /**
     * Asigna la variable  indicador
     * 
     * @param  indicador
     * Variable a asignar en  indicador
     */
    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }
    /**
     * Retorna la variable cuentaInicial
     * 
     * @return  cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }
    /**
     * Asigna la variable  cuentaInicial
     * 
     * @param  cuentaInicial
     * Variable a asignar en  cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }
    /**
     * Retorna la variable cuentaFinal
     * 
     * @return  cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }
    /**
     * Asigna la variable  cuentaFinal
     * 
     * @param  cuentaFinal
     * Variable a asignar en  cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
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
     * Retorna la variable nombreCuentaInicial
     * 
     * @return  nombreCuentaInicial
     */
    public String getNombreCuentaInicial() {
        return nombreCuentaInicial;
    }
    /**
     * Asigna la variable  nombreCuentaInicial
     * 
     * @param  nombreCuentaInicial
     * Variable a asignar en  nombreCuentaInicial
     */
    public void setNombreCuentaInicial(String nombreCuentaInicial) {
        this.nombreCuentaInicial = nombreCuentaInicial;
    }
    /**
     * Retorna la variable nombreCuentaFinal
     * 
     * @return  nombreCuentaFinal
     */
    public String getNombreCuentaFinal() {
        return nombreCuentaFinal;
    }
    /**
     * Asigna la variable  nombreCuentaFinal
     * 
     * @param  nombreCuentaFinal
     * Variable a asignar en  nombreCuentaFinal
     */
    public void setNombreCuentaFinal(String nombreCuentaFinal) {
        this.nombreCuentaFinal = nombreCuentaFinal;
    }
    /**
     * Retorna la variable nombreMes
     * 
     * @return  nombreMes
     */
    public String getNombreMes() {
        return nombreMes;
    }
    /**
     * Asigna la variable  nombreMes
     * 
     * @param  nombreMes
     * Variable a asignar en  nombreMes
     */
    public void setNombreMes(String nombreMes) {
        this.nombreMes = nombreMes;
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
    public List<Registro> getListaAno() {
        return listaAno;
    }
    
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
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
     * Variable a asignar en  listaCuentaInicial
     */
    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
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
     * Variable a asignar en  listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
