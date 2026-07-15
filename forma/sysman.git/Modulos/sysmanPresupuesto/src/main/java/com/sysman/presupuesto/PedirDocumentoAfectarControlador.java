package com.sysman.presupuesto;

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
import com.sysman.presupuesto.ejb.EjbPresupuestoUnoRemote;
import com.sysman.presupuesto.enums.PedirDocumentoAfectarControladorEnum;
import com.sysman.presupuesto.enums.PedirDocumentoAfectarControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * @author jlozano
 * @version 4, 07/09/2016 14:41:50 -- Modificado por jlozano
 * @modified jsforero
 * @version 2. 19/04/2017 Se realizo el refactoring.
 */
@ManagedBean
@ViewScoped
public class PedirDocumentoAfectarControlador extends BeanBaseModal {
    private final String compania;
    private final String numero;
    // <DECLARAR_ATRIBUTOS>
    private String cpteAfectado;
    private String tipoCpteAfect;
    private String tituloComprobante;
    private String strEstadoError;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmpteAfectado;
    private RegistroDataModelImpl listaTipoCpteAfect;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String terceroComprobante;
    private String sucursalComprobante;
    private Date fechaComprobante;
    private String claseAfectar;
    private String ano;
    private int anoH;
    private String tipoComprobante;
    private String numeroComprobante;
    private String nombreComprobante;
    private String claseComprobante;
    private String claseComprobanteAfectado;
    /**
     * Indica si se conserva durante el proceso el tercero relacionado
     * segun el tipo de comprobante. Valor segun la columna
     * TECEROIGUAL de la tabla TIPO_COMRPOBPP.
     */
    private String pideTercero;
    private String tituloMensaje;
    private boolean visibleDgAceptarAfectar;
    private boolean visibleDgComfirmarPAC;
    private Date fechaCpteAfectado;
    /**
     * Indica el tipo de afectacion que maneja la clase presupuestal.
     */
    private String afectacion;

    @EJB
    private EjbPresupuestoUnoRemote presupuestoUno;

