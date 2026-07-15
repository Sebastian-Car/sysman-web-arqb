/*-
 * FrmtarifasintrecargoControlador.java
 *
 * 1.0
 *
 * 25/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmtarifasintrecargoControladorEnum;
import com.sysman.serviciospublicos.enums.FrmtarifasintrecargoControladorUrlEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase en la cual se permite ingresar los recargos que se cobraran
 * para cada concepto por tarifa
 *
 * @version 1.0, 25/01/2017
 * @author mzanguna
 * 
 * @author eamaya
 * @version 2, 01/06/2017 Proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped

public class FrmtarifasintrecargoControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private static final String CODIGOCONST = "CODIGO";
    private static final String NOMBRECONST = "NOMBRE";
    private static final String TABLATARIFAINTCONST = "SP_TARIFA_INT_RECA";

    // <DECLARAR_ATRIBUTOS>
    private int anio;
    private String periodo;
    private String uso;
    private String estrato;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Combo grande para mostrar el concepto
     */
    private RegistroDataModelImpl listaCONCEPTO;
    private RegistroDataModelImpl listaCONCEPTOE;

    private String auxiliar;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista para la consulta del subformulario
     */
    private List<Registro> listaFrmtarifasintrec;
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
     * Crea una nueva instancia de FrmtarifasintrecargoControlador
     */
    public FrmtarifasintrecargoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMTARIFASINTRECARGO_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                anio = (int) parametrosEntrada.get("anio");
                periodo = parametrosEntrada.get("periodo").toString();
                uso = parametrosEntrada.get("uso").toString();
                estrato = parametrosEntrada.get("estrato").toString();
            }

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
        cargarListaFrmtarifasintrec();
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
        listaFrmtarifasintrec = null;
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
        enumBase = GenericUrlEnum.SP_TARIFA_INT_RECA;

        buscarLlave();
        asignarOrigenDatos();
        iniciarListasSub();
        iniciarListas();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

    }

    /**
     *
     * Carga la lista listaFrmtarifasintrec
     *
     * Consulta del formulario principal
     */
    public void cargarListaFrmtarifasintrec() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);
            param.put(GeneralParameterEnum.PERIODO.getName(),
                            periodo);
            param.put(FrmtarifasintrecargoControladorEnum.PARAM0.getValue(),
                            String.valueOf(uso));
            param.put(GeneralParameterEnum.ESTRATO.getName(),
                            estrato);

            listaFrmtarifasintrec = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmtarifasintrecargoControladorUrlEnum.URL5929
                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            TABLATARIFAINTCONST));

        }

        catch (SysmanException | SystemException e) {  
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCONCEPTO
     *
     * Combo grande de conceptos
     */
    public void cargarListaCONCEPTO() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtarifasintrecargoControladorUrlEnum.URL7175
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCONCEPTO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGOCONST);
    }

    public void cargarListaCONCEPTOE() {
        listaCONCEPTOE = listaCONCEPTO;

    }

    public void seleccionarFilaCONCEPTO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("CONCEPTO",
                        registroAux.getCampos().get(CODIGOCONST));
    }

    public void seleccionarFilaCONCEPTOE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(CODIGOCONST).toString();
    }

    /**
     * Metodo de insercion del formulario Frmtarifasintrec
     *
     * Inserci�n de nuevos registros en la tabla SP_TARIFA_INT_RECA
     */
    public void agregarRegistroSubFrmtarifasintrec() {
        try {
            registroSub.getCampos().remove(NOMBRECONST);
            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put("ANO", anio);
            registroSub.getCampos().put("PERIODO", periodo);
            registroSub.getCampos().put("USO", uso);
            registroSub.getCampos().put("ESTRATO", estrato);
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put("DATE_CREATED", new Date());
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_TARIFA_INT_RECA
                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

            cargarListaFrmtarifasintrec();
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
     * Metodo de edicion del formulario Frmtarifasintrec
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubFrmtarifasintrec(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            registroSub.getCampos().remove(NOMBRECONST);
            reg.getCampos().remove(NOMBRECONST);
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().put("MODIFIED_BY",
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put("DATE_MODIFIED", new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_TARIFA_INT_RECA
                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
            cargarListaFrmtarifasintrec();

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaFrmtarifasintrec();
        }
    }

    /**
     * Metodo de eliminacion del formulario Frmtarifasintrec
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubFrmtarifasintrec(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_TARIFA_INT_RECA
                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaFrmtarifasintrec();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Frmtarifasintrec
     *
     */
    public void cancelarEdicionFrmtarifasintrec() {
        cargarListaFrmtarifasintrec();
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Frmtarifasintrec public void
     * cancelarEdicionFrmtarifasintrec() {
     * cargarListaFrmtarifasintrec(); }
     *
     * // </METODOS_SUBFORM> //
     * <METODOS_ADICIONALES> // </METODOS_ADICIONALES> /** Este metodo
     * es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
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

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

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
     * Metodo para la eliminaci�n de los datos del frm principal
     *
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCONCEPTO
     *
     * @return listaCONCEPTO
     */
    public RegistroDataModelImpl getListaCONCEPTO() {
        return listaCONCEPTO;
    }

    /**
     * Asigna la lista listaCONCEPTO
     *
     * @param listaCONCEPTO
     * Variable a asignar en listaCONCEPTO
     */
    public void setListaCONCEPTO(RegistroDataModelImpl listaCONCEPTO) {
        this.listaCONCEPTO = listaCONCEPTO;
    }

    public RegistroDataModelImpl getListaCONCEPTOE() {
        return listaCONCEPTOE;
    }

    /**
     * Asigna la lista listaCONCEPTO
     *
     * @param listaCONCEPTO
     * Variable a asignar en listaCONCEPTO
     */
    public void setListaCONCEPTOE(RegistroDataModelImpl listaCONCEPTOE) {
        this.listaCONCEPTOE = listaCONCEPTOE;
    }
    /**
     * Retorna la lista listaFrmtarifasintrec
     *
     * @return listaFrmtarifasintrec
     */
    public List<Registro> getListaFrmtarifasintrec() {
        return listaFrmtarifasintrec;
    }

    /**
     * Asigna la lista listaFrmtarifasintrec
     *
     * @param listaFrmtarifasintrec
     * Variable a asignar en listaFrmtarifasintrec
     */
    public void setListaFrmtarifasintrec(List<Registro> listaFrmtarifasintrec) {
        this.listaFrmtarifasintrec = listaFrmtarifasintrec;
    }   
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

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
}
