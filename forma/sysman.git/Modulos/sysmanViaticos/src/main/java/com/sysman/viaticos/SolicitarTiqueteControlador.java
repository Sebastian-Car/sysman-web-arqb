/*-
 * SolicitarTiqueteControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import java.math.BigDecimal;
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.FrmComisionesControladorEnum;
import com.sysman.viaticos.enums.FrmViaticosControladorEnum;
import com.sysman.viaticos.enums.SolicitarTiqueteControladorUrlEnum;

/**
 * Clase migrada para administrar la solicitud de los viaticos
 *
 * @version 1.0, 18/09/2018
 * @author ybecerra
 * 
 * @version 2.0, 19/09/2018
 * @author eamaya, Proceso de Refactoring DSS y cambio de diseño de forma
 * 
 */
@ManagedBean
@ViewScoped
public class SolicitarTiqueteControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	private String plantilla;
	private String codPlantilla;
	private String nombrePlantilla;
	private Date fechaPlantilla;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista que carga los paises de origen
	 */
	private List<Registro> listaPaisOrigen;
	/**
	 * Lista que carga los paises de destino
	 */
	private List<Registro> listaPaisDestino;
	/**
	 * Lista que carga los departamentos de origen
	 */
	private List<Registro> listaDepartamentoOrigen;
	/**
	 * Lista que carga los departamentos de destino
	 */
	private List<Registro> listaDepartamentoDestino;
	/**
	 * Lista que carga las ciudades de origen
	 */
	private List<Registro> listaCiudadOrigen;
	/**
	 * Lista que carga las ciudades de destino
	 */
	private List<Registro> listaCiudadDestino;

	/**
	 * Indica si la solicitud necesita datos de vuelo o no
	 */
	private boolean solicitaTiquete;

	/**
	 * Titulo que llevara la primera pestania dependiendo el tipo de comision
	 */
	private String tituloPestana;

	/**
	 * Indicador que permite visualizar la pestania de liquidar en pesos
	 */
	private boolean liquidarPesos;

	/**
	 * Atributo que almacena el valor del parametro NUMERO DE CERTIFICADO DE
	 * DISPONIBILIDAD PRESUPUESTAL
	 */
	private String numeroCdp;

	/**
	 * Atributo que almacena el valor del parametro NUMERO DEL REGISTRO PRESUPUESTAL
	 * DE VIATICOS
	 */
	private String numeroRpViaticos;

	/**
	 * Atributo que almacena el valor del parametro NUMERO DEL REGISTRO PRESUPUESTAL
	 * DEL TRANSPORTE
	 */
	private String numeroRpTransporte;

	/**
	 * Atributo que permite visualizar la pestaña de tramites mintic
	 */
	private boolean visibleMintic;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista que contiene las plantillas para generar el informe.
	 */
	private RegistroDataModelImpl listaPlantilla;
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>

	/**
	 * Variable que almacena la fecha de aprobacion
	 */
	private Date fechaAprobacionOld;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de SolicitarTiqueteControlador
	 */
	public SolicitarTiqueteControlador() {
		super();
		compania = SessionUtil.getCompania();

		solicitaTiquete = true;
		try {
			numFormulario = 1923;
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.VI_VIATICOS;
		buscarLlave();
		asignarOrigenDatos();
		cargarListaPlantilla();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitarTiqueteControladorUrlEnum.URL28547.getValue());

		urlLectura = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitarTiqueteControladorUrlEnum.URL38745.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitarTiqueteControladorUrlEnum.URL47512.getValue());

	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaPaisOrigen
	 *
	 */
	public void cargarListaPaisOrigen() {
		try {
			listaPaisOrigen = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													SolicitarTiqueteControladorUrlEnum.URL1564.getValue())
											.getUrl(),
									null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaPaisDestino
	 *
	 */
	public void cargarListaPaisDestino() {
		try {

			listaPaisDestino = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													SolicitarTiqueteControladorUrlEnum.URL1564.getValue())
											.getUrl(),
									null));
		} catch (

		SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaDepartamentoOrigen
	 *
	 */
	public void cargarListaDepartamentoOrigen() {
		Map<String, Object> param = new TreeMap<>();

		param.put("PAIS", registro.getCampos().get("PAIS_ORIGEN"));

		try {
			listaDepartamentoOrigen = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													SolicitarTiqueteControladorUrlEnum.URL4851.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaDepartamentoDestino
	 *
	 */
	public void cargarListaDepartamentoDestino() {

		Map<String, Object> param = new TreeMap<>();

		param.put("PAIS", registro.getCampos().get("PAIS_DESTINO"));

		try {
			listaDepartamentoDestino = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													SolicitarTiqueteControladorUrlEnum.URL4851.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCiudadOrigen
	 *
	 */
	public void cargarListaCiudadOrigen() {

		Map<String, Object> param = new TreeMap<>();

		param.put("PAIS", registro.getCampos().get("PAIS_ORIGEN"));

		param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(), registro.getCampos().get("DEPARTAMENTO_ORIGEN"));

		try {
			listaCiudadOrigen = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													SolicitarTiqueteControladorUrlEnum.URL17258.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaCiudadDestino
	 *
	 */
	public void cargarListaCiudadDestino() {
		Map<String, Object> param = new TreeMap<>();

		param.put("PAIS", registro.getCampos().get("PAIS_DESTINO"));
		param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(), registro.getCampos().get("DEPARTAMENTO_DESTINO"));

		try {
			listaCiudadDestino = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													SolicitarTiqueteControladorUrlEnum.URL17258.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>

	/**
	 * Metodo ejecutado al cambiar el control Estado
	 * 
	 */
	public void cambiarEstado() {

		if ("A".equals(registro.getCampos().get("ESTADO")) || "C".equals(registro.getCampos().get("ESTADO"))) {

			registro.getCampos().put("FECHA_APROBACION", new Date());

		} else {

			registro.getCampos().put("FECHA_APROBACION", fechaAprobacionOld);

		}
	}

	/**
	 * Metodo ejecutado al cambiar el control EstadoMintic
	 * 
	 */
	public void cambiarEstadoMintic() {
		cargarPestaniaLiquidarPesos();
	}

	/**
	 * Metodo ejecutado al cambiar el control AvalCoordinador
	 * 
	 */
	public void cambiarAvalCoordinador() {
		cargarPestaniaLiquidarPesos();
	}

	/**
	 * Metodo ejecutado al cambiar el control DiaTransito
	 * 
	 */
	public void cambiarDiaTransito() {
		cargarValorComisionExterior();
		cambiarValorTasa();
	}

	/**
	 * Metodo ejecutado al cambiar el control ValorTasa
	 * 
	 */
	public void cambiarValorTasa() {

		Double valorTasa = Double
				.parseDouble(SysmanFunciones.nvl(registro.getCampos().get("VALOR_TASA_CAMBIO"), "0.0").toString());

		Double valorDolares = Double
				.parseDouble(SysmanFunciones.nvl(registro.getCampos().get("VALORTOTALVIATICO"), "0.0").toString());

		registro.getCampos().put("VALOR_TOTAL_PESOS", new BigDecimal(valorDolares * valorTasa));

	}

	/**
	 * Metodo ejecutado al cambiar el control Pernocta
	 * 
	 */
	public void cambiarPernocta() {

		if ((boolean) registro.getCampos().get("PERNOCTA")) {
			registro.getCampos().put("NODIAS", Double.parseDouble(registro.getCampos().get("NODIAS").toString()) + 0.5);
		} else {
			registro.getCampos().put("NODIAS", Double.parseDouble(registro.getCampos().get("NODIAS").toString()) - 0.5);
		}

		cargarValorComisionInterior();

		cargarValorComisionExterior();
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * Carga la lista listaPlantilla
	 *
	 */
	public void cargarListaPlantilla() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitarTiqueteControladorUrlEnum.URL1234.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.TIPO.getName(), "40");
		param.put("FECHACONSULTA", new Date());

		listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaPlantilla
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPlantilla(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		codPlantilla = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
		plantilla = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
		nombrePlantilla = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");

	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	public void oprimirGenerar() {
		Map<String, Object> param = new HashMap<>();
		param.put("codigoPlantilla", codPlantilla);
		param.put("fechaPlantilla", SysmanFunciones.formatearFecha(fechaPlantilla));
		param.put("nombreDocDescarga", nombrePlantilla);

		HashMap<String, String> variablesConsultaW = new HashMap<>();
		variablesConsultaW.put("s$compania$s", compania);
		variablesConsultaW.put("s$anio$s", registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString());
		variablesConsultaW.put("s$tipoViatico$s", registro.getCampos().get("TIPO_VIATICO").toString());
		variablesConsultaW.put("s$solicitud$s", registro.getCampos().get("CODSOLICITUD").toString());

		SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);

		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR.getCodigo()));
		direccionador.setParametros(param);

		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	}

	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();

		cargarListaPaisOrigen();
		cargarListaPaisDestino();
		cargarListaDepartamentoOrigen();
		cargarListaDepartamentoDestino();
		cargarListaCiudadOrigen();
		cargarListaCiudadDestino();
		plantilla = null;

		solicitaTiquete = (boolean) registro.getCampos().get("SOLICITA_TIQUETE");

		cargarPestaniaLiquidarPesos();

		if ("1".equals(registro.getCampos().get("TIPOCOMISION").toString())) {
			tituloPestana = "Preliquidar en Dolares";
			visibleMintic = true;
		} else {
			tituloPestana = "General";
			visibleMintic = false;
		}

		cargarValorComisionInterior();
		cargarValorComisionExterior();

		fechaAprobacionOld = (Date) registro.getCampos().get("FECHA_APROBACION");

		try {
			numeroCdp = ejbSysmanUtil.consultarParametro(compania,
					"NUMERO DE CERTIFICADO DE DISPONIBILIDAD PRESUPUESTAL", "6", new Date(), false);

			numeroRpViaticos = ejbSysmanUtil.consultarParametro(compania,
					"NUMERO DEL REGISTRO PRESUPUESTAL DE VIATICOS", "6", new Date(), false);

			numeroRpTransporte = ejbSysmanUtil.consultarParametro(compania,
					"NUMERO DEL REGISTRO PRESUPUESTAL DEL TRANSPORTE", "6", new Date(), false);

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	private void cargarPestaniaLiquidarPesos() {

		if ("A".equals(registro.getCampos().get("ESTADO_MINTIC"))
				&& "A".equals(registro.getCampos().get("AVAL_COORDINADOR"))

		) {

			liquidarPesos = true;

		} else {
			liquidarPesos = false;
		}

	}

	private void cargarValorComisionInterior() {
		if (2 == Integer.parseInt(registro.getCampos().get("TIPOCOMISION").toString())) {

			Double numDias = Double.parseDouble(registro.getCampos().get("NODIAS").toString());

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put("SALARIO", registro.getCampos().get("SALARIO_COMISIONADO"));
			param.put(GeneralParameterEnum.VIGENCIA.getName(),
					SysmanFunciones.ano((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));

			try {
				Registro regValores = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												SolicitarTiqueteControladorUrlEnum.URL5421.getValue())
										.getUrl(),
								param));

				if (regValores != null) {

					Double valorManual = SysmanFunciones.nvlDbl(regValores.getCampos().get("VALOR_MANUAL"), 0.0);

					registro.getCampos().put("VALOR_DIARIO_COMISION", valorManual);

					registro.getCampos().put("VALORTOTALVIATICO", numDias * valorManual);

					agregarRegistroNuevo(false);

				} else {
					JsfUtil.agregarMensajeAlerta(
							"No se encontró el equivalente de comisón para el salario del comisionado. Revisar el formulario de rango de viáticos");
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

	}

	private void cargarValorComisionExterior() {
		if (1 == Integer.parseInt(registro.getCampos().get("TIPOCOMISION").toString())) {

			Double numDias = Double.parseDouble(registro.getCampos().get("NODIAS").toString());

			int diasTransito = Integer
					.parseInt(SysmanFunciones.nvl(registro.getCampos().get("DIAS_TRANSITO"), "0").toString());

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put("SALARIO", registro.getCampos().get("SALARIO_COMISIONADO"));
			param.put(GeneralParameterEnum.VIGENCIA.getName(),
					SysmanFunciones.ano((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));

			try {
				Registro regValores = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												SolicitarTiqueteControladorUrlEnum.URL5421.getValue())
										.getUrl(),
								param));

				if (regValores != null) {

					String zona = SysmanFunciones.nvl(registro.getCampos().get("ZONA_EXTERIOR"), "").toString();

					Double valorDiario = null;

					switch (zona) {
					case "1":
						valorDiario = SysmanFunciones.nvlDbl(regValores.getCampos().get("VALOR_VIATICO_ZONA1"), 0.0);

						break;
					case "2":
						valorDiario = SysmanFunciones.nvlDbl(regValores.getCampos().get("VALOR_VIATICO_ZONA2"), 0.0);
						break;
					case "3":
						valorDiario = SysmanFunciones.nvlDbl(regValores.getCampos().get("VALOR_VIATICO_ZONA3"), 0.0);
						break;
					default:
						break;
					}

					registro.getCampos().put("VALOR_DIARIO_COMISION", valorDiario);

					if (diasTransito > 0) {

						registro.getCampos().put("VALORTOTALVIATICO",
								(valorDiario * numDias) + ((valorDiario * 0.3) * diasTransito));

					} else {
						registro.getCampos().put("VALORTOTALVIATICO", (valorDiario * numDias));

					}
					agregarRegistroNuevo(false);

				} else {
					JsfUtil.agregarMensajeAlerta(
							"No se encontraron equivalentes en dolares para el salario del comisionado. Revisar el formulario de rango de viáticos");
				}

			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 */
	@Override
	public boolean actualizarAntes() {
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

		registro.getCampos().remove(FrmComisionesControladorEnum.NOMBREDEPENDENCIA.getValue());

		registro.getCampos().remove(FrmComisionesControladorEnum.NOMBRETERCERO.getValue());

		registro.getCampos().remove(FrmComisionesControladorEnum.NOMBRERESPONSABLE.getValue());

		registro.getCampos().remove(FrmComisionesControladorEnum.NOMBREBANCO.getValue());

		registro.getCampos().remove("NOMBRECLASETRANSPORTE");

		registro.getCampos().remove(FrmComisionesControladorEnum.NOMBRE_EXPE.getValue());

		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaPaisOrigen
	 * 
	 * @return listaPaisOrigen
	 */
	public List<Registro> getListaPaisOrigen() {
		return listaPaisOrigen;
	}

	/**
	 * Asigna la lista listaPaisOrigen
	 * 
	 * @param listaPaisOrigen
	 *            Variable a asignar en listaPaisOrigen
	 */
	public void setListaPaisOrigen(List<Registro> listaPaisOrigen) {
		this.listaPaisOrigen = listaPaisOrigen;
	}

	/**
	 * Retorna la lista listaPaisDestino
	 * 
	 * @return listaPaisDestino
	 */
	public List<Registro> getListaPaisDestino() {
		return listaPaisDestino;
	}

	/**
	 * Asigna la lista listaPaisDestino
	 * 
	 * @param listaPaisDestino
	 *            Variable a asignar en listaPaisDestino
	 */
	public void setListaPaisDestino(List<Registro> listaPaisDestino) {
		this.listaPaisDestino = listaPaisDestino;
	}

	/**
	 * Retorna la lista listaDepartamentoOrigen
	 * 
	 * @return listaDepartamentoOrigen
	 */
	public List<Registro> getListaDepartamentoOrigen() {
		return listaDepartamentoOrigen;
	}

	/**
	 * Asigna la lista listaDepartamentoOrigen
	 * 
	 * @param listaDepartamentoOrigen
	 *            Variable a asignar en listaDepartamentoOrigen
	 */
	public void setListaDepartamentoOrigen(List<Registro> listaDepartamentoOrigen) {
		this.listaDepartamentoOrigen = listaDepartamentoOrigen;
	}

	/**
	 * Retorna la lista listaDepartamentoDestino
	 * 
	 * @return listaDepartamentoDestino
	 */
	public List<Registro> getListaDepartamentoDestino() {
		return listaDepartamentoDestino;
	}

	/**
	 * Asigna la lista listaDepartamentoDestino
	 * 
	 * @param listaDepartamentoDestino
	 *            Variable a asignar en listaDepartamentoDestino
	 */
	public void setListaDepartamentoDestino(List<Registro> listaDepartamentoDestino) {
		this.listaDepartamentoDestino = listaDepartamentoDestino;
	}

	/**
	 * Retorna la lista listaCiudadOrigen
	 * 
	 * @return listaCiudadOrigen
	 */
	public List<Registro> getListaCiudadOrigen() {
		return listaCiudadOrigen;
	}

	/**
	 * Asigna la lista listaCiudadOrigen
	 * 
	 * @param listaCiudadOrigen
	 *            Variable a asignar en listaCiudadOrigen
	 */
	public void setListaCiudadOrigen(List<Registro> listaCiudadOrigen) {
		this.listaCiudadOrigen = listaCiudadOrigen;
	}

	/**
	 * Retorna la lista listaCiudadDestino
	 * 
	 * @return listaCiudadDestino
	 */
	public List<Registro> getListaCiudadDestino() {
		return listaCiudadDestino;
	}

	/**
	 * Asigna la lista listaCiudadDestino
	 * 
	 * @param listaCiudadDestino
	 *            Variable a asignar en listaCiudadDestino
	 */
	public void setListaCiudadDestino(List<Registro> listaCiudadDestino) {
		this.listaCiudadDestino = listaCiudadDestino;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>

	public boolean isSolicitaTiquete() {
		return solicitaTiquete;
	}

	public void setSolicitaTiquete(boolean solicitaTiquete) {
		this.solicitaTiquete = solicitaTiquete;
	}

	public String getTituloPestana() {
		return tituloPestana;
	}

	public void setTituloPestana(String tituloPestana) {
		this.tituloPestana = tituloPestana;
	}

	public String getNumeroCdp() {
		return numeroCdp;
	}

	public void setNumeroCdp(String numeroCdp) {
		this.numeroCdp = numeroCdp;
	}

	public String getNumeroRpViaticos() {
		return numeroRpViaticos;
	}

	public void setNumeroRpViaticos(String numeroRpViaticos) {
		this.numeroRpViaticos = numeroRpViaticos;
	}

	public String getNumeroRpTransporte() {
		return numeroRpTransporte;
	}

	public void setNumeroRpTransporte(String numeroRpTransporte) {
		this.numeroRpTransporte = numeroRpTransporte;
	}

	public boolean isLiquidarPesos() {
		return liquidarPesos;
	}

	public void setLiquidarPesos(boolean liquidarPesos) {
		this.liquidarPesos = liquidarPesos;
	}

	public boolean isVisibleMintic() {
		return visibleMintic;
	}

	public void setVisibleMintic(boolean visibleMintic) {
		this.visibleMintic = visibleMintic;
	}

	public RegistroDataModelImpl getListaPlantilla() {
		return listaPlantilla;
	}

	public void setListaPlantilla(RegistroDataModelImpl listaPlantilla) {
		this.listaPlantilla = listaPlantilla;
	}

	public String getPlantilla() {
		return plantilla;
	}

	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	// </SET_GET_ADICIONALES>
}
