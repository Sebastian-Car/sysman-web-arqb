package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmfuentesfinanciacionsControladorEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

/**
 *
 * @author lcortes
 * @version 1, 13/08/2015
 * 
 * @version 2, 14/09/2017, <strong>pespitia</strong>
 * <li>Se reemplazaron los numeros de los formularios por su
 * correspondiente enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmfuentesfinanciacionsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String moduloBancos;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOFUENTE</code>
     */
    private final String cCodigoFuente;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ORDENLB</code>
     */
    private final String cOrdenLb;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>fuente</code>
     */
    private final String cFuente;

    public FrmfuentesfinanciacionsControlador() {
        super();

        compania = SessionUtil.getCompania();
        moduloBancos = SessionUtil.getModulo();

        cCompania = GeneralParameterEnum.COMPANIA.getName();

        cCodigoFuente = FrmfuentesfinanciacionsControladorEnum.CODIGOFUENTE
                        .getValue();

        cOrdenLb = FrmfuentesfinanciacionsControladorEnum.ORDENLB.getValue();
        cFuente = FrmfuentesfinanciacionsControladorEnum.FUENTE.getValue();

        try {
            // 117
            numFormulario = GeneralCodigoFormaEnum.FRMFUENTESFINANCIACIONS_CONTROLADOR
                            .getCodigo();

            registro = new Registro(new HashMap<String, Object>());

            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();

            Logger.getLogger(FrmfuentesfinanciacionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.FUENTESFINANCIACION;

        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSubFuente(ActionEvent ac) {
        agregarRegistroNuevo(false);

        String codigoFuente = registro.getCampos().get(cCodigoFuente)
                        .toString();

        if (SysmanFunciones.validarVariableVacio(codigoFuente)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2412"));
            return;
        }

        String[] campos = { cFuente };
        String[] valores = { codigoFuente };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMSUBFUENTESFINANCIACIONS_CONTROLADOR
                                                        .getCodigo()),
                        moduloBancos, campos,
                        valores);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();

    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(cCompania, compania);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        if (css != null) {
            registro.getCampos().remove(cCompania);
            registro.getCampos().remove(cOrdenLb);
        }

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }
}
