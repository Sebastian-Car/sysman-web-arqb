package com.sysman.general;

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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 28/04/2016
 * @modified jguerrero
 * @version 2. 04/04/2017 Se realizaron las respectivas Correcciones
 * del sonar.
 */
@ManagedBean
@ViewScoped
public class InformedeconsecutivosControlador extends BeanBaseModal {

    private final String modulo;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of InformedeconsecutivosControlador
     */
    public InformedeconsecutivosControlador() {
        super();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORMEDECONSECUTIVOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void obtenerReporte(FORMATOS formatos) {
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB985"));
            return;
        }
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            if ("3".equals(modulo)) {
                reemplazar.put("tablacomprobante", "COMPROBANTE_PPTAL");
                reemplazar.put("tablatipocom", "TIPO_COMPROBPP");
                reemplazar.put("fconsecutivos",
                                "PCK_PRESUPUESTO.FC_CONCATENACOMPROBANTESPPTO");
            }
            else {
                reemplazar.put("tablacomprobante", "COMPROBANTE_CNT");
                reemplazar.put("tablatipocom", "TIPO_COMPROBANTE");
                reemplazar.put("fconsecutivos",
                                "PCK_CONTABILIDAD1.FC_CONCATENACOMPROBANTES");
            }
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta("000694LisInformeDeConsecutivos",
                            Integer.parseInt(modulo), reemplazar, parametros);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            String entre = "Entre el periodo "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial,
                                "dd-MMMMM-yyyy")
                + " y " + SysmanFunciones.convertirAFechaCadena(fechaFinal,
                                "dd-MMMMM-yyyy");
            parametros.put("PR_ENTRE", entre);
            parametros.put("PR_ENTRE", entre);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000694LisInformeDeConsecutivos", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPdf(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
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

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
}
