package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.EscalafonsControladorEnum;
import com.sysman.general.enums.EscalafonsControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author cmanrique
 *
 * Revision Sonar * -- Modificado por ybecerra 16/03/2017
 * 
 * @author jcrodriguez,Refactoring y depuracion
 * @version 2, 29/09/2017
 */
@ManagedBean
@ViewScoped
public class EscalafonsControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se repite el
     * reemplazo <code>#$rta$#</code> que se utiliza para el Texto en
     * Bean <code>TB_TB2520</code>
     */
    private final String cReemplazo;
    /**
     * Constante definida por el numero de veces que se repite el
     * llamado al Texto en Bean <code>TB_TB2520</code>
     */
    private final String cTb2520;

    // <DECLARAR_ATRIBUTOS>
    private String anoNomina;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Variable que valida la visibilidad de botones
     */
    private boolean visibleNomina;

    private boolean visibleJornal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModelImpl listaCategorias;
    /**
     * Lista de registros de la tabla CGR_CODIGOS
     */
    private RegistroDataModelImpl listaEscalafonCgr;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSub;
    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de EscalafonsControlador
     */
    public EscalafonsControlador() {
        super();
        compania = SessionUtil.getCompania();
        cReemplazo = "#$rta$#";
        cTb2520 = "TB_TB2520";

        try {
            numFormulario = GeneralCodigoFormaEnum.ESCALAFONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>
        cargarListaEscalafonCgr();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        cargarListaCategorias();
        cargarListaAno();
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        listaCategorias = null;
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
        enumBase = GenericUrlEnum.ESCALAFON;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * 
     * Carga la lista listaCategorias
     */
    public void cargarListaCategorias() {
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscalafonsControladorUrlEnum.URL5714
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));

            listaCategorias = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            EscalafonsControladorEnum.CATEGORIA
                                                            .getValue()));

        }
        catch (SysmanException e) {

            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EscalafonsControladorUrlEnum.URL12675
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaEscalafonCgr
     *
     */
    public void cargarListaEscalafonCgr() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EscalafonsControladorUrlEnum.URL257
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        listaEscalafonCgr = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control SalarioRetroactivo
     * 
     */
    public void cambiarSalarioRetroactivo() {

        double vlrCalculado;
        if (registroSub.getCampos()
                        .get(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                        .getValue()) != "0") {
            vlrCalculado = valorCalculado(registroSub);

            registroSub.getCampos()
                            .put(EscalafonsControladorEnum.VLR_INCREMENTO
                                            .getValue(), vlrCalculado);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control VlrIncremento
     * 
     */
    public void cambiarVlrIncremento() {
        double vlrCalculado;
        if (registroSub.getCampos()
                        .get(EscalafonsControladorEnum.VLR_INCREMENTO
                                        .getValue()) == null) {
            registroSub.getCampos()
                            .put(EscalafonsControladorEnum.VLR_INCREMENTO
                                            .getValue(), "0");
            registroSub.getCampos()
                            .put(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                            .getValue(), "0");
        }
        if ("0".equals(registroSub
                        .getCampos()
                        .get(EscalafonsControladorEnum.VLR_INCREMENTO
                                        .getValue()))) {
            registroSub.getCampos()
                            .put(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                            .getValue(), "0");
        }
        if (!"0".equals(registroSub
                        .getCampos()
                        .get(EscalafonsControladorEnum.VLR_INCREMENTO
                                        .getValue()))) {

            vlrCalculado = valorPorcentaje(registroSub);

            registroSub.getCampos()
                            .put(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                            .getValue(), vlrCalculado);
        }
    }

    public void cambiarSalarioBase() {
        registroSub.getCampos().put("VALORJORNAL", Double.parseDouble(
                        registroSub.getCampos().get("SALARIO_BASE").toString())
            / 30);
    }

    /**
     * Metodo ejecutado al cambiar el control SalarioBase en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarSalarioBaseC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaCategorias.getDatasource().get(rowNum % 10).getCampos()
                        .put("VALORJORNAL", Double.parseDouble(
                                        listaCategorias.getDatasource()
                                                        .get(rowNum % 10)
                                                        .getCampos()
                                                        .get("SALARIO_BASE")
                                                        .toString())
                            / 30);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control SalarioRetroactivo en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarSalarioRetroactivoC(int rowNum) {

        double vlrCalculado;
        if (listaCategorias.getDatasource().get(rowNum % 10).getCampos()
                        .get(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                        .getValue()) != "0") {
            vlrCalculado = valorCalculado(listaCategorias.getDatasource()
                            .get(rowNum % 10));

            listaCategorias.getDatasource().get(rowNum % 10).getCampos()
                            .put(EscalafonsControladorEnum.VLR_INCREMENTO
                                            .getValue(), vlrCalculado);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control VlrIncremento en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarVlrIncrementoC(int rowNum) {

        double vlrCalculado;
        if (listaCategorias.getDatasource().get(rowNum % 10).getCampos()
                        .get(EscalafonsControladorEnum.VLR_INCREMENTO
                                        .getValue()) == null) {
            listaCategorias.getDatasource().get(rowNum % 10).getCampos()
                            .put(EscalafonsControladorEnum.VLR_INCREMENTO
                                            .getValue(), "0");
            listaCategorias.getDatasource().get(rowNum % 10).getCampos()
                            .put(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                            .getValue(), "0");
        }
        if ("0".equals(listaCategorias.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(EscalafonsControladorEnum.VLR_INCREMENTO
                                        .getValue()))) {
            listaCategorias.getDatasource().get(rowNum % 10).getCampos()
                            .put(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                            .getValue(), "0");
        }
        if (!"0".equals(listaCategorias.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(EscalafonsControladorEnum.VLR_INCREMENTO
                                        .getValue()))) {

            vlrCalculado = valorPorcentaje(
                            listaCategorias.getDatasource().get(rowNum % 10));

            listaCategorias.getDatasource().get(rowNum % 10).getCampos()
                            .put(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                            .getValue(), vlrCalculado);
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEscalafonCgr
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEscalafonCgr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CGR_CODIGO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton
     * cmdActualizaSalarioAnterior en la vista
     *
     */
    public void oprimircmdActualizaSalarioAnterior() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EscalafonsControladorUrlEnum.URL10635
                                                        .getValue());
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoNomina);

            Parameter parameter = new Parameter();

            parameter.setFields(param);
            int rta = requestManager.update(urlBean.getUrl(),
                            urlBean.getMetodo(), parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb2520)
                            .replace(cReemplazo, String.valueOf(rta)));
        }
        catch (SystemException ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EscalafonsControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        listaCategorias.load();
        cargarListaCategorias();
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdActualizarRetro en la
     * vista
     *
     */
    public void oprimircmdActualizarRetro() {
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscalafonsControladorUrlEnum.URL10637
                                                            .getValue());
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoNomina);

            Parameter parameter = new Parameter();

            parameter.setFields(param);
            int rta = requestManager.update(urlBean.getUrl(),
                            urlBean.getMetodo(), parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb2520)
                            .replace(cReemplazo, String.valueOf(rta)));
        }
        catch (SystemException ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EscalafonsControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        listaCategorias.load();
        cargarListaCategorias();
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdConsultarEmpleados en
     * la vista
     *
     */
    public void oprimircmdConsultarEmpleados() {
        archivoDescarga = null;
        Map<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        reemplazos.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(),
                        compania);
        reemplazos.put("anioNomina", anoNomina);

        Reporteador.resuelveConsulta(
                        EscalafonsControladorEnum.REPORTE800094.getValue(),
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos, parametros);
        try {
            long contar = service.getConteoConsulta(
                            parametros.get("PR_STRSQL").toString());

            if (contar > 0) {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(
                                parametros.get("PR_STRSQL").toString(),
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97);
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma
                                .getString(EscalafonsControladorEnum.TG_NO_EXISTE
                                                .getValue()));
            }

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ",
                            EscalafonsControladorEnum.REPORTE800094
                                            .getValue()));
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException | SQLException | DRException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnRedondearAlPeso en la
     * vista
     *
     */
    public void oprimirBtnRedondearAlPeso() {
        // <CODIGO_DESARROLLADO>
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscalafonsControladorUrlEnum.URL10640
                                                            .getValue());
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoNomina);
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            int rta = requestManager.update(urlBean.getUrl(),
                            urlBean.getMetodo(), parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb2520)
                            .replace(cReemplazo, String.valueOf(rta)));
        }
        catch (SystemException ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EscalafonsControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        listaCategorias.load();
        cargarListaCategorias();
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnRedondearAlMil en la
     * vista
     *
     */
    public void oprimirBtnRedondearAlMil() {
        // <CODIGO_DESARROLLADO>
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscalafonsControladorUrlEnum.URL607022
                                                            .getValue());
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoNomina);
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            int rta = requestManager.update(urlBean.getUrl(),
                            urlBean.getMetodo(), parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb2520)
                            .replace(cReemplazo, String.valueOf(rta)));
        }
        catch (SystemException ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EscalafonsControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        listaCategorias.load();
        cargarListaCategorias();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Categorias
     */
    public void agregarRegistroSubCategorias() {
        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscalafonsControladorUrlEnum.URL6934
                                                            .getValue());
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(
                            EscalafonsControladorEnum.ESCALAFON.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().remove("VALORJORNAL");

            if (validarReferencia(registroSub)) {
                requestManager.save(urlBean.getUrl(), urlBean.getMetodo(),
                                registroSub.getCampos());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1945"));
            }

            cargarListaCategorias();

        }
        catch (SystemException ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub.getCampos().clear();
        }
    }

    /**
     * Metodo de edicion del formulario Categorias
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubCategorias(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscalafonsControladorUrlEnum.URL8232
                                                            .getValue());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove("RID");
            reg.getCampos().remove("RNUM");
            reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
            reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
            reg.getCampos().remove("VALORJORNAL");
            if (validarReferencia(reg)) {
                requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                                reg.getCampos(), reg.getLlave());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }

        }
        catch (SystemException ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EscalafonsControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        finally {
            cargarListaCategorias();
        }
    }

    public boolean validarReferencia(Registro reg) {
        boolean estado = true;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), reg.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));

            Registro regCant = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EscalafonsControladorUrlEnum.URL5217
                                                                            .getValue())
                                            .getUrl(), param));

            if (regCant != null
                && !(reg.getCampos().get("ESCALAFON").toString()
                    + reg.getCampos().get("ID_DE_CATEGORIA").toString())
                                    .equals(regCant.getCampos().get("CODIGO")
                                                    .toString()
                                        + regCant.getCampos()
                                                        .get("ID_DE_CATEGORIA")
                                                        .toString())
                && (boolean) reg.getCampos().get("CATEGORIA_REFERENCIA")
                && Integer.parseInt(regCant.getCampos()
                                .get(GeneralParameterEnum.CANTIDAD.getName())
                                .toString()) > 0) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4293")
                                .replace("#Id#", regCant.getCampos()
                                                .get("ID_DE_CATEGORIA")
                                                .toString())
                                .replace("#Nombre#", regCant.getCampos()
                                                .get("NOMBRE_CATEGORIA")
                                                .toString())
                                .replace("#Ano#", reg.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                                .toString())
                                .replace("#Codigo#", regCant.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName())
                                                .toString()
                                    + " - " + regCant.getCampos()
                                                    .get(GeneralParameterEnum.NOMBRE
                                                                    .getName())
                                                    .toString())

                );
                estado = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estado;

    }

    /**
     * Metodo de eliminacion del formulario Categorias
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubCategorias(Registro reg) {
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscalafonsControladorUrlEnum.URL10619
                                                            .getValue());
            requestManager.delete(urlBean.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaCategorias();
        }
        catch (SystemException ex) {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EscalafonsControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Categorias
     */
    public void cancelarEdicionCategorias() {
        cargarListaCategorias();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>

    /**
     * calcula el porcentaje
     * 
     * @param registro
     * @return
     */
    public double valorPorcentaje(Registro registro) {
        double inc;

        inc = 1 + (Double
                        .parseDouble(validarCampoCadena(registro
                                        .getCampos(),
                                        EscalafonsControladorEnum.VLR_INCREMENTO
                                                        .getValue()))
            / 100);
        return Double
                        .parseDouble(validarCampoCadena(registro
                                        .getCampos(),
                                        EscalafonsControladorEnum.SALARIOANTERIOR
                                                        .getValue()))
            * inc;

    }

    /**
     * metodo que al cambiar el salario retroactivo calcula
     * 
     * @param registro
     * @return
     */
    private double valorCalculado(Registro registro) {
        return SysmanFunciones.redondear(
                        ((Double.parseDouble(registro.getCampos()
                                        .get(EscalafonsControladorEnum.SALARIO_RETROACTIVO
                                                        .getValue())
                                        .toString())
                            * 100)
                            / Double.parseDouble(
                                            registro
                                                            .getCampos()
                                                            .get(EscalafonsControladorEnum.SALARIOANTERIOR
                                                                            .getValue())
                                                            .toString()))
                            - 100,
                        6);
    }

    /**
     * @param campos
     * @param var
     * @return
     */
    public String validarCampoCadena(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            if ("21010118".equals(SessionUtil.getMenuActual())) {
                visibleNomina = false;
                anoNomina = Integer.toString(SysmanFunciones.ano(new Date()));
            }
            else {
                visibleNomina = true;
                anoNomina = SessionUtil.getSessionVar("anioNomina").toString();
            }

            visibleJornal = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania,
                            "CALCULAR NOMINA POR JORNALES",
                            SessionUtil.getModulo(), new Date(), true));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        precargarRegistro();
        listaCategorias = null;
        cargarListaCategorias();
        listaCategorias.load();

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
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

        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
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
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable visibleNomina
     * 
     * @return visibleNomina
     */
    public boolean isVisibleNomina() {
        return visibleNomina;
    }

    /**
     * Asigna la variable visibleNomina
     * 
     * @param visibleNomina
     * Variable a asignar en visibleNomina
     */
    public void setVisibleNomina(boolean visibleNomina) {
        this.visibleNomina = visibleNomina;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaEscalafonCgr
     * 
     * @return listaEscalafonCgr
     */
    public RegistroDataModelImpl getListaEscalafonCgr() {
        return listaEscalafonCgr;
    }

    /**
     * Asigna la lista listaEscalafonCgr
     * 
     * @param listaEscalafonCgr
     * Variable a asignar en listaEscalafonCgr
     */
    public void setListaEscalafonCgr(RegistroDataModelImpl listaEscalafonCgr) {
        this.listaEscalafonCgr = listaEscalafonCgr;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaCategorias
     * 
     * @return listaCategorias
     */
    public RegistroDataModelImpl getListaCategorias() {
        return listaCategorias;
    }

    /**
     * Asigna la lista listaCategorias
     * 
     * @param listaCategorias
     * Variable a asignar en listaCategorias
     */
    public void setListaCategorias(RegistroDataModelImpl listaCategorias) {
        this.listaCategorias = listaCategorias;
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
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public boolean isVisibleJornal() {
        return visibleJornal;
    }

    public void setVisibleJornal(boolean visibleJornal) {
        this.visibleJornal = visibleJornal;
    }

    // </SET_GET_ADICIONALES>

}
