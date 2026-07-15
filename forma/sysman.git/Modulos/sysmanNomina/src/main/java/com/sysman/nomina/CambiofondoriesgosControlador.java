package com.sysman.nomina;

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
import com.sysman.nomina.enums.CambiofondoriesgosControladorEnum;
import com.sysman.nomina.enums.CambiofondoriesgosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author jrodriguezr
 * @version 1, 01/04/2016
 * 
 * @author eamaya
 * @version 2.0, 22/08/2017, Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class CambiofondoriesgosControlador extends BeanBaseModal {

    private final String compania;
    private String fondoRiesgo;
    private Date fechaCambio;
    private List<Registro> listaCmbFondoR;

    /**
     * Creates a new instance of CambiofondoriesgosControlador
     */
    public CambiofondoriesgosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIOFONDORIESGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CambiofondoriesgosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaCmbFondoR();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaCmbFondoR() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.CLASE.getName(), "ARL");

        try {
            listaCmbFondoR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiofondoriesgosControladorUrlEnum.URL2205
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimircmdActualizar() {
        // <CODIGO_DESARROLLADO>

        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(CambiofondoriesgosControladorEnum.FONDO_RIESGOS
                            .getValue(), fondoRiesgo);

            param.put(CambiofondoriesgosControladorEnum.FECHAFONDORIESGOS
                            .getValue(), fechaCambio);

            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

            Parameter parameter = new Parameter();

            parameter.setFields(param);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CambiofondoriesgosControladorUrlEnum.URL2951
                                                            .getValue());
            String a = Integer
                            .toString(requestManager.update(urlUpdate.getUrl(),
                                            urlUpdate.getMetodo(),
                                            parameter));

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2520")
                            .replace("#$rta$#", a));
        }
        catch (SystemException ex) {
            Logger.getLogger(CambiofondoriesgosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            ex.getMessage()));
        }
        // </CODIGO_DESARROLLADO>
    }

    public String getFondoRiesgo() {
        return fondoRiesgo;
    }

    public void setFondoRiesgo(String fondoRiesgo) {
        this.fondoRiesgo = fondoRiesgo;
    }

    public Date getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Date fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public List<Registro> getListaCmbFondoR() {
        return listaCmbFondoR;
    }

    public void setListaCmbFondoR(List<Registro> listaCmbFondoR) {
        this.listaCmbFondoR = listaCmbFondoR;
    }
}
