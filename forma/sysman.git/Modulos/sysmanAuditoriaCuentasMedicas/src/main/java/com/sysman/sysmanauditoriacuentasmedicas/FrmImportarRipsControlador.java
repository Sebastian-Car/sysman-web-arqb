/*-
 * FrmImportarRipsControlador.java
 *
 * 1.0
 * 
 * 13/11/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import com.sysman.auditoriacuentasmedicas.ejb.EjbAuditoriaCuentasMedicasCeroLocal;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmImportarRipsControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.ResultadoValidacionFactura;
import com.sysman.util.rest.RipsPrincipal;
import com.sysman.util.rest.RipsServicio;

import net.sf.jasperreports.engine.JRException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**PARA TENER EN CUENTA: 
 * El archivo frmImportarRips.xhtml asociado a este controlador debe modificarse manualmente
 * */

/**
 * Formulario que permite importar los archivos rips a sus respectivas
 * tablas
 *
 * @version 1.0, 13/11/2019
 * @author jpulido
 */
@ManagedBean
@ViewScoped
public class FrmImportarRipsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Este atributo se usa para almacenar el nit de la entidad
     * prestadora del servicio
     */
    private String nit;
    
    /**
     * Este atributo se usa para almacenar el consecutivo unico del
     * paquete
     */
    private long consecutivo;
    /**
     * Este atributo se usa para almacenar el numero de radicado del
     * paquete
     */
    private String radicado;
    /**
     * Este atributo se usa para almacenar la fecha de radicado del
     * paquete
     */
    private String fecha;
    /**
     * Este atributo se usa para almacenar el nombre del tercero
     * prestador de servicio
     */
    private String nombreTercero;
    /**
     * Este atributo se usa como auxiliar del componente referencia de
     * archivos Rips y funciona como contenedor del archivo que se
     * desea cargar
     */
    private UploadedFile archivoCargaRips;

    /**
     * Este atributo se usa para almacenar la lista de archivos rips
     * cargados
     */
    private Map<String, Object> archivosCargados;

    /**
     * Este atributo se usa para almacenar el nombre del usuario que
     * manipula el sistema
     */
    private final String usuario;

    /**
     * Este atributo se usa para almacenar un estado de cargue fallido
     * de archivos rips
     */
    private boolean cargaFallida;

    /**
     * Este atributo se usa para almacenar el log de actividades de un
     * paquete
     */
    private String archivoSalida;

    /**
     * Este atributo se usa para almacenar un archivo a descargar con
     * la informacion d elos archivos rips cargados
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Este atributo se usa para almacenar la lista de terceros
     */
    private RegistroDataModelImpl listaTercero;
    
    private RegistroDataModelImpl listaCBEstadoCuenta;
    
	private RegistroDataModelImpl listaCBClaseCuenta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>

    // </DECLARAR_ADICIONALES>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete <code>PCK_SYSMAN_UTL</code>.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAuditoriaCuentasMedicasCeroLocal ejbAuditoriaCuentasMedicasCero;
	private RipsPrincipal ripsData;
	private String tipoNota;
	private String numNota;
	private String numIdentificacionDetectada;
	private String nitTercero;
	
	public boolean bloqueaNuevo = false;
	public boolean bloqueaCargar = false;
	public boolean bloqueaD = false;
	public boolean bloqueaR = false;
	public String numFacturaDetectada = "";
	public String clobUsuarios = "";
	public String clobConsultas = "";
	public String clobProcedimientos = "";
	public String clobMedicamentos = "";
	public String clobUrgencias = "";
	public String clobRecienNacidos = "";
	public String clobOtrosServicios = "";
	public String clobHospitalizacion = "";
	public String clobValFactura = "";
	public String clobValFacturaDetalle = "";
	public StringBuilder clobTransaccion = new StringBuilder();
	public String tipo = "";
	public int totalArchivos = 0;
	private String numFactura;
	private String claseCuenta;

    /**
     * Crea una nueva instancia de FrmImportarRipsControlador
     */
    public FrmImportarRipsControlador() {
        super();
        usuario = SessionUtil.getUser().getCodigo();
        compania = SessionUtil.getCompania();
        archivosCargados = new HashMap<>();
        try {
            // 2123
            numFormulario = GeneralCodigoFormaEnum.FRM_IMPORTAR_RIPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTercero();
        
        cargarlistaCBEstadoCuenta();
        
        cargarlistaCBClaseCuenta();

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
        enumBase = GenericUrlEnum.CM_IMPORTAR_RIPS;
        buscarLlave();

        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarRipsControladorUrlEnum.URL4391
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }
    
    public void cargarlistaCBEstadoCuenta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarRipsControladorUrlEnum.URL4394
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCBEstadoCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }
    
    public void cargarlistaCBClaseCuenta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarRipsControladorUrlEnum.URL4395
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCBClaseCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarMensajes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarRipsFev() {
    	archivosCargados.clear();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.NIT.getName(),
                        SysmanFunciones.nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NIT.getName()), "")
                                        .toString());
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        SysmanFunciones.nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()),
                                        "")
                                        .toString());
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        SysmanFunciones.nvl(
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.NOMBRE
                                                                        .getName()),
                                        "").toString());
    }
    /**
     * @param event
     */
    public void seleccionarFilaCBClaseCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CLASECUENTA",
                SysmanFunciones.nvl(registroAux.getCampos().get(
                        GeneralParameterEnum.CODIGO.getName()), "")
                        .toString());
        registro.getCampos().put("NOMBRE_CLASECUENTA",
                SysmanFunciones.nvl(
                                registroAux.getCampos().get(
                                                GeneralParameterEnum.NOMBRE
                                                                .getName()),
                                "").toString());
    }

    /**
     * @param event
     */ 
    public void seleccionarFilaCBEstadoCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ESTADOCUENTA",
                        SysmanFunciones.nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()), "")
                                        .toString());
        registro.getCampos().put("NOMBRE_ESTADOCUENTA",
                SysmanFunciones.nvl(
                                registroAux.getCampos().get(
                                                GeneralParameterEnum.NOMBRE
                                                                .getName()),
                                "").toString());
    }


    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
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
        registro = new Registro();
        archivosCargados = new HashMap<>();

        if ((rid != null) && !rid.isEmpty()) {
            cargarRegistro(rid, ACCION_MODIFICAR);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        archivosCargados = new HashMap<>();

        String criterio = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "''");

        if (registro.getCampos()
                        .get(GeneralParameterEnum.MENSAJES.getName()) == null)
            registro.getCampos().put(GeneralParameterEnum.MENSAJES.getName(),
                            "");
        
        if(registro.getCampos().get("ESTADO") != null){
    	bloqueaNuevo = (accion != null && accion.equals(ACCION_VER)) || registro.getCampos().get("ESTADO").equals("F");
    	bloqueaCargar = registro.getCampos().get("ESTADO").equals("T") || (registro.getCampos().get("ESTADO").equals("C") && registro.getCampos().get("RIPS_JSON").equals(true));
    	bloqueaD = registro.getCampos().get("ESTADO").equals("T");
        bloqueaR = registro.getCampos().get("ESTADO").equals("T") || (registro.getCampos().get("ESTADO").equals("C") && registro.getCampos().get("RIPS_JSON").equals(true));
        }else {
        	bloqueaR = accion.equals(ACCION_INSERTAR)?false:true;
        	bloqueaCargar = accion.equals(ACCION_INSERTAR);
        }
        
        if (css == null) {
            try {
                registro.getCampos().put(
                                GeneralParameterEnum.CONSECUTIVO.getName(),
                                ejbSysmanUtil.generarSiguienteConsecutivo(
                                                enumBase.getTable(), criterio,
                                                GeneralParameterEnum.CONSECUTIVO
                                                                .getName()));
                registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                                new Date());
                if (accion != null && accion.equals(ACCION_INSERTAR)) {
                    registro.getCampos().put("RIPS_JSON",true);
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true Si se permite insertar
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), "P");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBRE_ESTADO");
        registro.getCampos().remove("NOMBRE_ESTADOCUENTA");
        registro.getCampos().remove("NOMBRE_CLASECUENTA");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true Si inserto correctamente
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return true Si permite actualizar
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
    	if(registro.getCampos().get("ESTADO").equals("T")) {
    		JsfUtil.agregarMensajeAlerta("El registro no puede ser modificado en estado Causado");
    		return false;
    	}
    	
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBRE_ESTADO");
        registro.getCampos().remove("NOMBRE_ESTADOCUENTA");
        registro.getCampos().remove("NOMBRE_CLASECUENTA");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return true Si actualizo
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>kl
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
    	 if(registro.getCampos().get("ESTADO").equals("CAUSADO")) {
         	JsfUtil.agregarMensajeAlerta("Registro no puede ser eliminado en estado Causado");
         	return false;
         }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
	@Override
	public boolean eliminarDespues() {
		try {
			int consecutivo_Rips = Integer
					.parseInt(registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString());

			ejbAuditoriaCuentasMedicasCero.eliminarRips(compania, consecutivo_Rips);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
		return true;
	}

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    /**
     * Se ejecuta al momento de precionar el boton Procesar Rips y
     * realiza la insercion de la informacion de cada archivo rip en
     * la abse de datos
     */
	public void oprimirCargar() {

	    archivoDescarga = null;
	    archivoSalida = "";
	    cargaFallida = false;

	    InputStream ct = (InputStream) buscarArchivo("CT", "valor");

	    // Se limpia el campo mensaje para no dejar historial de errores
	    registro.getCampos().put(
	            GeneralParameterEnum.MENSAJES.getName(), " ");

	    if (ct != null) {

	        int num = 0;
	        StringBuilder cadena = new StringBuilder();
	        ArrayList<String> archivos = new ArrayList<>();

	        BufferedReader reader = new BufferedReader(
	                new InputStreamReader(ct));

	        cadena.append("TO_CLOB('");

	        try {

	            String linea;

	            while ((linea = reader.readLine()) != null) {

	                // Ignorar líneas vacías
	                if (linea.trim().isEmpty()) {
	                    continue;
	                }

	                linea += ",";

	                String[] datos = linea.split(",");

	                // Validar que la línea tenga las columnas necesarias
	                if (datos.length < 4) {
	                    logger.warn("Línea inválida en archivo CT: " + linea);
	                    continue;
	                }

	                archivos.add(SysmanFunciones.concatenar(
	                        datos[2],
	                        ",",
	                        datos[3]));

	                String registroCadena = linea.replace(",",
	                        SysmanConstantes.SEPARADOR_COL)
	                        + SysmanConstantes.SEPARADOR_REG;

	                if (num + registroCadena.length() >= 10000) {
	                    cadena.append("') || TO_CLOB('");
	                    num = 0;
	                }

	                cadena.append(registroCadena);
	                num += registroCadena.length();
	            }

	            cadena.append("')");

	            int consecutivo = Integer.parseInt(
	                    registro.getCampos()
	                            .get(GeneralParameterEnum.CONSECUTIVO.getName())
	                            .toString());

	            ejbAuditoriaCuentasMedicasCero.eliminarRips(
	                    compania,
	                    consecutivo);

	            ejbAuditoriaCuentasMedicasCero.cargarRips(
	                    compania,
	                    consecutivo,
	                    cadena.toString(),
	                    "CT",
	                    usuario);

	            asignarMensajesEstado(
	                    "Cargado archivo de control "
	                    + buscarArchivo("CT", "llave"),
	                    "C");

	            if (validarNitTercero(consecutivo)) {

	                for (String archivoInfo : archivos) {

	                    String tipoArchivo = archivoInfo.substring(0, 2);

	                    if ("AF".equals(tipoArchivo)) {
	                        continue;
	                    }

	                    String nombreArchivo = archivoInfo.split(",")[0];

	                    InputStream archivo = (InputStream) buscarArchivo(
	                            tipoArchivo,
	                            "valor");

	                    if (archivo != null) {

	                        int lineas = guardarArchivo(
	                                new BufferedReader(
	                                        new InputStreamReader(archivo)),
	                                tipoArchivo);

	                        archivoSalida = SysmanFunciones.concatenar(
	                                archivoSalida,
	                                nombreArchivo,
	                                ",",
	                                Integer.toString(lineas),
	                                "\n");

	                        asignarMensajesEstado(
	                                "Archivo " + nombreArchivo
	                                        + " Cargado exitosamente",
	                                "C");

	                        JsfUtil.agregarMensajeInformativo(
	                                "Archivo " + nombreArchivo
	                                        + " Cargado exitosamente");

	                    } else {

	                        JsfUtil.agregarMensajeAlerta(
	                                nombreArchivo + " no fue cargado.");

	                        asignarMensajesEstado(
	                                nombreArchivo + " no fue cargado.",
	                                "F");
	                    }
	                }
	            }

	            archivosCargados = new HashMap<>();

	        } catch (IOException | SystemException e) {

	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());

	            asignarMensajesEstado(
	                    e.getMessage().split("@#FIN#@")[0],
	                    "F");

	            archivosCargados = new HashMap<>();
	        }

	    } else {

	        JsfUtil.agregarMensajeAlerta(
	                "No se encuentra cargado el archivo de control.");

	        asignarMensajesEstado(
	                "No se encuentra cargado el archivo de control.",
	                "P");
	    }
	}
        
    
    public enum TipoArchivo {
		RIPS, CUV
	}

	private TipoArchivo detectarTipoJson(JsonNode rootNode) {

	    if (rootNode == null || !rootNode.isObject()) {
	        throw new IllegalArgumentException("JSON inválido o vacío");
	    }

	    // ---- RIPS ----
	    if (rootNode.has("numDocumentoIdObligado") 
	        && rootNode.has("numFactura")) {
	        return TipoArchivo.RIPS;
	    }

	    // ---- CUV ----
	    if (rootNode.has("CodigoUnicoValidacion") 
	        && rootNode.has("NumFactura")) {
	        return TipoArchivo.CUV;
	    }

	    throw new IllegalArgumentException(
	        "Estructura JSON no corresponde a RIPS ni CUV"
	    );
	}


	public void oprimirCargarJson() {

		 clobUsuarios = "";
		 clobConsultas = "";
		 clobProcedimientos = "";
		 clobMedicamentos = "";
		 clobUrgencias = "";
		 clobRecienNacidos = "";
		 clobOtrosServicios = "";
		 clobHospitalizacion = "";
		 clobValFactura = "";
		 clobValFacturaDetalle = "";
		 clobTransaccion = new StringBuilder();
				
		archivoDescarga = null;

		try {
			int consecutivo = Integer
					.parseInt(registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString());

			ejbAuditoriaCuentasMedicasCero.eliminarRips(compania, consecutivo);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
		
		archivoSalida = "";
		cargaFallida = false;
		int totalArchivos = 0;
		   
		    
		/* Se limpia el campo mensaje para no dejar historial de errores al cargar nuevamente los archivos
		 * 7715077 - Mperez
		 */
		registro.getCampos().put(GeneralParameterEnum.MENSAJES.getName(), " "); 
		Map<String, Map<String, byte[]>> gruposPorFactura = new HashMap<>();
		
		RipsServicio ripsService = new RipsServicio();

		for (Map.Entry<String, Object> cantArchivos : archivosCargados.entrySet()) {
		    String nombreDocumento = cantArchivos.getKey();
		    InputStream temp = (InputStream) cantArchivos.getValue();
		    if(temp != null) {
				// Usar ByteArrayOutputStream para leer todo el contenido del InputStream en un array de bytes.          	
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				byte[] data = new byte[1024];// Definir un tamaño de búfer para la lectura.
				int nRead;
				try {
					// Leer datos en fragmentos hasta el final del flujo.
					while ((nRead = temp.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}

					buffer.flush();// Asegurarse de que todos los datos almacenados en el búfer se escriban.
					byte[] contenidoArchivo = buffer.toByteArray();

					if (nombreDocumento != null
							&& (nombreDocumento.endsWith(".json") || nombreDocumento.endsWith(".JSON"))) {
						/*
						 * Para extraer informacion de un Json se crearon varios pojos que almacenan las
						 * pripiedades puntuales de cada segmento en el JSON estos se llamaron en la
						 * clase RipsServicio donde se hicieron validaciones de formato y longitud de
						 * los valores Tambien se crearon los metodos para crear los CLOB que serian
						 * enviados al procedimiento en la BD
						 */
						/*CC4368 - MPEREZ Ajuste para validar que el contenido del archivo si tenga formato JSON*/
						String contenido =
						        new String(contenidoArchivo, StandardCharsets.UTF_8).trim();

						if (!contenido.startsWith("{")) {
						    throw new Exception(
						        "Archivo CUV inválido: " + nombreDocumento +
						        ". El contenido del archivo no tiene formato JSON.  "
						    );
						}
						try {

							ObjectMapper mapper = new ObjectMapper();
							InputStream stream = new ByteArrayInputStream(contenidoArchivo);
							JsonNode rootNode = mapper.readTree(stream);

							TipoArchivo tipoArchivo = detectarTipoJson(rootNode);

							if (tipoArchivo == null) {
								cargaFallida = true;
								JsfUtil.agregarMensajeError(
										"No se pudo determinar el tipo del archivo JSON: " + nombreDocumento);
								continue;
							}


							switch (tipoArchivo) {

							case RIPS:

								try (InputStream streamRips = new ByteArrayInputStream(contenidoArchivo)) {
									tipo = "RIPS";

									ripsData = ripsService.parseRipsJsonStream(streamRips);
									procesarRips(ripsData, nombreDocumento, ripsService);
								}

								break;

							case CUV:

								try (InputStream streamCuv = new ByteArrayInputStream(contenidoArchivo)) {
									tipo = "CUV";

									ResultadoValidacionFactura fev = ripsService.parseFEVJsonStream(streamCuv);
									procesarCUV(fev, nombreDocumento, ripsService);
								}

								break;
							}
	
						} catch (Exception e) {

							cargaFallida = true;

							registro.getCampos().put(GeneralParameterEnum.MENSAJES.getName(),
									"Error procesando archivo JSON: " + e.getMessage());

							e.printStackTrace();
							archivosCargados = new HashMap<>();
						}
						}else if(nombreDocumento.endsWith(".xml")|| nombreDocumento.endsWith(".XML")) {
							
							/*Este bloque procesa archivos XML, extrayendo datos clave mediante la navegación de sus nodos. 
							* Primero, el contenido XML se carga como texto y se parsea en un objeto Document. 
							* Luego, se buscan elementos específicos (ej., <AttachedDocument>) y se extraen sus valores. 
							* Si un nodo contiene CDATA con XML anidado, este se parsea independientemente para obtener más detalles. 
							* Finalmente, los datos extraídos (incluyendo campos como IDs, fechas y valores monetarios) 
							* se concatenan en una cadena única, facilitando la integración con otros sistemas o bases de datos, incluso si el esquema XML varía levemente.
							*/
							// Convertir el contenido del array de bytes a una cadena usando codificación UTF-8.
							String texto = new String(contenidoArchivo, StandardCharsets.UTF_8);
							
							String numFacturaXml = extraerNumeroFacturaDesdeXML(texto);
							if (numFacturaXml != null && !numFacturaXml.isEmpty()) {
								numFacturaDetectada = numFacturaXml;
								tipo = "XML";
							}
							totalArchivos +=1;
		
							// Inicializar DocumentBuilderFactory y DocumentBuilder de XML para analizar XML.
							// setNamespaceAware(true) es importante para manejar correctamente los espacios de nombres XML.
							DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
							docFactory.setNamespaceAware(true);
							DocumentBuilder docBuilder;
							try {
								docBuilder = docFactory.newDocumentBuilder();
								// Usar InputSource con StringReader para analizar la cadena XML.
								InputSource is = new InputSource();
								is.setCharacterStream(new StringReader(texto));
		
								Document doc = docBuilder.parse(is);
								// Encontrar elementos por nombre de etiqueta.
								NodeList externalRefs = doc.getElementsByTagName("AttachedDocument");
		
							
								for (int i = 0; i < externalRefs.getLength(); i++) {
									Element extRef = (Element) externalRefs.item(i);
									// Acceder a elementos anidados usando espacios de nombres.
									NodeList descList = extRef.getElementsByTagNameNS(
											"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "Description");
									System.out.println("--- DATOS EXTRAIDOS ---");
		
									// ParentDocumentID
									
									System.out.println("ParentDocumentID (AttachedDocument): " + obtenerValor(doc.getDocumentElement(), "cbc", "ParentDocumentID"));
									clobTransaccion.append(obtenerValor(doc.getDocumentElement(), "cbc", "ParentDocumentID")).append(SysmanConstantes.SEPARADOR_COL);
									// IssueDate
									System.out.println("IssueDate (AttachedDocument): " + obtenerValor(doc.getDocumentElement(), "cbc", "IssueDate"));
									clobTransaccion.append(obtenerValor(doc.getDocumentElement(), "cbc", "IssueDate")).append(SysmanConstantes.SEPARADOR_COL);
		
		
									if (descList.getLength() > 0) {
											String cdataContent = descList.item(0).getTextContent().trim();
											// Parsear el contenido CDATA como un documento XML separado (innerDoc).
										Document innerDoc = docBuilder.parse(new InputSource(new StringReader(cdataContent)));
		
								
									// Extraer StartDate y EndDate de 'InvoicePeriod' dentro del documento interno.
										NodeList invoicePeriodList = innerDoc.getElementsByTagNameNS(
												"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "InvoicePeriod");
										if (invoicePeriodList.getLength() > 0) {
											Element invoicePeriod = (Element) invoicePeriodList.item(0);
											System.out.println("StartDate: " + obtenerValor(invoicePeriod, "cbc", "StartDate"));
											clobTransaccion.append(obtenerValor(invoicePeriod, "cbc", "StartDate")).append(SysmanConstantes.SEPARADOR_COL);
											System.out.println("EndDate: " + obtenerValor(invoicePeriod, "cbc", "EndDate"));
											clobTransaccion.append(obtenerValor(invoicePeriod, "cbc", "EndDate")).append(SysmanConstantes.SEPARADOR_COL);
										}
		
									// Extraer información del SenderParty: CompanyID, RegistrationName y tipo de documento.
										NodeList senderList = doc.getElementsByTagNameNS(
												"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "SenderParty");
		
										if (senderList.getLength() > 0) {
											Element senderParty = (Element) senderList.item(0);
											NodeList taxSchemeList = senderParty.getElementsByTagNameNS(
													"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "PartyTaxScheme");
											
											NodeList regNameList = senderParty.getElementsByTagNameNS(
												"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "RegistrationName");
		
											NodeList companyIdList = senderParty.getElementsByTagNameNS(
													"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "CompanyID");
											
											
											if (companyIdList.getLength() > 0) {
												Element companyIdElement = (Element) companyIdList.item(0);
												String schemeName = companyIdElement.getAttribute("schemeName");
												String company_ID = companyIdList.item(0).getTextContent().trim();
												String tipoDocumento = obtenerTipoDocumentoDesdeSchemeName(schemeName);
												System.out.println("Tipo de Documento: " + tipoDocumento);
												clobTransaccion.append(company_ID).append(SysmanConstantes.SEPARADOR_COL);
												numIdentificacionDetectada = company_ID;
												
												if (taxSchemeList.getLength() > 0) {
													if (regNameList.getLength() > 0) {
														String registrationName = regNameList.item(0).getTextContent().trim();
														System.out.println("Sender RegistrationName (PartyTaxScheme): " + registrationName);
														clobTransaccion.append(registrationName).append(SysmanConstantes.SEPARADOR_COL);
													}
												}
												clobTransaccion.append(tipoDocumento).append(SysmanConstantes.SEPARADOR_COL);
		
											}
										}	
											
										// ReceiverParty -> CompanyID y RegistrationName
										NodeList receiverList = doc.getElementsByTagNameNS(
												"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "ReceiverParty");
										if (receiverList.getLength() > 0) {
											Element receiver = (Element) receiverList.item(0);
											System.out.println("ReceiverParty RegistrationName: " + obtenerValor(receiver, "cbc", "RegistrationName"));
											clobTransaccion.append(obtenerValor(receiver, "cbc", "RegistrationName")).append(SysmanConstantes.SEPARADOR_COL);
										}
		
										// LegalMonetaryTotal -> PayableAmount
										NodeList monetaryList = innerDoc.getElementsByTagNameNS(
												"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "LegalMonetaryTotal");
										if (monetaryList.getLength() > 0) {
											Element legalMonetary = (Element) monetaryList.item(0);
											System.out.println("PayableAmount: " + obtenerValor(legalMonetary, "cbc", "PayableAmount"));
											clobTransaccion.append(obtenerValor(legalMonetary, "cbc", "PayableAmount")).append(SysmanConstantes.SEPARADOR_COL);
										}
											
										// Buscar AdditionalInformation dentro de <CustomTagGeneral> 
										//NodeList additionalList = innerDoc.getElementsByTagName("AdditionalInformation");
										NodeList additionalList = innerDoc.getElementsByTagNameNS("*", "AdditionalInformation");;
										for (int j = 0; j < additionalList.getLength(); j++) {
											Element ai = (Element) additionalList.item(j);
											String name = obtenerValor(ai, null, "Name");
											String value = obtenerValor(ai, null, "Value");
											if (name != null && (
													name.equals("CODIGO_PRESTADOR") ||
													name.equals("NUMERO_CONTRATO") ||
													name.equals("COBERTURA_PLAN_BENEFICIOS") ||
													name.equals("NUMERO_POLIZA")
											)) {
												System.out.println(name + ": " + value);
												clobTransaccion.append(value).append(SysmanConstantes.SEPARADOR_COL);
											}
										}
										
										doc.getDocumentElement().normalize();// Normalizar el documento para eliminar nodos de texto vacíos.
										// Obtener el nodo <cbc:Description>
										NodeList descriptionList2 = doc.getElementsByTagName("cbc:Description");
										if (descriptionList2.getLength() > 1) {
											// Acceder al segundo <cbc:Description>
											Element secondDescription = (Element) descriptionList2.item(1);
											// Obtener el contenido de CDATA
											String dataContentCUFE = secondDescription.getTextContent();
											
											// Parsear el contenido CDATA como XML
											DocumentBuilderFactory innerDbFactory = DocumentBuilderFactory.newInstance();
											DocumentBuilder innerDBuilder = innerDbFactory.newDocumentBuilder();
											//Document innerDocCUFE = innerDBuilder.parse(new ByteArrayInputStream(dataContentCUFE.getBytes("UTF-8")));
											String xmlLimpio = limpiarXml(dataContentCUFE);

											Document innerDocCUFE = innerDBuilder.parse(new ByteArrayInputStream(xmlLimpio.getBytes(StandardCharsets.UTF_8)));

											innerDocCUFE.getDocumentElement().normalize();
											// Obtener el campo <cbc:UUID>
											NodeList documentResponseList = innerDocCUFE.getElementsByTagName("cac:DocumentResponse");
											if (documentResponseList.getLength() > 0) {
												Element documentResponse = (Element) documentResponseList.item(0);
												
												// Obtener el nodo <cac:DocumentReference>
												NodeList documentReferenceList = documentResponse.getElementsByTagName("cac:DocumentReference");
												if (documentReferenceList.getLength() > 0) {
													Element documentReference = (Element) documentReferenceList.item(0);
													
													// Obtener el campo <cbc:UUID>
													NodeList uuidList = documentReference.getElementsByTagName("cbc:UUID");
													if (uuidList.getLength() > 0) {
														String uuidValue = uuidList.item(0).getTextContent();
														System.out.println("El valor de <cbc:UUID> es: " + uuidValue);
														clobTransaccion.append(uuidValue).append(SysmanConstantes.SEPARADOR_COL);
													} 
												} 
											}
										}
									

									clobTransaccion.append(SysmanFunciones.nvlStr(tipoNota, ""))
											.append(SysmanConstantes.SEPARADOR_COL);
									clobTransaccion.append(SysmanFunciones.nvlStr(numNota, ""))
											.append(SysmanConstantes.SEPARADOR_COL).append("")
											.append(numFactura)
											.append(SysmanConstantes.SEPARADOR_COL)
											.append(SysmanConstantes.SEPARADOR_REG);
									System.out.println("Extracción completada.");
									System.out.println(clobTransaccion);
								}
							
						    }
						} catch (ParserConfigurationException | SAXException e) {
							e.printStackTrace();
							archivosCargados.clear();

						}
						asignarMensajesEstado("Archivo " + nombreDocumento + " Cargado exitosamente", "C");
					}

					if (numFacturaDetectada == null || numFacturaDetectada.isEmpty()) {
						cargaFallida = true;
						JsfUtil.agregarMensajeError(
								"No se pudo identificar el número de factura en el archivo " + nombreDocumento);
						continue;
					} else {
						gruposPorFactura.putIfAbsent(numFacturaDetectada, new HashMap<String, byte[]>());
						Map<String, byte[]> datosFactura = gruposPorFactura.get(numFacturaDetectada);

					    // Guardamos el contenido del archivo (igual que antes)
					    datosFactura.put(tipo + "_BYTES", contenidoArchivo);
	   
					    // Guardamos también el NIT y el número de factura como texto (convertido a bytes)
					    if ("RIPS".equals(tipo) || "XML".equals(tipo) || "CUV".equals(tipo)) {
					        if (numFacturaDetectada != null && !numFacturaDetectada.isEmpty()) {
					            datosFactura.put(tipo + "_NUMFACTURA", numFacturaDetectada.getBytes(StandardCharsets.UTF_8));
					        }
					        if ((!"CUV".equals(tipo)) && numIdentificacionDetectada != null && !numIdentificacionDetectada.isEmpty()) {
					            datosFactura.put(tipo + "_NIT", numIdentificacionDetectada.getBytes(StandardCharsets.UTF_8));
					        }
					    }
					}
					
					nitTercero = SysmanFunciones.toString(
							registro.getCampos().get(GeneralParameterEnum.NIT.getName())
							).trim();
					
					
				} catch (Exception  e) {
					registro.getCampos().put(GeneralParameterEnum.MENSAJES.getName(),
							"Error procesando archivo JSON: " + e.getMessage());
						e.printStackTrace();
				} 
					
			    }else {
			        JsfUtil.agregarMensajeAlerta(
			                "No se encuentra cargado el archivo de control.");
						 asignarMensajesEstado(
			                "No se encuentra cargado el archivo de control.",
			                "P");
					}
			
			
			}
		
		boolean obligaCUV = false;

		try {
			claseCuenta = registro.getCampos().get("CLASECUENTA").toString();
			
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.CODIGO.getName(), claseCuenta);

		    Registro rsExiste = RegistroConverter.toRegistro(
		        requestManager.get(
		            UrlServiceUtil.getInstance()
		                .getUrlServiceByUrlByEnumID(
		                		FrmImportarRipsControladorUrlEnum.URL1885003.getValue())
		                .getUrl(),
		            param));

		    if (rsExiste != null) {
		        obligaCUV = (boolean) SysmanFunciones.nvl(rsExiste.getCampos().get("OBLIGA_CUV"),false)?true:false;

		    } else {
		        JsfUtil.agregarMensajeError("No se encontró configuración de OBLIGA_CUV.");
		    }

		} catch (Exception e) {
		    JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		for (Map.Entry<String, Map<String, byte[]>> grupo : gruposPorFactura.entrySet()) {
			numFactura = grupo.getKey();
			Map<String, byte[]> archivosFactura = grupo.getValue();

			String numFacturaRIPLocal = archivosFactura.containsKey("RIPS_NUMFACTURA")
				    ? new String(archivosFactura.get("RIPS_NUMFACTURA"), StandardCharsets.UTF_8)
				    : "";

			String numFacturaFEVLocal = archivosFactura.containsKey("XML_NUMFACTURA")
			    ? new String(archivosFactura.get("XML_NUMFACTURA"), StandardCharsets.UTF_8)
			    : "";
			
			String numFacturaCUVLocal = archivosFactura.containsKey("CUV_NUMFACTURA")
					? new String(archivosFactura.get("CUV_NUMFACTURA"), StandardCharsets.UTF_8)
							: "";

			String numIdentificacionRIPSLocal = archivosFactura.containsKey("RIPS_NIT")
			    ? new String(archivosFactura.get("RIPS_NIT"), StandardCharsets.UTF_8)
			    : "";

			String numIdentificacionFEVLocal = archivosFactura.containsKey("XML_NIT")
			    ? new String(archivosFactura.get("XML_NIT"), StandardCharsets.UTF_8)
			    : "";
				
			
			boolean tieneRIPS = archivosFactura.containsKey("RIPS_BYTES");
			boolean tieneCUV = archivosFactura.containsKey("CUV_BYTES");
			boolean tieneXML = archivosFactura.containsKey("XML_BYTES");

			
			boolean facturasCoinciden = 
					numFacturaRIPLocal.equals(numFacturaCUVLocal)
					&& numFacturaCUVLocal.equals(numFacturaFEVLocal);

			boolean identificacionCoincide =
					numIdentificacionRIPSLocal.equals(numIdentificacionFEVLocal)
				&& numIdentificacionFEVLocal.equals(nitTercero);
				
				
//			if (tieneRIPS && tieneCUV && tieneXML) {
//				
//			}else {
//						cargaFallida = true;
//						
//						StringBuilder faltantes = new StringBuilder("La factura ")
//									.append(numFactura)
//									.append(" no tiene todos los archivos requeridos. Faltan: ");
//
//							if (!tieneRIPS) faltantes.append("RIPS ");
//							if (!tieneCUV) faltantes.append("CUV ");
//							if (!tieneXML) faltantes.append("XML ");
//
//							String mensaje = faltantes.toString().trim();
//
//							asignarMensajesEstado(mensaje, "F");
//							
//							JsfUtil.agregarMensajeAlerta(mensaje);
//
//							System.out.println("Factura incompleta: " + mensaje);
//			}
			
			// RIPS y XML son obligatorios
			if (!tieneRIPS || !tieneXML || (obligaCUV && !tieneCUV)) {
				
			    cargaFallida = true;

			    StringBuilder faltantes = new StringBuilder("La factura ")
			    	    .append(numFactura)
			    	    .append(" no tiene los archivos obligatorios. Faltan: ");

			    	if (!tieneRIPS) faltantes.append("RIPS ");
			    	if (!tieneXML)  faltantes.append("XML ");
			    	if (obligaCUV && !tieneCUV) faltantes.append("CUV ");

			    String mensaje = faltantes.toString().trim();

			    asignarMensajesEstado(mensaje, "F");
			    JsfUtil.agregarMensajeError(mensaje);

			} else {
				if (!obligaCUV && !tieneCUV) {
				    JsfUtil.agregarMensajeAlerta(
				        "Archivo CUV no identificado para la factura " + numFactura
				    );
				}

			    // Si existe CUV, se valida coherencia
			    if (tieneCUV) {

			        facturasCoinciden =
			            numFacturaRIPLocal.equals(numFacturaCUVLocal)
			            && numFacturaCUVLocal.equals(numFacturaFEVLocal);

			        if (!facturasCoinciden) {
			            cargaFallida = true;
			            JsfUtil.agregarMensajeError(
			                "El número de factura " + numFactura +
			                " no coincide entre RIPS, XML y CUV");
			            asignarMensajesEstado(
			                "El número de factura " + numFactura +
			                " no coincide entre RIPS, XML y CUV", "F");
			            continue;
			        }
			    }

			    // Validación de NIT (RIPS vs XML)
			    if (!numIdentificacionRIPSLocal.equals(numIdentificacionFEVLocal)
			        || !numIdentificacionFEVLocal.equals(nitTercero)) {

			        cargaFallida = true;
			        JsfUtil.agregarMensajeError(
			            "El NIT del tercero no coincide entre RIPS y XML para la factura "
			            + numFactura);
			        asignarMensajesEstado(
			            "El NIT del tercero no coincide entre RIPS y XML para la factura "
			            + numFactura, "F");

			    } else {
			        asignarMensajesEstado(
			            "Archivos validados correctamente para la factura " + numFactura, "C");
			    }
			}
		}
		
		
		String clobArchivos = generarClobArchivoControl(archivosCargados);
		
		int consecutivo = Integer.parseInt(registro.getCampos()
		        .get(GeneralParameterEnum.CONSECUTIVO
		                        .getName())
		        .toString()); 
		
		try {
			if(!cargaFallida) {
				if(archivosCargados==null || archivosCargados.isEmpty()) {
					JsfUtil.agregarMensajeAlerta(
							"No se encuentra cargado el archivo de control.");
					asignarMensajesEstado(
							"No se encuentra cargado el archivo de control.",
							"P");
				}else {
					if(ripsData == null) {
						JsfUtil.agregarMensajeError("No se encontró información RIPS válida para procesar.");
						return;
					} else {
						ejbAuditoriaCuentasMedicasCero.cargarRipsJson(compania,
								consecutivo, 
								clobUsuarios,
								clobConsultas,
								clobProcedimientos,
								clobMedicamentos,
								clobUrgencias,
								clobRecienNacidos,
								clobOtrosServicios,
								clobHospitalizacion,
								clobValFactura,
								clobValFacturaDetalle,
								clobArchivos,
								clobTransaccion.toString(),
								usuario);
						
						registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), "C");
						registro.getCampos().put("NOMBRE_ESTADO", "CARGADO");

						JsfUtil.agregarMensajeInformativo("Archivos cargados exitosamente");
						}
					}

				}

				archivosCargados = new HashMap<>();

			} catch (SystemException e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
				asignarMensajesEstado(e.getMessage().split("@#FIN#@")[0], "F");
				archivosCargados = new HashMap<>();
			} finally {
				bloqueaCargar = true;
				archivosCargados.clear();
			}

		}

	private void procesarCUV(ResultadoValidacionFactura fev, String nombreDocumento, RipsServicio ripsService) {
		clobValFactura = ripsService.ValidacionFacturaClob(fev);
		clobValFacturaDetalle = ripsService.ValidacionFacturaDetalleClob(fev);

		numFacturaDetectada =
		        fev.getNumFactura() != null
		        ? fev.getNumFactura()
		        : "";

		asignarMensajesEstado("Archivo " + nombreDocumento + " Cargado exitosamente", "C");

		totalArchivos++;
	}

	private void procesarRips(RipsPrincipal ripsData, String nombreDocumento, RipsServicio ripsService) {
		tipoNota = ripsData.getTipoNota();
		numNota = ripsData.getNumNota();

		numIdentificacionDetectada = ripsData.getNumDocumentoIdObligado().toString();

		clobUsuarios += ripsService.UsuariosClob(ripsData);
		clobConsultas += ripsService.ConsultasClob(ripsData);
		clobProcedimientos += ripsService.ProcedimientosClob(ripsData);
		clobMedicamentos += ripsService.MedicamentosClob(ripsData);
		clobUrgencias += ripsService.UrgenciasClob(ripsData);
		clobRecienNacidos += ripsService.RecienNacidosClob(ripsData);
		clobOtrosServicios += ripsService.OtrosServiciosClob(ripsData);
		clobHospitalizacion += ripsService.HospitalizacionClob(ripsData);

		numFacturaDetectada = ripsData.getNumFactura().toString();

		asignarMensajesEstado("Archivo " + nombreDocumento + " Cargado exitosamente", "C");

		totalArchivos++;
	}
	
    
private String limpiarXml(String xml) {
    if (xml == null) {
        return null;
    }

    xml = xml.replace("\uFEFF", "");

    // Quitar todo antes del primer <?xml
    int index = xml.indexOf("<?xml");
    if (index > 0) {
        xml = xml.substring(index);
    }

    return xml.trim();
}
    
    
	private String extraerNumeroFacturaDesdeXML(String xmlContenido) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xmlContenido)));
			return obtenerValor(doc.getDocumentElement(), "cbc", "ParentDocumentID");
		} catch (Exception e) {
			return "";
		}
	}
    	            
    	            
    public String obtenerTipoDocumentoDesdeSchemeName(String schemeName) {
    	 switch (schemeName) {
         case "11": return "RC";  // Registro Civil
         case "12": return "TI";  // Tarjeta de Identidad
         case "13": return "CC";  // Cédula de Ciudadanía
         case "21": return "TE";  // Tarjeta de Extranjería
         case "22": return "CE";  // Cédula de Extranjería
         case "31": return "NI";  // NIT
         case "41": return "PA";  // Pasaporte
         case "42": return "DE";  // Documento Extranjero
         case "43": return "SI";  // Sin Identificación
         default:    return "ND"; // No definido
     }
	}

	public void asignarValorTagNS(Element element, StringBuilder aux, String namespaceURI, String tag) {
        NodeList nodoName = element.getElementsByTagNameNS(namespaceURI, tag);
        if (nodoName != null && nodoName.getLength() > 0) {
            Element elementoName = (Element) nodoName.item(0);
            if (elementoName != null && elementoName.getFirstChild() != null) {
                aux.append(elementoName.getFirstChild().getNodeValue());
                aux.append(SysmanConstantes.SEPARADOR_COL);
            }
        }
    }
    
    public String obtenerValorTagNS(Element parent, String namespaceURI, String tagName) {
        NodeList nodes = parent.getElementsByTagNameNS(namespaceURI, tagName);
        if (nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }
    
    public static String obtenerValor(Element parent, String namespacePrefix, String tagName) {
       /* String namespaceURI;
        if ("cbc".equals(namespacePrefix)) {
            namespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
        } else if ("cac".equals(namespacePrefix)) {
            namespaceURI = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
        } else {
            namespaceURI = null;
        }*/
    	/*CC4368 - MPEREZ Ajuste para que acepte diferentes namespaces*/
        NodeList nodes = parent.getElementsByTagNameNS("*", tagName);
               // parent.getElementsByTagName(tagName) :
               // parent.getElementsByTagNameNS(namespaceURI, tagName);                

        if (nodes.getLength() > 0 && nodes.item(0).getTextContent() != null) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }

    // Sobrecarga para documento raiz
    public static String obtenerValor(Document doc, String namespacePrefix, String tagName) {
        return obtenerValor(doc.getDocumentElement(), namespacePrefix, tagName);
    }

    
    public String generarClobArchivoControl(Map<String, Object> archivosCargados) {
        StringBuilder clob = new StringBuilder();
        String SEPARADOR_COL = ",.COL.,"; 
        String SEPARADOR_REG = ",.REG.,"; 
        
        
        for (Map.Entry<String, Object> entrada : archivosCargados.entrySet()) {
            String nombreDocumento = entrada.getKey();
            String nombreArchivo = nombreDocumento;
            String tipoArchivo = "";
            String codArchivo = "";

            if (nombreDocumento.toLowerCase().endsWith(".json")) {
                tipoArchivo = "JSON";
                codArchivo = nombreDocumento.contains("CUV") ? "VAL_RIPS" : "RIPS";
            } else if (nombreDocumento.toLowerCase().endsWith(".xml")) {
                tipoArchivo = "XML";
                codArchivo = "FEV";
            } else {
                continue; 
            }

            clob.append(codArchivo).append(SEPARADOR_COL)
                .append(nombreArchivo).append(SEPARADOR_COL)
                .append(tipoArchivo).append(SEPARADOR_REG);
        }

        return clob.toString();
    }
    

    private boolean validarNitTercero(int consecutivo) {
        int num = 0;
        String linea = "";
        String lineaNit = "";
        InputStream af = (InputStream) buscarArchivo("AF", "valor");
        StringBuilder cadena = new StringBuilder();
        cadena.append("TO_CLOB('");
        try {
            if (af != null) {

                BufferedReader reader = new BufferedReader(
                                new InputStreamReader(af));

                while (reader.ready()) {

                    if (num >= 10000) {
                        cadena.append("') || TO_CLOB('");
                        num = 0;
                    }

                    linea = reader.readLine() + ",";
                    //lineaNit = reader.readLine() + ",";
                    //lineaNit = lineaNit.split(",")[3];
                    lineaNit = linea.split(",")[3];
                    if (lineaNit.contains("-")) {
                        int fin = lineaNit.indexOf("-");
                        lineaNit = lineaNit.substring(0, fin);
                    }

                    if (!lineaNit.equals(registro.getCampos()
                                    .get(GeneralParameterEnum.NIT
                                                    .getName())
                                    .toString())) {
                        JsfUtil.agregarMensajeError(
                                        "El nit " + lineaNit
                                            + " en el archivo de transacciones AF no corresponde al tercero asignado");
                        asignarMensajesEstado(
                                        "El nit " + lineaNit
                                            + " en el archivo de transacciones AF no corresponde al tercero asignado",
                                        "F");

                        ejbAuditoriaCuentasMedicasCero.eliminarRips(compania,
                                        consecutivo);
                        archivosCargados = new HashMap<>();
                        return false;
                    }

                    cadena.append(linea.replace(",",
                                    SysmanConstantes.SEPARADOR_COL)
                        + SysmanConstantes.SEPARADOR_REG);

                    num = num + cadena.length();

                }

                cadena.append("')" + "");

                ejbAuditoriaCuentasMedicasCero.cargarRips(compania, consecutivo,
                                cadena.toString(),
                                "AF", usuario);

                asignarMensajesEstado("Archivo AF Cargado exitosamente", "C");
                JsfUtil.agregarMensajeInformativo(
                                "Archivo AF Cargado exitosamente");

                return true;
            }
            else {

                JsfUtil.agregarMensajeAlerta(
                                "No se encuentra cargado el archivo de transacciones(AF).");
                asignarMensajesEstado(
                                "No se encuentra cargado el archivo de transacciones(AF).",
                                "P");

                ejbAuditoriaCuentasMedicasCero.eliminarRips(compania,
                                consecutivo);

                archivosCargados = new HashMap<>();
                return false;
            }

        }

        catch (IOException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

            return false;
        }

    }

    /**
     * Realiza la descarga del archivo CT procesado
     */
    public void oprimirarchivoCT() {

        try {
            ByteArrayInputStream streamTexto = JsfUtil
                            .serializarPlano(archivoSalida);
            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                            "CT.txt");
        }
        catch (JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    /**
     * Realiza el guardado de un archivo RIP en la base de datos
     * 
     * @param reader
     * Data del archivo plano
     * @param tipoArchivo
     * Tipo de archivo RIP
     * @return Cantidad de lineas leidas del arhivo cargado en la base
     * de datos
     * @throws SystemException
     * Excepcion en caso de error
     */
    private int guardarArchivo(BufferedReader reader, String tipoArchivo)
            throws SystemException {

        StringBuilder cadena = new StringBuilder();
        int num = 0;
        int lineas = 0;

        cadena.append("TO_CLOB('");

        try {

            String linea;

            while ((linea = reader.readLine()) != null) {

                // Ignorar líneas vacías o con espacios
                if (linea.trim().isEmpty()) {
                    continue;
                }

                linea += ",";

                String registroCadena = linea.replace(",",
                        SysmanConstantes.SEPARADOR_COL)
                        + SysmanConstantes.SEPARADOR_REG;

                if (num + registroCadena.length() >= 10000) {
                    cadena.append("') || TO_CLOB('");
                    num = 0;
                }

                cadena.append(registroCadena);

                num += registroCadena.length();
                lineas++;
            }

            if (lineas == 0) {
                return 0;
            }

            cadena.append("')");

            int consecutivo = Integer.parseInt(
                    registro.getCampos()
                            .get(GeneralParameterEnum.CONSECUTIVO.getName())
                            .toString());

            ejbAuditoriaCuentasMedicasCero.cargarRips(
                    compania,
                    consecutivo,
                    cadena.toString(),
                    tipoArchivo,
                    usuario);

        } catch (IOException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

            archivosCargados = new HashMap<>();
        }

        return lineas;
    }

    /**
     * Realiza la busqueda del nombre de un archivo por tipo de RIP en
     * la lisat de arcivos planos cargados
     * 
     * @param tipo
     * Tipo de archivo RIP
     * @param retorno
     * tipo de respeusta esperada(data del archivo o el nombre del
     * mismo)
     * @return Cadena de caracteres
     */
    public Object buscarArchivo(String tipo, String retorno) {
        for (Map.Entry<String, Object> archivo : archivosCargados.entrySet()) {
            if (tipo.equals(archivo.getKey().substring(0, 2))) {
                if (retorno.equals("valor")) {
                    return archivo.getValue();
                }

                if (retorno.equals("llave"))
                    return archivo.getKey();
            }
        }
        return null;
    }

    /**
     * Realiza la lectura de un archivo en un componente upload file
     * 
     * @param event
     * Data del archibo subido
     */
    public void cargarRips(FileUploadEvent event) {

        archivoCargaRips = event.getFile();
        String extension = "";
        extension = event.getFile().getContentType().substring(event.getFile().getContentType().indexOf("/") + 1);
        Boolean ripsFev = false;
        ripsFev = Boolean.parseBoolean(SysmanFunciones.toString(registro.getCampos().get("RIPS_JSON")));
        if (ripsFev) {
            if (!extension.equals("json") && !extension.equals("xml")) {
                JsfUtil.agregarMensajeAlerta("Para RIPS FEV, solo se permiten archivos .json o .xml.");
                asignarMensajesEstado("Para RIPS FEV, solo se permiten archivos .json o .xml.", "F");
                return; 
            }
        } else {
            if (!extension.equals("txt") && !extension.equals("plain")) {
                JsfUtil.agregarMensajeAlerta("Solo se permiten archivos .txt para este tipo de carga.");
                asignarMensajesEstado("Solo se permiten archivos .txt para este tipo de carga.", "F");
                return;
            }
        }
        if(!ripsFev) {
	        for (Map.Entry<String, Object> archivo : archivosCargados.entrySet()) {
	            if (archivoCargaRips.getFileName().substring(0, 2)
	                            .equals(archivo.getKey().substring(0, 2))) {
	                JsfUtil.agregarMensajeAlerta("El archivo de tipo " +
	                    event.getFile().getFileName().substring(0, 2)
	                    + " ya se encuentra en la lista de archivos cargados.");
	                asignarMensajesEstado("El archivo de tipo " +
	                    event.getFile().getFileName().substring(0, 2)
	                    + " ya se encuentra en la lista de archivos cargados.",
	                                "P");
	
	                return;
	            }
	        }
        }
        try {

            archivosCargados.put(event.getFile().getFileName(),
                            event.getFile().getInputstream());
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        bloqueaCargar = false;

    }

    /**
     * Realiza el guardado de un estado y/o mensaje sobre el paquete
     * RIP
     * 
     * @param mensaje
     * Mensaje a guardar en le log
     * @param estado
     * Esatdo actual del cargue de archivos
     */
    private void asignarMensajesEstado(String mensaje, String estado) {
        Registro miReg = null;
        if (estado.equals("F"))
            cargaFallida = true;

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarRipsControladorUrlEnum.URL4392
                                                        .getValue());

        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.CONSECUTIVO
                                                        .getName()));

        if (cargaFallida)
            fields.put(GeneralParameterEnum.ESTADO.getName(), "F");
        else
            fields.put(GeneralParameterEnum.ESTADO.getName(), estado);

        String mensajeAct = (String) SysmanFunciones.nvl(registro.getCampos()
                        .get(GeneralParameterEnum.MENSAJES.getName()), "");

        fields.put(GeneralParameterEnum.MENSAJES.getName(),
                        SysmanFunciones.concatenar(mensajeAct, mensaje, "\n"));
        fields.put(GeneralParameterEnum.USUARIO.getName(),
                        SessionUtil.getUser().getCodigo());

        Parameter parameter = new Parameter();
        parameter.setFields(fields);

        try {
            int actualizadas = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            if (actualizadas > 0) {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                registro.getCampos().get(
                                                GeneralParameterEnum.CONSECUTIVO
                                                                .getName()));

                miReg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmImportarRipsControladorUrlEnum.URL4393
                                                                                .getValue())
                                                .getUrl(),
                                                param));
                registro.getCampos().put("NOMBRE_ESTADO",
                                miReg.getCampos()
                                                .get(GeneralParameterEnum.ESTADO
                                                                .getName()));
                registro.getCampos().put(
                                GeneralParameterEnum.MENSAJES.getName(),
                                miReg.getCampos().get(
                                                GeneralParameterEnum.MENSAJES
                                                                .getName()));
            }

        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());

        }

    }     

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable consecutivo
     * 
     * @return consecutivo
     */
    public long getConsecutivo() {
        return consecutivo;
    }

    /**
     * @return the nit
     */
    public String getNit() {
        return nit;
    }

    /**
     * @param nit
     * the nit to set
     */
    public void setNit(String nit) {
        this.nit = nit;
    }

    /**
     * Asigna la variable consecutivo
     * 
     * @param consecutivo
     * Variable a asignar en consecutivo
     */
    public void setConsecutivo(long consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Retorna la variable radicado
     * 
     * @return radicado
     */
    public String getRadicado() {
        return radicado;
    }

    /**
     * Asigna la variable radicado
     * 
     * @param radicado
     * Variable a asignar en radicado
     */
    public void setRadicado(String radicado) {
        this.radicado = radicado;
    }

    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha
     * the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Retorna la variable nombreTercero
     * 
     * @return nombreTercero
     */
    public String getNombreTercero() {
        return nombreTercero;
    }

    /**
     * Asigna la variable nombreTercero
     * 
     * @param nombreTercero
     * Variable a asignar en nombreTercero
     */
    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    /**
     * Retorna el objeto contArchivoRips
     * 
     * @return contArchivoRips
     */
    public UploadedFile getArchivoCargaRips() {
        return archivoCargaRips;
    }

    /**
     * Asigna el objeto contArchivoRips
     * 
     * @param contArchivoRips
     * Variable a asignar en contArchivoRips
     */
    public void setArchivoCargaRips(UploadedFile archivoCargaRips) {
        this.archivoCargaRips = archivoCargaRips;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }
       
    /**
	 * @return the listaCBEstadoCuenta
	 */
	public RegistroDataModelImpl getListaCBEstadoCuenta() {
		return listaCBEstadoCuenta;
	}

	/**
	 * @param listaCBEstadoCuenta the listaCBEstadoCuenta to set
	 */
	public void setListaCBEstadoCuenta(RegistroDataModelImpl listaCBEstadoCuenta) {
		this.listaCBEstadoCuenta = listaCBEstadoCuenta;
	}

	/**
	 * @return the listaCBClaseCuenta
	 */
	public RegistroDataModelImpl getListaCBClaseCuenta() {
		return listaCBClaseCuenta;
	}

	/**
	 * @param listaCBClaseCuenta the listaCBClaseCuenta to set
	 */
	public void setListaCBClaseCuenta(RegistroDataModelImpl listaCBClaseCuenta) {
		this.listaCBClaseCuenta = listaCBClaseCuenta;
	}
    /**
     * @return the archivosCargados
     */
    public List<Map.Entry<String, Object>> getArchivosCargados() {
        Set<Map.Entry<String, Object>> archivosSalida = archivosCargados
                        .entrySet();
        return new ArrayList<>(archivosSalida);
    }

    /**
     * @param archivosCargados
     * the archivosCargados to set
     */
    public void setArchivosCargados(Map<String, Object> archivosCargados) {
        this.archivosCargados = archivosCargados;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @return the archivoSalida
     */
    public String getArchivoSalida() {
        return archivoSalida;
    }

    /**
     * @param archivoSalida
     * the archivoSalida to set
     */
    public void setArchivoSalida(String archivoSalida) {
        this.archivoSalida = archivoSalida;
    }

	/**
	 * @return the bloqueaNuevo
	 */
	public boolean isBloqueaNuevo() {
		return bloqueaNuevo;
	}

	/**
	 * @param bloqueaNuevo the bloqueaNuevo to set
	 */
	public void setBloqueaNuevo(boolean bloqueaNuevo) {
		this.bloqueaNuevo = bloqueaNuevo;
	}

	/**
	 * @return the bloqueaCargar
	 */
	public boolean isBloqueaCargar() {
		return bloqueaCargar;
	}

	/**
	 * @param bloqueaCargar the bloqueaCargar to set
	 */
	public void setBloqueaCargar(boolean bloqueaCargar) {
		this.bloqueaCargar = bloqueaCargar;
	}

	/**
	 * @return the bloqueaD
	 */
	public boolean isBloqueaD() {
		return bloqueaD;
	}

	/**
	 * @param bloqueaD the bloqueaD to set
	 */
	public void setBloqueaD(boolean bloqueaD) {
		this.bloqueaD = bloqueaD;
	}

	/**
	 * @return the bloqueaR
	 */
	public boolean isBloqueaR() {
		return bloqueaR;
	}

	/**
	 * @param bloqueaR the bloqueaR to set
	 */
	public void setBloqueaR(boolean bloqueaR) {
		this.bloqueaR = bloqueaR;
	}

    
    

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
