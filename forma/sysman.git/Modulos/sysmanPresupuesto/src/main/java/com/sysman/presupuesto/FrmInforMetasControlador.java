/*-
 * FrmInforMetasControlador.java
 *
 * 1.0
 * 
 * 02/01/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmInforMetasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 02/01/2024
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  FrmInforMetasControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;

    private String ano;

    private String mesInicial;

    private String metaInicial;

    private String metaFinal;

    private String mesFinal;

    private StreamedContent archivoDescarga;

    private List<Registro> listaAno;

    private List<Registro> listaMesInicial;

    private List<Registro> listaMesFinal;

    private RegistroDataModelImpl listaMetaInicial;

    private RegistroDataModelImpl listaMetaFinal;


    public FrmInforMetasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario=GeneralCodigoFormaEnum.FRMINFORMETASCONTROLADOR
                    .getCodigo();
            
            validarPermisos();
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
		 cargarListaAno();
		 ano = String.valueOf(SysmanFunciones.ano(new Date()));
		 cargarListaMesInicial();
		 mesInicial = "1";
		 cargarListaMesFinal();
		 mesFinal = String.valueOf(SysmanFunciones.mes(new Date()));
		 metaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		 metaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		 cargarListaMetaInicial();
		 cargarListaMetaFinal(); 
	        
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
  
    private void cargarListaMesFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                ano);

        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                    FrmInforMetasControladorUrlEnum.URL7001
                                    .getValue())
                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    private void cargarListaMesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                ano);

        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                    FrmInforMetasControladorUrlEnum.URL7001
                                    .getValue())
                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }       
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    
    //<METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAno
     */
    public void cargarListaAno(){
    	 Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

         try
         {
             listaAno = RegistroConverter.toListRegistro(
                             requestManager.getList(UrlServiceUtil.getInstance()
                                             .getUrlServiceByUrlByEnumID(
                                            		 FrmInforMetasControladorUrlEnum.URL4007
                                                                             .getValue())
                                             .getUrl(), param));
         }
         catch (SystemException e)
         {
             logger.error(e.getMessage(), e);
             JsfUtil.agregarMensajeError(e.getMessage());
         }
    }
    /**
     * 
     * Carga la lista listaMetaInicial
     *
     */
    public void cargarListaMetaInicial(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
        		.getUrlServiceByUrlByEnumID(
        				FrmInforMetasControladorUrlEnum.URL1884003
        				.getValue());
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "003");

        listaMetaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
        		urlBean.getUrlConteo().getUrl(), param,
        		true, GeneralParameterEnum.CODIGO.getName());
        
    }
    
    /**
     * Carga la lista listaMetaFinal
     */
    public void cargarListaMetaFinal(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
        		.getUrlServiceByUrlByEnumID(
        				FrmInforMetasControladorUrlEnum.URL1884003
        				.getValue());
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "003");

        listaMetaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
        		urlBean.getUrlConteo().getUrl(), param,
        		true, GeneralParameterEnum.CODIGO.getName());
    	
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
    	archivoDescarga=null;    
		generaReporte(FORMATOS.EXCEL);
    }
    
    private void generaReporte(FORMATOS formato) {

		try {
			
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			String nombreConsulta = "002530INFORMEPORMETAS"; 
			reemplazar.put("compania", compania);
			reemplazar.put("ano",ano);
			reemplazar.put("mesInicial",mesInicial);
			reemplazar.put("mesFinal",mesFinal);
			reemplazar.put("metaInicial",metaInicial);
			reemplazar.put("metaFinal",metaFinal);


			parametros.put("PR_COMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_VIGENCIA",ano);
			Reporteador.resuelveConsulta(nombreConsulta,Integer.parseInt(SessionUtil.getModulo()),reemplazar, parametros);

			
				archivoDescarga = JsfUtil.exportarStreamed(nombreConsulta, parametros,
						ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}			

		
	}
	public void cambiarAno() {
    	cargarListaMetaInicial();
    	cargarListaMetaFinal();
	}
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMetaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        metaInicial= registroAux.getCampos().get("CODIGO").toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMetaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        metaFinal= registroAux.getCampos().get("CODIGO").toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return  ano
     */
    public String getAno() {
        return ano;
    }
    /**
     * Asigna la variable  ano
     * 
     * @param  ano
     * Variable a asignar en  ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }
    /**
     * Retorna la variable mesInicial
     * 
     * @return  mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }
    /**
     * Asigna la variable  mesInicial
     * 
     * @param  mesInicial
     * Variable a asignar en  mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }
    /**
     * Retorna la variable metaInicial
     * 
     * @return  metaInicial
     */
    public String getMetaInicial() {
        return metaInicial;
    }
    /**
     * Asigna la variable  metaInicial
     * 
     * @param  metaInicial
     * Variable a asignar en  metaInicial
     */
    public void setMetaInicial(String metaInicial) {
        this.metaInicial = metaInicial;
    }
    /**
     * Retorna la variable metaFinal
     * 
     * @return  metaFinal
     */
    public String getMetaFinal() {
        return metaFinal;
    }
    /**
     * Asigna la variable  metaFinal
     * 
     * @param  metaFinal
     * Variable a asignar en  metaFinal
     */
    public void setMetaFinal(String metaFinal) {
        this.metaFinal = metaFinal;
    }
    /**
     * Retorna la variable mesFinal
     * 
     * @return  mesFinal
     */
    public String getMesFinal() {
        return mesFinal;
    }
    /**
     * Asigna la variable  mesFinal
     * 
     * @param  mesFinal
     * Variable a asignar en  mesFinal
     */
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
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
     * Retorna la lista listaMetaInicial
     * 
     * @return listaMetaInicial
     */
    public RegistroDataModelImpl getListaMetaInicial() {
        return listaMetaInicial;
    }
    /**
     * Asigna la lista listaMetaInicial
     * 
     * @param listaMetaInicial
     * Variable a asignar en  listaMetaInicial
     */
    public void setListaMetaInicial(RegistroDataModelImpl listaMetaInicial) {
        this.listaMetaInicial = listaMetaInicial;
    }
    /**
     * Retorna la lista listaMetaFinal
     * 
     * @return listaMetaFinal
     */
    public RegistroDataModelImpl getListaMetaFinal() {
        return listaMetaFinal;
    }
    /**
     * Asigna la lista listaMetaFinal
     * 
     * @param listaMetaFinal
     * Variable a asignar en  listaMetaFinal
     */
    public void setListaMetaFinal(RegistroDataModelImpl listaMetaFinal) {
        this.listaMetaFinal = listaMetaFinal;
    }
    /**
     * @return the listaMesInicial
     */
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }
    /**
     * @param listaMesInicial the listaMesInicial to set
     */
    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }
    /**
     * @return the listaMesFinal
     */
    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }
    /**
     * @param listaMesFinal the listaMesFinal to set
     */
    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }
    
    
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
