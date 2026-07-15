/*-
 * AnalisisCarteraUsosControlador.java
 *
 * 1.0
 * 
 * 15/11/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.AnalisisCarteraUsosControladorEnum;
import com.sysman.serviciospublicos.enums.AnalisisCarteraUsosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la vista del formulario analisiscarterausos.
 *
 * @version 1.0, 15/11/2016
 * @author pespitia
 * 
 * @version 2 , 15/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class AnalisisCarteraUsosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /** Constante de clase que contiene el valor repetivo CODIGO */
    private final String codigo;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que contiene el valor asignado al radiobutton 'y/o' en
     * la vista.
     */
    private String condicion;

    /**
     * Atributo que verifica si la casilla 'Separar por concepto' esta
     * seleccionada en la vista.
     */
    private boolean concepto;

    /**
     * Atributo que verifica si la casilla 'Cartera por concepto' esta
     * seleccionada en la vista.
     */
    private boolean cartera;

    /**
     * Atributo que controla la visibilidad de la casilla 'Cartera por
     * Edades' en la vista.
     */
    private boolean visibleCartera;

    /**
     * Atributo que verifica si la casilla 'Agrupar por servicio' esta
     * seleccionada en la vista.
     */
    private boolean agrupar;

    /**
     * Atributo que controla la visibilidad de la casilla 'Agrupar por
     * Servicio' en la vista.
     */
    private boolean visibleAgrupar;

    /**
     * Atributo que continene el valor asignado al control ciclo
     * inicial de la vista.
     */
    private String cicloInicial;

    /**
     * Atributo que contiene el valor asignado al combo 'Uso inicial'
     * en la vista.
     */
    private String usoInicial;

    /**
     * Atributo que contiene el valor asignado al combo 'Estrato
     * inicial' en la vista.
     */
    private String estratoInicial;

    /**
     * Atributo que contiene el valor asignado al combo 'Uso final' en
     * la vista.
     */
    private String usoFinal;

    /**
     * Atributo que contiene el valor asignado al combo 'Estrato
     * final' en la vista.
     */
    private String estratoFinal;

    /**
     * Atributo que continene el valor asignado al control ciclo final
     * de la vista.
     */
    private String cicloFinal;

    /**
     * Atributo que contiene el valor asignado al control 'periodos de
     * atraso entre' de la vista.
     */
    private String periodoInicial;

    /**
     * Atributo que contiene el valor asignado al campo 'Valores
     * superiores a' en la vista.
     */
    private double superior;

    /**
     * Atributo que contiene el valor asignado al campo 'y' de la
     * vista.
     */
    private String periodoFinal;

    /**
     * Atributo que verifica si la casilla 'Incluir suspendidos y
     * retirados' esta seleccionada en la vista.
     */
    private boolean incluir;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /** Lista que contine los items del combo 'Ciclo final.' */
    private List<Registro> listaCicloInicial;

    /** Lista que contine los items del combo 'Ciclo final.' */
    private List<Registro> listaCicloFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista que contine los items del combo 'Uso inicial.' */
    private RegistroDataModelImpl listaUsoInicial;

    /** Lista que contine los items del combo 'Estrato inicial.' */
    private RegistroDataModelImpl listaEstratoInicial;

    /** Lista que contine los items del combo 'Uso final.' */
    private RegistroDataModelImpl listaUsoFinal;

    /** Lista que contine los items del combo 'Estrato final.' */
    private RegistroDataModelImpl listaEstratoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AnalisisCarteraUsosControlador
     */
    public AnalisisCarteraUsosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        codigo = GeneralParameterEnum.CODIGO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ANALISIS_CARTERA_USOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            condicion = "1";
            usoInicial = "00";
            usoFinal = "99";
            estratoInicial = "00";
            estratoFinal = "99";
            superior = 0.0;
            periodoInicial = "0";
            periodoFinal = "99999";

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Verifica si la compania tiene el nombre de la propiedad
     * TB_TB2688.
     * 
     * @return true si la compania actual tiene el mismo nombre de la
     * propiedad TB_TB2688.
     */
    public boolean verificarCompania()
    {
        String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        return idioma.getString("TB_TB2688").equals(nombreCompania);
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
        try
        {
            visibleCartera = verificarCompania();

            /* Verifica que el parametro exista y tenga valor SI */

            visibleAgrupar = "SI".equals(
                            SysmanFunciones.nvlStr(ejbParametro
                                            .consultarParametro(compania,
                                                            "ANALISIS DE CARTERA POR SERVICIO",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false)

            // Acciones.getParametro(
            // ConectorPool.ESQUEMA_SYSMAN,
            // compania,
            // "ANALISIS DE CARTERA POR SERVICIO",
            // SessionUtil.getModulo(), "SYSDATE")

            ,
                                            "NO"));

            // <CARGAR_LISTA>
            cargarListaCicloInicial();
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListaUsoInicial();
            cargarListaUsoFinal();
            cargarListaEstratoInicial();

            // </CARGAR_LISTA_COMBO_GRANDE>
            // <CREAR_ARBOLES>
            // </CREAR_ARBOLES>
            abrirFormulario();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la lista del combo 'Ciclo inicial'. */
    public void cargarListaCicloInicial()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCicloInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnalisisCarteraUsosControladorUrlEnum.URL8569
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga la lista del combo 'Ciclo final'. */
    public void cargarListaCicloFinal()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(AnalisisCarteraUsosControladorEnum.PARAM0.getValue(),
                            cicloInicial);

            listaCicloFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnalisisCarteraUsosControladorUrlEnum.URL8961
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga la lista del combo 'Uso inicial'. */
    public void cargarListaUsoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisisCarteraUsosControladorUrlEnum.URL9419
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaUsoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /** Carga la lista del combo 'Uso final'. */
    public void cargarListaUsoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisisCarteraUsosControladorUrlEnum.URL9979
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(AnalisisCarteraUsosControladorEnum.PARAM1.getValue(),
                        usoInicial);

        listaUsoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /** Carga la lista del combo 'Estrato inicial'. */
    public void cargarListaEstratoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisisCarteraUsosControladorUrlEnum.URL1313
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(AnalisisCarteraUsosControladorEnum.PARAM1.getValue(),
                        usoInicial);

        param.put(AnalisisCarteraUsosControladorEnum.PARAM2.getValue(),
                        usoFinal);

        listaEstratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /** Carga la lista del combo 'Estrato final'. */
    public void cargarListaEstratoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisisCarteraUsosControladorUrlEnum.URL1414
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(AnalisisCarteraUsosControladorEnum.PARAM3.getValue(),
                        estratoInicial);

        param.put(AnalisisCarteraUsosControladorEnum.PARAM1.getValue(),
                        usoInicial);

        param.put(AnalisisCarteraUsosControladorEnum.PARAM2.getValue(),
                        usoFinal);

        listaEstratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /** Metodo ejecutado al oprimir el boton Pdf en la vista. */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (verificarEdadesConcepto() && verificarCicloFinal()
            && verificarPeriodos())
        {
            generarReporte(FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado al oprimir el boton Excel en la vista. */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (verificarEdadesConcepto() && verificarCicloFinal()
            && verificarPeriodos())
        {
            generarReporte(FORMATOS.EXCEL97);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Valida el valor nulo del combo CicloFinal.
     * 
     * @return true
     */
    public boolean verificarCicloFinal()
    {
        boolean key = cicloFinal != null;

        if (!key)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2689"));
        }

        /* Permitir valor nulo del combo CicloFinal */
        if (cartera)
        {
            cicloFinal = cicloInicial;
        }

        return key || cartera;
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Extension o tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato)
    {
        String reporte = seleccionarReporte();
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("cicloInicial", cicloInicial);
            reemplazar.put("cicloFinal", cicloFinal);
            reemplazar.put("columnaServicio", agrupar ? " ,C.SERVICIO" : " ");
            reemplazar.put("periodoAtrasoInicial", periodoInicial);
            reemplazar.put("periodoAtrasoFinal", periodoFinal);
            reemplazar.put("condicionTot", condicionarTotalFactura());
            reemplazar.put("usoEstratoInicial",
                            colocarComillas(usoInicial, estratoInicial));
            reemplazar.put("usoEstratoFinal",
                            colocarComillas(usoFinal, estratoFinal));
            reemplazar.put("agruparConcepto",
                            concepto ? " ,F.CONCEPTO, C.NOMBRE " : " ");
            reemplazar.put("columnasConcepto", concepto
                ? " ,F.CONCEPTO, C.NOMBRE NOMBRECONCEPTO " : " ");
            reemplazar.put("ordenAgrupar", ordenarAgrupar());
            reemplazar.put("condicionEstado",
                            incluir ? "" : "AND U.ESTADO NOT IN('R','S')");

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_CICLOINICIAL", cicloInicial);
            parametros.put("PR_CICLOFINAL", cicloFinal);
            parametros.put("PR_PERIODO", recuperarPeriodo());
            parametros.put("PR_FRECUENCIA", recuperarFrecuencia());

            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        catch (JRException | IOException | SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Determina la consulta y el reporte asociado a generar.
     * 
     * @return consulta y reporte asociado.
     */
    public String seleccionarReporte()
    {
        String reporte;

        if (cartera)
        {
            reporte = "001247InfCarteraEdadesUsoEstrato";
        }
        else
        {
            reporte = concepto ? "001289AnalisisCarteraUsosUno"
                : agrupar ? "001295AnalisisUsoyEstratoTotalServicio"
                    : "001299AnalisisUsoyEstratoTotal";
        }
        return reporte;
    }

    /**
     * Determina la frecuencia de los periodos de facturacion.
     * 
     * @return valor de la frecuencia.
     */
    public int recuperarFrecuencia()
    {

        String valor = "NO";
        try
        {
            valor = SysmanFunciones.nvlStr(ejbParametro.consultarParametro(
                            compania, "FRECUENCIA PERIODOS DE FACTURACION",
                            SessionUtil.getModulo(), new Date(), false), "NO");

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return "M".equals(valor) ? 30 : 60;
    }

    /**
     * Concatena el uso y el estrato y los coloca entre comillas
     * simples.
     * 
     * @param uso
     * @param estrato
     * @return El uso y el estrato entre comillas.
     */
    public String colocarComillas(String uso, String estrato)
    {
        return "' " + uso + " " + estrato + "'";
    }

    /**
     * Genera la condicion para filtrar por TOTFACTURAPERACTUAL.
     * Cuando la condicion tenga valor "1" filtre con AND, de lo
     * contrario con OR.
     * 
     * @return Condicion para filtrar por el atributo
     * TOTFACTURAPERACTUAL de la tabla SP_USUARIO.
     */
    public String condicionarTotalFactura()
    {
        return "1".equals(condicion)
            ? " AND U.TOTFACTURAPERACTUAL >= " + superior
            : " OR U.TOTFACTURAPERACTUAL >= " + superior;
    }

    /**
     * Recupera el nombre del periodo del ciclo seleccionado en el
     * formulario.
     * 
     * @return El nombre del periodo del cicloInicial.
     */
    public String recuperarPeriodo()
    {
        Registro registro = null;
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(AnalisisCarteraUsosControladorEnum.PARAM0.getValue(),
                            cicloInicial);

            /*
             * Consulta para recuperar el periodo del ciclo y traer el
             * registro que contiene el periodo.
             */

            registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnalisisCarteraUsosControladorUrlEnum.URL6969
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return registro == null ? "TODOS"
            : registro.getCampos().get("NOMBREPERIODO").toString();

    }

    /**
     * Determina los campos por los que debe ordenar los registros de
     * la consulta, cuando este seleccionada la casilla de agrupar.
     * 
     * @return La sentencia para ordenar.
     */
    public String ordenarAgrupar()
    {
        return agrupar ? "ORDER BY C.SERVICIO, F.COMPANIA, F.ANO||F.PERIODO"
            : "ORDER BY F.COMPANIA, F.ANO||F.PERIODO";
    }

    /**
     * Verifica que la casilla 'Separar por concepto' y 'Cartera por
     * edades' no esten seleccionadas a la vez.
     * 
     * @return
     */
    private boolean verificarEdadesConcepto()
    {
        boolean key = true;

        if (concepto && cartera)
        {
            key = false;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1826"));
        }

        return key;
    }

    /**
     * Comprueba que el periodo inicial sea menor que el periodo final
     * 
     * @return true: si el periodo inicial es menor que el periodo
     * final. <br>
     * De lo contrario muestra un mensaje y retorna false.
     */
    private boolean verificarPeriodos()
    {
        if (Integer.parseInt(periodoInicial) > Integer
                        .parseInt(periodoFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3000"));
            return false;
        }

        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Gestiona los eventos de cambiar el valor del combo 'Ciclo
     * inicial'.
     */
    public void cambiarCicloInicial()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaCicloFinal();
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado al cambiar el control Incluir */
    public void cambiarIncluir()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado al cambiar el control CarteraEdades */
    public void cambiarCarteraEdades()
    {
        // <CODIGO_DESARROLLADO>
        cicloFinal = cicloInicial;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una item del combo 'Uso
     * inicial' en la vista.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaUsoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        usoInicial = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigo).toString(), "");
        usoFinal = estratoInicial = estratoFinal = null;

        cargarListaUsoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila del combo 'Estrato
     * inicial' en la vista.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaEstratoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        estratoInicial = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigo).toString(), "");
        estratoFinal = null;

        cargarListaEstratoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de combo 'Uso final'
     * en la vista.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaUsoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        usoFinal = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigo).toString(), "");

        estratoInicial = estratoFinal = null;

        cargarListaEstratoInicial();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila del combo 'Estrato
     * final' en la vista.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaEstratoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        estratoFinal = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigo).toString(), "");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable condicion
     * 
     * @return condicion
     */
    public String getCondicion()
    {
        return condicion;
    }

    /**
     * Asigna la variable condicion
     * 
     * @param condicion
     * Variable a asignar en condicion
     */
    public void setCondicion(String condicion)
    {
        this.condicion = condicion;
    }

    public boolean isConcepto()
    {
        return concepto;
    }

    public void setConcepto(boolean concepto)
    {
        this.concepto = concepto;
    }

    public boolean isCartera()
    {
        return cartera;
    }

    public void setCartera(boolean cartera)
    {
        this.cartera = cartera;
    }

    public boolean isAgrupar()
    {
        return agrupar;
    }

    public void setAgrupar(boolean agrupar)
    {
        this.agrupar = agrupar;
    }

    /**
     * Retorna la variable cicloInicial
     * 
     * @return cicloInicial
     */
    public String getCicloInicial()
    {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     * 
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial)
    {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable usoInicial
     * 
     * @return usoInicial
     */
    public String getUsoInicial()
    {
        return usoInicial;
    }

    /**
     * Asigna la variable usoInicial
     * 
     * @param usoInicial
     * Variable a asignar en usoInicial
     */
    public void setUsoInicial(String usoInicial)
    {
        this.usoInicial = usoInicial;
    }

    /**
     * Retorna la variable estratoInicial
     * 
     * @return estratoInicial
     */
    public String getEstratoInicial()
    {
        return estratoInicial;
    }

    /**
     * Asigna la variable estratoInicial
     * 
     * @param estratoInicial
     * Variable a asignar en estratoInicial
     */
    public void setEstratoInicial(String estratoInicial)
    {
        this.estratoInicial = estratoInicial;
    }

    /**
     * Retorna la variable usoFinal
     * 
     * @return usoFinal
     */
    public String getUsoFinal()
    {
        return usoFinal;
    }

    /**
     * Asigna la variable usoFinal
     * 
     * @param usoFinal
     * Variable a asignar en usoFinal
     */
    public void setUsoFinal(String usoFinal)
    {
        this.usoFinal = usoFinal;
    }

    /**
     * Retorna la variable estratoFinal
     * 
     * @return estratoFinal
     */
    public String getEstratoFinal()
    {
        return estratoFinal;
    }

    /**
     * Asigna la variable estratoFinal
     * 
     * @param estratoFinal
     * Variable a asignar en estratoFinal
     */
    public void setEstratoFinal(String estratoFinal)
    {
        this.estratoFinal = estratoFinal;
    }

    /**
     * Retorna la variable cicloFinal
     * 
     * @return cicloFinal
     */
    public String getCicloFinal()
    {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     * 
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal)
    {
        this.cicloFinal = cicloFinal;
    }

    /**
     * Retorna la variable periodoInicial
     * 
     * @return periodoInicial
     */
    public String getPeriodoInicial()
    {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     * 
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial)
    {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable superior
     * 
     * @return superior
     */
    public double getSuperior()
    {
        return superior;
    }

    /**
     * Asigna la variable superior
     * 
     * @param superior
     * Variable a asignar en superior
     */
    public void setSuperior(double superior)
    {
        this.superior = superior;
    }

    /**
     * Retorna la variable periodoFinal
     * 
     * @return periodoFinal
     */
    public String getPeriodoFinal()
    {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     * 
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal)
    {
        this.periodoFinal = periodoFinal;
    }

    public boolean isIncluir()
    {
        return incluir;
    }

    public void setIncluir(boolean incluir)
    {
        this.incluir = incluir;
    }

    public boolean isVisibleAgrupar()
    {
        return visibleAgrupar;
    }

    public void setVisibleAgrupar(boolean visibleAgrupar)
    {
        this.visibleAgrupar = visibleAgrupar;
    }

    public boolean isVisibleCartera()
    {
        return visibleCartera;
    }

    public void setVisibleCartera(boolean visibleCartera)
    {
        this.visibleCartera = visibleCartera;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCicloInicial
     * 
     * @return listaCicloInicial
     */
    public List<Registro> getListaCicloInicial()
    {
        return listaCicloInicial;
    }

    /**
     * Asigna la lista listaCicloInicial
     * 
     * @param listaCicloInicial
     * Variable a asignar en listaCicloInicial
     */
    public void setListaCicloInicial(List<Registro> listaCicloInicial)
    {
        this.listaCicloInicial = listaCicloInicial;
    }

    /**
     * Retorna la lista listaCicloFinal
     * 
     * @return listaCicloFinal
     */
    public List<Registro> getListaCicloFinal()
    {
        return listaCicloFinal;
    }

    /**
     * Asigna la lista listaCicloFinal
     * 
     * @param listaCicloFinal
     * Variable a asignar en listaCicloFinal
     */
    public void setListaCicloFinal(List<Registro> listaCicloFinal)
    {
        this.listaCicloFinal = listaCicloFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaUsoInicial
     * 
     * @return listaUsoInicial
     */
    public RegistroDataModelImpl getListaUsoInicial()
    {
        return listaUsoInicial;
    }

    /**
     * Asigna la lista listaUsoInicial
     * 
     * @param listaUsoInicial
     * Variable a asignar en listaUsoInicial
     */
    public void setListaUsoInicial(RegistroDataModelImpl listaUsoInicial)
    {
        this.listaUsoInicial = listaUsoInicial;
    }

    /**
     * Retorna la lista listaEstratoInicial
     * 
     * @return listaEstratoInicial
     */
    public RegistroDataModelImpl getListaEstratoInicial()
    {
        return listaEstratoInicial;
    }

    /**
     * Asigna la lista listaEstratoInicial
     * 
     * @param listaEstratoInicial
     * Variable a asignar en listaEstratoInicial
     */
    public void setListaEstratoInicial(
        RegistroDataModelImpl listaEstratoInicial)
    {
        this.listaEstratoInicial = listaEstratoInicial;
    }

    /**
     * Retorna la lista listaUsoFinal
     * 
     * @return listaUsoFinal
     */
    public RegistroDataModelImpl getListaUsoFinal()
    {
        return listaUsoFinal;
    }

    /**
     * Asigna la lista listaUsoFinal
     * 
     * @param listaUsoFinal
     * Variable a asignar en listaUsoFinal
     */
    public void setListaUsoFinal(RegistroDataModelImpl listaUsoFinal)
    {
        this.listaUsoFinal = listaUsoFinal;
    }

    /**
     * Retorna la lista listaEstratoFinal
     * 
     * @return listaEstratoFinal
     */
    public RegistroDataModelImpl getListaEstratoFinal()
    {
        return listaEstratoFinal;
    }

    /**
     * Asigna la lista listaEstratoFinal
     * 
     * @param listaEstratoFinal
     * Variable a asignar en listaEstratoFinal
     */
    public void setListaEstratoFinal(RegistroDataModelImpl listaEstratoFinal)
    {
        this.listaEstratoFinal = listaEstratoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
