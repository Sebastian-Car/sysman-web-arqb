package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.predial.ejb.EjbPredialUnoRemote;
import com.sysman.predial.enums.SubfrmanulacionsaldoscreditosControladorUrlEnum;
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
 * @author NGOMEZ
 * @version 1, 20/05/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author eamaya
 * @version 3.0, 18/07/2017 , Proceso de refactoring DSS y manejo de
 * EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class SubfrmanulacionsaldoscreditosControlador
                extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    /**
     * Constante definida para almacenar la cadena ""PRECOD"
     */
    private final String precod;
    /**
     * Constante definida para almacenar la cadena "DOCNUM"
     */
    private final String docNum;

    /**
     * Constante definida para almacenar la cadena "PREANO"
     */
    private final String preAno;

    /**
     * Constante definida para almacenar la cadena "PAGADO"
     */
    private final String pagado;

    // <DECLARAR_ATRIBUTOS>
    private String predio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacmbPredio;
    private RegistroDataModelImpl listacmbPredioE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB

    private EjbPredialUnoRemote ejbPredialUno;

    /**
     * Creates a new instance of
     * SubfrmanulacionsaldoscreditosControlador
     */
    public SubfrmanulacionsaldoscreditosControlador() {
        super();
        compania = SessionUtil.getCompania();
        precod = "PRECOD";
        docNum = "DOCNUM";
        preAno = "PREANO";
        pagado = "PAGADO";
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBFRMANULACIONSALDOSCREDITOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SubfrmanulacionsaldoscreditosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_PAGOSDOBLES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbPredio();
        cargarListacmbPredioE();
        // </CARGAR_LISTA_COMBO_GRANDE>
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.PREDIO.getName(), predio);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubfrmanulacionsaldoscreditosControladorUrlEnum.URL1515
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacmbPredio() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubfrmanulacionsaldoscreditosControladorUrlEnum.URL4684
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbPredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, precod);

    }

    public void cargarListacmbPredioE() {

        listacmbPredioE = listacmbPredio;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdRegistrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.PREDIO.getName(), predio);

        try {
            List<Registro> listAxu = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubfrmanulacionsaldoscreditosControladorUrlEnum.URL5749
                                                                            .getValue())
                                            .getUrl(), param));

            for (Registro registroAux : listAxu) {

                ejbPredialUno.anularAbonoSaldoCredito(compania,
                                registroAux.getCampos().get(docNum).toString(),
                                registroAux.getCampos().get(precod).toString(),
                                SessionUtil.getUser().getCodigo(),
                                Integer.parseInt(registroAux.getCampos()
                                                .get(preAno).toString()),
                                (boolean) registroAux.getCampos().get(pagado));

            }
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(SubfrmanulacionsaldoscreditosControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirREGISTRAR(Registro reg, int indice) {

        try {

            ejbPredialUno.anularAbonoSaldoCredito(compania,
                            reg.getCampos().get(docNum).toString(),
                            reg.getCampos().get(precod).toString(),
                            SessionUtil.getUser().getCodigo(),
                            Integer.parseInt(reg.getCampos().get(preAno)
                                            .toString()),
                            (boolean) reg.getCampos().get(pagado));

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(SubfrmanulacionsaldoscreditosControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacmbPredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predio = SysmanFunciones.nvl(registroAux.getCampos().get(precod), "")
                        .toString();
        reasignarOrigen();
    }

    public void seleccionarFilacmbPredioE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(precod), "")
                        .toString();
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

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacmbPredio() {
        return listacmbPredio;
    }

    public void setListacmbPredio(RegistroDataModelImpl listacmbPredio) {
        this.listacmbPredio = listacmbPredio;
    }

    public RegistroDataModelImpl getListacmbPredioE() {
        return listacmbPredioE;
    }

    public void setListacmbPredioE(RegistroDataModelImpl listacmbPredioE) {
        this.listacmbPredioE = listacmbPredioE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
