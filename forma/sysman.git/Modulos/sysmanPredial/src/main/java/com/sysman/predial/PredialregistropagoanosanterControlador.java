package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.PredialregistropagoanosanterControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
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

/**
 *
 * @author jlozano
 * @version 1, 16/08/2016 13:00:24 -- Modificado por jlozano
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author eamaya
 * @version 3.0, Proceso de Refactoring DSS, Manejo de EJBs,
 * Correcciones SonarLint y cambio de Sysdate por new Date()
 * 
 */
@ManagedBean
@ViewScoped

public class PredialregistropagoanosanterControlador extends BeanBaseModal {
    private final String compania;
    private final String nOrden;
    /**
     * Constante que almacena el nombre de la columna TRPCOD
     */
    private final String ctrpcod;
    // <DECLARAR_ATRIBUTOS>
    private String damnificados;
    private String cincuentaPor;
    private String codBanco;
    private String codPredio;
    private String anioFin;
    private String tarifaAp;
    private Date fechaCorte;
    private String nombrePredio;
    private String nombreBanco;
    private String nroRecibo;
    private String totalPagado;
    private String observaciones;
    private String trpCod;

    private boolean damniVisible;
    private boolean visible50;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnofin;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigobanco;
    private RegistroDataModelImpl listaCodPredio;
    private RegistroDataModelImpl listaTarifaAp;

    @EJB
    private EjbPredialDosRemote ejbPredialDos;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of
     * PredialregistropagoanosanterControlador
     */
    public PredialregistropagoanosanterControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        ctrpcod = "TRPCOD";

        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALREGISTROPAGOANOSANTER_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PredialregistropagoanosanterControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAnofin();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigobanco();
        cargarListaCodPredio();
        cargarListaTarifaAp();
        // </CARGAR_LISTA_COMBO_GRANDE>
        fechaCorte = new Date();
        cincuentaPor = "false";
        damnificados = "false";
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if ("ALCALDIA DE MELGAR".equalsIgnoreCase(
                        SessionUtil.getCompaniaIngreso().getNombre())) {
            visible50 = true;
            damniVisible = true;
        }
        else {
            visible50 = false;
            damniVisible = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAnofin() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnofin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialregistropagoanosanterControladorUrlEnum.URL4450
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigobanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialregistropagoanosanterControladorUrlEnum.URL4832
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCodigobanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOBANCO");
    }

