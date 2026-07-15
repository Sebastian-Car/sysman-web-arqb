package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.enums.PredialrelaciondeingresosControladorEnum;
import com.sysman.predial.enums.PredialrelaciondeingresosControladorUrlEnum;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 31/05/2016
 *  
 * @version 2, 12/07/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class PredialrelaciondeingresosControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante que identifica el nombre del campo CODIGOBANCO
     */
    private final String campoCodBanco;
    // <DECLARAR_ATRIBUTOS>
    private String resumen;
    private String bancoInicial;
    private String bancoFinal;
    private String nombreBancoInicial;
    private String nombreBancoFinal;
    private String tipo;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodibancoini;
    private RegistroDataModelImpl listacodibancofin;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    @EJB
    private EjbPredialCeroRemote ejbPredialCeroRemote;

    /**
     * Creates a new instance of PredialrelaciondeingresosControlador
     */
    public PredialrelaciondeingresosControlador() {
        super();
        compania = SessionUtil.getCompania();

        campoCodBanco = "CODIGOBANCO";
        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALRELACIONDEINGRESOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PredialrelaciondeingresosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodibancoini();
        cargarListacodibancofin();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacodibancoini() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialrelaciondeingresosControladorUrlEnum.URL4239
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodibancoini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodBanco);
    }

    public void cargarListacodibancofin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialrelaciondeingresosControladorUrlEnum.URL4832
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialrelaciondeingresosControladorEnum.PARAM0.getValue(),
                        bancoInicial);

        listacodibancofin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodBanco);
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

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {

            String reporte = definirReporte();

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", "'" + compania + "'");
            reemplazar.put("bancoInicio", "'" + bancoInicial + "'");
            reemplazar.put("bancoFinal", "'" + bancoFinal + "'");
            reemplazar.put("fechaInicio", SysmanFunciones
                            .formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("nOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");

            // reemplazar.put("valorCar", ((valorCar > 13) &&
            // (valorCar < 21))
            // ? "IP_RECIBOS_DE_PAGO.C" + valorCar : "0")
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_FORMS_PREDIAL_RELACION_DE_INGRESOS_FECHAINI",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_PREDIAL_RELACION_DE_INGRESOS_FECHAFIN",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_DETALLE_VISIBLE",
                            ("true").equals(resumen) ? false : true);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            // El reporte recibe por parametro el nombre de la
            // columna, para el caso,
            // el concepto1 esta relacionado con el concepto 16
            String aux1;
            aux1 = ejbPredialCeroRemote.consultarEncabezadoDeColumna(compania, 16);
            parametros.put("CONCEPTO1", aux1);

            // El reporte recibe por parametro el nombre de la
            // columna, para el caso,
            // el concepto1 esta relacionado con el concepto 18
            String aux2;
            aux2 = ejbPredialCeroRemote.consultarEncabezadoDeColumna(compania, 18);
            parametros.put("CONCEPTO2", aux2);

            // El reporte recibe por parametro el nombre de la
            // columna, para el caso,
            // el concepto1 esta relacionado con el concepto 17
            String aux3;
            aux3 = ejbPredialCeroRemote.consultarEncabezadoDeColumna(compania, 17);
            parametros.put("CONCEPTO3", aux3);

            // El reporte recibe por parametro el nombre de la
            // columna, para el caso,
            // el concepto1 esta relacionado con el concepto 3
            String aux4;
            aux4 = ejbPredialCeroRemote.consultarEncabezadoDeColumna(compania, 3);
            parametros.put("CONCEPTO4", aux4);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String definirReporte() {
        String reporte = "";
        switch (tipo) {
        case "1":
            resumen = "false";
            reporte = "001421PREDIALRELDIACARINGREANODUI";
            break;
        case "3":
            reporte = "000847PAGOSPORANOSANTERIORES";
            break;
        case "2":
            resumen = "true";
            reporte = "001421PREDIALRELDIACARINGREANODUI";
            break;
        default:
            break;
        }
        return reporte;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodibancoini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos().get(campoCodBanco).toString();
        nombreBancoInicial = registroAux.getCampos()
                        .get("NOMBREBANCO").toString();
        cargarListacodibancofin();
    }

    public void seleccionarFilacodibancofin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos().get(campoCodBanco).toString();
        nombreBancoFinal = registroAux.getCampos().get("NOMBREBANCO").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getBancoInicial() {
        return bancoInicial;
    }

    public void setBancoInicial(String bancoInicial) {
        this.bancoInicial = bancoInicial;
    }

    public String getBancoFinal() {
        return bancoFinal;
    }

    public void setBancoFinal(String bancoFinal) {
        this.bancoFinal = bancoFinal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getNombreBancoInicial() {
        return nombreBancoInicial;
    }

    public void setNombreBancoInicial(String nombreBancoInicial) {
        this.nombreBancoInicial = nombreBancoInicial;
    }

    public String getNombreBancoFinal() {
        return nombreBancoFinal;
    }

    public void setNombreBancoFinal(String nombreBancoFinal) {
        this.nombreBancoFinal = nombreBancoFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListacodibancoini() {
        return listacodibancoini;
    }

    public void setListacodibancoini(RegistroDataModelImpl listacodibancoini) {
        this.listacodibancoini = listacodibancoini;
    }

    public RegistroDataModelImpl getListacodibancofin() {
        return listacodibancofin;
    }

    public void setListacodibancofin(RegistroDataModelImpl listacodibancofin) {
        this.listacodibancofin = listacodibancofin;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
