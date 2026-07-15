/*-
 * FrmactualizaciondatostercerosautsControlador.java
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
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/02/2018
 * @author vmolano
 */
@ManagedBean
@ViewScoped
public class FrmactualizaciondatostercerosautsControlador
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
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadro_combinado778;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadro_combinado779;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadro_combinado781;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadro_combinado828;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadro_combinado829;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadro_combinado830;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listatxtNit;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de
     * FrmactualizaciondatostercerosautsControlador
     */
    public FrmactualizaciondatostercerosautsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1678;
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
        cargarListatxtNit();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListatxtCiudad();
        cargarListatxtPais();
        cargarListatxtDepartamento();
        cargarListaCuadro_combinado778();
        cargarListaCuadro_combinado779();
        cargarListaCuadro_combinado781();
        cargarListaCuadro_combinado828();
        cargarListaCuadro_combinado829();
        cargarListaCuadro_combinado830();
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

    /**
     * 
     * Carga la lista listaCuadro_combinado778
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadro_combinado778() {
        listaCuadro_combinado778 = service.getListado(conectorPool, "SELECT " +
            "     CIUDADES.CODIGO, " +
            "     CIUDADES.NOMBRE " +
            " FROM " +
            "     CIUDAD_HV  CIUDADES " +
            " ");
    }

    /**
     * 
     * Carga la lista listaCuadro_combinado779
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadro_combinado779() {
        listaCuadro_combinado779 = service.getListado(conectorPool, "SELECT " +
            "     PAISES_HV.PAIS, " +
            "     PAISES_HV.NOMBRE " +
            " FROM " +
            "     PAISES_HV " +
            " ");
    }

    /**
     * 
     * Carga la lista listaCuadro_combinado781
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadro_combinado781() {
        listaCuadro_combinado781 = service.getListado(conectorPool, "SELECT " +
            "     DEPARTAMENTO_HV.CODIGO, " +
            "     DEPARTAMENTO_HV.NOMBRE " +
            " FROM " +
            "     DEPARTAMENTO_HV " +
            " ");
    }

    /**
     * 
     * Carga la lista listaCuadro_combinado828
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadro_combinado828() {
        listaCuadro_combinado828 = service.getListado(conectorPool, "SELECT " +
            "     CIUDADES.CODIGO, " +
            "     CIUDADES.NOMBRE " +
            " FROM " +
            "     CIUDAD_HV  CIUDADES " +
            " ");
    }

    /**
     * 
     * Carga la lista listaCuadro_combinado829
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadro_combinado829() {
        listaCuadro_combinado829 = service.getListado(conectorPool, "SELECT " +
            "     PAISES_HV.PAIS, " +
            "     PAISES_HV.NOMBRE " +
            " FROM " +
            "     PAISES_HV " +
            " ");
    }

    /**
     * 
     * Carga la lista listaCuadro_combinado830
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadro_combinado830() {
        listaCuadro_combinado830 = service.getListado(conectorPool, "SELECT " +
            "     DEPARTAMENTO_HV.CODIGO, " +
            "     DEPARTAMENTO_HV.NOMBRE " +
            " FROM " +
            "     DEPARTAMENTO_HV " +
            " ");
    }

    /**
     * 
     * Carga la lista listatxtNit
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListatxtNit() {
        listatxtNit = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1678_nuevo:TBCB5612", "SELECT " +
                            "     TERCERO_HV.NIT, " +
                            "     TERCERO_HV.COMPANIA, " +
                            "     TERCERO_HV.SUCURSAL, " +
                            "     TERCERO_HV.NOMBRE, " +
                            "     TERCERO_HV.DIRECCION, " +
                            "     TERCERO_HV.CIUDAD, " +
                            "     TERCERO_HV.CONTACTO, " +
                            "     TERCERO_HV.TELEFONOS, " +
                            "     TERCERO_HV.FAX, " +
                            "     TERCERO_HV.CARGO_CONTACTO, " +
                            "     TERCERO_HV.DIRECCIONEMAIL, " +
                            "     TERCERO_HV.PAIS, " +
                            "     TERCERO_HV.DEPARTAMENTO " +
                            " FROM " +
                            "     TERCERO_HV " +
                            " WHERE " +
                            "     (" +
                            "         ((TERCERO_HV.NIT) = GETUSERNIT()) " +
                            "             AND " +
                            "         ((TERCERO_HV.COMPANIA) = '" + compania
                            + "') " +
                            "             AND " +
                            "         ((TERCERO_HV.SUCURSAL) = '001')" +
                            "     ) " +
                            " ",
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatxtNit
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatxtNit(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));
    }

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
         * FR1678-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Dim Estado As Boolean On Error Resume Next If
         * Forms!FRM_ACTDATOSPERSONAL_TERCEROS!TIPOS = "S" And
         * Forms!FRM_ACTDATOSPERSONAL_TERCEROS!ACTUALIZADO = 0 Then
         * Estado = False Me!Imprimir.Enabled = True ElseIf
         * Forms!FRM_ACTDATOSPERSONAL_TERCEROS!TIPOS = "A" Then Estado
         * = True ElseIf Forms!FRM_ACTDATOSPERSONAL_TERCEROS!TIPOS =
         * "T" And Not (Forms!Identificacion!N_Solicitud = 0) And
         * Forms!FRM_ACTDATOSPERSONAL_TERCEROS!ACTUALIZADO = 0 Then
         * Estado = False Me.Estado.Locked = Estado
         * Me!Imprimir.Enabled = True End If If
         * Forms!Identificacion!N_Solicitud = 0 Then 'NUEVO Estado =
         * False Forms!Identificacion!N_Solicitud = 0 Me.Undo
         * Me.Requery Me!Imprimir.Enabled = True Me!btn_Ruta.Enabled =
         * True End If If Not
         * Forms!FRM_ACTDATOSPERSONAL_TERCEROS!ACTUALIZADO = 0 Then
         * Estado = True Me.Estado.Locked = Estado End If
         * Me.txtDireccion.Locked = Estado Me.txtPais.Locked = Estado
         * Me.txtDepartamento.Locked = Estado Me.txtCiudad.Locked =
         * Estado Me.txtContacto.Locked = Estado
         * Me.txtTelefonos.Locked = Estado Me.txtFax.Locked = Estado
         * Me.txtCargoContacto.Locked = Estado End Sub
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
         * FR1678-ANTES_ACTUALIZAR Private Sub
         * Form_BeforeUpdate(Cancel As Integer) Dim Estado As Boolean
         * Dim db As DAO.Database Set db = CurrentDb Estado = True If
         * Me!Consecutivo = 0 Then Me!Consecutivo =
         * GenConsecutivo("aut_tercero", " COMPANIA = '" &
         * Getcompany() & "'  AND   NIT       = '" & GetUserNit() &
         * "' ", "CONSECUTIVO") db.Execute _ "   UPDATE aut_tercero  "
         * & _ "   SET DIRECCION_A = DIRECCION,  " & _
         * "       CIUDAD_A = CIUDAD, " & _
         * "       CONTACTO_A = CONTACTO, " & _
         * "       TELEFONOS_A = TELEFONOS, " & _
         * "       FAX_A = FAX, " & _
         * "       CARGO_CONTACTO = CARGO_CONTACTO_A " & _
         * "  WHERE COMPANIA = '" & Me!txtCompania & "' " & _
         * "    AND CONSECUTIVO = " & Me!Consecutivo & _
         * "    AND NIT = '" & Me!txtNit & "' " If Not Me!Enviado = 0
         * Then Me.txtDireccion.Locked = Estado Me.txtPais.Locked =
         * Estado Me.txtDepartamento.Locked = Estado
         * Me.txtCiudad.Locked = Estado Me.txtContacto.Locked = Estado
         * Me.txtTelefonos.Locked = Estado Me.txtFax.Locked = Estado
         * Me.txtCargoContacto.Locked = Estado End If End If End Sub
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

    /**
     * Retorna la lista listaCuadro_combinado778
     * 
     * @return listaCuadro_combinado778
     */
    public List<Registro> getListaCuadro_combinado778() {
        return listaCuadro_combinado778;
    }

    /**
     * Asigna la lista listaCuadro_combinado778
     * 
     * @param listaCuadro_combinado778
     * Variable a asignar en listaCuadro_combinado778
     */
    public void setListaCuadro_combinado778(
        List<Registro> listaCuadro_combinado778) {
        this.listaCuadro_combinado778 = listaCuadro_combinado778;
    }

    /**
     * Retorna la lista listaCuadro_combinado779
     * 
     * @return listaCuadro_combinado779
     */
    public List<Registro> getListaCuadro_combinado779() {
        return listaCuadro_combinado779;
    }

    /**
     * Asigna la lista listaCuadro_combinado779
     * 
     * @param listaCuadro_combinado779
     * Variable a asignar en listaCuadro_combinado779
     */
    public void setListaCuadro_combinado779(
        List<Registro> listaCuadro_combinado779) {
        this.listaCuadro_combinado779 = listaCuadro_combinado779;
    }

    /**
     * Retorna la lista listaCuadro_combinado781
     * 
     * @return listaCuadro_combinado781
     */
    public List<Registro> getListaCuadro_combinado781() {
        return listaCuadro_combinado781;
    }

    /**
     * Asigna la lista listaCuadro_combinado781
     * 
     * @param listaCuadro_combinado781
     * Variable a asignar en listaCuadro_combinado781
     */
    public void setListaCuadro_combinado781(
        List<Registro> listaCuadro_combinado781) {
        this.listaCuadro_combinado781 = listaCuadro_combinado781;
    }

    /**
     * Retorna la lista listaCuadro_combinado828
     * 
     * @return listaCuadro_combinado828
     */
    public List<Registro> getListaCuadro_combinado828() {
        return listaCuadro_combinado828;
    }

    /**
     * Asigna la lista listaCuadro_combinado828
     * 
     * @param listaCuadro_combinado828
     * Variable a asignar en listaCuadro_combinado828
     */
    public void setListaCuadro_combinado828(
        List<Registro> listaCuadro_combinado828) {
        this.listaCuadro_combinado828 = listaCuadro_combinado828;
    }

    /**
     * Retorna la lista listaCuadro_combinado829
     * 
     * @return listaCuadro_combinado829
     */
    public List<Registro> getListaCuadro_combinado829() {
        return listaCuadro_combinado829;
    }

    /**
     * Asigna la lista listaCuadro_combinado829
     * 
     * @param listaCuadro_combinado829
     * Variable a asignar en listaCuadro_combinado829
     */
    public void setListaCuadro_combinado829(
        List<Registro> listaCuadro_combinado829) {
        this.listaCuadro_combinado829 = listaCuadro_combinado829;
    }

    /**
     * Retorna la lista listaCuadro_combinado830
     * 
     * @return listaCuadro_combinado830
     */
    public List<Registro> getListaCuadro_combinado830() {
        return listaCuadro_combinado830;
    }

    /**
     * Asigna la lista listaCuadro_combinado830
     * 
     * @param listaCuadro_combinado830
     * Variable a asignar en listaCuadro_combinado830
     */
    public void setListaCuadro_combinado830(
        List<Registro> listaCuadro_combinado830) {
        this.listaCuadro_combinado830 = listaCuadro_combinado830;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listatxtNit
     * 
     * @return listatxtNit
     */
    public RegistroDataModel getListatxtNit() {
        return listatxtNit;
    }

    /**
     * Asigna la lista listatxtNit
     * 
     * @param listatxtNit
     * Variable a asignar en listatxtNit
     */
    public void setListatxtNit(RegistroDataModel listatxtNit) {
        this.listatxtNit = listatxtNit;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
