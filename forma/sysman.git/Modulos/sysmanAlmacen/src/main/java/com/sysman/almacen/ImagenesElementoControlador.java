/*-
 * ImagenesElementoControlador.java
 *
 * 1.0
 * 
 * 21/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.enums.ImagenesElementoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Permite carga imagenes por elemento.
 *
 * @version 1.0, 21/05/2018
 * @author asana
 * 
 *         directorioCatalogo.mkdir(); se omite dado que se sube el servidor en
 *         linux, e indicaciones se garantizar crear carpetas
 * 
 * 		@version, 12/10/2018
 * @author asana.
 * 
 *         *** - Si se actualizar la forma, actulizar la propiedad ALTO del
 *         elemento PL3473 con la siguiente linea: 445px ; width: 1190px;
 *         border:#A6A6A6 solid 1px dado que se da un borde al formulario. - ***
 * 
 *         Se ajusta que al momento de cargar la imagen se guarde y se cargue
 *         inmediatamente en el formulario.
 * 
 *         Se bloquean campos elemento y serie al momento de cargar el registro
 *         para evitar se modifiquen nombres de archivos guardados.
 */
@ManagedBean
@ViewScoped
public class ImagenesElementoControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private InputStream is;
	private String tipo;
	private String imagen;
	private String nombreImagen;
	private String rutaDocumentos;
	private boolean serieVisible;
	private boolean visibleEditar;
	private String nombreArchivo;
	private List<Registro> listaSerie;
	private List<Registro> listaConsecutivo;
	private RegistroDataModelImpl listaElemento;
	private Map<String, Object> ridElemento;
	private byte[] imagenBytes;
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private InputStream inputStreamImagen;
	private String nombre;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de ImagenesElementoControlador
	 */
	public ImagenesElementoControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.IMAGENELEMENTOS_CONTROLADOR.getCodigo();
			registro = new Registro(new HashMap<String, Object>());
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			serieVisible = true;

			if (parametrosEntrada != null) {
				ridElemento = (Map<String, Object>) parametrosEntrada.get("ridElemento");
			}

			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarlistaElemento();
		cargarlistaSerie();
		cargarlistaConsecutivo();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>

		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// cargarImagen();
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.IMAGEN_ELEMENTOS;

		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ELEMENTO.getName(),
				registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
		parametrosListado.put(GeneralParameterEnum.SERIE.getName(),
				registro.getCampos().get(GeneralParameterEnum.SERIE.getName()));
		parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(), registro.getCampos().get("CONSECUTIVO"));

	}

	// <METODOS_CARGAR_LISTA>

	public void cargarlistaElemento() {
		HashMap<String, Object> parametros = new HashMap<>();

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ImagenesElementoControladorUrlEnum.URL7530.getValue());

		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put("TIPO", tipo);

		listaElemento = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros, true,
				GeneralParameterEnum.ELEMENTO.getName());

	}

	public void cargarlistaSerie() {
		try {
			HashMap<String, Object> parametros = new HashMap<>();

			parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			parametros.put(GeneralParameterEnum.ELEMENTO.getName(),
					registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));

			listaSerie = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ImagenesElementoControladorUrlEnum.URL8418.getValue())
											.getUrl(),
									parametros));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarlistaConsecutivo() {

		UrlBean urlBean;

		HashMap<String, Object> parametros = new HashMap<>();

		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.ELEMENTO.getName(),
				registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
		parametros.put(GeneralParameterEnum.SERIE.getName(),
				registro.getCampos().get(GeneralParameterEnum.SERIE.getName()));

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ImagenesElementoControladorUrlEnum.URL8419.getValue());

		try {
			listaConsecutivo = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), parametros));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_CARGAR_LISTA>

	// <METODOS_CAMBIAR>

	public void cambiarTipoElemento() {
		tipo = registro.getCampos().get("TIPO").toString();
		cargarlistaElemento();
		if ((ACCION_INSERTAR.equals(accion)) && ("C".equals(tipo))) {
			serieVisible = false;
		} else {
			serieVisible = true;
		}

	}

	public void cambiarSerie() {

		cargarlistaConsecutivo();
	}

	public void generarConsecutivo() {
		String criterio;
		try {
			criterio = SysmanFunciones.concatenar(" COMPANIA = ''", compania, "'' AND ELEMENTO = ''",
					registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()).toString(), "'' AND SERIE = ",
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.SERIE.getName()), "0")
							.toString());
			long consecutivo;
			consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(GenericUrlEnum.IMAGEN_ELEMENTOS.toString(),
					criterio, "CONSECUTIVO", "1");

			if (ACCION_INSERTAR.equals(accion)) {
				registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al cargar un archivo desde el control SubirImagen
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 *
	 */
	public void cargarArchivoSubirImagen(FileUploadEvent event) {

		// <CODIGO_DESARROLLADO>
		try {
			if (accion.equals(ACCION_INSERTAR)) {
				generarConsecutivo();
			}

			is = event.getFile().getInputstream();
			nombreArchivo = event.getFile().getFileName();
			if (tieneExtensionValida(nombreArchivo)) {
				rutaDocumentos = SysmanFunciones.concatenar(SessionUtil.getRuta(10), "Imagenes");

				File directorioImagenes = new File(SysmanFunciones.concatenar(SessionUtil.getRuta(10), "Imagenes"));

				if (!directorioImagenes.isDirectory()) {

					directorioImagenes.mkdir();
				}
				rutaDocumentos = SysmanFunciones.concatenar(rutaDocumentos, "/", "CatalogoElementos", "/");
				File directorioCatalogo = new File(rutaDocumentos);

				if (!directorioCatalogo.isDirectory()) {
					directorioCatalogo.mkdir();
				}

				registrarImagen();
				cargarImagen();

				visibleEditar = true;
			} else {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4100"));
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	public void cargarImagen() {
		try {
			String rutaImagen = SysmanFunciones.concatenar(SessionUtil.getRuta(10), "Imagenes", File.separator,
					"CatalogoElementos", File.separator, nombre);
			File file = new File(rutaImagen);
			FileInputStream inputStream = new FileInputStream(file);
			imagen = JsfUtil.encodeImage(rutaImagen);
			inputStreamImagen = inputStream;
			inputStream.close();
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1978"));
			imagen = null;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private boolean tieneExtensionValida(String nombreArchivo) {
		String regex = "([^w]+(\\.(?i)(jpg|png|jpeg))$)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(nombreArchivo);
		return matcher.matches();
	}
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>

	public String asignarNombre() {

		if ("C".equals(tipo)) {
			nombre = SysmanFunciones.concatenar("C_",
					registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()).toString(), "_",
					registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString(), ".jpg");
		} else {
			nombre = SysmanFunciones.concatenar("D_",
					registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()).toString(), "_",
					registro.getCampos().get(GeneralParameterEnum.SERIE.getName()).toString(), "_",
					registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString(), ".jpg");
		}
		return nombre;
	}

	public void registrarImagen() {
		JsfUtil.upload(is, asignarNombre(), rutaDocumentos);

	}

	// </METODOS_ADICIONALES>
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
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>

		precargarRegistro();

		if (ACCION_MODIFICAR.equals(accion)) {
			asignarNombre();
			cargarImagen();
			visibleEditar = true;

			tipo = SysmanFunciones.nvl(registro.getCampos().get("TIPO"), "C").toString();
			if ("C".equals(tipo)) {
				serieVisible = false;
			} else {
				serieVisible = true;
			}
		} else {
			visibleEditar = false;
			imagen = null;
		}

		cargarlistaSerie();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 */
	@Override
	public boolean insertarAntes() {

		// <CODIGO_DESARROLLADO>

		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

		if ("C".equals(tipo)) {
			registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), "0");
		}

		if (rutaDocumentos != null) {
			if (tieneExtensionValida(SysmanFunciones.nvlStr(nombreArchivo, ""))) {
				registro.getCampos().put("RUTA", asignarNombre());
				registro.getCampos().remove("TIPO");
			}
			return true;
		} else {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3814"));
			return false;
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 */
	@Override
	public boolean actualizarAntes() {
		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

		// <CODIGO_DESARROLLADO>
		if (ACCION_MODIFICAR.equals(accion)) {
			registro.getCampos().remove("TIPO");
		}

		return true;
		// </CODIGO_DESARROLLADO>

	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		if ("NO APLICA".equals(registro.getCampos().get("SERIE"))) {
			registro.getCampos().put("SERIE", "0");
		}

		// </CODIGO_DESARROLLADO>
		return true;

	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}
	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>

	public void seleccionarFilaElemento(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()).toString());
		registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
				registroAux.getCampos().get("NOMBRELARGO").toString());

		cargarlistaSerie();
	}

	public List<Registro> getListaSerie() {
		return listaSerie;
	}

	public void setListaSerie(List<Registro> listaSerie) {
		this.listaSerie = listaSerie;
	}

	public RegistroDataModelImpl getListaElemento() {
		return listaElemento;
	}

	public void setListaElemento(RegistroDataModelImpl listaElemento) {
		this.listaElemento = listaElemento;
	}

	public List<Registro> getListaConsecutivo() {
		return listaConsecutivo;
	}

	public void setListaConsecutivo(List<Registro> listaConsecutivo) {
		this.listaConsecutivo = listaConsecutivo;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public boolean isSerieVisible() {
		return serieVisible;
	}

	public void setSerieVisible(boolean serieVisible) {
		this.serieVisible = serieVisible;
	}

	public boolean isVisibleEditar() {
		return visibleEditar;
	}

	public void setVisibleEditar(boolean visibleEditar) {
		this.visibleEditar = visibleEditar;
	}

	public String getNombreImagen() {
		return nombreImagen;
	}

	public void setNombreImagen(String nombreImagen) {
		this.nombreImagen = nombreImagen;
	}

	public byte[] getImagenBytes() {
		return imagenBytes;
	}

	public void setImagenBytes(byte[] imagenBytes) {
		this.imagenBytes = imagenBytes;
	}

	public InputStream getInputStreamImagen() {
		return inputStreamImagen;
	}

	public void setInputStreamImagen(InputStream inputStreamImagen) {
		this.inputStreamImagen = inputStreamImagen;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	// </SET_GET_ADICIONALES>
}
