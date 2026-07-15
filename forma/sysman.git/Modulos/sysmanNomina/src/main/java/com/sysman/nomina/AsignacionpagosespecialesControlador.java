/*-
 * AsignacionpagosespecialesControlador.java
 *
 * 1.0
 *
 * 02/04/2019
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.AsignacionpagosespecialesControladorEnum;
import com.sysman.nomina.enums.AsignacionpagosespecialesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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
 * @version 1.0, 02/04/2019
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class AsignacionpagosespecialesControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
    
     */
    private List<Registro> listaCbCargo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCbDetalleEmpl;

    private RegistroDataModelImpl listaCbDetalleEmplE;

    private RegistroDataModelImpl listaCbEmpleado;

    private RegistroDataModelImpl listaCbEmpleadoE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    private RegistroDataModelImpl listaCbDetalleCargo;
    private RegistroDataModelImpl listaCbDetalleCargoE;

    private RegistroDataModelImpl listaCbDetalleCat;

    private RegistroDataModelImpl listaCbDetalleCatE;

    private RegistroDataModelImpl listaCbCategoria;

    private RegistroDataModelImpl listaCbCategoriaE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>

    private RegistroDataModelImpl listaSfempleado;

    private RegistroDataModelImpl listaSfcargo;

    private RegistroDataModelImpl listaSfcategoria;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario SfEmpleado
     */
    private Registro registroSubSfEmpleado;
    /**
     * Atributo de referencia para el subformulario SfCargo
     */
    private Registro registroSubSfCargo;
    /**
     * Atributo de referencia para el subformulario SfCategoria
     */
    private Registro registroSubSfCategoria;

    private int indiceSfempleado;
    private int indiceSfcargo;
    private int indiceSfcategoria;
    private int anoCategoria = 0;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de AsignacionpagosespecialesControlador
     */
    public AsignacionpagosespecialesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 2054;
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubSfEmpleado = new Registro(new HashMap<String, Object>());
            registroSubSfCargo = new Registro(new HashMap<String, Object>());
            registroSubSfCategoria = new Registro(new HashMap<String, Object>());
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

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        if ("EMP".equals(registro.getCampos().get(AsignacionpagosespecialesControladorEnum.APLICACOD.getValue()).toString()))
        {
            cargarListaSfempleado();
            cargarListaCbDetalleEmpl();
            cargarListaCbDetalleEmplE();
            cargarListaCbEmpleado();
            cargarListaCbEmpleadoE();
        }
        else if ("CTG".equals(registro.getCampos().get(AsignacionpagosespecialesControladorEnum.APLICACOD.getValue()).toString()))
        {
            cargarListaSfcategoria();
            cargarListaCbDetalleCat();
            cargarListaCbDetalleCatE();
            cargarListaCbCategoria();
            cargarListaCbCategoriaE();
        }
        else if ("CRG".equals(registro.getCampos().get(AsignacionpagosespecialesControladorEnum.APLICACOD.getValue()).toString()))
        {
            cargarListaSfcargo();
            cargarListaCbDetalleCargo();
            cargarListaCbDetalleCargoE();
            cargarListaCbCargo();
        }

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
        listaSfempleado = null;
        listaSfcargo = null;
        listaSfcategoria = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.PAGOESPECIAL;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     *
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        AsignacionpagosespecialesControladorUrlEnum.URL0017.getValue());

        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        AsignacionpagosespecialesControladorUrlEnum.URL0018.getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     *
     * Carga la lista listaSfempleado
     *
     *
     */
    public void cargarListaSfempleado()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AsignacionpagosespecialesControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AsignacionpagosespecialesControladorEnum.PAGO.getValue(), registro
                        .getCampos().get(GeneralParameterEnum.CODIGO.getName()));

        try
        {
            listaSfempleado = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            AsignacionpagosespecialesControladorEnum.PAGOESPECIAL_PERSONAL.getValue()));
        }
        catch (SysmanException e)
        {
            Logger.getLogger(AsignacionpagosespecialesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    /**
     *
     * Carga la lista listaSfcargo
     *
     *
     */
    public void cargarListaSfcargo()
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0007
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(AsignacionpagosespecialesControladorEnum.PAGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

            listaSfcargo = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            AsignacionpagosespecialesControladorEnum.PAGOESPECIALCARGOS.getValue()));

        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaSfcategoria
     *
     *
     */
    public void cargarListaSfcategoria()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AsignacionpagosespecialesControladorUrlEnum.URL0010
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AsignacionpagosespecialesControladorEnum.PAGO.getValue(),
                        registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

        try
        {
            listaSfcategoria = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            AsignacionpagosespecialesControladorEnum.PAGOESPECIALCATEGORIA.getValue()));
        }
        catch (SysmanException e)
        {
            Logger.getLogger(AsignacionpagosespecialesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCbCargo
     *
     *
     */
    public void cargarListaCbCargo()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaCbCargo = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            AsignacionpagosespecialesControladorUrlEnum.URL0014
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo para cargar las listas de los combos detalles.
     *
     * @return RegistroDataModelImpl con la lista de detalles
     */

    public RegistroDataModelImpl cargarDetalleSub()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AsignacionpagosespecialesControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AsignacionpagosespecialesControladorEnum.PAGO.getValue(),
                        registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

        return new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());
    }

    /**
     *
     * Carga la lista listaCbDetalleEmpl
     *
     *
     */
    public void cargarListaCbDetalleEmpl()
    {
        listaCbDetalleEmpl = cargarDetalleSub();

    }

    /**
     *
     * Carga la lista listaCbDetalleEmpl
     *
     *
     */
    public void cargarListaCbDetalleEmplE()
    {
        listaCbDetalleEmplE = listaCbDetalleEmpl;
    }

    /**
     *
     * Carga la lista listaCbEmpleado
     *
     *
     */
    public void cargarListaCbEmpleado()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AsignacionpagosespecialesControladorUrlEnum.URL0015
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCbEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        AsignacionpagosespecialesControladorEnum.ID_DE_EMPLEADO.getValue());
    }

    /**
     *
     * Carga la lista listaCbEmpleado
     *
     *
     */
    public void cargarListaCbEmpleadoE()
    {
        listaCbEmpleadoE = listaCbEmpleado;
    }

    /**
     *
     * Carga la lista listaCbDetalleCargo
     *
     *
     */
    public void cargarListaCbDetalleCargo()
    {
        listaCbDetalleCargo = cargarDetalleSub();

    }

    /**
     *
     * Carga la lista listaCbDetalleCargo
     *
     *
     */
    public void cargarListaCbDetalleCargoE()
    {
        listaCbDetalleCargoE = listaCbDetalleCargo;

    }

    /**
     *
     * Carga la lista listaCbDetalleCat
     *
     *
     */
    public void cargarListaCbDetalleCat()
    {
        listaCbDetalleCat = cargarDetalleSub();
    }

    /**
     *
     * Carga la lista listaCbDetalleCat
     *
     *
     */
    public void cargarListaCbDetalleCatE()
    {
        listaCbDetalleCatE = listaCbDetalleCat;
    }

    /**
     *
     * Carga la lista listaCbCategoria
     *
     *
     */
    public void cargarListaCbCategoria()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(AsignacionpagosespecialesControladorUrlEnum.URL0016.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoCategoria);

        listaCbCategoria = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        AsignacionpagosespecialesControladorEnum.IDCATEGORIA.getValue());
    }

    /**
     *
     * Carga la lista listaCbCategoria
     *
     *
     */
    public void cargarListaCbCategoriaE()
    {
        listaCbCategoriaE = listaCbCategoria;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbDetalleEmpl
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbDetalleEmpl(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSfEmpleado.getCampos().put(AsignacionpagosespecialesControladorEnum.CODIGODETALLEPAGO.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
        registroSubSfEmpleado.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.ANO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbDetalleEmpl
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbDetalleEmplE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbEmpleado
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSfEmpleado.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.ID_DE_EMPLEADO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbEmpleado
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbEmpleadoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.ID_DE_EMPLEADO.getName()).toString();
        listaSfempleado.getDatasource().get(indiceSfempleado).getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbDetalleCargo
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbDetalleCargo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSfCargo.getCampos().put(AsignacionpagosespecialesControladorEnum.CODIGODETALLEPAGO.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
        registroSubSfCargo.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.ANO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbDetalleCargo
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbDetalleCargoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString();
        listaSfcargo.getDatasource().get(indiceSfcargo).getCampos().put(GeneralParameterEnum.ANO.getName(),
                        Integer.parseInt(registroAux.getCampos().get(GeneralParameterEnum.ANO.getName()).toString()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbDetalleCat
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbDetalleCat(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSfCategoria.getCampos().put(AsignacionpagosespecialesControladorEnum.CODIGODETALLEPAGO.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
        registroSubSfCategoria.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.ANO.getName()));
        anoCategoria = Integer.parseInt(SysmanFunciones.nvl(registroSubSfCategoria.getCampos().get(GeneralParameterEnum.ANO.getName()), "0")
                        .toString());
        cargarListaCbCategoria();
        cargarListaCbCategoriaE();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbDetalleCat
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbDetalleCatE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString();
        anoCategoria = Integer
                        .parseInt(SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.ANO.getName()), "0").toString());
        listaSfcategoria.getDatasource().get(indiceSfcategoria).getCampos().put(GeneralParameterEnum.ANO.getName(), anoCategoria);
        cargarListaCbCategoria();
        cargarListaCbCategoriaE();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbCategoria
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCategoria(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSfCategoria.getCampos().put(AsignacionpagosespecialesControladorEnum.IDCATEGORIA.getValue(),
                        registroAux.getCampos().get(AsignacionpagosespecialesControladorEnum.IDCATEGORIA.getValue()));

        registroSubSfCategoria.getCampos().put(AsignacionpagosespecialesControladorEnum.ESCALAFON.getValue(),
                        registroAux.getCampos().get(AsignacionpagosespecialesControladorEnum.CODESCALAFON.getValue()));

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbCategoria
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCategoriaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(AsignacionpagosespecialesControladorEnum.IDCATEGORIA.getValue());
        listaSfcategoria.getDatasource().get(indiceSfcategoria).getCampos().put(
                        AsignacionpagosespecialesControladorEnum.ESCALAFON.getValue(),
                        registroAux.getCampos().get(AsignacionpagosespecialesControladorEnum.CODESCALAFON.getValue()));

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>

    /**
     * Metodo de insercion del formulario Sfempleado
     *
     *
     */
    public void agregarRegistroSubSfempleado()
    {
        try
        {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0003
                                                            .getValue());

            registroSubSfEmpleado.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubSfEmpleado.getCampos().put(AsignacionpagosespecialesControladorEnum.CODIGOPAGOESP.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

            registroSubSfEmpleado.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSfEmpleado.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

            registroSubSfEmpleado.getCampos().remove("NOMBRECOMPLETO");
            registroSubSfEmpleado.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registroSubSfEmpleado.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

            requestManager.save(urlBean.getUrl(), urlBean.getMetodo(),
                            registroSubSfEmpleado.getCampos());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(AsignacionpagosespecialesControladorEnum.MENSAJEEXITOSO.getValue()));

        }
        catch (SystemException ex)
        {
            Logger.getLogger(AsignacionpagosespecialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubSfEmpleado.getCampos().clear();
        }
    }

    public void activarEdicionSfempleado(Registro reg)
    {
        indiceSfempleado = listaSfempleado.getRowIndex();
        cargarListaCbDetalleEmpl();
        cargarListaCbDetalleEmplE();
    }

    /**
     * Metodo de edicion del formulario Sfempleado
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSfempleado(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0004
                                                            .getValue());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
            reg.getCampos().remove("NOMBRECOMPLETO");
            reg.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

            requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(AsignacionpagosespecialesControladorEnum.MENSAJEMOD.getValue()));

        }

        catch (SystemException e)
        {
            Logger.getLogger(AsignacionpagosespecialesControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally
        {
            cargarListaSfempleado();
        }
    }

    /**
     * Metodo de eliminacion del formulario Sfempleado
     *
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSfempleado(Registro reg)
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0005
                                                            .getValue());
            requestManager.delete(urlBean.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSfempleado();
        }

        catch (SystemException e)
        {
            Logger.getLogger(AsignacionpagosespecialesControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Sfempleado
     *
     *
     */
    public void cancelarEdicionSfempleado()
    {
        cargarListaSfempleado();
        cargarListaSfcargo();
        cargarListaSfcategoria();
    }

    /**
     * Metodo de insercion del formulario Sfcargo
     *
     *
     */
    public void agregarRegistroSubSfcargo()
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0006
                                                            .getValue());

            registroSubSfCargo.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubSfCargo.getCampos().put(AsignacionpagosespecialesControladorEnum.CODIGOPAGOESP.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
            registroSubSfCargo.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSfCargo.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

            registroSubSfCargo.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
            registroSubSfCargo.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registroSubSfCargo.getCampos().remove("NOMBRE_DEL_CARGO");

            requestManager.save(urlBean.getUrl(), urlBean.getMetodo(),
                            registroSubSfCargo.getCampos());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(AsignacionpagosespecialesControladorEnum.MENSAJEEXITOSO.getValue()));
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubSfCargo.getCampos().clear();
        }
    }

    public void activarEdicionSfcargo(Registro reg)
    {
        indiceSfcargo = listaSfcargo.getRowIndex();
        cargarListaCbDetalleCargo();
        cargarListaCbDetalleCargoE();
    }

    /**
     * Metodo de edicion del formulario Sfcargo
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSfcargo(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0008
                                                            .getValue());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

            reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
            reg.getCampos().remove("NOMBRE_DEL_CARGO");
            reg.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

            requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(AsignacionpagosespecialesControladorEnum.MENSAJEMOD.getValue()));
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSfcargo();
        }
    }

    /**
     * Metodo de eliminacion del formulario Sfcargo
     *
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSfcargo(Registro reg)
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0009
                                                            .getValue());
            requestManager.delete(urlBean.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSfcargo();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Sfcargo
     *
     *
     */
    public void cancelarEdicionSfcargo()
    {
        cargarListaSfcargo();
        cargarListaSfcategoria();
    }

    /**
     * Metodo de insercion del formulario Sfcategoria
     *
     *
     */
    public void agregarRegistroSubSfcategoria()
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0012
                                                            .getValue());

            registroSubSfCategoria.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubSfCategoria.getCampos().put(AsignacionpagosespecialesControladorEnum.CODIGOPAGOESP.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

            registroSubSfCategoria.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSfCategoria.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

            registroSubSfCategoria.getCampos().remove("NOMBRE_CATEGORIA");
            registroSubSfCategoria.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

            requestManager.save(urlBean.getUrl(), urlBean.getMetodo(),
                            registroSubSfCategoria.getCampos());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(AsignacionpagosespecialesControladorEnum.MENSAJEEXITOSO.getValue()));
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubSfCategoria.getCampos().clear();
        }
    }

    public void activarEdicionSfcategoria(Registro reg)
    {
        indiceSfcategoria = listaSfcategoria.getRowIndex();
        cargarListaCbDetalleCat();
        cargarListaCbDetalleCatE();

        anoCategoria = Integer.parseInt(listaSfcategoria.getDatasource().get(indiceSfcategoria).getCampos().get("ANO").toString());
        cargarListaCbCategoria();
        cargarListaCbCategoriaE();
    }

    /**
     * Metodo de edicion del formulario Sfcategoria
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSfcategoria(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0011
                                                            .getValue());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

            reg.getCampos().remove("NOMBRE_CATEGORIA");

            reg.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

            requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(AsignacionpagosespecialesControladorEnum.MENSAJEMOD.getValue()));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSfcategoria();
        }
    }

    /**
     * Metodo de eliminacion del formulario Sfcategoria
     *
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSfcategoria(Registro reg)
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AsignacionpagosespecialesControladorUrlEnum.URL0013
                                                            .getValue());
            requestManager.delete(urlBean.getUrl(), reg.getLlave());
            cargarListaSfcategoria();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Sfcategoria
     *
     *
     */
    public void cancelarEdicionSfcategoria()
    {
        cargarListaSfcategoria();
    }

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
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
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
     *
     *
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
     *
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
     *
     *
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
     * Retorna la lista listaCbCargo
     *
     * @return listaCbCargo
     */
    public List<Registro> getListaCbCargo()
    {
        return listaCbCargo;
    }

    /**
     * Asigna la lista listaCbCargo
     *
     * @param listaCbCargo
     * Variable a asignar en listaCbCargo
     */
    public void setListaCbCargo(List<Registro> listaCbCargo)
    {
        this.listaCbCargo = listaCbCargo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCbDetalleEmpl
     *
     * @return listaCbDetalleEmpl
     */
    public RegistroDataModelImpl getListaCbDetalleEmpl()
    {
        return listaCbDetalleEmpl;
    }

    /**
     * Asigna la lista listaCbDetalleEmpl
     *
     * @param listaCbDetalleEmpl
     * Variable a asignar en listaCbDetalleEmpl
     */
    public void setListaCbDetalleEmpl(RegistroDataModelImpl listaCbDetalleEmpl)
    {
        this.listaCbDetalleEmpl = listaCbDetalleEmpl;
    }

    /**
     * Retorna la lista listaCbDetalleEmpl
     *
     * @return listaCbDetalleEmpl
     */
    public RegistroDataModelImpl getListaCbDetalleEmplE()
    {
        return listaCbDetalleEmplE;
    }

    /**
     * Asigna la lista listaCbDetalleEmpl
     *
     * @param listaCbDetalleEmpl
     * Variable a asignar en listaCbDetalleEmpl
     */
    public void setListaCbDetalleEmplE(RegistroDataModelImpl listaCbDetalleEmplE)
    {
        this.listaCbDetalleEmplE = listaCbDetalleEmplE;
    }

    /**
     * Retorna la lista listaCbEmpleado
     *
     * @return listaCbEmpleado
     */
    public RegistroDataModelImpl getListaCbEmpleado()
    {
        return listaCbEmpleado;
    }

    /**
     * Asigna la lista listaCbEmpleado
     *
     * @param listaCbEmpleado
     * Variable a asignar en listaCbEmpleado
     */
    public void setListaCbEmpleado(RegistroDataModelImpl listaCbEmpleado)
    {
        this.listaCbEmpleado = listaCbEmpleado;
    }

    /**
     * Retorna la lista listaCbEmpleado
     *
     * @return listaCbEmpleado
     */
    public RegistroDataModelImpl getListaCbEmpleadoE()
    {
        return listaCbEmpleadoE;
    }

    /**
     * Asigna la lista listaCbEmpleado
     *
     * @param listaCbEmpleado
     * Variable a asignar en listaCbEmpleado
     */
    public void setListaCbEmpleadoE(RegistroDataModelImpl listaCbEmpleadoE)
    {
        this.listaCbEmpleadoE = listaCbEmpleadoE;
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

    /**
     * Retorna la lista listaCbDetalleCargo
     *
     * @return listaCbDetalleCargo
     */
    public RegistroDataModelImpl getListaCbDetalleCargo()
    {
        return listaCbDetalleCargo;
    }

    /**
     * Asigna la lista listaCbDetalleCargo
     *
     * @param listaCbDetalleCargo
     * Variable a asignar en listaCbDetalleCargo
     */
    public void setListaCbDetalleCargo(RegistroDataModelImpl listaCbDetalleCargo)
    {
        this.listaCbDetalleCargo = listaCbDetalleCargo;
    }

    /**
     * Retorna la lista listaCbDetalleCargo
     *
     * @return listaCbDetalleCargo
     */
    public RegistroDataModelImpl getListaCbDetalleCargoE()
    {
        return listaCbDetalleCargoE;
    }

    /**
     * Asigna la lista listaCbDetalleCargo
     *
     * @param listaCbDetalleCargo
     * Variable a asignar en listaCbDetalleCargo
     */
    public void setListaCbDetalleCargoE(RegistroDataModelImpl listaCbDetalleCargoE)
    {
        this.listaCbDetalleCargoE = listaCbDetalleCargoE;
    }

    /**
     * Retorna la lista listaCbDetalleCat
     *
     * @return listaCbDetalleCat
     */
    public RegistroDataModelImpl getListaCbDetalleCat()
    {
        return listaCbDetalleCat;
    }

    /**
     * Asigna la lista listaCbDetalleCat
     *
     * @param listaCbDetalleCat
     * Variable a asignar en listaCbDetalleCat
     */
    public void setListaCbDetalleCat(RegistroDataModelImpl listaCbDetalleCat)
    {
        this.listaCbDetalleCat = listaCbDetalleCat;
    }

    /**
     * Retorna la lista listaCbDetalleCat
     *
     * @return listaCbDetalleCat
     */
    public RegistroDataModelImpl getListaCbDetalleCatE()
    {
        return listaCbDetalleCatE;
    }

    /**
     * Asigna la lista listaCbDetalleCat
     *
     * @param listaCbDetalleCat
     * Variable a asignar en listaCbDetalleCat
     */
    public void setListaCbDetalleCatE(RegistroDataModelImpl listaCbDetalleCatE)
    {
        this.listaCbDetalleCatE = listaCbDetalleCatE;
    }

    /**
     * Retorna la lista listaCbCategoria
     *
     * @return listaCbCategoria
     */
    public RegistroDataModelImpl getListaCbCategoria()
    {
        return listaCbCategoria;
    }

    /**
     * Asigna la lista listaCbCategoria
     *
     * @param listaCbCategoria
     * Variable a asignar en listaCbCategoria
     */
    public void setListaCbCategoria(RegistroDataModelImpl listaCbCategoria)
    {
        this.listaCbCategoria = listaCbCategoria;
    }

    /**
     * Retorna la lista listaCbCategoria
     *
     * @return listaCbCategoria
     */
    public RegistroDataModelImpl getListaCbCategoriaE()
    {
        return listaCbCategoriaE;
    }

    /**
     * Asigna la lista listaCbCategoria
     *
     * @param listaCbCategoria
     * Variable a asignar en listaCbCategoria
     */
    public void setListaCbCategoriaE(RegistroDataModelImpl listaCbCategoriaE)
    {
        this.listaCbCategoriaE = listaCbCategoriaE;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSfempleado
     *
     * @return listaSfempleado
     */
    public RegistroDataModelImpl getListaSfempleado()
    {
        return listaSfempleado;
    }

    /**
     * Asigna la lista listaSfempleado
     *
     * @param listaSfempleado
     * Variable a asignar en listaSfempleado
     */
    public void setListaSfempleado(RegistroDataModelImpl listaSfempleado)
    {
        this.listaSfempleado = listaSfempleado;
    }

    /**
     * Retorna la lista listaSfcargo
     *
     * @return listaSfcargo
     */
    public RegistroDataModelImpl getListaSfcargo()
    {
        return listaSfcargo;
    }

    /**
     * Asigna la lista listaSfcargo
     *
     * @param listaSfcargo
     * Variable a asignar en listaSfcargo
     */
    public void setListaSfcargo(RegistroDataModelImpl listaSfcargo)
    {
        this.listaSfcargo = listaSfcargo;
    }

    /**
     * Retorna la lista listaSfcategoria
     *
     * @return listaSfcategoria
     */
    public RegistroDataModelImpl getListaSfcategoria()
    {
        return listaSfcategoria;
    }

    /**
     * Asigna la lista listaSfcategoria
     *
     * @param listaSfcategoria
     * Variable a asignar en listaSfcategoria
     */
    public void setListaSfcategoria(RegistroDataModelImpl listaSfcategoria)
    {
        this.listaSfcategoria = listaSfcategoria;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubSfEmpleado
     *
     * @return registroSubSfEmpleado
     */
    public Registro getRegistroSubSfEmpleado()
    {
        return registroSubSfEmpleado;
    }

    /**
     * Asigna el objeto registroSubSfEmpleado
     *
     * @param registroSubSfEmpleado
     * Variable a asignar en registroSubSfEmpleado
     */
    public void setRegistroSubSfEmpleado(Registro registroSubSfEmpleado)
    {
        this.registroSubSfEmpleado = registroSubSfEmpleado;
    }

    /**
     * Retorna el objeto registroSubSfCargo
     *
     * @return registroSubSfCargo
     */
    public Registro getRegistroSubSfCargo()
    {
        return registroSubSfCargo;
    }

    /**
     * Asigna el objeto registroSubSfCargo
     *
     * @param registroSubSfCargo
     * Variable a asignar en registroSubSfCargo
     */
    public void setRegistroSubSfCargo(Registro registroSubSfCargo)
    {
        this.registroSubSfCargo = registroSubSfCargo;
    }

    /**
     * Retorna el objeto registroSubSfCategoria
     *
     * @return registroSubSfCategoria
     */
    public Registro getRegistroSubSfCategoria()
    {
        return registroSubSfCategoria;
    }

    /**
     * Asigna el objeto registroSubSfCategoria
     *
     * @param registroSubSfCategoria
     * Variable a asignar en registroSubSfCategoria
     */
    public void setRegistroSubSfCategoria(Registro registroSubSfCategoria)
    {
        this.registroSubSfCategoria = registroSubSfCategoria;
    }

    public int getIndiceSfempleado()
    {
        return indiceSfempleado;
    }

    public void setIndiceSfempleado(int indiceSfempleado)
    {
        this.indiceSfempleado = indiceSfempleado;
    }

    public int getIndiceSfcargo()
    {
        return indiceSfcargo;
    }

    public void setIndiceSfcargo(int indiceSfcargo)
    {
        this.indiceSfcargo = indiceSfcargo;
    }

    public int getIndiceSfcategoria()
    {
        return indiceSfcategoria;
    }

    public void setIndiceSfcategoria(int indiceSfcategoria)
    {
        this.indiceSfcategoria = indiceSfcategoria;
    }

    // </SET_GET_ADICIONALES>
}
