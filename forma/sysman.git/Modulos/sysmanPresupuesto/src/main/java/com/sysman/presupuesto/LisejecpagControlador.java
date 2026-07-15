package com.sysman.presupuesto;

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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucionGastosCaqControladorEnum;
import com.sysman.presupuesto.enums.EjecucionGastosCaqControladorUrlEnum;
import com.sysman.presupuesto.enums.LisejecpagControladorEnum;
import com.sysman.presupuesto.enums.LisejecpagControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author apineda
 * @version 1, 30/06/2016
 * @version 2, 19/04/2017, mzanguna, refactorizaci�n y ajustes sonar.
 * 
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 *  
 * @version 4.0, 19/07/2022
 * @author mrosero
 */

@ManagedBean
@ViewScoped
public class  LisejecpagControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	 private final String compania;
	 private final String modulo;
//<DECLARAR_ATRIBUTOS>

	 private boolean indCentroCosto;
	 private boolean indReferencia;
	 private boolean indAuxiliar;
	 private boolean indFuenteRecursos;
	private String cuentaInicial;
	private String cuentaFinal;
    private int anio;
    private int mes;
	private String centroCostoInicial;
	private String centroCostoFinal;
	private String referenciaInicial;
	private String referenciaFinal;
	private String fuenteInicial;
	private String fuenteFinal;
	private String auxiliarInicial;
	private String auxiliarFinal;
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
private List<Registro> listaAno;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
 private RegistroDataModelImpl listaCuentaInicial;
 private RegistroDataModelImpl listaCuentaFinal;
 private RegistroDataModelImpl listaCentroCostoInicial;
 private RegistroDataModelImpl listaCentroCostoFinal;
 private RegistroDataModelImpl listaReferenciaInicial;
 private RegistroDataModelImpl listaReferenciaFinal;
 private RegistroDataModelImpl listaFuenteInicial;
 private RegistroDataModelImpl listaFuenteFinal;
 private RegistroDataModelImpl listaAuxiliarInicial;
 private RegistroDataModelImpl listaAuxiliarFinal;
