package com.sysman.almacen;

import com.sysman.almacen.enums.SubinventcontabilidadsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

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
 * @author ngomez
 * @version 1, 28/10/2015
 * 
 * @version 2, 05/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos.
 */
@ManagedBean
@ViewScoped
public class SubinventcontabilidadsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private RegistroDataModelImpl listaAjusInflaDebito;
    private RegistroDataModelImpl listaAjusInflaCredito;
    private RegistroDataModelImpl listaDeprecDebito;
    private RegistroDataModelImpl listaDeprecCredito;
    private RegistroDataModelImpl listaAjusDeprecDebito;
    private RegistroDataModelImpl listaAjusDeprecCredito;
    private RegistroDataModelImpl listaCostoSalDb;
    private RegistroDataModelImpl listaCostoSalCr;
    private RegistroDataModelImpl listaCostoSalAjDb;
    private RegistroDataModelImpl listaCostoSalAjCr;
    private RegistroDataModelImpl listaDepAcumuladaDb;
    private RegistroDataModelImpl listaDepAcumuladaCr;
    private RegistroDataModelImpl listaAjusteDepreciacionDb;
    private RegistroDataModelImpl listaAjusteDepreciacionCr;
    private RegistroDataModelImpl listaAjusInflaDebitoS;
    private RegistroDataModelImpl listaAjusInflaCreditoS;
    private RegistroDataModelImpl listaDeprecDebitoS;
    private RegistroDataModelImpl listaDeprecCreditoS;
    private RegistroDataModelImpl listaAjusDeprecDebitoS;
    private RegistroDataModelImpl listaAjusDeprecCreditoS;
    private RegistroDataModelImpl listaDEPRECDEBITOSCOMODATO;
    private RegistroDataModelImpl listaDEPRECCREDITOSCOMODATO;
    private String anio;
    private String codigo;
    private String nombre;
    private UrlBean urlBean;
    private Map<String,Object> param;

    public SubinventcontabilidadsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBINVENTCONTABILIDADS_CONTROLADOR.getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("ridR");
                codigo =  parametrosEntrada.get("codigo").toString();
                anio =  parametrosEntrada.get("anio").toString();
                rid.put("KEY_ANO", anio);
                nombre = parametrosEntrada.get("nombre").toString();
            }
            SessionUtil.cleanFlash();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(SubinventcontabilidadsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase= GenericUrlEnum.INVENTARIOCONTABILIDAD;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
    }

   
    @Override
    public void iniciarListas() {
        cargarServicio();
        cargarListaAjusInflaDebito();
        cargarListaAjusInflaCredito();
        cargarListaDeprecDebito();
        cargarListaDeprecCredito();
        cargarListaAjusDeprecDebito();
        cargarListaAjusDeprecCredito();
        cargarListaCostoSalDb();
        cargarListaCostoSalCr();
        cargarListaCostoSalAjDb();
        cargarListaCostoSalAjCr();
        cargarListaDepAcumuladaDb();
        cargarListaDepAcumuladaCr();
        cargarListaAjusteDepreciacionDb();
        cargarListaAjusteDepreciacionCr();
        cargarListaAjusInflaDebitoS();
        cargarListaAjusInflaCreditoS();
        cargarListaDeprecDebitoS();
        cargarListaDeprecCreditoS();
        cargarListaAjusDeprecDebitoS();
        cargarListaAjusDeprecCreditoS();
        cargarListaDEPRECDEBITOSCOMODATO();
        cargarListaDEPRECCREDITOSCOMODATO();
    }
    
    public void cargarServicio(){
        urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubinventcontabilidadsControladorUrlEnum.URL12222.getValue());         
        param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);
    }

    @Override
    public void iniciarListasSub() {
        // Metodo heredado
    }

    @Override
    public void iniciarListasSubNulo() {
        // Metodo heredado

    }

    public void cargarListaAjusInflaDebito() {
        listaAjusInflaDebito = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusInflaCredito() {
        listaAjusInflaCredito = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaDeprecDebito() {
        listaDeprecDebito = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaDeprecCredito() {
        listaDeprecCredito = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusDeprecDebito() {
        listaAjusDeprecDebito = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusDeprecCredito() {
        listaAjusDeprecCredito = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaCostoSalDb() {
        listaCostoSalDb = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaCostoSalCr() {
        listaCostoSalCr = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaCostoSalAjDb() {
        listaCostoSalAjDb = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaCostoSalAjCr() {
        listaCostoSalAjCr = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaDepAcumuladaDb() {
        listaDepAcumuladaDb = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaDepAcumuladaCr() {
        listaDepAcumuladaCr = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusteDepreciacionDb() {
        listaAjusteDepreciacionDb = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusteDepreciacionCr() {
        listaAjusteDepreciacionCr = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusInflaDebitoS() {
        listaAjusInflaDebitoS = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusInflaCreditoS() {
        listaAjusInflaCreditoS = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaDeprecDebitoS() {
        listaDeprecDebitoS = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaDeprecCreditoS() {
        listaDeprecCreditoS = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusDeprecDebitoS() {
        listaAjusDeprecDebitoS = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaAjusDeprecCreditoS() {
        listaAjusDeprecCreditoS = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaDEPRECDEBITOSCOMODATO() {
        listaDEPRECDEBITOSCOMODATO = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void cargarListaDEPRECCREDITOSCOMODATO() {
        listaDEPRECCREDITOSCOMODATO = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "ID");
    }

    public void seleccionarFilaAjusInflaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSINFLADEBITO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusInflaCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSINFLACREDITO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaDeprecDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECDEBITO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaDeprecCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECCREDITO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusDeprecDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSDEPRECDEBITO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusDeprecCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSDEPRECCREDITO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCostoSalDb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COSTOSALDB",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCostoSalCr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COSTOSALCR",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCostoSalAjDb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COSTOSALAJDB",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCostoSalAjCr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COSTOSALAJCR",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaDepAcumuladaDb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPACUMULADADB",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaDepAcumuladaCr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPACUMULADACR",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusteDepreciacionDb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSTEDEPRECIACIONDB",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusteDepreciacionCr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSTEDEPRECIACIONCR",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusInflaDebitoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSINFLADEBITOS",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusInflaCreditoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSINFLACREDITOS",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaDeprecDebitoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECDEBITOS",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaDeprecCreditoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECCREDITOS",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusDeprecDebitoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSDEPRECDEBITOS",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaAjusDeprecCreditoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSDEPRECCREDITOS",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaDEPRECDEBITOSCOMODATO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECDEBITOSCOMODATO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaDEPRECCREDITOSCOMODATO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECCREDITOSCOMODATO",
                        registroAux.getCampos().get("ID"));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>ç
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put("CODIGOELEMENTO", codigo);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        actualizarAntes();
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

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        Object[] valores = { rid };
        SessionUtil.redireccionar("/inventario.sysman", campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }


    
    public RegistroDataModelImpl getListaAjusInflaDebito() {
        return listaAjusInflaDebito;
    }

    public void setListaAjusInflaDebito(
        RegistroDataModelImpl listaAjusInflaDebito) {
        this.listaAjusInflaDebito = listaAjusInflaDebito;
    }

    

    public RegistroDataModelImpl getListaAjusInflaCredito() {
        return listaAjusInflaCredito;
    }

    public void setListaAjusInflaCredito(
        RegistroDataModelImpl listaAjusInflaCredito) {
        this.listaAjusInflaCredito = listaAjusInflaCredito;
    }

    

    public RegistroDataModelImpl getListaDeprecDebito() {
        return listaDeprecDebito;
    }

    public void setListaDeprecDebito(RegistroDataModelImpl listaDeprecDebito) {
        this.listaDeprecDebito = listaDeprecDebito;
    }

    

    public RegistroDataModelImpl getListaDeprecCredito() {
        return listaDeprecCredito;
    }

    public void setListaDeprecCredito(RegistroDataModelImpl listaDeprecCredito) {
        this.listaDeprecCredito = listaDeprecCredito;
    }

    

    public RegistroDataModelImpl getListaAjusDeprecDebito() {
        return listaAjusDeprecDebito;
    }

    public void setListaAjusDeprecDebito(
        RegistroDataModelImpl listaAjusDeprecDebito) {
        this.listaAjusDeprecDebito = listaAjusDeprecDebito;
    }

    

    public RegistroDataModelImpl getListaAjusDeprecCredito() {
        return listaAjusDeprecCredito;
    }

    public void setListaAjusDeprecCredito(
        RegistroDataModelImpl listaAjusDeprecCredito) {
        this.listaAjusDeprecCredito = listaAjusDeprecCredito;
    }

    

    public RegistroDataModelImpl getListaCostoSalDb() {
        return listaCostoSalDb;
    }

    public void setListaCostoSalDb(RegistroDataModelImpl listaCostoSalDb) {
        this.listaCostoSalDb = listaCostoSalDb;
    }

   

    public RegistroDataModelImpl getListaCostoSalCr() {
        return listaCostoSalCr;
    }

    public void setListaCostoSalCr(RegistroDataModelImpl listaCostoSalCr) {
        this.listaCostoSalCr = listaCostoSalCr;
    }

    public RegistroDataModelImpl getListaCostoSalAjDb() {
        return listaCostoSalAjDb;
    }

    public void setListaCostoSalAjDb(RegistroDataModelImpl listaCostoSalAjDb) {
        this.listaCostoSalAjDb = listaCostoSalAjDb;
    }

    public RegistroDataModelImpl getListaCostoSalAjCr() {
        return listaCostoSalAjCr;
    }

    public void setListaCostoSalAjCr(RegistroDataModelImpl listaCostoSalAjCr) {
        this.listaCostoSalAjCr = listaCostoSalAjCr;
    }

    public RegistroDataModelImpl getListaDepAcumuladaDb() {
        return listaDepAcumuladaDb;
    }

    public void setListaDepAcumuladaDb(RegistroDataModelImpl listaDepAcumuladaDb) {
        this.listaDepAcumuladaDb = listaDepAcumuladaDb;
    }

    public RegistroDataModelImpl getListaDepAcumuladaCr() {
        return listaDepAcumuladaCr;
    }

    public void setListaDepAcumuladaCr(RegistroDataModelImpl listaDepAcumuladaCr) {
        this.listaDepAcumuladaCr = listaDepAcumuladaCr;
    }

    public RegistroDataModelImpl getListaAjusteDepreciacionDb() {
        return listaAjusteDepreciacionDb;
    }

    public void setListaAjusteDepreciacionDb(
        RegistroDataModelImpl listaAjusteDepreciacionDb) {
        this.listaAjusteDepreciacionDb = listaAjusteDepreciacionDb;
    }

    public RegistroDataModelImpl getListaAjusteDepreciacionCr() {
        return listaAjusteDepreciacionCr;
    }

    public void setListaAjusteDepreciacionCr(
        RegistroDataModelImpl listaAjusteDepreciacionCr) {
        this.listaAjusteDepreciacionCr = listaAjusteDepreciacionCr;
    }

    public RegistroDataModelImpl getListaAjusInflaDebitoS() {
        return listaAjusInflaDebitoS;
    }

    public void setListaAjusInflaDebitoS(
        RegistroDataModelImpl listaAjusInflaDebitoS) {
        this.listaAjusInflaDebitoS = listaAjusInflaDebitoS;
    }

    public RegistroDataModelImpl getListaAjusInflaCreditoS() {
        return listaAjusInflaCreditoS;
    }

    public void setListaAjusInflaCreditoS(
        RegistroDataModelImpl listaAjusInflaCreditoS) {
        this.listaAjusInflaCreditoS = listaAjusInflaCreditoS;
    }

    public RegistroDataModelImpl getListaDeprecDebitoS() {
        return listaDeprecDebitoS;
    }

    public void setListaDeprecDebitoS(RegistroDataModelImpl listaDeprecDebitoS) {
        this.listaDeprecDebitoS = listaDeprecDebitoS;
    }

    public RegistroDataModelImpl getListaDeprecCreditoS() {
        return listaDeprecCreditoS;
    }

    public void setListaDeprecCreditoS(RegistroDataModelImpl listaDeprecCreditoS) {
        this.listaDeprecCreditoS = listaDeprecCreditoS;
    }

    public RegistroDataModelImpl getListaAjusDeprecDebitoS() {
        return listaAjusDeprecDebitoS;
    }

    public void setListaAjusDeprecDebitoS(
        RegistroDataModelImpl listaAjusDeprecDebitoS) {
        this.listaAjusDeprecDebitoS = listaAjusDeprecDebitoS;
    }

    public RegistroDataModelImpl getListaAjusDeprecCreditoS() {
        return listaAjusDeprecCreditoS;
    }

    public void setListaAjusDeprecCreditoS(
        RegistroDataModelImpl listaAjusDeprecCreditoS) {
        this.listaAjusDeprecCreditoS = listaAjusDeprecCreditoS;
    }

    public RegistroDataModelImpl getListaDEPRECDEBITOSCOMODATO() {
        return listaDEPRECDEBITOSCOMODATO;
    }

    public void setListaDEPRECDEBITOSCOMODATO(
        RegistroDataModelImpl listaDEPRECDEBITOSCOMODATO) {
        this.listaDEPRECDEBITOSCOMODATO = listaDEPRECDEBITOSCOMODATO;
    }

    public RegistroDataModelImpl getListaDEPRECCREDITOSCOMODATO() {
        return listaDEPRECCREDITOSCOMODATO;
    }

    public void setListaDEPRECCREDITOSCOMODATO(
        RegistroDataModelImpl listaDEPRECCREDITOSCOMODATO) {
        this.listaDEPRECCREDITOSCOMODATO = listaDEPRECCREDITOSCOMODATO;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
