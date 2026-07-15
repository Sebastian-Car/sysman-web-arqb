package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.enums.FrmPlanIndxNivelControladorEnum;
import com.sysman.bancoproyectos.enums.FrmPlanIndxNivelControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @version 1, 24/08/2015
 * 
 * @author jcrodriguez,Refactoring, depuracion y creacion de dss
 * @version 2, 20/09/2017
 */
@ManagedBean
@ViewScoped

public class FrmPlanIndxNivelControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private final String municipioCompania;
    private final String departamentoCompania;
    private String anio;
    private String nivel;
    private List<Registro> listaAno;
    private List<Registro> listaNivel;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    @EJB
    private EjbBancoProyectoCincoRemote ejbBancoProyectoCinco;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of FrmPlanIndxNivelControlador
     */
    public FrmPlanIndxNivelControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        municipioCompania = SessionUtil.getCompaniaIngreso().getCiudad().toUpperCase();
        departamentoCompania = SessionUtil.getCompaniaIngreso()
                        .getDepartamento().toUpperCase();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_PLAN_INDX_NIVEL_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmPlanIndxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    @PostConstruct
    public void init()
    {

        anio = getParametro("VIGENCIA GUBERNAMENTAL ACTUAL", true);
        cargarListaano();
        cargarListanivel();
        abrirFormulario();
    }

    public void cargarListaano()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmPlanIndxNivelControladorUrlEnum.URL4471.getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListanivel()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), anio);

        try
        {
            listaNivel = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmPlanIndxNivelControladorUrlEnum.URL4826.getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAno()
    {
        nivel = null;
        cargarListanivel();
    }

    public void oprimirAceptar()
    {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);

    }

    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtilRemote.consultarParametro(compania, nombre, modulo, new Date(), indMayus);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    private void generarInforme(FORMATOS formato)
    {
        String dependencia;
        String tituloPlanDesarrollo;
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            tituloPlanDesarrollo = getParametro("TITULO PLAN DE DESARROLLO", true);
            dependencia = getParametro("DEPENDENCIA DE BANCO DE PROYECTOS", true);
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(FrmPlanIndxNivelControladorEnum.NIVEL.getValue().toLowerCase(), nivel);
            reemplazar.put(FrmPlanIndxNivelControladorEnum.ANIO.getValue().toLowerCase(), anio);
            Reporteador.resuelveConsulta(FrmPlanIndxNivelControladorEnum.REPORTE000178.getValue(),
                            Integer.parseInt(modulo), reemplazar, parametros);
            long contar = service.getConteoConsulta(parametros.get(FrmPlanIndxNivelControladorEnum.PR_STRSQL.getValue()).toString());
            if (contar > 0)
            {

                parametros.put(FrmPlanIndxNivelControladorEnum.PR_DEPARTAMENTOCOMPANIA.getValue(), departamentoCompania);
                parametros.put(FrmPlanIndxNivelControladorEnum.PR_CIUDADCOMPANIA.getValue(), municipioCompania);
                parametros.put(FrmPlanIndxNivelControladorEnum.PR_DEPENDENCIA_DE_BANCO_DE_PROYECTOS.getValue(), dependencia);
                parametros.put(FrmPlanIndxNivelControladorEnum.PR_TITULO_PLAN_DE_DESARROLLO.getValue(),
                                SysmanFunciones.concatenar(idioma.getString("TB_TB3627"),
                                                tituloPlanDesarrollo, "' ", this.anio, " - ",
                                                String.valueOf(Integer.parseInt(this.anio) + 3)));
                archivoDescarga = JsfUtil.exportarStreamed(FrmPlanIndxNivelControladorEnum.REPORTE000178.getValue(),
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString(FrmPlanIndxNivelControladorEnum.TG_NO_EXISTE.getValue()));
            }

        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(FrmPlanIndxNivelControladorEnum.MSM_INFORME_NO_EXISTE.getValue()), " ", ex.getMessage(), " ",
                            FrmPlanIndxNivelControladorEnum.REPORTE000178.getValue()));
            Logger.getLogger(FrmPlanIndxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException
                        | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), ex.getMessage()));
            Logger.getLogger(FrmPlanIndxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarInformeCritico(FORMATOS formato)
    {

        Map<String, Object> parametros = new HashMap<>();
        String tituloPlanDesarrollo = "";
        String dependencia = "";
        try
        {

            tituloPlanDesarrollo = getParametro("TITULO PLAN DE DESARROLLO", true);
            dependencia = getParametro("DEPENDENCIA DE BANCO DE PROYECTOS", true);

            parametros.put(FrmPlanIndxNivelControladorEnum.PR_NIVEL.getValue(),
                            service.buscarEnLista(nivel, FrmPlanIndxNivelControladorEnum.DIGITOS.getValue(),
                                            GeneralParameterEnum.DESCRIPCION.getName(), listaNivel));
            parametros.put(FrmPlanIndxNivelControladorEnum.PR_TITULO_PLAN_DE_DESARROLLO.getValue(),
                            SysmanFunciones.concatenar(idioma.getString("TB_TB3627"), " '", tituloPlanDesarrollo, "' \n ", anio,
                                            " - ",
                                            String.valueOf(Integer.parseInt(anio) + 3)));
            parametros.put(FrmPlanIndxNivelControladorEnum.PR_DEPENDENCIA_DE_BANCO_DE_PROYECTOS.getValue(),
                            dependencia);
            parametros.put(FrmPlanIndxNivelControladorEnum.PR_CIUDADCOMPANIA.getValue(), municipioCompania);
            parametros.put(FrmPlanIndxNivelControladorEnum.PR_DEPARTAMENTOCOMPANIA.getValue(), departamentoCompania);
            parametros.put(FrmPlanIndxNivelControladorEnum.PR_DEPARTAMENTOCOMPANIA.getValue(), departamentoCompania);

            HashMap<String, Object> reemplazar = new HashMap<>();
            if (generarConsultaCriticos() == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2415"));
                return;
            }
            else
            {
                reemplazar.put("query", generarConsultaCriticos());
            }

            reemplazar.put(FrmPlanIndxNivelControladorEnum.NIVEL.getValue().toLowerCase(), nivel);
            reemplazar.put(FrmPlanIndxNivelControladorEnum.ANIO.getValue().toLowerCase(), anio);
            Reporteador.resuelveConsulta(FrmPlanIndxNivelControladorEnum.REPORTE000182.getValue(),
                            Integer.parseInt(modulo), reemplazar,
                            parametros);
            Long contar = service.getConteoConsulta(parametros.get(FrmPlanIndxNivelControladorEnum.PR_STRSQL.getValue()).toString());
            if (contar > 0)
            {
                archivoDescarga = JsfUtil.exportarStreamed(FrmPlanIndxNivelControladorEnum.REPORTE000182.getValue(),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString(FrmPlanIndxNivelControladorEnum.TG_NO_EXISTE.getValue()));
            }

        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(FrmPlanIndxNivelControladorEnum.MSM_INFORME_NO_EXISTE.getValue()), " ", ex.getMessage(),
                            " ",
                            FrmPlanIndxNivelControladorEnum.REPORTE000178.getValue()));
            Logger.getLogger(FrmPlanIndxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(idioma
                            .getString(Constantes.MSM_TRANS_INTERRUMPIDA), ex.getMessage()));
            Logger.getLogger(FrmPlanIndxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String generarConsultaCriticos()
    {
        try
        {
            return ejbBancoProyectoCinco.generarConsultaCriticos(compania, Integer.parseInt(anio));
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    public void oprimirgrafica()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        Map<String, Object> parametros = new HashMap<>();
        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(FrmPlanIndxNivelControladorEnum.NIVEL.getValue().toLowerCase(), nivel);
            reemplazar.put(FrmPlanIndxNivelControladorEnum.ANIO.getValue().toLowerCase(), anio);
            Reporteador.resuelveConsulta(FrmPlanIndxNivelControladorEnum.REPORTE000217.getValue(),
                            Integer.parseInt(modulo), reemplazar, parametros);
            parametros.put(FrmPlanIndxNivelControladorEnum.PR_NIVEL.getValue(),
                            service.buscarEnLista(nivel, FrmPlanIndxNivelControladorEnum.DIGITOS.getValue(),
                                            GeneralParameterEnum.DESCRIPCION.getName(), listaNivel));
            parametros.put(FrmPlanIndxNivelControladorEnum.PR_VIGENCIA.getValue(), this.anio);
            long contar = service.getConteoConsulta(parametros.get(FrmPlanIndxNivelControladorEnum.PR_STRSQL.getValue()).toString());
            if (contar > 0)
            {
                archivoDescarga = JsfUtil.exportarStreamed(FrmPlanIndxNivelControladorEnum.REPORTE000217.getValue(),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.PDF);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString(FrmPlanIndxNivelControladorEnum.TG_NO_EXISTE.getValue()));
            }
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(FrmPlanIndxNivelControladorEnum.MSM_INFORME_NO_EXISTE.getValue()), " ", ex.getMessage(), " ",
                            FrmPlanIndxNivelControladorEnum.REPORTE000178.getValue()));
            Logger.getLogger(FrmPlanIndxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), ex.getMessage()));
            Logger.getLogger(FrmPlanIndxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirCriticos()
    {
        archivoDescarga = null;
        generarInformeCritico(FORMATOS.PDF);
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getNivel()
    {
        return nivel;
    }

    public void setNivel(String nivel)
    {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaano)
    {
        this.listaAno = listaano;
    }

    public List<Registro> getListaNivel()
    {
        return listaNivel;
    }

    public void setListaNivel(List<Registro> listanivel)
    {
        this.listaNivel = listanivel;
    }

}