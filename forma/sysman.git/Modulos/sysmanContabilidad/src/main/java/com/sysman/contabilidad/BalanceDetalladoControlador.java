package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceDetalladoControladorEnum;
import com.sysman.contabilidad.enums.BalanceDetalladoControladorUrlEnum;
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
 * @author otorres
 * @version 1, 22/04/2016
 *
 * @version 1.1, Nov 2016 - Modificado por: sdaza. Se modifica las
 * consultas de los informes con el fin de unificar con la consulta
 * base 800046
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 06/04/2017
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 *
 */
@ManagedBean
@ViewScoped

public class BalanceDetalladoControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante definida que toma el valor de
     * GeneralParameterEnum.CODIGO.getName();
     */
    private final String cCodigo;
    private boolean incluyeTercero;
    private boolean incluyeAuxiliar;
    private boolean incluyeCentroCosto;
    private boolean incluyeReferencia;
    private boolean incluyeFteRecurso;
    private boolean saldoCero;
    private boolean conExtracto;
    private String codigoInicial;
    private String codigoFinal;
    private String terceroInicial;
    private String terceroFinal;
    private String anio;
    private int mes;
    private int numeroDigitos;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    @EJB
    private EjbSysmanUtilRemote ejbContabilidadCero;

    /**
     * Creates a new instance of BalanceDetalladoControlador
     */
    public BalanceDetalladoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_DETALLADO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BalanceDetalladoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        numeroDigitos = 12;
        mes = SysmanFunciones.mes(new Date());
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaAnoTrabajo();
        cargarListaCodigoInicial();
        cargarListaTerceroInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalanceDetalladoControladorUrlEnum.URL3710
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
                                        BalanceDetalladoControladorUrlEnum.URL4131
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceDetalladoControladorUrlEnum.URL4779
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(BalanceDetalladoControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaTerceroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceDetalladoControladorUrlEnum.URL5556
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceDetalladoControladorUrlEnum.URL6322
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(BalanceDetalladoControladorEnum.PARAM1.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void oprimirImprimir() {
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

    public void generaInforme(ReportesBean.FORMATOS formato) {
        String titulo = " ";
        String cargoTesorero = " ";
        String firmaContable1 = " ";
        String firmaContable2 = " ";
        String firmaContable3 = " ";
        String cargoContable1 = " ";
        String cargoContable2 = " ";
        String cargoContable3 = " ";
        String documentContb1 = " ";
        String documentContb2 = " ";
        String documentContb3 = " ";
        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            if (conExtracto) {
                titulo = "EXTRACTO DEL MES DE "
                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                    .toUpperCase()
                    + " DE "
                    + anio;

                cargoTesorero = ejbContabilidadCero.consultarParametro(compania,
                                "CARGO TESORERO", modulo, new Date(), true);

            }
            else {
                titulo = "BALANCE DETALLADO DEL MES DE "
                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                    .toUpperCase()
                    + "  DE "
                    + anio;
                firmaContable1 = ejbContabilidadCero.consultarParametro(
                                compania, "FIRMA CONTABLE 1", modulo,
                                new Date(), true);
                firmaContable2 = ejbContabilidadCero.consultarParametro(
                                compania, "FIRMA CONTABLE 2", modulo,
                                new Date(), true);
                firmaContable3 = ejbContabilidadCero.consultarParametro(
                                compania, "FIRMA CONTABLE 3", modulo,
                                new Date(), true);
                cargoContable1 = ejbContabilidadCero.consultarParametro(
                                compania, "CARGO CONTABLE 1", modulo,
                                new Date(), true);
                cargoContable2 = ejbContabilidadCero.consultarParametro(
                                compania, "CARGO CONTABLE 2", modulo,
                                new Date(), true);
                cargoContable3 = ejbContabilidadCero.consultarParametro(
                                compania, "CARGO CONTABLE 3", modulo,
                                new Date(), true);
                documentContb1 = ejbContabilidadCero.consultarParametro(
                                compania, "DOCUMENTO CONTABLE 1", modulo,
                                new Date(), true);
                documentContb2 = ejbContabilidadCero.consultarParametro(
                                compania, "DOCUMENTO CONTABLE 2", modulo,
                                new Date(), true);
                documentContb3 = ejbContabilidadCero.consultarParametro(
                                compania, "DOCUMENTO CONTABLE 3", modulo,
                                new Date(), true);

            }

            int mesM = mes - 1;
            reemplazar.put("mesActual", mes);
            reemplazar.put("mestrabajo", mes);
            reemplazar.put("saldoCero", saldoCero ? ""
                : "AND PLAN_CONTABLE.SALDO" + mes + "<> 0");
            reemplazar.put("mesAnterior", mesM);
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("digitos", numeroDigitos);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            indicadores(reemplazar);

            reemplazar.put("filtro", filtroConsulta());
            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));

            Reporteador.resuelveConsulta("000675BalanceDetalladoBien",
                            Integer.parseInt(modulo), reemplazar, parametros);
            parametros.put("PR_TITULO", titulo.toUpperCase());
            parametros.put("PR_CARGO_TESORERO", cargoTesorero);
            parametros.put("PR_FIRMA_CONTABLE1", firmaContable1);
            parametros.put("PR_FIRMA_CONTABLE2", firmaContable2);
            parametros.put("PR_FIRMA_CONTABLE3", firmaContable3);
            parametros.put("PR_CARGO_CONTABLE1", cargoContable1);
            parametros.put("PR_CARGO_CONTABLE2", cargoContable2);
            parametros.put("PR_CARGO_CONTABLE3", cargoContable3);
            parametros.put("PR_DOCUMENTO_CONTABLE1", documentContb1);
            parametros.put("PR_DOCUMENTO_CONTABLE2", documentContb2);
            parametros.put("PR_DOCUMENTO_CONTABLE3", documentContb3);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000675BalanceDetalladoBien", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo llamado en generaInforme
     *
     * @param reemplazar
     */
    private void indicadores(HashMap<String, Object> reemplazar) {
        reemplazar.put("manTer", incluyeTercero ? "1" : "0");
        reemplazar.put("manAux", incluyeAuxiliar ? "1" : "0");
        reemplazar.put("manCen", incluyeCentroCosto ? "1" : "0");
        reemplazar.put("manRef", incluyeReferencia ? "1" : "0");
        reemplazar.put("manFue", incluyeFteRecurso ? "1" : "0");
    }

    /**
     * Metodo que retorna filtro para la consulta final,se llama en el
     * metodo generaInforme
     *
     * @return filtro
     */
    private String filtroConsulta() {
        String filtro = "";
        filtro = incluyeTercero
            ? filtro + " AND BASECONSULTA.TERCERO = DETALLE.TERCERO "
            : filtro;
        filtro = incluyeAuxiliar
            ? filtro + " AND BASECONSULTA.AUXILIAR = DETALLE.AUXILIAR "
            : filtro;
        filtro = incluyeCentroCosto
            ? filtro
                + " AND BASECONSULTA.CENTRO_COSTO = DETALLE.CENTRO_COSTO "
            : filtro;
        filtro = incluyeReferencia
            ? filtro + " AND BASECONSULTA.REFERENCIA = DETALLE.REFERENCIA "
            : filtro;
        filtro = incluyeFteRecurso
            ? filtro
                + " AND BASECONSULTA.FUENTE_RECURSOS = DETALLE.FUENTE_RECURSO "
            : filtro;
        return filtro;
    }

    public void cambiarAnoTrabajo() {
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

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT") == null ? ""
            : registroAux.getCampos().get("NIT").toString();
        terceroFinal = null;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT") == null ? ""
            : registroAux.getCampos().get("NIT").toString();
    }

    public boolean getIncluyeTercero() {
        return incluyeTercero;
    }

    public void setIncluyeTercero(boolean incluyeTercero) {
        this.incluyeTercero = incluyeTercero;
    }

    public boolean getIncluyeAuxiliar() {
        return incluyeAuxiliar;
    }

    public void setIncluyeAuxiliar(boolean incluyeAuxiliar) {
        this.incluyeAuxiliar = incluyeAuxiliar;
    }

    public boolean isIncluyeReferencia() {
        return incluyeReferencia;
    }

    public void setIncluyeReferencia(boolean incluyeReferencia) {
        this.incluyeReferencia = incluyeReferencia;
    }

    public boolean isIncluyeFteRecurso() {
        return incluyeFteRecurso;
    }

    public void setIncluyeFteRecurso(boolean incluyeFteRecurso) {
        this.incluyeFteRecurso = incluyeFteRecurso;
    }

    public boolean getSaldoCero() {
        return saldoCero;
    }

    public void setSaldoCero(boolean saldoCero) {
        this.saldoCero = saldoCero;
    }

    public boolean getConExtracto() {
        return conExtracto;
    }

    public void setConExtracto(boolean conExtracto) {
        this.conExtracto = conExtracto;
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

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getNumeroDigitos() {
        return numeroDigitos;
    }

    public void setNumeroDigitos(int numeroDigitos) {
        this.numeroDigitos = numeroDigitos;
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

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    /**
     * @return the incluyeCentroCosto
     */
    public boolean isIncluyeCentroCosto() {
        return incluyeCentroCosto;
    }

    /**
     * @param incluyeCentroCosto
     * the incluyeCentroCosto to set
     */
    public void setIncluyeCentroCosto(boolean incluyeCentroCosto) {
        this.incluyeCentroCosto = incluyeCentroCosto;
    }

}
