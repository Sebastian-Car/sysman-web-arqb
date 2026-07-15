package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.PagoapoderadosControladorUrlEnum;
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
 * @author lcortes
 * @version 1, 20/05/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 12/04/2017
 */
@ManagedBean
@ViewScoped

public class PagoapoderadosControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String nitApoderado;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreApoderado;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaApoderado;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of PagoapoderadosControlador
     */
    public PagoapoderadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PAGOAPODERADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PagoapoderadosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaApoderado();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaApoderado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PagoapoderadosControladorUrlEnum.URL3268
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaApoderado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            if (SysmanFunciones.validarVariableVacio(nitApoderado)
                || (fechaInicial == null) || (fechaFinal == null)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB514"));
                return;
            }
            if (fechaInicial.after(fechaFinal)) {
                fechaInicial = null;
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB515"));
                return;
            }

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // Reemplazos consulta del reporte
            reemplazar.put("tercero", nitApoderado);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            // Parámetros del reporte
            parametros.put("PR_CARGO_ENCARGADO_DE_TESORERIA",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "CARGO ENCARGADO DE TESORERIA",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE ENCARGADO DE TESORERIA",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_CARGO_REPRESENTANTE_LEGAL",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "CARGO REPRESENTANTE LEGAL",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            " "));

            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE REPRESENTANTE LEGAL",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            " "));

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Reporteador.resuelveConsulta("000791PagosApoderado",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000791PagosApoderado",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | ParseException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaApoderado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nitApoderado = registroAux.getCampos().get("NIT").toString();
        nombreApoderado = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getNitApoderado() {
        return nitApoderado;
    }

    public void setNitApoderado(String nitApoderado) {
        this.nitApoderado = nitApoderado;
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

    public String getNombreApoderado() {
        return nombreApoderado;
    }

    public void setNombreApoderado(String nombreApoderado) {
        this.nombreApoderado = nombreApoderado;
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
    public RegistroDataModelImpl getListaApoderado() {
        return listaApoderado;
    }

    public void setListaApoderado(RegistroDataModelImpl listaApoderado) {
        this.listaApoderado = listaApoderado;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
