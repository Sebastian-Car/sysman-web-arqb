package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CentroscostosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author apineda
 * @version 1, 08/04/2016
 * 
 * @author eamaya
 * @version 2, 05/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 */
@ManagedBean
@ViewScoped

public class CentroscostosControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String cCodigo;
    private final String cCompania;
    private final String cNombre;
    private final String formulario;
    private final String cCentroCosto;
    private final String cAnoQr;
    private final String cNombreCentro;
    private final String cMesInicialQr;
    private final String cMesFinalQr;

    private String anoNomina;
    private final String modulo;
    private Registro registroSubSubDistribucionCCto;
    private Registro registroSubDistribucionCentroCosto;
    private List<Registro> listaAno;
    private List<Registro> listaTipo;
    private List<Registro> listaCategoriaGasto;
    private List<Registro> listaAnoQR;
    private List<Registro> listaANOSUB;
    private List<Registro> listaSubdistribucionccto;
    private List<Registro> listaDistribucioncentrocosto;
    private RegistroDataModelImpl listaCENTROCOSTO;
    private RegistroDataModelImpl listaCENTROCOSTOE;
    private String auxiliar;
    private RegistroDataModelImpl listacbCentroCostoDis;
    private RegistroDataModelImpl listacbCentroCostoDisE;
    private String mesInicialQr;
    private String mesFinalQr;
    private String anoQr;
    private String anoSubSeleccionado;
    private boolean visibleCategoria;
    private List<Registro> listaequivalenteSigVig;

    public CentroscostosControlador() {
        super();
        compania = SessionUtil.getCompania();
        anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
        anoNomina = anoNomina == null
            ? String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                            Calendar.YEAR))
            : anoNomina;
        modulo = SessionUtil.getModulo();
        cCodigo = "CODIGO";
        cCompania = "COMPANIA";
        cNombre = "NOMBRE";
        formulario = "formulario";
        cCentroCosto = "centroCosto";
        cAnoQr = "anoQr";
        cNombreCentro = "nombreCentro";
        cMesInicialQr = "mesInicialQr";
        cMesFinalQr = "mesFinalQr";
        try {
            numFormulario = GeneralCodigoFormaEnum.CENTROSCOSTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSubSubDistribucionCCto = new Registro(
                            new HashMap<String, Object>());
            registroSubDistribucionCentroCosto = new Registro(
                            new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(CentroscostosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaCENTROCOSTO();
        cargarListaCENTROCOSTOE();

        cargarListacbCentroCostoDis();
        cargarListacbCentroCostoDisE();

        cargarListaAno();
        cargarListaTipo();
        cargarListaCategoriaGasto();
        cargarListaAnoQR();
        cargarListaANOSUB();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubdistribucionccto();
        cargarListaDistribucioncentrocosto();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubdistribucionccto = null;
        listaDistribucioncentrocosto = null;
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CENTRO_COSTO;
        buscarLlave();
        asignarOrigenDatos();
        mesInicialQr = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        mesFinalQr = String.valueOf(SysmanFunciones
                        .mes(new Date()));
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public void cargarListaSubdistribucionccto() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get("ANO"));

            listaSubdistribucionccto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.DISTRIBUCION_CENTROCOSTO
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "DISTRIBUCION_CENTROCOSTO"));
        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(CentroscostosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    public void cargarListaDistribucioncentrocosto() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(cCodigo));

        try {
            listaDistribucioncentrocosto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CentroscostosControladorUrlEnum.URL5961
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "CENTRO_COSTOESP"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CentroscostosControladorUrlEnum.URL6997
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTipo() {
        try {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CentroscostosControladorUrlEnum.URL7396
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCategoriaGasto() {
        try {
            listaCategoriaGasto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CentroscostosControladorUrlEnum.URL7706
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void cargarlistaequivalenteSigVig() {
   	 Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        int anonom = Integer.parseInt(registro.getCampos().get("ANO").toString());
        
        param.put(GeneralParameterEnum.ANO.getName(),
       		 anonom + 1);
      
       try {
       	listaequivalenteSigVig = RegistroConverter.toListRegistro(
                           requestManager.getList(UrlServiceUtil.getInstance()
                                           .getUrlServiceByUrlByEnumID(
                                                           CentroscostosControladorUrlEnum.URL20084
                                                                           .getValue())
                                           .getUrl(), param));
       }
       catch (SystemException e) {
           logger.error(e.getMessage(), e);
           JsfUtil.agregarMensajeError(e.getMessage());
       }
       
   }

    public void cargarListaAnoQR() {
        listaAnoQR = listaAno;
    }

    public void cargarListaANOSUB() {
        listaANOSUB = listaAno;
    }

    public void cargarListaCENTROCOSTO() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CentroscostosControladorUrlEnum.URL8755
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoNomina);

        listaCENTROCOSTO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCENTROCOSTOE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CentroscostosControladorUrlEnum.URL8755
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoNomina);

        listaCENTROCOSTOE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListacbCentroCostoDis() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CentroscostosControladorUrlEnum.URL8755
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoNomina);

        listacbCentroCostoDis = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListacbCentroCostoDisE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CentroscostosControladorUrlEnum.URL8755
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoNomina);

        listacbCentroCostoDisE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void agregarRegistroSubSubdistribucionccto() {
        try {
            registroSubSubDistribucionCCto.getCampos().put(cCompania,
                            compania);
            registroSubSubDistribucionCCto.getCampos().put("ANO",
                            registro.getCampos().get("ANO"));

            registroSubSubDistribucionCCto.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubSubDistribucionCCto.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubSubDistribucionCCto.getCampos().remove("NOMBRE_CCTO");

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DISTRIBUCION_CENTROCOSTO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubDistribucionCCto.getCampos());

            cargarListaSubdistribucionccto();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(CentroscostosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubDistribucionCCto = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubdistribucionccto(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove("NOMBRE_CCTO");
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DISTRIBUCION_CENTROCOSTO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(CentroscostosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubdistribucionccto();
        }
    }

    public void eliminarRegSubSubdistribucionccto(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DISTRIBUCION_CENTROCOSTO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubdistribucionccto();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarEdicionSubdistribucionccto() {
        cargarListaSubdistribucionccto();
        cargarListaDistribucioncentrocosto();
    }

    public void agregarRegistroSubDistribucioncentrocosto() {
        try {
            registroSubDistribucionCentroCosto.getCampos().put("COMPANIA",
                            compania);
            registroSubDistribucionCentroCosto.getCampos().put("ANO",
                            registro.getCampos().get("ANO"));
            registroSubDistribucionCentroCosto.getCampos()
                            .remove("NOMBRE_CCTOSUB");
            registroSubDistribucionCentroCosto.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubDistribucionCentroCosto.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CENTRO_COSTOESP
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubDistribucionCentroCosto.getCampos());
            cargarListaDistribucioncentrocosto();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            registroSubDistribucionCentroCosto = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubDistribucioncentrocosto(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove("NOMBRE_CCTOSUB");

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CENTRO_COSTOESP
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
            cargarListaDistribucioncentrocosto();
        }
        catch (SystemException ex) {
            Logger.getLogger(CentroscostosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaDistribucioncentrocosto();
        }
    }

    public void eliminarRegSubDistribucioncentrocosto(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CENTRO_COSTOESP
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaDistribucioncentrocosto();
        }
        catch (SystemException ex) {
            Logger.getLogger(CentroscostosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionDistribucioncentrocosto() {
        cargarListaDistribucioncentrocosto();
    }

    public void oprimircmdContable() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { cCentroCosto, cNombreCentro, cAnoQr,
                            cMesInicialQr, cMesFinalQr, formulario };
        Object[] valores = { registro.getCampos().get(cCodigo).toString(),
                             registro.getCampos().get(cNombre).toString(),
                             anoQr,
                             mesInicialQr, mesFinalQr, cCentroCosto };
        String numFormulario = String
                        .valueOf(GeneralCodigoFormaEnum.SUBFORMCENTROS_CONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlashCerrar(numFormulario, modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdPptales() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", cCentroCosto, cNombreCentro, cAnoQr,
                            cMesInicialQr, cMesFinalQr, formulario };
        Object[] valores = { css, registro.getCampos().get(cCodigo).toString(),
                             registro.getCampos().get(cNombre).toString(),
                             anoQr,
                             mesInicialQr, mesFinalQr, cCentroCosto };
        String numFormulario = String
                        .valueOf(GeneralCodigoFormaEnum.SUBFORMCENTROPS_CONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlashCerrar(numFormulario, modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdAlmacen() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", cCentroCosto, cNombreCentro, cAnoQr,
                            cMesInicialQr, cMesFinalQr, formulario };
        Object[] valores = { css, registro.getCampos().get(cCodigo).toString(),
                             registro.getCampos().get(cNombre).toString(),
                             anoQr,
                             mesInicialQr, mesFinalQr, cCentroCosto };
        String numFormulario = String
                        .valueOf(GeneralCodigoFormaEnum.SUBFORMCENTROAS_CONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlashCerrar(numFormulario, modulo, campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarANOSUB() {
        // <CODIGO_DESARROLLADO>
        anoSubSeleccionado = (String) registroSubDistribucionCentroCosto
                        .getCampos().get("ANO");
        registroSubDistribucionCentroCosto.getCampos().put(cCodigo, "");
        cargarListacbCentroCostoDis();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarANOSUBC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        anoSubSeleccionado = SysmanFunciones
                        .nvl(listaDistribucioncentrocosto.get(rowNum)
                                        .getCampos().get("ANO"), "")
                        .toString();
        listaDistribucioncentrocosto.get(rowNum).getCampos().put(cCodigo, "");
        cargarListacbCentroCostoDisE();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCENTROCOSTO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubDistribucionCCto.getCampos().put("CENTRO_COSTO",
                        registroAux.getCampos().get(cCodigo));
    }

    public void seleccionarFilaCENTROCOSTOE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigo) == null ? " "
            : registroAux.getCampos().get(cCodigo).toString();
    }

    public void seleccionarFilacbCentroCostoDis(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubDistribucionCentroCosto.getCampos().put(cCodigo,
                        registroAux.getCampos().get(cCodigo));
    }

    public void seleccionarFilacbCentroCostoDisE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigo) == null ? " "
            : registroAux.getCampos().get(cCodigo).toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if("21".equals(modulo)){
            visibleCategoria=false;
        }else{
            visibleCategoria=true;
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (accion.equals(ACCION_MODIFICAR)) {

            anoQr = registro.getCampos().get("ANO").toString();
        }
        cargarlistaequivalenteSigVig();
        // </CODIGO_DESARROLLADO>
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
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

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    public List<Registro> getListaCategoriaGasto() {
        return listaCategoriaGasto;
    }

    public void setListaCategoriaGasto(List<Registro> listaCategoriaGasto) {
        this.listaCategoriaGasto = listaCategoriaGasto;
    }

    public List<Registro> getListaAnoQR() {
        return listaAnoQR;
    }

    public void setListaAnoQR(List<Registro> listaAnoQR) {
        this.listaAnoQR = listaAnoQR;
    }

    public List<Registro> getListaANOSUB() {
        return listaANOSUB;
    }

    public void setListaANOSUB(List<Registro> listaANOSUB) {
        this.listaANOSUB = listaANOSUB;
    }

    public List<Registro> getListaSubdistribucionccto() {
        return listaSubdistribucionccto;
    }

    public void setListaSubdistribucionccto(
        List<Registro> listaSubdistribucionccto) {
        this.listaSubdistribucionccto = listaSubdistribucionccto;
    }

    public List<Registro> getListaDistribucioncentrocosto() {
        return listaDistribucioncentrocosto;
    }

    public void setListaDistribucioncentrocosto(
        List<Registro> listaDistribucioncentrocosto) {
        this.listaDistribucioncentrocosto = listaDistribucioncentrocosto;
    }

    public RegistroDataModelImpl getListaCENTROCOSTO() {
        return listaCENTROCOSTO;
    }

    public void setListaCENTROCOSTO(RegistroDataModelImpl listaCENTROCOSTO) {
        this.listaCENTROCOSTO = listaCENTROCOSTO;
    }

    public RegistroDataModelImpl getListaCENTROCOSTOE() {
        return listaCENTROCOSTOE;
    }

    public void setListaCENTROCOSTOE(RegistroDataModelImpl listaCENTROCOSTOE) {
        this.listaCENTROCOSTOE = listaCENTROCOSTOE;
    }
    
    public List<Registro> getListaequivalenteSigVig() {
		return listaequivalenteSigVig;
	}

	public void setListaequivalenteSigVig(List<Registro> listaequivalenteSigVig) {
		this.listaequivalenteSigVig = listaequivalenteSigVig;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListacbCentroCostoDis() {
        return listacbCentroCostoDis;
    }

    public void setListacbCentroCostoDis(
        RegistroDataModelImpl listacbCentroCostoDis) {
        this.listacbCentroCostoDis = listacbCentroCostoDis;
    }

    public RegistroDataModelImpl getListacbCentroCostoDisE() {
        return listacbCentroCostoDisE;
    }

    public void setListacbCentroCostoDisE(
        RegistroDataModelImpl listacbCentroCostoDisE) {
        this.listacbCentroCostoDisE = listacbCentroCostoDisE;
    }

    public Registro getRegistroSubSubDistribucionCCto() {
        return registroSubSubDistribucionCCto;
    }

    public void setRegistroSubSubDistribucionCCto(
        Registro registroSubSubDistribucionCCto) {
        this.registroSubSubDistribucionCCto = registroSubSubDistribucionCCto;
    }

    public Registro getRegistroSubDistribucionCentroCosto() {
        return registroSubDistribucionCentroCosto;
    }

    public void setRegistroSubDistribucionCentroCosto(
        Registro registroSubDistribucionCentroCosto) {
        this.registroSubDistribucionCentroCosto = registroSubDistribucionCentroCosto;
    }

    public String getMesInicialQr() {
        return mesInicialQr;
    }

    public void setMesInicialQr(String mesInicialQr) {
        this.mesInicialQr = mesInicialQr;
    }

    public String getMesFinalQr() {
        return mesFinalQr;
    }

    public void setMesFinalQr(String mesFinalQr) {
        this.mesFinalQr = mesFinalQr;
    }

    public String getAnoQr() {
        return anoQr;
    }

    public void setAnoQr(String anoQr) {
        this.anoQr = anoQr;
    }

    public String getAnoSubSeleccionado() {
        return anoSubSeleccionado;
    }

    public void setAnoSubSeleccionado(String anoSubSeleccionado) {
        this.anoSubSeleccionado = anoSubSeleccionado;
    }

    public String getModulo() {
        return modulo;
    }

    public boolean isVisibleCategoria() {
        return visibleCategoria;
    }

    public void setVisibleCategoria(boolean visibleCategoria) {
        this.visibleCategoria = visibleCategoria;
    }
    
    

}
