package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisRegistrosAbiertosCuentasControladorEnum;
import com.sysman.presupuesto.enums.LisRegistrosAbiertosCuentasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 07/07/2016 17:52:35 -- Modificado por jrodriguezr
 * @modified jsforero
 * @version 2. 19/04/2017 Se realizo el refactory.
 * 
 * @author jlramirez
 * @version 3, 24/04/2017, Manejo de EJBs
 * 
 * @author eamaya
 * @version 4.0, 12/06/2017 Cambio código formulario y actualización de
 *          ConnectorPool
 * 
 * @author jgomez
 * @version 5, 09/08/2018 Se ajusta para que el reporte por excel salga plano
 * 
 * 
 * @author gfigueredo
 * @version 6, 31/05/2021, Se cambia el parametro MANEJA AUXILIAR POR FUENTE EN
 *          PRESUPUESTO por los parametros REPORTE REGISTROS COMPROMISO ABIERTOS
 *          POR CUENTA y REPORTE EXCEL REGISTROS COMPROMISO ABIERTOS POR CUENTA,
 *          debido a que el uso del parametro MANEJA AUXILIAR POR FUENTE EN
 *          PRESUPUESTO en este controlador, afecta otros procesos en la
 *          aplicación.
 * @see #REPORTE_REGISTROS_COMPROMISO_ABIERTOS_POR_CUENTA
 * @see #REPORTE_EXCEL_REGISTROS_COMPROMISO_ABIERTOS_POR_CUENTA
 * @see #generaReporte(FORMATOS)
 * Integrar
 */
