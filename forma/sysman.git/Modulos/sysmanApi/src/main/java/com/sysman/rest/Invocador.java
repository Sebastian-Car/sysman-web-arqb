package com.sysman.rest;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import com.sysman.dao.Registro;
import com.sysman.enums.GeneralAutoservicioEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.rest.enums.InvocadorClaseEnum;
import com.sysman.rest.enums.InvocadorEnum;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.impl.ProcesaInfoInventario;
import com.sysman.rest.logica.Comprobante;
import com.sysman.rest.logica.ParametrosComprobanteCnt;
import com.sysman.rest.logica.ParametrosDetalleComprobanteCnt;
import com.sysman.rest.logica.ParametrosEntradaDatosFamiliares;
import com.sysman.rest.logica.ParametrosEntradaDatosPersonales;
import com.sysman.rest.logica.ParametrosEntradaInventario;
import com.sysman.rest.logica.ParametrosEntradaSolicitudes;
import com.sysman.rest.logica.ParametrosLiquidacionFactura;
import com.sysman.rest.logica.ParametrosExcelAfectaciones;
import com.sysman.rest.logica.ParametrosInformeFactura;
import com.sysman.rest.logica.ParametrosInformeFacturasPorCobrar;
import com.sysman.rest.logica.ParametrosPagarFacturaGeneral;
import com.sysman.rest.logica.ParametrosRecuadoCausacion;
import com.sysman.rest.logica.SalidaInformes1GobNar;
import com.sysman.rest.logica.SalidaInformes2GobNar;
import com.sysman.rest.logica.SalidaInformes3GobNar;
import com.sysman.rest.logica.SalidaInformes4GobNar;
import com.sysman.rest.logica.SalidaInformes5GobNar;
import com.sysman.rest.logica.SalidaInformes6GobNar;
import com.sysman.rest.logica.SalidaValidaIdsn;
import com.sysman.rest.negocio.GestionAutoservicio;
import com.sysman.rest.negocio.enums.GeneraArchivoUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.GenerarToken;
import com.sysman.util.rest.Parametro;
import com.sysman.util.rest.Respuesta;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaApiPagArqC;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/v1")
@Ejecutor(tipo = EnumRole.INVOKER)
public class Invocador {
	/**
	 * Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Invocador.class);
	/**
	 * Procesador de consultas/solicitudes. Servicio de acceso a datos.
	 */
	@Inject
	private Procesador<ParametrosEntradaSolicitudes, Respuesta> procesadorSolicitudes;

	/**
	 * Procesador de consultas/solicitudespropia. Servicio de acceso a datos.
	 */
	@Inject
	private Procesador<ParametrosEntradaSolicitudes, RespuestaApi> procesadorSolicitudesPropia;

	/**
	 * Procesador de consultas/solicitudespropia. Servicio de acceso a datos.
	 */
	@Inject
	private Procesador<ParametrosEntradaInventario, RespuestaApi> procesadorInventario;

	/**
	 * Procesador de consultas/solicitudespropia. Servicio de acceso a datos de
	 * invetario WS
	 */
	@Inject
	// private Procesador<Map<String, Object>, RespuestaApi> procesaInfoInventario;
	private ProcesaInfoInventario procesaInfoInventario;

	/**
	 * Procesador de actualizaciones de datos personales.
	 */
	@Inject
	private Procesador<ParametrosEntradaDatosPersonales, Respuesta> procesadorDatosPersonales;
	/**
	 * Procesador de actualizaciones de datos familiares.
	 */
	@Inject
	private Procesador<ParametrosEntradaDatosFamiliares, Respuesta> procesadorDatosFamiliares;
	/**
	 * Procesador para resolver consutlas del generador de reportes.
	 */
	@Inject
	private Procesador<Map<String, Object>, List<Map<String, Object>>> procesadorResuelveConsultas;

	/**
	 * Procesador para resolver la contabilizaci&oacute;n
	 */
	@Inject
	@Named("procesaContabiliza")
	private Procesador<Comprobante, RespuestaApi> procesadorContabilizar;

	/**
	 * Procesador para imprimir el comprobante contable
	 */
	@Inject
	@Named("procesaImprimeComprobante")
	private Procesador<Comprobante, RespuestaApi> procesadorImprimeComprobante;

	/**
	 * Procesador para generar el organigrama
	 */
	@Inject
	@Named("procesaGeneraOrganigrama")
	private Procesador<String, RespuestaApi> procesadorGeneraOrganigrama;

	/**
	 * Procesador para realizar el proceso de cargar causacion y recaudo
	 */
	@Inject
	@Named("cargarRecaudoCausacion")
	private Procesador<ParametrosRecuadoCausacion, RespuestaApi> procesadorCargarRecuadoCausacion;

	/**
	 * Procesador de comprobantes cnt
	 */
	@Inject
	@Named("procesaInsercionComprobanteCnt")
	private Procesador<ParametrosComprobanteCnt, RespuestaApi> procesadorPostComprobantesCnt;

	/**
	 * Procesador de comprobantes cnt
	 */
	@Inject
	@Named("procesaActualizacionComprobanteCnt")
	private Procesador<ParametrosComprobanteCnt, RespuestaApi> procesadorPutComprobantesCnt;

	/**
	 * Procesador de actualizacion comprobantes cnt
	 */
	@Inject
	@Named("procesaActualizacionComprobanteCntTexto")
	private Procesador<ParametrosComprobanteCnt, RespuestaApi> procesadorPutComprobantesCntTexto;

	/**
	 * Procesador de actualizacion comprobantes cnt anulado
	 */
	@Inject
	@Named("procesaActualizacionComprobanteCntAnulado")
	private Procesador<ParametrosComprobanteCnt, RespuestaApi> procesadorPutComprobantesCntAnulado;

	/**
	 * Procesador de elimiancion de comprobantes cnt
	 */
	@Inject
	@Named("procesaDeleteComprobanteCnt")
	private Procesador<ParametrosComprobanteCnt, RespuestaApi> procesadorDeleteComprobantesCnt;

	/**
	 * Procesador de insercion de detalle comprobantes cnt
	 */
	@Inject
	@Named("procesaInsercionDetalleComprobanteCnt")
	private Procesador<ParametrosDetalleComprobanteCnt, RespuestaApi> procesadorPostDetalleComprobantesCnt;

	/**
	 * Procesador de eliminacion de detalle comprobantes cnt
	 */
	@Inject
	@Named("procesaDeleteDetalleComprobanteCnt")
	private Procesador<ParametrosDetalleComprobanteCnt, RespuestaApi> procesadorDeleteDetalleComprobantesCnt;
	/**
	 * Procesador de actualizacion de detalle comprobantes cnt
	 */
	@Inject
	@Named("procesaActualizacionDetalleComprobanteCnt")
	private Procesador<ParametrosDetalleComprobanteCnt, RespuestaApi> procesadorPutDetalleComprobantesCnt;

	/**
	 * Procesador de pagar factura general
	 */
	@Inject
	@Named("procesaPagarFacturaGeneral")
	private Procesador<ParametrosPagarFacturaGeneral, RespuestaApi> procesadorPagarFacturaGeneral;

	/**
	 * Procesador para generar el inventario de dependencia
	 */
	@Inject
	@Named("procesaGeneraInventarioDependencia")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorGeneraInventarioDependencia;

	/**
	 * Procesador para cargar los terceros
	 */
	@Inject
	@Named("procesaCargarTercero")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarTercero;

	/**
	 * Procesador para consultar facturas generales
	 */
	@Inject
	@Named("procesaConsultarFacturas")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorConsultarFacturaGeneral;

	/**
	 * Procesador para cargar los comprobantes contables
	 */
	@Inject
	@Named("procesaCargarComprobanteCnt")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarComprobanteCnt;

	/**
	 * Procesador para cargar los detalles comprobantes contables
	 */
	@Inject
	@Named("procesaCargarDetalleComprobanteCnt")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarDetalleComprobanteCnt;

	/**
	 * Procesador para cargar el consecutivo de comprobantes contables
	 */
	@Inject
	@Named("procesaCargarConsecutivoComprobanteCnt")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarConsecutivoComprobanteCnt;

	@Inject
	@Named("procesaCargarGeneros")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarGeneros;

	@Inject
	@Named("procesaCargarEstadoPeriodo")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarEstadoPeriodo;

	@Inject
	@Named("procesaCargarPeriodosCerrados")
	private Procesador<Map<String, Object>, RespuestaApi> procesaCargarPeriodosCerrados;

	@Inject
	@Named("procesacargartipotramites")
	private Procesador<Map<String, Object>, RespuestaApi> procesaCargarTipoTramites;

	@Inject
	@Named("procesadorCargarPlanContable")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarPlanContable;

	@Inject
	@Named("procesaCargarTipoPersona")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarTipoPersona;

	@Inject
	@Named("procesaCargarTipoPobl")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarTipoPobl;

	@Inject
	@Named("procesaCargarRangoEdad")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarRangoEdad;

	@Inject
	@Named("procesaCargarVulneravilidad")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarVulneravilidad;

	@Inject
	@Named("procesaCargarOcupacion")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarOcupacion;

	@Inject
	@Named("procesaCargarEscolaridad")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCargarEscolaridad;

	/**
	 * Procesador para realizar el proceso de generar el excel de afectaciones
	 */
	@Inject
	@Named("excelAfectaciones")
	private Procesador<ParametrosExcelAfectaciones, RespuestaApi> procesadorExcelAfectaciones;
	/**
	 * Procesador de pagar factura general
	 */
	@Inject
	@Named("procesaLiquidacionFactura")
	private Procesador<ParametrosLiquidacionFactura, RespuestaApi> procesaLiquidacionFactura;	
	
	@Inject
	@Named("procesadorDependenciasContab")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorDependenciasContab;
	
	@Inject
	@Named("procesadorListaPlanContableCremil")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorListaPlanContableCremil;
	
	@Inject
	@Named("procesadorFuenteRecursos")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorFuenteRecursos;
	
	@Inject
	@Named("procesadorCentroCostos")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorCentroCostos;
	
	@Inject
	@Named("procesadorAuxiliares")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorAuxiliares;
	
	@Inject
	@Named("procesadorReferencias")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorReferencias;
	
	@Inject
	@Named("procesaListaSaldoAuxContable")
	private Procesador<Map<String, Object>, RespuestaApi> procesaListaSaldoAuxContable;
	
    @Inject
    @Named("procesadorInformeCuentasxCobrar")
    private Procesador<Map<String, Object>, RespuestaApi> procesadorInformeCuentasxCobrar;
	
	/**
	 * Procesador para cargar los terceros paginado
	 */
	@Inject
	@Named("procesaCargarTerceroPaginado")
	private Procesador<Map<String, Object>, RespuestaApiPagArqC> procesadorCargarTerceroPag;
	
	/**
	 * Procesador para cargar los terceros final paginado
	 */
	@Inject
	@Named("procesaCargarTerceroPaginadoFinal")
	private Procesador<Map<String, Object>, RespuestaApiPagArqC> procesadorCargarTerceroPagFinal;
	
	/**
	 * Procesador para cargar los terceros paginado
	 */
	@Inject
	@Named("procesaCargarCuentasCarteraPaginado")
	private Procesador<Map<String, Object>, RespuestaApiPagArqC> procesadorCargarCuentasCarteraPag;
	
	/**
	 * Procesador para cargar los terceros paginado
	 */
	@Inject
	@Named("procesaCargarCuentasCarteraPaginadoFinal")
	private Procesador<Map<String, Object>, RespuestaApiPagArqC> procesadorCargarCuentasCarteraPagFinal;
	
	@Inject
	@Named("procesadorConsultaParametro")
	private Procesador<Parametro, RespuestaApi> procesadorConsultaParametro;
	
	@Inject
	private Procesador<ParametrosInformeFactura, RespuestaApi> procesadorInformeFactura;
	
	@Inject
	private Procesador<ParametrosInformeFacturasPorCobrar, RespuestaApi> procesadorInformeFacturasPorCobrar;
	
