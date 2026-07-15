package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
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
import com.sysman.serviciospublicos.enums.ActualizarMedidorControladorUrlEnum;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosUnoGeneralRemote;
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

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 23/08/2016
 * @version 2, 11/05/2017 - spina, refactorizacion para dss y
 * depuracion sonar
 */
@ManagedBean
@ViewScoped

public class ActualizarMedidorControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String opcion;
    private String ciclo;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosUnoGeneralRemote ejbServiciosPublicosUnoGeneral;

    /**
     * Creates a new instance of ActualizarMedidorControlador
     */
    public ActualizarMedidorControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZAR_MEDIDOR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ActualizarMedidorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        opcion = "1";
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
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCiclo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ActualizarMedidorControladorUrlEnum.URL3665
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(ActualizarMedidorControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        switch (opcion) {
        case "1":
            redireccionarFormulario();
            break;
        case "2":
            actualizarMedidores();
            break;
        case "3":
            getReporte("001048Usuariosconmedidorrepetido");
            break;
        case "4":
            getReporte("001049Usuariosmedidordudoso");
            break;
        case "5":
            getReporte("001050medidorrepetido");
            break;
        default:
            break;
        }

        // </CODIGO_DESARROLLADO>
    }

    private void redireccionarFormulario() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ciclo", ciclo);
        Direccionador direccionador = new Direccionador();
        if (autorizacionMicroMedicion()) {
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.USUARIOSINMEDIDORMICRO_CONTROLADOR
                                            .getCodigo()));
        }
        else {
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.USUARIOSINMEDIDOR_CONTROLADOR
                                            .getCodigo()));
        }
        direccionador.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog(direccionador);
    }

    private void actualizarMedidores() {
        try {

            boolean valor = ejbServiciosPublicosUnoGeneral.actualizarMedidores(
                            compania, Integer.parseInt(ciclo),
                            SessionUtil.getUser()
                                            .getCodigo());

            if (valor) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1114"));
            }

        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(ActualizarMedidorControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void getReporte(String reporte) {

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        reemplazar.put("compania", compania);
        reemplazar.put("ciclo", ciclo);

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        parametros.put("PR_CICLO", ciclo);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean autorizacionMicroMedicion() {
        String nitValidar = SysmanFunciones
                        .nvlStr(SessionUtil.getCompaniaIngreso().getNit(), "");
        nitValidar = nitValidar.replace(".", "");
        nitValidar = nitValidar.replace("-", "");
        nitValidar = nitValidar.replace(" ", "");
        nitValidar = nitValidar.substring(0, 9);
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA PROCESO DE MICROMEDICION",
                            SessionUtil.getModulo(), new Date(),
                            true);
        }
        catch (SystemException e) {
            Logger.getLogger(ActualizarMedidorControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ((parametro != null) && !"SI".equals(parametro)) {
            return false;
        }

        return validarNit(nitValidar);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    private boolean validarNit(String nitValidar) {
        switch (nitValidar) {
        case "844000755":// 'YOPAL
        case "832000776":// 'FUNZA
        case "832001512":// 'MADRID
        case "899999714":// 'CHIA
        case "890680053":// 'FUSAGASUGA
            return true;
        default:
            return false;
        }
    }

    // <SET_GET_ATRIBUTOS>
    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

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
