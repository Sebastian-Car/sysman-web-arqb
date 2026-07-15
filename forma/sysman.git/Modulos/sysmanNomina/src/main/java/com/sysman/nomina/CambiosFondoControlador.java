package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import com.sysman.nomina.enums.CambiosFondoControladorEnum;
import com.sysman.nomina.enums.CambiosFondoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author cmanrique
 *
 * --Modificado por lcortes 15/03/2017 16:00 --> Ajustes de buenas
 * practicas SonarLint.
 * 
 * @version 3, 02/10/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 * <li>Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class CambiosFondoControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    /**
     * Constante a nivel de clase que aloja el nombre de la compania
     * en la que el usuario inicio sesion.
     */
    private final String nombreCompania = SessionUtil.getCompaniaIngreso()
                    .getNombre();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CLASE</code>.
     */
    private final String cClase = GeneralParameterEnum.CLASE.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CAMBIOSDEFONDO</code>.
     */
    private final String cCambiosDeFondo = GenericUrlEnum.CAMBIOSDEFONDO
                    .getTable();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOS</code>.
     */
    private final String cCodigos = CambiosFondoControladorEnum.CODIGOS
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ID_DE_EMPLEADO</code>.
     */
    private final String cIdDeEmpleado = GeneralParameterEnum.ID_DE_EMPLEADO
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>FECHAINICIAL</code>.
     */
    private final String cFechaInicial = GeneralParameterEnum.FECHAINICIAL
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>FONDOACTUAL</code>.
     */
    private final String cFondoActual = CambiosFondoControladorEnum.FONDOACTUAL
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPOFONDO</code>.
     */
    private final String cTipoFondo = CambiosFondoControladorEnum.TIPOFONDO
                    .getValue();

    private String escogerCuenta;
    private StreamedContent archivoDescarga;
    private Registro registroSub;

    /**
     * Atributo que controla el valor ingresado en el campo fecha
     * inicial.
     */
    private Date fechaInicial;

    /**
     * Atributo que controla el valor ingresado en el campo fecha
     * final.
     */
    private Date fechaFinal;

    private List<Registro> listaTIPOFONDO;

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los items del combo fondo actual al insertar
     * un nuevo registro
     */
    private RegistroDataModelImpl listaFONDOACTUAL;

    /**
     * Lista que contiene los items del combo fondo actual al editar
     * un registro
     */
    private RegistroDataModelImpl listaFONDOACTUALE;

    /**
     * Lista que contiene los items del combo fondo nuevo al insertar
     * un nuevo registro
     */
    private RegistroDataModelImpl listaEscogerCuentan;

    /**
     * Lista que contiene los items del combo fondo nuevo al editar un
     * registro
     */
    private RegistroDataModelImpl listaEscogerCuentanE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private List<Registro> listaSubcambiosdefondo;
    private int indiceSubcambiosdefondo;

    // <DECLARAR_EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: PCK_NOMINA.
     */
    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: PCK_NOMINA_COM4.
     */
    @EJB
    private EjbNominaCuatroRemote ejbNominaCuatro;
    // </DECLARAR_EJBs>

    public CambiosFondoControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            // 23
            numFormulario = GeneralCodigoFormaEnum.CAMBIOS_FONDO_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            logger.error(ex.getMessage(), ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.PERSONAL.getTable();

        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        urlListado = UrlServiceUtil.getUrlBeanById(
                        CambiosFondoControladorUrlEnum.URL0001.getValue());

        urlLectura = UrlServiceUtil.getUrlBeanById(
                        CambiosFondoControladorUrlEnum.URL0002.getValue());

        parametrosListado.put(cCompania, compania);
    }

    @Override
    public void iniciarListas() {
        cargarListaTIPOFONDO();

        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
    }

    @Override
    public void iniciarListasSub() {
        registroSub = new Registro(new HashMap<String, Object>());

        cargarListaSubcambiosdefondo();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubcambiosdefondo = null;
    }

    public void agregarRegistroSubSubcambiosdefondo() {
        try {
            registroSub.getCampos().put(cCompania, compania);

            registroSub.getCampos().put(cIdDeEmpleado,
                            registro.getCampos().get(cIdDeEmpleado));

            registroSub.getCampos().remove("FECHAINICIALF");
            registroSub.getCampos().remove("FECHA_TRASLADOF");
            registroSub.getCampos().remove("FECHAFINALF");
            registroSub.getCampos().remove("NOMBRE");
            registroSub.getCampos().remove("NOMBRE2");
            registroSub.getCampos().remove("NOMBRE3");
            registroSub.getCampos().put("CREATED_BY", usuario);
            registroSub.getCampos().put("DATE_CREATED", new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CAMBIOSDEFONDO
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubcambiosdefondo();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubcambiosdefondo(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void eliminarRegSubSubcambiosdefondo(Registro reg) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicionSubcambiosdefondo(Registro r) {
        indiceSubcambiosdefondo = listaSubcambiosdefondo.indexOf(r);
        escogerCuenta = r.getCampos().get("TIPOFONDO").toString();

        cargarListaEscogerCuentanE();
    }

    public void oprimirfondosActuales() {
        try {
            ejbNominaCuatro.crearNovedadesActuales(compania, usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

            cargarListaSubcambiosdefondo();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void oprimircrearNovedades() {
        try {
            int proceso = Integer.parseInt(SessionUtil
                            .getSessionVar("procesoNomina").toString());

            int anio = Integer.parseInt(
                            SessionUtil.getSessionVar("anioNomina").toString());

            int mes = Integer.parseInt(
                            SessionUtil.getSessionVar("mesNomina").toString());

            int periodo = Integer.parseInt(SessionUtil
                            .getSessionVar("periodoNomina").toString());

            ejbNominaCero.actualizarTraslados(compania, proceso, anio, mes,
                            periodo, usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

            cargarListaSubcambiosdefondo();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void oprimircambiosUnEmpleado() {
        generarReporte(1);
    }

    public void oprimirtodosEmpleados() {
        generarReporte(0);
    }

    @Override
    public void cargarRegistro() {
        // Evalua el estado del empleado.
        boolean permiteModificar = "RETIRADO"
                        .equals(registro.getCampos().get("ESTADO_ACTUAL"));

        accion = permiteModificar ? ACCION_VER : ACCION_MODIFICAR;

        precargarRegistro();
    }

    public void cambiarTIPOFONDO() {
        // <CODIGO_DESARROLLADO>
        registroSub.getCampos().put("FONDONUEVO", null);

        escogerCuenta = SysmanFunciones
                        .nvl(registroSub.getCampos().get(cTipoFondo), "")
                        .toString();

        cargarListaFONDOACTUAL();
        cargarListaEscogerCuentan();

        switch (escogerCuenta) {
        case "AFP":
            mostrarFondoActual("FECHAFONDOPENSION", "ID_DEL_FONDO");
            break;
        case "ARL":
            mostrarFondoActual("FECHAFONDORIESGOS", "FONDO_RIESGOS");
            break;
        case "EPS":
            mostrarFondoActual("FECHAFONDOSALUD", "FONDO_SALUD");
            break;
        default:
            registroSub.getCampos().put(cFechaInicial, null);
            registroSub.getCampos().put(cFondoActual, "");
            break;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTIPOFONDOC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        escogerCuenta = (String) listaSubcambiosdefondo.get(rowNum).getCampos()
                        .get(cTipoFondo);

        cargarListaEscogerCuentanE();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FECHA_TRASLADO en la
     * fila seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFechaTrasladoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FECHAFINAL en la fila
     * seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFECHAFINALC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        fechaInicial = null;
        fechaFinal = null;
    }

    public void cancelarEdicionSubcambiosdefondo() {
        cargarListaSubcambiosdefondo();
    }

    public void cargarListaSubcambiosdefondo() {
        String idDeEmpleado = registro.getCampos().get(cIdDeEmpleado)
                        .toString();

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(cCompania, compania);
            param.put(cIdDeEmpleado, idDeEmpleado);

            listaSubcambiosdefondo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            GenericUrlEnum.CAMBIOSDEFONDO
                                                                                            .getGridKey())
                                                            .getUrl(),
                                            param), CacheUtil.getLlaveServicio(
                                                            UrlServiceCache.SYSMANDSUNIST,
                                                            cCambiosDeFondo));
        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTIPOFONDO() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCodigos, SysmanFunciones.colocarComillas("AFP,ARL,EPS"));

        try {
            listaTIPOFONDO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiosFondoControladorUrlEnum.URL18074
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaFONDOACTUAL</code> asociada al combo
     * fondo actual al insertar.
     */
    public void cargarListaFONDOACTUAL() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiosFondoControladorUrlEnum.URL18929
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cClase, escogerCuenta);

        listaFONDOACTUAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * Carga la lista: <code>listaFONDOACTUALE</code> asociada al
     * combo fondo actual al editar.
     */
    public void cargarListaFONDOACTUALE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Carga la lista: {@code listaEscogerCuentan} asociada al combo
     * fondo nuevo al insertar un registro.
     */
    public void cargarListaEscogerCuentan() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiosFondoControladorUrlEnum.URL18929
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cClase, escogerCuenta);

        listaEscogerCuentan = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * Carga la lista: {@code listaEscogerCuentanE} asociada al combo
     * fondo nuevo al editar un registro.
     */
    public void cargarListaEscogerCuentanE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * {@code listaFONDOACTUAL}.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFONDOACTUAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registroSub.getCampos().put("FONDOACTUAL",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * {@code listaFONDOACTUAL}.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFONDOACTUALE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * {@code listaEscogerCuentan.}
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEscogerCuentan(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registroSub.getCampos().put("FONDONUEVO",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * {@code listaEscogerCuentan}.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEscogerCuentanE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }
    // </METODOS_COMBOS_GRANDES>

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param opcion
     * 1 -> Informe con el listado de traslados de un empleado.<br>
     * 2 -> Informe con el listado de traslados de todos los
     * empleados.
     */
    private void generarReporte(int opcion) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        archivoDescarga = null;
        String reporte = "000001Listadocambiosdefondo";

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("fechaInicialAux",
                        SysmanFunciones.formatearFecha(fechaInicial));

        reemplazar.put("fechaFinalAux",
                        SysmanFunciones.formatearFecha(fechaFinal));

        reemplazar.put("idEmpleado", 1 == opcion
            ? registro.getCampos().get(cIdDeEmpleado).toString() : "NULL");

        // </REEMPLAZAR VARIABLES EN CONSULTA>

        // <ENVIAR PARAMETROS AL REPORTE>
        parametros.put("PR_NOMBREEMPRESA", nombreCompania);

        // </ENVIAR PARAMETROS AL REPORTE>

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(modulo),
                        reemplazar, parametros);

        /*-aqui reporte hace referencia al nombre del reporte*/
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Asigna el valor de la fecha inicial y el fondo actual segun el
     * tipo de fondo seleccionado.
     * 
     * @param fecha
     * -> Nombre del campo que relaciona la fecha inicial.
     * @param fondo
     * -> Nombre del campo que relaciona el fondo actual.
     */
    private void mostrarFondoActual(String fecha, String fondo) {
        registroSub.getCampos().put(cFechaInicial,
                        registro.getCampos().get(fecha));

        registroSub.getCampos().put(cFondoActual,
                        registro.getCampos().get(fondo));
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public List<Registro> getListaTIPOFONDO() {
        return listaTIPOFONDO;
    }

    public void setListaTIPOFONDO(List<Registro> listaTIPOFONDO) {
        this.listaTIPOFONDO = listaTIPOFONDO;
    }

    public List<Registro> getListaSubcambiosdefondo() {
        return listaSubcambiosdefondo;
    }

    public void setListaSubcambiosdefondo(
        List<Registro> listaSubcambiosdefondo) {
        this.listaSubcambiosdefondo = listaSubcambiosdefondo;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getEscogerCuenta() {
        return escogerCuenta;
    }

    public void setEscogerCuenta(String escogerCuenta) {
        this.escogerCuenta = escogerCuenta;
    }

    public int getIndiceSubcambiosdefondo() {
        return indiceSubcambiosdefondo;
    }

    public void setIndiceSubcambiosdefondo(int indiceSubcambiosdefondo) {
        this.indiceSubcambiosdefondo = indiceSubcambiosdefondo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(cCompania, compania);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public RegistroDataModelImpl getListaFONDOACTUAL() {
        return listaFONDOACTUAL;
    }

    public void setListaFONDOACTUAL(RegistroDataModelImpl listaFONDOACTUAL) {
        this.listaFONDOACTUAL = listaFONDOACTUAL;
    }

    public RegistroDataModelImpl getListaFONDOACTUALE() {
        return listaFONDOACTUALE;
    }

    public void setListaFONDOACTUALE(RegistroDataModelImpl listaFONDOACTUALE) {
        this.listaFONDOACTUALE = listaFONDOACTUALE;
    }

    public RegistroDataModelImpl getListaEscogerCuentan() {
        return listaEscogerCuentan;
    }

    public void setListaEscogerCuentan(
        RegistroDataModelImpl listaEscogerCuentan) {
        this.listaEscogerCuentan = listaEscogerCuentan;
    }

    public RegistroDataModelImpl getListaEscogerCuentanE() {
        return listaEscogerCuentanE;
    }

    public void setListaEscogerCuentanE(
        RegistroDataModelImpl listaEscogerCuentanE) {
        this.listaEscogerCuentanE = listaEscogerCuentanE;
    }
}
