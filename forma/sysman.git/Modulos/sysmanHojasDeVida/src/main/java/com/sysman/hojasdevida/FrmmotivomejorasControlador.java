/*-
 * FrmmotivomejorasControlador.java
 *
 * 1.0
 * 
 * 17/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

/**
 * Clase Motivo de mejoras, formulario 1602.
 *
 * @version 1.0, 17/01/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class FrmmotivomejorasControlador extends BeanBaseContinuoAcmeImpl
{
	/**
	 * Constante a nivel de clase que almacena el código de la compañía en la
	 * cual inició sesión el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesión correspondiente.
	 */
	private final String compania;

	/**
	 * Implementación del EJB de SysmanUtil para acceder a funciones y/o
	 * procedimientos definidos en el paquete PCK_SYSMAN_UTL.
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmmotivomejorasControlador.
	 */
	public FrmmotivomejorasControlador()
	{
		super();
		compania = SessionUtil.getCompania();
		try
		{
			numFormulario = GeneralCodigoFormaEnum.FRM_MOTIVO_MEJORA_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este método se ejecuta justo después de que el objeto de la clase del
	 * Bean ha sido creado. En este se realizan las asignaciones iniciales
	 * necesarias para la visualización del formulario, como son tablas,
	 * origenes de datos, inicialización de listas y demas necesarios.
	 */
	@PostConstruct
	public void inicializar()
	{
		enumBase = GenericUrlEnum.EV_MOTIVO_MEJORAMIENTO;
		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	/**
	 * En este método se asigna al atributo origenDatos del bean base el valor
	 * de la consulta del formulario. Támbien carga la lista del formulario por
	 * primera vez.
	 */
	@Override
	public void reasignarOrigen()
	{
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	}

	// <METODOS_CARGAR_LISTA>
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este método es invocado por el método inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del formulario.
	 */
	@Override
	public void abrirFormulario()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Método ejecutado cuando se cancela la edición del registro seleccionado.
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event)
	{
		getListaInicial().load();
	}

	/**
	 * Método ejecutado antes de realizar la inserción del registro.
	 * 
	 */
	@Override
	public boolean insertarAntes()
	{
		// <CODIGO_DESARROLLADO>
		determinarConsecutivo();
		registro.getCampos().put("COMPANIA", compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la inserción del registro.
	 * 
	 */
	@Override
	public boolean insertarDespues()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la inserción y actualización del
	 * registro.
	 */
	@Override
	public boolean actualizarAntes()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la inserción y actualización del
	 * registro.
	 * 
	 */
	@Override
	public boolean actualizarDespues()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la eliminación del registro.
	 */
	@Override
	public boolean eliminarAntes()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la eliminacion del registro.
	 */
	@Override
	public boolean eliminarDespues()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este método se ejecuta antes envíar la acción de actualización, en él se
	 * pueden remover valores auxiliares que no se deseen o se deban envíar en
	 * el registro.
	 */
	@Override
	public void removerCombos()
	{
		registro.getCampos().remove("CODIGO");
		registro.getCampos().remove("COMPANIA");
	}

	/**
	 * Método para obtener el consecutivo del código del registro antes de la
	 * inserción.
	 */
	public void determinarConsecutivo()
	{
		try
		{
			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
					"EV_MOTIVO_MEJORAMIENTO",
					"COMPANIA = ''" + compania + "''",
					"CODIGO");
			registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), consecutivo);
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Este método es ejecutado después de finalizar la inserción y edición del
	 * registro, se usa cuando se desean agregar valores al registro después de
	 * dichas acciones.
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// No se requieren agregar valores al registro.
	}
	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
