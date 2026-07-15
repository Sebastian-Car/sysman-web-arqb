package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelaciondeingresosControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
 * @author dsuesca
 * @version 2, 19/05/2016 17:18:36 -- Modificado por dsuesca
 *
 * @version 3, 12/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.
 * 
 * 
 * @version 4, 12/06/2018, jromero :<br>
 * Se actualiza el controlador se crean listas y se realiza proceso de
 * refactoring
 */
@ManagedBean
@ViewScoped
public class RelaciondeingresosControlador extends BeanBaseModal {

    // <DECLARAR_ATRIBUTOS>
    private boolean totalPorCuenta;
    private Date fecha;
    private StreamedContent archivoDescarga;
    private boolean ckCentro;
    /**
     * Variable que almacena el centro de costo inicial
     */
    private String centroInicial;
    /**
     * Variable que almacena el centro de costo final
     */
    private String centroFinal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena el centro de costo inicial desde la vista
     */
    private RegistroDataModelImpl listaInicial;
    /**
     * Lista que almacena el centro de costo final desde la vista
     */
    private RegistroDataModelImpl listaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private String compania;

    /**
     * Creates a new instance of RelaciondeingresosControlador
     */
    public RelaciondeingresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACIONDEINGRESOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            fecha = new Date();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RelaciondeingresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaInicial();
        cargarListaFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaInicial
     *
     */
    public void cargarListaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelaciondeingresosControladorUrlEnum.URL3900
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listaFinal
     *
     */
    public void cargarListaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelaciondeingresosControladorUrlEnum.URL4523
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        // param.put(RelaciondeingresosControladorEnum.PARAM0.getValue(),
        // centroInicial);

        listaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando45() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL97);

        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        try {
            String reporte = "000780RelacionDeIngresos";

            if (ckCentro) {
                reporte = "001799RelacionDeIngresoscentrocosto";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fecha", SysmanFunciones.formatearFecha(fecha));
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_FORMS_RELACIONDEINGRESOS_FECHA", "Periodo del "
                + SysmanFunciones.convertirAFechaCadena(fecha));

            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,

                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_FECHATXT",
                            SysmanFunciones.convertirAFechaCadena(fecha));
            parametros.put("PR_TOTALPORCUENTA", totalPorCuenta ? "SI" : "NO");
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Centrocosto
     * 
     * 
     */
    public void cambiarCentrocosto() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos().get("CODIGO").toString();
    }
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public Date getFecha() {
        return fecha;
    }

    public boolean isTotalPorCuenta() {
        return totalPorCuenta;
    }

    public void setTotalPorCuenta(boolean totalPorCuenta) {
        this.totalPorCuenta = totalPorCuenta;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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
    /**
     * Retorna la lista listaInicial
     * 
     * @return listaInicial
     */
    public RegistroDataModelImpl getListaInicial() {
        return listaInicial;
    }

    /**
     * Asigna la lista listaInicial
     * 
     * @param listaInicial
     * Variable a asignar en listaInicial
     */
    public void setListaInicial(RegistroDataModelImpl listaInicial) {
        this.listaInicial = listaInicial;
    }

    /**
     * Retorna la lista listaFinal
     * 
     * @return listaFinal
     */
    public RegistroDataModelImpl getListaFinal() {
        return listaFinal;
    }

    /**
     * Asigna la lista listaFinal
     * 
     * @param listaFinal
     * Variable a asignar en listaFinal
     */
    public void setListaFinal(RegistroDataModelImpl listaFinal) {
        this.listaFinal = listaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

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

    public boolean isCkCentro() {
        return ckCentro;
    }

    public void setCkCentro(boolean ckCentro) {
        this.ckCentro = ckCentro;
    }
}
