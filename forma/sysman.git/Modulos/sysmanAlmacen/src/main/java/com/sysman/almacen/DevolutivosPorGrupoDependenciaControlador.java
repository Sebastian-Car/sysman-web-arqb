/*-
 * DevolutivosPorGrupoDependenciaControlador.java
 *
 * 1.0
 * 
 * 14/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.DevolutivosPorGrupoDependenciaControladorEnum;
import com.sysman.almacen.enums.DevolutivosPorGrupoDependenciaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que genera 4 reportes, todos con base a devolutivos por grupo y
 * dependencia pero unos detallados
 *
 * @version 1.0, 14/08/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class DevolutivosPorGrupoDependenciaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;

	// <DECLARAR_ATRIBUTOS>
	@EJB
	EjbSysmanUtil ejbSysmanUtil;
	private String digitosAgrupacionInventario;

	private String opcionordenDeAgrupacion;
	private String cmbElementoDesde;
	private String elementoHasta;
	private String grupofinal;
	private String grupoinicial;
	private String dependenciainicial;
	private String dependenciafinal;
	private StreamedContent archivoDescarga;
	private String cmbDependenciaInicial;
	private String cmbDependenciaFinal;
	private String opcionAgrupado;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * 
	 */
	private RegistroDataModelImpl listaGrupoInicial;
	private RegistroDataModelImpl listaGrupoFinal;
	private RegistroDataModelImpl listaDependenciaInicial;
	private RegistroDataModelImpl listaDependenciaFinal;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DevolutivosPorGrupoDependenciaControlador
	 */
	public DevolutivosPorGrupoDependenciaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 1892
			numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOS_POR_GRUPO_DEPENDENCIA_CONTROLADOR.getCodigo();
			validarPermisos();

			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		abrirFormulario();
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaGrupoInicial();
		opcionAgrupado = "1";
		opcionordenDeAgrupacion = "1";
		cargarListaDependenciaInicial();

		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>

	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		try {
			digitosAgrupacionInventario = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(), true), "");
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
		}
		/*
		 * FR1892-AL_ABRIR Private Sub Form_Open(Cancel As Integer) 'formularioAbrir 10,
		 * Me.Name DoCmd.Restore End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaGrupoInicial
	 *
	 *
	 */
	public void cargarListaGrupoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosPorGrupoDependenciaControladorUrlEnum.URL3217.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(DevolutivosPorGrupoDependenciaControladorEnum.DIGITOSAGRUPACIONINVENTARIO.getValue(),
				digitosAgrupacionInventario);

		listaGrupoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGOELEMENTO.getName());

	}

	/**
	 * 
	 * Carga la lista listaGrupoFinal
	 *
	 *
	 */
	public void cargarListaGrupoFinal() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosPorGrupoDependenciaControladorUrlEnum.URL3218.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put("CMBELEMENTODESDE", cmbElementoDesde);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(DevolutivosPorGrupoDependenciaControladorEnum.DIGITOSAGRUPACIONINVENTARIO.getValue(),
				digitosAgrupacionInventario);

		listaGrupoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGOELEMENTO.getName());

	}

	/**
	 * 
	 * Carga la lista listaDependenciaInicial
	 *
	 *
	 */
	public void cargarListaDependenciaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosPorGrupoDependenciaControladorUrlEnum.URL3219.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaDependenciaFinal
	 *
	 *
	 */
	public void cargarListaDependenciaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosPorGrupoDependenciaControladorUrlEnum.URL3220.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put("CODIGOINICIAL", cmbDependenciaInicial);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GeneraExcel en la vista
	 *
	 *
	 *
	 */
	public void oprimirGeneraExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GeneraPdf en la vista
	 *
	 *
	 *
	 */
	public void oprimirGeneraPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	private void generarReporte(ReportesBean.FORMATOS formato) {
		// Creacion arreglos
		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		String reporte;
		// Codigo del reporte
		if ("1".equals(opcionAgrupado)) {
			if ("1".equals(opcionordenDeAgrupacion)) { // agrupacion grupo y dependencia
				reporte = DevolutivosPorGrupoDependenciaControladorEnum.REPORTE001865.getValue();

			} else { // agrupacion dependencia y grupo
				reporte = DevolutivosPorGrupoDependenciaControladorEnum.REPORTE001869.getValue();
			}

		} else {
			if ("1".equals(opcionordenDeAgrupacion)) { // detallado grupo y dependencia
				reporte = DevolutivosPorGrupoDependenciaControladorEnum.REPORTE001863.getValue();

			} else { // detallado dependencia y grupo
				reporte = DevolutivosPorGrupoDependenciaControladorEnum.REPORTE001866.getValue();
			}
		}

		// <REEMPLAZAR VARIABLES EN CONSULTA>
		reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		reemplazar.put(DevolutivosPorGrupoDependenciaControladorEnum.DIGITOSREPORTE.getValue(),
				digitosAgrupacionInventario);
		reemplazar.put(DevolutivosPorGrupoDependenciaControladorEnum.cmbelementodesde.getValue(), cmbElementoDesde);
		reemplazar.put(DevolutivosPorGrupoDependenciaControladorEnum.cmbelementohasta.getValue(), elementoHasta);
		reemplazar.put(DevolutivosPorGrupoDependenciaControladorEnum.codigoinicial.getValue(), cmbDependenciaInicial);
		reemplazar.put(DevolutivosPorGrupoDependenciaControladorEnum.codigofinal.getValue(), cmbDependenciaFinal);

		// </REEMPLAZAR VARIABLES EN CONSULTA
		try {
			// <ENVIAR PARAMETROS AL REPORTE>
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre().toUpperCase());
			parametros.put("PR_CODIGOINICIAL", cmbDependenciaInicial);
			parametros.put("PR_CODIGOFINAL", cmbDependenciaFinal);
			// </ENVIAR PARAMETROS AL REPORTE>
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
			/*-aqui reporte hace referencia al nombre del reporte*/
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		}

		catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Agrupado
	 * 
	 * 
	 * 
	 */
	public void cambiarAgrupado() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	public void cambiarOrdenDeAgrupacion() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaGrupoInicial
	 *
	 * Selecciona la fila del combo grupo inicial
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaGrupoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbElementoDesde = registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()).toString();
		grupoinicial = registroAux.getCampos().get("NOMBRELARGO").toString();
		cargarListaGrupoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaGrupoFinal
	 *
	 * Selecciona la fila del combo grupo final
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */

	public void seleccionarFilaGrupoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoHasta = registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()).toString();
		grupofinal = registroAux.getCampos().get("NOMBRELARGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaDependenciaInicial
	 *
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDependenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbDependenciaInicial = registroAux.getCampos().get("CODIGO").toString();
		dependenciainicial = registroAux.getCampos().get("NOMBRE").toString();
		cargarListaDependenciaFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaDependenciaFinal
	 *
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDependenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cmbDependenciaFinal = registroAux.getCampos().get("CODIGO").toString();
		dependenciafinal = registroAux.getCampos().get("NOMBRE").toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cmbElementoDesde
	 * 
	 * @return cmbElementoDesde
	 */
	public String getCmbElementoDesde() {
		return cmbElementoDesde;
	}

	/**
	 * Asigna la variable cmbElementoDesde
	 * 
	 * @param cmbElementoDesde
	 *            Variable a asignar en cmbElementoDesde
	 */
	public void setCmbElementoDesde(String cmbElementoDesde) {
		this.cmbElementoDesde = cmbElementoDesde;
	}

	/**
	 * Retorna la variable elementoHasta
	 * 
	 * @return elementoHasta
	 */
	public String getElementoHasta() {
		return elementoHasta;
	}

	/**
	 * Asigna la variable elementoHasta
	 * 
	 * @param elementoHasta
	 *            Variable a asignar en elementoHasta
	 */
	public void setElementoHasta(String elementoHasta) {
		this.elementoHasta = elementoHasta;
	}

	/**
	 * Retorna la variable cmbDependenciaInicial
	 * 
	 * @return cmbDependenciaInicial
	 */
	public String getCmbDependenciaInicial() {
		return cmbDependenciaInicial;
	}

	/**
	 * Asigna la variable cmbDependenciaInicial
	 * 
	 * @param cmbDependenciaInicial
	 *            Variable a asignar en cmbDependenciaInicial
	 */
	public void setCmbDependenciaInicial(String cmbDependenciaInicial) {
		this.cmbDependenciaInicial = cmbDependenciaInicial;
	}

	/**
	 * Retorna la variable cmbDependenciaFinal
	 * 
	 * @return cmbDependenciaFinal
	 */
	public String getCmbDependenciaFinal() {
		return cmbDependenciaFinal;
	}

	/**
	 * Asigna la variable cmbDependenciaFinal
	 * 
	 * @param cmbDependenciaFinal
	 *            Variable a asignar en cmbDependenciaFinal
	 */
	public void setCmbDependenciaFinal(String cmbDependenciaFinal) {
		this.cmbDependenciaFinal = cmbDependenciaFinal;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaGrupoInicial
	 * 
	 * @return listaGrupoInicial
	 */
	public EjbSysmanUtil getEjbSysmanUtil() {
		return ejbSysmanUtil;
	}

	public void setEjbSysmanUtil(EjbSysmanUtil ejbSysmanUtil) {
		this.ejbSysmanUtil = ejbSysmanUtil;
	}

	public String getDigitosAgrupacionInventario() {
		return digitosAgrupacionInventario;
	}

	public void setDigitosAgrupacionInventario(String digitosAgrupacionInventario) {
		this.digitosAgrupacionInventario = digitosAgrupacionInventario;
	}

	public RegistroDataModelImpl getListaGrupoInicial() {
		return listaGrupoInicial;
	}

	public void setListaGrupoInicial(RegistroDataModelImpl listaGrupoInicial) {
		this.listaGrupoInicial = listaGrupoInicial;
	}

	public RegistroDataModelImpl getListaGrupoFinal() {
		return listaGrupoFinal;
	}

	public void setListaGrupoFinal(RegistroDataModelImpl listaGrupoFinal) {
		this.listaGrupoFinal = listaGrupoFinal;
	}

	public RegistroDataModelImpl getListaDependenciaInicial() {
		return listaDependenciaInicial;
	}

	public void setListaDependenciaInicial(RegistroDataModelImpl listaDependenciaInicial) {
		this.listaDependenciaInicial = listaDependenciaInicial;
	}

	public RegistroDataModelImpl getListaDependenciaFinal() {
		return listaDependenciaFinal;
	}

	public void setListaDependenciaFinal(RegistroDataModelImpl listaDependenciaFinal) {
		this.listaDependenciaFinal = listaDependenciaFinal;
	}

	public String getCompania() {
		return compania;
	}

	public String getModulo() {
		return modulo;
	}

	public String getGrupofinal() {
		return grupofinal;
	}

	public void setGrupofinal(String grupofinal) {
		this.grupofinal = grupofinal;
	}

	public String getGrupoinicial() {
		return grupoinicial;
	}

	public void setGrupoinicial(String grupoinicial) {
		this.grupoinicial = grupoinicial;
	}

	public String getDependenciainicial() {
		return dependenciainicial;
	}

	public void setDependenciainicial(String dependenciainicial) {
		this.dependenciainicial = dependenciainicial;
	}

	public String getDependenciafinal() {
		return dependenciafinal;
	}

	public void setDependenciafinal(String dependenciafinal) {
		this.dependenciafinal = dependenciafinal;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getOpcionAgrupado() {
		return opcionAgrupado;
	}

	public void setOpcionAgrupado(String opcionAgrupado) {
		this.opcionAgrupado = opcionAgrupado;
	}

	public String getOpcionordenDeAgrupacion() {
		return opcionordenDeAgrupacion;
	}

	public void setOpcionordenDeAgrupacion(String opcionordenDeAgrupacion) {
		this.opcionordenDeAgrupacion = opcionordenDeAgrupacion;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}
