/*-
 * CAlmacenContabilidadBajaNiifControlador.java
 *
 * 1.0
 * 
 * 21/02/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.enums.CAlmacenContabilidadBajaControladorEnum;
import com.sysman.contabilizar.enums.CAlmacenContabilidadBajaControladorUrlEnum;
import com.sysman.contabilizar.enums.CAlmacenContabilidadBajaNiifControladorEnum;
import com.sysman.contabilizar.enums.CAlmacenContabilidadBajaNiifControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;
/**
 *
 * @version 1.0, 21/02/2020
 * @author jalfonso
 */
@ManagedBean
@ViewScoped
public class  CAlmacenContabilidadBajaNiifControlador  
extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
	private final String modulo;
	
    /**
     */

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
 private RegistroDataModelImpl listaTipo;
    /**
     */
 private RegistroDataModelImpl listaTipoE;
    /**
     */
 private RegistroDataModelImpl listaDebitoHistoricoBajaniif;
    /**
     */
 private RegistroDataModelImpl listaDebitoHistoricoBajaniifE;
    /**
     */
 private RegistroDataModelImpl listaCreditoHistoricoBajaniif;
    /**
     */
 private RegistroDataModelImpl listaCreditoHistoricoBajaniifE;
    /**
     */
 private RegistroDataModelImpl listaDebitoAcumuladaBajaNiif;
    /**
     */
 private RegistroDataModelImpl listaDebitoAcumuladaBajaNiifE;
    /**
     */
 private RegistroDataModelImpl listaCreditoAcumuladaBajaNiif;
    /**
     */
 private RegistroDataModelImpl listaCreditoAcumuladaBajaNiifE;
    /**
     */
 private RegistroDataModelImpl listaDebitoLibrosBajaNiif;
    /**
     */
 private RegistroDataModelImpl listaDebitoLibrosBajaNiifE;
    /**
     */
 private RegistroDataModelImpl listaCreditoLibrosBajaNiif;
    /**
     */
 private RegistroDataModelImpl listaCreditoLibrosBajaNiifE;
    /**
     */
 private RegistroDataModelImpl listaDebitoRetiradosBajaNiif;
    /**
     */
 private RegistroDataModelImpl listaDebitoRetiradosBajaNiifE;
    /**
     */
 private RegistroDataModelImpl listaCreditoRetiradosBajaNiif;
    /**
     */
 private RegistroDataModelImpl listaCreditoRetiradosBajaNiifE;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
 private String auxiliar;
 private String codigoElemento;
 private String nombreLargo;
 private String anio;
 
 private String tipo;

	private Map<String, Object> ridP;

	private Map<String, Object> parametrosEntrada;
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CAlmacenContabilidadBajaNiifControlador
     */
    public CAlmacenContabilidadBajaNiifControlador() {
	super();
	compania = SessionUtil.getCompania();
	modulo = SessionUtil.getModulo();
	
	try {
		numFormulario = GeneralCodigoFormaEnum.CALMACEN_CONTABILIDAD_BAJA_NIIF_CONTROLADOR.getCodigo();
		parametrosEntrada = SessionUtil.getFlash();

		if (parametrosEntrada != null) {

			ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");

			codigoElemento = parametrosEntrada.get("codigoElemento")
					.toString();

			anio = parametrosEntrada.get("anio")
					.toString();

			tipo = parametrosEntrada.get("tipo")
					.toString();

			nombreLargo = parametrosEntrada.get("nombre")
					.toString();

		}
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
	  tabla = GenericUrlEnum.ALMACEN_CONTABILIDAD.getTable();
	  reasignarOrigen();		    
	  buscarLlave();
	  registro= new Registro();
	  //<CARGAR_LISTA>
	  //</CARGAR_LISTA>
	  //<CARGAR_LISTA_COMBO_GRANDE>
		 cargarListaTipo(); cargarListaTipoE();
		 cargarListaDebitoHistoricoBajaniif();
		 cargarListaDebitoHistoricoBajaniifE();
		 cargarListaCreditoHistoricoBajaniif(); 
		 cargarListaCreditoHistoricoBajaniifE();
		 cargarListaDebitoAcumuladaBajaNiif(); 
		 cargarListaDebitoAcumuladaBajaNiifE();
		 cargarListaCreditoAcumuladaBajaNiif(); 
		 cargarListaCreditoAcumuladaBajaNiifE();
		 cargarListaDebitoLibrosBajaNiif(); 
		 cargarListaDebitoLibrosBajaNiifE();
		 cargarListaCreditoLibrosBajaNiif(); 
		 cargarListaCreditoLibrosBajaNiifE();
		 cargarListaDebitoRetiradosBajaNiif(); 
		 cargarListaDebitoRetiradosBajaNiifE();
		 cargarListaCreditoRetiradosBajaNiif(); 
		 cargarListaCreditoRetiradosBajaNiifE();
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

    	parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		parametrosListado.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
				codigoElemento);

		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaNiifControladorUrlEnum.URL20888
						.getValue());

		urlCreacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaNiifControladorUrlEnum.URL25587
						.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaNiifControladorUrlEnum.URL19121
						.getValue());

		urlEliminacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadBajaNiifControladorUrlEnum.URL28803
						.getValue());

    	
    }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipo
     *
     */
