/*-
 * NatsubquinqueniosControlador.java
 *
 * 1.0
 * 
 * 23/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.NatsubquinqueniosControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Migracion del formulario access Nat_SubQuinquenio a web con el
 * controlador NatsubquinqueniosControlador forma
 * natsubquinquenio.xhtml creacion de menu para abrir el formulario
 * modal, creacion de properties para el formulario continuo.
 *
 * @version 1.0, 19/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 * @version 2.0 21/06/2018, Se realiza PY.ASEGURAMIENTO E INTEGRACION
 * descrito en el tar 1000081152
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class NatsubquinqueniosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>

    /**
     * variable definida para almacenar el numero de documento del
     * empleado recibido por parametro
     */
    private String numeroDcto;
    /**
     * variable definida para almacenar la sucursal del empleado
     * recibido por parametro
     */
    private String sucursal;

    /**
     * variable definida para almacenar el codigo del empleado
     * recibido por parametro
     */
    private String codigo;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private Map<String, Object> parametrosEntrada;

    private Map<String, Object> ridDatosPersonales;

    /**
     * Crea una nueva instancia de NatsubquinqueniosControlador
     */
    public NatsubquinqueniosControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        try {

            numFormulario = GeneralCodigoFormaEnum.NAT_SUB_QUINQUENIO_CONTROLADOR
                            .getCodigo();

            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                numeroDcto = (String) parametrosEntrada.get("numeroDcto");
                sucursal = (String) parametrosEntrada.get("sucursal");
                codigo = parametrosEntrada.get("codigo").toString();
            }
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

        tabla = GenericUrlEnum.PERSONAL.getTable();
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigo);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubquinqueniosControladorUrlEnum.URL100
                                                        .getValue());

    }

    // <MES_CARGAR_LISTA>
    // </MES_CARGAR_LISTA>
    // <MES_BOTONES>
    // </MES_BOTONES>
    // <MES_CAMBIAR>
    // </MES_CAMBIAR>
    // <MES_COMBOS_GRANDES>
    // </MES_COMBOS_GRANDES>
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
     * seleccionado DOCUMENTACION ADICIONAL
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        numeroDcto);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        codigo);
        registro.getCampos().put("QQ_CODIGOPERSONA",
                        codigo);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
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
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1635-ANTES_ACTUALIZAR Private Sub
         * Form_BeforeUpdate(Cancel As Integer) AuditarRegistro
         * Me.Numero End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
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
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
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
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("numeroDcto", numeroDcto);
        parametros.put("sucursal", sucursal);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DP_NUMEDOCU.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        registro.getCampos().remove("QQ_CODIGOPERSONA");

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // Auto-generated method stub
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
