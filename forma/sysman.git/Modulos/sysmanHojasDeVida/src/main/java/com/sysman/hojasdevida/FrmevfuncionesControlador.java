/*-
 * FrmevfuncionesControlador.java
 *
 * 1.0
 *
 * 17/01/2018
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
import com.sysman.hojasdevida.enums.FrmevfuncionesControladorEnum;
import com.sysman.hojasdevida.enums.FrmevfuncionesControladorUrlEnum;
import com.sysman.hojasdevida.enums.FrmevmanualsControladorEnum;
import com.sysman.hojasdevida.enums.FrmevrequisitosControladorEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Gestion de la tabla EV_FUNCIONES
 *
 * @version 1.0, 17/01/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class FrmevfuncionesControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    // <DECLARAR_ATRIBUTOS>
    private final String compania;
    private String numero;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * listado de versiones del manual
     */
    private List<Registro> listacmbVersionManual;
    /**
     * listado de tipos de funciones
     */
    private List<Registro> listacmbTipoFuncion;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * listado de manuales por codigo
     */
    private RegistroDataModelImpl listacmbNumeroManual;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Guarda la llave del registro del manual para redireccionar al formulario frmevmanualsControlador
     */
    private Map<String, Object> ridManual;

    /**
     * permite bloquear el combo numero manual al entrar desde el formulario frmevmanualsControlador
     */
    private boolean bloqNumero;

    /**
     * nombre del manual cargado desde el formulario principal
     */
    private String nombreManual;
    /**
     * sigla del manual cargado desde el formulario principal
     */
    private String sigla;
	private RegistroDataModelImpl listaCargo;
	private String cIdDeCargo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmevfuncionesControlador
     */
    @SuppressWarnings("unchecked")
    public FrmevfuncionesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMEVFUNCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro();
            cIdDeCargo = FrmevfuncionesControladorEnum.ID_DE_CARGO.getValue();
            bloqNumero = false;
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null
                && parametrosEntrada.get("rid") != null)
            {
                ridManual = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                nombreManual = SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get(FrmevrequisitosControladorEnum.NOMBRE_MANUAL
                                                                .getValue()),
                                                "")
                                .toString();
                sigla = SysmanFunciones.nvl(parametrosEntrada
                                .get(GeneralParameterEnum.SIGLA.getName()), "")
                                .toString();
                parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
                                ridManual.get(FrmevmanualsControladorEnum.KEY_NUMERO_MANUAL
                                                .getValue()));
                parametrosListado.put(
                                FrmevmanualsControladorEnum.VERSION.getValue(),
                                ridManual.get(FrmevmanualsControladorEnum.KEY_VERSION
                                                .getValue()));
                bloqNumero = true;
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
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbNumeroManual();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbTipoFuncion();
        cargarListaCargo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
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
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.EV_FUNCIONES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
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

    
    /**
     * 
     * Carga la lista listaCargo
     *
     */
    public void cargarListaCargo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmevfuncionesControladorUrlEnum.URL463003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cIdDeCargo);
    }
    
    // <METODOS_CARGAR_LISTA>

    /**
     *
     * Carga la lista listacmbTipoFuncion
     *
     */
    public void cargarListacmbTipoFuncion()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listacmbTipoFuncion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmevfuncionesControladorUrlEnum.URL4131
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listacmbNumeroManual
     *
     */
    public void cargarListacmbNumeroManual()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevfuncionesControladorUrlEnum.URL4132
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listacmbNumeroManual = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmevfuncionesControladorEnum.EV_MANUAL
                                                            .getValue()));
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CARGO",
                        retornarString(registroAux, cIdDeCargo));
        registro.getCampos().put("NOMBRE_DEL_CARGO",
                        retornarString(registroAux, "NOMBRE_DEL_CARGO"));
    }
    
    
    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
    
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listacmbNumeroManual
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbNumeroManual(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmevfuncionesControladorEnum.NUMERO_MANUAL.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevfuncionesControladorEnum.NUMERO_MANUAL
                                                        .getValue()));
        numero = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(FrmevfuncionesControladorEnum.NUMERO_MANUAL
                                        .getValue()),
                        "").toString();

        registro.getCampos().put(GeneralParameterEnum.SIGLA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.SIGLA.getName()));
        registro.getCampos().put(
                        FrmevfuncionesControladorEnum.NOMBRE_MANUAL.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevfuncionesControladorEnum.NOMBRE_MANUAL
                                                        .getValue()));
        registro.getCampos()
                        .put(FrmevfuncionesControladorEnum.VERSION
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        FrmevfuncionesControladorEnum.VERSION
                                                                        .getValue()));

    }

    public void ejecutarrcCerrar()
    {
        String menuActual = SessionUtil.getMenuActual();
        if ("21070101".equals(menuActual))
        {
            Direccionador direccionador = new Direccionador();
            HashMap<String, Object> param = new HashMap<>();
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.FRM_EVMANUAL_CONTROLADOR
                                            .getCodigo()));
            param.put("rid", ridManual);
            direccionador.setParametros(param);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else
        {

            SessionUtil.redireccionarMenu();
        }
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
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
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
        numero = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(FrmevfuncionesControladorEnum.NUMERO_MANUAL
                                                        .getValue()),
                                        "")
                        .toString();
        if (ACCION_INSERTAR.equals(accion) && ridManual != null)
        {
            registro.getCampos()
                            .put(FrmevmanualsControladorEnum.NUMERO_MANUAL
                                            .getValue(),
                                            ridManual.get(FrmevmanualsControladorEnum.KEY_NUMERO_MANUAL
                                                            .getValue()));
            registro.getCampos()
                            .put(FrmevmanualsControladorEnum.VERSION
                                            .getValue(),
                                            ridManual.get(FrmevmanualsControladorEnum.KEY_VERSION
                                                            .getValue()));

            registro.getCampos().put(GeneralParameterEnum.SIGLA.getName(),
                            sigla);
            registro.getCampos()
                            .put(FrmevrequisitosControladorEnum.NOMBRE_MANUAL
                                            .getValue(), nombreManual);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        long consecutivo = 1;
        try
        {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            FrmevfuncionesControladorEnum.EV_FUNCIONES
                                            .getValue(),
                            SysmanFunciones.concatenar(
                                            "COMPANIA = ''" + compania,
                                            "'' AND NUMERO_MANUAL = ''",
                                            SysmanFunciones.nvl(
                                                            registro.getCampos()
                                                                            .get(FrmevfuncionesControladorEnum.NUMERO_MANUAL
                                                                                            .getValue()),
                                                            "").toString(),
                                            "'' AND VERSION = ''",
                                            SysmanFunciones.nvl(
                                                            registro.getCampos()
                                                                            .get(FrmevfuncionesControladorEnum.VERSION
                                                                                            .getValue()),
                                                            "").toString(),
                                            "'' AND TIPO_FUNCION = ''",
                                            SysmanFunciones.nvl(
                                                            registro.getCampos()
                                                                            .get(FrmevfuncionesControladorEnum.TIPO_FUNCION
                                                                                            .getValue()),
                                                            "").toString(),
                                            "''")

            ,
                            GeneralParameterEnum.CONSECUTIVO.getName());

            registro.getCampos().put(
                            GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.SIGLA.getName());
        registro.getCampos().remove(FrmevfuncionesControladorEnum.NOMBRE_CARGO.getValue());
        registro.getCampos().remove(
                        FrmevfuncionesControladorEnum.NOMBRE_MANUAL.getValue());
        if (ACCION_MODIFICAR.equals(accion))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del registro
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
     * Retorna la lista listacmbVersionManual
     *
     * @return listacmbVersionManual
     */
    public List<Registro> getListacmbVersionManual()
    {
        return listacmbVersionManual;
    }

    /**
     * Asigna la lista listacmbVersionManual
     *
     * @param listacmbVersionManual
     * Variable a asignar en listacmbVersionManual
     */
    public void setListacmbVersionManual(List<Registro> listacmbVersionManual)
    {
        this.listacmbVersionManual = listacmbVersionManual;
    }

    /**
     * Retorna la lista listacmbTipoFuncion
     *
     * @return listacmbTipoFuncion
     */
    public List<Registro> getListacmbTipoFuncion()
    {
        return listacmbTipoFuncion;
    }

    /**
     * Asigna la lista listacmbTipoFuncion
     *
     * @param listacmbTipoFuncion
     * Variable a asignar en listacmbTipoFuncion
     */
    public void setListacmbTipoFuncion(List<Registro> listacmbTipoFuncion)
    {
        this.listacmbTipoFuncion = listacmbTipoFuncion;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbNumeroManual
     *
     * @return listacmbNumeroManual
     */
    public RegistroDataModelImpl getListacmbNumeroManual()
    {
        return listacmbNumeroManual;
    }

    /**
     * Asigna la lista listacmbNumeroManual
     *
     * @param listacmbNumeroManual
     * Variable a asignar en listacmbNumeroManual
     */
    public void setListacmbNumeroManual(
        RegistroDataModelImpl listacmbNumeroManual)
    {
        this.listacmbNumeroManual = listacmbNumeroManual;
    }

    public String getNumero()
    {
        return numero;
    }

    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    public Map<String, Object> getRidManual()
    {
        return ridManual;
    }

    public void setRidManual(Map<String, Object> ridManual)
    {
        this.ridManual = ridManual;
    }

    public boolean isBloqNumero()
    {
        return bloqNumero;
    }

    public void setBloqNumero(boolean bloqNumero)
    {
        this.bloqNumero = bloqNumero;
    }

    public String getNombreManual()
    {
        return nombreManual;
    }

    public void setNombreManual(String nombreManual)
    {
        this.nombreManual = nombreManual;
    }

    public String getSigla()
    {
        return sigla;
    }

    public void setSigla(String sigla)
    {
        this.sigla = sigla;
    }

	/**
	 * @return the listaCargo
	 */
	public RegistroDataModelImpl getListaCargo() {
		return listaCargo;
	}

	/**
	 * @param listaCargo the listaCargo to set
	 */
	public void setListaCargo(RegistroDataModelImpl listaCargo) {
		this.listaCargo = listaCargo;
	}

    
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
