/*-
 * LisEjecPptalIngresosCompFlContorlador.java
 *
 * 1.0
 * 
 * 12/09/2018
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

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
import com.sysman.presupuesto.enums.LisEjecPptalIngresosCompFlContorladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * Formulario que permite generar los ingresos comparativos
 *
 * @version 1.0, 12/09/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  LisEjecPptalIngresosCompFlContorlador extends BeanBaseModal{
	/**
	 * variable que almacena la compania
	 */
	private final String compania;
	/**
	 * variable que almacena el modulo
	 */
	private final String modulo;
	/**
	 * variable que almacena la cuenta inicial
	 */
	private String cuentaInicial;
	/**
	 * variable que almacena la cuenta final
	 */
	private String cuentaFinal;
	/**
	 * variable que almacena el ano
	 */
	private int ano;
	/**
	 * variable que almacena el mes
	 */
	private int mes;
	/**
	 * variable que almacena el informe
	 */
	private String reporte;
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
	 * lista del ano
	 */
	private List<Registro> listaAno;
	/**
	 * lista del mes
	 */
	private List<Registro> listaMes;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * lista almacena cuenta inicial
	 */
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 * lista almacena cuenta final
	 */
	private RegistroDataModelImpl listaCuentaFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de LisEjecPptalIngresosCompFlContorlador
	 */ @EJB
	 private EjbSysmanUtilRemote ejbSysmanUtil;
	 public LisEjecPptalIngresosCompFlContorlador() {
		 super();
		 compania = SessionUtil.getCompania();
		 modulo = SessionUtil.getModulo();
		 try {
			 numFormulario = GeneralCodigoFormaEnum.LIS_EJEC_PPTAL_INGRESOS_COMP_FL_CONTROLADOR.getCodigo();
			 validarPermisos();

		 }  catch (Exception ex)
		 {
			 Logger.getLogger(AimregistroejecucgastosControlador.class.getName())
			 .log(Level.SEVERE, null, ex);
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
		 ano = SysmanFunciones.ano(new Date());
		 cargarListaMes();
		 mes = SysmanFunciones.mes(new Date());
		 cargarListaCuentaInicial(); 
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
	 //<METODOS_CARGAR_LISTA>
	 /**
	  * 
	  * Carga la lista listaAno
	  *
	  */
	 public void cargarListaAno(){

		 Map<String, Object> param = new TreeMap<>();
		 param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		 try
		 {
			 listaAno = RegistroConverter.toListRegistro(
					 requestManager.getList(UrlServiceUtil.getInstance()
							 .getUrlServiceByUrlByEnumID(
									 LisEjecPptalIngresosCompFlContorladorUrlEnum.URL0007
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
	  * Carga la lista listaMes
	  *
	  */
	 public void cargarListaMes(){
		 Map<String, Object> param = new TreeMap<>();
		 param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		 param.put("ANIO", ano);

		 try
		 {
			 listaMes = RegistroConverter.toListRegistro(
					 requestManager.getList(UrlServiceUtil.getInstance()
							 .getUrlServiceByUrlByEnumID(
									 LisEjecPptalIngresosCompFlContorladorUrlEnum.URL0016
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
	  * Carga la lista listaCuentaInicial Y Carga la lista listaCuentaFinal
	  *
	  */
	 public void cargarListaCuentaInicial(){

		 UrlBean urlBean = UrlServiceUtil.getInstance()
				 .getUrlServiceByUrlByEnumID(LisEjecPptalIngresosCompFlContorladorUrlEnum.URL0023.getValue());

		 Map<String, Object> param = new TreeMap<>();
		 param.put("COMPANIA", compania);
		 param.put("ANO", ano);
		 listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,"ID");

	 }


	 public void cargarListaCuentaFinal(){


		 UrlBean urlBean = UrlServiceUtil.getInstance()
				 .getUrlServiceByUrlByEnumID(LisEjecPptalIngresosCompFlContorladorUrlEnum.URL0025.getValue());

		 Map<String, Object> param = new TreeMap<>();
		 param.put("COMPANIA", compania);
		 param.put("ANO", ano);
		 param.put("CUENTAINICIAL", cuentaInicial);
		 listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,"ID");

	 }
	 //</METODOS_CARGAR_LISTA>
	 //<METODOS_BOTONES>
	 /**
	  * 
	  * Metodo ejecutado al oprimir el boton Excel
	  * en la vista
	  *
	  *
	  */
	 public void oprimirExcel() {
		 archivoDescarga=null;      
		 generaInforme(FORMATOS.EXCEL);
	 }
	 /**
	  * 
	  * Metodo ejecutado al oprimir el boton Pdf
	  * en la vista
	  *
	  *
	  */
	 public void oprimirPdf() {
		 archivoDescarga=null;            
		 generaInforme(FORMATOS.PDF);
	 }
	 /**
	  * metodo que contiene la logica para genera los reportes en
	  * formato pdf y excel
	  *
	  * @param formato
	 * @throws SystemException 
	  * @throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException 
	  */

	 // </CODIGO_DESARROLLADO>


	 public void generaInforme(ReportesBean.FORMATOS formato)  {

		 try{
			 reporte="001903LisEjecPptalIngresosComp_FL"; 
			 Map<String, Object> parametros = new HashMap<>();
			 HashMap<String, Object> reemplazar = new HashMap<>();
			 
			 reemplazar.put("compania", compania);
			 reemplazar.put("ano", ano);
			 reemplazar.put("mes", mes);
			 reemplazar.put("cuentaInicial", cuentaInicial);
			 reemplazar.put("cuentaFinal", cuentaFinal);

			 Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),reemplazar, parametros); 
			 
			 parametros.put("PR_ANO", ano);
			 parametros.put("PR_CARGO_EJECUCION_1", ejbSysmanUtil.consultarParametro(compania,"CARGO EJECUCION 1",modulo, new Date(),true));
			 parametros.put("PR_FIRMA_EJECUCION_1", ejbSysmanUtil.consultarParametro(compania,"FIRMA EJECUCION 1",modulo, new Date(),true));
			 parametros.put("PR_CARGO_EJECUCION_2", ejbSysmanUtil.consultarParametro(compania,"CARGO EJECUCION 2",modulo, new Date(),true));
			 parametros.put("PR_FIRMA_EJECUCION_2", ejbSysmanUtil.consultarParametro(compania,"FIRMA EJECUCION 2",modulo, new Date(),true));
			 parametros.put("PR_CARGO_EJECUCION_3", ejbSysmanUtil.consultarParametro(compania,"CARGO EJECUCION 3",modulo, new Date(),true));
			 parametros.put("PR_FIRMA_EJECUCION_3", ejbSysmanUtil.consultarParametro(compania,"FIRMA EJECUCION 3",modulo, new Date(),true));
			 
			 archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		 }
		 catch (JRException | IOException | SysmanException |SystemException e) {
			 logger.error(e.getMessage(), e);
			 JsfUtil.agregarMensajeError(e.getMessage());
		 }

	 }
	 /**
	  * Metodo ejecutado al cambiar el control Ano
	  * 
	  * 
	  */
	 public void cambiarAno() {
		 cargarListaMes();
		 cuentaInicial = null;
		 cuentaFinal = null;
		 cargarListaCuentaInicial();
	 }

	 /**
	  * 
	  * Metodo ejecutado al seleccionar una fila de la lista
	  * listaCuentaInicial
	  *
	  *
	  * @param event
	  * objeto que encapsula la accion proveniente de la vista
	  */
	 public void seleccionarFilaCuentaInicial(SelectEvent event) {
		 Registro registroAux = (Registro) event.getObject();
		 cuentaInicial= registroAux.getCampos().get("ID").toString();
		 cargarListaCuentaFinal();
		 cuentaFinal=null;
	 }
	 /**
	  * 
	  * Metodo ejecutado al seleccionar una fila de la lista
	  * listaCuentaFinal
	  * @param event
	  * objeto que encapsula la accion proveniente de la vista
	  */
	 public void seleccionarFilaCuentaFinal(SelectEvent event) {
		 Registro registroAux = (Registro) event.getObject();
		 cuentaFinal= registroAux.getCampos().get("ID").toString();
	 }
	 //</METODOS_COMBOS_GRANDES>
	 //<METODOS_ARBOL>
	 //</METODOS_ARBOL>
	 //<SET_GET_ATRIBUTOS>
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
	  * Retorna la variable ano
	  * 
	  * @return  ano
	  */
	 public int getAno() {
		 return ano;
	 }
	 /**
	  * Asigna la variable  ano
	  * 
	  * @param  ano
	  * Variable a asignar en  ano
	  */
	 public void setAno(int ano) {
		 this.ano = ano;
	 }
	 /**
	  * Retorna la variable mes
	  * 
	  * @return  mes
	  */
	 public int getMes() {
		 return mes;
	 }
	 /**
	  * Asigna la variable  mes
	  * 
	  * @param  mes
	  * Variable a asignar en  mes
	  */
	 public void setMes(int mes) {
		 this.mes = mes;
	 }
	 public String getReporte() {
		 return reporte;
	 }
	 public void setReporte(String reporte) {
		 this.reporte = reporte;
	 }
	 public String getModulo() {
		 return modulo;
	 }
	 public String getCompania() {
		 return compania;
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
	 /**
	  * Retorna la lista listaMes
	  * 
	  * @return listaMes
	  */
	 public List<Registro> getListaMes() {
		 return listaMes;
	 }
	 /**
	  * Asigna la lista listaMes
	  * 
	  * @param listaMes
	  * Variable a asignar en  listaMes
	  */
	 public void setListaMes(List<Registro> listaMes) {
		 this.listaMes = listaMes;
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
