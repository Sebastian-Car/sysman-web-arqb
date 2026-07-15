package com.sysman.nomina;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.enums.LicenciasControladorEnum;
import com.sysman.nomina.enums.LicenciasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @author jgomez
 * @version 1, 03/11/2015
 * @modified spina 23/03/2017 Se realizo depuracion de sonar - se eliminaron
 *           cast innecesarios, se creo un metodo para mostrar los mensajes de
 *           alerta y se organiza el codigo
 *
 * @version 2, 10/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los
 *         combos, en el origen de grilla y de datos.
 *
 * @version 3, 06/03/2017
 * @author spina Se actualiza formulario segun la version
 *         NOMINAP2017.11.03_UNIFICADAS MPV 2112017_MPV - 413 GOBCAQUETA
 *
 */

@ManagedBean
@ViewScoped
public class LicenciasControlador extends BeanBaseDatosAcmeImpl {

	/**
	 * compania con la que se esta trabajando
	 */
	private final String compania;
	/**
	 * proceso seleccionado en el formulario principal nomina
	 */
	private String procesoNomina;
	/**
	 * valor del codigo del modulo nomina
	 */
	private String moduloNomina;
	/**
	 * lista para el combo grande con las licencias
	 */
	private RegistroDataModelImpl listaLicencia;
	/**
	 * listado de anios
	 */
	private List<Registro> listaAno;
	/**
	 * listado de meses
	 */
	private List<Registro> listaMes;
	/**
	 * listado de periodos
	 */
	private List<Registro> listaPeriodo;
	/**
	 * codigo empleado seleccionado en el formulario de novedades
	 */
	private String idEmpleado;
	/**
	 * nombre empleado seleccionado en el formulario de novedades
	 */
	private String nombreEmpleado;
	/**
	 * cedula del empleado seleccionado en el formulario de novedades
	 */
	private String cedula;
	/**
	 * anio seleccionado al ingresar al modulo
	 */
	private String anoNomina;
	/**
	 * mes seleccionado al ingresar al modulo
	 */
	private String mesNomina;
	/**
	 * periodo seleccionado al ingresar al modulo
	 */
	private String periodoNomina;
	/**
	 * titulo del formulario que muestra informacion del empleado a modificar
	 */
	private String tituloForm;
	/**
	 * permite revisar si el tipo de licencia es habil o no
	 */
	private boolean lichabiles;
	/**
	 * en true permite visualizar el dialogo para contar sabados
	 */
	private boolean dialogoContarSabadosVisible;
	/**
	 * contiene el valor de la seleccion realizara por el usuario en el dialogo para
	 * contar sabados
	 */
	private boolean contarSabados;

	private boolean vacaciones;
	private boolean cesantias;

	private boolean camposBloqueados;	

	private Date fechaFinalOrg;
	/**
	 * contiene el map con los parametros del formulario novedades
	 */
	private Map<String, Object> parametrosEntrada;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbNominaCeroRemote ejbNominaCero;

	@EJB
	private EjbNominaDosRemote ejbNominaDos;
	private boolean visibleLicRemunerada;

	public LicenciasControlador() {
		super();
		compania = SessionUtil.getCompania();
		parametrosEntrada = SessionUtil.getFlash();
		camposBloqueados = false;
		try {
			if (parametrosEntrada != null) {
				idEmpleado = SysmanFunciones.nvl(parametrosEntrada.get("idEmpleado"), "").toString();
				nombreEmpleado = SysmanFunciones.nvl(parametrosEntrada.get("nombreEmpleado"), "").toString();
				cedula = SysmanFunciones.nvl(parametrosEntrada.get("cedula"), "").toString();
				anoNomina = SysmanFunciones.nvl(parametrosEntrada.get("ano"), "").toString();
				mesNomina = SysmanFunciones.nvl(parametrosEntrada.get("mes"), "").toString();
				periodoNomina = SysmanFunciones.nvl(parametrosEntrada.get("periodo"), "").toString();
				vacaciones = (boolean) SysmanFunciones.nvl(parametrosEntrada.get("vacaciones"), false);
				cesantias = (boolean) SysmanFunciones.nvl(parametrosEntrada.get("cesantias"), false);
			}
			procesoNomina = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
			moduloNomina = SessionUtil.getModulo();
			numFormulario = GeneralCodigoFormaEnum.LICENCIAS_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (SysmanException ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@Override
	public void iniciarListas() {
		cargarListaLicencia();
		cargarListaAno();
	}

	@Override
	public void iniciarListasSub() {
		// Metodo heredado
	}

	@Override
	public void iniciarListasSubNulo() {
		// Metodo heredado
	}

	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.LICENCIAS;
		buscarLlave();
		asignarOrigenDatos();
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), idEmpleado);
	}

