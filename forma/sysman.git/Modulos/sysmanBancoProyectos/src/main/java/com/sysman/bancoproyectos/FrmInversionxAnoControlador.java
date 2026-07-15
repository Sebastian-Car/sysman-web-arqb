package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmInversionxAnoControladorEnum;
import com.sysman.bancoproyectos.enums.FrmInversionxAnoControladorUrlEnum;
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
 * @version 1, 27/08/2015
 *
 * @author spina
 * @version 2, 20/09/2017 - se refactoriza para dss, depuracion sonar
 * y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmInversionxAnoControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String anio;
    private String anioF;
    private String nivel;
    private String descripcion;
    private String nombreDescripcion;
    private List<Registro> listaAno;
    private List<Registro> listaAno1;
    private List<Registro> listaNIVEL;
    private List<Registro> listaDESCRIPCION;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmInversionxAno
     */
    public FrmInversionxAnoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_INVERSIONX_ANO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmInversionxAnoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        try
        {
            anio = ejbSysmanUtil.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL", modulo,
                            new Date(), true);
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmPlanIndxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        anioF = String.valueOf(Integer.parseInt(anio) + 3);
        cargarListaano();
        cargarListaano1();
        cargarListaNIVEL();
        cargarListaDESCRIPCION();
        abrirFormulario();
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
                                                            FrmInversionxAnoControladorUrlEnum.URL4310
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
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        try
        {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInversionxAnoControladorUrlEnum.URL4311
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNIVEL()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmInversionxAnoControladorEnum.ANIO.getValue(), anio);
            listaNIVEL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInversionxAnoControladorUrlEnum.URL4312
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDESCRIPCION()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmInversionxAnoControladorEnum.NIVEL.getValue(), nivel);
        param.put(FrmInversionxAnoControladorEnum.ANIO.getValue(), anio);
        param.put(FrmInversionxAnoControladorEnum.ANIO_FINAL.getValue(), anioF);

        try
        {
            listaDESCRIPCION = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInversionxAnoControladorUrlEnum.URL4313
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void generarInforme(FORMATOS formato)
    {

        Map<String, Object> parametros = new HashMap<>();
        String nombreReporte = "000195RPTINVERSIONDISTRIBUIDAANO";
        try
        {
            parametros.put("PR_STRSQL", getQuery(nombreReporte));
            parametros.put("PR_ANIO", anio);
            parametros.put("PR_ANIO1",
                            String.valueOf(Integer.parseInt(anio) + 3));
            parametros.put("PR_NIVEL", nivel);
            parametros.put("PR_DESCRIPCION", nombreDescripcion);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SysmanException | JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmInversionxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private String getQuery(String nombreReporte)
    {
        HashMap<String, Object> reemplazar = new HashMap<>();
        String query;
        reemplazar.put("anioInicial", anio);
        reemplazar.put("anioFinal", anioF);
        reemplazar.put("nivel", nivel);
        reemplazar.put("descripcion", nombreDescripcion);
        query = Reporteador.resuelveConsulta(nombreReporte,
                        Integer.parseInt(modulo), reemplazar);
        return query;
    }

    public void oprimirComando77()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        descripcion = null;
        cargarListaano1();
        if (anio != null)
        {
            anioF = String.valueOf(Integer.parseInt(anio) + 3);
        }
        cargarListaDESCRIPCION();
        cargarListaNIVEL();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1()
    {
        // <CODIGO_DESARROLLADO>
        if ((anioF != null)
            && ((Integer.parseInt(anioF) - Integer.parseInt(anio)) < 0))
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2316"));
            anioF = null;

        }
        descripcion = null;
        cargarListaDESCRIPCION();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNIVEL()
    {
        // <CODIGO_DESARROLLADO>
        descripcion = null;
        cargarListaDESCRIPCION();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDESCRIPCION()
    {
        // <CODIGO_DESARROLLADO>
        nombreDescripcion = descripcion;

        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getAnioF()
    {
        return anioF;
    }

    public void setAnioF(String anioF)
    {
        this.anioF = anioF;
    }

    public String getNivel()
    {
        return nivel;
    }

    public void setNivel(String nivel)
    {
        this.nivel = nivel;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaNIVEL()
    {
        return listaNIVEL;
    }

    public void setListaNIVEL(List<Registro> listaNIVEL)
    {
        this.listaNIVEL = listaNIVEL;
    }

    public List<Registro> getListaDESCRIPCION()
    {
        return listaDESCRIPCION;
    }

    public void setListaDESCRIPCION(List<Registro> listaDESCRIPCION)
    {
        this.listaDESCRIPCION = listaDESCRIPCION;
    }

    public String getNombreDescripcion()
    {
        return nombreDescripcion;
    }

    public void setNombreDescripcion(String nombreDescripcion)
    {
        this.nombreDescripcion = nombreDescripcion;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }
}