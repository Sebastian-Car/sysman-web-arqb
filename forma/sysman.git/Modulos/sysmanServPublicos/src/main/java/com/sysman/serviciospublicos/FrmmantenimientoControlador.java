package com.sysman.serviciospublicos;

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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.FrmmantenimientoControladorUrlEnum;
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
 * @version 1, 24/08/2016
 * 
 * @author jreina
 * @version 2, 13/06/2017 Cambio código formulario y actualización de
 * ConnectorPool
 * @modified jguerrero
 * @version 2. 23/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 * 
 */
@ManagedBean
@ViewScoped

public class FrmmantenimientoControlador extends BeanBaseModal {
    private final String compania;
    private final String cFechaMax;
    // <DECLARAR_ATRIBUTOS>
    private String ciclo;
    private Date fecha;
    private Date fechaPreparacion;
    private String ano;
    private String periodo;
    private boolean ejecutaMantenimientoDeRecaudosSinControl;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCiclo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmmantenimientoControlador
     */

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServPublOcho;

    public FrmmantenimientoControlador() {
        super();
        compania = SessionUtil.getCompania();
        cFechaMax = "FECHAMAX";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMMANTENIMIENTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmmantenimientoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        try {
            ejecutaMantenimientoDeRecaudosSinControl = "SI".equalsIgnoreCase(
                            SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "EJECUTA MANTENIMIENTO DE RECUADOS SIN CONTROL",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "NO").toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1060-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 74, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmantenimientoControladorUrlEnum.URL3648
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimircmdAceptar() {
        // <CODIGO_DESARROLLADO>
        if (validar()) {

            try {

                ejbServPublOcho.reconstruccionDeRecaudosPorConcepto(compania,
                                fecha, SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            catch (SystemException e) {
                Logger.getLogger(FrmmantenimientoControlador.class.getName())
                                .log(Level.SEVERE, null, e);

                JsfUtil.agregarMensajeError(e.getMessage());

            }

        }

        // </CODIGO_DESARROLLADO>
    }

    public boolean validar() {
        try {
            if (fechaPreparacion == null) {
                StringBuilder mensaje = new StringBuilder();
                mensaje.append(idioma.getString("TB_TB931")).append(" ")
                                .append(idioma.getString("TB_TB1292"));
                JsfUtil.agregarMensajeAlerta(mensaje.toString());
                return false;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            Registro rs;

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmantenimientoControladorUrlEnum.URL3649
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs.getCampos().get(cFechaMax) != null) {
                if (!ejecutaMantenimientoDeRecaudosSinControl
                    && !fecha.after(fechaPreparacion)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1389"));
                    return false;
                }
                else if (!fecha.after((Date) rs.getCampos().get(cFechaMax))) {

                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1390")
                        + " " + SysmanFunciones.convertirAFechaCadena(
                                        (Date) rs.getCampos().get(cFechaMax)));
                    return false;

                }
            }
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = validarStringRegistro(registroAux,
                        GeneralParameterEnum.NUMERO.getName());

        fechaPreparacion = registroAux.getCampos()
                        .get("FECHA_PREPARACION") == null ? null
                            : (Date) registroAux.getCampos()
                                            .get("FECHA_PREPARACION");
        ano = validarStringRegistro(registroAux,
                        GeneralParameterEnum.ANO.getName());
        periodo = validarStringRegistro(registroAux,
                        GeneralParameterEnum.PERIODO.getName());

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private String validarStringRegistro(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

}
