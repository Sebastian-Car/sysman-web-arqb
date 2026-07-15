package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.ResponsablesControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 18/09/2015
 * @version 2, 05/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos y reasignar origen
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se pasa el numero del formulario al enumerado, se eliminan conexiones y se ajustan metodos de generacion de reportes.
 * @version 3, 25/04/2018 sdaza adicionar atributos para validar permiso para editar informacion dependiendo el modulo y para visualizar o no la columna nombramiento
 */
@ManagedBean
@ViewScoped
public class ResponsablesControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String consNombre;
    private final String consSucursal;
    private RegistroDataModelImpl listaCedula;
    private RegistroDataModelImpl listaCedulaE;
    private Map<String, Object> parametrosEntrada;
    private String auxiliar;
    private Object cedula;
    private Object sucursal;
    private Object nombre;

    /**
     * Esta variable me identifica si desde la opcion de menu que se ingrese permite o no eliminar
     */
    private int permiteEliminar;
    /**
     * Esta variable identifica si desde la opcion de menu que se ingrese permite o no editar el registro
     */
    private int permiteEditar;

    /**
     * Esta variable valida segun la opcion de menu si el campo nombramiento sera o no visible
     */
    private boolean mostrarNombramiento;

    /**
     * Creates a new instance of ResponsablesControlador
     */
    public ResponsablesControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNombre = "NOMBRE";
        consSucursal = "SUCURSAL";
        if (SessionUtil.getMenuActual().equals("21070111")) {
            permiteEliminar = 0;
            permiteEditar = 0;
            mostrarNombramiento = false;
        }
        else {
            permiteEliminar = -1;
            permiteEditar = -1;
            mostrarNombramiento = true;
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.RESPONSABLES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ResponsablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.RESPONSABLE;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
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

    public void cargarListaCedula() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResponsablesControladorUrlEnum.URL3270
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cargarListaCedulaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResponsablesControladorUrlEnum.URL3938
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCedulaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cambiarCedulaC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CEDULA",
                        cedula);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(consNombre, nombre);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(consSucursal, sucursal);
    }

    public void seleccionarFilaCedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CEDULA", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(consNombre,
                        registroAux.getCampos().get(consNombre));
        registro.getCampos().put(consSucursal,
                        registroAux.getCampos().get(consSucursal));
    }

    public void seleccionarFilaCedulaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        cedula = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        nombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consNombre), " ")
                        .toString();
        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consSucursal), " ")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // METODO NO IMPLEMENTADO
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if ("52".equals(SessionUtil.getModulo())) {
            Direccionador direccionador = new Direccionador();
            if ("-1".equals(parametrosEntrada.get("menup"))) {
                direccionador.setNumForm(Integer.toString(
                                GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR
                                                .getCodigo()));
                parametrosEntrada.put("rid",
                                parametrosEntrada.get("ridProyecto"));
                parametrosEntrada.remove("ridProyecto");
            }
            else {
                direccionador.setNumForm(Integer.toString(
                                GeneralCodigoFormaEnum.SUBRESPONSABLESPROYECTOS_CONTROLADOR
                                                .getCodigo()));
            }
            direccionador.setParametros(parametrosEntrada);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            SessionUtil.redireccionar("/menu.sysman");
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove(consNombre);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(consNombre);
        return true;
    }

    @Override
    public boolean actualizarDespues() {
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

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    public int getPermiteEliminar() {
        return permiteEliminar;
    }

    public void setPermiteEliminar(int permiteEliminar) {
        this.permiteEliminar = permiteEliminar;
    }

    public int getPermiteEditar() {
        return permiteEditar;
    }

    public void setPermiteEditar(int permiteEditar) {
        this.permiteEditar = permiteEditar;
    }

    public boolean isMostrarNombramiento() {
        return mostrarNombramiento;
    }

    public void setMostrarNombramiento(boolean mostrarNombramiento) {
        this.mostrarNombramiento = mostrarNombramiento;
    }

}
