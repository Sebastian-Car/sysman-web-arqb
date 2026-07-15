package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelacionGeneralGastosControladorEnum;
import com.sysman.contabilidad.enums.RelacionGeneralGastosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 24/05/2016 17:38:13 -- Modificado por jrodriguezr
 * 
 * @version 2, 10/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico Refactoring.
 * 
 */
@ManagedBean
@ViewScoped
public class RelacionGeneralGastosControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * {@code RelacionGeneralGastosControladorEnum.ANIOINI}
     */
    private final String cAnioIni;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * {@code RelacionGeneralGastosControladorEnum.ANIOFIN }
     */
    private final String cAnioFin;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * {@code RelacionGeneralGastosControladorEnum.NIT }
     */
    private final String cNit;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * {@code RelacionGeneralGastosControladorEnum.NOMBRE }
     */
    private final String cNombre;

    // <DECLARAR_ATRIBUTOS>
    private String terceroInicial;
    private String terceroFinal;
    private String anioInicial;
    private String anioFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private String terceroInicialNom;
    private String terceroFinalNom;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private String cuentaInicialNom;
    private String cuentaFinalNom;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaterceroinicial;
    private RegistroDataModelImpl listatercerofinal;
    private RegistroDataModelImpl listaCmbCuenta;
    private RegistroDataModelImpl listaCmbCuentF;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RelacionGeneralGastosControlador
     */
    public RelacionGeneralGastosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cAnioIni = RelacionGeneralGastosControladorEnum.ANIOINI.getValue();
        cAnioFin = RelacionGeneralGastosControladorEnum.ANIOFIN.getValue();
        cNit = RelacionGeneralGastosControladorEnum.NIT.getValue();
        cNombre = RelacionGeneralGastosControladorEnum.NOMBRE.getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACION_GENERAL_GASTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaInicial = fechaFinal = new Date();
        anioInicial = anioFinal = String
                        .valueOf(SysmanFunciones.ano(new Date()));
        cargarListaterceroinicial();
        cargarListatercerofinal();
        cargarListaCmbCuenta();
        cargarListaCmbCuentF();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaterceroinicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionGeneralGastosControladorUrlEnum.URL3980
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaterceroinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, cNit);
    }

    public void cargarListatercerofinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionGeneralGastosControladorUrlEnum.URL4639
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RelacionGeneralGastosControladorEnum.TECEROINICIAL.getValue(),
                        terceroInicial);
        listatercerofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, cNit);
    }

    public void cargarListaCmbCuenta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionGeneralGastosControladorUrlEnum.URL5345
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cAnioIni, anioInicial);
        param.put(cAnioFin, anioFinal);
        listaCmbCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCmbCuentF() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionGeneralGastosControladorUrlEnum.URL6490
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cAnioIni, anioInicial);
        param.put(cAnioFin, anioFinal);
        param.put(RelacionGeneralGastosControladorEnum.CODIGOINI.getValue(),
                        cuentaInicial);
        listaCmbCuentF = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Valida las fechas
     * 
     * @return
     */
    private boolean validarFechas() {
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB191"));
            return true;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB192"));
            return true;
        }
        return false;
    }

    /**
     * Verifica que el valor de cada uno de los campos ingresados no
     * sea nulo o este vacio.
     * 
     * @return true si alguno de los campos de ingreso tiene valor
     * nulo o vacio.
     */
    private boolean validarEntradasReporte() {
        if (SysmanFunciones.validarVariableVacio(terceroInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB193"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(terceroFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB194"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB195"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB196"));
            return true;
        }
        return false;
    }

    private void generaReporte(FORMATOS formato) {
        if (validarEntradasReporte() || validarFechas()) {
            return;
        }
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = "000813INFRELGRALGASTOSDC";
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), "<br>",
                            e.getMessage()), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = cuentaFinal = cuentaInicialNom = cuentaFinalNom = null;
        anioInicial = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaCmbCuenta();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = cuentaFinal = cuentaInicialNom = cuentaFinalNom = null;
        anioFinal = String.valueOf(SysmanFunciones.ano(fechaFinal));
        cargarListaCmbCuenta();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaterceroinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = asignarValorCampo(registroAux, cNit);
        terceroInicialNom = asignarValorCampo(registroAux, cNombre);
        terceroFinal = terceroFinalNom = null;
        cargarListatercerofinal();
    }

    public void seleccionarFilatercerofinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = asignarValorCampo(registroAux, cNit);
        terceroFinalNom = asignarValorCampo(registroAux, cNombre);
    }

    public void seleccionarFilaCmbCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = asignarValorCampo(registroAux, "CODIGO");
        cuentaInicialNom = asignarValorCampo(registroAux, cNombre);
        cuentaFinal = cuentaFinalNom = null;
        cargarListaCmbCuentF();
    }

    public void seleccionarFilaCmbCuentF(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = asignarValorCampo(registroAux, "CODIGO");
        cuentaFinalNom = asignarValorCampo(registroAux, cNombre);
    }

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

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
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

    public String getTerceroInicialNom() {
        return terceroInicialNom;
    }

    public void setTerceroInicialNom(String terceroInicialNom) {
        this.terceroInicialNom = terceroInicialNom;
    }

    public String getTerceroFinalNom() {
        return terceroFinalNom;
    }

    public void setTerceroFinalNom(String terceroFinalNom) {
        this.terceroFinalNom = terceroFinalNom;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getCuentaInicialNom() {
        return cuentaInicialNom;
    }

    public void setCuentaInicialNom(String cuentaInicialNom) {
        this.cuentaInicialNom = cuentaInicialNom;
    }

    public String getCuentaFinalNom() {
        return cuentaFinalNom;
    }

    public void setCuentaFinalNom(String cuentaFinalNom) {
        this.cuentaFinalNom = cuentaFinalNom;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaterceroinicial() {
        return listaterceroinicial;
    }

    public void setListaterceroinicial(
        RegistroDataModelImpl listaterceroinicial) {
        this.listaterceroinicial = listaterceroinicial;
    }

    public RegistroDataModelImpl getListatercerofinal() {
        return listatercerofinal;
    }

    public void setListatercerofinal(RegistroDataModelImpl listatercerofinal) {
        this.listatercerofinal = listatercerofinal;
    }

    public RegistroDataModelImpl getListaCmbCuenta() {
        return listaCmbCuenta;
    }

    public void setListaCmbCuenta(RegistroDataModelImpl listaCmbCuenta) {
        this.listaCmbCuenta = listaCmbCuenta;
    }

    public RegistroDataModelImpl getListaCmbCuentF() {
        return listaCmbCuentF;
    }

    public void setListaCmbCuentF(RegistroDataModelImpl listaCmbCuentF) {
        this.listaCmbCuentF = listaCmbCuentF;
    }

    public String getAnioInicial() {
        return anioInicial;
    }

    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    public String getAnioFinal() {
        return anioFinal;
    }

    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
