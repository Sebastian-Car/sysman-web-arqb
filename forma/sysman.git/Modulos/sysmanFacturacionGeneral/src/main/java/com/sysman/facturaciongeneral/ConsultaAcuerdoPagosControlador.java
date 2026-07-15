/*-
 * ConsultaAcuerdoPagosControlador.java
 *
 * 1.0
 * 
 * 20/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralAcuerdosRemote;
import com.sysman.facturaciongeneral.enums.ConsultaAcuerdoPagosControladorEnum;
import com.sysman.facturaciongeneral.enums.ConsultaAcuerdoPagosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FacturarLotesControladorEnum;
import com.sysman.facturaciongeneral.enums.FacturarLotesControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.mail.Session;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario
 * "Consultar Acuerdo de Pago" en Access "FRM_CONSULTA_ACUERDOPAGO",
 * el cual es llamado desde Facturación\Procesos\Acuerdos de
 * Pago\Consultar Acuerdo
 *
 * @version 1.0, 20/11/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class ConsultaAcuerdoPagosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo en el que se esta
     * trabajando
     */
    private final String modulo;
    /**
     * Anio de trabajo que ha sido seleccionado al ingresar al modulo
     * de Facturacion General
     */
    private String anio;
    /**
     * Tipo de cobro que ha sido selecionado al ingresar al modulo de
     * Facturacion General
     */
    private String tipoCobro;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que almacena el valor total de Capital para los
     * detalles de Acuerdo de Pago
     */
    private String totalCapital;
    /**
     * Atributo que almacena el valor total de Interes para los
     * detalles de Acuerdo de Pago
     */
    private String totalInteres;
    /**
     * Atributo que almacena el valor total de Interes Financiacion
     * para los detalles de Acuerdo de Pago
     */
    private String totalFinanciacion;
    /**
     * Atributo que almacena el valor total de Interes Reargo para los
     * detalles de Acuerdo de Pago
     */
    private String totalRecargo;
    /**
     * Atributo que almacena el valor total de Cuotas para los
     * detalles de Acuerdo de Pago
     */
    private String totalCuota;
    /**
     * Implementacion del EJB de EjbFacturacionGeneralAcuerdosRemote
     * para hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_FACT_GENERAL_ACUERDOS
     */
    @EJB
    private EjbFacturacionGeneralAcuerdosRemote ejbFacturacionGeneralAcuerdos;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Listado de registros para el subformulario "SubConsultaAcuerdo"
     */
    private List<Registro> listaSubconsultaacuerdo;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;
    private boolean permiteVer;
	private String estado;
	private String codigoEan;
	private String factura;
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ConsultaAcuerdoPagosControlador
     */
    public ConsultaAcuerdoPagosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anio = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        try {
            numFormulario = GeneralCodigoFormaEnum.CONSULTA_ACUERDO_PAGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
            }
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubconsultaacuerdo();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubconsultaacuerdo = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        tabla = ConsultaAcuerdoPagosControladorEnum.SF_ACUERDO_PAGO.getValue();
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por el DSS
     * que contiene la consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsultaAcuerdoPagosControladorUrlEnum.URL0001
                                                        .getValue());
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsultaAcuerdoPagosControladorUrlEnum.URL0002
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(ConsultaAcuerdoPagosControladorEnum.TIPOCOBRO
                        .getValue(), tipoCobro);
    }

    /**
     * 
     * Carga la lista listaSubconsultaacuerdo, la cual se relaciona
     * con el subformulario "SubconsultaAcuerdo"
     *
     */
    public void cargarListaSubconsultaacuerdo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(ConsultaAcuerdoPagosControladorEnum.TIPOACUERDO
                            .getValue(), registro.getCampos().get("TIPO"));
            param.put(ConsultaAcuerdoPagosControladorEnum.CODACUERDO.getValue(),
                            registro.getCampos().get("CODIGO"));

            listaSubconsultaacuerdo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConsultaAcuerdoPagosControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(),
                                            param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            "SF_DETALLE_ACUERDO"));

        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Realiza el calculo de los totales que seran visualizados en el
     * pie del formulario
     */
    private void calcularTotales() {
        // <CODIGO_DESARROLLADO>
        try {
            Registro regTotales = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConsultaAcuerdoPagosControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(), parametrosListado));

            totalCapital = formatearValor(regTotales, "CAPITAL");
            totalInteres = formatearValor(regTotales, "INTERES");
            totalFinanciacion = formatearValor(regTotales, "INT_FINANCIACION");
            totalRecargo = formatearValor(regTotales, "INT_RECARGO");
            totalCuota = formatearValor(regTotales, "TOTAL_CUOTA");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Adiciona el formato de moneda a los totales que se presentan en
     * el pie del formulario
     * 
     * @param registro
     * Registro que obtiene los valores a formatear
     * @param campo
     * Nombre del campo a formatear
     * @return Valor con el formato moneda aplicado
     */
    private String formatearValor(Registro registro, String campo) {
        return new java.text.DecimalFormat(" #,##0.00")
                        .format(Double.parseDouble(registro.getCampos()
                                        .get(campo).toString()));
    }

    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton BtnDistribucion
     * 
     * Redirecciona al formulario "ConsultarCuotasControlador"(1460)
     * para ver los detalles relacionados con la cuota seleccionada
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirBtnDistribucion(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        String codigoAcuerdo = reg.getCampos().get("CODIGO_AP").toString();
        String cuota = reg.getCampos().get("CUOTA").toString();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("codigoAcuerdo", codigoAcuerdo);
        parametros.put("cuota", cuota);
        parametros.put("rid", css);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CONSULTA_CUOTAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton BtnImprimir
     * 
     * Realiza la facturacion para la cuota seleccionada mediante el
     * llamado al procedimiento
     * PCK_FACT_GENERAL_ACUERDOS.PR_FACTURARACUERDO
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirBtnImprimir(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            ejbFacturacionGeneralAcuerdos.facturarAcuerdo(compania,
                            Integer.parseInt(anio),
                            tipoCobro,
                            retornarString(registro, "TIPO"),
                            new BigInteger(retornarString(registro, "CODIGO")),
                            Integer.parseInt(retornarString(reg, "CUOTA")),
                            SessionUtil.getUser().getCodigo());
            // Recarga el listado de registros para el subformulario
            cargarListaSubconsultaacuerdo();
            
            validarEstado(reg);

            if ("F".equalsIgnoreCase(estado)) {
                ejecutarMsjGenerarFactura();
                generarInforme( FORMATOS.PDF);

            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }
    
    
    public Registro validarEstado(Registro reg) {
    	Registro rsDisminuido;
		try {
    	HashMap<String, Object> param = new HashMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.TIPO.getName(), tipoCobro);
    	param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    	param.put(GeneralParameterEnum.CUOTA.getName(), reg.getCampos().get(GeneralParameterEnum.CUOTA.getName()));
    	
    
			rsDisminuido = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ConsultaAcuerdoPagosControladorUrlEnum.URL0005.getValue())
							.getUrl(),
							param));
	

		if (rsDisminuido != null) {

            estado    = rsDisminuido.getCampos().get("ESTADO").toString();
            codigoEan = rsDisminuido.getCampos().get("CODIGOEAN").toString();
            factura   = rsDisminuido.getCampos().get("FACTURA_PAGO").toString();
           
       
		}
	} catch (SystemException e) {
		 logger.error(e.getMessage(), e);
         JsfUtil.agregarMensajeError(e.getMessage());
     }
		return reg;
    }    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Plantilla
     * en la vista
     *
     *
     */
