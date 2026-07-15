/*-
 * PiPlanDesarrolloControlador.java
 *
 * 1.0
 * 
 * 26/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.planDesarrollo.enums.FrmVigenciasControladorEnum;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroRemote;
import com.sysman.plandesarrollo.enums.PiPlanDesarrolloControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite crear editar y eliminar el plan de
 * desarrollo de una vigencia.
 *
 * @version 1.0, 26/07/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class PiPlanDesarrolloControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaE;
    private RegistroDataModelImpl listasector;
    private RegistroDataModelImpl listasectorE;

    private List<Registro> listaVigenciaFinal;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private String vigencia;
    private String accion;
    private boolean bloqueadoDepen;
    private boolean bloqueadoVigencia;

    private Map<String, Object> parametrosEntrada;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPlanDesarrolloCeroRemote ejbPlanDesarrollo;

    /**
     * Crea una nueva instancia de PiPlanDesarrolloControlador
     */
    public PiPlanDesarrolloControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.PIPLAN_DESARROLLO_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {
                vigencia = parametrosEntrada.get("vigencia").toString();
                accion = parametrosEntrada.get("digAccion").toString();
            }
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
        tabla = GenericUrlEnum.BP_PLAN_INDICATIVO.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaVigenciaFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDependencia();
        cargarListaDependenciaE();
        cargarListasector();
        cargarListasectorE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);
        parametrosListado.put("ACCION", accion);

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        PiPlanDesarrolloControladorUrlEnum.URL0001.getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiPlanDesarrolloControladorUrlEnum.URL0002
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiPlanDesarrolloControladorUrlEnum.URL0003
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiPlanDesarrolloControladorUrlEnum.URL0004
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiPlanDesarrolloControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependenciaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiPlanDesarrolloControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listasector
     *
     */
    public void cargarListasector() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiPlanDesarrolloControladorUrlEnum.URL0006
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listasector = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listasector
     *
     */
    public void cargarListasectorE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiPlanDesarrolloControladorUrlEnum.URL0006
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listasectorE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaVigenciaFinal
     *
     */
    public void cargarListaVigenciaFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmVigenciasControladorEnum.PARAM0.getValue(),
                        compania);
        try {
            listaVigenciaFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PiPlanDesarrolloControladorUrlEnum.URL0007
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Id
     * 
     * 
     */
    public void cambiarId() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPENDENCIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listasector
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasector(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("SECTOR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listasector
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasectorE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        try {
            bloqueadoVigencia = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "PERMITE EXTENDER VIGENCIA FINAL METAS",
                            SessionUtil.getModulo(), new Date(), true));

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.VIGENCIA.getName(),
                            vigencia);
            Registro reg;
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PiPlanDesarrolloControladorUrlEnum.URL0008
                                                                            .getValue())
                                            .getUrl(), param));

            if (Integer.parseInt(
                            reg.getCampos().get("EXISTE").toString()) == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4173")
                                .replaceAll("#elemento#", "Resultado"));

            }

            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PiPlanDesarrolloControladorUrlEnum.URL0009
                                                                            .getValue())
                                            .getUrl(), param));

            if (Integer.parseInt(
                            reg.getCampos().get("EXISTE").toString()) == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4173")
                                .replaceAll("#elemento#", "Producto"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigencia);
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
        registro.getCampos().remove("NOMBRE_SECTOR");
        registro.getCampos().remove("NOMBRE_DEPENDENCIA");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        try {
            indice = listaInicial.getRowIndex();
            int existePlan;
            existePlan = (int) ejbPlanDesarrollo.cargarNivel(compania,
                            Integer.parseInt(vigencia), "");

            if (existePlan == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4086"));
            }
            else {
                if (registro.getCampos().get("ID")
                                .toString().length() == ejbPlanDesarrollo
                                                .obtenerDigitosMetaResultado()) {
                    registro.getCampos().put("TIPO_META_PLAN", "001");
                }
                else if (registro.getCampos().get("ID")
                                .toString().length() == ejbPlanDesarrollo
                                                .obtenerDigitosMetaProduccion()) {
                    registro.getCampos().put("TIPO_META_PLAN", "002");
                    registro.getCampos().put("ES_INDICADOR", "-1");
                }

                bloqueadoDepen = ejbPlanDesarrollo.obtenerManejaDependencia(
                                String.valueOf(registro.getCampos().get("ID")
                                                .toString().length()));

            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependenciaE() {
        return listaDependenciaE;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependenciaE(RegistroDataModelImpl listaDependenciaE) {
        this.listaDependenciaE = listaDependenciaE;
    }

    /**
     * Retorna la lista listasector
     * 
     * @return listasector
     */
    public RegistroDataModelImpl getListasector() {
        return listasector;
    }

    /**
     * Asigna la lista listasector
     * 
     * @param listasector
     * Variable a asignar en listasector
     */
    public void setListasector(RegistroDataModelImpl listasector) {
        this.listasector = listasector;
    }

    /**
     * Retorna la lista listasector
     * 
     * @return listasector
     */
    public RegistroDataModelImpl getListasectorE() {
        return listasectorE;
    }

    /**
     * Asigna la lista listasector
     * 
     * @param listasector
     * Variable a asignar en listasector
     */
    public void setListasectorE(RegistroDataModelImpl listasectorE) {
        this.listasectorE = listasectorE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
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

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isBloqueadoDepen() {
        return bloqueadoDepen;
    }

    public void setBloqueadoDepen(boolean bloqueadoDepen) {
        this.bloqueadoDepen = bloqueadoDepen;
    }

    public List<Registro> getListaVigenciaFinal() {
        return listaVigenciaFinal;
    }

    public void setListaVigenciaFinal(List<Registro> listaVigenciaFinal) {
        this.listaVigenciaFinal = listaVigenciaFinal;
    }

    public boolean isBloqueadoVigencia() {
        return bloqueadoVigencia;
    }

    public void setBloqueadoVigencia(boolean bloqueadoVigencia) {
        this.bloqueadoVigencia = bloqueadoVigencia;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
