/*-
 * FactorescalculoControlador.java

 *
 * 1.0
 * 
 * 25/10/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 25/10/2023
 * @author ecabrera
 */
@ManagedBean
@ViewScoped
public class  FactorescalculoControlador  extends BeanBaseContinuoAcmeImpl{
	private final String compania;
	private String codigo;
	
	private Map<String, Object> parametrosEntrada;
	
	public FactorescalculoControlador() {
    	super();
    	compania = SessionUtil.getCompania();
    	try {
    		numFormulario = GeneralCodigoFormaEnum.FACTORES_CALCULO.getCodigo();
    		validarPermisos();
    		
    		if ( SessionUtil.getUser().getCodigo().equals("PRUEBAS_SS") ) {
    			permisos[0] = true;
    			permisos[1] = true; 
    			permisos[2] = true; 
    			permisos[4] = true;
    		}
    		else {
    			permisos[0] = false;
    			permisos[1] = false; 
    			permisos[2] = false; 
    			permisos[4] = true;    			
    		}
    		
    		parametrosEntrada = SessionUtil.getFlash();
    	} catch (Exception ex) {
    			logger.error(ex.getMessage(),ex);
    			SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }
   
    @PostConstruct
    public void inicializar(){
 		enumBase = GenericUrlEnum.FACTORES_CALCULO;
 		buscarLlave();
 		reasignarOrigen();
	    registro = new Registro(new HashMap<String, Object>());
	    abrirFormulario();
    }
  
    @Override
    public void reasignarOrigen(){
    	buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
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
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        return true;
    }
    
    @Override
    public boolean insertarDespues(){
        return true;
    }
    
    @Override
    public boolean actualizarAntes(){
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
    }
    
    public void ejecutarrcCerrar() {
        Map<String, Object> param = new HashMap<>();
        param.put("CODIGO", parametrosEntrada.get("CODIGO").toString());
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.DFACTORES_CALCULO.getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

}
