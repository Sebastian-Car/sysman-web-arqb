/*-
 * FrmWFInformaciongeneralControlador.java
 *
 * 1.0
 * 
 * 03/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmactuvigControladorUrlEnum;
//import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
//import com.sysman.hojasdevida.enums.FrmAnexosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmWFInformaciongeneralControladorEnum;
import com.sysman.workflow.enums.FrmWFInformaciongeneralControladorUrlEnum;
import com.sysman.workflow.enums.DTramiteVariablesControladorEnum;
import com.sysman.workflow.enums.DTramiteVariablesControladorUrlEnum;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;
import com.sysman.workflow.enums.FrmWFInformaciongeneralControladorEnum;

import net.sf.jasperreports.engine.JRException;


/**
 *
 * @version 1.0, 03/12/2020
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmWFInformaciongeneralControlador  extends BeanBaseContinuoAcmeImpl{
	//@EJB
	//private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Constante a nivel de clase que aloja el codigo del usuario que
	 * inicio sesion.
	 */
	private final String usuario = SessionUtil.getUser().getCodigo();

	/** Constante a nivel de clase que aloja la cadena: COMPANIA. */
	private final String cCompania = GeneralParameterEnum.COMPANIA.getName();
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;

	private final String modulo;
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se
	 * activa la edicion de un registro. Toma el valor del indice
	 * dentro de la grilla del registro seleccionado para editar
	 */
	private int indice;
	//<DECLARAR_ATRIBUTOS>
	/**
	 */
	private final String tipoMimeAppIma;
	private String extension;
	private String proceso;
	private String tipoTramite;
	private String tramite;
	private String detalleTramite;
	private String nodo;
	private int codFormRedireccion;
	private String nameFile;
	private boolean permiteModificar=true;
	/**
	 * Este atributo se usa como auxiliar del componente referencia de
	 * archivos BuscarAdjunto (RA18) y funciona como contenedor del
	 * archivo que se desea cargar
	 */
	private UploadedFile archivoCargaBuscarAdjunto;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * Adjuntar y funciona como contenedor del archivo que se desea cargar
	 */
	private UploadedFile archivoCargaAdjuntar;

	private Map<String, Object> ridTramite;

	private static final String FILE_SEPARADOR = File.separator;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmWFInformaciongeneralControlador
	 */
	public FrmWFInformaciongeneralControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		tipoMimeAppIma = "33,37";
		try {
			numFormulario=2214;
			validarPermisos();

			Map<String, Object> paramIn = SessionUtil.getFlash();
			System.out.println("------->"+paramIn);
			if (paramIn != null) {
				proceso = paramIn
						.get(FrmWFInformaciongeneralControladorEnum.PR_PROCESO
								.getValue())
						.toString();

				tipoTramite = paramIn
						.get(FrmWFInformaciongeneralControladorEnum.PR_TIPO_TRAMITE
								.getValue())
						.toString();

				tramite = paramIn
						.get(FrmWFInformaciongeneralControladorEnum.PR_TRAMITE
								.getValue())
						.toString();

				detalleTramite = paramIn
						.get(FrmWFInformaciongeneralControladorEnum.PR_D_TRAMITE
								.getValue())
						.toString();

				nodo = paramIn.get(FrmWFInformaciongeneralControladorEnum.PR_NODO
						.getValue()).toString();

				codFormRedireccion = (int) paramIn
						.get(FrmWFInformaciongeneralControladorEnum.PR_COD_FORM
								.getValue());

				ridTramite = (Map<String, Object>) paramIn
						.get(FrmTramitesControladorEnum.PR_ROWKEY
								.getValue());
			}

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
		enumBase = GenericUrlEnum.TRAMITE_VARIABLE;

		registro= new Registro();

		reasignarOrigen();		    
		buscarLlave();

		//<CARGAR_LISTA>
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

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		parametrosListado.put(
				FrmWFInformaciongeneralControladorEnum.PROCESO.getValue(),
				proceso);


		parametrosListado.put(FrmWFInformaciongeneralControladorEnum.TIPO_TRAMITE
				.getValue(), tipoTramite);

		parametrosListado.put(
				FrmWFInformaciongeneralControladorEnum.TRAMITE.getValue(),
				tramite);

		parametrosListado.put("SEPARADOR", FILE_SEPARADOR);
	}
	/**
	 * Retorna la variable indice 
	 * @return indice
	 */
	public int getIndice() {
		return indice;
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>

	/**
	 * Metodo ejecutado al oprimir el boton BtAdjunto
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */	
	public void oprimirBtAdjunto(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		String archivo = archivoCargaBuscarAdjunto.getFileName();

		if (SysmanFunciones.validarVariableVacio(archivo)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4079"));
			return;
		}

		String variable = SysmanFunciones
				.nvl(reg.getCampos()
						.get(FrmWFInformaciongeneralControladorEnum.VARIABLE
								.getValue()),
						"")
				.toString();

		/* Nombre original del archivo sin extension. */
		nameFile = FilenameUtils.getBaseName(archivo);

		Map<String, Object> keyAdjunto = new LinkedHashMap<>();
		keyAdjunto.put("KEY_TIPO_TRAMITE", tipoTramite);
		keyAdjunto.put("KEY_TRAMITE", tramite);
		keyAdjunto.put("KEY_PROCESO", proceso);
		keyAdjunto.put("KEY_VARIABLE", variable);
		keyAdjunto.put("KEY_NAME_FILE", nameFile);

		String miExtension = FilenameUtils.getExtension(archivo);

		try {
			String rutaAdjunto = recuperarRutaAdjunto(variable);

			String ruta = JsfUtil.generarNombreArchivo(modulo, keyAdjunto,
					rutaAdjunto, miExtension, "");

			// Validar extension
			if (validarExtAdjunto(ruta)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4123"));
				return;
			}

			// Validar mime
			if (validarMimeAdjunto(ruta)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4216"));
				return;
			}

			/* Truncar ruta y adicionar extension */
			if (ruta.length() >= 254) {
				Map<String, Object> keyAdjunto1 = new LinkedHashMap<>();
				keyAdjunto1.put("KEY_TIPO_TRAMITE", tipoTramite);
				keyAdjunto1.put("KEY_TRAMITE", tramite);
				keyAdjunto1.put("KEY_PROCESO", proceso);
				keyAdjunto1.put("KEY_VARIABLE", variable);
				keyAdjunto1.put("KEY_NAME_FILE", nameFile.substring(0, 150));
				
				ruta = JsfUtil.generarNombreArchivo(modulo, keyAdjunto1,
						rutaAdjunto, miExtension, "");
			}

			JsfUtil.upload(archivoCargaBuscarAdjunto.getInputstream(), ruta);

			ruta = rutaAdjunto.concat(FilenameUtils.getName(ruta));

			actualizarAdjuntoVarTramite(variable, ruta);

			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4083"));
		}
		catch (IOException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}


	/**
	 * Metodo ejecutado al oprimir el boton Ver (BT3132). Redirecciona
	 * al formulario visor de archivos (1824).
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirBtDescargar(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		try {
			String ruta = reg.getCampos().get(
					FrmWFInformaciongeneralControladorEnum.ADJUNTO.getValue())
					.toString();

			String rutaAbs = JsfUtil.generarRuta(modulo, "",
					FilenameUtils.getFullPath(ruta),
					FilenameUtils.getName(ruta));

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DTramiteVariablesControladorUrlEnum.URL004
							.getValue());

			Map<String, Object> param = new TreeMap<>();
			param.put(FrmWFInformaciongeneralControladorEnum.TIPO_MIME.getValue(), 37);
			param.put(FrmWFInformaciongeneralControladorEnum.ID.getValue(), 6);
			param.put(FrmWFInformaciongeneralControladorEnum.EXTENSION.getValue(),
					FilenameUtils.getExtension(ruta));

			Registro auxReg = null;


			auxReg = RegistroConverter.toRegistro(
					requestManager.get(urlBean.getUrl(), param));


			// Mostrar en visor si es una extension valida: PDF o Imagen
			if (auxReg != null && !SysmanFunciones
					.validarCampoVacio(auxReg.getCampos(), "EXISTE")) {

				File adjunto = new File(rutaAbs);

				InputStream inputStream = new FileInputStream(adjunto);
				if(adjunto.length() > 1502898) {
					byte[] vec = new byte[(int) adjunto.length()];

					inputStream.read(vec, 0, vec.length);


					archivoDescarga = JsfUtil.getArchivoDescarga(
							new ByteArrayInputStream(vec), adjunto.getName());

					ejecutaralertas();

					return;

				}else {

					SessionUtil.cargarModalVisor(rutaAbs,
							reg.getCampos().get(
									FrmWFInformaciongeneralControladorEnum.ADJUNTO_NOM
									.getValue())
							.toString());
					return;
				}
			}

			// Descargar archivo
			File adjunto = new File(rutaAbs);

			try (InputStream inputStream = new FileInputStream(adjunto)) {
				byte[] vec = new byte[(int) adjunto.length()];

				inputStream.read(vec, 0, vec.length);

				archivoDescarga = JsfUtil.getArchivoDescarga(
						new ByteArrayInputStream(vec), adjunto.getName());
			}
			catch (IOException | JRException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		catch (SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Eliminar
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */

	public void oprimirEliminar(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		try {
			actualizarAdjuntoVarTramite(reg.getCampos()
					.get(FrmWFInformaciongeneralControladorEnum.VARIABLE
							.getValue())
					.toString(), "");

			listaInicial.load();

			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4088"));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Escanear
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirEscanear(Registro reg, int indice) {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton VerOcr
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirVerOcr(Registro reg, int indice) {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo invocado al ejecutar el comando remoto alertas en la vista
	 *
	 *
	 */
	public void ejecutaralertas() {
		// <CODIGO_DESARROLLADO>
		JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4360"));
		// </CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		cargarListaExtensiones();
		//<CODIGO_DESARROLLADO>
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

		registro.getCampos().remove(FrmWFInformaciongeneralControladorEnum.OBLIGATORIO
				.getValue());

		registro.getCampos().remove(
				FrmWFInformaciongeneralControladorEnum.ETIQUETA.getValue());

		registro.getCampos()
		.remove(FrmWFInformaciongeneralControladorEnum.PROCESO
				.getValue());

		registro.getCampos().remove("TIPO_DATO");
		registro.getCampos().remove("CODIGO_NODO");
		registro.getCampos().remove("TRAMITE");

		registro.getCampos().remove(FrmWFInformaciongeneralControladorEnum.ADJUNTO_NOM
				.getValue());

		registro.getCampos().remove("VALOR_FECHA_STR");

		registro.getCampos().remove(
				FrmWFInformaciongeneralControladorEnum.ADJUNTO.getValue());

		registro.getCampos()
		.remove(FrmWFInformaciongeneralControladorEnum.CODIGO_NODO_VARIABLE
				.getValue());

		registro.getCampos().remove("COMPANIA");
		registro.getCampos().remove("MANEJA_ADJUNTO");
		registro.getCampos().remove("ADJUNTO_OBLIGATORIO");

		registro.getCampos()
		.remove(FrmWFInformaciongeneralControladorEnum.TIPO_TRAMITE
				.getValue());

		registro.getCampos().remove("VARIABLE");
		registro.getCampos().remove("OCR");
	}
	/**
	 * Metodo ejecutado cuando se activa la edicion de un registro del
	 * formulario
	 * 
	 *
	 * @param registro
	 * registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
	}
	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 */
	public void ejecutarrcCerrar(){
		//<CODIGO_DESARROLLADO>
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), ridTramite);

		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer.toString(codFormRedireccion));

		if (GeneralCodigoFormaEnum.FRM_MONITOR_HISTORIAL_CONTROLADOR
				.getCodigo() == codFormRedireccion) {
			param.put(FrmWFInformaciongeneralControladorEnum.PR_PROCESO.getValue(),
					proceso);

			param.put(FrmWFInformaciongeneralControladorEnum.PR_TIPO_TRAMITE
					.getValue(), tipoTramite);

			param.put(FrmWFInformaciongeneralControladorEnum.PR_TRAMITE.getValue(),
					tramite);

			param.put(FrmWFInformaciongeneralControladorEnum.PR_D_TRAMITE.getValue(),
					detalleTramite);

			param.put(FrmWFInformaciongeneralControladorEnum.PR_NODO.getValue(),
					nodo);
		}

		dir.setParametros(param);

		SessionUtil.redireccionarForma(dir, modulo);
		//</CODIGO_DESARROLLADO>
	}
	public void oprimirGuardar(Registro reg, int indice) {

		String ruta;
		String archivo = archivoCargaAdjuntar.getFileName();
		try {
			ruta = ejbSysmanUtil.consultarParametro(compania,
					"RUTA SERVIDOR DE ANEXOS",
					Integer.toString(
							SysmanConstantes.CODIGO_APLICACION_GENERAL),
					new Date(),
					false);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
		if (SysmanFunciones.validarVariableVacio(archivo)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB3979"));
			return;          
		}
	}

	private String recuperarRutaAdjunto(String variable)
			throws SystemException {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(FrmWFInformaciongeneralControladorEnum.PROCESO.getValue(), proceso);
		param.put(FrmWFInformaciongeneralControladorEnum.NODO.getValue(), nodo);
		param.put(FrmWFInformaciongeneralControladorEnum.VARIABLE.getValue(),
				variable);

		Registro auxReg = RegistroConverter
				.toRegistro(requestManager
						.get(UrlServiceUtil
								.getUrlBeanById(DTramiteVariablesControladorUrlEnum.URL0001
										.getValue())
								.getUrl(), param));

		if (auxReg != null) {
			return auxReg.getCampos().get(
					FrmWFInformaciongeneralControladorEnum.ADJUNTO.getValue())
					.toString();
		}

		return "";
	}
	/**
	 * Determina si el archivo de la ruta indicada es una imagen,
	 * office, comprimido o PDF.
	 * 
	 * @param ruta
	 * -> Ruta donde se encuentra ubicado el archivo.
	 * @return
	 * <li>true -> Cuando es otro tipo de archivo.
	 * <li>false -> Cuando el archivo es una imagen, office,
	 * comprimido o PDF.
	 */
	private boolean validarExtAdjunto(String ruta) {
		Map<String, Object> param = new TreeMap<>();

		param.put(FrmWFInformaciongeneralControladorEnum.TIPO_MIME.getValue(),
				tipoMimeAppIma);

		param.put(FrmWFInformaciongeneralControladorEnum.EXTENSION.getValue(),
				FilenameUtils.getExtension(ruta));

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						DTramiteVariablesControladorUrlEnum.URL001
						.getValue());
		Registro auxReg = null;

		try {
			auxReg = RegistroConverter.toRegistro(
					requestManager.get(urlBean.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return auxReg == null
				|| SysmanFunciones.validarCampoVacio(auxReg.getCampos(),
						FrmWFInformaciongeneralControladorEnum.EXISTE.getValue());
	}

	/**
	 * Valida que la extension corresponda al contenido del archivo
	 * mediante el MIME.
	 * 
	 * @param ruta
	 * -> Ruta donde se encuentra ubicado el archivo.
	 * @return
	 * <li>true</li> -> Cuando la extension del archivo no correponde
	 * a su contenido.
	 * <li>false</li> -> Cuando la extension correponde al tipo de
	 * contenido del archivo.
	 */
	private boolean validarMimeAdjunto(String ruta) {
		String mimeFile = "";

		Map<String, Object> param = new TreeMap<>();

		param.put(FrmWFInformaciongeneralControladorEnum.TIPO_MIME.getValue(),
				tipoMimeAppIma);

		param.put(FrmWFInformaciongeneralControladorEnum.EXTENSION.getValue(),
				FilenameUtils.getExtension(ruta));

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						DTramiteVariablesControladorUrlEnum.URL002
						.getValue());
		Registro auxReg = null;

		try {
			URL url = new File(ruta).toURI().toURL();

			mimeFile = url.openConnection().getContentType();

			/*-Subir archivos con formato desconocido, mientras tanto.*/
			if ("content/unknown".equals(mimeFile)) {
				return false;
			}

			param.put(GeneralParameterEnum.CODIGO.getName(), mimeFile);

			auxReg = RegistroConverter.toRegistro(
					requestManager.get(urlBean.getUrl(), param));
		}
		catch (IOException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return auxReg == null
				|| SysmanFunciones.validarCampoVacio(auxReg.getCampos(),
						FrmWFInformaciongeneralControladorEnum.EXISTE.getValue());
	}

	/**
	 * Actualiza el adjunto de la variable en el nodo actual del
	 * tramite.
	 * 
	 * @param variable
	 * -> Codigo de la variable.
	 * @param rutaAdjunto
	 * -> Ruta absoluta del adjunto.
	 * @throws SystemException
	 */
	private void actualizarAdjuntoVarTramite(String variable,
			String rutaAdjunto)
					throws SystemException {
		Map<String, Object> paramSet = new TreeMap<>();
		paramSet.put(cCompania, compania);

		paramSet.put(FrmWFInformaciongeneralControladorEnum.PROCESO.getValue(),
				proceso);

		paramSet.put(FrmWFInformaciongeneralControladorEnum.TIPO_TRAMITE.getValue(),
				tipoTramite);

		paramSet.put(FrmWFInformaciongeneralControladorEnum.TRAMITE.getValue(),
				tramite);

		paramSet.put(FrmWFInformaciongeneralControladorEnum.VARIABLE.getValue(),
				variable);

		paramSet.put(FrmWFInformaciongeneralControladorEnum.ADJUNTO.getValue(),
				rutaAdjunto);

		paramSet.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);

		Parameter parameter = new Parameter();
		parameter.setFields(paramSet);

		UrlBean urlBean = UrlServiceUtil.getUrlBeanById(
				FrmWFInformaciongeneralControladorUrlEnum.URL001.getValue());

		requestManager.update(urlBean.getUrl(), urlBean.getMetodo(), parameter);
	}

	private String verificarRuta(String ruta) {
		File verificar = new File(ruta);
		if (!verificar.isDirectory()) {
			verificar.mkdirs();
		}

		ruta = ruta + "/" + archivoCargaAdjuntar.getFileName();

		return ruta;

	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// TODO Auto-generated method stub
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable extension
	 * 
	 * @return  extension
	 */
	public String getExtension() {
		return extension;
	}
	/**
	 * Asigna la variable  extension
	 * 
	 * @param  extension
	 * Variable a asignar en  extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public void setIndice(int indice) {
		this.indice = indice;
	}

	private void cargarListaExtensiones() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						DTramiteVariablesControladorUrlEnum.URL003
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(DTramiteVariablesControladorEnum.TIPO_MIME.getValue(),
				tipoMimeAppIma);

		Registro auxReg = null;

		try {
			auxReg = RegistroConverter.toRegistro(
					requestManager.get(urlBean.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		if (auxReg != null) {
			extension = SysmanFunciones
					.nvl(auxReg.getCampos().get("EXTENSIONES"), "")
					.toString();
		}
	}

	/**
	 * Retorna el objeto contArchivoAdjuntar
	 * 
	 * @return contArchivoAdjuntar
	 */
	public UploadedFile getArchivoCargaAdjuntar() {
		return archivoCargaAdjuntar;
	}


	/**
	 * Asigna el objeto contArchivoAdjuntar
	 * 
	 * @param contArchivoAdjuntar Variable a asignar en contArchivoAdjuntar
	 */
	public void setArchivoCargaAdjuntar(UploadedFile archivoCargaAdjuntar) {
		this.archivoCargaAdjuntar = archivoCargaAdjuntar;
	}
	public String getTramite() {
		return tramite;
	}
	public void setTramite(String tramite) {
		this.tramite = tramite;
	}
	public String getUsuario() {
		return usuario;
	}
	public String getModulo() {
		return modulo;
	}
	public String getTipoMimeAppIma() {
		return tipoMimeAppIma;
	}
	public String getProceso() {
		return proceso;
	}
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	public String getTipoTramite() {
		return tipoTramite;
	}
	public void setTipoTramite(String tipoTramite) {
		this.tipoTramite = tipoTramite;
	}
	public String getNodo() {
		return nodo;
	}
	public void setNodo(String nodo) {
		this.nodo = nodo;
	}
	public String getNameFile() {
		return nameFile;
	}
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	/**
	 * Retorna el objeto contArchivoBuscarAdjunto
	 * 
	 * @return contArchivoBuscarAdjunto
	 */
	public UploadedFile getArchivoCargaBuscarAdjunto() {
		return archivoCargaBuscarAdjunto;
	}
	/**
	 * Asigna el objeto contArchivoBuscarAdjunto
	 * 
	 * @param contArchivoBuscarAdjunto
	 * Variable a asignar en contArchivoBuscarAdjunto
	 */
	public void setArchivoCargaBuscarAdjunto(UploadedFile archivoCargaBuscarAdjunto) {
		this.archivoCargaBuscarAdjunto = archivoCargaBuscarAdjunto;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public Map<String, Object> getRidTramite() {
		return ridTramite;
	}
	public void setRidTramite(Map<String, Object> ridTramite) {
		this.ridTramite = ridTramite;
	}
	public boolean isPermiteModificar() {
		return permiteModificar;
	}
	public void setPermiteModificar(boolean permiteModificar) {
		this.permiteModificar = permiteModificar;
	}

	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
