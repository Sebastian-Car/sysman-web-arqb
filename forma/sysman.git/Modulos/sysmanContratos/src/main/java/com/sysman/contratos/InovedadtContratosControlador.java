
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 13/11/2015
 * 
 * @modifier amonroy
 * @version 2, 10/08/2017 Se realiza la implementacion de EJBs para
 * las funcion que es llamada en el controlador
 */
@ManagedBean
@ViewScoped
public class InovedadtContratosControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloContratos;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of InovedadtContratosControlador
     */
    public InovedadtContratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloContratos = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.INOVEDADT_CONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InovedadtContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = fechaFinal = new Date();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdPantalla() {
        archivoDescarga = null;
        generarReporteNovedadTContratos(ReportesBean.FORMATOS.PDF);
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;
        generarReporteNovedadTContratos(ReportesBean.FORMATOS.EXCEL97);
    }

    private void generarReporteNovedadTContratos(FORMATOS formato) {
        try {
            String nombreReporte = "000380INovedadTContratos";
            // MANEJO DE REEMPLAZOS DEL REPORTE
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            // MANEJO DE PARAMETROS DEL REPORTE
            String parametroFirma = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA ORDENES DE SERVICIO",
                            moduloContratos,
                            new Date(),
                            true);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FIRMAORDENESDESERVICIO", parametroFirma);

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(moduloContratos), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);

        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getCompania() {
        return compania;
    }

}
