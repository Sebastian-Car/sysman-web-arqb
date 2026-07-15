/*-
 * DesviacionhistoriasControlador.java
 *
 * 1.0
 * 
 * 24/11/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.DesviacionhistoriasControladorEnum;
import com.sysman.serviciospublicos.enums.DesviacionhistoriasControladorUrlEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase que controla la interfaz del formulario que muestra los
 * procesos hist�ricos de desviaci�n.
 *
 * @version 1.0, 24/11/2016
 * @author vmolano
 * 
 * @version 2, 22/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla y de datos.
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */

@ManagedBean
@ViewScoped
public class DesviacionhistoriasControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar la cadena "SP_DESVIACIONES"
     */
    private final String tabla1;

    /**
     * Constante definida para almacenar la cadena
     * "SP_DESVIACIONES_CARTA"
     */
    private final String tabla2;
    /**
     * Constante definida para almacenar la cadena "SUBCLASE"
     */
    private final String subClase;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable que recibe por flash el ciclo del suscriptor actual
     */
    private String cicloActual;

    /**
     * Variable que recibe por flash el c�digo de ruta del suscriptor
     * actual.
     */
    private String codigoRuta;

    /**
     * Variable que almacena el c�digo de la desviaci�n actualmente
     * seleccionada.
     */
    private String desviacionActual;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable donde se carga el listado de cartas para edici�n.
     */
    private RegistroDataModelImpl listacmbSubClase;
    /**
     * Variable donde se carga el listado de cartas.
     */
    private RegistroDataModelImpl listacmbSubClaseE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Variable donde se carga la consulta dle sub de cartas.
     */
    private List<Registro> listaSubcartas;
    /**
     * Variable donde se carga la consulta del sub de desviaciones.
     */
    private List<Registro> listaSubdesviaciones;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario SubCartas
     */
    private Registro registroSubSubCartas;
    /**
     * Atributo de referencia para el subformulario SubDesviaciones
     */
    private Registro registroSubSubDesviaciones;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de DesviacionhistoriasControlador
     */
    public DesviacionhistoriasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        tabla1 = DesviacionhistoriasControladorEnum.TABLA1.getValue();
        tabla2 = DesviacionhistoriasControladorEnum.TABLA2.getValue();
        subClase = "SUBCLASE";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DESVIACIONHISTORIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubSubCartas = new Registro(new HashMap<String, Object>());
            registroSubSubDesviaciones = new Registro(
                            new HashMap<String, Object>());

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                cicloActual = (String) parametrosEntrada.get("ciclo");
                codigoRuta = (String) parametrosEntrada.get("codigoRuta");
            }
            else
            {
                SessionUtil.redireccionarMenu();
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbSubClase();
        cargarListacmbSubClaseE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubcartas();
        cargarListaSubdesviaciones();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubcartas = null;
        listaSubdesviaciones = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        tabla = "";
        iniciarListasSub();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    /**
     * 
     * Carga la lista listaSubcartas
     *
     * Son las cartas asociadas a un determinado proceso de
     * desviaci�n.
     */
    public void cargarListaSubcartas()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(DesviacionhistoriasControladorEnum.PARAM0.getValue(),
                            desviacionActual);
            listaSubcartas = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            DesviacionhistoriasControladorUrlEnum.URL7198
                                                                                            .getValue())
                                                            .getUrl(),
                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            tabla2));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaSubdesviaciones
     *
     * Son todos lode proceso de desviaci�n hist�ricos que ha tenido
     * el suscriptor.
     */
    public void cargarListaSubdesviaciones()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), cicloActual);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

            listaSubdesviaciones = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DesviacionhistoriasControladorUrlEnum.URL8562
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            tabla1));
        }
        catch (SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbSubClase
     *
     */
    public void cargarListacmbSubClase()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DesviacionhistoriasControladorUrlEnum.URL9681
                                                        .getValue());
        listacmbSubClase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, subClase);
    }

    /**
     * 
     * Carga la lista listacmbSubClase
     *
     */
    public void cargarListacmbSubClaseE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DesviacionhistoriasControladorUrlEnum.URL9681
                                                        .getValue());
        listacmbSubClaseE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, subClase);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbSubClase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbSubClase(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubCartas.getCampos().put(subClase,
                        registroAux.getCampos().get(subClase));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbSubClase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbSubClaseE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(subClase).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton btMas
     * 
     * Se actualiza el subFormulario de cartas con el proceso
     * actualmente seleccionado.
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirbtMas(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        desviacionActual = reg.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName())
                        .toString();
        cargarListaSubcartas();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Subcartas
     */
    public void agregarRegistroSubSubcartas()
    {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo de edicion del formulario Subcartas
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubcartas(RowEditEvent event)
    {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo de eliminacion del formulario Subcartas
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubcartas(Registro reg)
    {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subcartas
     */
    public void cancelarEdicionSubcartas()
    {
        cargarListaSubcartas();
        cargarListaSubdesviaciones();
    }

    /**
     * Metodo de insercion del formulario Subdesviaciones
     */
    public void agregarRegistroSubSubdesviaciones()
    {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo de edicion del formulario Subdesviaciones
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubdesviaciones(RowEditEvent event)
    {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo de eliminacion del formulario Subdesviaciones
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubdesviaciones(Registro reg)
    {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subdesviaciones
     */
    public void cancelarEdicionSubdesviaciones()
    {
        cargarListaSubdesviaciones();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listacmbSubClase
     * 
     * @return listacmbSubClase
     */

    public RegistroDataModelImpl getListacmbSubClase()
    {
        return listacmbSubClase;
    }

    public void setListacmbSubClase(RegistroDataModelImpl listacmbSubClase)
    {
        this.listacmbSubClase = listacmbSubClase;
    }

    public RegistroDataModelImpl getListacmbSubClaseE()
    {
        return listacmbSubClaseE;
    }

    public void setListacmbSubClaseE(RegistroDataModelImpl listacmbSubClaseE)
    {
        this.listacmbSubClaseE = listacmbSubClaseE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubcartas
     * 
     * @return listaSubcartas
     */
    public List<Registro> getListaSubcartas()
    {
        return listaSubcartas;
    }

    /**
     * Asigna la lista listaSubcartas
     * 
     * @param listaSubcartas
     * Variable a asignar en listaSubcartas
     */
    public void setListaSubcartas(List<Registro> listaSubcartas)
    {
        this.listaSubcartas = listaSubcartas;
    }

    /**
     * Retorna la lista listaSubdesviaciones
     * 
     * @return listaSubdesviaciones
     */
    public List<Registro> getListaSubdesviaciones()
    {
        return listaSubdesviaciones;
    }

    /**
     * Asigna la lista listaSubdesviaciones
     * 
     * @param listaSubdesviaciones
     * Variable a asignar en listaSubdesviaciones
     */
    public void setListaSubdesviaciones(List<Registro> listaSubdesviaciones)
    {
        this.listaSubdesviaciones = listaSubdesviaciones;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubSubCartas
     * 
     * @return registroSubSubCartas
     */
    public Registro getRegistroSubSubCartas()
    {
        return registroSubSubCartas;
    }

    /**
     * Asigna el objeto registroSubSubCartas
     * 
     * @param registroSubSubCartas
     * Variable a asignar en registroSubSubCartas
     */
    public void setRegistroSubSubCartas(Registro registroSubSubCartas)
    {
        this.registroSubSubCartas = registroSubSubCartas;
    }

    /**
     * Retorna el objeto registroSubSubDesviaciones
     * 
     * @return registroSubSubDesviaciones
     */
    public Registro getRegistroSubSubDesviaciones()
    {
        return registroSubSubDesviaciones;
    }

    /**
     * Asigna el objeto registroSubSubDesviaciones
     * 
     * @param registroSubSubDesviaciones
     * Variable a asignar en registroSubSubDesviaciones
     */
    public void setRegistroSubSubDesviaciones(
        Registro registroSubSubDesviaciones)
    {
        this.registroSubSubDesviaciones = registroSubSubDesviaciones;
    }
    // </SET_GET_ADICIONALES>
}
