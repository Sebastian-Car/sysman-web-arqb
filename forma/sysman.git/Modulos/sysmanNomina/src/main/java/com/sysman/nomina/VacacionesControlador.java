package com.sysman.nomina;


import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.ejb.EjbNominaTresRemote;
import com.sysman.nomina.enums.VacacionesControladorEnum;
import com.sysman.nomina.enums.VacacionesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import javax.faces.event.ActionEvent;
import javax.swing.Spring;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jgomez
 * @version 1, 06/08/2015
 * 
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 30/10/2017
 * 
 * @author ybecerra,documentacion
 * @version 3, 09/04/2018
 *
 */
@ManagedBean
@ViewScoped
public class VacacionesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private String compania;
    // <DECLARAR_ATRIBUTOS>
    private Date fecInicial;
    private Date fechaPago;
    private String idEmpleado;
    private String nombreEmpleado;
    private String cedula;
    private String tituloForm;
    private String mensajeVacLic;
    private String sabadoHabil;
    private String periodoNomina;
    private String procesoNomina;
    private String mesNomina;
    private String anioNomina;
    private String moduloNomina;
    private boolean confirmarHabiles;
    private boolean verMensajeVacLic;
    private boolean verDuplicado;
    private boolean camposBloqueados;
    private boolean pila;
    private boolean verAplicaBonificacion;
    private int rta;
    private int mesInicioDis;
    private int mesFinalDis;
    private int diaIniciaDis;
    private int diaFinalDis;
    private String pagar31;
    private boolean vacaciones;
    private boolean cesantias;
    private int estadoBoton40;
    private Registro listaRegistroPorPeriodo;
  
    
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaFechaPago;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> parametrosEntrada;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbNominaUnoRemote ejbNominaUno;
    @EJB
    private EjbNominaCeroRemote ejbNominaCero;
    @EJB
    private EjbNominaDosRemote ejbNominaDos;
    @EJB
    private EjbNominaTresRemote ejbNominaTres;
    // </DECLARAR_ADICIONALES>

    /**
     * Crea una nueva instancia de VacacionesControlador
     */
    public VacacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        camposBloqueados = false;
        estadoBoton40 = 0;
        try {
            periodoNomina = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                            .toString();
            procesoNomina = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                            .toString();
            mesNomina = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("mesNomina"), "")
                            .toString();
            anioNomina = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("anioNomina"), "")
                            .toString();
            moduloNomina = SessionUtil.getModulo();
            idEmpleado = SysmanFunciones
                            .nvl(parametrosEntrada.get("idEmpleado"), "")
                            .toString();
            nombreEmpleado = SysmanFunciones
                            .nvl(parametrosEntrada.get("nombreEmpleado"), "")
                            .toString();
            vacaciones = (boolean) SysmanFunciones
                            .nvl(parametrosEntrada.get("vacaciones"), false);
            cesantias = (boolean) SysmanFunciones
                            .nvl(parametrosEntrada.get("cesantias"), false);
            cedula = SysmanFunciones
                            .nvl(parametrosEntrada.get(
                                            VacacionesControladorEnum.CEDULA
                                                            .getValue()
                                                            .toLowerCase()),
                                            "")
                            .toString();
            numFormulario = GeneralCodigoFormaEnum.VACACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        cargarListaFechaPago();
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
        enumBase = GenericUrlEnum.VACACIONES;
        buscarLlave();
        asignarOrigenDatos();
        verMensajeVacLic = false;
        
        
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(
                        VacacionesControladorEnum.ID_DE_EMPLEADO.getValue(),
                        idEmpleado);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaFechaPago
     */
    public void cargarListaFechaPago() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        VacacionesControladorUrlEnum.URL3328
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(VacacionesControladorEnum.PROCESONOMINA.getValue(),
                        procesoNomina);
        listaFechaPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        VacacionesControladorEnum.FECHAFINAL.getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control FechaInicio
     * 
     */
    public void cambiarFechaInicio() {
        try {
            Date fechaIni = (Date) registro.getCampos().get(
                            VacacionesControladorEnum.FECHA_INICIO.getValue());
            if (fechaIni != null) {
                nDias();

                int mesInicio = SysmanFunciones.mes(fechaIni);
                int anoInicio = SysmanFunciones.ano(fechaIni);
                int diaInicio = SysmanFunciones.dia(fechaIni);
                Date fechaFin = SysmanFunciones
                                .convertirAFecha((diaInicio
                                    - (diaInicio == 29 && mesInicio == 2 ? 1
                                        : 0))
                                    + "/" + mesInicio
                                    + "/" + (anoInicio + 1));
                fechaFin = SysmanFunciones.sumarRestarDiasFecha(fechaFin, -1);
                registro.getCampos().put(VacacionesControladorEnum.FECHA_FINAL
                                .getValue(), fechaFin);
                if (!validarFechaIncioFin()) {
                    registro.getCampos().put(
                                    GeneralParameterEnum.FECHA_FINAL.getName(),
                                    null);
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
                    return;
                }
                cambiarFechaFinal();
                calcularPeriodo();

                validarVacios(fechaIni, fechaFin);
            }
        }
        catch (Exception ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal
     * 
     */
    public void cambiarFechaFinal() {
        if (!validarFechaIncioFin()) {
            registro.getCampos().put(GeneralParameterEnum.FECHA_FINAL.getName(),
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            return;
        }
        try {
            calcularPeriodo();
            String mensaje;
            Date fechaFin = (Date) registro.getCampos().get(
                            VacacionesControladorEnum.FECHA_FINAL.getValue());
            if (fechaFin != null) {
                mensaje = ejbNominaUno.getValidarFechasVacaciones(compania,
                                fechaFin, Integer.parseInt(idEmpleado));
                JsfUtil.agregarMensajeInformativo(mensaje);
            }
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        /**
         * mvenegas, esta validacion se hace para que
         */
        estadoBoton40 = 0;
        oprimirComando40();
        estadoBoton40 = 1;
    }

    /**
     * Metodo ejecutado al cambiar el control Iniciodisfrute
     * 
     */
    public void cambiarIniciodisfrute() {
    	
    	registro.getCampos().put(VacacionesControladorEnum.DIASHABILES.getValue(), 15);
    	registro.getCampos().put(VacacionesControladorEnum.FINAL_DISFRUTE.getValue(), null);
    	//INI JM MOD CC 1741, ya que estamos, para que no evalue nulos 
    	if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                VacacionesControladorEnum.INICIO_DISFRUTE
                                .getValue())) {
        if (validarFInicio()) {
            registro.getCampos().put(VacacionesControladorEnum.INICIO_DISFRUTE
                            .getValue(), null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3926"));
        }

        else {

            if (validarFechaIniDisfrute()) {
                lanzafechas(true);
                cambiarFinalDisfrute();
            }
            else {
                registro.getCampos()
                                .put(VacacionesControladorEnum.INICIO_DISFRUTE
                                                .getValue(), null);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3759"));
            }

            validarDiasPend(Integer.parseInt(registro.getCampos().get(
            											VacacionesControladorEnum.DIASHABILES
            															.getValue()).toString()));
        }
    
    	}
       
    }

    /**
     * Metodo ejecutado al cambiar el control FinalDisfrute
     * 
     */
    public void cambiarFinalDisfrute() {
        try {
            nDias();
            sabadoHabil = getParametro("CONTAR SABADOS COMO DIA HABIL", true);
            sabadoHabil = sabadoHabil.toUpperCase();
            boolean vaSabado = "SI".equals(sabadoHabil) ? true : false;
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            VacacionesControladorEnum.INICIO_DISFRUTE
                                            .getValue())
                && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                VacacionesControladorEnum.FINAL_DISFRUTE
                                                .getValue())) {
                int diasHabiles = ejbSysmanUtil.retornarDiasHabilesEntreFechas(
                                compania,
                                (Date) registro.getCampos().get(
                                                VacacionesControladorEnum.INICIO_DISFRUTE
                                                                .getValue()),
                                (Date) registro.getCampos().get(
                                                VacacionesControladorEnum.FINAL_DISFRUTE
                                                                .getValue()),
                                vaSabado);

                registro.getCampos().put(VacacionesControladorEnum.DIASHABILES
                                .getValue(), diasHabiles);
                int dias = Integer.parseInt(
                                ejbSysmanUtil.calcularDiferenciaEntreFechasVac(
                                                (Date) registro.getCampos().get(
                                                                VacacionesControladorEnum.INICIO_DISFRUTE
                                                                                .getValue()),
                                                (Date) registro.getCampos().get(
                                                                VacacionesControladorEnum.FINAL_DISFRUTE
                                                                                .getValue()),
                                                3, 0));

                cargarMesDiaDisfrute();
                //comentado por JM 18/06/2025 CC 1832 en el paquete ya se tome en cuenta el 31
                //asi que se comenta aqui porque sino lo suma otra vez 
                /*dias = meses(mesInicioDis, mesFinalDis, diaFinalDis, pagar31,
                                diaIniciaDis, dias); */
                //JM CC 3444
                if ("SI".equals(getParametro("LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES", true)) && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        VacacionesControladorEnum.DIASHABILES
                        .getValue())){
                	Object habiles = registro.getCampos().get(VacacionesControladorEnum.DIASHABILES.getValue());
                    if (habiles != null && !habiles.toString().isEmpty()) {
		                    dias = (int) registro.getCampos().get(
		                              VacacionesControladorEnum.DIASHABILES
		                                              .getValue());
                    }
                  }
                
                registro.getCampos().put(
                                VacacionesControladorEnum.DIAS.getValue(),
                                dias);

            }
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control FechaActo
     * 
     */
    public void cambiarFechaActo() {

        if (!validarFechaIniActo()) {
            registro.getCampos().put(
                            VacacionesControladorEnum.FECHA_ACTO.getValue(),
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3757"));
        }

    }

    /**
     * Metodo ejecutado al cambiar el control Dias
     * 
     */
    public void cambiarDias() {
        try {
            int diasDinero = Integer.parseInt(SysmanFunciones
                            .nvl(registro.getCampos().get(
                                            VacacionesControladorEnum.DIASDINERO
                                                            .getValue()),
                                            "")
                            .toString());
            int dias = Integer.parseInt(ejbSysmanUtil
                            .calcularDiferenciaEntreFechasVac(fecInicial,
                                            (Date) registro.getCampos().get(
                                                            VacacionesControladorEnum.FINAL_DISFRUTE
                                                                            .getValue()),
                                            3, 0));

            if (diasDinero > dias) {
                JsfUtil.agregarMensajeInformativo(idioma
                                .getString(VacacionesControladorEnum.TB_TB2666
                                                .getValue()));
            }
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Diashabiles
     * 
     */
    public void cambiarDiashabiles() {
    	 //INI JM MOD CC 1741, ya que estamos, para que no evalue nulos
    	if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                VacacionesControladorEnum.DIASHABILES
                                .getValue())) {
        lanzafechas(false);
        validarDiasPend(Integer.parseInt(registro.getCampos().get(
        											VacacionesControladorEnum.DIASHABILES
        															.getValue()).toString()));

     // mod JM CC 3444
        if ("SI".equals(getParametro("LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES", true))) {
            Object valorHabiles = registro.getCampos().get(VacacionesControladorEnum.DIASHABILES.getValue());
            
            if (valorHabiles != null && !valorHabiles.toString().isEmpty()) {
                try {
                    Integer diasNumerico = Integer.parseInt(valorHabiles.toString());
                    registro.getCampos().put(VacacionesControladorEnum.DIAS.getValue(), diasNumerico);
                } catch (NumberFormatException e) {
                    registro.getCampos().put(VacacionesControladorEnum.DIAS.getValue(), 0);
                }
            }
        }
        
    	}
    }

    /**
     * Metodo ejecutado al cambiar el control DiasPendientesdeDisfrute
     * 
     */
    public void cambiarDiasPendientesdeDisfrute() {
        cambiarFechaInicio();
        validarDiasPend(Integer.parseInt(registro.getCampos().get(
        											VacacionesControladorEnum.DIASPENDIENTESDEDISFRUTE
        															.getValue()).toString()));
    }

    /**
     * Metodo ejecutado al cambiar el control DiasDinero
     * 
     */
    public void cambiarDiasDinero() {
        try {
            int DiasSinDinero = 0;
            int diasDinero = Integer
                            .parseInt(registro.getCampos().get(
                                            VacacionesControladorEnum.DIASDINERO
                                                            .getValue())
                                            .toString());
            int dias = Integer.parseInt(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(VacacionesControladorEnum.DIAS
                                                            .getValue()),
                                            "")
                            .toString());
            if (diasDinero > dias) {
                registro.getCampos().put(
                                VacacionesControladorEnum.DIASDINERO.getValue(),
                                registroIni != null ? registroIni.get(
                                                VacacionesControladorEnum.DIASDINERO
                                                                .getValue())
                                    : "0");
                diasDinero = Integer.parseInt(SysmanFunciones
                                .nvl(registro.getCampos().get(
                                                VacacionesControladorEnum.DIASDINERO
                                                                .getValue()),
                                                "0")
                                .toString());
                
                nDias();
                JsfUtil.agregarMensajeInformativo(idioma
                                .getString(VacacionesControladorEnum.TB_TB2666
                                                .getValue()));
            }
            else {
            	
                dias = dias - diasDinero;
                if ("SI".equals(getParametro(
                                "CALCULAR DIAS CALENDARIO VACACIONES EN DISFRUTE DINERO ENTIDAD PRIVADA",
                                true))) {
                    // (MZANGUNA:10/08/2019):INICIO
                    int diasHabiles = Integer.parseInt(SysmanFunciones
                                    .nvl(registro.getCampos().get(
                                                    VacacionesControladorEnum.DIASHABILES
                                                                    .getValue()),
                                                    "0")
                                    .toString())
                        - diasDinero;

                    registro.getCampos()
                                    .put(VacacionesControladorEnum.DIASHABILES
                                                    .getValue(), diasHabiles);

                    lanzafechas(false);

                    Date fechaIniDisfrute = (Date) registro.getCampos().get(
                                    VacacionesControladorEnum.INICIO_DISFRUTE
                                                    .getValue());

                    Date fechaFinDisfrute = (Date) registro.getCampos().get(
                                    VacacionesControladorEnum.FINAL_DISFRUTE
                                                    .getValue());

                    try {
                        dias = Integer.parseInt(
                                        ejbSysmanUtil.calcularDiferenciaEntreFechasVac(
                                                        fechaIniDisfrute,
                                                        fechaFinDisfrute, 3,
                                                        0));
                        DiasSinDinero = dias;
                    }
                    catch (SystemException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }

                    // (MZANGUNA:10/08/2019):FIN
                }

            }
            registro.getCampos().put(VacacionesControladorEnum.DIAS.getValue(),
                            dias);
            if (diasDinero > 0) {
                registro.getCampos().put(
                                VacacionesControladorEnum.ENDINERO.getValue(),
                                true);
            }
            else {
                registro.getCampos().put(
                                VacacionesControladorEnum.ENDINERO.getValue(),
                                false);
            }

            iniciodisfrute(false);

            if (DiasSinDinero != 0) {
                registro.getCampos().put(
                                VacacionesControladorEnum.DIAS.getValue(),
                                DiasSinDinero);
            }
        }
        catch (NumberFormatException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * confirmaHabiles en la vista
     *
     */
    public void aceptarconfirmaHabiles() {
        sabadoHabil = "SI";
        iniciodisfrute(true);
        confirmarHabiles = false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * confirmaHabiles en la vista
     *
     */
    public void cancelarconfirmaHabiles() {
        sabadoHabil = "NO";
        iniciodisfrute(true);
        confirmarHabiles = false;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFechaPago
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFechaPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fechaPago = (Date) registroAux.getCampos()
                        .get(VacacionesControladorEnum.FECHAFINAL.getValue());
        registro.getCampos().put(VacacionesControladorEnum.FECHAPAGO.getValue(),
                        registroAux.getCampos().get(
                                        VacacionesControladorEnum.FECHAFINAL
                                                        .getValue()));
        registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.MES.getName()));
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.PERIODO
                                        .getName()));
        if (registroAux.getCampos().get(VacacionesControladorEnum.FECHAFINAL
                        .getValue()) == null) {
            try {
                fechaPago = ejbNominaCero.getFechaPeriodoIniFin(compania,
                                Integer.parseInt(procesoNomina),
                                Integer.parseInt(anioNomina),
                                Integer.parseInt(mesNomina),
                                Integer.parseInt(periodoNomina),
                                false, true);
                registro.getCampos().put(
                                VacacionesControladorEnum.FECHAPAGO.getValue(),
                                fechaPago);
            }
            catch (NumberFormatException | SystemException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando40 en la vista
     *
     */
    public void oprimirComando40() {
        try {
            String mensajeVac = ejbNominaUno.getAvisoVacaciones(compania,
                            Integer.parseInt(idEmpleado),
                            (Date) registro.getCampos().get(
                                            VacacionesControladorEnum.FECHA_INICIO
                                                            .getValue()));

            String mensajeLic = ejbNominaUno.getAvisoLicencias(compania,
                            Integer.parseInt(idEmpleado),
                            (Date) registro.getCampos().get(
                                            VacacionesControladorEnum.FECHA_INICIO
                                                            .getValue()),
                            (Date) registro.getCampos().get(
                                            VacacionesControladorEnum.FECHA_FINAL
                                                            .getValue()));

            if (SysmanFunciones.validarVariableVacio(mensajeVac)) {
                mensajeVac = "";
            }
            if (SysmanFunciones.validarVariableVacio(mensajeLic)) {
                mensajeLic = "";
            }
            mensajeVac += mensajeLic;

            mensajeVacLic = mensajeVac;
            verMensajeVacLic = false;
            if (!SysmanFunciones.validarVariableVacio(mensajeVacLic)) {
                mensajeVacLic = mensajeVacLic.replace(String.valueOf((char) 10),
                                "</br>");                
                verMensajeVacLic = true;
            }

            int diasLic = ejbNominaUno.getDiasLicCorrerVacaciones(compania,
                            Integer.parseInt(idEmpleado),
                            (Date) registro.getCampos().get(
                                            VacacionesControladorEnum.FECHA_INICIO
                                                            .getValue()),
                            (Date) registro.getCampos().get(
                                            VacacionesControladorEnum.FECHA_FINAL
                                                            .getValue()));

            /**
             * Si estadoBoton40 == 0, quiere decir que se accedio por
             * primera vez al formulario para insertar un registro
             * nuevo
             */
            if (diasLic != 0 && estadoBoton40 == 0) {
                Date nuevoFinal = (Date) registro.getCampos()
                                .get(VacacionesControladorEnum.FECHA_FINAL
                                                .getValue());
                nuevoFinal = SysmanFunciones.sumarRestarDiasFecha(nuevoFinal,  1); //JM MOD CC 2372 ya viene con un dia menos 
                
                nuevoFinal = SysmanFunciones.sumarRestarDiasFecha(nuevoFinal,  diasLic);
                
                registro.getCampos().put(VacacionesControladorEnum.FECHA_FINAL
                                .getValue(), nuevoFinal);
            }

        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimirduplicarnovedades(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
    	String[] campos = new String[4];
        campos[0] = "proceso";
        campos[1] = "anio";
        campos[2] = "mes";
        campos[3] = "periodo";

        Object[] valores = new Object[4];
        valores[0] = procesoNomina;
        valores[1] = anioNomina;
        valores[2] = mesNomina;
        valores[3] = periodoNomina;


        SessionUtil.cargarModalDatosFlashCerrar(
            Integer.toString(
                2455),
            moduloNomina, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * @param nombre
     * @param indMayus
     * @return
     */
    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            moduloNomina, new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * @return
     */
    private boolean validarFechaIniDisfrute() {
        String parPermiteFechaAnt = getParametro(
                        "PERMITE FECHA DISFRUTE VACACIONES MENOR A INICIO PERIODO",
                        true);
        
        if ("SI".equals(parPermiteFechaAnt)) {
            return true;
        }
        else {
            Date fechaInicio = (Date) registro.getCampos()
                            .get(VacacionesControladorEnum.FECHA_INICIO
                                            .getValue());
            Date fechaDisfrute = (Date) registro.getCampos().get(
                            VacacionesControladorEnum.INICIO_DISFRUTE
                                            .getValue());
            Date fechaFinal = (Date) registro.getCampos()
                            .get(VacacionesControladorEnum.FECHA_FINAL
                                            .getValue());
            return (fechaDisfrute.compareTo(fechaInicio) >= 0
                && fechaFinal.compareTo(fechaDisfrute) >= 0)
                || fechaDisfrute.compareTo(fechaFinal) >= 0;
        }
        
    }

    /**
     * 
     */
    private void nDias() {
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        VacacionesControladorEnum.INICIO_DISFRUTE.getValue())) {
            try {

                if (registro.getCampos()
                                .get(VacacionesControladorEnum.DIASDINERO
                                                .getValue()) == null) {
                    registro.getCampos()
                                    .put(VacacionesControladorEnum.DIASDINERO
                                                    .getValue(), 0);
                }
                int diasDinero = Integer
                                .parseInt(registro.getCampos().get(
                                                VacacionesControladorEnum.DIASDINERO
                                                                .getValue())
                                                .toString());

                cargarMesDiaDisfrute();
                fecInicial = (Date) registro.getCampos()
                                .get(VacacionesControladorEnum.INICIO_DISFRUTE
                                                .getValue());

                if ("NO".equals(pagar31) && diaIniciaDis == 31) {
                    fecInicial = SysmanFunciones
                                    .sumarRestarDiasFecha(fecInicial, 1);
                }
                
                //INI JM MOD CC 1741, ya que estamos, para que no envie nulos al ejb 
                int dias =0;
                if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        VacacionesControladorEnum.INICIO_DISFRUTE
                                        .getValue())
		            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
		                            VacacionesControladorEnum.FINAL_DISFRUTE
		                                            .getValue())) {
                //CC_3849(29/04/2026 JCROJAS): Se cambia funcion calcularDiferenciaEnDias por calcularDiferenciaEntreFechasVac para que calcule los dias bien
                dias = Integer.parseInt(ejbSysmanUtil.calcularDiferenciaEntreFechasVac(
                		fecInicial == null? (Date) registro.getCampos().get(VacacionesControladorEnum.INICIO_DISFRUTE.getValue()): fecInicial,
                        (Date) registro.getCampos().get(VacacionesControladorEnum.FINAL_DISFRUTE.getValue()),3,0));
                //comentado por JM 18/06/2025 CC 1832 en el paquete ya se tome en cuenta el 31
                //asi que se comenta aqui porque sino lo suma otra vez 
                /*dias = meses(mesInicioDis, mesFinalDis, diaFinalDis, pagar31,
                                diaIniciaDis, dias); */
                }
               //FIN JM MOD CC 1741, ya que estamos, para que no envie nulos al ejb 
                
                if (diasDinero == dias) {
                    dias = 0;
                }
                else {
                    dias -= diasDinero;
                }
                if (dias < 0) {
                    dias = 0;
                }
                //mod JM CC 3444 habiles 
                if ("SI".equals(getParametro("LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES", true))) {
                    Object habiles = registro.getCampos().get(VacacionesControladorEnum.DIASHABILES.getValue());
                    if (habiles != null && !habiles.toString().isEmpty()) {
                        try {
                            
                            dias = Integer.parseInt(habiles.toString());
                            if (diasDinero == dias) {
                                dias = 0;
                            }
                            else {
                                dias -= diasDinero;
                            }
                            if (dias < 0) {
                                dias = 0;
                            }
                            
                        } catch (NumberFormatException e) {
                            registro.getCampos().put(VacacionesControladorEnum.DIAS.getValue(), 0);
                        }
                    }
                }              	
                
                registro.getCampos().put(
                            VacacionesControladorEnum.DIAS.getValue(),
                            dias);
                
            }
            catch (NullPointerException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void validarCantVacaciones() { 
    	
    	try {

    		Date fechaIni = (Date) registro.getCampos().get(VacacionesControladorEnum.FECHA_INICIO.getValue()); 
    		Date fechaFin = (Date) registro.getCampos().get(VacacionesControladorEnum.FECHA_FINAL.getValue());
    		int empleado = Integer.parseInt(registro.getCampos().get(VacacionesControladorEnum.ID_DE_EMPLEADO.getValue()).toString());
    		int periodos  = 0;
    		int diasMax = 0;
    		Object parametro = null;
    		int diasTotales = 0;


    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);			
    		param.put(VacacionesControladorEnum.FECHA_INI.getValue(), SysmanFunciones.ano(fechaIni));
    		param.put(VacacionesControladorEnum.FECHA_FIN.getValue(),SysmanFunciones.ano(fechaFin));
    		param.put(VacacionesControladorEnum.ID_EMPLEADO.getValue(),empleado);

    		listaRegistroPorPeriodo = RegistroConverter.toRegistro(
    				requestManager.get(UrlServiceUtil.getInstance()
    						.getUrlServiceByUrlByEnumID(
    								VacacionesControladorUrlEnum.URL625009
    								.getValue())
    						.getUrl(), param));

    		if(listaRegistroPorPeriodo != null) {

    			diasTotales = Integer.parseInt(listaRegistroPorPeriodo.getCampos().get("TOTAL_DIAS").toString());
    			periodos = Integer.parseInt(listaRegistroPorPeriodo.getCampos().get("NUMPERIODOS").toString());
    			fechaIni =  (Date) listaRegistroPorPeriodo.getCampos().get("FECHA_INICIO"); 
    			fechaFin = (Date) listaRegistroPorPeriodo.getCampos().get("FECHA_FINAL");
    			empleado = Integer.parseInt(listaRegistroPorPeriodo.getCampos().get("ID_DE_EMPLEADO").toString());

    		}

    		diasMax = periodos * 15;

    		parametro = (ejbSysmanUtil.consultarParametro(compania,
    				"ENTIDAD PUBLICA O PRIVADA", moduloNomina,
    				new Date(), false)).toUpperCase();
    		if (diasTotales > diasMax || parametro == "PRIVADA") {

    			String mensaje = "";

    			mensaje = (idioma.getString("TB_TB4417")    		
    					.replace("#$fechaIni#$",SysmanFunciones.convertirAFechaCadena(fechaIni,"dd/MM/YYYY"))
    					.replace("#$fechaFin#$", SysmanFunciones.convertirAFechaCadena(fechaFin,"dd/MM/YYYY"))		
    					.replace("#$diasTotales#$", String.valueOf(diasTotales))
    					.replace("#$diasMaximo#$", String.valueOf(diasMax))
    					.replace("#$idEmpleado#$", String.valueOf(empleado)));

    			JsfUtil.agregarMensajeAlerta(mensaje);

    		}
    	} catch (ParseException | SystemException  e) {
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }



	private void cargarMesDiaDisfrute() {
        pagar31 = getParametro(
                        VacacionesControladorEnum.PAGAR_VACACIONES.getValue(),
                        true);
        pagar31 = pagar31.toUpperCase();
        fecInicial = (Date) registro.getCampos().get(
                        VacacionesControladorEnum.INICIO_DISFRUTE.getValue());
        mesInicioDis = SysmanFunciones.mes(fecInicial);
        mesFinalDis = SysmanFunciones
                        .mes((Date) registro.getCampos().get(
                                        VacacionesControladorEnum.FINAL_DISFRUTE
                                                        .getValue()));

        diaIniciaDis = SysmanFunciones.dia(fecInicial);

        diaFinalDis = SysmanFunciones
                        .dia((Date) registro.getCampos().get(
                                        VacacionesControladorEnum.FINAL_DISFRUTE
                                                        .getValue()));

    }

    /**
     * @param mesInicioDis
     * @param mesFinalDis
     * @param diaFinalDis
     * @param pagar31
     * @return
     */
    private boolean validarMesIniDInDiasFin(int mesInicioDis, int mesFinalDis,
        int diaFinalDis, String pagar31) {
        return (mesInicioDis != mesFinalDis || diaFinalDis == 31)
            && ("SI".equals(pagar31));

    }

    /**
     * @param mesInicioDis
     * @return
     */
    private boolean evaluarMesDis(int mesInicioDis) {
        return evaluarMesInicioDIs(mesInicioDis) || mesInicioDis == 8
            || mesInicioDis == 10 || mesInicioDis == 12;
    }

    /**
     * @param mesInicioDis
     * @return
     */
    private boolean evaluarMesInicioDIs(int mesInicioDis) {
        return mesInicioDis == 1 || mesInicioDis == 3 || mesInicioDis == 5
            || mesInicioDis == 7;
    }

    /**
     * @param mesInicioDis
     * @param mesFinalDis
     * @param diaFinalDis
     * @param pagar31
     * @param diaIniciaDis
     * @param dias
     * @return
     */
    public int meses(int mesInicioDis, int mesFinalDis, int diaFinalDis,
        String pagar31, int diaIniciaDis, int dias) {
        int diasAux = dias;

        if (validarMesIniDInDiasFin(mesInicioDis, mesFinalDis, diaFinalDis,
                        pagar31)
            && evaluarMesDis(mesInicioDis)
            && diaIniciaDis != 31) {
            diasAux += 1;
        }
        return diasAux;
    }

    /**
     * 
     */
    public void calcularPeriodo() {
        if (registro.getCampos()
                        .get(VacacionesControladorEnum.FECHA_FINAL
                                        .getValue()) != null
            && registro.getCampos().get(VacacionesControladorEnum.FECHA_INICIO
                            .getValue()) != null) {
            try {
                int numPer = Integer.parseInt(
                                ejbSysmanUtil.calcularDiferenciaEntreFechasVac(
                                                (Date) registro.getCampos().get(
                                                                VacacionesControladorEnum.FECHA_INICIO
                                                                                .getValue()),
                                                (Date) registro.getCampos().get(
                                                                VacacionesControladorEnum.FECHA_FINAL
                                                                                .getValue()),
                                                3, 0));

                numPer = (int) SysmanFunciones.redondear((double) numPer / 360,
                                0);
                registro.getCampos().put(VacacionesControladorEnum.NUMPERIODOS
                                .getValue(), numPer);
            }
            catch (NumberFormatException | SystemException ex) {
                JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                                idioma.getString(
                                                VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                                .getValue()),
                                ex.getMessage()));
                Logger.getLogger(VacacionesControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * 
     */
    public void cerrarFormulario() {
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta("/novedades.sysman");
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
    }

    /**
     * meotod que valida la fecha inicial que no sea mayor que la
     * fecha final
     * 
     * @return
     */
    private boolean validarFechaIncioFin() {
        Date fechaInicial = (Date) registro.getCampos()
                        .get(VacacionesControladorEnum.FECHA_INICIO.getValue());
        Date fechaFinal = (Date) registro.getCampos()
                        .get(VacacionesControladorEnum.FECHA_FINAL.getValue());
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        VacacionesControladorEnum.FECHA_INICIO.getValue())
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            VacacionesControladorEnum.FECHA_FINAL.getValue())) {
            return SysmanFunciones.comparaFechas(fechaInicial, fechaFinal);
        }
        return true;
    }

    /**
     * @param fechaIni
     * @param fechaFin
     */
    public void validarVacios(Date fechaIni, Date fechaFin) {
        String mensaje;

        try {
            mensaje = ejbNominaUno.getValidarFechasVacaciones(compania,
                            fechaIni, Integer.parseInt(idEmpleado));

            if ((fechaFin != null)
                && SysmanFunciones.validarVariableVacio(mensaje)) {
                mensaje = ejbNominaUno.getValidarFechasVacaciones(compania,
                                fechaFin, Integer.parseInt(idEmpleado));
            }
            if (SysmanFunciones.validarVariableVacio(mensaje)) {
                JsfUtil.agregarMensajeInformativo(mensaje);
            }
            double diasPendin = ejbNominaUno
                            .getDiaPendienteVacacionesHistorico(compania,
                                            Integer.parseInt(procesoNomina),
                                            Integer.parseInt(idEmpleado))
                            .doubleValue();
            if (diasPendin > 0) {
                mensaje = idioma.getString("TB_TB2665").replace(
                                "#$diaspendin#$", String.valueOf(diasPendin));
                JsfUtil.agregarMensajeInformativo(mensaje);
            }
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        validarCantVacaciones();
    }

    /**
     * @return
     */
    private boolean validarFechaIniActo() {
        Date fechaIniPeriodo = (Date) registro.getCampos()
                        .get(VacacionesControladorEnum.FECHA_INICIO.getValue());
        Date fechaActo = (Date) registro.getCampos()
                        .get(VacacionesControladorEnum.FECHA_ACTO.getValue());

        return fechaActo.compareTo(fechaIniPeriodo) >= 0;
    }

    /**
     * @return
     */
    private boolean validarFInicio() {
        String parPermiteFechaAnt = getParametro(
                        "PERMITE FECHA DISFRUTE VACACIONES MENOR A INICIO PERIODO",
                        true);
        boolean validaDisfrute;
        
        if ("SI".equals(parPermiteFechaAnt)) {
            return false;
        }
        else {
        	//INI JM MOD CC 1741, ya que estamos, para que evalue los nulos antes de hacer comparaciones 
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                    VacacionesControladorEnum.INICIO_DISFRUTE.getValue())
		        && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
		                        VacacionesControladorEnum.FECHA_FINAL.getValue())) {
       
   
            Date fechaFinal = (Date) registro.getCampos()
                            .get(VacacionesControladorEnum.FECHA_FINAL
                                            .getValue());
            Date fechaIniDisfrute = (Date) registro.getCampos().get(
                            VacacionesControladorEnum.INICIO_DISFRUTE
                                            .getValue());
            
            String parEntidadPublica = getParametro(
                    "ENTIDAD PUBLICA O PRIVADA",
                    true);
        	
               validaDisfrute = fechaIniDisfrute.compareTo(fechaFinal) <= 0;
        	if("PUBLICA".equals(parEntidadPublica) && validaDisfrute) {
        		JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB4392"));
        		return false;
        	}
            
            }else { 
            	validaDisfrute = true;
            }
        }
          //FIN JM MOD CC 1741, ya que estamos, para que evalue los nulos antes de hacer comparaciones 
            return validaDisfrute;
       
    }
    
    /**
     * mensaje
     */
    private void validarDiasPend(int diasHP) 
    {
    	try 
    	{
    		double diasPendien = ejbNominaUno
    						.getDiaPendienteVacacionesHistorico(compania,
    										Integer.parseInt(procesoNomina),
    										Integer.parseInt(idEmpleado))
			         		.doubleValue();
    		
    		if(diasPendien > 0 && diasHP > diasPendien)
			{
    			String mensaje = idioma.getString("TB_TB4418");
    			
    			mensaje = mensaje.replace("#$diasHabiles#$", String.valueOf(diasHP));
    		    mensaje = mensaje.replace("#$diaspendin#$", String.valueOf(diasPendien));
    		    
    			JsfUtil.agregarMensajeInformativo(mensaje);
			}	
    	} 
    	catch (NumberFormatException | SystemException e) 
    	{
    		e.printStackTrace();
    	}
    }

    /**
     * @param conMensaje
     */
    private void lanzafechas(boolean conMensaje) {

        sabadoHabil = getParametro("CONTAR SABADOS COMO DIA HABIL", true);
        sabadoHabil = sabadoHabil.toUpperCase();
        if (SysmanFunciones.validarVariableVacio(sabadoHabil)) {
            return;
        }
        confirmarHabiles = false;
        if (!("SI".equals(sabadoHabil)) && !("NO".equals(sabadoHabil))
            && !(VacacionesControladorEnum.PREGUNTAR.getValue()
                            .equals(sabadoHabil))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2667"));
            return;
        }
        if (VacacionesControladorEnum.PREGUNTAR.getValue()
                        .equals(sabadoHabil)) {
            sabadoHabil = "NO";
            confirmarHabiles = true;
        }
        else {
            iniciodisfrute(conMensaje);
        }
    }

    /**
     * @param conMensaje
     */
    private void iniciodisfrute(boolean conMensaje) {

        registro.getCampos().put(VacacionesControladorEnum.FECHAPAGO.getValue(),
                        fechaPago);
        int diasPen = Integer.parseInt(
                        registro.getCampos().get(
                                        VacacionesControladorEnum.DIASPENDIENTESDEDISFRUTE
                                                        .getValue())
                                        .toString());
        try {
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            VacacionesControladorEnum.INICIO_DISFRUTE
                                            .getValue())
                && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                VacacionesControladorEnum.DIASHABILES
                                                .getValue())) {
                Date fechaFin;

                fechaFin = ejbNominaUno.getFechaFinalVacaciones(compania,
                                (Date) registro.getCampos().get(
                                                VacacionesControladorEnum.INICIO_DISFRUTE
                                                                .getValue()),
                                Integer.parseInt(
                                                registro.getCampos().get(
                                                                VacacionesControladorEnum.DIASHABILES
                                                                                .getValue())
                                                                .toString()),
                                sabadoHabil, Integer.parseInt(moduloNomina));
                registro.getCampos()
                                .put(VacacionesControladorEnum.FINAL_DISFRUTE
                                                .getValue(), fechaFin);

            }

            if (conMensaje) {
                asignarValores(diasPen);
            }

            nDias();
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @param diasPen
     */
    public void asignarValores(int diasPen) {
        try {
            if (diasPen > 0) {
                Date ultimInter;
                ultimInter = ejbNominaUno.getUltimoInterrupcion(compania,
                                Integer.parseInt(procesoNomina),
                                Integer.parseInt(anioNomina),
                                Integer.parseInt(mesNomina),
                                Integer.parseInt(periodoNomina),
                                Integer.parseInt(idEmpleado), 3);

                if (((Date) registro.getCampos()
                                .get(VacacionesControladorEnum.INICIO_DISFRUTE
                                                .getValue()))
                                                                .compareTo(ultimInter) < 0) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2668"));
                }
            }
            String alertaLic = getParametro(
                            "ALERTA DE LICENCIAS NO REMUNERADA EN VACACIONES",
                            true);

            if ("SI".equals(alertaLic)
                && registro.getCampos()
                                .get(VacacionesControladorEnum.FECHA_FINAL
                                                .getValue()) != null
                && registro.getCampos()
                                .get(VacacionesControladorEnum.FECHA_INICIO
                                                .getValue()) != null) {
                Date fecInicio = (Date) registro.getCampos()
                                .get(VacacionesControladorEnum.FECHA_INICIO
                                                .getValue());
                Date fecFinal = (Date) registro.getCampos()
                                .get(VacacionesControladorEnum.FECHA_FINAL
                                                .getValue());
                double cna356;
                cna356 = ejbNominaUno.getAcumConceptoValor(compania, 356,
                                SysmanFunciones.ano(fecInicio),
                                SysmanFunciones.mes(fecInicio), 1,
                                SysmanFunciones.ano(fecFinal),
                                SysmanFunciones.mes(fecFinal),
                                99, Integer.parseInt(idEmpleado)).doubleValue();

                double cna357 = ejbNominaUno.getAcumConceptoValor(compania, 357,
                                SysmanFunciones.ano(fecInicio),
                                SysmanFunciones.mes(fecInicio), 1,
                                SysmanFunciones.ano(fecFinal),
                                SysmanFunciones.mes(fecFinal),
                                99, Integer.parseInt(idEmpleado)).doubleValue();
                double cna359 = ejbNominaUno.getAcumConceptoValor(compania, 359,
                                SysmanFunciones.ano(fecInicio),
                                SysmanFunciones.mes(fecInicio), 1,
                                SysmanFunciones.ano(fecFinal),
                                SysmanFunciones.mes(fecFinal),
                                99, Integer.parseInt(idEmpleado)).doubleValue();

                if (Double.doubleToLongBits(cna356 + cna357 + cna359) != Double
                                .doubleToLongBits(0)) {
                    JsfUtil.agregarMensajeInformativo(idioma
                                    .getString("TB_TB2669").replace("#$dias#$",
                                                    String.valueOf(cna356
                                                        + cna357 + cna359)));
                }

            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     */
    public void aceptaravisoVacLic() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * @param mesActual
     * @param anoActual
     */
    public void validar(String mesActual, String anoActual) {
        if (registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()) != anoActual) {
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            anoActual);
        }
        if (registro.getCampos()
                        .get(GeneralParameterEnum.MES.getName()) != mesActual) {
            registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                            mesActual);
        }
        if (registro.getCampos().get(VacacionesControladorEnum.ID_DE_PROCESO
                        .getValue()) != procesoNomina) {
            registro.getCampos().put(
                            VacacionesControladorEnum.ID_DE_PROCESO.getValue(),
                            procesoNomina);
        }
    }

    /**
     * @param unOpcion
     */
    private void diferirVac(String unOpcion) {
        boolean indBon = (Boolean) SysmanFunciones.nvl(registro.getCampos().get("APLICA_BONIFICACION"), false);
        insertarDespues();
        try {
            setRta(ejbNominaDos.getDiferirVac(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(idEmpleado),
                            Integer.parseInt(
                                            registro.getCampos().get(
                                                            VacacionesControladorEnum.ID_DE_CONCEPTO
                                                                            .getValue())
                                                            .toString()),
                            Integer.parseInt(
                                            registro.getCampos().get(
                                                            VacacionesControladorEnum.DIASHABILES
                                                                            .getValue())
                                                            .toString()),
                            Integer.parseInt(
                                            registro.getCampos().get(
                                                            VacacionesControladorEnum.DIASDINERO
                                                                            .getValue())
                                                            .toString()),
                            Integer.parseInt(registro.getCampos()
                                            .get(VacacionesControladorEnum.DIASPENDIENTESDEDISFRUTE
                                                            .getValue())
                                            .toString()),
                            Integer.parseInt(registro.getCampos()
                                            .get(VacacionesControladorEnum.DIAS
                                                            .getValue())
                                            .toString()),
                            (Date) registro.getCampos().get(
                                            VacacionesControladorEnum.INICIO_DISFRUTE
                                                            .getValue()),
                            fechaPago,
                            Integer.parseInt(
                                            registro.getCampos().get(
                                                            VacacionesControladorEnum.NUMPERIODOS
                                                                            .getValue())
                                                            .toString()),
                            indBon,
                            unOpcion, SessionUtil.getUser().getCodigo()) ? 1
                                : 0);

        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     */
    public void cambiarquitarPila() {
        if (pila) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3914"));
            
        }
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {

        String verDupl;
        

        tituloForm = SysmanFunciones.concatenar(idioma.getString("TB_TB4055"),
                        " ", nombreEmpleado);
        
        if ( SysmanFunciones.nvl(getParametro("CODIGO CONCEPTO DIAS BONIFICACION VACACIONES", true),"NO") != "NO" 
        		&& Integer.parseInt(SysmanFunciones.nvl(getParametro("DIAS BONIFICACION VACACIONES", true),"0").toString()) > 0) {
        	verAplicaBonificacion = true;
        }
        else {
        	verAplicaBonificacion = false;
        }
        	
        try {
            verDupl = ejbNominaUno.getParametroNomina(compania, 31);
            verDuplicado = SysmanFunciones.nvl(getParametro("DUPLICAR VACACIONES COLECTIVAS", false),"NO").equals("SI");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        precargarRegistro();
        if ("i".equals(accion)) {
            camposBloqueados = false;

            try {
                Date fechaIni = ejbNominaUno.getUltimaFechaVacaciones(compania,
                                Integer.parseInt(idEmpleado), true);
                Date fechaFin = ejbNominaUno.getUltimaFechaVacaciones(compania,
                                Integer.parseInt(idEmpleado), false);
                fechaPago = ejbNominaCero.getFechaPeriodoIniFin(compania,
                                Integer.parseInt(procesoNomina),
                                Integer.parseInt(anioNomina),
                                Integer.parseInt(mesNomina),
                                Integer.parseInt(periodoNomina),
                                false, true);
                registro.getCampos().put(VacacionesControladorEnum.FECHA_INICIO
                                .getValue(), fechaIni);
                registro.getCampos().put(VacacionesControladorEnum.FECHA_FINAL
                                .getValue(), fechaFin);
                registro.getCampos().put(VacacionesControladorEnum.DIASHABILES
                                .getValue(), "15");
                registro.getCampos().put(
                                VacacionesControladorEnum.DIASDINERO.getValue(),
                                "0");
                registro.getCampos().put("DIAS", "0");
                registro.getCampos().put(
                                VacacionesControladorEnum.FECHAPAGO.getValue(),
                                fechaPago);

                registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                                SysmanFunciones.convertirAFechaCadena(fechaPago,
                                                "MM"));
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                SysmanFunciones.convertirAFechaCadena(fechaPago,
                                                "YYYY"));
                registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                                periodoNomina);
                registro.getCampos().put(VacacionesControladorEnum.ID_DE_PROCESO
                                .getValue(), procesoNomina);
                registro.getCampos()
                                .put(VacacionesControladorEnum.ID_DE_EMPLEADO
                                                .getValue(), idEmpleado);

                registro.getCampos().put(
                                VacacionesControladorEnum.DIASPENDIENTESDEDISFRUTE
                                                .getValue(),
                                "0");
                registro.getCampos()
                                .put(VacacionesControladorEnum.ID_DE_CONCEPTO
                                                .getValue(), 403);
                sabadoHabil = getParametro(
                                VacacionesControladorEnum.CONTAR_SABADOS_COMO_DIA_HABIL
                                                .getValue(),
                                true);
                sabadoHabil = sabadoHabil.toUpperCase();
                confirmarHabiles = false;
                cambiarFechaInicio();
            }

            catch (ParseException | NumberFormatException
                            | SystemException ex) {
                JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                                idioma.getString(
                                                VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                                .getValue()),
                                ex.getMessage()));
                Logger.getLogger(VacacionesControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        if (ACCION_MODIFICAR.equals(accion)) {
            camposBloqueados = true;
        }
        verMensajeVacLic = false;
        
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {

        if (!validarFechaIniActo()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3757"));
            return false;
        }
        if (!validarFechaIniDisfrute()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3759"));
            return false;
        }
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        try {
            if (SysmanFunciones.calcularDiferenciaDias(
                            (Date) registro.getCampos().get(
                                            VacacionesControladorEnum.FECHA_INICIO
                                                            .getValue()),
                            (Date) registro.getCampos().get(
                                            VacacionesControladorEnum.FECHA_FINAL
                                                            .getValue())) < 360) {
                String msg1 = idioma.getString("TB_TB2670");
                JsfUtil.agregarMensajeFatal(msg1);
                return false;
            }
            else {
                if ("0".equals(
                                registro.getCampos().get(
                                                VacacionesControladorEnum.DIASPENDIENTESDEDISFRUTE
                                                                .getValue()))) {
                    setRta(0);
                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    VacacionesControladorUrlEnum.URL3322
                                                                    .getValue());
                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    parametros.put(GeneralParameterEnum.FECHA_INICIO.getName(),
                                    SysmanFunciones.convertirAFechaCadena(
                                                    (Date) registro.getCampos()
                                                                    .get(VacacionesControladorEnum.FECHA_INICIO
                                                                                    .getValue()),
                                                    VacacionesControladorEnum.FORMATO
                                                                    .getValue()));

                    parametros.put(GeneralParameterEnum.FECHA_FINAL.getName(),
                                    SysmanFunciones.convertirAFechaCadena(
                                                    (Date) registro.getCampos()
                                                                    .get(VacacionesControladorEnum.FECHA_FINAL
                                                                                    .getValue()),
                                                    VacacionesControladorEnum.FORMATO
                                                                    .getValue()));

                    parametros.put(VacacionesControladorEnum.INICIO_DISFRUTE
                                    .getValue(),
                                    SysmanFunciones.convertirAFechaCadena(
                                                    (Date) registro.getCampos()
                                                                    .get(VacacionesControladorEnum.INICIO_DISFRUTE
                                                                                    .getValue()),
                                                    VacacionesControladorEnum.FORMATO
                                                                    .getValue()));

                    parametros.put(VacacionesControladorEnum.FINAL_DISFRUTE
                                    .getValue(),
                                    SysmanFunciones.convertirAFechaCadena(
                                                    (Date) registro.getCampos()
                                                                    .get(VacacionesControladorEnum.FINAL_DISFRUTE
                                                                                    .getValue()),
                                                    VacacionesControladorEnum.FORMATO
                                                                    .getValue()));

                    parametros.put(VacacionesControladorEnum.ID_DE_EMPLEADO
                                    .getValue(),
                                    registro.getCampos().get(
                                                    VacacionesControladorEnum.ID_DE_EMPLEADO
                                                                    .getValue()));
                    Parameter parameter = new Parameter();
                    parameter.setFields(parametros);

                    setRta(requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(), parameter));
                }
            }

        }
        catch (ParseException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
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

        try {        	
        	
        	boolean activo = false;
        	
        	if ("i".equals(accion)) {        		
        		
        		 activo = ejbNominaUno.getPeriodoActivadoFecha(compania,
                         Integer.parseInt(procesoNomina), fechaPago,
                         Integer.parseInt(periodoNomina));
        		 if (!activo) {
                     JsfUtil.agregarMensajeInformativo(
                                     idioma.getString("TB_TB2553"));
                     return false;
                 }
        	}
        	
        	if ("m".equals(accion)) {        
        		
        		fechaPago = (Date) registro.getCampos().get(
                        VacacionesControladorEnum.FECHAPAGO.getValue());
        		
        		activo = verificarFechaActo();
        		
        		 if (!activo) {
                     JsfUtil.agregarMensajeAlerta(
                                     idioma.getString("TB_TB4425"));
                     return false;
                 }
        	}        	        	           
           

            if (!verificarPeriodoFechaFin()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4281")
                                .replace("#$anio#$", String
                                                .valueOf(SysmanFunciones.ano(
                                                                (Date) registro.getCampos()
                                                                                .get(VacacionesControladorEnum.FINAL_DISFRUTE
                                                                                                .getValue()))))
                                .replace("#$mes#$", String
                                                .valueOf(SysmanFunciones.mes(
                                                                (Date) registro.getCampos()
                                                                                .get(VacacionesControladorEnum.FINAL_DISFRUTE
                                                                                                .getValue()))))
                                .replace("#$periodo#$", periodoNomina)
                                .replace("#$fechaInicial#$", SysmanFunciones
                                                .convertirAFechaCadena(
                                                                (Date) registro.getCampos()
                                                                                .get(VacacionesControladorEnum.INICIO_DISFRUTE
                                                                                                .getValue())))
                                .replace("#$fechaFinal#$", SysmanFunciones
                                                .convertirAFechaCadena(
                                                                (Date) registro.getCampos()
                                                                                .get(VacacionesControladorEnum.FINAL_DISFRUTE
                                                                                                .getValue()))));
                return false;
            }
                                    

            registro.getCampos().put(
                            VacacionesControladorEnum.FECHAPAGO.getValue(),
                            fechaPago);

            if (fechaPago == null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2671"));
                return false;
            }
            String mesActual = SysmanFunciones.convertirAFechaCadena(fechaPago,
                            "MM");
            String anoActual = SysmanFunciones.convertirAFechaCadena(fechaPago,
                            "YYYY");
            int diasDinero = Integer
                            .parseInt(registro.getCampos().get(
                                            VacacionesControladorEnum.DIASDINERO
                                                            .getValue())
                                            .toString());

            int dias =
            		Integer
                            .parseInt(ejbSysmanUtil
                                            .calcularDiferenciaEntreFechasVac(
                                                            fecInicial == null
                                                                ? (Date) registro
                                                                                .getCampos()
                                                                                .get(VacacionesControladorEnum.INICIO_DISFRUTE
                                                                                                .getValue())
                                                                : fecInicial,
                                                            (Date) registro.getCampos()
                                                                            .get(VacacionesControladorEnum.FINAL_DISFRUTE
                                                                                            .getValue()),
                                                            3,
                                                            0));
            /*CC_3849(29/04/2026 JCROJAS): Se comentan lineas ya que calcula bien los dias en la funcion calcularDiferenciaEntreFechasVac
            dias = meses(mesInicioDis, mesFinalDis, diaFinalDis, pagar31,
                    diaIniciaDis, dias);*/
            
            if (diasDinero > dias) {
                JsfUtil.agregarMensajeInformativo(idioma
                                .getString(VacacionesControladorEnum.TB_TB2666
                                                .getValue()));
                return false;
            }
            validar(mesActual, anoActual);
        }
        catch (ParseException | NumberFormatException | SystemException ex) {
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        return true;
    }
    
    /**
     * Verifica que la fecha de acto se mayor o igual al primer dia del mes justamente anterior del mes de la fecha de pago
     * 
     * @return true
     */
    public boolean verificarFechaActo() {
    	// Obtenemos la fecha pago
        Date fechaPagoVal = (Date) registro.getCampos()
                .get(VacacionesControladorEnum.FECHAPAGO.getValue());
        
        // Convertir la fecha pago a LocalDate
        LocalDate localFechaPago = fechaPagoVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Obtener el primer dia del mes anterior
        LocalDate primerDiaMesAnterior = localFechaPago.minusMonths(1).withDayOfMonth(1);
        
        // Obtenemos la fecha acto
	    Date fechaActoVal = (Date) registro.getCampos()
	            .get(VacacionesControladorEnum.FECHA_ACTO.getValue());
     // Convertir la fecha acto de String a LocalDate
        LocalDate localFechaActo = fechaActoVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        
        if (localFechaActo.isAfter(primerDiaMesAnterior) || localFechaActo.equals(primerDiaMesAnterior) ) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Verifica que el periodo correspondiente a la fecha final
     * seleccionada se encuentre registrado en el sistema
     * 
     * @return true
     * @throws ParseException 
     * @throws NumberFormatException 
     */
    public boolean verificarPeriodoFechaFin() throws SystemException, NumberFormatException, ParseException {
        Date fecha = (Date) registro.getCampos().get(
                        VacacionesControladorEnum.FINAL_DISFRUTE.getValue());
        //MOD JM CC 2893
        if ("SI".equals(getParametro(
                "MANEJA NOMINA HIBRIDA",
                true))) {
        		
        	    return ejbNominaTres.validarPeriodoActivoNominaH(compania,
				        Integer.parseInt(procesoNomina),
				        SysmanFunciones.ano(fecha), SysmanFunciones.mes(fecha)
				        ,SysmanFunciones.convertirAFechaCadena(fecha));
   	
        }else {
        	
        	return ejbNominaCero.validarPeriodoActivoNomina(compania,
                    Integer.parseInt(procesoNomina),
                    SysmanFunciones.ano(fecha), SysmanFunciones.mes(fecha),
                    Integer.parseInt(periodoNomina));
        	
        }
        
        
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        diferirVac(null);
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        cargarRegistro(registro.getLlave(), "e", 0);
        try {

            fechaPago = (Date) registro.getCampos().get(
                            VacacionesControladorEnum.FECHAPAGO.getValue());
            if (registro.getCampos()
                            .get(VacacionesControladorEnum.FECHA_FINAL
                                            .getValue()) != null
                && registro.getCampos()
                                .get(VacacionesControladorEnum.FECHA_INICIO
                                                .getValue()) != null) {
            	boolean activo;
            	
            	if ("SI".equals(getParametro(
                        "MANEJA NOMINA HIBRIDA",
                        true))) { //MOD JM CC CC2893
            		
            		activo = ejbNominaTres.validarPeriodoActivoNominaH(compania,
    				        Integer.parseInt(procesoNomina),
    				        SysmanFunciones.ano(fechaPago), SysmanFunciones.mes(fechaPago)
    				        ,SysmanFunciones.convertirAFechaCadena(fechaPago));
            		
            	}else {
            		
            		activo = ejbNominaUno.getPeriodoActivadoFecha(compania,
                            Integer.parseInt(procesoNomina),
                            fechaPago, Integer.parseInt(periodoNomina));
            	}
                 
                
                if (!activo) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2630"));
                    return false;
                }
                diferirVac("BORRAR");
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                VacacionesControladorUrlEnum.URL3323
                                                                .getValue());
                Map<String, Object> param = new HashMap<>();
                param.put(VacacionesControladorEnum.ID_DE_EMPLEADO.getValue(),
                                registro.getCampos().get(
                                                VacacionesControladorEnum.ID_DE_EMPLEADO
                                                                .getValue()));
                param.put(VacacionesControladorEnum.FECHA_INICIO.getValue(),
                                registro.getCampos().get(
                                                VacacionesControladorEnum.FECHA_INICIO
                                                                .getValue()));
                param.put(VacacionesControladorEnum.FECHA_FINAL.getValue(),
                                registro.getCampos().get(
                                                VacacionesControladorEnum.FECHA_FINAL
                                                                .getValue()));
                param.put(VacacionesControladorEnum.DIASPENDIENTESDEDISFRUTE
                                .getValue(),
                                registro.getCampos().get(
                                                VacacionesControladorEnum.DIASPENDIENTESDEDISFRUTE
                                                                .getValue()));

                requestManager.delete(urlUpdate.getUrl(), param);

                String liqVac = getParametro(
                                "LIQUIDAN VACACIONES DENTRO DE NOMINA", true);
                if ("NO".equals(liqVac)) {

                    UrlBean urlUpdateAux = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    VacacionesControladorUrlEnum.URL3326
                                                                    .getValue());
                    Map<String, Object> params = new HashMap<>();
                    param.put(VacacionesControladorEnum.ID_DE_PROCESO
                                    .getValue(),
                                    registro.getCampos().get(
                                                    VacacionesControladorEnum.ID_DE_PROCESO
                                                                    .getValue()));

                    param.put(GeneralParameterEnum.ANO.getName(),
                                    registro.getCampos().get(
                                                    GeneralParameterEnum.ANO
                                                                    .getName()));

                    param.put(GeneralParameterEnum.MES.getName(),
                                    registro.getCampos().get(
                                                    GeneralParameterEnum.MES
                                                                    .getName()));

                    param.put(GeneralParameterEnum.PERIODO.getName(),
                                    Integer.parseInt(getParametro(
                                                    "PERIODO NOMINA ADICIONAL",
                                                    false)));

                    param.put(VacacionesControladorEnum.ID_DE_EMPLEADO
                                    .getValue(),
                                    registro.getCampos().get(
                                                    VacacionesControladorEnum.ID_DE_EMPLEADO
                                                                    .getValue()));

                    requestManager.delete(urlUpdateAux.getUrl(), params);
                }
//                Date fechaFin = ejbNominaCero.getFechaPeriodoIniFin(compania,
//                                Integer.parseInt(procesoNomina),
//                                Integer.parseInt(anioNomina),
//                                Integer.parseInt(mesNomina),
//                                Integer.parseInt(periodoNomina),
//                                false, true);
//
//                ejbNominaCero.actualizarVacacionesEliminar(compania,
//                                Integer.parseInt(registro.getCampos().get(
//                                                VacacionesControladorEnum.ID_DE_EMPLEADO
//                                                                .getValue())
//                                                .toString()),
//                                Integer.parseInt(periodoNomina), fechaFin,
//                                SessionUtil.getUser().getCodigo());
            }
            else {
                return false;
            }
        }
        catch (NumberFormatException | SystemException | ParseException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(VacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            return false;
        }
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
    	String parLiqVacNomina = getParametro("LIQUIDAN VACACIONES DENTRO DE NOMINA", true);
    	
    	if("NO".equals(parLiqVacNomina)){
    		JsfUtil.agregarMensajeInformativoDialogo(idioma.getString("TB_TB4394"));
    	}
    	try {
    		  if (registro.getCampos()
                      .get(VacacionesControladorEnum.FECHA_FINAL
                                      .getValue()) != null
                                      && registro.getCampos()
                       .get(VacacionesControladorEnum.FECHA_INICIO
                                          .getValue()) != null) 
    		  {

    			  Date fechaFin = ejbNominaCero.getFechaPeriodoIniFin(compania,
    					  Integer.parseInt(procesoNomina),
    					  Integer.parseInt(anioNomina),
    					  Integer.parseInt(mesNomina),
    					  Integer.parseInt(periodoNomina),
    					  false, true);

    			  ejbNominaCero.actualizarVacacionesEliminar(compania,
    					  Integer.parseInt(registro.getCampos().get(
                                 VacacionesControladorEnum.ID_DE_EMPLEADO
                                                 .getValue())
                                 .toString()),
                 Integer.parseInt(periodoNomina), fechaFin,
                 SessionUtil.getUser().getCodigo());
    		  }
    	  }
    	catch (NumberFormatException | SystemException ex) {
    		JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                     idioma.getString(
                                     VacacionesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                     .getValue()),
                     ex.getMessage()));
    		Logger.getLogger(VacacionesControlador.class.getName())
                     .log(Level.SEVERE, null, ex);
    		return false;
    	}
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void ejecutarrcCerrar() {
        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put("idEmpleado", idEmpleado);
        parametros.put("cedula", cedula);
        parametros.put("nombreEmpleado", nombreEmpleado);
        parametros.put("vacaciones", vacaciones);
        parametros.put("cesantias", cesantias);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(String
                        .valueOf(GeneralCodigoFormaEnum.NOVEDADES_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, moduloNomina);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable rta
     * 
     * @return rta
     */
    public int getRta() {
        return rta;
    }

    /**
     * Asigna la variable rta
     * 
     * @param rta
     * Variable a asignar en rta
     */
    public void setRta(int rta) {
        this.rta = rta;
    }

    /**
     * Retorna la variable idEmpleado
     * 
     * @return idEmpleado
     */
    public String getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Asigna la variable idEmpleado
     * 
     * @param idEmpleado
     * Variable a asignar en idEmpleado
     */
    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    /**
     * Retorna la variable cedula
     * 
     * @return cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Asigna la variable cedula
     * 
     * @param cedula
     * Variable a asignar en cedula
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * Retorna la variable tituloForm
     * 
     * @return tituloForm
     */
    public String getTituloForm() {
        return tituloForm;
    }

    /**
     * Asigna la variable tituloForm
     * 
     * @param tituloForm
     * Variable a asignar en tituloForm
     */
    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

    /**
     * Retorna la variable nombreEmpleado
     * 
     * @return nombreEmpleado
     */
    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    /**
     * Asigna la variable nombreEmpleado
     * 
     * @param nombreEmpleado
     * Variable a asignar en nombreEmpleado
     */
    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    /**
     * Retorna la variable verDuplicado
     * 
     * @return verDuplicado
     */
    public boolean isVerDuplicado() {
        return verDuplicado;
    }

    /**
     * Asigna la variable verDuplicado
     * 
     * @param verDuplicado
     * Variable a asignar en verDuplicado
     */
    public void setVerDuplicado(boolean verDuplicado) {
        this.verDuplicado = verDuplicado;
    }

    /**
     * Retorna la variable mensajeVacLic
     * 
     * @return mensajeVacLic
     */
    public String getMensajeVacLic() {
        return mensajeVacLic;
    }

    /**
     * Asigna la variable mensajeVacLic
     * 
     * @param mensajeVacLic
     * Variable a asignar en mensajeVacLic
     */
    public void setMensajeVacLic(String mensajeVacLic) {
        this.mensajeVacLic = mensajeVacLic;
    }

    /**
     * Retorna la variable verMensajeVacLic
     * 
     * @return verMensajeVacLic
     */
    public boolean isVerMensajeVacLic() {
        return verMensajeVacLic;
    }

    /**
     * Asigna la variable verMensajeVacLic
     * 
     * @param verMensajeVacLic
     * Variable a asignar en verMensajeVacLic
     */
    public void setVerMensajeVacLic(boolean verMensajeVacLic) {
        this.verMensajeVacLic = verMensajeVacLic;
    }

    /**
     * Retorna la variable confirmarHabiles
     * 
     * @return confirmarHabiles
     */
    public boolean isConfirmarHabiles() {
        return confirmarHabiles;
    }

    /**
     * Asigna la variable confirmarHabiles
     * 
     * @param confirmarHabiles
     * Variable a asignar en confirmarHabiles
     */
    public void setConfirmarHabiles(boolean confirmarHabiles) {
        this.confirmarHabiles = confirmarHabiles;
    }

    /**
     * Retorna la variable pila
     * 
     * @return pila
     */
    public boolean isPila() {
        return pila;
    }

    /**
     * Asigna la variable pila
     * 
     * @param pila
     * Variable a asignar en pila
     */
    public void setPila(boolean pila) {
        this.pila = pila;
    }

    /**
     * Retorna la variable camposBloqueados
     * 
     * @return camposBloqueados
     */
    public boolean isCamposBloqueados() {
        return camposBloqueados;
    }

    /**
     * Asigna la variable camposBloqueados
     * 
     * @param camposBloqueados
     * Variable a asignar en camposBloqueados
     */
    public void setCamposBloqueados(boolean camposBloqueados) {
        this.camposBloqueados = camposBloqueados;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la variable listaFechaPago
     * 
     * @return listaFechaPago
     */
    public RegistroDataModelImpl getListaFechaPago() {
        return listaFechaPago;
    }

    /**
     * Asigna la variable listaFechaPago
     * 
     * @param listaFechaPago
     * Variable a asignar en listaFechaPago
     */
    public void setListaFechaPago(RegistroDataModelImpl listaFechaPago) {
        this.listaFechaPago = listaFechaPago;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

	public boolean isVerAplicaBonificacion() {
		return verAplicaBonificacion;
	}

	public void setVerAplicaBonificacion(boolean verAplicaBonificacion) {
		this.verAplicaBonificacion = verAplicaBonificacion;
	}
}
