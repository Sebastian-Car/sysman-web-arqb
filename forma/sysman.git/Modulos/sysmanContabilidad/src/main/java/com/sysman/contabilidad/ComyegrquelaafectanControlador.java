package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ComyegrquelaafectanControladorEnum;
import com.sysman.contabilidad.enums.ComyegrquelaafectanControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 22/04/2016
 * @version 2, 11/04/2017 modificado por jcrodriguez
 * descripcion:--depuracion del controlador --se adicionaron los
 * servicion para las consultas quemadas en los combos grandes y
 * pequeños
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class ComyegrquelaafectanControlador extends BeanBaseModal {
    /**
     * variable que alamcena la compañia
     */
    private final String compania;
    /**
     * variable que alamcena el modulo9
     */
    private final String modulo;
    /***
     * variable que almacena el año
     */
    private int ano;
    /**
     * variable que almacena el comprobante
     */
    private String comprobante;
    /**
     * variable que almacena el tipo
     */
    private String tipo;
    /**
     * variable que almacena el nombre tipo
     */
    private String nombreTipo;
    /**
     * variable que almacena el archivo de descargar
     */
    private StreamedContent archivoDescarga;
    /**
     * lista los años
     */
    private List<Registro> listaano;
    /**
     * combo grande que lista los comprobantes
     */
    private RegistroDataModelImpl listaComprobante;
    /**
     * combo grande que lista el tipo
     */
    private RegistroDataModelImpl listaTipo;

    /**
     * Creates a new instance of ComyegrquelaafectanControlador
     */
    public ComyegrquelaafectanControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.COMYEGRQUELAAFECTAN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ComyegrquelaafectanControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo que carga la lista y abre el formulario
     */
    @PostConstruct
    public void init() {
        cargarListaTipo();
        abrirFormulario();
        cargarListaano();
    }

    /**
     * metodo que al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        ano = SysmanFunciones.ano(new Date());
    }

    /**
     * metodo que carga la lista de años
     */
    public void cargarListaano() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComyegrquelaafectanControladorUrlEnum.URL2978
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que carga la lista de comprobantes
     */
    public void cargarListaComprobante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComyegrquelaafectanControladorUrlEnum.URL3361
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ComyegrquelaafectanControladorEnum.TIPOS.getValue(), tipo);
        param.put(ComyegrquelaafectanControladorEnum.ANIO.getValue(), ano);
        listaComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * metodo que carga la lista de tipos
     */
    public void cargarListaTipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComyegrquelaafectanControladorUrlEnum.URL4280
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo que se ejecuta cuando se oprime el boton de excel
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se ejecuta cuando se oprime el boton pdf
     */
    public void oprimirPresentar() {

        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);

    }

    /**
     * metodo que tiene toda la logica para genera los reportes en
     * formato excel y pdf
     *
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {

        String parReporte = ComyegrquelaafectanControladorEnum.NOMBREINFORME
                        .getValue();

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(ComyegrquelaafectanControladorEnum.COMPROBANTE
                        .getValue(), comprobante);
        reemplazar.put(ComyegrquelaafectanControladorEnum.PTIPO.getValue(),
                        "'" + tipo + "'");

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(ComyegrquelaafectanControladorEnum.PR_ENCABEZADO
                        .getValue(), comprobante + " de " + ano);

        Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                        reemplazar, parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo para seleccionar un registro de un combo grande
     *
     * @param event
     */
    public void seleccionarFilaComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comprobante = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()).toString();
    }

    /**
     * metodo para seleccionar un registro de un combo grande
     *
     * @param event
     */
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreTipo = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        comprobante = null;
        cargarListaComprobante();
    }

    /**
     * metodo que se ejecuta al cambiar un año
     */
    public void cambiarano() {

        cargarListaComprobante();
    }

    /**
     * metodo get y set
     *
     * @return
     */
    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaano() {
        return listaano;
    }

    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    public RegistroDataModelImpl getListaComprobante() {
        return listaComprobante;
    }

    public void setListaComprobante(RegistroDataModelImpl listaComprobante) {
        this.listaComprobante = listaComprobante;
    }

    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }
}
