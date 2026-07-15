/*-
 * FrmFormuladorNominaControlador.java
 *
 * 1.0
 * 
 * 09/01/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

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
import java.util.TreeMap;

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
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmFormuladorNominaControladorEnum;
import com.sysman.general.enums.FrmFormuladorNominaControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 09/01/2026
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmFormuladorNominaControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private Boolean si;
	private Boolean no;
	private Date fechaIni;
	private Date fechaFin;
	private String beneficio;
	private String concepto;
	private String formula = "";
	private String tipoBeneficioFormula;

	private List<Registro> listaConcepto;
	private RegistroDataModelImpl listaFactor;
	private String operador;
	private RegistroDataModelImpl listaListafactores;

	private Registro registroSub;
	private String nombreConcepto;
	private String tipoFactor;
	private boolean beneficioValido;

//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmFormuladorNominaControlador
	 */
	public FrmFormuladorNominaControlador() {
		super();
		compania = SessionUtil.getCompania();
		Registro registro = new Registro();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_FORMULADOR_NOMINA_CONTROLADOR.getCodigo();
			validarPermisos();
			registroSub = new Registro(new HashMap<String, Object>());

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		cargarListaConcepto();
		cargarListaFactor();
	}

	private void cargarListaFactor() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmFormuladorNominaControladorUrlEnum.URL151001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaFactor = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.ID_DE_CONCEPTO.getName());
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		cargarListaListafactores();
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		listaListafactores = null;
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.FORMULAS_PROCEDIMIENTO;
		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
	}

	/**
	 * Se realiza la asignacion de la variable origenGrilla por la consulta
	 * correspondiente de la grilla del formulario, se hace la asignacion de dicha
	 * consulta a los objetos listaInicial y listaInicialF
	 * 
	 * 
	 */

