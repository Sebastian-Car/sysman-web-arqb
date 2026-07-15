package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmInversionCuatrenioControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
 * @author esarmiento
 * @version 1, 22/09/2015
 *
 * @author spina
 * @version 2, 19/09/2017 - se refactoriza para dss, depuracion y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmInversionCuatrenioControlador extends BeanBaseModal
{

    private String compania;
    private String modulo;
    private String anio1;
    private String anio2;
    private List<Registro> listaAno;
    private List<Registro> listaAno1;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmInversionCuatrenioControlador
     */
    public FrmInversionCuatrenioControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_INVERSION_CUATRENIO_CONTROLADOR
                            .getCodigo();
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmInversionCuatrenioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        try
        {
            anio1 = ejbSysmanUtil.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL", modulo, new Date(),
                            true);

            anio2 = String.valueOf(Integer.parseInt(anio1) + 3);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarListaano();
        cargarListaano1();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // METODO_NO_IMPLEMENTADO
    }

    public void cargarListaano()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInversionCuatrenioControladorUrlEnum.URL4310
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaano1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio1);
        try
        {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInversionCuatrenioControladorUrlEnum.URL4311
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando6()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato)
    {
        if (anio1 == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2324"));
            return;
        }
        if (anio2 == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2326"));
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        String nombreReporte = "000243RPTINVERSIONCUATRENIO";
        try
        {
            parametros.put("PR_ANIO1", anio1);
            parametros.put("PR_ANIO2", anio2);
            parametros.put("PR_STRSQL", getQuery(nombreReporte));

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SysmanException | JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmInversionCuatrenioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private String getQuery(String nombreReporte)
    {
        String query = null;
        String parametro;
        try
        {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA PROCESO FINANCIERO INDEPENDIENTE A SYSMAN",
                            modulo, new Date(), true);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anioInicial", anio1);
            reemplazar.put("anioFinal", anio2);
            if ("SI".equals(parametro))
            {
                // consulta with
                query = Reporteador.resuelveConsulta(SysmanFunciones
                                .concatenar(nombreReporte, "_OP2"),
                                Integer.parseInt(modulo), reemplazar);
            }
            else
            {
                // consulta plan_presupuestal
                query = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);
            }

        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmInversionCuatrenioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        return query;
    }

    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        if (anio2 != null
            && Integer.parseInt(anio1) > Integer.parseInt(anio2))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1785"));
            anio2 = null;

        }
        cargarListaano1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1()
    {
        // <CODIGO_DESARROLLADO>
        if (anio1 != null && anio2 != null
            && Integer.parseInt(anio2) < Integer.parseInt(anio1))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1785"));
            anio2 = null;

        }
        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getAnio1()
    {
        return anio1;
    }

    public void setAnio1(String anio1)
    {
        this.anio1 = anio1;
    }

    public String getAnio2()
    {
        return anio2;
    }

    public void setAnio2(String anio2)
    {
        this.anio2 = anio2;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaano(List<Registro> listaano)
    {
        this.listaAno = listaano;
    }

    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    public void setListaano1(List<Registro> listaano1)
    {
        this.listaAno1 = listaano1;
    }
}