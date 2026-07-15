/*-
 * SubPermRemunsControlador.java
 *
 * 1.0
 * 
 * 07/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.SubPermRemunsControladorEnum;
import com.sysman.hojasdevida.enums.SubPermRemunsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Permite configurar los permisos remunerados
 *
 * @version 1.0, 07/02/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class SubPermRemunsControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listalicencia;
    private List<Registro> listaTipoActo;
    private Map<String, Object> parametrosEntrada;
    private Map<String, Object> ridR;
    private String idEmpleado;
    private List<Registro> listaPeriodo;
    private List<Registro> listaMes;
    private List<Registro> listaAnio;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de SubPermRemunsControlador
     */
    public SubPermRemunsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBPERMRENUM_CONTROLADOR.getCodigo();
            validarPermisos();

            parametrosEntrada = SessionUtil.getFlash();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>

            if (parametrosEntrada != null)
            {
                ridR = (Map<String, Object>) parametrosEntrada.get("rid");
                idEmpleado = (String) parametrosEntrada.get("idEmpleado");
            }
            // <INI_ADICIONAL>
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
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListalicencia();
        cargarListaTipoActo();
        cargarListaAnio();
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
        tabla = GenericUrlEnum.LICENCIAS.getTable();
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(SubPermRemunsControladorEnum.PARAM0.getValue(), idEmpleado);
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubPermRemunsControladorUrlEnum.URL0002.getValue());
        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubPermRemunsControladorUrlEnum.URL0012.getValue());
        urlActualizacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubPermRemunsControladorUrlEnum.URL0004.getValue());
        urlEliminacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubPermRemunsControladorUrlEnum.URL0005.getValue());
        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubPermRemunsControladorUrlEnum.URL0008.getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listalicencia
     */
    public void cargarListalicencia()
    {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listalicencia = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(SubPermRemunsControladorUrlEnum.URL0006.getValue())
                                            .getUrl(),
                            parametros));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), "1");
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));
        param.put(GeneralParameterEnum.MES.getName(),
                        registro.getCampos().get("MES"));

        try
        {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubPermRemunsControladorUrlEnum.URL0011
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnio()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnio()
    {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubPermRemunsControladorUrlEnum.URL0009
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaMes()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), "1");
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));

        try
        {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubPermRemunsControladorUrlEnum.URL0010
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaTipoActo
     */
    public void cargarListaTipoActo()
    {
        try
        {
            listaTipoActo = RegistroConverter
                            .toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(SubPermRemunsControladorUrlEnum.URL0007.getValue()).getUrl(),
                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
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
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaMes();
        cargarListaPeriodo();
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if (ACCION_INSERTAR.equals(accion))
        {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
            registro.getCampos().remove("CODIGO");
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if (ACCION_MODIFICAR.equals(accion))
        {
            registro.getCampos().remove("NUMERO_DCTO");
            registro.getCampos().remove("ID_DE_EMPLEADO");
            registro.getCampos().remove("COMPANIA");
            registro.getCampos().remove("CODIGO");
            registro.getCampos().remove("ID_DE_PROCESO");
            registro.getCampos().remove("SUCURSAL");
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void ejecutarrcCerrar()
    {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.OTROSITEMS_CONTROLADOR.getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listalicencia
     * 
     * @return listalicencia
     */
    public List<Registro> getListalicencia()
    {
        return listalicencia;
    }

    /**
     * Asigna la lista listalicencia
     * 
     * @param listalicencia
     * Variable a asignar en listalicencia
     */
    public void setListalicencia(List<Registro> listalicencia)
    {
        this.listalicencia = listalicencia;
    }

    /**
     * Retorna la lista listaTipoActo
     * 
     * @return listaTipoActo
     */
    public List<Registro> getListaTipoActo()
    {
        return listaTipoActo;
    }

    /**
     * Asigna la lista listaTipoActo
     * 
     * @param listaTipoActo
     * Variable a asignar en listaTipoActo
     */
    public void setListaTipoActo(List<Registro> listaTipoActo)
    {
        this.listaTipoActo = listaTipoActo;
    }

    public List<Registro> getListaPeriodo()
    {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo)
    {
        this.listaPeriodo = listaPeriodo;
    }

    public List<Registro> getListaMes()
    {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes)
    {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaAnio()
    {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio)
    {
        this.listaAnio = listaAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
