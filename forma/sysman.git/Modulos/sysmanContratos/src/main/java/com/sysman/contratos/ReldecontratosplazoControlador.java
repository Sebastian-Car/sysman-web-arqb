package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.ReldecontratosplazoControladorEnum;
import com.sysman.contratos.enums.ReldecontratosplazoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @author dcastro
 * @version 1, 14/10/2015
 * 
 * @version 2, 10/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class ReldecontratosplazoControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String terceroInicial;
    private String terceroFinal;
    private String nombreInicial;
    private String nombreFinal;
    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listaTipoContratoInicial;
    private List<Registro> listaTipoContratoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;

    /**
     * Creates a new instance of ReldecontratosplazoControlador
     */
    public ReldecontratosplazoControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.RELDECONTRATOSPLAZO_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ReldecontratosplazoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContratoInicial();
        cargarListaTipoContratoFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaTipoContratoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReldecontratosplazoControladorUrlEnum.URL3545
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoContratoFinal() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(ReldecontratosplazoControladorEnum.PARAM0.getValue(),tipoContratoInicial);
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaTipoContratoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReldecontratosplazoControladorUrlEnum.URL3574
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ReldecontratosplazoControladorUrlEnum.URL4447.getValue());    
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "NIT");
        
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ReldecontratosplazoControladorUrlEnum.URL5087.getValue());    
        Map<String,Object> param = new TreeMap<>();
        param.put(ReldecontratosplazoControladorEnum.PARAM1.getValue(),nombreInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "NIT");
    }

    public void oprimircmdPdf() {
        archivoDescarga = null;
        generarReporteRelDeContratosPlazo(ReportesBean.FORMATOS.PDF);
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;
        generarReporteRelDeContratosPlazo(ReportesBean.FORMATOS.EXCEL97);
    }

    public void cambiarTipoContratoInicial() {
        cargarListaTipoContratoFinal();
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        nombreInicial = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
        nombreFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getTipoContratoFinal() {
        return tipoContratoFinal;
    }

    public void setTipoContratoFinal(String tipoContratoFinal) {
        this.tipoContratoFinal = tipoContratoFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        List<Registro> listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public List<Registro> getListaTipoContratoFinal() {
        return listaTipoContratoFinal;
    }

    public void setListaTipoContratoFinal(
        List<Registro> listaTipoContratoFinal) {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    private void generarReporteRelDeContratosPlazo(
        ReportesBean.FORMATOS formatos) {
        String fechaFinalizacion = SysmanFunciones.formatearFecha(new Date());
        try {

            String parReporte = "000279RelDeContratosPlazo";

            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put("fechaFinalizacion", fechaFinalizacion);
            remplazar.put("contratoInicial", "'" + tipoContratoInicial + "'");
            remplazar.put("contratoFinal", "'" + tipoContratoFinal + "'");
            remplazar.put("terceroInicial", "'" + nombreInicial + "'");
            remplazar.put("terceroFinal", "'" + nombreFinal + "'");

            String strSql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(ReldecontratosplazoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            Constantes.MSM_INFORME_NO_EXISTE, ex.getMessage()));
        }
        catch (SysmanException | JRException | IOException ex) {
            Logger.getLogger(ReldecontratosplazoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));
        }
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado
    }
}
