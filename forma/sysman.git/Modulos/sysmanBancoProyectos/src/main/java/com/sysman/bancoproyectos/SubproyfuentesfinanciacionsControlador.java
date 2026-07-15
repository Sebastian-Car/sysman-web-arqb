/*-
 * 
 */
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoDosRemote;
import com.sysman.bancoproyectos.enums.SubproyfuentesfinanciacionsControladorEnum;
import com.sysman.bancoproyectos.enums.SubproyfuentesfinanciacionsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.SQLException;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 24/09/2015
 * 
 * @version 2, 29/09/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class SubproyfuentesfinanciacionsControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private final String valorCons;
    
    private String auxiliar;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CONSECUTIVO</code>
     */
    private final String cConsecutivo = GeneralParameterEnum.CONSECUTIVO
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOFUENTE</code>
     */
    private final String cCodigoFuente = SubproyfuentesfinanciacionsControladorEnum.CODIGOFUENTE
                    .getValue();
    
    private final String cNombreFuente = SubproyfuentesfinanciacionsControladorEnum.NOMBREFUENTE
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>PROYECTO</code>
     */
    private final String cProyecto = GeneralParameterEnum.PROYECTO.getName();

    /**
     * Atributo a nivel clase que guarda el codigo seleccionado en el
     * combo codigo
     */
    private String codigoFuente;

    private String codigoProyecto;
    private String anoInicial;
    private String anoFinal;
    private BigDecimal totalProyecto;
    private String accion;
    private int indice;
    private List<Registro> listaCODIGOFUENTE;
    private List<Registro> listaCODIGOFUENTEE;
    private List<Registro> listaVigencia;
    private BigDecimal valorInicialRegistro;
    private String menuActual;
    private boolean muestraRegistro;

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: PCK_BANCOS_PROY2
     */
    @EJB
    private EjbBancoProyectoDosRemote ejbBancoProyectoDos;

    /**
     * Creates a new instance of
     * SubproyfuentesfinanciacionsControlador
     */
    public SubproyfuentesfinanciacionsControlador() {
        super();

        // 249
        numFormulario = GeneralCodigoFormaEnum.SUBPROYFUENTESFINANCIACIONS_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();

        valorCons = GeneralParameterEnum.VALOR.getName();

        try {
            validarPermisos();

            menuActual = SessionUtil.getMenuActual();
            menuActual = menuActual == null ? "NULL" : menuActual;

            switch (menuActual) {
            case "52020102":
            case "52020402":
                muestraRegistro = false;
                break;
            case "52020101":
                muestraRegistro = true;
                break;
            default:
                SessionUtil.redireccionarMenu();
                break;
            }

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                codigoProyecto = (String) parametrosEntrada
                                .get("codigoProyecto");

                anoInicial = (String) parametrosEntrada.get("anoInicial");
                anoFinal = (String) parametrosEntrada.get("anoFinal");

                totalProyecto = new BigDecimal(SysmanFunciones
                                .nvl(parametrosEntrada.get("totalProyecto"),
                                                "0")
                                .toString());

                accion = (String) parametrosEntrada.get("accion");
            }

            if (("v").equals(accion)) {
                muestraRegistro = false;
            }
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();

            Logger.getLogger(SubproyfuentesfinanciacionsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_PROYFUENTESFINANCIACION;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        asignarValoresRegistro();
        cargarListaCODIGOFUENTE();
        cargarListaCODIGOFUENTEE();
        cargarListaVigencia();
        abrirFormulario();
        }
    
    
    public String getAuxiliar() {
        return auxiliar;
    }
    
    public void setAuxiliar(String auxiliar) {
        this.auxiliar= auxiliar;
    }

    public List<Registro> getListaCODIGOFUENTE() {
        return listaCODIGOFUENTE;
    }

    public void setListaCODIGOFUENTE(List<Registro> listaCODIGOFUENTE) {
        this.listaCODIGOFUENTE = listaCODIGOFUENTE;
    }
    
    public List<Registro> getListaCODIGOFUENTEE() {
        return listaCODIGOFUENTEE;
    }

    public void setListaCODIGOFUENTEE(List<Registro> listaCODIGOFUENTEE) {
        this.listaCODIGOFUENTEE = listaCODIGOFUENTEE;
    }

    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public String getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    public String getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isMuestraRegistro() {
        return muestraRegistro;
    }

    public void setMuestraRegistro(boolean muestraRegistro) {
        this.muestraRegistro = muestraRegistro;
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        valorInicialRegistro = new BigDecimal(
                        registro.getCampos().get(valorCons).toString());
    }

    public void cargarListaCODIGOFUENTE() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaCODIGOFUENTE = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubproyfuentesfinanciacionsControladorUrlEnum.URL5848
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void  cargarListaCODIGOFUENTEE(){
        
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaCODIGOFUENTEE = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubproyfuentesfinanciacionsControladorUrlEnum.URL5848
                                                                            .getValue())
                                            .getUrl(), param));            
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
     }
    
    
    public void seleccionarFilaCODIGOFUENTE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        
        registro.getCampos().put(SubproyfuentesfinanciacionsControladorEnum.CODIGOFUENTE.getValue(),
                        registroAux.getCampos().get(cNombreFuente));
        registro.getCampos().put("NOMBREFUENTE", registroAux.getCampos().get(cCodigoFuente));
        
        registro.getCampos().put(SubproyfuentesfinanciacionsControladorEnum.CODIGOFUENTE.getValue(),
                        registroAux.getCampos().get(cCodigoFuente));
        registro.getCampos().put("CODIGOFUENTE", registroAux.getCampos().get(cCodigoFuente));
        
        cambiarCODIGOFUENTE();
    }

    
    public void seleccionarFilaCODIGOFUENTEE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar =  (String) registroAux.getCampos().get(cCodigoFuente);

    }
    
    public void cambiarCODIGOFUENTE() {
        
//        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CONCECUTIVO",
//                        cConsecutivo);
        
        codigoFuente = SysmanFunciones
                        .nvl(registro.getCampos().get(cCodigoFuente), "")
                        .toString();

        if (!codigoFuente.isEmpty()) {
            generarConsecutivo();
        }
    }

    public void cargarListaVigencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        param.put(SubproyfuentesfinanciacionsControladorEnum.ANOINICIAL
                        .getValue(), anoInicial);

        param.put(SubproyfuentesfinanciacionsControladorEnum.ANIOFINAL
                        .getValue(), anoFinal);

        try {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubproyfuentesfinanciacionsControladorUrlEnum.URL6163
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarVALOR() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVALORC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cProyecto, codigoProyecto);

        valorInicialRegistro = BigDecimal.ZERO;

        return true;
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(SubproyfuentesfinanciacionsControladorEnum.NOMBREFUENTE
                                        .getValue());

        try {
            verificarSaldoProyecto();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(SubproyfuentesfinanciacionsControladorEnum.CODIGOCOMPONENTE
                                        .getValue());

        registro.getCampos()
                        .remove(SubproyfuentesfinanciacionsControladorEnum.PANTALLA
                                        .getValue());

        registro.getCampos().remove(cCompania);

        registro.getCampos()
                        .remove(SubproyfuentesfinanciacionsControladorEnum.TIPOCOMPONENTE
                                        .getValue());

        registro.getCampos()
                        .remove(SubproyfuentesfinanciacionsControladorEnum.CODIGOSUBFUENTE
                                        .getValue());

        registro.getCampos().remove(cCodigoFuente);
        registro.getCampos().remove(cProyecto);

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cProyecto, codigoProyecto);

    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Util para generar el siguiente valor ascendente del consecutivo
     * para el proyecto y el codigo fuente seleccionado
     */
    private void generarConsecutivo() {
        String criterio = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "'' AND PROYECTO = ''", codigoProyecto,
                        "'' AND CODIGOFUENTE = ''", codigoFuente, "''");

        try {
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            enumBase.getTable(), criterio, cConsecutivo, "1");

            registro.getCampos().put(cConsecutivo, consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Verifica que el total de la suma de los valores de las fuentes
     * de financiación no supere el total de proyecto.
     * 
     * @throws SystemException
     */
    private void verificarSaldoProyecto() throws SystemException {
        BigDecimal valor = new BigDecimal(
                        registro.getCampos().get(valorCons).toString());

        ejbBancoProyectoDos.verificarSaldoProyecto(compania, codigoProyecto,
                        valor, valorInicialRegistro, totalProyecto);
    }
}