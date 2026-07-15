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
import com.sysman.predial.enums.FacturacionPorDireccionControladorEnum;
import com.sysman.predial.enums.FacturacionPorDireccionControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @author jrodriguezr
 * @version 1, 08/06/2016
 * 
 * @author eamaya
 * @version 2.0, 28/06/2017 Proceso de Refactoring DSS
 * 
 */
@ManagedBean
@ViewScoped

public class FacturacionPorDireccionControlador extends BeanBaseModal {
    private final String compania;
    private final String numeroOrden;
    // <DECLARAR_ATRIBUTOS>
    private boolean conAcuerdos;
    private String direccionInicial;
    private String direccionFinal;
    private Date fechaCorte;
    private String masDeCuanto;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listadirinicial;
    private RegistroDataModelImpl listadirfin;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private static final String DIRECCION = "DIRECCION";

    /**
     * Creates a new instance of FacturacionPorDireccionControlador
     */
    public FacturacionPorDireccionControlador() {
        super();
        compania = SessionUtil.getCompania();
        numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURACION_POR_DIRECCION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FacturacionPorDireccionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaCorte = new Date();
        masDeCuanto = "0.0";
        cargarListadirinicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListadirinicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        numeroOrden);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionPorDireccionControladorUrlEnum.URL3557
                                                        .getValue());

        listadirinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, DIRECCION);

    }

    public void cargarListadirfin() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        numeroOrden);

        param.put(FacturacionPorDireccionControladorEnum.PARAM0.getValue(),
                        direccionInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionPorDireccionControladorUrlEnum.URL4271
                                                        .getValue());

        listadirfin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, DIRECCION);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private boolean agregarMensaje(String campo, String mensaje) {
        if (SysmanFunciones.validarVariableVacio(campo)) {
            JsfUtil.agregarMensajeAlerta(mensaje);
            return true;
        }
        return false;
    }

    private void mensajes() {
        if (agregarMensaje(direccionInicial, idioma.getString("TB_TB152"))) {
            return;
        }
        if (agregarMensaje(direccionFinal, idioma.getString("TB_TB154"))) {
            return;
        }
        if (agregarMensaje(masDeCuanto, idioma.getString("TB_TB155"))) {
            return;
        }
    }

    private void generaReporte(FORMATOS formato) {
        try {
            mensajes();
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000885PREDIALLISFACDIR2";
            reemplazar.put("direccionInicial", direccionInicial);
            reemplazar.put("direccionFinal", direccionFinal);
            reemplazar.put("numeroOrden", numeroOrden);
            reemplazar.put("masDeCuanto", masDeCuanto);
            reemplazar.put("conAcuerdos", "1");

            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_DIRINICIAL", direccionInicial);
            parametros.put("PR_DIRFIN", direccionFinal);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), "<br>",
                            e.getMessage()));
        }
    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFiladirinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        direccionInicial = registroAux.getCampos().get(DIRECCION).toString();
        cargarListadirfin();

    }

    public void seleccionarFiladirfin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        direccionFinal = registroAux.getCampos().get(DIRECCION).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getConAcuerdos() {
        return conAcuerdos;
    }

    public void setConAcuerdos(boolean conAcuerdos) {
        this.conAcuerdos = conAcuerdos;
    }

    public String getDireccionInicial() {
        return direccionInicial;
    }

    public void setDireccionInicial(String direccionInicial) {
        this.direccionInicial = direccionInicial;
    }

    public String getDireccionFinal() {
        return direccionFinal;
    }

    public void setDireccionFinal(String direccionFinal) {
        this.direccionFinal = direccionFinal;
    }

    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public String getMasDeCuanto() {
        return masDeCuanto;
    }

    public void setMasDeCuanto(String masDeCuanto) {
        this.masDeCuanto = masDeCuanto;
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

    public RegistroDataModelImpl getListadirinicial() {
        return listadirinicial;
    }

    public void setListadirinicial(RegistroDataModelImpl listadirinicial) {
        this.listadirinicial = listadirinicial;
    }

    public RegistroDataModelImpl getListadirfin() {
        return listadirfin;
    }

    public void setListadirfin(RegistroDataModelImpl listadirfin) {
        this.listadirfin = listadirfin;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
