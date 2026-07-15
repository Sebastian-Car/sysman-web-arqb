package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ProyectosporpersonasControladorUrlEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sdaza
 * @version 1, 11/07/2015
 * 
 * @author eamaya
 * @version 2.0, 23/10/2017, Proceso de Refactoring y cambio de numero
 * de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class ProyectosporpersonasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * variable que alamcena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el idempleado
     */
    private String idEmpleado;
    /**
     * variable que almacena el nombre del empleado
     */
    private String nombreEmpleado;
    /**
     * lista los proyectos
     */
    private RegistroDataModelImpl listaIdProyecto;
    /**
     * lista los proyectos
     */
    private RegistroDataModelImpl listaIdProyectoE;
    /**
     * vairable auxiliar
     */
    private String auxiliar;
    /**
     * variable estaticas
     */
    private static final String ID_DE_PROYECTO = "ID_DE_PROYECTO";
    private static final String NOMBREPROYECTO_LB = "NOMBREPROYECTO_LB";
    private static final String DESCRIPCION = "DESCRIPCION";
    private static final String NOMBREPROYECTO = "NOMBREPROYECTO";
    /**
     * variable de registro auxiliar
     */
    private Registro registroAux;

    /**
     * Creates a new instance of ProyectosporpersonasControlador
     */
    public ProyectosporpersonasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PROYECTOSPORPERSONAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(ProyectosporpersonasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PROYECTOSPERSONAL;
        buscarLlave();
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            idEmpleado = (String) parametros.get("idEmpleado");
            nombreEmpleado = (String) parametros.get("nombreEmpleado");
        }
        else {
            SessionUtil.redireccionarMenu();
        }
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaIdProyecto();
        cargarListaIdProyectoE();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    /**
     * metodo ques se llama para reasignar el origen de grilla
     */
    @Override
    public void reasignarOrigen() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        idEmpleado);

    }

    /**
     * metodo que se llama cuando se carga lal ista del proyecto
     */
    public void cargarListaIdProyecto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProyectosporpersonasControladorUrlEnum.URL4208
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaIdProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ID_DE_PROYECTO);
    }

    /**
     * metodo que se llama cuando se carga la lista del proyecto
     */
    public void cargarListaIdProyectoE() {
        listaIdProyectoE = listaIdProyecto;
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

    public RegistroDataModelImpl getListaIdProyecto() {
        return listaIdProyecto;
    }

    public void setListaIdProyecto(RegistroDataModelImpl listaIdProyecto) {
        this.listaIdProyecto = listaIdProyecto;
    }

    public RegistroDataModelImpl getListaIdProyectoE() {
        return listaIdProyectoE;
    }

    public void setListaIdProyectoE(RegistroDataModelImpl listaIdProyectoE) {
        this.listaIdProyectoE = listaIdProyectoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    /**
     * metodo que se llama cuando se cambia un proyecto
     * 
     * @param rowNum
     */
    public void cambiarIdProyectoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        NOMBREPROYECTO_LB,
                        registroAux.getCampos().get(NOMBREPROYECTO));

    }

    /**
     * metodo que se llama cuando se selccionar la fila de un proyecto
     * 
     * @param event
     */
    public void seleccionarFilaIdProyecto(SelectEvent event) {
        registroAux = (Registro) event.getObject();

        registro.getCampos().put(ID_DE_PROYECTO,
                        registroAux.getCampos().get(ID_DE_PROYECTO));
        registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
        registro.getCampos().put(NOMBREPROYECTO_LB,
                        registroAux.getCampos().get(NOMBREPROYECTO));
        registro.getCampos().put(DESCRIPCION,
                        registroAux.getCampos().get(DESCRIPCION));
    }

    /**
     * metodo que se llama cuando se selccionar la fila de un proyecto
     * 
     * @param event
     */
    public void seleccionarFilaIdProyectoE(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(ID_DE_PROYECTO), "")
                        .toString();
        registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
        registro.getCampos().put(NOMBREPROYECTO_LB,
                        registroAux.getCampos().get(NOMBREPROYECTO));
        registro.getCampos().put(DESCRIPCION,
                        registroAux.getCampos().get(DESCRIPCION));
    }

    /**
     * metodo que se llama cuando se cierra el formuario
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * metodo que se llama cuando se va ha registrar un nuevo registro
     */
    @Override
    public boolean insertarAntes() {

        if (!validarFechas()) {
            return false;
        }

        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove(NOMBREPROYECTO_LB);

        return true;

    }

    private boolean validarFechas() {
        Date fechaInicio = (Date) registro.getCampos().get("FECHAINICIAL");

        Date fechaFin = (Date) registro.getCampos().get("FECHAFINAL");

        if (fechaInicio.after(fechaFin)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3713"));
            return false;
        }

        return true;
    }

    /**
     * metodo que se llama despues de registrar un nuevo registro
     */
    @Override
    public boolean insertarDespues() {

        return true;
    }

    @Override
    public boolean actualizarAntes() {
        if (!validarFechas()) {
            return false;
        }

        registro.getCampos().remove("FECHAINICIAL_LB");
        registro.getCampos().remove("FECHAFINAL_LB");
        registro.getCampos().remove(NOMBREPROYECTO_LB);

        return true;
    }

    /**
     * metodo que se llama despues de actualizar
     */
    @Override
    public boolean actualizarDespues() {

        return true;
    }

    /**
     * metodo que se llama cuando se elimina
     */
    @Override
    public boolean eliminarAntes() {

        return true;
    }

    /**
     * metodo que se llama despues de eliminar
     */
    @Override
    public boolean eliminarDespues() {

        return true;
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    @Override
    public void asignarValoresRegistro() {
        // heredado del bean base
    }

}
