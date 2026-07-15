package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author esarmiento
 * @version 1, 22/04/2016
 * @modified jguerrero
 * @version 2. 12/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped
public class BasesMalCalculadasControlador extends BeanBaseModal {
    private final String modulo;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private final String mensajeErrorCons;

    /**
     * Creates a new instance of BasesMalCalculadasControlador
     */
    public BasesMalCalculadasControlador() {
        super();
        modulo = SessionUtil.getModulo();
        mensajeErrorCons = "MSM_TRANS_INTERRUMPIDA";
        try {
            numFormulario = GeneralCodigoFormaEnum.BASES_MAL_CALCULADAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BasesMalCalculadasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        Date fechaActual = new Date();
        fechaInicial = fechaActual;
        fechaFinal = fechaActual;
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado de la clase Bean base

    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }

        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB752"));
            return;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB753"));
            return;
        }
        String nombreReporte = "000666RelacionBasesMalCalculadas";
        try {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            parametros.put("PR_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(fechaInicial,
                                            "EEEE, dd 'de' MMMM 'de' yyyy"));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal,
                                            "EEEE, dd 'de' MMMM 'de' yyyy"));

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e) {
            Logger.getLogger(BasesMalCalculadasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(
                            idioma.getString(mensajeErrorCons) + " "
                                + idioma.getString("MSM_INFORME_NO_EXISTE")
                                + " " + e.getMessage() + " " + nombreReporte);
        }
        catch (ParseException | JRException
                        | IOException e) {
            Logger.getLogger(BasesMalCalculadasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(
                            idioma.getString(mensajeErrorCons) + " "
                                + e.getMessage());
        }
        catch (SysmanException e) {
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

    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB528"));
            rta = false;
        }
        return rta;
    }

}
