/*-
 * DTransaccionModelosControlador.java
 *
 * 1.0
 *
 * 20/09/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.transautomaticas;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.transautomaticas.ejb.EjbTransAutomaticasCeroRemote;
import com.sysman.transautomaticas.enums.DTransaccionModelosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Se agregan los detalles de las transacciones de modelo.
 *
 * @version 1.0, 20/09/2018
 * @author asana
 *
 * @version 2.0 06/11/2018
 * @author asana
 *
 * Según indicaciones Yolima - Henry P 1. Se quita del combo Clase a Pedir la opción "Cuenta de presupuesto" 2. El numero de transacción se genera solo con el consecutivo que ingresa el usuario, no
 * por consecutivo 4. Solo los movimientos que tienen check de Movimiento activo se permite configurr centros de costo y detalles. 5. Se modifica el orden en la grilla siguiendo estructura de numero
 * tipo varchar.
 */
@ManagedBean
@ViewScoped
public class DTransaccionModelosControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     *
     *
     ****** FAVOR NO ACTUALIZAR LA FORMA DADO QUE EN PROPIEDAD "BORDE" SE COLOCA "#AAAAAA none 1px; text-transform:uppercase" PARA QUE SE VALIDE QUE LAS LETRAS QUE INGRESE EN LAS FORMULAS SE MUESTREN AL
     * USUARIO EN MAYÚSCULAS ******
     */
    private final String compania;
    private int ano;
    private String tipo;
    private String numero;
    private String descripcion;
    private boolean camposBloqueo;

    Map<String, Object> parametroTransaccion;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listacentroCosto;
    private List<Registro> listaClaseCuenta;
    private RegistroDataModelImpl listaTipoRetencionNombre;
    private RegistroDataModelImpl listaReferencia;
    private RegistroDataModelImpl listaFuenteRecurso;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaCodigoCuenta;
    private RegistroDataModelImpl listacuentaPptal;
    private boolean formula;
    private boolean aplica;
    private boolean formulaBase;
    private boolean alerta;
    private boolean valida;
    private boolean formulaC;
    private boolean aplicaC;
    private boolean formulaBaseC;
    private boolean alertaC;
    private boolean validaC;
    private String mensaje;
    private boolean formulaBloqueo;
    private boolean ocultarClaseCuenta;
    private boolean bloqueoMostrar;
    private boolean bloqueoRetencion;

    @EJB
    private EjbTransAutomaticasCeroRemote ejbTransAutomaticas;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de DTransaccionModelosControlador
     */
    public DTransaccionModelosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DTRANSACCIONMODELO_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {

                parametroTransaccion = (Map<String, Object>) parametrosEntrada
                                .get("rowTransaccion");
                ano = (int) parametrosEntrada
                                .get(GeneralParameterEnum.ANO.getName());
                tipo = (String) parametrosEntrada
                                .get(GeneralParameterEnum.TIPO.getName());
                numero = (String) parametrosEntrada
                                .get(GeneralParameterEnum.NUMERO.getName());
                descripcion = (String) parametrosEntrada.get(
                                GeneralParameterEnum.DESCRIPCION.getName());
            }
            validarPermisos();

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaClaseCuenta();
        cargarListaTipoRetencion();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.D_TRANSACCIONMODELO;
        buscarLlave();
        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos()
    {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numero);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAuxiliar
     */
    public void cargarListaAuxiliar()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DTransaccionModelosControladorUrlEnum.URL1816
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listacentroCosto
     */
    public void cargarListacentroCosto()
    {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.nvl(registro.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()),
                                        " "));

        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DTransaccionModelosControladorUrlEnum.URL1812
                                                        .getValue());

        listacentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaClaseCuenta
     */
    public void cargarListaClaseCuenta()
    {

        try
        {

            listaClaseCuenta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DTransaccionModelosControladorUrlEnum.URL1817
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaTipoRetencion
     */
    public void cargarListaTipoRetencion()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DTransaccionModelosControladorUrlEnum.URL1819
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaTipoRetencionNombre = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NOMBRERETE");

    }

    /**
     *
     * Carga la lista listaReferencia
     */
    public void cargarListaReferencia()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DTransaccionModelosControladorUrlEnum.URL1814
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaFuenteRecurso
     */
    public void cargarListaFuenteRecurso()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DTransaccionModelosControladorUrlEnum.URL1815
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaTercero
     */
    public void cargarListaTercero()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DTransaccionModelosControladorUrlEnum.URL12012
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    /**
     *
     * Carga la lista listaCodigoCuenta
     */
    public void cargarListaCodigoCuenta()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DTransaccionModelosControladorUrlEnum.URL1818
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        listaCodigoCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCuentaPptal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DTransaccionModelosControladorUrlEnum.URL2143
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        listacuentaPptal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarClaseAPedir()
    {
        // <CODIGO_DESARROLLADO>
        if ("V".equals(registro.getCampos().get("CLASEAPEDIR"))
            && "P".equals(registro.getCampos().get("PEDIRMOSTRAR")))
        {
            formulaBloqueo = true;
            registro.getCampos().put("FORMULA", "");
            formula = false;
            formulaC = true;
        }
        else
        {

            formulaBloqueo = false;

        }
        if ("P".equals(registro.getCampos().get("CLASEAPEDIR"))
            || "C".equals(registro.getCampos().get("CLASEAPEDIR")))
        {
            ocultarClaseCuenta = true;
        }
        else
        {
            ocultarClaseCuenta = false;
            registro.getCampos().put("CLASECUENTA", "");
        }
        if ("M".equals(registro.getCampos().get("PEDIRMOSTRAR")))
        {
            registro.getCampos().put("CLASEAPEDIR", "V");
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPedirMostrar()
    {
        // <CODIGO_DESARROLLADO>
        if ("V".equals(registro.getCampos().get("CLASEAPEDIR"))
            && "P".equals(registro.getCampos().get("PEDIRMOSTRAR")))
        {
            formulaBloqueo = true;
            registro.getCampos().put("FORMULA", "");
            formula = false;
            formulaC = true;
        }
        else
        {
            if (ACCION_MODIFICAR.equals(accion))
            {
                formulaBloqueo = false;
            }
            else
            {
                formulaBloqueo = true;
            }
        }

        if ("M".equals(registro.getCampos().get("PEDIRMOSTRAR")))
        {
            registro.getCampos().put("CLASEAPEDIR", "V");
            bloqueoMostrar = true;

        }
        else
        {

            bloqueoMostrar = false;
        }
        bloqueoMostrar = true;

        if (!"R".equals(registro.getCampos().get("PEDIRMOSTRAR")))
        {
            registro.getCampos().put("TIPORETENCION", null);
            registro.getCampos().put("NOMBRERETE", null);
            registro.getCampos().put("CONCEPTORETENCION", null);
            bloqueoRetencion = true;
        }
        else
        {
            bloqueoRetencion = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFormula()
    {

        formulaC = validarFormula(
                        registro.getCampos().get("FORMULA").toString(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ORDEN.getName())
                                        .toString());
        if (!formulaC)
        {
            formula = true;
        }
        else
        {
            formula = false;
        }

    }

    public void cambiarCondValidacion()
    {

        validaC = validarFormula(
                        registro.getCampos().get("COND_VALIDACION").toString(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ORDEN.getName())
                                        .toString());
        if (!validaC)
        {
            valida = true;
        }
        else
        {
            valida = false;
        }

    }

    public void cambiarcondAlerta()
    {
        alertaC = validarFormula(
                        registro.getCampos().get("COND_ALERTA").toString(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ORDEN.getName())
                                        .toString());
        if (!alertaC)
        {
            alerta = true;
        }
        else
        {
            alerta = false;
        }

    }

    public void cambiarformulaBase()
    {
        formulaBaseC = validarFormula(
                        registro.getCampos().get("FORMULA_BASE").toString(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ORDEN.getName())
                                        .toString());

        if (!formulaBaseC)
        {
            formulaBase = true;
        }
        else
        {
            formulaBase = false;
        }

    }

    public void cambiaraplicacions()
    {
        aplicaC = validarFormula(
                        registro.getCampos().get("APLICACION").toString(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ORDEN.getName())
                                        .toString());
        if (!aplicaC)
        {
            aplica = true;
        }
        else
        {
            aplica = false;
        }

    }

    public boolean validarFormula(String formulas, String orden)
    {

        if (!SysmanFunciones.strValidarFormula(formulas.toUpperCase(),
                        orden, false))
        {

            mensaje = idioma.getString("TB_TB4221");
            return false;

        }
        else
        {

            return true;
        }
    }

    public boolean validarInsertUpdate()
    {
        boolean salida = true;
        if (formulaC && aplicaC && formulaBaseC && alertaC && validaC)
        {
            salida = true;
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4259"));
            salida = false;
        }

        if ("R".equals(registro.getCampos().get("PEDIRMOSTRAR")) &&
            (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "TIPORETENCION")
                || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                "CONCEPTORETENCION")))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4260"));
            salida = false;
        }
        return salida;

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("NOMBRETERCERO", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    public void seleccionarFilaAuxiliar(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBREAUXILIAR", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilaFuenteRecurso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRERECURSO", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilaReferencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBREREFERENCIA", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilacuentaPptal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_PPTAL", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRECUENTAPPTAL", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilacentroCosto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRECENTROCOSTO", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilaTipoRetencionNombre(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPORETENCION", registroAux.getCampos()
                        .get(GeneralParameterEnum.TIPO.getName()));

        registro.getCampos().put("NOMBRERETE",
                        registroAux.getCampos().get("NOMBRERETE"));
        registro.getCampos().put("CONCEPTORETENCION",
                        registroAux.getCampos().get("RETENCIONCONCEPTO"));
        registro.getCampos().put("CODIGO_CUENTA",
                        registroAux.getCampos().get("CUENTA_CREDITO"));
        registro.getCampos().put("NOMBRECUENTA", registroAux.getCampos().get("NOMBRECTA"));
        registro.getCampos().put("NATURALEZA",
                        registroAux.getCampos().get("NATURALEZA"));
        registro.getCampos().put("CUENTA_PPTAL", registroAux.getCampos().get("CUENTA_PPTAL"));
        registro.getCampos().put("NOMBRECUENTAPPTAL",
                        registroAux.getCampos().get("NOMBREPPTAL"));

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCuenta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCuenta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_CUENTA", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRECUENTA", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put("NATURALEZA",
                        registroAux.getCampos().get("NATURALEZA"));
        registro.getCampos().put("CUENTA_PPTAL",
                        registroAux.getCampos().get("CUENTA_PPTAL"));
        registro.getCampos().put("NOMBRECUENTAPPTAL",
                        registroAux.getCampos().get("NOMBREPPTAL"));
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public void ejecutarrcCerrar()
    {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("parametroTransaccion", parametroTransaccion);
        parametros.put("ano", ano);
        parametros.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametros.put(GeneralParameterEnum.NUMERO.getName(), numero);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.TRANSACCIONMODELO_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }

    /**
     * Metodo que valida si la cuenta contable es obligatoria o no
     *
     * @return
     */
    public boolean validarCuentaContable()
    {

        if (registro.getCampos().get("PEDIRMOSTRAR").equals("R")
            || registro.getCampos().get("CLASEAPEDIR").equals("I"))
        {
            return true;
        }
        return false;
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // campos visible de los campos de error
        formula = false;
        aplica = false;
        formulaBase = false;
        alerta = false;
        valida = false;

        // validación de cada uno de los campos de fórmula
        formulaC = true;
        aplicaC = true;
        formulaBaseC = true;
        alertaC = true;
        validaC = true;

        ocultarClaseCuenta = false;
        // formulaBloqueo = true;

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipo);
        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numero);
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        descripcion);

        cargarListaTercero();
        cargarListaCodigoCuenta();
        cargarListaAuxiliar();
        cargarListacentroCosto();
        cargarListaReferencia();
        cargarListaFuenteRecurso();
        cargarListaCuentaPptal();

        try
        {

            Map<String, Object> paramet = new HashMap<>();
            paramet.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramet.put(GeneralParameterEnum.ANO.getName(), ano);
            paramet.put(GeneralParameterEnum.TIPO.getName(), tipo);
            paramet.put(GeneralParameterEnum.NUMERO.getName(), numero);

            Registro registroA = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DTransaccionModelosControladorUrlEnum.URL2144
                                                                            .getValue())
                                            .getUrl(), paramet));

            registro.getCampos().put("TIPONOMBRE",
                            registroA.getCampos().get("TIPONOMBRE"));

            if (ACCION_MODIFICAR.equals(accion))
            {
                camposBloqueo = false;

                if ("V".equals(registro.getCampos().get("CLASEAPEDIR"))
                    && "P".equals(registro.getCampos().get("PEDIRMOSTRAR")))
                {
                    formulaBloqueo = true;
                }
                else
                {
                    formulaBloqueo = false;
                }

                if ("P".equals(registro.getCampos().get("CLASEAPEDIR"))
                    || "C".equals(registro.getCampos().get("CLASEAPEDIR")))
                {
                    ocultarClaseCuenta = true;
                }
                else
                {
                    ocultarClaseCuenta = false;
                }

            }

            if (ACCION_INSERTAR.equals(accion))
            {
                camposBloqueo = true;
                formulaBloqueo = true;
                registro.getCampos().put("PEDIRMOSTRAR", "P");
                registro.getCampos().put("CLASEAPEDIR", "V");

                registro.getCampos().put(
                                GeneralParameterEnum.CENTRO_COSTO.getName(),
                                registroA.getCampos().get(
                                                GeneralParameterEnum.CENTRO_COSTO
                                                                .getName()));
                registro.getCampos().put("NOMBRECENTROCOSTO",
                                registroA.getCampos().get("NOMBRECENTROCOSTO"));
                registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                                registroA.getCampos().get(
                                                GeneralParameterEnum.TERCERO
                                                                .getName()));
                registro.getCampos().put("NOMBRETERCERO",
                                registroA.getCampos().get("NOMBRETERCERO"));
                registro.getCampos().put(
                                GeneralParameterEnum.SUCURSAL.getName(),
                                registroA.getCampos().get(
                                                GeneralParameterEnum.SUCURSAL
                                                                .getName()));
                registro.getCampos().put(
                                GeneralParameterEnum.AUXILIAR.getName(),
                                registroA.getCampos().get(
                                                GeneralParameterEnum.AUXILIAR
                                                                .getName()));
                registro.getCampos().put("NOMBREAUXILIAR",
                                registroA.getCampos().get("NOMBREAUXILIAR"));
                registro.getCampos().put(
                                GeneralParameterEnum.REFERENCIA.getName(),
                                registroA.getCampos().get(
                                                GeneralParameterEnum.REFERENCIA
                                                                .getName()));
                registro.getCampos().put("NOMBREREFERENCIA",
                                registroA.getCampos().get("NOMBREREFERENCIA"));
                registro.getCampos().put(
                                GeneralParameterEnum.FUENTE_RECURSO.getName(),
                                registroA.getCampos().get(
                                                GeneralParameterEnum.FUENTE_RECURSO
                                                                .getName()));
                registro.getCampos().put("NOMBRERECURSO",
                                registroA.getCampos().get("NOMBRERECURSO"));

            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if (!validarInsertUpdate())
        {
            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.ORDEN.getName()))
        {
            try
            {
                registro.getCampos().put(GeneralParameterEnum.ORDEN.getName(),
                                ejbTransAutomaticas.generarConsecutivoOrden(
                                                compania,
                                                ano, tipo, numero,
                                                GenericUrlEnum.D_TRANSACCIONMODELO
                                                                .getTable()));
            }
            catch (NumberFormatException | SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
                return false;

            }
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if (!validarInsertUpdate())
        {
            return false;
        }
        if (!validarCuentaContable() && SysmanFunciones
                        .nvl(registro.getCampos().get("CODIGO_CUENTA"), "")
                        .toString().isEmpty())
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4268"));
            return false;
        }
        registro.getCampos().remove("NOMBREAUXILIAR");
        registro.getCampos().remove("NOMBREREFERENCIA");
        registro.getCampos().remove("NOMBRECENTROCOSTO");
        registro.getCampos().remove("NOMBRERECURSO");
        registro.getCampos().remove("NOMBRETERCERO");
        registro.getCampos().remove("DESCRIPCION");
        registro.getCampos().remove("NOMBRECUENTA");
        registro.getCampos().put("FORMULAINTERNA", "");
        registro.getCampos().put("VALORCREDITO", "0");
        registro.getCampos().put("VALORDEBITO", "0");
        registro.getCampos().remove("NOMBRECUENTAPPTAL");
        registro.getCampos().remove("TIPONOMBRE");
        registro.getCampos().remove("NOMBRERETE");

        registro.getCampos().put("FORMULA", SysmanFunciones
                        .nvl(registro.getCampos().get("FORMULA"), "").toString()
                        .toUpperCase());
        registro.getCampos().put("COND_VALIDACION", SysmanFunciones
                        .nvl(registro.getCampos().get("COND_VALIDACION"), "")
                        .toString().toUpperCase());
        registro.getCampos().put("COND_ALERTA", SysmanFunciones
                        .nvl(registro.getCampos().get("COND_ALERTA"), "")
                        .toString().toUpperCase());
        registro.getCampos().put("FORMULA_BASE", SysmanFunciones
                        .nvl(registro.getCampos().get("FORMULA_BASE"), "")
                        .toString().toUpperCase());
        registro.getCampos().put("APLICACION", SysmanFunciones
                        .nvl(registro.getCampos().get("APLICACION"), "")
                        .toString().toUpperCase());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            ejbTransAutomaticas.validarOrden(compania, ano,
                            tipo, numero,
                            registro.getCampos()
                                            .get(GeneralParameterEnum.ORDEN
                                                            .getName())
                                            .toString());
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaClaseCuenta
     *
     * @return listaClaseCuenta
     */
    public List<Registro> getListaClaseCuenta()
    {
        return listaClaseCuenta;
    }

    /**
     * Asigna la lista listaClaseCuenta
     *
     * @param listaClaseCuenta
     * Variable a asignar en listaClaseCuenta
     */
    public void setListaClaseCuenta(List<Registro> listaClaseCuenta)
    {
        this.listaClaseCuenta = listaClaseCuenta;
    }

    public RegistroDataModelImpl getListaTipoRetencionNombre()
    {
        return listaTipoRetencionNombre;
    }

    public void setListaTipoRetencion(
        RegistroDataModelImpl listaTipoRetencionNombre)
    {
        this.listaTipoRetencionNombre = listaTipoRetencionNombre;
    }

    public RegistroDataModelImpl getListaReferencia()
    {
        return listaReferencia;
    }

    public void setListaReferencia(RegistroDataModelImpl listaReferencia)
    {
        this.listaReferencia = listaReferencia;
    }

    public RegistroDataModelImpl getListaFuenteRecurso()
    {
        return listaFuenteRecurso;
    }

    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso)
    {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    public RegistroDataModelImpl getListaTercero()
    {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero)
    {
        this.listaTercero = listaTercero;
    }

    public RegistroDataModelImpl getListaCodigoCuenta()
    {
        return listaCodigoCuenta;
    }

    public void setListaCodigoCuenta(RegistroDataModelImpl listaCodigoCuenta)
    {
        this.listaCodigoCuenta = listaCodigoCuenta;
    }

    public RegistroDataModelImpl getListaAuxiliar()
    {
        return listaAuxiliar;
    }

    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar)
    {
        this.listaAuxiliar = listaAuxiliar;
    }

    public RegistroDataModelImpl getListacentroCosto()
    {
        return listacentroCosto;
    }

    public void setListacentroCosto(RegistroDataModelImpl listacentroCosto)
    {
        this.listacentroCosto = listacentroCosto;
    }

    public RegistroDataModelImpl getListacuentaPptal()
    {
        return listacuentaPptal;
    }

    public void setListacuentaPptal(RegistroDataModelImpl listacuentaPptal)
    {
        this.listacuentaPptal = listacuentaPptal;
    }

    public boolean isFormula()
    {
        return formula;
    }

    public void setFormula(boolean formula)
    {
        this.formula = formula;
    }

    public boolean isAplica()
    {
        return aplica;
    }

    public void setAplica(boolean aplica)
    {
        this.aplica = aplica;
    }

    public boolean isFormulaBase()
    {
        return formulaBase;
    }

    public void setFormulaBase(boolean formulaBase)
    {
        this.formulaBase = formulaBase;
    }

    public boolean isAlerta()
    {
        return alerta;
    }

    public void setAlerta(boolean alerta)
    {
        this.alerta = alerta;
    }

    public boolean isValida()
    {
        return valida;
    }

    public void setValida(boolean valida)
    {
        this.valida = valida;
    }

    public String getMensaje()
    {
        return mensaje;
    }

    public void setMensaje(String mensaje)
    {
        this.mensaje = mensaje;
    }

    public boolean isCamposBloqueo()
    {
        return camposBloqueo;
    }

    public void setCamposBloqueo(boolean camposBloqueo)
    {
        this.camposBloqueo = camposBloqueo;
    }

    public boolean isFormulaBloqueo()
    {
        return formulaBloqueo;
    }

    public void setFormulaBloqueo(boolean formulaBloqueo)
    {
        this.formulaBloqueo = formulaBloqueo;
    }

    public boolean isOcultarClaseCuenta()
    {
        return ocultarClaseCuenta;
    }

    public void setOcultarClaseCuenta(boolean ocultarClaseCuenta)
    {
        this.ocultarClaseCuenta = ocultarClaseCuenta;
    }

    public boolean isBloqueoMostrar()
    {
        return bloqueoMostrar;
    }

    public void setBloqueoMostrar(boolean bloqueoMostrar)
    {
        this.bloqueoMostrar = bloqueoMostrar;
    }

    public boolean isBloqueoRetencion()
    {
        return bloqueoRetencion;
    }

    public void setBloqueoRetencion(boolean bloqueoRetencion)
    {
        this.bloqueoRetencion = bloqueoRetencion;
    }

}
