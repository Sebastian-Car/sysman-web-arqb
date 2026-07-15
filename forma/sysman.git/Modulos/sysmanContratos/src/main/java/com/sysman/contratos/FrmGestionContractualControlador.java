/*-
 * FrmGestionContractualControlador.java
 *
 * 1.0
 * 
 * 14/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.FrmGestionContractualControladorEnum;
import com.sysman.contratos.enums.FrmGestionContractualControladorUrlEnum;
import com.sysman.contratos.enums.InformeSiaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 14/04/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmGestionContractualControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String ckFormato;
	private String trimestre;
	private String formato;
	private int vigencia;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listacboModalidad;
	private String datosTrimestre;
	private String informe;
	private String valorTrimestre;
	
	private StreamedContent archivoDescarga;
	
	private boolean visibleTrimestreSerci;
	private boolean visibleMesSerci;
	
	private String mes;
	private String mostrarMes = "NO";
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmGestionContractualControlador
	 */
	public FrmGestionContractualControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2257
			numFormulario=GeneralCodigoFormaEnum.FRM_GESTION_CONTRACTUAL
                    .getCodigo();;
			validarPermisos();
			
			vigencia = SysmanFunciones.ano(new Date());
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
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
		//<CARGAR_LISTA>
		cargarListacboModalidad();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
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
		/*
FR2257-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
    formularioAbrir 9, Me.Name
    Me!cboTrimestre.RowSource = generarFechasVigencia(Me!txtVigencia)
    Me!chkTModalidades = False
End Sub
		 */
		
		try {
			mostrarMes = SysmanFunciones
			        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
			                        "GENERA INFORME SIRECI MENSUAL",
			                        SessionUtil.getModulo(), new Date(), true), "NO");
		} catch (SystemException e) {
			mostrarMes = "NO";
		}
		if(mostrarMes.equals("SI")){
			visibleMesSerci = true;
			visibleTrimestreSerci = false;			
		}else {
			visibleMesSerci = false;
			visibleTrimestreSerci = true;
		}
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacboModalidad
	 *
	 */
	public void cargarListacboModalidad(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listacboModalidad = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmGestionContractualControladorUrlEnum.URL0001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir
	 * en la vista
	 *
	 *
	 */
	public void oprimirImprimir() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInformes();
		//</CODIGO_DESARROLLADO>
	}

	public void generarFechasVigencia() {

		
		switch (trimestre) 
		{
		
		case "1":  
			valorTrimestre ="30/03/" + vigencia;
			break;
		case "2":  
			valorTrimestre = "30/06/" + vigencia;
			break;
		case "3":  
			valorTrimestre = "30/09/" + vigencia;
			break;
		case "4":  
			valorTrimestre = "30/12/" + vigencia ;
			break;
		}

	}
	
	public void escogerInforme() {
		
		switch (formato) 
		{
		case "1":  
			informe = FrmGestionContractualControladorEnum.F5_1.getValue(); 
			break;
		case "2":  
			informe = FrmGestionContractualControladorEnum.F5_2.getValue();
			break;
		case "3":  
			informe = FrmGestionContractualControladorEnum.F5_3.getValue();
			break;
		case "4":  
			informe = FrmGestionContractualControladorEnum.F5_4.getValue();
			break;
		case "5":  
			informe = FrmGestionContractualControladorEnum.F5_5.getValue();
			break;
		}
	}

	public void generarInformes() {
		try {
			Map<String, Object> reemplazos = new HashMap<String, Object>();
			Map<String, Object> parametros = new HashMap<String, Object>();
			
			mostrarMes = SysmanFunciones
			        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
			                        "GENERA INFORME SIRECI MENSUAL",
			                        SessionUtil.getModulo(), new Date(), true), "NO");
			if(mostrarMes.equals("NO") && !formato.equals("2")) {
				escogerInforme();
				generarFechasVigencia();
			}else {
				String ultimoDia = SysmanFunciones.convertirAFechaCadena(
					        		   SysmanFunciones.ultimoDiaDate(SysmanFunciones
					        				   		   .convertirAFecha("01/" + mes + "/" + vigencia)),
					        	   "dd/MM/yyyy");
				
				valorTrimestre = ultimoDia; 
				reemplazos.put("iniciovig", "01/01/" + vigencia);
				reemplazos.put("vigencia", vigencia);
				informe = FrmGestionContractualControladorEnum.F5_2_1.getValue();
			}
			
			reemplazos.put("trimestre", valorTrimestre);

			String sql= Reporteador.resuelveConsulta(informe,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos);
			
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, informe);

		} catch (ParseException | JRException | IOException | SysmanException | SQLException | DRException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}	

	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ckFormato
	 * 
	 * @return  ckFormato
	 */
	public String getCkFormato() {
		return ckFormato;
	}
	/**
	 * Asigna la variable  ckFormato
	 * 
	 * @param  ckFormato
	 * Variable a asignar en  ckFormato
	 */
	public void setCkFormato(String ckFormato) {
		this.ckFormato = ckFormato;
	}
	/**
	 * Retorna la variable trimestre
	 * 
	 * @return  trimestre
	 */
	public String getTrimestre() {
		return trimestre;
	}
	/**
	 * Asigna la variable  trimestre
	 * 
	 * @param  trimestre
	 * Variable a asignar en  trimestre
	 */
	public void setTrimestre(String trimestre) {
		this.trimestre = trimestre;
	}
	/**
	 * Retorna la variable formato
	 * 
	 * @return  formato
	 */
	public String getFormato() {
		return formato;
	}
	/**
	 * Asigna la variable  formato
	 * 
	 * @param  formato
	 * Variable a asignar en  formato
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}
	/**
	 * Retorna la variable vigencia
	 * 
	 * @return  vigencia
	 */
	public int getVigencia() {
		return vigencia;
	}
	/**
	 * Asigna la variable  vigencia
	 * 
	 * @param  vigencia
	 * Variable a asignar en  vigencia
	 */
	public void setVigencia(int vigencia) {
		this.vigencia = vigencia;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listacboModalidad
	 * 
	 * @return listacboModalidad
	 */
	public List<Registro> getListacboModalidad() {
		return listacboModalidad;
	}
	/**
	 * Asigna la lista listacboModalidad
	 * 
	 * @param listacboModalidad
	 * Variable a asignar en  listacboModalidad
	 */
	public void setListacboModalidad(List<Registro> listacboModalidad) {
		this.listacboModalidad = listacboModalidad;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the datosTrimestre
	 */
	public String getDatosTrimestre() {
		return datosTrimestre;
	}
	/**
	 * @param datosTrimestre the datosTrimestre to set
	 */
	public void setDatosTrimestre(String datosTrimestre) {
		this.datosTrimestre = datosTrimestre;
	}
	
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public boolean isVisibleTrimestreSerci() {
		return visibleTrimestreSerci;
	}
	public void setVisibleTrimestreSerci(boolean visibleTrimestreSerci) {
		this.visibleTrimestreSerci = visibleTrimestreSerci;
	}
	public boolean isVisibleMesSerci() {
		return visibleMesSerci;
	}
	public void setVisibleMesSerci(boolean visibleMesSerci) {
		this.visibleMesSerci = visibleMesSerci;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
}
