/*-
 * ConfigurarCodigosCuipoControlador.java
 *
 * 1.0
 * 
 * 21/06/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.cgr;

import com.ibm.icu.text.SimpleDateFormat;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.cgr.ejb.impl.EjbCGRCero;
import com.sysman.cgr.enums.ConfigurarCodigosCuipoControladorUrlEnum;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import co.com.sysman.comun.excepcion.NegocioExcepcion;
import net.sf.jasperreports.engine.JRException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * Formulario que permite configurar los codigos cuipo por comprobante
 *
 * @version 1.0, 21/06/2021
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class ConfigurarCodigosCuipoControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la
     * cual inicio sesion el usuario, el valor de esta constante es asignado en
     * el constructor a la variable de sesion correspondiente
     */
    private final String compania;

    private String anio;

    private String naturaleza;

    private String trimestre;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCodigoCPC;

    private RegistroDataModelImpl listaCodigoCPCE;

    private RegistroDataModelImpl listaFuenteCUIPO;

    private RegistroDataModelImpl listaFuenteCUIPOE;

    private RegistroDataModelImpl listaProductoCUIPO;

    private RegistroDataModelImpl listaProductoCUIPOE;
    
    private RegistroDataModelImpl listaSector;
    
    private RegistroDataModelImpl listaSectorE;
    
    private RegistroDataModelImpl listaPrograma;
    
    private RegistroDataModelImpl listaProgramaE;
    
    private RegistroDataModelImpl listaSubprograma;
    
    private RegistroDataModelImpl listaSubprogramaE;
    
    private RegistroDataModelImpl listaCodigoProducto;
    
    private RegistroDataModelImpl listaCodigoProductoE;
    
    private RegistroDataModelImpl listaCodigoBpin;
    
    private RegistroDataModelImpl listaCodigoBpinE;
    
    private RegistroDataModelImpl listaCodigoccpet;
    
    private RegistroDataModelImpl listaCodigoccpetE;
    
    private RegistroDataModelImpl listaCodigoCpcDane;
    
    private RegistroDataModelImpl listaCodigoCpcDaneE;
    
    private RegistroDataModelImpl listaCodigoUnidEjecutora;
    
    private RegistroDataModelImpl listaCodigoUnidEjecutoraE;
    
    private RegistroDataModelImpl listaCodigoFuente;
    
    private RegistroDataModelImpl listaCodigoFuenteE;
    
    private RegistroDataModelImpl listaCodigoCcpetRegalias;
    
    private RegistroDataModelImpl listaCodigoCcpetRegaliasE;
    
    private RegistroDataModelImpl listaPoliticaPublica;
    
    private RegistroDataModelImpl listaPoliticaPublicaE;
    
    private RegistroDataModelImpl listaDetalleSectorial;
    
    private RegistroDataModelImpl listaDetalleSectorialE;
    
    private List<Registro> listaTipoClasificador;
    
    private List<Registro> listaTipoCuentaRegalias;
    
    private RegistroDataModelImpl listaRecursoSGR;
    
    private RegistroDataModelImpl listaRecursoSGRE;
    
	/**
     * Esta variable se usa como auxiliar para subformularios y en esta se
     * alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    private int modeloA;
    
    private boolean bloqSector;
    
    private boolean bloqPrograma;
    
    private boolean bloqSubPrograma;
    
    private boolean bloqCodProducto;
    
    private boolean bloqCodBpin;
    
    private boolean bloqCodCcpet;
    
    private boolean bloqCodCpcDane;
    
    private boolean bloqUnidadEje;
    
    private boolean bloqCodFuente;
    
    private boolean bloqCodCcpetRega;
    
    private boolean bloqPoliticaP;
    
    private boolean bloqDetalleS;
    
    private boolean bloqCpc;
    
    private boolean bloqRecursoSGR;
    
    private int indice;
    
    private int aplicacion;
	
	private String vigencia;
	
	private int regalias;
	
	private String sector;
	
	private String programa;
	
	private String subPrograma;
	
	private String codProducto;
	
	private String codBpin;
	
	private String codCcpet;
	
	private String codCpcDane;
	
	private String unidadEje;
	
	private String codFuente;
	
	private String codCcpetRega;
	
	private String politicaPublica;
	
	private String detalleSectorial;
	
	private String recursoSGR;

	private String salida;
	
	private String naturalezaMod = idioma.getString("TB_TB4442");
	
	private String claseMod = ",ADC,APL,RED,TRA,";
	
	String clasesClasificador = " ";
    
   

	private Map<String, Object> parametrosEntrada;
    
    private List<Registro> listaData;
    
    
	private String cuentaAux = "";
    private String auxiliarCodigo =  "";
    private String codigoCons;
    private String ano;
    private  String naturalezaCuenta =  "";
    private String claseAux = ""; //de cualclasificador depende depende
  
   

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos cargarExcel y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;
	

	
	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * SelecFile y funciona como contenedor del archivo que se desea cargar
	 */
	private UploadedFile archivoCargaSelecFile;

	/**
	 * Variable que almacena la informacion del excel
	 */
	private String cadena;
	private long contador;

    @EJB
    private EjbCGRCero ejbCgrCero;
    
    @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfigurarCodigosCuipoControlador
     */
    public ConfigurarCodigosCuipoControlador() {
        super();
        
        contArchivocargarExcel = new ContenedorArchivo();
        
        compania = SessionUtil.getCompania();
        try {
            // 2305
            numFormulario = GeneralCodigoFormaEnum.CONFIGURAR_CODIGOS_CUIPO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            codigoCons = GeneralParameterEnum.CODIGO.getName();

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                anio = SysmanFunciones.nvl(parametrosEntrada.get("anio"), "0")
                                .toString();
                ano = anio;
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del
     * Bean ha sido creado, en este se realizan las asignaciones iniciales
     * necesarias para la visualizacion del formulario, como son tablas,
     * origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        tabla = "DETALLE_COMPROBANTE_PPTAL";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
//        cargarListaCodigoCPC();
//        cargarListaCodigoCPCE();
//        cargarListaFuenteCUIPO();
//        cargarListaFuenteCUIPOE();
//        cargarListaProductoCUIPO();
//        cargarListaProductoCUIPOE();
//        cargarListaSector(); 
//        cargarListaSectorE();
//        cargarListaPrograma(); 
//        cargarListaProgramaE();
//        cargarListaSubprograma(); 
//        cargarListaSubprogramaE();
//        cargarListaCodigoProducto(); 
//        cargarListaCodigoProductoE();
//        cargarListaCodigoBpin(); 
//        cargarListaCodigoBpinE();
//        cargarListaCodigoccpet(); 
//        cargarListaCodigoccpetE();
//        cargarListaCodigoCpcDane(); 
//        cargarListaCodigoCpcDaneE();
//        cargarListaCodigoUnidEjecutora(); 
//        cargarListaCodigoUnidEjecutoraE();
//        cargarListaCodigoFuente(); 
//        cargarListaCodigoFuenteE();
//        cargarListaCodigoCcpetRegalias(); 
//        cargarListaCodigoCcpetRegaliasE();
//        cargarListaPoliticaPublica();
//        cargarListaPoliticaPublicaE();
//        cargarListaDetalleSectorial();
//        cargarListaDetalleSectorialE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        
        aplicacion = 0;
		vigencia = "";
		regalias = 0;

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor
     * de la consulta del formulario. Tambien carga la lista del formulario por
     * primera vez
     */
    @Override
    public void reasignarOrigen() 
    {    
    	    	
		/*
		 * Se obtiene los clases configuradas en el parametro CLASES COMPROBANTE
		 * CONFIGURAR CLASIFICADORES
		 */
		try {
//			clasesClasificador = ejbSysmanUtil.consultarParametro(
//			        compania,
//			        "CLASES COMPROBANTE CONFIGURAR CLASIFICADORES",
//			        SessionUtil.getModulo(), new Date(),
//			        false);
			
			
			Map<String, Object> par = new HashMap<>();
			par.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			par.put(GeneralParameterEnum.ANO.getName(), anio);

			List<Registro> parametros = new ArrayList<>();
			parametros = ejecutarSelectMultiRegistro("4079", par);
			
			if (!parametros.isEmpty()) {
				for (Registro registro : parametros) {
					clasesClasificador =  (String) registro.getCampos().get("CLASE_CUIPO");
				}
			}
			
			if(clasesClasificador != null)
			{
				clasesClasificador = "," + clasesClasificador + ",";
			}
		} 
		catch (NegocioExcepcion e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

        parametrosListado.put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);

        parametrosListado.put(GeneralParameterEnum.TRIMESTRE.getName(), trimestre);        
        
		if (SysmanFunciones.nvl(naturaleza,"").equals("M")) {
			
			parametrosListado.put(GeneralParameterEnum.CLASE.getName(), claseMod); 
			
		}else {
			
	        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), clasesClasificador);
		}   
	        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarCodigosCuipoControladorUrlEnum.URL11819
                                                        .getValue());
		
        
