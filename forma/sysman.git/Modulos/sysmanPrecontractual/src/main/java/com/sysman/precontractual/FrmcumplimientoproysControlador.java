package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.precontractual.enums.FrmcumplimientoproysControladorEnum;
import com.sysman.precontractual.enums.FrmcumplimientoproysControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 23/03/2016
 * 
 * @author eamaya
 * @version 2.0, 25/08/2017, Proceso de Refactoring DSS, cambio de
 * numero de formulario por enum, correcciones SonarLint y cambio de
 * redireccionamientos
 * 
 */
@ManagedBean
@ViewScoped

public class FrmcumplimientoproysControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;
    private String etapa;
    private String tipoContrato;
    private String codEstudio;
    private boolean voBo;
    private HashMap<String, Object> rid;
    private String nombreEtapa;
    private String sucursalRecibido;
    private String sucursalEntrega;
    private String codEtapa;
    private boolean insertarFormulario;
    private boolean actualizarFormulario;
    private boolean eliminarFormulario;
    private Map<String, Object> parametrosEntrada;
    private RegistroDataModelImpl listaCmbEtapas;
    private RegistroDataModelImpl listaCmbEtapasE;
    private RegistroDataModelImpl listaDESCRIPCION;
    private RegistroDataModelImpl listaDESCRIPCIONE;
    private RegistroDataModelImpl listaRespRecibido;
    private RegistroDataModelImpl listaRespRecibidoE;
    private RegistroDataModelImpl listaRespEntrega;
    private RegistroDataModelImpl listaRespEntregaE;
    private String auxiliar;
    private StreamedContent archivoDescarga;
    private boolean esCreador;
    private String vigenciaPeriodo;
    /**
     * Creates a new instance of FrmcumplimientoproysControlador
     */
    public FrmcumplimientoproysControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCUMPLIMIENTOPROYS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
                tipoContrato = (String) parametrosEntrada.get("txtTipCont");
                codEstudio = (String) parametrosEntrada.get("codEstudio");
                voBo = Boolean.parseBoolean(
                                (String) parametrosEntrada.get("voBo"));
                esCreador = Boolean.parseBoolean(
                                (String) parametrosEntrada.get("esCreador"));
                vigenciaPeriodo = (String) parametrosEntrada.get("vigenciaPeriodo");
            }
        }
        catch (Exception ex) {
            Logger.getLogger(FrmcumplimientoproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ES_DETA_ESTPR;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaCmbEtapas();
        cargarListaCmbEtapasE();

        cargarListaRespRecibido();
        cargarListaRespRecibidoE();
        cargarListaRespEntrega();
        cargarListaRespEntregaE();
        abrirFormulario();

        esCreador = esCreador || (SessionUtil.getNivelGrupo(modulo) == 9);

    }

    @Override
    public void abrirFormulario() {

        if (!voBo) {
            insertarFormulario = false;
            actualizarFormulario = false;
            eliminarFormulario = false;
        }
        else {

            insertarFormulario = true;
            actualizarFormulario = true;
            eliminarFormulario = true;
        }

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(
                        FrmcumplimientoproysControladorEnum.ETAPA.getValue(),
                        etapa);

        parametrosListado.put(FrmcumplimientoproysControladorEnum.CODIGOESTUDIO
                        .getValue(), codEstudio);

        parametrosListado.put(FrmcumplimientoproysControladorEnum.TIPOCONTRATO
                        .getValue(), tipoContrato);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcumplimientoproysControladorUrlEnum.URL001
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmcumplimientoproysControladorUrlEnum.URL002
                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcumplimientoproysControladorUrlEnum.URL003
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcumplimientoproysControladorUrlEnum.URL004
                                                        .getValue());

    }

    public void cargarListaCmbEtapas() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcumplimientoproysControladorUrlEnum.URL7407
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmcumplimientoproysControladorEnum.TIPOCONTRATO.getValue(),
                        tipoContrato);

        listaCmbEtapas = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmcumplimientoproysControladorEnum.COD_ETAPA
                                        .getValue());
    }

    public void cargarListaCmbEtapasE() {
        listaCmbEtapasE = listaCmbEtapas;

    }

    public void cargarListaDESCRIPCION() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcumplimientoproysControladorUrlEnum.URL8734
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmcumplimientoproysControladorEnum.TIPOCONTRATO.getValue(),
                        tipoContrato);
        param.put(FrmcumplimientoproysControladorEnum.ETAPA.getValue(),
                        etapa);

        listaDESCRIPCION = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmcumplimientoproysControladorEnum.COD_D_ETAPA
                                        .getValue());
    }

    public void cargarListaDESCRIPCIONE() {
        listaDESCRIPCIONE = listaDESCRIPCION;

    }

    public void cargarListaRespRecibido() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcumplimientoproysControladorUrlEnum.URL10589
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaRespRecibido = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmcumplimientoproysControladorEnum.CEDULA.getValue());
    }

    public void cargarListaRespRecibidoE() {
        listaRespRecibidoE = listaRespRecibido;

    }

    public void cargarListaRespEntrega() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcumplimientoproysControladorUrlEnum.URL12285
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaRespEntrega = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmcumplimientoproysControladorEnum.CEDULA.getValue());
    }

    public void cargarListaRespEntregaE() {
        listaRespEntregaE = listaRespEntrega;

    }

    public void oprimirimprimir(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF, reg);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato, Registro reg) {

        try {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codEstudio", "'" + codEstudio + "'");
            reemplazar.put("codEtapa",
                            reg.getCampos().get(
                                            FrmcumplimientoproysControladorEnum.COD_D_ETAPA
                                                            .getValue())
                                            .toString());
            reemplazar.put("etapa", etapa);
            reemplazar.put("codContrato", "'" + tipoContrato + "'");

            Reporteador.resuelveConsulta("000586ESINFOBSER",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("000586ESINFOBSER",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB1766"));
            Logger.getLogger(FrmcumplimientoproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmcumplimientoproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarDESCRIPCIONC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmcumplimientoproysControladorEnum.COD_D_ETAPA
                                        .getValue(), codEtapa);

    }

    public void seleccionarFilaCmbEtapas(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        etapa = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmcumplimientoproysControladorEnum.COD_ETAPA
                                                        .getValue()),
                                        "")
                        .toString();
        nombreEtapa = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        reasignarOrigen();
        cargarListaDESCRIPCION();
        cargarListaDESCRIPCIONE();

    }

    public void seleccionarFilaDESCRIPCION(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DETALLE",
                        registroAux.getCampos()
                                        .get(FrmcumplimientoproysControladorEnum.COD_D_ETAPA
                                                        .getValue()));
        registro.getCampos().put(FrmcumplimientoproysControladorEnum.COD_D_ETAPA
                        .getValue(),
                        registroAux.getCampos()
                                        .get(FrmcumplimientoproysControladorEnum.COD_D_ETAPA
                                                        .getValue()));
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));

    }

    public void seleccionarFilaDESCRIPCIONE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codEtapa = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmcumplimientoproysControladorEnum.COD_D_ETAPA
                                                        .getValue()),
                                        "")
                        .toString();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();

    }

    public void seleccionarFilaRespRecibido(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RESP_RECIBIDO",
                        registroAux.getCampos()
                                        .get(FrmcumplimientoproysControladorEnum.CEDULA
                                                        .getValue()));
        setSucursalRecibido((String) registro.getCampos().put(
                        "SUCURSAL_RECIBIDO",
                        registroAux.getCampos().get("SUCURSAL")));
    }

    public void seleccionarFilaRespRecibidoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmcumplimientoproysControladorEnum.CEDULA
                                                        .getValue()),
                                        "")
                        .toString();
    }

    public void seleccionarFilaRespEntrega(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RESP_ENTREGA",
                        registroAux.getCampos()
                                        .get(FrmcumplimientoproysControladorEnum.CEDULA
                                                        .getValue()));
        setSucursalEntrega((String) registro.getCampos().put("SUCURSAL_ENTREGA",
                        registroAux.getCampos().get("SUCURSAL")));
    }

    public void seleccionarFilaRespEntregaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmcumplimientoproysControladorEnum.CEDULA
                                                        .getValue()),
                                        "")
                        .toString();
    }

    @Override
    public boolean insertarAntes() {

        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("COD_ESTUDIO", codEstudio);
        registro.getCampos().put(FrmcumplimientoproysControladorEnum.COD_ETAPA
                        .getValue(), etapa);
        registro.getCampos().put("COD_T_CONTRATO", tipoContrato);
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        registro.getCampos().remove("NOMBREENTREGA");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

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
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        registro.getCampos().remove("NOMBREENTREGA");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
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

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    public void ejecutarrcCerrar() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ridEstPrevios", rid);
        parametros.put("codEstudio", codEstudio);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);
        
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);

    }

    public RegistroDataModelImpl getListaCmbEtapas() {
        return listaCmbEtapas;
    }

    public void setListaCmbEtapas(RegistroDataModelImpl listaCmbEtapas) {
        this.listaCmbEtapas = listaCmbEtapas;
    }

    public RegistroDataModelImpl getListaCmbEtapasE() {
        return listaCmbEtapasE;
    }

    public void setListaCmbEtapasE(RegistroDataModelImpl listaCmbEtapasE) {
        this.listaCmbEtapasE = listaCmbEtapasE;
    }

    public RegistroDataModelImpl getListaDESCRIPCION() {
        return listaDESCRIPCION;
    }

    public void setListaDESCRIPCION(RegistroDataModelImpl listaDESCRIPCION) {
        this.listaDESCRIPCION = listaDESCRIPCION;
    }

    public RegistroDataModelImpl getListaDESCRIPCIONE() {
        return listaDESCRIPCIONE;
    }

    public void setListaDESCRIPCIONE(RegistroDataModelImpl listaDESCRIPCIONE) {
        this.listaDESCRIPCIONE = listaDESCRIPCIONE;
    }

    public RegistroDataModelImpl getListaRespRecibido() {
        return listaRespRecibido;
    }

    public void setListaRespRecibido(
        RegistroDataModelImpl listaRespRecibido) {
        this.listaRespRecibido = listaRespRecibido;
    }

    public RegistroDataModelImpl getListaRespRecibidoE() {
        return listaRespRecibidoE;
    }

    public void setListaRespRecibidoE(
        RegistroDataModelImpl listaRespRecibidoE) {
        this.listaRespRecibidoE = listaRespRecibidoE;
    }

    public RegistroDataModelImpl getListaRespEntrega() {
        return listaRespEntrega;
    }

    public void setListaRespEntrega(RegistroDataModelImpl listaRespEntrega) {
        this.listaRespEntrega = listaRespEntrega;
    }

    public RegistroDataModelImpl getListaRespEntregaE() {
        return listaRespEntregaE;
    }

    public void setListaRespEntregaE(
        RegistroDataModelImpl listaRespEntregaE) {
        this.listaRespEntregaE = listaRespEntregaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getNombreEtapa() {
        return nombreEtapa;
    }

    public void setNombreEtapa(String nombreEtapa) {
        this.nombreEtapa = nombreEtapa;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isInsertarFormulario() {
        return insertarFormulario;
    }

    public void setInsertarFormulario(boolean insertarFormulario) {
        this.insertarFormulario = insertarFormulario;
    }

    public boolean isActualizarFormulario() {
        return actualizarFormulario;
    }

    public void setActualizarFormulario(boolean actualizarFormulario) {
        this.actualizarFormulario = actualizarFormulario;
    }

    public boolean isEliminarFormulario() {
        return eliminarFormulario;
    }

    public void setEliminarFormulario(boolean eliminarFormulario) {
        this.eliminarFormulario = eliminarFormulario;
    }

    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

    public String getSucursalEntrega() {
        return sucursalEntrega;
    }

    public void setSucursalEntrega(String sucursalEntrega) {
        this.sucursalEntrega = sucursalEntrega;
    }

    public String getSucursalRecibido() {
        return sucursalRecibido;
    }

    public void setSucursalRecibido(String sucursalRecibido) {
        this.sucursalRecibido = sucursalRecibido;
    }

}
