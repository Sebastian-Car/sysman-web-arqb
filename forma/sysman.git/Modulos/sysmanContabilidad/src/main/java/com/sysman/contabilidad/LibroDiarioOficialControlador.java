package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LibroDiarioOficialControladorEnum;
import com.sysman.contabilidad.enums.LibroDiarioOficialControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
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
 * @author jrodriguezr
 * @version 1, 28/04/2016
 * @modifier amonroy
 * @version 2, 07/04/2017 Proceso de Refactoring y Revision de buenas
 * practicas sugeridas por la herramienta SonarLint
 * 
 * @version 3.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema.
 */
@ManagedBean
@ViewScoped
public class LibroDiarioOficialControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al reporte "000700LisDiarioOficial" en el formulario,
     * almacena el texto 000700LisDiarioOficial
     */
    private final String cReporte700;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al reporte "000701LisDiarioOficialCC" en el formulario,
     * almacena el texto 000701LisDiarioOficialCC
     */
    private final String cReporte701;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al reporte "000703LisDiarioOficialFechaCR" en el
     * formulario, almacena el texto 000703LisDiarioOficialFechaCR
     */
    private final String cReporte703;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "CODIGO" en el formulario, almacena el
     * texto CODIGO
     */
    private final String cCodigo;
    private String claseImpresion;
    private String titulo;
    private String cuentaInicial;
    private String cuentaFinal;
    private String centroInicial;
    private String centroFinal;
    private String anoTrabajo;
    private String mes;
    private String codigoLibro;
    private boolean imprimirFecha;
    private int numeroInicial;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaCmbCentroCInicial;
    private RegistroDataModelImpl listaCmbCentroCFinal;

    private boolean numeroInicialVisible;
    private boolean opcion85Visible;
    private boolean centroCostoVisible;
    private boolean tituloVisible;
    private boolean claseImpresionVisible;
    private boolean conCentrosVisible;
    private boolean centroCosto;
    private boolean comprobante;
    private boolean cuenta;
    private boolean resumen2;
    private boolean resumen6;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LibroDiarioOficialControlador
     */
    public LibroDiarioOficialControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cReporte700 = "000700LisDiarioOficial";
        cReporte701 = "000701LisDiarioOficialCC";
        cReporte703 = "000703LisDiarioOficialFechaCR";
        cCodigo = "CODIGO";
        try {
            // 672
            numFormulario = GeneralCodigoFormaEnum.LIBRO_DIARIO_OFICIAL_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            cuentaInicial = "0";
            cuentaFinal = "9999999999999999";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LibroDiarioOficialControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        String formatoEspImpresion = null;
        try {
            formatoEspImpresion = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO ESPECIAL IMPRESION", modulo, new Date(),
                            true);
            imprimirFecha = "SI".equalsIgnoreCase(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "IMPRIMIR FECHAS EN INFORMES OFICIALES",
                                            modulo, new Date(),
                                            true), "NO")
                            .toString());
        }
        catch (SystemException e) {
            Logger.getLogger(LibroDiarioOficialControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((formatoEspImpresion != null)
            && "SI".equalsIgnoreCase(formatoEspImpresion)) {
            numeroInicialVisible = true;
            opcion85Visible = true;
        }
        else {
            numeroInicialVisible = false;
            opcion85Visible = false;
        }
        cargarListaAnoTrabajo();
        anoTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMesTrabajo();
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        cargarListaCuentaInicial();
        cargarListaCmbCentroCInicial();
        cargarListaCmbCentroCFinal();
        claseImpresionVisible = true;
        claseImpresion = "1";
        titulo = "1";
        comprobante = true;
        conCentrosVisible = true;
        numeroInicial = 1;
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroDiarioOficialControladorUrlEnum.URL6311
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        try {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroDiarioOficialControladorUrlEnum.URL6680
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
                                        LibroDiarioOficialControladorUrlEnum.URL7139
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroDiarioOficialControladorUrlEnum.URL8203
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
        param.put(LibroDiarioOficialControladorEnum.PARAM0.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCmbCentroCInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroDiarioOficialControladorUrlEnum.URL9410
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbCentroCInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCmbCentroCFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroDiarioOficialControladorUrlEnum.URL10158
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);

        listaCmbCentroCFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * validación de campos obligatorios en el formulario
     * 
     * @return verdadero si todos los ca,pos poseen valor
     */
    private boolean validarCamposObligatorios() {
        boolean respuesta = true;
        if (SysmanFunciones.validarVariableVacio(anoTrabajo)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB113"));
            respuesta = false;
        }
        if (SysmanFunciones.validarVariableVacio(mes)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB114"));
            respuesta = false;
        }
        if (centroCosto
            && (SysmanFunciones.validarVariableVacio(centroInicial))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB115"));
            respuesta = false;
        }
        if (centroCosto
            && (SysmanFunciones.validarVariableVacio(centroFinal))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB116"));
            respuesta = false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB117"));
            respuesta = false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB118"));
            respuesta = false;
        }
        return respuesta;
    }

    /**
     * Define el valor a enviar en el reemplazo "agrupar" para generar
     * el informe
     * 
     * @return valor de acuerdo al check seleccionado
     */
    private String definirAgrupar() {
        String agupadoPor;
        if (comprobante) {
            agupadoPor = "1";
        }
        else if (cuenta) {
            agupadoPor = "2";
        }
        else if (resumen2) {
            agupadoPor = "3";
        }
        else if (resumen6) {
            agupadoPor = "4";
        }
        else {
            agupadoPor = "";
        }
        return agupadoPor;
    }

    /**
     * Evalua si el reporte a generar es el 703 y envia el nombre de
     * la consulta a la que debe apuntar
     * 
     * @param reporte
     * nombre del reporte a generar
     * @return nombre de consulta
     */
    private String definirConsultaReporte703(String reporte) {
        return "000703LisDiarioOficialFechaCR".equalsIgnoreCase(reporte)
            ? cReporte703
            : "";
    }

    /**
     * Define condicion de centro de costo para el informe
     * 000701LisDiarioOficialCC
     * 
     * @param reporte
     * nombre del reporte a generar
     * @return valor de la condicion
     */
    private String definirCondicionCentroCosto(String reporte) {
        return cReporte701.equals(reporte)
            ? " AND V_DETALLE_AUXILIAR_CNT.CENTRO_COSTO BETWEEN '"
                + centroInicial + "'" + " AND '" + centroFinal + "' "
            : "";
    }

    /**
     * Evalua si el reporte a generar esta entre los reportes
     * 700,701,702
     * 
     * @param reporte
     * el reporte que se va a generar
     * @return verdadero si corresponde a alguno de los reportes
     * 700,701,702
     */
    private boolean evaluarReporte(String reporte) {
        return cReporte700.equalsIgnoreCase(reporte)
            || cReporte701.equalsIgnoreCase(reporte)
            || "000702LisDiarioOficialCO".equalsIgnoreCase(reporte)
            || evaluarReporteAux(reporte);
    }

    /**
     * Evalua si el reporte a generar esta entre los reportes 704 o
     * 1449
     * 
     * @param reporte
     * el reporte que se va a generar
     * @return verdadero si corresponde a alguno de los reportes 704 o
     * 1449
     */
    private boolean evaluarReporteAux(String reporte) {
        return "000704LisDiarioOficialFechaCuenta".equalsIgnoreCase(reporte)
            || "001449LisDiarioOficialFechaCuentaSinFoliador"
                            .equalsIgnoreCase(reporte);
    }

    private void generaInforme(ReportesBean.FORMATOS formato) {
        if (validarCamposObligatorios()) {

            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = getReporte();
            String mesTrabajo = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mes)].toUpperCase();
            String centroCostoCond = definirCondicionCentroCosto(reporte);

            reemplazar.put("anoTrabajo", anoTrabajo);
            reemplazar.put("mesTrabajo", mes);
            reemplazar.put("agrupar", definirAgrupar());
            reemplazar.put("centroCostoCond", centroCostoCond);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            String parametroLibro = !SysmanFunciones
                            .validarVariableVacio(codigoLibro)
                                ? idioma.getString("TB_TB3092").replace(
                                                "s$codigoLibro$s",
                                                codigoLibro)
                                : "";

            parametros.put("PR_MESTRABAJO", mesTrabajo);
            parametros.put("PR_ANOTRABAJO", anoTrabajo);
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_NUMEROINICIAL", numeroInicial - 1);
            parametros.put("PR_CODIGOLIBRO", parametroLibro);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_VISIBLEPAGINA",
                            parametroLibro.isEmpty() ? false : true);
            parametros.put("PR_IMPRIMIRFECHA", imprimirFecha);

            Reporteador.resuelveConsulta(
                            evaluarReporte(reporte)
                                || "000705LisDiarioOficialFechaCuentaCO"
                                                .equalsIgnoreCase(reporte)
                                                    ? cReporte700
                                                    : definirConsultaReporte703(
                                                                    reporte),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            try {
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Define el nombre del reporte si se define la agrupacion por
     * comprobante o cuenta y la clase de impresion es Especial (3)
     * 
     * @param nomInfLibroOfiCuentaEspecial
     * valor del parametro
     * "NOMBRE INFORME LIBRO DIARIO OFICIAL CUENTA ESPECIAL"
     * @return nombre de reporte
     */
    private String getReporteComprobante(
        String nomInfLibroOfiCuentaEspecial) {
        String rp = null;
        if (comprobante) {
            rp = "000702LisDiarioOficialCO";
        }
        if (cuenta) {
            if (nomInfLibroOfiCuentaEspecial == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB119"));
                return null;
            }
            else {
                rp = nomInfLibroOfiCuentaEspecial;
            }
        }
        return rp;
    }

    /**
     * Evalua si los indicadores de Resumen Nivel 2 o Resumen Nivel 6
     * están seleccionados
     * 
     * @return si se ha seleccionado algun check de resumen
     */
    private boolean validarResumen() {
        return resumen2 || resumen6;
    }

    private String getReporte() {
        String reporte = "";
        String nomInfLibroOfiCuentaEspecial = null;
        String nomInfLibCuentaConFoliador = null;
        try {
            nomInfLibroOfiCuentaEspecial = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE INFORME LIBRO DIARIO OFICIAL CUENTA ESPECIAL",
                            modulo, new Date(), false);
            nomInfLibCuentaConFoliador = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE INFORME LIBRO DIARIO OFICIAL CUENTA CON FOLIADOR",
                            modulo, new Date(), false);
        }
        catch (SystemException e) {
            Logger.getLogger(LibroDiarioOficialControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ("3".equals(claseImpresion)) {
            reporte = getReporteComprobante(nomInfLibroOfiCuentaEspecial);
        }
        else if ("1".equals(claseImpresion) && cuenta) {
            if (nomInfLibCuentaConFoliador == null) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB120"));
            }
            else {
                reporte = nomInfLibCuentaConFoliador;
            }
        }
        else if ("2".equals(claseImpresion) && cuenta) {
            reporte = "001449LisDiarioOficialFechaCuentaSinFoliador";
        }
        else {
            reporte = getReporteAuxiliar();
        }
        return reporte;
    }

    /**
     * Metodo que permite definir el nombre del reporte que se va a
     * generar dependiendo de los indicadores seleccionados en el
     * formulario
     * 
     * @return nombre del formulario a generar
     */
    public String getReporteAuxiliar() {
        String reporte = null;
        if (comprobante) {
            if (centroCosto && (centroInicial != null
                || !centroFinal.equals(SysmanConstantes.CONS_CENTRO))) {
                reporte = cReporte701;
            }
            else {
                reporte = cReporte700;
            }
        }
        if (validarResumen()) {
            reporte = cReporte703;
        }
        return reporte;
    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        cuentaInicial = null;
        cuentaFinal = null;
        centroInicial = null;
        centroFinal = null;
        cargarListaMesTrabajo();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaCmbCentroCInicial();
        cargarListaCmbCentroCFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCentroCosto() {
        // <CODIGO_DESARROLLADO>
        if (centroCosto) {
            centroInicial = "0";
            centroFinal = SysmanConstantes.CONS_CENTRO;
            centroCostoVisible = true;
        }
        else {
            centroCostoVisible = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcomprobante() {
        // <CODIGO_DESARROLLADO>
        if (comprobante) {
            cuenta = false;
            resumen2 = false;
            resumen6 = false;
            claseImpresionVisible = true;
            centroCosto = false;
            cambiarCentroCosto();
            conCentrosVisible = true;
        }
        else {
            comprobante = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcuenta() {
        // <CODIGO_DESARROLLADO>
        if (cuenta) {
            claseImpresionVisible = true;
            comprobante = false;
            resumen2 = false;
            resumen6 = false;
            centroCosto = false;
            cambiarCentroCosto();
            conCentrosVisible = false;
        }
        else {
            cuenta = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarresumen2() {
        // <CODIGO_DESARROLLADO>
        if (resumen2) {
            comprobante = false;
            cuenta = false;
            resumen6 = false;
            claseImpresionVisible = false;
            tituloVisible = false;
            setCodigoLibro(codigoLibro);
            setNumeroInicial(numeroInicial);
            setMes(mes);
            centroCosto = false;
            cambiarCentroCosto();
            conCentrosVisible = false;
        }
        else {
            resumen2 = true;
            claseImpresionVisible = true;
            if ("2".equals(claseImpresion) || "3".equals(claseImpresion)) {
                tituloVisible = true;
            }
            else {
                tituloVisible = false;
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarresumen6() {
        // <CODIGO_DESARROLLADO>
        if (resumen6) {
            comprobante = false;
            cuenta = false;
            resumen2 = false;
            claseImpresionVisible = false;
            tituloVisible = false;
            centroCosto = false;
            cambiarCentroCosto();
            conCentrosVisible = false;
        }
        else {
            resumen6 = true;
            claseImpresionVisible = true;
            if ("2".equals(claseImpresion) || "3".equals(claseImpresion)) {
                tituloVisible = true;
            }
            else {
                tituloVisible = false;
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarClaseImpresion() {
        // <CODIGO_DESARROLLADO>
        if ("2".equals(claseImpresion) || "3".equals(claseImpresion)) {
            tituloVisible = true;
        }
        else {
            tituloVisible = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    public void seleccionarFilaCmbCentroCInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        centroFinal = null;
        cargarListaCmbCentroCFinal();
    }

    public void seleccionarFilaCmbCentroCFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    public String getClaseImpresion() {
        return claseImpresion;
    }

    public void setClaseImpresion(String claseImpresion) {
        this.claseImpresion = claseImpresion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getCodigoLibro() {
        return codigoLibro;
    }

    public void setCodigoLibro(String codigoLibro) {
        this.codigoLibro = codigoLibro;
    }

    public int getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(int numeroInicial) {
        this.numeroInicial = numeroInicial;
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

    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
    }

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

    public RegistroDataModelImpl getListaCmbCentroCInicial() {
        return listaCmbCentroCInicial;
    }

    public void setListaCmbCentroCInicial(
        RegistroDataModelImpl listaCmbCentroCInicial) {
        this.listaCmbCentroCInicial = listaCmbCentroCInicial;
    }

    public RegistroDataModelImpl getListaCmbCentroCFinal() {
        return listaCmbCentroCFinal;
    }

    public void setListaCmbCentroCFinal(
        RegistroDataModelImpl listaCmbCentroCFinal) {
        this.listaCmbCentroCFinal = listaCmbCentroCFinal;
    }

    public boolean isNumeroInicialVisible() {
        return numeroInicialVisible;
    }

    public void setNumeroInicialVisible(boolean numeroInicialVisible) {
        this.numeroInicialVisible = numeroInicialVisible;
    }

    public boolean isOpcion85Visible() {
        return opcion85Visible;
    }

    public void setOpcion85Visible(boolean opcion85Visible) {
        this.opcion85Visible = opcion85Visible;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isCentroCostoVisible() {
        return centroCostoVisible;
    }

    public void setCentroCostoVisible(boolean centroCostoVisible) {
        this.centroCostoVisible = centroCostoVisible;
    }

    public boolean isTituloVisible() {
        return tituloVisible;
    }

    public void setTituloVisible(boolean tituloVisible) {
        this.tituloVisible = tituloVisible;
    }

    public boolean isClaseImpresionVisible() {
        return claseImpresionVisible;
    }

    public void setClaseImpresionVisible(boolean claseImpresionVisible) {
        this.claseImpresionVisible = claseImpresionVisible;
    }

    public boolean isConCentrosVisible() {
        return conCentrosVisible;
    }

    public void setConCentrosVisible(boolean conCentrosVisible) {
        this.conCentrosVisible = conCentrosVisible;
    }

    public boolean isCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto) {
        this.centroCosto = centroCosto;
    }

    public boolean isComprobante() {
        return comprobante;
    }

    public void setComprobante(boolean comprobante) {
        this.comprobante = comprobante;
    }

    public boolean isCuenta() {
        return cuenta;
    }

    public void setCuenta(boolean cuenta) {
        this.cuenta = cuenta;
    }

    public boolean isResumen2() {
        return resumen2;
    }

    public void setResumen2(boolean resumen2) {
        this.resumen2 = resumen2;
    }

    public boolean isResumen6() {
        return resumen6;
    }

    public void setResumen6(boolean resumen6) {
        this.resumen6 = resumen6;
    }
}