//        
//        parametrosListado.put("FILTRO", "RES");
//        
//        parametrosListado.put("FILTROD", "ING");

        

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarCodigosCuipoControladorUrlEnum.URL13971
                                                        .getValue());

    }
    
    
    /**
     * Ejecuta un servicio DSS que corresponde a una consulta que devuelve multiples
     * registros.
     * 
     * @param idServicio identificador del servicio
     * @param params     parametros para resolver la consulta
     * @return lista de registros devueltos por la consulta
     * @throws NegocioExcepcion
     * 
     *                          en caso de que se presenten problemas al ejecutar el
     *                          servicio DSS
     */
    public List<Registro> ejecutarSelectMultiRegistro(String idServicio, Map<String, Object> params)
            throws NegocioExcepcion {
        UrlServiceUtil urlservice = UrlServiceUtil.getInstance();
        List<Parameter> parameters;
        try {
            String url = urlservice.getUrlServiceByUrlByEnumID(idServicio).getUrl();
            RequestManager requestManager = new RequestManager();
            parameters = requestManager.getList(url, params);
        } catch (Exception e) {
            String mensaje = "Error al consultar la Url con la información";
            String causa = mensaje;
            e.printStackTrace();
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(causa));
            throw error;
        }
        return RegistroConverter.toListRegistro(parameters);
    }
    
    public void activarEdicion(Registro registro) 
	{
		indice = listaInicial.getRowIndex();
		
		sector = SysmanFunciones.nvl(registro.getCampos().get("SECTOR"), "").toString();
		programa = SysmanFunciones.nvl(registro.getCampos().get("PROGRAMA"), "").toString();
		subPrograma = SysmanFunciones.nvl(registro.getCampos().get("SUBPROGRAMA"), "").toString();
		codProducto = SysmanFunciones.nvl(registro.getCampos().get("COD_PROD_CUIPO"), "").toString();
		codBpin = SysmanFunciones.nvl(registro.getCampos().get("CODIGO_BPIN"), "").toString();
		codCcpet = SysmanFunciones.nvl(registro.getCampos().get("CODIGO_CCPET"), "").toString();
		codCpcDane = SysmanFunciones.nvl(registro.getCampos().get("CODIGO_CPC"), "").toString();
		unidadEje = SysmanFunciones.nvl(registro.getCampos().get("CODIGOUNIDADEJE"), "").toString();
		codFuente = SysmanFunciones.nvl(registro.getCampos().get("FUENTE_CUIPO"), "").toString();
		codCcpetRega = SysmanFunciones.nvl(registro.getCampos().get("CODIGOCCPETREGA"), "").toString();
		politicaPublica = SysmanFunciones.nvl(registro.getCampos().get("POLITICA_PUBLICA"), "").toString();
		detalleSectorial = SysmanFunciones.nvl(registro.getCampos().get("DETALLE_SECTORIAL"), "").toString();
		recursoSGR = SysmanFunciones.nvl(registro.getCampos().get("RECURSO_SGR"), "").toString();
		
		this.registro = registro;
		String [] campos = cargarConfiguracionCuipo(registro.getCampos().get("CUENTA").toString());
		
		this.registro.getCampos().put("SECTOR", campos[0]);
		this.registro.getCampos().put("PROGRAMA", campos[1]);
		this.registro.getCampos().put("SUBPROGRAMA", campos[2]);
		this.registro.getCampos().put("COD_PROD_CUIPO", campos[3]);
		this.registro.getCampos().put("CODIGO_BPIN", campos[4]);
		this.registro.getCampos().put("CODIGO_CCPET", campos[5]);
		this.registro.getCampos().put("CODIGO_CPC", campos[6]);
		this.registro.getCampos().put("CODIGOUNIDADEJE", campos[7]);
		this.registro.getCampos().put("FUENTE_CUIPO", campos[8]);
		this.registro.getCampos().put("CODIGOCCPETREGA", campos[9]);
		this.registro.getCampos().put("POLITICA_PUBLICA", campos[10]);
		this.registro.getCampos().put("DETALLE_SECTORIAL", campos[11]);
	}
    /**
	 * cargar lista Modelo  combos cuipo
	 * @throws SystemException 
	 */
	public String[] cargarConfiguracionCuipo(String codigo)
	{		
			vigencia = validaVigencia(codigo);
    		regalias = validaRegalias(codigo);
			
			if(vigencia.equals("VA"))
			{
				String sectorC = "";
	    		String programaC = "";
	    		String subProgramaC = "";
	    		String codProductoC = "";
	    		String codBpinC = "";
	    		String codCcpetC = "";
	    		String codCpcDaneC = "";
	    		String unidadEjeC = "";
	    		String codFuenteC = "";
	    		String codCcpetRegaC = "";
	    		String politicaPublicaC = "";
	    		String detalleSectorialC = "";
	    		String recursoSGRC = "";
	    		
	    		Map<String, Object> param = new HashMap<>();
	    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    		param.put("ANIO", anio);
	    		param.put("CUENTA", codigo);
	    		
	    		try {
	    			listaTipoClasificador = RegistroConverter
	    					.toListRegistro(
	    							requestManager.getList(
	    									UrlServiceUtil.getInstance()
	    									.getUrlServiceByUrlByEnumID(
	    											ConfigurarCodigosCuipoControladorUrlEnum.URL13429.getValue())
	    									.getUrl(),
	    									param));
	    		} catch (SystemException e) {
	    			e.printStackTrace();
	    		}
	    		
	    		for (Registro option : listaTipoClasificador) 
	    		{
	    			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("001")) 
		    		{
	    				sectorC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("002")) 
		    		{
	    				programaC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("003")) 
		    		{
	    				subProgramaC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("004")) 
		    		{
	    				codProductoC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("005")) 
		    		{
	    				codBpinC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("006")) 
		    		{
	    				codCcpetC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("007")) 
		    		{
	    				codCpcDaneC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("008")) 
		    		{
		    			unidadEjeC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("009")) 
		    		{
		    			codFuenteC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("010")) 
		    		{
		    			codCcpetRegaC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("011")) 
		    		{
		    			politicaPublicaC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("012")) 
		    		{
		    			detalleSectorialC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
		    		if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("013")) 
		    		{
		    			recursoSGRC = option.getCampos().get("TIPOCLASIFICADOR").toString();
		    		};
	    		}
	    		  		
		    		try {
	//	    		aplicacion = validaAplicacion("001");
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "001",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("SECTOR", "0 ");
				    	bloqSector = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("SECTOR", sectorC);
				    	bloqSector = true;
				    }
				    else
				    {
				    	registro.getCampos().put("SECTOR", sector);
				    	bloqSector = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "002",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("PROGRAMA", "0 ");
				    	bloqPrograma = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("PROGRAMA", programaC);
				    	bloqPrograma = true;
				    }
				    else
				    {
				    	registro.getCampos().put("PROGRAMA", programa);
				    	bloqPrograma = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "003",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("SUBPROGRAMA", "0 ");
				    	bloqSubPrograma = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("SUBPROGRAMA", subProgramaC);
				    	bloqSubPrograma = true;
				    }
				    else
				    {
				    	registro.getCampos().put("SUBPROGRAMA", subPrograma);
				    	bloqSubPrograma = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "004",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("COD_PROD_CUIPO", "0 ");
				    	bloqCodProducto = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("COD_PROD_CUIPO", codProductoC);
				    	bloqCodProducto = true;
				    }
				    else
				    {
				    	registro.getCampos().put("COD_PROD_CUIPO", codProducto);
				    	bloqCodProducto = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "005",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("CODIGO_BPIN", "0 ");
				    	bloqCodBpin = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("CODIGO_BPIN", codBpinC);
				    	bloqCodBpin = true;
				    }
				    else
				    {
				    	registro.getCampos().put("CODIGO_BPIN", codBpin);
				    	bloqCodBpin = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "006",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("CODIGO_CCPET", "0 ");
				    	bloqCodCcpet = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("CODIGO_CCPET", codCcpetC);
				    	bloqCodCcpet = true;
				    }
				    else
				    {
				    	registro.getCampos().put("CODIGO_CCPET", codCcpet);
				    	bloqCodCcpet = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "007",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("CODIGO_CPC", "0 ");
				    	bloqCodCpcDane = true;
				    	bloqCpc = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("CODIGO_CPC", codCpcDaneC);
				    	bloqCodCpcDane = true;
				    	bloqCpc = true;
				    }
				    else
				    {
				    	registro.getCampos().put("CODIGO_CPC", codCpcDane);
				    	bloqCodCpcDane = false;
				    	bloqCpc = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "008",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("CODIGOUNIDADEJE", "0 ");
				    	bloqUnidadEje = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("CODIGOUNIDADEJE", unidadEjeC);
				    	bloqUnidadEje = true;
				    }
				    else
				    {
				    	registro.getCampos().put("CODIGOUNIDADEJE", unidadEje);
				    	bloqUnidadEje = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "009",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("FUENTE_CUIPO", "0 ");
				    	bloqCodFuente = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("FUENTE_CUIPO", codFuenteC);
				    	bloqCodFuente = true;
				    }
				    else
				    {
				    	registro.getCampos().put("FUENTE_CUIPO", codFuente);
				    	bloqCodFuente = false;
				    }
		    		
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "010",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("CODIGOCCPETREGA", "0 ");
				    	bloqCodCcpetRega = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("CODIGOCCPETREGA", codCcpetRegaC);
				    	bloqCodCcpetRega = true;
				    }
				    else
				    {
				    	if(regalias == 0)
				    	{
				    		bloqCodCcpetRega = true;
				    	}
				    	else 
				    	{
				    		bloqCodCcpetRega = false;
				    	}
				    	
				    	registro.getCampos().put("CODIGOCCPETREGA", codCcpetRega);
				    	
				    }
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "011",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("POLITICA_PUBLICA", "0 ");
				    	bloqPoliticaP = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("POLITICA_PUBLICA", politicaPublicaC);
				    	bloqPoliticaP = true;
				    }
				    else
				    {
				    	registro.getCampos().put("POLITICA_PUBLICA", politicaPublica);
				    	bloqPoliticaP = false;
				    }
		    		aplicacion = ejbSysmanUtil.aplicacionCuenta(compania,anio, "012",naturaleza);
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("DETALLE_SECTORIAL", "0 ");
				    	bloqDetalleS = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("DETALLE_SECTORIAL", detalleSectorialC);
				    	bloqDetalleS = true;
				    }
				    else
				    {
				    	registro.getCampos().put("DETALLE_SECTORIAL", detalleSectorial);
				    	bloqDetalleS = false;
				    }
		    		
		    		if(aplicacion == 0)
				    {
				    	registro.getCampos().put("RECURSO_SGR", "0 ");
				    	bloqRecursoSGR = true;
				    }
				    else if(aplicacion == 1)
				    {
				    	registro.getCampos().put("RECURSO_SGR", recursoSGRC);
				    	bloqRecursoSGR = true;
				    }
				    else
				    {
				    	registro.getCampos().put("RECURSO_SGR", recursoSGR);
				    	bloqRecursoSGR = false;
				    }
		    		}catch (SystemException e) {
						// TODO: handle exception
		    			e.printStackTrace();
					}		
	    		
			}
		
		Map<String, Object> parame = new HashMap<>();
		parame.put("COMPANIA", compania);
		parame.put("ANIO", String.valueOf(SysmanFunciones.ano(
				new Date())));
		parame.put("CUENTA",codigo);
		
		
		try {
			listaTipoCuentaRegalias = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConfigurarCodigosCuipoControladorUrlEnum.URL45086.getValue())
									.getUrl(),
									parame));
		} catch (SystemException e) {
			e.printStackTrace();
		}

		for (Registro option : listaTipoCuentaRegalias) {
			if(option.getCampos().get("REGALIAS").toString().equals("SI")  ) {
				bloqSector = false;
				bloqPrograma = false;
				bloqSubPrograma = false;
				bloqCodProducto = false;
				bloqCodBpin = false;
				bloqCodCcpet = false;
				bloqCodCpcDane = false;
				bloqCpc = false;
				bloqUnidadEje = false;
				bloqCodFuente = false;
				bloqCodCcpetRega = false;
			}
		}
		
		String [] campos = {SysmanFunciones.nvl(registro.getCampos().get("SECTOR"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("PROGRAMA"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("SUBPROGRAMA"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("COD_PROD_CUIPO"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("CODIGO_BPIN"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("CODIGO_CCPET"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("CODIGO_CPC"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("CODIGOUNIDADEJE"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("FUENTE_CUIPO"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("CODIGOCCPETREGA"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("POLITICA_PUBLICA"),"").toString(),
							SysmanFunciones.nvl(registro.getCampos().get("DETALLE_SECTORIAL"),"").toString()
			   				};
		
		
		
		return campos;
	}
	
	public String validaVigencia(String codigo)
	{
		UrlBean url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarCodigosCuipoControladorUrlEnum.URL13427.getValue());
			
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), anio);
	    param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
	        
	    try 
	    {
	    	Registro regAux = RegistroConverter.toRegistro(
	    			requestManager.get(url.getUrl(), param));
				
			vigencia = regAux.getCampos().get("TIPOVIGENCIA").toString();
		}
	    catch (SystemException e)
	    {
	    	e.printStackTrace();
		}
	    
	    return vigencia;
	}
	
	
	public int validaAplicacion(String clasificador)
	{
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), anio);
	    param.put("CLASIFICADOR", clasificador);
	    
	    UrlBean url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarCodigosCuipoControladorUrlEnum.URL13428.getValue());
	    
		try 
		{
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(url.getUrl(), param));
			
			aplicacion = (int) regAux.getCampos().get("APLICACION");
		} 
		catch (SystemException e) 
		{
			e.printStackTrace();
		}
			
		return aplicacion;
	}
	
	public int validaRegalias(String codigo)
	{
		UrlBean url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarCodigosCuipoControladorUrlEnum.URL13427.getValue());
			
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), anio);
	    param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
	        
	    try 
	    {
	    	Registro regAux = RegistroConverter.toRegistro(
	    			requestManager.get(url.getUrl(), param));
				
			regalias = (int) regAux.getCampos().get("REGALIAS");
		}
	    catch (SystemException e)
	    {
	    	e.printStackTrace();
		}
	    
	    return regalias;
	}
    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarCodigosCuipoControladorUrlEnum.URL10955
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCodigoCPC
     *
     */
    public void cargarListaCodigoCPC() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarCodigosCuipoControladorUrlEnum.URL11281
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoCPC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoCPC
     *
     */
    public void cargarListaCodigoCPCE() {
        listaCodigoCPCE = listaCodigoCPC;
    }

    /**
     * 
     * Carga la lista listaFuenteCUIPO
     *
     */
    public void cargarListaFuenteCUIPO() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarCodigosCuipoControladorUrlEnum.URL12357
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteCUIPO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteCUIPO
     *
     */
    public void cargarListaFuenteCUIPOE() {
        listaFuenteCUIPOE = listaFuenteCUIPO;
    }

    /**
     * 
     * Carga la lista listaProductoCUIPO
     *
     */
    public void cargarListaProductoCUIPO() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarCodigosCuipoControladorUrlEnum.URL13425
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaProductoCUIPO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaProductoCUIPO
     *
     */
    public void cargarListaProductoCUIPOE() {
        listaProductoCUIPOE = listaProductoCUIPO;
    }
    
    /**
	 * 
	 * Verifica si la clase clasificador tiene clase padre y lo retorna
	 *
	 */
	public String cargarClasePadre(String codigo, int aplicacion) {
		String clasePadre = "";
		Map<String, Object> param = new TreeMap<>();
		List<Registro> listaClasePadre = null;
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CLASE.getName(), codigo);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
		param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);

		try {
			listaClasePadre = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConfigurarCodigosCuipoControladorUrlEnum.URL13431.getValue())
									.getUrl(),
							param));
			if (!listaClasePadre.isEmpty()) {
				for (Registro clase : listaClasePadre) {
					clasePadre = clase.getCampos().get("CLASEPADRE").toString();
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return clasePadre;
	}

	/**
	 * 
	 * Carga la lista listaTipoClasificador Unificada MPEREZ
	 *
	 */
	public RegistroDataModelImpl cargarListaTipoClasificador(String codigo,int aplicacion)
	{
		RegistroDataModelImpl listaTipo = null;
		String clasePadre = "";		
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		if(naturaleza != null && !naturaleza.isEmpty() && !naturaleza.equals(""))
		{			
//			clasePadre = cargarClasePadre(codigo,aplicacion);		
//
//			if(clasePadre.isEmpty() || clasePadre == null || clasePadre.equals("") )
//			{
//				param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);
//				param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
//				param.put(GeneralParameterEnum.CLASE.getName(), codigo);
//				
//				urlBean = UrlServiceUtil.getInstance()
//						.getUrlServiceByUrlByEnumID(
//								ConfigurarCodigosCuipoControladorUrlEnum.URL13433
//								.getValue());
//			}
//			else
//			{			
//				//clasePadre = ","+clasePadre+",";
//				//codio provisional para luego con clasificadores hijo y padre debe borrarse
//				if(clasePadre.equals("006,008")) {
//					clasePadre = clasePadre.split(",")[1].toString();
//				}
//				clasePadre = clasePadre.replaceAll(",", "");		
				
				param.put(GeneralParameterEnum.CLASE.getName(), codigo);

				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								ConfigurarCodigosCuipoControladorUrlEnum.URL13432
								.getValue());
			}				

			listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());
			
