package com.sysman.general;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ImprimirwordsControladorEnum;
import com.sysman.general.enums.ImprimirwordsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Formulario;
import com.sysman.plantillas.UtilitarioPlantillas;
import com.sysman.plantillas.enums.PlantillasEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;
import com.sysman.util.rest.APIPdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Impresi&oacute;n de plantillas Word/Excel.
 * 
 * @author jacelas
 * @version 1, 15/10/2015 -- Modificado por: dmaldonado 03/06/2016
 * 
 * @author amonroy
 * @version 2, 05/04/2017 Proceso de Refactoring y Revision de buenas practicas
 * SonarLint
 * 
 * @author jreina
 * @version 3, 11/08/2017 Se ajusto el controlador para que sea posible la
 * edicion de las variables de usuario en el formulario.
 * 
 * @author jrodrigueza
 * @version 4, 22/02/2018 Implementación de impresi&oacute;n de plantillas
 * Excel.
 * 
 * @author jrodrigueza
 * @version 5, 05/06/2018 Implementación de utilitario de plantillas.
 */
@ManagedBean
@ViewScoped
public class ImprimirwordsControlador {

    // <DECLARAR_ATRIBUTOS>
    /**
     * Etiqueta valor.
     */
    private String valorEtiqueta;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Objeto para el registro de mensajes.
     */
    private final Log logger = LogFactory.getLog(this.getClass());
    /**
     * N&uacute;mero de formulario
     */
    private int numFormulario;
    /**
     * Permisos concedidos al formulario.
     */
    private boolean[] permisos;
    /**
     * Recurso para acceder al properties de idiomas.
     */
    private ResourceBundle idioma;
    /**
     * Administra peticiones REST.
     */
    private RequestManager requestManager;
    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_PARAMETROS>
    /**
     * C&oacute;digo de plantilla.
     */
    private String codigoPlantilla;
    /**
     * Atributo que almacena el valor de fechaPlantilla que se recibe por
     * parámetro sin el formateo de fecha que se utiliza en Oracle
     */
    private String fechaParametro;
    /**
     * Fecha de plantilla.
     */
    private String fechaPlantilla;
    /**
     * Nombre del documento de descarga.
     */
    private String nombreDocDescarga;

    private boolean descargaMasiva;
    private boolean descargaNormal;
    private boolean descargaPdf;

    /**
     * Constante que identifica el servicio que busca la URL y tipo de conexión
     */
    private static final String SERVICIO_API = "1710001";

    // </DECLARAR_PARAMETROS>

    // <DECLARAR_LISTAS>
    /**
     * Lista de variables creadas por el usuario, es decir, variables
     * personalizadas que NO provienen de la consulta de plantilla.
     */
    private List<Registro> listaInicial;
    // </DECLARAR_LISTAS>
    private String compania;
    private String modulo;

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private String tipoCpte;
	private boolean permiteEliminar;
	private static final String CERTIFICADO_CLOUDMERSIVE = "GlobalSign.crt";

