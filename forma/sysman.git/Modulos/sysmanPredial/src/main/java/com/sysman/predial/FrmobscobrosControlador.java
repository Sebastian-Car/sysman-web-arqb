package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.FrmobscobrosControladorEnum;
import com.sysman.predial.enums.FrmobscobrosControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author acaceres
 * @version 1, 26/05/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambió el llamado del código del
 * formulario
 * 
 * @modifier amonroy
 * @version 3, 05/07/2017 Se realiza el Proceso de Refactoring a la
 * actualizacion y consulta de registro que se encontraban definidas
 * en el Controlador
 */
@ManagedBean
@ViewScoped
public class FrmobscobrosControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String procesoDeCobro;
    private String txtObsNueva;
    private String codigo;
    private String txtObservacion;
    private String numeroOrden;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmobscobrosControlador
     */
    public FrmobscobrosControlador() {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();

        if (parametros != null) {
            codigo = parametros.get("codigo").toString();
        }
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMOBSCOBROS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmobscobrosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    public void cargarObservacion() {

        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.CODIGO.getName(), codigo);
            params.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            numeroOrden);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmobscobrosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));

            if ((reg != null)
                && (reg.getCampos()
                                .get("OBSERVACION_COBRO_COACTIVO") != null)) {
                txtObservacion = reg.getCampos()
                                .get("OBSERVACION_COBRO_COACTIVO")
                                .toString();
            }
            else {
                txtObservacion = "";
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        procesoDeCobro = SysmanFunciones
                        .concatenar(idioma.getString("TB_TB721"), " ", codigo);
        txtObsNueva = "";
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarObservacion();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirBtnAceptar() {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarVariableVacio(txtObsNueva)) {

            try {

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmobscobrosControladorUrlEnum.URL4758
                                                                .getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(FrmobscobrosControladorEnum.OBSERVACION_COBRO_COACTIVO
                                .getValue(), SysmanFunciones
                                                .concatenar(txtObservacion, " ",
                                                                txtObsNueva));
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                fields.put(FrmobscobrosControladorEnum.KEY_COMPANIA.getValue(),
                                compania);
                fields.put(FrmobscobrosControladorEnum.KEY_CODIGO.getValue(),
                                codigo);
                fields.put(FrmobscobrosControladorEnum.KEY_NUMERO_ORDEN
                                .getValue(), numeroOrden);
                Parameter parameter = new Parameter();
                parameter.setFields(fields);

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parameter);

                txtObsNueva = null;
                cargarObservacion();

            }
            catch (SystemException e) {
                Logger.getLogger(FrmobscobrosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdSalir() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTxtObservacion() {
        return txtObservacion;
    }

    public void setTxtObservacion(String txtObservacion) {
        this.txtObservacion = txtObservacion;
    }

    public String getProcesoDeCobro() {
        return procesoDeCobro;
    }

    public void setProcesoDeCobro(String procesoDeCobro) {
        this.procesoDeCobro = procesoDeCobro;
    }

    public String getTxtObsNueva() {
        return txtObsNueva;
    }

    public void setTxtObsNueva(String txtObsNueva) {
        this.txtObsNueva = txtObsNueva;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
