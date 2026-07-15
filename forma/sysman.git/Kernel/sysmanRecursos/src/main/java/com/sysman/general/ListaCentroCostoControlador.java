package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.ListaCentroCostoControladorEnum;
import com.sysman.general.enums.ListaCentroCostoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

import java.io.IOException;
import java.util.HashMap;
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
 * @author otorres
 * @version 1, 15/03/2016
 * @version 2, 05/04/2017 jcrodriguez Se adicionaron los servicios
 * para la lista de año,para el combo gran inicial y final Depuracion
 * del controaldor
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class ListaCentroCostoControlador extends BeanBaseModal {
    /**
     * variable que alamcena la compañia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * vairable que alamcena el costo inicial
     */
    private String costoInicial;
    /**
     * variable que alamcena el costo final
     */
    private String costoFinal;
    /**
     * variable que alamcena el nombre inicial
     */
    private String nombreInicial;
    /**
     * variable que almacena el nombre final
     */
    private String nombreFinal;
    /**
     * vairiable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que almacen aun listado de cosigos inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * variable que almacen aun listado de cosigos final
     */
    private RegistroDataModelImpl listaCodigoFinal;

    /**
     * Creates a new instance of ListaCentroCostoControlador
     */
    public ListaCentroCostoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTA_CENTRO_COSTO_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            costoInicial = "0";
            costoFinal = SysmanConstantes.CONS_SUCURSAL;
        }
        catch (Exception ex) {
            Logger.getLogger(ListaCentroCostoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que inicializa el formulario
     */
    @PostConstruct
    public void inicializar() {
        cargarListaCodigoInicial();

        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    /**
     * metodo que carga el listaodo de codigo inicial mostrando codigo
     * y nombre
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListaCentroCostoControladorUrlEnum.URL2600
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo que carga el listaodo de codigo final mostrando codigo y
     * nombre
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListaCentroCostoControladorUrlEnum.URL3204
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ListaCentroCostoControladorEnum.COSTOINICIAL.getValue(),
                        costoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que se llama cuando se oprime un boton presentar
     * 
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama cuando se oprime un boton excel
     * 
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    /**
     * metodo que genera el informe en formato pdf y excel
     * 
     * @param formato
     */
    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("codigoInicial", costoInicial);
            reemplazar.put("codigoFinal", costoFinal);
            Reporteador.resuelveConsulta(
                            ListaCentroCostoControladorEnum.NOMBREINFORME
                                            .getValue(),
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            ListaCentroCostoControladorEnum.NOMBREINFORME
                                            .getValue(),
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que selecciona un codigo inicial del combobox costo
     * inicial
     * 
     * @param event
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        costoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        nombreFinal = null;
        costoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * metodo que selecciona un codigo final del combobox costo final
     * 
     * @param event
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        costoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public String getCostoInicial() {
        return costoInicial;
    }

    public void setCostoInicial(String costoInicial) {
        this.costoInicial = costoInicial;
    }

    public String getCostoFinal() {
        return costoFinal;
    }

    public void setCostoFinal(String costoFinal) {
        this.costoFinal = costoFinal;
    }

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
}
