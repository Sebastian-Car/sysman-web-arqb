package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contratos.enums.ImprimeContratosControladorEnum;
import com.sysman.contratos.enums.ImprimeContratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 28/12/2015
 *
 * @version 2, 08/08/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 *
 * @modified lcortes, 29/09/2017. Se cambia el codigo del formulario a
 * cargar por el enumerado correspondiente en el metodo
 * oprimirimprimir.
 */
@ManagedBean
@ViewScoped
public class ImprimeContratosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String codigo;

    private List<Registro> listaCmbTipocontrato;
    private RegistroDataModelImpl listaCmbContratoinicial;
    private RegistroDataModelImpl listaCmbContratofinal;
    private RegistroDataModelImpl listaCmbDependencia;
    private String tipoContrato;
    private String contratoInicial;
    private String contratoFinal;
    private String dependencia;
    private boolean verificacion = false;
    private boolean visibleDependencia;
    private RegistroDataModelImpl listaplantilla;
    private String modeloPlantilla;
    private String nombreDependencia;
    private String fechaFormato;
    private String nombreTipoContrato;

    public ImprimeContratosControlador() {
        compania = SessionUtil.getCompania();
        codigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.IMPRIME_CONTRATOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(ImprimeContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbTipocontrato();
        cargarListacmbDependencia();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        // METODO_NO_IMPLEMENTADO
    }

    public void renderizar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListacmbTipocontrato() {
        // 73046
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCmbTipocontrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImprimeContratosControladorUrlEnum.URL6044
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbContratoinicial() {
        // 82052,82054
        String urlContrato = verificacion && (dependencia != null)
            ? ImprimeContratosControladorUrlEnum.URL6763.getValue()
            : ImprimeContratosControladorUrlEnum.URL6762.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlContrato);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ImprimeContratosControladorEnum.TIPOCONTRATO.getValue(),
                        tipoContrato);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);
        listaCmbContratoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    public void cargarListacmbContratofinal() {
        // 82056
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimeContratosControladorUrlEnum.URL7394
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ImprimeContratosControladorEnum.TIPOCONTRATO.getValue(),
                        tipoContrato);
        param.put(ImprimeContratosControladorEnum.CONTRATOINICIAL.getValue(),
                        contratoInicial);
        listaCmbContratofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    public void cargarListacmbDependencia() {
        // 62030
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimeContratosControladorUrlEnum.URL8061
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaplantilla(String tipoFormato) {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimeContratosControladorUrlEnum.URL8554
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ImprimeContratosControladorEnum.TIPOFORMATO.getValue(),
                        tipoFormato.replace(" ", ""));

        listaplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
        listaplantilla.load();
        if (listaplantilla.getDatasource().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2057"));
        }
    }

    public void oprimirimprimir() {
        // <CODIGO_DESARROLLADO>
        listaplantilla.load();
        if (listaplantilla.getDatasource().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2057"));
        }
        else {
            String strNombreDocumento = SysmanFunciones.concatenar(
                            nombreTipoContrato, " ", " No. ", contratoInicial,
                            " - ", contratoFinal);
            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = modeloPlantilla;
            valores[1] = fechaFormato;
            valores[2] = strNombreDocumento;

            Map<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s",
                            SysmanFunciones.colocarComillas(compania));
            variablesConsultaW.put("s$claseOrden$s", tipoContrato);
            variablesConsultaW.put("s$contratoInicial$s", contratoInicial);
            variablesConsultaW.put("s$contratoFinal$s", contratoFinal);
            variablesConsultaW.put("s$condDependencia$s",
                            verificacion ? SysmanFunciones.concatenar(
                                            "  AND ORDENDECOMPRA.DEPENDENCIA = '",
                                            dependencia, "'")
                                : "");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos, valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbContratoinicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbContratoinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contratoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()).toString();
        cargarListacmbContratofinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbContratofinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbContratofinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contratoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()).toString();
    }

    public void seleccionarFilaplantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        modeloPlantilla = registroAux.getCampos().get(codigo).toString();
        try {
            fechaFormato = SysmanFunciones
                            .formatearFecha(SysmanFunciones.convertirAFecha(
                                            (String) registroAux.getCampos()
                                                            .get("FECHA"),
                                            "dd/MM/yyyy"));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarcmbTipocontrato() {
        // <CODIGO_DESARROLLADO>
        cargarListacmbContratoinicial();
        contratoFinal = null;
        contratoInicial = null;
        modeloPlantilla = null;
        String tipoFormato = service.buscarEnLista(tipoContrato, codigo,
                        "TIPOFORMATO", listaCmbTipocontrato);
        nombreTipoContrato = service.buscarEnLista(tipoContrato, codigo,
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaCmbTipocontrato);
        cargarListaplantilla(tipoFormato);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCondicion() {
        // <CODIGO_DESARROLLADO>
        visibleDependencia = verificacion;
        contratoInicial = null;
        contratoFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbContratoinicial() {
        cargarListacmbContratofinal();
        contratoFinal = null;
    }

    public void seleccionarFilacmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        cargarListacmbContratoinicial();
        contratoFinal = null;
        contratoInicial = null;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void iniciarListasSubNulo() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void iniciarListasSub() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void iniciarListas() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public boolean isVerificacion() {
        return verificacion;
    }

    public void setVerificacion(boolean verificacion) {
        this.verificacion = verificacion;
    }

    public boolean isVisibleDependencia() {
        return visibleDependencia;
    }

    public void setVisibleDependencia(boolean visibleDependencia) {
        this.visibleDependencia = visibleDependencia;
    }

    public RegistroDataModelImpl getListaplantilla() {
        return listaplantilla;
    }

    public void setListaplantilla(RegistroDataModelImpl listaplantilla) {
        this.listaplantilla = listaplantilla;
    }

    public String getModeloPlantilla() {
        return modeloPlantilla;
    }

    public void setModeloPlantilla(String modeloPlantilla) {
        this.modeloPlantilla = modeloPlantilla;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public String getFechaFormato() {
        return fechaFormato;
    }

    public void setFechaFormato(String fechaFormato) {
        this.fechaFormato = fechaFormato;
    }

    public List<Registro> getListacmbTipocontrato() {
        return listaCmbTipocontrato;
    }

    public void setListacmbTipocontrato(List<Registro> listacmbTipocontrato) {
        this.listaCmbTipocontrato = listacmbTipocontrato;
    }

    public RegistroDataModelImpl getListacmbContratoinicial() {
        return listaCmbContratoinicial;
    }

    public void setListacmbContratoinicial(
        RegistroDataModelImpl listacmbContratoinicial) {
        this.listaCmbContratoinicial = listacmbContratoinicial;
    }

    public RegistroDataModelImpl getListacmbContratofinal() {
        return listaCmbContratofinal;
    }

    public void setListacmbContratofinal(
        RegistroDataModelImpl listacmbContratofinal) {
        this.listaCmbContratofinal = listacmbContratofinal;
    }

    public RegistroDataModelImpl getListacmbDependencia() {
        return listaCmbDependencia;
    }

    public void setListacmbDependencia(
        RegistroDataModelImpl listacmbDependencia) {
        this.listaCmbDependencia = listacmbDependencia;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getContratoInicial() {
        return contratoInicial;
    }

    public void setContratoInicial(String contratoInicial) {
        this.contratoInicial = contratoInicial;
    }

    public String getContratoFinal() {
        return contratoFinal;
    }

    public void setContratoFinal(String contratoFinal) {
        this.contratoFinal = contratoFinal;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }
}
