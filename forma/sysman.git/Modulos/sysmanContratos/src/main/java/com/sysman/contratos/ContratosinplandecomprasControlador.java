package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 30/09/2015
 * 
 * @author eamaya
 * @version 2.0, 08/08/2017 Cambio de numero de formulario por enum y
 * sysdate por new Date()
 * 
 */
@ManagedBean
@ViewScoped
public class ContratosinplandecomprasControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloContratos;
    private Date fechaFinal;
    private Date fechaInicial;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of ContratosinplandecomprasControlador
     */
    public ContratosinplandecomprasControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloContratos = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONTRATOSINPLANDECOMPRAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            ContratosinplandecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdPDF() {
        if (fechaInicial != null && fechaFinal != null) {
            if (!SysmanFunciones.comparaFechas(fechaInicial, fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
                return;
            }
            generarReporte(ReportesBean.FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmdEXCEL() {
        if (fechaInicial != null && fechaFinal != null) {
            if (!SysmanFunciones.comparaFechas(fechaInicial, fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
                return;
            }
            generarReporte(ReportesBean.FORMATOS.EXCEL97);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    private void generarReporte(FORMATOS formatos) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            String strSql = Reporteador.resuelveConsulta(
                            "000249contratosinactualizarplandecompras",
                            Integer.parseInt(moduloContratos), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000249contratosinactualizarplandecompras",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(
                            ContratosinplandecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | SysmanException | JRException
                        | IOException ex) {
            Logger.getLogger(
                            ContratosinplandecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));

        }
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getCompania() {
        return compania;
    }

}
