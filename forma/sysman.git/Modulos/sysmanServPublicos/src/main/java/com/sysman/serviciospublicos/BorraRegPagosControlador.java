package com.sysman.serviciospublicos;

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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUnoRemote;
import com.sysman.serviciospublicos.enums.BorraRegPagosControladorUrlEnum;

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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 30/08/2016
 * @modified jguerrero
 * @version 2. 17/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Se cambió el llamado del código del
 * formulario
 */
@ManagedBean
@ViewScoped

public class BorraRegPagosControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String banco;
    private String paquete;
    private Date fecha;
    private String nombreBanco;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaPaquete;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaBanco;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private boolean muestraDialogo;
    private String tituloDialogo;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServPubOcho;

    @EJB
    private EjbServiciosPublicosUnoRemote ejbServPubUno;

    /**
     * Creates a new instance of BorraRegPagosControlador
     */
    public BorraRegPagosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.BORRA_REG_PAGOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(BorraRegPagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        fecha = new Date();
        cargarListaPaquete();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBanco();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaPaquete() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.BANCO.getName(), banco);
        param.put(GeneralParameterEnum.FECHA.getName(), fecha);

        try {
            listaPaquete = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BorraRegPagosControladorUrlEnum.URL2857
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaBanco() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BorraRegPagosControladorUrlEnum.URL3581
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        try {
            boolean rta = ejbServPubOcho.borrarPaquetePago(compania, banco,
                            fecha, paquete);

            if (!rta) {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.BANCO.getName(), banco);
                param.put(GeneralParameterEnum.FECHA.getName(), fecha);
                param.put(GeneralParameterEnum.PAQUETE.getName(), paquete);

                Registro rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                BorraRegPagosControladorUrlEnum.URL2858
                                                                                .getValue())
                                                .getUrl(), param));
                tituloDialogo = idioma.getString("TB_TB1395").replace(
                                "#cantidad#",
                                rs.getCampos().get("CANTIDAD").toString());
                muestraDialogo = true;
                return;
            }
            else {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1412"));

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFecha() {
        // NO ESTï¿½ IMPLEMENTADO
        cargarListaPaquete();
    }

    public void cambiardialogoConfirmar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptardialogoConfirmar() {
        // <CODIGO_DESARROLLADO>
        try {
            JsfUtil.agregarMensajeInformativoDialogo(ejbServPubUno
                            .borrarRecPagos(compania, fecha, banco, paquete));

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
            muestraDialogo = false;
        }
        catch (SystemException e) {
            Logger.getLogger(BorraRegPagosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        banco = registroAux.getCampos().get("CODIGO").toString();
        nombreBanco = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaPaquete();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getPaquete() {
        return paquete;
    }

    public void setPaquete(String paquete) {
        this.paquete = paquete;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public boolean isMuestraDialogo() {
        return muestraDialogo;
    }

    public void setMuestraDialogo(boolean muestraDialogo) {
        this.muestraDialogo = muestraDialogo;
    }

    public String getTituloDialogo() {
        return tituloDialogo;
    }

    public void setTituloDialogo(String tituloDialogo) {
        this.tituloDialogo = tituloDialogo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaPaquete() {
        return listaPaquete;
    }

    public void setListaPaquete(List<Registro> listaPaquete) {
        this.listaPaquete = listaPaquete;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
