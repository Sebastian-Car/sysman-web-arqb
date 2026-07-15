/*-
 * ProcesosayudaControlador.java
 *
 * 1.0
 * 
 * 05/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ProcesosayudaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 05/06/2023
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  ProcesosayudaControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    //<DECLARAR_ATRIBUTOS>
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaaplicaciones;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaaplicacionesE;
    /**
     * variable EJB
     */
    @EJB
    EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
    private String auxiliar;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ProcesosayudaControlador
     */
    public ProcesosayudaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_AYUDA_PROCESOS
                            .getCodigo();
            validarPermisos();
            
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
    public void inicializar(){
        
            enumBase = GenericUrlEnum.AYUDA_PROCESOS;
            buscarLlave();
            reasignarOrigen();		    
            registro= new Registro();
            cargarListaaplicaciones(); 
            cargarListaaplicacionesE();
            abrirFormulario();
       
    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen(){
        
        buscarUrls();
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaaplicaciones
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaaplicaciones(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProcesosayudaControladorUrlEnum.URL1906
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        listaaplicaciones = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.APLICACION.getName());
    }
    /**
     * 
     * Carga la lista listaaplicaciones
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void  cargarListaaplicacionesE(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProcesosayudaControladorUrlEnum.URL1906
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        listaaplicacionesE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.APLICACION.getName());
        
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control aplicaciones en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiaraplicacionesC(int rowNum) {
        
       // listaInicial.getDatasource().get(rowNum % 10).getCampos()
       // .put("TIPO_SIRECI_INF", auxiliar);
       // listaInicial.getDatasource().get(rowNum % 10).getCampos()
       // .put(nomtipoInfCons, nombreTipoInf);
    
    }
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaaplicaciones
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaaplicaciones(SelectEvent event) {
        
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("APLICACION",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.APLICACION.getName()));
        
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaaplicaciones
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaaplicacionesE(SelectEvent event) {
        
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.APLICACION.getName());
    }
    //</METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        
        //</CODIGO_DESARROLLADO>
    }
    /**
         * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
         * TODO DOCUMENTACION ADICIONAL
         */
        @Override
        public void cancelarEdicion(RowEditEvent event) {
            getListaInicial().load();
        }
        /**
         * Metodo ejecutado antes de realizar la insercion del registro
         * TODO DOCUMENTACION ADICIONAL
         * 
         * @return TODO VARIABLE
         */
        @Override
        public boolean insertarAntes(){
            
            try
            {
            
            String[] parametros = {""};
            registro.getCampos().put("ID_PROCESO", ejbSysmanUtil
                            .generarConsecutivoConValorInicial("AYUDA_PROCESOS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            "ID_PROCESO", "1"));
            
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            return true;
        }
        /**
         * Metodo ejecutado despues de realizar la insercion del registro
         * TODO DOCUMENTACION ADICIONAL
         * 
         * @return TODO VARIABLE
         */
        @Override
        public boolean insertarDespues(){
            //<CODIGO_DESARROLLADO>
            //</CODIGO_DESARROLLADO>
            return true;
        }
        /**
         * Metodo ejecutado antes de realizar la insercion y actualizacion
         * del registro
         * 
         * TODO DOCUMENTACION ADICIONAL
         * 
         * @return TODO VARIABLE
         */
        @Override
        public boolean actualizarAntes(){
            //<CODIGO_DESARROLLADO>
            //</CODIGO_DESARROLLADO>
            return true;
        }
        /**
         * Metodo ejecutado despues de realizar la insercion y actualizacion
         * del registro
         * 
         * TODO DOCUMENTACION ADICIONAL
         * 
         * @return TODO VARIABLE
         */
        @Override   
        public boolean actualizarDespues(){
            //<CODIGO_DESARROLLADO>
            //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
             * Metodo ejecutado antes de realizar la eliminacion del
             * registro
             * 
             * TODO DOCUMENTACION ADICIONAL
             * 
             * @return TODO VARIABLE
             */
            @Override    
            public boolean eliminarAntes(){
                //<CODIGO_DESARROLLADO>
                //</CODIGO_DESARROLLADO>
                return true;
            }
            /**
             * Metodo ejecutado despues de realizar la eliminacion del
             * registro
             * 
             * TODO DOCUMENTACION ADICIONAL
             * 
             * @return TODO VARIABLE
             */
            @Override   
            public boolean eliminarDespues(){
                //<CODIGO_DESARROLLADO>
                //</CODIGO_DESARROLLADO>
                return true;
            }
            /**
             * Este metodo se ejecuta antes enviar la accion de actualizacion,
             * en el se pueden remover valores auxiliares que no se desee o se
             * deban enviar en el registro
             */
            @Override
            public void removerCombos() {
                
                registro.getCampos().remove("NOMAPLICACION");
            }
            /**
             * Este metodo es ejecutado despues de finalizar la insercion y
             * edicion del registro se usa cuando se desean agregar valores
             * al registro despues de dichas acciones
             */
            @Override
            public void asignarValoresRegistro()
            {
                // TODO Auto-generated method stub
            }
            //<SET_GET_ATRIBUTOS>
            //</SET_GET_ATRIBUTOS>
            //<SET_GET_PARAMETROS>
            //</SET_GET_PARAMETROS>
            //<SET_GET_LISTAS>
            //</SET_GET_LISTAS>
            //<SET_GET_LISTAS_COMBO_GRANDE>	
            /**
             * Retorna la lista listaaplicaciones
             * 
             * @return listaaplicaciones
             */
            public RegistroDataModelImpl getListaaplicaciones() {
                return listaaplicaciones;
            }
            /**
             * Asigna la lista listaaplicaciones
             * 
             * @param listaaplicaciones
             * Variable a asignar en  listaaplicaciones
             */
            public void setListaaplicaciones(RegistroDataModelImpl listaaplicaciones) {
                this.listaaplicaciones = listaaplicaciones;
            }
            /**
             * Retorna la lista listaaplicaciones
             * 
             * @return listaaplicaciones
             */
            public RegistroDataModelImpl getListaaplicacionesE() {
                return listaaplicacionesE;
            }
            /**
             * Asigna la lista listaaplicaciones
             * 
             * @param listaaplicaciones
             * Variable a asignar en  listaaplicaciones
             */
            public void setListaaplicacionesE(RegistroDataModelImpl listaaplicacionesE) {
                this.listaaplicacionesE = listaaplicacionesE;
            }
            /**
             * Retorna la variable auxiliar
             * 
             * @return auxiliar
             */
            public String getAuxiliar() {
                return auxiliar;
            }
            /**
             * Asigna la variable auxiliar
             * 
             * @param auxiliar
             * Variable a asignar en auxiliar
             */
            public void setAuxiliar(String auxiliar) {
                this.auxiliar= auxiliar;
            }
            //</SET_GET_LISTAS_COMBO_GRANDE>
            
            private String retornarString(Registro reg, String campo) {
                return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                    : reg.getCampos().get(campo).toString();
            }
            
        }
