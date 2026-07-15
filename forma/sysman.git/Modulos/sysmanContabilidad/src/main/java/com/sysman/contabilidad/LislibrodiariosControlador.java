package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LislibrodiariosControladorEnum;
import com.sysman.contabilidad.enums.LislibrodiariosControladorUrlEnum;
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
 * @author sdaza
 * @version 1, 02/05/2016
 * @modifier amonroy
 * @version 2, 10/04/2017 Proceso de Refactoring y Revision de buenas
 * practicas sugeridas por la herramienta SonarLint
 */
@ManagedBean
@ViewScoped
public class LislibrodiariosControlador extends BeanBaseModal {
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "CODIGO" en el formulario, almacena el
     * texto CODIGO
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "LIBRO DIARIO OFICIAL" en el formulario,
     * almacena el texto LIBRO DIARIO OFICIAL
     */
    private final String cLibro;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "PR_TITULO" en el formulario, almacena el
     * texto PR_TITULO
     */
    private final String cPrTitulo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "anoTrabajo" en el formulario, almacena
     * el texto anoTrabajo
     */
    private final String cAnio;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "compania" en el formulario, almacena el
     * texto compania
     */
    private final String cCompania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra ""cuentaFinal"" en el formulario,
     * almacena el texto "cuentaFinal"
     */
    private final String cCuentaFinal;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "cuentaInicial" en el formulario,
     * almacena el texto cuentaInicial
     */
    private final String cCuentaInicial;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "digitos" en el formulario, almacena el
     * texto digitos
     */
    private final String cDigitos;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "mesTrabajo" en el formulario, almacena
     * el texto mesTrabajo
     */
    private final String cMes;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "CODIGO" en el formulario, almacena el
     * texto CODIGO
     */
    private String modulo;
    private String opClaseImpresion;
    private String opAgrupar;
    private String opTitulo;
    private String cuentaInicial;
    private String cuentaFinal;
    private String anoTrabajo;
    private String mesTrabajo;
    private String digitos;
    private String codigoLibro;
    private String numeroInicial;
    private String nomCtaIn;
    private String nomCtaFin;
    private boolean verTitulo;
    private boolean verNroInicial;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LislibrodiariosControlador
     */
    public LislibrodiariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = "CODIGO";
        cLibro = "LIBRO DIARIO OFICIAL";
        cPrTitulo = "PR_TITULO";
        cAnio = "anoTrabajo";
        cCompania = "compania";
        cCuentaFinal = "cuentaFinal";
        cCuentaInicial = "cuentaInicial";
        cDigitos = "digitos";
        cMes = "mesTrabajo";
        try {
            numFormulario = GeneralCodigoFormaEnum.LISLIBRODIARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LislibrodiariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        anoTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
        mesTrabajo = String.valueOf(SysmanFunciones.mes(new Date()));
        digitos = "6";
        opAgrupar = "1";
        opClaseImpresion = "1";
        opTitulo = "1";
        cargarListaAnoTrabajo();
        cargarListaMesTrabajo();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            // valida el valor del parametro FORMATO ESPECIAL
            // IMPRESION, si el valor es Si se visualiza en el
            // formulario la etiqueta y cuadro de texto para ingresar
            // el numero inicial
            String formatoEspImpresion = ejbSysmanUtil.consultarParametro(
                            compania, "FORMATO ESPECIAL IMPRESION", modulo,
                            new Date(), true);

            if ("SI".equalsIgnoreCase(formatoEspImpresion)) {
                verNroInicial = true;
            }
            else {
                verNroInicial = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(LislibrodiariosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LislibrodiariosControladorUrlEnum.URL4932
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
                                                            LislibrodiariosControladorUrlEnum.URL5293
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
                                        LislibrodiariosControladorUrlEnum.URL5929
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
                                        LislibrodiariosControladorUrlEnum.URL7222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
        param.put(LislibrodiariosControladorEnum.PARAM0.getValue(),
                        String.valueOf(cuentaInicial));

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Define los valores de reemplazo y parametros a enviar al
     * reporte cuando se quiere generar agrupado por comprobante
     * 
     * @return Coleccion con los parametros necesarios para generar el
     * reporte 000719LisLibroDiario
     */
    private Map<String, Object> generarInformePorComprobante() {
        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(cCompania, compania);
        reemplazar.put(cAnio, anoTrabajo);
        reemplazar.put(cMes, mesTrabajo);
        reemplazar.put(cDigitos, digitos);
        reemplazar.put(cCuentaInicial, cuentaInicial);
        reemplazar.put(cCuentaFinal, cuentaFinal);
        parametros.put("PR_NOMCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_NITCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNit());
        if ("1".equals(opTitulo)) {
            parametros.put(cPrTitulo,
                            idioma.getString("TB_TB1763"));
        }
        else {
            parametros.put(cPrTitulo,
                            "Libro Diario del " + mesTrabajo);
        }

        if ((codigoLibro != null) && !"".equals(codigoLibro)
            && (codigoLibro.length() > 1)) {
            parametros.put("PR_CODLIBRO",
                            idioma.getString("TB_TB1764") + " " + codigoLibro);
        }
        else {
            parametros.put("PR_CODLIBRO", "");
        }
        parametros.put("PR_TITULOREPORTE",
                        "" + (SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mesTrabajo)]).toUpperCase()
                            + " DE " + anoTrabajo);
        Reporteador.resuelveConsulta("000719LisLibroDiario",
                        Integer.parseInt(modulo), reemplazar,
                        parametros);
        return parametros;
    }

    /**
     * Define los valores de reemplazo y parametros a enviar al
     * reporte cuando se quiere generar agrupado por fecha
     * 
     * @return Coleccion con los parametros necesarios para generar el
     * reporte 000725LisLibroDiarioCuentas
     */
    private Map<String, Object> generarInformePorFecha() {
        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();

        reemplazar.put(cCompania, compania);
        reemplazar.put(cAnio, anoTrabajo);
        reemplazar.put(cMes, mesTrabajo);
        reemplazar.put(cDigitos, digitos);
        reemplazar.put(cCuentaInicial, cuentaInicial);
        reemplazar.put(cCuentaFinal, cuentaFinal);
        parametros.put("PR_TITULOREPORTE",
                        (SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mesTrabajo)])
                                                        .toUpperCase()
                            + " DE " + anoTrabajo);
        if ("2".equals(opClaseImpresion) && "1".equals(opTitulo)) {
            parametros.put(cPrTitulo, cLibro);

        }

        if ("2".equals(opClaseImpresion) && "2".equals(opTitulo)) {
            parametros.put(cPrTitulo, cLibro);

        }
        Reporteador.resuelveConsulta("000725LisLibroDiarioCuentas",
                        Integer.parseInt(modulo), reemplazar,
                        parametros);
        return parametros;
    }

