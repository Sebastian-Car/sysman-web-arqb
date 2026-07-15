package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.nomina.enums.ResumenPorCentroCostoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 04/08/2015
 *
 * @author spina
 * @version 2, 26/10/2017 - se refactoriza para dss, depuracion sonar y ejbs
 * 
 * @author obarragan
 * @version 3, 10/06/2019 - Se agrego opcion de imprimir header con imagenes
 *          adicionales.
 * 
 */
@ManagedBean
@ViewScoped
public class ResumenPorCentroCostoControlador extends BeanBaseModal {

	private final String compania;
	private final String modulo;
	private String opcion;
	private String ano;
	private String ano1;
	private String ano2;
	private String mes1;
	private String mes2;
	private String periodo1;
	private String periodo2;
	private String proceso;
    private String de;
	private String selCentroCosto;
	private String interfase;
	private String periodoInicial;
	private String periodoFinal;
	private String centroCosto;
	private List<Registro> listaAno1;
	private List<Registro> listaAno2;
	private List<Registro> listaMes1;
	private List<Registro> listaMes2;
	private List<Registro> listaPeriodo1;
	private List<Registro> listaPeriodo2;
	private List<Registro> listaProceso;
	private List<Registro> listaCentroCosto;
	private RegistroDataModel listaEmpleadoI;
	private RegistroDataModel listaEmpleadof;

	private String headerEspecial;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Creates a new instance of ResumenPorCentroCostoControlador
	 */

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	public ResumenPorCentroCostoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		ano = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		ano1 = ano2 = ano;
		mes1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		mes2 = mes1;
		periodo1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		periodo2 = periodo1;
		try {
			numFormulario = GeneralCodigoFormaEnum.RESUMEN_POR_CENTRO_COSTO_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(ResumenPorCentroCostoControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void inicializar() {
		cargarListaProceso();
		cargarListaAno1();
		cargarListaAno2();
		cargarListaMes1();
		cargarListaMes2();
		cargarListaPeriodo1();
		cargarListaPeriodo2();
		cargarListaCentroCosto();
		abrirFormulario();
	}

	public void cargarListaProceso() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaProceso = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4750.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaAno1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4751.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaAno2() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno2 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4751.getValue())
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
		param.put(GeneralParameterEnum.ANO.getName(), ano1);
		try {
			listaMes1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4752.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaMes2() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano2);
		try {
			listaMes2 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4752.getValue())
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
		param.put(GeneralParameterEnum.ANO.getName(), ano1);
		param.put(GeneralParameterEnum.MES.getName(), mes1);
		try {
			listaPeriodo1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4753.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaPeriodo2() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano2);
		param.put(GeneralParameterEnum.MES.getName(), mes2);
		try {
			listaPeriodo2 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4753.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaCentroCosto() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		try {
			listaCentroCosto = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4754.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void oprimirPresentar() {
		// <CODIGO_DESARROLLADO>
		getReporte(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		getReporte(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAno1() {
		// <CODIGO_DESARROLLADO>
		mes1 = null;
		periodo1 = null;
		cargarListaMes1();
		cargarListaPeriodo1();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAno2() {
		// <CODIGO_DESARROLLADO>
		mes2 = null;
		periodo2 = null;
		cargarListaMes2();
		cargarListaPeriodo2();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarMes1() {
		// <CODIGO_DESARROLLADO>
		periodo1 = null;
		cargarListaPeriodo1();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarMes2() {
		// <CODIGO_DESARROLLADO>
		periodo2 = null;
		cargarListaPeriodo2();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarProceso() {
		// <CODIGO_DESARROLLADO>
		ano1 = null;
		ano2 = null;
		mes1 = null;
		mes2 = null;
		periodo1 = null;
		periodo2 = null;
		cargarListaAno1();
		cargarListaAno2();
		// </CODIGO_DESARROLLADO>
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

	public void getReporte(FORMATOS formatos) {
		archivoDescarga = null;
		String reporte = null;
		String nombreInforme = "";
		try {
			if (!validaciones()) {
				return;
			}
			if (!validacionPeriodo()) {
				return;
			}

			if (opcion.equals("2")) {
				reporte = "800097ResumenCctoPorEmpleado";
	    		nombreInforme = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"FORMATO RESUMEN NOMINA POR CENTRO DE COSTO", modulo, new Date(), false),
						"000147ResumenPorCentroCosto");
			} else {
				if(opcion.equals("3")) {
					reporte = "002117RESUMENTOTALCENTROCOSTOFUNCIONAMIENTOIDI";
		    		nombreInforme = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"FORMATO RESUMEN NOMINA POR CENTRO DE COSTO FUNCIONAMIENTO", modulo, new Date(), false),
							reporte);
				}else {
					reporte = "800098ResumenCcto";
		    		nombreInforme = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"FORMATO RESUMEN NOMINA POR CENTRO DE COSTO", modulo, new Date(), false),
							"000147ResumenPorCentroCosto");
		    		if(nombreInforme.equals("002116RESUMENTOTALCENTROCOSTOIDI"))	{
		    			nombreInforme = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
								"FORMATO RESUMEN NOMINA POR CENTRO DE COSTO", modulo, new Date(), false),
								"000147ResumenPorCentroCosto");
		    			reporte=nombreInforme;
		    		}
				}
			}
			
			
			

			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("proceso", proceso);
			reemplazar.put("ano1", ano1);
			reemplazar.put("anio2", ano2);
			reemplazar.put("mes1", mes1);
			reemplazar.put("mes2", mes2);
			reemplazar.put("periodo1", periodo1);
			reemplazar.put("periodo2", periodo2);
			reemplazar.put("empleado", selCentroCosto == null ? "" : selCentroCosto);

			Map<String, Object> parametros = new HashMap<>();
            de = SysmanFunciones.concatenar("DE: ",
                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                    .parseInt(mes1)],
                    " de ", ano1, " Periodo ", periodo1);
			
			
			String cargoGerente = ejbSysmanUtil.consultarParametro(compania, "CARGO DEL GERENTE", modulo, new Date(),
					true);

			headerEspecial = ejbSysmanUtil.consultarParametro(compania, "FORMATOS ESPECIALES BUCARAMANGA", modulo,
					new Date(), true);

			String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();

			cargoGerente = SysmanFunciones.validarVariableVacio(cargoGerente) ? "CARGO DEL GERENTE" : cargoGerente;

			String parametroEntre = idioma.getString("TB_TB3745")
					.replace("s$mes1$s", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes1)])
					.replace("s$ano1$s", ano1).replace("s$periodo1$s", periodo1)
					.replace("s$mes2$s", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes2)])
					.replace("s$ano2$s", ano2).replace("s$periodo2$s", periodo2);

