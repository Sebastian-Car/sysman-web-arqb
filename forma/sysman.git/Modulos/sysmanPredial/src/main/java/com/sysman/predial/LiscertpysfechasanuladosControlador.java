package com.sysman.predial;

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
 * @author dsuesca
 * @version 1, 20/06/2016
 * 
 * @modifier amonroy
 * @version 2, 07/07/2017 Se borra codigo de access que se encontraba
 * comentado
 */
@ManagedBean
@ViewScoped
public class LiscertpysfechasanuladosControlador extends BeanBaseModal {
    private final String compania;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of LiscertpysfechasanuladosControlador
     */
    public LiscertpysfechasanuladosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISCERTPYSFECHASANULADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            fechaInicial = fechaFinal = new Date();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            LiscertpysfechasanuladosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        String reporte = "000927LisPazYsalvosAnulados";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (JRException | IOException ex) {
            mostrarMensajeTransIntRup(ex);
        }

    }

    public void mostrarMensajeTransIntRup(Exception ex) {
        JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                        idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                        ex.getMessage()));
        Logger.getLogger(LiscertpysfechasanuladosControlador.class
                        .getName()).log(Level.SEVERE, null, ex);
    }

    // <SET_GET_ATRIBUTOS>
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

    public String getCompania() {
        return compania;
    }

}
