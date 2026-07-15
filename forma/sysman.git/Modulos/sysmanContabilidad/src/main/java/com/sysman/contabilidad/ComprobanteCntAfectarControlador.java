package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.ComprobanteCntAfectarControladorEnum;
import com.sysman.contabilidad.enums.ComprobanteCntAfectarControladorUrlEnum;
import com.sysman.contabilidad.enums.ComprobantecntsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 14/03/2016
 * @modifier amonroy
 * @version 2, 19/04/2017 Proceso de Refactoring y Revision de buenas practicas
 * sugeridas por la herramienta SonarLint
 * @modifier jeguerrero
 * @version 3, 25/05/2017 Implementacion de funciones para realizar las
 * afectaciones contables y presupuestales
 * @modifier jgomez
 * @version 4, 02/08/2018 Se implementa el metodo para realizar la anulaciÃ³n de
 * comprobantes
 * @modifier gfigueredo
 * @version 5, 14/05/2021 Se habilita la visualización del formulario
 * pedirFactura para la claseComprobante 'N', Se valida que la consulta sea la
 * adecuada (URL39812 - 16101) para claseComprobante 'N'. Se ajusta la clase
 * según las recomendaciones de SonarLint.
 * @modifier gfigueredo
 * @version 5.1. , 17/06/2021, Se cambia (URL39812 - 16101) por (URL39812 -
 * 16069)
 * 
 */
@ManagedBean
@ViewScoped
public class ComprobanteCntAfectarControlador extends BeanBaseModal {

    private static final String VLR_DOCUMENTO = "vlrDocumento";
    private static final String TB_TB686 = "TB_TB686";
    private final String compania;
    private final String modulo;
    private final String cNumero;
    private final String cRowIdComprobante;
    private HashMap<String, Object> rowIdComprobante;
    private String claseComprobante;
    private String anoComprobante;
    private String tipoComprobante;
    private String numeroComprobante;
    private String fechaComprobante;
    private String vlrDocumento;
    private boolean visibleDialogo;
    private List<Registro> listaSeleccionados;
    private RegistroDataModelImpl listaLista;
    /**
     * Lista de numeros de cuentas para el combo del dialogo PideCuentaAfectar
     */
    private RegistroDataModelImpl listaNumeroCuenta;
    private String terceroComprobante;
    private String sucursalComprobante;
    private String compRelacionado;
    private String terceroEnEncabezado;
    private String centroCosto;
    private String tipoCpteAfect;
    private String auxiliarComprobante;
    private String mesComprobante;
    private boolean visibleOrdenador;
    private String controlChequera;
    private String vlrGirar;
    private String opcionMenu;
    private String nombreFormulario;
    double valorDocumento;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo para indicar cuando se debe visualizar el dialogo de
     * Equivalencia Presupuestal.
     */
    private boolean visibleDialogoAdvertencia;
    /**
     * Permite mostrar/ocultar el dialogo PedirCuentaAfectar.
     */
    private boolean pedirCuentaAfectar;

    /**
     * Texto en el encabezado del dialogo PedirCuentaAfectar.
     */
    private String encabezadoPedirCuenta;
    private boolean vuelve;
    /**
     * Atributo que valida la visibilidad del boton inconsistencias
     */
    private boolean visibleInconsistencias;
    private String numeroCuenta;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de un
     * parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de EjbContabilidadCincoRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y se
     * encuentran almacenadas en el paquete PCK_CONTABILIDAD5
     */
    @EJB
    private EjbContabilidadCincoRemote ejbContabilidadCinco;
    /**
     * Implementacion del EJB de EjbContabilidadSeisRemote para hacer el llamado
     * a las funciones que se invocan dentro del Controlador y se encuentran
     * almacenadas en el paquete PCK_CONTABILIDAD6
     */
    @EJB
    private EjbContabilidadSeisRemote ejbContabilidadSeis;
    @EJB
    private EjbContabilidadSieteRemote ejbContabilidadSiete;
    /**
     * Define la URL que obtiene los registros que se cargan en la lista
     * principal del formulario
     */
    UrlBean urlconsultaLista;
    /**
     * Almacena los parametros a enviar al servicio, dependiendo la URL que se
     * defina en el atributo urlconsultaLista
     */
    Map<String, Object> paramConsultaLista;

