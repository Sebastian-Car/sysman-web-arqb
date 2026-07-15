/*-
 * FrmsubcronogramasstsControlador.java
 *
 * 1.0
 * 
 * 02/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.SubCronogramaSstControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * formulario que despliega en boton "Detalle" del formulario
 * Cronograma
 *
 * @version 1.0, 02/01/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class SubCronogramaSstControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * almacena lista listaCmbRaci
     */
    private RegistroDataModelImpl listaCmbRaci;

    private RegistroDataModelImpl listaCmbRaciE;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * almacena lista listaCmbIdNit
     */
    private RegistroDataModelImpl listaCmbIdNit;
    /**
     * almacena lista listaCmbIdNitE
     */
    private RegistroDataModelImpl listaCmbIdNitE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private String nCodigo;
    private String nNombres;
    private String nId;
    private String nTipo;
    private String nNombre;
    private String nSucursal;
    private String nDocumento;
    private Map<String, Object> parametrosEntrada;

    private String consecutivo;
    private String actividad;
    private String codActividad;
    private int indice;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmsubcronogramasstsControlador
     */
    public SubCronogramaSstControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMSUBCRONOGRAMASST_CONTROLADOR.getCodigo();
            validarPermisos();
            compania = SessionUtil.getCompania();
            parametrosEntrada = SessionUtil.getFlash();
            nNombres = "NOMBRES";
            nId = "ID_EMPLEADO";
            nTipo = "TIPO_RACI";
            nNombre = "NOMBRE";
            nSucursal = "SUCURSAL";
            nDocumento = "NUMERO_DCTO";
            nCodigo = "CODIGO";
            if (parametrosEntrada != null)
            {
                consecutivo = String.valueOf(parametrosEntrada.get("numero"));
                actividad = (String) parametrosEntrada.get("actividad");
                codActividad = (String) parametrosEntrada.get("codActividad");
            }
            SessionUtil.cleanFlash();

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
        // <CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        enumBase = GenericUrlEnum.SST_D_CRONOGRAMA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaCmbRaci();
        cargarListaCmbRaciE();
        cargarListaCmbIdNit();
        cargarListaCmbIdNitE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put("NUMERO", consecutivo);
        parametrosListado.put("ACTIVIDAD", codActividad);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCmbRaci
     */
    public void cargarListaCmbRaci()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(SubCronogramaSstControladorUrlEnum.URL5807.getValue());

        listaCmbRaci = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, true, "TIPO_RACI");
    }

    /**
     * 
     * Carga la lista listaCmbRaci
     */
    public void cargarListaCmbRaciE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(SubCronogramaSstControladorUrlEnum.URL5807.getValue());

        listaCmbRaciE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, true, "TIPO_RACI");
    }

    /**
     * 
     * Carga la lista listaCmbIdNit
     */
    public void cargarListaCmbIdNit()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubCronogramaSstControladorUrlEnum.URL7741.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbIdNit = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, nDocumento);
    }

    /**
     * 
     * Carga la lista listaCmbIdNit
     */
    public void cargarListaCmbIdNitE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubCronogramaSstControladorUrlEnum.URL7741.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbIdNitE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, nDocumento);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void cambiarCmbIdNitC(int rowNun)
    {
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .put(nDocumento, registro
                                        .getCampos()
                                        .get(nDocumento));
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .put(nNombres, registro
                                        .getCampos()
                                        .get(nNombres));
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .put(nId, registro
                                        .getCampos()
                                        .get(nId));
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .put(nSucursal, registro
                                        .getCampos()
                                        .get(nSucursal));
    }

    public void cambiarCmbRaciC(int rowNun)
    {
        listaInicial.getDatasource().get(rowNun %
            10).getCampos().put(nNombre, registro.getCampos().get(nNombre));
        listaInicial.getDatasource().get(rowNun %
            10).getCampos().put(nTipo, registro.getCampos().get(nTipo));
    }

    public void seleccionarFilaCmbRaci(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nNombre, registroAux.getCampos().get(nNombre));
        registro.getCampos().put(nTipo, registroAux.getCampos().get("TIPO_RACI"));
    }

    public void seleccionarFilaCmbRaciE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nNombre, registroAux.getCampos().get(nNombre));
        registro.getCampos().put(nTipo, registroAux.getCampos().get(nTipo));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbIdNit
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbIdNit(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nDocumento, registroAux.getCampos().get(nDocumento));
        registro.getCampos().put(nId, registroAux.getCampos().get("ID_DE_EMPLEADO"));
        registro.getCampos().put(nNombres, registroAux.getCampos().get("NOMBRERESPONSABLE"));
        registro.getCampos().put(nSucursal, registroAux.getCampos().get(nSucursal));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbIdNit
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbIdNitE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nDocumento, registroAux.getCampos().get(nDocumento));
        registro.getCampos().put(nId, registroAux.getCampos().get("ID_DE_EMPLEADO"));
        registro.getCampos().put(nNombres, registroAux.getCampos().get("NOMBRERESPONSABLE"));
        registro.getCampos().put(nSucursal, registroAux.getCampos().get(nSucursal));
        registro.getCampos();

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
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put("CONSECUTIVO", consecutivo);
        registro.getCampos().put("CODIGO_ACTIVIDAD", codActividad);
        registro.getCampos().put(nTipo, registro.getCampos().get(nTipo));
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
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put("CEDULA", registro.getCampos().get(nDocumento));
        registro.getCampos().put(nTipo, registro.getCampos().get(nTipo));
        registro.getCampos().remove(nNombres);
        registro.getCampos().remove(nNombre);
        registro.getCampos().remove(nDocumento);
        registro.getCampos().remove(nCodigo);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {

        registro.getCampos().remove("CONSECUTIVO");
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("CODIGO_ACTIVIDAD");
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaCmbRaci
     * 
     * @return listaCmbRaci
     */
    public RegistroDataModelImpl getListaCmbRaci()
    {
        return listaCmbRaci;
    }

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getActividad()
    {
        return actividad;
    }

    public void setActividad(String actividad)
    {
        this.actividad = actividad;
    }

    /**
     * Asigna la lista listaCmbRaci
     * 
     * @param listaCmbRaci
     * Variable a asignar en listaCmbRaci
     */
    public void setListaCmbRaci(RegistroDataModelImpl listaCmbRaci)
    {
        this.listaCmbRaci = listaCmbRaci;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaCmbIdNit
     * 
     * @return listaCmbIdNit
     */
    public RegistroDataModelImpl getListaCmbIdNit()
    {
        return listaCmbIdNit;
    }

    /**
     * Asigna la lista listaCmbIdNit
     * 
     * @param listaCmbIdNit
     * Variable a asignar en listaCmbIdNit
     */
    public void setListaCmbIdNit(RegistroDataModelImpl listaCmbIdNit)
    {
        this.listaCmbIdNit = listaCmbIdNit;
    }

    /**
     * Retorna la lista listaCmbIdNit
     * 
     * @return listaCmbIdNit
     */
    public RegistroDataModelImpl getListaCmbIdNitE()
    {
        return listaCmbIdNitE;
    }

    /**
     * Asigna la lista listaCmbIdNit
     * 
     * @param listaCmbIdNit
     * Variable a asignar en listaCmbIdNit
     */
    public void setListaCmbIdNitE(RegistroDataModelImpl listaCmbIdNitE)
    {
        this.listaCmbIdNitE = listaCmbIdNitE;
    }

    public RegistroDataModelImpl getListaCmbRaciE()
    {
        return listaCmbRaciE;
    }

    public void setListaCmbRaciE(RegistroDataModelImpl listaCmbRaciE)
    {
        this.listaCmbRaciE = listaCmbRaciE;
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

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
