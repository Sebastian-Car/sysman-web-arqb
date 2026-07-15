package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenDosRemote;
import com.sysman.almacen.enums.EntdevolutivoactivosControladorEnum;
import com.sysman.almacen.enums.EntdevolutivoactivosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 02/02/2016
 *
 * @author ybecerra
 * @version 2, 02/05/2017 Revision Sonar y Refactoring
 *
 */
@ManagedBean
@ViewScoped

public class EntdevolutivoactivosControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private final String modulo;
    private String nombreCompania;
    private String ciudadCompania;
    private RegistroDataModelImpl listaTipoMovimiento;
    private StreamedContent archivoDescarga;
    private String tipoMov;
    private boolean insertar;
    private String visibleCambiarC;
    private String cambiaConsecutivo;
    private String nuevoConsecutivo;
    private String visibleTrasladar;
    private boolean guardoRegistro;
    private boolean bloqueadoFecha;
    private String reporteMovimiento;

    @EJB
    private EjbSysmanUtilRemote ejbSymanUtl;

    @EJB
    private EjbAlmacenDosRemote ejbAlmacenDos;

    public EntdevolutivoactivosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        insertar = false;
        try
        {
            ciudadCompania = SessionUtil.getCompaniaIngreso().getCiudad();
            nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            numFormulario = 492;
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            tipoMov = "";

        }
        catch (Exception ex)
        {
            Logger.getLogger(EntdevolutivoactivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private void obtenerParametroTipoMov()
    {
        try
        {
            tipoMov = ejbSymanUtl.consultarParametro(compania,
                            "TIPO COMPROBANTE RECLASIFICACION TIPO ACTIVO",
                            modulo, new Date(), true);
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                            tipoMov);
            int tipo = (int) RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EntdevolutivoactivosControladorUrlEnum.URL108
                                                                            .getValue())
                                            .getUrl(), param))
                            .getCampos().get("TIPO");

            if (tipo == 0)
            {
                JsfUtil.agregarMensajeInformativoDialogo(
                                idioma.getString("TB_TB3135"));
                return;
            }
            cambiaConsecutivo = ejbSymanUtl.consultarParametro(compania,
                            "PERMITE MODIFICAR CONSECUTIVO ALMACEN", modulo,
                            new Date(), true);
        }
        catch (SystemException ex)
        {
            Logger.getLogger(
                            EntdevolutivoactivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.CAMBIOS_TIPOACTIVO;
        buscarLlave();
        obtenerParametroTipoMov();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    @Override
    public void iniciarListas()
    {
        cargarListaTipoMovimiento();
    }

    @Override
    public void iniciarListasSub()
    {
        visibleCambiarC = "none";
        reporteMovimiento = registro.getCampos().get(
                        EntdevolutivoactivosControladorEnum.PARAM0.getValue())
                        .toString();
        if ("v".equals(accion))
        {
            visibleTrasladar = "none";
            bloqueadoFecha = true;
        }
        else
        {
            String tipoMovimiento = registro.getCampos()
                            .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                            .getName()) == null ? ""
                                                : (String) registro.getCampos()
                                                                .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                                                                .getName());
            if (tipoMovimiento.equalsIgnoreCase(tipoMov))
            {
                visibleTrasladar = EntdevolutivoactivosControladorEnum.PARAM3
                                .getValue();
            }
            else
            {
                visibleTrasladar = "none";
            }
            boolean registrado = (boolean) registro.getCampos()
                            .get(EntdevolutivoactivosControladorEnum.PARAM1
                                            .getValue());
            bloqueadoFecha = registrado;
        }
    }

    @Override
    public void iniciarListasSubNulo()
    {
        bloqueadoFecha = false;
        visibleCambiarC = "SI".equals(cambiaConsecutivo)
            ? EntdevolutivoactivosControladorEnum.PARAM3.getValue() : "none";
        visibleTrasladar = "none";
    }

    public void cargarListaTipoMovimiento()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EntdevolutivoactivosControladorUrlEnum.URL5982
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                        tipoMov);

        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void oprimirbtnCambiarConsecutivo(ActionEvent ac)
    {
        // necesario en la vista
    }

    public void oprimircmdTrasladar()
    {
        agregarRegistroNuevo(false);
        if (!guardoRegistro)
        {
            return;
        }
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                                            .getName()));
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CONSECUTIVO
                                                            .getName()));

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EntdevolutivoactivosControladorUrlEnum.URL210
                                                                            .getValue())
                                            .getUrl(), param));
            if (reg != null)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1860"));
                return;
            }

            List<Registro> listaReg = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EntdevolutivoactivosControladorUrlEnum.URL224
                                                                            .getValue())
                                            .getUrl(), param));

            if (validarListaReg(listaReg))
            {
                return;
            }

            String tipoM = registro.getCampos()
                            .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                            .getName())
                            .toString();
            BigInteger cons = new BigInteger(registro.getCampos()
                            .get(GeneralParameterEnum.CONSECUTIVO
                                            .getName())
                            .toString());
            Date fec = (Date) registro.getCampos()
                            .get("FECHA");
            ejbAlmacenDos.cambiarTipoActivo(compania, tipoM,
                            cons,
                            fec, SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1862"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(EntdevolutivoactivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    private boolean validarListaReg(List<Registro> listaReg)
    {
        if (listaReg != null)
        {
            boolean placaAnulada;
            Date fechaAnulada;
            for (Registro listaReg1 : listaReg)
            {
                placaAnulada = (boolean) listaReg1.getCampos()
                                .get("PLACAANULADA");
                fechaAnulada = (Date) listaReg1.getCampos().get("FECHAANULADA");
                if (placaAnulada && (fechaAnulada != null))
                {
                    String mensaje = idioma.getString("TB_TB1861");
                    mensaje = mensaje.replace("s$elemento$s", listaReg1
                                    .getCampos().get("ELEMENTO").toString());
                    mensaje = mensaje.replace("s$serie$s", listaReg1.getCampos()
                                    .get("SERIE").toString());
                    JsfUtil.agregarMensajeError(mensaje);
                    return true;
                }
            }
        }
        return false;
    }

    public void oprimirPresentar()
    {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel()
    {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void generaInforme(ReportesBean.FORMATOS formato)
    {
        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();
        try
        {
            reemplazar.put("tipoMovimiento",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                                            .getName()));
            reemplazar.put("movimientoInicial",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CONSECUTIVO
                                                            .getName()));
            reemplazar.put("movimientoFinal",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CONSECUTIVO
                                                            .getName()));

            if (validarDigitosAgrupacion(reemplazar))
            {
                return;
            }

            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            parametros.put("PR_CIUDADCOMPANIA", ciudadCompania + ", "
                + SysmanFunciones.convertirAFechaCadena(
                                (Date) registro.getCampos().get("FECHA"),
                                "dd 'de' MMMMM 'de' YYYY"));

            // Inicio Parametros MovTD001
            parametros.put("PR_CIUDADCOMPANIAMOVTD001", ciudadCompania);
            // Fin Parï¿½metros MovTD001

            // Inicio Parametros Informe MOE_DI_TUN
            parametros.put("PR_VERSION_FORMATO_CALIDAD_TUNJA",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "VERSION FORMATO CALIDAD TUNJA",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
            parametros.put("PR_CODIGO_FORMATO_CALIDAD_TUNJA",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "CODIGO FORMATO CALIDAD TUNJA",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
            parametros.put("PR_FECHA_FORMATO_CALIDAD_TUNJA",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "FECHA FORMATO CALIDAD TUNJA",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
            parametros.put("PR_PIE_DE_PAGINA_FORMATO_CALIDAD_TRASLADOS_TUNJA",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "PIE DE PAGINA FORMATO CALIDAD TRASLADOS TUNJA",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
                                            // Fin Parametros Informe
                                            // MOE_DI_TUN

            // Inicio Parametros Informe MOI_DI_TUN
            parametros.put("PR_ALMACENISTA", SysmanFunciones
                            .nvl(ejbSymanUtl.consultarParametro(compania,
                                            "ALMACENISTA", modulo, new Date(),
                                            true), " "));
            parametros.put("PR_CARGO_ALMACENISTA", SysmanFunciones.nvl(
                            ejbSymanUtl.consultarParametro(compania,
                                            "CARGO ALMACENISTA", modulo,
                                            new Date(), true),
                            " "));
            parametros.put("PR_NOMBRE_SERIE-PLACA/CONSECUTIVO_INFORME_MOI_DI",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "NOMBRE SERIE-PLACA/CONSECUTIVO INFORME MOI_DI",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
                                            // Fin Parametros Informe
                                            // MOI_DI_TUN

            // Inicio Parametros Informe ODD_TUNJA
            parametros.put("PR_FECHA_FORMATO_CALIDAD_TRASLADOS_TUNJA",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "FECHA FORMATO CALIDAD TRASLADOS TUNJA",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
            parametros.put("PR_CODIGO_FORMATO_CALIDAD_TRASLADOS_TUNJA",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "CODIGO FORMATO CALIDAD TRASLADOS TUNJA",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
            parametros.put("PR_CARGO_COORDINADOR_ALMACEN", SysmanFunciones.nvl(
                            ejbSymanUtl.consultarParametro(compania,
                                            "CARGO COORDINADOR ALMACEN", modulo,
                                            new Date(), true),
                            " "));
            parametros.put("PR_FIRMA_CARGO_ORDENADOR_ALMACEN",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "FIRMA CARGO ORDENADOR ALMACEN",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
                                            // Fin Parametros Informe
                                            // ODD_TUNJA

            // Inicio Parametros Informe SDF_TUNJA
            parametros.put("PR_FIRMA_1_EN_ACTA_DE_TRASPASO",
                            SysmanFunciones.nvl(ejbSymanUtl
                                            .consultarParametro(compania,
                                                            "FIRMA 1 EN ACTA DE TRASPASO",
                                                            modulo, new Date(),
                                                            true),
                                            " "));
            parametros.put("PR_NOMBRE_AUXILIAR_ALMACEN", SysmanFunciones.nvl(
                            ejbSymanUtl.consultarParametro(compania,
                                            "NOMBRE AUXILIAR ALMACEN", modulo,
                                            new Date(), true),
                            " "));
            // Fin Parametros Informe SDF_TUNJA
            if (SysmanFunciones.validarVariableVacio(reporteMovimiento)
                || EntdevolutivoactivosControladorEnum.PARAM2.getValue()
                                .equalsIgnoreCase(reporteMovimiento))
            {
                reporteMovimiento = EntdevolutivoactivosControladorEnum.PARAM2
                                .getValue();
                Reporteador.resuelveConsulta(
                                EntdevolutivoactivosControladorEnum.PARAM2
                                                .getValue(),
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
            }
            else
            {
                Reporteador.resuelveConsulta(reporteMovimiento,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
            }

            archivoDescarga = JsfUtil.exportarStreamed(reporteMovimiento,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private boolean validarDigitosAgrupacion(
        HashMap<String, Object> reemplazar)
    {
        String digitosAgrupacion;
        try
        {
            digitosAgrupacion = ejbSymanUtl.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            true);
            if (digitosAgrupacion == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1863"));
                return true;
            }

            int nivelGrupo = Integer.parseInt(digitosAgrupacion);
            reemplazar.put("nivelGrupo", String.valueOf(nivelGrupo));
        }
        catch (SystemException e1)
        {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        catch (NumberFormatException e)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1864"));
            return true;
        }

        return false;
    }

    public void oprimirDetalleSub()
    {
        if (css == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1866"));
            return;
        }

        String[] campos = { "consecutivoCTA", "parametroTipoMov",
                            "tipoMovimientoCTA" };
        String[] valores = { registro.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName())
                        .toString(),
                             tipoMov,
                             (String) registro.getCampos()
                                             .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                                             .getName()) };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SDENTDEVOLUTIVOACTIVOS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
    }

    public void aceptardgCambiar()
    {
        if (ACCION_INSERTAR.equals(accion))
        {
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            nuevoConsecutivo);
        }
        else
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1866"));
        }
        nuevoConsecutivo = null;
    }

    public void cancelardgCambiar()
    {
        nuevoConsecutivo = null;
    }

    public void seleccionarFilaTipoMovimiento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        String tipoMovimiento = registroAux.getCampos().get("CODIGO")
                        .toString();
        String nombreMov = registroAux.getCampos().get("NOMBRE").toString();
        reporteMovimiento = registroAux.getCampos().get(
                        EntdevolutivoactivosControladorEnum.PARAM0.getValue())
                        .toString();
        registro.getCampos().put(
                        GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                        tipoMovimiento);
        registro.getCampos().put("NOMBRE", nombreMov);
        if (tipoMovimiento.equalsIgnoreCase(tipoMov))
        {
            visibleTrasladar = EntdevolutivoactivosControladorEnum.PARAM3
                            .getValue();
        }
        else
        {
            visibleTrasladar = "none";
        }
    }

    @Override
    public void abrirFormulario()
    {
        // necesario en la vista
    }

    @Override
    public void cargarRegistro()
    {
        precargarRegistro();
    }

    @Override
    public boolean insertarAntes()
    {
        insertar = true;
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove(
                        EntdevolutivoactivosControladorEnum.PARAM0.getValue());

        String tipoMovimiento = registro.getCampos()
                        .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                        .getName()) == null ? ""
                                            : (String) registro.getCampos()
                                                            .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                                                            .getName());
        int consecutivo = (int) (registro.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName()) == null
                            ? 0
                            : registro.getCampos()
                                            .get(GeneralParameterEnum.CONSECUTIVO
                                                            .getName()));
        if (!tipoMovimiento.isEmpty() && consecutivo == 0)
        {
            try
            {
                String parametro = ejbSymanUtl.consultarParametro(compania,
                                "MANEJA CONSECUTIVO UNICO PARA TRASPASOS",
                                modulo, new Date(), true);
                parametro = parametro == null ? "NO" : parametro;
                if ("NO".equals(parametro))
                {
                    registro.getCampos().put(
                                    GeneralParameterEnum.CONSECUTIVO.getName(),
                                    ejbSymanUtl.generarSiguienteConsecutivo(
                                                    GenericUrlEnum.CAMBIOS_TIPOACTIVO
                                                                    .getTable(),
                                                    "COMPANIA = ''" + compania
                                                        + "'' AND TIPOMOVIMIENTO = ''"
                                                        + tipoMovimiento + "''",
                                                    GeneralParameterEnum.CONSECUTIVO
                                                                    .getName()));
                }
                else
                {
                    registro.getCampos()
                                    .put(GeneralParameterEnum.CONSECUTIVO
                                                    .getName(),
                                                    ejbAlmacenDos.generarConsecutivoDevolutivo(
                                                                    tipoMovimiento,
                                                                    compania,
                                                                    "T",
                                                                    Integer.parseInt(
                                                                                    modulo)));

                }
            }
            catch (SystemException ex)
            {
                Logger.getLogger(
                                EntdevolutivoactivosControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA
                                            .getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO
                                            .getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.TIPOMOVIMIENTO.getName());
            registro.getCampos()
                            .remove(EntdevolutivoactivosControladorEnum.PARAM1
                                            .getValue());
            registro.getCampos().remove("TIPOFORMATO");

        }
        guardoRegistro = false;

        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        if (!insertar)
        {

            cargarRegistro(registro.getLlave(), "m");
        }
        guardoRegistro = true;
        insertar = false;
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.CONSECUTIVO
                                                        .getName()));
        params.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), "'" +
            registro.getCampos()
                            .get(GeneralParameterEnum.TIPOMOVIMIENTO.getName())
            + "'");
        try
        {
            Registro regAux = listaInicial.getRegistroUnico(params);
            if ((regAux != null)
                && (boolean) regAux.getCampos()
                                .get(EntdevolutivoactivosControladorEnum.PARAM1
                                                .getValue()))
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1867"));
                return false;
            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        return true;
    }

    public RegistroDataModelImpl getListaTipoMovimiento()
    {
        return listaTipoMovimiento;
    }

    public void setListaTipoMovimiento(
        RegistroDataModelImpl listaTipoMovimiento)
    {
        this.listaTipoMovimiento = listaTipoMovimiento;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getVisibleCambiarC()
    {
        return visibleCambiarC;
    }

    public void setVisibleCambiarC(String visibleCambiarC)
    {
        this.visibleCambiarC = visibleCambiarC;
    }

    public String getNuevoConsecutivo()
    {
        return nuevoConsecutivo;
    }

    public void setNuevoConsecutivo(String nuevoConsecutivo)
    {
        this.nuevoConsecutivo = nuevoConsecutivo;
    }

    public String getVisibleTrasladar()
    {
        return visibleTrasladar;
    }

    public void setVisibleTrasladar(String visibleTrasladar)
    {
        this.visibleTrasladar = visibleTrasladar;
    }

    public boolean isBloqueadoFecha()
    {
        return bloqueadoFecha;
    }

    public void setBloqueadoFecha(boolean bloqueadoFecha)
    {
        this.bloqueadoFecha = bloqueadoFecha;
    }
}
