/*-
 * LusuarioatrasosAtraso.java
 *
 * 1.0
 *
 * 11/11/2016
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUnoRemote;
import com.sysman.serviciospublicos.enums.LusuarioAtrasosControladorEnum;
import com.sysman.serviciospublicos.enums.LusuarioAtrasosControladorUrlEnum;

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
 * Clase migrada para el formulario Reporte de Cartera, es llamado
 * desde Facturacion\Informes\Facturaciï¿½n y cartera\Reporte de cartera
 *
 * @version 1.0, 11/11/2016
 * @author ybecerra
 * 
 * @version 2, 08/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */

@ManagedBean
@ViewScoped
public class LusuarioAtrasosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String cNueves;

    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa a la aplicacion
     */
    private final String modulo;

    /**
     * Constante definida para almacenar la cadena "CODIGO", se
     * utiliza en los metodos
     * cargarListaUsoInicial,cargarListaUsoFinal,
     * cargarListaEstratoInicial,cargarListaEstratoFinal,
     * cargarListaBarrioInicial, cargarListaBarrioFinal,
     * seleccionarFilaUsoInicial,seleccionarFilaUsoFinal,
     * seleccionarFilaEstratoInicial,seleccionarFilaEstratoFinal,
     * seleccionarFilaBarrioInicial,seleccionarFilaBarrioFinal
     */
    private final String cod;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que valida el ordenamiento del informe a generar en el
     * formulario
     */
    private String ordenado;
    /**
     * Atributo que valida que informe se generara al darle clic en el
     * boton presentar o excel del formulario
     */
    private String formato;
    /**
     * Atributo que valida la condicion a enviar en el informe a
     * generar
     */
    private String condicion;
    /**
     * Atributo que almacena el valor true si la casilla de
     * verificacion Incluir supendidos y Retirados del formulario esta
     * seleccionada
     */
    private boolean supeReti;
    /**
     * Atributo que almacena el codigo seleccionado del combo Ciclo
     * Final del formulario
     */
    private String cicloFinal;
    /**
     * Atributo que almacena el codigo seleccionado del combo Uso
     * Inicial del formulario
     */
    private String usoInicial;
    /**
     * Atributo que almacena el codigo seleccionado del combo Estrato
     * Inicial del formulario
     */
    private String estratoInicial;
    /**
     * Atributo que almacena el codigo seleccionado del combo Uso
     * Final del formulario
     */
    private String usoFinal;
    /**
     * Atributo que almacena el codigo seleccionado del combo Estrato
     * Final del formulario
     */
    private String estratoFinal;
    /**
     * Atributo que almacena el codigo seleccionado del combo Chapetas
     * del formulario
     */
    private String chapetas;
    /**
     * Atributo que almacena el codigo seleccionado del combo Ciclo
     * Inicial del formulario
     */
    private String cicloInicial;
    /**
     * Atributo que almacena el codigo seleccionado del combo Barrio
     * Inicial del formulario
     */
    private String barrioInicial;
    /**
     * Atributo que almacena el codigo seleccionado del combo Barrio
     * Final del formulario
     */
    private String barrioFinal;
    /**
     * Atributo que almacena el valor digitado en el campo Periodo
     * Atraso Final del formulario
     */
    private String atrasoFinal;
    /**
     * Atributo que almacena el valor digitado en el campo Valores
     * superiores a del formulario
     */
    private double valoresSuperiores;
    /**
     * Atributo que almacena el valor digitado en el campo Periodo
     * Atraso Inicial del formulario
     */
    private String atrasoInicial;

    /**
     * Atributo que valida si llas etiquetas y combos de barrios se
     * hacen visibles o no
     */
    private boolean barrioVisible;

    /**
     * Atributo que valida si la casilla de verificacion Incluir
     * Suspendidos y Retirados del formulario se hace visible o no
     */
    private boolean estadoVisible = false;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el combo de Ciclo Inicial
     */
    private List<Registro> listacicloInicial;
    /**
     * Listado de registros para el combo de Ciclo Final
     */
    private List<Registro> listacicloFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Contiene la lista de registros del combo Uso Inicial
     */
    private RegistroDataModelImpl listausoInicial;
    /**
     * Contiene la lista de registros del combo Estrato Inicial
     */
    private RegistroDataModelImpl listaestratoInicial;
    /**
     * Contiene la lista de registros del combo Uso Inicial
     */
    private RegistroDataModelImpl listausoFinal;
    /**
     * Contiene la lista de registros del combo Estrato Final
     */
    private RegistroDataModelImpl listaestratoFinal;

    /**
     * Contiene la lista de registros del combo Barrio Inicial
     */
    private RegistroDataModelImpl listabarrioInicial;
    /**
     * Contiene la lista de registros del combo Barrio Final
     */
    private RegistroDataModelImpl listabarrioFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbPublicosCeroRemote;

    @EJB
    private EjbServiciosPublicosUnoRemote ejbServiciosPublicosUnoRemote;

    /**
     * Crea una nueva instancia de LusuarioatrasosAtraso
     */
    public LusuarioAtrasosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cod = GeneralParameterEnum.CODIGO.getName();
        cNueves = "99999";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LUSUARIO_ATRASOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListacicloInicial();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListausoInicial();

        cargarListabarrioInicial();

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
    public void abrirFormulario()
    {

        mensajesInicioModal();

        condicion = "1";
        ordenado = "1";
        formato = "1";
        chapetas = "3";
        barrioInicial = "0";
        barrioFinal = cNueves;
        usoInicial = "0";
        usoFinal = "99";
        estratoInicial = "0";
        estratoFinal = "99";
        atrasoInicial = "0";
        atrasoFinal = "99";
        valoresSuperiores = 0;

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listacicloInicial
     *
     */
    public void cargarListacicloInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listacicloInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LusuarioAtrasosControladorUrlEnum.URL9209
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
     * Carga la lista listacicloFinal
     *
     */
    public void cargarListacicloFinal()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(LusuarioAtrasosControladorEnum.PARAM0.getValue(),
                            cicloInicial);

            listacicloFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LusuarioAtrasosControladorUrlEnum.URL9799
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
     * Carga la lista listausoInicial
     *
     */
    public void cargarListausoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuarioAtrasosControladorUrlEnum.URL10399
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listausoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);

    }

    /**
     *
     * Carga la lista listaestratoInicial
     *
     */
    public void cargarListaestratoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuarioAtrasosControladorUrlEnum.URL11001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LusuarioAtrasosControladorEnum.PARAM1.getValue(), usoInicial);

        listaestratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    /**
     *
     * Carga la lista listausoFinal
     *
     */
    public void cargarListausoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuarioAtrasosControladorUrlEnum.URL11759
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LusuarioAtrasosControladorEnum.PARAM1.getValue(), usoInicial);

        listausoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    /**
     *
     * Carga la lista listaestratoFinal
     *
     */
    public void cargarListaestratoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuarioAtrasosControladorUrlEnum.URL12473
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LusuarioAtrasosControladorEnum.PARAM1.getValue(), usoInicial);
        param.put(LusuarioAtrasosControladorEnum.PARAM2.getValue(),
                        estratoInicial);

        listaestratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    /**
     *
     * Carga la lista listabarrioInicial
     *
     */
    public void cargarListabarrioInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuarioAtrasosControladorUrlEnum.URL13412
                                                        .getValue());
        listabarrioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, cod);

    }

    /**
     *
     * Carga la lista listabarrioFinal
     *
     */
    public void cargarListabarrioFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuarioAtrasosControladorUrlEnum.URL14021
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(LusuarioAtrasosControladorEnum.PARAM2.getValue(),
                        String.valueOf(barrioInicial));

        listabarrioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void mensajesInicioModal()
    {
        try
        {
            String param = ejbSysmanUtilRemote.consultarParametro(compania,
                            "MANEJA BARRIOS EN SUSCRIPTORES", modulo,
                            new Date(), true);
            if (param == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1802"));
                return;
            }
            else if ("SI".equals(param))
            {
                barrioVisible = true;

            }
            else
            {
                barrioVisible = false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void generarInforme(ReportesBean.FORMATOS formatos)
    {

        String reporteConsulta = "";
        String parReporte = "";
        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reporteConsulta = asignarVariablesReemplazo(reemplazar);

            reemplazar.put("cicloInicial", "'" + cicloInicial + "'");
            reemplazar.put("cicloFinal", "'" + cicloFinal + "'");
            reemplazar.put("usoInicial", "'" + usoInicial + "'");
            reemplazar.put("usoFinal", "'" + usoFinal + "'");
            reemplazar.put("estratoInicial", "'" + estratoInicial + "'");
            reemplazar.put("estratoFinal", "'" + estratoFinal + "'");
            reemplazar.put("condicionBarrio", condicionBarrio());
            reemplazar.put("condicionChapetas", "3".equals(chapetas) ? " "
                : " AND SP_USUARIO.CHAPETAS IN ('" + chapetas + "')");
            reemplazar.put("orderBy", orderBy());
            if (reporte() == null)
            {
                return;
            }
            parReporte = reporte();

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLOINICIAL", cicloInicial);
            parametros.put("PR_CICLOFINAL", cicloFinal);

            parametros.put("PR_PERIODO",
                            ejbPublicosCeroRemote.asignarNombrePeriodo(compania,
                                            Integer.parseInt(service
                                                            .buscarEnLista(cicloInicial,
                                                                            "NUMERO",
                                                                            "ANO",
                                                                            listacicloInicial)),
                                            service.buscarEnLista(cicloInicial,
                                                            "NUMERO", "PERIODO",
                                                            listacicloInicial),
                                            ""));

            parametros.put("PR_PERIODOSDEATRASO", atrasoInicial);
            parametros.put("PR_PERIODOFINAL", atrasoFinal);
            parametros.put("PR_CONDICION", "1".equals(condicion) ? "y" : "ó");
            parametros.put("PR_VALORESSUPERIORESA", valoresSuperiores);

            Reporteador.resuelveConsulta(reporteConsulta,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeError(idioma
                            .getString("MSM_INFORME_VAR_NO_EXISTE")
                            .replace("s$reporte$s", parReporte));
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    private String asignarVariablesReemplazo(
        HashMap<String, Object> reemplazar)
    {
        if ("1".equals(formato) || "3".equals(formato))
        {

            reemplazar.put("condicionIncluir",
                            supeReti ? ""
                                : "AND SP_USUARIO.ESTADO IN ('A')");
            reemplazar.put("condicion", condicion());
            return "1".equals(formato) ? "001245LAtrasoPagosYOP"
                : "001259LAtrasoPagosPorServicio";
        }
        else
        {
            reemplazar.put("valoresSuperiores", valoresSuperiores);
            reemplazar.put("atrasoInicial", atrasoInicial);
            reemplazar.put("atrasoFinal", atrasoFinal);
            return "001263ListadoResumenCartera";
        }
    }

    /**
     * Metodo que se llama en generarInforme
     *
     * @return condicion para un filtro en la consulta del reporte
     * 001245LAtrasoPagosYOP
     */
    public String condicion()
    {

        String rtaCondicion;
        if ("1".equals(condicion))
        {

            rtaCondicion = " AND (SP_USUARIO.PERIODOSATRASO BETWEEN "
                + atrasoInicial
                + " AND " + atrasoFinal + " \n" +
                "   AND SP_USUARIO.TOTFACTURAPERACTUAL >= " + valoresSuperiores
                + ")";
        }
        else
        {
            rtaCondicion = " AND (SP_USUARIO.PERIODOSATRASO BETWEEN "
                + atrasoInicial
                + " AND " + atrasoFinal + " \n" +
                "   OR SP_USUARIO.TOTFACTURAPERACTUAL >= " + valoresSuperiores
                + ")";
        }

        return rtaCondicion;
    }

    /**
     * Metodo que se llama en generarInforme
     *
     * @return condicion para un filtro en la consulta del reporte
     * 001245LAtrasoPagosYOP
     */
    public String condicionBarrio()
    {
        String rtaCondicionBarrio;
        String barrioIni = "";
        String barrioFin = "";
        if (barrioInicial.isEmpty() || barrioFinal.isEmpty())
        {
            barrioIni = "0";
            barrioFin = cNueves;
        }

        if ((!"0".equals(barrioIni) && !"0".equals(barrioInicial))
            && (!cNueves.equals(barrioFin) && !cNueves.equals(barrioFinal)))
        {
            rtaCondicionBarrio = " AND SP_USUARIO.BARRIO BETWEEN '"
                + barrioInicial + "' AND '"
                + barrioFinal + "'";
        }
        else
        {
            rtaCondicionBarrio = "";
        }

        return rtaCondicionBarrio;
    }

    /**
     * se llama en el metodo generarInforme
     *
     * @return el ordenamimiento para el reporte generado
     */
    public String orderBy()
    {
        String rtaOrderBy = "";
        switch (ordenado)
        {
        case "1":
            rtaOrderBy = " ORDER BY SP_USUARIO.CODIGORUTA";
            break;
        case "2":
            rtaOrderBy = " ORDER BY SP_USUARIO.PERIODOSATRASO,SP_USUARIO.CODIGORUTA";
            break;

        case "3":
            rtaOrderBy = rtaOrderTres();

            break;
        default:
            break;
        }

        return rtaOrderBy;

    }

    private String rtaOrderTres()
    {
        String rtaOrderBy;
        if (!"2".equals(formato))
        {
            rtaOrderBy = " ORDER BY SUM(SP_FACTURADO.VALOR_FACTURADO+SP_FACTURADO.DEUDA+SP_FACTURADO.VALORFINACT+SP_FACTURADO.VALORFINANT-SP_FACTURADO.VALORABONOACT-SP_FACTURADO.VALORABONOANT-SP_FACTURADO.CREDITOABONADO)  ASC,SP_USUARIO.CODIGORUTA ";
        }
        else
        {
            rtaOrderBy = " ORDER BY  CASE WHEN SP_USUARIO.BANCOPERPROCESO IS NOT NULL THEN 0 ELSE SP_USUARIO.TOTFACTURAPERACTUAL END + NVL(SALDOFINDEUDAYSALDOOTROS.SALDOFINDEUDA,0)+NVL(SALDOFINDEUDAYSALDOOTROS.SALDOFINPERIODO,0)+NVL(SALDOFINDEUDAYSALDOOTROS.SALDOFINOTROS,0) ASC ,SP_USUARIO.CODIGORUTA";
        }
        return rtaOrderBy;
    }

    /**
     * se llama en el metodo generarInforme
     *
     * @return el nombre del reporte para la opcion
     * "Reporte de Cartera" del formulario
     */
    public String reporteFormatoUno()
    {
        String reporteUno = "";
        String nit;
        try
        {
            nit = ejbServiciosPublicosUnoRemote.validarNit(compania,
                            SessionUtil.getCompaniaIngreso()
                                            .getNit());

            if ("844000755".equals(nit))
            {
                reporteUno = "001245LAtrasoPagosYOP";
            }
            else
            {
                reporteUno = "001254LAtrasoPagos";
            }

        }
        catch (SystemException ex)
        {

            logger.error(ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        return reporteUno;

    }

    /**
     * se llama en el metodo generarInforme
     *
     * @return el nombre del reporte para la opcion
     * "Reporte de cartera por Servicio" del formulario
     * @throws SysmanException
     */
    public String reporteFormatoDos()
                    throws SysmanException
    {

        String reporteDos = "";
        try
        {
            String parFinanciables = ejbSysmanUtilRemote.consultarParametro(
                            compania, "FORMATO REPORTE CARTERA Y FINANCIABLES",
                            modulo, new Date(), false);
            if (parFinanciables == null)
            {
                throw new SysmanException(idioma.getString("TB_TB1824"));
            }
            else
            {
                reporteDos = parFinanciables;
            }
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex);

        }

        return reporteDos;
    }

    /**
     * se llama en el metodo generarInforme
     *
     * @return el nombre del reporte para la opcion
     * " Reporte de cartera y financiables" del formulario
     * @throws SysmanException
     */
    public String reporteFormatoTres() throws SysmanException
    {
        String reporteTres = "";
        try
        {
            String parServicio = ejbSysmanUtilRemote.consultarParametro(
                            compania, "FORMATO REPORTE CARTERA POR SERVICIO",
                            modulo, new Date(), false);
            if (parServicio == null)
            {
                throw new SysmanException(idioma.getString("TB_TB1825"));
            }
            else
            {
                reporteTres = parServicio;
            }
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex);
        }

        return reporteTres;

    }

    /**
     * Metodo que se ejecuta en generarInforme, este devuelve el
     * nombre del reporte a generar
     *
     * @return el nombre del reporte a generar
     */
    public String reporte()
    {
        String report = "";
        if ("1".equals(formato))
        {
            report = reporteFormatoUno();
        }
        else if ("2".equals(formato))
        {
            try
            {
                report = reporteFormatoDos();
            }
            catch (SysmanException ex)
            {

                JsfUtil.agregarMensajeError(ex.getMessage());
                logger.error(ex);
                return null;

            }
        }
        else if ("3".equals(formato))
        {

            try
            {
                report = reporteFormatoTres();
            }
            catch (SysmanException ex)
            {
                JsfUtil.agregarMensajeError(ex.getMessage());
                logger.error(ex);
                return null;
            }

        }
        return report;

    }
    // <METODOS_CAMBIAR>

    public void cambiarcicloInicial()
    {
        cicloFinal = null;
        cargarListacicloFinal();
    }

    public void cambiarFormato()
    {

        if ("2".equals(formato))
        {
            supeReti = false;
            estadoVisible = true;
        }
        else
        {
            estadoVisible = false;
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listausoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilausoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        usoInicial = registroAux.getCampos().get(cod).toString();
        usoFinal = null;
        estratoInicial = null;
        estratoFinal = null;
        cargarListausoFinal();
        cargarListaestratoInicial();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaestratoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaestratoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        estratoInicial = registroAux.getCampos().get(cod).toString();
        estratoFinal = null;
        cargarListaestratoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listausoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilausoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        usoFinal = registroAux.getCampos().get(cod).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaestratoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaestratoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        estratoFinal = registroAux.getCampos().get(cod).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listabarrioInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilabarrioInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        barrioInicial = registroAux.getCampos().get(cod).toString();
        barrioFinal = null;
        cargarListabarrioFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listabarrioFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilabarrioFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        barrioFinal = registroAux.getCampos().get(cod).toString();
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ordenado
     *
     * @return ordenado
     */
    public String getOrdenado()
    {
        return ordenado;
    }

    /**
     * Asigna la variable ordenado
     *
     * @param ordenado
     * Variable a asignar en ordenado
     */
    public void setOrdenado(String ordenado)
    {
        this.ordenado = ordenado;
    }

    /**
     * Retorna la variable formato
     *
     * @return formato
     */
    public String getFormato()
    {
        return formato;
    }

    /**
     * Asigna la variable formato
     *
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato)
    {
        this.formato = formato;
    }

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

    /**
     * Retorna la variable supeReti
     *
     * @return supeReti
     */
    public boolean getSupeReti()
    {
        return supeReti;
    }

    /**
     * Asigna la variable supeReti
     *
     * @param supeReti
     * Variable a asignar en supeReti
     */
    public void setSupeReti(boolean supeReti)
    {
        this.supeReti = supeReti;
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
     * Retorna la variable chapetas
     *
     * @return chapetas
     */
    public String getChapetas()
    {
        return chapetas;
    }

    /**
     * Asigna la variable chapetas
     *
     * @param chapetas
     * Variable a asignar en chapetas
     */
    public void setChapetas(String chapetas)
    {
        this.chapetas = chapetas;
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
     * Retorna la variable barrioInicial
     *
     * @return barrioInicial
     */
    public String getBarrioInicial()
    {
        return barrioInicial;
    }

    /**
     * Asigna la variable barrioInicial
     *
     * @param barrioInicial
     * Variable a asignar en barrioInicial
     */
    public void setBarrioInicial(String barrioInicial)
    {
        this.barrioInicial = barrioInicial;
    }

    /**
     * Retorna la variable barrioFinal
     *
     * @return barrioFinal
     */
    public String getBarrioFinal()
    {
        return barrioFinal;
    }

    /**
     * Asigna la variable barrioFinal
     *
     * @param barrioFinal
     * Variable a asignar en barrioFinal
     */
    public void setBarrioFinal(String barrioFinal)
    {
        this.barrioFinal = barrioFinal;
    }

    /**
     * Retorna la variable atrasoFinal
     *
     * @return atrasoFinal
     */
    public String getAtrasoFinal()
    {
        return atrasoFinal;
    }

    /**
     * Asigna la variable atrasoFinal
     *
     * @param atrasoFinal
     * Variable a asignar en atrasoFinal
     */
    public void setAtrasoFinal(String atrasoFinal)
    {
        this.atrasoFinal = atrasoFinal;
    }

    /**
     * Retorna la variable valoresSuperiores
     *
     * @return valoresSuperiores
     */
    public double getValoresSuperiores()
    {
        return valoresSuperiores;
    }

    /**
     * Asigna la variable valoresSuperiores
     *
     * @param valoresSuperiores
     * Variable a asignar en valoresSuperiores
     */
    public void setValoresSuperiores(double valoresSuperiores)
    {
        this.valoresSuperiores = valoresSuperiores;
    }

    /**
     * Retorna la variable atrasoInicial
     *
     * @return atrasoInicial
     */
    public String getAtrasoInicial()
    {
        return atrasoInicial;
    }

    /**
     * Asigna la variable atrasoInicial
     *
     * @param atrasoInicial
     * Variable a asignar en atrasoInicial
     */
    public void setAtrasoInicial(String atrasoInicial)
    {
        this.atrasoInicial = atrasoInicial;
    }

    public boolean isBarrioVisible()
    {
        return barrioVisible;
    }

    public void setBarrioVisible(boolean barrioVisible)
    {
        this.barrioVisible = barrioVisible;
    }

    public boolean isEstadoVisible()
    {
        return estadoVisible;
    }

    public void setEstadoVisible(boolean estadoVisible)
    {
        this.estadoVisible = estadoVisible;
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
     * Retorna la lista listacicloInicial
     *
     * @return listacicloInicial
     */
    public List<Registro> getListacicloInicial()
    {
        return listacicloInicial;
    }

    /**
     * Asigna la lista listacicloInicial
     *
     * @param listacicloInicial
     * Variable a asignar en listacicloInicial
     */
    public void setListacicloInicial(List<Registro> listacicloInicial)
    {
        this.listacicloInicial = listacicloInicial;
    }

    /**
     * Retorna la lista listacicloFinal
     *
     * @return listacicloFinal
     */
    public List<Registro> getListacicloFinal()
    {
        return listacicloFinal;
    }

    /**
     * Asigna la lista listacicloFinal
     *
     * @param listacicloFinal
     * Variable a asignar en listacicloFinal
     */
    public void setListacicloFinal(List<Registro> listacicloFinal)
    {
        this.listacicloFinal = listacicloFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListausoInicial()
    {
        return listausoInicial;
    }

    public RegistroDataModelImpl getListaestratoInicial()
    {
        return listaestratoInicial;
    }

    public void setListaestratoInicial(
        RegistroDataModelImpl listaestratoInicial)
    {
        this.listaestratoInicial = listaestratoInicial;
    }

    public void setListausoInicial(RegistroDataModelImpl listausoInicial)
    {
        this.listausoInicial = listausoInicial;
    }

    public RegistroDataModelImpl getListausoFinal()
    {
        return listausoFinal;
    }

    public void setListausoFinal(RegistroDataModelImpl listausoFinal)
    {
        this.listausoFinal = listausoFinal;
    }

    public RegistroDataModelImpl getListaestratoFinal()
    {
        return listaestratoFinal;
    }

    public void setListaestratoFinal(RegistroDataModelImpl listaestratoFinal)
    {
        this.listaestratoFinal = listaestratoFinal;
    }

    public RegistroDataModelImpl getListabarrioInicial()
    {
        return listabarrioInicial;
    }

    public void setListabarrioInicial(RegistroDataModelImpl listabarrioInicial)
    {
        this.listabarrioInicial = listabarrioInicial;
    }

    public RegistroDataModelImpl getListabarrioFinal()
    {
        return listabarrioFinal;
    }

    public void setListabarrioFinal(RegistroDataModelImpl listabarrioFinal)
    {
        this.listabarrioFinal = listabarrioFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
