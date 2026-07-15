package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmseleccionartipocobrosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
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
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ybecerra
 * @version 1, 11/03/2016
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario
 * 
 * @version 4, 09/11/2017, <strong>pespitia</strong>:
 * <li>Se ajusto el formulario para que pueda ser accedido desde la
 * opcion de menú 69 y realice los respectivos eventos del menu.
 * <li>Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class FrmseleccionartipocobrosControlador extends BeanBaseModal {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String menuActual = SessionUtil.getMenuActual();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGO</code>
     */
    private final String cCodigo = GeneralParameterEnum.CODIGO.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANO</code>
     */
    private final String cAno = GeneralParameterEnum.ANO.getName();

    private String ano;
    private String tipoCobro;

    /**
     * Atributo que almacena el nombre del tipo de cobro seleccionado
     * en el combo: <code>CB2058</code>.
     */
    private String nomTipoCobro;

    /**
     * Atributo que almacena el codigo del comprobante asociado al
     * tipo de cobro seleccionado en el combo: <code>CB2058</code>.
     */
    private String cpteRecaudo;

    /**
     * Atributo que almacena la clase de cuenta del recaudo asociada
     * al tipo de cobro seleccionado en el combo: <code>CB2058</code>.
     */
    private String claseCuentasRecaudo;

    /**
     * Atributo que indica si el tipo de cobro seleccionado en el
     * combo: <code>CB2058</code> maneja inventario.
     */
    private boolean indManejaInv;

    /**
     * Atributo que indica si el tipo de cobro seleccionado en el
     * combo: <code>CB2058</code> maneja INTERFAZ_RECAUDO.
     */
    private boolean indInterfazRecaudo;

    /**
     * Atributo que indica si el tipo de cobro seleccionado en el
     * combo: <code>CB2058</code> maneja TIPOCOBRO_NOFACTURADO.
     */
    private boolean indTipoCobroNoFac;

    /**
     * Atributo que indica si el tipo de cobro seleccionado en el
     * combo: <code>CB2058</code> maneja MANEJA_RECAUDOTERCEROS.
     */
    private boolean indManejaRecaudoTer;

    /**
     * Atributo que indica si el tipo de cobro seleccionado en el
     * combo: <code>CB2058</code> maneja INDPRELIQUIDACION.
     */
    private boolean indPreliquidacion;

    /**
     * Atributo que indica si el tipo de cobro seleccionado en el
     * combo: <code>CB2058</code> maneja APLCUENTAREC.
     */
    private boolean indAplCuentaRe;

    /**
     * Atributo que indica el prefijo referencia facturacion del tipo
     * de cobro
     */
    private String prefijo;

    /**
     * Atributo que indica el tipo de referencia de factura de recaudo
     */
    private String tipoFactRecaudo;

    private List<Registro> listaANO;
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los items del combo tipo de cobro
     * <code>CB2058</code>.
     */
    private RegistroDataModelImpl listaTipoCobro;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR_EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_EJBs>

    /**
     * Creates a new instance of FrmseleccionartipocobrosControlador
     */
    public FrmseleccionartipocobrosControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            // 571
            numFormulario = GeneralCodigoFormaEnum.FRMSELECCIONARTIPOCOBROS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            FrmseleccionartipocobrosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        cargarListaANO();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = Integer.toString(SysmanFunciones.ano(new Date()));
        cargarListaTipoCobro();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaANO() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaANO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmseleccionartipocobrosControladorUrlEnum.URL3211
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoCobro() {
        String urlEnumId = FrmseleccionartipocobrosControladorUrlEnum.URL3566
                        .getValue();

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cAno, ano);

        try {
            if (SysmanConstantes.MODULO_CONTABILIDAD == Integer
                            .parseInt(modulo)
                && !menuActual.equals("10128")) {
                String nomPar = "TIPO DE COBRO CONCEPTOS CONTABLES";
                String valor = recuperarValorPar(nomPar);
                valor = validarParametro(nomPar, valor) ? valor : "";

                param.put("TIPOCOBRO", valor);

                urlEnumId = FrmseleccionartipocobrosControladorUrlEnum.URL0001
                                .getValue();
            }

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);

            listaTipoCobro = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirCmdAceptar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(ano)
            || SysmanFunciones.validarVariableVacio(tipoCobro)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB625"));
            return;
        }

        Direccionador direccionador = new Direccionador();

        switch (menuActual) {
        case "69": // Facturacion General

            SessionUtil.cleanFlash();
            abrirMenuFacturacionGeneral(direccionador);
            break;
        case "10713": // Contabilidad
            abrirDesdeFormulario(direccionador,
                            Integer.toString(
                                            GeneralCodigoFormaEnum.CONCEPTOSSFS_CONTROLADOR
                                                            .getCodigo()));
            break;
        case "10128":
            inicializarVariablesFacturacionGeneral();
            abrirDesdeFormulario(direccionador,
                            Integer.toString(
                                            GeneralCodigoFormaEnum.SFCONCEPTOS_CONTROLADOR
                                                            .getCodigo()));
            break;
        case "690201":

            abrirDesdeFormulario(direccionador,
                            String.valueOf(GeneralCodigoFormaEnum.FACTURACIONCONCEPTOS_CONTROLADOR
                                            .getCodigo()));
            break;
        default:
            break;
        }

        RequestContext.getCurrentInstance().closeDialog(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    private void inicializarVariablesFacturacionGeneral() {
        try {
            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.ANIO.getValue(), ano);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.TIPOCOBRO.getValue(),
                            tipoCobro);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                            .getValue(),
                            nomTipoCobro);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.MANEJA_INVENTARIO
                                            .getValue(),
                            indManejaInv);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.INTERFAZ_RECAUDO
                                            .getValue(),
                            indInterfazRecaudo);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.TIPOCOBRO_NOFACTURADO
                                            .getValue(),
                            indTipoCobroNoFac);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.CPTE_RECAUDO
                                            .getValue(),
                            cpteRecaudo);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.MANEJA_RECAUDOTERCEROS
                                            .getValue(),
                            indManejaRecaudoTer);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.CLASE_CUENTASRECAUDO
                                            .getValue(),
                            claseCuentasRecaudo);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.INDPRELIQUIDACION
                                            .getValue(),
                            indPreliquidacion);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.APLICACUENTARE
                                            .getValue(),
                            indAplCuentaRe);

        }
        catch (NamingException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirCmdCancelar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarANO() {
        // <CODIGO_DESARROLLADO>
        cargarListaTipoCobro();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * <code>listaTipoCobro</code>, asociada al combo
     * <code>CB2058</code>.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaTipoCobro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoCobro = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        if (!SysmanFunciones.validarVariableVacio(tipoCobro)) {
            nomTipoCobro = registroAux.getCampos().get("NOMBRE").toString();

            indManejaInv = (Boolean) registroAux.getCampos()
                            .get("MANEJA_INVENTARIO");

            indInterfazRecaudo = (Boolean) registroAux.getCampos()
                            .get("INTERFAZ_RECAUDO");

            indTipoCobroNoFac = (Boolean) registroAux.getCampos()
                            .get("TIPOCOBRO_NOFACTURADO");

            cpteRecaudo = SysmanFunciones
                            .nvl(registroAux.getCampos().get("CPTE_RECAUDO"),
                                            "")
                            .toString();

            indManejaRecaudoTer = (Boolean) registroAux.getCampos()
                            .get("MANEJA_RECAUDOTERCEROS");

            claseCuentasRecaudo = SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get("CLASE_CUENTASRECAUDO"), "")
                            .toString();

            indPreliquidacion = (Boolean) registroAux.getCampos()
                            .get("INDPRELIQUIDACION");

            indAplCuentaRe = (Boolean) registroAux.getCampos()
                            .get("APLCUENTAREC");

            prefijo = SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get("REF_FACTURACION"), "")
                            .toString();

            tipoFactRecaudo = SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get("TIPOFACTURA_RECAUDO"), "")
                            .toString();

        }
    }
    // </METODOS_COMBOS_GRANDES>

    /**
     * Metodo ejecutado al presionar la opcion de menu 69.
     * 
     * @param direccionador
     * -> Referencia del direccionador.
     */
    private void abrirMenuFacturacionGeneral(Direccionador direccionador) {
        try {
            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.ANIO.getValue(), ano);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.TIPOCOBRO.getValue(),
                            tipoCobro);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                            .getValue(),
                            nomTipoCobro);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.MANEJA_INVENTARIO
                                            .getValue(),
                            indManejaInv);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.INTERFAZ_RECAUDO
                                            .getValue(),
                            indInterfazRecaudo);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.TIPOCOBRO_NOFACTURADO
                                            .getValue(),
                            indTipoCobroNoFac);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.CPTE_RECAUDO
                                            .getValue(),
                            cpteRecaudo);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.MANEJA_RECAUDOTERCEROS
                                            .getValue(),
                            indManejaRecaudoTer);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.CLASE_CUENTASRECAUDO
                                            .getValue(),
                            claseCuentasRecaudo);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.INDPRELIQUIDACION
                                            .getValue(),
                            indPreliquidacion);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.APLICACUENTARE
                                            .getValue(),
                            indAplCuentaRe);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.PREFIJO
                                            .getValue(),
                            prefijo);

            SessionUtil.setSessionVarContainer(
                            ConstantesFacturacionGenEnum.TIPOFACTURA_RECAUDO
                                            .getValue(),
                            tipoFactRecaudo);

            SessionUtil.setSessionVarContainer("menu", "69");

            direccionador.setRuta("/menu.sysman");
        }
        catch (NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al presionar la opcion de menu 10713.
     * 
     * @param direccionador
     * -> Referencia del direccionador.
     */
    private void abrirDesdeFormulario(Direccionador direccionador,
        String formulario) {
        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(ConstantesFacturacionGenEnum.ANIO.getValue(), ano);

        parametros.put(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        parametros.put(ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO.getValue(),
                        nomTipoCobro);

        parametros.put(ConstantesFacturacionGenEnum.MANEJA_RECAUDOTERCEROS
                        .getValue(), indManejaRecaudoTer);
        parametros.put(ConstantesFacturacionGenEnum.CLASE_CUENTASRECAUDO
                        .getValue(), claseCuentasRecaudo);

        direccionador.setNumForm(formulario);

        direccionador.setParametros(parametros);
    }

    /**
     * Consulta y retorna el valor asignado al parametro segun la base
     * de datos.
     * 
     * @param nombrePar
     * Nombre asignado al parametro
     * @return El valor del parametro asignado en la base de datos.
     * @throws SystemException
     */
    private String recuperarValorPar(String nombrePar) throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania, nombrePar, modulo,
                        new Date(), false);
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en
     * la base de datos. De lo contrario muestra un mensaje
     * informativo.
     * 
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor
     * diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3769")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public List<Registro> getListaANO() {
        return listaANO;
    }

    public void setListaANO(List<Registro> listaANO) {
        this.listaANO = listaANO;
    }

    public RegistroDataModelImpl getListaTipoCobro() {
        return listaTipoCobro;
    }

    public void setListaTipoCobro(RegistroDataModelImpl listaTipoCobro) {
        this.listaTipoCobro = listaTipoCobro;
    }
}
