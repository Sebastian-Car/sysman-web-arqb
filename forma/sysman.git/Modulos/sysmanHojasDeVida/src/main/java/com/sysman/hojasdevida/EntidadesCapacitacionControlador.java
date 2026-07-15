/*-
 * EntidadesCapacitacionControlador.java
 *
 * 1.0
 * 
 * 31/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.EntidadesCapacitacionControladorEnum;
import com.sysman.hojasdevida.enums.EntidadesCapacitacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: <code>entidadescapacitacion</code>. Fue
 * migrado del formulario en Access: ENTIDADESCAPACITACION.
 *
 * @version 1.0, 31/01/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class EntidadesCapacitacionControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL</code>.
     */
    private final String cSucursal = GeneralParameterEnum.SUCURSAL.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NIT</code>.
     */
    private final String cNit = EntidadesCapacitacionControladorEnum.NIT
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los items del combo Nit (CB5603).
     */
    private RegistroDataModelImpl listaNitEstablecimiento;

    /** Lista que contiene los items del combo Nit (CB5605). */
    private RegistroDataModelImpl listaPersonaContacto;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de EntidadesCapacitacionControlador
     */
    public EntidadesCapacitacionControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1670
            numFormulario = GeneralCodigoFormaEnum.ENTIDADES_CAPACITACION_CONTROLADOR
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
        cargarListaNitEstablecimiento();
        cargarListaPersonaContacto();
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
        enumBase = GenericUrlEnum.NAT_ENTIDADESCAPACITACION;

        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario. Se hace el llamado de
     * los enumerados CRUD del formulario.
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaNitEstablecimiento</code> asociada
     * al combo Nit de los datos de la institucion (CB5603).
     */
    public void cargarListaNitEstablecimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EntidadesCapacitacionControladorUrlEnum.URL4780
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put("NATURALEZA", "J");

        listaNitEstablecimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    /**
     * Carga la lista: <code>listaPersonaContacto</code> asociada al
     * combo Nit de la persona de contacto (CB5605).
     */
    public void cargarListaPersonaContacto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EntidadesCapacitacionControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaPersonaContacto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaNitEstablecimiento</code> asociada al combo Nit de
     * los datos de la institucion (CB5603).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitEstablecimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("NITESTABLECIMIENTO",
                        registroAux.getCampos().get(cNit));

        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));

        registro.getCampos().put("TELEFONOESTABLECIMIENTO",
                        registroAux.getCampos().get("TELEFONOS"));

        registro.getCampos().put("DIRECCIONESTABLECIMIENTO",
                        registroAux.getCampos().get("DIRECCION"));

        registro.getCampos().put("NOMBREESTABLECIMIENTO",
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaPersonaContacto</code> asociada al combo Nit de la
     * persona de contacto (CB5605)
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPersonaContacto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("PERSONACONTACTO",
                        registroAux.getCampos().get(cNit));

        registro.getCampos().put("NOMBRECONTACTO",
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put("SUCURSALCONTACTO",
                        registroAux.getCampos().get(cSucursal));

        registro.getCampos().put("DIRECCIONCONTACTO",
                        registroAux.getCampos().get("DIRECCION"));

        registro.getCampos().put("TELEFONOCONTACTO",
                        registroAux.getCampos().get("TELEFONOS"));

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton cmdCursosCarreras en la
     * vista. Redirecciona al formulario cursos - carreras (1681).
     */
    public void oprimircmdCursosCarreras() {
        // <CODIGO_DESARROLLADO>
        String nitEstablecimiento = SysmanFunciones
                        .nvl(registro.getCampos().get("NITESTABLECIMIENTO"), "")
                        .toString();

        String sucursal = SysmanFunciones
                        .nvl(registro.getCampos().get(cSucursal), "")
                        .toString();

        if (SysmanFunciones.validarVariableVacio(nitEstablecimiento)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3958"));
            return;
        }

        agregarRegistroNuevo(false); // Guardar cambios

        String[] campos = new String[2];

        campos[0] = EntidadesCapacitacionControladorEnum.IN_NIT_ESTABLECIMIENTO
                        .getValue();

        campos[1] = EntidadesCapacitacionControladorEnum.IN_SUCURSAL.getValue();

        Object[] valores = new Object[2];
        valores[0] = nitEstablecimiento;
        valores[1] = sucursal;

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.CURSOS_CARRERAS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
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
     * formulario.
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro.
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return true -> Permite realizar la insercion del registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro.
     * 
     * @return true -> Permite realizar la insercion o actualizacion
     * del registro.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRECONTACTO");

        if (css != null) {
            registro.getCampos().remove(cCompania);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     * 
     * @return true -> Permite eliminar el registro.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     * 
     * @return true
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

    public RegistroDataModelImpl getListaNitEstablecimiento() {
        return listaNitEstablecimiento;
    }

    public void setListaNitEstablecimiento(
        RegistroDataModelImpl listaNitEstablecimiento) {
        this.listaNitEstablecimiento = listaNitEstablecimiento;
    }

    public RegistroDataModelImpl getListaPersonaContacto() {
        return listaPersonaContacto;
    }

    public void setListaPersonaContacto(
        RegistroDataModelImpl listaPersonaContacto) {
        this.listaPersonaContacto = listaPersonaContacto;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
