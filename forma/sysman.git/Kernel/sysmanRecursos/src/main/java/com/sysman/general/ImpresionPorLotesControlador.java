package com.sysman.general;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.reportes.ComprobantesContPresReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ImpresionPorLotesControladorEnum;
import com.sysman.general.enums.ImpresionPorLotesControladorUrlEnum;
import com.sysman.general.enums.SolicitudDisponibilidadControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author apineda
 * @version 1, 12/05/2016
 *
 * @version 1.1, 04/04/2017, pespitia : <br>
 * Buenas practicas SonarLint. <br>
 * Se realizo el Refactoring.
 */
@ManagedBean
@ViewScoped
public class ImpresionPorLotesControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante a nivel de clase que aloja el menu actual utilizado
     * por el usuario que interactua
     */
    private final String menuActual;

    /**
     * Constante a nivel de clase que aloja la cadena {@code NUMERO}
     */
    private final String cNumero;

    /**
     * Constante a nivel de clase que aloja la cadena
     * {@code NUMEROINI}
     */
    private final String cNumeroIni;

    /**
     * Constante a nivel de clase que aloja la cadena
     * {@code NUMEROFIN}
     */
    private final String cNumeroFin;

    /**
     * Constante a nivel de clase que aloja la cadena {@code NOMBRE}
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que aloja la cadena
     * {@code VLRAGIRAR}
     */
    private final String cVlrGirar;

    /**
     * Constante a nivel de clase que aloja la cadena {@code TEXTO}
     */
    private final String cTexto;

    /**
     * Constante a nivel de clase que aloja la cadena {@code TIPO}
     */
    private final String cTipo;

    /**
     * Constante a nivel de clase que aloja la cadena {@code FORMATO}
     */
    private final String cFormato;

    /**
     * Constante a nivel de clase que aloja la cadena
     * {@code FECHA_VCN_DOC}
     */
    private final String cFechaVcn;

    /**
     * Constante a nivel de clase que aloja la cadena {@code CODIGO}
     */
    private final String cCodigo;

    /**
     * Constante a nivel de clase que aloja el nombre de la constante
     * {@code GeneralParameterEnum.COMPANIA}
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja el nombre de la constante
     * {@code ImpresionPorLotesControladorEnum.COMPROBANTE}
     */
    private final String cComprobante;

    /**
     * Constante a nivel de clase que aloja la cadena {@code ANIO}
     */
    private final String cAnio;

    /**
     * Constante a nivel de clase que aloja la cadena {@code @d30 }
     */
    private final String cD30;

    /**
     * Constante a nivel de clase que aloja la cadena {@code 20310}
     */
    private final String c20310;

    /**
     * Constante a nivel de clase que aloja la cadena {@code 10206}
     */
    private final String c10206;
    /**
     * Constante a nivel de clase que aloja la cadena {@code 10206}
     */
    private Date fechaInicial;
    /**
     * Constante a nivel de clase que aloja la cadena {@code 10206}
     */
    private Date fechaFinal;
    
    /**
     * Constante a nivel de clase que aloja la cadena {@code 10206}
     */
    private Boolean manejaFecha;

    // <DECLARAR_ATRIBUTOS>
    private String numeroInicial;
    private String numeroFinal;
    private String tipo;
    private String claseContable;
    private int anio = SysmanFunciones.getParteFecha(
                    new Date(),
                    Calendar.YEAR);
    private StreamedContent archivoDescarga;
    private String informe;

    /**
     * Atributo que gestiona el formato a utilizar para generar el
     * reporte
     */
    private String formato;
    private boolean visibleDialogo;
    private String textoDialogo = "";
    private StringBuilder strPlano;
    private String tituloPlano = "";
    private String sesionuser;
    private String NomCompania;
    private String NitCompania;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaAno;
    private List<Registro> listaComprobantes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaNumeroInicial;
    private RegistroDataModelImpl listaNumeroFinal;
    private RegistroDataModelImpl listaTipo;
    private ComprobantesContPresReporteador comprobantesContPresReporteador;
    
	private RegistroDataModelImpl listaListaPlantillas;

	private boolean visiblePresentarPlantillas;
	
	private Boolean visibleListaPlantillas = false;
	
	private String  plantilla;
	private String nombrePlantilla;
	private Date fechaPlantilla;
	
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ImpresionPorLotesControlador
     */
    public ImpresionPorLotesControlador() {
        super();

        compania = SessionUtil.getCompania();
        menuActual = SessionUtil.getMenuActual();
        modulo = SessionUtil.getModulo();
        sesionuser = SessionUtil.getUser().getNombre1();
        NomCompania = SessionUtil.getCompaniaIngreso().getNombre();
        NitCompania = SessionUtil.getCompaniaIngreso().getNit();
        cNumero = "NUMERO";
        cNumeroIni = ImpresionPorLotesControladorEnum.NUMEROINI.getValue();
        cNumeroFin = ImpresionPorLotesControladorEnum.NUMEROFIN.getValue();
        cNombre = "NOMBRE";
        cVlrGirar = "VLRAGIRAR";
        cTexto = "TEXTO";
        cTipo = ImpresionPorLotesControladorEnum.TIPO.getValue();
        cFormato = "FORMATO";
        cFechaVcn = "FECHA_VCN_DOC";
        cCodigo = "CODIGO";
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cComprobante = ImpresionPorLotesControladorEnum.COMPROBANTE.getValue();
        cAnio = ImpresionPorLotesControladorEnum.ANIO.getValue();
        cD30 = "@d30 ";
        c20310 = "20310";
        c10206 = "10206";
        fechaFinal = new Date();
        

        try {
            numFormulario = GeneralCodigoFormaEnum.IMPRESION_POR_LOTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ImpresionPorLotesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaTipo();
        cargarListaAno();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        
        if (SessionUtil.getSessionVar("variablesFormulario") != null) {
        HashMap<String, String> parametrosFormulario = (HashMap<String, String>) SessionUtil.getSessionVar("variablesFormulario");

            tipo = parametrosFormulario.get("tipoCpte")
                            .toString().replace("'", "");
            numeroInicial = parametrosFormulario.get("numeroInicial")
            		.toString();
            numeroFinal = parametrosFormulario.get("numeroFinal")
            		.toString();
            formato = parametrosFormulario.get("formato")
            		.toString();
            informe = parametrosFormulario.get("formato")
            		.toString();
            cargarListaListaPlantillas();
            SessionUtil.setSessionVar("variablesFormulario", null);
            
            visibleListaPlantillas = false;
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	try {
        comprobantesContPresReporteador = new ComprobantesContPresReporteador(
                        ejbSysmanUtil);
        strPlano = new StringBuilder();
        strPlano.append("");
        
        fechaInicial = SysmanFunciones.convertirAFecha("01/01/" + String.valueOf(SysmanFunciones.ano(new Date())));
        fechaFinal = SysmanFunciones.convertirAFecha("01/01/" + String.valueOf(SysmanFunciones.ano(new Date())));
			manejaFecha = (ejbSysmanUtil.consultarParametro(compania,"MANEJA IMPRESION POR LOTES POR RANGO DE FECHAS","1", new Date(), true)).equals("SI") ? true : false;
		} catch (SystemException | ParseException e) {
			e.printStackTrace();
		}
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipo() {
        // Contabilidad o Tesoreria
        if (c10206.equals(menuActual)
            || c20310.equals(menuActual)) {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ImpresionPorLotesControladorUrlEnum.URL6255
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();

            param.put(cCompania, compania);

            listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigo);

        }
        // Presupuesto
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ImpresionPorLotesControladorUrlEnum.URL7022
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();

            param.put(cCompania, compania);

            listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigo);

        }
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpresionPorLotesControladorUrlEnum.URL7218
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
	public void cargarListaListaPlantillas() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.MODULO.getName(), modulo);
		param.put(GeneralParameterEnum.TIPO.getName(), tipo);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL104080.getValue());
		
		
		listaListaPlantillas = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
		
		visibleListaPlantillas = listaListaPlantillas==null?false:true;
	}

    public void cargarListaNumeroInicial() {
    	
		 UrlBean urlBean;
		 Map<String, Object> param;

        // Contabilidad o Tesoreria
        if (c10206.equals(menuActual)
            || c20310.equals(menuActual)) {
        	
        	if(manejaFecha) {
        		
        		urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionPorLotesControladorUrlEnum.URL190801
                                                        .getValue());
		          param = new TreeMap<>();
		          param.put(cCompania, compania);
		          param.put(cTipo, tipo);
		          try {
						param.put("FECHAI", SysmanFunciones.convertirAFechaCadena(fechaInicial,"dd/MM/yyyy"));
						param.put("FECHAF", SysmanFunciones.convertirAFechaCadena(fechaFinal,"dd/MM/yyyy"));
			        } catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
        	}else {
        		
        		  urlBean = UrlServiceUtil.getInstance()
                          .getUrlServiceByUrlByEnumID(
                                          ImpresionPorLotesControladorUrlEnum.URL8338
                                                          .getValue());
		          param = new TreeMap<>();
		          param.put(cCompania, compania);
		          param.put(cAnio, anio);
		          param.put(cTipo, tipo);
        		
        	}
          

        }
        // Presupuesto
        else {
        	
        	
        	if(manejaFecha) {
           
        		urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionPorLotesControladorUrlEnum.URL190803
                                                        .getValue());
		        param = new TreeMap<>();
		        param.put(cCompania, compania);
		        param.put(cTipo, tipo);
		        try {
					param.put("FECHAI", SysmanFunciones.convertirAFechaCadena(fechaInicial,"dd/MM/yyyy"));
					param.put("FECHAF", SysmanFunciones.convertirAFechaCadena(fechaFinal,"dd/MM/yyyy"));
		        } catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
       
        	}else {
        		
		        urlBean = UrlServiceUtil.getInstance()
		                         .getUrlServiceByUrlByEnumID(
		                                         ImpresionPorLotesControladorUrlEnum.URL9218
		                                                         .getValue());
		         param = new TreeMap<>();
		         param.put(cCompania, compania);
		         param.put(cAnio, anio);
		         param.put(cTipo, tipo);
        	}
        	
           

        }
         
        listaNumeroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, cNumero);
    	
    }

    public void cargarListaNumeroFinal() {
    	
    	 UrlBean urlBean;
		 Map<String, Object> param;
		 
        // Contabilidad o Tesorer�a
        if (c10206.equals(menuActual)
            || c20310.equals(menuActual)) {
        	
        	if(manejaFecha) {
        		
        		urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionPorLotesControladorUrlEnum.URL190802
                                                        .getValue());

		        param = new TreeMap<>();
		
		        param.put(cNumeroIni, numeroInicial);
		        param.put(cTipo, tipo);
		        param.put(cCompania, compania);
		        try {
					param.put("FECHAI", SysmanFunciones.convertirAFechaCadena(fechaInicial,"dd/MM/yyyy"));
					param.put("FECHAF", SysmanFunciones.convertirAFechaCadena(fechaFinal,"dd/MM/yyyy"));
		        } catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			      
		        
        	}else {
        		
        		urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionPorLotesControladorUrlEnum.URL10386
                                                        .getValue());

		        param = new TreeMap<>();
		
		        param.put(cNumeroIni, numeroInicial);
		        param.put(cTipo, tipo);
		        param.put(cCompania, compania);
		        param.put(cAnio, anio);
        
        	}

        }
        // Presupuesto
        else {
        	if(manejaFecha) {
        		
	        	urlBean = UrlServiceUtil.getInstance()
	                         .getUrlServiceByUrlByEnumID(
	                                         ImpresionPorLotesControladorUrlEnum.URL190804
	                                                         .getValue());
	
		         param = new TreeMap<>();
		
		         param.put(cCompania, compania);
		         param.put(cTipo, tipo);
		         param.put(cNumeroIni, numeroInicial);
		         try {
						param.put("FECHAI", SysmanFunciones.convertirAFechaCadena(fechaInicial,"dd/MM/yyyy"));
						param.put("FECHAF", SysmanFunciones.convertirAFechaCadena(fechaFinal,"dd/MM/yyyy"));
			        } catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
        		
        	}else {
        		
	            urlBean = UrlServiceUtil.getInstance()
	                            .getUrlServiceByUrlByEnumID(
	                                            ImpresionPorLotesControladorUrlEnum.URL11250
	                                                            .getValue());
	
	            param = new TreeMap<>();
	
	            param.put(cCompania, compania);
	            param.put(cAnio, anio);
	            param.put(cTipo, tipo);
	            param.put(cNumeroIni, numeroInicial);

        	}

        }
        
        
        listaNumeroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, cNumero);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void aceptarCONFIRMACION() {
        try {
            HashMap<String, Object> parSet = new HashMap<>();
            parSet.put(ImpresionPorLotesControladorEnum.IMPRESO.getValue(), -1);

            Parameter parametro = new Parameter();
            parametro.setFields(parSet);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ImpresionPorLotesControladorUrlEnum.URL0005
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parametro);
        }
        catch (SystemException e) {
            Logger.getLogger(ImpresionPorLotesControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

	public void oprimirPresentarPlantillas() {
		// <CODIGO_DESARROLLADO>
		if (plantilla == null) {
			oprimirImprimirPdf();
		}else {
			generarPdfdesdeWord();
		}
		// </CODIGO_DESARROLLADO>
	}
	
	private void generarPdfdesdeWord() {
//		 TODO Auto-generated method stub
		 Map<String, Object> param = new HashMap<>();
	        param.put("s$compania$s", compania);
	        param.put("s$usuario$s", SessionUtil.getUser().getCodigo());
	        Map<String, Object> camposValores = new HashMap<>();

	        camposValores.put("codigoPlantilla", plantilla);
	        camposValores.put("fechaPlantilla", SysmanFunciones.formatearFecha(fechaPlantilla));
	        camposValores.put("nombreDocDescarga", nombrePlantilla);

	        HashMap<String, String> variablesFormulario = new HashMap<>();
	        variablesFormulario.put("tipoCpte", "'" + tipo + "'");
	        variablesFormulario.put("numeroInicial",numeroInicial);
	        variablesFormulario.put("numeroFinal",numeroFinal);
	        variablesFormulario.put("formato",formato);
	        
	        
	        HashMap<String, String> variablesConsultaW = new HashMap<>();
	        variablesConsultaW.put("s$compania$s", "'" + compania + "'");
	        variablesConsultaW.put("s$ano$s", String.valueOf(anio));
	        variablesConsultaW.put("s$tipo$s",  "'" +  tipo + "'");
	        variablesConsultaW.put("s$numeroIni$s",numeroInicial);
	        variablesConsultaW.put("s$numeroFin$s",numeroFinal);
	        // variables por parametro para documento word
	        SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);
	        SessionUtil.setSessionVar("variablesFormulario", variablesFormulario);
	        SessionUtil.setFlash(camposValores);

            SessionUtil.redireccionarFormularioRetorno("30412","281","3","30412","702","3",true);


	}
	
    public void oprimirImprimirExcel() {
        // <CODIGO_DESARROLLADO>
        /**
         **************************** IMPORTANTE ***************************
         *
         * La función "RetencionesXcomprobante" que se encuentra
         * defnida en Access no se migra debido a
         *
         * que realiza la copia de los datos a una tabla temporal
         * llamada "tmpretenciones" que no está
         *
         * siendo llamada en ningun proceso ni reporte relacionado con
         * el formulario. *
         */
        try {
            if (comprobantesContPresReporteador.parametroTxt()) {
                generaPlanoPuntosCNT();
            }
            generaInforme(ReportesBean.FORMATOS.EXCEL);
        }
        catch (IOException e) {
            Logger.getLogger(ImpresionPorLotesControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimirPdf() {
        // <CODIGO_DESARROLLADO>
        try {
            if (comprobantesContPresReporteador.parametroTxt()) {
                generaPlanoPuntosCNT();
            }
            generaInforme(ReportesBean.FORMATOS.PDF);
        }
        catch (IOException e) {
            Logger.getLogger(ImpresionPorLotesControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void generaInforme(FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();

            Map<String, Object> reemplazar = new HashMap<>();

            if(informe.equals("002524RECIBODECAJAIDCBIS")) {
            	if(manejaFecha) {
            		reemplazar.put("fechaInicial", fechaInicial);
            		reemplazar.put("fechaFinal", fechaFinal);            		
            	}
				reemplazar.put("tipoComprobante", tipo);
				reemplazar.put("compania", compania);
				reemplazar.put("ano", anio);
				reemplazar.put("comprobanteInicial", numeroInicial);
				reemplazar.put("comprobanteFinal", numeroFinal);
				
				parametros.put("PR_NOMBRECOMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
				parametros.put("PR_NITCOMPANIA",SessionUtil.getCompaniaIngreso().getNit());
				parametros.put("PR_DIRECCION",SessionUtil.getCompaniaIngreso().getDireccion());
            	parametros.put("PR_TELEFONO",SessionUtil.getCompaniaIngreso().getTelefono());
            	
            	Reporteador.resuelveConsulta(informe,//"002524RECIBODECAJAIDCBIS",
            		69, reemplazar, parametros);

            	archivoDescarga = JsfUtil.exportarStreamed(informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);	
            }else {
            	if(manejaFecha) {
            		reemplazar.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
            		reemplazar.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));  
            		//JM INI 05/02/2025 CC649
            		if(informe.equals("002562CDP_SINCHI")) {
            			informe = "002735CDP_SINCHI_LOTE";
            		}
            		if(informe.equals("002563RES_SINCHI")) {
            			informe = "002736RES_SINCHI_LOTE";
            		}	
            		//JM FIN 05/02/2025 CC649
            	}
            // reemplazar.put("compania", "'" + compania + "'");
            reemplazar.put("ano", anio);
            reemplazar.put("tipoCpte", tipo);
            reemplazar.put("fecha", new Date());
            reemplazar.put("numeroPptoInicial", numeroInicial);
            reemplazar.put("numeroPptoFinal", numeroFinal);
            reemplazar.put("centroCosto", SysmanConstantes.CONS_CENTRO);

            Map<String, Object> valores = new HashMap<>();
            valores.put("informe", informe);
            valores.put("formato", formato);
            valores.put("strPlano", strPlano);
            valores.put("tituloPlano", tituloPlano);
            valores.put("formatoNombre", this.formato);
            valores.put("lote", true);
            String sql = "001892SubCOM";
            String sql1 = "001891SubCOMCAQ";
            parametros.put("PR_STRSQL_INFORMESUBCOM_DC", sql1);
            parametros.put("PR_STRSQL_COMRETENCIONES", sql);
            parametros.put("PR_NOMBRESESION", sesionuser);
            parametros.put("PR_NOMBRECOMPANIA", NomCompania);
            parametros.put("PR_NITCOMPANIA", NitCompania);
            parametros.put("PR_ANO", anio + "");
            parametros.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE JEFE DE CONTABILIDAD",
                                            modulo, new Date(), true));
            parametros.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO JEFE DE CONTABILIDAD",
                                            modulo, new Date(), true));
            parametros.put("PR_CARGO_1_COMPROBANTE_CONTABLE",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO1 COMPROBANTE CONTABLE",
                                            modulo, new Date(), true));
            parametros.put("PR_NOMBRE_COLUMNA_FUENTE",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE COLUMNA FUENTE", modulo,
                                            new Date(), true));
            parametros.put("PR_NOMBRE_COLUMNA_CENTRO_COSTO",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE COLUMNA CENTRO COSTO",
                                            modulo, new Date(), true));
            parametros.put("PR_NOMBRE_COLUMNA_AUXILIAR_GENERAL",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE COLUMNA AUXILIAR GENERAL",
                                            modulo, new Date(), true));
            parametros.put("PR_NOMBRE_COLUMNA_REFERENCIA",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE COLUMNA REFERENCIA", modulo,
                                            new Date(), true));
            parametros.put("PR_NOMBRE_COLUMNA_COD_PPTAL",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE COLUMNA COD PPTAL", modulo,
                                            new Date(), true));
            parametros.put("PR_NOMBRE_SECRETARIA_HACIENDA",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DE SECRETARIA DE HACIENDA",
                                            modulo, new Date(), true));
            parametros.put("PR_NOMBRE_JEFE_PRESUPUESTO",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DE JEFE DE PRESUPUESTO",
                                            modulo, new Date(), true));
           
            parametros.put("PR_CARGO_JEFE_PRESUPUESTO",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO PRESUPUESTO",
                                            modulo, new Date(), true));
            parametros.put("PR_CARGO_SECRETARIA_HACIENDA",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO DE SECRETARIA DE HACIENDA",
                                            modulo, new Date(), true));
            
            parametros.put("PR_TEXTO_VENCIMIENTO_FORMATO_CDP",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "TEXTO DE VENCIMIENTO EN FORMATO CDP",
                                    modulo, new Date(), true));
            
          //--INI_7709352 _(22/04/2022 mrosero)_CONTABILIDAD1
            parametros.put("PR_CARGO_FINANCIERO",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO FINANCIERO",
                                    modulo, new Date(), true));
            
            parametros.put("PR_NOMBRE_FINANCIERO",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "NOMBRE FINANCIERO",
                                    modulo, new Date(), true));
            
            parametros.put("PR_CARGO_TESORERO",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO TESORERO",
                                    modulo, new Date(), true));
            
            parametros.put("PR_NOMBRE_TESORERO",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "NOMBRE TESORERO",
                                    modulo, new Date(), true));
            
            parametros.put("PR_RESOLUCION_EN_FORMATO_DE_EGRESO",
                    ejbSysmanUtil.consultarParametro(compania,
                                    "RESOLUCION EN FORMATO DE EGRESO",
                                    modulo, new Date(), true));
            
			parametros.put("PR_CARGO_DIRECTOR_EJECUTIVO",
					ejbSysmanUtil.consultarParametro(compania,
									"NOMBRE DEL CARGO DIRECTOR EJECUTIVO", 
									modulo, new Date(), true));
            
            
          //--FIN_7709352 _(22/04/2022 mrosero)_CONTABILIDAD1
            
            String observacionFormatos = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "OBSERVACION EN FORMATOS DIS_CB Y CB_REG",
                                            modulo,
                                            new Date(), true),
                            "OBSERVACION EN FORMATOS DIS_CB Y CB_REG");

            String cargoDivision = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO DIVISION FINANCIERA Y DE PRESUPUESTO",
                                            modulo,
                                            new Date(), true),
                            "CARGO DIVISION FINANCIERA Y DE PRESUPUESTO");

            String seccionPrincipal = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "PN SECCION PRINCIPAL",
                                            modulo,
                                            new Date(), true),
                            "PN SECCION PRINCIPAL");

            String unidadEjecutora = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "PN UNIDAD EJECUTORA",
                                            modulo,
                                            new Date(), true),
                            "PN UNIDAD EJECUTORA");

            String decretoComp = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "DECRETO DEL COMPROMISO",
                                            modulo,
                                            new Date(), true),
                            "DECRETO DEL COMPROMISO");

            String elaboroPresupuesto = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "ELABORO EN PRESUPUESTO",
                                            modulo,
                                            new Date(), true),
                            "ELABORO EN PRESUPUESTO");
            
            String cargoPresupuestoDis = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO PRESUPUESTO DISPONIBILIDAD",
                                    modulo,
                                    new Date(), true),
                    "CARGO PRESUPUESTO DISPONIBILIDAD");
            
            String tituloCompania = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "TITULO COMPANIA",
                                    modulo,
                                    new Date(), true),
                    "TITULO COMPANIA");
            
            
            String responsablesPresupuesto = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "RESPONSABLES DE PRESUPUESTO",
                                    modulo,
                                    new Date(), true),
                    "RESPONSABLES DE PRESUPUESTO");
            
            String cargo1Pto = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO 1 EN PRESUPUESTO",
                                    modulo,
                                    new Date(), true)," ");    
            
            String cargoPr0028 = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO PRESUPUESTO EN FORMATO 002887DISCOS",
                                    modulo,
                                    new Date(), true)," ");

            parametros.put("PR_OBSERVACION_EN_FORMATOS_DIS_CB_Y_CB_REG",
                            observacionFormatos);
            parametros.put("PR_CARGO_DIVISION_FINANCIERA_Y_DE_PRESUPUESTO",
                            cargoDivision);
            parametros.put("PR_SECCION_PRINCIPAL", seccionPrincipal);
            parametros.put("PR_UNIDAD_EJECUTORA", unidadEjecutora);
            parametros.put("PR_DECRETO_DEL_COMPROMISO", decretoComp);
            parametros.put("PR_ELABORO_EN_PRESUPUESTO", elaboroPresupuesto);
            parametros.put("PR_CARGO_PRESUPUESTO_DISPONIBILIDAD",
            		cargoPresupuestoDis);   
            parametros.put("PR_TITULO_COMPANIA",
            		tituloCompania);
            parametros.put("PR_RESPONSABLES_DE_PRESUPUESTO",
            		responsablesPresupuesto);
            parametros.put("PR_CARGO_1_EN_PRESUPUESTO",cargo1Pto);
            parametros.put("PR_CARGO_PRESUPUESTO0028",cargoPr0028);

    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
    		        "EEEE, d 'de' MMMM 'de' yyyy",
    		        new Locale("es", "CO"));

    		parametros.put("PR_FECHA_ACTUAL", LocalDate.now().format(formatter));

            archivoDescarga = comprobantesContPresReporteador
                            .generarInforme(valores, parametros, reemplazar);
          }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Verifica el valor del campo {@code var} y asigna un espacio en
     * caso de tener valor nulo.
     *
     * @param parReg
     * Registro que contiene los campos.
     * @param var
     * Nombre del campo.
     * @return
     */
    private String asignarValor(Registro parReg, String var) {
        if (SysmanFunciones.validarCampoVacio(parReg.getCampos(), var)) {
            return "";
        }

        return parReg.getCampos().get(var).toString();
    }

    private void procesarDetalleComprobanteCredito(Registro rs1) {
        if (rs1 != null) {
            String rs1CuentaPPTAL = asignarValor(rs1,
                            "CUENTAPPTAL");

            String col11 = "";
            // OBTIENE EL NOMBRE DE eSA CUENTAPPTAL
            if (!rs1CuentaPPTAL.isEmpty()) {
                Map<String, Object> param = new TreeMap<>();

                param.put(cCompania, compania);
                param.put(cAnio, anio);
                param.put(cCodigo, rs1CuentaPPTAL);

                try {
                    Registro rsAux = RegistroConverter
                                    .toRegistro(requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    ImpresionPorLotesControladorUrlEnum.URL0001
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));

                    col11 = rsAux == null ? " "
                        : asignarValor(rsAux, cNombre);
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }

            strPlano.append("\r\n");
            strPlano.append("@co08 0\r\n");
            strPlano.append("@co09 0\r\n");
            strPlano.append("@co10 " + rs1CuentaPPTAL + "\r\n");
            strPlano.append("@co11 " + col11 + "\r\n");
            strPlano.append("@co12 ");

            strPlano.append(asignarValor(rs1, "VALOR_CREDITO"));

            strPlano.append("\r\n");

        }
    }

    /**
     * Por cada comprobante ejecuta un proceso.
     *
     * @param i
     * Identifica el comprobante en la lista de comprobantes
     * {@code listaComprobantes}
     */
    private void procesarItemComprobante(int i) {
        strPlano.append("InIcIo\r\n");

        Registro rs = null;

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(cCompania, compania);
            param.put(cAnio, anio);
            param.put(cTipo, tipo);
            param.put(cNumero, asignarValor(listaComprobantes.get(i), cNumero));

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpresionPorLotesControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        if (rs != null) {
            String tercero = asignarValor(rs, "TERCERO");

            String rsAnio = asignarValor(rs, "ANO");

            String rsMes = asignarValor(rs, "MES");

            String rsDia = asignarValor(rs, "DIA");

            String rsAnioFv = SysmanFunciones
                            .nvlStr(String.valueOf(SysmanFunciones
                                            .ano((Date) rs.getCampos()
                                                            .get(cFechaVcn))),
                                            "");

            String rsMesFv = SysmanFunciones.nvlStr(String.valueOf(
                            SysmanFunciones.mes((Date) rs.getCampos()
                                            .get(cFechaVcn))),
                            "");

            String rsDiaFv = SysmanFunciones.nvlStr(String.valueOf(
                            SysmanFunciones.dia((Date) rs.getCampos()
                                            .get(cFechaVcn))),
                            "");

            String rsTexto = asignarValor(rs, cTexto);

            String rsNombre = asignarValor(rs, cNombre);

            String rsDireccion = asignarValor(rs, "DIRECCION");

            try {

                String rta = String.valueOf(ejbSysmanUtil
                                .generarDigitoDeVerificacion(tercero));

                strPlano.append("@p01 " + asignarValor(rs, cNumero)
                    + "\r\n");

                strPlano.append("@p02 " + rsTexto + "\r\n");
                strPlano.append("@p03 NIT-" + tercero + rta + "\r\n");

                strPlano.append("@p05 "
                    + SysmanFunciones.padl(rsDia, 2, "0") + " "
                    + SysmanFunciones.padl(rsMes, 2, "0")
                    + " " + SysmanFunciones.padl(rsAnio, 4, "0")
                    + "\r\n");

                strPlano.append("@p06 "
                    + SysmanFunciones.padl(rsDiaFv, 2, "0") + " "
                    + SysmanFunciones.padl(rsMesFv, 2, "0") + " "
                    + SysmanFunciones.padl(rsAnioFv, 4, "0")
                    + "\r\n");

                strPlano.append("@p10 " + rsNombre + "\r\n");
                strPlano.append("@p11 "
                    + asignarValor(rs, "NIT") + "\r\n");

                strPlano.append("@p12 " + rsDireccion + "\r\n");
            }
            catch (SystemException e) {
                Logger.getLogger(ImpresionPorLotesControlador.class
                                .getName()).log(Level.SEVERE, null, e);

                JsfUtil.agregarMensajeError(e.getMessage());

            }

            Registro rs1 = null;

            Map<String, Object> param = new TreeMap<>();
            param.put(cCompania, compania);
            param.put(cAnio, anio);
            param.put(cTipo, tipo);
            param.put(cComprobante, asignarValor(listaComprobantes.get(i),
                            cNumero));

            try {
                rs1 = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                ImpresionPorLotesControladorUrlEnum.URL0003
                                                                                .getValue())
                                                .getUrl(),
                                                param));

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            procesarDetalleComprobanteCredito(rs1);

            String strFecha = "";

            if (rs.getCampos().get(cFechaVcn) != null) {
                strFecha = SysmanFunciones.padl(rsDiaFv, 2, "0") + " "
                    + SysmanFunciones.padl(rsMesFv, 2, "0") + " "
                    + SysmanFunciones.padl(rsAnioFv, 4, "0") + "\r\n";
            }

            strPlano.append("\r\n");

            strPlano.append("@p15 "
                + SysmanFunciones.moneda(Double.parseDouble(
                                asignarValor(rs, cVlrGirar)),
                                2)
                + "PESOS MC.\r\n");

            strPlano.append("@p20 " + asignarValor(rs, cVlrGirar)
                + "\r\n");

            strPlano.append("@p21 0.00\r\n");
            strPlano.append("@p22 0.00\r\n");
            strPlano.append("@p25 " + asignarValor(rs, cVlrGirar)
                + "\r\n");

            strPlano.append(cD30
                + (nvl(rs.getCampos().get(cTexto), ".").toString()
                                .length() > 100
                                    ? asignarValor(rs, cTexto)
                                                    .substring(0, 99)
                                    : nvl(rs.getCampos().get(cTexto),
                                                    ".").toString())
                + "\r\n");

            strPlano.append(cD30
                + (nvl(rs.getCampos().get(cTexto), ".").toString()
                                .length() > 200
                                    ? asignarValor(rs, cTexto)
                                                    .substring(100, 199)
                                    : nvl(rs.getCampos().get(cTexto),
                                                    ".").toString())
                + "\r\n");

            strPlano.append(cD30
                + (nvl(rs.getCampos().get(cTexto), ".").toString()
                                .length() > 300
                                    ? asignarValor(rs, cTexto)
                                                    .substring(200, 299)
                                    : nvl(rs.getCampos().get(cTexto),
                                                    ".").toString())
                + "\r\n");

            strPlano.append("\r\n@p100 " + asignarValor(rs, cNumero)
                + "\r\n");

            strPlano.append("@p101 "
                + asignarValor(rs, cVlrGirar)
                + "\r\n");

            strPlano.append("@p102 " + strFecha + "\r\n");

            procesarDetallePlan(rs1, param);
        }
    }

    private void procesarDetallePlan(Registro rs1, Map<String, Object> param) {
        Registro rs2 = null;

        try {
            rs2 = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpresionPorLotesControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        String cuentaPPTAL = "";
        String cuentaDeCaja = "";
        String nombreCuentaCnt = "";
        String valor = "";

        if (rs1 != null && rs2 != null) {
            cuentaPPTAL = asignarValor(rs1, "CUENTAPPTAL");

            cuentaDeCaja = asignarValor(rs2, "CUENTA");

            nombreCuentaCnt = asignarValor(rs2, cNombre);

            valor = asignarValor(rs2, "VALOR_DEBITO");
        }

        strPlano.append("\r\n");
        strPlano.append("@co01 " + cuentaPPTAL + "\r\n");
        strPlano.append("@co02 " + cuentaDeCaja + "\r\n");
        strPlano.append("@co03 " + nombreCuentaCnt + "\r\n");
        strPlano.append("@co04 " + valor + "\r\n");
        strPlano.append("@co05 00.00\r\n");
        strPlano.append("FinDeDocumento\r\n\r\n");
    }

    /**
     * Genera un archivo Plano
     */
    public void generaPlanoPuntosCNT() throws IOException {
        Map<String, Object> param = new TreeMap<>();

        param.put(cCompania, compania);
        param.put(cAnio, anio);
        param.put(cTipo, tipo);
        param.put(cNumeroIni, numeroInicial);
        param.put(cNumeroFin, numeroFinal);

        try {
            listaComprobantes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpresionPorLotesControladorUrlEnum.URL36874
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        strPlano = new StringBuilder();
        strPlano.append("");

        if (!listaComprobantes.isEmpty()) {

            for (int i = 0; i < listaComprobantes.size(); i++) {
                procesarItemComprobante(i);
            }

            tituloPlano = SysmanFunciones.concatenar("INGRESOS_", tipo, "_",
                            numeroInicial, "_AL_", numeroFinal, ".txt");
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        if (anio != 0) {
            numeroInicial = "";
            numeroFinal = "";
            cargarListaNumeroInicial();
        }
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarFecha() {
    	numeroInicial = "";
        numeroFinal = "";
    	cargarListaNumeroInicial();
    }

	public void seleccionarFilaListaPlantillas(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		plantilla = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
        nombrePlantilla =  SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
        
        visiblePresentarPlantillas = plantilla==null?false:true;
	}
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipo = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        informe = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cFormato), "")
                        .toString();

        formato = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cFormato), "")
                        .toString();

        // Contabilidad o Tesoreria
        if (c10206.equals(menuActual)
            || c20310.equals(menuActual)) {
            informe = SysmanFunciones
                            .nvl(registroAux.getCampos().get(cFormato),
                                            "000722CDC")
                            .toString();

            claseContable = registroAux.getCampos().get("CLASE_CONTABLE")
                            .toString();
        }
        // Presupuesto
        else {
            informe = SysmanFunciones
                            .nvl(registroAux.getCampos().get(cFormato),
                                            "000958CDP")
                            .toString();
        }

        numeroInicial = "";
        numeroFinal = "";
        cargarListaNumeroInicial();
		cargarListaListaPlantillas();

    }

    public void seleccionarFilaNumeroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        numeroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNumero), "")
                        .toString();

        numeroFinal = "";

        cargarListaNumeroFinal();
    }

    public void seleccionarFilaNumeroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        numeroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNumero), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getTipo() {
        return tipo;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getClaseContable() {
        return claseContable;
    }

    public void setClaseContable(String claseContable) {
        this.claseContable = claseContable;
    }

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    public String getTextoDialogo() {
        return textoDialogo;
    }

    public void setTextoDialogo(String textoDialogo) {
        this.textoDialogo = textoDialogo;
    }

    public String getTituloPlano() {
        return tituloPlano;
    }

    public void setTituloPlano(String tituloPlano) {
        this.tituloPlano = tituloPlano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaComprobantes() {
        return listaComprobantes;
    }

    public void setListaComprobantes(List<Registro> listaComprobantes) {
        this.listaComprobantes = listaComprobantes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public RegistroDataModelImpl getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    public void setListaNumeroFinal(RegistroDataModelImpl listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }

    public void setListaNumeroInicial(
        RegistroDataModelImpl listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

	/**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * @return the manejaFecha
	 */
	public Boolean getManejaFecha() {
		return manejaFecha;
	}

	/**
	 * @param manejaFecha the manejaFecha to set
	 */
	public void setManejaFecha(Boolean manejaFecha) {
		this.manejaFecha = manejaFecha;
	}

    
	/**
	 * @return the listaListaPlantillas
	 */
	public RegistroDataModelImpl getListaListaPlantillas() {
		return listaListaPlantillas;
	}

	/**
	 * @param listaListaPlantillas the listaListaPlantillas to set
	 */
	public void setListaListaPlantillas(RegistroDataModelImpl listaListaPlantillas) {
		this.listaListaPlantillas = listaListaPlantillas;
	}

	/**
	 * @return the plantilla
	 */
	public String getPlantilla() {
		return plantilla;
	}

	/**
	 * @param plantilla the plantilla to set
	 */
	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	/**
	 * @return the visiblePresentarPlantillas
	 */
	public boolean isVisiblePresentarPlantillas() {
		return visiblePresentarPlantillas;
	}

	/**
	 * @param visiblePresentarPlantillas the visiblePresentarPlantillas to set
	 */
	public void setVisiblePresentarPlantillas(boolean visiblePresentarPlantillas) {
		this.visiblePresentarPlantillas = visiblePresentarPlantillas;
	}

	/**
	 * @return the visibleListaPlantillas
	 */
	public Boolean getVisibleListaPlantillas() {
		return visibleListaPlantillas;
	}

	/**
	 * @param visibleListaPlantillas the visibleListaPlantillas to set
	 */
	public void setVisibleListaPlantillas(Boolean visibleListaPlantillas) {
		this.visibleListaPlantillas = visibleListaPlantillas;
	}
	
}
