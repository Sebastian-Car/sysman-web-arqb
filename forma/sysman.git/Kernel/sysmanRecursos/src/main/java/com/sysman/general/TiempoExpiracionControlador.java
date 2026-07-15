/*-
 * TiempoExpiracion.java
 *
 * 1.0
 * 
 * 12/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.TiempoExpiracionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
/**
 * Formulario permite configurar el tiempo de expiración de la sesion
 *
 * @version 1.0, 12/07/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  TiempoExpiracionControlador extends BeanBaseModal{
    
    //<DECLARAR_ATRIBUTOS>
    /**
     * Variable que guarda los mintos ingresados por usuario
     */
    private String minuto;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de TiempoExpiracion
     */
    public TiempoExpiracionControlador() {
        super();
        
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_TIEMPOEXPIRACION_CONTROLADOR.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
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
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
        abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        valorModificar();
        //</CODIGO_DESARROLLADO>
    }
    //<METODOS_CARGAR_LISTA>
    //</METODOS_CARGAR_LISTA>
    
    public void valorModificar() {
        try {
            
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        SessionUtil.getUser().getCodigo());

        
            Registro registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TiempoExpiracionControladorUrlEnum.URL6228
                                                                            .getValue())
                                            .getUrl(), param));
            
            minuto = registro != null ? registro.getCampos().get("MINUTOSBLOQUEO").toString() : "0";
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        
         
    }
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * en la vista
     *
     * Guarda registro
     *
     */
    public void oprimirAceptar() {
        //<CODIGO_DESARROLLADO>
        
        
        if(Integer.parseInt(minuto) < 5) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4159"));
            minuto = "0";
        } else {
            try {
                
        Map<String, Object> parametros = new HashMap<>();
        
        parametros.put("KEY_CODIGO", SessionUtil.getUser().getCodigo());
        parametros.put("MINUTOSBLOQUEO", minuto);
        parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());

        Parameter parameter = new Parameter();

        parameter.setFields(parametros);
        
        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiempoExpiracionControladorUrlEnum.URL7918
                                                        .getValue());
        int numActaliz =  requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
        
        if(numActaliz == 1) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
      
        } 
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        
        }
           
        //</CODIGO_DESARROLLADO>
    }
    }
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable minuto
     * 
     * @return  minuto
     */
    public String getMinuto() {
        return minuto;
    }
    /**
     * Asigna la variable  minuto
     * 
     * @param  minuto
     * Variable a asignar en  minuto
     */
    public void setMinuto(String minuto) {
        this.minuto = minuto;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>     
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
