package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
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
 * @author otorres
 * @version 1, 04/04/2016
 * 
 * @version 2, 06/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Manejo de EJBs.
 * 
 * @author asana
 * @version 3, 12/06/2017 Redireccion de formulario y Conexion
 */
@ManagedBean
@ViewScoped
public class ComAbiertasControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of ComAbiertasControlador
     */
    public ComAbiertasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getCompania();
        fechaInicial = new Date();
        fechaFinal = new Date();

        try {
            numFormulario = GeneralCodigoFormaEnum.COM_ABIERTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ComAbiertasControlador.class.getName())
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
        /*
         * FR608-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 5, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        if (fechaInicial.before(fechaFinal)) {
            try {
                Map<String, Object> parametros = new HashMap<>();
                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put("compania", compania);
                reemplazar.put("fechaInicial",
                                SysmanFunciones.formatearFecha(fechaInicial));
                reemplazar.put("fechaFinal",
                                SysmanFunciones.formatearFecha(fechaFinal));
                parametros.put("FECHA_INICIAL_TITULO", "FECHA INICIAL "
                    + SysmanFunciones.convertirAFechaCadena(fechaInicial));
                parametros.put("FECHA_FINAL_TITULO", "FECHA FINAL "
                    + SysmanFunciones.convertirAFechaCadena(fechaFinal));
                Reporteador.resuelveConsulta("000608COMAbiertas",
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed("000608COMAbiertas",
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            catch (JRException | IOException | SysmanException
                            | ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB574"));
        }
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @return the fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial
     * the fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * @return the fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal
     * the fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }
}
