/*-
 * PorSectoresControlador.java
 *
 * 1.0
 * 
 * 06/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.PorSectoresControladorEnum;
import com.sysman.plandesarrollo.enums.PorSectoresControladorUrlEnum;
import com.sysman.plantillas.UtilitarioPlantillas;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Clase migrada para generar el informe departamental de plan de
 * accion
 *
 * @version 1.0, 06/09/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class PorSectoresControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del año seleccionado
     */
    private String vigencia;
    /**
     * Atributo que almacena el valor del sector seleccionado
     */
    private String sector;
    /**
     * Atributo que almacena el valor de la dependencia seleccionada
     */
    private String dependencia;
    /**
     * Atributo que almacena el codigo de la plantilla seleccioando
     */
    private String modeloPlantilla;
    private Date fechaPlantilla;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de los años
     */
    private List<Registro> listaVigencia;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los sectores
     */
    private RegistroDataModelImpl listaSector;
    /**
     * Lista de registros de las dependencias
     */
    private RegistroDataModelImpl listaDependencia;
    /**
     * Lista de registros de las plantillas
     */
    private RegistroDataModelImpl listaPlantilla;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PorSectoresControlador
     */
    public PorSectoresControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1910
            numFormulario = GeneralCodigoFormaEnum.POR_SECTORES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaVigencia();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaSector();
        cargarListaDependencia();
        cargarListaPlantilla();
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigencia
     *
     */
    public void cargarListaVigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PorSectoresControladorUrlEnum.URL144
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaSector
     *
     */
    public void cargarListaSector() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PorSectoresControladorUrlEnum.URL186
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaSector = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PorSectoresControladorUrlEnum.URL208
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaPlantilla
     *
     */
    public void cargarListaPlantilla() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PorSectoresControladorUrlEnum.URL232
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.TIPO.getName(), "58");
        listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    /**
     * Metodo que realiza el metodo de generacion de la plantilla
     */
    @SuppressWarnings("unchecked")
    public void generarExcel() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.TIPO.getName(), "58");
            param.put(GeneralParameterEnum.CODIGO.getName(), "67001");
            param.put(PorSectoresControladorEnum.FECHAGENERACION.getValue(),
                            new Date());

            Registro rsFecha = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PorSectoresControladorUrlEnum.URL271
                                                                            .getValue())
                                            .getUrl(), param));

            archivoDescarga = null;
            HashMap<String, String> variablesConsultaW = new HashMap<>();
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$ano$s", vigencia);
            variablesConsultaW.put("s$sector$s", "'" + sector + "'");
            variablesConsultaW.put("s$dependencia$s", "'" + dependencia + "'");

            Map<String, String> variablesConsulta = (HashMap<String, String>) SessionUtil
                            .getSessionVar("variablesConsultaWord");

            if (rsFecha != null) {
                
                archivoDescarga = UtilitarioPlantillas.exportarDocumento(
                                "67001",
                                SysmanFunciones.formatearFecha((Date) rsFecha
                                                .getCampos()
                                                .get(GeneralParameterEnum.FECHA
                                                                .getName())),
                                variablesConsulta, idioma.getString("TB_TB4253"),
                                null);
            } else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("MSG_GESTIONAUTOSERVICIO_NOREPORTE")); 
                
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSector
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSector(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        sector = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlantilla
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        modeloPlantilla = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        fechaPlantilla = (Date) SysmanFunciones.nvl(
                        registroAux.getCampos().get("FECHAAUX"),
                        "");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public String getVigencia() {
        return vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    /**
     * Retorna la variable sector
     * 
     * @return sector
     */
    public String getSector() {
        return sector;
    }

    /**
     * Asigna la variable sector
     * 
     * @param sector
     * Variable a asignar en sector
     */
    public void setSector(String sector) {
        this.sector = sector;
    }

    /**
     * Retorna la variable dependencia
     * 
     * @return dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * Asigna la variable dependencia
     * 
     * @param dependencia
     * Variable a asignar en dependencia
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    /**
     * Retorna la variable modeloPlantilla
     * 
     * @return modeloPlantilla
     */
    public String getModeloPlantilla() {
        return modeloPlantilla;
    }

    /**
     * Asigna la variable modeloPlantilla
     * 
     * @param modeloPlantilla
     * Variable a asignar en modeloPlantilla
     */
    public void setModeloPlantilla(String modeloPlantilla) {
        this.modeloPlantilla = modeloPlantilla;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaSector
     * 
     * @return listaSector
     */
    public RegistroDataModelImpl getListaSector() {
        return listaSector;
    }

    /**
     * Asigna la lista listaSector
     * 
     * @param listaSector
     * Variable a asignar en listaSector
     */
    public void setListaSector(RegistroDataModelImpl listaSector) {
        this.listaSector = listaSector;
    }

    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaPlantilla
     * 
     * @return listaPlantilla
     */
    public RegistroDataModelImpl getListaPlantilla() {
        return listaPlantilla;
    }

    /**
     * Asigna la lista listaPlantilla
     * 
     * @param listaPlantilla
     * Variable a asignar en listaPlantilla
     */
    public void setListaPlantilla(RegistroDataModelImpl listaPlantilla) {
        this.listaPlantilla = listaPlantilla;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
