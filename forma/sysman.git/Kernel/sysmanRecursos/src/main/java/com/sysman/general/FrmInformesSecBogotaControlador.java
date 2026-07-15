/*-
 * FrmInformesSecBogotaControlador.java
 *
 * 1.0
 * 
 * 02/10/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteGeneralRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmInformesSecBogotaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera planos de la secretario de hacienda
 *
 * @version 1.0, 02/10/2020
 * @author eorozco
 * 
 * @author gfigueredo
 * @version 2.0 23/08/2021
 * Se modifica la función {@link #generarPlanoH}, para pasar los datos de
 * cheque inicial, cheque final, comprobante inicial y comprobante final.
 * 
 * Se crean los servicios 72105, 72107, 72109, 72111.
 * 
 */
@ManagedBean
@ViewScoped
public class FrmInformesSecBogotaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */

	public static final String CHEQUEI = "CHEQUE_I";
	private static final String CHEQUEF = "CHEQUE_F";
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	private final String modulo;

	private boolean ckHacienda2018;

	private boolean ckRegistrosAdicionales;

	private boolean ckBogData;

	private boolean ckInfDisponibilidad;

	private boolean ckCompromiso;

	private boolean ckOrdenPago;

	private boolean ckOrdenPagoReserva;

	private boolean ckBogDataNomina;

	private String fuenteInicial;

	private String fuenteFinal;

	private Date fechaInicial;

	private Date fechaFinal;

	private boolean presupuesto;

	private boolean contabilidad;

	private String reporte;

	private String cptInicial;

	private String cptFinal;
	@EJB
	private EjbPresupuestoTresGeneralRemote ejbPresupuestoTres;
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtilRemote;
	@EJB
	private EjbContabilidadSieteGeneralRemote ejbSysmanContabilidadSiete;

	private String responsableppto;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista que carga las fuentes iniciales
	 */
	private RegistroDataModelImpl listaFuenteInicial;
	/**
	 * Lista que carga las fuentes finales
	 */
	private RegistroDataModelImpl listaFuenteFinal;
	private String nombreArchivo;
	private boolean ocultarTxt;
	private boolean ocultarExcel;
	private String chequeInicial;
	private String chequeFinal;
	private String comprobanteInicial;
	private String comprobanteFinal;

	/**
	 * Lista que carga las chques iniciales
	 */
	private RegistroDataModelImpl listaChequeInicial;

	/**
	 * Lista que carga las chque finales
	 */
	private RegistroDataModelImpl listaChequeFinal;

	/**
	 * Lista que carga las compronate iniciales
	 */
	private RegistroDataModelImpl listaComprobanteInicial;

	/**
	 * Lista que carga las comprobante finales
	 */
	private RegistroDataModelImpl listaComprobanteFinal;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmInformesSecBogotaControlador
	 */
	public FrmInformesSecBogotaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
