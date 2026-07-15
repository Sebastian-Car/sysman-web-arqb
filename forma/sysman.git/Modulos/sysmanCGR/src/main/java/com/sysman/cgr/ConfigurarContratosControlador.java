/*-
 * ConfigurarContratosControlador.java
 *
 * 1.0
 * 
 * 04/04/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.cgr.enums.ConfigurarContratosControladorUrlEnum;
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

/**
 * Clase migrada para la configuracion de los datos del informe personal y costo
 *
 * @version 1.0, 04/04/2019
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class ConfigurarContratosControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla CGR_CODIGO
     */
    private List<Registro> listaCgrConcepto;
    /**
     * Lista de registros de la tabla TIPODESTINO
     */
    private List<Registro> listaCgrTipoGasto;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de la tabla CRG_SEGMENTO
     */
    private RegistroDataModelImpl listaCgrSegmento;
    /**
     * Lista de registros de la tabla CRG_SEGMENTO
     */
    private RegistroDataModelImpl listaCgrSegmentoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfigurarContratosControlador
     */
    public ConfigurarContratosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try {
            //2059
            numFormulario = GeneralCodigoFormaEnum.CONFIGURAR_CONTRATOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
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
   
            tabla = GenericUrlEnum.ORDENDECOMPRA.getTable();
            buscarLlave();
            reasignarOrigen();
            registro = new Registro();
            // <CARGAR_LISTA>
            cargarListaCgrConcepto();
            cargarListaCgrTipoGasto();
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListaCgrSegmento();
            cargarListaCgrSegmentoE();
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
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarContratosControladorUrlEnum.URL155
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarContratosControladorUrlEnum.URL161
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCgrConcepto
     *
     */
    public void cargarListaCgrConcepto()
    {
        
        Map<String, Object> param = new TreeMap<>();

        try {
            listaCgrConcepto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarContratosControladorUrlEnum.URL184
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCgrTipoGasto
     *
     */
    public void cargarListaCgrTipoGasto()
    {
        
        Map<String, Object> param = new TreeMap<>();

        try {
            listaCgrTipoGasto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarContratosControladorUrlEnum.URL206
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    /**
     * 
     * Carga la lista listaCgrSegmento
     *
     */
    public void cargarListaCgrSegmento()
    {
        
        Map<String, Object> param = new TreeMap<>();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarContratosControladorUrlEnum.URL230
                                                        .getValue());
        listaCgrSegmento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        

    }

    /**
     * 
     * Carga la lista listaCgrSegmento
     *
     */
    public void cargarListaCgrSegmentoE()
    {
        Map<String, Object> param = new TreeMap<>();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarContratosControladorUrlEnum.URL230
                                                        .getValue());
        listaCgrSegmentoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
      }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCgrSegmento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCgrSegmento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CGR_SEGMENTO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCgrSegmento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCgrSegmentoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()),"").toString();
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
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
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
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     * 
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
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion, 
     * en el se pueden remover valores auxiliares que no se desee o 
     * se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CLASEORDEN.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBRECONCEPTO"); 
        registro.getCampos().remove("NOMBREGASTO"); 
        registro.getCampos().remove("NOMBRESEGMENTO"); 


        // </CODIGO_DESARROLLADO>
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
     * Retorna la lista listaCgrConcepto
     * 
     * @return listaCgrConcepto
     */
    public List<Registro> getListaCgrConcepto()
    {
        return listaCgrConcepto;
    }

    /**
     * Asigna la lista listaCgrConcepto
     * 
     * @param listaCgrConcepto
     * Variable a asignar en listaCgrConcepto
     */
    public void setListaCgrConcepto(List<Registro> listaCgrConcepto)
    {
        this.listaCgrConcepto = listaCgrConcepto;
    }

    /**
     * Retorna la lista listaCgrTipoGasto
     * 
     * @return listaCgrTipoGasto
     */
    public List<Registro> getListaCgrTipoGasto()
    {
        return listaCgrTipoGasto;
    }

    /**
     * Asigna la lista listaCgrTipoGasto
     * 
     * @param listaCgrTipoGasto
     * Variable a asignar en listaCgrTipoGasto
     */
    public void setListaCgrTipoGasto(List<Registro> listaCgrTipoGasto)
    {
        this.listaCgrTipoGasto = listaCgrTipoGasto;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCgrSegmento
     * 
     * @return listaCgrSegmento
     */
    public RegistroDataModelImpl getListaCgrSegmento()
    {
        return listaCgrSegmento;
    }

    /**
     * Asigna la lista listaCgrSegmento
     * 
     * @param listaCgrSegmento
     * Variable a asignar en listaCgrSegmento
     */
    public void setListaCgrSegmento(RegistroDataModelImpl listaCgrSegmento)
    {
        this.listaCgrSegmento = listaCgrSegmento;
    }

    /**
     * Retorna la lista listaCgrSegmento
     * 
     * @return listaCgrSegmento
     */
    public RegistroDataModelImpl getListaCgrSegmentoE()
    {
        return listaCgrSegmentoE;
    }

    /**
     * Asigna la lista listaCgrSegmento
     * 
     * @param listaCgrSegmento
     * Variable a asignar en listaCgrSegmento
     */
    public void setListaCgrSegmentoE(RegistroDataModelImpl listaCgrSegmentoE)
    {
        this.listaCgrSegmentoE = listaCgrSegmentoE;
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
}
