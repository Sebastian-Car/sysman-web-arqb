package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LisboletindiariotesoreriaControladorEnum;
import com.sysman.contabilidad.enums.LisboletindiariotesoreriaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
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
 * @version 1, 12/05/2016
 * 
 * @author eamaya
 * @version 2, 11/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 */

@ManagedBean
@ViewScoped

public class LisboletindiariotesoreriaControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    private String separadas;
    private String porFechas;
    private String cortolima;
	private String cuentaCaja;
    private Date fecha;
    private String observaciones;
    private Date fechaInicial;
    private StreamedContent archivoDescarga;
    private int anioFecha;
    private String clasesCuentas;
    private boolean fechaInicialVisible;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaCaja;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbParametroUno;

    /**
     * Creates a new instance of LisboletindiariotesoreriaControlador
     */
    public LisboletindiariotesoreriaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISBOLETINDIARIOTESORERIA_CONTROLADOR.getCodigo();
            validarPermisos();   
        }
        catch (Exception ex) {
            Logger.getLogger(LisboletindiariotesoreriaControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() { 
        try {
            clasesCuentas = "NO"
                            .equals(SysmanFunciones.nvl(ejbParametroUno
                                            .consultarParametro(compania,
                                                            "SOLO CUENTA CAJA BOLETIN",
                                                            modulo, new Date(),
                                                            false),
                                            "NO")) ? "'B','J'" : "'J'";
            cortolima = (String) SysmanFunciones
                    .nvl(ejbParametroUno.consultarParametro(compania,
                                    "BOLETIN DIARIO TESORERIA CORTOLIMA",
                                    modulo, new Date(), false),
                                    "NO");
            
            fecha = new Date();
            anioFecha = SysmanFunciones.getParteFecha(fecha, Calendar.YEAR);
            cargarListaCuentaCaja();
        }
        catch (SystemException e) {
            Logger.getLogger(LisboletindiariotesoreriaControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicialVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaCaja() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisboletindiariotesoreriaControladorUrlEnum.URL4221
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anioFecha);
        param.put(LisboletindiariotesoreriaControladorEnum.PARAM2.getValue(),
                        clasesCuentas);

        listaCuentaCaja = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;   
        String reporte = tipoReporte();
        try {
            if ("true".equals(porFechas)
                            && (SysmanFunciones.getParteFecha(fechaInicial,
                                            Calendar.MONTH) != SysmanFunciones
                                            .getParteFecha(fecha,
                                                            Calendar.MONTH))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB838"));
                return;
            }

            String formatoCalidad = (String) SysmanFunciones.nvl(ejbParametroUno
                            .consultarParametro(compania, "FORMATO CALIDAD",
                                            modulo, new Date(), false),
                            "NO"); 
            
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("fechaInicial", SysmanFunciones.nvl(
                            SysmanFunciones.convertirAFechaCadena(fechaInicial),
                            ""));

            reemplazar.put("fecha",
                            SysmanFunciones.convertirAFechaCadena(fecha));
            reemplazar.put("anio", SysmanFunciones.getParteFecha(fecha,
                            Calendar.YEAR));

            reemplazar.put("cuentaCaja", cuentaCaja);

            reemplazar.put("mes",
                            SysmanFunciones.getParteFecha(fecha, Calendar.MONTH)
                            + 1);

            reemplazar.put("mesAnterior", SysmanFunciones.getParteFecha(fecha,
                            Calendar.MONTH));

            reemplazar.put("formatoCalidad",
                            formatoCalidad(formatoCalidad, cortolima));

            reemplazar.put("comprobante", boletinDetallado(cortolima));

            Map<String, Object> parametros = new HashMap<>();

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_FORMATO_CALIDAD",
                            "SI".equals(formatoCalidad) ? true : false);

            parametros.put("PR_TOTAL_VISIBLE", "SI".equals(formatoCalidad)
                            || "SI".equals(cortolima) ? false : true);

            parametros.put("PR_FORMS_LISBOLETINDIARIOTESORERIA_FECHA",
                            lisboletinDiarioTesoreriaFecha());

            parametros.put("PR_FORMS_LISBOLETINDIARIOTESORERIA_OBSERVACIONES",
                            observaciones);

            parametros.put("PR_FIRMA_BOLETIN_1",
                            SysmanFunciones.nvl(ejbParametroUno
                                            .consultarParametro(compania,
                                                            "FIRMA BOLETIN 1",
                                                            modulo, new Date(),
                                                            false),
                                            ""));

            parametros.put("PR_CARGO_BOLETIN_1",
                            SysmanFunciones.nvl(ejbParametroUno
                                            .consultarParametro(compania,
                                                            "CARGO BOLETIN 1",
                                                            modulo, new Date(),
                                                            false),
                                            ""));

            parametros.put("PR_DOCUMENTO_BOLETIN_1",
                            SysmanFunciones.nvl(ejbParametroUno
                                            .consultarParametro(compania,
                                                            "DOCUMENTO BOLETIN 1",
                                                            modulo, new Date(),
                                                            false),
                                            ""));

            parametros.put("PR_FIRMA_BOLETIN_2",
                            SysmanFunciones.nvl(ejbParametroUno
                                            .consultarParametro(compania,
                                                            "FIRMA BOLETIN 2",
                                                            modulo, new Date(),
                                                            false),
                                            ""));

            parametros.put("PR_CARGO_BOLETIN_2",
                            SysmanFunciones.nvl(ejbParametroUno
                                            .consultarParametro(compania,
                                                            "CARGO BOLETIN 2",
                                                            modulo, new Date(),
                                                            false),
                                            ""));

            parametros.put("PR_DOCUMENTO_BOLETIN_2",
                            SysmanFunciones.nvl(ejbParametroUno
                                            .consultarParametro(compania,
                                                            "DOCUMENTO BOLETIN 2",
                                                            modulo, new Date(),
                                                            false),
                                            ""));

            parametros.put("PR_PIE_VISIBLE",
                            "true".equals(porFechas) ? false : true);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        } catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(LisboletindiariotesoreriaControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } catch ( JRException
                        | ParseException
                        | OutOfMemoryError | IOException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                            + ex.getMessage());
            Logger.getLogger(LisboletindiariotesoreriaControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {    
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }



    }

    private Object boletinDetallado(String cortolima) {
        String boletinDetallado;

        boletinDetallado = "SI".equals(cortolima) ? "''"
            : "BOLETINDETALLADO.COMPROBANTE";
        return boletinDetallado;
    }

    private Object lisboletinDiarioTesoreriaFecha() {
        String porFecha = null;
        try {
            porFecha = "true".equals(porFechas)
                            ? SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial)
                                + " y "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fecha)
                                : SysmanFunciones.convertirAFechaCadena(fecha);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return porFecha;
    }

    private Object formatoCalidad(String formatoCalidad, String cortolima) {
        String formato;
        formato = "SI".equals(formatoCalidad)
                        || "SI".equals(cortolima) ? "INNER" : "RIGHT";
        return formato;
    }

    private String tipoReporte() 
    {
        String reporte;
        if("SI".equals(cortolima))
        {
        	reporte = "true".equals(porFechas)
                    ? "002438LisBoletinDiarioTesoreriaFechasCOR"
                        : "002437LisBoletinDiarioTesoreriaCOR";
        }
        else
        {
	        reporte = "true".equals(porFechas)
	                        ? "000778LisBoletinDiarioTesoreriaFechas"
	                            : "000764LisBoletinDiarioTesoreriaCOS";
        }
        return reporte;
    }
    public void cambiarFecha() { 
        anioFecha = SysmanFunciones.getParteFecha(fecha, Calendar.YEAR);
        cuentaCaja = null;
    }

    public void cambiarfechas() {
        fechaInicialVisible = !fechaInicialVisible;
    }

    public void onRowSelectCuentaCaja(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaCaja = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
    }
    /**
     * metodos get y set
     * @return
     */
    public String getSeparadas() {
        return separadas;
    }

    public void setSeparadas(String separadas) {
        this.separadas = separadas;
    }

    public String getPorFechas() {
        return porFechas;
    }

    public void setPorFechas(String porFechas) {
        this.porFechas = porFechas;
    }

    public String getCuentaCaja() {
        return cuentaCaja;
    }

    public void setCuentaCaja(String cuentaCaja) {
        this.cuentaCaja = cuentaCaja;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public int getAnioFecha() {
        return anioFecha;
    }

    public void setAnioFecha(int anioFecha) {
        this.anioFecha = anioFecha;
    }

    public String getClasesCuentas() {
        return clasesCuentas;
    }

    public void setClasesCuentas(String clasesCuentas) {
        this.clasesCuentas = clasesCuentas;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public boolean isFechaInicialVisible() {
        return fechaInicialVisible;
    }

    public void setFechaInicialVisible(boolean fechaInicialVisible) {
        this.fechaInicialVisible = fechaInicialVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaCaja() {
        return listaCuentaCaja;
    }

    public void setListaCuentaCaja(RegistroDataModelImpl listaCuentaCaja) {
        this.listaCuentaCaja = listaCuentaCaja;
    }
    
    public String getCortolima() {
		return cortolima;
	}

	public void setCortolima(String cortolima) {
		this.cortolima = cortolima;
	}
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