public void oprimirPlantilla() {
         //<CODIGO_DESARROLLADO>
	try {
	String fechaPlantilla = null;
	String nombrePlantilla = null;
	String codigoPlantilla = null;
	
	Map<String, Object> paramPlantilla = new HashMap<>();
	paramPlantilla.put(GeneralParameterEnum.TIPO.getName(), "60");
	paramPlantilla.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get("FORMATO_PLANTILLA"));

	Registro rsPlantilla;
	
		rsPlantilla = RegistroConverter
				.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								FacturarLotesControladorUrlEnum.URL86964.getValue())
						.getUrl(),
						paramPlantilla));
	

	if (rsPlantilla != null) {
	  	 codigoPlantilla = rsPlantilla.getCampos().get("CODIGO").toString();
		 nombrePlantilla = rsPlantilla.getCampos().get("NOMBRE").toString();
		 fechaPlantilla = SysmanFunciones.formatearFecha((Date) rsPlantilla.getCampos().get("FECHA"));
	}

    String[] campos = new String[3];
    String[] valores = new String[3];
    campos[0] = "codigoPlantilla";
    campos[1] = "fechaPlantilla";
    campos[2] = "nombreDocDescarga";

    valores[0] = codigoPlantilla;
    valores[1] = fechaPlantilla;
    valores[2] = nombrePlantilla;
    
    String codigoAcuerdo = registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    String tipoAcuerdo = registro.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString();
    
    HashMap<String, String> variablesConsultaW = new HashMap<>();

    variablesConsultaW.put("s$compania$s",
                    SysmanFunciones.concatenar("'", compania, "'"));
    variablesConsultaW.put("s$codigo$s",
                    SysmanFunciones.concatenar("'", codigoAcuerdo,
                                    "'"));
    variablesConsultaW.put("s$tipo$s",
                    SysmanFunciones.concatenar("'",tipoAcuerdo,
                                    "'"));
    
    SessionUtil.setSessionVar("variablesConsultaWord",
            variablesConsultaW);
	
    SessionUtil.cargarModalDatosFlash(Integer
            .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                            .getCodigo()),
            SessionUtil.getModulo(), campos,
            valores);
    
	} catch (SystemException e) {
		e.printStackTrace();
	}
    
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo invocado al ejecutar el comando remoto MsjGenerarFactura
     * en la vista
     *
     * Permite la visualizacion del mensaje al oprimir el boton
     * "Imprimir"
     *
     */
    public void ejecutarMsjGenerarFactura() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("TB_TB3906"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del Subformulario Subconsultaacuerdo, en el
     * que se trabajan las cuotas asociadas a un Acuerdo de Pago
     * 
     */
    public void agregarRegistroSubSubconsultaacuerdo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de edicion del Subformulario Subconsultaacuerdo
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubconsultaacuerdo(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de eliminacion del Subformulario Subconsultaacuerdo
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubconsultaacuerdo(Registro reg) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subconsultaacuerdo
     *
     */
    public void cancelarEdicionSubconsultaacuerdo() {
        cargarListaSubconsultaacuerdo();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    /**
     * Permite obtener el nombre del formato con el que se desea
     * generar la factura
     * 
     * @return nombre del formato a generar
     */
    private String obtenerFormato() {
        String formato = "";
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        params.put(FacturarLotesControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturarLotesControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));

            if (rs != null && !SysmanFunciones.validarCampoVacio(rs.getCampos(),
                            "FORMATO_FACTURA")) {
                formato = rs.getCampos().get("FORMATO_FACTURA").toString();
            }
            else {
                formato = obtenerParametro("SF FORMATO FACTURACION",
                                "001493INFFACSTD010");
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return formato;
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme( FORMATOS formato) {
        try {
            /*
             * El formato que ha sido migrado a hoy 16/11/2017 es
             * "001493INFFACSTD010"
             */
            String informe = obtenerFormato();
            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", anio);
            reemplazar.put("tipoFactura", tipoCobro);
            reemplazar.put("facturaInicial", factura);
            reemplazar.put("facturaFinal", factura);
            reemplazar.put("codigoEan", codigoEan);
            // PARAMETROS PARA GENERACION DE INFORME
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());
            parametros.put("PR_CUENTABANCO1",
                            obtenerParametro("SF BANCO CUENTA 1", ""));
            parametros.put("PR_CUENTABANCO2",
                            obtenerParametro("SF BANCO CUENTA 2", ""));
            parametros.put("PR_CUENTABANCO3",
                            obtenerParametro("SF BANCO CUENTA 3", ""));
            parametros.put("PR_CUENTABANCO4",
                            obtenerParametro("SF BANCO CUENTA 4", ""));
            parametros.put("PR_SF_CPTO_PPAL_PRODESARROLLO", obtenerParametro(
                            "SF CONCEPTO PRINCIPAL PRODESARROLLO", ""));
            parametros.put("PR_SF_MANEJA_CODIGO_BARRAS",
                            "SI".equalsIgnoreCase(obtenerParametro(
                                            "SF MANEJA CODIGO DE BARRAS", "SI"))
                                                ? true : false);
            //inicia implementacion de marca_blanca LJDIAZ (Luis Jacobo Diaz Muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            //fin implementacion marca_blanca
            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException
                        | OutOfMemoryError e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        calcularTotales();
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
        
        permiteVer = Boolean.parseBoolean(obtenerParametro("PERMITE ACUERDO PREDIAL DESDE FACTURACION", "NO"));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return Verdadero si se autoriza la insercion de un registro en
     * el formulario principar
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public String getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(String totalCapital) {
        this.totalCapital = totalCapital;
    }

    public String getTotalInteres() {
        return totalInteres;
    }

    public void setTotalInteres(String totalInteres) {
        this.totalInteres = totalInteres;
    }

    public String getTotalFinanciacion() {
        return totalFinanciacion;
    }

    public void setTotalFinanciacion(String totalFinanciacion) {
        this.totalFinanciacion = totalFinanciacion;
    }

    public String getTotalRecargo() {
        return totalRecargo;
    }

    public void setTotalRecargo(String totalRecargo) {
        this.totalRecargo = totalRecargo;
    }

    public String getTotalCuota() {
        return totalCuota;
    }

    public void setTotalCuota(String totalCuota) {
        this.totalCuota = totalCuota;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubconsultaacuerdo
     * 
     * @return listaSubconsultaacuerdo
     */
    public List<Registro> getListaSubconsultaacuerdo() {
        return listaSubconsultaacuerdo;
    }

    /**
     * Asigna la lista listaSubconsultaacuerdo
     * 
     * @param listaSubconsultaacuerdo
     * Variable a asignar en listaSubconsultaacuerdo
     */
    public void setListaSubconsultaacuerdo(
        List<Registro> listaSubconsultaacuerdo) {
        this.listaSubconsultaacuerdo = listaSubconsultaacuerdo;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

	public boolean isPermiteVer() {
		return permiteVer;
	}

	public void setPermiteVer(boolean permiteVer) {
		this.permiteVer = permiteVer;
	}
    
    
    // </SET_GET_ADICIONALES>
}
