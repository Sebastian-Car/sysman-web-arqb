package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.PrediallisnoticobroControladorEnum;
import com.sysman.predial.enums.PrediallisnoticobroControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 02/06/2016
 *
 * @author asana
 * @version 2, 13/06/2017 Se implementa enum en formulario a demas se
 * ajusta conexiï¿½n.
 *
 * @version 2, 11/07/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class PrediallisnoticobroControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String codigoCons;

    // <DECLARAR_ATRIBUTOS>
    private Boolean incluyeLotes;
    private String codigoIncial;
    private String codigoFinal;
    private String nombreCodigoInicial;
    private String nombreCodigoFinal;
    private String vlrSuperior;
    private String vlrInferior;
    private Date fechaCorte;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigoini;
    private RegistroDataModelImpl listacodigofin;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of PrediallisnoticobroControlador
     */
    public PrediallisnoticobroControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALLISNOTICOBRO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigoini();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaCorte = new Date();
        vlrInferior = "999999999999";
        vlrSuperior = "0.0";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacodigoini() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallisnoticobroControladorUrlEnum.URL3743
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listacodigoini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListacodigofin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallisnoticobroControladorUrlEnum.URL4746
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PrediallisnoticobroControladorEnum.CODIGOINICIAL.getValue(),
                        codigoIncial);
        listacodigofin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    private void obtenerReporte(FORMATOS formatos) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", codigoIncial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            reemplazar.put("anoCorte", SysmanFunciones.ano(fechaCorte));
            // Valida cuando se encuentra seleccionado el check
            // Incluye lotes.
            if (incluyeLotes) {
                reemplazar.put("condicion", "");
            }
            else {
                reemplazar.put("condicion",
                                "AND    IP_USUARIOS_PREDIAL.AREA_CONSTRUIDA > 0");

            }
            reemplazar.put("valorInferior", vlrInferior);
            reemplazar.put("valorSuperior", vlrSuperior);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_FECHACORTE",
                            SysmanFunciones.convertirAFechaCadena(fechaCorte));
            parametros.put("PR_VLRINFERIOR", vlrInferior);
            parametros.put("PR_CODIGOINI", codigoIncial);
            parametros.put("PR_CODIGOFIN", codigoFinal);
            parametros.put("PR_VLRSUPERIOR", vlrSuperior);
            Reporteador.resuelveConsulta("000854PREDIALLISNOTICOBRO1",
                            Integer.valueOf(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000854PREDIALLISNOTICOBRO1",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formatos);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando55() {
        // <CODIGO_DESARROLLADO>
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigoini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoIncial = registroAux.getCampos().get(codigoCons).toString();
        nombreCodigoInicial = registroAux.getCampos().get("NOMBRE").toString();
        codigoFinal = " ";
        nombreCodigoFinal = " ";
        cargarListacodigofin();
    }

    public void seleccionarFilacodigofin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoCons).toString();
        nombreCodigoFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getCodigoIncial() {
        return codigoIncial;
    }

    public Boolean getIncluyeLotes() {
        return incluyeLotes;
    }

    public void setIncluyeLotes(Boolean incluyeLotes) {
        this.incluyeLotes = incluyeLotes;
    }

    public void setCodigoIncial(String codigoIncial) {
        this.codigoIncial = codigoIncial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getNombreCodigoInicial() {
        return nombreCodigoInicial;
    }

    public void setNombreCodigoInicial(String nombreCodigoInicial) {
        this.nombreCodigoInicial = nombreCodigoInicial;
    }

    public String getNombreCodigoFinal() {
        return nombreCodigoFinal;
    }

    public void setNombreCodigoFinal(String nombreCodigoFinal) {
        this.nombreCodigoFinal = nombreCodigoFinal;
    }

    public String getVlrSuperior() {
        return vlrSuperior;
    }

    public void setVlrSuperior(String vlrSuperior) {
        this.vlrSuperior = vlrSuperior;
    }

    public String getVlrInferior() {
        return vlrInferior;
    }

    public void setVlrInferior(String vlrInferior) {
        this.vlrInferior = vlrInferior;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacodigoini() {
        return listacodigoini;
    }

    public void setListacodigoini(RegistroDataModelImpl listacodigoini) {
        this.listacodigoini = listacodigoini;
    }

    public RegistroDataModelImpl getListacodigofin() {
        return listacodigofin;
    }

    public void setListacodigofin(RegistroDataModelImpl listacodigofin) {
        this.listacodigofin = listacodigofin;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