    /**
     * Creates a new instance of PedirDocumentoAfectarControlador
     */
    public PedirDocumentoAfectarControlador() {
        super();
        compania = SessionUtil.getCompania();
        numero = "NUMERO";
        try {
            numFormulario = GeneralCodigoFormaEnum.PEDIR_DOCUMENTO_AFECTAR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            cargarFlash();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoCpteAfect();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    public void cargarFlash() {
        Map<String, Object> parametrosEntrada;
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            ano = (String) parametrosEntrada.get("ano");
            tipoComprobante = (String) parametrosEntrada.get("tipoComprobante");
            numeroComprobante = (String) parametrosEntrada
                            .get("numeroComprobante");
            nombreComprobante = (String) parametrosEntrada
                            .get("nombreComprobante");
            claseComprobante = (String) parametrosEntrada
                            .get("claseComprobante");
            claseAfectar = (String) parametrosEntrada.get("claseAfectar");
            pideTercero = (String) parametrosEntrada.get("pideTercero");
            terceroComprobante = (String) parametrosEntrada
                            .get("terceroComprobante");
            sucursalComprobante = (String) parametrosEntrada
                            .get("sucursalComprobante");
            fechaComprobante = (Date) parametrosEntrada.get("fechaComprobante");
            afectacion = (String) parametrosEntrada.get("afectacion");
        }
        else {
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        tituloComprobante = SysmanFunciones.concatenar(nombreComprobante,
                        " -  No. -  ", numeroComprobante);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmpteAfectado() {
        int piTercero = 0;
        int inAfectacion = 3;
        if ("S".equals(pideTercero)) {
            piTercero = 1;
        }
        if (!"A".equals(afectacion)) {
            // Carga solo las disponibilidades abiertas
            if ("DIN".equals(claseComprobante)
                || "DIC".equals(claseComprobante)) {
                inAfectacion = 0;
            }
            else {
                inAfectacion = 1;
            }
        }

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PedirDocumentoAfectarControladorUrlEnum.URL7752
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(PedirDocumentoAfectarControladorEnum.TIPO.getValue(),
                        tipoCpteAfect);
        param.put(PedirDocumentoAfectarControladorEnum.FECHACOMP.getValue(),
                        fechaComprobante);
        param.put(PedirDocumentoAfectarControladorEnum.PIDETERCERO.getValue(),
                        piTercero);
        param.put(PedirDocumentoAfectarControladorEnum.INDAFECTACION.getValue(),
                        inAfectacion);
        param.put(PedirDocumentoAfectarControladorEnum.TERCERO.getValue(),
                        terceroComprobante);
        param.put(PedirDocumentoAfectarControladorEnum.SUCURSAL.getValue(),
                        sucursalComprobante);

        listaCmpteAfectado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, numero);
    }

    public void cargarListaTipoCpteAfect() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PedirDocumentoAfectarControladorUrlEnum.URL8770
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.CLASE.name(), claseAfectar);

        listaTipoCpteAfect = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>

    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        StringBuilder cadena = new StringBuilder();
        if (!" ".equals(SysmanFunciones.nvl(tipoCpteAfect, " "))
            && !SysmanFunciones.nvl(cpteAfectado, 0).equals(0)) {
            List<Registro> listado = null;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.name(), compania);
            param.put(PedirDocumentoAfectarControladorEnum.FECHACOMP.getValue(),
                            fechaComprobante);
            param.put(PedirDocumentoAfectarControladorEnum.TIPOAF.getValue(),
                            tipoCpteAfect);
            param.put(PedirDocumentoAfectarControladorEnum.NUMEROAFE.getValue(),
                            cpteAfectado);

            UrlBean urlListAno = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PedirDocumentoAfectarControladorUrlEnum.URL725
                                                            .getValue());
            try {
                listado = RegistroConverter.toListRegistro(requestManager
                                .getList(urlListAno.getUrl(), param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            for (Registro r : listado) {
                cadena.append("\n\rComprobante: "
                    + r.getCampos().get("TIPO") + " -- "
                    + r.getCampos().get(numero)
                    + " de " + r.getCampos().get("FECHA"));
            }
            strEstadoError = cadena.toString();
            if (!"".equals(strEstadoError)) {
                visibleDgAceptarAfectar = true;
            }
            else {
                aceptardgAceptarAfectar();
            }
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1442"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void aceptardgAceptarAfectar() {
        String claseC;
        String claseH;
        Date fechaAux;
        BigDecimal numeroC;
        BigDecimal numeroH;
        String tipoC;
        String tipoH;
        claseC = claseComprobante;
        tipoC = tipoComprobante;
        numeroC = new BigDecimal(numeroComprobante);
        tipoH = tipoCpteAfect;
        claseH = claseComprobanteAfectado;
        numeroH = new BigDecimal(cpteAfectado);
        fechaAux = fechaComprobante;
        try {
            String rta = presupuestoUno.seleccionarDocumentoAfectar(compania,
                            anoH, tipoH, tipoC, numeroH, numeroC, claseH,
                            claseC,
                            fechaCpteAfectado,
                            fechaAux,
                            SessionUtil.getUser().getCodigo());

            if (!" ".equals(rta)) {
                JsfUtil.agregarMensajeAlerta(rta);
            }
            else {
                RequestContext.getCurrentInstance().closeDialog(true);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelardgAceptarAfectar() {
        visibleDgAceptarAfectar = false;
    }

    public void aceptardgComfirmarPAC() {
        visibleDgComfirmarPAC = false;
    }

    public void cancelardgComfirmarPAC() {
        visibleDgComfirmarPAC = false;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiardgAceptarAfectar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmpteAfectado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cpteAfectado = SysmanFunciones
                        .nvl(registroAux.getCampos().get(numero), "")
                        .toString();
        anoH = Integer.valueOf(registroAux.getCampos().get("ANO").toString());
        try {
            fechaCpteAfectado = SysmanFunciones.convertirAFecha(
                            (String) registroAux.getCampos().get("FECHA"));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaTipoCpteAfect(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoCpteAfect = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        claseComprobanteAfectado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CLASE"), "")
                        .toString();
        cpteAfectado = null;
        cargarListaCmpteAfectado();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCpteAfectado() {
        return cpteAfectado;
    }

    public void setCpteAfectado(String cpteAfectado) {
        this.cpteAfectado = cpteAfectado;
    }

    public String getTipoCpteAfect() {
        return tipoCpteAfect;
    }

    public void setTipoCpteAfect(String tipoCpteAfect) {
        this.tipoCpteAfect = tipoCpteAfect;
    }

    public String getTituloComprobante() {
        return tituloComprobante;
    }

    public void setTituloComprobante(String tituloComprobante) {
        this.tituloComprobante = tituloComprobante;
    }

    public String getStrEstadoError() {
        return strEstadoError;
    }

    public void setStrEstadoError(String strEstadoError) {
        this.strEstadoError = strEstadoError;
    }

    public boolean isVisibleDgAceptarAfectar() {
        return visibleDgAceptarAfectar;
    }

    public void setVisibleDgAceptarAfectar(boolean visibleDgAceptarAfectar) {
        this.visibleDgAceptarAfectar = visibleDgAceptarAfectar;
    }

    public boolean isVisibleDgComfirmarPAC() {
        return visibleDgComfirmarPAC;
    }

    public void setVisibleDgComfirmarPAC(boolean visibleDgComfirmarPAC) {
        this.visibleDgComfirmarPAC = visibleDgComfirmarPAC;
    }

    public String getTituloMensaje() {
        return tituloMensaje;
    }

    public void setTituloMensaje(String tituloMensaje) {
        this.tituloMensaje = tituloMensaje;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCmpteAfectado() {
        return listaCmpteAfectado;
    }

    public void setListaCmpteAfectado(
        RegistroDataModelImpl listaCmpteAfectado) {
        this.listaCmpteAfectado = listaCmpteAfectado;
    }

    public RegistroDataModelImpl getListaTipoCpteAfect() {
        return listaTipoCpteAfect;
    }

    public void setListaTipoCpteAfect(
        RegistroDataModelImpl listaTipoCpteAfect) {
        this.listaTipoCpteAfect = listaTipoCpteAfect;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}