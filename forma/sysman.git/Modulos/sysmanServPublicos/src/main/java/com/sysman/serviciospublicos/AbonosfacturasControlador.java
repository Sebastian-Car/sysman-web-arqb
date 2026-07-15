/*-
 * AbonosfacturasControlador.java
 *
 * 1.0
 *
 * 23/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSieteRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.AbonosfacturasControladorEnum;
import com.sysman.serviciospublicos.enums.AbonosfacturasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase que contiene la migracion de la pestana Abonos del formulario Factura. Contiene dos subformulario: SubgormularioAbonos y SubformularioAbonosAnterior
 *
 * @version 1.0, 23/09/2016
 * @author acaceres
 *
 * @version 2.0, 11/05/2017
 * @author mzanguna - Se refactoriza y desacopla funciones dejando lo que corresponde en el trigger de abonos
 *
 * @version 2.1, 23/08/2017
 * @author mzanguna - Se ajusta el metodo oprimircmdDetalle, tomando los valores de año y periodo desde el Registro.
 */
@ManagedBean
@ViewScoped

public class AbonosfacturasControlador extends BeanBaseDatosAcme
{

    /**
     * Constante que almacena la compania de la factura
     */
    private final String compania;

    /**
     * Contante que almacena el modulo de servicios publicos
     */
    private final String modulo;

    /**
     * Constante que almacenara la cadena "PERIODO"
     */
    private final String periodoC;

    /**
     * Constante que almacenara el valor de la cadena "PAGOTERCERIZADO"
     */
    private final String pagoTercerizadoAC;

    /**
     * Constante que almacenara el valor de la cadena "PAGOCONVENIOS"
     */
    private final String pagoConveniosC;

    /**
     * Constante que almacenara la cadena "CODIGO"
     */
    private final String codigoC;
    /**
     * Constante que almacenara el nombre de la tabla "SP_ABONOS"
     */
    private final String spAbonos;

    /**
     * Constante que almacenara el formato en el que se presentaran los valores totales de los subformularios
     */
    private final String formatoMoneda;

    /**
     * Constante que almacenara la cadena "bancoperproceso"
     */
    private final String bancoPerProcesoC;

    /**
     * Constante que almacenara la cadena "autorizarBorrado"
     */
    private final String autorizarBorradoC;

    /**
     * Atributo que almacenara el nombre del usuario seleccionado para autorizar el abono
     */
    private String usuarioAutorizaCB;

    /**
     * Atributo que almacenara la clave que ingreso el usuario que esta autorizando un abono
     */
    private String claveAutoriza;

    /**
     * Constante que almacenara la cadena "VALORANTFACT"
     */
    private final String valorAntFactAC;

    /**
     * Constante que almacenara la cadena "FECHA"
     */
    private final String fechaAC;

    /**
     * Constante que almacenara la cadena "BANCO"
     */
    private final String bancoAC;

    /**
     * Constante que almacenara la cadena "CODIGORUTA"
     */
    private final String coidgoRutaAC;

    /**
     * Constante que almacenara la cadena "CONSECUTIVO"
     */
    private final String consecutivoAC;

    /**
     * Constante que almacenara la cadena "SP_USUARIO"
     */
    private final String spUsuarioAC;
    /**
     * Constante que almacenara la cadena "ABONOS PRIORIDAD TERCERIZADO Y CONVENIOS"
     */
    private final String parAbonosPrioridadAC;

    /**
     * Constante que almacenara la cadena "FACTURA SERVICIOS NO RECLAMADOS"
     */
    private final String facturaServiciosC;

    /**
     * Constante que almacenara la cadena "TOTAL"
     */
    private final String totalC;

    /**
     * Constante que almacenara la cadena "FECHA_PREPARACION"
     */
    private final String fechaPreparacionC;

    /**
     * Constante que almacenara la cadena "TB_TB1821"
     */
    private final String confParametro;

    /**
     * Constante que almacenara la cadena "txtFimm"
     */
    private final String txtFimmMinC;

    /**
     * Constante que almacenara la cadena "codigoInterno"
     */
    private final String codigoInternoMinC;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacenara el numero del ciclo que se trae desde el formulario FacturaIntegrado
     */

    private int ciclo;

    /**
     * Atributo que almacenara el codigo de Ruta que se trae desde el formulario Factura Integrado
     */
    private String codigoRuta;

    /**
     * Atributo que almacenara el ano que se trae desde el formulario Factura integrado
     */
    private int ano;

    /**
     * Atributo almacenara el periodo que se trae desde el formulario Factura Integrado
     */
    private String periodo;

    /**
     * Atributo que almacenara el periodo que se trae desde el formulario Factura Integrado
     */
    private String bancoperproceso;

    /**
     * Atributo que almacenara el codigo Interno que se trae desde el formulario Factura Integrado
     */
    private String codigoInterno;

    /**
     * Atributo que almacenara si los planos son financiables, se trae desde el formulario Factura Integrado
     */
    private String txtFimm = "";

    /**
     * Atributo que almacenara los periodos no cobrados, se trae desde el formulario Factura Integrado
     */
    private String periodosNoCobroFac;

    /**
     * Se trae desde el formulario Factura de la tabla SP_USUARIOS Indica la lectura que trae el codigo de ruta seleccionado
     *
     */
    private String lectura = "0";

    /**
     * Atributo que almacenara el valor total del subformulario Abonos del Periodo Actual.
     */
    private String totalValor;

    /**
     * Atributo que almacenara el total de la factura en el periodo actual. Se trae desde el formulario de factura
     */
    private double totFacturaPerActual;

    /**
     * Atributo que almacenara el consecutivo del registro a almacenar en el subformulario Abonos
     */
    private int consecutivo;

    /**
     * Atributo que almacenara si se puede borrar informacion del abono, se trae desde el formulario Factura Integrado
     */
    private boolean autorizarBorrado = true;

    /**
     * Atributo que almacenara el valor total del subformulario Abonos de periodos anteriores.
     */
    private String totalValorS;

    /**
     * Atributo que almacenara el mensaje mostrado en el dialogo numero Abonos
     */
    private String mensajeAbonos;

    /**
     * Dialogo dgValorAb que permite ingresar el valor del abonoab solicitado en el botón imprimir.
     */
    private boolean verNuevoValor;

    /**
     * Atributo que permite que el dialogo dialogoAutorizaAbono sea visible esto para autorizar los abonos.
     */
    private boolean verDialogoAutorizaAbono;

    /**
     * Atributo que almacenara el nuevo valor del abono que ingresara el usuario.
     */
    private double nuevoValor;

    /**
     * Atributo que almacenara el monto del abono al seleccionar un usuario en el dialogo
     */
    private double abonoMonto;

    /**
     * Atributo que almacenara el valor del abono del formulario Abono pide que se dejo como un dialogo.
     */
    private double abono;

