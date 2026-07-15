package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialSeisRemote;
import com.sysman.predial.enums.FrmasignaporcentajereservaControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 24/05/2016
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @version 3, 29/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class FrmasignaporcentajereservaControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el valor de
     * SysmanConstantes.NUMERO_ORDEN_PREDIAL
     */
    private final String numOrdenPredial;

    /** Constante a nivel de clase que aloja el codigo del usuario */
    private final String cUsuario;

    /**
     * Constante que identifica el nombre del campo CODIGO
     */
    private final String campoCodigo;

    /**
     * Constante que identifica el contenido del texto en bean
     * MSM_PROCESO_EJECUTADO
     */
    private final String msmProcEjecutado;
    // <DECLARAR_ATRIBUTOS>
    private String codigo;
    private String vigencia;
    private String resolucion;
    private String porcentaje;
    private boolean visibleCancelar;
    private boolean visibleDesactivar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listatxtVigencia;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTxtCodigo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    @EJB
    private EjbPredialSeisRemote ejbPredialSeisRemote;

    /**
     * Creates a new instance of FrmasignaporcentajereservaControlador
     */
    public FrmasignaporcentajereservaControlador() {
        super();

        compania = SessionUtil.getCompania();
        cUsuario = SessionUtil.getUser().getCodigo();
        numOrdenPredial = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        campoCodigo = "CODIGO";
        msmProcEjecutado = "MSM_PROCESO_EJECUTADO";

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMASIGNAPORCENTAJERESERVA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            visibleCancelar = false;
            visibleDesactivar = false;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmasignaporcentajereservaControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTxtCodigo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listatxtVigencia asociada al combo 'Vigencia'
     */
    public void cargarListatxtVigencia() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),codigo);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),numOrdenPredial);
            
            listatxtVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmasignaporcentajereservaControladorUrlEnum.URL4111
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista {@code listaTxtCodigo} asociada al combo
     * 'C�digo del Predio'
     */
    public void cargarListaTxtCodigo() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmasignaporcentajereservaControladorUrlEnum.URL5123.getValue());     
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),numOrdenPredial);

        listaTxtCodigo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, campoCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Gestiona los eventos de presionar el boton 'Descativar reserva
     * (A�o)'
     */
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbPredialSeisRemote.cancelarReserva(compania, codigo,
                            Integer.parseInt(vigencia), numOrdenPredial,
                            cUsuario, resolucion);
            cambiartxtVigencia();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msmProcEjecutado));

            porcentaje = null;
            resolucion = null;

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Gestiona los eventos de presionar el boton 'Activar reserva'
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        double porc = Double.parseDouble(porcentaje) / 100;
        try {
            ejbPredialSeisRemote.activarReserva(compania, codigo, porc,
                            Integer.parseInt(vigencia), numOrdenPredial,
                            cUsuario, resolucion);
            visibleCancelar = visibleDesactivar = true;
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msmProcEjecutado));

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Gestiona los eventos de presionar el boton 'Descativar reserva
     * (Todos a�os)'
     */
    public void oprimirDesactiva() {
        // <CODIGO_DESARROLLADO>

        try {
            ejbPredialSeisRemote.cancelarReservasUsuario(compania, codigo,
                            Integer.parseInt(vigencia), numOrdenPredial,
                            cUsuario, resolucion);
            cambiartxtVigencia();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msmProcEjecutado));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartxtVigencia() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),codigo);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),numOrdenPredial);
            param.put(GeneralParameterEnum.PREANO.getName(),vigencia);
            
            Registro rs= RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmasignaporcentajereservaControladorUrlEnum.URL4657
                                                                            .getValue())
                                            .getUrl(), param));
            if (rs != null) {
                
                double porc = Double.parseDouble(
                                rs.getCampos().get("PORCENTAJE_RESERVA").toString())
                                * 100;
                
                porcentaje = String.valueOf(porc);
                resolucion = rs.getCampos().get("RESOLUCION_RESERVA").toString();
                visibleCancelar = true;
                visibleDesactivar = true;
                
            }
            else {
                porcentaje = null;
                resolucion = null;
                visibleCancelar = false;
                visibleDesactivar = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }



        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTxtCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigo = registroAux.getCampos().get(campoCodigo).toString();

        vigencia = null;
        porcentaje = null;
        resolucion = null;
        visibleCancelar = false;
        visibleDesactivar = false;

        cargarListatxtVigencia();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public boolean isVisibleCancelar() {
        return visibleCancelar;
    }

    public void setVisibleCancelar(boolean visibleCancelar) {
        this.visibleCancelar = visibleCancelar;
    }

    public boolean isVisibleDesactivar() {
        return visibleDesactivar;
    }

    public void setVisibleDesactivar(boolean visibleDesactivar) {
        this.visibleDesactivar = visibleDesactivar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListatxtVigencia() {
        return listatxtVigencia;
    }

    public void setListatxtVigencia(List<Registro> listatxtVigencia) {
        this.listatxtVigencia = listatxtVigencia;
    }


    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
   
    public RegistroDataModelImpl getListaTxtCodigo() {
        return listaTxtCodigo;
    }
    
    public void setListaTxtCodigo(RegistroDataModelImpl listaTxtCodigo) {
        this.listaTxtCodigo = listaTxtCodigo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
