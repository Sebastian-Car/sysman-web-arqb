/*-
 * SubtasadecambiosControlador.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.SubtasadecambiosControladorEnum;

/**
 * Clase sub tasa de cambio. Formulario continúo.
 *
 * @version 1.0, 18/01/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class SubtasadecambiosControlador extends BeanBaseContinuoAcmeImpl
{
	/**
	 * Código de moneda que proviene del formulario moneda.
	 */
	private String moneda;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de SubtasadecambiosControlador
	 */
	public SubtasadecambiosControlador()
	{
		super();

		try
		{
			numFormulario = GeneralCodigoFormaEnum.SUB_TASA_DE_CAMBIO_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			Map<String, Object> parametros = SessionUtil.getFlash();
			if (!parametros.isEmpty())
			{
				moneda = SysmanFunciones.toString(parametros.get("moneda"));
			}
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
	 * origenes de datos, inicialización de listas y demás necesarios.
	 */
	@PostConstruct
	public void inicializar()
	{
		enumBase = GenericUrlEnum.TASADECAMBIO;
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
	 * de la consulta del formulario. También carga la lista del formulario por
	 * primera vez.
	 */
	@Override
	public void reasignarOrigen()
	{
		buscarUrls();
		parametrosListado.put(SubtasadecambiosControladorEnum.CODIGO.getValue(), moneda);
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
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
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
	 * Método ejecutado antes de realizar la inserción del registro
	 */
	@Override
	public boolean insertarAntes()
	{
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("MONEDAORIGEN", moneda);
		registro.getCampos().put("MONEDADESTINO", moneda);
		Date fechaActual = SysmanFunciones.truncarFecha(new Date());
		registro.getCampos().put("FECHA", fechaActual);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la inserción del registro.
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
	 * Método ejecutado después de realizar la eliminación del registro.
	 */
	@Override
	public boolean eliminarDespues()
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Retorna la variable moneda
	 * 
	 * @return moneda
	 */
	public String getMoneda()
	{
		return moneda;
	}

	/**
	 * Asigna la variable moneda
	 * 
	 * @param moneda
	 * Variable a asignar en moneda
	 */
	public void setCodigo(String moneda)
	{
		this.moneda = moneda;
	}

	/**
	 * Este método se ejecuta antes enviar la acción de actualización, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro.
	 */
	@Override
	public void removerCombos()
	{
		// No hay acciones para este método.
	}

	/**
	 * Este método es ejecutado después de finalizar la inserción y edición del
	 * registro, se usa cuando se desean agregar valores al registro después de
	 * dichas acciones.
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// No hay acciones para este método.
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
