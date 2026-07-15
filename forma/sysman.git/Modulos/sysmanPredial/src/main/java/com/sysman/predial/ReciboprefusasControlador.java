package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.ejb.EjbPredialTresRemote;
import com.sysman.predial.enums.ReciboprefusasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 2, 10/06/2016 17:16:14 -- Modificado por sdaza
 * @version 3, 09/03/2017 10:24:00 -- Modificado por yrojas Se implementaron eventos en el metodo imprimirRecibo de los botones de presentacion e impresion del formulario, creando dialogo adicional
 * para la confirmacion de creacion de factura en cero y de anulacion de factura en caso de que ya exista, ademas de modificar la condicion del visibilidad del dialogo de propietarios. Se elimino el
 * check de unico anio. Se modifico validacion de parametros de fechas para la fecha limite.
 *
 * @author spina - refactorizo conexiones
 * @version 4, 13/06/2017
 *
 * @author spina
 * @version 5, 15/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class ReciboprefusasControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private String modulo;

    private final String strInterrumpida;
    private final String strFormato;

    // <DECLARAR_ATRIBUTOS>

    private String vlrInd1175;

    private String idPropietario;
    private String nomPropietario;
    private String codigoPredio;
    private String numeroOrden;
    private Date fechaCorte;
    private String numRecibo;
    private String descEspecial;
    private String anoFin;
    private String anoAbono;
    private String anoInicial;
    private Date fecLimite;
    private String vlrAbono;
    private String anoActual;
    private String cptoDesc;
    private String totalImpuesto;
    private String totalInteres;
    private String totalCar;
    private String totalCarInt;
    private String totalDesc;
    private String totalC14;
    private String totalOtros;
    private String totalDeuda;
    private boolean indAnoUnico;
    private boolean indVigencia;
    private boolean indAbono;
    private boolean indCuotas;
    private boolean indAplica1066;
    private boolean indAplica1175;
    private boolean verAbono;
    private boolean verCuotas;
    private boolean verDescEsp;
    private boolean verPorcDescEsp;
    private String tituloDescEsp;
    private String abono;
    private String pagoAno;
    private int indOtros; // indicador que recibe las validadones del
                          // parametro VISUALIZAR CONCEPTOS 19 Y 20
                          // EN
                          // LIQUIDACION Y SI EL MUNICIPIO ES
                          // SOGAMOSO
    private boolean indVerDgProp;
    private String idPropietarioFac;
    private String nomPropietarioFac;
    private String ordenPropietarioFac;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaanofin;
    private List<Registro> listaanoAbono;
    private List<Registro> listaAnoInicial;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbDescesp;
    private RegistroDataModelImpl listaPropietario;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaFacturados2;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    private String fecSistFecMor;
    private String esMorFinalAmnistia;
    private String tipoFecAbono;
    private String fecLimitePago;
    private String fecLimitePagoMor;
    private String manFactAbono;
    private String manFactCuotas;
    private String manPorcDescEsp;
    private String manDescEsp;

    private String generaFraCoprop;
    private String nomC14;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSub;
    private boolean verDialogoFactCero;
    private String avaluoAno;
    private boolean opcionImprimir;
    private boolean verAnularFactura;
    private boolean anularFactura;
    private String numFactPendiente;
    private boolean impFacturaCero;
    private boolean vlrCheck1175;
    private Date auxFechaLimite;
    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPredialCeroRemote ejbPredialCero;

    @EJB
    private EjbPredialTresRemote ejbPredialTres;

    // </DECLARAR_ADICIONALES>
    @SuppressWarnings("unchecked")
    public ReciboprefusasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strInterrumpida = "MSM_TRANS_INTERRUMPIDA";
        strFormato = "dd/MM/YYYY";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.RECIBOPREFUSAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                codigoPredio = String
                                .valueOf(parametrosEntrada.get("codigoPredio"));
                numeroOrden = String.valueOf(
                                parametrosEntrada.get("nroOrden"));
                idPropietario = String.valueOf(
                                parametrosEntrada.get("idPropietario"));
                nomPropietario = String.valueOf(parametrosEntrada
                                .get("nomPropietario"));
                pagoAno = String.valueOf(parametrosEntrada.get("pagoAno"));
                indOtros = Integer.parseInt(String
                                .valueOf(parametrosEntrada.get("indOtros")));
                avaluoAno = String.valueOf(parametrosEntrada.get("avaluoAno"));
            }
            else
            {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(ReciboprefusasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {

        try
        {
            nomC14 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 14);

            // Consultar el ultimo numero de factura para cargarlo
            // en
            // el formulario
            cargarNumeroRecibo();

            // carga fecha actual como fecha de generacion de la
            // factura.
            fechaCorte = new Date();

            // validacion de fecha limite para factura teniendo en
            // cuenta configuracion de parametros
            esMorFinalAmnistia = esMorFinalAmnistia == null ? "NO"
                : esMorFinalAmnistia;

            fecLimite = obtenerFechaLimite();
            auxFechaLimite = fecLimite;

            // validacion de indicadores
            indAbono = false;
            indCuotas = false;
            indAnoUnico = false;
            indVigencia = true;
            verAbono = false;
            verCuotas = false;
            indAplica1175 = false;
            verPorcDescEsp = false;
            vlrCheck1175 = false;

            if ("SI".equals(manFactAbono))
            {
                verAbono = true;
            }

            if ("SI".equals(manFactCuotas)
                && (Integer.parseInt(pagoAno) < Integer.parseInt(anoActual)))
            {
                verCuotas = true;
            }

            if ("SI".equals(manDescEsp))
            {
                indAplica1175 = true;
                if ("SI".equals(vlrInd1175))
                {
                    vlrCheck1175 = true;
                }
            }

            if ("SI".equals(manPorcDescEsp))
            {
                verPorcDescEsp = true;
            }

        }
        catch (ParseException | SystemException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(strInterrumpida)
                                + e.getMessage());
            Logger.getLogger(PrediallistpropieporprediosControlador.class
                            .getName()).log(Level.SEVERE, null, e);
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbDescesp();
        cargarListaPropietario();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaanofin();
        cargarListaanoAbono();
        cargarListaAnoInicial();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaFacturados2();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaFacturados2 = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar()
    {
        tabla = "IP_FACTURADOS";
        buscarLlave();
        anoActual = String.valueOf(SysmanFunciones.ano(new Date()));
        asignarOrigenDatos();
        iniciarListas();
        iniciarListasSub();
        actualizarTotales();
        cargarParametros();
        abrirFormulario();

    }

    @Override
    public void asignarOrigenDatos()
    {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.PREDIO.getName(),
                        codigoPredio);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
                        numeroOrden);

        urlLectura = UrlServiceUtil
                        .getUrlBeanById(ReciboprefusasControladorUrlEnum.URL4842
                                        .getValue());

    }

    public void cargarListaFacturados2()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroOrden);
        param.put("INDOTROS", indOtros);

        try
        {
            listaFacturados2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ReciboprefusasControladorUrlEnum.URL4843
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaanofin()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        param.put(GeneralParameterEnum.ANO.getName(), pagoAno);

        try
        {
            listaanofin = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ReciboprefusasControladorUrlEnum.URL4844
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaanoAbono()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        param.put(GeneralParameterEnum.ANO.getName(), pagoAno);

        try
        {
            listaanoAbono = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReciboprefusasControladorUrlEnum.URL4844
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        param.put(GeneralParameterEnum.ANO.getName(), pagoAno);

        try
        {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReciboprefusasControladorUrlEnum.URL4844
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbDescesp()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReciboprefusasControladorUrlEnum.URL4845
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoActual);
        listaCmbDescesp = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID_PORCENTAJE");
    }

    public void cargarListaPropietario()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReciboprefusasControladorUrlEnum.URL4846
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        listaPropietario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control feclimite
     */
    public void cambiarfeclimite()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            if (auxFechaLimite.after(fecLimite))
            {
                fecLimite = auxFechaLimite;
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3017")
                                .replace("#$fechalim#$", SysmanFunciones
                                                .convertirAFechaCadena(
                                                                auxFechaLimite,
                                                                strFormato)));
                return;
            }
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarChkVigencia()
    {
        if (indVigencia)
        {
            indAbono = false;
        }
        else
        {
            indAbono = true;
        }
    }

    public void cambiarChkAbono()
    {
        if (indAbono)
        {
            indVigencia = false;
            indCuotas = false;
        }
        else
        {
            if (verCuotas)
            {
                indCuotas = true;
                indVigencia = false;
                indAbono = false;
            }
            else
            {
                indCuotas = false;
                indVigencia = true;
                indAbono = false;
            }

        }
    }

    public void cambiarChkCuotas()
    {
        if (indCuotas)
        {
            indVigencia = false;
            indAbono = false;
        }
        else
        {
            indCuotas = false;
            indVigencia = true;
            indAbono = false;

        }
    }

    // </METODOS_CAMBIAR>

    public void aceptarFactPropietario()
    {
        verDialogoFactCero = true;
        indVerDgProp = false;
    }

    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmbDescesp(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        descEspecial = String
                        .valueOf(registroAux.getCampos().get("ID_PORCENTAJE"));
    }

    private boolean validarIndVigencia()
    {
        if (SysmanFunciones.validarVariableVacio(anoInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB252"));
            return false;
        }

        if (SysmanFunciones.validarVariableVacio(anoFin))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB254"));
            return false;
        }

        if (Integer.parseInt(anoInicial) > Integer.parseInt(anoFin))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB255"));
            return false;
        }
        return true;
    }

    public boolean validarDatosRequeridos()
    {
        if (indVigencia)
        {
            return validarIndVigencia();
        }

        if (indAbono)
        {
            if (SysmanFunciones.validarVariableVacio(anoAbono))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB256"));
                return false;
            }
            if (SysmanFunciones.validarVariableVacio(vlrAbono))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB257"));
                return false;
            }
        }
        return true;
    }

    public void seleccionarFilaPropietario(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        idPropietarioFac = String.valueOf(registroAux.getCampos().get("NIT"));
        nomPropietarioFac = String
                        .valueOf(registroAux.getCampos().get("NOMBRE"));
        ordenPropietarioFac = String
                        .valueOf(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_ORDEN
                                                        .getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarDatosRequeridos())
        {
            return;
        }
        nomPropietarioFac = null;
        idPropietarioFac = null;

        if ("SI".equals(generaFraCoprop))
        {
            indVerDgProp = true;
        }
        else
        {
            verDialogoFactCero = true;
        }

        opcionImprimir = true;
        anularFactura();

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentacion()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarDatosRequeridos())
        {
            return;
        }

        if ("SI".equals(generaFraCoprop))
        {
            indVerDgProp = true;
        }
        else
        {
            verDialogoFactCero = true;
        }

        opcionImprimir = false;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubFacturados2()
    {
        // Metodo no implementado
    }

    public void editarRegSubFacturados2(RowEditEvent event)
    {
        // Metodo no implementado
    }

    public void eliminarRegSubFacturados2(Registro reg)
    {
        // Metodo no implementado
    }

    public void cancelarEdicionFacturados2()
    {
        cargarListaFacturados2();
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String ruta = "/usuariospredial.sysman";
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    public void identificarAnioUnico(String anoInicial, String anoFin)
    {
        if (anoInicial.equals(anoFin))
        {
            indAnoUnico = true;
        }
        else
        {
            indAnoUnico = false;
        }
    }

    // METODO PARA IMPLEMENTAR LA RUTINA DE FACTURACION
    public void imprimirRecibo(boolean impFactCero)
    {
        String numFactura = null;
        String consultaReporte;
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        if (SysmanFunciones.validarVariableVacio(idPropietarioFac))
        {
            idPropietarioFac = idPropietario;
            nomPropietarioFac = nomPropietario;
            ordenPropietarioFac = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        }
        if (!validarDatosRequeridos())
        {
            return;
        }

        identificarAnioUnico(anoInicial, anoFin);

        try
        {
            if (opcionImprimir)
            {
                numFactura = ejbPredialTres.facturarVigencia(compania,
                                SessionUtil.getCompaniaIngreso().getNit(),
                                SessionUtil.getCompaniaIngreso()
                                                .getSigla(),
                                codigoPredio, Integer.parseInt(pagoAno),
                                indAnoUnico,
                                fechaCorte,
                                fecLimite,
                                nomPropietarioFac,
                                ordenPropietarioFac, idPropietarioFac,
                                impFactCero,
                                SessionUtil.getUser().getCodigo(),
                                Integer.parseInt(anoInicial),
                                Integer.parseInt(anoFin),
                                avaluoAno, indAplica1175);

                reemplazar.put("docNum", "'" + numFactura + "'");
                parametros.put("PR_CODIGO", "'" + numFactura + "'");
                parametros.put("PR_COPIA", false);
                
                consultaReporte = "000817FORMATOPNUD";
            }
            else
            {
                reemplazar.put("codigo", "'" + codigoPredio + "'");
                reemplazar.put("fecLim",
                                "'" + SysmanFunciones.convertirAFechaCadena(
                                                fecLimite, strFormato)
                                    + "'");
                reemplazar.put("numeroOrden", "'" + ordenPropietarioFac + "'");
                reemplazar.put("docNum", "'" + numRecibo + "'");
                reemplazar.put("condicionSub", indAnoUnico
                    ? "AND IP_FACTURADOS.PREANO = " + anoFin
                    : "AND IP_FACTURADOS.PREANO BETWEEN " + anoInicial + " AND "
                        + anoFin);
                parametros.put("PR_CODIGO", codigoPredio);
                parametros.put("PR_COPIA", true);
                consultaReporte = "000817FORMATOPNUDPREVIO";
            }

            reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            reemplazar.put("strCodigoEAN",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CODIGO EAN",
                                            SessionUtil.getModulo(), new Date(),
                                            true));
            reemplazar.put("condicion", SysmanFunciones
                            .concatenar(" AND RP.DOCNUM = '", numFactura,
                                            "' "));

            parametros.put("PR_ANOS_PAGOS", anoInicial + " A " + anoFin);
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_NUMERO_ORDEN", "'" + ordenPropietarioFac + "'");
            parametros.put("PR_NOMBREBANCO", service.buscarEnLista(codigoPredio,
                            GeneralParameterEnum.CODIGO.getName(), "PAG_BAN",
                            listaFacturados2));
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PAGINA_WEB",
                            SessionUtil.getCompaniaIngreso().getPaginaWeb());
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_OBSERVACIONES", " ");
            parametros.put("PR_LEYENDA_USUARIO", SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "LEYENDA USUARIO",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                                            ""));
            parametros.put("PR_LEYENDA_LEGAL",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "LEYENDA LEGAL",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_ENC_1", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 1));
            parametros.put("PR_ENC_2", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 2));
            parametros.put("PR_ENC_3", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 3));
            parametros.put("PR_ENC_4", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 4));
            parametros.put("PR_ENC_13", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 13));
            parametros.put("PR_ENC_14", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 14));
            parametros.put("PR_ENC_15", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 15));
            parametros.put("PR_ENC_16", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 16));
            parametros.put("PR_ENC_19", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 19));
            parametros.put("PR_FECLIMITE", SysmanFunciones
                            .convertirAFechaCadena(fecLimite, strFormato));
            
            // IMPLEMENTACION MARCA BLANCA (ljdiaz - Luis Jacobo Diaz Muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", JsfUtil.obtenerParametroMarcaBlanca("TITULOLOGIN"));
            // FIN IMPLEMENTACION MARCA BLANCA

            Reporteador.resuelveConsulta(consultaReporte,
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazar, parametros);

            if (opcionImprimir)
            {
                parametros.put("PR_STRSQL_SECUNDARIO370",
                                parametros.get("PR_STRSQL_SECUNDARIO370")
                                                .toString()
                                                .replace("s$condicionSub$s",
                                                                ""));
            }
            archivoDescarga = JsfUtil.exportarStreamed("000817FORMATOPNUD",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.PDF);
        }
        catch (OutOfMemoryError | JRException | IOException | ParseException
                        | SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo AnularFactura en la vista
     */
    public void aceptarAnularFactura()
    {
        // <CODIGO_DESARROLLADO>
        imprimirRecibo(true);
        verAnularFactura = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo FacturaCero en la vista
     */
    public void aceptarFacturaCero()
    {
        // <CODIGO_DESARROLLADO>
        if (anularFactura && opcionImprimir)
        {
            verAnularFactura = true;
            impFacturaCero = true;
            verDialogoFactCero = false;
        }
        else
        {
            impFacturaCero = true;
            imprimirRecibo(impFacturaCero);
            verDialogoFactCero = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo FacturaCero en la vista
     */
    public void cancelarFacturaCero()
    {
        // <CODIGO_DESARROLLADO>
        if (anularFactura)
        {
            verAnularFactura = true;
            impFacturaCero = false;
            verDialogoFactCero = false;
        }
        else
        {
            impFacturaCero = false;
            imprimirRecibo(impFacturaCero);
            verDialogoFactCero = false;
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public void anularFactura()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);

        try
        {
            List<Registro> facturasPendientes = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ReciboprefusasControladorUrlEnum.URL4847
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (!facturasPendientes.isEmpty())
            {
                anularFactura = true;
                numFactPendiente = idioma.getString("TB_TB3037").replace(
                                "#$numFact#$",
                                String.valueOf(facturasPendientes.get(0)
                                                .getCampos()
                                                .get("DOCNUM")));
            }
            else
            {
                anularFactura = false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private Date obtenerFechaLimite() throws ParseException
    {

        if ("SI".equals(fecSistFecMor))
        {
            if (menorVigencia() == SysmanFunciones.ano(
                            new Date()))
            {
                if ((SessionUtil.getCompaniaIngreso().getNombre()
                                .equals(idioma.getObject("TB_TB291")))
                    && (SysmanFunciones.mes(new Date()) >= 6))
                {
                    fecLimite = fechaCorte;
                }
                else if ("SI".equals(esMorFinalAmnistia))
                {
                    validarAmnistia();
                }
                else
                {
                    fecLimite = SysmanFunciones.convertirAFecha(fecLimitePago);
                }
            }
            else
            {
                if (tipoFecAbono.equals(idioma.getObject("TB_TB293")))
                {
                    fecLimite = SysmanFunciones
                                    .ultimoDiaDate(new Date());
                }
                else
                {
                    fecLimite = new Date();
                }
            }
        }
        else
        {
            if (Integer.parseInt(
                            pagoAno) == (SysmanFunciones.ano(new Date())
                                - 1))
            {
                fecLimite = SysmanFunciones.convertirAFecha(fecLimitePago);
            }
            else
            {
                fecLimite = SysmanFunciones
                                .convertirAFecha(fecLimitePagoMor);
            }
        }

        return fecLimite;
    }

    private int menorVigencia()
    {
        int anio = 0;
        try
        {
            anio = ejbPredialTres.retornarMenorVigenciaAdeudadaSinAcuerdo(
                            compania, codigoPredio);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return anio;
    }

    private void validarAmnistia()
    {
        try
        {
            if (SysmanFunciones.mes(new Date()) > mesesAmnistia(
                            SysmanFunciones.ano(new Date())))
            {
                if (tipoFecAbono.equals(idioma.getObject("TB_TB293")))
                {
                    fecLimite = SysmanFunciones
                                    .ultimoDiaDate(new Date());
                }
                else
                {
                    fecLimite = new Date();
                }
            }
            else
            {
                fecLimite = SysmanFunciones
                                .convertirAFecha(fecLimitePago);
            }
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarNumeroRecibo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            List<Registro> cscFactura = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReciboprefusasControladorUrlEnum.URL4848
                                                                            .getValue())
                                            .getUrl(),
                                            param));

            if (!cscFactura.isEmpty())
            {
                numRecibo = String.valueOf(cscFactura.get(0).getCampos()
                                .get("CONSECUTIVOREAL") == null ? "00000001"
                                    : cscFactura.get(0).getCampos()
                                                    .get("CONSECUTIVOREAL"));
                numRecibo = String.valueOf(numRecibo + 1);
                numRecibo = SysmanFunciones.padl(numRecibo, 9, "0");
            }
            else
            {
                numRecibo = "00000001";
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public int mesesAmnistia(int ano)
    {
        int meses = 0;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try
        {
            List<Registro> mesAmnistia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReciboprefusasControladorUrlEnum.URL4849
                                                                            .getValue())
                                            .getUrl(),
                                            param));
            if (!mesAmnistia.isEmpty())
            {
                meses = Integer.parseInt(SysmanFunciones.nvl(mesAmnistia.get(0)
                                .getCampos().get("MESESAMNISTIA_PREDIAL"), "0")
                                .toString());
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return meses;
    }

    public void actualizarTotales()
    {
        if (codigoPredio == null)
        {
            return;
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroOrden);
        param.put("INDOTROS", indOtros);

        try
        {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReciboprefusasControladorUrlEnum.URL4850
                                                                            .getValue())
                                            .getUrl(), param));

            totalImpuesto = SysmanFunciones
                            .nvl(regAux.getCampos().get("TIMPUESTO"), "")
                            .toString();
            totalInteres = SysmanFunciones
                            .nvl(regAux.getCampos().get("TINTERES"), "")
                            .toString();
            totalCar = SysmanFunciones.nvl(regAux.getCampos().get("TCAR"), "")
                            .toString();
            totalCarInt = SysmanFunciones
                            .nvl(regAux.getCampos().get("TCARINT"), "")
                            .toString();
            totalDesc = SysmanFunciones.nvl(regAux.getCampos().get("TDESC"), "")
                            .toString();
            totalC14 = SysmanFunciones.nvl(regAux.getCampos().get("TC14"), "")
                            .toString();
            totalOtros = SysmanFunciones
                            .nvl(regAux.getCampos().get("TOTROS"), "")
                            .toString();
            totalDeuda = SysmanFunciones
                            .nvl(regAux.getCampos().get("TDEUDA"), "")
                            .toString();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String obtenerParametro(String nombreParametro)
                    throws NamingException, SQLException, SystemException
    {
        return ejbSysmanUtil.consultarParametro(compania, nombreParametro,
                        modulo, new Date(), true);
    }

    private void cargaParametrosSecundarios()
    {

        try
        {
            manPorcDescEsp = obtenerParametro(
                            "MANEJA DIF. PORCENTAJES EN LEY DESCUENTOS ESPECIALES");
            manPorcDescEsp = manPorcDescEsp == null ? "NO" : manPorcDescEsp;

            manDescEsp = obtenerParametro(
                            "MANEJA OPCION DESCUENTOS ESPECIALES");
            manDescEsp = manDescEsp == null ? "NO" : manDescEsp;

            vlrInd1175 = obtenerParametro(
                            "VALOR PREDETERMINADO INDICADOR OPCION DESC ESP");
            vlrInd1175 = vlrInd1175 == null ? "NO"
                : "1".equals(vlrInd1175) ? "SI" : "NO";

            tituloDescEsp = obtenerParametro(
                            "TITULO OPCION DESCUENTOS ESPECIALES");
            tituloDescEsp = tituloDescEsp == null
                ? "TITULO OPCION DESCUENTOS ESPECIALES" : tituloDescEsp;

            generaFraCoprop = obtenerParametro(
                            "GENERAR FACTURA A COPROPIETARIOS");
            generaFraCoprop = generaFraCoprop == null ? "NO" : generaFraCoprop;
        }
        catch (NamingException | SQLException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargaParametrosIniciales()
    {
        try
        {
            fecSistFecMor = obtenerParametro(
                            "FECHA SISTEMA COMO FECHA LIMITE MOROSOS");
            fecSistFecMor = fecSistFecMor == null ? "NO" : fecSistFecMor;

            esMorFinalAmnistia = obtenerParametro(
                            "MOROSO FINALIZADO PERIODO DE AMNISTIA");

            tipoFecAbono = obtenerParametro(
                            "TIPO FECHA AL DIA PARA ABONOS");

            tipoFecAbono = tipoFecAbono == null ? idioma.getString("TB_TB292")
                : tipoFecAbono;

            fecLimitePago = obtenerParametro("FECHA LIMITE DE PAGO");
            fecLimitePago = fecLimitePago == null
                ? SysmanFunciones.convertirAFechaCadena(fechaCorte)
                : fecLimitePago;

            fecLimitePagoMor = obtenerParametro(
                            "FECHA LIMITE DE PAGO MOROSOS");
            fecLimitePagoMor = fecLimitePagoMor == null
                ? SysmanFunciones.convertirAFechaCadena(fechaCorte)
                : fecLimitePagoMor;

            // La validacion del indicador de cuotas depende de la
            // configuracion de un parametro
            manFactAbono = obtenerParametro(
                            "MANEJA FACTURACION POR ABONOS");
            manFactAbono = manFactAbono == null ? "NO" : manFactAbono;

            manFactCuotas = obtenerParametro(
                            "MANEJA FACTURACION POR CUOTAS");
            manFactCuotas = manFactCuotas == null ? "NO" : manFactCuotas;
        }
        catch (NamingException | SQLException | SystemException
                        | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarParametros()
    {
        cargaParametrosIniciales();
        cargaParametrosSecundarios();
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
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

    public boolean isAnularFactura()
    {
        return anularFactura;
    }

    public void setAnularFactura(boolean anularFactura)
    {
        this.anularFactura = anularFactura;
    }

    public boolean isImpFacturaCero()
    {
        return impFacturaCero;
    }

    public void setImpFacturaCero(boolean impFacturaCero)
    {
        this.impFacturaCero = impFacturaCero;
    }

    public boolean isVerDialogoFactCero()
    {
        return verDialogoFactCero;
    }

    public void setVerDialogoFactCero(boolean verDialogoFactCero)
    {
        this.verDialogoFactCero = verDialogoFactCero;
    }

    public String getNumFactPendiente()
    {
        return numFactPendiente;
    }

    public void setNumFactPendiente(String numFactPendiente)
    {
        this.numFactPendiente = numFactPendiente;
    }

    public String getNomPropietario()
    {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario)
    {
        this.nomPropietario = nomPropietario;
    }

    public String getCodigoPredio()
    {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio)
    {
        this.codigoPredio = codigoPredio;
    }

    public Date getFechaCorte()
    {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte)
    {
        this.fechaCorte = fechaCorte;
    }

    public String getNumRecibo()
    {
        return numRecibo;
    }

    public void setNumRecibo(String numRecibo)
    {
        this.numRecibo = numRecibo;
    }

    public String getDescEspecial()
    {
        return descEspecial;
    }

    public void setDescEspecial(String descEspecial)
    {
        this.descEspecial = descEspecial;
    }

    public String getAnoFin()
    {
        return anoFin;
    }

    public void setAnoFin(String anoFin)
    {
        this.anoFin = anoFin;
    }

    public String getAnoAbono()
    {
        return anoAbono;
    }

    public void setAnoAbono(String anoAbono)
    {
        this.anoAbono = anoAbono;
    }

    public String getAnoInicial()
    {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial)
    {
        this.anoInicial = anoInicial;
    }

    public Date getFecLimite()
    {
        return fecLimite;
    }

    public void setFecLimite(Date fecLimite)
    {
        this.fecLimite = fecLimite;
    }

    public String getVlrAbono()
    {
        return vlrAbono;
    }

    public void setVlrAbono(String vlrAbono)
    {
        this.vlrAbono = vlrAbono;
    }

    public String getCptoDesc()
    {
        return cptoDesc;
    }

    public void setCptoDesc(String cptoDesc)
    {
        this.cptoDesc = cptoDesc;
    }

    public boolean isIndAnoUnico()
    {
        return indAnoUnico;
    }

    public void setIndAnoUnico(boolean indAnoUnico)
    {
        this.indAnoUnico = indAnoUnico;
    }

    public boolean isIndVigencia()
    {
        return indVigencia;
    }

    public void setIndVigencia(boolean indVigencia)
    {
        this.indVigencia = indVigencia;
    }

    public boolean isIndAbono()
    {
        return indAbono;
    }

    public void setIndAbono(boolean indAbono)
    {
        this.indAbono = indAbono;
    }

    public boolean isIndCuotas()
    {
        return indCuotas;
    }

    public void setIndCuotas(boolean indCuotas)
    {
        this.indCuotas = indCuotas;
    }

    public boolean isIndAplica1066()
    {
        return indAplica1066;
    }

    public void setIndAplica1066(boolean indAplica1066)
    {
        this.indAplica1066 = indAplica1066;
    }

    public boolean isIndAplica1175()
    {
        return indAplica1175;
    }

    public void setIndAplica1175(boolean indAplica1175)
    {
        this.indAplica1175 = indAplica1175;
    }

    public String getAbono()
    {
        return abono;
    }

    public void setAbono(String abono)
    {
        this.abono = abono;
    }

    public boolean isVerAbono()
    {
        return verAbono;
    }

    public void setVerAbono(boolean verAbono)
    {
        this.verAbono = verAbono;
    }

    public boolean isVerCuotas()
    {
        return verCuotas;
    }

    public void setVerCuotas(boolean verCuotas)
    {
        this.verCuotas = verCuotas;
    }

    public boolean isVerDescEsp()
    {
        return verDescEsp;
    }

    public void setVerDescEsp(boolean verDescEsp)
    {
        this.verDescEsp = verDescEsp;
    }

    public boolean isVerPorcDescEsp()
    {
        return verPorcDescEsp;
    }

    public void setVerPorcDescEsp(boolean verPorcDescEsp)
    {
        this.verPorcDescEsp = verPorcDescEsp;
    }

    public String getTituloDescEsp()
    {
        return tituloDescEsp;
    }

    public void setTituloDescEsp(String tituloDescEsp)
    {
        this.tituloDescEsp = tituloDescEsp;
    }

    public String getTotalImpuesto()
    {
        return totalImpuesto;
    }

    public void setTotalImpuesto(String totalImpuesto)
    {
        this.totalImpuesto = totalImpuesto;
    }

    public String getTotalInteres()
    {
        return totalInteres;
    }

    public void setTotalInteres(String totalInteres)
    {
        this.totalInteres = totalInteres;
    }

    public String getTotalCar()
    {
        return totalCar;
    }

    public void setTotalCar(String totalCar)
    {
        this.totalCar = totalCar;
    }

    public String getTotalCarInt()
    {
        return totalCarInt;
    }

    public void setTotalCarInt(String totalCarInt)
    {
        this.totalCarInt = totalCarInt;
    }

    public String getTotalDesc()
    {
        return totalDesc;
    }

    public void setTotalDesc(String totalDesc)
    {
        this.totalDesc = totalDesc;
    }

    public String getTotalC14()
    {
        return totalC14;
    }

    public void setTotalC14(String totalC14)
    {
        this.totalC14 = totalC14;
    }

    public String getTotalOtros()
    {
        return totalOtros;
    }

    public void setTotalOtros(String totalOtros)
    {
        this.totalOtros = totalOtros;
    }

    public String getTotalDeuda()
    {
        return totalDeuda;
    }

    public void setTotalDeuda(String totalDeuda)
    {
        this.totalDeuda = totalDeuda;
    }

    public boolean isIndVerDgProp()
    {
        return indVerDgProp;
    }

    public void setIndVerDgProp(boolean indVerDgProp)
    {
        this.indVerDgProp = indVerDgProp;
    }

    public String getIdPropietarioFac()
    {
        return idPropietarioFac;
    }

    public void setIdPropietarioFac(String idPropietarioFac)
    {
        this.idPropietarioFac = idPropietarioFac;
    }

    public String getNomPropietarioFac()
    {
        return nomPropietarioFac;
    }

    public void setNomPropietarioFac(String nomPropietarioFac)
    {
        this.nomPropietarioFac = nomPropietarioFac;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNomC14()
    {
        return nomC14;
    }

    public void setNomC14(String nomC14)
    {
        this.nomC14 = nomC14;
    }

    public boolean isVerAnularFactura()
    {
        return verAnularFactura;
    }

    public void setVerAnularFactura(boolean verAnularFactura)
    {
        this.verAnularFactura = verAnularFactura;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaanofin()
    {
        return listaanofin;
    }

    public void setListaanofin(List<Registro> listaanofin)
    {
        this.listaanofin = listaanofin;
    }

    public List<Registro> getListaanoAbono()
    {
        return listaanoAbono;
    }

    public void setListaanoAbono(List<Registro> listaanoAbono)
    {
        this.listaanoAbono = listaanoAbono;
    }

    public List<Registro> getListaAnoInicial()
    {
        return listaAnoInicial;
    }

    public void setListaAnoInicial(List<Registro> listaAnoInicial)
    {
        this.listaAnoInicial = listaAnoInicial;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCmbDescesp()
    {
        return listaCmbDescesp;
    }

    public void setListaCmbDescesp(RegistroDataModelImpl listaCmbDescesp)
    {
        this.listaCmbDescesp = listaCmbDescesp;
    }

    public RegistroDataModelImpl getListaPropietario()
    {
        return listaPropietario;
    }

    public void setListaPropietario(RegistroDataModelImpl listaPropietario)
    {
        this.listaPropietario = listaPropietario;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaFacturados2()
    {
        return listaFacturados2;
    }

    public void setListaFacturados2(List<Registro> listaFacturados2)
    {
        this.listaFacturados2 = listaFacturados2;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSub()
    {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    public String getVlrInd1175()
    {
        return vlrInd1175;
    }

    public void setVlrInd1175(String vlrInd1175)
    {
        this.vlrInd1175 = vlrInd1175;
    }

    public String getOrdenPropietarioFac()
    {
        return ordenPropietarioFac;
    }

    public void setOrdenPropietarioFac(String ordenPropietarioFac)
    {
        this.ordenPropietarioFac = ordenPropietarioFac;
    }

    public boolean isVlrCheck1175()
    {
        return vlrCheck1175;
    }

    public void setVlrCheck1175(boolean vlrCheck1175)
    {
        this.vlrCheck1175 = vlrCheck1175;
    }

    // </SET_GET_ADICIONALES>
}