    /**
     * Creates a new instance of ComprobanteCntAfectarControlador
     */
    @SuppressWarnings("unchecked")
    public ComprobanteCntAfectarControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cNumero = GeneralParameterEnum.NUMERO.getName();
        cRowIdComprobante = "rowIdComprobante";

        try {
            numFormulario = GeneralCodigoFormaEnum.COMPROBANTE_CNT_AFECTAR_CONTROLADOR
                            .getCodigo();
            visibleDialogo = false;
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                if (parametros.get(cRowIdComprobante) == null) {
                    vuelve = true;
                    return;
                }
                rowIdComprobante = (HashMap<String, Object>) parametros
                                .get(cRowIdComprobante);
                anoComprobante = validarCampos(parametros, "anoComprobante");
                tipoComprobante = validarCampos(parametros, "tipoComprobante");
                numeroComprobante = validarCampos(parametros,
                                "numeroComprobante");
                claseComprobante = validarCampos(parametros,
                                "claseComprobante");
                vlrDocumento = validarCampos(parametros, VLR_DOCUMENTO);
                fechaComprobante = validarCampos(parametros,
                                "fechaComprobante");
                terceroComprobante = validarCampos(parametros,
                                "terceroComprobante");
                sucursalComprobante = validarCampos(parametros,
                                "sucursalComprobante");
                compRelacionado = validarCampos(parametros, "compRelacionado");
                terceroEnEncabezado = validarCampos(parametros,
                                "terceroEncabezado");
                centroCosto = validarCampos(parametros, "centroCosto");
                tipoCpteAfect = validarCampos(parametros, "tipoCpteAfect");
                mesComprobante = validarCampos(parametros, "mesComprobante");
                auxiliarComprobante = validarCampos(parametros,
                                "auxiliarComprobante");
                controlChequera = validarCampos(parametros, "controlChequera");
                vlrGirar = validarCampos(parametros, "vlrGirar");
                opcionMenu = validarCampos(parametros, "opcionMenu");

                /**
                 * @var nombreFormulario: Al implementar la capa de logica, se
                 * debe tener en cuenta si el formulario a abrir es
                 * Comprobante_cntAfectar o Comprobante_cntAfectarRES, pues se
                 * dejo la misma forma y bean, para ser validado posteriormente.
                 *
                 * -Si la opcion de menu llama a Comprobante_cntAfectar, el
                 * parametro vendra con la letra A.
                 *
                 * -Si la opcion de menu llama a Comprobante_cntAfectarRES, el
                 * parametro vendra con la letra R.
                 */
                nombreFormulario = validarCampos(parametros,
                                "nombreFormulario");

            }
            validarPermisos();

