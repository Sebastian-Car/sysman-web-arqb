package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.EstadodetesoreriamControladorEnum;
import com.sysman.contabilidad.enums.EstadodetesoreriamControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author ybecerra
 * @version 1, 20/04/2016
 * 
 * @version 2, 07/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico Refactoring.<br>
 * Manejo de EJBs.
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class EstadodetesoreriamControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el valor del enumarado
     * {@code  EstadodetesoreriamControladorEnum.ID}
     */
    private final String cId;

    private boolean centroCosto;
    private boolean tercero;
    private boolean auxiliar;
    private boolean saldoCero;

    /**
     * Atributo que controla si el check con referencia esta marcado.
     */
    private boolean ckReferencia;

    /**
     * Atributo que controla si el check con fuente de rcursos esta
     * marcado.
     */
    private boolean ckFuenteRecurso;

    private String codigoInicial;
    private String codigoFinal;
    private int ano;
    private int mesInicial;
    private String digitos;
    private int mesFinal;
    private int periodo;
    private boolean centroVisible;
    private List<Registro> listaAnoInicial;
    private List<Registro> listaPeriodo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of EstadodetesoreriamControlador
     */
    public EstadodetesoreriamControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cId = EstadodetesoreriamControladorEnum.ID.getValue();

        try {
            numFormulario = GeneralCodigoFormaEnum.ESTADODETESORERIAM_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EstadodetesoreriamControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        digitos = "6";
        abrirFormulario();
        cargarListaAnoInicial();
        cargarListaPeriodo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();

        codigoInicial = "11";
        codigoFinal = "13";
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones.ano(new Date());
        periodo = SysmanFunciones.mes(new Date());
        mesInicial = mesFinal = periodo;

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstadodetesoreriamControladorUrlEnum.URL3757
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstadodetesoreriamControladorUrlEnum.URL4237
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial() {
        codigoInicial = null;
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodetesoreriamControladorUrlEnum.URL4921
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cId);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodetesoreriamControladorUrlEnum.URL5827
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EstadodetesoreriamControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cId);
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        String parReporte = "000631EstadoDeTesoreriaM";
        try {

            String saldoCero1;
            int mes = periodo - 1;

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mesI", mes);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("periodo", periodo);
            reemplazar.put("anio", ano);
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("digitos", digitos);
            reemplazar.put("manTer", tercero ? "1" : "0");
            reemplazar.put("manAux", auxiliar ? "1" : "0");
            reemplazar.put("manCen", centroCosto ? "1" : "0");
            reemplazar.put("manRef", ckReferencia ? "1" : "0");
            reemplazar.put("manFue", ckFuenteRecurso ? "1" : "0");

            if (!saldoCero) {
                saldoCero1 = "AND (SALDOAUX.SALDO" + mes
                    + " <> 0 OR SALDOAUX.DEBITO" + periodo
                    + " <> 0 OR SALDOAUX.CREDITO" + periodo
                    + " <> 0"
                    + " OR SALDOAUX.SALDO" + periodo
                    + " <> 0 OR SALDOAUX.AJUSTE" + periodo
                    + " <> 0)";

            }
            else {
                saldoCero1 = "";
            }

            reemplazar.put("saldoCero", saldoCero1);
            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_TITULO", "ESTADO DE TESORERIA MENSUAL");
            parametros.put("PR_ENTRE",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[periodo]
                                + " / " + ano);

            Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", parReporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (JRException | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "";
        codigoFinal = "";
        periodo = 1;
        mesFinal = mesInicial = 1;

        cargarListaPeriodo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo() {
        // <CODIGO_DESARROLLADO>
        mesInicial = periodo;
        mesFinal = periodo;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cId), "").toString();

        codigoFinal = null;

        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cId), "").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    public boolean isCentroCosto() {
        return centroCosto;
    }

    public boolean isCkReferencia() {
        return ckReferencia;
    }

    public void setCkReferencia(boolean ckReferencia) {
        this.ckReferencia = ckReferencia;
    }

    public boolean isCkFuenteRecurso() {
        return ckFuenteRecurso;
    }

    public void setCkFuenteRecurso(boolean ckFuenteRecurso) {
        this.ckFuenteRecurso = ckFuenteRecurso;
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public boolean isCentroVisible() {
        return centroVisible;
    }

    public void setCentroVisible(boolean centroVisible) {
        this.centroVisible = centroVisible;
    }

    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
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
