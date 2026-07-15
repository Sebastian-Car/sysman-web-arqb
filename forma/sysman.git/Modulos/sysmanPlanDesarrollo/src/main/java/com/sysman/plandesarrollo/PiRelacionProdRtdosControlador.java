/*-
 * PiRelacionProdRtdosControlador.java
 *
 * 1.0
 * 
 * 01/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.PiRelacionProdRtdosControladorEnum;
import com.sysman.plandesarrollo.enums.PiRelacionProdRtdosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
 * Ésta clase es el controlador para el formulario PiRelacionProdRtdos
 * en access "frmpirelacionprodrtdo" el cual es llamado desde Plan de
 * Desarrollo/Archivos/Relación Meta Producto\Resultado
 * 
 * @version 1.0, 01/03/2018
 * @author jmalaver
 */
@ManagedBean
@ViewScoped
public class PiRelacionProdRtdosControlador extends BeanBaseContinuoAcmeImpl {
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
    /**
     * Listado de registros para el combo de AnoPlan
     */
    private List<Registro> listaAnoPlan;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Listado de registros para el combo de IdPlanProducto
     */
    private RegistroDataModelImpl listaIdPlanProducto;

    /**
     * Listado de registros para el combo de IdPlanProductoE
     */
    private RegistroDataModelImpl listaIdPlanProductoE;

    /**
     * Listado de registros para el combo de IdPlanResultado
     */
    private RegistroDataModelImpl listaIdPlanResultado;

    /**
     * Listado de registros para el combo de IdPlanResultadoE
     */
    private RegistroDataModelImpl listaIdPlanResultadoE;

    /**
     * Esta variable se usa como auxiliar para almacenar el
     * identificador del registro que se selecciona
     */
    private String auxiliar;

    /**
     * Esta variable se usa como auxiliar para almacenar el codigo
     * producto del registro
     */
    private String auxiliarCodProd;

    /**
     * Esta variable se usa como auxiliar para almacenar el nombre
     * producto del registro
     */
    private String auxiliarNombProd;

    /**
     * Esta variable se usa como auxiliar para almacenar el codigo
     * resultado del registro
     */
    private String auxiliarCodResul;

    /**
     * Esta variable se usa como auxiliar para almacenar el nombre
     * resultado del registro
     */
    private String auxiliarNombResul;

    /**
     * Esta variable se usa como auxiliar para almacenar el año
     * obtenido mediante la funcion sysmanfunciones.ano
     */
    private int anioPlan;

    /**
     * Esta variable se usa como auxiliar para almacenar el número de
     * checkbox seleccionados en un momento determinado
     */
    private int cantidad;

    /**
     * Esta variable se usa para almacenar el consecutivo generado
     * para cada uno de los registros que se agregan
     * 
     */
    private long consecutivo;

    /**
     * Esta variable se usa como auxiliar para almacenar el valor de
     * digitos obtenido para meta resultado
     * 
     */
    private String digMetaResul;

    /**
     * Esta variable se usa como auxiliar para almacenar el valor de
     * digitos obtenido para meta producto
     * 
     */
    private String digMetaProd;

    /**
     * Constante definida por el numero de veces que se hace el
     * llamado al texto ID_PLAN
     */
    private final String cIdPlan;

    /**
     * Constante definida por el numero de veces que se hace el
     * llamado al texto ID_PLAN_PRODUCTO
     */
    private final String cIdPlanProducto;

    /**
     * Constante definida por el numero de veces que se hace el
     * llamado al texto CODIGO_PRODUCTO
     */
    private final String cCodigoProducto;

    /**
     * Constante definida por el numero de veces que se hace el
     * llamado al texto NOMBRE_PRODUCTO
     */
    private final String cNombreProducto;

    /**
     * Constante definida por el numero de veces que se hace el
     * llamado al texto NOMBRE_RESULTADO
     */
    private final String cNombreResultado;

    /**
     * Constante definida por el numero de veces que se hace el
     * llamado al texto CODIGO
     */
    private final String cCodigo;