	/**
	 * Procesador para cargar los referencia por pagina
	 */
	@Inject
	@Named("procesadorConsultarPaginado")
	private Procesador<Map<String, Object>, RespuestaApi> procesadorConsultarPaginado;
	/**
	 * constante para validar el nit de la entidad con la base de datos
	 */
	private static final String CODIGO_URL_DATOS_AUTENTICACION = "59011";

	/**
	 * constante para validar el nit de la entidad con la base de datos
	 */
	private static final String CODIGO_URL_EXISTE_COM = "72039";
	/**
	 * constante para validar el nit de la entidad con la base de datos
	 */
	private static final String CODIGO_VALIDA_PAGO_IDSN = "39089";

	public static final String PROCESOPQR = "00000";
	/**
	 * constante para validar el estado del mes contable
	 */
	private static final String CODIGO_URL_ESTADO_CONTABLE = "224001";
	
	//HU1 7733460 - Vista a Informes de base de datos - WebService Tipo Rest
	

	/**
	 * Informa ejecucion presupuestal de gastos acumulada
	 */
	private static final String EJECUCION_PRESUPUESTAL_DE_GASTOS_ACUMULADA = "1916004";
	/**
	 * Informa ejecucion presupuestal de gastos acumulada
	 */
	private static final String AUXILIAR_PRESUPUESTAL_POR_CUENTAS = "1916003";
	/**
	 * Informa ejecucion presupuestal de gastos acumulada
	 */
	private static final String LISTADO_DE_TERCEROS = "1916001";
	/**
	 * Informa ejecucion presupuestal de gastos acumulada
	 */
	private static final String PLAN_PRESUPUESTAL = "1916002";
	/**
	 * Informa ejecucion presupuestal de gastos acumulada
	 */
	private static final String PERSONAL_ACTIVO_DE_NOMINA = "1916005";
	/**
     * Informa ejecucion presupuestal de gastos acumulada
     */
	private static final String EJECUCION_PPTAL_INGRESOS = "1916007";
	//HU1 7733460 - Vista a Informes de base de datos - WebService Tipo Rest	
	
	/**
	 * identifica de donde se realiza el llamado
	 *
	 */
	private Boolean propia = false;
	/**
	 * Util para traducir los mensajes de errores
	 */
	private ResourceBundle idioma;

	@GET
	@Path("/prueba")
	@Produces("text/plain")
	public Response doGet() {
		return Response.ok("method doGet invoked").build();
	}

