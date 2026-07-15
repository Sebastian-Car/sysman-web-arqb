package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 23/05/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 * 
 * @author eamaya
 * @version 3.1, 06/07/2017, Cambio de Sysdate por new Date()
 * 
 */
@ManagedBean
@ViewScoped
public class FrmresolucionesControlador extends BeanBaseModal {
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String opcion;
    private boolean reporteNovedad;
    private boolean novedadBloqueado;
    private boolean decretos;
    private String tipoNovedad;
    private Date fechaInicial;
    private Date fechaFinal;
    private String resolucionInicial;
    private String resolucionFinal;
    private StreamedContent archivoDescarga;
    private boolean resolucionVisible;
    private boolean fechaVisible;
    private String condicion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmresolucionesControlador
     */
    public FrmresolucionesControlador() {
        super();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMRESOLUCIONES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmresolucionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {

        opcion = "1";
        fechaVisible = true;
        fechaInicial = new Date();
        fechaFinal = new Date();
        novedadBloqueado = true;
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            String nombreReporteUno = "000809INFIGACREPORTE";
            String nombreReporteDos = "000810INFRESOLUCION3";

            if (faltanCamosObligatorios(opcion)) {
                return;
            }

            definirCondicion(opcion);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            reemplazar.put("condicion", condicion);
            HashMap<String, Object> reemplazar1 = new HashMap<>();
            reemplazar1.put("resolucionInicial", "'" + resolucionInicial + "'");
            reemplazar1.put("resolucionFinal", "'" + resolucionFinal + "'");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            Map<String, Object> parametros1 = new HashMap<>();
            parametros1.put("PR_NOMBRECOMPANIA", SessionUtil
                            .getCompaniaIngreso().getNombre().toUpperCase());

            if (decretos) {
                Reporteador.resuelveConsulta(nombreReporteUno,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
                ByteArrayInputStream reporteIgac = JsfUtil.serializarReporte(
                                nombreReporteUno, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
                Reporteador.resuelveConsulta(nombreReporteDos,
                                Integer.parseInt(modulo), reemplazar1,
                                parametros1);
                ByteArrayInputStream resoluciones = JsfUtil.serializarReporte(
                                nombreReporteDos, parametros1,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

                ByteArrayInputStream[] reportes = { reporteIgac, resoluciones };
                String extension = definirExtensionInf(formato);
                String[] nombreReportes = { nombreReporteUno
                    + extension, nombreReporteDos + extension };

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                reportes, nombreReportes);
            }
            else {
                Reporteador.resuelveConsulta(nombreReporteUno,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                nombreReporteUno, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }
        }
        catch (JRException | IOException | SysmanException | SQLException
                        | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Crea la condicion para el informe segun la opcion seleccionada,
     * fecha o resolucion.
     * 
     * @param opcionSeleccionada
     * Opcion seleccionada.
     */
    private void definirCondicion(String opcionSeleccionada) {
        switch (opcionSeleccionada) {
        case "1":
            condicion = " AND IP_IGAC_REPORTE.FECHA_REGISTRO BETWEEN "
                + SysmanFunciones.formatearFecha(fechaInicial) + " AND "
                + SysmanFunciones.formatearFecha(fechaFinal) + " ";
            break;
        case "2":
            condicion = " AND IP_IGAC_REPORTE.RESOLUCION BETWEEN '"
                + resolucionInicial + "' AND '" + resolucionFinal + "' ";
            break;
        default:
            break;
        }
        asignarCondRepNovedad();
    }

    /**
     * Validacion de campos obligatorios segun la opcion seleccionada.
     * 
     * @param opcionSeleccionada
     * Opcion seleccionada
     * @return Verdadero si hace falta diligenciar campos
     * obligatorios.
     */
    private boolean faltanCamosObligatorios(String opcionSeleccionada) {
        if ("1".equals(opcionSeleccionada)
            && (fechaInicial == null || fechaFinal == null)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB150"));
            return true;
        }
        else if ("2".equals(opcionSeleccionada)
            && (SysmanFunciones.validarVariableVacio(resolucionInicial)
                || SysmanFunciones.validarVariableVacio(resolucionFinal))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB151"));
            return true;
        }
        else {
            return false;
        }
    }

    private void asignarCondRepNovedad() {
        String tN;
        if (reporteNovedad) {
            tN = (tipoNovedad == null) || tipoNovedad.isEmpty() ? "1"
                : tipoNovedad;
            condicion = condicion
                + "AND IP_IGAC_REPORTE.TIPO_NOVEDAD =  '" + tN + "' ";

        }

    }

    private String definirExtensionInf(ReportesBean.FORMATOS formato) {
        String extension = "";
        switch (formato) {
        case PDF:
            extension = ".pdf";
            break;
        case EXCEL97:
            extension = ".xls";
            break;
        case EXCEL:
            extension = ".xlsx";
            break;
        default:
            break;
        }
        return extension;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarMEntre() {
        fechaVisible = ("1").equals(opcion);
        resolucionVisible = ("2").equals(opcion);
        tipoNovedad = null;
        if (("1").equals(opcion)) {
            decretos = false;

        }
    }

    public void cambiarIndNovedad() {

        novedadBloqueado = !reporteNovedad;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public boolean isReporteNovedad() {
        return reporteNovedad;
    }

    public void setReporteNovedad(boolean reporteNovedad) {
        this.reporteNovedad = reporteNovedad;
    }

    public boolean isDecretos() {
        return decretos;
    }

    public void setDecretos(boolean decretos) {
        this.decretos = decretos;
    }

    public String getTipoNovedad() {
        return tipoNovedad;
    }

    public void setTipoNovedad(String tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
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

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getResolucionInicial() {
        return resolucionInicial;
    }

    public void setResolucionInicial(String resolucionInicial) {
        this.resolucionInicial = resolucionInicial;
    }

    public String getResolucionFinal() {
        return resolucionFinal;
    }

    public void setResolucionFinal(String resolucionFinal) {
        this.resolucionFinal = resolucionFinal;
    }

    public boolean isResolucionVisible() {
        return resolucionVisible;
    }

    public void setResolucionVisible(boolean resolucionVisible) {
        this.resolucionVisible = resolucionVisible;
    }

    public boolean isFechaVisible() {
        return fechaVisible;
    }

    public void setFechaVisible(boolean fechaVisible) {
        this.fechaVisible = fechaVisible;
    }

    public boolean isNovedadBloqueado() {
        return novedadBloqueado;
    }

    public void setNovedadBloqueado(boolean novedadBloqueado) {
        this.novedadBloqueado = novedadBloqueado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
