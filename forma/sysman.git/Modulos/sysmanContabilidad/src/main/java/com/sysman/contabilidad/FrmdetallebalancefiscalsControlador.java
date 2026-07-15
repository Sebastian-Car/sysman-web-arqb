/*-
 * FrmdetallebalancefiscalsControlador.java
 *
 * 1.0
 * 
 * 18/01/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

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
import javax.ejb.EJB;
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
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.FrmGeneraNuevoMarcoNorControladorEnum;
import com.sysman.contabilidad.enums.FrmbalancefiscalControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CambiarcontrasenasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
 * @version 1.0, 18/01/2023
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class FrmdetallebalancefiscalsControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbCodigoCuenta;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbCodigoCuentaE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;
	
	private String comprobante;
	
	private String ano;
	
	private String mes;
	
	private String dia;
	
	private String indice;
	
	private String identificadorcomprobante;
	
	private String conciliador;
	
	private String totalDebito;
	
	private String totalCredito;
	
	private String diferencia;
	
	private boolean bloqDebito;
	
	private boolean bloqCredito;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbContabilidadSieteRemote ejbContabilidadSiete;
	private Map<String, Object> rid;
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmdetallebalancefiscalsControlador
	 */
	public FrmdetallebalancefiscalsControlador() {
		super();
		compania = SessionUtil.getCompania();
		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
		if (parametrosEntrada != null) {
			comprobante = parametrosEntrada.get("COMPROBTANTE").toString();
			ano = parametrosEntrada.get("ANO").toString();
			mes = parametrosEntrada.get("MES").toString();
			dia = parametrosEntrada.get("DIA").toString();
			rid = (Map<String, Object>) parametrosEntrada.get("RID");
		}
		setIdentificadorcomprobante("COMPROBANTE AJUSTE FISCAL - No - "+comprobante);
		conciliador = SessionUtil.getUser().getNombre1()+" "+SessionUtil.getUser().getNombre1();
		bloqDebito = false;
		bloqCredito = false;
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_BALANCE_CONCILIACION_FISCAL_IMPUTACION_DETALLE.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.DETALLE_BALANCE_FISCAL;
		reasignarOrigen();
		buscarLlave();
		registro = new Registro();
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacmbCodigoCuenta();
		cargarListacmbCodigoCuentaE();
//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put("COMPROBANTE", comprobante);
		
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacmbCodigoCuenta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbCodigoCuenta() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmbalancefiscalControladorUrlEnum.URL1901001.getValue());
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listacmbCodigoCuenta = new RegistroDataModelImpl(urlBean.getUrl(), 
				urlBean.getUrlConteo().getUrl(), 
				param, true, GeneralParameterEnum.ID.getName());
	
	}

	/**
	 * 
	 * Carga la lista listacmbCodigoCuenta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbCodigoCuentaE() {
		listacmbCodigoCuentaE = listacmbCodigoCuenta;
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnCalcularTotales en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirbtnCalcularTotales() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}
	public void ejecutarrcCerrar() {
		if(diferencia.equals("Diferencia: 0.0")) {
			Direccionador direccionador = new Direccionador();
	
	        direccionador.setNumForm(Integer
	                        .toString(GeneralCodigoFormaEnum.FRM_BALANCE_CONCILIACION_FISCAL_IMPUTACION
	                                        .getCodigo()));
	        Map<String, Object> params = new HashMap<>();
			params.put("COMPROBTANTE", comprobante);
			params.put("ANO", ano);
			params.put("MES", mes);
			params.put("DIA", dia);
			if (rid.containsValue("")) {				
				rid.put("KEY_COMPANIA", SessionUtil.getCompania());
				rid.put("KEY_ANO",ano);
				rid.put("KEY_MES",mes);
				rid.put("KEY_DIA",dia);
			}
			params.put("RID", rid);
	        direccionador.setParametros(params);
	        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
		}else {
			JsfUtil.agregarMensajeInformativo("Comprobante Descuadraro, "+diferencia);
		}
	}
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbCodigoCuenta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbCodigoCuenta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CUENTA", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("CUENTA", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbCodigoCuenta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbCodigoCuentaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
		registro.getCampos().put("CODIGO_CUENTA", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("CUENTA", registroAux.getCampos().get("CODIGO"));
	}

//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		cargarTotales();
		// </CODIGO_DESARROLLADO>
	}
	private void cargarTotales() {
		try {
			Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(),
	                        compania);
	        param.put("COMPROBANTE", comprobante);
	        param.put("ANO", ano);
            UrlBean urlListMeI = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                    		FrmbalancefiscalControladorUrlEnum.URL1901003
                                            .getValue());
            Registro debitosCreditos = RegistroConverter.toRegistro(
            requestManager.get(urlListMeI.getUrl(), param));
            if(debitosCreditos == null) {
            	totalCredito = "0.0";
            	totalDebito = "0.0";
            	diferencia = "Diferencia: 0.0";
            }else {
	            totalCredito = SysmanFunciones.nvl(SysmanFunciones.toString(debitosCreditos.getCampos().get("CREDITOS")),"0").toString();
	            
	            totalDebito = SysmanFunciones.nvl(SysmanFunciones.toString(debitosCreditos.getCampos().get("DEBITOS")),"0").toString();
	            
	            diferencia = "Diferencia: "+String.valueOf(((Double.parseDouble(totalDebito))-(Double.parseDouble(totalCredito))));
            }
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}
	public void cambiartxtDebito() {
		if(registro.getCampos().get("SALDO_DEBITO").toString().equals("") || registro.getCampos().get("SALDO_DEBITO").toString().equals("0")) {
			bloqCredito = false;
			registro.getCampos().put("SALDO_CREDITO", "");
		}else {			
			bloqCredito = true;
			registro.getCampos().put("SALDO_CREDITO", 0);
		}
	}
	public void cambiartxtCredito() {
		if(registro.getCampos().get("SALDO_CREDITO").toString().equals("") || registro.getCampos().get("SALDO_CREDITO").toString().equals("0")) {
			bloqDebito =  false;
			registro.getCampos().put("SALDO_DEBITO", "");
		}else {
			bloqDebito =  true;
			registro.getCampos().put("SALDO_DEBITO", 0);
		}
	}
	public void cambiartxtDebitoC(int rowNum) {
		String debito = listaInicial.getDatasource().get(rowNum).getCampos().get("SALDO_DEBITO").toString();
		if(debito.equals("") || debito == null || debito.equals("0")) {
			listaInicial.getDatasource().get(rowNum).getCampos().put("SALDO_CREDITO","");
			bloqCredito = false;
		}else {
			listaInicial.getDatasource().get(rowNum).getCampos().put("SALDO_CREDITO",0);
			bloqCredito = true;
		}
	}
	public void cambiartxtCreditoC(int rowNum) {
		String credito = listaInicial.getDatasource().get(rowNum).getCampos().get("SALDO_CREDITO").toString();
		if(credito.equals("") || credito == null || credito.equals("0")) {
			listaInicial.getDatasource().get(rowNum).getCampos().put("SALDO_DEBITO","");
			bloqCredito = false;
		}else {
			listaInicial.getDatasource().get(rowNum).getCampos().put("SALDO_DEBITO",0);
			bloqCredito = true;
		}
	}
	public void activarEdicion(Registro reg) {
		int row = listaInicial.getDatasource().indexOf(reg);
		cambiartxtDebitoC(row);
		cambiartxtCreditoC(row);
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado TODO
	 * DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
		cargarTotales();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		try {
			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
			registro.getCampos().put(GeneralParameterEnum.COMPROBANTE.getName(), comprobante);
			registro.getCampos().put(GeneralParameterEnum.MES.getName(), mes);
			registro.getCampos().put("DIA", dia);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
			registro.getCampos().put(GeneralParameterEnum.FECHA.getName(), sdf.format(new Date()).toString());
			registro.getCampos().put("OBSERVACIONES", "DETALLE BALANCE FSICAL "+comprobante);
			// OJO DEBE CALCULAR EL CON SECUTIVO
			long consuecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(GenericUrlEnum.DETALLE_BALANCE_FISCAL.getTable(), 
					GenericUrlEnum.DETALLE_BALANCE_FISCAL.getTable()+".COMPANIA =''"
	                        + compania + "''"
	                        + " AND "+GenericUrlEnum.DETALLE_BALANCE_FISCAL.getTable()+".ANO ="
	                        + ano
	                        + " AND "+GenericUrlEnum.DETALLE_BALANCE_FISCAL.getTable()+".DIA ="
	                        + dia
	                        + " AND "+GenericUrlEnum.DETALLE_BALANCE_FISCAL.getTable()+".MES = ''"
	                        + mes
	                        + "'' AND "+GenericUrlEnum.DETALLE_BALANCE_FISCAL.getTable()+".COMPROBANTE="
	                        + comprobante,
	                        GenericUrlEnum.DETALLE_BALANCE_FISCAL.getTable()+".CONSECUTIVO");
			registro.getCampos().put("CONSECUTIVO", consuecutivo);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		cargarTotales();
		// Se actualiza el valor total del comprobante
		try {
			ejbContabilidadSiete.actualizarValoresTotalesComp(compania, ano, comprobante);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR2392-ANTES_ACTUALIZAR Private Sub Form_BeforeUpdate(Cancel As Integer) Dim
		 * rs As DAO.Recordset Dim db As DAO.Database Set db = CurrentDb() If
		 * Me.NewRecord Then Set rs = db.
		 * OpenRecordset("SELECT max(consecutivo) as MaxConsecutivo FROM DETALLE_BALANCE_FISCAL"
		 * ) If Not rs.EOF Then Me.TxtConsecutivo = Nz(rs!maxconsecutivo, 0) + 1 Else
		 * Me.TxtConsecutivo = 1 End If End If End Sub
		 */
		// </CODIGO_DESARROLLADO>
		registro.getLlave().put("KEY_CONSECUTIVO", registro.getCampos().get("CONSECUTIVO"));
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		cargarTotales();
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		cargarTotales();
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub
	}