	/**
	 * Ejecuta la petici&oacute;n POST para crear una consulta o solicitud de
	 * autoservicio.
	 *
	 * @param token    Credenciales de autorizaci&oacute;n. Token asociado con a la
	 *                 consulta o solicitud de autoservicio.
	 * @param contexto Parametros especificados en el cuerpo de la petici&oacute;n.
	 * @return respuesta generada al llevar a cabo la petici&oacute;n POST.
	 */
	@POST
	@Path("/solicitudes")
	@Consumes("application/json")
	@Produces("application/json")
	public Response crearSolicitudAutoservicio(@HeaderParam(AUTHORIZATION) String token,
			ParametrosEntradaSolicitudes contexto) {
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			if (!peticionAutorizada(contexto.getCompania(), contexto.getEntidad(), token, contexto.getClase())) {
				String mensaje = idioma.getString(InvocadorEnum.MSG_INVOCADOR_TOKENINVALIDO.getValue());
				String causa = idioma.getString(InvocadorEnum.MSG_INVOCADOR_NOAUTORIZA.getValue());
				LOG.error("Error en <<crear solicitut>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
				return Response.status(Response.Status.UNAUTHORIZED).entity(mensaje).build();
			}
		} catch (NegocioExcepcion e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			return Response.status(Response.Status.UNAUTHORIZED).entity(e1.getMessage()).build();
		}

		Respuesta resultado = new Respuesta();
		RespuestaApi respuestaApi = new RespuestaApi();
		try {
			if (propia) {
				procesadorSolicitudesPropia.setContexto(contexto);
				procesadorSolicitudesPropia.procesar();
				respuestaApi = procesadorSolicitudesPropia.getResultado();
			} else {
				procesadorSolicitudes.setContexto(contexto);
				procesadorSolicitudes.procesar();
				resultado = procesadorSolicitudes.getResultado();
				respuestaApi.setCuerpo(resultado);
			}
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Crea una solicitud de consulta, devolviendo como respuesta un documento con
	 * la consulta respectiva.
	 *
	 * @param token    Credenciales de autorizaci&oacute;n. Token asociado con a la
	 *                 consulta o solicitud de autoservicio.
	 * @param contexto Parametros especificados en el cuerpo de la petici&oacute;n.
	 * @return respuesta generada al llevar a cabo la petici&oacute;n POST.
	 */
	@POST
	@Path("/consultaspropia")
	@Consumes("application/json")
	@Produces("application/json")
	public Response crearConsultaAutoservicioPropia(@HeaderParam(AUTHORIZATION) String token,
			ParametrosEntradaSolicitudes contexto) {
		propia = true;
		return crearSolicitudAutoservicio(token, contexto);
	}

	/**
	 * Crea una solicitud de consulta, devolviendo como respuesta un documento con
	 * la consulta respectiva.
	 *
	 * @param token    Credenciales de autorizaci&oacute;n. Token asociado con a la
	 *                 consulta o solicitud de autoservicio.
	 * @param contexto Parametros especificados en el cuerpo de la petici&oacute;n.
	 * @return respuesta generada al llevar a cabo la petici&oacute;n POST.
	 */
	@POST
	@Path("/consultas")
	@Consumes("application/json")
	@Produces("application/json")
	public Response crearConsultaAutoservicio(@HeaderParam(AUTHORIZATION) String token,
			ParametrosEntradaSolicitudes contexto) {
		propia = false;
		return crearSolicitudAutoservicio(token, contexto);
	}

	/**
	 * Crea una solicitud de actualizaci&oacute;n de datos personales.
	 *
	 * @param token    Credenciales de autorizaci&oacute;n..
	 * @param contexto Parametros especificados en el cuerpo de la petici&oacute;n.
	 * @returnrespuesta generada al llevar a cabo la petici&oacute;n POST.
	 */
	@POST
	@Path("/datospersonales")
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizarDatosPersonales(@HeaderParam(AUTHORIZATION) String token,
			ParametrosEntradaDatosPersonales contexto) {
		Respuesta resultado;
		RespuestaApi respuestaApi = new RespuestaApi();
		try {
			validarTokenconBD(contexto.getCompania(), contexto.getEntidad(), token, contexto.getClase());
		} catch (NegocioExcepcion e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		try {
			procesadorDatosPersonales.setContexto(contexto);
			procesadorDatosPersonales.procesar();
			resultado = procesadorDatosPersonales.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		respuestaApi.setCuerpo(resultado);
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Creaci&oacute;n de solicitud de actualizaci&oacute;n de datos de familiares.
	 *
	 * @param token    Credenciales de autorizaci&oacute;n..
	 * @param contexto Parametros especificados en el cuerpo de la petici&oacute;n.
	 * @return respuesta generada al llevar a cabo la petici&oacute;n POST.
	 */
	@POST
	@Path("/datosfamiliares")
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizarDatosFamiliares(@HeaderParam(AUTHORIZATION) String token,
			ParametrosEntradaDatosFamiliares contexto) {
		Respuesta resultado;
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			validarTokenconBD(contexto.getCompania(), contexto.getEntidad(), token, contexto.getClase());
		} catch (NegocioExcepcion e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		try {
			procesadorDatosFamiliares.setContexto(contexto);
			procesadorDatosFamiliares.procesar();
			resultado = procesadorDatosFamiliares.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		respuestaApi.setCuerpo(resultado);
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Trae el resultado de ejecutar una consulta definida en el generador de
	 * reportes.
	 * 
	 * @param token    Credenciales de autorizaci&oacute;n..
	 * @param contexto Parametros especificados en el cuerpo de la petici&oacute;n.
	 * @return respuesta generada al llevar a cabo la petici&oacute;n POST.
	 */
	@POST
	@Path("/resuelveconsultas")
	@Consumes("application/json")
	@Produces("application/json")
	public Response resolverConsulta(@HeaderParam(AUTHORIZATION) String token, Map<String, Object> contexto) {
		Map<String, Object> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		treeMap.putAll(contexto);

		List<Map<String, Object>> resultado;
		RespuestaApi respuestaApi = new RespuestaApi();
		try {
			String compania = SysmanFunciones.toString(treeMap.get("compania"));
			String entidad = SysmanFunciones.toString(treeMap.get("entidad"));
			validarTokenconBD(compania, entidad, token, 0);
		} catch (NegocioExcepcion e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		try {
			procesadorResuelveConsultas.setContexto(treeMap);
			procesadorResuelveConsultas.procesar();
			resultado = procesadorResuelveConsultas.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		respuestaApi.setCuerpo(resultado);
		return Response.ok().entity(resultado).build();
	}

	/**
	 * Verifica si la petici&oacute;n puede ser autorizada.
	 *
	 * @param token token de seguridad
	 * @param clase código de la solicitud de autoservicio
	 * @return verdadero si la petici&oacute;n ha sido autorizada
	 * @throws NegocioExcepcion
	 */
	private boolean peticionAutorizada(String compania, String entidad, String token, int clase)
			throws NegocioExcepcion {
		validarTokenconBD(compania, entidad, token, clase);
		if (clase == InvocadorClaseEnum.CERTIFICADO_LABORAL.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.VOLANTE_PAGO.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.CERTIFICADO_RETENCION.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.PERMISOS.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.VACACIONES.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.FONDOS.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.CESANTIAS_PENDIENTES.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.CERTIFICADO_LABORAL_CON_FUNCIONES.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.VACACIONES_PENDIENTES.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.MANUAL_FUNCIONES.getClase()) {
			return true;
		} else if (clase == InvocadorClaseEnum.CERTIFICADO_LABORAL_SIN_ASIGNACION_SALARIAL.getClase()) {
			return true;
		} else {
			return false;
		}
	}

	private void validarTokenconBD(String compania, String entidadServicio, String tokenServicio, int claseServicio)
			throws NegocioExcepcion {
		String tokenGenerado;
		String tokenBD;
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		GenerarToken generarToken = new GenerarToken(entidadServicio, String.valueOf(claseServicio));
		tokenGenerado = generarToken.Base64Hash();

		if (!tokenServicio.equals(tokenGenerado)) {
			String mensaje = idioma.getString(InvocadorEnum.MSG_INVOCADOR_TOKENINVALIDO.getValue());
			String causa = idioma.getString(InvocadorEnum.MSG_INVOCADOR_NOAUTORIZA.getValue());
			LOG.error("Error en <<validarToken>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
			NegocioExcepcion error = new NegocioExcepcion(mensaje);
			error.initCause(new Exception(causa));
			throw error;
		}
		RequestManager rq = new RequestManager();
		Map<String, Object> par = new HashMap<>();
		par.put(GeneralParameterEnum.CODIGO.getName(), compania);

		Parameter parCompania;
		try {
			parCompania = rq.get(UrlServiceUtil.getUrlBeanById(CODIGO_URL_DATOS_AUTENTICACION).getUrl(), par);
		} catch (SystemException e) {
			String mensaje = idioma.getString(InvocadorEnum.MSG_INVOCADOR_URL_ERRADA.getValue());
			String causa = mensaje;
			LOG.error("Error en <<validarToken>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
			NegocioExcepcion error = new NegocioExcepcion(mensaje);
			error.initCause(new Exception(causa));
			throw error;
		}

		if (parCompania != null) {
			String entidadBD = parCompania.getFields().get(GeneralParameterEnum.NITCOMPANIA.getName()).toString();
			entidadBD = entidadBD.replace(".", "");
			if (entidadBD.indexOf("-") > 0) {
				entidadBD = entidadBD.substring(0, entidadBD.indexOf("-"));
			}

			GenerarToken generarTokenBD = new GenerarToken(entidadBD, String.valueOf(claseServicio));
			tokenBD = generarTokenBD.Base64Hash();

			if (!tokenBD.equals(tokenGenerado)) {
				String mensaje = idioma.getString(InvocadorEnum.MSG_INVOCADOR_TOKENINVALIDO_COM.getValue());
				String causa = idioma.getString(InvocadorEnum.MSG_INVOCADOR_TOKENINVALIDO_COM_CAUSA.getValue());
				LOG.error("Error en <<validarToken>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
				NegocioExcepcion error = new NegocioExcepcion(mensaje);
				error.initCause(new Exception(causa));
				throw error;
			}

		} else {
			String mensaje = idioma.getString(InvocadorEnum.MSG_INVOCADOR_TOKENINVALIDO_COM.getValue());
			String causa = idioma.getString(InvocadorEnum.MSG_INVOCADOR_TOKENINVALIDO_COM_CAUSA.getValue());
			LOG.error("Error en <<validarToken>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
			NegocioExcepcion error = new NegocioExcepcion(mensaje);
			error.initCause(new Exception(causa));
			throw error;
		}

	}

	/**
	 * Devuelve los datos del inventario individual de una persona.
	 *
	 * @param token    Credenciales de autorizaci&oacute;n. Token asociado con a la
	 *                 consulta o solicitud de autoservicio.
	 * @param contexto Parametros especificados en el cuerpo de la petici&oacute;n.
	 * @return respuesta generada al llevar a cabo la petici&oacute;n POST.
	 */
	@GET
	@Path("/inventario")
	@Produces("application/json")
	public Response consultarInventarioIndividual(@HeaderParam(AUTHORIZATION) String token,
			@QueryParam("compania") String compania, @QueryParam("cedula") String cedula,
			@QueryParam("nitEntidad") String nitEntidad) {
		ParametrosEntradaInventario contexto = new ParametrosEntradaInventario(compania, cedula, nitEntidad);
		RespuestaApi respuestaApi = new RespuestaApi();
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(contexto.getCompania(), contexto.getNitEntidad(), token, 11);
		} catch (NegocioExcepcion e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED).entity(respuestaApi).build();
		}

		try {
			procesadorInventario.setContexto(contexto);
			procesadorInventario.procesar();
			respuestaApi = procesadorInventario.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();

		}

		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Suministra informacion sobre el inventario a cargo de un funcionario
	 * 
	 * @param token
	 * @param compania
	 * @param cedula
	 * @param nitEntidad
	 * @return
	 */
	@GET
	@Path("/info_inventario")
	@Produces("application/json")
	public Response InfoInventario(@HeaderParam(AUTHORIZATION) String token, @QueryParam("compania") String compania,
			@QueryParam("cedula") String cedula, @QueryParam("nitEntidad") String nitEntidad) {
		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.RESPONSABLE.getName(), cedula);
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesaInfoInventario.setContexto(contexto);
			procesaInfoInventario.procesar();
			respuestaApi = procesaInfoInventario.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(20);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();

		}

		return Response.ok().entity(respuestaApi).build();

	}

	@POST
	@Path("/contabiliza")
	@Consumes("application/json")
	@Produces("application/json")
	public Response generarContabiliza(@HeaderParam(AUTHORIZATION) String token, Comprobante contexto) {
		RespuestaApi respuestaApi = new RespuestaApi();
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(contexto.getCompania(), Long.toString(contexto.getNitEntidad()), token, 11);
		} catch (Exception e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED).entity(respuestaApi).build();
		}

		try {
			procesadorContabilizar.setContexto(contexto);
			procesadorContabilizar.procesar();
			respuestaApi = procesadorContabilizar.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();

		}

		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/existecomprobante")
	@Produces("application/json")
	public Response existeComprobante(@HeaderParam(AUTHORIZATION) String token,
			@QueryParam("nitEntidad") long nitEntidad, @QueryParam("compania") String compania,
			@QueryParam("anio") int anio, @QueryParam("tipo") String tipo, @QueryParam("numero") long numero) {
		RespuestaApi respuestaApi = new RespuestaApi();
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(compania, Long.toString(nitEntidad), token, 11);
		} catch (Exception e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED).entity(respuestaApi).build();
		}

		try {
			RequestManager rq = new RequestManager();
			Map<String, Object> par = new HashMap<>();
			par.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			par.put(GeneralParameterEnum.ANO.getName(), anio);
			par.put(GeneralParameterEnum.TIPO.getName(), tipo);
			par.put(GeneralParameterEnum.NUMERO.getName(), numero);

			Parameter parExiste;
			try {
				parExiste = rq.get(UrlServiceUtil.getUrlBeanById(CODIGO_URL_EXISTE_COM).getUrl(), par);
			} catch (SystemException e) {
				String mensaje = idioma.getString(InvocadorEnum.MSG_INVOCADOR_URL_ERRADA.getValue());
				String causa = mensaje;
				LOG.error("Error en <<validarToken>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
				NegocioExcepcion error = new NegocioExcepcion(mensaje);
				error.initCause(new Exception(causa));
				throw error;
			}

			if (parExiste != null) {
				int cantidad = Integer
						.parseInt(parExiste.getFields().get(GeneralParameterEnum.CUENTA.getName()).toString());

				respuestaApi.setCuerpo(cantidad != 0);

			}
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();

		}

		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/imprimecomprobante")
	@Produces("application/json")
	public Response imprimeComprobante(@HeaderParam(AUTHORIZATION) String token,
			@QueryParam("nitEntidad") long nitEntidad, @QueryParam("compania") String compania,
			@QueryParam("anio") int anio, @QueryParam("tipo") String tipo, @QueryParam("numero") String numero) {
		RespuestaApi respuestaApi = new RespuestaApi();

		Comprobante contexto = new Comprobante();
		contexto.setCompania(compania);
		contexto.setTipo(tipo);
		contexto.setAno(anio);
		contexto.setNumero(numero);
		contexto.setFormato(FORMATOS.PDF);

		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(compania, Long.toString(nitEntidad), token, 11);
		} catch (Exception e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED).entity(respuestaApi).build();
		}

		try {
			procesadorImprimeComprobante.setContexto(contexto);
			procesadorImprimeComprobante.procesar();
			respuestaApi = procesadorImprimeComprobante.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();

		}

		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/generaorganigrama")
	@Produces("application/json")
	public Response generaOrganigrama(@HeaderParam(AUTHORIZATION) String token,
			@QueryParam("nitEntidad") long nitEntidad, @QueryParam("compania") String compania) {

		RespuestaApi respuestaApi = new RespuestaApi();

		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(compania, Long.toString(nitEntidad), token, 11);
		} catch (Exception e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		try {
			procesadorGeneraOrganigrama.setContexto(compania);
			procesadorGeneraOrganigrama.procesar();
			respuestaApi = procesadorGeneraOrganigrama.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * @author gfigueredo Ticket 7703247 - Metodo encagado de obtener el excel de
	 *         afectaciones contables Modificacion: TICKET 7727003 -
	 * @return
	 */
	@GET
	@Path("/getExcelAfectaciones")
	@Produces("application/vnd.ms-excel")
	public Response getExcelAfectaciones(@HeaderParam(AUTHORIZATION) String token,
			@QueryParam("nitEntidad") long nitEntidad, @QueryParam("compania") String compania,
			@QueryParam("anio") int anio, @QueryParam("mes") String mes, @QueryParam("dia") String dia,
			@QueryParam("usuario") String usuario) {
		RespuestaApi respuestaApi = new RespuestaApi();

		ParametrosExcelAfectaciones contexto = new ParametrosExcelAfectaciones();
		contexto.setCompania(compania);
		contexto.setAnio(anio);
		contexto.setNitEntidad(String.valueOf(nitEntidad));
		if (String.valueOf(mes).toString().length() == 1) {
			contexto.setMes("0" + mes);
		} else {
			contexto.setMes(mes);
		}
		if (String.valueOf(dia).toString().length() == 1) {
			contexto.setDia("0" + dia);
		} else {
			contexto.setDia(dia);
		}
		contexto.setUsuario(usuario);

		try {
			validarTokenconBD(compania, Long.toString(nitEntidad), token, 11);
		} catch (Exception e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		try {
			procesadorExcelAfectaciones.setContexto(contexto);
			procesadorExcelAfectaciones.procesar();
			respuestaApi = procesadorExcelAfectaciones.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}

		Map<String, Object> valores = new TreeMap<>();
		String[] valoresResp = respuestaApi.getCuerpo().toString().split("<>");
		valores.put("nombre", valoresResp[2]);
		valores.put("ruta", valoresResp[1]);

		return respuesta(valores).build();

	}

	/**
	 * @author gfigueredo Ticket - 7706218 Metodo encagado de obtener el excel de
	 *         conceptos
	 * @return
	 */
	@GET
	@Path("/getExcelConceptos")
	@Produces("application/vnd.ms-excel")
	public Response getExcelConceptos() {

		GestionAutoservicio gestionAutoservicio = new GestionAutoservicio();
		Map<String, Object> valores = gestionAutoservicio.getRutaConceptos();

		return respuesta(valores).build();

	}

	/**
	 * @author gfigueredo Ticket 7706218 - Crea el archivo que será devuelto por el
	 *         webservice
	 * @param valores
	 * @return
	 */
	public ResponseBuilder respuesta(Map<String, Object> valores) {
		String nombre = (String) valores.get("nombre");
		String ruta = (String) valores.get("ruta") + "//" + nombre;

		File file = new File(ruta);

		if (valores.get("fechacreacion") != null) {
			String[] valoresNombrePartido = valores.get("nombre").toString().split(".xlsx");
			nombre = valoresNombrePartido[0] + "_" + valores.get("fechacreacion").toString() + ".xlsx";
		}

		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=" + nombre);
		return response;
	}

	/**
	 * Ejecucion del proceso de recaudo causacion
	 * 
	 * @return respuesta POST.
	 */

	@POST
	@Path("/cargarRecaudoCausacion")
	@Produces("application/json")
	public Response cargarRecaudoCausacion(@HeaderParam(AUTHORIZATION) String token,
			ParametrosRecuadoCausacion contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(contexto.getCompania(), contexto.getNitEntidad(), token, 11);
		} catch (Exception e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		try {
			procesadorCargarRecuadoCausacion.setContexto(contexto);
			procesadorCargarRecuadoCausacion.procesar();
			respuestaApi = procesadorCargarRecuadoCausacion.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}

		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Creacion comprobante Cnt
	 * 
	 * @return respuesta POST.
	 */
	@POST
	@Path("/comprobanteCnt")
	@Consumes("application/json")
	@Produces("application/json")
	public Response insertarComprobantesCnt(@HeaderParam(AUTHORIZATION) String token,
			ParametrosComprobanteCnt contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorPostComprobantesCnt.setContexto(contexto);
			procesadorPostComprobantesCnt.procesar();
			respuestaApi = procesadorPostComprobantesCnt.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Actualizacion comprobante Cnt
	 * 
	 * @return respuesta PUT.
	 */
	@PUT
	@Path("/comprobanteCnt")
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizarComprobantesCnt(@HeaderParam(AUTHORIZATION) String token,
			ParametrosComprobanteCnt contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorPutComprobantesCnt.setContexto(contexto);
			procesadorPutComprobantesCnt.procesar();
			respuestaApi = procesadorPutComprobantesCnt.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Actualizacion comprobante Cnt
	 * 
	 * @return respuesta PUT.
	 */
	@PUT
	@Path("/comprobanteCntTexto")
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizarComprobantesCntTexto(@HeaderParam(AUTHORIZATION) String token,
			ParametrosComprobanteCnt contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorPutComprobantesCntTexto.setContexto(contexto);
			procesadorPutComprobantesCntTexto.procesar();
			respuestaApi = procesadorPutComprobantesCntTexto.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Actualizacion comprobante Cnt Anulado
	 * 
	 * @return respuesta PUT.
	 */
	@PUT
	@Path("/comprobanteCntAnulado")
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizarComprobantesCntAnulado(@HeaderParam(AUTHORIZATION) String token,
			ParametrosComprobanteCnt contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorPutComprobantesCntAnulado.setContexto(contexto);
			procesadorPutComprobantesCntAnulado.procesar();
			respuestaApi = procesadorPutComprobantesCntAnulado.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Eliminacion comprobante Cnt
	 * 
	 * @return respuesta DELETE.
	 */
	@DELETE
	@Path("/comprobanteCnt")
	@Consumes("application/json")
	@Produces("application/json")
	public Response eliminarComprobantesCnt(@HeaderParam(AUTHORIZATION) String token,
			ParametrosComprobanteCnt contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorDeleteComprobantesCnt.setContexto(contexto);
			procesadorDeleteComprobantesCnt.procesar();
			respuestaApi = procesadorDeleteComprobantesCnt.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Creacion detalle comprobante Cnt
	 * 
	 * @return respuesta POST.
	 */
	@POST
	@Path("/detalleComprobanteCnt")
	@Consumes("application/json")
	@Produces("application/json")
	public Response insertarDetalleComprobantesCnt(@HeaderParam(AUTHORIZATION) String token,
			ParametrosDetalleComprobanteCnt contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorPostDetalleComprobantesCnt.setContexto(contexto);
			procesadorPostDetalleComprobantesCnt.procesar();
			respuestaApi = procesadorPostDetalleComprobantesCnt.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Actualizacion detalle comprobante Cnt
	 * 
	 * @return respuesta PUT.
	 */
	@PUT
	@Path("/detalleComprobanteCnt")
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizarDetalleComprobantesCnt(@HeaderParam(AUTHORIZATION) String token,
			ParametrosDetalleComprobanteCnt contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorPutDetalleComprobantesCnt.setContexto(contexto);
			procesadorPutDetalleComprobantesCnt.procesar();
			respuestaApi = procesadorPutDetalleComprobantesCnt.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Eliminacion detalle comprobante Cnt
	 * 
	 * @return respuesta DELETE.
	 */
	@DELETE
	@Path("/detalleComprobanteCnt")
	@Consumes("application/json")
	@Produces("application/json")
	public Response eliminarDetalleComprobantesCnt(@HeaderParam(AUTHORIZATION) String token,
			ParametrosDetalleComprobanteCnt contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorDeleteDetalleComprobantesCnt.setContexto(contexto);
			procesadorDeleteDetalleComprobantesCnt.procesar();
			respuestaApi = procesadorDeleteDetalleComprobantesCnt.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/generaInventarioDependencia")
	@Produces("application/json")
	public Response generaInventarioDependencia(@HeaderParam(AUTHORIZATION) String token,
			@QueryParam("nitEntidad") long nitEntidad, @QueryParam("compania") String compania,
			@QueryParam("codigoEquivalente") String codigoEquivalente) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put("CODIGO", codigoEquivalente);

		RespuestaApi respuestaApi = new RespuestaApi();

		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(compania, Long.toString(nitEntidad), token, 11);
		} catch (Exception e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		try {
			procesadorGeneraInventarioDependencia.setContexto(contexto);
			procesadorGeneraInventarioDependencia.procesar();
			respuestaApi = procesadorGeneraInventarioDependencia.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/consultarFacturaGeneral")
	@Produces("application/json")
	public Response consultarFacturaGeneral(@QueryParam("referencia") String referencia) {

		String factura = referencia.substring(referencia.length() - 10, referencia.length());
		int tipoRecaudo = Integer.parseInt(referencia.substring(0, 5));

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.FACTURA.getName(), factura);
		contexto.put("TIPORECAUDO", tipoRecaudo);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorConsultarFacturaGeneral.setContexto(contexto);
			procesadorConsultarFacturaGeneral.procesar();
			respuestaApi = procesadorConsultarFacturaGeneral.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}

	/**
	 * Actualizacion detalle comprobante Cnt
	 * 
	 * @return respuesta POST.
	 */
	@POST
	@Path("/pagarFacturaGeneral")
	@Consumes("application/json")
	@Produces("application/json")
	public Response pagarFacturaGeneral(@HeaderParam(AUTHORIZATION) String token,
			ParametrosPagarFacturaGeneral contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorPagarFacturaGeneral.setContexto(contexto);
			procesadorPagarFacturaGeneral.procesar();
			respuestaApi = procesadorPagarFacturaGeneral.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/cargarTercero")
	@Produces("application/json")
	public Response cargarTercero(@QueryParam("compania") String compania,
			@QueryParam("terceroInicial") String terceroInicial, @QueryParam("terceroFinal") String terceroFinal) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put("TERCEROINI", terceroInicial);
		contexto.put("TERCEROFIN", terceroFinal);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorCargarTercero.setContexto(contexto);
			procesadorCargarTercero.procesar();
			respuestaApi = procesadorCargarTercero.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}

	@GET
	@Path("/cargarComprobanteCnt")
	@Produces("application/json")
	public Response cargarComprobanteCnt(@QueryParam("compania") String compania, @QueryParam("tipo") String tipo,
			@QueryParam("terceroInicial") String terceroInicial, @QueryParam("sucursalInicial") String sucursalInicial,
			@QueryParam("terceroFinal") String terceroFinal, @QueryParam("sucursalFinal") String sucursalFinal) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.TIPO.getName(), tipo);
		contexto.put("TERCEROI", terceroInicial);
		contexto.put("SUCURSALI", sucursalInicial);
		contexto.put("TERCEROF", terceroFinal);
		contexto.put("SUCURSALF", sucursalFinal);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorCargarComprobanteCnt.setContexto(contexto);
			procesadorCargarComprobanteCnt.procesar();
			respuestaApi = procesadorCargarComprobanteCnt.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}

	@GET
	@Path("/cargarDetalleComprobanteCnt")
	@Produces("application/json")
	public Response cargarDetalleComprobanteCnt(@QueryParam("compania") String compania,
			@QueryParam("tipo") String tipo, @QueryParam("terceroInicial") String terceroInicial,
			@QueryParam("sucursalInicial") String sucursalInicial, @QueryParam("terceroFinal") String terceroFinal,
			@QueryParam("sucursalFinal") String sucursalFinal) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.TIPO.getName(), tipo);
		contexto.put("TERCEROI", terceroInicial);
		contexto.put("SUCURSALI", sucursalInicial);
		contexto.put("TERCEROF", terceroFinal);
		contexto.put("SUCURSALF", sucursalFinal);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorCargarDetalleComprobanteCnt.setContexto(contexto);
			procesadorCargarDetalleComprobanteCnt.procesar();
			respuestaApi = procesadorCargarDetalleComprobanteCnt.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}

	@GET
	@Path("/cargarConsecutivoComprobanteCnt")
	@Produces("application/json")
	public Response cargarConsecutivoComprobanteCnt(@QueryParam("compania") String compania,
			@QueryParam("tipo") String tipo, @QueryParam("anio") String anio) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.TIPO.getName(), tipo);
		contexto.put(GeneralParameterEnum.ANIO.getName(), anio);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorCargarConsecutivoComprobanteCnt.setContexto(contexto);
			procesadorCargarConsecutivoComprobanteCnt.procesar();
			respuestaApi = procesadorCargarConsecutivoComprobanteCnt.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}

	@GET
	@Path("/validapagoidsn")
	@Produces("application/json")
	public Response validaPagoIdsn(@HeaderParam(AUTHORIZATION) String token, @QueryParam("nitEntidad") long nitEntidad,
			@QueryParam("compania") String compania, @QueryParam("tipo") String tipo,
			@QueryParam("nroDocumento") String nroDocumento, @QueryParam("tercero") String tercero) {
		RespuestaApi respuestaApi = new RespuestaApi();

		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(compania, Long.toString(nitEntidad), token, 11);
		} catch (Exception e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED).entity(respuestaApi).build();
		}

		try {
			Map<String, Object> par = new HashMap<>();
			par.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			par.put(GeneralParameterEnum.TIPO.getName(), tipo);
			par.put("NRODOCUMENTO", nroDocumento);
			par.put("TERCERO", tercero);

			List<Registro> parametros = new ArrayList<>();
			parametros = ejecutarSelectMultiRegistro(CODIGO_VALIDA_PAGO_IDSN, par);
			if (!parametros.isEmpty()) {
				List<SalidaValidaIdsn> salida = new ArrayList<>();
				SalidaValidaIdsn registroSalida;
				for (Registro registro : parametros) {
					registroSalida = new SalidaValidaIdsn();
					registroSalida.setTipo((String) registro.getCampos().get("TIPO"));
					registroSalida.setComprobante((Long) registro.getCampos().get("COMPROBANTE"));
					registroSalida.setTipocdp((String) registro.getCampos().get("DIS_TIPO"));
					registroSalida.setCdp((Long) registro.getCampos().get("DIS"));
					registroSalida.setTiporp((String) registro.getCampos().get("RES_TIPO"));
					registroSalida.setRp((Long) registro.getCampos().get("RES"));
					try {
						registroSalida.setFecha(
								SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get("FECHA")));
					} catch (ParseException e) {
						String mensaje = "No se pudo formatear le fecha del comprobante";
						String causa = mensaje;
						LOG.error("Error en <<Consultar Comprobante>> ->> mensaje ->> {} / causa ->> {}", mensaje,
								causa);
						NegocioExcepcion error = new NegocioExcepcion(mensaje);
						error.initCause(new Exception(causa));
						throw error;
					}
					salida.add(registroSalida);
				}
				respuestaApi.setCuerpo(salida);
			} else {
				String mensaje = "No se Encontraron datos con los parametros dados";
				String causa = mensaje;
				LOG.error("Error en <<Consultar Comprobante>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
				NegocioExcepcion error = new NegocioExcepcion(mensaje);
				error.initCause(new Exception(causa));
				throw error;
			}
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();

		}

		return Response.ok().entity(respuestaApi).build();

	}

	@GET
	@Path("/listageneros")
	@Produces("application/json")
	public Response listaGeneros() {
		RespuestaApi respuestaApi = new RespuestaApi();
		try {

			procesadorCargarGeneros.procesar();
			respuestaApi = procesadorCargarGeneros.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/estadoperiodo")
	@Produces("application/json")
	public Response cargarEstadoPeriodo(@QueryParam("compania") String compania, @QueryParam("ano") String anio,
			@QueryParam("mes") String mes, @QueryParam("periodo") String periodo,
			@QueryParam("usuario") String usuario) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.MES.getName(), mes);
		contexto.put(GeneralParameterEnum.ANO.getName(), anio);
		contexto.put(GeneralParameterEnum.PERIODO.getName(), periodo);
		contexto.put(GeneralParameterEnum.USUARIO.getName(), usuario);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorCargarEstadoPeriodo.setContexto(contexto);
			procesadorCargarEstadoPeriodo.procesar();
			respuestaApi = procesadorCargarEstadoPeriodo.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}

	@GET
	@Path("/periodoscerrados")
	@Produces("application/json")
	public Response cargarPeriodosCerrados(@QueryParam("ano") String anio, @QueryParam("mes") String mes,
			@QueryParam("cedula") String nroDocumento) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.MES.getName(), mes);
		contexto.put(GeneralParameterEnum.ANO.getName(), anio);
		contexto.put("NUMERO_DOC", nroDocumento);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesaCargarPeriodosCerrados.setContexto(contexto);
			procesaCargarPeriodosCerrados.procesar();
			respuestaApi = procesaCargarPeriodosCerrados.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}

	/**
	 * 
	 **/
	@GET
	@Path("/listatipotramites")
	@Produces("application/json")
	public Response cargarTipoTramite() {
		Map<String, Object> contexto = new TreeMap<>();
		contexto.put("PROCESOPQR", PROCESOPQR);
		RespuestaApi respuestaApi = new RespuestaApi();
		try {
			procesaCargarTipoTramites.setContexto(contexto);
			procesaCargarTipoTramites.procesar();
			respuestaApi = procesaCargarTipoTramites.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Actualizacion liquidacion Factura
	 * 
	 * @return respuesta POST.
	 */
	@POST
	@Path("/liquidacionfactura")
	@Consumes("application/json")
	@Produces("application/json")
	public Response pagarFacturaGeneral(ParametrosLiquidacionFactura contexto) {

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesaLiquidacionFactura.setContexto(contexto);
			procesaLiquidacionFactura.procesar();
			respuestaApi = procesaLiquidacionFactura.getResultado();
		} catch (NegocioExcepcion e) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	/**
	 * Ejecuta un servicio DSS que corresponde a una consulta que devuelve multiples
	 * registros.
	 * 
	 * @param idServicio identificador del servicio
	 * @param params     parametros para resolver la consulta
	 * @return lista de registros devueltos por la consulta
	 * @throws NegocioExcepcion
	 * 
	 *                          en caso de que se presenten problemas al ejecutar el
	 *                          servicio DSS
	 */
	private List<Registro> ejecutarSelectMultiRegistro(String idServicio, Map<String, Object> params)
			throws NegocioExcepcion {
		UrlServiceUtil urlservice = UrlServiceUtil.getInstance();
		List<Parameter> parameters;
		try {
			String url = urlservice.getUrlServiceByUrlByEnumID(idServicio).getUrl();
			RequestManager requestManager = new RequestManager();
			parameters = requestManager.getList(url, params);
		} catch (Exception e) {
			String mensaje = "Error al consultar la Url con la información";
			String causa = mensaje;
			LOG.error("Error en <<Consultar Comprobante>> ->> mensaje ->> {} / causa ->> {}", mensaje, mensaje);
			NegocioExcepcion error = new NegocioExcepcion(mensaje);
			error.initCause(new Exception(causa));
			throw error;
		}
		return RegistroConverter.toListRegistro(parameters);
	}

	/**
	 * 
	 * 
	 **/
	@GET
	@Path("/listaPlanContable")
	@Produces("application/json")
	public Response registroPlanContable(@QueryParam("compania") String compania, @QueryParam("ano") String anio,
			@QueryParam("codigo") String codigo) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANO.getName(), anio);
		contexto.put(GeneralParameterEnum.CODIGO.getName(), codigo);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorCargarPlanContable.setContexto(contexto);
			procesadorCargarPlanContable.procesar();
			respuestaApi = procesadorCargarPlanContable.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}

	@GET
	@Path("/listatipopersona")
	@Produces("application/json")
	public Response listaTipoPersona() {
		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralAutoservicioEnum.CATEGORIA.getValue(), "22");
		RespuestaApi respuestaApi = new RespuestaApi();
		try {

			procesadorCargarTipoPersona.setContexto(contexto);
			procesadorCargarTipoPersona.procesar();
			respuestaApi = procesadorCargarTipoPersona.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/listatipopoblacion")
	@Produces("application/json")
	public Response listaTipoPobl() {
		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralAutoservicioEnum.CATEGORIA.getValue(), "20");
		RespuestaApi respuestaApi = new RespuestaApi();
		try {

			procesadorCargarTipoPobl.setContexto(contexto);
			procesadorCargarTipoPobl.procesar();
			respuestaApi = procesadorCargarTipoPobl.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/listarangoedad")
	@Produces("application/json")
	public Response listaRangoEdad() {
		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralAutoservicioEnum.CATEGORIA.getValue(), "23");
		RespuestaApi respuestaApi = new RespuestaApi();
		try {

			procesadorCargarRangoEdad.setContexto(contexto);
			procesadorCargarRangoEdad.procesar();
			respuestaApi = procesadorCargarRangoEdad.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/listavulnerabilidad")
	@Produces("application/json")
	public Response listaVulnerabilidad() {
		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralAutoservicioEnum.CATEGORIA.getValue(), "25");
		RespuestaApi respuestaApi = new RespuestaApi();
		try {

			procesadorCargarVulneravilidad.setContexto(contexto);
			procesadorCargarVulneravilidad.procesar();
			respuestaApi = procesadorCargarVulneravilidad.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/listaocupacion")
	@Produces("application/json")
	public Response listaOcupacion() {
		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralAutoservicioEnum.CATEGORIA.getValue(), "26");
		RespuestaApi respuestaApi = new RespuestaApi();
		try {

			procesadorCargarOcupacion.setContexto(contexto);
			procesadorCargarOcupacion.procesar();
			respuestaApi = procesadorCargarOcupacion.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}

	@GET
	@Path("/estadoMesContable")
	@Produces("application/json")
	public Response estadoMesContable(@QueryParam("compania") String compania, @QueryParam("anio") int anio,
			@QueryParam("mes") int mes, @QueryParam("dia") int dia) {
		RespuestaApi respuestaApi = new RespuestaApi();
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);

		try {
			RequestManager rq = new RequestManager();
			Map<String, Object> par = new HashMap<>();
			par.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			par.put(GeneralParameterEnum.ANIO.getName(), anio);
			par.put(GeneralParameterEnum.MES.getName(), mes);
			par.put(GeneralParameterEnum.DIA.getName(), dia);
			par.put("APLICACION", 1);// contabilidad
			par.put("PROCESO", 1);// contable

			Parameter parExiste;
			String resultado = "";
			try {
				parExiste = rq.get(UrlServiceUtil.getUrlBeanById(CODIGO_URL_ESTADO_CONTABLE).getUrl(), par);
			} catch (SystemException e) {
				String mensaje = idioma.getString(InvocadorEnum.MSG_INVOCADOR_URL_ERRADA.getValue());
				String causa = mensaje;
				LOG.error("Error en <<validarToken>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
				NegocioExcepcion error = new NegocioExcepcion(mensaje);
				error.initCause(new Exception(causa));
				throw error;
			}

			if (parExiste != null) {
				int cantidad = Integer.parseInt(parExiste.getFields().get("ESTADO").toString());
				if (cantidad != 0) {
					resultado = "ABIERTO";
				} else {
					resultado = "CERRADO";
				}

				respuestaApi.setCuerpo(resultado);

			}
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();

		}

		return Response.ok().entity(respuestaApi).build();
	}

//	//HU1 7733460 - Vista a Informes de base de datos - WebService Tipo Rest
	@GET
	@Path("/informesGobNar")
	@Produces("application/json")
	public Response informesGobNar(@QueryParam("compania") String compania, @QueryParam("tipo_cpte") String tipo_cpte, @QueryParam("anio") int anio,
			@QueryParam("mes") int mes, @QueryParam("numinforme") int numinforme) {

		RespuestaApi respuestaApi = new RespuestaApi();
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);

		try {
			Map<String, Object> par = new HashMap<>();
			par.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			par.put(GeneralParameterEnum.ANIO.getName(), anio);
			par.put(GeneralParameterEnum.MES.getName(), mes);
			par.put(GeneralParameterEnum.NUMINFORME.getName(), numinforme);
			par.put(GeneralParameterEnum.TIPO_CPTE.getName(), tipo_cpte);

			List<Registro> parametros = new ArrayList<>();

			switch (numinforme) {
			case 1:

				parametros = ejecutarSelectMultiRegistro(EJECUCION_PRESUPUESTAL_DE_GASTOS_ACUMULADA, par);
				if (!parametros.isEmpty()) {
					List<SalidaInformes1GobNar> salida = new ArrayList<>();
					SalidaInformes1GobNar registroSalida;
					for (Registro registro : parametros) {

						registroSalida = new SalidaInformes1GobNar();
						registroSalida.setCodigocuenta(SysmanFunciones.toString(registro.getCampos().get("CODIGO")));
						registroSalida.setNombrerubro(SysmanFunciones.toString(registro.getCampos().get("NOMBRE")));
						registroSalida.setMovimiento(SysmanFunciones.toString(registro.getCampos().get("MOVIMIENTO")));
						registroSalida.setDestino(SysmanFunciones.toString(registro.getCampos().get("DESTINO")));
						registroSalida.setBpid(SysmanFunciones.toString(registro.getCampos().get("BPID").toString()));
						registroSalida.setApropiacioninicial(SysmanFunciones.toString( registro.getCampos().get("APROPIACIONINICIAL")));
						registroSalida.setAdicion(SysmanFunciones.toString( registro.getCampos().get("ADICION")));
						registroSalida.setReduccion(SysmanFunciones.toString( registro.getCampos().get("REDUCCION")));
						registroSalida.setCredito(SysmanFunciones.toString( registro.getCampos().get("CREDITO")));
						registroSalida.setContracredito(SysmanFunciones.toString( registro.getCampos().get("CONTRACREDITO")));
						registroSalida.setAplazamiento(SysmanFunciones.toString( registro.getCampos().get("APLAZAMIENTOS")));
						registroSalida.setDesplazaminento(SysmanFunciones.toString( registro.getCampos().get("DESAPLAZAMIENTOS")));
						registroSalida.setApropiacionvigente(SysmanFunciones.toString( registro.getCampos().get("APROPIACIONVIGENTE")));
						registroSalida.setDisponibilidades(SysmanFunciones.toString( registro.getCampos().get("DISPONIBILIDAD")));
						registroSalida.setSaldodisponible(SysmanFunciones.toString( registro.getCampos().get("SALDODISPONIBLE")));
						registroSalida.setCompromisos(SysmanFunciones.toString( registro.getCampos().get("COMPROMISOS")));
						registroSalida.setDisponibilidadesabiertas(SysmanFunciones.toString( registro.getCampos().get("DISPONIBILIDADESABIERTAS")));
						registroSalida.setObligacion(SysmanFunciones.toString( registro.getCampos().get("OBLIGACION")));
						registroSalida.setPagos(SysmanFunciones.toString( registro.getCampos().get("PAGOS")));
						registroSalida.setObligacionesporpagar(SysmanFunciones.toString( registro.getCampos().get("OBLIGACIONESPORPAGAR")));
						salida.add(registroSalida);
					}
					respuestaApi.setCuerpo(salida);
				} else {
					String mensaje = "No se Encontraron datos con los parametros dados";
					String causa = mensaje;
					LOG.error("Error en <<Consultar Informe>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
					NegocioExcepcion error = new NegocioExcepcion(mensaje);
					error.initCause(new Exception(causa));
					throw error;
				}

				break;
			case 2:

				parametros = ejecutarSelectMultiRegistro(AUXILIAR_PRESUPUESTAL_POR_CUENTAS, par);
				if (!parametros.isEmpty()) {
					List<SalidaInformes2GobNar> salida = new ArrayList<>();
					SalidaInformes2GobNar registroSalida = null;
					for (Registro registro : parametros) {
						registroSalida = new SalidaInformes2GobNar();
						registroSalida.setNumero(SysmanFunciones.toString(registro.getCampos().get("NUMERO")));
						registroSalida.setNombrepred(SysmanFunciones.toString(registro.getCampos().get("NOMBRE_PRED")));
						registroSalida.setIdprede(SysmanFunciones.toString(registro.getCampos().get("ID_PREDE")));
						registroSalida.setNombreplan(SysmanFunciones.toString(registro.getCampos().get("NOMBREPLAN")));
						registroSalida.setRubro(SysmanFunciones.toString(registro.getCampos().get("RUBRO")));
						registroSalida.setFecha(SysmanFunciones.toString(registro.getCampos().get("FECHA")));
						registroSalida.setTipocpte(SysmanFunciones.toString(registro.getCampos().get("TIPO_CPTE")));
						registroSalida.setTercero(SysmanFunciones.toString(registro.getCampos().get("TERCERO")));
						registroSalida.setNombretercero(SysmanFunciones.toString(registro.getCampos().get("NOMBRETERCERO")));
						registroSalida.setDescripcion(SysmanFunciones.toString(registro.getCampos().get("DESCRIPCION")));
						registroSalida.setNrodocumento(SysmanFunciones.toString(registro.getCampos().get("NRO_DOCUMENTO")!= null ? registro.getCampos().get("NRO_DOCUMENTO").toString() : ""));
						registroSalida.setValordebito(SysmanFunciones.toString( registro.getCampos().get("VALORDEBITO")));
						registroSalida.setValorcredito(SysmanFunciones.toString( registro.getCampos().get("VALORCREDITO")));
						registroSalida.setDebitoafectado(SysmanFunciones.toString(registro.getCampos().get("DEBITO_AFECTADO")));
						registroSalida.setCreditoafectado(SysmanFunciones.toString(registro.getCampos().get("CREDITO_AFECTADO")));
						registroSalida.setModificaciondebito(SysmanFunciones.toString( registro.getCampos().get("MODIFICACION_DEBITO")));
						registroSalida.setModificacioncredito(SysmanFunciones.toString( registro.getCampos().get("MODIFICACION_CREDITO")));
						registroSalida.setSaldoporejecutaresp(SysmanFunciones.toString( registro.getCampos().get("SALDOPOREJECUTARESP")));
						registroSalida.setTipocpteafect(SysmanFunciones.toString(registro.getCampos().get("TIPO_CPTE_AFECT")!= null ? registro.getCampos().get("TIPO_CPTE_AFECT").toString() : ""));
						registroSalida.setCmpteafectado(SysmanFunciones.toString(registro.getCampos().get("CMPTE_AFECTADO")!= null ? registro.getCampos().get("CMPTE_AFECTADO").toString() : ""));
						salida.add(registroSalida);
					}
					respuestaApi.setCuerpo(salida);
				} else {
					String mensaje = "No se Encontraron datos con los parametros dados";
					String causa = mensaje;
					LOG.error("Error en <<Consultar Informe>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
					NegocioExcepcion error = new NegocioExcepcion(mensaje);
					error.initCause(new Exception(causa));
					throw error;
				}

				break;
			case 3:

				parametros = ejecutarSelectMultiRegistro(LISTADO_DE_TERCEROS, par);
				if (!parametros.isEmpty()) {
					List<SalidaInformes3GobNar> salida = new ArrayList<>();
					SalidaInformes3GobNar registroSalida;
					for (Registro registro : parametros) {

						registroSalida = new SalidaInformes3GobNar();
						registroSalida.setNit(SysmanFunciones.toString(registro.getCampos().get("NIT")));
						registroSalida.setTerceronombre(SysmanFunciones.toString(registro.getCampos().get("TERCERONOMBRE")!= null ? registro.getCampos().get("TERCERONOMBRE").toString() : ""));
						registroSalida.setDireccion(SysmanFunciones.toString(registro.getCampos().get("DIRECCION")!= null ? registro.getCampos().get("DIRECCION").toString() : ""));
						registroSalida.setNombreciudad(SysmanFunciones.toString(registro.getCampos().get("NOMBRECIUDAD")!= null ? registro.getCampos().get("NOMBRECIUDAD").toString() : ""));
						registroSalida.setDepartamento(SysmanFunciones.toString(registro.getCampos().get("DEPARTAMENTO")!= null ? registro.getCampos().get("DEPARTAMENTO").toString() : ""));
						registroSalida.setTelefonos(SysmanFunciones.toString(registro.getCampos().get("TELEFONOS")!= null ? registro.getCampos().get("TELEFONOS").toString() : ""));
						registroSalida.setFax(SysmanFunciones.toString(registro.getCampos().get("FAX")!= null ? registro.getCampos().get("FAX").toString() : ""));
						registroSalida.setDireccionemail(SysmanFunciones.toString(registro.getCampos().get("DIRECCIONEMAIL")!= null ? registro.getCampos().get("DIRECCIONEMAIL").toString() : ""));
						registroSalida.setClase(SysmanFunciones.toString(registro.getCampos().get("CLASE")!= null ? registro.getCampos().get("CLASE").toString() : ""));
						registroSalida.setBanco(SysmanFunciones.toString(registro.getCampos().get("BANCO")!= null ? registro.getCampos().get("BANCO").toString() : ""));
						registroSalida.setCuenta(SysmanFunciones.toString(registro.getCampos().get("CUENTA")!= null ? registro.getCampos().get("CUENTA").toString() : ""));
						registroSalida.setTipocuenta(SysmanFunciones.toString(registro.getCampos().get("TIPOCUENTA")!= null ? registro.getCampos().get("TIPOCUENTA").toString() : ""));
						registroSalida.setActiva(SysmanFunciones.toString(registro.getCampos().get("ACTIVA")!= null ? registro.getCampos().get("ACTIVA").toString() : ""));
						salida.add(registroSalida);
					}
					respuestaApi.setCuerpo(salida);
				} else {
					String mensaje = "No se Encontraron datos con los parametros dados";
					String causa = mensaje;
					LOG.error("Error en <<Consultar Informe>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
					NegocioExcepcion error = new NegocioExcepcion(mensaje);
					error.initCause(new Exception(causa));
					throw error;
				}

				break;
			case 4:
				parametros = ejecutarSelectMultiRegistro(PLAN_PRESUPUESTAL, par);
				if (!parametros.isEmpty()) {
					List<SalidaInformes4GobNar> salida = new ArrayList<>();
					SalidaInformes4GobNar registroSalida = null;					
					for (Registro registro : parametros) {
						registroSalida = new SalidaInformes4GobNar();
						registroSalida.setCodigo(SysmanFunciones.toString(registro.getCampos().get("CODIGO")));
						registroSalida.setNombre(SysmanFunciones.toString(registro.getCampos().get("NOMBRE")));
						registroSalida.setDestino(SysmanFunciones.toString(registro.getCampos().get("DESTINO")));
						registroSalida.setNaturaleza(SysmanFunciones.toString(registro.getCampos().get("NATURALEZA")));
						registroSalida.setMovimiento(SysmanFunciones.toString(registro.getCampos().get("MOVIMIENTO")));
						registroSalida.setTipovigencia(SysmanFunciones.toString(registro.getCampos().get("TIPOVIGENCIA")));
						registroSalida.setSector(SysmanFunciones.toString(registro.getCampos().get("SECTOR")!= null ? registro.getCampos().get("SECTOR").toString() : ""));
						registroSalida.setPrograma(SysmanFunciones.toString(registro.getCampos().get("PROGRAMA")!= null ? registro.getCampos().get("PROGRAMA").toString() : ""));
						registroSalida.setSubPrograma(SysmanFunciones.toString(registro.getCampos().get("SUBPROGRAMA")!= null ? registro.getCampos().get("SUBPROGRAMA").toString() : ""));
						registroSalida.setCodigoProducto(SysmanFunciones.toString(registro.getCampos().get("CODIGOPRODUCTO")!= null ? registro.getCampos().get("CODIGOPRODUCTO").toString() : ""));
						registroSalida.setCodigoBPIN(SysmanFunciones.toString(registro.getCampos().get("CODIGOBPIN")!= null ? registro.getCampos().get("CODIGOBPIN").toString() : ""));
						registroSalida.setCodigoCCPET(SysmanFunciones.toString(registro.getCampos().get("CODIGOCCPET")!= null ? registro.getCampos().get("CODIGOCCPET").toString() : ""));
						registroSalida.setCodigoCPCDANE(SysmanFunciones.toString(registro.getCampos().get("CODIGOCPCDANE")!= null ? registro.getCampos().get("CODIGOCPCDANE").toString() : ""));
						registroSalida.setCodigoUnidadEjecutora(SysmanFunciones.toString(registro.getCampos().get("CODIGOUNIDADEJE")!= null ? registro.getCampos().get("CODIGOUNIDADEJE").toString() : ""));
						registroSalida.setCodigoFuente(SysmanFunciones.toString(registro.getCampos().get("CODIGOFUENTE")!= null ? registro.getCampos().get("CODIGOFUENTE").toString() : ""));
						registroSalida.setCodigoCCPETRegalias(SysmanFunciones.toString(registro.getCampos().get("CODIGOCCPETREGA")!= null ? registro.getCampos().get("CODIGOCCPETREGA").toString() : ""));
						registroSalida.setPoliticaPublica(SysmanFunciones.toString(registro.getCampos().get("POLITCA_PUBLICA_CUIPO")!= null ? registro.getCampos().get("POLITCA_PUBLICA_CUIPO").toString() : ""));
						registroSalida.setDetalleSectorial(SysmanFunciones.toString(registro.getCampos().get("DETALLE_SECTORIAL")!= null ? registro.getCampos().get("DETALLE_SECTORIAL").toString() : ""));
						registroSalida.setTipoRecurso(SysmanFunciones.toString(registro.getCampos().get("TIPO_RECURSO")!= null ? registro.getCampos().get("TIPO_RECURSO").toString() : ""));
						registroSalida.setCodigoSIA(SysmanFunciones.toString(registro.getCampos().get("CODIGOSIA")!= null ? registro.getCampos().get("CODIGOSIA").toString() : ""));
						registroSalida.setDependencia(SysmanFunciones.toString(registro.getCampos().get("DEPENDENCIAASOCIADA")!= null ? registro.getCampos().get("DEPENDENCIAASOCIADA").toString() : ""));
						registroSalida.setNombreDependencia(SysmanFunciones.toString(registro.getCampos().get("NOMBRE_DEPENDENCIA")!= null ? registro.getCampos().get("NOMBRE_DEPENDENCIA").toString() : ""));
						registroSalida.setCodigoEquiv(SysmanFunciones.toString(registro.getCampos().get("CODIGO_EQUIV")!= null ? registro.getCampos().get("CODIGO_EQUIV").toString() : "")); //JM CC 3155

						salida.add(registroSalida);
					}
					respuestaApi.setCuerpo(salida);
				} else {
					String mensaje = "No se Encontraron datos con los parametros dados";
					String causa = mensaje;
					LOG.error("Error en <<Consultar Informe>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
					NegocioExcepcion error = new NegocioExcepcion(mensaje);
					error.initCause(new Exception(causa));
					throw error;
				}

				break;
			case 5:
				parametros = ejecutarSelectMultiRegistro(PERSONAL_ACTIVO_DE_NOMINA, par);
				if (!parametros.isEmpty()) {
					List<SalidaInformes5GobNar> salida = new ArrayList<>();
					SalidaInformes5GobNar registroSalida;
					for (Registro registro : parametros) {
						registroSalida = new SalidaInformes5GobNar();
						registroSalida.setIddeempleado(SysmanFunciones.toString(registro.getCampos().get("ID_DE_EMPLEADO")));
						registroSalida.setApellido1(SysmanFunciones.toString(registro.getCampos().get("APELLIDO1")!= null ? registro.getCampos().get("APELLIDO1").toString() : ""));
						registroSalida.setApellido2(SysmanFunciones.toString(registro.getCampos().get("APELLIDO2")!= null ? registro.getCampos().get("APELLIDO2").toString() : ""));
						registroSalida.setNombres(SysmanFunciones.toString(registro.getCampos().get("NOMBRES")));
						registroSalida.setNumerodcto(SysmanFunciones.toString(registro.getCampos().get("NUMERO_DCTO")));
						registroSalida.setExpedida(SysmanFunciones.toString(registro.getCampos().get("EXPEDIDA")!= null ? registro.getCampos().get("EXPEDIDA").toString() : ""));
						registroSalida.setFechancto(SysmanFunciones.toString(registro.getCampos().get("FECHANCTO")!= null ? registro.getCampos().get("FECHANCTO").toString() : ""));
						registroSalida.setFechadeingreso(SysmanFunciones.toString(registro.getCampos().get("FECHA_DE_INGRESO")));
						registroSalida.setFechaderetiro(SysmanFunciones.toString(registro.getCampos().get("FECHA_DE_RETIRO")!= null ? registro.getCampos().get("FECHA_DE_RETIRO").toString() : ""));
						registroSalida.setIddecargo(SysmanFunciones.toString(registro.getCampos().get("ID_DE_CARGO")));
						registroSalida.setNombredelcargo(SysmanFunciones.toString(registro.getCampos().get("NOMBRE_DEL_CARGO")));
						registroSalida.setIddecategoria(SysmanFunciones.toString(registro.getCampos().get("ID_DE_CATEGORIA")));
						registroSalida.setNombrecategoria(SysmanFunciones.toString(registro.getCampos().get("NOMBRE_CATEGORIA")));
						registroSalida.setEscalafon(SysmanFunciones.toString(registro.getCampos().get("ESCALAFON")));
						registroSalida.setNombreescalafon(SysmanFunciones.toString(registro.getCampos().get("NOMBREESCALAFON")));
						registroSalida.setGrado(SysmanFunciones.toString(registro.getCampos().get("GRADO")));
						registroSalida.setDecarrera(SysmanFunciones.toString(registro.getCampos().get("DE_CARRERA")));
						registroSalida.setSalariobaseibc(SysmanFunciones.toString(registro.getCampos().get("SALARIO_BASE_IBC")));
						registroSalida.setDependenciaNombre(SysmanFunciones.toString(registro.getCampos().get("NOMBRE")));
						registroSalida.setAno(SysmanFunciones.toString(registro.getCampos().get("ANO")));
						registroSalida.setCompania(SysmanFunciones.toString(registro.getCampos().get("COMPANIA")));
						registroSalida.setEmailcorporativo(SysmanFunciones.toString(registro.getCampos().get("EMAIL_CORPORATIVO")!= null ? registro.getCampos().get("EMAIL_CORPORATIVO").toString() : ""));
						registroSalida.setEmailpersonal(SysmanFunciones.toString(registro.getCampos().get("EMAIL_PERSONAL")!= null ? registro.getCampos().get("EMAIL_PERSONAL").toString() : ""));
						registroSalida.setDireccion(SysmanFunciones.toString(registro.getCampos().get("DIRECCION")!= null ? registro.getCampos().get("DIRECCION").toString() : ""));
						registroSalida.setTelefonos(SysmanFunciones.toString(registro.getCampos().get("TELEFONOS") != null ? registro.getCampos().get("TELEFONOS").toString() : ""));
						registroSalida.setFechacumplimientobonificacion(SysmanFunciones.toString(registro.getCampos().get("FECHA_CUMPLIMIENTO_BONIFICACION")!= null ? registro.getCampos().get("FECHA_CUMPLIMIENTO_BONIFICACION").toString() : ""));
						salida.add(registroSalida);
					}
					respuestaApi.setCuerpo(salida);
				} else {
					String mensaje = "No se Encontraron datos con los parametros dados";
					String causa = mensaje;
					LOG.error("Error en <<Consultar Informe>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
					NegocioExcepcion error = new NegocioExcepcion(mensaje);
					error.initCause(new Exception(causa));
					throw error;
				}
				break;
			case 6:
				parametros = ejecutarSelectMultiRegistro(EJECUCION_PPTAL_INGRESOS, par);
				
				if (!parametros.isEmpty()) {
                    List<SalidaInformes6GobNar> salida = new ArrayList<>();
                    SalidaInformes6GobNar registroSalida = null;
                    for (Registro registro : parametros) {
                    	registroSalida = new SalidaInformes6GobNar();
                        registroSalida.setCompania(SysmanFunciones.toString(registro.getCampos().get("COMPANIA")));
                        registroSalida.setCuenta(SysmanFunciones.toString(registro.getCampos().get("CUENTA")));
                        registroSalida.setCodigo(SysmanFunciones.toString(registro.getCampos().get("CODIGO")));
                        registroSalida.setNombre(SysmanFunciones.toString(registro.getCampos().get("NOMBRE")));
                        registroSalida.setMovimiento(SysmanFunciones.toString(registro.getCampos().get("MOVIMIENTO")));
                        registroSalida.setTipoRecurso(SysmanFunciones.toString(registro.getCampos().get("TIPO_RECURSO")));
                        registroSalida.setFuenteRecurso(SysmanFunciones.toString(registro.getCampos().get("FUENTE_RECURSO")));
                        registroSalida.setApropiado(SysmanFunciones.toString(registro.getCampos().get("APROPIADO")));
                        registroSalida.setModificaciones(SysmanFunciones.toString(registro.getCampos().get("MODIFICACIONES")));
                        registroSalida.setTotalPresupuesto(SysmanFunciones.toString(registro.getCampos().get("TOTALPRESUPUESTO")));
                        registroSalida.setRecaudosAnteriores(SysmanFunciones.toString(registro.getCampos().get("RECAUDOSANTERIORES")));
                        registroSalida.setRecaudosMes(SysmanFunciones.toString(registro.getCampos().get("RECAUDOSMES")));
                        registroSalida.setRecaudosAcumulados(SysmanFunciones.toString(registro.getCampos().get("RECAUDOSACUMULADOS")));
                        registroSalida.setPorRecaudar(SysmanFunciones.toString(registro.getCampos().get("PORRECAUDAR")));
                        registroSalida.setPorcRecaudado(SysmanFunciones.toString(registro.getCampos().get("PORCRECAUDADO")));
                        salida.add(registroSalida);
                    }
				    respuestaApi.setCuerpo(salida);
                    } else {
    					String mensaje = "No se Encontraron datos con los parametros dados";
    					String causa = mensaje;
    					LOG.error("Error en <<Consultar Informe>> ->> mensaje ->> {} / causa ->> {}", mensaje, causa);
    					NegocioExcepcion error = new NegocioExcepcion(mensaje);
    					error.initCause(new Exception(causa));
    					throw error;
    				}
    				break;
				
			}

		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();

		}

		return Response.ok().entity(respuestaApi).build();
	}
//	//HU1 7733460 - Vista a Informes de base de datos - WebService Tipo Rest


	@GET
	@Path("/listaescolaridad")
	@Produces("application/json")
	public Response listaEscolaridad() {
		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralAutoservicioEnum.CATEGORIA.getValue(), "24");
		RespuestaApi respuestaApi = new RespuestaApi();
		try {

			procesadorCargarEscolaridad.setContexto(contexto);
			procesadorCargarEscolaridad.procesar();
			respuestaApi = procesadorCargarEscolaridad.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}
		return Response.ok().entity(respuestaApi).build();
	}
	
	/**
	 * Se obtienen la lista de dependencias de acuerdo a la compania	 * 
	 **/
	@GET
	@Path("/listaDependencias")
	@Produces("application/json")
	public Response listaDependenciasContab(@QueryParam("compania") String compania, @QueryParam("ano") String ano) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANO.getName(), ano);
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorDependenciasContab.setContexto(contexto);
			procesadorDependenciasContab.procesar();
			respuestaApi = procesadorDependenciasContab.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	/**
	 * 
	 * 
	 **/
	@GET
	@Path("/listaPlanContableCremil")
	@Produces("application/json")
	public Response listaPlanContableCremil(@QueryParam("compania") String compania, @QueryParam("ano") String anio) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANO.getName(), anio);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorListaPlanContableCremil.setContexto(contexto);
			procesadorListaPlanContableCremil.procesar();
			respuestaApi = procesadorListaPlanContableCremil.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	/**
	 * Se obtienen la lista de Fuente recurso de acuerdo a la compania y vigencia	 * 
	 **/
	@GET
	@Path("/listaFuenteRecursos")
	@Produces("application/json")
	public Response listaFuenteRecursos(@QueryParam("compania") String compania, @QueryParam("ano") String ano) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANO.getName(), ano);
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorFuenteRecursos.setContexto(contexto);
			procesadorFuenteRecursos.procesar();
			respuestaApi = procesadorFuenteRecursos.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	/**
	 * Se obtienen la lista de centro costos de acuerdo a la compania y vigencia	 * 
	 **/
	@GET
	@Path("/listaCentroCostos")
	@Produces("application/json")
	public Response listaCentroCostos(@QueryParam("compania") String compania, @QueryParam("ano") String ano) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANOACTUAL.getName(), ano);
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorCentroCostos.setContexto(contexto);
			procesadorCentroCostos.procesar();
			respuestaApi = procesadorCentroCostos.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	/**
	 * Se obtienen la lista de Auxiliares de acuerdo a la compania y vigencia	 * 
	 **/
	@GET
	@Path("/listaAuxiliar")
	@Produces("application/json")
	public Response listaAuxiliar(@QueryParam("compania") String compania, @QueryParam("ano") String ano) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANOACTUAL.getName(), ano);
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorAuxiliares.setContexto(contexto);
			procesadorAuxiliares.procesar();
			respuestaApi = procesadorAuxiliares.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	/**
	 * Se obtienen la lista de referencia de acuerdo a la compania y vigencia	 * 
	 **/
	@GET
	@Path("/listaReferencia")
	@Produces("application/json")
	public Response listaReferencia(@QueryParam("compania") String compania, @QueryParam("ano") String ano) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANO.getName(), ano);
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorReferencias.setContexto(contexto);
			procesadorReferencias.procesar();
			respuestaApi = procesadorReferencias.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	/**
	 * 
	 * 
	 **/
	@GET
	@Path("/listSaldoAux_Contable")
	@Produces("application/json")
	public Response listaSaldoAuxContable(@QueryParam("compania") String compania, @QueryParam("ano") String anio) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANO.getName(), anio);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesaListaSaldoAuxContable.setContexto(contexto);
			procesaListaSaldoAuxContable.procesar();
			respuestaApi = procesaListaSaldoAuxContable.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	@GET
	@Path("/reporteCuentasxCobrarCremil")
	@Produces("application/json")
	public Response generarInforme(@QueryParam("fechaCorte") String fechaCorte,
			@QueryParam("terceroInicial") String terceroInicial, @QueryParam("terceroFinal") String terceroFinal,
			@QueryParam("cuentaInicial") String cuentaInicial, @QueryParam("cuentaFinal") String cuentaFinal,
			@QueryParam("consolidado") boolean consolidado, @QueryParam("formatoEspecial") boolean formatoEspecial,
			@QueryParam("porVencimiento") boolean porVencimiento, @QueryParam("tercero") boolean tercero,
			@QueryParam("edades") boolean edades, @QueryParam("financiables") boolean financiables,
			@QueryParam("tipoformato") int tipoformato, @QueryParam("compania") String compania, @QueryParam("usuario") String usuario ) {
	
			// Preparar el contexto a partir de los parámetros recibidos
			Map<String, Object> contexto = new HashMap<>();
			contexto.put("fechaCorte", fechaCorte);
			contexto.put("terceroInicial", terceroInicial);
			contexto.put("terceroFinal", terceroFinal);
			contexto.put("cuentaInicial", cuentaInicial);
			contexto.put("cuentaFinal", cuentaFinal);
			contexto.put("consolidado", consolidado);
			contexto.put("formatoEspecial", formatoEspecial);
			contexto.put("porVencimiento", porVencimiento);
			contexto.put("tercero", tercero);
			contexto.put("edades", edades);
			contexto.put("financiables", financiables);
			contexto.put("tipoformato", tipoformato);
			contexto.put("compania", compania);
			contexto.put("usuario", usuario);
			
			RespuestaApi respuestaApi = new RespuestaApi();
			
			try {
				procesadorInformeCuentasxCobrar.setContexto(contexto);
				procesadorInformeCuentasxCobrar.procesar();
				respuestaApi = procesadorInformeCuentasxCobrar.getResultado();
			} catch (NegocioExcepcion e) {
				respuestaApi.setCodigo(5);
				respuestaApi.setMensaje(e.getMessage());
				return Response.ok().entity(respuestaApi).build();
			}

			return Response.ok().entity(respuestaApi).build();

		}
	/**
	 * 
	 * @param compania
	 * @param terceroInicial
	 * @param terceroFinal
	 * @return
	 */
	@GET
	@Path("/cargarTerceroPag")
	@Produces("application/json")
	public Response cargarTerceroPaginado(@QueryParam("compania") String compania,
			@QueryParam("pagInicio") String pagInicio, @QueryParam("pagTamanio") String pagTamanio, 
				@QueryParam("NIT") String NIT, @QueryParam("NOMBRE") String NOMBRE) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.PAGINICIO.getName(), pagInicio);
		contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), pagTamanio);
		if(NIT != null) {
			contexto.put("NIT", NIT);
		}
		if(NOMBRE != null) {
			contexto.put("NOMBRE", NOMBRE);
		}
		RespuestaApiPagArqC respuestaApi = new RespuestaApiPagArqC();

		try {
			procesadorCargarTerceroPag.setContexto(contexto);
			procesadorCargarTerceroPag.procesar();
			respuestaApi = procesadorCargarTerceroPag.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	/**
	 * 
	 * @param compania
	 * @param terceroInicial
	 * @param terceroFinal
	 * @return
	 */
	@GET
	@Path("/cargarTerceroPagFinal")
	@Produces("application/json")
	public Response cargarTerceroPaginadoFinal(@QueryParam("compania") String compania,
			@QueryParam("pagInicio") String pagInicio, @QueryParam("pagTamanio") String pagTamanio, 
				@QueryParam("NIT") String NIT, @QueryParam("NOMBRE") String NOMBRE, @QueryParam("TERCERO") String TERCERO) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.PAGINICIO.getName(), pagInicio);
		contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), pagTamanio);
		contexto.put("TERCEROINI", TERCERO);
		if(NIT != null) {
			contexto.put("NIT", NIT);
		}
		if(NOMBRE != null) {
			contexto.put("NOMBRE", NOMBRE);
		}
		RespuestaApiPagArqC respuestaApi = new RespuestaApiPagArqC();

		try {
			procesadorCargarTerceroPagFinal.setContexto(contexto);
			procesadorCargarTerceroPagFinal.procesar();
			respuestaApi = procesadorCargarTerceroPagFinal.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	/**
	 * 
	 * @param compania
	 * @param terceroInicial
	 * @param terceroFinal
	 * @return
	 */
	@GET
	@Path("/cargarCuentasCarteraPag")
	@Produces("application/json")
	public Response cargarCuentasCarteraPaginado(@QueryParam("compania") String compania, 
			@QueryParam("pagInicio") String pagInicio, @QueryParam("pagTamanio") String pagTamanio, 
				@QueryParam("CODIGO") String CODIGO, @QueryParam("NOMBRE") String NOMBRE) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.PAGINICIO.getName(), pagInicio);
		contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), pagTamanio);
		contexto.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));
		if(CODIGO != null) {
			contexto.put("CODIGO", CODIGO);
		}
		if(NOMBRE != null) {
			contexto.put("NOMBRE", NOMBRE);
		}
		RespuestaApiPagArqC respuestaApi = new RespuestaApiPagArqC();

		try {
			procesadorCargarCuentasCarteraPag.setContexto(contexto);
			procesadorCargarCuentasCarteraPag.procesar();
			respuestaApi = procesadorCargarCuentasCarteraPag.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	/**
	 * 
	 * @param compania
	 * @param terceroInicial
	 * @param terceroFinal
	 * @return
	 */
	@GET
	@Path("/cargarCuentasCarteraPagFinal")
	@Produces("application/json")
	public Response cargarCuentasCarteraPaginadoFinal(@QueryParam("compania") String compania, 
			@QueryParam("pagInicio") String pagInicio, @QueryParam("pagTamanio") String pagTamanio, 
				@QueryParam("CODIGO") String CODIGO, @QueryParam("NOMBRE") String NOMBRE, @QueryParam("CUENTAINI") String CODIGOINI) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.PAGINICIO.getName(), pagInicio);
		contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), pagTamanio);
		contexto.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));
		contexto.put("CUENTAINI", CODIGOINI);
		if(CODIGO != null) {
			contexto.put("CODIGO", CODIGO);
		}
		if(NOMBRE != null) {
			contexto.put("NOMBRE", NOMBRE);
		}
		RespuestaApiPagArqC respuestaApi = new RespuestaApiPagArqC();

		try {
			procesadorCargarCuentasCarteraPagFinal.setContexto(contexto);
			procesadorCargarCuentasCarteraPagFinal.procesar();
			respuestaApi = procesadorCargarCuentasCarteraPagFinal.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	/**
	 * 
	 * @param compania
	 * @param nombre
	 * @param aplicacion
	 * @return
	 */
	@GET
	@Path("/consultarParametro")
	@Produces("application/json")
	public Response consultarParametro(@QueryParam("compania") String compania, @QueryParam("nombre") String nombre, @QueryParam("aplicacion") String aplicacion) {
		
		Parametro contexto = new Parametro();
		contexto.setCompania(compania);
		contexto.setNombreParametro(nombre);
		contexto.setAplicacion(aplicacion);

		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorConsultaParametro.setContexto(contexto);
			procesadorConsultaParametro.procesar();
			respuestaApi = procesadorConsultaParametro.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	/**
	 * Retorna el informe de una factura.
	 *
	 * @param token    		Credenciales de autorizaci&oacute;n. Token asociado con a la
	 *                 		consulta o solicitud de autoservicio.
	 * @param compania 		Codigo de la compania
	 * @param tipo     		Tipo de cobro de la factura que se quiere imprimir
	 * @param anio     		Año de la factura que se quiere imprimir
	 * @param factura  		Codigo de la factura que se quiere imprimir
	 * @param nitEntidad 	Nit de la entidad que realiza la peticion del servicio 
	 * @return respuesta 	Respueta generada al llevar a cabo la peticion GET
	 */
	@GET
	@Path("/informeFactura")
	@Produces("application/json")
	public Response consultarInformeFactura(@HeaderParam(AUTHORIZATION) String token,
			@QueryParam("compania") String compania, @QueryParam("tipo") String tipo,
			@QueryParam("anio") String anio, @QueryParam("factura") String factura, @QueryParam("nitEntidad") String nit) {
		ParametrosInformeFactura contexto = new ParametrosInformeFactura(compania, tipo, anio, factura, nit);
		RespuestaApi respuestaApi = new RespuestaApi();
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(contexto.getCompania(), contexto.getNitEntidad(), token, 11);
		} catch (NegocioExcepcion e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED).entity(respuestaApi).build();
		}

		try {
			procesadorInformeFactura.setContexto(contexto);
			procesadorInformeFactura.procesar();
			respuestaApi = procesadorInformeFactura.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();
	}
	
	/**
	 * Retorna el informe de las facturas pendientes por pagar que tiene fecha proxima de vencimiento.
	 *
	 * @param token    		Credenciales de autorizaci&oacute;n. Token asociado con a la
	 *                 		consulta o solicitud de autoservicio.
	 * @param compania 		Codigo de la compania
	 * @param tipo     		Tipo de cobro de la factura que se quiere imprimir
	 * @param anio     		Año de la factura que se quiere imprimir
	 * @param factura  		Codigo de la factura que se quiere imprimir
	 * @param nitEntidad 	Nit de la entidad que realiza la peticion del servicio 
	 * @return respuesta 	Respueta generada al llevar a cabo la peticion GET
	 */
	@GET
	@Path("/informeFacturasPorCobrar")
	@Produces("application/json")
	public Response consultarFacturasPorCobrar(@HeaderParam(AUTHORIZATION) String token,
			@QueryParam("compania") String compania, @QueryParam("fechaInicial") String fechaInicial,
			@QueryParam("fechaFinal") String fechaFinal, @QueryParam("nitEntidad") String nit, @QueryParam("tipoFormato") String tipoFormato) {
		ParametrosInformeFacturasPorCobrar contexto = new ParametrosInformeFacturasPorCobrar(compania, fechaInicial, fechaFinal, nit, tipoFormato);
		RespuestaApi respuestaApi = new RespuestaApi();
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
		try {
			validarTokenconBD(contexto.getCompania(), contexto.getNitEntidad(), token, 11);
		} catch (NegocioExcepcion e1) {
			LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}", e1.getMessage(), e1.getCause());
			respuestaApi.setCodigo(401);
			respuestaApi.setMensaje(e1.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED).entity(respuestaApi).build();
		}

		try {
			procesadorInformeFacturasPorCobrar.setContexto(contexto);
			procesadorInformeFacturasPorCobrar.procesar();
			respuestaApi = procesadorInformeFacturasPorCobrar.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();
	}
	
	/**
	 * 
	 * @param compania
	 * @param anio
	 * @param pagina
	 * @param tamanio
	 * @return
	 */
	@GET
	@Path("/referenciasxpagina")
	@Produces("application/json")
	public Response cargarReferenciasxPagina(@QueryParam("compania") String compania, 
			@QueryParam("anio") String anio, @QueryParam("pagina") String pagina, @QueryParam("tamanio") String tamanio) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANIO.getName(), anio);
		contexto.put(GeneralParameterEnum.PAGINICIO.getName(), pagina);
		contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), tamanio);
		contexto.put(GeneralParameterEnum.URL.getName(), GeneraArchivoUrlEnum.URL13051.getValue());
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorConsultarPaginado.setContexto(contexto);
			procesadorConsultarPaginado.procesar();
			respuestaApi = procesadorConsultarPaginado.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	/**
	 * 
	 * @param compania
	 * @param anio
	 * @param pagina
	 * @param tamanio
	 * @return
	 */
	@GET
	@Path("/centrocostosxpagina")
	@Produces("application/json")
	public Response cargarCentroCostoxPagina(@QueryParam("compania") String compania, 
			@QueryParam("anio") String anio, @QueryParam("pagina") String pagina, @QueryParam("tamanio") String tamanio) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANIO.getName(), anio);
		contexto.put(GeneralParameterEnum.PAGINICIO.getName(), pagina);
		contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), tamanio);
		contexto.put(GeneralParameterEnum.URL.getName(), GeneraArchivoUrlEnum.URL20085.getValue());
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorConsultarPaginado.setContexto(contexto);
			procesadorConsultarPaginado.procesar();
			respuestaApi = procesadorConsultarPaginado.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	/**
	 * 
	 * @param compania
	 * @param anio
	 * @param pagina
	 * @param tamanio
	 * @return
	 */
	@GET
	@Path("/fuenterecursosxpagina")
	@Produces("application/json")
	public Response cargarFuenteRecursosxPagina(@QueryParam("compania") String compania, 
			@QueryParam("anio") String anio, @QueryParam("pagina") String pagina, @QueryParam("tamanio") String tamanio) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANIO.getName(), anio);
		contexto.put(GeneralParameterEnum.PAGINICIO.getName(), pagina);
		contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), tamanio);
		contexto.put(GeneralParameterEnum.URL.getName(), GeneraArchivoUrlEnum.URL34075.getValue());
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorConsultarPaginado.setContexto(contexto);
			procesadorConsultarPaginado.procesar();
			respuestaApi = procesadorConsultarPaginado.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
	
	/**
	 * 
	 * @param compania
	 * @param anio
	 * @param pagina
	 * @param tamanio
	 * @return
	 */
	@GET
	@Path("/auxiliaresxpagina")
	@Produces("application/json")
	public Response cargarAuxiliaresxPagina(@QueryParam("compania") String compania, 
			@QueryParam("anio") String anio, @QueryParam("pagina") String pagina, @QueryParam("tamanio") String tamanio) {

		Map<String, Object> contexto = new TreeMap<>();
		contexto.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		contexto.put(GeneralParameterEnum.ANIO.getName(), anio);
		contexto.put(GeneralParameterEnum.PAGINICIO.getName(), pagina);
		contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), tamanio);
		contexto.put(GeneralParameterEnum.URL.getName(), GeneraArchivoUrlEnum.URL23062.getValue());
		
		RespuestaApi respuestaApi = new RespuestaApi();

		try {
			procesadorConsultarPaginado.setContexto(contexto);
			procesadorConsultarPaginado.procesar();
			respuestaApi = procesadorConsultarPaginado.getResultado();
		} catch (NegocioExcepcion e) {
			respuestaApi.setCodigo(5);
			respuestaApi.setMensaje(e.getMessage());
			return Response.ok().entity(respuestaApi).build();
		}

		return Response.ok().entity(respuestaApi).build();

	}
}