    /**
     * Atributo que almacenara la clave del abono al seleccionar un usuario en el dialogo
     */
    private String abonoClave;

    /**
     * Atributo que almacenara el valor del campo Total Pago Tercerizado del subformulario Abonos del Periodo Actual
     */
    private double totalPagoTercerizadoA;

    /**
     *
     */
    private boolean bloqueaValorA = false;

    /**
     * Atributo que permite volver visible el boton de detalle en el subformulario Abonos
     */
    private boolean cmdDetalleVisible = true;

    /**
     * Atributo que permite volver visible el boton de detalle en el subformulario abonos anterior.
     */
    private boolean detalleAntVisible = true;

    /**
     *
     */
    private boolean txtPagoconveniosVisible = false;

    /**
     *
     */
    private boolean txtPagoTercerizadoVisible = false;

    /**
     *
     */
    private boolean txtTotalTercerizadoVisible = false;

    /**
     *
     */
    private boolean clearServiceVisible = false;

    /**
     * Atributo usado para mostrar el dialogo del numero de abonos
     */
    private boolean dialogoNumeroAbonos = false;
    int conteo = 0;

    /**
     * Atributo que permite volver visible el DialogoIndAutorizado al eliminar un registro del subformulario Abonos
     */
    private boolean dialogoIndAutorizadoVisible = false;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que almacenara los bancos del subformulario Abonos del periodo actual
     */
    private RegistroDataModelImpl listabancoA;

    /**
     * Lista que almacenara los bancos de la grilla del subformulario Abonos del periodo actual
     */
    private RegistroDataModelImpl listabancoAE;

    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>

    /**
     * Lista de Registros que almacenara la lista del subformulario Abonos del Periodo actual
     */
    private List<Registro> listaSubformularioabonos;

    /**
     * Lista de Registros que almacenara la lista del subformulario Abonos de Periodos Anteriores
     */
    private List<Registro> listaSubformularioabonosanterior;

    /**
     * Registro que almacenara la lista de los usuarios que autorizan los abonos
     */
    private RegistroDataModelImpl listausuarioAutoriza;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSubsubformularioAbonos;
    private Registro registroSubSubformularioAbonosAnterior;

    @EJB
    private EjbServiciosPublicosSieteRemote ejbServiciosPublicosSiete;

    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    public AbonosfacturasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoC = "CODIGO";
        spAbonos = "SP_ABONOS";
        periodoC = "PERIODO";
        totalC = "TOTAL";
        fechaPreparacionC = "FECHA_PREPARACION";
        pagoTercerizadoAC = "PAGOTERCERIZADO";
        pagoConveniosC = "PAGOCONVENIOS";
        formatoMoneda = "$ #,##0.00";
        fechaAC = "FECHA";
        bancoAC = "BANCO";
        confParametro = "TB_TB1821";
        coidgoRutaAC = "CODIGORUTA";
        consecutivoAC = "CONSECUTIVO";
        spUsuarioAC = "SP_USUARIO";
        valorAntFactAC = "VALORANTFACT";
        parAbonosPrioridadAC = "ABONOS PRIORIDAD TERCERIZADO Y CONVENIOS ";
        facturaServiciosC = "FACTURA SERVICIOS NO RECLAMADOS";
        txtFimmMinC = "txtFimm";
        codigoInternoMinC = "codigoInterno";
        bancoPerProcesoC = "bancoperproceso";
        autorizarBorradoC = "autorizarBorrado";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.ABONOSFACTURAS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {

                /* Parametros que se traen del formulario Factura */
                ciclo = Integer.parseInt(parametrosEntrada.get(AbonosfacturasControladorEnum.CICLOMIN.getValue()).toString());
                codigoRuta = (String) parametrosEntrada.get("codigoruta");
                ano = Integer.parseInt(parametrosEntrada.get("ano").toString());
                periodo = parametrosEntrada.get(AbonosfacturasControladorEnum.PERIODOMIN.getValue()).toString();
                bancoperproceso = parametrosEntrada.get(bancoPerProcesoC)
                                .toString().trim();
                codigoInterno = parametrosEntrada.get(codigoInternoMinC)
                                .toString();
                txtFimm = (String) parametrosEntrada.get(txtFimmMinC);
                autorizarBorrado = (boolean) parametrosEntrada
                                .get(autorizarBorradoC);

                totFacturaPerActual = Double
                                .parseDouble(SysmanFunciones
                                                .nvl(parametrosEntrada.get(AbonosfacturasControladorEnum.TOTFACTURA.getValue()), "0")
                                                .toString());
            }
            // <INI_ADICIONAL>
            registroSubsubformularioAbonos = new Registro(
                            new HashMap<String, Object>());
            registroSubSubformularioAbonosAnterior = new Registro(
                            new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>

        cargarListabancoA();
        cargarListabancoAE();
        cargarListausuarioAutoriza();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubformularioabonos();
        cargarListaSubformularioabonosanterior();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubformularioabonos = null;
        listaSubformularioabonosanterior = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar()
    {
        tabla = spUsuarioAC;
        buscarLlave();
        registroSubsubformularioAbonos.getCampos().put("ANO", ano);
        registroSubsubformularioAbonos.getCampos().put(periodoC, periodo);
        registroSubsubformularioAbonos.getCampos().put(AbonosfacturasControladorEnum.VALOR.getValue(), 0);
        registroSubsubformularioAbonos.getCampos().put("VALORABONADOPERIODO",
                        0);
        registroSubsubformularioAbonos.getCampos().put(fechaAC,
                        new Date());
        registroSubsubformularioAbonos.getCampos().put(valorAntFactAC, 0);
        registroSubsubformularioAbonos.getCampos().put(pagoConveniosC, 0);
        registroSubsubformularioAbonos.getCampos().put(pagoTercerizadoAC, 0);
        registroSubsubformularioAbonos.getCampos().put(totalC, 0);
        iniciarListasSub();
        iniciarListas();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    @Override
    public void reasignarOrigenGrilla()
    {
        origenGrilla = "";
        if (listaInicial != null)
        {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null)
        {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    public void cargarListaSubformularioabonos()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(AbonosfacturasControladorEnum.CODRUTA.getValue(), codigoRuta);
            param.put(AbonosfacturasControladorEnum.ANO.getValue(), ano);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            listaSubformularioabonos = RegistroConverter.toListRegistro(requestManager
                            .getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_ABONOS.getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            spAbonos));

            UrlBean urlReg = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL63676.getValue());

            Registro rsTotal = RegistroConverter.toRegistro(requestManager.get(urlReg.getUrl(), param));

            totalValor = new java.text.DecimalFormat(formatoMoneda).format(
                            Double.parseDouble(SysmanFunciones
                                            .nvl(rsTotal.getCampos().get(AbonosfacturasControladorEnum.VALOR.getValue()), "0").toString()));

        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaSubformularioabonosanterior()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(AbonosfacturasControladorEnum.CODRUTA.getValue(), codigoRuta);
            param.put(AbonosfacturasControladorEnum.ANO.getValue(), ano);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            listaSubformularioabonosanterior = RegistroConverter.toListRegistro(requestManager
                            .getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL22467.getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            spAbonos));

            param.clear();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
            param.put(AbonosfacturasControladorEnum.ANO.getValue(), ano);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            UrlBean urlReg = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL27247.getValue());

