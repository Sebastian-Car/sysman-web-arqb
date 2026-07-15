package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.RetencionpersonalsControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sdaza
 * @version 1, 10/07/2015
 * 
 * @version 2, 01/11/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class RetencionpersonalsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private String idEmpleado;
    private String nombreEmpleado;

    private RegistroDataModelImpl listaIdRetetipo;
    private RegistroDataModelImpl listaIdRetetipoE;
    private String auxiliar;
    private Registro registroAux;
    private final String reteTipoCons;
    private final String nombreCons;
    private final String nombreRetencionCons;

    /**
     * Creates a new instance of RetencionpersonalsControlador
     */
    public RetencionpersonalsControlador() {

        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();

        idEmpleado = (String) parametros.get("idEmpleado");
        nombreEmpleado = (String) parametros.get("nombreEmpleado");
        numFormulario = GeneralCodigoFormaEnum.RETENCIONPERSONALS_CONTROLADOR.getCodigo();
        reteTipoCons = "ID_RETETIPO";
        nombreCons = "NOMBRE";
        nombreRetencionCons = "NOMBRE_RETENCION";
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
        enumBase=GenericUrlEnum.RETENCIONPERSONAL;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaIdRetetipo();
        cargarListaIdRetetipoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        idEmpleado);
    }

  
    public void cargarListaIdRetetipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RetencionpersonalsControladorUrlEnum.URL4113
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaIdRetetipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, reteTipoCons);
    }

    public void cargarListaIdRetetipoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RetencionpersonalsControladorUrlEnum.URL4113
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaIdRetetipoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, reteTipoCons);
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public void cambiarIdRetetipoC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        nombreRetencionCons,
                        registroAux.getCampos().get(nombreCons));
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaIdRetetipo(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(reteTipoCons,
                        registroAux.getCampos().get(reteTipoCons));
        registro.getCampos().put(reteTipoCons,
                        registroAux.getCampos().get(reteTipoCons));
        registro.getCampos().put(nombreRetencionCons,
                        registroAux.getCampos().get(nombreCons));
        registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
    }

    public void seleccionarFilaIdRetetipoE(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(reteTipoCons).toString();
        registro.getCampos().put(reteTipoCons,
                        registroAux.getCampos().get(reteTipoCons));
        registro.getCampos().put(nombreRetencionCons,
                        registroAux.getCampos().get(nombreCons));
        registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().remove(nombreRetencionCons);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(nombreRetencionCons);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // Metodo heredado en la clase BeanBase
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo Heredado de la clase BeanBase

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

    public RegistroDataModelImpl getListaIdRetetipo() {
        return listaIdRetetipo;
    }

    public void setListaIdRetetipo(RegistroDataModelImpl listaIdRetetipo) {
        this.listaIdRetetipo = listaIdRetetipo;
    }

    public RegistroDataModelImpl getListaIdRetetipoE() {
        return listaIdRetetipoE;
    }

    public void setListaIdRetetipoE(RegistroDataModelImpl listaIdRetetipoE) {
        this.listaIdRetetipoE = listaIdRetetipoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

}
