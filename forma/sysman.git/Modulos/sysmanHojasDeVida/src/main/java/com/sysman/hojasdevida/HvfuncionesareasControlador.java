/*-
 * HvfuncionesareasControlador.java
 *
 * 1.0
 * 
 * 24/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase que permite gestionar los datos de las funciones de las areas
 *
 * @version 1.0, 24/02/2017
 * @author jguerrero
 */
@ManagedBean
@ViewScoped
public class HvfuncionesareasControlador extends BeanBaseContinuoAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el String GRADO
     */
    private final String gradoCons;
    /**
     * Constante a nivel de clase que almacena el String
     * FECHA_CREACION
     */
    private final String fechaCreacionCon;
    /**
     * Constante a nivel de clase que almacena el String CODIGO
     */
    private final String codigoCons;
    /**
     * Constante a nivel de clase que almacena el String IDFUNCION
     */
    private final String idFUncion;
    /**
     * Constante a nivel de clase que almacena el String CODAREA
     */
    private final String codArea;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena temporalmente la area seleccionada por el
     * combo en el formulario
     */
    private String area;

    /**
     * Variable que almacena temporalmente el codigo de area
     * seleccionado por el formulario
     */
    private String codigoArea;
    /**
     * Variable que almacena temporalmente la el cargo seleccionada
     * por el combo cargo en el formulario
     */

    private String cargo;
    /**
     * Variable que almacena temporalmente la el cargo seleccionada
     * por el combo cargo en el formulario
     */
    private String grado;
    /**
     * Variable que almacena temporalmente el a�o seleccionada por el
     * combo cargo en el formulario
     */
    private String ano;
    /**
     * Variable que almacena temporalmente el cargo seleccionada por
     * el combo cargo en el formulario
     */
    private String etiquetaCargo;
    /**
     * Variable que almacena temporalmente la fecha de creacion
     * seleccionada por el combo cargo en el formulario
     */

    private Date fechaCreacion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encarada de almacenar los datos de respuesta de la base
     * de datos de las funciones de area
     */
    private RegistroDataModel listaarea;
    /**
     * Lista encarada de almacenar los datos de respuesta de la base
     * de datos de las funciones de area
     */
    private RegistroDataModel listaareaE;
    /**
     * Lista encarada de almacenar los datos de respuesta de la base
     * de datos del cargo
     */
    private RegistroDataModel listacargo;
    /**
     * Lista encarada de almacenar los datos de respuesta de la base
     * de datos del cargo
     */
    private RegistroDataModel listacargoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de HvfuncionesareasControlador
     */
    public HvfuncionesareasControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";
        gradoCons = "GRADO";
        fechaCreacionCon = "FECHA_CREACION";
        idFUncion = "IDFUNCION";
        codArea = "CODAREA";

        try {
            numFormulario = 1308;
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
        try {
            tabla = "NAT_FUNCIONES_AREAS";
            reasignarOrigen();
            buscarLlave();
            conectorPool.conectar(nombreConexion);
            registro = new Registro();
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListaarea();
            cargarListaareaE();
            cargarListacargo();
            cargarListacargoE();
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
        origenDatos = "SELECT "
            + " IDCARGO,"
            + " COMPANIA,"
            + "CODIGOAREA, " +
            "   IDFUNCION," +
            "   ANO," +
            "   FECHA_CREACION,"
            +
            "   DESCRIPCION_FUNCION_AREAS " +
            " FROM NAT_FUNCIONES_AREAS " +
            " WHERE COMPANIA ='" + compania + "' " +
            " AND CODIGOAREA = '" + codigoArea + "' " +
            "AND  IDCARGO ='" + cargo + "' " +
            " AND ANO = " + ano;
        if (listaInicial != null) {
            listaInicial.setOrigen(origenDatos);
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaarea
     * 
     * Este metodo es el encargado de hacer la llamada a la base datos
     * y almacenar la respuesta en la lista anteriormente mencionada
     *
     */
    public void cargarListaarea() {
        listaarea = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1308:TBCB4305", "SELECT DISTINCT  " +
                            " NOMBRE_AREA," +
                            "     CODAREA " +
                            "   FROM " +
                            "     NAT_AREAS_DESEMPENO",
                        true, codArea);
    }

    /**
     * 
     * Carga la lista listaarea Este metodo es el encargado de hacer
     * la llamada a la base datos y almacenar la respuesta en la lista
     * anteriormente mencionada
     *
     */
    public void cargarListaareaE() {
        listaareaE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1308:TBCB4305", "  SELECT DISTINCT " +
                            "     NOMBRE_AREA," +
                            " CODAREA" +
                            "     " +
                            "   FROM " +
                            "     NAT_AREAS_DESEMPENO",
                        true, codArea);
    }

    /**
     * 
     * Carga la lista listacargo
     *
     */
    public void cargarListacargo() {
        listacargo = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1308:TBCB4306", "SELECT DISTINCT " +
                            "     CODIGO, " +
                            "     GRADO, " +
                            "    TO_CHAR(FECHACREACION,'DD/MM/YYYY') FECHACREACION  , "
                            +
                            "     DENOMINACION," +
                            "     EXTRACT (YEAR FROM FECHACREACION) AS ANOCREACION, "
                            + "   FECHACREACION   FECHACREACION_VISTA "
                            +
                            " FROM " +
                            "     NAT_HVCARGOS",
                        true, codigoCons);
    }

    /**
     * 
     * Carga la lista listacargo Este metodo es el encargado de hacer
     * la llamada a la base datos y almacenar la respuesta en la lista
     * anteriormente mencionada
     *
     */
    public void cargarListacargoE() {
        listacargoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1308:TBCB4306", "SELECT DISTINCT " +
                            "     CODIGO, " +
                            "     GRADO, " +
                            "     FECHACREACION, " +
                            "     DENOMINACION," +
                            "     EXTRACT (YEAR FROM FECHACREACION) AS ANOCREACION"
                            +
                            " FROM " +
                            "     NAT_HVCARGOS",
                        true, codigoCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaarea
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaarea(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        area = registroAux.getCampos().get("NOMBRE_AREA").toString();
        codigoArea = registroAux.getCampos().get(codArea).toString();
        reasignarOrigen();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaarea
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaareaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(codArea);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacargo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */

    public void seleccionarFilacargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cargo = registroAux.getCampos().get(codigoCons).toString();
        ano = registroAux.getCampos().get("ANOCREACION").toString();
        etiquetaCargo = registroAux.getCampos().get("DENOMINACION").toString();
        fechaCreacion = (Date) registroAux.getCampos()
                        .get("FECHACREACION_VISTA");

        grado = registroAux.getCampos().get(gradoCons)
                        .toString();

        registro.getCampos().put("ANO", ano);
        registro.getCampos().put(fechaCreacionCon, fechaCreacion);
        reasignarOrigen();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacargo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacargoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(codigoCons);
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
        Long numeroFuncion = generarConsecutivo();

        if (SysmanFunciones.validarVariableVacio(codigoArea)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2866"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(cargo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2867"));
            return false;
        }

        registro.getCampos().put(fechaCreacionCon, fechaCreacion);
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put(idFUncion, numeroFuncion);
        registro.getCampos().put("CODIGOAREA", codigoArea);
        registro.getCampos().put("IDCARGO", cargo);
        registro.getCampos().put(gradoCons, grado);

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
        reasignarOrigen();
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

        registro.getCampos().put("COMPANIA", compania);

        registro.getCampos().put("CODIGOAREA", codigoArea);
        registro.getCampos().put("IDCARGO", cargo);
        registro.getCampos().put(gradoCons, grado);
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
        registro.getCampos().put("ANO", ano);

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
        // Metodo heredado de la clase BeanBase
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        Long numeroFuncion = generarConsecutivo();
        registro.getCampos().put(idFUncion, numeroFuncion);

        registro.getCampos().put("ANO", ano);
        registro.getCampos().put(fechaCreacionCon, fechaCreacion);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable area
     * 
     * @return area
     */
    public String getArea() {
        return area;
    }

    /**
     * Asigna la variable area
     * 
     * @param area
     * Variable a asignar en area
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Retorna la variable cargo
     * 
     * @return cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Asigna la variable cargo
     * 
     * @param cargo
     * Variable a asignar en cargo
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable etiquetaCargo
     * 
     * @return etiquetaCargo
     */
    public String getEtiquetaCargo() {
        return etiquetaCargo;
    }

    /**
     * Asigna la variable etiquetaCargo
     * 
     * @param etiquetaCargo
     * Variable a asignar en etiquetaCargo
     */
    public void setEtiquetaCargo(String etiquetaCargo) {
        this.etiquetaCargo = etiquetaCargo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaarea
     * 
     * @return listaarea
     */
    public RegistroDataModel getListaarea() {
        return listaarea;
    }

    /**
     * Asigna la lista listaarea
     * 
     * @param listaarea
     * Variable a asignar en listaarea
     */
    public void setListaarea(RegistroDataModel listaarea) {
        this.listaarea = listaarea;
    }

    /**
     * Retorna la lista listaarea
     * 
     * @return listaarea
     */
    public RegistroDataModel getListaareaE() {
        return listaareaE;
    }

    /**
     * Asigna la lista listaarea
     * 
     * @param listaarea
     * Variable a asignar en listaarea
     */
    public void setListaareaE(RegistroDataModel listaareaE) {
        this.listaareaE = listaareaE;
    }

    /**
     * Retorna la lista listacargo
     * 
     * @return listacargo
     */
    public RegistroDataModel getListacargo() {
        return listacargo;
    }

    /**
     * Asigna la lista listacargo
     * 
     * @param listacargo
     * Variable a asignar en listacargo
     */
    public void setListacargo(RegistroDataModel listacargo) {
        this.listacargo = listacargo;
    }

    /**
     * Retorna la lista listacargo
     * 
     * @return listacargo
     */
    public RegistroDataModel getListacargoE() {
        return listacargoE;
    }

    /**
     * Asigna la lista listacargo
     * 
     * @param listacargo
     * Variable a asignar en listacargo
     */
    public void setListacargoE(RegistroDataModel listacargoE) {
        this.listacargoE = listacargoE;
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

    private Long generarConsecutivo() {
        Long consecutivo = null;
        String condicion = " CODIGOAREA='" + codigoArea + "' AND IDCARGO ='"
            + cargo + "' AND ANO = " + ano;

        try {
            consecutivo = Acciones.genConsecutivo(ConectorPool.ESQUEMA_SYSMAN,
                            "NAT_FUNCIONES_AREAS", condicion, idFUncion,
                            "1");
        }
        catch (SQLException | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