//</DECLARAR_LISTAS_COMBO_GRANDE>

 @EJB
 private EjbSysmanUtilRemote ejbSysmanUtilRemote;

 /**
  * Creates a new instance of LisejecpagControlador
  */
 public LisejecpagControlador() {
     super();
     compania = SessionUtil.getCompania();
     modulo = SessionUtil.getModulo();
     anio = SysmanFunciones.getParteFecha(new Date(),
                     Calendar.YEAR);
     mes = SysmanFunciones.getParteFecha(
                     new Date(),
                     Calendar.MONTH)
         + 1;
     try {
         numFormulario = GeneralCodigoFormaEnum.LISEJECPAG_CONTROLADOR
                         .getCodigo();
         validarPermisos();
         // <INI_ADICIONAL>
         // </INI_ADICIONAL>
     }
     catch (Exception ex) {
         Logger.getLogger(LisejecpagControlador.class.getName())
                         .log(Level.SEVERE, null, ex);
         SessionUtil.redireccionarMenuPermisos();
     }
 }

    @PostConstruct
    public void inicializar(){
//<CARGAR_LISTA>
		 cargarListaAno();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		 cargarListaCuentaInicial(); 
		 cargarListaCuentaFinal(); 
		 cargarListaCentroCostoInicial(); 
		 cargarListaCentroCostoFinal(); 
		 cargarListaReferenciaInicial(); 
		 cargarListaReferenciaFinal(); 
		 cargarListaFuenteInicial(); 
		 cargarListaFuenteFinal(); 
		 cargarListaAuxiliarInicial(); 
		 cargarListaAuxiliarFinal();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
    }

  @Override
  public void abrirFormulario() {
      // <CODIGO_DESARROLLADO>
      /*
       * FR961-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
       * DoCmd.Restore End Sub
       */
      // </CODIGO_DESARROLLADO>
  }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     */
  public void cargarListaAno() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      try {
          listaAno = RegistroConverter.toListRegistro(requestManager.getList(
                          UrlServiceUtil.getInstance()
                                          .getUrlServiceByUrlByEnumID(
                                                          LisejecpagControladorUrlEnum.URL3773
                                                                          .getValue())
                                          .getUrl(),
                          param));
      }
      catch (SystemException e) {
          Logger.getLogger(LisejecpagControlador.class.getName())
                          .log(Level.SEVERE, null, e);
          JsfUtil.agregarMensajeError(e.getMessage());

      }
  }


  public void cargarListaCuentaInicial() {

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      LisejecpagControladorUrlEnum.URL4170
                                                      .getValue());
      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(GeneralParameterEnum.ANO.getName(), anio);

      listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, "ID");

  }

  public void cargarListaCuentaFinal() {

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      LisejecpagControladorUrlEnum.URL4800
                                                      .getValue());
      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(GeneralParameterEnum.ANO.getName(), anio);
      param.put(LisejecpagControladorEnum.CINI.getValue(), cuentaInicial);

      listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, "ID");

  }
  public void cargarListaCentroCostoInicial() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(GeneralParameterEnum.ANO.getName(), anio);

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      EjecucionGastosCaqControladorUrlEnum.URL584
                                                      .getValue());
      listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, GeneralParameterEnum.CODIGO.getName());
  }
  
  
  public void cargarListaCentroCostoFinal() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(GeneralParameterEnum.ANO.getName(), anio);
      param.put(EjecucionGastosCaqControladorEnum.CENTRO_COSTO.getValue(),
                      centroCostoInicial);

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      EjecucionGastosCaqControladorUrlEnum.URL600
                                                      .getValue());
      listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, GeneralParameterEnum.CODIGO.getName());
  }
  
  public void cargarListaReferenciaInicial() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(GeneralParameterEnum.ANO.getName(), anio);

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      EjecucionGastosCaqControladorUrlEnum.URL707
                                                      .getValue());
      listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, GeneralParameterEnum.CODIGO.getName());

  }
  
  
  public void cargarListaReferenciaFinal() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(GeneralParameterEnum.ANO.getName(), anio);
      param.put(EjecucionGastosCaqControladorEnum.REFERENCIAINICIAL
                      .getValue(), referenciaInicial);

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      EjecucionGastosCaqControladorUrlEnum.URL732
                                                      .getValue());
      listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, GeneralParameterEnum.CODIGO.getName());

  }
 
  public void cargarListaFuenteInicial() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(GeneralParameterEnum.ANO.getName(), anio);

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      EjecucionGastosCaqControladorUrlEnum.URL663
                                                      .getValue());
      listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, GeneralParameterEnum.CODIGO.getName());

  }
    
  public void cargarListaFuenteFinal() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(GeneralParameterEnum.ANO.getName(), anio);
      param.put(EjecucionGastosCaqControladorEnum.FUENTEINICIAL.getValue(),
                      fuenteInicial);

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      EjecucionGastosCaqControladorUrlEnum.URL684
                                                      .getValue());
      listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, GeneralParameterEnum.CODIGO.getName());

  }
    /**
     * 
     * Carga la lista listaAuxiliarInicial
     *
     */
  public void cargarListaAuxiliarInicial() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(EjecucionGastosCaqControladorEnum.ANIO.getValue(), anio);

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      EjecucionGastosCaqControladorUrlEnum.URL749
                                                      .getValue());
      listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, GeneralParameterEnum.CODIGO.getName());

  }
    /**
     * 
     * Carga la lista listaAuxiliarFinal
     *
     */
  public void cargarListaAuxiliarFinal() {

      Map<String, Object> param = new TreeMap<>();
      param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
      param.put(EjecucionGastosCaqControladorEnum.ANIO.getValue(), anio);
      param.put(EjecucionGastosCaqControladorEnum.CODIGOFINAL.getValue(),
                      auxiliarInicial);

      UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(
                                      EjecucionGastosCaqControladorUrlEnum.URL776
                                                      .getValue());
      listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(), param,
                      true, GeneralParameterEnum.CODIGO.getName());

  }
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>

  public void oprimirImprimirPdf() {
      // <CODIGO_DESARROLLADO>

      archivoDescarga = null;

      generaInforme(ReportesBean.FORMATOS.PDF);

      // </CODIGO_DESARROLLADO>
  }

  public void oprimirImprimirExcel() {
      // <CODIGO_DESARROLLADO>
      archivoDescarga = null;

      generaInforme(ReportesBean.FORMATOS.EXCEL);

      // </CODIGO_DESARROLLADO>
  }
  
  public void generaInforme(ReportesBean.FORMATOS formato) {

      Map<String, Object> parametros = new HashMap<>();
      HashMap<String, Object> reemplazar = new HashMap<>();
      String tituloPAC = null;
      String informe;
      String columnas;
      String tituloColumna1;
      String obligacion = null;

      try {
          informe = "000957LisEjecPACREO";

          // Informe 000957LisEjecPACREO
          tituloPAC = ejbSysmanUtilRemote.consultarParametro(compania,
                          "TITULO EN EJECUCION DE PAC", modulo,
                          new Date(), false);
          obligacion = ejbSysmanUtilRemote.consultarParametro(compania,
                          "OBLIGA CONTROLAR PAC EN REGISTRO DE OBLIGACION",
                          modulo, new Date(), true);

          if (tituloPAC == null) {
              JsfUtil.agregarMensajeInformativo(
                              idioma.getString("TB_TB180"));
              return;
          }
          if (obligacion == null) {
              JsfUtil.agregarMensajeInformativo(
                              idioma.getString("TB_TB181"));
              return;
          }

          if ("SI".equals(obligacion)) {
              // Obligaciones = COLUMNA1, SaldoDelPACREO = COLUMNA2
              columnas = " SUM(SP.REGISTRO_OBLIGACION + SP.MODIF_REGISTRO_OBLIGACION) AS COLUMNA1, SUM(SP.PAC_APROPIADO) + SUM(CASE WHEN P.NATURALEZA='D' THEN (SP.MODIF_PAC_DEBITO - SP.MODIF_PAC_CREDITO) ELSE (SP.MODIF_PAC_CREDITO - MODIF_PAC_DEBITO) END) - SUM(SP.REGISTRO_OBLIGACION + SP.MODIF_REGISTRO_OBLIGACION) AS COLUMNA2, ";
              tituloColumna1 = idioma.getString("TB_TB222");
          }
          else { // EjecucionPpt = COLUMNA1, SaldoDelPAC = COLUMNA2
              columnas = " SUM(CASE WHEN P.NATURALEZA='D' THEN (SP.EJE_PPT_DEBITO - SP.EJE_PPT_CREDITO) ELSE ((SP.EJE_PPT_CREDITO - SP.EJE_PPT_DEBITO) + SP.MODIF_INGRESOS) END) AS COLUMNA1, SUM(SP.PAC_APROPIADO) + SUM(CASE WHEN P.NATURALEZA='D' THEN (SP.MODIF_PAC_DEBITO - SP.MODIF_PAC_CREDITO) ELSE (SP.MODIF_PAC_CREDITO - MODIF_PAC_DEBITO) END) - SUM(CASE WHEN P.NATURALEZA='D' THEN (SP.EJE_PPT_DEBITO - SP.EJE_PPT_CREDITO) ELSE ((SP.EJE_PPT_CREDITO - SP.EJE_PPT_DEBITO) + SP.MODIF_INGRESOS) END) AS COLUMNA2,";
              tituloColumna1 = idioma.getString("TB_TB224");
          }

          reemplazar.put("anio", anio);
          reemplazar.put("mes", mes);
          reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
          reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
          reemplazar.put("columnas", columnas);
          validarAuxiliares(reemplazar);

          parametros.put("PR_NOMBRECOMPANIA",
                          SessionUtil.getCompaniaIngreso().getNombre());
          parametros.put("PR_ANO", anio);
          parametros.put("PR_NOMBREMES",
                          SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                          .toUpperCase());
          parametros.put("PR_EJECUCIONPAC", tituloPAC);
          parametros.put("PR_TITULOCOLUMNA1", tituloColumna1);

          Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                          reemplazar, parametros);

          archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                          ConectorPool.ESQUEMA_SYSMAN, formato);
      }
      catch (JRException | IOException | SysmanException
                      | SystemException e) {
          logger.error(e.getMessage(), e);
          JsfUtil.agregarMensajeError(e.getMessage());
      }

  }
  
  public void validarAuxiliares(Map<String, Object> reemplazos) {
      reemplazos.put("centroCosto", "");
      reemplazos.put("auxiliar", "");
      reemplazos.put("referencia", "");
      reemplazos.put("fuenteRecurso", "");
      
      if (indCentroCosto) {
          reemplazos.put("centroCosto", " AND P.CENTRO_COSTO BETWEEN '"
              + centroCostoInicial + "' AND  '" + centroCostoFinal + "' ");
      }
      if (indAuxiliar) {
          reemplazos.put("auxiliar", " AND P.AUXILIAR BETWEEN '"
              + auxiliarInicial + "'  AND '" + auxiliarFinal + "' ");

      }
      if (indReferencia) {
          reemplazos.put("referencia", " AND P.REFERENCIA BETWEEN '"
              + referenciaInicial + "' AND '" + referenciaFinal + "' ");
      }
      if (indFuenteRecursos) {
          reemplazos.put("fuenteRecurso", " AND P.FUENTE_RECURSO BETWEEN '"
              + fuenteInicial + "' AND '" + fuenteFinal + "' ");
      }
  }
  
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>

  public void cambiarAno() {
      // <CODIGO_DESARROLLADO>
      cuentaInicial = null;
      cuentaFinal = null;
      centroCostoInicial = null;
      centroCostoFinal = null;
      auxiliarInicial = null;
      auxiliarFinal = null;
      referenciaInicial = null;
      referenciaFinal = null;
      fuenteInicial = null;
      fuenteFinal = null;
      cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
      centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
      auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
      referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
      fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
      cargarListaCuentaInicial();
      cargarListaCentroCostoInicial();
      cargarListaAuxiliarInicial();
      cargarListaReferenciaInicial();
      cargarListaFuenteInicial();
      // </CODIGO_DESARROLLADO>
  }

  public void cambiarIndCentroCosto() {
      // <CODIGO_DESARROLLADO>
      centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;

      // </CODIGO_DESARROLLADO>
  }

  public void cambiarIndReferencia() {
      // <CODIGO_DESARROLLADO>
      referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        //</CODIGO_DESARROLLADO>
    }

  public void cambiarIndAuxiliar() {
      // <CODIGO_DESARROLLADO>
      auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        //</CODIGO_DESARROLLADO>
    }

  public void cambiarIndFuenteRecursos() {
      // <CODIGO_DESARROLLADO>
      fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
      fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        //</CODIGO_DESARROLLADO>
    }
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
  public void seleccionarFilaCuentaInicial(SelectEvent event) {
      Registro registroAux = (Registro) event.getObject();
      cuentaInicial = SysmanFunciones
                      .nvl(registroAux.getCampos().get("ID"), "").toString();
      cuentaFinal = "";
      cargarListaCuentaFinal();
  }
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
  public void seleccionarFilaCuentaFinal(SelectEvent event) {
      Registro registroAux = (Registro) event.getObject();
      cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                      .toString();
  }
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
  public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
      Registro registroAux = (Registro) event.getObject();
      centroCostoInicial = SysmanFunciones.nvl(
                      registroAux.getCampos().get(
                                      GeneralParameterEnum.CODIGO.getName()),
                      "").toString();
       centroCostoFinal = null;
      cargarListaCentroCostoFinal();
  }

    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
  public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
      Registro registroAux = (Registro) event.getObject();
      centroCostoFinal = SysmanFunciones.nvl(
                      registroAux.getCampos().get(
                                      GeneralParameterEnum.CODIGO.getName()),
                      "").toString();     
  }
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
  public void seleccionarFilaReferenciaInicial(SelectEvent event) {
      Registro registroAux = (Registro) event.getObject();
      referenciaInicial = SysmanFunciones.nvl(
                      registroAux.getCampos().get(
                                      GeneralParameterEnum.CODIGO.getName()),
                      "").toString();
      referenciaFinal = null;
      cargarListaReferenciaFinal();
  }
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
  public void seleccionarFilaReferenciaFinal(SelectEvent event) {
      Registro registroAux = (Registro) event.getObject();
      referenciaFinal = SysmanFunciones.nvl(
                      registroAux.getCampos().get(
                                      GeneralParameterEnum.CODIGO.getName()),
                      "").toString();
  }
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
  public void seleccionarFilaFuenteInicial(SelectEvent event) {
      Registro registroAux = (Registro) event.getObject();
      fuenteInicial = SysmanFunciones.nvl(
                      registroAux.getCampos().get(
                                      GeneralParameterEnum.CODIGO.getName()),
                      "").toString();
      fuenteFinal = null;
      cargarListaFuenteFinal();
  }
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
  public void seleccionarFilaFuenteFinal(SelectEvent event) {
      Registro registroAux = (Registro) event.getObject();
      fuenteFinal = SysmanFunciones.nvl(
                      registroAux.getCampos().get(
                                      GeneralParameterEnum.CODIGO.getName()),
                      "").toString();
  }
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
    Registro registroAux = (Registro) event.getObject();
    auxiliarInicial = SysmanFunciones.nvl(
                    registroAux.getCampos().get(
                                    GeneralParameterEnum.CODIGO.getName()),
                    "").toString();
    
    auxiliarFinal = null;
    cargarListaAuxiliarFinal();
}
    /**
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
    Registro registroAux = (Registro) event.getObject();
    auxiliarFinal = SysmanFunciones.nvl(
                    registroAux.getCampos().get(
                                    GeneralParameterEnum.CODIGO.getName()),
                    "").toString();
    }
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indCentroCosto
     * 
     * @return  indCentroCosto
     */
	public boolean getIndCentroCosto() {
	    return indCentroCosto;
	}
    /**
     * Asigna la variable  indCentroCosto
     * 
     * @param  indCentroCosto
     * Variable a asignar en  indCentroCosto
     */
    public void setIndCentroCosto(boolean indCentroCosto) {
        this.indCentroCosto = indCentroCosto;
    }
    /**
     * Retorna la variable indReferencia
     * 
     * @return  indReferencia
     */
    public boolean getIndReferencia() {
        return indReferencia;
    }

    /**
     * Asigna la variable indReferencia
     * 
     * @param indReferencia
     * Variable a asignar en indReferencia
     */
    public void setIndReferencia(boolean indReferencia) {
        this.indReferencia = indReferencia;
    }
    /**
     * Retorna la variable indAuxiliar
     * 
     * @return  indAuxiliar
     */
    public boolean getIndAuxiliar() {
        return indAuxiliar;
    }

    /**
     * Asigna la variable  indAuxiliar
     * 
     * @param  indAuxiliar
     * Variable a asignar en  indAuxiliar
     */
    public void setIndAuxiliar(boolean indAuxiliar) {
        this.indAuxiliar = indAuxiliar;
    }
    /**
     * Retorna la variable indFuenteRecursos
     * 
     * @return  indFuenteRecursos
     */
    public boolean getIndFuenteRecursos() {
        return indFuenteRecursos;
    }

    /**
     * Asigna la variable indFuenteRecursos
     * 
     * @param indFuenteRecursos
     * Variable a asignar en indFuenteRecursos
     */
    public void setIndFuenteRecursos(boolean indFuenteRecursos) {
        this.indFuenteRecursos = indFuenteRecursos;
    }
    /**
     * Retorna la variable cuentaInicial
     * 
     * @return  cuentaInicial
     */
