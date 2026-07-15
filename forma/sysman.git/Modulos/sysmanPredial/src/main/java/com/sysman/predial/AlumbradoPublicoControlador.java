package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.AlumbradoPublicoControladorEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
 * @author jrodriguezr
 * @version 1, 27/05/2016 16:57:34 -- Modificado por jrodriguezr
 * @author jcrodriguez
 * @version 2, 27/05/2017 16:57:34 -- Modificado por jcrodriguez
 * DESCRIPCION depuracion del controlador deacuerdo al estandar
 * Refactoring
 * @version 3, 26/07/2017 16:57:34 -- se agregan los
 * reemplazos=>COMPANIA,concepto=>CONCEPTO DE ALUMBRADO
 */
@ManagedBean
@ViewScoped
public class AlumbradoPublicoControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    /**
     * variable estado indicador
     */
    private boolean causacion;
    /**
     * variable estado indicador
     */
    private boolean recaudo;
    /**
     * variable fecha que almacena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * variable fecha que almacena la fecha final
     */
    private Date fechaFinal;
    /**
     * variable que almacena el archivo de desacarga
     */
    private StreamedContent archivoDescarga;
    /**
     * variable estado que almacena la fecha bloqueada
     */
    private boolean fechaBloquea;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of AlumbradoPublicoControlador
     */
    public AlumbradoPublicoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {

            numFormulario = GeneralCodigoFormaEnum.ALUMBRADO_PUBLICO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(AlumbradoPublicoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo heredado del bean padre
     */
    @PostConstruct
    public void inicializar()
    {
        recaudo = true;
        abrirFormulario();
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void abrirFormulario()
    {
        fechaInicial = fechaFinal = new Date();
    }

    /**
     * metodo que es llamado al oprimir el boton pdf
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que es llamado al oprimir el boton pdf
     */
    public void oprimirExcel()
    {
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
    }

    /**
     * Evalua que la fecha inicial no sea nula
     *
     * @return verdadero o falso
     */
    private boolean fechaInicial()
    {
        if (recaudo && (fechaInicial == null))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB511"));
        }
        return recaudo && (fechaInicial == null);
    }

    /**
     * Evalua que la fecha final no sea nula
     *
     * @return verdadero o falso
     */
    private boolean fechaFinal()
    {
        if (recaudo && (fechaFinal == null))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB512"));
        }
        return recaudo && (fechaFinal == null);
    }

    /**
     * mwrodo que contiene la logica para generar los reportes en
     * formato pdf y excel
     * 
     * @param formato
     */
    private void generaReporte(FORMATOS formato)
    {
        if (fechaInicial() || fechaFinal())
        {
            return;
        }
        if (SysmanFunciones.validarVariableVacio(conceptoAlumbrado()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3335"));
            return;
        }
        String reporte = causacion
            ? AlumbradoPublicoControladorEnum.REPORTE000825.getValue()
            : AlumbradoPublicoControladorEnum.REPORTE000826.getValue();
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(AlumbradoPublicoControladorEnum.FECHAINICIAL
                            .getValue(), formatearFecha(fechaInicial));
            reemplazar.put(AlumbradoPublicoControladorEnum.FECHAFINAL
                            .getValue(), formatearFecha(fechaFinal));
            reemplazar.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(), compania);

            reemplazar.put(GeneralParameterEnum.CONCEPTO.getName().toLowerCase(), conceptoAlumbrado());

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);
            parametros.put(AlumbradoPublicoControladorEnum.PR_FECHAFINAL
                            .getValue(), SysmanFunciones
                                            .convertirAFechaCadena(fechaFinal,
                                                            AlumbradoPublicoControladorEnum.FORMATOFECHA
                                                                            .getValue()));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            StringBuilder concatenar = new StringBuilder();
            concatenar.append(idioma.getString("MSM_INFORME_NO_EXISTE"));
            concatenar.append("");
            concatenar.append(ex.getMessage());
            concatenar.append("");
            concatenar.append(reporte);
            JsfUtil.agregarMensajeInformativo(concatenar.toString());
            Logger.getLogger(AlumbradoPublicoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que consulta el parametro del sistem CONCEPTO DE
     * ALUMBRADO
     * 
     * @return
     */
    private String conceptoAlumbrado()
    {
        String concepto;
        try
        {
            concepto = ejbSysmanUtil.consultarParametro(compania, AlumbradoPublicoControladorEnum.CONCEPTO.getValue(),
                            modulo, new Date(), false);
            return concepto;
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return null;
    }

    public String formatearFecha(Date fecha)
    {
        if (fecha == null)
        {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "TO_DATE('" + sdf.format(fecha) + "','DD/MM/YYYY')";
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarTxtFechaInicial()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que es llamado al cambiar el cheeck de causacion
     */
    public void cambiarChkCausacion()
    {
        fechaBloquea = false;
        if (causacion)
        {
            recaudo = false;

            fechaFinal = new Date();
            fechaBloquea = true;
        }
        else
        {
            recaudo = true;
        }
    }

    /**
     * metodo que es llamado al cambiar el cheeck de recaudo
     */
    public void cambiarChkRecaudo()
    {
        fechaBloquea = false;
        if (recaudo)
        {
            causacion = false;
        }
        else
        {
            fechaBloquea = true;
            causacion = true;
        }
    }

    /**
     * metodos get y set
     * 
     * @return
     */
    public boolean getCausacion()
    {
        return causacion;
    }

    public void setCausacion(boolean causacion)
    {
        this.causacion = causacion;
    }

    public boolean getRecaudo()
    {
        return recaudo;
    }

    public void setRecaudo(boolean recaudo)
    {
        this.recaudo = recaudo;
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

    public boolean isFechaBloquea()
    {
        return fechaBloquea;
    }

    public void setFechaBloquea(boolean fechaBloquea)
    {
        this.fechaBloquea = fechaBloquea;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }
}
