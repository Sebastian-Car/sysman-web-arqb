package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmesmetasControladorEnum;
import com.sysman.precontractual.enums.FrmesmetasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 29/03/2016
 * 
 * @author ybecerra
 * @version 2, 28/08/2017, proceso de Refactoring y revision sonar
 * 
 */
@ManagedBean
@ViewScoped

public class FrmesmetasControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private String tComponente;
    private String codProy;
    private String componente;
    private String actividad;
    private String txtCodEstudio;
    //
    private int vigenciaP;
    private String vigenciaMeta;
    private double valorEjecutadoM;
    private double cantidadEejecutadoP;
    private double cantidadMEjecutar;
    private String descripcion;
    private boolean voBo;
    private HashMap<String, Object> ridM;
    private HashMap<String, Object> ridMetas;
    private Boolean esCreador;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listacodigoMeta;
    private RegistroDataModelImpl listacodigoMetaE;
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaEstudioproyectometas;
    private RegistroDataModelImpl listaPlanindicativometas;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     * EstudioProyectoMetas
     */
    private Registro registroSubEstudioProyectoMetas;
    /**
     * Atributo de referencia para el subformulario
     * PlanIndicativoMetas
     */
    private Registro registroSubPlanIndicativoMetas;

    // </DECLARAR_ADICIONALES>

    /**
     * Crea una nueva instancia de FrmesmetasControlador
     */

    @SuppressWarnings("unchecked")
    public FrmesmetasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 601;

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                ridM = (HashMap<String, Object>) parametrosEntrada.get("rid");
                ridMetas = (HashMap<String, Object>) parametrosEntrada
                                .get("ridMetas");
                tComponente = (String) parametrosEntrada.get("tComponente");
                codProy = (String) parametrosEntrada.get("codProy");
                componente = (String) parametrosEntrada.get("componente");
                actividad = (String) parametrosEntrada.get("actividad");
                txtCodEstudio = (String) parametrosEntrada.get("codEstudio");
                voBo = (boolean) parametrosEntrada.get("voBo");
                esCreador = Boolean.parseBoolean(
                                parametrosEntrada.get("esCreador").toString());

            }
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubEstudioProyectoMetas = new Registro(
                            new HashMap<String, Object>());
            registroSubPlanIndicativoMetas = new Registro(
                            new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmesmetasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        cargarListacodigoMeta();
        cargarListacodigoMetaE();
        cargarListaDependencia();
        cargarListaDependenciaE();
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        registroSubPlanIndicativoMetas.getCampos()
                        .put(FrmesmetasControladorEnum.VALOR_PROGRAMADO_META
                                        .getValue(),
                                        0);
        registroSubPlanIndicativoMetas.getCampos()
                        .put(FrmesmetasControladorEnum.VALOR_EJECUTADO_META
                                        .getValue(),
                                        0);
        registroSubPlanIndicativoMetas.getCampos().put("CANTIDAD_PROGRAMADA",
                        0);
        registroSubPlanIndicativoMetas.getCampos().put("CANTIDAD_EJECUTADA", 0);
        registroSubEstudioProyectoMetas.getCampos()
                        .put(FrmesmetasControladorEnum.VALOR_EJECUTADO_META
                                        .getValue(),
                                        0);
        registroSubEstudioProyectoMetas.getCampos()
                        .put(FrmesmetasControladorEnum.CANTIDAD_EJECUTADA_P
                                        .getValue(),
                                        0);
        registroSubEstudioProyectoMetas.getCampos()
                        .put(FrmesmetasControladorEnum.CANTIDAD_META_EJECUTAR
                                        .getValue(), 0);

        cargarListaEstudioproyectometas();
        cargarListaPlanindicativometas();
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        listaEstudioproyectometas = null;
        listaPlanindicativometas = null;
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
        tabla = "";
        asignarOrigenDatos();
        abrirFormulario();
        iniciarListas();
        iniciarListasSub();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     */
    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    /**
     * 
     * Carga la lista listaEstudioproyectometas
     */
    public void cargarListaEstudioproyectometas()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                        txtCodEstudio);
        param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);

        try
        {
            listaEstudioproyectometas = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmesmetasControladorUrlEnum.URL249
                                                                            .getValue())
                                            .getUrl(),
                                            param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            FrmesmetasControladorEnum.ES_PROY_METAS
                                                            .getValue()));
        }
        catch (SystemException | SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaPlanindicativometas
     */
    public void cargarListaPlanindicativometas()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmesmetasControladorUrlEnum.URL278
                                                        .getValue());

        try
        {
            listaPlanindicativometas = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), null,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmesmetasControladorEnum.BP_PLAN_INDICATIVO_METAS
                                                            .getValue()));
        }
        catch (SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacodigoMeta
     */

    public void cargarListacodigoMeta()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmesmetasControladorUrlEnum.URL10992
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmesmetasControladorEnum.TIPOCOMPONENTE.getValue(),
                        tComponente);
        param.put(GeneralParameterEnum.PROYECTO.getName(), codProy);
        param.put(FrmesmetasControladorEnum.COMPONENTE.getValue(), componente);
        param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);

        listacodigoMeta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmesmetasControladorEnum.ID_PLAN_P.getValue());

    }

    /**
     * 
     * Carga la lista listacodigoMeta
     */
    public void cargarListacodigoMetaE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmesmetasControladorUrlEnum.URL10992
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmesmetasControladorEnum.TIPOCOMPONENTE.getValue(),
                        tComponente);
        param.put(GeneralParameterEnum.PROYECTO.getName(), codProy);
        param.put(FrmesmetasControladorEnum.COMPONENTE.getValue(), componente);
        param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);

        listacodigoMetaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmesmetasControladorEnum.ID_PLAN_P.getValue());

    }

    /**
     * 
     * Carga la lista listaDependencia
     */
    public void cargarListaDependencia()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmesmetasControladorUrlEnum.URL15946
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaDependenciaE
     */
    public void cargarListaDependenciaE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmesmetasControladorUrlEnum.URL15946
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ID_PLAN_P en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcodigoMetaC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaEstudioproyectometas.get(rowNum).getCampos().put(
                        FrmesmetasControladorEnum.ID_PLAN_P.getValue(),
                        registroSubEstudioProyectoMetas.getCampos()
                                        .get(FrmesmetasControladorEnum.ID_PLAN_P
                                                        .getValue()));
        listaEstudioproyectometas.get(rowNum).getCampos().put(
                        FrmesmetasControladorEnum.VIGENCIA_PLAN_P.getValue(),
                        vigenciaP);
        listaEstudioproyectometas.get(rowNum).getCampos().put(
                        FrmesmetasControladorEnum.VIGENCIA_META_P.getValue(),
                        vigenciaMeta);
        listaEstudioproyectometas.get(rowNum).getCampos().put(
                        FrmesmetasControladorEnum.VALOR_EJECUTADO_META
                                        .getValue(),
                        valorEjecutadoM);
        listaEstudioproyectometas.get(rowNum).getCampos().put(
                        FrmesmetasControladorEnum.CANTIDAD_EJECUTADA_P
                                        .getValue(),
                        cantidadEejecutadoP);
        listaEstudioproyectometas.get(rowNum).getCampos().put(
                        FrmesmetasControladorEnum.CANTIDAD_META_EJECUTAR
                                        .getValue(),
                        cantidadMEjecutar);
        listaEstudioproyectometas.get(rowNum).getCampos().put(
                        FrmesmetasControladorEnum.DESCRIPCION_META.getValue(),
                        descripcion);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFilacodigoMeta(SelectEvent event)
    {
        /**
         * 
         * Metodo ejecutado al seleccionar una fila de la lista
         * listaID_PLAN_P
         *
         * @param event
         * objeto que encapsula la accion proveniente de la vista
         */
        Registro registroAux = (Registro) event.getObject();
        registroSubEstudioProyectoMetas.getCampos().put(
                        FrmesmetasControladorEnum.ID_PLAN_P.getValue(),
                        registroAux.getCampos()
                                        .get(FrmesmetasControladorEnum.ID_PLAN_P
                                                        .getValue()));
        registroSubEstudioProyectoMetas.getCampos().put(
                        FrmesmetasControladorEnum.VIGENCIA_PLAN_P.getValue(),
                        registroAux.getCampos()
                                        .get(FrmesmetasControladorEnum.VIGENCIA_PLAN_P
                                                        .getValue()));
        registroSubEstudioProyectoMetas.getCampos().put(
                        FrmesmetasControladorEnum.VIGENCIA_META_P.getValue(),
                        registroAux.getCampos()
                                        .get(FrmesmetasControladorEnum.VIGENCIA_META_P
                                                        .getValue()));
        registroSubEstudioProyectoMetas.getCampos().put("PONDERACION_META",
                        registroAux.getCampos().get("PONDERACION_META"));
        registroSubEstudioProyectoMetas.getCampos()
                        .put(FrmesmetasControladorEnum.VALOR_PROGRAMADO_META
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmesmetasControladorEnum.VALOR_PROGRAMADO_META
                                                                        .getValue()));
        registroSubEstudioProyectoMetas.getCampos()
                        .put(FrmesmetasControladorEnum.VALOR_EJECUTADO_META
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmesmetasControladorEnum.VALOR_EJECUTADO_META
                                                                        .getValue()));
        registroSubEstudioProyectoMetas.getCampos().put("CANTIDAD_PROGRAMADA_P",
                        registroAux.getCampos().get("CANTIDAD_PROGRAMADA_P"));
        registroSubEstudioProyectoMetas.getCampos()
                        .put(FrmesmetasControladorEnum.CANTIDAD_EJECUTADA_P
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        FrmesmetasControladorEnum.CANTIDAD_EJECUTADA_P
                                                                        .getValue()));
        registroSubEstudioProyectoMetas.getCampos().put(
                        FrmesmetasControladorEnum.DESCRIPCION_META.getValue(),
                        registroAux.getCampos()
                                        .get(FrmesmetasControladorEnum.DESCRIPCION_META
                                                        .getValue()));
        registroSubEstudioProyectoMetas.getCampos().put("SUBPROGRAMA",
                        registroAux.getCampos().get("SUBPROGRAMA"));
        registroSubEstudioProyectoMetas.getCampos().put("PROGRAMA",
                        registroAux.getCampos().get("PROGRAMA"));
        registroSubEstudioProyectoMetas.getCampos().put("SECTOR",
                        registroAux.getCampos().get("SECTOR"));
        registroSubEstudioProyectoMetas.getCampos().put("DIMENSION",
                        registroAux.getCampos().get("DIMENSION"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaID_PLAN_P
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoMetaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(FrmesmetasControladorEnum.ID_PLAN_P.getValue())
                        .toString();
        vigenciaP = Integer.valueOf(registroAux.getCampos().get(
                        FrmesmetasControladorEnum.VIGENCIA_PLAN_P.getValue())
                        .toString());
        vigenciaMeta = registroAux.getCampos().get(
                        FrmesmetasControladorEnum.VIGENCIA_META_P.getValue())
                        .toString();
        String valor = registroAux.getCampos()
                        .get(FrmesmetasControladorEnum.VALOR_EJECUTADO_META
                                        .getValue())
                        .toString();
        valorEjecutadoM = valor == null ? 0.0 : Double.parseDouble(valor);
        String cantidadEP = registroAux.getCampos()
                        .get(FrmesmetasControladorEnum.CANTIDAD_EJECUTADA_P
                                        .getValue())
                        .toString();
        cantidadEejecutadoP = cantidadEP == null ? 0.0
            : Double.parseDouble(cantidadEP);
        String cantidadME = (registroAux.getCampos()
                        .get(FrmesmetasControladorEnum.CANTIDAD_META_EJECUTAR
                                        .getValue()) == null ? "0.0"
                                            : registroAux.getCampos()
                                                            .get("CANTIDAD_META_EJECUTAR"))
                                                                            .toString();
        cantidadMEjecutar = cantidadME == null ? 0.0
            : Double.parseDouble(cantidadME);
        descripcion = (String) registroAux.getCampos().get(
                        FrmesmetasControladorEnum.DESCRIPCION_META.getValue());

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDEPENDENCIA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubPlanIndicativoMetas.getCampos().put(
                        GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDEPENDENCIA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Estudioproyectometas
     */
    public void agregarRegistroSubEstudioproyectometas()
    {
        try
        {
            registroSubEstudioProyectoMetas.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubEstudioProyectoMetas.getCampos().put("COD_PROY",
                            codProy);
            registroSubEstudioProyectoMetas.getCampos().put(
                            GeneralParameterEnum.COD_ESTUDIO.getName(),
                            txtCodEstudio);
            registroSubEstudioProyectoMetas.getCampos().put("T_COMPONENTE",
                            tComponente);
            registroSubEstudioProyectoMetas.getCampos().put(
                            FrmesmetasControladorEnum.COMPONENTE.getValue(),
                            componente);
            registroSubEstudioProyectoMetas.getCampos().put(
                            GeneralParameterEnum.ACTIVIDAD.getName(),
                            actividad);
            registroSubEstudioProyectoMetas.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubEstudioProyectoMetas.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubEstudioProyectoMetas.getCampos()
                            .remove(FrmesmetasControladorEnum.CANTIDAD_META_EJECUTAR
                                            .getValue());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_PROY_METAS
                                                            .getCreateKey());
            Parameter params = new Parameter();
            params.setFields(registroSubEstudioProyectoMetas.getCampos());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            params);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally
        {
            registroSubEstudioProyectoMetas.getCampos()
                            .put(FrmesmetasControladorEnum.VALOR_EJECUTADO_META
                                            .getValue(), 0);
            registroSubEstudioProyectoMetas.getCampos()
                            .put(FrmesmetasControladorEnum.CANTIDAD_EJECUTADA_P
                                            .getValue(), 0);
            registroSubEstudioProyectoMetas.getCampos().put(
                            FrmesmetasControladorEnum.CANTIDAD_META_EJECUTAR
                                            .getValue(),
                            0);
            registroSubEstudioProyectoMetas = new Registro(
                            new HashMap<String, Object>());
            cargarListaEstudioproyectometas();
        }
    }

    /**
     * Metodo de edicion del formulario Estudioproyectometas
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubEstudioproyectometas(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove("COD_PROY");
            reg.getCampos().remove(GeneralParameterEnum.COD_ESTUDIO.getName());
            reg.getCampos().remove("T_COMPONENTE");
            reg.getCampos().remove(
                            FrmesmetasControladorEnum.COMPONENTE.getValue());
            reg.getCampos().remove(GeneralParameterEnum.ACTIVIDAD.getName());
            reg.getCampos().remove(
                            FrmesmetasControladorEnum.CANTIDAD_META_EJECUTAR
                                            .getValue());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_PROY_METAS
                                                            .getUpdateKey());
            Parameter params = new Parameter();
            reg.getCampos().putAll(reg.getLlave());
            params.setFields(reg.getCampos());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            params);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally
        {
            cargarListaEstudioproyectometas();
        }
    }

    /**
     * Metodo de eliminacion del formulario Estudioproyectometas
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubEstudioproyectometas(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_PROY_METAS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaEstudioproyectometas();

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Estudioproyectometas
     */
    public void cancelarEdicionEstudioproyectometas()
    {
        cargarListaEstudioproyectometas();
        cargarListaPlanindicativometas();
    }

    /**
     * Metodo de insercion del formulario Planindicativometas
     */
    public void agregarRegistroSubPlanindicativometas()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo de edicion del formulario Planindicativometas
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubPlanindicativometas(RowEditEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de eliminacion del formulario Planindicativometas
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubPlanindicativometas(Registro reg)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Planindicativometas
     */
    public void cancelarEdicionPlanindicativometas()
    {
        cargarListaPlanindicativometas();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", "ridMetas", "txtCodEstudio", "voBo",
                            "esCreador",
                            "metas" };
        Object[] valores = { ridM, ridMetas, txtCodEstudio, voBo,
                             SysmanFunciones.nvl(esCreador, "").toString(),
                             true };

        SessionUtil.redireccionarPorFormulario(
                        SessionUtil.getModulo(), Integer.toString(
                                        GeneralCodigoFormaEnum.FRMESTPROY_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);

        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable esCreador
     * 
     * @return esCreador
     */
    public boolean isEsCreador()
    {
        return esCreador;
    }

    /**
     * Asigna la variable esCreador
     * 
     * @param esCreador
     * Variable a asignar en esCreador
     */
    public void setEsCreador(boolean esCreador)
    {
        this.esCreador = esCreador;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodigoMeta
     * 
     * @return listacodigoMeta
     */
    public RegistroDataModelImpl getListacodigoMeta()
    {
        return listacodigoMeta;
    }

    /**
     * Asigna la lista listacodigoMeta
     * 
     * @param listacodigoMeta
     * Variable a asignar en listacodigoMeta
     */
    public void setListacodigoMeta(RegistroDataModelImpl listacodigoMeta)
    {
        this.listacodigoMeta = listacodigoMeta;
    }

    /**
     * Retorna la lista listacodigoMeta
     * 
     * @return listacodigoMeta
     */
    public RegistroDataModelImpl getListacodigoMetaE()
    {
        return listacodigoMetaE;
    }

    /**
     * Asigna la lista listacodigoMeta
     * 
     * @param listacodigoMeta
     * Variable a asignar en listacodigoMeta
     */
    public void setListacodigoMetaE(RegistroDataModelImpl listacodigoMetaE)
    {
        this.listacodigoMetaE = listacodigoMetaE;
    }

    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependenciaE()
    {
        return listaDependenciaE;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependenciaE(RegistroDataModelImpl listaDependenciaE)
    {
        this.listaDependenciaE = listaDependenciaE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    /**
     * Retorna la lista listaEstudioproyectometas
     * 
     * @return listaEstudioproyectometas
     */
    public List<Registro> getListaEstudioproyectometas()
    {
        return listaEstudioproyectometas;
    }

    /**
     * Asigna la lista listaEstudioproyectometas
     * 
     * @param listaEstudioproyectometas
     * Variable a asignar en listaEstudioproyectometas
     */
    public void setListaEstudioproyectometas(
        List<Registro> listaEstudioproyectometas)
    {
        this.listaEstudioproyectometas = listaEstudioproyectometas;
    }

    /**
     * Retorna la lista listaPlanindicativometas
     * 
     * @return listaPlanindicativometas
     */
    public RegistroDataModelImpl getListaPlanindicativometas()
    {
        return listaPlanindicativometas;
    }

    /**
     * Asigna la lista listaPlanindicativometas
     * 
     * @param listaPlanindicativometas
     * Variable a asignar en listaPlanindicativometas
     */
    public void setListaPlanindicativometas(
        RegistroDataModelImpl listaPlanindicativometas)
    {
        this.listaPlanindicativometas = listaPlanindicativometas;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    /**
     * Retorna el objeto registroSubEstudioProyectoMetas
     * 
     * @return registroSubEstudioProyectoMetas
     */
    public Registro getRegistroSubEstudioProyectoMetas()
    {
        return registroSubEstudioProyectoMetas;
    }

    /**
     * Asigna el objeto registroSubEstudioProyectoMetas
     * 
     * @param registroSubEstudioProyectoMetas
     * Variable a asignar en registroSubEstudioProyectoMetas
     */
    public void setRegistroSubEstudioProyectoMetas(
        Registro registroSubEstudioProyectoMetas)
    {
        this.registroSubEstudioProyectoMetas = registroSubEstudioProyectoMetas;
    }

    /**
     * Retorna el objeto registroSubPlanIndicativoMetas
     * 
     * @return registroSubPlanIndicativoMetas
     */
    public Registro getRegistroSubPlanIndicativoMetas()
    {
        return registroSubPlanIndicativoMetas;
    }

    /**
     * Asigna el objeto registroSubPlanIndicativoMetas
     * 
     * @param registroSubPlanIndicativoMetas
     * Variable a asignar en registroSubPlanIndicativoMetas
     */
    public void setRegistroSubPlanIndicativoMetas(
        Registro registroSubPlanIndicativoMetas)
    {
        this.registroSubPlanIndicativoMetas = registroSubPlanIndicativoMetas;
    }

    // </SET_GET_ADICIONALES>

}
