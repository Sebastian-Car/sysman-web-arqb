/*-
 * ConfigurarplanpptalsControlador.java
 *
 * 1.0
 * 
 * 07/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.cgr.ejb.impl.EjbCGRCero;
import com.sysman.cgr.enums.ConfigurarplanpptalsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma configurarplanpptal asociada al formulario
 * Configurar plan presupuestal. Que permite visualizar y actualizar
 * la configuracion de los rubros del plan presupuestal, teniendo en
 * cuenta el aï¿½o, la naturaleza de la cuenta, las regalias y el tipo
 * de entidad. Tambien permite copiar la configuracion del aï¿½o
 * anterior y verificar la configuracion realizada, generando un
 * archivo .txt con las inconsistencias encontradas.
 *
 * @version 1.0, 07/03/2017
 * @author jlramirez
 * @modified jguerrero
 * @version 2. 20/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class ConfigurarplanpptalsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el tipo de entidad que
     * el usuario configuro para la compania en la cual inicio sesion.
     */
    private final int entidad;
    /**
     * Constante que almacena la cadena de texto "CODIGO"
     */
    private final String txtCodigo;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    // <DECLARAR_ATRIBUTOS>
    /**
     * atributo que almacena el tipo de naturaleza, seleccionado en la
     * combo naturaleza cuentas. (Ingresos: C, Gastos: D)
     */
    private String naturaleza;
    /**
     * atributo que almacena el tipo de regalia seleccionado en la
     * combo codigos. (Regalias: R, Rubros vigencia actual: V)
     */
    private String regalias;
    /**
     * atributo que almacena el anio seleccionado en la combo aï¿½o
     */
    private String anio;
    /**
     * atributo que controla si la columna del codigo fuente es
     * visible o no.
     */
    private boolean codFuente;
    /**
     * atributo que controla si la columna del codigo detalle fuente
     * es visible o no.
     */
    private boolean codDetFuente;
    /**
     * atributo que controla si la columna del rubro equivalente es
     * visible o no.
     */
    private boolean rubroEq;
    /**
     * atributo que controla si la columna del codigo entidad
     * reciproca es visible o no.
     */
    private boolean codRecip;
    /**
     * atributo que controla si la columna del recurso es habilitada o
     * no.
     */
    private boolean recurso;
    /**
     * atributo que controla si la columna del origen especifico es
     * habilitada o no.
     */
    private boolean recaudo;
    /**
     * atributo que controla si la columna del recurso es habilitada o
     * no.
     */
    private boolean oei;
    /**
     * atributo que controla si la columna del destino recurso es
     * habilitada o no.
     */
    private boolean destino;
    /**
     * atributo que controla si la columna de vigencia gasto es
     * habilitada o no.
     */
    private boolean vigTeso;
    /**
     * atributo que controla si la columna de vigencia tesoreria es
     * habilitada o no.
     */
    private boolean vigGasto;
    /**
     * atributo que controla si la columna de la finalidad gasto es
     * habilitada o no.
     */
    private boolean finGasto;
    /**
     * atributo que controla si la columna del check de situacion de
     * fondos es habilitada o no.
     */
    private boolean fondos;
    /**
     * atributo que controla si la columna de la dependencia es
     * habilitada o no.
     */
    private boolean depenAsociada;
    /**
     * atributo que controla si la el campo de mensaje es visible o
     * no.
     */
    private boolean mensaje;
    /**
     * atributo que almacena el texto que sera mostrado en el campo de
     * mensaje.
     */
    private String mensajeTxt;
    /**
     * atributo que controla si la el dialogo de actualizacion es
     * visible o no.
     */
    private boolean mostrarDgActualizar;
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
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del aï¿½o.
     */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del recurso.
     */
    private RegistroDataModelImpl listaRecursoSchipE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * de la dependencia.
     */
    private RegistroDataModelImpl listaDependenciaAsociadaE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del origen especifico.
     */
    private RegistroDataModelImpl listaoeiE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del destino.
     */
    private RegistroDataModelImpl listadestinoE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * de la finalidad de gasto.
     */
    private RegistroDataModelImpl listafinGastoE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * de la vigencia de gasto.
     */
    private RegistroDataModelImpl listaVigGastoE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * de la vigencia de tesoreria.
     */
    private RegistroDataModelImpl listavigTesoreriaE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del codigo schip.
     */
    private RegistroDataModelImpl listaCodigoEquivE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del Codigo CR1 Resguardo.
     */
    private RegistroDataModelImpl listaresguardoSchipE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del Codigo sireci.
     */
    private RegistroDataModelImpl listaCodigoSireciE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del Codigo entidad reciproca.
     */
    private RegistroDataModelImpl listaCodReciprocaE;
    /**
     * Lista que contiene la informaciï¿½n de los detalles del combo
     * del Codigo fuente.
     */

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbCGRCero ejbCgrCero;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfigurarplanpptalsControlador
     */
    public ConfigurarplanpptalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        entidad = SessionUtil.getCompaniaIngreso().getTipoEntidad();
        txtCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONFIGURARPLANPPTALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            ConfigurarplanpptalsControlador.class.getName())
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
    public void inicializar() {

        enumBase = GenericUrlEnum.PLAN_PPTAL_CONFIG;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        naturaleza = "C";
        regalias = "R";
        mensaje = false;
        mostrarDgActualizar = false;
        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        reasignarOrigen();
        habilitarCampos();
        visualizarCampos();
        abrirFormulario();
        cargarListas();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put("COMPANIA", compania);
        parametrosListado.put("ANO", anio);
        parametrosListado.put("NATURALEZA", naturaleza);
        parametrosListado.put("REGALIAS", regalias);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnio
     */
    public void cargarListaAnio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarplanpptalsControladorUrlEnum.URL14108
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaRecursoSchip
     */
    public void cargarListaRecursoSchipE() {

        if ("R".equals(regalias)) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplanpptalsControladorUrlEnum.URL18484
                                                            .getValue());
            listaRecursoSchipE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), null,
                            true, txtCodigo);

        }
        else {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.ENTIDAD.getName(), entidad);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplanpptalsControladorUrlEnum.URL18485
                                                            .getValue());
            listaRecursoSchipE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), parametros,
                            true, txtCodigo);

        }
    }

    /**
     * Carga la lista listaDependenciaAsociada
     */
    public void cargarListaDependenciaAsociadaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplanpptalsControladorUrlEnum.URL15313
                                                        .getValue());
        listaDependenciaAsociadaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, txtCodigo);

    }

    /**
     * Carga la lista listaoei
     */
    public void cargarListaoeiE() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.ENTIDAD.getName(), entidad);

        if ("R".equals(regalias)) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplanpptalsControladorUrlEnum.URL18486
                                                            .getValue());
            listaoeiE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), null,
                            true, txtCodigo);

            // 579001
        }
        else {
            if ("C".equals(naturaleza)) {

                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ConfigurarplanpptalsControladorUrlEnum.URL18487
                                                                .getValue());
                listaoeiE = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param,
                                true, txtCodigo);

                // 573001
            }
            else {

                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ConfigurarplanpptalsControladorUrlEnum.URL18488
                                                                .getValue());
                listaoeiE = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param,
                                true, txtCodigo);

                // 575001
            }
        }

    }

    /**
     * Carga la lista listadestino
     */
    public void cargarListadestinoE() {

        if ("R".equals(regalias)) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplanpptalsControladorUrlEnum.URL18489
                                                            .getValue());
            listadestinoE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), null,
                            true, txtCodigo);

            // 580001
        }
        else {

            Map<String, Object> param = new TreeMap<>();

            param.put("ENTIDAD", entidad);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplanpptalsControladorUrlEnum.URL18490
                                                            .getValue());
            listadestinoE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, txtCodigo);

            // 569001
        }

    }

    /**
     * Carga la lista listafinGasto
     */
    public void cargarListafinGastoE() {
        if ("D".equals(naturaleza)) {

            Map<String, Object> param = new TreeMap<>();

            param.put("COMPANIA", compania);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplanpptalsControladorUrlEnum.URL0001
                                                            .getValue());
            listafinGastoE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, txtCodigo);

        }
    }

    /**
     * Carga la lista listaVigGasto
     */
    public void cargarListaVigGastoE() {
        if ("D".equals(naturaleza)) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplanpptalsControladorUrlEnum.URL18483
                                                            .getValue());
            listaVigGastoE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), null,
                            true, txtCodigo);

        }
        // 582001
    }

    /**
     * Carga la lista listavigTesoreria
     */
    public void cargarListavigTesoreriaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplanpptalsControladorUrlEnum.URL19069
                                                        .getValue());
        listavigTesoreriaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, txtCodigo);

    }

    /**
     * Carga la lista listaresguardoSchip
     */
    public void cargarListaresguardoSchipE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplanpptalsControladorUrlEnum.URL19635
                                                        .getValue());
        listaresguardoSchipE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "ID");

        // 584001
    }

    /**
     * Carga la lista listaCodigoSireci
     */
    public void cargarListaCodigoSireciE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplanpptalsControladorUrlEnum.URL21184
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);

        listaCodigoSireciE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, txtCodigo);

        // 585001
    }

    /**
     * Carga la lista listaCodReciproca
     */
    public void cargarListaCodReciprocaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplanpptalsControladorUrlEnum.URL22207
                                                        .getValue());
        listaCodReciprocaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, txtCodigo);

        // 586001
    }

    /**
     * Carga la lista listaCodigoEquiv
     */
    public void cargarListaCodigoEquivE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplanpptalsControladorUrlEnum.URL24604
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ENTIDAD.getName(), entidad);

        listaCodigoEquivE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, txtCodigo);

        // 514001
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton ConfigAnioAnt en la vista
     */
    public void oprimirConfigAnioAnt() {
        // <CODIGO_DESARROLLADO>
        mostrarDgActualizar = true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Verificar en la vista
     */
    public void oprimirVerificar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String reporte;
        String sql;
        String nombreReporte;
        Map<String, Object> reemplazos = new TreeMap<>();

        reemplazos.put("compania", compania);
        reemplazos.put("ano", anio);

        try {

            if ("C".equals(naturaleza)) {

                reporte = "800348REVISION_CONFIGURACION_CGR_INGRESOS";
                nombreReporte = "RevisionConfiguracionCgrIngresos";
            }
            else {
                reporte = "800349REVISION_CONFIGURACION_CGR_GASTOS";
                nombreReporte = "RevisionConfiguracionCgrGastos";
            }

            sql = Reporteador.resuelveConsulta(
                            reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                            nombreReporte);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera un archivo txt de las inconsistencias
     * presentadas al verificar la configuracion de las cuentas
     * presupuestales.
     */
    public void generarReporte() {
        try {

            String cadena = ejbCgrCero.generarConfiguracion(compania,
                            Integer.parseInt(anio));
            ByteArrayInputStream incon = JsfUtil
                            .serializarPlano(cadena);
            archivoDescarga = JsfUtil.getArchivoDescarga(incon,
                            "InconsistenciasConfiguracion.txt");
        }
        catch (JRException | IOException | NumberFormatException
                        | SystemException e) {
            Logger.getLogger(ConfigurarplanpptalsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control NaturalezaSel
     */
    public void cambiarNaturalezaSel() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        habilitarCampos();
        cargarListas();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Regalias
     */
    public void cambiarRegalias() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        habilitarCampos();
        cargarListas();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Anio
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        habilitarCampos();
        cargarListas();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite definir que columnas se habilitan para
     * actualizar,segun lo seleccionado en la combo de naturaleza.
     */
    public void habilitarCampos() {
        if ("C".equals(naturaleza)) {
            recurso = false;
            oei = false;
            destino = false;
            depenAsociada = true;
            finGasto = true;
            vigGasto = true;
            vigTeso = true;
            mensaje = false;
            fondos = false;
            recaudo = false;

        }
        else {
            recurso = false;
            oei = false;
            destino = false;
            finGasto = false;
            fondos = false;
            vigGasto = false;
            vigTeso = false;
            mensaje = true;
            recaudo = true;
            switch (entidad) {
            case 1:
            case 2:
            case 4:
            case 5:
                depenAsociada = true;
                mensajeTxt = idioma.getString("TB_TB2909");
                break;
            case 3:
                mensajeTxt = idioma.getString("TB_TB2910");
                break;
            case 6:
                depenAsociada = false;
                mensajeTxt = idioma.getString("TB_TB2911");
                break;
            case 7:
                depenAsociada = false;
                mensajeTxt = idioma.getString("TB_TB2909");
                break;
            default:
                break;
            }
        }
    }

    /**
     * Metodo que permite cargar todas las listas de los combos de las
     * columnas
     */
    public void cargarListas() {

        cargarListaCodigoEquivE();
        cargarListaCodigoSireciE();
        cargarListaCodReciprocaE();
        cargarListaDependenciaAsociadaE();
        cargarListadestinoE();
        cargarListafinGastoE();
        cargarListaoeiE();
        cargarListaRecursoSchipE();
        cargarListaresguardoSchipE();
        cargarListaVigGastoE();
        cargarListavigTesoreriaE();
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * actualizar en la vista
     */
    public void aceptaractualizar() {
        // <CODIGO_DESARROLLADO>
        try {

            BigDecimal actu = ejbCgrCero.actualizarConfiguracionPptal(compania,
                            Integer.parseInt(anio),
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2912").replace("#$actu$#",
                                            String.valueOf(actu)));
            return;
        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(ConfigurarplanpptalsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRecursoSchip
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRecursoSchipE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaAsociada
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaAsociadaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaoei
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaoeiE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadestino
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladestinoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafinGasto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafinGastoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaVigGasto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaVigGastoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listavigTesoreria
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilavigTesoreriaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoEquiv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoEquivE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
        if ((!"-1".equals(SysmanFunciones.nvl(
                        registroAux.getCampos().get("MOVIMIENTO"), 0)))
            && ("".equals(SysmanFunciones
                            .nvl(auxiliar, "")))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2913"));
            return;
        }
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaresguardoSchip
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaresguardoSchipE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, "ID");
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoSireci
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoSireciE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodReciproca
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodReciprocaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodFuente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodFuenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodDetFuentePredis
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodDetFuentePredisE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbRubroEq
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbRubroEqE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, txtCodigo);
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            Registro aux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarplanpptalsControladorUrlEnum.URL17863
                                                                            .getValue())
                                            .getUrl(), param));

            if (SysmanFunciones.validarCampoVacio(aux.getCampos(),
                            "TIPOENTIDAD")) {
                SessionUtil.agregarMensajeErrorMenu(
                                idioma.getString("TB_TB2914"));
                SessionUtil.redireccionarMenu();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void visualizarCampos() {
        try {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE CONFIGURAR ENTIDAD RECIPROCA",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO"))) {
                codRecip = true;
            }
            else {
                codRecip = false;
            }

        }
        catch (SystemException e) {
            Logger.getLogger(ConfigurarplanpptalsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return
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
     * @return
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return
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
     * @return
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
     * @return
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove("MOVIMIENTO");
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove("CENTRO_COSTO");
        registro.getCampos().remove("TERCERO");
        registro.getCampos().remove("SUCURSAL");
        registro.getCampos().remove("FUENTE_RECURSO");
        registro.getCampos().remove("REFERENCIA");
        registro.getCampos().remove("AUXILIAR");
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable naturaleza
     * 
     * @return naturaleza
     */
    public String getNaturaleza() {
        return naturaleza;
    }

    /**
     * Asigna la variable naturaleza
     * 
     * @param naturaleza
     * Variable a asignar en naturaleza
     */
    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    /**
     * Retorna la variable regalias
     * 
     * @return regalias
     */
    public String getRegalias() {
        return regalias;
    }

    /**
     * Asigna la variable regalias
     * 
     * @param regalias
     * Variable a asignar en regalias
     */
    public void setRegalias(String regalias) {
        this.regalias = regalias;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable codFuente
     * 
     * @return codFuente
     */
    public boolean getCodFuente() {
        return codFuente;
    }

    /**
     * Asigna la variable codFuente
     * 
     * @param codFuente
     * Variable a asignar en codFuente
     */
    public void setCodFuente(boolean codFuente) {
        this.codFuente = codFuente;
    }

    /**
     * Retorna la variable codDetFuente
     * 
     * @return codDetFuente
     */
    public boolean getCodDetFuente() {
        return codDetFuente;
    }

    /**
     * Asigna la variable codFcodDetFuenteuente
     * 
     * @param codDetFuente
     * Variable a asignar en codDetFuente
     */
    public void setCodDetFuente(boolean codDetFuente) {
        this.codDetFuente = codDetFuente;
    }

    /**
     * Retorna la variable rubroEq
     * 
     * @return rubroEq
     */
    public boolean getRubroEq() {
        return rubroEq;
    }

    /**
     * Asigna la variable rubroEq
     * 
     * @param rubroEq
     * Variable a asignar en rubroEq
     */
    public void setRubroEq(boolean rubroEq) {
        this.rubroEq = rubroEq;
    }

    /**
     * Retorna la variable codRecip
     * 
     * @return codRecip
     */
    public boolean getCodRecip() {
        return codRecip;
    }

    /**
     * Asigna la variable codRecip
     * 
     * @param codRecip
     * Variable a asignar en codRecip
     */
    public void setCodRecip(boolean codRecip) {
        this.codRecip = codRecip;
    }

    /**
     * Retorna la variable recurso
     * 
     * @return recurso
     */
    public boolean getRecurso() {
        return recurso;
    }

    /**
     * Asigna la variable recurso
     * 
     * @param recurso
     * Variable a asignar en recurso
     */
    public void setRecurso(boolean recurso) {
        this.recurso = recurso;
    }

    /**
     * Retorna la variable recaudo
     * 
     * @return recaudo
     */
    public boolean getRecaudo() {
        return recaudo;
    }

    /**
     * Asigna la variable recaudo
     * 
     * @param recaudo
     * Variable a asignar en recaudo
     */
    public void setRecaudo(boolean recaudo) {
        this.recaudo = recaudo;
    }

    /**
     * Retorna la variable oei
     * 
     * @return oei
     */
    public boolean getOei() {
        return oei;
    }

    /**
     * Asigna la variable oei
     * 
     * @param oei
     * Variable a asignar en oei
     */
    public void setOei(boolean oei) {
        this.oei = oei;
    }

    /**
     * Retorna la variable destino
     * 
     * @return destino
     */
    public boolean getDestino() {
        return destino;
    }

    /**
     * Asigna la variable destino
     * 
     * @param destino
     * Variable a asignar en destino
     */
    public void setDestino(boolean destino) {
        this.destino = destino;
    }

    /**
     * Retorna la variable vigTeso
     * 
     * @return vigTeso
     */
    public boolean getVigTeso() {
        return vigTeso;
    }

    /**
     * Asigna la variable vigTeso
     * 
     * @param vigTeso
     * Variable a asignar en vigTeso
     */
    public void setVigTeso(boolean vigTeso) {
        this.vigTeso = vigTeso;
    }

    /**
     * Retorna la variable vigGasto
     * 
     * @return vigGasto
     */
    public boolean getVigGasto() {
        return vigGasto;
    }

    /**
     * Asigna la variable vigGasto
     * 
     * @param vigGasto
     * Variable a asignar en vigGasto
     */
    public void setVigGasto(boolean vigGasto) {
        this.vigGasto = vigGasto;
    }

    /**
     * Retorna la variable finGasto
     * 
     * @return finGasto
     */
    public boolean getFinGasto() {
        return finGasto;
    }

    /**
     * Asigna la variable finGasto
     * 
     * @param finGasto
     * Variable a asignar en finGasto
     */
    public void setFinGasto(boolean finGasto) {
        this.finGasto = finGasto;
    }

    /**
     * Retorna la variable fondos
     * 
     * @return fondos
     */
    public boolean getFondos() {
        return fondos;
    }

    /**
     * Asigna la variable fondos
     * 
     * @param fondos
     * Variable a asignar en fondos
     */
    public void setFondos(boolean fondos) {
        this.fondos = fondos;
    }

    /**
     * Retorna la variable depenAsociada
     * 
     * @return depenAsociada
     */
    public boolean getDepenAsociada() {
        return depenAsociada;
    }

    /**
     * Asigna la variable depenAsociada
     * 
     * @param depenAsociada
     * Variable a asignar en depenAsociada
     */
    public void setDepenAsociada(boolean depenAsociada) {
        this.depenAsociada = depenAsociada;
    }

    /**
     * Retorna la variable mensaje
     * 
     * @return mensaje
     */
    public boolean getMensaje() {
        return mensaje;
    }

    /**
     * Asigna la variable mensaje
     * 
     * @param mensaje
     * Variable a asignar en mensaje
     */
    public void setMensaje(boolean mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Retorna la variable mensajeTxt
     * 
     * @return mensajeTxt
     */
    public String getMensajeTxt() {
        return mensajeTxt;
    }

    /**
     * Asigna la variable mensajeTxt
     * 
     * @param mensajeTxt
     * Variable a asignar en mensajeTxt
     */
    public void setMensajeTxt(String mensajeTxt) {
        this.mensajeTxt = mensajeTxt;
    }

    /**
     * Retorna la variable mostrarDgActualizar
     * 
     * @return mostrarDgActualizar
     */
    public boolean getMostrarDgActualizar() {
        return mostrarDgActualizar;
    }

    /**
     * Asigna la variable mostrarDgActualizar
     * 
     * @param mostrarDgActualizar
     * Variable a asignar en mostrarDgActualizar
     */
    public void setMostrarDgActualizar(boolean mostrarDgActualizar) {
        this.mostrarDgActualizar = mostrarDgActualizar;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * Asigna la variable indice
     * 
     * @param indice
     * Variable a asignar en indice
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaRecursoSchip
     * 
     * @return listaRecursoSchip
     */
    public RegistroDataModelImpl getListaRecursoSchipE() {
        return listaRecursoSchipE;
    }

    /**
     * Asigna la lista listaRecursoSchip
     * 
     * @param listaRecursoSchip
     * Variable a asignar en listaRecursoSchip
     */
    public void setListaRecursoSchipE(
        RegistroDataModelImpl listaRecursoSchipE) {
        this.listaRecursoSchipE = listaRecursoSchipE;
    }

    /**
     * Retorna la lista listaDependenciaAsociada
     * 
     * @return listaDependenciaAsociada
     */
    public RegistroDataModelImpl getListaDependenciaAsociadaE() {
        return listaDependenciaAsociadaE;
    }

    /**
     * Asigna la lista listaDependenciaAsociada
     * 
     * @param listaDependenciaAsociada
     * Variable a asignar en listaDependenciaAsociada
     */
    public void setListaDependenciaAsociadaE(
        RegistroDataModelImpl listaDependenciaAsociadaE) {
        this.listaDependenciaAsociadaE = listaDependenciaAsociadaE;
    }

    /**
     * Retorna la lista listaoei
     * 
     * @return listaoei
     */
    public RegistroDataModelImpl getListaoeiE() {
        return listaoeiE;
    }

    /**
     * Asigna la lista listaoei
     * 
     * @param listaoei
     * Variable a asignar en listaoei
     */
    public void setListaoeiE(RegistroDataModelImpl listaoeiE) {
        this.listaoeiE = listaoeiE;
    }

    /**
     * Retorna la lista listadestino
     * 
     * @return listadestino
     */
    public RegistroDataModelImpl getListadestinoE() {
        return listadestinoE;
    }

    /**
     * Asigna la lista listadestino
     * 
     * @param listadestino
     * Variable a asignar en listadestino
     */
    public void setListadestinoE(RegistroDataModelImpl listadestinoE) {
        this.listadestinoE = listadestinoE;
    }

    /**
     * Retorna la lista listafinGasto
     * 
     * @return listafinGasto
     */
    public RegistroDataModelImpl getListafinGastoE() {
        return listafinGastoE;
    }

    /**
     * Asigna la lista listafinGasto
     * 
     * @param listafinGasto
     * Variable a asignar en listafinGasto
     */
    public void setListafinGastoE(RegistroDataModelImpl listafinGastoE) {
        this.listafinGastoE = listafinGastoE;
    }

    /**
     * Retorna la lista listaVigGasto
     * 
     * @return listaVigGasto
     */
    public RegistroDataModelImpl getListaVigGastoE() {
        return listaVigGastoE;
    }

    /**
     * Asigna la lista listaVigGasto
     * 
     * @param listaVigGasto
     * Variable a asignar en listaVigGasto
     */
    public void setListaVigGastoE(RegistroDataModelImpl listaVigGastoE) {
        this.listaVigGastoE = listaVigGastoE;
    }

    /**
     * Retorna la lista listavigTesoreria
     * 
     * @return listavigTesoreria
     */
    public RegistroDataModelImpl getListavigTesoreriaE() {
        return listavigTesoreriaE;
    }

    /**
     * Asigna la lista listavigTesoreria
     * 
     * @param listavigTesoreria
     * Variable a asignar en listavigTesoreria
     */
    public void setListavigTesoreriaE(
        RegistroDataModelImpl listavigTesoreriaE) {
        this.listavigTesoreriaE = listavigTesoreriaE;
    }

    /**
     * Retorna la lista listaCodigoEquiv
     * 
     * @return listaCodigoEquiv
     */
    public RegistroDataModelImpl getListaCodigoEquivE() {
        return listaCodigoEquivE;
    }

    /**
     * Asigna la lista listaCodigoEquiv
     * 
     * @param listaCodigoEquiv
     * Variable a asignar en listaCodigoEquiv
     */
    public void setListaCodigoEquivE(RegistroDataModelImpl listaCodigoEquivE) {
        this.listaCodigoEquivE = listaCodigoEquivE;
    }

    /**
     * Retorna la lista listaresguardoSchip
     * 
     * @return listaresguardoSchip
     */
    public RegistroDataModelImpl getListaresguardoSchipE() {
        return listaresguardoSchipE;
    }

    /**
     * Asigna la lista listaresguardoSchip
     * 
     * @param listaresguardoSchip
     * Variable a asignar en listaresguardoSchip
     */
    public void setListaresguardoSchipE(
        RegistroDataModelImpl listaresguardoSchipE) {
        this.listaresguardoSchipE = listaresguardoSchipE;
    }

    /**
     * Retorna la lista listaCodigoSireci
     * 
     * @return listaCodigoSireci
     */
    public RegistroDataModelImpl getListaCodigoSireciE() {
        return listaCodigoSireciE;
    }

    /**
     * Asigna la lista listaCodigoSireci
     * 
     * @param listaCodigoSireci
     * Variable a asignar en listaCodigoSireci
     */
    public void setListaCodigoSireciE(
        RegistroDataModelImpl listaCodigoSireciE) {
        this.listaCodigoSireciE = listaCodigoSireciE;
    }

    /**
     * Retorna la lista listaCodReciproca
     * 
     * @return listaCodReciproca
     */
    public RegistroDataModelImpl getListaCodReciprocaE() {
        return listaCodReciprocaE;
    }

    /**
     * Asigna la lista listaCodReciproca
     * 
     * @param listaCodReciproca
     * Variable a asignar en listaCodReciproca
     */
    public void setListaCodReciprocaE(
        RegistroDataModelImpl listaCodReciprocaE) {
        this.listaCodReciprocaE = listaCodReciprocaE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

}
