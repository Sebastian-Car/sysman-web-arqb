/*-
 * UsuariosControlador.java
 *
 * 1.0
 *
 * 17/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.UsuariosControladorEnum;
import com.sysman.serviciospublicos.enums.UsuariosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;

/**
 * Informaci�n de suscriptores de servicios p�blicos.
 *
 * @version 1.0, 17/01/2017
 * @author vmolano
 *
 * -- Modificado por lcortes 20,21/06/2017. Refactorizacion de las
 * consultas para usar dss y reemplazos a los llamados a la clase
 * Acciones por ejb respectivo.
 */
@ManagedBean
@ViewScoped
public class UsuariosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el str del n�mero del ciclo del
     * usuario actual.
     */
    private final String strCiclo;

    /**
     * Constante que almacena el str del codigo de ruta
     */
    private final String strCodigoRuta;

    /**
     * Constante que almacena el codigo del modulo por la que se
     * ingresa a la aplicacion, se utiliza para el llamado del metodo
     * getParametro
     */
    private final String modulo;

    /**
     * Constante que almacena el str de c�digo para evitar
     * repetici�n de literales.
     */
    private final String strCodigo;

    /**
     * Constante que almacena el str del campo de comentarios
     */
    private final String strComentarios;

    /**
     * Constante que almacena el str del campo ESTADO
     */
    private final String strEstado;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable que controla si se muestran o no los controles de ruta
     * de 720.
     */
    private boolean visibleRuta720;

    /**
     * Variable que controla si se debe bloquear el combo de uso.
     */
    private boolean bloqueaUso;

    /**
     * Variable que controla si se debe bloquear el combo de estrato.
     */
    private boolean bloqueaEstrato;

    /**
     * Variable que controla si se debe bloquear el combo de estado.
     */
    private boolean bloqueaEstado;

    /**
     * Variable que controla si se debe bloquear el campo de
     * FrecuenciaAseoSemana
     */
    private boolean bloqueaFrecuencia;

    /**
     * Variable que controla si se debe cambiar el nombre del servicio
     * de acueducto.
     */
    private boolean cambiarNombreAcueducto;

    /**
     * Variable que almacena el nombre personalizado para acueducto,
     * desde parametros del sistema.
     */
    private String nombreAcueducto;

    /**
     * Variable que controla si se debe cambiar el nombre del servicio
     * de alcantarillado.
     */
    private boolean cambiarNombreAlcantarillado;

    /**
     * Variable que almacena el nombre personalizado para
     * alcantarillado, desde parametros del sistema.
     */
    private String nombreAlcantarillado;

    /**
     * Variable que controla si se debe cambiar el nombre del servicio
     * de aseo.
     */
    private boolean cambiarNombreAseo;

    /**
     * Variable que almacena el nombre personalizado para aseo, desde
     * parametros de facturaci�n.
     */
    private String nombreAseo;

    /**
     * Variable que controla si se debe cambiar el nombre del servicio
     * de alumbrado.
     */
    private boolean cambiarNombreAlumbrado;

    /**
     * Variable que almacena el nombre personalizado para el servicio
     * de alumbrado.
     */
    private String nombreAlumbrado;

    /**
     * Variable que controla si se debe mostrar o no el campo de
     * c�digo externo.
     */
    private boolean visibleCodigoExterno;

    /**
     * Variable que define si la entidad maneja aseo tercerizado.
     */
    private boolean manejaTercerizado;

    /**
     *
     */
    private boolean aplicaRes720;

    private String cicloExcluido720;

    private String camposRequeridos;

    private boolean sincronizarFrecuencia;

    private String usoEspecialAlumbrado;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    /**
     * Listado de tipos de predios asignables a un suscriptor.
     */
    private List<Registro> listaTipoPredio;

    /**
     * Listado de tipos de documentos asignables a un suscriptor.
     */
    private List<Registro> listaTipodocumento;

    /**
     * Listado de barrios que maneja la entidad y que pueden ser
     * asignados a un suscriptor.
     */
    private List<Registro> listacmbBarrio;

    /**
     * Listado de usos para asignar al suscriptor,
     */
    private List<Registro> listaUso;

    /**
     * Lista de Estratos para el suscriptor.
     */
    private List<Registro> listaEstrato;

    /**
     * Lista de estratos de aseo que pueden ser asignados al
     * suscriptor.
     */
    private List<Registro> listaEstratoAseo;

    /**
     * Listado de estratos de alumbrado, que pueden ser asignados al
     * suscriptor.
     */
    private List<Registro> listaEstratoAlumbrado;

    /**
     * Listado de usuarios que pueden ser asignados como
     * totalizadores.
     */
    private List<Registro> listaCODTOTALIZADOR;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista de medidores disponibles para asignaci�n.
     */
    private RegistroDataModelImpl listaMedidor;

    /**
     * Lista de c�digos CIIU que puede tener un suscritor.
     */
    private List<Registro> listaCIIU;

    /**
     * Listado de sectores hidr�ulicos a los que puede pertenecer un
     * suscriptor.
     */
    private List<Registro> listaRedHidraulica;

    /**
     * Listado de posibles empresas de aseo externo.
     */
    private RegistroDataModelImpl listaTxtEmpresaAseo;

    /**
     * Variable que almacena el c�digo del uso del suscriptor
     * actual.
     */
    private String usoActual;

    /**
     * Variable que almacena el n�mero del ciclo del suscritor
     * actual.
     */
    private String cicloActual;

    /**
     * Variable que almacena el c�digo de ruta del suscriptor
     * actual.
     */
    private String codigoRutaActual;

    /**
     * Variable que almacena el a�o actual del suscriptor.
     */
    private String anoActual;

    /**
     * Variable que almacena el periodo actual del suscriptor.
     */
    private String periodoActual;

    /**
     * Variable que controla la visualizaci�n del dialogo de total
     * deuda o facturado al cambiar de estado un suscriptor.
     */
    private boolean verDialogoTotal;

    /**
     * Variable que controla la visualizaci�n del campo de
     * CodTotalizador.
     */
    private boolean verCodTotalizador;

    /**
     * Variable que almacena el mensaje a mostrar cuando se valida un
     * cambio de estado.
     */
    private String mensajeDialogoTotal;

    /**
     * Variable que almacena el nombre completo del suscriptor actual.
     */
    private String nombreCompleto;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de UsuariosControlador
     */
    public UsuariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        strCodigo = "CODIGO";
        strComentarios = "COMENTARIOS";
        strEstado = "ESTADO";
        strCiclo = "ciclo";
        strCodigoRuta = "codigoruta";

        try {

            cargarListaObligatorios();

            numFormulario = GeneralCodigoFormaEnum.USUARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                cicloActual = parametrosEntrada.get(strCiclo).toString();
            }
            else {
                SessionUtil.redireccionarMenu();
            }

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private void cargarListaObligatorios() {
        // Campos obligatorios al registrar un usuario.
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaMedidor();
        cargarListaCIIU();
        cargarListaRedHidraulica();
        cargarListaTxtEmpresaAseo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTipoPredio();
        cargarListaTipodocumento();
        cargarListaUso();

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
        enumBase = GenericUrlEnum.SP_USUARIO;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(),
                        cicloActual);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaTipoPredio
     *
     */
    public void cargarListaTipoPredio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoPredio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL17601
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
     * Carga la lista listaTipodocumento
     *
     */
    public void cargarListaTipodocumento() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipodocumento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL12606
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
     * Carga la lista listacmbBarrio
     *
     */
    public void cargarListacmbBarrio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(UsuariosControladorEnum.PARAM3.getValue(),
                        registro.getCampos().get("PAIS"));
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        registro.getCampos().get("DEPARTAMENTO"));
        param.put(GeneralParameterEnum.CIUDAD.getName(),
                        registro.getCampos().get("CIUDAD"));
        try {
            listacmbBarrio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL13373
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
     * Carga la lista listaUso
     *
     */
    public void cargarListaUso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaUso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL13755
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
     * Carga la lista listaEstrato
     *
     */
    public void cargarListaEstrato() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(UsuariosControladorEnum.PARAM0.getValue(), usoActual);

        try {
            listaEstrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL14237
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
     * Carga la lista listaEstratoAseo
     *
     */
    public void cargarListaEstratoAseo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(UsuariosControladorEnum.PARAM0.getValue(), usoActual);

        try {
            listaEstratoAseo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL14820
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
     * Carga la lista listaEstratoAlumbrado
     *
     */
    public void cargarListaEstratoAlumbrado() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(UsuariosControladorEnum.PARAM0.getValue(), usoActual);

        try {
            listaEstratoAlumbrado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL15434
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
     * Carga la lista listaCODTOTALIZADOR
     *
     */
    public void cargarListaCODTOTALIZADOR() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(UsuariosControladorEnum.PARAM1.getValue(), cicloActual);
        param.put(UsuariosControladorEnum.PARAM2.getValue(), codigoRutaActual);

        try {
            listaCODTOTALIZADOR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL15435
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
     * Carga la lista listaMedidor
     *
     */
    public void cargarListaMedidor() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariosControladorUrlEnum.URL17602
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaMedidor = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     *
     * Carga la lista listaCIIU
     *
     */
    public void cargarListaCIIU() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCIIU = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL18121
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
     * Carga la lista listaRedHidraulica
     *
     */
    public void cargarListaRedHidraulica() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaRedHidraulica = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL18565
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
     * Carga la lista listaTxtEmpresaAseo
     *
     */
    public void cargarListaTxtEmpresaAseo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariosControladorUrlEnum.URL19629
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTxtEmpresaAseo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    public void cambiarTotalizador() {
        String totActual = registro.getCampos().get("TOTALIZADOR").toString();
        verCodTotalizador = false;
        if ("S".equals(totActual)) {
            verCodTotalizador = true;
        }

    }

    public void cambiarESTADO() {
        String nuevoEstado = registro.getCampos().get(strEstado).toString();

        if (!"A".equals(nuevoEstado)
            && !SysmanFunciones.validarVariableVacio(nuevoEstado)) {
            String bancoPerProceso = registro.getCampos()
                            .get("BANCOPERPROCESO").toString();
            Double totFacturaPerActual = Double.parseDouble(
                            SysmanFunciones.nvlStr(registro.getCampos()
                                            .get("TOTFACTURAPERACTUAL")
                                            .toString(), "0"));

            Double deuda = Double.parseDouble(SysmanFunciones.nvlStr(
                            registro.getCampos().get("DEUDA").toString(), "0"));

            String periodosAtraso = registro.getCampos().get("PERIODOSATRASO")
                            .toString();
            DecimalFormat df = new DecimalFormat("$###,###.###");
            if (SysmanFunciones.validarVariableVacio(bancoPerProceso)
                && (totFacturaPerActual != 0)) {

                mensajeDialogoTotal = idioma.getString("TB_TB2770");
                mensajeDialogoTotal = mensajeDialogoTotal.replace(
                                "s$totFacturaPerActual$s",
                                df.format(totFacturaPerActual));
                mensajeDialogoTotal = mensajeDialogoTotal
                                .replace("s$periodosAtraso$s", periodosAtraso);

                verDialogoTotal = true;
            }
            else if (deuda != 0) {
                mensajeDialogoTotal = idioma.getString("TB_TB2771");
                mensajeDialogoTotal = mensajeDialogoTotal.replace(
                                "s$totalDeuda$s",
                                df.format(deuda));
                mensajeDialogoTotal = mensajeDialogoTotal
                                .replace("s$periodosAtraso$s", periodosAtraso);

                verDialogoTotal = true;
            }

        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMedidor
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMedidor(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MEDIDOR",
                        registroAux.getCampos().get(strCodigo));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRedHidraulica
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRedHidraulica(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("REDHIDRAULICA",
                        registroAux.getCampos().get(strCodigo));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTxtEmpresaAseo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTxtEmpresaAseo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EMPRESAASEOEXT",
                        registroAux.getCampos().get("ID"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton cmbDirTecnica en la vista
     *
     * Abre el formulario correspondiente para la generaci�n de la
     * direcci�n.
     *
     */
    public void oprimircmbDirTecnica() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { strCiclo, strCodigoRuta, "direccionInicial" };
        Object[] valores = { cicloActual, codigoRutaActual,
                             registro.getCampos().get("DIRTECNICA") };
        int numForm = GeneralCodigoFormaEnum.ARMAR_DIRECCIONES_CONTROLADOR
                        .getCodigo();
        SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numForm),
                        modulo, campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton cmdGenerar en la vista Se
     * consulta y extrae la �ltima matricula para posteriormente
     * generar el consecutivo.
     */
    public void oprimircmdGenerar() {
        // <CODIGO_DESARROLLADO>

        try {
            Registro rMatricula = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosControladorUrlEnum.URL15436
                                                                            .getValue())
                                            .getUrl(), null));

            String nuevaMatricula;
            Date sysdate = new Date();
            String ano = String.valueOf(SysmanFunciones.ano(sysdate));
            String mes = SysmanFunciones.strZero(
                            String.valueOf(SysmanFunciones.mes(sysdate)), 2);

            if (rMatricula != null) {

                String consecutivo = SysmanFunciones
                                .strZero(rMatricula.getCampos().get("MATRICULA")
                                                .toString()
                                    + 1, 4);

                nuevaMatricula = ano + mes + consecutivo;

            }
            else {
                nuevaMatricula = ano + mes + "0001";
            }

            registro.getCampos().put("MATRICULA", nuevaMatricula);
            agregarRegistroNuevo(false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton VerFactura en la vista
     *
     * Redirige a la consulta de facturaci�n del suscriptor actual.
     *
     */
    public void oprimirVerFactura() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmbDircorrespondencia en
     * la vista
     *
     * Abre el formulario que permite el diligenciamiento de la
     * direcci�n.
     *
     */
    public void oprimircmbDircorrespondencia() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { strCiclo, strCodigoRuta, "direccionInicial" };
        Object[] valores = { cicloActual, codigoRutaActual,
                             registro.getCampos().get("DIRCORRESPONDENCIA") };
        int numForm = GeneralCodigoFormaEnum.ARMAR_DIRECCIONES_CONTROLADOR
                        .getCodigo();
        SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numForm),
                        modulo, campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Multiusuario en la vista
     *
     * Abre el formulario de Multiusuarios.
     *
     */
    public void oprimirMultiusuario() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { strCiclo, strCodigoRuta };
        Object[] valores = { cicloActual, codigoRutaActual };
        int numForm = GeneralCodigoFormaEnum.MULTIUSUARIOS_CONTROLADOR
                        .getCodigo();
        SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numForm),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton btHistoricosServicio
     *
     * Abre el formulario de cambios hist�ricos de servicio.
     *
     */
    public void oprimirbtHistoricoServicio() {
        String[] campos = { strCiclo, strCodigoRuta };
        Object[] valores = { cicloActual, codigoRutaActual };
        int numForm = GeneralCodigoFormaEnum.HISTORIANOVEDADES_CONTROLADOR
                        .getCodigo();
        SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numForm),
                        modulo, campos, valores);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton btAuditoria
     *
     * Abre el formulario de auditoria de usuario.
     *
     */
    public void oprimirbtAuditoria() {
        String[] campos = { strCiclo, strCodigoRuta };
        Object[] valores = { cicloActual, codigoRutaActual };
        int numForm = GeneralCodigoFormaEnum.AUDITORIAUSUARIOS_CONTROLADOR
                        .getCodigo();
        SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numForm),
                        modulo, campos, valores);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdComentarios en la vista
     *
     * Se abre el formulario correspondiente para la gesti�n de los
     * comentarios de facturaci�n del suscriptor actual.
     *
     */
    public void oprimirCmdComentarios() {
        // <CODIGO_DESARROLLADO>
        String comentario = SysmanFunciones
                        .nvl(registro.getCampos().get(strComentarios), "")
                        .toString();
        String[] campos = { "ano", "periodo", "comentario" };
        String[] valores = { anoActual, periodoActual, comentario };
        int numForm = GeneralCodigoFormaEnum.FRM_COMENTARIOS_CONTROLADOR
                        .getCodigo();
        SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numForm),
                        modulo, campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        try {
            cambiarNombreAseo = parSiNo("PR_CAMBIAR_NOMBRE_ASEO");
            manejaTercerizado = parSiNo("PR_MANEJA_TERCERIZADO");
            visibleCodigoExterno = parSiNo("PR_NOVEDADES_EXTERNAS");
            cambiarNombreAcueducto = parSiNo("PR_CAMBIAR_NOMBRE_ACUEDUCTO");
            cambiarNombreAlumbrado = parSiNo("PR_CAMBIAR_NOMBRE_ALUMBRADO");
            cambiarNombreAlcantarillado = parSiNo(
                            "PR_CAMBIAR_NOMBRE_ALCANTARILLADO");

            nombreAcueducto = parStr("PR_NOMBRE_ACUEDUCTO");
            nombreAlcantarillado = parStr("PR_NOMBRE_ALCANTARILLADO");
            nombreAseo = parStr("PR_NOMBRE_ASEO");
            nombreAlumbrado = parStr("PR_NOMBRE_ALUMBRADO");
        }
        catch (NamingException | SQLException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Retorna el valor dado por la funcion de obtener parametro a
     * partir del nombre del mismo.
     *
     * @param parametro
     * - Identificador del properties con el nombre del parametro.
     * @return
     * @throws SystemException
     */
    private String parStr(String parametro)
                    throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania,
                        parametros.getString(parametro), modulo, new Date(),
                        false);

    }

    /**
     * Devuelve el valor del parametro o el valor defecto segun
     * corresponda.
     *
     * @param parametro
     * - Identificador del properties con el nombre del parametro.
     * @param defecto
     * - Valor que se retorna si el parametro fue nulo.
     * @return
     * @throws NamingException
     * @throws SQLException
     * @throws SystemException
     */
    private String parStrDefault(String parametro, String defecto)
                    throws NamingException, SQLException, SystemException {
        return SysmanFunciones.nvlStr(parStr(parametro), defecto);
    }

    /**
     * Evalua un determinado parametro y retorna su equivalente en
     * boolean
     *
     * @param parametro
     * - Identificador del properties con el nombre del parametro.
     * @return
     * @throws NamingException
     * @throws SQLException
     * @throws SystemException
     */
    private boolean parSiNo(String parametro)
                    throws NamingException, SQLException, SystemException {

        String parFinal = parStrDefault(parametro, "NO");

        return "SI".equals(parFinal) ? true : false;
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        usoActual = registro.getCampos().get("USO").toString();
        codigoRutaActual = registro.getCampos().get("CODIGORUTA").toString();
        anoActual = registro.getCampos().get("ANO").toString();
        periodoActual = registro.getCampos().get("PERIODO").toString();

        nombreCompleto = registro.getCampos().get("NOMBRES") + " "
            + registro.getCampos().get("PRIMERAPELLIDO") + " "
            + registro.getCampos().get("SEGUNDOAPELLIDO");

        cargarListacmbBarrio();
        cargarListaEstrato();
        cargarListaEstratoAseo();
        cargarListaEstratoAlumbrado();
        cargarListaCODTOTALIZADOR();

        try {
            String modificador = ejbSysmanUtil.consultarParametro(compania,
                            parametros.getString("PR_GRUPO_MODIFICADOR"),
                            modulo, new Date(), false);

            String grupoActual = SessionUtil.getGrupo(modulo).getCodigo();

            if (grupoActual.equalsIgnoreCase(modificador)) {
                bloqueaUso = false;
                bloqueaEstrato = false;
                bloqueaEstado = false;
                bloqueaFrecuencia = false;
            }
            else {
                bloqueaUso = true;
                bloqueaEstrato = true;
                bloqueaEstado = true;
                bloqueaFrecuencia = true;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return True
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return True
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
     * @return True
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos().remove(UsuariosControladorEnum.PARAM3.getValue());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DEPARTAMENTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CIUDAD.getName());
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
     * @return True
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void retornarFormularioCmdComentarios(SelectEvent event) {
        // <CODIGO_DESARROLLADO>

        if (event.getObject() != null) {
            String comentariosActuales = event.getObject().toString();
            String comentarioAnterior = SysmanFunciones
                            .nvl(registro.getCampos().get(strComentarios), "")
                            .toString();
            if (!comentariosActuales.equals(comentarioAnterior)) {
                registro.getCampos().put(strComentarios, comentariosActuales);
                agregarRegistroNuevo(false);
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormulariocmbDirTecnica(SelectEvent event) {
        actualizarDireccion("DIRTECNICA");
    }

    public void retornarFormulariocmbDircorrespondencia(SelectEvent event) {
        actualizarDireccion("DIRCORRESPONDENCIA");
    }

    public void actualizarDireccion(String strDestino) {
        Map<String, Object> params = SessionUtil.getFlash();
        if (params != null) {
            String dirNueva = params.get("direccion").toString();
            if (!"".equals(dirNueva)) {
                registro.getCampos().put(strDestino, dirNueva);
                agregarRegistroNuevo(false);
            }
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DG_Total en la vista
     *
     * Se cierra el dialogo
     *
     */
    public void aceptarDGTotal() {
        // <CODIGO_DESARROLLADO>
        verDialogoTotal = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * DG_Total en la vista
     *
     * Deshace el valor cambiado al campo de estado.
     *
     */
    public void cancelarDGTotal() {
        // <CODIGO_DESARROLLADO>

        String estadoAnterior = registroIni.get(strEstado).toString();

        registro.getCampos().put(strEstado, estadoAnterior);
        verDialogoTotal = false;
        // </CODIGO_DESARROLLADO>
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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoPredio
     *
     * @return listaTipoPredio
     */
    public List<Registro> getListaTipoPredio() {
        return listaTipoPredio;
    }

    /**
     * Asigna la lista listaTipoPredio
     *
     * @param listaTipoPredio
     * Variable a asignar en listaTipoPredio
     */
    public void setListaTipoPredio(
        List<Registro> listaTipoPredio) {
        this.listaTipoPredio = listaTipoPredio;
    }

    /**
     * Retorna la lista listaTipodocumento
     *
     * @return listaTipodocumento
     */
    public List<Registro> getListaTipodocumento() {
        return listaTipodocumento;
    }

    /**
     * Asigna la lista listaTipodocumento
     *
     * @param listaTipodocumento
     * Variable a asignar en listaTipodocumento
     */
    public void setListaTipodocumento(List<Registro> listaTipodocumento) {
        this.listaTipodocumento = listaTipodocumento;
    }

    /**
     * Retorna la lista listacmbBarrio
     *
     * @return listacmbBarrio
     */
    public List<Registro> getListacmbBarrio() {
        return listacmbBarrio;
    }

    /**
     * Asigna la lista listacmbBarrio
     *
     * @param listacmbBarrio
     * Variable a asignar en listacmbBarrio
     */
    public void setListacmbBarrio(List<Registro> listacmbBarrio) {
        this.listacmbBarrio = listacmbBarrio;
    }

    /**
     * Retorna la lista listaUso
     *
     * @return listaUso
     */
    public List<Registro> getListaUso() {
        return listaUso;
    }

    /**
     * Asigna la lista listaUso
     *
     * @param listaUso
     * Variable a asignar en listaUso
     */
    public void setListaUso(List<Registro> listaUso) {
        this.listaUso = listaUso;
    }

    /**
     * Retorna la lista listaEstrato
     *
     * @return listaEstrato
     */
    public List<Registro> getListaEstrato() {
        return listaEstrato;
    }

    /**
     * Asigna la lista listaEstrato
     *
     * @param listaEstrato
     * Variable a asignar en listaEstrato
     */
    public void setListaEstrato(List<Registro> listaEstrato) {
        this.listaEstrato = listaEstrato;
    }

    /**
     * Retorna la lista listaEstratoAseo
     *
     * @return listaEstratoAseo
     */
    public List<Registro> getListaEstratoAseo() {
        return listaEstratoAseo;
    }

    /**
     * Asigna la lista listaEstratoAseo
     *
     * @param listaEstratoAseo
     * Variable a asignar en listaEstratoAseo
     */
    public void setListaEstratoAseo(List<Registro> listaEstratoAseo) {
        this.listaEstratoAseo = listaEstratoAseo;
    }

    /**
     * Retorna la lista listaEstratoAlumbrado
     *
     * @return listaEstratoAlumbrado
     */
    public List<Registro> getListaEstratoAlumbrado() {
        return listaEstratoAlumbrado;
    }

    /**
     * Asigna la lista listaEstratoAlumbrado
     *
     * @param listaEstratoAlumbrado
     * Variable a asignar en listaEstratoAlumbrado
     */
    public void setListaEstratoAlumbrado(List<Registro> listaEstratoAlumbrado) {
        this.listaEstratoAlumbrado = listaEstratoAlumbrado;
    }

    /**
     * Retorna la lista listaCODTOTALIZADOR
     *
     * @return listaCODTOTALIZADOR
     */
    public List<Registro> getListaCODTOTALIZADOR() {
        return listaCODTOTALIZADOR;
    }

    /**
     * Asigna la lista listaCODTOTALIZADOR
     *
     * @param listaCODTOTALIZADOR
     * Variable a asignar en listaCODTOTALIZADOR
     */
    public void setListaCODTOTALIZADOR(List<Registro> listaCODTOTALIZADOR) {
        this.listaCODTOTALIZADOR = listaCODTOTALIZADOR;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaMedidor
     *
     * @return listaMedidor
     */
    public RegistroDataModelImpl getListaMedidor() {
        return listaMedidor;
    }

    /**
     * Asigna la lista listaMedidor
     *
     * @param listaMedidor
     * Variable a asignar en listaMedidor
     */
    public void setListaMedidor(RegistroDataModelImpl listaMedidor) {
        this.listaMedidor = listaMedidor;
    }

    /**
     * Retorna la lista listaCIIU
     *
     * @return listaCIIU
     */
    public List<Registro> getListaCIIU() {
        return listaCIIU;
    }

    /**
     * Asigna la lista listaCIIU
     *
     * @param listaCIIU
     * Variable a asignar en listaCIIU
     */
    public void setListaCIIU(List<Registro> listaCIIU) {
        this.listaCIIU = listaCIIU;
    }

    /**
     * Retorna la lista listaRedHidraulica
     *
     * @return listaRedHidraulica
     */
    public List<Registro> getListaRedHidraulica() {
        return listaRedHidraulica;
    }

    /**
     * Asigna la lista listaRedHidraulica
     *
     * @param listaRedHidraulica
     * Variable a asignar en listaRedHidraulica
     */
    public void setListaRedHidraulica(List<Registro> listaRedHidraulica) {
        this.listaRedHidraulica = listaRedHidraulica;
    }

    /**
     * Retorna la lista listaTxtEmpresaAseo
     *
     * @return listaTxtEmpresaAseo
     */
    public RegistroDataModelImpl getListaTxtEmpresaAseo() {
        return listaTxtEmpresaAseo;
    }

    /**
     * Asigna la lista listaTxtEmpresaAseo
     *
     * @param listaTxtEmpresaAseo
     * Variable a asignar en listaTxtEmpresaAseo
     */
    public void setListaTxtEmpresaAseo(
        RegistroDataModelImpl listaTxtEmpresaAseo) {
        this.listaTxtEmpresaAseo = listaTxtEmpresaAseo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public boolean isVisibleRuta720() {
        return visibleRuta720;
    }

    public void setVisibleRuta720(boolean visibleRuta720) {
        this.visibleRuta720 = visibleRuta720;
    }

    public boolean isVerDialogoTotal() {
        return verDialogoTotal;
    }

    public void setVerDialogoTotal(boolean verDialogoTotal) {
        this.verDialogoTotal = verDialogoTotal;
    }

    public String getMensajeDialogoTotal() {
        return mensajeDialogoTotal;
    }

    public void setMensajeDialogoTotal(String mensajeDialogoTotal) {
        this.mensajeDialogoTotal = mensajeDialogoTotal;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public boolean isBloqueaUso() {
        return bloqueaUso;
    }

    public void setBloqueaUso(boolean bloqueaUso) {
        this.bloqueaUso = bloqueaUso;
    }

    public boolean isBloqueaEstrato() {
        return bloqueaEstrato;
    }

    public void setBloqueaEstrato(boolean bloqueaEstrato) {
        this.bloqueaEstrato = bloqueaEstrato;
    }

    public boolean isBloqueaEstado() {
        return bloqueaEstado;
    }

    public void setBloqueaEstado(boolean bloqueaEstado) {
        this.bloqueaEstado = bloqueaEstado;
    }

    public boolean isBloqueaFrecuencia() {
        return bloqueaFrecuencia;
    }

    public void setBloqueaFrecuencia(boolean bloqueaFrecuencia) {
        this.bloqueaFrecuencia = bloqueaFrecuencia;
    }

    public boolean isVerCodTotalizador() {
        return verCodTotalizador;
    }

    public void setVerCodTotalizador(boolean verCodTotalizador) {
        this.verCodTotalizador = verCodTotalizador;
    }

    public boolean isCambiarNombreAcueducto() {
        return cambiarNombreAcueducto;
    }

    public void setCambiarNombreAcueducto(boolean cambiarNombreAcueducto) {
        this.cambiarNombreAcueducto = cambiarNombreAcueducto;
    }

    public String getNombreAcueducto() {
        return nombreAcueducto;
    }

    public void setNombreAcueducto(String nombreAcueducto) {
        this.nombreAcueducto = nombreAcueducto;
    }

    public boolean isCambiarNombreAlcantarillado() {
        return cambiarNombreAlcantarillado;
    }

    public void setCambiarNombreAlcantarillado(
        boolean cambiarNombreAlcantarillado) {
        this.cambiarNombreAlcantarillado = cambiarNombreAlcantarillado;
    }

    public String getNombreAlcantarillado() {
        return nombreAlcantarillado;
    }

    public void setNombreAlcantarillado(String nombreAlcantarillado) {
        this.nombreAlcantarillado = nombreAlcantarillado;
    }

    public boolean isCambiarNombreAseo() {
        return cambiarNombreAseo;
    }

    public void setCambiarNombreAseo(boolean cambiarNombreAseo) {
        this.cambiarNombreAseo = cambiarNombreAseo;
    }

    public String getNombreAseo() {
        return nombreAseo;
    }

    public void setNombreAseo(String nombreAseo) {
        this.nombreAseo = nombreAseo;
    }

    public boolean isCambiarNombreAlumbrado() {
        return cambiarNombreAlumbrado;
    }

    public void setCambiarNombreAlumbrado(boolean cambiarNombreAlumbrado) {
        this.cambiarNombreAlumbrado = cambiarNombreAlumbrado;
    }

    public String getNombreAlumbrado() {
        return nombreAlumbrado;
    }

    public void setNombreAlumbrado(String nombreAlumbrado) {
        this.nombreAlumbrado = nombreAlumbrado;
    }

    public boolean isVisibleCodigoExterno() {
        return visibleCodigoExterno;
    }

    public void setVisibleCodigoExterno(boolean visibleCodigoExterno) {
        this.visibleCodigoExterno = visibleCodigoExterno;
    }

    public boolean isManejaTercerizado() {
        return manejaTercerizado;
    }

    public void setManejaTercerizado(boolean manejaTercerizado) {
        this.manejaTercerizado = manejaTercerizado;
    }

    public boolean isAplicaRes720() {
        return aplicaRes720;
    }

    public void setAplicaRes720(boolean aplicaRes720) {
        this.aplicaRes720 = aplicaRes720;
    }

    public String getCicloExcluido720() {
        return cicloExcluido720;
    }

    public void setCicloExcluido720(String cicloExcluido720) {
        this.cicloExcluido720 = cicloExcluido720;
    }

    public String getCamposRequeridos() {
        return camposRequeridos;
    }

    public void setCamposRequeridos(String camposRequeridos) {
        this.camposRequeridos = camposRequeridos;
    }

    public boolean isSincronizarFrecuencia() {
        return sincronizarFrecuencia;
    }

    public void setSincronizarFrecuencia(boolean sincronizarFrecuencia) {
        this.sincronizarFrecuencia = sincronizarFrecuencia;
    }

    public String getUsoEspecialAlumbrado() {
        return usoEspecialAlumbrado;
    }

    public void setUsoEspecialAlumbrado(String usoEspecialAlumbrado) {
        this.usoEspecialAlumbrado = usoEspecialAlumbrado;
    }

    // </SET_GET_ADICIONALES>
}
