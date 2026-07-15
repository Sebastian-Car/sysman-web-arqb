/*-
 * FrmgruposControlador.java
 *
 * 1.0
 * 
 * 26/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmgruposControladorEnum;
import com.sysman.hojasdevida.enums.FrmgruposControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

/**
 * Clase para listar, insertar, modificar y eliminar datos de la tabla
 * EV_GRUPO.
 *
 * @version 1.0, 26/01/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class FrmgruposControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el código de la
     * compañía en la cual inició sesión el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesión
     * correspondiente.
     */
    private final String compania;

    /**
     * Variable de clase evaluación que lo toma de la sesión
     */
    private final String claseEvaluacion;

    private String titulo;

    /**
     * Implementación del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmgruposControlador
     */
    public FrmgruposControlador() {
        super();
        compania = SessionUtil.getCompania();
        claseEvaluacion = (String) SessionUtil.getSessionVar("claseEvaluacion");
        try {
            /** Formulario no 1644. */
            numFormulario = GeneralCodigoFormaEnum.FRM_GRUPO_CONTROLADOR
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
     * Este método se ejecuta justo después de que el objeto de la
     * clase del Bean ha sido creado. En este se realizan las
     * asignaciones iniciales necesarias para la visualización del
     * formulario, como lo son tablas, origenes de datos,
     * inicialización de listas y demás necesarios.
     */
    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.EV_GRUPO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este método se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. También carga la lista
     * del formulario por primera vez.
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(FrmgruposControladorEnum.COMPANIA.getValue(),
                        compania);
        parametrosListado.put(
                        FrmgruposControladorEnum.CLASEEVALUACION.getValue(),
                        claseEvaluacion);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Método ejecutado al oprimir el botón cmdDetalle
     * 
     * @param reg
     * registro en el cual esta ubicado el botón oprimido dentro de la
     * grilla
     * @param indice
     * índice en el cual esta ubicado el botón oprimido dentro de la
     * grilla
     */
    public void oprimircmdDetalle(Registro reg, int indice) {

        // <CODIGO_DESARROLLADO>

        String[] campos = { FrmgruposControladorEnum.GRUPO
                        .getValue() };
        Object[] valores = { reg.getCampos()
                        .get(FrmgruposControladorEnum.GRUPO.getValue())
                        .toString() };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_GRUPO_CRITERIO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * En este método es invocado el método inicializar, donde se
     * ejecutan las acciones a tener en cuenta en el momento de
     * apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarTitulo(claseEvaluacion);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método ejecutado cuando se cancela la edición del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Método ejecutado antes de realizar la inserción del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        determinarConsecutivo();
        registro.getCampos().put(FrmgruposControladorEnum.COMPANIA.getValue(),
                        compania);
        registro.getCampos().put(
                        FrmgruposControladorEnum.CLASE_EVALUACION.getValue(),
                        claseEvaluacion);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la inserción del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado antes de realizar la inserción y actualización
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la inserción y
     * actualización del registro
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado antes de realizar la eliminación del registro.
     * 
     * En este método se verifica que la tabla padre, EV_GRUPO, no
     * tenga registros en la tabla hijo, EV_CRITERIO_GRUPO. De ser así
     * no se elimina el registro y se envía un mesaje al usuario.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();

        param.put(FrmgruposControladorEnum.COMPANIA.getValue(), compania);

        param.put(FrmgruposControladorEnum.GRUPO.getValue(),
                        registro.getCampos().get(FrmgruposControladorEnum.GRUPO
                                        .getValue()));

        param.put(FrmgruposControladorEnum.CLASEEVALUACION.getValue(),
                        claseEvaluacion);

        Registro rs = null;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgruposControladorUrlEnum.URL239
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        String conteo = "0";

        if (rs != null) {
            conteo = SysmanFunciones.toString(rs.getCampos()
                            .get(FrmgruposControladorEnum.GRUPO
                                            .getValue()));
        }
        if (!"0".equals(conteo)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3950"));

            return false;
        }
        else {
            return true;
        }

        // </CODIGO_DESARROLLADO>

    }

    /**
     * Método ejecutado después de realizar la eliminación del
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
     * Este método se ejecuta antes enviar la acción de actualización,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // No hay código aquí.
    }

    /**
     * Método para obtener el consecutivo del código del registro
     * antes de la inserción.
     */
    public void determinarConsecutivo() {
        try {
            registro.getCampos().put(
                            FrmgruposControladorEnum.COMPANIA.getValue(),
                            compania);
            long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "EV_GRUPO",
                            "COMPANIA = ''" + compania + "''"
                                + " AND CLASE_EVALUACION =  ''"
                                + claseEvaluacion
                                + "'' ",
                            "GRUPO");
            registro.getCampos().put(FrmgruposControladorEnum.GRUPO.getValue(),
                            consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarTitulo(String claseEvaluacion) {

        Map<String, Object> param = new TreeMap<>();

        param.put(FrmgruposControladorEnum.CODIGO.getValue(),
                        claseEvaluacion);

        Registro rs = null;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgruposControladorUrlEnum.URL345
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        String nombre = "";

        if (rs != null) {
            nombre = SysmanFunciones.toString(rs.getCampos()
                            .get(FrmgruposControladorEnum.NOMBRE.getValue()));
        }
        if (!"0".equals(nombre)) {
            titulo = "GRUPO CRITERIOS DE EVALUACIÓN - " + nombre;
        }

    }

    /**
     * Este método es ejecutado después de finalizar la inserción y
     * edición del registro se usa cuando se desean agregar valores al
     * registro después de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // No hay código aquí.
    }
    // <SET_GET_ATRIBUTOS>

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