    /**
     * Constante definida por el numero de veces que se hace el
     * llamado al texto NOMBRE
     */
    private final String cNombre;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PiRelacionProdRtdosControlador
     */
    public PiRelacionProdRtdosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cIdPlan = "ID_PLAN";
        cIdPlanProducto = "ID_PLAN_PRODUCTO";
        cCodigoProducto = "CODIGO_PRODUCTO";
        cNombreProducto = "NOMBRE_PROD";
        cNombreResultado = "NOMBRE_RESUL";
        cCodigo = "CODIGO";
        cNombre = "NOMBRE";
        anioPlan = SysmanFunciones.ano(new Date());
        try {
            numFormulario = 1730;
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
        enumBase = GenericUrlEnum.PI_RELACION_PROD_RTDO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();

        obtenerDigitos();
        // <CARGAR_LISTA>
        cargarListaAnoPlan();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaIdPlanProducto();
        cargarListaIdPlanProductoE();
        cargarListaIdPlanResultado();
        cargarListaIdPlanResultadoE();
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
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("ANIOPLAN", anioPlan);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoPlan
     */
    public void cargarListaAnoPlan() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        try {
            listaAnoPlan = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PiRelacionProdRtdosControladorUrlEnum.URL5920
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaIdPlanProducto
     */
    public void cargarListaIdPlanProducto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiRelacionProdRtdosControladorUrlEnum.URL6594
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(PiRelacionProdRtdosControladorEnum.ANIO.getValue(),
                        String.valueOf(anioPlan));
        param.put(PiRelacionProdRtdosControladorEnum.DIGMETAPROD.getValue(),
                        String.valueOf(digMetaProd));

        listaIdPlanProducto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdPlan);

    }

    /**
     * 
     * Carga la lista listaIdPlanProductoE
     */
    public void cargarListaIdPlanProductoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiRelacionProdRtdosControladorUrlEnum.URL7663
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(PiRelacionProdRtdosControladorEnum.ANIO.getValue(),
                        String.valueOf(anioPlan));
        param.put(PiRelacionProdRtdosControladorEnum.DIGMETAPROD.getValue(),
                        String.valueOf(digMetaProd));

        listaIdPlanProductoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdPlan);
    }

    /**
     * 
     * Carga la lista listaIdPlanResultado
     */
    public void cargarListaIdPlanResultado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiRelacionProdRtdosControladorUrlEnum.URL8758
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(PiRelacionProdRtdosControladorEnum.ANIO.getValue(),
                        String.valueOf(anioPlan));
        param.put(PiRelacionProdRtdosControladorEnum.DIGMETAPROD.getValue(),
                        String.valueOf(digMetaResul));

        listaIdPlanResultado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdPlan);
    }

    /**
     * 
     * Carga la lista listaIdPlanResultadoE
     */

    public void cargarListaIdPlanResultadoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PiRelacionProdRtdosControladorUrlEnum.URL9842
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(PiRelacionProdRtdosControladorEnum.ANIO.getValue(),
                        String.valueOf(anioPlan));
        param.put(PiRelacionProdRtdosControladorEnum.DIGMETAPROD.getValue(),
                        String.valueOf(digMetaResul));

        listaIdPlanResultadoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdPlan);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoPlan
     * 
     */
    public void cambiarAnoPlan() {
        cargarListaIdPlanProducto();
        cargarListaIdPlanProductoE();
        cargarListaIdPlanResultado();
        cargarListaIdPlanResultadoE();
        reasignarOrigen();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IdPlanProducto en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarIdPlanProductoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cCodigoProducto, auxiliarCodProd);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreProducto, auxiliarNombProd);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IdPlanResultado en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarIdPlanResultadoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("CODIGO_RESULTADO", auxiliarCodResul);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreResultado, auxiliarNombResul);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Principal
     * 
     */

    public void cambiarPrincipal() {

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.VIGENCIA.getName(), anioPlan);
        params.put(PiRelacionProdRtdosControladorEnum.ID_PLAN_PRODUCTO
                        .getValue(),
                        registro.getCampos().get(cIdPlanProducto));
        params.put(PiRelacionProdRtdosControladorEnum.CODIGO_PRODUCTO
                        .getValue(),
                        registro.getCampos().get(cCodigoProducto));

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PiRelacionProdRtdosControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), params));
            if (rs != null) {
                cantidad = Integer
                                .parseInt(rs.getCampos().get("CANT")
                                                .toString());
                if (cantidad > 0) {
                    String mensaje = idioma.getString("TB_TB4005");
                    mensaje = mensaje.replace("s$idPlan$s",
                                    registro.getCampos().get(cIdPlanProducto)
                                                    .toString());

                    mensaje = mensaje.replace("s$nombrePlan$s",
                                    registro.getCampos().get(cNombreProducto)
                                                    .toString());
                    JsfUtil.agregarMensajeInformativo(
                                    mensaje);
                    registro.getCampos().put("PRINCIPAL", false);
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo ejecutado al cambiar el control Principal en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPrincipalC(int rowNum) {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.VIGENCIA.getName(), anioPlan);
        params.put(PiRelacionProdRtdosControladorEnum.ID_PLAN_PRODUCTO
                        .getValue(),
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get(cIdPlanProducto));
        params.put(PiRelacionProdRtdosControladorEnum.CODIGO_PRODUCTO
                        .getValue(),
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get(cCodigoProducto));

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PiRelacionProdRtdosControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), params));
            if (rs != null) {
                cantidad = Integer
                                .parseInt(rs.getCampos().get("CANT")
                                                .toString());
                if (cantidad > 0) {
                    String mensaje = idioma.getString("TB_TB4005");
                    mensaje = mensaje.replace("s$idPlan$s",
                                    listaInicial.getDatasource()
                                                    .get(rowNum % 10)
                                                    .getCampos()
                                                    .get(cIdPlanProducto)
                                                    .toString());

                    mensaje = mensaje.replace("s$nombrePlan$s",
                                    listaInicial.getDatasource()
                                                    .get(rowNum % 10)
                                                    .getCampos()
                                                    .get(cNombreProducto)
                                                    .toString());
                    JsfUtil.agregarMensajeInformativo(
                                    mensaje);
                    listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                    .put("PRINCIPAL", false);
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </CODIGO_DESARROLLADO>

    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIdPlanProducto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdPlanProducto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cIdPlanProducto,
                        registroAux.getCampos().get(cIdPlan));
        registro.getCampos().put(cCodigoProducto,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cNombreProducto,
                        registroAux.getCampos().get(cNombre));
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "PRINCIPAL")) {
            cambiarPrincipal();
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIdPlanProducto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdPlanProductoE(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cIdPlan).toString();
        auxiliarCodProd = registroAux.getCampos()
                        .get(cCodigo).toString();
        auxiliarNombProd = registroAux.getCampos()
                        .get(cNombre).toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIdPlanResultado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdPlanResultado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_PLAN_RESULTADO",
                        registroAux.getCampos().get(cIdPlan));
        registro.getCampos().put("CODIGO_RESULTADO",
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cNombreResultado,
                        registroAux.getCampos().get(cNombre));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIdPlanResultado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdPlanResultadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cIdPlan).toString();
        auxiliarCodResul = registroAux.getCampos()
                        .get(cCodigo).toString();
        auxiliarNombResul = registroAux.getCampos()
                        .get(cNombre).toString();

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
     * @return valor booleano true
     */
    @Override
    public boolean insertarAntes() {
        generarConsecutivo();
        registro.getCampos().remove(cNombreProducto);
        registro.getCampos().remove(cNombreResultado);
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("VIGENCIA", anioPlan);
        registro.getCampos().put("CONSECUTIVO", consecutivo);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return valor booleano true
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
     * @return valor booleano true
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
     * @return valor booleano true
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
     * @return valor booleano true
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
     * @return valor booleano true
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
        registro.getCampos().remove(cNombreProducto);
        registro.getCampos().remove(cNombreResultado);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <>
        // </>
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * @return the anioPlan
     */
    public int getAnioPlan() {
        return anioPlan;
    }

    /**
     * @param anioPlan
     * the anioPlan to set
     */
    public void setAnioPlan(int anioPlan) {
        this.anioPlan = anioPlan;
    }

    /**
     * @return the digMetaResul
     */
    public String getDigMetaResul() {
        return digMetaResul;
    }

    /**
     * @param digMetaResul
     * the digMetaResul to set
     */
    public void setDigMetaResul(String digMetaResul) {
        this.digMetaResul = digMetaResul;
    }

    /**
     * @return the digMetaProd
     */
    public String getDigMetaProd() {
        return digMetaProd;
    }

    /**
     * @param digMetaProd
     * the digMetaProd to set
     */
    public void setDigMetaProd(String digMetaProd) {
        this.digMetaProd = digMetaProd;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoPlan
     * 
     * @return listaAnoPlan
     */
    public List<Registro> getListaAnoPlan() {
        return listaAnoPlan;
    }

    /**
     * Asigna la lista listaAnoPlan
     * 
     * @param listaAnoPlan
     * Variable a asignar en listaAnoPlan
     */
    public void setListaAnoPlan(List<Registro> listaAnoPlan) {
        this.listaAnoPlan = listaAnoPlan;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaIdPlanProducto
     * 
     * @return listaIdPlanProducto
     */
    public RegistroDataModelImpl getListaIdPlanProducto() {
        return listaIdPlanProducto;
    }

    /**
     * Asigna la lista listaIdPlanProducto
     * 
     * @param listaIdPlanProducto
     * Variable a asignar en listaIdPlanProducto
     */
    public void setListaIdPlanProducto(
        RegistroDataModelImpl listaIdPlanProducto) {
        this.listaIdPlanProducto = listaIdPlanProducto;
    }

    /**
     * Retorna la lista listaIdPlanProducto
     * 
     * @return listaIdPlanProducto
     */
    public RegistroDataModelImpl getListaIdPlanProductoE() {
        return listaIdPlanProductoE;
    }

    /**
     * Asigna la lista listaIdPlanProducto
     * 
     * @param listaIdPlanProducto
     * Variable a asignar en listaIdPlanProducto
     */
    public void setListaIdPlanProductoE(
        RegistroDataModelImpl listaIdPlanProductoE) {
        this.listaIdPlanProductoE = listaIdPlanProductoE;
    }

    /**
     * Retorna la lista listaIdPlanResultado
     * 
     * @return listaIdPlanResultado
     */
    public RegistroDataModelImpl getListaIdPlanResultado() {
        return listaIdPlanResultado;
    }

    /**
     * Asigna la lista listaIdPlanResultado
     * 
     * @param listaIdPlanResultado
     * Variable a asignar en listaIdPlanResultado
     */
    public void setListaIdPlanResultado(
        RegistroDataModelImpl listaIdPlanResultado) {
        this.listaIdPlanResultado = listaIdPlanResultado;
    }

    /**
     * Retorna la lista listaIdPlanResultado
     * 
     * @return listaIdPlanResultado
     */
    public RegistroDataModelImpl getListaIdPlanResultadoE() {
        return listaIdPlanResultadoE;
    }

    /**
     * Asigna la lista listaIdPlanResultado
     * 
     * @param listaIdPlanResultado
     * Variable a asignar en listaIdPlanResultado
     */
    public void setListaIdPlanResultadoE(
        RegistroDataModelImpl listaIdPlanResultadoE) {
        this.listaIdPlanResultadoE = listaIdPlanResultadoE;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Metodo que genera el valor consecutivo del registro en la tabla
     * 
     */

    public void generarConsecutivo() {
        try {
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "PI_RELACION_PROD_RTDO",
                            "COMPANIA = ''" + compania + "'' " +
                                "  AND   VIGENCIA = " + anioPlan +
                                "  AND   ID_PLAN_PRODUCTO = ''"
                                + registro.getCampos().get("ID_PLAN_PRODUCTO")
                                + "'' " +
                                "  AND   CODIGO_PRODUCTO = ''"
                                + registro.getCampos().get(cCodigoProducto)
                                + "''",
                            "CONSECUTIVO", "1");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que obtiene el número de digitos que tiene configurado
     * el plan meta producto y meta resultado
     * 
     */

    public void obtenerDigitos() {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.VIGENCIA.getName(), anioPlan);

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PiRelacionProdRtdosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), params));
            if (rs != null) {
                digMetaResul = rs.getCampos().get("DIGITOS").toString();
            }

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PiRelacionProdRtdosControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), params));
            if (rs != null) {
                digMetaProd = rs.getCampos().get("DIGITOS").toString();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
}
