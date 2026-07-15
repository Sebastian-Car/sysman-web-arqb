/*-
 * DiferirFacturaReporteador.java
 *
 * 1.0
 * 
 * 22/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmDiferirFacturaControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Reporteador general que sirve de plantilla para la impresion del
 * reporte configurado en la tabla SF_TIPO_COBRO o en el parametro SF
 * FORMATO FACTURACION
 * 
 * @version 1.0, 22/12/2017
 * @author jcrodriguez
 *
 */
public class DiferirFacturaReporteador {

    private String compania;
    private String modulo;
    private StreamedContent archivoDescarga;
    private ResourceBundle idioma;

    private final Log logger = LogFactory.getLog(this.getClass());

    private EjbSysmanUtilRemote ejbSysmanUtil;

    public DiferirFacturaReporteador(EjbSysmanUtilRemote ejbSysmanUtil) {
        this.ejbSysmanUtil = ejbSysmanUtil;
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
    }

    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
                            new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    private void parametrosInforme0001489(Map<String, Object> parametros,
        Registro registro) {

        if (registro != null) {
            if (Boolean.parseBoolean(
                            registro.getCampos()
                                            .get(FrmDiferirFacturaControladorEnum.MANEJA_RESFACTURACION
                                                            .getValue())
                                            .toString())) {
                parametros.put("PR_DATOSRES",
                                SysmanFunciones.concatenar(
                                                SysmanFunciones.nvl(
                                                                registro.getCampos()
                                                                                .get(FrmDiferirFacturaControladorEnum.NORES_FACTURACION
                                                                                                .getValue()),
                                                                "")
                                                                .toString(),
                                                " ",
                                                SysmanFunciones.concatenar(
                                                                SysmanFunciones.nvl(
                                                                                registro.getCampos()
                                                                                                .get(FrmDiferirFacturaControladorEnum.FECRES_FACTURACION
                                                                                                                .getValue()),
                                                                                "")
                                                                                .toString())));

                parametros.put("PR_CONSECUTIVO",
                                SysmanFunciones.concatenar(
                                                SysmanFunciones.nvl(
                                                                registro.getCampos()
                                                                                .get(FrmDiferirFacturaControladorEnum.NOINICIAL_FACTURACION
                                                                                                .getValue()),
                                                                "")
                                                                .toString(),
                                                " - ",
                                                SysmanFunciones.concatenar(
                                                                SysmanFunciones.nvl(
                                                                                registro.getCampos()
                                                                                                .get(FrmDiferirFacturaControladorEnum.NOFINAL_FACTURACION
                                                                                                                .getValue()),
                                                                                "")
                                                                                .toString())));
                parametros.put(FrmDiferirFacturaControladorEnum.PR_VISIBLE
                                .getValue(), true);
            }
            else {
                parametros.put(FrmDiferirFacturaControladorEnum.PR_VISIBLE
                                .getValue(), true);
            }

        }
        else {
            parametros.put(FrmDiferirFacturaControladorEnum.PR_VISIBLE
                            .getValue(), false);
        }
        parametros.put("PR_DIRECCION", SessionUtil.getCompaniaIngreso()
                        .getDireccion());
        parametros.put("PR_LEYENDAFACTURA", SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.LEYENDA_FACTURA
                                                        .getValue()),
                                        " ")
                        .toString());

        parametros.put("PR_SF_ESLOGAN_PIE",
                        getParametro("SF ESLOGAN PIE", true));

        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        parametros.put("PR_NITCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNit());

        parametros.put("PR_TITULO",
                        getParametro("SF ESLOGAN TITULO", true));

        parametros.put("PR_CIUDADCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getCiudad());
        parametros.put("PR_VISIBLEGRUPO", true);
        
    }

    public StreamedContent generarInforme(String reporte, Registro registro,
        Map<String, Object> parametros, Map<String, Object> reemplazar,
        FORMATOS formato) {

        archivoDescarga = null;
        try {
            if (reporte.equals(FrmDiferirFacturaControladorEnum.INFORME001489
                            .getValue())) {

                parametrosInforme0001489(parametros, registro);
            }

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public ResourceBundle getIdioma() {
        return idioma;
    }

    public void setIdioma(ResourceBundle idioma) {
        this.idioma = idioma;
    }

}
