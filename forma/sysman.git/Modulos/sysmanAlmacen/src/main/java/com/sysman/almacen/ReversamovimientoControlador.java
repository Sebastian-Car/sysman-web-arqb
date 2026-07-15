package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.enums.ReversamovimientoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
 * @version 1, 22/01/2016
 *
 * @version 2, 08/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente.
 */

@ManagedBean
@ViewScoped
public class ReversamovimientoControlador extends BeanBaseModal {

    private final String compania;
    private String tipoDeMovimiento;
    private String numero;
    private String nombreMovimiento;
    private String numeroDescripcion;
    private boolean visibleConfirmacion;
    private RegistroDataModelImpl listaTipoDeMovimiento;
    private RegistroDataModelImpl listaNumero;

    @EJB
    private EjbAlmacenCeroRemote ejbAlmacenCeroRemote;

    /**
     * Creates a new instance of ReversamovimientoControlador
     */
    public ReversamovimientoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.REVERSAMOVIMIENTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ReversamovimientoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
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
                                        ReversamovimientoControladorUrlEnum.URL2245
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
                                        ReversamovimientoControladorUrlEnum.URL3046
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                        tipoDeMovimiento);

        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        
        visibleConfirmacion= true;
        

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * del dialogo DgConfirmacion en la vista
     *
     */
    public void aceptarDgConfirmacion() {
        //<CODIGO_DESARROLLADO>
        try {
            String respuesta = ejbAlmacenCeroRemote.reversarMovimiento(compania,
                            tipoDeMovimiento, Long.parseLong(numero));
            JsfUtil.agregarMensajeInformativo(respuesta);
            visibleConfirmacion=false;
        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(ReversamovimientoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        //</CODIGO_DESARROLLADO>
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
        numeroDescripcion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DESCRIPCION"), " ")
                        .toString();
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

    public boolean isVisibleConfirmacion() {
        return visibleConfirmacion;
    }

    public void setVisibleConfirmacion(boolean visibleConfirmacion) {
        this.visibleConfirmacion = visibleConfirmacion;
    }
    
    

}
