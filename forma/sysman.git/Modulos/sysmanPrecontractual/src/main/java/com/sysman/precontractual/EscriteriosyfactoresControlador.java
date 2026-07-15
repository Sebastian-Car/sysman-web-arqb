package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EscriteriosyfactoresControladorEnum;
import com.sysman.precontractual.enums.EscriteriosyfactoresControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 26/07/2016
 * 
 * @modifier amonroy
 * @version 2, 24/08/2017 Proceso de Refactoring, revision de buenas
 * practicas sugeridas por la herramienta SonarLint e implementacion
 * de EJBs para las funciones que son llamadas en el controlador
 */
@ManagedBean
@ViewScoped
public class EscriteriosyfactoresControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el codigo de la modalidad seleccionada en
     * el formulario
     */
    private String codigoModalidad;
    /**
     * Listado para el combo de Modalidad
     */
    private List<Registro> listacmbModalidad;
    /**
     * Indicador que permite la visibilidad del boton "Nuevo", para
     * realizar un registro nuevo en el formulario
     */
    private boolean verNuevo;
    /**
     * Implementacion del EJB de SysmanUtil para el llamado a la
     * funcion PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de EscriteriosyfactoresControlador
     */
    public EscriteriosyfactoresControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ESCRITERIOSYFACTORES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            verNuevo = false;
        }
        catch (Exception ex) {
            Logger.getLogger(EscriteriosyfactoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ES_CRITERIOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListacmbModalidad();
        abrirFormulario();
    }

    /**
     * Realiza la busqueda de las Urls asociadas al enumerado de la
     * tabla, Asigna los parametros para la carga de la UrlListado
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoModalidad);
    }

    /**
     * 
     * Carga la lista listacmbModalidad
     *
     */
    public void cargarListacmbModalidad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listacmbModalidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EscriteriosyfactoresControladorUrlEnum.URL3703
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control cmbModalidad
     * 
     * Verifica si ha sido seleccionada una modalidad para mostrar u
     * ocultar el boton de "Nuevo"
     * 
     */
    public void cambiarcmbModalidad() {
        // <CODIGO_DESARROLLADO>
        verNuevo = false;
        if (!SysmanFunciones.validarVariableVacio(codigoModalidad)) {
            verNuevo = true;
            reasignarOrigen();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * Calcula el valor del consecutivo para realizar la insercion
     * 
     * @return La aprobacoin para realizar el registro de la
     * informacion
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos()
                            .put(EscriteriosyfactoresControladorEnum.CODIGOMODALIDAD
                                            .getValue(), codigoModalidad);

            String condicion = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania,
                            "'' AND CODIGOMODALIDAD = ''", codigoModalidad,
                            "''");
            long consecutivoAux = ejbSysmanUtil
                            .generarConsecutivoConValorInicial(
                                            GenericUrlEnum.ES_CRITERIOS
                                                            .getTable(),
                                            condicion,
                                            EscriteriosyfactoresControladorEnum.CODIGOCRITERIO
                                                            .getValue(),
                                            "01");

            String consecutivo = SysmanFunciones
                            .padl(String.valueOf(consecutivoAux), 2, "0");

            registro.getCampos()
                            .put(EscriteriosyfactoresControladorEnum.CODIGOCRITERIO
                                            .getValue(), consecutivo);
            reasignarOrigen();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

    /**
     * Este metodo se ejecuta antes de enviar la accion de
     * actualizacion, en el se pueden remover valores auxiliares que
     * no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(EscriteriosyfactoresControladorEnum.CODIGOMODALIDAD
                                        .getValue());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListacmbModalidad() {
        return listacmbModalidad;
    }

    public void setListacmbModalidad(List<Registro> listacmbModalidad) {
        this.listacmbModalidad = listacmbModalidad;
    }

    public String getCodigoModalidad() {
        return codigoModalidad;
    }

    public void setCodigoModalidad(String codigoModalidad) {
        this.codigoModalidad = codigoModalidad;
    }

    public boolean isVerNuevo() {
        return verNuevo;
    }

    public void setVerNuevo(boolean verNuevo) {
        this.verNuevo = verNuevo;
    }

}
