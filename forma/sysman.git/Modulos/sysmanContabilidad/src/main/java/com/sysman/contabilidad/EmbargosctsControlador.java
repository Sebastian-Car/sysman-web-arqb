package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.EmbargosctsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author apineda
 * @version 1, 13/04/2016
 * 
 * @author jlramirez
 * @version 2, 07/04/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de SONARLINT
 * @version 3, 20/04/2017, Manejo de EBJs
 * @author asana
 * @version 4 12/06/2017, Se implementa enum en formulario y se implementa metodo concatenar.
 */
@ManagedBean
@ViewScoped
public class EmbargosctsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String demandado;
    private final String demandante;
    private final String nitdemandan;
    private RegistroDataModelImpl listanitDemandante;
    private RegistroDataModelImpl listanitDemandanteE;
    private RegistroDataModelImpl listanitDemandado;
    private RegistroDataModelImpl listanitDemandadoE;
    private String auxiliar;
    private List<Registro> listajuzgado;

    private String nombreDemandante;
    private String nombreDemandado;
    private String nitDemandante;
    private String nitDemandado;
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;

    /**
     * Creates a new instance of EmbargosctsControlador
     */
    public EmbargosctsControlador() {
        super();
        compania = SessionUtil.getCompania();
        demandado = "DEMANDADO";
        demandante = "DEMANDANTE";
        nitdemandan = "NIT_DEMANDANTE";
        try {
            numFormulario = GeneralCodigoFormaEnum.EMBARGOSCTS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EmbargosctsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.EMBARGOSCT;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListajuzgado();
        cargarListanitDemandante();
        cargarListanitDemandanteE();
        cargarListanitDemandado();
        cargarListanitDemandadoE();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListajuzgado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listajuzgado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EmbargosctsControladorUrlEnum.URL3310
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(EmbargosctsControlador.class.getName())
            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListanitDemandante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosctsControladorUrlEnum.URL4181
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listanitDemandante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListanitDemandanteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosctsControladorUrlEnum.URL4181
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listanitDemandanteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListanitDemandado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosctsControladorUrlEnum.URL4181
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listanitDemandado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListanitDemandadoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosctsControladorUrlEnum.URL4181
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listanitDemandadoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cambiarnitDemandanteC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        .put(nitdemandan, nitDemandante);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        .put(demandante, nombreDemandante);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarnitDemandadoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        .put(nitdemandan, nitDemandado);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        .put(demandado, nombreDemandado);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilanitDemandante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nitdemandan,
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(nitdemandan,
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSAL_DEMANDANTE",
                        registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL
                                        .getName()));
        registro.getCampos().put(demandante, "");
        registro.getCampos().put(demandante,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanitDemandante
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanitDemandanteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT")," ").toString();
        nitDemandante = auxiliar;
        registro.getCampos().put(nitdemandan, nitDemandante);
        registro.getCampos().put("SUCURSAL_DEMANDANTE",
                        registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL
                                        .getName()));
        if ((registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == "")
                        || (registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()) == null)) {
            nombreDemandante = "";
        }
        else {
            nombreDemandante = SysmanFunciones.nvl(registroAux.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName())," ")
                            .toString();

        }
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanitDemandado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanitDemandado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT_DEMANDADO",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSAL_DEMANDADO",
                        registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL
                                        .getName()));
        registro.getCampos().put(demandado, "");
        registro.getCampos().put(demandado,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanitDemandado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanitDemandadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT")," ").toString();
        nitDemandado = auxiliar;
        registro.getCampos().put("NIT_DEMANDADO", nitDemandado);
        registro.getCampos().put("SUCURSAL_DEMANDADO",
                        registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL
                                        .getName()));
        if ((registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == "")
                        || (registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()) == null)) {
            nombreDemandado = "";
        }
        else {
            nombreDemandado = SysmanFunciones.nvl(registroAux.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName())," ")
                            .toString();

        }
    }
    // </METODOS_COMBOS_GRANDES>

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);


        try {
            String [] parametro = {
                                   "COMPANIA = " + compania,
                                   " AND COD_RADICADO = ", 
                                   registro.getCampos().get("COD_RADICADO").toString()
            };

            long consecutivo = sysmanUtil.generarConsecutivoConValorInicial("EMBARGOS_CT", 
                            SysmanFunciones.concatenar(parametro), 
                            "CONSECUTIVO", 
                            "1");
            registro.getCampos().put("CONSECUTIVO", consecutivo);
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex) {
            Logger.getLogger(EmbargosctsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
        return true;
        // </CODIGO_DESARROLLADO>
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

    @Override
    public void removerCombos() {
        // NO SE IMPLEMENTA
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA

    }

    public List<Registro> getListajuzgado() {
        return listajuzgado;
    }

    public void setListajuzgado(List<Registro> listajuzgado) {
        this.listajuzgado = listajuzgado;
    }

    public RegistroDataModelImpl getListanitDemandante() {
        return listanitDemandante;
    }

    public void setListanitDemandante(
        RegistroDataModelImpl listanitDemandante) {
        this.listanitDemandante = listanitDemandante;
    }

    public RegistroDataModelImpl getListanitDemandanteE() {
        return listanitDemandanteE;
    }

    public void setListanitDemandanteE(
        RegistroDataModelImpl listanitDemandanteE) {
        this.listanitDemandanteE = listanitDemandanteE;
    }

    public RegistroDataModelImpl getListanitDemandado() {
        return listanitDemandado;
    }

    public void setListanitDemandado(RegistroDataModelImpl listanitDemandado) {
        this.listanitDemandado = listanitDemandado;
    }

    public RegistroDataModelImpl getListanitDemandadoE() {
        return listanitDemandadoE;
    }

    public void setListanitDemandadoE(
        RegistroDataModelImpl listanitDemandadoE) {
        this.listanitDemandadoE = listanitDemandadoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
}
