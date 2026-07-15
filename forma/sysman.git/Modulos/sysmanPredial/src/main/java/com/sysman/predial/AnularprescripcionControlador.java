package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.AnularprescripcionControladorEnum;
import com.sysman.predial.enums.AnularprescripcionControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
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
 * @author ybecerra
 * @version 1, 01/08/2016
 * @version 2, 27/06/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 *
 */
@ManagedBean
@ViewScoped
public class AnularprescripcionControlador extends BeanBaseModal {
    private final String compania;
    private final String codigoCons;
    // <DECLARAR_ATRIBUTOS>

    private String codigo;
    private String formato;
    private String resolucion;
    private Date fechaPrescripcion;
    private String elaborada;
    private String firmada;
    private String resolucionAnt;
    private Date fechaAnterior;
    private String fechaPlantilla;
    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModelImpl listaformato;

    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    /**
     * Creates a new instance of AnularprescripcionControlador
     */
    public AnularprescripcionControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.ANULARPRESCRIPCION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AnularprescripcionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaCodigo();
        cargarListaformato();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnularprescripcionControladorUrlEnum.URL3148
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaformato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(AnularprescripcionControladorEnum.TIPO.getValue(),
                        AnularprescripcionControladorEnum.TIPOFORMATO
                                        .getValue());
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnularprescripcionControladorUrlEnum.URL3711
                                                        .getValue());
        listaformato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdRegistrar() {
        if (fechaPrescripcion.before(fechaAnterior)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1298"));
            return;
        }
        try {
            String observacion = idioma.getString("TB_TB1087")
                            .replace("s$resolucion$s", resolucion)
                            .replace("s$fechaPrescripcion$s",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaAnterior))
                            .replace("s$elaborada$s", elaborada)
                            .replace("s$firmada$s", firmada);

            if (ejbPredialOcho.anularPrescripcion(compania, codigo,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            fechaAnterior,
                            resolucionAnt, SessionUtil.getUser().getCodigo(),
                            observacion, resolucion,
                            SysmanFunciones.ano(fechaAnterior))) {
                codigo = null;
                fechaAnterior = null;
                resolucionAnt = null;
                fechaPrescripcion = null;
                resolucion = null;
                elaborada = null;
                firmada = null;
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1007"));
            }
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtnExo() {
        // <CODIGO_DESARROLLADO>
        String strNombreDocumento = idioma.getString("TB_TB498") + codigo;
        String[] campos = new String[3];
        String[] valores = new String[3];
        campos[0] = "codigoPlantilla";
        campos[1] = "fechaPlantilla";
        campos[2] = "nombreDocDescarga";

        valores[0] = formato;
        try {
            valores[1] = SysmanFunciones.formatearFecha(
                            SysmanFunciones.convertirAFecha(fechaPlantilla));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        valores[2] = strNombreDocumento;

        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$compania$s", "'" + compania + "'");
        variablesConsultaW.put("s$numeroOrden$s",
                        "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
        variablesConsultaW.put("s$codigo$s", "'" + codigo + "'");

        // variables por parametro para documento word
        SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigo = registroAux.getCampos().get(codigoCons).toString();
    }

    public void seleccionarFilaformato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        formato = registroAux.getCampos().get(codigoCons).toString();
        fechaPlantilla = registroAux.getCampos().get("FECHAOCULTA").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public Date getFechaPrescripcion() {
        return fechaPrescripcion;
    }

    public void setFechaPrescripcion(Date fechaPrescripcion) {
        this.fechaPrescripcion = fechaPrescripcion;
    }

    public String getElaborada() {
        return elaborada;
    }

    public void setElaborada(String elaborada) {
        this.elaborada = elaborada;
    }

    public String getFirmada() {
        return firmada;
    }

    public void setFirmada(String firmada) {
        this.firmada = firmada;
    }

    public String getResolucionAnt() {
        return resolucionAnt;
    }

    public void setResolucionAnt(String resolucionAnt) {
        this.resolucionAnt = resolucionAnt;
    }

    public Date getFechaAnterior() {
        return fechaAnterior;
    }

    public void setFechaAnterior(Date fechaAnterior) {
        this.fechaAnterior = fechaAnterior;
    }

    public String getFechaPlantilla() {
        return fechaPlantilla;
    }

    public void setFechaPlantilla(String fechaPlantilla) {
        this.fechaPlantilla = fechaPlantilla;
    }

    public RegistroDataModelImpl getListaCodigo() {
        return listaCodigo;
    }

    public void setListaCodigo(RegistroDataModelImpl listaTxtCodigo) {
        this.listaCodigo = listaTxtCodigo;
    }

    public RegistroDataModelImpl getListaformato() {
        return listaformato;
    }

    public void setListaformato(RegistroDataModelImpl listaformato) {
        this.listaformato = listaformato;
    }

}
