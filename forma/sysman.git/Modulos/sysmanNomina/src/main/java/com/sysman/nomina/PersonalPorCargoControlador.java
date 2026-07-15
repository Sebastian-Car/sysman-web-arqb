package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.PersonalPorCargoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @author acaceres
 * @version 1, 29/07/2015
 * 
 * @author eamaya
 * @version 2.0, 18/10/2017, Proceso de Refactoring DSS,cambio de
 * numero de formulario por enum y correcciones SonarQub
 * 
 */
@ManagedBean
@ViewScoped
public class PersonalPorCargoControlador extends BeanBaseModal {
    /**
     * variable que almacena la compania
     */
    private final String compania;
    /**
     * variable que almacena la periodo
     */
    private String periodo;
    /**
     * variable que almacena la mes
     */
    private String mes;
    /**
     * variable que almacena la proceso
     */
    private String proceso;
    
    /**
     * variable que almacen la opcion
     */
    private String opcion;
    /**
     * variable que almacena el cargo
     */
    private String cargo;
    /**
     * variable que almacena el nombre del cargo
     */
    private String nombreCargo;
    /**
     * lista los cargos
     */
    private RegistroDataModelImpl listaIdCargo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of PersonalPorCargoControlador
     */
    public PersonalPorCargoControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.PERSONAL_POR_CARGO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            proceso = SessionUtil.getSessionVar("procesoNomina").toString();
            mes = SessionUtil.getSessionVar("mesNomina").toString();
            periodo = SessionUtil.getSessionVar("periodoNomina").toString();
        }
        catch (Exception ex) {
            Logger.getLogger(PersonalPorCargoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenu();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaIdCargo();
        abrirFormulario();
    }

    /**
     * metodo que se llama cuando el formulario abre
     */
    @Override
    public void abrirFormulario() {
        opcion = "1";
    }

    /**
     * metodo que carga una lista de cargos
     */
    public void cargarListaIdCargo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalPorCargoControladorUrlEnum.URL2961
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaIdCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_CARGO");
    }

    /**
     * metodo que se llama cuando se oprime el boton PDF
     * 
     * @param ac
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // <CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama cuando se oprime el boton EXCEL
     * 
     * @param ac
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte(FORMATOS.EXCEL97);
        // <CODIGO_DESARROLLADO>
    }

    /**
     * metodo que genera los reportes en formato pdf y excel
     * 
     * @param formatos
     */
    public void getReporte(FORMATOS formatos) {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        String condicion = "";
        if (SysmanFunciones.validarVariableVacio(cargo) && "1".equals(opcion)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2659"));
            return;
        }

        if ("1".equals(opcion)) {
            condicion = "AND CARGOS.ID_DE_CARGO = " + cargo + " \n";
        }
        try {

            Map<String, Object> parametros = new TreeMap<>();
            Map<String, Object> reemplazar = new TreeMap<>();

            reemplazar.put("condicion", condicion);
            reemplazar.put("proceso", proceso);
            reemplazar.put("mes", mes);
            reemplazar.put("periodo", periodo);

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta("000097PersonalCargo",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000097PersonalCargo",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException ex) {
            Logger.getLogger(PersonalPorCargoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * metodo que se llama cuando un cambiio en la opcion
     */
    public void cambiarOpcion() {
        // <CODIGO_DESARROLLADO>

    }

    /**
     * metodo que se llama cuando se seleciconia un elemento de la
     * lista cargo
     * 
     * @param event
     */
    public void seleccionarFilaIdCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        cargo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_DE_CARGO"), "")
                        .toString();

        nombreCargo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE_DEL_CARGO"),
                                        "")
                        .toString();
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    public RegistroDataModelImpl getListaIdCargo() {
        return listaIdCargo;
    }

    public void setListaIdCargo(RegistroDataModelImpl listaIdCargo) {
        this.listaIdCargo = listaIdCargo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
}
