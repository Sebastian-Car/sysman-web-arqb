package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.contabilidad.enums.CertificadosRetencionControladorEnum;
import com.sysman.contabilidad.enums.CertificadosRetencionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.reporte.PrepararReporte;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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
 * @author jrodriguezr
 * @version 1, 11/04/2016
 *
 * @author eamaya
 * @version 2, 12/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 *
 * @version 3.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema.
 *
 */
@ManagedBean
@ViewScoped

public class CertificadosRetencionControlador extends BeanBaseDatosAcme {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el que el usuario abre el formulario
     */
    private final String modulo;

    private final String cFiltroNitOnombre;
    private final String cTextoFormatoCertificados;
    private final String cTercero1;
    private final String cLimiteIngresosPatrimonio;
    private final String cPRCentrarFirma;
    private final String cTituloEnCertificados;
    private final String cFechaInicial;
    private final String cFechaFinal;
    private final String cFirmaCertificadoRetencion;
    private final String cCargoCertificadoRetencion;
    private final String cNombreVerificaCertificadoRetencion;
    private final String cCargoVerificaCertificadoRetencion;

    private boolean especial;
    private boolean centrarFirma;
    private boolean bimestral;
    private boolean conPeriodo;
    private boolean titulo;
    private boolean porNit;
    private boolean discriminado;
    private boolean valorPagos;
    private boolean formato220;
    private boolean discriminadoPorcentaje;
    private boolean formato350;
    private boolean sinDatos;
    private String tercero1;
    private String tercero2;
    private String cuentaInicial;
    private String cuentaFinal;
    private String tipoRetencion;
    private String consignadaEn;
    private String anio;
    private String tercero1Nombre;
    private String tercero2Nombre;
    private Date fechaInicial;
    private Date fechaFinal;
    private Date fecha;
    private String fechainicial1;
    private String fechafinal2;
    private String nombreCuentaInicial;
    private String nombreCuentaFinal;
    private String ciudadPago;
    private List<Registro> listaTipoRetencion;
    private List<Registro> listaConsignadaEn;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaTercero1;
    private RegistroDataModelImpl listaTercero2;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaterceroNombreInicial;
    private RegistroDataModelImpl listaterceroNombreFinal;
    private StreamedContent archivoDescarga;
    private boolean tercero2Visible;
    private boolean terceroNVisible;
    private boolean terceroNitVisible;
    private boolean terceroNFinalVisible;
    private boolean tercero2Etq;
    private boolean espIngresos;
    private String PermiteVerFirmaCertificadoRetencion;

    // <EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private String NombreVerificaCertificadoRetencion;

    private String CargoVerificaCertificadoRetencion;

    private String firmaCertRet;

    private String cargoCertRet;

    // </EJBs>

    /**
     * Creates a new instance of CertificadosRetencionControlador
     */
    public CertificadosRetencionControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cFiltroNitOnombre = "filtroNitONombre";
        cTextoFormatoCertificados = "PR_TEXTO_PARA_FORMATO_DE_CERTIFICADOS_DE_RETENCION";
        cLimiteIngresosPatrimonio = "LIMITE INGRESOS PATRIMONIO PARA CERTIFICADO UVT";
        cTituloEnCertificados = "PR_TITULO_EN_CERTIFICADOS_DE_RETENCION";
        cFirmaCertificadoRetencion = "PR_FIRMA_CERTIFICADO_RETENCION";
        cCargoCertificadoRetencion = "PR_CARGO_CERTIFICADO_RETENCION";
        cTercero1 = "tercero1";
        cPRCentrarFirma = "PR_CENTRARFIRMA";
        cFechaInicial = "PR_FECHAINICIAL";
        cFechaFinal = "PR_FECHAFINAL";
        cNombreVerificaCertificadoRetencion = "PR_NOMBRE_QUIEN_VERIFICA_CERTIFICADO_RETENCION";
        cCargoVerificaCertificadoRetencion = "PR_CARGO_QUIEN_VERIFICA_CERTIFICADO_RETENCION";

