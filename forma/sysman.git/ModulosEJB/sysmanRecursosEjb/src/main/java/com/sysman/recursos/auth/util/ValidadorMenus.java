/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.recursos.auth.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.logica.Usuario;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @author cmanrique
 */
public class ValidadorMenus {

	private static final String TAG_FORMULARIO = "FR";

	private ValidadorMenus() {

	}

	public static void validarMenu(Element element, List<String> excluidos, Usuario usuario, String compania,
			EjbSysmanUtilRemote ejbSysmanUtil) throws SystemException {
		if (element.getAttribute("A") != null && !element.getAttribute("A").isEmpty()) {
			switch (Integer.parseInt(element.getAttribute("A"))) {

			case SysmanConstantes.MODULO_BANCOPROY:
				validarMenuBancoProyectos(element, excluidos, usuario, compania, ejbSysmanUtil);
				break;
			case SysmanConstantes.MODULO_FACTURACION_SERVICIOS_PUBLICOS:
				validarMenuServiciosPublicos(element, excluidos, compania, ejbSysmanUtil);
				break;
			case SysmanConstantes.MODULO_CONTRATOS:
				validarMenuContratos(element, excluidos, compania, ejbSysmanUtil);
				break;
			case SysmanConstantes.MODULO_NOMINA:
				validarMenusNomina(element, excluidos, compania, ejbSysmanUtil);
				break;
			case SysmanConstantes.MODULO_ENTES_DE_CONTROL:
				validarMenusChipFut(element, excluidos, compania, ejbSysmanUtil);
			default:
			}
		}
	}

	/**
	 * Validaciones para men&uacute;s del modulo Entes de Control.
	 * 
	 * @param element
	 * @param excluidos
	 * @param compania
	 * @param ejbSysmanUtil
	 * @throws SystemException
	 */
	private static void validarMenusNomina(Element element, List<String> excluidos, String compania,
			EjbSysmanUtilRemote ejbSysmanUtil) throws SystemException {
		String modulo = String.valueOf(SysmanConstantes.MODULO_NOMINA);

		if ("60303020802".equals(element.getAttribute("id"))) {
			String parametro = ejbSysmanUtil.consultarParametro(compania, "ENTIDAD DEL ORDEN NACIONAL O TERRITORIAL",
					modulo, new Date(), true);
			parametro = parametro == null ? "NO" : parametro;
			if (!"NACIONAL".equals(parametro)) {
				borrarMenu(element, true, modulo, excluidos, true);
			}
		}
	}

	private static void validarMenusChipFut(Element element, List<String> excluidos, String compania,
			EjbSysmanUtilRemote ejbSysmanUtil) throws SystemException {
		String modulo = String.valueOf(SysmanConstantes.MODULO_ENTES_DE_CONTROL);

		if ("99020203".equals(element.getAttribute("id"))) {
			String parametro = ejbSysmanUtil.consultarParametro(compania, "MANEJA SEGUIMIENTO RECIPROCAS", modulo,
					new Date(), true);
			parametro = parametro == null ? "NO" : parametro;
			if (!"SI".equals(parametro)) {
				borrarMenu(element, true, modulo, excluidos, true);
			}
		}
	}

	/**
	 *
	 * @param element
	 *            La opcion de menu que se desea borrar
	 * @param padre
	 *            Al invocar el metodo siempre debe estar en true
	 * @param aplicacion
	 *            La aplicaciòn a la cual pertenece la opccion de menu
	 * @param excluidos
	 *            Mapa de los exluidos llega automaticamente.
	 * @param borrarPermisoDeFormularios
	 *            Indica si al borrar el menu tambien se deben eliminar los permisos
	 *            del formulario invocado, queda a discresion del desarrollador,
	 *            tener en cuenta que si el formulario es llamado desde otra opcion
	 *            de menu diferente a la se va a borrar el valor de este parametro
	 *            debe ser false
	 */
	private static void borrarMenu(Element element, boolean padre, String aplicacion, List<String> excluidos,
			boolean borrarPermisoDeFormularios) {

		element.getChildNodes();
		Element node;
		if (borrarPermisoDeFormularios) {
			if (element.getAttribute("CO").equals(TAG_FORMULARIO) && (element.getAttribute("P") != null)
					&& !element.getAttribute("P").isEmpty()) {
				excluidos.add(new StringBuilder(element.getAttribute("P")).append(",").append(aplicacion).toString());

			}

			for (int i = 0; i < element.getChildNodes().getLength(); i++) {
				node = (Element) element.getChildNodes().item(i);
				if (node.getAttribute("CO").equals(TAG_FORMULARIO) && (node.getAttribute("P") != null)
						&& !node.getAttribute("P").isEmpty()) {
					excluidos.add(
							new StringBuilder(element.getAttribute("P")).append(",").append(aplicacion).toString());

				}
				borrarMenu(node, false, aplicacion, excluidos, borrarPermisoDeFormularios);
			}
		}
		if (padre) {
			element.getParentNode().removeChild(element);
		}
	}

