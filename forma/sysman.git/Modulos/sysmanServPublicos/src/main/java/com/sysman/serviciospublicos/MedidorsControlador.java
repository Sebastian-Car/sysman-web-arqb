package com.sysman.serviciospublicos;

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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.MedidorsControladorEnum;
import com.sysman.serviciospublicos.enums.MedidorsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jlozano
 * @version 1, 03/08/2016
 * 
 * @author eamaya
 * @version 2.0, 09/06/2017 Proceso de Refactoring, Manejo de EJBs y
 * correcciones SonarLint
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 13/06/2017
 */
@ManagedBean
@ViewScoped

public class MedidorsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean numCerVisible;
    private BigDecimal digitos;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaMarca;
    private RegistroDataModelImpl listaMarcaE;
    private String auxiliar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of MedidorsControlador
     */
    public MedidorsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.MEDIDORS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(MedidorsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.SP_MEDIDOR;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaMarca();
        cargarListaMarcaE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidorsControladorUrlEnum.URL0001
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidorsControladorUrlEnum.URL0002
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidorsControladorUrlEnum.URL0003
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidorsControladorUrlEnum.URL0004
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMarca() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidorsControladorUrlEnum.URL6654
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaMarca = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.MARCA.getName());

    }

    public void cargarListaMarcaE() {
        listaMarcaE = listaMarca;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMarcaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        MedidorsControladorEnum.DIGITOS.getValue(),
                        digitos);

    }

    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaMarca(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.MARCA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.MARCA.getName()));
        registro.getCampos().put(MedidorsControladorEnum.DIGITOS.getValue(),
                        registroAux.getCampos()
                                        .get(MedidorsControladorEnum.DIGITOS
                                                        .getValue()));
    }

    public void seleccionarFilaMarcaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.MARCA.getName()).toString();
        digitos = new BigDecimal(
                        registroAux.getCampos()
                                        .get(MedidorsControladorEnum.DIGITOS
                                                        .getValue())
                                        .toString());
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        try {
            String par = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MOSTRAR NUMERO CERTIFICACION",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO");
            if ("SI".equals(par)) {
                numCerVisible = true;
            }
            else {
                numCerVisible = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        try {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.CODIGO
                                                            .getName()));

            Registro r = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            MedidorsControladorUrlEnum.URL7311
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            int serie = Integer.parseInt(r.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString());
            if (serie > 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1035"));
                return false;
            }

            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            "SP_MEDIDOR",
                                            "compania=" + compania + "",
                                            "CONSECUTIVO", "1"));

            registro.getCampos().put(GeneralParameterEnum.CLASE.getName(), 0);
            registro.getCampos().put("LOCALIZACION", 0);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
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
        boolean auxRetorno = true;
        String auxCodigo = (String) SysmanFunciones
                        .nvl(registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "");
        String auxMarca = (String) SysmanFunciones
                        .nvl(registro.getCampos().get(
                                        GeneralParameterEnum.MARCA.getName()),
                                        "");
        String auxDigitos = (String) SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(MedidorsControladorEnum.DIGITOS
                                                        .getValue()),
                                        "");
        if ("".equals(auxDigitos.trim())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1050"));
            auxRetorno = false;
        }
        if ("".equals(auxMarca.trim())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1051"));
            auxRetorno = false;
        }
        if ("".equals(auxCodigo.trim())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1054"));
            auxRetorno = false;
        }

        registro.getCampos().remove("NOMBREESTADO");
        // </CODIGO_DESARROLLADO>
        return auxRetorno;
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
        registro.getCampos().remove("NOMBREESTADO");
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove("");

    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isNumCerVisible() {
        return numCerVisible;
    }

    public void setNumCerVisible(boolean numCerVisible) {
        this.numCerVisible = numCerVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaMarca() {
        return listaMarca;
    }

    public void setListaMarca(RegistroDataModelImpl listaMarca) {
        this.listaMarca = listaMarca;
    }

    public RegistroDataModelImpl getListaMarcaE() {
        return listaMarcaE;
    }

    public void setListaMarcaE(RegistroDataModelImpl listaMarcaE) {
        this.listaMarcaE = listaMarcaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
