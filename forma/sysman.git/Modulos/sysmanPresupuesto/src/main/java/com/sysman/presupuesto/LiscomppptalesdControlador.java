package com.sysman.presupuesto;

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
import com.sysman.presupuesto.enums.LiscomppptalesdControladorEnum;
import com.sysman.presupuesto.enums.LiscomppptalesdControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author acaceres
 * @version 1, 08/07/2016
 * @modified jsforero
 * @version 2. 19/04/2017 Se realizo el refactory.
 */
@ManagedBean
@ViewScoped
public class LiscomppptalesdControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "CODIGO"
     */
    private final String codigo;
    /**
     * Constante definida para almacenar la cadena
     * "MSM_TRANS_INTERRUMPIDA"
     */
    private final String mensaje;
    // <DECLARAR_ATRIBUTOS>
    private String formato;
    private String tipoInicial;
    private String tipoFinal;
    private Date fechaIInicial;
    private Date fechaFinal;
    private String nombreTipoInicial;
    private String nombreTipoFin;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of LiscomppptalesdControlador
     */
    public LiscomppptalesdControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigo = "CODIGO";
        mensaje = "MSM_TRANS_INTERRUMPIDA";
        try {
            numFormulario = GeneralCodigoFormaEnum.LISCOMPPPTALESD_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LiscomppptalesdControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaIInicial = new Date();
        fechaFinal = new Date();

        formato = "1";

        /*
         * FR989-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 3, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipoInicial() {
	UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LiscomppptalesdControladorUrlEnum.URL3980.getValue()); 	
			Map<String,Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.name(),compania);
 
        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigo);
    }

    public void cargarListaTipoFinal() {
	UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LiscomppptalesdControladorUrlEnum.URL4694.getValue()); 	
			Map<String,Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.name(),compania);
			param.put(LiscomppptalesdControladorEnum.CODIGOINICIAL.getValue(),tipoInicial);
 
        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    public void obtenerReporteLcpd(FORMATOS formatos) {
            String reporte="";
        try {        
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaIInicial, "yyyyMMdd"));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "yyyyMMdd"));

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String entreTipos = "Entre tipos " + tipoInicial + " y " + tipoFinal
                + "";
            String entreFechas = "Listado de comprobantes entre "
                + SysmanFunciones.convertirAFechaCadena(fechaIInicial,
                                "dd/MM/yyyy")
                + " y " + SysmanFunciones.convertirAFechaCadena(fechaFinal,
                                "dd/MM/yyyy")
                + " ";
            parametros.put("PR_ENTRETIPOS", entreTipos);
            parametros.put("PR_ENTREFECHAS", entreFechas);

            if ("1".equals(formato)) {
                reporte="000993LisCompPptalesD";
                Reporteador.resuelveConsulta(reporte,
                                Integer.valueOf(modulo), reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(
                                reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);
            }
            else {
                reporte="000994LisCompPptalesDCorto";
                Reporteador.resuelveConsulta(reporte,
                                Integer.valueOf(modulo), reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(
                                reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);
            }

        }catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(LiscomppptalesdControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        } 
        catch ( JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(mensaje)
                                + ex.getMessage());
            Logger.getLogger(LiscomppptalesdControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException|ParseException e) {
            Logger.getLogger(LiscomppptalesdControlador.class.getName())
                            .log(Level.SEVERE, null, e);

        }
       

    }

    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporteLcpd(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporteLcpd(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(codigo),"").toString();
        nombreTipoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()),"").toString();
        tipoFinal = "";
        nombreTipoFin = "";
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(codigo),"").toString();
        nombreTipoFin = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()),"").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    public Date getFechaIInicial() {
        return fechaIInicial;
    }

    public void setFechaIInicial(Date fechaIInicial) {
        this.fechaIInicial = fechaIInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreTipoInicial() {
        return nombreTipoInicial;
    }

    public void setNombreTipoInicial(String nombreTipoInicial) {
        this.nombreTipoInicial = nombreTipoInicial;
    }

    public String getNombreTipoFin() {
        return nombreTipoFin;
    }

    public void setNombreTipoFin(String nombreTipoFin) {
        this.nombreTipoFin = nombreTipoFin;
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
    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
