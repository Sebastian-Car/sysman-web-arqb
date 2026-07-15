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
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.FrmexintduplicidadControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
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
 * @author NGOMEZ
 * @version 1, 27/05/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author eamaya
 * @version 3.0, 30/06/2017, Proceso de Refactoring y Manejo de EJBs,
 * se creo el procedimiento PCK_PREDIAL_COM8.PR_EXINTXDUPLICIDADCAT
 * 
 */
@ManagedBean
@ViewScoped

public class FrmexintduplicidadControlador extends BeanBaseModal {
    private final String compania;
    private final String nOrden;
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    private String predio;
    private String anioInicial;
    private String anioFinal;
    private String noResolucion;
    private Date fecResolucion;
    private String elabResolucion;
    private String firmaResolucion;
    private boolean tipoExencion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaFecInicialProceso;
    private List<Registro> listaFecFinalProceso;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTxtPredio;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    /**
     * Creates a new instance of FrmexintduplicidadControlador
     */
    public FrmexintduplicidadControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        fecResolucion = new Date();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMEXINTDUPLICIDAD_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmexintduplicidadControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaFecInicialProceso();
        cargarListaFecFinalProceso();
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
                                        FrmexintduplicidadControladorUrlEnum.URL5858
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        nOrden);

        listaTxtPredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");

    }

    public void cargarListaFecInicialProceso() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.PREDIO.getName(),
                            predio);

            listaFecInicialProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmexintduplicidadControladorUrlEnum.URL5878
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFecFinalProceso() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.PREDIO.getName(),
                            predio);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anioInicial);

            listaFecFinalProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmexintduplicidadControladorUrlEnum.URL4662
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdAceptar() {
        tipoExencion = true;

        llamarFuncion();

    }

    public void oprimirComando17() {
        tipoExencion = false;

        llamarFuncion();

    }

    public void llamarFuncion() {
        try {
            ejbPredialOcho.exencionDeInteresPorDuplicidad(compania, nOrden,
                            predio,
                            Integer.parseInt(anioInicial),
                            Integer.parseInt(anioFinal), noResolucion,
                            elabResolucion,
                            firmaResolucion,
                            SysmanFunciones.convertirAFechaCadena(
                                            fecResolucion),
                            tipoExencion, usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFecInicialProceso() {
        // <CODIGO_DESARROLLADO>
        anioFinal = null;
        cargarListaFecFinalProceso();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTxtPredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predio = registroAux.getCampos().get("CODIGO").toString();
        anioInicial = null;
        anioFinal = null;
        cargarListaFecInicialProceso();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    public String getAnioInicial() {
        return anioInicial;
    }

    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    public String getAnioFinal() {
        return anioFinal;
    }

    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    public String getNoResolucion() {
        return noResolucion;
    }

    public void setNoResolucion(String noResolucion) {
        this.noResolucion = noResolucion;
    }

    public Date getFecResolucion() {
        return fecResolucion;
    }

    public void setFecResolucion(Date fecResolucion) {
        this.fecResolucion = fecResolucion;
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    public List<Registro> getListaFecInicialProceso() {
        return listaFecInicialProceso;
    }

    public void setListaFecInicialProceso(
        List<Registro> listaFecInicialProceso) {
        this.listaFecInicialProceso = listaFecInicialProceso;
    }

    public List<Registro> getListaFecFinalProceso() {
        return listaFecFinalProceso;
    }

    public void setListaFecFinalProceso(List<Registro> listaFecFinalProceso) {
        this.listaFecFinalProceso = listaFecFinalProceso;
    }

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
