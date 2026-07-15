package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.nomina.enums.PeriodosControladorUrlEnum;
import com.sysman.nomina.enums.SubformdetallevacacionesControladorEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que gestiona la programación de vacaciones.
 *
 * @version 1.0, 18/05/2018
 * @author jmalaver
 */
@ManagedBean
@ViewScoped
public class SubformdetallevacacionesControlador
                extends BeanBaseContinuoAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /** Constante a nivel de clase que aloja la cadena: COMPANIA */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaSieteRemote ejbNominaSieteRemote;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * lista que permite visualizar el combo anio (oculto en el
     * formulario)
     */
    private List<Registro> listaCbAnio;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private String idEmpleado;

    private String nombreEmpleado;

    private Map<String, Object> parametrosEntrada;

    private Map<String, Object> ridDetalleVac;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private StreamedContent archivoDescarga;

    private final String modulo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * 
     * Crea una nueva instancia de SubformdetallevacacionesControlador
     */
    @SuppressWarnings("unchecked")
    public SubformdetallevacacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.SUBFORMDETALLEVACACIONES_CONTROLADOR
                            .getCodigo();

            idEmpleado = (String) parametrosEntrada
                            .get("idEmpleado");

            nombreEmpleado = (String) parametrosEntrada
                            .get("nombreEmpleado");

            ridDetalleVac = (Map<String, Object>) parametrosEntrada
                            .get("rid");

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
        enumBase = GenericUrlEnum.D_VACACIONES;
        registro = new Registro();
        reasignarOrigen();
        buscarLlave();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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

        parametrosListado.put(cCompania, compania);
        parametrosListado.put("ANIO", SysmanFunciones.ano(new Date()));
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaCbAnio
     *
     */
    public void cargarListaCbAnio() {

        try {
            listaCbAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodosControladorUrlEnum.URL4925
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnGenerarProgramacion en
     * la vista
     *
     */
    public void oprimirBtnGenerarProgramacion() {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * @return
     */
    private boolean validarFechaInicioDisfrute() {

        Date fechaFinal = (Date) listaInicial.getDatasource()
                        .get(indice % 10).getCampos()
                        .get(SubformdetallevacacionesControladorEnum.FECHA_FINAL
                                        .getValue());

        Date fechaInicioDisfrute = (Date) listaInicial.getDatasource()
                        .get(indice % 10).getCampos()
                        .get(SubformdetallevacacionesControladorEnum.FECHA_INICIO_DISFRUTE
                                        .getValue());

        return SysmanFunciones.comparaFechas(fechaInicioDisfrute,
                        SysmanFunciones.sumarRestarDiasFecha(fechaFinal, +1));

    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicioDisfrute en
     * la fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFechaInicioDisfruteC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get(SubformdetallevacacionesControladorEnum.FECHA_INICIO_DISFRUTE
                                        .getValue())) != null) {
            if (validarFechaInicioDisfrute()) {

                listaInicial.getDatasource()
                                .get(rowNum % 10).getCampos()
                                .put(SubformdetallevacacionesControladorEnum.FECHA_ESTIMADA_REGRESO
                                                .getValue(), null);
                listaInicial.getDatasource()
                                .get(rowNum % 10).getCampos()
                                .put(SubformdetallevacacionesControladorEnum.FECHA_INICIO_DISFRUTE
                                                .getValue(), null);
                listaInicial.getDatasource()
                                .get(rowNum % 10).getCampos()
                                .put(SubformdetallevacacionesControladorEnum.DIAS_ESTIMADOS_DISFRUTE
                                                .getValue(), null);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4124"));
            }

            else {
                Date fechaInicioDisfrute = (Date) listaInicial.getDatasource()
                                .get(rowNum % 10).getCampos()
                                .get(SubformdetallevacacionesControladorEnum.FECHA_INICIO_DISFRUTE
                                                .getValue());

                try {
                    Integer habiles = Integer
                                    .parseInt(ejbSysmanUtil.consultarParametro(
                                                    compania,
                                                    "DIAS HABILES ENTIDAD",
                                                    SessionUtil.getModulo(),
                                                    new Date(), true));

                    habiles = habiles == null ? 15
                        : habiles;

                    Date fechaEstimadaRegreso = ejbSysmanUtil
                                    .retornarFechaMasDiasHabiles(
                                                    compania,
                                                    fechaInicioDisfrute,
                                                    habiles + 1,
                                                    false);

                    listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                    .put(SubformdetallevacacionesControladorEnum.DIAS_ESTIMADOS_DISFRUTE
                                                    .getValue(),
                                                    SysmanFunciones.calcularDiferenciaDias(
                                                                    fechaInicioDisfrute,
                                                                    fechaEstimadaRegreso));

                    listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                    .put(SubformdetallevacacionesControladorEnum.FECHA_ESTIMADA_REGRESO
                                                    .getValue(),
                                                    ejbSysmanUtil.retornarFechaMasDiasHabiles(
                                                                    compania,
                                                                    fechaInicioDisfrute,
                                                                    habiles + 1,
                                                                    false));
                }
                catch (SystemException | ParseException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
        }

        // </CODIGO_DESARROLLADO>
    }

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

        try {
            ejbNominaSieteRemote.programarVacaciones(compania,
                            SysmanFunciones.ano(new Date()),
                            SessionUtil.getUser().getCodigo());

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        // indice = listaInicial.getRowIndex();
        indice = registro.getIndice();

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
     * @return true
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
     * @return true
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
     * @return true
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
     * @return true
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
     * @return true
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
     * @return true
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        registro.getCampos().remove("ANIO");
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove("NOMBRECOMPLETO");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new HashMap<>();

        param.put("ridR", ridDetalleVac);

        param.put("idEmpleado", idEmpleado);
        param.put("nombreEmpleado", nombreEmpleado);
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PERSONALS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        String informe = "001807VacacionesProgramadas";

        try {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("anio", SysmanFunciones.ano(new Date()));
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(informe,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
     * Retorna la variable idEmpleado
     * 
     * @return idEmpleado
     */
    public String getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Asigna la variable idEmpleado
     * 
     * @param idEmpleado
     * Variable a asignar en idEmpleado
     */
    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    /**
     * Retorna la variable nombreEmpleado
     * 
     * @return nombreEmpleado
     */
    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    /**
     * Asigna la variable nombreEmpleado
     * 
     * @param nombreEmpleado
     * Variable a asignar en nombreEmpleado
     */
    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    // </SET_GET_ATRIBUTOS>

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the listaCbAnio
     */
    public List<Registro> getListaCbAnio() {
        return listaCbAnio;
    }

    /**
     * @param listaCbAnio
     * the listaCbAnio to set
     */
    public void setListaCbAnio(List<Registro> listaCbAnio) {
        this.listaCbAnio = listaCbAnio;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar
     * the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @param habiles
     * the habiles to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

}
