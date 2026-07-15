package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceCentroCostoControladorEnum;
import com.sysman.contabilidad.enums.BalanceCentroCostoControladorUrlEnum;
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
 * @version 1, 25/04/2016
 * 
 * @author eamaya
 * @version 2, 06/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class BalanceCentroCostoControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String c171px;
    private final String c144px;
    private final String cBlock;

    private boolean saldoCero;
    private boolean formato;
    private boolean mayorizar;
    private boolean mayorizado;
    private boolean naturaleza;
    private boolean formatoEspecialExcel;
    private String arribaNivel;
    private String arribaCentroCosto;
    private String arribaCentroInicial;
    private String mostrarCentroCosto;
    private String mostrarNivel;
    private String codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String anio;
    private String condicionCentroCosto;
    private int mes;
    private int nivel;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaCentroInicial;
    private RegistroDataModelImpl listaCentroFinal;

    private String nombreCentroInicial;

    @EJB
    private EjbSysmanUtilRemote ejbParametroUno;

    /**
     * Creates a new instance of BalanceCentroCostoControlador
     */
    public BalanceCentroCostoControlador() {
        super();
        c171px = "171px";
        c144px = "144px";
        cBlock = "block";
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_CENTRO_COSTO_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BalanceCentroCostoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        nivel = 0;
        arribaCentroCosto = c171px;
        arribaCentroInicial = c144px;
        mostrarNivel = "none";
        mostrarCentroCosto = cBlock;
        mes = SysmanFunciones.mes(new Date());
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaAnoTrabajo();
        cargarListaCodigoInicial();
        cargarListaCentroInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR658-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
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
                                                            BalanceCentroCostoControladorUrlEnum.URL4054
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // usar la consulta estandar
    }

    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceCentroCostoControladorUrlEnum.URL4553
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceCentroCostoControladorUrlEnum.URL5173
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(BalanceCentroCostoControladorEnum.PARAM3.getValue(),
                        codigoInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCentroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceCentroCostoControladorUrlEnum.URL5742
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(BalanceCentroCostoControladorEnum.PARAM5.getValue(),
                        nivel);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCentroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCentroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceCentroCostoControladorUrlEnum.URL6376
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(BalanceCentroCostoControladorEnum.PARAM9.getValue(),
                        centroInicial);

        listaCentroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarCentroCostoFinal()) {
            generaInforme(ReportesBean.FORMATOS.PDF);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarCentroCostoFinal()) {

            generaInforme(ReportesBean.FORMATOS.EXCEL97);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMayorizar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(FORMATOS formato) {
        int mesAux = 0;
        condicionCentroCosto = " ";
        String titulo = null;
        String informe = null;
        String condicionSaldoCero = null;
        String condicionReportes = " ";
        String firmaContable1 = " ";
        String firmaContable2 = " ";
        String firmaContable3 = " ";
        String cargoContable1 = " ";
        String cargoContable2 = " ";
        String cargoContable3 = " ";
        String documentContb1 = " ";
        String documentContb2 = " ";
        String documentContb3 = " ";
        String condicionMayorizar = "";
        String condicionMayorizarSub = "";
        String consultaResuelta;

        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            titulo = "BALANCE POR CENTRO DE COSTO DEL MES DE "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes] + " DE "
                + anio;

            condicionMayorizar = "SUBSTR(SALDO.CODIGO, 1, LENGTH(PLAN.CODIGO)) = PLAN.CODIGO";
            condicionMayorizarSub = "SUBSTR(SALDO.CODIGO, 1, LENGTH(PLAN.CODIGO)) = PLAN.CODIGO";

            if (!condicionCentroCosto()) {
                return;
            }

            else {
                String mensaje;
                informe = "000687BalancePorCentroCosto";
                
                if (mayorizado) {
                    informe = "002580BalancePorCentroCostoM";    
                }

                if ("".equals(centroFinal) || naturaleza) {
                    mensaje = "CONSOLIDADO";
                }
                else {
                    mensaje = "CONSOLIDADO";
                    parametros.put("PR_CENTRO_DE_COSTO",
                                    SysmanFunciones.nvl(nombreCentroInicial, "")
                                                    .toString().toUpperCase());
                }

                condicionReportes = "   AND  SUBSTR(PLAN_CONTABLE.CODIGO,1,1)  NOT IN('0') ";

                mesAux = mes - 1;

                /*
                 * Indicador que hace visible las etiquetas NIT, NIT
                 * COMPANIA, CENTRO DE COSTO y REPUBLICA DE COLOMBIA
                 */
                parametros.put("PRVISIBLE", 0);
                parametros.put("PR_NIT", "NIT:");
                parametros.put("PR_NIT_COMPANIA",
                                SessionUtil.getCompaniaIngreso().getNit());
                parametros.put("PR_REPUBLICA_DE_COLOMBIA",
                                "REPUBLICA DE COLOMBIA");
                titulo = "BALANCE POR CENTRO DE COSTO " + mensaje + "  DE  "
                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                    .toUpperCase()
                    + " DE "
                    + anio;

            }
            condicionSaldoCero = !saldoCero ? ""
                + " AND (SALDO_AUX_CONTABLE.SALDO" + mes
                + "        NOT IN(0)  \n"
                + "   OR SALDO_AUX_CONTABLE.DEBITO" + mes
                + "       NOT IN(0)  \n"
                + "   OR SALDO_AUX_CONTABLE.CREDITO" + mes
                + "      NOT IN(0)  \n"
                + "   OR SALDO_AUX_CONTABLE.SALDO" + (mes - 1) + "  NOT IN(0)) "
                : " ";

            firmaContable1 = ejbParametroUno.consultarParametro(compania,
                            "FIRMA CONTABLE 1", modulo, new Date(), false);

            firmaContable2 = ejbParametroUno.consultarParametro(compania,
                            "FIRMA CONTABLE 2", modulo, new Date(), false);

            firmaContable3 = ejbParametroUno.consultarParametro(compania,
                            "FIRMA CONTABLE 3", modulo, new Date(), false);

            cargoContable1 = ejbParametroUno.consultarParametro(compania,
                            "CARGO CONTABLE 1", modulo, new Date(), false);

            cargoContable2 = ejbParametroUno.consultarParametro(compania,
                            "CARGO CONTABLE 2", modulo, new Date(), false);

            cargoContable3 = ejbParametroUno.consultarParametro(compania,
                            "CARGO CONTABLE 3", modulo, new Date(), false);

            documentContb1 = ejbParametroUno.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 1", modulo, new Date(), false);

            documentContb2 = ejbParametroUno.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 2", modulo, new Date(), false);

            documentContb3 = ejbParametroUno.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 3", modulo, new Date(), false);

            reemplazar.put("mes", mes);
            reemplazar.put("mesActual", mes);
            reemplazar.put("mesAux", mesAux);
            reemplazar.put("anio", anio);
            reemplazar.put("mesAnterior", mes - 1);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);
            reemplazar.put("condicionSaldoCero", condicionSaldoCero);
            reemplazar.put("condicionReportes", condicionReportes);
            reemplazar.put("condicionCentroCosto", condicionCentroCosto);
            reemplazar.put("condicionMayorizar", mayorizar ? "0" : "-1");
            consultaResuelta = Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo),
                            reemplazar);

            // reemplazar.put("condicionMayorizar",
            // condicionMayorizar);
            reemplazar.put("condicionMayorizar", mayorizar ? "0" : "-1");

            reemplazar.put("consultaResuelta", consultaResuelta);

            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);

            parametros.put("PR_TITULO_INFORME", titulo.toUpperCase());
            parametros.put("PR_FIRMA_CONTABLE1", firmaContable1);
            parametros.put("PR_FIRMA_CONTABLE2", firmaContable2);
            parametros.put("PR_FIRMA_CONTABLE3", firmaContable3);
            parametros.put("PR_CARGO_CONTABLE1", cargoContable1);
            parametros.put("PR_CARGO_CONTABLE2", cargoContable2);
            parametros.put("PR_CARGO_CONTABLE3", cargoContable3);
            parametros.put("PR_DOCUMENTO_CONTABLE1", documentContb1);
            parametros.put("PR_DOCUMENTO_CONTABLE2", documentContb2);
            parametros.put("PR_DOCUMENTO_CONTABLE3", documentContb3);
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", formatoEspecialExcel);

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean condicionCentroCosto() {
        if (!naturaleza) {
            if (mayorizar) {
                if (!"".equals(centroInicial)) {
                    condicionCentroCosto = " AND SUBSTR(SALDO_AUX_CONTABLE.CENTRO_COSTO,1, "
                        + nivel + ") = '" + centroInicial + "' ";
                }
                else {
                    JsfUtil.agregarMensajeError(
                                    idioma.getString("TB_TB577"));
                    return false;
                }
            }
            else {
                if (!"".equals(centroInicial) || !"".equals(centroFinal)) {
                    condicionCentroCosto = " AND SALDO_AUX_CONTABLE.CENTRO_COSTO  BETWEEN '"
                        + centroInicial + "' AND '" + centroFinal + "' ";
                }
                else {
                    JsfUtil.agregarMensajeError(
                                    idioma.getString("TB_TB578"));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validarCentroCostoFinal() {
        if (!mayorizar && SysmanFunciones.validarVariableVacio(centroFinal)) {
            JsfUtil.agregarMensajeError("Seleccione el Centro de Costo Final");
            return false;
        }
        return true;
    }

    public void cambiarnivelCC() {
        // <CODIGO_DESARROLLADO>
        centroInicial = null;

        cargarListaCentroInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarmayorizarCC() {
        // <CODIGO_DESARROLLADO>
        centroInicial = null;
        if (mayorizar) {
            nivel = 2;
            arribaNivel = c144px;
            arribaCentroInicial = c171px;
            mostrarNivel = cBlock;
            mostrarCentroCosto = "none";
        }
        else {
            nivel = 0;
            arribaCentroInicial = c144px;
            arribaCentroCosto = c171px;
            mostrarNivel = "none";
            mostrarCentroCosto = cBlock;
        }
        cargarListaCentroInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        " ")
                        .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
    }

    public void seleccionarFilaCentroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
        nombreCentroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
        centroFinal = null;
        cargarListaCentroFinal();
    }

    public void seleccionarFilaCentroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
    }

    public boolean getSaldoCero() {
        return saldoCero;
    }

    public void setSaldoCero(boolean saldoCero) {
        this.saldoCero = saldoCero;
    }

    public boolean getFormato() {
        return formato;
    }

    public void setFormato(boolean formato) {
        this.formato = formato;
    }

    public boolean getMayorizar() {
        return mayorizar;
    }

    public void setMayorizar(boolean mayorizar) {
        this.mayorizar = mayorizar;
    }
    
    public boolean getMayorizado() {
        return mayorizado;
    }

    public void setMayorizado(boolean mayorizado) {
        this.mayorizado = mayorizado;
    }

    public boolean getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(boolean naturaleza) {
        this.naturaleza = naturaleza;
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

    public String getCentroInicial() {
        return centroInicial;
    }

    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    public String getCentroFinal() {
        return centroFinal;
    }

    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
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

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
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

    public RegistroDataModelImpl getListaCentroInicial() {
        return listaCentroInicial;
    }

    public void setListaCentroInicial(
        RegistroDataModelImpl listaCentroInicial) {
        this.listaCentroInicial = listaCentroInicial;
    }

    public RegistroDataModelImpl getListaCentroFinal() {
        return listaCentroFinal;
    }

    public void setListaCentroFinal(RegistroDataModelImpl listaCentroFinal) {
        this.listaCentroFinal = listaCentroFinal;
    }

    /**
     * @return the mostrarCentroCosto
     */
    public String getMostrarCentroCosto() {
        return mostrarCentroCosto;
    }

    /**
     * @param mostrarCentroCosto
     * the mostrarCentroCosto to set
     */
    public void setMostrarCentroCosto(String mostrarCentroCosto) {
        this.mostrarCentroCosto = mostrarCentroCosto;
    }

    /**
     * @return the mostrarNivel
     */
    public String getMostrarNivel() {
        return mostrarNivel;
    }

    /**
     * @param mostrarNivel
     * the mostrarNivel to set
     */
    public void setMostrarNivel(String mostrarNivel) {
        this.mostrarNivel = mostrarNivel;
    }

    /**
     * @return the arribaNivel
     */
    public String getArribaNivel() {
        return arribaNivel;
    }

    /**
     * @param arribaNivel
     * the arribaNivel to set
     */
    public void setArribaNivel(String arribaNivel) {
        this.arribaNivel = arribaNivel;
    }

    /**
     * @return the arribaCentroCosto
     */
    public String getArribaCentroCosto() {
        return arribaCentroCosto;
    }

    /**
     * @param arribaCentroCosto
     * the arribaCentroCosto to set
     */
    public void setArribaCentroCosto(String arribaCentroCosto) {
        this.arribaCentroCosto = arribaCentroCosto;
    }

    /**
     * @return the arribaCentroInicial
     */
    public String getArribaCentroInicial() {
        return arribaCentroInicial;
    }

    /**
     * @param arribaCentroInicial
     * the arribaCentroInicial to set
     */
    public void setArribaCentroInicial(String arribaCentroInicial) {
        this.arribaCentroInicial = arribaCentroInicial;
    }

    public String getCondicionCentroCosto() {
        return condicionCentroCosto;
    }

    public void setCondicionCentroCosto(String condicionCentroCosto) {
        this.condicionCentroCosto = condicionCentroCosto;
    }

    public boolean isFormatoEspecialExcel() {
        return formatoEspecialExcel;
    }

    public void setFormatoEspecialExcel(boolean formatoEspecialExcel) {
        this.formatoEspecialExcel = formatoEspecialExcel;
    }

}
