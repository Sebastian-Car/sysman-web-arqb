/*-
 * CotizacionesActividadesControlador.java
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
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.CotizacionesActividadesControladorEnum;
import com.sysman.hojasdevida.enums.CotizacionesActividadesControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: <code>cotizacionesactividades</code>.
 * Resultado de migrar el formulario CotizacionesActividades del
 * modulo SysmanHv2018.01.07_HV_SST_Manual_SelPersonal_Bienestar.
 *
 * @version 1.0, 05/02/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class CotizacionesActividadesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>DIRECCIONCONTACTO</code>.
     */
    private final String cDireccionContacto = CotizacionesActividadesControladorEnum.DIRECCIONCONTACTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>DIRECCIONESTABLECIMIENTO</code>.
     */
    private final String cDireccionEstablecimiento = CotizacionesActividadesControladorEnum.DIRECCIONESTABLECIMIENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>FECHAINICIAL</code>.
     */
    private final String cFechaInicial = GeneralParameterEnum.FECHAINICIAL
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>IDEVENTO</code>.
     */
    private final String cIdEvento = CotizacionesActividadesControladorEnum.IDEVENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NITESTABLECIMIENTO</code>.
     */
    private final String cNitEstablecimiento = CotizacionesActividadesControladorEnum.NITESTABLECIMIENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBREESTABLECIMIENTO</code>.
     */
    private final String cNombreEstablecimiento = CotizacionesActividadesControladorEnum.NOMBREESTABLECIMIENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL</code>.
     */
    private final String cSucursal = GeneralParameterEnum.SUCURSAL.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSALESTABLECIMIENTO</code>.
     */
    private final String cSucursalEstablecimiento = CotizacionesActividadesControladorEnum.SUCURSALESTABLECIMIENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TELEFONOCONTACTO</code>.
     */
    private final String cTelefonoContacto = CotizacionesActividadesControladorEnum.TELEFONOCONTACTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TELEFONOESTABLECIMIENTO</code>.
     */
    private final String cTelefonoEstablecimiento = CotizacionesActividadesControladorEnum.TELEFONOESTABLECIMIENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPOEVENTO</code>.
     */
    private final String cTipoEvento = CotizacionesActividadesControladorEnum.TIPOEVENTO
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que contiene el codigo asignado a la actividad
     * programada.
     */
    private String idEvento;

    /**
     * Variable que contiene el tipo de evento asignado a la actividad
     * programada.
     */
    private int tipoEvento;

    /**
     * Variable que contiene la fecha inicial asignada en la actividad
     * programada.
     */
    private Date fechaInicial;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del combo nit (CB5660). */
    private RegistroDataModelImpl listaNitEstablecimiento;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de CotizacionesActividadesControlador
     */
    public CotizacionesActividadesControlador() {
        super();

        compania = SessionUtil.getCompania();

        Map<String, Object> parametersIn = SessionUtil.getFlash();

        if (parametersIn != null) {
            idEvento = (String) parametersIn
                            .get(CotizacionesActividadesControladorEnum.PR_IDEVENTO
                                            .getValue());

            tipoEvento = (int) parametersIn
                            .get(CotizacionesActividadesControladorEnum.PR_TIPOEVENTO
                                            .getValue());

            fechaInicial = (Date) parametersIn
                            .get(CotizacionesActividadesControladorEnum.PR_FECHAINICIAL
                                            .getValue());
        }

        try {
            // 1695
            numFormulario = GeneralCodigoFormaEnum.COTIZACIONES_ACTIVIDADES_CONTROLADOR
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
        enumBase = GenericUrlEnum.NAT_COTIZACIONESACTIVIDADES;

        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de consultas para las operaciones CRUD
     * del formulario.
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cIdEvento, idEvento);
        parametrosListado.put(cTipoEvento, tipoEvento);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaNitEstablecimiento</code> asociada
     * al combo nit (CB5660).
     */
    public void cargarListaNitEstablecimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CotizacionesActividadesControladorUrlEnum.URL5096
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaNitEstablecimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNitEstablecimiento);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaNitEstablecimiento</code> asociada al combo nit
     * (CB5660).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitEstablecimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cNitEstablecimiento,
                        registroAux.getCampos().get(cNitEstablecimiento));

        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursalEstablecimiento));

        registro.getCampos().put(cNombreEstablecimiento,
                        registroAux.getCampos().get(cNombreEstablecimiento));

        registro.getCampos().put(cDireccionEstablecimiento, registroAux
                        .getCampos().get(cDireccionEstablecimiento));

        registro.getCampos().put(cTelefonoEstablecimiento,
                        registroAux.getCampos().get(cTelefonoEstablecimiento));

        registro.getCampos().put(cDireccionContacto,
                        registroAux.getCampos().get(cDireccionContacto));

        registro.getCampos().put(cTelefonoContacto,
                        registroAux.getCampos().get(cTelefonoContacto));
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
        registro.getCampos().put(cFechaInicial, fechaInicial);
        // registro.getCampos().remove(cFechaInicial);
        registro.getCampos().put(cIdEvento, idEvento);
        registro.getCampos().put(cTipoEvento, tipoEvento);
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
     * @return true -> Permite realizar la insercion o actualizacion.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cDireccionContacto);
        registro.getCampos().remove(cTelefonoEstablecimiento);
        registro.getCampos().remove(cDireccionEstablecimiento);
        registro.getCampos().remove(cNombreEstablecimiento);
        registro.getCampos().remove(cTelefonoContacto);

        if (css != null) {
            registro.getCampos().remove(cCompania);
            registro.getCampos().remove(cFechaInicial);
            registro.getCampos().remove(cTipoEvento);
            registro.getCampos().remove(cIdEvento);
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

    /** Metodo ejecutado cuando se cierra el formulario. */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNitEstablecimiento
     * 
     * @return listaNitEstablecimiento
     */
    public RegistroDataModelImpl getListaNitEstablecimiento() {
        return listaNitEstablecimiento;
    }

    /**
     * Asigna la lista listaNitEstablecimiento
     * 
     * @param listaNitEstablecimiento
     * Variable a asignar en listaNitEstablecimiento
     */
    public void setListaNitEstablecimiento(
        RegistroDataModelImpl listaNitEstablecimiento) {
        this.listaNitEstablecimiento = listaNitEstablecimiento;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
