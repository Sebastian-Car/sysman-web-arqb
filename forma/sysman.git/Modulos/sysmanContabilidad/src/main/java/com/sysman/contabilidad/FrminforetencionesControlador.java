/*-
 * FrminforetencionesControlador.java
 *
 * 1.0
 * 
 * 03/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrminforetencionesControladorUrlEnum;
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 03/05/2024
 * @author JhohanDavidAmayaSalc
 */
@ManagedBean
@ViewScoped
public class FrminforetencionesControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    //<DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private String retencionInicial;

    private String retencionFinal;

    private int ano;
    
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_LISTAS>
    private List<Registro> listaAno;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoRetencionInicial;
    private RegistroDataModelImpl listaTipoRetencionFinal;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    //<DECLARAR_LISTAS_SUBFORM>
    //</DECLARAR_LISTAS_SUBFORM>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_ADICIONALES>
    //</DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrminforetencionesControlador
     */
    public FrminforetencionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_RETENCIONES.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @PostConstruct
    public void inicializar(){
//<CARGAR_LISTA>
         cargarListaAno();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
         cargarListaTipoRetencionInicial(); 
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
        abrirFormulario();
    }

    //<METODOS_CARGAR_LISTA>    

    public void cargarListaAno(){

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter
                    .toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                    FrminforetencionesControladorUrlEnum.URL4001.getValue())
                            .getUrl(),
                            param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    public void cargarListaTipoRetencionInicial(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        FrminforetencionesControladorUrlEnum.URL8001
                        .getValue());
        listaTipoRetencionInicial = new RegistroDataModelImpl(urlBean
                .getUrl(),
                urlBean
                .getUrlConteo()
                .getUrl(),
                null,
                true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoRetencionFinal(){
        
    	Map<String, Object> param = new TreeMap<>();
		param.put("TIPOINICIAL", retencionInicial);
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        FrminforetencionesControladorUrlEnum.URL8007
                        .getValue());
        listaTipoRetencionFinal = new RegistroDataModelImpl(urlBean
                .getUrl(),
                urlBean
                .getUrlConteo()
                .getUrl(),
                param,
                true, GeneralParameterEnum.CODIGO.getName());
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_CAMBIAR> 

    public void cambiarAno() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>  
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRetencionInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoRetencionInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        retencionInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        retencionFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaTipoRetencionFinal();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRetencionFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoRetencionFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        retencionFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>   
    //</METODOS_ARBOL>
    //<METODOS_BOTONES> 
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar
     * en la vista
     *
     *
     */
    public void oprimirPresentar() throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;    
        generarInforme(FORMATOS.PDF);
       //</CODIGO_DESARROLLADO>
   }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     *
     */
    public void oprimirExcel() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;         
        generarInforme(FORMATOS.EXCEL97);
        
       //</CODIGO_DESARROLLADO>
   }
    public void generarInforme(ReportesBean.FORMATOS formato) {
        
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio",ano);
            reemplazar.put("tipoInicial",retencionInicial);
            reemplazar.put("tipoFinal",retencionFinal);
            
            
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000668LisRetenciones";
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException | JRException | IOException  e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
        
    }
    //</METODOS_BOTONES>    
    //<METODOS_SUBFORM> 
    //</METODOS_SUBFORM>    
    //<METODOS_ADICIONALES> 
    //</METODOS_ADICIONALES>
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
    //<SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }


    /**
     * @return the retencionInicial
     */
    public String getRetencionInicial() {
        return retencionInicial;
    }
    /**
     * @param retencionInicial the retencionInicial to set
     */
    public void setRetencionInicial(String retencionInicial) {
        this.retencionInicial = retencionInicial;
    }
    /**
     * @return the retencionFinal
     */
    public String getRetencionFinal() {
        return retencionFinal;
    }
    /**
     * @param retencionFinal the retencionFinal to set
     */
    public void setRetencionFinal(String retencionFinal) {
        this.retencionFinal = retencionFinal;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_LISTAS>
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
     * Variable a asignar en  listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE> 
    /**
     * @return the listaTipoRetencionInicial
     */
    public RegistroDataModelImpl getListaTipoRetencionInicial() {
        return listaTipoRetencionInicial;
    }
    /**
     * @param listaTipoRetencionInicial the listaTipoRetencionInicial to set
     */
    public void setListaTipoRetencionInicial(RegistroDataModelImpl listaTipoRetencionInicial) {
        this.listaTipoRetencionInicial = listaTipoRetencionInicial;
    }
    /**
     * @return the listaTipoRetencionFinal
     */
    public RegistroDataModelImpl getListaTipoRetencionFinal() {
        return listaTipoRetencionFinal;
    }
    /**
     * @param listaTipoRetencionFinal the listaTipoRetencionFinal to set
     */
    public void setListaTipoRetencionFinal(RegistroDataModelImpl listaTipoRetencionFinal) {
        this.listaTipoRetencionFinal = listaTipoRetencionFinal;
    }
    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }
    /**
     * @param ano the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    //</SET_GET_LISTAS_COMBO_GRANDE>
    //<SET_GET_LISTAS_SUBFORM>
    //</SET_GET_LISTAS_SUBFORM>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_ADICIONALES> 
    //</SET_GET_ADICIONALES>
}
