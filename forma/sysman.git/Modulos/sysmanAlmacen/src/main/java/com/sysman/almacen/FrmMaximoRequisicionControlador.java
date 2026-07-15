/*-
 * FrmMaximoRequisicionControlador.java
 *
 * 1.0
 * 
 * 21/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.FrmMaximoRequisicionEnum;
import com.sysman.almacen.enums.FrmMaximoRequisicionEnumUrl;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Esta clase permite limitar el tope maximo de de requisicion
 * disponible para un empleado, este controlador se diseño y creo
 * desde cero.
 *
 * @version 1.0, 21/05/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmMaximoRequisicionControlador extends BeanBaseDatosAcmeImpl
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
    /**
     * Variable que almacena los datos del ltado listacmbAno
     */
    private List<Registro> listacmbAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable que almacena los datos del ltado listacmbDependencia
     */
    private RegistroDataModelImpl listacmbDependencia;
    /**
     * Variable que almacena los datos del ltado listacmbResponsable
     */
    private RegistroDataModelImpl listacmbResponsable;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmMaximoRequisicionControlador
     */
    public FrmMaximoRequisicionControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMMAXIMOREQUISICION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
        cargarListacmbResponsable();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbAno();
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
        enumBase = GenericUrlEnum.D_DEPENDENCIA_RESPONSABLE;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbAno
     *
     */
    public void cargarListacmbAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmMaximoRequisicionEnumUrl.URL0003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listacmbDependencia
     *
     */
    public void cargarListacmbDependencia()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmMaximoRequisicionEnumUrl.URL0001
                                                        .getValue());

        listacmbDependencia = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacmbResponsable
     *
     */
    public void cargarListacmbResponsable()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmMaximoRequisicionEnumUrl.URL0002
                                                        .getValue());

        listacmbResponsable = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        "NIT");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPENDENCIA",
                        registroAux.getCampos().get("CODIGO"));

        registro.getCampos().put(
                        FrmMaximoRequisicionEnum.NOMBRE_DEPENDENCIA.getValue(),
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbResponsable
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbResponsable(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RESPONSABLE",
                        registroAux.getCampos().get("NIT"));

        registro.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get("SUCURSAL"));

        registro.getCampos().put(
                        FrmMaximoRequisicionEnum.NOMBRE_RESPONSABLE.getValue(),
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put("DEPENDENCIA", null);
        registro.getCampos().put(
                        FrmMaximoRequisicionEnum.NOMBRE_DEPENDENCIA.getValue(),
                        null);

        cargarListacmbDependencia();
    }

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
     * 
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (!accion.equals(ACCION_INSERTAR))
        {
            registro.getCampos().put("VLR_REQUISICIONES_ACUM",
                            obtenerValorMaximo()[0]);
        }
        else
        {
            registro.getCampos().put("VLR_REQUISICIONES_ACUM",
                            0);
        }

        // </CODIGO_DESARROLLADO>
    }

    public int[] obtenerValorMaximo()
    {
        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.COMPANIA
                                                        .getName()));
        parametros.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));

        parametros.put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.RESPONSABLE
                                                        .getName()));

        parametros.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));

        parametros.put(GeneralParameterEnum.SUCURSAL.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        Registro rsValorAcum;
        Registro rsValorMax;
        int[] valores = new int[2];
        valores[0] = 0;
        valores[1] = 0;
        try
        {
            rsValorAcum = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmMaximoRequisicionEnumUrl.URL0004
                                                                            .getValue())
                                            .getUrl(), parametros));

            rsValorMax = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmMaximoRequisicionEnumUrl.URL0005
                                                                            .getValue())
                                            .getUrl(), parametros));

            valores[0] = Integer.parseInt(SysmanFunciones.toString(rsValorAcum
                            .getCampos()
                            .get("VLR_ACUMULADO")));

            valores[1] = Integer.parseInt(SysmanFunciones.toString(rsValorMax
                            .getCampos()
                            .get("VLR_MAX")));

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return valores;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_INSERTAR))
        {
            registro.getCampos()
                            .remove(FrmMaximoRequisicionEnum.NOMBRE_RESPONSABLE
                                            .getValue());
            registro.getCampos()
                            .remove(FrmMaximoRequisicionEnum.NOMBRE_DEPENDENCIA
                                            .getValue());
        }
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     * 
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos()
                            .remove(FrmMaximoRequisicionEnum.NOMBRE_RESPONSABLE
                                            .getValue());
            registro.getCampos()
                            .remove(FrmMaximoRequisicionEnum.NOMBRE_DEPENDENCIA
                                            .getValue());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        cargarRegistro();
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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbAno
     * 
     * @return listacmbAno
     */
    public List<Registro> getListacmbAno()
    {
        return listacmbAno;
    }

    /**
     * Asigna la lista listacmbAno
     * 
     * @param listacmbAno
     * Variable a asignar en listacmbAno
     */
    public void setListacmbAno(List<Registro> listacmbAno)
    {
        this.listacmbAno = listacmbAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbDependencia
     * 
     * @return listacmbDependencia
     */
    public RegistroDataModelImpl getListacmbDependencia()
    {
        return listacmbDependencia;
    }

    /**
     * Asigna la lista listacmbDependencia
     * 
     * @param listacmbDependencia
     * Variable a asignar en listacmbDependencia
     */
    public void setListacmbDependencia(
        RegistroDataModelImpl listacmbDependencia)
    {
        this.listacmbDependencia = listacmbDependencia;
    }

    /**
     * Retorna la lista listacmbResponsable
     * 
     * @return listacmbResponsable
     */
    public RegistroDataModelImpl getListacmbResponsable()
    {
        return listacmbResponsable;
    }

    /**
     * Asigna la lista listacmbResponsable
     * 
     * @param listacmbResponsable
     * Variable a asignar en listacmbResponsable
     */
    public void setListacmbResponsable(
        RegistroDataModelImpl listacmbResponsable)
    {
        this.listacmbResponsable = listacmbResponsable;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
