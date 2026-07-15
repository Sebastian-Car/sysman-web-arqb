/*-
 * FrmbalancefiscalControlador.java
 *
 * 1.0
 * 
 * 05/01/2023
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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.contabilidad.enums.ActualizaConfiguracionControladorUrlEnum;
import com.sysman.contabilidad.enums.BalancegeneralControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmGeneraNuevoMarcoNorControladorEnum;
import com.sysman.contabilidad.enums.FrmGeneraNuevoMarcoNorControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmbalancefiscalControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ApropiacioninicialanoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
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

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 05/01/2023
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class FrmbalancefiscalControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private Registro registro;

	private String mes;
	private String ano;
	private String mesTrabajo;
	private String anoTrabajo;
	private String mesCompara;
	private String anoCompara;
	private String cmbGenerar;
	private String codCtaInicial;
	private boolean chkSaldoCero;
	private String digitos;
	private boolean mostrarPreparadatos;
	private boolean mostrarImprimir;
	private StreamedContent archivoDescarga;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listacmbGenerar;
	/**
	 * 
	 */
	@EJB
	private EjbContabilidadCeroRemote ejbContabilidadCero;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	private String sqlInforme = "002425BALANCECONCILIACIONFISCAL";
	
	private String sqlInformeSub = "002509SUBINFORMECONCILIACIONFISCAL";

	private String informe = "002152BalancePruebaDEDUCIBLE";

	private String modulo;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmbalancefiscalControlador
	 */
	public FrmbalancefiscalControlador() {
		super();
		compania = SessionUtil.getCompania();
		cmbGenerar = "1";
		modulo = SessionUtil.getModulo();
		mostrarImprimir = false;
		mostrarPreparadatos = true;
		try {
			// 2390
			numFormulario = GeneralCodigoFormaEnum.FRM_BALANCE_CONCILIACION_FISCAL.getCodigo();
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
//<CARGAR_LISTA>
//		cargarListaCodigoInicial();
//		cargarListaCodigoFinal();
//		cargarListacmbGenerar();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR2390-AL_ABRIR Private Sub Form_Open(Cancel As Integer) DoCmd.Restore
		 * Me.Requery End Sub
		 */
		registro = new Registro();
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCodigoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmbalancefiscalControladorUrlEnum.URL7882.getValue());
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmGeneraNuevoMarcoNorControladorEnum.ANIOTRABAJO.getValue(), anoTrabajo);

		param.put(FrmGeneraNuevoMarcoNorControladorEnum.ANIOCOMPARAR.getValue(), anoCompara);

		listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigoFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmbalancefiscalControladorUrlEnum.URL9675.getValue());
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(FrmGeneraNuevoMarcoNorControladorEnum.CODIGOINICIAL.getValue(), codCtaInicial);

		param.put(FrmGeneraNuevoMarcoNorControladorEnum.ANIOTRABAJO.getValue(), anoTrabajo);

		param.put(FrmGeneraNuevoMarcoNorControladorEnum.ANIOCOMPARAR.getValue(), anoCompara);

		listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cambiarcmbGenerar() {
		if (cmbGenerar.equals("1")) {
			mostrarPreparadatos = true;
			mostrarImprimir = false;
		} else if (cmbGenerar.equals("2")) {
			mostrarImprimir = true;
			mostrarPreparadatos = false;
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		if (cmbGenerar.equals("2")) {
			generarDatos();
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirpreparardatos() {
		if (cmbGenerar.equals("1")) {
			preapararDatosAjusteFiscal(anoTrabajo, mesTrabajo);
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnSalir en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirbtnSalir() {
		// <CODIGO_DESARROLLADO>
		// JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaCodigoInicial(SelectEvent event) {
		Registro regTemp = (Registro) event.getObject();
		registro.getCampos().put("CODIGO", regTemp.getCampos().get("CODIGO"));
		codCtaInicial = regTemp.getCampos().get("CODIGO").toString();
		cargarListaCodigoFinal();
	}

	public void seleccionarFilaCodigoFinal(SelectEvent event) {
		Registro regTemp = (Registro) event.getObject();
		registro.getCampos().put("CODIGOFINAL", regTemp.getCampos().get("CODIGO"));
	}

	private void preapararDatosAjusteFiscal(String anoFiscal, String mesFiscal) {
		borrarDatosTemporales();
		try {
			ejbContabilidadCero.perpararDatosAjusteFiscal(compania, anoFiscal, mesFiscal);
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));
		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(FrmbalancefiscalControlador.class.getName()).log(Level.SEVERE, null, e);

			JsfUtil.agregarMensajeError(e.getMessage());

		}
	}

	public void cambiarMesTrabajo() {
		int mesTrabajoTemp = Integer.parseInt(mesTrabajo);
		if (!(mesTrabajoTemp > 0 && mesTrabajoTemp < 13)) {
			// mensaje a mostrar de validacion.
			JsfUtil.agregarMensajeAlerta("El mes no esta dentro del rango ");
			return;
		}
	}

	public void cambiarAnoTrabajo() {
		if (anoTrabajo != null) {
			// se acula el año a comparar
			anoCompara = String.valueOf(Integer.parseInt(anoTrabajo) - 1);
			cargarListaCodigoInicial();
		}
	}

	public void generarDatos() {
		Map<String, Object> param = new TreeMap<>();

		try {
			List<Registro> Listreg = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmbalancefiscalControladorUrlEnum.URL1899001.getValue())
											.getUrl(),
									param));
			if (Listreg.isEmpty()) {
				JsfUtil.agregarMensajeAlerta("No se han preparado los datos, por favor primero prepare los datos");
				return;
			}
			// si la lista tiene datos por lo tanto continua e inicia a crear el reporte del
			// informe
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			// se agregan los digitos
			reemplazar.put("digitos", digitos);
			if (chkSaldoCero) {
				// esta es la condicion que se debe agregar al momento de tener el check de
				// saldo cero.
				reemplazar.put("checkceorcondicion",
						" AND (DEBITO + CREDITO + SALDOANTDEBITO +  SALDOANTCREDITO + SALDONUEDEBITO + SALDONUECREDITO + SALDONUEDEBITONODEDUCIBLE  + SALDONUECREDITONODEDUCIBLE + SALDODEBITOFISCAL + SALDOCREDITOFISCAL ) > 0");
			} else {
				reemplazar.put("checkceorcondicion", "");
			}
			if (!(registro.getCampos().get("CODIGO").toString().equals("")
					&& registro.getCampos().get("CODIGOFINAL").toString().equals(""))) {
				// se realiza la condicion de las cuentas que ambas esten digligenciadas
				reemplazar.put("condicioncuentas", " AND ID BETWEEN " + registro.getCampos().get("CODIGO").toString()
						+ " AND " + registro.getCampos().get("CODIGOFINAL").toString() + "");
			} else {
				reemplazar.put("condicioncuentas", "");
			}
			String miStrSql = Reporteador.resuelveConsulta(sqlInforme, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);
			
			String miStrSqlSub = Reporteador.resuelveConsulta(sqlInformeSub, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar); // Se ejecuta la consulta del Sub reporte
			
			// se envia los valores a los parametros del reporte estos los determino al
			// momento de
			// migrarlo o crearlo.

			String firmaCont1 = ejbSysmanUtil.consultarParametro(compania, "FIRMA CONTABLE 1", modulo, new Date(),
					true);

			String cargoCont1 = ejbSysmanUtil.consultarParametro(compania, "CARGO CONTABLE 1", modulo, new Date(),
					true);

			String documentoCont1 = ejbSysmanUtil.consultarParametro(compania, "DOCUMENTO CONTABLE 1", modulo,
					new Date(), true);

			String firmaCont2 = ejbSysmanUtil.consultarParametro(compania, "FIRMA CONTABLE 2", modulo, new Date(),
					true);

			String cargoCont2 = ejbSysmanUtil.consultarParametro(compania, "CARGO CONTABLE 2", modulo, new Date(),
					true);

			String documentoCont2 = ejbSysmanUtil.consultarParametro(compania, "DOCUMENTO CONTABLE 2", modulo,
					new Date(), true);

			String firmaCont3 = ejbSysmanUtil.consultarParametro(compania, "FIRMA CONTABLE 3", modulo, new Date(),
					true);

			String cargoCont3 = ejbSysmanUtil.consultarParametro(compania, "CARGO CONTABLE 3", modulo, new Date(),
					true);

			String documentoCont3 = ejbSysmanUtil.consultarParametro(compania, "DOCUMENTO CONTABLE 3", modulo,
					new Date(), true);

			param = new TreeMap<>();
			param.put(GeneralParameterEnum.CODIGO.getName(), compania);

			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_DOCUMENTO_CONTABLE_1", documentoCont1);
			parametros.put("PR_FORMS_FRMBALANCEFISCAL_ANOTRABAJO", anoTrabajo);
			parametros.put("PR_FORMS_FRMBALANCEFISCAL_MESTRABAJO", nombreMes(mesTrabajo));
			parametros.put("PR_AHORA", "");
			parametros.put("PR_DOCUMENTO_CONTABLE_3", documentoCont3);
			parametros.put("PR_CARGO_CONTABLE_3", cargoCont3);
			parametros.put("PR_FIRMA_CONTABLE_3", firmaCont3);
			parametros.put("PR_DOCUMENTO_CONTABLE_2", documentoCont2);
			parametros.put("PR_CARGO_CONTABLE_2", cargoCont2);
			parametros.put("PR_FIRMA_CONTABLE_2", firmaCont2);
			parametros.put("PR_STRSQL", miStrSql);
			parametros.put("PR_STRSQL_BALANCEPRUEBA", miStrSqlSub);
			parametros.put("PR_COMPANIA", compania);
			parametros.put("PR_FIRMA_CONTABLE_1", firmaCont1);
			parametros.put("PR_CARGO_CONTABLE_1", cargoCont1);

			archivoDescarga = JsfUtil.exportarStreamed(informe, parametros, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));

		} catch (SystemException | JRException | IOException
				| com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			// TODO Auto-generated catch block
			Logger.getLogger(FrmbalancefiscalControlador.class.getName()).log(Level.SEVERE, null, e);

			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));
		}
	}

	private void borrarDatosTemporales() {
		Map<String, Object> params = new TreeMap<>();

		UrlBean urlDelete = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmbalancefiscalControladorUrlEnum.URL1899003.getValue());

		try {
			requestManager.delete(urlDelete.getUrl(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private String nombreMes(String numeroMes) {
		if (numeroMes.equals("1"))
			return "ENERO";

		if (numeroMes.equals("2"))
			return "FEBRERO";

		if (numeroMes.equals("3"))
			return "MARZO";

		if (numeroMes.equals("4"))
			return "ABRIL";

		if (numeroMes.equals("5"))
			return "MAYO";

		if (numeroMes.equals("6"))
			return "JUNIO";

		if (numeroMes.equals("7"))
			return "JULIO";

		if (numeroMes.equals("8"))
			return "AGOSTO";

		if (numeroMes.equals("9"))
			return "SEPTIEMBRE";

		if (numeroMes.equals("10"))
			return "OCTUBRE";

		if (numeroMes.equals("11"))
			return "NOMVIEMBRE";

		if (numeroMes.equals("12"))
			return "DICIEMBRE";

		return null;
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaCodigoInicial
	 * 
	 * @return listaCodigoInicial
	 */
	public RegistroDataModelImpl getListaCodigoInicial() {
		return listaCodigoInicial;
	}

	/**
	 * Asigna la lista listaCodigoInicial
	 * 
	 * @param listaCodigoInicial Variable a asignar en listaCodigoInicial
	 */
	public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial) {
		this.listaCodigoInicial = listaCodigoInicial;
	}

	/**
	 * Retorna la lista listaCodigoFinal
	 * 
	 * @return listaCodigoFinal
	 */
	public RegistroDataModelImpl getListaCodigoFinal() {
		return listaCodigoFinal;
	}

	/**
	 * Asigna la lista listaCodigoFinal
	 * 
	 * @param listaCodigoFinal Variable a asignar en listaCodigoFinal
	 */
	public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
		this.listaCodigoFinal = listaCodigoFinal;
	}

	/**
	 * Retorna la lista listacmbGenerar
	 * 
	 * @return listacmbGenerar
	 */
	public List<Registro> getListacmbGenerar() {
		return listacmbGenerar;
	}

	/**
	 * Asigna la lista listacmbGenerar
	 * 
	 * @param listacmbGenerar Variable a asignar en listacmbGenerar
	 */
	public void setListacmbGenerar(List<Registro> listacmbGenerar) {
		this.listacmbGenerar = listacmbGenerar;
	}
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>

	public Registro getRegistro() {
		return registro;
	}

	public void setRegistro(Registro registro) {
		this.registro = registro;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getMesTrabajo() {
		return mesTrabajo;
	}

	public void setMesTrabajo(String mesTrabajo) {
		this.mesTrabajo = mesTrabajo;
	}

	public String getCmbGenerar() {
		return cmbGenerar;
	}

	public void setCmbGenerar(String cmbGenerar) {
		this.cmbGenerar = cmbGenerar;
	}

	public String getAnoTrabajo() {
		return anoTrabajo;
	}

	public void setAnoTrabajo(String anoTrabajo) {
		this.anoTrabajo = anoTrabajo;
	}

	public boolean isChkSaldoCero() {
		return chkSaldoCero;
	}

	public void setChkSaldoCero(boolean chkSaldoCero) {
		this.chkSaldoCero = chkSaldoCero;
	}

	public String getDigitos() {
		return digitos;
	}

	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public boolean isMostrarPreparadatos() {
		return mostrarPreparadatos;
	}

	public void setMostrarPreparadatos(boolean mostrarPreparadatos) {
		this.mostrarPreparadatos = mostrarPreparadatos;
	}

	public boolean isMostrarImprimir() {
		return mostrarImprimir;
	}

	public void setMostrarImprimir(boolean mostrarImprimir) {
		this.mostrarImprimir = mostrarImprimir;
	}

}
