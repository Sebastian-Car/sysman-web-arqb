/*-
 * FrmSelecCambioFondoControlador.java
 *
 * 1.0
 * 
 * 14/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmSelecCambioFondoControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
/**
 *
 * @version 1.0, 14/06/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmSelecCambioFondoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String periodo;
	//<DECLARAR_ATRIBUTOS>
	/**
	 */
	private String claseFondo;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 */
	private List<Registro> listaCodClase;

	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmSelecCambioFondoControlador
	 */
	public FrmSelecCambioFondoControlador() {
		super();
		compania = SessionUtil.getCompania();
		periodo = (String) SessionUtil.getSessionVar("periodoNomina");
		try {
			//2413
			numFormulario= GeneralCodigoFormaEnum.FRM_SELEC_CAMBIO_FONDO_CONTROLADOR.getCodigo();;
			validarPermisos();
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
		cargarListaCodClase();
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

	public void mensajesInicioModal() {

		if(!periodo.equals("33")) {

			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4426"));

		}
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCodClase
	 *
	 */
	public void cargarListaCodClase(){
		try {
			Map<String, Object> param = new HashMap<>();
			param.put("CODIGOS", "AFP,EPS,ARL,APV,FMP");

			listaCodClase = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmSelecCambioFondoControladorUrlEnum.URL475002.getValue()).getUrl(),param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton cmdAceptar
	 * en la vista
	 *
	 *
	 */
	public void oprimircmdAceptar() {
		//<CODIGO_DESARROLLADO>
		Direccionador direccionador = new Direccionador();
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("claseFondo", claseFondo);


		direccionador.setNumForm(Integer.toString(
				GeneralCodigoFormaEnum.FRM_CAMBIAR_FONDO_CONTROLADOR
				.getCodigo()));
		direccionador.setParametros(parametros);
		RequestContext.getCurrentInstance().closeDialog(direccionador);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CmdCancelar
	 * en la vista
	 *
	 *
	 */
	public void oprimirCmdCancelar() {
		//<CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable claseFondo
	 * 
	 * @return  claseFondo
	 */
	public String getClaseFondo() {
		return claseFondo;
	}
	/**
	 * Asigna la variable  claseFondo
	 * 
	 * @param  claseFondo
	 * Variable a asignar en  claseFondo
	 */
	public void setClaseFondo(String claseFondo) {
		this.claseFondo = claseFondo;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaCodClase
	 * 
	 * @return listaCodClase
	 */
	public List<Registro> getListaCodClase() {
		return listaCodClase;
	}
	/**
	 * Asigna la lista listaCodClase
	 * 
	 * @param listaCodClase
	 * Variable a asignar en  listaCodClase
	 */
	public void setListaCodClase(List<Registro> listaCodClase) {
		this.listaCodClase = listaCodClase;
	}
	/**
	 * @return the periodo
	 */
	public String getPeriodo() {
		return periodo;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
