/*-
 * SubFrmDistribucionCuotasControlador.java
 *
 * 1.0
 * 
 * 11/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoNAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmAcuerdoPagoControladorEnum;
import com.sysman.facturaciongeneral.enums.SubFrmDistribucionAcuerdosControladorEnum;
import com.sysman.facturaciongeneral.enums.SubFrmDistribucionCuotasControladorEnum;
import com.sysman.facturaciongeneral.enums.SubFrmDistribucionCuotasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Controlador de la forma: <code>subfrmdistribucioncuota</code>.
 * Migracion del formulario <code>SUBFRM_DISTRIBUCION_CUOTA</code> de
 * Access.
 * 
 * @version 1.0, 11/12/2017
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class SubFrmDistribucionCuotasControlador
                extends BeanBaseContinuoNAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

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
     * <code>MI_CUOTA</code>
     */
    private final String cMiCuota = SubFrmDistribucionCuotasControladorEnum.MI_CUOTA
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPO</code>
     */
    private final String cTipo = SubFrmDistribucionCuotasControladorEnum.TIPO
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el tipo de cobro seleccionado al ingresar
     * al modulo de facturacion general,
     */
    private String tipoCobro;

    /**
     * Atributo que contiene el numero del acuerdo preliminar asociado
     * al controlador: {@link FrmAcuerdoPagoControlador},
     * <code>CP45962</code>.
     */
    private String acuerdoNro;

    /**
     * Atributo que contiene la cuota asociada al controlador:
     * {@link SubFrmDistribucionAcuerdosControlador},
     * <code>CP47478</code>.
     */
    private String cuota;

    /**
     * Atributo que contiene la sumatoria de los valores del campo
     * <code>47645</code>.
     */
    private String sumBase;

    /**
     * Atributo que contiene la sumatoria de los valores del campo
     * <code>47646</code>.
     */
    private String sumIVA;

    /**
     * Atributo que contiene la sumatoria de los valores del campo
     * <code>47647</code>.
     */
    private String sumRetefuente;

    /**
     * Atributo que contiene la sumatoria de los valores del campo
     * <code>47648</code>.
     */
    private String sumDescuento;

    /**
     * Atributo que contiene la sumatoria de los valores del campo
     * <code>47649</code>.
     */
    private String sumICA;

    /**
     * Atributo que contiene la sumatoria de los valores del campo
     * <code>47650</code>.
     */
    private String sumTotalCuota;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SubFrmDistribucionCuotasControlador
     */
    public SubFrmDistribucionCuotasControlador() {
        super();

        compania = SessionUtil.getCompania();

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

        if (parametrosEntrada != null) {
            tipoCobro = (String) parametrosEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_TIPOCOBRO
                                            .getValue());

            acuerdoNro = (String) parametrosEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_NUMACUERDO
                                            .getValue());

            cuota = parametrosEntrada
                            .get(SubFrmDistribucionAcuerdosControladorEnum.PAR_CUOTA
                                            .getValue())
                            .toString();
        }

        try {
            // 1496
            numFormulario = GeneralCodigoFormaEnum.SUB_FRM_DISTRIBUCION_CUOTAS_CONTROLADOR
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
        tabla = GenericUrlEnum.SF_TEMP_DETALLE_CUOTA.getTable();

        urlListado = UrlServiceUtil
                        .getUrlBeanById(SubFrmDistribucionCuotasControladorUrlEnum.URL0002
                                        .getValue());

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cTipo, tipoCobro);
        parametrosListado.put(cCodigo, acuerdoNro);
        parametrosListado.put(cMiCuota, cuota);

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        calcularSumatorias();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario desde el boton
     * cerrar.
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Realiza la sumatoria de las columnas de la grilla y muestra los
     * totales en el pie de grilla.
     */
    private void calcularSumatorias() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cTipo, tipoCobro);
        param.put(cCodigo, acuerdoNro);
        param.put(cMiCuota, cuota);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            Registro auxReg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubFrmDistribucionCuotasControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(),
                            param));

            if (auxReg != null) {
                sumBase = formatearADecimal(auxReg, "BASE_SUM", df);
                sumIVA = formatearADecimal(auxReg, "IVA_SUM", df);
                sumRetefuente = formatearADecimal(auxReg, "RETE_SUM", df);
                sumDescuento = formatearADecimal(auxReg, "DESCUENTO_SUM", df);
                sumICA = formatearADecimal(auxReg, "ICA_SUM", df);
                sumTotalCuota = formatearADecimal(auxReg, "CUOTA_SUM", df);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Asigna la mascara <code>#,##0.00</code> al valor numerico del
     * campo: <code>nomCampo</code>.
     * 
     * @author pespitia
     * 
     * @param parReg
     * -> Registro que contiene la coleccion de campos.
     * @param nomCampo
     * -> Nombre del campo.
     * @param df
     * -> Referencia de la estructura de formateo.
     * @return El valor formateado a decimal.
     */
    private String formatearADecimal(Registro parReg, String nomCampo,
        DecimalFormat df) {
        return df.format(Double.parseDouble(SysmanFunciones
                        .nvl(parReg.getCampos().get(nomCampo), "0")
                        .toString()));
    }

    // <SET_GET_ATRIBUTOS>
    public String getSumBase() {
        return sumBase;
    }

    public void setSumBase(String sumBase) {
        this.sumBase = sumBase;
    }

    public String getSumIVA() {
        return sumIVA;
    }

    public void setSumIVA(String sumIVA) {
        this.sumIVA = sumIVA;
    }

    public String getSumRetefuente() {
        return sumRetefuente;
    }

    public void setSumRetefuente(String sumRetefuente) {
        this.sumRetefuente = sumRetefuente;
    }

    public String getSumDescuento() {
        return sumDescuento;
    }

    public void setSumDescuento(String sumDescuento) {
        this.sumDescuento = sumDescuento;
    }

    public String getSumICA() {
        return sumICA;
    }

    public void setSumICA(String sumICA) {
        this.sumICA = sumICA;
    }

    public String getSumTotalCuota() {
        return sumTotalCuota;
    }

    public void setSumTotalCuota(String sumTotalCuota) {
        this.sumTotalCuota = sumTotalCuota;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
