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
import com.sysman.predial.enums.PrediallisfacturacionxfirmasControladorEnum;
import com.sysman.predial.enums.PrediallisfacturacionxfirmasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @author ybecerra
 * @version 1, 15/06/2016
 *
 * @version 2, 11/07/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class PrediallisfacturacionxfirmasControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String facturacionInicial;
    private String facturacionFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listafacturaInicial;
    private RegistroDataModelImpl listafacturaFinal;

    /**
     * Creates a new instance of
     * PrediallisfacturacionxfirmasControlador
     */
    public PrediallisfacturacionxfirmasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALLISFACTURACIONXFIRMAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PrediallisfacturacionxfirmasControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListafacturaInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListafacturaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallisfacturacionxfirmasControladorUrlEnum.URL3159
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listafacturaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PrediallisfacturacionxfirmasControladorEnum.DOCNUM
                                        .getValue());
    }

    public void cargarListafacturaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallisfacturacionxfirmasControladorUrlEnum.URL4082
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PrediallisfacturacionxfirmasControladorEnum.FACTURAINICIAL
                        .getValue(), facturacionInicial);
        listafacturaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PrediallisfacturacionxfirmasControladorEnum.DOCNUM
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        String reporte = "000912PREDIALLISFACXFIRMAS";
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("numeroOrden",
                            SysmanFunciones.colocarComillas(
                                            SysmanConstantes.NUMERO_ORDEN_PREDIAL));
            reemplazar.put("facturaInicial", SysmanFunciones
                            .colocarComillas(facturacionInicial));
            reemplazar.put("facturaFinal",
                            SysmanFunciones.colocarComillas(facturacionFinal));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_ENCABEZADO",
                            SysmanFunciones.concatenar("Entra Facturas ",
                                            facturacionInicial, " y ",
                                            facturacionFinal));

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SysmanException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void onRowSelectfacturaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturacionInicial = registroAux.getCampos()
                        .get(PrediallisfacturacionxfirmasControladorEnum.DOCNUM
                                        .getValue())
                        .toString();
        facturacionFinal = null;
        cargarListafacturaFinal();
    }

    public void onRowSelectfacturaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturacionFinal = registroAux.getCampos()
                        .get(PrediallisfacturacionxfirmasControladorEnum.DOCNUM
                                        .getValue())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getFacturacionInicial() {
        return facturacionInicial;
    }

    public void setFacturacionInicial(String facturacionInicial) {
        this.facturacionInicial = facturacionInicial;
    }

    public String getFacturacionFinal() {
        return facturacionFinal;
    }

    public void setFacturacionFinal(String facturacionFinal) {
        this.facturacionFinal = facturacionFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListafacturaInicial() {
        return listafacturaInicial;
    }

    public void setListafacturaInicial(
        RegistroDataModelImpl listafacturaInicial) {
        this.listafacturaInicial = listafacturaInicial;
    }

    public RegistroDataModelImpl getListafacturaFinal() {
        return listafacturaFinal;
    }

    public void setListafacturaFinal(RegistroDataModelImpl listafacturaFinal) {
        this.listafacturaFinal = listafacturaFinal;
    }
}
