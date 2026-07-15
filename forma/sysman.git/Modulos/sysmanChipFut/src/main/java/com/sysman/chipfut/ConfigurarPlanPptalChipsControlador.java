/*-
 * ConfigurarPlanPptalChipsControlador.java
 *
 * 1.0
 * 
 * 23/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.ejb.EjbChipFutCeroRemote;
import com.sysman.chipfut.enums.ConfigurarPlanPptalChipsControladorEnum;
import com.sysman.chipfut.enums.ConfigurarPlanPptalChipsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Clase encargada de la configuracion del plan presupuestal con
 * codigosfut.
 *
 * @version 1.0, 23/03/2017
 * @author jguerrero
 * 
 * @version 2.0,13/07/2017, Proceso de Refactoring DSS,cambio de
 * numero de formulario por enum y cambio de tabla de origen por
 * PLAN_PPTAL_CONFIG,cambio de llamado de funciones por EJB
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class ConfigurarPlanPptalChipsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo destino de la interfaz grafica del la aplicacion
     */
    private String destinoSel;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo naturaleza de la interfaz grafica del la aplicacion
     */
    private String naturaleza;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo ano de la interfaz grafica del la aplicacion
     */
    private int ano;

    /**
     * Atributo encargado de de almacenar temporalmente un booleano
     * que permite mostrar o no los componentes de la grilla segun la
     * logica del negocio
     */

    private boolean visibleInversion;
    /**
     * Atributo encargado de de almacenar temporalmente un booleano
     * que permite mostrar o no los componentes de la grilla segun la
     * logica del negocio
     */
    private boolean visibleRegalias;
    /**
     * Atributo encargado de de almacenar temporalmente un booleano
     * que permite mostrar o no los componentes de la grilla segun la
     * logica del negocio
     */
    private boolean visibleRegaliasCredito;

    /**
     * Atributo usado para mostrar el dialogo cuando no hay codigos
     * fut disponibles para el a�o en que se esta trabajando
     */
    private boolean dialogoVisible;
    /**
     * Variable que almacena temporalmente el movimiento de la cuenta
     * que es seleccionado en el metodo seleccionarfialConseFut
     */
    private boolean movimientoCodigoFut;
    /**
     * Variable que almacena temporalmente el codigoFut seleccioando
     * cuando se edita el registro
     */
    private String codigoFutAux;
    /**
     * Variable que almacena temporalmente el sector regalias
     * seleccioando cuando se edita el registro
     */
    private String codigoSectorRegalias;
    /**
     * Atributo usado para mostrar el dialogo cuando no hay codigos
     * fut disponibles para el a�o en que se esta trabajando
     */
    private boolean dialogoVisibleTrasladar;
    /**
     * Atributo usado para cargar el texto del dialogo trasladar
     * configuracion del siguiente ano
     */
    private String textoDialogTrasladar;
    /**
     * Atributo usado para bloquear el campo fuente dependiendo de los
     * parametros y de la logica de negociol.
     */
    private boolean bloquearFuenteFut;

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
     * Lista encargada de almacenar tempporalmente los registros de
     * los fuentes fut como resultado a la peticion a la base de datos
     */
    private RegistroDataModelImpl listaFuenteFut;
    /**
     * Lista encargada de almacenar tempporalmente los registros de
     * los fuentes como resultado a la peticion a la base de datos
     */
    private RegistroDataModelImpl listavFuente;
    /**
     * Lista encargada de almacenar tempporalmente los registros de
     * los fuentes como resultado a la peticion a la base de datos
     */

    private RegistroDataModelImpl listavFuenteE;

    /**
     * Lista encargada de almacenar tempporalmente los registros de
     * los fuenteFutReserva como resultado a la peticion a la base de
     * datos
     */
    private List<Registro> listafuenteFutReserva;
    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista fuenteFutR como resultado a la peticion a la base de
     * datos
     */
    private List<Registro> listafuenteFutR;

    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista fuenteFutRE como resultado a la peticion a la base de
     * datos
     */
    private RegistroDataModelImpl listafuenteFutRE;

    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista sectorR como resultado a la peticion a la base de datos
     */
    private RegistroDataModelImpl listasectorR;
    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista sectorRE como resultado a la peticion a la base de datos
     */
    private RegistroDataModelImpl listasectorRE;
    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista ano como resultado a la peticion a la base de datos
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista conseFut como resultado a la peticion a la base de datos
     */
    private RegistroDataModelImpl listaConseFut;
    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista conseFut como resultado a la peticion a la base de datos
     */
    private RegistroDataModelImpl listaConseFutE;
    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista conseFutR como resultado a la peticion a la base de datos
     */
    private RegistroDataModelImpl listacodigoFutR;
    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista listacodigoFutRE como resultado a la peticion a la base
     * de datos
     */
    private RegistroDataModelImpl listacodigoFutRE;

    /**
     * Lista encargada de almacenar tempporalmente los registros de la
     * lista listaFuenteFutE como resultado a la peticion a la base de
     * datos
     */
    private RegistroDataModelImpl listaFuenteFutE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Variable encargada de almacenar temporalmente un booleano que
     * muestra o no la fuente Registro dependiendo de un parametro.
     */
    private boolean visibleFuenteRegistrosP;
    /**
     * Variable encargada de almacenar temporalmente un booleano que
     * muestra o no la fuente dependiendo de un parametro.
     */
    private boolean visibleFuente;
    /**
     * Constante encargada de almacenar el String CODIGOFU_FUT
     */
    private final String codigoFuFutCons;
    /**
     * Constante encargada de almacenar el String CODIGOFUT_H
     */

    private final String codigoFuFutHCons;
    /**
     * Constante encargada de almacenar el String CODIGO
     */
    private final String codigoCons;
    /**
     * Constante encargada de almacenar el String CODIGOFUT
     */
    private final String codigoFutCons;

    /**
     * Constante encargada de almacenar el String TB_TB3039
     */

    private final String etiquetaNoExisteCodigoF;
    /**
     * Constante encargada de almacenar el String TB_TB3040
     */

    private final String etiquetaNoExisteFuenteF;

    /**
     * Crea una nueva instancia de ConfigurarPlanPptalChipsControlador
     */

    private final String anoSigCons;

    @EJB
    private EjbChipFutCeroRemote ejbChipFutCero;

    public ConfigurarPlanPptalChipsControlador() {
        super();
        etiquetaNoExisteCodigoF = "TB_TB3039";
        etiquetaNoExisteFuenteF = "TB_TB3040";
        anoSigCons = "s$anoSig$s";
        codigoFuFutCons = "CODIGOFU_FUT";
        codigoFuFutHCons = "CODIGOFUT_H";
        codigoCons = "CODIGO";
        codigoFutCons = "CODIGOFUT";

        compania = SessionUtil.getCompania();

        try {

            // 1368
            numFormulario = GeneralCodigoFormaEnum.CONFIGURAR_PLAN_PPTAL_CHIPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            destinoSel = "I";
            naturaleza = "D";
            visibleInversion = true;
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
        tabla = "PLAN_PPTAL_CONFIG";
        ano = SysmanFunciones.ano(new Date());
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaFuenteFut();
        cargarListavFuente();
        cargarListavFuenteE();
        cargarListafuenteFutReserva();
        cargarListafuenteFutR();
        cargarListafuenteFutRE();
        cargarListaFuenteFutE();
        cargarListasectorR();
        cargarListasectorRE();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConseFut();
        cargarListaConseFutE();
        cargarListacodigoFutR();
        cargarListacodigoFutRE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        crearfuentesFut();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);

        parametrosListado.put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);

        parametrosListado.put(GeneralParameterEnum.DESTINO.getName(),
                        destinoSel);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL20888
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL18489
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaFuenteFut
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listaFuenteFut
     */
    public void cargarListaFuenteFut() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(ConfigurarPlanPptalChipsControladorEnum.DESTINO.getValue(),
                        destinoSel);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL12901
                                                        .getValue());
        listaFuenteFut = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoFuFutCons);
    }

    /**
     * 
     * Carga la lista listaFuenteFut
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listaFuenteFutE
     */
    public void cargarListaFuenteFutE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL12901
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(ConfigurarPlanPptalChipsControladorEnum.DESTINO.getValue(),
                        destinoSel);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);

        listaFuenteFutE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoFuFutCons);
    }

    /**
     * 
     * Carga la lista listavFuente
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listavFuente
     */
    public void cargarListavFuente() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL14513
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listavFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * 
     * Carga la lista listavFuente
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listavFuenteE
     */
    public void cargarListavFuenteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL15296
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listavFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * 
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listafuenteFutReserva
     *
     */
    public void cargarListafuenteFutReserva() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listafuenteFutReserva = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarPlanPptalChipsControladorUrlEnum.URL9938
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listafuenteFutR
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listafuenteFutRE
     */
    public void cargarListafuenteFutRE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL16738
                                                        .getValue());
        listafuenteFutRE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, codigoCons);
    }

    /**
     * 
     * Carga la lista listafuenteFutR
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listafuenteFutR
     */
    public void cargarListafuenteFutR() {
        try {
            listafuenteFutR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarPlanPptalChipsControladorUrlEnum.URL9940
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listasectorR
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listasectorR
     */
    public void cargarListasectorR() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL17870
                                                        .getValue());
        listasectorR = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, codigoCons);
    }

    /**
     * 
     * Carga la lista listasectorR
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listasectorRE
     */
    public void cargarListasectorRE() {
        listasectorRE = listasectorR;
    }

    /**
     * 
     * Carga la lista listaAno
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listasectorRE
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarPlanPptalChipsControladorUrlEnum.URL18636
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaConseFut
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listasectorRE
     */
    public void cargarListaConseFut() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL19540
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaConseFut = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoFutCons);
    }

    /**
     * 
     * Carga la lista listaConseFut
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listaConseFutE
     */
    public void cargarListaConseFutE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL20639
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaConseFutE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoFutCons);
    }

    /**
     * 
     * Carga la lista listacodigoFutR
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listacodigoFutR
     */
    public void cargarListacodigoFutR() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL21771
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listacodigoFutR = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * 
     * Carga la lista listacodigoFutR
     *
     * Metodo encargado de hacer la peticion a la base de datos y
     * almacenar la respuesta en la listacodigoFutRE
     */
    public void cargarListacodigoFutRE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarPlanPptalChipsControladorUrlEnum.URL22879
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listacodigoFutRE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton TrasladarConfigSigAno en
     * la vista ademas es el encargado de hacer visible el dialogo de
     * trasladar configuracion al siguiente ano
     *
     *
     */
    public void oprimirTrasladarConfigSigAno() {
        // <CODIGO_DESARROLLADO>
        dialogoVisibleTrasladar = true;
        String mensaje = idioma.getString("TB_TB3033");

        textoDialogTrasladar = mensaje.replace("s$ano$s",
                        String.valueOf(ano + 1));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton TraerFuentesPresupuesto en
     * la vista ademas es el encargado de ejecutar el procedimiento
     * PR_TRAER_FUENTES_PRESUPUESTO del paquete PCK_CHIPFUT
     *
     *
     */
    public void oprimirTraerFuentesPresupuesto() {
        // <CODIGO_DESARROLLADO>

        try {

            ejbChipFutCero.traerFuentesPresupuesto(compania, ano);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control destinoSel Este cambio
     * proporcioona la logica de los campos que se deben mopstrar
     * dependiendo del destino. Adem�s, se encarga de crear la
     * condcion para la grilla.
     * 
     * 
     */
    public void cambiardestinoSel() {
        // <CODIGO_DESARROLLADO>

        if ("I".equals(destinoSel) || "F".equals(destinoSel)
            || "S".equals(destinoSel) || "L".equals(destinoSel)) {
            visibleRegalias = false;
            visibleInversion = true;
            visibleRegaliasCredito = false;
            reasignarOrigen();

        }

        if ("R".equals(destinoSel)) {
            visibleInversion = false;
            visibleFuente = false;
            visibleRegalias = true;
            visibleRegaliasCredito = true;

            reasignarOrigen();

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Naturaleza
     * 
     * Este cambio proporcioona la logica de los campos que se deben
     * mopstrar dependiendo del destino. Adem�s, se encarga de crear
     * la condcion para la grilla.
     * 
     */
    public void cambiarNaturaleza() {
        // <CODIGO_DESARROLLADO>
        if ("R".equals(destinoSel) && "C".equals(naturaleza)) {
            visibleInversion = false;
            visibleRegaliasCredito = false;
            visibleRegalias = true;
        }
        if ("R".equals(destinoSel) && "D".equals(naturaleza)) {
            visibleInversion = false;
            visibleRegaliasCredito = true;
            visibleRegalias = true;
        }

        if (!"R".equals(destinoSel) && "C".equals(naturaleza)) {
            bloquearFuenteFut = true;
        }
        else {
            bloquearFuenteFut = false;
        }

        reasignarOrigen();
        cargarListacodigoFutRE();
        cargarListaConseFutE();
        cargarListaFuenteFutE();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * Este cambio se encarga de cargar todas las listas relacionadas
     * con el a�o, con el fin de actualizar los datos a mostrar en
     * la vista.
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>

        cargarListaFuenteFutE();
        cargarListaConseFutE();
        cargarListacodigoFutR();
        cargarListacodigoFutRE();
        cargarListafuenteFutReserva();
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarConseFutC(int rowNum) {

        String tipo = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get("TIPOVIGENCIA").toString();

        if ("VA".equals(tipo) && "I".equals(destinoSel)
            && "C".equals(naturaleza) && "A".equals(auxiliar.substring(0, 1))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4160"));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(codigoFuFutHCons, null);
            return;
        }

        validarCambioConseFut(rowNum, tipo);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private void validarCambioConseFut(int rowNum, String tipo) {
        if (!movimientoCodigoFut)

        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4161"));

            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(codigoFuFutHCons, null);
            return;
        }

        if ("VA".equals(tipo) && "F".equals(destinoSel)
            && !"C".equals(naturaleza)
            && !"1".equals(auxiliar.substring(0, 1))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4160"));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(codigoFuFutHCons, null);
            return;
        }

    }

    /**
     * Metodo ejecutado al cambiar el control FuenteFut en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFuenteFutC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("FUENTE_FUT", codigoFutAux);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control codigoFutR en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcodigoFutRC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control sectorR en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarsectorRC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("SECTOR_REGALIAS", codigoSectorRegalias);

    }

    /**
     * Metodo ejecutado al cambiar el control fuenteFutR en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarfuenteFutRC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConseFut
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConseFut(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigoFuFutHCons,
                        registroAux.getCampos().get(codigoFutCons));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConseFut
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConseFutE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(codigoFutCons),"").toString();
        movimientoCodigoFut = (boolean) registroAux.getCampos()
                        .get("MOVIMIENTO");

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listasectorR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasectorR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("SECTOR_REGALIAS",
                        registroAux.getCampos().get(codigoCons));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listasectorR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasectorRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoSectorRegalias = SysmanFunciones.nvl(registroAux.getCampos().get(codigoCons),"").toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafuenteFutR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuenteFutRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(codigoCons);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoFutR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoFutR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGOFUT_REGALIAS",
                        registroAux.getCampos().get(codigoCons));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoFutR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoFutRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFut
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteFutE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFutAux = registroAux.getCampos().get(codigoFuFutCons).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listavFuente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilavFuente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE",
                        registroAux.getCampos().get(codigoCons));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listavFuente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilavFuenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
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

        parametroFuenteRegalias();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * crearCodigosFut en la vista
     *
     *
     */
    public void aceptarcrearCodigosFut() {
        // <CODIGO_DESARROLLADO>

        try {

            ejbChipFutCero.traerCrearCodigosFut(compania, ano);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * trasladarConfiguracionSigAno en la vista
     *
     *
     */
    public void aceptartrasladarConfiguracionSigAno() {
        // <CODIGO_DESARROLLADO>
        String respuesta = "";
        String mensaje = "";
        String parametros = "'" + compania + "'," + ano;
        try {
            respuesta = Acciones.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CHIPFUT.FC_TRASLADAR_CONF_SIG_ANO",
                            parametros, Types.VARCHAR).toString();

            if (respuesta.equals(etiquetaNoExisteCodigoF)) {
                mensaje = idioma.getString(etiquetaNoExisteCodigoF);

                JsfUtil.agregarMensajeError(mensaje.replace(anoSigCons,
                                String.valueOf(ano + 1)));
            }

            if (respuesta.equals(etiquetaNoExisteFuenteF)) {
                mensaje = idioma.getString(etiquetaNoExisteFuenteF);

                JsfUtil.agregarMensajeError(mensaje.replace(anoSigCons,
                                String.valueOf(ano + 1)));
            }

            if (!respuesta.equals(etiquetaNoExisteCodigoF)
                && !respuesta.equals(etiquetaNoExisteFuenteF)) {

                mensaje = idioma.getString("TB_TB3041");
                mensaje = mensaje.replace("s$registros$s", respuesta);
                mensaje = mensaje.replace(anoSigCons,
                                String.valueOf(ano + 1));

                JsfUtil.agregarMensajeInformativo(mensaje);
            }

        }
        catch (NamingException | SQLException | IllegalAccessException
                        | InstantiationException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            dialogoVisibleTrasladar = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * trasladarConfiguracionSigAno en la vista
     *
     *
     */
    public void cancelartrasladarConfiguracionSigAno() {
        // <CODIGO_DESARROLLADO>
        dialogoVisible = false;
        dialogoVisibleTrasladar = false;
        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove("NOMBRE_SECTOR_REGA");

        registro.getCampos().remove("NOMBREFU_FUT");
        registro.getCampos().remove("CONSECUTIVO_FUT");

        registro.getLlave().remove("MES");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
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
     * 
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.CENTRO_COSTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.TERCERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove(GeneralParameterEnum.AUXILIAR.getName());
        registro.getCampos().remove(GeneralParameterEnum.REFERENCIA.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.FUENTE_RECURSO.getName());

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

        registro.getCampos().remove("NOMBREFU_FUT");

        registro.getCampos().remove("NOMBRE_SECTOR_REGA");
        registro.getCampos().remove("TIPOVIGENCIA"); 


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

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable destinoSel
     * 
     * @return destinoSel
     */
    public String getDestinoSel() {
        return destinoSel;
    }

    /**
     * Asigna la variable destinoSel
     * 
     * @param destinoSel
     * Variable a asignar en destinoSel
     */
    public void setDestinoSel(String destinoSel) {
        this.destinoSel = destinoSel;
    }

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
     * Retorna la variable ano
     * 
     * @return ano
     */

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaFuenteFut
     * 
     * @return listaFuenteFut
     */

    /**
     * Retorna la lista listavFuente
     * 
     * @return listavFuente
     */

    public RegistroDataModelImpl getListaFuenteFut() {
        return listaFuenteFut;
    }

    public RegistroDataModelImpl getListafuenteFutRE() {
        return listafuenteFutRE;
    }

    public void setListafuenteFutRE(RegistroDataModelImpl listafuenteFutRE) {
        this.listafuenteFutRE = listafuenteFutRE;
    }

    public RegistroDataModelImpl getListavFuente() {
        return listavFuente;
    }

    public void setListavFuente(RegistroDataModelImpl listavFuente) {
        this.listavFuente = listavFuente;
    }

    public RegistroDataModelImpl getListavFuenteE() {
        return listavFuenteE;
    }

    public void setListavFuenteE(RegistroDataModelImpl listavFuenteE) {
        this.listavFuenteE = listavFuenteE;
    }

    public void setListaFuenteFut(RegistroDataModelImpl listaFuenteFut) {
        this.listaFuenteFut = listaFuenteFut;
    }

    /**
     * Retorna la lista listafuenteFutReserva
     * 
     * @return listafuenteFutReserva
     */
    public List<Registro> getListafuenteFutReserva() {
        return listafuenteFutReserva;
    }

    /**
     * Asigna la lista listafuenteFutReserva
     * 
     * @param listafuenteFutReserva
     * Variable a asignar en listafuenteFutReserva
     */
    public void setListafuenteFutReserva(List<Registro> listafuenteFutReserva) {
        this.listafuenteFutReserva = listafuenteFutReserva;
    }

    /**
     * Retorna la lista listafuenteFutR
     * 
     * @return listafuenteFutR
     */
    public List<Registro> getListafuenteFutR() {
        return listafuenteFutR;
    }

    /**
     * Asigna la lista listafuenteFutR
     * 
     * @param listafuenteFutR
     * Variable a asignar en listafuenteFutR
     */
    public void setListafuenteFutR(List<Registro> listafuenteFutR) {
        this.listafuenteFutR = listafuenteFutR;
    }

    /**
     * Retorna la lista listasectorR
     * 
     * @return listasectorR
     */

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public RegistroDataModelImpl getListasectorR() {
        return listasectorR;
    }

    public void setListasectorR(RegistroDataModelImpl listasectorR) {
        this.listasectorR = listasectorR;
    }

    public RegistroDataModelImpl getListasectorRE() {
        return listasectorRE;
    }

    public void setListasectorRE(RegistroDataModelImpl listasectorRE) {
        this.listasectorRE = listasectorRE;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaConseFut
     * 
     * @return listaConseFut
     */
    public RegistroDataModelImpl getListaConseFut() {
        return listaConseFut;
    }

    /**
     * Asigna la lista listaConseFut
     * 
     * @param listaConseFut
     * Variable a asignar en listaConseFut
     */
    public void setListaConseFut(RegistroDataModelImpl listaConseFut) {
        this.listaConseFut = listaConseFut;
    }

    /**
     * Retorna la lista listaConseFut
     * 
     * @return listaConseFut
     */
    public RegistroDataModelImpl getListaConseFutE() {
        return listaConseFutE;
    }

    /**
     * Asigna la lista listaConseFut
     * 
     * @param listaConseFut
     * Variable a asignar en listaConseFut
     */
    public void setListaConseFutE(RegistroDataModelImpl listaConseFutE) {
        this.listaConseFutE = listaConseFutE;
    }

    /**
     * Retorna la lista listacodigoFutR
     * 
     * @return listacodigoFutR
     */
    public RegistroDataModelImpl getListacodigoFutR() {
        return listacodigoFutR;
    }

    /**
     * Asigna la lista listacodigoFutR
     * 
     * @param listacodigoFutR
     * Variable a asignar en listacodigoFutR
     */
    public void setListacodigoFutR(RegistroDataModelImpl listacodigoFutR) {
        this.listacodigoFutR = listacodigoFutR;
    }

    /**
     * Retorna la lista listacodigoFutR
     * 
     * @return listacodigoFutR
     */
    public RegistroDataModelImpl getListacodigoFutRE() {
        return listacodigoFutRE;
    }

    /**
     * Asigna la lista listacodigoFutR
     * 
     * @param listacodigoFutR
     * Variable a asignar en listacodigoFutR
     */
    public void setListacodigoFutRE(RegistroDataModelImpl listacodigoFutRE) {
        this.listacodigoFutRE = listacodigoFutRE;
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

    public boolean isVisibleInversion() {
        return visibleInversion;
    }

    public void setVisibleInversion(boolean visibleInversion) {
        this.visibleInversion = visibleInversion;
    }

    public boolean isVisibleRegalias() {
        return visibleRegalias;
    }

    public void setVisibleRegalias(boolean visibleRegalias) {
        this.visibleRegalias = visibleRegalias;
    }

    public boolean isVisibleRegaliasCredito() {
        return visibleRegaliasCredito;
    }

    public void setVisibleRegaliasCredito(boolean visibleRegaliasCredito) {
        this.visibleRegaliasCredito = visibleRegaliasCredito;
    }

    public boolean isVisibleFuenteRegistrosP() {
        return visibleFuenteRegistrosP;
    }

    public void setVisibleFuenteRegistrosP(boolean visibleFuenteRegistrosP) {
        this.visibleFuenteRegistrosP = visibleFuenteRegistrosP;
    }

    public boolean isVisibleFuente() {
        return visibleFuente;
    }

    public void setVisibleFuente(boolean visibleFuente) {
        this.visibleFuente = visibleFuente;
    }

    public RegistroDataModelImpl getListaFuenteFutE() {
        return listaFuenteFutE;
    }

    public void setListaFuenteFutE(RegistroDataModelImpl listaFuenteFutE) {
        this.listaFuenteFutE = listaFuenteFutE;
    }

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    public boolean isDialogoVisibleTrasladar() {
        return dialogoVisibleTrasladar;
    }

    public void setDialogoVisibleTrasladar(boolean dialogoVisibleTrasladar) {
        this.dialogoVisibleTrasladar = dialogoVisibleTrasladar;
    }

    public String getTextoDialogTrasladar() {
        return textoDialogTrasladar;
    }

    public void setTextoDialogTrasladar(String textoDialogTrasladar) {
        this.textoDialogTrasladar = textoDialogTrasladar;
    }

    public boolean isBloquearFuenteFut() {
        return bloquearFuenteFut;
    }

    public void setBloquearFuenteFut(boolean bloquearFuenteFut) {
        this.bloquearFuenteFut = bloquearFuenteFut;
    }

    private void parametroFuenteRegalias() {

        try {
            if ("SI".equals(SysmanFunciones
                            .nvl(Acciones.getParametro(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            compania,
                                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                                            SessionUtil.getModulo(), "SYSDATE"),
                                            "NO"))) {

                visibleFuenteRegistrosP = false;
                visibleFuente = true;

            }
            else {

                visibleFuenteRegistrosP = true;
                visibleFuente = false;
            }
        }
        catch (NamingException | SQLException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void crearfuentesFut() {
        String sql = "SELECT COUNT(*) REG FROM CODIGOSFUT WHERE ANO=" + ano;

        Registro reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sql);
        if (Integer.parseInt(reg.getCampos().get("REG").toString()) == 0) {
            dialogoVisible = true;
        }

    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
