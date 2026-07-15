/*-
 * ParametrosFinanciacionControlador.java
 *
 * 1.0
 * 
 * 20/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralAcuerdosRemote;
import com.sysman.facturaciongeneral.enums.FrmAcuerdoPagoControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Controlador de la forma: <code>parametrosfinanciacion</code>.
 * Migracion de la pestania: <code>Parametros de Financiacion</code>
 * asociada al formulario <code>FRM_ACUERDO_PAGO</code> de Access.
 *
 * @version 1.0, 20/11/2017
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class ParametrosFinanciacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que indica si el check Simple esta seleccionado,
     * <code>CK1342</code>.
     */
    private boolean ckSimple;

    /**
     * Atributo que indica si el check Condonar Intereses esta
     * seleccionado, <code>CK1342</code>.
     */
    private boolean ckCondonarInteres;

    /** Atributo que contiene la deuda capital calculada. */
    private double deudaCapital;

    /** Atributo que contiene la deuda interes calculada. */
    private double deudaInteres;

    /**
     * Atributo que contiene la deuda interes calculada sin
     * modificaciones.
     */
    private double deudaInteresReal;

    /** Atributo que contiene la deuda total calculada. */
    private double deudaTotal;

    /**
     * Atributo que controla el valor ingresado en el campo tasa
     * interes <code>CP46628</code>.
     */
    private double tasa;

    /**
     * Atributo que controla el valor ingresado en el campo gradiante
     * <code>CP46629</code>.
     */
    private double gradiante;

    /**
     * Atributo que controla el valor ingresado en el campo cuota
     * inicial <code>CP46630</code>.
     */
    private double cuotaInicial;

    /**
     * Atributo que controla el valor ingresado en el campo valor
     * condonado <code>CP46631</code>.
     */
    private double valorCondonado;

    /**
     * Atributo que controla el valor ingresado en el campo
     * descripcion condonacion <code>CP46632</code>.
     */
    private String obsCondonacion;

    /**
     * Atributo que contiene el numero de cuotas ingresado en el
     * campo: <code>CP46634</code>.
     */
    private int numCuotas;

    /**
     * Indicador que controla el bloqueo de los controles:
     * <li>CK1343 -> Condonar intereses.
     * <li>CP46631 -> Valor condonado.
     * <li>CP46632 -> Descripcion condonacion.
     */
    private boolean indDeudaInteres;

    /** Indicador que controla la visibilidad del dialogo DG210. */
    private boolean verDgTasaCero;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Atributo que contiene el tipo de cobro seleccionado al ingresar
     * al modulo de facturacion general, <code>CB2058</code>.
     */
    private String tipoCobro;

    /**
     * Atributo que contiene el anio seleccionado al ingresar al
     * modulo de facturacion general, <code>CB2057</code>.
     */
    private int anio;

    /**
     * Atributo que contiene el codigo del tercero seleccionado en
     * {@link FrmAcuerdoPagoControlador}, <code>CB4754</code>.
     */
    private String tercero;

    /**
     * Atributo que contiene la sucursal asociada al tercero
     * <code>CB4754</code> seleccionado en
     * {@link FrmAcuerdoPagoControlador}.
     */
    private String sucursal;

    /**
     * Atributo que contiene los numeros de facturas seleccionados en
     * {@link FrmAcuerdoPagoControlador}, <code>LM9</code>. Los
     * numeros entre factura y factura estan separados por el simbolo
     * coma.
     */
    private String numFacSeleccionados;

    /**
     * Atributo que contiene el numero del acuerdo preliminar,
     * <code>CP45962</code>.
     */
    private String numAcuerdo;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_EJBs>
    /**
     * Instancia que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_FACT_GENERAL_ACUERDOS</code>.
     */
    @EJB
    private EjbFacturacionGeneralAcuerdosRemote ejbFacturacionGeneralAcuerdos;
    // </DECLARAR_EJBs>

    /**
     * Crea una nueva instancia de ParametrosFinanciacionControlador
     */
    public ParametrosFinanciacionControlador() {
        super();

        compania = SessionUtil.getCompania();

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

        if (parametrosEntrada != null) {
            deudaCapital = parsearADouble(parametrosEntrada,
                            FrmAcuerdoPagoControladorEnum.PAR_DEUDACAPITAL
                                            .getValue());

            deudaInteresReal = deudaInteres = parsearADouble(parametrosEntrada,
                            FrmAcuerdoPagoControladorEnum.PAR_DEUDAINTERES
                                            .getValue());

            deudaTotal = parsearADouble(parametrosEntrada,
                            FrmAcuerdoPagoControladorEnum.PAR_DEUDATOTAL
                                            .getValue());

            tipoCobro = parametrosEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_TIPOCOBRO
                                            .getValue())
                            .toString();

            anio = (int) parametrosEntrada.get(
                            FrmAcuerdoPagoControladorEnum.PAR_ANIO.getValue());

            tercero = parametrosEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_TERCERO
                                            .getValue())
                            .toString();

            sucursal = parametrosEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_SUCURSAL
                                            .getValue())
                            .toString();

            numFacSeleccionados = parametrosEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_NUMFACSELECCIONADOS
                                            .getValue())
                            .toString();

            numAcuerdo = parametrosEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_NUMACUERDO
                                            .getValue())
                            .toString();
        }

        try {
            // 1458
            numFormulario = GeneralCodigoFormaEnum.PARAMETROS_FINANCIACION_CONTROLADOR
                            .getCodigo();

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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        numCuotas = 1;
        cuotaInicial = 0;
        tasa = 0;
        indDeudaInteres = deudaInteres <= 0;
        obsCondonacion = "";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton: <code>2655</code> en la
     * vista. Desencadena el proceso para generar el acuerdo
     * preliminar.
     */
    public void oprimirCmdAcuerdo() {
        // <CODIGO_DESARROLLADO>
        if (Double.compare(tasa, 0) == 0) {
            verDgTasaCero = true;

            return;
        }

        generarAcuerdoPreliminar();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton: <code>BT2656</code> en la
     * vista.
     */
    public void oprimirCmdAprobar() {
        // <CODIGO_DESARROLLADO>
        if (numAcuerdo.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3822"));
            return;
        }

        try {
            ejbFacturacionGeneralAcuerdos.aprobarAcuerdo(compania, tipoCobro,
                            new BigInteger(numAcuerdo), numFacSeleccionados,
                            usuario);

            RequestContext.getCurrentInstance()
                            .closeDialog("MSM_PROCESO_EJECUTADO");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton BtSalir
     * <code>BT2676</code> en la vista.
     */
    public void oprimirBtSalir() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(numAcuerdo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CuotaInicial, CP46630.
     */
    public void cambiarCuotaInicial() {
        // <CODIGO_DESARROLLADO>
        if (cuotaInicial >= deudaTotal) {
            cuotaInicial = 0;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3787"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorCondonado, CP46631.
     */
    public void cambiarValorCondonado() {
        // <CODIGO_DESARROLLADO>
        if (ckCondonarInteres) {
            deudaInteres = deudaInteres - valorCondonado;
        }
        else {
            valorCondonado = 0;
            deudaInteres = deudaInteresReal;
        }

        deudaTotal = deudaCapital + deudaInteres;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton <strong>Si</strong> del
     * dialogo DgTasaCero en la vista.
     */
    public void aceptarDgTasaCero() {
        // <CODIGO_DESARROLLADO>
        verDgTasaCero = false;

        generarAcuerdoPreliminar();

        return;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton <strong>No</strong> del
     * dialogo DgTasaCero en la vista.
     */
    public void cancelarDgTasaCero() {
        // <CODIGO_DESARROLLADO>
        verDgTasaCero = false;
        return;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Ejecuta el proceso que permite generar el acuerdo preliminar.
     */
    private void generarAcuerdoPreliminar() {
        try {
            numAcuerdo = ejbFacturacionGeneralAcuerdos.generarAcuerdoPreliminar(
                            compania, tipoCobro, anio, tercero, sucursal,
                            Double.toString(deudaCapital),
                            Double.toString(deudaInteres),
                            Double.toString(deudaTotal),
                            Double.toString(cuotaInicial), ckCondonarInteres,
                            obsCondonacion, numFacSeleccionados, numAcuerdo,
                            tasa, numCuotas, Double.toString(valorCondonado),
                            ckSimple, gradiante, usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

            String[] campos = { "tipoCobro", "numAcuerdo" };

            String[] valores = { tipoCobro, numAcuerdo };

            SessionUtil.cargarModalDatosFlashCerrar(
                            Integer.toString(
                                            GeneralCodigoFormaEnum.SUB_FRM_DISTRIBUCION_ACUERDOS_CONTROLADOR
                                                            .getCodigo()),
                            SessionUtil.getModulo(), campos, valores);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo utilizado para realizar el parseo del valor de un campo
     * de tipo <code>java.lang.String</code> a tipo
     * <code>java.lang.Double</code>.
     * 
     * @param campos
     * -> Coleccion que contiene los campos.
     * @param nombre
     * -> Nombre del campo.
     * @return El valor del campo.
     */
    private double parsearADouble(Map<String, Object> campos, String nombre) {
        return Double.parseDouble(SysmanFunciones.nvl(campos.get(nombre), "0")
                        .toString());
    }

    public boolean isCkSimple() {
        return ckSimple;
    }

    public void setCkSimple(boolean ckSimple) {
        this.ckSimple = ckSimple;
    }

    public boolean isCkCondonarInteres() {
        return ckCondonarInteres;
    }

    public void setCkCondonarInteres(boolean ckCondonarInteres) {
        this.ckCondonarInteres = ckCondonarInteres;
    }

    public double getDeudaCapital() {
        return deudaCapital;
    }

    public void setDeudaCapital(double deudaCapital) {
        this.deudaCapital = deudaCapital;
    }

    public double getDeudaInteres() {
        return deudaInteres;
    }

    public void setDeudaInteres(double deudaInteres) {
        this.deudaInteres = deudaInteres;
    }

    public double getDeudaTotal() {
        return deudaTotal;
    }

    public void setDeudaTotal(double deudaTotal) {
        this.deudaTotal = deudaTotal;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }

    public double getGradiante() {
        return gradiante;
    }

    public void setGradiante(double gradiante) {
        this.gradiante = gradiante;
    }

    public double getCuotaInicial() {
        return cuotaInicial;
    }

    public void setCuotaInicial(double cuotaInicial) {
        this.cuotaInicial = cuotaInicial;
    }

    public double getValorCondonado() {
        return valorCondonado;
    }

    public void setValorCondonado(double valorCondonado) {
        this.valorCondonado = valorCondonado;
    }

    /**
     * Retorna la variable obsCondonacion
     * 
     * @return obsCondonacion
     */
    public String getObsCondonacion() {
        return obsCondonacion;
    }

    /**
     * Asigna la variable obsCondonacion
     * 
     * @param obsCondonacion
     * Variable a asignar en obsCondonacion
     */
    public void setObsCondonacion(String obsCondonacion) {
        this.obsCondonacion = obsCondonacion;
    }

    /**
     * Retorna la variable numCuotas
     * 
     * @return numCuotas
     */
    public int getNumCuotas() {
        return numCuotas;
    }

    /**
     * Asigna la variable numCuotas
     * 
     * @param numCuotas
     * Variable a asignar en numCuotas
     */
    public void setNumCuotas(int numCuotas) {
        this.numCuotas = numCuotas;
    }

    public boolean isIndDeudaInteres() {
        return indDeudaInteres;
    }

    public void setIndDeudaInteres(boolean indDeudaInteres) {
        this.indDeudaInteres = indDeudaInteres;
    }

    public boolean isVerDgTasaCero() {
        return verDgTasaCero;
    }

    public void setVerDgTasaCero(boolean verDgTasaCero) {
        this.verDgTasaCero = verDgTasaCero;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
