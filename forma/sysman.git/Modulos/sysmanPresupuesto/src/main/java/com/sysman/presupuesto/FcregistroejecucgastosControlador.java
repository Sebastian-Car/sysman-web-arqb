package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FcregistroejecucgastosControladorEnum;
import com.sysman.presupuesto.enums.FcregistroejecucgastosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
 * @author lcortes
 * @version 1, 27/06/2016
 * 
 * @version 2, 18/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico Refactoring.<br>
 * Manejo de EJBs.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class FcregistroejecucgastosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo desde el que se
     * abre el formulario.
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.NOMBRE</code>
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.CODIGO</code>
     */
    private final String cCodigo;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * <code>FcregistroejecucgastosControladorEnum.TIPOVIGENCIA</code>
     */
    private final String cTipoVigencia;

    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private boolean enMiles;
    private boolean informesNacion;
    private boolean conCentros;
    private boolean conFuentes;
    private String cuentaInicial;
    private String cuentaFinal;
    private String centroCostoInicial;
    private String centroCostoFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String tipoVigencia;
    private String anio;
    private String mes;
    private String nivel;
    private String nomCuentaInicial;
    private String nomCuentaFinal;
    private String nomCentroInicial;
    private String nomCentroFinal;
    private String nomFuenteInicial;
    private String nomFuenteFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listacentrocostoInicial;
    private RegistroDataModelImpl listacentrocostoFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // </DECLARAR_EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_EJBs>
    /**
     * Creates a new instance of FcregistroejecucgastosControlador
     */
    public FcregistroejecucgastosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cTipoVigencia = FcregistroejecucgastosControladorEnum.TIPOVIGENCIA
                        .getValue();

        try {
            numFormulario = GeneralCodigoFormaEnum.FCREGISTROEJECUCGASTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FcregistroejecucgastosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            // No se limpian parametros flash o no se requiere
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaAno();
        cargarListaMes();
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        nivel = "60";
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR952-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore TipoVigencia_AfterUpdate End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcregistroejecucgastosControladorUrlEnum.URL5613
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcregistroejecucgastosControladorUrlEnum.URL6015
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucgastosControladorUrlEnum.URL6469
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
        param.put(cTipoVigencia, tipoVigencia);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucgastosControladorUrlEnum.URL7304
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
        param.put(cTipoVigencia, tipoVigencia);
        param.put(FcregistroejecucgastosControladorEnum.CUENTAINI.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListacentrocostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucgastosControladorUrlEnum.URL8131
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListacentrocostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucgastosControladorUrlEnum.URL8743
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCostoInicial);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucgastosControladorUrlEnum.URL9459
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaFuenteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucgastosControladorUrlEnum.URL10065
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(FcregistroejecucgastosControladorEnum.CODIGOINICIAL
                        .getValue(), fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdExcel() {
        // <CODIGO_DESARROLLADO>
        generarReporte(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarReporte(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;

        if (faltanCamposObligatorios()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB658"));
            return;
        }

        String condicion = "";
        try {
            condicion = generarCondicion();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return;
        }

        String conSeccion = "";
        String seccion = "";
        String conUnidad = "";
        String unidad = "";
        String condicionInforme = "";
        String strEnMiles;
        String nomTipoVigencia = getNombreVigencia(tipoVigencia);

        String parametroSeccion = getParametro(
                        "SECCION EN INFORMES RESOLUCION 036", "NO");
        String parametroMaximoDigitos = getParametro(
                        "MAXIMO DIGITOS ESTRUCTURA PRESUPUESTAL", " ");
        String parametroCargo1 = getParametro("CARGO1 EN RESOLUCION 036", " ");
        String parametroCargo2 = getParametro("CARGO2 EN RESOLUCION 036", " ");
        String parametroCargo3 = getParametro("CARGO3 EN RESOLUCION 036", " ");
        String parametroFirma1 = getParametro("FIRMA1 EN RESOLUCION 036", " ");
        String parametroFirma2 = getParametro("FIRMA2 EN RESOLUCION 036", " ");
        String parametroFirma3 = getParametro("FIRMA3 EN RESOLUCION 036", " ");
        String parametroCedula1 = getParametro("CEDULA1 EN RESOLUCION 036",
                        " ");
        String parametroCedula2 = getParametro("CEDULA2 EN RESOLUCION 036",
                        " ");
        String parametroCedula3 = getParametro("CEDULA3 EN RESOLUCION 036",
                        " ");

        if ("SI".equalsIgnoreCase(parametroSeccion)) {
            conSeccion = getParametro("SECCION 036", " ");
            if (!"".equals(conSeccion)) {
                seccion = "SECCION";
            }
            conUnidad = getParametro("UNIDAD EJECUTORA 036", " ");
            if (!"".equals(conUnidad)) {
                unidad = "UNIDAD EJECUTORA";
            }
        }

        strEnMiles = enMiles ? "-1" : "0";

        if (informesNacion) {
            condicionInforme = " AND PLAN_PRESUPUESTAL.INFORME NOT IN (0) ";
        }

        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        // Reemplazos valores consulta reporte
        reemplazos.put("enMiles", strEnMiles);
        reemplazos.put("mes", mes);
        reemplazos.put("anio", anio);
        reemplazos.put("cuentaInicial", cuentaInicial);
        reemplazos.put("cuentaFinal", cuentaFinal);
        reemplazos.put("nivel", nivel);
        reemplazos.put("condicion", condicion);
        reemplazos.put("maxDigitos", parametroMaximoDigitos);
        reemplazos.put("condInforme", condicionInforme);

        // Reemplazo parametros reporte
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_TIPOVIGENCIA", nomTipoVigencia);
        parametros.put("PR_MES",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes)]);
        parametros.put("PR_CONSECCION", conSeccion);
        parametros.put("PR_CONUNIDAD", conUnidad);
        parametros.put("PR_SECCION", seccion);
        parametros.put("PR_UNIDAD", unidad);
        parametros.put("PR_CARGO1_EN_RESOLUCION_036", parametroCargo1);
        parametros.put("PR_CARGO2_EN_RESOLUCION_036", parametroCargo2);
        parametros.put("PR_CARGO3_EN_RESOLUCION_036", parametroCargo3);
        parametros.put("PR_FIRMA1_EN_RESOLUCION_036", parametroFirma1);
        parametros.put("PR_FIRMA2_EN_RESOLUCION_036", parametroFirma2);
        parametros.put("PR_FIRMA3_EN_RESOLUCION_036", parametroFirma3);
        parametros.put("PR_CEDULA1_EN_RESOLUCION_036", parametroCedula1);
        parametros.put("PR_CEDULA2_EN_RESOLUCION_036", parametroCedula2);
        parametros.put("PR_CEDULA3_EN_RESOLUCION_036", parametroCedula3);
        parametros.put("PR_ANO", anio);
        parametros.put("PR_FORMATO", getFormatoApropiaciones());

        archivoDescarga = getReporte(reemplazos, parametros, formato);
    }

    /**
     * Trae el patron usado para formatear los decimales en el
     * reporte.
     * 
     * @return cadena que contiene la expresion de formato numerico
     */
    private String getFormatoApropiaciones() {
        return enMiles ? "#,#00;(#,#00)" : "#,#00.00;(#,#00.00)";
    }

    /**
     * Trae el nombre de la vigencia.
     * 
     * @param tipo
     * Tipo de vigencia
     * @return Nombre de la vigencia segun el tipo.
     */
    private String getNombreVigencia(String tipo) {
        String nombre;
        switch (tipo) {
        case "RC":
            nombre = "CUENTAS POR PAGAR";
            break;
        case "RA":
            nombre = "RESERVAS PRESUPUESTALES";
            break;
        case "VA":
            nombre = "VIGENCIA  ACTUAL";
            break;
        default:
            nombre = "";
            break;
        }
        return nombre;
    }

    /**
     * Si requiere seleccion de cento de costo o fuente de recurso,
     * construye la condicion por cada uno.
     * 
     * @return Condicion por centro de costo o fuente de recurso, o
     * cadena vacia si no se requiere.
     * @throws SysmanException
     * En caso de nulo.
     */
    private String generarCondicion() throws SysmanException {
        StringBuilder condicion = new StringBuilder("");
        if (conCentros) {
            if (faltaCentroCosto()) {
                throw new SysmanException(idioma.getString("TB_TB659"));
            }
            condicion.append(" AND PLAN_PRESUPUESTAL.CENTRO_COSTO BETWEEN '"
                + centroCostoInicial + "' AND '" + centroCostoFinal
                + "' \r\n");
        }
        if (conFuentes) {
            if (faltaFuentesRecurso()) {
                throw new SysmanException(idioma.getString("TB_TB660"));
            }
            condicion.append(" AND PLAN_PRESUPUESTAL.AUXILIAR BETWEEN '"
                + fuenteInicial + "' AND '" + fuenteFinal + "' \r\n");
        }
        return condicion.toString();
    }

    /**
     * Genera el reporte como objeto tipo StreamedContent.
     * 
     * @param reporte
     * Nombre del reporte.
     * @param reemplazos
     * Reemplazos para resolver la consulta.
     * @param parametros
     * Parametros necesarios para generar el reporte.
     * @param formato
     * Formato con el que se generara el informe.
     * @return archivo para descarga.
     */
    private StreamedContent getReporte(HashMap<String, Object> reemplazos,
        Map<String, Object> parametros, FORMATOS formato) {
        try {
            String reporte;
            if (indicador) {
                reemplazos.put("nivel", "60");
                reporte = "000944FCREGISTROEJECUCGASTOS036";
            }
            else {
                reporte = "000945FCREGISTROEJECUCGASTOS036SO";
            }
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazos,
                            parametros);

            return JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            Logger.getLogger(FcregistroejecucgastosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }
    }

    /**
     * Revisa si el usuario selecciono la fuente de recurso inicial y
     * final.
     * 
     * @return Verdadero si falta seleccionar alguna fuente de
     * recurso.
     */
    private boolean faltaFuentesRecurso() {
        return (fuenteInicial == null) || "".equals(fuenteInicial)
            || (fuenteFinal == null) || "".equals(fuenteFinal);
    }

    /**
     * Revisa si el usuario selecciono el centro de costo inicial y
     * final.
     * 
     * @return Verdadero si falta diligenciar el centro de costo
     * inicial o el final.
     */
    private boolean faltaCentroCosto() {
        return (centroCostoInicial == null) || "".equals(centroCostoInicial)
            || (centroCostoFinal == null) || "".equals(centroCostoFinal);
    }

    /**
     * Revision de campos obligatorios.
     * 
     * @return Verdadero si faltan campos por diligenciar.
     */
    private boolean faltanCamposObligatorios() {
        boolean faltanCampos = false;
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(mes)
            || SysmanFunciones.validarVariableVacio(tipoVigencia)) {
            faltanCampos = true;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)
            || SysmanFunciones.validarVariableVacio(cuentaFinal)
            || SysmanFunciones.validarVariableVacio(nivel)) {
            faltanCampos = true;
        }
        return faltanCampos;
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
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
                            nombreParametro, modulo, new Date(), false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        mes = "";
        cuentaInicial = nomCuentaInicial = "";
        cuentaFinal = nomCuentaFinal = "";
        centroCostoInicial = nomCentroInicial = "";
        centroCostoFinal = nomCentroFinal = "";
        fuenteInicial = nomFuenteInicial = "";
        fuenteFinal = nomFuenteFinal = "";
        listaCuentaFinal = listacentrocostoFinal = listaFuenteFinal = null;
        cargarListaMes();
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();
    }

    public void cambiarTipoVigencia() {
        cuentaInicial = "";
        nomCuentaInicial = "";
        cuentaFinal = "";
        nomCuentaFinal = "";
        listaCuentaFinal = null;
        cargarListaMes();
        cargarListaCuentaInicial();
    }

    public void cambiarConCentrosCosto() {
        cargarListacentrocostoInicial();

    }

    public void cambiarConFuentes() {
        cargarListaFuenteInicial();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = asignarValorCampo(registroAux, "CODIGO");
        nomCuentaInicial = asignarValorCampo(registroAux, cNombre);
        cuentaFinal = "";
        nomCuentaFinal = "";

        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = asignarValorCampo(registroAux, "CODIGO");
        nomCuentaFinal = asignarValorCampo(registroAux, cNombre);
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = asignarValorCampo(registroAux, cCodigo);
        nomCentroInicial = asignarValorCampo(registroAux, cNombre);
        centroCostoFinal = "";
        nomCentroFinal = "";
        cargarListacentrocostoFinal();
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = asignarValorCampo(registroAux, cCodigo);
        nomCentroFinal = asignarValorCampo(registroAux, cNombre);
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = asignarValorCampo(registroAux, cCodigo);
        nomFuenteInicial = asignarValorCampo(registroAux, cNombre);
        fuenteFinal = "";
        nomFuenteFinal = "";

        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = asignarValorCampo(registroAux, cCodigo);
        nomFuenteFinal = asignarValorCampo(registroAux, cNombre);
    }

    // </METODOS_COMBOS_GRANDES>

    /**
     * Verifica que el registro <code>reg</code> tenga una coleccion
     * de campos que no sea nula.
     * 
     * @param reg
     * @param campo
     * El campo a evaluar en la coleccion.
     * @return El valor del campo segun la coleccion.
     */
    private String asignarValorCampo(Registro reg, String campo) {
        return reg.getCampos().isEmpty() ? ""
            : SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                : reg.getCampos().get(campo).toString();
    }

    // <SET_GET_ATRIBUTOS>

    public boolean isIndicador() {
        return indicador;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    public boolean isEnMiles() {
        return enMiles;
    }

    public void setEnMiles(boolean enMiles) {
        this.enMiles = enMiles;
    }

    public boolean isInformesNacion() {
        return informesNacion;
    }

    public void setInformesNacion(boolean informesNacion) {
        this.informesNacion = informesNacion;
    }

    public boolean isConCentros() {
        return conCentros;
    }

    public void setConCentros(boolean conCentros) {
        this.conCentros = conCentros;
    }

    public boolean isConFuentes() {
        return conFuentes;
    }

    public void setConFuentes(boolean conFuentes) {
        this.conFuentes = conFuentes;
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

    public String getCentroCostoInicial() {
        return centroCostoInicial;
    }

    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }

    public String getCentroCostoFinal() {
        return centroCostoFinal;
    }

    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
    }

    public String getFuenteInicial() {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    public String getFuenteFinal() {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    public String getTipoVigencia() {
        return tipoVigencia;
    }

    public void setTipoVigencia(String tipoVigencia) {
        this.tipoVigencia = tipoVigencia;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNomCuentaInicial() {
        return nomCuentaInicial;
    }

    public void setNomCuentaInicial(String nomCuentaInicial) {
        this.nomCuentaInicial = nomCuentaInicial;
    }

    public String getNomCuentaFinal() {
        return nomCuentaFinal;
    }

    public void setNomCuentaFinal(String nomCuentaFinal) {
        this.nomCuentaFinal = nomCuentaFinal;
    }

    public String getNomCentroInicial() {
        return nomCentroInicial;
    }

    public void setNomCentroInicial(String nomCentroInicial) {
        this.nomCentroInicial = nomCentroInicial;
    }

    public String getNomCentroFinal() {
        return nomCentroFinal;
    }

    public void setNomCentroFinal(String nomCentroFinal) {
        this.nomCentroFinal = nomCentroFinal;
    }

    public String getNomFuenteInicial() {
        return nomFuenteInicial;
    }

    public void setNomFuenteInicial(String nomFuenteInicial) {
        this.nomFuenteInicial = nomFuenteInicial;
    }

    public String getNomFuenteFinal() {
        return nomFuenteFinal;
    }

    public void setNomFuenteFinal(String nomFuenteFinal) {
        this.nomFuenteFinal = nomFuenteFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListacentrocostoInicial() {
        return listacentrocostoInicial;
    }

    public void setListacentrocostoInicial(
        RegistroDataModelImpl listacentrocostoInicial) {
        this.listacentrocostoInicial = listacentrocostoInicial;
    }

    public RegistroDataModelImpl getListacentrocostoFinal() {
        return listacentrocostoFinal;
    }

    public void setListacentrocostoFinal(
        RegistroDataModelImpl listacentrocostoFinal) {
        this.listacentrocostoFinal = listacentrocostoFinal;
    }

    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
