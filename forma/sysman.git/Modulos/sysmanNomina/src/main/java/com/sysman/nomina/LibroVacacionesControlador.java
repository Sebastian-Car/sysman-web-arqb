package com.sysman.nomina;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.enums.LibroVacacionesControladorEnum;
import com.sysman.nomina.enums.LibroVacacionesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
import com.sysman.nomina.ejb.EjbNominaDosRemote;

/**
 *
 * @author jacelas
 * @version 1, 29/07/2015
 * @modified spina 23/03/2017 Depuracion sonar - se organizaron los
 * metodos que se encontraban antes del constructor, se definieron
 * constantes para las cadenas que se repetian mas de 3 veces, se
 * eliminaron parametros innecesarios, se elimino el metodo
 * "sqlArmado", "sqlLibroVacacionesTotal",
 * "sqlLibroVacacionesSabanaCesantias",
 * "sqlLibroVacacionesPendientesPorFecha",
 * "sqlLibroVacacionesPagoCatorcena", "sqlLibroVacacionesPendientes"
 * ya que las consultas se encuentran en la bd
 * 
 * 
 * @author eamaya
 * @version 2.0, 09/10/2017, Proceso de Refactoring DSS, Manejo de
 * EJBs y cambio de numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class LibroVacacionesControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String procesoSesion;
    private final String anioSesion;
    private final String mesSesion;
    private final String periodoSesion;
    private String anoFormulario;
    private String mesFormulario;
    private String periodoFormulario;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private String opcion;
    private Date fechaFormulario;
    private static final String FORMATOFECHA = "dd-M-yyyy";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbNominaCeroRemote ejbNominaCero;
    
    @EJB
    private EjbNominaDosRemote ejbNominaDos;
    /**
     * Creates a new instance of LibroVacacionesControlador
     */
    public LibroVacacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        procesoSesion = (String) SessionUtil.getSessionVar("procesoNomina");
        anioSesion = (String) SessionUtil.getSessionVar("anioNomina");
        mesSesion = (String) SessionUtil.getSessionVar("mesNomina");
        periodoSesion = (String) SessionUtil.getSessionVar("periodoNomina");
        try {
            numFormulario = GeneralCodigoFormaEnum.LIBRO_VACACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LibroVacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        cargarListaAno1();
        anoFormulario = anioSesion;
        cargarListaMes1();
        mesFormulario = mesSesion;
        try {
            cargarListaPeriodo1();
        }
        catch (ParseException ex) {
            Logger.getLogger(LibroVacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        periodoFormulario = periodoSesion;

        // Asigna valor por defecto al opcion
        opcion = "8";
        
        abrirFormulario();
    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroVacacionesControladorUrlEnum.URL4426
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroVacacionesControladorUrlEnum.URL5195
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodo1() throws ParseException {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoFormulario);
        param.put(GeneralParameterEnum.MES.getName(),
                        mesFormulario);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(LibroVacacionesControladorEnum.PROCESO.getValue(),
                        "1");

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroVacacionesControladorUrlEnum.URL6125
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        SimpleDateFormat sdf = new SimpleDateFormat(FORMATOFECHA);
        String fecha = "1-" + mesFormulario + "-" + anoFormulario;

        fechaFormulario = SysmanFunciones.ultimoDiaDate(sdf.parse(fecha));
    }

    public void oprimirComando13() {
    	verificarFechaPago();
        seleccionarReporte(FORMATOS.EXCEL97);

    }

    public void oprimirPreliminarBancos() {
        // <CODIGO_DESARROLLADO>
    	verificarFechaPago();
        seleccionarReporte(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    private void seleccionarReporte(FORMATOS formato) {
        try {
            String nombreReporte;

            if ("1".equals(opcion)) {
                nombreReporte = "000135LibroVacacionesCHI";
            }
            else if ("2".equals(opcion)) {
                nombreReporte = "000139LibroVacacionesPendientes";
            }
            else if ("4".equals(opcion)) {
                nombreReporte = "001638SabanaVacaciones";
            }
            else if ("5".equals(opcion)) {
                nombreReporte = "000144VacacionesTotal";
            }
            else if ("6".equals(opcion)) {
                nombreReporte = "000150LIBROVACACIONESPENDIENTESPORFECHAS";
            }
            else if ("7".equals(opcion)) {
                nombreReporte = "000157SabanaCesantiasParciales";
            }
            else if ("8".equals(opcion)) {

                nombreReporte = SysmanFunciones.nvlStr(
                                ejbSysmanUtl.consultarParametro(compania,
                                                "FORMATO LIBRO VACACIONES",
                                                modulo, new Date(), false),
                                "null");

            }
            else {
                return;
            }
            genInforme(formato, nombreReporte);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    private void verificarFechaPago() {
    	
    	try {
			ejbNominaDos.getActualizarVacaPeriodo(compania,
			        Integer.parseInt(procesoSesion), Integer.parseInt(anoFormulario),
			        Integer.parseInt(mesFormulario), Integer.parseInt(periodoFormulario),
			        Integer.parseInt("0"),
			        SessionUtil.getUser().getCodigo());
		} catch (NumberFormatException | SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    private void genInforme(FORMATOS formato, String nombreReporte) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        SimpleDateFormat sdf = new SimpleDateFormat(FORMATOFECHA);
        String fecha = "1-" + mesFormulario + "-" + anoFormulario;

        try {
            String fechaPeriodo = SysmanFunciones.ultimoDiaInt(sdf.parse(fecha))
                + "/" + mesFormulario + "/" + anoFormulario;
            String fechaDelPeriodo = "'"
                + SysmanFunciones.convertirAFechaCadena(fechaFormulario)
                + "'";
            String nombreCompania = ejbNominaCero.getDatoEmpresa(compania, 0);

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("fecha", fechaPeriodo);
            reemplazar.put("fecha2", fechaDelPeriodo);
            reemplazar.put("procesoActual", procesoSesion);
            reemplazar.put("ano1", anoFormulario);
            reemplazar.put("mes1", mesFormulario);
            reemplazar.put("periodo1", periodoFormulario);
            reemplazar.put("fechaPeriodo", fechaPeriodo);
            reemplazar.put("fechaRef", fechaPeriodo);
            reemplazar.put("anoFormulario", anoFormulario);
            reemplazar.put("mesFormulario", mesFormulario);
            reemplazar.put("modulo", modulo);
            reemplazar.put("procesoSesion", procesoSesion);

            parametros.put("PR_NOMBREEMPRESA", nombreCompania);
            parametros.put("PR_EQUIPO", "#EQUIPO#");

            String nombreGerente = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE DEL GERENTE", modulo, new Date(), false);

            String cargoGerente = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO DEL GERENTE", modulo, new Date(), false);

            String nomCargoTesoPago = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE DEL CARGO TESORERO PAGADOR",
                            modulo, new Date(), false);

            String prCargoDelTesoreroPagador = ejbSysmanUtl.consultarParametro(
                            compania,
                            "CARGO DEL TESORERO PAGADOR", modulo, new Date(),
                            false);

            String prNombreDeQuienRevisaNomina = ejbSysmanUtl
                            .consultarParametro(compania,
                                            "NOMBRE DE QUIEN REVISA NOMINA",
                                            modulo, new Date(), false);

            String prCargoDeQuienRevisaNomina = ejbSysmanUtl.consultarParametro(
                            compania, "CARGO DE QUIEN REVISA NOMINA", modulo,
                            new Date(), false);

            String prNombreDelJefeDeRecursosHumanos = ejbSysmanUtl
                            .consultarParametro(compania,
                                            "NOMBRE DEL JEFE DE RECURSOS HUMANOS",
                                            modulo, new Date(), false);

            String cargoJefeRH = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS",
                            modulo, new Date(), false);

            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
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
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRH);

            Reporteador.resuelveConsulta(nombreReporte, Integer.valueOf(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (ParseException | OutOfMemoryError | JRException
                        | IOException | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() throws ParseException {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo1();

        // </CODIGO_DESARROLLADO>
    }

    public String getAnoFormulario() {
        return anoFormulario;
    }

    public void setAnoFormulario(String anoFormulario) {
        this.anoFormulario = anoFormulario;
    }

    public String getMesFormulario() {
        return mesFormulario;
    }

    public void setMesFormulario(String mesFormulario) {
        this.mesFormulario = mesFormulario;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR93-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name Me!fecha =
         * FechaPeriodo(GetProceso(), Forms!libro_vacaciones!Ano1,
         * Forms!libro_vacaciones!Mes1,
         * Forms!libro_vacaciones!Periodo1) Me.fecha.Requery End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public Date getFechaFormulario() {
        return fechaFormulario;
    }

    public void setFechaFormulario(Date fechaFormulario) {
        this.fechaFormulario = fechaFormulario;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getPeriodoFormulario() {
        return periodoFormulario;
    }

    public void setPeriodoFormulario(String periodoFormulario) {
        this.periodoFormulario = periodoFormulario;
    }
}