//		}		 
		return listaTipo;
	}
    
    /**
     * 
     * Carga la lista listaSector
     *
     */ 
	public void cargarListaSector()
	{		
//		if(!"".equals(auxiliarCodigo)) {
//		UrlBean urlBean;
//		Map<String, Object> param = new TreeMap<>();
//		if("".equals(auxiliarCodigo)) {
//			urlBean = UrlServiceUtil.getInstance()
//					.getUrlServiceByUrlByEnumID(
//							ConfigurarCodigosCuipoControladorUrlEnum.URL1884027
//							.getValue());
//		}else {
//			urlBean = UrlServiceUtil.getInstance()
//					.getUrlServiceByUrlByEnumID(
//							ConfigurarCodigosCuipoControladorUrlEnum.URL1884043
//							.getValue());
//			param.put("IDHIJOO", auxiliarCodigo);
//			param.put("CLASEPADRE", "002");
//		}
//		auxiliarCodigo =  "";
//		listaPrograma = null;
//		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//		param.put(GeneralParameterEnum.ANO.getName(), ano);
//		param.put(GeneralParameterEnum.CLASE.getName(), "001");
//		param.put(GeneralParameterEnum.NATURALEZA.getName(), naturalezaCuenta);
//		param.put(GeneralParameterEnum.APLICACION.getName(), "2");
//
//
//
//		listaSector = new RegistroDataModelImpl(urlBean.getUrl(),
//				urlBean.getUrlConteo().getUrl(), param, true, codigoCons); 
//		}else {
//			listaSector = cargarListaTipoClasificador("001", 2);	
//		}
		
		listaSector  =  cargarListaTipoClasificador("001",2);
		cargarListaSectorE();
		
	}
    /**
     * 
     * Carga la lista listaSector
     *
     */
	public void  cargarListaSectorE()
	{
		listaSectorE = listaSector;
	}
	 /**
     * 
     * Carga la lista listaPrograma
     *
     */
	public void cargarListaPrograma()
	{		
//		if("".equals(cuentaAux)) {
//			UrlBean urlBean;
//			Map<String, Object> param = new TreeMap<>();
//			if("".equals(auxiliarCodigo)) {
//				urlBean = UrlServiceUtil.getInstance()
//						.getUrlServiceByUrlByEnumID(
//								ConfigurarCodigosCuipoControladorUrlEnum.URL1884027
//								.getValue());
//			}else {
//				urlBean = UrlServiceUtil.getInstance()
//						.getUrlServiceByUrlByEnumID(
//								ConfigurarCodigosCuipoControladorUrlEnum.URL1884029
//								.getValue());
//				param.put("IDHIJOO", auxiliarCodigo);
//			}
//			auxiliarCodigo =  "";
//			listaPrograma = null;
//			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//			param.put(GeneralParameterEnum.ANO.getName(), ano);
//			param.put(GeneralParameterEnum.CLASE.getName(), "002");
//			param.put(GeneralParameterEnum.NATURALEZA.getName(), naturalezaCuenta);
//			param.put(GeneralParameterEnum.APLICACION.getName(), "2");
//
//
//
//			listaPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
//					urlBean.getUrlConteo().getUrl(), param, true, codigoCons); 
//		}else {
//			listaPrograma =  null;
			listaPrograma  =  cargarListaTipoClasificador("002",2);

//			String clasePadre = "";		
//			UrlBean urlBean = new UrlBean();
//			Map<String, Object> param = new TreeMap<>();
//
//			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//			param.put(GeneralParameterEnum.ANO.getName(), ano);
//
//			if(naturalezaCuenta != null && !naturalezaCuenta.isEmpty() && !naturalezaCuenta.equals(""))
//			{			
//
//				param.put("CODIGOKEY", cuentaAux);
//				param.put(GeneralParameterEnum.APLICACION.getName(), 2);
//				param.put(GeneralParameterEnum.NATURALEZA.getName(), naturalezaCuenta);
//				param.put(GeneralParameterEnum.CLASE.getName(), "002");
//				urlBean = UrlServiceUtil.getInstance()
//						.getUrlServiceByUrlByEnumID(
//								ConfigurarCodigosCuipoControladorUrlEnum.URL1884034
//								.getValue());
//				listaPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
//						urlBean.getUrlConteo().getUrl(), param,
//						true, GeneralParameterEnum.CODIGO.getName());
//				if (listaPrograma.isVacio()) {
//					clasePadre = cargarClasePadre("002",2);
//					if(clasePadre.isEmpty() || clasePadre == null || clasePadre.equals("") )
//					{
//
//						urlBean = UrlServiceUtil.getInstance()
//								.getUrlServiceByUrlByEnumID(
//										ConfigurarCodigosCuipoControladorUrlEnum.URL134333
//										.getValue());
//						listaPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
//								urlBean.getUrlConteo().getUrl(), param,
//								true, GeneralParameterEnum.CODIGO.getName());
//					}		
//				}
//
//
//			}	
//			urlBean =  null;
//
//		}
//
//		cuentaAux =  "";
//		auxiliarCodigo =  "";

		cargarListaProgramaE();
	}
    /**
     * 
     * Carga la lista listaPrograma
     *
     */
	public void  cargarListaProgramaE()
	{
		listaProgramaE = listaPrograma;
	}
	   /**
     * 
     * Carga la lista listaSubprograma
     *
     */
	public void cargarListaSubprograma()
	{		
		listaSubprograma =   cargarListaTipoClasificador("003",2);
		cargarListaSubprogramaE();
	}
    /**
     * 
     * Carga la lista listaSubprograma
     *
     */
	public void  cargarListaSubprogramaE()
	{
		listaSubprogramaE = listaSubprograma;
	}
	/**
     * 
     * Carga la lista listaCodigoProducto
     *
     */
	public void cargarListaCodigoProducto()
	{		
//		Map<String, Object> param = new TreeMap<>();
//		UrlBean urlBean;
//		if("".equals(auxiliarCodigo)) {
//			urlBean = UrlServiceUtil.getInstance()
//					.getUrlServiceByUrlByEnumID(
//							ConfigurarCodigosCuipoControladorUrlEnum.URL1884027
//							.getValue());
//		}else {
//			urlBean = UrlServiceUtil.getInstance()
//					.getUrlServiceByUrlByEnumID(
//							ConfigurarCodigosCuipoControladorUrlEnum.URL1884031
//							.getValue());
//			param.put("IDPADRE", auxiliarCodigo);
//		}
//		auxiliarCodigo =  "";
//		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//		param.put(GeneralParameterEnum.ANO.getName(), ano);
//		param.put(GeneralParameterEnum.CLASE.getName(), "004");
//		param.put(GeneralParameterEnum.NATURALEZA.getName(), naturalezaCuenta);
//		param.put(GeneralParameterEnum.APLICACION.getName(), "2");
//		listaCodigoProducto =  null;
//		listaCodigoProducto = new RegistroDataModelImpl(urlBean.getUrl(),
//				urlBean.getUrlConteo().getUrl(), param, true, codigoCons); 
		listaCodigoProducto  =  cargarListaTipoClasificador("004",2);
		cargarListaCodigoProductoE();
	}
    /**
     * 
     * Carga la lista listaCodigoProducto
     *
     */
	public void  cargarListaCodigoProductoE()
	{
		listaCodigoProductoE = listaCodigoProducto;
	}
    /**
     * 
     * Carga la lista listaCodigoBpin
     *
     */
	public void cargarListaCodigoBpin()
	{		
		listaCodigoBpin = cargarListaTipoClasificador("005", 2);
	}
    /**
     * 
     * Carga la lista listaCodigoBpin
     *
     */
	public void  cargarListaCodigoBpinE()
	{
		listaCodigoBpinE = listaCodigoBpin;
	}
    /**
     * 
     * Carga la lista listaCodigoccpet
     *
     */
	public void cargarListaCodigoccpet()
	{		
		listaCodigoccpet = cargarListaTipoClasificador("006", 2);
	}
    /**
     * 
     * Carga la lista listaCodigoccpet
     *
     */
	public void  cargarListaCodigoccpetE()
	{

		listaCodigoccpetE =listaCodigoccpet;
	}
    /**
     * 
     * Carga la lista listaCodigoCpcDane
     *
     */
	public void cargarListaCodigoCpcDane()
	{		
//		Map<String, Object> param = new TreeMap<>();
//		UrlBean urlBean;
//		if("".equals(auxiliarCodigo)) {
//			urlBean = UrlServiceUtil.getInstance()
//					.getUrlServiceByUrlByEnumID(
//							ConfigurarCodigosCuipoControladorUrlEnum.URL1884027
//							.getValue());
//		}else {
//			urlBean = UrlServiceUtil.getInstance()
//					.getUrlServiceByUrlByEnumID(
//							ConfigurarCodigosCuipoControladorUrlEnum.URL1884041
//							.getValue());
//			param.put(GeneralParameterEnum.IDHIJO.getName(), auxiliarCodigo);
//		}
//		auxiliarCodigo =  "";
//		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//		param.put(GeneralParameterEnum.ANO.getName(), ano);
//		param.put(GeneralParameterEnum.CLASE.getName(), "007"); 
//		param.put(GeneralParameterEnum.IDCLASEPADRE.getName(), claseAux);
//		claseAux =  "";
//		param.put(GeneralParameterEnum.CLASIFICADORLISTA.getName(),"007");
//		param.put(GeneralParameterEnum.NATURALEZA.getName(), naturalezaCuenta);
//		param.put(GeneralParameterEnum.APLICACION.getName(), "2");
//		listaCodigoCPC =  null;
//		listaCodigoCPC = new RegistroDataModelImpl(urlBean.getUrl(),
//				urlBean.getUrlConteo().getUrl(), param, true, codigoCons); 
		listaCodigoCPC = cargarListaTipoClasificador("007", 2);
		cargarListaCodigoCpcDaneE();
	}
    /**
     * 
     * Carga la lista listaCodigoCpcDane
     *
     */
	public void  cargarListaCodigoCpcDaneE()
	{
		listaCodigoCPCE = listaCodigoCPC;
	}
    /**
     * 
     * Carga la lista listaCodigoUnidEjecutora
     *
     */
	public void cargarListaCodigoUnidEjecutora()
	{		
		listaCodigoUnidEjecutora = cargarListaTipoClasificador("008", 2);
	}
    /**
     * 
     * Carga la lista listaCodigoUnidEjecutora
     *
     */
	public void  cargarListaCodigoUnidEjecutoraE()
	{
		listaCodigoUnidEjecutoraE = listaCodigoUnidEjecutora;
	}
    /**
     * 
     * Carga la lista listaCodigoFuente
     *
     */
	public void cargarListaCodigoFuente()
	{
//		Map<String, Object> param = new TreeMap<>();
//
//		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//		param.put(GeneralParameterEnum.ANO.getName(), anio);		
//		param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);
//		param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
//		param.put(GeneralParameterEnum.CLASE.getName(), "009");
//		param.put(GeneralParameterEnum.REGALIAS.getName(), regalias);
//
//		UrlBean urlBean = UrlServiceUtil.getInstance()
//				.getUrlServiceByUrlByEnumID(ConfigurarCodigosCuipoControladorUrlEnum.URL13434.getValue());
//
//		listaCodigoFuente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
//				GeneralParameterEnum.CODIGO.getName());
		
		listaCodigoFuente = cargarListaTipoClasificador("009", 2);
	}
    /**
     * 
     * Carga la lista listaCodigoFuente
     *
     */
	public void  cargarListaCodigoFuenteE()
	{
		listaCodigoFuenteE = listaCodigoFuente;
	}
    /**
     * 
     * Carga la lista listaCodigoCcpetRegalias
     *
     */
	public void cargarListaCodigoCcpetRegalias()
	{		
		listaCodigoCcpetRegalias = cargarListaTipoClasificador("010", 2);
	}
    /**
     * 
     * Carga la lista listaCodigoCcpetRegalias
     *
     */
	public void  cargarListaCodigoCcpetRegaliasE()
	{
		listaCodigoCcpetRegaliasE = listaCodigoCcpetRegalias;
	}
	
	/**
     * 
     * Carga la lista listaPoliticaPublica
     *
     */
	public void cargarListaPoliticaPublica()
	{		
		listaPoliticaPublica =  cargarListaTipoClasificador("011",2 );
		cargarListaPoliticaPublicaE();
	}
    /**
     * 
     * Carga la lista listaCodigoCcpetRegalias
     *
     */
	public void  cargarListaPoliticaPublicaE()
	{
		listaPoliticaPublicaE = listaPoliticaPublica;
	}	
	/**
     * 
     * Carga la lista listaCodigoFuente
     *
     */
	public void cargarListaDetalleSectorial()
	{		
//		Map<String, Object> param = new TreeMap<>();
//		UrlBean urlBean;
//		urlBean = UrlServiceUtil.getInstance()
//				.getUrlServiceByUrlByEnumID(
//						ConfigurarCodigosCuipoControladorUrlEnum.URL1884041
//						.getValue());
//		//param.put("CODIGOKEY", cuentaAux);
//		param.put(GeneralParameterEnum.IDHIJO.getName(), cuentaAux);
//		cuentaAux =  "";
//		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//		param.put(GeneralParameterEnum.ANO.getName(), ano);
//		param.put(GeneralParameterEnum.CLASE.getName(),"012");//de donde proviene
//		param.put(GeneralParameterEnum.CLASIFICADORLISTA.getName(),"012");//a donde llega
//		param.put(GeneralParameterEnum.IDCLASEPADRE.getName(), claseAux);//de donde viene
//		claseAux =  "";
//		param.put(GeneralParameterEnum.NATURALEZA.getName(), naturalezaCuenta);
//		param.put(GeneralParameterEnum.APLICACION.getName(), "2");
//		listaDetalleSectorial =  null;
//		listaDetalleSectorial = new RegistroDataModelImpl(urlBean.getUrl(),
//				urlBean.getUrlConteo().getUrl(), param, true, codigoCons); 

		listaDetalleSectorial =  cargarListaTipoClasificador("012",2);
		cargarListaDetalleSectorialE();
	}
    /**
     * 
     * Carga la lista listaCodigoFuente
     *
     */
	public void  cargarListaDetalleSectorialE()
	{
		listaDetalleSectorialE = listaDetalleSectorial;
	}
	
	/**
     * 
     * Carga la lista listaCodigoFuente
     *
     */
	public void cargarListaRecursoSGR()
	{	
		listaRecursoSGR =  cargarListaTipoClasificador("013",2);
		cargarListaRecursoSGRE();
	}
    /**
     * 
     * Carga la lista listaCodigoFuente
     *
     */
	public void  cargarListaRecursoSGRE()
	{
		listaRecursoSGRE = listaRecursoSGR;
	}
	
    // </METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cargar en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCargar() {

		Workbook workbook = null;
		cadena = "";
		try (FileInputStream file = new FileInputStream(contArchivocargarExcel.getArchivo());) {
			

			if (validarArchivo()) {

				String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
						rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				Sheet sheet = workbook.getSheet("Plantilla");
				long i = 1;
				contador = 0;
				for (Row row : sheet) {
					contador++;
				}
				 salida = null;
				for (Row row : sheet) {
					
					if (!validarCelda(row.getCell(0))) {
						break;
					}
                    //carga cada 50 registros cuando  la cantidad de los mismos son mas  autor:cperez
					capturaDatosExcel(row);
					if(50 * (i/50)  == i || (i >= contador && !"".equals(cadena)) ) {
						if (!cadena.equals("")) { 
							cargarDatos();
							cadena =  "";
						}
					}
					
					i =  i +1;
				}
				ArchivosBean.generarPlano("RelacionDeCodigos.txt", salida);
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	/**
	 * Verifica el valor de la celda y retorna false si esta vacia.
	 * 
	 * @param celda Objeto de tipo <code>Cell</code>
	 * @return false si la celda esta vacia.
	 */
	private boolean validarCelda(Cell celda) {
		if (celda == null) {
			return false;
		}

		return !celda.getStringCellValue().isEmpty();
	}

	private void capturaDatosExcel(Row row) {

		if (row.getRowNum() > 0) {

			for (int i = 0; i < 28; i++) {
				String val="";
					if(i >= 4 && i <= 6 || i >= 8 && i <= 14 ) {
						val = "";
					}else {
						val = row.getCell(i) + "";
						if(val.equals("")) {
							val =  "NoDato";
						}else {
							int cuantosPuntos = val.toString().split("\\.").length;
							String[] cuantosPuntosArray = val.toString().split("\\.");
							if(cuantosPuntos == 2) {
								if(val.contains(".0"))
									val = val.substring(0, val.length() - 2);
							}
						}
					}
				cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
			}
			cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());

			cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}
	}
	private void cargarDatos() {
		
		try {
			String parametro = (SysmanFunciones.esBdSqlServer())
					? cadena.replace("TO_CLOB(", "").replace(")", "")
							: cadena;

			salida =	salida +	ejbCgrCero.actualizarTipoClasificador(compania, parametro, SessionUtil.getUser().getCodigo());
		

		} catch (SystemException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {
		    
		if (contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Descargar en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @throws IOException
	 *
	 */
	public void oprimirCrear() throws IOException {
		// <CODIGO_DESARROLLADO>
		setArchivoDescarga(null);
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			HSSFSheet excelSheet = workbook.createSheet("Plantilla");

			/* Propiedades letra encabezado */
			Font font = workbook.createFont();
			font.setFontName("Calibri");
			font.setBold(true);

			// TamaÃƒÂ±o de letra
			font.setFontHeightInPoints((short) 8);

			/* Estilo encabezado */
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);

			Row row = excelSheet.createRow(1);
			Cell cell = row.createCell(0);

			row = excelSheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("Tipo Comprobante");
			excelSheet.autoSizeColumn(0);

			cell = row.createCell(1);
			cell.setCellValue("Comprobante");
			excelSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue("Consecutivo Ppto");
			excelSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue("Fecha");
			excelSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue("Descripcion");
			excelSheet.autoSizeColumn(4);			
			
			cell = row.createCell(5);
			cell.setCellValue("Nit Tercero");
			excelSheet.autoSizeColumn(5);	

			cell = row.createCell(6);
			cell.setCellValue("Nombre Tercero");
			excelSheet.autoSizeColumn(6);	
			
			cell = row.createCell(7);
			cell.setCellValue("Rubro");
			excelSheet.autoSizeColumn(7);	
			
			cell = row.createCell(8);
			cell.setCellValue("Nombre Rubro");
			excelSheet.autoSizeColumn(8);	
			
			cell = row.createCell(9);
			cell.setCellValue("Centro de Costo");
			excelSheet.autoSizeColumn(9);	
			
			cell = row.createCell(10);
			cell.setCellValue("Referencia");
			excelSheet.autoSizeColumn(10);	
			
			cell = row.createCell(11);
			cell.setCellValue("Fuente Recurso");
			excelSheet.autoSizeColumn(11);	
			
			cell = row.createCell(12);
			cell.setCellValue("Auxiliar General");
			excelSheet.autoSizeColumn(12);	
			
			cell = row.createCell(13);
			cell.setCellValue("Crédito");
			excelSheet.autoSizeColumn(13);	
			
			cell = row.createCell(14);
			cell.setCellValue("Contracredito");
			excelSheet.autoSizeColumn(14);	
			
			cell = row.createCell(15);
			cell.setCellValue("Sector");
			excelSheet.autoSizeColumn(15);	
			
			cell = row.createCell(16);
			cell.setCellValue("Programa");
			excelSheet.autoSizeColumn(16);	
			
			cell = row.createCell(17);
			cell.setCellValue("Subprograma");
			excelSheet.autoSizeColumn(17);	
			
			cell = row.createCell(18);
			cell.setCellValue("Código Producto");
			excelSheet.autoSizeColumn(18);	
			
			cell = row.createCell(19);
			cell.setCellValue("Código BPIN");
			excelSheet.autoSizeColumn(19);	
			
			cell = row.createCell(20);
			cell.setCellValue("Código CCPET");
			excelSheet.autoSizeColumn(20);	
			
			cell = row.createCell(21);
			cell.setCellValue("Código CPC");
			excelSheet.autoSizeColumn(21);	
			
			cell = row.createCell(22);
			cell.setCellValue("Código Unidad Ejecutora");
			excelSheet.autoSizeColumn(22);	

			cell = row.createCell(23);
			cell.setCellValue("Código Fuente");
			excelSheet.autoSizeColumn(23);
			
			cell = row.createCell(24);
			cell.setCellValue("Código CCPET Regalias");
			excelSheet.autoSizeColumn(24);
			
			cell = row.createCell(25);
			cell.setCellValue("Política Pública");
			excelSheet.autoSizeColumn(25);
			
			cell = row.createCell(26);
			cell.setCellValue("Detalle Sectorial");
			excelSheet.autoSizeColumn(26);
			
			cell = row.createCell(27);
			cell.setCellValue("Tipo Recurso SGR");
			excelSheet.autoSizeColumn(27);
			
			Map<String, Object> param = new HashMap<>();

	        try {
	            
	            param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

	            param.put(GeneralParameterEnum.ANO.getName(), anio);

	            param.put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);

	            param.put(GeneralParameterEnum.TRIMESTRE.getName(), trimestre);
	            
	            if (SysmanFunciones.nvl(naturaleza,"").equals("M")) {
	    			
	    			param.put(GeneralParameterEnum.CLASE.getName(), claseMod); 
	    			
	    		}else {
	    			param.put(GeneralParameterEnum.CLASE.getName(), clasesClasificador);  
	    		}
				listaData = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ConfigurarCodigosCuipoControladorUrlEnum.URL38065.getValue())
										.getUrl(),
										param));
				


				int rowIndex = 1;
				for (Registro option : listaData) {
					 row = excelSheet.createRow(rowIndex++);
					 cell = row.createCell(0);
					 cell.setCellValue(option.getCampos().get("TIPO_CPTE").toString());
					 
					 cell = row.createCell(1);					 
					 cell.setCellValue(option.getCampos().get("COMPROBANTE").toString());
					 
					 cell = row.createCell(2);					 
					 cell.setCellValue(option.getCampos().get("CONSECUTIVO").toString());

					 cell = row.createCell(3);	
					 SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					 String fechaTexto = formatter.format(option.getCampos().get("FECHA"));
					 cell.setCellValue(fechaTexto );
					 
					 cell = row.createCell(4);					 
					 cell.setCellValue(option.getCampos().get("DESCRIPCION").toString());
					 
					 cell = row.createCell(5);					 
					 cell.setCellValue(option.getCampos().get("NIT").toString());
					 
					 cell = row.createCell(6);					 
					 cell.setCellValue(option.getCampos().get("NOMBRE").toString());
					 
					 cell = row.createCell(7);					 
					 cell.setCellValue(option.getCampos().get("CUENTA").toString());
					 
					 cell = row.createCell(8);					 
					 cell.setCellValue(option.getCampos().get("CUENTANOMBRE").toString());
					 
					 cell = row.createCell(9);					 
					 cell.setCellValue(option.getCampos().get("CENTRO_COSTO").toString());
					 
					 cell = row.createCell(10);					 
					 cell.setCellValue(option.getCampos().get("REFERENCIA").toString());
					 
					 cell = row.createCell(11);					 
					 cell.setCellValue(option.getCampos().get("FUENTE_RECURSO").toString());
					 
					 cell = row.createCell(12);					 
					 cell.setCellValue(option.getCampos().get("AUXILIAR").toString());
					 
					 cell = row.createCell(13);					 
					 cell.setCellValue(option.getCampos().get("CREDITO").toString());
					 
					 cell = row.createCell(14);					 
					 cell.setCellValue(option.getCampos().get("CONTRACREDITO").toString());
					 
					 cell = row.createCell(15);	
					 if(option.getCampos().get("SECTOR") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("SECTOR").toString());
					 }
					 
					 cell = row.createCell(16);		
					 if(option.getCampos().get("PROGRAMA") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("PROGRAMA").toString());
					 }
					 
					 cell = row.createCell(17);	
					 if(option.getCampos().get("SUBPROGRAMA") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("SUBPROGRAMA").toString());
					 }
					 
					 cell = row.createCell(18);
					 if(option.getCampos().get("COD_PROD_CUIPO") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("COD_PROD_CUIPO").toString());
					 }

					 cell = row.createCell(19);
					 if(option.getCampos().get("CODIGO_BPIN") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("CODIGO_BPIN").toString());
					 }


					 cell = row.createCell(20);		
					 if(option.getCampos().get("CODIGO_CCPET") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("CODIGO_CCPET").toString());
					 }

					 cell = row.createCell(21);		
					 if(option.getCampos().get("CODIGO_CPC") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("CODIGO_CPC").toString());
					 }


					 cell = row.createCell(22);			
					 if(option.getCampos().get("CODIGOUNIDADEJE") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("CODIGOUNIDADEJE").toString());
					 }

					 cell = row.createCell(23);	
					 if(option.getCampos().get("FUENTE_CUIPO") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("FUENTE_CUIPO").toString());
					 }

					 cell = row.createCell(24);		
					 if(option.getCampos().get("CODIGOCCPETREGA") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("CODIGOCCPETREGA").toString());
					 }

					 cell = row.createCell(25);		
					 if(option.getCampos().get("POLITICA_PUBLICA") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("POLITICA_PUBLICA").toString());
					 }


					 cell = row.createCell(26);	
					 if(option.getCampos().get("DETALLE_SECTORIAL") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("DETALLE_SECTORIAL").toString());
					 }
					 
					 cell = row.createCell(27);	
					 if(option.getCampos().get("RECURSO_SGR") == null) {
						 cell.setCellValue("");
					 }else {
						 cell.setCellValue(option.getCampos().get("RECURSO_SGR").toString());
					 }
					 
				}
				
				
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        workbook.write(out);
	        
			setArchivoDescarga(JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Configuración Clasificadores a detalle.xls"));

		} catch (IOException | JRException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
		}

		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Agregar una lista desplegable a la pÃƒÂ¡gina de la hoja
	 *
	 * Archivo de Excel del libro de trabajo @param, utilizado para agregar el
	 * nombre
	 * 
	 * @param targetSheet La pÃƒÂ¡gina de la hoja donde se encuentra la lista en
	 *                    cascada
	 * @param options     Datos en cascada ['Baidu', 'Alibaba']
	 * @param column      La columna de la lista desplegable comienza en'A '
	 * @param fromRow     fila de inicio del lÃƒÂ­mite desplegable
	 * @param endRow      lÃƒÂ­mite desplegable de la fila final
	 */
	public static void addValidationToSheet2(Workbook workbook, Sheet targetSheet, List<Registro> options, char column,
			int fromRow, int endRow, String name) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";

		int rowIndex = 0;
		for (Registro option : options) {
			int columnIndex = 0;
			Row row = optionsSheet.createRow(rowIndex++);
			Cell cell = row.createCell(columnIndex++);
			Cell cell1 = row.createCell(columnIndex);
			cell.setCellValue(option.getCampos().get("CODIGO").toString());
			cell1.setCellValue(option.getCampos().get("NOMBRE").toString());
		}

		createName(workbook, nameName, hiddenSheetName + "!$A$1:$A$" + options.size());
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
		CellRangeAddressList regions = new CellRangeAddressList(fromRow, endRow, (int) column - 'A',
				(int) column - 'A');
		targetSheet.addValidationData(new HSSFDataValidation(regions, constraint));
	}

	private static Name createName(Workbook workbook, String nameName, String formula) 
	{
		Name name = workbook.createName();
		name.setNameName(nameName);
		name.setRefersToFormula(formula);
		return name;
	}

	/**
	 * No se puede empezar con un nÃƒÂºmero
	 *
	 * @param name
	 * @return
	 */
	static String formatNameName(String name) {
		name = name.replace(" ", "").replace("-", "_").replace(":", ".");
		if (Character.isDigit(name.charAt(0))) {
			name = "_" + name;
		}

		return name;
	}
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
//        cargarListaCodigoCPC();
//        cargarListaCodigoCPCE();
//        cargarListaFuenteCUIPO();
//        cargarListaFuenteCUIPOE();
//        cargarListaProductoCUIPO();
//        cargarListaProductoCUIPOE();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Naturaleza
     * 
     * 
     */
    public void cambiarNaturaleza() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Trimestre
     * 
     * 
     */
    public void cambiarTrimestre() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        cargarListaSector(); 
        cargarListaSectorE();
        cargarListaPrograma(); 
        cargarListaProgramaE();
        cargarListaSubprograma(); 
        cargarListaSubprogramaE();
        cargarListaCodigoProducto(); 
        cargarListaCodigoProductoE();
        cargarListaCodigoBpin(); 
        cargarListaCodigoBpinE();
        cargarListaCodigoccpet(); 
        cargarListaCodigoccpetE();
        cargarListaCodigoCpcDane(); 
        cargarListaCodigoCpcDaneE();
        cargarListaCodigoUnidEjecutora(); 
        cargarListaCodigoUnidEjecutoraE();
        cargarListaCodigoFuente(); 
        cargarListaCodigoFuenteE();
        cargarListaCodigoCcpetRegalias(); 
        cargarListaCodigoCcpetRegaliasE();
        cargarListaPoliticaPublica();
        cargarListaPoliticaPublicaE();
        cargarListaDetalleSectorial();
        cargarListaDetalleSectorialE();
        cargarListaRecursoSGR();
        cargarListaRecursoSGRE();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCPC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCPC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_CPC",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
