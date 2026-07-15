package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.EncargosControladorEnum;
import com.sysman.nomina.enums.NovedadesControladorEnum;
import com.sysman.nomina.enums.NovedadesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.services.ServidorCorreo;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.reporte.PrepararReporte;
import com.sysman.util.reporte.RetornoReporte;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jgomez
 * @version 1, 25/07/2015
 *
 * Revision Sonar
 * @author ybecerra
 * @version 2, 22/03/2017
 *
 * Se eliminaron parametros de ActionEvent ac llamados en los metodos
 * de los botones
 * @author ybecerra
 * @version 3, 23/03/2017
 *
 * @author jcrodriguez, Refactoring y depuracion
 * @version 4, 04/10/2017
 *
 * @author lcortes
 * @version 4, 16/11/2017. Se cambia el metodo cargarModalDatosFlash
 * por el metodo cargarModalDatosFlashCerrar en el metodo
 * oprimirBtQuinquenio.
 * 
 * @author jmalaver
 * @version 5, 20/04/2018. Se agrega metodo ejecutarrcerrar. Se
 * realizan validaciones de visualizaciï¿½n de botones "vacaciones" y
 * "casantias".
 */
@ManagedBean
@ViewScoped
public class NovedadesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida que almacena el codigo del modulo de la
     * aplicacion por la que se ingresa
     */
    private final String moduloNomina;
    /**
     * Constante definida para almacenar la cadena "idEmpleado"
     */
    private final String cIdEmpleado;
    /**
     * Constante definida para almacenar la cadena "nombreEmpleado"
     */
    private final String cNombreEmpleado;
    private String companiaNombre;
    private String companiaNit;
    private String procesoNomina;
    private String anioNomina;
    private String mesNomina;
    private String periodoNomina;
    private String nombreMes;
    private String nombrePeriodo;
    private String desProceso;
    private boolean activo;
    private String idDeEmpleado;
    private String tipo;
    private String cedula;
    private String cNombreConcepto;
    private String email;
    private String emailCorporativo;
    private String nombre;
    private String auxiliar;
    private String idProceso;
    private String ano;
    private String mes;
    private String periodo;
    private String titulo;
    private String tituloForm;
    private boolean retro;
    private boolean vacaciones;
    private boolean cesantias;
    private boolean visibleNovedades; //JM CC 4561
    private String dgAno;
    private ServidorCorreo correo;
    private List<Registro> listaDgAno;
    private RegistroDataModelImpl listaiddeempleado;
    private RegistroDataModelImpl listaiddeempleadoE;
    private RegistroDataModelImpl listaIDdeConcepto;
    private RegistroDataModelImpl listaIDdeConceptoE;
    private RegistroDataModel listaIDdeConceptoA;
    private StreamedContent archivoDescarga;
    private String mensaje;
    private Map<String, Object> parametrosEntrada;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbNominaUnoRemote ejbNominaUno;
    @EJB
    private EjbNominaCeroRemote ejbNominaCero;
    @EJB
    private EjbNominaSieteRemote ejbNominaSiete;
    @EJB
    private EjbNominaDosRemote ejbNominaDos;
    
    String cargoAutorizaPago;

    /**
     * Creates a new instance of NovedadesControlador
     */
    public NovedadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloNomina = SessionUtil.getModulo();
        correo = new ServidorCorreo();
        cIdEmpleado = "idEmpleado";
        cNombreEmpleado = "nombreEmpleado";
        cNombreConcepto = "Nombre_Concepto";

        tituloForm = idioma.getString("TB_TB3698");

        try {
            companiaNombre = SessionUtil.getCompaniaIngreso().getNombre();
            companiaNit = SessionUtil.getCompaniaIngreso().getNit();
            procesoNomina = validarSessionCadena(
                            SessionUtil.getSessionVar("procesoNomina"));
            anioNomina = validarSessionCadena(
                            SessionUtil.getSessionVar("anioNomina"));
            mesNomina = validarSessionCadena(
                            SessionUtil.getSessionVar("mesNomina"));
            periodoNomina = validarSessionCadena(
                            SessionUtil.getSessionVar("periodoNomina"));
            nombreMes = validarSessionCadena(
                            SessionUtil.getSessionVar("nombreMesNomina"));
            nombrePeriodo = validarSessionCadena(
                            SessionUtil.getSessionVar("nombrePeriodoNomina"));
            desProceso = SysmanFunciones.concatenar(" ",
                            idioma.getString("TG_NOVEDADES"), " ",
                            idioma.getString("TG_DE2"), " ",
                            validarSessionCadena(SessionUtil.getSessionVar(
                                            "nombreProcesoNomina")));
            activo = Boolean
                            .parseBoolean(SysmanFunciones.nvl(
                                            SessionUtil.getSessionVar(
                                                            "periodoActivo"),
                                            "false").toString());
            numFormulario = GeneralCodigoFormaEnum.NOVEDADES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();
        }
        catch (Exception ex) {
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    private String validarSessionCadena(Object objeto) {
        return SysmanFunciones.validarVariableVacio(objeto.toString()) ? ""
            : objeto.toString();
    }

    private String validarCampoCadena(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    private String getParametro(String nombre, boolean indMayus, Date date) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            moduloNomina, date, indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.NOVEDADES.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaiddeempleado();
        cargarListaIDdeConcepto();
        cargarListaIDdeConceptoE();
        cargarlistaDgAno();
        abrirFormulario();
        recibirParametros();

    }

    private void recibirParametros() {
        if (parametrosEntrada != null) {
            idDeEmpleado = validarCampoCadena(parametrosEntrada, "idEmpleado");
            cedula = validarCampoCadena(parametrosEntrada,
                            NovedadesControladorEnum.CEDULA.getValue()
                                            .toLowerCase());
            nombre = validarCampoCadena(parametrosEntrada, "nombreEmpleado");
            vacaciones = (boolean) SysmanFunciones
                            .nvl(parametrosEntrada.get("vacaciones"), false);
            cesantias = (boolean) SysmanFunciones
                            .nvl(parametrosEntrada.get("cesantias"), false);
            reasignarOrigen();
            cargarForma();
            parametrosEntrada.clear();
        }

    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(NovedadesControladorEnum.COMPANIA_AUX.getValue(),
                        compania);
        parametrosListado.put(
                        NovedadesControladorEnum.ID_DE_PROCESO_AUX.getValue(),
                        idProceso);
        parametrosListado.put(NovedadesControladorEnum.ANO_AUX.getValue(), ano);
        parametrosListado.put(NovedadesControladorEnum.MES_AUX.getValue(), mes);
        parametrosListado.put(NovedadesControladorEnum.PERIODO_AUX.getValue(),
                        periodo);
        parametrosListado.put(
                        NovedadesControladorEnum.ID_DE_EMPLEADO_AUX.getValue(),
                        idDeEmpleado);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NovedadesControladorUrlEnum.URL28529
                                                        .getValue());
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NovedadesControladorUrlEnum.URL15494
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        GenericUrlEnum.NOVEDADES.getCreateKey());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.NOVEDADES
                                        .getUpdateKey());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.NOVEDADES
                                        .getDeleteKey());
         
        
         try {
        	 

             String fechanovedad = "01/"+Integer.parseInt(mesNomina)+"/"+Integer.parseInt(anioNomina);
             SimpleDateFormat formato =new SimpleDateFormat("dd/MM/yyyy");  
             Date fecha_novedad = formato.parse(fechanovedad); 
             
             if (idDeEmpleado != null  && !idDeEmpleado.equals("")) {
     			ejbNominaDos.getIncluirNovedadEncargos(
     					Integer.parseInt(mesNomina),
     					Integer.parseInt(periodoNomina),
     					Integer.parseInt(anioNomina),
     			        Integer.parseInt(procesoNomina), compania, 
     			        (Date) fecha_novedad,
     			        (Date) fecha_novedad, 
     			        SessionUtil.getUser().getCodigo(),
     			        (int) Integer.parseInt(idDeEmpleado));
              }
             
             //JM INI CC 4561
             this.visibleNovedades = false;
             
             String nominaMensual = getParametro(
                     "NOMINA MENSUAL", false,
                     new Date());
             
             if("SI".equalsIgnoreCase(nominaMensual)) {
             	if("3".equalsIgnoreCase(periodoNomina) || "03".equalsIgnoreCase(periodoNomina)) {
             		this.visibleNovedades = true;
             	}
             }else{
             	if("1".equalsIgnoreCase(periodoNomina) || "01".equalsIgnoreCase(periodoNomina) || "2".equalsIgnoreCase(periodoNomina) || "02".equalsIgnoreCase(periodoNomina)) {
             		this.visibleNovedades = true;
             	}
             }
           //JM FIN CC 4561
             
             
             
		} catch (NumberFormatException | SystemException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }

    /**
     * Metodo llamado en cambiarTipo
     */
    private void asignarValores() {
        idProceso = procesoNomina;
        ano = anioNomina;
        mes = mesNomina;
        periodo = periodoNomina;
    }

    public void cambiarTipo() {
        mes = "";
        ano = "";
        periodo = "";
        idProceso = "";
        cedula = "";
        nombre = "";
        email = null;
        emailCorporativo = null;
        if (tipo != null) {
            switch (tipo)
            {
            case "1":
                idProceso = ano = mes = periodo = idDeEmpleado = "0";
                break;
            case "2":
                idProceso = ano = mes = periodo = "0";
                idDeEmpleado = null;
                break;
            case "3":
                asignarValores();
                idDeEmpleado = "0";
                break;
            case "4":
                asignarValores();
                idDeEmpleado = null;
                break;
            default:
                idProceso = ano = mes = periodo = "";
                idDeEmpleado = null;
                tipo = null;
                break;
            }
        }
        else {
            idProceso = "";
            ano = "";
            mes = "";
            periodo = "";
            idDeEmpleado = null;
        }
        armaTitulo();
        vacaciones = false;
        cesantias = false;
        reasignarOrigen();
        cargarForma();
    }

    public void aceptarkardex() {

        archivoDescarga = null;
        try {
            String condicionPivot = ejbNominaUno.getPivotKardexNomina(compania,
                            Integer.parseInt(dgAno),
                            Integer.parseInt(idDeEmpleado),
                            Integer.parseInt(idDeEmpleado), 1, new Long("1"),
                            true);

            if (SysmanFunciones.validarVariableVacio(condicionPivot)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1854"));
                return;
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(NovedadesControladorEnum.EMPLEADO.getValue()
                            .toLowerCase(), idDeEmpleado);
            reemplazar.put("anoIni", dgAno);
            reemplazar.put(NovedadesControladorEnum.PIVOT.getValue()
                            .toLowerCase(), condicionPivot);
            reemplazar.put(NovedadesControladorEnum.ALIAS.getValue()
                            .toLowerCase(),
                            // SysmanFunciones.padl(SysmanFunciones.concatenar(
                            // "Ced_", cedula, "_", nombre.trim()
                            // .replace(" ", "_")),
                            // 30, " ")
                            cNombreConcepto);
            String strSql = Reporteador.resuelveConsulta(
                            NovedadesControladorEnum.REPORTE900001.getValue(),
                            Integer.parseInt(moduloNomina), reemplazar);
            Long contar = Long
                            .parseLong(Integer.toString(service.getListado(
                                            ConectorPool.ESQUEMA_SYSMAN, strSql)
                                            .size()));
            if (contar > 0) {

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma
                                .getString(NovedadesControladorEnum.TG_NO_EXISTE
                                                .getValue()));
            }
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_INFORME_NO_EXISTE
                                                            .getValue()),
                            " ", ex.getMessage(),
                            " ",
                            NovedadesControladorEnum.REPORTE900001.getValue()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (DRException | IOException | JRException | SQLException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaiddeempleado() {

        try {
            Date fechaFin = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anioNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), false,
                            true);
            Date fechaIni = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anioNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), true,
                            true);
            if ((fechaIni == null) || (fechaFin == null)) {
                SessionUtil.setSessionVar("mensajeError",
                                idioma.getString("TB_TB2633"));
                SessionUtil.redireccionarMenu();
            }
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            NovedadesControladorUrlEnum.URL17630
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(NovedadesControladorEnum.FECHAFIN.getValue(), fechaFin);
            param.put(NovedadesControladorEnum.FECHAINI.getValue(), fechaIni);
            listaiddeempleado = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true,
                            NovedadesControladorEnum.ID_DE_EMPLEADO.getValue());

        }
        catch (SystemException | NumberFormatException ex) {

            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void cambiarIDdeConceptoC(int rowNum) {
        // heredado del bean base
    }

    public void cargarListaIDdeConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NovedadesControladorUrlEnum.URL17692
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaIDdeConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        NovedadesControladorEnum.ID_DE_CONCEPTO.getValue());
    }

    public void cargarListaIDdeConceptoE() {
        listaIDdeConceptoE = listaIDdeConcepto;
    }

    /**
     * Metodo que valida si el IdEmpleado o IdProceso viene vacio , o
     * nulo o 0
     *
     * @return true o false
     */
    private boolean validarVacios() {
        if ("0".equals(idDeEmpleado)
            || SysmanFunciones.validarVariableVacio(idDeEmpleado)) {
            return true;
        }

        if ("0".equals(idProceso)
            || SysmanFunciones.validarVariableVacio(idProceso)) {
            return true;
        }
        return false;
    }

    /**
     * Metodo llamado en oprimirCalculo
     *
     * @return true o false
     */
    private boolean validarPeriodo() {
        if (!"0".equals(mes) || SysmanFunciones.validarVariableVacio(mes)) {
            return true;
        }

        if ("0".equals(ano) || SysmanFunciones.validarVariableVacio(ano)) {
            return true;

        }
        if ("0".equals(periodo)
            || SysmanFunciones.validarVariableVacio(periodo)) {
            return true;
        }
        return false;
    }

    public void oprimirCalculo() {
        String sPer = periodo;
        String sMes = mes;
        String sAno = ano;
        archivoDescarga = null;
        if (validarVacios()) {
            return;
        }
        if (validarPeriodo()) {
            sPer = periodoNomina;
            sMes = mesNomina;
            sAno = anioNomina;
        }
        try {
            if (sPer.compareTo("3") <= 0) {
                ejbNominaCero.actualizarTraslados(compania,
                                Integer.parseInt(idProceso),
                                Integer.parseInt(ano),
                                Integer.parseInt(mes),
                                Integer.parseInt(periodo),
                                SessionUtil.getUser().getCodigo());

            }
            String rutina = SysmanFunciones
                            .concatenar("FC_PROC", SysmanFunciones
                                            .strZero(idProceso, 2))
                            .toUpperCase();
            String idInicial = idDeEmpleado;
            String idFinal = idDeEmpleado;

            mensaje = ejbNominaSiete.liquidarNomina(compania,
                            Integer.parseInt(idProceso), idInicial, idFinal,
                            rutina,
                            Integer.parseInt(sAno), Integer.parseInt(sMes),
                            Integer.parseInt(sPer),
                            SessionUtil.getUser().getCodigo());
            ejecutaractualizarMensaje();
            // 19/09/2024 se actualizan automaticamente las cesantias si el parametro esta activo en SI - ticket 7741791
            String parametroCesanAuto = SysmanFunciones.nvlStr(getParametro(
                    "MIGRAR A CESANTIAS AUTO", false,
                    new Date()), "NO");
            if(parametroCesanAuto.equals("SI")) {
            	try {
            			ejbNominaSiete.almacenarCesantiasAuto(compania,
	                        Integer.parseInt(idProceso), 
	                        idDeEmpleado,
	                        Integer.parseInt(sAno), Integer.parseInt(sMes),
	                        Integer.parseInt(sPer),
	                        SessionUtil.getUser().getCodigo());
            	}catch(SystemException e) {
            		if (!e.getMessage().toString().contains("Ya existe información migrada para este periodo.")) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
            	}
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            HashMap<String, Object> parametros = new HashMap<>();
            reemplazar.put(NovedadesControladorEnum.EMPLEADO.getValue()
                            .toLowerCase(), idDeEmpleado);
            Reporteador.resuelveConsulta(
                            NovedadesControladorEnum.REPORTE000070.getValue(),
                            Integer.parseInt(moduloNomina), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            NovedadesControladorEnum.REPORTE000070.getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_INFORME_NO_EXISTE
                                                            .getValue()),
                            " ", ex.getMessage(),
                            " ",
                            NovedadesControladorEnum.REPORTE000070.getValue()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException | SysmanException e) {
            if (!("No existen datos").equals(e.getMessage().toString())) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void ejecutaractualizarMensaje() {
        if (!SysmanFunciones.validarVariableVacio(mensaje)) {
            JsfUtil.agregarMensajeInformativo(mensaje);
        }
        reasignarOrigen();
        cargarForma();
    }

    private String getReoprte() {
        String parReporte = getParametro(
                        "INFORME DE VOLANTES DE PAGO POR PANTALLA", false,
                        new Date());

        if (SysmanFunciones.validarVariableVacio(parReporte)) {
            return NovedadesControladorEnum.REPORTE000141.getValue();
        }
        else {
        	return parReporte;
        }
        
    }

    private void genVolante(ReportesBean.FORMATOS formato, boolean bolEmail) {
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        if (validarVacios()) {
            return;
        }

        String parReporte = getReoprte();


        RetornoReporte retornoReporte = new RetornoReporte();
        try {
            /**
             * MVENEGAS, se realiza cambio de parametros centro de
             * costo inicial y final por las constantes de sysman
             * constantes
             */
        	
            PrepararReporte prepararReporte = new PrepararReporte();
            try {
				retornoReporte = prepararReporte.preparaVolante(compania,
				                Integer.parseInt(idDeEmpleado.toString()),
				                Integer.parseInt(idDeEmpleado.toString()),
				                Integer.parseInt(idProceso.toString()),
				                Integer.parseInt(ano.toString()),
				                Integer.parseInt(mes.toString()),
				                Integer.parseInt(periodo.toString()),
				                SysmanConstantes.DEFECTOINICIAL_STRING,
				                SysmanConstantes.DEFECTOFINAL_STRING);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            				
            				
            				


        
        }
        catch (Exception e) {
            JsfUtil.agregarMensajeAlerta(
                            "Obteniendo parametros y reemplazos del informe");
        }
        reemplazar = retornoReporte.getReemplazar();
        parametros = retornoReporte.getParametros();
        String nombreEmpresa;
        String nombreCompania;
        String nomLiqNomina;
        String cargoLiqNomina;
		try {
			nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);
			nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
			nomLiqNomina = SysmanFunciones.nvlStr(getParametro("NOMBRE DE QUIEN LIQUIDA NOMINA", false,new Date()),"");
			cargoLiqNomina = SysmanFunciones.nvlStr(getParametro("CARGO DE QUIEN LIQUIDA NOMINA", false,new Date()),"");
			parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
			parametros.put("PR_NOMBRECOMPANIA",nombreCompania);
			parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA",nomLiqNomina);
			parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA",cargoLiqNomina);
			// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muï¿½oz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA  
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        

        try {
            Reporteador.resuelveConsulta(parReporte.toUpperCase(),
                            Integer.parseInt(moduloNomina), reemplazar,
                            parametros);
            if (!bolEmail) {
                long contar = service.getConteoConsulta(
                                parametros.get("PR_STRSQL").toString());
                if (contar > 0) {
                    archivoDescarga = JsfUtil.exportarStreamed(parReporte,
                                    parametros, ConectorPool.ESQUEMA_SYSMAN,
                                    formato);
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString(
                                    NovedadesControladorEnum.TG_NO_EXISTE
                                                    .getValue()));
                }

            }
            else if (!SysmanFunciones.validarVariableVacio(email)
                || !SysmanFunciones.validarVariableVacio(emailCorporativo)) {
                String strPara = SysmanFunciones.validarVariableVacio(
                                emailCorporativo) ? email : emailCorporativo;

                ByteArrayInputStream reporteSerializado = JsfUtil
                                .serializarReporteConstrasenia(parReporte,
                                                parametros,
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                formato, cedula);

                String strAsunto = idioma.getString("TB_TB3684")
                                .replace("s$anioNomina$s", anioNomina)
                                .replace(NovedadesControladorEnum.IDIOMANOMBREMES
                                                .getValue(), nombreMes)
                                .replace("s$periodoNomina$s", periodoNomina)
                                .replace(NovedadesControladorEnum.IDIOMANOMBREPERIODO
                                                .getValue(), nombrePeriodo);

                String strMensaje = idioma.getString("TB_TB3686")
                                .replace("s$companiaNombre$s", companiaNombre)
                                .replace("s$companiaNit$s", companiaNit)
                                .replace("s$anioNomina$s", anioNomina)
                                .replace("s$nombreMes$s", nombreMes)
                                .replace("s$periodoNomina$s", periodoNomina)
                                .replace("s$nombrePeriodo$s", nombrePeriodo);

                correo.enviarAdjunto(strPara, strAsunto, strMensaje,
                                idioma.getString("TB_TB3712"),
                                reporteSerializado,
                                "application/pdf");

            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2638"));
            }
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ", parReporte));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (SystemException | IOException | JRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirVolante() {
        genVolante(ReportesBean.FORMATOS.PDF, false);
    }

    public void cargarlistaDgAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaDgAno = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            NovedadesControladorUrlEnum.URL17629
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirKardex() {

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAcumulados() {
        String[] campos = { NovedadesControladorEnum.TIPO.getValue()
                        .toLowerCase(), "idDeEmpleado", cNombreEmpleado,
                            NovedadesControladorEnum.CEDULA.getValue()
                                            .toLowerCase() };
        String[] valores = { tipo, idDeEmpleado, nombre, cedula };
        SessionUtil.cargarModal(String.valueOf(
                        GeneralCodigoFormaEnum.ACUMULADOS_UNO_CONTROLADOR
                                        .getCodigo()),
                        moduloNomina, campos, valores);
    }

    public void oprimirVacaciones() {
        archivoDescarga = null;
        
        try {
            // com.sysman.utl.SysmanFunciones
            String strDocName;
            ejbNominaDos.getActualizarVacaPeriodo(compania,
                            Integer.parseInt(idProceso), Integer.parseInt(ano),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            Integer.parseInt(idDeEmpleado),
                            SessionUtil.getUser().getCodigo());

            strDocName = getParametro("FORMATO FACTORES VACACIONES", false,
                            new Date());

            String memorando = getParametro("FORMATO MEMORANDO VACACIONES",
                            false, new Date());
            
            String generaMemorando = SysmanFunciones.nvlStr(getParametro(
                    "GENERA MEMORANDO VACACIONES", false,
                    new Date()), "NO");

            if ("001844FACTORESVACACIONESGOBCAQUETA".equals(strDocName)
                && "001845MEMORANDOVACACIONESGOBCAQUETA".equals(memorando)) {

                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(NovedadesControladorEnum.EMPLEADO.getValue()
                                .toLowerCase(), idDeEmpleado);

                reemplazar.put("proceso", procesoNomina);
                reemplazar.put("ano", anioNomina);
                reemplazar.put("mes", mesNomina);
                Map<String, Object> parametros = new HashMap<>();
                String jefe = getParametro(
                                "NOMBRE DEL JEFE DE RECURSOS HUMANOS", true,
                                new Date());
                String cargoJefe = getParametro("CARGO JEFE RECURSOS HUMANOS",
                                true, new Date());
                String elaborado = getParametro("ELABORADO POR", true,
                                new Date());
                String nombreEmpresa = SessionUtil.getCompaniaIngreso()
                                .getNombre();

                String encabezado = getParametro(
                                "RUTA IMAGEN ENCABEZADO VOLANTE DE PAGOS", true,
                                new Date());
                String piePagina = getParametro(
                                "RUTA IMAGEN PIEPAGINA VOLANTE DE PAGOS", true,
                                new Date());
                parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS", jefe);
                parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefe);
                parametros.put("PR_ELABORADO_POR", elaborado);
                parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
                parametros.put("PR_NIT", companiaNit);
                parametros.put("PR_RUTA_IMAGEN_ENCABEZADO_VOLANTE_DE_PAGOS",
                                encabezado);
                parametros.put("PR_RUTA_IMAGEN_PIEPAGINA_VOLANTE_DE_PAGOS",
                                piePagina);
                parametros.put("PR_EXTRASEMESTRAL","805018833-8".equals(companiaNit));
                

                String[] informe = new String[2];
                informe[0] = "001844FACTORESVACACIONESGOBCAQUETA";
                informe[1] = "001845MEMORANDOVACACIONESGOBCAQUETA";
                Reporteador.resuelveConsulta(informe[0],
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazar,
                                parametros);

                ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

                salidas[0] = JsfUtil.serializarReporte(informe[0], parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.PDF);

                Reporteador.resuelveConsulta(informe[1],
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazar,
                                parametros);

                salidas[1] = JsfUtil.serializarReporte(informe[1], parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.PDF);
                String[] nombresArchivos = new String[2];

                if (ReportesBean.FORMATOS.PDF
                                .equals(ReportesBean.FORMATOS.PDF)) {
                    nombresArchivos[0] = "001844FACTORESVACACIONESGOBCAQUETA.pdf";
                    nombresArchivos[1] = "001845MEMORANDOVACACIONESGOBCAQUETA.pdf";
                }
                else {
                    nombresArchivos[0] = "001844FACTORESVACACIONESGOBCAQUETA.xlsx";
                    nombresArchivos[1] = "001845MEMORANDOVACACIONESGOBCAQUETA.xlsx";
                }

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas, nombresArchivos);

            }
            
            else if (generaMemorando.equals("SI")) {

                    HashMap<String, Object> reemplazar = new HashMap<>();
                    reemplazar.put(NovedadesControladorEnum.EMPLEADO.getValue()
                                    .toLowerCase(), idDeEmpleado);

                    reemplazar.put("proceso", procesoNomina);
                    reemplazar.put("ano", anioNomina);
                    reemplazar.put("mes", mesNomina);
                    reemplazar.put("procesoNomina", procesoNomina);
                    reemplazar.put("anioNomina", anioNomina);
                    reemplazar.put("mesNomina", mesNomina);
                    Map<String, Object> parametros = new HashMap<>();
                    Map<String, Object> parametros2 = new HashMap<>();
                    String jefe = getParametro(
                                    "NOMBRE DEL JEFE DE RECURSOS HUMANOS", true,
                                    new Date());
                    String cargoJefe = getParametro("CARGO JEFE RECURSOS HUMANOS",
                                    true, new Date());
                    String elaborado = getParametro("ELABORADO POR", true,
                                    new Date());
                    String nombreEmpresa = SessionUtil.getCompaniaIngreso()
                                    .getNombre();

                    String encabezado = getParametro(
                                    "RUTA IMAGEN ENCABEZADO VOLANTE DE PAGOS", true,
                                    new Date());
                    
                    parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS", jefe);
                    parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefe);
                    parametros.put("PR_ELABORADO_POR", elaborado);
                    parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
                    parametros.put("PR_NIT", companiaNit);
                    parametros.put("PR_RUTA_IMAGEN_ENCABEZADO_VOLANTE_DE_PAGOS",
                                    encabezado);
                    parametros.put("PR_EXTRASEMESTRAL","805018833-8".equals(companiaNit));
                    
                    parametros2 = parametros;
                    String[] informe = new String[2];
                    informe[0] = strDocName;
                    informe[1] = memorando;
                    Reporteador.resuelveConsulta(informe[0],
                                    Integer.valueOf(SessionUtil.getModulo()),
                                    reemplazar,
                                    parametros);

                    ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

                    salidas[0] = JsfUtil.serializarReporte(informe[0], parametros,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.PDF);

                    Reporteador.resuelveConsulta(informe[1],
                                    Integer.valueOf(SessionUtil.getModulo()),
                                    reemplazar,
                                    parametros2);

                    salidas[1] = JsfUtil.serializarReporte(informe[1], parametros2,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.PDF);
                    String[] nombresArchivos = new String[2];

                    if (ReportesBean.FORMATOS.PDF
                                    .equals(ReportesBean.FORMATOS.PDF)) {
                        nombresArchivos[0] = strDocName+".pdf";
                        nombresArchivos[1] = memorando+".pdf";
                    }
                    else {
                        nombresArchivos[0] = strDocName+".xlsx";
                        nombresArchivos[1] = memorando+".xlsx";
                    }

                    archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                    salidas, nombresArchivos);
            }
            else if (strDocName.equals(strDocName)) {
                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(NovedadesControladorEnum.EMPLEADO.getValue()
                                .toLowerCase(), idDeEmpleado);
                reemplazar.put("procesoNomina", procesoNomina);
                reemplazar.put("anioNomina", anioNomina);
                reemplazar.put("mesNomina", mesNomina);
                Map<String, Object> parametros = new HashMap<>();
                Reporteador.resuelveConsulta(strDocName,
                                Integer.parseInt(moduloNomina), reemplazar,
                                parametros);
                String jefe = getParametro(
                                "NOMBRE DEL JEFE DE RECURSOS HUMANOS", true,
                                new Date());
                String cargoJefe = getParametro("CARGO JEFE RECURSOS HUMANOS",
                                true, new Date());
                String elaborado = getParametro("ELABORADO POR", true,
                                new Date());
                String nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania,
                                0);
                parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS", jefe);
                parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefe);
                parametros.put("PR_ELABORADO_POR", elaborado);
                parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
                parametros.put("PR_EXTRASEMESTRAL","805018833-8".equals(companiaNit));

                archivoDescarga = JsfUtil.exportarStreamed(strDocName,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.PDF);
            }
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1766"));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | DRException | SQLException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirCESANTIAS() {
        archivoDescarga = null;
        String parcialCesantias = null;
        try {
            Date fechaParametros = SysmanFunciones.ultimoDiaDate(SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("01/",mes, "/", ano)));
            parcialCesantias = getParametro("FORMATO FACTORES CESANTIAS PARCIALES NOV", false,fechaParametros);

            //String pagoInteresParcial = getParametro("PAGAR INTERESES PARCIALES EN NOMINA MENSUAL", true,fechaParametros);

            HashMap<String, Object> reemplaza = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            //reemplaza.put("pagoInteresParcial", pagoInteresParcial);
            reemplaza.put("compania", compania);						//compania
            reemplaza.put("IdEmpleado", idDeEmpleado);					//id_empleado
            reemplaza.put("ano",ano);									//ano
            reemplaza.put("mes",mes);									//mes
            reemplaza.put("periodo",periodo);							//periodo
            reemplaza.put("proceso",Integer.parseInt(procesoNomina));	//proceso
            //reemplaza.put("IdConcepto", "411");																	//id_proceso
            Reporteador.resuelveConsulta(parcialCesantias,Integer.parseInt(moduloNomina), reemplaza,parametros);
            
            String cargoRH = getParametro("CARGO JEFE RECURSOS HUMANOS", true,fechaParametros);
            String jefeRH = getParametro("NOMBRE JEFE RECURSOS HUMANOS", true,fechaParametros);
            String cargoGerente = getParametro("CARGO DEL GERENTE", true,fechaParametros);
            String nombreGerente = getParametro("NOMBRE DEL GERENTE", true,fechaParametros);
            String elaborado = getParametro("ELABORADO POR", true, new Date());
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoRH);						//cargoRH
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", jefeRH);						//jefeRH
            parametros.put("PR_ELABORADO_POR",elaborado);			//user
            parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);							//cargoGerente
            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);							//cargoGerente
            // si el siguiente parametro esta en SI, se debe mosrtar los intereses de las cesantias
            String parametroCesanAuto = SysmanFunciones.nvlStr(getParametro(
                    "MIGRAR A CESANTIAS AUTO", false,
                    new Date()), "NO");
            if(parametroCesanAuto.equals("SI")) {
            	parametros.put("PR_INTERESES_CESANTIAS_MOSTRAR", parametroCesanAuto);
            }
            archivoDescarga = JsfUtil.exportarStreamed(parcialCesantias,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_INFORME_NO_EXISTE
                                                            .getValue()),
                            " ", ex.getMessage(),
                            " ", parcialCesantias));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (ParseException | JRException | IOException ex) {
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPARAMETROSRETRO() {
        SessionUtil.cargarModal(
                        String.valueOf(GeneralCodigoFormaEnum.ACTUALIZAPARAMETROSRETROACTIVOS_CONTROLADOR
                                        .getCodigo()),
                        moduloNomina);
    }

    public void oprimirENVIARVOLANTE() {
        genVolante(ReportesBean.FORMATOS.PDF, true);
    }

    public void oprimirBtEncargos() {
        Map<String, Object> param = new HashMap<>();
        param.put(cIdEmpleado, idDeEmpleado);
        param.put(cNombreEmpleado, nombre);
        param.put(NovedadesControladorEnum.CEDULA.getValue().toLowerCase(),
                        cedula);
        param.put("vacaciones", vacaciones);
        param.put("rid", llave);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String
                        .valueOf(GeneralCodigoFormaEnum.ENCARGOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, moduloNomina);
    }

    public void oprimirBtInteVacaciones() {

        Map<String, Object> param = new HashMap<>();
        param.put(cIdEmpleado, idDeEmpleado);
        param.put(cNombreEmpleado, nombre);
        param.put("vacaciones", vacaciones);
        param.put(NovedadesControladorEnum.CEDULA.getValue().toLowerCase(),
                        cedula);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.INTERRUPCIONVACACIONES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, moduloNomina);

    }

    public void oprimirBtQuinquenio() {

        String[] campos = { cIdEmpleado, cNombreEmpleado,
                            NovedadesControladorEnum.CEDULA.getValue()
                                            .toLowerCase() };
        String[] valores = { idDeEmpleado, nombre, cedula };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.QUINQUENIOS_CONTROLADOR
                                        .getCodigo()),
                        moduloNomina, campos,
                        valores);
    }

    public void oprimirBtLicencias() {

        Map<String, Object> param = new HashMap<>();
        param.put(cIdEmpleado, idDeEmpleado);
        param.put(cNombreEmpleado, nombre);
        param.put("ano", anioNomina);
        param.put("mes", mesNomina);
        param.put("vacaciones", vacaciones);
        param.put("cesantias", cesantias);
        param.put("periodo", periodoNomina);
        param.put(NovedadesControladorEnum.CEDULA.getValue().toLowerCase(),
                        cedula);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String
                        .valueOf(GeneralCodigoFormaEnum.LICENCIAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, moduloNomina);
    }

    public void oprimirBtIncapacidad() {

        Map<String, Object> param = new HashMap<>();
        param.put(cIdEmpleado, idDeEmpleado);
        param.put(cNombreEmpleado, nombre);
        param.put(NovedadesControladorEnum.CEDULA.getValue().toLowerCase(),
                        cedula);
        param.put("vacaciones", vacaciones);
        param.put("cesantias", cesantias);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.INCAPACIDADES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, moduloNomina);
    }

    public void oprimirBtVacaciones() {
        Map<String, Object> param = new HashMap<>();
        param.put(cIdEmpleado, idDeEmpleado);
        param.put(cNombreEmpleado, nombre);
        param.put(NovedadesControladorEnum.CEDULA.getValue().toLowerCase(),
                        cedula);
        param.put("vacaciones", vacaciones);
        param.put("cesantias", cesantias);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String
                        .valueOf(GeneralCodigoFormaEnum.VACACIONES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, moduloNomina);
    }

    public void seleccionarFilaiddeempleado(SelectEvent event) {
        try {
            Registro registroAux = (Registro) event.getObject();
            idDeEmpleado = validarCampoCadena(registroAux.getCampos(),
                            NovedadesControladorEnum.ID_DE_EMPLEADO.getValue());

            cedula = validarCampoCadena(registroAux.getCampos(),
                            NovedadesControladorEnum.NUMERO_DCTO.getValue());
            nombre = validarCampoCadena(registroAux.getCampos(),
                            GeneralParameterEnum.NOMBRE.getName());
            email = validarCampoCadena(registroAux.getCampos(),
                            NovedadesControladorEnum.EMAIL_PERSONAL.getValue());
            emailCorporativo = validarCampoCadena(registroAux.getCampos(),
                            NovedadesControladorEnum.EMAIL_CORPORATIVO
                                            .getValue());
            if (!SysmanFunciones.validarVariableVacio(idDeEmpleado)) {
                armaTitulo();
                vacaciones = false;
                cesantias = false;
                // Asignar variable de vacaciones para condicionar el
                // boton de vacaciones
                String fechanovedad = "01/"+Integer.parseInt(mesNomina)+"/"+Integer.parseInt(anioNomina);
                SimpleDateFormat formato =new SimpleDateFormat("dd/MM/yyyy");  
                Date fecha_novedad = formato.parse(fechanovedad);  
                
                 ejbNominaDos.getIncluirNovedadEncargos(
                		Integer.parseInt(mesNomina),
                		Integer.parseInt(periodoNomina),
                		Integer.parseInt(anioNomina),
                        Integer.parseInt(procesoNomina), compania, 
                        (Date) fecha_novedad,
                        (Date) fecha_novedad, 
                        SessionUtil.getUser().getCodigo(),Integer.parseInt(idDeEmpleado));

                double cnp174 = ejbNominaUno
                                .getAcumConceptoValor(compania, 174,
                                                Integer.parseInt(anioNomina),
                                                Integer.parseInt(mesNomina),
                                                Integer.parseInt(periodoNomina),
                                                Integer.parseInt(anioNomina),
                                                Integer.parseInt(mesNomina),
                                                Integer.parseInt(periodoNomina),
                                                Integer.parseInt(idDeEmpleado))
                                .doubleValue();

                double cnp175 = ejbNominaUno
                                .getAcumConceptoValor(compania, 175,
                                                Integer.parseInt(anioNomina),
                                                Integer.parseInt(mesNomina),
                                                Integer.parseInt(periodoNomina),
                                                Integer.parseInt(anioNomina),
                                                Integer.parseInt(mesNomina),
                                                Integer.parseInt(periodoNomina),
                                                Integer.parseInt(idDeEmpleado))
                                .doubleValue();

                double nov403 = ejbNominaUno
                                .getAcumNovedadConcValor(compania, 403,
                                                Integer.parseInt(anioNomina),
                                                Integer.parseInt(mesNomina),
                                                Integer.parseInt(periodoNomina),
                                                Integer.parseInt(anioNomina),
                                                Integer.parseInt(mesNomina),
                                                Integer.parseInt(periodoNomina),
                                                Integer.parseInt(idDeEmpleado))
                                .doubleValue();

                if ((cnp174 + cnp175 + nov403) > 0) {
                    vacaciones = true;
                }
                // Asignar variable de cesantias para condicionar el
                // boton
                // de cesantias
                double cnp411 = ejbNominaUno
                        .getAcumNovedadConcValor(compania, 411,
                                        Integer.parseInt(anioNomina),
                                        Integer.parseInt(mesNomina),
                                        Integer.parseInt(periodoNomina),
                                        Integer.parseInt(anioNomina),
                                        Integer.parseInt(mesNomina),
                                        Integer.parseInt(periodoNomina),
                                        Integer.parseInt(idDeEmpleado))
                        .doubleValue();

                if (cnp411 > 0) {
                    cesantias = true;
                }

            }
        }
        catch (NumberFormatException | SystemException | ParseException ex) {
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        reasignarOrigen();
        cargarForma();
    }

    public void seleccionarFilaIDdeConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        NovedadesControladorEnum.ID_DE_CONCEPTO.getValue(),
                        registroAux.getCampos().get(
                                        NovedadesControladorEnum.ID_DE_CONCEPTO
                                                        .getValue()));
        registro.getCampos().put(
                        NovedadesControladorEnum.NOMBRE_CONCEPTO.getValue(),
                        registroAux.getCampos().get(
                                        NovedadesControladorEnum.NOMBRE_CONCEPTO
                                                        .getValue()));
    }

    public void seleccionarFilaIDdeConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(
                        NovedadesControladorEnum.ID_DE_CONCEPTO.getValue());
        registro.getCampos().put(
                        NovedadesControladorEnum.NOMBRE_CONCEPTO.getValue(),
                        registroAux.getCampos().get(
                                        NovedadesControladorEnum.NOMBRE_CONCEPTO
                                                        .getValue()));
    }

    @Override
    public void abrirFormulario() {

        try {
            if (!activo) {
                SessionUtil.agregarMensajeErrorMenu(
                                idioma.getString("TB_TB2550"));

                SessionUtil.redireccionarMenu();

                return;
            }
            // Para condicionar el boton de parametros de
            // retroactivo
            retro = false;
            double valor306 = ejbNominaDos
                            .getValorConceptoNovedad(compania,
                                            Integer.parseInt(anioNomina),
                                            Integer.parseInt(periodoNomina),
                                            Integer.parseInt(mesNomina), 306)
                            .doubleValue();

            if ("5".equals(periodoNomina) || (valor306 > 0)) {
                retro = true;
            }
            tituloForm = SysmanFunciones.concatenar(companiaNombre,
                            idioma.getString("TB_TB3696"));
            tipo = "4";
            cambiarTipo();
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        if (SysmanFunciones.validarVariableVacio(idDeEmpleado)) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            NovedadesControladorEnum.TI_MS_ERROR_VALIDACION
                                            .getValue()));
            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        NovedadesControladorEnum.ID_DE_PROCESO.getValue(),
                        idProceso);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), mes);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        registro.getCampos().put(
                        NovedadesControladorEnum.ID_DE_EMPLEADO.getValue(),
                        idDeEmpleado);
        registro.getCampos().remove(
                        NovedadesControladorEnum.NOMBRE_CONCEPTO.getValue());
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        new Date());
        return true;
    }

    @Override
    public boolean insertarDespues() {

        return true;
    }

    @Override
    public boolean actualizarAntes() {
        if (SysmanFunciones.validarVariableVacio(idDeEmpleado)) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            NovedadesControladorEnum.TI_MS_ERROR_VALIDACION
                                            .getValue()));
            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        NovedadesControladorEnum.ID_DE_PROCESO.getValue(),
                        idProceso);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), mes);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        registro.getCampos().remove(
                        NovedadesControladorEnum.NOMBRE_CONCEPTO.getValue());
        registro.getCampos().remove(
                        NovedadesControladorEnum.OBSERVACIONES.getValue());

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        if (SysmanFunciones.validarVariableVacio(idDeEmpleado)) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            NovedadesControladorEnum.TI_MS_ERROR_VALIDACION
                                            .getValue()));
            return false;
        }
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    private void armaTitulo() {
        String nMes;
        String nAno;
        String nPer;
        String nCedula;
        if ("0".equals(mes) || mes.isEmpty()) {
            nMes = idioma.getString("TB_TB3687");
        }
        else {
            nMes = idioma.getString("TB_TB3688").replace("s$nombreMes$s",
                            nombreMes);
        }
        if ("0".equals(ano)) {
            nAno = idioma.getString("TB_TB3689");
        }
        else {
            nAno = SysmanFunciones.concatenar(" ", idioma.getString("TB_TB3695")
                            .replace("s$ano$s", ano));
        }
        if ("0".equals(periodo)) {
            nPer = idioma.getString("TB_TB3690");
        }
        else {
            nPer = SysmanFunciones.concatenar(" ",
                            idioma.getString("TB_TB3691").replace(
                                            "s$nombrePeriodo$s", nombrePeriodo),
                            " ",
                            (String) SessionUtil.getSessionVar(
                                            "nombreProcesoNomina"));
        }
        if (cedula.isEmpty()) {
            nCedula = idioma.getString("TB_TB3692");
        }
        else {
            nCedula = idioma.getString("TB_TB3693").replace("s$cedula$s",
                            nombre);
        }
        titulo = idioma.getString("TB_TB3694")
                        .replace("s$mesAnoPer$s",
                                        SysmanFunciones.concatenar(nMes, nAno,
                                                        nPer))
                        .replace("s$cedula$s", nCedula);
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @throws NamingException
     */
    public void ejecutarrcCerrar() throws NamingException {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionarMenu();
        vacaciones = false;
        //cesantias = false;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // heradado del bean base
    }

    @Override
    public void removerCombos() {
        // heradado del bean base
    }

    public void retornarFormularioBtVacaciones(SelectEvent event) {
        // heredado del bean base
    }

    public void retornarFormularioBtIncapacidad(SelectEvent event) {
        // heredado del bean base
    }

    public void retornarFormularioBtLicencias(SelectEvent event) {
        // heredado del bean base
    }

    public void retornarFormularioBtQuinquenio(SelectEvent event) {
        // heredado del bean base
    }

    public void retornarFormularioBtInteVacaciones(SelectEvent event) {
        // heredado del bean base
    }

    public void retornarFormularioBtEncargos(SelectEvent event) {
        // heredado del bean base
    }

    /**
     * metodo get y set
     *
     * @return
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getIdDeEmpleado() {
        return idDeEmpleado;
    }

    public void setIdDeEmpleado(String iddeempleado) {
        this.idDeEmpleado = iddeempleado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDesProceso() {
        return desProceso;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isRetro() {
        return retro;
    }

    public void setRetro(boolean retro) {
        this.retro = retro;
    }

    public String getTituloForm() {
        return tituloForm;
    }

    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

    public boolean isVacaciones() {
        return vacaciones;
    }

    public void setVacaciones(boolean vacaciones) {
        this.vacaciones = vacaciones;
    }

    public boolean isCesantias() {
        return cesantias;
    }

    public void setCesantias(boolean cesantias) {
        this.cesantias = cesantias;
    }

    public String getDgAno() {
        return dgAno;
    }

    public void setDgAno(String dgAno) {
        this.dgAno = dgAno;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean getActivo() {
        return activo;
    }

    public List<Registro> getListaDgAno() {
        return listaDgAno;
    }

    public void setListaDgAno(List<Registro> listaDgAno) {
        this.listaDgAno = listaDgAno;
    }

    public RegistroDataModelImpl getListaiddeempleado() {
        return listaiddeempleado;
    }

    public void setListaiddeempleado(RegistroDataModelImpl listaiddeempleado) {
        this.listaiddeempleado = listaiddeempleado;
    }

    public RegistroDataModelImpl getListaiddeempleadoE() {
        return listaiddeempleadoE;
    }

    public void setListaiddeempleadoE(
        RegistroDataModelImpl listaiddeempleadoE) {
        this.listaiddeempleadoE = listaiddeempleadoE;
    }

    public RegistroDataModelImpl getListaIDdeConcepto() {
        return listaIDdeConcepto;
    }

    public void setListaIDdeConcepto(RegistroDataModelImpl listaIDdeConcepto) {
        this.listaIDdeConcepto = listaIDdeConcepto;
    }

    public RegistroDataModelImpl getListaIDdeConceptoE() {
        return listaIDdeConceptoE;
    }

    public void setListaIDdeConceptoE(
        RegistroDataModelImpl listaIDdeConceptoE) {
        this.listaIDdeConceptoE = listaIDdeConceptoE;
    }

    public RegistroDataModel getListaIDdeConceptoA() {
        return listaIDdeConceptoA;
    }

    public void setListaIDdeConceptoA(RegistroDataModel listaIDdeConceptoA) {
        this.listaIDdeConceptoA = listaIDdeConceptoA;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isVisibleNovedades() {
        return visibleNovedades;
    }

    public void setVisibleNovedades(boolean visibleNovedades) {
        this.visibleNovedades = visibleNovedades;
    }
}
