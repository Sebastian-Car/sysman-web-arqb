package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ListadoBancosControladorEnum;
import com.sysman.contabilidad.enums.ListadoBancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author otorres
 * @version 1, 19/04/2016
 * @version 2, 12/04/2017 modificado por jcrodriguez descripcion:
 * --depuracion del controlador --adicion de servicios para el
 * formulario
 * @version 3, 21/04/2017--mzanguna, cambio Ejb.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y
 * actualizaci�n de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class ListadoBancosControlador extends BeanBaseModal {
    private final String compania;
    /**
     * variable que alamcen el modulo9
     */
    private final String modulo;
    /**
     * variable que alamcena el tipo inicial
     */
    private String tipoInicial;
    /**
     * variable que alamcena el tipo final
     */
    private String tipoFinal;
    /**
     * variable que alamcena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * variable que alamcena la fecha final
     */
    private Date fechaFinal;
    /**
     * variable que almacena el reporte a descargar
     */
    private StreamedContent archivoDescarga;
    /**
     * lista el tipo inicial
     */
    private RegistroDataModelImpl listaTipoInicial;
    /**
     * lista el tipo final
     */
    private RegistroDataModelImpl listaTipoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ListadoBancosControlador
     */
    public ListadoBancosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_BANCOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ListadoBancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que se llama al inicial el formulario
     */
    @PostConstruct
    public void inicializar() {
        fechaInicial = new Date();
        fechaFinal = fechaInicial;
        cargarListaTipoInicial();
        abrirFormulario();
    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    /**
     * metodo para cargar la lista tipo inicial del combo grande
     */
    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoBancosControladorUrlEnum.URL3500
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo para cargar la lista tipo final del combo grande
     */
    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoBancosControladorUrlEnum.URL4468
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ListadoBancosControladorEnum.TIPOINICIAL.getValue()
                        .toUpperCase(), tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que actual cuando se oprime el boton pdf
     *
     * @param ac
     */
    public void oprimirImprimir() {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
    }

    /**
     * metodo que se llama cuanso se oprime el boton excel
     *
     * @param ac
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL);
    }

    /**
     * metodo que contiene la logica para crear un reporte en formato
     * excel o pdf
     *
     * @param formato
     */
    public void generaInforme(ReportesBean.FORMATOS formato) {
        if (fechaInicial.before(fechaFinal)
            || fechaInicial.equals(fechaFinal)) {

            try {
                Map<String, Object> parametros = new HashMap<>();
                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(GeneralParameterEnum.COMPANIA.getName()
                                .toLowerCase(), compania);
                reemplazar.put(ListadoBancosControladorEnum.TIPOINICIAL
                                .getValue(), tipoInicial);
                reemplazar.put(ListadoBancosControladorEnum.TIPOFINAL
                                .getValue(), tipoFinal);
                reemplazar.put(ListadoBancosControladorEnum.FECHAINICIAL
                                .getValue(),
                                SysmanFunciones.formatearFecha(fechaInicial));
                reemplazar.put(ListadoBancosControladorEnum.FECHAFINAL
                                .getValue(),
                                SysmanFunciones.formatearFecha(fechaFinal));
                reemplazar.put(ListadoBancosControladorEnum.ANIO.getValue()
                                .toLowerCase(),
                                SysmanFunciones.ano(fechaInicial));
                parametros.put(ListadoBancosControladorEnum.PR_RESPONSABLE_AREA
                                .getValue(),
                                ejbSysmanUtil.consultarParametro(compania,
                                                ListadoBancosControladorEnum.NOMBRE_RESPONSABLE_DE_AREA
                                                                .getValue(),
                                                modulo, new Date(),
                                                true));

                parametros.put(ListadoBancosControladorEnum.PR_DESCRIPCION_FECHAS
                                .getValue(),
                                idioma.getString(
                                                ListadoBancosControladorEnum.IDIOMA1
                                                                .getValue())
                                    + " "
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial)
                                    + " Y "
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal));
                Reporteador.resuelveConsulta(
                                ListadoBancosControladorEnum.NOMBREINFORME
                                                .getValue(),
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                ListadoBancosControladorEnum.NOMBREINFORME
                                                .getValue(),
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            ListadoBancosControladorEnum.IDIOMA2.getValue()));
        }
    }

    /**
     * metodo que valida el casteo a toString
     *
     * @param campos
     * @param var
     * @return
     */
    private String cadenaVacia(Registro campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos.getCampos(), var) ? null
            : campos.getCampos().get(var).toString();
    }

    /**
     * metodo que se llama cuando se selcciona un registro de un combo
     * grande
     *
     * @param event
     */
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = cadenaVacia(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        tipoFinal = null;
        cargarListaTipoFinal();
    }

    /**
     * metodo que se llama cuando se selcciona un registro de un combo
     * grande
     *
     * @param event
     */
    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = cadenaVacia(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodos get y set
     *
     * @return
     */
    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }
}
