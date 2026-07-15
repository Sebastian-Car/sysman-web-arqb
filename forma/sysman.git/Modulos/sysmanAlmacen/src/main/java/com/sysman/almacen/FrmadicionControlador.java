package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.almacen.enums.FrmadicionControladorEnum;
import com.sysman.almacen.enums.FrmadicionControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author apineda
 * @version 1, 19/02/2016
 * @version 2, 27/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 */
@ManagedBean
@ViewScoped
public class FrmadicionControlador extends BeanBaseModal {

    private String compania;
    private String seleccion;
    private String ordena;
    private String adicionesAlmacen;
    private String direccion;
    private String escritura;
    private String codPredial;
    private String serie;
    private String descripcion;
    private String nombreAdicion;
    private String nombreSeleccion;
    private String nombreResponsable;
    private String tipoInmueble;
    private String etiquetaSeleccion;
    private String informacionTitulo;
    private String elemento;
    private String menuActual;
    private String tituloDireccion;
    private String tituloEscritura;
    private String tipoMovimiento;
    private String numeroMovimiento;
    private String valorTotal;
    private String anioConstruccion;
    private String sucursal;
    private boolean placaAnulada;
    private boolean visibleCodPredial;
    private boolean bloqueado;
    private Date fechaMov;
    private RegistroDataModelImpl listaSELECCION;
    private RegistroDataModelImpl listaORDENA;
    private RegistroDataModelImpl listacbAdicionesAlmacen;
 

    @EJB
    private EjbAlmacenCuatroRemote ejbAlmacenCuatro;

    /**
     * Creates a new instance of FrmadicionControlador
     */
    public FrmadicionControlador() {
        super();
    

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMADICION_CONTROLADOR.getCodigo();
            compania = SessionUtil.getCompania();
            fechaMov = new Date();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmadicionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaSELECCION();
        cargarListaORDENA();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // NO ESTA IMPLEMENTADO
    }

