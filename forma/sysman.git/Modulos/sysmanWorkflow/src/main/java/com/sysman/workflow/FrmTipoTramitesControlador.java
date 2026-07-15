/*-
 * FrmTipoTramitesControlador.java
 *
 * 1.0
 * 
 * 28/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmTipoTramitesControladorEnum;
import com.sysman.workflow.enums.FrmTipoTramitesControladorUrlEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador que gestiona los eventos de la forma:
 * <code>frmtipotramites</code>.
 *
 * @version 1.0, 27/03/2018
 * @author pespitia
 * 
 * @version 1.1, 15/09/2021
 * @author gfigueredo
 * Se añade el modificador para el campo Activo, según tar 1000109194 
 * @see #activarEdicion(Registro)
 */
@ManagedBean
@ViewScoped
public class FrmTipoTramitesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /** Constante a nivel de clase que aloja la cadena: CODIGO */
    private final String cCodigo = GeneralParameterEnum.CODIGO.getName();

    /** Constante a nivel de clase que aloja la cadena: COMPANIA */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /** Constante a nivel de clase que aloja la cadena: PROCESOS. */
    private final String cProcesos = FrmTipoTramitesControladorEnum.PROCESOS
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena: TIPOTRAMITE.
     */
    private final String cTipoTramite = FrmTipoTramitesControladorEnum.TIPOTRAMITE
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private boolean modificador;
    private boolean modificadorActivo;
    
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
	private int indice;
    
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los detalles del combo Proceso (CB5821) al
     * insertar un registro.
     */
    private List<Registro> listaProcesos;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete <code>PCK_SYSMAN_UTL</code>.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_EJBs>
    /**
     * Crea una nueva instancia de FrmTipoTramitesControlador
     */
    public FrmTipoTramitesControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1751
            numFormulario = GeneralCodigoFormaEnum.FRM_TIPO_TRAMITES_CONTROLADOR
                            .getCodigo();

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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        registro = new Registro();
        enumBase = GenericUrlEnum.TIPOTRAMITES;

        reasignarOrigen();
        buscarLlave();
        // <CARGAR_LISTA>
        cargarListaProcesos();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista <code>listaProcesos</code> asociada al combo
     * Procesos (CB5821) al ingresar un registro.
     */
    public void cargarListaProcesos() {
        Map<String, Object> param = new HashMap<>();
        param.put(cCompania, compania);

        try {
            listaProcesos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID( FrmTipoTramitesControladorUrlEnum.URL3347.getValue()).getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el valor del combo Procesos
     * (CB5821) en la fila seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarProcesosC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        String proceso = SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get(cProcesos), "")
                        .toString();

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cTipoTramite, generarConsecutivo(proceso));
        // </CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control NumeracionUnica en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarNumeracionUnicaC(int rowNum) {
         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
         // listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }

public void cambiarActivoC(int rowNum) {
	
}
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * <code>listaProcesos</code> asociada al combo Procesos (CB5821)
     * al insertar un registro.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProcesos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cProcesos,
                        registroAux.getCampos().get(cCodigo));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * <code>listaProcesosE</code> asociada al combo Procesos (CB5821)
     * al editar un registro.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProcesosE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado cuando se cancela la edicion del registro. */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return true -> Permite insertar.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);

        String proceso = SysmanFunciones
                        .nvl(registro.getCampos().get(cProcesos), "")
                        .toString();

        registro.getCampos().put(cTipoTramite, generarConsecutivo(proceso));
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro.
     * 
     * @return true -> Permite realizar la insercion y/o actualizacion
     * del registro.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("PROCESO_NOM");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     * 
     * @return true -> Permite realizar la eliminacion del registro.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(cCompania);
    }
    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        
        modificador = Boolean.valueOf(registro
                .getCampos().get("NUMERACION_UNICA").toString());
        
        //modificadorActivo = Boolean.valueOf(registro
        //        .getCampos().get("ACTIVO").toString());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera el valor consecutivo de ingresar un registro en la tabla
     * TIPOTRAMITES respecto a la compania y el proceso.
     * 
     * @return proceso -> Codigo del proceso.
     */
    private long generarConsecutivo(String proceso) {
        String criterio = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "'' AND PROCESOS = ''", proceso, "''");

        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            enumBase.getTable(), criterio, cTipoTramite);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar
     * the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaProcesos() {
        return listaProcesos;
    }

    public void setListaProcesos(List<Registro> listaProcesos) {
        this.listaProcesos = listaProcesos;
    }

	public boolean isModificador() {
		return modificador;
	}

	public void setModificador(boolean modificador) {
		this.modificador = modificador;
	}
	
	
    public boolean isModificadorActivo() {
		return modificadorActivo;
	}

	public void setModificadorActivo(boolean modificadorActivo) {
		this.modificadorActivo = modificadorActivo;
	}

	public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    
    
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
