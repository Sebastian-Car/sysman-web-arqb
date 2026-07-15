package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.AcummensualControladorUrlEnum;
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
 * @version 1, 11/05/2016
 *
 * @modified jguerrero
 * @version 2. 11/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * reemplaza el envio de la conexion por el nombre de conexion para
 * generar el reporte.
 */
@ManagedBean
@ViewScoped

public class AcummensualControlador extends BeanBaseModal {
    private final String compania;
    private String codigo;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreTipo;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of AcummensualControlador
     */
    public AcummensualControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACUMMENSUAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AcummensualControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipo();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        fechaInicial = fechaFinal = new Date();
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTipo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcummensualControladorUrlEnum.URL3665
                                                        .getValue());

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

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

    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigo = registroAux.getCampos().get("CODIGO").toString();
        nombreTipo = registroAux.getCampos().get("NOMBRE").toString();
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        if (SysmanFunciones.validarVariableVacio(codigo)
            || (fechaInicial == null)
            || (fechaFinal == null)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB139"));
            return;
        }
        if (fechaInicial.after(fechaFinal)) {
            fechaInicial = null;
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB140"));
            return;
        }
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        try {
            // Reemplazos valores consulta reporte
            reemplazos.put("anio", SysmanFunciones.ano(fechaInicial));
            reemplazos.put("tipoCpte", codigo);
            reemplazos.put("fechaInicial", SysmanFunciones
                            .formatearFecha(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            // Inicio Parametros Informe
            parametros.put("PR_COMPANIA", compania);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_TIPO", nombreTipo);
            parametros.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "CARGO DE JEFE DE CONTABILIDAD FDF",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE DE JEFE DE CONTABILIDAD",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            " "));

            Reporteador.resuelveConsulta("000756AcumMensual",
                            Integer.parseInt(
                                            SessionUtil.getModulo()),
                            reemplazos, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("000756AcumMensual",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }
}
