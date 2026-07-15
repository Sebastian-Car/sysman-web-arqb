/*-
 * RiesgosControlador.java
 *
 * 1.0
 * 
 * 02/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.RiesgosControladorEnum;
import com.sysman.nomina.enums.RiesgosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Clase migrada para visualizar los valores totales del fondo de
 * riesgo seleccionado
 *
 * @version 1.0, 02/01/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class RiesgosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el valor del ano
     */
    private String ano;
    /**
     * Variable que almacena el valor del mes
     */
    private String mes;
    /**
     * Variable que almacena el valor del proceso recibido por
     * parametro
     */
    private String proceso;
    /**
     * Variable que almacena el valor del proceso recibido por
     * parametro
     */
    private String periodo;
    /**
     * Variable que almacena el valor del codigoFondo recibido por
     * parametro
     */
    private String codigoFondo;
    /**
     * Variable que almacena el valor del nitFondo recibido por
     * parametro
     */
    private String nitFondo;
    /**
     * Variable que almacena el valor del sucursalFondo recibido por
     * parametro
     */
    private String sucursalFondo;
    /**
     * Variable que almacena el valor del nombre del fondo recibido
     * por parametro
     */
    private String nombreFondo;
    /**
     * variable que almacena el valor de los aportes
     */
    private double valorAportes;
    /**
     * variable que almacena el valor de las incapacidades
     */
    private double valorIncapacidades;
    /**
     * variable que almacena el valor de los aportes pagados
     */
    private double valorAportesPagados;

    /**
     * variable que almacena el valor de los intereses mora
     */
    private double valorInteresesMora;
    /**
     * variable que almacena el Valor Saldo a Favor del Periodo
     * Anterior
     */
    private double valorPeriodoAnterior;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de RiesgosControlador
     */
    public RiesgosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1569
            numFormulario = GeneralCodigoFormaEnum.RIESGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                proceso = (String) SessionUtil.getSessionVar("procesoNomina");
                ano = (String) SessionUtil.getSessionVar("anioNomina");
                mes = SessionUtil.getSessionVar("mesNomina").toString();
                periodo = (String) SessionUtil.getSessionVar("periodoNomina");
                codigoFondo = parametrosEntrada.get("codigoFondo").toString();
                nitFondo = parametrosEntrada.get("nit").toString();
                sucursalFondo = parametrosEntrada.get("sucursal").toString();
                nombreFondo = parametrosEntrada.get("nombreFondo").toString();

            }
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
        enumBase = GenericUrlEnum.NOVEDADES_AUTOLIQUIDACION;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(RiesgosControladorEnum.KEY_COMPANIA.getValue(),
                        compania);
        parametrosListado.put(RiesgosControladorEnum.KEY_ANO.getValue(),
                        ano);
        parametrosListado.put(RiesgosControladorEnum.KEY_MES.getValue(),
                        mes);
        parametrosListado.put(RiesgosControladorEnum.KEY_PERIODO.getValue(),
                        periodo);
        parametrosListado.put(RiesgosControladorEnum.KEY_TIPO_ADMINISTRADORA
                        .getValue(),
                        "03");
        parametrosListado.put(
                        RiesgosControladorEnum.KEY_CODIGO_FONDO.getValue(),
                        codigoFondo);
        parametrosListado.put(RiesgosControladorEnum.KEY_NIT.getValue(),
                        nitFondo);
        parametrosListado.put(RiesgosControladorEnum.KEY_SUCURSAL.getValue(),
                        sucursalFondo);
        parametrosListado.put(
                        RiesgosControladorEnum.KEY_ID_DE_PROCESO.getValue(),
                        proceso);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TotalAportesArp
     * 
     */
    public void cambiarTotalAportesArp() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.TOTAL_APORTES_ARP.getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.TOTAL_APORTES_ARP.getValue(),
                            "0");
        }

        restarValoresAportes();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorIncapacidades
     * 
     */
    public void cambiarValorIncapacidades() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.VALOR_TOTAL_INCAP_PAGADASARP
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.VALOR_TOTAL_INCAP_PAGADASARP
                                            .getValue(),
                            "0");
        }

        restarValoresAportes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorAportesPagados
     * 
     */
    public void cambiarValorAportesPagados() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.VALOR_AP_PAGADOS_OTROS_RIESGOS
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.VALOR_AP_PAGADOS_OTROS_RIESGOS
                                            .getValue(),
                            "0");
        }

        restarValoresAportes();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorCotizacion
     * 
     */
    public void cambiarValorCotizacion() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.VALOR_TOTAL_COTIZACION
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.VALOR_TOTAL_COTIZACION
                                            .getValue(),
                            "0");
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DiasMora
     * 
     */
    public void cambiarDiasMora() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.DIAS_MORA_ARP.getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.DIAS_MORA_ARP.getValue(),
                            "0");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorInteresesMora
     * 
     */
    public void cambiarValorInteresesMora() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.VALOR_INTERESES_MORA_ARP
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.VALOR_INTERESES_MORA_ARP
                                            .getValue(),
                            "0");
        }

        sumarSubTotalAportes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control SubTotalAportesArp
     * 
     */
    public void cambiarSubTotalAportesArp() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.SUBTOTAL_APORTES_ARP
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.SUBTOTAL_APORTES_ARP
                                            .getValue(),
                            "0");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorSaldoPeriodo
     * 
     */
    public void cambiarValorSaldoPeriodo() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.VALOR_SALDOPERIODOANTERIORARP
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.VALOR_SALDOPERIODOANTERIORARP
                                            .getValue(),
                            "0");
        }

        sumarSubTotalAportes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorTotalNeto
     * 
     */
    public void cambiarValorTotalNeto() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.VALOR_TOTAL_NETO_ARP
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.VALOR_TOTAL_NETO_ARP
                                            .getValue(),
                            "0");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorFondoSolidario
     * 
     */
    public void cambiarValorFondoSolidario() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.VALOR_FONDO_SOLIDARIDAD_ARP
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.VALOR_FONDO_SOLIDARIDAD_ARP
                                            .getValue(),
                            "0");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TotalAfiliados
     * 
     */
    public void cambiarTotalAfiliados() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        RiesgosControladorEnum.TOTAL_AFILIADOS_RIESGOS
                                        .getValue())) {
            registro.getCampos().put(
                            RiesgosControladorEnum.TOTAL_AFILIADOS_RIESGOS
                                            .getValue(),
                            "0");
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton eliminar en la vista
     *
     * 
     */
    public void oprimireliminar() {
        // <CODIGO_DESARROLLADO>
        eliminarReg(registro);
        cargarRegistro(null, ACCION_INSERTAR);
        cargarDatosRegistro();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * metodo que toma los valores ingresados en el formulario
     */
    public void cargarValores() {
        valorAportes = Double
                        .parseDouble(registro.getCampos()
                                        .get(RiesgosControladorEnum.TOTAL_APORTES_ARP
                                                        .getValue())
                                        .toString());
        valorIncapacidades = Double
                        .parseDouble(registro.getCampos()
                                        .get(RiesgosControladorEnum.VALOR_TOTAL_INCAP_PAGADASARP
                                                        .getValue())
                                        .toString());

        valorAportesPagados = Double
                        .parseDouble(registro.getCampos()
                                        .get(RiesgosControladorEnum.VALOR_AP_PAGADOS_OTROS_RIESGOS
                                                        .getValue())
                                        .toString());

        valorInteresesMora = Double
                        .parseDouble(registro.getCampos()
                                        .get(RiesgosControladorEnum.VALOR_INTERESES_MORA_ARP
                                                        .getValue())
                                        .toString());

        valorPeriodoAnterior = Double
                        .parseDouble(registro.getCampos()
                                        .get(RiesgosControladorEnum.VALOR_SALDOPERIODOANTERIORARP
                                                        .getValue())
                                        .toString());
    }

    /**
     * metodo que valida la suma de los subtotales aportes y total
     * pago riesgos
     */
    public void sumarSubTotalAportes() {

        cargarValores();
        double subTotalAportes = valorAportes - valorIncapacidades
            - valorAportesPagados + valorInteresesMora;

        registro.getCampos().put(RiesgosControladorEnum.SUBTOTAL_APORTES_ARP
                        .getValue(), subTotalAportes);

        if (!"0".equals(registro.getCampos()
                        .get(RiesgosControladorEnum.VALOR_SALDOPERIODOANTERIORARP
                                        .getValue())
                        .toString())) {

            double valorTotal = subTotalAportes - valorPeriodoAnterior;
            registro.getCampos().put(RiesgosControladorEnum.VALOR_TOTAL_NETO_ARP
                            .getValue(), valorTotal);

        }
        else {
            registro.getCampos().put(RiesgosControladorEnum.VALOR_TOTAL_NETO_ARP
                            .getValue(), subTotalAportes);
        }

    }

    /**
     * Metodo que realiza operacion de restar los valores de aportes
     */
    public void restarValoresAportes() {

        cargarValores();
        double valorNetoAportes = valorAportes - valorIncapacidades
            - valorAportesPagados;

        double fondoSolidario = valorAportes * 1 / 100;

        double valorFondoSolidario = SysmanFunciones.redondear(fondoSolidario,
                        0);

        registro.getCampos()
                        .put(RiesgosControladorEnum.VALOR_FONDO_SOLIDARIDAD_ARP
                                        .getValue(), valorFondoSolidario);

        registro.getCampos().put(RiesgosControladorEnum.VALOR_TOTAL_COTIZACION
                        .getValue(), valorNetoAportes);

        if (!"0".equals(registro.getCampos()
                        .get(RiesgosControladorEnum.SUBTOTAL_APORTES_ARP
                                        .getValue())
                        .toString())
            || !"0".equals(registro.getCampos()
                            .get(RiesgosControladorEnum.SUBTOTAL_APORTES_ARP
                                            .getValue()))) {
            sumarSubTotalAportes();
        }
        else {
            registro.getCampos().put(RiesgosControladorEnum.SUBTOTAL_APORTES_ARP
                            .getValue(), valorNetoAportes);
            registro.getCampos().put(RiesgosControladorEnum.VALOR_TOTAL_NETO_ARP
                            .getValue(), valorNetoAportes);
        }

    }

    /**
     * Metodo que cargar valores por defecto al ingresar un nuevo
     * registro
     */
    public void cargarDatosRegistro() {
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        ano);
        registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                        mes);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        registro.getCampos()
                        .put(RiesgosControladorEnum.TIPO_ADMINISTRADORA
                                        .getValue(), "03");
        registro.getCampos().put(RiesgosControladorEnum.NIT.getValue(),
                        nitFondo);
        registro.getCampos().put(
                        RiesgosControladorEnum.CODIGO_FONDO.getValue(),
                        codigoFondo);
        registro.getCampos().put(
                        RiesgosControladorEnum.NOMBRE_FONDO.getValue(),
                        nombreFondo);
        registro.getCampos().put(
                        RiesgosControladorEnum.TOTAL_APORTES_ARP
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.VALOR_TOTAL_INCAP_PAGADASARP
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.VALOR_AP_PAGADOS_OTROS_RIESGOS
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.VALOR_TOTAL_COTIZACION
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.DIAS_MORA_ARP.getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.VALOR_INTERESES_MORA_ARP
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.SUBTOTAL_APORTES_ARP
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.VALOR_SALDOPERIODOANTERIORARP
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.VALOR_TOTAL_NETO_ARP
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.VALOR_FONDO_SOLIDARIDAD_ARP
                                        .getValue(),
                        "0");
        registro.getCampos().put(
                        RiesgosControladorEnum.TOTAL_AFILIADOS_RIESGOS
                                        .getValue(),
                        "0");
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.MES.getName(), mes);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        param.put(RiesgosControladorEnum.CODIGO_FONDO.getValue(), codigoFondo);
        param.put(RiesgosControladorEnum.NIT.getValue(), nitFondo);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursalFondo);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            Registro rsAutoliquidacion = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RiesgosControladorUrlEnum.URL227
                                                                            .getValue())
                                            .getUrl(), param));
            if ("0".equals(rsAutoliquidacion.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString())) {
                cargarRegistro(null, ACCION_INSERTAR);

                cargarDatosRegistro();
            }
            else {
                cargarRegistro(parametrosListado, ACCION_MODIFICAR);
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        restarValoresAportes();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        proceso);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursalFondo);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(GeneralParameterEnum.MES.getName());
            registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
            registro.getCampos()
                            .remove(RiesgosControladorEnum.TIPO_ADMINISTRADORA
                                            .getValue());
            registro.getCampos().remove(
                            RiesgosControladorEnum.CODIGO_FONDO.getValue());
            registro.getCampos().remove(RiesgosControladorEnum.NIT.getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.ID_DE_PROCESO.getName());

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable codigoFondo
     * 
     * @return codigoFondo
     */
    public String getCodigoFondo() {
        return codigoFondo;
    }

    /**
     * Asigna la variable codigoFondo
     * 
     * @param codigoFondo
     * Variable a asignar en codigoFondo
     */
    public void setCodigoFondo(String codigoFondo) {
        this.codigoFondo = codigoFondo;
    }

    /**
     * Retorna la variable nitFondo
     * 
     * @return nitFondo
     */
    public String getNitFondo() {
        return nitFondo;
    }

    /**
     * Asigna la variable nitFondo
     * 
     * @param nitFondo
     * Variable a asignar en nitFondo
     */
    public void setNitFondo(String nitFondo) {
        this.nitFondo = nitFondo;
    }

    /**
     * Retorna la variable sucursalFondo
     * 
     * @return sucursalFondo
     */
    public String getSucursalFondo() {
        return sucursalFondo;
    }

    /**
     * Asigna la variable sucursalFondo
     * 
     * @param sucursalFondo
     * Variable a asignar en sucursalFondo
     */
    public void setSucursalFondo(String sucursalFondo) {
        this.sucursalFondo = sucursalFondo;
    }

    /**
     * Retorna la variable nombreFondo
     * 
     * @return nombreFondo
     */
    public String getNombreFondo() {
        return nombreFondo;
    }

    /**
     * Asigna la variable nombreFondo
     * 
     * @param nombreFondo
     * Variable a asignar en nombreFondo
     */
    public void setNombreFondo(String nombreFondo) {
        this.nombreFondo = nombreFondo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
