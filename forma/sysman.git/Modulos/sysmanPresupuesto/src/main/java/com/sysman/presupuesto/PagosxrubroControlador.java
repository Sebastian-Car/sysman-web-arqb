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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.PagosxrubroControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 08/07/2016
 * @author lcortes
 * @version 2, 07/12/2016 08:28:02 -- Modificado por lcortes
 * @modified jguerrero
 * @version 2. 19/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class PagosxrubroControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String anio;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreCuenta;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of PagosxrubroControlador
     */
    public PagosxrubroControlador() {
        super();
        compania = SessionUtil.getCompania();
        fechaInicial = new Date();
        fechaFinal = new Date();
        try {
            numFormulario = GeneralCodigoFormaEnum.PAGOSXRUBRO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PagosxrubroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {

        // <CODIGO_DESARROLLADO>
        /*
         * FR992-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagosxrubroControladorUrlEnum.URL3684
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 4001
    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PagosxrubroControladorUrlEnum.URL4120
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
        // 94052
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        generarInforme(ReportesBean.FORMATOS.PDF);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdExcel() {
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(anio)
            || (fechaInicial == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB894"));
            return;
        }
        if ((fechaFinal == null)
            || SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB894"));
            return;
        }
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB515"));
            return;
        }

        String cargoPresupuesto = "";
        String nombreJefePres = "";
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try {
            cargoPresupuesto = (String) SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO PRESUPUESTO",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ");

            nombreJefePres = (String) SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DE JEFE DE PRESUPUESTO",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ");

            // Reemplazos valores consulta reporte
            reemplazos.put("cuentaInicial", cuentaInicial);
            reemplazos.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazos.put("anio", anio);

            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAINICIALNOMBRE", nombreCuenta);
            parametros.put("PR_CARGO_PRESUPUESTO", cargoPresupuesto);
            parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO", nombreJefePres);

            Reporteador.resuelveConsulta("000995PagosXRubro",
                            Integer.parseInt(
                                            SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000995PagosXRubro",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException
                        | SystemException | SysmanException e) {
            Logger.getLogger(PagosxrubroControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        cuentaInicial = "";
        nombreCuenta = "";
        cargarListaCuentaInicial();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreCuenta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
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

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
