package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.OrdenadorsControladorUrlEnum;
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
 * @author ybecerra
 * @version 1, 15/09/2015
 *
 * @author jlramirez
 * @version 2, 03/04/2017, proceso de Refactoring y modificaciones
 * segUn especificaciones de SONARLINT
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class OrdenadorsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String txtCedula;
    private final String txtSucursal;
    private final String txtNombre;
    private final String txtExpedida;
    private RegistroDataModelImpl listaCedula;
    private RegistroDataModelImpl listaCedulaE;
    private String auxiliar;
    private Object nombre;
    private Object expedida;

    private Object cedula;
    private Object sucursal;

    /**
     * Creates a new instance of OrdenadorsControlador
     */
    public OrdenadorsControlador() {
        super();
        compania = SessionUtil.getCompania();
        txtCedula = "CEDULA";
        txtSucursal = "SUCURSAL";
        txtNombre = "NOMBRE";
        txtExpedida = "EXPEDIDACEDULAORDENADOR";
        try {
            numFormulario = GeneralCodigoFormaEnum.ORDENADORS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(OrdenadorsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ORDENADOR;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        registro.getCampos().put("IND_ACTIVO", true);
        registro.getCampos().put("IND_ACTIVOCON", true);
        cargarListaCedula();
        cargarListaCedulaE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Object getCedula() {
        return cedula;
    }

    public void setCedula(Object cedula) {
        this.cedula = cedula;
    }

    public Object getSucursal() {
        return sucursal;
    }

    public void setSucursal(Object sucursal) {
        this.sucursal = sucursal;
    }

    public void cargarListaCedula() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdenadorsControladorUrlEnum.URL3082
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaCedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, txtCedula);
    }

    public void cargarListaCedulaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdenadorsControladorUrlEnum.URL3929
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCedulaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, txtCedula);
    }

    public void seleccionarFilaCedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(txtCedula,
                        registroAux.getCampos().get(txtCedula));
        registro.getCampos().put(txtSucursal,
                        registroAux.getCampos().get(txtSucursal));
        registro.getCampos().put(txtNombre,
                        registroAux.getCampos().get(txtNombre));
        registro.getCampos().put(txtExpedida,
                        registroAux.getCampos().get(txtExpedida));

    }

    public void seleccionarFilaCedulaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(txtCedula) == null ? " "
            : registroAux.getCampos().get(txtCedula).toString();
        cedula = registroAux.getCampos().get(txtCedula) == null ? " "
            : registroAux.getCampos().get(txtCedula).toString();
        nombre = registroAux.getCampos().get(txtNombre) == null ? " "
            : registroAux.getCampos().get(txtNombre).toString();
        sucursal = registroAux.getCampos().get(txtSucursal) == null ? " "
            : registroAux.getCampos().get(txtSucursal).toString();
        expedida = registroAux.getCampos().get(txtExpedida) == null ? " "
            : registroAux.getCampos().get(txtExpedida).toString();

    }

    public void cambiarCedulaC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(txtNombre,
                        nombre);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(txtExpedida, expedida);
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
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("USUARIO", SessionUtil.getUser().getCodigo());
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
    public void asignarValoresRegistro() {
        registro.getCampos().put("IND_ACTIVO", true);
        registro.getCampos().put("IND_ACTIVOCON", true);

    }
}
