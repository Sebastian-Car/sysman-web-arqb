package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.PagosdoblesControladorEnum;
import com.sysman.predial.enums.PagosdoblesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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

/**
 *
 * @author NGOMEZ
 * @version 1, 27/05/2016
 * 
 * @author asana
 * @version 2, 13/06/2017 Se implementa enum en formulario, a demas se
 * ajusta conexion.
 * 
 * @version 3.0, 07/07/2017, <strong>pespitia</strong>:<br>
 * Manejo de EJBs.<br>
 * Refactoring.
 */
@ManagedBean
@ViewScoped
public class PagosdoblesControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el numero de orden predial
     */
    private final String nOrden;

    private final String codigo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * {@code GeneralParameterEnum.VALOR}
     */
    private final String cValor;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * {@code PagosdoblesControladorEnum.MI_COMPANIA}
     */
    private final String cMiCompania;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * {@code PagosdoblesControladorEnum.MI_TIPO}
     */
    private final String cMiTipo;

    private int indice;
    // <DECLARAR_ATRIBUTOS>
    private String propietarioAux;
    private String nOrdenAux;
    private String pagoAnioAux;
    private boolean cuadroVisible;
    private String tituloCuadro;
    private int nuevovalor;
    private boolean activo;
    private final String propietario;
    private final String numeroOrden;
    private final String precod;
    private final String pagoAnoAux;
    private final String anoExcedente;
    private final String pagoAno;
    private final String modulo;

    /**
     * Indicador que determina si se debe actualizar el registro que
     * esta siendo editado.
     */
    private boolean keyActualiza;

    /**
     * Array que contiene el valor de las etiquetas que identifican
     * los campos de los conceptos.
     */
    private String[] conceptosNom;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaPreAno;
    private List<Registro> listaTexto102;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaPrecod;
    private RegistroDataModelImpl listaPrecodE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR_EJBs>
    /**
     * Instancia que permite utilizar las funciones y procedimientos
     * del paquete: <code>PCK_PREDIAL_COM8</code>
     */
    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;
    // </DECLARAR_EJBs>

    /**
     * Creates a new instance of PagosdoblesControlador
     */
    public PagosdoblesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;

        cValor = GeneralParameterEnum.VALOR.getName();
        cMiCompania = PagosdoblesControladorEnum.MI_COMPANIA.getValue();
        cMiTipo = PagosdoblesControladorEnum.MI_TIPO.getValue();

        propietario = "PROPIETARIO";
        numeroOrden = "NUMERO_ORDEN";
        precod = "PRECOD";
        pagoAnoAux = "PAGO_ANO_AUX";
        anoExcedente = "ANO_EXCEDENTE";
        pagoAno = "PAGO_ANO";
        codigo = "CODIGO";
        nuevovalor = 0;

        conceptosNom = new String[21];

        cargarEtiquetasConceptos();

        try {
            numFormulario = GeneralCodigoFormaEnum.PAGOSDOBLES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PagosdoblesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_PAGOSDOBLES;

        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaPreAno();
        cargarListaTexto102();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaPrecod();
        cargarListaPrecodE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        activo = false;

        registro.getCampos().put("VALOR", 0);

        iniciarConceptosCero(1, 4);
        iniciarConceptosCero(13, 20);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cMiCompania, compania);
        parametrosListado.put(cMiTipo, "D");
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaPreAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaPreAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagosdoblesControladorUrlEnum.URL7801
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTexto102() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTexto102 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagosdoblesControladorUrlEnum.URL7801
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPrecod() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PagosdoblesControladorUrlEnum.URL8548
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);

        listaPrecod = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaPrecodE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PagosdoblesControladorUrlEnum.URL8548
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);

        listaPrecodE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton cargarExcedentes en la
     * vista
     *
     */
    public void oprimircargarExcedentes() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CARGAR_EXCEDENTES_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarPrecodC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(propietario, propietarioAux);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(numeroOrden, nOrdenAux);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(pagoAnoAux, pagoAnioAux);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTexto102() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(pagoAnoAux) != null) {
            if (Integer.parseInt(registro.getCampos().get(anoExcedente)
                            .toString()) > (Integer.parseInt(
                                            registro.getCampos().get(pagoAnoAux)
                                                            .toString())
                                + 1)) {
                tituloCuadro = idioma.getString("TB_TB187");
                cuadroVisible = true;
            }
            else if (Integer.parseInt(registro.getCampos().get(anoExcedente)
                            .toString()) <= Integer.parseInt(
                                            registro.getCampos().get(pagoAnoAux)
                                                            .toString())) {
                tituloCuadro = idioma.getString("TB_TB188");
                cuadroVisible = true;
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValor() {
        nuevovalor = Integer.parseInt(SysmanFunciones
                        .nvlStr((String) registro.getCampos().get("C1"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C2"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C3"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C4"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C13"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C14"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C15"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C16"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C17"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C18"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C19"), "0"))
            + Integer.parseInt(SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("C20"), "0"));

        registro.getCampos().put(cValor, Integer.toString(nuevovalor));
    }

    public void cambiarc1() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarc2() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c3
     * 
     */
    public void cambiarc3() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c4
     * 
     */
    public void cambiarc4() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c13
     * 
     */
    public void cambiarc13() {
        // <CODIGO_DESARROLLADO>
        String numero = SysmanFunciones
                        .nvl(registro.getCampos().get("C13"), "0").toString();

        if (validarNumeroNegativo(numero)) {
            cambiarValor();
        }
        else {
            registro.getCampos().put("C13", 0);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c14
     * 
     */
    public void cambiarc14() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c15
     * 
     */
    public void cambiarc15() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c16
     * 
     */
    public void cambiarc16() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c17
     * 
     */
    public void cambiarc17() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c18
     * 
     */
    public void cambiarc18() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c19
     * 
     */
    public void cambiarc19() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c20
     * 
     */
    public void cambiarc20() {
        // <CODIGO_DESARROLLADO>
        cambiarValor();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTexto102C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (pagoAnioAux != null) {
            if (Integer.parseInt(listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos().get(anoExcedente)
                            .toString()) > (Integer.parseInt(
                                            pagoAnioAux)
                                + 1)) {
                tituloCuadro = idioma.getString("TB_TB187");
            }
            else if (Integer.parseInt(listaInicial.getDatasource()
                            .get(rowNum % 10).getCampos().get(anoExcedente)
                            .toString()) <= Integer.parseInt(
                                            pagoAnioAux)) {
                tituloCuadro = idioma.getString("TB_TB188");
            }

            cuadroVisible = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCuadro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c1 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Impuesto Predial Presente Anio</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc1C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c2 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Intereses Predial Presente Anio</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc2C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c3 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Impuesto CAR Presente Anio</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc3C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c4 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Intereses CAR Presente Anio</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc4C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c13 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Descuento predial</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc13C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (validarNumeroNegativo(rowNum, "C13")) {
            calcularValor(rowNum);
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarNumeroNegativo(int rowNum, String concepto) {
        String numero = SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get(concepto), "0")
                        .toString();

        if ("0".equals(numero)) {
            return true;
        }

        if (numero.lastIndexOf('-') != 0 || Integer.parseInt(numero) > 0) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put("C13",
                            0);
            return false;
        }

        return true;
    }

    private boolean validarNumeroNegativo(String numero) {
        if ("0".equals(numero)) {
            return true;
        }

        if (numero.lastIndexOf('-') != 0) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB189"));
            return false;
        }

        if (Integer.parseInt(numero) > 0) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB190"));
            return false;
        }

        return true;
    }

    /**
     * Metodo ejecutado al cambiar el control c14 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Arborización</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc14C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c15 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Sobretasa Ambiental</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc15C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c16 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Bomberos</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc16C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c17 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Saldos y Devoluciones</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc17C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c18 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Alumbrado</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc18C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c19 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Concepto 19</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc19C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control c20 en la fila
     * seleccionada dentro de la grilla. Asociado al campo:
     * <code>Concepto 20</code>
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarc20C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        calcularValor(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Calcula el valor total resultante de la sumatoria de los
     * conceptos. <br>
     * {C1 + C2 + C3 + C4 + C13 + C14 + C15 + C16 + C17 + C18 + C19 +
     * C20}
     * 
     * @param rowNum
     * Indice del registro.
     */
    private void calcularValor(int rowNum) {
        String[] campos = { "C1", "C2", "C3", "C4", "C13", "C14", "C15", "C16",
                            "C17", "C18", "C19", "C20" };

        int total = 0;

        for (String c : campos) {
            int valorC = Integer.parseInt(SysmanFunciones
                            .nvl(listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos().get(c), "0")
                            .toString());

            total += valorC;
        }

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cValor,
                        total);
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaPrecod(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos()
                        .put(precod, SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get(codigo), "")
                                        .toString());

        registro.getCampos()
                        .put(propietario, SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get("NOMBRE"), "")
                                        .toString());

        registro.getCampos()
                        .put(pagoAnoAux, SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get(pagoAno), "")
                                        .toString());

        registro.getCampos().put(numeroOrden, nOrden);
    }

    public void seleccionarFilaPrecodE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(codigo, "").toString();

        propietarioAux = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        pagoAnioAux = SysmanFunciones
                        .nvl(registroAux.getCampos().get(pagoAno), "")
                        .toString();

        nOrdenAux = nOrden;
    }

    // </METODOS_COMBOS_GRANDES>

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
        activo = false;
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove(propietario);
        registro.getCampos().remove(pagoAnoAux);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("TIPO", "D");
        registro.getCampos().remove(propietario);
        registro.getCampos().remove(pagoAnoAux);

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.OBSERVACIONES.getName())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4331"));
            return false;
        }

        try {
            ejbPredialOcho.actualizarAntesPagosDobles(
                            compania, SysmanFunciones
                                            .nvl(registro.getCampos()
                                                            .get(cValor), "0")
                                            .toString(),
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get(precod),
                                            "").toString(),
                            registro.getCampos().get(numeroOrden)
                                            .toString(),
                            Integer.parseInt(SysmanFunciones
                                            .nvl(registro.getCampos().get(
                                                            anoExcedente), 0)
                                            .toString()));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }

        if (keyActualiza) {
            registro.getCampos().remove("PRECOD");

            registro.getCampos().remove(
                            GeneralParameterEnum.NUMERO_ORDEN.getName());

            registro.getCampos()
                            .remove(GeneralParameterEnum.PREANO.getName());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        activo = false;
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void aceptarCuadro() {
        // <CODIGO_DESARROLLADO>
        if (activo) {
            listaInicial.getDatasource().get(indice).getCampos().put(
                            anoExcedente, Integer.parseInt(pagoAnioAux) + 1);
        }
        else {
            registro.getCampos().put(anoExcedente, Integer.parseInt(
                            registro.getCampos().get(pagoAnoAux).toString())
                + 1);
        }

        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarCuadro() {
        // <CODIGO_DESARROLLADO>
        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        keyActualiza = true;

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DOCNUM.getName());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("VALOR", 0);

        iniciarConceptosCero(1, 4);
        iniciarConceptosCero(13, 20);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Carga el valor de las etiquetas asociadas a los campos de los
     * conceptos.
     */
    private void cargarEtiquetasConceptos() {
        int anioActual = SysmanFunciones.ano(new Date());

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), this.compania);
        params.put(GeneralParameterEnum.ANO.getName(), anioActual);

        try {
            List<Registro> list = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagosdoblesControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));

            if (list != null) {
                for (Registro r : list) {
                    int clave = (int) r.getCampos()
                                    .get(GeneralParameterEnum.CODIGO.getName());

                    this.conceptosNom[clave] = r.getCampos()
                                    .get(GeneralParameterEnum.NOMBRE.getName())
                                    .toString();
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB4330").replace("#ANO#",
                                                Integer.toString(anioActual)));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Inicializa el cero valor de los conceptos desde un limite
     * inferior hasta un limite superior.
     * 
     * @param cInicial
     * -> Limite inferior.
     * @param cFinal
     * -> Limite superior.
     */
    private void iniciarConceptosCero(int cInicial, int cFinal) {
        for (int i = cInicial; i <= cFinal; i++) {
            registro.getCampos().put("C".concat(Integer.toString(i)), "0");
        }
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getPropietarioAux() {
        return propietarioAux;
    }

    public void setPropietarioAux(String propietarioAux) {
        this.propietarioAux = propietarioAux;
    }

    public String getnOrdenAux() {
        return nOrdenAux;
    }

    public void setnOrdenAux(String nOrdenAux) {
        this.nOrdenAux = nOrdenAux;
    }

    public String getPagoAnioAux() {
        return pagoAnioAux;
    }

    public void setPagoAnioAux(String pagoAnioAux) {
        this.pagoAnioAux = pagoAnioAux;
    }

    public boolean isCuadroVisible() {
        return cuadroVisible;
    }

    public void setCuadroVisible(boolean cuadroVisible) {
        this.cuadroVisible = cuadroVisible;
    }

    public String getTituloCuadro() {
        return tituloCuadro;
    }

    public void setTituloCuadro(String tituloCuadro) {
        this.tituloCuadro = tituloCuadro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaPreAno() {
        return listaPreAno;
    }

    public void setListaPreAno(List<Registro> listaPreAno) {
        this.listaPreAno = listaPreAno;
    }

    public List<Registro> getListaTexto102() {
        return listaTexto102;
    }

    public void setListaTexto102(List<Registro> listaTexto102) {
        this.listaTexto102 = listaTexto102;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaPrecod() {
        return listaPrecod;
    }

    public void setListaPrecod(RegistroDataModelImpl listaPrecod) {
        this.listaPrecod = listaPrecod;
    }

    public RegistroDataModelImpl getListaPrecodE() {
        return listaPrecodE;
    }

    public void setListaPrecodE(RegistroDataModelImpl listaPrecodE) {
        this.listaPrecodE = listaPrecodE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        Registro regAux = null;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(precod).toString());

        try {
            regAux = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagosdoblesControladorUrlEnum.URL8174
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        pagoAnioAux = regAux != null
            ? regAux.getCampos().get(pagoAno).toString()
            : "";

        activo = true;
    }

    public String[] getConceptosNom() {
        return conceptosNom;
    }

    public void setConceptosNom(String[] conceptosNom) {
        this.conceptosNom = conceptosNom;
    }
}
