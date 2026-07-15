package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LisBoletinCajaFechasControladorEnum;
import com.sysman.contabilidad.enums.LisBoletinCajaFechasControladorUrlEnum;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 10/05/2016
 * @version 2, 11/04/2017 modificado por jcrodriguez descripcion:--depuracion
 *          del controlador --creacion de dss o servicios para consultas
 *          quemadas
 * @author yrojas
 * @version 3, 20/04/2017 Se cambiaron los llamados de Acciones por las
 *          invocaciones de los ejb.
 * @author asana
 * @version 4, 12/06/2017 Se implementa enum en formulario y modifica conexi�n         
 *
 */
@ManagedBean
@ViewScoped
public class LisBoletinCajaFechasControlador extends BeanBaseModal {
    /**
     * variable ejb para solicitar un servicio
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * variable que alamcena la compa�ia
     */
    private final String compania;
    /**
     * variable que alamcena el modulo
     */
    private final String modulo;
    /**
     * variable que alamcena el saldo cero
     */
    private boolean saldoCero;
    /**
     * variable que alamacen el estado con firmas
     */
    private boolean conFirmas;
    /**
     * variable que alamcena la cuenta inicial
     */
    private String cuentaIni;
    /**
     * variable que almacena la cuenta final
     */
    private String cuentaFin;
    /**
     * variable que alamcena la fecha inicial
     */
    private Date fechaIni;
    /**
     * variable que almacena la fecha inicial
     */
    private Date fechaFin;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * lista la cuenta inicial
     */
    private RegistroDataModelImpl listaCuentaini;
    /**
     * lista la cuenta final
     */
    private RegistroDataModelImpl listacuentafin;
    /**
	/**
     * Creates a new instance of LisBoletinCajaFechasControlador
     */
    public LisBoletinCajaFechasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LIS_BOLETIN_CAJA_FECHAS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
            Logger.getLogger(LisBoletinCajaFechasControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } 	

    }

    /**
     * metodo que inicializa el formulario
     */
    @PostConstruct
    public void init() {
        cargarListaCuentaini();
        cargarListacuentafin();
        abrirFormulario();
    }

    /**
     * metodo al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        saldoCero=true;
        fechaFin = new Date();
        fechaIni = SysmanFunciones.sumarRestarDiasFecha(fechaFin, -365);
        cargarListaCuentaini();
        cargarListacuentafin();
    }

    /**
     * metodo que carga la ilsta de la cuenta inicial
     */
    public void cargarListaCuentaini() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(LisBoletinCajaFechasControladorUrlEnum.URL3590.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisBoletinCajaFechasControladorEnum.FECHAINI.getValue(), SysmanFunciones.ano(fechaIni));

        listaCuentaini = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que carga la lista de la cuenta final
     */
    public void cargarListacuentafin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(LisBoletinCajaFechasControladorUrlEnum.URL4559.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisBoletinCajaFechasControladorEnum.FECHAINI.getValue(), SysmanFunciones.ano(fechaIni));
        param.put(LisBoletinCajaFechasControladorEnum.CUENTAINI.getValue(), cuentaIni);

        listacuentafin = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que se llama al oprimir el boton pdf
     */
    public void oprimirImprimir() {

        archivoDescarga = null;
        generaBoletinesDeCaja(ReportesBean.FORMATOS.PDF);

    }

    /**
     * metodo que se llama al oprimir el boton excel
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generaBoletinesDeCaja(ReportesBean.FORMATOS.EXCEL97);
    }

    /**
     * metodo que se llama cuando se cambia la fecha inicial
     */
    public void cambiarFechaini() {

        cuentaIni = null;
        cuentaFin = null;
        cargarListaCuentaini();
        cargarListacuentafin();

    }

    /**
     * metodo que se llama cuando se selecciona un registro de un combo grande
     * 
     * @param event
     */
    public void seleccionarFilaCuentaini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaIni = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListacuentafin();
    }

    /**
     * metodo que se llama cuando se selecciona un registro de un combo grande
     * 
     * @param event
     */
    public void seleccionarFilacuentafin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFin = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * metodo que contienen la logia para imprimir un reporte
     * 
     * @param formato
     */
    public void generaInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
            
        try {
            
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("id_codigo", "ID");
            reemplazar.put("mesAnterior", SysmanFunciones.getParteFecha(fechaIni, Calendar.MONTH));
            reemplazar.put("id_cuenta", "ID");
            reemplazar.put("anio", SysmanFunciones.getParteFecha(fechaIni, Calendar.YEAR));
            reemplazar.put("tipoInicial", "AAA");
            reemplazar.put("tipoFinal", "ZZZ");
            reemplazar.put("cuentaInicial", cuentaIni);
            reemplazar.put("cuentaFinal", cuentaFin);
            reemplazar.put(LisBoletinCajaFechasControladorEnum.FECHAINICIAL.getValue(),
                            SysmanFunciones.convertirAFechaCadena(fechaIni));
            reemplazar.put(LisBoletinCajaFechasControladorEnum.FECHAFINAL.getValue(),
                            SysmanFunciones.convertirAFechaCadena(fechaFin));
            reemplazar.put("es_id", 1);
            reemplazar.put("filtrosTercero", "");
            reemplazar.put("filtrosCentro", "");
            parametros.put("PR_ENTREFECHAS", "DESDE EL " + SysmanFunciones.convertirAFechaCadena(fechaIni) + " Y "
                            + SysmanFunciones.convertirAFechaCadena(fechaFin));
            parametros.put("PR_CARGO_ENCARGADO_DE_TESORERIA", ejbSysmanUtil.consultarParametro(compania,
                            "CARGO ENCARGADO DE TESORERIA", modulo, new Date(), false));
            parametros.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA", ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE ENCARGADO DE TESORERIA", modulo, new Date(), false));
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_TITULO_INFORME_FLUJO_DE_CAJA",ejbSysmanUtil.consultarParametro(compania,
                            "TITULO INFORME FLUJO DE CAJA", modulo, new Date(), false));
            String strsqlBase = Reporteador.resuelveConsulta("800044BaseAuxiliares", Integer.valueOf(modulo),
                            reemplazar);
            reemplazar = new HashMap<>();
            reemplazar.put("baseAux", strsqlBase);
            reemplazar.put("condicionSaldoCero",
                            saldoCero ? "" : " HAVING SUM(SALDOS_AUX.SALDO_ANT + SALDOS_AUX.SALDO) <> 0 ");

            Reporteador.resuelveConsulta(LisBoletinCajaFechasControladorEnum.INFORME4.getValue(),
                            Integer.valueOf(modulo), reemplazar, parametros);
            
                archivoDescarga = JsfUtil.exportarStreamed(LisBoletinCajaFechasControladorEnum.INFORME4.getValue(),
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException | ParseException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
    

    }

    /**
     * metodo que genera el boletin de caja
     * 
     * @param formato
     */
    public void generaBoletinesDeCaja(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
            try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("ano", SysmanFunciones.ano(fechaIni));
            reemplazar.put("fechaInicial", SysmanFunciones.formatearFecha(fechaIni));
            reemplazar.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFin));

            parametros.put("PR_ENTREFECHAS", "DESDE EL " + SysmanFunciones.convertirAFechaCadena(fechaIni) + " Y "
                            + SysmanFunciones.convertirAFechaCadena(fechaFin));
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_CARGO_TESORERO", SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE ENCARGADO DE TESORERIA", modulo, new Date(), false),"").toString());
            parametros.put("PR_NOMBRE_TESORERO", SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE TESORERO", modulo, new Date(), false),"").toString());
            parametros.put("CON_FIRMAS", conFirmas);

            Reporteador.resuelveConsulta(LisBoletinCajaFechasControladorEnum.INFORME1.getValue(),
                            Integer.valueOf(modulo), reemplazar, parametros);
            ByteArrayInputStream boletindeCaja3 = JsfUtil.serializarReporte(
                            LisBoletinCajaFechasControladorEnum.INFORME1.getValue(), parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
            Reporteador.resuelveConsulta(LisBoletinCajaFechasControladorEnum.INFORME2.getValue(),
                            Integer.valueOf(modulo), reemplazar, parametros);
            ByteArrayInputStream relCompEmitidos = JsfUtil.serializarReporte(
                            LisBoletinCajaFechasControladorEnum.INFORME2.getValue(), parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
            StringBuilder condicion=new StringBuilder("");
            condicion.append(" WHERE SALDOANTERIOR + SALDOFINAL <> 0 ");
            reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("anio", SysmanFunciones.getParteFecha(fechaIni, Calendar.YEAR));
            reemplazar.put("cuentaInicial", cuentaIni);
            reemplazar.put("cuentaFinal", cuentaFin);                
            reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaIni));
            reemplazar.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFin));                        
            reemplazar.put("condicion",saldoCero?"":condicion.toString());
            Reporteador.resuelveConsulta(LisBoletinCajaFechasControladorEnum.INFORME3.getValue(),
                            Integer.valueOf(modulo), reemplazar, parametros);	
            ByteArrayInputStream boletindeCaja1 = 
                            JsfUtil.serializarReporte(
                            LisBoletinCajaFechasControladorEnum.INFORME3.getValue(), parametros, ConectorPool.ESQUEMA_SYSMAN,formato);

            ByteArrayInputStream[] reportes = { boletindeCaja1, boletindeCaja3, relCompEmitidos };
            String extension = "";
            switch (formato) {
            case PDF:
                extension = ".pdf";
                break;
            case EXCEL97:
                extension = ".xls";
                break;
            case EXCEL:
                extension = ".xlsx";
                break;
            case CSV:
                extension = ".csv";
                break;
            default:

                break;
            }
            String[] nombreReportes = { LisBoletinCajaFechasControladorEnum.INFORME3.getValue() + extension,
                                        LisBoletinCajaFechasControladorEnum.INFORME1.getValue() + extension,
                                        LisBoletinCajaFechasControladorEnum.INFORME2.getValue() + extension };

            
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(reportes, nombreReportes);
            }
            catch (JRException | IOException | SQLException | DRException | ParseException | SystemException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }


    }

    /**
     *  metodos get y set
     * @return
     */
    public boolean getSaldoCero() {
        return saldoCero;
    }

    public void setSaldoCero(boolean saldoCero) {
        this.saldoCero = saldoCero;
    }

    public boolean getConFirmas() {
        return conFirmas;
    }

    public void setConFirmas(boolean conFirmas) {
        this.conFirmas = conFirmas;
    }

    public String getCuentaIni() {
        return cuentaIni;
    }

    public void setCuentaIni(String cuentaIni) {
        this.cuentaIni = cuentaIni;
    }

    public String getCuentaFin() {
        return cuentaFin;
    }

    public void setCuentaFin(String cuentaFin) {
        this.cuentaFin = cuentaFin;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCuentaini() {
        return listaCuentaini;
    }

    public void setListaCuentaini(RegistroDataModelImpl listaCuentaini) {
        this.listaCuentaini = listaCuentaini;
    }

    public RegistroDataModelImpl getListacuentafin() {
        return listacuentafin;
    }

    public void setListacuentafin(RegistroDataModelImpl listacuentafin) {
        this.listacuentafin = listacuentafin;
    }
}