public String getCuentaInicial() {
        return cuentaInicial;
    }
    /**
     * Asigna la variable  cuentaInicial
     * 
     * @param  cuentaInicial
     * Variable a asignar en  cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }
    /**
     * Retorna la variable cuentaFinal
     * 
     * @return  cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }
    /**
     * Asigna la variable  cuentaFinal
     * 
     * @param  cuentaFinal
     * Variable a asignar en  cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }
    /**
     * Retorna la variable anio
     * 
     * @return  anio
     */
    public int getAnio() {
        return anio;
    }
    /**
     * Asigna la variable  anio
     * 
     * @param  anio
     * Variable a asignar en  anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }
    /**
     * Retorna la variable mes
     * 
     * @return  mes
     */
    public int getMes() {
        return mes;
    }
    /**
     * Asigna la variable  mes
     * 
     * @param  mes
     * Variable a asignar en  mes
     */

    public void setMes(int mes) {
        this.mes = mes;
    }
    /**
     * Retorna la variable centroCostoInicial
     * 
     * @return  centroCostoInicial
     */
public String getCentroCostoInicial() {
        return centroCostoInicial;
    }
    /**
     * Asigna la variable  centroCostoInicial
     * 
     * @param  centroCostoInicial
     * Variable a asignar en  centroCostoInicial
     */
    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }
    /**
     * Retorna la variable centroCostoFinal
     * 
     * @return  centroCostoFinal
     */
