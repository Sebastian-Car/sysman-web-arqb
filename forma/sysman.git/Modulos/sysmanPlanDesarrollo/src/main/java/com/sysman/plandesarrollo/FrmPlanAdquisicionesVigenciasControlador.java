/*-
 * FrmPlanAdquisicionesVigenciasControlador.java
 *
 * 1.0
 * 
 * 17/04/2018
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloDosRemote;
import com.sysman.plandesarrollo.enums.FrmPlanAdquisicionesVigenciasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Formulario que administra los planes de adquisiciones del plan de
 * accion por vigencias
 *
 * @version 1.0, 17/04/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmPlanAdquisicionesVigenciasControlador
                extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

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
     * Atributo que almacena el id del plan del formulario anterior
     */

    private String idPlan;
    /**
     * Atributo que almacena la vigencia GetVIGENCIA_GUB()
     * 
     */

    private String vigenciaGubernamental;

    /**
     * Atributo que almacena el valor total estimado del formulario
     * anterior
     * 
     */
    private String valorTotalEstimado;
    /**
     * Atributo que almacena el codigo plan del formulario anterior
     */
    private String codigo;

    /**
     * Atributo que guarfa el total de valor estimado en pie pagina
     */
    private String totalEstimado;

    /**
     * Atributo que guarfa el total de valor ejecutado en pie pagina
     */

    private String totalEjecutado;

    /**
     * Atributo que almacena el valor estimado de registro antes de
     * actualzar
     */
    private double valorEstimadoViejo;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    /**
     * Atributo que administra el bloqueo de los campos de valor
     * estimado y vigencia
     */
    private boolean editaPlaneado;

    /**
     * Atributo que administra el bloqueo del campo de valor ejecutado
     */
    private boolean editaEjecutado;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga las vigencias
     */
    private List<Registro> listaVIGENCIA;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbPlanDesarrolloDosRemote ejbPlanDesarrolloDos;

    /**
     * Crea una nueva instancia de
     * FrmPlanAdquisicionesVigenciasControlador
     */
    public FrmPlanAdquisicionesVigenciasControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        totalEjecutado = "0";

        totalEstimado = "0";
        try
        {

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {

                tipo = (String) parametrosEntrada.get("tipo");
                numero = (String) parametrosEntrada.get("numero");
                idPlan = (String) parametrosEntrada.get("idPlan");
                vigenciaGubernamental = (String) parametrosEntrada
                                .get("vigenciaGub");
                codigo = (String) parametrosEntrada.get("codigo");

                valorTotalEstimado = (String) parametrosEntrada
                                .get("totalEstimado");

            }

            numFormulario = GeneralCodigoFormaEnum.FRM_PLAN_ADQUISICIONES_VIGENCIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
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
        tabla = "PI_PLAN_ADQUISICIONES_VIGENCIA";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaVIGENCIA();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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

        parametrosListado.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigenciaGubernamental);

        parametrosListado.put(GeneralParameterEnum.ID_PLAN.getName(),
                        idPlan);

        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigo);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesVigenciasControladorUrlEnum.URL8094
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesVigenciasControladorUrlEnum.URL8095
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesVigenciasControladorUrlEnum.URL8096
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPlanAdquisicionesVigenciasControladorUrlEnum.URL8097
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVIGENCIA
     *
     */
    public void cargarListaVIGENCIA() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaVIGENCIA = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPlanAdquisicionesVigenciasControladorUrlEnum.URL8819
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control VIGENCIA
     * 
     */
    public void cambiarVIGENCIA() {
        if (!validarVigencia())
        {

            registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),
                            "");

            return;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control VIGENCIA en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarVIGENCIAC(int rowNum) {

        try
        {

            editaPlaneado = !ejbPlanDesarrolloDos.verificarPlanAdquisiciones(
                            compania,
                            Integer.parseInt(listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get(GeneralParameterEnum.VIGENCIA
                                                            .getName())
                                            .toString()),
                            "0");

            editaEjecutado = !ejbPlanDesarrolloDos.verificarPlanAdquisiciones(
                            compania,
                            Integer.parseInt(listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get(GeneralParameterEnum.VIGENCIA
                                                            .getName())
                                            .toString()),
                            "1");

        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarVigencia() {

        int vigencia = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.VIGENCIA
                                                        .getName()),
                                        "0")
                        .toString());

        try
        {
            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            vigencia,
                            "0"))
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4200").replace("s$vigencia$s", String.valueOf(vigencia)));
                return false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        cargarTotales();
    }

    private void cargarTotales() {

        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        params.put("TIPO", tipo);

        params.put(GeneralParameterEnum.NUMERO.getName(), numero);

        params.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigenciaGubernamental);

        params.put(GeneralParameterEnum.ID_PLAN.getName(),
                        idPlan);

        params.put(GeneralParameterEnum.CODIGO.getName(),
                        codigo);

        Registro rs;

        try
        {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPlanAdquisicionesVigenciasControladorUrlEnum.URL5050
                                                                            .getValue())
                                            .getUrl(), params));

            if (rs != null)
            {
                totalEstimado = retornarConFormato(SysmanFunciones
                                .nvlDbl(rs.getCampos().get("ESTIMADO"), 0));

                totalEjecutado = retornarConFormato(SysmanFunciones
                                .nvlDbl(rs.getCampos().get("EJECUTADO"), 0));

            }

        }
        catch (SystemException e)
        {
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

        editaPlaneado = false;

        editaEjecutado = false;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        codigo);

        registro.getCampos().put("TIPO", tipo);

        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numero);

        registro.getCampos().put(
                        GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigenciaGubernamental);

        registro.getCampos().put(GeneralParameterEnum.ID_PLAN.getName(),
                        idPlan);
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        cargarTotales();
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
        if (!validarValorEstimado() || !validarVigencia())
        {

            editaPlaneado = false;

            editaEjecutado = false;

            return false;
        }
        return true;
    }

    private boolean validarValorEstimado() {
        double valorEstimadoNuevo;

        valorEstimadoNuevo = SysmanFunciones.nvlDbl(
                        registro.getCampos().get("VALOR_ESTIMADO_FIN"),
                        0);

        if (Double.parseDouble(totalEstimado.replace(",", ""))
            - valorEstimadoViejo + valorEstimadoNuevo > Double.parseDouble(
                            valorTotalEstimado.replace(",", "")))
        {

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4201"));

            return false;

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
        cargarTotales();
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

    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVIGENCIA
     * 
     * @return listaVIGENCIA
     */
    public List<Registro> getListaVIGENCIA() {
        return listaVIGENCIA;
    }

    /**
     * Asigna la lista listaVIGENCIA
     * 
     * @param listaVIGENCIA
     * Variable a asignar en listaVIGENCIA
     */
    public void setListaVIGENCIA(List<Registro> listaVIGENCIA) {
        this.listaVIGENCIA = listaVIGENCIA;
    }

    public String getValorTotalEstimado() {
        return valorTotalEstimado;
    }

    public void setValorTotalEstimado(String valorTotalEstimado) {
        this.valorTotalEstimado = valorTotalEstimado;
    }

    public String getTotalEstimado() {
        return totalEstimado;
    }

    public void setTotalEstimado(String totalEstimado) {
        this.totalEstimado = totalEstimado;
    }

    public String getTotalEjecutado() {
        return totalEjecutado;
    }

    public void setTotalEjecutado(String totalEjecutado) {
        this.totalEjecutado = totalEjecutado;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isEditaPlaneado() {
        return editaPlaneado;
    }

    public void setEditaPlaneado(boolean editaPlaneado) {
        this.editaPlaneado = editaPlaneado;
    }

    public boolean isEditaEjecutado() {
        return editaEjecutado;
    }

    public void setEditaEjecutado(boolean editaEjecutado) {
        this.editaEjecutado = editaEjecutado;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
