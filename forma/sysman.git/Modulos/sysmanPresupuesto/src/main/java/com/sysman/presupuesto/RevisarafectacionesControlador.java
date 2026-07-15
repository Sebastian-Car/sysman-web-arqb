package com.sysman.presupuesto;

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
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.presupuesto.enums.RevisarafectacionesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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
 * @author NGOMEZ
 * @version 1, 15/06/2016
 *
 *
 * @author ybecerra
 * @version 2, 19/04/2017 Revision Sonar y Refactoring
 *
 */
@ManagedBean
@ViewScoped

public class RevisarafectacionesControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String anio;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaanio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;
    @EJB
    private EjbPresupuestoCeroRemote ejbPresupuestoCero;

    /**
     * Creates a new instance of RevisarafectacionesControlador
     */
    public RevisarafectacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REVISARAFECTACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RevisarafectacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaanio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        anio = String.valueOf(SysmanFunciones.getParteFecha(
                        new Date(),
                        Calendar.YEAR));
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaanio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisarafectacionesControladorUrlEnum.URL3043
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void oprimirInconsistencias() {
        archivoDescarga = null;

        Map<String, Object> reemplazos = new TreeMap<>();

        reemplazos.put("compania", compania);
        reemplazos.put("anio", anio);

        Map<String, Object> param = new TreeMap<>();

        Reporteador.resuelveConsulta("002024LisCompAfectInconsistentes",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos, param);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "002024LisCompAfectInconsistentes", param,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            boolean aux = ejbPresupuestoCero.revisarAfectacionesPresupuestales(
                            compania,
                            Integer.parseInt(anio),
                            SessionUtil.getUser().getCodigo());

            if (aux) {
                ejecutarmensajeArchivo();
            }
        }
        catch (SystemException e) {
            Logger.getLogger(RevisarafectacionesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto mensajeArchivo en
     * la vista
     *
     */
    public void ejecutarmensajeArchivo() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB234"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaanio() {
        return listaanio;
    }

    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
