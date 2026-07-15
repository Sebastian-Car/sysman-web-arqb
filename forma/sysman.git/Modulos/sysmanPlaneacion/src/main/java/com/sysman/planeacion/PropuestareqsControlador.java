package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.TercerosControlador;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.ejb.EjbPlaneacionUnoRemote;
import com.sysman.planeacion.enums.PropuestareqsControladorEnum;
import com.sysman.planeacion.enums.PropuestareqsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 16/12/2015
 * 
 * @author jcrodriguez,Refactoring y Depuracion, se elimina la logica
 * de las pestañas detalle y informacion presupuestal
 * @version 2, 08/09/2017
 */
@ManagedBean
@ViewScoped

public class PropuestareqsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private boolean cargar;
    private boolean viabilidadVisible;
    private boolean respondioBloqueado;
    private Registro registroSub;
    private List<Registro> listacmbDependencia;
    private RegistroDataModelImpl listaSubppropuesta;

    private RegistroDataModelImpl listaProponente;
    private RegistroDataModelImpl listaProponenteE;
    private String auxiliar;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;
    private RegistroDataModelImpl listaNombre;
    private RegistroDataModelImpl listaNombreE;
    private RegistroDataModelImpl listacmbActividad;
    private String auxNitC = "";
    private String auxSucursalC = "";
    private int auxC = 0;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaComboResponsable;
    private RegistroDataModelImpl listaactividadCombo;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPlaneacionUnoRemote ejbPlaneacionUno;

    public PropuestareqsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.PROPUESTAREQS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
            registro = new Registro(new HashMap<String, Object>());

        }
        catch (Exception ex) {
            Logger.getLogger(PropuestareqsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        tabla = GenericUrlEnum.REQUISICION.getTable();
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos()

    {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PropuestareqsControladorUrlEnum.URL2923
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PropuestareqsControladorUrlEnum.URL2625
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PropuestareqsControladorUrlEnum.URL2938
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.REQUISICION
                                        .getDeleteKey());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PropuestareqsControladorUrlEnum.URL2940
                                                        .getValue());

    }

    private String validarParametroCadena(Map<String, Object> campos,
        String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    @Override
    public void iniciarListas() {
        cargarListaProponente();
        cargarListaProponenteE();
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaNombre();
        cargarListaNombreE();
        cargarListacmbActividad();
        cargarListacmbDependencia();
        cargarListaactividadCombo();
        SessionUtil.cleanFlash();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaComboResponsable();
        cargarListaSubppropuesta();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubppropuesta = null;

    }

    public void cargarListaSubppropuesta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.PROPUESTA
                                                        .getGridKey());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PropuestareqsControladorEnum.COD_REQUISICION.getValue(),
                        registro.getCampos()
                                        .get(PropuestareqsControladorEnum.COD_REQUISICION
                                                        .getValue()));

        try {
            listaSubppropuesta = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            PropuestareqsControladorEnum.PROPUESTA
                                                            .getValue()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbDependencia() {
        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listacmbDependencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PropuestareqsControladorUrlEnum.URL2926
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProponente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PropuestareqsControladorUrlEnum.URL2928
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PropuestareqsControladorEnum.NIT.getValue());
    }

    public void cargarListaProponenteE() {
        listaProponenteE = listaProponente;
    }

    public void cargarListaElemento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PropuestareqsControladorUrlEnum.URL2928
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        PropuestareqsControladorEnum.CODIGOELEMENTO.getValue());
    }

    public void cargarListaElementoE() {
        listaElementoE = listaElemento;
    }

    public void cargarListaNombre() {
        listaNombre = listaElemento;
    }

    public void cargarListaNombreE() {
        listaNombreE = listaElemento;
    }

    public void cargarListacmbActividad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PropuestareqsControladorUrlEnum.URL2932
                                                        .getValue());

        listacmbActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true,
                        PropuestareqsControladorEnum.COD_ACTIVIDAD.getValue());

    }

    public void cargarListaComboResponsable() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PropuestareqsControladorUrlEnum.URL2934
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.DEPENDENCIA.getName()));
        listaComboResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        PropuestareqsControladorEnum.RESPONSABLE.getValue());
    }

    public void cargarListaactividadCombo() {
        listaactividadCombo = listacmbActividad;
    }

    public void agregarRegistroSubSubppropuesta() {
        if (!SysmanFunciones.validarCampoVacio(registroSub.getCampos(),
                        PropuestareqsControladorEnum.NIT.getValue())
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            PropuestareqsControladorEnum.COD_REQUISICION
                                            .getValue())) {
            StringBuilder criterio = new StringBuilder();
            criterio.append("PROPUESTA.COMPANIA =''");
            criterio.append(compania);
            criterio.append("''");
            criterio.append(" AND PROPUESTA.COD_REQUISICION = ''");
            criterio.append(registro.getCampos()
                            .get(PropuestareqsControladorEnum.COD_REQUISICION
                                            .getValue()));
            criterio.append("''");
            int consecutivo;
            try {
                consecutivo = (int) ejbSysmanUtil.generarSiguienteConsecutivo(
                                PropuestareqsControladorEnum.PROPUESTA
                                                .getValue(),
                                criterio.toString(),
                                PropuestareqsControladorEnum.COD_PROPUESTA
                                                .getValue());
                registroSub.getCampos()
                                .put(PropuestareqsControladorEnum.COD_PROPUESTA
                                                .getValue(), consecutivo);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        try {
            registroSub.getCampos()
                            .put(PropuestareqsControladorEnum.COD_REQUISICION
                                            .getValue(),
                                            registro.getCampos()
                                                            .get(PropuestareqsControladorEnum.COD_REQUISICION
                                                                            .getValue()));
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSub.getCampos()
                            .remove(PropuestareqsControladorEnum.FECHA_ENTREGALV
                                            .getValue());
            registroSub.getCampos()
                            .remove(PropuestareqsControladorEnum.HORA_ENTREGALV
                                            .getValue());
            if ((registroSub.getCampos()
                            .get(PropuestareqsControladorEnum.HORA_ENTREGA
                                            .getValue()) != null)
                && (registroSub.getCampos()
                                .get(PropuestareqsControladorEnum.FECHA_ENTREGA
                                                .getValue()) != null)) {
                registroSub.getCampos()
                                .put(PropuestareqsControladorEnum.HORA_ENTREGA
                                                .getValue(),
                                                SysmanFunciones.pasarHoraDia(
                                                                (Date) registroSub
                                                                                .getCampos()
                                                                                .get(PropuestareqsControladorEnum.HORA_ENTREGA
                                                                                                .getValue()),
                                                                (Date) registroSub
                                                                                .getCampos()
                                                                                .get(PropuestareqsControladorEnum.FECHA_ENTREGA
                                                                                                .getValue())));
            }
            UrlBean url = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.PROPUESTA
                                            .getCreateKey());

            requestManager.save(url.getUrl(), url.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubppropuesta();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));

        }
        catch (SystemException ex) {
            Logger.getLogger(PropuestareqsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubppropuesta(RowEditEvent event) {

        Registro reg = (Registro) event.getObject();

        respondio(Boolean.parseBoolean(
                        SysmanFunciones.nvl(reg.getCampos()
                                        .get(PropuestareqsControladorEnum.RESPONDIO
                                                        .getValue()),
                                        PropuestareqsControladorEnum.FALSE
                                                        .getValue()
                                                        .toLowerCase())
                                        .toString()),
                        validarParametroCadena(reg.getCampos(),
                                        PropuestareqsControladorEnum.COD_PROPUESTA
                                                        .getValue()));
        if (Boolean.parseBoolean(
                        SysmanFunciones.nvl(reg.getCampos()
                                        .get(PropuestareqsControladorEnum.RESPONDIO
                                                        .getValue()),
                                        PropuestareqsControladorEnum.FALSE
                                                        .getValue()
                                                        .toLowerCase())
                                        .toString())) {
            reg.getCampos().put(PropuestareqsControladorEnum.FECHA_ENTREGA
                            .getValue(),
                            new Date());
            reg.getCampos().put(PropuestareqsControladorEnum.HORA_ENTREGA
                            .getValue(),
                            new Date());
        }
        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        reg.getCampos().remove(PropuestareqsControladorEnum.FECHA_ENTREGALV
                        .getValue());
        reg.getCampos().remove(
                        PropuestareqsControladorEnum.HORA_ENTREGALV.getValue());
        try {

            if (auxC != 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3567"));
            }
            else {
                reg.getCampos().put(PropuestareqsControladorEnum.HORA_ENTREGA
                                .getValue(),
                                SysmanFunciones.pasarHoraDia(
                                                (Date) reg.getCampos()
                                                                .get(PropuestareqsControladorEnum.HORA_ENTREGA
                                                                                .getValue()),
                                                (Date) reg.getCampos()
                                                                .get(PropuestareqsControladorEnum.FECHA_ENTREGA
                                                                                .getValue())));
                UrlBean url = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.PROPUESTA
                                                                .getUpdateKey());
                reg.getCampos().remove(
                                GeneralParameterEnum.DATE_CREATED.getName());
                reg.getCampos().remove(
                                GeneralParameterEnum.CREATED_BY.getName());
                reg.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                requestManager.update(url.getUrl(), url.getMetodo(),
                                reg.getCampos(), reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
                if (Boolean.parseBoolean(
                                reg.getCampos().get("RESPONDIO").toString())) {
                    actualizarDespuesRespondio(reg);
                }
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(PropuestareqsControlador.class.getName()).log(
                            Level.SEVERE,
                            null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void eliminarRegSubSubppropuesta(Registro reg) {

        try {

            if (Boolean.parseBoolean(
                            SysmanFunciones.nvl(reg.getCampos()
                                            .get(PropuestareqsControladorEnum.RESPONDIO
                                                            .getValue()),
                                            "false")
                                            .toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3565"));
            }
            else {
                UrlBean url = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.PROPUESTA
                                                                .getDeleteKey());
                requestManager.delete(url.getUrl(), reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
                cargarListaSubppropuesta();
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(PropuestareqsControlador.class.getName()).log(
                            Level.SEVERE,
                            null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubppropuesta() {
        cargarListaSubppropuesta();
    }

    public void oprimirSeleccionarRequisiciones() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("codRequisicion",
                        registro.getCampos()
                                        .get(PropuestareqsControladorEnum.COD_REQUISICION
                                                        .getValue()));
        parametros.put("codDetalle", registro.getCampos().get(
                        PropuestareqsControladorEnum.COD_DETALLE.getValue()));
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.AUXORDENDESUMINISTROS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionar(direccionador);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando163() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private String getParametro(String nombre, boolean indMayus) {
        try {
            return SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, nombre,
                                            modulo, new Date(), indMayus),
                            "");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    public void genInforme(ReportesBean.FORMATOS formato, boolean tercero,
        String parametro) {
        archivoDescarga = null;
        String reporte = "000443CotizacionTerc";
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codReq",
                            registro.getCampos()
                                            .get(PropuestareqsControladorEnum.COD_REQUISICION
                                                            .getValue()));
            reemplazar.put("modulo", modulo);
            reemplazar.put("compania",
                            SysmanFunciones.concatenar("'", compania, "'"));

            HashMap<String, Object> parametros = new HashMap<>();

            Reporteador.resuelveConsulta(parametro,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_TERCERO", tercero);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            "MSM_INFORME_NO_EXISTE"),
                                            " ", ex.getMessage(), " ",
                                            reporte));
            Logger.getLogger(PropuestareqsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));
            Logger.getLogger(PropuestareqsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void oprimirComando167() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        String parametro = getParametro(
                        PropuestareqsControladorEnum.FORMATO_SOLICITUD_COTIZACION_TERCERO
                                        .getValue(),
                        false);
        if (!SysmanFunciones.validarVariableVacio(parametro)
            && idioma.getString("TB_TB3571").equals(parametro)) {
            genInforme(ReportesBean.FORMATOS.PDF, true, parametro);
        }
        else {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3566").replace("s$nombre$s",
                                            PropuestareqsControladorEnum.FORMATO_SOLICITUD_COTIZACION_TERCERO
                                                            .getValue())));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando168() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String parametro = getParametro(
                        PropuestareqsControladorEnum.FORMATO_SOLICITUD_COTIZACION_GENERAL
                                        .getValue(),
                        false);
        if (!SysmanFunciones.validarVariableVacio(parametro)
            && idioma.getString("TB_TB3570").equals(parametro)) {
            genInforme(ReportesBean.FORMATOS.PDF, false, parametro);
        }
        else {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3566").replace("s$nombre$s",
                                            PropuestareqsControladorEnum.FORMATO_SOLICITUD_COTIZACION_GENERAL
                                                            .getValue())));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbDependencia() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(
                        PropuestareqsControladorEnum.RESPONSABLE.getValue(),
                        null);
        registro.getCampos().put(
                        PropuestareqsControladorEnum.NOMRESPONSABLE.getValue(),
                        null);
        cargarListaComboResponsable();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTexto262() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarClaseGasto() {
        // <CODIGO_DESARROLLADO>
        viabilidadVisible = "I".equals(registro.getCampos().get(
                        PropuestareqsControladorEnum.CLASEGASTO.getValue()));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaRequisicion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaProponente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        String nitAux = validarParametroCadena(registroSub.getCampos(),
                        PropuestareqsControladorEnum.NIT.getValue());
        registroSub.getCampos().put(PropuestareqsControladorEnum.NIT.getValue(),
                        registroAux.getCampos()
                                        .get(PropuestareqsControladorEnum.NIT
                                                        .getValue()));

        int aux2 = validarProponente(nitAux);

        if (aux2 != 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3567"));
            registroSub.getCampos().put(
                            PropuestareqsControladorEnum.NIT.getValue(),
                            nitAux);
        }
        else {
            registroSub.getCampos().put(
                            PropuestareqsControladorEnum.NIT.getValue(),
                            registroAux.getCampos()
                                            .get(PropuestareqsControladorEnum.NIT
                                                            .getValue()));
            registroSub.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
        }

    }

    public void seleccionarFilaProponenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = validarParametroCadena(registroAux.getCampos(),
                        PropuestareqsControladorEnum.NIT.getValue());
        auxC = validarProponente(auxiliar);
        auxNitC = auxiliar;
        if (auxC == 0) {
            auxSucursalC = validarParametroCadena(registroAux.getCampos(),
                            GeneralParameterEnum.SUCURSAL.getName());
        }
    }

    public void cambiarProponenteC(int rowNum) {
        // heredado del bean base
    }

    public void cambiarRespondioC(int rowNum) {
        // heredado del bean base
    }

    private void actualizarDespuesRespondio(Registro registro) {
        boolean respondio = Boolean
                        .parseBoolean(registro.getCampos()
                                        .get(PropuestareqsControladorEnum.RESPONDIO
                                                        .getValue())
                                        .toString());

        BigInteger codigoRequisicion = new BigInteger(SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(PropuestareqsControladorEnum.COD_REQUISICION
                                                        .getValue()),
                                        "0")
                        .toString());

        BigInteger codPropuesta = new BigInteger(SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(PropuestareqsControladorEnum.COD_PROPUESTA
                                                        .getValue()),
                                        "0")
                        .toString());
        try {
            boolean miRpta = ejbPlaneacionUno.actualizarEstadoRespondio(
                            compania, codPropuesta,
                            codigoRequisicion, respondio,
                            SessionUtil.getUser().getCodigo());
            if (miRpta) {
                registro.getCampos()
                                .put(PropuestareqsControladorEnum.FECHA_ENTREGA
                                                .getValue(), new Date());
                registro.getCampos()
                                .put(PropuestareqsControladorEnum.HORA_ENTREGA
                                                .getValue(), new Date());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaNombreE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarParametroCadena(registroAux.getCampos(),
                        PropuestareqsControladorEnum.CODIGOELEMENTO.getValue());
    }

    public void seleccionarFilacmbActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (registroAux.getCampos().get(
                        PropuestareqsControladorEnum.COD_ACTIVIDAD.getValue())
                        .toString().length() <= 4) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3568"));
        }
        else {
            registro.getCampos().put(PropuestareqsControladorEnum.COD_ACTIVIDAD
                            .getValue(),
                            registroAux.getCampos()
                                            .get(PropuestareqsControladorEnum.COD_ACTIVIDAD
                                                            .getValue()));
            registro.getCampos().put(PropuestareqsControladorEnum.NOMACTIVIDAD
                            .getValue(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()));
        }

    }

    public void seleccionarFilaactividadCombo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (registroAux.getCampos().get(
                        PropuestareqsControladorEnum.COD_ACTIVIDAD.getValue())
                        .toString().length() <= 4) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3568"));
        }
        else {
            registro.getCampos().put(PropuestareqsControladorEnum.COD_ACTIVIDAD
                            .getValue(),
                            registroAux.getCampos()
                                            .get(PropuestareqsControladorEnum.COD_ACTIVIDAD
                                                            .getValue()));
            registro.getCampos().put(PropuestareqsControladorEnum.NOMACTIVIDAD
                            .getValue(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()));
        }
    }

    public void seleccionarFilaComboResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        PropuestareqsControladorEnum.RESPONSABLE.getValue(),
                        registroAux.getCampos()
                                        .get(PropuestareqsControladorEnum.RESPONSABLE
                                                        .getValue()));
        registro.getCampos().put(
                        PropuestareqsControladorEnum.NOMRESPONSABLE.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        cargarListaComboResponsable();

        if (css == null) {
            viabilidadVisible = false;
        }
        else {
            viabilidadVisible = "I".equals(registro.getCampos()
                            .get(PropuestareqsControladorEnum.CLASEGASTO
                                            .getValue()));

        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.NOMACTIVIDAD.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.NOMRESPONSABLE.getValue());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.NOMACTIVIDAD.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.NOMRESPONSABLE.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.COD_DETALLE.getValue());

        // </CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            removerRegistros();
        }
        return true;
    }

    private void removerRegistros() {
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.SUCURSAL.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.RUBROCOMPRAS.getValue());
        registro.getCampos()
                        .remove(PropuestareqsControladorEnum.ORDENDESUMINISTRO
                                        .getValue());
        registro.getCampos()
                        .remove(PropuestareqsControladorEnum.RUBRO.getValue());
        registro.getCampos()
                        .remove(PropuestareqsControladorEnum.VALORDISPONIBILIDAD
                                        .getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.REQUISIONES.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.VIABILIDAD.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.NUMOFICIO.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.VALORREAL.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.CLASEGASTO.getValue());
        registro.getCampos()
                        .remove(PropuestareqsControladorEnum.VACIA.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.DISPONIBILIDAD.getValue());
        registro.getCampos().remove(
                        PropuestareqsControladorEnum.COD_CONTRATO.getValue());
        registro.getCampos().remove(PropuestareqsControladorEnum.COD_CLACONTRATO
                        .getValue());
        registro.getCampos()
                        .remove(PropuestareqsControladorEnum.DEFINIRCONTRATACION
                                        .getValue());
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new HashMap<>();
        param.put(PropuestareqsControladorEnum.RESPONSABLE.getValue(),
                        registro.getCampos()
                                        .get(PropuestareqsControladorEnum.RESPONSABLE
                                                        .getValue()));
        try {
            Registro reg = listaComboResponsable.getRegistroUnico(param);
            registro.getCampos().put(PropuestareqsControladorEnum.NOMRESPONSABLE
                            .getValue(),
                            reg.getCampos().get(GeneralParameterEnum.NOMBRE
                                            .getName()));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public int validarProponente(String nit) {

        Map<String, Object> param = new HashMap<>();

        param.put(PropuestareqsControladorEnum.COD_REQUISICION.getValue(),
                        registro.getCampos()
                                        .get(PropuestareqsControladorEnum.COD_REQUISICION
                                                        .getValue()));
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PropuestareqsControladorEnum.NIT.getValue(), nit);

        try {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PropuestareqsControladorUrlEnum.URL2936
                                                                            .getValue())
                                            .getUrl(), param));
            return Integer.parseInt(validarParametroCadena(reg.getCampos(),
                            GeneralParameterEnum.CUENTA.getName()));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return 0;
    }

    public void respondio(boolean respondio, String codPro) {
        try {
            ejbPlaneacionUno.responderPropuesta(compania,
                            SessionUtil.getUser().getCodigo(),
                            Long.parseLong(SysmanFunciones
                                            .nvl(registro.getCampos()
                                                            .get(PropuestareqsControladorEnum.COD_REQUISICION
                                                                            .getValue()),
                                                            "0")
                                            .toString()),
                            Long.parseLong(codPro), respondio);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirregCotizacion(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        if (!Boolean.parseBoolean(
                        SysmanFunciones.nvl(reg.getCampos()
                                        .get(PropuestareqsControladorEnum.RESPONDIO
                                                        .getValue()),
                                        "false").toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3569"));
        }
        else {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codReq",
                            validarParametroCadena(reg.getCampos(),
                                            PropuestareqsControladorEnum.COD_REQUISICION
                                                            .getValue()));
            parametros.put("codPro",
                            validarParametroCadena(reg.getCampos(),
                                            PropuestareqsControladorEnum.COD_PROPUESTA
                                                            .getValue()));
            if (css != null) {
                parametros.put("rid", css);
            }
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.SUBPROPUESTADETALLES_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
    }

    public List<Registro> getListacmbDependencia() {
        return listacmbDependencia;
    }

    public void setListacmbDependencia(List<Registro> listacmbDependencia) {
        this.listacmbDependencia = listacmbDependencia;
    }

    public RegistroDataModelImpl getListaProponente() {
        return listaProponente;
    }

    public void setListaProponente(RegistroDataModelImpl listaProponente) {
        this.listaProponente = listaProponente;
    }

    public RegistroDataModelImpl getListaProponenteE() {
        return listaProponenteE;
    }

    public void setListaProponenteE(RegistroDataModelImpl listaProponenteE) {
        this.listaProponenteE = listaProponenteE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    public void setListaElemento(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getListaElementoE() {
        return listaElementoE;
    }

    public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
        this.listaElementoE = listaElementoE;
    }

    public RegistroDataModelImpl getListaNombre() {
        return listaNombre;
    }

    public void setListaNombre(RegistroDataModelImpl listaNombre) {
        this.listaNombre = listaNombre;
    }

    public RegistroDataModelImpl getListaNombreE() {
        return listaNombreE;
    }

    public void setListaNombreE(RegistroDataModelImpl listaNombreE) {
        this.listaNombreE = listaNombreE;
    }

    public RegistroDataModelImpl getListacmbActividad() {
        return listacmbActividad;
    }

    public void setListacmbActividad(RegistroDataModelImpl listacmbActividad) {
        this.listacmbActividad = listacmbActividad;
    }

    public boolean isViabilidadVisible() {
        return viabilidadVisible;
    }

    public void setViabilidadVisible(boolean viabilidadVisible) {
        this.viabilidadVisible = viabilidadVisible;
    }

    public boolean isRespondioBloqueado() {
        return respondioBloqueado;
    }

    public void setRespondioBloqueado(boolean respondioBloqueado) {
        this.respondioBloqueado = respondioBloqueado;
    }

    public String getAuxNitC() {
        return auxNitC;
    }

    public void setAuxNitC(String auxNitC) {
        this.auxNitC = auxNitC;
    }

    public String getAuxSucursalC() {
        return auxSucursalC;
    }

    public void setAuxSucursalC(String auxSucursalC) {
        this.auxSucursalC = auxSucursalC;
    }

    public int getAuxC() {
        return auxC;
    }

    public void setAuxC(int auxC) {
        this.auxC = auxC;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaComboResponsable() {
        return listaComboResponsable;
    }

    public void setListaComboResponsable(
        RegistroDataModelImpl listaComboResponsable) {
        this.listaComboResponsable = listaComboResponsable;
    }

    public RegistroDataModelImpl getListaactividadCombo() {
        return listaactividadCombo;
    }

    public void setListaactividadCombo(
        RegistroDataModelImpl listaactividadCombo) {
        this.listaactividadCombo = listaactividadCombo;
    }

    public Registro getRegistroSubSubPPropuesta() {
        return registroSub;
    }

    public void setRegistroSubSubPPropuesta(Registro registroSubSubPPropuesta) {
        this.registroSub = registroSubSubPPropuesta;
    }

    public String getModulo() {
        return modulo;
    }

    public boolean isCargar() {
        return cargar;
    }

    public void setCargar(boolean cargar) {
        this.cargar = cargar;
    }

    public RegistroDataModelImpl getListaSubppropuesta() {
        return listaSubppropuesta;
    }

    public void setListaSubppropuesta(
        RegistroDataModelImpl listaSubppropuesta) {
        this.listaSubppropuesta = listaSubppropuesta;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

}
