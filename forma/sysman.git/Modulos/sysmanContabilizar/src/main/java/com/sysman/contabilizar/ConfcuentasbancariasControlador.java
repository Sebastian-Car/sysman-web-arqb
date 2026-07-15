/*-
 * ConfcuentasbancariasControlador.java
 *
 * 1.0
 * 
 * 05/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyacá.
 * All rights reserved.
 */
package com.sysman.contabilizar;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilizar.enums.ConfcuentasbancariasControladorEnum;
import com.sysman.contabilizar.enums.ConfcuentasbancariasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

/**
 * Listado de códigos de Plan contable con movimiento tipo 0 y clase cuenta B.
 * En frente de estos se modifica el número de la cuenta bancaria.
 *
 * @version 1.0, 05/07/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class ConfcuentasbancariasControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el código de la compañia en la cual
	 * inicio sesión el usuario. El valor de esta constante es asignado en el
	 * constructor a la variable de sesión correspondiente.
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Año correspondiente al combo cmbAno.
	 */
	private int anio;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista del combo cmbAno.
	 */
	private List<Registro> listacmbAno;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ConfcuentasbancariasControlador.
	 */
	public ConfcuentasbancariasControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			// Número de formulario: 1848.
			numFormulario = GeneralCodigoFormaEnum.CONF_CUENTAS_BANCARIAS_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este método se ejecuta justo después de que el objeto de la clase del Bean ha
	 * sido creado. En este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como lo son tablas, origenes de datos,
	 * inicialización de listas y demás necesarios.
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.PLANCONTABLE;
		anio = SysmanFunciones.ano(new Date());

		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		// <CARGAR_LISTA>
		cargarListacmbAno();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

	}

	/**
	 * En este método se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. También carga la lista del formulario por primera
	 * vez.
	 */
	@Override
	public void reasignarOrigen() {

		buscarUrls();
		parametrosListado.put(ConfcuentasbancariasControladorEnum.COMPANIA.getValue(), compania);
		parametrosListado.put(ConfcuentasbancariasControladorEnum.ANO.getValue(), anio);

		urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("16136");
		urlActualizacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("16140");
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacmbAno.
	 *
	 */
	public void cargarListacmbAno() {

		Map<String, Object> param = new TreeMap<>();
		param.put(ConfcuentasbancariasControladorEnum.COMPANIA.getValue(), String.valueOf(compania));

		try {
			listacmbAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ConfcuentasbancariasControladorUrlEnum.URL161.getValue())
											.getUrl(),
									param));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Método ejecutado al cambiar el control cmbAno.
	 * 
	 */
	public void cambiarcmbAno() {
		// <CODIGO_DESARROLLADO>
		reasignarOrigen();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Método ejecutado al cambiar el control cmbAno en la fila seleccionada dentro
	 * de la grilla.
	 * 
	 * @param rowNum
	 *            indice de la fila seleccionada.
	 */
	public void cambiarcmbAnoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este método es invocado del metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario.
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Método ejecutado cuando se cancela la edición del registro seleccionado.
	 * 
	 */

	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Método ejecutado antes de realizar la inserción del registro.
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la inserción del registro.
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la inserción y actualización del registro.
	 * 
	 */
	@Override
	public boolean actualizarAntes() {
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
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la eliminación del registro.
	 * 
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la eliminación del registro.
	 * 
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este método se ejecuta antes de enviar la acción de actualización, en él se
	 * pueden remover valores auxiliares que no se deseen o se deban enviar en el
	 * registro.
	 */
	@Override
	public void removerCombos() {
		registro.getCampos().remove(ConfcuentasbancariasControladorEnum.COMPANIA.getValue());
		registro.getCampos().remove(ConfcuentasbancariasControladorEnum.ANO.getValue());
	}

	/**
	 * Este método es ejecutado después de finalizar la inserción y edición del
	 * registro. Se usa cuando se desean agregar valores al registro después de
	 * dichas acciones.
	 */
	@Override
	public void asignarValoresRegistro() {
		// No hay código aquí.
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio.
	 * 
	 * @return anio
	 */
	public int getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio.
	 * 
	 * @param anio
	 *            Variable a asignar en anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listacmbAno.
	 * 
	 * @return listacmbAno
	 */
	public List<Registro> getListacmbAno() {
		return listacmbAno;
	}

	/**
	 * Asigna la lista listacmbAno.
	 * 
	 * @param listacmbAno
	 *            Variable a asignar en listacmbAno.
	 */
	public void setListacmbAno(List<Registro> listacmbAno) {
		this.listacmbAno = listacmbAno;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
