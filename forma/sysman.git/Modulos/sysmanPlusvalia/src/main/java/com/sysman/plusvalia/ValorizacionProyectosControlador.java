/*-
 * ValorizacionProyectosControlador.java
 *
 * 1.0
 * 
 * 08/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plusvalia.enums.PlusvaliaProyectosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 08/03/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ValorizacionProyectosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String categoria;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaTipoComprobante;
    private RegistroDataModelImpl listaBpCodigo;
    private RegistroDataModelImpl listaClase;
    private BigInteger idProyecto;
    private String codigoProyecto;
    private String claseProyecto;
    private Map<String, Object> ridProyecto;
    private String nombreClase;
    private String encabezado;
    private String Clase;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ValorizacionProyectosControlador
     */
    public ValorizacionProyectosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        categoria = "16";

        Clase = "45";

        encabezado = idioma.getString("TB_TB4287");

        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            idProyecto = (BigInteger) parametros.get("idProyecto");
            codigoProyecto = parametros
                            .get("codigoProyecto").toString();
            claseProyecto = (String) parametros
                            .get("claseProyecto");
            rid = (Map<String, Object>) parametros
                            .get("rid");
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.VALORIZACION_PROYECTOS_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoComprobante();
        cargarListaBpCodigo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
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
    public void iniciarListasSubNulo() {
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
    public void inicializar() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("CLASEVP", Clase);
        enumBase = GenericUrlEnum.VP_PROYECTOS;
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

        buscarUrls();

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoComprobante
     *
     */
    public void cargarListaTipoComprobante() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaProyectosControladorUrlEnum.URL1022
                                                        .getValue());

        listaTipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaBpCodigo
     *
     */
    public void cargarListaBpCodigo() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaProyectosControladorUrlEnum.URL558
                                                        .getValue());

        listaBpCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobante
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_COMPROBANTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBpCodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBpCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("BP_CODIGO",
                        registroAux.getCampos().get("CODIGO"));

        registro.getCampos().put("NOMBREACTIVIDAD",
                        registroAux.getCampos().get("NOMBRE"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Beneficiarios en la vista
     *
     *
     */
    public void oprimirBeneficiarios() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("idProyecto", registro.getCampos().get("ID"));
        param.put("codigoProyecto", registro.getCampos().get("CODIGO"));
        param.put("claseProyecto", Clase);
        param.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.VALORIZACION_BENEFICIARIOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ConfigurarConceptos en la
     * vista
     *
     *
     */
    public void oprimirConfigurarConceptos() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("idProyecto", registro.getCampos().get("ID"));
        param.put("codigoProyecto",
                        registro.getCampos().get("CODIGO"));
        param.put("claseProyecto",
                        Clase);
        param.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.VALORIZACION_CONCEPTOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Anexos en la vista
     *
     *
     */
    public void oprimirAnexos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Destinacionconomica en la
     * vista
     *
     *
     */
    public void oprimirDestinacionconomica() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        String[] campos = { "idProyecto",
                            "codigoProyecto",
                            "claseProyecto",
                            "claseFactor",
                            "rid" };
        Object[] valores = { registro.getCampos().get("ID"),
                             registro.getCampos().get("CODIGO"),
                             Clase,
                             "FDE",
                             css };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.VALORIZACION_FACTORES_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GradoBeneficio en la vista
     *
     *
     */
    public void oprimirGradoBeneficio() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        String[] campos = { "idProyecto",
                            "codigoProyecto",
                            "claseProyecto",
                            "claseFactor",
                            "rid" };
        Object[] valores = { registro.getCampos().get("ID"),
                             registro.getCampos().get("CODIGO"),
                             Clase,
                             "FGB",
                             css };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.VALORIZACION_FACTORES_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if ((rid != null) && !rid.isEmpty()) {
            cargarRegistro(rid, ACCION_MODIFICAR);
        }
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

        if (css == null) {

            registro.getCampos().put("FECHA_INICIAL_PROYECTO", new Date());
            registro.getCampos().put("FECHA_FINAL_PROYECTO", new Date());
            registro.getCampos().put("PORCENTAJE_DESCUENTO", "0");
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove("NOMBRECLASE");
        registro.getCampos().remove("NOMBREACTIVIDAD");
        registro.getCampos().put("CLASE", Clase);
        registro.getCampos().remove("PI_ID_PLAN");
        registro.getCampos().remove("PI_TIPO");
        registro.getCampos().remove("ANO_BASE");
        registro.getCampos().remove("MES_BASE");
        registro.getCampos().remove("BP_CODIGO");

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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRECLASE");
        registro.getCampos().remove("NOMBREACTIVIDAD");
        registro.getCampos().remove("ID");
        registro.getCampos().put("CLASE", Clase);
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cleanFlash();
        SessionUtil.redireccionarMenu();
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * @param categoria
     * the categoria to set
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Retorna la lista listaBpCodigo
     * 
     * @return listaBpCodigo
     */
    public RegistroDataModelImpl getListaBpCodigo() {
        return listaBpCodigo;
    }

    /**
     * Asigna la lista listaBpCodigo
     * 
     * @param listaBpCodigo
     * Variable a asignar en listaBpCodigo
     */
    public void setListaBpCodigo(RegistroDataModelImpl listaBpCodigo) {
        this.listaBpCodigo = listaBpCodigo;
    }

    /**
     * Retorna la lista listaClase
     * 
     * @return listaClase
     */
    public RegistroDataModelImpl getListaClase() {
        return listaClase;
    }

    /**
     * Asigna la lista listaClase
     * 
     * @param listaClase
     * Variable a asignar en listaClase
     */
    public void setListaClase(RegistroDataModelImpl listaClase) {
        this.listaClase = listaClase;
    }

    /**
     * @return the nombreClase
     */
    public String getNombreClase() {
        return nombreClase;
    }

    /**
     * @param nombreClase
     * the nombreClase to set
     */
    public void setNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    /**
     * @return the encabezado
     */
    public String getEncabezado() {
        return encabezado;
    }

    /**
     * @param encabezado
     * the encabezado to set
     */
    public void setEncabezado(String encabezado) {
        this.encabezado = encabezado;
    }

    /**
     * @return the listaTipoComprobante
     */
    public RegistroDataModelImpl getListaTipoComprobante() {
        return listaTipoComprobante;
    }

    /**
     * @param listaTipoComprobante
     * the listaTipoComprobante to set
     */
    public void setListaTipoComprobante(
        RegistroDataModelImpl listaTipoComprobante) {
        this.listaTipoComprobante = listaTipoComprobante;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
