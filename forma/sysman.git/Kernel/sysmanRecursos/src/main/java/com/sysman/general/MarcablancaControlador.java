/*-
 * MarcablancaControlador.java
 *
 * 1.0
 * 
 * 25/11/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;
import org.apache.poi.util.IOUtils;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 25/11/2022
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class MarcablancaControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private boolean visibleImages;
	
	private boolean visibleImagesLoad;
	
	private boolean visiblesEditar;

	private String nombreBannerCompania;

	private String imagenBanner;

	private String directorioBanner;

	private String infBanner;

	private String rutaBannerpptal;
	
	private String nombreCargandoCompania;

	private String imagen;

	private String imagenLoad;

	private String directorioLoad;

	private String rutaLoad;
	/**
	 * Arreglo de bytes que contiene la imagen de la compaÃ±ia que se carga desde el
	 * componente de Primefaces.
	 */
	private byte[] imagenBannerBytes;
	/**
	 * Arreglo de bytes que contiene la imagen de la compaÃ±ia que se carga desde el
	 * componente de Primefaces.
	 */
	private byte[] imagenLoadBytes;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
//<DECLARAR_ATRIBUTOS>
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos logo y
	 * funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivologo;

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
	 * Crea una nueva instancia de MarcablancaControlador
	 * @throws SystemException 
	 */
	public MarcablancaControlador() {
		super();
		compania = SessionUtil.getCompania();
		contArchivologo = new ContenedorArchivo();
		
		try {
			// 2379
			numFormulario = GeneralCodigoFormaEnum.CONFIGURAR_MARCA_BLANCA.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
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
		// <CARGAR_LISTA_COMBO_GRANDE>
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
		enumBase = GenericUrlEnum.CONFIG_MARCA_BLANCA;
		buscarLlave();
		asignarOrigenDatos();
		
		try {
			String rutaParam = ejbSysmanUtil.consultarParametro(compania,"RUTA_BANNER_MARCABLANCA_LOCAL", "999", new Date(), false);
			if(rutaParam.equals("NO")) {
				directorioBanner = "/opt/sysman/data/imagenes";
				setRutaBannerpptal("/opt/sysman/data/imagenes");
			}else if(rutaParam.equals("SI")) {
				directorioBanner = "C:\\opt\\sysman\\data\\imagenes";
				setRutaBannerpptal("C:\\opt\\sysman\\data\\imagenes");
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}		
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
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
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */

//<METODOS_CARGAR_LISTA>
	public void cargarLista() {
		UrlBean urlBean;
		try {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.CONFIG_MARCA_BLANCA.getGridKey());

			Map<String, Object> param = new TreeMap<>();

			listaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.CONFIG_MARCA_BLANCA.getTable()));
			listaInicialF = listaInicial;
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	
//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>	
	/**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault)
    {
        String parametro = null;
        try
        {
            parametro = (String) SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(),
                            false), "NO");
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
	public void cargarArchivolectorbanner(FileUploadEvent event) {
		UploadedFile archivoImagen = event.getFile();
		nombreBannerCompania = archivoImagen.getFileName();
		imagenBannerBytes = getFileContent(archivoImagen);
		imagenBanner = JsfUtil.encodeImage(imagenBannerBytes);
//        JsfUtil.ejecutarJavaScript("cargarImagen('FR2379_nuevo:PL4595:IM2057');");
	}

	/**
	 * Recibe el archivo que se cargo en el selector de imagen y lo comnvierte a un
	 * arreglo de bytes.
	 * 
	 * @param file Archivo subido por medio del componente <i>fileUpload</i> de
	 *             Primefaces.
	 * @return Archivo como arreglo de bytes.
	 */
	private byte[] getFileContent(UploadedFile file) {
		byte[] bytes = new byte[0];
		try (InputStream stream = file.getInputstream();) {
			bytes = IOUtils.toByteArray(stream);
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2839"));
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(idioma.getString("MSM_TRANS_INTERRUMPIDA") + "<br>" + ex.getMessage());
		}
		return bytes;
	}

	public boolean validarArchivoImagen() {
		if (!SysmanFunciones.validarVariableVacio(imagenBanner)) {
			try {
				File ficheroImagen = new File(directorioBanner);
				String subExtImagen = nombreBannerCompania.substring(nombreBannerCompania.lastIndexOf('.'),
						nombreBannerCompania.length());

				if (SysmanFunciones.validarVariableVacio(directorioBanner)) {
					JsfUtil.agregarMensajeError(idioma.getString("TB_TB96"));
					return false;
				}

				if (!tieneExtensionValida(nombreBannerCompania)) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB97"));
					return false;
				}

				String msg = idioma.getString("TB_TB98");
				nombreBannerCompania = "banner" + subExtImagen;
				directorioBanner = !directorioBanner.endsWith(File.separator) ? directorioBanner + File.separatorChar
						: directorioBanner;
				String ruta = directorioBanner + nombreBannerCompania;
				infBanner = msg + " " + ruta;
				registro.getCampos().put("NOMBRE_IMAGEN", nombreBannerCompania);
				registro.getCampos().put("VALOR", ruta);

				if (ficheroImagen.exists()) {
					return true;
				} else if (ficheroImagen.isDirectory()) {
					ficheroImagen.mkdir();
					return true;
				} else {
					JsfUtil.agregarMensajeAlertaVentana(idioma.getString("TB_TB103"));
					return false;
				}
			} catch (NullPointerException ex) {
				Logger.getLogger(CompaniasControlador.class.getName()).log(Level.SEVERE, null, ex);
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB104"));
				return false;
			}
		}

		return true;
	}
	
	private boolean tieneExtensionValida(String nombreArchivo) {
		String regex = "([^\\s]+(\\.(?i)(svg|gif|jpg))$)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(nombreArchivo);
		return matcher.matches();
	}

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
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		if (registro.getCampos().get("LLAVE").toString().equals("BANNER")) {
			visibleImages = true;
			visibleImagesLoad = false;
			visiblesEditar = true;
		}else if( registro.getCampos().get("LLAVE").toString().equals("CARGANDO")) {
			visibleImagesLoad = true;
			visibleImages = false;
			visiblesEditar = true;
		} else {
			visibleImages = false;
			visibleImagesLoad = false;
			visiblesEditar = false;
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		// </CODIGO_DESARROLLADO>
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
		// <CODIGO_DESARROLLADO>
		
		if(registro.getCampos().get("LLAVE").toString().equals("BANNER")) {
			if(!validarArchivoImagen()) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB104"));
				return false;
			}
		}
		// </CODIGO_DESARROLLADO>
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
		// carga la imagen del banner
		if (imagenBannerBytes != null && directorioBanner != null && nombreBannerCompania != null) {
			JsfUtil.upload(imagenBannerBytes, directorioBanner, nombreBannerCompania);
		}
		// carga la imagen del Load
		if (imagenLoadBytes != null && directorioLoad != null && nombreCargandoCompania != null) {
			JsfUtil.upload(imagenLoadBytes, directorioLoad, nombreCargandoCompania);
		}
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
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
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

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna el objeto contArchivologo
	 * 
	 * @return contArchivologo
	 */
	public ContenedorArchivo getContArchivologo() {
		return contArchivologo;
	}

	/**
	 * Asigna el objeto contArchivologo
	 * 
	 * @param contArchivologo Variable a asignar en contArchivologo
	 */
	public void setContArchivologo(ContenedorArchivo contArchivologo) {
		this.contArchivologo = contArchivologo;
	}
//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
//</SET_GET_ADICIONALES>

	public boolean isVisibleImages() {
		return visibleImages;
	}

	public void setVisibleImages(boolean visibleImages) {
		this.visibleImages = visibleImages;
	}

	public String getImagenBanner() {
		return imagenBanner;
	}

	public void setImagenBanner(String imagenBanner) {
		this.imagenBanner = imagenBanner;
	}

	public String getRutaLoad() {
		return rutaLoad;
	}

	public void setRutaLoad(String rutaLoad) {
		this.rutaLoad = rutaLoad;
	}

	public String getImagenLoad() {
		return imagenLoad;
	}

	public void setImagenLoad(String imagenLoad) {
		this.imagenLoad = imagenLoad;
	}

	public String getRutaBannerpptal() {
		return rutaBannerpptal;
	}

	public void setRutaBannerpptal(String rutaBannerpptal) {
		this.rutaBannerpptal = rutaBannerpptal;
	}

	public boolean isVisibleImagesLoad() {
		return visibleImagesLoad;
	}

	public void setVisibleImagesLoad(boolean visibleImagesLoad) {
		this.visibleImagesLoad = visibleImagesLoad;
	}

	public boolean isVisiblesEditar() {
		return visiblesEditar;
	}

	public void setVisiblesEditar(boolean visiblesEditar) {
		this.visiblesEditar = visiblesEditar;
	}

}
