package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.enums.CambiarhoramovimientoControladorEnum;
import com.sysman.almacen.enums.CambiarhoramovimientoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 18/01/2016
 *
 * @author jlramirez
 * @version 2, 26/04/2017, Se realizo refactoring y manejo de EJBs
 *
 * @author jrodriguezr
 * @version 3, 02/05/2017, Se ajusto el metodo cambiarHoraMovimiento
 * del ejb y agregando el campo de auditoria del usuario
 * 
 * @version 4, 16/08/2017, <strong>pespitia</strong>:<br>
 * Se adiciona la validación del estado del mes y el año antes de
 * cambiar la hora.
 *
 */
@ManagedBean
@ViewScoped
public class CambiarhoramovimientoControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion
     */
    private final String usuario;

    /** Atributo que almacena el anio asociado al movimiento */
    private int anio;

    /** Atributo que almacena el mes asociado al movimiento */
    private int mes;

    private Registro registro;
    private RegistroDataModelImpl listaTipoDeMovimiento;
    private RegistroDataModelImpl listaNumero;
    private String tipoDeMovimiento;
    private String numero;
    private String nombreTipoMovimiento;
    private StreamedContent archivoDescarga;
    private String hora;
    private String formatoHora;
    private String numeroDescripcion;

    /**
     * Atributo que controla la visibilidad del dialogo:
     * <code>ProcesoIrreversible</code>>
     */
    private boolean verProcesoIrre;

    @EJB
    private EjbAlmacenCeroRemote almacenCero;

    public CambiarhoramovimientoControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();

        registro = new Registro(new HashMap<String, Object>());
        
       
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIARHORAMOVIMIENTO_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoDeMovimiento();
        abrirFormulario();
    }

    public void renderizar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTipoDeMovimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarhoramovimientoControladorUrlEnum.URL2710
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipoDeMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaNumero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarhoramovimientoControladorUrlEnum.URL4609
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CambiarhoramovimientoControladorEnum.PARAM0.getValue(),
                        tipoDeMovimiento);
        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        if (faltanCamposObligatorios()) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("MSM_FALTAN_DATOS_PROCESO"));
        }

        try {
            if (almacenCero.verificarEstadoAlmacen(compania, anio, mes)) {
                verProcesoIrre = true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Validacion de campos obligatorios.
     *
     * @return Verdadero si faltan variables por diligenciar.
     */
    private boolean faltanCamposObligatorios() {
        return SysmanFunciones.validarVariableVacio(hora)
            || SysmanFunciones.validarVariableVacio(formatoHora)
            || SysmanFunciones.validarVariableVacio(tipoDeMovimiento)
            || SysmanFunciones.validarVariableVacio(String.valueOf(numero));
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
        // </CODIGO_DESARROLLADO>
    }

    public void obtenerReporteCambioHoraM(FORMATOS formatos) {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("tipoDeMovimiento", tipoDeMovimiento);
            reemplazar.put("numero", numero);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000462CambioHoraMovimientos";

            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);

            parametros.put("PR_STRSQL", strSql);

            parametros.put("PR_FORMS_CAMBIARHORAMOVIMIENTO_TIPODEMOVIMIENTO",
                            tipoDeMovimiento);

            parametros.put("PR_FORMS_CAMBIARHORAMOVIMIENTO_NUMERO", numero);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            " ", ex.getMessage()));
        }
        // <CODIGO_DESARROLLADO>
    }

    public void oprimirRevisarInforme() {
        // <CODIGO_DESARROLLADO>
        obtenerReporteCambioHoraM(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * ProcesoIrreversible en la vista.
     */
    public void aceptarProcesoIrreversible() {
        // <CODIGO_DESARROLLADO>
        verProcesoIrre = false;

        try {
            almacenCero.cambiarHoraMovimiento(compania, tipoDeMovimiento,
                            Long.parseLong(numero), hora, formatoHora, usuario);

            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoDeMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoDeMovimiento = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        nombreTipoMovimiento = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
        numero = null;
        numeroDescripcion = null;
        cargarListaNumero();
    }

    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()), "0")
                        .toString();

        numeroDescripcion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();

        anio = SysmanFunciones.ano((Date) registroAux.getCampos().get("FECHA"));
        mes = SysmanFunciones.mes((Date) registroAux.getCampos().get("FECHA"));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public RegistroDataModelImpl getListaTipoDeMovimiento() {
        return listaTipoDeMovimiento;
    }

    public void setListaTipoDeMovimiento(
        RegistroDataModelImpl listaTipoDeMovimiento) {
        this.listaTipoDeMovimiento = listaTipoDeMovimiento;
    }

    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }

    public String getTipoDeMovimiento() {
        return tipoDeMovimiento;
    }

    public void setTipoDeMovimiento(String tipoDeMovimiento) {
        this.tipoDeMovimiento = tipoDeMovimiento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombreTipoMovimiento() {
        return nombreTipoMovimiento;
    }

    public void setNombreTipoMovimiento(String nombreTipoMovimiento) {
        this.nombreTipoMovimiento = nombreTipoMovimiento;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFormatoHora() {
        return formatoHora;
    }

    public void setFormatoHora(String formatoHora) {
        this.formatoHora = formatoHora;
    }

    public String getNumeroDescripcion() {
        return numeroDescripcion;
    }

    public void setNumeroDescripcion(String numeroDescripcion) {
        this.numeroDescripcion = numeroDescripcion;
    }

    public boolean isVerProcesoIrre() {
        return verProcesoIrre;
    }

    public void setVerProcesoIrre(boolean verProcesoIrre) {
        this.verProcesoIrre = verProcesoIrre;
    }

}
