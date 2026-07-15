package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.enums.CambiarfechamovimientoControladorEnum;
import com.sysman.almacen.enums.CambiarfechamovimientoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 19/01/2016
 * 
 * @version 2, 26/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */
@ManagedBean
@ViewScoped
public class CambiarfechamovimientoControlador extends BeanBaseModal {

    private final String compania;

    private String tipoDeMovimiento;
    private String numero;
    private String nombreMovimiento;
    private String numeroDescripcion;
    private Date fechaAnterior;
    private Date fechaActual;
    private RegistroDataModelImpl listaTipoDeMovimiento;
    private RegistroDataModelImpl listaNumero;

    @EJB
    private EjbAlmacenCeroRemote ejbAlmacenCeroRemote;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of CambiarfechamovimientoControlador
     */
    public CambiarfechamovimientoControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.CAMBIARFECHAMOVIMIENTO_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        fechaActual = new Date();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CambiarfechamovimientoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoDeMovimiento();
        cargarListaNumero();
        abrirFormulario();
    }

    public void cargarListaTipoDeMovimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarfechamovimientoControladorUrlEnum.URL2397
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoDeMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListaNumero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarfechamovimientoControladorUrlEnum.URL4276
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CambiarfechamovimientoControladorEnum.PARAM0.getValue(),
                        tipoDeMovimiento);

        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        boolean condicionTipoDeMov = (tipoDeMovimiento == null)
            || tipoDeMovimiento.isEmpty();

        if ((fechaActual == null)
            || condicionTipoDeMov
            || (numero == null) || numero.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("MSM_FALTAN_DATOS_PROCESO"));
        }
        try {
            
            String estado = ejbSysmanUtil.verificarEstadoDiario(compania,
                            SysmanFunciones.ano(fechaAnterior),
                            SysmanFunciones.mes(fechaAnterior),
                            SysmanFunciones.dia(fechaAnterior),
                            Integer.parseInt(SessionUtil.getModulo()), 1);
            
            String estadoActual = ejbSysmanUtil.verificarEstadoDiario(compania,
                            SysmanFunciones.ano(fechaActual),
                            SysmanFunciones.mes(fechaActual),
                            SysmanFunciones.dia(fechaActual),
                            Integer.parseInt(SessionUtil.getModulo()), 1);
            
            if ("A".equals(estado) && "A".equals(estadoActual)){
                ejbAlmacenCeroRemote.cambiarFechaMovimiento(compania,
                                tipoDeMovimiento, Long.parseLong(numero),
                                fechaActual, SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }else{
                String texto= "C".equals(estado) ? "Fecha Anterior": "Fecha Nueva";
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3473").replace("$#fecha#$", texto));
            }

           
            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(CambiarfechamovimientoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoDeMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoDeMovimiento = registroAux.getCampos().get("CODIGO").toString();
        nombreMovimiento = registroAux.getCampos().get("NOMBRE").toString();
        numero = null;
        numeroDescripcion = null;
        cargarListaNumero();
    }

    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = registroAux.getCampos().get("NUMERO").toString();
        numeroDescripcion = SysmanFunciones.nvl(registroAux.getCampos().get("DESCRIPCION"), "").toString();
        fechaAnterior = (Date) registroAux.getCampos().get("FECHA");
    }

    public String getTipoDeMovimiento() {
        return tipoDeMovimiento;
    }

    public void setTipoDeMovimiento(String tipoDeMovimiento) {
        this.tipoDeMovimiento = tipoDeMovimiento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombreMovimiento() {
        return nombreMovimiento;
    }

    public void setNombreMovimiento(String nombreMovimiento) {
        this.nombreMovimiento = nombreMovimiento;
    }

    public String getNumeroDescripcion() {
        return numeroDescripcion;
    }

    public void setNumeroDescripcion(String numeroDescripcion) {
        this.numeroDescripcion = numeroDescripcion;
    }

    public Date getFechaAnterior() {
        return fechaAnterior;
    }

    public void setFechaAnterior(Date fechaAnterior) {
        this.fechaAnterior = fechaAnterior;
    }

    public Date getFechaActual() {
        return fechaActual;
    }

    public void setFechaActual(Date fechaActual) {
        this.fechaActual = fechaActual;
    }

    public RegistroDataModelImpl getListaTipoDeMovimiento() {
        return listaTipoDeMovimiento;
    }

    public void setListaTipoDeMovimiento(
        RegistroDataModelImpl listaTipoDeMovimiento) {
        this.listaTipoDeMovimiento = listaTipoDeMovimiento;
    }

    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

}
