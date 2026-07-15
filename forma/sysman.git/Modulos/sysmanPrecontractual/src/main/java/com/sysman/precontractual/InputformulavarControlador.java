package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.InputformulavarControladorEnum;
import com.sysman.precontractual.enums.InputformulavarControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

/**
 *
 * @author dcastro
 * @version 1, 04/12/2015
 * @modified jguerrero
 * @version 2. 30/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class InputformulavarControlador extends BeanBaseModal {

    private final String compania;

    private String lstVariables;
    private String lstFunciones;
    private String modelo;
    private List<Registro> listaLstVariables;

    /**
     * Creates a new instance of InputformulavarControlador
     */
    public InputformulavarControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.INPUTFORMULAVAR_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InputformulavarControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        cargarListaLstVariables();
        abrirFormulario();
    }

    public void cargarListaLstVariables() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaLstVariables = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InputformulavarControladorUrlEnum.URL1898
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimircmdAceptar(ActionEvent ac) {
        RequestContext.getCurrentInstance().closeDialog(modelo);

        if (lstFunciones.equals(InputformulavarControladorEnum.VALOR_EN_LETRAS
                        .getValue())) {
            modelo = modelo.substring(0, modelo.length() - 1);
            modelo = SysmanFunciones.concatenar(modelo, ",1)");
        }
        SessionUtil.setSessionVar("variable", modelo);
    }

    public void cambiarModelo() {
        // Metodo heredado del bean base

    }

    public void ejecutarmodelo() {
        // Metodo heredado del bean base
    }

    public void cambiarLstVariables() {

        modelo = SysmanFunciones.concatenar("<<", lstVariables, ">>", modelo);

    }

    public void cambiarLstFunciones() {
        lstVariables = null;
        modelo = lstFunciones;
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2206"));
    }

    public String getLstVariables() {
        return lstVariables;
    }

    public void setLstVariables(String lstVariables) {
        this.lstVariables = lstVariables;
    }

    public String getLstFunciones() {
        return lstFunciones;
    }

    public void setLstFunciones(String lstFunciones) {
        this.lstFunciones = lstFunciones;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<Registro> getListaLstVariables() {
        return listaLstVariables;
    }

    public void setListaLstVariables(List<Registro> listaLstVariables) {
        this.listaLstVariables = listaLstVariables;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

}
