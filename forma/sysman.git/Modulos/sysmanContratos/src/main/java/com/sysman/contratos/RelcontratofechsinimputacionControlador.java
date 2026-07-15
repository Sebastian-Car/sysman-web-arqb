package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 14/10/2015
 * @version 2, 10/08/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 *
 */
@ManagedBean
@ViewScoped
public class RelcontratofechsinimputacionControlador extends BeanBaseModal {

    private final String compania;
    private Date fechaFinal;
    private Date fechaInicial;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of
     * RelcontratofechsininputacionControlador
     */
    public RelcontratofechsinimputacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELCONTRATOFECHSININPUTACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RelcontratofechsinimputacionControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = fechaFinal = new Date();
        abrirFormulario();
    }

    public void oprimircmdPantalla() {
        archivoDescarga = null;
        if ((fechaFinal != null) && (fechaInicial != null)) {
            if (!validarFechas(fechaInicial, fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
                return;
            }
            generarReporteRelContratosFechaSinImputacion(
                            FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;
        if ((fechaFinal != null) && (fechaInicial != null)) {
            if (!validarFechas(fechaInicial, fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
                return;
            }
            generarReporteRelContratosFechaSinImputacion(
                            FORMATOS.EXCEL97);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean validarFechas(Date fechaInicial, Date fechaFinal) {
        return fechaInicial.compareTo(fechaFinal) <= 0;
    }

    private void generarReporteRelContratosFechaSinImputacion(
        FORMATOS formatos) {
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta("000280RelContratosFechasininputacion",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_SUBTITULO",
                            SysmanFunciones.concatenar(idioma.getString(
                                            "TB_TB3442"),
                                            " ",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial,
                                                            "dd/MM/yyyy"),
                                            " y ",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaFinal,
                                                            "dd/MM/yyyy")));

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000280RelContratosFechasininputacion", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getCompania() {
        return compania;
    }
}
