package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.EstadodetesoreriaaControladorEnum;
import com.sysman.contabilidad.enums.EstadodetesoreriaaControladorUrlEnum;
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
 * @author acaceres
 * @version 1, 18/04/2016
 *
 * @version 1.1, Nov 2016 - Modificado por: sdaza. Se adiciona
 * componente gráfico para indicador de referencia y fuente de
 * recurso. Se valida la condición de con saldo cero debido a que se
 * omite de la consulta base y se debe validar desde la consulta final
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 07/04/2017
 */
@ManagedBean
@ViewScoped

public class EstadodetesoreriaaControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante definida que toma el valor de
     * GeneralParameterEnum.CODIGO.getName()
     */
    private final String cCodigo;

    private boolean centroCosto;
    private boolean tercero;
    private boolean auxiliar;
    private boolean referencia;
    private boolean fuenteRecurso;
    private boolean saldoCero;
    private String codigoInicial;
    private String codigoFinal;
    private int anoInicial;
    private int mesInicial;
    private int mesFinal;
    private String digitos;
    String condicion;
    private List<Registro> listaAnoInicial;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of EstadodetesoreriaaControlador
     */
    public EstadodetesoreriaaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.ESTADODETESORERIAA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EstadodetesoreriaaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {

        cargarListaAnoInicial();

        mesInicial = 1;
        mesFinal = 12;
        codigoInicial = "11";
        codigoFinal = "13";
        digitos = "6";
        abrirFormulario();
        cargarListaCodigoInicial();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anoInicial = SysmanFunciones
                        .ano(new Date());

        /*
         * FR637-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstadodetesoreriaaControladorUrlEnum.URL4089
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
                                        EstadodetesoreriaaControladorUrlEnum.URL5430
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        String.valueOf(anoInicial));

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodetesoreriaaControladorUrlEnum.URL6322
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EstadodetesoreriaaControladorEnum.PARAM0.getValue(),
                        codigoInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        String.valueOf(anoInicial));

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void obtenerEstadoTACC(FORMATOS formatos) {
        String reporte = "000631EstadoDeTesoreriaA";
        try {

            int mesI = mesInicial - 1;

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mesI", mesI);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("anio", anoInicial);
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("digitos", digitos);
            reemplazar.put("manTer", tercero ? "1" : "0");
            reemplazar.put("manAux", auxiliar ? "1" : "0");
            reemplazar.put("manCen", centroCosto ? "1" : "0");
            reemplazar.put("manRef", referencia ? "1" : "0");
            reemplazar.put("manFue", fuenteRecurso ? "1" : "0");
            if (!saldoCero) {
                condicion = "AND (SALDOAUX.SALDO" + mesInicial + " <> 0  "
                    + " OR SALDOAUX.DEBITO1 + SALDOAUX.DEBITO2 + SALDOAUX.DEBITO3 + SALDOAUX.DEBITO4 + "
                    + "    SALDOAUX.DEBITO5 + SALDOAUX.DEBITO6 +SALDOAUX.DEBITO7 +SALDOAUX.DEBITO8 + "
                    + "    SALDOAUX.DEBITO9 + SALDOAUX.DEBITO10 + SALDOAUX.DEBITO11 + SALDOAUX.DEBITO12  <> 0"
                    + " OR SALDOAUX.DEBITO1 + SALDOAUX.DEBITO2 + SALDOAUX.DEBITO3 + SALDOAUX.DEBITO4 + "
                    + "    SALDOAUX.DEBITO5 + SALDOAUX.DEBITO6 +SALDOAUX.DEBITO7 +SALDOAUX.DEBITO8 + "
                    + "    SALDOAUX.DEBITO9 + SALDOAUX.DEBITO10 + SALDOAUX.DEBITO11 + SALDOAUX.DEBITO12  <> 0 "
                    + " OR SALDOAUX.SALDO" + mesFinal + " <> 0 "
                    + " OR SALDOAUX.AJUSTE" + mesFinal + " <> 0)";

            }
            else {
                condicion = "";
            }
            reemplazar.put("saldoCero", condicion);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_TITULO", "ESTADO DE TESORERIA ANUAL");
            String entre = "Entre Enero y Diciembre de " + anoInicial;
            parametros.put("PR_ENTRE", entre);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (IOException | JRException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(EstadodetesoreriaaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerEstadoTACC(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerEstadoTACC(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        cargarListaCodigoInicial();

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(cCodigo) == null ? ""
            : registroAux.getCampos().get(cCodigo).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(cCodigo) == null ? ""
            : registroAux.getCampos().get(cCodigo).toString();
    }

    public boolean isCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto) {
        this.centroCosto = centroCosto;
    }

    public boolean isTercero() {
        return tercero;
    }

    public void setTercero(boolean tercero) {
        this.tercero = tercero;
    }

    public boolean isAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(boolean auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isSaldoCero() {
        return saldoCero;
    }

    /**
     * @return the referencia
     */
    public boolean isReferencia() {
        return referencia;
    }

    /**
     * @param referencia
     * the referencia to set
     */
    public void setReferencia(boolean referencia) {
        this.referencia = referencia;
    }

    /**
     * @return the fuenteRecurso
     */
    public boolean isFuenteRecurso() {
        return fuenteRecurso;
    }

    /**
     * @param fuenteRecurso
     * the fuenteRecurso to set
     */
    public void setFuenteRecurso(boolean fuenteRecurso) {
        this.fuenteRecurso = fuenteRecurso;
    }

    public void setSaldoCero(boolean saldoCero) {
        this.saldoCero = saldoCero;
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

    public int getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(int anoInicial) {
        this.anoInicial = anoInicial;
    }

    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
