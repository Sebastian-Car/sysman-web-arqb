
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmtipofuenterecursosControladorEnum;
import com.sysman.bancoproyectos.enums.FrmtipofuenterecursosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.List;
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
 * @version 1, 17/09/2015
 * 
 * @modifier amonroy
 * @version 2, 22/09/2017 Se realiza el Proceso de Refactoring a las
 * operaciones CRUD del formulario
 * 
 * @version 3 , 15/03/2018, lbotia. se agrego el boton actualizar tipo
 * recurso.
 */
@ManagedBean
@ViewScoped
public class FrmtipofuenterecursosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * constante a nivel de clase que contiene el codigo del usuario
     * que inicio sesion.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    /**
     * constante a nivel de clase que aloja la cadena CODIGO.
     */
    private final String cCodigo = GeneralParameterEnum.CODIGO.getName();

    /**
     * constante a nivel de clase que aloja la cadena COMPANIA.
     */

    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * variable que contiene el año seleccionado en el combo anio
     * (CB5792).
     */
    private String anio;

    /**
     * Lista que contiene los detalles del combo anio (CB5792).
     */
    private List<Registro> listaAnoActualizar;
    /**
     * Listado de registros para el combo de Anio
     */
    private List<Registro> listaAno;

    /**
     * Lista que carga los tipos de recurso
     */
    private RegistroDataModelImpl listaTipoRecurso;
    /**
     * Lista que carga los tipos de recurso en la grilla
     */
    private RegistroDataModelImpl listaTipoRecursoE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Crea una nueva instancia de FrmtipofuenterecursosControlador
     */
    public FrmtipofuenterecursosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMTIPOFUENTERECURSOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmtipofuenterecursosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = FrmtipofuenterecursosControladorEnum.FUENTE_RECURSOS.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaAno();

        cargarListaTipoRecurso();
        cargarListaTipoRecursoE();
        cargarListaAnoActualizar();
        cargarListaAno();
        abrirFormulario();

    }

    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmtipofuenterecursosControladorUrlEnum.URL2483
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipoRecurso
     *
     */
    public void cargarListaTipoRecurso() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipofuenterecursosControladorUrlEnum.URL12254
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    /**
     * 
     * Carga la lista listaAnoActualizar asociada al combo
     * anio(CB5792).
     *
     * 
     */
    public void cargarListaAnoActualizar() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaAnoActualizar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmtipofuenterecursosControladorUrlEnum.URL2483
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipoRecurso
     *
     */
    public void cargarListaTipoRecursoE() {
        listaTipoRecursoE = listaTipoRecurso;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btnActualizar en la vista
     *
     */
    public void oprimirbtnActualizar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put("ANO", anio);
        param.put("USUARIO", usuario);

        Parameter parametro = new Parameter();
        parametro.setFields(param);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipofuenterecursosControladorUrlEnum.URL005
                                                        .getValue());

        try {
            requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                            parametro);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbAno() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoRecurso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_RECURSO",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoRecursoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .remove(FrmtipofuenterecursosControladorEnum.TIPO_RECURSOLB
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        return true;
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos()
                        .remove(FrmtipofuenterecursosControladorEnum.TIPO_RECURSOLB
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
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
    public int getNumFormulario() {
        return numFormulario;
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipofuenterecursosControladorUrlEnum.URL001
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipofuenterecursosControladorUrlEnum.URL002
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipofuenterecursosControladorUrlEnum.URL003
                                                        .getValue());

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipofuenterecursosControladorUrlEnum.URL004
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoRecurso
     * 
     * @return listaTipoRecurso
     */
    public RegistroDataModelImpl getListaTipoRecurso() {
        return listaTipoRecurso;
    }

    /**
     * Asigna la lista listaTipoRecurso
     * 
     * @param listaTipoRecurso
     * Variable a asignar en listaTipoRecurso
     */
    public void setListaTipoRecurso(RegistroDataModelImpl listaTipoRecurso) {
        this.listaTipoRecurso = listaTipoRecurso;
    }

    /**
     * Retorna la lista listaTipoRecurso
     * 
     * @return listaTipoRecurso
     */
    public RegistroDataModelImpl getListaTipoRecursoE() {
        return listaTipoRecursoE;
    }

    /**
     * Asigna la lista listaTipoRecurso
     * 
     * @param listaTipoRecurso
     * Variable a asignar en listaTipoRecurso
     */
    public void setListaTipoRecursoE(RegistroDataModelImpl listaTipoRecursoE) {
        this.listaTipoRecursoE = listaTipoRecursoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * @param anio
     * the anio to set
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * @return the listaAnoActualizar
     */
    public List<Registro> getListaAnoActualizar() {
        return listaAnoActualizar;
    }

    /**
     * @param listaAnoActualizar
     * the listaAnoActualizar to set
     */
    public void setListaAnoActualizar(List<Registro> listaAnoActualizar) {
        this.listaAnoActualizar = listaAnoActualizar;
    }

}