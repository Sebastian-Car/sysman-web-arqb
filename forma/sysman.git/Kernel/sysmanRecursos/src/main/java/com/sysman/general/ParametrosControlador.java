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
import com.sysman.general.enums.ParametrosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author lcortes
 * @version 1, 19/11/2015
 * 
 * @version 1.1, 05/04/2017, pespitia:<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class ParametrosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena {@code VALOR}
     */
    private final String cValor;

    /**
     * Constante a nivel de clase que aloja la cadena
     * {@code TIPOPARAMETRO}
     */
    private final String cTipoParametro;

    /**
     * Constante a nivel de clase que aloja la cadena {@code NOMBRE}
     */
    private final String cNombre;
    
    private String cerrar;

    private List<Registro> listaTipoParametro;
    private List<Registro> listaAplicacion;
    
    private Map<String, Object> ridDatos;
    private Map<String, Object> parametrosEntrada;

    /**
     * Creates a new instance of ParametrosControlador
     */
    public ParametrosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cValor = "VALOR";
        cTipoParametro = "TIPOPARAMETRO";
        cNombre = "NOMBRE";
        //Modificacion Daniel Niño
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
        	cerrar = (String) parametrosEntrada.get("cerrar"); 
        }
        //Fin Modificacion
        try {
            numFormulario = GeneralCodigoFormaEnum.PARAMETROS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            SessionUtil.cleanFlash();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PARAMETROS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaTipoParametro();
        cargarListaAplicacion();
        abrirFormulario();
        try {
            registro.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            SysmanFunciones.convertirAFechaCadena(
                                            new Date()));
            registro.getCampos().put("FECHA_INICIAL", new Date());
        }
        catch (ParseException ex) {
            Logger.getLogger(ParametrosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        if("1".equals(cerrar)) {
        	urlListado = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID("67017");
        }
        
        
    }

    public List<Registro> getListaTipoParametro() {
        return listaTipoParametro;
    }

    public void setListaTipoParametro(List<Registro> listaTipoParametro) {
        this.listaTipoParametro = listaTipoParametro;
    }

    public List<Registro> getListaAplicacion() {
        return listaAplicacion;
    }

    public void setListaAplicacion(List<Registro> listaAplicacion) {
        this.listaAplicacion = listaAplicacion;
    }

    public void cargarListaTipoParametro() {
        try {
            listaTipoParametro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosControladorUrlEnum.URL3391
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAplicacion() {
        try {
            listaAplicacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosControladorUrlEnum.URL3856
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Valida que el valor ingresado en el parametro {@code valor} sea
     * un numero tipo flotante.
     * 
     * @param valor
     * @return
     */
    private boolean validarValorDouble(String valor) {
        try {
            double mValor = Double.parseDouble(valor.contains(",")
                ? valor.replace(",", ".") : valor);
            registro.getCampos().put(cValor, mValor);
        }
        catch (NumberFormatException nfex) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB545"));
            return false;
        }

        return true;
    }

    /**
     * Valida que el valor ingresado en el parametro {@code valor} sea
     * un numero entero.
     * 
     * @param valor
     * @return
     */
    private boolean validarValorInt(String valor) {
        try {
            Integer.parseInt(valor);
        }
        catch (NumberFormatException nfe) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB546"));
            return false;
        }

        return true;
    }

    private boolean validarValorFecha(String valor) {
        if (!SysmanFunciones.validarFecha(valor)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB547"));
            return false;
        }
        registro.getCampos().put(cValor, valor);
        return true;
    }

    /**
     * Valida que el valor ingresado en el parametro {@code valor} sea
     * {@code SI} o {@code NO}
     * 
     * @param valor
     * @return
     */
    private boolean validarSINO(String valor) {
        switch (valor) {
        case "SI":
        case "NO":
            registro.getCampos().put(cValor, valor.trim());
            break;
        default:
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB549"));
            return false;
        }
        return true;
    }

    /**
     * Valida que el valor ingresado en el parametro {@code valor}
     * corresponda al patron de una hora
     * 
     * @param valor
     * @return
     */
    private boolean validarHora(String valor) {
        Pattern pat = Pattern
                        .compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
        Matcher mat = pat.matcher(valor);
        if (mat.matches()) {
            return true;
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB550"));
        }
        return false;
    }

    public boolean verificarFormatoValor() {
        String valor = (String) registro.getCampos().get(cValor);
        boolean verificar = true;
        try {
            switch (registro.getCampos().get(cTipoParametro).toString()) {
            case "2":
                verificar = validarValorDouble(valor);
                break;
            case "3":
                verificar = validarValorInt(valor);
                break;
            case "4":
                verificar = validarValorFecha(valor.replace("-", "/"));
                break;
            case "5":
                verificar = validarSINO(valor.trim().toUpperCase());
                break;
            case "6":
                verificar = validarHora(valor);
                break;
            default:
                break;
            }
        }
        catch (NullPointerException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB551"));
            return false;
        }
        return verificar;
    }

    public void cambiarTipoParametroC(int rowNum) {
        listaInicial.getDatasource().get(rowNum).getCampos().put(cValor, "");
        if ("6".equals(listaInicial.getDatasource().get(rowNum).getCampos()
                        .get(cTipoParametro))) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB552"));
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        String nombre = registro.getCampos().get(cNombre).toString()
                        .toUpperCase();
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put(cNombre, nombre);

        registro.getCampos().remove("NOMPARAMETRO");
        registro.getCampos().remove("NOMAPLICACION");
        // </CODIGO_DESARROLLADO>
        return true;

    }

    public void cambiarTipoParametro() {
        if ("6".equals(registro.getCampos().get(cTipoParametro))) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB552"));
        }
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
        registro.getCampos().remove("NOMPARAMETRO");
        registro.getCampos().remove("NOMAPLICACION");

        if ((registro.getCampos().get(cTipoParametro) == null)
            || ("").equals(registro.getCampos().get(cTipoParametro))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1781"));
            return false;
        }
        else {
            if ((registro.getCampos().get(cValor) == null)
                || ("").equals(registro.getCampos().get(cValor))) {
                return true;
            }
            else {
                return verificarFormatoValor();
            }
        }
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
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

    @Override
    public void removerCombos() {
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove("MODULO");
        registro.getCampos().remove("DESCRIPCION");
        registro.getCampos().remove("COMPANIA");
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
    }
    
    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     */
	public void ejecutarrcCerrar(){
		
        if("1".equals(cerrar)) {
        	Direccionador direccionador = new Direccionador();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("cerrar", "1");
            parametros.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            direccionador.setNumForm(Integer
                    .toString(GeneralCodigoFormaEnum.PARAMETROS_CONTROLADOR
                                    .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarMenuFormulario("960227");
        }
        else {
        	SessionUtil.redireccionar("/menu.sysman");
        }
        
	}

    @Override
    public void asignarValoresRegistro() {
        try {
            registro.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            SysmanFunciones.convertirAFechaCadena(
                                            new Date(),
                                            "dd/MM/YYYY"));
            registro.getCampos().put("FECHA_INICIAL",
                            new Date());
        }
        catch (ParseException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

}
