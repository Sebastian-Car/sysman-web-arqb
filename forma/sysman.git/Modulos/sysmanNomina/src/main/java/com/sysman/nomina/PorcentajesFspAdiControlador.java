/*-
 * PorcentajesFspAdiControlador.java
 *
 * 1.0
 * 
 * 09/05/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.apache.commons.io.IOUtils;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.PorcentajesFspAdiControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;
/**
 *
 * @version 1.0, 09/05/2025
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  PorcentajesFspAdiControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	private String estado;
	private int vigencia;
	private String salario;

	private String imagen;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 */
	private List<Registro> listaVigencia;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbNominaUnoRemote ejbNominaUno;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de PorcentajesFspAdiControlador
	 */
	public PorcentajesFspAdiControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2512
			numFormulario = GeneralCodigoFormaEnum.PORCENTAJES_FSP_ADI_CONTROLADOR.getCodigo();
			estado = "A";
			vigencia = SysmanFunciones.ano(new Date());
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
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

		enumBase = GenericUrlEnum.PORCENTAJES_FSP_Y_ADICIONAL;
		buscarLlave();
		registro = new Registro();
		reasignarOrigen();
		//<CARGAR_LISTA>
		cargarListaVigencia();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		parametrosListado.put(GeneralParameterEnum.ANIO.getName(),
				vigencia);
		parametrosListado.put(GeneralParameterEnum.ESTADO.getName(),
				estado);
		
		cargarImagen();
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaVigencia
	 *
	 */
	public void cargarListaVigencia(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		try {
			listaVigencia = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
					PorcentajesFspAdiControladorUrlEnum.URL4001
					.getValue())
					.getUrl(), param));
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
	 * Metodo ejecutado al oprimir el boton Actualizar
	 * en la vista
	 *
	 *
	 */
	public void oprimirActualizar() {
		//<CODIGO_DESARROLLADO>
		try {
			ejbNominaUno.porcentajesFsp(compania, vigencia, Integer.parseInt(salario), SessionUtil.getUser().getCodigo());
			
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_PROCESO_EJECUTADO"));
			
			reasignarOrigen();
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Estado
	 * 
	 * 
	 */
	public void cambiarEstado() {
		//<CODIGO_DESARROLLADO>
		reasignarOrigen();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Vigencia
	 * 
	 * 
	 */
	public void cambiarVigencia() {
		//<CODIGO_DESARROLLADO>
		reasignarOrigen();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	public void cargarImagen() {

		InputStream archivo = null;
		imagen = null;
		try {

			String rutaImagen = SysmanFunciones.toString(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"RUTA IMAGEN PORCENTAJES FSP",
					SessionUtil.getModulo(), new Date(), false),""));


			// Verificar si el campo "RUTA" existe y no es nulo
			if (rutaImagen != null) {
				File ficheroImagen = new File(rutaImagen);

				if (!ficheroImagen.exists()) {
					throw new IOException("El archivo no se encontró: " + rutaImagen);
				}

				archivo = new FileInputStream(ficheroImagen);
				imagen = JsfUtil.encodeImage(IOUtils.toByteArray(archivo));

				//ejecuta la expecion de javascrpit para eliminar el parametro: pfdrid_c=true
				JsfUtil.ejecutarJavaScript("cargarImagen('FRFR2512:IM2075')");

			} else {
				logger.warn("El campo 'RUTA' no está disponible o es nulo.");
				imagen = ""; 
			}
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error al procesar el archivo: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (archivo != null) {
				try {
					archivo.close();
				} catch (IOException e) {
					System.err.println("Error al cerrar el archivo: " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Recupera el nombre del periodo de la opcion 31 en la tabla
	 * PARAMETROS_DE_ENTRADA.
	 * 
	 * @return
	 */
	private String salarioMinimo() {
		String par = "0";

		try {

			par = ejbNominaUno.getParametroNomina(compania, 20);
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return par;
	}


	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		salario = salarioMinimo();
		cargarImagen();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		String rangoInferior = SysmanFunciones.toString(registro.getCampos().get("RANGO_INFERIOR"));
		String rangoSuperior = SysmanFunciones.toString(registro.getCampos().get("RANGO_SUPERIOR"));
		if(rangoInferior == null || rangoSuperior == null) {
	         JsfUtil.agregarMensajeAlerta("Se esta dejando un valor obligatorio vacío. Por favor verificar los datos ingresados");
			return false;
		}
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), vigencia);
		registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), estado);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
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
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		String rangoInferior = SysmanFunciones.toString(registro.getCampos().get("RANGO_INFERIOR"));
		String rangoSuperior = SysmanFunciones.toString(registro.getCampos().get("RANGO_SUPERIOR"));
		if(rangoInferior == null || rangoInferior.isEmpty() || rangoSuperior == null || rangoSuperior.isEmpty()) {
	         JsfUtil.agregarMensajeAlerta("Se esta dejando un valor obligatorio vacío. Por favor verificar los datos ingresados");
			return false;
		}
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
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
	 * 
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
	 * 
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
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable estado
	 * 
	 * @return  estado
	 */
	public String getEstado() {
		return estado;
	}
	/**
	 * Asigna la variable  estado
	 * 
	 * @param  estado
	 * Variable a asignar en  estado
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}
	/**
	 * Retorna la variable vigencia
	 * 
	 * @return  vigencia
	 */
	public int getVigencia() {
		return vigencia;
	}
	/**
	 * Asigna la variable  vigencia
	 * 
	 * @param  vigencia
	 * Variable a asignar en  vigencia
	 */
	public void setVigencia(int vigencia) {
		this.vigencia = vigencia;
	}
	/**
	 * Retorna la variable salario
	 * 
	 * @return  salario
	 */
	public String getSalario() {
		return salario;
	}
	/**
	 * Asigna la variable  salario
	 * 
	 * @param  salario
	 * Variable a asignar en  salario
	 */
	public void setSalario(String salario) {
		this.salario = salario;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaVigencia
	 * 
	 * @return listaVigencia
	 */
	public List<Registro> getListaVigencia() {
		return listaVigencia;
	}
	/**
	 * Asigna la lista listaVigencia
	 * 
	 * @param listaVigencia
	 * Variable a asignar en  listaVigencia
	 */
	public void setListaVigencia(List<Registro> listaVigencia) {
		this.listaVigencia = listaVigencia;
	}
	/**
	 * @return the imagen
	 */
	public String getImagen() {
		return imagen;
	}
	/**
	 * @param imagen the imagen to set
	 */
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
