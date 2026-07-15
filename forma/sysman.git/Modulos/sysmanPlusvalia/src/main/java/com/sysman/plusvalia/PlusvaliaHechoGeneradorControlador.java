/*-
 * PlusvaliaHechoGeneradorControlador.java
 *
 * 1.0
 * 
 * 12/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @version 1.0, 12/03/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PlusvaliaHechoGeneradorControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;

    private Map<String, Object> parametrosEntrada;
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private BigInteger idProyecto;
    private String codigoProyecto;
    private String claseProyecto;
    private String nombreClase;
    private String claseVP;
    private Map<String, Object> ridProyecto;
    private boolean divideAcuerdo;
    private String hecho;
    private boolean hechoVisible;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PlusvaliaHechoGeneradorControlador
     */
    public PlusvaliaHechoGeneradorControlador() {
        super();
        compania = SessionUtil.getCompania();

        claseVP = "44";
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            idProyecto = (BigInteger) parametrosEntrada
                            .get("idProyecto");

            codigoProyecto = (String) parametrosEntrada
                            .get("codigoProyecto");
            claseProyecto = (String) parametrosEntrada.get("claseProyecto");
            ridProyecto = (Map<String, Object>) parametrosEntrada.get("rid");
        }
        try {
            // 2042
            numFormulario = GeneralCodigoFormaEnum.PLUSVALIA_HECHO_GENERADOR_CONTROLADOR
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
     * Retorna la variable varVolver
     * 
     * @return var
     */
    public boolean isVarVolver() {
        return varVolver;
    }

    /**
     * Asigna la variable varVolver
     * 
     * @param var
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
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

        enumBase = GenericUrlEnum.VP_HECHOS_PROYECTOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.PROYECTO.getName(),
                        idProyecto);

        parametrosListado.put(GeneralParameterEnum.CLASE.getName(),
                        claseVP);

        parametrosListado.put("PAGTAMANIO", 5);

    }

    /**
     * Metodo ejecutado desde un comando remoto en el boton volver del
     * formulario
     * 
     */
    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>

        if (varVolver) {
            accion = null;
            varVolver = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Tratamiento en la vista
     *
     *
     */
    public void oprimirTratamiento() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        String[] campos = { "idProyecto",
                            "codigoProyecto",
                            "claseProyecto",
                            "idHechos",
                            "codigoHecho",
                            "clase",
                            "rid" };
        Object[] valores = { idProyecto,
                             codigoProyecto,
                             claseProyecto,
                             registro.getCampos().get("ID"),
                             registro.getCampos().get("CODIGO"),
                             "TRATAMIENTO",
                             css };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.PLUSVALIA_DOMINIOS_HECHO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Actividad en la vista
     *
     *
     */
    public void oprimirActividad() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        String[] campos = { "idHechos",
                            "codigoHecho",
                            "clase",
                            "rid" };
        Object[] valores = { registro.getCampos().get("ID"),
                             registro.getCampos().get("CODIGO"),
                             "ACTIVIDAD",
                             css };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.PLUSVALIA_DOMINIOS_HECHO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton UsoSuelo en la vista
     *
     *
     */
    public void oprimirUsoSuelo() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        String[] campos = { "idHechos",
                            "codigoHecho",
                            "clase",
                            "rid" };
        Object[] valores = { registro.getCampos().get("ID"),
                             registro.getCampos().get("CODIGO"),
                             "USO",
                             css };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.PLUSVALIA_DOMINIOS_HECHO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Norma en la vista
     *
     *
     */
    public void oprimirNorma() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        String[] campos = { "idHechos",
                            "codigoHecho",
                            "clase",
                            "rid" };
        Object[] valores = { registro.getCampos().get("ID"),
                             registro.getCampos().get("CODIGO"),
                             "USO",
                             css };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.PLUSVALIA_NORMA_URBANISTICA_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
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

//        hecho = SysmanFunciones.nvl(registro.getCampos().get("NOMBRE"), "")
//                        .toString();
//
//        divideAcuerdo = hecho.contains("EDIFICABILIDAD")
//            || hecho.contains("edificabilidad");
//
//        String divide = SysmanFunciones.nvl(
//                        registro.getCampos().get("DIVIDE_ACUERDO"),
//                        "")
//                        .toString();
//        hechoVisible = divide.equals("true");
        
        hechoVisible = divideAcuerdo = (Boolean) SysmanFunciones
                .nvl(registro.getCampos().get("DIVIDE_ACUERDO"), false);
        

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ID_PROYECTO", idProyecto);
        registro.getCampos().put("CODIGO_PROYECTO", codigoProyecto);
        registro.getCampos().put("CLASE", claseVP);
        cargarRegistro();
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("ID");
        registro.getCampos().remove("PRODESC");
        cargarRegistro();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
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
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("idProyecto", idProyecto);
        parametros.put("codigoProyecto", codigoProyecto);
        parametros.put("claseProyecto", claseProyecto);
        parametros.put("rid", ridProyecto);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.PLUSVALIA_ACUERDO_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * @return the divideAcuerdo
     */
    public boolean isDivideAcuerdo() {
        return divideAcuerdo;
    }

    /**
     * @param divideAcuerdo
     * the divideAcuerdo to set
     */
    public void setDivideAcuerdo(boolean divideAcuerdo) {
        this.divideAcuerdo = divideAcuerdo;
    }

    /**
     * @return the hechoVisible
     */
    public boolean isHechoVisible() {
        return hechoVisible;
    }

    /**
     * @param hechoVisible
     * the hechoVisible to set
     */
    public void setHechoVisible(boolean hechoVisible) {
        this.hechoVisible = hechoVisible;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
