package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmactualizarfechaslecturasControladorEnum;
import com.sysman.serviciospublicos.enums.FrmactualizarfechaslecturasControladorUrlEnum;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jguerrero
 * @version 1, 24/08/2016
 *
 * -- Modificado por lcortes 24/05/2017. Refactorizacion de codigo de
 * las listas para utilizar dss. Reemplazo de llamados a la clase
 * Acciones.
 */
@ManagedBean
@ViewScoped
public class FrmactualizarfechaslecturasControlador extends BeanBaseModal {
    private final String compania;
    private final String strCodigoRuta;
    private String codigoInicial;
    private String codigoFinal;
    private String ciclo;
    private String periodo;
    private Date fechaAnterior;
    private Date fechaActual;  
    private RegistroDataModelImpl listaMiCodigoInicial;
    private RegistroDataModelImpl listaMiCodigoFinal;
    private RegistroDataModelImpl listaCmbCiclo;
    /**
     * Creates a new instance of
     * FrmactualizarfechaslecturasControlador
     */
    public FrmactualizarfechaslecturasControlador() {
        super();
        compania = SessionUtil.getCompania();

        strCodigoRuta = "CODIGORUTA";

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMACTUALIZARFECHASLECTURAS_CONTROLADOR.getCodigo();
            validarPermisos();  
        }
        catch (Exception ex) {
            Logger.getLogger(FrmactualizarfechaslecturasControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaCmbCiclo(); 
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMiCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactualizarfechaslecturasControladorUrlEnum.URL2977
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

        listaMiCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigoRuta);
    }

    public void cargarListaMiCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactualizarfechaslecturasControladorUrlEnum.URL3716
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        param.put(FrmactualizarfechaslecturasControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaMiCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigoRuta);
    }

    public void cargarListaCmbCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactualizarfechaslecturasControladorUrlEnum.URL4494
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdNuevo() {
        // <CODIGO_DESARROLLADO>
        try {

            if (fechaAnterior.after(fechaActual)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1331"));
                fechaAnterior = null;
                fechaActual = null;
            }
            else {

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmactualizarfechaslecturasControladorUrlEnum.URL5876
                                                .getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(FrmactualizarfechaslecturasControladorEnum.PARAM1
                                .getValue(), fechaAnterior);
                fields.put(FrmactualizarfechaslecturasControladorEnum.PARAM2
                                .getValue(), fechaActual);
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                fields.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                fields.put(GeneralParameterEnum.PERIODO.getName(), periodo);
                fields.put(FrmactualizarfechaslecturasControladorEnum.PARAM3
                                .getValue(), codigoInicial);
                fields.put(FrmactualizarfechaslecturasControladorEnum.PARAM4
                                .getValue(), codigoFinal);
                Parameter parameter = new Parameter();
                parameter.setFields(fields);
                int actualizados = requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);
                if (actualizados > 0) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1332")
                                    + " " + actualizados + " "
                                    + idioma.getString("TB_TB1333"));
                    ciclo = null;
                    codigoInicial = null;
                    codigoFinal = null;
                    fechaAnterior = null;
                    fechaActual = null;
                    listaMiCodigoInicial = listaMiCodigoFinal = null;
                }
                else {
                    ciclo = null;
                    codigoInicial = null;
                    codigoFinal = null;
                    fechaAnterior = null;
                    fechaActual = null;
                    listaMiCodigoInicial = listaMiCodigoFinal = null;
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1334"));
                }

            }
        }
        catch (SystemException e) {
            Logger.getLogger(FrmactualizarfechaslecturasControlador.class
                            .getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void seleccionarFilaMiCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(strCodigoRuta).toString();
        codigoFinal = null;
        cargarListaMiCodigoFinal();
    }

    public void seleccionarFilaMiCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(strCodigoRuta).toString();
    }

    public void seleccionarFilaCmbCiclo(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        periodo = registroAux.getCampos().get("PERIODO").toString();
        codigoInicial = null;
        codigoFinal = null;

        cargarListaMiCodigoInicial();
        cargarListaMiCodigoFinal();

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
    public RegistroDataModelImpl getListaMiCodigoInicial() {
        return listaMiCodigoInicial;
    }

    public void setListaMiCodigoInicial(
        RegistroDataModelImpl listaMiCodigoInicial) {
        this.listaMiCodigoInicial = listaMiCodigoInicial;
    }

    public RegistroDataModelImpl getListaMiCodigoFinal() {
        return listaMiCodigoFinal;
    }

    public void setListaMiCodigoFinal(
        RegistroDataModelImpl listaMiCodigoFinal) {
        this.listaMiCodigoFinal = listaMiCodigoFinal;
    }

    public RegistroDataModelImpl getListaCmbCiclo() {
        return listaCmbCiclo;
    }

    public void setListaCmbCiclo(RegistroDataModelImpl listaCmbCiclo) {
        this.listaCmbCiclo = listaCmbCiclo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Date getFechaAnterior() {
        return fechaAnterior;
    }

    public void setFechaAnterior(Date fechaAnterior) {
        this.fechaAnterior = fechaAnterior;
    }

    public Date getFechaActual() {
        return fechaActual;
    }

    public void setFechaActual(Date fechaActual) {
        this.fechaActual = fechaActual;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
