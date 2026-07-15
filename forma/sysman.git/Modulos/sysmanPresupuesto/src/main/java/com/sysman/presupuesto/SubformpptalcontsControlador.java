package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.SubformpptalcontsControladorEnum;
import com.sysman.presupuesto.enums.SubformpptalcontsControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 20/06/2016
 * 
 * @version 2, 21/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class SubformpptalcontsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String mesInicial;
    private String mesFinal;
    private String valorTotal;
    private String titulo;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String anio;
    private String codigo;
    private String nombre;
    private String naturaleza;
    private Map<String, Object> rid;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listatipoCpte;
    private RegistroDataModelImpl listatipoCpteE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Map<String, Object> parametrosEntrada;
    private String tipoCpte;
    private static final String STRCODIGO = "CODIGO";

    /**
     * Creates a new instance of SubformpptalcontsControlador
     */
    public SubformpptalcontsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBFORMPPTALCONTS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();
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
            Logger.getLogger(SubformpptalcontsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = SubformpptalcontsControladorEnum.TABLA.getValue();
        buscarLlave();
        mesInicial = mesFinal = "1";
        tipoCpte = "TODOS";
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListatipoCpte();
        cargarListatipoCpteE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SubformpptalcontsControladorUrlEnum.URL16201
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        parametrosListado.put(
                        SubformpptalcontsControladorEnum.PARAM0.getValue(),
                        tipoCpte);
        parametrosListado.put(
                        SubformpptalcontsControladorEnum.PARAM1.getValue(),
                        mesInicial);
        parametrosListado.put(
                        SubformpptalcontsControladorEnum.PARAM2.getValue(),
                        mesFinal);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListatipoCpte() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubformpptalcontsControladorUrlEnum.URL7780
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoCpte = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, STRCODIGO);
    }

    /**
     *
     * Carga la lista listatipoCpte
     *
     * 
     */
    public void cargarListatipoCpteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubformpptalcontsControladorUrlEnum.URL7780
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoCpteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, STRCODIGO);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
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
            String reporte = "000919LisMovPptalCont";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(SubformpptalcontsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    // <METODOS_CAMBIAR>
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        if ((mesInicial != null) && (mesFinal != null) && (tipoCpte != null)) {
            reasignarOrigen();
            actualizarTotales();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesFinal() {
        // <CODIGO_DESARROLLADO>
        if ((mesInicial != null) && (mesFinal != null) && (tipoCpte != null)) {
            reasignarOrigen();
            actualizarTotales();
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilatipoCpte(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if ((mesInicial != null) && (mesFinal != null)
            && (registroAux.getCampos().get(STRCODIGO) != null)) {
            tipoCpte = registroAux.getCampos().get(STRCODIGO).toString();
            reasignarOrigen();
            actualizarTotales();
        }
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        titulo = "CUENTA: " + codigo + " " + nombre.toUpperCase();
        valorTotal = "0.00";
        mesInicial = mesFinal = "1";
        tipoCpte = "TODOS";
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
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
        // NO SE IMPLEMENTA
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", "anio" };
        Object[] valores = { rid, anio };
        SessionUtil.redireccionarPorFormulario(SessionUtil.getModulo(),
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }

    // <SET_GET_ATRIBUTOS>
    public String getMesInicial() {
        return mesInicial;
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

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void actualizarTotales() {
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
            param.put(SubformpptalcontsControladorEnum.PARAM0.getValue(),
                            tipoCpte);
            param.put(SubformpptalcontsControladorEnum.PARAM1.getValue(),
                            mesInicial);
            param.put(SubformpptalcontsControladorEnum.PARAM2.getValue(),
                            mesFinal);
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubformpptalcontsControladorUrlEnum.URL16202
                                                                            .getValue())
                                            .getUrl(), param));
            valorTotal = regAux.getCampos().get("TOTAL").toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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

    public String getTipoCpte() {
        return tipoCpte;
    }

    public void setTipoCpte(String tipoCpte) {
        this.tipoCpte = tipoCpte;
    }

}
