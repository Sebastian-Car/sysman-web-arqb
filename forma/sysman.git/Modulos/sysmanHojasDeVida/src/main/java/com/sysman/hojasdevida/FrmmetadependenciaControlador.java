/*-
 * FrmmetadependenciaControlador.java
 *
 * 1.0
 * 
 * 20/01/2018
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
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmmetadependenciaControladorEnum;
import com.sysman.hojasdevida.enums.FrmmetadependenciaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase permite gestionar el archivo frmmetadependencia.
 *
 * @version 1.0, 20/01/2018
 * @author dnino
 * 
 * Se añade etiqueta para visualizar el nombre de la dependencia
 * seleccionada.
 * @version 2.0, 20/02/2018
 * @author dnino
 */
@ManagedBean
@ViewScoped
public class FrmmetadependenciaControlador extends BeanBaseContinuoAcmeImpl
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
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private String codigo;
    private String dependencia;
    private String nombreMeta;
    /**
     * Declaracion lista de las dependencias de la compañia.
     */
    private RegistroDataModelImpl listaDependencia;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmmetadependenciaControlador
     */
    public FrmmetadependenciaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        codigo = GeneralParameterEnum.CODIGO.getName();
        try
        {
            registro = new Registro(new HashMap<String, Object>());
            numFormulario = GeneralCodigoFormaEnum.FRM_META_DEPENDENCIA_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.EV_META_DEPENDENCIAS;
        reasignarOrigen();
        buscarLlave();
        cargarListaDependencia();
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbDependencia
     *
     */
    public void cargarListaDependencia()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmetadependenciaControladorUrlEnum.URL4515
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
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
     * listaDependencia
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        dependencia = SysmanFunciones
                        .toString(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()));
        nombreMeta = SysmanFunciones
                        .toString(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
        reasignarOrigen();
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

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true En caso que encuentre registros coincidentes con
     * la Compañia y la Dependencia seleccionada, en tal caso busca el
     * último consecutivo almacenado y lo incrementa en uno.
     */
    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        Long consec = (long) 1;
        try
        {
            consec = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "EV_META_DEPENDENCIAS",
                            SysmanFunciones.concatenar("COMPANIA = " + compania
                                + " AND DEPENDENCIA = ''"
                                + dependencia + "'' "),
                            "CODIGO_META");
            codigo = SysmanFunciones
                            .strZero(consec.toString(), 10);
            registro.getCampos()
                            .put(FrmmetadependenciaControladorEnum.CODIGO_META
                                            .getValue(),
                                            String.valueOf(codigo));
            registro.getCampos()
                            .put(FrmmetadependenciaControladorEnum.DEPENDENCIA
                                            .getValue(),
                                            dependencia);
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
     * @return true Al ejecutar acciones luego de crear un registro
     * nuevo.
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
     * @return true en caso de ejecutar acciones antes de actualizar
     * un registro.
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
     * @return true en caso de ejecutar acciones despues de actualizar
     * un registro.
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
     * @return true en caso de ejecutar acciones antes de eliminar un
     * registro.
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
     * @return true en caso de ejecutar acciones despues de actualizar
     * un registro.
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

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
     * @return the listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    /**
     * @param listaDependencia
     * the listaDependencia to set
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    /**
     * @return the compania
     */
    public String getCompania()
    {
        return compania;
    }

    /**
     * @return the ccodigo
     */
    public String getCodigo()
    {
        return codigo;
    }

    /**
     * @param ccodigo
     * the ccodigo to set
     */
    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    /**
     * @return the nombreMeta
     */
    public String getNombreMeta()
    {
        return nombreMeta;
    }

    /**
     * @param nombreMeta
     * the nombreMeta to set
     */
    public void setNombreMeta(String nombreMeta)
    {
        this.nombreMeta = nombreMeta;
    }

    /**
     * @return the ejbSysmanUtil
     */
    public EjbSysmanUtilRemote getEjbSysmanUtil()
    {
        return ejbSysmanUtil;
    }

    /**
     * @param ejbSysmanUtil
     * the ejbSysmanUtil to set
     */
    public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil)
    {
        this.ejbSysmanUtil = ejbSysmanUtil;
    }

    /**
     * @return the dependencia
     */
    public String getDependencia()
    {
        return dependencia;
    }

    /**
     * @param dependencia
     * the dependencia to set
     */
    public void setDependencia(String dependencia)
    {
        this.dependencia = dependencia;
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
