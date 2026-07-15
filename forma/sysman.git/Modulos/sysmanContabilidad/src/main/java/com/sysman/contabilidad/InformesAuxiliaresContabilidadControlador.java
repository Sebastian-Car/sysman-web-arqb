package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.impl.EjbContabilidadSiete;
import com.sysman.contabilidad.enums.InformesAuxiliaresContabilidadControladorEnum;
import com.sysman.contabilidad.enums.InformesAuxiliaresContabilidadControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 21/04/2016
 * @author yrojas
 * @version 2, 17/04/2017 Se cambiaron las consultas por la invocaciï¿½n de los
 *          DSS. Se cambio controlador segun especificaciones del SonarLint.
 * @author yrojas
 * @version 3, 20/04/2017 Se cambiaron los llamados de Acciones por las
 *          invocaciones de los ejb.
 * @author gfigueredo
 * @version 4 18/05/2021 Se añade la consulta y generación de dos reportes
 *          auxiliares, y se realizan los ajustes marcados por SonarLint.
 * 
 * @author gfigueredo
 * @version 03/06/2021, Se añade consulta al procedimiento PCK_CONTABILIDAD7
 *          para asignar el valor del pivot en los archivos planos de
 *          {@link #STRINFAUXGENERALCUENTACONS} y {@link #STRINFAUXCCTERCONS}
 * @see #generaInforme(com.sysman.jsfutil.ReportesBean.FORMATOS)
 */
@ManagedBean
@ViewScoped
public class InformesAuxiliaresContabilidadControlador extends BeanBaseModal {
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	@EJB
	private EjbContabilidadSiete ejbContabilidadSiete;
	private final String compania;
	private static final String STRINFCOSTOTERCERO = "000648DCAuxiliarCCostoyTercero";
	private static final String STRINFAUXCTOCTA = "000649LisAuxCentroCta";
	private static final String STRINFAUXCTOCTACOS = "000650LisAuxCentroCtaCOS";
	private static final String STRINFAUXCCTERCONS = "002271ListAuxCentroCostoTercero";
	private static final String STRINFAUXGENERALCUENTACONS = "002270ListAuxGeneralCuentaConsol";
	private static final String STRINFAUXCENTREF = "002599AUXILIARCENTROTERCEROREFERENCIA";
	private static final String STRINFAUXCENTREFF = "002590AUXILIARCENTROTERCEROREFERENCIAEXCEL";
	private static final String FORMATO_FECHA = "dd/MM/yyyy";
	private boolean especial;
	private boolean consolidado;
	private boolean totalPorTercero;
	private boolean sinValorDebito;
	private boolean terceros;
	private boolean especialVisible;
	private boolean consolidadoVisible;
	private boolean totalTerceroVisible;
	private boolean sinValorDebVisible;
	private boolean tercerosCorto;
	private boolean auxGralCuenta;
	private boolean centroCostoyCuenta;
	private boolean centroCostoTercero;
	private boolean centroCostoTerceroAuxiliar;
	private boolean cuentayAuxGral;
	private boolean cuentayCentroCosto;
	private boolean cuentayTercero;
	private boolean terceroyAuxiliar;
	private boolean terceroyCuenta;
	private boolean centroCostoTerceroReferencia;
	private boolean cuentaCentroCostoyReferencia;
	private boolean porCuenta;
	private boolean visiblePorCuenta;
	private String tipoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private String centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private String auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private String anio;
	private final Date fechaActual;
	private String mesInicial;
	private String mesFinal;
	private Date fechaInicial;
	private Date fechaFinal;
	private StreamedContent archivoDescarga;
	private List<Registro> listaAnioTrabajo;
	private List<Registro> listaMesInicial;
	private List<Registro> listaMesFinal;
	private RegistroDataModelImpl listaTipoInicial;
	private RegistroDataModelImpl listaTipoFinal;
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;
	private RegistroDataModelImpl listaCentroInicial;
	private RegistroDataModelImpl listaCentroFinal;
	private RegistroDataModelImpl listaTerceroInicial;
	private RegistroDataModelImpl listaTerceroFinal;
	private RegistroDataModelImpl listaAuxiliarInicial;
	private RegistroDataModelImpl listaAuxiliarFinal;

	private boolean anioVisible;
	private boolean mesVisible;
	private boolean fechaVisible;
	private boolean cuentaVisible;
	private boolean tipoVisible;
	private boolean centroVisible;
	private boolean terceroVisible;
	private boolean auxiliarVisible;
	private boolean contabilidadVisible;
	private boolean referenciaVisible;
	private String sucursalInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String sucursalFinal = SysmanConstantes.DEFECTOFINAL_STRING;

	private String refInicial= SysmanConstantes.DEFECTOINICIAL_STRING;
	private String refFinal= SysmanConstantes.DEFECTOFINAL_STRING;
	private RegistroDataModelImpl listaReferenciInicial;
	private RegistroDataModelImpl listaReferenciFinal;
	
	/**
	 * Creates a new instance of InformesAuxiliaresContabilidadControlador
	 */
	public InformesAuxiliaresContabilidadControlador() {
		super();
		compania = SessionUtil.getCompania();
		fechaActual = new Date();
		try {
			if (!"2030203".equals(SessionUtil.getMenuActual())) {
				contabilidadVisible = true;
			}
			numFormulario = GeneralCodigoFormaEnum.INFORMES_AUXILIARES_CONTABILIDAD_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(InformesAuxiliaresContabilidadControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void inicializar() {
		fechaInicial = fechaFinal = fechaActual;
		anio = String.valueOf(SysmanFunciones.ano(fechaActual));
		terceros = true;
		especialVisible = true;
		consolidadoVisible = false;
		terceroVisible = true;
		fechaVisible = true;
		cuentaVisible = true;
		visiblePorCuenta = false;
		
		cargarListaTerceroInicial();
		cargarListaCuentaInicial();
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		// heredado del bean base
	}

	public void cargarListaAnioTrabajo() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnioTrabajo = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							InformesAuxiliaresContabilidadControladorUrlEnum.URL5019.getValue())
					.getUrl(),
					param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaMesInicial() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try {
			listaMesInicial = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							InformesAuxiliaresContabilidadControladorUrlEnum.URL5244.getValue())
					.getUrl(),
					param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaMesFinal() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(InformesAuxiliaresContabilidadControladorEnum.PARAM0.getValue(), mesInicial);

		try {
			listaMesFinal = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							InformesAuxiliaresContabilidadControladorUrlEnum.URL5521.getValue())
					.getUrl(),
					param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaTipoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL5853.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTipoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL6296.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(InformesAuxiliaresContabilidadControladorEnum.PARAM1.getValue(), tipoInicial);

		listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL6798.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7333.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(InformesAuxiliaresContabilidadControladorEnum.PARAM2.getValue(), cuentaInicial);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCentroInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7456.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCentroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCentroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7561.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(InformesAuxiliaresContabilidadControladorEnum.PARAM3.getValue(), centroInicial);

		listaCentroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTerceroInicial() {
		if (terceros || centroCostoTerceroAuxiliar || cuentaCentroCostoyReferencia || centroCostoTerceroReferencia) {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7678.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.NOMBRE.getName());
		} else {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7789.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.NOMBRE.getName());
		}
	}

	public void cargarListaTerceroFinal() {
		if (terceros || centroCostoTerceroAuxiliar  || cuentaCentroCostoyReferencia || centroCostoTerceroReferencia) {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7795.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(InformesAuxiliaresContabilidadControladorEnum.PARAM5.getValue(), terceroInicial);

			listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.NOMBRE.getName());
		} else {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7805.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(InformesAuxiliaresContabilidadControladorEnum.PARAM5.getValue(), terceroInicial);

			listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.NOMBRE.getName());
		}
	}

	public void cargarListaAuxiliarInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7812.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaAuxiliarFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL7956.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(InformesAuxiliaresContabilidadControladorEnum.PARAM2.getValue(), auxiliarInicial);

		listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	
	/**
	 * 
	 * Carga la lista listaReferenciInicial
	 *
	 */
	public void cargarListaReferenciInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(

				InformesAuxiliaresContabilidadControladorUrlEnum.URL6234.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(GeneralParameterEnum.ANO.name(), anio);

		listaReferenciInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	public void cargarListaReferenciFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformesAuxiliaresContabilidadControladorUrlEnum.URL6235.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(GeneralParameterEnum.ANO.name(), anio);
		param.put(GeneralParameterEnum.CODIGOINICIAL.name(), refInicial);

		listaReferenciFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}


	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInformeExcel(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	private void generaInformeExcel(ReportesBean.FORMATOS formato) {
	    String reporte = getReporte(formato);
	    try {
	        HashMap<String, Object> reemplazar = new HashMap<>();
	        if (!validarVacios(reporte)) {
	            if (STRINFCOSTOTERCERO.equals(reporte)) {
	                asignarReemplazosCostoTercero(reemplazar);
	            } else {
	                asignarReemplazos(reemplazar);
	            }

	            Map<String, Object> parametros = new HashMap<>();

	            Reporteador.resuelveConsulta(STRINFAUXCTOCTACOS.equals(reporte) ? STRINFAUXCTOCTA : reporte,
	                    Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);

	            asignarParametros(parametros);

	            if (reporte.equals(STRINFAUXGENERALCUENTACONS)) {
	                String condicionPivot = ejbContabilidadSiete.getPrepararPivotInformeAuxGC(compania, anio,
	                        auxiliarInicial, auxiliarFinal, cuentaInicial, cuentaFinal, fechaInicial, fechaFinal);
	                reemplazar.put("pivot", condicionPivot);
	                String indicador = ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", "-1", new Date(), false);
	                if (indicador.equals("SI")) {
	                    String sql = Reporteador.resuelveConsulta(STRINFAUXGENERALCUENTACONS,
	                            Integer.parseInt(SessionUtil.getModulo()), reemplazar);

	                    archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN,
	                            formato, "Listado Auxiliar General Cuentas Consolidado");
	                } else {
	                    archivoDescarga = JsfUtil.exportarStreamed(STRINFAUXGENERALCUENTACONS, parametros, ConectorPool.ESQUEMA_SYSMAN,
	                            formato);
	                }
	            } else if (reporte.equals(STRINFAUXCCTERCONS)) {
	                String condicionPivot = ejbContabilidadSiete.getPrepararPivotInformeAuxCCT(compania, anio,
	                        cuentaInicial, cuentaFinal, terceroInicial, terceroFinal, centroInicial, centroFinal,
	                        auxiliarInicial, auxiliarFinal, tipoInicial, tipoFinal, mesInicial, mesFinal);
	                reemplazar.put("pivot", condicionPivot);

	                String sql = Reporteador.resuelveConsulta(STRINFAUXCCTERCONS,
	                        Integer.parseInt(SessionUtil.getModulo()), reemplazar);
	                String indicador = ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", "-1", new Date(), false);
	                if (indicador.equals("SI")) {
	                    archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN,
	                            formato, "Auxiliar con Saldos por Centro de Costo, Tercero y Auxiliar General");
	                } else {
	                    archivoDescarga = JsfUtil.exportarStreamed(STRINFAUXCCTERCONS, parametros, ConectorPool.ESQUEMA_SYSMAN,
	                            formato);
	                }
	                
	            } else if (reporte.equals(STRINFAUXCENTREFF)) {	               

	                String sql = Reporteador.resuelveConsulta(STRINFAUXCENTREFF,
	                        Integer.parseInt(SessionUtil.getModulo()), reemplazar);

	                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN,
	                        formato, "CENTRO COSTO Y TERCERO REFERENCIA");
	            } 
	            else {
	                String indicador = ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", "-1", new Date(), false);
	                if (indicador.equals("SI")) {
	                    String sql = Reporteador.resuelveConsulta(reporte,
	                            Integer.parseInt(SessionUtil.getModulo()), reemplazar);

	                    archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
	                            ConectorPool.ESQUEMA_SYSMAN, formato, reporte);
	                } else {
	                    archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
	                            formato);
	                }
	            }
	        }
	    } catch (FileNotFoundException ex) {
	        JsfUtil.agregarMensajeInformativo(
	                idioma.getString(Constantes.MSM_INFORME_NO_EXISTE) + ex.getMessage() + " " + reporte);
	        Logger.getLogger(InformesAuxiliaresContabilidadControlador.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (JRException | IOException ex) {
	        JsfUtil.agregarMensajeError(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + ex.getMessage());
	        Logger.getLogger(InformesAuxiliaresContabilidadControlador.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (SysmanException | SQLException | DRException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    } catch (SystemException | ParseException e) {
	        e.printStackTrace();
	    }
	}

	private void generaInforme(ReportesBean.FORMATOS formato) {
		String reporte = getReporte(formato);
		try {

			HashMap<String, Object> reemplazar = new HashMap<>();
			if (!validarVacios(reporte)) {
				if (STRINFCOSTOTERCERO.equals(reporte)) {
					asignarReemplazosCostoTercero(reemplazar);
				} else {
					asignarReemplazos(reemplazar);
				}

				Map<String, Object> parametros = new HashMap<>();

				Reporteador.resuelveConsulta(STRINFAUXCTOCTACOS.equals(reporte) ? STRINFAUXCTOCTA : reporte,
						Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);

				asignarParametros(parametros);

				if (reporte.equals(STRINFAUXGENERALCUENTACONS)) {

					String condicionPivot = ejbContabilidadSiete.getPrepararPivotInformeAuxGC(compania, anio,
							auxiliarInicial, auxiliarFinal, cuentaInicial, cuentaFinal, fechaInicial, fechaFinal);
					reemplazar.put("pivot", condicionPivot);

					String sql = Reporteador.resuelveConsulta(STRINFAUXGENERALCUENTACONS,
							Integer.parseInt(SessionUtil.getModulo()), reemplazar);

					archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN,
							ReportesBean.FORMATOS.EXCEL97, "Listado Auxiliar General Cuentas Consolidado");
				} else if (reporte.equals(STRINFAUXCCTERCONS)) {

					String condicionPivot = ejbContabilidadSiete.getPrepararPivotInformeAuxCCT(compania, anio,
							cuentaInicial, cuentaFinal, terceroInicial, terceroFinal, centroInicial, centroFinal,
							auxiliarInicial, auxiliarFinal, tipoInicial, tipoFinal, mesInicial, mesFinal);
					reemplazar.put("pivot", condicionPivot);

					String sql = Reporteador.resuelveConsulta(STRINFAUXCCTERCONS,
							Integer.parseInt(SessionUtil.getModulo()), reemplazar);

					archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN,
							ReportesBean.FORMATOS.EXCEL97,
							"Auxiliar con Saldos por Centro de Costo, Tercero y Auxiliar General");
				} else {
					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				}
			}
		} catch (FileNotFoundException ex) {
			JsfUtil.agregarMensajeInformativo(
					idioma.getString(Constantes.MSM_INFORME_NO_EXISTE) + ex.getMessage() + " " + reporte);
			Logger.getLogger(InformesAuxiliaresContabilidadControlador.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JRException | IOException ex) {
			JsfUtil.agregarMensajeError(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + ex.getMessage());
			Logger.getLogger(InformesAuxiliaresContabilidadControlador.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SysmanException | SQLException | DRException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (SystemException | ParseException e) {
			e.printStackTrace();
		}

	}

	private void asignarReemplazos(HashMap<String, Object> reemplazar) {
		reemplazar.put("anoTrabajo", anio == null ? "" : anio);
		reemplazar.put("mesInicial", mesInicial == null ? "" : mesInicial);
		reemplazar.put("mesFinal", mesFinal == null ? "" : mesFinal);
		reemplazar.put("terceroInicial", terceroInicial == null ? "" : terceroInicial);
		reemplazar.put("terceroFinal", terceroFinal == null ? "" : terceroFinal);		
		reemplazar.put("codigoInicial", cuentaInicial == null ? "" : cuentaInicial);
		reemplazar.put("codigoFinal", cuentaFinal == null ? "" : cuentaFinal);
		reemplazar.put("centroInicial", centroInicial == null ? "" : centroInicial);
		reemplazar.put("centroFinal", centroFinal == null ? "" : centroFinal);

		asignarReemplazos2(reemplazar);
	}

	private void asignarReemplazos2(HashMap<String, Object> reemplazar) {
		reemplazar.put("tipoInicial", tipoInicial == null ? "" : tipoInicial);
		reemplazar.put("tipoFinal", tipoFinal == null ? "" : tipoFinal);
		reemplazar.put("auxiliarInicial", auxiliarInicial == null ? "" : auxiliarInicial);
		reemplazar.put("auxiliarFinal", auxiliarFinal == null ? "" : auxiliarFinal);
		reemplazar.put("sucursalInicial", sucursalInicial == null ? "" : sucursalInicial);
		 reemplazar.put("refiInicial", refInicial == null ? "" : refInicial);
         reemplazar.put("refFinal", refFinal == null ? "" : refFinal);

		asignarReemplazos3(reemplazar);
	}

	private void asignarReemplazos3(HashMap<String, Object> reemplazar) {
		String groupBySinTotal = totalPorTercero
				? " V_DETALLE_AUXILIAR_CNT.DIRECCION, \n" + " V_DETALLE_AUXILIAR_CNT.DEPARTAMENTO, \n"
				+ " V_DETALLE_AUXILIAR_CNT.CIUDAD, \n" + " V_DETALLE_AUXILIAR_CNT.PAIS, \n"
				+ " V_DETALLE_AUXILIAR_CNT.TIPO_CPTE, \n"
				: "";
		String havingSinDebito = sinValorDebito ? " HAVING SUM(V_DETALLE_AUXILIAR_CNT.VALOR_DEBITO) IN (0) " : "";

		reemplazar.put("fechaInicial", fechaInicial == null ? "" : SysmanFunciones.formatearFecha(fechaInicial));
		reemplazar.put("fechaFinal", fechaFinal == null ? "" : SysmanFunciones.formatearFecha(fechaFinal));
		reemplazar.put("mesAnterior", fechaInicial == null ? "" : SysmanFunciones.getParteFecha(fechaInicial, Calendar.MONTH));
		reemplazar.put("groupBySinTotal", groupBySinTotal);
		reemplazar.put("havingSinDebito", havingSinDebito);
	}

	private void asignarReemplazosCostoTercero(HashMap<String, Object> reemplazar) {
		reemplazar.put("es_id", "0");
		reemplazar.put("id_codigo", idioma.getString("TG_CODIGO2"));
		reemplazar.put("id_cuenta", idioma.getString("TG_CUENTA"));
		reemplazar.put("filtrosCentro",
				" AND DETALLE_COMPROBANTE_CNT.CENTRO_COSTO BETWEEN '" + centroInicial + "' AND '" + centroFinal + "' ");
		reemplazar.put("mesAnterior", String.valueOf(Integer.parseInt(mesInicial) - 1));
		reemplazar.put("anio", anio);
		reemplazar.put("tipoInicial", tipoInicial);
		reemplazar.put("tipoFinal", tipoFinal);
		reemplazar.put("cuentaInicial", cuentaInicial);
		reemplazar.put("cuentaFinal", cuentaFinal);
		reemplazar.put("fechaInicial", "01/" + mesInicial + "/" + anio);

		String fechaFin = "";
		try {
			fechaFin = SysmanFunciones.convertirAFechaCadena(
					SysmanFunciones.ultimoDiaDate(SysmanFunciones
							.convertirAFecha("01/" + ("13".equals(mesFinal) ? "12" : mesFinal) + "/" + anio)),
					FORMATO_FECHA);
		} catch (ParseException e) {
			Logger.getLogger(InformesAuxiliaresContabilidadControlador.class.getName()).log(Level.SEVERE, null, e);
		}

		reemplazar.put("condicionReferencias", " ");
		reemplazar.put("fechaFinal", fechaFin);
		reemplazar.put("filtrosTercero",
				" AND TRIM(TERCERO_DET.NOMBRE) BETWEEN '" + terceroInicial + "' AND '" + terceroFinal + "' ");
		String baseAuxiliar = Reporteador.resuelveConsulta("800044BaseAuxiliares",
				Integer.parseInt(SessionUtil.getModulo()), reemplazar);
		reemplazar.put("baseAuxiliar", baseAuxiliar);
	}

	private void asignarParametros(Map<String, Object> parametros) {
		parametros.put("PR_ANOTRABAJO", anio == null ? "" : anio);
		parametros.put("PR_TERCEROINICIAL", terceroInicial == null ? "" : terceroInicial);
		parametros.put("PR_TERCEROFINAL", terceroFinal == null ? "" : terceroFinal);
		parametros.put("PR_CODIGOINICIAL", cuentaInicial == null ? "" : cuentaInicial);
		parametros.put("PR_CODIGOFINAL", cuentaFinal == null ? "" : cuentaFinal);
		parametros.put("PR_CUENTAINICIAL", cuentaInicial == null ? "" : cuentaInicial);
		parametros.put("PR_CUENTAFINAL", cuentaFinal == null ? "" : cuentaFinal);
		parametros.put("PR_CENTROINICIAL", centroInicial == null ? "" : centroInicial);
		parametros.put("PR_CENTROFINAL", centroFinal == null ? "" : centroFinal);
		parametros.put("PR_USERNAME", SessionUtil.getUser().getCodigo());
		
		parametros.put("PR_COMPANIA", SessionUtil.getCompaniaIngreso().getNombre() + " NIT: " + SessionUtil.getCompaniaIngreso().getNit());

		asignarParametros2(parametros);
	}

	private void asignarParametros2(Map<String, Object> parametros) {
		parametros.put("PR_MESINICIAL", mesInicial == null ? "" : mesInicial);
		parametros.put("PR_MESFINAL", mesFinal == null ? "" : mesFinal);
		parametros.put("PR_TIPOINICIAL", tipoInicial == null ? "" : tipoInicial);
		parametros.put("PR_TIPOFINAL", tipoFinal == null ? "" : tipoFinal);
		parametros.put("PR_AUXILIARINICIAL", auxiliarInicial == null ? "" : auxiliarInicial);
		parametros.put("PR_AUXILIARFINAL", auxiliarFinal == null ? "" : auxiliarFinal);
		parametros.put("PR_REFERENCIAINICIAL", refInicial == null ? "" : refInicial);
		parametros.put("PR_REFERENCIAFINAL", refFinal == null ? "" : refFinal);
		

		try {
			parametros.put("PR_FECHAINICIAL",
					fechaInicial == null ? "" : SysmanFunciones.convertirAFechaCadena(fechaInicial, FORMATO_FECHA));
			parametros.put("PR_FECHAFINAL",
					fechaFinal == null ? "" : SysmanFunciones.convertirAFechaCadena(fechaFinal, FORMATO_FECHA));
		} catch (ParseException e) {
			Logger.getLogger(InformesAuxiliaresContabilidadControlador.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private String asignarReporteTerceros() {
		String reporte="";
		if (especial) {
			
			reporte = "000653LisAuxContTercerosEsp";

		} else {
			try {
			String indicador=ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", "-1", new Date(), false);
            if (indicador.equals("SI")) {
            	reporte = "800512LisAuxContTerceros";
			}else {
				reporte = "000651LisAuxContTerceros";
			}
			}
            catch ( SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                    + e.getMessage());
            }
			
		}
		return reporte;
	}

	private String asignarReporteCentroCosto(String formatoCalidad) {
		String reporte;
		if ((formatoCalidad != null) && "SI".equals(formatoCalidad)) {
			reporte = STRINFAUXCTOCTACOS;
		} else {
			reporte = STRINFAUXCTOCTA;
		}
		return reporte;
	}

	private String asignarReporteCuenta() {
		String reporte;
		if (especial) {
			reporte = "000657LisAuxCtaTerceroGN";
		} else if(consultaParametro("MANEJA INFORME AUXILIAR CUENTA Y TERCERO AGRUPADO").equals("SI"))
		{
			reporte = "002725LisAuxCtaTercero_SINCHI";
		}
		else
		{
			reporte = "000656LisAuxCtaTercero";
		}
		return reporte;
	}
	
	private String asignarReporteCuentaCentroReferencia() {
		String reporte;
		if (totalPorTercero) {
			reporte = "002619AuxTerceroCuentaCostoRef";
		}else if(porCuenta) {
			reporte = "002623AuxTerceroCuentaCostoRefporCuenta";
		}else {
			reporte = "002612AuxCuentaCCostoReferencia";
		}
		return reporte;
	}

	public String getReporte(ReportesBean.FORMATOS formato) {
		String reporte = "";
		if (terceros) {
			reporte = asignarReporteTerceros();
		} else if (tercerosCorto) {
			reporte = "000652LisAuxContTercerosCorto";
		} else if (auxGralCuenta) {
			reporte = consolidado ? STRINFAUXGENERALCUENTACONS : "000658LisAuxGralCta";
		} else if (centroCostoyCuenta) {
			reporte = asignarReporteCentroCosto(consultaParametro("FORMATO CALIDAD"));
		} else if (centroCostoTercero) {
			reporte = STRINFCOSTOTERCERO;
		} else if (centroCostoTerceroAuxiliar) {
			reporte = consolidado ? STRINFAUXCCTERCONS : "002515AuxiliarCentroTerceroAuxiliar";
		} else if (cuentayAuxGral) {
			reporte = "000655LisAuxCtaGral";
		} else if (cuentayCentroCosto) {
			reporte = "000654LisAuxCtaCentro";
		} else if (cuentayTercero) {
			reporte = asignarReporteCuenta();
		} else if (terceroyAuxiliar) {
			reporte = "000661SEAAUXILIARTERCEROAUXILIAR";
		} else if (terceroyCuenta) {
			reporte = "000660LisAuxTerceroCta";
		} else if(centroCostoTerceroReferencia && formato.equals(ReportesBean.FORMATOS.EXCEL)) {
			reporte = STRINFAUXCENTREF; // MOD JM 03/12/2024
		} else if(centroCostoTerceroReferencia && formato.equals(ReportesBean.FORMATOS.PDF)) {
			reporte = STRINFAUXCENTREF;
		} else if(cuentaCentroCostoyReferencia) {
			reporte = asignarReporteCuentaCentroReferencia();
		}
		return reporte;
	}

	public String consultaParametro(String parametro) {
		String valorParametro = "";
		try {
			valorParametro = ejbSysmanUtil.consultarParametro(compania, parametro, SessionUtil.getModulo(), new Date(),
					true);

		} catch (SystemException e) {
			Logger.getLogger(InformesAuxiliaresContabilidadControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return valorParametro;
	}

	private boolean validarVaciosFechas(int opcionTerceros) {
		if (SysmanFunciones.validarVariableVacio(fechaInicial.toString())) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB58"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(fechaFinal.toString())) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB59"));
			return true;
		}

		return validarVaciosCuentas(opcionTerceros);
	}

	private boolean validarVaciosCuentas(int opcion) {
		if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB60"));
			return true;
		}

		if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB61"));
			return true;
		}
		if (opcion == 0) {
			if (validarVaciosTerceros()) {
				return true;
			}
		} else if (opcion == 1) {
			if (validarVaciosAuxiliares()) {
				return true;
			}
		} else {
			return false;
		}
		return false;
	}

	private boolean validarVaciosTerceros() {
		if (SysmanFunciones.validarVariableVacio(terceroInicial)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB62"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(terceroFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB63"));
			return true;
		}

		return false;
	}

	private boolean validarVaciosAuxiliares() {
		if (SysmanFunciones.validarVariableVacio(auxiliarInicial)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB64"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(auxiliarFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB65"));
			return true;
		}

		return false;
	}

	private boolean validarVaciosAnio(int opcionFechas, int opcionTerceros) {
		if (SysmanFunciones.validarVariableVacio(anio)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB70"));
			return true;
		}
		if (opcionFechas == 0) {
			if (validarVaciosFechas(opcionTerceros)) {
				return true;
			}
		} else {
			if (validarVaciosMeses(opcionTerceros)) {
				return true;
			}
		}
		return false;
	}

	private boolean validarVaciosCentros() {
		if (SysmanFunciones.validarVariableVacio(centroInicial)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB71"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(centroFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB72"));
			return true;
		}
		return false;
	}

	private boolean validarVaciosTipos() {
		if (SysmanFunciones.validarVariableVacio(tipoInicial)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB77"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(tipoFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB80"));
			return true;
		}
		return false;
	}

	private boolean validarVaciosMeses(int opcionTerceros) {
		if (SysmanFunciones.validarVariableVacio(mesInicial)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB82"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(mesFinal)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB83"));
			return true;
		}

		return validarVaciosCuentas(opcionTerceros);
	}

	private boolean validarVacios(String reporte) {
		switch (reporte) {
		case "000653LisAuxContTercerosEsp":
		case "000652LisAuxContTercerosCorto":
		case "000651LisAuxContTerceros":
			return validarVaciosFechas(0);
		case "000658LisAuxGralCta":
			return validarVaciosFechas(1);
		case STRINFAUXCTOCTACOS:
		case STRINFAUXCTOCTA:
		case "000654LisAuxCtaCentro":
			if (validarVaciosAnio(0, 2) || validarVaciosCentros()) {
				return true;
			}
			break;
		case "000655LisAuxCtaGral":
			return validarVaciosAnio(0, 1);
		case "000657LisAuxCtaTerceroGN":
		case "000656LisAuxCtaTercero":
		case "000660LisAuxTerceroCta":
			if (validarVaciosAnio(0, 0) || validarVaciosTipos()) {
				return true;
			}
			break;
		case STRINFCOSTOTERCERO:
			if (validarVaciosAnio(1, 0) || validarVaciosTipos() || validarVaciosCentros()) {
				return true;
			}
			break;
		case "000661SEAAUXILIARTERCEROAUXILIAR":
			if (validarVaciosAnio(0, 0) || validarVaciosAuxiliares()) {
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	public void cambiarAnioTrabajo() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	public void cambiarFechaInicial() {
		// <CODIGO_DESARROLLADO>
		anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
		cargarListaAuxiliarInicial();
		cargarListaCentroInicial();
		cargarListaCuentaInicial();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarMesInicial() {
		// <CODIGO_DESARROLLADO>
		mesFinal = "";
		cargarListaMesFinal();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarVerEspecial() {
		// <CODIGO_DESARROLLADO>
		if (especial) {
			totalPorTercero = false;
			sinValorDebVisible = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiartotalPorTercero() {
		// <CODIGO_DESARROLLADO>
		if (!cuentaCentroCostoyReferencia) {
			if (totalPorTercero) {
				sinValorDebVisible = true;
				especial = false;
			} else {
				sinValorDebVisible = false;
			}
		} else {
			sinValorDebVisible = false;
			porCuenta = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarterceros() {
		// <CODIGO_DESARROLLADO>
		if (terceros) {
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			especialVisible = true;
			consolidadoVisible = false;
			terceroVisible = true;
			fechaVisible = true;
			cuentaVisible = true;
			auxiliarVisible = false;
			mesVisible = false;
			tipoVisible = false;
			anioVisible = false;
			centroVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaTerceroInicial();
			cargarListaTerceroFinal();
			cargarListaCuentaInicial();
		} else {
			terceros = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarterceroCorto() {
		// <CODIGO_DESARROLLADO>
		if (tercerosCorto) {
			terceros = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			terceroVisible = true;
			fechaVisible = true;
			cuentaVisible = true;
			auxiliarVisible = false;
			mesVisible = false;
			tipoVisible = false;
			anioVisible = false;
			centroVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaTerceroInicial();
			cargarListaCuentaInicial();
		} else {
			tercerosCorto = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAuxGralyCuenta() {
		// <CODIGO_DESARROLLADO>
		if (auxGralCuenta) {
			terceros = false;
			tercerosCorto = false;
			centroCostoyCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = true;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			fechaVisible = true;
			cuentaVisible = true;
			auxiliarVisible = true;
			terceroVisible = false;
			mesVisible = false;
			tipoVisible = false;
			anioVisible = false;
			centroVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAuxiliarInicial();
			cargarListaCuentaInicial();
		} else {
			auxGralCuenta = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCentroCostoyCuenta() {
		// <CODIGO_DESARROLLADO>
		if (centroCostoyCuenta) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			anioVisible = true;
			centroVisible = true;
			fechaVisible = true;
			cuentaVisible = true;
			mesVisible = false;
			auxiliarVisible = false;
			tipoVisible = false;
			terceroVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAnioTrabajo();
			cargarListaCentroInicial();
			cargarListaCuentaInicial();
		} else {
			centroCostoyCuenta = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCentroCostoyTercero() {
		// <CODIGO_DESARROLLADO>
		if (centroCostoTercero) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			centroCostoTerceroAuxiliar = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			tipoVisible = true;
			anioVisible = true;
			mesVisible = true;
			cuentaVisible = true;
			centroVisible = true;
			terceroVisible = true;
			fechaVisible = false;
			auxiliarVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAnioTrabajo();
			cargarListaMesInicial();
			cargarListaTipoInicial();
			cargarListaCentroInicial();
			cargarListaTerceroInicial();
			cargarListaCuentaInicial();
		} else {
			centroCostoTercero = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCentroCostoTerceroyAuxiliar() {
		// <CODIGO_DESARROLLADO>
		if (centroCostoTerceroAuxiliar) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			centroCostoTercero = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = true;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			tipoVisible = true;
			anioVisible = true;
			mesVisible = true;
			cuentaVisible = true;
			centroVisible = true;
			terceroVisible = true;
			fechaVisible = false;
			auxiliarVisible = true;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAnioTrabajo();
			cargarListaMesInicial();
			cargarListaTipoInicial();
			cargarListaCentroInicial();
			cargarListaTerceroInicial();
			cargarListaCuentaInicial();
			cargarListaAuxiliarInicial();
			cargarListaAuxiliarFinal();
		} else {
			centroCostoTerceroAuxiliar = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCuentayAuxGral() {
		// <CODIGO_DESARROLLADO>
		if (cuentayAuxGral) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayCentroCosto = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			anioVisible = true;
			auxiliarVisible = true;
			fechaVisible = true;
			cuentaVisible = true;
			tipoVisible = false;
			mesVisible = false;
			centroVisible = false;
			terceroVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAuxiliarInicial();
			cargarListaAnioTrabajo();
			cargarListaCuentaInicial();
		} else {
			cuentayAuxGral = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCuentayCentroCosto() {
		// <CODIGO_DESARROLLADO>
		if (cuentayCentroCosto) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayAuxGral = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			anioVisible = true;
			centroVisible = true;
			fechaVisible = true;
			cuentaVisible = true;
			auxiliarVisible = false;
			tipoVisible = false;
			mesVisible = false;
			terceroVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAnioTrabajo();
			cargarListaCentroInicial();
			cargarListaCuentaInicial();
		} else {
			cuentayCentroCosto = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCuentayTercero() {
		// <CODIGO_DESARROLLADO>
		if (cuentayTercero) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = true;
			consolidadoVisible = false;
			totalTerceroVisible = true;
			sinValorDebVisible = false;
			anioVisible = true;
			terceroVisible = true;
			fechaVisible = true;
			cuentaVisible = true;
			tipoVisible = true;
			centroVisible = false;
			auxiliarVisible = false;
			mesVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAnioTrabajo();
			cargarListaTerceroInicial();
			cargarListaCuentaInicial();
			cargarListaTipoInicial();
		} else {
			cuentayTercero = true;
			especialVisible = true;
			totalTerceroVisible = true;
			sinValorDebVisible = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTerceroyAuxiliar() {
		// <CODIGO_DESARROLLADO>
		if (terceroyAuxiliar) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			cuentayTercero = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			anioVisible = true;
			terceroVisible = true;
			auxiliarVisible = true;
			fechaVisible = true;
			cuentaVisible = true;
			tipoVisible = false;
			centroVisible = false;
			mesVisible = false;
			consolidado = false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAnioTrabajo();
			cargarListaTerceroInicial();
			cargarListaAuxiliarInicial();
			cargarListaCuentaInicial();
		} else {
			terceroyAuxiliar = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTerceroyCuenta() {
		// <CODIGO_DESARROLLADO>
		if (terceroyCuenta) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			centroCostoTercero = false;
			centroCostoTerceroAuxiliar = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			anioVisible = true;
			terceroVisible = true;
			fechaVisible = true;
			cuentaVisible = true;
			auxiliarVisible = false;
			tipoVisible = true;
			centroVisible = false;
			mesVisible = false;
			consolidado = false;
			referenciaVisible= false;
			centroCostoTerceroReferencia = false;
			referenciaVisible= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAnioTrabajo();
			cargarListaTerceroInicial();
			cargarListaCuentaInicial();
			cargarListaTipoInicial();
		} else {
			terceroyCuenta = true;
		}
		// </CODIGO_DESARROLLADO>
	}
	public void cambiarCentroCostoTerceroyReferencia() {
		// <CODIGO_DESARROLLADO>
		if (centroCostoTerceroReferencia) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			centroCostoTercero = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = false;
			sinValorDebVisible = false;
			tipoVisible = true;
			anioVisible = true;
			mesVisible = true;
			cuentaVisible = true;
			centroVisible = true;
			terceroVisible = true;
			fechaVisible = false;
			auxiliarVisible = false;
			consolidado = false;
			referenciaVisible= true; // ESTE ES EL QUE CONTROLA LOS COMBOS // hya que colocarlo en todos en false OK? vale ahorita validemos que funcione con ese
			centroCostoTerceroAuxiliar= false;
			cuentaCentroCostoyReferencia = false;
			visiblePorCuenta = false;
			cargarListaAnioTrabajo();
			cargarListaMesInicial();
			cargarListaTipoInicial();
			cargarListaCentroInicial();
			cargarListaTerceroInicial();
			cargarListaCuentaInicial();
			cargarListaReferenciInicial();
		} else {
			centroCostoTerceroReferencia = true;
		}
		// </CODIGO_DESARROLLADO>
	}
	
	public void cambiarCuentaCentroCostoyReferencia() {
		// <CODIGO_DESARROLLADO>
		if (cuentaCentroCostoyReferencia) {
			terceros = false;
			tercerosCorto = false;
			auxGralCuenta = false;
			centroCostoyCuenta = false;
			cuentayAuxGral = false;
			cuentayCentroCosto = false;
			centroCostoTercero = false;
			cuentayTercero = false;
			terceroyAuxiliar = false;
			terceroyCuenta = false;
			especialVisible = false;
			consolidadoVisible = false;
			totalTerceroVisible = true;
			sinValorDebVisible = false;
			tipoVisible = true;
			anioVisible = true;
			mesVisible = false;
			cuentaVisible = true;
			centroVisible = true;
			terceroVisible = true;
			fechaVisible = true;
			auxiliarVisible = false;
			consolidado = false;
			referenciaVisible= true; // ESTE ES EL QUE CONTROLA LOS COMBOS // hya que colocarlo en todos en false OK? vale ahorita validemos que funcione con ese
			centroCostoTerceroAuxiliar= false;
			centroCostoTerceroReferencia = false;
			visiblePorCuenta = true;
			cargarListaAnioTrabajo();
			cargarListaMesInicial();
			cargarListaTipoInicial();
			cargarListaCentroInicial();
			cargarListaTerceroInicial();
			cargarListaCuentaInicial();
			cargarListaReferenciInicial();
		} else {
			cuentaCentroCostoyReferencia = true;
		}
		// </CODIGO_DESARROLLADO>
	}
	
	 /**
     * Metodo ejecutado al cambiar el control porCuenta
     * 
     * 
     */
public void cambiarporCuenta() {
         //<CODIGO_DESARROLLADO>
	if(porCuenta) {
		 totalPorTercero = false;
		}
        //</CODIGO_DESARROLLADO>
    }

	public void seleccionarFilaTipoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		cargarListaTipoFinal();
	}

	public void seleccionarFilaTipoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaCuentaFinal();
	}

	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

	public void seleccionarFilaCentroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		cargarListaCentroFinal();
	}

	public void seleccionarFilaCentroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

	public void seleccionarFilaTerceroInicial(SelectEvent event) {
		terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		Registro registroAux = (Registro) event.getObject();
		terceroInicial = (terceros || centroCostoTerceroAuxiliar 
								   || centroCostoTerceroReferencia || cuentaCentroCostoyReferencia) ? registroAux.getCampos().get("NIT").toString()
				: registroAux.getCampos().get("NOMBRE").toString();
		sucursalInicial = registroAux.getCampos().get("SUCURSAL").toString();
		cargarListaTerceroFinal();
	}

	public void seleccionarFilaTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroFinal = (terceros || centroCostoTerceroAuxiliar
								 || centroCostoTerceroReferencia || cuentaCentroCostoyReferencia) ? registroAux.getCampos().get("NIT").toString()
				: registroAux.getCampos().get("NOMBRE").toString();
		sucursalFinal = registroAux.getCampos().get("SUCURSAL").toString();
	}

	public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		cargarListaAuxiliarFinal();
	}

	public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}
	
	   /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciInicial
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaReferenciInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		refInicial = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		cargarListaReferenciFinal();
		
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferenciFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		refFinal = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	public boolean isEspecial() {
		return especial;
	}

	public void setEspecial(boolean especial) {
		this.especial = especial;
	}

	public void setConsolidado(boolean consolidado) {
		this.consolidado = consolidado;
	}

	public boolean isConsolidado() {
		return consolidado;
	}

	public boolean isTotalPorTercero() {
		return totalPorTercero;
	}

	public void setTotalPorTercero(boolean totalPorTercero) {
		this.totalPorTercero = totalPorTercero;
	}

	public boolean isSinValorDebito() {
		return sinValorDebito;
	}

	public void setSinValorDebito(boolean sinValorDebito) {
		this.sinValorDebito = sinValorDebito;
	}

	public boolean isEspecialVisible() {
		return especialVisible;
	}

	public void setEspecialVisible(boolean especialVisible) {
		this.especialVisible = especialVisible;
	}

	public boolean isConsolidadoVisible() {
		return consolidadoVisible;
	}

	public void setConsolidadoVisible(boolean consolidadoVisible) {
		this.consolidadoVisible = consolidadoVisible;
	}

	public boolean isTotalTerceroVisible() {
		return totalTerceroVisible;
	}

	public void setTotalTerceroVisible(boolean totalTerceroVisible) {
		this.totalTerceroVisible = totalTerceroVisible;
	}

	public boolean isSinValorDebVisible() {
		return sinValorDebVisible;
	}

	public void setSinValorDebVisible(boolean sinValorDebVisible) {
		this.sinValorDebVisible = sinValorDebVisible;
	}

	public boolean isTerceros() {
		return terceros;
	}

	public void setTerceros(boolean terceros) {
		this.terceros = terceros;
	}

	public boolean isTercerosCorto() {
		return tercerosCorto;
	}

	public void setTercerosCorto(boolean tercerosCorto) {
		this.tercerosCorto = tercerosCorto;
	}

	public boolean isAuxGralCuenta() {
		return auxGralCuenta;
	}

	public void setAuxGralCuenta(boolean auxGralCuenta) {
		this.auxGralCuenta = auxGralCuenta;
	}

	public boolean isCentroCostoyCuenta() {
		return centroCostoyCuenta;
	}

	public void setCentroCostoyCuenta(boolean centroCostoyCuenta) {
		this.centroCostoyCuenta = centroCostoyCuenta;
	}

	public boolean isCentroCostoTercero() {
		return centroCostoTercero;
	}

	public void setCentroCostoTercero(boolean centroCostoTercero) {
		this.centroCostoTercero = centroCostoTercero;
	}

	public boolean isCentroCostoTerceroAuxiliar() {
		return centroCostoTerceroAuxiliar;
	}

	public void setCentroCostoTerceroAuxiliar(boolean centroCostoTerceroAuxiliar) {
		this.centroCostoTerceroAuxiliar = centroCostoTerceroAuxiliar;
	}

	public boolean isCuentayAuxGral() {
		return cuentayAuxGral;
	}

	public void setCuentayAuxGral(boolean cuentayAuxGral) {
		this.cuentayAuxGral = cuentayAuxGral;
	}

	public boolean isCuentayCentroCosto() {
		return cuentayCentroCosto;
	}

	public void setCuentayCentroCosto(boolean cuentayCentroCosto) {
		this.cuentayCentroCosto = cuentayCentroCosto;
	}

	public boolean isCuentayTercero() {
		return cuentayTercero;
	}

	public void setCuentayTercero(boolean cuentayTercero) {
		this.cuentayTercero = cuentayTercero;
	}

	public boolean isTerceroyAuxiliar() {
		return terceroyAuxiliar;
	}

	public void setTerceroyAuxiliar(boolean terceroyAuxiliar) {
		this.terceroyAuxiliar = terceroyAuxiliar;
	}

	public boolean isTerceroyCuenta() {
		return terceroyCuenta;
	}

	public void setTerceroyCuenta(boolean terceroyCuenta) {
		this.terceroyCuenta = terceroyCuenta;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getTipoInicial() {
		return tipoInicial;
	}

	public void setTipoInicial(String tipoInicial) {
		this.tipoInicial = tipoInicial;
	}

	public String getTipoFinal() {
		return tipoFinal;
	}

	public void setTipoFinal(String tipoFinal) {
		this.tipoFinal = tipoFinal;
	}

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

	public String getCentroInicial() {
		return centroInicial;
	}

	public void setCentroInicial(String centroInicial) {
		this.centroInicial = centroInicial;
	}

	public String getCentroFinal() {
		return centroFinal;
	}

	public void setCentroFinal(String centroFinal) {
		this.centroFinal = centroFinal;
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

	public String getAuxiliarInicial() {
		return auxiliarInicial;
	}

	public void setAuxiliarInicial(String auxiliarInicial) {
		this.auxiliarInicial = auxiliarInicial;
	}

	public String getAuxiliarFinal() {
		return auxiliarFinal;
	}

	public void setAuxiliarFinal(String auxiliarFinal) {
		this.auxiliarFinal = auxiliarFinal;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	public String getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
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

	public List<Registro> getListaAnioTrabajo() {
		return listaAnioTrabajo;
	}

	public void setListaAnioTrabajo(List<Registro> listaAnioTrabajo) {
		this.listaAnioTrabajo = listaAnioTrabajo;
	}

	public List<Registro> getListaMesInicial() {
		return listaMesInicial;
	}

	public void setListaMesInicial(List<Registro> listaMesInicial) {
		this.listaMesInicial = listaMesInicial;
	}

	public List<Registro> getListaMesFinal() {
		return listaMesFinal;
	}

	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}

	public RegistroDataModelImpl getListaTipoInicial() {
		return listaTipoInicial;
	}

	public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
		this.listaTipoInicial = listaTipoInicial;
	}

	public RegistroDataModelImpl getListaTipoFinal() {
		return listaTipoFinal;
	}

	public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
		this.listaTipoFinal = listaTipoFinal;
	}

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

	public RegistroDataModelImpl getListaCentroInicial() {
		return listaCentroInicial;
	}

	public void setListaCentroInicial(RegistroDataModelImpl listaCentroInicial) {
		this.listaCentroInicial = listaCentroInicial;
	}

	public RegistroDataModelImpl getListaCentroFinal() {
		return listaCentroFinal;
	}

	public void setListaCentroFinal(RegistroDataModelImpl listaCentroFinal) {
		this.listaCentroFinal = listaCentroFinal;
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

	public RegistroDataModelImpl getListaAuxiliarInicial() {
		return listaAuxiliarInicial;
	}

	public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) {
		this.listaAuxiliarInicial = listaAuxiliarInicial;
	}

	public RegistroDataModelImpl getListaAuxiliarFinal() {
		return listaAuxiliarFinal;
	}

	public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) {
		this.listaAuxiliarFinal = listaAuxiliarFinal;
	}

	public boolean isAnioVisible() {
		return anioVisible;
	}

	public void setAnioVisible(boolean anioVisible) {
		this.anioVisible = anioVisible;
	}

	public boolean isMesVisible() {
		return mesVisible;
	}

	public void setMesVisible(boolean mesVisible) {
		this.mesVisible = mesVisible;
	}

	public boolean isFechaVisible() {
		return fechaVisible;
	}

	public void setFechaVisible(boolean fechaVisible) {
		this.fechaVisible = fechaVisible;
	}

	public boolean isCuentaVisible() {
		return cuentaVisible;
	}

	public void setCuentaVisible(boolean cuentaVisible) {
		this.cuentaVisible = cuentaVisible;
	}

	public boolean isTipoVisible() {
		return tipoVisible;
	}

	public void setTipoVisible(boolean tipoVisible) {
		this.tipoVisible = tipoVisible;
	}

	public boolean isCentroVisible() {
		return centroVisible;
	}

	public void setCentroVisible(boolean centroVisible) {
		this.centroVisible = centroVisible;
	}

	public boolean isTerceroVisible() {
		return terceroVisible;
	}

	public void setTerceroVisible(boolean terceroVisible) {
		this.terceroVisible = terceroVisible;
	}

	public boolean isAuxiliarVisible() {
		return auxiliarVisible;
	}

	public void setAuxiliarVisible(boolean auxiliarVisible) {
		this.auxiliarVisible = auxiliarVisible;
	}

	public boolean isContabilidadVisible() {
		return contabilidadVisible;
	}

	public void setContabilidadVisible(boolean contabilidadVisible) {
		this.contabilidadVisible = contabilidadVisible;
	}

	public String getSucursalInicial() {
		return sucursalInicial;
	}

	public void setSucursalInicial(String sucursalInicial) {
		this.sucursalInicial = sucursalInicial;
	}

	public String getSucursalFinal() {
		return sucursalFinal;
	}

	public void setSucursalFinal(String sucursalFinal) {
		this.sucursalFinal = sucursalFinal;
	}

	public boolean isReferenciaVisible() {
		return referenciaVisible;
	}

	public void setReferenciaVisible(boolean referenciaVisible) {
		this.referenciaVisible = referenciaVisible;
	}

	public boolean isCentroCostoTerceroReferencia() {
		return centroCostoTerceroReferencia;
	}

	public void setCentroCostoTerceroReferencia(boolean centroCostoTerceroReferencia) {
		this.centroCostoTerceroReferencia = centroCostoTerceroReferencia;
	}

	public String getRefInicial() {
		return refInicial;
	}

	public void setRefInicial(String refInicial) {
		this.refInicial = refInicial;
	}

	public String getRefFinal() {
		return refFinal;
	}

	public void setRefFinal(String refFinal) {
		this.refFinal = refFinal;
	}

	public RegistroDataModelImpl getListaReferenciInicial() {
		return listaReferenciInicial;
	}

	public void setListaReferenciInicial(RegistroDataModelImpl listaReferenciInicial) {
		this.listaReferenciInicial = listaReferenciInicial;
	}

	public RegistroDataModelImpl getListaReferenciFinal() {
		return listaReferenciFinal;
	}

	public void setListaReferenciFinal(RegistroDataModelImpl listaReferenciFinal) {
		this.listaReferenciFinal = listaReferenciFinal;
	}
	
	public boolean isCuentaCentroCostoyReferencia() {
		return cuentaCentroCostoyReferencia;
	}

	public void setCuentaCentroCostoyReferencia(boolean cuentaCentroCostoyReferencia) {
		this.cuentaCentroCostoyReferencia = cuentaCentroCostoyReferencia;
	}

	public boolean isPorCuenta() {
		return porCuenta;
	}

	public void setPorCuenta(boolean porCuenta) {
		this.porCuenta = porCuenta;
	}

	public boolean isVisiblePorCuenta() {
		return visiblePorCuenta;
	}

	public void setVisiblePorCuenta(boolean visiblePorCuenta) {
		this.visiblePorCuenta = visiblePorCuenta;
	}

}
