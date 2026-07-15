package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.LrevisarecaudosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 20/08/2016
 *
 * @author eamaya
 * @version 2.0, 07/06/2017 Proceso de Refactoring, Manejo de EJBs y
 * Correcciones SonarLint
 *
 * @version 3, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped

public class LrevisarecaudosControlador extends BeanBaseModal {
    private final String compania;

    private final String constanteFechaPreparacion;

    private final String constanteFrecuencuaPeriodosFacturacion;
    // <DECLARAR_ATRIBUTOS>
    private String ciclo;
    private StreamedContent archivoDescarga;
    private String frecuenciaPeriodosFacturacion;
    private Date fechaPreparacion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmaUtil;

    /**
     * Creates a new instance of LrevisarecaudosControlador
     */
    public LrevisarecaudosControlador() {
        super();
        compania = SessionUtil.getCompania();
        constanteFechaPreparacion = "FECHA_PREPARACION";
        constanteFrecuencuaPeriodosFacturacion = "FRECUENCIA PERIODOS DE FACTURACION";
        try {
            numFormulario = GeneralCodigoFormaEnum.LREVISARECAUDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LrevisarecaudosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            frecuenciaPeriodosFacturacion = ejbSysmaUtil.consultarParametro(
                            compania, constanteFrecuencuaPeriodosFacturacion,
                            SessionUtil.getModulo(), new Date(), false);

        }
        catch (SystemException e) {
            Logger.getLogger(LrevisarecaudosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (NullPointerException e) {
            Logger.getLogger(LrevisarecaudosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB999").replace(
                            "#parametro#",
                            constanteFrecuencuaPeriodosFacturacion));

        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LrevisarecaudosControladorUrlEnum.URL4164
                                                                            .getValue())
                                            .getUrl(), param));

            if (!listaCiclo.isEmpty()) {
                ciclo = listaCiclo.get(0).getCampos()
                                .get(GeneralParameterEnum.NUMERO.getName())
                                .toString();
                fechaPreparacion = listaCiclo.get(0).getCampos()
                                .get(constanteFechaPreparacion) == null ? null
                                    : (Date) listaCiclo.get(0).getCampos()
                                                    .get(constanteFechaPreparacion);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirVerificar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        revisaRecaudos();

    }

    public void revisaRecaudos() {

        if (!validarVacios()) {
            return;
        }
        Date datFecha = SysmanFunciones.sumarRestarDiasFecha(fechaPreparacion,
                        1);
        int dias = 0;
        switch (frecuenciaPeriodosFacturacion) {
        case "M":
            dias = 30;
            break;
        case "B":
        case "C":
            dias = 60;
            break;
        case "T":
            dias = 90;
            break;
        case "S":
            dias = 180;
            break;
        case "A":
            dias = 360;
            break;

        default:
            break;
        }
        Date datFechaFin = SysmanFunciones.sumarRestarDiasFecha(datFecha, dias);
        generarInforme(datFecha, datFechaFin);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVacios() {
        if (frecuenciaPeriodosFacturacion == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB999").replace(
                            "#parametro#",
                            constanteFrecuencuaPeriodosFacturacion));
            return false;
        }
        if (fechaPreparacion == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1292"));
            return false;
        }
        return true;
    }

    private void generarInforme(Date fechaInicial, Date fechaFinal) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "001041TMPERRORREC";
        // MANEJO DE PARAMETROS DEL REPORTE
        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarCiclo() {
        fechaPreparacion = service.buscarEnListaObj(ciclo,
                        GeneralParameterEnum.NUMERO.getName(),
                        constanteFechaPreparacion, listaCiclo) == null ? null
                            : (Date) service.buscarEnListaObj(ciclo,
                                            GeneralParameterEnum.NUMERO
                                                            .getName(),
                                            constanteFechaPreparacion,
                                            listaCiclo);
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
