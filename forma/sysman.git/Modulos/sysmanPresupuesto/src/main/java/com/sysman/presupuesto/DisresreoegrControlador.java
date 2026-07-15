package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.DisresreoegrControladorEnum;
import com.sysman.presupuesto.enums.DisresreoegrControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 21/06/2016
 * 
 * @author eamaya
 * @version 2, 17/04/2017 Proceso de Refactoring y correciones
 * SonarLint
 */
@ManagedBean
@ViewScoped
public class DisresreoegrControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Indicador de formato especial
     */
    private boolean formatoEspecial;
    private String numeroInicial;
    private String numeroFinal;
    private String tipo;
    private String anio;
    private String nombreTipo;
    private StreamedContent archivoDescarga;
    
    /**
     * Atributo que almacena el valor Indicador que referencia a "Con
     * auxiliares"
     */
    private boolean indAuxiliar;
    
    /**
     * Atributo que almacena el valor del centro de costo inicial
     * seleccionado
     */
    private String centroCostoInicial;
    /**
     * Atributo que almacena el valor del centro de costo final
     * seleccionado
     */
    private String centroCostoFinal;
    /**
     * Atributo que almacena el valor del auxiliar inicial
     * seleccionado
     */
    private String auxiliarInicial;
    /**
     * Atributo que almacena el valor del auxiliar final seleccionado
     */
    private String auxiliarFinal;
    /**
     * Atributo que almacena el valor de la referencia inicial
     * seleccionada
     */
    private String referenciaInicial;
    /**
     * Atributo que almacena el valor de la referencia final
     * seleccionada
     */
    private String referenciaFinal;
    /**
     * Atributo que almacena el valor de la fuente de recurso inicial
     * seleccionda
     */
    private String fuenteInicial;
    /**
     * Atributo que almacena el valor de la fuente de recurso final
     * seleccionada
     */
    private String fuenteFinal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaNumeroInicial;
    private List<Registro> listaNumeroFinal;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipo;
    /**
     * Lista de registros de los centros de costos
     */
    private RegistroDataModelImpl listaCentroCostoInicial;
    /**
     * Lista de registros de los centros de costos
     */
    private RegistroDataModelImpl listaCentroCostoFinal;
    /**
     * Lista de registros de los auxiliares
     */
    private RegistroDataModelImpl listaAuxiliarInicial;
    /**
     * Lista de registros de los auxiliares
     */
    private RegistroDataModelImpl listaAuxiliarFinal;
    /**
     * Lista de registros de las referencias
     */
    private RegistroDataModelImpl listaReferenciaInicial;
    /**
     * Lista de registros de las referencias
     */
    private RegistroDataModelImpl listaReferenciaFinal;
    /**
     * Lista de registros de las fuentes de recursos
     */
    private RegistroDataModelImpl listaFuenteInicial;
    /**
     * Lista de registros de las fuentes de recursos
     */
    private RegistroDataModelImpl listaFuenteFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE> 
    /**
	 * variable que almacena el valor del parametro: GENERAR REPORTE SIN PAGOS REPETIDOS
	 */
    private boolean manejaReporteSinPago;
    /**
	 * variable ejb
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
    
    /**
     * Creates a new instance of DisresreoegrControlador
     */
    public DisresreoegrControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.DISRESREOEGR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
            centroCostoInicial = auxiliarInicial= referenciaInicial = fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
            centroCostoFinal = auxiliarFinal= referenciaFinal = fuenteFinal= SysmanConstantes.DEFECTOFINAL_STRING;
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipo();
        cargarListaCentroCostoInicial();
        cargarListaAuxiliarInicial();
        cargarListaReferenciaInicial();
        cargarListaFuenteInicial();        
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
    	
    	try {
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        
        manejaReporteSinPago = "SI".equals(SysmanFunciones
	            .nvl(ejbSysmanUtil.consultarParametro(compania, "GENERAR REPORTE SIN PAGOS REPETIDOS",
	                "-1", new Date(), true), "NO"));
        
        
    	}catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaNumeroInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);
            param.put(DisresreoegrControladorEnum.PARAM2.getValue(),
                            tipo);

            listaNumeroInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DisresreoegrControladorUrlEnum.URL3460
                                                                            .getValue())
                                            .getUrl(), param));

            if ((listaNumeroInicial == null) || listaNumeroInicial.isEmpty()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB434"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNumeroFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);
            param.put(DisresreoegrControladorEnum.PARAM2.getValue(),
                            tipo);
            param.put(DisresreoegrControladorEnum.PARAM6.getValue(),
                            numeroInicial);

            listaNumeroFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DisresreoegrControladorUrlEnum.URL4302
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DisresreoegrControladorUrlEnum.URL5089
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DisresreoegrControladorUrlEnum.URL5517
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(DisresreoegrControladorEnum.PARAM0.getValue(),
                        "DIS");
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }
    
    /**
     * 
     * Carga la lista listaCentroCostoInicial
     *
     */
    public void cargarListaCentroCostoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisresreoegrControladorUrlEnum.URL20013
                                                        .getValue());
        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroCostoFinal
     *
     */
    public void cargarListaCentroCostoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCostoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisresreoegrControladorUrlEnum.URL20015
                                                        .getValue());
        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAuxiliarInicial
     *
     */
    public void cargarListaAuxiliarInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANIO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisresreoegrControladorUrlEnum.URL23006
                                                        .getValue());
        listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaAuxiliarFinal
     *
     */
    public void cargarListaAuxiliarFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANIO.getName(), anio);
        param.put(DisresreoegrControladorEnum.CODIGOFINAL.getValue(),
                        auxiliarInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisresreoegrControladorUrlEnum.URL23008
                                                        .getValue());
        listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaReferenciaInicial
     *
     */
    public void cargarListaReferenciaInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisresreoegrControladorUrlEnum.URL13001
                                                        .getValue());
        listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaReferenciaFinal
     *
     */
    public void cargarListaReferenciaFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(DisresreoegrControladorEnum.REFERENCIAINICIAL
                        .getValue(), referenciaInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisresreoegrControladorUrlEnum.URL13035
                                                        .getValue());
        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuenteInicial
     *
     */
    public void cargarListaFuenteInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisresreoegrControladorUrlEnum.URL34001
                                                        .getValue());
        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuenteFinal
     *
     */
    public void cargarListaFuenteFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(DisresreoegrControladorEnum.FUENTEINICIAL.getValue(),
                        fuenteInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisresreoegrControladorUrlEnum.URL34003
                                                        .getValue());
        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirinforme() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        if (formatoEspecial) {
            generarInformeEspecial();
        }
        else {

            generarInforme(ReportesBean.FORMATOS.EXCEL97);
            // </CODIGO_DESARROLLADO>
        }
    }

    private void generarInformeEspecial() {
        String strSql;
        long existeDatos;

        Map<String, Object> reemplazos = new TreeMap<>();

        reemplazos.put("numeroInicial", numeroInicial);
        reemplazos.put("numeroFinal", numeroFinal);
        reemplazos.put("anio", anio);
        reemplazos.put("tipo", tipo);

        if(manejaReporteSinPago && formatoEspecial) {
    		generarInforme(ReportesBean.FORMATOS.EXCEL);
    		return;
    	}
        else if(indAuxiliar) 
        {
        	reemplazos.put("centroInicial", centroCostoInicial);
            reemplazos.put("centroFinal", centroCostoFinal);
            reemplazos.put("auxiliarInicial", auxiliarInicial);
            reemplazos.put("auxiliarFinal", auxiliarFinal);
            reemplazos.put("referenciaInicial", referenciaInicial);
            reemplazos.put("referenciaFinal", referenciaFinal);
            reemplazos.put("fuenteInicial", fuenteInicial);
            reemplazos.put("fuenteFinal", fuenteFinal);
        	
        	strSql = Reporteador.resuelveConsulta(
                    "002591DISRESREOEGR_SINCHI",
                    Integer.parseInt(SessionUtil.getModulo()), reemplazos);
        	
        }
        else 
        {
	        strSql = Reporteador.resuelveConsulta(
	                        "000932DISRESREOEGR",
	                        Integer.parseInt(SessionUtil.getModulo()), reemplazos);
        }

        try {
            existeDatos = service.getConteoConsulta(strSql);

            if (existeDatos != 0) {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);
            }
            else {

                JsfUtil.agregarMensajeError(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));

            }

        }
        catch (SystemException | JRException | IOException | SQLException
                        | DRException | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
    	String reporte;
    	archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(anio) ||
            SysmanFunciones.validarVariableVacio(tipo) ||
            SysmanFunciones.validarVariableVacio(numeroInicial) ||
            SysmanFunciones.validarVariableVacio(numeroFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB435"));
        }
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try {
            // Reemplazos valores consulta reporte
            reemplazos.put("numeroInicial", numeroInicial);
            reemplazos.put("numeroFinal", numeroFinal);
            reemplazos.put("anio", anio);
            reemplazos.put("tipo", tipo);

            // Inicio Parametros Reporte
            parametros.put("PR_NUMEROINICIAL", numeroInicial);
            parametros.put("PR_NUMEROFINAL", numeroFinal);

            if (manejaReporteSinPago && formatoEspecial) {
    			reporte = "800724DISRESREOEGR_SIN_PAGOS_REPETIDOS";
    		}
            
            else if(indAuxiliar) 
            {
            	reemplazos.put("centroInicial", centroCostoInicial);
                reemplazos.put("centroFinal", centroCostoFinal);
                reemplazos.put("auxiliarInicial", auxiliarInicial);
                reemplazos.put("auxiliarFinal", auxiliarFinal);
                reemplazos.put("referenciaInicial", referenciaInicial);
                reemplazos.put("referenciaFinal", referenciaFinal);
                reemplazos.put("fuenteInicial", fuenteInicial);
                reemplazos.put("fuenteFinal", fuenteFinal);
            	
            	reporte = "002591DISRESREOEGR_SINCHI";
            	
            }
            else
            {
            	reporte = "000932DISRESREOEGR";
            }
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        numeroInicial = "";
        numeroFinal = "";
        listaNumeroFinal = null;
        centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING; 
        cargarListaNumeroInicial();
        cargarListaCentroCostoInicial();
        cargarListaAuxiliarInicial();
        cargarListaReferenciaInicial();
        cargarListaFuenteInicial();
    }

    public void cambiarNumeroInicial() {
        numeroFinal = "";
        cargarListaNumeroFinal();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarIndAuxiliar() 
	{
		centroCostoInicial = auxiliarInicial= referenciaInicial = fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		centroCostoFinal = auxiliarFinal= referenciaFinal = fuenteFinal= SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaCentroCostoInicial();
        cargarListaAuxiliarInicial();
        cargarListaReferenciaInicial();
        cargarListaFuenteInicial();
	}


    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), " ")
                        .toString();
        nombreTipo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
        numeroInicial = "";
        numeroFinal = "";
        listaNumeroFinal = null;
        if (SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB436"));
            return;
        }
        cargarListaNumeroInicial();
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();        
        centroCostoFinal = null;
        cargarListaCentroCostoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();        
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();        
        auxiliarFinal = null;
        cargarListaAuxiliarFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();        
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();        
        referenciaFinal = null;
        cargarListaReferenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();       
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();        
        fuenteFinal = null;
        cargarListaFuenteFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();        
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public void setFormatoEspecial(boolean formatoEspecial) {
        this.formatoEspecial = formatoEspecial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public boolean isFormatoEspecial() {
        return formatoEspecial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public void setListaNumeroInicial(List<Registro> listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    public List<Registro> getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    public void setListaNumeroFinal(List<Registro> listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

	public boolean isIndAuxiliar() {
		return indAuxiliar;
	}

	public void setIndAuxiliar(boolean indAuxiliar) {
		this.indAuxiliar = indAuxiliar;
	}

	public String getCentroCostoInicial() {
		return centroCostoInicial;
	}

	public void setCentroCostoInicial(String centroCostoInicial) {
		this.centroCostoInicial = centroCostoInicial;
	}

	public String getCentroCostoFinal() {
		return centroCostoFinal;
	}

	public void setCentroCostoFinal(String centroCostoFinal) {
		this.centroCostoFinal = centroCostoFinal;
	}

	public String getAuxiliarInicial() {
		return auxiliarInicial;
	}

	public void setAuxiliarInicial(String auxiliarInicial) {
		this.auxiliarInicial = auxiliarInicial;
	}

	public String getAuxiliarFinal() {
		return auxiliarFinal;
	}

	public void setAuxiliarFinal(String auxiliarFinal) {
		this.auxiliarFinal = auxiliarFinal;
	}

	public String getReferenciaInicial() {
		return referenciaInicial;
	}

	public void setReferenciaInicial(String referenciaInicial) {
		this.referenciaInicial = referenciaInicial;
	}

	public String getReferenciaFinal() {
		return referenciaFinal;
	}

	public void setReferenciaFinal(String referenciaFinal) {
		this.referenciaFinal = referenciaFinal;
	}

	public String getFuenteInicial() {
		return fuenteInicial;
	}

	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}

	public String getFuenteFinal() {
		return fuenteFinal;
	}

	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
	}

	public RegistroDataModelImpl getListaCentroCostoInicial() {
		return listaCentroCostoInicial;
	}

	public void setListaCentroCostoInicial(RegistroDataModelImpl listaCentroCostoInicial) {
		this.listaCentroCostoInicial = listaCentroCostoInicial;
	}

	public RegistroDataModelImpl getListaCentroCostoFinal() {
		return listaCentroCostoFinal;
	}

	public void setListaCentroCostoFinal(RegistroDataModelImpl listaCentroCostoFinal) {
		this.listaCentroCostoFinal = listaCentroCostoFinal;
	}

	public RegistroDataModelImpl getListaAuxiliarInicial() {
		return listaAuxiliarInicial;
	}

	public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) {
		this.listaAuxiliarInicial = listaAuxiliarInicial;
	}

	public RegistroDataModelImpl getListaAuxiliarFinal() {
		return listaAuxiliarFinal;
	}

	public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) {
		this.listaAuxiliarFinal = listaAuxiliarFinal;
	}

	public RegistroDataModelImpl getListaReferenciaInicial() {
		return listaReferenciaInicial;
	}

	public void setListaReferenciaInicial(RegistroDataModelImpl listaReferenciaInicial) {
		this.listaReferenciaInicial = listaReferenciaInicial;
	}

	public RegistroDataModelImpl getListaReferenciaFinal() {
		return listaReferenciaFinal;
	}

	public void setListaReferenciaFinal(RegistroDataModelImpl listaReferenciaFinal) {
		this.listaReferenciaFinal = listaReferenciaFinal;
	}

	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}

	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}

	public RegistroDataModelImpl getListaFuenteFinal() {
		return listaFuenteFinal;
	}

	public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
		this.listaFuenteFinal = listaFuenteFinal;
	}
}
