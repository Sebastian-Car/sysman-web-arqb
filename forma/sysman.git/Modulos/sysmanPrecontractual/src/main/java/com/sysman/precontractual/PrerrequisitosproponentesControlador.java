package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.PrerrequisitosproponentesControladorEnum;
import com.sysman.precontractual.enums.PrerrequisitosproponentesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author lcortes
 * @version 1, 28/03/2016
 * 
 * @version 2, 04/09/2017, <strong>pespitia<strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Se reemplazo la creacion de conexiones a ConectorPool por el
 * esquema.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class PrerrequisitosproponentesControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CONSECUTIVODETALLE</code>
     */
    private final String cConsecutivoDetalle;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPOCONTRATO</code>
     */
    private final String cTipoContrato;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TRANSACCION</code>
     */
    private final String cTransaccion;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPO</code>
     */
    private final String cTipo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>PROPONENTE</code>
     */
    private final String cProponente;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL</code>
     */
    private final String cSucursal;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VALOR</code>
     */
    private final String cValor;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VALOR1</code>
     */
    private final String cValorUno;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VALOR2</code>
     */
    private final String cValorDos;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VALORFORMATO</code>
     */
    private final String cValorFormato;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRE</code>
     */
    private final String cNombre;

    private String tituloEtapa;
    private String tituloProponente;
    private String auxiliar;
    private String tipoContrato;
    private String consecutivo;
    private String transaccion;
    private String proponente;
    private String nombreProponente;
    private String sucursal;
    private String idEtapa;
    private String nombreEtapa;
    private String estadoEtapa;
    private String estadoProponente;

    /** Atributo que contiene el estado del anio: {A, C} */
    private String estadoVigencia;

    /**
     * Atributo que controla la opcion de actualización del formulario
     */
    private boolean verUpdate;

    /**
     * Creates a new instance of PrerrequisitosproponentesControlador
     */
    public PrerrequisitosproponentesControlador() {
        super();

        // 595
        numFormulario = GeneralCodigoFormaEnum.PRERREQUISITOSPROPONENTES_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();

        cCompania = GeneralParameterEnum.COMPANIA.getName();

        cConsecutivoDetalle = PrerrequisitosproponentesControladorEnum.CONSECUTIVODETALLE
                        .getValue();

        cTipoContrato = GeneralParameterEnum.TIPOCONTRATO.getName();

        cTransaccion = PrerrequisitosproponentesControladorEnum.TRANSACCION
                        .getValue();

        cTipo = PrerrequisitosproponentesControladorEnum.TIPO.getValue();

        cProponente = PrerrequisitosproponentesControladorEnum.PROPONENTE
                        .getValue();

        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        cValor = GeneralParameterEnum.VALOR.getName();
        cValorUno = PrerrequisitosproponentesControladorEnum.VALOR1.getValue();
        cValorDos = PrerrequisitosproponentesControladorEnum.VALOR2.getValue();

        cValorFormato = PrerrequisitosproponentesControladorEnum.VALORFORMATO
                        .getValue();

        cNombre = GeneralParameterEnum.NOMBRE.getName();

        Map<String, Object> paramEntrada = SessionUtil.getFlash();

        try {
            tipoContrato = paramEntrada.get("tipoContrato").toString();
            transaccion = paramEntrada.get("consecutivoTransaccion").toString();
            consecutivo = paramEntrada.get("consecutivoDetalle").toString();
            proponente = paramEntrada.get("proponente").toString();
            nombreProponente = paramEntrada.get("nombreProponente").toString();
            sucursal = paramEntrada.get("sucursal").toString();
            idEtapa = paramEntrada.get("idEtapa").toString();
            nombreEtapa = paramEntrada.get("nombreEtapa").toString();

            verUpdate = Boolean
                            .valueOf(paramEntrada.get("modificar").toString());

            estadoEtapa = paramEntrada.get("estadoEtapa").toString();
            estadoProponente = paramEntrada.get("estadoProponente").toString();
            estadoVigencia = paramEntrada.get("estadoVigencia").toString();

            estadoEtapa = estadoEtapa == null ? "NEE" : estadoEtapa;

            estadoProponente = estadoProponente == null ? "NEP"
                : estadoProponente;

            tituloEtapa = SysmanFunciones.concatenar("ETAPA ", idEtapa, "- ",
                            nombreEtapa.toUpperCase());

            tituloProponente = SysmanFunciones.concatenar("PROPONENTE: ",
                            proponente, " - ", nombreProponente);

            if (verUpdate) {
                verUpdate = "A".equals(estadoVigencia)
                    && "A".equals(estadoEtapa)
                    && !"RE".equals(estadoProponente);
            }

            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PRERREQUISITOS_PROPONENTE;
        registro = new Registro();

        buscarLlave();
        reasignarOrigen();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cTipoContrato, tipoContrato);
        parametrosListado.put(cTransaccion, transaccion);
        parametrosListado.put(cConsecutivoDetalle, consecutivo);
        parametrosListado.put(cProponente, proponente);

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(cSucursal, sucursal);
            param.put(cCompania, compania);
            param.put(cTipoContrato, tipoContrato);
            param.put(cTransaccion, transaccion);
            param.put(cConsecutivoDetalle, consecutivo);
            param.put(cProponente, proponente);
            param.put("CREATED_BY", usuario);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PrerrequisitosproponentesControladorUrlEnum.URL9992
                                                            .getValue());

            int res = requestManager.saveCount(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            param);

            if (res != 0) {
                reasignarOrigen();
            }
        }
        catch (SystemException e) {
            Logger.getLogger(PrerrequisitosproponentesControlador.class
                            .getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cTipoContrato);
        registro.getCampos().remove(cTransaccion);
        registro.getCampos().remove(cConsecutivoDetalle);
        registro.getCampos().remove(cProponente);
        registro.getCampos().remove(cSucursal);
        registro.getCampos().remove(cValor);
        registro.getCampos().remove(cTipo);
        registro.getCampos().remove(cValorUno);
        registro.getCampos().remove(cValorDos);
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove(cValorFormato);
        registro.getCampos().remove("CODIGO");
        // </CODIGO_DESARROLLADO>
        return true;
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

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getTituloEtapa() {
        return tituloEtapa;
    }

    public void setTituloEtapa(String tituloEtapa) {
        this.tituloEtapa = tituloEtapa;
    }

    public String getTituloProponente() {
        return tituloProponente;
    }

    public void setTituloProponente(String tituloProponente) {
        this.tituloProponente = tituloProponente;
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public boolean isVerUpdate() {
        return verUpdate;
    }

    public void setVerUpdate(boolean verUpdate) {
        this.verUpdate = verUpdate;
    }
}
