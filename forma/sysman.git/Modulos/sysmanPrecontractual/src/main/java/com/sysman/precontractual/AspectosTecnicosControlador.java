package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.AspectosTecnicosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 23/11/2015
 * 
 * @version 2, 22/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos y en el origen de grilla.
 */

@ManagedBean
@ViewScoped
public class AspectosTecnicosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String consCodigoAspecto;
    private String tipoAspectoTecnico;
    private String codigoTipoAspecto;
    private RegistroDataModelImpl listaTipoAspectoTecnico;
    private RegistroDataModelImpl listaTipoAspectoTecnicoE;
    private String auxiliar;

    /**
     * Creates a new instance of AspectosTecnicosControlador
     */
    public AspectosTecnicosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCodigoAspecto="COD_T_ASPTEC";
        try {
            numFormulario = GeneralCodigoFormaEnum.ASPECTOS_TECNICOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase= GenericUrlEnum.ES_ASP_TECN;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaTipoAspectoTecnico();
        cargarListaTipoAspectoTecnicoE();
        abrirFormulario();
    }
    
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigoTipoAspecto);
    }
    
    @Override
    public void abrirFormulario() {
      //METODO NO IMPLEMENTADO
    }

   
    public void cargarListaTipoAspectoTecnico() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AspectosTecnicosControladorUrlEnum.URL3351.getValue());       
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaTipoAspectoTecnico = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, consCodigoAspecto);
    }

    public void cargarListaTipoAspectoTecnicoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AspectosTecnicosControladorUrlEnum.URL3351.getValue());       
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaTipoAspectoTecnicoE = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, consCodigoAspecto);
    }

    public void cambiarTipoAspectoTecnico() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoAspectoTecnico(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoAspectoTecnico =  registroAux.getCampos().get("NOMBRE").toString();
        codigoTipoAspecto = registroAux.getCampos()
                        .get(consCodigoAspecto).toString();
        reasignarOrigen();
        cargado = false;
    }



    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public void removerCombos() {
        //METODO NO IMPLEMENTADO
        registro.getCampos().remove(consCodigoAspecto);
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(consCodigoAspecto, codigoTipoAspecto);
        if(codigoTipoAspecto==null){
           JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3476"));
           return false;
        }
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {    
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
    
    
    @Override
    public void asignarValoresRegistro() {
        //METODO NO IMPLEMENTADO
    }
    
    public RegistroDataModelImpl getListaTipoAspectoTecnico() {
        return listaTipoAspectoTecnico;
    }

    public void setListaTipoAspectoTecnico(
        RegistroDataModelImpl listaTipoAspectoTecnico) {
        this.listaTipoAspectoTecnico = listaTipoAspectoTecnico;
    }

    public RegistroDataModelImpl getListaTipoAspectoTecnicoE() {
        return listaTipoAspectoTecnicoE;
    }

    public void setListaTipoAspectoTecnicoE(
        RegistroDataModelImpl listaTipoAspectoTecnicoE) {
        this.listaTipoAspectoTecnicoE = listaTipoAspectoTecnicoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getTipoAspectoTecnico() {
        return tipoAspectoTecnico;
    }

    public void setTipoAspectoTecnico(String tipoAspectoTecnico) {
        this.tipoAspectoTecnico = tipoAspectoTecnico;
    }

}