public String getCentroCostoFinal() {
        return centroCostoFinal;
    }
    /**
     * Asigna la variable  centroCostoFinal
     * 
     * @param  centroCostoFinal
     * Variable a asignar en  centroCostoFinal
     */
    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
    }
    /**
     * Retorna la variable referenciaInicial
     * 
     * @return  referenciaInicial
     */
public String getReferenciaInicial() {
        return referenciaInicial;
    }
    /**
     * Asigna la variable  referenciaInicial
     * 
     * @param  referenciaInicial
     * Variable a asignar en  referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }
    /**
     * Retorna la variable referenciaFinal
     * 
     * @return  referenciaFinal
     */
public String getReferenciaFinal() {
        return referenciaFinal;
    }
    /**
     * Asigna la variable  referenciaFinal
     * 
     * @param  referenciaFinal
     * Variable a asignar en  referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }
    /**
     * Retorna la variable fuenteInicial
     * 
     * @return  fuenteInicial
     */
public String getFuenteInicial() {
        return fuenteInicial;
    }
    /**
     * Asigna la variable  fuenteInicial
     * 
     * @param  fuenteInicial
     * Variable a asignar en  fuenteInicial
     */
    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }
    /**
     * Retorna la variable fuenteFinal
     * 
     * @return  fuenteFinal
     */