            Registro rsTAbonoAnt = RegistroConverter.toRegistro(requestManager.get(urlReg.getUrl(), param));
            totalValorS = new java.text.DecimalFormat(formatoMoneda).format(
                            Double.parseDouble(SysmanFunciones.nvl(rsTAbonoAnt.getCampos().get("TOTABONOANT"), "0").toString()));

        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListabancoA()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL26301.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listabancoA = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);

    }

    public void cargarListabancoAE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL26301.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listabancoAE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);

    }

    /**
     * Carga la lista listausuarioAutoriza
     */
    public void cargarListausuarioAutoriza()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL30038.getValue());
        listausuarioAutoriza = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null,
                        true, codigoC);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control AnoA
     *
     */
    public void cambiarAnoA()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del campo VALOR, del subformulario Abonos del Periodo Actual
     */
    public void cambiarvalorA()
    {
        // <CODIGO_DESARROLLADO>
        // Atributo que almacenara el valor del campo VALORA del
        // subformulario Abonos del Periodo Actual
        String abonoAct;

        // Atributo que almacenara el valor que genera la funcion
        // FC_VALORPAGOTERCERIZADO
        double valorTercerizado;

        // Atributo que almacenara el valor que genera la funcion
        // FC_VALORPAGOCONVENIOS
        double valorConvenios;

        double minimoAbono;

        try
        {
            String parametro = ejbSysmanUtil.consultarParametro(compania, parAbonosPrioridadAC, modulo, new Date(), true);

            if (parametro == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString(confParametro)
                    + parAbonosPrioridadAC);
                return;
            }

            if ("SI".equals(parametro))
            {
                abonoAct = registroSubsubformularioAbonos.getCampos()
                                .get(AbonosfacturasControladorEnum.VALOR.getValue()).toString();

                valorConvenios = ejbServiciosPublicosDos.consultarValorPagoConvenios(compania, ciclo, codigoRuta, ano, periodo, "SI")
                                .doubleValue();

                valorTercerizado = ejbServiciosPublicosDos.consultarValorPagoTercerizado(compania, ciclo, codigoRuta, ano, periodo, "SI")
                                .doubleValue();

                minimoAbono = valorConvenios + valorTercerizado;

                if (Double.parseDouble(abonoAct) < minimoAbono)
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1803"));
                }
                else
                {
                    registroSubsubformularioAbonos
                                    .getCampos().put(AbonosfacturasControladorEnum.VALOR.getValue(),
                                                    Double.parseDouble(
                                                                    abonoAct)
                                                        - minimoAbono);
                    registroSubsubformularioAbonos.getCampos()
                                    .put(pagoConveniosC, valorConvenios);
                    registroSubsubformularioAbonos.getCampos()
                                    .put(pagoTercerizadoAC, valorTercerizado);

                    totalPagoTercerizadoA = (Double.parseDouble(abonoAct)
                        - minimoAbono) + valorConvenios + valorTercerizado;

                    registroSubsubformularioAbonos.getCampos().put(totalC,
                                    totalPagoTercerizadoA);

                }
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
     * Metodo que se ejecuta al momento de cambiar el valor en la grilla del subformulario abonos.
     *
     * @param rowNum:
     * Registro de la fila que esta seleccionada
     */
    public void cambiarvalorAC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        // Atributo que almacenara el valor del campo VALORA del
        // subformulario Abonos del Periodo Actual
        String abonoAct;

        // Atributo que almacenara el valor que genera la funcion
        // FC_VALORPAGOTERCERIZADO
        double valorTercerizado;

        // Atributo que almacenara el valor que genera la funcion
        // FC_VALORPAGOCONVENIOS
        double valorConvenios;

        double minimoAbono;

        try
        {
            String parametro = ejbSysmanUtil.consultarParametro(compania, parAbonosPrioridadAC, modulo, new Date(), true);

            if (parametro == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString(confParametro)
                    + parAbonosPrioridadAC);
                return;
            }

            if ("SI".equals(parametro))
            {
                abonoAct = listaSubformularioabonos.get(rowNum).getCampos()
                                .get(AbonosfacturasControladorEnum.VALOR.getValue()).toString();

                valorConvenios = ejbServiciosPublicosDos.consultarValorPagoConvenios(compania, ciclo, codigoRuta, ano, periodo, "SI")
                                .doubleValue();

                valorTercerizado = ejbServiciosPublicosDos.consultarValorPagoTercerizado(compania, ciclo, codigoRuta, ano, periodo, "SI")
                                .doubleValue();

                minimoAbono = valorConvenios + valorTercerizado;

                if (Double.parseDouble(abonoAct) < minimoAbono)
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1803"));
                }
                else
                {
                    listaSubformularioabonos.get(rowNum)
                                    .getCampos().put(AbonosfacturasControladorEnum.VALOR.getValue(),
                                                    Double.parseDouble(
                                                                    abonoAct)
                                                        - minimoAbono);
                    listaSubformularioabonos.get(rowNum).getCampos()
                                    .put(pagoConveniosC, valorConvenios);
                    listaSubformularioabonos.get(rowNum).getCampos()
                                    .put(pagoTercerizadoAC, valorTercerizado);

                    totalPagoTercerizadoA = (Double.parseDouble(abonoAct)
                        - minimoAbono) + valorConvenios + valorTercerizado;

                    listaSubformularioabonos.get(rowNum).getCampos().put(totalC,
                                    totalPagoTercerizadoA);

                }
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
     * Metodo ejecutado al cambiar el control AnoA en la fila seleccionada dentro de la grilla
     *
     */
    public void cambiarAnoAC()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se cambia la fecha en el subformulario abonos.
     */
    public void cambiarfechaA()
    {
        // <CODIGO_DESARROLLADO>
        if (!validarCambioFecha((Date) registroSubsubformularioAbonos.getCampos().get(fechaAC)))
        {
            return;
        }
    }

    /**
     * Metodo que se ejecuta cuando se modifica la fecha en la grilla del subformulario Abonos.
     *
     * @param rowNum:
     * registro de la fila que esta seleccionada
     */
    public void cambiarfechaAC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (!validarCambioFecha((Date) listaSubformularioabonos.get(rowNum).getCampos()
                        .get(fechaAC)))
        {
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que valida si al cambiar una fecha del abono es permitida o no
     */
    public boolean validarCambioFecha(Date fecha)
    {
        try
        {
            int anoFecha = SysmanFunciones.ano(fecha);
            int anoActual = SysmanFunciones
                            .ano(new Date());

            if (anoFecha != anoActual)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1804"));
                return false;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            UrlBean urlReg = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL28876.getValue());

            Registro rs;

            rs = RegistroConverter.toRegistro(requestManager.get(urlReg.getUrl(), param));

            if (rs != null)
            {
                Date rsFechaPreparacion = (Date) rs.getCampos()
                                .get(fechaPreparacionC);

                int diferenciaDias = SysmanFunciones.calcularDiferenciaDias(
                                fecha, rsFechaPreparacion);

                if (diferenciaDias >= 0)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1805"));
                    return false;
                }

            }

        }
        catch (SystemException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return true;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listausuarioAutoriza
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilausuarioAutoriza(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        usuarioAutorizaCB = registroAux.getCampos().get("CODIGO").toString();
        abonoMonto = Double.parseDouble(
                        registroAux.getCampos().get("ABONOMONTO").toString());
        abonoClave = registroAux.getCampos().get("ABONOCLAVE").toString();
    }

    public void seleccionarFilaPeriodoAE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("MES");
    }

    public void seleccionarFilabancoA(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubsubformularioAbonos.getCampos().put(bancoAC,
                        registroAux.getCampos().get(codigoC));
    }

    public void seleccionarFilabancoAE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(codigoC);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirLimpiarServicios()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSelectDetalleA(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton cmdDetalle
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimircmdDetalle(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        int anoDetalle = Integer.parseInt(reg.getCampos().get(GeneralParameterEnum.ANO.getName()).toString());

        String periodoDetalle = String.valueOf(reg.getCampos().get(GeneralParameterEnum.PERIODO.getName()));

        consecutivo = Integer.parseInt(reg.getCampos().get(consecutivoAC).toString());
        String[] campos = { AbonosfacturasControladorEnum.CICLOMIN.getValue(), "ano", AbonosfacturasControladorEnum.PERIODOMIN.getValue(),
                            AbonosfacturasControladorEnum.CODRUTAMINUS.getValue(),
                            AbonosfacturasControladorEnum.CONSECMINUS.getValue(), bancoPerProcesoC, codigoInternoMinC,
                            txtFimmMinC,
                            AbonosfacturasControladorEnum.ANIOACT.getValue(),
                            AbonosfacturasControladorEnum.PERIODOACT.getValue() };

        String[] valores = { String.valueOf(ciclo), String.valueOf(anoDetalle), periodoDetalle, codigoRuta,
                             String.valueOf(consecutivo), bancoperproceso,
                             codigoInterno, txtFimm == null ? "" : txtFimm,
                             String.valueOf(ano), periodo };

        SessionUtil.redireccionar(AbonosfacturasControladorEnum.RUTASUBDETALLE.getValue(), campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton cmdDetalleAnt
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimircmdDetalleAnt(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        int anoDetalle = Integer.parseInt(reg.getCampos().get(GeneralParameterEnum.ANO.getName()).toString());

        String periodoDetalle = String.valueOf(reg.getCampos().get(GeneralParameterEnum.PERIODO.getName()));

        consecutivo = Integer.parseInt(reg.getCampos().get(consecutivoAC).toString());
        String[] campos = { AbonosfacturasControladorEnum.CICLOMIN.getValue(), "ano", AbonosfacturasControladorEnum.PERIODOMIN.getValue(),
                            AbonosfacturasControladorEnum.CODRUTAMINUS.getValue(),
                            AbonosfacturasControladorEnum.CONSECMINUS.getValue(), bancoPerProcesoC, codigoInternoMinC,
                            txtFimmMinC,
                            AbonosfacturasControladorEnum.ANIOACT.getValue(),
                            AbonosfacturasControladorEnum.PERIODOACT.getValue() };

        String[] valores = { String.valueOf(ciclo), String.valueOf(anoDetalle), periodoDetalle, codigoRuta,
                             String.valueOf(consecutivo), bancoperproceso,
                             codigoInterno, txtFimm == null ? "" : txtFimm,
                             String.valueOf(ano), periodo };

        SessionUtil.redireccionar(AbonosfacturasControladorEnum.RUTASUBDETALLE.getValue(), campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton detalleAnt
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirdetalleAnt(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        consecutivo = Integer.parseInt(reg.getCampos().get(consecutivoAC).toString());
        String bancoAnt = reg.getCampos().get(bancoAC).toString();
        String[] campos = { AbonosfacturasControladorEnum.CICLOMIN.getValue(), "ano", AbonosfacturasControladorEnum.PERIODOMIN.getValue(),
                            AbonosfacturasControladorEnum.CODRUTAMINUS.getValue(),
                            AbonosfacturasControladorEnum.CONSECMINUS.getValue(), bancoPerProcesoC, codigoInternoMinC,
                            txtFimmMinC,
                            autorizarBorradoC };
        String[] valores = { String.valueOf(ciclo), String.valueOf(ano), periodo, codigoRuta,
                             String.valueOf(consecutivo), bancoAnt,
                             codigoInterno, txtFimm == null ? "" : txtFimm,
                             String.valueOf(autorizarBorrado) };

        SessionUtil.redireccionar(AbonosfacturasControladorEnum.RUTASUBDETALLE.getValue(), campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirclearService()
    {
        // <CODIGO_DESARROLLADO>

        tabla = spUsuarioAC;
        try
        {

            Map<String, Object> campos = new TreeMap<>();
            campos.put(AbonosfacturasControladorEnum.SERVICIO.getValue(), "");
            campos.put("DATE_MODIFIED", new Date());
            campos.put("MODIFIED_BY", SessionUtil.getUser().getCodigo());

            Map<String, Object> parametros = new TreeMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            parametros.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL47384.getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            campos, parametros);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1806") + codigoRuta);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se selecciona el boton Imprimir Factura
     */
    public void oprimirImprimirFactura()
    {
        // <CODIGO_DESARROLLADO>
        String parManejaControl;
        int numAbonoVigencia = 0;

        try
        {
            if (autorizarBorrado)
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1392"));
                return;
            }

            else if (!bancoperproceso.isEmpty())
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2473"));
                return;
            }

            parManejaControl = ejbSysmanUtil.consultarParametro(compania, "MANEJA CONTROL DE ABONOS POR VIGENCIA", modulo, new Date(),
                            true);
            if ("SI".equals(parManejaControl))
            {

                numAbonoVigencia = Integer.parseInt(
                                ejbSysmanUtil.consultarParametro(compania, "NUMERO DE ABONOS POR VIGENCIA", modulo, new Date(), false));

                if (numAbonoVigencia == 0)
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2700"));
                    return;
                }
                Map<String, Object> parametrosAb = new HashMap<>();
                parametrosAb.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                parametrosAb.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
                parametrosAb.put(AbonosfacturasControladorEnum.ANO.getValue(), ano);

                HashMap<String, Object> rs;
                UrlBean urlReg = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL29427.getValue());

                rs = (HashMap<String, Object>) requestManager.get(urlReg.getUrl(), parametrosAb).getFields();

                int numAbonosReg;
                if (!rs.isEmpty() || ((int) rs.get("CUENTAABONO") > 0))
                {
                    numAbonosReg = Integer.parseInt(SysmanFunciones.nvl(rs.get("CUENTAABONO"), "0").toString());
                    validaNumeroMaximo(numAbonosReg, numAbonoVigencia);
                }
            }
            else
            {
                verNuevoValor = true;
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
     * Metodo usado para validar el numero máximo del que trae el registro
     */
    public void validaNumeroMaximo(int numAbonosReg, int parNumAbonosVigencia)
    {
        if ((numAbonosReg >= parNumAbonosVigencia) || (parNumAbonosVigencia == 0))
        {
            mensajeAbonos = idioma.getString("TB_TB2472")
                            .replace("aux", String.valueOf(numAbonosReg));

            dialogoNumeroAbonos = true;

        }
        else
        {
            verNuevoValor = true;
        }
    }

    /**
     * Metodo al aceptar del dialogo dialogoNumeroAbonos
     */
    public void aceptardialogoNumeroAbonos()
    {
        // <CODIGO_DESARROLLADO>
        dialogoNumeroAbonos = false;

        // Pide el valor del abono
        verNuevoValor = true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo al cancelar del dialogo dialogoNumeroAbonos
     */
    public void cancelardialogoNumeroAbonos()
    {
        dialogoNumeroAbonos = false;
    }

    /**
     * Metodo que se ejecuta cuando se digita el valor de la factura de abonos en el diaglogo dgValorAb.
     */
    public void aceptardgValorAb()
    {

        verNuevoValor = false;
        String parFactu = "";
        double minimoAbono = 0;
        try
        {
            if (nuevoValor <= 0)
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2701"));
            }
            totFacturaPerActual = totalFacturaUsuario();
            if (nuevoValor >= totFacturaPerActual)
            {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2704"));
                return;

            }

            parFactu = ejbSysmanUtil.consultarParametro(compania, parAbonosPrioridadAC, modulo, new Date(), true);

            if ("SI".equals(parFactu))
            {
                minimoAbono = obtenerMinimoAbono();
            }

            if (nuevoValor < minimoAbono)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2705"));
                return;
            }

            parFactu = ejbSysmanUtil.consultarParametro(compania, "MANEJA CONTROL DE ABONOS POR MONTO", modulo, new Date(), true);

            if ("SI".equals(parFactu))
            {
                // Muestra el dialogo para autorizar con clave
                verDialogoAutorizaAbono = true;
            }
            else
            {
                imprimirFacturaAbonos();
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que se ejecuta cuando se va a imprimir la factura cuando ya pasa todas las validaciones desde oprimirImprimirFactura()
     */
    public void imprimirFacturaAbonos()
    {
        String strReciboInicial = "";

        actualizarUsuario(nuevoValor);
        Map<String, Object> parametrosAb = new HashMap<>();
        parametrosAb.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        // Muestra el dialogo que pide el valor del abono.
        // Llama a la misma función para pedir los abonos
        try
        {
            HashMap<String, Object> rsAux;
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL79326.getValue());

            rsAux = (HashMap<String, Object>) requestManager.get(urlReg.getUrl(), parametrosAb).getFields();

            if (!rsAux.isEmpty())
            {
                strReciboInicial = SysmanFunciones.nvl(rsAux.get("CONSECUTIVOREAL"), "").toString();
            }

            // Abrir el Formulario "ImpresionFactura"
            String[] campos = { AbonosfacturasControladorEnum.CICLOMIN.getValue(), "ano",
                                AbonosfacturasControladorEnum.PERIODOMIN.getValue(), "codigoInicial",
                                "codigoFinal", "marca",
                                "marcaIni", "noReciboInicial" };
            String[] valores = { String.valueOf(ciclo), String.valueOf(ano), periodo, codigoRuta, codigoRuta, "3",
                                 "3", strReciboInicial };

            SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(GeneralCodigoFormaEnum.IMPRESIONFACTURAS_CONTROLADOR.getCodigo()),
                            SessionUtil.getModulo(), campos, valores);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo usado para actualizar el abono actual en la tabla sp_usuario
     */
    public void actualizarUsuario(double valorAb)
    {

        Map<String, Object> campos = new TreeMap<>();
        campos.put(AbonosfacturasControladorEnum.ABONO.getValue(), valorAb);
        campos.put("DATE_MODIFIED", new Date());
        campos.put("MODIFIED_BY", SessionUtil.getUser().getCodigo());

        Map<String, Object> parametros = new TreeMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametros.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);
        parametros.put(GeneralParameterEnum.PERIODO.getName(), periodo);

        try
        {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL53644.getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            campos, parametros);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que devielve el valor del monto minimo del abono
     */
    public double obtenerMinimoAbono()
    {
        double valorPagoConv;
        double valorPagoTer;
        double minimoAbono = 0;

        try
        {
            valorPagoConv = ejbServiciosPublicosDos.consultarValorPagoConvenios(compania, ciclo, codigoRuta, ano, periodo, "SI")
                            .doubleValue();

            valorPagoTer = ejbServiciosPublicosDos.consultarValorPagoTercerizado(compania, ciclo, codigoRuta, ano, periodo, "SI")
                            .doubleValue();

            minimoAbono = valorPagoConv + valorPagoTer;

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return minimoAbono;
    }

    public void cambiardialogoAutorizaAbono()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo dialogoAutorizaAbono en la vista
     *
     */
    public void aceptardialogoAutorizaAbono()
    {
        // <CODIGO_DESARROLLADO>
        verDialogoAutorizaAbono = false;

        if ("".equals(usuarioAutorizaCB) || "".equals(claveAutoriza))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2706"));
            return;
        }

        // Forms.Factura.AutoAbono = ""
        if (abonoMonto >= abono)
        {
            if (claveAutoriza.equals(abonoClave))
            {
                // Forms.Factura.Autoabono = usuarioAutorizaCB
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2707"));
            }
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2708"));
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo dialogoAutorizaAbono en la vista
     */
    public void cancelardialogoAutorizaAbono()
    {
        // <CODIGO_DESARROLLADO>
        verDialogoAutorizaAbono = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiardialogoNumeroAbonos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubSubformularioabonos()
    {
        try
        {
            conteo = 0;

            registroSubsubformularioAbonos.getCampos().put("COMPANIA",
                            compania);
            registroSubsubformularioAbonos.getCampos().put("CICLO", ciclo);
            registroSubsubformularioAbonos.getCampos().put(coidgoRutaAC,
                            codigoRuta);
            registroSubsubformularioAbonos.getCampos().put(consecutivoAC,
                            generarConsecutivo());
            registroSubsubformularioAbonos.getCampos().put("ANO", ano);

            registroSubsubformularioAbonos.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());

            registroSubsubformularioAbonos.getCampos().put("DATE_CREATED", new Date());

            registroSubsubformularioAbonos.getCampos().remove(totalC);

            registroSubsubformularioAbonos.getCampos().put(valorAntFactAC, totalFacturaUsuario());

            if (validacionesAlInsertar())
            {
                return;
            }

            UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_ABONOS.getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubsubformularioAbonos.getCampos());

            cargarListaSubformularioabonos();

            ejbServiciosPublicosSiete.calcularFacturacion(compania,
                            ciclo,
                            codigoRuta,
                            codigoRuta,
                            false,
                            false,
                            SessionUtil.getUser().getCodigo());

            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania, "FACTURA SERVICIOS NO RECLAMADOS", modulo, new Date(), true)))
            {
                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                parametros.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                parametros.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

                UrlBean urlReg = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL102949.getValue());

                HashMap<String, Object> rsServNoReclama = (HashMap<String, Object>) requestManager.get(urlReg.getUrl(), parametros)
                                .getFields();

                if (!" ".equals(SysmanFunciones.nvl(rsServNoReclama.get("SERVNORECLAMADO"), " ")))
                {
                    String mensaje = idioma.getString("TB_TB3544") + (rsServNoReclama.get("SERVNORECLAMADO").toString());
                    mensaje = mensaje.replace("01", AbonosfacturasControladorEnum.ACU.getValue());
                    mensaje = mensaje.replace("02", AbonosfacturasControladorEnum.ALC.getValue());
                    mensaje = mensaje.replace("03", AbonosfacturasControladorEnum.ASE.getValue());

                    JsfUtil.agregarMensajeInformativo(mensaje);
                }
            }

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (NumberFormatException | SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubsubformularioAbonos = new Registro(
                            new HashMap<String, Object>());
            registroSubsubformularioAbonos.getCampos().put("ANO", ano);
            registroSubsubformularioAbonos.getCampos().put(periodoC, periodo);
            registroSubsubformularioAbonos.getCampos().put(AbonosfacturasControladorEnum.VALOR.getValue(), 0);
            registroSubsubformularioAbonos.getCampos().put(
                            "VALORABONADOPERIODO",
                            0);
            registroSubsubformularioAbonos.getCampos().put(fechaAC,
                            new Date());
            registroSubsubformularioAbonos.getCampos().put(valorAntFactAC, 0);
            registroSubsubformularioAbonos.getCampos().put(pagoConveniosC, 0);
            registroSubsubformularioAbonos.getCampos().put(pagoTercerizadoAC,
                            0);
            registroSubsubformularioAbonos.getCampos().put(totalC, 0);

        }
    }

    public void eliminarRegSubSubformularioabonos(Registro reg)
    {
        try
        {

            FacesContext context = FacesContext.getCurrentInstance();
            UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_ABONOS.getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                            idioma.getString("TB_TB8"),
                                            idioma.getString(
                                                            "MSM_REGISTRO_ELIMINADO")));

            // Al eliminar el abono se dispara el trigger BIUD_SP_ABONOS, que reversa los abonos y detalles.
            ejbServiciosPublicosSiete.calcularFacturacion(compania,
                            ciclo,
                            codigoRuta,
                            codigoRuta,
                            false,
                            false,
                            SessionUtil.getUser().getCodigo());

            ejecutarAuditoria("Eliminación");

            cargarListaSubformularioabonos();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo usado para validar la lectura que se trae desde el formulario Factura al momento de ingresar un nuevo registro en el subformulario abonos.
     *
     * @return true: no permite ingresar un registro. false: permite ingresar un registro
     */
    public boolean validarLectura()
    {
        boolean salirMetodo = false;
        if ("P".equals(txtFimm))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1809"));
            salirMetodo = true;
            return salirMetodo;
        }

        if (validaTxtFimm())
        {
            salirMetodo = true;
            return salirMetodo;
        }

        return salirMetodo;
    }

    public boolean validaTxtFimm()
    {
        boolean salirMetodo = false;

        if ("F".equals(txtFimm) && "0".equals(lectura)
            && "".equals(bancoperproceso)
            && "".equals(periodosNoCobroFac))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1810"));
            salirMetodo = true;
            return salirMetodo;

        }

        if (validaTxtfimmLectura())
        {
            salirMetodo = true;
            return salirMetodo;
        }

        return salirMetodo;
    }

    public boolean validaTxtfimmLectura()
    {

        boolean salirMetodo = false;
        if (!"F".equals(txtFimm) && "0".equals(lectura)
            && "".equals(bancoperproceso)
            && "".equals(periodosNoCobroFac))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1811"));
            salirMetodo = true;
            return salirMetodo;
        }
        return salirMetodo;
    }

    /**
     * Metodo que realiza las validaciones respecto al periodo del proceso, el valor y el total del pago tercerizado
     *
     * @return true: no permite realizar la insercion de un registro, false: permite ingresar un registro nuevo
     */
    public boolean validacionesAlInsertar()
    {
        boolean salirMetodo = false;

        if (validarLectura())
        {
            salirMetodo = true;
            return salirMetodo;
        }

        double valorA = Double
                        .parseDouble(registroSubsubformularioAbonos.getCampos()
                                        .get(AbonosfacturasControladorEnum.VALOR.getValue()).toString());

        if (!bancoperproceso.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1814"));
            salirMetodo = true;
            return salirMetodo;
        }

        totFacturaPerActual = totalFacturaUsuario();

        if ((valorA) >= totFacturaPerActual)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1818"));
            salirMetodo = true;
            return salirMetodo;

        }

        return salirMetodo;
    }

    /**
     * Metoto que devuelve el total de la factura del usuario
     */

    public double totalFacturaUsuario()
    {
        double rta = 0;

        try
        {
            Map<String, Object> parametrosAno = new HashMap<>();
            parametrosAno.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametrosAno.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            parametrosAno.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

            HashMap<String, Object> rsF;
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(AbonosfacturasControladorUrlEnum.URL28057.getValue());

            rsF = (HashMap<String, Object>) requestManager.get(urlReg.getUrl(), parametrosAno).getFields();
            rta = Double.parseDouble(SysmanFunciones.nvlStr(rsF.get("TOTFACTURAPERACTUAL").toString(), "0"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return rta;

    }

    /**
     * Metodo usado para generar el consecutivo con el que se almacenara el nuevo registro en el subformulario Abonos.
     *
     * @return consecutivo: numero con el que se va a almacenar el registro nuevo en la base de datos.
     */
    public int generarConsecutivo()
    {

        try
        {
            consecutivo = Integer.parseInt(String.valueOf(ejbSysmanUtil.generarConsecutivoConValorInicial("SP_ABONOS",
                            "COMPANIA=''" + compania + "''", "CONSECUTIVO", "1")));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return consecutivo;

    }

    /**
     * Metodo que se ejecuta al momento de ingresar un nuevo registro en el subformulario Abonos.
     */
    public void ejecutarAuditoria(String accionP)
    {

        String tabla = spAbonos;

        String descripcion = " Valor: "
            + SysmanFunciones.nvl(
                            registroSubsubformularioAbonos.getCampos()
                                            .get(AbonosfacturasControladorEnum.VALOR.getValue()),
                            0)
            + " ;Banco: "
            + SysmanFunciones.nvl(
                            registroSubsubformularioAbonos.getCampos()
                                            .get(bancoAC),
                            0)
            + " ;Fecha: "
            + SysmanFunciones.nvl(
                            registroSubsubformularioAbonos.getCampos()
                                            .get(fechaAC),
                            0)
            + " ;ValorAntFact: " + SysmanFunciones.nvl(
                            registroSubsubformularioAbonos.getCampos()
                                            .get(valorAntFactAC),
                            0)
            + " ;Consecutivo: " + consecutivo + "";

        try
        {
            ejbServiciosPublicosTres.auditoriaGeneral(compania, SessionUtil.getUser().getCodigo(),
                            tabla, accionP, ano, periodo, codigoInterno, descripcion);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void editarRegSubSubformularioabonos(RowEditEvent event)
    {
        // No se permite actualizar. Si desea cambiar debe eliminar el registro y luego adicionar uno nuevo ", vbInformation, "Sysman Software"
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo dialogoIndAutorizado en la vista Al eliminar el abono se dispara el trigger BIUD_SP_ABONOS, que reversa los abonos y detalles.
     */
    public void aceptardialogoIndAutorizado()
    {
        // <CODIGO_DESARROLLADO>

    }

    public void cancelardialogoIndAutorizado()
    {
        // <CODIGO_DESARROLLADO>
        dialogoIndAutorizadoVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void onCancelSubformularioabonos()
    {
        cargarListaSubformularioabonos();
        cargarListaSubformularioabonosanterior();
    }

    public void agregarRegistroSubSubformularioabonosanterior()
    {
        // Desde el subformulario Anterior no se permite ingresar datos, Solamente visualizarlos
    }

    public void editarRegSubSubformularioabonosanterior(RowEditEvent event)
    {
        // En el subFormularioAnterior no se permite editar información solo visualizarla.
    }

    public void onCancelSubformularioabonosanterior()
    {
        cargarListaSubformularioabonosanterior();
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void ejecutarrcCerrar()
    {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(AbonosfacturasControladorEnum.KCOMPANIA.getValue(), compania);
        parametros.put(AbonosfacturasControladorEnum.KCICLO.getValue(), ciclo);
        parametros.put(AbonosfacturasControladorEnum.KRUTA.getValue(), codigoRuta);

        String[] campos = { "ridAbono", "ciclo", "ano", "periodo" };
        Object[] valores = { parametros, ciclo, ano, periodo };

        SessionUtil.redireccionar("/factura.sysman", campos, valores);
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        String parametro;

        totalPagoTercerizadoA = Double
                        .parseDouble(registroSubsubformularioAbonos
                                        .getCampos().get(AbonosfacturasControladorEnum.VALOR.getValue()).toString())
            + Double.parseDouble(registroSubsubformularioAbonos
                            .getCampos().get(pagoConveniosC).toString())
            + Double
                            .parseDouble(registroSubsubformularioAbonos
                                            .getCampos()
                                            .get(pagoTercerizadoAC).toString());

        try
        {

            parametro = ejbSysmanUtil.consultarParametro(compania, "SEPARAR ABONOS POR CONCEPTO", modulo, new Date(), true);

            if (parametro == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString(confParametro)
                    + "SEPARAR ABONOS POR CONCEPTO");
            }
            if ("NO".equals(parametro))
            {
                bloqueaValorA = false;
                cmdDetalleVisible = false;
                detalleAntVisible = false;
            }

            parametro = ejbSysmanUtil.consultarParametro(compania, parAbonosPrioridadAC, modulo, new Date(), true);

            if ("SI".equals(parametro))
            {
                txtPagoconveniosVisible = true;
                txtPagoTercerizadoVisible = true;
                txtTotalTercerizadoVisible = true;
            }

            parametro = ejbSysmanUtil.consultarParametro(compania, facturaServiciosC, modulo, new Date(), true);

            if ("SI".equals(parametro))
            {
                clearServiceVisible = true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>

        precargarRegistro();

        if ("i".equals(accion))
        {
            bloqueaValorA = false;
        }
        else
        {
            bloqueaValorA = true;

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Subformularioabonos
     *
     */
    public void cancelarEdicionSubformularioabonos()
    {
        cargarListaSubformularioabonos();
        cargarListaSubformularioabonosanterior();
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Subformularioabonosanterior
     *
     */
    public void cancelarEdicionSubformularioabonosanterior()
    {
        cargarListaSubformularioabonosanterior();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1118-DESPUES_INSERTAR Private Sub Form_AfterInsert() AuditarModif Me, 1, Me!Compania & "^" & Me!Ciclo & "^" & Me!CodigoRuta End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1118-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate() Dim str As String If Not Me.NewRecord Then AudRegistroComparar Me, Me!Compania & "^" & Me!Ciclo & "^" & Me!CodigoRuta, Me!Compania,
         * Me!Ciclo, Me!CodigoRuta End If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1118-ANTES_ELIMINAR Private Sub Form_Delete(Cancel As Integer) AuditarModif Me, 2, Me!Compania & "^" & Me!Ciclo & "^" & Me!CodigoRuta End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

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

    public String getBancoperproceso()
    {
        return bancoperproceso;
    }

    public void setBancoperproceso(String bancoperproceso)
    {
        this.bancoperproceso = bancoperproceso;
    }

    public boolean isDialogoIndAutorizadoVisible()
    {
        return dialogoIndAutorizadoVisible;
    }

    public void setDialogoIndAutorizadoVisible(
        boolean dialogoIndAutorizadoVisible)
    {
        this.dialogoIndAutorizadoVisible = dialogoIndAutorizadoVisible;
    }

    public String getPeriodoC()
    {
        return periodoC;
    }

    public String getPagoTercerizadoAC()
    {
        return pagoTercerizadoAC;
    }

    public String getPagoConveniosC()
    {
        return pagoConveniosC;
    }

    public String getSpAbonos()
    {
        return spAbonos;
    }

    public int getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(int ciclo)
    {
        this.ciclo = ciclo;
    }

    public String getCodigoRuta()
    {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta)
    {
        this.codigoRuta = codigoRuta;
    }

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public String getTotalValor()
    {
        return totalValor;
    }

    public void setTotalValor(String totalValor)
    {
        this.totalValor = totalValor;
    }

    public String getTotalValorS()
    {
        return totalValorS;
    }

    public void setTotalValorS(String totalValorS)
    {
        this.totalValorS = totalValorS;
    }

    public boolean isBloqueaValorA()
    {
        return bloqueaValorA;
    }

    public void setBloqueaValorA(boolean bloqueaValorA)
    {
        this.bloqueaValorA = bloqueaValorA;
    }

    public boolean isCmdDetalleVisible()
    {
        return cmdDetalleVisible;
    }

    public void setCmdDetalleVisible(boolean cmdDetalleVisible)
    {
        this.cmdDetalleVisible = cmdDetalleVisible;
    }

    public boolean isDetalleAntVisible()
    {
        return detalleAntVisible;
    }

    public void setDetalleAntVisible(boolean detalleAntVisible)
    {
        this.detalleAntVisible = detalleAntVisible;
    }

    public boolean isTxtPagoconveniosVisible()
    {
        return txtPagoconveniosVisible;
    }

    public void setTxtPagoconveniosVisible(boolean txtPagoconveniosVisible)
    {
        this.txtPagoconveniosVisible = txtPagoconveniosVisible;
    }

    public boolean isTxtPagoTercerizadoVisible()
    {
        return txtPagoTercerizadoVisible;
    }

    public void setTxtPagoTercerizadoVisible(
        boolean txtPagoTercerizadoVisible)
    {
        this.txtPagoTercerizadoVisible = txtPagoTercerizadoVisible;
    }

    public boolean isTxtTotalTercerizadoVisible()
    {
        return txtTotalTercerizadoVisible;
    }

    public void setTxtTotalTercerizadoVisible(
        boolean txtTotalTercerizadoVisible)
    {
        this.txtTotalTercerizadoVisible = txtTotalTercerizadoVisible;
    }

    public boolean isClearServiceVisible()
    {
        return clearServiceVisible;
    }

    public void setClearServiceVisible(boolean clearServiceVisible)
    {
        this.clearServiceVisible = clearServiceVisible;
    }

    public double getTotalPagoTercerizadoA()
    {
        return totalPagoTercerizadoA;
    }

    public void setTotalPagoTercerizadoA(double totalPagoTercerizadoA)
    {
        this.totalPagoTercerizadoA = totalPagoTercerizadoA;
    }

    public String getMensajeAbonos()
    {
        return mensajeAbonos;
    }

    public void setMensajeAbonos(String mensajeAbonos)
    {
        this.mensajeAbonos = mensajeAbonos;
    }

    public boolean isDialogoNumeroAbonos()
    {
        return dialogoNumeroAbonos;
    }

    public void setDialogoNumeroAbonos(boolean dialogoNumeroAbonos)
    {
        this.dialogoNumeroAbonos = dialogoNumeroAbonos;
    }

    public boolean isVerNuevoValor()
    {
        return verNuevoValor;
    }

    public void setVerNuevoValor(boolean verNuevoValor)
    {
        this.verNuevoValor = verNuevoValor;
    }

    public boolean isVerDialogoAutorizaAbono()
    {
        return verDialogoAutorizaAbono;
    }

    public void setVerDialogoAutorizaAbono(boolean verDialogoAutorizaAbono)
    {
        this.verDialogoAutorizaAbono = verDialogoAutorizaAbono;
    }

    public double getNuevoValor()
    {
        return nuevoValor;
    }

    public void setNuevoValor(double nuevoValor)
    {
        this.nuevoValor = nuevoValor;
    }

    public double getAbonoMonto()
    {
        return abonoMonto;
    }

    public void setAbonoMonto(double abonoMonto)
    {
        this.abonoMonto = abonoMonto;
    }

    public String getAbonoClave()
    {
        return abonoClave;
    }

    public void setAbonoClave(String abonoClave)
    {
        this.abonoClave = abonoClave;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListabancoA()
    {
        return listabancoA;
    }

    public void setListabancoA(RegistroDataModelImpl listabancoA)
    {
        this.listabancoA = listabancoA;
    }

    public RegistroDataModelImpl getListabancoAE()
    {
        return listabancoAE;
    }

    public void setListabancoAE(RegistroDataModelImpl listabancoAE)
    {
        this.listabancoAE = listabancoAE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaSubformularioabonos()
    {
        return listaSubformularioabonos;
    }

    public void setListaSubformularioabonos(
        List<Registro> listaSubformularioabonos)
    {
        this.listaSubformularioabonos = listaSubformularioabonos;
    }

    public List<Registro> getListaSubformularioabonosanterior()
    {
        return listaSubformularioabonosanterior;
    }

    public void setListaSubformularioabonosanterior(
        List<Registro> listaSubformularioabonosanterior)
    {
        this.listaSubformularioabonosanterior = listaSubformularioabonosanterior;
    }

    public String getUsuarioAutorizaCB()
    {
        return usuarioAutorizaCB;
    }

    public void setUsuarioAutorizaCB(String usuarioAutorizaCB)
    {
        this.usuarioAutorizaCB = usuarioAutorizaCB;
    }

    public String getClaveAutoriza()
    {
        return claveAutoriza;
    }

    public void setClaveAutoriza(String claveAutoriza)
    {
        this.claveAutoriza = claveAutoriza;
    }

    public RegistroDataModelImpl getListausuarioAutoriza()
    {
        return listausuarioAutoriza;
    }

    public void setListausuarioAutoriza(
        RegistroDataModelImpl listausuarioAutoriza)
    {
        this.listausuarioAutoriza = listausuarioAutoriza;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSubsubformularioAbonos()
    {
        return registroSubsubformularioAbonos;
    }

    public void setRegistroSubsubformularioAbonos(
        Registro registroSubsubformularioAbonos)
    {
        this.registroSubsubformularioAbonos = registroSubsubformularioAbonos;
    }

    public Registro getRegistroSubSubformularioAbonosAnterior()
    {
        return registroSubSubformularioAbonosAnterior;
    }

    public void setRegistroSubSubformularioAbonosAnterior(
        Registro registroSubSubformularioAbonosAnterior)
    {
        this.registroSubSubformularioAbonosAnterior = registroSubSubformularioAbonosAnterior;
    }
    // </SET_GET_ADICIONALES>
}
