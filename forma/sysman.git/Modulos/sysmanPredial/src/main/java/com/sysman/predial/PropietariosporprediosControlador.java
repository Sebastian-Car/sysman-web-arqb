package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.PropietariosporprediosControladorEnum;
import com.sysman.predial.enums.PropietariosporprediosControladorUrlEnum;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author sdaza
 * @version 1, 07/06/2016
 * @version , 13/03/2017 sdaza. Se valida segun la opcion de menu la
 * consulta de origen de datos
 * 
 * @version 2, 14/07/2017
 * @author jreina se realizaron los cambios de refactoring en el origen de grilla.
 */

@ManagedBean
@ViewScoped
public class PropietariosporprediosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private String nomPropietario;
    private String codigoPredio;
    private String direccionPredio;
    private String estadoPredio;
    private String numeroOrden;
    private String nit;
    private Map<String, Object> parametrosEntrada;

    /**
     * Creates a new instance of PropietariosporprediosControlador
     */

    public PropietariosporprediosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PROPIETARIOSPORPREDIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                
                codigoPredio = parametrosEntrada.get("codigoPredio").toString();
                numeroOrden = parametrosEntrada.get("nroOrden").toString();
                nomPropietario = parametrosEntrada
                                .get("nomPropietario").toString();
                direccionPredio = parametrosEntrada
                                .get("direccionPredio").toString();
                estadoPredio = parametrosEntrada.get("estadoPredio").toString();
                
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PropietariosporprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = PropietariosporprediosControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        if ("600101".equals(SessionUtil.getMenuActual())) {
            urlListado=UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            PropietariosporprediosControladorUrlEnum.URL15084.getValue());
        }
        else {
            urlListado=UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            PropietariosporprediosControladorUrlEnum.URL11023.getValue());
        }
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(PropietariosporprediosControladorEnum.PARAM0.getValue(), numeroOrden);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
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

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.USUARIOSPREDIALS_CONTROLADOR
                        .getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getNomPropietario() {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario) {
        this.nomPropietario = nomPropietario;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public String getDireccionPredio() {
        return direccionPredio;
    }

    public void setDireccionPredio(String direccionPredio) {
        this.direccionPredio = direccionPredio;
    }

    public String getEstadoPredio() {
        return estadoPredio;
    }

    public void setEstadoPredio(String estadoPredio) {
        this.estadoPredio = estadoPredio;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }
}
