package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCuatroRemote;
import com.sysman.bancoproyectos.enums.CambiarPeriodicidadControladorUrlEnum;
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

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author ngomez
 * @version 1, 28/08/2015
 * 
 * @author ybecerra
 * @version 2, 12/09/2017, proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped

public class CambiarPeriodicidadControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String proyectoInicial;
    private String periocidad;
    private String periodicidadAux;
    private boolean conprogramacion;
    private String programacion;
    private boolean cuadro;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaProyectoInicial;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbBancoProyectoCuatroRemote ejbBancoProyectoCuatro;

    /**
     * Crea una nueva instancia de CambiarPeriodicidadControlador
     */
    public CambiarPeriodicidadControlador()
    {
        super();
        // 156
        numFormulario = GeneralCodigoFormaEnum.CAMBIAR_PERIODICIDAD_CONTROLODAR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(CambiarPeriodicidadControlador.class.getName())
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
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaProyectoInicial();
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
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaProyectoInicial
     *
     */
    public void cargarListaProyectoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarPeriodicidadControladorUrlEnum.URL2303
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyectoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     */
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>

        cuadro = false;
        programacion = idioma.getString("TB_TB3546").replace(
                        "s$programacion$s", "");
        if (periocidad.equals(periodicidadAux))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2395"));
        }
        else
        {

            if (conprogramacion)
            {
                programacion = idioma.getString("TB_TB3546").replace(
                                "s$programacion$s",
                                idioma.getString("TB_TB3545"));
            }
            cuadro = true;

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Periocidad
     * 
     */
    public void cambiarPeriocidad()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cuadro
     * 
     * 
     */
    public void cambiarCuadro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo Cuadro
     * en la vista
     *
     */
    public void aceptarCuadro()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            periodicidadAux = ejbBancoProyectoCuatro.getCambiarPeriodicidad(
                            compania, proyectoInicial, periocidad,
                            periodicidadAux, SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        cuadro = false;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProyectoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProyectoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        proyectoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        periodicidadAux = registroAux.getCampos().get("PERIOCIDAD").toString();
        conprogramacion = (boolean) registroAux.getCampos()
                        .get("CONPROGRAMACION");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable proyectoInicial
     * 
     * @return proyectoInicial
     */
    public String getProyectoInicial()
    {
        return proyectoInicial;
    }

    /**
     * Asigna la variable proyectoInicial
     * 
     * @param proyectoInicial
     * Variable a asignar en proyectoInicial
     */
    public void setProyectoInicial(String proyectoInicial)
    {
        this.proyectoInicial = proyectoInicial;
    }

    /**
     * Retorna la variable periocidad
     * 
     * @return periocidad
     */

    public String getPeriocidad()
    {
        return periocidad;
    }

    /**
     * Asigna la variable periocidad
     * 
     * @param periocidad
     * Variable a asignar en periocidad
     */
    public void setPeriocidad(String periocidad)
    {
        this.periocidad = periocidad;
    }

    /**
     * Retorna la variable periodicidadAux
     * 
     * @return periodicidadAux
     */
    public String getPeriodicidadAux()
    {
        return periodicidadAux;
    }

    /**
     * Asigna la variable periodicidadAux
     * 
     * @param periodicidadAux
     * Variable a asignar en periodicidadAux
     */
    public void setPeriodicidadAux(String periodicidadAux)
    {
        this.periodicidadAux = periodicidadAux;
    }

    /**
     * Retorna la variable programacion
     * 
     * @return programacion
     */
    public String getProgramacion()
    {
        return programacion;
    }

    /**
     * Asigna la variable programacion
     * 
     * @param programacion
     * Variable a asignar en programacion
     */
    public void setProgramacion(String programacion)
    {
        this.programacion = programacion;
    }

    /**
     * Retorna la variable conprogramacion
     * 
     * @return conprogramacion
     */
    public boolean getConprogramacion()
    {
        return conprogramacion;
    }

    /**
     * Asigna la variable conprogramacion
     * 
     * @param conprogramacion
     * Variable a asignar en conprogramacion
     */
    public void setConprogramacion(boolean conprogramacion)
    {
        this.conprogramacion = conprogramacion;
    }

    /**
     * Retorna la variable cuadro
     * 
     * @return cuadro
     */
    public boolean isCuadro()
    {
        return cuadro;
    }

    /**
     * Asigna la variable cuadro
     * 
     * @param cuadro
     * Variable a asignar en cuadro
     */
    public void setCuadro(boolean cuadro)
    {
        this.cuadro = cuadro;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaProyectoInicial
     * 
     * @return listaProyectoInicial
     */
    public RegistroDataModelImpl getListaProyectoInicial()
    {
        return listaProyectoInicial;
    }

    /**
     * Asigna la lista listaProyectoInicial
     * 
     * @param listaProyectoInicial
     * Variable a asignar en listaProyectoInicial
     */
    public void setListaProyectoInicial(
        RegistroDataModelImpl listaProyectoInicial)
    {
        this.listaProyectoInicial = listaProyectoInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
