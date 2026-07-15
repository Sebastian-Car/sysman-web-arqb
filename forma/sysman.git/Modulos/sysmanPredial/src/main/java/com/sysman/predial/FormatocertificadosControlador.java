package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author acaceres
 * @version 1, 24/05/2016
 * 
 * @version 2, 29/06/2017
 * @author jreina se realizaron los cambios de refactoring en el origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class FormatocertificadosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private String accion;

    /**
     * Creates a new instance of FormatocertificadosControlador
     */
    public FormatocertificadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FORMATOCERTIFICADOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FormatocertificadosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() { 
        enumBase= GenericUrlEnum.IP_FORMATO_CERTIFICADOS;
        buscarLlave();
        reasignarOrigen();      
        registro = new Registro();         
        abrirFormulario();       
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
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
        /*
         * FR778-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * '(TAR:1000057765; FECHA:04/11/2015; AUTOR: AA) 'Para que se
         * tengan en cuenta los permisos configurados desde la opción
         * usuarios/ grupos formularioAbrir 60, Me.Name DoCmd.Restore
         * End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        accion="i";
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
        if("i".equals(accion)){
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        }else{
            registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        }
        accion="m";
        if ((registro.getCampos().get(GeneralParameterEnum.FORMATO.getName()) == null)
                        || "".equals(registro.getCampos().get(GeneralParameterEnum.FORMATO.getName()))
                        || (registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()) == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB521"));
            return false;
        }
        if ("".equals(registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()))
                        || (registro.getCampos().get("MODELORESOLUCION") == null)
                        || "".equals(registro.getCampos().get("MODELORESOLUCION"))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB521"));
            return false;
        }
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
        // NO SE IMPLEMENTA
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }  
}
