package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
 * @version 1, 11/07/2016
 * 
 * @version 2, 20/04/2017
 * @author jreina se realizaron los cambios de refactoring
 * 
 * @author jlramirez
 * @version 3, 24/04/2017, Manejo EJBs
 * 
 * @author eamaya
 * @version 4.0, 12/06/2017 Cambio código formulario y actualización de
 *          ConnectorPool
 * 
 * @author jgomez
 * @version 5, 10/08/2018 Se ajusta para que el reporte por excel salga plano
 * 
 * @author gfigueredo
 * @version 6, 31/05/2021, Se cambia el parametro MANEJA AUXILIAR POR FUENTE EN
 *          PRESUPUESTO por los parametros REPORTE REGISTROS OBLIGACION ABIERTOS
 *          POR CUENTA y REPORTE EXCEL REGISTROS OBLIGACION ABIERTOS POR CUENTA,
 *          debido a que el uso del parametro MANEJA AUXILIAR POR FUENTE EN
 *          PRESUPUESTO en este controlador, afecta otros procesos en la
 *          aplicación.
 * @see #REPORTE_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA
 * @see #REPORTE_EXCEL_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA
 * @see #generaReporte(FORMATOS)
 * @author gfigueredo
 * @version 6, 16/07/2021, Se reversa el cambio anterior, debido que para este proceso
 * no se especifica que no se deba tener en cuenta el parametro MANEJA AUXILIAR POR FUENTE EN
 *          PRESUPUESTO
 */
@ManagedBean
@ViewScoped
public class RegistrosObligacionAbiertosControlador extends BeanBaseModal {

	private static final String REPORTE_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA = "REPORTE REGISTROS OBLIGACION ABIERTOS POR CUENTA";
	private static final String REPORTE_EXCEL_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA = "REPORTE EXCEL REGISTROS OBLIGACION ABIERTOS POR CUENTA";
	/**
	 * Varible que almacena el dato de compania
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Variable que almacena el dato de tipo cuenta
	 */
	private String tipoCuenta;
	/**
	 * Variable que almacena el datode fecha inicial
	 */
	private Date fechaInicial;
	/**
	 * Variable que almacena el dato de fecha final
	 */
	private Date fechaFinal;
	/**
	 * Variable que almacena el dato de modulo
	 */
	private final String modulo;
	/**
	 * Variable que almacena el dato de codigoInicio
	 */
	private String codigoInicio;
	/**
	 * Variable que almacena el dato de codigoFinal
	 */
	private String codigoFinal;
	/**
	 * Variable que almacena el archivo de descarga
	 */
	private StreamedContent archivoDescarga;

	private String fechaIni;
	private String fechaFin;
	private String nomEncargTesoreria;
	private String nombreGerente;
	private String cargoEncargadoTesoreria;
	private String cargoGerente;
	private String reporte;
	private String excelSalida;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
	private EjbSysmanUtilRemote sysmanUtil;

	/**
	 * Creates a new instance of RegistrosObligacionAbiertosControlador
	 */
	public RegistrosObligacionAbiertosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();

