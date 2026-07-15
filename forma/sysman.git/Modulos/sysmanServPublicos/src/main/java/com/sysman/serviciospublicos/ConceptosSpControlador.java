package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.serviciospublicos.enums.ConceptosSpControladorEnum;
import com.sysman.serviciospublicos.enums.ConceptosSpControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

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
 * @author jguerrero
 * @version 1, 05/08/2016
 * @version 2, 18/05/2017 modificado por jcrodriguez
 * Descripcion:*Depuracion del controlador *Refactoriong y creacion de
 * Dss
 */
@ManagedBean
@ViewScoped
public class ConceptosSpControlador extends BeanBaseDatosAcmeImpl {
    /**
     * variable que almacena la compañia
     */
    private final String compania;
    /**
     * variable que almacena el nombre del centro de costo
     */
    private String nombreCentroCosto;
    /**
     * variable que almacena el estado
     */
    private boolean financiablesAcumulado;
    /**
     * variable que almacena el estado
     */
    private boolean porcDescuento;
    /**
     * variable que almacena el estado
     */
    private boolean iva;
    /**
     * variable que almacena la lista de servicios
     */
    private List<Registro> listaServicio;
    /**
     * variable que almacena la lista de centro de costos
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Ejb
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public ConceptosSpControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONCEPTOS_SP_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ConceptosSpControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void iniciarListas() {

        cargarListaCentroCosto();
        cargarListaServicio();
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * metodo heredado del bean padre
     */
    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.SP_CONCEPTOS;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * metodo que se llama para cargar la lista de servicios
     */
    public void cargarListaServicio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaServicio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptosSpControladorUrlEnum.URL3645
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama para cargar la lista de centro de costo
     */
    public void cargarListaCentroCosto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosSpControladorUrlEnum.URL4169
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que se llama al cambiar el codigo
     */
    public void cambiarCodigo() {
        // <CODIGO_DESARROLLADO>
        int codigo = Integer.parseInt(registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString());
        if (codigo < 1) {
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            ConceptosSpControladorEnum.TB_TB1071.getValue()));
        }

    }

    /**
     * metodo que se llama al seleccionar un registro de un comobo
     * grande
     * 
     * @param event
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(
                        ConceptosSpControladorEnum.CENTROCOSTO.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(
                        ConceptosSpControladorEnum.NOMBRECENTROCOSTO.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * Obtiene el valor del parametro ingresado por parametro
     * 
     * @param nombre
     * Paramtero que se desea consultar en la Base de Datos
     * @param indMayus
     * Indicador para obtener el resultado en mayuscula o exactamente
     * como se encuentra almacenado
     * @return valor del parametro a consultar
     */
    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            SessionUtil.getModulo(), new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void abrirFormulario() {

        if (ConceptosSpControladorEnum.SI.getValue().equals(SysmanFunciones.nvl(
                        getParametro(idioma
                                        .getString(ConceptosSpControladorEnum.TB_TB3167
                                                        .getValue()),
                                        true),
                        ConceptosSpControladorEnum.NO.getValue()))) {
            financiablesAcumulado = true;
        }
        else {
            financiablesAcumulado = false;
        }

        if (SysmanFunciones
                        .nvl(getParametro(idioma
                                        .getString(ConceptosSpControladorEnum.TB_TB3168
                                                        .getValue()),
                                        true),
                                        ConceptosSpControladorEnum.NO
                                                        .getValue())
                        .equals(ConceptosSpControladorEnum.SI.getValue())) {
            porcDescuento = true;
        }
        else {
            porcDescuento = false;
        }

        if (SysmanFunciones
                        .nvl(getParametro(idioma
                                        .getString(ConceptosSpControladorEnum.TB_TB3169
                                                        .getValue()),
                                        true),
                                        ConceptosSpControladorEnum.NO
                                                        .getValue())
                        .equals(ConceptosSpControladorEnum.SI.getValue())) {
            if (SessionUtil.getNivelUsuario(SessionUtil.getModulo()) != 9) {
                iva = true;
            }
            else {
                iva = false;
            }
        }

    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        valPorDefecto();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
        }
        registro.getCampos().remove(ConceptosSpControladorEnum.NOMBRECENTROCOSTO
                        .getValue());
        removerAuditoria();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo que se llama para colocar valores por defecto en los
     * campos que aparecen en el cuerpo del metodo
     */
    private void valPorDefecto() {

        registro.getCampos().put(ConceptosSpControladorEnum.IVA.getValue(),
                        0.00);
        registro.getCampos().put(ConceptosSpControladorEnum.INTERES.getValue(),
                        0.00);
        registro.getCampos().put(ConceptosSpControladorEnum.VALOR.getValue(),
                        0.00);
        registro.getCampos().put(ConceptosSpControladorEnum.RECARGO.getValue(),
                        0.00);
        registro.getCampos().put(ConceptosSpControladorEnum.RECARGO.getValue(),
                        0.00);
        registro.getCampos().put(
                        ConceptosSpControladorEnum.PORC_DESCUENTO.getValue(),
                        0.000);

    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(ConceptosSpControladorEnum.NOMBRECENTROCOSTO
                        .getValue());
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            addAllRegistro();
        }
        removerAuditoria();
        return true;
    }

    /**
     * metod que valida el estado del registro
     * 
     * @param var
     * @param valor
     */
    private void addRegistro(String var, Object valor) {
        registro.getCampos().put(var,
                        SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                        var) ? valor
                                            : registro.getCampos().get(var));
    }

    /**
     * metodo que adiciona los registro obligatorio y no obligatorios
     * para la actualizacion de los datos
     */
    private void addAllRegistro() {
        // obligatorios
        addRegistro(ConceptosSpControladorEnum.SERVICIO.getValue(), "");
        addRegistro(ConceptosSpControladorEnum.IVA.getValue(), 0.0);
        addRegistro(ConceptosSpControladorEnum.VALOR.getValue(), 0.0);
        addRegistro(ConceptosSpControladorEnum.PORC_DESCUENTO.getValue(), 0.0);
        addRegistro(ConceptosSpControladorEnum.AUXILIAR.getValue(), "");
        addRegistro(ConceptosSpControladorEnum.ACUMFIN.getValue(), 0);
        addRegistro(ConceptosSpControladorEnum.CODIGO.getValue(), 0);
        addRegistro(ConceptosSpControladorEnum.FINANCIAR.getValue(), 0);
        addRegistro(ConceptosSpControladorEnum.NOREPORTAR.getValue(), 0);
        addRegistro(ConceptosSpControladorEnum.ORDENABONAR.getValue(), 0.0);
        addRegistro(ConceptosSpControladorEnum.CENTROCOSTO.getValue(), "");
        addRegistro(ConceptosSpControladorEnum.AUTORETENCION.getValue(), 0.0);
        addRegistro(ConceptosSpControladorEnum.RECARGO.getValue(), 0.0);
        addRegistro(ConceptosSpControladorEnum.INTERES.getValue(), 0.0);
        // no obligaotrios
        addRegistro(ConceptosSpControladorEnum.CUENTACRABONO.getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CTADBRECFINANCIACIONPERIODO
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRSALDOCREDITO.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRRECAUDADOFAC.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRFINANCIACIONPERIODO
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBRECAUDADODEUDA
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRFACTURADO.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBFINANCIACIONPERIODO
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRDEUDA.getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CTACRRECFINANCIACIONDEUDA
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CTACRRECFINANCIACIONPERIODO
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CTACRRECABONOPERIODO.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.NOMBRE.getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBSALDOCREDITO.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBRECAUDADO.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBRECAUDADOFAC.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.DESCRIPCIONINTERFACE.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRRECAUDADOPERIODO
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRRECAUDADO.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CTACRRECABONODEUDA.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBFACTURADO.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRRECAUDADODEUDA
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBRECAUDADOPERIODO
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBDEUDA.getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBPAGOSDOBLES.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CUENTADBABONO.getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CTADBRECABONODEUDA.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CTADBRECABONOPERIODO.getValue(),
                        null);
        addRegistro(ConceptosSpControladorEnum.CTADBRECFINANCIACIONDEUDA
                        .getValue(), null);
        addRegistro(ConceptosSpControladorEnum.CUENTACRPAGOSDOBLES.getValue(),
                        null);
    }

    /**
     * remoueve los campos de auditoria
     */
    private void removerAuditoria() {
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_MODIFIED.getName());
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodos get y set
     * 
     * @return
     */
    public String getNombreCentroCosto() {
        return nombreCentroCosto;
    }

    public boolean isIva() {
        return iva;
    }

    public void setIva(boolean iva) {
        this.iva = iva;
    }

    public boolean isPorcDescuento() {
        return porcDescuento;
    }

    public void setPorcDescuento(boolean porcDescuento) {
        this.porcDescuento = porcDescuento;
    }

    public boolean isFinanciablesAcumulado() {
        return financiablesAcumulado;
    }

    public void setFinanciablesAcumulado(boolean financiablesAcumulado) {
        this.financiablesAcumulado = financiablesAcumulado;
    }

    public void setNombreCentroCosto(String nombreCentroCosto) {
        this.nombreCentroCosto = nombreCentroCosto;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaServicio() {
        return listaServicio;
    }

    public void setListaServicio(List<Registro> listaServicio) {
        this.listaServicio = listaServicio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
