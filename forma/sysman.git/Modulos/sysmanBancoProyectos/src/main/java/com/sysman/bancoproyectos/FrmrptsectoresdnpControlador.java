package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmrptsectoresdnpControladorEnum;
import com.sysman.bancoproyectos.enums.FrmrptsectoresdnpControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 15/10/2015
 * 
 * @author jcrodriguez,Refactoring, depuracion y creacion de dss,
 * cambio de combos sencillos por combos grandes,tambien en las dos
 * consultas para los reportes se filtra por codigo y no por nombre
 * @version 2, 21/09/2017
 */
@ManagedBean
@ViewScoped

public class FrmrptsectoresdnpControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private boolean conSector;
    private String sectorDnp;
    private String sectorDnpf;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCmbSectorDNP;
    private RegistroDataModelImpl listaCmBSectorDNPF;
    private String sectorDnpNombre;
    private String sectorDnpfNombre;

    /**
     * Creates a new instance of FrmrptsectoresdnpControlador
     */
    public FrmrptsectoresdnpControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMRPTSECTORESDNP_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmrptsectoresdnpControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaCmbSectorDNP();
        cargarListaCmBSectorDNPF();
        abrirFormulario();
    }

    public void cargarListaCmbSectorDNP()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmrptsectoresdnpControladorUrlEnum.URL2851.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbSectorDNP = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        FrmrptsectoresdnpControladorEnum.CODIGODNP.getValue());
    }

    private String validarCadenaParametro(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    public void seleccionarFilaCmbSectorDNP(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        sectorDnp = validarCadenaParametro(registroAux.getCampos(), FrmrptsectoresdnpControladorEnum.CODIGODNP.getValue());
        sectorDnpNombre = validarCadenaParametro(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
        sectorDnpfNombre = null;
        sectorDnpf = null;
        cargarListaCmBSectorDNPF();
    }

    public void cargarListaCmBSectorDNPF()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmrptsectoresdnpControladorUrlEnum.URL3880.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), sectorDnp);
        listaCmBSectorDNPF = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        FrmrptsectoresdnpControladorEnum.CODIGODNP.getValue());

    }

    public void seleccionarFilaCmBSectorDNPF(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        sectorDnpf = validarCadenaParametro(registroAux.getCampos(), FrmrptsectoresdnpControladorEnum.CODIGODNP.getValue());
        sectorDnpfNombre = validarCadenaParametro(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
    }

    public void oprimircmbPdf()
    {

        if ((sectorDnp != null) && (sectorDnpf != null))
        {
            archivoDescarga = null;
            if (conSector)
            {
                generarReporteRptSectoresDnpSubsectores(
                                ReportesBean.FORMATOS.PDF);
            }
            else
            {
                generarReporteRptSectoresDnp(ReportesBean.FORMATOS.PDF);
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmbExcel()
    {
        if ((sectorDnp != null) && (sectorDnpf != null))
        {
            if (conSector)
            {
                generarReporteRptSectoresDnpSubsectores(
                                ReportesBean.FORMATOS.EXCEL);
            }
            else
            {
                generarReporteRptSectoresDnp(ReportesBean.FORMATOS.EXCEL);
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    private void generarReporteRptSectoresDnp(FORMATOS formatos)
    {
        try
        {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(), compania);
            reemplazar.put("sectorDnp", sectorDnp);
            reemplazar.put("sectorDnpf", sectorDnpf);
            Map<String, Object> parametros = new HashMap<>();

            Reporteador.resuelveConsulta(FrmrptsectoresdnpControladorEnum.REPORTE000292.getValue(),
                            Integer.parseInt(modulo), reemplazar, parametros);

            long contar = service.getConteoConsulta(parametros.get(FrmrptsectoresdnpControladorEnum.PR_STRSQL.getValue()).toString());
            if (contar > 0)
            {
                archivoDescarga = JsfUtil.exportarStreamed(FrmrptsectoresdnpControladorEnum.REPORTE000292.getValue(),
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString(FrmrptsectoresdnpControladorEnum.TG_NO_EXISTE.getValue()));
            }
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ",
                            FrmrptsectoresdnpControladorEnum.REPORTE000292.getValue()));
            Logger.getLogger(FrmrptsectoresdnpControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex)
        {
            Logger.getLogger(FrmrptsectoresdnpControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(FrmrptsectoresdnpControladorEnum.MSM_TRANS_INTERRUMPIDA.getValue()), ex.getMessage()));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void generarReporteRptSectoresDnpSubsectores(FORMATOS formatos)
    {
        try
        {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(), compania);
            reemplazar.put("sectorDnp", sectorDnp);
            reemplazar.put("sectorDnpf", sectorDnpf);
            Map<String, Object> parametros = new HashMap<>();

            Reporteador.resuelveConsulta(FrmrptsectoresdnpControladorEnum.REPORTE000293.getValue(),
                            Integer.parseInt(modulo), reemplazar, parametros);

            long contar = service.getConteoConsulta(parametros.get(FrmrptsectoresdnpControladorEnum.PR_STRSQL.getValue()).toString());
            if (contar > 0)
            {
                archivoDescarga = JsfUtil.exportarStreamed(
                                FrmrptsectoresdnpControladorEnum.REPORTE000293.getValue(), parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString(FrmrptsectoresdnpControladorEnum.TG_NO_EXISTE.getValue()));
            }
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ",
                            FrmrptsectoresdnpControladorEnum.REPORTE000293.getValue()));
            Logger.getLogger(FrmrptsectoresdnpControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex)
        {
            Logger.getLogger(FrmrptsectoresdnpControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(FrmrptsectoresdnpControladorEnum.MSM_TRANS_INTERRUMPIDA.getValue()), ex.getMessage()));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    public boolean getConSector()
    {
        return conSector;
    }

    public void setConSector(boolean conSector)
    {
        this.conSector = conSector;
    }

    public String getSectorDnp()
    {
        return sectorDnp;
    }

    public void setSectorDnp(String sectorDnp)
    {
        this.sectorDnp = sectorDnp;
    }

    public String getSectorDnpf()
    {
        return sectorDnpf;
    }

    public void setSectorDnpf(String sectorDnpf)
    {
        this.sectorDnpf = sectorDnpf;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCmbSectorDNP()
    {
        return listaCmbSectorDNP;
    }

    public void setListaCmbSectorDNP(RegistroDataModelImpl listaCmbSectorDNP)
    {
        this.listaCmbSectorDNP = listaCmbSectorDNP;
    }

    public RegistroDataModelImpl getListaCmBSectorDNPF()
    {
        return listaCmBSectorDNPF;
    }

    public void setListaCmBSectorDNPF(RegistroDataModelImpl listaCmBSectorDNPF)
    {
        this.listaCmBSectorDNPF = listaCmBSectorDNPF;
    }

    public String getSectorDnpNombre()
    {
        return sectorDnpNombre;
    }

    public void setSectorDnpNombre(String sectorDnpNombre)
    {
        this.sectorDnpNombre = sectorDnpNombre;
    }

    public String getSectorDnpfNombre()
    {
        return sectorDnpfNombre;
    }

    public void setSectorDnpfNombre(String sectorDnpfNombre)
    {
        this.sectorDnpfNombre = sectorDnpfNombre;
    }

}