/*-
 * HvcargosControlador.java
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

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 24/02/2017
 * @author spina
 */
@ManagedBean
@ViewScoped
public class HvcargosControlador extends BeanBaseDatosAcme {
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
     * lista para cargar el combo dependencia del formulario
     */
    private List<Registro> listaDependencia;
    /**
     * lista para cargar el combo de areas en el formulario
     */
    private List<Registro> listaarea;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista para cargar el combo con las denominaciones en el
     * formulario
     */
    private RegistroDataModel listaDenominacion;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * lista para cargar el combo con las funciones
     */
    private List<Registro> listaFunciones;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */

    /**
     * captura la denominacion seleccionada
     */
    private String denominacion;

    /**
     * variable que genera el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * variable qeu el valor del modulo actual
     */
    private String modulo;
    /**
     * captura los parametros de entrada
     */
    private Map<String, Object> parametrosEntrada;

    /**
     * declaracion de constantes
     */

    private static final String CTEGRADO = "GRADO";
    private static final String CTEDENOMINACION = "DENOMINACION";
    static final String CTECODIGO = "CODIGO";

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de HvcargosControlador
     */
    @SuppressWarnings("unchecked")
    public HvcargosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = 1309;
            validarPermisos();

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
            }
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
        cargarListaDenominacion();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaDependencia();
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
        listaFunciones = null;
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
        tabla = "NAT_HVCARGOS";
        buscarLlave();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "SELECT COMPANIA,"
            + " FECHACREACION, "
            + " CODIGO, "
            + " GRADO, "
            + " DENOMINACION, "
            + " NUMEROCARGOS, "
            + " DEPENDENCIA,"
            + " JEFEINMEDIATO,"
            + " NATURALEZA,"
            + " INDACTIVO,"
            + " UBICACIONGEOGRAFICA,"
            + " PROPOSITO, "
            + " CONOCIMIENTOS_BASICOS,"
            + " ESTUDIOS,"
            + " EXPERIENCIA,"
            + " EQUIVALENCIAS,"
            + " NUMERO_RESOLUCION,"
            + " SIGLA,"
            + " NIVEL,"
            + " METAS,"
            + " CONTRIBUCIONES_INDIVIDUALES,"
            + " CREATED_BY,"
            + " DATE_CREATED,"
            + " MODIFIED_BY,"
            + " DATE_MODIFIED,"
            + " FORMACION,"
            + " HABILIDADES "
            + " FROM NAT_HVCARGOS";
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     * 
     */
    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = " SELECT "
            + " COMPANIA,"
            + " CODIGO, "
            + " GRADO, "
            + " DENOMINACION, "
            + " NUMEROCARGOS, "
            + " (SELECT DEPENDENCIA.NOMBRE "
            + " FROM DEPENDENCIA "
            + " WHERE DEPENDENCIA.CODIGO = NAT_HVCARGOS.DEPENDENCIA "
            + " AND DEPENDENCIA.COMPANIA = NAT_HVCARGOS.COMPANIA"
            + " ) DEPENDENCIA,"
            + " FECHACREACION"
            + " FROM NAT_HVCARGOS "
            + " WHERE COMPANIA = '" + compania + "'";
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
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {
        listaDependencia = service.getListado(conectorPool, "SELECT " +
            "     DEPENDENCIA.CODIGO, " +
            "     DEPENDENCIA.NOMBRE " +
            " FROM " +
            "     DEPENDENCIA "
            + " WHERE COMPANIA = '" + compania + "'" +
            " ");
    }

    /**
     * 
     * Carga la lista listaDenominacion
     *
     */
    public void cargarListaDenominacion() {
        listaDenominacion = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1309_nuevo:TBCB4303", "SELECT " +
                            "     CARGOS.ID_DE_CARGO, " +
                            "     CARGOS.NOMBRE_DEL_CARGO, " +
                            "     CARGOS.ESCALAFON, " +
                            "     CARGOS.CARRERA, " +
                            "     CARGOS.GRADO, " +
                            "     CARGOS.PLAZAS, " +
                            "     CARGOS.FUNCIONES, " +
                            "     CARGOS.ENCARRERA, " +
                            "     CARGOS.TIPO, " +
                            "     CARGOS.VALOR_HORA, " +
                            "     ESCALAFON.NOMBRE " +
                            " FROM " +
                            "     CARGOS " +
                            "         INNER JOIN ESCALAFON " +
                            "         ON CARGOS.COMPANIA = ESCALAFON.COMPANIA" +
                            "         AND CARGOS.ESCALAFON = ESCALAFON.CODIGO "
                            + " WHERE CARGOS.COMPANIA = '" + compania + "'"
                            + " ORDER BY CARGOS.ID_DE_CARGO" +
                            " ",
                        true, "ID_DE_CARGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDenominacion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDenominacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CTEDENOMINACION,
                        registroAux.getCampos().get("NOMBRE_DEL_CARGO"));
        registro.getCampos().put(CTECODIGO,
                        registroAux.getCampos().get("ID_DE_CARGO"));

    }

    public void ejecutarrcCerrar() {
        parametrosEntrada = null;
        String ruta = "/menu.sysman";
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdPdf en la vista
     *
     *
     */
    public void oprimirCmdPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdExcel en la vista
     *
     *
     */
    public void oprimirCmdExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generaInforme(FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        try {

            reemplazar.put("compania",
                            "'" + compania + "'");
            reemplazar.put("codigo",
                            "'" + registro.getCampos().get(CTECODIGO) + "'");
            reemplazar.put("grado",
                            "'" + registro.getCampos().get(CTEGRADO) + "'");

            Reporteador.resuelveConsulta("001435ManualDeFunciones",
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001435ManualDeFunciones", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException
                        | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton abrircargos en la vista
     *
     *
     */

    public void oprimirabrirdetalle() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { CTECODIGO, CTEGRADO, "ACCION" };
        String[] valores = { (String) registro.getCampos()
                        .get(CTECODIGO),
                             (String) registro.getCampos()
                                             .get(CTEGRADO),
                             accion };
        SessionUtil.cargarModalDatos("1314",
                        SessionUtil.getModulo(), campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirabrircargos() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        Map<String, Object> parametros = new HashMap<>();
        String ruta = "/cargosporareas.sysman";

        parametros.put("rid", css);
        parametros.put(CTEDENOMINACION,
                        String.valueOf(registro.getCampos()
                                        .get(CTEDENOMINACION)));
        parametros.put(CTECODIGO,
                        String.valueOf(registro.getCampos()
                                        .get(CTECODIGO)));
        parametros.put(CTEGRADO,
                        String.valueOf(registro.getCampos()
                                        .get(CTEGRADO)));
        parametros.put("FECHACREACION",
                        registro.getCampos()
                                        .get("FECHACREACION"));
        parametros.put("ACCION", accion);

        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar("retornoFormulario", "retorna");
        SessionUtil.redireccionar(direccionador);

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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public List<Registro> getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(List<Registro> listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaarea
     * 
     * @return listaarea
     */
    public List<Registro> getListaarea() {
        return listaarea;
    }

    /**
     * Asigna la lista listaarea
     * 
     * @param listaarea
     * Variable a asignar en listaarea
     */
    public void setListaarea(List<Registro> listaarea) {
        this.listaarea = listaarea;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDenominacion
     * 
     * @return listaDenominacion
     */
    public RegistroDataModel getListaDenominacion() {
        return listaDenominacion;
    }

    /**
     * Asigna la lista listaDenominacion
     * 
     * @param listaDenominacion
     * Variable a asignar en listaDenominacion
     */
    public void setListaDenominacion(RegistroDataModel listaDenominacion) {
        this.listaDenominacion = listaDenominacion;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaFunciones
     * 
     * @return listaFunciones
     */
    public List<Registro> getListaFunciones() {
        return listaFunciones;
    }

    /**
     * Asigna la lista listaFunciones
     * 
     * @param listaFunciones
     * Variable a asignar en listaFunciones
     */
    public void setListaFunciones(List<Registro> listaFunciones) {
        this.listaFunciones = listaFunciones;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ADICIONALES>
}
