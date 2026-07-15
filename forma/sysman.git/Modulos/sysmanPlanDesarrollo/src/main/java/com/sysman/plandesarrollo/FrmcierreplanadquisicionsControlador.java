/*-
 * FrmcierreplanadquisicionsControlador.java
 *
 * 1.0
 * 
 * 05/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyacá.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.FrmcierreplanadquisicionsControladorEnum;
import com.sysman.util.SysmanFunciones;

/**
 * Ingresa y modifica registros en la tabla ANO para la configuración de cierre
 * de año.
 *
 * @version 1.0, 05/03/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class FrmcierreplanadquisicionsControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el código de la compañía en la
	 * cual inició sesión el usuario. El valor de esta constante es asignado en
	 * el constructor a la variable de sesión correspondiente
	 */
	private final String compania;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmcierreplanadquisicionsControlador.
	 */
	public FrmcierreplanadquisicionsControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			// Número de formulario: 1732.
			numFormulario = GeneralCodigoFormaEnum.ANO_PDD_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este método se ejecuta justo después de que el objeto de la clase del
	 * Bean ha sido creado. En este se realizan las asignaciones iniciales
	 * necesarias para la visualización del formulario, como lo son tablas,
	 * origenes de datos, inicialización de listas y demás necesarios.
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.ANO;
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
	public void reasignarOrigen() {
		buscarUrls();
		parametrosListado.put(FrmcierreplanadquisicionsControladorEnum.COMPANIA.getValue(), compania);

		/**
		 * Aquí se reasignan las rutas Url del DSS al controlador.
		 */
		urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("4058");
		urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("4060");
		urlActualizacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("4061");
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
	 * Este método es invocado por el método inicializar, donde se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del formulario.
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Método ejecutado cuando se cancela la edición del registro seleccionado.
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Método ejecutado antes de realizar la inserción del registro.
	 * 
	 * Aquí también se valida si la fecha cierre de planeación es menor que la
	 * fecha cierre de ejecución. De ser así se inserta el registro, de lo
	 * contrario no. También se valida si hay una fecha de cierre de planeación
	 * en caso de haber una de cierre de ejecución.
	 * 
	 * @return
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>

		if (registro.getCampos()
				.get(FrmcierreplanadquisicionsControladorEnum.FECHA_CIERRE_ADQUI_EJEC.getValue()) != null) {
			if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
					FrmcierreplanadquisicionsControladorEnum.FECHA_CIERRE_ADQUISICIONES.getValue())) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4007"));
				return false;
			} else {
				if (validarFechas(
						registro.getCampos()
								.get(FrmcierreplanadquisicionsControladorEnum.FECHA_CIERRE_ADQUISICIONES.getValue()),
						registro.getCampos()
								.get(FrmcierreplanadquisicionsControladorEnum.FECHA_CIERRE_ADQUI_EJEC.getValue()))) {
					return false;
				} else {
					registro.getCampos().put(FrmcierreplanadquisicionsControladorEnum.COMPANIA.getValue(), compania);
					return true;
				}
			}
		} else {
			registro.getCampos().put(FrmcierreplanadquisicionsControladorEnum.COMPANIA.getValue(), compania);
			return true;
		}

		// </CODIGO_DESARROLLADO>

	}

	/**
	 * Método ejecutado despues de realizar la insercion del registro.
	 * 
	 * @return
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la inserción y actualización del
	 * registro
	 * 
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este método se ejecuta antes enviar la acción de actualización. En el se
	 * pueden remover valores auxiliares que no se deseen o no se deban enviar
	 * en el registro.
	 * 
	 * Aquí también se valida si la fecha cierre de planeación es menor que la
	 * fecha cierre de ejecución. De ser así se inserta el registro, de lo
	 * contrario no. También se valida si hay una fecha de cierre de planeación
	 * en caso de haber una de cierre de ejecución.
	 */
	@Override
	public void removerCombos() {

		if (registro.getCampos()
				.get(FrmcierreplanadquisicionsControladorEnum.FECHA_CIERRE_ADQUI_EJEC.getValue()) != null) {

			if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
					FrmcierreplanadquisicionsControladorEnum.FECHA_CIERRE_ADQUISICIONES.getValue())) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4007"));
				return;
			} else {
				if (validarFechas(
						registro.getCampos()
								.get(FrmcierreplanadquisicionsControladorEnum.FECHA_CIERRE_ADQUISICIONES.getValue()),
						registro.getCampos()
								.get(FrmcierreplanadquisicionsControladorEnum.FECHA_CIERRE_ADQUI_EJEC.getValue()))) {
					return;
				} else {
					registro.getCampos().remove(FrmcierreplanadquisicionsControladorEnum.COMPANIA.getValue());
					registro.getCampos().remove(FrmcierreplanadquisicionsControladorEnum.NUMERO.getValue());
				}
			}

		} else {
			registro.getCampos().remove(FrmcierreplanadquisicionsControladorEnum.COMPANIA.getValue());
			registro.getCampos().remove(FrmcierreplanadquisicionsControladorEnum.NUMERO.getValue());
		}

	}

	/**
	 * Método que permite validar que la fecha inicial (fecha de cierre de
	 * planeación de adquisiciones) no sea posterior a la fecha final (fecha de
	 * cierre de ejecución de adquiciciones).
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @return
	 */
	public boolean validarFechas(Object fechaIni, Object fechaFin) {
		if (fechaIni != null && fechaFin != null) {
			Date fecIni = (Date) fechaIni;
			Date fecFin = (Date) fechaFin;

			if (fecFin.before(fecIni)) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4006"));
				return true;
			}
		}
		return false;
	}

	/**
	 * Este método es ejecutado después de finalizar la inserción y edición del
	 * registro se usa cuando se desean agregar valores al registro después de
	 * dichas acciones.
	 */
	@Override
	public void asignarValoresRegistro() {
		// No hay código aquí.
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
