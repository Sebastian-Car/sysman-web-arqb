package com.sysman.predial;

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
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.AnularregistroControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author dsuesca
 * @version 1, 24/05/2016
 * 
 * @author eamaya
 * @version 1.1, 13/06/2017 Se cambió el llamado del código del
 * formulario
 * 
 * @version 2, 22/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class AnularregistroControlador extends BeanBaseModal {
    
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String numFactura;
    private String nombrePredio;
    private Date fechaPago;
    private String codPredio;
    private boolean dialogoVisible;
    private String textoEtiqueta;
    private StreamedContent archivoDescarga;
    private String pagBan;
    private String paquete;
    private String acuerdo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTxtRecibo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    
    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatroRemote;

    /**
     * Creates a new instance of AnularregistroControlador
     */
    public AnularregistroControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ANULARREGISTRO_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AnularregistroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTxtRecibo();
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTxtRecibo() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AnularregistroControladorUrlEnum.URL3473.getValue());         
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaTxtRecibo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "DOCNUM");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAnular() {
        // <CODIGO_DESARROLLADO>
        textoEtiqueta = idioma.getString("TB_TB1038");
        textoEtiqueta = textoEtiqueta.replace("s$factura$s", numFactura);
        textoEtiqueta = textoEtiqueta.replace("s$predio$s", codPredio);
        dialogoVisible = true;

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    public void aceptarDGPR() {
        String rta = "";
        try {

            rta = ejbPredialCuatroRemote.getAnularRegistroPago(compania,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL, numFactura,
                            codPredio, pagBan, paquete, fechaPago, acuerdo,
                            SessionUtil.getUser().getCodigo());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ("1".equals(rta)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1049"));
        }
        else {
            JsfUtil.agregarMensajeInformativo(rta);
        }
        cargarListaTxtRecibo();
        numFactura = null;
        fechaPago=null;
        codPredio=null;
        nombrePredio=null;
        dialogoVisible = false;

    }

    public void cancelarDGPR() {
        dialogoVisible = false;

    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTxtRecibo(SelectEvent event) {
        try {
            Registro registroAux = (Registro) event.getObject();
            numFactura = registroAux.getCampos().get("DOCNUM").toString();
            pagBan = registroAux.getCampos().get("PAG_BANPAG").toString();
            paquete = registroAux.getCampos().get("PAQUETEPAG").toString();
            acuerdo = registroAux.getCampos().get("ACUERDO").toString();
            fechaPago = SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                            "PREFECPAG") ? null
                                : ((Date) registroAux.getCampos().get("PREFECPAG"));
            codPredio = registroAux.getCampos().get("PRECOD").toString();

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), codPredio);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnularregistroControladorUrlEnum.URL6076
                                                            .getValue())
                                            .getUrl(), param));

            if ((rs != null) && (rs.getCampos().get("NOMBRE") != null)) {
                nombrePredio = rs.getCampos().get("NOMBRE").toString();
            }
            else {
                nombrePredio = null;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getNombrePredio() {
        return nombrePredio;
    }

    public String getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }

    public void setNombrePredio(String nombrePredio) {
        this.nombrePredio = nombrePredio;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(String codPredio) {
        this.codPredio = codPredio;
    }

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    public String getTextoEtiqueta() {
        return textoEtiqueta;
    }

    public void setTextoEtiqueta(String textoEtiqueta) {
        this.textoEtiqueta = textoEtiqueta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getPagBan() {
        return pagBan;
    }

    public void setPagBan(String pagBan) {
        this.pagBan = pagBan;
    }

    public String getPaquete() {
        return paquete;
    }

    public void setPaquete(String paquete) {
        this.paquete = paquete;
    }


    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTxtRecibo() {
        return listaTxtRecibo;
    }
    
    public void setListaTxtRecibo(RegistroDataModelImpl listaTxtRecibo) {
        this.listaTxtRecibo = listaTxtRecibo;
    }
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