//<SET_GET_ATRIBUTOS>
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacmbCodigoCuenta
	 * 
	 * @return listacmbCodigoCuenta
	 */
	public RegistroDataModelImpl getListacmbCodigoCuenta() {
		return listacmbCodigoCuenta;
	}

	/**
	 * Asigna la lista listacmbCodigoCuenta
	 * 
	 * @param listacmbCodigoCuenta Variable a asignar en listacmbCodigoCuenta
	 */
	public void setListacmbCodigoCuenta(RegistroDataModelImpl listacmbCodigoCuenta) {
		this.listacmbCodigoCuenta = listacmbCodigoCuenta;
	}

	/**
	 * Retorna la lista listacmbCodigoCuenta
	 * 
	 * @return listacmbCodigoCuenta
	 */
	public RegistroDataModelImpl getListacmbCodigoCuentaE() {
		return listacmbCodigoCuentaE;
	}

	/**
	 * Asigna la lista listacmbCodigoCuenta
	 * 
	 * @param listacmbCodigoCuenta Variable a asignar en listacmbCodigoCuenta
	 */
	public void setListacmbCodigoCuentaE(RegistroDataModelImpl listacmbCodigoCuentaE) {
		this.listacmbCodigoCuentaE = listacmbCodigoCuentaE;
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
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	public String getIdentificadorcomprobante() {
		return identificadorcomprobante;
	}

	public void setIdentificadorcomprobante(String identificadorcomprobante) {
		this.identificadorcomprobante = identificadorcomprobante;
	}

	public String getConciliador() {
		return conciliador;
	}

	public void setConciliador(String conciliador) {
		this.conciliador = conciliador;
	}

	public String getTotalDebito() {
		return totalDebito;
	}

	public void setTotalDebito(String totalDebito) {
		this.totalDebito = totalDebito;
	}

	public String getTotalCredito() {
		return totalCredito;
	}

	public void setTotalCredito(String totalCredito) {
		this.totalCredito = totalCredito;
	}

	public String getDiferencia() {
		return diferencia;
	}

	public void setDiferencia(String diferencia) {
		this.diferencia = diferencia;
	}

	public boolean isBloqDebito() {
		return bloqDebito;
	}

	public void setBloqDebito(boolean bloqDebito) {
		this.bloqDebito = bloqDebito;
	}

	public boolean isBloqCredito() {
		return bloqCredito;
	}

	public void setBloqCredito(boolean bloqCredito) {
		this.bloqCredito = bloqCredito;
	}

	public String getIndice() {
		return indice;
	}

	public void setIndice(String indice) {
		this.indice = indice;
	}
}
