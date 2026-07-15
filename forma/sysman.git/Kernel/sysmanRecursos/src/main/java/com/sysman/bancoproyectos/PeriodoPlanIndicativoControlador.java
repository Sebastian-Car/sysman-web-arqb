package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.PeriodoPlanIndicativoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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

import org.primefaces.context.RequestContext;

/**
 *
 * @author jrodrigueza
 * @version 1, 22/08/2015
 * 
 * @version 2, 28/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class PeriodoPlanIndicativoControlador extends BeanBaseModal {

    private final String compania;

    private boolean bloquearAno;

    private String vigencia;
    private List<Registro> listaAno;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of PeriodoPlanIndicativoControlador
     */
    public PeriodoPlanIndicativoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODO_PLAN_INDICATIVO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoPlanIndicativoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        cargarListaAno();
        abrirFormulario();
    }

    public void cargarListaAno() {
        try {

            if (SessionUtil.getMenuActual().equals("520133")) {
                bloquearAno = false;

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                listaAno = RegistroConverter.toListRegistro(
                                requestManager.getList(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                PeriodoPlanIndicativoControladorUrlEnum.URL2281
                                                                                .getValue())
                                                .getUrl(), param));

            }
            else {
                bloquearAno = true;
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                listaAno = RegistroConverter.toListRegistro(
                                requestManager.getList(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                PeriodoPlanIndicativoControladorUrlEnum.URL2281
                                                                                .getValue())
                                                .getUrl(), param));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        if (vigencia != null) {
            String menuActual = SessionUtil.getMenuActual();
            if ("520133".equals(menuActual)) {
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("vigencia", vigencia);
                Direccionador direccionador = new Direccionador();
                direccionador.setNumForm("1745");
                direccionador.setParametros(parametros);
                RequestContext.getCurrentInstance().closeDialog(direccionador);

            }
            else {
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("vigencia", vigencia);
                Direccionador direccionador = new Direccionador();
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.BPPLANINDICATIVOS_CONTROLADOR
                                                .getCodigo()));
                direccionador.setParametros(parametros);
                RequestContext.getCurrentInstance().closeDialog(direccionador);
            }
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2256"));
        }
    }

    public void oprimirCancelar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void abrirFormulario() {
        cargarVigencia();
    }

    /**
     * Trae la vigencia gubernamental actual que est� configurada en
     * la base de datos.
     */
    private void cargarVigencia() {
        try {
            vigencia = ejbSysmanUtil.consultarParametro(
                            compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL",
                            SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(
                                            "MSM_TRANS_INTERRUMPIDA"),
                                            ex.getMessage()));
            Logger.getLogger(PeriodoPlanIndicativoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * @return the bloquearAno
     */
    public boolean isBloquearAno() {
        return bloquearAno;
    }

    /**
     * @param bloquearAno
     * the bloquearAno to set
     */
    public void setBloquearAno(boolean bloquearAno) {
        this.bloquearAno = bloquearAno;
    }

}
