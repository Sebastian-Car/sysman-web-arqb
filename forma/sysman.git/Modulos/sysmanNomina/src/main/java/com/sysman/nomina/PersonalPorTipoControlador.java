package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.PersonalPorTipoControladorEnum;
import com.sysman.nomina.enums.PersonalPorTipoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 28/07/2015
 *
 * @author lcortes
 * @version 2, 18,23/10/2017. Refactorizacion de codigo y revision de
 * observaciones de la herramienta SonarLint.
 */
@ManagedBean
@ViewScoped
public class PersonalPorTipoControlador extends BeanBaseModal {
    /**
     * variable que almacena la compania
     */
    private final String compania;
    /**
     * variable que alamcena la opcion
     */
    private String opcion;
    /**
     * variable que almacena de la lista tipo lo seleccionado
     */
    private String combo;
    /**
     * variable que almacena el nombre del tipo
     */
    private String nombreTipo;
    /**
     * lista el combo tipo
     */
    private RegistroDataModelImpl listaComboTipo;
    /**
     * variable auxiliar que almacena el registro del combobox tipo
     */
    private Registro registroAux;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * variable estatica que alamcena el id de tipo
     */
    private static final String ID_DE_TIPO = "ID_DE_TIPO";

    /**
     * Creates a new instance of PersonalPorTipoControlador
     */
    public PersonalPorTipoControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.PERSONAL_POR_TIPO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PersonalPorTipoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        cargarListaComboTipo();
        abrirFormulario();
    }

    /**
     * metodo que carga la lista del combobox tipo
     */
    public void cargarListaComboTipo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalPorTipoControladorUrlEnum.URL3153
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaComboTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ID_DE_TIPO);
    }

    /**
     * metodo llamado cuando se presiona el boton pdf
     *
     * @param ac
     */
    public void oprimirPresentar(ActionEvent ac) {
        generarInforme(ReportesBean.FORMATOS.PDF);

    }

    /**
     * metodo llamado cuando se presiona el boton de excel
     *
     * @param ac
     */
    public void oprimirExcel(ActionEvent ac) {

        generarInforme(ReportesBean.FORMATOS.EXCEL97);

    }

    /**
     * metodo que genera el reporte en formato pdf y excel
     *
     * @param formato
     */
    private void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strWhere = "";
            if (("1").equals(opcion)) {
                strWhere = SysmanFunciones.concatenar(
                                " AND PERSONAL.ID_DE_TIPO  IN ('",
                                registroAux.getCampos().get(ID_DE_TIPO)
                                                .toString(),
                                "') ");
            }
            String nombreCompania = SessionUtil.getCompaniaIngreso()
                            .getNombre();
            parametros.put("PR_NOMBREEMPRESA", nombreCompania);
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("strWhere", strWhere);
            Reporteador.resuelveConsulta("000112PersonalTipo",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("000112PersonalTipo",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que al cambio la opcion
     */
    public void cambiarOpcion() {
        // heredado del bean base
    }

    /**
     * metodo que se llama cuando se selecciona la informacion del
     * combobox tipo
     *
     * @param event
     */
    public void seleccionarFilaComboTipo(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        combo = registroAux.getCampos().get(ID_DE_TIPO).toString();
        nombreTipo = registroAux.getCampos().get(
                        PersonalPorTipoControladorEnum.NOMBRE_TIPO.getValue())
                        .toString();
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getCombo() {
        return combo;
    }

    public void setCombo(String combo) {
        this.combo = combo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public RegistroDataModelImpl getListaComboTipo() {
        return listaComboTipo;
    }

    public void setListaComboTipo(RegistroDataModelImpl listaComboTipo) {
        this.listaComboTipo = listaComboTipo;
    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        opcion = "2";
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
