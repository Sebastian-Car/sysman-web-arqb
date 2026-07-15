/*-
 * TipoInformesEntesControlador.java
 *
 * 1.0
 * 
 * 20/11/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 20/11/2024
 * @author lvega
 */
@ManagedBean
@ViewScoped
public class  TipoInformesEntesControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	
	private int indice;


	/**
	 * Crea una nueva instancia de TipoInformesEntesControlador
	 */
	public TipoInformesEntesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=GeneralCodigoFormaEnum.FRMTIPO_INFORMES_ENTES_CONTROLADOR.getCodigo();
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

			enumBase =  GenericUrlEnum.TIPO_INFORMES_ENTES;
			reasignarOrigen();		    
			buscarLlave();
			registro= new Registro();
			abrirFormulario();
	}
	
	@Override
	public void reasignarOrigen(){
		buscarUrls();
	}
	
    /**
     * Retorna la variable indice 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
	}
	@Override
	public void asignarValoresRegistro() {

	}
	@Override
	public void removerCombos() {

	}
	@Override
	public void cancelarEdicion(RowEditEvent event) {

	}
	@Override
	public boolean insertarAntes() {

		return true;
	}
	@Override
	public boolean insertarDespues() {
		return true;
	}
	@Override
	public boolean actualizarAntes() {
		
		registro.getCampos().remove("OBSERVACION");
		registro.getCampos().remove("NOMBRE");
		registro.getCampos().remove("CODIGO");
		return true;
	}
	@Override
	public boolean actualizarDespues() {
	reasignarOrigen();
	return true;
	}
	@Override
	public boolean eliminarAntes() {
		return true;
	}
	@Override
	public boolean eliminarDespues() {
		return true;
	}
	
    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
    }
    
	public void setIndice(int indice) {
		this.indice = indice;
	}
    
}
