/*-
 * FrmtipotransaccionsControlador.java
 *
 * 1.0
 * 
 * 25/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.transautomaticas;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.transautomaticas.enums.FrmtipotransaccionsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 * Permite configurar el tipo de transacciones.
 *
 * @version 1.0, 25/09/2018
 * @author jcaceres
 */
@ManagedBean
@ViewScoped
public class FrmtipotransaccionsControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>

    private String clasePresupuesto;

    private String rutaArchivo;

    private boolean porClase = true;

    private boolean visibleClase = true;

    private String tipoDocumento;

    private String rutaDocumentos;

    private String iTipoCpbt;
    
    private String contabilidadTipoCpbt;

    private String presupuestoTipoCpbt;

    /**
     * extension de la plantilla
     */
    private String extension;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private List<Registro> listaclasePpto;

    private RegistroDataModelImpl listaTipo;

    private RegistroDataModelImpl listaCodigoAfectar;

    private RegistroDataModelImpl listaCompRelacionadoPpto;

    private RegistroDataModelImpl listatipoDocumento;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmtipotransaccionsControlador
     */
    public FrmtipotransaccionsControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FR_TIPO_TRANSACCIONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
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
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipo();
        cargarListaCodigoAfectar();
        cargarListaCompRelacionadoPpto();
        cargarListaclasePpto();
        cargarListatipoDocumento();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.TIPOTRANSACCION;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     */

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaclasePpto
     */
    public void cargarListaclasePpto()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaclasePpto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmtipotransaccionsControladorUrlEnum.URL1996
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipo
     */
    public void cargarListaTipo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipotransaccionsControladorUrlEnum.URL1993
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoAfectar
     *
     */
    public void cargarListaCodigoAfectar()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipotransaccionsControladorUrlEnum.URL1994
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        listaCodigoAfectar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
        
    }

    /**
     * 
     * Carga la lista listaCompRelacionadoPpto
     */
    public void cargarListaCompRelacionadoPpto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipotransaccionsControladorUrlEnum.URL1995
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCompRelacionadoPpto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listaTipo_documento
     */
    public void cargarListatipoDocumento()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipotransaccionsControladorUrlEnum.URL1997
                                                        .getValue());

        listatipoDocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "CODIGO");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control porClase
     * 
     */
    public void cambiarporClase()
    {

        if ((boolean) registro.getCampos().get("PORCLASE"))
        {
            visibleClase = false;
        }
        else
        {
            visibleClase = true;
        }
    }
    
    public void cambiarclasePpto() {
        registro.getCampos().put("NOMBRECLASE", GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * verifica si el documento es de tipo Word o Excel.
     * 
     * @return verdadero si la extension es valida
     */
    public boolean validarExtension()
    {
        return Arrays.asList("doc", "docx", "xls", "xlsx", "xlsm")
                        .contains(extension);
    }

    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control
     * SubirArchivo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoSubirArchivo(FileUploadEvent event)
    {
            // <CODIGO_DESARROLLADO>
            // </CODIGO_DESARROLLADO>

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRETIPOMPROBANTE", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoAfectar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoAfectar(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("CODIGOAFECTAR", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRETIPOCONT", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        // CODIGOAFECTAR

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipo_documento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoDocumento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_DOCUMENTO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRETIPODOCUMENTO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCompRelacionadoPpto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCompRelacionadoPpto(SelectEvent event)
    {
        // FALTA REFERENCIAR

        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("COMPRELACIONADOPPTO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRETIPOPP", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
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
        /*
         * FR1919-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 80, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes()
    {

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;

    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues()
    {
        return true;

        // <CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO> 

    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        
        registro.getCampos().remove("NOMBRETIPOMPROBANTE");
        registro.getCampos().remove("NOMBRETIPODOCUMENTO");
        registro.getCampos().remove("NOMBRETIPOCONT");
        registro.getCampos().remove("NOMBRECLASE");
        registro.getCampos().remove("NOMBRETIPOPP");
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        //</CODIGO_DESARROLLADO> 
        return true;
        
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
     */
    @Override
    public boolean eliminarDespues()
    {
        listaInicial.load();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable contabilidadTipoCpbt
     * 
     * @return contabilidadTipoCpbt
     */
    public String getContabilidadTipoCpbt()
    {
        return contabilidadTipoCpbt;
    }

    /**
     * Asigna la variable contabilidadTipoCpbt
     * 
     * @param contabilidadTipoCpbt
     * Variable a asignar en contabilidadTipoCpbt
     */
    public void setContabilidadTipoCpbt(String contabilidadTipoCpbt)
    {
        this.contabilidadTipoCpbt = contabilidadTipoCpbt;
    }

    /**
     * Retorna la variable presupuestoTipoCpbt
     * 
     * @return presupuestoTipoCpbt
     */
    public String getPresupuestoTipoCpbt()
    {
        return presupuestoTipoCpbt;
    }

    /**
     * Asigna la variable presupuestoTipoCpbt
     * 
     * @param presupuestoTipoCpbt
     * Variable a asignar en presupuestoTipoCpbt
     */
    public void setPresupuestoTipoCpbt(String presupuestoTipoCpbt)
    {
        this.presupuestoTipoCpbt = presupuestoTipoCpbt;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaTipo()
    {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo)
    {
        this.listaTipo = listaTipo;
    }

    public RegistroDataModelImpl getListaCodigoAfectar()
    {
        return listaCodigoAfectar;
    }

    public void setListaCodigoAfectar(RegistroDataModelImpl listaCodigoAfectar)
    {
        this.listaCodigoAfectar = listaCodigoAfectar;
    }

    public RegistroDataModelImpl getListaCompRelacionadoPpto()
    {
        return listaCompRelacionadoPpto;
    }

    public void setListaCompRelacionadoPpto(
        RegistroDataModelImpl listaCompRelacionadoPpto)
    {
        this.listaCompRelacionadoPpto = listaCompRelacionadoPpto;
    }

    public String getiTipoCpbt()
    {
        return iTipoCpbt;
    }

    public void setiTipoCpbt(String iTipoCpbt)
    {
        this.iTipoCpbt = iTipoCpbt;
    }

    public String getClasePresupuesto()
    {
        return clasePresupuesto;
    }

    public void setClasePresupuesto(String clasePresupuesto)
    {
        this.clasePresupuesto = clasePresupuesto;
    }

    /**
     * Retorna la lista listaclasePpto
     * 
     * @return listaclasePpto
     */
    public List<Registro> getListaclasePpto()
    {
        return listaclasePpto;
    }

    /**
     * Asigna la lista listaclasePpto
     * 
     * @param listaclasePpto
     * Variable a asignar en listaclasePpto
     */
    public void setListaclasePpto(List<Registro> listaclasePpto)
    {
        this.listaclasePpto = listaclasePpto;
    }

    /**
     * Retorna la variable tipoDocumento
     * 
     * @return tipoDocumento
     */
    public String getTipoDocumento()
    {
        return tipoDocumento;
    }

    /**
     * Asigna la variable tipoDocumento
     * 
     * @param tipoDocumento
     * Variable a asignar en tipoDocumento
     */
    public void setTipoDocumento(String tipoDocumento)
    {
        this.tipoDocumento = tipoDocumento;
    }

    public RegistroDataModelImpl getListatipoDocumento() {
        return listatipoDocumento;
    }

    public void setListatipoDocumento(RegistroDataModelImpl listatipoDocumento) {
        this.listatipoDocumento = listatipoDocumento;
    }

    public boolean isPorClase()
    {
        return porClase;
    }

    public void setPorClase(boolean porClase)
    {
        this.porClase = porClase;
    }

    public String getRutaArchivo()
    {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo)
    {
        this.rutaArchivo = rutaArchivo;
    }

    public boolean isVisibleClase()
    {
        return visibleClase;
    }

    public void setVisibleClase(boolean visibleClase)
    {
        this.visibleClase = visibleClase;
    }

    public String getRutaDocumentos()
    {
        return rutaDocumentos;
    }

    public void setRutaDocumentos(String rutaDocumentos)
    {
        this.rutaDocumentos = rutaDocumentos;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
