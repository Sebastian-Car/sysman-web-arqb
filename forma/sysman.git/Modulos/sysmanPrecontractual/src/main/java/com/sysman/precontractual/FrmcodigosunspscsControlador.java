package com.sysman.precontractual;

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
import com.sysman.precontractual.enums.FrmcodigosunspscsControladorEnum;
import com.sysman.precontractual.enums.FrmcodigosunspscsControladorUrlEnum;

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
 * @version 1, 24/08/2016
 * 
 * @version 2, 25/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazo el numero del formulario por enumerado.<br>
 * Refactoring de conexiones por {@code ConectorPool.ESQUEMA_SYSMAN}.
 * <br>
 * Refactoring de sentencias SQL.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmcodigosunspscsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>NIVEL</code>
     */
    private final String cNivel;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>NOMBRENIVEL</code>
     */
    private final String cNombreNivel;

    // <DECLARAR_LISTAS>
    /** Lista que contiene los items del combo <code>Nivel</code> */
    private List<Registro> listacmbNivel;
    // </DECLARAR_LISTAS>

    /**
     * Creates a new instance of FrmcodigosunspscsControlador
     */
    public FrmcodigosunspscsControlador() {
        super();

        compania = SessionUtil.getCompania();

        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cNivel = FrmcodigosunspscsControladorEnum.NIVEL.getValue();
        cNombreNivel = FrmcodigosunspscsControladorEnum.NOMBRENIVEL.getValue();

        try {
            // 1058
            numFormulario = GeneralCodigoFormaEnum.FRMCODIGOSUNSPSCS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmcodigosunspscsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CODIGOS_UNSPSC;
        registro = new Registro();

        buscarLlave();
        reasignarOrigen();
        // <CARGAR_LISTA>
        cargarListacmbNivel();
        // </CARGAR_LISTA>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    public void oprimirbtnActualizar() {

        String[] campos = { "opcion" };

        Object[] valores = { SessionUtil.getMenuActual() };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(1813),
                        SessionUtil.getModulo(), campos, valores);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: {@code listacmbNivel} asociada al combo Nivel.
     */
    public void cargarListacmbNivel() {
        Map<String, Object> param = new TreeMap<>();

        try {
            listacmbNivel = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcodigosunspscsControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    // </METODOS_CARGAR_LISTA>

    public void cambiarcmbNivel() {
        // <CODIGO_DESARROLLADO>
        String nivel = (String) registro.getCampos().get(cNivel);

        String nombreNivel = service.buscarEnLista(nivel, cNivel, cNombreNivel,
                        listacmbNivel);

        registro.getCampos().put("NOMBRE_NIVEL", nombreNivel);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbNivelC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // <CODIGO_DESARROLLADO>
        String nivel = (String) listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cNivel);

        String nombreNivel = service.buscarEnLista(nivel, cNivel, cNombreNivel,
                        listacmbNivel);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("NOMBRE_NIVEL", nombreNivel);
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().put(cCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
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
        registro.getCampos().remove(cCompania);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListacmbNivel() {
        return listacmbNivel;
    }

    public void setListacmbNivel(List<Registro> listacmbNivel) {
        this.listacmbNivel = listacmbNivel;
    }
}
