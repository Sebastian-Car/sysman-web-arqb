package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.SubformmovpptalsControladorEnum;
import com.sysman.presupuesto.enums.SubformmovpptalsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 17/06/2016
 * 
 * @author eamaya
 * @version 2, 20/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 * 
 */
@ManagedBean
@ViewScoped
public class SubformmovpptalsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String mesInicial;
    private String mesFinal;
    private String titulo;
    private String valorTotal;
    private String saldoInicial;
    private String saldoFinal;
    private String tipoCpte;
    private StreamedContent archivoDescarga;
    /**
     * Constante para el tipo de comprobante TODOS.
     */
    private static final String TIPO_CPTE_TODOS = "TODOS";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String anio;
    private String codigo;
    private String nombre;
    private String naturaleza;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMes;
    private List<Registro> listaMesFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listatipoCpte;
    private RegistroDataModelImpl listatipoCpteE;
    private Map<String, Object> rid;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbGeneralesRemote ejbFuncionGeneral;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    // corregir calcularSaldos()

    /**
     * Creates a new instance of SubformmovpptalsControlador
     */
    public SubformmovpptalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBFORMMOVPPTALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                anio = (String) parametrosEntrada.get("anio");
                codigo = (String) parametrosEntrada.get("codigo");
                nombre = (String) parametrosEntrada.get("nombre");
                naturaleza = (String) parametrosEntrada.get("naturaleza");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SubformmovpptalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            // No limpia parametros flash
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.DETALLE_COMPROBANTE_PPTAL
                        .getTable();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaMes();
        mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
        cargarListaMesFinal();
        mesFinal = String.valueOf(SysmanFunciones.mes(new Date()));
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        tipoCpte = TIPO_CPTE_TODOS;
        cargarListatipoCpte();
        cargarListatipoCpteE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        reasignarOrigen();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubformmovpptalsControladorUrlEnum.URL6969
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigo);
        parametrosListado.put(
                        SubformmovpptalsControladorEnum.PARAM0.getValue(),
                        mesInicial);
        parametrosListado.put(
                        SubformmovpptalsControladorEnum.PARAM1.getValue(),
                        mesFinal);
        parametrosListado.put(
                        SubformmovpptalsControladorEnum.PARAM2.getValue(),
                        naturaleza);
        parametrosListado.put(
                        SubformmovpptalsControladorEnum.PARAM3.getValue(),
                        nombre);

        parametrosListado.put(
                        SubformmovpptalsControladorEnum.PARAM4.getValue(),
                        tipoCpte);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMes() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubformmovpptalsControladorUrlEnum.URL11290
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaMesFinal
     *
     */
    public void cargarListaMesFinal() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);
            param.put(SubformmovpptalsControladorEnum.PARAM9.getValue(),
                            String.valueOf(mesInicial));

            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubformmovpptalsControladorUrlEnum.URL11842
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListatipoCpte() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubformmovpptalsControladorUrlEnum.URL12399
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listatipoCpte = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");

    }

    /**
     *
     * Carga la lista listatipoCpte.
     */
    public void cargarListatipoCpteE() {
        listatipoCpteE = listatipoCpte;

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimirMov() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        if ((mesInicial != null) && (mesFinal != null) && (tipoCpte != null)) {
            reasignarOrigen();

            actualizarTotales();
            calcularSaldos();
        }
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesFinal() {
        // <CODIGO_DESARROLLADO>
        if ((mesInicial != null) && (mesFinal != null) && (tipoCpte != null)) {
            reasignarOrigen();
            actualizarTotales();
            calcularSaldos();
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilatipoCpte(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoCpte = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), " ")
                        .toString();
        if ((mesInicial != null) && (mesFinal != null) && (tipoCpte != null)) {
            reasignarOrigen();
            actualizarTotales();
            calcularSaldos();
        }
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        titulo = SysmanFunciones.concatenar(idioma.getString("TB_TB437"), " ",
                        codigo, " \n", nombre.toUpperCase());
        valorTotal = "0.00";
        actualizarTotales();
        calcularSaldos();
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", anio);
            reemplazar.put("codigo", codigo);
            reemplazar.put("naturaleza", naturaleza);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("nombre", nombre);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_PLANPRESUPUESTAL_ID", codigo);
            parametros.put("PR_FORMS_PLANPRESUPUESTAL_NOMBRE", nombre);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMTERCERO", "SI"
                            .equals(SysmanFunciones.nvl(ejbParametro
                                            .consultarParametro(compania,
                                                            "LISTADO DE MOVIMIENTOS PRESUPUESTALES CON TERCEROS",
                                                            modulo, new Date(),
                                                            false),
                                            "NO")));
            String reporte = "000916LisMovimientoPptalTer";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            e.getMessage()));
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // Metodo heredado de BeanBaseContinuoAcme
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", "anio" };
        Object[] valores = { rid, anio };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de BeanBaseContinuoAcme
    }

    // <SET_GET_ATRIBUTOS>

    public String getMesInicial() {
        return mesInicial;
    }

    public String getTipoCpte() {
        return tipoCpte;
    }

    public void setTipoCpte(String tipoCpte) {
        this.tipoCpte = tipoCpte;
    }

    public RegistroDataModelImpl getListatipoCpte() {
        return listatipoCpte;
    }

    public void setListatipoCpte(RegistroDataModelImpl listatipoCpte) {
        this.listatipoCpte = listatipoCpte;
    }

    public RegistroDataModelImpl getListatipoCpteE() {
        return listatipoCpteE;
    }

    public void setListatipoCpteE(RegistroDataModelImpl listatipoCpteE) {
        this.listatipoCpteE = listatipoCpteE;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(String saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public String getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(String saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void actualizarTotales() {
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            codigo);
            param.put(
                            SubformmovpptalsControladorEnum.PARAM0.getValue(),
                            mesInicial);
            param.put(
                            SubformmovpptalsControladorEnum.PARAM1.getValue(),
                            mesFinal);
            param.put(
                            SubformmovpptalsControladorEnum.PARAM2.getValue(),
                            naturaleza);
            param.put(
                            SubformmovpptalsControladorEnum.PARAM3.getValue(),
                            nombre);

            param.put(
                            SubformmovpptalsControladorEnum.PARAM4.getValue(),
                            tipoCpte);

            Registro regAux;

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubformmovpptalsControladorUrlEnum.URL22679
                                                                            .getValue())
                                            .getUrl(), param));

            valorTotal = regAux.getCampos().get("TOTAL").toString();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void calcularSaldos() {
        try {

            BigDecimal saldoApropacion = ejbFuncionGeneral
                            .consultarEjecucionPresupuestal(compania,
                                            "SALDOAPROPIACION",
                                            Integer.parseInt(anio), codigo,
                                            Integer.parseInt(mesInicial) - 1);

            BigDecimal disponibilidadAcum = ejbFuncionGeneral
                            .consultarEjecucionPresupuestal(compania,
                                            "DISPONIBILIDADACUM",
                                            Integer.parseInt(anio), codigo,
                                            Integer.parseInt(mesInicial) - 1);

            saldoInicial = SysmanFunciones
                            .nvlStr(saldoApropacion.subtract(disponibilidadAcum)
                                            .toString(), "0");

            BigDecimal saldoApropacionFin = ejbFuncionGeneral
                            .consultarEjecucionPresupuestal(compania,
                                            "SALDOAPROPIACION",
                                            Integer.parseInt(anio), codigo,
                                            Integer.parseInt(mesFinal));

            BigDecimal disponibilidadAcumFin = ejbFuncionGeneral
                            .consultarEjecucionPresupuestal(compania,
                                            "DISPONIBILIDADACUM",
                                            Integer.parseInt(anio), codigo,
                                            Integer.parseInt(mesFinal));

            saldoFinal = SysmanFunciones.nvlStr(
                            saldoApropacionFin.subtract(disponibilidadAcumFin)
                                            .toString(),
                            "0");

        }
        catch (NumberFormatException | SystemException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

}
