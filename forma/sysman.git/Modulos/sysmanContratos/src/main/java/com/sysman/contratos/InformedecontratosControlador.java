
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 20/11/2015
 * 
 * @author asana
 * @version 2, 09/08/2017 Se realiza proceso de refactoring
 */
@ManagedBean
@ViewScoped
public class InformedecontratosControlador extends BeanBaseModal
{

    private final String compania;
    private final String moduloContratos;
    private String informe;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of InformedecontratosControlador
     */
    public InformedecontratosControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.INFORMEDECONTRATOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        moduloContratos = SessionUtil.getModulo();
        informe = "000389InformeDeContratos";
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(InformedecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
        fechaInicial = new Date();
        fechaFinal = new Date();

    }

    public boolean validarFechas(Date fechaInicial, Date fechaFinal)
    {
        return fechaInicial.compareTo(fechaFinal) <= 0;
    }

    public void oprimircmdPdf()
    {
        if (fechaInicial != null && fechaFinal != null)
        {
            if (!validarFechas(fechaInicial, fechaFinal))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3371"));
                return;
            }
            generarReporteInformeDeContratos(ReportesBean.FORMATOS.PDF);
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmbExcel()
    {
        if (fechaInicial != null && fechaFinal != null)
        {
            if (!validarFechas(fechaInicial, fechaFinal))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3371"));
                return;
            }
            generarReporteInformeDeContratos(ReportesBean.FORMATOS.EXCEL97);
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    private void generarReporteInformeDeContratos(FORMATOS formato)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            String strSql = Reporteador.resuelveConsulta(
                            informe,
                            Integer.parseInt(moduloContratos), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_PERIODO",
                            SysmanFunciones.concatenar("Periodo rendición de ",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial,
                                                            "dd/MM/yyyy"),
                                            " a ",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaFinal,
                                                            "dd/MM/yyyy")));
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "yyyy"));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "yyyy"));

            try
            {
                parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",
                                ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "NOMBRE REPRESENTANTE LEGAL",
                                                moduloContratos,
                                                new Date(), true));
            }
            catch (SystemException e1)
            {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }

            try
            {
                parametros.put("PR_FIRMA_DEL_ASESOR_JURIDICO",
                                ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "FIRMA DEL ASESOR JURIDICO",
                                                moduloContratos, new Date(),
                                                true));
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {

            JsfUtil.agregarMensajeInformativo(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            Constantes.MSM_INFORME_NO_EXISTE),
                                            " ", ex.getMessage()));
            Logger.getLogger(InformedecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | ParseException | JRException
                        | IOException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), informe,
                            ex.getMessage()));
            Logger.getLogger(InformedecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

}