@ManagedBean
@ViewScoped
public class LisRegistrosAbiertosCuentasControlador extends BeanBaseModal {
	private static final String REPORTE_REGISTROS_COMPROMISO_ABIERTOS_POR_CUENTA = "REPORTE REGISTROS COMPROMISO ABIERTOS POR CUENTA";
	private static final String REPORTE_EXCEL_REGISTROS_COMPROMISO_ABIERTOS_POR_CUENTA = "REPORTE EXCEL REGISTROS COMPROMISO ABIERTOS POR CUENTA";
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private String tipoCuenta;
	private Date fechaInicial;
	private Date fechaFinal;
	private StreamedContent archivoDescarga;
	@EJB
	private EjbSysmanUtilRemote sysmanUtil;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;
	private RegistroDataModelImpl listaTerceroInicial;
	private RegistroDataModelImpl listaTerceroFinal;
	// </DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Creates a new instance of LisRegistrosAbiertosCuentasControlador
	 */
	public LisRegistrosAbiertosCuentasControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.LIS_REGISTROS_ABIERTOS_CUENTAS_CONTROLADOR.getCodigo();

			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(LisRegistrosAbiertosCuentasControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		fechaInicial = fechaFinal = new Date();
		tipoCuenta = "1";
		cargarListaCuentaInicial();
		cargarListaTerceroInicial();
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {

		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	public void cargarListaCuentaInicial() {
		String fechaIni = "";
		try {
			fechaIni = SysmanFunciones.convertirAFechaCadena(fechaInicial);
		} catch (ParseException e) {
			Logger.getLogger(LisRegistrosAbiertosCuentasControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LisRegistrosAbiertosCuentasControladorUrlEnum.URL3829.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(LisRegistrosAbiertosCuentasControladorEnum.FECHAINICIO.getValue(), fechaIni);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID");
	}

	public void cargarListaCuentaFinal() {
		String fechaIni = "";
		try {
			fechaIni = SysmanFunciones.convertirAFechaCadena(fechaInicial);
		} catch (ParseException e) {
			Logger.getLogger(LisRegistrosAbiertosCuentasControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LisRegistrosAbiertosCuentasControladorUrlEnum.URL4888.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(LisRegistrosAbiertosCuentasControladorEnum.CUENTAINICIAL.getValue(), cuentaInicial);
		param.put(LisRegistrosAbiertosCuentasControladorEnum.FECHAINICIO.getValue(), fechaIni);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID");
	}

	public void cargarListaTerceroInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LisRegistrosAbiertosCuentasControladorUrlEnum.URL7549.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"NIT");
	}

	public void cargarListaTerceroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LisRegistrosAbiertosCuentasControladorUrlEnum.URL8027.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(LisRegistrosAbiertosCuentasControladorEnum.NITINICIAL.getValue(), terceroInicial);

		listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"NIT");
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaReporte(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaReporte(FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}

	private boolean auxGen() {
		boolean aux = false;
		if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB367"));
			aux = true;
		}
		if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB368"));
			aux = true;
		}
		if (SysmanFunciones.validarVariableVacio(terceroInicial)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB369"));
			aux = true;
		}
		if (SysmanFunciones.validarVariableVacio(terceroFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB370"));
			aux = true;
		}
		if (fechaInicial == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB373"));
			aux = true;
		}
		if (fechaFinal == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB374"));
			aux = true;
		}
		return aux;
	}

	private void generaReporte(FORMATOS formato) {
		try {
			String reporte = null;
			String excelSalida = null;
			if (auxGen()) {
				return;
			}

			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();

			String fechaIni;
			String fechaFin;
			String mostrarContrato;

			fechaIni = SysmanFunciones.convertirAFechaCadena(fechaInicial);
			fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

			mostrarContrato = sysmanUtil.consultarParametro(compania,
					"MOSTRAR CONTRATO EN INFORME REGISTROS ABIERTOS POR CUENTAS", SessionUtil.getModulo(), new Date(),
					true);
			if (mostrarContrato == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB376"));
				return;
			}

			reemplazar.put("cuentaInicial", cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("fechaInicial", fechaIni);
			reemplazar.put("fechaFinal", fechaFin);
			reemplazar.put("terceroInicial", terceroInicial);
			reemplazar.put("terceroFinal", terceroFinal);
			reemplazar.put("tipoCuenta", tipoCuenta);
			
			
			if ("SI".equals(obtenerParametro("MANEJA REPORTES IDIPRON", "NO"))) {
				reporte = "002156LisRegAbiertosCuentasIDIPRON";
				excelSalida = "002156LisRegAbiertosCuentasIDIPRON";
			} else {
				reporte = obtenerParametro(REPORTE_REGISTROS_COMPROMISO_ABIERTOS_POR_CUENTA, "001816LisRegAbiertosCuentasF");
				excelSalida = obtenerParametro(REPORTE_EXCEL_REGISTROS_COMPROMISO_ABIERTOS_POR_CUENTA, "001819LisRegAbiertosF_Excel");
			}
			
			int modulo = Integer.parseInt(SessionUtil.getModulo());
			
			//INI 7719211 _PRESUPUESTO(12/10/2022 MROSERO)
			if ("SI".equals(obtenerParametro("MANEJA REFERENCIA Y DEPENDENCIA PARA INFORMES", "NO")))
			{
			reemplazar.put("referencia", "referencia,");
			reemplazar.put("dependencia", "dependencia,");
			} else {
				reemplazar.put("referencia", " ");
				reemplazar.put("dependencia", " ");
			}			
			//INI 7719211 _PRESUPUESTO(12/10/2022 MROSERO)
			
			parametros.put("PR_FECHAINICIAL", fechaIni);
			parametros.put("PR_FECHAFINAL", fechaFin);
			parametros.put("PR_MOSTRAR_CONTRATO", mostrarContrato);

			archivoDescarga = JsfUtil.exportarExcelPlano(reporte, excelSalida, ConectorPool.ESQUEMA_SYSMAN, formato,
					reemplazar, parametros, modulo);

		}

		catch (JRException | IOException | SysmanException | ParseException | SystemException | SQLException
				| DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Obtiene el valor almacenado en la base de datos para el parametro ingresado.
	 *
	 * @param nombreParametro Nombre del parametro a consultar en la base de datos.
	 * @param valorDefault    Valor por omision en caso de nulo.
	 * @return valor asignado al parametro
	 */
	private String obtenerParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = sysmanUtil.consultarParametro(SessionUtil.getCompania(), nombreParametro,
					SessionUtil.getModulo(), new Date(), true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	public void cambiarFechaInicial() {
		// <CODIGO_DESARROLLADO>
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaCuentaInicial();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarFechaFinal() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get("ID").toString();
		cargarListaCuentaFinal();
		cuentaFinal = SysmanConstantes.DEFECTOINICIAL_STRING;
	}

	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get("ID").toString();
	}

	public void seleccionarFilaTerceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroInicial = registroAux.getCampos().get("NIT").toString();
		cargarListaTerceroFinal();
		terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	}

	public void seleccionarFilaTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroFinal = registroAux.getCampos().get("NIT").toString();
	}

	// </METODOS_COMBOS_GRANDES>

	// <SET_GET_ATRIBUTOS>
	public String getCuentaInicial() {
		return cuentaInicial;
	}

	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	public String getCuentaFinal() {
		return cuentaFinal;
	}

	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	public String getTerceroInicial() {
		return terceroInicial;
	}

	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	public String getTerceroFinal() {
		return terceroFinal;
	}

	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public String getTipoCuenta() {
		return tipoCuenta;
	}

	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}

	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}

	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}

	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}

	public RegistroDataModelImpl getListaTerceroInicial() {
		return listaTerceroInicial;
	}

	public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
		this.listaTerceroInicial = listaTerceroInicial;
	}

	public RegistroDataModelImpl getListaTerceroFinal() {
		return listaTerceroFinal;
	}

	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
