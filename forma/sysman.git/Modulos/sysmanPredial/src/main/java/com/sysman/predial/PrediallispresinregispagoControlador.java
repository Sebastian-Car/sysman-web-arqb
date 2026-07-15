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
import com.sysman.predial.enums.PrediallispresinregispagoControladorEnum;
import com.sysman.predial.enums.PrediallispresinregispagoControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

import java.io.IOException;
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
 * @version 1, 02/06/2016
 *
 * @version 2, 11/07/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class PrediallispresinregispagoControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private Boolean chkConAcuerdos;
    private String codigoInicial;
    private String codigoFinal;
    private String nombreCodigoInicial;
    private Date fecha;
    private String nombreCodigoFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacodigoini;
    private RegistroDataModelImpl listacodigofin;
    private static final String CODIGO = GeneralParameterEnum.CODIGO.getName();

    /**
     * Creates a new instance of PrediallispresinregispagoControlador
     */
    public PrediallispresinregispagoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALLISPRESINREGISPAGO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PrediallispresinregispagoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListacodigoini();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fecha = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacodigoini() {
        //
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallispresinregispagoControladorUrlEnum.URL3376
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listacodigoini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListacodigofin() {
        //
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallispresinregispagoControladorUrlEnum.URL4378
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PrediallispresinregispagoControladorEnum.CODIGOINICIAL
                        .getValue(), codigoInicial);
        listacodigofin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    private void obtenerReporte(FORMATOS formatos) {
        String reporte = "000861PREDIALLISPRESINREGISPAGO";
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            if (chkConAcuerdos) {
                reemplazar.put("condicion", "");
            }
            else {
                reemplazar.put("condicion",
                                "AND    (IP_FACTURADOS.INDPAGO_ACPAG=0 OR IP_FACTURADOS.INDPAGO_ACPAG IS NULL) ");
            }
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_CODIGOINI", codigoInicial);
            parametros.put("PR_CODIGOFIN", codigoFinal);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (SysmanException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        obtenerReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigoini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(CODIGO).toString();
        nombreCodigoInicial = registroAux.getCampos().get("NOMBRE").toString();
        cargarListacodigofin();
    }

    public void seleccionarFilacodigofin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(CODIGO).toString();
        nombreCodigoFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public Boolean getChkConAcuerdos() {
        return chkConAcuerdos;
    }

    public void setChkConAcuerdos(Boolean chkConAcuerdos) {
        this.chkConAcuerdos = chkConAcuerdos;
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

    public String getNombreCodigoInicial() {
        return nombreCodigoInicial;
    }

    public void setNombreCodigoInicial(String nombreCodigoInicial) {
        this.nombreCodigoInicial = nombreCodigoInicial;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreCodigoFinal() {
        return nombreCodigoFinal;
    }

    public void setNombreCodigoFinal(String nombreCodigoFinal) {
        this.nombreCodigoFinal = nombreCodigoFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListacodigoini() {
        return listacodigoini;
    }

    public void setListacodigoini(RegistroDataModelImpl listacodigoini) {
        this.listacodigoini = listacodigoini;
    }

    public RegistroDataModelImpl getListacodigofin() {
        return listacodigofin;
    }

    public void setListacodigofin(RegistroDataModelImpl listacodigofin) {
        this.listacodigofin = listacodigofin;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