        try {
            // 606
            numFormulario = GeneralCodigoFormaEnum.CERTIFICADOS_RETENCION_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CertificadosRetencionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = "";
        asignarOrigenDatos();
        reasignarOrigenGrilla();
        fecha = fechaInicial = fechaFinal = new Date();
        
        fechainicial1 = SysmanFunciones.concatenar("01/01/", anio);
        fechafinal2 = SysmanFunciones.concatenar("31/12/", anio);

        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));

        ciudadPago = SessionUtil.getCompaniaIngreso().getCiudad().toUpperCase();
        porNit = true;
        tercero2Etq = true;
        terceroNitVisible = true;
        tercero1 = "00000";
        tercero2 = SysmanConstantes.CONS_TERCERO;
        tercero2Visible = true;
        cargarListaAno();
        cargarListaTipoRetencion();
        cargarListaConsignadaEn();
        consignadaEn = service.buscarEnLista("1",
                        GeneralParameterEnum.CODIGO.getName(),
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaConsignadaEn);
        cargarListaTercero1();
        cargarListaTercero2();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    @Override
    public void iniciarListas() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void iniciarListasSub() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void iniciarListasSubNulo() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    public void cargarListaTipoRetencion() {
        try {
            listaTipoRetencion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadosRetencionControladorUrlEnum.URL5278
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaConsignadaEn() {
        String regLista = null;

        try {
            regLista = ejbSysmanUtil.consultarParametro(compania,
                            "RETENCIONES CONSIGNADAS EN", modulo, new Date(),
                            false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        String lista = SysmanFunciones.nvlStr(regLista, "");
        lista = lista.replace("'", "");
        String[] objetos = lista.split(";");

        listaConsignadaEn = new ArrayList<>();

        for (int i = 0; i < objetos.length; i++) {
            Map<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.CODIGO.getName(),
                            Integer.toString(i + 1));

            param.put(GeneralParameterEnum.NOMBRE.getName(), objetos[i]);

            listaConsignadaEn.add(new Registro(i, param));

        }
    }

    public void cargarListaAno() {

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadosRetencionControladorUrlEnum.URL6678
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTercero1() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadosRetencionControladorUrlEnum.URL6985
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTercero1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTercero2() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadosRetencionControladorUrlEnum.URL7567
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(CertificadosRetencionControladorEnum.PARAM2.getValue(),
                        tercero1);

        listaTercero2 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadosRetencionControladorUrlEnum.URL8231
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadosRetencionControladorUrlEnum.URL9217
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CertificadosRetencionControladorEnum.PARAM5.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaterceroNombreInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadosRetencionControladorUrlEnum.URL10434
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaterceroNombreInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NOMBRE.getName());
    }

    public void cargarListaterceroNombreFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadosRetencionControladorUrlEnum.URL11046
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CertificadosRetencionControladorEnum.PARAM9.getValue(),
                        tercero1);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaterceroNombreFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NOMBRE.getName());
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

        generaInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarabrirFormato350() {
        // <CODIGO_DESARROLLADO>
        if (formato350) {
            SessionUtil.cargarModal("621", SessionUtil.getModulo());
            formato350 = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    private String getReporte(HashMap<String, Object> reemplazar,
        Map<String, Object> parametros) {
        String codPostal = null;
        String nombreJefe = "";
        String cargoJefe = "";
        String cPeriodo = null;
        String strFormato = null;
        String strFormatoBimestral = null;
        String formatoCertRetEsp = null;
        String reporte = null;
        String textoFormatoCertRet = null;
        String tituloCertRet = null;
        String firmaCertRet = null;
        String cargoCertRet = null;
        String cargoCertRet2 = null;
        String firmaCertRet2 = "";
        String fechaIni = null;
        String fechaFin = null;
        String strFechaInicial = null;
        String strFechaFinal = null;

        try {
            codPostal = ejbSysmanUtil.consultarParametro(compania,
                            "CODIGO POSTAL", SessionUtil.getModulo(),
                            new Date(), false);

            cPeriodo = ejbSysmanUtil.consultarParametro(compania,
                            this.parametros.getString("PR_PERCERTRETENCION"),
                            SessionUtil.getModulo(), new Date(), false);

            cPeriodo = "0".equals(cPeriodo) ? "1" : "2";
            textoFormatoCertRet = ejbSysmanUtil.consultarParametro(compania,
                            "TEXTO PARA FORMATO DE CERTIFICADOS DE RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            tituloCertRet = ejbSysmanUtil.consultarParametro(compania,
                            "TITULO EN CERTIFICADOS DE RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            firmaCertRet = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CERTIFICADO RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            cargoCertRet = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CERTIFICADO RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            cargoCertRet2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO 2 EN CERTIFICADO RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            firmaCertRet2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA 2 EN CERTIFICADO RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            nombreJefe = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE JEFE DE CONTABILIDAD",
                            SessionUtil.getModulo(), new Date(), false);

            cargoJefe = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE JEFE DE CONTABILIDAD",
                            SessionUtil.getModulo(), new Date(), false);

            formatoCertRetEsp = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO CERTIFICADOS DE RETENCION ESPECIAL",
                            SessionUtil.getModulo(), new Date(), false);

            strFormato = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO CERTIFICADOS DE RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            strFormatoBimestral = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO CERTIFICADOS DE RETENCION BIMESTRAL",
                            SessionUtil.getModulo(), new Date(), false);

            fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "MMMM")
                            .toUpperCase();

            fechaFin = SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "MMMM")
                            .toUpperCase();

            strFechaInicial = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);

            strFechaFinal = SysmanFunciones.convertirAFechaCadena(fechaFinal);

            String filtroNitONombre = porNit ? "TERCERO.NIT" : "TERCERO.NOMBRE";

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            Registro r = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CertificadosRetencionControladorUrlEnum.URL6522
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            String telefono = r.getCampos().get("TELEFONO").toString();

            String claseRet = service.buscarEnLista(tipoRetencion,
                            GeneralParameterEnum.NOMBRE.getName(),
                            "CLASE", listaTipoRetencion) == null ? ""
                                : service.buscarEnLista(tipoRetencion,
                                                GeneralParameterEnum.NOMBRE
                                                                .getName(),
                                                "CLASE", listaTipoRetencion);

            // Pï¿½RAMETROS COMUNES
            parametros.put("PR_ANO", anio);
            parametros.put("PR_TELEFONOCOMPANIA", telefono);
            parametros.put("PR_DIRECCIONCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDireccion());
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_CONSIGNADAEN", consignadaEn);
            parametros.put("PR_TIPORETENCION", tipoRetencion.toUpperCase());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            String strFecha = SysmanFunciones.dia(fecha) + " dias del mes de "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                .mes(fecha)]
                + " de "
                + SysmanFunciones.ano(fecha) + ". ";
            parametros.put("PR_FECHARETENCION", strFecha);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            // Pï¿½RAMETROS COMUNES
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA

            if ((reporte = sinDatos(reemplazar, filtroNitONombre, codPostal,
                            textoFormatoCertRet, parametros)) != null) {
                return reporte;
            }

            if ((reporte = formato220(reemplazar, parametros)) != null) {
                return reporte;
            }
            // VARIABLES COMUNES
            reemplazar.put(cFiltroNitOnombre, filtroNitONombre);
            reemplazar.put(cTercero1, tercero1);
            reemplazar.put("tercero2", tercero2);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("anio", anio);
            reemplazar.put("fechaInicial", strFechaInicial);
            reemplazar.put("fechaFinal", strFechaFinal);
            reemplazar.put("codigoPostal", codPostal == null ? "" : codPostal);
            // VARIABLES COMUNES

            if (conPeriodo) {
                reporte = "000613CerRetencionesCR";
                reemplazar.put("cPeriodo", cPeriodo);

                reemplazar.put("conPeriodoDiscriminado", "1");
                reemplazar.put("conPeriodo", "1");

                if (discriminado) {
                    reemplazar.put("conPeriodoDiscriminado", "2");
                    reemplazar.put("conPeriodo", "2");
                }
                // Pï¿½RAMETROS COMUNES
                parametros.put(cTextoFormatoCertificados,
                                textoFormatoCertRet);
                // Pï¿½RAMETROS COMUNES
                String periodoCardinal = noCardinales(
                                Integer.parseInt(cPeriodo));
                parametros.put("PR_PERIODO_DE_CERTIFICADOS_DE_RETENCION_CARDINAL",
                                periodoCardinal);
                parametros.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD", nombreJefe);
                parametros.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD", cargoJefe);
            }
            else if(espIngresos){
            	reporte = "002749CerRetenIDCBIS";
            	parametros.put(cTextoFormatoCertificados,
                        textoFormatoCertRet);
            	parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial,"MMM dd/yyyy").toUpperCase());
                parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal,"MMM dd/yyyy").toUpperCase());
                parametros.put("PR_CIUDAD", ciudadPago);

            }
            else {
                reporte = especialBimestral(reemplazar, parametros, strFormato,
                                strFormatoBimestral, formatoCertRetEsp,
                                firmaCertRet, cargoCertRet,
                                cargoCertRet2, firmaCertRet2, fechaIni,
                                fechaFin, claseRet, cPeriodo,
                                textoFormatoCertRet, tituloCertRet);
            }
        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return reporte;
    }
    

    private String formato220(HashMap<String, Object> reemplazar,
        Map<String, Object> parametros) {
        String reporte;
        try {
            if (formato220) {
            	 // INI_7715646 CONTABILIDAD (13/07/2022 MROSERO) 
            	String filtroNitONombre = porNit ? "TERCERO.NIT" : "TERCERO.NOMBRE";
            	 // FIN_7715646 CONTABILIDAD (13/07/2022 MROSERO) 
                PrepararReporte prepararReporte = new PrepararReporte();
                Registro rs = prepararReporte.obtenerDatosAnoDian(compania,
                                Integer.parseInt(anio));

                reporte = SysmanFunciones.nvl(rs.getCampos().get(
                                CertificadosRetencionControladorEnum.FRMDIAN
                                                .getValue()),
                                "001993DIAN2018")
                                .toString();

                reemplazar.put("fecha2",
                                SysmanFunciones.formatearFecha(fechaFinal));

                reemplazar.put("fecha1",
                                SysmanFunciones.formatearFecha(fechaInicial));

                reemplazar.put("fechaExpedicion",
                                SysmanFunciones.formatearFecha(fecha));

                reemplazar.put("modulo", modulo);
                reemplazar.put("anio", anio);
             // INI_7715646 CONTABILIDAD (13/07/2022 MROSERO)   
                reemplazar.put(cFiltroNitOnombre, filtroNitONombre);
             // FIN_7715646 CONTABILIDAD (13/07/2022 MROSERO)   
                reemplazar.put("cedulaInicial", tercero1);
                reemplazar.put("cedulaFinal", tercero1);
                
                
                // PARAMETROS PARA LAS CUENTAS PAGOS 220 (002251DIAN2020) - KEVIN MARTINEZ
                reemplazar.put("cuentasPagosSolidariosCovid", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 SOLIDARIOS COVID",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                
                reemplazar.put("cuentasPagosSalariosEE", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 SALARIOS EMOLUMENTOS ECLESIASTICOS",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                reemplazar.put("cuentasPagosHonorarios", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 HONORARIOS",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                reemplazar.put("cuentasPagosServicios", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 SERVICIOS",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                reemplazar.put("cuentasPagosComisiones", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 COMISIONES",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                reemplazar.put("cuentasPagosPrestacionesSociales", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 PRESTACIONES SOCIALES",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                reemplazar.put("cuentasPagosViaticos", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 VIATICOS",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                reemplazar.put("cuentasPagosGastosRepresentacion", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 GASTOS DE REPRESENTACION",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                reemplazar.put("cuentasPagosCompensaciones", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 COMPENSACIONES POR EL TRABAJO ASOCIADO COOPERATIVO",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                reemplazar.put("cuentasPagosOtros", SysmanFunciones.colocarComillas(
                		SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "CUENTAS PAGOS 220 OTROS PAGOS",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "")
                		));
                
                
                // FIN PARAMENTROS PARA LAS CUENTAS PAGOS 220
                
                //// DABERIA Ir REMPLAZOS FALTANTES
                reemplazar.put("condDCTO", " ");

                String parValorPorcentaje = SysmanFunciones.nvlStr(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "VALOR PORCENTAJE APLICAR PROCESO RETEFUENTE",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "0");

                reemplazar.put("valorPorcentaje", parValorPorcentaje);

                if (ejecutarFCMINIT(312) != null) {
                    reemplazar.put("nit", SysmanFunciones.concatenar("'",
                                    ejecutarFCMINIT(312), "'"));
                }
                else {
                    reemplazar.put("nit", " ");
                }

                if (ejecutarFCMINIT(1) != null) {
                    reemplazar.put("dc", SysmanFunciones.concatenar("'",
                                    ejecutarFCMINIT(1), ""));
                }
                else {
                    reemplazar.put("dc", " ");
                }

                String pRCodigoCiudad = SessionUtil.getCompaniaIngreso()
                                .getCodigoCiudad();
                String pRCodigoDepartamento = SessionUtil.getCompaniaIngreso()
                                .getCodigoDepartamento();
                String pRCiudadRetencion = SessionUtil.getCompaniaIngreso()
                                .getCiudad();
                String pRRazonSocialRetenedor = SessionUtil
                                .getCompaniaIngreso().getNombre();
                String pRDvRetenedor = SysmanFunciones
                                .extraerDigitoVerificacion(SessionUtil
                                                .getCompaniaIngreso().getNit());
                String pRNitRetenedor = SysmanFunciones.extraerNIT(
                                SessionUtil.getCompaniaIngreso().getNit());
                String pRMonedaValorIngressPatrimoniosParaCertificadoUVT;

                pRMonedaValorIngressPatrimoniosParaCertificadoUVT = SysmanFunciones
                                .moneda(Double.parseDouble(ejbSysmanUtil
                                                .consultarParametro(compania,
                                                                cLimiteIngresosPatrimonio,
                                                                SessionUtil.getModulo(),
                                                                new Date(),
                                                                false)),
                                                1);

                String pRValorIngressPatrimoniosParaCertificadoUVT = ejbSysmanUtil
                                .consultarParametro(compania,
                                                cLimiteIngresosPatrimonio,
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRValorIngresosPatrimoniosParaCertificado = ejbSysmanUtil
                                .consultarParametro(compania,
                                                "VALOR INGRESOS PATRIMONIOS PARA CERTIFICADO",
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRMonedaValorIngressSuperioresParaCertificadoUVT = SysmanFunciones
                                .moneda(Double.parseDouble(ejbSysmanUtil
                                                .consultarParametro(compania,
                                                                "LIMITE INGRESOS SUPERIORES PARA CERTIFICADO UVT",
                                                                SessionUtil.getModulo(),
                                                                new Date(),
                                                                false)),
                                                1);
                String pRValorIngressSuperioresParaCertificadoUVT = ejbSysmanUtil
                                .consultarParametro(compania,
                                                cLimiteIngresosPatrimonio,
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRValorIngresosSuperioresParaCertificado = ejbSysmanUtil
                                .consultarParametro(compania,
                                                "VALOR INGRESOS SUPERIORES PARA CERTIFICADO",
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRMonedaValorConsumsConTarjParaCertificadoUVT = SysmanFunciones
                                .moneda(Double.parseDouble(ejbSysmanUtil
                                                .consultarParametro(compania,
                                                                "LIMITE CONSUMOS CON TARJ PARA CERTIFICADO UVT",
                                                                SessionUtil.getModulo(),
                                                                new Date(),
                                                                false)),
                                                1);
                String pRValorConsumsConTarjParaCertificadoUVT = ejbSysmanUtil
                                .consultarParametro(compania,
                                                "LIMITE CONSUMOS CON TARJ PARA CERTIFICADO UVT",
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRValorConsumosConTarjetasParaCertificado = ejbSysmanUtil
                                .consultarParametro(compania,
                                                "VALOR CONSUMOS CON TARJETAS PARA CERTIFICADO",
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRMonedaValorTotalComprasYConParaCertificadoUVT = SysmanFunciones
                                .moneda(Double.parseDouble(ejbSysmanUtil
                                                .consultarParametro(compania,
                                                                "LIMITE TOTAL COMPRAS Y CON PARA CERTIFICADO UVT",
                                                                SessionUtil.getModulo(),
                                                                new Date(),
                                                                false)),
                                                1);
                String pRValorTotalComprasYConParaCertificadoUVT = ejbSysmanUtil
                                .consultarParametro(compania,
                                                "LIMITE TOTAL COMPRAS Y CON PARA CERTIFICADO UVT",
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRValorTotalComprasYConsumosParaCertificado = ejbSysmanUtil
                                .consultarParametro(compania,
                                                "VALOR TOTAL COMPRAS Y CONSUMOS PARA CERTIFICADO",
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRMonedaValorConsigBancariasParaCertificadoUVT = SysmanFunciones
                                .moneda(Double.parseDouble(ejbSysmanUtil
                                                .consultarParametro(compania,
                                                                "LIMITE CONSIG BANCARIAS PARA CERTIFICADO UVT",
                                                                SessionUtil.getModulo(),
                                                                new Date(),
                                                                false)),
                                                1);

                String pRValorConsigBancariasParaCertificadoUVT = ejbSysmanUtil
                                .consultarParametro(compania,
                                                "LIMITE CONSIG BANCARIAS PARA CERTIFICADO UVT",
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRValorConsignacionesBancariasParaCertificado = ejbSysmanUtil
                                .consultarParametro(compania,
                                                "VALOR CONSIGNACIONES BANCARIAS PARA CERTIFICADO",
                                                SessionUtil.getModulo(),
                                                new Date(), false);

                String pRCedulaDeQuiemFirmaCertificadoDIAN = SysmanFunciones
                                .nvl(rs.getCampos().get(
                                                CertificadosRetencionControladorEnum.CEDULADIAN
                                                                .getValue()),
                                                "")
                                .toString();

                String pRNombreDeQuienFirmaCertificadoDIAN = SysmanFunciones
                                .nvl(rs.getCampos().get(
                                                CertificadosRetencionControladorEnum.NOMBREDIAN
                                                                .getValue()),
                                                "")
                                .toString();

                parametros.put("PR_CODIGO_CIUDAD", pRCodigoCiudad);
                parametros.put("PR_CODIGO_DEPARTAMENTO",
                                pRCodigoDepartamento);
                parametros.put("PR_CIUDAD_RETENCION", pRCiudadRetencion);
                parametros.put("PR_RAZON_SOCIAL_RETENEDOR",
                                pRRazonSocialRetenedor);
                parametros.put("PR_DV_RETENEDOR", pRDvRetenedor);
                parametros.put("PR_NIT_RETENEDOR", pRNitRetenedor);
                parametros.put("PR_ANO_GRAVABLE", anio);
                int anoint = Integer.parseInt(anio);
                if (anoint >= 2019) {
               parametros.put("PR_NUMERO_FORMATO",
                                obtenerRuta("Formato2202019.png"));

                parametros.put("PR_LOGO_DIAN", obtenerRuta("DIAN2019.png"));
                
                } else {
                parametros.put("PR_NUMERO_FORMATO", obtenerRuta("Formato220.png"));
                parametros.put("PR_LOGO_DIAN", obtenerRuta("DIAN2013.png"));
                }
                parametros.put("PR_FORMS_CERTIFICADOS_ORIGINAL",
                                "original".toUpperCase());
                parametros.put("PR_MONEDA_VALOR_INGRESS_PATRIMONIOS_PARA_CERTIFICADO_UVT",
                                pRMonedaValorIngressPatrimoniosParaCertificadoUVT);
                parametros.put("PR_MONEDA_VALOR_CONSUMS_CON_TARJ_PARA_CERTIFICADO_UVT",
                                pRMonedaValorConsumsConTarjParaCertificadoUVT);
                parametros.put("PR_MONEDA_VALOR_INGRESS_SUPERIORES_PARA_CERTIFICADO_UVT",
                                pRMonedaValorIngressSuperioresParaCertificadoUVT);
                parametros.put("PR_MONEDA_VALOR_TOTAL_COMPRAS_Y_CON_PARA_CERTIFICADO_UVT",
                                pRMonedaValorTotalComprasYConParaCertificadoUVT);
                parametros.put("PR_MONEDA_VALOR_CONSIG_BANCARIAS_PARA_CERTIFICADO_UVT",
                                pRMonedaValorConsigBancariasParaCertificadoUVT);
                parametros.put("PR_VALOR_INGRESOS_PATRIMONIOS_PARA_CERTIFICADO",
                                pRValorIngresosPatrimoniosParaCertificado);
                parametros.put("PR_VALOR_INGRESS_PATRIMONIOS_PARA_CERTIFICADO_UVT",
                                pRValorIngressPatrimoniosParaCertificadoUVT);
                parametros.put("PR_VALOR_INGRESOS_SUPERIORES_PARA_CERTIFICADO",
                                pRValorIngresosSuperioresParaCertificado);
                parametros.put("PR_VALOR_INGRESS_SUPERIORES_PARA_CERTIFICADO_UVT",
                                pRValorIngressSuperioresParaCertificadoUVT);
                parametros.put("PR_VALOR_CONSUMOS_CON_TARJETAS_PARA_CERTIFICADO",
                                pRValorConsumosConTarjetasParaCertificado);
                parametros.put("PR_VALOR_CONSUMS_CON_TARJ_PARA_CERTIFICADO_UVT",
                                pRValorConsumsConTarjParaCertificadoUVT);
                parametros.put("PR_VALOR_TOTAL_COMPRAS_Y_CONSUMOS_PARA_CERTIFICADO",
                                pRValorTotalComprasYConsumosParaCertificado);
                parametros.put("PR_VALOR_TOTAL_COMPRAS_Y_CON_PARA_CERTIFICADO_UVT",
                                pRValorTotalComprasYConParaCertificadoUVT);
                parametros.put("PR_VALOR_CONSIGNACIONES_BANCARIAS_PARA_CERTIFICADO",
                                pRValorConsignacionesBancariasParaCertificado);
                parametros.put("PR_VALOR_CONSIG_BANCARIAS_PARA_CERTIFICADO_UVT",
                                pRValorConsigBancariasParaCertificadoUVT);
                parametros.put("PR_CEDULA_DE_QUIEN_FIRMA_CERTIFICADO_DIAN",
                                pRCedulaDeQuiemFirmaCertificadoDIAN);
                parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_CERTIFICADO_DIAN",
                                pRNombreDeQuienFirmaCertificadoDIAN);
                parametros.put("PR_FECHA_EXPEDICION",
        						fecha);

                return reporte;
            }
        }
        catch (NumberFormatException | SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return null;
    }

    public String obtenerRuta(String imagen) {
        String imagenRuta = null;
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            Registro ruta = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CertificadosRetencionControladorUrlEnum.URL2292
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
            String registroRuta = ruta.getCampos().get("RUTA_IMAGEN")
                            .toString();
            imagenRuta = SysmanFunciones.concatenar(registroRuta.substring(0,
                            registroRuta.lastIndexOf(File.separator) + 1),
                            imagen);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return imagenRuta;
    }

    private String ejecutarFCMINIT(int opcion) {
        String rta = null;
        try {
            rta = ejbSysmanUtil.formatearNitEntidad(compania, opcion);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rta;
    }

    private String sinDatos(HashMap<String, Object> reemplazar,
        String filtroNitONombre, String codPostal, String textoFormatoCertRet,
        Map<String, Object> parametros) {
        String reporte;
        if (sinDatos) {
            reporte = "000603ICerRetencionesNODATOS";
            reemplazar.put(cFiltroNitOnombre, filtroNitONombre);
            reemplazar.put(cTercero1, tercero1);
            reemplazar.put("tercero2", tercero2);
            reemplazar.put("codigoPostal",
                            codPostal == null ? "" : codPostal);
            parametros.put(cTextoFormatoCertificados,
                            textoFormatoCertRet);

            return reporte;
        }

        return null;
    }

    private String especialBimestral(HashMap<String, Object> reemplazar,
        Map<String, Object> parametros, String strFormato,
        String strFormatoBimestral,
        String formatoCertRetEsp, String firmaCertRet, String cargoCertRet,
        String cargoCertRet2, String firmaCertRet2, String fechaIni,
        String fechaFin, String claseRet, String cPeriodo,
        String textoFormatoCertRet, String tituloCertRet) {
        String reporte;
        if (especial) {
            if (bimestral) {
                reporte = "000614ICerRetencionesCCB";
                reemplazar.put("cPeriodo", cPeriodo);
                reemplazar.put("discriminado", discriminado ? -1 : 0);
                reemplazar.put("valorPagos", valorPagos ? -1 : 0);
                parametros.put(cTextoFormatoCertificados,
                                textoFormatoCertRet);
                parametros.put(cPRCentrarFirma, centrarFirma);
                parametros.put(cTituloEnCertificados,
                                tituloCertRet);
                parametros.put(cFechaInicial, fechaIni);
                parametros.put(cFechaFinal, fechaFin);
                parametros.put(cFirmaCertificadoRetencion,
                                firmaCertRet);
                parametros.put(cCargoCertificadoRetencion,
                                cargoCertRet);

                return reporte;
            }
            else {
                reporte = formatoCertRet(parametros, formatoCertRetEsp,
                                reemplazar, textoFormatoCertRet, tituloCertRet,
                                claseRet, firmaCertRet, cargoCertRet, fechaIni,
                                fechaFin, firmaCertRet2, cargoCertRet2);

                return reporte;
            }
        }
        else {
            if (!"".equals(strFormato == null ? "" : strFormato)
                && !"".equals(strFormatoBimestral == null ? ""
                    : strFormatoBimestral)) {
                if (bimestral) {
                    reporte = strFormatoBimestral;
                    parametros.put(cTextoFormatoCertificados,
                                    textoFormatoCertRet);
                    return reporte;
                }
                else {
                    reporte = discriminadoPorcentaje(parametros, firmaCertRet,
                                    tituloCertRet, fechaIni, fechaFin,
                                    strFormato, textoFormatoCertRet);
                    return reporte;
                }
            }
            else {
                if (bimestral) {
                    reporte = "000622ICerRetencionesB";
                    reemplazar.put("discriminado", discriminado ? -1 : 0);
                    reemplazar.put("valorPagos", valorPagos ? -1 : 0);
                    parametros.put(cPRCentrarFirma, centrarFirma);
                    parametros.put(cFirmaCertificadoRetencion,
                                    firmaCertRet);
                    parametros.put(cCargoCertificadoRetencion,
                                    cargoCertRet);
                    parametros.put(cFechaInicial, fechaIni);
                    parametros.put(cFechaFinal, fechaFin);

                    return reporte;

                }
                else {
                    reporte = "000621ICerRetenciones";
                    reemplazar.put("discriminado", discriminado ? -1 : 0);
                    reemplazar.put("valorPagos", valorPagos ? -1 : 0);
                    parametros.put(cPRCentrarFirma, centrarFirma);
                    parametros.put(cCargoCertificadoRetencion,
                                    cargoCertRet);
                    parametros.put(cFirmaCertificadoRetencion,
                                    firmaCertRet);
                    parametros.put(cFechaInicial, fechaIni);
                    parametros.put(cFechaFinal, fechaFin);
                    parametros.put("PR_CIUDAD", ciudadPago);


                    return reporte;

                }
            }
        }

    }

    private String formatoCertRet(Map<String, Object> parametros,
        String formatoCertRetEsp, HashMap<String, Object> reemplazar,
        String textoFormatoCertRet, String tituloCertRet, String claseRet,
        String firmaCertRet, String cargoCertRet, String fechaIni,
        String fechaFin, String firmaCertRet2, String cargoCertRet2) {
        String reporte;

        if ("SI".equals(formatoCertRetEsp == null ? "SI"
            : formatoCertRetEsp)) {
            reporte = "000620ICerRetencionesCCMET";
            reemplazar.put("discriminado", discriminado ? -1 : 0);
            reemplazar.put("valorPagos", valorPagos ? -1 : 0);
            parametros.put(cTextoFormatoCertificados,
                            textoFormatoCertRet);
            parametros.put(cTituloEnCertificados,
                            tituloCertRet);
            parametros.put("PR_CLASERET", "RET".equals(
                            claseRet));
            parametros.put(cPRCentrarFirma, centrarFirma);
            parametros.put(cFirmaCertificadoRetencion,
                            firmaCertRet);
            parametros.put(cCargoCertificadoRetencion,
                            cargoCertRet);
            parametros.put(cFechaInicial, fechaIni);
            parametros.put(cFechaFinal, fechaFin);

        }
        else {
            reporte = "000619ICerRetencionesCC";
            // VARIABLES COMUNES
            reemplazar.put("discriminado", discriminado ? -1 : 0);
            reemplazar.put("valorPagos", valorPagos ? -1 : 0);
            // Pï¿½RAMETROS COMUNES
            parametros.put("PR_CIUDAD", ciudadPago);
            parametros.put(cPRCentrarFirma, centrarFirma);
            parametros.put("PR_FIRMA_2VISIBLE",
                            (firmaCertRet2 != null)
                                && !"".equals(firmaCertRet2)
                                    ? true
                                    : false);

            parametros.put("PR_CARGO_2_EN_CERTIFICADO_RETENCION",
                            cargoCertRet2);
            parametros.put("PR_FIRMA_2_EN_CERTIFICADO_RETENCION",
                            firmaCertRet2);
            parametros.put(cTituloEnCertificados,
                            tituloCertRet);
            parametros.put(cFechaInicial, fechaIni);
            parametros.put(cFechaFinal, fechaFin);
            parametros.put(cFirmaCertificadoRetencion,
                            firmaCertRet);
            parametros.put(cCargoCertificadoRetencion,
                            cargoCertRet);
            

        }

        return reporte;
    }

    private String discriminadoPorcentaje(
        Map<String, Object> parametros, String firmaCertRet,
        String tituloCertRet, String fechaIni, String fechaFin,
        String strFormato, String textoFormatoCertRet) {
        String reporte;
        if (discriminadoPorcentaje) {
            reporte = "000610ICerRetencionesYOPAL";
            parametros.put(cPRCentrarFirma, centrarFirma);
            parametros.put(cFirmaCertificadoRetencion,
                            firmaCertRet);

            parametros.put(cTituloEnCertificados,
                            tituloCertRet);
            parametros.put(cFechaInicial, fechaIni);
            parametros.put(cFechaFinal, fechaFin);

        }
        else {
            reporte = strFormato;

            parametros.put("PR_TIPORETENCIONDE",
                            tipoRetencion == null ? " "
                                : " DE ");
            parametros.put(cTextoFormatoCertificados,
                            textoFormatoCertRet);
        }

        return reporte;
    }

    public boolean validarVacios() {
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB48"));
            return true;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB50"));
            return true;
        }
        if (fecha == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB51"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB52"));
            return true;
        }

        return false;
    }

    public boolean validarVaciosDos() {
        if (SysmanFunciones.validarVariableVacio(tercero1)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB53"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(tercero2)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB54"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB55"));
            return true;
        }

        return false;
    }

    public boolean validarVaciosTres() {
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB56"));
            return true;
        }
        if ((!formato220 || !sinDatos)
            && SysmanFunciones.validarVariableVacio(tipoRetencion)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB57"));
            return true;
        }

        return false;
    }

    /**
     * Metodo que permite generar los reportes.
     *
     * @param formato
     * recibe el formato segï¿½n el que se desee generar desde el
     * botï¿½n.
     */
    private void generaInforme(ReportesBean.FORMATOS formato) {
        if (validarVacios()
            || validarVaciosDos()
            || validarVaciosTres()) {
            return;
        }

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = getReporte(reemplazar, parametros);
        reemplazar.put("discriminado", discriminado ? -1 : 0);
        reemplazar.put("valorPagos", valorPagos ? -1 : 0);

        // MANEJO DE PARAMETROS DEL REPORTE
        
    	Reporteador.resuelveConsulta(reporte,
                    Integer.parseInt(SessionUtil.getModulo()),
                    reemplazar, parametros);
        
        try {
            PermiteVerFirmaCertificadoRetencion = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE VER FIRMA VERIFICAR",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO")
                            .toString();

            parametros.put("PR_PERMITE_VER_FIRMA_VERIFICAR",
                            PermiteVerFirmaCertificadoRetencion.equals("SI")
                                ? true
                                : false);

            firmaCertRet = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CERTIFICADO RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            cargoCertRet = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CERTIFICADO RETENCION",
                            SessionUtil.getModulo(), new Date(), false);

            NombreVerificaCertificadoRetencion = ejbSysmanUtil
                            .consultarParametro(
                                            compania,
                                            "NOMBRE QUIEN VERIFICA CERTIFICADO RETENCION",
                                            SessionUtil.getModulo(), new Date(),
                                            false);

            CargoVerificaCertificadoRetencion = ejbSysmanUtil
                            .consultarParametro(
                                            compania,
                                            "CARGO QUIEN VERIFICA CERTIFICADO RETENCION",
                                            SessionUtil.getModulo(), new Date(),
                                            false);

            parametros.put(cCargoCertificadoRetencion,
                            cargoCertRet);
            parametros.put(cFirmaCertificadoRetencion,
                            firmaCertRet);
            parametros.put(cNombreVerificaCertificadoRetencion,
                            NombreVerificaCertificadoRetencion);
            parametros.put(cCargoVerificaCertificadoRetencion,
                            CargoVerificaCertificadoRetencion);

            parametros.put("PR_CENTRARFIRMA", centrarFirma);
            
            parametros.put("PR_FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));

            // INI_7715646 CONTABILIDAD (13/07/2022 MROSERO)      
            int anoini = SysmanFunciones.ano(fechaInicial);
            int mesini = SysmanFunciones.mes(fechaInicial);
            int diaini = SysmanFunciones.dia(fechaInicial);
            int anofin = SysmanFunciones.ano(fechaFinal);
            int mesfin = SysmanFunciones.mes(fechaFinal);
            int diafin = SysmanFunciones.dia(fechaFinal);
             
             parametros.put("PR_ANOINI",anoini);
             parametros.put("PR_MESINI",mesini);
             parametros.put("PR_DIAINI",diaini);
             
             parametros.put("PR_ANOFIN", anofin);
             parametros.put("PR_MESFIN",mesfin);
             parametros.put("PR_DIAFIN",diafin);
             // FIN_7715646 CONTABILIDAD (13/07/2022 MROSERO)            
            
            
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException | ParseException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String noCardinales(int unNumero) {

        String noCardinales = "";

        switch (unNumero) {

        case 2:
            noCardinales = "BI";
            break;
        case 3:
            noCardinales = "TRI";
            break;
        case 4:
            noCardinales = "CUATRI";
            break;
        case 6:
            noCardinales = "SE";
            break;
        default:
            break;

        }
        return noCardinales;
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaCuentaInicial();
        cuentaInicial = "";
        cuentaFinal = "";
        nombreCuentaInicial = "";
        nombreCuentaFinal = "";
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEspecial() {
        // <CODIGO_DESARROLLADO>
        if (especial) {
            conPeriodo = false;
        }
        sinDatos = false;
        formato220 = false;
        discriminadoPorcentaje = false;
        if (porNit) {
            tercero2Visible = true;
        }
        else {
            terceroNFinalVisible = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCentrarFirma() {
        // <CODIGO_DESARROLLADO>
        if (centrarFirma) {
            conPeriodo = false;
            sinDatos = false;
            formato220 = false;
            formato350 = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarBimestral() {
        // <CODIGO_DESARROLLADO>
        if (bimestral) {
            conPeriodo = false;
        }
        sinDatos = false;
        conPeriodo = false;
        formato350 = false;
        formato220 = false;
        if (porNit) {
            tercero2Visible = true;
        }
        else {
            terceroNFinalVisible = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarconperiodo() {
        // <CODIGO_DESARROLLADO>
        if (conPeriodo) {
            especial = false;
            centrarFirma = false;
            bimestral = false;
        }
        formato220 = false;
        sinDatos = false;
        discriminadoPorcentaje = false;
        especial = false;
        bimestral = false;
        formato350 = false;
        if (porNit) {
            tercero2Visible = true;  
        }
        else {
            terceroNFinalVisible = true;
        }
        tercero2Visible = true;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTitulo() {
        // <CODIGO_DESARROLLADO>
        sinDatos = false;
        formato220 = false;
        formato350 = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPorNit() {
        // <CODIGO_DESARROLLADO>
        if (porNit) {
            if (formato220) {
                tercero2Visible = false;
                tercero2Etq = false;
                terceroNitVisible = true;
                terceroNVisible = false;
                terceroNFinalVisible = false;
                cargarListaTercero1();
            }
            else {
                terceroNVisible = false;
                terceroNFinalVisible = false;
                terceroNitVisible = true;
                tercero2Visible = true;
                cargarListaTercero1();
            }
        }
        else {
            if (formato220) {
                tercero2Visible = false;
                tercero2Etq = false;
                terceroNitVisible = false;
                terceroNVisible = true;
                terceroNFinalVisible = false;

                cargarListaterceroNombreInicial();
            }
            else {
                tercero2Visible = false;
                terceroNitVisible = false;
                terceroNVisible = true;
                terceroNFinalVisible = true;

                cargarListaterceroNombreInicial();
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFormato220() {
        // <CODIGO_DESARROLLADO>
        if (formato220) {
            tercero2Etq = false;
            if (porNit) {
                tercero2Visible = false;
                cargarListaTercero1();
            }
            else {
                terceroNFinalVisible = false;
                cargarListaterceroNombreInicial();
            }
        }
        else {
            tercero2Etq = true;
            if (porNit) {
                tercero2Visible = true;
                terceroNFinalVisible = false;
            }
            else {
                terceroNFinalVisible = true;
                tercero2Visible = false;
            }
        }
        sinDatos = false;
        conPeriodo = false;
        discriminadoPorcentaje = false;
        discriminado = false;
        especial = false;
        bimestral = false;
        formato350 = false;
        centrarFirma = false;
        titulo = false;
        valorPagos = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDiscriminado() {
        // <CODIGO_DESARROLLADO>
        sinDatos = false;
        formato220 = false;
        formato350 = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarvalorpagos() {
        // <CODIGO_DESARROLLADO>
        sinDatos = false;
        formato220 = false;
        formato350 = false;

        // </CODIGO_DESARROLLADO>
    }

    public void cambiardiscriminadoporcentaje() {
        // <CODIGO_DESARROLLADO>
        if (porNit) {
            tercero2Visible = true;
        }
        else {
            terceroNFinalVisible = true;
        }
        sinDatos = false;
        conPeriodo = false;
        especial = false;
        bimestral = false;
        formato350 = false;
        formato220 = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarformato350() {
        // <CODIGO_DESARROLLADO>
        if (formato350) {
            if (porNit) {
                tercero2Visible = true;
            }
            else {
                terceroNFinalVisible = true;
            }
            formato220 = false;
            conPeriodo = false;
            discriminadoPorcentaje = false;
            especial = false;
            bimestral = false;
            titulo = false;
            centrarFirma = false;
            discriminado = false;
            sinDatos = false;
            valorPagos = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarsindatos() {
        // <CODIGO_DESARROLLADO>
        formato220 = false;
        conPeriodo = false;
        discriminadoPorcentaje = false;
        especial = false;
        bimestral = false;
        formato350 = false;
        discriminado = false;
        bimestral = false;
        valorPagos = false;
        centrarFirma = false;
        titulo = false;
        if (porNit) {
            tercero2Visible = true;
        }
        else {
            terceroNFinalVisible = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTercero1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero1 = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        cargarListaTercero2();
    }

    public void seleccionarFilaTercero2(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero2 = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
        nombreCuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), " ")
                        .toString();
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
        nombreCuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), " ")
                        .toString();
    }

    public void seleccionarFilaterceroNombreInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero1Nombre = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), " ")
                        .toString();
        tercero1 = tercero1Nombre;
        cargarListaterceroNombreFinal();
        tercero2 = null;
    }

    public void seleccionarFilaterceroNombreFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero2Nombre = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), " ")
                        .toString();
        tercero2 = tercero2Nombre;
    }

    public boolean isTercero2Visible() {
        return tercero2Visible;
    }

    public void setTercero2Visible(boolean tercero2Visible) {
        this.tercero2Visible = tercero2Visible;
    }

    public String getTercero1() {
        return tercero1;
    }

    public void setTercero1(String tercero1) {
        this.tercero1 = tercero1;
    }

    public String getTercero2() {
        return tercero2;
    }

    public void setTercero2(String tercero2) {
        this.tercero2 = tercero2;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    public String getTipoRetencion() {
        return tipoRetencion;
    }

    public void setTipoRetencion(String tipoRetencion) {
        this.tipoRetencion = tipoRetencion;
    }

    public String getConsignadaEn() {
        return consignadaEn;
    }

    public void setConsignadaEn(String consignadaEn) {
        this.consignadaEn = consignadaEn;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public RegistroDataModelImpl getListaterceroNombreInicial() {
        return listaterceroNombreInicial;
    }

    public void setListaterceroNombreInicial(
        RegistroDataModelImpl listaterceroNombreInicial) {
        this.listaterceroNombreInicial = listaterceroNombreInicial;
    }

    public RegistroDataModelImpl getListaterceroNombreFinal() {
        return listaterceroNombreFinal;
    }

    public void setListaterceroNombreFinal(
        RegistroDataModelImpl listaterceroNombreFinal) {
        this.listaterceroNombreFinal = listaterceroNombreFinal;
    }

    public boolean isEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public boolean isCentrarFirma() {
        return centrarFirma;
    }

    public void setCentrarFirma(boolean centrarFirma) {
        this.centrarFirma = centrarFirma;
    }

    public boolean isBimestral() {
        return bimestral;
    }

    public void setBimestral(boolean bimestral) {
        this.bimestral = bimestral;
    }

    public boolean isConPeriodo() {
        return conPeriodo;
    }

    public void setConPeriodo(boolean conPeriodo) {
        this.conPeriodo = conPeriodo;
    }

    public boolean isTitulo() {
        return titulo;
    }

    public void setTitulo(boolean titulo) {
        this.titulo = titulo;
    }

    public boolean isPorNit() {
        return porNit;
    }

    public void setPorNit(boolean porNit) {
        this.porNit = porNit;
    }

    public boolean isDiscriminado() {
        return discriminado;
    }

    public void setDiscriminado(boolean discriminado) {
        this.discriminado = discriminado;
    }

    public boolean isValorPagos() {
        return valorPagos;
    }

    public void setValorPagos(boolean valorPagos) {
        this.valorPagos = valorPagos;
    }

    public boolean isFormato220() {
        return formato220;
    }

    public void setFormato220(boolean formato220) {
        this.formato220 = formato220;
    }

    public boolean isDiscriminadoPorcentaje() {
        return discriminadoPorcentaje;
    }

    public void setDiscriminadoPorcentaje(boolean discriminadoPorcentaje) {
        this.discriminadoPorcentaje = discriminadoPorcentaje;
    }

    public boolean isFormato350() {
        return formato350;
    }

    public void setFormato350(boolean formato350) {
        this.formato350 = formato350;
    }

    public boolean isSinDatos() {
        return sinDatos;
    }

    public String getTercero1Nombre() {
        return tercero1Nombre;
    }

    public void setTercero1Nombre(String tercero1Nombre) {
        this.tercero1Nombre = tercero1Nombre;
    }

    public String getTercero2Nombre() {
        return tercero2Nombre;
    }

    public void setTercero2Nombre(String tercero2Nombre) {
        this.tercero2Nombre = tercero2Nombre;
    }

    public void setSinDatos(boolean sinDatos) {
        this.sinDatos = sinDatos;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombreCuentaInicial() {
        return nombreCuentaInicial;
    }

    public void setNombreCuentaInicial(String nombreCuentaInicial) {
        this.nombreCuentaInicial = nombreCuentaInicial;
    }

    public boolean isTerceroNFinalVisible() {
        return terceroNFinalVisible;
    }

    public void setTerceroNFinalVisible(boolean terceroNFinalVisible) {
        this.terceroNFinalVisible = terceroNFinalVisible;
    }

    public String getNombreCuentaFinal() {
        return nombreCuentaFinal;
    }

    public void setNombreCuentaFinal(String nombreCuentaFinal) {
        this.nombreCuentaFinal = nombreCuentaFinal;
    }

    public String getCiudadPago() {
        return ciudadPago;
    }

    public void setCiudadPago(String ciudadPago) {
        this.ciudadPago = ciudadPago;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isTerceroNVisible() {
        return terceroNVisible;
    }

    public void setTerceroNVisible(boolean terceroNVisible) {
        this.terceroNVisible = terceroNVisible;
    }

    public boolean isTerceroNitVisible() {
        return terceroNitVisible;
    }

    public boolean isTercero2Etq() {
        return tercero2Etq;
    }

    public void setTercero2Etq(boolean tercero2Etq) {
        this.tercero2Etq = tercero2Etq;
    }

    public void setTerceroNitVisible(boolean terceroNitVisible) {
        this.terceroNitVisible = terceroNitVisible;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaTipoRetencion() {
        return listaTipoRetencion;
    }

    public void setListaTipoRetencion(List<Registro> listaTipoRetencion) {
        this.listaTipoRetencion = listaTipoRetencion;
    }

    public List<Registro> getListaConsignadaEn() {
        return listaConsignadaEn;
    }

    public void setListaConsignadaEn(List<Registro> listaConsignadaEn) {
        this.listaConsignadaEn = listaConsignadaEn;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaTercero1() {
        return listaTercero1;
    }

    public void setListaTercero1(RegistroDataModelImpl listaTercero1) {
        this.listaTercero1 = listaTercero1;
    }

    public RegistroDataModelImpl getListaTercero2() {
        return listaTercero2;
    }

    public void setListaTercero2(RegistroDataModelImpl listaTercero2) {
        this.listaTercero2 = listaTercero2;
    }

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    @Override
    public void abrirFormulario() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();
    }

    @Override
    public boolean insertarAntes() {
        return false;
    }

    @Override
    public boolean insertarDespues() {
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        return false;
    }

    @Override
    public boolean actualizarDespues() {
        return false;
    }

    @Override
    public boolean eliminarAntes() {
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        return false;
    }

	public String getFechainicial1() {
		return fechainicial1;
	}

	public void setFechainicial1(String fechainicial1) {
		this.fechainicial1 = fechainicial1;
	}

	public String getFechafinal2() {
		return fechafinal2;
	}

	public void setFechafinal2(String fechafinal2) {
		this.fechafinal2 = fechafinal2;
	}

	/**
	 * @return the espIngresos
	 */
	public boolean isEspIngresos() {
		return espIngresos;
	}

	/**
	 * @param espIngresos the espIngresos to set
	 */
	public void setEspIngresos(boolean espIngresos) {
		this.espIngresos = espIngresos;
	}
	
	
}