    /**
     * Crea una nueva instancia de ImprimirwordsControlador
     */
    public ImprimirwordsControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        listaInicial = new ArrayList<>();
        requestManager = new RequestManager();
        try {
            numFormulario = GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            cargarFlash();
            // </INI_ADICIONAL>
        }
        catch (SysmanException ex) {
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
        // <CARGAR_LISTA>
        cargarLista();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Carga la lista listaInicial con las variables de usuario.
     */
    public void cargarLista() {
        Map<String, Object> param = new TreeMap<>();
        param.put(ImprimirwordsControladorEnum.CODIGOPLANTILLA.getValue(),
                        codigoPlantilla);
        param.put(ImprimirwordsControladorEnum.FECHAPLANTILLA.getValue(),
                        fechaParametro);
        try {
            listaInicial = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            ImprimirwordsControladorUrlEnum.URL10399
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
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
     */
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            if (SessionUtil.getMenuActual().equals("350306")) {
                descargaMasiva = false;
                descargaNormal = true;
                descargaPdf = false;
            }
            else
//                (SessionUtil.getMenuActual().equals("350201")
//                		|| SessionUtil.getMenuActual().equals("52020101")  // 7714837 mperez 17/05/2022
//                		|| SessionUtil.getMenuActual().startsWith("302") // 478 lvega 17/02/2025
//                		|| SessionUtil.getMenuActual().equals("30412") // 2357 lvega 04/09/2025
//                		|| SessionUtil.getMenuActual().startsWith("103") // 478 lvega 10/03/2025
//                		|| SessionUtil.getMenuActual().startsWith("102") // 2357 lvega 04/09/2025
//                		|| SessionUtil.getMenuActual().equals("90352")// 2188 cfbarrera 26/08/2025
//                		|| SessionUtil.getMenuActual().equals("190210") // 7712636 jcrojas 06/07/2022
//                		|| SessionUtil.getMenuActual().equals("690101") // 1982 jcrojas 15/09/2025
//                		|| SessionUtil.getMenuActual().startsWith("202"))// 2357 lvega 12/09/2025
            	if((SessionUtil.getModulo().equals("1")
            			|| SessionUtil.getModulo().equals("3")            	
            			|| SessionUtil.getModulo().equals("9")            	
            			|| SessionUtil.getModulo().equals("19")            	
            			|| SessionUtil.getModulo().equals("69")            	
            			|| SessionUtil.getModulo().equals("35")            	
            			|| SessionUtil.getModulo().equals("52")
            			|| SessionUtil.getModulo().equals("10"))
                	 && SysmanFunciones
		                     .nvl(ejbSysmanUtil.consultarParametro(
		                                     compania,
		                                     "GENERA PLANTILLA PDF",
		                                     modulo,
		                                     new Date(),
		                                     false), "NO").equals("SI")) {
		                                         descargaMasiva = false;
		                                         descargaNormal = false;
		                                         descargaPdf = true;
		                                     }

                else {
                    descargaMasiva = false;
                    descargaNormal = true;
                    descargaPdf = false;
                }
			
			permiteEliminar =SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "PERMITE ELIMINAR DOCUMENTO ORIGEN", modulo, new Date(), false), "NO").equals("SI");
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Acciones que se ejecutan antes de renderizar la forma.
     */
    public void cargarForma() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Validaci&oacute;n de permisos de acceso.
     * 
     * @throws SysmanException
     * en caso de que no existan permisos asociados.
     */
    public void validarPermisos() throws SysmanException {
        if (permisos == null) {
            Formulario form = SessionUtil.cargarFormulario(
                            numFormulario + "," + SessionUtil.getModulo());
            if (form == null) {
                throw new SysmanException(
                                idioma.getString("MSM_PERMISOS_ACCEDER"));
            }
            permisos = form.getPermisos();
            if (permisos == null || !permisos[3]) {
                throw new SysmanException(
                                idioma.getString("MSM_PERMISOS_ACCEDER"));
            }
        }
    }

    /**
     * Captura de las variables enviadas por flash.
     */
    public void cargarFlash() {
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                codigoPlantilla = parametrosEntrada.get("codigoPlantilla")
                                .toString();
                fechaPlantilla = parametrosEntrada.get("fechaPlantilla")
                                .toString();
                nombreDocDescarga = parametrosEntrada.get("nombreDocDescarga")
                                .toString();
                tipoCpte = SysmanFunciones.nvlStr(SysmanFunciones.toString(parametrosEntrada.get("tipoCpte")),"");
                if (SysmanFunciones.esBdSqlServer()) {
                    fechaParametro = fechaPlantilla.substring(13, 23);
                }
                else {
                    fechaParametro = fechaPlantilla.substring(9, 19);
                }
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3050"));
                RequestContext.getCurrentInstance().closeDialog(null);
            }
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * Permite la edición de la lista de variables personalizables.
     * 
     * @param event
     * un evento
     */
    public void editar(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        int indice = listaInicial.indexOf(reg);
        listaInicial.remove(reg);
        reg.getCampos().put(GeneralParameterEnum.VALOR.getName(),
                        reg.getCampos().get(
                                        PlantillasEnum.ETIQUETA.getValue()));
        listaInicial.add(indice, reg);
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_REGISTRO_MODIFICADO"));
    }

    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton BtnGenerarDocumento en la vista.
     */
    @SuppressWarnings("unchecked")
    public void oprimirBtnGenerarDocumento() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        Map<String, String> variablesConsulta = (HashMap<String, String>) SessionUtil
                        .getSessionVar("variablesConsultaWord");
        try {
//            	archivoDescarga = UtilitarioPlantillas.exportarDocumento(
//                        codigoPlantilla, fechaPlantilla, variablesConsulta,
//                        nombreDocDescarga,
//                        listaInicial);

            
            if ("SI".equals(SysmanFunciones
                                    .nvl(ejbSysmanUtil.consultarParametro(
                                    		compania, 
                                    		"LETRA TAMANO 8 PARA TABLAS EN ACTAS", 
                                    		modulo, 
                                    		new Date(),
                                    		false), 
                                    		"SI"))) {
										            	archivoDescarga = UtilitarioPlantillas.exportarDocumento1(
								                        codigoPlantilla, fechaPlantilla, variablesConsulta,
								                        nombreDocDescarga,
								                        listaInicial);
                                                    }

                else {
                	archivoDescarga = UtilitarioPlantillas.exportarDocumento(
                            codigoPlantilla, fechaPlantilla, variablesConsulta,
                            nombreDocDescarga,
                            listaInicial);
                }
            
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton BtnGenerarDocumento en la vista.
     */
    @SuppressWarnings("unchecked")
    public void oprimirBtGenerarMasivo() {

        try {
            archivoDescarga = null;
            int i = 0;
            Map<String, String> variablesConsulta = (HashMap<String, String>) SessionUtil
                            .getSessionVar("variablesConsultaWord");
            Map<String, String> numeroTramite = (HashMap<String, String>) SessionUtil
                            .getSessionVar("numeroTramite");

            if (numeroTramite.isEmpty()) {
                throw new SystemException(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }
            Iterator it = numeroTramite.entrySet().iterator();
            ByteArrayInputStream[] salidas = new ByteArrayInputStream[numeroTramite
                            .size()];
            String[] nombresArchivos = new String[numeroTramite.size()];

            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String entrada = String.valueOf(e.getValue());
                Map<String, Object> paramVar = new HashMap<>();

                variablesConsulta.put("s$tramite$s", entrada);

                salidas[i] = new ByteArrayInputStream(IOUtils.toByteArray(
                                UtilitarioPlantillas.exportarDocumentoMasiva(
                                                codigoPlantilla, fechaPlantilla,
                                                variablesConsulta,
                                                nombreDocDescarga,
                                                listaInicial).getStream()));

                nombresArchivos[i] = nombreDocDescarga + " N° " + entrada
                    + ".docx";
                i++;
            }

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas,
                            nombresArchivos);
        }
        catch (SystemException | JRException | IOException | SQLException
                        | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarPdf en la vista
     *
     */
    @SuppressWarnings("unchecked")
    public void oprimirGenerarPdf() {

        archivoDescarga = null;

        String url;
        Registro rs;
        ByteArrayInputStream respuesta;

        Map<String, String> variablesConsulta = (HashMap<String, String>) SessionUtil
                        .getSessionVar("variablesConsultaWord");
        try {


            String rutaCertificado ="/opt/sysman/CERTIFICADO/CLOUDMERSIVE/";
            rutaCertificado = rutaCertificado+CERTIFICADO_CLOUDMERSIVE;
            
            String rutaPlantilla = null;// 2188 cfbarrera 26/08/2025
            if(!tipoCpte.isEmpty()) {
            	rutaPlantilla = "opt/sysman/data/reportes/General/plantillasword/"+tipoCpte+"/";
            }
            else {
            	rutaPlantilla = "opt/sysman/data/reportes/General/plantillasword/";

            }

          	if ((SessionUtil.getModulo().equals("1")
        			|| SessionUtil.getModulo().equals("3")            	
        			|| SessionUtil.getModulo().equals("9")            	
        			|| SessionUtil.getModulo().equals("19")            	
        			|| SessionUtil.getModulo().equals("69")
        			|| SessionUtil.getModulo().equals("10"))
        			    && "SI".equals(SysmanFunciones
        			                    .nvl(ejbSysmanUtil.consultarParametro(
        			                                    compania,
        			                                    "GENERA PLANTILLA PDF",
        			                                    modulo,
        			                                    new Date(),
        			                                    false), "NO"))) {
        				archivoDescarga = UtilitarioPlantillas.exportarDocumentoAPdf(
                                codigoPlantilla, fechaPlantilla, variablesConsulta,
                                nombreDocDescarga,
                                listaInicial,rutaCertificado,rutaPlantilla, permiteEliminar);        	 
			} else {
			
	            byte[] archivoBytes = IOUtils
                        .toByteArray(UtilitarioPlantillas
                                        .exportarDocumentoMasiva(
                                                        codigoPlantilla,
                                                        fechaPlantilla,
                                                        variablesConsulta,
                                                        nombreDocDescarga,
                                                        listaInicial)
                                        .getStream());
	            
	        String documento = Base64.getEncoder().encodeToString(archivoBytes);
	        
            Map<String, Object> parametros = new TreeMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametros.put(GeneralParameterEnum.CODIGO.getName(),
                            "35");

            RequestManager requestManager = new RequestManager();

            rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getUrlBeanById(SERVICIO_API)
                                            .getUrl(),
                            parametros));

            // SessionUtil.getUser().getCedula();

            if (rs == null) {
                throw new SysmanException(idioma
                                .getString("TB_TB4232"));
            }
            else
                if (rs.getCampos().get(GeneralParameterEnum.URL.getName())
                                .toString() == null) {
                                    throw new SysmanException(idioma
                                                    .getString("TB_TB4231"));
                                }
            url = rs.getCampos().get(GeneralParameterEnum.URL.getName())
                            .toString();

            APIPdf api = new APIPdf();

            respuesta = api.convertirPdf(compania, nombreDocDescarga + ".docx",
                            documento,
                            url);

            archivoDescarga = new DefaultStreamedContent(respuesta,
                            ConstanteArchivo.PDF.getContentType(),
                            nombreDocDescarga
                                + ConstanteArchivo.PDF.getExtension());
        	}

        }
        catch (SystemException | IOException | SysmanException
                        | com.sysman.util.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    public static byte[] convertirDocxAPdf(byte[] docxPath) {
        try (ByteArrayInputStream  fis = new ByteArrayInputStream(docxPath);
             XWPFDocument docx = new XWPFDocument(fis);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document pdfDoc = new Document();
            PdfWriter.getInstance(pdfDoc, baos);
            pdfDoc.open();

            for (XWPFParagraph paragraph : docx.getParagraphs()) {
                pdfDoc.add(new Paragraph(paragraph.getText()));
            }

            pdfDoc.close();
            return baos.toByteArray();  // Devolvemos el PDF como un arreglo de bytes

        } catch (IOException | DocumentException  e) {
            e.printStackTrace();
            return null;
        }
    }
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario() {
        SessionUtil.setSessionVar("variablesConsultaWord", null);
        RequestContext.getCurrentInstance().closeDialog(this);
        
        if(SessionUtil.getMenuActual().equals("30412")){
            SessionUtil.redireccionarFormularioRetorno("30412","702","3","30412","281","3",true);

        }
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable valorEtiqueta
     * 
     * @return valorEtiqueta
     */
    public String getValorEtiqueta() {
        return valorEtiqueta;
    }

    /**
     * Asigna la variable valorEtiqueta
     * 
     * @param valorEtiqueta
     * Variable a asignar en valorEtiqueta
     */
    public void setValorEtiqueta(String valorEtiqueta) {
        this.valorEtiqueta = valorEtiqueta;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @return the permisos
     */
    public boolean[] getPermisos() {
        return permisos;
    }

    /**
     * @param permisos
     * the permisos to set
     */
    public void setPermisos(boolean[] permisos) {
        this.permisos = permisos;
    }

    /**
     * @return the codigoPlantilla
     */
    public String getCodigoPlantilla() {
        return codigoPlantilla;
    }

    /**
     * @param codigoPlantilla
     * the codigoPlantilla to set
     */
    public void setCodigoPlantilla(String codigoPlantilla) {
        this.codigoPlantilla = codigoPlantilla;
    }
    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_LISTAS>
    /**
     * 
     * @return lista inicial
     */
    public List<Registro> getListaInicial() {
        return listaInicial;
    }

    /**
     * 
     * @param listaInicial
     */
    public void setListaInicial(List<Registro> listaInicial) {
        this.listaInicial = listaInicial;
    }

    public boolean isDescargaMasiva() {
        return descargaMasiva;
    }

    public void setDescargaMasiva(boolean descargaMasiva) {
        this.descargaMasiva = descargaMasiva;
    }

    public boolean isDescargaNormal() {
        return descargaNormal;
    }

    public void setDescargaNormal(boolean descargaNormal) {
        this.descargaNormal = descargaNormal;
    }

    public boolean isDescargaPdf() {
        return descargaPdf;
    }

    public void setDescargaPdf(boolean descargaPdf) {
        this.descargaPdf = descargaPdf;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
