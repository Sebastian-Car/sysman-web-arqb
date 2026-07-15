package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.CambiarDatosPagoControladorUrlEnum;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 22/08/2016
 * @modifier amonroy
 * @version 2, 16/05/2017 Proceso de Refactoring e implementación de
 * EJBs
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente.
 *
 */
@ManagedBean
@ViewScoped
public class CambiarDatosPagoControlador extends BeanBaseModal {
    private final String compania;
    /**
     * Constante que identifica el nombre del campo CODIGO
     */
    private final String campoCodigo;
    // <DECLARAR_ATRIBUTOS>
    private String bancoAct;
    private String paqueteAct;
    private String bancoNue;
    private String paqueteNue;
    private Date fechaAct;
    private Date fechaNue;
    private String nombreBancoAct;
    private String nombreBancoNue;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaPaqueteact;
    private List<Registro> listapaquetenue;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaBancoact;
    private RegistroDataModelImpl listabanconue;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Implementacion del EJB de Servicios Publicos Cero para hacer el
     * llamado a la funcion
     * "PCK_SERVICIOS_PUBLICOS.FC_CAMBIARFECHAPAGO"
     */
    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;

    /**
     * Creates a new instance of CambiarDatosPagoControlador
     */
    public CambiarDatosPagoControlador() {
        super();
        compania = SessionUtil.getCompania();
        campoCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIAR_DATOS_PAGO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CambiarDatosPagoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        fechaAct = fechaNue = new Date();
        cargarListaPaqueteact();
        cargarListapaquetenue();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBancoact();
        cargarListabanconue();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaPaqueteact() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHA.getName(), fechaAct);
        param.put(GeneralParameterEnum.BANCO.getName(), bancoAct);

        try {
            listaPaqueteact = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiarDatosPagoControladorUrlEnum.URL3530
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListapaquetenue() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHA.getName(), fechaNue);
        param.put(GeneralParameterEnum.BANCO.getName(), bancoNue);

        try {
            listapaquetenue = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiarDatosPagoControladorUrlEnum.URL3530
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaBancoact() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarDatosPagoControladorUrlEnum.URL5152
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBancoact = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    public void cargarListabanconue() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarDatosPagoControladorUrlEnum.URL5780
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listabanconue = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Realiza el llamado a la funcion
     * PCK_SERVICIOS_PUBLICOS.FC_CAMBIARFECHAPAGO que se encarga de
     * ejecutar el proceso para el cambio de registros de pago
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            String mensaje = ejbServiciosPublicosCero.cambiarFechaPago(compania,
                            bancoAct,
                            fechaAct,
                            paqueteAct,
                            bancoNue,
                            fechaNue,
                            paqueteNue,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(mensaje);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaact() {
        // <CODIGO_DESARROLLADO>
        cargarListaPaqueteact();
        paqueteAct = null;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechanue() {
        // <CODIGO_DESARROLLADO>
        cargarListapaquetenue();
        paqueteNue = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectBancoact(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoAct = registroAux.getCampos().get(campoCodigo).toString();
        nombreBancoAct = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaPaqueteact();
        paqueteAct = null;
    }

    public void onRowSelectbanconue(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoNue = registroAux.getCampos().get(campoCodigo).toString();
        nombreBancoNue = registroAux.getCampos().get("NOMBRE").toString();
        cargarListapaquetenue();
        paqueteNue = null;
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getBancoAct() {
        return bancoAct;
    }

    public void setBancoAct(String bancoAct) {
        this.bancoAct = bancoAct;
    }

    public String getPaqueteAct() {
        return paqueteAct;
    }

    public void setPaqueteAct(String paqueteAct) {
        this.paqueteAct = paqueteAct;
    }

    public String getBancoNue() {
        return bancoNue;
    }

    public void setBancoNue(String bancoNue) {
        this.bancoNue = bancoNue;
    }

    public String getPaqueteNue() {
        return paqueteNue;
    }

    public void setPaqueteNue(String paqueteNue) {
        this.paqueteNue = paqueteNue;
    }

    public Date getFechaAct() {
        return fechaAct;
    }

    public void setFechaAct(Date fechaAct) {
        this.fechaAct = fechaAct;
    }

    public String getNombreBancoAct() {
        return nombreBancoAct;
    }

    public void setNombreBancoAct(String nombreBancoAct) {
        this.nombreBancoAct = nombreBancoAct;
    }

    public String getNombreBancoNue() {
        return nombreBancoNue;
    }

    public void setNombreBancoNue(String nombreBancoNue) {
        this.nombreBancoNue = nombreBancoNue;
    }

    public Date getFechaNue() {
        return fechaNue;
    }

    public void setFechaNue(Date fechaNue) {
        this.fechaNue = fechaNue;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaPaqueteact() {
        return listaPaqueteact;
    }

    public void setListaPaqueteact(List<Registro> listaPaqueteact) {
        this.listaPaqueteact = listaPaqueteact;
    }

    public List<Registro> getListapaquetenue() {
        return listapaquetenue;
    }

    public void setListapaquetenue(List<Registro> listapaquetenue) {
        this.listapaquetenue = listapaquetenue;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaBancoact() {
        return listaBancoact;
    }

    public void setListaBancoact(RegistroDataModelImpl listaBancoact) {
        this.listaBancoact = listaBancoact;
    }

    public RegistroDataModelImpl getListabanconue() {
        return listabanconue;
    }

    public void setListabanconue(RegistroDataModelImpl listabanconue) {
        this.listabanconue = listabanconue;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
