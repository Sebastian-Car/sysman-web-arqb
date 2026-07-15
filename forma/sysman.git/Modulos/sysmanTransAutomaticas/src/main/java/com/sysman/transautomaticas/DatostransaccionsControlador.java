/*-
 * DatostransaccionsControlador.java
 *
 * 1.0
 *
 * 24/09/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.transautomaticas;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.reportes.ComprobantesContPresReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.transautomaticas.ejb.EjbTransAutomaticasCeroRemote;
import com.sysman.transautomaticas.enums.DatosTransaccionControladorEnum;
import com.sysman.transautomaticas.enums.DatosTransaccionControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Formulario que permite ingresar los datos de transacciones
 *
 * @version 1.0, 24/09/2018
 * @author jgomezp
 *
 * @version 2.0 06/11/2018
 * @author asana
 *
 * Según indicaciones Yolima - Henry P 1. En el procedimiento FC_VALIDAR_TRANSACCION se validan los terceros de los detalles se configuren en los detalles del comprobante contable dado que se estaba
 * heredando en del header. 2. En el procedimiento FC_VALIDAR_TRANSACCION Cuando no se encontraba ultimo comprobante del tipo configurado se generaba error al aumentar el consecutivo dado que este
 * venía null 3. En el procedimiento FC_VALIDAR_TRANSACCION Cuando se consultaba en la tabla SALDO_AUX_CONTABLE, Se filtra por tercero dado que se mostraban mas de un registro 4. Se reubica actualizar
 * detalles de transacción del botonDetalles() al ActualizarAntes() 5. AL seleccionar el tipo de transaccion se permita crear solo para los que tengan movimiento.
 *
 */
