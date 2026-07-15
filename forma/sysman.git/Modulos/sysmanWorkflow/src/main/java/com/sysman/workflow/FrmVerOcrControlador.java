/*-
 * FrmVerOcrControlador.java
 *
 * 1.0
 * 
 * 18/11/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/11/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmVerOcrControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 */
	private String verOcr;
	private String ocr;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmVerOcrControlador
	 */
	public FrmVerOcrControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			
			//2132
			numFormulario= GeneralCodigoFormaEnum.FRM_VER_OCR_CONTROLADOR.getCodigo();
			validarPermisos();

			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

			if(parametrosEntrada != null)
			{
				ocr = (String) parametrosEntrada.get("ocr");
				verOcr = ocr;
			}


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
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable verOcr
	 * 
	 * @return  verOcr
	 */
	public String getVerOcr() {
		return verOcr;
	}
	/**
	 * Asigna la variable  verOcr
	 * 
	 * @param  verOcr
	 * Variable a asignar en  verOcr
	 */
	public void setVerOcr(String verOcr) {
		this.verOcr = verOcr;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
