package com.sysman.almacen;

import com.sysman.almacen.enums.GenerarStickerControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 29/01/2016
 * @modified jguerrero
 * @version 2. 12/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 * 
 * @author jmalaver version 4, 10/05/2017. Se ajustan las validaciones
 * para la generaci�n de reportes y se agregan los m�todos necesarios
 * para generar un reporte adicional.
 */
@ManagedBean
@ViewScoped

public class GenerarStickerControlador extends BeanBaseModal {

    private final String compania;
    private String elementoDesde;
    private String elementoHasta;
    private String placaInicial;
    private String placaFinal;
    private String dependenciaInicial;
    private String dependenciaFinal;
    private String responsableInicial;
    private String responsableFinal;
    private String tipoMovimiento;
    private String numeroMovimiento;
    private String nombreElementoDesde;
    private String nombreElementoHasta;
    private String seleccionado;
    private boolean elemento;
    private boolean placa;
    private boolean dependencia;
    private boolean responsable;
    private boolean movimiento;
    private String modeloCodigoBarrasAne;
    private StreamedContent archivoDescarga;
    private static final String CEDULA = "CEDULA";
    private static final String CODIGO = "CODIGO";
    private static final String CTEELEMENTO = "ELEMENTO";
    private static final String MSM_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    private static final String PR_NOMBRECOMPANIA = "PR_NOMBRECOMPANIA";
    private static final String PR_SIGLACOMPANIA = "PR_SIGLACOMPANIA";
    private static final String PR_STRSQL = "PR_STRSQL";
    private static final String SERIE = "SERIE";
    private static final String TB_TB1890 = "TB_TB1890";

    private List<Registro> listacmbtipomovimiento;
    private List<Registro> listacmbnumeromovimiento;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private RegistroDataModelImpl listacmbplacainicial;
    private RegistroDataModelImpl listacmbplacafinal;
    private RegistroDataModelImpl listacmbdependenciainicial;
    private RegistroDataModelImpl listacmbdependenciafinal;
    private RegistroDataModelImpl listacmbresponsableinicial;
    private RegistroDataModelImpl listacmbresponsablefinal;

    private boolean muestraTipoSticker;
    private boolean muestraTipoStickerExcel;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of GenerarStickerControlador
     */
    public GenerarStickerControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.GENERAR_STICKER_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(GenerarStickerControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbdependenciainicial();
        cargarListacmbresponsableinicial();
        cargarListacmbtipomovimiento();
        cargarListacmbnumeromovimiento();
        cargarListacmbElementoDesde();
        cargarListacmbplacainicial();
        abrirFormulario();
    }

    public void cargarListacmbdependenciainicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarStickerControladorUrlEnum.URL4372
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbdependenciainicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListacmbdependenciafinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarStickerControladorUrlEnum.URL4980
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependenciaInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbdependenciafinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);

        // 62019 DEPENDENCIA
    }

    public void cargarListacmbtipomovimiento() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listacmbtipomovimiento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarStickerControladorUrlEnum.URL5492
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 41008
    }

    public void cargarListacmbnumeromovimiento() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.MOVIMIENTO.getName(),
                        tipoMovimiento);

        try {
            listacmbnumeromovimiento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarStickerControladorUrlEnum.URL54922
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 41009

    }

    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarStickerControladorUrlEnum.URL54923
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CTEELEMENTO);

        // 141029
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarStickerControladorUrlEnum.URL54924
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ELEMENTOINICIAL", elementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CTEELEMENTO);

        // 141031
    }

    public void cargarListacmbplacainicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarStickerControladorUrlEnum.URL54925
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbplacainicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SERIE);

        // 1410035
    }

    public void cargarListacmbplacafinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarStickerControladorUrlEnum.URL54926
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("PLACAINICIAL", placaInicial);

        listacmbplacafinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SERIE);
        // 141037
    }

    public void cargarListacmbresponsableinicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarStickerControladorUrlEnum.URL7958
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CTEELEMENTO, compania);

        listacmbresponsableinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CEDULA);
        // 61012
    }

    public void cargarListacmbresponsablefinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarStickerControladorUrlEnum.URL7075
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TERCEROINICIAL", responsableInicial);

        listacmbresponsablefinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CEDULA);
        // 61015
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnExcelImpresora en la
     * vista
     *
     * Genera una hoja de datos que incluye la informacion de los
     * elementos de acuerdo a los filtro seleccionados
     *
     */
    public void oprimirBtnExcelImpresora() {
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = null;
            HashMap<String, Object> reemplazar = new HashMap<>();

            if (seleccionado == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(TB_TB1890));
                return;
            }
            if (validarYCrearConsulta(reemplazar)) {
                return;
            }

            String sql = Reporteador.resuelveConsulta(
                            "800190StickerImpresora",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL,
                            idioma.getString("TB_TB4186"));

        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> reemplazar = new HashMap<>();

            if (seleccionado == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(TB_TB1890));
                return;
            }
            if (validarYCrearConsulta(reemplazar)) {
                return;
            }

            // MANEJO DE PARAMETROS DEL REPORTE
            String modeloAlmacenSoloCodigo = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MODELO STICKER ALMACEN SOLO CODIGO DE BARRAS",
                            SessionUtil.getModulo(),
                            new Date(), true);

            modeloAlmacenSoloCodigo = modeloAlmacenSoloCodigo == null ? "NO"
                : modeloAlmacenSoloCodigo;
            if ("NO".equals(modeloAlmacenSoloCodigo)) {
                String parametro = ejbSysmanUtil.consultarParametro(compania,
                                "MODELO STICKER ALMACEN",
                                SessionUtil.getModulo(),
                                new Date(), false);

                Map<String, Object> parametros = new HashMap<>();
                String strSql = Reporteador.resuelveConsulta(parametro,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar);
                
                parametros.put(PR_NOMBRECOMPANIA,SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(
                        compania,
                        "NOMBRE PARA MOSTRAR EN EL ENCABEZADO EN STICKERS DE ALMACEN",
                        SessionUtil.getModulo(),
                        new Date(), false),SessionUtil.getCompaniaIngreso().getNombre())); // mod JM CC3330
                
                parametros.put(PR_SIGLACOMPANIA,
                                SessionUtil.getCompaniaIngreso().getSigla());
                parametros.put(PR_STRSQL, strSql);
                archivoDescarga = JsfUtil.exportarStreamed(parametro,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            else {
                formatoReporte(formato);

            }
        }
        catch (JRException | IOException | SystemException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA) + " "
                                + ex.getMessage());
            Logger.getLogger(GenerarStickerControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void formatoReporte(ReportesBean.FORMATOS formato) {
        if (ReportesBean.FORMATOS.PDF.equals(formato)) {
            muestraTipoSticker = true;
        }
        else {
            muestraTipoStickerExcel = true;
        }
    }

    private boolean validarYCrearConsulta(HashMap<String, Object> reemplazar) {
        boolean retornoCierre = false;
        String condicion;
        String orden;
        String variableInner = "";
        if ("1".equals(seleccionado)) {
            if (validarCampoVacios(placaInicial, placaFinal)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1891"));
                retornoCierre = true;
            }
            condicion = "DEVOLUTIVO.SERIE BETWEEN " + placaInicial + "\n"
                + "                       AND " + placaFinal + "";
            orden = " DEVOLUTIVO.SERIE, \n"
                + " DEVOLUTIVO.ELEMENTO ";
            reemplazarValores(reemplazar, condicion, orden, variableInner);
        }
        else if ("2".equals(seleccionado)) {
            if (validarCampoVacios(elementoDesde, elementoHasta)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1892"));
                retornoCierre = true;
            }
            condicion = "DEVOLUTIVO.ELEMENTO BETWEEN " + elementoDesde
                + "\n"
                + "                          AND " + elementoHasta + "";
            orden = " DEVOLUTIVO.ELEMENTO";
            reemplazarValores(reemplazar, condicion, orden, variableInner);
        }
        else if ("3".equals(seleccionado)) {
            if (validarCampoVacios(dependenciaInicial, dependenciaFinal)

            ) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1893"));
                retornoCierre = true;
            }
            condicion = " DEVOLUTIVO.DEPENDENCIA   BETWEEN '"
                + dependenciaInicial + "' \n"
                + "                                AND '" + dependenciaFinal
                + "'";
            orden = " DEVOLUTIVO.DEPENDENCIA, \n"
                + "          DEVOLUTIVO.SERIE";
            reemplazarValores(reemplazar, condicion, orden, variableInner);
        }
        else if ("4".equals(seleccionado)) {
            if (validarCampoVacios(responsableInicial, responsableFinal)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1894"));
                retornoCierre = true;
            }
            condicion = " DEVOLUTIVO.RESPONSABLE BETWEEN '"
                + responsableInicial + "' \n"
                + "                              AND '" + responsableFinal
                + "'";
            orden = " DEVOLUTIVO.RESPONSABLE, \n"
                + "          DEVOLUTIVO.SERIE";
            reemplazarValores(reemplazar, condicion, orden, variableInner);
        }
        else {
            if (validarCampoVacios(tipoMovimiento, numeroMovimiento)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1895"));
                retornoCierre = true;
            }
            condicion = "  D_MOVIMIENTO.TIPOMOVIMIENTO = '" + tipoMovimiento
                + "' \n"
                + "   AND D_MOVIMIENTO.MOVIMIENTO     = " + numeroMovimiento
                + "  ";
            orden = " DEVOLUTIVO.SERIE";
            variableInner = " INNER JOIN D_MOVIMIENTO  \n"
                + "   ON  DEVOLUTIVO.COMPANIA = D_MOVIMIENTO.COMPANIA \n"
                + "   AND DEVOLUTIVO.SERIE          = D_MOVIMIENTO.SERIE ";

            // + " INNER JOIN V_MOVIMIENTO MOVIMIENTO \n"
            // + " ON D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA \n"
            // + " AND D_MOVIMIENTO.TIPOMOVIMIENTO =
            // MOVIMIENTO.TIPOMOVIMIENTO \n"
            // + " AND D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO ";
            reemplazarValores(reemplazar, condicion, orden, variableInner);
        }

        return retornoCierre;
    }

    public void reemplazarValores(Map<String, Object> reemplazar,
        Object condicion, Object orden, Object variableInner) {
        reemplazar.put("condicion", condicion);
        reemplazar.put("orden", orden);
        reemplazar.put("variableInner", variableInner);
    }

    public boolean validarCampoVacios(String valor1, String valor2) {
        return SysmanFunciones.validarVariableVacio(valor1)
            || SysmanFunciones.validarVariableVacio(valor2);
    }

    public void cambiarcmbseleccionarpor() {
        // <CODIGO_DESARROLLADO>
        if ("1".equals(seleccionado)) {
            placa = true;
            elemento = false;
            elementoDesde = null;
            elementoHasta = null;
            nombreElementoDesde = null;
            nombreElementoHasta = null;
            dependencia = false;
            dependenciaInicial = null;
            dependenciaFinal = null;
            responsable = false;
            responsableInicial = null;
            responsableFinal = null;
            movimiento = false;
            tipoMovimiento = null;
            numeroMovimiento = null;
        }
        else if ("2".equals(seleccionado)) {
            placa = false;
            placaInicial = null;
            placaFinal = null;
            elemento = true;
            dependencia = false;
            dependenciaInicial = null;
            dependenciaFinal = null;
            responsable = false;
            responsableInicial = null;
            responsableFinal = null;
            movimiento = false;
            tipoMovimiento = null;
            numeroMovimiento = null;
        }
        else if ("3".equals(seleccionado)) {
            placa = false;
            placaInicial = null;
            placaFinal = null;
            elemento = false;
            elementoDesde = null;
            elementoHasta = null;
            nombreElementoDesde = null;
            nombreElementoHasta = null;
            dependencia = true;
            responsable = false;
            responsableInicial = null;
            responsableFinal = null;
            movimiento = false;
            tipoMovimiento = null;
            numeroMovimiento = null;
        }
        else if ("4".equals(seleccionado)) {
            placa = false;
            placaInicial = null;
            placaFinal = null;
            elemento = false;
            elementoDesde = null;
            elementoHasta = null;
            nombreElementoDesde = null;
            nombreElementoHasta = null;
            dependencia = false;
            dependenciaInicial = null;
            dependenciaFinal = null;
            responsable = true;
            movimiento = false;
            tipoMovimiento = null;
            numeroMovimiento = null;
        }
        else {
            placa = false;
            placaInicial = null;
            placaFinal = null;
            elemento = false;
            elementoDesde = null;
            elementoHasta = null;
            nombreElementoDesde = null;
            nombreElementoHasta = null;
            dependencia = false;
            dependenciaInicial = null;
            dependenciaFinal = null;
            responsable = false;
            responsableInicial = null;
            responsableFinal = null;
            movimiento = true;
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbtipomovimiento() {
        // <CODIGO_DESARROLLADO>
        numeroMovimiento = null;
        cargarListacmbnumeromovimiento();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = registroAux.getCampos().get(CTEELEMENTO).toString();
        nombreElementoDesde = registroAux.getCampos()
                        .get("DESCRIPCION").toString();
        nombreElementoHasta = null;
        elementoHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = registroAux.getCampos().get(CTEELEMENTO).toString();
        nombreElementoHasta = registroAux.getCampos()
                        .get("DESCRIPCION").toString();
    }

    public void seleccionarFilacmbplacainicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        placaInicial = (registroAux.getCampos().get(SERIE))
                        .toString();
        placaFinal = null;
        cargarListacmbplacafinal();
    }

    public void seleccionarFilacmbplacafinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        placaFinal = (registroAux.getCampos().get(SERIE))
                        .toString();
    }

    public void seleccionarFilacmbdependenciainicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaInicial = registroAux.getCampos().get(CODIGO).toString();
        dependenciaFinal = null;
        cargarListacmbdependenciafinal();
    }

    public void seleccionarFilacmbdependenciafinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFinal = registroAux.getCampos().get(CODIGO).toString();
    }

    public void seleccionarFilacmbresponsableinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsableInicial = registroAux.getCampos().get(CEDULA).toString();
        responsableFinal = null;
        cargarListacmbresponsablefinal();
    }

    public void seleccionarFilacmbresponsablefinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsableFinal = registroAux.getCampos().get(CEDULA).toString();
    }

    public void aceptartipoStickerExcel() {

        try {
            archivoDescarga = null;
            modeloCodigoBarrasAne = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MODELO STICKER ALMACEN SOLO CODIGO DE BARRAS ANE",
                            SessionUtil.getModulo(),
                            new Date(), true);
            modeloCodigoBarrasAne = modeloCodigoBarrasAne == null ? "NO"
                : modeloCodigoBarrasAne;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ("SI".equals(modeloCodigoBarrasAne)) {
            generarStickers("001763StickerAne80mm", FORMATOS.EXCEL);
        }

        else {
            generarStickers("000502Sticker", FORMATOS.EXCEL);
        }

    }

    public void aceptartipoSticker() {

        try {
            archivoDescarga = null;
            modeloCodigoBarrasAne = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MODELO STICKER ALMACEN SOLO CODIGO DE BARRAS ANE",
                            SessionUtil.getModulo(),
                            new Date(), true);
            modeloCodigoBarrasAne = modeloCodigoBarrasAne == null ? "NO"
                : modeloCodigoBarrasAne;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ("SI".equals(modeloCodigoBarrasAne)) {
            generarStickers("001763StickerAne80mm", FORMATOS.PDF);
        }

        else {
            generarStickers("000502Sticker", FORMATOS.PDF);
        }
    }

    public void cancelartipoStickerExcel() {
        if (seleccionado == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(TB_TB1890));
            return;
        }
        generarStickers("000507IStickerSinBarras", FORMATOS.EXCEL);
    }

    public void cancelartipoSticker() {
        if (seleccionado == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(TB_TB1890));
            return;
        }
        generarStickers("000507IStickerSinBarras", FORMATOS.PDF);
    }

    public boolean generarStickers(String reporte, FORMATOS formato) {
        try {

            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> reemplazar = new HashMap<>();

            if (validarYCrearConsulta(reemplazar)) {
                return false;
            }

            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(PR_NOMBRECOMPANIA,
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(PR_STRSQL, strSql);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA) + " "
                                + ex.getMessage());
            Logger.getLogger(GenerarStickerControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        muestraTipoSticker = false;
        muestraTipoStickerExcel = false;
        return true;
    }

    public String getElementoDesde() {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    public String getElementoHasta() {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    public String getPlacaInicial() {
        return placaInicial;
    }

    public void setPlacaInicial(String placaInicial) {
        this.placaInicial = placaInicial;
    }

    public String getPlacaFinal() {
        return placaFinal;
    }

    public void setPlacaFinal(String placaFinal) {
        this.placaFinal = placaFinal;
    }

    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
    }

    public String getResponsableInicial() {
        return responsableInicial;
    }

    public void setResponsableInicial(String responsableInicial) {
        this.responsableInicial = responsableInicial;
    }

    public String getResponsableFinal() {
        return responsableFinal;
    }

    public void setResponsableFinal(String responsableFinal) {
        this.responsableFinal = responsableFinal;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getNumeroMovimiento() {
        return numeroMovimiento;
    }

    public void setNumeroMovimiento(String numeroMovimiento) {
        this.numeroMovimiento = numeroMovimiento;
    }

    public String getNombreElementoDesde() {
        return nombreElementoDesde;
    }

    public void setNombreElementoDesde(String nombreElementoDesde) {
        this.nombreElementoDesde = nombreElementoDesde;
    }

    public String getNombreElementoHasta() {
        return nombreElementoHasta;
    }

    public void setNombreElementoHasta(String nombreElementoHasta) {
        this.nombreElementoHasta = nombreElementoHasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListacmbtipomovimiento() {
        return listacmbtipomovimiento;
    }

    public void setListacmbtipomovimiento(
        List<Registro> listacmbtipomovimiento) {
        this.listacmbtipomovimiento = listacmbtipomovimiento;
    }

    public List<Registro> getListacmbnumeromovimiento() {
        return listacmbnumeromovimiento;
    }

    public void setListacmbnumeromovimiento(
        List<Registro> listacmbnumeromovimiento) {
        this.listacmbnumeromovimiento = listacmbnumeromovimiento;
    }

    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }

    public RegistroDataModelImpl getListacmbplacainicial() {
        return listacmbplacainicial;
    }

    public void setListacmbplacainicial(
        RegistroDataModelImpl listacmbplacainicial) {
        this.listacmbplacainicial = listacmbplacainicial;
    }

    public RegistroDataModelImpl getListacmbplacafinal() {
        return listacmbplacafinal;
    }

    public void setListacmbplacafinal(
        RegistroDataModelImpl listacmbplacafinal) {
        this.listacmbplacafinal = listacmbplacafinal;
    }

    public RegistroDataModelImpl getListacmbdependenciainicial() {
        return listacmbdependenciainicial;
    }

    public void setListacmbdependenciainicial(
        RegistroDataModelImpl listacmbdependenciainicial) {
        this.listacmbdependenciainicial = listacmbdependenciainicial;
    }

    public RegistroDataModelImpl getListacmbdependenciafinal() {
        return listacmbdependenciafinal;
    }

    public void setListacmbdependenciafinal(
        RegistroDataModelImpl listacmbdependenciafinal) {
        this.listacmbdependenciafinal = listacmbdependenciafinal;
    }

    public RegistroDataModelImpl getListacmbresponsableinicial() {
        return listacmbresponsableinicial;
    }

    public void setListacmbresponsableinicial(
        RegistroDataModelImpl listacmbresponsableinicial) {
        this.listacmbresponsableinicial = listacmbresponsableinicial;
    }

    public RegistroDataModelImpl getListacmbresponsablefinal() {
        return listacmbresponsablefinal;
    }

    public void setListacmbresponsablefinal(
        RegistroDataModelImpl listacmbresponsablefinal) {
        this.listacmbresponsablefinal = listacmbresponsablefinal;
    }

    @Override
    public void abrirFormulario() {

        placa = false;
        elemento = false;
        dependencia = false;
        responsable = false;
        movimiento = false;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public boolean isElemento() {
        return elemento;
    }

    public void setElemento(boolean elemento) {
        this.elemento = elemento;
    }

    public boolean isPlaca() {
        return placa;
    }

    public void setPlaca(boolean placa) {
        this.placa = placa;
    }

    public boolean isDependencia() {
        return dependencia;
    }

    public void setDependencia(boolean dependencia) {
        this.dependencia = dependencia;
    }

    public boolean isResponsable() {
        return responsable;
    }

    public void setResponsable(boolean responsable) {
        this.responsable = responsable;
    }

    public boolean isMovimiento() {
        return movimiento;
    }

    public void setMovimiento(boolean movimiento) {
        this.movimiento = movimiento;
    }

    public boolean isMuestraTipoSticker() {
        return muestraTipoSticker;
    }

    public void setMuestraTipoSticker(boolean muestraTipoSticker) {
        this.muestraTipoSticker = muestraTipoSticker;
    }

    public boolean isMuestraTipoStickerExcel() {
        return muestraTipoStickerExcel;
    }

    public void setMuestraTipoStickerExcel(boolean muestraTipoStickerExcel) {
        this.muestraTipoStickerExcel = muestraTipoStickerExcel;
    }

    public String getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(String seleccionado) {
        this.seleccionado = seleccionado;
    }

}
