package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrminformesscdafectadasControladorEnum;
import com.sysman.bancoproyectos.enums.FrminformesscdafectadasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 22/08/2015
 */
@ManagedBean
@ViewScoped

public class FrminformesscdafectadasControlador extends BeanBaseModal
{

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private final String cCodigo;
    private final String cNombre;
    private String valDefFin;
    private String valDefIni;
    private String depIni;
    private String depFin;
    private String titEtIni;
    private String titEtFin;
    private String titColFin;
    private String titColmIni;
    private String titColDepIni;
    private String titColDepFin;
    private String nombreDepFin;
    private String nomDepIni;
    private String nivUsuario;
    private boolean seleccionado;
    private boolean selDependencia;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private Registro dependenciaIni;
    private Registro registro;
    private Registro dependenciaFin;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaFinal;
    private RegistroDataModelImpl listaInicial;
    private RegistroDataModelImpl listaDependenciaInicial;
    private RegistroDataModelImpl listaDependenciaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrminformesscdafectadasControlador
     */
    public FrminformesscdafectadasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        try
        {
            registro = new Registro(new HashMap<String, Object>());

            selDependencia = false;
            // 134
            numFormulario = GeneralCodigoFormaEnum.FRMINFORMESSCDAFECTADAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrminformesscdafectadasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDependenciaInicial();
        cargarListaDependenciaFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {

        cambiarvproyecto();

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaFinal
     */
    public void cargarListaFinal()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        if (!seleccionado)
        {
            titColFin = idioma.getString("TG_VIGENCIA3");
            param.put(FrminformesscdafectadasControladorEnum.PROYECTOINICIAL
                            .getValue(),
                            valDefIni);
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrminformesscdafectadasControladorUrlEnum.URL3627
                                                            .getValue());

            listaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigo);
        }
        else
        {
            titColFin = idioma.getString("TG_NOMBRE_PROYECTO");
            param.put(GeneralParameterEnum.NUMERO.getName(), valDefIni);
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrminformesscdafectadasControladorUrlEnum.URL4278
                                                            .getValue());
            listaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigo);
        }
    }

    /**
     * 
     * Carga la lista listaInicial
     */
    public void cargarListaInicial()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        if (!seleccionado)
        {
            titColmIni = idioma.getString("TG_VIGENCIA3");

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrminformesscdafectadasControladorUrlEnum.URL4949
                                                            .getValue());

            listaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigo);

        }
        else
        {
            titColmIni = idioma.getString("TG_NOMBRE_PROYECTO");

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrminformesscdafectadasControladorUrlEnum.URL5531
                                                            .getValue());

            listaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigo);

        }

    }

    /**
     * 
     * Carga la lista listaDependenciaInicial
     */
    public void cargarListaDependenciaInicial()
    {

        titColDepIni = idioma.getString("TC_CP10668");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesscdafectadasControladorUrlEnum.URL6117
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependenciaInicial = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    /**
     * 
     * Carga la lista listaDependenciaFinal
     */
    public void cargarListaDependenciaFinal()
    {

        titColDepFin = idioma.getString("TC_CP10668");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesscdafectadasControladorUrlEnum.URL7458
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrminformesscdafectadasControladorEnum.CODIGOINICIAL
                        .getValue(),
                        depIni);

        listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BTImpExcel en la vista
     *
     */
    public void oprimirBTImpExcel()
    {
        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL97);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control vproyecto
     * 
     */
    public void cambiarvproyecto()
    {
        if (!seleccionado)
        {
            titEtIni = idioma.getString("TB_TB3616");
            titEtFin = idioma.getString("TB_TB3617");
        }
        else
        {
            titEtIni = idioma.getString("TG_PROYECTO_INICIAL2");
            titEtFin = idioma.getString("TG_PROYECTO_FINAL2");

        }
        valDefIni = null;
        valDefFin = null;
        cargarListaInicial();
        cargarListaFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        valDefFin = SysmanFunciones.nvl(
                        registroAux.getCampos().get(cCodigo), "").toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        valDefIni = SysmanFunciones.nvl(
                        registroAux.getCampos().get(cCodigo), "").toString();
        valDefFin = null;
        cargarListaFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaFinal(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        depFin = SysmanFunciones.nvl(
                        registroAux.getCampos().get(cCodigo), "").toString();
        nombreDepFin = registroAux.getCampos().get(cNombre).toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaInicial(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        depIni = SysmanFunciones.nvl(
                        registroAux.getCampos().get(cCodigo), "").toString();
        nomDepIni = registroAux.getCampos().get(cNombre).toString();
        depFin = null;
        nombreDepFin = null;
        cargarListaDependenciaFinal();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    /**
     * Metodo llamado al darle clic en el boton pdf o excel de la
     * vista
     * 
     * @param formato
     * formato dependiendo del boton seleccionado
     */
    public void generaInforme(FORMATOS formato)
    {
        String reporte = "000186SCDafectadas";
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String condicion;
            String titAfec = idioma.getString("TB_TB3618");
            String solicitudAfec;
            String tDependencia;
            String tProyecto;
            StringBuilder cond = new StringBuilder();
            StringBuilder solicitudA = new StringBuilder();
            StringBuilder tDepen = new StringBuilder();
            StringBuilder tProy = new StringBuilder();
            StringBuilder titA = new StringBuilder();

            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "CONTROLAR DEPENDENCIA EN BPPIM", modulo,
                            new Date(), true);

            parametro = parametro == null ? "NO" : parametro;
            nivUsuario = Integer.toString(SessionUtil.getNivelUsuario(modulo));

            enviarSolicitudAfect(parametro, cond, solicitudA);

            condicion = cond.toString();
            solicitudAfec = solicitudA.toString();

            asignarTitulos(parametro, tDepen, titA, tProy);

            tDependencia = tDepen.toString();
            if (!"".equals(titA.toString()))
            {
                titAfec = titA.toString();
            }

            tProyecto = tProy.toString();

            // MANEJO DE PARAMETROS DEL REPORTE
            condicion = condicion == null ? "" : condicion;
            reemplazar.put("condicion", condicion);
            parametros.put("PR_FORMS_FRM_INFORMES_SCDAFECTADAS_TITULO",
                            titAfec);
            parametros.put("PR_SOLICITUDAFECT", solicitudAfec);
            parametros.put("PR_FORMS_FRM_INFORMES_SCDAFECTADAS_T_DEPENDENCIA",
                            tDependencia);
            parametros.put("PR_FORMS_FRM_INFORMES_SCDAFECTADAS_T_PROYECTO",
                            tProyecto);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException |

        SystemException ex)

        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);

        }

    }

    /**
     * 
     * @param parametro
     * @param condicion
     * @param solicitudAfec
     */
    public void enviarSolicitudAfect(String parametro, StringBuilder condicion,
        StringBuilder solicitudAfec)
    {
        if ("SI".equals(parametro) && !("9".equals(nivUsuario)))
        {
            condicion.append(" AND DN.DEPENDENCIA IN('" + depIni
                + "') ");
            solicitudAfec.append(idioma.getString("TB_TB3628"));
        }
        else
        {
            condicion.append(" AND DN.DEPENDENCIA BETWEEN '" + depIni
                + "' AND  '" + depFin + "'");
            solicitudAfec.append(idioma.getString("TB_TB3629"));
        }
        if (seleccionado)
        {
            condicion.append(" AND DN.PROYECTO BETWEEN '" + valDefIni
                + "' AND '" + valDefFin + "' ");
        }
        else
        {
            condicion.append(" AND DN.NOVEDAD BETWEEN '" + valDefIni
                + "' AND '" + valDefFin + "' ");
        }
    }

    /**
     * @param parametro
     * @param tDependencia
     * @param titAfec
     * @param tProyecto
     */
    public void asignarTitulos(String parametro, StringBuilder tDependencia,
        StringBuilder titAfec, StringBuilder tProyecto)
    {
        if ("SI".equals(parametro) && !("9".equals(nivUsuario)))
        {
            tDependencia.append(
                            (idioma.getString("TB_TB3619") + " " + depIni + " "
                                + nomDepIni).toUpperCase());
        }
        else
        {
            tDependencia.append(idioma.getString("TB_TB3623")
                            .replace("s$depIni$s", depIni)
                            .replace("s$nomDepIni$s", nomDepIni)
                            .replace("s$depFin$s", depFin)
                            .replace("s$nombreDepFin$s", nombreDepFin));

        }
        if (seleccionado)
        {
            titAfec.append(idioma.getString("TB_TB3622"));
            tProyecto.append(idioma.getString("TB_TB3624")
                            .replace("s$valDefIni$s", valDefIni)
                            .replace("s$valDefFin$s", valDefFin));
        }
        else
        {
            tProyecto.append(idioma.getString("TB_TB3625")
                            .replace("s$valDefInis$", valDefIni)
                            .replace("s$valDefFin$s", valDefFin));

        }
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable seleccionado
     * 
     * @return seleccionado
     */
    public boolean getSeleccionado()
    {
        return seleccionado;
    }

    /**
     * Asigna la variable seleccionado
     * 
     * @param seleccionado
     * Variable a asignar en seleccionado
     */
    public void setSeleccionado(boolean seleccionado)
    {
        this.seleccionado = seleccionado;
    }

    /**
     * Retorna la variable valDefFin
     * 
     * @return valDefFin
     */
    public String getValDefFin()
    {
        return valDefFin;
    }

    /**
     * Asigna la variable valDefFin
     * 
     * @param valDefFin
     * Variable a asignar en valDefFin
     */
    public void setValDefFin(String valDefFin)
    {
        this.valDefFin = valDefFin;
    }

    /**
     * Retorna la variable valDefIni
     * 
     * @return valDefIni
     */
    public String getValDefIni()
    {
        return valDefIni;
    }

    /**
     * Asigna la variable valDefIni
     * 
     * @param valDefIni
     * Variable a asignar en valDefIni
     */
    public void setValDefIni(String valDefIni)
    {
        this.valDefIni = valDefIni;
    }

    /**
     * Retorna la variable depIni
     * 
     * @return depIni
     */
    public String getDepIni()
    {
        return depIni;
    }

    /**
     * Asigna la variable depIni
     * 
     * @param depIni
     * Variable a asignar en depIni
     */
    public void setDepIni(String depIni)
    {
        this.depIni = depIni;
    }

    /**
     * Retorna la variable depFin
     * 
     * @return depFin
     */
    public String getDepFin()
    {
        return depFin;
    }

    /**
     * Asigna la variable depFin
     * 
     * @param depFin
     * Variable a asignar en depFin
     */
    public void setDepFin(String depFin)
    {
        this.depFin = depFin;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna la variable titColFin
     * 
     * @return titColFin
     */
    public String getTitColFin()
    {
        return titColFin;
    }

    /**
     * Asigna la variable titColFin
     * 
     * @param titColFin
     * Variable a asignar en titColFin
     */
    public void setTitColFin(String titColFin)
    {
        this.titColFin = titColFin;
    }

    /**
     * Retorna la variable titColmIni
     * 
     * @return titColmIni
     */
    public String getTitColmIni()
    {
        return titColmIni;
    }

    /**
     * Asigna la variable titColmIni
     * 
     * @param titColmIni
     * Variable a asignar en titColmIni
     */
    public void setTitColmIni(String titColmIni)
    {
        this.titColmIni = titColmIni;
    }

    /**
     * Retorna la variable titColDepIni
     * 
     * @return titColDepIni
     */
    public String getTitColDepIni()
    {
        return titColDepIni;
    }

    /**
     * Asigna la variable titColDepIni
     * 
     * @param titColDepIni
     * Variable a asignar en titColDepIni
     */
    public void setTitColDepIni(String titColDepIni)
    {
        this.titColDepIni = titColDepIni;
    }

    /**
     * Retorna la variable titColDepFin
     * 
     * @return titColDepFin
     */
    public String getTitColDepFin()
    {
        return titColDepFin;
    }

    /**
     * Asigna la variable titColDepFin
     * 
     * @param titColDepFin
     * Variable a asignar en titColDepFin
     */
    public void setTitColDepFin(String titColDepFin)
    {
        this.titColDepFin = titColDepFin;
    }

    /**
     * Retorna la variable titEtIni
     * 
     * @return titEtIni
     */
    public String getTitEtIni()
    {
        return titEtIni;
    }

    /**
     * Asigna la variable titEtIni
     * 
     * @param titEtIni
     * Variable a asignar en titEtIni
     */
    public void setTitEtIni(String titEtIni)
    {
        this.titEtIni = titEtIni;
    }

    /**
     * Retorna la variable titEtFin
     * 
     * @return titEtFin
     */
    public String getTitEtFin()
    {
        return titEtFin;
    }

    /**
     * Asigna la variable titEtFin
     * 
     * @param titEtFin
     * Variable a asignar en titEtFin
     */
    public void setTitEtFin(String titEtFin)
    {
        this.titEtFin = titEtFin;
    }

    /**
     * Retorna la variable nombreDepFin
     * 
     * @return nombreDepFin
     */
    public String getNombreDepFin()
    {
        return nombreDepFin;
    }

    /**
     * Asigna la variable nombreDepFin
     * 
     * @param nombreDepFin
     * Variable a asignar en nombreDepFin
     */
    public void setNombreDepFin(String nombreDepFin)
    {
        this.nombreDepFin = nombreDepFin;
    }

    /**
     * Retorna la variable nomDepIni
     * 
     * @return nomDepIni
     */
    public String getNomDepIni()
    {
        return nomDepIni;
    }

    /**
     * Asigna la variable nomDepIni
     * 
     * @param nomDepIni
     * Variable a asignar en nomDepIni
     */
    public void setNomDepIni(String nomDepIni)
    {
        this.nomDepIni = nomDepIni;
    }

    /**
     * Retorna la variable selDependencia
     * 
     * @return selDependencia
     */
    public boolean isSelDependencia()
    {
        return selDependencia;
    }

    /**
     * Asigna la variable selDependencia
     * 
     * @param selDependencia
     * Variable a asignar en selDependencia
     */
    public void setSelDependencia(boolean selDependencia)
    {
        this.selDependencia = selDependencia;
    }

    /**
     * Retorna la variable nivUsuario
     * 
     * @return nivUsuario
     */
    public String getNivUsuario()
    {
        return nivUsuario;
    }

    /**
     * Asigna la variable nivUsuario
     * 
     * @param nivUsuario
     * Variable a asignar en nivUsuario
     */
    public void setNivUsuario(String nivUsuario)
    {
        this.nivUsuario = nivUsuario;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public Registro getRegistro()
    {
        return registro;
    }

    public void setRegistro(Registro registro)
    {
        this.registro = registro;
    }

    public Registro getListaDependenciaIni()
    {
        return dependenciaIni;
    }

    public void setListaDependenciaIni(Registro listaDependenciaIni)
    {
        this.dependenciaIni = listaDependenciaIni;
    }

    public Registro getListaDependenciaFin()
    {
        return dependenciaFin;
    }

    public void setListaDependenciaFin(Registro listaDependenciaFin)
    {
        this.dependenciaFin = listaDependenciaFin;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFinal
     * 
     * @return listaFinal
     */
    public RegistroDataModelImpl getListaFinal()
    {
        return listaFinal;
    }

    /**
     * Asigna la lista listaFinal
     * 
     * @param listaFinal
     * Variable a asignar en listaFinal
     */
    public void setListaFinal(RegistroDataModelImpl listaFinal)
    {
        this.listaFinal = listaFinal;
    }

    /**
     * Retorna la lista listaInicial
     * 
     * @return listaInicial
     */
    public RegistroDataModelImpl getListaInicial()
    {
        return listaInicial;
    }

    /**
     * Asigna la lista listaInicial
     * 
     * @param listaInicial
     * Variable a asignar en listaInicial
     */
    public void setListaInicial(RegistroDataModelImpl listaInicial)
    {
        this.listaInicial = listaInicial;
    }

    /**
     * Retorna la lista listaDependenciaInicial
     * 
     * @return listaDependenciaInicial
     */
    public RegistroDataModelImpl getListaDependenciaInicial()
    {
        return listaDependenciaInicial;
    }

    /**
     * Asigna la lista listaDependenciaInicial
     * 
     * @param listaDependenciaInicial
     * Variable a asignar en listaDependenciaInicial
     */
    public void setListaDependenciaInicial(
        RegistroDataModelImpl listaDependenciaInicial)
    {
        this.listaDependenciaInicial = listaDependenciaInicial;
    }

    /**
     * Retorna la lista listaDependenciaFinal
     * 
     * @return listaDependenciaFinal
     */
    public RegistroDataModelImpl getListaDependenciaFinal()
    {
        return listaDependenciaFinal;
    }

    /**
     * Asigna la lista listaDependenciaFinal
     * 
     * @param listaDependenciaFinal
     * Variable a asignar en listaDependenciaFinal
     */
    public void setListaDependenciaFinal(
        RegistroDataModelImpl listaDependenciaFinal)
    {
        this.listaDependenciaFinal = listaDependenciaFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}