	private static void validarMenuBancoProyectos(Element element, List<String> excluidos, Usuario usuario,
			String compania, EjbSysmanUtilRemote ejbSysmanUtil) throws SystemException {

		ResourceBundle parametros = ResourceBundle.getBundle(SysmanConstantes.RUTA_PARAMETROS);
		if ("52030203".equals(element.getAttribute("id")) && usuario.getDependencia() == null) {
			borrarMenu(element, true, "52", excluidos, true);

		}
		if ("52030302".equals(element.getAttribute("id")) && usuario.getDependencia() == null) {
			borrarMenu(element, true, "52", excluidos, true);

		}
		if ("520127".equals(element.getAttribute("id"))) {
			if ("NO".equals(ejbSysmanUtil.consultarParametro(compania,
					parametros.getString("PR_CONFIGURA_IDENTIFICACION_EVALUACION"),
					String.valueOf(SysmanConstantes.MODULO_BANCOPROY), new Date(), true))) {
				borrarMenu(element, true, "52", excluidos, true);
			}
		}

	}

	private static void validarMenuServiciosPublicos(Element element, List<String> excluidos, String compania,
			EjbSysmanUtilRemote ejbSysmanUtil) throws SystemException {
		ResourceBundle parametros = ResourceBundle.getBundle(SysmanConstantes.RUTA_PARAMETROS);

		if ("74070248".equals(element.getAttribute("id")) || "74070265".equals(element.getAttribute("id"))) {
			String parametro = ejbSysmanUtil.consultarParametro(compania, "INFORME DE OPERACIONES SIMPLIFICADO",
					String.valueOf(SysmanConstantes.MODULO_FACTURACION_SERVICIOS_PUBLICOS), new Date(), true);

			parametro = parametro == null ? "NO" : parametro;
			if ("74070248".equals(element.getAttribute("id")) && "NO".equals(parametro)) {
				borrarMenu(element, true, "74", excluidos, true);

			}
			if ("74070265".equals(element.getAttribute("id")) && "SI".equals(parametro)) {
				borrarMenu(element, true, "74", excluidos, true);

			}

		} else if ("740518".equals(element.getAttribute("id")) || "74070256".equals(element.getAttribute("id"))) {

			String manejaDispoSicion = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA DISPOSICION FINAL DETALLADO",
							String.valueOf(SysmanConstantes.MODULO_FACTURACION_SERVICIOS_PUBLICOS), new Date(), true),
					"NO").toString();
			if ("NO".equals(manejaDispoSicion)) {
				borrarMenu(element, true, "74", excluidos, false);
			}
		} else if ("740407".equals(element.getAttribute("id")) || "7404022".equals(element.getAttribute("id"))) {

			String parametro = ejbSysmanUtil.consultarParametro(compania,
					parametros.getString("PR_PROCESO_UNICO_CRITICA_DESVIACION"),
					String.valueOf(SysmanConstantes.MODULO_FACTURACION_SERVICIOS_PUBLICOS), new Date(), true);

			parametro = parametro == null ? "NO" : parametro;
			if ("7404022".equals(element.getAttribute("id")) && "SI".equals(parametro)) {
				borrarMenu(element, true, "74", excluidos, false);

			}
			if ("740407".equals(element.getAttribute("id")) && "NO".equals(parametro)) {
				borrarMenu(element, true, "74", excluidos, false);

			}

		}

	}

	private static void validarMenuContratos(Element element, List<String> excluidos, String compania,
			EjbSysmanUtilRemote ejbSysmanUtil) throws SystemException {

		if ("90115".equals(element.getAttribute("id"))) {
			String parametro = ejbSysmanUtil.consultarParametro(compania, "IMPRIME INFORME F50_7_PERSONAL Y COSTOS",
					String.valueOf(SysmanConstantes.MODULO_CONTRATOS), new Date(), true);
			if ("NO".equals(parametro)) {
				borrarMenu(element, true, "9", excluidos, false);
			}
		}
	}

	public static String getXmlMenus(String rta, String compania, List<String> excluidos, Usuario usuario,
			EjbSysmanUtilRemote ejbSysmanUtil) throws SysmanException {
		String menus = "";
		if (!rta.isEmpty()) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			try {
				docBuilder = docFactory.newDocumentBuilder();

				InputSource is = new InputSource(new StringReader(rta));
				Document doc = docBuilder.parse(is);
				NodeList nodeList = doc.getElementsByTagName("M");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Element element = (Element) nodeList.item(i);
					validarMenu(element, excluidos, usuario, compania, ejbSysmanUtil);
				}
				nodeList = doc.getElementsByTagName("P");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Element element = (Element) nodeList.item(i);
					validarMenu(element, excluidos, usuario, compania, ejbSysmanUtil);
				}
				menus = getStringFromDocument(doc);
			} catch (ParserConfigurationException | SAXException | IOException | SystemException e) {
				throw new SysmanException(e, e.getMessage());
			}
		}
		return menus;
	}

	private static String getStringFromDocument(Document doc) throws SysmanException {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException e) {
			throw new SysmanException(e, e.getMessage());
		}
	}

}
