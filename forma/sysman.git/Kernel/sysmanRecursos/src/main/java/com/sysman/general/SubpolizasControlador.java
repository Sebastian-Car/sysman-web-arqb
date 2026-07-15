package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SubpolizasControladorEnum;
import com.sysman.general.enums.SubpolizasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author jrodriguezr
 * @version 1.0, 17/12/2015
 *
 * @author spina
 * @version 2.0, 05/04/2017 - se realiza refactoring para dss y
 * depuracion sonar, se bloquea el campo nro del consecutivo al editar
 * y se modifica el campo a tipo numerico simple
 *
 * @author ybecerra
 * @version 3.0, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 *
 * @author jrodrigueza
 * @version 4.0, 09/10/2017 Adici&oacute;n de lista para seleccionar
 * la plantilla de impresi&oacute;n.
 */
@ManagedBean
@ViewScoped
public class SubpolizasControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private List<Registro> listaTipo;
    private List<Registro> listaAseguradora;
    private String claseOrden;
    private String numeroOrden;
    private String fechaFirma;
    private String valorContrato;
    private int indice;
    private int ano;
    private String unidad;
    private static final String FORMATO_FECHA = "dd/MM/yyyy";
    private static final String CAMPO_ORDENDECOMPRA = "ORDENDECOMPRA";
    private static final String CAMPO_FECHA_EXPEDICION = "FECHAEXPEDICION";
    private static final String TXT_REQUIERE_FECHA_FIRMA = "TB_TB2099";
    private static final String CAMPO_VIGENCIA_DESDE = "VIGENCIADESDE";
    private static final String CAMPO_VIGENCIA_HASTA = "VIGENCIAHASTA";
    private static final String CAMPO_SUCURSAL = GeneralParameterEnum.SUCURSAL
                    .getName();
    private static final String CAMPO_VALORASEGURADO = "VALORASEGURADO";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Tipo de plantilla configurado para la impresi&oacute;n de
     * p&oacute;lizas.
     */
    private static final String TIPO_FORMATO_POLIZAS = "10";

    /**
     * Plantilla de impresi&oacute;n seleccionada en el encabezado del
     * formulario.
     */
    private String plantilla;

    /**
     * Lista de plantillas configuradas con seg&uacute;n el tipo.
     */
    private RegistroDataModelImpl listaPlantillaImpresion;

    /**
     * Par&aacute;metros enviados desde el formulario de Contratos.
     */
    private Map<String, Object> parametrosEntrada;

    /**
     * Cadena que representa la fecha de creaci&oacute;n del modelo de
     * plantilla.
     */
    private String fechaPlantilla;

    /**
     * C&oacute;digo de la poliza seleccionada en el momento de hacer
     * clic en el boton Imprimir.
     */
    private String codigoPoliza;

    /**
     * Consecutivo de la poliza seleccionada en el momento de hacer
     * clic en el boton Imprimir.
     */
    private String consecutivo;
	private double valorMinimo;
	private boolean valorUnidad;
	private String modulo;
	private Object parametroswf;

    /**
     * Creates a new instance of SubpolizasControlador
     */
    public SubpolizasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
            modulo = SessionUtil.getModulo();
            
            numFormulario = GeneralCodigoFormaEnum.SUBPOLIZAS_CONTROLADOR
                            .getCodigo();
            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                claseOrden = SysmanFunciones
                                .nvl(parametrosEntrada.get("claseF"), "")
                                .toString();
                numeroOrden = SysmanFunciones
                                .nvl(parametrosEntrada.get("numeroOrden"), "")
                                .toString();
                fechaFirma = SysmanFunciones
                                .nvl(parametrosEntrada.get("fechaFirma"), "")
                                .toString();
                valorContrato = SysmanFunciones
                                .nvl(parametrosEntrada.get("valorContrato"), "")
                                .toString();
                parametrosEntrada.remove("fechaFirma");
            }
            validarPermisos();
        }
        catch (SysmanException | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.POLIZAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaTipo();
        cargarListaAseguradora();
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaPlantillaImpresion();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        parametrosListado.put("ORDENDECOMPRA",
                        numeroOrden);
    }

    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    public List<Registro> getListaAseguradora() {
        return listaAseguradora;
    }

    public void setListaAseguradora(List<Registro> listaAseguradora) {
        this.listaAseguradora = listaAseguradora;
    }

    public void cargarListaTipo() {
        try {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpolizasControladorUrlEnum.URL3689
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAseguradora() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAseguradora = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpolizasControladorUrlEnum.URL3968
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaPlantillaImpresion
     */
    public void cargarListaPlantillaImpresion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(SubpolizasControladorEnum.TIPO.getValue(),
                        TIPO_FORMATO_POLIZAS);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpolizasControladorUrlEnum.URL20274
                                                        .getValue());
        listaPlantillaImpresion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SubpolizasControladorEnum.LLAVE.getValue());
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlantillaImpresion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlantillaImpresion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        plantilla = extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        fechaPlantilla = extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName()));
    }

    /**
     * Metodo ejecutado al oprimir el boton ImprimirPolizas
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirImprimirPolizas(Registro reg, int indice) {
        if (SysmanFunciones.validarVariableVacio(plantilla)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3700"));
            return;
        }

        Map<String, Object> poliza = reg.getCampos();
        codigoPoliza = extraerString(
                        poliza.get(GeneralParameterEnum.CODIGO.getName()));
        consecutivo = extraerString(
                        poliza.get(GeneralParameterEnum.CONSECUTIVO.getName()));

        Map<String, Object> param = new HashMap<>();
        param.put(SubpolizasControladorEnum.TIPO.getValue(),
                        TIPO_FORMATO_POLIZAS);
        String strNombreDocumento = "Poliza No. " + codigoPoliza;
        String[] campos = new String[3];
        String[] valores = new String[3];
        campos[0] = "codigoPlantilla";
        campos[1] = "fechaPlantilla";
        campos[2] = "nombreDocDescarga";
        String fechaFormato = null;
        try {
            fechaFormato = SysmanFunciones.formatearFecha(SysmanFunciones
                            .convertirAFecha(fechaPlantilla, FORMATO_FECHA));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        valores[0] = plantilla;
        valores[1] = fechaFormato;
        valores[2] = strNombreDocumento;

        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$compania$s", "'" + compania + "'");
        variablesConsultaW.put("s$claseOrden$s",
                        "'" + claseOrden + "'");
        variablesConsultaW.put("s$numeroOrden$s", "'" + numeroOrden + "'");
        SessionUtil.setSessionVar("variablesConsultaWord",
                        variablesConsultaW);
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaExpedicion() {
        // <CODIGO_DESARROLLADO>
        Date fechaExpedicion = (Date) registro.getCampos()
                        .get(CAMPO_FECHA_EXPEDICION);
        if (fechaExpedicion == null) {
            return;
        }
        if (fechaFirma == null || fechaFirma == "" ) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(TXT_REQUIERE_FECHA_FIRMA));
            registro.getCampos().put(CAMPO_FECHA_EXPEDICION, null);
            JsfUtil.ejecutarJavaScript("PF('dlg').hide()");
            return;
        }
        Date dtTechaFirma = null;
        try {
            dtTechaFirma = SysmanFunciones.convertirAFecha(fechaFirma,
                            FORMATO_FECHA);
        }
        catch (ParseException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (fechaExpedicion.before(dtTechaFirma)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2162"));
            registro.getCampos().put(CAMPO_FECHA_EXPEDICION, null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutaralerta() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), "A");
        if (fechaFirma == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2177"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo invocado al ejecutar el comando remoto ActualizarImpreso
     * en la vista
     */
    public void ejecutarActualizarImpreso() {
        // <CODIGO_DESARROLLADO>
        String urlEnumId = SubpolizasControladorUrlEnum.URL35890.getValue();
        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        fields.put(SubpolizasControladorEnum.INDIMPRESION.getValue(), -1);
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        fields.put(SubpolizasControladorEnum.ORDENDECOMPRA.getValue(),
                        numeroOrden);
        fields.put(GeneralParameterEnum.CODIGO.getName(), codigoPoliza);
        fields.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
        Parameter parameter = new Parameter();
        parameter.setFields(fields);
        try {
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAseguradora() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(CAMPO_SUCURSAL,
                        service.buscarEnLista(
                                        SysmanFunciones
                                                        .nvl(registro.getCampos()
                                                                        .get("ASEGURADORA"),
                                                                        "")
                                                        .toString(),
                                        "NITASEGURADORA",
                                        CAMPO_SUCURSAL, listaAseguradora));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVigenciaDesde() {
        // <CODIGO_DESARROLLADO>
        Date fechaExpedicion = registro.getCampos()
                        .get(CAMPO_VIGENCIA_DESDE) == null
                            ? null
                            : (Date) registro.getCampos()
                                            .get(CAMPO_VIGENCIA_DESDE);
        if (fechaExpedicion == null) {
            return;
        }
        if (fechaFirma == null) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(TXT_REQUIERE_FECHA_FIRMA));
            registro.getCampos().put(CAMPO_VIGENCIA_DESDE, null);
            JsfUtil.ejecutarJavaScript("PF('dlg').hide()");
            return;
        }
        Date dtTechaFirma = null;
        try {
            dtTechaFirma = SysmanFunciones.convertirAFecha(fechaFirma,
                            FORMATO_FECHA);
        }
        catch (ParseException ex) {
            Logger.getLogger(SubpolizasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        if (fechaExpedicion.before(dtTechaFirma)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2179"));
            registro.getCampos().put(CAMPO_VIGENCIA_DESDE, null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVigenciaHasta() {
        // <CODIGO_DESARROLLADO>
        Date fechaVigenciaHasta = registro.getCampos()
                        .get(CAMPO_VIGENCIA_HASTA) == null ? null
                            : (Date) registro.getCampos()
                                            .get(CAMPO_VIGENCIA_HASTA);
        Date fechaVigenciaDesde = registro.getCampos()
                        .get(CAMPO_VIGENCIA_DESDE) == null ? null
                            : (Date) registro.getCampos()
                                            .get(CAMPO_VIGENCIA_DESDE);
        if ((fechaVigenciaHasta != null && fechaVigenciaDesde != null)
            && fechaVigenciaHasta.before(fechaVigenciaDesde)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2181"));
            registro.getCampos().put(CAMPO_VIGENCIA_HASTA, null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPorcPoliza() {
        // <CODIGO_DESARROLLADO>
        String campoPorcPoliza = "PORC_POLIZA";
        if (numeroOrden != null) {
            valorContrato = SysmanFunciones.nvl(valorContrato, "0").toString();
            double valContrato = Double.parseDouble(
                            SysmanFunciones.nvl(valorContrato, "0").toString());
            String valPorcPoliza = SysmanFunciones.nvl(registro.getCampos()
                            .get(campoPorcPoliza), "0").toString();
            double valorPorcPoliza = Double.parseDouble(valPorcPoliza);
            if(unidad.equals("P")) {
            registro.getCampos().put(CAMPO_VALORASEGURADO,
                            (valContrato * valorPorcPoliza) / 100);
            }
        }
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarNsalarios() {
    	String campoSalario = "N_SALARIOS";
        if (numeroOrden != null) {
            valorContrato = SysmanFunciones.nvl(valorContrato, "0").toString();
            double valContrato = Double.parseDouble(
                            SysmanFunciones.nvl(valorContrato, "0").toString());
          //  double valContrato = Double.parseDouble(
               //             SysmanFunciones.nvl(valorContrato, "0").toString());
            String valSalario = SysmanFunciones.nvl(registro.getCampos()
                            .get(campoSalario), "0").toString();
            double valorNSalario = Double.parseDouble(valSalario);
            if(unidad.equals("SM")) {
            	//Ticket#7700904   modificamos la formula  valContrato - (valorMinimo * valorNSalario)) a (valorMinimo * valorNSalario)
            registro.getCampos().put(CAMPO_VALORASEGURADO,
                          //  valContrato - (valorMinimo * valorNSalario));
                             (valorMinimo * valorNSalario));
            }
        }

    	
    }
    
    public void cambiarUnidad() {
    	
    	setValorUnidad(unidad.equals("P")?true:false);
    	
    }

    public void cambiarAseguradoraC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().put(CAMPO_SUCURSAL,
                                        service.buscarEnLista(
                                                        SysmanFunciones.nvl(
                                                                        listaInicial
                                                                                        .getDatasource()
                                                                                        .get(rowNum
                                                                                            % 10)
                                                                                        .getCampos()
                                                                                        .get("ASEGURADORA"),
                                                                        "")
                                                                        .toString(),
                                                        "NITASEGURADORA",
                                                        CAMPO_SUCURSAL,
                                                        listaAseguradora));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaExpedicionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        Date fechaExpedicion = (Date) listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get(CAMPO_FECHA_EXPEDICION);
        if (fechaExpedicion == null) {
            return;
        }
        if (fechaFirma == null) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(TXT_REQUIERE_FECHA_FIRMA));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CAMPO_FECHA_EXPEDICION, null);
            return;
        }
        Date dtTechaFirma = null;
        try {
            dtTechaFirma = SysmanFunciones.convertirAFecha(fechaFirma,
                            FORMATO_FECHA);
        }
        catch (ParseException ex) {
            Logger.getLogger(SubpolizasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        if (fechaExpedicion.before(dtTechaFirma)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2162"));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CAMPO_FECHA_EXPEDICION, null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVigenciaDesdeC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        Date fechaVigenciaDesde = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(CAMPO_VIGENCIA_DESDE) == null ? null
                            : (Date) listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get(CAMPO_VIGENCIA_DESDE);
        if (fechaVigenciaDesde == null) {
            return;
        }
        if (fechaFirma == null) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(TXT_REQUIERE_FECHA_FIRMA));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CAMPO_VIGENCIA_DESDE, null);
            return;
        }
        Date dtTechaFirma = null;
        try {
            dtTechaFirma = SysmanFunciones.convertirAFecha(fechaFirma,
                            FORMATO_FECHA);
        }
        catch (ParseException ex) {
            Logger.getLogger(SubpolizasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        if (fechaVigenciaDesde.before(dtTechaFirma)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2179"));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CAMPO_VIGENCIA_DESDE, null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVigenciaHastaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        Date fechaVigenciaHasta = (Date) listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(CAMPO_VIGENCIA_HASTA);
        Date fechaVigenciaDesde = (Date) listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(CAMPO_VIGENCIA_DESDE);
        if ((fechaVigenciaHasta != null && fechaVigenciaDesde != null)
            && (fechaVigenciaHasta.before(fechaVigenciaDesde))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2181"));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CAMPO_VIGENCIA_HASTA, null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPorcPolizaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(CAMPO_ORDENDECOMPRA) != null)
            && (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .get("CODIGO") != null)) {
            String valContrato = SysmanFunciones
                            .nvl(listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get("VALOR_CONTRATO"), "0")
                            .toString();
            double vlrContrato = Double.parseDouble(valContrato);
            String valPorcPoliza = SysmanFunciones
                            .nvl(listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get("PORC_POLIZA"), "0")
                            .toString();
            double valorPorcPoliza = Double.parseDouble(valPorcPoliza);
            if(unidad.equals("P")) {
            registro.getCampos().put(CAMPO_VALORASEGURADO,
                            (vlrContrato * valorPorcPoliza) / 100);
        }
        }
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarNsalariosC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(CAMPO_ORDENDECOMPRA) != null)
            && (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .get("CODIGO") != null)) {
            String valContrato = SysmanFunciones
                            .nvl(listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get("VALOR_CONTRATO"), "0")
                            .toString();
            double vlrContrato = Double.parseDouble(valContrato);
            String valSalirio = SysmanFunciones
                            .nvl(listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get("N_SALARIOS"), "0")
                            .toString();
            double valorPorcPoliza = Double.parseDouble(valSalirio);
            if(unidad.equals("SM")) {
            registro.getCampos().put(CAMPO_VALORASEGURADO,
                            vlrContrato - (valorPorcPoliza * valorMinimo));
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    // variable
    // necesaria en el
    // llamado de la
    // vista
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
    }

    @Override
    public void abrirFormulario() {
    	// <CODIGO_DESARROLLADO>
        
        Date Fecha = null;
        
        try {
            Fecha = SysmanFunciones.convertirAFecha(fechaFirma,
                            FORMATO_FECHA);
            ano = SysmanFunciones.ano(Fecha);
                            
            
        }
        catch (ParseException ex) {
            Logger.getLogger(SubpolizasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
            
            Registro reg;

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);   
    	
        try {
            
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpolizasControladorUrlEnum.URL35891
                                                                            .getValue())
                                            .getUrl(), param));
            
            valorMinimo = (double) reg.getCampos().get("SALARIOMINIMO");
    	    
    	        	} catch (SystemException e) {
    		Logger.getLogger(SubpolizasControlador.class.getName())
    		.log(Level.SEVERE, null, e);
    	
        }
    	// </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        registro.getCampos().put(CAMPO_ORDENDECOMPRA, numeroOrden);

        try {
            String campoConsecutivo = "CONSECUTIVO";
            registro.getCampos().put(campoConsecutivo,
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            "POLIZAS",
                                            "COMPANIA = ''" + compania
                                                + "'' AND CLASEORDEN=''"
                                                + claseOrden
                                                + "'' AND ORDENDECOMPRA="
                                                + numeroOrden,
                                            campoConsecutivo, "1"));
        }
        catch (SystemException ex) {
            Logger.getLogger(SubpolizasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        return true;
    }

    @Override
    public boolean insertarDespues() {
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), "A");
        String unidadD = unidad;
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        if (registro.getCampos().get(CAMPO_VALORASEGURADO) == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3074"));
            return false;
        }
        registro.getCampos().remove("TIPODESC");
        registro.getCampos().remove("NOMBREASEGURADORA");
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
    	unidad = null;
        // </CODIGO_DESARROLLADO>
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * Retorna la variable plantilla
     * 
     * @return plantilla
     */
    public String getPlantilla() {
        return plantilla;
    }

    /**
     * Asigna la variable plantilla
     * 
     * @param plantilla
     * Variable a asignar en plantilla
     */
    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    /**
     * Retorna la lista listaPlantillaImpresion
     * 
     * @return listaPlantillaImpresion
     */
    public RegistroDataModelImpl getListaPlantillaImpresion() {
        return listaPlantillaImpresion;
    }

    /**
     * Asigna la lista listaPlantillaImpresion
     * 
     * @param listaPlantillaImpresion
     * Variable a asignar en listaPlantillaImpresion
     */
    public void setListaPlantillaImpresion(
        RegistroDataModelImpl listaPlantillaImpresion) {
        this.listaPlantillaImpresion = listaPlantillaImpresion;
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametrosEntrada);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PCONTRATOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    /**
     * Metodo ejecutado al cambiar el control indimpresion en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarindimpresionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

	/**
	 * @return the unidad
	 */
	public String getUnidad() {
		return unidad;
	}

	/**
	 * @param unidad the unidad to set
	 */
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	/**
	 * @return the valorUnidad
	 */
	public boolean isValorUnidad() {
		return valorUnidad;
	}

	/**
	 * @param valorUnidad the valorUnidad to set
	 */
	public void setValorUnidad(boolean valorUnidad) {
		this.valorUnidad = valorUnidad;
	}
	
}