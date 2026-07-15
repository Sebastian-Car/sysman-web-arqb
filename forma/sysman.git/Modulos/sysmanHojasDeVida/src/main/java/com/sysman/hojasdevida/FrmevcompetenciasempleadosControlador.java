/*-
 * FrmevcompetenciasempleadosControlador.java
 *
 * 1.0
 * 
 * 29/01/2018
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmevcompetenciasempleadosControladorEnum;
import com.sysman.hojasdevida.enums.FrmevcompetenciasempleadosControladorUrlEmun;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Este controlador me permite gestionar la informacion contenida en
 * el formulario FRM_COMPETENCIAS_EMPLEADOS
 *
 * @version 1.0, 29/01/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmevcompetenciasempleadosControlador
                extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String cerrar;

    private Map<String, Object> ridDatos;
    private Map<String, Object> parametrosEntrada;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Varible que me almacena los datos de la lista listaId_empleado
     */
    private RegistroDataModelImpl listaIdempleado;
    /**
     * Varible que me almacena los datos de la lista
     * listaTipo_competencia
     */
    private RegistroDataModelImpl listaTipocompetencia;
    /**
     * Varible que me almacena los datos de la lista listadependencia
     */
    private RegistroDataModelImpl listadependencia;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /*
     * Constante que sirve para almacenar el consecutivo del codigo en
     * la tabla ev_competencias_empleado
     */
    private long consecutivo;
    /*
     * Constante que sirve para identificar si el usuario selecciono
     * un tipo de competencia, y poder bloquear los campos numero de
     * manual, competencia y version
     */
    private boolean seleccionarTipo;
    /*
     * Constante que sirve para identificar el registro actual en
     * edicion
     */
    private int indice;
    /*
     * Esta opcion la utilizo para saber que tipo de competencia e
     * selecciono
     */
    private String tipoSeleccionado;
    /*
     * Constante que sirve para capturar los datos de la fila que
     * estoy editando
     */
    private String auxiliarEdicion;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmevcompetenciasempleadosControlador
     */
    public FrmevcompetenciasempleadosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        consecutivo = 0;
        seleccionarTipo = false;
        indice = 0;
        auxiliarEdicion = "";
        tipoSeleccionado = "";
        parametrosEntrada = SessionUtil.getFlash();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_EV_COMPETENCIAS_EMPLEADO_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null)
            {
                ridDatos = (Map<String, Object>) parametrosEntrada.get("rid");
                cerrar = (String) parametrosEntrada.get("cerrar");
            }
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.EV_COMPETENCIAS_EMPLEADO;
        asignarOrigenDatos();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        abrirFormulario();

    }

    @Override
    public void iniciarListas()
    {
        cargarListaIdempleado();
        cargarListaTipocompetencia();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
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
     * Carga la lista listaId_empleado
     *
     */
    public void cargarListaIdempleado()
    {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevcompetenciasempleadosControladorUrlEmun.URL102
                                                        .getValue());

        listaIdempleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmevcompetenciasempleadosControladorEnum.ID_DE_EMPLEADO
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaTipo_competencia
     *
     */
    public void cargarListaTipocompetencia()
    {
        try
        {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmevcompetenciasempleadosControladorUrlEmun.URL104
                                                            .getValue());

            listaTipocompetencia = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(urlConexionCache,
                                            "EV_COMPETENCIAS"));
        }
        catch (SysmanException e)
        {
            // Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listadependencia
     *
     */
    public void cargarListadependencia()
    {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmevcompetenciasempleadosControladorEnum.ID_DE_EMPLEADO
                        .getValue(), registro.getCampos()
                                        .get(FrmevcompetenciasempleadosControladorEnum.ID_EMPLEADO
                                                        .getValue()));

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevcompetenciasempleadosControladorUrlEmun.URL103
                                                        .getValue());

        listadependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmevcompetenciasempleadosControladorEnum.CODIGO
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaId_empleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdempleado(SelectEvent event)
    {
        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.DEPENDENCIA
                                        .getValue(), null);

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.ID_EMPLEADO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevcompetenciasempleadosControladorEnum.ID_DE_EMPLEADO
                                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.NOMBREEMPLEADO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBRECOMPLETO"));
        cargarListadependencia();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipo_competencia
     *
     * En este metodo se realizo la comparacion de si se habia
     * seleccionado tipo de competencia y basado en esa eleccion se
     * determina si se deben bloquear los campos Numero_Manual,
     * Competencia y Version
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipocompetencia(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.TIPO_COMPETENCIA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevcompetenciasempleadosControladorEnum.TIPO_COMPETENCIA
                                                                        .getValue()));
        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.NOMBRETIPOCOMPETENCIA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBRE"));

        seleccionarTipo = true;

        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.NUMERO_MANUAL
                                        .getValue(), registroAux.getCampos()
                                                        .get(FrmevcompetenciasempleadosControladorEnum.NUMERO_MANUAL
                                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.VERSION
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevcompetenciasempleadosControladorEnum.VERSION
                                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.ID_COMPETENCIA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevcompetenciasempleadosControladorEnum.CONSECUTIVO
                                                                        .getValue()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.DEPENDENCIA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevcompetenciasempleadosControladorEnum.CODIGO
                                                                        .getValue()));
        registro.getCampos()
                        .put(FrmevcompetenciasempleadosControladorEnum.NOMBREDEPENDENCIA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBRE"));
    }

    /**
     * Metodo ejecutado al cambiar el control Id_empleado en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarIdempleadoC(int rowNum)
    {
        // DOCUMENTACION
    }

    /**
     * Metodo ejecutado al cambiar el control Tipo_competencia en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipocompetenciaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>
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
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if (accion.equals(ACCION_INSERTAR))
        {
            registro.getCampos()
                            .remove(FrmevcompetenciasempleadosControladorEnum.NOMBREDEPENDENCIA
                                            .getValue());
            registro.getCampos()
                            .remove(FrmevcompetenciasempleadosControladorEnum.NOMBRETIPOCOMPETENCIA
                                            .getValue());
            registro.getCampos()
                            .remove(FrmevcompetenciasempleadosControladorEnum.NOMBREEMPLEADO
                                            .getValue());
        }

        try
        {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            GenericUrlEnum.EV_COMPETENCIAS_EMPLEADO.getTable(),
                            SysmanFunciones.concatenar("COMPANIA=''", compania,
                                            "''"),
                            GeneralParameterEnum.CONSECUTIVO.getName());

            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
        }
        catch (SystemException e)
        {
            // Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return VARIABLE
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
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(FrmevcompetenciasempleadosControladorEnum.NOMBREEMPLEADO
                                            .getValue());
            registro.getCampos()
                            .remove(FrmevcompetenciasempleadosControladorEnum.NOMBREDEPENDENCIA
                                            .getValue());
            registro.getCampos()
                            .remove(FrmevcompetenciasempleadosControladorEnum.NOMBRETIPOCOMPETENCIA
                                            .getValue());

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
        /*
         * FR1658-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
         * 'AuditarRegistro Me.Numero End Sub
         */
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
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatos);

        if ("1".equals(cerrar))
        {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR
                                            .getCodigo()));

            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else
        {
            SessionUtil.redireccionar("/menu.sysman");
        }
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaId_empleado
     * 
     * @return listaId_empleado
     */
    public RegistroDataModelImpl getListaIdempleado()
    {
        return listaIdempleado;
    }

    /**
     * Asigna la lista listaId_empleado
     * 
     * @param listaIdempleado
     * Variable a asignar en listaId_empleado
     */
    public void setListaIdempleado(RegistroDataModelImpl listaIdempleado)
    {
        this.listaIdempleado = listaIdempleado;
    }

    /**
     * Retorna la lista listaTipo_competencia
     * 
     * @return listaTipo_competencia
     */
    public RegistroDataModelImpl getListaTipocompetencia()
    {
        return listaTipocompetencia;
    }

    /**
     * Asigna la lista listaTipo_competencia
     * 
     * @param listaTipocompetencia
     * Variable a asignar en listaTipo_competencia
     */
    public void setListaTipocompetencia(
        RegistroDataModelImpl listaTipocompetencia)
    {
        this.listaTipocompetencia = listaTipocompetencia;
    }

    /**
     * Retorna la lista listadependencia
     * 
     * @return listadependencia
     */
    public RegistroDataModelImpl getListadependencia()
    {
        return listadependencia;
    }

    /**
     * Asigna la lista listadependencia
     * 
     * @param listadependencia
     * Variable a asignar en listadependencia
     */
    public void setListadependencia(RegistroDataModelImpl listadependencia)
    {
        this.listadependencia = listadependencia;
    }

    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();

        auxiliarEdicion = SysmanFunciones
                        .toString(registro.getCampos()
                                        .get(FrmevcompetenciasempleadosControladorEnum.TIPO_COMPETENCIA
                                                        .getValue()));

        seleccionarTipo = true;

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

    public boolean isseleccionarTipo()
    {
        return seleccionarTipo;
    }

    public void setseleccionarTipo(boolean seleccionarTipo)
    {
        this.seleccionarTipo = seleccionarTipo;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public String getAuxiliarEdicion()
    {
        return auxiliarEdicion;
    }

    public void setAuxiliarEdicion(String auxiliarEdicion)
    {
        this.auxiliarEdicion = auxiliarEdicion;
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // Auto-generated method stub

    }

    @Override
    public void iniciarListasSub()
    {
        // Auto-generated method stub

    }
}
