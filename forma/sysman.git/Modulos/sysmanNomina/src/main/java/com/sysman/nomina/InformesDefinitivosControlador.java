/*-
 * InformesDefinitivosControlador.java
 *
 * 1.0
 * 
 * 23/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaOchoRemote;
import com.sysman.nomina.enums.InformesDefinitivosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 23/08/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class InformesDefinitivosControlador extends BeanBaseDatosAcme {
	/*
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;

	/* Variables utilizadas en el controlador */
	private String proceso;
	private String ano;
	private String mes;
	private String periodo;
	private String nivel;
	private Date FECHASOLICITUD;
	private String DOCSOPORTE;
	private String NUMERODOC;
	private String CODEXPEDIDOR;
	private String JUSTIFICACION;
	private String fechaInicial;
	private String fechaFinal;
	private String opcion;
	private String empleado;
	private String condicion;
	private String nit;
	private Workbook workbook;
	private String nombrePeriodo;
	private String intervalo1;
	private String intervalo2;
	private String FechaInicial;
	private String FechaFinal;
	private String fechaInicialCons;
	private String fechaFinalCons;

	/*
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * SelecionarArchivo y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivoSelecionarArchivo;
	private ContenedorArchivo contArchivoSeleccionarPlanilla;

	/*
	 * Variables encargadas de cargar los datos de sesion como lo son el año, el mes
	 * y el periodo
	 */
	private List<Registro> listaAno;
	private List<Registro> listaMes;
	private List<Registro> listaPeriodo;

	StreamedContent archivoDescarga;

	@EJB
	EjbSysmanUtilRemote ejbSysmanUtil;
	@EJB
	private EjbNominaOchoRemote ejbNominaOcho;

	/**
	 * Crea una nueva instancia de InformesDefinitivosControlador
	 */
	public InformesDefinitivosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();

		try {
			numFormulario = GeneralCodigoFormaEnum.INFORMES_DEFINITIVOS_CONTROLADOR.getCodigo();
			contArchivoSelecionarArchivo = new ContenedorArchivo();
			contArchivoSeleccionarPlanilla = new ContenedorArchivo();
			proceso = SessionUtil.getSessionVar("procesoNomina").toString();
			ano = SessionUtil.getSessionVar("anioNomina").toString();
			mes = SessionUtil.getSessionVar("mesNomina").toString();
			periodo = SessionUtil.getSessionVar("periodoNomina").toString();
			nit = SessionUtil.getCompaniaIngreso().getNit();
			nombrePeriodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("nombrePeriodoNomina"), "").toString();
			opcion = "1";
			empleado = "0";
			condicion = " ";
			intervalo1 = ano + SysmanFunciones.padl(mes, 2, "0") + SysmanFunciones.padl(periodo, 2, "0");
			intervalo2 = ano + SysmanFunciones.padl(mes, 2, "0") + SysmanFunciones.padl(periodo, 2, "0");
			fechaInicialCons = "fechaInicial";
			fechaFinalCons = "fechaFinal";
			validarPermisos();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	@Override
	public void iniciarListas() {
	}

	@Override
	public void iniciarListasSub() {
	}

	@Override
	public void iniciarListasSubNulo() {
	}

	/*
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		tabla = "";
		FECHASOLICITUD = new Date();
		DOCSOPORTE = "19";
		JUSTIFICACION = "PAGO NÓMINA";
		NUMERODOC = "NÓMINA";
		CODEXPEDIDOR = "11";
		fechaInicial = "fechaInicial";
		fechaFinal = "fechaFinal";
		cargarListaAno();
		cargarListaMes();
		cargarListaPeriodo();

	}

	@Override
	public void asignarOrigenDatos() {
		origenDatos = "";
	}

	@Override
	public void reasignarOrigenGrilla() {

	}

	/*
	 * Metodo encargado de almacenar una Fecha Inicial desde el proceso, el año, el
	 * mes y el periodo
	 */
	private String fechInicial() {
		String fecIni;
		fecIni = SysmanFunciones.concatenar(proceso.length() == 1 ? "0" + proceso : proceso, ano,
				mes.length() == 1 ? "0" + mes : mes, periodo.length() == 1 ? "0" + periodo : periodo);
		return fecIni;
	}

	/*
	 * Metodo encargado de almacenar una Fecha Final desde el proceso, el año, el
	 * mes y el periodo
	 */
	private String fechFinal() {
		String fecFinal;
		fecFinal = SysmanFunciones.concatenar(proceso.length() == 1 ? "0" + proceso : proceso, ano,
				mes.length() == 1 ? "0" + mes : mes, periodo.length() == 1 ? "0" + periodo : periodo);
		return fecFinal;

	}

	private String fechainicial() {
		String fecIni;
		fecIni = SysmanFunciones.concatenar(proceso.length() == 1 ? "0" + proceso : proceso, ano,
				mes.length() == 1 ? "0" + mes : mes, periodo.length() == 1 ? "0" + periodo : periodo);
		return fecIni;
	}

	private String fechafinal() {
		String fecFinal;
		fecFinal = SysmanFunciones.concatenar(proceso.length() == 1 ? "0" + proceso : proceso, ano,
				mes.length() == 1 ? "0" + mes : mes, periodo.length() == 1 ? "0" + periodo : periodo);
		return fecFinal;

	}
	/* Carga la lista listaAno */

	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InformesDefinitivosControladorUrlEnum.URL0001.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/* Cargar la lista ListaMes */

	public void cargarListaMes() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		try {
			listaMes = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InformesDefinitivosControladorUrlEnum.URL0002.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// Carga la lista listaPeriodo
	public void cargarListaPeriodo() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

		try {
			listaPeriodo = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InformesDefinitivosControladorUrlEnum.URL0003.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/*
	 * Metodo ejecutado al oprimir el boton BT3277 en la vista Para generar 30
	 * Reportes especificos para ANE
	 */

	public void oprimirBT3277() {

		archivoDescarga = null;

		generarInforme();
	}

	/*
	 * Metodo creado para hacer todo el proceso de validacion de consultas tambien
	 * para generar archivos planos, EXCEL y PDF
	 */

	public void generarInforme() {
		StringBuilder inconsistencias = new StringBuilder();
		try {
			validarFechas();
			int fila = 1;

			double totalDeduccion = 0;
			String[] nombresArchivos = new String[31];

			if (contArchivoSelecionarArchivo.getArchivo() == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1901"));
				return;
			}

			ByteArrayInputStream[] salidas = new ByteArrayInputStream[31];

			String fechaIni = fechInicial();
			String fechaFin = fechFinal();
			String inicial = fechainicial();
			String fin = fechafinal();

			// Reemplazos
			HashMap<String, Object> reemplaza = new HashMap<>();

			reemplaza.put("procesoNomina", proceso);
			reemplaza.put("anioNomina", ano);
			reemplaza.put("mesNomina", mes);
			reemplaza.put("periodoNomina", periodo);
			reemplaza.put("proceso", proceso);
			reemplaza.put("idProceso", proceso);
			reemplaza.put("anio", ano);
			reemplaza.put("ano", ano);
			reemplaza.put("mes", mes);
			reemplaza.put("periodo", periodo);
			reemplaza.put("idPeriodo", periodo);
			reemplaza.put("ano1", ano);
			reemplaza.put("mes1", mes);
			reemplaza.put("periodo1", periodo);
			reemplaza.put(fechaInicial, fechaIni);
			reemplaza.put(fechaFinal, fechaFin);
			reemplaza.put("fechaInicial", FechaInicial);
			reemplaza.put("fechaFinal", FechaFinal);
			reemplaza.put("fechaInicial", inicial);
			reemplaza.put("fechaFinal", fin);
			reemplaza.put("opcion", opcion);
			reemplaza.put("intervalo1", SysmanFunciones.padl(proceso, 2, "0") + intervalo1);
			reemplaza.put("intervalo2", SysmanFunciones.padl(proceso, 2, "0") + intervalo2);
			reemplaza.put("empleado", empleado);
			reemplaza.put("condicion", condicion);
			reemplaza.put("nit", nit);
			reemplaza.put("parametroRubroSIIFPensionPublico",
					SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
							"RUBRO SIIF DESCUENTO PENSION PUBLICO", modulo, new Date(), false), ""));
			String encabezado = SysmanFunciones.concatenar("Entre:", "Período ", nombrePeriodo.toUpperCase(), " de ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)], " de ", ano, " y Período ",
					nombrePeriodo.toUpperCase(), " de ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)], " de ", ano);

			String entre = SysmanFunciones.concatenar("Entre: ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)], " de ", ano, periodo,
					periodo.length() == 1 ? "0" + periodo : periodo, " y ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)], " de ", ano, periodo,
					periodo.length() == 1 ? "0" + periodo : periodo);
			String nomPeriodo = nombrePeriodo.toUpperCase();
			String elaboradoPor = ejbSysmanUtil.consultarParametro(compania, "ELABORADO POR", modulo, new Date(),
					false);
			String nombreGerente = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL GERENTE", modulo, new Date(),
					false);

			String cargoGerente = ejbSysmanUtil.consultarParametro(compania, "CARGO DEL GERENTE", modulo, new Date(),
					false);
			String nomCargoTesoreroPaga = ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DEL CARGO TESORERO PAGADOR", modulo, new Date(), false);
			String cargoTesoreroPagador = ejbSysmanUtil.consultarParametro(compania, "CARGO DEL TESORERO PAGADOR",
					modulo, new Date(), false);
			String nomQuienAutorizaNomina = ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE QUIEN AUTORIZA NOMINA", modulo, new Date(), false);
			String cargoQuienAutorizaNomina = ejbSysmanUtil.consultarParametro(compania,
					"CARGO DE QUIEN AUTORIZA NOMINA", modulo, new Date(), false);
			String nombreJefeRH = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE RECURSOS HUMANOS", modulo,
					new Date(), false);

			String cargoJefeRH = ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE RECURSOS HUMANOS", modulo,
					new Date(), false);

			String nombreLiquida = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN AUTORIZA NOMINA", modulo,
					new Date(), false);

			String cargoLiquida = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
					new Date(), false);

			String nombreRevisa = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN REVISA NOMINA", modulo,
					new Date(), false);

			String cargoRevisa = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN REVISA NOMINA", modulo,
					new Date(), false);
			String titulo = SysmanFunciones.concatenar(
					service.buscarEnLista(mes, "MES", GeneralParameterEnum.NOMBRE.getName(), listaMes), " de ", ano);
			// Parametros
			Map<String, Object> parametros = new HashMap<>();

			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_ENCABEZADO", encabezado);
			parametros.put("PR_ELABORADO_POR", elaboradoPor);
			parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
			parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
			parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR", nomCargoTesoreroPaga);
			parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR", cargoTesoreroPagador);
			parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA", nomQuienAutorizaNomina);
			parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", cargoQuienAutorizaNomina);
			parametros.put("PR_MES", ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes)));
			parametros.put("PR_PERIODO", periodo);
			parametros.put("PR_ANO", ano);
			parametros.put("PR_ENTRE",
					SysmanFunciones.concatenar("Entre ",
							SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)], " de ", ano, " Periodo ",
							periodo, " y ", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)], " de ",
							ano, " Periodo ", periodo));
			parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreJefeRH);
			parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRH);
			parametros.put("PR_NOMBREMES",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)].toUpperCase());

			parametros.put("PR_NOMBREPERIODO", nomPeriodo);

			parametros.put("PR_NOMBREDELGERENTE", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL GERENTE", modulo, new Date(), true), " "));
			parametros.put("PR_CARGODELGERENTE", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "CARGO DEL GERENTE", modulo, new Date(), true), " "));
			parametros.put("PR_NOMBREDEQUIENAUTORIZA", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE QUIEN AUTORIZA NOMINA", modulo, new Date(), true), " "));
			parametros.put("PR_CARGODEQUIENAUTORIZA", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CARGO DE QUIEN AUTORIZA NOMINA", modulo, new Date(), true), " "));
			parametros.put("PR_NOMBREDELCARGOTESOREROPAGADOR", SysmanFunciones.nvlStr(ejbSysmanUtil
					.consultarParametro(compania, "NOMBRE DEL CARGO TESORERO PAGADOR", modulo, new Date(), true), " "));

			parametros.put("PR_CARGODELTESOREROPAGADOR", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "CARGO DEL TESORERO PAGADOR", modulo, new Date(), true),
					" "));

			parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA", nombreLiquida);
			parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", cargoLiquida);
			parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA", nombreRevisa);
			parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA", cargoRevisa);
			parametros.put("PR_NOMBRE_GERENTE", obtenerParametro("NOMBRE DEL GERENTE", ""));
			parametros.put("PR_CARGO_GERENTE", obtenerParametro("CARGO DEL GERENTE", ""));
			parametros.put("PR_NOMBRE_CARGO_TESORERO_PAGADOR",
					obtenerParametro("NOMBRE DEL CARGO TESORERO PAGADOR", ""));
			parametros.put("PR_CARGO_TESORERO_PAGADOR", obtenerParametro("CARGO DEL TESORERO PAGADOR", ""));
			parametros.put("PR_NOMBRE_AUTORIZA_NOMINA", obtenerParametro("NOMBRE DE QUIEN AUTORIZA NOMINA", ""));
			parametros.put("PR_CARGO_AUTORIZA_NOMINA", obtenerParametro("CARGO DE QUIEN AUTORIZA NOMINA", ""));

			parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA", obtenerParametro("NOMBRE DE QUIEN LIQUIDA NOMINA", ""));
			parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA", obtenerParametro("CARGO DE QUIEN LIQUIDA NOMINA", ""));
			parametros.put("PR_FORMS_INFORME_COMPENSACIONF_ANO1", ano);
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_FORMS_INFORME_COMPENSACIONF_MES1",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)]);
			parametros.put("PR_TITULO", titulo);
			parametros.put("PR_NOMBRE DEL GERENTE", obtenerParametro("NOMBRE DEL GERENTE", ""));
			parametros.put("PR_MOSTAR", "900.334.265-3".equals(nit) ? false : true);
			parametros.put("PR_MOSTRAR",
					"900.334.265-3".equals(SessionUtil.getCompaniaIngreso().getNit()) ? false : true);

			// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
			
			String[] informe = new String[25];
			informe[0] = "001748ResumenSalariosAneRubroPptal,PDF";
			informe[1] = "001696CONSOLIDADOSDESCUENTOSANE,PDF";
			informe[2] = "001754CONSOLIDADOSDESCUENTOSANEDETALLADOENTIDAD,PDF";
			informe[3] = "001700INFORMETOTALINCAPCIDADES,PDF";
			informe[4] = "000099ListadoRteFte,PDF";
			informe[5] = "001699RESUMENPAGOSDENOMINA,PDF";
			informe[6] = "001762PagosATerceros,PDF";
			informe[7] = "001711ResumenPATRONALESANEDETALLADO,PDF";
			informe[8] = "001708PlanillaautoPensionVolAFC,PDF";
			informe[9] = "001749PRENOMINAANE,PDF";
			informe[10] = "001721PLANILLAMEDICINAPREPAGADA,PDF";
			informe[11] = "001702ListadoParafiscalesMensual,PDF";
			informe[12] = "001701PlanillaresumenAutoRies,PDF";
			informe[13] = "000278ResumenAutoPensiontotal,PDF";
			informe[14] = "000271ResumenDtoSaludTotal,PDF";
			informe[15] = "000090PlanillaAutoPensionVol,PDF";
			informe[16] = "001828PlanillaCesantiasFnaAcum2ANE,PDF";
			informe[17] = "000159InformeNovedades,PDF";
			informe[18] = SysmanFunciones.concatenar(
					obtenerParametro("FORMATO FACTORES PRIMA JUNIO", "001760PlanillaPrimaJunioANE"), ",PDF");
			informe[19] = "001723PLANILLAPRIMADICANE,PDF";
			informe[20] = "001696CONSOLIDADOSDESCUENTOSANE,EXCEL";
			informe[21] = "001754CONSOLIDADOSDESCUENTOSANEDETALLADOENTIDAD,EXCEL";
			informe[22] = "001699RESUMENPAGOSDENOMINA,EXCEL";
			informe[23] = "001762PagosATerceros,EXCEL";
			informe[24] = "001828PlanillaCesantiasFnaAcum2ANE,EXCEL";
			for (int i = 0; i < informe.length; i++) {
				if (i == 19) {
					if (i == 19 && !(periodo.equals("4") && mes.equals("12"))) {
						continue;
					}
				}
				if (i == 18) {
					if (i == 18 && !(periodo.equals("4") && mes.equals("7"))) {
						continue;
					}
				}
				if (i == 3) {

					String fechaInicials = ano + (mes.length() == 1 ? "0" + mes : mes)
							+ (periodo.length() == 1 ? "0" + periodo : periodo);
					String fechaFinals = ano + (mes.length() == 1 ? "0" + mes : mes)
							+ (periodo.length() == 1 ? "0" + periodo : periodo);
					reemplaza.put(fechaInicialCons, fechaInicials);
					reemplaza.put(fechaFinalCons, fechaFinals);
				}
				if (i == 7) {
					String FechaIni1711 = SysmanFunciones.concatenar(proceso.length() == 1 ? "0" + proceso : proceso,
							ano, mes.length() == 1 ? "0" + mes : mes, periodo.length() == 1 ? "0" + periodo : periodo);
					String FechaFin1711 = SysmanFunciones.concatenar(proceso.length() == 1 ? "0" + proceso : proceso,
							ano, mes.length() == 1 ? "0" + mes : mes, periodo.length() == 1 ? "0" + periodo : periodo);
					reemplaza.put("fechaInicial", FechaIni1711);
					reemplaza.put("fechaFinal", FechaFin1711);
				}

				String[] data = informe[i].split(",");

				FORMATOS formato = FORMATOS.PDF;

				switch (data[1]) {
				case "EXCEL":
					formato = FORMATOS.EXCEL97;
					break;
				case "TXT":
					formato = FORMATOS.TXT;
					break;
				default:
					break;
				}

				String sqlt = Reporteador.resuelveConsulta(data[0], Integer.valueOf(SessionUtil.getModulo()),
						reemplaza);

				try {

					parametros.put("PR_STRSQL", sqlt);

					salidas[i] = JsfUtil.serializarReporte(data[0], parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

				} catch (SysmanException e) {

					inconsistencias.append("Nombre:").append(informe[i]).append(" --> No Existen Datos").append("\r\n");

				}
			}

			//
			try (FileInputStream fileIn = new FileInputStream(contArchivoSelecionarArchivo.getArchivo());) {
				Map<String, Object> param = new TreeMap<>();

				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

				param.put("PROCESO", proceso);

				param.put(GeneralParameterEnum.ANO.getName(), ano);

				param.put(GeneralParameterEnum.MES.getName(), mes);

				param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

				List<Registro> listaDeducciones = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												InformesDefinitivosControladorUrlEnum.URL0004.getValue())
										.getUrl(),
								param));

				workbook = new HSSFWorkbook(fileIn);
				Sheet sheet = workbook.getSheet("DEDUCCIONES1");

				StringBuilder builder = new StringBuilder();

				for (Registro valor : listaDeducciones) {

					double deduccion = Double
							.parseDouble(SysmanFunciones.nvl(valor.getCampos().get("TOTAL"), "0").toString());

					totalDeduccion = totalDeduccion + deduccion;

					String strCuenta = SysmanFunciones.nvl(valor.getCampos().get("RUBROS_SIIF"), "").toString();

					if (!SysmanFunciones.validarVariableVacio(strCuenta)) {
						strCuenta = crearCuenta(strCuenta);
					}
					Row rowDatos = sheet.getRow(fila);

					Cell cellConsecutivo = rowDatos.getCell(0);
					cellConsecutivo.setCellValue(fila);

					Cell cellCuenta = rowDatos.getCell(1);
					cellCuenta.setCellValue(strCuenta);

					Cell cellTipoDocumento = rowDatos.getCell(2);
					cellTipoDocumento.setCellValue(1);

					Cell cellNit = rowDatos.getCell(3);
					cellNit.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("NIT"), "").toString());

					Cell cellTotal = rowDatos.getCell(8);
					cellTotal.setCellValue(deduccion);

					Cell cellConversion = rowDatos.getCell(9);
					cellConversion.setCellValue(fila + "|" + strCuenta + "|1|"
							+ SysmanFunciones.nvl(valor.getCampos().get("NIT"), "").toString() + "|||||" + deduccion);

					builder.append(fila + "|" + strCuenta + "|1|"
							+ SysmanFunciones.nvl(valor.getCampos().get("NIT"), "").toString() + "|||||" + deduccion);
					builder.append("\r\n");
					fila++;

				}

				Row rowTotal = sheet.getRow(fila + 2);
				Cell cellTotal = rowTotal.getCell(8);
				cellTotal.setCellValue(totalDeduccion);

				ByteArrayOutputStream fileOut = new ByteArrayOutputStream();

				workbook.write(fileOut);
				fileOut.close();
				fileIn.close();

				salidas[26] = new ByteArrayInputStream(fileOut.toByteArray());
				salidas[27] = JsfUtil.serializarPlano(builder.toString());

			} catch (IOException | SystemException | JRException e) {
				String xls = "4. DEDUCCIONES_OBLIG_PPTAL_SIIF_" + ano + "_" + mes + "_" + periodo + ".xls";
				String txt = "4. DEDUCCIONES_" + ano + "_" + mes + "_" + periodo + ".txt";
				inconsistencias.append("Nombre: ").append(xls).append(" --> No Existen Datos").append("\r\n");
				inconsistencias.append("Nombre: ").append(txt).append(" --> No Existen Datos").append("\r\n");
			}

			if (validarArchivo()) {
				ByteArrayOutputStream salida = new ByteArrayOutputStream();
				String datos = ejbNominaOcho.generarInformacionSiifPatronal(compania, Integer.parseInt(ano),
						Integer.parseInt(mes), Integer.parseInt(periodo), Integer.parseInt(ano), Integer.parseInt(mes),
						Integer.parseInt(periodo), FECHASOLICITUD, DOCSOPORTE, NUMERODOC, CODEXPEDIDOR, JUSTIFICACION,
						"1".equals(nivel) ? "Nivel Administrativo" : "2".equals(nivel) ? "Nivel Operativo" : "",
						SessionUtil.getUser().getCodigo());

				String rutaArchivo = contArchivoSeleccionarPlanilla.getArchivo().getPath();
				FileInputStream file = null;
				file = new FileInputStream(new File(rutaArchivo));

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length());

				if (".xlsx".equals(extension)) {
					workbook = new XSSFWorkbook(file);
				} else {
					workbook = new HSSFWorkbook(file);
				}

				Sheet sheet = workbook.getSheet("DatosGenerales");

				String[] hojas = datos.split(",.HOJ.,");
				if (hojas.length > 2) {
					String[] datosGenerales = hojas[0].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet, 0);
					sheet = workbook.getSheet("Conceptos");
					datosGenerales = hojas[1].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet, 0);
					sheet = workbook.getSheet("Deducciones");
					datosGenerales = hojas[2].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet, 1);

					workbook.write(salida);
					salida.close();
					workbook.close();

					salidas[28] = new ByteArrayInputStream(salida.toByteArray());
				} else {
					String Patronal = "14.2 Cargas Masivas NóminaSIIF_" + ano + "_" + mes + "_" + periodo
							+ "_Aportes Patronales.xls";
					inconsistencias.append("Nombre: ").append(Patronal).append(" --> No Existen Datos").append("\r\n");

				}
			}
			if (validarArchivo()) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				String datos = ejbNominaOcho.generarInformacionSiif(compania, Integer.parseInt(ano),
						Integer.parseInt(mes), Integer.parseInt(periodo), Integer.parseInt(ano), Integer.parseInt(mes),
						Integer.parseInt(periodo), FECHASOLICITUD, DOCSOPORTE, NUMERODOC, CODEXPEDIDOR, JUSTIFICACION,
						"1".equals(nivel) ? "'Nivel Administrativo'" : "2".equals(nivel) ? "'Nivel Operativo'" : null,
						(false ? -1 : 0));

				String rutaArchivo = contArchivoSeleccionarPlanilla.getArchivo().getPath();
				FileInputStream file = null;
				file = new FileInputStream(new File(rutaArchivo));

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length());

				if (".xlsx".equals(extension)) {
					workbook = new XSSFWorkbook(file);
				} else {
					workbook = new HSSFWorkbook(file);
				}

				Sheet sheet = workbook.getSheet("DatosGenerales");

				String[] hojas = datos.split(",.HOJ.,");
				if (hojas.length > 2) {
					String[] datosGenerales = hojas[0].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet, 0);
					sheet = workbook.getSheet("Conceptos");
					datosGenerales = hojas[1].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet, 0);
					sheet = workbook.getSheet("Deducciones");
					datosGenerales = hojas[2].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet, 1);

					workbook.write(out);
					out.close();
					workbook.close();
					salidas[29] = new ByteArrayInputStream(out.toByteArray());
				} else {
					String SBefeneficios = "14. Cargas Masivas Nómina SIIF_" + ano + "_" + mes + "_" + periodo
							+ "_Pagos a Empleados Sin Beneficios.xls";

					inconsistencias.append("Nombre: ").append(SBefeneficios).append(" --> No Existen Datos")
							.append("\r\n");
				}
			}
			if (validarArchivo()) {
				ByteArrayOutputStream outPut = new ByteArrayOutputStream();
				String datos2 = ejbNominaOcho.generarInformacionSiif(compania, Integer.parseInt(ano),
						Integer.parseInt(mes), Integer.parseInt(periodo), Integer.parseInt(ano), Integer.parseInt(mes),
						Integer.parseInt(periodo), FECHASOLICITUD, DOCSOPORTE, NUMERODOC, CODEXPEDIDOR, JUSTIFICACION,
						"1".equals(nivel) ? "'Nivel Administrativo'" : "2".equals(nivel) ? "'Nivel Operativo'" : null,
						(true ? -1 : 0));

				String rutaArchivo2 = contArchivoSeleccionarPlanilla.getArchivo().getPath();
				FileInputStream file2 = null;
				file2 = new FileInputStream(new File(rutaArchivo2));

				String extension2 = rutaArchivo2.substring(rutaArchivo2.indexOf('.'), rutaArchivo2.length());

				if (".xlsx".equals(extension2)) {
					workbook = new XSSFWorkbook(file2);
				} else {
					workbook = new HSSFWorkbook(file2);
				}

				Sheet sheet2 = workbook.getSheet("DatosGenerales");

				String[] hojas2 = datos2.split(",.HOJ.,");
				if (hojas2.length > 2) {
					String[] datosGenerales = hojas2[0].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet2, 0);
					sheet2 = workbook.getSheet("Conceptos");
					datosGenerales = hojas2[1].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet2, 0);
					sheet2 = workbook.getSheet("Deducciones");
					datosGenerales = hojas2[2].split(SysmanConstantes.SEPARADOR_REG);
					asignarValor(datosGenerales, sheet2, 1);

					workbook.write(outPut);
					outPut.close();
					workbook.close();
					salidas[30] = new ByteArrayInputStream(outPut.toByteArray());
				} else {

					String Beneficios = "14.1 Cargas Masivas Nómina SIIF_" + ano + "_" + mes + "_" + periodo
							+ "_Pagos a Empleados Beneficios.xls";

					inconsistencias.append("Nombre: ").append(Beneficios).append(" --> No Existen Datos")
							.append("\r\n");
				}
			}

			salidas[25] = JsfUtil.serializarPlano(inconsistencias.toString());

			nombresArchivos[0] = "1. Registro de Compromisos Presupuestal (Devengos).pdf";
			nombresArchivos[1] = "2. Consolidado de Descuentos de Nomina.pdf";
			nombresArchivos[2] = "3. Descuentos por Tercero.pdf";
			nombresArchivos[3] = "6. Informe de Incapacidades.pdf";
			nombresArchivos[4] = "7. Informe de retencion en la Fuente.pdf";
			nombresArchivos[5] = "8. Informe Nomina por Empleados.pdf";
			nombresArchivos[6] = "9. Pago por Terceros.pdf";
			nombresArchivos[7] = "10. Registro Presupuestal Patrono por Tercero.pdf";
			nombresArchivos[8] = "15. Informe Cuentas AFC.pdf";
			nombresArchivos[9] = "16. Informe de Prenomina.pdf";
			nombresArchivos[10] = "17. Informe Medicina Prepagada.pdf";
			nombresArchivos[11] = "18. Informe Pago Parafiscales verificado con Planilla de Seg.Social.pdf";
			nombresArchivos[12] = "19. Informe Pago Riesgos verificado con Planilla de Seg.Social.pdf";
			nombresArchivos[13] = "20. Informe Pagos Pension verificado con Planilla de Seg.Social.pdf";
			nombresArchivos[14] = "21. Informe Pagos Salud verificado con Planilla de Seg.Social.pdf";
			nombresArchivos[15] = "22. Informe Pension Voluntaria.pdf";
			nombresArchivos[16] = "23. Planilla Cesantias FNA.pdf";
			nombresArchivos[17] = "24. Consulta de Novedades.pdf";
			nombresArchivos[18] = "25. Planilla Prima de Servicios.pdf";
			nombresArchivos[19] = "26. planilla Prima de Navidad.pdf";
			nombresArchivos[20] = "2. Consolidado de Descuentos de Nomina.xls";
			nombresArchivos[21] = "3. Descuentos por Tercero.xls";
			nombresArchivos[22] = "8. Informe Nomina por Empleados.xls";
			nombresArchivos[23] = "9. Pago por Terceros.xls";
			nombresArchivos[24] = "23. Planilla Cesantias FNA.xls";
			nombresArchivos[25] = "Inconsistencias.txt";
			nombresArchivos[26] = "4. DEDUCCIONES_OBLIG_PPTAL_SIIF_" + ano + "_" + mes + "_" + periodo + ".xls";
			nombresArchivos[27] = "4. DEDUCCIONES_" + ano + "_" + mes + "_" + periodo + ".txt";
			nombresArchivos[28] = "14.2 Cargas Masivas Nómina SIIF_" + ano + "_" + mes + "_" + periodo
					+ "_Aportes Patronales.xls";
			nombresArchivos[29] = "14. Cargas Masivas Nómina SIIF_" + ano + "_" + mes + "_" + periodo
					+ "_Pagos a Empleados Sin Beneficios.xls";
			nombresArchivos[30] = "14.1 Cargas Masivas Nómina SIIF_" + ano + "_" + mes + "_" + periodo
					+ "_Pagos a Empleados Beneficios.xls";

			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

		} catch (IOException | SystemException | JRException | SQLException | NumberFormatException | DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public boolean validarArchivo() {

		String archivo = String.valueOf(contArchivoSeleccionarPlanilla.getArchivo());
		if (contArchivoSeleccionarPlanilla.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			String extension = archivo.substring(archivo.indexOf('.'), archivo.length()).toLowerCase();
			if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
				return true;
			} else {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
				return false;
			}
		}
	}

	private String obtenerParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = ejbSysmanUtil.consultarParametro(compania, nombreParametro, SessionUtil.getModulo(), new Date(),
					true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	private String crearCuenta(String strCuenta) {
		int tamano = strCuenta.length();

		StringBuilder cadenaTerminada = new StringBuilder();

		cadenaTerminada.append(strCuenta.substring(0, 1));

		if (tamano >= 3) {
			cadenaTerminada.append("-" + strCuenta.substring(1, 3));
		}
		if (tamano >= 5) {
			cadenaTerminada.append("-" + strCuenta.substring(3, 5));
		}
		if (tamano >= 7) {
			cadenaTerminada.append("-" + strCuenta.substring(5, 7));
		}
		if (tamano >= 9) {
			cadenaTerminada.append("-" + strCuenta.substring(7, 9));
		}
		if (tamano >= 11) {
			cadenaTerminada.append("-" + strCuenta.substring(9, 11));
		}
		if (tamano >= 13) {
			cadenaTerminada.append("-" + strCuenta.substring(11, 13));
		}

		return cadenaTerminada.toString();
	}

	public void asignarValor(String[] datosGenerales, Sheet sheet1, int tipo) {
		String[] colum;
		Row row;
		for (int i = 0; i < datosGenerales.length; i++) {
			colum = datosGenerales[i].split(SysmanConstantes.SEPARADOR_COL);
			row = sheet1.createRow(i + 1);
			int k = 0;
			for (int j = 0; j < colum.length; j++) {
				Cell nCell = row.createCell(k);
				formatea(nCell, j, colum);
				if (tipo == 1 && j == 1) {
					k = 3;
				}
				k++;
			}
		}
	}

	private void formatea(Cell cell, int columna, String[] colum) {
		switch (cell.getSheet().getSheetName()) {
		case "DatosGenerales":
			if (columna == 9 || columna == 10 || columna == 11) {
				formatoNumero(cell);
				cell.setCellValue(Double.parseDouble(colum[columna]));
			} else {
				cell.setCellValue(colum[columna]);
			}
			break;
		case "Conceptos":
			if (columna == 3) {
				formatoNumero(cell);
				cell.setCellValue(Double.parseDouble(colum[columna]));
			} else {
				cell.setCellValue(colum[columna]);
			}
			break;
		case "Deducciones":
			if (columna == 2) {
				formatoNumero(cell);
				cell.setCellValue(Double.parseDouble(colum[columna]));
			} else {
				cell.setCellValue(colum[columna]);
			}
			break;
		default:
			break;
		}
	}

	private void formatoNumero(Cell cell) {
		Workbook wb = cell.getSheet().getWorkbook();
		CellStyle style = wb.createCellStyle();
		DataFormat format = wb.createDataFormat();
		style.setDataFormat(format.getFormat("#,##0.00"));
		style.setLocked(false);
		cell.setCellStyle(style);
	}

	private void validarFechas() {
		FechaInicial = ano + (mes.length() == 1 ? "0" + mes : mes) + (periodo.length() == 1 ? "0" + periodo : periodo);
		FechaFinal = ano + (mes.length() == 1 ? "0" + mes : mes) + (periodo.length() == 1 ? "0" + periodo : periodo);

	}

	@Override
	public void abrirFormulario() {
	}

	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean insertarAntes() {

		return true;
	}

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano
	 *            Variable a asignar en ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes
	 *            Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * Retorna la variable periodo
	 * 
	 * @return periodo
	 */
	public String getPeriodo() {
		return periodo;
	}

	/**
	 * Asigna la variable periodo
	 * 
	 * @param periodo
	 *            Variable a asignar en periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	/**
	 * Retorna la variable nivel
	 * 
	 * @return nivel
	 */
	public String getNivel() {
		return nivel;
	}

	/**
	 * Asigna la variable nivel
	 * 
	 * @param nivel
	 *            Variable a asignar en nivel
	 */
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	/**
	 * Retorna la variable FECHASOLICITUD
	 * 
	 * @return FECHASOLICITUD
	 */
	public Date getFECHASOLICITUD() {
		return FECHASOLICITUD;
	}

	/**
	 * Asigna la variable FECHASOLICITUD
	 * 
	 * @param FECHASOLICITUD
	 *            Variable a asignar en FECHASOLICITUD
	 */
	public void setFECHASOLICITUD(Date FECHASOLICITUD) {
		this.FECHASOLICITUD = FECHASOLICITUD;
	}

	/**
	 * Retorna la variable DOCSOPORTE
	 * 
	 * @return DOCSOPORTE
	 */
	public String getDOCSOPORTE() {
		return DOCSOPORTE;
	}

	/**
	 * Asigna la variable DOCSOPORTE
	 * 
	 * @param DOCSOPORTE
	 *            Variable a asignar en DOCSOPORTE
	 */
	public void setDOCSOPORTE(String DOCSOPORTE) {
		this.DOCSOPORTE = DOCSOPORTE;
	}

	/**
	 * Retorna la variable NUMERODOC
	 * 
	 * @return NUMERODOC
	 */
	public String getNUMERODOC() {
		return NUMERODOC;
	}

	/**
	 * Asigna la variable NUMERODOC
	 * 
	 * @param NUMERODOC
	 *            Variable a asignar en NUMERODOC
	 */
	public void setNUMERODOC(String NUMERODOC) {
		this.NUMERODOC = NUMERODOC;
	}

	/**
	 * Retorna la variable CODEXPEDIDOR
	 * 
	 * @return CODEXPEDIDOR
	 */
	public String getCODEXPEDIDOR() {
		return CODEXPEDIDOR;
	}

	/**
	 * Asigna la variable CODEXPEDIDOR
	 * 
	 * @param CODEXPEDIDOR
	 *            Variable a asignar en CODEXPEDIDOR
	 */
	public void setCODEXPEDIDOR(String CODEXPEDIDOR) {
		this.CODEXPEDIDOR = CODEXPEDIDOR;
	}

	/**
	 * Retorna la variable JUSTIFICACION
	 * 
	 * @return JUSTIFICACION
	 */
	public String getJUSTIFICACION() {
		return JUSTIFICACION;
	}

	/**
	 * Asigna la variable JUSTIFICACION
	 * 
	 * @param JUSTIFICACION
	 *            Variable a asignar en JUSTIFICACION
	 */
	public void setJUSTIFICACION(String JUSTIFICACION) {
		this.JUSTIFICACION = JUSTIFICACION;
	}

	/**
	 * Retorna el objeto contArchivoSelecionarArchivo
	 * 
	 * @return contArchivoSelecionarArchivo
	 */
	public ContenedorArchivo getContArchivoSelecionarArchivo() {
		return contArchivoSelecionarArchivo;
	}

	/**
	 * Asigna el objeto contArchivoSelecionarArchivo
	 * 
	 * @param contArchivoSelecionarArchivo
	 *            Variable a asignar en contArchivoSelecionarArchivo
	 */
	public void setContArchivoSelecionarArchivo(ContenedorArchivo contArchivoSelecionarArchivo) {
		this.contArchivoSelecionarArchivo = contArchivoSelecionarArchivo;
	}

	/**
	 * Retorna el objeto contArchivoSeleccionarPlanilla
	 * 
	 * @return contArchivoSeleccionarPlanilla
	 */
	public ContenedorArchivo getContArchivoSeleccionarPlanilla() {
		return contArchivoSeleccionarPlanilla;
	}

	/**
	 * Asigna el objeto contArchivoSeleccionarPlanilla
	 * 
	 * @param contArchivoSeleccionarPlanilla
	 *            Variable a asignar en contArchivoSeleccionarPlanilla
	 */
	public void setContArchivoSeleccionarPlanilla(ContenedorArchivo contArchivoSeleccionarPlanilla) {
		this.contArchivoSeleccionarPlanilla = contArchivoSeleccionarPlanilla;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno
	 *            Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}

	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes
	 *            Variable a asignar en listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	/**
	 * Retorna la lista listaPeriodo
	 * 
	 * @return listaPeriodo
	 */
	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}

	/**
	 * Asigna la lista listaPeriodo
	 * 
	 * @param listaPeriodo
	 *            Variable a asignar en listaPeriodo
	 */
	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}

	/**
	 * @return the proceso
	 */
	public String getProceso() {
		return proceso;
	}

	/**
	 * @param proceso
	 *            the proceso to set
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	/**
	 * @return the fechaInicial
	 */
	public String getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * @param fechaInicial
	 *            the fechaInicial to set
	 */
	public void setFechaInicial(String fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * @return the fechaFinal
	 */
	public String getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * @param fechaFinal
	 *            the fechaFinal to set
	 */
	public void setFechaFinal(String fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @param archivoDescarga
	 *            the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * @return the opcion
	 */
	public String getOpcion() {
		return opcion;
	}

	/**
	 * @param opcion
	 *            the opcion to set
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	/**
	 * @return the empleado
	 */
	public String getEmpleado() {
		return empleado;
	}

	/**
	 * @param empleado
	 *            the empleado to set
	 */
	public void setEmpleado(String empleado) {
		this.empleado = empleado;
	}

	/**
	 * @return the condicion
	 */
	public String getCondicion() {
		return condicion;
	}

	/**
	 * @param condicion
	 *            the condicion to set
	 */
	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	/**
	 * @return the nit
	 */
	public String getNit() {
		return nit;
	}

	/**
	 * @param nit
	 *            the nit to set
	 */
	public void setNit(String nit) {
		this.nit = nit;
	}

}
