package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceComparativoControladorEnum;
import com.sysman.contabilidad.enums.BalanceComparativoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 20/04/2016
 *
 * @version 1.1, 19/10/2016 - Modificado por: Pablo Espitia. Se cambio el valor
 *          de reemplazo de <auxiliar> y <tercero>.
 *
 * @version 1.2, Nov 2016 - Modificado por: sdaza. Se modifica las consultas de
 *          los informes con el fin de unificar con la consulta base 800046 Se
 *          usaban tres informes 642, 643 y 646. Se unifican al 646 debido a que
 *          la estructura es la misma, se controlan las condiciones con las
 *          variables de reemplazo.
 * 
 * @version 2, 06/04/2017 - Modificado por: jreina. Se modificaron la consulta
 *          de los combos segun el proceso de refactoring
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n de
 *          ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class BalanceComparativoControlador extends BeanBaseModal {
	private final String compania;
	private final String modulo;
	private final String consPrMesTrabajo;
	private final String consPrMesComparar;
	private final String consPrFirma1;
	private final String consPrFirma2;
	private final String consPrDocumento1;
	private final String consPrDocumento2;
	private final String consPrCargo1;
	private final String consPrCargo2;
	private final String consPrAnioTrabajo;
	private final String consPrAnioComparar;
	private final String consCodigo;
	private final String consFirmaContable1;
	private final String consFirmaContable2;
	private final String consFirmaContable3;
	private final String consDocumentoContable1;
	private final String consDocumentoContable2;
	private final String consDocumentoContable3;
	private final String consCargoContable1;
	private final String consCargoContable2;
	private final String consCargoContable3;
	private boolean porCC;
	private boolean porTercero;
	private boolean porAuxiliarGeneral;
	private boolean porReferencia;
	private boolean porFteRecurso;
	private boolean porSaldoCero;
	private boolean porMeses;
	private boolean conParalelo;
	private String codigoInicial;
	private String codigoFinal;
	private String mesTrabajo;
	private String mesComparar;
	private String anioTrabajo;
	private String anioComparar;
	private String digitos;
	private StreamedContent archivoDescarga;
	private List<Registro> listaAnoTrabajo;
	private List<Registro> listaAnoComparar;
	private RegistroDataModelImpl listaCodigoInicial;
	private RegistroDataModelImpl listaCodigoFinal;
	private final String consPrFirmaContable1;
	private final String consPrFirmaContable2;
	private final String consPrFirmaContable3;
	private final String consPrCargoContable1;
	private final String consPrCargoContable2;
	private final String consPrCargoContable3;
	private final String consPrDocumentoContable1;
	private final String consPrDocumentoContable2;
	private final String consPrDocumentoContable3;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtilRemote;

	/**
	 * Creates a new instance of BalanceComparativoControlador Creates a new
	 * instance of BalanceComparativoControlador Creates a new instance of
	 * BalanceComparativoControlador
	 */
	public BalanceComparativoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		consPrMesTrabajo = "PR_MESTRABAJO";
		consPrMesComparar = "PR_MESCOMPARAR";
		consPrFirma1 = "PR_FIRMA1";
		consPrFirma2 = "PR_FIRMA2";
		consPrDocumento1 = "PR_DOCUMENTO1";
		consPrDocumento2 = "PR_DOCUMENTO2";
		consPrCargo1 = "PR_CARGO1";
		consPrCargo2 = "PR_CARGO2";
		consPrFirmaContable1 = "PR_FIRMA_CONTABLE_1";
		consPrFirmaContable2 = "PR_FIRMA_CONTABLE_2";
		consPrFirmaContable3 = "PR_FIRMA_CONTABLE_3";
		consPrCargoContable1 = "PR_CARGO_CONTABLE_1";
		consPrCargoContable2 = "PR_CARGO_CONTABLE_2";
		consPrCargoContable3 = "PR_CARGO_CONTABLE_3";
		consPrDocumentoContable1 = "PR_DOCUMENTO_CONTABLE_1";
		consPrDocumentoContable2 = "PR_DOCUMENTO_CONTABLE_2";
		consPrDocumentoContable3 = "PR_DOCUMENTO_CONTABLE_3";

		consPrAnioTrabajo = "PR_ANOTRABAJO";
		consPrAnioComparar = "PR_ANOCOMPARAR";
		consCodigo = "CODIGO";
		consFirmaContable1 = "FIRMA CONTABLE 1";
		consFirmaContable2 = "FIRMA CONTABLE 2";
		consFirmaContable3 = "FIRMA CONTABLE 3";
		consDocumentoContable1 = "DOCUMENTO CONTABLE 1";
		consDocumentoContable2 = "DOCUMENTO CONTABLE 2";
		consDocumentoContable3 = "DOCUMENTO CONTABLE 3";
		consCargoContable1 = "CARGO CONTABLE 1";
		consCargoContable2 = "CARGO CONTABLE 2";
		consCargoContable3 = "CARGO CONTABLE 3";

		codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		try {
			numFormulario = GeneralCodigoFormaEnum.BALANCE_COMPARATIVO_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(BalanceComparativoControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		Date fechaActual = new Date();
		anioTrabajo = String.valueOf(SysmanFunciones.getParteFecha(fechaActual, Calendar.YEAR));
		mesTrabajo = String.valueOf(SysmanFunciones.getParteFecha(fechaActual, Calendar.MONTH) + 1);
		anioComparar = String.valueOf(SysmanFunciones.getParteFecha(fechaActual, Calendar.YEAR));
		mesComparar = String.valueOf(SysmanFunciones.getParteFecha(fechaActual, Calendar.MONTH) + 1);

		digitos = "6";

		cargarListaAnoTrabajo();
		cargarListaAnoComparar();
		cargarListaCodigoInicial();
		cargarListaCodigoFinal();

		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		// METODO NO IMPLEMENTADO
	}

	public void cargarListaAnoTrabajo() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			listaAnoTrabajo = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													BalanceComparativoControladorUrlEnum.URL4579.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaAnoComparar() {
		listaAnoComparar = listaAnoTrabajo;
	}

	public void cargarListaCodigoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(BalanceComparativoControladorUrlEnum.URL5464.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(BalanceComparativoControladorEnum.PARAM0.getValue(), anioTrabajo);
		param.put(BalanceComparativoControladorEnum.PARAM1.getValue(), anioComparar);

		listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				consCodigo);
	}

	public void cargarListaCodigoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(BalanceComparativoControladorUrlEnum.URL6237.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(BalanceComparativoControladorEnum.PARAM0.getValue(), anioTrabajo);
		param.put(BalanceComparativoControladorEnum.PARAM1.getValue(), anioComparar);
		param.put(BalanceComparativoControladorEnum.PARAM2.getValue(), codigoInicial);

		listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				consCodigo);

	}

	public void oprimirpdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirexcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAnoTrabajo() {
		// <CODIGO_DESARROLLADO>
		cargarListaCodigoInicial();
		cargarListaCodigoFinal();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAnoComparar() {
		// <CODIGO_DESARROLLADO>
		cargarListaCodigoInicial();
		cargarListaCodigoFinal();
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaCodigoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(consCodigo), " ").toString();
		codigoFinal = codigoInicial;
		cargarListaCodigoFinal();
	}

	public void seleccionarFilaCodigoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(consCodigo), " ").toString();
	}

	private void generarInforme(FORMATOS formato) {
		boolean estado = true;
		if ((anioTrabajo == null) || "".equals(anioTrabajo)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB88"));
			estado = false;
		} else if ((mesTrabajo == null) || "".equals(mesTrabajo)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB89"));
			estado = false;
		} else if ((anioComparar == null) || "".equals(anioComparar)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB90"));
			estado = false;
		} else if ((mesComparar == null) || "".equals(mesComparar)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB91"));
			estado = false;
		} else if ((digitos == null) || "".equals(digitos)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB92"));
			estado = false;
		} else if ((codigoInicial == null) || "".equals(codigoInicial)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB93"));
			estado = false;
		} else if ((codigoFinal == null) || "".equals(codigoFinal)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB94"));
			estado = false;
		}

		try {
			if (estado) {
				Map<String, Object> parametros = new HashMap<>();
				HashMap<String, Object> reemplazar = new HashMap<>();
				String nombreReporte = "000646BalanceComparativo";
				reemplazar.put("codigoInicial", SysmanFunciones.concatenar("'", codigoInicial, "'"));
				reemplazar.put("codigoFinal", SysmanFunciones.concatenar("'", codigoFinal, "'"));
				reemplazar.put("digitos", digitos);
				reemplazar.put("manTer", porTercero ? "1" : "0");
				reemplazar.put("manAux", porAuxiliarGeneral ? "1" : "0");
				reemplazar.put("manCen", porCC ? "1" : "0");
				reemplazar.put("manParalelo", conParalelo ? "1" : "0");
				reemplazar.put("manRef", porReferencia ? "1" : "0");
				reemplazar.put("manFue", porFteRecurso ? "1" : "0");
				reemplazar.put("anio", anioTrabajo);
				reemplazar.put("mestrabajo", mesTrabajo);
				reemplazar.put("mesComparar", mesComparar);
				reemplazar.put("saldoCero", porSaldoCero ? "" : "HAVING (SUM(SANTERIOR)<>0 OR SUM(NUEVO)<>0) ");
				reemplazar.put("baseBalance", Reporteador.resuelveConsulta("800538BaseBalancesComparativo",
						Integer.parseInt(SessionUtil.getModulo()), reemplazar));
				reemplazar.put("anio", porMeses ? anioTrabajo : anioComparar);

				reemplazar.put("baseBalanceUnion", Reporteador.resuelveConsulta("800538BaseBalancesComparativo",
						Integer.parseInt(SessionUtil.getModulo()), reemplazar));
				reemplazar.put("condicionCC", "");

				try {
					if (porMeses) {
						// balance comparativo entre meses

						parametros.put(consPrDocumento2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consDocumentoContable2, modulo, new Date(), false), ""));

						parametros.put(consPrMesTrabajo,
								SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesTrabajo)].toUpperCase());
						parametros.put(consPrFirma2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consFirmaContable2, modulo, new Date(), false), ""));
						parametros.put(consPrDocumento1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consDocumentoContable1, modulo, new Date(), false), ""));
						parametros.put(consPrCargo1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consCargoContable1, modulo, new Date(), false), ""));
						parametros.put(consPrCargo2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consCargoContable2, modulo, new Date(), false), ""));
						parametros.put(consPrFirma1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consFirmaContable1, modulo, new Date(), false), ""));
						parametros.put(consPrAnioComparar, anioTrabajo);
						parametros.put(consPrMesComparar,
								SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesComparar)]
										.toUpperCase());
						parametros.put(consPrAnioTrabajo, anioTrabajo);

					} else if (porCC) {
						// balance comparativo cc

						parametros.put(consPrDocumento2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consDocumentoContable2, modulo, new Date(), false), ""));

						parametros.put(consPrCargo2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consCargoContable2, modulo, new Date(), false), ""));
						parametros.put(consPrFirma2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consFirmaContable2, modulo, new Date(), false), ""));
						parametros.put(consPrDocumento1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consDocumentoContable1, modulo, new Date(), false), ""));
						parametros.put(consPrCargo1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consCargoContable1, modulo, new Date(), false), ""));
						parametros.put(consPrFirma1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consFirmaContable1, modulo, new Date(), false), ""));
						parametros.put(consPrAnioTrabajo, anioTrabajo);
						parametros.put(consPrAnioComparar, anioComparar);
						parametros.put(consPrMesComparar,
								SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesComparar)]
										.toUpperCase());
						parametros.put(consPrMesTrabajo,
								SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesTrabajo)].toUpperCase());

					} else {
						// balance comparativo

						parametros.put(consPrDocumento2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consDocumentoContable2, modulo, new Date(), false), ""));

						parametros.put(consPrCargo2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consCargoContable2, modulo, new Date(), false), ""));
						parametros.put(consPrFirma2, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consFirmaContable2, modulo, new Date(), false), ""));
						parametros.put(consPrDocumento1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consDocumentoContable1, modulo, new Date(), false), ""));
						parametros.put(consPrCargo1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consCargoContable1, modulo, new Date(), false), ""));
						parametros.put(consPrFirma1, SysmanFunciones.nvl(ejbSysmanUtilRemote
								.consultarParametro(compania, consFirmaContable1, modulo, new Date(), false), ""));
						parametros.put(consPrMesTrabajo,
								SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesTrabajo)].toUpperCase());
						parametros.put(consPrAnioComparar, anioComparar);
						parametros.put(consPrMesComparar,
								SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesComparar)]
										.toUpperCase());
						parametros.put(consPrAnioTrabajo, anioTrabajo);

					}
				} catch (SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
				Reporteador.resuelveConsulta(nombreReporte, Integer.parseInt(modulo), reemplazar, parametros);
				archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
						formato);

			}
			if (conParalelo) {
				try {
					Map<String, Object> parametros = new HashMap<>();
					HashMap<String, Object> reemplazar = new HashMap<>();
					String nombreReporte = "002068BalanceGeneralMEParalelo";
					reemplazar.put("codigoInicial", SysmanFunciones.concatenar("'", codigoInicial, "'"));
					reemplazar.put("codigoFinal", SysmanFunciones.concatenar("'", codigoFinal, "'"));
					reemplazar.put("digitos", digitos);
					reemplazar.put("manTer", porTercero ? "1" : "0");
					reemplazar.put("manAux", porAuxiliarGeneral ? "1" : "0");
					reemplazar.put("manCen", porCC ? "1" : "0");
					reemplazar.put("manParalelo", conParalelo ? "1" : "0");
					reemplazar.put("manRef", porReferencia ? "1" : "0");
					reemplazar.put("manFue", porFteRecurso ? "1" : "0");
					reemplazar.put("anio", anioTrabajo);
					reemplazar.put("mestrabajo", mesTrabajo);
					reemplazar.put("mesComparar", mesComparar);
					reemplazar.put("saldoCero", porSaldoCero ? "" : "HAVING (SUM(SANTERIOR)<>0 OR SUM(NUEVO)<>0) ");
					reemplazar.put("baseBalance", Reporteador.resuelveConsulta("800538BaseBalancesComparativo",
							Integer.parseInt(SessionUtil.getModulo()), reemplazar));
					reemplazar.put("anio", porMeses ? anioTrabajo : anioComparar);

					reemplazar.put("baseBalanceUnion", Reporteador.resuelveConsulta("800538BaseBalancesComparativo",
							Integer.parseInt(SessionUtil.getModulo()), reemplazar));
					reemplazar.put("condicionCC", "");
					
					parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

					parametros.put(consPrAnioComparar, anioComparar);
					parametros.put(consPrMesComparar,
							SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesComparar)].toUpperCase());
					parametros.put(consPrAnioTrabajo, anioTrabajo);
					
					parametros.put(consPrDocumentoContable3, SysmanFunciones.nvl(ejbSysmanUtilRemote
							.consultarParametro(compania, consDocumentoContable3, modulo, new Date(), false), ""));
					
					parametros.put(consPrCargoContable3, SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
							consCargoContable3, modulo, new Date(), false), ""));
					
					parametros.put(consPrFirmaContable3, SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
							consFirmaContable3, modulo, new Date(), false), ""));
					
					parametros.put(consPrDocumentoContable2, SysmanFunciones.nvl(ejbSysmanUtilRemote
							.consultarParametro(compania, consDocumentoContable2, modulo, new Date(), false), ""));
					
					parametros.put(consPrCargoContable2, SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
							consCargoContable2, modulo, new Date(), false), ""));
					
					parametros.put(consPrFirmaContable2, SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
							consFirmaContable2, modulo, new Date(), false), ""));
					
					parametros.put(consPrDocumentoContable1, SysmanFunciones.nvl(ejbSysmanUtilRemote
							.consultarParametro(compania, consDocumentoContable1, modulo, new Date(), false), ""));
					
					parametros.put(consPrCargoContable1, SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
							consCargoContable1, modulo, new Date(), false), ""));
					
					parametros.put(consPrFirmaContable1, SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania,
							consFirmaContable1, modulo, new Date(), false), ""));

					Reporteador.resuelveConsulta(nombreReporte, Integer.parseInt(modulo), reemplazar, parametros);
					archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}

			}

		} catch (JRException | IOException | SysmanException e) {
			Logger.getLogger(BalanceComparativoControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cambiarEntremeses() {
		if (porMeses && porCC) {
			porCC = false;
		}
	}

	public String getCodigoInicial() {
		return codigoInicial;
	}

	public void setCodigoInicial(String codigoInicial) {
		this.codigoInicial = codigoInicial;
	}

	public String getCodigoFinal() {
		return codigoFinal;
	}

	public void setCodigoFinal(String codigoFinal) {
		this.codigoFinal = codigoFinal;
	}

	public String getMesTrabajo() {
		return mesTrabajo;
	}

	public void setMesTrabajo(String mesTrabajo) {
		this.mesTrabajo = mesTrabajo;
	}

	public String getMesComparar() {
		return mesComparar;
	}

	public void setMesComparar(String mesComparar) {
		this.mesComparar = mesComparar;
	}

	public String getAnioTrabajo() {
		return anioTrabajo;
	}

	public void setAnioTrabajo(String anioTrabajo) {
		this.anioTrabajo = anioTrabajo;
	}

	public String getAnioComparar() {
		return anioComparar;
	}

	public void setAnioComparar(String anioComparar) {
		this.anioComparar = anioComparar;
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

	public List<Registro> getListaAnoTrabajo() {
		return listaAnoTrabajo;
	}

	public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
		this.listaAnoTrabajo = listaAnoTrabajo;
	}

	public List<Registro> getListaAnoComparar() {
		return listaAnoComparar;
	}

	public void setListaAnoComparar(List<Registro> listaAnoComparar) {
		this.listaAnoComparar = listaAnoComparar;
	}

	public RegistroDataModelImpl getListaCodigoInicial() {
		return listaCodigoInicial;
	}

	public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial) {
		this.listaCodigoInicial = listaCodigoInicial;
	}

	public RegistroDataModelImpl getListaCodigoFinal() {
		return listaCodigoFinal;
	}

	public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
		this.listaCodigoFinal = listaCodigoFinal;
	}

	public boolean isPorCC() {
		return porCC;
	}

	public void setPorCC(boolean porCC) {
		this.porCC = porCC;
	}

	public boolean isPorTercero() {
		return porTercero;
	}

	public void setPorTercero(boolean porTercero) {
		this.porTercero = porTercero;
	}

	public boolean isPorReferencia() {
		return porReferencia;
	}

	public void setPorReferencia(boolean porReferencia) {
		this.porReferencia = porReferencia;
	}

	public boolean isPorFteRecurso() {
		return porFteRecurso;
	}

	public void setPorFteRecurso(boolean porFteRecurso) {
		this.porFteRecurso = porFteRecurso;
	}

	public boolean isPorSaldoCero() {
		return porSaldoCero;
	}

	public void setPorSaldoCero(boolean porSaldoCero) {
		this.porSaldoCero = porSaldoCero;
	}

	public boolean isPorMeses() {
		return porMeses;
	}

	public void setPorMeses(boolean porMeses) {
		this.porMeses = porMeses;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public boolean isPorAuxiliarGeneral() {
		return porAuxiliarGeneral;
	}

	public void setPorAuxiliarGeneral(boolean porAuxiliarGeneral) {
		this.porAuxiliarGeneral = porAuxiliarGeneral;
	}

	/**
	 * @return the conParalelo
	 */
	public boolean isConParalelo() {
		return conParalelo;
	}

	/**
	 * @param conParalelo the conParalelo to set
	 */
	public void setConParalelo(boolean conParalelo) {
		this.conParalelo = conParalelo;
	}

}
