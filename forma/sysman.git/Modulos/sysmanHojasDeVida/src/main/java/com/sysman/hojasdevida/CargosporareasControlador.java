/*-
 * CargosporareasControlador.java
 *
 * 1.0
 * 
 * 02/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 02/03/2017
 * @author spina
 */
@ManagedBean
@ViewScoped
public class CargosporareasControlador extends BeanBaseContinuoAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * navegacion entre los formularios
     */
    private Map<String, Object> rid;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModel listaCACODIGOAREA;
    /**
     */
    private RegistroDataModel listaCACODIGOAREAE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * denominacion seleccionada en el combo
     */
    private String denominacion;
    /**
     * codigo obtenido por parametro
     */
    private String codigo;
    /**
     * grado obtenido por parametro
     */
    private String grado;

    private Date fechacreacion;
    private int anio;
    /**
     * hasmap con los parametros obtenidos del formulario anterior
     */
    private Map<String, Object> parametrosEntrada;
    /**
     * valor del modulo actual
     */
    private String modulo;
    /**
     * variable que genera el archivo con el reporte
     */
    private StreamedContent archivoDescarga;
    /**
     * habilita o deshabilita la edicion de los registros
     */
    private String accion;

    /**
     * constante
     */
    private static final String CODAREA = "CODAREA";

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CargosporareasControlador
     */
    @SuppressWarnings("unchecked")
    public CargosporareasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = 1336;
            validarPermisos();
            // <INI_ADICIONAL>

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                denominacion = (String) parametrosEntrada.get("DENOMINACION");
                codigo = (String) parametrosEntrada.get("CODIGO");
                grado = (String) parametrosEntrada.get("GRADO");
                fechacreacion = (Date) parametrosEntrada.get("FECHACREACION");
                anio = SysmanFunciones.getParteFecha(
                                (Date) parametrosEntrada
                                                .get("FECHACREACION"),
                                1);
                accion = (String) parametrosEntrada.get("ACCION");

            }
            else {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
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
        try {
            tabla = "NAT_CARGOS_POR_AREAS";

            registro = new Registro();

            reasignarOrigen();
            buscarLlave();
            conectorPool.conectar(nombreConexion);
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListaCACODIGOAREA();
            cargarListaCACODIGOAREAE();
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
        }
        catch (NamingException | SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        origenDatos = "SELECT" +
            " NAT_CARGOS_POR_AREAS.COMPANIA," +
            " NAT_CARGOS_POR_AREAS.CA_CODIGOAREA," +
            " NAT_CARGOS_POR_AREAS.CA_RESPONSABILIDADES," +
            " NAT_CARGOS_POR_AREAS.CA_ESTUDIOS," +
            " NAT_CARGOS_POR_AREAS.CA_EXPERIENCIA," +
            " NAT_CARGOS_POR_AREAS.SUPERIOR_JERARQUICO," +
            " NAT_CARGOS_POR_AREAS.CA_FORMACION," +
            " NAT_CARGOS_POR_AREAS.CA_HABILIDADES," +
            " NAT_CARGOS_POR_AREAS.CA_IDCARGO," +
            " NAT_CARGOS_POR_AREAS.CA_ANO," +
            " NAT_CARGOS_POR_AREAS.CA_FECHA_CREACION" +
            "  FROM " +
            " NAT_CARGOS_POR_AREAS "
            + " WHERE NAT_CARGOS_POR_AREAS.CA_IDCARGO = '" + codigo + "' "
            + " ORDER BY NAT_CARGOS_POR_AREAS.CA_CODIGOAREA"
            + "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenDatos);
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCACODIGOAREA
     *
     */
    public void cargarListaCACODIGOAREA() {
        listaCACODIGOAREA = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1336:TBCB4347", "SELECT DISTINCT " +
                            "     NAT_AREAS_DESEMPENO.CODAREA, " +
                            "     NAT_AREAS_DESEMPENO.NOMBRE_AREA" +
                            " FROM " +
                            "     NAT_AREAS_DESEMPENO",
                        true, CODAREA);
    }

    /**
     * 
     * Carga la lista listaCACODIGOAREA
     *
     */
    public void cargarListaCACODIGOAREAE() {
        listaCACODIGOAREAE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1336:TBCB4347", "SELECT DISTINCT " +
                            "     NAT_AREAS_DESEMPENO.CODAREA, " +
                            "     NAT_AREAS_DESEMPENO.NOMBRE_AREA" +
                            " FROM " +
                            "     NAT_AREAS_DESEMPENO ",
                        true, CODAREA);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton CmdPreview
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirCmdPreview(Registro reg, int indice) { // indice
                                                              // necesario
                                                              // por
                                                              // la
                                                              // vista
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        try {
            conectorPool.conectar(ConectorPool.ESQUEMA_SYSMAN);

            reemplazar.put("compania",
                            "'" + compania + "'");
            reemplazar.put("codigoarea",
                            "'" + reg.getCampos().get("CA_CODIGOAREA")
                                + "'");
            reemplazar.put("idcargo",
                            "'" + reg.getCampos().get("CA_IDCARGO") + "'");

            reemplazar.put("codigo",
                            "'" + codigo + "'");
            reemplazar.put("grado",
                            "'" + grado + "'");

            Reporteador.resuelveConsulta("001436ManualDeFuncionesPORAREAS",
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001436ManualDeFuncionesPORAREAS", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (NamingException | SQLException | OutOfMemoryError | JRException
                        | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar() {
        String ruta = "/hvcargos.sysman";
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCACODIGOAREA
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCACODIGOAREA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CA_CODIGOAREA",
                        registroAux.getCampos().get(CODAREA));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCACODIGOAREA
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCACODIGOAREAE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(CODAREA);
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
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
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("CA_IDCARGO", codigo);
        registro.getCampos().put("GRADO", grado);
        registro.getCampos().put("CA_FECHA_CREACION", fechacreacion);
        registro.getCampos().put("CA_ANO", anio);
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // Metodo heredado
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCACODIGOAREA
     * 
     * @return listaCACODIGOAREA
     */
    public RegistroDataModel getListaCACODIGOAREA() {
        return listaCACODIGOAREA;
    }

    /**
     * Asigna la lista listaCACODIGOAREA
     * 
     * @param listaCACODIGOAREA
     * Variable a asignar en listaCACODIGOAREA
     */
    public void setListaCACODIGOAREA(RegistroDataModel listaCACODIGOAREA) {
        this.listaCACODIGOAREA = listaCACODIGOAREA;
    }

    /**
     * Retorna la lista listaCACODIGOAREA
     * 
     * @return listaCACODIGOAREA
     */
    public RegistroDataModel getListaCACODIGOAREAE() {
        return listaCACODIGOAREAE;
    }

    /**
     * Asigna la lista listaCACODIGOAREA
     * 
     * @param listaCACODIGOAREA
     * Variable a asignar en listaCACODIGOAREA
     */
    public void setListaCACODIGOAREAE(RegistroDataModel listaCACODIGOAREAE) {
        this.listaCACODIGOAREAE = listaCACODIGOAREAE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
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

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public String getIdcargo() {
        return codigo;
    }

    public void setIdcargo(String idcargo) {
        this.codigo = idcargo;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public Date getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(Date fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getCompania() {
        return compania;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
