/*-
 * FrmAcuerdoPagoControlador.java
 *
 * 1.0
 * 
 * 10/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman. Paipa, Boyaca. All rights
 * reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralAcuerdosRemote;
import com.sysman.facturaciongeneral.enums.FrmAcuerdoPagoControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmAcuerdoPagoControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: <code>frmacuerdopago</code>. Migracion del
 * formulario: <code>FRM_ACUERDO_PAGO</code> de Access.
 *
 * @version 1.0, 10/11/2017
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmAcuerdoPagoControlador extends BeanBaseDatosAcmeImpl {

    /** Constante que almacena el caracter coma. */
    private static final String SIMBOLO_COMA = ",";

    /** Constante que almacena el caracter punto y coma. */
    private static final String SIMBOLO_PUNTOYCOMA = ";";

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que almacena el tipo de cobro
     * seleccionado al ingresar al modulo de facturacion general.
     */
    private final String tipoCobro = SessionUtil.getSessionVar(
                    ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                    .toString();

    /**
     * Constante a nivel de clase que almacena el anio seleccionado al
     * ingresar al modulo de facturacion general.
     */
    private final int anio = Integer.parseInt(SessionUtil
                    .getSessionVar(ConstantesFacturacionGenEnum.ANIO.getValue())
                    .toString());

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>MSM_PROCESO_EJECUTADO</code>
     */
    private final String cMsmProEje = FrmAcuerdoPagoControladorEnum.MSM_PROCESO_EJECUTADO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NIT</code>
     */
    private final String cNit = FrmAcuerdoPagoControladorEnum.NIT.getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRE</code>
     */
    private final String cNombre = GeneralParameterEnum.NOMBRE.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TERCERO</code>
     */
    private final String cTercero = GeneralParameterEnum.TERCERO.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL</code>
     */
    private final String cSucursal = GeneralParameterEnum.SUCURSAL.getName();
    
    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL</code>
     */
    private final String cTipo = GeneralParameterEnum.TIPO.getName();


    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable de sesion que indica si el tipo de cobro seleccionado
     * al ingresar al modulo maneja inventario.
     */
    private boolean indManejaInv;

    /**
     * Atributo que contiene el nit del tercero seleccionado en el
     * combo <code>CB4754</code>.
     */
    private String codTercero;

    /**
     * Atributo que contiene el nombre del tercero seleccionado en el
     * combo <code>CB4754</code>.
     */
    private String nomTercero;

    /**
     * Atributo que contiene la sucursal del tercero seleccionado en
     * el combo <code>CB4754</code>.
     */
    private String sucursalTercero;

    /**
     * Atributo que contiene el numero del acuerdo del campo
     * <code>CP45962</code>.
     */
    private String numAcuerdo;

    /**
     * Atributo que contiene los numeros de facturas seleccionados en
     * la lista multiple <code>LM9</code>. En donde los numeros de
     * factura estan separados por coma.
     */
    private String numFacSeleccionados;

    /**
     * Atributo que contiene la deuda capital resultante de preparar
     * la deuda.
     */
    private String deudaCapital;

    /**
     * Atributo que contiene la deuda interes resultante de preparar
     * la deuda.
     */
    private String deudaInteres;

    /**
     * Atributo que contiene la deuda total resultante de preparar la
     * deuda.
     */
    private String deudaTotal;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que contiene los items del combo tercero,
     * <code>CB4754</code>.
     */
    private RegistroDataModelImpl listaTerceroAp;

    /**
     * Lista que contiene los items de la lista multiple
     * <code>LM9</code>.
     */
    private RegistroDataModelImpl listaFacturasAp;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    // <DECLARAR_EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del del paquete: <code>PCK_FACT_GENERAL</code>.
     */
    @EJB
    private EjbFacturacionGeneralAcuerdosRemote ejbFacturacionGeneralAcuerdos;

	private String manejaIndicador;
    // </DECLARAR_EJBs>

    /**
     * Crea una nueva instancia de FrmAcuerdoPagoControlador
     */
    public FrmAcuerdoPagoControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1445
            numFormulario = GeneralCodigoFormaEnum.FRM_ACUERDO_PAGO_CONTROLADOR
                            .getCodigo();

            // Variables de sesion
            indManejaInv = (Boolean) SessionUtil
                            .getSessionVar(ConstantesFacturacionGenEnum.MANEJA_INVENTARIO
                                            .getValue());

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        numAcuerdo = "";

        cargarListaTerceroAp();
        indicadorAcuerdoPredial();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario.
     */
    @Override
    public void asignarOrigenDatos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista <code>listaTerceroAp</code> asociada al combo
     * <code>CB4754</code>.
     */
    public void cargarListaTerceroAp() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAcuerdoPagoControladorUrlEnum.URL14317
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaTerceroAp = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    /**
     * Carga la lista <code>listaFacturasAp</code> asociada a la lista
     * multiple LM9.
     */
    public void cargarListaFacturasAp() {
        String urlEnumId = indManejaInv
            ? FrmAcuerdoPagoControladorUrlEnum.URL0001.getValue()
            : FrmAcuerdoPagoControladorUrlEnum.URL14952.getValue();

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cTercero, codTercero);
        param.put(cSucursal, sucursalTercero);
        param.put(cTipo ,tipoCobro);

        try {
            String[] nombreLlave = CacheUtil.getLlaveServicio(urlConexionCache,
                            GenericUrlEnum.SF_FACTURA.getTable());

            listaFacturasAp = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, false,
                            nombreLlave, true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al salir del formulario
     * {@link ParametrosFinanciacionControlador}.
     * 
     * @param event
     * -> Numero del acuerdo.
     */
    public void retornarFormularioBtParFinanciacion(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        numAcuerdo = SysmanFunciones.nvl(event.getObject(), "").toString();

        if (cMsmProEje.equals(numAcuerdo)) {
            numAcuerdo = "";
            codTercero = "";
            nomTercero = "";
            deudaCapital = "";
            deudaInteres = "";
            deudaTotal = "";

            listaFacturasAp = new RegistroDataModelImpl();

            JsfUtil.agregarMensajeInformativo(idioma.getString(cMsmProEje));
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * {@code listaTerceroAp} asociada al control <code>CB4754</code>.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaTerceroAp(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codTercero = SysmanFunciones.nvl(registroAux.getCampos().get(cNit), "")
                        .toString();

        nomTercero = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();

        sucursalTercero = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();

        eliminarDetallesAcuerdoPreliminar();
        cargarListaFacturasAp();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista multiple:
     * <code>LM9</code>.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaFacturasAp(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton BtPrepararDeuda (BT2628)
     * en la vista.
     */
    public void oprimirBtPrepararDeuda() {
        // <CODIGO_DESARROLLADO>
        obtenerFacturasSeleccionadas();

        try {
            String result = ejbFacturacionGeneralAcuerdos
                            .prepararDeudaAcuerdosPago(compania, numAcuerdo,
                                            numFacSeleccionados, tipoCobro);

            String[] deudas = result.split(SIMBOLO_PUNTOYCOMA);

            deudaCapital = deudas[0];
            deudaInteres = deudas[1];
            deudaTotal = deudas[2];

            numAcuerdo = "";

            JsfUtil.agregarMensajeInformativo(idioma.getString(cMsmProEje));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton BtDeudaActual en la vista.
     */
    public void oprimirBtDeudaActual() {
        // <CODIGO_DESARROLLADO>
        obtenerFacturasSeleccionadas();

        String[] campos = { "tipoCobro", "seleccionados" };
        String[] valores = { tipoCobro, numFacSeleccionados };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUB_FRM_AP_DEUDA_TOTALS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton BtParFinanciacion en la
     * vista.
     */
    public void oprimirBtParFinanciacion() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(deudaCapital)
            || SysmanFunciones.validarVariableVacio(deudaInteres)
            || SysmanFunciones.validarVariableVacio(deudaTotal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3786"));
            return;
        }

        String[] campos = new String[9];
        campos[0] = FrmAcuerdoPagoControladorEnum.PAR_DEUDACAPITAL.getValue();
        campos[1] = FrmAcuerdoPagoControladorEnum.PAR_DEUDAINTERES.getValue();
        campos[2] = FrmAcuerdoPagoControladorEnum.PAR_DEUDATOTAL.getValue();
        campos[3] = FrmAcuerdoPagoControladorEnum.PAR_TIPOCOBRO.getValue();
        campos[4] = FrmAcuerdoPagoControladorEnum.PAR_ANIO.getValue();
        campos[5] = FrmAcuerdoPagoControladorEnum.PAR_TERCERO.getValue();
        campos[6] = FrmAcuerdoPagoControladorEnum.PAR_SUCURSAL.getValue();

        campos[7] = FrmAcuerdoPagoControladorEnum.PAR_NUMFACSELECCIONADOS
                        .getValue();

        campos[8] = FrmAcuerdoPagoControladorEnum.PAR_NUMACUERDO.getValue();

        Object[] valores = new Object[9];
        valores[0] = deudaCapital;
        valores[1] = deudaInteres;
        valores[2] = deudaTotal;
        valores[3] = tipoCobro;
        valores[4] = anio;
        valores[5] = codTercero;
        valores[6] = sucursalTercero;
        valores[7] = numFacSeleccionados;
        valores[8] = numAcuerdo;

        String form = Integer
                        .toString(GeneralCodigoFormaEnum.PARAMETROS_FINANCIACION_CONTROLADOR
                                        .getCodigo());

        SessionUtil.cargarModalDatosFlash(form, modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton BtDistribucionCuotas
     * <code>BT2649</code> en la vista.
     */
    public void oprimirBtDistribucionCuotas() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { FrmAcuerdoPagoControladorEnum.PAR_TIPOCOBRO
                        .getValue(),
                            FrmAcuerdoPagoControladorEnum.PAR_NUMACUERDO
                                            .getValue() };

        String[] valores = { tipoCobro, numAcuerdo };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUB_FRM_DISTRIBUCION_ACUERDOS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CargarDatosPredial en la
     * vista
     *
     *
     */
    public void oprimirCargarDatosPredial() {

        String[] campos = { GeneralParameterEnum.COMPANIA.getName() };

        String[] valores = { compania };

        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_CARGAR_DATOS_PREDIAL_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos, valores);
    }
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro.
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return <code>true</code> -> Permite realizar la inserción del
     * registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
     * 
     * @return <code>true</code>.
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro.
     * 
     * @return <code>true</code> -> Permite insertar o actualizar el
     * registro.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro.
     * 
     * @return <code>true</code>.
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     * 
     * @return <code>true</code> -> Permite eliminar el registro.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     * 
     * @return <code>true</code>.
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo utilizado para concatenar los numeros de facturas
     * seleccionados en la lista multiple <code>LM9</code> y
     * separarlos por un {@code caracterComa}.
     * 
     */
    private void obtenerFacturasSeleccionadas() {
        StringBuilder facturas = new StringBuilder();

        for (Registro r : listaFacturasAp.getSeleccionados()) {
            facturas.append(SIMBOLO_COMA).append(
                            r.getCampos().get("NUMERO_FACTURA").toString());
        }

        numFacSeleccionados = facturas.toString().replaceFirst(SIMBOLO_COMA,
                        "");
    }

    /**
     * Elimina las cuotas, los detalles de acuerdos y el acuerdo de
     * pago asociado al tercero seleccionado en el combo:
     * <code>CB4754</code>.
     */
    private void eliminarDetallesAcuerdoPreliminar() {
        try {
            ejbFacturacionGeneralAcuerdos.eliminarAcuerdoPagoPorTercero(
                            compania, tipoCobro, codTercero, sucursalTercero);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void indicadorAcuerdoPredial() {
    	
    	Registro rsIndicador;
try {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoCobro);

			rsIndicador = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmAcuerdoPagoControladorUrlEnum.URL0002.getValue())
							.getUrl(),
							param));
	
		if (rsIndicador != null) {

			manejaIndicador = SysmanFunciones.nvl(rsIndicador.getCampos().get("MAN_ACUERDO_PRED"), "0").toString();
		}

} catch (SystemException e) {
	e.printStackTrace();
}

    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codTercero
     * 
     * @return codTercero
     */
    public String getCodTercero() {
        return codTercero;
    }

    /**
     * Asigna la variable codTercero
     * 
     * @param codTercero
     * Variable a asignar en codTercero
     */
    public void setCodTercero(String codTercero) {
        this.codTercero = codTercero;
    }

    public String getNomTercero() {
        return nomTercero;
    }

    public void setNomTercero(String nomTercero) {
        this.nomTercero = nomTercero;
    }

    public String getNumAcuerdo() {
        return numAcuerdo;
    }

    public void setNumAcuerdo(String numAcuerdo) {
        this.numAcuerdo = numAcuerdo;
    }

    // </SET_GET_ATRIBUTOS>

    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>

    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTerceroAp() {
        return listaTerceroAp;
    }

    public void setListaTerceroAp(RegistroDataModelImpl listaTerceroAp) {
        this.listaTerceroAp = listaTerceroAp;
    }

    public RegistroDataModelImpl getListaFacturasAp() {
        return listaFacturasAp;
    }

    public void setListaFacturasAp(RegistroDataModelImpl listaFacturasAp) {
        this.listaFacturasAp = listaFacturasAp;
    }

	public String getManejaIndicador() {
		return manejaIndicador;
	}

	public void setManejaIndicador(String manejaIndicador) {
		this.manejaIndicador = manejaIndicador;
	}
    
    
    // </SET_GET_LISTAS_COMBO_GRANDE>

    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
