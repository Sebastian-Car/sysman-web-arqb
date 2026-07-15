/*-
 * FrmAnexoshvControlador.java
 *
 * 1.0
 * 
 * 18/09/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmAnexosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/09/2025
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmAnexoshvControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * Variable que almacena el modulo
	 */
	private final String modulo;

	/**
	 * Usuario que ingresa a la aplicacion
	 */
	private final String usuario;
//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * Adjuntar y funciona como contenedor del archivo que se desea cargar
	 */
	private UploadedFile archivoCargaAdjuntar;

	private String numeroDocumento;
	private String sucursal;
	private boolean permiteEliminar;
	private String rutaEliminar;
	private Registro registroEliminar;

	private Map<String, Object> ridDatosPersonales;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoNivel;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoNivelE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;

	@EJB
	private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmAnexoshvControlador
	 */
	@SuppressWarnings("unchecked")
	public FrmAnexoshvControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();
		try {
			numFormulario = 2540;
			validarPermisos();
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				ridDatosPersonales = (Map<String, Object>) parametrosEntrada.get("rid");

				numeroDocumento = SysmanFunciones.nvl(parametrosEntrada.get("dp_numedocu"), "").toString();
				sucursal = SysmanFunciones.nvl(parametrosEntrada.get("sucursal"), "").toString();
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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
		enumBase = GenericUrlEnum.CONFIGURACION_ANEXOSHV;
		crearJerarquiaCarpetas();
		reasignarOrigen();
		buscarLlave();
		registro = new Registro();
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCodigoNivel();
		cargarListaCodigoNivelE();
//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	private void crearJerarquiaCarpetas() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(), numeroDocumento);

		Parameter parameter = new Parameter();
		parameter.setFields(param);

		UrlBean urlBean = UrlServiceUtil.getUrlBeanById(FrmAnexosControladorUrlEnum.URL8858.getValue());

		try {
			requestManager.save(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		parametrosListado.put(GeneralParameterEnum.MODULO.getName(), modulo);

		parametrosListado.put(GeneralParameterEnum.REFERENCIA.getName(), numeroDocumento);
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCodigoNivel
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoNivel() {

		HashMap<String, Object> parametros = new HashMap<>();

		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.MODULO.getName(), modulo);
		parametros.put(GeneralParameterEnum.REFERENCIA.getName(), numeroDocumento);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmAnexosControladorUrlEnum.URL7559.getValue());

		listaCodigoNivel = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigoNivel
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoNivelE() {
		HashMap<String, Object> parametros = new HashMap<>();

		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.MODULO.getName(), modulo);
		parametros.put(GeneralParameterEnum.REFERENCIA.getName(), numeroDocumento);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmAnexosControladorUrlEnum.URL7559.getValue());

		listaCodigoNivel = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros,
				true, GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * Metodo ejecutado al oprimir el boton Guardar
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirGuardar(Registro reg, int indice) {
		String ruta;
		String archivoOriginal = archivoCargaAdjuntar.getFileName(); // Archivo original

		// Obtener el codigo del registro
		String codigoRegistro = reg.getCampos().get(GeneralParameterEnum.NIVEL_AGRUPAMIENTO.getName()).toString();

		// Validar que el archivo no esta vacio
		if (SysmanFunciones.validarVariableVacio(archivoOriginal)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB3979")); // Mensaje de archivo vacio
			return;
		}

		// Si el codigo es "20", solo permitir la carga de imagenes
		if ("20".equals(codigoRegistro)) {
			// Verificar si el archivo tiene una extension de imagen
			if (!archivoOriginal.toLowerCase().endsWith(".jpg") && !archivoOriginal.toLowerCase().endsWith(".png")
					&& !archivoOriginal.toLowerCase().endsWith(".jpeg")
					&& !archivoOriginal.toLowerCase().endsWith(".gif")) {
				JsfUtil.agregarMensajeError("Solo se permiten archivos de imagen (jpg, png, jpeg) para el c贸digo 20.");
				return;
			}
		}

		try {
			// Obtener la ruta del servidor para los anexos
			ruta = ejbSysmanUtil.consultarParametro(compania, "RUTA SERVIDOR DE ANEXOS",
					Integer.toString(SysmanConstantes.CODIGO_APLICACION_GENERAL), new Date(), false);

			// Crear la ruta del anexo con base en la compa駃a, modulo y numero de documento
			ruta = ruta + ejbHojasDeVidaCero.crearRutaAnexos(compania, Integer.parseInt(modulo), numeroDocumento,
					codigoRegistro);

			// Llama a verificarRuta pasando codigoRegistro para ajustar el nombre si es
			// "20"
			ruta = verificarRuta(ruta, codigoRegistro);

			// Subir el archivo a la ruta generada con el nombre ajustado
			JsfUtil.upload(archivoCargaAdjuntar.getInputstream(), ruta);

			// Actualizar la ruta del archivo adjunto en el registro
			actualizarRutaAdjunto(ruta, reg);

			reasignarOrigen();

			// Mensaje de exito
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4083"));

		} catch (NumberFormatException | SystemException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo ejecutado al oprimir el boton Eliminar
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirEliminar(Registro reg, int indice) {
		permiteEliminar = true;
		rutaEliminar = SysmanFunciones.nvl(reg.getCampos().get("RUTA"), "").toString();
		registroEliminar = reg;
	}

	/**
	 * Metodo ejecutado al oprimir el boton Descargar
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirDescargar(Registro reg, int indice) {
		archivoDescarga = null;
		String ruta = SysmanFunciones.nvl(reg.getCampos().get("RUTA"), "").toString();

		if (SysmanFunciones.validarVariableVacio(ruta)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4332"));
			return;
		}

		File adjunto = new File(ruta);

		try (InputStream inputStream = new FileInputStream(adjunto)) {
			byte[] vec = new byte[(int) adjunto.length()];

			inputStream.read(vec, 0, vec.length);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(vec), adjunto.getName());
		} catch (IOException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control permiteEliminar
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarpermiteEliminar() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo permiteEliminar en
	 * la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void aceptarpermiteEliminar() {
		if (SysmanFunciones.validarVariableVacio(rutaEliminar)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4332"));
			return;
		}

		File adjunto = new File(rutaEliminar);

		adjunto.delete();

		actualizarRutaAdjunto("", registroEliminar);

		JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2490"));

		permiteEliminar = false;
	}

	private void actualizarRutaAdjunto(String ruta, Registro reg) {

		Map<String, Object> param = new TreeMap<>();

		try {
			param.put(GeneralParameterEnum.RUTA.getName(), ruta);
			param.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
			param.put("KEY_MODULO", modulo);
			param.put("KEY_NIVEL_AGRUPAMIENTO",
					reg.getCampos().get(GeneralParameterEnum.NIVEL_AGRUPAMIENTO.getName()).toString());
			param.put(GeneralParameterEnum.KEY_CODIGO.getName(), numeroDocumento);
			param.put("KEY_CONSECUTIVO", reg.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString());

			param.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			Parameter parameter = new Parameter();
			parameter.setFields(param);

			UrlBean urlBean = UrlServiceUtil.getUrlBeanById(FrmAnexosControladorUrlEnum.URL1983001.getValue());

			requestManager.update(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo que Verifica si una ruta de directorio existe y crea el directorio si
	 * no es as铆. Genera un nombre de archivo basado en el c贸digo de registro y la
	 * carga de archivo actual.
	 *
	 * @param ruta           La ruta del directorio donde se desea guardar el
	 *                       archivo.
	 * @param codigoRegistro El c贸digo que determina el formato del nombre del
	 *                       archivo. Si el c贸digo es "20", se usar谩 un nombre
	 *                       personalizado.
	 * @return La ruta completa del archivo con el nombre adecuado.
	 */
	private String verificarRuta(String ruta, String codigoRegistro) {
		File verificar = new File(ruta);
		if (!verificar.isDirectory()) {
			verificar.mkdirs();
		}

		// Verificar si el c贸digo de registro es "20"
		String nombreArchivo;
		if ("20".equals(codigoRegistro)) {
			// Usar el nombre personalizado "numeroDocumento_FOTO_PERFIL"
			String extensionArchivo = archivoCargaAdjuntar.getFileName()
					.substring(archivoCargaAdjuntar.getFileName().lastIndexOf('.'));
			nombreArchivo = numeroDocumento + "_FOTO_PERFIL" + extensionArchivo;
		} else {
			// Usar el nombre original del archivo
			nombreArchivo = numeroDocumento + "_" + archivoCargaAdjuntar.getFileName();
		}

		// Concatenar el nombre de archivo adecuado a la ruta
		ruta = ruta + "/" + nombreArchivo;

		return ruta;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo permiteEliminar en
	 * la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void cancelarpermiteEliminar() {
		permiteEliminar = false;
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoNivel
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoNivel(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.NIVEL_AGRUPAMIENTO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoNivel
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoNivelE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get(GeneralParameterEnum.NIVEL_AGRUPAMIENTO.getName()).toString();
		registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR2101-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Me.Caption =
		 * UCase("EVALUADOR, EVALUADO " & GetNClaseEvaluacion()) End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado TODO
	 * DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {

		try {
			long aux = ejbSysmanUtil.generarConsecutivoConValorInicial("CONFIGURACION_ANEXOSHV",
					"CONFIGURACION_ANEXOSHV.COMPANIA =''" + compania + "''" + " AND CONFIGURACION_ANEXOSHV.MODULO ="
							+ modulo + " AND CONFIGURACION_ANEXOSHV.NIVEL_AGRUPAMIENTO = ''"
							+ registro.getCampos().get(GeneralParameterEnum.NIVEL_AGRUPAMIENTO.getName())
							+ "'' AND CONFIGURACION_ANEXOSHV.CODIGO=" + numeroDocumento,
					"CONSECUTIVO", "1");

			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registro.getCampos().put(GeneralParameterEnum.MODULO.getName(), modulo);
			registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), numeroDocumento);
			registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), aux);
			registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
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
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {

		registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
		registro.getCampos().remove("ES_ARCHIVO");
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
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
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes() {
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
	}

	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void ejecutarrcCerrar() {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("rid", ridDatosPersonales);
		parametros.put("numeroDcto", numeroDocumento);
		parametros.put("sucursal", sucursal);

		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR.getCodigo()));
		direccionador.setParametros(parametros);
		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub
	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
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

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCodigoNivel
	 * 
	 * @return listaCodigoNivel
	 */
	public RegistroDataModelImpl getListaCodigoNivel() {
		return listaCodigoNivel;
	}

	/**
	 * Asigna la lista listaCodigoNivel
	 * 
	 * @param listaCodigoNivel Variable a asignar en listaCodigoNivel
	 */
	public void setListaCodigoNivel(RegistroDataModelImpl listaCodigoNivel) {
		this.listaCodigoNivel = listaCodigoNivel;
	}

	/**
	 * Retorna la lista listaCodigoNivel
	 * 
	 * @return listaCodigoNivel
	 */
	public RegistroDataModelImpl getListaCodigoNivelE() {
		return listaCodigoNivelE;
	}

	/**
	 * Asigna la lista listaCodigoNivel
	 * 
	 * @param listaCodigoNivel Variable a asignar en listaCodigoNivel
	 */
	public void setListaCodigoNivelE(RegistroDataModelImpl listaCodigoNivelE) {
		this.listaCodigoNivelE = listaCodigoNivelE;
	}

	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	public boolean isPermiteEliminar() {
		return permiteEliminar;
	}

	public void setPermiteEliminar(boolean permiteEliminar) {
		this.permiteEliminar = permiteEliminar;
	}
}
