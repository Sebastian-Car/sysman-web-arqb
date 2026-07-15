package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
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
 * @version 1, 29/10/2015
 */
@ManagedBean
@ViewScoped
public class LiselementossegurospaipaControlador extends BeanBaseModal {

    private String moduloAlmacen;
    private String tipo;
    private String ordenar;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of LiselementossegurospaipaControlador
     */
    public LiselementossegurospaipaControlador() {
        try {
            numFormulario = GeneralCodigoFormaEnum.LISELEMENTOSSEGUROSPAIPA_CONTROLADOR
                            .getCodigo();
            moduloAlmacen = SessionUtil.getModulo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            LiselementossegurospaipaControlador.class.getName())
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

    public void oprimircmdPantalla() {
        archivoDescarga = null;
        if ((tipo != null) && (ordenar != null)) {
            generarReporteLisElementosSeguros(ReportesBean.FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmbExcel() {
        archivoDescarga = null;
        if ((tipo != null) && (ordenar != null)) {
            generarReporteLisElementosSeguros(ReportesBean.FORMATOS.EXCEL97);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    private void generarReporteLisElementosSeguros(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipo", tipo);
            String strOrderBy;
            switch (ordenar) {
            case "P":
                strOrderBy = "ORDER BY DEVOLUTIVO.SERIE";
                break;
            case "N":
                strOrderBy = "ORDER BY INVENTARIO.NOMBRELARGO";
                break;
            case "D":
                strOrderBy = "ORDER BY DEPENDENCIA.NOMBRE";
                break;
            case "R":
                strOrderBy = "ORDER BY TERCERO.NOMBRE";
                break;
            case "V":
                strOrderBy = "ORDER BY DEVOLUTIVO.VALOR";
                break;
            default:
                strOrderBy = "ORDER BY DEVOLUTIVO.SERIE";
            }
            reemplazar.put("strOrderBy", strOrderBy);
            String strSql = Reporteador.resuelveConsulta(
                            "000359LisElementosSegurosPaipa",
                            Integer.parseInt(moduloAlmacen), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);
            if ("D".equals(tipo)) {
                parametros.put("PR_SUBTITULO",
                                "LISTADO DE ELEMENTOS DEVOLUTIVOS");
            }
            else {
                parametros.put("PR_SUBTITULO", "LISTADO DE BIENES INMUEBLES");
            }

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000359LisElementosSegurosPaipa", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOrdenar() {
        return ordenar;
    }

    public void setOrdenar(String ordenar) {
        this.ordenar = ordenar;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
}
