package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.RetencionMinimaControladorEnum;
import com.sysman.general.enums.RetencionMinimaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.Calendar;
import java.util.GregorianCalendar;
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

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author cmanrique
 * @version 1
 * @author yrojas
 * @version 2, 04/04/2017 Se cambiaron las consultas por la invocación
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 * 
 * @version 3.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class RetencionMinimaControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private List<Registro> listaAnoOrigen;
    private List<Registro> listaAnoDestino;

    private String anoOrigen;
    private String anoDestino;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of RetencionMinimaControlador
     */
    public RetencionMinimaControlador() {
        super();
        compania = SessionUtil.getCompania();

        // 6
        numFormulario = GeneralCodigoFormaEnum.RETENCION_MINIMA_CONTROLADOR
                        .getCodigo();

        try {
            validarPermisos();
            Calendar calendario = new GregorianCalendar();
            anoOrigen = String.valueOf(calendario.get(Calendar.YEAR));
            anoDestino = String.valueOf(calendario.get(Calendar.YEAR) + 1);
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.RETENCIONES_MINIMAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaAnoOrigen();
        cargarListaAnoDestino();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        // <CODIGO_DESARROLLADO>
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoOrigen() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RetencionMinimaControladorUrlEnum.URL2753
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoDestino() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RetencionMinimaControladorUrlEnum.URL3183
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirRetefuente() {
        // <CODIGO_DESARROLLADO>
        try {

            if (Integer.parseInt(anoDestino) < Integer.parseInt(anoOrigen)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1762"));
                anoDestino = null;
                return;

            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(RetencionMinimaControladorEnum.PARAM0.getValue(),
                            anoOrigen);
            parametros.put(RetencionMinimaControladorEnum.PARAM1.getValue(),
                            anoDestino);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RetencionMinimaControladorUrlEnum.URL4607
                                                            .getValue());
            Parameter parameter = new Parameter();

            parameter.setFields(parametros);
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);

            listaInicial.load(0);
            eliminarDespues();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(RetencionMinimaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            String criterio = "COMPANIA = ''" + compania + "''  AND ANO = "
                + registro.getCampos().get("ANO");

            long id = ejbSysmanUtil.generarSiguienteConsecutivo(
                            tabla,
                            criterio,
                            "ID");

            registro.getCampos().put("ID", id);
        }
        catch (SystemException ex) {
            Logger.getLogger(RetencionMinimaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
        }

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("TIPON_LB");
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        cargarListaAnoOrigen();
        cargarListaAnoDestino();
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public List<Registro> getListaAnoOrigen() {
        return listaAnoOrigen;
    }

    public void setListaAnoOrigen(List<Registro> listaAnoOrigen) {
        this.listaAnoOrigen = listaAnoOrigen;
    }

    public List<Registro> getListaAnoDestino() {
        return listaAnoDestino;
    }

    public void setListaAnoDestino(List<Registro> listaAnoDestino) {
        this.listaAnoDestino = listaAnoDestino;
    }

    public String getAnoDestino() {
        return anoDestino;
    }

    public void setAnoDestino(String anoDestino) {
        this.anoDestino = anoDestino;
    }

    public String getAnoOrigen() {
        return anoOrigen;
    }

    public void setAnoOrigen(String anoOrigen) {
        this.anoOrigen = anoOrigen;
    }

}
