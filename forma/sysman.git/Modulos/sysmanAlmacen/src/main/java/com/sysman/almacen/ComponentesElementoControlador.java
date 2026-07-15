/*-
 * ComponentesElementoControlador.java
 *
 * 1.0
 * 
 * 30/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.ComponentesElementoControladorEnum;
import com.sysman.almacen.enums.ComponentesElementoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Permite configurar los componentes de un elemento
 *
 * @version 1.0, 30/08/2018
 * @author asana
 * 
 * @version 2.0, 08/11/2018 Se adiciona la segunda regla de clasificacion, relacionada con la materialidad de los componentes
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class ComponentesElementoControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo en el que se esta trabajando
     */
    private final String modulo;
    /**
     * Constante definida por las veces que se repite la palabra <b>MATERIAL</b> en el controlador
     */
    private final String cMaterial;
    // <DECLARAR_ATRIBUTOS>
    /**
     * almacena elemento a configfurar sus componentes
     */
    private String elemento;

    private int consecutivo;

    private int meses;
    /**
     * Valor del Componente seleccionado
     */
    private double valorComponente;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * almacena lista de elemento a seleccionar sus componentes
     */
    private RegistroDataModelImpl listalistaelementos;
    /**
     * almacena lista de elemento a seleccionar sus componentes
     */
    private RegistroDataModelImpl listalistaelementosE;
    /**
     * lista componentes quie contiene un elemento
     */
    private RegistroDataModelImpl listalistacomponentes;
    /**
     * lista componentes quie contiene un elemento
     */
    private RegistroDataModelImpl listalistacomponentesE;

    private boolean mesesBloqueo;

    private String mesesInicial;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    private String nombreElemento;
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Implementacion del EJB AlmacenCinco para acceder a funciones y/o procedimientos definidos en el paquete PCK_ALMACEN_COM5
     */
    @EJB
    private EjbAlmacenCincoRemote ejbAlmacenCinco;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ComponentesElementoControlador
     */
    public ComponentesElementoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cMaterial = ComponentesElementoControladorEnum.MATERIAL.getValue();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DETALLE_COMPONENTE_CONTROLADOR.getCodigo();
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.DETALLE_ELEMENTOSCOMPONENTES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        mesesBloqueo = false;
        valorComponente = 0;
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListalistaelementos();
        cargarListalistaelementosE();
        cargarListalistacomponentes();
        cargarListalistacomponentesE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

        buscarUrls();

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listalistaelementos
     *
     * método carga elementos a configurar
     */
    public void cargarListalistaelementos()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComponentesElementoControladorUrlEnum.URL2066
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listalistaelementos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CONSECUTIVO.getName());

    }

    /**
     * 
     * método carga elementos a configurar
     *
     * método carga componentes que contienen los elementos a configurar
     */
    public void cargarListalistaelementosE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComponentesElementoControladorUrlEnum.URL2066
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listalistaelementosE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CONSECUTIVO.getName());

    }

    /**
     * 
     * Carga la lista listalistacomponentes
     *
     * método carga componentes que contienen los elementos a configurar
     */
    public void cargarListalistacomponentes()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComponentesElementoControladorUrlEnum.URL2857
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listalistacomponentes = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.SERIE.getName());

    }

    /**
     * 
     * Carga la lista listalistacomponentes
     *
     * método carga componentes que contienen los elementos a configurar
     */
    public void cargarListalistacomponentesE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComponentesElementoControladorUrlEnum.URL2857
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listalistacomponentesE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.SERIE.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnSegundaRegla en la vista
     *
     * Validacion de la segunda regla de clasificacion, para definir la materialidad de los componentes en una estacion
     *
     */
    public void oprimirBtnSegundaRegla()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            ejbAlmacenCinco.clasificacionMaterialComponentes(compania, SessionUtil.getUser().getCodigo());
            listaInicial.load();
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listalistaelementos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilalistaelementos(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elemento = registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString();
        nombreElemento = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        consecutivo = Integer.parseInt(registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString());
        meses = Integer.parseInt(registroAux.getCampos().get("VIDAUTILCOMPONENTE").toString());
        registro.getCampos().put("MESESVIDAUTIL", meses);
        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listalistaelementos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilalistaelementosE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
        nombreElemento = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        meses = Integer.parseInt(registroAux.getCampos().get("VIDAUTILCOMPONENTE").toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listalistacomponentes
     *
     * método seleccionar carga componentes que contienen los elementos a configurar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilalistacomponentes(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(ComponentesElementoControladorEnum.PARAM1.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
        registro.getCampos().put(ComponentesElementoControladorEnum.PARAM0.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()));
        registro.getCampos().put(ComponentesElementoControladorEnum.PARAM2.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(ComponentesElementoControladorEnum.PARAM4.getValue(), meses);
        mesesInicial = SysmanFunciones.nvl(registroAux.getCampos().get(ComponentesElementoControladorEnum.PARAM3.getValue()), meses)
                        .toString();
        valorComponente = (double) SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.VALOR.getName()), 0);
        validarMaterialidad(registro);
        cambiarMaterial();
    }

    public void cambiarlistacomponentesC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(ComponentesElementoControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get(ComponentesElementoControladorEnum.PARAM1.getValue()));
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(ComponentesElementoControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get(ComponentesElementoControladorEnum.PARAM0.getValue()));
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(ComponentesElementoControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(ComponentesElementoControladorEnum.PARAM4.getValue(), meses);
        registro.getCampos().put(cMaterial, 0);
        mesesInicial = SysmanFunciones.nvl(registro.getCampos().get(ComponentesElementoControladorEnum.PARAM3.getValue()), meses)
                        .toString();

        validarMaterialidad(listaInicial.getDatasource().get(rowNum % 10));
        cambiarMaterialC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listalistacomponentes
     *
     * método seleccionar carga componentes que contienen los elementos a configurar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilalistacomponentesE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()).toString();
        valorComponente = (double) SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.VALOR.getName()), 0);

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(ComponentesElementoControladorEnum.PARAM1.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
        registro.getCampos().put(ComponentesElementoControladorEnum.PARAM0.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()));
        registro.getCampos().put(ComponentesElementoControladorEnum.PARAM4.getValue(),
                        registroAux.getCampos().get(ComponentesElementoControladorEnum.PARAM3.getValue()));
        registro.getCampos().put(cMaterial, 0);
        mesesInicial = SysmanFunciones.nvl(registroAux.getCampos().get(ComponentesElementoControladorEnum.PARAM3.getValue()), meses)
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
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
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put("CODIGO_ELEMENTO", consecutivo);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(ComponentesElementoControladorEnum.PARAM2.getValue());
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(ComponentesElementoControladorEnum.PARAM2.getValue());
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

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    public void cambiarMaterial()
    {

        if (Boolean.valueOf(registro.getCampos().get(cMaterial).toString()))
        {
            mesesBloqueo = true;

            consultarMesesInicial(registro.getCampos().get("CODIGO_COMPONENTE").toString(),
                            registro.getCampos().get("SERIE_COMPONENTE").toString());

            registro.getCampos().put("MESESVIDAUTIL", mesesInicial);
        }
        else
        {
            mesesBloqueo = true;
            registro.getCampos().put("MESESVIDAUTIL", meses);

        }
    }

    public void cambiarMaterialC(int rowNum)
    {
        if (Boolean.parseBoolean(listaInicial.getDatasource().get(rowNum % 10).getCampos().get(cMaterial).toString()))
        {
            mesesBloqueo = true;
            consultarMesesInicial(listaInicial.getDatasource().get(rowNum % 10).getCampos().get("CODIGO_COMPONENTE").toString(),
                            listaInicial.getDatasource().get(rowNum % 10).getCampos().get("SERIE_COMPONENTE").toString());
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put("MESESVIDAUTIL", mesesInicial);
        }
        else
        {
            mesesBloqueo = true;
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put("MESESVIDAUTIL", meses);

        }

    }

    public void consultarMesesInicial(String elementoI, String serieI)
    {

        Registro mesesElemento = null;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(), elementoI);
        param.put(GeneralParameterEnum.SERIE.getName(), serieI);

        try
        {
            mesesElemento = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComponentesElementoControladorUrlEnum.URL2858
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (mesesElemento != null)
        {
            mesesInicial =  SysmanFunciones.nvl(mesesElemento.getCampos().get("MESESVIDAUTILPLACA"),0).toString();
        }

    }

    /**
     * Define la materialidad de un componente validando si su valor es mayor a la multiplicacion del SMMLV por el valor definido en el parametro "SMMLV MATERIALIDAD POLITICA CONTABLE"
     * 
     * @param registroActual
     * Registro al realizar una insercion o modificacion
     */
    private void validarMaterialidad(Registro registroActual)
    {
        double materialidadEnSmmlv = Double.parseDouble(consultarParametro("SMMLV MATERIALIDAD POLITICA CONTABLE", "0"));
        if (materialidadEnSmmlv == 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4245"));
            return;
        }

        Registro reg;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));

        try
        {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComponentesElementoControladorUrlEnum.URL2859
                                                                            .getValue())
                                            .getUrl(), param));
            if (reg != null)
            {
                double valorSmmlv = (double) reg.getCampos().get("SALARIOMINIMO");
                boolean esMaterial = valorComponente > (valorSmmlv * materialidadEnSmmlv);
                registroActual.getCampos().put(cMaterial, esMaterial);
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Obtiene el valor almacenado en la base de datos para el parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String consultarParametro(String nombreParametro,
                    String valorDefault)
    {
        String parametro = null;
        try
        {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable elemento
     * 
     * @return elemento
     */
    public String getElemento()
    {
        return elemento;
    }

    /**
     * Asigna la variable elemento
     * 
     * @param elemento
     * Variable a asignar en elemento
     */
    public void setElemento(String elemento)
    {
        this.elemento = elemento;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listalistaelementos
     * 
     * @return listalistaelementos
     */
    public RegistroDataModelImpl getListalistaelementos()
    {
        return listalistaelementos;
    }

    /**
     * Asigna la lista listalistaelementos
     * 
     * @param listalistaelementos
     * Variable a asignar en listalistaelementos
     */
    public void setListalistaelementos(RegistroDataModelImpl listalistaelementos)
    {
        this.listalistaelementos = listalistaelementos;
    }

    /**
     * Retorna la lista listalistaelementos
     * 
     * @return listalistaelementos
     */
    public RegistroDataModelImpl getListalistaelementosE()
    {
        return listalistaelementosE;
    }

    /**
     * Asigna la lista listalistaelementos
     * 
     * @param listalistaelementos
     * Variable a asignar en listalistaelementos
     */
    public void setListalistaelementosE(RegistroDataModelImpl listalistaelementosE)
    {
        this.listalistaelementosE = listalistaelementosE;
    }

    /**
     * Retorna la lista listalistacomponentes
     * 
     * @return listalistacomponentes
     */
    public RegistroDataModelImpl getListalistacomponentes()
    {
        return listalistacomponentes;
    }

    /**
     * Asigna la lista listalistacomponentes
     * 
     * @param listalistacomponentes
     * Variable a asignar en listalistacomponentes
     */
    public void setListalistacomponentes(RegistroDataModelImpl listalistacomponentes)
    {
        this.listalistacomponentes = listalistacomponentes;
    }

    /**
     * Retorna la lista listalistacomponentes
     * 
     * @return listalistacomponentes
     */
    public RegistroDataModelImpl getListalistacomponentesE()
    {
        return listalistacomponentesE;
    }

    /**
     * Asigna la lista listalistacomponentes
     * 
     * @param listalistacomponentes
     * Variable a asignar en listalistacomponentes
     */
    public void setListalistacomponentesE(RegistroDataModelImpl listalistacomponentesE)
    {
        this.listalistacomponentesE = listalistacomponentesE;
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

    public String getNombreElemento()
    {
        return nombreElemento;
    }

    public void setNombreElemento(String nombreElemento)
    {
        this.nombreElemento = nombreElemento;
    }

    public boolean isMesesBloqueo()
    {
        return mesesBloqueo;
    }

    public void setMesesBloqueo(boolean mesesBloqueo)
    {
        this.mesesBloqueo = mesesBloqueo;
    }

    public int getMeses()
    {
        return meses;
    }

    public void setMeses(int meses)
    {
        this.meses = meses;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
