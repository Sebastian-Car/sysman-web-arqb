package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.enums.ReversarrequisicionControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 14/01/2016
 *
 * @version 2, 08/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */

@ManagedBean
@ViewScoped
public class ReversarrequisicionControlador extends BeanBaseModal {

    private final String compania;
    private String numero;
    private String numeroDescripcion;
    private Date fecha;
    private boolean visibleDialogo;
    private RegistroDataModelImpl listaNumero;

    @EJB
    private EjbAlmacenCeroRemote ejbAlmacenCeroRemote;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of ReversarrequisicionControlador
     */
    public ReversarrequisicionControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.REVERSARREQUISICION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ReversarrequisicionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaNumero();
        abrirFormulario();
    }

    public void cargarListaNumero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReversarrequisicionControladorUrlEnum.URL1941
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    public void oprimirComando13() {
        // <CODIGO_DESARROLLADO>
        if ((numero == null) || numero.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1934"));
            return;
        }
        try {
            String estado = ejbSysmanUtilRemote.verificarEstadoDiario(compania,
                            SysmanFunciones.ano(fecha),
                            SysmanFunciones.mes(fecha),
                            SysmanFunciones.dia(fecha),
                            Integer.parseInt(SessionUtil.getModulo()), 1);
            if ("A".equals(estado)){
                visibleDialogo=true;
            }else{
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3249"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }
    
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * del dialogo DG_MENSAJE en la vista
     *
     *
     */
    public void aceptarDGMENSAJE() {
        //<CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        List<Registro> rs;
        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReversarrequisicionControladorUrlEnum.URL3252
                                                                            .getValue())
                                            .getUrl(), param));
            int aux = rs.size();
            if (aux == 0) {
                ejbAlmacenCeroRemote.reversarRequisicion(compania,
                                Long.parseLong(numero),
                                SessionUtil.getUser().getCodigo());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1932"));
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1933"));
            }
            visibleDialogo=false;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        //</CODIGO_DESARROLLADO>
    }

    public void oprimirComando14() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = registroAux.getCampos().get("NUMERO").toString();
        numeroDescripcion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DESCRIPCION"), " ")
                        .toString();
        fecha= (Date) registroAux.getCampos().get("FECHA");
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }

    public String getNumeroDescripcion() {
        return numeroDescripcion;
    }

    public void setNumeroDescripcion(String numeroDescripcion) {
        this.numeroDescripcion = numeroDescripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
