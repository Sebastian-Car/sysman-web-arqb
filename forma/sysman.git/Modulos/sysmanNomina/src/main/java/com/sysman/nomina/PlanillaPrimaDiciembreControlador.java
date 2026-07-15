package com.sysman.nomina;

import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.PlanillaPrimaDiciembreControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 09/09/2015
 *
 * @author spina
 * @version 2, 19/10/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class PlanillaPrimaDiciembreControlador extends BeanBaseModal {

	private String compania;
	private String anio;
	private String mes;
	private String periodo;
	private String proceso;
	private String observacion;
	private List<Registro> listaAno1;
	private List<Registro> listaMes1;
	private List<Registro> listaPeriodo1;
	private StreamedContent archivoDescarga;
	private String planillaSTR;
	private String planillaGOBCAQUETA;
	private String planillaANE;
	private String planillaAlcTocancipa;
	private String planillaBucaramanga;
	private String planillaAlcAcacias;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Creates a new instance of PlanillaPrimaDiciembreControlador
	 */
	public PlanillaPrimaDiciembreControlador() {
		super();
		try {
			compania = SessionUtil.getCompania();
			numFormulario = GeneralCodigoFormaEnum.PLANILLA_PRIMA_DICIEMBRE_CONTROLADOR.getCodigo();
			planillaSTR = "001805PlanillaPrimaDICSTR";
			planillaGOBCAQUETA = "001834PLANILLAPRIMADICGOBCAQUETA";
			planillaANE = "001723PLANILLAPRIMADICANE";
			planillaAlcTocancipa = "001967PlanillaPrimaDICSTRALCTOCANCIPA";
			planillaBucaramanga = "002007PlanillaPrimaNavidadBucaramanga";
			planillaAlcAcacias = "900020PLANILLAPRIMADICSTRALCACACIAS";
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(PlanillaPrimaDiciembreControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		cargarListaAno1();
		cargarListaMes1();
		cargarListaPeriodo1();
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		// Metodo heredado
	}

	public void cargarListaAno1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											PlanillaPrimaDiciembreControladorUrlEnum.URL28520.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaMes1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		try {
			listaMes1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											PlanillaPrimaDiciembreControladorUrlEnum.URL28521.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaPeriodo1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		try {
			listaPeriodo1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											PlanillaPrimaDiciembreControladorUrlEnum.URL28522.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void oprimirPresentar() {
		archivoDescarga = null;
		generarInforme(ReportesBean.FORMATOS.PDF);
	}

	public void oprimirExcel() {
		archivoDescarga = null;
		generarInforme(ReportesBean.FORMATOS.EXCEL);
	}

	private void generarInforme(ReportesBean.FORMATOS formato) {
		try {

			String reporte = ejbSysmanUtil.consultarParametro(compania, "FORMATO PLANILLA PRIMA DICIEMBRE",
					SessionUtil.getModulo(), new Date(), false);
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazos = new HashMap<>();
			if ((anio == null) || (mes == null) || (periodo == null)) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB2523"));
				return;
			} else {
				cargarParametros(parametros);
				reemplazos.put(compania, compania);
				reemplazos.put("anio", anio);
				reemplazos.put("mes", mes);
				reemplazos.put("proceso", proceso);
				reemplazos.put("periodo", periodo);
				if (planillaSTR.equalsIgnoreCase(reporte)) {
					Reporteador.resuelveConsulta("000120PlanillaPrimaDICSTR", Integer.parseInt(SessionUtil.getModulo()),
							reemplazos, parametros);

					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				} else if (planillaGOBCAQUETA.equalsIgnoreCase(reporte) && mes.equals("12") && periodo.equals("4")) {
					Reporteador.resuelveConsulta(planillaGOBCAQUETA, Integer.parseInt(SessionUtil.getModulo()),
							reemplazos, parametros);

					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				} else if (planillaANE.equalsIgnoreCase(reporte)) {
					Reporteador.resuelveConsulta(planillaANE, Integer.parseInt(SessionUtil.getModulo()), reemplazos,
							parametros);

					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				} else if (planillaAlcTocancipa.equals(reporte)) {
					Reporteador.resuelveConsulta(planillaAlcTocancipa, Integer.parseInt(SessionUtil.getModulo()),
							reemplazos, parametros);

					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				} else if (planillaBucaramanga.equals(reporte)) {
					Reporteador.resuelveConsulta(planillaBucaramanga, Integer.parseInt(SessionUtil.getModulo()),
							reemplazos, parametros);

					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				} else if (planillaAlcAcacias.equals(reporte)) {
					Reporteador.resuelveConsulta(planillaAlcAcacias, Integer.parseInt(SessionUtil.getModulo()),
							reemplazos, parametros);

					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				}

				else if (reporte.equals(reporte)) {
					Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazos,
							parametros);

					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				}
			}
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB4255"));

		} catch (SystemException | SysmanException | JRException | IOException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	public void cargarParametros(Map<String, Object> parametros) {

		try {
			// MANEJO DE PARAMETROS DE LOS REPORTES
			String moduloNomina = SessionUtil.getModulo();
			String nombreLiquida = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN LIQUIDA NOMINA",
					moduloNomina, new Date(), true);

			String nombreAutoriza = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN AUTORIZA NOMINA",
					moduloNomina, new Date(), true);

			String nombreRevisa = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN REVISA NOMINA",
					moduloNomina, new Date(), true);

			String cargoLiquida = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN LIQUIDA NOMINA",
					moduloNomina, new Date(), true);

			String nombreTesorero = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL CARGO TESORERO PAGADOR",
					moduloNomina, new Date(), true);

			String cargoTesorero = ejbSysmanUtil.consultarParametro(compania, "CARGO DEL TESORERO PAGADOR",
					moduloNomina, new Date(), true);

			String cargoAutoriza = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN AUTORIZA NOMINA",
					moduloNomina, new Date(), true);

			String cargoRevisa = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN REVISA NOMINA",
					moduloNomina, new Date(), true);

			String cargoGerente = ejbSysmanUtil.consultarParametro(compania, "CARGO DEL GERENTE", moduloNomina,
					new Date(), true);

			String nombreGerente = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL GERENTE", moduloNomina,
					new Date(), true);

			String cargoGerenteAdm = ejbSysmanUtil.consultarParametro(compania, "CARGO DEL GERENTE ADMINISTRATIVO",
					moduloNomina, new Date(), true);

			String nombreGerenteAdm = ejbSysmanUtil.consultarParametro(compania, "NOMBRE GERENTE ADMINISTRATIVO",
					moduloNomina, new Date(), true);

			String cargoAnalista = ejbSysmanUtil.consultarParametro(compania, "CARGO ANALISTA DE NOMINAS", moduloNomina,
					new Date(), true);

			String nombreAnalista = ejbSysmanUtil.consultarParametro(compania, "NOMBRE ANALISTA DE NOMINAS",
					moduloNomina, new Date(), true);

			// inicio dcastiblanco
			String nombreJeFeRecursos = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE RECURSOS HUMANOS",
					moduloNomina, new Date(), true);

			String cargoJefeRecursos = ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE RECURSOS HUMANOS",
					moduloNomina, new Date(), true);
			String elaborado = ejbSysmanUtil.consultarParametro(compania, "ELABORADO POR", moduloNomina, new Date(),
					true);
			String nombrePresupuesto = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE JEFE DE PRESUPUESTO",
					moduloNomina, new Date(), true);

			String cargoPresupuesto = ejbSysmanUtil.consultarParametro(compania, "CARGO DEL JEFE DE PRESUPUESTO",
					moduloNomina, new Date(), true);

			String calcularPrima = ejbSysmanUtil.consultarParametro(compania,
					"CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS", moduloNomina, new Date(), false);

			// fin dcastiblanco

			String proporcional = ejbSysmanUtil.consultarParametro(compania,
					"CALCULAR PRIMA DE NAVIDAD PROPORCIONAL DECRETO 853/2012", moduloNomina, new Date(), true);

			String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
					"CARGO JEFE DESARROLLO HUMANO", moduloNomina, new Date(), true);
			String jefeNomina = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE NOMINA", moduloNomina,
					new Date(), true);
			String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania, "CARGO RESPONSABLE DE NOMINA",
					moduloNomina, new Date(), true);
			String jefeDH = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE DESARROLLO HUMANO", moduloNomina,
					new Date(), true);

			String observaciones = observacion;
			String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
			parametros.put("PR_CALCULAR_PRIMA_DE_NAVIDAD_PROPORCIONAL_POR_DIAS", calcularPrima);
			parametros.put("PR_NOMBREEMPRESA", nombreCompania);
			parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA", nombreLiquida);
			parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA", nombreAutoriza);
			parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA", nombreRevisa);
			parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA", cargoLiquida);

			parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR", nombreTesorero);
			parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR", cargoTesorero);

			parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA", cargoRevisa);
			parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", cargoAutoriza);
			parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
			parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
			parametros.put("PR_CARGO_GERENTE_ADMINISTRATIVO", cargoGerenteAdm);
			parametros.put("PR_NOMBRE_GERENTE_ADMINISTRATIVO", nombreGerenteAdm);
			parametros.put("PR_FORMS_PLANILLA_PRIMA_DIC_OBSERVACION", observaciones);
			parametros.put("PR_FORMS_PLANILLA_PRIMA_DIC_OBSERVACION", observaciones);
			parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreJeFeRecursos);
			parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRecursos);
			parametros.put("PR_ELABORADO_POR", elaborado);
			parametros.put("PR_CALCULAR_PRIMA_DE_NAVIDAD_PROPORCIONAL_DECRETO_853/2012", proporcional);

			parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO", nombrePresupuesto);
			parametros.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO", cargoPresupuesto);

			parametros.put("PR_NOMBRE_ANALISTA_DE_NOMINAS", nombreAnalista);
			parametros.put("PR_CARGO_ANALISTA_DE_NOMINAS", cargoAnalista);

			parametros.put("PR_FORMS_FACTORES_LIQUIDACION_PRIMA_NAVIDAD_OBS", observaciones);
			parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
			parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDH);
			parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
			parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
			parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeDH);
			
			// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
		} catch (NumberFormatException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	public void cambiarAno1() {
		periodo = null;
		mes = null;
		cargarListaMes1();
	}

	public void cambiarMes1() {
		periodo = null;
		cargarListaPeriodo1();
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public List<Registro> getListaAno1() {
		return listaAno1;
	}

	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}

	public List<Registro> getListaMes1() {
		return listaMes1;
	}

	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
	}

	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}

	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

}