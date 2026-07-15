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
 * @version 1, 07/10/2015
 * 
 * @version 2, 10/08/2017, <strong>pespitia</strong>:<br>
 * *Se reemplazo el numero del formulario por un enumerado.<br>
 * Se aplicaron las recomendaciones de SonarLint
 */
@ManagedBean
@ViewScoped
public class RelacioncontratosfechaControlador extends BeanBaseModal {

    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el nombre de la compania
     * ingreso desde el usuario inicio sesion
     */
    private final String nombreCompania;

    private Date fechaFinal;
    private Date fechaInicial;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of RelacioncontratosfechaControlador
     */
    public RelacioncontratosfechaControlador() {
        super();

        modulo = SessionUtil.getModulo();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();

        try {
            // 265
            numFormulario = GeneralCodigoFormaEnum.RELACIONCONTRATOSFECHA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RelacioncontratosfechaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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

        generarReporteRelacionDeContratosfechas(FORMATOS.PDF);
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;

        generarReporteRelacionDeContratosfechas(FORMATOS.EXCEL97);
    }

    private void generarReporteRelacionDeContratosfechas(FORMATOS formatos) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "000262RelacionDeContratosFechas";

        try {
            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));

            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_DESDE", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            parametros.put("PR_HASTA",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
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
}