    public void cargarListaCodPredio() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialregistropagoanosanterControladorUrlEnum.URL5356
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        nOrden);

        listaCodPredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTarifaAp() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialregistropagoanosanterControladorUrlEnum.URL6306
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(GeneralParameterEnum.ANO.getName(),
                        anioFin);

        listaTarifaAp = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ctrpcod);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirRegistrar() {
        // <CODIGO_DESARROLLADO>

        if (!validarCampos()) {
            return;
        }

        try {

            ejbPredialDos.registroDePagoVigenciaAnterior(
                            compania,
                            cincuentaPor,
                            damnificados,
                            SysmanFunciones.convertirAFechaCadena(fechaCorte),
                            codPredio,
                            codBanco,
                            Integer.parseInt(anioFin),
                            nroRecibo,
                            new BigDecimal(totalPagado),
                            tarifaAp,
                            trpCod,
                            observaciones,
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (ParseException | NumberFormatException | SystemException e) {
            Logger.getLogger(PredialregistropagoanosanterControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarCampos() {

        if (!validarCamposVacios()) {
            return false;
        }

        else if (trpCod == null
            || "".equals(SysmanFunciones.nvlStr(trpCod.trim(), ""))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1247"));
            return false;

        }
        return true;
    }

    private boolean validarCamposVacios() {
        if (" ".equals(SysmanFunciones.nvl(fechaCorte, " "))
            || " ".equals(SysmanFunciones.nvl(codPredio.trim(), " "))
            || " ".equals(SysmanFunciones.nvl(codBanco.trim(), " "))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1246"));
            return false;

        }
        if (" ".equals(SysmanFunciones.nvl(anioFin, " "))
            || " ".equals(SysmanFunciones.nvl(nroRecibo, " "))
            || " ".equals(SysmanFunciones.nvl(totalPagado, " "))
            || " ".equals(SysmanFunciones.nvl(tarifaAp, " "))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1246"));
            return false;

        }

        return true;
    }

    public void oprimirNuevoReg() {
        // <CODIGO_DESARROLLADO>
        nroRecibo = null;
        fechaCorte = new Date();
        codBanco = null;
        nombreBanco = null;
        codPredio = null;
        nombrePredio = null;
        anioFin = null;
        tarifaAp = null;
        totalPagado = null;
        observaciones = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAnofin() {
        // <CODIGO_DESARROLLADO>
        tarifaAp = null;
        cargarListaTarifaAp();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaCorte
     * 
     * 
     */
    public void cambiarFechaCorte() {
        // <CODIGO_DESARROLLADO>
        if (fechaCorte.after(new Date())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2892"));
            fechaCorte = new Date();
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigobanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codBanco = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOBANCO"), "")
                        .toString();
        nombreBanco = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBREBANCO"), "")
                        .toString();
    }

    public void seleccionarFilaCodPredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        if (registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                        .toString().length() > 15) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB200"));
            return;
        }
        else {
            codPredio = SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName()),
                                            "")
                            .toString();
            nombrePredio = SysmanFunciones
                            .nvl(registroAux.getCampos().get("NOMBRE"), "")
                            .toString();
            trpCod = SysmanFunciones
                            .nvl(registroAux.getCampos().get(ctrpcod), "")
                            .toString();
        }
    }

    public void seleccionarFilaTarifaAp(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tarifaAp = SysmanFunciones.nvl(registroAux.getCampos().get(ctrpcod), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getDamnificados() {
        return damnificados;
    }

    public void setDamnificados(String damnificados) {
        this.damnificados = damnificados;
    }

    public String getCincuentaPor() {
        return cincuentaPor;
    }

    public void setCincuentaPor(String cincuentaPor) {
        this.cincuentaPor = cincuentaPor;
    }

    public String getCodBanco() {
        return codBanco;
    }

    public void setCodBanco(String codBanco) {
        this.codBanco = codBanco;
    }

    public String getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(String codPredio) {
        this.codPredio = codPredio;
    }

    public String getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(String anioFin) {
        this.anioFin = anioFin;
    }

    public String getTarifaAp() {
        return tarifaAp;
    }

    public void setTarifaAp(String tarifaAp) {
        this.tarifaAp = tarifaAp;
    }

    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public String getNombrePredio() {
        return nombrePredio;
    }

    public void setNombrePredio(String nombrePredio) {
        this.nombrePredio = nombrePredio;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getNroRecibo() {
        return nroRecibo;
    }

    public void setNroRecibo(String nroRecibo) {
        this.nroRecibo = nroRecibo;
    }

    public String getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(String totalPagado) {
        this.totalPagado = totalPagado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isDamniVisible() {
        return damniVisible;
    }

    public void setDamniVisible(boolean damniVisible) {
        this.damniVisible = damniVisible;
    }

    public boolean isVisible50() {
        return visible50;
    }

    public void setVisible50(boolean visible50) {
        this.visible50 = visible50;
    }

    public String getTrpCod() {
        return trpCod;
    }

    public void setTrpCod(String trpCod) {
        this.trpCod = trpCod;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAnofin() {
        return listaAnofin;
    }

    public void setListaAnofin(List<Registro> listaAnofin) {
        this.listaAnofin = listaAnofin;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigobanco() {
        return listaCodigobanco;
    }

    public void setListaCodigobanco(RegistroDataModelImpl listaCodigobanco) {
        this.listaCodigobanco = listaCodigobanco;
    }

    public RegistroDataModelImpl getListaCodPredio() {
        return listaCodPredio;
    }

    public void setListaCodPredio(RegistroDataModelImpl listaCodPredio) {
        this.listaCodPredio = listaCodPredio;
    }

    public RegistroDataModelImpl getListaTarifaAp() {
        return listaTarifaAp;
    }

    public void setListaTarifaAp(RegistroDataModelImpl listaTarifaAp) {
        this.listaTarifaAp = listaTarifaAp;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
