package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.ejb.impl.EjbMantenimientoActivosCero;
import com.sysman.mantenimientoactivos.enums.ProgramacionmantenimientosControladorEnum;
import com.sysman.mantenimientoactivos.enums.ProgramacionmantenimientosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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
 * @author ngomez
 * @version 1, 28/09/2015
 * 
 * @modified jguerrero
 * @version 2. 22/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 * 
 * @author jrodrigueza
 * @version 3, 30/08/2017 Captura de par&aacute;metro rid para cargar
 * el registro actual y adici&oacute;n de par&aacute;metro a&ntilde;o
 * como filtro para la consulta de los reportes.
 * 
 * @author eamaya
 * @version 3.1, 26/10/2017, Cambio en la enumarcion del consecutivo
 * en el metodo insertarAntes()
 * 
 */
@ManagedBean
@ViewScoped
public class ProgramacionmantenimientosControlador
                extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final int modulo;

    private String totalMantenimiento;
    private String eSolicitud;
    private String listSolicitud;
    private String registroauxNombreElemento;
    private String registroauxSucursal;
    private String registroauxSucursalResponsable;

    private String tipoCombo;
    private Registro registroSub;
    private List<Registro> listaNumero;
    private List<Registro> listaDependencia;
    private List<Registro> listaSolicitud;
    private List<Registro> listaResponsable;

    private String auxiliar;
    private String tipo;
    private String panio;
    private String pmes;
    private String tipoNombre;
    private String tituloSolicitud;
    private boolean camposVisible = true;
    private boolean camposVisibleRes = true;
    private boolean solicitudVisible = true;
    private boolean bloqueaDetalle = true;
    private boolean bloqueaMantenimiento = true;
    private boolean bloqueaNumero = true;
    private boolean cargarSolicitud;

    private StreamedContent archivoDescarga;
    private int indiceDmantenimientopreventivo;

    private boolean visibleMovimientos;

    private final String panioCons;
    private final String tipoNombreCons;
    private final String solicitudCons;
    private final String valorTotalCons;
    private final String aprobadoCons;
    private final String impresoCons;
    private final String numeroMayCons;
    private final String dependenciaCons;
    private final String ejeCons;

    private final String numeroCons;
    private final String responsableCons;
    private final String sucursalCons;
    private final String valorUnitarioCons;
    private final String fechaCons;

    private final String prStrSqlCons;
    private final String cantidadCons;
    private final String consecutivoCons;
    private final String solCons;
    private final String autCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbMantenimientoActivosCero ejbManActCero;

    @SuppressWarnings("unchecked")
    public ProgramacionmantenimientosControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.PROGRAMACIONMANTENIMIENTOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = Integer.parseInt(SessionUtil.getModulo());

        panioCons = ProgramacionmantenimientosControladorEnum.PANIO.getValue();
        tipoNombreCons = ProgramacionmantenimientosControladorEnum.TIPONOMBRE
                        .getValue();
        solicitudCons = ProgramacionmantenimientosControladorEnum.SOLICITUDUPPER
                        .getValue();
        valorTotalCons = GeneralParameterEnum.VALORTOTAL.getName();
        aprobadoCons = ProgramacionmantenimientosControladorEnum.APROBADO
                        .getValue();
        impresoCons = GeneralParameterEnum.IMPRESO.getName();
        numeroMayCons = GeneralParameterEnum.NUMERO.getName();
        dependenciaCons = GeneralParameterEnum.DEPENDENCIA.getName();

        numeroCons = ProgramacionmantenimientosControladorEnum.NUMERO
                        .getValue();
        responsableCons = GeneralParameterEnum.RESPONSABLE.getName();
        sucursalCons = GeneralParameterEnum.SUCURSAL.getName();
        valorUnitarioCons = ProgramacionmantenimientosControladorEnum.VALORUNITARIO
                        .getValue();
        fechaCons = GeneralParameterEnum.FECHA.getName();

        prStrSqlCons = ProgramacionmantenimientosControladorEnum.PR_STRSQL
                        .getValue();
        cantidadCons = GeneralParameterEnum.CANTIDAD.getName();
        consecutivoCons = GeneralParameterEnum.CONSECUTIVO.getName();
        ejeCons = ProgramacionmantenimientosControladorEnum.EJE.getValue();
        solCons = ProgramacionmantenimientosControladorEnum.SOL.getValue();
        autCons = ProgramacionmantenimientosControladorEnum.AUT.getValue();

        try {
            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                pmes = (String) parametrosEntrada
                                .get(ProgramacionmantenimientosControladorEnum.PMES
                                                .getValue());
                panio = (String) parametrosEntrada.get(panioCons);
                tipo = (String) parametrosEntrada
                                .get(ProgramacionmantenimientosControladorEnum.TIPOLOWER
                                                .getValue());
                tipoNombre = parametrosEntrada.get(tipoNombreCons).toString();
            }
            if (ejeCons.equals(tipo)) {
                visibleMovimientos = true;
                tituloSolicitud = ProgramacionmantenimientosControladorEnum.AUTORIZACION
                                .getValue();
            }
            else {
                visibleMovimientos = false;
                tituloSolicitud = ProgramacionmantenimientosControladorEnum.SOLICITUD
                                .getValue();
            }
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ProgramacionmantenimientosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.MANTENIMIENTO;

        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), panio);
        parametrosListado.put(GeneralParameterEnum.MES.getName(), pmes);
        parametrosListado.put(ProgramacionmantenimientosControladorEnum.TIPO
                        .getValue(), tipo);

    }

    @Override
    public void iniciarListas() {

        cargarListaNumero();
        cargarListaDependencia();
        cargarListaSolicitud();
        cargarListaResponsable();

    }

    @Override
    public void iniciarListasSub() {

        cargarListaResponsable();

        totalMantenimiento = registro.getCampos().get(valorTotalCons)
                        .toString();

        solicitudVisible = "0".equals(
                        registro.getCampos().get(solicitudCons).toString());

        boolean aprobado = (boolean) registro.getCampos().get(aprobadoCons);
        boolean impreso = (boolean) registro.getCampos().get(impresoCons);
        boolean rechazado = (boolean) registro.getCampos()
                        .get(ProgramacionmantenimientosControladorEnum.RECHAZADO
                                        .getValue());

        bloqueaMantenimiento = !(aprobado || impreso);

        bloqueaDetalle = !(aprobado || impreso || rechazado);
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>

        totalMantenimiento = "";
        bloqueaMantenimiento = true;
        bloqueaDetalle = true;

        if (solCons.equals(tipo)) {
            solicitudVisible = false;
        }

        if (autCons.equals(tipo)) {
            solicitudVisible = true;
        }

        if (ejeCons.equals(tipo)) {
            solicitudVisible = true;
        }

        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    public void cargarListaNumero() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), panio);
        param.put(GeneralParameterEnum.MES.getName(), pmes);
        param.put(ProgramacionmantenimientosControladorEnum.TIPO.getValue(),
                        tipo);

        try {
            listaNumero = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramacionmantenimientosControladorUrlEnum.URL7114
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 447003 COMPANIA ANO TIPO MES
    }

    public void cargarListaDependencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaDependencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramacionmantenimientosControladorUrlEnum.URL7682
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 62004 COMPANIA
    }

    public void cargarListaSolicitud() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), panio);
        param.put(GeneralParameterEnum.MES.getName(), pmes);

        try {

            if (solCons.equals(tipo)) {
                param.put(ProgramacionmantenimientosControladorEnum.TIPO
                                .getValue(), solCons);

                listaSolicitud = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ProgramacionmantenimientosControladorUrlEnum.URL7683
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
                // 447004 COMPANIA TIPO
            }

            if (autCons.equals(tipo)) {
                param.put(ProgramacionmantenimientosControladorEnum.TIPO
                                .getValue(), solCons);

                listaSolicitud = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ProgramacionmantenimientosControladorUrlEnum.URL7684
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
                // 447005 compania tipo ano
            }

            if (ejeCons.equals(tipo)) {
                param.put(ProgramacionmantenimientosControladorEnum.TIPO
                                .getValue(), autCons);

                listaSolicitud = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ProgramacionmantenimientosControladorUrlEnum.URL7685
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
                // 477006 compania tipo ano
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaResponsable() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(dependenciaCons));

        try {
            listaResponsable = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramacionmantenimientosControladorUrlEnum.URL10095
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String parametro(String par) {
        String rta = "";

        try {
            rta = ejbSysmanUtil.consultarParametro(compania,
                            par, SessionUtil.getModulo(),
                            new Date(), true);

            if (rta == null) {
                JsfUtil.agregarMensajeAlerta(
                                SysmanFunciones.concatenar(
                                                idioma.getString("TB_TB2502"),
                                                par, "."));
                rta = "";
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(ProgramacionmantenimientosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return rta;
    }

    private void genInforme(ReportesBean.FORMATOS formato)
                    throws JRException, IOException, SysmanException {

        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();
        String strSql;
        String reporte = ProgramacionmantenimientosControladorEnum.REPORTE250
                        .getValue();
        String reporte1 = ProgramacionmantenimientosControladorEnum.REPORTE252
                        .getValue();
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(numeroCons, registro.getCampos().get(numeroMayCons));
        reemplazar.put(ProgramacionmantenimientosControladorEnum.TIPOLOWER
                        .getValue(), tipo);
        reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(), panio);

        if (solCons.equals(tipo)) {
            strSql = Reporteador.resuelveConsulta(reporte, modulo,
                            reemplazar);
            parametros.put(prStrSqlCons, strSql);
            archivoDescarga = JsfUtil.exportarStreamed(
                            ProgramacionmantenimientosControladorEnum.REPORTE250
                                            .getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }

        if (autCons.equals(tipo)) {
            strSql = Reporteador.resuelveConsulta(reporte, modulo,
                            reemplazar);
            parametros.put(prStrSqlCons, strSql);
            parametros.put(ProgramacionmantenimientosControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(ProgramacionmantenimientosControladorEnum.PR_PIEPA_FORMAT_CALIDAD
                            .getValue(),
                            parametro(ProgramacionmantenimientosControladorEnum.PIE_PAG_FORMA_CALIDAD
                                            .getValue()));
            parametros.put(ProgramacionmantenimientosControladorEnum.PR_FECHA_FORMA_CALIDAD
                            .getValue(),
                            parametro(ProgramacionmantenimientosControladorEnum.FECHA_FORMA_CALIDAD
                                            .getValue()));
            parametros.put(ProgramacionmantenimientosControladorEnum.PR_VERS_FORMA_CALIDAD
                            .getValue(),
                            parametro(ProgramacionmantenimientosControladorEnum.VERS_FORMATO_CALIDAD
                                            .getValue()));
            parametros.put(ProgramacionmantenimientosControladorEnum.PR_COD_FORMA_CALIDAD
                            .getValue(),
                            parametro(ProgramacionmantenimientosControladorEnum.COD_FORMA_CALIDAD
                                            .getValue()));
            archivoDescarga = JsfUtil.exportarStreamed(
                            ProgramacionmantenimientosControladorEnum.REPORTE251
                                            .getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        if (ejeCons.equals(tipo)) {
            strSql = Reporteador.resuelveConsulta(reporte1, modulo,
                            reemplazar);
            parametros.put(prStrSqlCons, strSql);
            archivoDescarga = JsfUtil.exportarStreamed(
                            ProgramacionmantenimientosControladorEnum.REPORTE252
                                            .getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }

    }

    public void oprimirBtMovimientos() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { ProgramacionmantenimientosControladorEnum.ANOLOWER
                        .getValue(),
                            ProgramacionmantenimientosControladorEnum.TIPOLOWER
                                            .getValue(),
                            ProgramacionmantenimientosControladorEnum.NUM_MANTENIMIENTO
                                            .getValue() };
        String[] valores = { retornarString(registro,
                        GeneralParameterEnum.ANO.getName()),
                             retornarString(registro,
                                             ProgramacionmantenimientosControladorEnum.TIPO
                                                             .getValue()),
                             retornarString(registro, numeroMayCons) };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.MANTENIMIENTOMOVASOCIADOS_CONTROLADOR
                                        .getCodigo()),
                        String.valueOf(modulo),
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!(boolean) registro.getCampos().get(impresoCons)) {
            try {
                genInforme(ReportesBean.FORMATOS.PDF);

            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1481"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (!(boolean) registro.getCampos().get(impresoCons)) {
            try {
                genInforme(ReportesBean.FORMATOS.PDF);

            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1481"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!(boolean) registro.getCampos().get(impresoCons)) {
            try {
                genInforme(ReportesBean.FORMATOS.PDF);

            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1481"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarComandoImprimir() {
        // <CODIGO_DESARROLLADO>
        if (!(boolean) registro.getCampos().get(impresoCons)) {
            registro.getCampos().put(impresoCons, true);
            agregarRegistroNuevo(false);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirDetalle() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();

        if (css == null) {

            parametros.put(ProgramacionmantenimientosControladorEnum.ANOLOWER
                            .getValue(),
                            retornarString(registro, GeneralParameterEnum.ANO
                                            .getName()));

            parametros.put(ProgramacionmantenimientosControladorEnum.TIPO2
                            .getValue(), registro.getCampos()
                                            .get(ProgramacionmantenimientosControladorEnum.TIPO
                                                            .getValue()));
            parametros.put(numeroCons, retornarString(registro, numeroMayCons));
            parametros.put(ProgramacionmantenimientosControladorEnum.PMES
                            .getValue(), pmes);
            parametros.put(panioCons, panio);
            parametros.put(ProgramacionmantenimientosControladorEnum.TIPOLOWER
                            .getValue(), tipo);
            parametros.put(tipoNombreCons, tipoNombre);
            parametros.put(ProgramacionmantenimientosControladorEnum.TOTALMANTENIMIENTO
                            .getValue(), registro.getCampos()
                                            .get(valorTotalCons));
            parametros.put(ProgramacionmantenimientosControladorEnum.CAMPOVISIBLE
                            .getValue(), Boolean.toString(camposVisible));
            parametros.put(ProgramacionmantenimientosControladorEnum.CAMPOVISIBLE_RES
                            .getValue(), Boolean.toString(camposVisibleRes));

            parametros.put(ProgramacionmantenimientosControladorEnum.BLOQUEADETALLE
                            .getValue(), Boolean.toString(bloqueaDetalle));
            parametros.put(ProgramacionmantenimientosControladorEnum.APROBADOLOWER
                            .getValue(), Boolean.toString((boolean) registro
                                            .getCampos()
                                            .get(aprobadoCons)));

        }
        else {

            parametros.put(ProgramacionmantenimientosControladorEnum.RID
                            .getValue(), css);
            parametros.put(ProgramacionmantenimientosControladorEnum.ANOLOWER
                            .getValue(),
                            retornarString(registro, GeneralParameterEnum.ANO
                                            .getName()));
            parametros.put(ProgramacionmantenimientosControladorEnum.TIPO2
                            .getValue(), registro.getCampos()
                                            .get(ProgramacionmantenimientosControladorEnum.TIPO
                                                            .getValue()));
            parametros.put(numeroCons, retornarString(registro, numeroMayCons));
            parametros.put(ProgramacionmantenimientosControladorEnum.PMES
                            .getValue(), pmes);
            parametros.put(panioCons, panio);
            parametros.put(ProgramacionmantenimientosControladorEnum.TIPOLOWER
                            .getValue(), tipo);
            parametros.put(tipoNombreCons, tipoNombre);
            parametros.put(ProgramacionmantenimientosControladorEnum.TOTALMANTENIMIENTO
                            .getValue(), registro.getCampos()
                                            .get(valorTotalCons));
            parametros.put(ProgramacionmantenimientosControladorEnum.CAMPOVISIBLE
                            .getValue(), Boolean.toString(camposVisible));
            parametros.put(ProgramacionmantenimientosControladorEnum.CAMPOVISIBLE_RES
                            .getValue(), Boolean.toString(camposVisibleRes));
            parametros.put(ProgramacionmantenimientosControladorEnum.BLOQUEADETALLE
                            .getValue(), Boolean.toString(bloqueaDetalle));
            parametros.put(ProgramacionmantenimientosControladorEnum.APROBADOLOWER
                            .getValue(), Boolean.toString((boolean) registro
                                            .getCampos()
                                            .get(aprobadoCons)));

            Direccionador direccionador = new Direccionador();

            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.DMANTENIMPREVENTIVOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            String.valueOf(modulo));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarSolicitud() {
        // <CODIGO_DESARROLLADO>
        if (!solCons.equals(tipo)) {
            registro.getCampos().put(dependenciaCons,
                            service.buscarEnLista(
                                            (String) registro.getCampos()
                                                            .get(solicitudCons),
                                            numeroMayCons, dependenciaCons,
                                            listaSolicitud));
            cargarListaResponsable();
            registro.getCampos().put(responsableCons,
                            service.buscarEnLista(
                                            (String) registro.getCampos()
                                                            .get(solicitudCons),
                                            numeroMayCons, responsableCons,
                                            listaSolicitud));
            registro.getCampos().put(sucursalCons,
                            service.buscarEnLista(registro.getCampos()
                                            .get(responsableCons).toString(),
                                            responsableCons,
                                            sucursalCons, listaResponsable));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarRechazada() {
        // <CODIGO_DESARROLLADO>
        boolean b = (boolean) registro.getCampos()
                        .get(ProgramacionmantenimientosControladorEnum.RECHAZADO
                                        .getValue());
        bloqueaDetalle = !b;
        bloqueaMantenimiento = !b;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDependencia() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(responsableCons, null);
        cargarListaResponsable();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCODIGOELEMENTO() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarResponsable() {
        // <CODIGO_DESARROLLADO>
        String aux = service.buscarEnLista(
                        registro.getCampos().get(responsableCons).toString(),
                        responsableCons, sucursalCons, listaResponsable);
        registro.getCampos().put(sucursalCons, aux);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        if (registroSub.getCampos().get(valorUnitarioCons) != null) {
            registroSub.getCampos().put(valorTotalCons, Integer.parseInt(
                            registroSub.getCampos().get(valorUnitarioCons)
                                            .toString())
                * Integer.parseInt(
                                registroSub.getCampos().get(valorUnitarioCons)
                                                .toString()));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAprobado() {
        // <CODIGO_DESARROLLADO>
        boolean a = (boolean) registro.getCampos().get(aprobadoCons);
        if (a) {
            bloqueaMantenimiento = false;
            bloqueaDetalle = false;

        }
        else {
            bloqueaMantenimiento = true;
            bloqueaDetalle = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVerificacion() {
        // <CODIGO_DESARROLLADO>
        boolean b = (boolean) registro.getCampos().get(impresoCons);
        bloqueaDetalle = !b;
        // </CODIGO_DESARROLLADO>
    }

    public void generarTotal() {

        // 411001
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), panio);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(numeroMayCons));
            param.put(ProgramacionmantenimientosControladorEnum.TIPO.getValue(),
                            registro.getCampos()
                                            .get(ProgramacionmantenimientosControladorEnum.TIPO
                                                            .getValue()));

            Registro registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramacionmantenimientosControladorUrlEnum.URL7686
                                                                            .getValue())
                                            .getUrl(), param));

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProgramacionmantenimientosControladorUrlEnum.URL7687
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(ProgramacionmantenimientosControladorEnum.KEY_COMPANIA
                            .getValue(), compania);
            fields.put(ProgramacionmantenimientosControladorEnum.KEY_ANO
                            .getValue(), panio);
            fields.put(ProgramacionmantenimientosControladorEnum.KEY_NUMERO
                            .getValue(),
                            registro.getCampos().get(numeroMayCons));
            fields.put(ProgramacionmantenimientosControladorEnum.KEY_TIPO
                            .getValue(),
                            registro.getCampos()
                                            .get(ProgramacionmantenimientosControladorEnum.TIPO
                                                            .getValue()));
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

        }
        catch (SystemException ex) {
            Logger.getLogger(ProgramacionmantenimientosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void copiarElementos() {
        try {

            if (!solCons.equals(tipo)) {

                boolean aux = ejbManActCero.actualizarSolicitudMantenimiento(
                                compania, Integer.parseInt(panio), tipo,
                                Long.parseLong(registro.getCampos()
                                                .get(solicitudCons).toString()),
                                Long.parseLong(registro.getCampos()
                                                .get(numeroMayCons).toString()),
                                SessionUtil.getUser().getCodigo());

                if (aux) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2501"));
                    generarTotal();
                }
                else {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB3725"));

                }
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            boolean rechazada = (Boolean) registro.getCampos().get("RECHAZADO");
            if (rechazada) {
                accion = "v";
            }
        }
        validarSolicitudEnAut();
        cargarListaResponsable();

        precargarRegistro();

        if (solCons.equals(tipo)) {
            camposVisible = false;
            camposVisibleRes = false;
            eSolicitud = ProgramacionmantenimientosControladorEnum.NUMERO_SOLICITUD
                            .getValue();
            solicitudVisible = cargarSolicitud = false;
        }

        if (autCons.equals(tipo)) {
            camposVisibleRes = false;
            eSolicitud = ProgramacionmantenimientosControladorEnum.NUMERO_SOLICITUD
                            .getValue();
            cargarSolicitud = true;
        }

        if (ejeCons.equals(tipo)) {
            eSolicitud = ProgramacionmantenimientosControladorEnum.NUMERO_AUTORIZACION
                            .getValue();
            cargarSolicitud = true;
        }

    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        if (!validarFechaInicio()) {
            return false;
        }
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            BigInteger aux = ejbManActCero.enumerarMantenimiento(compania,
                            Integer.parseInt(panio),
                            tipo, 0);

            registro.getCampos().put(numeroMayCons, aux);
            registro.getCampos()
                            .put(ProgramacionmantenimientosControladorEnum.TIPO
                                            .getValue(), tipo);
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(), panio);

            if ((registro.getCampos().get(solicitudCons) != null)
                && !"0".equals(registro.getCampos().get(solicitudCons))) {

                boolean rta = validarDetalles();
                if (!rta) {
                    return false;
                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        if ((registro.getCampos().get(solicitudCons) != null)
            && !"0".equals(registro.getCampos().get(solicitudCons))) {

            copiarElementos();
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validacionesTipo() {
        boolean rta = true;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), panio);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos()
                                            .get(ProgramacionmantenimientosControladorEnum.SOLICITUDUPPER
                                                            .getValue()));
            param.put(ProgramacionmantenimientosControladorEnum.TIPO.getValue(),
                            solCons);

            Registro regFechaIni = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramacionmantenimientosControladorUrlEnum.URL7688
                                                                            .getValue())
                                            .getUrl(), param));

            Map<String, Object> paramEje = new TreeMap<>();
            paramEje.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramEje.put(GeneralParameterEnum.ANO.getName(), panio);
            paramEje.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos()
                                            .get(ProgramacionmantenimientosControladorEnum.SOLICITUDUPPER
                                                            .getValue()));
            paramEje.put(ProgramacionmantenimientosControladorEnum.TIPO
                            .getValue(), autCons);

            Registro regFechaFin = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProgramacionmantenimientosControladorUrlEnum.URL7688
                                                                            .getValue())
                                            .getUrl(), paramEje));

            if (autCons.equals(tipo)
                && ((Date) registro.getCampos().get(fechaCons))
                                .before((Date) regFechaIni.getCampos()
                                                .get(fechaCons))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2497"));
                rta = false;
                // 447008
            }

            if (ejeCons.equals(tipo)
                && ((Date) registro.getCampos().get(fechaCons))
                                .before((Date) regFechaFin.getCampos()
                                                .get(fechaCons))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2496"));
                rta = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rta;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        if (!validacionesTipo()) {
            return false;
        }

        if ("m".equals(accion)) {
            boolean aprobado = (boolean) registro.getCampos().get(aprobadoCons);
            if (autCons.equals(tipo)
                && aprobado
                && !validarTipoAut()) {
                return false;
            }

            if (solicitudVisible
                && !"0".equals(registro.getCampos().get(solicitudCons))) {

                copiarElementos();

            }
        }

        // </CODIGO_DESARROLLADO>
        return true;

    }

    private boolean validarTipoAut() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.ANO
                                                        .getName()));
        param.put(GeneralParameterEnum.COMPROBANTE.getName(),
                        registro.getCampos().get(numeroMayCons));
        param.put(ProgramacionmantenimientosControladorEnum.TIPO
                        .getValue(), tipo);

        List<Registro> aux = null;
        try {
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ProgramacionmantenimientosControladorUrlEnum.URL7689
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((aux != null) && !aux.isEmpty()) {
            for (Registro aux1 : aux) {

                if (!validarFechasValores(aux1)) {
                    return false;
                }

            }
        }
        return true;
    }

    private boolean validarFechasValores(Registro aux1) {
        if (SysmanFunciones.validarCampoVacio(aux1.getCampos(),
                        ProgramacionmantenimientosControladorEnum.FECHAINICIAL
                                        .getValue())
            || SysmanFunciones.validarCampoVacio(aux1.getCampos(),
                            ProgramacionmantenimientosControladorEnum.FECHAFINAL
                                            .getValue())
            || "0".equals(retornarString(aux1, cantidadCons))) {
            JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB2495"),
                            retornarString(aux1,
                                            consecutivoCons),
                            idioma.getString(
                                            "TB_TB2498")));
            registro.getCampos().put(aprobadoCons, false);
            bloqueaDetalle = !bloqueaDetalle;
            bloqueaMantenimiento = !bloqueaMantenimiento;

            return false;

        }
        if ("0".equals(retornarString(aux1, valorUnitarioCons))
            || (aux1.getCampos()
                            .get(ProgramacionmantenimientosControladorEnum.NIT_TALLER
                                            .getValue()) == null)) {
            JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB2495"),
                            retornarString(aux1,
                                            consecutivoCons),
                            idioma.getString(
                                            "TB_TB2498")));
            registro.getCampos().put(aprobadoCons, false);
            bloqueaDetalle = !bloqueaDetalle;
            bloqueaMantenimiento = !bloqueaMantenimiento;
            return false;
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
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

    public boolean isCargarSolicitud() {
        return cargarSolicitud;
    }

    public void setCargarSolicitud(boolean cargarSolicitud) {
        this.cargarSolicitud = cargarSolicitud;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPanio() {
        return panio;
    }

    public void setPanio(String panio) {
        this.panio = panio;
    }

    public String getPmes() {
        return pmes;
    }

    public void setPmes(String pmes) {
        this.pmes = pmes;
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTotalMantenimiento() {
        return totalMantenimiento;
    }

    public void setTotalMantenimiento(String totalMantenimiento) {
        this.totalMantenimiento = totalMantenimiento;
    }

    public boolean isCamposVisible() {
        return camposVisible;
    }

    public void setCamposVisible(boolean camposVisible) {
        this.camposVisible = camposVisible;
    }

    public boolean isCamposVisibleRes() {
        return camposVisibleRes;
    }

    public void setCamposVisibleRes(boolean camposVisibleRes) {
        this.camposVisibleRes = camposVisibleRes;
    }

    public String geteSolicitud() {
        return eSolicitud;
    }

    public void seteSolicitud(String eSolicitud) {
        this.eSolicitud = eSolicitud;
    }

    public String getListSolicitud() {
        return listSolicitud;
    }

    public void setListSolicitud(String listSolicitud) {
        this.listSolicitud = listSolicitud;
    }

    public boolean isSolicitudVisible() {
        return solicitudVisible;
    }

    public void setSolicitudVisible(boolean solicitudVisible) {
        this.solicitudVisible = solicitudVisible;
    }

    public String getRegistroauxNombreElemento() {
        return registroauxNombreElemento;
    }

    public void setRegistroauxNombreElemento(String registroauxNombreElemento) {
        this.registroauxNombreElemento = registroauxNombreElemento;
    }

    public boolean isBloqueaMantenimiento() {
        return bloqueaMantenimiento;
    }

    public void setBloqueaMantenimiento(boolean bloqueaMantenimiento) {
        this.bloqueaMantenimiento = bloqueaMantenimiento;
    }

    public boolean isBloqueaDetalle() {
        return bloqueaDetalle;
    }

    public void setBloqueaDetalle(boolean bloqueaDetalle) {
        this.bloqueaDetalle = bloqueaDetalle;
    }

    public String getTipoCombo() {
        return tipoCombo;
    }

    public void setTipoCombo(String tipoCombo) {
        this.tipoCombo = tipoCombo;
    }

    public boolean isBloqueaNumero() {
        return bloqueaNumero;
    }

    public void setBloqueaNumero(boolean bloqueaNumero) {
        this.bloqueaNumero = bloqueaNumero;
    }

    public String getRegistroauxSucursal() {
        return registroauxSucursal;
    }

    public void setRegistroauxSucursal(String registroauxSucursal) {
        this.registroauxSucursal = registroauxSucursal;
    }

    public String getRegistroauxSucursalResponsable() {
        return registroauxSucursalResponsable;
    }

    public void setRegistroauxSucursalResponsable(
        String registroauxSucursalResponsable) {
        this.registroauxSucursalResponsable = registroauxSucursalResponsable;
    }

    public String getTituloSolicitud() {
        return tituloSolicitud;
    }

    public void setTituloSolicitud(String tituloSolicitud) {
        this.tituloSolicitud = tituloSolicitud;
    }

    public boolean isVisibleMovimientos() {
        return visibleMovimientos;
    }

    public void setVisibleMovimientos(boolean visibleMovimientos) {
        this.visibleMovimientos = visibleMovimientos;
    }

    public List<Registro> getListaSolicitud() {
        return listaSolicitud;
    }

    public void setListaSolicitud(List<Registro> listaSolicitud) {
        this.listaSolicitud = listaSolicitud;
    }

    public List<Registro> getListaResponsable() {
        return listaResponsable;
    }

    public void setListaResponsable(List<Registro> listaResponsable) {
        this.listaResponsable = listaResponsable;
    }

    public List<Registro> getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(List<Registro> listaNumero) {
        this.listaNumero = listaNumero;
    }

    public List<Registro> getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(List<Registro> listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public int getIndiceDmantenimientopreventivo() {
        return indiceDmantenimientopreventivo;
    }

    public void setIndiceDmantenimientopreventivo(
        int indiceDmantenimientopreventivo) {
        this.indiceDmantenimientopreventivo = indiceDmantenimientopreventivo;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private boolean validarFechaInicio() {
        Date fecha = (Date) registro.getCampos().get("FECHA");
        String ano = String.valueOf(SysmanFunciones.ano(fecha));
        String mes = String.valueOf(SysmanFunciones.mes(fecha));
        if (!ano.equals(panio) || !mes.equals(pmes)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3719"));
            return false;
        }
        return true;
    }

    private void validarSolicitudEnAut() {
        if ((css != null) && !"SOL".equals(tipo)) {
            listaSolicitud = new ArrayList<>();
            Registro reg = new Registro();
            reg.getCampos().put("NUMERO",
                            registro.getCampos().get("SOLICITUD"));
            listaSolicitud.add(reg);
        }
    }

    private boolean validarDetalles() {
        boolean rta = true;

        if (!"EJE".equals(retornarString(registro, "TIPO"))) {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.ANO
                                                            .getName()));
            param.put(GeneralParameterEnum.COMPROBANTE.getName(),
                            registro.getCampos().get("SOLICITUD"));

            List<Registro> aux = null;
            try {
                aux = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ProgramacionmantenimientosControladorUrlEnum.URL23799
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            if ((aux == null) || aux.isEmpty()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3725"));
                rta = false;
            }
        }
        return rta;
    }

}
