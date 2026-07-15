package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialUnoRemote;
import com.sysman.predial.enums.IndprocesodecobroControladorEnum;
import com.sysman.predial.enums.IndprocesodecobroControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 26/05/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author eamaya
 * @version 3.0, 06/07/2017, Proceso de Refactoring DSS y Manejo de
 * EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class IndprocesodecobroControlador extends BeanBaseDatosAcme {
    private final String compania;
    private final String modulo;

    private final String strProcesoEjecutado;
    /**
     * Constante que almacena el valor de la cadena NUMERO_PROCESO
     */
    private final String strNumeroProceso;
    /**
     * Constante que almacena el valor de la cadena PREDIO CON PROCESO
     */
    private final String strPredioConProceso;

    /**
     * Constante que almacena el valor de la cadena PROCESO_DE_COBRO
     */
    private final String strProcesoDeCobro;
    // <DECLARAR_ATRIBUTOS>
    private String codigo;
    private String txtNombre;
    private String txtEstado;
    private String txtNumProceso;
    private boolean bloqueaCmdObservaciones;
    private boolean bloqueaCmdRegistrarNotif;
    private boolean txtNumProcesoBloqueado;

    private boolean procesoDeCobro = false;
    private String numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigo;

    @EJB
    private EjbPredialUnoRemote ejbPredialUno;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public IndprocesodecobroControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strProcesoEjecutado = "MSM_PROCESO_EJECUTADO";
        strNumeroProceso = "NUMERO_PROCESO";
        strPredioConProceso = "PREDIO CON PROCESO";
        strProcesoDeCobro = "PROCESO_DE_COBRO";
        try {
            numFormulario = GeneralCodigoFormaEnum.INDPROCESODECOBRO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(IndprocesodecobroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>

        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        tabla = "";
        asignarOrigenDatos();
        reasignarOrigenGrilla();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacodigo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IndprocesodecobroControladorUrlEnum.URL4416
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(IndprocesodecobroControladorEnum.PARAM0.getValue(),
                        numeroOrden);

        listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigo = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        txtNombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        if ((registroAux.getCampos().get(strNumeroProceso) != null)
            && !".".equals(registroAux.getCampos().get(strNumeroProceso))) {
            txtNumProceso = registroAux.getCampos().get(strNumeroProceso)
                            .toString();
            txtNumProcesoBloqueado = true;
        }
        else {
            txtNumProceso = ".";
            txtNumProcesoBloqueado = false;
        }

        List<Registro> rs;

        HashMap<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            IndprocesodecobroControladorUrlEnum.URL6070
                                                                            .getValue())
                                            .getUrl(), param));

            for (Registro registro : rs) {
                procesoDeCobro = (boolean) SysmanFunciones
                                .nvl(registro.getCampos()
                                                .get(strProcesoDeCobro),
                                                "false");

                if (!procesoDeCobro) {
                    txtEstado = idioma.getString("TB_TB563");
                }
                else {
                    txtEstado = idioma.getString("TB_TB564");
                }

            }
            // Desbloquea el bot�n Observaciones.
            if (!"".equals(codigo)) {
                if (strPredioConProceso.equals(txtEstado)) {
                    bloqueaCmdRegistrarNotif = false;
                }
                else {
                    bloqueaCmdRegistrarNotif = true;
                }
                bloqueaCmdObservaciones = false;
            }
            else {
                bloqueaCmdObservaciones = true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    private void procesoDeCobro(Registro registroAux) {
        if (registroAux != null) {
            txtEstado = "0".equals(registroAux.getCampos()
                            .get(strProcesoDeCobro))
                                ? "PREDIO SIN PROCESO"
                                : strPredioConProceso;

            int procesoDeCobroInt = (boolean) registroAux
                            .getCampos()
                            .get(strProcesoDeCobro)
                                ? -1
                                : 0;
            if (procesoDeCobroInt != 0) {

                if (registroAux.getCampos()
                                .get(strNumeroProceso) != null) {
                    txtNumProceso = registroAux.getCampos()
                                    .get(strNumeroProceso)
                                    .toString();
                    txtNumProcesoBloqueado = true;
                }
                else {
                    txtNumProceso = "";
                }

            }
        }

    }

    public void oprimirCmdActivar() {
        // <CODIGO_DESARROLLADO>
        if (!validarVacio()) {
            return;
        }

        try {

            if (!procesoDeCobro) {

                if (codigo != null) {

                    ejbPredialUno.activarPredialEnCobro(compania, codigo,
                                    SessionUtil.getUser().getCodigo(),
                                    txtNumProceso, numeroOrden);

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString(strProcesoEjecutado));
                    bloqueaCmdRegistrarNotif = false;
                }
                else {

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString(strProcesoEjecutado));

                    HashMap<String, Object> param = new HashMap<>();

                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
                    param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                    numeroOrden);

                    Registro registroAux = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    IndprocesodecobroControladorUrlEnum.URL6070
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));

                    procesoDeCobro(registroAux);

                }
                procesoDeCobro = true;
                txtEstado = idioma.getString("TB_TB564");
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2734"));
            }

        }
        catch (SystemException e) {
            Logger.getLogger(IndprocesodecobroControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVacio() {

        if (SysmanFunciones.validarVariableVacio(codigo)
            && SysmanFunciones.validarVariableVacio(txtNumProceso)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
            return false;
        }
        return true;
    }

    private void procesoDeCobroDesactivar(Registro registroAux) {
        if (registroAux != null) {
            txtEstado = "false".equals(registroAux.getCampos()
                            .get(strProcesoDeCobro).toString())
                                ? strPredioConProceso
                                : "PREDIO SIN PROCESO";

            int procesoDeCobroAux = (boolean) registroAux
                            .getCampos().get(strProcesoDeCobro)
                                ? -1 : 0;
            if (procesoDeCobroAux == 0) {
                txtNumProceso = registroAux.getCampos()
                                .get(strNumeroProceso)
                                .toString();
                txtNumProcesoBloqueado = false;
            }
        }
    }

    public void oprimirCmdDesactivar() {

        if (!validarVacio()) {
            return;
        }

        if (procesoDeCobro && codigo != null) {
            // <CODIGO_DESARROLLADO>
            try {

                HashMap<String, Object> parametro = new HashMap<>();

                parametro.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                parametro.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                parametro.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                parametro.put(GeneralParameterEnum.CODIGO.getName(), codigo);

                parametro.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                numeroOrden);

                Parameter parameter = new Parameter();
                parameter.setFields(parametro);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                IndprocesodecobroControladorUrlEnum.URL11637
                                                                .getValue());

                int resultadoUpdate = requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(), parameter);

                if (resultadoUpdate == 0) {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB1127"));
                    return;
                }

                else {
                    JsfUtil.agregarMensajeInformativo(idioma
                                    .getString(strProcesoEjecutado));
                    bloqueaCmdRegistrarNotif = true;

                    HashMap<String, Object> param = new HashMap<>();

                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
                    param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                    numeroOrden);
                    Registro registroAux = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    IndprocesodecobroControladorUrlEnum.URL6070
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));

                    procesoDeCobroDesactivar(registroAux);
                }
                procesoDeCobro = false;
            }
            catch (SystemException e) {
                Logger.getLogger(IndprocesodecobroControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2740"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdObs() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "codigo" };
        String[] valores = { codigo };

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMOBSCOBROS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdNotif() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "codigo" };
        String[] valores = { codigo };

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMNOTIFICA_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarListacodigo();
        bloqueaCmdObservaciones = true;
        bloqueaCmdRegistrarNotif = true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
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
        // </CODIGO_DESARROLLADO>
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

    // <SET_GET_ATRIBUTOS>

    public String getCodigo() {
        return codigo;
    }

    public boolean isTxtNumProcesoBloqueado() {
        return txtNumProcesoBloqueado;
    }

    public void setTxtNumProcesoBloqueado(boolean txtNumProcesoBloqueado) {
        this.txtNumProcesoBloqueado = txtNumProcesoBloqueado;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTxtNombre() {
        return txtNombre;
    }

    public void setTxtNombre(String txtNombre) {
        this.txtNombre = txtNombre;
    }

    public String getTxtEstado() {
        return txtEstado;
    }

    public void setTxtEstado(String txtEstado) {
        this.txtEstado = txtEstado;
    }

    public String getTxtNumProceso() {
        return txtNumProceso;
    }

    public void setTxtNumProceso(String txtNumProceso) {
        this.txtNumProceso = txtNumProceso;
    }

    public boolean isBloqueaCmdObservaciones() {
        return bloqueaCmdObservaciones;
    }

    public void setBloqueaCmdObservaciones(boolean bloqueaCmdObservaciones) {
        this.bloqueaCmdObservaciones = bloqueaCmdObservaciones;
    }

    public boolean isBloqueaCmdRegistrarNotif() {
        return bloqueaCmdRegistrarNotif;
    }

    public void setBloqueaCmdRegistrarNotif(boolean bloqueaCmdRegistrarNotif) {
        this.bloqueaCmdRegistrarNotif = bloqueaCmdRegistrarNotif;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacodigo() {
        return listacodigo;
    }

    public void setListacodigo(RegistroDataModelImpl listacodigo) {
        this.listacodigo = listacodigo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
