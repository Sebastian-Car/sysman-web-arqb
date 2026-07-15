/*-
 * FrmclasecuentaControlador.java
 *
 * 1.0
 * 
 * 09/02/2022
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
 * Formulario que permite la creación de las diferentes clases de cuentas medicas
 *
 * @version 1.0, 09/02/2022
 * @author dlrangel
 */
@ManagedBean
@ViewScoped
public class FrmclasecuentaControlador extends BeanBaseContinuoAcmeImpl{
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
     * Crea una nueva instancia de FrmclasecuentaControlador
     */
	public FrmclasecuentaControlador() {
		super();
	    	compania = SessionUtil.getCompania();
 try {
		//	numFormulario = 2337;
	 numFormulario =GeneralCodigoFormaEnum.FRM_CLASE_CUENTA_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar(){	
    	enumBase = GenericUrlEnum.CM_CLASE_CUENTA;	 
    	registro = new Registro();
    	reasignarOrigen();
		buscarLlave();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        asignarValoresRegistro();

	}
    

	  @Override
	  public void abrirFormulario(){
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
	  asignarValoresRegistro();
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
        registro.getCampos().put("OBLIGA_CUV",true);
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
