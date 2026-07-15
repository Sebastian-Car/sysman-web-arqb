/*-
 * FrmURLBIControlador.java
 *
 * 1.0
 * 
 * 10/07/2025
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
import com.sysman.general.enums.FrmURLBIControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.sound.midi.SysexMessage;

import org.primefaces.event.RowEditEvent;
/**
 * esta clase permite guardar las URL  que se utilizaran para el caso de tableros BI
 *
 * @version 1.0, 10/07/2025
 * @author NCARDENAS
 */
@ManagedBean
@ViewScoped
public class  FrmURLBIControlador  extends BeanBaseContinuoAcmeImpl{
  	
	private String opcionMenu;
	private String titulo;

    public FrmURLBIControlador() {
	super();
	try {
			numFormulario = GeneralCodigoFormaEnum.FRM_URLBI_CONTROLADOR.getCodigo();
			opcionMenu = SessionUtil.getMenuActual();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
	} catch (Exception ex) {
		logger.error(ex.getMessage(),ex);
        SessionUtil.redireccionarMenuPermisos();
        }

    }
    
  @PostConstruct
    public void inicializar(){
	  	
	  		enumBase = GenericUrlEnum.URLBI;
	        reasignarOrigen();
	        buscarLlave();
	        registro = new Registro();
	        abrirFormulario();
	  	
	  		
  }	
    
    @Override
    public void reasignarOrigen(){
    	
    	String codigo = "998";
    	titulo = idioma.getString("TT_LB58069");
    	if (opcionMenu.equals("99701")) {
    		titulo = idioma.getString("TB_TB4505");
    		codigo = "997";
    	}
    	
    	parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),codigo); 
    	buscarUrls();
    }

@Override
	public void abrirFormulario(){
      
    }

 
    @Override
 public void cancelarEdicion(RowEditEvent event) {
  	 getListaInicial().load();
      }
   
    @Override
    public boolean insertarAntes(){
        
           return true;
    }
    
	@Override
    public boolean insertarDespues(){
         
        return true;
    }
  
    @Override
    public boolean actualizarAntes(){
            	
    	String url = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.URL.getName()));
    	
    	if(!esURLValida(url)) {
    		JsfUtil.agregarMensajeAlerta("La URL ingresada es incorrecta.");
    		return false;
    	}
       
         return true;
    }

    @Override   
    public boolean actualizarDespues(){
        return true;
    }
   
    @Override    
    public boolean eliminarAntes(){
         
      return true;
    }
   
    @Override   
    public boolean eliminarDespues(){
        
       return true;
    }
    
    @Override
    public void removerCombos() {
    }
  
   @Override
    public void asignarValoresRegistro()
    {
        // TODO Auto-generated method stub
    }
   
   public boolean esURLValida(String url) {
	    try {
	        new java.net.URL(url).toURI(); 
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
   
   public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
