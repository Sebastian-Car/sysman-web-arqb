package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.enums.PrequisicionsControladorEnum;
import com.sysman.planeacion.enums.PrequisicionsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

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
 * @author acaceres
 * @version 1, 22/12/2015
 * 
 * @author eamaya
 * @version 2.0, 08/09/2017, Proceso de Refactoring DSS,Manejo de
 * EJBs, cambio de numero de formulario por Enum, cambio de textos por
 * textos en Bean y correcciones SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class PrequisicionsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private boolean bloqueaRequisiciones;
    private boolean viabilidadVisible;
    private boolean etiqueta87Visible;
    private boolean bloqueaCodigoReq;
    private String nombreActividad;
    private String claseGasto;
    private Registro registroSub;
    private String nombreElemento;
    private String nombreDependencia;
    private String dependenciaSub;
    private String responsable;
    private String sucursal;
    private String dependenciaDetalle;
    private Date fechaDetalle;
    private RegistroDataModelImpl listacmbDependencia;
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaE;
    private RegistroDataModelImpl listacmbActividad;
    private List<Registro> listaSubdetalle;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;

    private RegistroDataModelImpl listasubDependencia;

    private RegistroDataModelImpl listasubDependenciaE;
    private RegistroDataModel listaNombre;
    private RegistroDataModel listaNombreE;
    private String auxiliar;
    private StreamedContent archivoDescarga;
    private String codigoDependencia;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    public PrequisicionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        bloqueaRequisiciones = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.PREQUISICIONS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PrequisicionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        registro = new Registro(new HashMap<String, Object>());
        registroSub = new Registro(new HashMap<String, Object>());

    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.REQUISICION;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    @Override
    public void iniciarListas() {
        cargarListaElemento();
        cargarListaElementoE();
        cargarListacmbDependencia();
        cargarListaDependencia();
        cargarListaDependenciaE();
        cargarListacmbActividad();
        cargarListaSubDependencia();
        cargarListaSubDependenciaE();

    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubdetalle();
        nombreDependencia = null;
        dependenciaDetalle = null;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));

        Registro regNombreDependencia;
        try {
            regNombreDependencia = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrequisicionsControladorUrlEnum.URL9597
                                                                            .getValue())
                                            .getUrl(), param));

            nombreDependencia = regNombreDependencia.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName())
                            .toString();
            fechaDetalle = (Date) registro.getCampos().get("FECHAREQUISICION");
            dependenciaDetalle = registro.getCampos()
                            .get(GeneralParameterEnum.DEPENDENCIA.getName())
                            .toString();
            registroSub.getCampos().put(
                            GeneralParameterEnum.DEPENDENCIA.getName(),
                            dependenciaDetalle);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubdetalle = null;
        nombreDependencia = null;
        bloqueaCodigoReq = false;
    }

    public void cargarListacmbDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrequisicionsControladorUrlEnum.URL7838
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrequisicionsControladorUrlEnum.URL8712
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaDependenciaE() {
        listaDependenciaE = listaDependencia;

    }

    public void cargarListacmbActividad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrequisicionsControladorUrlEnum.URL9257
                                                        .getValue());

        listacmbActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, PrequisicionsControladorEnum.COD_ACTIVIDAD
                                        .getValue());

    }

    public void cargarListaElemento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrequisicionsControladorUrlEnum.URL10274
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaElementoE() {
        listaElementoE = listaElemento;

    }

    /**
     * 
     * Carga la lista listadependencia
     *
     */
    public void cargarListaSubDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrequisicionsControladorUrlEnum.URL7777
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listasubDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listadependencia
     *
     */
    public void cargarListaSubDependenciaE() {
        listasubDependenciaE = listasubDependencia;

    }

    public void cargarListaSubdetalle() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(PrequisicionsControladorEnum.REQUISICION.getValue(),
                            registro.getCampos()
                                            .get(PrequisicionsControladorEnum.COD_REQUISICION
                                                            .getValue()));

            listaSubdetalle = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrequisicionsControladorUrlEnum.URL5848
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "DETALLEREQUIS"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void agregarRegistroSubSubdetalle() {
        try {
            registroSub.getCampos()
                            .put(PrequisicionsControladorEnum.COD_REQUISICION
                                            .getValue(),
                                            registro.getCampos()
                                                            .get(PrequisicionsControladorEnum.COD_REQUISICION
                                                                            .getValue()));
            registroSub.getCampos().put("COMPANIA", compania);

            registroSub.getCampos().put(
                            PrequisicionsControladorEnum.COD_DETALLE.getValue(),
                            ejbSysmanUtl.generarConsecutivoConValorInicial(
                                            "DETALLEREQUIS",
                                            SysmanFunciones.concatenar(
                                                            "COMPANIA = ''",
                                                            compania, "''",
                                                            " AND  COD_REQUISICION = ''",
                                                            registro.getCampos()
                                                                            .get(PrequisicionsControladorEnum.COD_REQUISICION
                                                                                            .getValue())
                                                                            .toString(),
                                                            "''"),
                                            PrequisicionsControladorEnum.COD_DETALLE
                                                            .getValue(),
                                            "1"));

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSub.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());
            registroSub.getCampos()
                            .remove(PrequisicionsControladorEnum.NOMBRELARGO
                                            .getValue());
            registroSub.getCampos().remove("NOMBREDEPENDENCIA");

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PrequisicionsControladorUrlEnum.URL8888
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

            cargarListaSubdetalle();
        }
        catch (SystemException ex) {
            Logger.getLogger(PrequisicionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubdetalle(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().remove(PrequisicionsControladorEnum.NOMBRELARGO
                            .getValue());
            reg.getCampos().remove("NOMBREDEPENDENCIA");

            reg.getCampos().remove(PrequisicionsControladorEnum.COD_DETALLE
                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(PrequisicionsControladorEnum.COD_REQUISICION
                            .getValue());
            reg.getCampos().remove("PORCDESCUENTO");
            reg.getCampos().remove("PORCIVA");
            reg.getCampos().remove("SALDODETALLE");
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            new Date());

            reg.getLlave().put("KEY_COD_REQUISICION",
                            registro.getCampos()
                                            .get(PrequisicionsControladorEnum.COD_REQUISICION
                                                            .getValue()));

            reg.getLlave().put("KEY_COMPANIA", registro.getCampos()
                            .get(GeneralParameterEnum.COMPANIA.getName()));

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PrequisicionsControladorUrlEnum.URL11359
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(PrequisicionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubdetalle();
        }
    }

    public void eliminarRegSubSubdetalle(Registro reg) {
        try {

            reg.getLlave().put("KEY_COD_REQUISICION",
                            registro.getCampos()
                                            .get(PrequisicionsControladorEnum.COD_REQUISICION
                                                            .getValue()));

            reg.getLlave().put("KEY_COMPANIA", registro.getCampos()
                            .get(GeneralParameterEnum.COMPANIA.getName()));

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PrequisicionsControladorUrlEnum.URL14876
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubdetalle();
        }
        catch (SystemException ex) {
            Logger.getLogger(PrequisicionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cancelarEdicionSubdetalle() {
        cargarListaSubdetalle();
    }

    public void oprimirSeleccionarRequisiciones() {
        agregarRegistroNuevo(false);

        String codRequisicion = registro.getCampos().get(
                        PrequisicionsControladorEnum.COD_REQUISICION.getValue())
                        .toString();
        String codDetalle = registroSub.getCampos()
                        .get(PrequisicionsControladorEnum.COD_DETALLE
                                        .getValue()) == null
                                            ? "1"
                                            : registroSub.getCampos()
                                                            .get(PrequisicionsControladorEnum.COD_DETALLE
                                                                            .getValue())
                                                            .toString();

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put("codRequisicion", codRequisicion);
        parametros.put("codDetalle", codDetalle);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.AUXORDENDESUMINISTROS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);

        cargarListaSubdetalle();

    }

    public void obtenerReporte(FORMATOS formatos) {
        archivoDescarga = null;
        String codigoRequisicion = registro.getCampos()
                        .get(PrequisicionsControladorEnum.COD_REQUISICION
                                        .getValue())
                        .toString();
        try {
            Map<String, Object> reemplazar = new TreeMap<>();
            reemplazar.put("codRequisicion", codigoRequisicion);
            reemplazar.put("compania", compania);
            Map<String, Object> parametros = new TreeMap<>();
            // MANEJO DE PARAMETROS DE REEMPLAZO
            String reporte = "000454IRequisicion";
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }

        catch (JRException | IOException ex) {
            Logger.getLogger(PrequisicionsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarClaseGasto() {
        // <CODIGO_DESARROLLADO>
        claseGasto = SysmanFunciones
                        .nvl(registro.getCampos().get("CLASEGASTO"), "")
                        .toString();

        if ("I".equals(claseGasto)) {
            viabilidadVisible = true;
            etiqueta87Visible = true;
        }
        else {
            viabilidadVisible = false;
            etiqueta87Visible = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(
                        GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()));
        nombreElemento = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(PrequisicionsControladorEnum.NOMBRELARGO
                                                        .getValue()),
                                        "")
                        .toString();
        registroSub.getCampos().put(
                        PrequisicionsControladorEnum.NOMBRELARGO.getValue(),
                        nombreElemento);

    }

    public void seleccionarFilaElementoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();
        nombreElemento = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(PrequisicionsControladorEnum.NOMBRELARGO
                                                        .getValue()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasubDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registroSub.getCampos().put("DEPENDENCIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasubDependenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

    }

    public void cambiarFechaRequisicion() {
        // <CODIGO_DESARROLLADO>
        fechaDetalle = (Date) registro.getCampos().get("FECHAREQUISICION");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitario() {
        // <CODIGO_DESARROLLADO>
        double valorTotalDet;

        double cantidad = registroSub.getCampos()
                        .get(GeneralParameterEnum.CANTIDAD.getName()) == null
                            ? 0.0
                            : Double.parseDouble(
                                            registroSub.getCampos()
                                                            .get(GeneralParameterEnum.CANTIDAD
                                                                            .getName())
                                                            .toString());
        double valorUnitario = registroSub.getCampos()
                        .get(GeneralParameterEnum.VALORUNITARIO
                                        .getName()) == null ? 0.0
                                            : Double.parseDouble(registroSub
                                                            .getCampos()
                                                            .get(GeneralParameterEnum.VALORUNITARIO
                                                                            .getName())
                                                            .toString());

        valorTotalDet = cantidad * valorUnitario;

        registroSub.getCampos().put("VALORTOTAL", valorTotalDet);
        cargarListaSubdetalle();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitarioC(int rowNum) {

        actualizarValorTotal(rowNum);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidadC(int rowNum) {

        actualizarValorTotal(rowNum);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private void actualizarValorTotal(int rowNum) {
        double cantidadSub = listaSubdetalle.get(rowNum).getCampos()
                        .get(GeneralParameterEnum.CANTIDAD.getName()) == null
                            ? 0.0
                            : Double.parseDouble(listaSubdetalle
                                            .get(rowNum).getCampos()
                                            .get(GeneralParameterEnum.CANTIDAD
                                                            .getName())
                                            .toString());
        double valorUnitarioSub = listaSubdetalle.get(rowNum).getCampos()
                        .get(GeneralParameterEnum.VALORUNITARIO
                                        .getName()) == null ? 0.0
                                            : Double.parseDouble(listaSubdetalle
                                                            .get(rowNum)
                                                            .getCampos()
                                                            .get(GeneralParameterEnum.VALORUNITARIO
                                                                            .getName())
                                                            .toString());

        double valorTotalCon = cantidadSub * valorUnitarioSub;

        listaSubdetalle.get(rowNum).getCampos().put("VALORTOTAL",
                        valorTotalCon);

    }

    public void cambiarElementoC(int rowNum) {

        listaSubdetalle.get(rowNum).getCampos().put(
                        PrequisicionsControladorEnum.NOMBRELARGO.getValue(),
                        nombreElemento);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilacmbDependencia(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        codigoDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        dependenciaDetalle = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()),
                                        "")
                        .toString();
        registroSub.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependenciaDetalle);
    }

    public void seleccionarFilacmbActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(
                        PrequisicionsControladorEnum.COD_ACTIVIDAD.getValue(),
                        registroAux.getCampos()
                                        .get(PrequisicionsControladorEnum.COD_ACTIVIDAD
                                                        .getValue()));
        nombreActividad = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        String cmbActividad = registro.getCampos().get(
                        PrequisicionsControladorEnum.COD_ACTIVIDAD.getValue())
                        .toString();

        if (cmbActividad.length() <= 4) {
            cmbActividad = " ";
            nombreActividad = " ";
            registro.getCampos().put(PrequisicionsControladorEnum.COD_ACTIVIDAD
                            .getValue(), cmbActividad);
        }

    }

    public void retornarFormularioSeleccionarRequisiciones() {
        // <CODIGO_DESARROLLADO>
        cargarListaSubdetalle();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {

        try {
            nombreActividad = "";
            precargarRegistro();
            if (accion.equals(ACCION_INSERTAR)) {
                registro.getCampos().put("VALORESTIMADO", 0);
                registro.getCampos().put("VALORDISPONIBILIDAD", 0);
                if ((registro.getCampos()
                                .get(PrequisicionsControladorEnum.COD_ACTIVIDAD
                                                .getValue()) == null)
                    || "".equals(registro.getCampos()
                                    .get(PrequisicionsControladorEnum.COD_ACTIVIDAD
                                                    .getValue()))) {
                    registro.getCampos()
                                    .put(PrequisicionsControladorEnum.COD_ACTIVIDAD
                                                    .getValue(),
                                                    "999999999999999999");
                }
                bloqueaRequisiciones = true;
            }
            else {

                Map<String, Object> param = new TreeMap<>();

                param.put("COD_ACTIVIDAD", registro.getCampos()
                                .get(PrequisicionsControladorEnum.COD_ACTIVIDAD
                                                .getValue())
                                .toString());

                nombreActividad = SysmanFunciones
                                .nvl(listacmbActividad.getRegistroUnico(param)
                                                .getCampos()
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName()),
                                                "")
                                .toString();

                bloqueaRequisiciones = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CODIGO.getName(), codigoDependencia);

        Registro regResponsable;
        try {
            regResponsable = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrequisicionsControladorUrlEnum.URL9318
                                                                            .getValue())
                                            .getUrl(), param));

            if (regResponsable != null) {

                String responsableDep = SysmanFunciones
                                .nvl(regResponsable.getCampos()
                                                .get(GeneralParameterEnum.RESPONSABLE
                                                                .getName()),
                                                "")
                                .toString();

                String sucursalDep = SysmanFunciones
                                .nvl(regResponsable.getCampos()
                                                .get(GeneralParameterEnum.SUCURSAL
                                                                .getName()),
                                                "")
                                .toString();

                registro.getCampos().put(
                                GeneralParameterEnum.RESPONSABLE.getName(),
                                responsableDep);
                registro.getCampos().put(
                                GeneralParameterEnum.SUCURSAL.getName(),
                                sucursalDep);
                registro.getCampos()
                                .remove(GeneralParameterEnum.NOMBRE.getName());

                registro.getCampos()
                                .put(PrequisicionsControladorEnum.COD_REQUISICION
                                                .getValue(),
                                                ejbSysmanUtl.generarSiguienteConsecutivo(
                                                                "REQUISICION",
                                                                "COMPANIA = ''"
                                                                    + compania
                                                                    + "''",
                                                                PrequisicionsControladorEnum.COD_REQUISICION
                                                                                .getValue())

                );

            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3543"));
                return false;
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(PrequisicionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        registro.getCampos().remove(PrequisicionsControladorEnum.COD_REQUISICION
                        .getValue());
        return true;
    }

    @Override
    public boolean eliminarDespues() {

        return true;
    }

    public RegistroDataModelImpl getListacmbDependencia() {
        return listacmbDependencia;
    }

    public void setListacmbDependencia(
        RegistroDataModelImpl listacmbDependencia) {
        this.listacmbDependencia = listacmbDependencia;
    }

    public RegistroDataModelImpl getListacmbActividad() {
        return listacmbActividad;
    }

    public void setListacmbActividad(RegistroDataModelImpl listacmbActividad) {
        this.listacmbActividad = listacmbActividad;
    }

    public List<Registro> getListaSubdetalle() {
        return listaSubdetalle;
    }

    public void setListaSubdetalle(List<Registro> listaSubdetalle) {
        this.listaSubdetalle = listaSubdetalle;
    }

    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    public void setListaElemento(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getListaElementoE() {
        return listaElementoE;
    }

    public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
        this.listaElementoE = listaElementoE;
    }

    /**
     * Retorna la lista listasubDependencia
     * 
     * @return listasubDependencia
     */
    public RegistroDataModelImpl getListasubDependencia() {
        return listasubDependencia;
    }

    /**
     * Asigna la lista listasubDependencia
     * 
     * @param listasubDependencia
     * Variable a asignar en listasubDependencia
     */
    public void setListasubDependencia(
        RegistroDataModelImpl listasubDependencia) {
        this.listasubDependencia = listasubDependencia;
    }

    /**
     * Retorna la lista listasubDependencia
     * 
     * @return listasubDependencia
     */
    public RegistroDataModelImpl getListasubDependenciaE() {
        return listasubDependenciaE;
    }

    /**
     * Asigna la lista listasubDependencia
     * 
     * @param listasubDependencia
     * Variable a asignar en listasubDependencia
     */
    public void setListasubDependenciaE(
        RegistroDataModelImpl listasubDependenciaE) {
        this.listasubDependenciaE = listasubDependenciaE;
    }

    public RegistroDataModel getListaNombre() {
        return listaNombre;
    }

    public void setListaNombre(RegistroDataModel listaNombre) {
        this.listaNombre = listaNombre;
    }

    public RegistroDataModel getListaNombreE() {
        return listaNombreE;
    }

    public void setListaNombreE(RegistroDataModel listaNombreE) {
        this.listaNombreE = listaNombreE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isBloqueaRequisiciones() {
        return bloqueaRequisiciones;
    }

    public void setBloqueaRequisiciones(boolean bloqueaRequisiciones) {
        this.bloqueaRequisiciones = bloqueaRequisiciones;
    }

    public boolean isViabilidadVisible() {
        return viabilidadVisible;
    }

    public void setViabilidadVisible(boolean viabilidadVisible) {
        this.viabilidadVisible = viabilidadVisible;
    }

    public boolean isEtiqueta87Visible() {
        return etiqueta87Visible;
    }

    public void setEtiqueta87Visible(boolean etiqueta87Visible) {
        this.etiqueta87Visible = etiqueta87Visible;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public String getNombreElemento() {
        return nombreElemento;
    }

    public void setNombreElemento(String nombreElemento) {
        this.nombreElemento = nombreElemento;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public String getDependenciaSub() {
        return dependenciaSub;
    }

    public void setDependenciaSub(String dependenciaSub) {
        this.dependenciaSub = dependenciaSub;
    }

    public boolean isBloqueaCodigoReq() {
        return bloqueaCodigoReq;
    }

    public void setBloqueaCodigoReq(boolean bloqueaCodigoReq) {
        this.bloqueaCodigoReq = bloqueaCodigoReq;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public RegistroDataModelImpl getListaDependenciaE() {
        return listaDependenciaE;
    }

    public void setListaDependenciaE(RegistroDataModelImpl listaDependenciaE) {
        this.listaDependenciaE = listaDependenciaE;
    }

    public String getDependenciaDetalle() {
        return dependenciaDetalle;
    }

    public void setDependenciaDetalle(String dependenciaDetalle) {
        this.dependenciaDetalle = dependenciaDetalle;
    }

    public Date getFechaDetalle() {
        return fechaDetalle;
    }

    public void setFechaDetalle(Date fechaDetalle) {
        this.fechaDetalle = fechaDetalle;
    }

    public String getCodigoDependencia() {
        return codigoDependencia;
    }

    public void setCodigoDependencia(String codigoDependencia) {
        this.codigoDependencia = codigoDependencia;
    }

    public String getClaseGasto() {
        return claseGasto;
    }

    public void setClaseGasto(String claseGasto) {
        this.claseGasto = claseGasto;
    }

}
