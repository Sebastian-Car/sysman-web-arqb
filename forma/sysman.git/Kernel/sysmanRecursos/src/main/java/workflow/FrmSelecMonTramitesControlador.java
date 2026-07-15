/*-
 * FrmSelecMonTramitesControlador.java
 *
 * 1.0
 * 
 * 11/10/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package workflow;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import workflow.enums.SelecMonTramitesControladorUrlEnum;

/**
 *
 * @version 1.0, 11/10/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmSelecMonTramitesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private int anio;
	private Date fechaFinal;
	private Date fechaInicial;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnioFiltro;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmSelecMonTramitesControlador
	 */
	public FrmSelecMonTramitesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2426;
			validarPermisos();
			anio = SysmanFunciones.ano(new Date());
			fechaInicial = SysmanFunciones.primeroDeMesFecha(new Date());
			fechaFinal = SysmanFunciones.ultimoDiaDate(new Date());
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
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
		cargarListaAnioFiltro();
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
	/**
	 * 
	 * Carga la lista listaAnioFiltro
	 *
	 */
	public void cargarListaAnioFiltro(){

		Map<String, Object> parametros = new HashMap<>();

		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnioFiltro = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SelecMonTramitesControladorUrlEnum.URL0001
									.getValue())
							.getUrl(),
							parametros));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * en la vista
	 *
	 *
	 */
	public void oprimirAceptar() {
		//<CODIGO_DESARROLLADO>
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("anio", anio);
			parametros.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			parametros.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));

			Direccionador direccionador = new Direccionador();
			direccionador.setNumForm("2264");
			direccionador.setParametros(parametros);
			RequestContext.getCurrentInstance().closeDialog(direccionador);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * en la vista
	 *
	 *
	 */
	public void oprimirCancelar() {
		//<CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
     * Metodo ejecutado al cambiar el control AnioFiltro
     * 
     * 
     */
public void cambiarAnioFiltro() {
         //<CODIGO_DESARROLLADO>
	try {
	    int mes = SysmanFunciones.mes(new Date());
	    String fechaIni = "01/" + mes + "/" + anio;
	    
	    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	     fechaInicial = formato.parse(fechaIni);
	     fechaFinal = SysmanFunciones.ultimoDiaDate(fechaInicial);
	     
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	    
        //</CODIGO_DESARROLLADO>
    }
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public int getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnioFiltro
	 * 
	 * @return listaAnioFiltro
	 */
	public List<Registro> getListaAnioFiltro() {
		return listaAnioFiltro;
	}
	/**
	 * Asigna la lista listaAnioFiltro
	 * 
	 * @param listaAnioFiltro
	 * Variable a asignar en  listaAnioFiltro
	 */
	public void setListaAnioFiltro(List<Registro> listaAnioFiltro) {
		this.listaAnioFiltro = listaAnioFiltro;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
