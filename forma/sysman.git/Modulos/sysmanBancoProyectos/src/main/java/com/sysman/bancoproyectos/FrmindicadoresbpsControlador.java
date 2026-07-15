package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmindicadoresbpsControladorEnum;
import com.sysman.bancoproyectos.enums.FrmindicadoresbpsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 17/06/2016
 * 
 * @version 2, 15/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos y en el origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class FrmindicadoresbpsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private List<Registro> listacmbIndicador;
    private List<Registro> listacmbPrograma;
    private String accionActual;
    private String programaActual;
    private int vigenciaActual;
    private boolean condicionPrograma;
    private int digitosActual;
    private String tipoMeta;
    private int indice;
    private Map<String, Object> parametrosEntrada;
    private String codigoBpim;
    private String codigoProyecto;
    private boolean muestraNuevo;
    private boolean muestraActualiza;
    private String menuActual;
    private String filtro;
    private static final String TIPO_META = "TIPO_META";
    private static final String ID_PLAN = "ID_PLAN";
    private static final String MR = "MR";
    private final String consIndicador;
    private boolean estado;

    /**
     * Creates a new instance of FrmindicadoresbpsControlador
     */
    public FrmindicadoresbpsControlador() {
        super();
        compania = SessionUtil.getCompania();
        condicionPrograma = true;
        consIndicador="COD_INDICADOR";
        try {

            menuActual = SessionUtil.getMenuActual();
            menuActual = menuActual == null ? "NULL" : menuActual;
            if ("52020102".equals(menuActual)) {
                muestraNuevo = false;
                muestraActualiza = false;
            }
            if ("52020101".equals(menuActual)) {
                muestraNuevo = true;
                muestraActualiza = true;
            }
            if ("52020402".equals(menuActual)) {
                muestraNuevo = false;
                muestraActualiza = false;
            }

            numFormulario = GeneralCodigoFormaEnum.FRMINDICADORESBPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                codigoBpim = (String) parametrosEntrada.get("codigoBpim");
                codigoProyecto = (String) parametrosEntrada
                                .get("codigoProyecto");

                parametrosEntrada.put("rid",
                                parametrosEntrada.get("ridProyecto"));
                parametrosEntrada.remove("codigoProyecto");
                parametrosEntrada.remove("ridProyecto");
            }
            else {
                SessionUtil.redireccionarMenuPermisos();
            }
        }
        catch (Exception ex) {
            Logger.getLogger(FrmindicadoresbpsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_INDICADORES_PI;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListacmbIndicador();
        cargarListacmbPrograma();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoProyecto);
    }

    public String getProgramaActual() {
        return programaActual;
    }

    public void setProgramaActual(String programaActual) {
        this.programaActual = programaActual;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getVigenciaActual() {
        return vigenciaActual;
    }

    public void setVigenciaActual(int vigenciaActual) {
        this.vigenciaActual = vigenciaActual;
    }

    public boolean getCondicionPrograma() {
        return condicionPrograma;
    }

    public void setCondicionPrograma(Boolean condicionPrograma) {
        this.condicionPrograma = condicionPrograma;
    }

    public int getDigitosActual() {
        return digitosActual;
    }

    public void setDigitosActual(int digitosActual) {
        this.digitosActual = digitosActual;
    }

    public String getCodigoBpim() {
        return codigoBpim;
    }

    public void setCodigoBpim(String codigoBpim) {
        this.codigoBpim = codigoBpim;
    }

    public String getTipoMeta() {
        return tipoMeta;
    }

    public void setTipoMeta(String tipoMeta) {
        this.tipoMeta = tipoMeta;
    }

    public String getAccionActual() {
        return accionActual;
    }

    public void setAccionActual(String accionActual) {
        this.accionActual = accionActual;
    }

    public List<Registro> getListacmbIndicador() {
        return listacmbIndicador;
    }

    public void setListacmbIndicador(List<Registro> listacmbIndicador) {
        this.listacmbIndicador = listacmbIndicador;
    }

    public List<Registro> getListacmbPrograma() {
        return listacmbPrograma;
    }

    public void setListacmbPrograma(List<Registro> listacmbPrograma) {
        this.listacmbPrograma = listacmbPrograma;
    }

    public void cargarListacmbIndicador() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.VIGENCIA.getName(),
                            vigenciaActual);
            param.put(GeneralParameterEnum.ID_PLAN.getName(),
                            programaActual);
            
            listacmbIndicador = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmindicadoresbpsControladorUrlEnum.URL6560
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListacmbPrograma() {
        try {
            String url;
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.VIGENCIA.getName(),vigenciaActual);

            if(MR.equals(tipoMeta)){
                param.put(GeneralParameterEnum.ID_PLAN.getName(),filtro);
                url=UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmindicadoresbpsControladorUrlEnum.URL7265
                                                                .getValue())
                                .getUrl();
            }else {
                param.put(FrmindicadoresbpsControladorEnum.PARAM0.getValue(),filtro);
                url=UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmindicadoresbpsControladorUrlEnum.URL4786
                                                                .getValue())
                                .getUrl();
            }
            
            listacmbPrograma = RegistroConverter.toListRegistro(
                            requestManager.getList(url, param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarcmbTipoMeta() {
  
            
        tipoMeta = registro.getCampos().get(TIPO_META).toString();
        registro.getCampos().put("CANTIDAD_META", 0);
        registro.getCampos().put(ID_PLAN, null);
        registro.getCampos().put(consIndicador, null);
        cargarDatosPrograma();
    }

    public void cambiarcmbTipoMetaC(int rowNum) {
        if(estado){
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
            .put(ID_PLAN, null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
            .put(consIndicador, null);
        }
        tipoMeta = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(TIPO_META).toString();
        cargarDatosPrograma();

    }

    public void cambiarcmbPrograma() {
        programaActual = SysmanFunciones.nvl(registro.getCampos().get(ID_PLAN),"").toString();
        registro.getCampos().put(consIndicador, null);
        cargarListacmbIndicador();
    }

    public void cambiarcmbProgramaC(int rowNum) {
        if(estado){
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
            .put(consIndicador, null);
        }
        programaActual = SysmanFunciones.nvl(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(ID_PLAN), "").toString();
        estado=true;
        cargarListacmbIndicador();

    }

    public void cargarDatosPrograma() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),codigoBpim);
            
            Registro rAccion = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmindicadoresbpsControladorUrlEnum.URL6271
                                                                            .getValue())
                                            .getUrl(), param));
            
            if (rAccion != null) {
                accionActual = rAccion.getCampos().get("ID").toString();
                vigenciaActual = Integer.parseInt(rAccion.getCampos()
                                .get("VIGENCIA_INICIAL").toString());
                
                Map<String,Object> param2 = new TreeMap<>();
                param2.put(GeneralParameterEnum.COMPANIA.getName(),compania);
                param2.put(GeneralParameterEnum.VIGENCIA.getName(),vigenciaActual);
                
                Registro rDigitos = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmindicadoresbpsControladorUrlEnum.URL4576
                                                                                .getValue())
                                                .getUrl(), param2));
                if (rDigitos != null) {
                    digitosActual = Integer.parseInt(
                                    rDigitos.getCampos().get("DIGITOS").toString());

                    filtro = accionActual.substring(0, digitosActual);
                    cargarListacmbPrograma();
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2308")
                                    .replace("#$tipoMeta#$", tipoMeta));
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2309"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(ID_PLAN, programaActual);
        registro.getCampos().put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(), vigenciaActual);
        registro.getCampos().put("COD_PROYECTO", codigoProyecto);
        registro.getCampos().put(TIPO_META, tipoMeta);

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
        registro.getCampos().remove("TIPO_META_LB");
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

        if ((registro.getCampos().get("CANTIDAD_META") == null)
            || (registro.getCampos().get(TIPO_META) == null)
            || (registro.getCampos().get(consIndicador) == null)
            || (registro.getCampos().get(ID_PLAN) == null)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2310"));
            return false;
        }

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

    @Override
    public void removerCombos() {
        // HEREDADO DEL BEAN BASE
    }

    public void ejecutarrcCerrar() {
        SessionUtil.setFlash(parametrosEntrada);
        SessionUtil.redireccionar("/frmproyectos.sysman");
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        estado=false;
    }

    @Override
    public void asignarValoresRegistro() {
        // HEREDADO DEL BEAN BASE
    }

    public boolean isMuestraNuevo() {
        return muestraNuevo;
    }

    public void setMuestraNuevo(boolean muestraNuevo) {
        this.muestraNuevo = muestraNuevo;
    }

    public boolean isMuestraActualiza() {
        return muestraActualiza;
    }

    public void setMuestraActualiza(boolean muestraActualiza) {
        this.muestraActualiza = muestraActualiza;
    }

}
