package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.enums.ReportenotificacionescobroControladorEnum;
import com.sysman.predial.enums.ReportenotificacionescobroControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 02/06/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambia el llamado del codigo del
 * formulario y actualizacion de ConnectorPool
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 13/06/2017
 * 
 * @modifier amonroy
 * @version 4, 17/07/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para la funcion que es llamada en el
 * controlador
 */
@ManagedBean
@ViewScoped
public class ReportenotificacionescobroControlador extends BeanBaseModal {
    private final String compania;
    private final String cConsecutivo;
    /**
     * Constante que identifica el nombre del campo CODIGO
     */
    private final String campoCodigo;
    // <DECLARAR_ATRIBUTOS>
    private String opcionEntre;
    private String opcionDetalle;
    private String notificacionInicial;
    private String notificacionFinal;
    private String predioInicial;
    private String predioFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private boolean visibleFecha;
    private boolean visiblePredio;
    private boolean visibleNotificacion;
    /**
     * Implementacion del EJB de EjbPredialCeroRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL
     */
    @EJB
    private EjbPredialCeroRemote ejbPredialCero;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listatxtNotificacionInicial;
    private RegistroDataModelImpl listatxtNotificacionFinal;
    private RegistroDataModelImpl listatxtPredioInicial;
    private RegistroDataModelImpl listatxtPredioFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ReportenotificacionescobroControlador
     */
    public ReportenotificacionescobroControlador() {
        super();
        compania = SessionUtil.getCompania();
        campoCodigo = "CODIGO";
        cConsecutivo = "CONSECUTIVO";
        try {
            numFormulario = GeneralCodigoFormaEnum.REPORTENOTIFICACIONESCOBRO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            initAdicional();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ReportenotificacionescobroControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public void initAdicional() {
        opcionDetalle = "1";
        opcionEntre = "1";
        visibleFecha = true;
        fechaInicial = fechaFinal = new Date();
        visibleNotificacion = visiblePredio = false;
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListatxtNotificacionInicial();
        cargarListatxtNotificacionFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListatxtPredioInicial();
        cargarListatxtPredioFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listatxtNotificacionInicial
     *
     */
    public void cargarListatxtNotificacionInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportenotificacionescobroControladorUrlEnum.URL5062
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatxtNotificacionInicial = new RegistroDataModelImpl(
                        urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
                        param,
                        true, cConsecutivo);
    }

    /**
     * 
     * Carga la lista listatxtNotificacionFinal
     *
     */
    public void cargarListatxtNotificacionFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportenotificacionescobroControladorUrlEnum.URL5819
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ReportenotificacionescobroControladorEnum.NOTIFICACION
                        .getValue(), notificacionInicial);

        listatxtNotificacionFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cConsecutivo);
    }

    public void cargarListatxtPredioInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportenotificacionescobroControladorUrlEnum.URL6580
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listatxtPredioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    public void cargarListatxtPredioFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportenotificacionescobroControladorUrlEnum.URL7511
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(ReportenotificacionescobroControladorEnum.PREDIO.getValue(),
                        predioInicial);

        listatxtPredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envia los
     * parametros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void getInforme(FORMATOS formato) {
        String condAdicional = null;
        String reporte = null;
        try {
            fechaFinal = SysmanFunciones.convertirFechaFinalDia(
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            switch (opcionEntre) {
            case "1":
                condAdicional = armarCondicionFechas();
                break;
            case "2":
                condAdicional = SysmanFunciones.concatenar(
                                " AND IP_NOTIFICAPREDIAL.CONSECUTIVO BETWEEN '",
                                notificacionInicial, "' AND '",
                                notificacionFinal, "' ");
                break;
            case "3":
                condAdicional = SysmanFunciones.concatenar(
                                "AND IP_NOTIFICAPREDIAL.CODIGO BETWEEN '",
                                predioInicial, "' AND '", predioFinal, "' ");
                break;
            default:
                break;
            }

            // El informe detallado ya se encuentra migrado y su
            // consulta tambien se encuentra registrada en la BD
            // "000868PREDIALLISTANOTICOBRODET"
            reporte = "000865PREDIALLISTANOTICOBRO";

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("condAdicional", condAdicional);
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametrosRep = new HashMap<>();
            parametrosRep.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametrosRep.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametrosRep.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametrosRep.put("PR_ENTREFECHAS", "1".equals(opcionEntre));
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametrosRep);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametrosRep,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Arma la condicion que se envia como reemplazo a la consulta que
     * genera el reporte
     * 
     * @return condicion entre fechas
     */
    private String armarCondicionFechas() {
        return SysmanFunciones.concatenar(
                        "AND IP_NOTIFICAPREDIAL.FECHA_REGISTRO BETWEEN ",
                        SysmanFunciones.formatearFecha(fechaInicial), " AND ",
                        SysmanFunciones.formatearFecha(fechaFinal), " ");
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartxtNotificacionInicial() {
        // <CODIGO_DESARROLLADO>
        notificacionFinal = null;
        cargarListatxtNotificacionFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarentre() {
        // <CODIGO_DESARROLLADO>
        visibleFecha = ("1").equals(opcionEntre);
        visibleNotificacion = ("2").equals(opcionEntre);
        visiblePredio = ("3").equals(opcionEntre);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatxtNotificacionInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatxtNotificacionInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        notificacionInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(cConsecutivo), "").toString();
        notificacionFinal = null;
        cargarListatxtNotificacionFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatxtNotificacionFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatxtNotificacionFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        notificacionFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cConsecutivo), "")
                        .toString();

    }

    public void seleccionarFilatxtPredioInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predioInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodigo), "")
                        .toString();
        predioFinal = null;
        cargarListatxtPredioFinal();
    }

    public void seleccionarFilatxtPredioFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predioFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodigo), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getOpcionEntre() {
        return opcionEntre;
    }

    public void setOpcionEntre(String opcionEntre) {
        this.opcionEntre = opcionEntre;
    }

    public String getOpcionDetalle() {
        return opcionDetalle;
    }

    public void setOpcionDetalle(String opcionDetalle) {
        this.opcionDetalle = opcionDetalle;
    }

    public String getNotificacionInicial() {
        return notificacionInicial;
    }

    public void setNotificacionInicial(String notificacionInicial) {
        this.notificacionInicial = notificacionInicial;
    }

    public String getNotificacionFinal() {
        return notificacionFinal;
    }

    public void setNotificacionFinal(String notificacionFinal) {
        this.notificacionFinal = notificacionFinal;
    }

    public String getPredioInicial() {
        return predioInicial;
    }

    public void setPredioInicial(String predioInicial) {
        this.predioInicial = predioInicial;
    }

    public String getPredioFinal() {
        return predioFinal;
    }

    public void setPredioFinal(String predioFinal) {
        this.predioFinal = predioFinal;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isVisibleFecha() {
        return visibleFecha;
    }

    public void setVisibleFecha(boolean visibleFecha) {
        this.visibleFecha = visibleFecha;
    }

    public boolean isVisiblePredio() {
        return visiblePredio;
    }

    public void setVisiblePredio(boolean visiblePredio) {
        this.visiblePredio = visiblePredio;
    }

    public boolean isVisibleNotificacion() {
        return visibleNotificacion;
    }

    public void setVisibleNotificacion(boolean visibleNotificacion) {
        this.visibleNotificacion = visibleNotificacion;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListatxtPredioInicial() {
        return listatxtPredioInicial;
    }

    public RegistroDataModelImpl getListatxtNotificacionInicial() {
        return listatxtNotificacionInicial;
    }

    public void setListatxtNotificacionInicial(
        RegistroDataModelImpl listatxtNotificacionInicial) {
        this.listatxtNotificacionInicial = listatxtNotificacionInicial;
    }

    public RegistroDataModelImpl getListatxtNotificacionFinal() {
        return listatxtNotificacionFinal;
    }

    public void setListatxtNotificacionFinal(
        RegistroDataModelImpl listatxtNotificacionFinal) {
        this.listatxtNotificacionFinal = listatxtNotificacionFinal;
    }

    public void setListatxtPredioInicial(
        RegistroDataModelImpl listatxtPredioInicial) {
        this.listatxtPredioInicial = listatxtPredioInicial;
    }

    public RegistroDataModelImpl getListatxtPredioFinal() {
        return listatxtPredioFinal;
    }

    public void setListatxtPredioFinal(
        RegistroDataModelImpl listatxtPredioFinal) {
        this.listatxtPredioFinal = listatxtPredioFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
