/*-
 * FrmPiPoblacionbeneficiada.java
 *
 * 1.0
 * 
 * 23/01/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroRemote;
import com.sysman.plandesarrollo.enums.FrmPbPoblacionBeneficiadaControladorEnum;
import com.sysman.plandesarrollo.enums.FrmPbPoblacionBeneficiadaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;
import com.sysman.exception.SystemException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/01/2020
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmPiPoblacionbeneficiada extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual
     * inicio sesion el usuario, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private final String compania;
    private boolean esAdministrador;
    private boolean esJefeUnidad;
    private String dependencia;
    private int digitosMetaProducto;
    private String vigenciaFinal;
    private String tipo;
    private String numero;
    private String vigenciaGubernamental;
    private String idPlan;
    private int digitosAccion;
    private String tituloBoton;

    private Registro rsExtraerDatos;

	@EJB
	private EjbPlanDesarrolloCeroRemote ejbPlanDesarrolloCeroRemote;
    /**
     * Crea una nueva instancia de FrmPiPoblacionbeneficiada
     */
    public FrmPiPoblacionbeneficiada() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // numFormulario = 2152;
            numFormulario = GeneralCodigoFormaEnum.FRM_PIPOBLACIONBENEFICIADA.getCodigo();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                esAdministrador = (boolean) parametrosEntrada.get("administrador");
                esJefeUnidad = (boolean) parametrosEntrada.get("jefeUnidad");
                dependencia = SysmanFunciones.nvl(parametrosEntrada.get("dependencia"), "").toString();
                // predecesor = SysmanFunciones.nvl(
                // parametrosEntrada.get("predecesor"),
                // "").toString();
                digitosMetaProducto = (int) SysmanFunciones.nvl(parametrosEntrada.get("digitosMetaProducto"), 0);
                // esUsuarioConsulta = (boolean) parametrosEntrada
                // .get("esUsuarioConsulta");

                vigenciaFinal = SysmanFunciones.nvl(parametrosEntrada.get("vigenciaFinal"), "").toString();

                tipo = parametrosEntrada.get("tipo").toString();
                numero = parametrosEntrada.get("numero").toString();
                vigenciaGubernamental = parametrosEntrada.get("vigenciaGubernamental").toString();
                idPlan = parametrosEntrada.get("idPlan").toString();
                digitosAccion = Integer.parseInt(parametrosEntrada.get("digitosAccion").toString());
                // nombrePlan = parametrosEntrada.get("nombrePlan").toString();

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
        try {
            enumBase = GenericUrlEnum.PB_POBLACION_BENEFICIADA;
            buscarLlave();
            registro = new Registro();
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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

    /**
     * 
     * Metodo ejecutado al oprimir el boton Proceso en la vista
     *
     * 
     *
     */
    public void oprimirProceso() {
        updateRegistro();
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cerrarFormulario en la vista
     *
     * 
     *
     */
    public void oprimircerrarFormulario() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        param.put("digitosMetaProducto", digitosMetaProducto);

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigencia", vigenciaGubernamental);

        param.put("idPlan", idPlan);

        param.put("digitosAccion", digitosAccion);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.PLAN_INDICATIVO_CONTROLADOR.getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, SessionUtil.getModulo());

    }

    /**
     * Metodo ejecutado al cambiar el control PI
     */
    public void cambiarPI() {
        asignarTotalCicloV();
    }

    /**
     * Metodo ejecutado al cambiar el control I
     */
    public void cambiarI() {
        asignarTotalCicloV();
    }

    /**
     * Metodo ejecutado al cambiar el control AD
     */
    public void cambiarAD() {
        asignarTotalCicloV();
    }

    /**
     * Metodo ejecutado al cambiar el control JUV
     */
    public void cambiarJUV() {
        asignarTotalCicloV();
    }

    /**
     * Metodo ejecutado al cambiar el control ADU
     */
    public void cambiarADU() {
        asignarTotalCicloV();
    }

    /**
     * Metodo ejecutado al cambiar el control ADM
     */
    public void cambiarADM() {
        asignarTotalCicloV();
    }

    /**
     * Metodo ejecutado al cambiar el control M
     */
    public void cambiarM() {
        asignarTotalUsuario();
    }

    /**
     * Metodo ejecutado al cambiar el control H
     */
    public void cambiarH() {
        asignarTotalUsuario();
    }

    /**
     * Metodo ejecutado al cambiar el control TotalGenero
     */
    public void cambiarTotalGenero() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control VCA
     */
    public void cambiarVCA() {
        asignarTotalCond();
    }

    public void cambiarLGTB() {
        asignarTotalCond();
    }

    /**
     * Metodo ejecutado al cambiar el control DISCAP
     */
    public void cambiarDISCAP() {
        asignarTotalCond();
    }

    /**
     * Metodo ejecutado al cambiar el control AFRO
     */
    public void cambiarAFRO() {
        asignarTotalCond();
    }

    /**
     * Metodo ejecutado al cambiar el control INDIG
     */
    public void cambiarINDIG() {
        asignarTotalCond();
    }

    /**
     * Metodo ejecutado al cambiar el control ValorTotalCond
     */
    public void cambiarValorTotalCond() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        traerDatosRegistro();
        asignarTotalCicloV();
        asignarTotalUsuario();
        asignarTotalCond();
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
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
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
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


    private void updateRegistro() 
    {
    	try 
    	{
    		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipo);
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numero);
            registro.getCampos().put(GeneralParameterEnum.ID_PLAN.getName(), idPlan);
            registro.getCampos().put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(), vigenciaGubernamental);

            if (rsExtraerDatos != null) 
            {
            	Map<String, Object> paramLlaves = new HashMap<>();
            	
                paramLlaves.put("KEY_COMPANIA", compania);
                paramLlaves.put("KEY_TIPO", tipo);
                paramLlaves.put("KEY_ID_PLAN", idPlan);
                paramLlaves.put("KEY_NUMERO", numero);
                paramLlaves.put("KEY_VIGENCIA_INICIAL", vigenciaGubernamental);
                
                registro.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
                registro.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.PB_POBLACION_BENEFICIADA.getUpdateKey());

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), registro.getCampos(), paramLlaves);

                JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
            } 
            else 
            {
                registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
                registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.PB_POBLACION_BENEFICIADA.getCreateKey());
                
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registro.getCampos());

                JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
        } 
    	catch (SystemException e) 
    	{
            logger.error(e.getMessage(), e);
        }
    }

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
        int aMayor = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("VALOR_ADULTO_MAYOR"), "0").toString());

        int valorTotal = pInfancia + infancia + adolescencia + juventud + adulto + aMayor;

        registro.getCampos().put("VALOR_TOTAL", valorTotal);

    }

    public void traerDatosRegistro() {
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

    public boolean isEsAdministrador() {
        return esAdministrador;
    }

    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }

    public boolean isEsJefeUnidad() {
        return esJefeUnidad;
    }

    public void setEsJefeUnidad(boolean esJefeUnidad) {
        this.esJefeUnidad = esJefeUnidad;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public int getDigitosMetaProducto() {
        return digitosMetaProducto;
    }

    public void setDigitosMetaProducto(int digitosMetaProducto) {
        this.digitosMetaProducto = digitosMetaProducto;
    }

    public String getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(String vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getVigenciaGubernamental() {
        return vigenciaGubernamental;
    }

    public void setVigenciaGubernamental(String vigenciaGubernamental) {
        this.vigenciaGubernamental = vigenciaGubernamental;
    }

    public String getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    public int getDigitosAccion() {
        return digitosAccion;
    }

    public void setDigitosAccion(int digitosAccion) {
        this.digitosAccion = digitosAccion;
    }

    public String getTituloBoton() {
        return tituloBoton;
    }

    public void setTituloBoton(String tituloBoton) {
        this.tituloBoton = tituloBoton;
    }

    public String getCompania() {
        return compania;
    }
}
