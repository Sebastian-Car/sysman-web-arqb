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

import java.io.FileNotFoundException;
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
 * @author dsuesca
 * @version 1, 11/07/2016
 * @modifed jsforero
 * @version 2. 27/04/2017 Se quita codigo quemado y se remplaza por
 * MSM_TRANS_INTERRUMPIDA .
 * 
 * @author jgomez
 * @version 3, 09/08/2018 Se ajusta para que el reporte por excel
 * salga plano
 */
@ManagedBean
@ViewScoped
public class LisregabiertosControlador extends BeanBaseModal {
    private final String compania;
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
     * Creates a new instance of LisregabiertosControlador
     */
    public LisregabiertosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISREGABIERTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            fechaInicial = fechaFinal = new Date();
            tipoCuenta = "1";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LisregabiertosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>

    // <METODOS_BOTONES>
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        String reporte = null;
        String excelSalida = null;
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoCuenta", tipoCuenta);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.convertirAFechaCadena(fechaInicial,
                                            "dd/MM/yyyy"));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
            
            if ("SI".equals(obtenerParametro("MANEJA REFERENCIA Y DEPENDENCIA PARA INFORMES", "NO")))
			{
			reemplazar.put("referencia", "referencia,");
			reemplazar.put("dependencia", "dependencia,");
			} else {
				reemplazar.put("referencia", " ");
				reemplazar.put("dependencia", " ");
			}
            
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE

            if ("SI".equals(obtenerParametro(
                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                            "NO"))) {
                reporte = "001819LisRegAbiertosF";
                excelSalida = "001819LisRegAbiertosF_Excel";
            }
            else {
                reporte = "001025LisRegAbiertos";
                excelSalida = "001025LisRegAbiertos_EXCEL";
            }

            int modulo = Integer.parseInt(SessionUtil.getModulo());

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            archivoDescarga = JsfUtil.exportarExcelPlano(reporte, excelSalida,
                            ConectorPool.ESQUEMA_SYSMAN, formato, reemplazar,
                            parametros, Integer.valueOf(modulo));
        }

        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                                + ex.getMessage() + " " + reporte);
            Logger.getLogger(LisregabiertosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(LisregabiertosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (DRException e) {
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

    /**
     * metodos get y set
     * 
     * @return
     */
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getCompania() {
        return compania;
    }

}
