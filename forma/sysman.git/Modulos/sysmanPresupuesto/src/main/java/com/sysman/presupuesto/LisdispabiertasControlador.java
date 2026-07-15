package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 07/07/2016
 * @version 2, 27/04/2017, jsforero : Se corrige error de formato en
 * la consulta oracle, originiado por el formta de la fecha
 * 
 * @version 3, 12/05/2017, pespitia:<br>
 * Se reemplaz� el texto quemado por etiquetas del archivo
 * properties.
 * 
 * @author jreina
 * @version 4, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 * 
 * @author jgomez
 * @version 5, 9/08/2018 Se ajusta para que el reporte por excel salga
 * plano
 */
@ManagedBean
@ViewScoped
public class LisdispabiertasControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String consFormato;
    // <DECLARAR_ATRIBUTOS>
    private String tipoCuenta;
    private Date fechaInicial;
    private Date fechaFinal;

    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LisdispabiertasControlador
     */

    public LisdispabiertasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consFormato = "dd/MM/yyyy";
        try {
            numFormulario = GeneralCodigoFormaEnum.LISDISPABIERTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LisdispabiertasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        tipoCuenta = "1";
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>

    public void obtenerReporte(FORMATOS formatos) {
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            String nombreReporte = null;
            reemplazar.put("tipoCuenta", tipoCuenta);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, consFormato));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, consFormato));
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            String entreFechas = idioma.getString("TB_TB3151").replace(
                            "#FECHAINI#",
                            SysmanFunciones.convertirAFechaCadena(fechaInicial,
                                            consFormato))
                            .replace("#FECHAFIN#", SysmanFunciones
                                            .convertirAFechaCadena(fechaFinal,
                                                            consFormato));
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_ENTREFECHAS", entreFechas);

            if ("SI".equals(obtenerParametro(
                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                            "NO"))) {
                nombreReporte = "001814LisDispAbiertasF";
            }
            else {
                nombreReporte = "000985LisDispAbiertas";
            }
            String excelSalida = nombreReporte + "_Excel";

            archivoDescarga = JsfUtil.exportarExcelPlano(nombreReporte,
                            excelSalida,
                            ConectorPool.ESQUEMA_SYSMAN, formatos, reemplazar,
                            parametros, Integer.valueOf(modulo));
        }
        catch (JRException | IOException | SysmanException
                        | SQLException | DRException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
    // <METODOS_BOTONES>

    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
