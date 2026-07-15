/*-
 * BasesNovedadesControlador.java
 *
 * 1.0
 *
 * 24/09/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.nomina.enums.BasesNovedadesControladorEnum;
import com.sysman.nomina.enums.BasesNovedadesControladorUrlEnum;
import com.sysman.nomina.enums.NovedadesControladorEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite visualizar y editar las novedades de las
 * bases de seguridad social calculadas.
 *
 * @version 1.0, 24/09/2018
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class BasesNovedadesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String idDeEmpleado;
    private String cedula;
    private String procesoNomina;
    private String anioNomina;
    private String mesNomina;
    private String periodoNomina;
    private String nombre;
    private String mensaje;
    private String nombreMes;
    private String nombrePeriodo;
    private String titulo;

    /**
     * Atributo para total Base, del formulario continuo.
     */
    private String totalBase;
    /**
     * Atributo para total Días, del formulario continuo.
     */
    private String totalDias;
    /**
     * Atributo para total Base proporcional, del formulario continuo.
     */
    private String totalBaseProporcional;
    /**
     * Atributo para total Pension Patronal, del formulario continuo.
     */
    private String totalPatronalPension;
    /**
     * Atributo para total Pensión del empleado, del formulario
     * continuo.
     */
    private String totalEmpleadoPension;
    /**
     * Atributo para total Fsp, del formulario continuo.
     */
    private String totalFsp;
    /**
     * Atributo para total Fsp Subsistencia, del formulario continuo.
     */
    private String totalFspSubsistencia;
    /**
     * Atributo para total Fsp Adicional, del formulario continuo.
     */
    private String totalFspAdicional;
    /**
     * Atributo para total Salud Patrono, del formulario continuo.
     */
    private String totalPatronalSalud;
    /**
     * Atributo para total Salud Empleado, del formulario continuo.
     */
    private String totalEmpleadoSalud;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaiddeempleado;

    private RegistroDataModelImpl listaiddeempleadoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    @EJB
    private EjbNominaSieteRemote ejbNominaSiete;
    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de BasesNovedadesControlador
     */
    public BasesNovedadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1932;
            validarPermisos();
            procesoNomina = validarSessionCadena(
                            SessionUtil.getSessionVar("procesoNomina"));
            anioNomina = validarSessionCadena(
                            SessionUtil.getSessionVar("anioNomina"));
            mesNomina = validarSessionCadena(
                            SessionUtil.getSessionVar("mesNomina"));
            periodoNomina = validarSessionCadena(
                            SessionUtil.getSessionVar("periodoNomina"));
            nombreMes = validarSessionCadena(
                            SessionUtil.getSessionVar("nombreMesNomina"));
            nombrePeriodo = validarSessionCadena(
                            SessionUtil.getSessionVar("nombrePeriodoNomina"));

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private String validarSessionCadena(Object objeto) {
        return SysmanFunciones.validarVariableVacio(objeto.toString()) ? ""
            : objeto.toString();
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

        enumBase = GenericUrlEnum.BASESNOVEDADES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaiddeempleado();
        cargarListaiddeempleadoE();
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
        parametrosListado.put(NovedadesControladorEnum.COMPANIA_AUX.getValue(),
                        compania);
        parametrosListado.put(NovedadesControladorEnum.ANO_AUX.getValue(),
                        anioNomina);
        parametrosListado.put(NovedadesControladorEnum.MES_AUX.getValue(),
                        mesNomina);
        parametrosListado.put(NovedadesControladorEnum.PERIODO_AUX.getValue(),
                        periodoNomina);
        parametrosListado.put(
                        NovedadesControladorEnum.ID_DE_EMPLEADO_AUX.getValue(),
                        idDeEmpleado);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaiddeempleado
     *
     */
    public void cargarListaiddeempleado() {
        try {
            Date fechaFin = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anioNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), false,
                            true);
            Date fechaIni = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anioNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), true,
                            true);
            if ((fechaIni == null) || (fechaFin == null)) {
                SessionUtil.setSessionVar("mensajeError",
                                idioma.getString("TB_TB2633"));
                SessionUtil.redireccionarMenu();
            }
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            BasesNovedadesControladorUrlEnum.URL17630
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(BasesNovedadesControladorEnum.FECHAFIN.getValue(),
                            fechaFin);
            param.put(BasesNovedadesControladorEnum.FECHAINI.getValue(),
                            fechaIni);
            listaiddeempleado = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, BasesNovedadesControladorEnum.ID_DE_EMPLEADO
                                            .getValue());

        }
        catch (SystemException | NumberFormatException ex) {

            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * Carga la lista listaiddeempleado
     *
     */
    public void cargarListaiddeempleadoE() {
        listaiddeempleadoE = listaiddeempleado;
    }

    /**
     * Metodo para cálcular los totales del formulario continuo.
     */
    public void asignarTotales() {
        Map<String, Object> param = new TreeMap<>();
        param.put(NovedadesControladorEnum.COMPANIA_AUX.getValue(), compania);
        param.put(NovedadesControladorEnum.ANO_AUX.getValue(), anioNomina);
        param.put(NovedadesControladorEnum.MES_AUX.getValue(), mesNomina);
        param.put(NovedadesControladorEnum.PERIODO_AUX.getValue(),
                        periodoNomina);
        param.put(NovedadesControladorEnum.ID_DE_EMPLEADO_AUX.getValue(),
                        idDeEmpleado);
        try {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BasesNovedadesControladorUrlEnum.URL17631
                                                                            .getValue())
                                            .getUrl(), param));
            totalBase = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.BASE
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";

            totalDias = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.DIAS
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";
            totalBaseProporcional = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.BASEPROPORCIONAL
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";
            totalPatronalPension = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.PATRONALPENSION
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";
            totalEmpleadoPension = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.EMPLEADOPENSION
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";
            totalFsp = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.FSP
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";
            totalFspSubsistencia = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.FSPSUBSISTENCIA
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";
            totalFspAdicional = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.FSPADICIONAL
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";
            totalPatronalSalud = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.PATRONALSALUD
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";
            totalEmpleadoSalud = regAux != null ? SysmanFunciones
                            .nvl(regAux.getCampos().get(
                                            BasesNovedadesControladorEnum.EMPLEADOSALUD
                                                            .getValue()),
                                            "0")
                            .toString()
                : "0";

        }
        catch (SystemException ex) {
            Logger.getLogger(BasesNovedadesControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Liquidar en la vista
     *
     *
     */
    public void oprimirCalculo() {
        try {
            if ("0".equals(idDeEmpleado)
                || SysmanFunciones.validarVariableVacio(idDeEmpleado)) {
                return;
            }
            archivoDescarga = null;
            String rutina = SysmanFunciones
                            .concatenar("FC_PROC",
                                            SysmanFunciones.strZero(
                                                            procesoNomina, 2))
                            .toUpperCase();

            mensaje = ejbNominaSiete.liquidarNomina(compania,
                            Integer.parseInt(procesoNomina), idDeEmpleado,
                            idDeEmpleado, rutina, Integer.parseInt(anioNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina),
                            SessionUtil.getUser().getCodigo());

            ejecutaractualizarMensaje();
            HashMap<String, Object> reemplazar = new HashMap<>();
            HashMap<String, Object> parametros = new HashMap<>();
            reemplazar.put(NovedadesControladorEnum.EMPLEADO.getValue()
                            .toLowerCase(), idDeEmpleado);
            Reporteador.resuelveConsulta(
                            NovedadesControladorEnum.REPORTE000070.getValue(),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            NovedadesControladorEnum.REPORTE000070.getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            NovedadesControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            e.getMessage()));
            Logger.getLogger(NovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, e);

        }
    }

    public void ejecutaractualizarMensaje() {
        if (!SysmanFunciones.validarVariableVacio(mensaje)) {
            JsfUtil.agregarMensajeInformativo(mensaje);
        }
        asignarTotales();
        reasignarOrigen();
        cargarForma();
    }

    private void armaTitulo() {
        String nMes;
        String nAno;
        String nPer;
        if ("0".equals(mesNomina) || mesNomina.isEmpty()) {
            nMes = idioma.getString("TB_TB3687");
        }
        else {
            nMes = idioma.getString("TB_TB3688").replace("s$nombreMes$s",
                            nombreMes);
        }
        if ("0".equals(anioNomina)) {
            nAno = idioma.getString("TB_TB3689");
        }
        else {
            nAno = SysmanFunciones.concatenar(" ", idioma.getString("TB_TB3695")
                            .replace("s$ano$s", anioNomina));
        }
        if ("0".equals(periodoNomina)) {
            nPer = idioma.getString("TB_TB3690");
        }
        else {
            nPer = SysmanFunciones.concatenar(" ",
                            idioma.getString("TB_TB3691").replace(
                                            "s$nombrePeriodo$s",
                                            nombrePeriodo));
        }
        titulo = idioma.getString("TB_TB3694")
                        .replace("s$mesAnoPer$s",
                                        SysmanFunciones.concatenar(nMes, nAno,
                                                        nPer))
                        .replace("para", "").replace("s$cedula$s", "");
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaiddeempleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaiddeempleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idDeEmpleado = registroAux.getCampos().get("ID_DE_EMPLEADO").toString();
        cedula = registroAux.getCampos().get(
                        BasesNovedadesControladorEnum.NUMERO_DCTO.getValue())
                        .toString();
        nombre = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();

        asignarTotales();
        reasignarOrigen();
        cargarForma();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaiddeempleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaiddeempleadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("ID_DE_EMPLEADO");
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        armaTitulo();
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     */
    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(
                        NovedadesControladorEnum.OBSERVACIONES.getValue());
        registro.getCampos()
                        .remove(NovedadesControladorEnum.PERIODO.getValue());
        registro.getLlave().put(NovedadesControladorEnum.KEY_PERIODO.getValue(),
                        periodoNomina);
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
        registro.getCampos().remove("LLAVENOVEDAD");
        registro.getCampos().remove("TIPONOVEDAD");
        registro.getCampos()
                        .remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove("MES");
        registro.getCampos().remove("DESCRIPCION");
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     *
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionarMenu();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable idDeEmpleado
     *
     * @return idDeEmpleado
     */
    public String getIdDeEmpleado() {
        return idDeEmpleado;
    }

    /**
     * Asigna la variable idDeEmpleado
     *
     * @param idDeEmpleado
     * Variable a asignar en idDeEmpleado
     */
    public void setIdDeEmpleado(String idDeEmpleado) {
        this.idDeEmpleado = idDeEmpleado;
    }

    /**
     * Retorna la variable cedula
     *
     * @return cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Asigna la variable cedula
     *
     * @param cedula
     * Variable a asignar en cedula
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * Retorna la variable nombre
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     *
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProcesoNomina() {
        return procesoNomina;
    }

    public void setProcesoNomina(String procesoNomina) {
        this.procesoNomina = procesoNomina;
    }

    public String getAnioNomina() {
        return anioNomina;
    }

    public void setAnioNomina(String anioNomina) {
        this.anioNomina = anioNomina;
    }

    public String getMesNomina() {
        return mesNomina;
    }

    public void setMesNomina(String mesNomina) {
        this.mesNomina = mesNomina;
    }

    public String getPeriodoNomina() {
        return periodoNomina;
    }

    public void setPeriodoNomina(String periodoNomina) {
        this.periodoNomina = periodoNomina;
    }

    public String getNombreMes() {
        return nombreMes;
    }

    public void setNombreMes(String nombreMes) {
        this.nombreMes = nombreMes;
    }

    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

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
    /**
     * Retorna la lista listaiddeempleado
     *
     * @return listaiddeempleado
     */
    public RegistroDataModelImpl getListaiddeempleado() {
        return listaiddeempleado;
    }

    /**
     * Asigna la lista listaiddeempleado
     *
     * @param listaiddeempleado
     * Variable a asignar en listaiddeempleado
     */
    public void setListaiddeempleado(RegistroDataModelImpl listaiddeempleado) {
        this.listaiddeempleado = listaiddeempleado;
    }

    /**
     * Retorna la lista listaiddeempleado
     *
     * @return listaiddeempleado
     */
    public RegistroDataModelImpl getListaiddeempleadoE() {
        return listaiddeempleadoE;
    }

    /**
     * Asigna la lista listaiddeempleado
     *
     * @param listaiddeempleado
     * Variable a asignar en listaiddeempleado
     */
    public void setListaiddeempleadoE(
        RegistroDataModelImpl listaiddeempleadoE) {
        this.listaiddeempleadoE = listaiddeempleadoE;
    }

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getTotalBase() {
        return totalBase;
    }

    public void setTotalBase(String totalBase) {
        this.totalBase = totalBase;
    }

    public String getTotalDias() {
        return totalDias;
    }

    public void setTotalDias(String totalDias) {
        this.totalDias = totalDias;
    }

    public String getTotalBaseProporcional() {
        return totalBaseProporcional;
    }

    public void setTotalBaseProporcional(String totalBaseProporcional) {
        this.totalBaseProporcional = totalBaseProporcional;
    }

    public String getTotalPatronalPension() {
        return totalPatronalPension;
    }

    public void setTotalPatronalPension(String totalPatronalPension) {
        this.totalPatronalPension = totalPatronalPension;
    }

    public String getTotalEmpleadoPension() {
        return totalEmpleadoPension;
    }

    public void setTotalEmpleadoPension(String totalEmpleadoPension) {
        this.totalEmpleadoPension = totalEmpleadoPension;
    }

    public String getTotalFsp() {
        return totalFsp;
    }

    public void setTotalFsp(String totalFsp) {
        this.totalFsp = totalFsp;
    }

    public String getTotalFspSubsistencia() {
        return totalFspSubsistencia;
    }

    public void setTotalFspSubsistencia(String totalFspSubsistencia) {
        this.totalFspSubsistencia = totalFspSubsistencia;
    }

    public String getTotalFspAdicional() {
        return totalFspAdicional;
    }

    public void setTotalFspAdicional(String totalFspAdicional) {
        this.totalFspAdicional = totalFspAdicional;
    }

    public String getTotalPatronalSalud() {
        return totalPatronalSalud;
    }

    public void setTotalPatronalSalud(String totalPatronalSalud) {
        this.totalPatronalSalud = totalPatronalSalud;
    }

    public String getTotalEmpleadoSalud() {
        return totalEmpleadoSalud;
    }

    public void setTotalEmpleadoSalud(String totalEmpleadoSalud) {
        this.totalEmpleadoSalud = totalEmpleadoSalud;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
