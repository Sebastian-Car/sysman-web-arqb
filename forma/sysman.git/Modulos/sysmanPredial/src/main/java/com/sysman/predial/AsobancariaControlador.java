package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCincoRemote;
import com.sysman.predial.enums.AsobancariaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 27/05/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @author lcortes - Refactorizacion de las consultas de las listas,
 * se eliminan los metodos asobancariatablas, validarFacturaMF y
 * fnroAsobancaria ya que se implementa el llamado al ejb del paquete
 * cinco del modulo de predial
 * @version 3, 22,23,27,28,29,30/06/2017,
 * 04,05,06,10,11,12,13,14,15/07/2017
 */
@ManagedBean
@ViewScoped
public class AsobancariaControlador extends BeanBaseModal {
    private final String compania;
    private final String nOrden;
    private final String modulo;
    private final String user;

    // <DECLARAR_ATRIBUTOS>
    private String plano;
    private String recaudo;
    private String nombreBanco;
    private String codigoBanco;
    private String codigoBancoI;
    private Date fecha;
    private StreamedContent archivoDescarga;
    private UploadedFile archivoCargaARCHIVO;
    private String codASBC;
    private BufferedReader br;
    private String lineaArchivo;
    private String fechaRecaudo;
    private String paqueteA;
    private String registrosA;
    private String valorA;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaeBanco;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listabanco;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPredialCincoRemote ejbPredialCinco;

    /**
     * Creates a new instance of AsobancariaControlador
     */
    public AsobancariaControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        fecha = new Date();
        modulo = SessionUtil.getModulo();
        user = SessionUtil.getUser().getCodigo();
        recaudo = "true";

        try {
            numFormulario = GeneralCodigoFormaEnum.ASOBANCARIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AsobancariaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarlistaeBanco();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListabanco();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarlistaeBanco() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaeBanco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AsobancariaControladorUrlEnum.URL5588
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListabanco() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AsobancariaControladorUrlEnum.URL5991
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listabanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOBANCO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirregistrarPagos(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>

        try {
            archivoDescarga = null;
            String estAsobancaria = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "ESTANDAR ARCHIVOS ASOBANCARIA",
                                            modulo, new Date(), true), "")
                            .toString();

            int factordeMultiplicacion = 0;

            if (archivoCargaARCHIVO == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1104"));
                return;
            }

            InputStream is;
            is = archivoCargaARCHIVO.getInputstream();
            archivoCargaARCHIVO.getFileName();
            InputStreamReader r = new InputStreamReader(is);
            br = new BufferedReader(r);
            lineaArchivo = br.readLine();
            if (lineaArchivo == null) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB2929"));
                return;
            }

