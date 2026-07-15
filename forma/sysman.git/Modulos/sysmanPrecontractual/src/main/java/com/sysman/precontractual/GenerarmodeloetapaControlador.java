package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.GenerarmodeloetapaControladorEnum;
import com.sysman.precontractual.enums.GenerarmodeloetapaControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 17/03/2016
 *
 * @author lcortes
 * @version 2, 30,31/08/2017. Se realiza refactorizacion de codigo
 * para usar dss y revision de observaciones de la herramienta
 * SonarLint.
 *
 * @modified lcortes 17/10/2017. Se agrega el filtro tipo contrato
 * para la consulta de la lista de modelos de la etapa.
 *
 * @modified lcortes 26/10/2017. Se cambia el origen de control del
 * combo Consecutivo.
 *
 * @modified lcortes 07/11/2017. Se implementa el uso del metodo
 * toString de la clase SysmanFunciones en el metodo consultarModelo.
 */
@ManagedBean
@ViewScoped
public class GenerarmodeloetapaControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String cNombre;
    private final String cProponente;
    private final String cTipoContrato;
    private final String cEstadoVigencia;
    private final String cEstadoProceso;
    private final String cDesdeMonitor;
    private final String cCondicion;
    private List<Registro> listatipoContrato;
    private RegistroDataModelImpl listaproponenteini;
    private RegistroDataModelImpl listaproponentefin;
    private RegistroDataModelImpl listaConsecutivo;
    private RegistroDataModelImpl listaEtapa;
    private RegistroDataModelImpl listadoc;
    private String todosProponentes;
    private String tipoContrato;
    private String consecutivo;
    private String consecutivoProceso;
    private String idEtapa;
    private String etapa;
    private String proponenteIni;
    private String proponenteFin;
    private String documentoGen;
    private String nombreDoc;
    private HashMap<String, Object> ridT;
    private String anio;
    private String condicion;
    private String fechaConsecutivo;
    private String cpProponenteIni;
    private String cpProponenteFin;
    private String transaccion;
    private String fechaPlantilla;
    private boolean heredaProponente;
    private String estadoVigencia;
    private String estadoProceso;
    private String listaModelos;
    private String desdeMonitor;
    private boolean bloqueado;

    public GenerarmodeloetapaControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cProponente = "PROPONENTE";
        cTipoContrato = "tipoContrato";
        cEstadoProceso = "estadoProceso";
        cEstadoVigencia = "estadoVigencia";
        cDesdeMonitor = "desdeMonitor";
        cCondicion = "condicion";
        bloqueado = false;
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                String desdeTransaccion = (String) parametrosEntrada
                                .get("desdeTransaccion");
                desdeTransaccion = desdeTransaccion == null ? "false"
                    : desdeTransaccion;
                if (("true").equals(desdeTransaccion)) {
                    tipoContrato = (String) parametrosEntrada
                                    .get(cTipoContrato);
                    transaccion = (String) parametrosEntrada
                                    .get("consecutivoTransaccion");
                    consecutivo = (String) parametrosEntrada
                                    .get("consecutivoDetalle");
                    consecutivoProceso = transaccion;
                    ridT = (HashMap<String, Object>) parametrosEntrada
                                    .get("rid");
                    anio = (String) parametrosEntrada.get("anio");
                    condicion = (String) parametrosEntrada.get(cCondicion);
                    estadoVigencia = (String) parametrosEntrada
                                    .get(cEstadoVigencia);
                    estadoProceso = (String) parametrosEntrada
                                    .get(cEstadoProceso);
                    fechaConsecutivo = (String) parametrosEntrada.get("fecha");
                    idEtapa = (String) parametrosEntrada.get("idEtapa");
                    etapa = (String) parametrosEntrada.get("nombreEtapa");
                    desdeMonitor = (String) parametrosEntrada
                                    .get(cDesdeMonitor);
                    bloqueado = true;
                    heredaProponente = false; // Por el momento se
                                              // define dejar no
                                              // visibles las listas
                                              // proponente incial,
                                              // proponente final y el
                                              // check todos los
                                              // proponentes que son
                                              // visibles cuando esta
                                              // variable
                                              // (heredaProponente) es
                                              // true.
                }
                else {
                    bloqueado = false;
                }
            }
            else {
                SessionUtil.redireccionarMenu();
            }
            numFormulario = GeneralCodigoFormaEnum.GENERARMODELOETAPA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(GenerarmodeloetapaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @PostConstruct
    public void init() {
        cargarListatipoContrato();
        cargarListaConsecutivo();
        cargarListaEtapa();
        abrirFormulario();

    }

    @Override
    public void asignarOrigenDatos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListatipoContrato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listatipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarmodeloetapaControladorUrlEnum.URL6552
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaproponenteini() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarmodeloetapaControladorUrlEnum.URL6913
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(GenerarmodeloetapaControladorEnum.TRANSACCION.getValue(),
                        transaccion);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivoProceso);

        listaproponenteini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cProponente);

    }

    public void cargarListaproponentefin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarmodeloetapaControladorUrlEnum.URL8124
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(GenerarmodeloetapaControladorEnum.TRANSACCION.getValue(),
                        transaccion);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivoProceso);
        param.put(GenerarmodeloetapaControladorEnum.CODINI.getValue(),
                        proponenteIni);

        listaproponentefin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cProponente);

    }

    public void cargarListaConsecutivo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarmodeloetapaControladorUrlEnum.URL9309
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(GeneralParameterEnum.ESTADO.getName(), "AC");

        listaConsecutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());
    }

    public void cargarListaEtapa() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarmodeloetapaControladorUrlEnum.URL10244
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(GenerarmodeloetapaControladorEnum.TRANSACCION.getValue(),
                        transaccion);

        listaEtapa = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "IDETAPA");
    }

    public void cargarListadoc() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenerarmodeloetapaControladorUrlEnum.URL12013
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GenerarmodeloetapaControladorEnum.LISTAMODELOS.getValue(),
                        listaModelos);
        param.put(GenerarmodeloetapaControladorEnum.MODULO.getValue(),
                        SessionUtil.getModulo());

        listadoc = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirBGenerar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        try {
            if (SysmanFunciones.validarVariableVacio(tipoContrato)
                || SysmanFunciones.validarVariableVacio(consecutivoProceso)
                || SysmanFunciones.validarVariableVacio(idEtapa)
                || SysmanFunciones.validarVariableVacio(documentoGen)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1887"));
                return;
            }
            String[] campos = { "codigoPlantilla", "nombreDocDescarga",
                                "fechaPlantilla" };
            String[] valores = { documentoGen, nombreDoc, SysmanFunciones
                            .formatearFecha(SysmanFunciones
                                            .convertirAFecha(fechaPlantilla)) };

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s",
                            SysmanFunciones.concatenar("'", compania, "'"));
            variablesConsultaW.put("s$consecutivo$s",
                            SysmanFunciones.concatenar("'", consecutivoProceso,
                                            "'"));
            variablesConsultaW.put("s$consecutivoDetalle$s",
                            SysmanFunciones.concatenar("'", consecutivo,
                                            "'"));
            variablesConsultaW.put("s$anio$s", anio);
            variablesConsultaW.put("s$tipoContrato$s",
                            SysmanFunciones.concatenar("'", tipoContrato, "'"));

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos, valores);
        }
        catch (ParseException e) {
            Logger.getLogger(GenerarmodeloetapaControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    public void oprimirCancelar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridT);
        parametros.put(cTipoContrato, tipoContrato);
        parametros.put("anio", anio);
        parametros.put(cCondicion, condicion);
        parametros.put(cEstadoVigencia, estadoVigencia);
        parametros.put(cEstadoProceso, estadoProceso);
        parametros.put(cDesdeMonitor, desdeMonitor);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.TRANSACCIONS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartipoContrato() {
        // <CODIGO_DESARROLLADO>
        consecutivo = null;
        fechaConsecutivo = null;
        idEtapa = null;
        etapa = null;
        documentoGen = null;
        nombreDoc = null;
        proponenteIni = null;
        cpProponenteIni = null;
        proponenteFin = null;
        cpProponenteFin = null;
        listaEtapa = null;
        listadoc = null;
        heredaProponente = false;
        cargarListaConsecutivo();

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaConsecutivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        consecutivoProceso = ((BigInteger) registroAux.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName()))
                                        .toString();
        fechaConsecutivo = registroAux.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName()).toString();
        idEtapa = "";
        etapa = "";
        documentoGen = "";
        nombreDoc = "";
        proponenteIni = "";
        cpProponenteIni = "";
        proponenteFin = "";
        cpProponenteFin = "";
        heredaProponente = false;
        cargarListaEtapa();

    }

    public void seleccionarFilaEtapa(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idEtapa = ((Integer) registroAux.getCampos().get("IDETAPA"))
                        .toString();
        etapa = registroAux.getCampos()
                        .get(GeneralParameterEnum.DESCRIPCION.getName()) == null
                            ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.DESCRIPCION
                                                            .getName())
                                            .toString();
        String regHereda = String.valueOf(
                        registroAux.getCampos().get("HEREDAPROPONENTE"));
        documentoGen = "";
        nombreDoc = "";
        proponenteIni = "";
        cpProponenteIni = "";
        proponenteFin = "";
        cpProponenteFin = "";
        consultarModelo();
        cargarListadoc();
        if (("true").equals(regHereda)) {
            heredaProponente = true;
            cargarListaproponenteini();
        }
        else {
            heredaProponente = false;
        }

    }

    public void seleccionarFilaproponenteini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proponenteIni = registroAux.getCampos().get(cProponente).toString();
        cpProponenteIni = registroAux.getCampos().get(cNombre).toString();
        proponenteFin = null;
        cargarListaproponentefin();
    }

    public void seleccionarFilaproponentefin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proponenteFin = registroAux.getCampos().get(cProponente).toString();
        cpProponenteFin = registroAux.getCampos().get(cNombre).toString();
    }

    public void seleccionarFiladoc(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        documentoGen = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreDoc = registroAux.getCampos().get(cNombre).toString();
        fechaPlantilla = registroAux.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName()).toString();

    }

    private void consultarModelo() {
        // Lista modelo plantillas asociadas a etapa seleccionada
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GenerarmodeloetapaControladorEnum.TIPO.getValue(),
                        tipoContrato);
        param.put(GenerarmodeloetapaControladorEnum.ETAPA.getValue(), idEtapa);
        try {
            String urlEnumId = GenerarmodeloetapaControladorUrlEnum.URL6914
                            .getValue();
            String url = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
            Registro registro = RegistroConverter
                            .toRegistro(requestManager.get(url, param));
            if (registro != null) {
                Map<String, Object> campos = registro.getCampos();
                if (campos != null) {
                    listaModelos = SysmanFunciones
                                    .toString(campos.get("LISTADO"));
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void abrirFormulario() {
        if (!SysmanFunciones.validarVariableVacio(idEtapa)) {
            consultarModelo();
            cargarListadoc();
        }
        if (heredaProponente) {
            cargarListaproponenteini();
            cargarListaproponentefin();
        }
        // <CODIGO_DESARROLLADO>
        /*
         * FR579-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridT);
        parametros.put(cTipoContrato, tipoContrato);
        parametros.put("anio", anio);
        parametros.put(cCondicion, condicion);
        parametros.put(cEstadoVigencia, estadoVigencia);
        parametros.put(cEstadoProceso, estadoProceso);
        parametros.put(cDesdeMonitor, desdeMonitor);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.TRANSACCIONS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListatipoContrato() {
        return listatipoContrato;
    }

    public void setListatipoContrato(List<Registro> listatipoContrato) {
        this.listatipoContrato = listatipoContrato;
    }

    public RegistroDataModelImpl getListaproponenteini() {
        return listaproponenteini;
    }

    public void setListaproponenteini(
        RegistroDataModelImpl listaproponenteini) {
        this.listaproponenteini = listaproponenteini;
    }

    public RegistroDataModelImpl getListaproponentefin() {
        return listaproponentefin;
    }

    public void setListaproponentefin(
        RegistroDataModelImpl listaproponentefin) {
        this.listaproponentefin = listaproponentefin;
    }

    public RegistroDataModelImpl getListaConsecutivo() {
        return listaConsecutivo;
    }

    public void setListaConsecutivo(RegistroDataModelImpl listaConsecutivo) {
        this.listaConsecutivo = listaConsecutivo;
    }

    public RegistroDataModelImpl getListaEtapa() {
        return listaEtapa;
    }

    public void setListaEtapa(RegistroDataModelImpl listaEtapa) {
        this.listaEtapa = listaEtapa;
    }

    public RegistroDataModelImpl getListadoc() {
        return listadoc;
    }

    public void setListadoc(RegistroDataModelImpl listadoc) {
        this.listadoc = listadoc;
    }

    public String getTodosProponentes() {
        return todosProponentes;
    }

    public void setTodosProponentes(String todosProponentes) {
        this.todosProponentes = todosProponentes;
    }

    public boolean isHeredaProponente() {
        return heredaProponente;
    }

    public void setHeredaProponente(boolean heredaProponente) {
        this.heredaProponente = heredaProponente;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getConsecutivoProceso() {
        return consecutivoProceso;
    }

    public void setConsecutivoProceso(String consecutivoProceso) {
        this.consecutivoProceso = consecutivoProceso;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getProponenteIni() {
        return proponenteIni;
    }

    public void setProponenteIni(String proponenteIni) {
        this.proponenteIni = proponenteIni;
    }

    public String getProponenteFin() {
        return proponenteFin;
    }

    public void setProponenteFin(String proponenteFin) {
        this.proponenteFin = proponenteFin;
    }

    public String getDocumentoGen() {
        return documentoGen;
    }

    public void setDocumentoGen(String documentoGen) {
        this.documentoGen = documentoGen;
    }

    public String getNombreDoc() {
        return nombreDoc;
    }

    public void setNombreDoc(String nombreDoc) {
        this.nombreDoc = nombreDoc;
    }

    public String getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(String idEtapa) {
        this.idEtapa = idEtapa;
    }

    public String getFechaConsecutivo() {
        return fechaConsecutivo;
    }

    public void setFechaConsecutivo(String fechaConsecutivo) {
        this.fechaConsecutivo = fechaConsecutivo;
    }

    public String getCpProponenteIni() {
        return cpProponenteIni;
    }

    public void setCpProponenteIni(String cpProponenteIni) {
        this.cpProponenteIni = cpProponenteIni;
    }

    public String getCpProponenteFin() {
        return cpProponenteFin;
    }

    public void setCpProponenteFin(String cpProponenteFin) {
        this.cpProponenteFin = cpProponenteFin;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

}
