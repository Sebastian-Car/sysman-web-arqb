package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.planeacion.ejb.EjbPlaneacionCeroRemote;
import com.sysman.planeacion.enums.ActualizarpcControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author vmolano
 * @version 1, 22/03/2016
 * @modified jguerrero
 * @version 2. 07/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class ActualizarpcControlador extends BeanBaseModal {

    private final String compania;
    private String anoSeleccionado;
    private List<Registro> listaAno;

    @EJB
    private EjbPlaneacionCeroRemote ejbPlaneacion;

    /**
     * Creates a new instance of ActualizarpcControlador
     */
    public ActualizarpcControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZARPC_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ActualizarpcControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        abrirFormulario();
        cargarListaAno();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getAnoSeleccionado() {
        return anoSeleccionado;
    }

    public void setAnoSeleccionado(String anoSeleccionado) {
        this.anoSeleccionado = anoSeleccionado;
    }

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizarpcControladorUrlEnum.URL2576
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirAceptar() {
        boolean rta;

        try {

            rta = ejbPlaneacion.actualizarPlanCompras(compania,
                            Integer.parseInt(anoSeleccionado),
                            SessionUtil.getUser().getCodigo());

            if (rta) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }

        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(ActualizarpcControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void oprimirCancelar() {

        // Metodo heredado del beanBase
    }
}
