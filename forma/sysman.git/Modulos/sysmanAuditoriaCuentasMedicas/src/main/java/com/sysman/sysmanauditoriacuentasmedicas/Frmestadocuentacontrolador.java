/*-
 * Frmestadocuentacontrolador.java
 *
 * 1.0
 * 
 * 15/02/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 15/02/2022
 * @author dlrangel
 */
@ManagedBean
@ViewScoped
public class Frmestadocuentacontrolador extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
//</DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de Frmestadocuentacontrolador
     */
	public Frmestadocuentacontrolador() {
		super();
	 	compania = SessionUtil.getCompania();
	 	 try {
	 			//	numFormulario = 2339;
	 		 numFormulario =GeneralCodigoFormaEnum.FRM_ESTADO_CUENTA_CONTROLADOR
	 	             .getCodigo(); 
	 	  validarPermisos();
	 	//<INI_ADICIONAL>
	 	//</INI_ADICIONAL>
	 			 } catch (Exception ex) {
	 	logger.error(ex.getMessage(),ex);
	 	            SessionUtil.redireccionarMenuPermisos();
	 	        } 
	 	    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
	 @PostConstruct
	    public void inicializar(){
		//tabla="";
	    	enumBase = GenericUrlEnum.CM_ESTADO_CUENTA;	 
	    	reasignarOrigen();
			buscarLlave();
		   registro = new Registro();
	        // <CARGAR_LISTA>
	        // </CARGAR_LISTA>
	        // <CARGAR_LISTA_COMBO_GRANDE>
	        // </CARGAR_LISTA_COMBO_GRANDE>
	        abrirFormulario();
		}
	    /**
	     * Se realiza la asignacion de la variable origenDatos por la
	     * consulta correspondiente del formulario
	     * 
	     * TODO DOCUMENTACION ADICIONAL
	     * 
	     */
	@Override
	  public void reasignarOrigen() {
	//origenDatos="";
		  buscarUrls();
	}
	    /**
	     * Se realiza la asignacion de la variable origenGrilla por la
	     * consulta correspondiente de la grilla del formulario, se hace
	     * la asignacion de dicha consulta a los objetos listaInicial y
	     * listaInicialF
	     * 
	     * TODO DOCUMENTACION ADICIONAL
	     * 
	     */
	  /**public void reasignarOrigenGrilla() {
		origenGrilla="";
	  if (listaInicial != null) {
	            listaInicial.setOrigen(origenGrilla);
	        }
	        if (listaInicialF != null) {
	            listaInicialF.setOrigen(origenGrilla);
	        }
	}*/
	//<METODOS_CARGAR_LISTA>	
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
	//</METODOS_SUBFORM>	
	//<METODOS_ADICIONALES>	
	//</METODOS_ADICIONALES>
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
	     * Metodo ejecutado en el momento despues de cargar el registro
	     * 
	     * TODO DOCUMENTACION ADICIONAL
	     */
	    /**@Override
	    public void cargarRegistro() {
	        //<CODIGO_DESARROLLADO>
	        precargarRegistro();
	        //</CODIGO_DESARROLLADO>
	    }*/
	@Override
	    public boolean insertarAntes(){
	         //<CODIGO_DESARROLLADO>
			 registro.getCampos().put("COMPANIA", compania);
	        //</CODIGO_DESARROLLADO>
			return true;
	    }
	    @Override
	    public boolean insertarDespues(){
	         //<CODIGO_DESARROLLADO>
	        //</CODIGO_DESARROLLADO>
			return true;
	    }
	@Override
	    public boolean actualizarAntes(){
	         //<CODIGO_DESARROLLADO>
	        //</CODIGO_DESARROLLADO>
			return true;
	    }
	    @Override
	    public boolean actualizarDespues(){
	         //<CODIGO_DESARROLLADO>
	        //</CODIGO_DESARROLLADO>
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
		public void asignarValoresRegistro() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void removerCombos() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void cancelarEdicion(RowEditEvent event) {
			// TODO Auto-generated method stub
			
		}
	}
