package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenUnoRemote;
import com.sysman.almacen.enums.DepreciacionMesDependenciaControladorEnum;
import com.sysman.almacen.enums.DepreciacionMesDependenciaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 29/01/2016
 *
 * -- Modificado por lcortes 26,27/04/2017. Refactorizacion de codigo
 * de las listas para utilizar dss y se ajusta los llamados a
 * funciones, procedimientos y metodos de la clase Acciones.
 * 
 * @version 3, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 * 
 * @version 4, 04/09/2018, @author asana Se agrega la opciï¿½n
 * "Compuestos" de acuerdo a TAR 1000085939 y se genera el reporte
 * correspondiente agregando el mï¿½todo
 * generarIDepreciarMesComponentesGrupoNIIF(), validaciï¿½n se agrega
 * en el mï¿½todo solucionarNiif()
 * 
 */
@ManagedBean
@ViewScoped
public class DepreciacionMesDependenciaControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String cMsmTransInterrum;
    private final String cCodigo;
    private final String cCodigoElemento;
    private final String cNombre;
    private final String cPrDepSuma;
    private final String cPrVisibleDep;
    private final String filtro;
    private String agrupadoPor;
    private boolean porCC;
    private boolean depCuenta;
    private boolean depPeriodo;
    private boolean depTotal;
    private boolean depFecha;
    private boolean visibleAgrupacion;
    private boolean visibleFiltros;
    private boolean visibleFecha;
    private boolean excelpl;
    private boolean ckContraloria;

    private String elementoInicial;
    private String elementoFinal;
    private String filtroPor;
    private String ccInicial;
    private String ccFinal;
    private String mes;
    private String anio;
    private String nombreElemInicial;
    private String nombreElemFinal;
    private String nombreCCinicial;
    private String nombreCCfinal;
    private boolean visibleCC;
    private StreamedContent archivoDescarga;
    private List<Registro> listaFiltradoPor;
    private List<Registro> listaMeses;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaCodigoElementoInicial;
    private RegistroDataModelImpl listaCodigoElementoFinal;
    private RegistroDataModelImpl listaCodigoccinicial;
    private RegistroDataModelImpl listaCodigoccfinal;
    private String niif; // Identifica si la entidad maneja niif
    private String formatoEspecial; // Identifica el formato del
    // informe de acuerdo al parametro
    private String depPorCC;
    private String manejaFormato;
    private String parametroComodato;
    private String colgaapNiif; // Identifica si la entidad maneja los procesos de colgaap y niif de manera simultanea
    private String entAplicaNiif;
    private boolean ActivocolgaapNiif = false;
    
	HashMap<String, Object> reemplazar = new HashMap<>();
    HashMap<String, Object> parametrosRep = new HashMap<>();
    private static final String SERVICIO = "Servicio";
    private static final String BODEGA = "Bodega";
    private static final String CONSOLIDADO = "Consolidado";
    private static final String CONSOLIDADONIIF = "ConsolidadoNIIF";
    private static final String INSERVIBLES = "Inservibles";
    private static final String RESPONSABILIDADES = "Responsabilidades";
    private static final String SERVICIOINTERNO = "Servicio Interno";
    private static final String SERVICIOCOMODATO = "Servicio en Comodato";
    private static final String CLASEBODEGA = "Consolidado por Clase de Bodega";
    /**
     * Variable que almacena el valor del parametro = MANEJA INFORME DE AMORTIZACION AJUSTADA
     */
    private boolean manejaInformeAmortizacion = false;
    /**
     * Variable que almacena el valor del check = Incluir en Informe de Amortización
       Modulo inventarios CC:1958
     */
    private int valorAmortizacion; 
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAlmacenUnoRemote ejbAlmacenUno;
    private String manejaCentroRef;

    /**
     * Creates a new instance of DepreciacionMesDependenciaControlador
     */
    public DepreciacionMesDependenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cMsmTransInterrum = "MSM_TRANS_INTERRUMPIDA";
        cCodigo = "CODIGO";
        cCodigoElemento = "CODIGOELEMENTO";
        cNombre = "NOMBRE";
        cPrDepSuma = "PR_DEPRECIACIONSUMA";
        cPrVisibleDep = "PR_VISIBLEDEPRECIACION";
        filtro = "filtro";
        agrupadoPor = "1";
        try {
            // 486
            numFormulario = GeneralCodigoFormaEnum.DEPRECIACION_MES_DEPENDENCIA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DepreciacionMesDependenciaControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        try {

            niif = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA NIIF EN ALMACEN", modulo, new Date(), true);

            formatoEspecial = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO RELACION DE DEPRECIACIONES", modulo,
                            new Date(), true);

            depPorCC = ejbSysmanUtil.consultarParametro(compania,
                            "GENERAR RELACION DE DEPRECIACIONES POR CENTRO DE COSTO",
                            modulo, new Date(), true);

            manejaFormato = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA FORMATO ESPECIAL RELACION DEPRECIACIONES",
                            modulo, new Date(), true);

            parametroComodato = ejbSysmanUtil.consultarParametro(compania,
                            "DEPRECIACION COMODATO", modulo, new Date(), true);
            
            colgaapNiif = ejbSysmanUtil.consultarParametro(compania,
                    		"EJECUTA COLGAAP y NIIF", modulo, new Date(), true);
            
            entAplicaNiif = ejbSysmanUtil.consultarParametro(compania,
            				"ENTIDAD APLICA NIIF", modulo, new Date(), true);
            
            manejaCentroRef = SysmanFunciones.toString(ejbSysmanUtil.consultarParametro(compania,
    				"MANEJA DEPRECIACION POR CENTRO DE COSTO Y REFERENCIA", modulo, new Date(), true));  
            
			manejaInformeAmortizacion = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA INFORME DE AMORTIZACION AJUSTADA", modulo, new Date(), true), "NO").equals("SI") ? true: false;

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(cMsmTransInterrum) +
                                ex.getMessage());
            Logger.getLogger(DepreciacionMesDependenciaControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        porCC = false;

        visibleCC = !((depPorCC == null) || "NO".equalsIgnoreCase(depPorCC));
        cargarListaFiltradoPor();

        anio = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR));
        mes = String.valueOf(
                        SysmanFunciones.getParteFecha(new Date(),
                                        Calendar.MONTH)
                            +
                            1);
        visibleAgrupacion = true;
        visibleFiltros = true;
        visibleFecha = true;
        cargarListaAno();
        cargarListaCodigoElementoInicial();
        cargarListaCodigoElementoFinal();
        cargarListacodigoccinicial();
        cargarListacodigoccfinal();
        cargarListameses();
        abrirFormulario();
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public void cargarRegistroFiltro(int posicion, String elemento) {
        // Carga un elemento al combo de filtros
        HashMap<String, Object> lista = new HashMap<>();
        lista.put(cCodigo, elemento);
        lista.put(cNombre, elemento);
        listaFiltradoPor.add(new Registro(posicion, lista));

    }

    public void cargarListaFiltradoPor() {
        int posicion;
        listaFiltradoPor = new ArrayList<>();
        posicion = 1;
        cargarRegistroFiltro(posicion, BODEGA);
        posicion += 1;
        cargarRegistroFiltro(posicion, SERVICIO);
        posicion += 1;
        cargarRegistroFiltro(posicion, INSERVIBLES);
        posicion += 1;
        cargarRegistroFiltro(posicion, RESPONSABILIDADES);
        posicion += 1;
        cargarRegistroFiltro(posicion, CONSOLIDADO);

        posicion += 1;
        cargarRegistroFiltro(posicion, CLASEBODEGA);

        if ("SI".equalsIgnoreCase(niif)) {

            posicion += 1;
            cargarRegistroFiltro(posicion, "Servicio en Comodato");
            posicion += 1;
            cargarRegistroFiltro(posicion, "Servicio Interno");
        }
        if (!("I_DepreciarMesDependenciaSG".equalsIgnoreCase(formatoEspecial))
            &&
            ("NO".equalsIgnoreCase(manejaFormato))) {
            posicion += 1;
            cargarRegistroFiltro(posicion, SERVICIOCOMODATO);
            posicion += 1;
            cargarRegistroFiltro(posicion, SERVICIOINTERNO);

        }
        
        if ("SI".equalsIgnoreCase(colgaapNiif)) {

            posicion += 1;
            cargarRegistroFiltro(posicion, "Bodega COLGAAP");
            posicion += 1;
            cargarRegistroFiltro(posicion, "Servicio COLGAAP");
            posicion += 1;
            cargarRegistroFiltro(posicion, "Inservibles COLGAAP");
            posicion += 1;
            cargarRegistroFiltro(posicion, "Servicio en Comodato COLGAAP");
            posicion += 1;
            cargarRegistroFiltro(posicion, "Consolidado COLGAAP");
        }

    }

    public void cargarListameses() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMeses = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DepreciacionMesDependenciaControladorUrlEnum.URL8625
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(DepreciacionMesDependenciaControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DepreciacionMesDependenciaControladorUrlEnum.URL9133
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(DepreciacionMesDependenciaControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoElementoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepreciacionMesDependenciaControladorUrlEnum.URL9459
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "D,N,E");

        listaCodigoElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarListaCodigoElementoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepreciacionMesDependenciaControladorUrlEnum.URL10101
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "D,N,E");
        param.put(DepreciacionMesDependenciaControladorEnum.PARAM0.getValue(),
                        elementoInicial);

        listaCodigoElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);

    }

    public void cargarListacodigoccinicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepreciacionMesDependenciaControladorUrlEnum.URL10812
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoccinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListacodigoccfinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepreciacionMesDependenciaControladorUrlEnum.URL11410
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DepreciacionMesDependenciaControladorEnum.PARAM1.getValue(),
                        ccInicial);

        listaCodigoccfinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }
    
    /**
     * Validacion  si el elemento tiene activado
     * el check de informe de amortizacion, usando los valores de 
     * {@code elementoInicial} y {@code elementoFinal}. 
     * El resultado se almacena en {@code valorAmortizacion}.
     */
    public void validarInformeAmortizacion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DepreciacionMesDependenciaControladorEnum.PARAM0.getValue(), elementoInicial);
        param.put(DepreciacionMesDependenciaControladorEnum.PARAM2.getValue(), elementoFinal);

        try {
            String url = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(DepreciacionMesDependenciaControladorUrlEnum.URL112198.getValue())
                    .getUrl();

            Registro reg = RegistroConverter.toRegistro(requestManager.get(url, param));

            if (reg != null) {
            	valorAmortizacion = Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get("RESULTADO")));
            }
        } catch (SystemException e) {
                Logger.getLogger(DepreciacionMesDependenciaControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
    }    

    public void oprimircmdPantalla() {
        generarInforme(FORMATOS.PDF);
    }

    public void oprimirexcel() {
        generarInforme(FORMATOS.EXCEL);
    }

    public void cambiarFiltradoPor() {
        if ("SI".equals(niif)) {
            switch (filtroPor) {
            case "NIIF":
                visibleAgrupacion = true;
                visibleFiltros = false;
                visibleFecha = false;
                porCC = false;
                break;
            case CONSOLIDADONIIF:
            case "Bodega NIIF":
            case "Servicio NIIF":
            case "Inservibles NIIF":
            case "Resposabilidades NIIF":
            case "Servicio en Comodato NIIF":
            case "Servicio Interno NIIF":
                visibleAgrupacion = true;
                visibleFiltros = false;
                visibleFecha = true;
                porCC = false;
                break;
            default:
                visibleAgrupacion = true;
                visibleFiltros = true;
                visibleFecha = true;
                break;
            }
        }

        if ("SI".equals(colgaapNiif)) {
            switch (filtroPor) {
            case "Bodega COLGAAP":
            case "Servicio COLGAAP":
            case "Inservibles COLGAAP":
            case "Servicio en Comodato COLGAAP":
            case "Consolidado COLGAAP":
            	visibleFiltros = false;
            	visibleFecha = false;
            }
        }
    }

    public void cambiarMes() {
        if ((mes == null) || (Integer.parseInt(mes) < 1) ||
            (Integer.parseInt(mes) > 12)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1838"));
            mes = null;
        }
    }

    public void cambiarexcelpl() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarckContraloria() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAgrupadopor() {
        // Esta incluido en la forma
    }

    public void cambiarAno() {
        if ((anio == null) || (anio.length() < 4)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1839"));
            anio = null;
        }
        cargarListameses();
    }

    private void generarParametros() {
        String parametro = "";
        String nombreContador = "";
        String nombreAlmacenista = "";
        String ultimoDia = "";
        String formatosUspec = "";

        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            true);
            
            nombreContador = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE CONTADOR", modulo, new Date(),
                            true);

            nombreAlmacenista = ejbSysmanUtil.consultarParametro(compania,
                            "ALMACENISTA", modulo, new Date(),
                            true);

            ultimoDia = SysmanFunciones.convertirAFechaCadena(
                            SysmanFunciones.ultimoDiaDate(SysmanFunciones
                                            .convertirAFecha("01/" + mes + "/" +
                                                anio)),
                            "dd/MM/yyyy");
            
            formatosUspec = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
    		        		"FORMATOS UNICOS USPEC", modulo, new Date(), false), "NO").toString();
        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        reemplazar.put("agrupacion", parametro == null ? "3" : parametro);
        reemplazar.put("agrupacionSub", anio);
        reemplazar.put("elementoInicial", elementoInicial);
        reemplazar.put("elementoFinal", elementoFinal);
        reemplazar.put("ultimoDia", ultimoDia);
        reemplazar.put("anio", anio);
        reemplazar.put("mes", mes);
        parametrosRep.put("PR_MES",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes)].toUpperCase());
        parametrosRep.put("PR_ANO", anio);

        parametrosRep.put("PR_FILTRADOPOR", filtroPor.toUpperCase());
        parametrosRep.put("PR_NOMBRE_CONTADOR", nombreContador == null ? ""
            : nombreContador.toUpperCase());
        parametrosRep.put("PR_ALMACENISTA", nombreAlmacenista == null ? ""
            : nombreAlmacenista.toUpperCase());
        parametrosRep.put("PR_AGRUPADO", agrupadoPor == null ? 2
            : Integer.parseInt(agrupadoPor));
        
        if ("SI".equals(formatosUspec) && filtroPor.equals(CONSOLIDADO)) {
        	parametrosRep.put("PR_FORMATOS_USPEC", true);
        } else {
        	parametrosRep.put("PR_FORMATOS_USPEC", false);
        }
    }

    private void generarInforme(FORMATOS formato) {
        reemplazar = new HashMap<>();
        parametrosRep = new HashMap<>();
        generarParametros();

        if (depCuenta) {
            generarIDepreciarMesDependenciaAgrupado(formato);
        }
        else if (porCC) {
            generarIDepreciarMesDependenciaGrupoCentroCosto(formato);
        }
        else if(ckContraloria){
        	generarInformeContraloria(formato);
        }
        else if ("I_DepreciarMesDependenciaSG"
                        .equalsIgnoreCase(formatoEspecial)) {
            generarIDepreciarMesDependenciaSG();
        }
        else if ("I_DepreciarMesDependencia"
                        .equalsIgnoreCase(formatoEspecial)) {
            if (depFecha) {
                generarIDepreciarMesDependenciaDetallado(formato);
            }
            else {
                solucionarNiif(formato, false);
            }
        }
        else if ("I_DepreciarMesDependencia_CC"
                        .equalsIgnoreCase(formatoEspecial)) {
            if (depFecha) {
                generarIDepreciarMesDependenciaCC(formato);
            }
            else {
                generarIDepreciarMesDependenciaDetallado(formato);
            }
        }
        else {
            solucionarNiif(formato, true);
        }
        

    }

    private void solucionarNiif(FORMATOS formato, boolean grupo) {
        if ("SI".equalsIgnoreCase(niif) && "NIIF".equalsIgnoreCase(filtroPor)) {
            generarIDepreciarMesDependenciaGrupoNIIF(formato);
        }
        else if ("SI".equalsIgnoreCase(niif) &&
            CONSOLIDADONIIF.equalsIgnoreCase(filtroPor)) {
            generarIDepreciarMesDependenciaGrupoConsolidadoNIIF(formato);
        }
        else {
            if (grupo) {
                generarIDepreciarMesDependenciaGrupo(formato);
            }
            else {
                generarIDepreciarMesDependencia(formato);
            }
        }
    }

    /**
     * @author jgomez Este el metodo final que lanza la impresiï¿½n
     * del informe despues de solucionar la consulta y el reporte
     * @param formato
     * @param nombreReporte
     * @param nombreConsulta
     */
    private void impirmirInforme(FORMATOS formato, String nombreReporte,
        String nombreConsulta) {
        archivoDescarga = null;
        String strSql;
        String strSqlAmortizacion;
        String consulta;
        
        try {

            if (filtroPor.equals(BODEGA) && excelpl == true) {
                nombreReporte = "800382IDepreciarMesDependencia";

                if (filtroPor.equals(BODEGA) && excelpl == true
                    && agrupadoPor.equals("1")) {
                    nombreReporte = "800387IDepreciarMesDependencia_AG";

                }
                if (filtroPor.equals(BODEGA) && excelpl == true
                    && agrupadoPor.equals("1") && depCuenta == true) {
                    nombreReporte = "800393IDepreciarMesDependenciaAgrupado";

                }

                if (filtroPor.equals(BODEGA) && excelpl == true
                    && !agrupadoPor.equals("1") && depCuenta == true) {
                    nombreReporte = "800392IDepreciarMesDependencia";

                }

                strSql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);

                parametrosRep.put("PR_STRSQL", strSql);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL, nombreReporte);

            }
            else

            if (filtroPor.equals(SERVICIO) && excelpl == true) {
                nombreReporte = "800383IDepreciarMesDependencia_S";

                if (filtroPor.equals(SERVICIO) && excelpl == true
                    && agrupadoPor.equals("1")) {
                    nombreReporte = "800388IDepreciarMesDependencia_S_AG";

                }
                if (filtroPor.equals(SERVICIO) && excelpl == true
                    && agrupadoPor.equals("1") && depCuenta == true) {
                    nombreReporte = "800394IDepreciarMesDependenciaAgrupado_S";

                }
                if (filtroPor.equals(SERVICIO) && excelpl == true
                    && !agrupadoPor.equals("1") && depCuenta == true) {
                    nombreReporte = "800395IDepreciarMesDependencia_S";

                }

                strSql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);

                parametrosRep.put("PR_STRSQL", strSql);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL, nombreReporte);

            }
            else

            if (filtroPor.equals(SERVICIOCOMODATO) && excelpl == true) {
                nombreReporte = "800384IDepreciarMesDependencia_SC";

                if (filtroPor.equals(SERVICIOCOMODATO) && excelpl == true
                    && agrupadoPor.equals("1")) {
                    nombreReporte = "800389IDepreciarMesDependencia_SC_AG";

                }
                if (filtroPor.equals(SERVICIOCOMODATO) && excelpl == true
                    && agrupadoPor.equals("1") && depCuenta == true) {
                    nombreReporte = "800396IDepreciarMesDependenciaAgrupado_Comodato";

                }
                if (filtroPor.equals(SERVICIOCOMODATO) && excelpl == true
                    && !agrupadoPor.equals("1") && depCuenta == true) {
                    nombreReporte = "800397IDepreciarMesDependencia_Comodato";

                }

                strSql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);

                parametrosRep.put("PR_STRSQL", strSql);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL, nombreReporte);

            }
            else

            if (filtroPor.equals(SERVICIOINTERNO) && excelpl == true) {
                nombreReporte = "800385IDepreciarMesDependencia_SI";

                if (filtroPor.equals(SERVICIOINTERNO) && excelpl == true
                    && agrupadoPor.equals("1")) {
                    nombreReporte = "800390IDepreciarMesDependencia_SI_AG";

                }

                strSql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);

                parametrosRep.put("PR_STRSQL", strSql);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL, nombreReporte);

            }
            else

            if (filtroPor.equals(CONSOLIDADO) && excelpl == true) {
                nombreReporte = "800386IDepreciarMesDependencia_CON";

                if (filtroPor.equals(CONSOLIDADO) && excelpl == true
                    && agrupadoPor.equals("1")) {
                    nombreReporte = "800391IDepreciarMesDependencia_CON_AG";

                }

                strSql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);

                try {
                    String informeResumen = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                            				"MANEJA INFORME RESUMEN EN ALMACEN", modulo, new Date(), true),"NO");

                    if ("SI".equals(informeResumen)) {
                        archivoDescarga = generarReporteConsolidadoResumen(nombreReporte, strSql, reemplazar, compania, modulo);
                    } else {
                        archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL, nombreReporte);
                    }
                } catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
            else

            if (filtroPor.equals(INSERVIBLES) && excelpl == true) {
                nombreReporte = "800398IDepreciarMesDependencia_I";

                if (filtroPor.equals(INSERVIBLES) && excelpl == true
                    && agrupadoPor.equals("1")) {
                    nombreReporte = "800399IDepreciarMesDependencia_I_AG";

                }

                strSql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);

                parametrosRep.put("PR_STRSQL", strSql);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL, nombreReporte);

            }
            else

            if (filtroPor.equals(RESPONSABILIDADES) && excelpl == true) {
                nombreReporte = "800400IDepreciarMesDependencia_R";

                if (filtroPor.equals(RESPONSABILIDADES) && excelpl == true
                    && agrupadoPor.equals("1")) {
                    nombreReporte = "800401IDepreciarMesDependencia_R_AG";

                }

                strSql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);

                parametrosRep.put("PR_STRSQL", strSql);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL, nombreReporte);

            }
            else {

                if (depCuenta && FORMATOS.EXCEL.equals(formato)) {

                    strSql = Reporteador.resuelveConsulta(nombreConsulta,
                                    Integer.parseInt(modulo), reemplazar);

                    parametrosRep.put("PR_STRSQL", strSql);

                    archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    FORMATOS.EXCEL, nombreReporte);

                }else if(ckContraloria) {
                	strSql = Reporteador.resuelveConsulta(nombreReporte,
                			Integer.parseInt(modulo), reemplazar);

                	parametrosRep.put("PR_STRSQL", strSql);

                	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                			ConectorPool.ESQUEMA_SYSMAN,
                			FORMATOS.EXCEL, nombreReporte);
                }
                else {
                	
                	// Ejecuta las subconsultas dependientes del informe padre 000519IDepreciarMesDependencia,
                	// en los casos donde no se ha marcado ningún check en el módulo,
                	// y el parámetro de configuración "MANEJA INFORME DE AMORTIZACIÓN AJUSTADA" se encuentra habilitado ("Si").

                	switch (filtroPor) {
                    case BODEGA:
                        consulta = "002814IamortizacionMesDependenciaAgrupado";
                        break;
                    case SERVICIO:
                        consulta = "002815IamortizacionMesDependencia_S";
                        break;
                    case INSERVIBLES:
                        consulta = "002816IamortizacionMesDependencia_I";
                        break;
                    case RESPONSABILIDADES:
                        consulta = "002817IamortizacionMesDependencia_R";
                        break;
                    case CONSOLIDADO:
                        consulta = "002818IIamortizacionMesDependencia_CON";
                        break;
                    case SERVICIOCOMODATO:
                        consulta = "002819IamortizacionMesDependencia_SC";
                        break;
                    case SERVICIOINTERNO:
                        consulta = "002820IamortizacionMesDependencia_SI";
                        break;
                    default:
                        consulta = "";
                        break;
                    }
                	
                    strSql = Reporteador.resuelveConsulta(nombreConsulta,
                                    Integer.parseInt(modulo), reemplazar);
                    
                    strSqlAmortizacion = Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(modulo), reemplazar);

                    parametrosRep.put("PR_STRSQL", strSql);
                    
                    parametrosRep.put("PR_SQL_SUB_AMORT_MES_DEP", strSqlAmortizacion);
                    
                    archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                                    parametrosRep,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    formato);
                }

            }

        }
        catch (JRException | IOException | SysmanException | SQLException
                        | DRException e) {
            logger.error(e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    public void mensajePrueba(){
    	JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4481"));
    }


    private void generarIDepreciarMesDependenciaAgrupado(FORMATOS formato) {
        String consulta;
        String nombreReporte = "000505IDepreciarMesDependenciaAgrupado";
        if (depPeriodo) {
            reemplazar.put(filtro,
                            " AND DEPRECIAR.VLRDEPRECIACION <> 0 AND DEPRECIAR.VLRLIBROS = 0");
        }
        else {
            reemplazar.put(filtro, "");
        }

        switch (filtroPor) {
        case BODEGA:
            consulta = "000505IDepreciarMesDependenciaAgrupado";
            break;
        case SERVICIO:
            consulta = "000505IDepreciarMesDependenciaAgrupado_S";
            break;
        case INSERVIBLES:
            consulta = "000505IDepreciarMesDependenciaAgrupado_I";
            break;
        case RESPONSABILIDADES:
            consulta = "000505IDepreciarMesDependenciaAgrupado_R";
            break;
        case CONSOLIDADO:
            consulta = "000505IDepreciarMesDependenciaAgrupado_C";
            break;
        case SERVICIOCOMODATO:
            consulta = "000505IDepreciarMesDependenciaAgrupado_Comodato";
            break;
        case SERVICIOINTERNO:
            consulta = "000505IDepreciarMesDependenciaAgrupado_SI";
            break;
        default:
            consulta = "";
            break;
        }

        impirmirInforme(formato, nombreReporte, consulta);
    }
    
    private StreamedContent generarReporteConsolidadoResumen(String nombreReporte, String strSql, Map<String, Object> reemplazar, String compania, String modulo) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Workbook workbook = new XSSFWorkbook(JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL).getStream());
            Sheet sheet = workbook.getSheetAt(0);

            CellStyle contabilidadStyle = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            contabilidadStyle.setDataFormat(df.getFormat("#,##0.00"));

            Row r4 = sheet.createRow(sheet.getLastRowNum() + 1);
            Cell cellT = r4.createCell(1);
            cellT.setCellValue("TOTAL GENERAL");

            String[] columnas = {"C", "D", "E", "F", "G", "H"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = r4.createCell(i + 2);
                cell.setCellFormula("SUM(" + columnas[i] + "2:" + columnas[i] + (sheet.getLastRowNum()) + ")");
                cell.setCellStyle(contabilidadStyle);
            }

            Map<String, Object> param = new HashMap<>();
            param.put("COMPANIA", compania);
            param.put("AGRUPACION", reemplazar.get("agrupacion"));
            param.put("ULTIMODIA", reemplazar.get("ultimoDia"));
            param.put("ELEMENTOINICIAL", reemplazar.get("elementoInicial"));
            param.put("ELEMENTOFINAL", reemplazar.get("elementoFinal"));

            List<Registro> listaReporte2 = RegistroConverter.toListRegistro(
                requestManager.getList(
                    UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(DepreciacionMesDependenciaControladorUrlEnum.URL179005.getValue())
                        .getUrl(),
                    param));

            int filaTotalGeneral = sheet.getLastRowNum();
            int filaEncabezado = filaTotalGeneral + 2;
            Row headerRow = sheet.createRow(filaEncabezado);

            String[] encabezados = {
                "CODIGOCUENTA", "NOMBRECUENTA", "VALOR_HISTORICO",
                "DEPRECIACION_DEL_PERIODO", "DEPRECIACION_ACUMULADA",
                "DEPRECIACION_ANTERIOR", "VALOR_LIBROS"
            };

            for (int i = 0; i < encabezados.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(encabezados[i]);
            }

            int filaDatos = filaEncabezado + 1;
            for (Registro reg : listaReporte2) {
                Row dataRow = sheet.createRow(filaDatos++);

                Object valor = reg.getCampos().get("CODIGOCUENTA");
                dataRow.createCell(0).setCellValue(valor != null ? valor.toString() : "");
                dataRow.createCell(1).setCellValue(String.valueOf(reg.getCampos().get("NOMBRECUENTA")));

                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue(parseDouble(reg.getCampos().get("VALOR_HISTORICO")));
                cell2.setCellStyle(contabilidadStyle);

                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue(parseDouble(reg.getCampos().get("DEPRECIACION_DEL_PERIODO")));
                cell3.setCellStyle(contabilidadStyle);

                Cell cell4 = dataRow.createCell(4);
                cell4.setCellValue(parseDouble(reg.getCampos().get("DEPRECIACION_ACUMULADA")));
                cell4.setCellStyle(contabilidadStyle);

                Cell cell5 = dataRow.createCell(5);
                cell5.setCellValue(parseDouble(reg.getCampos().get("DEPRECIACION_ANTERIOR")));
                cell5.setCellStyle(contabilidadStyle);

                Cell cell6 = dataRow.createCell(6);
                cell6.setCellValue(parseDouble(reg.getCampos().get("VALOR_LIBROS")));
                cell6.setCellStyle(contabilidadStyle);
            }

            workbook.write(out);
            workbook.close();
            
            return JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()), nombreReporte + ".xlsx");
        }
    }

    private double parseDouble(Object val) {
        try {
            return Double.parseDouble(String.valueOf(val));
        } catch (Exception e) {
            return 0.0;
        }
    }


    private boolean validarCentros() {
        boolean rta = true;
        if (porCC && SysmanFunciones.validarVariableVacio(ccInicial)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_DEBE_CENTRO_INI"));
            rta = false;
        }
        if (porCC && SysmanFunciones.validarVariableVacio(ccFinal)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_DEBE_CENTRO_FIN"));
            rta = false;
        }
        return rta;
    }

    private void generarIDepreciarMesDependenciaGrupoCentroCosto(
        FORMATOS formato) {
        if (!validarCentros()) {
            return;
        }
        String nombreReporte = "000524IDepreciarMesDependenciaGrupoCentroCosto";
        String consulta;
        switch (filtroPor) {
        case BODEGA:
            consulta = "000524IDepreciarMesDependenciaGrupoCentroCosto";
            break;
        case SERVICIO:
            consulta = "000524IDepreciarMesDependenciaGrupoCentroCosto_S";
            break;
        case INSERVIBLES:
            consulta = "000524IDepreciarMesDependenciaGrupoCentroCosto_I";
            break;
        case RESPONSABILIDADES:
            consulta = "000524IDepreciarMesDependenciaGrupoCentroCosto_R";
            break;
        case CONSOLIDADO:
            consulta = "000524IDepreciarMesDependenciaGrupoCentroCosto_CON";
            break;
        case SERVICIOCOMODATO:
            consulta = "000524IDepreciarMesDependenciaGrupoCentroCosto_SC";
            break;
        case SERVICIOINTERNO:
            consulta = "000524IDepreciarMesDependenciaGrupoCentroCosto_SI";
            break;
        default:
            consulta = "";
            break;
        }
        impirmirInforme(formato, nombreReporte, consulta);
    }

    private void generarIDepreciarMesDependenciaSG() {
        // sin desarrollar
    }

    private void depreciaComodato() {
        if ((filtroPor == SERVICIO) || (filtroPor == BODEGA)) {
            try {
                String depreciacionSuma = ejbAlmacenUno
                                .calcularDepreciacionAcumulado(compania,
                                                elementoInicial, elementoFinal,
                                                Integer.parseInt(anio),
                                                Integer.parseInt(mes), "''", 2)
                                .toString();

                parametrosRep.put(cPrDepSuma,
                                depreciacionSuma);
            }
            catch (NumberFormatException | SystemException e) {
                Logger.getLogger(DepreciacionMesDependenciaControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            parametrosRep.put(cPrDepSuma, 0.0);
        }

    }

    private void cargarFiltro() {
        if (depPeriodo) {
            reemplazar.put(filtro,
                            " AND DEPRECIAR.VLRDEPRECIACIONAJ <> 0 AND DEPRECIAR.VLRLIBROS = 0");
        }
        else if (depTotal) {
            reemplazar.put(filtro,
                            " AND DEPRECIAR.VLRDEPRECIACIONAJ = 0 AND DEPRECIAR.VLRLIBROS = 0");
        }
        else if ("Bodega NIIF".equals(filtroPor)) {
            reemplazar.put(filtro, " AND BODEGA.CLASE_BODEGA IN (20) ");
        }
        else if ("Servicio NIIF".equals(filtroPor)) {
            reemplazar.put(filtro,
                            " AND BODEGA.CLASE_BODEGA NOT IN (20,40,50)");
        }
        else if ("Inservibles NIIF".equals(filtroPor)) {
            reemplazar.put(filtro, " AND BODEGA.CLASE_BODEGA IN (40)");
        }
        else if ("Resposabilidades NIIF".equals(filtroPor)) {
            reemplazar.put(filtro, " AND BODEGA.CLASE_BODEGA IN (50) ");
        }
        else if ("Servicio en Comodato NIIF".equals(filtroPor)) {
            reemplazar.put(filtro,
                            " AND BODEGA.CLASE_BODEGA IN (20,40,50) AND DEPENDENCIA.COMODATO NOT IN (0)");
        }
        else if ("Servicio Interno NIIF".equals(filtroPor)) {
            reemplazar.put(filtro,
                            " AND BODEGA.CLASE_BODEGA  IN (20,40,50) AND DEPENDENCIA.COMODATO IN (0)");
        }
        else {
            reemplazar.put(filtro, "");
        }
    }

    private void generarIDepreciarMesDependencia(FORMATOS formato) {
        String consulta;
        String nombreReporte = filtroPor.equals(CLASEBODEGA)
            ? "000519IDepreciarMesDependenciaCB"
            : "000519IDepreciarMesDependencia";
        
        if(manejaInformeAmortizacion) {
        	nombreReporte = "002821IamortizacionMesDependencia_STR";
        }   
        
        if (manejaCentroRef.equals("SI") && agrupadoPor.equals("2")) {
        	nombreReporte = "002601IDepreciarMesDependenciaDetallado";
        	return;
         }
        
        if (colgaapNiif.equals("NO")
        		&& entAplicaNiif.equals("NO")
        		&& niif.equals("NO")) {
        	nombreReporte = filtroPor.equals(CLASEBODEGA)
                    ? "002503IDepreciarMesDependenciaCB"
                    : "002502IDepreciarMesDependencia";
        	ActivocolgaapNiif = true;
        }
        
        cargarFiltro();
        depreciaComodato();
        if (filtroPor == SERVICIO) {
            parametrosRep.put("PR_VISIBLECOMODATO", 1);
        }
        else {
            parametrosRep.put("PR_VISIBLECOMODATO", 0);
        }

        consulta = generarIDepreciarMesDependenciaConsulta();
        impirmirInforme(formato, nombreReporte, consulta);
    }

    private String generarIDepreciarMesDependenciaConsulta() {
        String consulta;
 
        if (manejaInformeAmortizacion) {
            reemplazar.put(filtro,
                            " AND INVENTARIO.DIGITOS_AGRUPACION_INVENTARIO <> -1");
        }
        else {
            reemplazar.put(filtro, "");
        }
        
        switch (filtroPor) {
        case BODEGA:
        	if(ActivocolgaapNiif) {
    			consulta = "002925IDepreciarMesDependencia";
        		break;
    		}else {
            consulta = "000519IDepreciarMesDependencia";
            break;
    		}
        case SERVICIO:
        	if(ActivocolgaapNiif) {
    			consulta = "002928IDepreciarMesDependencia_S";
        		break;
    		}else {
            consulta = "000519IDepreciarMesDependencia_S";
            break;
    		}
        case INSERVIBLES:
        	if(ActivocolgaapNiif) {
    			consulta = "002929IDepreciarMesDependencia_I";
        		break;
    		}else {
            consulta = "000519IDepreciarMesDependencia_I";
            break;
    		}
        case RESPONSABILIDADES:
        	if(ActivocolgaapNiif) {
    			consulta = "002930IDepreciarMesDependencia_R";
        		break;
    		}else {
            consulta = "000519IDepreciarMesDependencia_R";
            break;
    		}
        case CONSOLIDADO:
        	if(manejaCentroRef.equals("SI") && agrupadoPor.equals("2")) {
        		consulta = "002601IDepreciarMesDependenciaDetallado";
        		break;
        	}else {
        		if(ActivocolgaapNiif) {
        			consulta = "002931IDepreciarMesDependencia_CON";
            		break;
        		}else {
        			consulta = "000519IDepreciarMesDependencia_CON";
        			break;
        		}
        	}
        case SERVICIOCOMODATO:
        	if(ActivocolgaapNiif) {
    			consulta = "002932IDepreciarMesDependencia_SC";
        		break;
    		}else {
            consulta = "000519IDepreciarMesDependencia_SC";
            break;
    		}
        case SERVICIOINTERNO:
            consulta = "000519IDepreciarMesDependencia_SI";
            break;
        case CLASEBODEGA:
            consulta = "000519IDepreciarMesDependencia_CON";
            break;
        case "Bodega COLGAAP":
        	consulta = "000519IDepreciarMesDependenciaColgaap";
            break;
        case "Servicio COLGAAP":
        	consulta = "000519IDepreciarMesDependencia_S_Colgaap";
            break;
        case "Inservibles COLGAAP":
        	consulta = "000519IDepreciarMesDependencia_I_Colgaap";
            break;
        case "Servicio en Comodato COLGAAP":
        	consulta = "000519IDepreciarMesDependencia_SC_Colgaap";
            break;
        case "Consolidado COLGAAP":
        	consulta = "000519IDepreciarMesDependencia_CON_Colgaap";
            break;
        default:
            consulta = "800189ConsultasNiif";
            break;
        }
        return consulta;
    }

    private void generarIDepreciarMesDependenciaDetallado(FORMATOS formato) {
        String consulta = generarConsultaPorDependenciaDetallado();
        String nombreReporte = "000522IDepreciarMesDependenciaDetallado";
        cargarFiltro();
        depreciaComodato();

        if ((filtroPor == SERVICIO) &&
            "SI".equalsIgnoreCase(parametroComodato)) {
            parametrosRep.put(cPrVisibleDep, 1);
        }
        else {
            parametrosRep.put(cPrVisibleDep, 0);
        }
        impirmirInforme(formato, nombreReporte, consulta);
    }

    private void generarIDepreciarMesDependenciaGrupoNIIF(FORMATOS formato) {
        String consulta = "000514IDepreciarMesDependenciaGrupoNIIF";
        String nombreReporte = consulta;
        impirmirInforme(formato, nombreReporte, consulta);
    }

    private void generarIDepreciarMesDependenciaGrupoConsolidadoNIIF(
        FORMATOS formato) {
        String consulta = "000518IDepreciarMesDependenciaGrupoConsolidadoNIIF";
        String nombreReporte = consulta;
        impirmirInforme(formato, nombreReporte, consulta);
    }

    private void generarIDepreciarMesDependenciaCC(FORMATOS formato) {
        String consulta;
        String nombreReporte = "000521IDepreciarMesDependenciaCC";
        cargarFiltro();

        if (filtroPor == BODEGA) {
            parametrosRep.put(cPrDepSuma, 0);
            parametrosRep.put(cPrVisibleDep, 0);
        }
        else if (filtroPor == SERVICIO) {
            depreciaComodato();
            parametrosRep.put(cPrVisibleDep,
                            "SI".equalsIgnoreCase(parametroComodato) ? 1 : 0);

        }
        consulta = generarIDepreciarMesDependenciaCCConsulta();
        impirmirInforme(formato, nombreReporte, consulta);
    }

    private String generarIDepreciarMesDependenciaCCConsulta() {
        String consulta;
        switch (filtroPor) {
        case BODEGA:
            consulta = "000521IDepreciarMesDependenciaCC";
            break;
        case SERVICIO:
            consulta = "000521IDepreciarMesDependenciaCC_S";
            break;
        case INSERVIBLES:
            consulta = "000521IDepreciarMesDependenciaCC_I";
            break;
        case RESPONSABILIDADES:
            consulta = "000521IDepreciarMesDependenciaCC_R";
            break;
        case CONSOLIDADO:
            consulta = "000521IDepreciarMesDependenciaCC_CON";
            break;
        case SERVICIOCOMODATO:
            consulta = "000521IDepreciarMesDependenciaCC_SC";
            break;
        case SERVICIOINTERNO:
            consulta = "000521IDepreciarMesDependenciaCC_SI";
            break;
        default:
            consulta = "";
            break;
        }
        return consulta;
    }

    private void generarIDepreciarMesDependenciaGrupo(FORMATOS formato) {
        String consulta;
        String nombreReporte = "000523IDepreciarMesDependenciaGrupo";
        if (depPeriodo) {
            reemplazar.put(filtro,
                            " AND DEPRECIAR.VLRDEPRECIACION <> 0 AND DEPRECIAR.VLRLIBROS = 0");
        }
        else {
            reemplazar.put(filtro, "");
        }
        switch (filtroPor) {
        case BODEGA:
            consulta = "000523IDepreciarMesDependenciaGrupo";
            break;
        case SERVICIO:
            consulta = "000523IDepreciarMesDependenciaGrupo_S";
            break;
        case INSERVIBLES:
            consulta = "000523IDepreciarMesDependenciaGrupo_I";
            break;
        case RESPONSABILIDADES:
            consulta = "000523IDepreciarMesDependenciaGrupo_R";
            break;
        case CONSOLIDADO:
            consulta = "000523IDepreciarMesDependenciaGrupo_CON";
            break;
        case SERVICIOCOMODATO:
            consulta = "000523IDepreciarMesDependenciaGrupo_SC";
            break;
        case SERVICIOINTERNO:
            consulta = "000523IDepreciarMesDependenciaGrupo_SI";
            break;
        default:
            consulta = "";
            break;
        }
        impirmirInforme(formato, nombreReporte, consulta);
    }

    private String generarConsultaPorDependenciaDetallado() {
        // Genera el nombre de la consulta para el informe cuando se
        // seleccciona por dependencia Detallado
        String nomConsulta;
        switch (filtroPor) {
        case BODEGA:
            nomConsulta = "000522IDepreciarMesDependenciaDetallado";
            break;
        case SERVICIO:
            nomConsulta = "000522IDepreciarMesDependenciaDetallado_S";
            break;
        case INSERVIBLES:
            nomConsulta = "000522IDepreciarMesDependenciaDetallado_I";
            break;
        case RESPONSABILIDADES:
            nomConsulta = "000522IDepreciarMesDependenciaDetallado_R";
            break;
        case CONSOLIDADO:
            nomConsulta = "000522IDepreciarMesDependenciaDetallado_CON";
            break;
        case SERVICIOCOMODATO:
            nomConsulta = "000522IDepreciarMesDependenciaDetallado_SC";
            break;
        case SERVICIOINTERNO:
            nomConsulta = "000522IDepreciarMesDependenciaDetallado_SI";
            break;
        default:
            nomConsulta = "";
            break;
        }
        return nomConsulta;
    }
    
    private void generarInformeContraloria(FORMATOS formato) {
    	String consulta = "800670InformeContraloria";
    	String nombreReporte = consulta;
    	impirmirInforme(formato, nombreReporte, consulta);
    }

    public void cambiarckCxC() {
        cargarListacodigoccinicial();
    }

    public void seleccionarFilaCodigoElementoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = registroAux.getCampos().get(cCodigoElemento)
                        .toString();
        nombreElemInicial = registroAux.getCampos().get("NOMBRELARGO")
                        .toString();
        cargarListaCodigoElementoFinal();
        
        
        if (elementoInicial != null && !elementoInicial.trim().isEmpty()
                && elementoFinal != null && !elementoFinal.trim().isEmpty()) {

            validarInformeAmortizacion();

            if (manejaInformeAmortizacion && valorAmortizacion == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4481"));
            }
        }

    }

    public void seleccionarFilaCodigoElementoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoFinal = registroAux.getCampos().get(cCodigoElemento).toString();
        nombreElemFinal = registroAux.getCampos().get("NOMBRELARGO").toString();

        if (elementoInicial != null && !elementoInicial.trim().isEmpty()
                && elementoFinal != null && !elementoFinal.trim().isEmpty()) {

            validarInformeAmortizacion();

            if (manejaInformeAmortizacion && valorAmortizacion == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4481"));
            }
        }

    }


    public void seleccionarFilaCodigoccinicial(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        ccInicial = registroAux.getCampos().get(cCodigo).toString();
        nombreCCinicial = registroAux.getCampos().get(cNombre).toString();
        cargarListacodigoccfinal();
    }

    public void seleccionarFilaCodigoccfinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ccFinal = registroAux.getCampos().get(cCodigo).toString();
        nombreCCfinal = registroAux.getCampos().get(cNombre).toString();
    }

    public String getAgrupadoPor() {
        return agrupadoPor;
    }

    public void setAgrupadoPor(String agrupadoPor) {
        this.agrupadoPor = agrupadoPor;
    }

    public boolean isPorCC() {
        return porCC;
    }

    public void setPorCC(boolean porCC) {
        this.porCC = porCC;
    }

    public boolean isDepCuenta() {
        return depCuenta;
    }

    public void setDepCuenta(boolean depCuenta) {
        this.depCuenta = depCuenta;
    }

    public boolean isDepPeriodo() {
        return depPeriodo;
    }

    public void setDepPeriodo(boolean depPeriodo) {
        this.depPeriodo = depPeriodo;
    }

    public boolean isDepTotal() {
        return depTotal;
    }

    public void setDepTotal(boolean depTotal) {
        this.depTotal = depTotal;
    }

    public boolean isDepFecha() {
        return depFecha;
    }

    public void setDepFecha(boolean depFecha) {
        this.depFecha = depFecha;
    }

    public String getElementoInicial() {
        return elementoInicial;
    }

    public void setElementoInicial(String elementoInicial) {
        this.elementoInicial = elementoInicial;
    }

    public String getElementoFinal() {
        return elementoFinal;
    }

    public void setElementoFinal(String elementoFinal) {
        this.elementoFinal = elementoFinal;
    }

    public String getFiltroPor() {
        return filtroPor;
    }

    public void setFiltroPor(String filtroPor) {
        this.filtroPor = filtroPor;
    }

    public String getCcInicial() {
        return ccInicial;
    }

    public void setCcInicial(String ccInicial) {
        this.ccInicial = ccInicial;
    }

    public String getCcFinal() {
        return ccFinal;
    }

    public void setCcFinal(String ccFinal) {
        this.ccFinal = ccFinal;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNombreElemInicial() {
        return nombreElemInicial;
    }

    public void setNombreElemInicial(String nombreElemInicial) {
        this.nombreElemInicial = nombreElemInicial;
    }

    public String getNombreElemFinal() {
        return nombreElemFinal;
    }

    public void setNombreElemFinal(String nombreElemFinal) {
        this.nombreElemFinal = nombreElemFinal;
    }

    public String getNombreCCinicial() {
        return nombreCCinicial;
    }

    public void setNombreCCinicial(String nombreCCinicial) {
        this.nombreCCinicial = nombreCCinicial;
    }

    public String getNombreCCfinal() {
        return nombreCCfinal;
    }

    public void setNombreCCfinal(String nombreCCfinal) {
        this.nombreCCfinal = nombreCCfinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaFiltradoPor() {
        return listaFiltradoPor;
    }

    public void setListaFiltradoPor(List<Registro> listaFiltradoPor) {
        this.listaFiltradoPor = listaFiltradoPor;
    }

    /**
     * Retorna la lista listaAno
     *
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     *
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaCodigoElementoInicial() {
        return listaCodigoElementoInicial;
    }

    public void setListaCodigoElementoInicial(
        RegistroDataModelImpl listaCodigoElementoInicial) {
        this.listaCodigoElementoInicial = listaCodigoElementoInicial;
    }

    public RegistroDataModelImpl getListaCodigoElementoFinal() {
        return listaCodigoElementoFinal;
    }

    public void setListaCodigoElementoFinal(
        RegistroDataModelImpl listaCodigoElementoFinal) {
        this.listaCodigoElementoFinal = listaCodigoElementoFinal;
    }

    public RegistroDataModelImpl getListaCodigoccinicial() {
        return listaCodigoccinicial;
    }

    public void setListaCodigoccinicial(
        RegistroDataModelImpl listaCodigoccinicial) {
        this.listaCodigoccinicial = listaCodigoccinicial;
    }

    public RegistroDataModelImpl getListaCodigoccfinal() {
        return listaCodigoccfinal;
    }

    public void setListaCodigoccfinal(
        RegistroDataModelImpl listaCodigoccfinal) {
        this.listaCodigoccfinal = listaCodigoccfinal;
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado
    }

    public boolean isVisibleCC() {
        return visibleCC;
    }

    public void setVisibleCC(boolean visibleCC) {
        this.visibleCC = visibleCC;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public boolean isVisibleAgrupacion() {
        return visibleAgrupacion;
    }

    public void setVisibleAgrupacion(boolean visibleAgrupacion) {
        this.visibleAgrupacion = visibleAgrupacion;
    }

    public boolean isVisibleFiltros() {
        return visibleFiltros;
    }

    public void setVisibleFiltros(boolean visibleFiltros) {
        this.visibleFiltros = visibleFiltros;
    }

    public boolean isVisibleFecha() {
        return visibleFecha;
    }

    public void setVisibleFecha(boolean visibleFecha) {
        this.visibleFecha = visibleFecha;
    }

    public List<Registro> getListaMeses() {
        return listaMeses;
    }

    public void setListaMeses(List<Registro> listaMeses) {
        this.listaMeses = listaMeses;
    }

    public boolean isexcelpl() {
        return excelpl;
    }

    public void setexcelpl(boolean excelpl) {
        this.excelpl = excelpl;
    }

	public String getManejaCentroRef() {
		return manejaCentroRef;
	}

	public void setManejaCentroRef(String manejaCentroRef) {
		this.manejaCentroRef = manejaCentroRef;
	}

	/**
	 * @return the ckContraloria
	 */
	public boolean isCkContraloria() {
		return ckContraloria;
	}

	/**
	 * @param ckContraloria the ckContraloria to set
	 */
	public void setCkContraloria(boolean ckContraloria) {
		this.ckContraloria = ckContraloria;
	}
	
	 public boolean isActivocolgaapNiif() {
		return ActivocolgaapNiif;
	}

	 public void setActivocolgaapNiif(boolean activocolgaapNiif) {
		ActivocolgaapNiif = activocolgaapNiif;
	}
    

}