/*-
 * ConfigurarPagoRecaudoControlador.java
 *
 * 1.0
 * 
 * 12/07/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
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
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 12/07/2019
 * @author obarragan
 */
@ManagedBean
@ViewScoped
public class  ConfigurarPagoRecaudoControlador  extends BeanBaseContinuoAcme{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>

	private String banco;

	private String referencia;

	private String nombreBanco;

	private String paquetes;
	private String fecha;
	private String numeroCupones;
	private String valorReportado;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModel listaBanco;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModel listaBancoE;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ConfigurarPagoRecaudoControlador
	 */
	public ConfigurarPagoRecaudoControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2096;
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
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
			tabla="GN_PAGO_BANCO_REC";
			reasignarOrigen();		    
			buscarLlave();
			registro= new Registro();
			//<CARGAR_LISTA>
			//</CARGAR_LISTA>
			//<CARGAR_LISTA_COMBO_GRANDE>
			cargarListaBanco(); cargarListaBancoE();
			//</CARGAR_LISTA_COMBO_GRANDE>
			abrirFormulario();
	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		origenDatos="";
		if (listaInicial != null) {
			listaInicial.setOrigen(origenDatos);
		}
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaBanco
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaBanco(){
		//listaBanco = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR2096:TBCB7091","SELECT GNBA.BANCO,"+
		//" BANCO.NOMBREBANCO"+
		//" FROM GN_BANCO_APLICACION GNBA"+
		//" INNER JOIN BANCO "+
		//"  ON GNBA.COMPANIA = BANCO.COMPANIA"+
		//" AND GNBA.BANCO    = BANCO.BANCO"+
		//" WHERE GNBA.COMPANIA = :COMPANIA"+
		//" AND GNBA.APLICACION = :APLICACIÓN",true,"BANCO");
	}
	/**
	 * 
	 * Carga la lista listaBanco
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaBancoE(){
		//listaBancoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR2096:TBCB7091","SELECT GNBA.BANCO,"+
		//" BANCO.NOMBREBANCO"+
		//" FROM GN_BANCO_APLICACION GNBA"+
		//" INNER JOIN BANCO "+
		//"  ON GNBA.COMPANIA = BANCO.COMPANIA"+
		//" AND GNBA.BANCO    = BANCO.BANCO"+
		//" WHERE GNBA.COMPANIA = :COMPANIA"+
		//" AND GNBA.APLICACION = :APLICACIÓN",true,"BANCO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
    public void cambiarReferencia() {
        //<CODIGO_DESARROLLADO>
    	if (banco == "0"|| referencia == "0"|| nombreBanco == "0"|| paquetes == "0"|| fecha == "0"|| numeroCupones == "0" || valorReportado == "0"){
			
		}
    	
       //</CODIGO_DESARROLLADO>
   }
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaBanco
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBanco(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		banco= registroAux.getCampos().get("BANCO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaBanco
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBancoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("BANCO");
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
	 * Retorna la variable banco
	 * 
	 * @return  banco
	 */
	public String getBanco() {
		return banco;
	}
	/**
	 * Asigna la variable  banco
	 * 
	 * @param  banco
	 * Variable a asignar en  banco
	 */
	public void setBanco(String banco) {
		this.banco = banco;
	}
	/**
	 * Retorna la variable referencia
	 * 
	 * @return  referencia
	 */
	public String getReferencia() {
		return referencia;
	}
	/**
	 * Asigna la variable  referencia
	 * 
	 * @param  referencia
	 * Variable a asignar en  referencia
	 */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	/**
	 * Retorna la variable nombreBanco
	 * 
	 * @return  nombreBanco
	 */
	public String getNombreBanco() {
		return nombreBanco;
	}
	/**
	 * Asigna la variable  nombreBanco
	 * 
	 * @param  nombreBanco
	 * Variable a asignar en  nombreBanco
	 */
	public void setNombreBanco(String nombreBanco) {
		this.nombreBanco = nombreBanco;
	}
	/**
	 * Retorna la variable paquetes
	 * 
	 * @return  paquetes
	 */
	public String getPaquetes() {
		return paquetes;
	}
	/**
	 * Asigna la variable  paquetes
	 * 
	 * @param  paquetes
	 * Variable a asignar en  paquetes
	 */
	public void setPaquetes(String paquetes) {
		this.paquetes = paquetes;
	}
	/**
	 * Retorna la variable fecha
	 * 
	 * @return  fecha
	 */
	public String getFecha() {
		return fecha;
	}
	/**
	 * Asigna la variable  fecha
	 * 
	 * @param  fecha
	 * Variable a asignar en  fecha
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	/**
	 * Retorna la variable numeroCupones
	 * 
	 * @return  numeroCupones
	 */
	public String getNumeroCupones() {
		return numeroCupones;
	}
	/**
	 * Asigna la variable  numeroCupones
	 * 
	 * @param  numeroCupones
	 * Variable a asignar en  numeroCupones
	 */
	public void setNumeroCupones(String numeroCupones) {
		this.numeroCupones = numeroCupones;
	}
	/**
	 * Retorna la variable valorReportado
	 * 
	 * @return  valorReportado
	 */
	public String getValorReportado() {
		return valorReportado;
	}
	/**
	 * Asigna la variable  valorReportado
	 * 
	 * @param  valorReportado
	 * Variable a asignar en  valorReportado
	 */
	public void setValorReportado(String valorReportado) {
		this.valorReportado = valorReportado;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaBanco
	 * 
	 * @return listaBanco
	 */
	public RegistroDataModel getListaBanco() {
		return listaBanco;
	}
	/**
	 * Asigna la lista listaBanco
	 * 
	 * @param listaBanco
	 * Variable a asignar en  listaBanco
	 */
	public void setListaBanco(RegistroDataModel listaBanco) {
		this.listaBanco = listaBanco;
	}
	/**
	 * Retorna la lista listaBanco
	 * 
	 * @return listaBanco
	 */
	public RegistroDataModel getListaBancoE() {
		return listaBancoE;
	}
	/**
	 * Asigna la lista listaBanco
	 * 
	 * @param listaBanco
	 * Variable a asignar en  listaBanco
	 */
	public void setListaBancoE(RegistroDataModel listaBancoE) {
		this.listaBancoE = listaBancoE;
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
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
