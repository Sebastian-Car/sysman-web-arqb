package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.EstadodetesoreriabControladorEnum;
import com.sysman.contabilidad.enums.EstadodetesoreriabControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 21/04/2016
 *
 * @version 1.1, Nov 2016 - Modificado por: sdaza. Se adiciona
 * componente gr�fico para indicador de referencia y fuente de
 * recurso. Se valida la condici�n de con saldo cero debido a que se
 * omite de la consulta base y se debe validar desde la consulta final
 * @Version 2, 11/04/2017- mzanguna se realiza proceso de
 * refactorizaci�n y ajustes del sonar.
 * @version 3, 21/04/2017, mzanguna cambio EJB.
 *
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y
 * actualizaci�n de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class EstadodetesoreriabControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    private boolean centroCosto;
    private boolean tercero;
    private boolean auxiliar;
    private boolean referencia;
    private boolean fuenteRecurso;
    private boolean saldoCero;

    private int anoInicial;
    private int mesInicial;
    private String digitos;
    private int mesFinal;
    private int periodo;
    private String codigoInicial;
    private String codigoFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoInicial;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    /**
     * Creates a new instance of EstadodetesoreriabControlador
     */
    public EstadodetesoreriabControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.ESTADODETESORERIAB_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EstadodetesoreriabControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {

        codigoInicial = "1";
        codigoFinal = "9";
        digitos = "6";
        periodo = 1;
        mesInicial = 1;
        mesFinal = 2;
        abrirFormulario();
        cargarListaCodigoInicial();
        cargarListaAnoInicial();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anoInicial = SysmanFunciones
                        .ano(new Date());
        /*
         * FR645-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
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
                                                            EstadodetesoreriabControladorUrlEnum.URL4076
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(EstadodetesoreriabControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodetesoreriabControladorUrlEnum.URL4626
                                                        .getValue());
        Map<String, Object> parametroLista = new TreeMap<>();
        parametroLista.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametroLista.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametroLista,
                        true, EstadodetesoreriabControladorEnum.COD.getValue());
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodetesoreriabControladorUrlEnum.URL5508
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EstadodetesoreriabControladorEnum.CODIN.getValue(),
                        codigoInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void obtenerEstadoTB(FORMATOS formatos) {

        String condicion = "";
        String parReporte = "";
        int mesI = mesInicial - 1;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("mesI", mesI);
            reemplazar.put("mesInicial", mesInicial);
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
                condicion = " AND (SALDOAUX.SALDO" + mesInicial
                    + " <> 0  "
                    + " OR SALDOAUX.DEBITO" + mesInicial
                    + " + SALDOAUX.DEBITO" + mesFinal + " <> 0"
                    + " OR SALDOAUX.CREDITO" + mesInicial
                    + " + SALDOAUX.CREDITO" + mesFinal + " <> 0 "
                    + " OR SALDOAUX.AJUSTE" + mesFinal + " <> 0)";
            }

            reemplazar.put("condicion", condicion);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_TITULO", "ESTADO DE TESORERIA BIMENSUAL");
            String entre = "Entre "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
                + " y "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]
                + " de "
                + anoInicial;
            parametros.put("PR_ENTRE", entre);
            parReporte = "000631EstadoDeTesoreriaA";

            Reporteador.resuelveConsulta("000631EstadoDeTesoreriaB",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirpdf(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerEstadoTB(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerEstadoTB(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigoInicial() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo() {
        // <CODIGO_DESARROLLADO>

        if ((periodo <= 0) || (periodo > 6)) {

            setPeriodo(1);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3182"));
        }

        if (periodo == 1) {
            mesInicial = 1;
            mesFinal = 2;
        }
        else if (periodo == 2) {
            mesInicial = 3;
            mesFinal = 4;
        }
        else if (periodo == 3) {
            mesInicial = 5;
            mesFinal = 6;
        }
        else if (periodo == 4) {
            mesInicial = 7;
            mesFinal = 8;
        }
        else if (periodo == 5) {
            mesInicial = 9;
            mesFinal = 10;
        }
        else if (periodo == 6) {
            mesInicial = 11;
            mesFinal = 12;
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(EstadodetesoreriabControladorEnum.COD.getValue())
                        .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(EstadodetesoreriabControladorEnum.COD.getValue())
                        .toString();
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

    public void setSaldoCero(boolean saldoCero) {
        this.saldoCero = saldoCero;
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

    public String getCompania() {
        return compania;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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

}
