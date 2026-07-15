package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.InformesProyectosPlanDesarrolloControladorEnum;
import com.sysman.bancoproyectos.enums.InformesProyectosPlanDesarrolloControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jacelas
 * @version 1, 22/08/2015
 * 
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 25/09/2017
 */
@ManagedBean
@ViewScoped

public class InformesProyectosPlanDesarrolloControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String proyectoInicial;
    private String proyectoFinal;
    private String informe;
    private String dimension;
    private String sector;
    private String programa;
    private String subprograma;
    private String estado;
    private String depenedencia;
    private String vigencia;
    private String codDependencia;
    private String anioFiltro;
    private RegistroDataModelImpl listaSector;
    private RegistroDataModelImpl listaDependencia;
    private List<Registro> listaVigenciaInicial;
    private RegistroDataModelImpl listaProyectoinicial;
    private RegistroDataModelImpl listaProyectofinal;
    private RegistroDataModelImpl listaDimension;
    private RegistroDataModelImpl listaPrograma;
    private RegistroDataModelImpl listaSubprograma;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * InformesProyectosPlanDesarrolloControlador
     */
    public InformesProyectosPlanDesarrolloControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORMES_PROYECTOS_PLAN_DESARROLLO_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(InformesProyectosPlanDesarrolloControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        String parametro = getParametro("CONTROLAR DEPENDENCIA EN BPPIM", true);
        if ("SI".equals(parametro)
            && SessionUtil.getNivelUsuario(modulo) != 9)
        {
            proyectoInicial = null;
            proyectoFinal = null;
        }
        else
        {
            proyectoInicial = "00000000";
            proyectoFinal = "99999999";
        }

        informe = "1";
        dimension = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
        sector = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
        programa = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
        subprograma = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
        codDependencia = idioma.getString("TG_TODAS").toUpperCase();
        depenedencia = idioma.getString("TB_TB3646");
        vigencia = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
        estado = "X";
        asignarAnio();
        cargarListaDependencia();
        cargarListaVIGENCIAINICIAL();
        cargarListaProyectoinicial();
        cargarListaProyectofinal();
        cargarListaDimension();
        abrirFormulario();
    }

    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo, new Date(), indMayus);
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    public void cargarLista()
    {
        if (SessionUtil.getUser().getDependencia() == null)
        {
            SessionUtil.setSessionVar(idioma.getString("TB_TB3648"), idioma.getString("TB_TB3647"));
            RequestContext.getCurrentInstance().closeDialog(null);
        }
    }

    public void cargarListaDependencia()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4823
                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaVIGENCIAINICIAL()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaVigenciaInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformesProyectosPlanDesarrolloControladorUrlEnum.URL4824
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(InformesProyectosPlanDesarrolloControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaProyectoinicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4826
                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyectoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaProyectofinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4828
                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformesProyectosPlanDesarrolloControladorEnum.CODIGO_INCIIAL.getValue(), proyectoInicial);

        listaProyectofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaDimension()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4830
                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformesProyectosPlanDesarrolloControladorEnum.ANIOFILTRO.getValue(), anioFiltro);

        listaDimension = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());
    }

    public void cargarListaSector()
    {

        if (dimension != null && !InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue().equals(dimension))
        {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4836
                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(InformesProyectosPlanDesarrolloControladorEnum.ANIOFILTRO.getValue(), anioFiltro);
            param.put(InformesProyectosPlanDesarrolloControladorEnum.DIMENSION.getValue(), dimension);

            listaSector = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());
        }
        else
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4834
                                            .getValue());
            listaSector = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), null,
                            true, InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());

        }
    }

    public void cargarListaPrograma()
    {

        if (sector != null && !InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue().equals(sector))
        {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4838
                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(InformesProyectosPlanDesarrolloControladorEnum.ANIOFILTRO.getValue(), anioFiltro);
            param.put(InformesProyectosPlanDesarrolloControladorEnum.DIMENSION.getValue(), dimension);

            listaPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());
        }
        else
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4834
                                            .getValue());
            listaPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), null,
                            true, InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());

        }

    }

    public void cargarListaSubprograma()
    {

        if (programa != null && !InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue().equals(programa))
        {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4840
                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(InformesProyectosPlanDesarrolloControladorEnum.ANIOFILTRO.getValue(), anioFiltro);
            param.put(InformesProyectosPlanDesarrolloControladorEnum.DIMENSION.getValue(), programa);

            listaSubprograma = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());
        }
        else
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(InformesProyectosPlanDesarrolloControladorUrlEnum.URL4834
                                            .getValue());
            listaSubprograma = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), null,
                            true, InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());

        }
    }

    private String validarCampoCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        depenedencia = validarCampoCadena(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
        codDependencia = validarCampoCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
    }

    public void seleccionarFilaSector(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        sector = validarCampoCadena(registroAux.getCampos(), InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());
        if (sector != null && InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue().equals(sector))
        {
            programa = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
            subprograma = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
        }
        cargarListaPrograma();
        cargarListaSubprograma();
    }

    public void oprimirexcel()
    {
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
    }

    /**
     * 
     * @return Verdadero si el informe es el n�mero 4
     */
    private boolean evaluarCondicion()
    {
        return informe != null && "4".equals(informe);
    }

    /**
     * Valida los campos obligatorios y define un mensaje por cada
     * campo vac�o
     * 
     * @return Verdadero cuando ninguno de los campos se encuentra
     * vacio
     */
    private boolean generarInformeCondicion()
    {
        boolean respuesta = true;

        if (SysmanFunciones.validarVariableVacio(proyectoInicial))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2462"));
            respuesta = false;
        }
        if (SysmanFunciones.validarVariableVacio(proyectoFinal))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2463"));
            respuesta = false;
        }
        if (SysmanFunciones.validarVariableVacio(dimension))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2461"));
            respuesta = false;
        }
        if (SysmanFunciones.validarVariableVacio(sector))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2460"));
            respuesta = false;
        }
        if (SysmanFunciones.validarVariableVacio(programa))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2459"));
            respuesta = false;
        }
        if (SysmanFunciones.validarVariableVacio(subprograma))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2458"));
            respuesta = false;
        }
        if (evaluarCondicion())
        {
            if (SysmanFunciones.validarVariableVacio(vigencia))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2256"));
                respuesta = false;
            }
            if (SysmanFunciones.validarVariableVacio(estado))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2457"));
                respuesta = false;
            }
        }
        return respuesta;
    }

    /**
     * 
     * @param reemplazar
     * Estructura que almacena los reemplazos que se har�n en la
     * consulta del informe
     * @param parametros
     * Estructura que almacena los par�metros que se env�an al
     * reporte
     * @param con
     * Conexion a la base de datos
     * @param formato
     * Formato con el que se genera el reporte
     */
    private void generarInformeCaso(Map<String, Object> reemplazar,
        Map<String, Object> parametros, FORMATOS formato, String nombreReporte)
    {
        try
        {

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            long contar = service.getConteoConsulta(
                            parametros.get(InformesProyectosPlanDesarrolloControladorEnum.PR_STRSQL.getValue()).toString());

            if (contar > 0)
            {

                archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString(InformesProyectosPlanDesarrolloControladorEnum.TG_NO_EXISTE.getValue()));
            }

        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(InformesProyectosPlanDesarrolloControladorEnum.MSM_INFORME_NO_EXISTE.getValue()), " ",
                            ex.getMessage(), " ",
                            nombreReporte));
            Logger.getLogger(InformesProyectosPlanDesarrolloControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (OutOfMemoryError | JRException | IOException | SystemException
                        | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Define las acciones necesarias para generar el informe, realiza
     * el reemplazo de valores en la consulta del informe y env�a
     * los par�metros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    private void generarInforme(FORMATOS formato)
    {
        if (!generarInformeCondicion())
        {
            return;
        }
        else
        {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("proyectoInicial", proyectoInicial);
            reemplazar.put("proyectoFin", proyectoFinal);
            reemplazar.put("dimension", dimension);
            reemplazar.put("sector", sector);
            reemplazar.put("programa", programa);
            reemplazar.put("subprograma", subprograma);
            reemplazar.put("dependencia", codDependencia);
            if ("1".equals(informe))
            {

                generarInformeCaso(reemplazar, parametros, formato,
                                InformesProyectosPlanDesarrolloControladorEnum.REPORTE000180.getValue());
            }
            else if ("2".equals(informe))
            {

                generarInformeCaso(reemplazar, parametros, formato,
                                InformesProyectosPlanDesarrolloControladorEnum.REPORTE000188.getValue());
            }
            else if ("3".equals(informe))
            {

                generarInformeCaso(reemplazar, parametros, formato,
                                InformesProyectosPlanDesarrolloControladorEnum.REPORTE000189.getValue());

            }
            else if ("4".equals(informe))
            {
                reemplazar.put(GeneralParameterEnum.VIGENCIA.getName().toLowerCase(), vigencia);
                reemplazar.put(GeneralParameterEnum.ESTADO.getName().toLowerCase(), estado);
                parametros.put(InformesProyectosPlanDesarrolloControladorEnum.PR_VIGENCIA.getValue(), vigencia);
                generarInformeCaso(reemplazar, parametros, formato,
                                InformesProyectosPlanDesarrolloControladorEnum.REPORTE000193.getValue());
            }
        }
    }

    public void oprimirImprimir()
    {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    public void asignarAnio()
    {
        if (proyectoInicial != null && proyectoFinal != null)
        {
            if (Integer.valueOf(proyectoInicial.substring(0, 4)) > Integer
                            .valueOf(proyectoFinal.substring(0, 4)))
            {
                anioFiltro = proyectoInicial.substring(0, 4);
            }
            else
            {
                anioFiltro = proyectoFinal.substring(0, 4);
            }
            if ("9999".equals(anioFiltro))
            {
                anioFiltro = String.valueOf(SysmanFunciones.getParteFecha(
                                new Date(),
                                Calendar.YEAR));
            }
        }
        else
        {
            anioFiltro = null;
        }
    }

    public void cambiarInforme()
    {
        // heredado del bean base
    }

    public void seleccionarFilaProyectoinicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        proyectoInicial = validarCampoCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
        asignarAnio();
        cargarListaProyectofinal();
    }

    public void seleccionarFilaProyectofinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        proyectoFinal = validarCampoCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
        asignarAnio();
        if (!SysmanFunciones.validarVariableVacio(proyectoInicial)
            && !SysmanFunciones.validarVariableVacio(proyectoFinal))
        {
            cargarListaDimension();
            if (proyectoInicial.equals(proyectoFinal))
            {
                dimension = validarCampoCadena(registroAux.getCampos(),
                                InformesProyectosPlanDesarrolloControladorEnum.ID_DIMENSION.getValue());
                sector = validarCampoCadena(registroAux.getCampos(), InformesProyectosPlanDesarrolloControladorEnum.ID_SECTOR.getValue());
                programa = validarCampoCadena(registroAux.getCampos(),
                                InformesProyectosPlanDesarrolloControladorEnum.ID_PROGRAMA.getValue());
                subprograma = validarCampoCadena(registroAux.getCampos(),
                                InformesProyectosPlanDesarrolloControladorEnum.ID_SUBPROGRAMA.getValue());
            }
            else
            {
                dimension = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
                sector = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
                programa = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
                subprograma = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
            }
        }
    }

    public void seleccionarFilaDimension(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        dimension = validarCampoCadena(registroAux.getCampos(), InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());
        if (InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue().equals(dimension))
        {
            sector = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
            programa = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
            subprograma = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
            anioFiltro = null;
        }
        else
        {
            sector = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
            asignarAnio();

        }
        cargarListaSector();
        cargarListaPrograma();
        cargarListaSubprograma();
    }

    public void seleccionarFilaPrograma(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        programa = validarCampoCadena(registroAux.getCampos(), InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());
        if (programa != null && InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue().equals(programa))
        {
            subprograma = InformesProyectosPlanDesarrolloControladorEnum.TODOS.getValue();
        }
        else
        {
            subprograma = null;
            asignarAnio();
        }
        cargarListaSubprograma();
    }

    @Override
    public void abrirFormulario()
    {
        // Acciones adicionles al abrir el formulario
    }

    public void seleccionarFilaSubprograma(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        subprograma = validarCampoCadena(registroAux.getCampos(), InformesProyectosPlanDesarrolloControladorEnum.ID.getValue());
    }

    public String getProyectoInicial()
    {
        return proyectoInicial;
    }

    public void setProyectoInicial(String proyectoInicial)
    {
        this.proyectoInicial = proyectoInicial;
    }

    public String getProyectoFinal()
    {
        return proyectoFinal;
    }

    public void setProyectoFinal(String proyectoFinal)
    {
        this.proyectoFinal = proyectoFinal;
    }

    public String getInforme()
    {
        return informe;
    }

    public void setInforme(String informe)
    {
        this.informe = informe;
    }

    public String getDimension()
    {
        return dimension;
    }

    public void setDimension(String dimension)
    {
        this.dimension = dimension;
    }

    public String getSector()
    {
        return sector;
    }

    public void setSector(String sector)
    {
        this.sector = sector;
    }

    public String getPrograma()
    {
        return programa;
    }

    public void setPrograma(String programa)
    {
        this.programa = programa;
    }

    public String getSubprograma()
    {
        return subprograma;
    }

    public void setSubprograma(String subprograma)
    {
        this.subprograma = subprograma;
    }

    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    public List<Registro> getListaVigenciaInicial()
    {
        return listaVigenciaInicial;
    }

    public void setListaVigenciaInicial(List<Registro> listaVigenciaInicial)
    {
        this.listaVigenciaInicial = listaVigenciaInicial;
    }

    public RegistroDataModelImpl getListaProyectoinicial()
    {
        return listaProyectoinicial;
    }

    public void setListaProyectoinicial(
        RegistroDataModelImpl listaProyectoinicial)
    {
        this.listaProyectoinicial = listaProyectoinicial;
    }

    public RegistroDataModelImpl getListaProyectofinal()
    {
        return listaProyectofinal;
    }

    public void setListaProyectofinal(RegistroDataModelImpl listaProyectofinal)
    {
        this.listaProyectofinal = listaProyectofinal;
    }

    public RegistroDataModelImpl getListaDimension()
    {
        return listaDimension;
    }

    public void setListaDimension(RegistroDataModelImpl listaDimension)
    {
        this.listaDimension = listaDimension;
    }

    public RegistroDataModelImpl getListaPrograma()
    {
        return listaPrograma;
    }

    public void setListaPrograma(RegistroDataModelImpl listaPrograma)
    {
        this.listaPrograma = listaPrograma;
    }

    public RegistroDataModelImpl getListaSubprograma()
    {
        return listaSubprograma;
    }

    public void setListaSubprograma(RegistroDataModelImpl listaSubprograma)
    {
        this.listaSubprograma = listaSubprograma;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    public String getDepenedencia()
    {
        return depenedencia;
    }

    public void setDepenedencia(String depenedencia)
    {
        this.depenedencia = depenedencia;
    }

    public String getVigencia()
    {
        return vigencia;
    }

    public void setVigencia(String vigencia)
    {
        this.vigencia = vigencia;
    }

    public String getCodDependencia()
    {
        return codDependencia;
    }

    public void setCodDependencia(String codDependencia)
    {
        this.codDependencia = codDependencia;
    }

    public RegistroDataModelImpl getListaSector()
    {
        return listaSector;
    }

    public void setListaSector(RegistroDataModelImpl listaSector)
    {
        this.listaSector = listaSector;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public FormContinuoService getService()
    {
        return service;
    }

    @Override
    public void setService(FormContinuoService service)
    {
        this.service = service;
    }

}