//        listaCodigoCPC.setFilters(null);
    }
    

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCPC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCPCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
//        listaCodigoCPCE.setFilters(null);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaFuenteCUIPO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteCUIPO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_CUIPO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaFuenteCUIPO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteCUIPOE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaProductoCUIPO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProductoCUIPO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COD_PROD_CUIPO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaProductoCUIPO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProductoCUIPOE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSector
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSector(SelectEvent event) 
    {
    	Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SECTOR", registroAux.getCampos().get("CODIGO"));
//		registro.getCampos().put("PROGRAMA", "");
//		registro.getCampos().put("COD_PROD_CUIPO", "");
//		cuentaAux =  extraerString( registroAux.getCampos().get("CODIGO"));
		cargarListaPrograma();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSector
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSectorE(SelectEvent event) 
    {
    	Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
//		cuentaAux =  auxiliar;
		cargarListaPrograma();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPrograma
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPrograma(SelectEvent event) 
    {
    	Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("PROGRAMA", registroAux.getCampos().get("CODIGO"));
		//auxiliarCodigo =  extraerString(registroAux.getCampos().get("IDHIJO"));
		//cargarListaCodigoProducto();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPrograma
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProgramaE(SelectEvent event) 
    {
    	Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		//auxiliarCodigo =  extraerString(registroAux.getCampos().get("IDHIJO"));
		//cargarListaCodigoProducto();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSubprograma
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaSubprograma(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
	    registro.getCampos().put("SUBPROGRAMA", 
	    		registroAux.getCampos().get(
	    				GeneralParameterEnum.CODIGO.getName()));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSubprograma
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaSubprogramaE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
	    auxiliar =  SysmanFunciones.nvl(registroAux.getCampos()
    			.get(GeneralParameterEnum.CODIGO.getName()), "")
    			.toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoProducto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoProducto(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("COD_PROD_CUIPO", registroAux.getCampos().get("CODIGO"));
//		String auxC =   extraerString(registroAux.getCampos().get("IDPADRE"));
//		if(!"".equals(auxC) || !auxC.isEmpty() || auxC !=  null) {
//			auxC =  extraerString(registroAux.getCampos().get("IDPADRE")).substring(3, extraerString(registroAux.getCampos().get("IDPADRE")).length());
//		}
//		registro.getCampos().put("PROGRAMA",auxC );
//		auxiliarCodigo =  extraerString(registroAux.getCampos().get("IDPADRE"));
//		String hijo =  "";
//		hijo =  cargarClaseHijo("002",2, naturalezaCuenta,auxiliarCodigo);
//		if(!"".equals(hijo) || !hijo.isEmpty() || hijo !=  null) {
//			auxiliarCodigo =  hijo;
//			hijo =  hijo.substring(3,hijo.length());
//			registro.getCampos().put("SECTOR",hijo );
//		}
//		auxiliarCodigo =  extraerString(registroAux.getCampos().get("IDPADRE"));
		cargarListaPrograma();
//		cargarListaSector();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoProducto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoProductoE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
//		registro.getCampos().put("COD_PROD_CUIPO", registroAux.getCampos().get("CODIGO"));
//		
//		String auxC =   extraerString(registroAux.getCampos().get("IDPADRE"));
//		if(!"".equals(auxC) || !auxC.isEmpty() || auxC !=  null) {
//			auxC =  extraerString(registroAux.getCampos().get("IDPADRE")).substring(3, extraerString(registroAux.getCampos().get("IDPADRE")).length());
//		}
//		registro.getCampos().put("PROGRAMA",auxC );
//		auxiliarCodigo =  extraerString(registroAux.getCampos().get("IDPADRE"));
//		String hijo =  "";
//		hijo =  cargarClaseHijo("002",2, naturalezaCuenta,auxiliarCodigo);
//		if(!"".equals(hijo) || !hijo.isEmpty() || hijo !=  null) {
//			auxiliarCodigo =  hijo;
//			hijo =  hijo.substring(3,hijo.length());
//			registro.getCampos().put("SECTOR",hijo );
//			
//		}
//		auxiliarCodigo =  extraerString(registroAux.getCampos().get("IDPADRE"));
		cargarListaPrograma();
//		cargarListaSector();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoBpin
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoBpin(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
	    registro.getCampos().put("CODIGOBPIN", 
	    		registroAux.getCampos().get(
	    				GeneralParameterEnum.CODIGO.getName()));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoBpin
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoBpinE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
    			.get(GeneralParameterEnum.CODIGO.getName()), "")
    			.toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoccpet
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoccpet(SelectEvent event) 
	{
		
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CCPET", registroAux.getCampos().get("CODIGO"));
//		cuentaAux =  extraerString( registroAux.getCampos().get("CODIGO"));
//		auxiliarCodigo =  cuentaAux;
//		registro.getCampos().put("CODIGO_CPC", "");
//		registro.getCampos().put("DETALLE_SECTORIAL", "");
//		claseAux =  "006"; //de cual clasificador se envia
//		cargarListaCodigoCpcDane(); 
//		claseAux =  "006"; //de cual clasificador se envia
//		auxiliarCodigo =  cuentaAux;
//		cargarListaDetalleSectorial();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoccpet
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoccpetE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
//		cuentaAux =  auxiliar;
//		auxiliarCodigo =  auxiliar;
//		registro.getCampos().put("DETALLE_SECTORIAL", "");
//		registro.getCampos().put("CODIGO_CPC", "");
//		claseAux =  "006";//de cual clasificador se envia
//		cargarListaCodigoCpcDane(); 
//		claseAux =  "006"; //de cual clasificador envia
//		auxiliarCodigo =  cuentaAux;
//		cargarListaDetalleSectorial();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCpcDane
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoCpcDane(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CPC", registroAux.getCampos().get("CODIGO"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCpcDane
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoCpcDaneE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
	    auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
    			.get(GeneralParameterEnum.CODIGO.getName()), "")
    			.toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoUnidEjecutora
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoUnidEjecutora(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGOUNIDADEJE", registroAux.getCampos().get("CODIGO"));
//		cuentaAux =  extraerString( registroAux.getCampos().get("CODIGO"));
//		registro.getCampos().put("DETALLE_SECTORIAL", "");
//		claseAux =  "008";
//		cargarListaDetalleSectorial();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoUnidEjecutora
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoUnidEjecutoraE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
//		registro.getCampos().put("DETALLE_SECTORIAL", "");
//		cuentaAux = auxiliar;
//		claseAux =  "008";
//		cargarListaDetalleSectorial();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFuente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoFuente(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FUENTE_CUIPO", registroAux.getCampos().get("CODIGO"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFuente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoFuenteE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCcpetRegalias
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoCcpetRegalias(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGOCCPETREGA", registroAux.getCampos().get("CODIGO"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCcpetRegalias
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoCcpetRegaliasE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
	}
	
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPoliticaPublica
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaPoliticaPublica(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("POLITICA_PUBLICA", registroAux.getCampos().get("CODIGO"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPoliticaPublica
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaPoliticaPublicaE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
	
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDetalleSectorial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaDetalleSectorial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DETALLE_SECTORIAL", registroAux.getCampos().get("CODIGO"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDetalleSectorial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaDetalleSectorialE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
	
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRecursoSGR
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaRecursoSGR(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("RECURSO_SGR", registroAux.getCampos().get("CODIGO"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRecursoSGR
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaRecursoSGRE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
    /**
     * Validaciones realizadas antes de cerrar el formulario.
     
    public void ejecutarrcCerrar() {

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CONF_PLAN_PPTAL_CUIPO
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }
*/
	/**
     * Validaciones realizadas antes de cerrar el formulario.
     */
	public void ejecutarrcCerrar() 
	 {
	 	Map<String, Object> param = new HashMap<>();
		param.put("anio", anio);

	        Direccionador direccionador = new Direccionador();

	        direccionador.setNumForm(Integer
	                        .toString(GeneralCodigoFormaEnum.CONFIGURACION_PLAN
	                                        .getCodigo()));

		direccionador.setParametros(param);
	        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

	 }
    // </METODOS_COMBOS_GRANDES>
	//<METODOS_CAMBIAR>
   
    /**
     * Metodo ejecutado al cambiar el control Sector en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarSectorC(int rowNum) {
         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
         ///  listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola ");
//		listaInicial.getDatasource().get(rowNum).getCampos().put("PROGRAMA", "");
//		listaInicial.getDatasource().get(rowNum).getCampos().put("COD_PROD_CUIPO", ""); 
         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control Programa en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarProgramaC(int rowNum) {
         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
         // listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola ");
//	     listaInicial.getDatasource().get(rowNum).getCampos().put("SECTOR", ""); 
//         listaInicial.getDatasource().get(rowNum).getCampos().put("COD_PROD_CUIPO", ""); 
         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control CodigoProducto en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarCodigoProductoC(int rowNum) {
         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
//          listaInicial.getDatasource().get(rowNum).getCampos().put("SECTOR", registro.getCampos().get("SECTOR")); 
//          listaInicial.getDatasource().get(rowNum).getCampos().put("PROGRAMA", registro.getCampos().get("PROGRAMA")); 
         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control Codigoccpet en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarCodigoccpetC(int rowNum) {
         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
//	     listaInicial.getDatasource().get(rowNum).getCampos().put("CODIGO_CPC", "");
//	     listaInicial.getDatasource().get(rowNum).getCampos().put("DETALLE_SECTORIAL", "");
         // listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control CodigoUnidEjecutora en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarCodigoUnidEjecutoraC(int rowNum) {
         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
         // listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola ");
//	     listaInicial.getDatasource().get(rowNum).getCampos().put("DETALLE_SECTORIAL", "");
         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control DetalleSectorial en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarDetalleSectorialC(int rowNum) {
         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
         // listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
//</METODOS_CAMBIAR>
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
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * 
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del
     * registro
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

    /*
	 * 
	 */
	public String cargarClaseHijo(String codigo,int aplicacion, String Naturaleza,String idHijo) {
		String padre = "";
		Map<String, Object> param = new TreeMap<>();
		List <Registro>  listaPadre =  null;

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CLASE.getName(), codigo);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), Naturaleza);
		param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);	
		param.put("IDHIJO", idHijo);	

		try {
			listaPadre =  RegistroConverter.toListRegistro( requestManager.getList( UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarCodigosCuipoControladorUrlEnum.URL1884033.getValue()).getUrl()
					,param));
			if (!listaPadre.isEmpty()) {
				for( Registro id: listaPadre) {
					padre = id.getCampos().get("IDPADRE").toString();
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return padre;
	}
    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
     * pueden remover valores auxiliares que no se desee o se deban enviar en el
     * registro
     */
    @Override
    public void removerCombos() {

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove(GeneralParameterEnum.FECHA.getName());
        registro.getCampos().remove(GeneralParameterEnum.TIPO_CPTE.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPROBANTE.getName());
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        registro.getCampos().remove(GeneralParameterEnum.NIT.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.CUENTA.getName());
        registro.getCampos().remove("CUENTANOMBRE");
        registro.getCampos().remove("CENTRO_COSTO");
        registro.getCampos().remove("FUENTE_RECURSO");
        registro.getCampos().remove("AUXILIAR");
        registro.getCampos().remove("REFERENCIA");
        registro.getCampos().remove("CREDITO");
        registro.getCampos().remove("CONTRACREDITO");

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del
     * registro se usa cuando se desean agregar valores al registro despues de
     * dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable naturaleza
     * 
     * @return naturaleza
     */
    public String getNaturaleza() {
    	naturalezaCuenta =  naturaleza;
        return naturaleza;
    }

    /**
     * Asigna la variable naturaleza
     * 
     * @param naturaleza
     * Variable a asignar en naturaleza
     */
    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
        naturalezaCuenta =  naturaleza;
    }

    /**
     * Retorna la variable trimestre
     * 
     * @return trimestre
     */
    public String getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     * 
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }
    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoCPC
     * 
     * @return listaCodigoCPC
     */
    public RegistroDataModelImpl getListaCodigoCPC() {
        return listaCodigoCPC;
    }

    /**
     * Asigna la lista listaCodigoCPC
     * 
     * @param listaCodigoCPC
     * Variable a asignar en listaCodigoCPC
     */
    public void setListaCodigoCPC(RegistroDataModelImpl listaCodigoCPC) {
        this.listaCodigoCPC = listaCodigoCPC;
    }

    /**
     * Retorna la lista listaCodigoCPC
     * 
     * @return listaCodigoCPC
     */
    public RegistroDataModelImpl getListaCodigoCPCE() {
        return listaCodigoCPCE;
    }

    /**
     * Asigna la lista listaCodigoCPC
     * 
     * @param listaCodigoCPC
     * Variable a asignar en listaCodigoCPC
     */
    public void setListaCodigoCPCE(RegistroDataModelImpl listaCodigoCPCE) {
        this.listaCodigoCPCE = listaCodigoCPCE;
    }

    /**
     * Retorna la lista listaFuenteCUIPO
     * 
     * @return listaFuenteCUIPO
     */
    public RegistroDataModelImpl getListaFuenteCUIPO() {
        return listaFuenteCUIPO;
    }

    /**
     * Asigna la lista listaFuenteCUIPO
     * 
     * @param listaFuenteCUIPO
     * Variable a asignar en listaFuenteCUIPO
     */
    public void setListaFuenteCUIPO(RegistroDataModelImpl listaFuenteCUIPO) {
        this.listaFuenteCUIPO = listaFuenteCUIPO;
    }

    /**
     * Retorna la lista listaFuenteCUIPO
     * 
     * @return listaFuenteCUIPO
     */
    public RegistroDataModelImpl getListaFuenteCUIPOE() {
        return listaFuenteCUIPOE;
    }

    /**
     * Asigna la lista listaFuenteCUIPO
     * 
     * @param listaFuenteCUIPO
     * Variable a asignar en listaFuenteCUIPO
     */
    public void setListaFuenteCUIPOE(RegistroDataModelImpl listaFuenteCUIPOE) {
        this.listaFuenteCUIPOE = listaFuenteCUIPOE;
    }

    /**
     * Retorna la lista listaProductoCUIPO
     * 
     * @return listaProductoCUIPO
     */
    public RegistroDataModelImpl getListaProductoCUIPO() {
        return listaProductoCUIPO;
    }

    /**
     * Asigna la lista listaProductoCUIPO
     * 
     * @param listaProductoCUIPO
     * Variable a asignar en listaProductoCUIPO
     */
    public void setListaProductoCUIPO(
        RegistroDataModelImpl listaProductoCUIPO) {
        this.listaProductoCUIPO = listaProductoCUIPO;
    }

    /**
     * Retorna la lista listaProductoCUIPO
     * 
     * @return listaProductoCUIPO
     */
    public RegistroDataModelImpl getListaProductoCUIPOE() {
        return listaProductoCUIPOE;
    }

    /**
     * Asigna la lista listaProductoCUIPO
     * 
     * @param listaProductoCUIPO
     * Variable a asignar en listaProductoCUIPO
     */
    public void setListaProductoCUIPOE(
        RegistroDataModelImpl listaProductoCUIPOE) {
        this.listaProductoCUIPOE = listaProductoCUIPOE;
    }
    /**
     * Retorna la lista listaSector
     * 
     * @return listaSector
     */
    public RegistroDataModelImpl getListaSector() 
    {
        return listaSector;
    }
    /**
     * Asigna la lista listaSector
     * 
     * @param listaSector
     * Variable a asignar en  listaSector
     */
    public void setListaSector(RegistroDataModelImpl listaSector) 
    {
        this.listaSector = listaSector;
    }
    /**
     * Retorna la lista listaSector
     * 
     * @return listaSector
     */
    public RegistroDataModelImpl getListaSectorE() 
    {
        return listaSectorE;
    }
    /**
     * Asigna la lista listaSector
     * 
     * @param listaSector
     * Variable a asignar en  listaSector
     */
    public void setListaSectorE(RegistroDataModelImpl listaSectorE) 
    {
        this.listaSectorE = listaSectorE;
    }
    /**
     * Retorna la lista listaPrograma
     * 
     * @return listaPrograma
     */
    public RegistroDataModelImpl getListaPrograma() 
    {
        return listaPrograma;
    }
    /**
     * Asigna la lista listaPrograma
     * 
     * @param listaPrograma
     * Variable a asignar en  listaPrograma
     */
    public void setListaPrograma(RegistroDataModelImpl listaPrograma) 
    {
        this.listaPrograma = listaPrograma;
    }
    /**
     * Retorna la lista listaPrograma
     * 
     * @return listaPrograma
     */
    public RegistroDataModelImpl getListaProgramaE() 
    {
        return listaProgramaE;
    }
    /**
     * Asigna la lista listaPrograma
     * 
     * @param listaPrograma
     * Variable a asignar en  listaPrograma
     */
    public void setListaProgramaE(RegistroDataModelImpl listaProgramaE)
    {
        this.listaProgramaE = listaProgramaE;
    }
    /**
     * Retorna la lista listaSubprograma
     * 
     * @return listaSubprograma
     */
    public RegistroDataModelImpl getListaSubprograma() 
    {
        return listaSubprograma;
    }
    /**
     * Asigna la lista listaSubprograma
     * 
     * @param listaSubprograma
     * Variable a asignar en  listaSubprograma
     */
    public void setListaSubprograma(RegistroDataModelImpl listaSubprograma) 
    {
        this.listaSubprograma = listaSubprograma;
    }
    /**
     * Retorna la lista listaSubprograma
     * 
     * @return listaSubprograma
     */
    public RegistroDataModelImpl getListaSubprogramaE() 
    {
        return listaSubprogramaE;
    }
    /**
     * Asigna la lista listaSubprograma
     * 
     * @param listaSubprograma
     * Variable a asignar en  listaSubprograma
     */
    public void setListaSubprogramaE(RegistroDataModelImpl listaSubprogramaE) 
    {
        this.listaSubprogramaE = listaSubprogramaE;
    }
    /**
     * Retorna la lista listaCodigoProducto
     * 
     * @return listaCodigoProducto
     */
    public RegistroDataModelImpl getListaCodigoProducto() 
    {
        return listaCodigoProducto;
    }
    /**
     * Asigna la lista listaCodigoProducto
     * 
     * @param listaCodigoProducto
     * Variable a asignar en  listaCodigoProducto
     */
    public void setListaCodigoProducto(RegistroDataModelImpl listaCodigoProducto) 
    {
        this.listaCodigoProducto = listaCodigoProducto;
    }
    /**
     * Retorna la lista listaCodigoProducto
     * 
     * @return listaCodigoProducto
     */
    public RegistroDataModelImpl getListaCodigoProductoE() 
    {
        return listaCodigoProductoE;
    }
    /**
     * Asigna la lista listaCodigoProducto
     * 
     * @param listaCodigoProducto
     * Variable a asignar en  listaCodigoProducto
     */
    public void setListaCodigoProductoE(RegistroDataModelImpl listaCodigoProductoE) 
    {
        this.listaCodigoProductoE = listaCodigoProductoE;
    }
    /**
     * Retorna la lista listaCodigoBpin
     * 
     * @return listaCodigoBpin
     */
    public RegistroDataModelImpl getListaCodigoBpin() 
    {
        return listaCodigoBpin;
    }
    /**
     * Asigna la lista listaCodigoBpin
     * 
     * @param listaCodigoBpin
     * Variable a asignar en  listaCodigoBpin
     */
    public void setListaCodigoBpin(RegistroDataModelImpl listaCodigoBpin) 
    {
        this.listaCodigoBpin = listaCodigoBpin;
    }
    /**
     * Retorna la lista listaCodigoBpin
     * 
     * @return listaCodigoBpin
     */
    public RegistroDataModelImpl getListaCodigoBpinE() 
    {
        return listaCodigoBpinE;
    }
    /**
     * Asigna la lista listaCodigoBpin
     * 
     * @param listaCodigoBpin
     * Variable a asignar en  listaCodigoBpin
     */
    public void setListaCodigoBpinE(RegistroDataModelImpl listaCodigoBpinE) 
    {
        this.listaCodigoBpinE = listaCodigoBpinE;
    }
    /**
     * Retorna la lista listaCodigoccpet
     * 
     * @return listaCodigoccpet
     */
    public RegistroDataModelImpl getListaCodigoccpet() 
    {
        return listaCodigoccpet;
    }
    /**
     * Asigna la lista listaCodigoccpet
     * 
     * @param listaCodigoccpet
     * Variable a asignar en  listaCodigoccpet
     */
    public void setListaCodigoccpet(RegistroDataModelImpl listaCodigoccpet) 
    {
        this.listaCodigoccpet = listaCodigoccpet;
    }
    /**
     * Retorna la lista listaCodigoccpet
     * 
     * @return listaCodigoccpet
     */
    public RegistroDataModelImpl getListaCodigoccpetE() 
    {
        return listaCodigoccpetE;
    }
    /**
     * Asigna la lista listaCodigoccpet
     * 
     * @param listaCodigoccpet
     * Variable a asignar en  listaCodigoccpet
     */
    public void setListaCodigoccpetE(RegistroDataModelImpl listaCodigoccpetE) 
    {
        this.listaCodigoccpetE = listaCodigoccpetE;
    }
    /**
     * Retorna la lista listaCodigoCpcDane
     * 
     * @return listaCodigoCpcDane
     */
    public RegistroDataModelImpl getListaCodigoCpcDane() 
    {
        return listaCodigoCpcDane;
    }
    /**
     * Asigna la lista listaCodigoCpcDane
     * 
     * @param listaCodigoCpcDane
     * Variable a asignar en  listaCodigoCpcDane
     */
    public void setListaCodigoCpcDane(RegistroDataModelImpl listaCodigoCpcDane) 
    {
        this.listaCodigoCpcDane = listaCodigoCpcDane;
    }
    /**
     * Retorna la lista listaCodigoCpcDane
     * 
     * @return listaCodigoCpcDane
     */
    public RegistroDataModelImpl getListaCodigoCpcDaneE() 
    {
        return listaCodigoCpcDaneE;
    }
    /**
     * Asigna la lista listaCodigoCpcDane
     * 
     * @param listaCodigoCpcDane
     * Variable a asignar en  listaCodigoCpcDane
     */
    public void setListaCodigoCpcDaneE(RegistroDataModelImpl listaCodigoCpcDaneE) 
    {
        this.listaCodigoCpcDaneE = listaCodigoCpcDaneE;
    }
    /**
     * Retorna la lista listaCodigoUnidEjecutora
     * 
     * @return listaCodigoUnidEjecutora
     */
    public RegistroDataModelImpl getListaCodigoUnidEjecutora() 
    {
        return listaCodigoUnidEjecutora;
    }
    /**
     * Asigna la lista listaCodigoUnidEjecutora
     * 
     * @param listaCodigoUnidEjecutora
     * Variable a asignar en  listaCodigoUnidEjecutora
     */
    public void setListaCodigoUnidEjecutora(RegistroDataModelImpl listaCodigoUnidEjecutora) 
    {
        this.listaCodigoUnidEjecutora = listaCodigoUnidEjecutora;
    }
    /**
     * Retorna la lista listaCodigoUnidEjecutora
     * 
     * @return listaCodigoUnidEjecutora
     */
    public RegistroDataModelImpl getListaCodigoUnidEjecutoraE() 
    {
        return listaCodigoUnidEjecutoraE;
    }
    /**
     * Asigna la lista listaCodigoUnidEjecutora
     * 
     * @param listaCodigoUnidEjecutora
     * Variable a asignar en  listaCodigoUnidEjecutora
     */
    public void setListaCodigoUnidEjecutoraE(RegistroDataModelImpl listaCodigoUnidEjecutoraE) 
    {
        this.listaCodigoUnidEjecutoraE = listaCodigoUnidEjecutoraE;
    }
    /**
     * Retorna la lista listaCodigoFuente
     * 
     * @return listaCodigoFuente
     */
    public RegistroDataModelImpl getListaCodigoFuente() 
    {
        return listaCodigoFuente;
    }
    /**
     * Asigna la lista listaCodigoFuente
     * 
     * @param listaCodigoFuente
     * Variable a asignar en  listaCodigoFuente
     */
    public void setListaCodigoFuente(RegistroDataModelImpl listaCodigoFuente) 
    {
        this.listaCodigoFuente = listaCodigoFuente;
    }
    /**
     * Retorna la lista listaCodigoFuente
     * 
     * @return listaCodigoFuente
     */
    public RegistroDataModelImpl getListaCodigoFuenteE() 
    {
        return listaCodigoFuenteE;
    }
    /**
     * Asigna la lista listaCodigoFuente
     * 
     * @param listaCodigoFuente
     * Variable a asignar en  listaCodigoFuente
     */
    public void setListaCodigoFuenteE(RegistroDataModelImpl listaCodigoFuenteE) 
    {
        this.listaCodigoFuenteE = listaCodigoFuenteE;
    }
    /**
     * Retorna la lista listaCodigoCcpetRegalias
     * 
     * @return listaCodigoCcpetRegalias
     */
    public RegistroDataModelImpl getListaCodigoCcpetRegalias() 
    {
        return listaCodigoCcpetRegalias;
    }
    /**
     * Asigna la lista listaCodigoCcpetRegalias
     * 
     * @param listaCodigoCcpetRegalias
     * Variable a asignar en  listaCodigoCcpetRegalias
     */
    public void setListaCodigoCcpetRegalias(RegistroDataModelImpl listaCodigoCcpetRegalias) 
    {
        this.listaCodigoCcpetRegalias = listaCodigoCcpetRegalias;
    }
    /**
     * Retorna la lista listaCodigoCcpetRegalias
     * 
     * @return listaCodigoCcpetRegalias
     */
    public RegistroDataModelImpl getListaCodigoCcpetRegaliasE() 
    {
        return listaCodigoCcpetRegaliasE;
    }
    /**
     * Asigna la lista listaCodigoCcpetRegalias
     * 
     * @param listaCodigoCcpetRegalias
     * Variable a asignar en  listaCodigoCcpetRegalias
     */
    public void setListaCodigoCcpetRegaliasE(RegistroDataModelImpl listaCodigoCcpetRegaliasE)
    {
        this.listaCodigoCcpetRegaliasE = listaCodigoCcpetRegaliasE;
    }
    /**
	 * @return the listaTipoClasificador
	 */
	public List<Registro> getListaTipoClasificador() {
		return listaTipoClasificador;
	}
	/**
	 * @param listaTipoClasificador the listaTipoClasificador to set
	 */
	public void setListaTipoClasificador(List<Registro> listaTipoClasificador) {
		this.listaTipoClasificador = listaTipoClasificador;
	}
	
	public List<Registro> getListaTipoCuentaRegalias() {
		return listaTipoCuentaRegalias;
	}

	public void setListaTipoCuentaRegalias(List<Registro> listaTipoCuentaRegalias) {
		this.listaTipoCuentaRegalias = listaTipoCuentaRegalias;
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
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    /**
	 * Retorna la variable modeloA
	 * 
	 * @return modeloA
	 */
	public int getModeloA() {
		return modeloA;
	}
	/**
	 * Asigna la variable modeloA
	 * 
	 * @param modeloA
	 * Variable a asignar en modeloA
	 */
	public void setModeloA(int modeloA) {
		this.modeloA = modeloA;
	}
	/**
	 * Retorna la variable bloqSector
	 * 
	 * @return bloqSector
	 */
	public boolean getBloqSector() {
		return bloqSector;
	}
	/**
	 * Asigna la variable bloqSector
	 * 
	 * @param bloqSector
	 * Variable a asignar en bloqSector
	 */
	public void setBloqSector(boolean bloqSector) {
		this.bloqSector = bloqSector;
	}
	/**
	 * Retorna la variable bloqPrograma
	 * 
	 * @return bloqPrograma
	 */
	public boolean getBloqPrograma() {
		return bloqPrograma;
	}
	/**
	 * Asigna la variable bloqPrograma
	 * 
	 * @param bloqPrograma
	 * Variable a asignar en bloqPrograma
	 */
	public void setBloqPrograma(boolean bloqPrograma) {
		this.bloqPrograma = bloqPrograma;
	}
	/**
	 * Retorna la variable bloqSubPrograma
	 * 
	 * @return bloqSubPrograma
	 */
	public boolean getBloqSubPrograma() {
		return bloqSubPrograma;
	}
	/**
	 * Asigna la variable bloqSubPrograma
	 * 
	 * @param bloqSubPrograma
	 * Variable a asignar en bloqSubPrograma
	 */
	public void setBloqSubPrograma(boolean bloqSubPrograma) {
		this.bloqSubPrograma = bloqSubPrograma;
	}
	/**
	 * Retorna la variable bloqCodProducto
	 * 
	 * @return bloqCodProducto
	 */
	public boolean getBloqCodProducto() {
		return bloqCodProducto;
	}
	/**
	 * Asigna la variable bloqCodProducto
	 * 
	 * @param bloqCodProducto
	 * Variable a asignar en bloqCodProducto
	 */
	public void setBloqCodProducto(boolean bloqCodProducto) {
		this.bloqCodProducto = bloqCodProducto;
	}
	/**
	 * Retorna la variable bloqCodBpin
	 * 
	 * @return bloqCodBpin
	 */
	public boolean getBloqCodBpin() {
		return bloqCodBpin;
	}
	/**
	 * Asigna la variable bloqCodBpin
	 * 
	 * @param bloqCodBpin
	 * Variable a asignar en bloqCodBpin
	 */
	public void setBloqCodBpin(boolean bloqCodBpin) {
		this.bloqCodBpin = bloqCodBpin;
	}
	/**
	 * Retorna la variable bloqCodCcpet
	 * 
	 * @return bloqCodCcpet
	 */
	public boolean getBloqCodCcpet() {
		return bloqCodCcpet;
	}
	/**
	 * Asigna la variable bloqCodCcpet
	 * 
	 * @param bloqCodCcpet
	 * Variable a asignar en bloqCodCcpet
	 */
	public void setBloqCodCcpet(boolean bloqCodCcpet) {
		this.bloqCodCcpet = bloqCodCcpet;
	}
	/**
	 * Retorna la variable bloqCodCpcDane
	 * 
	 * @return bloqCodCpcDane
	 */
	public boolean getBloqCodCpcDane() {
		return bloqCodCpcDane;
	}
	/**
	 * Asigna la variable bloqCodCpcDane
	 * 
	 * @param bloqCodCpcDane
	 * Variable a asignar en bloqCodCpcDane
	 */
	public void setBloqCodCpcDane(boolean bloqCodCpcDane) {
		this.bloqCodCpcDane = bloqCodCpcDane;
	}
	/**
	 * Retorna la variable bloqUnidadEje
	 * 
	 * @return bloqUnidadEje
	 */
	public boolean getBloqUnidadEje() {
		return bloqUnidadEje;
	}
	/**
	 * Asigna la variable bloqUnidadEje
	 * 
	 * @param bloqUnidadEje
	 * Variable a asignar en bloqUnidadEje
	 */
	public void setBloqUnidadEje(boolean bloqUnidadEje) {
		this.bloqUnidadEje = bloqUnidadEje;
	}
	/**
	 * Retorna la variable bloqCodFuente
	 * 
	 * @return bloqCodFuente
	 */
	public boolean getBloqCodFuente() {
		return bloqCodFuente;
	}
	/**
	 * Asigna la variable bloqCodFuente
	 * 
	 * @param bloqCodFuente
	 * Variable a asignar en bloqCodFuente
	 */
	public void setBloqCodFuente(boolean bloqCodFuente) {
		this.bloqCodFuente = bloqCodFuente;
	}
	/**
	 * Retorna la variable bloqCodCcpetRega
	 * 
	 * @return bloqCodCcpetRega
	 */
	public boolean getBloqCodCcpetRega() {
		return bloqCodCcpetRega;
	}
	/**
	 * Asigna la variable bloqCodCcpetRega
	 * 
	 * @param bloqCodCcpetRega
	 * Variable a asignar en bloqCodCcpetRega
	 */
	public void setBloqCodCcpetRega(boolean bloqCodCcpetRega) {
		this.bloqCodCcpetRega = bloqCodCcpetRega;
	}
	/**
	 * Retorna la variable indice
	 * 
	 * @return indice
	 */
	public int getIndice() {
		return indice;
	}
	/**
	 * Asigna la variable indice
	 * 
	 * @param indice
	 * Variable a asignar en indice
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}
    // </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the archivoCargaSelecFile
	 */
	public UploadedFile getArchivoCargaSelecFile() {
		return archivoCargaSelecFile;
	}

	/**
	 * @param archivoCargaSelecFile the archivoCargaSelecFile to set
	 */
	public void setArchivoCargaSelecFile(UploadedFile archivoCargaSelecFile) {
		this.archivoCargaSelecFile = archivoCargaSelecFile;
	}

	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * @return the listaPoliticaPublica
	 */
	public RegistroDataModelImpl getListaPoliticaPublica() {
		return listaPoliticaPublica;
	}

	/**
	 * @param listaPoliticaPublica the listaPoliticaPublica to set
	 */
	public void setListaPoliticaPublica(RegistroDataModelImpl listaPoliticaPublica) {
		this.listaPoliticaPublica = listaPoliticaPublica;
	}

	/**
	 * @return the listaPoliticaPublicaE
	 */
	public RegistroDataModelImpl getListaPoliticaPublicaE() {
		return listaPoliticaPublicaE;
	}

	/**
	 * @param listaPoliticaPublicaE the listaPoliticaPublicaE to set
	 */
	public void setListaPoliticaPublicaE(RegistroDataModelImpl listaPoliticaPublicaE) {
		this.listaPoliticaPublicaE = listaPoliticaPublicaE;
	}

	/**
	 * @return the listaDetalleSectorial
	 */
	public RegistroDataModelImpl getListaDetalleSectorial() {
		return listaDetalleSectorial;
	}

	/**
	 * @param listaDetalleSectorial the listaDetalleSectorial to set
	 */
	public void setListaDetalleSectorial(RegistroDataModelImpl listaDetalleSectorial) {
		this.listaDetalleSectorial = listaDetalleSectorial;
	}

	/**
	 * @return the listaDetalleSectorialE
	 */
	public RegistroDataModelImpl getListaDetalleSectorialE() {
		return listaDetalleSectorialE;
	}

	/**
	 * @param listaDetalleSectorialE the listaDetalleSectorialE to set
	 */
	public void setListaDetalleSectorialE(RegistroDataModelImpl listaDetalleSectorialE) {
		this.listaDetalleSectorialE = listaDetalleSectorialE;
	}

	/**
	 * @return the listaRecursoSGR
	 */
	public RegistroDataModelImpl getListaRecursoSGR() {
		return listaRecursoSGR;
	}

	/**
	 * @param listaRecursoSGR the listaRecursoSGR to set
	 */
	public void setListaRecursoSGR(RegistroDataModelImpl listaRecursoSGR) {
		this.listaRecursoSGR = listaRecursoSGR;
	}

	/**
	 * @return the listaRecursoSGRE
	 */
	public RegistroDataModelImpl getListaRecursoSGRE() {
		return listaRecursoSGRE;
	}

	/**
	 * @param listaRecursoSGRE the listaRecursoSGRE to set
	 */
	public void setListaRecursoSGRE(RegistroDataModelImpl listaRecursoSGRE) {
		this.listaRecursoSGRE = listaRecursoSGRE;
	}

	/**
	 * @return the bloqPoliticaP
	 */
	public boolean isBloqPoliticaP() {
		return bloqPoliticaP;
	}

	/**
	 * @param bloqPoliticaP the bloqPoliticaP to set
	 */
	public void setBloqPoliticaP(boolean bloqPoliticaP) {
		this.bloqPoliticaP = bloqPoliticaP;
	}

	/**
	 * @return the bloqDetalleS
	 */
	public boolean isBloqDetalleS() {
		return bloqDetalleS;
	}

	/**
	 * @param bloqDetalleS the bloqDetalleS to set
	 */
	public void setBloqDetalleS(boolean bloqDetalleS) {
		this.bloqDetalleS = bloqDetalleS;
	}

	/**
	 * @return the politicaPublica
	 */
	public String getPoliticaPublica() {
		return politicaPublica;
	}

	/**
	 * @param politicaPublica the politicaPublica to set
	 */
	public void setPoliticaPublica(String politicaPublica) {
		this.politicaPublica = politicaPublica;
	}

	/**
	 * @return the detalleSectorial
	 */
	public String getDetalleSectorial() {
		return detalleSectorial;
	}

	/**
	 * @param detalleSectorial the detalleSectorial to set
	 */
	public void setDetalleSectorial(String detalleSectorial) {
		this.detalleSectorial = detalleSectorial;
	}
	
	/**
	 * @return the recursoSGR
	 */
	public String getRecursoSGR() {
		return recursoSGR;
	}

	/**
	 * @param recursoSGR the recursoSGR to set
	 */
	public void setRecursoSGR(String recursoSGR) {
		this.recursoSGR = recursoSGR;
	}
	
	public int getAplicacion() {
		return aplicacion;
	}

	public void setAplicacion(int aplicacion) {
		this.aplicacion = aplicacion;
	}

	public String getVigencia() {
		return vigencia;
	}

	public void setVigencia(String vigencia) {
		this.vigencia = vigencia;
	}

	public int getRegalias() {
		return regalias;
	}

	public void setRegalias(int regalias) {
		this.regalias = regalias;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getPrograma() {
		return programa;
	}

	public void setPrograma(String programa) {
		this.programa = programa;
	}

	public String getSubPrograma() {
		return subPrograma;
	}

	public void setSubPrograma(String subPrograma) {
		this.subPrograma = subPrograma;
	}

	public String getCodProducto() {
		return codProducto;
	}

	public void setCodProducto(String codProducto) {
		this.codProducto = codProducto;
	}

	public String getCodBpin() {
		return codBpin;
	}

	public void setCodBpin(String codBpin) {
		this.codBpin = codBpin;
	}

	public String getCodCcpet() {
		return codCcpet;
	}

	public void setCodCcpet(String codCcpet) {
		this.codCcpet = codCcpet;
	}

	public String getCodCpcDane() {
		return codCpcDane;
	}

	public void setCodCpcDane(String codCpcDane) {
		this.codCpcDane = codCpcDane;
	}

	public String getUnidadEje() {
		return unidadEje;
	}

	public void setUnidadEje(String unidadEje) {
		this.unidadEje = unidadEje;
	}

	public String getCodFuente() {
		return codFuente;
	}

	public void setCodFuente(String codFuente) {
		this.codFuente = codFuente;
	}

	public String getCodCcpetRega() {
		return codCcpetRega;
	}

	public void setCodCcpetRega(String codCcpetRega) {
		this.codCcpetRega = codCcpetRega;
	}

	public Map<String, Object> getParametrosEntrada() {
		return parametrosEntrada;
	}

	public void setParametrosEntrada(Map<String, Object> parametrosEntrada) {
		this.parametrosEntrada = parametrosEntrada;
	}

	public String getCuentaAux() {
		return cuentaAux;
	}

	public void setCuentaAux(String cuentaAux) {
		this.cuentaAux = cuentaAux;
	}

	public String getAuxiliarCodigo() {
		return auxiliarCodigo;
	}

	public void setAuxiliarCodigo(String auxiliarCodigo) {
		this.auxiliarCodigo = auxiliarCodigo;
	}

	

	public String getCadena() {
		return cadena;
	}

	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

	public long getContador() {
		return contador;
	}

	public void setContador(long contador) {
		this.contador = contador;
	}

	public EjbCGRCero getEjbCgrCero() {
		return ejbCgrCero;
	}

	public void setEjbCgrCero(EjbCGRCero ejbCgrCero) {
		this.ejbCgrCero = ejbCgrCero;
	}

	public EjbSysmanUtilRemote getEjbSysmanUtil() {
		return ejbSysmanUtil;
	}

	public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
		this.ejbSysmanUtil = ejbSysmanUtil;
	}

	public String getCompania() {
		return compania;
	}
	public String getCodigoCons() {
		return codigoCons;
	}
	public void setCodigoCons(String codigoCons) {
		this.codigoCons = codigoCons;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}
	
	public String getNaturalezaCuenta() {
		return naturalezaCuenta;
	}

	public void setNaturalezaCuenta(String naturalezaCuenta) {
		this.naturalezaCuenta = naturalezaCuenta;
	}
	/**
	 * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
	 *
	 * @param object
	 * Un Objeto
	 * @return String que representa al objeto
	 */
	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}
	public String getClaseAux() {
		return claseAux;
	}

	public void setClaseAux(String claseAux) {
		this.claseAux = claseAux;
	}
	public List<Registro> getListaData() {
		return listaData;
	}

	public void setListaData(List<Registro> listaData) {
		this.listaData = listaData;
	}
	public String getSalida() {
		return salida;
	}

	public void setSalida(String salida) {
		this.salida = salida;
	}
	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}

	public boolean isBloqCpc() {
		return bloqCpc;
	}

	public void setBloqCpc(boolean bloqCpc) {
		this.bloqCpc = bloqCpc;
	}

	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}

	/**
	 * @return the bloqRecursoSGR
	 */
	public boolean isBloqRecursoSGR() {
		return bloqRecursoSGR;
	}

	/**
	 * @param bloqRecursoSGR the bloqRecursoSGR to set
	 */
	public void setBloqRecursoSGR(boolean bloqRecursoSGR) {
		this.bloqRecursoSGR = bloqRecursoSGR;
	}
	
}
