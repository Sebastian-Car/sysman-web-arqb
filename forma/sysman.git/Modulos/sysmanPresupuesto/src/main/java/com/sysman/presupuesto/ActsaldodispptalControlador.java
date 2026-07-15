/*-
 * ActsaldodispptalControlador.java
 *
 * 1.0
 *
 * 18/04/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresRemote;
import com.sysman.presupuesto.enums.ActsaldodispptalControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Permite ejecutar el proceso congelarSaldoDetalle del ejbPresupuestoTres
 *
 * @version 1.0, 18/04/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class ActsaldodispptalControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * anio seleccionado en la lista simple del formulario
     */
    private String ano;
    /**
     * codigo del comprobante seleccionado como inicial
     */
    private String comprobanteInicial;
    /**
     * codigo del comprobante seleccionado como final
     */
    private String comprobanteFinal;
    /**
     * nombre del comprobante inicial
     */
    private String nombreInicial;
    /**
     * nombr del comprobante final
     */
    private String nombreFinal;

    @EJB
    private EjbPresupuestoTresRemote ejbPresupuestoTres;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista de anio activos
     */
    private List<Registro> listanumero;
    /**
     * lista de comprobantes DIS inicial
     */
    private RegistroDataModelImpl listacomprobanteInicial;
    /**
     * lista de comprobantes DIS final
     */
    private RegistroDataModelImpl listacomprobanteFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ActsaldodispptalControlador
     */
    public ActsaldodispptalControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ACTSALDODISPPTAL_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListanumero();
        cargarListacomprobanteInicial();
        cargarListacomprobanteFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
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
     * Carga la lista listanumero
     *
     */
    public void cargarListanumero()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listanumero = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActsaldodispptalControladorUrlEnum.URL12000
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
     * Carga la lista listacomprobanteInicial
     *
     */
    public void cargarListacomprobanteInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActsaldodispptalControladorUrlEnum.URL12001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.TIPO_CPTE.getName(), "DIS");
        listacomprobanteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());
    }

    /**
     *
     * Carga la lista listacomprobanteFinal
     *
     */
    public void cargarListacomprobanteFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActsaldodispptalControladorUrlEnum.URL12002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.TIPO_CPTE.getName(), "DIS");
        param.put(GeneralParameterEnum.CODIGO.getName(), comprobanteInicial);
        listacomprobanteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Comando11 en la vista
     *
     *
     */
    public void oprimirComando11()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            ejbPresupuestoTres.congelarSaldoDetalle(compania,
                            Integer.parseInt(ano),
                            "DIS",
                            Long.parseLong(comprobanteInicial),
                            Long.parseLong(comprobanteFinal));
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarnumero()
    {
        comprobanteInicial = null;
        comprobanteFinal = null;
        nombreInicial = null;
        nombreFinal = null;
        cargarListacomprobanteInicial();
    }

    public void seleccionarFilacomprobanteInicial(SelectEvent event)
    {
        comprobanteFinal = null;
        nombreFinal = null;
        Registro registroAux = (Registro) event.getObject();
        comprobanteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()),
                                        "")
                        .toString();
        nombreInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();
        cargarListacomprobanteFinal();
    }

    public void seleccionarFilacomprobanteFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        comprobanteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()),
                                        "")
                        .toString();
        nombreFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listanumero
     *
     * @return listanumero
     */
    public List<Registro> getListanumero()
    {
        return listanumero;
    }

    /**
     * Asigna la lista listanumero
     *
     * @param listanumero
     * Variable a asignar en listanumero
     */
    public void setListanumero(List<Registro> listanumero)
    {
        this.listanumero = listanumero;
    }

    /**
     * Retorna la lista listacomprobanteInicial
     *
     * @return listacomprobanteInicial
     */
    public RegistroDataModelImpl getListacomprobanteInicial()
    {
        return listacomprobanteInicial;
    }

    /**
     * Asigna la lista listacomprobanteInicial
     *
     * @param listacomprobanteInicial
     * Variable a asignar en listacomprobanteInicial
     */
    public void setListacomprobanteInicial(
        RegistroDataModelImpl listacomprobanteInicial)
    {
        this.listacomprobanteInicial = listacomprobanteInicial;
    }

    /**
     * Retorna la lista listacomprobanteFinal
     *
     * @return listacomprobanteFinal
     */
    public RegistroDataModelImpl getListacomprobanteFinal()
    {
        return listacomprobanteFinal;
    }

    /**
     * Asigna la lista listacomprobanteFinal
     *
     * @param listacomprobanteFinal
     * Variable a asignar en listacomprobanteFinal
     */
    public void setListacomprobanteFinal(
        RegistroDataModelImpl listacomprobanteFinal)
    {
        this.listacomprobanteFinal = listacomprobanteFinal;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getComprobanteInicial()
    {
        return comprobanteInicial;
    }

    public void setComprobanteInicial(String comprobanteInicial)
    {
        this.comprobanteInicial = comprobanteInicial;
    }

    public String getComprobanteFinal()
    {
        return comprobanteFinal;
    }

    public void setComprobanteFinal(String comprobanteFinal)
    {
        this.comprobanteFinal = comprobanteFinal;
    }

    public String getNombreInicial()
    {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial)
    {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal()
    {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal)
    {
        this.nombreFinal = nombreFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
