/*-
 * FrmbalancefiscalimputacionsControlador.java
 *
 * 1.0
 * 
 * 19/01/2023
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.FrmbalancefiscalControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 19/01/2023
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class FrmbalancefiscalimputacionsControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private String comprobante;

	private String ano;
	
	private String mes;

	private StreamedContent archivoDescarga;

	private String modulo;
	
	private boolean bloqImprimir;
	
	private boolean bloqDetalle;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	private boolean bloqFecha;
	
	String dia = "";

//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmbalancefiscalimputacionsControlador
	 */
	public FrmbalancefiscalimputacionsControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_BALANCE_CONCILIACION_FISCAL_IMPUTACION.getCodigo();
			validarPermisos();
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				comprobante = parametrosEntrada.get("COMPROBTANTE").toString();
				rid = (Map<String, Object>) parametrosEntrada.get("RID");
			}
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.BALANCE_FISCAL;
		buscarLlave();
		asignarOrigenDatos();

	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	}
	/**
	 * Se realiza la asignacion de la variable origenGrilla por la consulta
	 * correspondiente de la grilla del formulario, se hace la asignacion de dicha
	 * consulta a los objetos listaInicial y listaInicialF
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */

//<METODOS_CARGAR_LISTA>	
//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CmbDetalle en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCmbDetalle() {
		// <CODIGO_DESARROLLADO>
		comprobante = registro.getCampos().get("COMPROBANTE").toString();
		SimpleDateFormat formatFecha;
		formatFecha = new SimpleDateFormat("yyyy");
		ano = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		formatFecha = new SimpleDateFormat("dd");
		dia = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		formatFecha = new SimpleDateFormat("MM");
		mes = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		
		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRM_BALANCE_CONCILIACION_FISCAL_IMPUTACION_DETALLE.getCodigo()));
		Map<String, Object> params = new HashMap<>();
		params.put("COMPROBTANTE", comprobante);
		params.put("ANO", ano);
		params.put("MES", mes);
		params.put("DIA", dia);
		params.put("RID", css);
		direccionador.setParametros(params);
		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @throws ParseException
	 *
	 */
	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		try {
			comprobante = registro.getCampos().get("COMPROBANTE").toString();
			//Date ano = SysmanFunciones.convertirAFecha(registro.getCampos().get("FECHA").toString(), "DD/MM/YYYY");
			Map<String, Object> param = new TreeMap<>();
			String informe = "002154COMPFISCAL";

			String sqlInforme = "002426COMP_FISCAL";
			
			String[] partes = registro.getCampos().get("FECHADATE").toString().split("/");
			SimpleDateFormat formatFecha = new SimpleDateFormat("YY");
                   
			// si la lista tiene datos por lo tanto continua e inicia a crear el reporte del
			// informe
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("compania", compania);
			reemplazar.put("ano", partes[2]);
			reemplazar.put("comprobante", comprobante);
			

			String miStrSql = Reporteador.resuelveConsulta(sqlInforme, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);
			// falta enviarle los parametros al reporte estos los determino al momento de
			// migrarlo o crearlo.

			String firmaCont1 = ejbSysmanUtil.consultarParametro(compania, "FIRMA CONTABLE 1", modulo, new Date(),
					true);

			String cargoCont1 = ejbSysmanUtil.consultarParametro(compania, "CARGO CONTABLE 1", modulo, new Date(),
					true);

			param = new TreeMap<>();

			param.put(GeneralParameterEnum.CODIGO.getName(), compania);
			parametros.put("PR_STRSQL", miStrSql);
			parametros.put("PR_STRSQL_BALANCEPRUEBA", miStrSql);
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD", firmaCont1);
			parametros.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD", cargoCont1);
			parametros.put("PR_GETUSER", SessionUtil.getUser().getCodigo());

			archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                    ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
			
		} catch (SystemException | JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			// TODO Auto-generated catch block
			Logger.getLogger(FrmbalancefiscalimputacionsControlador.class.getName()).log(Level.SEVERE, null, e);

			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Impresora en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirImpresora() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	
//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>	
//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR2391-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir
		 * GetIdModulo, Me.Name Dim strsql As String strsql =
		 * " SELECT *                       " & vbCrLf & _
		 * " FROM BALANCE_FISCAL           " & vbCrLf & _ " WHERE Compania = '" &
		 * Getcompany() & "' " & vbCrLf & _ "   AND BALANCE_FISCAL.ANO = " &
		 * getAnoFiscal() & " " & vbCrLf & _ "   AND BALANCE_FISCAL.MES = " &
		 * getMesFiscal() & " " & vbCrLf & _ " ORDER BY BALANCE_FISCAL.COMPANIA DESC " &
		 * vbCrLf & _ "        , BALANCE_FISCAL.ANO DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.MES DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.DIA DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.COMPROBANTE DESC " & vbCrLf Me.RecordSource =
		 * strsql strsql =
		 * " SELECT COMPROBANTE,FECHA,OBSERVACIONES  AS DESCRIPCION                    "
		 * & vbCrLf & _ " FROM BALANCE_FISCAL            " & vbCrLf & _
		 * " WHERE Compania = '" & Getcompany() & "' " & vbCrLf & _
		 * "   AND BALANCE_FISCAL.ANO = " & getAnoFiscal() & " " & vbCrLf & _
		 * "   AND BALANCE_FISCAL.MES = " & getMesFiscal() & " " & vbCrLf & _
		 * " ORDER BY BALANCE_FISCAL.COMPANIA DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.ANO DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.MES DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.DIA DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.COMPROBANTE DESC " & vbCrLf Me.cmbNumero.RowSource
		 * = strsql Me.Recalc End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		
		if(!registro.getCampos().isEmpty()) {
			bloqImprimir = false;
			bloqDetalle = false;
			SimpleDateFormat formatFecha;
			formatFecha = new SimpleDateFormat("yyyy");
			ano = formatFecha.format((Date) registro.getCampos().get("FECHA"));
			Map<String, Object> parametros = new TreeMap<>();
			
			parametros.put(GeneralParameterEnum.COMPANIA.getName(),
	                        compania);
			parametros.put(GeneralParameterEnum.ANO.getName(), ano);
			parametros.put(GeneralParameterEnum.COMPROBANTE.getName(), registro.getCampos().get("COMPROBANTE"));
            UrlBean urlLista = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                    		FrmbalancefiscalControladorUrlEnum.URL1901004
                                            .getValue());
           
			Registro total;
			try {
				total = RegistroConverter.toRegistro(
					requestManager.get(urlLista.getUrl(), parametros));
				
				int detalles = 0;
				if(total != null) {
					 detalles = Integer.parseInt(total.getCampos().get("TOTAL").toString());
					 
					 if(detalles > 0) {
						bloqFecha = true;
					}else {
						bloqFecha = false;
					}
				}
		
			} catch (SystemException e) {
				e.printStackTrace();
			}
			
			if(registro.getCampos().get("VALOR") == null) {
				registro.getCampos().put("VALOR", 0);
			}else {
				registro.getCampos().put("VALOR", registro.getCampos().get("VALOR").toString());
			}			
		}else{
			// se bloquean botones de detalle y de imprimir 
			bloqImprimir = true;
			bloqDetalle = true;
			bloqFecha = false;
			// Se calcula el consecutivo a asignar
			 try {
				Map<String, Object> param = new TreeMap<>();
				mes = String.valueOf(SysmanFunciones.mes(new Date()));
				if(mes.length() == 1) {
					mes = "0"+mes;
				}
				ano = String.valueOf(SysmanFunciones.ano(new Date()));
				dia = String.valueOf(SysmanFunciones.dia(new Date()));
		        param.put(GeneralParameterEnum.COMPANIA.getName(),
		                        compania);
		        param.put(GeneralParameterEnum.ANO.getName(), ano);
	            UrlBean urlListMeI = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                    		FrmbalancefiscalControladorUrlEnum.URL1900001
	                                            .getValue());
	           
				Registro consecutivo = RegistroConverter.toRegistro(
				requestManager.get(urlListMeI.getUrl(), param));
				if(consecutivo.getCampos().get("COMPROBANTE") == null) {
					String consecutivoTemp = ano+"000001";
					registro.getCampos().put("COMPROBANTE", consecutivoTemp);					
				}else {
					int consecutivoTemp = Integer.parseInt(consecutivo.getCampos().get("COMPROBANTE").toString());
					registro.getCampos().put("COMPROBANTE", consecutivoTemp+1);
				}
				
				registro.getCampos().put("FECHA", new Date());
				registro.getCampos().put("VALOR", 0);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		// </CODIGO_DESARRsOLLADO>
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
		SimpleDateFormat formatFecha;
		formatFecha = new SimpleDateFormat("yyyy");
		ano = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		formatFecha = new SimpleDateFormat("dd");
		dia = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		formatFecha = new SimpleDateFormat("MM");
		mes = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("MES", mes);
		registro.getCampos().put("ANO", ano);
		registro.getCampos().put("DIA", dia);
		registro.getCampos().remove("VALOR");
		// </CODIGO_DESARROLLADO>
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
		 * FR2391-ANTES_ACTUALIZAR Private Sub Form_BeforeUpdate(Cancel As Integer) Dim
		 * strsql As String Dim rs As DAO.Recordset Dim db As DAO.Database Dim
		 * CONSECUTIVO As Double Dim DATO As String Set db = CurrentDb() On Error Resume
		 * Next If Me.NewRecord Then If IsNull(Me!TxtObservaciones) Or
		 * Nz(Me!TxtObservaciones, "") = "" Then MsgBox
		 * "Debe digitar la descripción  del comprobante.", vbInformation + vbOKOnly,
		 * "Sysman Software" Me!TxtObservaciones.SetFocus Cancel = True SendKeys "{ESC}"
		 * Exit Sub End If End If If Me.NewRecord Then strsql =
		 * " SELECT top 1 COMPROBANTE                      " & vbCrLf & _
		 * " FROM BALANCE_FISCAL                               " & vbCrLf & _
		 * " WHERE Compania = '" & Getcompany() & "'           " & vbCrLf & _
		 * "   AND BALANCE_FISCAL.ANO = " & Forms!frm_Balance_Fiscal!txtAno & " " &
		 * vbCrLf & _ "   AND BALANCE_FISCAL.MES = " & Forms!frm_Balance_Fiscal!txtMes &
		 * " " & vbCrLf & _ " ORDER BY BALANCE_FISCAL.COMPANIA DESC             " &
		 * vbCrLf & _ "        , BALANCE_FISCAL.ANO DESC                  " & vbCrLf & _
		 * "        , BALANCE_FISCAL.MES DESC                  " & vbCrLf & _
		 * "        , BALANCE_FISCAL.DIA DESC                  " & vbCrLf & _
		 * "        , BALANCE_FISCAL.COMPROBANTE DESC " & vbCrLf Set rs =
		 * db.OpenRecordset(strsql) If Not rs.EOF Then Me!cmbNumero = rs!Comprobante + 1
		 * Else Me!cmbNumero = Val(Forms!frm_Balance_Fiscal!txtAno &
		 * Format(Forms!frm_Balance_Fiscal!txtMes, "00") & Format("0", "0000")) + 1 End
		 * If End If GuardarRegistro strsql =
		 * " SELECT COMPROBANTE,FECHA,OBSERVACIONES AS DESCRIPCION                       "
		 * & vbCrLf & _ " FROM BALANCE_FISCAL           " & vbCrLf & _
		 * " WHERE COMPANIA = '" & Getcompany() & "' " & vbCrLf & _
		 * "   AND BALANCE_FISCAL.ANO = " & Forms!frm_Balance_Fiscal!txtAno & " " &
		 * vbCrLf & _ "   AND BALANCE_FISCAL.MES = " & Forms!frm_Balance_Fiscal!txtMes &
		 * " " & vbCrLf & _ " ORDER BY BALANCE_FISCAL.COMPANIA DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.ANO DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.MES DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.DIA DESC " & vbCrLf & _
		 * "        , BALANCE_FISCAL.COMPROBANTE DESC " & vbCrLf Me.cmbNumero.RowSource
		 * = strsql Me.Recalc End Sub
		 */
		// </CODIGO_DESARROLLADO>
//		registro.getCampos().put("COMPANIA", compania);
		SimpleDateFormat formatFecha;
		formatFecha = new SimpleDateFormat("yyyy");
		ano = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		formatFecha = new SimpleDateFormat("dd");
		dia = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		formatFecha = new SimpleDateFormat("MM");
		mes = formatFecha.format((Date) registro.getCampos().get("FECHA"));
		
		if(!accion.equals("i")) {
			registro.getCampos().remove("VALOR");
			registro.getCampos().remove("CREDITO");
			registro.getCampos().remove("FECHADATE");
			registro.getCampos().remove("DEBITO");
			registro.getCampos().remove("COMPROBANTE");
			registro.getCampos().remove("COMPANIA");
			registro.getCampos().put("MES", mes);
			registro.getCampos().put("ANO", ano);
			registro.getCampos().put("DIA", dia);
		}
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
		// </CODIGO_DESARROLLADO>
//		registro.getCampos().put("FECHADATE", registro.getCampos().get("FECHA").toString());
//		
//		
//		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/YYYY");
//		String fechaSolicitud = registro.getCampos().get("FECHA").toString();
//		
//		registro.getCampos().put("FECHA",  fechaSolicitud);
		
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

		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
		params.put(GeneralParameterEnum.MES.getName(), registro.getCampos().get("MES"));
		params.put(GeneralParameterEnum.ANO.getName(), ano);
		params.put(GeneralParameterEnum.DIA.getName(), registro.getCampos().get("DIA"));
		params.put(GeneralParameterEnum.COMPROBANTE.getName(), registro.getCampos().get("COMPROBANTE"));
		UrlBean urlDelete = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmbalancefiscalControladorUrlEnum.URL1901005.getValue());

		try {
			requestManager.delete(urlDelete.getUrl(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}
//<SET_GET_ATRIBUTOS>
//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
//</SET_GET_ADICIONALES>

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public boolean isBloqImprimir() {
		return bloqImprimir;
	}

	public void setBloqImprimir(boolean bloqImprimir) {
		this.bloqImprimir = bloqImprimir;
	}

	public boolean isBloqDetalle() {
		return bloqDetalle;
	}

	public void setBloqDetalle(boolean bloqDetalle) {
		this.bloqDetalle = bloqDetalle;
	}

	public boolean isBloqFecha() {
		return bloqFecha;
	}

	public void setBloqFecha(boolean bloqFecha) {
		this.bloqFecha = bloqFecha;
	}
	
	
}
