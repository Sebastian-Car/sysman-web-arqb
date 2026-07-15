package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.SeleccionarFondosControladorUrlEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author cmanrique
 * 
 * @author ybecerra
 * @version 2, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 * 
 * @author ybecerra
 * @version 2, 31/10/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class SeleccionarFondosControlador extends BeanBaseModal {
    // <DECLARAR_ATRIBUTOS>
    private String claseFondo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCodClase;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de SeleccionarFondosControlador
     */
    public SeleccionarFondosControlador() {
        super();
        try {
            // 34
            numFormulario = GeneralCodigoFormaEnum.SELECCIONAR_FONDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SeleccionarFondosControlador.class.getName())
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
        // <CARGAR_LISTA>
        cargarListaCodClase();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
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

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCodClase
     */
    public void cargarListaCodClase() {
        try {

            String modal = (String) SessionUtil
                            .getSessionVarContainer("origen");
            if (modal != null) {
                listaCodClase = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SeleccionarFondosControladorUrlEnum.URL3239
                                                                                                .getValue())
                                                                .getUrl(),
                                                null));

            }
            else {
                listaCodClase = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SeleccionarFondosControladorUrlEnum.URL3238
                                                                                                .getValue())
                                                                .getUrl(),
                                                null));
            }

        }
        catch (SystemException | NamingException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdAceptar en la vista
     *
     */
    public void oprimircmdAceptar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        try {
            String origen = (String) SessionUtil
                            .getSessionVarContainer("origen");
            Direccionador direccionador = new Direccionador();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("claseFondo", claseFondo);

            if (origen != null) {
                parametros.put("mostrarRubro", true);
            }

            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.FONDOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            if (origen != null) {
                SessionUtil.removeSessionVarContainer("origen");
            }
            RequestContext.getCurrentInstance().closeDialog(direccionador);
        }
        catch (NamingException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdCancelar en la vista
     *
     */
    public void oprimirCmdCancelar() {
        // <CODIGO_DESARROLLADO>
        String retorno;
        try {
            retorno = (String) SessionUtil
                            .getSessionVarContainer("retorno");

            if (retorno != null) {
                SessionUtil.redireccionarMenuFormulario(retorno, true);
            }
            else {
                RequestContext.getCurrentInstance().closeDialog(null);
            }
        }
        catch (NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable claseFondo
     * 
     * @return claseFondo
     */
    public String getClaseFondo() {
        return claseFondo;
    }

    /**
     * Asigna la variable claseFondo
     * 
     * @param claseFondo
     * Variable a asignar en claseFondo
     */
    public void setClaseFondo(String claseFondo) {
        this.claseFondo = claseFondo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCodClase
     * 
     * @return listaCodClase
     */
    public List<Registro> getListaCodClase() {
        return listaCodClase;
    }

    /**
     * Asigna la lista listaCodClase
     * 
     * @param listaCodClase
     * Variable a asignar en listaCodClase
     */
    public void setListaCodClase(List<Registro> listaCodClase) {
        this.listaCodClase = listaCodClase;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
