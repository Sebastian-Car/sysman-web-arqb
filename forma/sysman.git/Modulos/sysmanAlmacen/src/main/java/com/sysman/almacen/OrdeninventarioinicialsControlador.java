package com.sysman.almacen;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.almacen.ejb.EjbAlmacenDosRemote;
import com.sysman.almacen.ejb.EjbAlmacenTresRemote;
import com.sysman.almacen.enums.OrdeninventarioinicialsControladorEnum;
import com.sysman.almacen.enums.OrdeninventarioinicialsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
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
 * @author dsuesca
 * @version 1, 23/03/2016
 *
 * -- Modificado por lcortes 04,05,08,09,13/05/2017. Refactorizacion
 * de las consultas para usar dss, reemplazos a los llamados a la
 * clase Acciones por los ejb respectivos.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 *
 * -- Modificado por lcortes 27,28,31/07/2017. Se reemplazan los
 * llamados a la clase Acciones para generar el comprobante de almacen
 * por el llamado a la funcion en pl/sql.
 *
 * --Modificado por lcortes 01/08/2017. Se elimina la funcionalidad
 * del boton eliminar comprobante.
 */
@ManagedBean
@ViewScoped
public class OrdeninventarioinicialsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String ordenCompra;
    private final String cCantidad;
    private final String saldoCant;
    private final String vlrTotal;
    private final String cCompania;
    private final String cUnidad;
    private final String cDependencia;
    private final String cTercero;
    private final String cResponsable;
    private final String cNombreLargo;
    private final String cSerie;
    private final String cElemento;
    private final String cFecha;
    private final String cValorUnitario;
    private final String cFechaAdquisicion;
    private final String cFechaSalidaServicio;
    private final String cFechaBodega;
    private final String cSucursalResponsable;
    private final String cClaseOrden;
    private final String cCodigo;
    private final String cCodigoElemento;
    private final String cNumero;
    private final String tDOrdenDeCompra;
    private final String cTotal;
    private final String cNombre;
    private final String cSucursal;
    private final String cNombreTipoActivo;

    private Registro registroSub;
    private RegistroDataModelImpl listaDependencia;
    private List<Registro> listaEstado;
    private List<Registro> listaTipoActivo;
    private RegistroDataModelImpl listaSubordencompra;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;
    private String auxiliar;
    private String indiceSubordencompra;
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaResponsable;
    private String nombreProveedor;
    private String nombreResponsable;
    private String nombreDependencia;
    private String claseOrden;
    private boolean visibleDialogo;
    private boolean permiteModificar = true;
    private String etiquetaMensaje;
    private String tituloMensajes;
    private StreamedContent archivoDescarga;
    private int redondeoCantidad;
    private Object nombreElemento;
    private Object unidad;
    private String totalSub;
    private String tieneDevolutivos;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAlmacenDosRemote almacenDos;

    @EJB
    private EjbAlmacenTresRemote almacenTres;

    public OrdeninventarioinicialsControlador() {
        super();
        compania = SessionUtil.getCompania();
        ordenCompra = "ORDENDECOMPRA";
        cCantidad = GeneralParameterEnum.CANTIDAD.getName();
        saldoCant = "SALDOCANT";
        vlrTotal = "VLRTOTAL";
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cUnidad = GeneralParameterEnum.UNIDAD.getName();
        cDependencia = GeneralParameterEnum.DEPENDENCIA.getName();
        cTercero = GeneralParameterEnum.TERCERO.getName();
        cResponsable = GeneralParameterEnum.RESPONSABLE.getName();
        cNombreLargo = "NOMBRELARGO";
        cSerie = GeneralParameterEnum.SERIE.getName();
        cElemento = GeneralParameterEnum.ELEMENTO.getName();
        cFecha = GeneralParameterEnum.FECHA.getName();
        cValorUnitario = GeneralParameterEnum.VALORUNITARIO.getName();
        cFechaAdquisicion = "FECHAADQUISICION";
        cFechaSalidaServicio = "FECHASALIDASERVICIO";
        cFechaBodega = "FECHABODEGA";
        cSucursalResponsable = "SUCURSAL_RESPONSABLE";
        cClaseOrden = GeneralParameterEnum.CLASEORDEN.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cCodigoElemento = GeneralParameterEnum.CODIGOELEMENTO.getName();
        cNumero = GeneralParameterEnum.NUMERO.getName();
        tDOrdenDeCompra = "D_ORDENDECOMPRA";
        cTotal = GeneralParameterEnum.TOTAL.getName();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        cNombreTipoActivo = "NOMBRE_TIPOACTIVO";

        try {
            numFormulario = GeneralCodigoFormaEnum.ORDENINVENTARIOINICIALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    public void cargarParametros() {

        try {
            String valorParametro = ejbSysmanUtil.consultarParametro(compania,
                            "REDONDEO CANTIDAD", SessionUtil.getModulo(),
                            new Date(), true);

            redondeoCantidad = valorParametro == null ? 2
                : Integer.parseInt(valorParametro);
        }
        catch (SystemException e) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaTercero();

        cargarListaDependencia();
        cargarListaEstado();
        cargarListaTipoActivo();

    }

    @Override
    public void iniciarListasSub() {

        verificarExisteDevolutivo();
        cargarListaSubOrdencompra();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubordencompra = null;
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.ORDENDECOMPRA.getTable();
        buscarLlave();
        claseOrden = "ODI";
        cargarParametros();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {

        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        OrdeninventarioinicialsControladorUrlEnum.URL00002
                                        .getValue());
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        OrdeninventarioinicialsControladorUrlEnum.URL00001
                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL00003
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL00004
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL00005
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);

    }

    public void cargarListaSubOrdencompra() {

        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            OrdeninventarioinicialsControladorUrlEnum.URL11052
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            registro.getCampos().get(cClaseOrden));
            param.put(OrdeninventarioinicialsControladorEnum.PARAM1.getValue(),
                            registro.getCampos().get(cNumero));

            listaSubordencompra = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            tDOrdenDeCompra));

            totalSub = calcularTotalSub();

        }
        catch (SysmanException e) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL15067
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaEstado() {
        try {
            listaEstado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdeninventarioinicialsControladorUrlEnum.URL14458
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoActivo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaTipoActivo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdeninventarioinicialsControladorUrlEnum.URL20706
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaElemento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL17032
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(OrdeninventarioinicialsControladorEnum.PARAM0.getValue(),
                        "C,S");

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarListaElementoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL18252
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(OrdeninventarioinicialsControladorEnum.PARAM0.getValue(),
                        "C,S");

        listaElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL21954
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaResponsable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL22796
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos().get(cDependencia));

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cResponsable);
    }

    public void agregarRegistroSubSubordencompra() {
        try {
            if (insertarAntesSub() && actualizarAntesSub(registroSub)) {
                registroSub.getCampos()
                                .put(GeneralParameterEnum.CREATED_BY
                                                .getName(),
                                                SessionUtil.getUser()
                                                                .getCodigo());
                registroSub.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                registroSub.getCampos().remove(cNombreTipoActivo);
                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                OrdeninventarioinicialsControladorUrlEnum.URL18243
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSub.getCampos());
                cargarListaSubOrdencompra();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
                insertarDespuesSub();
                actualizarDespuesSub();
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
            registroSub.getCampos().put(cCantidad, 1);
            registroSub.getCampos().put(saldoCant, 1);
            registroSub.getCampos().put(vlrTotal, 0);
        }
    }

    public void actualizarDespuesSub() {
        totalSub = calcularTotalSub();
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(cClaseOrden));
        param.put(OrdeninventarioinicialsControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get(cNumero));
        String totalSubNuevo;
        try {
            totalSubNuevo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdeninventarioinicialsControladorUrlEnum.URL20128
                                                                            .getValue())
                                            .getUrl(), param))
                            .getCampos().get(cTotal)
                            .toString();

            param.remove(OrdeninventarioinicialsControladorEnum.PARAM1
                            .getValue());
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            param.put(GeneralParameterEnum.VALORTOTAL.getName(), totalSubNuevo);
            param.put(GeneralParameterEnum.ORDEN.getName(),
                            registro.getCampos().get(cNumero));
            Parameter campos = new Parameter();
            campos.setFields(param);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            OrdeninventarioinicialsControladorUrlEnum.URL38853
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            campos);

        }
        catch (SystemException e) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean insertarAntesSub() {
        try {
            registroSub.getCampos().put(cCompania, compania);
            registroSub.getCampos().put(cClaseOrden,
                            registro.getCampos().get(cClaseOrden));
            registroSub.getCampos().put(ordenCompra,
                            registro.getCampos().get(cNumero));
            registroSub.getCampos().remove(cUnidad);
            registroSub.getCampos().remove(cNombreLargo);

            StringBuilder condicion = new StringBuilder();
            condicion.append(" COMPANIA = ''").append(compania)
                            .append("'' AND CLASEORDEN = ''").append(claseOrden)
                            .append("'' AND ORDENDECOMPRA = ")
                            .append(registro.getCampos().get(cNumero)
                                            .toString());

            long intCodigo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "D_ORDENDECOMPRA", condicion.toString(), "CODIGO",
                            "1");
            registroSub.getCampos().put(cCodigo, intCodigo);
        }
        catch (SystemException ex) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private boolean validarCamposUno(Registro reg) {
        if ((reg.getCampos().get(cSerie) == null)
            || "".equals(reg.getCampos().get(cSerie))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1939"));
            return false;
        }
        if ((reg.getCampos().get(cElemento) == null)
            || "".equals(reg.getCampos().get(cElemento))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1940"));
            return false;
        }
        if ((reg.getCampos().get(cCantidad) == null)
            || "".equals(reg.getCampos().get(cCantidad))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1941"));
            return false;
        }

        return true;
    }

    private boolean validarCamposDos(Registro reg) {
        if ((reg.getCampos().get(cValorUnitario) == null)
            || "".equals(reg.getCampos().get(cValorUnitario))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1943"));
            return false;
        }
        if ((reg.getCampos().get(cFechaAdquisicion) == null)
            || "".equals(reg.getCampos().get(cFechaAdquisicion))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1944"));
            return false;
        }
        if ((reg.getCampos().get(cFechaSalidaServicio) == null)
            || "".equals(reg.getCampos().get(cFechaSalidaServicio))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1946"));
            return false;
        }

        return true;
    }

    private boolean validarCamposTres(Registro reg) {
        if ((reg.getCampos().get(cFechaBodega) == null)
            || "".equals(reg.getCampos().get(cFechaBodega))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1947"));
            return false;
        }

        boolean tieneFechas = (reg.getCampos().get(cFechaAdquisicion) != null)
            && (reg.getCampos().get(cFechaSalidaServicio) != null);
        if (tieneFechas && ((Date) reg.getCampos().get(cFechaAdquisicion))
                        .after((Date) (reg.getCampos()
                                        .get(cFechaSalidaServicio)))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1948"));
            return false;
        }

        return true;
    }

    private boolean validarCamposCuatro(Registro reg) {
        boolean tieneFechas = (reg.getCampos().get(cFechaAdquisicion) != null)
            && (reg.getCampos().get(cFechaBodega) != null);
        if (tieneFechas && ((Date) reg.getCampos().get(cFechaAdquisicion))
                        .after((Date) (reg.getCampos().get(cFechaBodega)))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1950"));
            return false;
        }

        if (tieneFechas && ((Date) reg.getCampos().get(cFechaBodega))
                        .after((Date) (reg.getCampos()
                                        .get(cFechaSalidaServicio)))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB4041"));
            return false;
        }

        return true;
    }

    public boolean actualizarAntesSub(Registro reg) {
        reg.getCampos().remove(cUnidad);
        reg.getCampos().remove(cNombreLargo);

        if (!validarCamposUno(reg)) {
            return false;
        }
        if (!validarCamposDos(reg)) {
            return false;
        }

        if (!validarCamposTres(reg)) {
            return false;
        }
        if (!validarCamposCuatro(reg)) {
            return false;
        }

        return true;
    }

    public void insertarDespuesSub() {

        Map<String, Object> param = new TreeMap<>();
        param.put(OrdeninventarioinicialsControladorEnum.PARAM2.getValue(),
                        registroSub.getCampos().get(cCantidad));
        param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        registroSub.getCampos().get(cElemento));
        Parameter campos = new Parameter();
        campos.setFields(param);

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdeninventarioinicialsControladorUrlEnum.URL49423
                                                        .getValue());
        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            campos);
        }
        catch (SystemException e) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void editarRegSubSubordencompra(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            if (actualizarAntesSub(reg)) {
                reg.getCampos().remove(cCompania);
                reg.getCampos().remove(cClaseOrden);
                reg.getCampos().remove(ordenCompra);
                reg.getCampos().remove(cCodigo);
                reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                reg.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                reg.getCampos().remove(cNombreTipoActivo);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                OrdeninventarioinicialsControladorUrlEnum.URL21581
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(),
                                reg.getLlave());
                actualizarDespuesSub();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubOrdencompra();
        }
    }

    public void activarEdicionSubordencompra(Registro reg) {
        reg.getCampos().put(
                        "NIIF_TIPO_ACTIVO",
                        reg.getCampos().get(cNombreTipoActivo));
    }

    public void eliminarRegSubSubordencompra(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            OrdeninventarioinicialsControladorUrlEnum.URL35649
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubOrdencompra();
        }
        catch (SystemException ex) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubordencompra() {
        listaSubordencompra.load();
    }

    public void oprimircmdPantalla() {
        // PDF
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando59() {
        // EXCEL
        // </CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        String aux;

        try {
            aux = almacenTres
                            .obtenerNumeroRequisiciones(compania,
                                            claseOrden,
                                            Long.parseLong(registro
                                                            .getCampos()
                                                            .get(cNumero)
                                                            .toString()));

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("claseOrden", claseOrden);
            reemplazar.put("numero",
                            registro.getCampos().get(cNumero).toString());
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000602contratoCompraventaODC001";
            // MANEJO DE PARAMETROS DEL REPORTE

            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);

            parametros.put("PR_FORMATO",
                            formato.equals(ReportesBean.FORMATOS.PDF)
                                ? "PDF"
                                : "EXCEL");
            parametros.put("PR_NUMREQUISICIONES", aux);
            parametros.put("PR_FORMS_ORDENINVENTARIOINICIAL_CLASEORDEN",
                            claseOrden);
            parametros.put("PR_FORMS_ORDENINVENTARIOINICIAL_NUMERO",
                            registro.getCampos().get(cNumero));
            parametros.put("PR_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que verifica si el comprobante inicial ya teine
     * comprobante de almacen relacionado, si es asi retorna verdadeo
     * y modifica lel atributo permiteModificar a falso con el fin de
     * realizar las validaciones graficas
     */
    private boolean verificarExisteDevolutivo() {
        boolean retorno = false;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(cNumero));
        param.put(OrdeninventarioinicialsControladorEnum.PARAM3.getValue(),
                        "EDI,ECI");
        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdeninventarioinicialsControladorUrlEnum.URL43784
                                                                            .getValue())
                                            .getUrl(), param));

            if (!"0".equals(rs.getCampos().get(cNumero).toString())) {

                retorno = true;
                permiteModificar = false;
            }
            else {
                retorno = false;
                permiteModificar = true;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return retorno;

    }

    public void oprimirGeneraComprobante() {
        // <CODIGO_DESARROLLADO>
        if (verificarExisteDevolutivo()) {
            etiquetaMensaje = idioma.getString("TB_TB2788");
            visibleDialogo = true;
        }
        else {
            tieneDevolutivos = "NO";
            generaComprobanteAlmacen();
            permiteModificar = false;
        }

    }

    public void aceptardialogo() {
        visibleDialogo = false;

        tieneDevolutivos = "SI";
        generaComprobanteAlmacen();
        permiteModificar = false;

    }

    private void generaComprobanteAlmacen() {

        try {
            String respuesta = almacenDos.generarComprobanteAlmacen(compania,
                            claseOrden,
                            Long.parseLong(registro.getCampos().get(cNumero)
                                            .toString()),
                            SessionUtil.getUser().getCodigo(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.DEPENDENCIA
                                                            .getName())
                                            .toString(),
                            tieneDevolutivos);

            if ("-1".equals(respuesta)) {
                String[] campos = { "claseOrden" };
                String[] valores = { claseOrden };
                SessionUtil.cargarModalDatosFlash(
                                String.valueOf(GeneralCodigoFormaEnum.ERRORALMACENINVENTARIOINICIAL_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(), campos, valores);
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1953"));
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelardialogo() {
        // <CODIGO_DESARROLLADO>
        visibleDialogo = false;
        JsfUtil.agregarMensajeError(
                        idioma.getString("TB_TB1954"));

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDependencia() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        registroSub.getCampos().put(vlrTotal, calcularTotal());
        registroSub.getCampos().put(saldoCant,
                        registroSub.getCampos().get(cCantidad));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitario() {
        // <CODIGO_DESARROLLADO>
        if (Double.parseDouble(registroSub.getCampos()
                        .get(GeneralParameterEnum.VALORUNITARIO.getName())
                        .toString()) < 0) {
            registroSub.getCampos().put(
                            GeneralParameterEnum.VALORUNITARIO.getName(), "0");
            JsfUtil.agregarMensajeAlerta("El valor debe ser positivo");
        }
        else {
            registroSub.getCampos().put(vlrTotal, calcularTotal());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaAdquisicion() {
        // <CODIGO_DESARROLLADO>
        registroSub.getCampos().put(cFechaSalidaServicio,
                        registroSub.getCampos().get(cFechaAdquisicion));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaAdquisicionC(int rowNum) {
        listaSubordencompra.getDatasource().get(rowNum).getCampos().put(
                        cFechaSalidaServicio,
                        listaSubordencompra.getDatasource().get(rowNum)
                                        .getCampos().get(cFechaAdquisicion));
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaSalidaServicio() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaSalidaServicioC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaBodega() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaBodegaC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarElementoC(int rowNum) {

        listaSubordencompra.getDatasource().get(rowNum).getCampos()
                        .put(cNombreLargo, nombreElemento);
        listaSubordencompra.getDatasource().get(rowNum).getCampos()
                        .put(cUnidad, unidad);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidadC(int rowNum) {

        listaSubordencompra.getDatasource().get(rowNum).getCampos()
                        .put(vlrTotal, calcularTotalE(rowNum));
        listaSubordencompra.getDatasource().get(rowNum).getCampos().put(
                        saldoCant,
                        listaSubordencompra.getDatasource().get(rowNum)
                                        .getCampos().get(cCantidad));

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitarioC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        if (Double.parseDouble(listaSubordencompra.getDatasource().get(rowNum)
                        .getCampos()
                        .get(GeneralParameterEnum.VALORUNITARIO.getName())
                        .toString()) < 0) {
            JsfUtil.agregarMensajeAlerta("El valor debe ser positivo");
            listaSubordencompra.getDatasource().get(rowNum)
                            .getCampos().put(
                                            GeneralParameterEnum.VALORUNITARIO
                                                            .getName(),
                                            0);
        }
        else {
            listaSubordencompra.getDatasource().get(rowNum).getCampos()
                            .put(vlrTotal, calcularTotalE(rowNum));
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(cElemento,
                        registroAux.getCampos().get(cCodigoElemento));
        registroSub.getCampos().put(cNombreLargo,
                        registroAux.getCampos().get(cNombreLargo));
        registroSub.getCampos().put(cUnidad,
                        registroAux.getCampos().get(cUnidad));
    }

    public void seleccionarFilaElementoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigoElemento).toString();
        nombreElemento = registroAux.getCampos().get(cNombreLargo);
        unidad = registroAux.getCampos().get(cUnidad);

    }

    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cTercero, registroAux.getCampos().get("NIT"));
        nombreProveedor = registroAux.getCampos().get(cNombre).toString();
        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal).toString());
    }

    public void seleccionarFilaResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cResponsable,
                        registroAux.getCampos().get(cResponsable));
        registro.getCampos().put(cSucursalResponsable,
                        registroAux.getCampos().get(cSucursal));
        nombreResponsable = registroAux.getCampos().get(cNombre).toString();
    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cDependencia,
                        registroAux.getCampos().get(cCodigo));
        nombreDependencia = registroAux.getCampos().get(cNombre).toString();
        cargarListaResponsable();
        registro.getCampos().remove(cResponsable);
        nombreResponsable = null;
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
        cargarListaResponsable();
        cargarNombres();
        registroSub.getCampos().put(cCantidad, 1);
        registroSub.getCampos().put(saldoCant, 1);
        registroSub.getCampos().put(vlrTotal, 0);

        // </CODIGO_DESARROLLADO>
    }

    public void cargarNombres() {
        Map<String, Object> parametro = new TreeMap<>();
        parametro.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(cDependencia));
        parametro.put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registro.getCampos().get(cResponsable));
        parametro.put(OrdeninventarioinicialsControladorEnum.PARAM4.getValue(),
                        registro.getCampos().get(cTercero));
        parametro.put(OrdeninventarioinicialsControladorEnum.PARAM5.getValue(),
                        registro.getCampos().get(cSucursal));
        if (css != null) {
            try {
                nombreDependencia = listaDependencia.getRegistroUnico(parametro)
                                .getCampos()
                                .get(cNombre).toString();

                nombreProveedor = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                OrdeninventarioinicialsControladorUrlEnum.URL13982
                                                                                                .getValue())
                                                                .getUrl(),
                                                parametro))
                                .getCampos().get(cNombre)
                                .toString();
                nombreResponsable = listaResponsable
                                .getRegistroUnico(parametro)
                                .getCampos().get(cNombre).toString();

            }
            catch (SystemException e) {
                Logger.getLogger(OrdeninventarioinicialsControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            try {
                String valorParametro = nvl(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "FECHA DE CORTE PARA INICIO DEL ALMACEN",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                "").toString();
                registro.getCampos().put(cFecha,
                                SysmanFunciones.sumarRestarDiasFecha(
                                                SysmanFunciones.convertirAFecha(
                                                                valorParametro),
                                                -2));
                claseOrden = "ODI";
                registro.getCampos().put(cClaseOrden, claseOrden);
            }
            catch (ParseException | SystemException ex) {
                Logger.getLogger(OrdeninventarioinicialsControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
            }

            nombreDependencia = nombreProveedor = nombreResponsable = null;
        }
    }

    public Double calcularTotal() {
        Double valorUnitario = Double.parseDouble(
                        nvl(registroSub.getCampos().get(cValorUnitario), "0.0")
                                        .toString());
        Integer cantidad = Integer.parseInt(
                        nvl(registroSub.getCampos().get(cCantidad), "0")
                                        .toString());
        return valorUnitario * cantidad;
    }

    public Double calcularTotalE(int rowNum) {
        Double valorUnitario = Double.parseDouble(
                        nvl(listaSubordencompra.getDatasource().get(rowNum)
                                        .getCampos().get(cValorUnitario),
                                        "0.0").toString());
        Integer cantidad = Integer
                        .parseInt(nvl(listaSubordencompra.getDatasource()
                                        .get(rowNum).getCampos()
                                        .get(cCantidad), "0").toString());
        return valorUnitario * cantidad;
    }

    private String calcularTotalSub() {
        String valorTotalSub = "0";
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(cClaseOrden));
        param.put(OrdeninventarioinicialsControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get(cNumero));
        try {
            valorTotalSub = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdeninventarioinicialsControladorUrlEnum.URL19314
                                                                            .getValue())
                                            .getUrl(), param))
                            .getCampos().get(cTotal).toString();
        }
        catch (SystemException e) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return valorTotalSub;
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        StringBuilder condicion = new StringBuilder();

        try {
            condicion.append(" COMPANIA = ''").append(compania)
                            .append("'' AND CLASEORDEN = ''").append(claseOrden)
                            .append("'' ");
            long intCodigo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            ordenCompra, condicion.toString(), "NUMERO", "1");

            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            intCodigo);

        }
        catch (SystemException ex) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
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
        if (!"i".equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CLASEORDEN.getName());
            registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            registro.getCampos().remove("TIPOAFECTADO");
            registro.getCampos().remove("OBJETOCONTRATO");
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

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public List<Registro> getListaEstado() {
        return listaEstado;
    }

    public void setListaEstado(List<Registro> listaEstado) {
        this.listaEstado = listaEstado;
    }

    public RegistroDataModelImpl getListaSubordencompra() {
        return listaSubordencompra;
    }

    public void setListaSubordencompra(
        RegistroDataModelImpl listaSubordencompra) {
        this.listaSubordencompra = listaSubordencompra;
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

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    public RegistroDataModelImpl getListaResponsable() {
        return listaResponsable;
    }

    public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
        this.listaResponsable = listaResponsable;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    public String getEtiquetaMensaje() {
        return etiquetaMensaje;
    }

    public void setEtiquetaMensaje(String etiquetaMensaje) {
        this.etiquetaMensaje = etiquetaMensaje;
    }

    public String getTituloMensajes() {
        return tituloMensajes;
    }

    public void setTituloMensajes(String tituloMensajes) {
        this.tituloMensajes = tituloMensajes;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public int getRedondeoCantidad() {
        return redondeoCantidad;
    }

    public void setRedondeoCantidad(int redondeoCantidad) {
        this.redondeoCantidad = redondeoCantidad;
    }

    public Object getNombreElemento() {
        return nombreElemento;
    }

    public void setNombreElemento(Object nombreElemento) {
        this.nombreElemento = nombreElemento;
    }

    public Object getUnidad() {
        return unidad;
    }

    public void setUnidad(Object unidad) {
        this.unidad = unidad;
    }

    public String getTotalSub() {
        return totalSub;
    }

    public void setTotalSub(String totalSub) {
        this.totalSub = totalSub;
    }

    public boolean isPermiteModificar() {
        return permiteModificar;
    }

    public void setPermiteModificar(boolean permiteModificar) {
        this.permiteModificar = permiteModificar;
    }

    public List<Registro> getListaTipoActivo() {
        return listaTipoActivo;
    }

    public void setListaTipoActivo(List<Registro> listaTipoActivo) {
        this.listaTipoActivo = listaTipoActivo;
    }

    public String getIndiceSubordencompra() {
        return indiceSubordencompra;
    }

    public void setIndiceSubordencompra(String indiceSubordencompra) {
        this.indiceSubordencompra = indiceSubordencompra;
    }

}
