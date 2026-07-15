package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.ejb.EjbContratosDosRemote;
import com.sysman.contratos.ejb.EjbContratosUnoLocal;
import com.sysman.contratos.enums.SubnovedadcontratosControladorEnum;
import com.sysman.contratos.enums.SubnovedadcontratosControladorUrlEnum;
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 21/10/2015
 * 
 * @author asana
 * @version 04 /09/2017, 28/09/2017 Se realiza refactoring.
 */
@ManagedBean
@ViewScoped
public class SubnovedadcontratosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private String modulo;
    /**
     * Constante definida para almacenar la cadena "NUMERO"
     */
    private final String cNumero;
    /**
     * Constante definida para almacenar la cadena "claseOrden"
     */
    private final String cClaseOrden;
    /**
     * Constante definida para almacenar la cadena "CEDULA"
     */
    private final String cCedula;
    /**
     * Constante definida para almacenar la cadena "CLASET"
     */
    private final String cClaseT;
    /**
     * Constante definida para almacenar la cadena "TIPOT"
     */
    private final String cTipoT;

    /**
     * Constante definida para almacenar la cadena "IMPRESO"
     */
    private final String cImpreso;
    /**
     * Constante definida para almacenar la cadena "TIPOMODELO"
     */
    private final String cTipoModelo;
    /**
     * Constante definida para almacenar la cadena "EXPEDIDACEDULA"
     */
    private final String cExpedidaCedula;
    /**
     * Constante definida para almacenar la cadena "FECHA"
     */
    private final String cFecha;
    /**
     * Contante definida para almacenar la cadena "INDADICION"
     */
    private final String cIndicadorAdicion;
    /**
     * Constante definida para almacenar la cadena "POREJECUCION"
     */
    private final String cPorcentajeEjecucion;

    /**
     * Constante definida para almacenar la cadena "FECHAINICIAL"
     */
    private final String cFechaInicial;
    /**
     * Constante definida para almacenar la cadena "NOVEDAD"
     */
    private final String cNovedad;

    /**
     * Constante definida para almacenar la cadena "DIAS_CONTRATO"
     */
    private final String cDiasContrato;
    /**
     * Constante definida para almacenar la cadena "VALORTOTAL"
     */
    private final String cValorTotal;

    /**
     * Constante definida para almacenar la cadena "FECHAFINAL"
     */
    private final String cFechaFinal;
    /**
     * Constante definida para almacenar la cadena "ORDENADOR"
     */
    private final String cOrdenador;
    /**
     * Constante definida para almacenar la cadena "NOMBRE"
     */
    private final String cNombre;
    /**
     * Constante definida para almacenar la cadena "TAUTOMATICA"
     */
    private final String cTautomatica;
    /**
     * Constante definida para almacenar la cadena "AFECTAITEMS"
     */
    private final String cAfectaItems;
    /**
     * Constante definida para almacenar la cadena "true"
     */
    private final String cTrue;
    /**
     * Constante definida para almacenar la cadena "ordenDeCompra"
     */
    private final String cOrdenCompra;

    /**
     * Constante definida para almacenar la cadena "OBSERVACIONES"
     */
    private final String cObservaciones;
    /**
     * Constante definida para almacenar la cadena "TB_TB2168"
     */
    private final String cMensaje;

    /**
     * Constante definida para la SUCRUSAL
     */
    private final String sucursal;

    private Registro registroinicial;

    private String cantidadReg;
    /**
     * Constante definida para almacenar la cadena " FROM
     * ORDENDECOMPRA "
     */
    private Map<String, Object> parametrosEntrada;

    private Map<String, Object> parametrosPagos;

    private Map<String, Object> llaveRID;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    @EJB
    private EjbContratosDosRemote ejbContratosDosRemote;
    @EJB
    private EjbContratosUnoLocal ejbContratosUnoLocal;

    private Registro registroSub;
    private List<Registro> listaNIT;
    private List<Registro> listaTercerosAportantes;
    private RegistroDataModelImpl listaOrdenador;
    private RegistroDataModelImpl listaTexto602;
    private RegistroDataModelImpl listatipoT;
    private String tipoContrato;
    private String numero;
    private String novedad;
    private String claseNov;
    private String txtNovedad;
    private String claseT;
    private String anio;
    private boolean manejaNominaDeContratistas;
    private boolean manejaIndicadorDeImpresoEnActas;
    private boolean habilitaCampoValorTotal;
    private boolean manejaBancoDeProyectos;
    private boolean manejaControlDeActas;
    private int nivelGrupo;
    private boolean visibleTipoRetencion;
    private boolean visibleValorLiberado;
    private boolean visibleVobo;
    private boolean visibleValorTotal;
    private boolean enabledAfectaItems;
    private boolean newRecord;
    private boolean manejaIndicadorEImpreso;
    private boolean visibleAlquiler;
    private boolean bloqueadoVobo;
    private boolean clasenovAYgrupoDif9;
    private boolean calificaContratistaEnActaDeTerminacion;
    private boolean visibleAmortizar;
    private String claseOrden;
    private Date fechaFirma;
    private Date fechaFinalizacion;
    private String txtFecha;
    private Double valorFinal;
    private Date fechaPolizas;
    private String plazoEntrega;
    private String titulo;
    private StreamedContent archivoDescarga;
    private String numeroRetorno;
    private boolean visibleCalificacionContr;
    private boolean generaAutomaticamenteConsecutivoDeNovedades;
    private boolean bloqueadoNumero;
    private boolean bloqueadoTipoT;
    private boolean bloqueadoClaseT;
    private boolean bloqueadoNovedad;
    private boolean bloqueadoAno;
    private boolean bloqueadoFecha;
    private boolean bloqueadoFechaInicial;
    private boolean bloqueadoValorLiberado;
    private boolean bloqueadoTexto602;
    private boolean bloqueadoTValorTotal;
    private boolean bloqueadoPejecucion;
    private boolean bloqueadoTipoRetencion;
    private boolean bloqueadoObservaciones;
    private boolean bloqueadoTexto66;
    private boolean bloqueadoDiasContrato;
    private boolean bloqueadoFechaFinal;
    private boolean bloqueadoValorTotal;
    private boolean bloqueadoTxtaportest;
    private boolean bloqueadoTxtVlrAqVehiculo;
    private boolean bloqueadoTxtResultados;
    private boolean visibleCumplimientoActiv;
    private boolean actualiza;
    private String nombreOrdenador;
    private String etiquetaMensaje;
    private int numeroMensaje;
    private String modelo;
    private List<Registro> listaformato;
    private String fechaFormato;
    private boolean visibleFormato;
    private String dependencia;
    private String valorTotalNovedad;
    private String valorAPagar;
    private boolean afectaItems;
    private String nNumero;
    private Registro rs;
    private Double valorTotalCont;
  
    
    /**
     * Indica si el proceso de creacion de novedades permite mostrar los adjuntos.
     * true: los adjuntos son visibles.
     * false: los adjuntos no se muestran.
     */
    private boolean visibleAdjuntos;

    private boolean visibleEval;
    private boolean indPago;
    private Map<String,Object> parametroswf;
    /**
     * Variable boolena: Maneja el proceso de Creacion Novedades
     */
	private boolean aplicasActasIni;

    public SubnovedadcontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNumero = "NUMERO";
        cClaseOrden = "claseOrden";
        cCedula = "CEDULA";
        cClaseT = "CLASET";
        cTipoT = "TIPOT";
        cImpreso = "IMPRESO";
        cTipoModelo = "TIPOMODELO";
        cExpedidaCedula = "EXPEDIDACEDULA";
        cFecha = "FECHA";
        cIndicadorAdicion = "INDADICION";
        cPorcentajeEjecucion = "POREJECUCION";
        cFechaInicial = "FECHAINICIAL";
        cNovedad = "NOVEDAD";
        cDiasContrato = "DIAS_CONTRATO";
        cValorTotal = "VALORTOTAL";
        cFechaFinal = "FECHAFINAL";
        cOrdenador = "ORDENADOR";
        cNombre = "NOMBRE";
        cTautomatica = "TAUTOMATICA";
        cAfectaItems = "AFECTAITEMS";
        cTrue = "true";
        cOrdenCompra = "ordenDeCompra";
        cObservaciones = "OBSERVACIONES";
        cMensaje = "TB_TB2168";
        nNumero = "numero";
        sucursal = "SUCURSAL";
        cantidadReg = "CANTIDAD";
        try {
            // Formulario 286
            numFormulario = GeneralCodigoFormaEnum.SUBNOVEDADCONTRATOS_CONTROLADOR
                            .getCodigo();
            parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
            if(parametroswf != null) {
                SessionUtil.setSessionVar("modulo", "9");
            }
            modulo = SessionUtil.getModulo();
            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());
            actualiza = false;
            parametrosEntrada = SessionUtil.getFlash();
            parametrosPagos = new HashMap<>(parametrosEntrada);

            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                llaveRID = (Map<String, Object>) parametrosEntrada.get("ridR");
                tipoContrato = (String) parametrosEntrada.get("tipoContrato");
                numero = (String) parametrosEntrada.get(cNumero.toLowerCase());
                claseNov = (String) parametrosEntrada.get("claseNov");
                claseOrden = (String) parametrosEntrada.get(cClaseOrden);
                fechaFirma = (Date) parametrosEntrada.get("fechaFirma");
                fechaFinalizacion = (Date) parametrosEntrada
                                .get("fechaFinalizacion");
                txtFecha = (String) parametrosEntrada.get("txtFecha");
                fechaPolizas = (Date) parametrosEntrada.get("fechaPolizas");
                valorFinal = (Double) parametrosEntrada.get("valorFinal");
                plazoEntrega = (String) parametrosEntrada.get("plazoEntrega");
                titulo = (String) parametrosEntrada.get("titulo");
                anio = (String) parametrosEntrada.get("anio");
                dependencia = (String) parametrosEntrada.get("dependencia");
                valorTotalNovedad = (String) parametrosEntrada
                                .get("valorTotalNovedad");

                valorAPagar = (String) parametrosEntrada.get("valorAPagar");
                valorTotalCont = (Double) parametrosEntrada.get("valorTotalCont");
                aplicasActasIni = Boolean.parseBoolean(SysmanFunciones.nvl(parametrosEntrada
                        .get("aplicasActasIni"), 0)
                        .toString());

                parametrosEntrada.remove(nNumero);
                parametrosEntrada.remove("fechaFirma");
                parametrosEntrada.remove("fechaFinalizacion");
                parametrosEntrada.remove("txtFecha");
                parametrosEntrada.remove("fechaPolizas");
                parametrosEntrada.remove("valorFinal");
                parametrosEntrada.remove("plazoEntrega");
                parametrosEntrada.remove(cClaseOrden);
            }
            validarPermisos();

        }
        catch (SysmanException | NamingException ex) {
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }

    }

    @PostConstruct
    public void inicializar() {
        tabla = SubnovedadcontratosControladorEnum.PARAM12.getValue();
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        tipoContrato);

        parametrosListado.put(
                        SubnovedadcontratosControladorEnum.PARAM5.getValue(),
                        numero);

        parametrosListado.put(
                        SubnovedadcontratosControladorEnum.PARAM7.getValue(),
                        claseNov);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubnovedadcontratosControladorUrlEnum.URL23317
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubnovedadcontratosControladorUrlEnum.URL56636
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SubnovedadcontratosControladorUrlEnum.URL24087
                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubnovedadcontratosControladorUrlEnum.URL24085
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubnovedadcontratosControladorUrlEnum.URL24086
                                                        .getValue());

    }

    public void initVariables() {
        nivelGrupo = SessionUtil.getNivelGrupo(modulo);
        registroinicial = null;
        afectaItems = false;
        // CODIGO INICIALMENTE EJECUTADO EN ABRIR FORMULARIO, SE PASA
        // AL
        // CONSTRUCTOR YA QUE SON VARIABLES QUE SE INSTANCIAN UNA SOLA
        // VES, EN ABRIR FORMULARIO SE ESTABAN EJECUTANDO 4 VECES
        manejaNominaDeContratistas = valorParametroIgnoreCase(
                        "MANEJA NOMINA DE CONTRATISTAS");

        manejaIndicadorDeImpresoEnActas = valorParametroIgnoreCase(
                        "MANEJA INDICADOR DE IMPRESO EN ACTAS");

        habilitaCampoValorTotal = valorParametroIgnoreCase(
                "HABILITA CAMPO VALOR TOTAL");
        
        manejaBancoDeProyectos = valorParametroIgnoreCase(
                        "MANEJA BANCO DE PROYECTOS");

        manejaControlDeActas = valorParametroIgnoreCase(
                        "MANEJA CONTROL DE ACTAS");

        calificaContratistaEnActaDeTerminacion = valorParametroIgnoreCase(
                        "CALIFICA CONTRATISTA EN ACTA DE TERMINACION");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Funcion que retorna verdadero o falso, dependiendo del nombre
     * del parametro recibido, se evalua con equalsIgnoreCase
     *
     * @param nombreParametro
     * @return true o false
     */
    private boolean valorParametroIgnoreCase(String nombreParametro) {
        boolean respuesta = false;

        try {
            respuesta = "SI".equalsIgnoreCase(
                            SysmanFunciones.nvl(ejbSysmanUtilRemote
                                            .consultarParametro(compania,
                                                            nombreParametro,
                                                            modulo, new Date(),
                                                            true),
                                            "NO").toString());
        }
        catch (NullPointerException | SystemException ex)

        {
            respuesta = false;
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return respuesta;

    }

    /**
     * Funcion que retorna verdadero o falso, dependiendo del nombre
     * del parametro recibido, se evalua con equals
     *
     * @param nombreParametro
     * @return true o false
     */
    private boolean valorParametro(String nombreParametro) {
        boolean respuesta = false;
        try {
            respuesta = "SI".equals(ejbSysmanUtilRemote.consultarParametro(
                            compania, nombreParametro, modulo, new Date(),
                            true));

        }
        catch (NullPointerException | SystemException ex) {
            respuesta = false;
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        return respuesta;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    @Override
    public void iniciarListas() {
        cargarListaOrdenador();
        cargarListaTexto602();
        cargarListatipoT();
        cargarListaNIT();
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaNIT() {
        Map<String, Object> param = new TreeMap<>();
        param.put(SubnovedadcontratosControladorEnum.PARAM0.getValue(),
                        String.valueOf(compania));
        try {
            listaNIT = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubnovedadcontratosControladorUrlEnum.URL25190
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaOrdenador() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubnovedadcontratosControladorUrlEnum.URL21955
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaOrdenador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCedula);

    }

    public void cargarListaTexto602() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubnovedadcontratosControladorUrlEnum.URL25597
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTexto602 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCedula);
    }

    public void cargarListatipoT() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubnovedadcontratosControladorUrlEnum.URL22474
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(SubnovedadcontratosControladorEnum.PARAM7.getValue(),
                        claseNov);
        param.put(SubnovedadcontratosControladorEnum.PARAM8.getValue(),
                        registro.getCampos().get(cClaseT));

        listatipoT = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cTipoT);

    }

    public void cargarListaformato() {

        String tipoT = registro.getCampos().get(cTipoT) == null ? ""
            : (String) registro.getCampos().get(cTipoT);
        String tipoFormato = "";

        Map<String, Object> param = new TreeMap<>();
        param.put(SubnovedadcontratosControladorEnum.PARAM7.getValue(),
                        claseNov);
        param.put(SubnovedadcontratosControladorEnum.PARAM8.getValue(),
                        registro.getCampos().get(cClaseT));
        param.put(cTipoT, tipoT);

        Registro rsNovedad;
        try {
            rsNovedad = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubnovedadcontratosControladorUrlEnum.URL24078
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            if(rsNovedad != null) {

            tipoFormato = rsNovedad.getCampos().get("TIPOFORMATO").toString();

            tipoFormato = tipoFormato.replace(" ", "");
            tipoFormato = tipoFormato.replace(",", "','");
            }
        }
        catch (NullPointerException | SystemException ex) {
            tipoFormato = "";
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        if (!tipoFormato.isEmpty()) {

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.FORMATO.getName(), tipoFormato);
            try {
                listaformato = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubnovedadcontratosControladorUrlEnum.URL10266
                                                                                                .getValue())
                                                                .getUrl(),
                                                params));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    public void validaformato() {

        String valorNovedadHasta;

        if (modelo != null) {
            String lugarExpCedulaSupervisor;
            String lugarExpCedulaInterventor;

            try {

                valorNovedadHasta = valorNovedad();

                Map<String, Object> params = new TreeMap<>();
                params.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                params.put(SubnovedadcontratosControladorEnum.PARAM3
                                .getValue(), claseOrden);
                params.put(SubnovedadcontratosControladorEnum.PARAM11
                                .getValue(),
                                numero);

                Registro rsExpedidaT;

                rsExpedidaT = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubnovedadcontratosControladorUrlEnum.URL24077
                                                                                                .getValue())
                                                                .getUrl(),
                                                params));
                lugarExpCedulaSupervisor = cedulaSupervisor(
                                rsExpedidaT);

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                param.put(SubnovedadcontratosControladorEnum.PARAM1
                                .getValue(), numero);

                Registro rsExpedida;

                rsExpedida = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubnovedadcontratosControladorUrlEnum.URL24076
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
                lugarExpCedulaInterventor = cedulaInterventor(
                                rsExpedida);
                StringBuilder strNombreDocumento = new StringBuilder();
                strNombreDocumento.append(txtNovedad);
                strNombreDocumento.append(" CONTRATO No. ");
                strNombreDocumento.append(numero);

                String[] campos = new String[3];
                String[] valores = new String[3];
                campos[0] = "codigoPlantilla";
                campos[1] = "fechaPlantilla";
                campos[2] = "nombreDocDescarga";

                valores[0] = modelo;
                valores[1] = fechaFormato;
                valores[2] = strNombreDocumento.toString();

                HashMap<String, String> variablesConsultaW = new HashMap<>();
                variablesConsultaW.put("s$compania$s",
                                SysmanFunciones.concatenar("'", compania, "'"));
                variablesConsultaW.put("s$ano$s", anio);
                variablesConsultaW.put("s$CLASEORDEN$s", SysmanFunciones
                                .concatenar("'", claseOrden, "'"));
                variablesConsultaW.put("s$NUMERO$s", numero);
                variablesConsultaW.put("s$NOVEDAD$s", (String) registro
                                .getCampos().get(cNovedad));
                variablesConsultaW.put("s$VLRTNOVEDADES$s",
                                valorTotalNovedad);
                variablesConsultaW.put("s$LUGAREXPCEDULASUPERVISOR$s",
                                SysmanFunciones.concatenar("'",
                                                lugarExpCedulaSupervisor, "'"));
                variablesConsultaW.put("s$LUGAREXPCEDULAINTERVENTOR$s",
                                SysmanFunciones.concatenar("'",
                                                lugarExpCedulaInterventor,
                                                "'"));
                DecimalFormat num = new DecimalFormat("#,###.00");
                variablesConsultaW.put("s$SALDOPORPAGAR$s", SysmanFunciones
                                .concatenar("'", num.format(Double
                                                .parseDouble(valorAPagar)),
                                                "'"));
                variablesConsultaW.put("s$VLRNOVEDADHASTA$s", SysmanFunciones
                                .concatenar("'", valorNovedadHasta, "'"));
                variablesConsultaW.put("s$CLASETRANSACCIONC$s",
                                (String) registro.getCampos()
                                                .get(cClaseT));
                // variables por parametro para documento word
                SessionUtil.setSessionVar("variablesConsultaWord",
                                variablesConsultaW);

                SessionUtil.cargarModalDatosFlash(Integer
                                .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(), campos,
                                valores);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2152"));

        }

    }

    public void datosOrdenador() {
        String tipoModelo = tipos();
        if ("R".equals(tipoModelo)) {
            generarReporte(ReportesBean.FORMATOS.PDF);

        }
        else {

            if ((listaformato != null) && !listaformato.isEmpty()) {
                validaformato();

            }
            else {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2057"));
                // Modelo = Nz(Me!TipoT.Column(7), "")
                // AbrirNovedad Modelo
            }

        }
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (manejaIndicadorDeImpresoEnActas) {
            if (!(boolean) registro.getCampos().get(cImpreso)) {
                manejaIndicadorEImpreso = false;
                bloqueadoNumero = false;
                bloqueadoTipoT = false;
                bloqueadoClaseT = false;
                bloqueadoNovedad = false;
                bloqueadoAno = false;
                bloqueadoFecha = false;
                bloqueadoFechaInicial = false;
                bloqueadoValorLiberado = false;
                bloqueadoTexto602 = false;
                bloqueadoTValorTotal = false;
                bloqueadoPejecucion = false;
                bloqueadoTipoRetencion = false;
                bloqueadoObservaciones = false;
                bloqueadoTexto66 = false;
                bloqueadoDiasContrato = false;
                bloqueadoFechaFinal = false;
                bloqueadoValorTotal = false;
                bloqueadoTxtaportest = false;
                bloqueadoTxtVlrAqVehiculo = false;
                bloqueadoTxtResultados = false;

            }
            else {
                manejaIndicadorEImpreso = true;
                bloqueadoNumero = true;
                bloqueadoTipoT = true;
                bloqueadoClaseT = true;
                bloqueadoNovedad = true;
                bloqueadoAno = true;
                bloqueadoFecha = true;
                bloqueadoFechaInicial = true;
                bloqueadoValorLiberado = true;
                bloqueadoTexto602 = true;
                bloqueadoTValorTotal = true;
                bloqueadoPejecucion = true;
                bloqueadoTipoRetencion = true;
                bloqueadoObservaciones = true;
                bloqueadoTexto66 = true;
                bloqueadoDiasContrato = true;
                bloqueadoFechaFinal = true;
                bloqueadoValorTotal = true;
                bloqueadoTxtaportest = true;
                bloqueadoTxtVlrAqVehiculo = true;
                bloqueadoTxtResultados = true;
                // ljdiaz (Luis Jacobo Diaz Muñoz) 
                // se aplica implementacion de marca blanca 
                String mensaje = idioma.getString("TB_TB2150");
                mensaje = mensaje.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
                JsfUtil.agregarMensajeInformativo(
                                mensaje);
                return;
            }

        }

        // </CODIGO_DESARROLLADO>
        datosOrdenador();
    }
    
    /**
     * Metodo que se ejecuta al oprimir la opcion de ver adjuntos.
     * Carga un modal con los datos necesarios para mostrar los archivos
     * asociados al contrato actual.
     */
    public void oprimirVerAdjuntos() {
        String[] campos = { 
            "claseOrden",      
            "numeroOrden",     
            "ordenDeCompra",   
            "novedad",         
            "claseT",          
            "tipoT"            
        };
        
        String[] valores = { 
            tipoContrato,                                              
            numero,                                                    
            String.valueOf(registro.getCampos().get("ORDENDECOMPRA")), 
            String.valueOf(registro.getCampos().get(cNovedad)),        
            String.valueOf(registro.getCampos().get(cClaseT)),         
            String.valueOf(registro.getCampos().get(cTipoT))           
        };
         
        SessionUtil.cargarModalDatos(
            String.valueOf(GeneralCodigoFormaEnum.DIGITALIZACION_CONTRATOS_CONTROLADOR.getCodigo()),
            modulo,
            campos, 
            valores
        );
        

    }
    /**
     *
     * @param rsSuma
     * @param sql
     * @return
     */
    private String valorNovedad() {

        String valorNovedadHasta = null;

        Map<String, Object> paramet = new TreeMap<>();

        paramet.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        paramet.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        paramet.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        paramet.put(SubnovedadcontratosControladorEnum.PARAM10
                        .getValue(),
                        registro.getCampos().get(cNovedad));
        paramet.put(SubnovedadcontratosControladorEnum.PARAM9
                        .getValue(), claseNov);

        Registro rsSuma;

        try {
            rsSuma = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubnovedadcontratosControladorUrlEnum.URL24064
                                                                                            .getValue())
                                                            .getUrl(),
                                            paramet));

            valorNovedadHasta = (rsSuma == null)
                || (rsSuma.getCampos().get("SUMADEVALORTOTAL") == null) ? ""
                    : rsSuma.getCampos().get("SUMADEVALORTOTAL").toString();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return valorNovedadHasta;
    }

    /**
     * Se llama en el metodo oprimirImprimir
     *
     * @return respuesta
     */
    private String tipos() {
        listatipoT.getDatasource().remove(0);
        registro.getCampos().put(cTipoT, registro.getCampos().get(cTipoT));
        String respuesta;
        String tipoT = registro.getCampos().get(cTipoT) == null ? " "
            : registro.getCampos().get(cTipoT).toString();
        String tipoModelo = service.buscarEnLista(tipoT, cTipoT, cTipoModelo,
                        listatipoT.getDatasource()) == null ? ""
                            : service.buscarEnLista(tipoT, cTipoT,
                                            cTipoModelo,
                                            listatipoT.getDatasource());

        respuesta = tipoModelo;
        return respuesta;
    }

    /**
     * Se llama en el metodo oprimirImprimir
     *
     * @param rsExpedida
     * @param sql
     * @return una cadena
     */
    private String cedulaInterventor(Registro rsExpedida) {
        String respuesta;
        respuesta = ((rsExpedida == null)
            || (rsExpedida.getCampos().get(cExpedidaCedula) == null)) ? ""
                : rsExpedida.getCampos().get(cExpedidaCedula).toString();

        return respuesta;
    }

    /**
     * Se llama en el metodo oprimirImprimir
     *
     * @param rsExpedidaT
     * @param sql
     * @return una cadena
     */
    private String cedulaSupervisor(Registro rsExpedidaT) {
        String respuesta;
        respuesta = rsExpedidaT == null
            || rsExpedidaT.getCampos().get(cExpedidaCedula) == null ? ""
                : rsExpedidaT.getCampos().get(cExpedidaCedula).toString();

        return respuesta;
    }

    public void cambiarformato() {

        Date conversion = (Date) service.buscarEnListaObj(modelo,
                        "CODIGO", cFecha, listaformato);

        fechaFormato = SysmanFunciones.formatearFecha(conversion);

    }

    public void generarReporte(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(cClaseOrden, claseOrden);
            reemplazar.put("claseNovedad", claseNov);
            reemplazar.put(nNumero, numero);
            reemplazar.put("compania", compania);
            // anterior linea se debe quitar
            // para enviar reporte a jose
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000357NovedadesPorContrato";
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_FORMS_NOVEDADCONTRATO_CLASEORDEN", claseOrden);
            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_FORMS_NOVEDADCONTRATO_NUMERO", numero);
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_INFORME_NO_EXISTE),
                            " ", ex.getMessage()));
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            ex.getMessage()));
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void oprimirCmdMetasProducto() {
        try {

            parametrosEntrada.put(cClaseOrden, tipoContrato);
            parametrosEntrada.put(nNumero, numero);
            parametrosEntrada.put(cNovedad.toLowerCase(),
                            registro.getCampos().get(cNovedad));
            parametrosEntrada.put(cTipoT.toLowerCase(),
                            registro.getCampos().get(cTipoT));
            parametrosEntrada.put(cClaseT.toLowerCase(),
                            registro.getCampos().get(cClaseT));
            parametrosEntrada.put("fechan",
                            SysmanFunciones.convertirAFechaCadena(
                                            (Date) registro.getCampos()
                                                            .get(cFecha)));
            parametrosEntrada.put("pejecucion", registro.getCampos()
                            .get(cPorcentajeEjecucion));

            Direccionador direccionador = new Direccionador();

            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.SUBBPPLANINDEJECUTADOCONTRATOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametrosEntrada);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        catch (ParseException ex) {
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimirBtnManejaSeguridadSocial() {

        try {

            parametrosEntrada.put(cClaseOrden, tipoContrato);
            parametrosEntrada.put(nNumero, numero);
            parametrosEntrada.put(cNovedad.toLowerCase(),
                            registro.getCampos().get(cNovedad));
            parametrosEntrada.put(cTipoT.toLowerCase(),
                            registro.getCampos().get(cTipoT));
            parametrosEntrada.put(cClaseT.toLowerCase(),
                            registro.getCampos().get(cClaseT));
            parametrosEntrada.put("fechan",
                            SysmanFunciones.convertirAFechaCadena(
                                            (Date) registro.getCampos()
                                                            .get(cFecha)));
            parametrosEntrada.put("pejecucion", registro.getCampos()
                            .get(cPorcentajeEjecucion));

            Direccionador direccionador = new Direccionador();

            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.FMR_SEGURIDAD_SOC_NOVS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametrosEntrada);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        catch (ParseException ex) {
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimirvisitain() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirRegistrarPagos() {
        // <CODIGO_DESARROLLADO>
        parametrosPagos.put("rid", css);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.REGISTROPAGOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosPagos);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirResultados(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPasarNovedadNomina() {

        agregarRegistroNuevo(false);

        numeroMensaje = 0;

        Map<String, Object> param = new TreeMap<>();
        param.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                        claseOrden);
        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubnovedadcontratosControladorUrlEnum.URL24075
                                                                            .getValue())
                                            .getUrl(), param));

            boolean novedadNomina = ((rs == null)
                || (rs.getCampos().get("NOVEDADNOMINA") == null)) ? false
                    : (boolean) rs.getCampos().get("NOVEDADNOMINA");
            if (novedadNomina) {
                if (!(Boolean) registro.getCampos().get(cIndicadorAdicion)) {

                    numeroMensaje = 1;
                    etiquetaMensaje = idioma.getString("TB_TB2769");
                    actualiza = true;
                }
                else {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2154"));
                }

            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2154"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnCumplimientoActiv en la
     * vista
     *
     */
    public void oprimirBtnCumplimientoActiv() {
        // <CODIGO_DESARROLLADO>

        try {
            ejbContratosDosRemote.actualizarCumplimientoActividades(compania,
                            Long.parseLong(numero), tipoContrato,
                            Long.parseLong(registro.getCampos().get(cNovedad)
                                            .toString()),
                            registro.getCampos().get(cTipoT).toString(),
                            Long.parseLong(registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName())
                                            .toString()),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        parametrosEntrada.put(cNovedad.toLowerCase(),
                        registro.getCampos().get(cNovedad));

        parametrosEntrada.put("codActa", registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));

        parametrosEntrada.put(cTipoT.toLowerCase(),
                        registro.getCampos().get(cTipoT));

        parametrosEntrada.put(nNumero, numero);

        parametrosEntrada.put(cClaseOrden, tipoContrato);

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(1937));

        direccionador.setParametros(parametrosEntrada);

        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarimpreso() {

        if (!(boolean) registro.getCampos().get(cImpreso)) {

            bloqueadoTipoT = false;
            bloqueadoClaseT = false;
            bloqueadoNovedad = false;
            bloqueadoAno = false;
            bloqueadoNumero = false;
            bloqueadoFecha = false;
            bloqueadoFechaInicial = false;
            bloqueadoValorLiberado = false;
            bloqueadoTexto602 = false;
            bloqueadoTValorTotal = false;
            bloqueadoPejecucion = false;
            bloqueadoTipoRetencion = false;
            bloqueadoObservaciones = false;
            bloqueadoTexto66 = false;
            bloqueadoDiasContrato = false;
            bloqueadoFechaFinal = false;
            bloqueadoValorTotal = false;
            bloqueadoTxtaportest = false;
            bloqueadoTxtVlrAqVehiculo = false;
            bloqueadoTxtResultados = false;
        }
        else {
            bloqueadoTipoT = true;
            bloqueadoClaseT = true;
            bloqueadoNovedad = true;
            bloqueadoAno = true;
            bloqueadoNumero = true;
            bloqueadoFecha = true;
            bloqueadoFechaInicial = true;
            bloqueadoValorLiberado = true;
            bloqueadoTexto602 = true;
            bloqueadoTValorTotal = true;
            bloqueadoPejecucion = true;
            bloqueadoTipoRetencion = true;
            bloqueadoObservaciones = true;
            bloqueadoTexto66 = true;
            bloqueadoDiasContrato = true;
            bloqueadoFechaFinal = true;
            bloqueadoValorTotal = true; 
            bloqueadoTxtaportest = true;
            bloqueadoTxtVlrAqVehiculo = true;
            bloqueadoTxtResultados = true;

        }

    }

    public void cambiarClaseT() {
        // <CODIGO_DESARROLLADO>
        cargarListatipoT();
        registro.getCampos().remove(cTipoT);
        txtNovedad = "";

        // </CODIGO_DESARROLLADO>
    }

    public boolean cambiarFechaInicial() {
        if (registro.getCampos().get(cFechaInicial) == null) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2155"));
            return false;
        }

        String strTipoT = SysmanFunciones.nvlStr(
                        registro.getCampos().get(cTipoT).toString(), "");

        // ACI
        if (!tipoAciCambiarFechaInicial(strTipoT)) {
            return false;
        }

        if (fechaFirma == null) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2157"));
            return false;
        }
        else {

            if (!validacionFechas()) {
                return false;
            }
        }

        return true;

    }

    /**
     * Se llama en el metodo cambiarFechaInicial
     *
     * @param strTipoT
     * @return verdadero o falso
     */
    public boolean tipoAciCambiarFechaInicial(String strTipoT) {
        boolean respuesta = true;
        if (fechaPolizas != null) {
            Date fecIni = (Date) registro.getCampos()
                            .get(cFechaInicial);
            Date fecPol = fechaPolizas;

            if ("ACI".equals(strTipoT) && (fecPol.after(fecIni))) {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2156"));
                respuesta = false;

            }
        }

        return respuesta;
    }

    /**
     * Se llama en el metodo cambiarFechaInicial
     *
     * @return verdadero o falso
     */
    public boolean validacionFechas() {
        boolean respuesta = true;

        Date fecIni = (Date) registro.getCampos().get(cFechaInicial);
        if (fecIni.before(fechaFirma)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2158"));
            return false;

        }
        if (!"0".equals(SysmanFunciones.nvlStr(plazoEntrega, "0")) && (fecIni
                        .before(fecIni)
            || fecIni.after(SysmanFunciones.sumarRestarDiasFecha(fecIni,
                            Integer.parseInt(
                                            SysmanFunciones.nvlStr(plazoEntrega,
                                                            "0")))))) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2159"));
            respuesta = false;
        }
        return respuesta;

    }

    public void cambiarDiasContrato() {

        int intDiasMaximoAdicion = 0;
        double intPeriodoContrato;
        boolean diferirFechas = false;

        try {

            diferirFechas = valorParametroIgnoreCase(
                            "DIFERIR FECHAS CONTANDO MESES DE 31 Y 28 DIAS");

            String diasContrato = registro.getCampos()
                            .get(cDiasContrato) == null ? ""
                                : registro.getCampos()
                                                .get(cDiasContrato).toString();
            if (diferirFechas) {
                funcionFechaFinal(diasContrato);
            }
            else {
                funcionFechaFinalUno(diasContrato);
            }
            intPeriodoContrato = 0;
            if ((fechaFinalizacion != null) && (txtFecha != null)) {
                intPeriodoContrato = SysmanFunciones.calcularDiferenciaDias(
                                fechaFinalizacion,
                                SysmanFunciones.convertirAFecha(txtFecha));
            }
            if (intPeriodoContrato <= 0) {
                intPeriodoContrato = intPeriodoContrato * -1;
                intDiasMaximoAdicion = (int) SysmanFunciones
                                .redondear((intPeriodoContrato * 60) / 100, 2);
            }
            else {
                intDiasMaximoAdicion = (int) SysmanFunciones
                                .redondear((intPeriodoContrato * 60) / 100, 2);

            }

            if (Integer.parseInt((String) registro.getCampos()
                            .get(cDiasContrato)) > intDiasMaximoAdicion) {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2160"));
            }

        }
        catch (ParseException ex) {
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Metodo ejecutado al cambiar el control PorcAmortizado
     * 
     */
    public void cambiarPorcAmortizado() {
        if (validarPorcentajeAmortizado()) {

            double valorTotal = Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos().get("VALORTOTAL"), "0")
                            .toString());

            double valorLiberado = Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos().get("VALOR_LIBERADO"),
                                            "0")
                            .toString());

            double porcentajeAmortizacion = Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos().get(
                                            SubnovedadcontratosControladorEnum.PORCAMORTIZADO
                                                            .getValue()),
                                            "0")
                            .toString());

            double valorAmortizado = valorTotal
                * (porcentajeAmortizacion / 100);

            double valorAmortizadoLiberado = valorLiberado
                * (porcentajeAmortizacion / 100);

            double valorAmortizadoActa = valorAmortizado
                + valorAmortizadoLiberado;

            double valorTotalAPagar = valorTotal - valorAmortizadoActa;

            registro.getCampos().put("VLRAMORTIZADO", valorAmortizado);

            registro.getCampos().put(
                            SubnovedadcontratosControladorEnum.VLRAMORTIZADOLIBERADO
                                            .getValue(),
                            valorAmortizadoLiberado);

            registro.getCampos().put(
                            SubnovedadcontratosControladorEnum.VLRAMORTIZADOACTA
                                            .getValue(),
                            valorAmortizadoActa);

            registro.getCampos()
                            .put(SubnovedadcontratosControladorEnum.VALORAPAGAR
                                            .getValue(), valorTotalAPagar);
        }
    }

    private boolean validarPorcentajeAmortizado() {

        double porcentaje = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get(
                                        SubnovedadcontratosControladorEnum.PORCAMORTIZADO
                                                        .getValue()),
                                        "0")
                        .toString());

        if (porcentaje > 100) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4333"));
            registro.getCampos().put(
                            SubnovedadcontratosControladorEnum.PORCAMORTIZADO
                                            .getValue(),
                            "0");

            return false;
        }

        return true;
    }

    /**
     * Se llama en el metodo cambiarDiasContrato
     *
     * @param diasContrato
     */
    private void funcionFechaFinal(String diasContrato) {
        try {
            if ((registro.getCampos().get(cDiasContrato) != null)
                && (registro.getCampos().get(cFechaInicial) != null)) {

                registro.getCampos().put(cFechaFinal, ejbSysmanUtilRemote
                                .fechaFinalMasDiasComerciales(
                                                (Date) (registro.getCampos()
                                                                .get(cFechaInicial)),
                                                Integer.parseInt(diasContrato),
                                                false));
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Se llama en el metodo cambiarDiasContrato
     *
     * @param diasContrato
     */
    private void funcionFechaFinalUno(String diasContrato) {

        int dias;
        String diascontrato = SysmanFunciones.validarVariableVacio(diasContrato)
            ? "0"
            : diasContrato;

        try {

            if ((registro.getCampos().get(cDiasContrato) != null)
                && (registro.getCampos().get(cFechaInicial) != null)) {
                dias = Integer.parseInt(diascontrato);
                Date fechaini = (Date) registro
                                .getCampos().get(cFechaInicial);
                Date fechae = ejbSysmanUtilRemote
                                .fechaFinalMasDiasComerciales(
                                                fechaini,
                                                dias,
                                                false);
                registro.getCampos().put(cFechaFinal, fechae);

            }

        }
        catch (NumberFormatException | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarValorTotal() {
        /*Double dblAdicionMaxima = (valorFinal * 50) / 100;
        Double valorTotal = registro.getCampos().get(cValorTotal) == null ? 0.0
            : Double.parseDouble(
                            (String) registro.getCampos().get(cValorTotal));

        if (valorTotal > dblAdicionMaxima) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2160"));
        }*/    	
        try 
        {
        	Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(), claseOrden);
            param.put(SubnovedadcontratosControladorEnum.PARAM5.getValue(), numero);
        	
        	Registro rsVal = RegistroConverter.toRegistro(requestManager.get(
				                	 UrlServiceUtil.getInstance()
				                                	 .getUrlServiceByUrlByEnumID(
				                                				 	 SubnovedadcontratosControladorUrlEnum.URL002
				                                                                	 .getValue())
				                                	 .getUrl(),
				                     param));
        
        	Double valorActasC = ((rsVal.getCampos().get("VLRTOTALACTAS") == null ? 0.0
        						 	: Double.parseDouble(rsVal.getCampos().get("VLRTOTALACTAS").toString())) + 
        						   Double.parseDouble(registro.getCampos().get(cValorTotal).toString()));
        	
        	if(valorActasC > valorTotalCont
        		&& registro.getCampos().get(cClaseT).equals("N")
        		&& registro.getCampos().get("CLASENOVEDAD").equals("A"))
        	{
        		registro.getCampos().put(cValorTotal, "0.0");
        		
        		String mensaje = idioma.getString("TB_TB4419");
        		mensaje = mensaje.replace("s$valorTotal$s", asignarFormato(valorTotalCont.toString()));
        		
        		JsfUtil.agregarMensajeAlertaDialogo(mensaje);
        	}
		} 
        catch (SystemException e) 
        {
        	e.printStackTrace();
		}
    }
    
    private String asignarFormato(String campo) {

        DecimalFormatSymbols valoresDecimales = new DecimalFormatSymbols(
                        Locale.US);
        return new DecimalFormat("#,##0.00", valoresDecimales)
                        .format(Double.parseDouble(campo));

    }

    public void cambiarAfectaItems() {
        Registro rs;
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                            claseOrden);
            param.put(SubnovedadcontratosControladorEnum.PARAM5.getValue(),
                            numero);
            param.put(SubnovedadcontratosControladorEnum.PARAM6.getValue(),
                            registro.getCampos().get(cNovedad));

            rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubnovedadcontratosControladorUrlEnum.URL24089
                                                                            .getValue())
                                            .getUrl(),
                            param));

            if ("0".equals(rs.getCampos().get(cantidadReg).toString())) {
                param.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                param.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubnovedadcontratosControladorUrlEnum.URL24074
                                                                .getValue());

                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                param);
            }

            String[] camposVector = { cClaseOrden, cOrdenCompra,
                                      cNovedad.toLowerCase() };
            String[] valoresVector = { claseOrden, numero, registro
                            .getCampos().get(cNovedad).toString() };
            SessionUtil.cargarModalDatosFlash(
                            Integer.toString(
                                            GeneralCodigoFormaEnum.DNOVEDADCONTRATOS_CONTROLADOR
                                                            .getCodigo()),
                            SessionUtil.getModulo(),
                            camposVector, valoresVector);

            afectaItems = false;
        }
        catch (SystemException ex) {
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void seleccionarFilaOrdenador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cOrdenador,
                        registroAux.getCampos().get(cCedula));
        registro.getCampos().put(sucursal,
                        registroAux.getCampos().get(sucursal));
        nombreOrdenador = (String) registroAux.getCampos().get(cNombre);
    }

    public void onRowSelectTexto602(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cOrdenador,
                        registroAux.getCampos().get(cCedula));
    }

    public void seleccionarFilatipoT(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cTipoT, registroAux.getCampos().get(cTipoT));
        registro.getCampos().put("CLASENOVEDAD", registroAux.getCampos().get("CLASENOVEDAD"));
        String tipoT = registro.getCampos().get(cTipoT) == null ? ""
            : (String) registro.getCampos().get(cTipoT);
        txtNovedad = (String) registroAux.getCampos().get(cNombre);

        visibleEval = (boolean) registroAux.getCampos()
                        .get(SubnovedadcontratosControladorEnum.APLICA_CALIFICACION
                                        .getValue());

        indPago = (boolean) registroAux.getCampos()
                        .get(SubnovedadcontratosControladorEnum.INDPAGO
                                        .getValue());

        visibleValorLiberado = "ACL".equals(tipoT);
        StringBuilder criterio = new StringBuilder(" ");
        criterio.append("COMPANIA = ''");
        criterio.append(compania);
        criterio.append("'' AND CLASEORDEN = ''");
        criterio.append(claseOrden);
        criterio.append("'' AND ORDENDECOMPRA = ");
        criterio.append(numero);
        try {
            registro.getCampos()
                            .put(cNovedad, ejbSysmanUtilRemote
                                            .generarSiguienteConsecutivo(
                                                            "NOVEDADCONTRATO",
                                                            criterio.toString(),
                                                            "NOVEDAD"));

            calificaContratistaEnActaDeTerminacion = valorParametro(
                            "CALIFICA CONTRATISTA EN ACTA DE TERMINACION");

            visibleCalificacionContr = calificaContratistaEnActaDeTerminacion
                && "ACT".equals(tipoT);

            manejaNominaDeContratistas = valorParametro(
                            "MANEJA NOMINA DE CONTRATISTAS");

            visibleTipoRetencion = manejaNominaDeContratistas
                && "ACI".equals(tipoT);

            String tAutomatica = SysmanFunciones.nvlStr(registroAux.getCampos()
                            .get(cTautomatica).toString(), "");

            if (!"M".equals(tAutomatica)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2161"));
                visibleValorLiberado = false;
                return;
            }
            boolean bAfectaItems = registroAux.getCampos()
                            .get(cAfectaItems) == null ? false
                                : (boolean) registroAux.getCampos()
                                                .get(cAfectaItems);
            bAfectaItems(bAfectaItems);

            // ARI
            tipoAri(tipoT);
            // ACL
            tipoAcl(tipoT);
            
            tipoAcf(tipoT);

            generaAutomaticamenteConsecutivoDeNovedades = valorParametroIgnoreCase(
                            "GENERA AUTOMATICAMENTE CONSECUTIVO DE NOVEDADES");
            StringBuilder criterios = new StringBuilder();
            criterios.append("COMPANIA = ''");
            criterios.append(compania);
            criterios.append("'' AND CLASEORDEN = ''");
            criterios.append(claseOrden);
            criterios.append("'' AND ORDENDECOMPRA = ");
            criterios.append(numero);
            criterios.append(" AND TIPOT = ''");
            criterios.append(tipoT);
            criterios.append("''");
            if (generaAutomaticamenteConsecutivoDeNovedades) {

                registro.getCampos().put(cNumero,
                                ejbSysmanUtilRemote.generarSiguienteConsecutivo(
                                                "NOVEDADCONTRATO",
                                                criterios.toString(),
                                                "NUMERO"));

                bloqueadoNumero = true;
            }
            else {
                bloqueadoNumero = false;
            }
            
            bloqueadoValorTotal = false;

        }
        catch (SystemException ex) {
            Logger.getLogger(SubnovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Se llama en el metodo seleccionarFilatipoT
     *
     * @param afecta
     */
    private void bAfectaItems(boolean afecta) {
        if (afecta) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2163"));
            enabledAfectaItems = true;
            bloqueadoValorTotal = false;
        }
        else {
            enabledAfectaItems = false;
            bloqueadoValorTotal = true;
        }
    }
    
  

    /**
     * Se llama en el metodo seleccionarFilatipoT
     *
     * @param tipoT
     */
    private void tipoAri(String tipoT) {

        if ("ARI".equals(tipoT)) {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                            claseOrden);
            params.put(GeneralParameterEnum.NUMERO.getName(),
                            numero);
            params.put(SubnovedadcontratosControladorEnum.PARAM4.getValue(),
                            anio);

            Registro rs;
            try {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubnovedadcontratosControladorUrlEnum.URL24073
                                                                                .getValue())
                                                .getUrl(), params));

                if ((rs != null)
                    && (rs.getCampos().get(cFechaFinal) != null)) {

                    registro.getCampos().put(cFechaInicial,
                                    rs.getCampos().get(cFechaFinal));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    /**
     * Se llama en el metodo seleccionarFilatipoT
     *
     * @param tipoT
     */
    private void tipoAcl(String tipoT) {

        if ("ARI".equals(tipoT)) {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(SubnovedadcontratosControladorEnum.PARAM0.getValue(),
                            claseOrden);
            params.put(GeneralParameterEnum.NUMERO.getName(),
                            numero);
            params.put(SubnovedadcontratosControladorEnum.PARAM4.getValue(),
                            anio);
            Registro rs;
            try {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubnovedadcontratosControladorUrlEnum.URL24072
                                                                                .getValue())
                                                .getUrl(), params));
                if (rs != null) {
                    registro.getCampos().put("VALOR_LIBERADO",
                                    rs.getCampos().get(cValorTotal));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }
    
    /**
     * Se llama en el metodo habilitaCampoValorTotal
     *
     * @param tipoT
     */
    
    private void tipoAcf(String tipoT) {
    	if (!"ACF".equals(tipoT) && habilitaCampoValorTotal == false) {
               bloqueadoValorTotal = true;
        }
            else if ("ACF".equals(tipoT) && habilitaCampoValorTotal == true) {
                bloqueadoValorTotal = false;
            }
           else if ("ACF".equals(tipoT) && habilitaCampoValorTotal == false) {
                bloqueadoValorTotal = true;
            }
    	}

        
    @Override
    public void abrirFormulario() {
        initVariables();
        numeroRetorno = numero;
        clasenovAYgrupoDif9 = false;
        // oksdwq
        if (manejaControlDeActas && "A".equals(claseNov)
            && (SessionUtil.getNivelGrupo(modulo) != 9)) {
            clasenovAYgrupoDif9 = true;
        }
    }

    @Override
    public void cargarRegistro() {

        Map<String, Object> param = new TreeMap<>();
        param.put("CLASET", claseNov);
        param.put(cTipoT, tipoContrato);

        if (accion.equals(ACCION_MODIFICAR)) {
            indPago = (boolean) registro.getCampos()
                            .get(SubnovedadcontratosControladorEnum.INDPAGO
                                            .getValue());
            visibleEval = (boolean) registro.getCampos()
                            .get(SubnovedadcontratosControladorEnum.APLICA_CALIFICACION
                                            .getValue());
        }
        else {
            txtNovedad = null;
            visibleEval = false;
        }

        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubnovedadcontratosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null) {

                if ((boolean) rs.getCampos().get("AFECTA_ACTIVIDADES")
                    && !accion.equals(ACCION_INSERTAR)) {
                    visibleCumplimientoActiv = true;
                }
                else {
                    visibleCumplimientoActiv = false;
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registroinicial = new Registro(new HashMap<String, Object>());
        registroinicial.setCampos(
                        new HashMap<>(registro.getCampos()));

        precargarRegistro();

        cargarListaformato();
        despuesCargarRegsitro();

    }

    public void camposVisibles() {

        if (css != null) {
            String tipoT = (String) registro.getCampos().get(cTipoT);

            visibleAmortizar = (boolean) registro.getCampos()
                            .get("AMORTIZAANT");

            visibleCalificacionContr = calificaContratistaEnActaDeTerminacion
                && "ACT".equals(tipoT);
            visibleTipoRetencion = manejaNominaDeContratistas
                && "ACI".equals(tipoT);
            visibleAlquiler = manejaNominaDeContratistas && "ADI".equals(tipoT);
            visibleValorLiberado = "ACL".equals(tipoT);
            bloqueadoTipoT = true;
            bloqueadoClaseT = true;         
            visibleAdjuntos = true;         
            cargarListatipoT();
            listatipoT.load();
            listatipoT.getDatasource().remove(0);
            enabledAfectaItems = service.buscarEnLista(tipoT, cTipoT,
                            cAfectaItems,
                            listatipoT.getDatasource()) == null ? false
                                : service.buscarEnLista(tipoT, cTipoT,
                                                cAfectaItems,
                                                listatipoT.getDatasource())
                                                .equals(cTrue);

            Map<String, Object> param = new TreeMap<>();
            param.put(SubnovedadcontratosControladorEnum.PARAM7.getValue(),
                            claseNov);
            param.put(SubnovedadcontratosControladorEnum.PARAM8.getValue(),
                            registro.getCampos().get(cClaseT));
            param.put(cTipoT, tipoT);

            Registro rsNovedad = null;

            try {
                rsNovedad = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubnovedadcontratosControladorUrlEnum.URL24078
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            if (rsNovedad != null) {

                txtNovedad = rsNovedad.getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString();
            }

            String tipoModelo = service.buscarEnLista(tipoT, cTipoT,
                            cTipoModelo, listatipoT.getDatasource()) == null
                                ? ""
                                : service.buscarEnLista(tipoT, cTipoT,
                                                cTipoModelo,
                                                listatipoT.getDatasource());

            visibleFormato = !"R".equals(tipoModelo);

            if (manejaIndicadorDeImpresoEnActas) {
                if (!(boolean) registro.getCampos().get(cImpreso)) {
                    manejaIndicadorEImpreso = false;
                    bloqueadoNumero = false;
                    bloqueadoTipoT = false;
                    bloqueadoClaseT = false;
                    bloqueadoNovedad = false;
                    bloqueadoAno = false;
                    bloqueadoFecha = false;
                    bloqueadoFechaInicial = false;
                    bloqueadoValorLiberado = false;
                    bloqueadoTexto602 = false;
                    bloqueadoTValorTotal = false;
                    bloqueadoPejecucion = false;
                    bloqueadoTipoRetencion = false;
                    bloqueadoObservaciones = false;
                    bloqueadoTexto66 = false;
                    bloqueadoDiasContrato = false;
                    bloqueadoFechaFinal = false;
                    bloqueadoValorTotal = !enabledAfectaItems;
                    bloqueadoTxtaportest = false;
                    bloqueadoTxtVlrAqVehiculo = false;
                    bloqueadoTxtResultados = false;
                }
                else {
                    manejaIndicadorEImpreso = true;
                    bloqueadoNumero = true;
                    bloqueadoTipoT = true;
                    bloqueadoClaseT = true;
                    bloqueadoNovedad = true;
                    bloqueadoAno = true;
                    bloqueadoFecha = true;
                    bloqueadoFechaInicial = true;
                    bloqueadoValorLiberado = true;
                    bloqueadoTexto602 = true;
                    bloqueadoTValorTotal = true;
                    bloqueadoPejecucion = true;
                    bloqueadoTipoRetencion = true;
                    bloqueadoObservaciones = true;
                    bloqueadoTexto66 = true;
                    bloqueadoDiasContrato = true;
                    bloqueadoFechaFinal = true;
                    bloqueadoValorTotal = true;
                    bloqueadoTxtaportest = true;
                    bloqueadoTxtVlrAqVehiculo = true;
                    bloqueadoTxtResultados = true;

                }
            }


            nivelGrupo = SessionUtil.getNivelGrupo(modulo);
            //bloqueadoValorTotal = false; 
                    }
        
        
        else {
            manejaControlActas();
            bloqueadoTipoT = false;
            bloqueadoClaseT = false;
            bloqueadoValorTotal = false;
            visibleAdjuntos = false; 
            registro.getCampos().put(cClaseT, "N");
            registro.getCampos().put("ANO", SysmanFunciones.ano(new Date()));
            registro.getCampos().put(cPorcentajeEjecucion, "0");
            registro.getCampos().put(cFecha, new Date());
            registro.getCampos().put(cImpreso, false);
            registro.getCampos().put("APORTESTE", "0");

            cargarListatipoT();
        }

    }

    public void despuesCargarRegsitro() {
        newRecord = css == null;
        visibleTipoRetencion = false;
        visibleAlquiler = false;
        visibleValorLiberado = false;
        visibleVobo = false;
        visibleFormato = false;
        camposVisibles();
        cargarListatipoT();
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(SubnovedadcontratosControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get(cOrdenador));

        Registro ordenador;
        try {
            ordenador = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubnovedadcontratosControladorUrlEnum.URL24071
                                                                            .getValue())
                                            .getUrl(), params));

            nombreOrdenador = ordenador == null ? ""
                : (String) ordenador.getCampos().get(cNombre);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Se llama en el metodo despuesCargarRegsitro
     */
      
    private void manejaControlActas() {
        if (manejaControlDeActas) {

            if ("A".equals(claseNov)
                && (SessionUtil.getNivelGrupo(modulo) == 9)) {
                visibleVobo = true;
                bloqueadoVobo = false;
            }
            else if ("A".equals(claseNov)
                && (SessionUtil.getNivelGrupo(modulo) != 9)) {
                visibleVobo = true;
                bloqueadoVobo = true;
            }

        }
    }
    
    /**
     * Método que realiza validaciones previas a la inserción de una subnovedad de contrato. 
     * 
     * @return true si pasa las validaciones y puede continuar con el proceso; false si debe detenerse.
     */
    public boolean insertarAntes() {
        try {
            // Paso 1: Consultar si ya existe un ACI (Acuerdo de Contratación Inicial)
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania); 
            param.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(), claseOrden);
            param.put(SubnovedadcontratosControladorEnum.PARAM5.getValue(), numero); // Numero de orden de compra

            // Realiza la solicitud al servicio y convierte el resultado en un objeto Registro
            Registro resultado = RegistroConverter.toRegistro(
                requestManager.get(
                    UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(SubnovedadcontratosControladorUrlEnum.URL127014.getValue())
                        .getUrl(),
                    param)
            );

            // Verifica si ya existe un ACI con los datos consultados
            boolean existeACI = resultado != null && resultado.getCampos().get("EXISTE_ACI") != null;

            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania); 
            registro.getCampos().put(cClaseOrden.toUpperCase(), claseOrden); 
            registro.getCampos().put(cOrdenCompra.toUpperCase(), numero); 

            // Elimina campos no necesarios o que deben limpiarse antes de continuar
            registro.getCampos().remove(SubnovedadcontratosControladorEnum.VLRAMORTIZADOLIBERADO.getValue());
            registro.getCampos().remove(SubnovedadcontratosControladorEnum.VLRAMORTIZADOACTA.getValue());
            registro.getCampos().remove(SubnovedadcontratosControladorEnum.VALORAPAGAR.getValue());
            registro.getCampos().remove("MANEJASEGURIDADSOC");

            // Validar tipo de orden de acta  solo si NO existe un tipo de acta ACI
            if (!existeACI) {
                String tipot = (String) registro.getCampos().get(cTipoT);   
             // Si el tipo no es 'ACI' y la opción 'aplica actas inicio' está activada, mostrar mensaje y detener el proceso
                if (!"ACI".equals(tipot) && aplicasActasIni) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString(cMensaje));
                    return false;
                }
            }

            // Si todo va bien, mostrar mensaje de éxito
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
            return true;

        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        actualizarDespues();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void validaMes(String tipot) {

        if ("ACI".equals(tipot)) {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(SubnovedadcontratosControladorEnum.PARAM3
                            .getValue(),
                            claseOrden);
            param.put(SubnovedadcontratosControladorEnum.PARAM1
                            .getValue(),
                            numero);

            Registro rs;

            try {
                rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubnovedadcontratosControladorUrlEnum.URL24070
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                int mes = ((rs == null)
                    || (rs.getCampos().get("MES") == null)) ? 0
                        : Integer.parseInt(
                                        (String) rs.getCampos().get("MES"));
                int mesActual = SysmanFunciones
                                .getParteFechaMes(new Date());
                if (mes == mesActual) {
                    cantidad();
                }
                else {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB2165"));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        else {
            ejecutarNovedadNomina();
        }
    }

    public void aceptarConfirmacionActualiza() {
        // <CODIGO_DESARROLLADO>
        actualiza = false;

        if (dependencia.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2166"));
            return;
        }
        if (numeroMensaje == 1) {
            String tipot = registro.getCampos().get(cTipoT) == null ? ""
                : (String) registro.getCampos().get(cTipoT);
            validaMes(tipot);

        }
        else if (numeroMensaje == 2) {
            ejecutarNovedadNomina();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Se llama en el metodo aceptarConfirmacionActualiza
     */
    public void cantidad() {
        Registro rs;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                        claseOrden);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);

        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubnovedadcontratosControladorUrlEnum.URL24069
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            int cantidad = ((rs == null)
                || (rs.getCampos().get(cantidadReg) == null)) ? 0
                    : Integer.parseInt((String) rs.getCampos()
                                    .get(cantidadReg));
            if (cantidad == 0) {

                etiquetaMensaje = idioma.getString("TB_TB3493");
                actualiza = true;
                numeroMensaje = 2;
            }
            else {
                ejecutarNovedadNomina();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void validaNovedadNomina() {

        String resultado;

        try {
            resultado = ejbContratosUnoLocal.enviarNovedadesaNomina(
                            compania,
                            SysmanFunciones.nvlStr(
                                            registro.getCampos().get(cNovedad)
                                                            .toString(),
                                            ""),
                            numero,
                            new BigDecimal(valorTotalNovedad),
                            SessionUtil.getUser().getCodigo());

            resultadoNovedadNomina(resultado);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void resultadoNovedadNomina(String resultado) {
        if ("1".equals(resultado)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3481"));
        }

        if ((Integer.parseInt(resultado) >= 3)
            && (Integer.parseInt(resultado) <= 8)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
    }

    public void ejecutarNovedadNomina() {

        try {

            validaNovedadNomina();

        }
        catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public boolean actualizarAntes() {
        if (!cambiarFechaInicial()) {
            return false;
        }
        String strTipoT = SysmanFunciones.nvlStr(
                        registro.getCampos().get(cTipoT).toString(), "");
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(
                            SubnovedadcontratosControladorEnum.PARAM3
                                            .getValue());
            registro.getCampos().remove(
                            SubnovedadcontratosControladorEnum.PARAM5
                                            .getValue());

            registro.getCampos().remove(
                            SubnovedadcontratosControladorEnum.PARAM6
                                            .getValue());
            registro.getCampos().remove(
                            SubnovedadcontratosControladorEnum.APLICA_CALIFICACION
                                            .getValue());
            registro.getCampos()
                            .remove(SubnovedadcontratosControladorEnum.INDPAGO
                                            .getValue());

            registro.getCampos().remove(
                            SubnovedadcontratosControladorEnum.VLRAMORTIZADOLIBERADO
                                            .getValue());

            registro.getCampos().remove(
                            SubnovedadcontratosControladorEnum.VLRAMORTIZADOACTA
                                            .getValue());

            registro.getCampos().remove(
                            SubnovedadcontratosControladorEnum.VALORAPAGAR
                                            .getValue());

            registro.getCampos().remove("BLOQUEAVLRPAG");

            registro.getCampos().remove("AMORTIZAANT");

        }

        // if (service.buscarEnLista(
        // (String) registro.getCampos().get(cTipoT), cTipoT,
        // cAfectaItems,
        // listatipoT.getDatasource()).equals(cFalse)) {
        // registro.getCampos().put(cClaseT, "N");
        // }

        registro.getCampos().remove("CLASENOVEDAD");
        registro.getCampos().remove("TIPOFORMATO");
        registro.getCampos().remove("TIPOMODELO");
        registro.getCampos().remove("MANEJASEGURIDADSOC");

        registro.getCampos().put(sucursal,
                        registro.getCampos().get(sucursal));
        // ACI
        if (!tipoAciActualizaAntes(strTipoT)) {
            return false;
        }
        // ACS , ARI , ACL , ADI,tAutomatica , nvlObservaciones , echa
        // , fechaInicial
        if (!tiposAcualizaAntes(strTipoT)
            || !validacionActualizaAntes(strTipoT)
            || !camposActualizaAntes(strTipoT)) {
            return false;
        }

        return true;
    }

    public boolean validVlrTotal() {
        boolean respuesta = true;
        Registro rs;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                        claseOrden);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);

        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubnovedadcontratosControladorUrlEnum.URL24088
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            String nvlValorTotal = ((rs == null) || ((rs
                            .getCampos().get(cValorTotal).toString()) == null))
                                ? "0"
                                : rs.getCampos()
                                                .get(cValorTotal).toString();
            double totalValor = Double.parseDouble(nvlValorTotal);

            if (!nvlValorTotal.equals(SysmanFunciones
                            .nvl(registro.getCampos().get(cValorTotal), 0))) {
                DecimalFormat num = new DecimalFormat("#,###.00");
                JsfUtil.agregarMensajeAlerta(idioma
                                .getString("TB_TB2167")
                                .replace("#$valorTotal#$", num.format(
                                                Double.valueOf(SysmanFunciones
                                                                .nvl(registro.getCampos()
                                                                                .get(cValorTotal),
                                                                                0)
                                                                .toString())))
                                .replace("s$totalValor$s",
                                                num.format(totalValor)));

                respuesta = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;
    }

    /**
     * Se llama en el metodo actualizaAntes
     *
     * @param strTipoT
     * @return
     */
    public boolean tipoAciActualizaAntes(String strTipoT) {
        boolean controlarValorEnActaInicio;

        boolean respuesta = true;
        if ("ACI".equals(strTipoT)) {

            controlarValorEnActaInicio = valorParametroIgnoreCase(
                            "CONTROLAR VALOR EN ACTA DE INICIO");

            if (controlarValorEnActaInicio) {

                respuesta = validVlrTotal();

            }
        }
        return respuesta;
    }

    public boolean validaDuracion() {

        Registro rs;
        boolean respuesta = true;

        Map<String, Object> parametro = new TreeMap<>();
        parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametro.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                        claseOrden);
        parametro.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);

        try {
            rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubnovedadcontratosControladorUrlEnum.URL24066
                                                                            .getValue())
                                            .getUrl(),
                            parametro));
            String nvlDuracion = ((rs == null)
                || (rs.getCampos().get("DURACION") == null)) ? "0"
                    : rs.getCampos().get("DURACION").toString();

            if (nvlDuracion.isEmpty()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2171"));
                respuesta = false;
            }
        }
        catch (SystemException e2) {
            logger.error(e2.getMessage(), e2);
            JsfUtil.agregarMensajeError(e2.getMessage());
        }
        return respuesta;
    }

    /**
     * Se llama en el metodo actualizarAntes
     *
     * @param strTipoT
     * @return verdadero o falso
     */
    public boolean validacionActualizaAntes(String strTipoT) {
        Registro rs;
        boolean respuesta = true;
        if ("ADI".equals(strTipoT)) {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                            claseOrden);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            numero);
            param.put(SubnovedadcontratosControladorEnum.PARAM2.getValue(),
                            "ACL");

            try {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubnovedadcontratosControladorUrlEnum.URL24065
                                                                                .getValue())
                                                .getUrl(), param));
                if (rs != null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2170"));
                    respuesta = false;
                }
            }
            catch (SystemException e3) {
                logger.error(e3.getMessage(), e3);
                JsfUtil.agregarMensajeError(e3.getMessage());
            }

            validaDuracion();

            Map<String, Object> parametros = new TreeMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                            claseOrden);
            parametros.put(GeneralParameterEnum.NUMERO.getName(),
                            numero);

            try {
                rs = RegistroConverter.toRegistro(requestManager.get(
                                UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubnovedadcontratosControladorUrlEnum.URL24067
                                                                                .getValue())
                                                .getUrl(),
                                parametros));
                String nvlEstado = ((rs == null)
                    || (rs.getCampos().get("ESTADO") == null)) ? ""
                        : rs.getCampos().get("ESTADO").toString();

                if (!"V".equals(nvlEstado)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2172"));
                    respuesta = false;
                }
            }
            catch (SystemException e1) {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }

            Map<String, Object> parametr = new TreeMap<>();
            parametr.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametr.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                            claseOrden);
            parametr.put(GeneralParameterEnum.NUMERO.getName(),
                            numero);
            parametr.put(SubnovedadcontratosControladorEnum.PARAM2.getValue(),
                            "ACI");
            try {
                rs = RegistroConverter.toRegistro(requestManager.get(
                                UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubnovedadcontratosControladorUrlEnum.URL24065
                                                                                .getValue())
                                                .getUrl(),
                                parametr));
                if (rs == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString(cMensaje));
                    respuesta = false;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        return respuesta;
    }

    /**
     * Se llama en el metodo actualizarAntes
     *
     * @param strTipoT
     * @return true o falso
     */
    public boolean camposActualizaAntes(String strTipoT) {
        boolean respuesta = true;
        listatipoT.getDatasource().remove(0);
        String tAutomatica = service.buscarEnLista(strTipoT, cTipoT,
                        cTautomatica,
                        listatipoT.getDatasource()) == null ? ""
                            : service.buscarEnLista(strTipoT,
                                            cTipoT, cTautomatica,
                                            listatipoT.getDatasource());

        if (!"M".equals(tAutomatica)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2173"));
            respuesta = false;
        }

        String nvlObservaciones = registro.getCampos()
                        .get(cObservaciones) == null ? ""
                            : (String) registro.getCampos()
                                            .get(cObservaciones);
        if (nvlObservaciones.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2174"));
            respuesta = false;
        }
        if (registro.getCampos().get(cFecha) == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2175"));
            respuesta = false;
        }

        if (registro.getCampos().get(cFechaInicial) == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2176"));
            respuesta = false;
        }
        if (registro.getCampos().get(cFechaFinal) == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2178"));
            respuesta = false;
        }

        if (registro.getCampos().get("FECHAVENCIMIENTO") == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2180"));
            respuesta = false;
        }
        if (strTipoT.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2182"));
            respuesta = false;
        }
        return respuesta;
    }

    public boolean validacionTipo(String strTipoT) {

        boolean respuesta = true;
        Registro rs;
        if ("ACS".equals(strTipoT)) {
            Map<String, Object> params = new TreeMap<>();

            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                            claseOrden);
            params.put(GeneralParameterEnum.NUMERO.getName(),
                            numero);
            params.put(SubnovedadcontratosControladorEnum.PARAM2.getValue(),
                            "ACI");
            try {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubnovedadcontratosControladorUrlEnum.URL24065
                                                                                .getValue())
                                                .getUrl(), params));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        return respuesta;
    }

    /**
     * Se llama en el metodo tipoAcsAcualizaAntes
     *
     * @param strTipoT
     * @return verdadero o false
     */
    public boolean tiposAcualizaAntes(String strTipoT) {
        boolean respuesta;
        Registro rs;
        respuesta = validacionTipo(strTipoT);
        if ("ARI".equals(strTipoT)) {

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(SubnovedadcontratosControladorEnum.PARAM3.getValue(),
                            claseOrden);
            params.put(GeneralParameterEnum.NUMERO.getName(),
                            numero);
            params.put(SubnovedadcontratosControladorEnum.PARAM2.getValue(),
                            "ACS");
            try {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubnovedadcontratosControladorUrlEnum.URL24065
                                                                                .getValue())
                                                .getUrl(), params));
                if (rs == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2169"));
                    return false;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        return respuesta;
    }

    @Override
    public boolean actualizarDespues() {

        try {
        setNovedad(SysmanFunciones.nvl(registro.getCampos().get(cNovedad),"").toString());
        	
            ejbContratosDosRemote.actDespNovedadContr(compania,
                            SysmanFunciones.nvl(
                                            registro.getCampos().get(cTipoT),
                                            "").toString(),
                            claseOrden,
                            Long.parseLong(numero),
                            Long.parseLong(novedad),
                            SysmanFunciones.nvlStr(registro.getCampos()
                                            .get(cPorcentajeEjecucion)
                                            .toString(),
                                            "0"),
                            (Date) registro.getCampos().get(cFechaInicial),

                            (Date) registro.getCampos().get(cFechaFinal),
                            
                            (Date) registro.getCampos().get("FECHAVENCIMIENTO"),
                            
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get(cValorTotal),
                                            "0.0").toString(),
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get(cDiasContrato), 0).toString());
            
            if (manejaIndicadorDeImpresoEnActas) {
                if (!(boolean) registro.getCampos().get(cImpreso)) {
                	bloqueadoValorTotal = false;
                }
                else
                {
                	bloqueadoValorTotal = true;
                }
            }
            
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;

    }

    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        try {
            ejbContratosDosRemote.eliminarNovedadContrato(
                            compania,
                             registroIni.get(cTipoT).toString(),
                             registroIni.get(cClaseT).toString(),
                            claseOrden,
                            Long.parseLong(numero),
                            Long.parseLong(registro.getCampos().get(cNovedad)
                                            .toString()),
                            Long.parseLong(registro.getCampos()
                                            .get("ORDENDECOMPRA")
                                            .toString()));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    /**
     * Se llama en el metodo eliminarAntes
     *
     * @param registroTipoT
     * @return nvlTipOt
     */
    public String strTipoOt(Registro registroTipoT) {
        String nvlTipOt;
        nvlTipOt = ((registroTipoT == null) ||
            (registroTipoT.getCampos().get(cTipoT) == null)) ? ""
                : (String) registroTipoT.getCampos().get(cTipoT);
        return nvlTipOt;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {

        Map<String, Object> param = new TreeMap<>();

        param.put("ridR", llaveRID);
        param.put("anio", anio);
        param.put("tipoContrato", tipoContrato);
        param.put("claseNov", claseNov);
        param.put("titulo", titulo);
        param.put("parametroswf", parametroswf);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NOVEDADCONTRATOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public boolean isVisibleFormato() {
        return visibleFormato;
    }

    public void setVisibleFormato(boolean visibleFormato) {
        this.visibleFormato = visibleFormato;
    }

    public void ejecutarafectaItems() {

        agregarRegistroNuevo(false);

        if ("m".equals(accion)) {

            Map<String, Object> parametro = new TreeMap<>();
            parametro.put(cClaseOrden, claseOrden);
            parametro.put("ordenDeCompra", numero);
            parametro.put(cNovedad.toLowerCase(), registro
                            .getCampos().get(cNovedad).toString());

            Direccionador direccionador = new Direccionador();

            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.DNOVEDADCONTRATOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametro);
            SessionUtil.redireccionarForma(direccionador, modulo);

        }
    }

    // <SET_GET_ATRIBUTOS>
    public String getClaseT() {
        return claseT;
    }

    public void setClaseT(String claseT) {
        this.claseT = claseT;
    }

    public boolean isManejaNominaDeContratistas() {
        return manejaNominaDeContratistas;
    }

    public void setManejaNominaDeContratistas(
        boolean manejaNominaDeContratistas) {
        this.manejaNominaDeContratistas = manejaNominaDeContratistas;
    }

    public boolean isManejaIndicadorDeImpresoEnActas() {
        return manejaIndicadorDeImpresoEnActas;
    }

    public void setManejaIndicadorDeImpresoEnActas(
        boolean manejaIndicadorDeImpresoEnActas) {
        this.manejaIndicadorDeImpresoEnActas = manejaIndicadorDeImpresoEnActas;
    }

    public boolean ishabilitaCampoValorTotal() {
        return habilitaCampoValorTotal;
    }

    public void sethabilitaCampoValorTotal(
        boolean habilitaCampoValorTotal) {
        this.habilitaCampoValorTotal = habilitaCampoValorTotal;
    }
    
    
    public boolean isManejaBancoDeProyectos() {
        return manejaBancoDeProyectos;
    }

    public void setManejaBancoDeProyectos(boolean manejaBancoDeProyectos) {
        this.manejaBancoDeProyectos = manejaBancoDeProyectos;
    }

    public int getNivelGrupo() {
        return nivelGrupo;
    }

    public void setNivelGrupo(int nivelGrupo) {
        this.nivelGrupo = nivelGrupo;
    }

    public boolean isVisibleTipoRetencion() {
        return visibleTipoRetencion;
    }

    public void setVisibleTipoRetencion(boolean visibleTipoRetencion) {
        this.visibleTipoRetencion = visibleTipoRetencion;
    }

    public boolean isVisibleValorLiberado() {
        return visibleValorLiberado;
    }

    public void setVisibleValorLiberado(boolean visibleValorLiberado) {
        this.visibleValorLiberado = visibleValorLiberado;
    }

    public boolean isvisibleValorTotal() {
        return visibleValorTotal;
    }

    public void setvisibleValorTotal(boolean visibleValorTotal) {
        this.visibleValorTotal = visibleValorTotal;

    }
    public boolean isVisibleVobo() {
        return visibleVobo;
    }

    public void setVisibleVobo(boolean visibleVobo) {
        this.visibleVobo = visibleVobo;
    }
    
    public boolean isNewRecord() {
        return newRecord;
    }

    public void setNewRecord(boolean newRecord) {
        this.newRecord = newRecord;
    }

    public boolean isManejaIndicadorEImpreso() {
        return manejaIndicadorEImpreso;
    }

    public void setManejaIndicadorEImpreso(boolean manejaIndicadorEImpreso) {
        this.manejaIndicadorEImpreso = manejaIndicadorEImpreso;
    }

    public boolean isVisibleAlquiler() {
        return visibleAlquiler;
    }

    public void setVisibleAlquiler(boolean visibleAlquiler) {
        this.visibleAlquiler = visibleAlquiler;
    }

    public boolean isManejaControlDeActas() {
        return manejaControlDeActas;
    }

    public void setManejaControlDeActas(boolean manejaControlDeActas) {
        this.manejaControlDeActas = manejaControlDeActas;
    }

    public boolean isBloqueadoVobo() {
        return bloqueadoVobo;
    }

    public void setBloqueadoVobo(boolean bloqueadoVobo) {
        this.bloqueadoVobo = bloqueadoVobo;
    }

    public boolean isClasenovAYgrupoDif9() {
        return clasenovAYgrupoDif9;
    }

    public void setClasenovAYgrupoDif9(boolean clasenovAYgrupoDif9) {
        this.clasenovAYgrupoDif9 = clasenovAYgrupoDif9;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public Date getFechaFirma() {
        return fechaFirma;
    }

    public void setFechaFirma(Date fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public Double getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(Double valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Date getFechaPolizas() {
        return fechaPolizas;
    }

    public void setFechaPolizas(Date fechaPolizas) {
        this.fechaPolizas = fechaPolizas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isEnabledAfectaItems() {
        return enabledAfectaItems;
    }

    public void setEnabledAfectaItems(boolean enabledAfectaItems) {
        this.enabledAfectaItems = enabledAfectaItems;
    }

    public boolean isAfectaItems() {
        return afectaItems;
    }

    public void setAfectaItems(boolean afectaItems) {
        this.afectaItems = afectaItems;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNumeroRetorno() {
        return numeroRetorno;
    }

    public void setNumeroRetorno(String numeroRetorno) {
        this.numeroRetorno = numeroRetorno;
    }

    public boolean isCalificaContratistaEnActaDeTerminacion() {
        return calificaContratistaEnActaDeTerminacion;
    }

    public void setCalificaContratistaEnActaDeTerminacion(
        boolean calificaContratistaEnActaDeTerminacion) {
        this.calificaContratistaEnActaDeTerminacion = calificaContratistaEnActaDeTerminacion;
    }

    public boolean isVisibleCalificacionContr() {
        return visibleCalificacionContr;
    }

    public void setVisibleCalificacionContr(boolean visibleCalificacionContr) {
        this.visibleCalificacionContr = visibleCalificacionContr;
    }

    public boolean isGeneraAutomaticamenteConsecutivoDeNovedades() {
        return generaAutomaticamenteConsecutivoDeNovedades;
    }

    public void setGeneraAutomaticamenteConsecutivoDeNovedades(
        boolean generaAutomaticamenteConsecutivoDeNovedades) {
        this.generaAutomaticamenteConsecutivoDeNovedades = generaAutomaticamenteConsecutivoDeNovedades;
    }

    public boolean isBloqueadoNumero() {
        return bloqueadoNumero;
    }

    public void setBloqueadoNumero(boolean bloqueadoNumero) {
        this.bloqueadoNumero = bloqueadoNumero;
    }

    public boolean isBloqueadoTipoT() {
        return bloqueadoTipoT;
    }

    public void setBloqueadoTipoT(boolean bloqueadoTipoT) {
        this.bloqueadoTipoT = bloqueadoTipoT;
    }

    public boolean isBloqueadoClaseT() {
        return bloqueadoClaseT;
    }

    public void setBloqueadoClaseT(boolean bloqueadoClaseT) {
        this.bloqueadoClaseT = bloqueadoClaseT;
    }

    public boolean isBloqueadoNovedad() {
        return bloqueadoNovedad;
    }

    public void setBloqueadoNovedad(boolean bloqueadoNovedad) {
        this.bloqueadoNovedad = bloqueadoNovedad;
    }

    public boolean isBloqueadoAno() {
        return bloqueadoAno;
    }

    public void setBloqueadoAno(boolean bloqueadoAno) {
        this.bloqueadoAno = bloqueadoAno;
    }

    public boolean isBloqueadoFecha() {
        return bloqueadoFecha;
    }

    public void setBloqueadoFecha(boolean bloqueadoFecha) {
        this.bloqueadoFecha = bloqueadoFecha;
    }

    public boolean isBloqueadoFechaInicial() {
        return bloqueadoFechaInicial;
    }

    public void setBloqueadoFechaInicial(boolean bloqueadoFechaInicial) {
        this.bloqueadoFechaInicial = bloqueadoFechaInicial;
    }

    public boolean isBloqueadoValorLiberado() {
        return bloqueadoValorLiberado;
    }

    public void setBloqueadoValorLiberado(boolean bloqueadoValorLiberado) {
        this.bloqueadoValorLiberado = bloqueadoValorLiberado;
    }

    public boolean isBloqueadoTexto602() {
        return bloqueadoTexto602;
    }

    public void setBloqueadoTexto602(boolean bloqueadoTexto602) {
        this.bloqueadoTexto602 = bloqueadoTexto602;
    }

    public boolean isBloqueadoTValorTotal() {
        return bloqueadoTValorTotal;
    }

    public void setBloqueadoTValorTotal(boolean bloqueadoTValorTotal) {
        this.bloqueadoTValorTotal = bloqueadoTValorTotal;
    }

    public boolean isBloqueadoPejecucion() {
        return bloqueadoPejecucion;
    }

    public void setBloqueadoPejecucion(boolean bloqueadoPejecucion) {
        this.bloqueadoPejecucion = bloqueadoPejecucion;
    }

    public boolean isBloqueadoTipoRetencion() {
        return bloqueadoTipoRetencion;
    }

    public void setBloqueadoTipoRetencion(boolean bloqueadoTipoRetencion) {
        this.bloqueadoTipoRetencion = bloqueadoTipoRetencion;
    }

    public boolean isBloqueadoObservaciones() {
        return bloqueadoObservaciones;
    }

    public void setBloqueadoObservaciones(boolean bloqueadoObservaciones) {
        this.bloqueadoObservaciones = bloqueadoObservaciones;
    }

    public boolean isBloqueadoTexto66() {
        return bloqueadoTexto66;
    }

    public void setBloqueadoTexto66(boolean bloqueadoTexto66) {
        this.bloqueadoTexto66 = bloqueadoTexto66;
    }

    public boolean isBloqueadoDiasContrato() {
        return bloqueadoDiasContrato;
    }

    public void setBloqueadoDiasContrato(boolean bloqueadoDiasContrato) {
        this.bloqueadoDiasContrato = bloqueadoDiasContrato;
    }

    public boolean isBloqueadoFechaFinal() {
        return bloqueadoFechaFinal;
    }

    public void setBloqueadoFinal(boolean bloqueadoFechaFinal) {
        this.bloqueadoFechaFinal = bloqueadoFechaFinal;
    }

    public boolean isBloqueadoValorTotal() {
        return bloqueadoValorTotal;
    }

    public void setBloqueadoValorTotal(boolean bloqueadoValorTotal) {
        this.bloqueadoValorTotal = bloqueadoValorTotal;
    }

    public boolean isBloqueadoTxtaportest() {
        return bloqueadoTxtaportest;
    }

    public void setBloqueadoTxtaportest(boolean bloqueadoTxtaportest) {
        this.bloqueadoTxtaportest = bloqueadoTxtaportest;
    }

    public boolean isBloqueadoTxtVlrAqVehiculo() {
        return bloqueadoTxtVlrAqVehiculo;
    }

    public void setBloqueadoTxtVlrAqVehiculo(
        boolean bloqueadoTxtVlrAqVehiculo) {
        this.bloqueadoTxtVlrAqVehiculo = bloqueadoTxtVlrAqVehiculo;
    }

    public boolean isBloqueadoTxtResultados() {
        return bloqueadoTxtResultados;
    }

    public void setBloqueadoTxtResultados(boolean bloqueadoTxtResultados) {
        this.bloqueadoTxtResultados = bloqueadoTxtResultados;
    }

    public String getNombreOrdenador() {
        return nombreOrdenador;
    }

    public void setNombreOrdenador(String nombreOrdenador) {
        this.nombreOrdenador = nombreOrdenador;
    }

    public boolean isActualiza() {
        return actualiza;
    }

    public void setActualiza(boolean actualiza) {
        this.actualiza = actualiza;
    }

    public String getEtiquetaMensaje() {
        return etiquetaMensaje;
    }

    public void setEtiquetaMensaje(String etiquetaMensaje) {
        this.etiquetaMensaje = etiquetaMensaje;
    }

    public int getNumeroMensaje() {
        return numeroMensaje;
    }

    public void setNumeroMensaje(int numeroMensaje) {
        this.numeroMensaje = numeroMensaje;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getClaseNov() {
        return claseNov;
    }

    public void setClaseNov(String claseNov) {
        this.claseNov = claseNov;
    }

    public String getTxtNovedad() {
        return txtNovedad;
    }

    public void setTxtNovedad(String txtNovedad) {
        this.txtNovedad = txtNovedad;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaNIT() {
        return listaNIT;
    }

    public void setListaNIT(List<Registro> listaNIT) {
        this.listaNIT = listaNIT;
    }

    public List<Registro> getListaTercerosAportantes() {
        return listaTercerosAportantes;
    }

    public void setListaTercerosAportantes(
        List<Registro> listaTercerosAportantes) {
        this.listaTercerosAportantes = listaTercerosAportantes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaOrdenador() {
        return listaOrdenador;
    }

    public void setListaOrdenador(RegistroDataModelImpl listaOrdenador) {
        this.listaOrdenador = listaOrdenador;
    }

    public RegistroDataModelImpl getListaTexto602() {
        return listaTexto602;
    }

    public void setListaTexto602(RegistroDataModelImpl listaTexto602) {
        this.listaTexto602 = listaTexto602;
    }

    public RegistroDataModelImpl getListatipoT() {
        return listatipoT;
    }

    public void setListatipoT(RegistroDataModelImpl listatipoT) {
        this.listatipoT = listatipoT;
    }

    public Registro getRegistroinicial() {
        return registroinicial;
    }

    /**
     * @return the listaformato
     */
    public List<Registro> getListaformato() {
        return listaformato;
    }

    /**
     * @param listaformato
     * the listaformato to set
     */
    public void setListaformato(List<Registro> listaformato) {
        this.listaformato = listaformato;
    }

    public void setRegistroinicial(Registro registroinicial) {
        this.registroinicial = registroinicial;
    }

    public String getPlazoEntrega() {
        return plazoEntrega;
    }

    public void setPlazoEntrega(String plazoEntrega) {
        this.plazoEntrega = plazoEntrega;
    }

    public String getTxtFecha() {
        return txtFecha;
    }

    public void setTxtFecha(String txtFecha) {
        this.txtFecha = txtFecha;
    }

    /**
     * @return the visibleCumplimientoActiv
     */
    public boolean isVisibleCumplimientoActiv() {
        return visibleCumplimientoActiv;
    }

    /**
     * @param visibleCumplimientoActiv
     * the visibleCumplimientoActiv to set
     */
    public void setVisibleCumplimientoActiv(
        boolean visibleCumplimientoActiv) {
        this.visibleCumplimientoActiv = visibleCumplimientoActiv;
    }

    public boolean isVisibleEval() {
        return visibleEval;
    }

    public void setVisibleEval(boolean visibleEval) {
        this.visibleEval = visibleEval;
    }

    public boolean isIndPago() {
        return indPago;
    }

    public void setIndPago(boolean indPago) {
        this.indPago = indPago;
    }

    public boolean isVisibleAmortizar() {
        return visibleAmortizar;
    }

    public void setVisibleAmortizar(boolean visibleAmortizar) {
        this.visibleAmortizar = visibleAmortizar;
    }

	public String getNovedad() {
		return novedad;
	}

	public void setNovedad(String novedad) {
		this.novedad = novedad;
	}
	
	public Double getValorTotalCont() {
        return valorTotalCont;
    }

    public void setValorTotalCont(Double valorTotalCont) {
        this.valorTotalCont = valorTotalCont;
    }

	/**
	 * @return the visibleAdjuntos
	 */
	public boolean isVisibleAdjuntos() {
		return visibleAdjuntos;
	}

	/**
	 * @param visibleAdjuntos the visibleAdjuntos to set
	 */
	public void setVisibleAdjuntos(boolean visibleAdjuntos) {
		this.visibleAdjuntos = visibleAdjuntos;
	}
    
    

    /**
     * Retorna la lista listatipoT
     *
     * @return listatipoT
     */

    /**
     * Asigna la lista listatipoT
     *
     * @param listatipoT
     * Variable a asignar en listatipoT
     */

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

}
