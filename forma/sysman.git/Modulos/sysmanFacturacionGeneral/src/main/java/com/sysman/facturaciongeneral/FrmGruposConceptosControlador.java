/*-
 * FrmGruposConceptosControlador.java
 *
 * 1.0
 * 
 * 22/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmGruposConceptosControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmGruposConceptosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite configurar los grupos de conceptos.
 *
 * @version 1.0, 22/11/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class FrmGruposConceptosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String consNomConcepto;
    // <DECLARAR_ATRIBUTOS>

    private String tipoCobro;
    private String anio;
    private String auxiliar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCodigoGr;
    private RegistroDataModelImpl listaCodigoGrE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaFrmgruposconceptos;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmGruposConceptosControlador
     */
    public FrmGruposConceptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNomConcepto= "NOMCONCEPTO";
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());

        anio = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_GRUPOS_CONCEPTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCONCEPTO();
        cargarListaCONCEPTOE();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaFrmgruposconceptos();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaFrmgruposconceptos = null;
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
    public void inicializar() {
        enumBase = GenericUrlEnum.SF_GRUPOS_CONCEPTOS;
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
    public void asignarOrigenDatos() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        FrmGruposConceptosControladorEnum.PARAM0.getValue(),
                        tipoCobro);
        buscarUrls();
    }

    /**
     * 
     * Carga la lista listaSubfrm_gruposconceptos
     *
     */
    public void cargarListaFrmgruposconceptos() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmGruposConceptosControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get(FrmGruposConceptosControladorEnum.PARAM0.getValue()));
        param.put(FrmGruposConceptosControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get("CODIGO"));
        param.put(GeneralParameterEnum.ANO.getName(),anio);

        try {
            listaFrmgruposconceptos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmGruposConceptosControladorUrlEnum.URL9064
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "SF_DETALLE_GRUPOS_CONCEPTOS"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCONCEPTO
     *
     */
    public void cargarListaCONCEPTO() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmGruposConceptosControladorUrlEnum.URL7458
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(FrmGruposConceptosControladorEnum.PARAM0.getValue(),
                        tipoCobro);

        listaCodigoGr = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONCEPTO.getName());
    }

    /**
     * 
     * Carga la lista listaCONCEPTO
     *
     */
    public void cargarListaCONCEPTOE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmGruposConceptosControladorUrlEnum.URL7458
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(FrmGruposConceptosControladorEnum.PARAM0.getValue(),
                        tipoCobro);

        listaCodigoGrE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONCEPTO.getName());
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CONCEPTO en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoGrC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaFrmgruposconceptos.get(rowNum).getCampos().put(GeneralParameterEnum.CONCEPTO.getName(), registroSub.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()) == null ? " "
            : registroSub.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()).toString());
        listaFrmgruposconceptos.get(rowNum).getCampos().put(consNomConcepto,
                        registroSub.getCampos().get(consNomConcepto) == null ? " "
                            : registroSub.getCampos().get(consNomConcepto).toString());
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCONCEPTO
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoGr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));
        registroSub.getCampos().put(consNomConcepto,
                        registroAux.getCampos().get(consNomConcepto));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCONCEPTO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoGrE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar =  registroAux.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()).toString();
        registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));
        registroSub.getCampos().put(consNomConcepto,
                        registroAux.getCampos().get(consNomConcepto));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Subfrm_gruposconceptos
     * 
     */
    public void agregarRegistroSubFrmgruposconceptos() {
        try {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSub.getCampos().put(FrmGruposConceptosControladorEnum.PARAM0.getValue(),registro.getCampos().get(FrmGruposConceptosControladorEnum.PARAM0.getValue()));
            registroSub.getCampos().remove(consNomConcepto);
            registroSub.getCampos().put("GRUPO",registro.getCampos().get("CODIGO"));
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_GRUPOS_CONCEPTOS
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaFrmgruposconceptos();
           
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Subfrm_gruposconceptos
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubFrmgruposconceptos(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
            reg.getCampos().remove(consNomConcepto);
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_GRUPOS_CONCEPTOS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
            cargarListaFrmgruposconceptos();
        }
    }

    /**
     * Metodo de eliminacion del formulario Subfrm_gruposconceptos
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubFrmgruposconceptos(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_GRUPOS_CONCEPTOS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaFrmgruposconceptos();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subfrm_gruposconceptos
     *
     */
    public void cancelarEdicionFrmgruposconceptos() {
        cargarListaFrmgruposconceptos();
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(FrmGruposConceptosControladorEnum.PARAM0.getValue(),tipoCobro);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if(accion.equals(ACCION_MODIFICAR)){
            registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCONCEPTO
     * 
     * @return listaCONCEPTO
     */
    public RegistroDataModelImpl getListaCodigoGr() {
        return listaCodigoGr;
    }
    /**
     * Asigna la lista listaCONCEPTO
     * 
     * @param listaCONCEPTO
     * Variable a asignar en listaCONCEPTO
     */
    public void setListaCodigoGr(RegistroDataModelImpl listaCodigoGr) {
        this.listaCodigoGr = listaCodigoGr;
    }
    

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>





    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubfrm_gruposconceptos
     * 
     * @return listaSubfrm_gruposconceptos
     */
    public List<Registro> getListaFrmgruposconceptos() {
        return listaFrmgruposconceptos;
    }

    public RegistroDataModelImpl getListaCodigoGrE() {
        return listaCodigoGrE;
    }


    public void setListaCodigoGrE(RegistroDataModelImpl listaCodigoGrE) {
        this.listaCodigoGrE = listaCodigoGrE;
    }
    /**
     * Asigna la lista listaSubfrm_gruposconceptos
     * 
     * @param listaSubfrm_gruposconceptos
     * Variable a asignar en listaSubfrm_gruposconceptos
     */
    public void setListaFrmgruposconceptos(
        List<Registro> listaFrmgruposconceptos) {
        this.listaFrmgruposconceptos = listaFrmgruposconceptos;
    }
    
    




    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_ADICIONALES>
}
