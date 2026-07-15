package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmsaldosxaplicarControladorEnum;
import com.sysman.predial.enums.FrmsaldosxaplicarControladorUrlEnum;
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
 * @author NGOMEZ
 * @version 1, 02/06/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @modifier amonroy
 * @version 3, 06/07/2017 Se realiza el Proceso de Refactoring a los
 * listados de los combos definidos en el controlador
 */
@ManagedBean
@ViewScoped
public class FrmsaldosxaplicarControlador extends BeanBaseModal {
    private final String compania;
    /**
     * Constante a nivel de clase que aloja el numero de orden predial
     */
    private final String nOrden;
    private final String cCodigo;
    // <DECLARAR_ATRIBUTOS>
    private String tipo;
    private String predioInicial;
    private String predioFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listapredioinicial;
    private RegistroDataModelImpl listaPredioFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmsaldosxaplicarControlador
     */
    public FrmsaldosxaplicarControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        cCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMSALDOSXAPLICAR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmsaldosxaplicarControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListapredioinicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listapredioinicial asociada al combo
     * 'CodigoInicialPredio'
     */
    public void cargarListapredioinicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsaldosxaplicarControladorUrlEnum.URL3909
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);

        listapredioinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista listaPredioFinal asoicada al combo
     * 'CodigoFinalPredio'
     */
    public void cargarListaPredioFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsaldosxaplicarControladorUrlEnum.URL4836
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);
        param.put(FrmsaldosxaplicarControladorEnum.CODIGO_INICIAL.getValue(),
                        predioInicial);

        listaPredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        String reporte = "000869PREDIALSALDOSXAPLICAR";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("predioInicial", predioInicial);
            reemplazar.put("predioFinal", predioFinal);
            reemplazar.put("tipo", tipo);
            reemplazar.put("nOrden", nOrden);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_FORMS_FRMSALDOSXAPLICAR_FECHAINI",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_FRMSALDOSXAPLICAR_FECHAFIN",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilapredioinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predioInicial = registroAux.getCampos().get(cCodigo).toString();

        predioFinal = null;
        cargarListaPredioFinal();
    }

    public void seleccionarFilaPredioFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predioFinal = registroAux.getCampos().get(cCodigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getPredioInicial() {
        return predioInicial;
    }

    public void setPredioInicial(String predioInicial) {
        this.predioInicial = predioInicial;
    }

    public String getPredioFinal() {
        return predioFinal;
    }

    public void setPredioFinal(String predioFinal) {
        this.predioFinal = predioFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListapredioinicial() {
        return listapredioinicial;
    }

    public void setListapredioinicial(
        RegistroDataModelImpl listapredioinicial) {
        this.listapredioinicial = listapredioinicial;
    }

    public RegistroDataModelImpl getListaPredioFinal() {
        return listaPredioFinal;
    }

    public void setListaPredioFinal(RegistroDataModelImpl listaPredioFinal) {
        this.listaPredioFinal = listaPredioFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
