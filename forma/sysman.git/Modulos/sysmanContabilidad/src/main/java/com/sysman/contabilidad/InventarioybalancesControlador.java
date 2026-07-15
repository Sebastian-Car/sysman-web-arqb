package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.InventarioybalancesControladorEnum;
import com.sysman.contabilidad.enums.InventarioybalancesControladorUrlEnum;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 03/05/2016
 * 
 * @author jlramirez
 * @version 2, 10/04/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de SONARLINT
 * @version 3, 20/04/2017, Manejo EJBs
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class InventarioybalancesControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String opcionImpresion;
    private Boolean centroCosto;
    private Boolean tercero;
    private String terceroC;
    private Boolean auxiliar;
    private String auxiliarC;
    private Boolean saldoCero;
    private String condicion;
    private String codigoInicial;
    private String codigoFinal;
    private boolean referencia;
    private boolean recurso;
    private int anoTrabajo;
    private int mesTrabajo;
    private String digitos;
    private String codigoLibro;
    private String numeroInicial;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private boolean saldoCeroVisible;
    private boolean centroCostoVisible;
    private boolean terceroVisible;
    private boolean auxiliarVisible;
    private boolean etiquetaCentrosCostoVisible;
    private boolean etiquetaAuxTerceroVisible;
    private boolean etiquetaAuxGenVisible;

    /**
     * Creates a new instance of InventarioybalancesControlador
     */
    public InventarioybalancesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.INVENTARIOYBALANCES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InventarioybalancesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAnoTrabajo();

        anoTrabajo = SysmanFunciones.ano(new Date());
        cargarListaMesTrabajo();
        mesTrabajo = SysmanFunciones.mes(new Date());
        codigoInicial = "0";
        codigoFinal = "9999999999999999";
        digitos = "6";
        opcionImpresion = "2";
        saldoCeroVisible = true;
        centroCostoVisible = true;
        terceroVisible = true;
        auxiliarVisible = true;
        etiquetaAuxGenVisible = true;
        etiquetaAuxTerceroVisible = true;
        etiquetaCentrosCostoVisible = true;
        cargarListaCodigoInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InventarioybalancesControladorUrlEnum.URL4178
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(InventarioybalancesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMesTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        try {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InventarioybalancesControladorUrlEnum.URL4666
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(InventarioybalancesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioybalancesControladorUrlEnum.URL5150
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioybalancesControladorUrlEnum.URL5842
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
        param.put(InventarioybalancesControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void obtenerReportes(FORMATOS formatos) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();

            condicion = saldoCero ? ""
                : "AND  PLAN_CONTABLE.SALDO" + mesTrabajo
                    + " NOT IN (0)";

            reemplazar.put("mesTrabajo", mesTrabajo);
            reemplazar.put("anio", anoTrabajo);
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("digitos", digitos);
            reemplazar.put("saldoCero", condicion);
            reemplazar.put("manCen", centroCosto ? "1" : "0");
            reemplazar.put("manTer", tercero ? "1" : "0");
            reemplazar.put("manAux", auxiliar ? "1" : "0");
            reemplazar.put("manRef", referencia ? "1" : "0");
            reemplazar.put("manFue", recurso ? "1" : "0");
            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String entre = "INVENTARIO Y BALANCES DEL MES DE "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesTrabajo]
                                .toUpperCase()
                + " DE "
                + anoTrabajo;
            // MANEJO DE PARAMETROS DEL REPORTE

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_ENTRE", entre);

            if (("3").equals(opcionImpresion)) {
                reemplazar.put("condicion",
                                saldoCero ? "" : addCondicionR000723());

                Reporteador.resuelveConsulta("000723InventarioYBalancesEsp",
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "000723InventarioYBalancesEsp", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);

            }
            else {
                reemplazar.put("condicion",
                                saldoCero ? "" : addCondicionR000720());
                Reporteador.resuelveConsulta("000720InventarioYBalances",
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "000720InventarioYBalances", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);
            }
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * agrega la condicion o filtro para la consulta del reporte
     * 
     * @return
     */
    private String addCondicionR000720() {
        StringBuilder sql = new StringBuilder("");
        sql.append(" AND ");
        sql.append("( ");
        sql.append(" (CASE WHEN SALDOAUX.Naturaleza='D' And Saldo" + mesTrabajo
            + ">=0 THEN Saldo" + mesTrabajo + " ELSE 0 END) + ");
        sql.append(" (CASE WHEN SALDOAUX.Naturaleza='C' And Saldo" + mesTrabajo
            + "<0 THEN -Saldo" + mesTrabajo + " ELSE 0 END) ");
        sql.append(" )+( ");
        sql.append(" (CASE WHEN SALDOAUX.Naturaleza='C' And Saldo" + mesTrabajo
            + ">=0 THEN Saldo" + mesTrabajo + " ELSE 0 END ) + ");
        sql.append(" (CASE WHEN SALDOAUX.Naturaleza='D' And Saldo" + mesTrabajo
            + "<0 THEN -Saldo" + mesTrabajo + " ELSE 0 END ) ");
        sql.append(" )<>0");
        return sql.toString();
    }

    private String addCondicionR000723() {
        StringBuilder sql = new StringBuilder(" AND ");
        sql.append(" (CASE WHEN SALDOAUX.NATURALEZA = 'C' AND  SALDOAUX.SALDO"
            + mesTrabajo + " >= 0 ");
        sql.append("               THEN  SALDOAUX.SALDO" + mesTrabajo);
        sql.append("               ELSE 0");
        sql.append("                END +  CASE WHEN SALDOAUX.NATURALEZA = 'D' AND  SALDOAUX.SALDO"
            + mesTrabajo + " < 0 ");
        sql.append("                           THEN  -  SALDOAUX.SALDO"
            + mesTrabajo);
        sql.append("                           ELSE 0");
        sql.append("                            END)");
        sql.append("       + (CASE WHEN SALDOAUX.NATURALEZA = 'D' AND  SALDOAUX.SALDO"
            + mesTrabajo + " >= 0 ");
        sql.append("                THEN  SALDOAUX.SALDO" + mesTrabajo + " ");
        sql.append("                ELSE 0 ");
        sql.append("                END + CASE WHEN SALDOAUX.NATURALEZA = 'C' AND  SALDOAUX.SALDO"
            + mesTrabajo + " < 0");
        sql.append("                          THEN -  SALDOAUX.SALDO"
            + mesTrabajo);
        sql.append("                          ELSE 0 ");
        sql.append("                          END) <> 0");

        return sql.toString();
    }

    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReportes(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReportes(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarClaseImpresion() {
        // <CODIGO_DESARROLLADO>
        if (("1").equals(opcionImpresion) || ("2").equals(opcionImpresion)) {
            centroCostoVisible = true;
            terceroVisible = true;
            auxiliarVisible = true;
            etiquetaAuxGenVisible = true;
            etiquetaAuxTerceroVisible = true;
            etiquetaCentrosCostoVisible = true;
        }
        else if (("3").equals(opcionImpresion)) {
            centroCostoVisible = false;
            terceroVisible = false;
            auxiliarVisible = false;
            etiquetaAuxGenVisible = false;
            etiquetaAuxTerceroVisible = false;
            etiquetaCentrosCostoVisible = false;

        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public String getOpcionImpresion() {
        return opcionImpresion;
    }

    public void setOpcionImpresion(String opcionImpresion) {
        this.opcionImpresion = opcionImpresion;
    }

    public Boolean getTercero() {
        return tercero;
    }

    public void setTercero(Boolean tercero) {
        this.tercero = tercero;
    }

    public String getTerceroC() {
        return terceroC;
    }

    public void setTerceroC(String terceroC) {
        this.terceroC = terceroC;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public Boolean getSaldoCero() {
        return saldoCero;
    }

    public void setSaldoCero(Boolean saldoCero) {
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

    public int getAnoTrabajo() {
        return anoTrabajo;
    }

    public void setAnoTrabajo(int anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    public int getMesTrabajo() {
        return mesTrabajo;
    }

    public void setMesTrabajo(int mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public String getCodigoLibro() {
        return codigoLibro;
    }

    public void setCodigoLibro(String codigoLibro) {
        this.codigoLibro = codigoLibro;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
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

    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
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

    public boolean isSaldoCeroVisible() {
        return saldoCeroVisible;
    }

    public void setSaldoCeroVisible(boolean saldoCeroVisible) {
        this.saldoCeroVisible = saldoCeroVisible;
    }

    public boolean isCentroCostoVisible() {
        return centroCostoVisible;
    }

    public void setCentroCostoVisible(boolean centroCostoVisible) {
        this.centroCostoVisible = centroCostoVisible;
    }

    public boolean isTerceroVisible() {
        return terceroVisible;
    }

    public void setTerceroVisible(boolean terceroVisible) {
        this.terceroVisible = terceroVisible;
    }

    public boolean isAuxiliarVisible() {
        return auxiliarVisible;
    }

    public void setAuxiliarVisible(boolean auxiliarVisible) {
        this.auxiliarVisible = auxiliarVisible;
    }

    public boolean isEtiquetaCentrosCostoVisible() {
        return etiquetaCentrosCostoVisible;
    }

    public void setEtiquetaCentrosCostoVisible(
        boolean etiquetaCentrosCostoVisible) {
        this.etiquetaCentrosCostoVisible = etiquetaCentrosCostoVisible;
    }

    public boolean isEtiquetaAuxTerceroVisible() {
        return etiquetaAuxTerceroVisible;
    }

    public void setEtiquetaAuxTerceroVisible(
        boolean etiquetaAuxTerceroVisible) {
        this.etiquetaAuxTerceroVisible = etiquetaAuxTerceroVisible;
    }

    public boolean isEtiquetaAuxGenVisible() {
        return etiquetaAuxGenVisible;
    }

    public void setEtiquetaAuxGenVisible(boolean etiquetaAuxGenVisible) {
        this.etiquetaAuxGenVisible = etiquetaAuxGenVisible;
    }

    public Boolean getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(Boolean auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getAuxiliarC() {
        return auxiliarC;
    }

    public void setAuxiliarC(String auxiliarC) {
        this.auxiliarC = auxiliarC;
    }

    public Boolean getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(Boolean centroCosto) {
        this.centroCosto = centroCosto;
    }

    public boolean isReferencia() {
        return referencia;
    }

    public void setReferencia(boolean referencia) {
        this.referencia = referencia;
    }

    public boolean isRecurso() {
        return recurso;
    }

    public void setRecurso(boolean recurso) {
        this.recurso = recurso;
    }

}
