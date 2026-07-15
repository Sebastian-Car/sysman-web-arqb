/*-
 * FrmactualizaciondatostercerosControlador.java
 *
 * 1.0
 * 
 * 01/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/02/2018
 * @author vmolano
 */
@ManagedBean
@ViewScoped
public class FrmactualizaciondatostercerosControlador
                extends BeanBaseDatosAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listatxtCiudad;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listatxtPais;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listatxtDepartamento;

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
     * Crea una nueva instancia de
     * FrmactualizaciondatostercerosControlador
     */
    public FrmactualizaciondatostercerosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1677;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        cargarListatxtCiudad();
        cargarListatxtPais();
        cargarListatxtDepartamento();
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
        tabla = "AUT_TERCERO";
        buscarLlave();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "SELECT " +
            "     ROWID RID, " +
            "     AUT_TERCERO.COMPANIA, " +
            "     AUT_TERCERO.CONSECUTIVO, " +
            "     AUT_TERCERO.NIT, " +
            "     AUT_TERCERO.SUCURSAL, " +
            "     AUT_TERCERO.NIT_CEDULA, " +
            "     AUT_TERCERO.DIRECCION, " +
            "     AUT_TERCERO.PAIS, " +
            "     AUT_TERCERO.DEPARTAMENTO, " +
            "     AUT_TERCERO.CIUDAD, " +
            "     AUT_TERCERO.CONTACTO, " +
            "     AUT_TERCERO.TELEFONOS, " +
            "     AUT_TERCERO.FAX, " +
            "     AUT_TERCERO.CARGO_CONTACTO, " +
            "     AUT_TERCERO.BIENOFRECIDO, " +
            "     AUT_TERCERO.ANTECEDENTES, " +
            "     AUT_TERCERO.NOMBRE, " +
            "     AUT_TERCERO.CLASE, " +
            "     AUT_TERCERO.ZONA, " +
            "     AUT_TERCERO.ACTIVO, " +
            "     AUT_TERCERO.NIT_CEDULA_A, " +
            "     AUT_TERCERO.DIRECCION_A, " +
            "     AUT_TERCERO.PAIS_A, " +
            "     AUT_TERCERO.DEPARTAMENTO_A, " +
            "     AUT_TERCERO.CIUDAD_A, " +
            "     AUT_TERCERO.CONTACTO_A, " +
            "     AUT_TERCERO.TELEFONOS_A, " +
            "     AUT_TERCERO.FAX_A, " +
            "     AUT_TERCERO.CARGO_CONTACTO_A, " +
            "     AUT_TERCERO.BIENOFRECIDO_A, " +
            "     AUT_TERCERO.ANTECEDENTES_A, " +
            "     AUT_TERCERO.NOMBRE_A, " +
            "     AUT_TERCERO.CLASE_A, " +
            "     AUT_TERCERO.ZONA_A, " +
            "     AUT_TERCERO.ACTIVO_A, " +
            "     AUT_TERCERO.ESTADO, " +
            "     AUT_TERCERO.DESTINATARIO, " +
            "     AUT_TERCERO.SUCURSAL_DESTINATARIO, " +
            "     AUT_TERCERO.FECHA_APROBACION, " +
            "     AUT_TERCERO.ENVIADO, " +
            "     AUT_TERCERO.CREATED_BY, " +
            "     AUT_TERCERO.DATE_CREATED, " +
            "     AUT_TERCERO.MODIFIED_BY, " +
            "     AUT_TERCERO.DATE_MODIFIED, " +
            "     AUT_TERCERO.ACTUALIZADO, " +
            "     AUT_TERCERO.RUTA, " +
            "     AUT_TERCERO.OBSERVACION " +
            " FROM " +
            "     AUT_TERCERO " +
            " WHERE " +
            "     (" +
            "         ((AUT_TERCERO.COMPANIA) = '" + compania + "') " +
            "             AND " +
            "         ((AUT_TERCERO.CONSECUTIVO) = FORMS!IDENTIFICACION!N_SOLICITUD)"
            +
            "     ) ";
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listatxtCiudad
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListatxtCiudad() {
        listatxtCiudad = service.getListado(conectorPool, "SELECT " +
            "     CIUDADES.CODIGO, " +
            "     CIUDADES.NOMBRE " +
            " FROM " +
            "     CIUDAD_HV  CIUDADES " +
            " ");
    }

    /**
     * 
     * Carga la lista listatxtPais
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListatxtPais() {
        listatxtPais = service.getListado(conectorPool, "SELECT " +
            "     PAISES_HV.PAIS, " +
            "     PAISES_HV.NOMBRE " +
            " FROM " +
            "     PAISES_HV " +
            " ");
    }

    /**
     * 
     * Carga la lista listatxtDepartamento
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListatxtDepartamento() {
        listatxtDepartamento = service.getListado(conectorPool, "SELECT " +
            "     DEPARTAMENTO_HV.CODIGO, " +
            "     DEPARTAMENTO_HV.NOMBRE " +
            " FROM " +
            "     DEPARTAMENTO_HV " +
            " ");
    }

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
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btn_Ruta en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirbtn_Ruta() {
        // <CODIGO_DESARROLLADO>
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
        /*
         * FR1677-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Dim Estado As Boolean On Error Resume Next If
         * Forms!FRM_ACTDATOSPERSONAL_TERCEROS!TIPOS = "S" Then Estado
         * = False Me!Imprimir.Enabled = True If Not Me!Enviado = 0
         * Then Estado = True Me!Imprimir.Enabled = False
         * Me!btn_Ruta.Enabled = False End If ElseIf
         * Forms!FRM_ACTDATOSPERSONAL_TERCEROS!TIPOS = "A" Then Estado
         * = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled =
         * False ElseIf Forms!FRM_ACTDATOSPERSONAL_TERCEROS!TIPOS =
         * "T" And Not (Forms!Identificacion!N_Solicitud = 0) Then
         * Estado = False If Not Me!Enviado = 0 Then Me.Estado.Locked
         * = Estado Else Me.Estado.Locked = True End If Estado = True
         * Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled = False End
         * If If Forms!FRM_ACTDATOSPERSONAL_TERCEROS!ACTUALIZADO = 0
         * And Not (Forms!Identificacion!N_Solicitud = 0) Then Estado
         * = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled =
         * False If Me!Enviado = 0 Then Estado = False
         * Me!Imprimir.Enabled = True Me!btn_Ruta.Enabled = True End
         * If End If If Forms!Identificacion!N_Solicitud = 0 And
         * Forms!FRM_ACTDATOSPERSONAL_TERCEROS!ACTUALIZADO = 0 Then
         * 'NUEVO Estado = False Forms!Identificacion!N_Solicitud = 0
         * Me.Undo Me.Requery Me!Imprimir.Enabled = True
         * Me!btn_Ruta.Enabled = True End If Me.txtDireccion.Locked =
         * Estado Me.txtContacto.Locked = Estado
         * Me.txtTelefonos.Locked = Estado Me.txtFax.Locked = Estado
         * Me.txtCargoContacto.Locked = Estado
         * Me.txtObservaciones.Locked = Estado Me.txtPais.Locked =
         * Estado Me.txtDepartamento.Locked = Estado
         * Me.txtCiudad.Locked = Estado End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
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
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1677-ANTES_ACTUALIZAR Private Sub
         * Form_BeforeUpdate(Cancel As Integer) Dim Estado As Boolean
         * Dim db As DAO.Database Set db = CurrentDb Estado = True If
         * Me!Consecutivo = 0 Then Me!Consecutivo =
         * GenConsecutivo("aut_tercero", " COMPANIA = '" &
         * Getcompany() & "'  AND   NIT       = '" & GetUserNit() &
         * "' ", "CONSECUTIVO") db.Execute _ "   UPDATE aut_tercero  "
         * & _ "   SET DIRECCION_A = DIRECCION,  " & _
         * "       PAIS_A = PAIS, " & _
         * "       DEPARTAMENTO_A = DEPARTAMENTO, " & _
         * "       CIUDAD_A = CIUDAD, " & _
         * "       CONTACTO_A = CONTACTO, " & _
         * "       TELEFONOS_A = TELEFONOS, " & _
         * "       FAX_A = FAX, " & _
         * "       CARGO_CONTACTO = CARGO_CONTACTO_A " & _
         * "  WHERE COMPANIA = '" & Me!txtCompania & "' " & _
         * "    AND CONSECUTIVO = " & Me!Consecutivo & _
         * "    AND NIT = '" & Me!nit & "' " If Not Me!Enviado = 0
         * Then Me.txtDireccion.Locked = Estado Me.txtCiudad.Locked =
         * Estado Me.txtContacto.Locked = Estado
         * Me.txtTelefonos.Locked = Estado Me.txtFax.Locked = Estado
         * Me.txtCargoContacto.Locked = Estado
         * Me.txtObservaciones.Locked = Estado Me.txtPais.Locked =
         * Estado Me.txtDepartamento.Locked = Estado End If End If End
         * Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
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
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
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
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
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
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listatxtCiudad
     * 
     * @return listatxtCiudad
     */
    public List<Registro> getListatxtCiudad() {
        return listatxtCiudad;
    }

    /**
     * Asigna la lista listatxtCiudad
     * 
     * @param listatxtCiudad
     * Variable a asignar en listatxtCiudad
     */
    public void setListatxtCiudad(List<Registro> listatxtCiudad) {
        this.listatxtCiudad = listatxtCiudad;
    }

    /**
     * Retorna la lista listatxtPais
     * 
     * @return listatxtPais
     */
    public List<Registro> getListatxtPais() {
        return listatxtPais;
    }

    /**
     * Asigna la lista listatxtPais
     * 
     * @param listatxtPais
     * Variable a asignar en listatxtPais
     */
    public void setListatxtPais(List<Registro> listatxtPais) {
        this.listatxtPais = listatxtPais;
    }

    /**
     * Retorna la lista listatxtDepartamento
     * 
     * @return listatxtDepartamento
     */
    public List<Registro> getListatxtDepartamento() {
        return listatxtDepartamento;
    }

    /**
     * Asigna la lista listatxtDepartamento
     * 
     * @param listatxtDepartamento
     * Variable a asignar en listatxtDepartamento
     */
    public void setListatxtDepartamento(List<Registro> listatxtDepartamento) {
        this.listatxtDepartamento = listatxtDepartamento;
    }
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
