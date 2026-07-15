package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.ejb.EjbNominaOchoRemote;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.ejb.EjbNominaNueveGeneralRemote;
import com.sysman.nomina.enums.CalculoPrenominaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jacelas
 * @version 1, 13/07/2015
 *
 * @author mzanguna
 * @version 2, 05/01/2018 - Proceso de refactoring
 * 
 * @author ybecerra
 * @version 3, 23/02/2018 - Se agrego boton de PDF
 *
 *
 */
@ManagedBean
@ViewScoped

public class CalculoPrenominaControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String moduloNomina;
    private final String nombreCompania;
    private final String procesoSesion;
    private final String anioSesion;
    private final String mesSesion;
    private final String periodoSesion;
    private final String nombreProceso;
    private final boolean activo;
    private final String nombrePeriodo;
    private final String idEmpleadoCons;
    private final String msgInterrumpidaCons;
    private final String inicioCons;
    private final String msgTbCons;
    // <DECLARAR_ATRIBUTOS>
    private String proceso;
    private String ano;
    private String periodo;
    private String mes;
    private String opcion;
    private String empleadoInicial;
    private String empleado;
    private String empleadoFinal;
    private String definicionFormulario;
    private String centroCostos;
    private String estadoActual;
    private String tipo;
    private boolean retro;
    private boolean deducibles;
    private String inicial = "";
    private String fin = "";
    private StreamedContent archivoDescarga;
    private String resultadoLiquidacion = "";
    private boolean cargarMensaje = false;
    private boolean manBeneficios = true; //JM 14/01/2025 CC 643
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCencos;
    private List<Registro> listaTipo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoEmpleado;
    private RegistroDataModelImpl listaCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    @EJB
    private EjbNominaDosRemote ejbNominaDos;

    @EJB
    private EjbNominaSeisRemote ejbNominaSeis;

    @EJB
    private EjbNominaOchoRemote ejbNominaOcho;
    
    @EJB
    private EjbNominaNueveGeneralRemote ejbNominaNueve;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de CalculoPrenominaControlador
     */
    public CalculoPrenominaControlador() {
        super();
        // 62
        numFormulario = GeneralCodigoFormaEnum.CALCULO_PRENOMINA_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        moduloNomina = SessionUtil.getModulo();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        procesoSesion = (String) SessionUtil.getSessionVar("procesoNomina");
        anioSesion = (String) SessionUtil.getSessionVar("anioNomina");
        mesSesion = (String) SessionUtil.getSessionVar("mesNomina");
        activo = (boolean) SessionUtil.getSessionVar("periodoActivo");
        nombrePeriodo = (String) SessionUtil
                        .getSessionVar("nombrePeriodoNomina");
        periodoSesion = (String) SessionUtil.getSessionVar("periodoNomina");
        nombreProceso = (String) SessionUtil
                        .getSessionVar("nombreProcesoNomina");
        
			       
 
        	 
	
        idEmpleadoCons = "ID_DE_EMPLEADO";
        msgInterrumpidaCons = "MSM_TRANS_INTERRUMPIDA";
        inicioCons = "Inicio";

        msgTbCons = "TB_TB1766";

        opcion = "1";
        cargarNombreEtiqueta();
        try {
            registro = new Registro(new HashMap<String, Object>());
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(CalculoPrenominaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoEmpleado();
        cargarListaCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCencos();
        cargarListaTipo();
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
        asignarOrigenDatos();
        iniciarListas();
        if (getParametro("MANEJA BENEFICIOS LIQUIDACION","NO").equalsIgnoreCase("NO")) {
        	manBeneficios = false;
		}//JM 14/01/2025 CC 643
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        // Metodo heredado de la clase BeanBase

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCencos
     */
    public void cargarListaCencos() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCencos = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CalculoPrenominaControladorUrlEnum.URL0002
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(CalculoPrenominaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipo
     */
    public void cargarListaTipo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CalculoPrenominaControladorUrlEnum.URL0003
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(CalculoPrenominaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoPrenominaControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, idEmpleadoCons);

    }

    /**
     * 
     * Carga la lista listaCodigoEmpleado
     */
    public void cargarListaCodigoEmpleado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoPrenominaControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, idEmpleadoCons);

    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoPrenominaControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, idEmpleadoCons);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarAplicable() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoInicial = (registroAux.getCampos()
                        .get(idEmpleadoCons)).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoEmpleado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = (registroAux.getCampos().get(idEmpleadoCons))
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoFinal = (registroAux.getCampos()
                        .get(idEmpleadoCons)).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton revisarencargos en la
     * vista
     *
     */
    public void oprimirrevisarencargos() {
        Date fechaini;
        Date fechafini;
        Integer generados = 0;
        ConectorPool conexion = new ConectorPool();
        Statement consulta = null;
        ResultSet result = null;
        archivoDescarga = null;
        try {

            fechaini = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(mesSesion),
                            Integer.parseInt(periodoSesion), true, false);

            fechafini = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(mesSesion),
                            Integer.parseInt(periodoSesion), false, false);

            generados = (int) ejbNominaDos.getIncluirNovedadEncargos(
                            Integer.parseInt(mesSesion),
                            Integer.parseInt(periodoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(procesoSesion), compania, fechaini,
                            fechafini, SessionUtil.getUser().getCodigo(),0);
            
            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("fechaIni", SysmanFunciones.convertirAFechaCadena(fechaini));
            reemplazos.put("fechaFin", SysmanFunciones.convertirAFechaCadena(fechafini));

            String sql = Reporteador.resuelveConsulta("800729RevisarEncargos", Integer.parseInt(SessionUtil.getModulo()),
            		reemplazos);

            conexion.conectar(ConectorPool.ESQUEMA_SYSMAN);

            consulta = conexion.getConection().createStatement();
            result = consulta.executeQuery(sql);

            if (result.next()) {
            	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, "800729RevisarEncargos");
            }
            FacesContext context = FacesContext.getCurrentInstance();
            String mensaje = idioma.getString("TB_TB2559").replace(
                            "#$generados#$", String.valueOf(generados));
            context.addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_INFO, inicioCons, mensaje));

        }
        catch (NumberFormatException | SystemException | JRException | IOException | SQLException | DRException | SysmanException | NamingException | ParseException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgTbCons) + ex.getMessage());
            Logger.getLogger(CalculoPrenominaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton revisarlicencias en la
     * vista
     *
     */
    public void oprimirrevisarlicencias() {
        // <CODIGO_DESARROLLADO>
        try {

            Date fechaini = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(mesSesion),
                            Integer.parseInt(periodoSesion), true, false);

            Date fechafini = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(mesSesion),
                            Integer.parseInt(periodoSesion), false, false);

            int generados = (int) ejbNominaDos.getIncluirNovedadLicencias(
                            Integer.parseInt(mesSesion),
                            Integer.parseInt(periodoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(procesoSesion), compania, fechaini,
                            fechafini,
                            SessionUtil.getUser().getCodigo());

            FacesContext context = FacesContext.getCurrentInstance();
            String mensaje = idioma.getString("TB_TB2560").replace(
                            "#$generados#$", String.valueOf(generados));
            context.addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_INFO, inicioCons, mensaje));
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
            Logger
                            .getLogger(CalculoPrenominaControlador.class
                                            .getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PARAMETROSRETRO en la
     * vista
     *
     */
    public void oprimirPARAMETROSRETRO() {
        SessionUtil.cargarModal("105", moduloNomina);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Calcular en la vista
     *
     */
    public void oprimirCalcular() {

        // <CODIGO_DESARROLLADO>

        if (!cargarMensaje) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB3993"));
            cargarMensaje = true;
            return;
        }

        String mensaje = "";
        archivoDescarga = null;
        try {
            String rutina = "FC_PROC"
                + SysmanFunciones.padl(procesoSesion, 2, "0");
            asignarValoresIniyFin();

            mensaje = ejbNominaSeis.calcularPrenomina(compania,
                            Integer.parseInt(periodoSesion),
                            Integer.parseInt(procesoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(mesSesion), "1", inicial, fin,
                            rutina, SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(mensaje);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);

            generarComprimidoInforme("000070ReporteLiquidacion");

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void oprimirCalcularBeneficios() {

        // <CODIGO_DESARROLLADO>
        String mensaje = "";
        archivoDescarga = null;
        try {
            String rutina = "BENEFICIOS";
            asignarValoresIniyFin();

            mensaje = ejbNominaSeis.calcularPrenomina(compania,
                            Integer.parseInt(periodoSesion),
                            Integer.parseInt(procesoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(mesSesion), "1", inicial, fin,
                            rutina, SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(mensaje);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);

            //generarComprimidoInforme("000070ReporteLiquidacion");

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton revisarextras en la vista
     *
     */
    public void oprimirrevisarextras() {
        archivoDescarga = null;
        generarHojaDatos("800093revisarextras");

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton RevisarNumeroH en la vista
     *
     */
    public void oprimirRevisarNumeroH() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarHojaDatos("800094revisarHoraExtr");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton RevisarSumaDes en la vista
     *
     *
     */
    public void oprimirRevisarSumaDes() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarHojaDatos("800095revisarSumaDesc");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirInconsistencias() {
        asignarValoresIniyFin();

        try {
            ejbNominaOcho.identificarNovedadesAntesDeLiquidar(compania,
                            Integer.parseInt(procesoSesion),
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(mesSesion),
                            Integer.parseInt(periodoSesion), "00000", "99999",
                            SessionUtil.getUser().getCodigo());
            generarComprimidoInforme("001616ReporteLiquidacionInicial");
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void oprimirPrevalidarPlano() {
    	//<CODIGO_DESARROLLADO>
    	try {
    		String reporte = "800581PrevalidarPlanoPila";

    		// PARAMETROS DE REEMPLAZO EN LA CONSULTA
    		HashMap<String, Object> reemplazar = new HashMap<>();
    		reemplazar.put("anio", anioSesion);
    		reemplazar.put("proceso",procesoSesion);
    		reemplazar.put("mes",mesSesion);
    		reemplazar.put("periodo",periodoSesion);

    		String sql= Reporteador.resuelveConsulta(reporte,
    				Integer.parseInt(SessionUtil.getModulo()),
    				reemplazar);

    		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, reporte);

    	} catch (JRException | IOException | SQLException | DRException | SysmanException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}         
    	//</CODIGO_DESARROLLADO>
    }
    
    public void oprimirrevisarfondo() {
    	


    	try {
			ejbNominaNueve.verificarcalculofondosolidaridad(compania,
			        Integer.parseInt(periodoSesion),
			        Integer.parseInt(procesoSesion),
			        Integer.parseInt(anioSesion),
			        Integer.parseInt(mesSesion), 
			        SessionUtil.getUser().getCodigo().toString());
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB81"));
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		} catch (SystemException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
    	
    	  
    }  

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que asigna valores a las variables de inicial y fin
     * dependiendo de la opcion seleccionada
     */
    public void asignarValoresIniyFin() {
        if ("1".equals(opcion)) {
            inicial = "00000";
            fin = "99999";
        }
        if ("2".equals(opcion)) {
            inicial = empleado;
            fin = empleado;
        }
        if ("3".equals(opcion)) {
            inicial = empleadoInicial;
            fin = empleadoFinal;
        }
        if ("4".equals(opcion)) {
            inicial = "CC";
            fin = centroCostos;
        }
        if ("5".equals(opcion)) {
            inicial = "EA";
            fin = estadoActual;
        }
        if ("6".equals(opcion)) {
            inicial = "TE";
            fin = tipo;
        }
    }

    /**
     * 
     */
    public void llamarComando() {
        JsfUtil.agregarMensajeInformativo(resultadoLiquidacion);
    }

    /**
     * @param n
     * @param l
     * @return
     */
    public String replicate(String n, int l) {
        StringBuilder aux = new StringBuilder();
        for (int i = 0; i < l; i++) {
            aux.append(aux + n);
        }
        return aux.toString();
    }

    /**
     * 
     */
    public void generarComprimidoInforme(String nombreReporte) {

        try {
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBREEMPRESA", nombreCompania);
            parametros.put("PR_EQUIPO", "#EQUIPO#");

            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL GERENTE", moduloNomina,
                            new Date(), false);

            String nombreEmpresa = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBREEMPRESA", moduloNomina,
                            new Date(), false);

            String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL GERENTE", moduloNomina,
                            new Date(), false);

            String nomCargoTesoPago = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL CARGO TESORERO PAGADOR", moduloNomina,
                            new Date(), false);

            String prCargoDelTesoreroPagador = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CARGO DEL TESORERO PAGADOR", moduloNomina,
                            new Date(), false);

            String prNombreDeQuienRevisaNomina = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "NOMBRE DE QUIEN REVISA NOMINA",
                                            moduloNomina,
                                            new Date(), false);

            String prCargoDeQuienRevisaNomina = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "CARGO DE QUIEN REVISA NOMINA",
                                            moduloNomina,
                                            new Date(), false);

            String prNombreDelJefeDeRecursosHumanos = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "NOMBRE DEL JEFE DE RECURSOS HUMANOS",
                                            moduloNomina,
                                            new Date(), false);

            String cargoJefeRh = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS", moduloNomina,
                            new Date(), false);

            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
            parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            nomCargoTesoPago);
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            prCargoDelTesoreroPagador);
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                            prNombreDeQuienRevisaNomina);
            parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                            prCargoDeQuienRevisaNomina);
            parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS",
                            prNombreDelJefeDeRecursosHumanos);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRh);
            HashMap<String, Object> reemplazar = new HashMap<>();

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(moduloNomina), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);

        }
        catch (JRException | IOException
                        | SysmanException | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * @param reporte
     */
    private void generarHojaDatos(String reporte) {

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("compania", compania);
        reemplazar.put("procesoSesion", procesoSesion);
        reemplazar.put("anioSesion", anioSesion);
        reemplazar.put("mesSesion", mesSesion);
        reemplazar.put("periodoSesion", periodoSesion);
        String consulta = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(moduloNomina), reemplazar);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consulta,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_ADICIONALES>
    
    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
    
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        precargarRegistro();
        if (!(activo)) {
            SessionUtil.setSessionVar("mensajeError",
                            idioma.getString("TB_TB2550"));
            SessionUtil.redireccionarMenu();
            return;
        }
        origenDatos = "";
        opcion = "1";

        abrirFormulario();
        // Para condicionar el boton de parametros de retroactivo
        retro = false;
        try {

            double valor306 = ejbNominaDos.getValorConceptoNovedad(compania,
                            Integer.parseInt(anioSesion),
                            Integer.parseInt(periodoSesion),
                            Integer.parseInt(mesSesion), 306).doubleValue();

            if ("5".equals(periodoSesion) || (valor306 > 0)) {
                retro = true;
            }
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgInterrumpidaCons)
                                + ex.getMessage());
            Logger.getLogger(CalculoPrenominaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        deducibles = false;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable nombreProceso
     * 
     * @return nombreProceso
     */
    public String getNombreProceso() {
        return nombreProceso;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable opcion
     * 
     * @return opcion
     */
    public String getOpcion() {
        return opcion;
    }

    /**
     * Asigna la variable opcion
     * 
     * @param opcion
     * Variable a asignar en opcion
     */
    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    /**
     * Retorna la variable empleadoInicial
     * 
     * @return empleadoInicial
     */
    public String getEmpleadoInicial() {
        return empleadoInicial;
    }

    /**
     * Asigna la variable empleadoInicial
     * 
     * @param empleadoInicial
     * Variable a asignar en empleadoInicial
     */
    public void setEmpleadoInicial(String empleadoInicial) {
        this.empleadoInicial = empleadoInicial;
    }

    /**
     * Retorna la variable empleado
     * 
     * @return empleado
     */
    public String getEmpleado() {
        return empleado;
    }

    /**
     * Asigna la variable empleado
     * 
     * @param empleado
     * Variable a asignar en empleado
     */
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    /**
     * Retorna la variable empleadoFinal
     * 
     * @return empleadoFinal
     */
    public String getEmpleadoFinal() {
        return empleadoFinal;
    }

    /**
     * Asigna la variable empleadoFinal
     * 
     * @param empleadoFinal
     * Variable a asignar en empleadoFinal
     */
    public void setEmpleadoFinal(String empleadoFinal) {
        this.empleadoFinal = empleadoFinal;
    }

    /**
     * Retorna la variable estadoActual
     * 
     * @return estadoActual
     */
    public String getEstadoActual() {
        return estadoActual;
    }

    /**
     * Asigna la variable estadoActual
     * 
     * @param estadoActual
     * Variable a asignar en estadoActual
     */
    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    /**
     * Retorna la variable centroCostos
     * 
     * @return centroCostos
     */
    public String getCentroCostos() {
        return centroCostos;
    }

    /**
     * Asigna la variable centroCostos
     * 
     * @param centroCostos
     * Variable a asignar en centroCostos
     */
    public void setCentroCostos(String centroCostos) {
        this.centroCostos = centroCostos;
    }

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
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable definicionFormulario
     * 
     * @return definicionFormulario
     */
    public String getDefinicionFormulario() {
        return definicionFormulario;
    }

    /**
     * Asigna la variable definicionFormulario
     * 
     * @param definicionFormulario
     * Variable a asignar en definicionFormulario
     */
    public void setDefinicionFormulario(String definicionFormulario) {
        this.definicionFormulario = definicionFormulario;
    }

    /**
     * Retorna la variable retro
     * 
     * @return retro
     */
    public boolean isRetro() {
        return retro;
    }

    /**
     * Asigna la variable retro
     * 
     * @param retro
     * Variable a asignar en retro
     */
    public void setRetro(boolean retro) {
        this.retro = retro;
    }

    /**
     * Retorna la variable deducibles
     * 
     * @return deducibles
     */
    public boolean isDeducibles() {
        return deducibles;
    }

    /**
     * Asigna la variable deducibles
     * 
     * @param deducibles
     * Variable a asignar en deducibles
     */
    public void setDeducibles(boolean deducibles) {
        this.deducibles = deducibles;
    }
    
    
    /**
     * Retorna la variable manBeneficios
     * 
     * @return manBeneficios
     */
    public boolean isManBeneficios() {
        return manBeneficios;
    }

    /**
     * Asigna la variable manBeneficios
     * 
     * @param manBeneficios
     * Variable a asignar en manBeneficios
     */
    public void setManBeneficios(boolean manBeneficios) {
        this.manBeneficios = manBeneficios;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCencos
     * 
     * @return listaCencos
     */
    public List<Registro> getListaCencos() {
        return listaCencos;
    }

    /**
     * Asigna la lista listaCencos
     * 
     * @param listaCencos
     * Variable a asignar en listaCencos
     */
    public void setListaCencos(List<Registro> listaCencos) {
        this.listaCencos = listaCencos;
    }

    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en listaTipo
     */
    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoEmpleado
     * 
     * @return listaCodigoEmpleado
     */
    public RegistroDataModelImpl getListaCodigoEmpleado() {
        return listaCodigoEmpleado;
    }

    /**
     * Asigna la lista listaCodigoEmpleado
     * 
     * @param listaCodigoEmpleado
     * Variable a asignar en listaCodigoEmpleado
     */
    public void setListaCodigoEmpleado(
        RegistroDataModelImpl listaCodigoEmpleado) {
        this.listaCodigoEmpleado = listaCodigoEmpleado;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    private void cargarNombreEtiqueta() {
        definicionFormulario = idioma.getString("TB_TB2951")
                        .replace("#$nomproceso#$", nombreProceso)
                        .replace("#$nombremes#$",
                                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                        .valueOf(mesSesion)])
                        .replace("#$aniosesion#$", anioSesion)
                        .replace("#$periodosesion#$", periodoSesion)
                        .replace("#$nomperiodo#$", nombrePeriodo);
    }

}