public String getFuenteFinal() {
        return fuenteFinal;
    }
    /**
     * Asigna la variable  fuenteFinal
     * 
     * @param  fuenteFinal
     * Variable a asignar en  fuenteFinal
     */
    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }
    /**
     * Retorna la variable auxiliarInicial
     * 
     * @return  auxiliarInicial
     */
public String getAuxiliarInicial() {
        return auxiliarInicial;
    }
    /**
     * Asigna la variable  auxiliarInicial
     * 
     * @param  auxiliarInicial
     * Variable a asignar en  auxiliarInicial
     */
    public void setAuxiliarInicial(String auxiliarInicial) {
        this.auxiliarInicial = auxiliarInicial;
    }
    /**
     * Retorna la variable auxiliarFinal
     * 
     * @return  auxiliarFinal
     */
public String getAuxiliarFinal() {
        return auxiliarFinal;
    }
    /**
     * Asigna la variable  auxiliarFinal
     * 
     * @param  auxiliarFinal
     * Variable a asignar en  auxiliarFinal
     */
    public void setAuxiliarFinal(String auxiliarFinal) {
        this.auxiliarFinal = auxiliarFinal;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
public List<Registro> getListaAno() {
        return listaAno;
    }
    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en  listaAno
     */
public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }
    /**
     * Asigna la lista listaCuentaInicial
     * 
     * @param listaCuentaInicial
     * Variable a asignar en  listaCuentaInicial
     */
    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }
    /**
     * Retorna la lista listaCuentaFinal
     * 
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }
    /**
     * Asigna la lista listaCuentaFinal
     * 
     * @param listaCuentaFinal
     * Variable a asignar en  listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    /**
     * Retorna la lista listaCentroCostoInicial
     * 
     * @return listaCentroCostoInicial
     */
    public RegistroDataModelImpl getListaCentroCostoInicial() {
        return listaCentroCostoInicial;
    }
    /**
     * Asigna la lista listaCentroCostoInicial
     * 
     * @param listaCentroCostoInicial
     * Variable a asignar en  listaCentroCostoInicial
     */
    public void setListaCentroCostoInicial(RegistroDataModelImpl listaCentroCostoInicial) {
        this.listaCentroCostoInicial = listaCentroCostoInicial;
    }
    /**
     * Retorna la lista listaCentroCostoFinal
     * 
     * @return listaCentroCostoFinal
     */
    public RegistroDataModelImpl getListaCentroCostoFinal() {
        return listaCentroCostoFinal;
    }
    /**
     * Asigna la lista listaCentroCostoFinal
     * 
     * @param listaCentroCostoFinal
     * Variable a asignar en  listaCentroCostoFinal
     */
    public void setListaCentroCostoFinal(RegistroDataModelImpl listaCentroCostoFinal) {
        this.listaCentroCostoFinal = listaCentroCostoFinal;
    }
    /**
     * Retorna la lista listaReferenciaInicial
     * 
     * @return listaReferenciaInicial
     */
    public RegistroDataModelImpl getListaReferenciaInicial() {
        return listaReferenciaInicial;
    }
    /**
     * Asigna la lista listaReferenciaInicial
     * 
     * @param listaReferenciaInicial
     * Variable a asignar en  listaReferenciaInicial
     */
    public void setListaReferenciaInicial(RegistroDataModelImpl listaReferenciaInicial) {
        this.listaReferenciaInicial = listaReferenciaInicial;
    }
    /**
     * Retorna la lista listaReferenciaFinal
     * 
     * @return listaReferenciaFinal
     */
    public RegistroDataModelImpl getListaReferenciaFinal() {
        return listaReferenciaFinal;
    }
    /**
     * Asigna la lista listaReferenciaFinal
     * 
     * @param listaReferenciaFinal
     * Variable a asignar en  listaReferenciaFinal
     */
    public void setListaReferenciaFinal(RegistroDataModelImpl listaReferenciaFinal) {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }
    /**
     * Retorna la lista listaFuenteInicial
     * 
     * @return listaFuenteInicial
     */
    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }
    /**
     * Asigna la lista listaFuenteInicial
     * 
     * @param listaFuenteInicial
     * Variable a asignar en  listaFuenteInicial
     */
    public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }
    /**
     * Retorna la lista listaFuenteFinal
     * 
     * @return listaFuenteFinal
     */
    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }
    /**
     * Asigna la lista listaFuenteFinal
     * 
     * @param listaFuenteFinal
     * Variable a asignar en  listaFuenteFinal
     */
    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }
    /**
     * Retorna la lista listaAuxiliarInicial
     * 
     * @return listaAuxiliarInicial
     */
    public RegistroDataModelImpl getListaAuxiliarInicial() {
        return listaAuxiliarInicial;
    }
    /**
     * Asigna la lista listaAuxiliarInicial
     * 
     * @param listaAuxiliarInicial
     * Variable a asignar en  listaAuxiliarInicial
     */
    public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }
    /**
     * Retorna la lista listaAuxiliarFinal
     * 
     * @return listaAuxiliarFinal
     */
    public RegistroDataModelImpl getListaAuxiliarFinal() {
        return listaAuxiliarFinal;
    }
    /**
     * Asigna la lista listaAuxiliarFinal
     * 
     * @param listaAuxiliarFinal
     * Variable a asignar en  listaAuxiliarFinal
     */
    public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }
//</SET_GET_LISTAS_COMBO_GRANDE>
}
