package com.sysman.almacen;

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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.ejb.EjbAlmacenDosRemote;
import com.sysman.almacen.enums.SubpredioavaluosControladorEnum;
import com.sysman.almacen.enums.SubpredioavaluosControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @author dmaldonado
 * @version 1, 25/02/2016
 * 
 * @version 2, 16/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos y en el origen de datos .
 */
@ManagedBean
@ViewScoped
public class SubpredioavaluosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String strAreaConstruccion;
    private final String strAreaLote;
    private final String strAreaZona;
    private final String strFecha;
    private final String strLongCerramiento;
    private final String strPlacaCerramiento;
    private final String strPlacaConstruccion;
    private final String strPlacaLote;
    private final String strPlacaZona;

    private final String strSerie;
    private final String strTb2010;
    private final String strvalor;
    private final String strTotalCerramiento;
    private final String strTotalConstruccion;
    private final String strTotalLote;
    private final String strTotalZonaDura;
    private final String strUndCerramiento;
    private final String strUndConstruccion;
    private final String strUndLote;
    private final String strUndZona;

    private List<Registro> listaano;
    private RegistroDataModelImpl listaPlacaLote;
    private RegistroDataModelImpl listaPlacaConstruccion;
    private RegistroDataModelImpl listaPlacaCerramiento;
    private RegistroDataModelImpl listaPlacaZonaD;
    private String predio;
    private String ultimoAvaluo;
    private String placa;
    private Object anoCombo;
    private boolean bloqueadoValUniLote;
    private boolean bloqueadoValUniConstruccion;
    private boolean bloqueadoValUniCerramiento;
    private boolean bloqueadoValUniZona;
    private String visibleNoDepreciable;
    private double valorConstruccion;
    private double valorTotal;
    private int anoMinimo;
    private int anoMaximo;
    private boolean visibleConfirmar;
    private String vidaUtil;

    @EJB
    private EjbAlmacenDosRemote ejbAlmacenDosRemote;

    public SubpredioavaluosControlador() {
        super();
        compania = SessionUtil.getCompania();
        strAreaConstruccion = "AREA_CONSTRUCCION";
        strAreaLote = "AREA_LOTE";
        strAreaZona = "AREA_ZONA_DURA";
        strFecha = "FECHA";
        strLongCerramiento = "LONG_CERRAMIENTO";
        strPlacaCerramiento = "PLACA_CERRAMIENTO";
        strPlacaConstruccion = "PLACA_CONSTRUCCION";
        strPlacaLote = "PLACA_LOTE";
        strPlacaZona = "PLACA_ZONA_D";
        strSerie = "SERIE";
        strTb2010 = "TB_TB2010";
        strvalor = "VALOR";
        strTotalCerramiento = "VAL_TOTAL_CERRAMIENTO";
        strTotalConstruccion = "VAL_TOTAL_CONSTRUCCION";
        strTotalLote = "VAL_TOTAL_LOTE";
        strTotalZonaDura = "VAL_TOTAL_ZONADURA";
        strUndCerramiento = "VAL_UNIDAD_CERRAMIENTO";
        strUndConstruccion = "VAL_UNIDAD_CONSTRUCCION";
        strUndLote = "VAL_UNIDAD_LOTE";
        strUndZona = "VAL_UNIDAD_ZONA_DURA";

        try {
            visibleConfirmar = false;
            registro = new Registro(new HashMap<String, Object>());
            numFormulario = GeneralCodigoFormaEnum.SUBPREDIOAVALUOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                predio = (String) parametrosEntrada.get("predio");
                ultimoAvaluo = (String) parametrosEntrada.get("ultimoAvaluo");
                anoMinimo = Integer.valueOf(ultimoAvaluo);
                anoMaximo = Integer.valueOf(ultimoAvaluo);
                placa = (String) parametrosEntrada.get("placa");

            }
        }
        catch (Exception ex) {
            Logger.getLogger(SubpredioavaluosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    public void cargarRegistroPredio() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PREDIO.getName(), predio);
            param.put(GeneralParameterEnum.ANO.getName(), anoCombo);
            registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpredioavaluosControladorUrlEnum.URL1072
                                                                            .getValue())
                                            .getUrl(), param));
            if (registro != null) {
                registro.asignarLlave(
                                CacheUtil.getLlaveServicio(urlConexionCache,
                                                GenericUrlEnum.HIST_AVALUOS
                                                                .getTable()));
            }

        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (registro == null) {
            registro = new Registro(new HashMap<String, Object>());
            registro.getCampos().put("ANO", anoCombo);
            accion = ACCION_INSERTAR;
            buscarLlave();
            css = null;
        }
        else {
            accion = ACCION_MODIFICAR;
            css = registro.getLlave() == null ? null : registro.getLlave();
        }

        registro.getCampos().put("VIDA_UTIL", vidaUtil);
        registroIni = registro.getCampos();

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.HIST_AVALUOS;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
        actualizarTotales();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    public void cargarListaano() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpredioavaluosControladorUrlEnum.URL6623
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPlacaLote() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpredioavaluosControladorUrlEnum.URL7017
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(SubpredioavaluosControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get(strPlacaConstruccion));
        param.put(SubpredioavaluosControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(strPlacaCerramiento));
        param.put(SubpredioavaluosControladorEnum.PARAM3.getValue(),
                        registro.getCampos().get(strPlacaZona));

        listaPlacaLote = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strSerie);
    }

    public void cargarListaPlacaConstruccion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpredioavaluosControladorUrlEnum.URL7017
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(SubpredioavaluosControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get(strPlacaLote));
        param.put(SubpredioavaluosControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(strPlacaCerramiento));
        param.put(SubpredioavaluosControladorEnum.PARAM3.getValue(),
                        registro.getCampos().get(strPlacaZona));

        listaPlacaConstruccion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strSerie);
    }

    public void cargarListaPlacaCerramiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpredioavaluosControladorUrlEnum.URL7017
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(SubpredioavaluosControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get(strPlacaLote));
        param.put(SubpredioavaluosControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(strPlacaConstruccion));
        param.put(SubpredioavaluosControladorEnum.PARAM3.getValue(),
                        registro.getCampos().get(strPlacaZona));

        listaPlacaCerramiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strSerie);

    }

    public void cargarListaPlacaZonaD() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpredioavaluosControladorUrlEnum.URL7017
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(SubpredioavaluosControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get(strPlacaLote));
        param.put(SubpredioavaluosControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(strPlacaConstruccion));
        param.put(SubpredioavaluosControladorEnum.PARAM3.getValue(),
                        registro.getCampos().get(strPlacaCerramiento));

        listaPlacaZonaD = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strSerie);
    }

    public void cambiarano() {
        // <CODIGO_DESARROLLADO>
        cargarRegistroPredio();
        registro.getCampos().put("ANO", anoCombo);
        anoMinimo = Integer.valueOf(anoCombo.toString());
        anoMaximo = Integer.valueOf(anoCombo.toString());
        registroIni = new HashMap<>(registro.getCampos());
        valorConstruccion = 0;
        valorTotal = 0;
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void actualizarTotales() {
        valorConstruccion = Double
                        .parseDouble(SysmanFunciones.nvl(registro.getCampos()
                                        .get(strTotalConstruccion),
                                        "0").toString())
            + Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get(strTotalCerramiento),
                            "0").toString())
            + Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos().get(strTotalZonaDura),
                                            "0")
                            .toString());

        valorTotal = valorConstruccion + Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get(strTotalLote), "0")
                        .toString());
    }

    public void cambiarAreaLote() {
        // <CODIGO_DESARROLLADO>
        if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaLote), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strUndLote), ""))
            && "".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strPlacaLote), ""))) {
            double redondeo = SysmanFunciones.redondear(Double.valueOf(
                            registro.getCampos().get(strAreaLote).toString())
                * Double.valueOf(registro.getCampos().get(strUndLote)
                                .toString()),
                            2);
            registro.getCampos().put(strTotalLote, redondeo);
        }
        else if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaLote), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strTotalLote), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strPlacaLote), ""))) {
            double redondeo = SysmanFunciones.redondear(Double.valueOf(registro
                            .getCampos().get(strTotalLote).toString())
                / Double.valueOf(registro.getCampos().get(strAreaLote)
                                .toString()),
                            2);
            registro.getCampos().put(strUndLote, redondeo);
        }
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValUnidadLote() {
        // <CODIGO_DESARROLLADO>
        if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaLote), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strUndLote), ""))) {
            double valTotal = Double.valueOf(
                            registro.getCampos().get(strAreaLote).toString())
                * Double.valueOf(registro.getCampos().get(strUndLote)
                                .toString());
            registro.getCampos().put(strTotalLote, valTotal);
        }
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAreaConstruccion() {
        // <CODIGO_DESARROLLADO>
        if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaConstruccion), ""))
            && !"".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(strUndConstruccion), ""))
            && "".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(strPlacaConstruccion),
                            ""))) {
            double redondeo = SysmanFunciones.redondear(Double.valueOf(registro
                            .getCampos().get(strAreaConstruccion).toString())
                * Double.valueOf(registro.getCampos()
                                .get(strUndConstruccion).toString()),
                            2);
            registro.getCampos().put(strTotalConstruccion, redondeo);
        }
        else if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaConstruccion), ""))
            && !"".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(strTotalConstruccion), ""))
            && !"".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(strPlacaConstruccion),
                            ""))) {
            double redondeo = SysmanFunciones.redondear(Double.valueOf(
                            registro.getCampos().get(strTotalConstruccion)
                                            .toString())
                / Double.valueOf(registro.getCampos().get(strAreaConstruccion)
                                .toString()),
                            2);
            registro.getCampos().put(strUndConstruccion, redondeo);
        }
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValUnidadContruccion() {
        // <CODIGO_DESARROLLADO>
        if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaConstruccion), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(strUndConstruccion), ""))) {
            double valTotal = Double.valueOf(registro.getCampos()
                            .get(strAreaConstruccion).toString())
                * Double.valueOf(registro.getCampos()
                                .get(strUndConstruccion).toString());
            registro.getCampos().put(strTotalConstruccion, valTotal);
        }
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarLongCerramiento() {
        // <CODIGO_DESARROLLADO>
        if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strLongCerramiento), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(strUndCerramiento), ""))
            && "".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strPlacaCerramiento),
                                            ""))) {
            double redondeo = SysmanFunciones.redondear(Double.valueOf(registro
                            .getCampos().get(strLongCerramiento).toString())
                * Double.valueOf(registro.getCampos()
                                .get(strUndCerramiento).toString()),
                            2);
            registro.getCampos().put(strTotalCerramiento, redondeo);
        }
        else if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strLongCerramiento), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(strTotalCerramiento), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strPlacaCerramiento),
                                            ""))) {
            double redondeo = SysmanFunciones.redondear(Double.valueOf(
                            registro.getCampos().get(strTotalCerramiento)
                                            .toString())
                / Double.valueOf(registro.getCampos().get(strLongCerramiento)
                                .toString()),
                            2);
            registro.getCampos().put(strUndCerramiento, redondeo);
        }
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValUnidadCerramiento() {
        // <CODIGO_DESARROLLADO>
        if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strLongCerramiento), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(strUndCerramiento), ""))) {
            double valTotal = Double.valueOf(registro.getCampos()
                            .get(strLongCerramiento).toString())
                * Double.valueOf(registro.getCampos()
                                .get(strUndCerramiento).toString());
            registro.getCampos().put(strTotalCerramiento, valTotal);
        }
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAreaZonaDura() {
        // <CODIGO_DESARROLLADO>
        if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaZona), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(strUndZona), ""))
            && "".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strPlacaZona), ""))) {
            double redondeo = SysmanFunciones.redondear(Double.valueOf(registro
                            .getCampos().get(strAreaZona).toString())
                * Double.valueOf(registro.getCampos()
                                .get(strUndZona).toString()),
                            2);
            registro.getCampos().put(strTotalZonaDura, redondeo);
        }
        else if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaZona), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strTotalZonaDura),
                                            ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(strPlacaZona), ""))) {
            double redondeo = SysmanFunciones.redondear(Double.valueOf(registro
                            .getCampos().get(strTotalZonaDura).toString())
                / Double.valueOf(registro.getCampos().get(strAreaZona)
                                .toString()),
                            2);
            registro.getCampos().put(strUndZona, redondeo);
        }
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValUnidadZonaDura() {
        // <CODIGO_DESARROLLADO>
        if (!"".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaZona), ""))
            && !"".equals(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(strUndZona), ""))) {
            double valTotal = Double.valueOf(
                            (String) registro.getCampos().get(strAreaZona))
                * Double.valueOf((String) registro.getCampos()
                                .get(strUndZona));
            registro.getCampos().put(strTotalZonaDura, valTotal);
        }
        actualizarTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFecha() {
        // <CODIGO_DESARROLLADO>
        Date fecha = (Date) registro.getCampos().get(strFecha);
        if (fecha == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB573"));
            return;
        }
        try {
            if (fecha.before(SysmanFunciones
                            .convertirAFecha("01/01/" + anoCombo))
                || fecha.after(SysmanFunciones
                                .convertirAFecha("31/12/" + anoCombo))) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB2009") + anoCombo + ".");
                registro.getCampos().put(strFecha, registroIni.get(strFecha));
            }
        }
        catch (ParseException e) {
            Logger.getLogger(SubpredioavaluosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * @author dmaldonado
     * @param placa
     * placa a evaluar
     * @return true si la placa puede ser utilizada, false si no.
     */
    public boolean verificarPlacaAvaluo(double placa) {
        boolean salida = true;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubpredioavaluosControladorEnum.PARAM1.getValue(), placa);
        param.put(SubpredioavaluosControladorEnum.PARAM2.getValue(),
                        this.placa);

        List<Registro> aux = null;
        try {
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpredioavaluosControladorUrlEnum.URL21791
                                                                            .getValue())
                                            .getUrl(), param));
            salida = aux.isEmpty();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        /**
         * @watch Remover comentarios en caso de que se requiera que
         * la placa sea diferente para cada tipo de avaluo.
         */
        /*
         * int contador = 0; if (placa ==
         * Double.valueOf(SysmanFunciones.nvl(registro.getCampos().get
         * ("PLACA_LOTE"), "-1").toString())) { contador = contador +
         * 1; } if (placa ==
         * Double.valueOf(SysmanFunciones.nvl(registro.getCampos().get
         * ("PLACA_CERRAMIENTO"), "-1").toString())) { contador =
         * contador + 1; } if (placa ==
         * Double.valueOf(SysmanFunciones.nvl(registro.getCampos().get
         * ("PLACA_ZONA_D"), "-1").toString())) { contador = contador
         * + 1; } if (placa ==
         * Double.valueOf(SysmanFunciones.nvl(registro.getCampos().get
         * ("PLACA_CONSTRUCCION"), "-1").toString())) { contador =
         * contador + 1; } if (contador > 0) { return false; }
         */
        return salida;
    }

    public void seleccionarFilaPlacaLote(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!verificarPlacaAvaluo(Double.valueOf(
                        registroAux.getCampos().get(strSerie).toString()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(strTb2010));
            return;
        }
        registro.getCampos().put(strPlacaLote,
                        registroAux.getCampos().get(strSerie));
        registro.getCampos().put(strTotalLote,
                        registroAux.getCampos().get(strvalor));
        bloqueadoValUniLote = !SysmanFunciones
                        .validarCampoVacio(registro.getCampos(), strPlacaLote);
        actualizarTotales();

    }

    public void seleccionarFilaPlacaConstruccion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!verificarPlacaAvaluo(Double.valueOf(
                        registroAux.getCampos().get(strSerie).toString()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(strTb2010));
            return;
        }
        registro.getCampos().put(strPlacaConstruccion,
                        registroAux.getCampos().get(strSerie));
        registro.getCampos().put(strTotalConstruccion,
                        registroAux.getCampos().get(strvalor));
        bloqueadoValUniConstruccion = !SysmanFunciones.validarCampoVacio(
                        registro.getCampos(), strPlacaConstruccion);
        actualizarTotales();
    }

    public void seleccionarFilaPlacaCerramiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!verificarPlacaAvaluo(Double.valueOf(
                        registroAux.getCampos().get(strSerie).toString()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(strTb2010));
            return;
        }
        registro.getCampos().put(strPlacaCerramiento,
                        registroAux.getCampos().get(strSerie));
        registro.getCampos().put(strTotalCerramiento,
                        registroAux.getCampos().get(strvalor));
        bloqueadoValUniCerramiento = !SysmanFunciones.validarCampoVacio(
                        registro.getCampos(), strPlacaCerramiento);
        actualizarTotales();
    }

    public void seleccionarFilaPlacaZonaD(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!verificarPlacaAvaluo(Double.valueOf(
                        registroAux.getCampos().get(strSerie).toString()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(strTb2010));
            return;
        }
        registro.getCampos().put(strPlacaZona,
                        registroAux.getCampos().get(strSerie));
        registro.getCampos().put(strTotalZonaDura,
                        registroAux.getCampos().get(strvalor));
        bloqueadoValUniZona = !SysmanFunciones
                        .validarCampoVacio(registro.getCampos(), strPlacaZona);
        actualizarTotales();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            vidaUtil = ejbAlmacenDosRemote.consultarVidaUtilPlaca(compania,
                            Long.parseLong(SysmanFunciones.nvlStr(placa,
                                            "-1")));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (SysmanFunciones.validarVariableVacio(vidaUtil.trim())) {
            visibleNoDepreciable = "block";
            vidaUtil = "0";
        }
        else {
            visibleNoDepreciable = "none";
        }
        registro.getCampos().put("VIDA_UTIL", vidaUtil);
        cargarListaano();
        cargarListaPlacaLote();
        cargarListaPlacaConstruccion();
        cargarListaPlacaCerramiento();
        cargarListaPlacaZonaD();
        anoCombo = ultimoAvaluo;
        cargarRegistroPredio();
        registroIni = new HashMap<>(registro.getCampos());
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        Map<String, Object> parametrosSalida = new HashMap<>();
        parametrosSalida.put("precioTerreno", anoCombo);
        SessionUtil.setFlash(parametrosSalida);
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void actualizaPredio() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PREDIO.getName(), predio);

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpredioavaluosControladorUrlEnum.URL10260
                                                                            .getValue())
                                            .getUrl(), param));

            Map<String, Object> paramUpd = new TreeMap<>();
            paramUpd.put(SubpredioavaluosControladorEnum.PARAM4.getValue(),
                            regAux.getCampos().get(strTotalLote));
            paramUpd.put(GeneralParameterEnum.VALOR.getName(),
                            regAux.getCampos().get("VALOR_CONSTRUCCION"));
            paramUpd.put(GeneralParameterEnum.TOTAL.getName(),
                            regAux.getCampos().get("VALOR_TOTAL"));
            paramUpd.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            paramUpd.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            paramUpd.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramUpd.put(GeneralParameterEnum.PREDIO.getName(), predio);

            Parameter parameter = new Parameter();
            parameter.setFields(paramUpd);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubpredioavaluosControladorUrlEnum.URL29539
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
        registro.getCampos();
        if (!visibleConfirmar) {
            visibleConfirmar = true;
            return false;
        }
        else {
            visibleConfirmar = false;
            return true;
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        actualizaPredio();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void aceptarGuardado() {
        registro.getCampos().put(strPlacaLote, SysmanFunciones.nvlStr(
                        registro.getCampos().get(strPlacaLote).toString(),
                        placa));
        registro.getCampos().put(strPlacaConstruccion,
                        SysmanFunciones.nvlStr(registro.getCampos()
                                        .get(strPlacaConstruccion).toString(),
                                        placa));
        registro.getCampos().put(strPlacaCerramiento,
                        SysmanFunciones.nvlStr(registro.getCampos()
                                        .get(strPlacaCerramiento).toString(),
                                        placa));
        registro.getCampos().put(strPlacaZona,
                        SysmanFunciones.nvlStr(registro.getCampos()
                                        .get(strPlacaZona).toString(),
                                        placa));
        registro.getCampos().put(strAreaLote, SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaLote), 0));
        registro.getCampos().put(strAreaConstruccion, SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaConstruccion), 0));
        registro.getCampos().put(strLongCerramiento, SysmanFunciones
                        .nvl(registro.getCampos().get(strLongCerramiento), 0));
        registro.getCampos().put(strAreaZona, SysmanFunciones
                        .nvl(registro.getCampos().get(strAreaZona), 0));
        registro.getCampos().put(strUndLote, SysmanFunciones
                        .nvl(registro.getCampos().get(strUndLote), 0));
        registro.getCampos().put(strUndConstruccion,
                        SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get(strUndConstruccion),
                                        0));
        registro.getCampos().put(strUndCerramiento,
                        SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get(strUndCerramiento),
                                        0));
        registro.getCampos().put(strUndZona,
                        SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get(strUndZona),
                                        0));
        registro.getCampos().put(strTotalLote, SysmanFunciones
                        .nvl(registro.getCampos().get(strTotalLote), 0));
        registro.getCampos().put(strTotalConstruccion,
                        SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get(strTotalConstruccion),
                                        0));
        registro.getCampos().put(strTotalCerramiento,
                        SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get(strTotalCerramiento),
                                        0));
        registro.getCampos().put(strTotalZonaDura,
                        SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get(strTotalZonaDura),
                                        0));
        registro.getCampos().put("ID_INMUEBLE", predio);
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", anoCombo);
        agregarRegistroNuevo(false);
    }

    public void cancelarGuardado() {
        visibleConfirmar = false;
    }

    public List<Registro> getListaano() {
        return listaano;
    }

    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    public RegistroDataModelImpl getListaPlacaLote() {
        return listaPlacaLote;
    }

    public void setListaPlacaLote(RegistroDataModelImpl listaPlacaLote) {
        this.listaPlacaLote = listaPlacaLote;
    }

    public RegistroDataModelImpl getListaPlacaConstruccion() {
        return listaPlacaConstruccion;
    }

    public void setListaPlacaConstruccion(
        RegistroDataModelImpl listaPlacaConstruccion) {
        this.listaPlacaConstruccion = listaPlacaConstruccion;
    }

    public RegistroDataModelImpl getListaPlacaCerramiento() {
        return listaPlacaCerramiento;
    }

    public void setListaPlacaCerramiento(
        RegistroDataModelImpl listaPlacaCerramiento) {
        this.listaPlacaCerramiento = listaPlacaCerramiento;
    }

    public RegistroDataModelImpl getListaPlacaZonaD() {
        return listaPlacaZonaD;
    }

    public void setListaPlacaZonaD(RegistroDataModelImpl listaPlacaZonaD) {
        this.listaPlacaZonaD = listaPlacaZonaD;
    }

    public Object getAnoCombo() {
        return anoCombo;
    }

    public void setAnoCombo(Object anoCombo) {
        this.anoCombo = anoCombo;
    }

    public boolean isBloqueadoValUniLote() {
        return bloqueadoValUniLote;
    }

    public void setBloqueadoValUniLote(boolean bloqueadoValUniLote) {
        this.bloqueadoValUniLote = bloqueadoValUniLote;
    }

    public boolean isBloqueadoValUniConstruccion() {
        return bloqueadoValUniConstruccion;
    }

    public void setBloqueadoValUniConstruccion(
        boolean bloqueadoValUniConstruccion) {
        this.bloqueadoValUniConstruccion = bloqueadoValUniConstruccion;
    }

    public boolean isBloqueadoValUniCerramiento() {
        return bloqueadoValUniCerramiento;
    }

    public void setBloqueadoValUniCerramiento(
        boolean bloqueadoValUniCerramiento) {
        this.bloqueadoValUniCerramiento = bloqueadoValUniCerramiento;
    }

    public boolean isBloqueadoValUniZona() {
        return bloqueadoValUniZona;
    }

    public void setBloqueadoValUniZona(boolean bloqueadoValUniZona) {
        this.bloqueadoValUniZona = bloqueadoValUniZona;
    }

    public double getValorConstruccion() {
        return valorConstruccion;
    }

    public void setValorConstruccion(double valorConstruccion) {
        this.valorConstruccion = valorConstruccion;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public int getAnoMinimo() {
        return anoMinimo;
    }

    public void setAnoMinimo(int anoMinimo) {
        this.anoMinimo = anoMinimo;
    }

    public int getAnoMaximo() {
        return anoMaximo;
    }

    public void setAnoMaximo(int anoMaximo) {
        this.anoMaximo = anoMaximo;
    }

    public String getVisibleNoDepreciable() {
        return visibleNoDepreciable;
    }

    public void setVisibleNoDepreciable(String visibleNoDepreciable) {
        this.visibleNoDepreciable = visibleNoDepreciable;
    }

    public boolean isVisibleConfirmar() {
        return visibleConfirmar;
    }

    public void setVisibleConfirmar(boolean visibleConfirmar) {
        this.visibleConfirmar = visibleConfirmar;
    }

    @Override
    public void cargarRegistro() {
        // Metodo generado por herencia
    }

    @Override
    public void iniciarListasSubNulo() {
        // Metodo generado por herencia
    }

    @Override
    public void iniciarListasSub() {
        // Metodo generado por herencia
    }

    @Override
    public void iniciarListas() {
        // Metodo generado por herencia
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }
}
