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
import com.sysman.predial.enums.FrmcuotasdeudaControladorEnum;
import com.sysman.predial.enums.FrmcuotasdeudaControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 27/05/2016
 * 
 * @author eamaya
 * @version 2.0, 30/06/2017, Proceso de Refactoring DSS y cambio de
 * SYSDATE por new Date()
 * 
 */
@ManagedBean
@ViewScoped

public class FrmcuotasdeudaControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbAcuerdoInicial;
    private RegistroDataModelImpl listaCmbAcuerdoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmcuotasdeudaControlador
     */
    public FrmcuotasdeudaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCUOTASDEUDA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmcuotasdeudaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbAcuerdoInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        fechaInicial = new Date();
        fechaFinal = new Date();

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmbAcuerdoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcuotasdeudaControladorUrlEnum.URL5294
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCmbAcuerdoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOACUERDO.getName());
    }

    public void cargarListaCmbAcuerdoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcuotasdeudaControladorUrlEnum.URL6093
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        param.put(FrmcuotasdeudaControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCmbAcuerdoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOACUERDO.getName());
    }

    public void obtenerReporte(FORMATOS formatos) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String entre = "ENTRE EL "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial)
                + " Y EL  "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal) + "";
            parametros.put("PR_ENTRE", entre);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta("000828INFCUOTASDEUDA",
                            Integer.valueOf(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000828INFCUOTASDEUDA",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmbAcuerdoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOACUERDO.getName())
                        .toString();
        cargarListaCmbAcuerdoFinal();
    }

    public void seleccionarFilaCmbAcuerdoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOACUERDO.getName())
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
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

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCmbAcuerdoInicial() {
        return listaCmbAcuerdoInicial;
    }

    public void setListaCmbAcuerdoInicial(
        RegistroDataModelImpl listaCmbAcuerdoInicial) {
        this.listaCmbAcuerdoInicial = listaCmbAcuerdoInicial;
    }

    public RegistroDataModelImpl getListaCmbAcuerdoFinal() {
        return listaCmbAcuerdoFinal;
    }

    public void setListaCmbAcuerdoFinal(
        RegistroDataModelImpl listaCmbAcuerdoFinal) {
        this.listaCmbAcuerdoFinal = listaCmbAcuerdoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
