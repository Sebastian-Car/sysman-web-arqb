package com.sysman.almacen;

import com.sysman.almacen.enums.InformeproyectosControladorUrlEnum;
import com.sysman.almacen.enums.KardexControladorEnum;
import com.sysman.almacen.enums.KardexControladorUrlEnum;
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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 18/02/2016
 * @version 2, 04/05/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss. Tambien los llamados a funciones,
 * procedimientos y metodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class KardexControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String cCodigoElemento;
    private final String cStrsql;
    private final String cTarjetaKardex;
    private final String cTarjetaKardexDev;
    private final String cParReporte;
    private final String cOpcionMenu1;
    private final String cOpcionMenu2;
    private final String cOpcionMenu3;
    private final String cOpcionMenu4;
    private final String cOpcionMenu5;
    private final String cOpcionMenu6;
    private final String cOpcionMenu7;
    private final String cOpcionMenu8;
    private String titulo;
    private boolean resumen;
    private String elementoHasta;
    private String elementoDesde;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreElementoIni;
    private String nombreElementoFin;
    private String agrupacion;
    private String elementoDesdeVisible;
    private String elementoHastaVisible;
    private boolean resumenVisible;
    private String tipo;
    private String cnsConsumo = "C";
    private String cnsDevolutivo = "D";
    private String cnsComodato = "E";
    // Codigo de la bodega seleccionada
    private String bodega;
    // Nombre de la bodega seleccionada
    private String nombreBodega;
    // Almacena el valor del parametro: ALMACEN MULTIBODEGAS PROYECTOS
    private boolean almacenMultiProyectos;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoHasta;
    private RegistroDataModelImpl listacmbElementoDesde;
    // Lista que contiene los datos obtenidos del consumo del servicio para las bodegas
    private RegistroDataModelImpl listabodega;   
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Clase que permite generar los informes de kardex desde las
     * diferentes opciones de menu
     */
    public KardexControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigoElemento = "CODIGOELEMENTO";
        cStrsql = "PR_STRSQL";
        cTarjetaKardex = "000492ITarjetaKardex";
        cTarjetaKardexDev = "000516ITarjetaKardexDev";
        cParReporte = "parReporte";
        cOpcionMenu1 = "1004020201";
        cOpcionMenu2 = "1004020202";
        cOpcionMenu3 = "1004020208";
        cOpcionMenu4 = "1004020209";
        cOpcionMenu5 = "1004020203";
        cOpcionMenu6 = "1004020206";
        cOpcionMenu7 = "1004020207";
        cOpcionMenu8 = "1004020205";
        try {
            numFormulario = GeneralCodigoFormaEnum.KARDEX_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
        cargarListacmbElementoDesde();
        cargarListaBodega();
        cargarNombreBodegaPorDefecto(); 

    }
    /**
     * Carga la lista de bodegas desde el servicio segun la compania y la clase de bodega.
     */
    public void cargarListaBodega() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CLASE_BODEGA", "20");
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(KardexControladorUrlEnum.URL135009.getValue());

        listabodega = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());
    }
    
    public void cargarListacmbElementoHasta() {
        if (cOpcionMenu6.equals(SessionUtil.getMenuActual())
            || cOpcionMenu1.equals(SessionUtil.getMenuActual())
            || cOpcionMenu3.equals(SessionUtil.getMenuActual())) {
            // KCI
            // KCB
            // KCCERO
            tipo = cnsConsumo;
        }
        else if (cOpcionMenu2.equals(SessionUtil.getMenuActual())
            || cOpcionMenu4.equals(SessionUtil.getMenuActual())
            || cOpcionMenu5.equals(SessionUtil.getMenuActual())
            || cOpcionMenu8.equals(SessionUtil.getMenuActual())) {
            // KDB
            // KDS
            // KDE
            // KDI
            tipo = cnsDevolutivo;
        }
        else if (cOpcionMenu7.equals(SessionUtil.getMenuActual())) {
            // KEB
            tipo = cnsComodato;
        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        KardexControladorUrlEnum.URL7809
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(KardexControladorEnum.TIPOELEMENTO.getValue(),
                        tipo);
        param.put(KardexControladorEnum.ELEMENTODESDE.getValue(),
                        elementoDesde);
        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarListacmbElementoDesde() {
        if (cOpcionMenu6.equals(SessionUtil.getMenuActual())
            || cOpcionMenu1.equals(SessionUtil.getMenuActual())
            || cOpcionMenu3.equals(SessionUtil.getMenuActual())) {
            // KCI
            // KCB
            // KCCERO
            // 112044
            tipo = cnsConsumo;
        }
        else if (cOpcionMenu2.equals(SessionUtil.getMenuActual()) ||
            cOpcionMenu4.equals(SessionUtil.getMenuActual()) ||
            cOpcionMenu5.equals(SessionUtil.getMenuActual())
            || cOpcionMenu8.equals(SessionUtil.getMenuActual())) {
            // KDB
            // KDS
            // KDE
            // KDI
            tipo = cnsDevolutivo;
        }
        else if (cOpcionMenu7.equals(SessionUtil.getMenuActual())) {// KEB
            tipo = cnsComodato;
        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        KardexControladorUrlEnum.URL11488
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(KardexControladorEnum.TIPOELEMENTO.getValue(),
                        tipo);
        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarNombreBodegaPorDefecto() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            
            String url = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(KardexControladorUrlEnum.URL135014.getValue())
                    .getUrl();
            
            Object respuestaObj = requestManager.getList(url, param);
            
            List<Registro> lista = null;
            if (respuestaObj instanceof List) {
                lista = RegistroConverter.toListRegistro((List) respuestaObj);
            }
            
            if (lista != null && !lista.isEmpty()) {
                Registro primerRegistro = lista.get(0);
                
                bodega = SysmanFunciones.nvl(
                    primerRegistro.getCampos().get(GeneralParameterEnum.CODIGO.getName()), 
                    ""
                ).toString();
                
                nombreBodega = SysmanFunciones.nvl(
                    primerRegistro.getCampos().get("NOMBRE"), 
                    ""
                ).toString();
            } else {
                bodega = "";
                nombreBodega = "";
            }
            
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }
    
    public void cambiarHasta() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDesde() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        presentarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        presentarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void auxPresentarInforme(HashMap<String, Object> remplazar,
        Map<String, Object> parametros)
                        throws NamingException, SQLException, SystemException,
                        ParseException {
        if (cOpcionMenu1.equals(SessionUtil.getMenuActual())) {
            // "KCB"
            casoKCB(parametros, remplazar);
        }
        else if (cOpcionMenu2.equals(SessionUtil.getMenuActual())) {
            // KDB
            casoKDB(parametros, remplazar);
        }
        else if (cOpcionMenu3.equals(SessionUtil.getMenuActual())
            || cOpcionMenu4.equals(SessionUtil.getMenuActual())) {
            // KDCERO
            // KCCERO
            casoKDCero(parametros, remplazar);
        }
        else if (cOpcionMenu5.equals(SessionUtil.getMenuActual())) {
            // KDE
            casoKDE(parametros, remplazar);
        }
        else if (cOpcionMenu6.equals(SessionUtil.getMenuActual())) {
            // KCI
            casoKCI(parametros, remplazar);
        }
        else if (cOpcionMenu7.equals(SessionUtil.getMenuActual())) {
            // KEB
            casoKEB(parametros, remplazar);
        }
        else if (cOpcionMenu8.equals(SessionUtil.getMenuActual())) {
            // KEB
            casoKDI(parametros, remplazar);
        }
    }

    private void presentarInforme(FORMATOS formatos) {
        try {
            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO",
                            modulo, new Date(), true);
            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put("parametro", parametro);
            remplazar.put("elementoInicial", "'" + elementoDesde + "'");
            remplazar.put("elementoFinal", "'" + elementoHasta + "'");
            remplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal)
                                            .replace("00:00:00", "23:59:59"));
            remplazar.put("tipo", "'" + tipo + "'");
            
            remplazar.put("bodega", "'" + bodega + "'");

            Map<String, Object> parametros = new HashMap<>();

            auxPresentarInforme(remplazar, parametros);

            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_FECHAS", idioma.getString("TB_TB3127").replace(
                            "#fechaInicial#",
                            SysmanFunciones.convertirAFechaCadena(fechaInicial,
                                            "dd MMMMM yyyy"))
                            .replace("#fechaFinal#", SysmanFunciones
                                            .convertirAFechaCadena(
                                                            fechaFinal,
                                                            "dd MMMMM yyyy")));
            parametros.put("PR_BODEGA", bodega);
            parametros.put("PR_TIPOBODEGA", nombreBodega);

            archivoDescarga = JsfUtil.exportarStreamed(
                            parametros.get(cParReporte).toString(), parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (NamingException | SQLException | ParseException
                        | OutOfMemoryError | JRException | IOException
                        | SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void casoKDI(Map<String, Object> parametros,
        HashMap<String, Object> remplazar)
                        throws ParseException {
        remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
                        SysmanFunciones.convertirAFechaCadena(
                                        fechaInicial,
                                        KardexControladorEnum.FORMATOFECHA
                                                        .getValue()));
        remplazar.put("baseUltimoMov", Reporteador.resuelveConsulta(
                        "800111MovAlmacenFechaHora",
                        Integer.parseInt(modulo), remplazar));

        parametros.put(cStrsql, Reporteador.resuelveConsulta(
                        "000530ITarjetaKardexInserv",
                        Integer.parseInt(modulo), remplazar));
        parametros.put(cParReporte, "000530ITarjetaKardexInserv");
    }

    private void casoKCI(Map<String, Object> parametros,
        HashMap<String, Object> remplazar)
                        throws ParseException {
        remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
                        SysmanFunciones.convertirAFechaCadena(
                                        fechaInicial,
                                        KardexControladorEnum.FORMATOFECHA
                                                        .getValue()));
        remplazar.put(KardexControladorEnum.ULTIMOSERIE.getValue(),
                        Reporteador.resuelveConsulta(
                                        KardexControladorEnum.CONSULTA800110
                                                        .getValue(),
                                        Integer.parseInt(modulo), remplazar));
        parametros.put(cStrsql, Reporteador.resuelveConsulta(cTarjetaKardexDev,
                        Integer.parseInt(modulo), remplazar));
        parametros.put(cParReporte, cTarjetaKardexDev);
    }

    private void casoKEB(Map<String, Object> parametros,
        HashMap<String, Object> remplazar)
                        throws ParseException {
        remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
                        SysmanFunciones.convertirAFechaCadena(
                                        fechaInicial,
                                        KardexControladorEnum.FORMATOFECHA
                                                        .getValue()));
        remplazar.put(KardexControladorEnum.ULTIMOSERIE.getValue(),
                        Reporteador.resuelveConsulta(
                                        KardexControladorEnum.CONSULTA800110
                                                        .getValue(),
                                        Integer.parseInt(modulo), remplazar));
        parametros.put(cStrsql, Reporteador.resuelveConsulta(cTarjetaKardex,
                        Integer.parseInt(modulo), remplazar));
        parametros.put(cParReporte, cTarjetaKardex);
    }

    private void casoKDE(Map<String, Object> parametros,
        HashMap<String, Object> remplazar)
                        throws ParseException {
        remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
                        SysmanFunciones.convertirAFechaCadena(
                                        fechaInicial,
                                        KardexControladorEnum.FORMATOFECHA
                                                        .getValue()));
        remplazar.put(KardexControladorEnum.ULTIMOSERIE.getValue(),
                        Reporteador.resuelveConsulta(
                                        KardexControladorEnum.CONSULTA800110
                                                        .getValue(),
                                        Integer.parseInt(modulo), remplazar));
        parametros.put(cStrsql, Reporteador.resuelveConsulta(cTarjetaKardex,
                        Integer.parseInt(modulo), remplazar));
        parametros.put(cParReporte, cTarjetaKardex);
    }

    private void casoKDCero(Map<String, Object> parametros,
        HashMap<String, Object> remplazar)
                        throws ParseException {
        remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
                        SysmanFunciones.convertirAFechaCadena(
                                        fechaInicial,
                                        KardexControladorEnum.FORMATOFECHA
                                                        .getValue()));
        remplazar.put("baseUltimoMov", Reporteador.resuelveConsulta(
                        "800111MovAlmacenFechaHora",
                        Integer.parseInt(modulo), remplazar));
        remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
                        SysmanFunciones.convertirAFechaCadena(
                                        fechaFinal,
                                        KardexControladorEnum.FORMATOFECHA
                                                        .getValue()));
        remplazar.put("baseUltimoMovIni", Reporteador.resuelveConsulta(
                        "800109MovAlmacenIniFechaHora",
                        Integer.parseInt(modulo), remplazar));

        parametros.put(cStrsql, Reporteador.resuelveConsulta(
                        "000529ITarjetaKardexSaldo0",
                        Integer.parseInt(modulo),
                        remplazar));
        parametros.put(cParReporte, "000529ITarjetaKardexSaldo0");
    }

    private void casoKDB(Map<String, Object> parametros,
        HashMap<String, Object> remplazar)
                        throws ParseException {
        String parReporte = resumen ? "000520ITarjetaKardexDevolutivoResumen"
            : cTarjetaKardexDev;
        remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
                        SysmanFunciones.convertirAFechaCadena(
                                        fechaInicial,
                                        KardexControladorEnum.FORMATOFECHA
                                                        .getValue()));
        remplazar.put(KardexControladorEnum.ULTIMOSERIE.getValue(),
                        Reporteador.resuelveConsulta(
                                        KardexControladorEnum.CONSULTA800110
                                                        .getValue(),
                                        Integer.parseInt(modulo), remplazar));
        parametros.put(cStrsql, Reporteador.resuelveConsulta(
                        cTarjetaKardexDev,
                        Integer.parseInt(modulo), remplazar));
        parametros.put(cParReporte, parReporte);
    }
    
	private void casoKCB(Map<String, Object> parametros,
		    HashMap<String, Object> remplazar)
		                    throws ParseException, SystemException {
		    String formatoCalidad = ejbSysmanUtil.consultarParametro(
		                    compania,
		                    "FORMATO CALIDAD",
		                    modulo, new Date(), true);
		    String parReporte;
		    String consultaSQL;  
		    
		    if (almacenMultiProyectos) {
		        parReporte = "002876ITarjetaKardexPorBodega";
		        consultaSQL = "002876ITarjetaKardexPorBodega";  
		    }
		    else if ("SI".equals(formatoCalidad)) {
		        parReporte = "000531ITarjetaKardexCOS";
		        consultaSQL = cTarjetaKardex; 
		    }
		    else {
		        parReporte = cTarjetaKardex;
		        consultaSQL = cTarjetaKardex;  
		    }

		    remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
		                    SysmanFunciones.convertirAFechaCadena(
		                                    fechaInicial,
		                                    KardexControladorEnum.FORMATOFECHA
		                                                    .getValue()));
		    remplazar.put(KardexControladorEnum.ULTIMOSERIE.getValue(),
		                    Reporteador.resuelveConsulta(
		                                    KardexControladorEnum.CONSULTA800110
		                                                    .getValue(),
		                                    Integer.parseInt(modulo), remplazar));
		    remplazar.put(KardexControladorEnum.FECHAHASTA.getValue(),
		                    SysmanFunciones.convertirAFechaCadena(
		                                    fechaFinal,
		                                    KardexControladorEnum.FORMATOFECHA
		                                                    .getValue()));
		    remplazar.put("baseUltimoMovIni", Reporteador.resuelveConsulta(
		                    "800109MovAlmacenIniFechaHora",
		                    Integer.parseInt(modulo), remplazar));
		    
		   
		    parametros.put(cStrsql, Reporteador.resuelveConsulta(consultaSQL,
		                    Integer.parseInt(modulo), remplazar));
		    parametros.put(cParReporte, parReporte);
		}	   
    
    /**
     * Evento que se ejecuta al seleccionar una fila en la tabla de bodegas.
     * Obtiene el codigo y el nombre de la bodega seleccionada.
     *
     * @param event evento generado por la seleccion de la fila
     */
    public void seleccionarFilaBodega(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bodega = SysmanFunciones.nvl(
            registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), 
            ""
        ).toString();
        
        nombreBodega= SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "")
                .toString();
       
    }
    
    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigoElemento), "")
                        .toString();
        nombreElementoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigoElemento), "")
                        .toString();
        nombreElementoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();

        elementoHasta = null;
        nombreElementoFin = null;
        cargarListacmbElementoHasta();
    }

    @Override
    public void abrirFormulario() {
           try {
        // <CODIGO_DESARROLLADO>
        if (cOpcionMenu6.equals(SessionUtil.getMenuActual())
            || cOpcionMenu1.equals(SessionUtil.getMenuActual())
            || cOpcionMenu3.equals(SessionUtil.getMenuActual())) {
            // KCI
            // KCB
            // KCCERO
            cargarTitulo();
        }
        else if (cOpcionMenu2.equals(SessionUtil.getMenuActual()) ||
            cOpcionMenu4.equals(SessionUtil.getMenuActual()) ||
            cOpcionMenu5.equals(SessionUtil.getMenuActual())) {
            // KDB
            // KDS
            // KDE
            // KDI
            cargarTituloKdCero();
        }
        else if (cOpcionMenu8.equals(SessionUtil.getMenuActual())) {
            // 112044
            tipo = cnsDevolutivo;
            cargarTituloKdCero();
        }
        else if (cOpcionMenu7.equals(SessionUtil.getMenuActual())) {// KEB
            // 112044
            tipo = cnsComodato;
            titulo = idioma.getString("TB_TB3128");
        }
             
        almacenMultiProyectos = "SI".equals(SysmanFunciones.nvl(
                ejbSysmanUtil.consultarParametro(compania, "ALMACEN MULTIBODEGAS PROYECTOS", 
                    modulo, 
                    new Date(), true), 
                "NO")
            );
        
        
           }
           catch (Exception ex) {
               logger.error(ex.getMessage(), ex);
               SessionUtil.redireccionarMenuPermisos();
           }

    }

    private void cargarTituloKdCero() {
        if (cOpcionMenu2.equals(SessionUtil.getMenuActual())) {
            titulo = idioma.getString("TB_TB3120");
            resumenVisible = true;
        }
        else if (cOpcionMenu4.equals(SessionUtil.getMenuActual())) {
            titulo = idioma.getString("TB_TB3121");
        }
        else if ("1004020204".equals(SessionUtil.getMenuActual())) {
            titulo = idioma.getString("TB_TB3122");
        }
        else if (cOpcionMenu8.equals(SessionUtil.getMenuActual())) {
            titulo = idioma.getString("TB_TB3123");
        }
        else if (cOpcionMenu5.equals(SessionUtil.getMenuActual())) {
            titulo = idioma.getString("TB_TB3124");
        }
        else {
            titulo = "";
        }
    }

    private void cargarTitulo() {
        if (cOpcionMenu6.equals(SessionUtil.getMenuActual())) {
            titulo = idioma.getString("TB_TB3125");
        }
        else if (cOpcionMenu1.equals(SessionUtil.getMenuActual())
            || cOpcionMenu3.equals(SessionUtil.getMenuActual())) {
            titulo = idioma.getString("TB_TB3126");
        }
        else {
            titulo = "";
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean getResumen() {
        return resumen;
    }

    public void setResumen(boolean resumen) {
        this.resumen = resumen;
    }

    public String getElementoHasta() {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    public String getElementoDesde() {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getNombreElementoIni() {
        return nombreElementoIni;
    }

    public void setNombreElementoIni(String nombreElementoIni) {
        this.nombreElementoIni = nombreElementoIni;
    }

    public String getNombreElementoFin() {
        return nombreElementoFin;
    }

    public void setNombreElementoFin(String nombreElementoFin) {
        this.nombreElementoFin = nombreElementoFin;
    }

    public String getElementoDesdeVisible() {
        return elementoDesdeVisible;
    }

    public void setElementoDesdeVisible(String elementoDesdeVisible) {
        this.elementoDesdeVisible = elementoDesdeVisible;
    }

    public String getElementoHastaVisible() {
        return elementoHastaVisible;
    }

    public void setElementoHastaVisible(String elementoHastaVisible) {
        this.elementoHastaVisible = elementoHastaVisible;
    }

    public boolean isResumenVisible() {
        return resumenVisible;
    }

    public void setResumenVisible(boolean resumenVisible) {
        this.resumenVisible = resumenVisible;
    }

    public String getAgrupacion() {
        return agrupacion;
    }

    public void setAgrupacion(String agrupacion) {
        this.agrupacion = agrupacion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }

    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    /**
     * @return the listabodega
     */
    public RegistroDataModelImpl getListabodega() {
        return listabodega;
    }

    /**
     * @param listabodega the listabodega to set
     */
    public void setListabodega(RegistroDataModelImpl listabodega) {
        this.listabodega = listabodega;
    }

    /**
     * @return the bodega
     */
    public String getBodega() {
        return bodega;
    }

    /**
     * @param bodega the bodega to set
     */
    public void setBodega(String bodega) {
        this.bodega = bodega;
    }

    /**
     * @return the almacenMultiProyectos
     */
    public boolean isAlmacenMultiProyectos() {
        return almacenMultiProyectos;
    }

    /**
     * @param almacenMultiProyectos the almacenMultiProyectos to set
     */
    public void setAlmacenMultiProyectos(boolean almacenMultiProyectos) {
        this.almacenMultiProyectos = almacenMultiProyectos;
    }

    
}