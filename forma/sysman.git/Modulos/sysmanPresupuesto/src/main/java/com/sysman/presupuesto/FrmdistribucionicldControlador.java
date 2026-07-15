/*-
 * FrmdistribucionicldControlador.java
 *
 * 1.0
 * 
 * 28/03/2023
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;


import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmdistribucionicldControladorUrlEnum;
import com.sysman.presupuesto.enums.SubdetallecomprobantepptalsControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 28/03/2023
 * @author cperez2
 */
@ManagedBean
@ViewScoped
public class  FrmdistribucionicldControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anno;
	private String ruubro;
	

	//</DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
	
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaano;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaanno;
	

	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listarubro;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listarubroE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listafuente;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listafuenteE;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	private String sumaPorcentaje =  "100";
	
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmdistribucionicldControlador
	 */
	public FrmdistribucionicldControlador() {
		super();
		compania = SessionUtil.getCompania();
		//modulo = SessionUtil.getModulo();
		try {
			//numFormulario=2400;
			numFormulario = GeneralCodigoFormaEnum.DISTRIBUCION_ICLD.getCodigo();
			anno = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			ruubro = "";
			validarPermisos();
			//cargarFlash();
			abrirFormulario();
			registro.getCampos().put("ANO", anno);
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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

			//tabla="DISTRIBUCION_ICLD";
			enumBase = GenericUrlEnum.DISTRIBUCION_ICLD;
			reasignarOrigen();		    
			buscarLlave();
			//conectorPool.conectar(nombreConexion);
			//				registro= new Registro();
			//<CARGAR_LISTA>
			cargarListaano();
			cargarListaanno();
			//</CARGAR_LISTA>
			//<CARGAR_LISTA_COMBO_GRANDE>
			cargarListarubro(); cargarListarubroE();
			cargarListafuente(); cargarListafuenteE();
			//</CARGAR_LISTA_COMBO_GRANDE>
			abrirFormulario();
			calcularPieDeTabla();
			


	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anno);
	    parametrosListado.put(GeneralParameterEnum.RUBRO.getName(), ruubro);
	}
	public void calcularPieDeTabla() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anno);
		param.put(GeneralParameterEnum.RUBRO.getName(),
				ruubro);
		
		Registro regAux = null;
		try {
			regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmdistribucionicldControladorUrlEnum.URL1904003.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
		if (regAux == null || "".equals(ruubro) ) {
//			DecimalFormat df = new DecimalFormat("#,##0", dfs);
			sumaPorcentaje = "0";
			return;
		}
		//DecimalFormat df = new DecimalFormat("#,##0", dfs);
		sumaPorcentaje = regAux.getCampos().get("SUMAPORCENTAJE").toString();
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaano
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaano(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);


			try {
				listaano = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FrmdistribucionicldControladorUrlEnum.URL21760.getValue())
										.getUrl(),
								param));
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	/**
	 * 
	 * Carga la lista listaano
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaanno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);


			try {
				listaanno = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FrmdistribucionicldControladorUrlEnum.URL21760.getValue())
										.getUrl(),
								param));
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
	/**
	 * 
	 * Carga la lista listarubro
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListarubro(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmdistribucionicldControladorUrlEnum.URL1904001.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anno);

			listarubro = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listarubro
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListarubroE(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmdistribucionicldControladorUrlEnum.URL1904001.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anno);

			listarubroE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listafuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListafuente(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmdistribucionicldControladorUrlEnum.URL34008.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anno);

		listafuente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listafuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListafuenteE(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmdistribucionicldControladorUrlEnum.URL34008.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anno);

		listafuenteE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarano() {
		//<CODIGO_DESARROLLADO>
		anno =  (String) registro.getCampos().get("ANO");
		cargarListarubro();
		cargarListafuente();
		cargarListarubroE();
		cargarListafuenteE();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiaranno() {
		//<CODIGO_DESARROLLADO>
		inicializar();
		registro.getCampos().put("ANO", anno);
		
		//</CODIGO_DESARROLLADO>
	}
    /**
     * Metodo ejecutado al cambiar el control ruubro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarruubro() {
         //<CODIGO_DESARROLLADO>
		inicializar();
    	if(!"".equals(ruubro)) {
			registro.getCampos().put("RUBRO", ruubro);
		}
        //</CODIGO_DESARROLLADO>
    }
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listarubro
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilarubro(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("RUBRO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listarubro
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilarubroE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
		inicializar();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listafuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilafuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FUENTE", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listafuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilafuenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
	}
	//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		 registro =  new Registro();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", anno);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		calcularPieDeTabla();
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		calcularPieDeTabla();
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
	}
	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void ejecutarrcCerrar(){
		//<CODIGO_DESARROLLADO>
		calcularPieDeTabla();
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anno);
		param.put(GeneralParameterEnum.RUBRO.getName(),
				ruubro);
		String menores = "";
		String mayores = "";
		
		List <Registro>  regAux =  null;
		try {
			regAux =  RegistroConverter.toListRegistro( requestManager.getList( UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmdistribucionicldControladorUrlEnum.URL1904004.getValue()).getUrl()
					,param));
			StringBuilder may = new StringBuilder();
			StringBuilder men = new StringBuilder();
			if (!regAux.isEmpty()) {
				for( Registro id: regAux) {
                   if ( Double.parseDouble(id.getCampos().get("SUMPORCENTA").toString()) > 100) {
                	   may.append(id.getCampos().get("RUBRO").toString() + "" + (char)10 + (char)13);
				   }
                   if ( Double.parseDouble(id.getCampos().get("SUMPORCENTA").toString()) < 100) {
        
                	   men.append(id.getCampos().get("RUBRO").toString() + "" + (char)10 + (char)13);
					}
				}
				menores =men.toString();
				mayores =may.toString();
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if (!"".equals(menores)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB4423") + " Los Rubros son =  (" + (char)10 + (char)13 + menores + ") para el año " + anno );
		}
		if (!"".equals(mayores)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB4422") + " Los Rubros son =  (" + (char)10 + (char)13 + mayores + ")  para el año " + anno);
		}
		if (!"".equals(menores) || !"".equals(mayores) ) {
			return;
		}else{
			SessionUtil.redireccionarMenu(); 
		}
		//</CODIGO_DESARROLLADO>
	}
	
	  /**
     * 
     * Metodo ejecutado al oprimir el boton inconsistencias en la
     * vista
     *
     *
     */
    public void oprimirinconsistencias()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
		calcularPieDeTabla();
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anno);
		param.put(GeneralParameterEnum.RUBRO.getName(),
				ruubro);
		String menores = "";
		String mayores = "";
		
		List <Registro>  regAux =  null;
		try {
			regAux =  RegistroConverter.toListRegistro( requestManager.getList( UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmdistribucionicldControladorUrlEnum.URL1904004.getValue()).getUrl()
					,param));
			StringBuilder may = new StringBuilder();
			StringBuilder men = new StringBuilder();
			if (!regAux.isEmpty()) {
				for( Registro id: regAux) {
                   if (Double.parseDouble(id.getCampos().get("SUMPORCENTA").toString()) > 100) {
                	   may.append(id.getCampos().get("RUBRO").toString() + "" + (char)10 + (char)13);
				   }
                   if (Double.parseDouble(id.getCampos().get("SUMPORCENTA").toString()) < 100) {
        
                	   men.append(id.getCampos().get("RUBRO").toString() + "" + (char)10 + (char)13);
					}
				}
				menores =men.toString();
				mayores =may.toString();
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (!"".equals(menores) || !"".equals(mayores) ) {
			try { 
				archivoDescarga = JsfUtil
                                .getArchivoDescarga(JsfUtil.serializarPlano(
                                                SysmanFunciones.concatenar( idioma.getString("TB_TB4423") + " Los Rubros son =  (" + (char)10 + (char)13 + menores + ") para el año " + anno + (char)10 + (char)13  + (char)10 + (char)13
                                                		, idioma.getString("TB_TB4422") + " Los Rubros son =  (" + (char)10 + (char)13 + mayores + ")  para el año " + anno)), "Inconsistencias.txt");
			}catch (JRException | IOException e) {
				e.printStackTrace();
			}
			return;
		}else {
			JsfUtil.agregarMensajeInformativo( "Los Rubros  para el año " + anno  +" Se encuentan bien 100%");
		}
        // </CODIGO_DESARROLLADO>
    }
	
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// TODO Auto-generated method stub
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano
	 * 
	 * @return  ano
	 */
	public String getAnno() {
		return anno;
	}
	/**
	 * Asigna la variable  ano
	 * 
	 * @param  ano
	 * Variable a asignar en  ano
	 */
	public void setAnno(String anno) {
		this.anno = anno;
	}
	 /**
     * Retorna la variable ruubro
     * 
     * @return  ruubro
     */
    public String getRuubro() {
        return ruubro;
    }
    /**
     * Asigna la variable  ruubro
     * 
     * @param  ruubro
     * Variable a asignar en  ruubro
     */
    public void setRuubro(String ruubro) {
        this.ruubro = ruubro;
    }
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaano
	 * 
	 * @return listaano
	 */
	public List<Registro> getListaano() {
		return listaano;
	}
	/**
	 * Asigna la lista listaano
	 * 
	 * @param listaano
	 * Variable a asignar en  listaano
	 */
	public void setListaano(List<Registro> listaano) {
		this.listaano = listaano;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listarubro
	 * 
	 * @return listarubro
	 */
	public RegistroDataModelImpl getListarubro() {
		return listarubro;
	}
	/**
	 * Asigna la lista listarubro
	 * 
	 * @param listarubro
	 * Variable a asignar en  listarubro
	 */
	public void setListarubro(RegistroDataModelImpl listarubro) {
		this.listarubro = listarubro;
	}
	/**
	 * Retorna la lista listarubro
	 * 
	 * @return listarubro
	 */
	public RegistroDataModelImpl getListarubroE() {
		return listarubroE;
	}
	/**
	 * Asigna la lista listarubro
	 * 
	 * @param listarubro
	 * Variable a asignar en  listarubro
	 */
	public void setListarubroE(RegistroDataModelImpl listarubroE) {
		this.listarubroE = listarubroE;
	}
	/**
	 * Retorna la lista listafuente
	 * 
	 * @return listafuente
	 */
	public RegistroDataModelImpl getListafuente() {
		return listafuente;
	}
	/**
	 * Asigna la lista listafuente
	 * 
	 * @param listafuente
	 * Variable a asignar en  listafuente
	 */
	public void setListafuente(RegistroDataModelImpl listafuente) {
		this.listafuente = listafuente;
	}
	/**
	 * Retorna la lista listafuente
	 * 
	 * @return listafuente
	 */
	public RegistroDataModelImpl getListafuenteE() {
		return listafuenteE;
	}
	/**
	 * Asigna la lista listafuente
	 * 
	 * @param listafuente
	 * Variable a asignar en  listafuente
	 */
	public void setListafuenteE(RegistroDataModelImpl listafuenteE) {
		this.listafuenteE = listafuenteE;
	}
	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 * Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar= auxiliar;
	}
	
	public String getSumaPorcentaje() {
		return sumaPorcentaje;
	}
	
	public void setSumaPorcentaje(String sumaPorcentaje) {
		this.sumaPorcentaje = sumaPorcentaje;
	}
	public List<Registro> getListaanno() {
		return listaanno;
	}
	public void setListaanno(List<Registro> listaanno) {
		this.listaanno = listaanno;
	}
	public String getCompania() {
		return compania;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
