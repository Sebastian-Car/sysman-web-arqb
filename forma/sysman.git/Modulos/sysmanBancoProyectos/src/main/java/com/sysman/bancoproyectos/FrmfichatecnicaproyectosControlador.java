package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.enums.FrmfichatecnicaproyectosControladorEnum;
import com.sysman.bancoproyectos.enums.FrmfichatecnicaproyectosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 21/09/2015
 *
 * @author spina
 * @version 2, 14/09/2017 - se refactoriza para dss, depuracion y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmfichatecnicaproyectosControlador
                extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private final String modulo;
    private final String usuario;
    private String codigoProyecto;
    private String accion;
    private String sector;
    private boolean cumpleGeneral;
    private String seccion;
    private String observacion;
    private List<Registro> listaSECCION;
    private List<Registro> listaSector;
    private boolean existeFicha;
    private boolean muestraRegistro;
    private String menuActual;
    private StreamedContent archivoDescarga;
    private String textoDG;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbBancoProyectoCincoRemote ejbBancoProyectoCinco;

    /**
     * Creates a new instance of FrmfichatecnicaproyectosControlador
     */
    public FrmfichatecnicaproyectosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMFICHATECNICAPROYECTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametros = SessionUtil.getFlash();

            if (parametros != null)
            {
                codigoProyecto = (String) parametros.get("codigoProyecto");
                accion = (String) parametros.get("accion");
            }

            menuActual = SessionUtil.getMenuActual();
            if (menuActual == null)
            {
                SessionUtil.redireccionarMenu();
            }
            if ("52020102".equals(menuActual) || "52020402".equals(menuActual))
            {
                muestraRegistro = false;
            }
            if ("52020101".equals(menuActual))
            {
                muestraRegistro = true;
            }

        }
        catch (Exception ex)
        {
            Logger.getLogger(
                            FrmfichatecnicaproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }

    }

    @PostConstruct
    public void init()
    {
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaSECCION();

        cargarListaSector();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmfichatecnicaproyectosControladorUrlEnum.URL2390
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.PROYECTO.getName(),
                        codigoProyecto);
        parametrosListado.put(GeneralParameterEnum.SECTOR.getName(), sector);
        parametrosListado.put(FrmfichatecnicaproyectosControladorEnum.SECCION
                        .getValue(), seccion);

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmfichatecnicaproyectosControladorUrlEnum.URL2398
                                                        .getValue());
    }

    public void cargarListaSECCION()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.SECTOR.getName(), sector);
        try
        {
            listaSECCION = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfichatecnicaproyectosControladorUrlEnum.URL2391
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSector()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaSector = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfichatecnicaproyectosControladorUrlEnum.URL2392
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarSECCION()
    {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCUMPLEGENERAL()
    {
        try
        {
            // <CODIGO_DESARROLLADO>
            Registro reg = new Registro();
            reg.getCampos().put(FrmfichatecnicaproyectosControladorEnum.CUMPLE
                            .getValue(), cumpleGeneral);
            reg.getLlave().put(
                            FrmfichatecnicaproyectosControladorEnum.KEY_COMPANIA
                                            .getValue(),
                            compania);
            reg.getLlave().put(
                            FrmfichatecnicaproyectosControladorEnum.KEY_PROYECTO
                                            .getValue(),
                            codigoProyecto);
            reg.getLlave().put(
                            FrmfichatecnicaproyectosControladorEnum.KEY_SECTOR
                                            .getValue(),
                            sector);
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmfichatecnicaproyectosControladorUrlEnum.URL2393
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex)
        {
            Logger.getLogger(
                            FrmfichatecnicaproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cambiarOBSERVACION()
    {
        try
        {
            // <CODIGO_DESARROLLADO>
            Registro reg = new Registro();
            reg.getCampos().put(
                            FrmfichatecnicaproyectosControladorEnum.OBSERVACION
                                            .getValue(),
                            observacion);
            reg.getLlave().put(
                            FrmfichatecnicaproyectosControladorEnum.KEY_COMPANIA
                                            .getValue(),
                            compania);
            reg.getLlave().put(
                            FrmfichatecnicaproyectosControladorEnum.KEY_PROYECTO
                                            .getValue(),
                            codigoProyecto);
            reg.getLlave().put(
                            FrmfichatecnicaproyectosControladorEnum.KEY_SECTOR
                                            .getValue(),
                            sector);
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmfichatecnicaproyectosControladorUrlEnum.URL2394
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex)
        {
            Logger.getLogger(
                            FrmfichatecnicaproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cambiarSector()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PROYECTO.getName(), codigoProyecto);
        param.put(GeneralParameterEnum.SECTOR.getName(), sector);
        try
        {
            List<Registro> listaFichas = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfichatecnicaproyectosControladorUrlEnum.URL2395
                                                                            .getValue())
                                            .getUrl(), param));

            if (listaFichas.isEmpty())
            {
                existeFicha = true;
                textoDG = idioma.getString("TB_TB3574").replace("s$sector$s",
                                sector);
                return;
            }
            else
            {
                existeFicha = false;
                reasignarOrigen();
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        seccion = null;
        cargarInformacion(sector);
        cargarListaSECCION();

        // </CODIGO_DESARROLLADO>
    }

    public void aceptardialogoCrearFicha()
    {
        // <CODIGO_DESARROLLADO>
        existeFicha = false;
        try
        {
            ejbBancoProyectoCinco.crearFichaTecnica(compania, codigoProyecto,
                            sector, usuario);
        }
        catch (SystemException ex)
        {
            Logger.getLogger(
                            FrmfichatecnicaproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        seccion = null;
        cargarInformacion(sector);
        cargarListaSECCION();
        reasignarOrigen();
    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(FORMATOS formato)
    {
        try
        {
            HashMap<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoProyecto", codigoProyecto);
            reemplazar.put("sector", sector);
            String responsable = ejbSysmanUtil.consultarParametro(compania,
                            idioma.getString("TB_TB3577"),
                            SessionUtil.getModulo(),
                            new Date(), true);

            parametros.put(idioma.getString("TB_TB3578"),
                            responsable);
            parametros.put("PR_COMPANIA", compania);
            Reporteador.resuelveConsulta("000202FICHATECNICAPROY",
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("000202FICHATECNICA",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException | JRException | IOException | SystemException ex)
        {
            Logger.getLogger(
                            FrmfichatecnicaproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
        }

    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        if ("v".equals(accion))
        {
            muestraRegistro = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.PROYECTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SECTOR.getName());

        registro.getCampos()
                        .remove(FrmfichatecnicaproyectosControladorEnum.SECCION
                                        .getValue());
        registro.getCampos().remove(FrmfichatecnicaproyectosControladorEnum.ITEM
                        .getValue());

        registro.getCampos()
                        .put(FrmfichatecnicaproyectosControladorEnum.KEY_COMPANIA
                                        .getValue(),
                                        compania);
        registro.getCampos()
                        .put(FrmfichatecnicaproyectosControladorEnum.KEY_PROYECTO
                                        .getValue(),
                                        codigoProyecto);

        registro.getCampos()
                        .put(FrmfichatecnicaproyectosControladorEnum.KEY_SECTOR
                                        .getValue(),
                                        sector);
        registro.getCampos()
                        .put(FrmfichatecnicaproyectosControladorEnum.KEY_CODIGO_DET
                                        .getValue(),
                                        registro.getCampos()
                                                        .get(FrmfichatecnicaproyectosControladorEnum.CODIGO_DET
                                                                        .getValue()));
        registro.getCampos()
                        .remove(FrmfichatecnicaproyectosControladorEnum.CODIGO_DET
                                        .getValue());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cargarInformacion(String sector)
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.SECTOR.getName(), sector);
        param.put(GeneralParameterEnum.PROYECTO.getName(), codigoProyecto);

        try
        {
            Registro info = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfichatecnicaproyectosControladorUrlEnum.URL2397
                                                                            .getValue())
                                            .getUrl(), param));
            cumpleGeneral = (boolean) info.getCampos()
                            .get(FrmfichatecnicaproyectosControladorEnum.CUMPLE
                                            .getValue());
            observacion = SysmanFunciones
                            .nvl(info.getCampos()
                                            .get(FrmfichatecnicaproyectosControladorEnum.OBSERVACION
                                                            .getValue()),
                                            "")
                            .toString();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos()
    {
        // HEREDADO DEL BEAN BASE
    }

    @Override
    public void asignarValoresRegistro()
    {
        // HEREDADO DEL BEAN BASE
    }

    public List<Registro> getListaSector()
    {
        return listaSector;
    }

    public void setListaSector(List<Registro> listaSector)
    {
        this.listaSector = listaSector;
    }

    public List<Registro> getListaSECCION()
    {
        return listaSECCION;
    }

    public void setListaSECCION(List<Registro> listaSECCION)
    {
        this.listaSECCION = listaSECCION;
    }

    public String getCodigoProyecto()
    {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto)
    {
        this.codigoProyecto = codigoProyecto;
    }

    public String getSector()
    {
        return sector;
    }

    public void setSector(String sector)
    {
        this.sector = sector;
    }

    public boolean getCumpleGeneral()
    {
        return cumpleGeneral;
    }

    public void setCumpleGeneral(boolean cumpleGeneral)
    {
        this.cumpleGeneral = cumpleGeneral;
    }

    public String getSeccion()
    {
        return seccion;
    }

    public void setSeccion(String seccion)
    {
        this.seccion = seccion;
    }

    public String getObservacion()
    {
        return observacion;
    }

    public void setObservacion(String observacion)
    {
        this.observacion = observacion;
    }

    public boolean isExisteFicha()
    {
        return existeFicha;
    }

    public void setExisteFicha(boolean existeFicha)
    {
        this.existeFicha = existeFicha;
    }

    public boolean isMuestraRegistro()
    {
        return muestraRegistro;
    }

    public void setMuestraRegistro(boolean muestraRegistro)
    {
        this.muestraRegistro = muestraRegistro;
    }

    public String getAccion()
    {
        return accion;
    }

    public void setAccion(String accion)
    {
        this.accion = accion;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getTextoDG()
    {
        return textoDG;
    }

    public void setTextoDG(String textoDG)
    {
        this.textoDG = textoDG;
    }

}
