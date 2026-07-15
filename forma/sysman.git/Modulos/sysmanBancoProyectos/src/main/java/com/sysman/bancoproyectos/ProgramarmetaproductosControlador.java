/*-
 * ProgramarmetaproductosControlador.java
 *
 * 1.0
 * 
 * 18/03/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import javax.ejb.EJB;
import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.jsfutil.JsfUtil;



import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;

import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;




/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/03/2026
 * @author User
 */
@ManagedBean
@ViewScoped
public class  ProgramarmetaproductosControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    private String vigenciaActual;
    
    @EJB
    private EjbBancoProyectoCincoRemote ejbBancoProyectoCinco;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ProgramarmetaproductosControlador
     */
    public ProgramarmetaproductosControlador() {
    	super();
        compania = SessionUtil.getCompania();
        try {
        		numFormulario=GeneralCodigoFormaEnum.PROGRAMAR_META_PRODUCTO
                        .getCodigo();
        		Map<String, Object> parametros = SessionUtil.getFlash();
    			if (parametros != null) {
    				vigenciaActual = parametros.get("vigenciaActual").toString();
    			}

            validarPermisos();

        } catch (Exception ex) {
        	Logger.getLogger(ProgramarmetaproductosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
        }
    }
   
    @PostConstruct
    public void inicializar(){
		abrirFormulario();
    }
   
  @Override
	public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>

public void oprimircancelar() {
	RequestContext.getCurrentInstance().closeDialog(null);
	
    }

public void oprimiriniciar() {
	try {
		
		ejbBancoProyectoCinco.actualizarMetaProducto(compania,  Integer.parseInt(vigenciaActual),
                 SessionUtil.getUser().getCodigo());
     

        JsfUtil.agregarMensajeAlerta(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
    }
    catch (SystemException ex) {
        logger.error(ex.getMessage(), ex);
    }
    }

public String getVigenciaActual() {
	return vigenciaActual;
}

public void setVigenciaActual(String vigenciaActual) {
	this.vigenciaActual = vigenciaActual;
}



}