//<METODOS_CARGAR_LISTA>	
//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
	/**
	 * 
	 * Carga la lista listaAnoTrabajo
	 *
	 * 
	 */
	public void cargarListaConcepto() {
		Map<String, Object> param = new TreeMap<>();

		try {
			listaConcepto = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmFormuladorNominaControladorUrlEnum.URL1995001.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaListafactores() {
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmFormuladorNominaControladorUrlEnum.URL1999001.getValue());

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(FrmFormuladorNominaControladorEnum.PROCEDIMIENTO.getValue(), beneficio);

			listaListafactores = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					CacheUtil.getLlaveServicio(urlConexionCache, "FACTORES_CALCULO_FORMULADOR"));
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void agregarRegistroSubListafactores() {
		try {
			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.ID_DE_FACTOR.getValue(), concepto);
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.ID_DE_CONCEPTO.getValue(), concepto);
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.ALIAS_CONCEPTO.getValue(), nombreConcepto);
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.PROCEDIMIENTO.getValue(), beneficio);
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.ACUMULADO.getValue(), "1");
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.PERIODICIDAD.getValue(), "1");
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.DOCEAVA.getValue(), "1");
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.TIPO_DOCEAVA.getValue(), "1");
			registroSub.getCampos().put(FrmFormuladorNominaControladorEnum.CONCEPTO_RESULTANTE.getValue(), "160");
			registroSub.getCampos().put("CREATED_BY", SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put("DATE_CREATED", new Date());
			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.FACTORES_CALCULO_FORMULADOR.getCreateKey());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSub.getCampos());

			cargarListaListafactores();
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSub = new Registro(new HashMap<String, Object>());
		}
	}

	/**
	 * Metodo de edicion del formulario Listafactores
	 * 
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubListafactores(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			int conteo;
			conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, "DUAL", reg.getCampos(), reg.getLlave());
			if (conteo > 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
			}
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException
				| NamingException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			listaListafactores.load();
		}
	}

	/**
	 * Metodo de eliminacion del formulario Listafactores
	 * 
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubListafactores(Registro reg) {
		try {
			int conteo;
			conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, "DUAL", reg.getLlave());
			if (conteo > 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			}
			listaListafactores.load();
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException
				| NamingException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Listafactores
	 *
	 */
	public void cancelarEdicionListafactores() {
		cargarListaListafactores();
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Agregar en la vista
	 *
	 *
	 */
	public void oprimirAgregarConcepto() {
		// <CODIGO_DESARROLLADO>
		if (concepto != null) {
			formula += concepto;
			agregarRegistroSubListafactores();
//			tipoBeneficioFormula = beneficio;
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Eliminar en la vista
	 *
	 *
	 */
	public void oprimirEliminarOperador() {
		// <CODIGO_DESARROLLADO>
		if (formula.length() > 0) {
			formula = formula.substring(0, formula.length() - 1);
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Comando51 en la vista
	 *
	 *
	 */
	public void oprimirEditar2() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Eliminar2 en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirEliminar2() {

	}

//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	
//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>	
//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		concepto = "";
		fechaIni = null;
		fechaFin = null;
		beneficio = SysmanFunciones.toString(registro.getCampos().get("PROCEDIMIENTO"));
		formula = SysmanFunciones.toString(registro.getCampos().get("FORMULA_APLICADA"));
		tipoBeneficioFormula = SysmanFunciones.toString(registro.getCampos().get("PROCEDIMIENTO"));
		beneficioValido = true;
		cargarListaListafactores();
		// if (registro == null || registro.getCampos() == null ||
		// registro.getCampos().isEmpty()) {
		JsfUtil.agregarMensajeAlerta("No existe procedimiento");
		// }

		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaFactor(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		concepto = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.ID_DE_CONCEPTO.getName()));
		nombreConcepto = SysmanFunciones
				.toString(registroAux.getCampos().get(FrmFormuladorNominaControladorEnum.NOMBRE_CONCEPTO.getValue()));

	}

	public void oprimirAgregarOperador() {
		// <CODIGO_DESARROLLADO>
		formula += operador;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control operador
	 * 
	 * 
	 */
	public void cambiaroperador() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarConcepto() {
		tipoBeneficioFormula = beneficio;
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.CODIGO.getName(), beneficio);

		Registro rsExiste;
		try {
			rsExiste = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmFormuladorNominaControladorUrlEnum.URL2000001.getValue())
									.getUrl(),
							param));

			if (rsExiste != null && SysmanFunciones.nvl(rsExiste.getCampos().get("EXISTE"), 0).equals(0)) {
				beneficioValido = true;
			} else {
				beneficioValido = false;
				JsfUtil.agregarMensajeError("Ya existe una formulación para el beneficio seleccionado.");
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.CODIGO.getName(), beneficio);

		Registro rsExiste;
		try {
			rsExiste = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmFormuladorNominaControladorUrlEnum.URL2000001.getValue())
									.getUrl(),
							param));

			if (!(rsExiste != null && SysmanFunciones.nvl(rsExiste.getCampos().get("EXISTE"), 0).equals(0))) {
				JsfUtil.agregarMensajeError("Ya existe una formulación para el beneficio seleccionado.");
				return false;
			}

			if (contieneAlMenosUnNumero(formula)) {

				registro.getCampos().put("COMPANIA", compania);

				registro.getCampos().put("FORMULA_APLICADA", formula);

				if (beneficio != null && !beneficio.isEmpty()) {
					registro.getCampos().put("PROCEDIMIENTO", beneficio);
				}
				registro.getCampos().put("CONCEPTO_RESULTANTE", beneficio);
			} else {
				JsfUtil.agregarMensajeErrorDialogo("La Formula debe tener almenos 1 factor base.");
				return false;
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	}

	public boolean contieneAlMenosUnNumero(String formula) {
		return formula != null && formula.matches(".*\\d.*");
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * @return the si
	 */
	public Boolean getSi() {
		return si;
	}

	/**
	 * @param si the si to set
	 */
	public void setSi(Boolean si) {
		this.si = si;
	}

	/**
	 * @return the no
	 */
	public Boolean getNo() {
		return no;
	}

	/**
	 * @param no the no to set
	 */
	public void setNo(Boolean no) {
		this.no = no;
	}

	/**
	 * @return the fechaIni
	 */
	public Date getFechaIni() {
		return fechaIni;
	}

	/**
	 * @param fechaIni the fechaIni to set
	 */
	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	/**
	 * @return the fechaFin
	 */
	public Date getFechaFin() {
		return fechaFin;
	}

	/**
	 * @param fechaFin the fechaFin to set
	 */
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	/**
	 * @return the listaConcepto
	 */
	public List<Registro> getListaConcepto() {
		return listaConcepto;
	}

	/**
	 * @param listaConcepto the listaConcepto to set
	 */
	public void setListaConcepto(List<Registro> listaConcepto) {
		this.listaConcepto = listaConcepto;
	}

	/**
	 * @return the listaFactor
	 */
	public RegistroDataModelImpl getListaFactor() {
		return listaFactor;
	}

	/**
	 * @param listaFactor the listaFactor to set
	 */
	public void setListaFactor(RegistroDataModelImpl listaFactor) {
		this.listaFactor = listaFactor;
	}

	/**
	 * @return the beneficio
	 */
	public String getBeneficio() {
		return beneficio;
	}

	/**
	 * @param beneficio the beneficio to set
	 */
	public void setBeneficio(String beneficio) {
		this.beneficio = beneficio;
	}

	/**
	 * @return the operador
	 */
	public String getOperador() {
		return operador;
	}

	/**
	 * @param operador the operador to set
	 */
	public void setOperador(String operador) {
		this.operador = operador;
	}

	/**
	 * @return the concepto
	 */
	public String getConcepto() {
		return concepto;
	}

	/**
	 * @param concepto the concepto to set
	 */
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	/**
	 * @return the listaListafactores
	 */
	public RegistroDataModelImpl getListaListafactores() {
		return listaListafactores;
	}

	/**
	 * @param listaListafactores the listaListafactores to set
	 */
	public void setListaListafactores(RegistroDataModelImpl listaListafactores) {
		this.listaListafactores = listaListafactores;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getTipoBeneficioFormula() {
		return tipoBeneficioFormula;
	}

	public void setTipoBeneficioFormula(String tipoBeneficioFormula) {
		this.tipoBeneficioFormula = tipoBeneficioFormula;
	}

	public String getTipoFactor() {
		return tipoFactor;
	}

	public void setTipoFactor(String tipoFactor) {
		this.tipoFactor = tipoFactor;
	}

	public boolean isBeneficioValido() {
		return beneficioValido;
	}

	public void setBeneficioValido(boolean beneficioValido) {
		this.beneficioValido = beneficioValido;
	}

}
