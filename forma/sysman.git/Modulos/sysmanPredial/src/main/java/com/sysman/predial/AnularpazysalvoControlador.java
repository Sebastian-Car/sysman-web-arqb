package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.AnularpazysalvoControladorEnum;
import com.sysman.predial.enums.AnularpazysalvoControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 20/05/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @version 3, 22/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 */

@ManagedBean
@ViewScoped
public class AnularpazysalvoControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String recibo;
    private String fechaPago;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listanrorecibo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    

    /**
     * Creates a new instance of AnularpazysalvoControlador
     */
    public AnularpazysalvoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ANULARPAZYSALVO_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(AnularpazysalvoControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListanrorecibo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListanrorecibo(){
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AnularpazysalvoControladorUrlEnum.URL2708.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listanrorecibo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param, true, "NUMCER");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAnular() {
        // <CODIGO_DESARROLLADO>
        try {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.ANULADO.getName(),
                            SessionUtil.getUser().getCodigo());
            parametros.put(AnularpazysalvoControladorEnum.PARAM0.getValue(),
                            recibo);
            Parameter parameter = new Parameter();
            parameter.setFields(parametros);
            
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AnularpazysalvoControladorUrlEnum.URL3711
                                                            .getValue());
            int rta = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
            if (rta > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1069"));
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1070"));
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(AnularpazysalvoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilanrorecibo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        recibo = registroAux.getCampos().get("NUMCER").toString();
        fechaPago = registroAux.getCampos().get("FECHA_EXP").toString();

    }

    // </METODOS_COMBOS_GRANDES>



    // <SET_GET_ATRIBUTOS>
    public String getRecibo()
    {
        return recibo;
    }

    public void setRecibo(String recibo)
    {
        this.recibo = recibo;
    }

    public String getFechaPago()
    {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago)
    {
        this.fechaPago = fechaPago;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListanrorecibo() {
        return listanrorecibo;
    }
    
    public void setListanrorecibo(RegistroDataModelImpl listanrorecibo) {
        this.listanrorecibo = listanrorecibo;
    }
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