            if (obtenerCodigoBanco(lineaArchivo, factordeMultiplicacion,
                            estAsobancaria) != null) {

                importarAsobancaria();
            }

        }
        catch (IOException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private String obtenerCodigoBanco(String lineaArchivo,
        int factordeMultiplicacion, String estAsobancaria) {

        String codigo = "";
        if ("2000".equals(estAsobancaria)) {
            if ("01".equals(mid(lineaArchivo, 1 + factordeMultiplicacion,
                            2))) {
                codigo = mid(lineaArchivo, 21 + factordeMultiplicacion,
                                3);
            }
        }
        else if ("98".equals(estAsobancaria)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1295"));
            codigo = null;
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1101"));
            codigo = null;
        }

        if (!codASBC.equals(codigo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1102"));
            codigo = null;
        }
        return codigo;
    }

    public void importarAsobancaria() {
        try {
            InputStream is;
            is = archivoCargaARCHIVO.getInputstream();
            InputStreamReader r = new InputStreamReader(is);
            br = new BufferedReader(r);
            int lineas = 0;
            fechaRecaudo = "";
            paqueteA = "";
            registrosA = "";
            valorA = "";
            double tam;

            tam = archivoCargaARCHIVO.getSize() / 		162;

            StringBuilder datos = new StringBuilder();
            while ((lineaArchivo = br.readLine()) != null) {
                datos.append(lineaArchivo).append(";").append("\r\n");
                lineas++;
            }  

            String res = ejbPredialCinco.importarAsobancaria(compania,
                            BigDecimal.valueOf(tam),
                            Acciones.getClobConcatenado(datos.toString()),
                            lineas, user, nOrden, modulo, codigoBanco,
                            archivoCargaARCHIVO.getFileName());

            JsfUtil.agregarMensajeInformativo(res);

        }
        catch (IOException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirasobancariaInforme() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF, "000833INFASOBANCARIAERROR");
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirlog() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF, "000831INFASOBANCARIALOG");
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdAsobDet() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF, "000834INFASOBANCARIADET");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarSistema() {
        // <CODIGO_DESARROLLADO>
        if ("true".equals(plano)) {
            recaudo = "false";
        }
        else {
            recaudo = "true";
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarRecaudo() {
        // <CODIGO_DESARROLLADO>
        if ("true".equals(recaudo)) {
            plano = "false";
        }
        else {
            plano = "true";
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilabanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoBanco = registroAux.getCampos().get("CODIGOBANCO").toString();
        nombreBanco = registroAux.getCampos().get("NOMBREBANCO").toString();
        codASBC = registroAux.getCampos().get("COD_ASBC").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getPlano() {
        return plano;
    }

    public void setPlano(String plano) {
        this.plano = plano;
    }

    public String getRecaudo() {
        return recaudo;
    }

    public void setRecaudo(String recaudo) {
        this.recaudo = recaudo;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getCodigoBancoI() {
        return codigoBancoI;
    }

    public void setCodigoBancoI(String codigoBancoI) {
        this.codigoBancoI = codigoBancoI;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public UploadedFile getArchivoCargaARCHIVO() {
        return archivoCargaARCHIVO;
    }

    public void setArchivoCargaARCHIVO(UploadedFile archivoCargaARCHIVO) {
        this.archivoCargaARCHIVO = archivoCargaARCHIVO;
    }

    public String getCodASBC() {
        return codASBC;
    }

    public void setCodASBC(String codASBC) {
        this.codASBC = codASBC;
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public String getlineaArchivo() {
        return lineaArchivo;
    }

    public void setlineaArchivo(String lineaArchivo) {
        this.lineaArchivo = lineaArchivo;
    }

    public String getFechaRecaudo() {
        return fechaRecaudo;
    }

    public void setFechaRecaudo(String fechaRecaudo) {
        this.fechaRecaudo = fechaRecaudo;
    }

    public String getPaqueteA() {
        return paqueteA;
    }

    public void setPaqueteA(String paqueteA) {
        this.paqueteA = paqueteA;
    }

    public String getRegistrosA() {
        return registrosA;
    }

    public void setRegistrosA(String registrosA) {
        this.registrosA = registrosA;
    }

    public String getValorA() {
        return valorA;
    }

    public void setValorA(String valorA) {
        this.valorA = valorA;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getlistaeBanco() {
        return listaeBanco;
    }

    public void setlistaeBanco(List<Registro> listaeBanco) {
        this.listaeBanco = listaeBanco;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListabanco() {
        return listabanco;
    }

    public void setListabanco(RegistroDataModelImpl listabanco) {
        this.listabanco = listabanco;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("banco", codigoBancoI);
            reemplazar.put("fecha",
                            "true".equals(plano)
                                ? SysmanFunciones.formatearFecha(fecha)
                                : "'" + SysmanFunciones.convertirAFechaCadena(
                                                fecha)
                                    + "'");
            reemplazar.put("fechaCampo",
                            "true".equals(plano) ? "IP_ASOBANCARIA_LOG.FECHA"
                                : "TO_CHAR(IP_ASOBANCARIA_LOG.FECHARECAUDO,'DD/MM/YYYY')");
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | SysmanException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String mid(String valor, int ini, int len) {
        return valor.substring(ini - 1, (ini - 1) + len);
    }

}
