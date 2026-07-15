package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroRemote;
import com.sysman.bancoproyectos.enums.FrmdescindicadoresControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

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

/**
 *
 * @author lcortes
 * @version 1, 18/09/2015
 * 
 * @version 2, 14/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class FrmdescindicadoresControlador extends BeanBaseModal {

    private final String compania;
    private List<Registro> listaVigenciaIncial;
    private Registro registro;
    
    @EJB
    private EjbBancoProyectoCeroRemote ejbBancoProyectoCero;

    /**
     * Creates a new instance of FrmdescindicadoresControlador
     */
    public FrmdescindicadoresControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRMDESCINDICADORES_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(FrmdescindicadoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaVigenciaIncial();
        abrirFormulario();
    }

    public void cargarListaVigenciaIncial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaVigenciaIncial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdescindicadoresControladorUrlEnum.URL2066
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
        if (registro.getCampos().get("NUMERO") == (null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2304"));
            return;
        }
        try {

            ejbBancoProyectoCero.actualizarDescripcionPlanIndi(compania,
                            Integer.parseInt(registro.getCampos().get("NUMERO")
                                            .toString()),
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2306"));
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(FrmdescindicadoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        //HEREDADO DEL BEAN BASE      
    }

    public List<Registro> getListaVigenciaIncial() {
        return listaVigenciaIncial;
    }

    public void setListaVigenciaIncial(List<Registro> listaVigenciaIncial) {
        this.listaVigenciaIncial = listaVigenciaIncial;
    }

     public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }
}
