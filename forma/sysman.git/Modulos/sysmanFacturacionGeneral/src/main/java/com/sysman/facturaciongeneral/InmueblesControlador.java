/*-
 * InmueblesControlador.java
 *
 * 1.0
 * 
 * 22/10/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import java.io.File;
import java.io.FileInputStream;
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
import javax.ejb.Stateless;
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
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.InmueblesControladorUrlEnum;
import com.sysman.general.CompaniasControlador;
import com.sysman.general.TercerosControlador;
import com.sysman.general.enums.TercerosControladorEnum;
import com.sysman.general.enums.TercerosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 22/10/2024
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class InmueblesControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String tipo;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String edificio;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ubicacion;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
	/**
     * TODO DOCUMENTACION NECESARIA
     */
	private List<Registro> listatipoInmueble;
	/**
     * TODO DOCUMENTACION NECESARIA
     */
	private List<Registro> listaEdificio;
	/**
     * TODO DOCUMENTACION NECESARIA
     */
	private List<Registro> listaubicacion;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaDepartamento;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaCiudad;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaconcepto1;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaconcepto2;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaconcepto3;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaconcepto4;
	
	/**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al nit
     */
    private RegistroDataModelImpl listaNit;

	/**
	 * Arreglo de bytes que contiene la imagen de la compañia que se carga desde el
	 * componente de Primefaces.
	 */
	private byte[] imagenInmuebleByte;
	
	private String nombreImagen;
	
	private String extencionImagen;
	
	private String imagenInmueble;
	
	private String directorioImagen;
	
    private InputStream inputStreamImagen;
	 /**
     * Variable encargada de almacenar temporalmente el ano de cobro
     * de ingreso al modulo
     */
    private String ano;
    /**
     * Variable encargada de almacenar temporalmente el tipo de cobro
     * de ingreso al modulo
     */

    private String tipoCobro;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    private int areaInmueble;
    
	private boolean activo;
	
	private boolean disponible;
	
	private boolean varVolver;
	
	private static final String RUTA_INNUEBLE = "/inmuebles.sysman";
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de InmueblesControlador
	 */
	public InmueblesControlador() {
		super();
		compania = SessionUtil.getCompania();
		ano = (String) SessionUtil.getSessionVar(
                ConstantesFacturacionGenEnum.ANIO.getValue());
		tipoCobro = (String) SessionUtil.getSessionVar(
                ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
		
		varVolver = true;
		try {
			//2490
			numFormulario = GeneralCodigoFormaEnum.FRM_INMUEBLE.getCodigo();
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
		cargarListaconcepto1();
		cargarListaconcepto2();
		cargarListaconcepto3();
		cargarListaconcepto4();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		cargarListatipoInmueble();
		cargarListaEdificio();
		cargarListaubicacion();
		cargarListaDepartamento();
		cargarListaNit();
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
//	public void cargarLista() {
//		UrlBean urlBean;
//		try {
//			urlBean = UrlServiceUtil.getInstance()
//					.getUrlServiceByUrlByEnumID(GenericUrlEnum.INMUEBLE_FACTURACION.getGridKey());
//
//			Map<String, Object> param = new TreeMap<>();
//			param.put("COMPANIA", compania);
//
//			listaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
//					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.INMUEBLE_FACTURACION.getTable()));
//			listaInicialF = listaInicial;
//		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
//			logger.error(e.getMessage(), e);
//			JsfUtil.agregarMensajeError(e.getMessage());
//		}
//	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.INMUEBLE_FACTURACION;
		buscarLlave();
		asignarOrigenDatos();
//		cargarLista();
		
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
		
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	}

	//<METODOS_CARGAR_LISTA>	
	/**
     * 
     * Carga la lista listatipoInmueble
     *
     */
	public void cargarListatipoInmueble() {
		Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        try {
            listatipoInmueble = RegistroConverter.toListRegistro(
            				requestManager.getList(UrlServiceUtil.getInstance()
            								.getUrlServiceByUrlByEnumID(
            										InmueblesControladorUrlEnum.URL1980001.getValue())
            								.getUrl(),param));
        } catch(SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
	}
	/**
     * 
     * Carga la lista listaEdificio
     *
     */
	public void cargarListaEdificio() {
		Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        try {
            listaEdificio = RegistroConverter.toListRegistro(
            			requestManager.getList(UrlServiceUtil.getInstance()
            							.getUrlServiceByUrlByEnumID(
            									InmueblesControladorUrlEnum.URL1981001.getValue())
            							.getUrl(),param));
        } catch(SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
	}
	/**
     * 
     * Carga la lista listaubicacion
     *
     */
	public void cargarListaubicacion() {
		Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        try {
            listaubicacion = RegistroConverter.toListRegistro(
            			 requestManager.getList(UrlServiceUtil.getInstance()
            							 .getUrlServiceByUrlByEnumID(
            									 InmueblesControladorUrlEnum.URL1982001.getValue())
            							 .getUrl(),param));
        } catch(SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
	}
	/**
	 * 
	 * Carga la lista listaDepartamento
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaDepartamento() {
		Map<String, Object> param = new TreeMap<>();
        param.put(TercerosControladorEnum.PAIS.getValue(), "001");

        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		InmueblesControladorUrlEnum.URL18011
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}

	/**
	 * 
	 * Carga la lista listaCiudad
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCiudad() {
		Map<String, Object> param = new TreeMap<>();

        param.put(TercerosControladorEnum.PAIS.getValue(), "001");
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.DEPARTAMENTO.getName()));

        try {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		InmueblesControladorUrlEnum.URL18599
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}

	/**
	 * 
	 * Carga la lista listaconcepto1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaconcepto1() {
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean;
    	urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InmueblesControladorUrlEnum.URL18021
                                                        .getValue());
    	
    	listaconcepto1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaconcepto2
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaconcepto2() {
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean;
    	urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InmueblesControladorUrlEnum.URL18021
                                                        .getValue());
    	
    	listaconcepto2 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaconcepto3
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaconcepto3() {
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean;
    	
        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InmueblesControladorUrlEnum.URL18021
                                                        .getValue());
    	

    	listaconcepto3 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaconcepto4
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaconcepto4() {
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean;
    	
        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InmueblesControladorUrlEnum.URL18021
                                                        .getValue());
    	

    	listaconcepto4 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
	}
	
	/**
    *
    * Carga la lista listaNit
    *
    * Metodo encargado de hacer el llamado a la base datos y
    * almacenar los datos en la lista listaNit
    */
   public void cargarListaNit()
   {
       UrlBean urlBean = UrlServiceUtil.getInstance()
                       .getUrlServiceByUrlByEnumID(
                                       FacturacionconceptosControladorUrlEnum.URL20067
                                                       .getValue());
       Map<String, Object> param = new TreeMap<>();
       param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

       listaNit = new RegistroDataModelImpl(urlBean.getUrl(),
                       urlBean.getUrlConteo().getUrl(), param, true, "NIT");

       // 14128
   }
//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
	/**
	 * Metodo ejecutado al cambiar el control Departamento
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarDepartamento() {
		// <CODIGO_DESARROLLADO>
		cargarListaCiudad();
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaconcepto1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaconcepto1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO1", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMCONCEPTO1", registroAux.getCampos().get("NOMBRE"));
		registro.getCampos().put("VALORCONCEPTO1", registroAux.getCampos().get("VALOR_BASE"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaconcepto2
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaconcepto2(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO2", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMCONCEPTO2", registroAux.getCampos().get("NOMBRE"));
		registro.getCampos().put("VALORCONCEPTO2", registroAux.getCampos().get("VALOR_BASE"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaconcepto3
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaconcepto3(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO3", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMCONCEPTO3", registroAux.getCampos().get("NOMBRE"));
		registro.getCampos().put("VALORCONCEPTO3", registroAux.getCampos().get("VALOR_BASE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaconcepto4
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaconcepto4(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO4", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMCONCEPTO4", registroAux.getCampos().get("NOMBRE"));
		registro.getCampos().put("VALORCONCEPTO4", registroAux.getCampos().get("VALOR_BASE"));
	}
	
	/**
    *
    * Metodo ejecutado al seleccionar una fila de la lista listaNit
    *
    *
    * @param event
    * objeto que encapsula la accion proveniente de la vista
    */
   public void seleccionarFilaNit(SelectEvent event)
   {
       Registro registroAux = (Registro) event.getObject();
       registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                       registroAux.getCampos().get("NIT"));
       registro.getCampos().put("NOMBRETERCERO", registroAux.getCampos()
                       .get(GeneralParameterEnum.NOMBRE.getName()));
       registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                       registroAux.getCampos()
                                       .get(GeneralParameterEnum.SUCURSAL
                                                       .getName()));		
	}
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	
//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>
	public void cargarArchivolectorload(FileUploadEvent event) {
		UploadedFile archivoImagen = event.getFile();
		nombreImagen = archivoImagen.getFileName();
		extencionImagen = archivoImagen.getContentType();
		imagenInmuebleByte = getFileContent(archivoImagen);
		imagenInmueble = JsfUtil.encodeImage(imagenInmuebleByte);
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
		if (!SysmanFunciones.validarVariableVacio(imagenInmueble)) {
			try {
				File ficheroImagen = new File(directorioImagen);
				String subExtImagen = nombreImagen.substring(nombreImagen.lastIndexOf('.'),
						nombreImagen.length());

				if (SysmanFunciones.validarVariableVacio(directorioImagen)) {
					JsfUtil.agregarMensajeError(idioma.getString("TB_TB96"));
					return false;
				}

				String msg = idioma.getString("TB_TB98");
				nombreImagen = compania+"_"+registro.getCampos().get("TIPO").toString()+"_"+registro.getCampos().get("CODIGOINMUEBLE").toString()+subExtImagen;
				
				if (!tieneExtensionValida(nombreImagen)) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB97"));
					return false;
				}
				
				directorioImagen = !directorioImagen.endsWith(File.separator) ? directorioImagen + File.separatorChar
						: directorioImagen;
				String ruta = directorioImagen + nombreImagen;
//				infBanner = msg + " " + ruta;
				registro.getCampos().put("NOMBRE_IMAGEN", nombreImagen);
				registro.getCampos().put("RUTA_IMAGEN", ruta);

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
		String regex = "([^\\s]+(\\.(?i)(svg|gif|jpg|png))$)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(nombreArchivo);
		return matcher.matches();
	}
	private Object[] cargar(String ruta) {
        // 0,nombreIconoCns
        // 1,infImagenCns
        // 2,directorioCns
        // 3,iconoCnsc
        // 4,inputStream
		Object[] aux = new Object[5];
		if(!registro.getCampos().isEmpty()) {	        
	        if(registro.getCampos().get("NOMBRE_IMAGEN") != null && registro.getCampos().get("RUTA_IMAGEN") != null) {
				String rutaImagen = SysmanFunciones.nvl(
		                        registro.getCampos().get(ruta).toString()+registro.getCampos().get("NOMBRE_IMAGEN").toString(), "").toString();
		        File file = new File(rutaImagen);
		        if (!rutaImagen.isEmpty()) {
		            try (FileInputStream inputStream = new FileInputStream(file)) {
		                aux[0] = rutaImagen.substring(
		                                rutaImagen.lastIndexOf(File.separator) + 1,
		                                rutaImagen.length());
		
		                aux[1] = aux[0];
		                aux[2] = rutaImagen.substring(0,
		                                rutaImagen.lastIndexOf(File.separator))
		                    + File.separator;
		                aux[3] = JsfUtil.encodeImage(rutaImagen);
		                aux[4] = inputStream;
		                return aux;
		            }
		            catch (StringIndexOutOfBoundsException ex) {
		                logger.error(ex.getMessage(), ex);
		                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB123"));
		            }
		            catch (FileNotFoundException ex) {
		                logger.error(ex.getMessage(), ex);
		                JsfUtil.agregarMensajeAlerta(
		                                idioma.getString("MSM_RUTA_ERRONEA") + directorioImagen);
		            }
		            catch (Exception ex) {
		                logger.error(ex.getMessage(), ex);
		                JsfUtil.agregarMensajeFatal(idioma.getString("TB_TB2840") + " "
		                    + rutaImagen + "<br>" + ex.getMessage());
		            }
		            finally {
		                if (inputStreamImagen != null) {
		                    try {
		                        inputStreamImagen.close();
		                    }
		                    catch (IOException e) {
		                        logger.error(e.getMessage(), e);
		                        JsfUtil.agregarMensajeError(e.getMessage());
		                    }
		                }
		            }
		        }
	        }
		}
        return aux;
    }
	
	public void cargarImagen() {
		imagenInmueble = null;
        Object[] aux = cargar("RUTA_IMAGEN");
        nombreImagen = (String) aux[0];
        //infImagenCns = (String) aux[1];
        directorioImagen = (String) aux[2];
        imagenInmueble = (String) aux[3];
        inputStreamImagen = (InputStream) aux[4];

    }
	/**
	 * Se cargan los datos para combos estaticos y datos que se necesitan inicializados
	 */
	public void cargaDatosPorAccion() {
		if(!ACCION_INSERTAR.equals(accion)) {
			tipo = registro.getCampos().get("TIPO").toString();
			edificio = registro.getCampos().get("EDIFICIO").toString();
			if(registro.getCampos().get("UBICACION") != null) {
				ubicacion = registro.getCampos().get("UBICACION").toString();
			}
			activo = (boolean) registro.getCampos().get("ACTIVO");
			disponible = (boolean) registro.getCampos().get("DISPONIBLE");
			areaInmueble = Integer.parseInt(registro.getCampos().get("AREA").toString());
			cambiarDepartamento();
			cargarImagen();
		} else { 
			activo = true;
			disponible = true;
			tipo = "0";
			edificio = "0";
			ubicacion = "0";
			areaInmueble = 0;
			imagenInmuebleByte = null;
			directorioImagen = "";
			nombreImagen = "";
			imagenInmueble = "";
		}
	}
	public void ejecutarrcVolver() {
		System.out.println("jaco aqui");
		accion = null;
		String[] campos = {};
		Object[] valores = {};
		SessionUtil.redireccionar(RUTA_INNUEBLE, campos, valores);
	}
//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		System.out.println("jaco aqui");
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
		cargaDatosPorAccion();
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
		registro.getCampos().put("TIPO", tipo);
		registro.getCampos().put("EDIFICIO", edificio);
		registro.getCampos().put("UBICACION", ubicacion);
		registro.getCampos().put("AREA", areaInmueble);
		registro.getCampos().put("ACTIVO", activo?-1:0);
		registro.getCampos().put("DISPONIBLE", disponible?-1:0);
		
		if (!SysmanFunciones.validarVariableVacio(imagenInmueble)) {
			nombreImagen = compania+"_"+registro.getCampos().get("TIPO").toString()+"_"+registro.getCampos().get("CODIGOINMUEBLE").toString()+"."+extencionImagen.split("/")[1].toString();
			registro.getCampos().put("NOMBRE_IMAGEN", nombreImagen);
			try {
				String rutaParam = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,"RUTA_IMAGEN_AMBIENTE_DESARROLLO", SessionUtil.getModulo() , new Date(), false), "NO");
				if(rutaParam.equals("NO")) {
					directorioImagen = "/opt/sysman/data/imagenes/";
				}else if(rutaParam.equals("SI")) {
					directorioImagen = "C:\\opt\\sysman\\data\\imagenes\\";
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}	
			registro.getCampos().put("RUTA_IMAGEN", directorioImagen);
		}
		
		// campos que no van en la bd
		registro.getCampos().remove("NOMCONCEPTO1");
		registro.getCampos().remove("NOMCONCEPTO2");
		registro.getCampos().remove("NOMCONCEPTO3");
		registro.getCampos().remove("NOMCONCEPTO4");
		registro.getCampos().remove("VALORCONCEPTO1");
		registro.getCampos().remove("VALORCONCEPTO2");
		registro.getCampos().remove("VALORCONCEPTO3");
		registro.getCampos().remove("VALORCONCEPTO4");
		registro.getCampos().remove("NOMBRETIPO");
		registro.getCampos().remove("NOMBRETERCERO");
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
		if(ACCION_INSERTAR.equals(accion)) {
		// se almacena la imagen en el servidor luego de insertar en bd los datos
			if (imagenInmuebleByte != null && directorioImagen != null && nombreImagen != null) {
				JsfUtil.upload(imagenInmuebleByte, directorioImagen, nombreImagen);
			}
		}
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
		try {
            if (validarArchivoImagen()) {
            	registro.getCampos().put("COMPANIA", compania);
        		registro.getCampos().put("TIPO", tipo);
        		registro.getCampos().put("EDIFICIO", edificio);
        		registro.getCampos().put("UBICACION", ubicacion);
        		registro.getCampos().put("AREA", areaInmueble);
        		registro.getCampos().put("ACTIVO", activo?-1:0);
        		registro.getCampos().put("DISPONIBLE", disponible?-1:0);
        		
        		if (!SysmanFunciones.validarVariableVacio(imagenInmueble)) {
	        		nombreImagen = compania+"_"+registro.getCampos().get("TIPO").toString()+"_"+registro.getCampos().get("CODIGOINMUEBLE").toString()+nombreImagen.substring(nombreImagen.lastIndexOf('.'),
							nombreImagen.length());
	        		registro.getCampos().put("NOMBRE_IMAGEN", nombreImagen);
	        		try {
	        			String rutaParam = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,"RUTA_IMAGEN_AMBIENTE_DESARROLLO", SessionUtil.getModulo() , new Date(), false), "NO");
	        			if(rutaParam.equals("NO")) {
	        				directorioImagen = "/opt/sysman/data/imagenes/";
	        			}else if(rutaParam.equals("SI")) {
	        				directorioImagen = "C:\\opt\\sysman\\data\\imagenes\\";
	        			}
	        		} catch (SystemException e) {
	        			logger.error(e.getMessage(), e);
	        			JsfUtil.agregarMensajeError(e.getMessage());
	        		}	
	        		registro.getCampos().put("RUTA_IMAGEN", directorioImagen);
        		}
        		// campos que no van en la bd
        		registro.getCampos().remove("NOMCONCEPTO1");
        		registro.getCampos().remove("NOMCONCEPTO2");
        		registro.getCampos().remove("NOMCONCEPTO3");
        		registro.getCampos().remove("NOMCONCEPTO4");
        		registro.getCampos().remove("VALORCONCEPTO1");
        		registro.getCampos().remove("VALORCONCEPTO2");
        		registro.getCampos().remove("VALORCONCEPTO3");
        		registro.getCampos().remove("VALORCONCEPTO4");
        		registro.getCampos().remove("NOMBRETIPO");
        		registro.getCampos().remove("NOMBRETERCERO");
                return true;
            }else {
                return false;
            }
        }
        catch (NullPointerException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
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
		if(ACCION_MODIFICAR.equals(accion)) {
			if (imagenInmuebleByte != null && directorioImagen != null && nombreImagen != null) {
				JsfUtil.upload(imagenInmuebleByte, directorioImagen, nombreImagen);
			}
			JsfUtil.ejecutarJavaScript("cargarImagen('FR2490_nuevo:IM2071')");
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// TODO Auto-generated method stub
		try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.INMUEBLE_FACTURACION
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), registro.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "MSM_REGISTRO_ELIMINADO"));
//            cargarLista();

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
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
//		cargarLista();
		// </CODIGO_DESARROLLADO>
		return true;
	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipo
	 * 
	 * @return tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * Asigna la variable tipo
	 * 
	 * @param tipo Variable a asignar en tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Retorna la variable edificio
	 * 
	 * @return edificio
	 */
	public String getEdificio() {
		return edificio;
	}

	/**
	 * Asigna la variable edificio
	 * 
	 * @param edificio Variable a asignar en edificio
	 */
	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}

	/**
	 * Retorna la variable ubicacion
	 * 
	 * @return ubicacion
	 */
	public String getUbicacion() {
		return ubicacion;
	}

	/**
	 * Asigna la variable ubicacion
	 * 
	 * @param ubicacion Variable a asignar en ubicacion
	 */
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
	/**
     * Retorna la lista listatipoInmueble
     * 
     * @return listatipoInmueble
     */
	public List<Registro> getListatipoInmueble() {
        return listatipoInmueble;
    }
    /**
     * Asigna la lista listatipoInmueble
     * 
     * @param listatipoInmueble
     * Variable a asignar en  listatipoInmueble
     */
	public void setListatipoInmueble(List<Registro> listatipoInmueble) {
        this.listatipoInmueble = listatipoInmueble;
    }
	/**
     * Retorna la lista listaEdificio
     * 
     * @return listaEdificio
     */
	public List<Registro> getListaEdificio() {
        return listaEdificio;
    }
    /**
     * Asigna la lista listaEdificio
     * 
     * @param listaEdificio
     * Variable a asignar en  listaEdificio
     */
	public void setListaEdificio(List<Registro> listaEdificio) {
        this.listaEdificio = listaEdificio;
    }
	/**
     * Retorna la lista listaubicacion
     * 
     * @return listaubicacion
     */
	public List<Registro> getListaubicacion() {
        return listaubicacion;
    }
    /**
     * Asigna la lista listaubicacion
     * 
     * @param listaubicacion
     * Variable a asignar en  listaubicacion
     */
	public void setListaubicacion(List<Registro> listaubicacion) {
        this.listaubicacion = listaubicacion;
    }
	/**
	 * Retorna la lista listaDepartamento
	 * 
	 * @return listaDepartamento
	 */
	public List<Registro> getListaDepartamento() {
		return listaDepartamento;
	}

	/**
	 * Asigna la lista listaDepartamento
	 * 
	 * @param listaDepartamento Variable a asignar en listaDepartamento
	 */
	public void setListaDepartamento(List<Registro> listaDepartamento) {
		this.listaDepartamento = listaDepartamento;
	}

	/**
	 * Retorna la lista listaCiudad
	 * 
	 * @return listaCiudad
	 */
	public List<Registro> getListaCiudad() {
		return listaCiudad;
	}

	/**
	 * Asigna la lista listaCiudad
	 * 
	 * @param listaCiudad Variable a asignar en listaCiudad
	 */
	public void setListaCiudad(List<Registro> listaCiudad) {
		this.listaCiudad = listaCiudad;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaconcepto1
	 * 
	 * @return listaconcepto1
	 */
	public RegistroDataModelImpl getListaconcepto1() {
		return listaconcepto1;
	}

	/**
	 * Asigna la lista listaconcepto1
	 * 
	 * @param listaconcepto1 Variable a asignar en listaconcepto1
	 */
	public void setListaconcepto1(RegistroDataModelImpl listaconcepto1) {
		this.listaconcepto1 = listaconcepto1;
	}

	/**
	 * Retorna la lista listaconcepto2
	 * 
	 * @return listaconcepto2
	 */
	public RegistroDataModelImpl getListaconcepto2() {
		return listaconcepto2;
	}

	/**
	 * Asigna la lista listaconcepto2
	 * 
	 * @param listaconcepto2 Variable a asignar en listaconcepto2
	 */
	public void setListaconcepto2(RegistroDataModelImpl listaconcepto2) {
		this.listaconcepto2 = listaconcepto2;
	}
	/**
	 * Retorna la lista listaconcepto2
	 * 
	 * @return listaconcepto2
	 */
	public RegistroDataModelImpl getListaconcepto3() {
		return listaconcepto3;
	}

	/**
	 * Asigna la lista listaconcepto2
	 * 
	 * @param listaconcepto2 Variable a asignar en listaconcepto2
	 */
	public void setListaconcepto3(RegistroDataModelImpl listaconcepto3) {
		this.listaconcepto3 = listaconcepto3;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
//</SET_GET_ADICIONALES>

	public String getImagenInmueble() {
		return imagenInmueble;
	}

	public void setImagenInmueble(String imagenInmueble) {
		this.imagenInmueble = imagenInmueble;
	}

	public RegistroDataModelImpl getListaconcepto4() {
		return listaconcepto4;
	}

	public void setListaconcepto4(RegistroDataModelImpl listaconcepto4) {
		this.listaconcepto4 = listaconcepto4;
	}

	public int getAreaInmueble() {
		return areaInmueble;
	}

	public void setAreaInmueble(int areaInmueble) {
		this.areaInmueble = areaInmueble;
	}

	public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public boolean getVarVolver() {
		return varVolver;
	}

	public void setVarVolver(boolean varVolver) {
		this.varVolver = varVolver;
	}

	public RegistroDataModelImpl getListaNit() {
		return listaNit;
	}

	public void setListaNit(RegistroDataModelImpl listaNit) {
		this.listaNit = listaNit;
	}

	
}