    /**
     * Define los valores de reemplazo y parametros a enviar al
     * reporte cuando se quiere generar agrupado por cuenta
     * 
     * @return Coleccion con los parametros necesarios para generar el
     * reporte 000728LisLibroDiarioFecha
     */
    private Map<String, Object> generarInformePorCuenta() {
        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(cCompania, compania);
        reemplazar.put(cAnio, anoTrabajo);
        reemplazar.put(cMes, mesTrabajo);
        reemplazar.put(cDigitos, digitos);
        reemplazar.put(cCuentaInicial, cuentaInicial);
        reemplazar.put(cCuentaFinal, cuentaFinal);
        parametros.put("PR_TITULO1",
                        (SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mesTrabajo)]
                            + " DE " + anoTrabajo)
                                            .toString().toUpperCase());

        if ("2".equals(opClaseImpresion) && "1".equals(opTitulo)) {
            parametros.put(cPrTitulo, cLibro);

        }

        if ("2".equals(opClaseImpresion) && "2".equals(opTitulo)) {
            parametros.put(cPrTitulo, "LIBRO DIARIO DEL MES");

        }

        Reporteador.resuelveConsulta("000728LisLibroDiarioFecha",
                        Integer.parseInt(modulo), reemplazar,
                        parametros);
        return parametros;
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            if (SysmanFunciones.validarVariableVacio(opAgrupar)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB513"));
                return;
            }
            if ("1".equals(opAgrupar)) {
                Map<String, Object> parametros = new HashMap<>();
                parametros = generarInformePorComprobante();
                archivoDescarga = JsfUtil.exportarStreamed(
                                "000719LisLibroDiario", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            if ("2".equals(opAgrupar)) {
                Map<String, Object> parametros = new HashMap<>();
                parametros = generarInformePorFecha();
                archivoDescarga = JsfUtil.exportarStreamed(
                                "000725LisLibroDiarioCuentas", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }

            if ("3".equals(opAgrupar)) {
                Map<String, Object> parametros = new HashMap<>();
                parametros = generarInformePorCuenta();

                archivoDescarga = JsfUtil.exportarStreamed(
                                "000728LisLibroDiarioFecha", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        mesTrabajo = null;
        cuentaInicial = null;
        nomCtaIn = null;
        cuentaFinal = null;
        nomCtaFin = null;
        cargarListaMesTrabajo();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarClaseImpresion() {
        // <CODIGO_DESARROLLADO>
        if ("2".equals(opClaseImpresion)) {
            verTitulo = true;
        }
        else {
            verTitulo = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        nomCtaIn = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        cuentaFinal = null;
        nomCtaFin = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nomCtaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
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

    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    public String getMesTrabajo() {
        return mesTrabajo;
    }

    public void setMesTrabajo(String mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public String getCodigoLibro() {
        return codigoLibro;
    }

    public void setCodigoLibro(String codigoLibro) {
        this.codigoLibro = codigoLibro;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNomCtaIn() {
        return nomCtaIn;
    }

    public void setNomCtaIn(String nomCtaIn) {
        this.nomCtaIn = nomCtaIn;
    }

    public String getNomCtaFin() {
        return nomCtaFin;
    }

    public void setNomCtaFin(String nomCtaFin) {
        this.nomCtaFin = nomCtaFin;
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

    public String getOpClaseImpresion() {
        return opClaseImpresion;
    }

    public void setOpClaseImpresion(String opClaseImpresion) {
        this.opClaseImpresion = opClaseImpresion;
    }

    public String getOpAgrupar() {
        return opAgrupar;
    }

    public void setOpAgrupar(String opAgrupar) {
        this.opAgrupar = opAgrupar;
    }

    public String getOpTitulo() {
        return opTitulo;
    }

    public void setOpTitulo(String opTitulo) {
        this.opTitulo = opTitulo;
    }

    public boolean isVerTitulo() {
        return verTitulo;
    }

    public void setVerTitulo(boolean verTitulo) {
        this.verTitulo = verTitulo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isVerNroInicial() {
        return verNroInicial;
    }

    public void setVerNroInicial(boolean verNroInicial) {
        this.verNroInicial = verNroInicial;
    }

}
