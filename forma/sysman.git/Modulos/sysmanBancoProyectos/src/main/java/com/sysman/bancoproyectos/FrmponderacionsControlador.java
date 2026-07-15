package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCuatroRemote;
import com.sysman.bancoproyectos.enums.FrmponderacionsControladorEnum;
import com.sysman.bancoproyectos.enums.FrmponderacionsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
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

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jrodrigueza
 * @version 1, 19/09/2015
 *
 * @author spina
 * @version 2, 20/09/2017 - se refactoriza para dss, depuracion sonar
 * y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmponderacionsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String usuario;
    private String vigencia;
    private List<Registro> listaVIGENCIA;
    private double totalPonderacion;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbBancoProyectoCuatroRemote ejbBancoProyectoCuatro;

    /**
     * Creates a new instance of FrmponderacionsControlador
     */
    public FrmponderacionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMPONDERACIONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(FrmponderacionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init() {
        tabla = FrmponderacionsControladorEnum.BP_PLAN_INDICATIVO.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaVIGENCIA();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(FrmponderacionsControladorUrlEnum.URL4310
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

        urlActualizacion = UrlServiceUtil
                        .getUrlBeanById(FrmponderacionsControladorUrlEnum.URL4312
                                        .getValue());
    }

    public void cargarListaVIGENCIA() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaVIGENCIA = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmponderacionsControladorUrlEnum.URL4311
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Despu�s de seleccionar la vigencia actualiza el origen de
     * datos y calcula el total de la ponderaci�n.
     */
    public void cambiarVIGENCIA() {

        reasignarOrigen();
        calcularTotalPonderacion();
    }

    @Override
    public void abrirFormulario() {
        try {
            vigencia = ejbSysmanUtil.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL",
                            SessionUtil.getModulo(),
                            new Date(), true);
            reasignarOrigen();
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmponderacionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        calcularTotalPonderacion();
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO

    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(FrmponderacionsControladorEnum.ID.getValue());
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        registro.getCampos().remove(
                        GeneralParameterEnum.VIGENCIA_INICIAL.getName());
        registro.getCampos().remove(
                        FrmponderacionsControladorEnum.META_PRODUC.getValue());
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        String indicador = (String) registro.getCampos()
                        .get(FrmponderacionsControladorEnum.KEY_ID.getValue());
        try {
            ejbBancoProyectoCuatro.mayorizarPonderacion(compania,
                            Integer.parseInt(vigencia),
                            false,
                            indicador, false, usuario);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        calcularTotalPonderacion();
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public double getTotalPonderacion() {
        return totalPonderacion;
    }

    /**
     * Realiza la sumatoria de las ponderaciones asignadas a las metas
     * de producto para hallar el total de la ponderaci�n.
     */
    public void calcularTotalPonderacion() {
        if (vigencia == null) {
            return;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmponderacionsControladorUrlEnum.URL4313
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null) {
                totalPonderacion = Double.parseDouble(SysmanFunciones
                                .nvl(rs.getCampos()
                                                .get(GeneralParameterEnum.TOTAL
                                                                .getName()),
                                                0)
                                .toString());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public List<Registro> getListaVIGENCIA() {
        return listaVIGENCIA;
    }

    public void setListaVIGENCIA(List<Registro> listaVIGENCIA) {
        this.listaVIGENCIA = listaVIGENCIA;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTAD

    }
}