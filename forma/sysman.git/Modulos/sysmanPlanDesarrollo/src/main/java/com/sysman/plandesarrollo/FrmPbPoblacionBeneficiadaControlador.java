/*-
 * FrmPbPoblacionBeneficiadaControlador.java
 *
 * 1.0
 * 
 * 02/10/2019
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
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.FrmPbPoblacionBeneficiadaControladorEnum;
import com.sysman.plandesarrollo.enums.FrmPbPoblacionBeneficiadaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Se agrega en el style la propiedad: word-wrap: break-word; a las etiquetas
 * LB51305,LB51270,LB51269,LB51259 para ajustar el contenido de la etiqueta
 *
 * @version 1.0, 02/10/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmPbPoblacionBeneficiadaControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual
     * inicio sesion el usuario, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private final String compania;
    private boolean esAdministrador;
    private boolean esJefeUnidad;
    private String dependencia;
    private String predecesor;
    private int digitosMetaProducto;
    private boolean esUsuarioConsulta;
    private String vigenciaFinal;
    private String tipo;
    private String numero;
    private String vigenciaGubernamental;
    private String idPlan;
    private int digitosAccion;
    private String nombrePlan;
    private String tituloBoton;

    private Registro rsExtraerDatos;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmPbPoblacionBeneficiadaControlador
     */
    public FrmPbPoblacionBeneficiadaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_POBLACIONBENEFICIADAS_CONTROLADOR.getCodigo();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                esAdministrador = (boolean) parametrosEntrada.get("administrador");
                esJefeUnidad = (boolean) parametrosEntrada.get("jefeUnidad");
                dependencia = SysmanFunciones.nvl(parametrosEntrada.get("dependencia"), "").toString();
                predecesor = SysmanFunciones.nvl(parametrosEntrada.get("predecesor"), "").toString();
                digitosMetaProducto = (int) SysmanFunciones.nvl(parametrosEntrada.get("digitosMetaProducto"), 0);
                esUsuarioConsulta = (boolean) parametrosEntrada.get("esUsuarioConsulta");

                vigenciaFinal = SysmanFunciones.nvl(parametrosEntrada.get("vigenciaFinal"), "").toString();

                tipo = parametrosEntrada.get("tipo").toString();
                numero = parametrosEntrada.get("numero").toString();
                vigenciaGubernamental = parametrosEntrada.get("vigenciaGubernamental").toString();
                idPlan = parametrosEntrada.get("idPlan").toString();
                digitosAccion = Integer.parseInt(parametrosEntrada.get("digitosAccion").toString());
                nombrePlan = parametrosEntrada.get("nombrePlan").toString();

            }
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
     * sido creado, en este se realizan las asignaciones iniciales necesarias para
     * la visualizacion del formulario, como son tablas, origenes de datos,
     * inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PB_POBLACION_BENEFICIADA;
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la
     * consulta del formulario. Tambien carga la lista del formulario por primera
     * vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void oprimirProceso() {
        // <CODIGO_DESARROLLADO>
        updateRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cerrarFormulario en la vista
     *
     *
     */
    public void oprimircerrarFormulario() {
        // <CODIGO_DESARROLLADO>
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

        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.PLAN_DE_ACCION_CONTROLADOR.getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control PI
     * 
     * 
     * 
     */
    public void cambiarPI() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCicloV();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control I
     * 
     * 
     * 
     */
    public void cambiarI() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCicloV();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AD
     * 
     * 
     * 
     */
    public void cambiarAD() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCicloV();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control JUV
     * 
     * 
     * 
     */
    public void cambiarJUV() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCicloV();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ADU
     * 
     * 
     * 
     */
    public void cambiarADU() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCicloV();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ADM
     * 
     * 
     * 
     */
    public void cambiarADM() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCicloV();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control M
     * 
     * 
     * 
     */
    public void cambiarM() {
        // <CODIGO_DESARROLLADO>
        asignarTotalUsuario();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control H
     * 
     * 
     * 
     */
    public void cambiarH() {
        // <CODIGO_DESARROLLADO>
        asignarTotalUsuario();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTotalGenero() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control VCA
     * 
     * 
     */
    public void cambiarVCA() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCond();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control LGTB
     * 
     * 
     */
    public void cambiarLGTB() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCond();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DISCAP
     * 
     * 
     */
    public void cambiarDISCAP() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCond();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AFRO
     * 
     * 
     */
    public void cambiarAFRO() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCond();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control INDIG
     * 
     * 
     */
    public void cambiarINDIG() {
        // <CODIGO_DESARROLLADO>
        asignarTotalCond();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorTotalCond
     * 
     * 
     */
    public void cambiarValorTotalCond() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    public void asignarTotalCond() {

        int vca = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_VCA"), "0").toString());
        int lgtb = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_LGTB"), "0").toString());

        int discap = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_DISCAP"), "0").toString());

        int afro = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_AFRO"), "0").toString());

        int indig = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_INDIG"), "0").toString());

        int valorTotal = vca + lgtb + discap + afro + indig;

        registro.getCampos().put("VALOR_TOTAL_COND", valorTotal);

    }

    public void asignarTotalUsuario() {

        int mujeres = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_MUJERES"), "0").toString());
        int hombres = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_HOMBRES"), "0").toString());

        int valorTotal = mujeres + hombres;

        registro.getCampos().put("VALOR_TOTGENERO", valorTotal);

    }

    public void asignarTotalCicloV() {

        int pInfancia = Integer
                .parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_PRIMINFANCIA"), "0").toString());
        int infancia = Integer
                .parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_INFANCIA"), "0").toString());
        int adolescencia = Integer
                .parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_ADOLESCENCIA"), "0").toString());
        int juventud = Integer
                .parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_JUVENTUD"), "0").toString());
        int adulto = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_ADULTO"), "0").toString());
        int aMayor = Integer
                .parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_ADULTO_MAYOR"), "0").toString());

        int valorTotal = pInfancia + infancia + adolescencia + juventud + adulto + aMayor;

        registro.getCampos().put("VALOR_TOTAL", valorTotal);

    }

    private void updateRegistro() {

        try {

            Map<String, Object> paramLlaves = new HashMap<>();
            paramLlaves.put("KEY_COMPANIA", compania);
            paramLlaves.put("KEY_TIPO", tipo);
            paramLlaves.put("KEY_ID_PLAN", idPlan);
            paramLlaves.put("KEY_NUMERO", numero);
            paramLlaves.put("KEY_VIGENCIA_INICIAL", vigenciaGubernamental);

            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipo);
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numero);
            registro.getCampos().put(GeneralParameterEnum.ID_PLAN.getName(), idPlan);
            registro.getCampos().put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(), vigenciaGubernamental);

            /*
             * registro.getCampos().put( FrmPbPoblacionBeneficiadaControladorEnum.
             * VALOR_PRIMINFANCIA .getValue(), registro.getCampos().get(
             * FrmPbPoblacionBeneficiadaControladorEnum. VALOR_PRIMINFANCIA .getValue()));
             * registro.getCampos().put(
             * FrmPbPoblacionBeneficiadaControladorEnum.VALOR_INFANCIA .getValue(),
             * registro.getCampos().get(
             * FrmPbPoblacionBeneficiadaControladorEnum.VALOR_INFANCIA .getValue()));
             * registro.getCampos().put( FrmPbPoblacionBeneficiadaControladorEnum.
             * VALOR_ADOLESCENCIA .getValue(), registro.getCampos().get(
             * FrmPbPoblacionBeneficiadaControladorEnum. VALOR_ADOLESCENCIA .getValue()));
             * registro.getCampos().put(
             * FrmPbPoblacionBeneficiadaControladorEnum.VALOR_JUVENTUD .getValue(),
             * registro.getCampos().get(
             * FrmPbPoblacionBeneficiadaControladorEnum.VALOR_JUVENTUD .getValue()));
             * registro.getCampos().put(
             * FrmPbPoblacionBeneficiadaControladorEnum.VALOR_ADULTO .getValue(),
             * registro.getCampos().get(
             * FrmPbPoblacionBeneficiadaControladorEnum.VALOR_ADULTO .getValue()));
             * registro.getCampos().put( FrmPbPoblacionBeneficiadaControladorEnum.
             * VALOR_ADULTO_MAYOR .getValue(), registro.getCampos().get(
             * FrmPbPoblacionBeneficiadaControladorEnum. VALOR_ADULTO_MAYOR .getValue()));
             */

            if (rsExtraerDatos != null) {

                registro.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
                registro.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.PB_POBLACION_BENEFICIADA.getUpdateKey());

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), registro.getCampos(), paramLlaves);

                JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
            } else {

                registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
                registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.PB_POBLACION_BENEFICIADA.getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registro.getCampos());

                JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void taerDatosRegistro() {
        try {
            Map<String, Object> paramExtraer = new HashMap<>();
            paramExtraer.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramExtraer.put(GeneralParameterEnum.TIPO.getName(), tipo);
            paramExtraer.put(GeneralParameterEnum.ID_PLAN.getName(), idPlan);
            paramExtraer.put(GeneralParameterEnum.NUMERO.getName(), numero);
            paramExtraer.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(), vigenciaGubernamental);

            rsExtraerDatos = RegistroConverter
                    .toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            FrmPbPoblacionBeneficiadaControladorUrlEnum.URL0001.getValue())
                                    .getUrl(),
                            paramExtraer));

            if (rsExtraerDatos != null) {

                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_PRIMINFANCIA.getValue(),
                        Integer.parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_PRIMINFANCIA"), "0")
                                .toString()));

                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_ADOLESCENCIA.getValue(),
                        Integer.parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_ADOLESCENCIA"), "0")
                                .toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_JUVENTUD.getValue(),
                        Integer.parseInt(
                                SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_JUVENTUD"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_ADULTO.getValue(), Integer
                        .parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_ADULTO"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_ADULTO_MAYOR.getValue(),
                        Integer.parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_ADULTO_MAYOR"), "0")
                                .toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_TOTAL.getValue(), Integer
                        .parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_TOTAL"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_MUJERES.getValue(),
                        Integer.parseInt(
                                SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_MUJERES"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_HOMBRES.getValue(),
                        Integer.parseInt(
                                SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_HOMBRES"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_TOTGENERO.getValue(),
                        Integer.parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_TOTGENERO"), "0")
                                .toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_VCA.getValue(), Integer
                        .parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_VCA"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_LGTB.getValue(), Integer
                        .parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_LGTB"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_DISCAP.getValue(), Integer
                        .parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_DISCAP"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_AFRO.getValue(), Integer
                        .parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_AFRO"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_INDIG.getValue(), Integer
                        .parseInt(SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_INDIG"), "0").toString()));
                registro.getCampos().put(FrmPbPoblacionBeneficiadaControladorEnum.VALOR_INFANCIA.getValue(),
                        Integer.parseInt(
                                SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_INFANCIA"), "0").toString()));

                registro.getCampos().put("VALOR_TOTAL_COND", Integer.parseInt(
                        SysmanFunciones.nvl(rsExtraerDatos.getCampos().get("VALOR_TOTAL_COND"), "0").toString()));

                tituloBoton = "Actualizar";

            } else {

                tituloBoton = "Guardar";
            }
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        taerDatosRegistro();
        asignarTotalCicloV();
        asignarTotalUsuario();
        asignarTotalCond();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del
     * registro
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
     * Metodo ejecutado despues de realizar la eliminacion del registro
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
     * pueden remover valores auxiliares que no se desee o se deban enviar en el
     * registro
     */
    @Override
    public void removerCombos() {
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del
     * registro se usa cuando se desean agregar valores al registro despues de
     * dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // TODO Auto-generated method stub
    }
    // <SET_GET_ATRIBUTOS>

    /**
     * @return the tituloBoton
     */
    public String getTituloBoton() {
        return tituloBoton;
    }

    /**
     * @param tituloBoton the tituloBoton to set
     */
    public void setTituloBoton(String tituloBoton) {
        this.tituloBoton = tituloBoton;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
