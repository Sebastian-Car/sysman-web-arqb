/*-
 * FrmWFProcesosControlador.java
 *
 * 1.0
 * 
 * 06/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmNodoVariablesControladorEnum;
import com.sysman.workflow.enums.FrmNodosControladorEnum;
import com.sysman.workflow.enums.FrmWFProcesosControladorEnum;
import com.sysman.workflow.enums.FrmWFProcesosControladorUrlEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 * Controlador de la forma: <code>frmworkflowprocesos</code>.
 *
 * @version 1.0, 06/04/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmWFProcesosControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;

	/**
	 * Constante a nivel de clase que aloja el codigo del modulo desde
	 * el cual el usuario abre el formulario.
	 */
	private final String modulo = SessionUtil.getModulo();

	/** Constante a nivel de clase que aloja la cadena: COMPANIA. */
	private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaTraerDeuda;

	private Object urlProJud;
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmWFProcesosControlador
	 */
	@SuppressWarnings("unchecked")
	public FrmWFProcesosControlador() {
		super();

		compania = SessionUtil.getCompania();

		try {
			// 1697
			numFormulario = GeneralCodigoFormaEnum.FRM_WF_PROCESOS_CONTROLADOR
					.getCodigo();

			validarPermisos();
			// <INI_ADICIONAL>
			Map<String, Object> paramIn = SessionUtil.getFlash();

			if (paramIn != null) {
				rid = (Map<String, Object>) paramIn.get(
						FrmWFProcesosControladorEnum.PR_RID.getValue());
			}
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
		finally {
			SessionUtil.cleanFlash();
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaTraerDeuda();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.PROCESOS;

		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * Asigna los DSS a las operaciones basicas del formulario (CRUD).
	 * Envia los parametros de entrada del parametro.
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();

		parametrosListado.put(cCompania, compania);
	}

	// <METODOS_CARGAR_LISTA>
	//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 *
	 */
	public void cargarListaTraerDeuda(){


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmWFProcesosControladorUrlEnum.URL0001
						.getValue());

		listaTraerDeuda = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cargar un archivo desde el lector de
	 * archivo Seleccionar Imagen (LA31).
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void cargarArchivoLectorDiagrama(FileUploadEvent event) {
		// <CODIGO_DESARROLLADO>
		UploadedFile file = event.getFile();

		String diagramaRuta = "";

		try (InputStream stream = file.getInputstream()) {
			/* Extraer informacion de la imagen */
			String archivo = file.getFileName();

			// Extension de la imagen
			String archivoExt = FilenameUtils.getExtension(archivo);

			// Nombre de la imagen con extension
			String archivoNom = FilenameUtils.getName(archivo);

			// Ruta del proceso
			String rutaProceso = generarRutaProceso();

			// Ruta completa de la imagen
			diagramaRuta = JsfUtil.generarRuta(modulo, "", rutaProceso,
					archivoNom);

			/* Truncar ruta y adicionar extension */
			if (diagramaRuta.length() >= 250) {
				diagramaRuta = diagramaRuta.substring(250).concat(".")
						.concat(archivoExt);
			}

			JsfUtil.upload(file.getInputstream(), diagramaRuta);

			registro.getCampos().put(
					FrmWFProcesosControladorEnum.RUTA_IMG_WF.getValue(),
					rutaProceso.concat(archivoNom));

			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4096"));
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTraerDeuda
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTraerDeuda(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TRAE_DEUDA", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

	}
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	/**
	 * Metodo ejecutado al oprimir el boton Remover Imagen (BT3163) en
	 * la vista. Elimina la ruta donde se encuentra la imagen.
	 */
	public void oprimirBtRemoverImg() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(
				FrmWFProcesosControladorEnum.RUTA_IMG_WF.getValue(),
				"");
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton Mostrar Imagen (BT3371) en
	 * la vista. Accede al formulario visor de imagenes y pdfs.
	 */
	public void oprimirBtMostrarImagen() {
		// <CODIGO_DESARROLLADO>
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
				FrmWFProcesosControladorEnum.RUTA_IMG_WF.getValue())) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4217"));
			return;
		}

		String rutaRel = registro.getCampos().get(
				FrmWFProcesosControladorEnum.RUTA_IMG_WF.getValue())
				.toString();

		String rutaAbs = JsfUtil.generarRuta(modulo, "",
				FilenameUtils.getFullPath(rutaRel),
				FilenameUtils.getName(rutaRel));

		SessionUtil.cargarModalVisor(rutaAbs, FilenameUtils.getName(rutaRel));
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton Etapas (BT3167) en la
	 * vista. Redirecciona al formulario de Etapas (1759).
	 */
	public void oprimirBtEtapas() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);

		Map<String, Object> param = new TreeMap<>();

		param.put(FrmNodosControladorEnum.PR_PROCESO.getValue(),
				registro.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		param.put(FrmNodosControladorEnum.PR_RID.getValue(), css);

		Direccionador dir = new Direccionador();
		dir.setParametros(param);

		dir.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRM_NODOS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(dir, modulo);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton Detalles Etapas (BT3168)
	 * en la vista.
	 */
	public void oprimirBtDetEtapas() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);

		Map<String, Object> param = new TreeMap<>();

		param.put(FrmNodosControladorEnum.PR_PROCESO.getValue(),
				registro.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		param.put(FrmNodosControladorEnum.PR_RID.getValue(), css);

		Direccionador dir = new Direccionador();
		dir.setParametros(param);

		dir.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRM_D_NODOS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(dir, modulo);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirBtVaribleProceso() {
		agregarRegistroNuevo(false);

		String[] campos = new String[5];
		campos[0] = FrmNodoVariablesControladorEnum.PR_CODIGO_PROCESO
				.getValue();
		campos[1] = FrmNodoVariablesControladorEnum.PR_PROCESO_NOM.getValue();
		//campos[4] = FrmNodoVariablesControladorEnum.PR_RUTA.getValue();

		Object[] valores = new Object[5];
		valores[0] = registro.getCampos().get(GeneralParameterEnum.CODIGO.getName());
		valores[1] = registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName());
		//valores[4] = registro.getCampos().get("RUTA");

		SessionUtil.cargarModalDatosFlashCerrar(
				Integer.toString(GeneralCodigoFormaEnum.FRM_PROCESOS_VARIABLES_CONTROLADOR.getCodigo()),
				modulo, campos, valores);

	}

	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro.
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();

		if (css == null) {
			registro.getCampos().put(FrmWFProcesosControladorEnum.FECHA_CREACION
					.getValue(), new Date());

			registro.getCampos().put("ELABORO_FECHA", new Date());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro.
	 * 
	 * @return true.
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(cCompania, compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro.
	 * 
	 * @return true
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion o actualizacion
	 * del registro.
	 * 
	 * @return true.
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		if (css != null) {
			registro.getCampos().remove(cCompania);
		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y
	 * actualizacion del registro.
	 * 
	 * @return true.
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro.
	 * 
	 * @return true
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro.
	 * 
	 * @return true
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Genera la ruta donde se ubicara la Imagen del Diagrama. Toma la
	 * longitud maxima que puede tomar la compania y el proceso; y
	 * crea una ruta para almacenar la imagen del workflow.
	 * 
	 * @return Ruta especifica del proceso.
	 */
	private String generarRutaProceso() {
		String ruta = SysmanFunciones
				.concatenar("/C", compania, "/P",
						SysmanFunciones.nvl(
								registro.getCampos()
								.get(GeneralParameterEnum.CODIGO
										.getName()),
								"").toString(),
						"/");

		/*- Ruta: L
        Compania: 13
         Proceso: 34*/
		if (ruta.length() >= 48) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4097"));
			return ruta.substring(82);
		}

		return ruta;
	}



	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>

	public RegistroDataModelImpl getListaTraerDeuda() {
		return listaTraerDeuda;
	}

	public void setListaTraerDeuda(RegistroDataModelImpl listaTraerDeuda) {
		this.listaTraerDeuda = listaTraerDeuda;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	// </SET_GET_ADICIONALES>
}
