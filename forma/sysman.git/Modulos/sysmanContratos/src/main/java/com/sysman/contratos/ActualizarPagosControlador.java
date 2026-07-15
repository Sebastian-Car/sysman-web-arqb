package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.ejb.EjbContratosCeroRemote;
import com.sysman.contratos.enums.ActualizarPagosControladorEnum;
import com.sysman.contratos.enums.ActualizarPagosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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

import org.primefaces.model.StreamedContent;

/**
 *
 * @author sdaza
 * @version 1, 23/10/2015
 * 
 * @modifier amonroy
 * @version 2, 02/08/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para la funcion que es llamada en el
 * controlador
 */
@ManagedBean
@ViewScoped
public class ActualizarPagosControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String tipoContratoIni;
    private String tipoContratoFin;
    private Date fechaCorte;
    private StreamedContent archivoDescarga;
    private List<Registro> listaTipoContratoInicial;
    private List<Registro> listaTipoContratoFinal;
    /**
     * Implementacion del EJB de EjbContratosCeroRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_CONTRATOS
     */
    @EJB
    private EjbContratosCeroRemote ejbContratosCero;
    /**
     * Atributo que almacena el nombre del contrato inicial que ha
     * sido seleccionado
     */
    private String nombreContratoInicial;

    /**
     * Creates a new instance of ActualizarPagosControlador
     */
    public ActualizarPagosControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.ACTUALIZAR_PAGOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ActualizarPagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContratoInicial();
        cargarListaTipoContratoFinal();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTipoContratoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizarPagosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoContratoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ActualizarPagosControladorEnum.CONTRATO.getValue(),
                        nombreContratoInicial);

        try {
            listaTipoContratoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizarPagosControladorUrlEnum.URL3150
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        String rta;
        try {
            rta = ejbContratosCero.actualizarPagosOrdenCompra(compania,
                            fechaCorte,
                            tipoContratoIni,
                            tipoContratoFin,
                            Integer.parseInt(modulo),
                            new Date(),
                            SessionUtil.getUser().getCodigo());

            String mensaje = idioma.getString("TB_TB3345");
            mensaje = mensaje.replace("s$respuesta$s", rta);
            JsfUtil.agregarMensajeInformativo(mensaje);
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(ActualizarPagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTipoContratoInicial() {
        // <CODIGO_DESARROLLADO>
        nombreContratoInicial = service.buscarEnLista(tipoContratoIni,
                        GeneralParameterEnum.CODIGO.getName(),
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaTipoContratoInicial);
        cargarListaTipoContratoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public String getTipoContratoIni() {
        return tipoContratoIni;
    }

    public void setTipoContratoIni(String tipoContratoIni) {
        this.tipoContratoIni = tipoContratoIni;
    }

    public String getTipoContratoFin() {
        return tipoContratoFin;
    }

    public void setTipoContratoFin(String tipoContratoFin) {
        this.tipoContratoFin = tipoContratoFin;
    }

    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        List<Registro> listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public List<Registro> getListaTipoContratoFinal() {
        return listaTipoContratoFinal;
    }

    public void setListaTipoContratoFinal(
        List<Registro> listaTipoContratoFinal) {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

    @Override
    public void abrirFormulario() {
        fechaCorte = new Date();
    }
}
