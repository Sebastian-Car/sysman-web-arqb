package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LiscompdetcontableControladorEnum;
import com.sysman.contabilidad.enums.LiscompdetcontableControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
 * @version 1, 26/04/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 10/04/2017
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped

public class LiscompdetcontableControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    /**
     * Constante definida que toma el valor de
     * GeneralParameterEnum.CODIGO.getName()
     */
    private final String cCodigo;
    private String tipoInicial;
    private String tipoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private boolean formatoEspecialExcel = false; //JM 04/12/2024

    /**
     * Creates a new instance of LiscompdetcontableControlador
     */
    public LiscompdetcontableControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISCOMPDETCONTABLE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LiscompdetcontableControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        fechaInicial = fechaFinal = new Date();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR664-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTipoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscompdetcontableControladorUrlEnum.URL3225
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaTipoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscompdetcontableControladorUrlEnum.URL3892
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LiscompdetcontableControladorEnum.PARAM0.getValue(),
                        tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void obtenerReporte(FORMATOS formatos) {
        try {
        	String reporte = "";
        	String titulo = "";
        	if(formatoEspecialExcel) {
        		reporte = "002677LISCOMPDETCONTABLEESPECIAL";
        		titulo = "Relacion de Documentos Entre ";
        	}else {
        		reporte = "000681LisCompDetContable";
        		titulo = "Comprobante de Diario Entre ";
        	}
            //002677LISCOMPDETCONTABLE
        	HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("tipoInicial", "'" + tipoInicial + "'");
            reemplazar.put("tipoFinal", "'" + tipoFinal + "'");

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            String entre = titulo
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal);

            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            parametros.put("PR_ENTRE", entre);
            archivoDescarga = JsfUtil.exportarStreamed(
            				reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (JRException | IOException | ParseException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(LiscompdetcontableControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux.getCampos().get(cCodigo) == null ? ""
            : registroAux.getCampos().get(cCodigo).toString();
        cargarListaTipoFinal();
        tipoFinal = null;
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos().get(cCodigo) == null ? ""
            : registroAux.getCampos().get(cCodigo).toString();
    }

    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
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

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }
    
    public boolean isFormatoEspecialExcel() {
        return formatoEspecialExcel;
    }

    public void setFormatoEspecialExcel(boolean formatoEspecialExcel) {
        this.formatoEspecialExcel = formatoEspecialExcel;
    }

}
