/*-
 * RepresentantereunioncomisionsControlador.java
 *
 * 1.0
 * 
 * 03/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.NatsubpensionsControladorUrlEnum;
import com.sysman.hojasdevida.enums.RepresentantereunioncomisionControladorEnum;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Este controlador se encarga de gestionar el formulario COMITE
 * COMISION DE PERSONAL
 *
 * @version 1.0, 03/02/2018
 * @author mvenegas
 * 
 * Se agrego el botón anexos que redirecciona al formulario
 * FrmDetalleDocumentoBienesyCapControlador
 * 
 * @version 2.0 21/06/2018
 * @version lbotia
 * 
 * 
 */
@ManagedBean
@ViewScoped
public class RepresentantereunioncomisionsControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Esta variable me captura el valor en mayuscula que debe tomar
     * el formulario basado en la opcion de menu por la que se ingrese
     */
    private String tituloMayuscula;
    /**
     * Esta variable me captura el valor en minuscula que debe tomar
     * el formulario basado en la opcion de menu por la que se ingrese
     */
    private String tituloMinuscula;
    /**
     * Esta variable me almacena el tipo de comite que se seleccione
     * basado en la opcion de menu por la que se ingrese
     */
    private String tipoComiteSeleccionado;
    /**
     * Esta variable me toma el modulo actual en el que se esta
     * trabajando, queen este caso es hojas de vida
     */
    private String moduloHojasDeVida;

    private final String menuActual = SessionUtil.getMenuActual();

    private String ctipoComite = RepresentantereunioncomisionControladorEnum.TIPOCOMITE
                    .getValue();

    private String cNumeroComite = RepresentantereunioncomisionControladorEnum.NUMERO_COMITE
                    .getValue();

    /**
     * Atributo que contiene la variable verResponsable con los campos
     * que debe mostrar de la actividad programada.
     */

    private boolean verAnexos;

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
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * RepresentantereunioncomisionsControlador
     */
    public RepresentantereunioncomisionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloHojasDeVida = SessionUtil.getModulo();
        tituloMinuscula = "";
        tituloMayuscula = "";

        validarMen();

        try {
            numFormulario = GeneralCodigoFormaEnum.REPRESENTANTEREUNIONCOMISION_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            mostrarTitulos();

            // <INI_ADICIONAL>
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

        enumBase = GenericUrlEnum.NAT_REUNION_COMISIONPERSONAL;
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

        parametrosListado.put("TIPO_COMITE",
                        tipoComiteSeleccionado);

    }

    public void mostrarTitulos() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        tipoComiteSeleccionado);
        try {
            Registro datosNatComite = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatsubpensionsControladorUrlEnum.URL105
                                                                            .getValue())
                                            .getUrl(), param));

            if (datosNatComite != null) {
                tituloMinuscula = SysmanFunciones.nvl(
                                datosNatComite.getCampos().get("NOMBRE_MINUS"),
                                "")
                                .toString();

                tituloMayuscula = SysmanFunciones.nvl(
                                datosNatComite.getCampos().get("NOMBRE_MAYUS"),
                                "")
                                .toString();
            }
            else {
                SessionUtil.redireccionarMenuMensaje(SysmanConstantes.MSJ_FATAL,
                                idioma.getString("TB_TB4058"),
                                false);
            }

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
        }

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
     * Metodo ejecutado al oprimir el boton botonIntegrantes en la
     * vista
     *
     *
     * 
     */
    public void oprimirbotonIntegrantes() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        if (!SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .toString(registro.getCampos()
                                        .get(RepresentantereunioncomisionControladorEnum.NUMERO_COMITE
                                                        .getValue())))) {

            String[] campos = { RepresentantereunioncomisionControladorEnum.NUMERO_COMITE
                            .getValue(),
                                RepresentantereunioncomisionControladorEnum.TIPOCOMITE
                                                .getValue() };

            Object[] valores = { registro.getCampos()
                            .get(RepresentantereunioncomisionControladorEnum.NUMERO_COMITE
                                            .getValue()),
                                 tipoComiteSeleccionado };

            SessionUtil.cargarModalDatosFlashCerrar(
                            String.valueOf(GeneralCodigoFormaEnum.ADREUNIONCOMITEPERSONAL_CONTROLADOR
                                            .getCodigo()),
                            moduloHojasDeVida, campos, valores);

        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimiranexos() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String[] campos = { "tipoComprobante", "numeroComite" };

        Object[] valores = {
                             registro.getCampos().get(ctipoComite).toString(),
                             registro.getCampos().get(cNumeroComite)
                                             .toString() };

        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_DETALLE_DOCUMENTO_BIENESTAR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos, valores);

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
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos()
                            .put(RepresentantereunioncomisionControladorEnum.TIPOCOMITE
                                            .getValue(),
                                            tipoComiteSeleccionado);
        }
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * 
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * 
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
     * 
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

    private void validarMen() {
        switch (menuActual) {
        case "2104020501":
            tipoComiteSeleccionado = "1";
            verAnexos = true;
            break;

        case "2104020502":
            tipoComiteSeleccionado = "2";

            break;
        default:// 2104020503
            tipoComiteSeleccionado = "3";

        }

    }

    /**
     * if ("2104020501".equals(SessionUtil.getMenuActual())) {
     * tipoComiteSeleccionado = "1";
     * 
     * } else if ("2104020502".equals(SessionUtil.getMenuActual())) {
     * tipoComiteSeleccionado = "2"; } else if
     * ("2104020503".equals(SessionUtil.getMenuActual())) {
     * tipoComiteSeleccionado = "3"; }
     * 
     */

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

    public String getTituloMayuscula() {
        return tituloMayuscula;
    }

    public void setTituloMayuscula(String tituloMayuscula) {
        this.tituloMayuscula = tituloMayuscula;
    }

    public String getModuloHojasDeVida() {
        return moduloHojasDeVida;
    }

    public void setModuloHojasDeVida(String moduloHojasDeVida) {
        this.moduloHojasDeVida = moduloHojasDeVida;
    }

    public String getTituloMinuscula() {
        return tituloMinuscula;
    }

    public void setTituloMinuscula(String tituloMinuscula) {
        this.tituloMinuscula = tituloMinuscula;
    }

    /**
     * @return the verAnexos
     */
    public boolean isVerAnexos() {
        return verAnexos;
    }

    /**
     * @param verAnexos
     * the verAnexos to set
     */
    public void setVerAnexos(boolean verAnexos) {
        this.verAnexos = verAnexos;
    }

}
