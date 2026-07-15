package com.sysman.mantenimientoactivos;
import java.util.HashMap;
import java.util.Map;

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
 * @version 1.0, 24/11/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  EstadoActivosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	
	private String opcion;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de EstadoActivosControlador
	 */
	public EstadoActivosControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario= GeneralCodigoFormaEnum.ESTADO_ACTIVOS_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} 
		catch (Exception ex) {
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
		opcion = "1";
		//</CODIGO_DESARROLLADO>
	}
	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * en la vista
	 */
	public void oprimirAceptar() {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> parametro = new HashMap<>();
		parametro.put("opcion", opcion);
		Direccionador direccionar = new Direccionador();
		direccionar.setNumForm(Integer.toString(GeneralCodigoFormaEnum.INVENTARIO_ACTIVOS_CONTROLADOR.getCodigo()));
		direccionar.setParametros(parametro);
		RequestContext.getCurrentInstance().closeDialog(direccionar);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * en la vista
	 *
	 */
	public void oprimirCancelar() {
		//<CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Retorna la variable opcion
	 * 
	 * @return  opcion
	 */
	public String getOpcion() {
		return opcion;
	}
	/**
	 * Asigna la variable  opcion
	 * 
	 * @param  opcion
	 * Variable a asignar en  opcion
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
	public String getCompania() {
		return compania;
	}
	
}
