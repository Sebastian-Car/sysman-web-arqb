package com.sysman.mantenimientoactivos;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;

/**
 *
 * @author ngomez
 * @version 1, 21/09/2015
 * 
 * @author asana
 * @version 2, 12/06/2017 Redireccion de formulario.
 * 
 * @author jcrodriguez,Refactoring
 * @version 3, 06/09/2017
 */
@ManagedBean
@ViewScoped
public class EstadoVehiculosControlador extends BeanBaseModal {

	private String opcion;
	private String menuActual;
	private String tituloEtiqueta;
	private String tituloModal;
	private String encabezadoModal;

	/**
	 * Creates a new instance of EstadoVehiculosControlador
	 */
	public EstadoVehiculosControlador() {
		super();
		try {
			menuActual = SessionUtil.getMenuActual();
			numFormulario = GeneralCodigoFormaEnum.ESTADO_VEHICULOS_CONTROLADOR.getCodigo();

			if ("50010501".equals(menuActual)) {
				tituloEtiqueta = "ESTADO DE LOS VEHICULOS";
				tituloModal = "Estado de los vehículos";
				encabezadoModal = "Estado de los vehículos";

			} else {
				tituloEtiqueta = "ESTADO DE ACTIVOS";
				tituloModal = "Estado de Activos";
				encabezadoModal = "Estado de Activos";

			}

			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(EstadoVehiculosControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void inicializar() {
		abrirFormulario();
	}

	public void oprimirAceptar() {

		Map<String, Object> parametros = new HashMap<>();
		parametros.put("opcion", opcion);
		Direccionador direccionador = new Direccionador();
		if ("50010501".equals(menuActual)) {
			direccionador.setNumForm(
					String.valueOf(GeneralCodigoFormaEnum.INVENTARIOPARQUEAUTOMOTOR_CONTROLADOR.getCodigo()));
			direccionador.setParametros(parametros);
		} else {
			direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.INVENTARIO_ACTIVOS_CONTROLADOR.getCodigo()));
			direccionador.setParametros(parametros);
		}

		RequestContext.getCurrentInstance().closeDialog(direccionador);

	}

	public void oprimirCancelar() {
		// <CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		// </CODIGO_DESARROLLADO>
	}

	public String getOpcion() {
		return opcion;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		opcion = "1";
		// </CODIGO_DESARROLLADO>
	}

	public String getTituloEtiqueta() {
		return tituloEtiqueta;
	}

	public void setTituloEtiqueta(String tituloEtiqueta) {
		this.tituloEtiqueta = tituloEtiqueta;
	}

	public String getTituloModal() {
		return tituloModal;
	}

	public void setTituloModal(String tituloModal) {
		this.tituloModal = tituloModal;
	}

	public String getEncabezadoModal() {
		return encabezadoModal;
	}

	public void setEncabezadoModal(String encabezadoModal) {
		this.encabezadoModal = encabezadoModal;
	}

}
