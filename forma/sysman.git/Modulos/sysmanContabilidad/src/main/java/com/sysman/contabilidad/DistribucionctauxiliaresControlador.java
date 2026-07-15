package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.DistribucionctauxiliaresControladorEnum;
import com.sysman.contabilidad.enums.DistribucionctauxiliaresControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 10/03/2016
 * @version 2, 11/04/2017 modificado por jcrodriguez
 * descripcion:--depuracion del controlador --creacino de DSS o
 * servicios para el formulario continuo
 */
@ManagedBean
@ViewScoped
public class DistribucionctauxiliaresControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * variable que alamcena la compa�ia
     */
    private final String compania;
    /**
     * lista los codigos cuenta
     */
    private List<Registro> listaCodigoCuenta;
    /**
     * lista auxiliar
     */
    private List<Registro> listaAUXILIAR;
    /**
     * lista los centro de costos
     */
    private List<Registro> listaCentroCosto;
    /**
     * variable que almacena el a�o actual
     */
    private int anoActual;
    /**
     * variable que almacena la cuenta actual
     */
    private String cuentaActual;
    /**
     * variable que almacena la suma del porcentaje
     */
    private int sumaPorcentaje;
    /**
     * variable que almacena el estado verNuevo
     */
    private boolean verNuevo;
    /**
     * variable que almacen al la centa actual
     */
    private String cuentaActualSub;

    /**
     * Creates a new instance of DistribucionctauxiliaresControlador
     */
    public DistribucionctauxiliaresControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            cuentaActual = cadenaVaciaH(parametrosEntrada,
                            GeneralParameterEnum.CUENTA.getName());
            anoActual = variableVacia(parametrosEntrada,
                            GeneralParameterEnum.ANO.getName());
            numFormulario = GeneralCodigoFormaEnum.DISTRIBUCIONCTAUXILIARES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            DistribucionctauxiliaresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * metodo que valida el casteo a toString
     * 
     * @param campos
     * @param var
     * @return
     */
    private String cadenaVaciaH(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    /**
     * metodo que valida el casteo a int
     * 
     * @param campos
     * @param var
     * @return
     */
    private int variableVacia(Map<String, Object> campos, String var) {
        return Integer.parseInt(SysmanFunciones.validarCampoVacio(campos, var)
            ? "0" : campos.get(var).toString());
    }

    /**
     * metodo que se llama cuando se inicia el formulario
     */
    @PostConstruct
    public void inicializar() {
        try {
            enumBase = GenericUrlEnum.DISTRIBUCIONCT_AUXILIARES;
            buscarLlave();
            reasignarOrigen();
            registro = new Registro();
            cargarListaCodigoCuenta();
            String esta = service.buscarEnLista(cuentaActual,
                            GeneralParameterEnum.CODIGO.getName(),
                            GeneralParameterEnum.CODIGO.getName(),
                            listaCodigoCuenta);
            verNuevo = esta != null;
            if (verNuevo) {
                registro.getCampos()
                                .put(DistribucionctauxiliaresControladorEnum.CUENTA_DIS
                                                .getValue(), cuentaActual);
            }
            cargarListaAUXILIAR();
            cargarListaCentroCosto();
            abrirFormulario();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            DistribucionctauxiliaresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * metodo que se llama para calcula la suma del porcentaje
     */
    public void calcularSumaPorcentaje() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DistribucionctauxiliaresControladorEnum.ANOACTUAL.getValue(),
                        anoActual);
        param.put(DistribucionctauxiliaresControladorEnum.CUENTAACTUAL
                        .getValue(), cuentaActual);

        try {
            sumaPorcentaje = 0;
            List<Registro> lista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DistribucionctauxiliaresControladorUrlEnum.URL2728
                                                                            .getValue())
                                            .getUrl(), param));
            if (lista.get(0).getCampos()
                            .get(DistribucionctauxiliaresControladorEnum.SUMA_PORCENTAJES
                                            .getValue()) != null) {
                String sum = lista.get(0).getCampos()
                                .get(DistribucionctauxiliaresControladorEnum.SUMA_PORCENTAJES
                                                .getValue())
                                .toString();
                sumaPorcentaje = Integer.parseInt(sum);
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(
                            DistribucionctauxiliaresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * reasignar el origen
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(DistribucionctauxiliaresControladorEnum.ANOACTUAL
                        .getValue(), anoActual);
        parametrosListado
                        .put(DistribucionctauxiliaresControladorEnum.CUENTAACTUAL
                                        .getValue(), cuentaActual);
        calcularSumaPorcentaje();
    }

    /**
     * metodo que carga la lista de codigo cuenta
     */
    public void cargarListaCodigoCuenta() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DistribucionctauxiliaresControladorEnum.ANOACTUAL.getValue(),
                        anoActual);

        try {
            listaCodigoCuenta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DistribucionctauxiliaresControladorUrlEnum.URL8251
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga la lista auxiliar
     */
    public void cargarListaAUXILIAR() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DistribucionctauxiliaresControladorEnum.ANOACTUAL.getValue(),
                        anoActual);

        try {
            listaAUXILIAR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DistribucionctauxiliaresControladorUrlEnum.URL8790
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga la lista de centros de costos
     */
    public void cargarListaCentroCosto() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DistribucionctauxiliaresControladorEnum.ANOACTUAL.getValue(),
                        anoActual);

        try {
            listaCentroCosto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DistribucionctauxiliaresControladorUrlEnum.URL9246
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que valida el total del porcentaje
     * 
     * @return
     */
    public boolean validarTotalPorcentaje() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DistribucionctauxiliaresControladorEnum.ANOACTUAL.getValue(),
                        anoActual);

        try {
            List<Registro> rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DistribucionctauxiliaresControladorUrlEnum.URL2729
                                                                            .getValue())
                                            .getUrl(), param));

            StringBuilder ms = new StringBuilder("");
            ms.append(idioma.getString("TB_TB483"));
            if (rs != null) {
                String cActual;
                String pActual;
                for (Registro r : rs) {
                    cActual = r.getCampos()
                                    .get(GeneralParameterEnum.CUENTA.getName())
                                    .toString()
                        + "#";
                    pActual = r.getCampos()
                                    .get(DistribucionctauxiliaresControladorEnum.SUMA_PORCENTAJES
                                                    .getValue())
                                    .toString();
                    ms.append("\n" + SysmanFunciones.padr(cActual, 72, ".")
                        + " ");
                    ms.append(pActual + "%</span>");
                    ms.append(ms.toString().replace("#",
                                    "<span style=float:right>"));
                }
            }
            if (ms.toString().equals(idioma.getString("TB_TB483"))) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB484"));
                return true;
            }
            else {
                JsfUtil.agregarMensajeInformativoDialogo(ms.toString());
                return false;
            }
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    /**
     * metodo que se llama cuando se oprime el boton revisar
     * distribucion
     */
    public void oprimirComando0() {
        validarTotalPorcentaje();
    }

    /**
     * metodo que se ejecuta cuando se cambia el codigo cuenta
     */
    public void cambiarCodigoCuenta() {

        if (cuentaActual != null) {
            verNuevo = true;
            calcularSumaPorcentaje();
            registro.getCampos()
                            .put(DistribucionctauxiliaresControladorEnum.CUENTA_DIS
                                            .getValue(), cuentaActual);
        }
        else {
            verNuevo = false;
        }
        reasignarOrigen();
    }

    /**
     * metodo que se llama cuando se abre el formulario
     */
    @Override
    public void abrirFormulario() {
        cuentaActualSub = idioma
                        .getString(DistribucionctauxiliaresControladorEnum.IDIOMA1
                                        .getValue())
                        .replace(DistribucionctauxiliaresControladorEnum.CUENTAREEPLAZAR
                                        .getValue(), cuentaActual);
    }

    /**
     * metodo que cancela la edicion
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * metodo heredado del bean base para insertar antes
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anoActual);
        registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
                        cuentaActual);
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_MODIFIED.getName());
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean base para insertar despues
     */
    @Override
    public boolean insertarDespues() {
        calcularSumaPorcentaje();
        return true;
    }

    /**
     * metodo heredado del bean base para actualizar antes
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOM_AUXILIAR");
        registro.getCampos().remove("NOM_CENTRO");
        return true;
    }

    /**
     * metodo heredado del bean base para actualizar despues
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        calcularSumaPorcentaje();
        return true;
    }

    /**
     * metodo heredado del bean base para eliminar antes
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean base para eliminar despues
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        calcularSumaPorcentaje();
        return true;
    }

    /**
     * metodo para remover
     */
    @Override
    public void removerCombos() {
        registro.getCampos()
                        .remove(DistribucionctauxiliaresControladorEnum.NOM_CENTRO
                                        .getValue());
        registro.getCampos()
                        .remove(DistribucionctauxiliaresControladorEnum.NOM_AUXILIAR
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CUENTA.getName());

    }

    /**
     * metodo para cerrar el formulario
     */
    public void cerrarFormulario() {
        if (validarTotalPorcentaje()) {
            RequestContext.getCurrentInstance().closeDialog(null);
        }
    }

    /**
     * metodo para asignar los valores a los registros
     */
    @Override
    public void asignarValoresRegistro() {
        registro.getCampos()
                        .put(DistribucionctauxiliaresControladorEnum.CUENTA_DIS
                                        .getValue(), cuentaActual);
    }

    /**
     * metodo get y set
     * 
     * @return
     */
    public int getAnoActual() {
        return anoActual;
    }

    public void setAnoActual(int anoActual) {
        this.anoActual = anoActual;
    }

    public String getCuentaActual() {
        return cuentaActual;
    }

    public void setCuentaActual(String cuentaActual) {
        this.cuentaActual = cuentaActual;
    }

    public String getCuentaActualSub() {
        return cuentaActualSub;
    }

    public void setCuentaActualSub(String cuentaActualSub) {
        this.cuentaActualSub = cuentaActualSub;
    }

    public int getSumaPorcentaje() {
        return sumaPorcentaje;
    }

    public void setSumaPorcentaje(int sumaPorcentaje) {
        this.sumaPorcentaje = sumaPorcentaje;
    }

    public boolean isVerNuevo() {
        return verNuevo;
    }

    public void setVerNuevo(boolean verNuevo) {
        this.verNuevo = verNuevo;
    }

    public List<Registro> getListaCodigoCuenta() {
        return listaCodigoCuenta;
    }

    public void setListaCodigoCuenta(List<Registro> listaCodigoCuenta) {
        this.listaCodigoCuenta = listaCodigoCuenta;
    }

    public List<Registro> getListaAUXILIAR() {
        return listaAUXILIAR;
    }

    public void setListaAUXILIAR(List<Registro> listaAUXILIAR) {
        this.listaAUXILIAR = listaAUXILIAR;
    }

    public List<Registro> getListaCentroCosto() {
        return listaCentroCosto;
    }

    public void setListaCentroCosto(List<Registro> listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

}