			String nombreTesorero = ejbSysmanUtil.consultarParametro(compania, "NOMBRE CARGO TESORERO PAGADOR", modulo,
					new Date(), true);
			nombreTesorero = SysmanFunciones.validarVariableVacio(nombreTesorero) ? "NOMBRE CARGO TESORERO PAGADOR"
					: nombreTesorero;

			String cargoTesorero = ejbSysmanUtil.consultarParametro(compania, "CARGO TESORERO PAGADOR", modulo,
					new Date(), true);
			cargoTesorero = SysmanFunciones.validarVariableVacio(cargoTesorero) ? "CARGO TESORERO PAGADOR"
					: cargoTesorero;

			String nombreJefePresupuesto = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE PRESUPUESTO", modulo,
					new Date(), true);
			nombreJefePresupuesto = SysmanFunciones.validarVariableVacio(nombreJefePresupuesto)
					? "NOMBRE JEFE PRESUPUESTO"
					: nombreJefePresupuesto;

			String cargoJefePresupuesto = ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE PRESUPUESTO", modulo,
					new Date(), true);
			cargoJefePresupuesto = SysmanFunciones.validarVariableVacio(cargoJefePresupuesto) ? "CARGO JEFE PRESUPUESTO"
					: cargoJefePresupuesto;

			String nombreQuienAutoriza = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN AUTORIZA NOMINA",
					modulo, new Date(), true);
			nombreQuienAutoriza = SysmanFunciones.validarVariableVacio(nombreQuienAutoriza) ? "NOMBRE QUIEN AUTORIZA"
					: nombreQuienAutoriza;

			String cargoQuienAutoriza = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN AUTORIZA NOMINA",
					modulo, new Date(), true);
			cargoQuienAutoriza = SysmanFunciones.validarVariableVacio(cargoQuienAutoriza) ? "CARGO QUIEN AUTORIZA"
					: cargoQuienAutoriza;

			String nombreQuienRevisa = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN REVISA NOMINA",
					modulo, new Date(), true);
			String nombreAutoriza = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN AUTORIZA NOMINA",
					modulo, new Date(), false);

			String cargoAutoriza = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
					new Date(), false);
			String nombreRevisaNomina = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DE QUIEN REVISA NOMINA",
					modulo, new Date(), false);

			String cargoRevisaNomina = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN REVISA NOMINA",
					modulo, new Date(), false);
			// inicio dcastiblanco
			nombreQuienRevisa = SysmanFunciones.validarVariableVacio(nombreQuienRevisa) ? "NOMBRE QUIEN REVISA"
					: nombreQuienRevisa;

			String nombreJefeRecursos = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE RECURSOS HUMANOS",
					modulo, new Date(), true);
			nombreJefeRecursos = SysmanFunciones.validarVariableVacio(nombreJefeRecursos)
					? "NOMBRE JEFE RECURSOS HUMANOS"
					: nombreJefeRecursos;

			String cargoJefeRecursos = ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE RECURSOS HUMANOS", modulo,
					new Date(), false);
			cargoJefeRecursos = SysmanFunciones.validarVariableVacio(cargoJefeRecursos) ? "CARGO JEFE RECURSOS HUMANOS"
					: cargoJefeRecursos;

			String elaboro = ejbSysmanUtil.consultarParametro(compania, "ELABORADO POR", modulo, new Date(), true);
			elaboro = SysmanFunciones.validarVariableVacio(elaboro) ? "ELABORADO POR" : elaboro;
			// fin dcastiblanco
			String cargoQuienRevisa = ejbSysmanUtil.consultarParametro(compania, "CARGO DE QUIEN REVISA NOMINA", modulo,
					new Date(), false);
			cargoQuienRevisa = SysmanFunciones.validarVariableVacio(cargoQuienRevisa) ? "CARGO QUIEN REVISA"
					: cargoQuienRevisa;

			String nombreGerente = ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL GERENTE", modulo, new Date(),
					true);
			nombreGerente = SysmanFunciones.validarVariableVacio(nombreGerente) ? "NOMBRE DEL GERENTE" : nombreGerente;


            String jefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE DESARROLLO HUMANO", modulo, new Date(), false);
            String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO JEFE DESARROLLO HUMANO", modulo, new Date(), false); 
            String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE NOMINA", modulo, new Date(), false);
            String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO RESPONSABLE DE NOMINA", modulo, new Date(), false);
            String directorAdministrativo = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE DIRECTOR ADMINISTRATIVO", modulo, new Date(), false);
            String cargoDirectorAdministrativo = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO DIRECTOR ADMINISTRATIVO", modulo, new Date(), false);
            
            parametros.put("PR_ANO1", ano1);
            parametros.put("PR_MES1", mes1);
            parametros.put("PR_PERIODO1", periodo1);
            parametros.put("PR_DE", de);
            
            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
            parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
            parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
            parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
            parametros.put("PR_NOMBRE_DIRECTOR_ADMINISTRATIVO", directorAdministrativo);
            parametros.put("PR_CARGO_DIRECTOR_ADMINISTRATIVO", cargoDirectorAdministrativo);
			
			parametros.put("PR_ENTRE", parametroEntre);
			parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
			parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR", nombreTesorero);
			parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR", cargoTesorero);
			parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO", nombreJefePresupuesto);
			parametros.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO", cargoJefePresupuesto);
			parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA", nombreAutoriza);
			parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", cargoAutoriza);
			parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA", nombreRevisaNomina);
			parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA", cargoRevisaNomina);
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
			parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreJefeRecursos);
			parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRecursos);

			// parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
			// cargoJefeRecursos);

			parametros.put("PR_ELABORADO_POR", elaboro);

			parametros.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI") ? true : false);
			parametros.put("PR_IMAGEN_ESPECIAL", sticker);
			
            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
            parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
            parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
            parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
            parametros.put("PR_NOMBRE_DIRECTOR_ADMINISTRATIVO", directorAdministrativo);
            parametros.put("PR_CARGO_DIRECTOR_ADMINISTRATIVO", cargoDirectorAdministrativo);

			String strsql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);

			if (service.getConteoConsulta(strsql) <= 0) {
				JsfUtil.agregarMensajeError(
						idioma.getString("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
				return;
			}
			
			// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
			
            parametros.put("PR_STRSQL", strsql);
			archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, parametros, ConectorPool.ESQUEMA_SYSMAN,
					formatos);

		} catch (JRException | IOException | SysmanException | SystemException ex) {
			Logger.getLogger(ResumPorCentroCostoControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

	}

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
		if (SysmanFunciones.validarVariableVacio(opcion)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1855"));

			rta = false;
		}
		return rta;

	}

	public void onRowSelectEmpleado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		selCentroCosto = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();

	}

	public void cambiarOpcion() {
		// <CODIGO_DESARROLLADO>

		// <CODIGO_DESARROLLADO>
	}

	public String getCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}

	public String getInterfase() {
		return interfase;
	}

	public void setInterfase(String interfase) {
		this.interfase = interfase;
	}

	public String getPeriodoInicial() {
		return periodoInicial;
	}

	public void setPeriodoInicial(String periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

	public String getPeriodoFinal() {
		return periodoFinal;
	}

	public void setPeriodoFinal(String periodoFinal) {
		this.periodoFinal = periodoFinal;
	}

	public String getOpcion() {
		return opcion;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	public String getAno1() {
		return ano1;
	}

	public void setAno1(String ano1) {
		this.ano1 = ano1;
	}

	public String getAno2() {
		return ano2;
	}

	public void setAno2(String ano2) {
		this.ano2 = ano2;
	}

	public String getMes1() {
		return mes1;
	}

	public void setMes1(String mes1) {
		this.mes1 = mes1;
	}

	public String getMes2() {
		return mes2;
	}

	public void setMes2(String mes2) {
		this.mes2 = mes2;
	}

	public String getPeriodo1() {
		return periodo1;
	}

	public void setPeriodo1(String periodo1) {
		this.periodo1 = periodo1;
	}

	public String getPeriodo2() {
		return periodo2;
	}

	public void setPeriodo2(String periodo2) {
		this.periodo2 = periodo2;
	}

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public String getSelCentroCosto() {
		return selCentroCosto;
	}

	public void setSelCentroCosto(String selCentroCosto) {
		this.selCentroCosto = selCentroCosto;
	}

	public List<Registro> getListaAno1() {
		return listaAno1;
	}

	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}

	public List<Registro> getListaAno2() {
		return listaAno2;
	}

	public void setListaAno2(List<Registro> listaAno2) {
		this.listaAno2 = listaAno2;
	}

	public List<Registro> getListaMes1() {
		return listaMes1;
	}

	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
	}

	public List<Registro> getListaMes2() {
		return listaMes2;
	}

	public void setListaMes2(List<Registro> listaMes2) {
		this.listaMes2 = listaMes2;
	}

	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}

	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}

	public List<Registro> getListaPeriodo2() {
		return listaPeriodo2;
	}

	public void setListaPeriodo2(List<Registro> listaPeriodo2) {
		this.listaPeriodo2 = listaPeriodo2;
	}

	public List<Registro> getListaProceso() {
		return listaProceso;
	}

	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}

	public List<Registro> getListaCentroCosto() {
		return listaCentroCosto;
	}

	public void setListaEmpleado(List<Registro> listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}

	public RegistroDataModel getListaEmpleadoI() {
		return listaEmpleadoI;
	}

	public void setListaEmpleadoI(RegistroDataModel listaEmpleadoI) {
		this.listaEmpleadoI = listaEmpleadoI;
	}

	public RegistroDataModel getListaEmpleadof() {
		return listaEmpleadof;
	}

	
	public String getDe() {
		return de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	public void setListaEmpleadof(RegistroDataModel listaEmpleadof) {
		this.listaEmpleadof = listaEmpleadof;
	}

	@Override
	public void abrirFormulario() {
		opcion = "1";
	}

	public String getHeaderEspecial() {
		return headerEspecial;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public String isHeaderEspecial() {
		return headerEspecial;
	}

	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}

}