    public void cargarListaSELECCION() {
        menuActual = SessionUtil.getMenuActual();
        menuActual = menuActual == null ? "NULL" : menuActual;
        if (("1007020303").equals(menuActual)) {
            tipoInmueble = "P";
            etiquetaSeleccion = "Nombre del predio:";
            tituloDireccion = idioma.getString("TG_DIRECCION4");
            tituloEscritura = "Escritura:";
            visibleCodPredial = true;
            informacionTitulo = idioma.getString("TB_TB1843");
            bloqueado = false;
            // 137016
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmadicionControladorUrlEnum.URL3981
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaSELECCION = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, FrmadicionControladorEnum.SELECCION.getValue());
        }
        else if (("1007020402").equals(menuActual)) {
            tipoInmueble = "V";
            etiquetaSeleccion = idioma.getString("TB_TB1844");
            tituloDireccion = idioma.getString("TB_TB1845");
            tituloEscritura = "Tramo:";
            visibleCodPredial = false;
            informacionTitulo = idioma.getString("TB_TB1846");
            // 136016
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmadicionControladorUrlEnum.URL5727
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaSELECCION = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, FrmadicionControladorEnum.SELECCION.getValue());
        }

    }

    public void cargarListaORDENA() {
        String url;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        if (descripcion != null) {
            // 61010
            url = FrmadicionControladorUrlEnum.URL7073.getValue();
            param.put(GeneralParameterEnum.NUMERO.getName(), ordena);
        }
        else {
            // 61005
            url = FrmadicionControladorUrlEnum.URL7072.getValue();
        }

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        url);
        listaORDENA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CEDULA");
    }

    public void cargarListacbAdicionesAlmacen() {
        // 119009
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmadicionControladorUrlEnum.URL7933
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.SERIE.getName(), serie);

        listacbAdicionesAlmacen = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirAceptar() {  
        try {
            ejbAlmacenCuatro.registrarAdicionPredial(compania, seleccion,
                            new BigInteger(serie),
                            elemento, fechaMov,
                            new BigInteger(numeroMovimiento),
                            tipoMovimiento,
                            tipoInmueble, valorTotal, ordena,
                            Integer.parseInt(anioConstruccion),
                            sucursal, descripcion,
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));  
    }

    public void oprimirCancelar() { 
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");   
    }

    // onRowSelectSELECCION
    public void seleccionarFilaSELECCION(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (("P").equals(tipoInmueble)) {
            seleccion = SysmanFunciones
                            .nvl(registroAux.getCampos().get(FrmadicionControladorEnum.SELECCION.getValue()), "")
                            .toString();
            nombreSeleccion = SysmanFunciones
                            .nvl(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
                            .toString();
            direccion = SysmanFunciones
                            .nvl(registroAux.getCampos().get("DIRECCION"), "")
                            .toString();
            escritura = SysmanFunciones
                            .nvl(registroAux.getCampos().get("ESCRITURA_NO"),
                                            "")
                            .toString();
            codPredial = SysmanFunciones
                            .nvl(registroAux.getCampos().get("COD_PREDIAL"), "")
                            .toString();
            serie = SysmanFunciones
                            .nvl(registroAux.getCampos().get("SERIE_PLACA"), "")
                            .toString();
            elemento = SysmanFunciones
                            .nvl(registroAux.getCampos().get("ELEMENTO"), "")
                            .toString();
            placaAnulada = (boolean) SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get("PLACAANULADA"), false);
        }
        else {
            seleccion = SysmanFunciones
                            .nvl(registroAux.getCampos().get(FrmadicionControladorEnum.SELECCION.getValue()), "")
                            .toString();
            nombreSeleccion = SysmanFunciones
                            .nvl(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
                            .toString();
            direccion = SysmanFunciones
                            .nvl(registroAux.getCampos().get("DESCRIPCION"), "")
                            .toString();
            serie = SysmanFunciones
                            .nvl(registroAux.getCampos().get("SERIE_PLACA"), "")
                            .toString();
            escritura = SysmanFunciones
                            .nvl(registroAux.getCampos().get("NO_TRAMO"), "")
                            .toString();
            elemento = SysmanFunciones
                            .nvl(registroAux.getCampos().get("ELEMENTO"), "")
                            .toString();
            placaAnulada = (boolean) SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get("PLACAANULADA"), false);
        }
        adicionesAlmacen = "";
        cargarListacbAdicionesAlmacen();
        // Bloquea Ordenado por y descripcion cuando la placa esta
        // anulada
        if (placaAnulada) {
            bloqueado = true;
        }

    }

    // onRowSelectORDENA
    public void seleccionarFilaORDENA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ordena = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CEDULA"), "")
                        .toString();
        nombreResponsable = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "")
                        .toString();

    }

    // onRowSelectcbAdicionesAlmacen
    public void seleccionarFilacbAdicionesAlmacen(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        adicionesAlmacen = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPO_MOV"), "")
                        .toString();
        tipoMovimiento = adicionesAlmacen;
        numeroMovimiento = SysmanFunciones
                        .nvl(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
                        .toString();
        valorTotal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("VALORTOTAL"), "")
                        .toString();
        try {
            fechaMov = SysmanFunciones
                            .convertirAFecha(SysmanFunciones
                                            .nvl(registroAux.getCampos()
                                                            .get(GeneralParameterEnum.FECHA.getName()), "")
                                            .toString());
        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        anioConstruccion = new SimpleDateFormat("yyyy").format(fechaMov);
        nombreAdicion = tipoMovimiento + " " + numeroMovimiento + " "
            + registroAux.getCampos().get("NOMBRE_MOVIMIENTO").toString();
        ordena = null;
        descripcion = null;
        nombreResponsable = null;
        // 153001
        Registro regAux = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.SERIE.getName(), serie);
        param.put(FrmadicionControladorEnum.SELECCION.getValue(), seleccion);
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                        tipoMovimiento);
        param.put(FrmadicionControladorEnum.NUMEROMOVIMIENTO.getValue(),
                        numeroMovimiento);
        param.put(FrmadicionControladorEnum.TIPOINMUEBLE.getValue(),
                        tipoInmueble);
        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmadicionControladorUrlEnum.URL7074
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (regAux != null) {
            ordena = SysmanFunciones
                            .nvl(regAux.getCampos().get("RESPONSABLE_ORDENA"),
                                            "")
                            .toString();
            descripcion = SysmanFunciones
                            .nvl(regAux.getCampos().get("DESCRIPCION"), "")
                            .toString();
            nombreResponsable = SysmanFunciones
                            .nvl(regAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
                            .toString();
        }
    }

    public String getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(String seleccion) {
        this.seleccion = seleccion;
    }

    public String getOrdena() {
        return ordena;
    }

    public void setOrdena(String ordena) {
        this.ordena = ordena;
    }

    public String getAdicionesAlmacen() {
        return adicionesAlmacen;
    }

    public void setAdicionesAlmacen(String adicionesAlmacen) {
        this.adicionesAlmacen = adicionesAlmacen;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEscritura() {
        return escritura;
    }

    public void setEscritura(String escritura) {
        this.escritura = escritura;
    }

    public String getCodPredial() {
        return codPredial;
    }

    public void setCodPredial(String codPredial) {
        this.codPredial = codPredial;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreAdicion() {
        return nombreAdicion;
    }

    public void setNombreAdicion(String nombreAdicion) {
        this.nombreAdicion = nombreAdicion;
    }

    public String getNombreSeleccion() {
        return nombreSeleccion;
    }

    public void setNombreSeleccion(String nombreSeleccion) {
        this.nombreSeleccion = nombreSeleccion;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public RegistroDataModelImpl getListaSELECCION() {
        return listaSELECCION;
    }

    public void setListaSELECCION(RegistroDataModelImpl listaSELECCION) {
        this.listaSELECCION = listaSELECCION;
    }

    public RegistroDataModelImpl getListaORDENA() {
        return listaORDENA;
    }

    public void setListaORDENA(RegistroDataModelImpl listaORDENA) {
        this.listaORDENA = listaORDENA;
    }

    public String getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public boolean getPlacaAnulada() {
        return placaAnulada;
    }

    public boolean isPlacaAnulada() {
        return placaAnulada;
    }

    public void setPlacaAnulada(boolean placaAnulada) {
        this.placaAnulada = placaAnulada;
    }

    public String getMenuActual() {
        return menuActual;
    }

    public void setMenuActual(String menuActual) {
        this.menuActual = menuActual;
    }

    public String getTituloDireccion() {
        return tituloDireccion;
    }

    public void setTituloDireccion(String tituloDireccion) {
        this.tituloDireccion = tituloDireccion;
    }

    public String getTituloEscritura() {
        return tituloEscritura;
    }

    public void setTituloEscritura(String tituloEscritura) {
        this.tituloEscritura = tituloEscritura;
    }

    public boolean isVisibleCodPredial() {
        return visibleCodPredial;
    }

    public String getEtiquetaSeleccion() {
        return etiquetaSeleccion;
    }

    public void setEtiquetaSeleccion(String etiquetaSeleccion) {
        this.etiquetaSeleccion = etiquetaSeleccion;
    }

    public String getInformacionTitulo() {
        return informacionTitulo;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setInformacionTitulo(String informacionTitulo) {
        this.informacionTitulo = informacionTitulo;
    }

    public void setVisibleCodPredial(boolean visibleCodPredial) {
        this.visibleCodPredial = visibleCodPredial;
    }

    public RegistroDataModelImpl getListacbAdicionesAlmacen() {
        return listacbAdicionesAlmacen;
    }

    public void setListacbAdicionesAlmacen(
        RegistroDataModelImpl listacbAdicionesAlmacen) {
        this.listacbAdicionesAlmacen = listacbAdicionesAlmacen;
    }
}
