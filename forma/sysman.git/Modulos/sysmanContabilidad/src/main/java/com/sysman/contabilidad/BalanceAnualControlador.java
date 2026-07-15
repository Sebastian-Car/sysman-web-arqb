package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceAnualControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 27/04/2016
 * @modified jguerrero
 * @version 2. 04/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class BalanceAnualControlador extends BeanBaseModal {
    private final String compania;
    private String saldoONeto;
    private boolean ckCentroCosto;
    private boolean ckTercero;
    private boolean ckAuxiliar;
    private boolean ckSaldoCero;
    private boolean ckReferencia;
    private boolean ckFuente;
    private String codigoInicial;
    private String codigoFinal;
    private String anio;
    private String digitos;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private final String codigoCons;

    /**
     * Creates a new instance of BalanceAnualControlador
     */
    public BalanceAnualControlador() {
        super();
        initValores();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_ANUAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();

        }
        catch (Exception ex) {
            Logger.getLogger(BalanceAnualControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    public void initValores() {
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        codigoInicial = "0";
        codigoFinal = "9";
        digitos = "6";
        saldoONeto = "1";
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAnoTrabajo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR667-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalanceAnualControladorUrlEnum.URL3335
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAnualControladorUrlEnum.URL3620
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceAnualControladorUrlEnum.URL4635
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOINICIAL", codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando57() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarReporte(FORMATOS formato) {
        try {
            String titulo;
            String campo;
            if ("1".equals(saldoONeto)) {
                titulo = "BALANCE ANUAL COMPARATIVO  DE " + anio;
                campo = "SALDO";

            }
            else {
                titulo = "BALANCE COMPARATIVO ANUAL NETO DE MOVIMIENTOS "
                    + anio;
                campo = "NETO";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("NETOOSALDO", campo);
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("digitos", digitos);
            reemplazar.put("manTer", ckTercero ? "1" : "0");
            reemplazar.put("manAux", ckAuxiliar ? "1" : "0");
            reemplazar.put("manCen", ckCentroCosto ? "1" : "0");
            reemplazar.put("manRef", ckReferencia ? "1" : "0");
            reemplazar.put("manFue", ckFuente ? "1" : "0");
            reemplazar.put("saldoCero",
                            !ckSaldoCero
                                ? " AND SALDOAUX.SALDO0 + SALDOAUX.SALDO1 + SALDOAUX.SALDO2 + SALDOAUX.SALDO3 + SALDOAUX.SALDO4 + SALDOAUX.SALDO5 + SALDOAUX.SALDO6 + SALDOAUX.SALDO7 + SALDOAUX.SALDO8 + SALDOAUX.SALDO9 + SALDOAUX.SALDO10 + SALDOAUX.SALDO11 + SALDOAUX.SALDO12 + SALDOAUX.SALDO13 NOT IN (0) "
                                : " ");
            reemplazar.put("baseBalances", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));
            String reporte = "000695BalanceAnual";

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_TITULO", titulo);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = codigoFinal = null;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
    }

    public String getSaldoONeto() {
        return saldoONeto;
    }

    public void setSaldoONeto(String saldoONeto) {
        this.saldoONeto = saldoONeto;
    }

    public boolean isCkCentroCosto() {
        return ckCentroCosto;
    }

    public void setCkCentroCosto(boolean ckCentroCosto) {
        this.ckCentroCosto = ckCentroCosto;
    }

    public boolean isCkTercero() {
        return ckTercero;
    }

    public void setCkTercero(boolean ckTercero) {
        this.ckTercero = ckTercero;
    }

    public boolean isCkAuxiliar() {
        return ckAuxiliar;
    }

    public void setCkAuxiliar(boolean ckAuxiliar) {
        this.ckAuxiliar = ckAuxiliar;
    }

    public boolean isCkSaldoCero() {
        return ckSaldoCero;
    }

    public void setCkSaldoCero(boolean ckSaldoCero) {
        this.ckSaldoCero = ckSaldoCero;
    }

    public boolean isCkReferencia() {
        return ckReferencia;
    }

    public void setCkReferencia(boolean ckReferencia) {
        this.ckReferencia = ckReferencia;
    }

    public boolean isCkFuente() {
        return ckFuente;
    }

    public void setCkFuente(boolean ckFuente) {
        this.ckFuente = ckFuente;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
}
