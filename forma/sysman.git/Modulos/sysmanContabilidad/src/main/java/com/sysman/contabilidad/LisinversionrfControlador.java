package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 05/05/2016
 *
 * @version 2, 07/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class LisinversionrfControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    private Date fechaInicial;
    private Date fechaFinal;
    private String observacion;
    private String titulo;
    private String tituloForm;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LisinversionrfControlador
     */
    public LisinversionrfControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.LISINVERSIONRF_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LisinversionrfControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {

        switch (SessionUtil.getMenuActual()) {
        case "1021001":
            titulo = idioma.getString("TB_TB516");
            tituloForm = idioma.getString("TB_TB517");
            break;
        case "1021002":
            titulo = idioma.getString("TB_TB518");
            tituloForm = idioma.getString("TB_TB519");
            break;
        default:
            break;
        }

        fechaInicial = new Date();
        fechaFinal = new Date();

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {

            String parReporte = "";

            switch (SessionUtil.getMenuActual()) {
            case "1021001":
                parReporte = "000740InversionRF";
                break;
            case "1021002":
                parReporte = "000762InversionRV";
                break;
            default:
                break;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            // Parametro "CONTRALORIA DEPARTAMENTAL"
            String contraloria = ejbSysmanUtil.consultarParametro(compania,
                            "CONTRALORIA DEPARTAMENTAL", modulo, new Date(),
                            false);

            // Parametro "NOMBRE REPRESENTANTE LEGAL"
            String representante = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE REPRESENTANTE LEGAL", modulo, new Date(),
                            false);

            // Parametro "CARGO REPRESENTANTE LEGAL"
            String cargoRepresentante = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CARGO REPRESENTANTE LEGAL", modulo, new Date(),
                            false);

            // Parametro "NOMBRE ENCARGADO AREA"
            String encargado = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE ENCARGADO AREA", modulo, new Date(),
                            false);

            // Parametro "CARGO ENCARGADO AREA"
            String cargoEncargado = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO ENCARGADO AREA", modulo, new Date(),
                            false);

            // Parametro "NUMERO POLIZA"
            String poliza = ejbSysmanUtil.consultarParametro(compania,
                            "NUMERO POLIZA", modulo, new Date(),
                            false);

            // Parametro "VENCIMIENTO POLIZA"
            String vencimiento = ejbSysmanUtil.consultarParametro(compania,
                            "VENCIMIENTO POLIZA", modulo, new Date(),
                            false);

            // Parametro "ASEGURADORA"
            String aseguradora = ejbSysmanUtil.consultarParametro(compania,
                            "ASEGURADORA", modulo, new Date(),
                            false);

            // Parametro "VALOR ASEGURADO"
            String valorAsegurado;

            valorAsegurado = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR ASEGURADO", modulo, new Date(),
                            false);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_OBSERVACION", observacion);
            parametros.put("PR_REPRESENTANTELEGAL", representante);
            parametros.put("PR_CARGOREPRESENTANTE", cargoRepresentante);
            parametros.put("PR_NOMBREENCARGADO", encargado);
            parametros.put("PR_CARGOENCARGADO", cargoEncargado);
            parametros.put("PR_NUMEROPOLIZA", poliza);
            parametros.put("PR_VENCIMIENTOPOLIZA", vencimiento);
            parametros.put("PR_ASEGURADORA", aseguradora);
            parametros.put("PR_VALORASEGURADO", valorAsegurado);
            parametros.put("PR_FECHAINI", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_CONTRALORIADEPARTAMENTAL", contraloria);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FECHAFIN",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());

            Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | ParseException | JRException | IOException
                        | SysmanException e) {
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getTituloForm() {
        return tituloForm;
    }

    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

}
