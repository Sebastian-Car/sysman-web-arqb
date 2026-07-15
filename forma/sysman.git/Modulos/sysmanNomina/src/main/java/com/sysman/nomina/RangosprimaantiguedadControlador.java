/*-
 * RangosprimaantiguedadControlador.java
 *
 * 1.0
 * 
 * 18/10/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.TiposretencionesControladorEnum;
import com.sysman.nomina.enums.TiposretencionesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/10/2023
 * @author ecabrera
 */
@ManagedBean
@ViewScoped
public class  RangosprimaantiguedadControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;

	public RangosprimaantiguedadControlador() {
		
		 super();
         compania = SessionUtil.getCompania();    
         try
         {
        	 numFormulario = GeneralCodigoFormaEnum.RANGOS_PRIMA_ANTIGUEDAD_CONTROLADOR.getCodigo();
             validarPermisos();
         }
         catch (SysmanException ex)
         {
             logger.error(ex.getMessage(), ex);
             SessionUtil.redireccionarMenuPermisos();
         }
    }
   
	@PostConstruct
	public void inicializar()
	{
	     enumBase = GenericUrlEnum.RANGOS_PRIMA_ANTIGUEDAD;
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
    	//registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
         return true;
    }
    
    @Override   
    public boolean actualizarDespues(){
        return true;
    }
    
    @Override    
    public boolean eliminarAntes(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
      return true;
    }
    
    @Override   
    public boolean eliminarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
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
}