@ManagedBean
@ViewScoped
public class DatostransaccionsControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private final String modulo;
    private final String nombreCompania;
    private int ano;
    private String tipo;
    private int numero;
    private String retorno;
    private String usuario;

    private boolean bloquear;
    private boolean actiImpreso;
    private boolean actiConfirma;
    private boolean bloqDetalle;
    private boolean mostrarEdicion;
    private boolean validarCentroCosto;
    private boolean validarAuxiliar;
    private boolean validarReferencia;
    private boolean validarFuente;
    private boolean validarTercero;
    private boolean bloqPpto;
    private long consecutivo;

    private String tipoComp;
    public static final String FORMAT = "FORMATO";

    private ComprobantesContPresReporteador comprobantesContPresReporteador;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Lista el ano
     */
    private List<Registro> listaAno;
    /**
     * Lista el tipo
     */
    private RegistroDataModelImpl listaTipo;
    /**
     * Lista el codigo
     */
    private RegistroDataModelImpl listaCodigo;
    /**
     * Listo el tercero
     */
    private RegistroDataModelImpl listaTercero;
    /**
     * Lista el auxiliar
     */
    private RegistroDataModelImpl listaAuxiliar;
    /**
     * Lista el centro de costo
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Lista la referencia
     */
    private RegistroDataModelImpl listaReferencia;
    /**
     * Lista la fuente recurso
     */
    private RegistroDataModelImpl listaFuenteRecurso;

    private RegistroDataModelImpl listaTipoGasto;

    private RegistroDataModelImpl listaConcepto;

    private RegistroDataModelImpl listaMedioPago;

    private RegistroDataModelImpl listaCuentaBancos;

    /**
     * Crea una nueva instancia de DatostransaccionsControlador
     */
    @EJB
    private EjbTransAutomaticasCeroRemote ejbTransAutomaticas;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @SuppressWarnings("unchecked")
    public DatostransaccionsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        ano = SysmanFunciones.ano(new Date());
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();

        try
        {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada
                                .get("parametroTransaccion");

            }
            actiImpreso = true;
            numFormulario = GeneralCodigoFormaEnum.DATOS_TRANSACCIONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

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
        cargarListaTipo();
        cargarListaTercero();
        cargarListaAno();
        cargarListaMediosDePago();
        cargarListaConceptosDian();
        cargarListaTipoGasto();

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>

    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.TRANSACCIONES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos()
    {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    /**
     *
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DatosTransaccionControladorUrlEnum.URL4
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaTipo
     *
     */
    public void cargarListaTipo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL1719
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
    
     *
     */
    public void cargarListaCodigo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL172
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        registro.getCampos();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.TIPO.getName()));
        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());

    }

    /**
     * } Carga la lista listaTercero
     *
     */
    public void cargarListaTercero()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL140
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    /**
     *
     * Carga la lista listaAuxiliar
     *
     */
    public void cargarListaAuxiliar()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL230
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));
        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCentro_Costo
     *
     */
    public void cargarListaCentroCosto()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL200
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));
        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferencia()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL130
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
     *
     */
    public void cargarListaFuenteRecurso()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL340
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaTipoGasto()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL2144
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoGasto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaConceptosDian()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL2145
                                                        .getValue());

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaMediosDePago()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL2146
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaMedioPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCuentaBancos()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL161
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        listaCuentaBancos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTipo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("TIPONOMBRE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

        listaCodigo = null;
        cargarListaCodigo();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * @throws SystemException
     */

    public void seleccionarFilaCodigo(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        bloqPpto = (boolean) registroAux.getCampos().get("AFECTAPPTO");
        if ((boolean) registroAux.getCampos().get("MOVIMIENTOI"))
        {

            registro.getCampos().put(
                            DatosTransaccionControladorEnum.NUMERO_MODELO
                                            .getValue(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName()));
            registro.getCampos().put(
                            DatosTransaccionControladorEnum.NOMBRECOD
                                            .getValue(),
                            registroAux.getCampos().get(
                                            GeneralParameterEnum.DESCRIPCION
                                                            .getName()));

            try
            {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                DatosTransaccionControladorUrlEnum.URL1720
                                                                .getValue());
                Registro registroAX = null;

                Map<String, Object> parametros = new TreeMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.ANO.getName(), registro
                                .getCampos()
                                .get(GeneralParameterEnum.ANO.getName()));
                parametros.put(GeneralParameterEnum.TIPO.getName(),
                                registro.getCampos().get("TIPO"));
                parametros.put(GeneralParameterEnum.NUMERO.getName(),
                                registro.getCampos().get(
                                                DatosTransaccionControladorEnum.NUMERO_MODELO
                                                                .getValue()));

                registroAX = RegistroConverter.toRegistro(
                                requestManager.get(urlBean.getUrl(),
                                                parametros));

                registro.getCampos().put(
                                GeneralParameterEnum.CENTRO_COSTO.getName(),
                                registroAX.getCampos().get(
                                                GeneralParameterEnum.CENTRO_COSTO
                                                                .getName()));
                registro.getCampos().put(
                                GeneralParameterEnum.AUXILIAR.getName(),
                                registroAX.getCampos().get(
                                                GeneralParameterEnum.AUXILIAR
                                                                .getName()));
                registro.getCampos().put(
                                GeneralParameterEnum.FUENTE_RECURSO.getName(),
                                registroAX.getCampos().get(
                                                GeneralParameterEnum.FUENTE_RECURSO
                                                                .getName()));
                registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                                registroAX.getCampos().get(
                                                GeneralParameterEnum.TERCERO
                                                                .getName()));
                registro.getCampos().put(
                                GeneralParameterEnum.REFERENCIA.getName(),
                                registroAX.getCampos().get(
                                                GeneralParameterEnum.REFERENCIA
                                                                .getName()));
                registro.getCampos().put(
                                GeneralParameterEnum.SUCURSAL.getName(),
                                registroAX.getCampos().get(
                                                GeneralParameterEnum.SUCURSAL
                                                                .getName()));
                registro.getCampos().put(
                                GeneralParameterEnum.CONCEPTO.getName(),
                                registroAX.getCampos().get(
                                                GeneralParameterEnum.CONCEPTO
                                                                .getName()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.GASTOTIPO
                                                .getValue(),
                                registroAX.getCampos().get(
                                                DatosTransaccionControladorEnum.GASTOTIPO
                                                                .getValue()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.MEDIO
                                                .getValue(),
                                registroAX.getCampos().get(
                                                DatosTransaccionControladorEnum.MEDIO
                                                                .getValue()));
                registro.getCampos().put("DESCRIPCION",
                                registroAX.getCampos().get("DESCRIPCION"));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.NOMBRETERCERO
                                                .getValue(),
                                registroAX.getCampos().get(
                                                DatosTransaccionControladorEnum.NOMBRETERCERO
                                                                .getValue()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.AUXILIARNOMBRE
                                                .getValue(),
                                registroAX.getCampos().get(
                                                DatosTransaccionControladorEnum.AUXILIARNOMBRE
                                                                .getValue()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.NOMBRECONCEPTO
                                                .getValue(),
                                registroAX.getCampos().get(
                                                DatosTransaccionControladorEnum.NOMBRECONCEPTO
                                                                .getValue()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.NOMBRETIPOGASTO
                                                .getValue(),
                                registroAX.getCampos().get(
                                                DatosTransaccionControladorEnum.NOMBRETIPOGASTO
                                                                .getValue()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.NOMBREMEDIOPAGO
                                                .getValue(),
                                registroAX.getCampos().get(
                                                DatosTransaccionControladorEnum.NOMBREMEDIOPAGO
                                                                .getValue()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.REFERENCIANOMBRE
                                                .getValue(),
                                registroAX.getCampos().get(
                                                DatosTransaccionControladorEnum.REFERENCIANOMBRE
                                                                .getValue()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.CENTROCOSTONOMBRE
                                                .getValue(),
                                registroAX
                                                .getCampos()
                                                .get(DatosTransaccionControladorEnum.CENTROCOSTONOMBRE
                                                                .getValue()));
                registro.getCampos().put(
                                DatosTransaccionControladorEnum.FUENTERECURSONOMBRE
                                                .getValue(),
                                registroAX
                                                .getCampos()
                                                .get(DatosTransaccionControladorEnum.FUENTERECURSONOMBRE
                                                                .getValue()));

            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else
        {

            registro.getCampos()
                            .put(DatosTransaccionControladorEnum.NUMERO_MODELO
                                            .getValue(), "");
            registro.getCampos().put(DatosTransaccionControladorEnum.NOMBRECOD
                            .getValue(), "");

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4249"));
        }

    }

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
        registro.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.NOMBRETERCERO
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliar(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AUXILIAR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.AUXILIARNOMBRE
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCentro_Costo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CENTRO_COSTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.CENTROCOSTONOMBRE
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaReferencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("REFERENCIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.REFERENCIANOMBRE
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaFuenteRecurso
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecurso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_RECURSO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.FUENTERECURSONOMBRE
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
    }

    public void seleccionarFilaTipoGasto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.GASTOTIPO.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.NOMBRETIPOGASTO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    public void seleccionarFilaMedioPago(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.MEDIO.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.NOMBREMEDIOPAGO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    public void seleccionarFilaConcepto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("RESOLUCION",
                        registroAux.getCampos().get(GeneralParameterEnum.FORMATO
                                        .getName()));
        registro.getCampos().put(
                        DatosTransaccionControladorEnum.NOMBRECONCEPTO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    public void seleccionarFilaCuentaBancos(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBANCOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRECUENTABANCOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /*
     *
     * Metodo ejecutado al oprimir el boton confirmar en la vista
     *
     */
    public void oprimirconfirmar()
    {
        // <CODIGO_DESARROLLADO>

        agregarRegistroNuevo(false);

        try
        {

            String retornoF = ejbTransAutomaticas.validarTransaccion(compania,
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.ANO
                                                            .getName())
                                            .toString()),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.TIPO
                                                            .getName())
                                            .toString(),
                            registro.getCampos().get(
                                            DatosTransaccionControladorEnum.NUMERO_MODELO
                                                            .getValue())
                                            .toString(),
                            new BigInteger(registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName())
                                            .toString()),
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(retornoF);

            if ("COMPROBANTE CREADO CORRECTAMENTE".equals(retornoF))
            {

                Map<String, Object> parametrosComp = new HashMap<>();

                parametrosComp.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametrosComp.put(GeneralParameterEnum.ANO.getName(),
                                ano);
                parametrosComp.put(GeneralParameterEnum.TIPO.getName(), registro
                                .getCampos()
                                .get(GeneralParameterEnum.TIPO.getName()));
                parametrosComp.put("NUMEROMODELO",
                                registro.getCampos().get(
                                                DatosTransaccionControladorEnum.NUMERO_MODELO
                                                                .getValue()));
                parametrosComp.put(GeneralParameterEnum.NUMERO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName()));

                Registro registroComprobante;
                registroComprobante = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                DatosTransaccionControladorUrlEnum.URL003
                                                                                .getValue())
                                                .getUrl(), parametrosComp));

                registro.getCampos().put(
                                GeneralParameterEnum.TIPO_CPTE.getName(),
                                registroComprobante.getCampos().get(
                                                GeneralParameterEnum.TIPO_CPTE
                                                                .getName()));
                registro.getCampos().put(
                                GeneralParameterEnum.COMPROBANTE.getName(),
                                registroComprobante.getCampos().get(
                                                GeneralParameterEnum.COMPROBANTE
                                                                .getName()));

            }

        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        DatosTransaccionControladorEnum.COMPROBANTE.toString())
            &&
            SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            DatosTransaccionControladorEnum.TIPO_CPTE
                                            .toString()))
        {
            actiConfirma = false;
            actiImpreso = true;
        }
        else
        {
            actiConfirma = true;
            actiImpreso = false;
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton AfectarPpto en la vista
     */
    public void oprimirAfectarPpto()
    {
        redireccionarSub(GeneralCodigoFormaEnum.COMPROBANTE_PPTAL_AFECTAR_CONTROLADOR
                        .getCodigo());
    }

    public void actualizarDetalles()
    {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        DatosTransaccionControladorEnum.COMPROBANTE.toString())
            &&
            SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            DatosTransaccionControladorEnum.TIPO_CPTE
                                            .toString()))
        {
            try
            {

                String salida = ejbTransAutomaticas.modificarTransaccionesRete(
                                compania,
                                Integer.parseInt(registro.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                                .toString()),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.TIPO
                                                                .getName())
                                                .toString(),
                                registro.getCampos().get(
                                                DatosTransaccionControladorEnum.NUMERO_MODELO
                                                                .getValue())
                                                .toString(),
                                new BigInteger(registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName())
                                                .toString()),
                                SessionUtil.getUser().getCodigo());
                if (!"".equals(salida.trim()))
                {
                    JsfUtil.agregarMensajeAlerta(salida);
                }
            }
            catch (NumberFormatException | SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir()
    {

        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    public void generarInforme(FORMATOS format)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("ano", ano);
            reemplazar.put("tipoCpte", registro.getCampos()
                            .get(GeneralParameterEnum.TIPO_CPTE.getName()));
            reemplazar.put("numeroPptoInicial",
                            registro.getCampos().get(
                                            GeneralParameterEnum.COMPROBANTE
                                                            .getName()));
            reemplazar.put("numeroPptoFinal",
                            registro.getCampos().get(
                                            GeneralParameterEnum.COMPROBANTE
                                                            .getName()));
            reemplazar.put("nombreCompania", nombreCompania);

            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_ANO", ano);
            parametros.put("PR_TIPO", registro.getCampos()
                            .get(GeneralParameterEnum.TIPO_CPTE.getName()));
            parametros.put("PR_COMPROBANTE",
                            registro.getCampos().get(
                                            GeneralParameterEnum.COMPROBANTE
                                                            .getName()));

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.TIPO_CPTE
                                                            .getName()));

            Map<String, Object> regTipoComprobante;

            regTipoComprobante = requestManager
                            .get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DatosTransaccionControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), param)
                            .getFields();

            String reporte = regTipoComprobante.get(FORMAT).toString();

            Map<String, Object> valores = new HashMap<>();
            valores.put("informe", reporte);
            valores.put("formato", format);
            valores.put("lote", false);

            archivoDescarga = comprobantesContPresReporteador
                            .generarInforme(valores, parametros, reemplazar);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + " " + e.getMessage());
        }
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        comprobantesContPresReporteador = new ComprobantesContPresReporteador(
                        ejbSysmanUtil);

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtnDetalle en la vista
     *
     */
    public void oprimirBtnDetalle()
    {
        redireccionarSub(GeneralCodigoFormaEnum.DTRANSACCIONES_CONTROLADOR
                        .getCodigo());

    }

    /**
     * permite redireccionar a los subformularios
     *
     * @param codFormu
     */
    private void redireccionarSub(int codFormu)
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString());
        param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.TIPO.getName()));
        param.put("NUMEROMODELO",
                        registro.getCampos().get(
                                        DatosTransaccionControladorEnum.NUMERO_MODELO
                                                        .getValue())
                                        .toString());
        param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()).toString());
        param.put("confirmado", mostrarEdicion);
        param.put("rowTransaccion", registro.getLlave());
        param.put(GeneralParameterEnum.VALOR.getName(),
                        registro.getCampos().get(GeneralParameterEnum.VALOR.getName()).toString());

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(codFormu));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    public void cambiarAno()
    {
        cargarListaCentroCosto();
        cargarListaFuenteRecurso();
        cargarListaReferencia();
        cargarListaCodigo();
        cargarListaAuxiliar();
        cargarListaCuentaBancos();
    }

    /**
     * Metodo ejecutado al cambiar el control NumeroAfectar
     *
     */
    @Override
    public void cargarRegistro()
    {

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        new Date());
        precargarRegistro();
        cargarListaCentroCosto();
        cargarListaAuxiliar();
        cargarListaFuenteRecurso();
        cargarListaReferencia();
        cargarListaCuentaBancos();
        cargarListaTipo();
        cargarListaCodigo();
        if (accion.equals(ACCION_INSERTAR))
        {
            actiImpreso = true;
            bloqDetalle = true;
            actiConfirma = true;
            cargarNombres();
            bloquear = false;

        }

        if (accion.equals(ACCION_VER))
        {
            actiImpreso = false;

        }

        if (accion.equals(ACCION_MODIFICAR))
        {
            bloqDetalle = false;
            actiImpreso = true;
            bloquear = true;
            validarIgualdadConstante();

            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            DatosTransaccionControladorEnum.COMPROBANTE
                                            .toString())
                &&
                SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                DatosTransaccionControladorEnum.TIPO_CPTE
                                                .toString()))
            {
                actiConfirma = false;
                actiImpreso = true;
                mostrarEdicion = true;
            }
            else
            {
                actiConfirma = true;
                actiImpreso = false;
                mostrarEdicion = false;
                bloqPpto = true;
                accion = ACCION_VER;
            }

            try
            {
                validarPermisos();
            }
            catch (Exception ex)
            {
                logger.error(ex.getMessage(), ex);
                SessionUtil.redireccionarMenuPermisos();
            }
        }

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "TRANSACCIONES",
                            "COMPANIA =''" + compania + "''" +
                                " AND ANO = " + ano +
                                " AND TIPO = ''"
                                + registro.getCampos()
                                                .get(GeneralParameterEnum.TIPO
                                                                .getName())
                                + "''" +
                                " AND NUMERO_MODELO = ''"
                                + registro.getCampos().get(
                                                DatosTransaccionControladorEnum.NUMERO_MODELO
                                                                .getValue())
                                + "''",
                            GeneralParameterEnum.NUMERO.getName(),
                            "1");
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            consecutivo);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {

        try
        {
            /**
             * Se valida que la transacción se permita modificar de lo contrario enviar el error
             */
            ejbTransAutomaticas.controlarTransaccion(
                            compania,
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.ANO
                                                            .getName())
                                            .toString()),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.TIPO
                                                            .getName())
                                            .toString(),
                            registro.getCampos().get(
                                            DatosTransaccionControladorEnum.NUMERO_MODELO
                                                            .getValue())
                                            .toString(),
                            new BigInteger(registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName())
                                            .toString()));

        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }

        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.AUXILIARNOMBRE
                                        .getValue());
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.REFERENCIANOMBRE
                                        .getValue());
        registro.getCampos().remove(
                        DatosTransaccionControladorEnum.FUENTERECURSONOMBRE
                                        .getValue());
        registro.getCampos().remove(
                        DatosTransaccionControladorEnum.CENTROCOSTONOMBRE
                                        .getValue());
        registro.getCampos().remove("NUMEROAAFECTAR"); //
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.NOMBRETERCERO
                                        .getValue());
        registro.getCampos().remove("NUMEROAFECTARPPTO"); //
        registro.getCampos().remove("TIPONOMBRE");
        registro.getCampos().remove(
                        DatosTransaccionControladorEnum.NOMBRECOD.getValue());
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.NOMBRETIPOGASTO
                                        .getValue());
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.NOMBREMEDIOPAGO
                                        .getValue());
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.NOMBRECONCEPTO
                                        .getValue());
        registro.getCampos().remove("TODOSLOSCENTROS");
        registro.getCampos().remove("NOMBRECUENTABANCOS");
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.NOMBRECONCEPTO
                                        .getValue());
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.NOMBRETIPOGASTO
                                        .getValue());
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.NOMBREMEDIOPAGO
                                        .getValue());
        registro.getCampos()
                        .remove(DatosTransaccionControladorEnum.AFECTAPPTO
                                        .getValue());
        if (css != null)
        {
            registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.NUMERO.getName());
            registro.getCampos()
                            .remove(DatosTransaccionControladorEnum.NUMERO_MODELO
                                            .getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.ANO.getName());

            registro.getCampos()
                            .remove(GeneralParameterEnum.TIPO.getName());

        }

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues()
    {

        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     *
     */
    @Override
    public boolean eliminarAntes()
    {

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     *
     */
    @Override
    public boolean eliminarDespues()
    {

        return true;
    }

    public void cargarNombres()
    {
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        SysmanConstantes.CONS_CENTRO);
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        SysmanConstantes.CONS_AUXILIAR);
        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        SysmanConstantes.CONS_REFERENCIA);
        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        SysmanConstantes.CONS_FUENTE);
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        SysmanConstantes.CONS_TERCERO);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        SysmanConstantes.CONS_SUCURSAL);

        // Centro Costo
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL20
                                                        .getValue());
        consultarNombre(urlBean,
                        DatosTransaccionControladorEnum.CENTROCOSTONOMBRE
                                        .getValue(),
                        SysmanConstantes.CONS_CENTRO);
        // Auxiliar
        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL23
                                                        .getValue());
        consultarNombre(urlBean,
                        DatosTransaccionControladorEnum.AUXILIARNOMBRE
                                        .getValue(),
                        SysmanConstantes.CONS_AUXILIAR);

        // Fuente Recurso
        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL34
                                                        .getValue());
        consultarNombre(urlBean,
                        DatosTransaccionControladorEnum.FUENTERECURSONOMBRE
                                        .getValue(),
                        SysmanConstantes.CONS_FUENTE);

        // Referencia
        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL13
                                                        .getValue());
        consultarNombre(urlBean,
                        DatosTransaccionControladorEnum.REFERENCIANOMBRE
                                        .getValue(),
                        SysmanConstantes.CONS_REFERENCIA);

        // Tercero
        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosTransaccionControladorUrlEnum.URL14
                                                        .getValue());
        consultarNombre(urlBean,
                        DatosTransaccionControladorEnum.NOMBRETERCERO
                                        .getValue(),
                        SysmanConstantes.CONS_TERCERO);
    }

    public void consultarNombre(UrlBean urlServ, String campoAsignar,
        String contante)
    {

        try
        {

            Map<String, Object> parametrosAux = new HashMap<>();

            parametrosAux.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosAux.put(GeneralParameterEnum.ANO.getName(), ano);
            parametrosAux.put(GeneralParameterEnum.CODIGO.getName(), contante);
            parametrosAux.put(GeneralParameterEnum.SUCURSAL.getName(),
                            SysmanConstantes.CONS_SUCURSAL);
            parametrosAux.put("NIT", SysmanConstantes.CONS_TERCERO);

            Registro registroAuxiliares;
            registroAuxiliares = RegistroConverter.toRegistro(
                            requestManager.get(urlServ.getUrl(),
                                            parametrosAux));

            registro.getCampos().put(campoAsignar, registroAuxiliares
                            .getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName()));

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void validarIgualdadConstante()
    {

        if (SysmanConstantes.CONS_CENTRO
                        .equals(registro.getCampos().get("CENTRO_COSTO")))
        {
            validarCentroCosto = true;
        }
        else
        {
            validarCentroCosto = false;
        }

        if (SysmanConstantes.CONS_AUXILIAR
                        .equals(registro.getCampos().get("AUXILIAR")))
        {
            validarAuxiliar = true;
        }
        else
        {
            validarAuxiliar = false;
        }

        if (SysmanConstantes.CONS_REFERENCIA
                        .equals(registro.getCampos().get("REFERENCIA")))
        {
            validarReferencia = true;
        }
        else
        {
            validarReferencia = false;
        }

        if (SysmanConstantes.CONS_FUENTE
                        .equals(registro.getCampos().get("FUENTE_RECURSO")))
        {
            validarFuente = true;
        }
        else
        {
            validarFuente = false;
        }

        if (SysmanConstantes.CONS_TERCERO
                        .equals(registro.getCampos().get("TERCERO")))
        {
            validarTercero = true;
        }
        else
        {
            validarTercero = false;
        }

    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna la lista listaAno
     *
     * @return listaAno
     */
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     *
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaTipo
     *
     * @return listaTipo
     */
    public RegistroDataModelImpl getListaTipo()
    {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     *
     * @param listaTipo
     * Variable a asignar en listaTipo
     */
    public void setListaTipo(RegistroDataModelImpl listaTipo)
    {
        this.listaTipo = listaTipo;
    }

    /**
     * Retorna la lista listaCodigo
     *
     * @return listaCodigo
     */
    public RegistroDataModelImpl getListaCodigo()
    {
        return listaCodigo;
    }

    /**
     * Asigna la lista listaCodigo
     *
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigo(RegistroDataModelImpl listaCodigo)
    {
        this.listaCodigo = listaCodigo;
    }

    /**
     * Retorna la lista listaTercero
     *
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero()
    {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     *
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero)
    {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaAuxiliar
     *
     * @return listaAuxiliar
     */
    public RegistroDataModelImpl getListaAuxiliar()
    {
        return listaAuxiliar;
    }

    /**
     * Asigna la lista listaAuxiliar
     *
     * @param listaAuxiliar
     * Variable a asignar en listaAuxiliar
     */
    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar)
    {
        this.listaAuxiliar = listaAuxiliar;
    }

    /**
     * Retorna la lista listaCentroCosto
     *
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto()
    {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     *
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto)
    {
        this.listaCentroCosto = listaCentroCosto;
    }

    /**
     * Retorna la lista listaReferencia
     *
     * @return listaReferencia
     */
    public RegistroDataModelImpl getListaReferencia()
    {
        return listaReferencia;
    }

    /**
     * Asigna la lista listaReferencia
     *
     * @param listaReferencia
     * Variable a asignar en listaReferencia
     */
    public void setListaReferencia(RegistroDataModelImpl listaReferencia)
    {
        this.listaReferencia = listaReferencia;
    }

    /**
     * Retorna la lista listaFuenteRecurso
     *
     * @return listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecurso()
    {
        return listaFuenteRecurso;
    }

    /**
     * Asigna la lista listaFuenteRecurso
     *
     * @param listaFuenteRecurso
     * Variable a asignar en listaFuenteRecurso
     */
    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso)
    {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    public boolean isBloquear()
    {
        return bloquear;
    }

    public void setBloquear(boolean bloquear)
    {
        this.bloquear = bloquear;
    }

    public boolean isActiImpreso()
    {
        return actiImpreso;
    }

    public void setActiImpreso(boolean actiImpreso)
    {
        this.actiImpreso = actiImpreso;
    }

    public boolean isActiConfirma()
    {
        return actiConfirma;
    }

    public void setActiConfirma(boolean actiConfirma)
    {
        this.actiConfirma = actiConfirma;
    }

    public boolean isBloqDetalle()
    {
        return bloqDetalle;
    }

    public void setBloqDetalle(boolean bloqDetalle)
    {
        this.bloqDetalle = bloqDetalle;
    }

    public RegistroDataModelImpl getListaTipoGasto()
    {
        return listaTipoGasto;
    }

    public void setListaTipoGasto(RegistroDataModelImpl listaTipoGasto)
    {
        this.listaTipoGasto = listaTipoGasto;
    }

    public RegistroDataModelImpl getListaConcepto()
    {
        return listaConcepto;
    }

    public void setListaConcepto(RegistroDataModelImpl listaConcepto)
    {
        this.listaConcepto = listaConcepto;
    }

    public RegistroDataModelImpl getListaMedioPago()
    {
        return listaMedioPago;
    }

    public void setListaMedioPago(RegistroDataModelImpl listaMedioPago)
    {
        this.listaMedioPago = listaMedioPago;
    }

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public RegistroDataModelImpl getListaCuentaBancos()
    {
        return listaCuentaBancos;
    }

    public void setListaCuentaBancos(RegistroDataModelImpl listaCuentaBancos)
    {
        this.listaCuentaBancos = listaCuentaBancos;
    }

    public boolean isValidarCentroCosto()
    {
        return validarCentroCosto;
    }

    public void setValidarCentroCosto(boolean validarCentroCosto)
    {
        this.validarCentroCosto = validarCentroCosto;
    }

    public boolean isValidarAuxiliar()
    {
        return validarAuxiliar;
    }

    public void setValidarAuxiliar(boolean validarAuxiliar)
    {
        this.validarAuxiliar = validarAuxiliar;
    }

    public boolean isValidarReferencia()
    {
        return validarReferencia;
    }

    public void setValidarReferencia(boolean validarReferencia)
    {
        this.validarReferencia = validarReferencia;
    }

    public boolean isValidarFuente()
    {
        return validarFuente;
    }

    public void setValidarFuente(boolean validarFuente)
    {
        this.validarFuente = validarFuente;
    }

    public boolean isValidarTercero()
    {
        return validarTercero;
    }

    public void setValidarTercero(boolean validarTercero)
    {
        this.validarTercero = validarTercero;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public int getNumero()
    {
        return numero;
    }

    public void setNumero(int numero)
    {
        this.numero = numero;
    }

    public String getRetorno()
    {
        return retorno;
    }

    public void setRetorno(String retorno)
    {
        this.retorno = retorno;
    }

    public String getCompania()
    {
        return compania;
    }

    public String getUsuario()
    {
        return usuario;
    }

    public void setUsuario(String usuario)
    {
        this.usuario = usuario;
    }

    public EjbTransAutomaticasCeroRemote getEjbTransAutomaticas()
    {
        return ejbTransAutomaticas;
    }

    public void setEjbTransAutomaticas(
        EjbTransAutomaticasCeroRemote ejbTransAutomaticas)
    {
        this.ejbTransAutomaticas = ejbTransAutomaticas;
    }

    public String getModulo()
    {
        return modulo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isMostrarEdicion()
    {
        return mostrarEdicion;
    }

    public void setMostrarEdicion(boolean mostrarEdicion)
    {
        this.mostrarEdicion = mostrarEdicion;
    }

    public long getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(long consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getTipoComp()
    {
        return tipoComp;
    }

    public void setTipoComp(String tipoComp)
    {
        this.tipoComp = tipoComp;
    }

    public ComprobantesContPresReporteador getComprobantesContPresReporteador()
    {
        return comprobantesContPresReporteador;
    }

    public void setComprobantesContPresReporteador(
        ComprobantesContPresReporteador comprobantesContPresReporteador)
    {
        this.comprobantesContPresReporteador = comprobantesContPresReporteador;
    }

    public EjbSysmanUtilRemote getEjbSysmanUtil()
    {
        return ejbSysmanUtil;
    }

    public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil)
    {
        this.ejbSysmanUtil = ejbSysmanUtil;
    }

    public String getNombreCompania()
    {
        return nombreCompania;
    }

    /**
     * @return the bloqPpto
     */
    public boolean isBloqPpto()
    {
        return bloqPpto;
    }

    /**
     * @param bloqPpto
     * the bloqPpto to set
     */
    public void setBloqPpto(boolean bloqPpto)
    {
        this.bloqPpto = bloqPpto;
    }

}
