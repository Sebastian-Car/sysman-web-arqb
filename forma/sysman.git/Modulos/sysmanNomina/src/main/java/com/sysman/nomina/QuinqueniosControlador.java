package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.nomina.enums.QuinqueniosControladorEnum;
import com.sysman.nomina.enums.QuinqueniosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jgomez
 * @version 1, 04/11/2015
 *
 * @modified lcortes
 * @version 2, 23,24,25,26,27,28/10/2017. Se realiza proceso de
 * refactorizacion de codigo y reemplazo de llamados a funciones de la
 * clase Acciones.
 */
@ManagedBean
@ViewScoped
public class QuinqueniosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String periodoNomina;
    private String idEmpleado;
    private String nombreEmpleado;
    private String cedula;
    private RegistroDataModelImpl listaFechaPago;
    private RegistroDataModelImpl listaFechaPagoE;
    private String auxiliar;
    private String procesoNomina;
    private String tituloForm;
    private Date fechaPago;

    private final String fechaPagQuinCons;
    private final String fechaFinalCons;

    @EJB
    private EjbNominaDosRemote ejbNominaDos;
    @EJB
    private EjbNominaSieteRemote ejbNominaSiete;

    /**
     * Creates a new instance of QuinqueniosControlador
     */
    public QuinqueniosControlador() {

        super();
        compania = SessionUtil.getCompania();
        fechaPagQuinCons = QuinqueniosControladorEnum.FECHAPAGOQUINQUENIO
                        .getValue();
        fechaFinalCons = QuinqueniosControladorEnum.FECHAFINAL.getValue();

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {

            idEmpleado = (String) parametrosEntrada.get("idEmpleado");
            nombreEmpleado = (String) parametrosEntrada.get("nombreEmpleado");
            cedula = (String) parametrosEntrada.get("cedula");
            periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
            procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
        }
        // 324
        numFormulario = GeneralCodigoFormaEnum.QUINQUENIOS_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.PERSONAL.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        tituloForm = SysmanFunciones.concatenar("Quinquenio ", nombreEmpleado);
        fechaPago = (Date) registro.getCampos().get(fechaPagQuinCons);
        cargarListaFechaPago();
        cargarListaFechaPagoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        idEmpleado);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        QuinqueniosControladorUrlEnum.URL0001
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        QuinqueniosControladorUrlEnum.URL0002
                                                        .getValue());
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public RegistroDataModelImpl getListaFechaPago() {
        return listaFechaPago;
    }

    public void setListaFechaPago(RegistroDataModelImpl listaFechaPago) {
        this.listaFechaPago = listaFechaPago;
    }

    public RegistroDataModelImpl getListaFechaPagoE() {
        return listaFechaPagoE;
    }

    public void setListaFechaPagoE(RegistroDataModelImpl listaFechaPagoE) {
        this.listaFechaPagoE = listaFechaPagoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getTituloForm() {
        return tituloForm;
    }

    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

    public void cargarListaFechaPago() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        QuinqueniosControladorUrlEnum.URL5693
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), procesoNomina);

        listaFechaPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, fechaFinalCons);
    }

    public void cargarListaFechaPagoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        QuinqueniosControladorUrlEnum.URL6490
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), procesoNomina);

        listaFechaPagoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, fechaFinalCons);
    }

    public void seleccionarFilaFechaPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(fechaPagQuinCons,
                        registroAux.getCampos().get(fechaFinalCons));
        fechaPago = (Date) registroAux.getCampos().get(fechaFinalCons);
    }

    public void seleccionarFilaFechaPagoE(SelectEvent event) {
        try {
            Registro registroAux = (Registro) event.getObject();
            if ("".equals(registroAux.getCampos().get(fechaFinalCons))) {
                auxiliar = null;
                registro.getCampos().put(fechaFinalCons, null);
                fechaPago = null;
            }
            else {
                auxiliar = SysmanFunciones
                                .convertirAFechaCadena((Date) registroAux
                                                .getCampos()
                                                .get(fechaFinalCons));
                fechaPago = (Date) registroAux.getCampos().get(fechaFinalCons);
            }

        }
        catch (ParseException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // Metodo Heredado de la Clase BeanBase

    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        if ((fechaPago == null)
            || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            fechaPagQuinCons)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2629"));
            return false;
        }
        registro.getCampos().put(fechaPagQuinCons, fechaPago);
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        diferirQui(null);
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void oprimireliminar(Registro reg, int indice) {
        fechaPago = (Date) reg.getCampos().get(fechaPagQuinCons);
        if (fechaPago != null) {

            try {
                ejbNominaSiete.eliminarQuinquenio(compania,
                                Integer.valueOf(procesoNomina), fechaPago,
                                Integer.valueOf(periodoNomina),
                                Integer.valueOf(idEmpleado), "BORRAR",
                                SessionUtil.getUser().getCodigo());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

    }

    private void diferirQui(String unOpcion) {
        if (fechaPago != null) {
            try {
                ejbNominaDos.getDiferirQuin(compania,
                                Integer.valueOf(procesoNomina),
                                Integer.valueOf(idEmpleado), unOpcion == null
                                    ? "null" : "'" + unOpcion + "'",
                                fechaPago);
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    @Override
    public void removerCombos() {
        // Metodo heredado de la clase BeanBase
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        registro.getCampos()
                        .remove(QuinqueniosControladorEnum.FECHAQLB.getValue());
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de la clase BeanBase

    }

}