		try {
			numFormulario = GeneralCodigoFormaEnum.REGISTROS_OBLIGACION_ABIERTOS_CONTROLADOR.getCodigo();

			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(RegistrosObligacionAbiertosControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		fechaInicial = fechaFinal = new Date();
		tipoCuenta = "1";

		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
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

	public boolean validarNulos() {
		if (fechaInicial == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB356"));
			return false;
		}
		if (fechaFinal == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB357"));
			return false;
		}
		return true;
	}

	private void generaReporte(FORMATOS formato) {
		try {
			Map<String, Object> reemplazar = new TreeMap<>();
			Map<String, Object> parametros = new HashMap<>();

			if ("SI".equals(obtenerParametro(
					"MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
					"NO"))) {
				reporte = "001817LisReoAbiertosF";
				excelSalida = "001818LisReoAbiertosCuentasF_Excel";
			}
			else {
				reporte = "000996LisReoAbiertos";
				excelSalida = "001000LisReoAbiertosCuentas_Excel";
			}

			nomEncargTesoreria = sysmanUtil.consultarParametro(compania, "NOMBRE ENCARGADO DE TESORERIA",
					SessionUtil.getModulo(), new Date(), true);

			nombreGerente = sysmanUtil.consultarParametro(compania, "NOMBRE GERENTE", SessionUtil.getModulo(),
					new Date(), true);

			cargoEncargadoTesoreria = sysmanUtil.consultarParametro(compania, "CARGO ENCARGADO DE TESORERIA",
					SessionUtil.getModulo(), new Date(), true);

			cargoGerente = sysmanUtil.consultarParametro(compania, "CARGO GERENTE", SessionUtil.getModulo(), new Date(),
					true);

			fechaIni = SysmanFunciones.convertirAFechaCadena(fechaInicial);
			fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

			reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazar.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			reemplazar.put("tipoCuenta", tipoCuenta);

			parametros.put("PR_FECHAINICIAL", fechaIni);
			parametros.put("PR_FECHAFINAL", fechaFin);
			parametros.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA", nomEncargTesoreria);
			parametros.put("PR_NOMBRE_GERENTE", nombreGerente);
			parametros.put("PR_CARGO_ENCARGADO_DE_TESORERIA", cargoEncargadoTesoreria);
			parametros.put("PR_CARGO_GERENTE", cargoGerente);

			archivoDescarga = JsfUtil.exportarExcelPlano(reporte, excelSalida, ConectorPool.ESQUEMA_SYSMAN, formato,
					reemplazar, parametros, Integer.parseInt(modulo));

		} catch (JRException | IOException | SysmanException | ParseException | SystemException | SQLException
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
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciaInicial
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoInicio = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciaFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoFinal = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	// <SET_GET_ATRIBUTOS>
	public String getTipoCuenta() {
		return tipoCuenta;
	}

	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
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

	public String getCodigoInicio() {
		return codigoInicio;
	}

	public void setCodigoInicio(String codigoInicio) {
		this.codigoInicio = codigoInicio;
	}

	public String getCodigoFinal() {
		return codigoFinal;
	}

	public void setCodigoFinal(String codigoFinal) {
		this.codigoFinal = codigoFinal;
	}

	public String getModulo() {
		return modulo;
	}

	public EjbSysmanUtilRemote getSysmanUtil() {
		return sysmanUtil;
	}

	public void setSysmanUtil(EjbSysmanUtilRemote sysmanUtil) {
		this.sysmanUtil = sysmanUtil;
	}

	public String getCompania() {
		return compania;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(String fechaIni) {
		this.fechaIni = fechaIni;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getNomEncargTesoreria() {
		return nomEncargTesoreria;
	}

	public void setNomEncargTesoreria(String nomEncargTesoreria) {
		this.nomEncargTesoreria = nomEncargTesoreria;
	}

	public String getNombreGerente() {
		return nombreGerente;
	}

	public void setNombreGerente(String nombreGerente) {
		this.nombreGerente = nombreGerente;
	}

	public String getCargoEncargadoTesoreria() {
		return cargoEncargadoTesoreria;
	}

	public void setCargoEncargadoTesoreria(String cargoEncargadoTesoreria) {
		this.cargoEncargadoTesoreria = cargoEncargadoTesoreria;
	}

	public String getCargoGerente() {
		return cargoGerente;
	}

	public void setCargoGerente(String cargoGerente) {
		this.cargoGerente = cargoGerente;
	}

	public String getReporte() {
		return reporte;
	}

	public void setReporte(String reporte) {
		this.reporte = reporte;
	}

	public String getExcelSalida() {
		return excelSalida;
	}

	public void setExcelSalida(String excelSalida) {
		this.excelSalida = excelSalida;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
