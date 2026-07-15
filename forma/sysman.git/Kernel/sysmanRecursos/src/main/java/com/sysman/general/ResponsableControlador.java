package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.ResponsableControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 30/10/2015
 *
 * @author jlramirez
 * @version 2, 05/04/2017, Se realizo el proceso de Refactoring y
 * modificaciones según especificaciones de SONARLINT
 *
 * --Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numero de formulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class ResponsableControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String txtCedula;
    private final String txtDependencia;
    private final String txtNomDep;
    private final String cod;
    private final String nom;
    private final String nomRes;
    private final String act;
    private RegistroDataModelImpl listaCedula;
    private RegistroDataModelImpl listaCedulaE;
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaE;
    private RegistroDataModelImpl listaNombre;
    private RegistroDataModelImpl listaNombreE;
    private String auxiliar;

    private String cedula;
    private boolean activo;
    private String nombResponsable;
    private String cargo;
    private String nomDependencia;
    private String sucursal;
    private String nombramiento;

    /**
     * Creates a new instance of ResponsableControlador
     */
    public ResponsableControlador() {
        super();
        compania = SessionUtil.getCompania();
        txtCedula = "CEDULA";
        txtDependencia = "DEPENDENCIA";
        txtNomDep = "NOMDEPENDENCIA";
        cod = "CODIGO";
        nom = "NOMBRE";
        nomRes = "NOMBRESPONSABLE";
        act = "ACTIVO";
        try {
            numFormulario = GeneralCodigoFormaEnum.RESPONSABLE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(ResponsableControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.RESPONSABLEDEP;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaCedula();
        cargarListaDependencia();
        cargarListaDependenciaE();
        abrirFormulario();
    }

    public RegistroDataModelImpl getListaCedula() {
        return listaCedula;
    }

    public void setListaCedula(RegistroDataModelImpl listaCedula) {
        this.listaCedula = listaCedula;
    }

    public RegistroDataModelImpl getListaCedulaE() {
        return listaCedulaE;
    }

    public void setListaCedulaE(RegistroDataModelImpl listaCedulaE) {
        this.listaCedulaE = listaCedulaE;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public RegistroDataModelImpl getListaDependenciaE() {
        return listaDependenciaE;
    }

    public void setListaDependenciaE(RegistroDataModelImpl listaDependenciaE) {
        this.listaDependenciaE = listaDependenciaE;
    }

    public RegistroDataModelImpl getListaNombre() {
        return listaNombre;
    }

    public void setListaNombre(RegistroDataModelImpl listaNombre) {
        this.listaNombre = listaNombre;
    }

    public RegistroDataModelImpl getListaNombreE() {
        return listaNombreE;
    }

    public void setListaNombreE(RegistroDataModelImpl listaNombreE) {
        this.listaNombreE = listaNombreE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombResponsable() {
        return nombResponsable;
    }

    public void setNombResponsable(String nombResponsable) {
        this.nombResponsable = nombResponsable;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getNomDependencia() {
        return nomDependencia;
    }

    public void setNomDependencia(String nomDependencia) {
        this.nomDependencia = nomDependencia;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public void cargarListaCedula() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResponsableControladorUrlEnum.URL5035
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, txtCedula);
    }

    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResponsableControladorUrlEnum.URL6115
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);

    }

    public void cargarListaDependenciaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResponsableControladorUrlEnum.URL6809
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaDependenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cambiarDependenciaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        txtNomDep,
                        registro.getCampos().get(txtNomDep));
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(txtNomDep, nomDependencia);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedula = registroAux.getCampos().get(txtCedula) == null ? " "
            : registroAux.getCampos().get(txtCedula).toString();
        nombResponsable = registroAux.getCampos().get(nomRes) == null ? " "
            : registroAux.getCampos().get(nomRes).toString();
        cargo = registroAux.getCampos()
                        .get(GeneralParameterEnum.CARGO.getName()) == null ? " "
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CARGO
                                                            .getName())
                                            .toString();
        activo = (boolean) registroAux.getCampos().get(act);
        sucursal = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString();
        nombramiento = registroAux.getCampos()
                        .get("NOMBRAMIENTO") == null
                        ? " "
                        : registroAux.getCampos()
                                        .get("NOMBRAMIENTO")
                                        .toString();
        registro.getCampos().put(txtCedula, cedula);
        registro.getCampos().put(nomRes, nombResponsable);
        registro.getCampos().put(GeneralParameterEnum.CARGO.getName(), cargo);
        registro.getCampos().put(act, activo);
        reasignarOrigen();
    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(txtDependencia,
                        registroAux.getCampos().get(cod));
        registro.getCampos().put(txtNomDep,
                        registroAux.getCampos().get(nom));
    }

    public void seleccionarFilaDependenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cod) == null ? " "
            : registroAux.getCampos().get(cod).toString();
        nomDependencia = registroAux.getCampos().get(nom) == null ? " "
            : registroAux.getCampos().get(nom).toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if (("").equals(cedula) || (cedula == null)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB657"));
            return false;
        }
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("RESPONSABLE", cedula);
        registro.getCampos().put("SUCURSAL", sucursal);
        registro.getCampos().remove(txtNomDep);
        registro.getCampos().remove(nomRes);
        registro.getCampos().remove(act);
        registro.getCampos().remove(txtCedula);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMDEPENDENCIA");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

    @Override
    public void removerCombos() {
        // NO SE IMPLEMENTA

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(txtCedula, cedula);

    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA

    }

    public String getNombramiento() {
        return nombramiento;
    }

    public void setNombramiento(String nombramiento) {
        this.nombramiento = nombramiento;
    }
    
    
}
