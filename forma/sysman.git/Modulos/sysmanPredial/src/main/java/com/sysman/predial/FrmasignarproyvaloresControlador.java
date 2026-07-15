package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.FrmasignarproyvaloresControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
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
 * @author NGOMEZ
 * @version 1, 08/06/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @modifier amonroy
 * @version 3, 29/06/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones que son llamadas en el
 * controlador
 */
@ManagedBean
@ViewScoped
public class FrmasignarproyvaloresControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String nOrden;
    // <DECLARAR_ATRIBUTOS>
    private boolean indAporte;
    private String proyecto;
    private String factura;
    private String predio;
    private String propietario;
    private String preval;
    private String vlrAporte;
    private String concAporte;
    private boolean aceptarInactivo;
    private final String cCodigo;
    /**
     * Implementacion del EJB de SysmanUtil para hacer el llamado a la
     * funcion FC_PAR
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de EjbPredialOchoRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL_COM8
     */
    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaProyecto;
    private RegistroDataModelImpl listaFactura;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmasignarproyvaloresControlador
     */
    public FrmasignarproyvaloresControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        cCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMASIGNARPROYVALORES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmasignarproyvaloresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        consultarConcAporte();
        cargarListaProyecto();
        cargarListaFactura();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        aceptarInactivo = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaProyecto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmasignarproyvaloresControladorUrlEnum.URL3571
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaFactura() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmasignarproyvaloresControladorUrlEnum.URL4247
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CONCEPTO.getName(), concAporte);

        listaFactura = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "DOCNUM");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimircmdAceptar() {
        // <CODIGO_DESARROLLADO>
        consultarConcAporte();
        if ("0".equals(vlrAporte)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1338"));
            return;
        }

        if (SysmanFunciones.validarVariableVacio(proyecto) && indAporte) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1339"));
            return;
        }

        actualizaRegistro();
        // </CODIGO_DESARROLLADO>
    }

    private void actualizaRegistro() {
        try {
            int rta = ejbPredialOcho.actualizarAporteRecibo(compania,
                            SessionUtil.getUser().getCodigo(),
                            " C" + concAporte,
                            BigDecimal.valueOf(Double.parseDouble(vlrAporte)),
                            BigDecimal.valueOf(Double.parseDouble(preval)),
                            new BigInteger(SysmanFunciones.nvlStr(proyecto,
                                            "0")),
                            nOrden,
                            factura,
                            indAporte);

            if (rta == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1340"));
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(FrmasignarproyvaloresControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimircmdLimpiar() {
        // <CODIGO_DESARROLLADO>
        limpiarCampos();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        cCodigo)) {
            proyecto = (registroAux.getCampos().get(cCodigo))
                            .toString();
        }
        else {
            proyecto = "";
        }
    }

    public void seleccionarFilaFactura(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        if (!validaRegistro()) {
            return;
        }

        if ((boolean) SysmanFunciones
                        .nvl(registroAux.getCampos().get("ANULADO"), false)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB203"));
            limpiarCampos();
            return;
        }

        if ((boolean) SysmanFunciones.nvl(registroAux.getCampos().get("PAGO"),
                        false)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB204"));
            limpiarCampos();
            return;
        }

        factura = SysmanFunciones.nvl(registroAux.getCampos().get("DOCNUM"), "")
                        .toString();
        predio = SysmanFunciones.nvl(registroAux.getCampos().get("PRECOD"), "")
                        .toString();
        propietario = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        preval = SysmanFunciones
                        .nvl(registroAux.getCampos().get("VLRTOTAL"), "")
                        .toString();
        indAporte = (boolean) SysmanFunciones
                        .nvl(registroAux.getCampos().get("INDAPORTE"), false);
        proyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PROYECTO_APORTE"), "")
                        .toString();
        vlrAporte = SysmanFunciones
                        .nvl(registroAux.getCampos().get("VALOR_APORTE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    private boolean validaRegistro() {
        aceptarInactivo = false;

        if (!isNumeric(concAporte)
            || SysmanFunciones.validarVariableVacio(concAporte)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1336"));
            aceptarInactivo = true;
            return false;
        }
        else if ((Integer.parseInt(concAporte) < 14)
            || (Integer.parseInt(concAporte) > 20)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1337"));
            aceptarInactivo = true;
            return false;
        }
        return true;
    }

    public void limpiarCampos() {
        factura = null;
        indAporte = false;
        proyecto = null;
        factura = null;
        predio = null;
        propietario = null;
        preval = null;
        vlrAporte = null;
    }

    /**
     * Realiza la consulta al parametro
     * "CONCEPTO PARA APORTE VOLUNTARIO" y lo asigna al atributo
     * concAporte
     */
    private void consultarConcAporte() {
        try {
            concAporte = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CONCEPTO PARA APORTE VOLUNTARIO",
                                            modulo, new Date(), true), "")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <SET_GET_ATRIBUTOS>
    public String getProyecto() {
        return proyecto;
    }

    public boolean isIndAporte() {
        return indAporte;
    }

    public void setIndAporte(boolean indAporte) {
        this.indAporte = indAporte;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getPreval() {
        return preval;
    }

    public void setPreval(String preval) {
        this.preval = preval;
    }

    public String getVlrAporte() {
        return vlrAporte;
    }

    public void setVlrAporte(String vlrAporte) {
        this.vlrAporte = vlrAporte;
    }

    public String getConcAporte() {
        return concAporte;
    }

    public void setConcAporte(String concAporte) {
        this.concAporte = concAporte;
    }

    public boolean isAceptarInactivo() {
        return aceptarInactivo;
    }

    public void setAceptarInactivo(boolean aceptarInactivo) {
        this.aceptarInactivo = aceptarInactivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaProyecto() {
        return listaProyecto;
    }

    public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    public RegistroDataModelImpl getListaFactura() {
        return listaFactura;
    }

    public void setListaFactura(RegistroDataModelImpl listaFactura) {
        this.listaFactura = listaFactura;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private boolean isNumeric(String txt) {
        try {
            Double a = Double.parseDouble(txt);
            return a != null;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }
}
