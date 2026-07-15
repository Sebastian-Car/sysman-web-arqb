/*-
 * FrmResumenTotalDistControlador.java
 *
 * 1.0
 * 
 * 14/10/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmDistribucionFinalControladorUrlEnum;
import com.sysman.nomina.enums.NominaresumentotalControladorUrlEnum;
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
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 14/10/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmResumenTotalDistControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String ano1;
	private String ano2;
	private String mes1;
	private String mes2;
	private String periodo1;
	private String periodo2;
	private String proceso;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnoDesde;
	private List<Registro> listaAnoHasta;
	private List<Registro> listaMesDesde;
	private List<Registro> listaMesHasta;
	private List<Registro> listaPeriodoDesde;
	private List<Registro> listaPeriodoHasta;
	private List<Registro> listaProceso;
	private String periodoInicial;
	private String periodoFinal;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private PrintStream inconsistencias;
	/**
	 * Crea una nueva instancia de FrmResumenTotalDistControlador
	 */
	public FrmResumenTotalDistControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2487;
			validarPermisos();
			//<INI_ADICIONAL>
			proceso = (String) SessionUtil.getSessionVar("procesoNomina");
			ano1 = (String) SessionUtil.getSessionVar("anioNomina");
			ano2 = (String) SessionUtil.getSessionVar("anioNomina");
			mes1 = (String) SessionUtil.getSessionVar("mesNomina");
			mes2 = (String) SessionUtil.getSessionVar("mesNomina");
			periodo1 = (String) SessionUtil.getSessionVar("periodoNomina");
			periodo2 = (String) SessionUtil.getSessionVar("periodoNomina");
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
		cargarListaAnoDesde();
		cargarListaAnoHasta();
		cargarListaMesDesde();
		cargarListaMesHasta();
		cargarListaPeriodoDesde();
		cargarListaPeriodoHasta();
		cargarListaProceso();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
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
	 * Carga la lista listaAnoDesde
	 *
	 */
	public void cargarListaAnoDesde(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaAnoDesde = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									NominaresumentotalControladorUrlEnum.URL6322
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaAnoHasta
	 *
	 */
	public void cargarListaAnoHasta(){
		listaAnoHasta = listaAnoDesde;
	}
	/**
	 * 
	 * Carga la lista listaMesDesde
	 *
	 */
	public void cargarListaMesDesde(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano1);

			listaMesDesde = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									NominaresumentotalControladorUrlEnum.URL7253
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaMesHasta
	 *
	 */
	public void cargarListaMesHasta(){

		listaMesHasta = listaMesDesde;
	}
	/**
	 * 
	 * Carga la lista listaPeriodoDesde
	 *
	 */
	public void cargarListaPeriodoDesde(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
			param.put(GeneralParameterEnum.ANO.getName(), ano1);
			param.put(GeneralParameterEnum.MES.getName(), mes1);

			listaPeriodoDesde = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									NominaresumentotalControladorUrlEnum.URL5682
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaPeriodoHasta
	 *
	 */
	public void cargarListaPeriodoHasta(){
		listaPeriodoHasta = listaPeriodoDesde;
	}
	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 */
	public void cargarListaProceso(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaProceso = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									NominaresumentotalControladorUrlEnum.URL10155
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar
	 * en la vista
	 *
	 *
	 */
	public void oprimirPresentar() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;       
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generarInforme(FORMATOS.EXCEL);            
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton revisarsinconfigurare
	 * en la vista
	 *
	 *
	 */
	public void oprimirrevisarsinconfigurare() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		generaReporte("800655RevisarConceptSinConfSUI");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton personsinconfigurar
	 * en la vista
	 *
	 *
	 */
	public void oprimirpersonsinconfigurar() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generaReporte("800654RevisarPersSinConfSUI");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton consultardatossui
	 * en la vista
	 *
	 *
	 */
	public void oprimirconsultardatossui() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generaReporte("800656ConsultarDatosSUI");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton portipoynivel
	 * en la vista
	 *
	 *
	 */
	public void oprimirportipoynivel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generaReporte("800657ConsultaSUITipoNominayNivel");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton totalempleados
	 * en la vista
	 *
	 *
	 */
	public void oprimirtotalempleados() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generaReporte("800659TotalEmpleadosTipoyNivel");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton porformanombramiento
	 * en la vista
	 *
	 *
	 */
	public void oprimirporformanombramiento() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		generaReporte("800658ConsultaSUIFormaNombramiento");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton totalformanombramiento
	 * en la vista
	 *
	 *
	 */
	public void oprimirtotalformanombramiento() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generaReporte("800660TotalEmpleadosFormaNombramiento");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton actualizapersonal
	 * en la vista
	 *
	 *
	 */
	public void oprimiractualizapersonal() {
		//<CODIGO_DESARROLLADO>
		try {
			archivoDescarga=null;     
			Map<String, Object> param = new HashMap<>();
			param.put("COMPANIA", compania);
			param.put("ANO1", ano1);
			param.put("MES1", mes1);
			param.put("PERIODO1", periodo1);
			param.put("ANO2", ano2);
			param.put("MES2", mes2);
			param.put("PERIODO2", periodo2);

			String urlEnumId = FrmDistribucionFinalControladorUrlEnum.URL210166.getValue();
			UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			Parameter parameter = new Parameter();
			parameter.setFields(param);

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>

	public void generarInforme(FORMATOS formato) {
		try {
			if (!validaciones()) {
				return;
			}
			if (!validacionPeriodo()) {
				return;
			}

			Map<String, Object> parametros = new HashMap<>();
			Map<String, Object> reemplazos = new HashMap<>();

			reemplazos.put("anio1", ano1);
			reemplazos.put("mes1", mes1);
			reemplazos.put("periodo1", periodo1);
			reemplazos.put("anio2", ano2);
			reemplazos.put("mes2", mes2);
			reemplazos.put("periodo2", periodo2);
			reemplazos.put("proceso", proceso);

			String entre = SysmanFunciones.concatenar("Entre: ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
					                                           .parseInt(mes1)],
					" de ", ano1, " Periodo ", periodo1, " y ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
					                                           .parseInt(mes2)],
					" de ", ano2, " Periodo ", periodo2);
			String nombreGerente = consultarParametro("NOMBRE DEL GERENTE",
					false);

			String cargoGerente = consultarParametro("CARGO DEL GERENTE",
					false);
			String nomCargoTesoreroPaga = consultarParametro(
					"NOMBRE DEL CARGO TESORERO PAGADOR", false);
			String cargoTesoreroPagador = consultarParametro(
					"CARGO DEL TESORERO PAGADOR", false);
			String nombreJefeRecursosHumanos = consultarParametro(
                    "NOMBRE DEL JEFE DE RECURSOS HUMANOS", false);
			String cargoJefeNomina = consultarParametro(
                    "CARGO DEL JEFE DE NOMINA", false);

			parametros.put("PR_ENTRE", entre);
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
			parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
			parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR", cargoTesoreroPagador);
			parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR", nomCargoTesoreroPaga);
			parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS", nombreJefeRecursosHumanos);
			parametros.put("PR_CARGO_DEL_JEFE_DE_NOMINA", cargoJefeNomina);
			String reporte;
			reporte = SysmanFunciones.nvlStr(SysmanFunciones.toString(ejbSysmanUtil.consultarParametro(compania, "FORMATO RESUMEN TOTAL NOMINA", modulo, new Date(), false)), "000560ResumenTotal");
			String parametro = SysmanFunciones.nvlStr(SysmanFunciones.toString(ejbSysmanUtil.consultarParametro(compania, "DISCRIMINAR RESUMENES NOMINA", modulo, new Date(), false)), "NO");

			if (reporte.equals("000560ResumenTotal") && parametro.equals("SI")) {
				ByteArrayInputStream[] salidas = new ByteArrayInputStream[7];
				for (int i = 0; i <= 6; i++) {

					switch (i) {
					case 0:
						reemplazos.put("depencia", "AND PERSONAL.DEPENDENCIA = '05'");
						reemplazos.put("grupoContable", "");
						parametros.put("PR_TITULO",  "Resumen Total de Nómina (Servicio Publico)");
						break;
					case 1:
						reemplazos.put("depencia", "AND PERSONAL.DEPENDENCIA = '05'");
						reemplazos.put("grupoContable", "AND PERSONAL.GRUPOCONTABLE = 'A'");
						parametros.put("PR_TITULO",  "Resumen Total de Nómina (Servicio Publico - Administrativo)");
						break;
					case 2:
						reemplazos.put("depencia", "AND PERSONAL.DEPENDENCIA = '05'");
						reemplazos.put("grupoContable", "AND PERSONAL.GRUPOCONTABLE = 'P'");
						parametros.put("PR_TITULO",  "Resumen Total de Nómina (Servicio Publico - Operativo)");
						break;
					case 3:
						reemplazos.put("depencia", "AND PERSONAL.DEPENDENCIA <> '05'");
						reemplazos.put("grupoContable", "");
						parametros.put("PR_TITULO",  "Resumen Total de Nómina (Central)");
						break;
					case 4:
						reemplazos.put("depencia", "AND PERSONAL.DEPENDENCIA <> '05'");
						reemplazos.put("grupoContable", "AND PERSONAL.GRUPOCONTABLE = 'A'");
						parametros.put("PR_TITULO",  "Resumen Total de Nómina (Central - Aministrativo)");
						break;
					case 5:
						reemplazos.put("depencia", "AND PERSONAL.DEPENDENCIA <> '05'");
						reemplazos.put("grupoContable", "AND PERSONAL.GRUPOCONTABLE = 'P'");
						parametros.put("PR_TITULO",  "Resumen Total de Nómina (Central - Operativo)");
						break;

					}
					try {
						Reporteador.resuelveConsulta(i == 6 ? reporte : "002646ResumenTotalDistribucion", Integer.valueOf(SessionUtil.getModulo()), reemplazos,
								parametros);



						salidas[i] = JsfUtil.serializarReporte(i == 6 ? reporte : "002646ResumenTotalDistribucion", parametros, ConectorPool.ESQUEMA_SYSMAN,
								formato);
					} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
						System.out.println(e);

					}

				}

				String[] nombresArchivos = new String[7];


				if (ReportesBean.FORMATOS.PDF.equals(formato)) {
					nombresArchivos[0] = "002646ResumenTotalDistribucionSerPub.pdf";
					nombresArchivos[1] = "002646ResumenTotalDistribucionSerPubAdmin.pdf";
					nombresArchivos[2] = "002646ResumenTotalDistribucionSerPubOper.pdf";
					nombresArchivos[3] = "002646ResumenTotalDistribucionCentral.pdf";
					nombresArchivos[4] = "002646ResumenTotalDistribucionCentralAdmin.pdf";
					nombresArchivos[5] = "002646ResumenTotalDistribucionCentralOper.pdf";
					nombresArchivos[6] = reporte + ".pdf";
				}
				else {
					nombresArchivos[0] = "002646ResumenTotalDistribucionSerPub.xlsx";
					nombresArchivos[1] = "002646ResumenTotalDistribucionSerPubAdmin.xlsx";
					nombresArchivos[2] = "002646ResumenTotalDistribucionSerPubOper.xlsx";
					nombresArchivos[3] = "002646ResumenTotalDistribucionCentral.xlsx";
					nombresArchivos[4] = "002646ResumenTotalDistribucionCentralAdmin.xlsx";
					nombresArchivos[5] = "002646ResumenTotalDistribucionCentralOper.xlsx";
					nombresArchivos[6] = reporte + ".xlsx";
				}

				archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

			}else {

				Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);

				archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
						ConectorPool.ESQUEMA_SYSMAN, formato);
			}

		} catch (SystemException | JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SQLException | DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void generaReporte(String reporte) {
		try {

			// PARAMETROS DE REEMPLAZO EN LA CONSULTA
			HashMap<String, Object> reemplazos = new HashMap<>();
			reemplazos.put("anio1", ano1);
			reemplazos.put("mes1", mes1);
			reemplazos.put("periodo1", periodo1);
			reemplazos.put("anio2", ano2);
			reemplazos.put("mes2", mes2);
			reemplazos.put("periodo2", periodo2);
			reemplazos.put("proceso", proceso);

			// MANEJO DE PARAMETROS DEL REPORTE
			Map<String, Object> parametros = new HashMap<>();

			String sql= Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos);
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, reporte);

		}
		catch (JRException | IOException | NumberFormatException  | DRException | SQLException  | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private String consultarParametro(String nombre, boolean mayus)
			throws SystemException {
		return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
				new Date(), mayus);
	}
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control AnoDesde
	 * 
	 * 
	 */
	public void cambiarAnoDesde() {
		//<CODIGO_DESARROLLADO>
		mes1 = null;
		periodo1 = null;
		cargarListaMesDesde();
		cargarListaPeriodoDesde();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control AnoHasta
	 * 
	 * 
	 */
	public void cambiarAnoHasta() {
		//<CODIGO_DESARROLLADO>
		mes2 = null;
		periodo2 = null;
		cargarListaMesHasta();
		cargarListaPeriodoHasta();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control MesDesde
	 * 
	 * 
	 */
	public void cambiarMesDesde() {
		//<CODIGO_DESARROLLADO>
		periodo1 = null;
		cargarListaPeriodoDesde();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control MesHasta
	 * 
	 * 
	 */
	public void cambiarMesHasta() {
		//<CODIGO_DESARROLLADO>
		periodo2 = null;
		cargarListaPeriodoHasta();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Proceso
	 * 
	 * 
	 */
	public void cambiarProceso() {
		//<CODIGO_DESARROLLADO>
		ano1 = null;
		ano2 = null;
		mes1 = null;
		mes2 = null;
		periodo1 = null;
		periodo2 = null;
		cargarListaAnoDesde();
		cargarListaAnoHasta();
		cargarListaMesDesde();
		cargarListaMesHasta();
		cargarListaPeriodoDesde();
		cargarListaPeriodoHasta();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	private boolean validacionPeriodo() {
		boolean rta = true;
		periodoInicial = SysmanFunciones.concatenar(ano1, SysmanFunciones.padl(mes1, 2, "0"),
				SysmanFunciones.padl(periodo1, 2, "0"));
		periodoFinal = SysmanFunciones.concatenar(ano2, SysmanFunciones.padl(mes2, 2, "0"),
				SysmanFunciones.padl(periodo2, 2, "0"));
		if (Integer.parseInt(periodoInicial) > Integer.parseInt(periodoFinal)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2509"));
			rta = false;
		}
		return rta;

	}
	private boolean validaciones() {
		boolean rta = true;
		if (SysmanFunciones.validarVariableVacio(ano1)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2324"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(mes1)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB703"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(periodo1)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2618"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(ano2)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2326"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(mes2)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB704"));
			rta = false;
		}
		if (SysmanFunciones.validarVariableVacio(periodo2)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2621"));
			rta = false;

		}
		return rta;
	}
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano1
	 * 
	 * @return  ano1
	 */
	public String getAno1() {
		return ano1;
	}
	/**
	 * Asigna la variable  ano1
	 * 
	 * @param  ano1
	 * Variable a asignar en  ano1
	 */
	public void setAno1(String ano1) {
		this.ano1 = ano1;
	}
	/**
	 * Retorna la variable ano2
	 * 
	 * @return  ano2
	 */
	public String getAno2() {
		return ano2;
	}
	/**
	 * Asigna la variable  ano2
	 * 
	 * @param  ano2
	 * Variable a asignar en  ano2
	 */
	public void setAno2(String ano2) {
		this.ano2 = ano2;
	}
	/**
	 * Retorna la variable mes1
	 * 
	 * @return  mes1
	 */
	public String getMes1() {
		return mes1;
	}
	/**
	 * Asigna la variable  mes1
	 * 
	 * @param  mes1
	 * Variable a asignar en  mes1
	 */
	public void setMes1(String mes1) {
		this.mes1 = mes1;
	}
	/**
	 * Retorna la variable mes2
	 * 
	 * @return  mes2
	 */
	public String getMes2() {
		return mes2;
	}
	/**
	 * Asigna la variable  mes2
	 * 
	 * @param  mes2
	 * Variable a asignar en  mes2
	 */
	public void setMes2(String mes2) {
		this.mes2 = mes2;
	}
	/**
	 * Retorna la variable periodo1
	 * 
	 * @return  periodo1
	 */
	public String getPeriodo1() {
		return periodo1;
	}
	/**
	 * Asigna la variable  periodo1
	 * 
	 * @param  periodo1
	 * Variable a asignar en  periodo1
	 */
	public void setPeriodo1(String periodo1) {
		this.periodo1 = periodo1;
	}
	/**
	 * Retorna la variable periodo2
	 * 
	 * @return  periodo2
	 */
	public String getPeriodo2() {
		return periodo2;
	}
	/**
	 * Asigna la variable  periodo2
	 * 
	 * @param  periodo2
	 * Variable a asignar en  periodo2
	 */
	public void setPeriodo2(String periodo2) {
		this.periodo2 = periodo2;
	}
	/**
	 * Retorna la variable proceso
	 * 
	 * @return  proceso
	 */
	public String getProceso() {
		return proceso;
	}
	/**
	 * Asigna la variable  proceso
	 * 
	 * @param  proceso
	 * Variable a asignar en  proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnoDesde
	 * 
	 * @return listaAnoDesde
	 */
	public List<Registro> getListaAnoDesde() {
		return listaAnoDesde;
	}
	/**
	 * Asigna la lista listaAnoDesde
	 * 
	 * @param listaAnoDesde
	 * Variable a asignar en  listaAnoDesde
	 */
	public void setListaAnoDesde(List<Registro> listaAnoDesde) {
		this.listaAnoDesde = listaAnoDesde;
	}
	/**
	 * Retorna la lista listaAnoHasta
	 * 
	 * @return listaAnoHasta
	 */
	public List<Registro> getListaAnoHasta() {
		return listaAnoHasta;
	}
	/**
	 * Asigna la lista listaAnoHasta
	 * 
	 * @param listaAnoHasta
	 * Variable a asignar en  listaAnoHasta
	 */
	public void setListaAnoHasta(List<Registro> listaAnoHasta) {
		this.listaAnoHasta = listaAnoHasta;
	}
	/**
	 * Retorna la lista listaMesDesde
	 * 
	 * @return listaMesDesde
	 */
	public List<Registro> getListaMesDesde() {
		return listaMesDesde;
	}
	/**
	 * Asigna la lista listaMesDesde
	 * 
	 * @param listaMesDesde
	 * Variable a asignar en  listaMesDesde
	 */
	public void setListaMesDesde(List<Registro> listaMesDesde) {
		this.listaMesDesde = listaMesDesde;
	}
	/**
	 * Retorna la lista listaMesHasta
	 * 
	 * @return listaMesHasta
	 */
	public List<Registro> getListaMesHasta() {
		return listaMesHasta;
	}
	/**
	 * Asigna la lista listaMesHasta
	 * 
	 * @param listaMesHasta
	 * Variable a asignar en  listaMesHasta
	 */
	public void setListaMesHasta(List<Registro> listaMesHasta) {
		this.listaMesHasta = listaMesHasta;
	}
	/**
	 * Retorna la lista listaPeriodoDesde
	 * 
	 * @return listaPeriodoDesde
	 */
	public List<Registro> getListaPeriodoDesde() {
		return listaPeriodoDesde;
	}
	/**
	 * Asigna la lista listaPeriodoDesde
	 * 
	 * @param listaPeriodoDesde
	 * Variable a asignar en  listaPeriodoDesde
	 */
	public void setListaPeriodoDesde(List<Registro> listaPeriodoDesde) {
		this.listaPeriodoDesde = listaPeriodoDesde;
	}
	/**
	 * Retorna la lista listaPeriodoHasta
	 * 
	 * @return listaPeriodoHasta
	 */
	public List<Registro> getListaPeriodoHasta() {
		return listaPeriodoHasta;
	}
	/**
	 * Asigna la lista listaPeriodoHasta
	 * 
	 * @param listaPeriodoHasta
	 * Variable a asignar en  listaPeriodoHasta
	 */
	public void setListaPeriodoHasta(List<Registro> listaPeriodoHasta) {
		this.listaPeriodoHasta = listaPeriodoHasta;
	}
	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}
	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso
	 * Variable a asignar en  listaProceso
	 */
	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
