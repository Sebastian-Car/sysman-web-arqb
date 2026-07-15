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
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.PredialRecCajasControladorEnum;
import com.sysman.predial.enums.PredialRecCajasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 02/06/2016 18:07:22 -- Modificado por sdaza
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * @author jcrodriguez - Refactoring (creacion de dss) y depuracion
 * del controlador
 * @version 3, 11/07/2017
 */
@ManagedBean
@ViewScoped
public class PredialRecCajasControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante definida para almacenar "SYSDATE"
     */
    private String nomPropietario;
    private String codigoPredio;
    private String numeroOrden;
    private String anoPago;
    private String anoFin;
    private String fechaCorte;
    private String fecLimite;
    private String numRecibo;
    private String avaluo;
    private boolean aplica1175;
    private boolean aplica1066;
    private boolean anoUnico;
    private boolean indVisbleDscEsp;
    private String etAplica1175;
    private boolean indVerDgProp;
    private String idPropietarioFac;
    private String nomPropietarioFac;
    private String banco;
    private String nombreCompania;
    private String nitCompania;
    private StreamedContent archivoDescarga;
    private List<Registro> listaanofin;
    private List<Registro> listaCmbBanco;
    private RegistroDataModelImpl listaPropietarioFac;
    private Map<String, Object> parametrosEntrada;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatro;
    @EJB
    private EjbPredialCeroRemote ejbPredialCero;

    /**
     * Creates a new instance of PredialRecCajasControlador
     */
    public PredialRecCajasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();

        numFormulario = GeneralCodigoFormaEnum.PREDIAL_REC_CAJAS_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                codigoPredio = parametrosEntrada.get("codigoPredio").toString();
                numeroOrden = parametrosEntrada.get("nroOrden").toString();
                nomPropietario = parametrosEntrada
                                .get("nomPropietario").toString();
                anoPago = parametrosEntrada.get("anoPago").toString();
                avaluo = parametrosEntrada.get("avaluo").toString();
            }
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <INI_ADICIONAL>
        // </INI_ADICIONAL>
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaanofin();
        cargarListaCmbBanco();
        cargarListaPropietarioFac();
        abrirFormulario();
    }

    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
                            new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        try {
            fechaCorte = SysmanFunciones.convertirAFechaCadena(new Date());
        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        // Consultar el ultimo numero de factura para cargarlo en
        // el formulario
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        List<Registro> cscFactura;
        try {
            cscFactura = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialRecCajasControladorUrlEnum.URL9523
                                                                            .getValue())
                                            .getUrl(), param));
            if (!cscFactura.isEmpty()) {
                numRecibo = cscFactura.get(0).getCampos()
                                .get("CONSECUTIVOREAL") == null ? "000000001"
                                    : cscFactura.get(0).getCampos()
                                                    .get("CONSECUTIVOREAL")
                                                    .toString();
                numRecibo = String.valueOf(Integer.parseInt(numRecibo) + 1);
                numRecibo = SysmanFunciones.padl(numRecibo, 9, "0");
            }
            else {
                numRecibo = "000000001";
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // validacion de parametros de ley especial de
        // descuentos
        String manejaDscEspeciales;
        indVisbleDscEsp = false;

        manejaDscEspeciales = getParametro(
                        "MANEJA OPCION DESCUENTOS ESPECIALES",
                        true);
        manejaDscEspeciales = manejaDscEspeciales == null ? "NO"
            : manejaDscEspeciales;
        if ("SI".equals(manejaDscEspeciales)) {
            indVisbleDscEsp = true;
            String vlrIndicador;
            vlrIndicador = getParametro(
                            "VALOR PREDETERMINADO INDICADOR OPCION DESC ESP",
                            false);
            vlrIndicador = vlrIndicador == null ? "0" : vlrIndicador;
            if ("0".equals(vlrIndicador)) {
                aplica1175 = false;
            }
            else {
                aplica1175 = true;
            }
            etAplica1175 = getParametro(
                            "TITULO OPCION DESCUENTOS ESPECIALES",
                            false);
            etAplica1175 = etAplica1175 == null
                ? "TITULO OPCION DESCUENTOS ESPECIALES" : etAplica1175;
        }
        else {
            indVisbleDscEsp = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaanofin() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialRecCajasControladorEnum.CODIGOPREDIO.getValue(),
                        codigoPredio);
        param.put(PredialRecCajasControladorEnum.NUMERO_ORDEN_PREDIAL
                        .getValue(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PredialRecCajasControladorEnum.ANOPAGO.getValue(), anoPago);

        try {
            listaanofin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialRecCajasControladorUrlEnum.URL8404
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbBanco() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbBanco = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PredialRecCajasControladorUrlEnum.URL9503
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPropietarioFac() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialRecCajasControladorUrlEnum.URL10110
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialRecCajasControladorEnum.CODIGOPREDIO.getValue(),
                        codigoPredio);
        param.put(PredialRecCajasControladorEnum.NUMERO_ORDEN_PREDIAL
                        .getValue(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaPropietarioFac = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PredialRecCajasControladorEnum.NIT.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // en este metodo se debe realizar el proceso de facturacion
        // y pago de la factura, finalmente generar reporte de la
        // factura y cerrar el formulario modal
        String generaFraCoprop;

        generaFraCoprop = getParametro(
                        "GENERAR FACTURA A COPROPIETARIOS",
                        true);
        generaFraCoprop = generaFraCoprop == null ? "NO" : generaFraCoprop;
        nomPropietarioFac = null;
        idPropietarioFac = null;
        if ("SI".equals(generaFraCoprop)) {
            indVerDgProp = true;
        }
        else {
            indVerDgProp = false;
            imprimirReciboCaja();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentacion() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void aceptarFactPropietario() {
        archivoDescarga = null;
        imprimirReciboCaja();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaPropietarioFac(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idPropietarioFac = registroAux.getCampos()
                        .get(PredialRecCajasControladorEnum.NIT.getValue())
                        .toString();
        nomPropietarioFac = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Realiza el llamado al procedimiento PR_IMPRESORAVIGENCIACAJA
     * que realiza el proceso para la impresion del recibo de caja de
     * un predio
     */
    public void imprimirReciboCaja() {
        // <CODIGO_DESARROLLADO>
        try {
            String auxAplicaLey1066 = aplica1066 ? "-1" : "0";
            String auxAplicaLey1175 = aplica1175 ? "-1" : "0";
            String auxAnoUnico = anoUnico ? "-1" : "0";

            ejbPredialCuatro.imprimirVigenciaCaja(compania, codigoPredio,
                            numeroOrden, new BigDecimal(avaluo), new Date(),
                            Boolean.parseBoolean(auxAplicaLey1066),
                            Boolean.parseBoolean(auxAplicaLey1175),
                            Boolean.parseBoolean(auxAnoUnico),
                            Integer.parseInt(anoFin),
                            Integer.parseInt(anoPago),
                            SessionUtil.getUser().getCodigo(),
                            SysmanFunciones.validarVariableVacio(
                                            nomPropietarioFac) ? ""
                                                : nomPropietarioFac,
                            SysmanFunciones.validarVariableVacio(
                                            idPropietarioFac) ? ""
                                                : idPropietarioFac,
                            banco);

            generarInforme(FORMATOS.PDF);
        }
        catch (SystemException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Define el periodo en anios que se va a facturar
     * 
     * @return el anio o anio a facturar
     */
    private String calcularAniosPagos() {
        String anio;
        int aux = Integer.parseInt(anoPago) + 1;
        if (Integer.parseInt(anoFin) - aux > 0 && !anoUnico) {
            anio = aux + " - " + anoFin;
        }
        else {
            anio = anoFin;
        }

        return anio;
    }

    /**
     * Obtiene el nombre del concepto que se envia por parametro
     * 
     * @param con
     * Conexion a la BD
     * @param concepto
     * Numero de concepto que se quiere consultar
     * @return Nombre del concepto asociado al numero ingresado por
     * parametro
     */
    private String obtenerNombreColumna(String concepto) {
        String nombre = "";
        try {
            nombre = ejbPredialCero.consultarEncabezadoDeColumna(compania,
                            Integer.parseInt(concepto));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return nombre;

    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envia los
     * parametros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        try {
            String parLeyendaUsuario = getParametro(
                            "LEYENDA USUARIO",
                            false);
            String parLeyendaLegal = getParametro(
                            "LEYENDA LEGAL",
                            false);
            // HashMap reemplazar: Envio de reemplazos para la
            // consulta almacenda en la BD
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoPredial", "'" + codigoPredio + "'");
            reemplazar.put("anoUnico", anoUnico ? "-1" : "0");
            reemplazar.put("anoFin", anoFin);
            reemplazar.put("numeroOrden", "'" + numeroOrden + "'");
            reemplazar.put("pagoAno", anoPago);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHAEMISION",
                            SysmanFunciones.convertirAFechaCadena(new Date()));
            parametros.put("PR_ANIOSPAGOS", calcularAniosPagos());
            parametros.put("PR_NUMRECIBO", numRecibo);
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            parametros.put("PR_NITCOMPANIA", nitCompania);
            parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());
            parametros.put("PR_LEYENDAUSUARIO", parLeyendaUsuario);
            parametros.put("PR_LEYENDALEGAL", parLeyendaLegal);
            parametros.put("PR_PAGINAWEB",
                            SessionUtil.getCompaniaIngreso().getPaginaWeb());
            parametros.put("PR_ENCABEZADO1", obtenerNombreColumna("1"));
            parametros.put("PR_ENCABEZADO2", obtenerNombreColumna("2"));
            parametros.put("PR_ENCABEZADO3", obtenerNombreColumna("3"));
            parametros.put("PR_ENCABEZADO4", obtenerNombreColumna("4"));
            parametros.put("PR_ENCABEZADO13", obtenerNombreColumna("13"));
            parametros.put("PR_ENCABEZADO14", obtenerNombreColumna("14"));
            parametros.put("PR_ENCABEZADO15", obtenerNombreColumna("15"));
            parametros.put("PR_ENCABEZADO16", obtenerNombreColumna("16"));
            parametros.put("PR_ENCABEZADO20", obtenerNombreColumna("20"));
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", JsfUtil.obtenerParametroMarcaBlanca("TITULOLOGIN"));
            // FIN IMPLEMENTACION MARCA_BLANCA

            Reporteador.resuelveConsulta(
                            PredialRecCajasControladorEnum.REPORTE001415
                                            .getValue(),
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            PredialRecCajasControladorEnum.REPORTE001415
                                            .getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2903"));
            RequestContext.getCurrentInstance().closeDialog(null);

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                                + ex.getMessage() + " "
                                + PredialRecCajasControladorEnum.REPORTE001415
                                                .getValue());
            Logger.getLogger(PredialRecCajasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <SET_GET_ATRIBUTOS>
    public String getNomPropietario() {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario) {
        this.nomPropietario = nomPropietario;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public String getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(String fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public String getFecLimite() {
        return fecLimite;
    }

    public void setFecLimite(String fecLimite) {
        this.fecLimite = fecLimite;
    }

    public String getNumRecibo() {
        return numRecibo;
    }

    public void setNumRecibo(String numRecibo) {
        this.numRecibo = numRecibo;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getAvaluo() {
        return avaluo;
    }

    public void setAvaluo(String avaluo) {
        this.avaluo = avaluo;
    }

    public String getAnoPago() {
        return anoPago;
    }

    public void setAnoPago(String anoPago) {
        this.anoPago = anoPago;
    }

    public String getAnoFin() {
        return anoFin;
    }

    public void setAnoFin(String anoFin) {
        this.anoFin = anoFin;
    }

    public boolean isAplica1175() {
        return aplica1175;
    }

    public void setAplica1175(boolean aplica1175) {
        this.aplica1175 = aplica1175;
    }

    public boolean isAplica1066() {
        return aplica1066;
    }

    public void setAplica1066(boolean aplica1066) {
        this.aplica1066 = aplica1066;
    }

    public boolean isAnoUnico() {
        return anoUnico;
    }

    public void setAnoUnico(boolean anoUnico) {
        this.anoUnico = anoUnico;
    }

    public boolean isIndVisbleDscEsp() {
        return indVisbleDscEsp;
    }

    public void setIndVisbleDscEsp(boolean indVisbleDscEsp) {
        this.indVisbleDscEsp = indVisbleDscEsp;
    }

    public String getEtAplica1175() {
        return etAplica1175;
    }

    public void setEtAplica1175(String etAplica1175) {
        this.etAplica1175 = etAplica1175;
    }

    public boolean isIndVerDgProp() {
        return indVerDgProp;
    }

    public void setIndVerDgProp(boolean indVerDgProp) {
        this.indVerDgProp = indVerDgProp;
    }

    public String getIdPropietarioFac() {
        return idPropietarioFac;
    }

    public void setIdPropietarioFac(String idPropietarioFac) {
        this.idPropietarioFac = idPropietarioFac;
    }

    public String getNomPropietarioFac() {
        return nomPropietarioFac;
    }

    public void setNomPropietarioFac(String nomPropietarioFac) {
        this.nomPropietarioFac = nomPropietarioFac;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaanofin() {
        return listaanofin;
    }

    public void setListaanofin(List<Registro> listaanofin) {
        this.listaanofin = listaanofin;
    }

    public List<Registro> getListaCmbBanco() {
        return listaCmbBanco;
    }

    public void setListaCmbBanco(List<Registro> listaCmbBanco) {
        this.listaCmbBanco = listaCmbBanco;
    }

    public RegistroDataModelImpl getListaPropietarioFac() {
        return listaPropietarioFac;
    }

    public void setListaPropietarioFac(
        RegistroDataModelImpl listaPropietarioFac) {
        this.listaPropietarioFac = listaPropietarioFac;
    }
}
