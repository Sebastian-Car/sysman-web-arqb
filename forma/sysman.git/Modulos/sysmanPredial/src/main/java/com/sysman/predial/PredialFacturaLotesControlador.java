package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.PredialFacturaLotesControladorEnum;
import com.sysman.predial.enums.PredialFacturaLotesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
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
 * @author dmaldonado
 * @version 1, 15/06/2016 16:27:05 -- Modificado por dmaldonado
 *
 * @version 2, 12/07/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class PredialFacturaLotesControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String numeroOrden;
    // <DECLARAR_ATRIBUTOS>
    private Double valorInferior;
    private Double valorSuperior;
    private String codigoInicial;
    private String codigoFinal;
    private String dirInicial;
    private String dirFinal;
    private String nombreInicial;
    private String nombreFinal;
    private String nitInicial;
    private String nitFinal;
    private int anoInicial;
    private int anoFinal;
    private String visibleBtListaFact;
    private String visibleCmdPlano;
    private String anoHasta;
    private String tipoPredio;
    private String orden;
    private String observaciones;
    private Date limiteAlDia;
    private Date limiteMorosos;
    private boolean confirmarVisible;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTxtHastaAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigoini;
    private RegistroDataModelImpl listacodigofin;
    private RegistroDataModelImpl listaCmbDirInicial;
    private RegistroDataModelImpl listaCmbDirFinal;
    private RegistroDataModelImpl listaCmbNombreInicial;
    private RegistroDataModelImpl listaCmbNombreFinal;
    private RegistroDataModelImpl listaNitIni;
    private RegistroDataModelImpl listaNitFin;
    private static final String CODIGO = GeneralParameterEnum.CODIGO.getName();
    private static final String DIRECCION = GeneralParameterEnum.DIRECCION
                    .getName();
    private static final String NOMBRE = GeneralParameterEnum.NOMBRE.getName();
    private static final String CONDICION = "condicion";
    private static final String ZZZZ = "zzzzzzzzzzzzzz";

    @EJB
    private EjbPredialCeroRemote ejbPredialCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    private String mensajeCantidad;

    private boolean visibleCantidad;
    private boolean manejaAsoBancaria;
    private String formatoFactura;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public PredialFacturaLotesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIAL_FACTURA_LOTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PredialFacturaLotesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigoini();
        cargarListaCmbDirInicial();
        cargarListaCmbNombreInicial();
        cargarListaNitIni();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTxtHastaAno();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        tabla = "";
        asignarOrigenDatos();

        visibleBtListaFact = "none";
        visibleCmdPlano = "none";

        cargarListacodigoini();
        cargarListaCmbDirInicial();
        cargarListaCmbNombreInicial();
        cargarListaNitIni();
        cargarListaTxtHastaAno();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTxtHastaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTxtHastaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialFacturaLotesControladorUrlEnum.URL6045
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacodigoini() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL6587
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listacodigoini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListacodigofin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL7617
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PredialFacturaLotesControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);
        listacodigofin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaCmbDirInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL8726
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listaCmbDirInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, DIRECCION);
    }

    public void cargarListaCmbDirFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL9507
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PredialFacturaLotesControladorEnum.DIRINICIAL.getValue(),
                        dirInicial);
        listaCmbDirFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, DIRECCION);
    }

    public void cargarListaCmbNombreInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL10368
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listaCmbNombreInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NOMBRE);
    }

    public void cargarListaCmbNombreFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL11080
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PredialFacturaLotesControladorEnum.MINOMBRE.getValue(),
                        nombreInicial);
        listaCmbNombreFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NOMBRE);

    }

    public void cargarListaNitIni() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL11843
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listaNitIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaNitFin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL12509
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PredialFacturaLotesControladorEnum.NITINICIAL.getValue(),
                        nitInicial);
        listaNitFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigoini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        cargarListacodigofin();
    }

    public void seleccionarFilacodigofin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
    }

    public void seleccionarFilaCmbDirInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dirInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(DIRECCION), "")
                        .toString();
        cargarListaCmbDirFinal();
    }

    public void seleccionarFilaCmbDirFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dirFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(DIRECCION), "")
                        .toString();
    }

    public void seleccionarFilaCmbNombreInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NOMBRE), "")
                        .toString();
        cargarListaCmbNombreFinal();
    }

    public void seleccionarFilaCmbNombreFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NOMBRE), "")
                        .toString();
    }

    public void seleccionarFilaNitIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nitInicial = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        cargarListaNitFin();
    }

    public void seleccionarFilaNitFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nitFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * confirmaCantidad en la vista
     */
    public void aceptarconfirmaCantidad() {
        // <CODIGO_DESARROLLADO>
        String inicioFacturacion = null;
        archivoDescarga = null;
        try {
            inicioFacturacion = ejbSysmanUtil.consultarParametro(
                            compania, "INICIO FACTURACION", modulo, new Date(),
                            true);
            if ("NO".equals(inicioFacturacion)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3319"));
                return;
            }

            if (actualizaParametro("NO")) {// facturarenlote
                String rta = ejbPredialOcho.facturarEnLote(compania,
                                SessionUtil.getCompaniaIngreso().getNit(),
                                SessionUtil.getCompaniaIngreso().getSigla(),
                                numeroOrden, codigoInicial, codigoFinal,
                                dirInicial, dirFinal, nombreInicial,
                                nombreFinal, nitInicial, nitFinal,
                                anoInicial,
                                anoFinal,
                                new BigDecimal(valorInferior),
                                new BigDecimal(valorSuperior),
                                Integer.parseInt(anoHasta), limiteAlDia,
                                tipoPredio, SessionUtil.getUser().getCodigo());
                rta = rta == null ? "" : rta;
                String[] recibos = rta.split(",");

                String condicion = SysmanFunciones.concatenar(
                                "AND RP.DOCNUM BETWEEN '", recibos[0],
                                "' AND '",
                                recibos[1], "'");
                generaFormatoFactura(FORMATOS.PDF, condicion);
            }
        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeErrorDialogo(e.getMessage());
        }
        finally {
            actualizaParametro("SI");
            visibleCantidad = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean actualizaParametro(String valor) {

        int rtaActualiza = 0;

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialFacturaLotesControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.VALOR.getName(), valor);
        fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        fields.put("KEY_COMPANIA", compania);
        fields.put("KEY_NOMBRE", "INICIO FACTURACION");
        fields.put(PredialFacturaLotesControladorEnum.MODULO.getValue(),
                        modulo);
        Parameter parameter = new Parameter();
        parameter.setFields(fields);
        try {
            rtaActualiza = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rtaActualiza > 0;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * confirmaImpresion en la vista
     */
    public void aceptarconfirmaImpresion() {
        // <CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * confirmaImpresion en la vista
     */
    public void cancelarconfirmaImpresion() {
        // <CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirFactura() {
        // <CODIGO_DESARROLLADO>
        if (formatoFactura == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3324"));
            return;
        }
        Long cantidadUsuarios = null;
        try {
            cantidadUsuarios = ejbPredialOcho
                            .calcularUsuariosFacturaEnLote(
                                            compania,
                                            numeroOrden,
                                            codigoInicial,
                                            codigoFinal,
                                            dirInicial,
                                            dirFinal,
                                            nombreInicial,
                                            nombreFinal,
                                            nitInicial,
                                            nitFinal,
                                            anoInicial,
                                            anoFinal,
                                            new BigDecimal(valorInferior),
                                            new BigDecimal(valorSuperior),
                                            anoFinal,
                                            tipoPredio);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (cantidadUsuarios > 0) {
            mensajeCantidad = idioma.getString("TB_TB3312")
                            .replace("#cantidad#",
                                            Long.toString(cantidadUsuarios));
            visibleCantidad = true;
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3313"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirInforme() {
        // <CODIGO_DESARROLLADO>
        generaListadoFacturados(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPlano() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            if (manejaAsoBancaria) {
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(ejbPredialOcho
                                                .generarPlanoFacturacionAsoBanNoventaOcho(
                                                                compania,
                                                                codigoInicial,
                                                                codigoFinal)),
                                "Plano_Facturacion.txt");
            }
            else {
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(ejbPredialOcho
                                                .generaPlanoFacturacion(
                                                                compania,
                                                                codigoInicial,
                                                                codigoFinal,
                                                                nombreInicial,
                                                                nombreFinal,
                                                                anoInicial,
                                                                anoFinal,
                                                                Integer.parseInt(
                                                                                anoHasta),
                                                                nitInicial,
                                                                nitFinal,
                                                                numeroOrden,
                                                                orden,
                                                                new BigDecimal(valorInferior),
                                                                new BigDecimal(valorSuperior))),
                                "Plano_Facturacion.txt");
            }
        }
        catch (SystemException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>

    private void generaFormatoFactura(FORMATOS formato,
        String condicion)
                        throws SystemException, JRException, IOException,
                        SysmanException {
        archivoDescarga = null;
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        /* Manejo de reemplazos de la consulta */
        String codigoEAN = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania, "CODIGO EAN", modulo,
                                        new Date(), true),
                        "CONFIGURE EL PARAMETRO CODIGO EAN");
        reemplazar.put("strCodigoEAN", codigoEAN);
        reemplazar.put("condicionSub",
                        "AND RP.PAGO = 0 AND RP.ANULADO = 0 ");
        reemplazar.put(CONDICION,
                        SysmanFunciones.concatenar(condicion, " ORDER BY UP.",
                                        "3".equals(orden) ? DIRECCION
                                            : ("2".equals(orden) ? NOMBRE
                                                : CODIGO)));
        Reporteador.resuelveConsulta(formatoFactura,
                        Integer.parseInt(modulo), reemplazar, parametros);
        /* Manejo de parametros del informe */
        parametros.put("PR_ENC_1", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 1));
        parametros.put("PR_ENC_2", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 2));
        parametros.put("PR_ENC_3", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 3));
        parametros.put("PR_ENC_4", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 4));
        parametros.put("PR_ENC_13", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 13));
        parametros.put("PR_ENC_14", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 14));
        parametros.put("PR_ENC_15", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 15));
        parametros.put("PR_ENC_16", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 16));
        parametros.put("PR_ENC_19", ejbPredialCero
                        .consultarEncabezadoDeColumna(compania, 19));
        parametros.put("PR_ANOS_PAGOS", SysmanFunciones
                        .concatenar(Integer.toString(anoInicial), " A ",
                                        Integer.toString(anoFinal)));
        parametros.put("PR_COMPANIA", compania);
        parametros.put("PR_NITCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNit());
        parametros.put("PR_PAGINA_WEB",
                        SessionUtil.getCompaniaIngreso().getPaginaWeb());
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_OBSERVACIONES", observaciones);

        parametros.put("PR_LEYENDA_USUARIO", SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "LEYENDA USUARIO", modulo,
                                        new Date(), true),
                                        ""));
        parametros.put("PR_LEYENDA_LEGAL",
                        SysmanFunciones.nvlStr(
                                        ejbSysmanUtil.consultarParametro(
                                                        compania,
                                                        "LEYENDA LEGAL",
                                                        modulo, new Date(),
                                                        true),
                                        ""));
        parametros.put("PR_COPIA", false);

        archivoDescarga = JsfUtil.exportarStreamed(formatoFactura,
                        parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
    }

    private void generaListadoFacturados(FORMATOS formato) {
        archivoDescarga = null;
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("numeroOrden", numeroOrden);
            reemplazar.put("codigoInicial",
                            SysmanFunciones.nvlStr(codigoInicial, "0"));
            reemplazar.put("codigoFinal",
                            SysmanFunciones.nvlStr(codigoFinal, "0"));
            reemplazar.put("nitInicial", nitInicial);
            reemplazar.put("nitFinal", nitFinal);
            reemplazar.put("direccionInicial", dirInicial);
            reemplazar.put("direccionFinal", dirFinal);
            reemplazar.put("anoInicial", anoInicial);
            reemplazar.put("anoFinal", anoFinal);
            reemplazar.put("valorInferior",
                            SysmanFunciones.nvlDbl(valorInferior, 0.0));
            reemplazar.put("valorSuperior",
                            SysmanFunciones.nvlDbl(valorSuperior, 0.0));
            reemplazar.put("nombreInicial", nombreInicial);
            reemplazar.put("nombreFinal", nombreFinal);
            orden = SysmanFunciones.nvlStr(orden, "1");
            reemplazar.put("ordenamiento", SysmanFunciones
                            .concatenar(" ORDER BY ", "3".equals(orden)
                                ? DIRECCION
                                : ("2".equals(orden) ? NOMBRE : CODIGO)));
            Reporteador.resuelveConsulta("000827listadodefacturados",
                            Integer.parseInt(modulo), reemplazar, parametros);

            parametros.put("PR_ANOS_PAGOS", SysmanFunciones
                            .concatenar(Integer.toString(anoInicial), " A ",
                                            Integer.toString(anoFinal)));
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_NUMERO_ORDEN",
                            SysmanFunciones.colocarComillas(numeroOrden));
            parametros.put("PR_NOMBREBANCO", "");

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000827listadodefacturados", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        valorInferior = 0.0;
        valorSuperior = 9999999999999.00;
        dirInicial = " ";
        dirFinal = "zzzzzz";
        nombreInicial = " ";
        nombreFinal = ZZZZ;
        codigoInicial = "000000000000000";
        codigoFinal = "999999999999999";
        nitInicial = " ";
        nitFinal = ZZZZ;
        anoFinal = SysmanFunciones
                        .ano(new Date());
        anoInicial = anoFinal - 1;
        orden = "1";
        tipoPredio = "1";
        anoHasta = Integer.toString(anoFinal);
        try {
            limiteAlDia = SysmanFunciones
                            .convertirAFecha(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "FECHA LIMITE DE PAGO", modulo,
                                            new Date(), true));
            limiteMorosos = SysmanFunciones
                            .convertirAFecha(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "FECHA LIMITE DE PAGO MOROSOS",
                                            modulo,
                                            new Date(), true));
            if ("SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA LISTADO DE FACTURADOS",
                                            modulo, new Date(), true),
                                            "NO"))) {
                visibleBtListaFact = "block";
            }
            if ("8912800003".equals(
                            SessionUtil.getCompaniaIngreso().getNit())) {
                visibleCmdPlano = "block";
            }
            manejaAsoBancaria = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "GENERAR PLANO FACTURACION ASOBANCARIA",
                                            modulo, new Date(), true), "NO"));
            formatoFactura = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "FORMATO FACTURA",
                                            modulo, new Date(), false), "NO");
            observaciones = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "OBSERVACIONES FACTURACION",
                                            modulo, new Date(), false), "");
        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
    public Double getValorInferior() {
        return valorInferior;
    }

    public void setValorInferior(Double valorInferior) {
        this.valorInferior = valorInferior;
    }

    public Double getValorSuperior() {
        return valorSuperior;
    }

    public void setValorSuperior(Double valorSuperior) {
        this.valorSuperior = valorSuperior;
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

    public String getDirInicial() {
        return dirInicial;
    }

    public void setDirInicial(String dirInicial) {
        this.dirInicial = dirInicial;
    }

    public String getDirFinal() {
        return dirFinal;
    }

    public void setDirFinal(String dirFinal) {
        this.dirFinal = dirFinal;
    }

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public String getNitInicial() {
        return nitInicial;
    }

    public void setNitInicial(String nitInicial) {
        this.nitInicial = nitInicial;
    }

    public String getNitFinal() {
        return nitFinal;
    }

    public void setNitFinal(String nitFinal) {
        this.nitFinal = nitFinal;
    }

    public int getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(int anoInicial) {
        this.anoInicial = anoInicial;
    }

    public int getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(int anoFinal) {
        this.anoFinal = anoFinal;
    }

    public String getAnoHasta() {
        return anoHasta;
    }

    public void setAnoHasta(String anoHasta) {
        this.anoHasta = anoHasta;
    }

    public String getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(String tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getLimiteAlDia() {
        return limiteAlDia;
    }

    public void setLimiteAlDia(Date limiteAlDia) {
        this.limiteAlDia = limiteAlDia;
    }

    public Date getLimiteMorosos() {
        return limiteMorosos;
    }

    public void setLimiteMorosos(Date limiteMorosos) {
        this.limiteMorosos = limiteMorosos;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTxtHastaAno() {
        return listaTxtHastaAno;
    }

    public void setListaTxtHastaAno(List<Registro> listaTxtHastaAno) {
        this.listaTxtHastaAno = listaTxtHastaAno;
    }

    public String getVisibleBtListaFact() {
        return visibleBtListaFact;
    }

    public void setVisibleBtListaFact(String visibleBtListaFact) {
        this.visibleBtListaFact = visibleBtListaFact;
    }

    public String getVisibleCmdPlano() {
        return visibleCmdPlano;
    }

    public void setVisibleCmdPlano(String visibleCmdPlano) {
        this.visibleCmdPlano = visibleCmdPlano;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacodigoini() {
        return listacodigoini;
    }

    public void setListacodigoini(RegistroDataModelImpl listacodigoini) {
        this.listacodigoini = listacodigoini;
    }

    public RegistroDataModelImpl getListacodigofin() {
        return listacodigofin;
    }

    public void setListacodigofin(RegistroDataModelImpl listacodigofin) {
        this.listacodigofin = listacodigofin;
    }

    public RegistroDataModelImpl getListaCmbDirInicial() {
        return listaCmbDirInicial;
    }

    public void setListaCmbDirInicial(
        RegistroDataModelImpl listaCmbDirInicial) {
        this.listaCmbDirInicial = listaCmbDirInicial;
    }

    public RegistroDataModelImpl getListaCmbDirFinal() {
        return listaCmbDirFinal;
    }

    public void setListaCmbDirFinal(RegistroDataModelImpl listaCmbDirFinal) {
        this.listaCmbDirFinal = listaCmbDirFinal;
    }

    public RegistroDataModelImpl getListaCmbNombreInicial() {
        return listaCmbNombreInicial;
    }

    public void setListaCmbNombreInicial(
        RegistroDataModelImpl listaCmbNombreInicial) {
        this.listaCmbNombreInicial = listaCmbNombreInicial;
    }

    public RegistroDataModelImpl getListaCmbNombreFinal() {
        return listaCmbNombreFinal;
    }

    public void setListaCmbNombreFinal(
        RegistroDataModelImpl listaCmbNombreFinal) {
        this.listaCmbNombreFinal = listaCmbNombreFinal;
    }

    public RegistroDataModelImpl getListaNitIni() {
        return listaNitIni;
    }

    public void setListaNitIni(RegistroDataModelImpl listaNitIni) {
        this.listaNitIni = listaNitIni;
    }

    public RegistroDataModelImpl getListaNitFin() {
        return listaNitFin;
    }

    public void setListaNitFin(RegistroDataModelImpl listaNitFin) {
        this.listaNitFin = listaNitFin;
    }

    public boolean isConfirmarVisible() {
        return confirmarVisible;
    }

    public void setConfirmarVisible(boolean confirmarVisible) {
        this.confirmarVisible = confirmarVisible;
    }

    public String getMensajeCantidad() {
        return mensajeCantidad;
    }

    public void setMensajeCantidad(String mensajeCantidad) {
        this.mensajeCantidad = mensajeCantidad;
    }

    public boolean isVisibleCantidad() {
        return visibleCantidad;
    }

    public void setVisibleCantidad(boolean visibleCantidad) {
        this.visibleCantidad = visibleCantidad;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    // </SET_GET_ADICIONALES>
}
