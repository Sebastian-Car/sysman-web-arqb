/*-
 * FrmEsFuente.java
 *
 * 1.0
 * 
 * 30/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Esta cale permite relacionar un riesgo con diversas fuentes, etapas
 * y tipos, LA FORMA SE LLAMA "opciones123.xhtml"
 *
 * @version 1.0, 30/07/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmEsFuente extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Variable que almacena la opcion desde la cual es llamado el
     * formulario
     */
    private String opcion;

    /**
     * Variable que almacena la opcion desde la cual es llamado el
     * formulario
     */
    private String opcionMenu;
    /**
     * Varible que cambiara el titulo del formulario
     */

    private String tituloFormulario;
    /**
     * Variable que almacena el nombre del campo "NOMBRE O ETAPA"
     */
    private String nombreCampo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Constante quer almacena la palabra TIPO_OPCION
     */
    private final String cTipoOpcion;
    /**
     * Constante que almacena la palacra COD_T_RIESGO
     */
    private final String cCodTipoRiesgo;
    /**
     * Constante que almacena la palbra COD_RIESGO
     */
    private final String cCodRiesgo;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmEsFuente
     */
    public FrmEsFuente() {
        super();
        compania = SessionUtil.getCompania();
        cTipoOpcion = "TIPO_OPCION";
        cCodTipoRiesgo = "COD_T_RIESGO";
        cCodRiesgo = "COD_RIESGO";

        opcionMenu = SessionUtil.getMenuActual();

        configurarFormuario();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ES_FUENTE_CONTROLADOR.getCodigo();
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
     * Este metodo configura el formulario Segun la opcion de menu
     * desde la cual se abra
     */
    public void configurarFormuario() {
        nombreCampo = "Nombre";
        if (opcionMenu.equals("19011502")) {
            tituloFormulario = "Fuentes";
            opcion = "1";
        }
        else if (opcionMenu.equals("19011503")) {
            tituloFormulario = "Etapas";
            nombreCampo = "Etapa";
            opcion = "2";
        }
        else {
            tituloFormulario = "Tipos";
            opcion = "3";
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

        enumBase = GenericUrlEnum.ES_FUENTE_ETAPA_TIPO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        abrirFormulario();
    }

    // <METODOS_CARGAR_LISTA>
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public String generarConsecutivo() {

        String consecutivoSalida = "0";
        try {
            long consecutivoGenerado = ejbSysmanUtil
                            .generarSiguienteConsecutivo(
                                            "ES_FUENTE_ETAPA_TIPO",
                                            SysmanFunciones.concatenar("COMPANIA = ''", compania, "''", " AND TIPO_OPCION= ", opcion),
                                            GeneralParameterEnum.CODIGO.getName());

            consecutivoSalida = String.valueOf(consecutivoGenerado);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivoSalida;
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    @Override
    public void asignarValoresRegistro() {
        // Auto-generated method stub

    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(cTipoOpcion);
        registro.getCampos().remove(cCodTipoRiesgo);
        registro.getCampos().remove(cCodRiesgo);

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // Auto-generated method stub

    }

    @Override
    public void reasignarOrigen() {
        // Auto-generated method stub

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("TIPO_OPERACION", opcion);
    }

    @Override
    public boolean insertarAntes() {
        // Auto-generated method stub

        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), generarConsecutivo());
        registro.getCampos().put(cTipoOpcion, opcion);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // Auto-generated method stub
        return true;
    }

    @Override
    public boolean actualizarAntes() {

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // Auto-generated method stub
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // Auto-generated method stub
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // Auto-generated method stub
        return true;
    }

    public String getTituloFormulario() {
        return tituloFormulario;
    }

    public void setTituloFormulario(String tituloFormulario) {
        this.tituloFormulario = tituloFormulario;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }

    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }
}
