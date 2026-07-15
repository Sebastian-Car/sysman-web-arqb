/*-
 * FrmTarifasBaseControlador.java
 *
 * 1.0
 * 
 * 23 ago. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralTresRemote;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;

/**
 * Formulario que permite configurar las tarifas base
 *
 * @version 1.0, 23/08/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmTarifasBaseControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el anio que se
     * selcciona antes de ingresar al modulo
     */

    private final String anio;

    /**
     * Constante a nivel de clase que almacena el tipo de cobro que se
     * selcciona antes de ingresar al modulo
     */
    private final String tipoCobro;

    @EJB
    private EjbFacturacionGeneralTresRemote ejbFacturacionTres;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmTarifasBaseControlador
     */
    public FrmTarifasBaseControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue())
                        .toString();
        tipoCobro = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                        .toString();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_TARIFAS_BASE_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SF_TARIFAS_BASE;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
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
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

        parametrosListado.put("TIPOCOBRO", tipoCobro);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Formula
     * 
     */
    public void cambiarFormula() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put("VALOR", asignarValorFormula(
                        registro.getCampos().get("FORMULA").toString()));

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Formula en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFormulaC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("VALOR",
                        asignarValorFormula(listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get("FORMULA")
                                        .toString()));

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private Object asignarValorFormula(String formula) {

        double retorno = 0;

        double salarioDiario;

        try {

            salarioDiario = Double.parseDouble(
                            ejbFacturacionTres.extraerSalarioMinimo(compania,
                                            Integer.parseInt(anio)).toString())
                / 30;

            formula = formula.replace("SalarioDiario()",
                            Double.toString(salarioDiario));

            retorno = Double.parseDouble(ejbFacturacionTres
                            .reemplazarFormula(formula).toString());

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return retorno;
    }

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
     */
    @Override
    public boolean insertarAntes() {

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);

        registro.getCampos().put("TIPO_COBRO", tipoCobro);
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
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
