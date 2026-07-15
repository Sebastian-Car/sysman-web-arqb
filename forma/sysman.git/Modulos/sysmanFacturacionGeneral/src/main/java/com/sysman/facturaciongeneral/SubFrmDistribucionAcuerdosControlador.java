/*-
 * SubFrmDistribucionAcuerdosControlador.java
 *
 * 1.0
 * 
 * 06/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmAcuerdoPagoControladorEnum;
import com.sysman.facturaciongeneral.enums.SubFrmDistribucionAcuerdosControladorEnum;
import com.sysman.facturaciongeneral.enums.SubFrmDistribucionAcuerdosControladorUrlEnum;
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
import org.primefaces.event.RowEditEvent;

/**
 * Controlador de la forma: <code>subfrmdistribucionacuerdo</code>.
 * Migracion de la pestania: <code>Distribucion de Cuotas</code>
 * asociada al formulario <code>FRM_ACUERDO_PAGO</code> de Access.
 *
 * @version 1.0, 06/12/2017
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class SubFrmDistribucionAcuerdosControlador
                extends BeanBaseContinuoAcmeImpl {
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
     * Constante a nivel de clase que aloja la cadena
     * <code>CODIGO</code>.
     */
    private final String cCodigo = GeneralParameterEnum.CODIGO.getName();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>TIPO</code>.
     */
    private final String cTipo = SubFrmDistribucionAcuerdosControladorEnum.TIPO
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo quer contiene el tipo de cobro seleccionado al
     * ingresar al modulo de facturacion general.
     */
    private String tipoCobro;

    /**
     * Atributo que contiene el numero del acuerdo preliminar asociado
     * al controlador: {@link FrmAcuerdoPagoControlador},
     * <code>CP45962</code>.
     */
    private String numAcuerdo;

    /**
     * Atributo que almacena la sumatoria de los valores del campo
     * Capital, CP47480.
     */
    private String sumCapital;

    /**
     * Atributo que almacena la sumatoria de los valores del campo
     * total cuota, CP47483.
     */
    private String sumTotalCuota;

    /**
     * Atributo que almacena la sumatoria de los valores del campo int
     * financiacion, CP47481.
     */
    private String sumIntFin;

    /**
     * Atributo que almacena la sumatoria de los valores del campo int
     * recargo, CP47482.
     */
    private String sumIntRec;

    /**
     * Atributo que almacena la sumatoria de los valores del campo
     * interes, CP47484.
     */
    private String sumInteres;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * SubFrmDistribucionAcuerdosControlador
     */
    public SubFrmDistribucionAcuerdosControlador() {
        super();

        compania = SessionUtil.getCompania();

        Map<String, Object> paramEntrada = SessionUtil.getFlash();

        if (paramEntrada != null) {
            tipoCobro = paramEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_TIPOCOBRO
                                            .getValue())
                            .toString();

            numAcuerdo = paramEntrada
                            .get(FrmAcuerdoPagoControladorEnum.PAR_NUMACUERDO
                                            .getValue())
                            .toString();
        }

        try {
            // 1486
            numFormulario = GeneralCodigoFormaEnum.SUB_FRM_DISTRIBUCION_ACUERDOS_CONTROLADOR
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
        tabla = GenericUrlEnum.SF_TEMP_DETALLE_ACUERDO.getTable();
        registro = new Registro();

        reasignarOrigen();
        buscarLlave();

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(SubFrmDistribucionAcuerdosControladorUrlEnum.URL0001
                                        .getValue());

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cTipo, tipoCobro);
        parametrosListado.put(cCodigo, numAcuerdo);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton <code>BT2689</code>.
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirBtDistribucion(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        String[] campos = new String[3];
        campos[0] = FrmAcuerdoPagoControladorEnum.PAR_TIPOCOBRO.getValue();
        campos[1] = FrmAcuerdoPagoControladorEnum.PAR_NUMACUERDO.getValue();

        campos[2] = SubFrmDistribucionAcuerdosControladorEnum.PAR_CUOTA
                        .getValue();

        Object[] valores = new Object[3];
        valores[0] = tipoCobro;
        valores[1] = numAcuerdo;
        valores[2] = reg.getCampos().get("CUOTA");

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUB_FRM_DISTRIBUCION_CUOTAS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

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
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cTipo, tipoCobro);
        param.put(cCodigo, numAcuerdo);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            Registro auxReg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubFrmDistribucionAcuerdosControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(),
                            param));

            if (auxReg != null) {
                sumCapital = formatearADecimal(auxReg, "SUMCAPITAL", df);
                sumInteres = formatearADecimal(auxReg, "SUMINTERES", df);
                sumIntFin = formatearADecimal(auxReg, "SUMFINANCIACION", df);
                sumIntRec = formatearADecimal(auxReg, "SUMRECARGO", df);
                sumTotalCuota = formatearADecimal(auxReg, "SUMTOTAL", df);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return <code>true</code>: Permite insertar el registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
     * 
     * @return <code>true</code>
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
     * @return <code>true</code>: Permite insertar o actualizar el
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
     * @return <code>true</code>
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
     * @return <code>true</code>: Permite eliminar el registro.
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
     * @return <code>true</code>
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario.
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Asigna la mascara <code>#,##0.00</code> al valor numerico del
     * campo: <code>nomCampo</code>.
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
        return df.format(SysmanFunciones
                        .nvlDbl(parReg.getCampos().get(nomCampo), 0));
    }

    // <SET_GET_ATRIBUTOS>
    public String getSumCapital() {
        return sumCapital;
    }

    public void setSumCapital(String sumCapital) {
        this.sumCapital = sumCapital;
    }

    public String getSumTotalCuota() {
        return sumTotalCuota;
    }

    public void setSumTotalCuota(String sumTotalCuota) {
        this.sumTotalCuota = sumTotalCuota;
    }

    public String getSumIntFin() {
        return sumIntFin;
    }

    public void setSumIntFin(String sumIntFin) {
        this.sumIntFin = sumIntFin;
    }

    public String getSumIntRec() {
        return sumIntRec;
    }

    public void setSumIntRec(String sumIntRec) {
        this.sumIntRec = sumIntRec;
    }

    public String getSumInteres() {
        return sumInteres;
    }

    public void setSumInteres(String sumInteres) {
        this.sumInteres = sumInteres;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
