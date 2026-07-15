/*-
 * LegalizacionViaticosControlador.java
 *
 * 1.0
 * 
 * 17/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.ejb.EjbViaticosCeroRemote;
import com.sysman.viaticos.enums.LegalizacionViaticosControladorEnum;
import com.sysman.viaticos.enums.LegalizacionViaticosControladorUrlEnum;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para administrar la legalizacion de los viaticos
 *
 * @version 1.0, 17/01/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped

public class LegalizacionViaticosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del codigo de solicitud
     * seleccionado
     */
    private String solicitud;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de los años
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los terceros
     */
    private RegistroDataModelImpl listaTercero;
    /**
     * Lista de registros de los viaticos
     */
    private RegistroDataModelImpl listaSolicitud;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbViaticosCeroRemote ejbViaticosCero;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de LegalizacionViaticosControlador
     */
    @SuppressWarnings("unchecked")
    public LegalizacionViaticosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1607
            numFormulario = GeneralCodigoFormaEnum.LEGALIZACION_VIATICOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
            }
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
        cargarListaTercero();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaAno();
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
        enumBase = GenericUrlEnum.VI_LEGALIZACION_VIATICOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
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
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LegalizacionViaticosControladorUrlEnum.URL184
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
     * Carga la lista listaTercero
     * 
     */
    public void cargarListaTercero() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LegalizacionViaticosControladorUrlEnum.URL213
                                                        .getValue());
        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        LegalizacionViaticosControladorEnum.NIT.getValue());
    }

    /**
     * 
     * Carga la lista listaSolicitud
     */
    public void cargarListaSolicitud() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));
        param.put(LegalizacionViaticosControladorEnum.CODIGOTERCERO.getValue(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.TERCERO
                                                        .getName()));

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LegalizacionViaticosControladorUrlEnum.URL235
                                                        .getValue());
        listaSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        LegalizacionViaticosControladorEnum.CODSOLICITUD
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        solicitud = null;
        cargarListaSolicitud();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos()
                                        .get(LegalizacionViaticosControladorEnum.NIT
                                                        .getValue()));
        registro.getCampos()
                        .put(LegalizacionViaticosControladorEnum.NOMBRETERCERO
                                        .getValue(), registroAux.getCampos()
                                                        .get(GeneralParameterEnum.NOMBRE
                                                                        .getName()));
        registro.getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(),
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.SUCURSAL
                                                                        .getName()));
        solicitud = null;
        cargarListaSolicitud();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSolicitud
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSolicitud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        solicitud = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODSOLICITUD"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnDetalle en la vista
     *
     */
    public void oprimirBtnDetalle() {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarVariableVacio(solicitud)) {

            try {
                ejbViaticosCero.crearDetalleLegalizaViaticos(compania,
                                Integer.parseInt(registro.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                                .toString()),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.TERCERO
                                                                .getName())
                                                .toString(),
                                solicitud,
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName())
                                                .toString(),
                                SessionUtil.getUser().getCodigo());

            }
            catch (NumberFormatException | SystemException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }
        String[] campos = { "ridSub", "tercero", "sucursal",
                            "nombreTercero" };
        Object[] valores = { css,
                             registro.getCampos()
                                             .get(GeneralParameterEnum.TERCERO
                                                             .getName())
                                             .toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.SUCURSAL
                                                             .getName())
                                             .toString(),
                             registro.getCampos()
                                             .get(LegalizacionViaticosControladorEnum.NOMBRETERCERO
                                                             .getValue())
                                             .toString() };

        SessionUtil.redireccionarPorFormulario(SessionUtil.getModulo(),
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUB_LEGALIZACION_VIATICOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
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

        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos()
                            .put(LegalizacionViaticosControladorEnum.VALOR_LEGALIZADO
                                            .getValue(), "0");
            registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                            new Date());
        }
        else if (ACCION_MODIFICAR.equals(accion)) {
            cargarListaSolicitud();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.VI_LEGALIZACION_VIATICOS.getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania,
                                            "''"),
                            GeneralParameterEnum.NUMERO.getName(), "1");
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            consecutivo);
            registro.getCampos()
                            .put(LegalizacionViaticosControladorEnum.TIPO_VIATICO
                                            .getValue(), "1");
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(LegalizacionViaticosControladorEnum.NOMBRETERCERO
                                        .getValue());
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            registro.getCampos()
                            .remove(LegalizacionViaticosControladorEnum.TIPO_VIATICO
                                            .getValue());

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
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
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable solicitud
     * 
     * @return solicitud
     */
    public String getSolicitud() {
        return solicitud;
    }

    /**
     * Asigna la variable solicitud
     * 
     * @param solicitud
     * Variable a asignar en solicitud
     */
    public void setSolicitud(String solicitud) {
        this.solicitud = solicitud;
    }

    // </SET_GET_ATRIBUTOS>
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
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaSolicitud
     * 
     * @return listaSolicitud
     */
    public RegistroDataModelImpl getListaSolicitud() {
        return listaSolicitud;
    }

    /**
     * Asigna la lista listaSolicitud
     * 
     * @param listaSolicitud
     * Variable a asignar en listaSolicitud
     */
    public void setListaSolicitud(RegistroDataModelImpl listaSolicitud) {
        this.listaSolicitud = listaSolicitud;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
