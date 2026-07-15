/*-
 * FrmPlanAdquisicionesControlador.java
 *
 * 1.0
 * 
 * 11/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloDosRemote;
import com.sysman.plandesarrollo.enums.FrmPlanAdquisicionesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que administra los planes de adquisiciones del plan de
 * accion
 *
 * @version 1.0, 11/04/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmPlanAdquisicionesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor de la vigencia seleccionada en
     * la forma
     */
    private String vigencia;

    /**
     * Atributo que almacena el valor de GetTipoT()
     */
    private String tipo;

    /**
     * Atributo que almacena el valor de GetNumeroT()
     * 
     */
    private String numero;

    /**
     * Atributo que almacena la vigencia GetVIGENCIA_GUB()
     * 
     */
    private String vigenciaGubernamental;

    /**
     * Atributo que almacena los digitos de GetACCION()
     */
    private int digitosAccion;

    /**
     * Atributo que almacena el id del plan del formulario anterior
     */

    private String idPlan;

    /**
     * Atributo que almacena el total del valor estimado
     */
    private String valorTotalEstimado;

    /**
     * Atributo que almacena el consecutivo del codigo del plan de
     * adquisicion
     */
    private long constanteCodigo;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    /**
     * Atributo que almacena el valor estimado anterior en el registro
     * a editar
     */

    private Double valorEstimadoViejo;

    /**
     * Atributo que administra el bloqueo de campos de cierres
     * adquisiciones
     */

    private boolean bloqueaPlaneado;

    /**
     * Atributo que administra el bloqueo de campos de cierres
     * adquisiciones ejecutadas
     */
    private boolean bloqueaEjecutado;

    /**
     * Atributo que sirve como bandera para validar las acciones que
     * se estan ejecutando
     */
    private boolean editando;
    /**
     * Atributo que almacena el nombre del del plan seleccionado en el
     * subformulario
     */
    private String nombrePlanAdquisiciones;
    /**
     * Atributo que almacena la descripcion del plan seleccionado
     */
    private String nomPlanAdquisiciones;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que carga las vigencias
     */

    private List<Registro> listaVigencia;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena las modalidades de contrato
     */
    private RegistroDataModelImpl listaModalidadContrato;
    /**
     * Lista que almacena las modalidades de contrato en la grilla
     */
    private RegistroDataModelImpl listaModalidadContratoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esAdministrador;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esJefeUnidad;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private String dependencia;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private String predecesor;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private int digitosMetaProducto;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esUsuarioConsulta;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private String vigenciaFinal;
    /**
     * Atributo que almacena el nombre del id seleccionado en el arbol
     */
    private String nombrePlan;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPlanDesarrolloDosRemote ejbPlanDesarrolloDos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmPlanAdquisicionesControlador
     */
    public FrmPlanAdquisicionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        vigencia = Integer.toString(SysmanFunciones.ano(new Date()));
        valorTotalEstimado = "0";

        //
        bloqueaEjecutado = true;
        bloqueaPlaneado = true;
        editando = false;

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_PLAN_ADQUISICIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                esAdministrador = (boolean) parametrosEntrada
                                .get("administrador");
                esJefeUnidad = (boolean) parametrosEntrada
                                .get("jefeUnidad");
                dependencia = SysmanFunciones.nvl(
                                parametrosEntrada.get("dependencia"),
                                "").toString();
                predecesor = SysmanFunciones.nvl(
                                parametrosEntrada.get("predecesor"),
                                "").toString();
                digitosMetaProducto = (int) SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get("digitosMetaProducto"), 0);

                esUsuarioConsulta = (boolean) parametrosEntrada
                                .get("esUsuarioConsulta");

                vigenciaFinal = SysmanFunciones.nvl(parametrosEntrada
                                .get("vigenciaFinal"), "").toString();

                tipo = parametrosEntrada.get("tipo").toString();
                numero = parametrosEntrada.get("numero").toString();
                vigenciaGubernamental = parametrosEntrada
                                .get("vigenciaGubernamental").toString();
                idPlan = parametrosEntrada.get("idPlan").toString();
                digitosAccion = Integer
                                .parseInt(parametrosEntrada.get("digitosAccion")
                                                .toString());
                nombrePlan = parametrosEntrada.get("nombrePlan").toString();
                nomPlanAdquisiciones = parametrosEntrada
                                .get("nombrePlanAdquisciones").toString();
            }

            nombrePlanAdquisiciones = SysmanFunciones
                            .concatenar("Plan Adquisiciones", " ", idPlan);

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

        tabla = "PI_PLAN_ADQUISICIONES";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaVigencia();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaModalidadContrato();
        cargarListaModalidadContratoE();
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

        parametrosListado.put("TIPO", tipo);

        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numero);

        parametrosListado.put("VIGENCIAGUB", vigenciaGubernamental);

        parametrosListado.put(GeneralParameterEnum.ID_PLAN.getName(),
                        idPlan);

        parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesControladorUrlEnum.URL6994
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesControladorUrlEnum.URL6995
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesControladorUrlEnum.URL6996
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesControladorUrlEnum.URL6997
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigencia
     *
     */
    public void cargarListaVigencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPlanAdquisicionesControladorUrlEnum.URL5943
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
     * Carga la lista listaModalidadContrato
     *
     */
    public void cargarListaModalidadContrato() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesControladorUrlEnum.URL6418
                                                        .getValue());

        listaModalidadContrato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaModalidadContrato
     *
     */
    public void cargarListaModalidadContratoE() {
        listaModalidadContratoE = listaModalidadContrato;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Detalle
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirDetalle(Registro reg, int indice) {

        String[] campos = { "tipo", "numero", "idPlan", "vigenciaGub",
                            "codigo", "totalEstimado" };

        Object[] valores = { tipo, numero, idPlan, vigenciaGubernamental,
                             reg.getCampos()
                                             .get(GeneralParameterEnum.CODIGO
                                                             .getName())
                                             .toString(),
                             valorTotalEstimado };

        SessionUtil.cargarModalDatosFlash(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_PLAN_ADQUISICIONES_VIGENCIA_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);

    }

    // </METODOS_BOTONES>

    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Vigencia
     * 
     */
    public void cambiarVigencia() {
        reasignarOrigen();
        cargarTotalEstimado();
        validarEdicion();
    }

    /**
     * Metodo ejecutado al cambiar el control Eliminar en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEliminarC(int rowNum) {

        if (!permiteEliminar(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()))) {

            editando = false;

            actualizarAntes();

        }

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModalidadContrato
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidadContrato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MODALIDAD_FIN",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModalidadContrato
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidadContratoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "0")
                        .toString();
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
        cargarTotalEstimado();

        validarEdicion();
        // </CODIGO_DESARROLLADO>
    }

    private void validarEdicion() {

        try {
            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            Integer.parseInt(vigencia), String.valueOf(0))) {
                bloqueaPlaneado = true;
            }
            else {
                bloqueaPlaneado = false;
            }

            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            Integer.parseInt(vigencia), String.valueOf(1))) {
                bloqueaEjecutado = true;
            }
            else {
                bloqueaEjecutado = false;
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarTotalEstimado() {
        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        params.put("TIPO", tipo);

        params.put(GeneralParameterEnum.NUMERO.getName(), numero);

        params.put("VIGENCIAGUB", vigenciaGubernamental);

        params.put(GeneralParameterEnum.ID_PLAN.getName(),
                        idPlan);

        params.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPlanAdquisicionesControladorUrlEnum.URL4133
                                                                            .getValue())
                                            .getUrl(), params));

            if (rs != null) {
                valorTotalEstimado = retornarConFormato(SysmanFunciones
                                .nvlDbl(rs.getCampos().get("TOTAL"), 0));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String retornarConFormato(double valor) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#,##0.00", dfs).format(valor);
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     * 
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

        constanteCodigo = 0;

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        constanteCodigo = generarConsecutivo();

        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        constanteCodigo);

        registro.getCampos().put("TIPO", tipo);

        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numero);

        registro.getCampos().put(
                        GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigenciaGubernamental);

        registro.getCampos().put(GeneralParameterEnum.ID_PLAN.getName(),
                        idPlan);
        registro.getCampos().put("ESNUEVO",
                        "-1");

        return true;
    }

    private long generarConsecutivo() {
        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "PI_PLAN_ADQUISICIONES",
                            "COMPANIA = ''" + compania + "''  AND TIPO= ''"
                                + tipo
                                + "'' AND NUMERO = " + numero
                                + " AND VIGENCIA_INICIAL="
                                + vigenciaGubernamental + " AND ID_PLAN= ''"
                                + idPlan + "''",
                            GeneralParameterEnum.CODIGO.getName(), "1");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * Se inserta un valor por defecto en la tabla
     * PI_PLAN_ADQUISICIONES
     * 
     */
    @Override
    public boolean insertarDespues() {

        Map<String, Object> campos = new HashMap<>();

        campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        campos.put(GeneralParameterEnum.CODIGO.getName(),
                        constanteCodigo);

        campos.put("TIPO", tipo);

        campos.put(GeneralParameterEnum.NUMERO.getName(), numero);

        campos.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigenciaGubernamental);

        campos.put(GeneralParameterEnum.ID_PLAN.getName(),
                        idPlan);

        campos.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

        campos.put(GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        campos.put(GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());

        Parameter parameter = new Parameter();
        parameter.setFields(campos);

        UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesControladorUrlEnum.URL5944
                                                        .getValue());

        try {
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarTotalEstimado();

        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {

        if (editando && !validarValorEstimado()) {

            editando = false;

            return false;

        }

        return true;
    }

    private boolean permiteEliminar(Object codigo) {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("TIPO", tipo);

        param.put(GeneralParameterEnum.NUMERO.getName(), numero);

        param.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigenciaGubernamental);

        param.put(GeneralParameterEnum.ID_PLAN.getName(), idPlan);

        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

        try {
            Registro reg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPlanAdquisicionesControladorUrlEnum.URL7272
                                                                            .getValue())
                                            .getUrl(),
                            param));

            if (reg != null) {

                JsfUtil.agregarMensajeAlerta(
                                "No se puede actualizar el indicador Eliminar porque existen detalles por vigencia para este registro.");
                return false;
            }
            JsfUtil.agregarMensajeAlerta(
                            "Se actualizó el idicador Eliminar.Este registro será eliminado una vez que se actualice la transacción.");
            return true;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    private boolean validarValorEstimado() {

        double totalFuentes;
        double valorEstimadoNuevo;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_PLAN.getName(), idPlan);
        param.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigencia);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
        param.put("TIPO", tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        param.put("ACCION", digitosAccion);

        try {
            Registro reg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPlanAdquisicionesControladorUrlEnum.URL6969
                                                                            .getValue())
                                            .getUrl(),
                            param));

            if (reg != null) {

                totalFuentes = SysmanFunciones
                                .nvlDbl(reg.getCampos().get("TOTAL_FUENTES"),
                                                0);

            }
            else {
                totalFuentes = 0;

            }

            valorEstimadoNuevo = SysmanFunciones.nvlDbl(
                            registro.getCampos().get("VALOR_ESTIMADO_FIN"),
                            0);

            if (Double.parseDouble(valorTotalEstimado.replace(",", ""))
                - valorEstimadoViejo
                + valorEstimadoNuevo > totalFuentes) {
                JsfUtil.agregarMensajeAlerta(
                                "Operación no permitida. El valor ingresado supera el valor estimado total para esta vigencia");
                // return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        cargarTotalEstimado();
        editando = false;
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
        cargarTotalEstimado();
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        valorEstimadoViejo = SysmanFunciones.nvlDbl(
                        registro.getCampos().get("VALOR_ESTIMADO_FIN"), 0);

        editando = true;

    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        param.put("predecesor", predecesor);

        param.put("digitosMetaProducto", digitosMetaProducto);

        param.put("esUsuarioConsulta", esUsuarioConsulta);

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigencia", vigenciaGubernamental);

        param.put("idPlan", idPlan);

        param.put("digitosAccion", digitosAccion);

        param.put("nombrePlan", nombrePlan);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.PLAN_DE_ACCION_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador,
                        SessionUtil.getModulo());
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public String getVigencia() {
        return vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    /**
     * Retorna la variable ValorTotalEstimado
     * 
     * @return ValorTotalEstimado
     */
    public String getValorTotalEstimado() {
        return valorTotalEstimado;
    }

    /**
     * Asigna la variable ValorTotalEstimado
     * 
     * @param ValorTotalEstimado
     * Variable a asignar en ValorTotalEstimado
     */
    public void setValorTotalEstimado(String valorTotalEstimado) {
        this.valorTotalEstimado = valorTotalEstimado;
    }

    /**
     * Retorna el objeto nombrePlanAdquisiciones
     * 
     * @return nombrePlanAdquisiciones
     */
    public String getNombrePlanAdquisiciones() {
        return nombrePlanAdquisiciones;
    }

    /**
     * Asigna el objeto nombrePlanAdquisiciones
     * 
     * @param nombrePlanAdquisiciones
     * Variable a asignar en nombrePlanAdquisiciones
     */
    public void setNombrePlanAdquisiciones(String nombrePlanAdquisiciones) {
        this.nombrePlanAdquisiciones = nombrePlanAdquisiciones;
    }

    /**
     * Retorna el objeto nomPlanAdquisiciones
     * 
     * @return nomPlanAdquisiciones
     */
    public String getNomPlanAdquisiciones() {
        return nomPlanAdquisiciones;
    }

    /**
     * Asigna el objeto nomPlanAdquisiciones
     * 
     * @param nomPlanAdquisiciones
     * Variable a asignar en nomPlanAdquisiciones
     */
    public void setNomPlanAdquisiciones(String nomPlanAdquisiciones) {
        this.nomPlanAdquisiciones = nomPlanAdquisiciones;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaModalidadContrato
     * 
     * @return listaModalidadContrato
     */
    public RegistroDataModelImpl getListaModalidadContrato() {
        return listaModalidadContrato;
    }

    /**
     * Asigna la lista listaModalidadContrato
     * 
     * @param listaModalidadContrato
     * Variable a asignar en listaModalidadContrato
     */
    public void setListaModalidadContrato(
        RegistroDataModelImpl listaModalidadContrato) {
        this.listaModalidadContrato = listaModalidadContrato;
    }

    /**
     * Retorna la lista listaModalidadContrato
     * 
     * @return listaModalidadContrato
     */
    public RegistroDataModelImpl getListaModalidadContratoE() {
        return listaModalidadContratoE;
    }

    /**
     * Asigna la lista listaModalidadContrato
     * 
     * @param listaModalidadContrato
     * Variable a asignar en listaModalidadContrato
     */
    public void setListaModalidadContratoE(
        RegistroDataModelImpl listaModalidadContratoE) {
        this.listaModalidadContratoE = listaModalidadContratoE;
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

    public boolean isBloqueaPlaneado() {
        return bloqueaPlaneado;
    }

    public void setBloqueaPlaneado(boolean bloqueaPlaneado) {
        this.bloqueaPlaneado = bloqueaPlaneado;
    }

    public boolean isBloqueaEjecutado() {
        return bloqueaEjecutado;
    }

    public void setBloqueaEjecutado(boolean bloqueaEjecutado) {
        this.bloqueaEjecutado = bloqueaEjecutado;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
