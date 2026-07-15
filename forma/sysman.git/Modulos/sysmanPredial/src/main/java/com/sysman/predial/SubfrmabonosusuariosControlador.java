package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.enums.SubfrmabonosusuariosControladorEnum;
import com.sysman.predial.enums.SubfrmabonosusuariosControladorUrlEnum;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author sdaza
 * @version 1, 29/06/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 * 
 * @version 3, 18/07/2017
 * @author jreina se realizaron los cambios de refactoring en el origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class SubfrmabonosusuariosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private Map<String, Object> rid;
    private String direccionPredio;
    private String nomPropietario;
    private String codigoPredio;
    private String nomC1;
    private String nomC2;
    private String nomC3;
    private String nomC4;
    private String nomC13;
    private String nomC14;
    private String nomC15;
    private String nomC16;
    private String nomC17;
    private String nomC18;
    private String nomC19;
    private String nomC20;
    
    @EJB
    private EjbPredialCeroRemote ejbPredialCero;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

private Map<String, Object> parametrosEntrada;

    /**
     * Creates a new instance of SubfrmabonosusuariosControlador
     */
    @SuppressWarnings("unchecked")
    public SubfrmabonosusuariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBFRMABONOSUSUARIOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                codigoPredio = parametrosEntrada.get("codigoPredio").toString();
                nomPropietario = parametrosEntrada
                                .get("nomPropietario").toString();
                direccionPredio = parametrosEntrada
                                .get("direccionPredio").toString();
            }
            else {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SubfrmabonosusuariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
            tabla = SubfrmabonosusuariosControladorEnum.TABLA.getValue();
            buscarLlave();
            reasignarOrigen();
            registro = new Registro();
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
      
    }

    @Override
    public void reasignarOrigen() {
        urlListado=UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SubfrmabonosusuariosControladorUrlEnum.URL4586.getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
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
        try {
            nomC1 = obtenerNombre(1);
            nomC2 = obtenerNombre(2);
            nomC3 = obtenerNombre(3);
            nomC4 = obtenerNombre(4);
            nomC13 = obtenerNombre(13);
            nomC14 = obtenerNombre(14);
            nomC15 = obtenerNombre(15);
            nomC16 = obtenerNombre(16);
            nomC17 = obtenerNombre(17);
            nomC18 = obtenerNombre(18);
            nomC19 = obtenerNombre(19);
            nomC20 = obtenerNombre(20);
        }
        catch (NamingException | SQLException | ClassNotFoundException
                        | InstantiationException | IllegalAccessException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(PrediallistpropieporprediosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    private String obtenerNombre(int numero)
                    throws IllegalAccessException, InstantiationException,
                    ClassNotFoundException, SQLException, NamingException, SystemException {
        return ejbPredialCero.consultarEncabezadoDeColumna(compania, numero);
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
        // Actualmente no se requiere remover ningun combo.
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametrosEntrada);
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.USUARIOSPREDIALS_CONTROLADOR.getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // Actualmente no se requiere asignar ningun valor.
    }

    // <SET_GET_ATRIBUTOS>
    public String getDireccionPredio() {
        return direccionPredio;
    }

    public void setDireccionPredio(String direccionPredio) {
        this.direccionPredio = direccionPredio;
    }

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

    public String getNomC1() {
        return nomC1;
    }

    public void setNomC1(String nomC1) {
        this.nomC1 = nomC1;
    }

    public String getNomC2() {
        return nomC2;
    }

    public void setNomC2(String nomC2) {
        this.nomC2 = nomC2;
    }

    public String getNomC3() {
        return nomC3;
    }

    public void setNomC3(String nomC3) {
        this.nomC3 = nomC3;
    }

    public String getNomC4() {
        return nomC4;
    }

    public void setNomC4(String nomC4) {
        this.nomC4 = nomC4;
    }

    public String getNomC13() {
        return nomC13;
    }

    public void setNomC13(String nomC13) {
        this.nomC13 = nomC13;
    }

    public String getNomC14() {
        return nomC14;
    }

    public void setNomC14(String nomC14) {
        this.nomC14 = nomC14;
    }

    public String getNomC15() {
        return nomC15;
    }

    public void setNomC15(String nomC15) {
        this.nomC15 = nomC15;
    }

    public String getNomC16() {
        return nomC16;
    }

    public void setNomC16(String nomC16) {
        this.nomC16 = nomC16;
    }

    public String getNomC17() {
        return nomC17;
    }

    public void setNomC17(String nomC17) {
        this.nomC17 = nomC17;
    }

    public String getNomC18() {
        return nomC18;
    }

    public void setNomC18(String nomC18) {
        this.nomC18 = nomC18;
    }

    public String getNomC19() {
        return nomC19;
    }

    public void setNomC19(String nomC19) {
        this.nomC19 = nomC19;
    }

    public String getNomC20() {
        return nomC20;
    }

    public void setNomC20(String nomC20) {
        this.nomC20 = nomC20;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

}
