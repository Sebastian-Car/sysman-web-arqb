package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LisRetencionesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
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
 * @author esarmiento
 * @version 1, 22/04/2016
 * @modified spina 07/04/2017 - se refactorizo para servicios DSS y
 * depuracion sonar
 * @version 3, 21/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a getSysdate a New Date.
 * 
 * @author asana
 * @version 4, 12/06/2017 Implementacion enum en formulario y se
 * modifica conexion
 * 
 */
@ManagedBean
@ViewScoped
public class LisRetencionesControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String tipoInicial;
    private String tipoFinal;
    private String anio;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;

    /**
     * Creates a new instance of LisRetencionesControlador
     */
    public LisRetencionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        tipoInicial = SysmanFunciones.defectoValorTexto(1, false);
        tipoFinal = SysmanFunciones.defectoValorTexto(3, true);
        try {
            numFormulario = GeneralCodigoFormaEnum.LIS_RETENCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LisRetencionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        cargarListaAno();
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        try {
            anio = SysmanFunciones.convertirAFechaCadena(
                            new Date(),
                            "yyyy");
        }
        catch (ParseException e) {
            Logger.getLogger(LisRetencionesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisRetencionesControladorUrlEnum.URL3236
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisRetencionesControladorUrlEnum.URL3495
                                                        .getValue());
        listaTipoInicial = new RegistroDataModelImpl(urlBean
                        .getUrl(),
                        urlBean
                                        .getUrlConteo()
                                        .getUrl(),
                        null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put("TIPOINICIAL", tipoInicial);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisRetencionesControladorUrlEnum.URL3496
                                                        .getValue());
        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarCampos() {
        if (SysmanFunciones.validarVariableVacio(tipoInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB100"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(tipoFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB101"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB102"));
            return true;
        }
        return false;
    }

    private void generarInforme(FORMATOS formato) {
        if (validarCampos()) {
            return;
        }

        try {
            String nombreReporte = "000668LisRetenciones";
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("anio", anio);
            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
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

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
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
}
