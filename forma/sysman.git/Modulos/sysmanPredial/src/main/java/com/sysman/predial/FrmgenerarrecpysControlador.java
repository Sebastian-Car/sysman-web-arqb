package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.FrmgenerarrecpysControladorEnum;
import com.sysman.predial.enums.FrmgenerarrecpysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 26/05/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author ybecerra
 * @version 3, 05/07/2017, proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped

public class FrmgenerarrecpysControlador extends BeanBaseModal
{

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean indRegistrado;
    private String codigoPredio;
    private String propietario;
    private String nombrePredio;
    private String direccion;
    private String cedula;
    private String txtFactura;
    private String nombreUsuario;
    private String identificacion;
    private String recibo;
    private String vlrPys;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    private boolean manejaCodigoDeBarrasMultimpuesto;
    // </DECLARAR_ATRIBUTOS>
    private static final String JS_MOSTRAR_DG79 = "PF('DG79').show()";
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigopredio;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String txtTituloAldia;
    private String strPredio;
    private String strOrden;
    private String strNitNoInscrito;
    private String strNomNoInscrito;
    private String eliminarAnt;
    private String dialogo;
    private String tipoFra;
    private String secuencia1;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    /**
     * Crea una nueva instancia de FrmgenerarrecpysControlador
     */
    public FrmgenerarrecpysControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMGENERARRECPYS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmgenerarrecpysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoPredio();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        initAdicional();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacodigopredio
     */
    public void cargarListaCodigoPredio()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmgenerarrecpysControladorUrlEnum.URL4372
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listacodigopredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdAceptar en la vista
     *
     */
    public void oprimircmdAceptar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (faltanCamposObligatorios())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1075"));
            return;
        }
        if (indRegistrado)
        {
            if (tieneAsociadoPredios())
            {
                return;
            }

            strPredio = "999999999999999";
            strNitNoInscrito = identificacion;
            strNomNoInscrito = nombreUsuario;

            generaReciboPyS();
        }
        else
        {
            strPredio = codigoPredio;
            strNitNoInscrito = cedula;
            strNomNoInscrito = nombrePredio;
            strOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;

            generaReciboPyS();
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control indNoRegistrado
     * 
     */
    public void cambiarindNoRegistrado()
    {
        // <CODIGO_DESARROLLADO>
        if (indRegistrado)
        {
            codigoPredio = cedula = direccion = txtFactura = propietario = nombrePredio = "";
        }
        else
        {
            nombreUsuario = identificacion = "";
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dialogoAlDia en la vista
     *
     */
    public void aceptardialogoAlDia()
    {
        // <CODIGO_DESARROLLADO>
        switch (dialogo)
        {
        case "1":
        case "2":
            generaReciboPyS();
            break;
        case "3":
            registrarEnPagosWeb();
            getReporte();
            break;
        default:
            break;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * dialogoAlDia en la vista
     *
     */
    public void cancelardialogoAlDia()
    {
        // <CODIGO_DESARROLLADO>
        switch (dialogo)
        {
        case "1":
        case "2":
            dialogo = null;
            generaReciboPyS();
            break;
        case "3":
            eliminarAnt = "";
            break;
        default:
            break;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    public void initAdicional()
    {
        manejaCodigoDeBarrasMultimpuesto = "SI".equalsIgnoreCase(getParametro(
                        "MANEJA CODIGO DE BARRAS MULTIIMPUESTO", "NO"));

        vlrPys = getParametro("TARIFA CERTIFICADO PREDIO", "0");
    }

    /**
     * Metodo que ejecuta el llamado al procedimiento registrar Pagos
     * Web
     */
    private void registrarEnPagosWeb()
    {

        try
        {
            ejbPredialOcho.registrarPagosWeb(compania, recibo, strPredio,
                            strOrden, vlrPys, strNomNoInscrito,
                            strNitNoInscrito, SessionUtil.getUser().getCodigo(),
                            eliminarAnt);
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault)
    {
        String parametro = null;
        try
        {
            parametro = ejbSysmanUtl.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Metodo que se ejecuta al darle clic al boton PDF o excel
     */
    public void getReporte()
    {

        try
        {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmgenerarrecpysControladorUrlEnum.URL770
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            recibo);
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(FrmgenerarrecpysControladorEnum.PARAM6.getValue(),
                            secuencia1);
            fields.put(FrmgenerarrecpysControladorEnum.PARAM5.getValue(),
                            tipoFra);

            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoEAN", getParametro("CODIGO EAN", ""));
            reemplazar.put("codFac", manejaCodigoDeBarrasMultimpuesto
                ? "'609990000000000'" : "IP_PAGOS_WEB.CODIGO_PREDIO");
            reemplazar.put("recibo", recibo);
            reemplazar.put("nombres", indRegistrado ? "'" + nombreUsuario + "'"
                : "IP_USUARIOS_PREDIAL.NOMBRE");
            reemplazar.put("nit", indRegistrado
                ? "IP_PAGOS_WEB.NIT_NO_INSCRITRO" : "IP_USUARIOS_PREDIAL.NIT");

            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> parametros = new HashMap<>();

            String reporte = getParametro("FACTURA PAZ Y SALVO", "");
            if ("".equals(reporte))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1086"));
                return;
            }
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_OBSERVACIONES_FACTURA_PAZ_Y_SALVO", getParametro(
                            "OBSERVACIONES FACTURA PAZ Y SALVO", ""));

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_LEYENDA_FACTURA_PAZ_Y_SALVO",
                            getParametro("LEYENDA FACTURA PAZ Y SALVO", ""));
            parametros.put("PR_MANEJAMULTIIMPUESTO",
                            manejaCodigoDeBarrasMultimpuesto);
            parametros.put("PR_PAGINAWEB", "PAGINA WEEEEEEEEEEEEEEEEEEEEEEEEB");

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException | SystemException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(FrmgenerarrecpysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verifica si esta pago con el parametro en NO.
     * 
     * @param pagoAno
     * @param anio
     * ano actual
     * @return si esta pago
     */
    private boolean estaPagoSinVigenciasAnteriores(String pagoAno, int anio)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), strPredio);
        boolean estaPago = false;
        try
        {

            int anioRegistro = pagoAno == null ? 0 : Integer.parseInt(pagoAno);
            if (anioRegistro == anio)
            {
                estaPago = true;
            }
            else
            {

                Registro rsFac;

                rsFac = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmgenerarrecpysControladorUrlEnum.URL700
                                                                                .getValue())
                                                .getUrl(), param));

                if (Integer.parseInt(
                                rsFac.getCampos().get("PAG").toString()) != 0)
                {
                    estaPago = false;
                    return estaPago;
                }

                param.put(GeneralParameterEnum.ANO.getName(), anio);
                rsFac = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmgenerarrecpysControladorUrlEnum.URL718
                                                                                .getValue())
                                                .getUrl(), param));

                if (rsFac != null
                    && (!"0".equals(rsFac.getCampos().get("PAGADO"))))
                {
                    estaPago = true;
                }
                else
                {
                    estaPago = false;
                }
            }
            /*
             * verificar si tiene excedentes por cobrar para vigencias
             * iguales o inferiores a la actual.
             */
            Registro rsFac = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgenerarrecpysControladorUrlEnum.URL737
                                                                            .getValue())
                                            .getUrl(), param));

            if (!"0".equals(rsFac.getCampos().get("CANT")))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1083").replace("#cant#",
                                                rsFac.getCampos().get("CANT")
                                                                .toString()));
                estaPago = false;
                return estaPago;
            }
        }
        catch (SystemException e)
        {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return estaPago;
    }

    /**
     * Verifica si el usuario tiene asociado predios, ya sea por NIT o
     * por nombre de usuario.
     * 
     * @return Verdadero si el usuario tiene predios asociados.
     */
    private boolean tieneAsociadoPredios()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmgenerarrecpysControladorEnum.PARAM0.getValue(),
                            identificacion);

            String variablePredios = "#predios#";
            StringBuilder codigosPredios = new StringBuilder("");
            List<Registro> predios;

            predios = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgenerarrecpysControladorUrlEnum.URL222
                                                                            .getValue())
                                            .getUrl(), param));

            if (!predios.isEmpty())
            {
                for (Registro predio : predios)
                {
                    codigosPredios.append(predio.getCampos()
                                    .get(GeneralParameterEnum.CODIGO
                                                    .getName()));
                    codigosPredios.append(" ");
                }
                if (predios.size() > 1)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1076").replace(
                                                    variablePredios,
                                                    codigosPredios));

                }
                else
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1077").replace(
                                                    variablePredios,
                                                    codigosPredios));
                }
                return true;
            }
            param.clear();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmgenerarrecpysControladorEnum.PARAM1.getValue(),
                            nombreUsuario);
            predios = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgenerarrecpysControladorUrlEnum.URL254
                                                                            .getValue())
                                            .getUrl(), param));
            if (!predios.isEmpty())
            {
                for (Registro registro : predios)
                {
                    codigosPredios.append(registro.getCampos()
                                    .get(GeneralParameterEnum.CODIGO
                                                    .getName()));
                    codigosPredios.append(" ");
                }
                if (predios.size() > 1)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1076").replace(
                                                    variablePredios,
                                                    codigosPredios));

                }
                else
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1077").replace(
                                                    variablePredios,
                                                    codigosPredios));
                }
                return true;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return false;
    }

    /**
     * Validacion de campos obligatorios.
     * 
     * @return Verdadero si faltan campos por diligenciar.
     */
    private boolean faltanCamposObligatorios()
    {
        if (indRegistrado)
        {
            return SysmanFunciones.validarVariableVacio(nombreUsuario)
                || SysmanFunciones.validarVariableVacio(identificacion);
        }
        else
        {
            return SysmanFunciones.validarVariableVacio(codigoPredio);
        }
    }

    public void generaReciboPyS()
    {
        if ("999999999999999".equals(strPredio))
        {
            strOrden = "999";
        }
        else if ((!estaPago()) && (dialogo == null))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1079"));
            return;
        }

        String manejaNumeracionUnica = getParametro("MANEJA NUMERACION UNICA",
                        "NO");

        if ("SI".equals(manejaNumeracionUnica))
        {
            tipoFra = "N";
        }
        else
        {
            tipoFra = "P";
        }

        if (!procesarNumeroFactura())
        {
            return;
        }

        if (!procesarPagosWeb())
        {
            return;
        }

    }

    /**
     * Creacion del registro del recibo de paz y salvo.
     * 
     * @return Falso si no se encuentran pagos o ocurren errores.
     */
    private boolean procesarPagosWeb()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), strPredio);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            strOrden);

            Registro pagosWeb;

            pagosWeb = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgenerarrecpysControladorUrlEnum.URL366
                                                                            .getValue())
                                            .getUrl(), param));

            if (pagosWeb == null)
            {

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmgenerarrecpysControladorUrlEnum.URL380
                                                                .getValue());

                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                fields.put(GeneralParameterEnum.REFERENCIA.getName(), recibo);
                fields.put(GeneralParameterEnum.PREDIO.getName(), strPredio);
                fields.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                strOrden);
                fields.put(FrmgenerarrecpysControladorEnum.PARAM2.getValue(),
                                vlrPys);
                fields.put(FrmgenerarrecpysControladorEnum.PARAM3.getValue(),
                                strNomNoInscrito);
                fields.put(FrmgenerarrecpysControladorEnum.PARAM4.getValue(),
                                strNitNoInscrito);
                fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                Parameter parameter = new Parameter();
                parameter.setFields(fields);

                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                parameter);

                getReporte();
            }
            else
            {
                String referencia = pagosWeb.getCampos()
                                .get("REFERENCIA").toString();
                dialogo = "3";
                txtTituloAldia = idioma.getString("TB_TB1082")
                                .replace("#referencia#", referencia)
                                .replace("#strPredio#", strPredio);
                eliminarAnt = referencia == null ? "" : referencia;
                JsfUtil.ejecutarJavaScript(JS_MOSTRAR_DG79);
                return false;
            }
        }
        catch (SystemException e1)
        {
            JsfUtil.agregarMensajeError(e1.getMessage());
            logger.error(e1.getMessage(), e1);

        }
        return true;
    }

    /**
     * Captura la secuencia y el consecutivo del recibo de paz y
     * salvo.
     * 
     * @return Falso si no existe el numero de factura.
     */
    private boolean procesarNumeroFactura()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmgenerarrecpysControladorEnum.PARAM5.getValue(),
                        tipoFra);

        Registro numeroFactura;
        try
        {

            numeroFactura = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgenerarrecpysControladorUrlEnum.URL448
                                                                            .getValue())
                                            .getUrl(), param));

            String campoConsecutivoReal = "CONSECUTIVOREAL";

            if ((numeroFactura == null)
                || (numeroFactura.getCampos()
                                .get(campoConsecutivoReal) == null))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1080"));
                return false;
            }
            else
            {
                secuencia1 = numeroFactura.getCampos().get("SECUENCIA1")
                                .toString();

                recibo = numeroFactura.getCampos()
                                .get(campoConsecutivoReal) == null
                                    ? "1"
                                    : String.valueOf(Long.parseLong(
                                                    numeroFactura.getCampos()
                                                                    .get(campoConsecutivoReal)
                                                                    .toString())
                                        + 1);

                return validarDigitos(Integer.parseInt(numeroFactura.getCampos()
                                .get("DIGITOS").toString()));
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return true;

    }

    /**
     * Verifica que la longitud de la cadena del recibo sea menor o
     * igual al parametro {@code longitud}
     * 
     * @param longitud
     * cantidad maxima de caracteres que puede tener la cadena
     * @return false : si la longitud del {@code recibo} es mayor que
     * el valor del parametro {@code longitud}
     */
    private boolean validarDigitos(int longitud)
    {
        if (longitud == recibo.length())
        {
            return true;
        }

        if (longitud > recibo.length())
        {
            recibo = SysmanFunciones.padl(recibo, longitud, "0");
            return true;
        }

        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2873")
                        .replace("#longitud#", String.valueOf(longitud)));

        return false;
    }

    /**
     * Metodo que valida si el usuario ya presenta pago
     * 
     * @return
     */
    public boolean estaPago()
    {
        try
        {
            int anio = SysmanFunciones.ano(new Date());
            boolean estaPago = false;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PREDIO.getName(), strPredio);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            Registro rsUsuario;

            rsUsuario = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgenerarrecpysControladorUrlEnum.URL538
                                                                            .getValue())
                                            .getUrl(), param));

            String permitePzYSVigenAnt = getParametro(
                            "PERMITE PAZ Y SALVO DE VIGENCIAS ANTERIORES",
                            null);

            if (permitePzYSVigenAnt == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2765"));
                return false;
            }

            String pagoAno = SysmanFunciones.nvlStr(
                            rsUsuario.getCampos().get("PAGO_ANO").toString(),
                            "0");

            if (!"SI".equals(permitePzYSVigenAnt))
            {
                estaPago = estaPagoSinVigenciasAnteriores(pagoAno, anio);
            }
            else if ("SI".equals(permitePzYSVigenAnt))
            {
                estaPago = estaPagoConVigenciasAnteriores(pagoAno, anio);
            }

            return estaPago;
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return false;
    }

    /**
     * Verifica si esta pago con el parametro en SI.
     * 
     * @param pagoAno
     * @param anio
     * ano actual
     * @return si esta pago
     */
    private boolean estaPagoConVigenciasAnteriores(String pagoAno, int anio)
    {
        boolean estaPago = false;
        try
        {

            int intAnoMax = Integer.parseInt(
                            getParametro("MAXIMA VIGENCIA DE PAZ Y SALVO POR VIGENCIAS ANTERIORES",
                                            "2050"));
            int anioPago = pagoAno == null ? 0 : Integer.parseInt(pagoAno);
            if (anioPago == anio)
            {
                estaPago = true;
            }
            else if (anioPago < intAnoMax)
            {
                estaPago = false;
            }
            else
            {

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.PREDIO.getName(), strPredio);
                param.put(GeneralParameterEnum.ANO.getName(),
                                intAnoMax);

                Registro rsFac;

                rsFac = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmgenerarrecpysControladorUrlEnum.URL611
                                                                                .getValue())
                                                .getUrl(), param));

                if ("0".equals(rsFac.getCampos().get("PAG")))
                {
                    dialogo = "1";
                    txtTituloAldia = idioma.getString("TB_TB1084")
                                    .replace("#anio#", String.valueOf(anio));
                    JsfUtil.ejecutarJavaScript(JS_MOSTRAR_DG79);
                    return estaPago;
                }
                else
                {
                    estaPago = false;
                    return estaPago;
                }

            }
            /*
             * verificar si tiene excedentes por cobrar para vigencias
             * iguales o inferiores a la actual.
             */

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PREDIO.getName(), strPredio);
            param.put(GeneralParameterEnum.ANO.getName(),
                            intAnoMax);
            Registro rsFac;

            rsFac = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmgenerarrecpysControladorUrlEnum.URL653
                                                                            .getValue())
                                            .getUrl(), param));

            if (!"0".equals(rsFac.getCampos().get("CANT").toString()))
            {
                dialogo = "2";
                txtTituloAldia = idioma.getString("TB_TB1085").replace("#cant#",
                                rsFac.getCampos().get("CANT").toString());
                JsfUtil.ejecutarJavaScript(JS_MOSTRAR_DG79);
            }
            else
            {
                estaPago = true;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return estaPago;
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigopredio
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigopredio(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoPredio = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        if (!("0".equals(registroAux.getCampos().get("CODIGOANT1") == null ? "0"
            : registroAux.getCampos().get("CODIGOANT1")))
            || Boolean.parseBoolean(registroAux.getCampos().get("INDBORRADO")
                            .toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1153"));
            codigoPredio = "";
            return;
        }
        if (Boolean.parseBoolean(registroAux.getCampos().get("ACUERDO_PAGO")
                        .toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1154"));
            codigoPredio = "";
            return;
        }
        if (Boolean.parseBoolean(registroAux.getCampos().get("IND_PROCESOJUD")
                        .toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1155"));
            codigoPredio = "";
            return;
        }
        cedula = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        nombrePredio = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        " ")
                        .toString();
        direccion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DIRECCION
                                                        .getName()),
                                        " ")
                        .toString();
        txtFactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUM_COM"), " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indRegistrado
     * 
     * @return indRegistrado
     */
    public boolean getIndRegistrado()
    {
        return indRegistrado;
    }

    /**
     * Asigna la variable indRegistrado
     * 
     * @param indRegistrado
     * Variable a asignar en indRegistrado
     */
    public void setIndRegistrado(boolean indRegistrado)
    {
        this.indRegistrado = indRegistrado;
    }

    /**
     * Retorna la variable codigoPredio
     * 
     * @return codigoPredio
     */
    public String getCodigoPredio()
    {
        return codigoPredio;
    }

    /**
     * Asigna la variable codigoPredio
     * 
     * @param codigoPredio
     * Variable a asignar en codigoPredio
     */
    public void setCodigoPredio(String codigoPredio)
    {
        this.codigoPredio = codigoPredio;
    }

    /**
     * Retorna la variable nombrePredio
     * 
     * @return nombrePredio
     */
    public String getPropietario()
    {
        return propietario;
    }

    /**
     * Asigna la variable nombrePredio
     * 
     * @param nombrePredio
     * Variable a asignar en nombrePredio
     */
    public void setPropietario(String propietario)
    {
        this.propietario = propietario;
    }

    /**
     * Retorna la variable nombrePredio
     * 
     * @return nombrePredio
     */
    public String getNombrePredio()
    {
        return nombrePredio;
    }

    /**
     * Asigna la variable nombrePredio
     * 
     * @param nombrePredio
     * Variable a asignar en nombrePredio
     */
    public void setNombrePredio(String nombrePredio)
    {
        this.nombrePredio = nombrePredio;
    }

    /**
     * Retorna la variable direccion
     * 
     * @return direccion
     */
    public String getDireccion()
    {
        return direccion;
    }

    /**
     * Asigna la variable direccion
     * 
     * @param direccion
     * Variable a asignar en direccion
     */
    public void setDireccion(String direccion)
    {
        this.direccion = direccion;
    }

    /**
     * Retorna la variable cedula
     * 
     * @return cedula
     */
    public String getCedula()
    {
        return cedula;
    }

    /**
     * Asigna la variable cedula
     * 
     * @param cedula
     * Variable a asignar en cedula
     */
    public void setCedula(String cedula)
    {
        this.cedula = cedula;
    }

    /**
     * Retorna la variable txtFactura
     * 
     * @return txtFactura
     */
    public String getTxtFactura()
    {
        return txtFactura;
    }

    /**
     * Asigna la variable txtFactura
     * 
     * @param txtFactura
     * Variable a asignar en txtFactura
     */
    public void setTxtFactura(String txtFactura)
    {
        this.txtFactura = txtFactura;
    }

    /**
     * Retorna la variable nombreUsuario
     * 
     * @return nombreUsuario
     */
    public String getNombreUsuario()
    {
        return nombreUsuario;
    }

    /**
     * Asigna la variable nombreUsuario
     * 
     * @param nombreUsuario
     * Variable a asignar en nombreUsuario
     */
    public void setNombreUsuario(String nombreUsuario)
    {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Retorna la variable identificacion
     * 
     * @return identificacion
     */
    public String getIdentificacion()
    {
        return identificacion;
    }

    /**
     * Asigna la variable identificacion
     * 
     * @param identificacion
     * Variable a asignar en identificacion
     */
    public void setIdentificacion(String identificacion)
    {
        this.identificacion = identificacion;
    }

    /**
     * Retorna la variable recibo
     * 
     * @return recibo
     */
    public String getRecibo()
    {
        return recibo;
    }

    /**
     * Asigna la variable recibo
     * 
     * @param recibo
     * Variable a asignar en recibo
     */
    public void setRecibo(String recibo)
    {
        this.recibo = recibo;
    }

    /**
     * Retorna la variable vlrPys
     * 
     * @return vlrPys
     */
    public String getVlrPys()
    {
        return vlrPys;
    }

    /**
     * Asigna la variable vlrPys
     * 
     * @param vlrPys
     * Variable a asignar en vlrPys
     */
    public void setVlrPys(String vlrPys)
    {
        this.vlrPys = vlrPys;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna la variable txtTituloAldia
     * 
     * @return txtTituloAldia
     */
    public String getTxtTituloAldia()
    {
        return txtTituloAldia;
    }

    /**
     * Asigna la variable txtTituloAldia
     * 
     * @param txtTituloAldia
     * Variable a asignar en txtTituloAldia
     */
    public void setTxtTituloAldia(String txtTituloAldia)
    {
        this.txtTituloAldia = txtTituloAldia;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodigopredio
     * 
     * @return listacodigopredio
     */
    public RegistroDataModelImpl getListacodigopredio()
    {
        return listacodigopredio;
    }

    /**
     * Asigna la lista listacodigopredio
     * 
     * @param listacodigopredio
     * Variable a asignar en listacodigopredio
     */
    public void setListacodigopredio(RegistroDataModelImpl listacodigopredio)
    {
        this.listacodigopredio = listacodigopredio;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}
