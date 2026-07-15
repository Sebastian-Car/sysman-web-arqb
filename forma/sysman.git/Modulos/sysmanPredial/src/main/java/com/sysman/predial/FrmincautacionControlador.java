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
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.FrmincautacionControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
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
 * @author dsuesca
 * @version 1, 27/05/2016
 * 
 * @modifier amonroy
 * @version 2, 30/06/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para procedimiento que es llamado en el
 * controlador
 */
@ManagedBean
@ViewScoped
public class FrmincautacionControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String predio;
    private Date fechaInicial;
    private Date fechaFinal;
    private String noResolucion;
    private Date fechaResolucion;
    private String elabResolucion;
    private String firmaResolucion;
    private boolean fecInicialBloqueada;
    private boolean fecFinalBloqueado;
    private int predioInc;
    private int anoPago;
    /**
     * Implementacion del EJB de EjbServiciosPublicosCeroRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_PREDIAL_COM4
     */
    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatro;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTxtPredio;

    private static final String PAGOANO = "PAGO_ANO";
    private static final String FORMATOFECHAHORA = "dd/MM/yyyy HH:mm:ss";
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmincautacionControlador
     */
    public FrmincautacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINCAUTACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmincautacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTxtPredio();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTxtPredio() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmincautacionControladorUrlEnum.URL3391
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaTxtPredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdAceptar() {
        String obs = "";
        String campos = "";
        int opcion = 0;
        try {
            if (predioInc == 2) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1111"));
            }
            else if (predioInc == 1) {
                obs = idioma.getString("TB_TB1112");
                campos = "IP_USUARIOS_PREDIAL.FECFINAL_EXEINT = ''"
                    + SysmanFunciones.convertirAFechaCadena(fechaFinal,
                                    FORMATOFECHAHORA)
                    + "''";
                opcion = 1;
            }
            else {
                if (SysmanFunciones.ano(fechaInicial) < 2002) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1115"));
                    return;
                }
                if (SysmanFunciones.ano(fechaInicial) <= anoPago) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1116"));
                    return;
                }
                if (fechaFinal == null) {
                    obs = idioma.getString("TB_TB1117");
                    campos = "IP_USUARIOS_PREDIAL.IND_EXEINT = -1 , IP_USUARIOS_PREDIAL.FECINICIAL_EXEINT = ''"
                        + SysmanFunciones.convertirAFechaCadena(fechaInicial,
                                        FORMATOFECHAHORA)
                        + "''";
                    opcion = 2;
                }
                else {
                    obs = idioma.getString("TB_TB1118");
                    campos = "IP_USUARIOS_PREDIAL.IND_EXEINT = -1, IP_USUARIOS_PREDIAL.FECINICIAL_EXEINT = ''"
                        + SysmanFunciones.convertirAFechaCadena(fechaInicial,
                                        FORMATOFECHAHORA)
                        + "''"
                        + ", IP_USUARIOS_PREDIAL.FECFINAL_EXEINT = ''"
                        + SysmanFunciones.convertirAFechaCadena(fechaFinal,
                                        FORMATOFECHAHORA)
                        + "''";
                    opcion = 3;
                }
            }

            obs = obs.replace("s$numResolucion$s", noResolucion);

            obs = obs.replace("s$fechaResolucion$s", SysmanFunciones
                            .convertirAFechaCadena(fechaResolucion));

            ejbPredialCuatro.incautarPredio(compania,
                            predio,
                            SessionUtil.getUser().getCodigo(),
                            obs,
                            opcion,
                            campos,
                            noResolucion);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1114"));
            limpiarValores();

        }
        catch (ParseException | SystemException ex)

        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmincautacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void limpiarValores() {
        predio = null;
        fechaInicial = null;
        fechaFinal = null;
        noResolucion = null;
        fechaResolucion = null;
        elabResolucion = null;
        firmaResolucion = null;
    }

    // </METODOS_BOTONES>

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTxtPredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predio = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        try {
            String msj;
            Date fechaInicialPredio;
            Date fechaFinalPredio;

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.CODIGO.getName(), predio);
            params.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmincautacionControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));

            if (rs == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1110"));
                return;
            }

            boolean indexe = (boolean) (rs.getCampos()
                            .get("IND_EXEINT") == null ? 0
                                : rs.getCampos().get("IND_EXEINT"));
            if (indexe) {
                if (rs.getCampos().get("FECFINAL_EXEINT") == null) {

                    fechaInicialPredio = (Date) rs.getCampos()
                                    .get("FECINICIAL_EXEINT");

                    msj = idioma.getString("TB_TB1106");
                    msj = msj.replace("s$fechaInicial$s",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicialPredio));
                    JsfUtil.agregarMensajeInformativo(msj);

                    fechaInicial = fechaInicialPredio;
                    fechaFinal = new Date();
                    fecInicialBloqueada = true;
                    fecFinalBloqueado = false;
                    predioInc = 1;
                    anoPago = Integer.valueOf(
                                    rs.getCampos().get(PAGOANO).toString());

                }
                else {
                    fechaInicialPredio = (Date) rs.getCampos()
                                    .get("FECINICIAL_EXEINT");
                    fechaFinalPredio = (Date) rs.getCampos()
                                    .get("FECFINAL_EXEINT");
                    msj = idioma.getString("TB_TB1109");
                    msj = msj.replace("s$fechaInicial$s",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicialPredio));
                    msj = msj.replace("s$fechaFinal$s",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinalPredio));
                    JsfUtil.agregarMensajeInformativo(msj);

                    fechaInicial = fechaInicialPredio;
                    fechaFinal = fechaFinalPredio;
                    fecInicialBloqueada = true;
                    fecFinalBloqueado = true;
                    predioInc = 2;
                    anoPago = Integer.valueOf(SysmanFunciones.nvl(
                                    rs.getCampos().get(PAGOANO), "")
                                    .toString());

                }
            }
            else {

                predioInc = 0;
                fechaInicial = null;
                fechaFinal = null;
                fecInicialBloqueada = false;
                fecFinalBloqueado = false;
                anoPago = Integer
                                .valueOf(SysmanFunciones
                                                .nvl(rs.getCampos().get(
                                                                PAGOANO), "0")
                                                .toString());
            }

        }
        catch (ParseException | SystemException ex) {

            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmincautacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getNoResolucion() {
        return noResolucion;
    }

    public void setNoResolucion(String noResolucion) {
        this.noResolucion = noResolucion;
    }

    public Date getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(Date fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getElabResolucion() {
        return elabResolucion;
    }

    public void setElabResolucion(String elabResolucion) {
        this.elabResolucion = elabResolucion;
    }

    public String getFirmaResolucion() {
        return firmaResolucion;
    }

    public void setFirmaResolucion(String firmaResolucion) {
        this.firmaResolucion = firmaResolucion;
    }

    public boolean isFecInicialBloqueada() {
        return fecInicialBloqueada;
    }

    public void setFecInicialBloqueada(boolean fecInicialBloqueada) {
        this.fecInicialBloqueada = fecInicialBloqueada;
    }

    public boolean isFecFinalBloqueado() {
        return fecFinalBloqueado;
    }

    public void setFecFinalBloqueado(boolean fecFinalBloqueado) {
        this.fecFinalBloqueado = fecFinalBloqueado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTxtPredio() {
        return listaTxtPredio;
    }

    public void setListaTxtPredio(RegistroDataModelImpl listaTxtPredio) {
        this.listaTxtPredio = listaTxtPredio;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
