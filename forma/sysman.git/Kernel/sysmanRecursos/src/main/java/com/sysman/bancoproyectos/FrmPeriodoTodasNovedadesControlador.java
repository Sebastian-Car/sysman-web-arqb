/*-
 * FrmPeriodoTodasNovedades.java
 *
 * 1.0
 * 
 * 22/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmPeriodoTodasNovedadesControladorEnum;
import com.sysman.bancoproyectos.enums.FrmPeriodoTodasNovedadesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.RetencionsControlador;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Se migro el formulario periodotodosnovedades del modulo banco de
 * proyectos SysmanBP2018.05.02.access
 *
 * @version 1.0, 22/05/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class FrmPeriodoTodasNovedadesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String vigenciaPeriodo;

    private String nombre;

    private String ano;

    private String tipoT;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private String modulo = SessionUtil.getModulo();
    /**
     * Lista que trae la lista de años del combo
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que trae la lista tiponovedad del combo
     */
    private RegistroDataModelImpl listaTipoNovedad;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmPeriodoTodasNovedades
     */
    public FrmPeriodoTodasNovedadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1797;
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
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoNovedad();
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
     * Carga la lista listaAno
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPeriodoTodasNovedadesControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(RetencionsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipoNovedad
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaTipoNovedad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPeriodoTodasNovedadesControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        if (!SessionUtil.getGrupo(modulo).isEsAdministrador()) {
            param.put(FrmPeriodoTodasNovedadesControladorEnum.TIPO.getValue(),
                            "MOD");
        }
        else {
            param.put(FrmPeriodoTodasNovedadesControladorEnum.TIPO.getValue(),
                            "MOD,CDP");
        }

        param.put(FrmPeriodoTodasNovedadesControladorEnum.CLASET.getValue(),
                        "B");

        listaTipoNovedad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NOMBRE");

        // listaTipoNovedad = new
        // RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
        // ":FR1797_nuevo:TBCB5974", "SELECT " +
        // " NOMBRE, " +
        // " TIPOT, " +
        // " CLASET " +
        // " FROM " +
        // " BPTIPONOVEDAD " +
        // " WHERE " +
        // " TIPOT <> 'SCD'" +
        // " AND " +
        // " CLASET = 'B' " +
        // " AND " +
        // " COMPANIA = :COMPANIA " +
        // " ORDER BY " +
        // " NOMBRE",
        // true, "NOMBRE");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_SOLICITUD_CDP_CONTROLADOR
                                        .getCodigo()));

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("vigenciaPeriodo", ano);
        parametros.put("tipo", tipoT);
        parametros.put("clase", "B");
        parametros.put("TipoTmodal", tipoT);
        direccionador.setParametros(parametros);

        RequestContext.getCurrentInstance().closeDialog(direccionador);
        // SessionUtil.getNivelUsuario(modulo)
        // </CODIGO_DESARROLLADO>

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoNovedad *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoNovedad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        setNombre(SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString());
        tipoT = (String) registroAux.getCampos().get("TIPOT");

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoNovedad
     * 
     * @return listaTipoNovedad
     */
    public RegistroDataModelImpl getListaTipoNovedad() {
        return listaTipoNovedad;
    }

    /**
     * Asigna la lista listaTipoNovedad
     * 
     * @param listaTipoNovedad
     * Variable a asignar en listaTipoNovedad
     */
    public void setListaTipoNovedad(RegistroDataModelImpl listaTipoNovedad) {
        this.listaTipoNovedad = listaTipoNovedad;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre
     * the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * @param ano
     * the ano to set
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * @return the tipoT
     */
    public String getTipoT() {
        return tipoT;
    }

    /**
     * @param tipoT
     * the tipoT to set
     */
    public void setTipoT(String tipoT) {
        this.tipoT = tipoT;
    }

    /**
     * @return the vigenciaPeriodo
     */
    public String getVigenciaPeriodo() {
        return vigenciaPeriodo;
    }

    /**
     * @param vigenciaPeriodo
     * the vigenciaPeriodo to set
     */
    public void setVigenciaPeriodo(String vigenciaPeriodo) {
        this.vigenciaPeriodo = vigenciaPeriodo;
    }

}
