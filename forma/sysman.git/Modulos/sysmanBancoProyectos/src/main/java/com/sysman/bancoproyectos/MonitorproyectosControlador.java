package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.MonitorproyectosControladorEnum;
import com.sysman.bancoproyectos.enums.MonitorproyectosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 22/08/2015
 * 
 * @version 2, 26/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos y en el origen de grilla.
 * 
 * @version 3, 16/03/2018
 * @author jhernandez se agregó la columna valor disminuido y su total
 * al pie de página.
 */

@ManagedBean
@ViewScoped
public class MonitorproyectosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;
    private final String cCodigo;
    private final String cTodas;
    private String dependencia;
    private String vigencia;
    private String estado;
    private String proyecto;
    private String titulo;
    private String valorTotal;
    private String valorProgramado;
    private String valor;
    private String valorSolicitado;
    private String idDependencia;
    private String total;
    private String ejecutado;
    private String disminuido;
    private String solicitado;
    private String programado;
    private RegistroDataModelImpl listaCmbDependencia;
    private RegistroDataModelImpl listacmbDependenciaE;
    private RegistroDataModelImpl listaProyecto;
    private RegistroDataModelImpl listaProyectoE;
    private String auxiliar;
    private String sSql;
    private List<Registro> listaVIGENCIAFILTRAR;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of MonitorproyectosControlador
     */
    public MonitorproyectosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = "CODIGO";
        cTodas = "TODAS";
        try {
            numFormulario = GeneralCodigoFormaEnum.MONITORPROYECTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                proyecto = (String) parametrosEntrada.get("proyectoMonitor");
                idDependencia = (String) parametrosEntrada
                                .get("idDependenciaMonitor");
                dependencia = (String) parametrosEntrada
                                .get("dependenciaMonitor");
                vigencia = (String) parametrosEntrada.get("vigenciaMonitor");
                estado = (String) parametrosEntrada.get("estadoMonitor");
            }
            else {
                proyecto = "TODOS";
                idDependencia = "T";
                dependencia = cTodas;
                vigencia = cTodas;
                estado = "X";
            }
        }
        catch (SysmanException ex) {
            Logger.getLogger(MonitorproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            SessionUtil.cleanFlash();
        }

    }

    @PostConstruct
    public void inicializar() {
        tabla = MonitorproyectosControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaVIGENCIAFILTRAR();
        cargarListacmbDependencia();
        cargarListacmbDependenciaE();
        cargarListaProyecto();
        cargarListaProyectoE();
        calcularTotales();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        MonitorproyectosControladorUrlEnum.URL15084.getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.PROYECTO.getName(),
                        proyecto);
        parametrosListado.put(MonitorproyectosControladorEnum.PARAM2.getValue(),
                        idDependencia);
        parametrosListado.put(MonitorproyectosControladorEnum.PARAM0.getValue(),
                        vigencia);
        parametrosListado.put(MonitorproyectosControladorEnum.PARAM1.getValue(),
                        vigencia);
        parametrosListado.put(GeneralParameterEnum.ESTADO.getName(), estado);
    }

    private void reiniciarTotales() {
        total = "0";
        programado = "0";
        solicitado = "0";
        ejecutado = "0";
        disminuido = "0";
    }

    public void oprimirbtnVer(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put("rid", reg.getLlave());
        param.put("proyectoMonitor", proyecto);
        param.put("dependenciaMonitor", dependencia);
        param.put("vigenciaMonitor", vigencia);
        param.put("estadoMonitor", estado);
        param.put("idDependenciaMonitor", idDependencia);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);

        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    private void calcularTotales() {
        try {
            reiniciarTotales();
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
            param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            idDependencia);
            param.put(MonitorproyectosControladorEnum.PARAM0.getValue(),
                            vigencia);
            param.put(MonitorproyectosControladorEnum.PARAM1.getValue(),
                            vigencia);
            param.put(GeneralParameterEnum.ESTADO.getName(), estado);

            List<Registro> listaTotales = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MonitorproyectosControladorUrlEnum.URL10369
                                                                            .getValue())
                                            .getUrl(), param));

            total = (listaTotales.get(0).getCampos().get("VALOR_TOTAL"))
                            .toString();
            programado = (listaTotales.get(0).getCampos()
                            .get("VALOR_PROGRAMADO"))
                                            .toString();
            solicitado = (listaTotales.get(0).getCampos()
                            .get("VALOR_SOLICITADO"))
                                            .toString();
            ejecutado = (listaTotales.get(0).getCampos().get("VALOR_EJECUTADO"))
                            .toString();
            disminuido = (listaTotales.get(0).getCampos()
                            .get("VALOR_DISMINUIDO"))
                                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaVIGENCIAFILTRAR() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaVIGENCIAFILTRAR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MonitorproyectosControladorUrlEnum.URL10977
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorproyectosControladorUrlEnum.URL11690
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "COD");
    }

    public void cargarListacmbDependenciaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorproyectosControladorUrlEnum.URL11690
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbDependenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "COD");
    }

    public void cargarListaProyecto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorproyectosControladorUrlEnum.URL13792
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaProyectoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorproyectosControladorUrlEnum.URL13792
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyectoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void oprimirVerInforme() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEnviarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Permite la validacion de campos nulos en el formulario
     * 
     * @return verdadero si existe algun campo en blanco
     */
    private boolean validarCondicionesReporte() {
        return SysmanFunciones.validarVariableVacio(proyecto) ||
            SysmanFunciones.validarVariableVacio(dependencia) ||
            SysmanFunciones.validarVariableVacio(vigencia) ||
            SysmanFunciones.validarVariableVacio(estado);
    }

    private void generarReporte(FORMATOS formato) {
        try {
            if (validarCondicionesReporte()) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2451"));
                return;
            }
            Map<String, Object> parametros = new HashMap<>();

            String nombreProyecto = "000172RPTMonitorProyectos";
            parametros.put("PR_ESTADO", SysmanFunciones.concatenar("Estado: ",
                            getEstadoProyecto()));
            parametros.put("PR_VIGENCIA", SysmanFunciones
                            .concatenar("Vigencia: ", this.vigencia));
            parametros.put("PR_DEPENDENCIA",
                            SysmanFunciones.concatenar("Dependencia: ",
                                            this.dependencia));
            parametros.put("PR_PROYECTO", SysmanFunciones
                            .concatenar("Proyecto: ", this.proyecto));
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("proyecto", proyecto);
            reemplazar.put("dependencia", idDependencia);
            reemplazar.put("vigencia", vigencia);
            reemplazar.put("estadoActual", estado);
            Reporteador.resuelveConsulta(nombreProyecto,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(nombreProyecto,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(MonitorproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            "MSM_TRANS_INTERRUMPIDA"),
                                            ex.getMessage()));
            Logger.getLogger(MonitorproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void cambiarVIGENCIAFILTRAR() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        calcularTotales();

        // </CODIGO_DESARROLLADO>
    }

    private String getEstadoProyecto() {
        String estadoFinal = "";
        switch (this.estado) {
        case "X":
            estadoFinal = idioma.getString("TI_TODOS");
            break;
        case "E":
            estadoFinal = idioma.getString("OD_CB454_0");
            break;
        case "S":
            estadoFinal = idioma.getString("OD_CB454_1");
            break;
        case "P":
            estadoFinal = idioma.getString("OD_CB454_2");
            break;
        case "T":
            estadoFinal = idioma.getString("TG_EJECUTADO");
            break;
        default:
            break;
        }
        return estadoFinal;
    }

    public void cambiarESTADOFILTRAR() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        calcularTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = registroAux.getCampos().get("NOM").toString();
        idDependencia = registroAux.getCampos().get("COD").toString();
        reasignarOrigen();
        calcularTotales();

    }

    public String getIdDependencia() {
        return idDependencia;
    }

    public void setIdDependencia(String idDependencia) {
        this.idDependencia = idDependencia;
    }

    public void seleccionarFilaProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyecto = registroAux.getCampos().get(cCodigo).toString();
        reasignarOrigen();
        calcularTotales();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public String getsSql() {
        return sSql;
    }

    public void setsSql(String sSql) {
        this.sSql = sSql;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEjecutado() {
        return ejecutado;
    }

    public void setEjecutado(String ejecutado) {
        this.ejecutado = ejecutado;
    }

    public String getSolicitado() {
        return solicitado;
    }

    public void setSolicitado(String solicitado) {
        this.solicitado = solicitado;
    }

    public String getProgramado() {
        return programado;
    }

    public void setProgramado(String programado) {
        this.programado = programado;
    }

    public RegistroDataModelImpl getListaCmbDependencia() {
        return listaCmbDependencia;
    }

    public void setListaCmbDependencia(
        RegistroDataModelImpl listaCmbDependencia) {
        this.listaCmbDependencia = listaCmbDependencia;
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo generado por herencia
    }

    @Override
    public void removerCombos() {
        // Metodo generado por herencia
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("1");
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaVIGENCIAFILTRAR() {
        return listaVIGENCIAFILTRAR;
    }

    public void setListaVIGENCIAFILTRAR(List<Registro> listaVIGENCIAFILTRAR) {
        this.listaVIGENCIAFILTRAR = listaVIGENCIAFILTRAR;
    }

    public RegistroDataModelImpl getListaCmbDependenciaE() {
        return listacmbDependenciaE;
    }

    public void setListacmbDependenciaE(
        RegistroDataModelImpl listacmbDependenciaE) {
        this.listacmbDependenciaE = listacmbDependenciaE;
    }

    public RegistroDataModelImpl getListaProyecto() {
        return listaProyecto;
    }

    public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    public RegistroDataModelImpl getListaProyectoE() {
        return listaProyectoE;
    }

    public void setListaProyectoE(RegistroDataModelImpl listaProyectoE) {
        this.listaProyectoE = listaProyectoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getValorProgramado() {
        return valorProgramado;
    }

    public void setValorProgramado(String valorProgramado) {
        this.valorProgramado = valorProgramado;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValorSolicitado() {
        return valorSolicitado;
    }

    public void setValorSolicitado(String valorSolicitado) {
        this.valorSolicitado = valorSolicitado;
    }

    /**
     * @return the disminuido
     */
    public String getDisminuido() {
        return disminuido;
    }

    /**
     * @param disminuido
     * the disminuido to set
     */
    public void setDisminuido(String disminuido) {
        this.disminuido = disminuido;
    }

}
