package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RevelacionesControladorUrlEnum;
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
import com.sysman.persistencia.Acciones;

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
 * @author ngomez
 * @version 1, 09/03/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 11/04/2017
 */
@ManagedBean
@ViewScoped

public class RevelacionesControlador extends BeanBaseModal {

    private final String compania;
    private String revelaciones;
    private String anio;
    private String tipoCpte;
    private String comprobante;
    private String consecutivo;
    private String condicion;

    /**
     * Creates a new instance of RevelacionesControlador
     */
    public RevelacionesControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.REVELACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RevelacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            anio = parametrosEntrada.get("anio").toString();
            tipoCpte = parametrosEntrada.get("tipoComp").toString();
            comprobante = parametrosEntrada.get("numeroComp").toString();
            consecutivo = parametrosEntrada.get("consecutivo").toString();
        }
        SessionUtil.cleanFlash();

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
    }

    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar() {
        String clob = Acciones.getClobConcatenado(revelaciones);

        try {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RevelacionesControladorUrlEnum.URL2945
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put("REVELACIONES", clob.substring(0, clob.length() - 1));
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.ANO.getName(),
                            anio);
            fields.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                            tipoCpte);
            fields.put(GeneralParameterEnum.COMPROBANTE.getName(),
                            comprobante);
            fields.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            int aux = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), parameter);
            if (aux == 1) {
                SessionUtil.setSessionVar("mensajeRev",
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
                RequestContext.getCurrentInstance().closeDialog(null);
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(RevelacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public String getRevelaciones() {
        return revelaciones;
    }

    public void setRevelaciones(String revelaciones) {
        this.revelaciones = revelaciones;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getTipoCpte() {
        return tipoCpte;
    }

    public void setTipoCpte(String tipoCpte) {
        this.tipoCpte = tipoCpte;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                        tipoCpte);
        param.put(GeneralParameterEnum.COMPROBANTE.getName(),
                        comprobante);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

        Registro regAux;
        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevelacionesControladorUrlEnum.URL177
                                                                            .getValue())
                                            .getUrl(), param));
            revelaciones = (String) regAux.getCampos().get("REVELACIONES");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

}
