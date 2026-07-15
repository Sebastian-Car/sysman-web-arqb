/*-
 * FrmTipoMedioControlador.java
 *
 * 1.0
 * 
 * 05/12/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 05/12/2019
 * @author jalfonsop
 */
@ManagedBean
@ViewScoped
public class  FrmTipoMedioControlador  extends BeanBaseContinuoAcmeImpl{
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
//</DECLARAR_LISTAS_COMBO_GRANDE>
	 @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de FrmTipoMedioControlador
     */
    public FrmTipoMedioControlador() {
	super();
	compania = SessionUtil.getCompania();
 try {
	 //2139
		numFormulario= GeneralCodigoFormaEnum.FRM_TIPO_MEDIO_CONTROLADOR.getCodigo();
		validarPermisos();
		//<INI_ADICIONAL>
		//</INI_ADICIONAL>
	}
 
 catch (Exception ex) {
	 		logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
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
	  enumBase = GenericUrlEnum.TIPO_MEDIO;
	    
 buscarLlave();
 reasignarOrigen();		
				registro= new Registro(new HashMap<String, Object>());
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
//</CARGAR_LISTA_COMBO_GRANDE>
				abrirFormulario();

    }
  
    @Override
    public void reasignarOrigen(){
buscarUrls();
parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    }
    
    public void generarConsecutivo() {
		try {
			
			
			long codigo = ejbSysmanUtil.generarConsecutivoConValorInicial(
			        "TIPO_MEDIO",
			        "COMPANIA = ''" + compania + "''", "CODIGO", "1");
			
			registro.getCampos().put("CODIGO", codigo);
			
			
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

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
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		generarConsecutivo();
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
//</SET_GET_LISTAS_COMBO_GRANDE>
}
