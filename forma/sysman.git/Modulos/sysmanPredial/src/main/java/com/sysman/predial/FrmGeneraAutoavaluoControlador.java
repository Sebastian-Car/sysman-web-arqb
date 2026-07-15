package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.FrmGeneraAutoavaluoControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 * Genera declaraci&oacute;n de autoaval&uacute;o.
 *
 * @author dmaldonado
 * @version 1, 07/06/2016 15:51:19
 * 
 * @author jrodriguezr
 * @version 2, 13/06/2017 Se refactoriza el c&oacute;digo: Se pasa el
 * numero del formulario al enumerado, se eliminan conexiones y se
 * ajustan metodos de generacion de reportes.
 * 
 * @author jrodrigueza
 * @version 3, 21/08/2019 Proceso de refactoring WSO2-DSS.
 */
@ManagedBean
@ViewScoped
public class FrmGeneraAutoavaluoControlador extends BeanBaseDatosAcmeImpl {

    // <DECLARAR_ATRIBUTOS>
    /**
     * Codigo de la compa&ntilde;&iacute;a con lo que se inici&oacute;
     * la sesi&oacute;n.
     */
    private final String compania;
    /**
     * C&oacute;digo del m&oacute;dulo al que pertenece el formulario.
     */
    private final String modulo;
    /** C&oacute;digo del usuario de predial. */
    private String codigoPredio;
    private String nombre;
    private String declaracion;
    private StreamedContent archivoDescarga;
    private String auxiliar;
    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbCodigo;
    private RegistroDataModelImpl listaTrpCod;
    private RegistroDataModelImpl listaTrpCodE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModelImpl listaSubautoavaluo;
    // </DECLARAR_LISTAS_SUBFORM>

    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>

    // <DECLARAR_ADICIONALES>
    private Registro registroSub;
    private boolean bloqueaCodigo;
    private boolean bloqueaDeclaracion;
    private boolean bloqueaAvaluo;
    private boolean bloqueaTrpCod;
    private int indiceSubautoavaluo;
    private String nombreTarifa;
    private String mensajeFormulario;
    private int anoImpresion;
    private BigDecimal avaluoAno;
    private String trpcodAnt;
    private boolean visibleDgAnular;
    private String docNum;
    // </DECLARAR_ADICIONALES>

    /**
     * Para emplear funciones/procedimientos del paquete SysmanUtil.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Para emplear funciones/procedimientos del paquete PCK_PREDIAL
     */
    private EjbPredialCeroRemote ejbPredialCero;

    /**
     * Para emplear funciones/procedimientos del paquete
     * PCK_PREDIAL_COM2
     */
    private EjbPredialDosRemote ejbPredialDos;

    public FrmGeneraAutoavaluoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        numFormulario = GeneralCodigoFormaEnum.FRM_GENERA_AUTOAVALUO_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        asignarOrigenDatos();
        iniciarListas();
        iniciarListasSub();
    }

    @Override
    public void asignarOrigenDatos() {
        // El formulario no tiene origen de datos
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbCodigo();
        cargarListaTrpCod();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubautoavaluo();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubautoavaluo = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    public void activarEdicionSubautoavaluo(Registro reg) {
        indiceSubautoavaluo = reg.getIndice();
        registroSub = new Registro(new HashMap<>(reg.getCampos()));
        cargarListaTrpCod();
        cargarListaTrpCodE();
        trpcodAnt = (String) reg.getCampos()
                        .get(GeneralParameterEnum.TRPCOD.getName());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga combo grande que trae los datos del predio.
     */
    public void cargarListaCmbCodigo() {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        String urlEnumId = FrmGeneraAutoavaluoControladorUrlEnum.DSS_367210
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        String url = urlBean.getUrl();
        String urlConteo = urlBean.getUrlConteo().getUrl();
        String rowKey = GeneralParameterEnum.CODIGO.getName();
        boolean vacio = true;
        listaCmbCodigo = new RegistroDataModelImpl(url, urlConteo, params,
                        vacio, rowKey);
    }

    /**
     * Origen de datos para el subformulario.
     */
    public void cargarListaSubautoavaluo() {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        params.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);

        String urlEnumId = FrmGeneraAutoavaluoControladorUrlEnum.DSS_385035
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        String url = urlBean.getUrl();
        String urlConteo = urlBean.getUrlConteo().getUrl();
        String rowKey = GeneralParameterEnum.PREANO.getName();
        listaSubautoavaluo = new RegistroDataModelImpl(url, urlConteo, params,
                        rowKey);
    }

    /**
     * Construye la estructura para un combo grande con las tarifas y
     * sus datos respectivos.
     * 
     * @param preano
     * a&ntilde;o del predio
     * @return registro data model nueva versi&oacute;n
     */
    private RegistroDataModelImpl getListaTarifasConRango(Object preano) {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.PREANO.getName(), preano);

        String urlEnumId = FrmGeneraAutoavaluoControladorUrlEnum.DSS_376016
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        String url = urlBean.getUrl();
        String urlConteo = urlBean.getUrlConteo().getUrl();
        String rowKey = GeneralParameterEnum.TRPCOD.getName();
        return new RegistroDataModelImpl(url, urlConteo, params, rowKey);
    }

    /**
     * Carga de combo grande de tarifas con rango, para el modal de
     * creaci&oacute;n de registro.
     */
    public void cargarListaTrpCod() {
        Object preano = registroSub.getCampos()
                        .get(GeneralParameterEnum.PREANO.getName());

        listaTrpCod = getListaTarifasConRango(preano);
    }

    /**
     * Carga de combo grande de tarifas con rango, para el
     * subformulario.
     */
    public void cargarListaTrpCodE() {
        Object preano = listaSubautoavaluo.getDatasource()
                        .get(indiceSubautoavaluo).getCampos()
                        .get(GeneralParameterEnum.PREANO.getName());
        listaTrpCod = getListaTarifasConRango(preano);
    }
    // </METODOS_CARGAR_LISTA>

    // <METODOS_CAMBIAR>
    public void cambiarCmbCodigo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtDeclaracion() {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.nvlStr(declaracion, "").isEmpty()) {
            bloqueaAvaluo = false;
            bloqueaTrpCod = false;
        }
        else {
            bloqueaAvaluo = true;
            bloqueaTrpCod = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTrpCodC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaSubautoavaluo.getDatasource().get(rowNum).getCampos()
                        .put(GeneralParameterEnum.TRPDES.getName(),
                                        nombreTarifa);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPreano() {
        // <CODIGO_DESARROLLADO>
        registroSub.getCampos().put(GeneralParameterEnum.TRPCOD.getName(),
                        null);
        registroSub.getCampos().put(GeneralParameterEnum.TRPDES.getName(),
                        null);
        cargarListaTrpCod();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPreanoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaSubautoavaluo.getDatasource().get(rowNum).getCampos()
                        .put(GeneralParameterEnum.TRPCOD.getName(), null);
        listaSubautoavaluo.getDatasource().get(rowNum).getCampos()
                        .put(GeneralParameterEnum.TRPDES.getName(), null);
        cargarListaTrpCodE();
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    /**
     * Acciones ejecutadas al seleccionar una fila del combo grande de
     * predios.
     * 
     * @param event
     */
    public void seleccionarFilaCmbCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoPredio = SysmanFunciones.toString(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        avaluoAno = new BigDecimal(SysmanFunciones.toString(registroAux
                        .getCampos()
                        .get(GeneralParameterEnum.AVALUO_ANO.getName())));
        if (SysmanFunciones.validarVariableVacio(codigoPredio)) {
            return;
        }
        nombre = SysmanFunciones.toString(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

        String parametro = consultarParametro(
                        "GENERA CONSECUTIVO DE RADICADO AUTOAVALUO");
        parametro = SysmanFunciones.nvlStr(parametro, "NO");

        if ("NO".equals(parametro)) {
            if (SysmanFunciones.nvlStr(declaracion, "").isEmpty()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB736"));
                bloqueaAvaluo = true;
                bloqueaTrpCod = true;
            }
            else {
                bloqueaAvaluo = false;
                bloqueaTrpCod = false;
            }
        }
        cargarListaSubautoavaluo();
    }

    /**
     * Acciones ejecutadas al seleccionar una fila del combo grande de
     * tarifas.
     * 
     * @param event
     */
    public void seleccionarFilaTrpCod(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.TRPCOD.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.TRPCOD.getName()));
        registroSub.getCampos().put(GeneralParameterEnum.TRPDES.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.TRPDES.getName()));
    }

    public void seleccionarFilaTrpCodE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.TRPCOD.getName());
        nombreTarifa = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.TRPDES.getName());
    }
    // </METODOS_COMBOS_GRANDES>

    // <METODOS_BOTONES>
    public void oprimirImprimir(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        anoImpresion = Integer.parseInt(SysmanFunciones.toString(reg.getCampos()
                        .get(GeneralParameterEnum.PREANO.getName())));
        ejecutarrcAvaluo();

        BigDecimal avaluo = new BigDecimal(SysmanFunciones.toString(reg
                        .getCampos()
                        .get(GeneralParameterEnum.AVALUO.getName())));
        try {
            ejbPredialDos.incrementarAvaluos(compania, codigoPredio,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL, avaluo,
                            avaluoAno, anoImpresion);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        String urlEnumId = FrmGeneraAutoavaluoControladorUrlEnum.DSS_385034
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        String url = urlBean.getUrl();

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        params.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        params.put(GeneralParameterEnum.ANO.getName(), anoImpresion);

        List<Parameter> registros = new ArrayList<>();
        try {
            registros = requestManager.getList(url, params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (!registros.isEmpty()) {
            generaInforme(FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2811").replace(
                            "s$anoImpresion$s", String.valueOf(anoImpresion)));
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_BOTONES>

    // <METODOS_SUBFORM>
    public void agregarRegistroSubSubautoavaluo() {
        // no se permite crear registros
    }

    public void editarRegSubSubautoavaluo(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        reg.getCampos().remove("RNUM");
        reg.getCampos().remove(GeneralParameterEnum.TRPDES.getName());
        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());

        Map<String, Object> criterio = new HashMap<>();
        criterio.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        criterio.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        criterio.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        // PREANO debe existir en los campos del registro
        reg.getCampos().putAll(criterio);

        String urlEnumId = FrmGeneraAutoavaluoControladorUrlEnum.DSS_385037
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        String url = urlBean.getUrl();
        String serviceName = urlBean.getMetodo();

        try {
            actualizarAntesSub(reg);
            requestManager.update(url, serviceName, reg.getCampos(),
                            reg.getLlave());
            despuesActualizarSub(reg);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
            cargarListaSubautoavaluo();
        }
    }

    /**
     * Acciones realizadas antes de actualizar un dato en el
     * subformulario.
     * 
     * @param reg
     * registro con los campos requeridos
     * @throws SystemException
     */
    private void actualizarAntesSub(Registro reg) throws SystemException {
        Map<String, Object> campos = reg.getCampos();

        int vigencia = Integer.parseInt(SysmanFunciones.toString(
                        campos.get(GeneralParameterEnum.PREANO.getName())));

        /*
         * TODO: El tipo de dato del retorno debería ser una cadena,
         * no se han realizado pruebas
         */
        long rta = ejbPredialCero.consultarAbonoEnRecibosDePago(compania,
                        codigoPredio, vigencia);
        docNum = String.valueOf(rta);

        switch (docNum) {
        case "-1":
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1206"));
            break;
        case "-2":
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1207"));
            break;
        default:
            visibleDgAnular = true;
            break;
        }

        verificarUsuarioAlDia();
    }

    /**
     * Verifica si el predio se encuentra al d&iacute;a para la
     * vigencia seleccionada.
     * 
     * @throws SystemException
     */
    private void verificarUsuarioAlDia() throws SystemException {
        String urlEnumId = FrmGeneraAutoavaluoControladorUrlEnum.DSS_385034
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        String url = urlBean.getUrl();

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        params.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        Parameter rs = requestManager.get(url, params);
        Map<String, Object> map = rs.getFields();
        Object var = map.get(GeneralParameterEnum.ANOS_FACTURADOS.getName());

        int anosFacturados = Integer.parseInt(SysmanFunciones.toString(var));

        if (anosFacturados > 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1208"));
        }
    }

    /**
     * Acciones realizadas despu&eacute;s de actualizar un dato en el
     * subformulario.
     * 
     * @param reg
     * registro con los campos requeridos
     * @throws SystemException
     */
    private void despuesActualizarSub(Registro reg) throws SystemException {
        Map<String, Object> campos = reg.getCampos();

        String vigencia = SysmanFunciones.toString(
                        campos.get(GeneralParameterEnum.PREANO.getName()));
        BigDecimal avaluoIgag = avaluoAno;
        BigDecimal avaluo = new BigDecimal(SysmanFunciones.toString(
                        campos.get(GeneralParameterEnum.AVALUO.getName())));
        String trpcod = SysmanFunciones.toString(
                        campos.get(GeneralParameterEnum.TRPCOD.getName()));
        int preano = Integer.parseInt(vigencia);
        int ano = preano;
        String consecutivorad = declaracion;
        String usuario = SessionUtil.getUser().getCodigo();
        String rta = ejbPredialCero.actualizarAvaluo(compania, codigoPredio,
                        vigencia,
                        Integer.parseInt(modulo), avaluoIgag, avaluo, trpcodAnt,
                        trpcod, preano, ano, consecutivorad, usuario);

        if (!"0".equals(rta)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1209"));
            declaracion = rta;

            String modcco = "022";
            String descripcion = "Cambio de Avalúo vigencia";
            String vanmod = avaluo.toString();
            String vnumod = avaluo.toString();
            Date fechaHoy = new Date();
            String numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;

            ejbPredialCero.insertarCambiosEnAuditoria(compania, codigoPredio,
                            usuario,
                            modcco, vanmod, vnumod, descripcion, fechaHoy,
                            fechaHoy, numeroOrden);
        }
    }

    public void eliminarRegSubSubautoavaluo(Registro reg) {
        // no se permite eliminar registros
    }

    public void cancelarEdicionSubautoavaluo() {
        /*
         * Comentario porque la lista se estaba borrando al momentode
         * cancelar la edicion.
         */
    }
    // </METODOS_SUBFORM>

    // <METODOS_ADICIONALES>
    public void ejecutarrcAvaluo() {
        // <CODIGO_DESARROLLADO>
        String mensaje = idioma.getString("TB_TB2843");
        mensaje = mensaje.replace("s$preAno$s", String.valueOf(anoImpresion));
        JsfUtil.agregarMensajeInformativo(mensaje);
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarDgAnular() {
        String urlEnumId = FrmGeneraAutoavaluoControladorUrlEnum.DSS_374038
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        String url = urlBean.getUrl();
        String serviceName = urlBean.getMetodo();

        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put(GeneralParameterEnum.DOCNUM.getName(), docNum);
        fields.put(GeneralParameterEnum.PRECOD.getName(), codigoPredio);
        Parameter params = new Parameter();
        try {
            requestManager.update(url, serviceName, params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        visibleDgAnular = false;
    }

    public void cancelarDgAnular() {
        JsfUtil.agregarMensajeError(idioma.getString("TB_TB1229") + docNum);
        visibleDgAnular = false;
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        declaracion = "";

        String parametro = consultarParametro("MANEJA AUTOAVALUO");
        parametro = SysmanFunciones.nvlStr(parametro, "NO");
        if ("NO".equals(parametro)) {
            mensajeFormulario = idioma.getString("TB_TB741");
            bloqueaCodigo = true;
            bloqueaDeclaracion = true;
        }
        else {
            mensajeFormulario = idioma.getString("TB_TB742");
            bloqueaCodigo = false;
            bloqueaDeclaracion = false;
        }

        parametro = consultarParametro(
                        "GENERA CONSECUTIVO DE RADICADO AUTOAVALUO");
        if ("NO".equals(parametro)) {
            bloqueaDeclaracion = false;
            bloqueaTrpCod = true;
            bloqueaAvaluo = true;
        }
        else {
            bloqueaDeclaracion = true;
            bloqueaTrpCod = false;
            bloqueaAvaluo = false;
            mensajeFormulario = "";
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        declaracion = (String) registro.getCampos().get("CONSECT");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Generaci&oacute;n de informe de autoaval&uacute;o.
     * 
     * @param formato
     * un formato v&aacute;lido para generar el reporte.
     */
    private void generaInforme(FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("numeroOrden", SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        reemplazar.put("ano", anoImpresion);
        reemplazar.put("codigoCons", codigoPredio);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("PR_ANOAVALUO", anoImpresion);
        try {
            Reporteador.resuelveConsulta("000856INFAUTOAVALUO",
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("000856INFAUTOAVALUO",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(compania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public String getCodigo() {
        return codigoPredio;
    }

    public void setCodigo(String codigo) {
        this.codigoPredio = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDeclaracion() {
        return declaracion;
    }

    public void setDeclaracion(String declaracion) {
        this.declaracion = declaracion;
    }

    public int getIndiceSubautoavaluo() {
        return indiceSubautoavaluo;
    }

    public void setIndiceSubautoavaluo(int indiceSubautoavaluo) {
        this.indiceSubautoavaluo = indiceSubautoavaluo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * @return the listaCmbCodigo
     */
    public RegistroDataModelImpl getListaCmbCodigo() {
        return listaCmbCodigo;
    }

    /**
     * @param listaCmbCodigo
     * the listaCmbCodigo to set
     */
    public void setListaCmbCodigo(RegistroDataModelImpl listaCmbCodigo) {
        this.listaCmbCodigo = listaCmbCodigo;
    }

    /**
     * @return the listaTrpCod
     */
    public RegistroDataModelImpl getListaTrpCod() {
        return listaTrpCod;
    }

    /**
     * @param listaTrpCod
     * the listaTrpCod to set
     */
    public void setListaTrpCod(RegistroDataModelImpl listaTrpCod) {
        this.listaTrpCod = listaTrpCod;
    }

    /**
     * @return the listaTrpCodE
     */
    public RegistroDataModelImpl getListaTrpCodE() {
        return listaTrpCodE;
    }

    /**
     * @param listaTrpCodE
     * the listaTrpCodE to set
     */
    public void setListaTrpCodE(RegistroDataModelImpl listaTrpCodE) {
        this.listaTrpCodE = listaTrpCodE;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    // <SET_GET_LISTAS_SUBFORM>
    /**
     * @return the listaSubautoavaluo
     */
    public RegistroDataModelImpl getListaSubautoavaluo() {
        return listaSubautoavaluo;
    }

    /**
     * @param listaSubautoavaluo
     * the listaSubautoavaluo to set
     */
    public void setListaSubautoavaluo(
        RegistroDataModelImpl listaSubautoavaluo) {
        this.listaSubautoavaluo = listaSubautoavaluo;
    }
    // </SET_GET_LISTAS_SUBFORM>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_ADICIONALES>
    /**
     * @return the bloqueaCodigo
     */
    public boolean isBloqueaCodigo() {
        return bloqueaCodigo;
    }

    /**
     * @param bloqueaCodigo
     * the bloqueaCodigo to set
     */
    public void setBloqueaCodigo(boolean bloqueaCodigo) {
        this.bloqueaCodigo = bloqueaCodigo;
    }

    /**
     * @return the bloqueaDeclaracion
     */
    public boolean isBloqueaDeclaracion() {
        return bloqueaDeclaracion;
    }

    /**
     * @param bloqueaDeclaracion
     * the bloqueaDeclaracion to set
     */
    public void setBloqueaDeclaracion(boolean bloqueaDeclaracion) {
        this.bloqueaDeclaracion = bloqueaDeclaracion;
    }

    /**
     * @return the bloqueaAvaluo
     */
    public boolean isBloqueaAvaluo() {
        return bloqueaAvaluo;
    }

    /**
     * @param bloqueaAvaluo
     * the bloqueaAvaluo to set
     */
    public void setBloqueaAvaluo(boolean bloqueaAvaluo) {
        this.bloqueaAvaluo = bloqueaAvaluo;
    }

    /**
     * @return the bloqueaTrpCod
     */
    public boolean isBloqueaTrpCod() {
        return bloqueaTrpCod;
    }

    /**
     * @param bloqueaTrpCod
     * the bloqueaTrpCod to set
     */
    public void setBloqueaTrpCod(boolean bloqueaTrpCod) {
        this.bloqueaTrpCod = bloqueaTrpCod;
    }

    /**
     * @return the nombreTarifa
     */
    public String getNombreTarifa() {
        return nombreTarifa;
    }

    /**
     * @param nombreTarifa
     * the nombreTarifa to set
     */
    public void setNombreTarifa(String nombreTarifa) {
        this.nombreTarifa = nombreTarifa;
    }

    /**
     * @return the mensajeFormulario
     */
    public String getMensajeFormulario() {
        return mensajeFormulario;
    }

    /**
     * @param mensajeFormulario
     * the mensajeFormulario to set
     */
    public void setMensajeFormulario(String mensajeFormulario) {
        this.mensajeFormulario = mensajeFormulario;
    }

    /**
     * @return the anoImpresion
     */
    public int getAnoImpresion() {
        return anoImpresion;
    }

    /**
     * @param anoImpresion
     * the anoImpresion to set
     */
    public void setAnoImpresion(int anoImpresion) {
        this.anoImpresion = anoImpresion;
    }

    /**
     * @return the avaluoAno
     */
    public BigDecimal getAvaluoAno() {
        return avaluoAno;
    }

    /**
     * @param avaluoAno
     * the avaluoAno to set
     */
    public void setAvaluoAno(BigDecimal avaluoAno) {
        this.avaluoAno = avaluoAno;
    }

    /**
     * @return the trpcodAnt
     */
    public String getTrpcodAnt() {
        return trpcodAnt;
    }

    /**
     * @param trpcodAnt
     * the trpcodAnt to set
     */
    public void setTrpcodAnt(String trpcodAnt) {
        this.trpcodAnt = trpcodAnt;
    }

    /**
     * @return the visibleDgAnular
     */
    public boolean isVisibleDgAnular() {
        return visibleDgAnular;
    }

    /**
     * @param visibleDgAnular
     * the visibleDgAnular to set
     */
    public void setVisibleDgAnular(boolean visibleDgAnular) {
        this.visibleDgAnular = visibleDgAnular;
    }

    /**
     * @return the docNum
     */
    public String getDocNum() {
        return docNum;
    }

    /**
     * @param docNum
     * the docNum to set
     */
    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * @return the modulo
     */
    public String getModulo() {
        return modulo;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @param registroSub
     * the registroSub to set
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>

    /**
     * Realiza la consulta del valor asignado al parametro de la base
     * de datos.
     * 
     * @return valor del par&aacute;metro
     */
    private String consultarParametro(String nombre) {
        Date fecha = new Date();
        boolean indMayus = true;
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
                            fecha, indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }
    }

}