            paramConsultaLista = new TreeMap<>();

        }
        catch (Exception ex) {
            Logger.getLogger(ComprobanteCntAfectarControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        if (vuelve) {
            oprimirCancelar();
            return;
        }
        if ("R".equals(nombreFormulario)) {
            // Comprobante_cntAfectarRES
            inicializarComprobanteCntAfectarRES();
        }
        else {
        	if("SI".equals(getParametro("LISTAR NOTAS CLIENTE EN EGRESO", "NO"))){
        	compRelacionado += ", 'N'";
        	}
            // Comprobante_cntAfectar
            inicializarComprobanteCntAfectar();
        }
        cargarListaLista();
        cargarListaNumeroCuenta();
        abrirFormulario();
    }

    /**
     * Metodo auxiliar que define la consulta de la lista que debe cargarse para
     * afectar un comprobante contable.
     */
    private void inicializarComprobanteCntAfectarAuxiliar() {
        if ((tipoCpteAfect != null) && "V".equals(tipoCpteAfect)) {
            urlconsultaLista = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ComprobanteCntAfectarControladorUrlEnum.URL001
                                                            .getValue());
            paramConsultaLista.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                            .getValue(),
                            terceroComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                            .getValue(),
                            sucursalComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
                                            .getValue(),
                            compRelacionado);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
                                            .getValue(),
                            fechaComprobante);
        }
        else if ((claseComprobante != null) && "T".equals(claseComprobante)) {
            urlconsultaLista = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ComprobanteCntAfectarControladorUrlEnum.URL002
                                                            .getValue());
            paramConsultaLista.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                            .getValue(),
                            terceroComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                            .getValue(),
                            sucursalComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
                                            .getValue(),
                            compRelacionado);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
                                            .getValue(),
                            fechaComprobante);
        }
        else
            if ("".equals(getParametro("MANEJA PROGRAMACION DE PAGOS", "NO"))
                && (claseComprobante != null)
                && !"A".equals(claseComprobante)) {

                    urlconsultaLista = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    ComprobanteCntAfectarControladorUrlEnum.URL003
                                                                    .getValue());
                    paramConsultaLista.put(
                                    GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    paramConsultaLista.put(
                                    ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                                    .getValue(),
                                    terceroComprobante);
                    paramConsultaLista.put(
                                    ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                                    .getValue(),
                                    sucursalComprobante);
                    paramConsultaLista.put(
                                    ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
                                                    .getValue(),
                                    compRelacionado);
                    paramConsultaLista.put(
                                    ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
                                                    .getValue(),
                                    fechaComprobante);
                }
            else
                if ("SI".equals(getParametro(
                                "FILTRAR ORDEN DE PAGO AFECTADA PRESUPUESTALMENTE",
                                "NO"))) {
                                    urlconsultaLista = UrlServiceUtil
                                                    .getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                                    ComprobanteCntAfectarControladorUrlEnum.URL004
                                                                                    .getValue());
                                    paramConsultaLista.put(
                                                    GeneralParameterEnum.COMPANIA
                                                                    .getName(),
                                                    compania);
                                    paramConsultaLista.put(
                                                    ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                                                    .getValue(),
                                                    terceroComprobante);
                                    paramConsultaLista.put(
                                                    ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                                                    .getValue(),
                                                    sucursalComprobante);
                                    paramConsultaLista.put(
                                                    ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
                                                                    .getValue(),
                                                    compRelacionado);
                                    paramConsultaLista.put(
                                                    ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
                                                                    .getValue(),
                                                    fechaComprobante);
                                    paramConsultaLista.put(
                                                    ComprobanteCntAfectarControladorEnum.ANOCOMPROBANTE
                                                                    .getValue(),
                                                    anoComprobante);
                                }
                else
                    if ("SI".equals(getParametro(
                                    "MANEJA CAMBIO DE SESIONES EN CONTRATOS",
                                    "NO"))) {

                                        urlconsultaLista = UrlServiceUtil
                                                        .getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        ComprobanteCntAfectarControladorUrlEnum.URL005
                                                                                        .getValue());
                                        paramConsultaLista.put(
                                                        GeneralParameterEnum.COMPANIA
                                                                        .getName(),
                                                        compania);
                                        paramConsultaLista.put(
                                                        ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                                                        .getValue(),
                                                        terceroComprobante);
                                        paramConsultaLista.put(
                                                        ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                                                        .getValue(),
                                                        sucursalComprobante);
                                        paramConsultaLista.put(
                                                        ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
                                                                        .getValue(),
                                                        compRelacionado);
                                        paramConsultaLista.put(
                                                        ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
                                                                        .getValue(),
                                                        fechaComprobante);
                                        paramConsultaLista.put(
                                                        ComprobanteCntAfectarControladorEnum.ANOCOMPROBANTE
                                                                        .getValue(),
                                                        anoComprobante);
                                    }
                    else {
                        urlconsultaLista = UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        ComprobanteCntAfectarControladorUrlEnum.URL006
                                                                        .getValue());
                        paramConsultaLista.put(
                                        GeneralParameterEnum.COMPANIA.getName(),
                                        compania);
                        paramConsultaLista.put(
                                        ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                                        .getValue(),
                                        terceroComprobante);
                        paramConsultaLista.put(
                                        ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                                        .getValue(),
                                        sucursalComprobante);
                        paramConsultaLista.put(
                                        ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
                                                        .getValue(),
                                        compRelacionado);
                        paramConsultaLista.put(
                                        ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
                                                        .getValue(),
                                        fechaComprobante);
                        paramConsultaLista.put(
                                        ComprobanteCntAfectarControladorEnum.ANOCOMPROBANTE
                                                        .getValue(),
                                        anoComprobante);
                    }
        visibleOrdenador = "SI".equals(getParametro(
                        "MANEJA CAMBIO DE SESIONES EN CONTRATOS", "NO"));
    }

    /**
     * Define la consulta de la lista que debe cargarse para afectar un
     * comprobante contable.
     */
    private void inicializarComprobanteCntAfectar() {
        if ((terceroEnEncabezado != null) && "SI".equals(terceroEnEncabezado)) {
            inicializarComprobanteCntAfectarAuxiliar();
        }
        else if ((tipoCpteAfect != null) && "V".equals(tipoCpteAfect)) {
            urlconsultaLista = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ComprobanteCntAfectarControladorUrlEnum.URL007
                                                            .getValue());
            paramConsultaLista.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                            .getValue(),
                            terceroComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                            .getValue(),
                            sucursalComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
                                            .getValue(),
                            compRelacionado);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
                                            .getValue(),
                            fechaComprobante);

        }
        else {
            urlconsultaLista = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ComprobanteCntAfectarControladorUrlEnum.URL008
                                                            .getValue());
            paramConsultaLista.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                            .getValue(),
                            terceroComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                            .getValue(),
                            sucursalComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
                                            .getValue(),
                            compRelacionado);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
                                            .getValue(),
                            fechaComprobante);
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            visibleInconsistencias = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "CONTROLA CUENTA BANCARIA Y RUBRO PRESUPUESTAL",
                                            modulo, new Date(), true), "NO"));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaLista() {
        try {
            listaLista = new RegistroDataModelImpl(urlconsultaLista.getUrl(),
                            urlconsultaLista.getUrlConteo().getUrl(),
                            paramConsultaLista, false,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "COMPROBANTE_CNT"),
                            true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {

        if (listaLista.getSeleccionados().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(TB_TB686));
            return;
        }
        else {
            listaSeleccionados = listaLista.getSeleccionados();
        }

        if ("R".equals(nombreFormulario)) { // Comprobante_cntAfectarRES

            aceptarEquivalenciaPresupuestal();

        }else if(claseComprobante.equals("O")) {
        	afectarAFC();
        }
        else {
            // Comprobante_cntAfectar
            afectarComprobante();
        }

    }

    private void afectarAFC() {
    	try {

    		ejbContabilidadSeis.generarADC(compania,
    				Integer.parseInt(anoComprobante),
    				tipoComprobante,
    				new BigInteger(numeroComprobante),
    				SysmanFunciones.convertirAFecha(
    						fechaComprobante),
    				claseComprobante,
    				listaComprobanteAfectar(
    						listaSeleccionados),
    				SessionUtil.getUser().getCodigo());
    		JsfUtil.agregarMensajeInformativo(
    				idioma.getString("MSM_PROCESO_EJECUTADO"));
    		oprimirCancelar();

    	}
    	catch (NumberFormatException  | SystemException | ParseException e) {
    		JsfUtil.agregarMensajeError(e.getMessage());
    		logger.error(e.getMessage(), e);
    	}

	}

	/**
     * 
     * Metodo ejecutado al oprimir el boton Inconsistencias en la vista
     *
     *
     */
    public void oprimirInconsistencias() {
        // <CODIGO_DESARROLLADO>

        if (listaLista.getSeleccionados().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(TB_TB686));
            return;
        }
        else {
            listaSeleccionados = listaLista.getSeleccionados();
        }
        try {
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(ejbContabilidadSiete
                                            .validarCuentasEquivalentesEgr(
                                                            compania,
                                                            Integer.parseInt(
                                                                            anoComprobante),
                                                            listaComprobanteAfectar(
                                                                            listaSeleccionados))),
                            "Cuentas Equivalentes no configuradas.txt");
        }
        catch (NumberFormatException | JRException | IOException
                        | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Proceso para afectar un comprobante contable.
     * 
     * Este metodo se realiza con el fin de que se puedan generar los dialogos
     * de consecutivo anterior al generar las anulaciones.
     */
    private void afectarComprobante() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("anoEgreso", anoComprobante);
        parametros.put("tipoEgreso", tipoComprobante);
        parametros.put("numeroEgreso", numeroComprobante);
        parametros.put("listaAfectar", listaAfectar());
        parametros.put(cRowIdComprobante, rowIdComprobante);
        parametros.put(VLR_DOCUMENTO, vlrDocumento);
        parametros.put("terceroComprobante", terceroComprobante);
        parametros.put("sucursalComprobante", sucursalComprobante);
        parametros.put("centroCostoComprobante", centroCosto);
        parametros.put("auxiliarComprobante", auxiliarComprobante);
        parametros.put("fechaComprobante", fechaComprobante);
        parametros.put("mesComprobante", mesComprobante);
        parametros.put("visibleOrdenador", visibleOrdenador);
        parametros.put("claseComprobante", claseComprobante);
        parametros.put("vlrGirar", vlrGirar);
        parametros.put("opcionMenu", opcionMenu);

        if ("A".equals(nombreFormulario)) {

            if ("E".equalsIgnoreCase(claseComprobante)
                || "G".equalsIgnoreCase(claseComprobante)) {

                Direccionador direccionador = new Direccionador();
                direccionador.setRuta("/comprobantecntbancos.sysman");
                direccionador.setParametros(parametros);
                SessionUtil.redireccionarLocal(direccionador);

            }
            else
					if ("SIB".contains(claseComprobante) || "N".equalsIgnoreCase(claseComprobante)) {
						/*
						 * Abre el formulario pedirFactura Se abre el mismo dialogo para para ambos
						 * tipos de comprobante, pero en la función cargarListaNumeroCuenta se carga la
						 * lista de cuentas con aquellas cuya clase cuenta sea 'B' o 'J'
						 */
						
		//INI_7741561_CONTABILIDAD (mrosero)
						
						if ("N".equalsIgnoreCase(claseComprobante)) {
							encabezadoPedirCuenta = idioma.getString("TB_TB2471");
							pedirCuentaAfectar = false;
							aceptarPedirCuentaAfectar();
							
						} else {
							encabezadoPedirCuenta = idioma.getString("TB_TB2471");
							pedirCuentaAfectar = true;
						}
		//FIN_7741561_CONTABILIDAD (mrosero)				
					}
                else if ("AJTRU".contains(claseComprobante)) {
                    try {
                        ejbContabilidadSeis.generarAnulacion(compania,
                                        Integer.parseInt(anoComprobante),
                                        tipoComprobante,
                                        new BigInteger(numeroComprobante),
                                        SysmanFunciones.convertirAFecha(
                                                        fechaComprobante),
                                        claseComprobante,
                                        listaComprobanteAfectar(
                                                        listaSeleccionados),
                                        SessionUtil.getUser().getCodigo());
                        oprimirCancelar();
                    }
                    catch (NumberFormatException | SystemException
                                    | ParseException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }
                else {
                    RequestContext.getCurrentInstance().closeDialog(null);
                }
        }
        else {
            encabezadoPedirCuenta = idioma.getString("TB_TB2471");
            pedirCuentaAfectar = true;
            // Pendiente solucionar Presupuesto
        }
    }

    private List<Registro> listaAfectar() {
        HashMap<String, Object> map;
        List<Registro> listaComponentes = new ArrayList<>();
        for (Registro reg : listaSeleccionados) {
            map = new HashMap<>();
            map.put("ano", reg.getCampos().get("ANO"));
            map.put("tipo", reg.getCampos().get("TIPO"));
            map.put("numero", reg.getCampos().get(cNumero));
            map.put("clasecuenta", claseComprobante);
            listaComponentes.add(new Registro(map));
        }
        return listaComponentes;
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("accion", "0");
        SessionUtil.setFlash(parametros);
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaLista(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaLista() {
        return listaLista;
    }

    public void setListaLista(RegistroDataModelImpl listaLista) {
        this.listaLista = listaLista;
    }

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    /**
     * Asigna la variable visibleDialogo
     * 
     * @param numeroCuenta
     * Indica si el dialogo es visible.
     */
    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    /**
     * Retorna la variable visibleDialogoAdvertencia
     * 
     * @return visibleDialogoAdvertencia
     */
    public boolean isVisibleDialogoAdvertencia() {
        return visibleDialogoAdvertencia;
    }

    /**
     * Asigna la lista listaNumeroCuenta
     * 
     * @param listaNumeroCuenta
     * Variable a asignar en listaNumeroCuenta
     */
    public void setVisibleDialogoAdvertencia(
        boolean visibleDialogoAdvertencia) {
        this.visibleDialogoAdvertencia = visibleDialogoAdvertencia;
    }

    /**
     * Retorna la lista listaNumeroCuenta
     * 
     * @return listaNumeroCuenta
     */
    public RegistroDataModelImpl getListaNumeroCuenta() {
        return listaNumeroCuenta;
    }

    /**
     * Asigna la lista listaNumeroCuenta
     * 
     * @param listaNumeroCuenta
     * Variable a asignar en listaNumeroCuenta
     */
    public void setListaNumeroCuenta(RegistroDataModelImpl listaNumeroCuenta) {
        this.listaNumeroCuenta = listaNumeroCuenta;
    }

    /**
     * Retorna la variable numeroCuenta
     * 
     * @return numeroCuenta
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * Asigna la variable numeroCuenta
     * 
     * @param numeroCuenta
     * Valor a asignar en numeroCuenta
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * Retorna la variable encabezadoPedirCuenta
     * 
     * @return texto del encabezado del dialogo
     */
    public String getEncabezadoPedirCuenta() {
        return encabezadoPedirCuenta;
    }

    /**
     * Asigna valor a la variable encabezadoPedirCuenta
     * 
     * @param encabezadoPedirCuenta
     * Texto que se mostrara en la barra de titulo del dialogo.
     */
    public void setEncabezadoPedirCuenta(String encabezadoPedirCuenta) {
        this.encabezadoPedirCuenta = encabezadoPedirCuenta;
    }

    /**
     * Retorna el valor de la variable pedirCuentaAfectar.
     * 
     * @return <code>true</code> si el dialogo PedirCuentaAfectar es visible.
     */
    public boolean isPedirCuentaAfectar() {
        return pedirCuentaAfectar;
    }

    /**
     * Asigna valor a la variable pedirCuentaAfectar
     * 
     * @param pedirCuentaAfectar
     * <code>true</code> si se debe mostrar el dialogo PedirCuentaAfectar.
     */
    public void setPedirCuentaAfectar(boolean pedirCuentaAfectar) {
        this.pedirCuentaAfectar = pedirCuentaAfectar;
    }

    /**
     * Retorna la variable controlChequera
     * 
     * @return texto del encabezado del dialogo
     */
    public String getControlChequera() {
        return controlChequera;
    }

    /**
     * Asigna valor a la variable controlChequera
     * 
     * @param controlChequera
     */
    public void setControlChequera(String controlChequera) {
        this.controlChequera = controlChequera;
    }

    /**
     * Carga la lista listaNumeroCuenta.
     */
    public void cargarListaNumeroCuenta() {
        UrlBean urlBean;

        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobanteCntAfectarControladorUrlEnum.URL39812
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoComprobante);

        listaNumeroCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CODIGO");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaNumeroCuenta.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroCuenta = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * PedirCuentaAfectar en la vista.
     */
	public void aceptarPedirCuentaAfectar() {
		// <CODIGO_DESARROLLADO>
		
		  //INI_7741561_CONTABILIDAD (mrosero)    
		try {
			if ("N".equalsIgnoreCase(claseComprobante)) {
				ejbContabilidadSeis.generarIngresoNotasCliente(compania, Integer.parseInt(anoComprobante),
						tipoComprobante, new BigInteger(numeroComprobante), terceroComprobante, sucursalComprobante,
						listaComprobanteAfectar(listaSeleccionados), SysmanFunciones.convertirAFecha(fechaComprobante),
						claseComprobante, SessionUtil.getUser().getCodigo());
			} else {
				ejbContabilidadSeis.generarIngreso(compania, Integer.parseInt(anoComprobante), tipoComprobante,
						new BigInteger(numeroComprobante), terceroComprobante, sucursalComprobante,
						listaComprobanteAfectar(listaSeleccionados), SysmanFunciones.convertirAFecha(fechaComprobante),
						claseComprobante, numeroCuenta, SessionUtil.getUser().getCodigo());
			}
		 //FIN_7741561_CONTABILIDAD (mrosero)  
			cargarDescripcion();
            JsfUtil.agregarMensajeInformativo(
                    idioma.getString("MSM_PROCESO_EJECUTADO"));
			RequestContext.getCurrentInstance().closeDialog(null);

		} catch (NumberFormatException | SystemException | ParseException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

    /**
     * Arma la estructura en la que se envian las llaves de los comprobantes que
     * se van a afectar
     * 
     * @param listaAfectar
     * el listado de los comprobantes a afectar que han sido seleccionados en el
     * formulario
     * @return Cadena con la informacion de los comprobantes a afectar
     */
    private String listaComprobanteAfectar(List<Registro> listaAfectar) {
        String comprobante;
        StringBuilder rta = new StringBuilder();

        for (Registro reg : listaAfectar) {
            rta.append("(''"
                + reg.getCampos().get(GeneralParameterEnum.ANO.getName())
                + "'',")
                            .append("''" + reg.getCampos().get(
                                            ComprobanteCntAfectarControladorEnum.TIPO
                                                            .getValue())
                                + "'',")
                            .append("''" + reg.getCampos().get(cNumero) + "'')")
                            .append(",");

        }
        comprobante = rta.toString();
        comprobante = comprobante.substring(0, comprobante.length() - 1);

        return comprobante;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * EquivalenciaPresupuestal en la vista.
     *
     */
    public void aceptarEquivalenciaPresupuestal() {
        // <CODIGO_DESARROLLADO>
        if (generarComprobantePresupuestal()) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
            RequestContext.getCurrentInstance().closeDialog(null);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo validarCuentas
     * en la vista
     *
     *
     */
    public void aceptarvalidarCuentas() {
        // <CODIGO_DESARROLLADO>
        try {

            listaSeleccionados = listaLista.getSeleccionados();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(ejbContabilidadSiete
                                            .validarCuentasEquivalentesEgr(
                                                            compania,
                                                            Integer.parseInt(
                                                                            anoComprobante),
                                                            listaComprobanteAfectar(
                                                                            listaSeleccionados))),
                            "Cuentas Equivalentes no configuradas.txt");

            if (listaLista.getSeleccionados().isEmpty()) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(TB_TB686));
                return;
            }
            else {
                listaSeleccionados = listaLista.getSeleccionados();
            }

            if ("R".equals(nombreFormulario)) { // Comprobante_cntAfectarRES

                aceptarEquivalenciaPresupuestal();

            }
            else {
                // Comprobante_cntAfectar
                afectarComprobante();
            }

        }
        catch (NumberFormatException | JRException | IOException
                        | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
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
     * Define la consulta de la lista que debe cargarse para generar el
     * comprobante presupuestal (Comprobante_cntAfectarRES).
     */
    private void inicializarComprobanteCntAfectarRES() {
        String cadena = "HEREDAR COMPROBANTES VIGENCIA FUTURA ANIO POSTERIOR";
        if ("NO".equals(getParametro(cadena, "NO"))) {
            cadena = "SI";
        }
        else {
            cadena = "NO";
        }
        try {
            Map<String, Object> paramsReg = new TreeMap<>();
            paramsReg.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramsReg.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                            tipoComprobante);
            Registro reg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ComprobanteCntAfectarControladorUrlEnum.URL009
                                                                                            .getValue())
                                                            .getUrl(),
                                            paramsReg));
            String claseAfectar = null;
            if (reg != null) {
                claseAfectar = extraerString(
                                reg.getCampos().get(
                                                ComprobanteCntAfectarControladorEnum.CLASEAFECTAR
                                                                .getValue()));
            }
            urlconsultaLista = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ComprobanteCntAfectarControladorUrlEnum.URL010
                                                            .getValue());
            paramConsultaLista.clear();
            paramConsultaLista.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            paramConsultaLista.put(GeneralParameterEnum.COMPROBANTE_AFECT.getName(),
            		tipoComprobante); //JM 30/01/2025 CC774
            
            paramConsultaLista.put(GeneralParameterEnum.ANO.getName(),
                            anoComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.CLASEAFECTAR
                                            .getValue(),
                            claseAfectar);
            paramConsultaLista.put(ComprobanteCntAfectarControladorEnum.CADENA
                            .getValue(), cadena);
            paramConsultaLista.put(ComprobanteCntAfectarControladorEnum.FECHARES
                            .getValue(), fechaComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
                                            .getValue(),
                            terceroComprobante);
            paramConsultaLista.put(
                            ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
                                            .getValue(),
                            sucursalComprobante);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Proceso para generar el comprobante presupuestal.
     * 
     * @return
     */
    private boolean generarComprobantePresupuestal() {
        for (Registro registro : listaSeleccionados) {
            Map<String, Object> comprobante = registro.getCampos();
            if (!revisarSaldoRegistro(comprobante)) {
                /*
                 * No se afecta presupuestalmente. Saldo del registro inferior a
                 * la orden de pago.
                 */
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2687"));
                return false;
            }
            else
                return generarComprobantePptalvarios(comprobante);
        }
        return false;
    }

    /**
     * Desde un registro de comprobantes contables, realiza la generacion de un
     * comprobante presupuestal asociado.
     * 
     * @param comprobante
     * Comprobante a afectar
     * @return
     */
    private boolean generarComprobantePptalvarios(
        Map<String, Object> comprobante) {
        try {
            ejbContabilidadCinco.generarComprobantePresupuestalVarios(compania,
                            Integer.parseInt(comprobante
                                            .get(GeneralParameterEnum.ANO
                                                            .getName())
                                            .toString()),
                            tipoComprobante,
                            new BigInteger(numeroComprobante), true,
                            listaComprobanteAfectar(listaSeleccionados),
                            "(''" + terceroComprobante + "'',''"
                                + sucursalComprobante + "'')",
                            SessionUtil.getUser().getCodigo());
            return true;
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }
    }

    /**
     * Revisa si el valor del comprobante contable es menor o igual al valor del
     * registro.
     * 
     * @param comprobante
     * Comprobante a afectar
     * @return Verdadero si el saldo es v&aacute;lido.
     */
    private boolean revisarSaldoRegistro(Map<String, Object> comprobante) {
        boolean continuar = true;
        String parametro = "CONTROLAR SALDO DE REGISTRO EN ORDEN DE PAGO";
        if ("SI".equals(getParametro(parametro, ""))
            && "P".equals(claseComprobante)) {
            String urlValue = ComprobanteCntAfectarControladorUrlEnum.URL016
                            .getValue();

            parametro = "CONTROLA SOLO CUENTA GASTOS EN SALDO REGISTRO";
            if ("SI".equals(getParametro(parametro, ""))) {
                urlValue = ComprobanteCntAfectarControladorUrlEnum.URL017
                                .getValue();
            }

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.ANO.getName(), anoComprobante);
            params.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                            tipoComprobante);
            params.put(GeneralParameterEnum.NUMERO.getName(),
                            numeroComprobante);

            try {
                Registro reg = RegistroConverter.toRegistro(requestManager
                                .get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                urlValue)
                                                .getUrl(), params));

                double valorDoc = Double.parseDouble(
                                reg.getCampos().get(
                                                ComprobanteCntAfectarControladorEnum.VALORDOC
                                                                .getValue())
                                                .toString());

                continuar = ejbContabilidadCinco.verificarSaldoRegistro(
                                compania,
                                Integer.parseInt(comprobante
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                                .toString()),
                                comprobante.get(ComprobanteCntAfectarControladorEnum.TIPO
                                                .getValue()).toString(),
                                new BigInteger(comprobante.get(cNumero)
                                                .toString()),
                                BigDecimal.valueOf(valorDoc));
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }

        }
        return continuar;
    }

    /**
     * Evalua si el valor del campo que ingresa por parametro se encuentra vacio
     * dentro del Map que tambien es enviado por parametro
     * 
     * @param parametros
     * Estructura que almacena los parametros que han sido enviados desde otros
     * formularios
     * @param campo
     * El campo a evaluar dentro de la estructura de Map
     * @return El valor del campo o cadena vacia si su valor es nulo
     */
    private String validarCampos(Map<String, Object> parametros, String campo) {
        return SysmanFunciones.nvl(parametros.get(campo), "").toString();

    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    public void cargarDescripcion() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANIO.getName(), anoComprobante);
            param.put(GeneralParameterEnum.TIPO.getName(), tipoComprobante);
            param.put(GeneralParameterEnum.NUMERO.getName(), numeroComprobante);

            Registro rsDatos;

            rsDatos = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            ComprobantecntsControladorUrlEnum.URL678
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            if (rsDatos != null) {
                Map<String, Object> datos = new HashMap<>();
                datos.put("descripcion",
                                rsDatos.getCampos().get("DESCRIPCION"));
                datos.put(VLR_DOCUMENTO,
                                rsDatos.getCampos().get("VLR_DOCUMENTO"));
                SessionUtil.setSessionVar("descripcionVlr", datos);
            }
        }
        catch (SystemException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable visibleInconsistencias
     * 
     * @return visibleDigitos
     */
    public boolean isVisibleInconsistencias() {
        return visibleInconsistencias;
    }

    /**
     * Asigna la variable visibleInconsistencias
     * 
     * @param visibleInconsistencias
     * Variable a asignar en visibleInconsistencias
     */
    public void setVisibleInconsistencias(boolean visibleInconsistencias) {
        this.visibleInconsistencias = visibleInconsistencias;
    }

}
