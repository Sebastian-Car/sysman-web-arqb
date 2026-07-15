/*-
 * FrmConsultasControlador.java
 *
 * 1.0
 * 
 * 12/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmConsultasControladorEnum;
import com.sysman.hojasdevida.enums.FrmConsultasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar plantillas o reportes segun seleccion
 *
 * @version 1.0, 12/01/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped

public class FrmConsultasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el codigo de proceso recibido por
     * parametro
     */
    private String proceso;
    /**
     * Variable que almacena el numero de ano seleccioado
     */
    private String ano;
    /**
     * Variable que almacena el numero de mes seleccionado
     */
    private String mes;
    /**
     * Variable que almacena el numero de periodo seleccionado
     */
    private String periodo;
    /**
     * Variable que almacena el valor de los empleados activos segun
     * identificacion
     */
    private String idEmpleados;

    /**
     * Atributo que almacena el codigo de la ciudad donde se encuentra
     * registrada la compania en la que se ingresa en la aplicacion
     */
    private String codigoCiudad;
    /**
     * Atributo que almacena el codigo del departamento donde se
     * encuentra registrada la compania en la que se ingresa en la
     * aplicacion
     */
    private String codigoDepartamento;
    /**
     * Atributo que almacena el nombre de la ciudad donde se encuentra
     * registrada la compania en la que se ingresa en la aplicacion
     */
    private String nombreCiudad;
    /**
     * Atributo que almacena el nombre de la compañia por la cual se
     * ingresa en la aplicacion
     */
    private String nombreCompania;
    /**
     * Atributo que almacena el nit de la compañia por al cual se
     * ingresa en la aplicacion
     */
    private String nit;
    /**
     * Atributo que almacena el la fecha con el ultimo dia y mes del
     * ano seleccionado
     */
    private String fecha2;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR INGRESOS PATRIMONIOS PARA CERTIFICADO}
     */
    private String parValorIngresos;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR INGRESS PATRIMONIOS PARA CERTIFICADO UVT}
     */
    private String parValorIngress;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR INGRESS SUPERIORES PARA CERTIFICADO UVT}
     */
    private String parValorIngressSup;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR INGRESOS SUPERIORES PARA CERTIFICADO}
     */
    private String parValorSuperiores;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR CONSUMOS CON TARJETAS PARA CERTIFICADO}
     */
    private String parValorConsumos;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR CONSUMS CON TARJ PARA CERTIFICADO UVT}
     */
    private String parValorConsums;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR TOTAL COMPRAS Y CONSUMOS PARA CERTIFICADO}
     */
    private String parValorTotal;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR TOTAL COMPRAS Y CON PARA CERTIFICADO UVT}
     */
    private String parValorTotalCom;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR CONSIGNACIONES BANCARIAS PARA CERTIFICADO}
     */
    private String parValorConsignaciones;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {VALOR CONSIG BANCARIAS PARA CERTIFICADO UVT}
     */
    private String parValorConsig;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {CEDULA DE QUIEN FIRMA CERTIFICADO DIAN}
     */
    private String parCedula;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * {NOMBRE DE QUIEN FIRMA CERTIFICADO DIAN}
     */
    private String parNombre;
    /**
     * Atributo que almacena el numero de documento del usuario que
     * ingreso a la aplicacion
     */
    private String cedula;

    /**
     * atributo que valida si el boton de presentar o imprimir esta
     * activo o inactivo
     */
    private boolean inactivarBoton;

    /**
     * Registro auxiliar para guardar campos segun seleccion
     */
    private Registro registroAux;

    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de los anos de la tabla periodo
     */
    private List<Registro> listaAno;
    /**
     * Lista de registros de los meses de la tabla periodo
     */
    private List<Registro> listaMes;
    /**
     * Lista de registos de los periodos
     */
    private List<Registro> listaPeriodo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbNominaCeroGeneralRemote ejbNominaCeroGeneral;

    /**
     * Crea una nueva instancia de FrmConsultasControlador
     */
    public FrmConsultasControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCiudad = SessionUtil.getCompaniaIngreso().getCodigoCiudad();
        codigoDepartamento = SessionUtil.getCompaniaIngreso()
                        .getCodigoDepartamento();
        nombreCiudad = SessionUtil.getCompaniaIngreso().getCiudad();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        nit = SessionUtil.getCompaniaIngreso().getNit();
        cedula = SessionUtil.getUser().getCedula();
        try {
            // 1593
            numFormulario = GeneralCodigoFormaEnum.CONSULTAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            // <INI_ADICIONAL>
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

        enumBase = GenericUrlEnum.AUTOSER_CONSULTAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();

        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmConsultasControladorUrlEnum.URL4789
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmConsultasControladorUrlEnum.URL5201
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaPeriodo
     *
     */
    public void cargarListaPeriodo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmConsultasControladorUrlEnum.URL5769
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_CARGAR_LISTA>

    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton ver
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirver(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        if (validarCombos()) {
            return;

        }

        archivoDescarga = null;
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(),
                        "NOMBRE_PLANTILLA")) {

            try {

                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put("cedula",
                                SysmanFunciones.concatenar("'", cedula, "'"));

                HashMap<String, Object> parametros = new HashMap<>();

                if ("2".equals(reg.getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName())
                                .toString())) {
                    Date fechaFin = ejbNominaCeroGeneral.getFechaPeriodoIniFin(
                                    compania,
                                    Integer.parseInt(proceso),
                                    Integer.parseInt(ano),
                                    Integer.parseInt(mes),
                                    Integer.parseInt(periodo),
                                    false, true);
                    Date fechaIni = ejbNominaCeroGeneral.getFechaPeriodoIniFin(
                                    compania,
                                    Integer.parseInt(proceso),
                                    Integer.parseInt(ano),
                                    Integer.parseInt(mes),
                                    Integer.parseInt(periodo),
                                    true, true);
                    String tituloVolante = SysmanFunciones.concatenar(
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaIni),
                                    " ",
                                    idioma.getString("TB_TB3685"), " ",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFin));
                    reemplazar.put("proceso", proceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put("periodo", periodo);
                    reemplazar.put("idEmpleados", idEmpleados);
                    parametros.put("PR_TITULO", tituloVolante);
                    parametros.put("PR_NOMBREEMPRESA",
                                    SessionUtil.getCompaniaIngreso()
                                                    .getNombre());

                    Reporteador.resuelveConsulta(
                                    "000141VOLANTESUNO_AUTOSERVICIO",
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar, parametros);

                }
                else if ("5".equals(reg.getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName())
                                .toString())) {

                    reemplazar.put("fecha2", SysmanFunciones.formatearFecha(
                                    SysmanFunciones.convertirAFecha(fecha2)));

                    reemplazar.put("fecha1", SysmanFunciones.formatearFecha(
                                    SysmanFunciones.convertirAFecha(
                                                    SysmanFunciones.concatenar(
                                                                    "01/01/",
                                                                    ano))));

                    reemplazar.put("fechaExpedicion",
                                    SysmanFunciones.formatearFecha(new Date()));

                    reemplazar.put("modulo", SessionUtil.getModulo());
                    reemplazar.put("anio", ano);
                    reemplazar.put("condDCTO", SysmanFunciones.concatenar(
                                    " AND PERSONAL.NUMERO_DCTO  = ",
                                    cedula));

                    reemplazar.put("valorPorcentaje", SysmanFunciones.nvlStr(
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "VALOR PORCENTAJE APLICAR PROCESO RETEFUENTE",
                                                    SessionUtil.getModulo(),
                                                    new Date(), true),
                                    "0"));
                    reemplazar.put("nit", SysmanFunciones.concatenar("'",
                                    ejecutarFCMINIT(312), "'"));
                    reemplazar.put("dc", SysmanFunciones.concatenar("'",
                                    ejecutarFCMINIT(1), "'"));

                    cargarParametros(parametros);

                    Reporteador.resuelveConsulta(
                                    reg.getCampos().get(
                                                    FrmConsultasControladorEnum.REPORTE
                                                                    .getValue())
                                                    .toString(),
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar, parametros);

                }

                archivoDescarga = JsfUtil.exportarStreamed(
                                reg.getCampos().get(
                                                FrmConsultasControladorEnum.REPORTE
                                                                .getValue())
                                                .toString(),
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.PDF);

            }
            catch (JRException | IOException | SysmanException
                            | SystemException | ParseException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }

        }
        else {
            registroAux = reg;
            if (validarPlantilla(reg)) {
                return;
            }
            else {

                try {
                    Map<String, Object> param = new TreeMap<>();

                    param.put(GeneralParameterEnum.CODIGO.getName(),
                                    reg.getCampos().get(
                                                    FrmConsultasControladorEnum.CODIGO_PLANTILLA
                                                                    .getValue()));

                    param.put(FrmConsultasControladorEnum.TIPO.getValue(),
                                    "41");

                    param.put(GeneralParameterEnum.NOMBRE.getName(),
                                    reg.getCampos().get(
                                                    FrmConsultasControladorEnum.NOMBRE_PLANTILLA
                                                                    .getValue()));

                    Registro rs;

                    rs = RegistroConverter
                                    .toRegistro(requestManager
                                                    .get(UrlServiceUtil
                                                                    .getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    FrmConsultasControladorUrlEnum.URL292
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                                    param));

                    Date fecha = (Date) rs.getCampos()
                                    .get(GeneralParameterEnum.FECHA.getName());
                    String codigoPlantilla = SysmanFunciones
                                    .nvl(reg.getCampos().get(
                                                    FrmConsultasControladorEnum.CODIGO_PLANTILLA
                                                                    .getValue()),
                                                    "")
                                    .toString();

                    String strNombreDocumento = SysmanFunciones
                                    .nvl(reg.getCampos().get(
                                                    FrmConsultasControladorEnum.NOMBRE_PLANTILLA
                                                                    .getValue()),
                                                    "")
                                    .toString();

                    String[] campos = new String[3];
                    String[] valores = new String[3];
                    campos[0] = "codigoPlantilla";
                    campos[1] = "fechaPlantilla";
                    campos[2] = "nombreDocDescarga";

                    valores[0] = codigoPlantilla;
                    valores[1] = SysmanFunciones.formatearFecha(fecha);
                    valores[2] = SysmanFunciones.concatenar(strNombreDocumento,
                                    "_",
                                    cedula);

                    HashMap<String, String> variablesConsultaW = new HashMap<>();
                    variablesConsultaW.put("s$compania$s",
                                    SysmanFunciones.concatenar("'", compania,
                                                    "'"));
                    variablesConsultaW.put("s$cedula$s",
                                    SysmanFunciones.concatenar("'",
                                                    cedula,
                                                    "'"));
                    variablesConsultaW.put("s$usuario$s",
                                    SysmanFunciones.concatenar("'",
                                                    SessionUtil.getUser()
                                                                    .getCodigo(),
                                                    "'"));
                    variablesConsultaW.put("s$ano$s", ano);
                    variablesConsultaW.put("s$mes$s", mes);
                    variablesConsultaW.put("s$idEmpleados$s", idEmpleados);

                    // variables por parametro para documento word
                    SessionUtil.setSessionVar("variablesConsultaWord",
                                    variablesConsultaW);
                    String numForm = String
                                    .valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                    .getCodigo());
                    SessionUtil.cargarModalDatosFlash(numForm,
                                    SessionUtil.getModulo(),
                                    campos, valores);
                }

                catch (SystemException e) {
                    JsfUtil.agregarMensajeError(e.getMessage());
                    logger.error(e.getMessage(), e);

                }
            }

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaAno();
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        validarEntrada();

        if (cedula != null) {
            fecha2 = SysmanFunciones.concatenar("31/12/",
                            Integer.toString(SysmanFunciones.ano(new Date())));
            iniciarParametros();
        }

        // </CODIGO_DESARROLLADO>
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
     * 
     * @retur true
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
     * @return true
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
     * @return true
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
     * 
     * @return true
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
     * 
     * @return true
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
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
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

    /**
     * // * Metodo que valida si el registro tiene plantilla asociada
     * 
     * @param codigo
     * @return
     */
    public boolean validarPlantilla(Registro reg) {

        if (SysmanFunciones.validarCampoVacio(reg.getCampos(),
                        FrmConsultasControladorEnum.CODIGO_PLANTILLA
                                        .getValue())) {

            ejecutarmensajeValidacion();

            return true;
        }
        return false;
    }

    /**
     * Comando remoto que se ejecuta al evaluar un registro
     * seleccinado
     */
    public void ejecutarmensajeValidacion() {
        JsfUtil.agregarMensajeError(idioma.getString("TB_TB3945")
                        .replace("s$plantilla$s", registroAux.getCampos()
                                        .get(FrmConsultasControladorEnum.NOMBRE_PLANTILLA
                                                        .getValue())
                                        .toString()));
    }

    /**
     * Metodo que valida si el combo de ano, mes y periodo estan
     * vacios
     * 
     * @return
     */
    public boolean validarCombos() {
        if (SysmanFunciones.validarVariableVacio(ano)
            || SysmanFunciones.validarVariableVacio(mes)
            || SysmanFunciones.validarVariableVacio(periodo)) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3937"));
            return true;

        }
        return false;
    }

    /**
     * Metodo que retorna el formato del nit de la entidad segun la
     * opcion seleccionada
     * 
     * @param opcion
     * @return
     */
    private String ejecutarFCMINIT(int opcion) {
        String rta = null;
        try {
            rta = ejbSysmanUtil.formatearNitEntidad(compania, opcion);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rta;
    }

    /**
     * Metodo que devuelve la ruta de la imagen almacenada
     * 
     * @param imagen
     * @return
     */
    public String obtenerRuta(String imagen) {
        String imagenRuta = null;
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            Registro ruta = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmConsultasControladorUrlEnum.URL647
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
            String registroRuta = ruta.getCampos().get("RUTA_IMAGEN")
                            .toString();
            imagenRuta = SysmanFunciones.concatenar(registroRuta.substring(0,
                            registroRuta.lastIndexOf(File.separator) + 1),
                            imagen);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return imagenRuta;
    }

    /**
     * Metodo que carga los parametros para el informe DIAN
     * 
     * @param parametros
     */
    private void cargarParametros(HashMap<String, Object> parametros) {
        parametros.put("PR_CODIGO_CIUDAD", codigoCiudad);
        parametros.put("PR_CODIGO_DEPARTAMENTO",
                        codigoDepartamento);
        parametros.put("PR_CIUDAD_RETENCION", nombreCiudad);
        parametros.put("PR_RAZON_SOCIAL_RETENEDOR", nombreCompania);
        parametros.put("PR_DV_RETENEDOR",
                        SysmanFunciones.extraerDigitoVerificacion(
                                        nit));

        parametros.put("PR_NIT_RETENEDOR",
                        SysmanFunciones.extraerNIT(nit));
        parametros.put("PR_ANO_GRAVABLE", ano);
        parametros.put("PR_NUMERO_FORMATO",
                        obtenerRuta("Formato220.jpg"));
        parametros.put("PR_LOGO_DIAN", obtenerRuta("DIAN2013.jpg"));
        parametros.put("PR_FORMS_CERTIFICADOS_ORIGINAL",
                        "Original: Empleado");
        parametros.put("PR_VALOR_INGRESOS_PATRIMONIOS_PARA_CERTIFICADO",
                        parValorIngresos);

        parametros.put("PR_VALOR_INGRESS_PATRIMONIOS_PARA_CERTIFICADO_UVT",
                        parValorIngress);

        parametros.put("PR_VALOR_INGRESOS_SUPERIORES_PARA_CERTIFICADO",
                        parValorSuperiores);

        parametros.put("PR_VALOR_INGRESS_SUPERIORES_PARA_CERTIFICADO_UVT",
                        parValorIngressSup);

        parametros.put("PR_VALOR_CONSUMOS_CON_TARJETAS_PARA_CERTIFICADO",
                        parValorConsumos);

        parametros.put("PR_VALOR_CONSUMS_CON_TARJ_PARA_CERTIFICADO_UVT",
                        parValorConsums);

        parametros.put("PR_VALOR_TOTAL_COMPRAS_Y_CONSUMOS_PARA_CERTIFICADO",
                        parValorTotal);

        parametros.put("PR_VALOR_TOTAL_COMPRAS_Y_CON_PARA_CERTIFICADO_UVT",
                        parValorTotalCom);

        parametros.put("PR_VALOR_CONSIGNACIONES_BANCARIAS_PARA_CERTIFICADO",
                        parValorConsignaciones);

        parametros.put("PR_VALOR_CONSIG_BANCARIAS_PARA_CERTIFICADO_UVT",
                        parValorConsig);

        parametros.put("PR_CEDULA_DE_QUIEN_FIRMA_CERTIFICADO_DIAN",
                        parCedula);

        parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_CERTIFICADO_DIAN",
                        parNombre);

        parametros.put("PR_MONEDA_VALOR_INGRESS_PATRIMONIOS_PARA_CERTIFICADO_UVT",
                        valorNeto(parValorIngress));

        parametros.put("PR_MONEDA_VALOR_INGRESS_SUPERIORES_PARA_CERTIFICADO_UVT",
                        valorNeto(parValorIngressSup));

        parametros.put("PR_MONEDA_VALOR_CONSUMS_CON_TARJ_PARA_CERTIFICADO_UVT",
                        valorNeto(parValorConsums));

        parametros.put("PR_MONEDA_VALOR_TOTAL_COMPRAS_Y_CON_PARA_CERTIFICADO_UVT",
                        valorNeto(parValorTotalCom));

        parametros.put("PR_MONEDA_VALOR_CONSIG_BANCARIAS_PARA_CERTIFICADO_UVT",
                        valorNeto(parValorConsig));
    }

    /**
     * Metodo que carga el valor de los parametros
     */
    private void iniciarParametros() {
        String nomPar;
        // Parametro
        nomPar = "VALOR INGRESOS PATRIMONIOS PARA CERTIFICADO";
        parValorIngresos = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR INGRESS PATRIMONIOS PARA CERTIFICADO UVT";
        parValorIngress = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR INGRESOS SUPERIORES PARA CERTIFICADO";
        parValorSuperiores = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR INGRESS SUPERIORES PARA CERTIFICADO UVT";
        parValorIngressSup = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR CONSUMOS CON TARJETAS PARA CERTIFICADO";
        parValorConsumos = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR CONSUMS CON TARJ PARA CERTIFICADO UVT";
        parValorConsums = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR TOTAL COMPRAS Y CONSUMOS PARA CERTIFICADO";
        parValorTotal = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR TOTAL COMPRAS Y CON PARA CERTIFICADO UVT";
        parValorTotalCom = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR CONSIGNACIONES BANCARIAS PARA CERTIFICADO";
        parValorConsignaciones = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "VALOR CONSIG BANCARIAS PARA CERTIFICADO UVT";
        parValorConsig = consultarParametro(nomPar, true);

        // Parametro
        nomPar = "CEDULA DE QUIEN FIRMA CERTIFICADO DIAN";
        parCedula = consultarParametro(nomPar, false);

        validarParametro(parCedula, nomPar);

        // Parametro
        nomPar = "NOMBRE DE QUIEN FIRMA CERTIFICADO DIAN";
        parNombre = consultarParametro(nomPar, false);
    }

    /**
     * Metodo que devuelve el valor del parametro ingresado por
     * parametro
     * 
     * @param nomPar
     * @return
     */
    public String consultarParametro(String nomPar, boolean validar) {
        String valor;
        Date fecha = null;
        try {
            fecha = SysmanFunciones
                            .convertirAFecha(
                                            SysmanFunciones.concatenar(fecha2));
        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        valor = "1";
        try {
            if (validar) {
                valor = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                nomPar, SessionUtil.getModulo(),
                                                fecha, true),
                                "0");
            }
            else {
                valor = ejbSysmanUtil.consultarParametro(compania,
                                nomPar, SessionUtil.getModulo(), fecha, true);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return valor;

    }

    /**
     * Metodo que valida si el el parametro recibido por parametro
     * viene nulo o no
     * 
     * @param valor
     * @param nombre
     */
    private void validarParametro(String valor, String nombre) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2995").replace("#PAR#",
                                            nombre));
        }
    }

    /**
     * Metodo que valida si el se puede cargar el formulario o no
     * 
     * @throws SysmanException
     */
    private void validarEntrada() {
        try {

            if (cedula == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4042"));
                inactivarBoton = true;
                return;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            cedula);

            Registro rsIdEmpleado = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmConsultasControladorUrlEnum.URL288
                                                                            .getValue())
                                            .getUrl(), param));

            if (!SysmanFunciones.validarCampoVacio(rsIdEmpleado.getCampos(),
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName())) {
                idEmpleados = rsIdEmpleado.getCampos().get(
                                GeneralParameterEnum.ID_DE_EMPLEADO.getName())
                                .toString();
                inactivarBoton = false;
            }
            else {
                inactivarBoton = true;
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB3933").replace(
                                                "s$cedula$s",
                                                cedula));

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que devuelve el valor recibo en letras
     * 
     * @param valor
     * @return
     */
    private String valorNeto(String valor) {
        String valorretorno = null;
        try {
            valorretorno = ejbSysmanUtil.convetirValorEnLetras(
                            new BigDecimal(valor), false);
            valorretorno = valorretorno.replace("  CTVS.MC", "");
            valorretorno = valorretorno.replace(" PESOS MC", "");
            valorretorno = valorretorno.replace("PESOS CON", "CON");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return valorretorno;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable inactivarBoton
     * 
     * @return inactivarBoton
     */
    public boolean isInactivarBoton() {
        return inactivarBoton;
    }

    /**
     * Asigna la variable inactivarBoton
     * 
     * @param inactivarBoton
     * Variable a asignar en inactivarBoton
     */
    public void setInactivarBoton(boolean inactivarBoton) {
        this.inactivarBoton = inactivarBoton;
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
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
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

    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
