package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.enums.PredialrecibosdepagoControladorEnum;
import com.sysman.predial.enums.PredialrecibosdepagoControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 25/05/2016
 *
 * @version 2, 11/07/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class PredialrecibosdepagoControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String codigoBancoC;
    private final String configuracionEan;
    private final String leyenda;
    private final String copiaReciboPr;
    private final String nombreBancoC;
    private final String prCopia;
    private final String prLeyendaUsuario;
    private final String prNitCompania;
    private final String prNombreBanco;
    private final String prNombreCompania;
    private final String numerordenC;
    private final String strCodigoEANC;
    private boolean indactivo;
    private boolean indAnulado;
    private boolean indPago;
    private boolean indAcuerdo;
    private boolean indCuota;
    private boolean indVigencia;
    private boolean indAbono;
    private String pagoBanco;
    private String docNum;
    private String precod;
    private String anoFin;
    private String anoIni;
    private String noCuota;
    private String fechaExpedido;
    private String preval;
    private String fechaLimite;
    private String fechaAnulacion;
    private String fechaPago;
    private String nombreBanco;
    private String acuerdo;
    private boolean visiblefechaPago;
    private boolean visiblefechaAnulacion;
    private StreamedContent archivoDescarga;
    private List<Registro> listaPagoBanco;
    private RegistroDataModelImpl listaDocNum;

    @EJB
    private EjbPredialCeroRemote ejbPredialCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    private String codigoEAN;
    private String encabezadoUno;
    private String encabezadoDos;
    private String encabezadoTres;
    private String encabezadoCuatro;
    private String encabezadoTrece;
    private String encabezadoQuince;
    private String encabezadoDieciseis;
    private String encabezadoDiecisiete;
    private String encabezadoDieciNueve;
    private String encabezadoVeinte;
    private String encabezadoCatorce;

    /**
     * Creates a new instance of PredialrecibosdepagoControlador
     */
    public PredialrecibosdepagoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoBancoC = "CODIGOBANCO";
        configuracionEan = "CONFIGURE EL PARAMETRO CODIGO EAN";
        leyenda = "LEYENDA USUARIO";
        copiaReciboPr = "MANEJA COPIA DE RECIBO EN PREDIAL";
        nombreBancoC = "NOMBREBANCO";
        prCopia = "PR_COPIA";
        prLeyendaUsuario = "PR_LEYENDA_USUARIO";
        prNitCompania = "PR_NITCOMPANIA";
        prNombreBanco = "PR_NOMBREBANCO";
        prNombreCompania = "PR_NOMBRECOMPANIA";
        numerordenC = "numeroOrden";
        strCodigoEANC = "strCodigoEAN";
        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALRECIBOSDEPAGO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PredialrecibosdepagoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaPagoBanco();
        cargarListaDocNum();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        visiblefechaPago = true;
        // <CODIGO_DESARROLLADO>
        try {
            codigoEAN = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CODIGO EAN",
                                            modulo, new Date(),
                                            true),
                            configuracionEan);
            encabezadoUno = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 1);
            encabezadoDos = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 2);
            encabezadoTres = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 3);
            encabezadoCuatro = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 4);
            encabezadoTrece = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 13);
            encabezadoCatorce = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 14);
            encabezadoQuince = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 15);
            encabezadoDieciseis = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 16);
            encabezadoDiecisiete = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 17);
            encabezadoDieciNueve = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 19);
            encabezadoVeinte = ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 20);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaPagoBanco() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaPagoBanco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialrecibosdepagoControladorUrlEnum.URL7372
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDocNum() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialrecibosdepagoControladorUrlEnum.URL7811
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaDocNum = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "DOCNUM");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (indVigencia) {
            getInforme();
        }
        else if (indAbono) {
            getInformeAbono();
        }
        else if (indAcuerdo) {
            getInformeAcuerdo();
        }
        // </CODIGO_DESARROLLADO>
    }

    private void getInforme() {
        String reporte = "000817FORMATOPNUD";
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            parametros.put(prCopia, "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, copiaReciboPr,
                                            modulo,
                                            new Date(), true),
                                            "SI")));
            parametros.put("PR_ANOS_PAGOS", anoIni + " A " + anoFin);
            parametros.put(prNitCompania,
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put(prNombreCompania,
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_OBSERVACIONES", " ");
            // parametros.put("PR_GETUSER",
            parametros.put("PR_OBSERVACIONES", " ");
            parametros.put("PR_CODIGO", docNum);
            parametros.put(prLeyendaUsuario, SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, leyenda,
                                            modulo, new Date(),
                                            true),
                                            ""));
            parametros.put("PR_LEYENDA_LEGAL",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "LEYENDA LEGAL",
                                                            modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put(prNombreBanco, service.buscarEnListaObj(pagoBanco,
                            codigoBancoC, nombreBancoC, listaPagoBanco));

            reemplazar.put(numerordenC, SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            /* SE APLICA EL FILTRO DE NUMERO DE RECIBO */
            reemplazar.put("condicion", SysmanFunciones.concatenar(
                            " AND UP.NUMERO_ORDEN = '",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL, "' ",
                            " AND RP.DOCNUM = '", docNum, "' "));
            reemplazar.put("condicionSub", "");

            /**
             * ******************************* OJO
             * ************************************
             *
             * Los siguientes parametros, usados para los encabezados
             * de columna del subreporte 000818ReciboBancosPNUD,
             * dependen de la funciÃ¯Â¿Â½n Encabezado_Columna(int) a
             * ser migrada
             *
             * El reporte original se llama FORMATO_STD_COPIA, sin
             * embargo por motivos de unificar reportes, se sugiere
             * que se apunte al reporte 000817FORMATOPNUD
             *
             */
            parametros = cargarParametrosInforme(parametros);

            reemplazar.put(strCodigoEANC, codigoEAN);
            reemplazar.put("docNum", SysmanFunciones.colocarComillas(docNum));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.PDF);
        }
        catch (SysmanException | SystemException | JRException
                        | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private Map<String, Object> cargarParametrosInforme(
        Map<String, Object> parametros) {
        parametros.put("PR_ENC_1", encabezadoUno);
        parametros.put("PR_ENC_2", encabezadoDos);
        parametros.put("PR_ENC_3", encabezadoTres);
        parametros.put("PR_ENC_4", encabezadoCuatro);
        parametros.put("PR_ENC_13", encabezadoTrece);
        parametros.put("PR_ENC_14", encabezadoCatorce);
        parametros.put("PR_ENC_15", encabezadoQuince);
        parametros.put("PR_ENC_16", encabezadoDieciseis);
        parametros.put("PR_ENC_19", encabezadoDieciNueve);
        // IMPLEMENTACION MARCA BLANCA (ljdiaz - Luis Jacobo Diaz Muñoz)
        parametros.put("PR_EMPRESAPARAMETRIZADA", JsfUtil.obtenerParametroMarcaBlanca("TITULOLOGIN"));
        // FIN IMPLEMENTACION MARCA BLANCA
        return parametros;
    }

    /**
     * Genera la copia de factura cuando la factura estÃ¯Â¿Â½
     * relacionada con los abono
     */
    private void getInformeAbono() {
        String reporte = "001054FORMATOSTDABONO";
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            parametros.put(prCopia, "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, copiaReciboPr,
                                            modulo,
                                            new Date(), true),
                                            "SI")));
            parametros.put("PR_FECLIMITE", fechaLimite);
            parametros.put("PR_FECHA", fechaExpedido);
            parametros.put(prNombreCompania,
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(prNitCompania,
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_GETUSER", SessionUtil.getUser().getCodigo());
            parametros.put(prLeyendaUsuario, SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, leyenda,
                                            modulo, new Date(),
                                            true),
                                            ""));
            parametros.put("PR_LEYENDA_LEGAL_ABONO",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "LEYENDA LEGAL ABONO",
                                                            modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_PARAMETROSRECIBOS",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "PARAMETROS RECIBOS",
                                                            modulo,
                                                            new Date(), true),
                                            ""));
            // PR_GETPAGINAWEB
            parametros.put("PR_GETPAGINAWEB",
                            SessionUtil.getCompaniaIngreso().getPaginaWeb());
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", JsfUtil.obtenerParametroMarcaBlanca("TITULOLOGIN"));
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            parametros.put(prNombreBanco, service.buscarEnListaObj(pagoBanco,
                            codigoBancoC, nombreBancoC, listaPagoBanco));

            parametros = cargarParametrosAbono(parametros);

            reemplazar.put(strCodigoEANC,
                            SysmanFunciones.colocarComillas(codigoEAN));
            reemplazar.put("codigo", SysmanFunciones.colocarComillas(precod));
            reemplazar.put(numerordenC, SysmanFunciones.colocarComillas(
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL));
            reemplazar.put("docnum", SysmanFunciones.colocarComillas(docNum));

            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.PDF);
        }
        catch (SysmanException | JRException | IOException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private Map<String, Object> cargarParametrosAbono(
        Map<String, Object> parametros) {
        parametros.put("PR_ENCABEZADO_COLUMNA1", encabezadoUno);
        parametros.put("PR_ENCABEZADO_COLUMNA2", encabezadoDos);
        parametros.put("PR_ENCABEZADO_COLUMNA13", encabezadoTrece);
        parametros.put("PR_ENCABEZADO_COLUMNA16", encabezadoDieciseis);
        parametros.put("PR_ENCABEZADO_COLUMNA17", encabezadoDiecisiete);
        parametros.put("PR_ENCABEZADO_COLUMNA19", encabezadoDieciNueve);
        parametros.put("PR_ENCABEZADO_COLUMNA20", encabezadoVeinte);
        return parametros;
    }

    private void getInformeAcuerdo() {
        String reporte = "001057FORMATOSTDACUERDO";
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            parametros.put(prCopia, "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, copiaReciboPr,
                                            modulo,
                                            new Date(), true),
                                            "SI")));
            parametros.put(prNombreBanco, SysmanFunciones
                            .nvl(service.buscarEnListaObj(pagoBanco,
                                            codigoBancoC, nombreBancoC,
                                            listaPagoBanco), "")
                            .toString());
            parametros.put("PR_FECHA", new Date());
            parametros.put(prNombreCompania,
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(prNitCompania,
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_GETUSER", SessionUtil.getUser().getCodigo());
            parametros.put(prLeyendaUsuario, SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, leyenda,
                                            modulo, new Date(),
                                            true),
                                            ""));
            parametros.put("PR_LEYENDA_LEGAL_ACUERDO",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "LEYENDA LEGAL ACUERDO",
                                                            modulo,
                                                            new Date(), true),
                                            ""));
            // PR_GETPAGINAWEB
            parametros.put("PR_GETPAGINAWEB",
                            SessionUtil.getCompaniaIngreso().getPaginaWeb());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PredialrecibosdepagoControladorEnum.ACUERDO.getValue(),
                            acuerdo);
            param.put(PredialrecibosdepagoControladorEnum.PRECOD.getValue(),
                            precod);
            param.put(GeneralParameterEnum.DOCNUM.getName(), docNum);
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialrecibosdepagoControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
            boolean cuotasRecibos = true;
            if (rs != null) {
                cuotasRecibos = false;
            }
            parametros.put("PR_CUOTASRECIBOS", cuotasRecibos);

            parametros.put("PR_ENCABEZADO_COLUMNA1", encabezadoUno);
            parametros.put("PR_ENCABEZADO_COLUMNA2", encabezadoDos);
            parametros.put("PR_ENCABEZADO_COLUMNA13", encabezadoTrece);
            parametros.put("PR_ENCABEZADO_COLUMNA14", encabezadoCatorce);
            parametros.put("PR_ENCABEZADO_COLUMNA15", encabezadoQuince);
            parametros.put("PR_ENCABEZADO_COLUMNA17", encabezadoDiecisiete);
            parametros.put("PR_ENCABEZADO_COLUMNA20", encabezadoVeinte);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", JsfUtil.obtenerParametroMarcaBlanca("TITULOLOGIN"));
            // FIN IMPLEMENTACION MARCA_BLANCA

            reemplazar.put(strCodigoEANC,
                            SysmanFunciones.colocarComillas(codigoEAN));
            reemplazar.put("codigo", SysmanFunciones.colocarComillas(precod));
            reemplazar.put(numerordenC, SysmanFunciones.colocarComillas(
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL));
            reemplazar.put("docnum", SysmanFunciones.colocarComillas(docNum));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.PDF);
        }
        catch (SysmanException | JRException | IOException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void onRowSelectDocNum(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        docNum = registroAux.getCampos().get("DOCNUM").toString();

        precod = registroAux.getCampos().get("PRECOD").toString();
        indPago = (boolean) registroAux.getCampos().get("PAGO");
        indAnulado = (boolean) registroAux.getCampos().get("ANULADO");
        indAbono = !"0".equals(
                        registroAux.getCampos().get("ESABONO").toString());
        indAcuerdo = !"0".equals(
                        registroAux.getCampos().get("ESACUERDO").toString());
        indCuota = !"0".equals(
                        registroAux.getCampos().get("ESCUOTA").toString());
        fechaExpedido = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PREFEC"), " ")
                        .toString();
        fechaLimite = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PREFECLIM"), " ")
                        .toString();
        preval = registroAux.getCampos().get("PREVAL").toString();
        fechaPago = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PREFECPAG"), " ")
                        .toString();
        pagoBanco = registroAux.getCampos().get("PAG_BANPAG").toString();
        fechaAnulacion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("FECHAANULACION"), " ")
                        .toString();
        anoIni = registroAux.getCampos().get("PREANOI").toString();
        anoFin = registroAux.getCampos().get("PREANOF").toString();
        acuerdo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ACUERDO"), " ")
                        .toString();

        if (!indPago && !indAnulado) {
            indactivo = true;
        }

        visiblefechaAnulacion = !indPago;
        visiblefechaPago = indPago;

        visiblefechaAnulacion = indAnulado;
        visiblefechaPago = !indAnulado;

        if (indAcuerdo) {
            noCuota = registroAux.getCampos().get("NCUOTA_ACUERDO").toString();
            indVigencia = false;
        }
        if (indCuota) {
            noCuota = registroAux.getCampos().get("NCUOTA").toString();
        }
        if (indAbono) {
            noCuota = null;
            indVigencia = false;
        }

        if (!indAbono && !indAcuerdo && !indCuota) {
            indVigencia = true;
        }
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getPagoBanco() {
        return pagoBanco;
    }

    public boolean isIndactivo() {
        return indactivo;
    }

    public void setIndactivo(boolean indactivo) {
        this.indactivo = indactivo;
    }

    public boolean isIndAnulado() {
        return indAnulado;
    }

    public void setIndAnulado(boolean indAnulado) {
        this.indAnulado = indAnulado;
    }

    public boolean isIndPago() {
        return indPago;
    }

    public void setIndPago(boolean indPago) {
        this.indPago = indPago;
    }

    public boolean isIndAcuerdo() {
        return indAcuerdo;
    }

    public void setIndAcuerdo(boolean indAcuerdo) {
        this.indAcuerdo = indAcuerdo;
    }

    public boolean isIndCuota() {
        return indCuota;
    }

    public void setIndCuota(boolean indCuota) {
        this.indCuota = indCuota;
    }

    public boolean isIndVigencia() {
        return indVigencia;
    }

    public void setIndVigencia(boolean indVigencia) {
        this.indVigencia = indVigencia;
    }

    public boolean isIndAbono() {
        return indAbono;
    }

    public void setIndAbono(boolean indAbono) {
        this.indAbono = indAbono;
    }

    public void setPagoBanco(String pagoBanco) {
        this.pagoBanco = pagoBanco;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getPrecod() {
        return precod;
    }

    public void setPrecod(String precod) {
        this.precod = precod;
    }

    public String getAnoFin() {
        return anoFin;
    }

    public void setAnoFin(String anoFin) {
        this.anoFin = anoFin;
    }

    public String getAnoIni() {
        return anoIni;
    }

    public void setAnoIni(String anoIni) {
        this.anoIni = anoIni;
    }

    public String getNoCuota() {
        return noCuota;
    }

    public void setNoCuota(String noCuota) {
        this.noCuota = noCuota;
    }

    public String getFechaExpedido() {
        return fechaExpedido;
    }

    public void setFechaExpedido(String fechaExpedido) {
        this.fechaExpedido = fechaExpedido;
    }

    public String getPreval() {
        return preval;
    }

    public void setPreval(String preval) {
        this.preval = preval;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(String fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getAcuerdo() {
        return acuerdo;
    }

    public void setAcuerdo(String acuerdo) {
        this.acuerdo = acuerdo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isVisiblefechaPago() {
        return visiblefechaPago;
    }

    public void setVisiblefechaPago(boolean visiblefechaPago) {
        this.visiblefechaPago = visiblefechaPago;
    }

    public boolean isVisiblefechaAnulacion() {
        return visiblefechaAnulacion;
    }

    public void setVisiblefechaAnulacion(boolean visiblefechaAnulacion) {
        this.visiblefechaAnulacion = visiblefechaAnulacion;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaPagoBanco() {
        return listaPagoBanco;
    }

    public void setListaPagoBanco(List<Registro> listaPagoBanco) {
        this.listaPagoBanco = listaPagoBanco;
    }

    public RegistroDataModelImpl getListaDocNum() {
        return listaDocNum;
    }

    public void setListaDocNum(RegistroDataModelImpl listaDocNum) {
        this.listaDocNum = listaDocNum;
    }
}
