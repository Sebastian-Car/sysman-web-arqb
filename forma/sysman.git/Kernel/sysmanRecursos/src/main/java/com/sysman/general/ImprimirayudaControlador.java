/*-
 * ImprimirayudaControlador.java
 *
 * 1.0
 * 
 * 08/06/2023
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
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.ImprimirayudaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 08/06/2023
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  ImprimirayudaControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    //<DECLARAR_ATRIBUTOS>
    private String modulo;
    private String proceso;
    /**
     * Atributo que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ImprimirayudaControlador
     */
    public ImprimirayudaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario=GeneralCodigoFormaEnum.FRM_IMPRESION_AYUDAS
                            .getCodigo();
            cargarFlash();
            
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
            reasignarOrigen();		    
            buscarLlave();
            registro= new Registro();
            abrirFormulario();
        
    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen(){
        
        parametrosListado.put("APLICACION", modulo);
        
        if(!"-1".equals(modulo)){

            urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            ImprimirayudaControladorUrlEnum.URL1906
                            .getValue());
        }
        
        else {
            
            urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            ImprimirayudaControladorUrlEnum.URL1907
                            .getValue());
        }
    }
    
    /**
     * Captura de las variables enviadas por flash.
     */
    public void cargarFlash() {
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                modulo = parametrosEntrada.get("Aplicacion")
                                .toString();
            }    
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3050"));
                RequestContext.getCurrentInstance().closeDialog(null);
            }
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }
    
    //<METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Imprimir
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirImprimir(Registro reg, int indice) {
        
        proceso = reg.getCampos().get("ID_PROCESO").toString();
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        
    }
    
    private void generarReporte(FORMATOS formato) {

        try {
        
            String reporte = "002476Ayudaprocesos";    
            
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
        
            reemplazar.put("proceso", proceso);

            Reporteador.resuelveConsulta(reporte,
            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    
    
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
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
            //<CODIGO_DESARROLLADO>
            //</CODIGO_DESARROLLADO>
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
            }
            /**
             * Metodo ejecutado cuando se cierra el formulario
             * 
             * TODO DOCUMENTACION ADICIONAL
             */
            public void cerrarFormulario() {
                RequestContext.getCurrentInstance().closeDialog(null);
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

            public StreamedContent getArchivoDescarga() {
                return archivoDescarga;
            }
            
        }