//		fechaInicial = new Date();
//		fechaFinal = new Date();

		try {
			// 2178
			numFormulario = GeneralCodigoFormaEnum.FRM_INFORMES_SEC_BOGOTA_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaFuenteInicial();
		cargarListaFuenteFinal();
		cargarListaChequeInicial();
		cargarListaChequeFinal();
		cargarListaComprobanteInicial();
		cargarListaComprobanteFinal();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
		
		try {
            setFechaInicial(SysmanFunciones.primeroDeMesFecha(new Date()));
            setFechaFinal(SysmanFunciones.ultimoDiaDate(new Date()));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            
        }
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		if (SessionUtil.getMenuActual().equals("2031506")) {
			presupuesto = false;
			contabilidad = true;
			ckHacienda2018 = true;
			ocultarExc();
			ocultarTxt();
		} else {
			presupuesto = true;
			contabilidad = false;
			ckInfDisponibilidad = true;
			ocultarExc();
			ocultarTxt();
		}

		try {
			responsableppto = ejbSysmanUtilRemote.consultarParametro(compania, "DOCUMENTO_RESPONSABLE_PRESUPUESTO",
					modulo, new Date(), false);
		} catch (SystemException e) {

			e.printStackTrace();
		}

		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaFuenteInicial
	 *
	 */
	public void cargarListaFuenteInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmInformesSecBogotaControladorUrlEnum.URL0001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), SysmanFunciones.ano(fechaInicial));

		listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaFuenteFinal
	 *
	 */
	public void cargarListaFuenteFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmInformesSecBogotaControladorUrlEnum.URL0002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), SysmanFunciones.ano(fechaInicial));
		param.put("CODIGOINICIAL", fuenteInicial);

		listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaChequeInicial
	 *
	 */
	public void cargarListaChequeInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmInformesSecBogotaControladorUrlEnum.URL0003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		//param.put(GeneralParameterEnum.ANIO.getName(), SysmanFunciones.ano(fechaInicial));
		try {
			param.put("FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			param.put("FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		listaChequeInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NRO_DOCUMENTO.getName());
	}

	/**
	 * 
	 * Carga la lista listaChequeFinal
	 *
	 */
	public void cargarListaChequeFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmInformesSecBogotaControladorUrlEnum.URL0004.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		//param.put(GeneralParameterEnum.ANIO.getName(), SysmanFunciones.ano(fechaInicial));
		try {
			param.put("FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			param.put("FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		param.put(CHEQUEI, chequeInicial);

		listaChequeFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NRO_DOCUMENTO.getName());
	}

	/**
	 * 
	 * Carga la lista listaComrobanteIncial
	 *
	 */
	public void cargarListaComprobanteInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmInformesSecBogotaControladorUrlEnum.URL0005.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		//param.put(GeneralParameterEnum.ANIO.getName(), SysmanFunciones.ano(fechaInicial));
		try {
			param.put("FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			param.put("FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		param.put(CHEQUEI, chequeInicial);
		param.put(CHEQUEF, chequeFinal);

		listaComprobanteInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}

	/**
	 * 
	 * Carga la lista listaComprobanteFinal
	 *
	 */
	public void cargarListaComprobanteFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmInformesSecBogotaControladorUrlEnum.URL0006.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		//param.put(GeneralParameterEnum.ANIO.getName(), SysmanFunciones.ano(fechaInicial));
		try {
			param.put("FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			param.put("FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		param.put("COMPINICIAL", comprobanteInicial);
		param.put(CHEQUEI, chequeInicial);
		param.put(CHEQUEF, chequeFinal);

		listaComprobanteFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista
	 *
	 *
	 */
	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte();

		// </CODIGO_DESARROLLADO>
	}

	private void generarReporte() {

		String tipocomp;

		try {

			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("fechaInicial", fechaInicial);
			reemplazar.put("fechaFinal", fechaFinal);
			reemplazar.put("cptInicial", cptInicial);
			reemplazar.put("cptFinal", cptFinal);
			// MANEJO DE PARAMETROS DEL REPORTE

			// Modulo Contabilidad
			if ("2031506".equals(SessionUtil.getMenuActual())) {

				if (ckHacienda2018) {

					reporte = ejbSysmanContabilidadSiete.generarPlanoH2018(compania,
							SysmanFunciones.convertirAFechaCadena(fechaInicial),
							SysmanFunciones.convertirAFechaCadena(fechaFinal), fuenteInicial, fuenteFinal);

					nombreArchivo = "INFORMESECBOGOTA_Hacienda2018.txt";

				} else if (ckRegistrosAdicionales) {

					reporte = ejbSysmanContabilidadSiete.generarPlanoHAdi(compania, fechaInicial, fechaFinal,
							fuenteInicial, fuenteFinal);

					nombreArchivo = "INFORMESECBOGOTA_RegAdicionales.txt";

				} else if (ckBogData) {

					reporte = ejbSysmanContabilidadSiete.generarPlanoH(compania,
							SysmanFunciones.convertirAFechaCadena(fechaInicial),
							SysmanFunciones.convertirAFechaCadena(fechaFinal), fuenteInicial, fuenteFinal,
							chequeInicial, chequeFinal, comprobanteInicial, comprobanteFinal);

					nombreArchivo = "INFORMESECBOGOTA_BogData.txt";
				} else if (ckBogDataNomina) {
					reporte = ejbSysmanContabilidadSiete.generarPlanoHNomina(compania,
							SysmanFunciones.convertirAFechaCadena(fechaInicial),
							SysmanFunciones.convertirAFechaCadena(fechaFinal), fuenteInicial, fuenteFinal);

					nombreArchivo = "INFORMESECBOGOTA_BogDataNomina.txt";
				}

			}
			// Modulo Presupuesto
			else {

				if (ckInfDisponibilidad) {
					tipocomp = "DIS";
					reporte = ejbPresupuestoTres.generarPlanoSecretaria(compania, fechaInicial, fechaFinal, tipocomp,
							cptInicial, cptFinal, responsableppto);
					nombreArchivo = "INFORMESECBOGOTA_Disponibilidad.txt";

				} else if (ckCompromiso) {
					tipocomp = "RES";
					reporte = ejbPresupuestoTres.generarPlanoSecretaria(compania, fechaInicial, fechaFinal, tipocomp,
							cptInicial, cptFinal, responsableppto);
					nombreArchivo = "INFORMESECBOGOTA_RegCompromiso.txt";

				}

			}

			ByteArrayInputStream texto = JsfUtil.serializarPlano(reporte);

			String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
			parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);

			archivoDescarga = JsfUtil.getArchivoDescarga(texto, nombreArchivo);
		} catch (JRException | IOException | SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// Oprimir Excel
	public void oprimirOprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporteExcel();
		// </CODIGO_DESARROLLADO>
	}

	private void generarReporteExcel() {

		String tipocomp;
		String vigencia;
		String nombreDocumento = null;
		try {


			// Modulo Presupuesto
			if ("3040216".equals(SessionUtil.getMenuActual())) {

				if (ckOrdenPago) {
					tipocomp = "REO";
					vigencia = "VA";
					reporte = ejbPresupuestoTres.generarPlanoOrdenPago(compania, fechaInicial, fechaFinal, tipocomp,
							cptInicial, cptFinal, vigencia, responsableppto);
					nombreDocumento = "INFORMESECBOGOTA_OrdenPagoVigenciaActual";
				} else if (ckOrdenPagoReserva) {
					
					tipocomp = ejbSysmanUtilRemote.consultarParametro(compania, "TIPO DE COMPROBANTE PARA RESERVA DE CAJA",
							modulo, new Date(), false);
					vigencia = ejbSysmanUtilRemote.consultarParametro(compania, "TIPO DE VIGENCIA PARA RESERVA DE CAJA",
							modulo, new Date(), false);
					reporte = ejbPresupuestoTres.generarPlanoOrdenPagoReserva(compania, fechaInicial, fechaFinal,
							tipocomp,vigencia, cptInicial, cptFinal, responsableppto);
					nombreDocumento = "INFORMESECBOGOTA_OrdenPagoReserva";

				}

			}
			String separadorRegistros = System.getProperty("line.separator");
			String separadorColumnas = "\t";
			String nombreHoja = "Gastos";
			archivoDescarga = JsfUtil.armarExcel(reporte, separadorRegistros, separadorColumnas, nombreHoja,
					nombreDocumento);

		}

		catch (/* JRException | IOException | */ SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo ejecutado al cambiar el control FechaInicial
	 * 
	 * 
	 */
	public void cambiarFechaInicial() {
		cargarListaFuenteInicial();
		cargarListaChequeInicial();
	}
	
	/**
	 * Metodo ejecutado al cambiar el control FechaFinal
	 * 
	 * 
	 */
	public void cambiarFechaFinal() {
		cargarListaChequeInicial();
		cargarListaChequeFinal();
	}

	/**
	 * Metodo ejecutado al cambiar el control CkHacienda2018
	 * 
	 */
	public void cambiarCkHacienda2018() {

		ckRegistrosAdicionales = false;
		ckBogDataNomina = false;
		ckBogData = false;
		ocultarTxt();
	}

	/**
	 * Metodo ejecutado al cambiar el control CkBogData
	 * 
	 */
	public void cambiarCkBogData() {

		ckRegistrosAdicionales = false;
		ckBogDataNomina = false;
		ckHacienda2018 = false;
		ocultarTxt();
	}

	/**
	 * Metodo ejecutado al cambiar el control CkRegistrosAdicionales
	 * 
	 */
	public void cambiarCkRegistrosAdicionales() {
		ckHacienda2018 = false;
		ckBogDataNomina = false;
		ckBogData = false;
		ocultarTxt();
	}

	public void cambiarCkInfDisponibilidad() {
		// <CODIGO_DESARROLLADO>
		ckOrdenPago = false;
		ckCompromiso = false;
		ckOrdenPagoReserva = false;
		ocultarTxt();
		ocultarExc();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control CkCompromiso
	 * 
	 * 
	 */
	public void cambiarCkCompromiso() {
		// <CODIGO_DESARROLLADO>
		ckInfDisponibilidad = false;
		ckOrdenPago = false;
		ckOrdenPagoReserva = false;
		ckBogDataNomina = false;
		ocultarExc();
		ocultarTxt();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control CkOrdenPago
	 * 
	 * 
	 */
	public void cambiarCkOrdenPago() {
		// <CODIGO_DESARROLLADO>
		ckInfDisponibilidad = false;
		ckCompromiso = false;
		ckOrdenPagoReserva = false;
		ckBogDataNomina = false;
		ocultarExc();
		ocultarTxt();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control ckOrdenPagoReserva
	 * 
	 * 
	 */
	public void cambiarckOrdenPagoReserva() {
		// <CODIGO_DESARROLLADO>
		ckInfDisponibilidad = false;
		ckCompromiso = false;
		ckOrdenPago = false;
		ckBogDataNomina = false;
		ocultarExc();
		ocultarTxt();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarckBogDataNomina() {
		// <CODIGO_DESARROLLADO>
		ckInfDisponibilidad = false;
		ckCompromiso = false;
		ckOrdenPago = false;
		ckOrdenPagoReserva = false;

		ckHacienda2018 = false;
		ckRegistrosAdicionales = false;
		ckBogData = false;
		ocultarTxt();
		// </CODIGO_DESARROLLADO>
	}

	public void ocultarTxt() {

		if (ckInfDisponibilidad || ckCompromiso || ckHacienda2018 || ckRegistrosAdicionales || ckBogData
				|| ckBogDataNomina) {

			ocultarTxt = true;
		} else {
			ocultarTxt = false;
		}

	}

	public void ocultarExc() {

		if (ckOrdenPago || ckOrdenPagoReserva) {

			ocultarExcel = true;
		} else {
			ocultarExcel = false;
		}

	}

	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial = registroAux.getCampos().get("CODIGO").toString();
		fuenteFinal = null;
		cargarListaFuenteFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaChequeInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		chequeInicial = registroAux.getCampos().get("NRO_DOCUMENTO").toString();
		chequeFinal = null;
		cargarListaChequeFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaChequeFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		chequeFinal = registroAux.getCampos().get("NRO_DOCUMENTO").toString();
		cargarListaComprobanteInicial();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaComprobanteInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaComprobanteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		comprobanteInicial = registroAux.getCampos().get("NUMERO").toString();
		comprobanteFinal = null;
		cargarListaComprobanteFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaComprobanteFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaComprobanteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		comprobanteFinal = registroAux.getCampos().get("NUMERO").toString();
	}
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>

	/**
	 * Retorna la variable fuenteInicial
	 * 
	 * @return fuenteInicial
	 */
	public String getFuenteInicial() {
		return fuenteInicial;
	}

	public boolean isCkHacienda2018() {
		return ckHacienda2018;
	}

	public void setCkHacienda2018(boolean ckHacienda2018) {
		this.ckHacienda2018 = ckHacienda2018;
	}

	public boolean isCkRegistrosAdicionales() {
		return ckRegistrosAdicionales;
	}

	public void setCkRegistrosAdicionales(boolean ckRegistrosAdicionales) {
		this.ckRegistrosAdicionales = ckRegistrosAdicionales;
	}

	public boolean isCkBogData() {
		return ckBogData;
	}

	public void setCkBogData(boolean ckBogData) {
		this.ckBogData = ckBogData;
	}

	public boolean isCkInfDisponibilidad() {
		return ckInfDisponibilidad;
	}

	public void setCkInfDisponibilidad(boolean ckInfDisponibilidad) {
		this.ckInfDisponibilidad = ckInfDisponibilidad;
	}

	public boolean isCkCompromiso() {
		return ckCompromiso;
	}

	public void setCkCompromiso(boolean ckCompromiso) {
		this.ckCompromiso = ckCompromiso;
	}

	public boolean isCkOrdenPago() {
		return ckOrdenPago;

	}

	public void setCkOrdenPago(boolean ckOrdenPago) {
		this.ckOrdenPago = ckOrdenPago;
	}

	public boolean isckOrdenPagoReserva() {
		return ckOrdenPagoReserva;

	}

	public void setckOrdenPagoReserva(boolean ckOrdenPagoReserva) {
		this.ckOrdenPagoReserva = ckOrdenPagoReserva;
	}

	public boolean isckBogDataNomina() {
		return ckBogDataNomina;

	}

	public void setckBogDataNomina(boolean ckBogDataNomina) {
		this.ckBogDataNomina = ckBogDataNomina;
	}

	public boolean isPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(boolean presupuesto) {
		this.presupuesto = presupuesto;
	}

	public boolean isContabilidad() {
		return contabilidad;
	}

	public void setContabilidad(boolean contabilidad) {
		this.contabilidad = contabilidad;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * Asigna la variable fuenteInicial
	 * 
	 * @param fuenteInicial Variable a asignar en fuenteInicial
	 */
	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}

	/**
	 * Retorna la variable fuenteFinal
	 * 
	 * @return fuenteFinal
	 */
	public String getFuenteFinal() {
		return fuenteFinal;
	}

	/**
	 * Asigna la variable fuenteFinal
	 * 
	 * @param fuenteFinal Variable a asignar en fuenteFinal
	 */
	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
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

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public String getCptInicial() {
		return cptInicial;
	}

	public void setCptInicial(String cptInicial) {
		this.cptInicial = cptInicial;
	}

	public String getCptFinal() {

		return cptFinal;
	}

	public void setCptFinal(String cptFinal) {
		this.cptFinal = cptFinal;

	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaFuenteInicial
	 * 
	 * @return listaFuenteInicial
	 */
	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}

	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}

	public RegistroDataModelImpl getListaFuenteFinal() {
		return listaFuenteFinal;
	}

	public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
		this.listaFuenteFinal = listaFuenteFinal;
	}

	public String getChequeInicial() {
		return chequeInicial;
	}

	public void setChequeInicial(String chequeInicial) {
		this.chequeInicial = chequeInicial;
	}

	public String getChequeFinal() {
		return chequeFinal;
	}

	public void setChequeFinal(String chequeFinal) {
		this.chequeFinal = chequeFinal;
	}

	public String getComprobanteInicial() {
		return comprobanteInicial;
	}

	public void setComprobanteInicial(String comprobanteInicial) {
		this.comprobanteInicial = comprobanteInicial;
	}

	public String getComprobanteFinal() {
		return comprobanteFinal;
	}

	public void setComprobanteFinal(String comprobanteFinal) {
		this.comprobanteFinal = comprobanteFinal;
	}

	public RegistroDataModelImpl getListaChequeInicial() {
		return listaChequeInicial;
	}

	public void setListaChequeInicial(RegistroDataModelImpl listaChequeInicial) {
		this.listaChequeInicial = listaChequeInicial;
	}

	public RegistroDataModelImpl getListaChequeFinal() {
		return listaChequeFinal;
	}

	public void setListaChequeFinal(RegistroDataModelImpl listaChequeFinal) {
		this.listaChequeFinal = listaChequeFinal;
	}

	public RegistroDataModelImpl getListaComprobanteInicial() {
		return listaComprobanteInicial;
	}

	public void setListaComprobanteInicial(RegistroDataModelImpl listaComprobanteInicial) {
		this.listaComprobanteInicial = listaComprobanteInicial;
	}

	public RegistroDataModelImpl getListaComprobanteFinal() {
		return listaComprobanteFinal;
	}

	public void setListaComprobanteFinal(RegistroDataModelImpl listaComprobanteFinal) {
		this.listaComprobanteFinal = listaComprobanteFinal;
	}

	public boolean isOcultarTxt() {
		return ocultarTxt;
	}

	public void setOcultarTxt(boolean ocultarTxt) {
		this.ocultarTxt = ocultarTxt;
	}

	public boolean isOcultarExcel() {
		return ocultarExcel;
	}

	public void setOcultarExcel(boolean ocultarExcel) {
		this.ocultarExcel = ocultarExcel;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
