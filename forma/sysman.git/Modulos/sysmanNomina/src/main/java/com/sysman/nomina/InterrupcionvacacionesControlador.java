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
import com.sysman.nomina.enums.InterrupcionvacacionesControladorEnum;
import com.sysman.nomina.enums.InterrupcionvacacionesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jgomez
 * @version 1, 23/11/2015
 *
 * -- Modificado por lcortes 17/03/2017 15:32. --> Ajustes de buenas
 * practicas SonarLint.
 * @modified jguerrero
 * @version 2. 10/10/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además, se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class InterrupcionvacacionesControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    /**
     * Constante a nivel de clase que identifica el campo
     * DIASINTERRUPCION.
     */
    private final String campoDiasInterrupcion;
    /**
     * Constante a nivel de clase que identifica el campo FECHAFINAL.
     */
    private final String campoFechaFinal;
    /**
     * Constante a nivel de clase que identifica el campo
     * FECHAINTERRUPCION.
     */
    private final String campoFechaInterrupcion;
    /**
     * Constante a nivel de clase que identifica el campo
     * FECHAPAGOINTERRUPCION.
     */
    private final String campoFechaPagoInterr;
    /**
     * Constante a nivel de clase que identifica el campo FECHA_FINAL.
     */
    private final String campoFechaFin;
    /**
     * Constante a nivel de clase que identifica el campo
     * FINAL_DISFRUTE.
     */
    private final String campoFinalDisfrute;
    /**
     * Constante a nivel de clase que identifica el campo
     * ID_DE_EMPLEADO.
     */
    private final String campoIdEmpleado;
    /**
     * Constante a nivel de clase que identifica el campo
     * INICIO_DISFRUTE.
     */
    private final String campoIniDisfrute;
    /**
     * Constante a nivel de clase que identifica el campo
     * INTERRUPCION_ENDINERO.
     */
    private final String campoInteEnDinero;
    /**
     * Constante a nivel de clase que identifica el campo PERIODO.
     */
    private final String campoPeriodo;

    private boolean vacaciones;
    private boolean cesantias;

    /**
     * Constante a nivel de clase que identifica el mensaje
     * MSM_TRANS_INTERRUMPIDA.
     */

    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaDosRemote ejbNominaDos;

    @EJB
    private EjbNominaUnoRemote ejbNominaUno;

    private String periodoNomina;
    private String procesoNomina;
    private String mesNomina;
    private String anioNomina;
    private RegistroDataModelImpl listaFechaPago;
    private String idEmpleado;
    private String nombreEmpleado;
    private String cedula;
    private String tituloForm;
    private Date fechaPago;

    public InterrupcionvacacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        campoDiasInterrupcion = "DIASINTERRUPCION";
        campoFechaFinal = "FECHAFINAL";
        campoFechaInterrupcion = "FECHAINTERRUPCION";
        campoFechaPagoInterr = "FECHAPAGOINTERRUPCION";
        campoFechaFin = "FECHA_FINAL";
        campoFinalDisfrute = "FINAL_DISFRUTE";
        campoIdEmpleado = InterrupcionvacacionesControladorEnum.ID_DE_EMPLEADO
                        .getValue();
        campoIniDisfrute = "INICIO_DISFRUTE";
        campoInteEnDinero = "INTERRUPCION_ENDINERO";
        campoPeriodo = GeneralParameterEnum.PERIODO.getName();

        try {

            Map<String, Object> parametros = new HashMap<>();
            parametros = SessionUtil.getFlash();

            periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
            procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
            mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
            anioNomina = (String) SessionUtil.getSessionVar("anioNomina");

            idEmpleado = (String) parametros.get("idEmpleado");
            nombreEmpleado = (String) parametros.get("nombreEmpleado");
            cedula = (String) parametros.get("cedula");
            vacaciones = (boolean) SysmanFunciones
                            .nvl(parametros.get("vacaciones"), false);
            cesantias = (boolean) SysmanFunciones
                            .nvl(parametros.get("cesantias"), false);
            numFormulario = GeneralCodigoFormaEnum.INTERRUPCIONVACACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InterrupcionvacacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaFechaPago();
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.INTERRUPCION_VACACIONES;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(InterrupcionvacacionesControladorEnum.ID_EMPLEADO
                        .getValue(), idEmpleado);

    }

    public void cargarListaFechaPago() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InterrupcionvacacionesControladorUrlEnum.URL15494
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InterrupcionvacacionesControladorEnum.PROCESO.getValue(),
                        procesoNomina);

        listaFechaPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoFechaFinal);

        // 471036 PROCESO

    }

    public RegistroDataModelImpl getListaFechaPago() {
        return listaFechaPago;
    }

    public void setListaFechaPago(RegistroDataModelImpl listaFechaPago) {
        this.listaFechaPago = listaFechaPago;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTituloForm() {
        return tituloForm;
    }

    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

    public void cambiarFechaInicio() {

        try {

            String fechaInicio = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos().get("FECHA_INICIO"));

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.FECHA.getName(), fechaInicio);

            param.put(InterrupcionvacacionesControladorEnum.ID_EMPLEADO
                            .getValue(), idEmpleado);

            Registro total = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InterrupcionvacacionesControladorUrlEnum.URL28529
                                                                            .getValue())
                                            .getUrl(), param));

            if (total != null) {
                registro.getCampos().put(campoFechaFin,
                                total.getCampos().get(campoFechaFin));
                registro.getCampos().put(campoIniDisfrute,
                                total.getCampos().get(campoIniDisfrute));
                registro.getCampos().put(campoFinalDisfrute,
                                total.getCampos().get(campoFinalDisfrute));
                fechaPago = ejbNominaCero.getFechaPeriodoIniFin(compania,
                                Integer.parseInt(procesoNomina),
                                Integer.parseInt(anioNomina),
                                Integer.parseInt(mesNomina),
                                Integer.parseInt(periodoNomina), false, true);

                Date fechaAux = SysmanFunciones.convertirAFecha(SysmanFunciones
                                .convertirAFechaCadena(fechaPago));

                registro.getCampos().put(campoFechaPagoInterr, fechaAux);
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                anioNomina);
                registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                                mesNomina);
                registro.getCampos().put(campoPeriodo, periodoNomina);
                registro.getCampos().put(campoInteEnDinero, false);
            }
            else {

                registro.getCampos().put("FECHA_INICIO", null);
                registro.getCampos().put(campoFechaFin, null);
                registro.getCampos().put(campoIniDisfrute, null);
                registro.getCampos().put(campoFinalDisfrute, null);
                fechaPago = null;
                registro.getCampos().put(campoFechaPagoInterr, fechaPago);
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                null);
                registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                                null);
                registro.getCampos().put(campoPeriodo, null);
                registro.getCampos().put(campoInteEnDinero, false);

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4111"));
            }
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarDiasInterrupcion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarFechasInterrupcion() {
        boolean rta = true;

        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        campoFechaInterrupcion)
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            campoIniDisfrute)
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            campoFinalDisfrute)) {

            Date fechaIterrupcion = (Date) registro.getCampos()
                            .get(campoFechaInterrupcion);
            Date fechaIniDisfrute = (Date) registro.getCampos()
                            .get(campoIniDisfrute);

            Date fechaFinalDisfrute = (Date) registro.getCampos()
                            .get(campoFinalDisfrute);

            if (fechaIterrupcion.before(fechaIniDisfrute)
                || (fechaIterrupcion.after(fechaFinalDisfrute))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2604"));
                registro.getCampos().put(campoFechaInterrupcion, null);
                registro.getCampos().put(campoDiasInterrupcion, 0);
                rta = false;

            }
        }

        return rta;

    }

    public void cambiarFechaInterrupcion() {
        try {
            if (!validarFechasInterrupcion()) {
                return;
            }

            String fecha = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos()
                                            .get(campoFechaInterrupcion));

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.FECHA.getName(), fecha);
            param.put(InterrupcionvacacionesControladorEnum.PROCESO.getValue(),
                            procesoNomina);

            Registro regPeriodo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InterrupcionvacacionesControladorUrlEnum.URL17692
                                                                            .getValue())
                                            .getUrl(), param));

            if ((regPeriodo != null) && (fecha == null)) {
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                regPeriodo.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName()));
                registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                                regPeriodo.getCampos()
                                                .get(GeneralParameterEnum.MES
                                                                .getName()));

                registro.getCampos().put(campoPeriodo,
                                regPeriodo.getCampos().get(campoPeriodo));
                fechaPago = (Date) regPeriodo.getCampos()
                                .get(campoFechaFinal);

                registro.getCampos().put(campoFechaPagoInterr,
                                SysmanFunciones.convertirAFechaCadena(
                                                (Date) regPeriodo.getCampos()
                                                                .get(campoFechaFinal)));
            }
            String sabadoHabil = ejbSysmanUtil.consultarParametro(compania,
                            "CONTAR SABADOS COMO DIA HABIL",
                            SessionUtil.getModulo(),
                            new Date(), true);

            sabadoHabil = sabadoHabil.toUpperCase();
            boolean vaSabado = ("SI").equals(sabadoHabil);

            if ((registro.getCampos().get(campoFechaInterrupcion) != null)
                && (registro.getCampos().get(campoFinalDisfrute) != null)) {
                int diasHabiles;

                diasHabiles = ejbSysmanUtil.retornarDiasHabilesEntreFechas(
                                compania,
                                (Date) registro.getCampos()
                                                .get(campoFechaInterrupcion),
                                (Date) registro.getCampos()
                                                .get(campoFinalDisfrute),
                                vaSabado);

                registro.getCampos().put(campoDiasInterrupcion,
                                diasHabiles);
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3714"));
            }
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaFechaPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(campoFechaPagoInterr,
                        registroAux.getCampos().get(campoFechaFinal));

        if (!SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        campoFechaFinal)) {

            fechaPago = (Date) registroAux.getCampos()
                            .get(campoFechaFinal);
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(fechaPago));
            registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                            SysmanFunciones.mes(fechaPago));
            registro.getCampos().put(campoPeriodo,
                            registroAux.getCampos().get(campoPeriodo));
        }

    }

    @Override
    public void abrirFormulario() {
        tituloForm = nombreEmpleado;
    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();
        if (("i").equals(accion)) {
            registro.getCampos().put("ID_DE_PROCESO", procesoNomina);
            registro.getCampos().put(campoIdEmpleado, idEmpleado);
        }
    }

    @Override
    public boolean insertarAntes() {

        registro.getCampos().put("ID_DE_CONCEPTO", 403);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        actualizarFechaFinalPersonal();
        accion = "v";
        return true;
    }

    @Override
    public boolean actualizarAntes() {

        if (registro.getCampos().get(campoFechaPagoInterr) == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2600"));
            return false;
        }

        return true;
    }

    private boolean diferir(String opcion) {

        boolean endinero = (Boolean) SysmanFunciones.nvl(registro.getCampos()
                        .get(campoInteEnDinero), false);

        try {

            ejbNominaDos.getDiferirIntVac(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(idEmpleado), opcion,
                            (Date) registro
                                            .getCampos()
                                            .get(campoFechaPagoInterr),
                            endinero, (Date) registro
                                            .getCampos()
                                            .get(campoFechaInterrupcion),

                            (Date) registro
                                            .getCampos()
                                            .get(campoFinalDisfrute),
                            Integer.parseInt(registro.getCampos()
                                            .get(campoDiasInterrupcion)
                                            .toString()),
                            SessionUtil.getUser().getCodigo());

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return diferir(null);
    }

    @Override
    public boolean eliminarAntes() {
        boolean bolRta = false;

        boolean activo;
        try {
            activo = ejbNominaCero.validarPeriodoActivoNomina(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(registro.getCampos().get(
                                            GeneralParameterEnum.ANO.getName())
                                            .toString()),
                            Integer.parseInt(registro.getCampos().get(
                                            GeneralParameterEnum.MES.getName())
                                            .toString()),
                            Integer.parseInt(registro.getCampos().get(
                                            GeneralParameterEnum.ANO.getName())
                                            .toString()));

            if (activo) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2553"));
                return bolRta;
            }

            actualizarFechaFinalPersonal();

            bolRta = diferir("'BORRAR'");
            return bolRta;

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return bolRta;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public void ejecutarrcCerrar() {
        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put("idEmpleado", idEmpleado);
        parametros.put("cedula", cedula);
        parametros.put("nombreEmpleado", nombreEmpleado);
        parametros.put("vacaciones", vacaciones);
        parametros.put("cesantias", cesantias);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.NOVEDADES_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    private void actualizarFechaFinalPersonal() {
        try {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            InterrupcionvacacionesControladorUrlEnum.URL17693
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();

            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(InterrupcionvacacionesControladorEnum.ID_EMPLEADO
                            .getValue(),
                            registro.getCampos().get(campoIdEmpleado));
            fields.put(GeneralParameterEnum.FECHA.getName(), registro
                            .getCampos()
                            .get(campoFechaInterrupcion));

            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

}