	public void cargarListaLicencia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LicenciasControladorUrlEnum.URL3847.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaLicencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				LicenciasControladorEnum.LICENCIA.getValue());
	}

	public void cargarListaAno() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaAno = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											LicenciasControladorUrlEnum.URL4234.getValue())
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
			param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

			listaMes = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											LicenciasControladorUrlEnum.URL4698.getValue())
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
			param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
			param.put(GeneralParameterEnum.MES.getName(), registro.getCampos().get(GeneralParameterEnum.MES.getName()));

			listaPeriodo = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											LicenciasControladorUrlEnum.URL5784.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cambiarAno() {
		cargarListaMes();
		cargarListaPeriodo();
	}

	public void cambiarMes() {
		cargarListaPeriodo();
	}

	public void seleccionarFilaLicencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(LicenciasControladorEnum.LICENCIA.getValue(),
				registroAux.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()));
		registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
		lichabiles = (boolean) registroAux.getCampos().get(LicenciasControladorEnum.HABILES.getValue());
		
		String licencia = SysmanFunciones.toString(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()));
		if(licencia.equals("10")){
				visibleLicRemunerada = true;
		}else {
			    visibleLicRemunerada = false;
		}
	}

	public void cambiarFechaInicio() {
		try {
			if ("04".equals(SysmanFunciones
					.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString())) {
				registro.getCampos().put(LicenciasControladorEnum.DIAS.getValue(), 5);
				registro.getCampos().put(LicenciasControladorEnum.FECHA_FINAL.getValue(),
						ejbSysmanUtil.retornarFechaMasDiasHabiles(compania,
								(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()),
								Integer.parseInt(SysmanFunciones
										.nvl(registro.getCampos().get(LicenciasControladorEnum.DIAS.getValue()), "0")
										.toString()),
								false));
				int dias = Integer.parseInt(ejbSysmanUtil.calcularDiferenciaEntreFechas(
						(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()),
						(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()), 3, 0));
				
				//JM CC 3444
				if (("04".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
						|| "05".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
						|| "10".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
						|| "12".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString())) &&
						"SI".equals(ejbSysmanUtil.consultarParametro(compania,"LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES", moduloNomina, new Date(), true).toString())){
							
					dias = Integer.parseInt(SysmanFunciones.nvl(String.valueOf(ejbSysmanUtil.retornarDiasHabilesEntreFechas(compania,
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()), 
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()), false)), "0"));
				 }
				
				registro.getCampos().put(LicenciasControladorEnum.DIAS.getValue(), dias);
			}

			if (registro.getCampos().get(LicenciasControladorEnum.DIAS.getValue()) != null
					&& registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()) != null) {
				cargarFechaFinalComerciales(false);
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cambiarFechaFinal() {
		registro.getCampos().put(LicenciasControladorEnum.DIAS_INICIALES.getValue(),
				registro.getCampos().get(LicenciasControladorEnum.DIAS.getValue()));
		int dias = 0;
		try {
			if (registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()) != null
					&& registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()) != null
					&& SysmanFunciones.comparaFechas(
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()),
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()))) {
				String diasa31 = ejbSysmanUtil.consultarParametro(compania,
						LicenciasControladorEnum.DIFERIR_FECHAS_CONTANDO_MESES_DE_31_Y_28_DIAS.getValue(), moduloNomina,
						new Date(), true);
				if ("SI".equals(diasa31)) {
					dias = Integer.parseInt(SysmanFunciones.nvl(ejbSysmanUtil.calcularDiferenciaEntreFechas(
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()), (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()), 3,
							0), "0").toString());
					/*dias = SysmanFunciones.calcularDiferenciaDias(
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()),
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()));*/
					
					//JM CC 3444
					if (("04".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
							|| "05".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
							|| "10".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
							|| "12".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString())) &&
							"SI".equals(ejbSysmanUtil.consultarParametro(compania,"LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES", moduloNomina, new Date(), true).toString())){
								
								dias = Integer.parseInt(SysmanFunciones.nvl(String.valueOf(ejbSysmanUtil.retornarDiasHabilesEntreFechas(compania,
								(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()), 
								(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()), false)), "0"));
					 }
					
				} else {
					dias = Integer.parseInt(ejbSysmanUtil.calcularDiferenciaEntreFechas(
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()),
							(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()), 3, 0));
					
					//JM CC 3444
					if (("04".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
							|| "05".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
							|| "10".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString()) 
							|| "12".equals(SysmanFunciones.nvl(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()), "").toString())) &&
							"SI".equals(ejbSysmanUtil.consultarParametro(compania,"LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES", moduloNomina, new Date(), true).toString())){
								
						dias = Integer.parseInt(SysmanFunciones.nvl(String.valueOf(ejbSysmanUtil.retornarDiasHabilesEntreFechas(compania,
								(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()), 
								(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()), false)), "0"));
					 }

				}
				registro.getCampos().put(LicenciasControladorEnum.DIAS.getValue(), dias);
			} else {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
				registro.getCampos().put(LicenciasControladorEnum.DIAS.getValue(), null);
			}
		} catch (/*ParseException |*/ SystemException ex) {
			mostrarAlertaLog(ex);
		}
	}

	private void mostrarAlertaLog(Exception ex) {
		logger.error(ex.getMessage(), ex);
		JsfUtil.agregarMensajeError(
				SysmanFunciones.concatenar(idioma.getString("MSM_TRANS_INTERRUMPIDA"), ex.getMessage()));
	}

	public void cambiarDias() {
		fechaFin();
	}

	private void fechaFin() {
		if (registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()) == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4008"));
			registro.getCampos().put(LicenciasControladorEnum.DIAS.getValue(), 0);
			return;
		}
		registro.getCampos().put(LicenciasControladorEnum.DIAS_INICIALES.getValue(),
				registro.getCampos().get(LicenciasControladorEnum.DIAS.getValue()));
		// preguntar si desea contarle los sabados como habiles - en
		// la variable contarSabados
		dialogoContarSabadosVisible = true;
	}

	public void aceptarcontarsabados() {
		contarSabados = true;
		actualizarDiasFechaHabiles();
		contarSabados = false;
	}

	public void cancelarcontarsabados() {
		contarSabados = false;
		actualizarDiasFechaHabiles();
	}

	public void actualizarDiasFechaHabiles() {
		try {
			registro.getCampos().put(LicenciasControladorEnum.OBSERVACION_L.getValue(), idioma.getString("TB_TB4009"));
			String diasa31 = ejbSysmanUtil.consultarParametro(compania,
					LicenciasControladorEnum.DIFERIR_FECHAS_CONTANDO_MESES_DE_31_Y_28_DIAS.getValue(), moduloNomina,
					new Date(), true);
			if ("SI".equalsIgnoreCase(diasa31)) {

				if ("04".equals(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()))
						&& Integer.parseInt(SysmanFunciones
								.nvl(registro.getCampos().get(LicenciasControladorEnum.DIAS.getValue()), "0")
								.toString()) <= 7) // 06032015
					// luto
				{
					registro.getCampos().put(LicenciasControladorEnum.DIAS.getValue(), 5);
				}
				cargaDiasObservacion();
			} else {
				if (lichabiles) // 20062016web
				{
					cargaDiasObservacion();
				} else {
					cargarFechaFinalComerciales(true);
				}
			}

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		dialogoContarSabadosVisible = false;
	}

	private void cargaDiasObservacion() {
		try {
			Date fechaFinal = ejbSysmanUtil.retornarFechaMasDiasHabiles(compania,
					(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()),
					Integer.parseInt(SysmanFunciones
							.nvl(registro.getCampos().get(LicenciasControladorEnum.DIAS.getValue()), "0").toString()),
					contarSabados);

			String diasCalculados = SysmanFunciones.nvl(ejbSysmanUtil.calcularDiferenciaEntreFechas(
					(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()), fechaFinal, 3,
					0), "0").toString();
			registro.getCampos().put(LicenciasControladorEnum.DIAS.getValue(), diasCalculados);
			registro.getCampos().put(LicenciasControladorEnum.OBSERVACION_L.getValue(),
					idioma.getString("TB_TB4010").replace("s$diasIniciales$s", SysmanFunciones
							.nvl(registro.getCampos().get(LicenciasControladorEnum.DIAS_INICIALES.getValue()), "0")
							.toString()).replace("s$booleano$s", contarSabados ? "SI" : "NO")
					.replace("s$diasCalculados$s", diasCalculados));
			registro.getCampos().put(LicenciasControladorEnum.FECHA_FINAL.getValue(), fechaFinal);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void cargarFechaFinalComerciales(boolean usarParam) {
		try {
			String diasa31 = ejbSysmanUtil.consultarParametro(compania,
					LicenciasControladorEnum.DIFERIR_FECHAS_CONTANDO_MESES_DE_31_Y_28_DIAS.getValue(), moduloNomina,
					new Date(), true);

			registro.getCampos()
			.put(LicenciasControladorEnum.FECHA_FINAL.getValue(),
					ejbSysmanUtil
					.fechaFinalMasDiasComerciales(
							(Date) registro.getCampos()
							.get(LicenciasControladorEnum.FECHA_INICIO.getValue()),
							Integer.parseInt(SysmanFunciones
									.nvl(registro.getCampos()
											.get(LicenciasControladorEnum.DIAS.getValue()), "0")
									.toString()),
							usarParam ? "SI".equalsIgnoreCase(diasa31) : false));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	@Override
	public void abrirFormulario() {
		tituloForm = SysmanFunciones.concatenar(idioma.getString("TB_TB3720"), " ", nombreEmpleado);
		if (procesoNomina.equals("1")) {
			permisos[2] = true;
		}
		else {
			permisos[2] = false;
		}


	}

	@Override
	public void cargarRegistro() {
		precargarRegistro();
		if ("i".equals(accion)) {
			camposBloqueados = false;
			registro.getCampos().put(GeneralParameterEnum.ID_DE_PROCESO.getName(), procesoNomina);
			registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), idEmpleado);
			registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anoNomina);
			registro.getCampos().put(GeneralParameterEnum.MES.getName(), mesNomina);
			registro.getCampos().put(LicenciasControladorEnum.PERIODO.getValue(), periodoNomina);
			registro.getCampos().put(LicenciasControladorEnum.FECHA_ACTO.getValue(), new Date());
			registro.getCampos().put(LicenciasControladorEnum.OBSERVACION_L.getValue(), idioma.getString("TB_TB4009"));			
		}
		cargarListaMes();
		cargarListaPeriodo();

		if (ACCION_MODIFICAR.equals(accion)) {
			camposBloqueados = true;           
			fechaFinalOrg = (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue());
		}
		
		String licencia = SysmanFunciones.toString(registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()));
		if(licencia.equals("10")){
				visibleLicRemunerada = true;
		}else {
			    visibleLicRemunerada = false;
		}
	}

	@Override
	public boolean insertarAntes() {
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		return true;
	}

	@Override
	public boolean insertarDespues() {
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		try {
			if (!verificarPeriodo()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2631"));
				return false;
			}
			if (!verificarFechaFin()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4424"));
				return false;
			}
			if (!verificarPeriodoFechaFin()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4281")
						.replace("#$anio#$", String.valueOf(SysmanFunciones
								.ano((Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()))))
						.replace("#$mes#$", String.valueOf(SysmanFunciones
								.mes((Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()))))
						.replace("#$periodo#$",
								registro.getCampos().get(LicenciasControladorEnum.PERIODO.getValue()).toString())
						.replace("#$fechaInicial#$", SysmanFunciones.convertirAFechaCadena(
								(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue())))
						.replace("#$fechaFinal#$", SysmanFunciones.convertirAFechaCadena(
								(Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()))));
				return false;
			}
			if (accion.equals(ACCION_MODIFICAR)) {
				registro.getCampos().remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
				registro.getCampos().remove(LicenciasControladorEnum.NUMEROPERIODO.getValue());
				registro.getCampos().remove(GeneralParameterEnum.ID_DE_PROCESO.getName());
			}
			registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
		} catch (NumberFormatException | SystemException | ParseException ex) {
			mostrarAlertaLog(ex);
		}
		return true;
	}

	public boolean verificarPeriodo() throws SystemException {

		if (accion.equals(ACCION_MODIFICAR)) {
			return ejbNominaCero.validarPeriodoActivoNomina(compania, Integer.parseInt(procesoNomina),
					Integer.parseInt(anoNomina),
					Integer.parseInt(mesNomina),
					Integer.parseInt(registro.getCampos().get(LicenciasControladorEnum.PERIODO.getValue()).toString()));
		}
		else {
			return ejbNominaCero.validarPeriodoActivoNomina(compania, Integer.parseInt(procesoNomina),
					Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString()),
					Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.MES.getName()).toString()),
					Integer.parseInt(registro.getCampos().get(LicenciasControladorEnum.PERIODO.getValue()).toString()));
		}

	}

	public boolean verificarPeriodoFechaFin() throws SystemException {
		Date fecha = (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue());

		return ejbNominaCero.validarPeriodoActivoNomina(compania, Integer.parseInt(procesoNomina),
				SysmanFunciones.ano(fecha), SysmanFunciones.mes(fecha),
				Integer.parseInt(registro.getCampos().get(LicenciasControladorEnum.PERIODO.getValue()).toString()));
	}

	public boolean verificarFechaFin() throws SystemException {

		if (accion.equals(ACCION_MODIFICAR)) {
			//Obtenemos la nueva fecha seleccionada por el usuario
			Date fechaNueva = (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue());

			// Convertir la nueva fcehal a LocalDate
			LocalDate localFechaNueva = fechaNueva.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			// Convertir la fecha anterior a LocalDate
			String fechaAnterior = String.format("%s-%02d-%02d", anoNomina, Integer.parseInt(mesNomina), Integer.parseInt("1"));

			LocalDate localFechaAnterior = LocalDate.parse(fechaAnterior);	        

			// Obtener el mes inmediatamente anterior de la fecha anterior
			LocalDate mesAnterior = localFechaAnterior.minusMonths(1);
			// Obtener el ultimo dia del mes inmediatamente anterior
			LocalDate ultimoDiaMesAnterior = mesAnterior.withDayOfMonth(mesAnterior.lengthOfMonth());

			// Comparar las fechas
			if (localFechaNueva.isAfter(ultimoDiaMesAnterior) || localFechaNueva.equals(ultimoDiaMesAnterior) ) {
				return true;
			} else {
				return false;
			}
		}
		else {
			return true;
		}

	}

	@Override
	public boolean actualizarDespues() {

		boolean bolRta = true;
		
		validarFechasPersonal();

		if  ("m".equals(accion)) {

			bolRta = diferirLic(LicenciasControladorEnum.BORRAR.getValue());
		}		

		if (bolRta) {

			return diferirLic(LicenciasControladorEnum.ADICIONAR.getValue());
		}
		else {
			return bolRta;
		}

	}

	private boolean diferirLic(String opcion) {
		boolean bolRta = false;
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(LicenciasControladorEnum.LICENCIA.getValue(),
					registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()));

			Registro rConcepto = RegistroConverter.toRegistro(requestManager.get(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(LicenciasControladorUrlEnum.URL8112.getValue()).getUrl(),
					param));

			if (rConcepto.getCampos().get(GeneralParameterEnum.ID_DE_CONCEPTO.getName()) == null) {
				return bolRta;
			}
			bolRta = ejbNominaDos.getDiferirMas(compania, Integer.parseInt(procesoNomina),
					Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString()),
					Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.MES.getName()).toString()),
					Integer.parseInt(registro.getCampos().get(LicenciasControladorEnum.PERIODO.getValue()).toString()),
					Integer.parseInt(idEmpleado),
					Integer.parseInt(
							rConcepto.getCampos().get(GeneralParameterEnum.ID_DE_CONCEPTO.getName()).toString()),
					Integer.parseInt(registro.getCampos().get(LicenciasControladorEnum.DIAS.getValue()).toString()),
					opcion, (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()),
					SessionUtil.getUser().getCodigo(),
					fechaFinalOrg);
			if (bolRta) {
				fechaFinalOrg = (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue());
			}
		} catch (SystemException ex) {
			mostrarAlertaLog(ex);
		}

		return bolRta;
	}

	@Override
	public boolean eliminarAntes() {
		boolean bolRta = false;
		cargarRegistro(registro.getLlave(), "e", 0);
		try {
			if (!verificarPeriodo()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2631"));
				return bolRta;
			}
			bolRta = diferirLic(LicenciasControladorEnum.BORRAR.getValue());
			return bolRta;
		} catch (SystemException ex) {
			mostrarAlertaLog(ex);
		}
		return bolRta;
	}

	@Override
	public boolean eliminarDespues() {
		
		actualizarPersonal(1);
		
		return true;
	}

	public void ejecutarrcCerrar() {
		HashMap<String, Object> parametros = new HashMap<>();

		parametros.put("idEmpleado", idEmpleado);
		parametros.put("cedula", cedula);
		parametros.put("nombreEmpleado", nombreEmpleado);
		parametros.put("vacaciones", vacaciones);
		parametros.put("cesantias", cesantias);

		Direccionador direccionador = new Direccionador();
		direccionador.setParametros(parametros);
		direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.NOVEDADES_CONTROLADOR.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, moduloNomina);
	}
	
	public void validarFechasPersonal() {
		
	        String mes = "";
                String codigoLicencia = "";
		int periodoNominaActual = 0;
		int periodoInicioLicencia = 0;
		int periodoFinLicencia = 0;
		Date fechaInicioLicencia = null;
		Date fechaFinLicencia = null;
		
		if (mesNomina.length() == 1) {
			mes = "0" + mesNomina;
	    }
		else {
			mes = mesNomina;
		}
		
		periodoNominaActual = Integer.parseInt(anoNomina.toString() + mes);		        
		fechaInicioLicencia = (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue());
		fechaFinLicencia = (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue());
		
		SimpleDateFormat formato = new SimpleDateFormat("yyyyMM");
		
		periodoInicioLicencia = Integer.parseInt(formato.format(fechaInicioLicencia));
		periodoFinLicencia = Integer.parseInt(formato.format(fechaFinLicencia));
		
		codigoLicencia = registro.getCampos().get(LicenciasControladorEnum.LICENCIA.getValue()).toString();
		
		if (codigoLicencia.equals("11")) {
		
        		if (periodoInicioLicencia <= periodoNominaActual && periodoFinLicencia >= periodoNominaActual) {
        			actualizarPersonal(6);
        		}
        		
        		else {
        			actualizarPersonal(1);
        		}
		}
	}
	
	public void actualizarPersonal(int estadoActual) {
		
		try {
						
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put(LicenciasControladorEnum.TOTAL_DIAS_COMISION.getValue(), estadoActual == 6 ? registro.getCampos().get(LicenciasControladorEnum.DIAS.getValue()) : null);
			parametros.put(LicenciasControladorEnum.FECHA_INICIO_COMISION.getValue(), estadoActual == 6 ? (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_INICIO.getValue()) : null );
			parametros.put(LicenciasControladorEnum.FECHA_FINAL_COMISION.getValue(), estadoActual == 6 ? (Date) registro.getCampos().get(LicenciasControladorEnum.FECHA_FINAL.getValue()) : null);
			parametros.put(LicenciasControladorEnum.ESTADO_ACTUAL.getValue(), estadoActual);
			
			
			parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			
			parametros.put("KEY_COMPANIA", compania);
			parametros.put("KEY_ID_DE_EMPLEADO", idEmpleado);
			Parameter parameter = new Parameter();

			parameter.setFields(parametros);
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(LicenciasControladorUrlEnum.URL3848.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException ex) {
			Logger.getLogger(LicenciasControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public RegistroDataModelImpl getListaLicencia() {
		return listaLicencia;
	}

	public void setListaLicencia(RegistroDataModelImpl listaLicencia) {
		this.listaLicencia = listaLicencia;
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

	public boolean isDialogoContarSabadosVisible() {
		return dialogoContarSabadosVisible;
	}

	public void setDialogoContarSabadosVisible(boolean dialogoContarSabadosVisible) {
		this.dialogoContarSabadosVisible = dialogoContarSabadosVisible;
	}

	public boolean isContarSabados() {
		return contarSabados;
	}

	public void setContarSabados(boolean contarSabados) {
		this.contarSabados = contarSabados;
	}

	/**
	 * Retorna la variable camposBloqueados
	 * 
	 * @return camposBloqueados
	 */
	public boolean isCamposBloqueados() {
		return camposBloqueados;
	}

	/**
	 * Asigna la variable camposBloqueados
	 * 
	 * @param camposBloqueados
	 * Variable a asignar en camposBloqueados
	 */
	public void setCamposBloqueados(boolean camposBloqueados) {
		this.camposBloqueados = camposBloqueados;
	}

	/**
	 * Retorna la variable camposBloqueados
	 * 
	 * @return camposBloqueados
	 */
	public Date getFechaFinalOrg() {
		return fechaFinalOrg;
	}

	/**
	 * Asigna la variable camposBloqueados
	 * 
	 * @param camposBloqueados
	 * Variable a asignar en camposBloqueados
	 */
	public void setFechaFinalOrg(Date fechaFinalOrg) {
		this.fechaFinalOrg = fechaFinalOrg;
	}

	/**
	 * @return the visibleLicRemunerada
	 */
	public boolean isVisibleLicRemunerada() {
		return visibleLicRemunerada;
	}

	/**
	 * @param visibleLicRemunerada the visibleLicRemunerada to set
	 */
	public void setVisibleLicRemunerada(boolean visibleLicRemunerada) {
		this.visibleLicRemunerada = visibleLicRemunerada;
	}

	
}
