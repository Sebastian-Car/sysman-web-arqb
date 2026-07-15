package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.UresolucionesControladorEnum;
import com.sysman.predial.enums.UresolucionesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author sdaza
 * @version 1, 03/06/2016
 * 
 * @version 2, 24/07/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos y en el origen de datos.
 * 
 */

@ManagedBean
@ViewScoped
public class UresolucionesControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;

    /**
     * Constante que almacenara la cadena "RESOLUCION"
     */
    private final String resolucionC;

    /**
     * Constante que almacenara la cadena "MUNICIPIO"
     */
    private final String municipioC;

    /**
     * Constante que almacenara la cadena "DEPARTAMENTO"
     */
    private final String departamentoC;

    /**
     * Constante que almacenara la cadena "COMPANIA"
     */
    private final String companiaC;
    
    // <DECLARAR_ATRIBUTOS>
    private String paisResolucion;
    private String dptoResolucion;
    private String ciudadResolucion;
    private String anoResolucion;
    private String resolucion;
    private String fechaResolucion;
    private String codigoPredio;
    private String nomPropietario;
    private String direccionPredio;
    private String numeroOrden;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaResDpto;
    private List<Registro> listaCiudadRes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaResolucion;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaUresolucionessub;
    private List<Registro> listaSubdesubavaluos;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSubUResolucionesSub;
    private Registro registroSubSUBDESUBAVALUOS;
    private Map<String, Object> parametrosEntrada;

    // </DECLARAR_ADICIONALES>
    public UresolucionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        resolucionC = "RESOLUCION";
        municipioC = "MUNICIPIO";
        departamentoC = "DEPARTAMENTO";
        companiaC = "COMPANIA";

        try {
            numFormulario = GeneralCodigoFormaEnum.URESOLUCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubUResolucionesSub = new Registro(
                            new HashMap<String, Object>());
            registroSubSUBDESUBAVALUOS = new Registro(
                            new HashMap<String, Object>());
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                codigoPredio = parametrosEntrada.get("codigoPredio").toString();
                numeroOrden = parametrosEntrada.get("nroOrden").toString();
                nomPropietario = SysmanFunciones.nvl(parametrosEntrada.get("nomPropietario"), "").toString();
                direccionPredio = SysmanFunciones.nvl(parametrosEntrada.get("direccionPredio"),"").toString();
                paisResolucion = parametrosEntrada.get("paisPredio").toString();
                dptoResolucion = parametrosEntrada.get("dptoPredio").toString();
                ciudadResolucion = parametrosEntrada.get("ciudadPredio").toString();
                accion =  parametrosEntrada.get("accion").toString();
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(UresolucionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaResolucion();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaResDpto();
        cargarListaCiudadRes();
        cargarListaResolucion();
        cargarListaSubdesubavaluos();
        cargarListaUResolucionessub();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaUResolucionessub();
        cargarListaSubdesubavaluos();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaUresolucionessub = null;
        listaSubdesubavaluos = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        tabla = "";
        asignarOrigenDatos();
        iniciarListas();
    }

    @Override
    public void asignarOrigenDatos() {
        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        UresolucionesControladorUrlEnum.URL13925.getValue());
        
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);
    }



    public void cargarListaUResolucionessub() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(UresolucionesControladorEnum.PARAM0.getValue(),paisResolucion);
            param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),dptoResolucion);
            param.put(GeneralParameterEnum.CIUDAD.getName(),ciudadResolucion);
            param.put(GeneralParameterEnum.ANO.getName(),anoResolucion);
            param.put(GeneralParameterEnum.CODIGO.getName(),codigoPredio);
            
            listaUresolucionessub = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UresolucionesControladorUrlEnum.URL7967
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSubdesubavaluos() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.RESOLUCION.getName(),resolucion);
            
            listaSubdesubavaluos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UresolucionesControladorUrlEnum.URL12187
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaResDpto() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(UresolucionesControladorEnum.PARAM0.getValue(),paisResolucion);
            param.put(GeneralParameterEnum.CODIGO.getName(),dptoResolucion);
            
            listaResDpto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UresolucionesControladorUrlEnum.URL12977
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCiudadRes() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(UresolucionesControladorEnum.PARAM0.getValue(),paisResolucion);
            param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),dptoResolucion);
            param.put(GeneralParameterEnum.CODIGO.getName(),ciudadResolucion);
            
            listaCiudadRes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UresolucionesControladorUrlEnum.URL13585
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaResolucion() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(UresolucionesControladorUrlEnum.URL14299.getValue());         
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),codigoPredio);

        listaResolucion = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, resolucionC);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaResolucion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        resolucion = registroAux.getCampos().get(resolucionC).toString();
        fechaResolucion = registroAux.getCampos().get("FECHAINGRESOSISTEMA").toString();
        paisResolucion = registroAux.getCampos().get("PAIS").toString();
        dptoResolucion = registroAux.getCampos().get(departamentoC).toString();
        ciudadResolucion = registroAux.getCampos().get(municipioC).toString();
        anoResolucion = registroAux.getCampos().get("ANO").toString();
        cargarListaResDpto();
        cargarListaCiudadRes();
        cargarListaUResolucionessub();
        cargarListaSubdesubavaluos();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>

    public void onCancelUResolucionessub() {
        cargarListaUResolucionessub();
        cargarListaSubdesubavaluos();
    }

    public void onCancelSubdesubavaluos() {
        cargarListaSubdesubavaluos();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(companiaC, compania);
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
        // </CODIGO_DESARROLLADO>
        return true;
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

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.USUARIOSPREDIALS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getPaisResolucion() {
        return paisResolucion;
    }

    public void setPaisResolucion(String paisResolucion) {
        this.paisResolucion = paisResolucion;
    }

    public String getDptoResolucion() {
        return dptoResolucion;
    }

    public void setDptoResolucion(String dptoResolucion) {
        this.dptoResolucion = dptoResolucion;
    }

    public String getCiudadResolucion() {
        return ciudadResolucion;
    }

    public void setCiudadResolucion(String ciudadResolucion) {
        this.ciudadResolucion = ciudadResolucion;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(String fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public String getNomPropietario() {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario) {
        this.nomPropietario = nomPropietario;
    }

    public String getDireccionPredio() {
        return direccionPredio;
    }

    public void setDireccionPredio(String direccionPredio) {
        this.direccionPredio = direccionPredio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaResDpto() {
        return listaResDpto;
    }

    public void setListaResDpto(List<Registro> listaResDpto) {
        this.listaResDpto = listaResDpto;
    }

    public List<Registro> getListaCiudadRes() {
        return listaCiudadRes;
    }

    public void setListaCiudadRes(List<Registro> listaCiudadRes) {
        this.listaCiudadRes = listaCiudadRes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaResolucion() {
        return listaResolucion;
    }

    public void setListaResolucion(RegistroDataModelImpl listaResolucion) {
        this.listaResolucion = listaResolucion;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    public List<Registro> getListaSubdesubavaluos() {
        return listaSubdesubavaluos;
    }

   

    public List<Registro> getListaUresolucionessub() {
        return listaUresolucionessub;
    }

    public void setListaUresolucionessub(List<Registro> listaUresolucionessub) {
        this.listaUresolucionessub = listaUresolucionessub;
    }

    public void setListaSubdesubavaluos(List<Registro> listaSubdesubavaluos) {
        this.listaSubdesubavaluos = listaSubdesubavaluos;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public Registro getRegistroSubSUBDESUBAVALUOS() {
        return registroSubSUBDESUBAVALUOS;
    }

    public Registro getRegistroSubUResolucionesSub() {
        return registroSubUResolucionesSub;
    }

    public void setRegistroSubUResolucionesSub(
        Registro registroSubUResolucionesSub) {
        this.registroSubUResolucionesSub = registroSubUResolucionesSub;
    }

    public void setRegistroSubSUBDESUBAVALUOS(
        Registro registroSubSUBDESUBAVALUOS) {
        this.registroSubSUBDESUBAVALUOS = registroSubSUBDESUBAVALUOS;
    }
    // </SET_GET_ADICIONALES>
}
