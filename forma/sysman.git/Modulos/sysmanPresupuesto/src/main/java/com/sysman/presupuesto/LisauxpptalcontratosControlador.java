/*-
 * LisauxpptalcontratosControlador.java
 *
 * 1.0
 * 
 * 13/12/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.ActsaldodispptalControladorUrlEnum;
import com.sysman.presupuesto.enums.LisauxpptalcontratosControladorUrlEnum;
import com.sysman.presupuesto.enums.LisauxpptalcuentasControladorUrlEnum;
import com.sysman.presupuesto.enums.LisauxpptalfuenterecursosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 13/12/2023
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  LisauxpptalcontratosControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    
    private String cuentaInicial;
    
    private String cuentaFinal;
    
    private String terceroInicial;
    
    private String terceroFinal;
    
    private String tipoContrato;
    
    private String contratoInicial;
    
    private String contratoFinal;
    
    private Date fechaInicial;
    
    private Date fechaFinal;

    private int anio;

    private RegistroDataModelImpl listaCuentaInicial;
    
    private RegistroDataModelImpl listaCuentaFinal;
    
    private RegistroDataModelImpl listaTipoContrato;
    
    private List<Registro> listaNumeroContratoInicial;
    
    private List<Registro> listaNumeroContratoFinal;
    
    private RegistroDataModelImpl listaTerceroInicial;
    
    private RegistroDataModelImpl listaTerceroFinal;
    private String codigo;
    private StreamedContent archivoDescarga;
    /**
     * Crea una nueva instancia de LisauxpptalcontratosControlador
     */
    public LisauxpptalcontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario= GeneralCodigoFormaEnum.LISAUXPPTALCUENTAS_CONTROLADOR
                    .getCodigo();
            fechaInicial = new Date();
            fechaFinal = new Date();
            anio = SysmanFunciones.ano(fechaInicial);
            codigo = "CODIGO";
            validarPermisos();
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
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
        
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaTipoContrato();
        cargarListaTerceroInicial(); cargarListaTerceroFinal();
        abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
    	
    }
    
    public void cargarListaCuentaInicial(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        LisauxpptalcontratosControladorUrlEnum.URL45014
                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                2022);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, codigo);
    }
    
    
    
    public void cargarListaCuentaFinal(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        LisauxpptalcontratosControladorUrlEnum.URL45014
                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                2022);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, codigo);
    }
    
    
    public void cargarListaTipoContrato(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        LisauxpptalcontratosControladorUrlEnum.URL73010
                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);


        listaTipoContrato = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, codigo);
        
    }
    
    
    public void cargarListaNumeroContratoInicial(){

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TERCEROINI", terceroInicial);
        param.put("TERCEROFIN", terceroFinal);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);


        try {
            listaNumeroContratoInicial = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                    LisauxpptalcontratosControladorUrlEnum.URL82120
                                    .getValue())
                            .getUrl(), param));
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void cargarListaNumeroContratoFinal(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TERCEROINI", terceroInicial);
        param.put("TERCEROFIN", terceroFinal);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);


        try {
            listaNumeroContratoFinal = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                    LisauxpptalcontratosControladorUrlEnum.URL82120
                                    .getValue())
                            .getUrl(), param));
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void cargarListaTerceroInicial(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        LisauxpptalcontratosControladorUrlEnum.URL14008
                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);


        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, "NIT");
        cargarListaNumeroContratoInicial();
        cargarListaNumeroContratoFinal();
    }


    
    public void cargarListaTerceroFinal(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        LisauxpptalcontratosControladorUrlEnum.URL14008
                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);


        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, "NIT");
        cargarListaNumeroContratoInicial();
        cargarListaNumeroContratoFinal();
    }
    
    
    
    public void oprimirImprimir() {
        archivoDescarga=null; 
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    private void generarInforme(FORMATOS pdf) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        try {
            reemplazar.put("fechainicial",formatoFecha.format(fechaInicial));
            reemplazar.put("fechafinal",formatoFecha.format(fechaFinal));
            reemplazar.put("numerocontratoinicial", contratoInicial);
            reemplazar.put("numerocontratofinal", contratoFinal);
            reemplazar.put("tipocontrato", tipoContrato);
            reemplazar.put("cuentainicial", cuentaInicial);
            reemplazar.put("cuentafinal", cuentaFinal);
            reemplazar.put("terceroinicial", terceroInicial);
            reemplazar.put("tercerofinal", terceroFinal);
            reemplazar.put("compania", compania);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAFINAL", cuentaFinal);
            parametros.put("PR_FECHAFINAL",
                    SysmanFunciones.convertirAFechaCadena(fechaFinal));         
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                    .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_TERCEROINICIAL", terceroInicial);
            parametros.put("PR_TERCEROFINAL", terceroFinal);            

            String nombreInforme = "002523lisAuxPptalContratos";
            Reporteador.resuelveConsulta(nombreInforme,
                    Integer.valueOf(SessionUtil.getModulo()), reemplazar, parametros);

            try {
                archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, parametros,
                        ConectorPool.ESQUEMA_SYSMAN, pdf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial= registroAux.getCampos().get("NIT").toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal= registroAux.getCampos().get("NIT").toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial= registroAux.getCampos().get(codigo).toString();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal= registroAux.getCampos().get(codigo).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoContrato
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoContrato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContrato = registroAux.getCampos().get("CODIGO").toString();
        
        cargarListaNumeroContratoInicial();
        cargarListaNumeroContratoFinal();
    }
    
    public void cambiarNumeroContratoInicial() {
        //<CODIGO_DESARROLLADO>
       //</CODIGO_DESARROLLADO>
   }
    
    public void cambiarNumeroContratoFinal() {
        //<CODIGO_DESARROLLADO>
       //</CODIGO_DESARROLLADO>
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
     * Retorna la variable terceroInicial
     * 
     * @return  terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }
    /**
     * Asigna la variable  terceroInicial
     * 
     * @param  terceroInicial
     * Variable a asignar en  terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }
    /**
     * Retorna la variable terceroFinal
     * 
     * @return  terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }
    /**
     * Asigna la variable  terceroFinal
     * 
     * @param  terceroFinal
     * Variable a asignar en  terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }
    /**
     * Retorna la variable tipoContrato
     * 
     * @return  tipoContrato
     */
    public String getTipoContrato() {
        return tipoContrato;
    }
    /**
     * Asigna la variable  tipoContrato
     * 
     * @param  tipoContrato
     * Variable a asignar en  tipoContrato
     */
    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
    /**
     * Retorna la variable contratoInicial
     * 
     * @return  contratoInicial
     */
    public String getContratoInicial() {
        return contratoInicial;
    }
    /**
     * Asigna la variable  contratoInicial
     * 
     * @param  contratoInicial
     * Variable a asignar en  contratoInicial
     */
    public void setContratoInicial(String contratoInicial) {
        this.contratoInicial = contratoInicial;
    }
    /**
     * Retorna la variable contratoFinal
     * 
     * @return  contratoFinal
     */
    public String getContratoFinal() {
        return contratoFinal;
    }
    /**
     * Asigna la variable  contratoFinal
     * 
     * @param  contratoFinal
     * Variable a asignar en  contratoFinal
     */
    public void setContratoFinal(String contratoFinal) {
        this.contratoFinal = contratoFinal;
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
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
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
    /**
     * Retorna la lista listaTipoContrato
     * 
     * @return listaTipoContrato
     */
    public RegistroDataModelImpl getListaTipoContrato() {
        return listaTipoContrato;
    }
    /**
     * Asigna la lista listaTipoContrato
     * 
     * @param listaTipoContrato
     * Variable a asignar en  listaTipoContrato
     */
    public void setListaTipoContrato(RegistroDataModelImpl listaTipoContrato) {
        this.listaTipoContrato = listaTipoContrato;
    }
    /**
     * Retorna la lista listaNumeroContratoInicial
     * 
     * @return listaNumeroContratoInicial
     */
    public List<Registro> getListaNumeroContratoInicial() {
        return listaNumeroContratoInicial;
    }
    /**
     * Asigna la lista listaNumeroContratoInicial
     * 
     * @param listaNumeroContratoInicial
     * Variable a asignar en  listaNumeroContratoInicial
     */
    public void setListaNumeroContratoInicial(List<Registro> listaNumeroContratoInicial) {
        this.listaNumeroContratoInicial = listaNumeroContratoInicial;
    }
    /**
     * Retorna la lista listaNumeroContratoFinal
     * 
     * @return listaNumeroContratoFinal
     */
    public List<Registro> getListaNumeroContratoFinal() {
        return listaNumeroContratoFinal;
    }
    /**
     * Asigna la lista listaNumeroContratoFinal
     * 
     * @param listaNumeroContratoFinal
     * Variable a asignar en  listaNumeroContratoFinal
     */
    public void setListaNumeroContratoFinal(List<Registro> listaNumeroContratoFinal) {
        this.listaNumeroContratoFinal = listaNumeroContratoFinal;
    }
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE> 
    /**
     * Retorna la lista listaTerceroInicial
     * 
     * @return listaTerceroInicial
     */
    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }
    /**
     * Asigna la lista listaTerceroInicial
     * 
     * @param listaTerceroInicial
     * Variable a asignar en  listaTerceroInicial
     */
    public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }
    /**
     * Retorna la lista listaTerceroFinal
     * 
     * @return listaTerceroFinal
     */
    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }
    /**
     * Asigna la lista listaTerceroFinal
     * 
     * @param listaTerceroFinal
     * Variable a asignar en  listaTerceroFinal
     */
    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }
    //</SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }
    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }
    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    /**
     * @param archivoDescarga the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }


}
