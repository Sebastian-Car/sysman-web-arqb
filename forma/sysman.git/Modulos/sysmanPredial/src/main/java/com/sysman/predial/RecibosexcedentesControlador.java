package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.RecibosexcedentesControladorEnum;
import com.sysman.predial.enums.RecibosexcedentesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
 * @version 2, 09/03/2017 15:26:29 -- Modificado por jrodriguezr
 * 
 * @version 3, 17/07/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class RecibosexcedentesControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String anuladoC;
    private final String preAnoC;
    private final String valorC;
    private final String cNumeroFactura;
    private final String cPreCod;
    private final String cCodigoPredio;
    // <DECLARAR_ATRIBUTOS>
    private String numeroFactura;
    private Date fechaExpedicion;
    private String codigoPredioAnular;
    private String valorAnular;
    private String nombreAnular;
    private String codigoPredioGenerar;
    private String nombreGenerar;
    private String resolucion;
    private String valorGenerar;
    private String anoCauso;
    private String anoExcedente;
    private Date fechaGenerar;
    private String opcionCodigo;
    private String banco;
    private String paquete;
    private String predio;
    private String barras;
    private String factura;
    private boolean dialogoVisible;
    private boolean dialogoVisibleUno;
    private boolean dialogoRegistro;
    private boolean dialogoAuxiliar;
    private boolean dialogoRecibo;
    private boolean dialogoRegistroPredio;
    private String textoEtiqueta;
    private String textoEtiquetaUno;
    private Date fecha;
    private boolean actualizacion;
    private boolean insercion;
    private StreamedContent archivoDescarga;
    
    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatro;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listanumeroFactura;
    private RegistroDataModelImpl listacodigoPredioG;
    private RegistroDataModelImpl listabanco;
    private RegistroDataModelImpl listapredio;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private Registro rsExcedente;
    private Registro rsUsuario;
    boolean pago;
    boolean anulado;
    String campos;
    String valores;
    String condicion;
    String codigoPredio;
    String facturaPago;
    int conExcedentes;

    /**
     * Creates a new instance of RecibosexcedentesControlador
     */
    public RecibosexcedentesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anuladoC = "ANULADO";
        preAnoC = "PREANO";
        valorC = "VALOR";
        cNumeroFactura = "NUMERO_FACTURA";
        cPreCod = "PRECOD";
        cCodigoPredio = "CODIGO_PREDIO";
        insercion=false;
        actualizacion=false;
        try {
            numFormulario = GeneralCodigoFormaEnum.RECIBOSEXCEDENTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            opcionCodigo = "1";
            fecha = new Date();
            fechaGenerar = new Date();

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RecibosexcedentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListanumeroFactura();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigoPredioG();
        cargarListabanco();
        cargarListapredio();
        cargarListanumeroFactura();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListanumeroFactura() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RecibosexcedentesControladorUrlEnum.URL5517.getValue());      
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listanumeroFactura = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, cNumeroFactura);
    }

    public void cargarListacodigoPredioG() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RecibosexcedentesControladorUrlEnum.URL6394.getValue());      
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(RecibosexcedentesControladorEnum.PARAM0.getValue(),"D");

        listacodigoPredioG = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, cPreCod);
        
    }

    public void cargarListabanco() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RecibosexcedentesControladorUrlEnum.URL7269.getValue());      
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listabanco = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "CODIGOBANCO");
    }

    public void cargarListapredio() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RecibosexcedentesControladorUrlEnum.URL7764.getValue());      
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listapredio = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, cCodigoPredio);
    }

    // </METODOS_CARGAR_LISTA>
    // Anular Recibo Excedente
    public void aceptarDGRE() {
        try {
            ejbPredialCuatro.anulaRecibosExcedentes(compania,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            numeroFactura, SessionUtil.getUser().getCodigo());
            dialogoVisible = false;
            dialogoVisibleUno = false;
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1152"));
            numeroFactura = null;
            codigoPredioAnular = null;
            nombreAnular = null;
            fechaExpedicion = null;
            valorAnular = null;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cancelarDGRE() {
        dialogoVisibleUno = false;
        JsfUtil.agregarMensajeError(
                        idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA));
    }

    public void aceptarDGNF() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(GeneralParameterEnum.DOCNUM.getName(), numeroFactura);
        
        try {
            Registro rsR = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL7080
                                                                            .getValue())
                                            .getUrl(), param));
            if (rsR != null) {
                pago = (boolean) rsR.getCampos().get("PAGO");
                anulado = (boolean) rsR.getCampos().get(anuladoC);
                if (!pago && !anulado) {
                    textoEtiquetaUno = idioma.getString("TB_TB1136");
                    dialogoVisible = false;
                    dialogoVisibleUno = true; // aceptarDG_RE
                }
                else {
                    dialogoVisibleUno = false;
                    anularRecExc();
                }
            }
            else {
                anularRecExc();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarDGNF() {
        dialogoVisible = false;
        JsfUtil.agregarMensajeError(
                        idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA));
    }

    public void anularRecExc() {
        dialogoVisible = false;
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            parametros.put(RecibosexcedentesControladorEnum.PARAM1.getValue(), numeroFactura);
            
            Parameter parameter = new Parameter();
            parameter.setFields(parametros);
            
            UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RecibosexcedentesControladorUrlEnum.URL10874.getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1152"));
            numeroFactura = null;
            codigoPredioAnular = null;
            nombreAnular = null;
            fechaExpedicion = null;
            valorAnular = null;
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(RecibosexcedentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    // <METODOS_BOTONES>
    public void oprimiranularRecibo() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            param.put(RecibosexcedentesControladorEnum.PARAM1.getValue(), numeroFactura);
            
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL6452
                                                                            .getValue())
                                            .getUrl(), param));
            
            if (rs != null) {
                textoEtiqueta = idioma.getString("TB_TB1134");
                textoEtiqueta = textoEtiqueta.replace("s$numeroFactura$s",
                                numeroFactura);
                dialogoVisible = true; // aceptarDG_NF
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirregistrarExcedente() { // REGISTRAR PAGO
                                              // EXCEDENTE
                                              // <CODIGO_DESARROLLADO>
        boolean indicador = false;
        if ("1".equals(opcionCodigo)
            && (SysmanFunciones.validarVariableVacio(barras)
                || (barras.length() <= 15))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB175"));
            indicador = true;
        }
        else if (SysmanFunciones.validarVariableVacio(predio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB176"));
            indicador = true;
        }
        else if (predio.length() != 15) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB177"));
            indicador = true;
        }
        else if (SysmanFunciones.validarVariableVacio(factura)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB178"));
            indicador = true;
        }
        else if ((factura.length() < 9) || (factura.length() > 10)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB179"));
            indicador = true;
        }

        if (!indicador) {
            pagoCaja();
        }

        // </CODIGO_DESARROLLADO>
    }

    // Registrar Pagos Excedentes
    private void pagoCaja() {
        if ("1".equals(opcionCodigo)) {
            if (barras.length() < 34) {
                codigoPredio = "0";
            }
            else {
                codigoPredio = barras.substring(19, 34);
            }
            if (barras.length() < 42) {
                facturaPago = "0";
            }
            else {
                facturaPago = barras.substring(34, 42);
                facturaPago = "EX" + facturaPago;
            }
        }
        else {
            codigoPredio = predio;
            facturaPago = factura;
        }
        try {
            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "CONCEPTO RECIBOS EXCEDENTES", modulo, new Date(),
                            false);
            
            if (parametro == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1156"));
                return;
            }
            
            conExcedentes = Integer.parseInt(ejbSysmanUtil.consultarParametro(
                            compania, "CONCEPTO RECIBOS EXCEDENTES", modulo,
                            new Date(), false));
            
            if (!("14,15,16,17,18,19,20")
                            .contains(String.valueOf(conExcedentes))) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1158"));
                return;
            }
            realizarPagoCaja(codigoPredio, facturaPago);
        }
        catch (NullPointerException | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(RecibosexcedentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        catch (NumberFormatException ex) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1157"));
            Logger.getLogger(RecibosexcedentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    private void realizarPagoCaja(String codigoPredio, String facturaPago) {
        // Validar Activado

        // Valida si el c�digo de predio registrado en Recibos
        // excedentes , se encuentra en Usuarios predial.
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        
        try {
            rsUsuario = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL2345
                                                                            .getValue())
                                            .getUrl(), param));
      
        
        if (rsUsuario == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1160"));
            return;
        }
        // Valida si la factura ingresada se encuentra registrada en
        // Recibos excedentes.
        param.remove(GeneralParameterEnum.CODIGO.getName());
        param.put(RecibosexcedentesControladorEnum.PARAM1.getValue(), facturaPago);
        rsExcedente = RegistroConverter.toRegistro(
                        requestManager.get(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        RecibosexcedentesControladorUrlEnum.URL1047
                                                                        .getValue())
                                        .getUrl(), param));
        if (rsExcedente == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1161"));
            return;
        }
        else {
            anulado = (boolean) rsExcedente.getCampos().get(anuladoC);
            pago = (boolean) rsExcedente.getCampos().get("PAGO");
            // Valida el estado de la factura, Si esta activa o no
            if (pago && !anulado) {
                // Revisar Recibo
                dialogoRegistro = true; // --> aceptarDG_REX
            }
            else {
                dialogoRegistro = false;
                recibosExcedentes();
            }
        }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    private void recibosExcedentes() {
        try {
            double total;
            String numFacturaEx = rsExcedente.getCampos().get(cNumeroFactura)
                            .toString();
            String codPredioEx = rsExcedente.getCampos().get(cCodigoPredio)
                            .toString();
            double avaluo = Double.parseDouble(
                            rsUsuario.getCampos().get("AVALUO_ANO").toString());
            int anoEx = Integer.parseInt(
                            rsExcedente.getCampos().get("ANO_CAUSO")
                                            .toString());
            anulado = Boolean.parseBoolean(
                            rsExcedente.getCampos().get(anuladoC).toString());

            if (anulado) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1168"));
                return;
            }
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PREDIO.getName(), codPredioEx);
            
            Registro rsDobles= RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL3056
                                                                            .getValue())
                                            .getUrl(), param));
            if (rsDobles == null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1169"));
                return;
            }
            param.remove(GeneralParameterEnum.PREDIO.getName());
            param.put(RecibosexcedentesControladorEnum.PARAM1.getValue(), facturaPago);
            
            Registro rsSumExcedentes = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL4587
                                                                            .getValue())
                                            .getUrl(), param));

            total = Double.parseDouble(
                            rsSumExcedentes.getCampos().get(valorC).toString());
            
            param.remove(RecibosexcedentesControladorEnum.PARAM1.getValue());
            param.put(RecibosexcedentesControladorEnum.PARAM2.getValue(), numFacturaEx);
            Registro rsRecibosPago = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL1752
                                                                            .getValue())
                                            .getUrl(), param));
            // Verifica si el Recibo de Pago existe
            if (rsRecibosPago != null) {

                // Si le da S� a la confirmai�n del dialogo debe
                // seguir con el proceso
                dialogoRecibo = true; // --> aceptarDG_RP
            }
            else {
                dialogoRecibo = false;
                insercion = true;
            }
            recibosExcedentesUno(numFacturaEx, codPredioEx,avaluo,anoEx, total);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void recibosExcedentesUno(String numFacturaEx, String codPredioEx, double avaluo, int anoEx, double total) {
        String numOrdenEx = SysmanConstantes.NUMERO_ORDEN_PREDIAL;

        StringBuilder builder = new StringBuilder();
        builder.append(" UN_COMPANIA       => '" + compania + "',");
        builder.append(" UN_NUMFACTURAEX   => '" + factura + "',");
        builder.append(" UN_CODIGOPREDIOEX => '" + codigoPredio + "',");
        builder.append(" UN_NUMEROORDEN    => '"
            + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "',");
        builder.append(" UN_BANCO          => '" + banco + "',");
        builder.append(" UN_PAQUETE        => '" + paquete + "',");
        builder.append(" UN_FECHA          => "
            + SysmanFunciones.formatearFecha(fecha) + ",");
        builder.append(" UN_USUARIO        => '"
            + SessionUtil.getUser().getCodigo() + "',");
        builder.append(" UN_CODIGOPREDIO   => '" + codigoPredio + "',");
        builder.append(" UN_NUMORDENEX     => '" + numOrdenEx + "',");
        builder.append(" UN_BARRAS         => "
            + (SysmanFunciones.validarVariableVacio(barras) ? "NULL"
                : "'" + barras + "'")
            + ",");
        builder.append(" UN_FACTURAPAGO    => '" + facturaPago + "'");
        String rta = null;
        try {
            rta = ejbPredialCuatro.getRecibosExcedentesUno(compania, factura,
                            codigoPredio, SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            banco, paquete, fecha,
                            SessionUtil.getUser().getCodigo(), codigoPredio,
                            numOrdenEx,
                            SysmanFunciones.validarVariableVacio(barras)
                                ? "NULL"
                                : barras,
                            facturaPago, String.valueOf(conExcedentes),
                            numFacturaEx, codPredioEx, anoEx,
                            String.valueOf(total), String.valueOf(avaluo),
                            insercion, actualizacion);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {

            if ((rta != null) && rta.contains("TB_TB")) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(rta));
            }
            barras = null;
            predio = null;
            banco = null;
            factura = null;
            paquete = null;
            dialogoAuxiliar = false;
        }

    }

    public void oprimirimprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (generarReciboExcedente()) {
            String nuevaFactura = generarFactura();
            getInforme(nuevaFactura);
            codigoPredioGenerar = null;
            nombreGenerar = null;
            resolucion = null;
            anoCauso = null;
            anoExcedente = null;
            valorGenerar = null;
            cargarListanumeroFactura();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarDGREX() {
        boolean reciboPago;
        boolean reciboAnulado;
        double valorEx = Double.parseDouble(
                        rsExcedente.getCampos().get(valorC).toString());
        // Verifica que el recibo de pago exista y que este pago.

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            param.put(GeneralParameterEnum.DOCNUM.getName(), facturaPago);
            
            Registro rsRecibos = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL7080
                                                                            .getValue())
                                            .getUrl(), param));
            
            if (rsRecibos != null) {
                verificarValorReciboExcedente(rsRecibos, valorEx);
            }
            else {
                // Verifica si existe registro en bancos det                
                Map<String, Object> param2 = new TreeMap<>();
                param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param2.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
                param2.put(RecibosexcedentesControladorEnum.PARAM3.getValue(), facturaPago);
                param2.put(RecibosexcedentesControladorEnum.PARAM4.getValue(), codigoPredio);
                
                Registro rsBancosDet = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                RecibosexcedentesControladorUrlEnum.URL7080
                                                                                .getValue())
                                                .getUrl(), param2));
        
                
                Map<String, Object> param3 = new TreeMap<>();
                param3.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param3.put(RecibosexcedentesControladorEnum.PARAM4.getValue(), codigoPredio);
                param3.put(GeneralParameterEnum.VALOR.getName(), valorEx);
                
                Registro rsReciboExeAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                RecibosexcedentesControladorUrlEnum.URL4928
                                                                                .getValue())
                                                .getUrl(), param3));
                if (rsBancosDet != null) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1166"));
                    recibosExcedentes();
                    dialogoRegistro = false;
                }
                // Verifica que no existan otros recibos de excedentes
                // pendientes para el mismo predio.
                else if (rsReciboExeAux != null) {

                    reciboPago = (boolean) rsReciboExeAux.getCampos()
                                    .get("PAGO");
                    reciboAnulado = (boolean) rsReciboExeAux.getCampos()
                                    .get(anuladoC);

                    if (!reciboPago && !reciboAnulado) {
                        JsfUtil.agregarMensajeError(
                                        idioma.getString("TB_TB1167"));
                        return;
                    }
                    recibosExcedentes();
                    dialogoRegistro = false;

                }
                else {
                    dialogoAuxiliar = true; // --> aceptarDG_RAUX

                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
       

    }

    private void verificarValorReciboExcedente(Registro rsRecibos,
        double valorEx) {
        boolean reciboPago = (boolean) rsRecibos.getCampos().get("PAGO");
        boolean reciboAnulado = (boolean) rsRecibos.getCampos().get(anuladoC);

        try {
            if (reciboPago && !reciboAnulado) {
                // Verifica que el valor de Recibos Excedentes sea
                // igual
                // al registrado en Recibos de Pagos
                double prevalRe = Double.parseDouble(
                                rsRecibos.getCampos().get("PREVAL").toString());
                if (Double.doubleToLongBits(valorEx) != Double
                                .doubleToLongBits(prevalRe)) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1162"));
                    return;
                }
                // Verifica que el registro en Pago_Bancos_Det exista

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                SysmanConstantes.NUMERO_ORDEN_PREDIAL);
                param.put(RecibosexcedentesControladorEnum.PARAM3.getValue(),
                                facturaPago);
                param.put(RecibosexcedentesControladorEnum.PARAM4.getValue(),
                                codigoPredio);

                Registro rsPagoBanco = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                RecibosexcedentesControladorUrlEnum.URL8754
                                                                                .getValue())
                                                .getUrl(), param));

                if (rsPagoBanco != null) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1163"));
                }
                else {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1164"));
                    return;
                }
            }
            if (reciboAnulado) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1165"));
                return;
            }
            recibosExcedentes();
            dialogoRegistro = false;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarDGREX() {
        dialogoRegistro = false;
        JsfUtil.agregarMensajeError(
                        idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA));
        return;
    }

    public void aceptarDGRAUX() {
            actualizacion=true;
            recibosExcedentes();
            dialogoRegistro = false;
            dialogoAuxiliar = false;
        
    }

    public void cancelarDGRAUX() {
        dialogoAuxiliar = false;

    }

    public void aceptarDGRP() {
        recibosExcedentesUno("","", 0,0,0);

    }

    public void cancelarDGRP() {
        dialogoRecibo = false;
        JsfUtil.agregarMensajeError(
                        idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA));

    }

    private void getInforme(String nuevaFactura) {

        try {
            String codEan;
            HashMap<String, Object> reemplazar = new HashMap<>();
            
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            param.put(RecibosexcedentesControladorEnum.PARAM4.getValue(),codigoPredioGenerar);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL9863
                                                                            .getValue())
                                            .getUrl(), param));
            
            
            codEan = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
                            compania, "CODIGO EAN", modulo, new Date(), false),
                            "").toString();
            String reporte = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO FACTURA EXCEDENTES", modulo, new Date(),
                            false);// 000843facturaexcedentes
            
            if (reporte == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3310"));
                return;
            }
            reemplazar.put("predio", codigoPredioGenerar);
            reemplazar.put("codigoEAN", codEan);
            reemplazar.put("valorFactura", rs.getCampos().get("C_TOTAL"));
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            reemplazar.put("numeroFactura", nuevaFactura);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);
            parametros.put("PR_STRNOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_STRNITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_LEYENDA_FACTURA_EXCEDENTES",
                            SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "LEYENDA FACTURA EXCEDENTES",
                                                            modulo, new Date(),
                                                            false),
                                            ""));
            parametros.put("PR_TOTALSUB", Double.parseDouble(
                            rs.getCampos().get("C_TOTAL").toString()));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean generarReciboExcedente() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(RecibosexcedentesControladorEnum.PARAM4.getValue(),codigoPredioGenerar);

            Registro rsFactura = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL1765
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsFactura != null) {
                dialogoRegistroPredio = true; // -->aceptarDG_RPR
            }
            else {
                dialogoRegistroPredio = false;
                return true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    private String generarFactura() {
        String rta = null;
        try {
            rta = ejbPredialCuatro.generarFacturaExcedente(compania,
                            codigoPredioGenerar,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,SessionUtil.getUser().getCodigo(),
                            nombreGenerar);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    public void aceptarDGRPR() {
        archivoDescarga = null;
        String nuevaFactura = generarFactura();
        if (nuevaFactura != null) {
            getInforme(nuevaFactura);
        }
    }

    public void cancelarDGRPR() {
        dialogoRegistroPredio = false;
        JsfUtil.agregarMensajeError(
                        idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA));
        return;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarMarco17() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfecha() {
        // <CODIGO_DESARROLLADO>
        if (fecha.after(new Date())) {
            JsfUtil.agregarMensajeError( idioma.getString("TB_TB3311"));
            fecha = new Date();
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDGNF() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDGRE() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDGREX() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDGRAUX() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDGRP() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDGRPR() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilanumeroFactura(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroFactura = registroAux.getCampos().get(cNumeroFactura)
                        .toString();
        if (registroAux.getCampos().get("FECHA_EXPEDICION") != null) {
            fechaExpedicion = (Date) registroAux.getCampos()
                            .get("FECHA_EXPEDICION");
        }
        codigoPredioAnular = registroAux.getCampos().get(cCodigoPredio)
                        .toString();
        nombreAnular = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        valorAnular = registroAux.getCampos().get(valorC).toString();
    }

    public void seleccionarFilacodigoPredioG(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoPredioGenerar = registroAux.getCampos().get(cPreCod).toString();
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            param.put(GeneralParameterEnum.CODIGO.getName(),codigoPredioGenerar);
            
            nombreGenerar = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecibosexcedentesControladorUrlEnum.URL4571
                                                                            .getValue())
                                            .getUrl(), param)).getCampos().get("NOMBRE").toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        
        resolucion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("OBSERVACIONES"), "")
                        .toString();
        valorGenerar = registroAux.getCampos().get(valorC).toString();
        anoExcedente = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ANO_EXCEDENTE"), "")
                        .toString();
        anoCauso = registroAux.getCampos().get(preAnoC).toString();
    }

    public void seleccionarFilabanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        banco = registroAux.getCampos().get("CODIGOBANCO").toString();
    }

    public void seleccionarFilapredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predio = registroAux.getCampos().get(cCodigoPredio).toString();
        factura = registroAux.getCampos().get("NUMERO_FACTURA").toString();

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Date getFechaExpedicion() {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(Date fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public String getCodigoPredioAnular() {
        return codigoPredioAnular;
    }

    public void setCodigoPredioAnular(String codigoPredioAnular) {
        this.codigoPredioAnular = codigoPredioAnular;
    }

    public String getValorAnular() {
        return valorAnular;
    }

    public void setValorAnular(String valorAnular) {
        this.valorAnular = valorAnular;
    }

    public String getNombreAnular() {
        return nombreAnular;
    }

    public void setNombreAnular(String nombreAnular) {
        this.nombreAnular = nombreAnular;
    }

    public String getCodigoPredioGenerar() {
        return codigoPredioGenerar;
    }

    public void setCodigoPredioGenerar(String codigoPredioGenerar) {
        this.codigoPredioGenerar = codigoPredioGenerar;
    }

    public String getNombreGenerar() {
        return nombreGenerar;
    }

    public void setNombreGenerar(String nombreGenerar) {
        this.nombreGenerar = nombreGenerar;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getValorGenerar() {
        return valorGenerar;
    }

    public void setValorGenerar(String valorGenerar) {
        this.valorGenerar = valorGenerar;
    }

    public String getAnoCauso() {
        return anoCauso;
    }

    public void setAnoCauso(String anoCauso) {
        this.anoCauso = anoCauso;
    }

    public String getAnoExcedente() {
        return anoExcedente;
    }

    public void setAnoExcedente(String anoExcedente) {
        this.anoExcedente = anoExcedente;
    }

    public Date getFechaGenerar() {
        return fechaGenerar;
    }

    public void setFechaGenerar(Date fechaGenerar) {
        this.fechaGenerar = fechaGenerar;
    }

    public String getOpcionCodigo() {
        return opcionCodigo;
    }

    public void setOpcionCodigo(String opcionCodigo) {
        this.opcionCodigo = opcionCodigo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getPaquete() {
        return paquete;
    }

    public void setPaquete(String paquete) {
        this.paquete = paquete;
    }

    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    public String getBarras() {
        return barras;
    }

    public void setBarras(String barras) {
        this.barras = barras;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    public String getTextoEtiqueta() {
        return textoEtiqueta;
    }

    public void setTextoEtiqueta(String textoEtiqueta) {
        this.textoEtiqueta = textoEtiqueta;
    }

    public boolean isDialogoVisibleUno() {
        return dialogoVisibleUno;
    }

    public void setDialogoVisibleUno(boolean dialogoVisibleUno) {
        this.dialogoVisibleUno = dialogoVisibleUno;
    }

    public String getTextoEtiquetaUno() {
        return textoEtiquetaUno;
    }

    public void setTextoEtiquetaUno(String textoEtiquetaUno) {
        this.textoEtiquetaUno = textoEtiquetaUno;
    }

    public boolean isDialogoAuxiliar() {
        return dialogoAuxiliar;
    }

    public void setDialogoAuxiliar(boolean dialogoAuxiliar) {
        this.dialogoAuxiliar = dialogoAuxiliar;
    }

    public boolean isDialogoRecibo() {
        return dialogoRecibo;
    }

    public void setDialogoRecibo(boolean dialogoRecibo) {
        this.dialogoRecibo = dialogoRecibo;
    }

    public boolean isDialogoRegistro() {
        return dialogoRegistro;
    }

    public void setDialogoRegistro(boolean dialogoRegistro) {
        this.dialogoRegistro = dialogoRegistro;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isDialogoRegistroPredio() {
        return dialogoRegistroPredio;
    }

    public void setDialogoRegistroPredio(boolean dialogoRegistroPredio) {
        this.dialogoRegistroPredio = dialogoRegistroPredio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListanumeroFactura() {
        return listanumeroFactura;
    }

    public RegistroDataModelImpl getListacodigoPredioG() {
        return listacodigoPredioG;
    }

    public void setListacodigoPredioG(RegistroDataModelImpl listacodigoPredioG) {
        this.listacodigoPredioG = listacodigoPredioG;
    }

    public void setListanumeroFactura(RegistroDataModelImpl listanumeroFactura) {
        this.listanumeroFactura = listanumeroFactura;
    }
    
    public RegistroDataModelImpl getListabanco() {
        return listabanco;
    }

    public void setListabanco(RegistroDataModelImpl listabanco) {
        this.listabanco = listabanco;
    }

    public RegistroDataModelImpl getListapredio() {
        return listapredio;
    }

    public void setListapredio(RegistroDataModelImpl listapredio) {
        this.listapredio = listapredio;
    }
    
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
