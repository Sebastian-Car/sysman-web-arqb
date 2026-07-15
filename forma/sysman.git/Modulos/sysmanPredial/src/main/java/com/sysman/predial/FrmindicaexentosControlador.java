package com.sysman.predial;

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
import com.sysman.predial.ejb.EjbPredialSeisRemote;
import com.sysman.predial.enums.FrmindicaexentosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
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

/**
 *
 * @author acaceres
 * @version 1, 25/05/2016
 * 
 * @version 2.0, 06/07/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Manejo de EJBs.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmindicaexentosControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del numero de
     * orden predial
     */
    private final String numeroOrden;

    /**
     * Constante a nivel de clase que aloja el nivel del usuario que
     * inicio sesion
     */
    private final int nivelUsuario;

    /**
     * Constante a nivel de clase que aloja el modulo en el que el
     * usuario esta interactuando
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion
     */
    private final String usuario;

    private final String tb1219;
    private final String conscodigo;

    // <DECLARAR_ATRIBUTOS>
    private boolean indExentoImpuesto;
    private boolean indExentoCAR;
    private boolean indExentoOtros;

    /**
     * Atributo que controla el valor seleccionado en el combo:
     * <code>Codigo catastral</code>
     */
    private String codigo;

    /**
     * Atributo que contiene el valor seleccionado en el combo 'Desde'
     */
    private int desde;

    /**
     * Atributo que contiene el valor seleccionado en el combo 'Hasta'
     */
    private int hasta;

    /**
     * Atributo que controla el valor ingresado en el campo:
     * <code>Numero</code>
     */
    private String numeroResol;
    private Date fechaResol;
    private String elaboradoPor;
    private String firmadaPor;
    private String codigoForm;
    private String sql;
    private String tipoRes;
    private Registro reg;

    /**
     * Atributo que contiene la descripcion con los datos de la
     * resolucion a aplicar
     */
    private String obs;
    private int pagoAno;
    private boolean dialogoVisible;
    private String desDialogo;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaEXINICIAL;
    private List<Registro> listaEXFINAL;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbCodigo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR_EJBs>
    /**
     * Instancia que permite utilizar a las funciones y procedimientos
     * del paquete <code>PCK_PREDIAL_COM6</code>
     */
    @EJB
    private EjbPredialSeisRemote ejbPredialSeis;
    // </DECLARAR_EJBs>

    /**
     * Creates a new instance of FrmindicaexentosControlador
     */
    public FrmindicaexentosControlador() {
        super();

        compania = SessionUtil.getCompania();
        numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        modulo = SessionUtil.getModulo();
        nivelUsuario = SessionUtil.getNivelUsuario(modulo);
        usuario = SessionUtil.getUser().getCodigo();

        tb1219 = "TB_TB1219";
        conscodigo = "CODIGO";

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINDICAEXENTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmindicaexentosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        cargarListaEXINICIAL();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbCodigo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        fechaResol = new Date();

        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        tipoRes = "1";
        obs = "";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaEXINICIAL() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaEXINICIAL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmindicaexentosControladorUrlEnum.URL4147
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaEXFINAL() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), desde);

        try {
            listaEXFINAL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmindicaexentosControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbCodigo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmindicaexentosControladorUrlEnum.URL5121
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        listaCmbCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, conscodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdRegistrar() {
        dialogoVisible = false;

        try {
            obs = idioma.getString("TB_TB497")
                            .replace("#NUMRES#", numeroResol)
                            .replace("#FECHARES#", SysmanFunciones
                                            .convertirAFechaCadena(fechaResol))
                            .replace("#NOMELABORO#", elaboradoPor)
                            .replace("#NOMFIRMO#", firmadaPor);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ("1".equals(tipoRes)) {
            habilitarExento();
        }
        else {
            deshabilitarExento();
        }
        // </CODIGO_DESARROLLADO>
    }

    /** Contiene el proceso para habilitar el exento de un predio */
    private void habilitarExento() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbPredialSeis.habilitarExento(compania, nivelUsuario, usuario,
                            indExentoImpuesto, indExentoCAR, indExentoOtros,
                            desde, hasta, codigo, numeroOrden, numeroResol,
                            fechaResol, elaboradoPor, firmadaPor, obs);

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1227")
                            .replace("#ESTADO#", "habilitado"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Contiene el proceso para deshabilitar el exento de un predio
     */
    private void deshabilitarExento() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbPredialSeis.deshabilitarExento(compania, nivelUsuario, usuario,
                            indExentoImpuesto, indExentoCAR, indExentoOtros,
                            desde, hasta, codigo, numeroOrden, numeroResol,
                            fechaResol, elaboradoPor, firmadaPor, obs);

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1227")
                            .replace("#ESTADO#", "deshabilitado"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarEXFINAL() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEXINICIAL() {
        // <CODIGO_DESARROLLADO>
        if (desde <= pagoAno) {
            desde = pagoAno + 1;

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1228"));
        }

        cargarListaEXFINAL();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmbCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigo = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        pagoAno = Integer
                        .parseInt(SysmanFunciones
                                        .nvl(registroAux.getCampos().get(
                                                        "PAGO_ANO"), "0")
                                        .toString());

        desde = hasta = 0;
        listaEXFINAL = null;
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo des en
     * la vista
     */
    public void aceptardes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo des
     * en la vista
     */
    public void cancelardes() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeAlerta(idioma.getString(tb1219));
        // </CODIGO_DESARROLLADO>
    }
    // <SET_GET_ATRIBUTOS>

    public void retornarFormularioCmdRegistrar(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getDesde() {
        return desde;
    }

    public void setDesde(int desde) {
        this.desde = desde;
    }

    public int getHasta() {
        return hasta;
    }

    public void setHasta(int hasta) {
        this.hasta = hasta;
    }

    public String getNumeroResol() {
        return numeroResol;
    }

    public void setNumeroResol(String numeroResol) {
        this.numeroResol = numeroResol;
    }

    public Date getFechaResol() {
        return fechaResol;
    }

    public void setFechaResol(Date fechaResol) {
        this.fechaResol = fechaResol;
    }

    public String getElaboradoPor() {
        return elaboradoPor;
    }

    public void setElaboradoPor(String elaboradoPor) {
        this.elaboradoPor = elaboradoPor;
    }

    public String getFirmadaPor() {
        return firmadaPor;
    }

    public void setFirmadaPor(String firmadaPor) {
        this.firmadaPor = firmadaPor;
    }

    public String getCodigoForm() {
        return codigoForm;
    }

    public void setCodigoForm(String codigoForm) {
        this.codigoForm = codigoForm;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Registro getReg() {
        return reg;
    }

    public void setReg(Registro reg) {
        this.reg = reg;
    }

    public int getPagoAno() {
        return pagoAno;
    }

    public void setPagoAno(int pagoAno) {
        this.pagoAno = pagoAno;
    }

    public boolean isIndExentoImpuesto() {
        return indExentoImpuesto;
    }

    public void setIndExentoImpuesto(boolean indExentoImpuesto) {
        this.indExentoImpuesto = indExentoImpuesto;
    }

    public boolean isIndExentoCAR() {
        return indExentoCAR;
    }

    public void setIndExentoCAR(boolean indExentoCAR) {
        this.indExentoCAR = indExentoCAR;
    }

    public boolean isIndExentoOtros() {
        return indExentoOtros;
    }

    public void setIndExentoOtros(boolean indExentoOtros) {
        this.indExentoOtros = indExentoOtros;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaEXINICIAL() {
        return listaEXINICIAL;
    }

    public void setListaEXINICIAL(List<Registro> listaEXINICIAL) {
        this.listaEXINICIAL = listaEXINICIAL;
    }

    public List<Registro> getListaEXFINAL() {
        return listaEXFINAL;
    }

    public void setListaEXFINAL(List<Registro> listaEXFINAL) {
        this.listaEXFINAL = listaEXFINAL;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCmbCodigo() {
        return listaCmbCodigo;
    }

    public void setListaCmbCodigo(RegistroDataModelImpl listaCmbCodigo) {
        this.listaCmbCodigo = listaCmbCodigo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    public String getDesDialogo() {
        return desDialogo;
    }

    public void setDesDialogo(String desDialogo) {
        this.desDialogo = desDialogo;
    }

    public String getTipoRes() {
        return tipoRes;
    }

    public void setTipoRes(String tipoRes) {
        this.tipoRes = tipoRes;
    }
}
