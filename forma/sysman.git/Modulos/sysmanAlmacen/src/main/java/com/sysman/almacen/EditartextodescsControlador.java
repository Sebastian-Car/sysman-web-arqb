package com.sysman.almacen;

import com.sysman.almacen.enums.EditartextodescsControladorUrlEnum;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author NGOMEZ
 * @version 1, 04/02/2016
 *
 * @version 2, 27/04/2017, pespitia:<br>
 * Se aplico Refactoring.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class EditartextodescsControlador extends BeanBaseModal {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * esta interactuando con el formulario.
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.ESPECIFICACION</code>
     */
    private final String cEspecificacion;

    private String nuevaDescripcion;
    private String serie;
    private String tipoMovp;
    private String movp;

    /**
     * Creates a new instance of EditartextodescsControlador
     */
    public EditartextodescsControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.EDITARTEXTODESCS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();

        cEspecificacion = GeneralParameterEnum.ESPECIFICACION.getName();

        try {
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                tipoMovp = (String) parametrosEntrada.get("tipoMovp");
                movp = (String) parametrosEntrada.get("movp");
            }
            SessionUtil.cleanFlash();
        }
        catch (Exception ex) {
            Logger.getLogger(EditartextodescsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();

        }
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
        // <CODIGO_DESARROLLADO>
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMovp);
        param.put(GeneralParameterEnum.MOVIMIENTO.getName(), movp);
        param.put(GeneralParameterEnum.SERIE.getName(), serie);

        Registro reg = null;

        try {
            reg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EditartextodescsControladorUrlEnum.URL4970
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1904"));
        }
        else {
            HashMap<String, Object> parSet = new HashMap<>();
            parSet.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parSet.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMovp);
            parSet.put(GeneralParameterEnum.MOVIMIENTO.getName(), movp);
            parSet.put(GeneralParameterEnum.SERIE.getName(), serie);
            parSet.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
            parSet.put(cEspecificacion, nuevaDescripcion);
            parSet.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            Parameter parametro = new Parameter();
            parametro.setFields(parSet);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EditartextodescsControladorUrlEnum.URL3983
                                                            .getValue());

            try {
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parametro);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2015"));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2019"));
            }

        }
        // </CODIGO_DESARROLLADO>
    }

    public String getNuevaDescripcion() {
        return nuevaDescripcion;
    }

    public void setNuevaDescripcion(String nuevaDescripcion) {
        this.nuevaDescripcion = nuevaDescripcion;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getTipoMovp() {
        return tipoMovp;
    }

    public void setTipoMovp(String tipoMovp) {
        this.tipoMovp = tipoMovp;
    }

    public String getMovp() {
        return movp;
    }

    public void setMovp(String movp) {
        this.movp = movp;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCerrar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }
}
