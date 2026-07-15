/*-
 * JuridentificacionsControlador.java
 *
 * 1.0
 * 
 * 05/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.JuridentificacionsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de gestionar las personas juridicas uqe se
 * relacionan con terceros
 *
 * @version 1.0, 05/02/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class JuridentificacionsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    /**
     * Variable encargada de almacenar temporalmente la respuesta de
     * la base de datos a la tabla Tercero
     */
    private RegistroDataModelImpl listaNitSociedad;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable encargada de almacenar temporalmente la respuesta de
     * la base de datos a la tabla Tercero
     */
    private RegistroDataModelImpl listacedula;

    /**
     * Constante encargada de almacenar el String PAIS_NOMBRE
     */
    private final String paisNombreCons;
    /**
     * Constante encargada de almacenar el String DEPARTAMENTO_NOMBRE
     */
    private final String departamentoNombreCons;
    /**
     * Constante encargada de almacenar el String MUNICIPIO_NOMBRE
     */
    private final String municipioNombreCons;
    /**
     * Constante encargada de almacenar el String TELEFONOS
     */

    private final String telefonoCons;
    /**
     * Constante encargada de almacenar el String APELLIDO1
     */
    private final String apellido1Cons;
    /**
     * Constante encargada de almacenar el String APELLIDO2
     */
    private final String apeliido2Cons;
    /**
     * Constante encargada de almacenar el String TIPO_DOCU
     */
    private final String tipoDocuCons;
    /**
     * Lista encargada de almacenar temporalemnte la respuesta de la
     * base de datos y mostrarlo en el combo responab.le
     */
    private RegistroDataModelImpl listaResponsable;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de JuridentificacionsControlador
     */
    public JuridentificacionsControlador() {
        super();
        compania = SessionUtil.getCompania();

        paisNombreCons = "PAIS_NOMBRE";
        departamentoNombreCons = "DEPARTAMENTO_NOMBRE";
        municipioNombreCons = "MUNICIPIO_NOMBRE";
        telefonoCons = "TELEFONOS";
        apellido1Cons = "APELLIDO1";
        apeliido2Cons = "APELLIDO2";
        tipoDocuCons = "TIPO_DOCU";

        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            rid = (Map<String, Object>) parametros.get("rid");
            accion = (String) parametros.get("accion");
        }

        try {

            numFormulario = GeneralCodigoFormaEnum.JURIDENTIFICACIONS_CONTROLADOR
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
        cargarListacedula();
        cargarListaNitSociedad();
        cargarListaResponsable();
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

        enumBase = GenericUrlEnum.NAT_JURIDENTIFICACION;
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * 
     * Carga la lista listacedula Metodo encargado de hacer el llamado
     * a la base de datos y almacenar la respuesta en la lista
     * listacedula
     *
     */
    public void cargarListacedula() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        JuridentificacionsControladorUrlEnum.URL6229
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

        // 14148 COMPANIA
    }

    /**
     * 
     * Carga la lista listacedula Metodo encargado de hacer el llamado
     * a la base de datos y almacenar la respuesta en la lista
     * listaResponsable
     */
    public void cargarListaResponsable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        JuridentificacionsControladorUrlEnum.URL6230
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

        // 61027 COMPANIA
    }

    /**
     * 
     * 
     * Carga la lista listacedula Metodo encargado de hacer el llamado
     * a la base de datos y almacenar la respuesta en la lista
     * listaNitSociedad
     *
     */

    public void cargarListaNitSociedad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        JuridentificacionsControladorUrlEnum.URL6231
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNitSociedad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

        // 14150 COMPANIA
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control PAIS
     * 
     * 
     */
    public void cambiarPAIS() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DEPARTAMENTO
     * 
     * 
     */
    public void cambiarDEPARTAMENTO() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * sys
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacedula
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NUMERO_IDREPRE",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSAL_REPRE",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        registro.getCampos().put("TIPOID_REPRE",
                        registroAux.getCampos().get("TIPOID"));

        registro.getCampos().put(tipoDocuCons,
                        registroAux.getCampos().get(tipoDocuCons));

        registro.getCampos().put(apellido1Cons,
                        registroAux.getCampos().get(apellido1Cons));
        registro.getCampos().put(apeliido2Cons,
                        registroAux.getCampos().get(apeliido2Cons));
        registro.getCampos().put(GeneralParameterEnum.NOMBRES.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNitSociedad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitSociedad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NITSOCIEDAD",
                        registroAux.getCampos().get("NIT"));

        registro.getCampos().put("RAZONSOCIAL",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(paisNombreCons,
                        registroAux.getCampos().get(paisNombreCons));

        registro.getCampos().put(departamentoNombreCons,
                        registroAux.getCampos().get(departamentoNombreCons));

        registro.getCampos().put(municipioNombreCons,
                        registroAux.getCampos().get(municipioNombreCons));

        registro.getCampos().put(GeneralParameterEnum.DIRECCION.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DIRECCION
                                                        .getName()));
        registro.getCampos().put(telefonoCons,
                        registroAux.getCampos().get(telefonoCons));
        registro.getCampos().put("FAX", registroAux.getCampos().get("FAX"));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RESPONSABLE",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSAL_RESPONSABLE",
                        registroAux.getCampos().get("SUCURSAL"));

        registro.getCampos().put("NOMBRE_RESPONSABLE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put("CARGO_RESPONSABLE",
                        registroAux.getCampos().get("CARGO"));

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btmServicios en la vista
     *
     *
     */
    public void oprimirbtmServicios() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("nitSociedad", retornarString(registro, "NITSOCIEDAD"));

        parametros.put("sucursal", retornarString(registro,
                        GeneralParameterEnum.SUCURSAL.getName()));

        parametros.put("nombreSociedad",
                        retornarString(registro, "RAZONSOCIAL"));

        parametros.put("accion", accion);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.SERVICIOSPERSONAJURIDICAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdExperiencia en la vista
     *
     *
     */
    public void oprimircmdExperiencia() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("nitSociedad", retornarString(registro, "NITSOCIEDAD"));

        parametros.put("sucursal", retornarString(registro,
                        GeneralParameterEnum.SUCURSAL.getName()));
        parametros.put("accion", accion);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.JUREXPYSITUAACTUALS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

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
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
        eliminarCamposRegistro();
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
        if (css != null) {
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        }
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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacedula
     * 
     * @return listacedula
     */
    public RegistroDataModelImpl getListacedula() {
        return listacedula;
    }

    /**
     * Asigna la lista listacedula
     * 
     * @param listacedula
     * Variable a asignar en listacedula
     */
    public void setListacedula(RegistroDataModelImpl listacedula) {
        this.listacedula = listacedula;
    }

    public RegistroDataModelImpl getListaNitSociedad() {
        return listaNitSociedad;
    }

    public void setListaNitSociedad(RegistroDataModelImpl listaNitSociedad) {
        this.listaNitSociedad = listaNitSociedad;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public RegistroDataModelImpl getListaResponsable() {
        return listaResponsable;
    }

    public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
        this.listaResponsable = listaResponsable;
    }

    private void eliminarCamposRegistro() {
        registro.getCampos().remove("RAZONSOCIAL");
        registro.getCampos().remove(paisNombreCons);
        registro.getCampos().remove(departamentoNombreCons);
        registro.getCampos().remove(municipioNombreCons);
        registro.getCampos().remove(GeneralParameterEnum.DIRECCION.getName());
        registro.getCampos().remove(telefonoCons);
        registro.getCampos().remove("FAX");
        registro.getCampos().remove(tipoDocuCons);
        registro.getCampos().remove(apellido1Cons);
        registro.getCampos().remove(apeliido2Cons);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
        registro.getCampos().remove("CARGO_RESPONSABLE");
        registro.getCampos().remove("NOMBRE_RESPONSABLE");

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
