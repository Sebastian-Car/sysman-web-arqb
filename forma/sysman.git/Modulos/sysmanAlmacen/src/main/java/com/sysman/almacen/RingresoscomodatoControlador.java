package com.sysman.almacen;

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
 * @author jrodriguezr
 * @version 1, 27/01/2016
 * 
 * @author eamaya
 * @version 2, 08/05/2017 Manejo de EJBs y Correcciones SonarLint
 * 
 */
@ManagedBean
@ViewScoped
public class RingresoscomodatoControlador extends BeanBaseModal {

    private final String compania;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of RingresoscomodatoControlador
     */
    public RingresoscomodatoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RINGRESOSCOMODATO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RingresoscomodatoControlador.class.getName())
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

    public void oprimirPresentar() {
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
        if ((fechaInicial == null) || (fechaFinal == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2001"));
            return;
        }
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("compania", compania);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            String reporte = "000481CIngresosComoDet";
            String reporteSub = "000482SubCIngresosComodato";
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            String strSqlSub = Reporteador.resuelveConsulta(reporteSub,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_STRSQL_SUBC_INGRESOSCONSUMO", strSqlSub);

            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal,
                                            "dd ' de ' MMMM ' de ' yyyy"));
            parametros.put("PR_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial,
                                            "dd ' de ' MMMM ' de ' yyyy"));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

    @Override
    public void abrirFormulario() {
        // Metodo heredado
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

}
