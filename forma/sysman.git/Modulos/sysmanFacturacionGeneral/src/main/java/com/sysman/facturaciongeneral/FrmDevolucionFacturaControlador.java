/*-
 * FrmDevolucionFacturaControlador.java
 *
 * 1.0
 * 
 * 10/12/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.enums.FrmDevolucionFacturaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;
/**
 *
 * @version 1.0, 10/12/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmDevolucionFacturaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String factura;
	private Date fecha;
	private String cuenta;
	private boolean indPago;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaFactura;
	private RegistroDataModelImpl listaCuenta;
	private String ano;
	private String tipoCobro;
	
	@EJB
	private EjbFacturacionGeneralCuatroRemote generalCuatroRemote;
	private boolean facturado;
	private String numeroCobro;
	
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmDevolucionFacturaControlador
	 */
	public FrmDevolucionFacturaControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2500
			numFormulario = GeneralCodigoFormaEnum.FRM_DEVOLUCION_FACTURA_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			ano = (String) SessionUtil.getSessionVar(
					ConstantesFacturacionGenEnum.ANIO.getValue());
			tipoCobro = (String) SessionUtil.getSessionVar(
					ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaFactura();
		cargarListaCuenta();
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
	 * Carga la lista listaFactura
	 *
	 */
	public void cargarListaFactura(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmDevolucionFacturaControladorUrlEnum.URL3595
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TIPOCOBRO",
				tipoCobro);

		listaFactura = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NUMERO_FACTURA");
	}
	
	/**
	 * 
	 * Carga la lista listaCuenta
	 *
	 */
	public void cargarListaCuenta(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmDevolucionFacturaControladorUrlEnum.URL16230
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), ano);
		
		listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * en la vista
	 *
	 *
	 */
	public void oprimirAceptar() {
		//<CODIGO_DESARROLLADO>
		ejecutarDevolucionFactura();
		//</CODIGO_DESARROLLADO>
	}
	
	
	public void ejecutarDevolucionFactura() {
		String msg = null;
       try {
		msg = generalCuatroRemote.devolverFacturas(compania,Integer.parseInt(ano), new BigInteger(factura), tipoCobro, new BigInteger(numeroCobro), facturado, indPago, cuenta, fecha, SessionUtil.getUser().getCodigo());

		JsfUtil.agregarMensajeInformativo(msg);
	} catch (NumberFormatException | SystemException e) {
		logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
	}

	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Factura en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarFacturaC(int rowNum) {
		// Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
		// Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFactura
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFactura(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		factura = SysmanFunciones.toString(registroAux.getCampos().get("NUMERO_FACTURA"));
		numeroCobro = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO_COBRO"));
		facturado = Boolean.parseBoolean(SysmanFunciones.toString(registroAux.getCampos().get("IMPRESO")));
		indPago = Boolean.parseBoolean(SysmanFunciones.toString(registroAux.getCampos().get("INDPAGO")));
	}
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuenta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCuenta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuenta = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable factura
	 * 
	 * @return  factura
	 */
	public String getFactura() {
		return factura;
	}
	/**
	 * Asigna la variable  factura
	 * 
	 * @param  factura
	 * Variable a asignar en  factura
	 */
	public void setFactura(String factura) {
		this.factura = factura;
	}
	/**
	 * Retorna la variable fecha
	 * 
	 * @return  fecha
	 */
	public Date getFecha() {
		return fecha;
	}
	/**
	 * Asigna la variable  fecha
	 * 
	 * @param  fecha
	 * Variable a asignar en  fecha
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaFactura
	 * 
	 * @return listaFactura
	 */
	public RegistroDataModelImpl getListaFactura() {
		return listaFactura;
	}
	/**
	 * Asigna la lista listaFactura
	 * 
	 * @param listaFactura
	 * Variable a asignar en  listaFactura
	 */
	public void setListaFactura(RegistroDataModelImpl listaFactura) {
		this.listaFactura = listaFactura;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the cuenta
	 */
	public String getCuenta() {
		return cuenta;
	}
	/**
	 * @param cuenta the cuenta to set
	 */
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	/**
	 * @return the listaCuenta
	 */
	public RegistroDataModelImpl getListaCuenta() {
		return listaCuenta;
	}
	/**
	 * @param listaCuenta the listaCuenta to set
	 */
	public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
		this.listaCuenta = listaCuenta;
	}
	/**
	 * @return the indPago
	 */
	public boolean isIndPago() {
		return indPago;
	}
	/**
	 * @param indPago the indPago to set
	 */
	public void setIndPago(boolean indPago) {
		this.indPago = indPago;
	}
}