public void cargarListaTipo(){
	UrlBean urlBean = UrlServiceUtil.getInstance()
			.getUrlServiceByUrlByEnumID(
					CAlmacenContabilidadBajaNiifControladorUrlEnum.URL11457
					.getValue());
	Map<String, Object> param = new TreeMap<>();

	param.put(GeneralParameterEnum.COMPANIA.getName(),
			compania);

	param.put(GeneralParameterEnum.CLASE.getName(),
			"E");

	param.put(GeneralParameterEnum.CONCEPTO.getName(),
			"CM,DS,L,T"); // jm mod 05/05/2026 CC 4155

	param.put(CAlmacenContabilidadBajaNiifControladorEnum.TIPO.getValue(),
			"C");

	listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
			urlBean.getUrlConteo().getUrl(), param,
			true, GeneralParameterEnum.CODIGO.getName());
}
    /**
     * 
     * Carga la lista listaTipo
     *
     */
public void  cargarListaTipoE(){
	listaTipoE = listaTipo;
}
    /**
     * 
     * Carga la lista listaDebitoHistoricoBajaniif
     *
     */
public void cargarListaDebitoHistoricoBajaniif(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
		.getUrlServiceByUrlByEnumID(
				CAlmacenContabilidadBajaNiifControladorUrlEnum.URL14271
				.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		compania);
		
		param.put(GeneralParameterEnum.ANO.getName(),
		anio);
		
		listaDebitoHistoricoBajaniif = new RegistroDataModelImpl(urlBean.getUrl(),
		urlBean.getUrlConteo().getUrl(), param,
		true, GeneralParameterEnum.CODIGO.getName());
}
    /**
     * 
     * Carga la lista listaDebitoHistoricoBajaniif
     *
     */
