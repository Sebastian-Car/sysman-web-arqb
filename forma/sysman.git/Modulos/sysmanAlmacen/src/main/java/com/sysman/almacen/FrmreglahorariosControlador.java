package com.sysman.almacen;

import com.sysman.almacen.enums.FrmreglahorariosControladorEnum;
import com.sysman.almacen.enums.FrmreglahorariosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author vmolano
 * @version 1, 11/02/2016
 * 
 * @author eamaya
 * @version 2, 2/04/2017 Proceso de Refactoring, Manejo de EJBs y
 * Correcciones SonarLint
 * 
 */
@ManagedBean
@ViewScoped
public class FrmreglahorariosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String str140px;
    private final String strExpr1;
    private final String strFecha;
    private final String strLimpiar;

    private String parteSeleccionada;
    private String devolutivoSeleccionado;
    private int paraSeleccionado;
    private RegistroDataModelImpl listaParte;
    private RegistroDataModelImpl listaParteE;
    private RegistroDataModelImpl listaDevolutivo;
    private RegistroDataModelImpl listaDevolutivoE;
    private String auxiliar;
    private List<Registro> listaHoraInicio;
    private List<Registro> listaHoraFinal;

    private String elementoDevolutivo;
    private int serieDevolutivo;
    private boolean verParte;
    private boolean verDevolutivo;
    private String elementoActual;
    private String serieActual;

    private String elemetoParteActual;
    private String serieParteActual;
    private boolean verNuevo;
    private String altoEncabezado;
    private boolean bloquearFechaFinal;
    private int diaNuevo;

    @EJB
    private EjbSysmanUtilRemote ejbConsecutivo;

    /**
     * Creates a new instance of FrmreglahorariosControlador
     */
    public FrmreglahorariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        str140px = "140px";
        strExpr1 = "EXPR1";
        strFecha = "FECHA_FINAL";
        strLimpiar = "Limpiar";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMREGLAHORARIOS_CONTROLADOR
                            .getCodigo();
            altoEncabezado = "45px";
            elementoDevolutivo = "-1";
            serieDevolutivo = -1;
            paraSeleccionado = 1;
            bloquearFechaFinal = false;
            verNuevo = true;
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(FrmreglahorariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.REGLA_HORARIO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaHoraInicio();
        cargarListaHoraFinal();
        cargarListaParte();
        cargarListaParteE();
        cargarListaDevolutivo();
        cargarListaDevolutivoE();
        abrirFormulario();
    }

    public boolean isBloquearFechaFinal() {
        return bloquearFechaFinal;
    }

    public void setBloquearFechaFinal(boolean bloquearFechaFinal) {
        this.bloquearFechaFinal = bloquearFechaFinal;
    }

    public String getAltoEncabezado() {
        return altoEncabezado;
    }

    public void setAltoEncabezado(String altoEncabezado) {
        this.altoEncabezado = altoEncabezado;
    }

    public boolean isVerNuevo() {
        return verNuevo;
    }

    public void setVerNuevo(boolean verNuevo) {
        this.verNuevo = verNuevo;
    }

    public String getElemetoParteActual() {
        return elemetoParteActual;
    }

    public void setElemetoParteActual(String elemetoParteActual) {
        this.elemetoParteActual = elemetoParteActual;
    }

    public String getSerieParteActual() {
        return serieParteActual;
    }

    public void setSerieParteActual(String serieParteActual) {
        this.serieParteActual = serieParteActual;
    }

    public String getElementoActual() {
        return elementoActual;
    }

    public void setElementoActual(String elementoActual) {
        this.elementoActual = elementoActual;
    }

    public String getSerieActual() {
        return serieActual;
    }

    public void setSerieActual(String serieActual) {
        this.serieActual = serieActual;
    }

    public boolean isVerParte() {
        return verParte;
    }

    public void setVerParte(boolean verParte) {
        this.verParte = verParte;
    }

    public boolean isVerDevolutivo() {
        return verDevolutivo;
    }

    public void setVerDevolutivo(boolean verDevolutivo) {
        this.verDevolutivo = verDevolutivo;
    }

    public String getElementoDevolutivo() {
        return elementoDevolutivo;
    }

    public void setElementoDevolutivo(String elementoDevolutivo) {
        this.elementoDevolutivo = elementoDevolutivo;
    }

    public int getSerieDevolutivo() {
        return serieDevolutivo;
    }

    public void setSerieDevolutivo(int serieDevolutivo) {
        this.serieDevolutivo = serieDevolutivo;
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public List<Registro> getListaHoraInicio() {
        return listaHoraInicio;
    }

    public void setListaHoraInicio(List<Registro> listaHoraInicio) {
        this.listaHoraInicio = listaHoraInicio;
    }

    public List<Registro> getListaHoraFinal() {
        return listaHoraFinal;
    }

    public void setListaHoraFinal(List<Registro> listaHoraFinal) {
        this.listaHoraFinal = listaHoraFinal;
    }

    public RegistroDataModelImpl getListaParte() {
        return listaParte;
    }

    public void setListaParte(RegistroDataModelImpl listaParte) {
        this.listaParte = listaParte;
    }

    public RegistroDataModelImpl getListaParteE() {
        return listaParteE;
    }

    public void setListaParteE(RegistroDataModelImpl listaParteE) {
        this.listaParteE = listaParteE;
    }

    public RegistroDataModelImpl getListaDevolutivo() {
        return listaDevolutivo;
    }

    public void setListaDevolutivo(RegistroDataModelImpl listaDevolutivo) {
        this.listaDevolutivo = listaDevolutivo;
    }

    public RegistroDataModelImpl getListaDevolutivoE() {
        return listaDevolutivoE;
    }

    public void setListaDevolutivoE(RegistroDataModelImpl listaDevolutivoE) {
        this.listaDevolutivoE = listaDevolutivoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getParteSeleccionada() {
        return parteSeleccionada;
    }

    public void setParteSeleccionada(String parteSeleccionada) {
        this.parteSeleccionada = parteSeleccionada;
    }

    public String getDevolutivoSeleccionado() {
        return devolutivoSeleccionado;
    }

    public void setDevolutivoSeleccionado(String devolutivoSeleccionado) {
        this.devolutivoSeleccionado = devolutivoSeleccionado;
    }

    public int getParaSeleccionado() {
        return paraSeleccionado;
    }

    public void setParaSeleccionado(int paraSeleccionado) {
        this.paraSeleccionado = paraSeleccionado;
    }

    public void cargarListaHoraInicio() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaHoraInicio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmreglahorariosControladorUrlEnum.URL8454
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaHoraFinal() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaHoraFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmreglahorariosControladorUrlEnum.URL9051
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaParte() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmreglahorariosControladorUrlEnum.URL4666
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmreglahorariosControladorEnum.PARAM0.getValue(),
                        elementoActual);
        param.put(FrmreglahorariosControladorEnum.PARAM1.getValue(),
                        serieActual);

        listaParte = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        strExpr1);
    }

    public void cargarListaParteE() {
        listaParteE = listaParte;

    }

    public void cargarListaDevolutivo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmreglahorariosControladorUrlEnum.URL6969
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDevolutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        strExpr1);
    }

    public void cargarListaDevolutivoE() {
        listaDevolutivoE = listaDevolutivo;

    }

    public void cambiarPARA() {
        verDevolutivo = false;
        verParte = false;
        verNuevo = false;

        if (paraSeleccionado == 1) {
            verNuevo = true;
            actualizarRegistros("Todos");
        }
        else if (paraSeleccionado == 2) {
            verDevolutivo = true;
            devolutivoSeleccionado = "";
            elementoActual = "";
            serieActual = "";
            altoEncabezado = str140px;
            actualizarRegistros(strLimpiar);
        }
        else {
            altoEncabezado = "45px";
            actualizarRegistros(strLimpiar);
        }
    }

    public void cambiarDIA() {
        diaNuevo = Integer.parseInt(registro.getCampos().get("DIA").toString());
        if (diaNuevo == 9) {
            bloquearFechaFinal = true;
            cambiarFechaInicio();
        }
        else {
            bloquearFechaFinal = false;
            registro.getCampos().put(strFecha, null);
        }

    }

    public void cambiarFechaInicio() {
        if (diaNuevo == 9) {
            Date fechaIni = (Date) registro.getCampos().get("FECHA_INICIO");
            registro.getCampos().put(strFecha, fechaIni);
        }
    }

    public void seleccionarFilaParte(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        parteSeleccionada = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DESCRIPCION"), " ")
                        .toString();

        if (SysmanFunciones.validarVariableVacio(parteSeleccionada)) {
            actualizarRegistros("Padre");
            elemetoParteActual = "";
            serieParteActual = "";
        }
        else {
            elemetoParteActual = SysmanFunciones.nvl(registroAux.getCampos()
                            .get("ELEMENTO"), " ").toString();

            serieParteActual = SysmanFunciones
                            .nvl(registroAux.getCampos().get("SERIE"), " ")
                            .toString();

            actualizarRegistros("Parte");
        }
    }

    public void onRowSelectParteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(strExpr1);
    }

    public void seleccionarFilaDevolutivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        devolutivoSeleccionado = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("DESCRIPCION"), " ").toString();

        verNuevo = false;
        verParte = false;

        if (SysmanFunciones.validarVariableVacio(devolutivoSeleccionado)) {
            elementoActual = "";
            serieActual = "";
            actualizarRegistros(strLimpiar);
            altoEncabezado = str140px;
        }
        else {
            verNuevo = true;
            elementoActual = SysmanFunciones
                            .nvl(registroAux.getCampos().get("ELEMENTO"), " ")
                            .toString();

            serieActual = SysmanFunciones
                            .nvl(registroAux.getCampos().get("SERIE"), " ")
                            .toString();

            int numHijos = Integer.parseInt(
                            registroAux.getCampos().get("HIJOS").toString());
            if (numHijos > 0) {
                parteSeleccionada = "";
                elemetoParteActual = "";
                serieParteActual = "";
                verParte = true;
                altoEncabezado = "212px";
            }
            else {
                altoEncabezado = str140px;
            }
            cargarListaParte();
            actualizarRegistros("Devolutivo");
        }
    }

    public void onRowSelectDevolutivoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strExpr1), " ")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(
                            FrmreglahorariosControladorEnum.PARAM2.getValue(),
                            ejbConsecutivo.generarSiguienteConsecutivo(
                                            "REGLAHORARIO",
                                            "COMPANIA=" + compania + "",
                                            FrmreglahorariosControladorEnum.PARAM2
                                                            .getValue()));
            registro.getCampos().put(
                            FrmreglahorariosControladorEnum.PARAM3.getValue(),
                            elementoDevolutivo);
            registro.getCampos().put(
                            FrmreglahorariosControladorEnum.PARAM4.getValue(),
                            serieDevolutivo);
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        try {
            // <CODIGO_DESARROLLADO>
            if (((Date) registro.getCampos().get(strFecha)).before(
                            (Date) registro.getCampos().get("FECHA_INICIO"))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB38"));
                return false;
            }

            registro.getCampos().remove("NOMBRE_DIA");
            registro.getCampos().remove("HORA_INICIO_LB");
            registro.getCampos().remove("HORA_FINAL_LB");

            registro.getCampos().put("HORA_INICIO",
                            SysmanFunciones.convertirAFechaHora(
                                            "30/12/1899 " + registro
                                                            .getCampos()
                                                            .get("HORA_INICIO")
                                                + ":00"));

            registro.getCampos().put("HORA_FINAL",
                            SysmanFunciones.convertirAFechaHora(
                                            "30/12/1899 " + registro
                                                            .getCampos()
                                                            .get("HORA_FINAL")
                                                + ":00"));

            // </CODIGO_DESARROLLADO>
        }
        catch (ParseException ex) {
            Logger.getLogger(FrmreglahorariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void actualizarRegistros(String accion) {
        /*
         * @accion Limpiar = Sin registros. Todos = Cuando se
         * selecciona todos los devolutivos. Devolutivo = Cuando se
         * selecciona un devolutivo especifico. Padre = Cuando se
         * selecciona vacio en una parte del devolutivo, se deben
         * cargar las reglas del Devolutivo Actual. Parte = Cuando se
         * selecciona una parte del devolutivo actual.
         */
        if (strLimpiar.equalsIgnoreCase(accion)) {
            elementoDevolutivo = "0";
            serieDevolutivo = 0;
        }
        else if ("Todos".equalsIgnoreCase(accion)) {
            elementoDevolutivo = "-1";
            serieDevolutivo = -1;
            altoEncabezado = "45px";
        }
        else if ("Devolutivo".equalsIgnoreCase(accion)
            || "Padre".equalsIgnoreCase(accion)) {
            elementoDevolutivo = elementoActual;
            serieDevolutivo = Integer.parseInt(serieActual);
        }
        else if ("Parte".equalsIgnoreCase(accion)) {
            altoEncabezado = "212px";
            elementoDevolutivo = elemetoParteActual;
            serieDevolutivo = Integer.parseInt(serieParteActual);
        }
        reasignarOrigen();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("ELEMENTODEVOLUTIVO", elementoDevolutivo);
        parametrosListado.put("SERIEDEVOLUTIVO", serieDevolutivo);

    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(
                        FrmreglahorariosControladorEnum.PARAM2.getValue());
        registro.getCampos().remove("ELEMENTO_DEVOLUTIVO");
        registro.getCampos().remove("SERIE_DEVOLUTIVO");
        registro.getCampos().remove("DIA");
        registro.getCampos().remove("HORA_INICIO_LB");
        registro.getCampos().remove("HORA_FINAL_LB");
        registro.getCampos().remove("NOMBRE_DIA");
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
