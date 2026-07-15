package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.EstadodetesoreriafechasControladorEnum;
import com.sysman.contabilidad.enums.EstadodetesoreriafechasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 21/04/2016
 *
 * @version 1.1, Nov 2016 - Modificado por: sdaza. Se adiciona
 * componente gráfico para indicador de referencia y fuente de
 * recurso. Se valida la condición de con saldo cero debido a que se
 * omite de la consulta base y se debe validar desde la consulta final
 * @version 2 jrodriguezr Se refactoriza el codigo SQL de las listas
 * para utilizar dss.
 * @version 3, 20/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamdos a funciones, procedimiento y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @author jreina
 * @version 4, 13/06/2017 Cambio código formulario y actualización de
 * ConnectorPool
 */
@ManagedBean
@ViewScoped

public class EstadodetesoreriafechasControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private boolean centroCosto;
    private boolean tercero;
    private boolean auxiliar;
    private boolean referencia;
    private boolean fuenteRecurso;
    private boolean saldoCero;
    private String codigoInicial;
    private String codigoFinal;
    private String digitos;
    private Date fechaInicial;
    private String fecha;
    private int mesInicial;
    private Date fechaFinal;
    private int anoInicial;
    private int anoFinal;
    private String condicion;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    /**
     * Creates a new instance of EstadodetesoreriafechasControlador
     */
    public EstadodetesoreriafechasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.ESTADODETESORERIAFECHAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EstadodetesoreriafechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        fechaInicial = new Date();
        fechaFinal = new Date();
        anoInicial = SysmanFunciones.ano(fechaInicial);
        anoFinal = SysmanFunciones.ano(fechaFinal);
        cargarListaCodigoInicial();
        codigoInicial = "11";
        codigoFinal = "13";
        digitos = "6";
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaCodigoInicial() {
        // 29007
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodetesoreriafechasControladorUrlEnum.URL3874
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCodigoFinal() {
        // 29009
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodetesoreriafechasControladorUrlEnum.URL4785
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);
        param.put(EstadodetesoreriafechasControladorEnum.CODIGOINICIAL
                        .getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cambiarFechaini() {
        // <CODIGO_DESARROLLADO>
        mesInicial = SysmanFunciones.mes(fechaInicial);

        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechafin() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCentroCosto() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVigencias() {
        anoInicial = SysmanFunciones.ano(fechaInicial);
        anoFinal = SysmanFunciones.ano(fechaFinal);
        if (anoInicial != anoFinal) {
            fechaInicial = new Date();
            fechaFinal = new Date();
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3758"));
            return false;

        }
        return true;
    }

    public void generaReporteETFechas(FORMATOS formatos) {
        String parReporte;
        archivoDescarga = null;

        if (!validarVigencias()) {
            return;
        }

        try {
            int mesInicialInt = SysmanFunciones.mes(fechaInicial) - 1;
            int mesFinal = SysmanFunciones.mes(fechaFinal);
            fecha = SysmanFunciones.convertirAFechaCadena(fechaInicial);
            Date fechaIMes = SysmanFunciones.convertirAFecha(fecha);
            fecha = SysmanFunciones.primeroDeMesCadena(fechaIMes);
            anoInicial = SysmanFunciones.ano(fechaInicial);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mesInicial", mesInicialInt);
            reemplazar.put("anoInicial", anoInicial);
            reemplazar.put("anio", anoInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("fechaIMes", fecha);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));
            reemplazar.put("codigoInicial",
                            SysmanFunciones.colocarComillas(codigoInicial));
            reemplazar.put("codigoFinal",
                            SysmanFunciones.colocarComillas(codigoFinal));
            reemplazar.put("digitos", digitos);
            reemplazar.put("referencia", "");
            reemplazar.put("fuenteRecurso", "");
            if (!saldoCero) {
                condicion = "  AND ((NVL(BASE.SALDO" + mesInicialInt
                    + ",0)  + NVL(DETALLE.SALDO_ANT,0)) NOT IN (0) "
                    + " OR NVL(DETALLE.MOV_DEBITO,0)  NOT IN (0) "
                    + " OR NVL(DETALLE.MOV_CREDITO,0) NOT IN (0) "
                    + " OR ( (NVL(BASE.SALDO" + mesInicialInt
                    + ",0)  + NVL(DETALLE.SALDO_ANT,0)) + NVL(DETALLE.MOV_DEBITO,0) - NVL(DETALLE.MOV_CREDITO,0)) NOT IN (0) )";
            }
            else {
                condicion = "";
            }
            reemplazar.put("condicion", condicion);
            reemplazar.put("manTer", tercero ? "1" : "0");
            reemplazar.put("manAux", auxiliar ? "1" : "0");
            reemplazar.put("manCen", centroCosto ? "1" : "0");
            reemplazar.put("manRef", referencia ? "1" : "0");
            reemplazar.put("manFue", fuenteRecurso ? "1" : "0");

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_TITULO", idioma.getString("TB_TB846"));
            String entre = "Entre "
                + SysmanFunciones.convertirAFechaCadena(
                                fechaInicial)
                + " y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal);
            parametros.put("PR_ENTRE", entre);

            parReporte = "000647EstadoDeTesoreriaFechas";
            Reporteador.resuelveConsulta("000647EstadoDeTesoreriaFechas",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (OutOfMemoryError | JRException | IOException
                        | ParseException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporteETFechas(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporteETFechas(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public boolean isCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto) {
        this.centroCosto = centroCosto;
    }

    public boolean isTercero() {
        return tercero;
    }

    public void setTercero(boolean tercero) {
        this.tercero = tercero;
    }

    public boolean isAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(boolean auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isSaldoCero() {
        return saldoCero;
    }

    public void setSaldoCero(boolean saldoCero) {
        this.saldoCero = saldoCero;
    }

    public int getMesInicial() {
        return mesInicial;
    }

    /**
     * @return the referencia
     */
    public boolean isReferencia() {
        return referencia;
    }

    /**
     * @param referencia
     * the referencia to set
     */
    public void setReferencia(boolean referencia) {
        this.referencia = referencia;
    }

    /**
     * @return the fuenteRecurso
     */
    public boolean isFuenteRecurso() {
        return fuenteRecurso;
    }

    /**
     * @param fuenteRecurso
     * the fuenteRecurso to set
     */
    public void setFuenteRecurso(boolean fuenteRecurso) {
        this.fuenteRecurso = fuenteRecurso;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public int getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(int anoInicial) {
        this.anoInicial = anoInicial;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public int getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(int anoFinal) {
        this.anoFinal = anoFinal;
    }

}