public void  cargarListaDebitoHistoricoBajaniifE(){
listaDebitoHistoricoBajaniifE = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaCreditoHistoricoBajaniif
     *
     */
public void cargarListaCreditoHistoricoBajaniif(){
listaCreditoHistoricoBajaniif = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaCreditoHistoricoBajaniif
     *
     */
public void  cargarListaCreditoHistoricoBajaniifE(){
listaCreditoHistoricoBajaniifE = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaDebitoAcumuladaBajaNiif
     *
     */
public void cargarListaDebitoAcumuladaBajaNiif(){
listaDebitoAcumuladaBajaNiif = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaDebitoAcumuladaBajaNiif
     *
     */
public void  cargarListaDebitoAcumuladaBajaNiifE(){
listaDebitoAcumuladaBajaNiifE = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaCreditoAcumuladaBajaNiif
     *
     */
public void cargarListaCreditoAcumuladaBajaNiif(){
listaCreditoAcumuladaBajaNiif = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaCreditoAcumuladaBajaNiif
     *
     */
public void  cargarListaCreditoAcumuladaBajaNiifE(){
listaCreditoAcumuladaBajaNiifE = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaDebitoLibrosBajaNiif
     *
     */
public void cargarListaDebitoLibrosBajaNiif(){
listaDebitoLibrosBajaNiif = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaDebitoLibrosBajaNiif
     *
     */
public void  cargarListaDebitoLibrosBajaNiifE(){
listaDebitoLibrosBajaNiifE = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaCreditoLibrosBajaNiif
     *
     */
public void cargarListaCreditoLibrosBajaNiif(){
listaCreditoLibrosBajaNiif = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaCreditoLibrosBajaNiif
     *
     */
public void  cargarListaCreditoLibrosBajaNiifE(){
listaCreditoLibrosBajaNiifE = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaDebitoRetiradosBajaNiif
     *
     */
public void cargarListaDebitoRetiradosBajaNiif(){
listaDebitoRetiradosBajaNiif = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaDebitoRetiradosBajaNiif
     *
     */
public void  cargarListaDebitoRetiradosBajaNiifE(){
listaDebitoRetiradosBajaNiifE = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaCreditoRetiradosBajaNiif
     *
     */
public void cargarListaCreditoRetiradosBajaNiif(){
listaCreditoRetiradosBajaNiif = listaDebitoHistoricoBajaniif;
}
    /**
     * 
     * Carga la lista listaCreditoRetiradosBajaNiif
     *
     */
public void  cargarListaCreditoRetiradosBajaNiifE(){
listaCreditoRetiradosBajaNiifE = listaDebitoHistoricoBajaniif;
}
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Tipo en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarTipoC(int rowNum) {
	listaInicial.getDatasource().get(rowNum % 10).getCampos()
	.put(GeneralParameterEnum.NOMBRE.getName(), registro
			.getCampos()
			.get(GeneralParameterEnum.NOMBRE
					.getName()));    
	    }
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaTipo(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("TIPOMOVIMIENTO",
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));

	registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
			registroAux.getCampos().get(
					GeneralParameterEnum.NOMBRE.getName()));
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaTipoE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = (String) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName());

	registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
			registroAux.getCampos().get(
					GeneralParameterEnum.NOMBRE.getName()));
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoHistoricoBajaniif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaDebitoHistoricoBajaniif(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("NIIF_DEBITO_HISTORICO_BAJA", 
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));	
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoHistoricoBajaniif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaDebitoHistoricoBajaniifE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoHistoricoBajaniif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCreditoHistoricoBajaniif(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("NIIF_CREDITO_HISTORICO_BAJA",
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoHistoricoBajaniif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCreditoHistoricoBajaniifE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoAcumuladaBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaDebitoAcumuladaBajaNiif(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("NIIF_DEBITO_ACUMULADA_BAJA",
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoAcumuladaBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaDebitoAcumuladaBajaNiifE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoAcumuladaBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCreditoAcumuladaBajaNiif(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("NIIF_CREDITO_ACUMULADA_BAJA",
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoAcumuladaBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCreditoAcumuladaBajaNiifE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoLibrosBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaDebitoLibrosBajaNiif(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("NIIF_DEBITO_LIBROS_BAJA",
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoLibrosBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaDebitoLibrosBajaNiifE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoLibrosBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCreditoLibrosBajaNiif(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("NIIF_CREDITO_LIBROS_BAJA",
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoLibrosBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCreditoLibrosBajaNiifE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoRetiradosBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaDebitoRetiradosBajaNiif(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("NIIF_DEBITO_RETIRADOS_BAJA",
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoRetiradosBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaDebitoRetiradosBajaNiifE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoRetiradosBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCreditoRetiradosBajaNiif(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	registro.getCampos().put("NIIF_CREDITO_RETIRADOS_BAJA",
			registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName()));

}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoRetiradosBajaNiif
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCreditoRetiradosBajaNiifE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	auxiliar = SysmanFunciones.nvl( (BigInteger) registroAux.getCampos()
			.get(GeneralParameterEnum.CODIGO.getName()),"").toString();
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
     */
    @Override
 public void cancelarEdicion(RowEditEvent event) {
  	 getListaInicial().load();
      }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes(){
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		registro.getCampos().put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
				codigoElemento);

		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
           return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     * 
     */
    @Override
    public boolean actualizarAntes(){
    	registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
         return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * 
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
     * 
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
     * 
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
    	registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    	
    }
    
	public void ejecutarrcCerrar(){
		Map<String, Object> param = new TreeMap<>();
		param.put("rid", ridP);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put("codigoElemento", registro.getCampos()
				.get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(param);
		direccionador.setNumForm(Integer.toString(
				GeneralCodigoFormaEnum.CALMACEN_CONTABILIDADS_CONTROLADOR
				.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);
	}
    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores
     * al registro despues de dichas acciones
     */
   @Override
    public void asignarValoresRegistro()
    {
    }
//<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoElemento
     * 
     * @return  codigoElemento
     */
public String getCodigoElemento() {
        return codigoElemento;
    }
    /**
     * Asigna la variable  codigoElemento
     * 
     * @param  codigoElemento
     * Variable a asignar en  codigoElemento
     */
    public void setCodigoElemento(String codigoElemento) {
        this.codigoElemento = codigoElemento;
    }
    /**
     * Retorna la variable nombreLargo
     * 
     * @return  nombreLargo
     */
public String getNombreLargo() {
        return nombreLargo;
    }
    /**
     * Asigna la variable  nombreLargo
     * 
     * @param  nombreLargo
     * Variable a asignar en  nombreLargo
     */
    public void setNombreLargo(String nombreLargo) {
        this.nombreLargo = nombreLargo;
    }
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }
    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en  listaTipo
     */
    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }
    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public RegistroDataModelImpl getListaTipoE() {
        return listaTipoE;
    }
    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en  listaTipo
     */
    public void setListaTipoE(RegistroDataModelImpl listaTipoE) {
        this.listaTipoE = listaTipoE;
    }
    /**
     * Retorna la lista listaDebitoHistoricoBajaniif
     * 
     * @return listaDebitoHistoricoBajaniif
     */
    public RegistroDataModelImpl getListaDebitoHistoricoBajaniif() {
        return listaDebitoHistoricoBajaniif;
    }
    /**
     * Asigna la lista listaDebitoHistoricoBajaniif
     * 
     * @param listaDebitoHistoricoBajaniif
     * Variable a asignar en  listaDebitoHistoricoBajaniif
     */
    public void setListaDebitoHistoricoBajaniif(RegistroDataModelImpl listaDebitoHistoricoBajaniif) {
        this.listaDebitoHistoricoBajaniif = listaDebitoHistoricoBajaniif;
    }
    /**
     * Retorna la lista listaDebitoHistoricoBajaniif
     * 
     * @return listaDebitoHistoricoBajaniif
     */
    public RegistroDataModelImpl getListaDebitoHistoricoBajaniifE() {
        return listaDebitoHistoricoBajaniifE;
    }
    /**
     * Asigna la lista listaDebitoHistoricoBajaniif
     * 
     * @param listaDebitoHistoricoBajaniif
     * Variable a asignar en  listaDebitoHistoricoBajaniif
     */
    public void setListaDebitoHistoricoBajaniifE(RegistroDataModelImpl listaDebitoHistoricoBajaniifE) {
        this.listaDebitoHistoricoBajaniifE = listaDebitoHistoricoBajaniifE;
    }
    /**
     * Retorna la lista listaCreditoHistoricoBajaniif
     * 
     * @return listaCreditoHistoricoBajaniif
     */
    public RegistroDataModelImpl getListaCreditoHistoricoBajaniif() {
        return listaCreditoHistoricoBajaniif;
    }
    /**
     * Asigna la lista listaCreditoHistoricoBajaniif
     * 
     * @param listaCreditoHistoricoBajaniif
     * Variable a asignar en  listaCreditoHistoricoBajaniif
     */
    public void setListaCreditoHistoricoBajaniif(RegistroDataModelImpl listaCreditoHistoricoBajaniif) {
        this.listaCreditoHistoricoBajaniif = listaCreditoHistoricoBajaniif;
    }
    /**
     * Retorna la lista listaCreditoHistoricoBajaniif
     * 
     * @return listaCreditoHistoricoBajaniif
     */
    public RegistroDataModelImpl getListaCreditoHistoricoBajaniifE() {
        return listaCreditoHistoricoBajaniifE;
    }
    /**
     * Asigna la lista listaCreditoHistoricoBajaniif
     * 
     * @param listaCreditoHistoricoBajaniif
     * Variable a asignar en  listaCreditoHistoricoBajaniif
     */
    public void setListaCreditoHistoricoBajaniifE(RegistroDataModelImpl listaCreditoHistoricoBajaniifE) {
        this.listaCreditoHistoricoBajaniifE = listaCreditoHistoricoBajaniifE;
    }
    /**
     * Retorna la lista listaDebitoAcumuladaBajaNiif
     * 
     * @return listaDebitoAcumuladaBajaNiif
     */
    public RegistroDataModelImpl getListaDebitoAcumuladaBajaNiif() {
        return listaDebitoAcumuladaBajaNiif;
    }
    /**
     * Asigna la lista listaDebitoAcumuladaBajaNiif
     * 
     * @param listaDebitoAcumuladaBajaNiif
     * Variable a asignar en  listaDebitoAcumuladaBajaNiif
     */
    public void setListaDebitoAcumuladaBajaNiif(RegistroDataModelImpl listaDebitoAcumuladaBajaNiif) {
        this.listaDebitoAcumuladaBajaNiif = listaDebitoAcumuladaBajaNiif;
    }
    /**
     * Retorna la lista listaDebitoAcumuladaBajaNiif
     * 
     * @return listaDebitoAcumuladaBajaNiif
     */
    public RegistroDataModelImpl getListaDebitoAcumuladaBajaNiifE() {
        return listaDebitoAcumuladaBajaNiifE;
    }
    /**
     * Asigna la lista listaDebitoAcumuladaBajaNiif
     * 
     * @param listaDebitoAcumuladaBajaNiif
     * Variable a asignar en  listaDebitoAcumuladaBajaNiif
     */
    public void setListaDebitoAcumuladaBajaNiifE(RegistroDataModelImpl listaDebitoAcumuladaBajaNiifE) {
        this.listaDebitoAcumuladaBajaNiifE = listaDebitoAcumuladaBajaNiifE;
    }
    /**
     * Retorna la lista listaCreditoAcumuladaBajaNiif
     * 
     * @return listaCreditoAcumuladaBajaNiif
     */
    public RegistroDataModelImpl getListaCreditoAcumuladaBajaNiif() {
        return listaCreditoAcumuladaBajaNiif;
    }
    /**
     * Asigna la lista listaCreditoAcumuladaBajaNiif
     * 
     * @param listaCreditoAcumuladaBajaNiif
     * Variable a asignar en  listaCreditoAcumuladaBajaNiif
     */
    public void setListaCreditoAcumuladaBajaNiif(RegistroDataModelImpl listaCreditoAcumuladaBajaNiif) {
        this.listaCreditoAcumuladaBajaNiif = listaCreditoAcumuladaBajaNiif;
    }
    /**
     * Retorna la lista listaCreditoAcumuladaBajaNiif
     * 
     * @return listaCreditoAcumuladaBajaNiif
     */
    public RegistroDataModelImpl getListaCreditoAcumuladaBajaNiifE() {
        return listaCreditoAcumuladaBajaNiifE;
    }
    /**
     * Asigna la lista listaCreditoAcumuladaBajaNiif
     * 
     * @param listaCreditoAcumuladaBajaNiif
     * Variable a asignar en  listaCreditoAcumuladaBajaNiif
     */
    public void setListaCreditoAcumuladaBajaNiifE(RegistroDataModelImpl listaCreditoAcumuladaBajaNiifE) {
        this.listaCreditoAcumuladaBajaNiifE = listaCreditoAcumuladaBajaNiifE;
    }
    /**
     * Retorna la lista listaDebitoLibrosBajaNiif
     * 
     * @return listaDebitoLibrosBajaNiif
     */
    public RegistroDataModelImpl getListaDebitoLibrosBajaNiif() {
        return listaDebitoLibrosBajaNiif;
    }
    /**
     * Asigna la lista listaDebitoLibrosBajaNiif
     * 
     * @param listaDebitoLibrosBajaNiif
     * Variable a asignar en  listaDebitoLibrosBajaNiif
     */
    public void setListaDebitoLibrosBajaNiif(RegistroDataModelImpl listaDebitoLibrosBajaNiif) {
        this.listaDebitoLibrosBajaNiif = listaDebitoLibrosBajaNiif;
    }
    /**
     * Retorna la lista listaDebitoLibrosBajaNiif
     * 
     * @return listaDebitoLibrosBajaNiif
     */
    public RegistroDataModelImpl getListaDebitoLibrosBajaNiifE() {
        return listaDebitoLibrosBajaNiifE;
    }
    /**
     * Asigna la lista listaDebitoLibrosBajaNiif
     * 
     * @param listaDebitoLibrosBajaNiif
     * Variable a asignar en  listaDebitoLibrosBajaNiif
     */
    public void setListaDebitoLibrosBajaNiifE(RegistroDataModelImpl listaDebitoLibrosBajaNiifE) {
        this.listaDebitoLibrosBajaNiifE = listaDebitoLibrosBajaNiifE;
    }
    /**
     * Retorna la lista listaCreditoLibrosBajaNiif
     * 
     * @return listaCreditoLibrosBajaNiif
     */
    public RegistroDataModelImpl getListaCreditoLibrosBajaNiif() {
        return listaCreditoLibrosBajaNiif;
    }
    /**
     * Asigna la lista listaCreditoLibrosBajaNiif
     * 
     * @param listaCreditoLibrosBajaNiif
     * Variable a asignar en  listaCreditoLibrosBajaNiif
     */
    public void setListaCreditoLibrosBajaNiif(RegistroDataModelImpl listaCreditoLibrosBajaNiif) {
        this.listaCreditoLibrosBajaNiif = listaCreditoLibrosBajaNiif;
    }
    /**
     * Retorna la lista listaCreditoLibrosBajaNiif
     * 
     * @return listaCreditoLibrosBajaNiif
     */
    public RegistroDataModelImpl getListaCreditoLibrosBajaNiifE() {
        return listaCreditoLibrosBajaNiifE;
    }
    /**
     * Asigna la lista listaCreditoLibrosBajaNiif
     * 
     * @param listaCreditoLibrosBajaNiif
     * Variable a asignar en  listaCreditoLibrosBajaNiif
     */
    public void setListaCreditoLibrosBajaNiifE(RegistroDataModelImpl listaCreditoLibrosBajaNiifE) {
        this.listaCreditoLibrosBajaNiifE = listaCreditoLibrosBajaNiifE;
    }
    /**
     * Retorna la lista listaDebitoRetiradosBajaNiif
     * 
     * @return listaDebitoRetiradosBajaNiif
     */
    public RegistroDataModelImpl getListaDebitoRetiradosBajaNiif() {
        return listaDebitoRetiradosBajaNiif;
    }
    /**
     * Asigna la lista listaDebitoRetiradosBajaNiif
     * 
     * @param listaDebitoRetiradosBajaNiif
     * Variable a asignar en  listaDebitoRetiradosBajaNiif
     */
    public void setListaDebitoRetiradosBajaNiif(RegistroDataModelImpl listaDebitoRetiradosBajaNiif) {
        this.listaDebitoRetiradosBajaNiif = listaDebitoRetiradosBajaNiif;
    }
    /**
     * Retorna la lista listaDebitoRetiradosBajaNiif
     * 
     * @return listaDebitoRetiradosBajaNiif
     */
    public RegistroDataModelImpl getListaDebitoRetiradosBajaNiifE() {
        return listaDebitoRetiradosBajaNiifE;
    }
    /**
     * Asigna la lista listaDebitoRetiradosBajaNiif
     * 
     * @param listaDebitoRetiradosBajaNiif
     * Variable a asignar en  listaDebitoRetiradosBajaNiif
     */
    public void setListaDebitoRetiradosBajaNiifE(RegistroDataModelImpl listaDebitoRetiradosBajaNiifE) {
        this.listaDebitoRetiradosBajaNiifE = listaDebitoRetiradosBajaNiifE;
    }
    /**
     * Retorna la lista listaCreditoRetiradosBajaNiif
     * 
     * @return listaCreditoRetiradosBajaNiif
     */
    public RegistroDataModelImpl getListaCreditoRetiradosBajaNiif() {
        return listaCreditoRetiradosBajaNiif;
    }
    /**
     * Asigna la lista listaCreditoRetiradosBajaNiif
     * 
     * @param listaCreditoRetiradosBajaNiif
     * Variable a asignar en  listaCreditoRetiradosBajaNiif
     */
    public void setListaCreditoRetiradosBajaNiif(RegistroDataModelImpl listaCreditoRetiradosBajaNiif) {
        this.listaCreditoRetiradosBajaNiif = listaCreditoRetiradosBajaNiif;
    }
    /**
     * Retorna la lista listaCreditoRetiradosBajaNiif
     * 
     * @return listaCreditoRetiradosBajaNiif
     */
    public RegistroDataModelImpl getListaCreditoRetiradosBajaNiifE() {
        return listaCreditoRetiradosBajaNiifE;
    }
    /**
     * Asigna la lista listaCreditoRetiradosBajaNiif
     * 
     * @param listaCreditoRetiradosBajaNiif
     * Variable a asignar en  listaCreditoRetiradosBajaNiif
     */
    public void setListaCreditoRetiradosBajaNiifE(RegistroDataModelImpl listaCreditoRetiradosBajaNiifE) {
        this.listaCreditoRetiradosBajaNiifE = listaCreditoRetiradosBajaNiifE;
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
