package com.sysman.nomina;

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

import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.enums.IncapacidadesControladorEnum;
import com.sysman.nomina.enums.IncapacidadesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @author jgomez
 * @version 1, 27/10/2015
 * 
 * @author amonroy
 * @version 1.1, 23/03/2017 Ajustes de buenas practicas de programacion
 *          sugeridas por la herramienta SonarLint
 * 
 * @version 2, 09/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los
 *         combos, en el origen de grilla y de datos.
 * 
 * @version 3, 07/06/20187
 * @author jmalaver, Se añaden validaciones en el método "insertarDespues" que
 *         bloquean la modificación de un registro creado.
 */

@ManagedBean
@ViewScoped
public class IncapacidadesControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * FECHAFINREAL en el formulario, almacena el texto FECHAFINREAL
	 */
	private final String cFechaFinReal;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * FECHAINIREAL en el formulario, almacena el texto FECHAINIREAL
	 */
	private final String cFechaIniReal;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * FECHA_FIN en el formulario, almacena el texto FECHA_FIN
	 */
	private final String cFechaFin;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * FECHA_INICIO en el formulario, almacena el texto FECHA_INICIO
	 */
	private final String cFechaInicio;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al
	 * elemento almacenado en el archivo de properties "MSM_TRANS_INTERRUMPIDA" en
	 * el formulario, almacena el texto "MSM_TRANS_INTERRUMPIDA"
	 */
	private final String cTxInterrumpida;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * "PERIODO" en el formulario, almacena el texto "PERIODO"
	 */
	private final String cPeriodo;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * TIPOENFERMEDAD en el formulario, almacena el texto TIPOENFERMEDAD
	 */
	private final String cTipoEnfermedad;

	private final String consCodigoEn;

	private List<Registro> listaIncapacidad;
	private List<Registro> listaAno;
	private List<Registro> listaMes;
	private List<Registro> listaPeriodo;
	private RegistroDataModelImpl listaTIPOENFERMEDAD;
	private String idEmpleado;
	private String nombreEmpleado;
	private String cedula;
	private String nombreTipoEnfermedad;
	private String procesoNomina;
	private String moduloNomina;

	private String anio;
	private String mes;
	private String periodo;
	private boolean vacaciones;

	private String tituloForm;
	private Map<String, Object> parametrosEntrada;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbNominaCeroRemote ejbNominaCero;

	@EJB
	private EjbNominaDosRemote ejbNominaDos;

	public IncapacidadesControlador() {
		super();
		compania = SessionUtil.getCompania();
		parametrosEntrada = SessionUtil.getFlash();
		cFechaFinReal = "FECHAFINREAL";
		cFechaIniReal = "FECHAINIREAL";
		cFechaFin = "FECHA_FIN";
		cFechaInicio = "FECHA_INICIO";
		cTxInterrumpida = "MSM_TRANS_INTERRUMPIDA";
		cPeriodo = "PERIODO";
		cTipoEnfermedad = "TIPOENFERMEDAD";
		consCodigoEn = "CODIGOEN";

		try {
			if (parametrosEntrada != null) {
				idEmpleado = (String) parametrosEntrada.get("idEmpleado");
				nombreEmpleado = (String) parametrosEntrada.get("nombreEmpleado");
				cedula = (String) parametrosEntrada.get("cedula");
				vacaciones = (boolean) SysmanFunciones.nvl(parametrosEntrada.get("vacaciones"), false);
			}
			procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
			anio = (String) SessionUtil.getSessionVar("anioNomina");
			mes = (String) SessionUtil.getSessionVar("mesNomina");
			periodo = (String) SessionUtil.getSessionVar("periodoNomina");
			moduloNomina = SessionUtil.getModulo();
			numFormulario = GeneralCodigoFormaEnum.INCAPACIDADES_CONTROLADOR.getCodigo();

			validarPermisos();
		} catch (SysmanException ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@Override
	public void iniciarListas() {
		cargarListaTIPOENFERMEDAD();
		cargarListaIncapacidad();
		cargarListaAno();
	}

	@Override
	public void iniciarListasSub() {
		cargarListaMes();
		cargarListaPeriodo();
	}

	@Override
	public void iniciarListasSubNulo() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.INCAPACIDADES;
		buscarLlave();
		asignarOrigenDatos();
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), idEmpleado);
	}

	public void cargarListaIncapacidad() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaIncapacidad = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													IncapacidadesControladorUrlEnum.URL6096.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaAno() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													IncapacidadesControladorUrlEnum.URL6471.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaMes() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), procesoNomina);
			param.put(GeneralParameterEnum.ANO.getName(), anio);

			listaMes = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													IncapacidadesControladorUrlEnum.URL6830.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaPeriodo() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), procesoNomina);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(GeneralParameterEnum.MES.getName(), mes);

			listaPeriodo = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													IncapacidadesControladorUrlEnum.URL4658.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaTIPOENFERMEDAD() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(IncapacidadesControladorUrlEnum.URL8965.getValue());
		listaTIPOENFERMEDAD = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, true,
				consCodigoEn);
	}

	public void cambiarFechaInicio() {
		registro.getCampos().put("FECHAINIREAL", registro.getCampos().get(GeneralParameterEnum.FECHA_INICIO.getName()));
		fechaFin();
	}

	public void cambiarDias() {
		fechaFin();
	}

	public boolean compararFechas() {
		if (registro.getCampos().get(cFechaInicio) != null && registro.getCampos().get(cFechaFin) != null) {
			Date fechai = (Date) registro.getCampos().get(cFechaInicio);
			Date fechaf = (Date) registro.getCampos().get(cFechaFin);
			if (fechai.equals(fechaf)) {
				return true;
			} else {
				return SysmanFunciones.comparaFechas((Date) registro.getCampos().get(cFechaInicio),
						(Date) registro.getCampos().get(cFechaFin));

			}
		} else {
			return false;
		}
	}

	public void cambiarFechaFin() {
		int dias = 0;
		registro.getCampos().put("FECHAFINREAL", registro.getCampos().get("FECHA_FIN"));
		try {
			if (compararFechas()) {
				String diasa31 = ejbSysmanUtil.consultarParametro(compania,
						"DIFERIR FECHAS CONTANDO MESES DE 31 Y 28 DIAS", moduloNomina, new Date(), true);
				if ("SI".equals(diasa31)) {
					dias = ejbSysmanUtil.calcularDiferenciaEntreFechasInc((Date) registro.getCampos().get(cFechaInicio),
							(Date) registro.getCampos().get(cFechaFin), 3);
				} else {
					dias = Integer.parseInt(
							ejbSysmanUtil.calcularDiferenciaEntreFechas((Date) registro.getCampos().get(cFechaInicio),
									(Date) registro.getCampos().get(cFechaFin), 3, 0));
				}
				registro.getCampos().put("DIAS", dias);
			} else {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
				registro.getCampos().put(cFechaFin, null);
			}

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(idioma.getString(cTxInterrumpida) + ex.getMessage());
		}
	}

	/**
	 * Evalua si los campos "DIAS" Y "FECHA_INICIO" del registro que se esta
	 * trabajando no se encuentran vacios
	 * 
	 * @return verdadero si los campos "DIAS" Y "FECHA_INICIO" no estan nulos
	 */
	private boolean evaluarVacios() {
		return registro.getCampos().get("DIAS") != null && registro.getCampos().get(cFechaInicio) != null;
	}

	private void fechaFin() {
		Date fechaFin;
		int diasBd;
		try {
			if (evaluarVacios()) {
				int diasReg = Integer.parseInt(registro.getCampos().get("DIAS").toString());
				String diasa31 = ejbSysmanUtil.consultarParametro(compania,
						"DIFERIR FECHAS CONTANDO MESES DE 31 Y 28 DIAS", moduloNomina, new Date(), true);

				fechaFin = ejbSysmanUtil.fechaFinalMasDiasComerciales((Date) registro.getCampos().get(cFechaInicio),
						diasReg, "SI".equalsIgnoreCase(diasa31));

				registro.getCampos().put(cFechaFin, fechaFin);
				if (registro.getCampos().get(cFechaIniReal) == null) {
					registro.getCampos().put(cFechaIniReal, registro.getCampos().get(cFechaInicio));
				}
				if (registro.getCampos().get(cFechaFinReal) == null) {
					registro.getCampos().put(cFechaFinReal, registro.getCampos().get(cFechaFin));
				}
				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), idEmpleado);
				Registro total = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														IncapacidadesControladorUrlEnum.URL6321.getValue())
												.getUrl(),
										param));

				if (total.getCampos().get("DIAS") == null) {
					diasBd = 0;
				} else {
					diasBd = Integer.parseInt((total.getCampos().get("DIAS")).toString());
				}
				if (diasBd + diasReg > 180) {
					JsfUtil.agregarMensajeInformativo(
							idioma.getString("TB_TB2549").replace("#$dias#$", String.valueOf(diasBd + diasReg)));
				} else if (diasBd + diasReg > 90) {
					JsfUtil.agregarMensajeInformativo(
							idioma.getString("TB_TB2551").replace("#$dias#$", String.valueOf(diasBd + diasReg)));
				}
			}
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(idioma.getString(cTxInterrumpida) + ex.getMessage());
		}
	}

	public void seleccionarFilaTIPOENFERMEDAD(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cTipoEnfermedad, registroAux.getCampos().get(consCodigoEn));
		nombreTipoEnfermedad = registroAux.getCampos().get("NOMBRE_ENFERMEDAD").toString();
	}

	public void cambiarAno() {
		anio = registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString();
		registro.getCampos().put(GeneralParameterEnum.MES.getName(), null);
		registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
		cargarListaMes();
		cargarListaPeriodo();
	}

	public void cambiarMes() {
		mes = registro.getCampos().get(GeneralParameterEnum.MES.getName()).toString();
		registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
		cargarListaPeriodo();
	}

	@Override
	public void abrirFormulario() {
		tituloForm = SysmanFunciones.concatenar(idioma.getString("TB_TB3721"), " ", nombreEmpleado);
	}

	@Override
	public void cargarRegistro() {
		try {
			cargarListaTIPOENFERMEDAD();
			cargarListaMes();
			cargarListaPeriodo();
			precargarRegistro();
			nombreTipoEnfermedad = null;

			if (registro.getCampos().get(cTipoEnfermedad) != null) {
				Map<String, Object> param = new HashMap<>();
				param.put(consCodigoEn, registro.getCampos().get(cTipoEnfermedad));

				Registro regTipoEnf = listaTIPOENFERMEDAD.getRegistroUnico(param);

				nombreTipoEnfermedad = (String) regTipoEnf.getCampos().get("NOMBRE_ENFERMEDAD");
			}
			if ("i".equals(accion)) {
				anio = (String) SessionUtil.getSessionVar("anioNomina");
				mes = (String) SessionUtil.getSessionVar("mesNomina");
				periodo = (String) SessionUtil.getSessionVar("periodoNomina");
				registro.getCampos().put("ID_DE_PROCESO", procesoNomina);
				registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
				registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
				registro.getCampos().put(GeneralParameterEnum.MES.getName(), mes);
				registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), periodo);
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	@Override
	public boolean insertarAntes() {
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		return true;
	}

	@Override
	public boolean insertarDespues() {
		cargarRegistro();
		return true;
	}

	@Override
	public boolean actualizarAntes() {

		try {
			if (!verificarPeriodo()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2553"));
				return false;
			}
			if (!verificarPeriodoFechaFin()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4281")
						.replace("#$anio#$",
								String.valueOf(SysmanFunciones.ano((Date) registro.getCampos().get(cFechaFin))))
						.replace("#$mes#$",
								String.valueOf(SysmanFunciones.mes((Date) registro.getCampos().get(cFechaFin))))
						.replace("#$periodo#$", registro.getCampos().get(cPeriodo).toString())
						.replace("#$fechaInicial#$",
								SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get(cFechaInicio)))
						.replace("#$fechaFinal#$",
								SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get(cFechaFin))));
				return false;
			}
			if (registro.getCampos().get(cFechaIniReal) == null) {
				registro.getCampos().put(cFechaIniReal, registro.getCampos().get(cFechaInicio));
			}
			if (registro.getCampos().get(cFechaFinReal) == null) {
				registro.getCampos().put(cFechaFinReal, registro.getCampos().get(cFechaFin));
			}
		} catch (NumberFormatException | SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	}

	@Override
	public boolean actualizarDespues() {
		return diferirMas("ADICIONAR");
	}

	private boolean diferirMas(String opcion) {
		boolean bolRta = false;
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(IncapacidadesControladorEnum.PARAM0.getValue(), registro.getCampos().get("INCAPACIDAD"));
			Registro rConcepto = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													IncapacidadesControladorUrlEnum.URL8472.getValue())
											.getUrl(),
									param));
			if (rConcepto.getCampos().get("ID_DE_CONCEPTO") == null) {
				return bolRta;
			}
			bolRta = ejbNominaDos.getDiferirMas(compania, Integer.parseInt(procesoNomina),
					Integer.parseInt(registro.getCampos().get("ANO").toString()),
					Integer.parseInt(registro.getCampos().get("MES").toString()),
					Integer.parseInt(registro.getCampos().get(cPeriodo).toString()), Integer.parseInt(idEmpleado),
					Integer.parseInt(rConcepto.getCampos().get("ID_DE_CONCEPTO").toString()),
					Integer.parseInt(registro.getCampos().get("DIAS").toString()), opcion,
					(Date) registro.getCampos().get(cFechaInicio), SessionUtil.getUser().getCodigo(), null);
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(idioma.getString(cTxInterrumpida) + ex.getMessage());
		}
		return bolRta;
	}

	public boolean verificarPeriodo() throws SystemException {
		return ejbNominaCero.validarPeriodoActivoNomina(compania, Integer.parseInt(procesoNomina),
				Integer.parseInt(registro.getCampos().get("ANO").toString()),
				Integer.parseInt(registro.getCampos().get("MES").toString()),
				Integer.parseInt(registro.getCampos().get(cPeriodo).toString()));
	}

	public boolean verificarPeriodoFechaFin() throws SystemException {
		Date fecha = (Date) registro.getCampos().get(cFechaFin);

		return ejbNominaCero.validarPeriodoActivoNomina(compania, Integer.parseInt(procesoNomina),
				SysmanFunciones.ano(fecha), SysmanFunciones.mes(fecha),
				Integer.parseInt(registro.getCampos().get(cPeriodo).toString()));
	}

	@Override
	public boolean eliminarAntes() {
		boolean bolRta = false;
		cargarRegistro(registro.getLlave(), "e", 0);
		try {
			if (!verificarPeriodo()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3699"));
				return bolRta;
			}
			bolRta = diferirMas("BORRAR");
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return bolRta;
	}

	@Override
	public boolean eliminarDespues() {
		return true;
	}

	public void ejecutarrcCerrar() {
		HashMap<String, Object> parametros = new HashMap<>();

		parametros.put("idEmpleado", idEmpleado);
		parametros.put("cedula", cedula);
		parametros.put("nombreEmpleado", nombreEmpleado);
		parametros.put("vacaciones", vacaciones);

		Direccionador direccionador = new Direccionador();
		direccionador.setParametros(parametros);
		direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.NOVEDADES_CONTROLADOR.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	}

	public List<Registro> getListaIncapacidad() {
		return listaIncapacidad;
	}

	public void setListaIncapacidad(List<Registro> listaIncapacidad) {
		this.listaIncapacidad = listaIncapacidad;
	}

	public List<Registro> getListaAno() {
		return listaAno;
	}

	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	public List<Registro> getListaMes() {
		return listaMes;
	}

	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}

	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}

	public RegistroDataModelImpl getListaTIPOENFERMEDAD() {
		return listaTIPOENFERMEDAD;
	}

	public void setListaTIPOENFERMEDAD(RegistroDataModelImpl listaTIPOENFERMEDAD) {
		this.listaTIPOENFERMEDAD = listaTIPOENFERMEDAD;
	}

	public String getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(String idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getTituloForm() {
		return tituloForm;
	}

	public void setTituloForm(String tituloForm) {
		this.tituloForm = tituloForm;
	}

	public String getNombreTipoEnfermedad() {
		return nombreTipoEnfermedad;
	}

	public void setNombreTipoEnfermedad(String nombreTipoEnfermedad) {
		this.nombreTipoEnfermedad = nombreTipoEnfermedad;
	}
}
