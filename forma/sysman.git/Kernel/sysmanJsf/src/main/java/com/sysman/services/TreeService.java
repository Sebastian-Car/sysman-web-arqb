/*-
 * TreeService.java
 *
 * 1.0
 * 
 * 29/09/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.sysman.componentes.TreeNodeDescriptor;
import com.sysman.dao.Registro;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;

/**
 * Clase para trabajar con el componente Tree de Primefaces. Dicho componente es
 * usado para mostrar datos de forma jerarquica y crear puntos de navegacion.
 * 
 * @version 1.0, 29/09/2016
 * @author jrodrigueza
 *
 */
public class TreeService {

	/**
	 * Nombre de la columna que trae el codigo o identificador del elemento.
	 */
	private String columnaId;
	/**
	 * Nombre de la columna que trae la descripcion del elemento.
	 */
	private String columnaValor;
	/**
	 * Nombre de la columna que trae el tipo de elemento. Esto con el fin de aplicar
	 * estilos a cada nodo segun corresponda.
	 */
	private String columnaTipo;
	/**
	 * Tamano de los primeros elementos o elementos padres que se pintan en el
	 * arbol.
	 */
	private int baseSize;
	/**
	 * Tamano maximo que puede tener el identificador de un elemento.
	 */
	private int maxSize;
	/**
	 * Consulta que define la estructura del arbol.
	 */
	private String consulta;
	/**
	 * Cadena para seleccionar todas las columnas.
	 */
	private String selectTodo;
	/**
	 * Criterio que permite extraer la longitud del identificador del nodo, segun
	 * {@link #columnaId}
	 */
	private String criterioLength;
	/**
	 * Criterio que permite substraer partes del identificador del nodo.
	 */
	private String criterioSubstr;
	/**
	 * Para saber si al arbol fue construido de manera correcta.
	 */
	private boolean cargado;

	/**
	 * Creacion de estructura tipo arbol segun la consulta dada. Los primeros
	 * elementos vienen dados segun el tamano base.
	 * 
	 * @param consulta
	 *            Consulta que define la estructura del arbol. Debe contener una
	 *            columna que traiga los codigos o identificador unico del elemento,
	 *            y otra que traiga el valor o descripcion del mismo.
	 * @return Nodos para anexar a la raiz.
	 * @throws SysmanException
	 *             en caso de que se presente algun error en el proceso de
	 *             construccion del arbol
	 */
	public TreeNode crearArbol(String consulta) throws SysmanException {
		this.consulta = consulta;
		cargado = false;
		TreeNode root = new DefaultTreeNode(new TreeNodeDescriptor("id", "name"), null);
		try {
			traerMetaDatos();
			baseSize = getBaseSize();
			crearElementos(root);
		} catch (NamingException | SQLException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
			throw new SysmanException("Error al construir el arbol: " + e.getMessage());
		} finally {
			cargado = true;
		}
		return root;
	}

	/**
	 * Trae los metadatos necesarios para la creacion de los nodos del arbol.
	 * 
	 * @throws NamingException
	 *             cuando el nombre de la conexion no pueda ser resuelto.
	 * @throws SQLException
	 *             en caso de que la consulta a ejecutar sea invalida.
	 */
	private void traerMetaDatos() throws NamingException, SQLException {
		ResultSetMetaData metaData = null;
		Statement statement = null;
		ResultSet resultSet = null;
		ConectorPool conectorPool = new ConectorPool();
		try {
			conectorPool.conectar(ConectorPool.ESQUEMA_SYSMAN);
			statement = conectorPool.getConection().createStatement();
			resultSet = statement.executeQuery(consulta);
			metaData = resultSet.getMetaData();
			int numeroColumnas = metaData.getColumnCount();
			maxSize = metaData.getPrecision(1);
			columnaId = metaData.getColumnName(1);
			columnaValor = metaData.getColumnLabel(2);
			if (numeroColumnas == 3) {
				columnaTipo = metaData.getColumnLabel(3);
			}
			selectTodo = "SELECT * FROM (\n";
			criterioLength = (SysmanFunciones.esBdSqlServer() ? " LEN( " : " LENGTH( ") + columnaId + " ) =  ? "
					+ "\n ORDER BY " + columnaId;
			criterioSubstr = (SysmanFunciones.esBdSqlServer() ? " SUBSTRING( " : " SUBSTR( ") + columnaId
					+ ", 1, ? ) = ";
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
			conectorPool.getConection().close();
		}
	}

	/**
	 * Trae el tamano del identificador de los nodos padres.
	 * 
	 * @return tamano inicial
	 * @throws NamingException
	 *             cuando el nombre de la conexion no pueda ser resuelto.
	 * @throws SQLException
	 *             en caso de que la consulta a ejecutar sea invalida.
	 */
	private int getBaseSize() throws NamingException, SQLException {
		int size = 0;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ConectorPool conectorPool = new ConectorPool();

		String sql = selectTodo + consulta + (SysmanFunciones.esBdSqlServer() ? "\n)X WHERE " : "\n) WHERE ")
				+ criterioLength;

		if (SysmanFunciones.esBdSqlServer()) {

			String strFinal = SysmanUtl.strTraductorSql(sql);
			strFinal = SysmanUtl.strTraductorOnLine(strFinal);
			sql = strFinal;

		}

		try {
			conectorPool.conectar(ConectorPool.ESQUEMA_SYSMAN);
			statement = conectorPool.getConection().prepareStatement(sql);
			for (int i = 0; i < maxSize; i++) {
				statement.setInt(1, i);
				resultSet = statement.executeQuery();
				if (resultSet.next()) {
					size = i;
					break;
				}
				resultSet.close();
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
			conectorPool.getConection().close();
		}
		return size;
	}

	private TreeNode crearNodo(TreeNode parent, String id, String name, String tipo) {
		TreeNodeDescriptor nodeDescriptor = new TreeNodeDescriptor(id, name);
		TreeNode node = new DefaultTreeNode(nodeDescriptor, parent);
		if (columnaTipo != null && tipo != null && !("null".equals(tipo))) {
			node.setType(tipo);
		}
		return node;
	}

	/**
	 * Crea los nodos padres (nodos hijos del nodo superior del arbol).
	 * 
	 * @param root
	 *            Raiz o nodo superior del arbol.
	 * @throws NamingException
	 *             cuando el nombre de la conexion no pueda ser resuelto.
	 * @throws SQLException
	 *             en caso de que la consulta a ejecutar sea invalida.
	 */
	private void crearElementos(TreeNode root) throws NamingException, SQLException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ConectorPool conectorPool = new ConectorPool();

		String sql = selectTodo + consulta + (SysmanFunciones.esBdSqlServer() ? "\n)X WHERE" : "\n) WHERE")
				+ criterioLength;

		if (SysmanFunciones.esBdSqlServer()) {

			String strFinal = SysmanUtl.strTraductorSql(sql);
			strFinal = SysmanUtl.strTraductorOnLine(strFinal);
			sql = strFinal;

		}

		try {
			conectorPool.conectar(ConectorPool.ESQUEMA_SYSMAN);
			statement = conectorPool.getConection().prepareStatement(sql);
			statement.setInt(1, baseSize);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String id = resultSet.getString(1);
				String name = resultSet.getString(2);
				String type = columnaTipo != null ? resultSet.getString(3) : null;
				TreeNode node = crearNodo(root, id, name, type);
				traerHijos(node);
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
			conectorPool.getConection().close();
		}
	}

	/**
	 * Realiza la carga de los nodos hijos.
	 * 
	 * @param parent
	 *            Nodo padre
	 */
	private void traerHijos(TreeNode parent) {
		StringBuilder sql = new StringBuilder(selectTodo + consulta);
		String id = ((TreeNodeDescriptor) parent.getData()).getId();

		sql.append((SysmanFunciones.esBdSqlServer() ? "\n) X WHERE" : "\n) WHERE")
				+ criterioSubstr.replace("?", String.valueOf(id.length())) + "'" + id + "'");

		FormContinuoService service = FormContinuoService.getInstance();
		for (int i = id.length() + 1; i <= maxSize; i++) {
			String aux = sql.toString() + "\n   AND" + criterioLength.replace("?", String.valueOf(i));

			if (SysmanFunciones.esBdSqlServer()) {

				String strFinal = SysmanUtl.strTraductorSql(aux);
				strFinal = SysmanUtl.strTraductorOnLine(strFinal);
				aux = strFinal;

			}

			List<Registro> cuentas = service.getListado(ConectorPool.ESQUEMA_SYSMAN, aux);
			if (!cuentas.isEmpty()) {
				for (Registro registro : cuentas) {
					Map<String, Object> campos = registro.getCampos();
					String clave = String.valueOf(campos.get(columnaId));
					String valor = String.valueOf(campos.get(columnaValor));
					String tipo = String.valueOf(campos.get(columnaTipo));
					TreeNode node = crearNodo(parent, clave, valor, tipo);
					parent.getChildren().add(node);
				}
				break;
			}
		}
	}

	/**
	 * Trae los hijos del nodo padre. Utilizado en el evento <i>onExpandNode</i>
	 * para cargar los hijos de los hijos simulando una carga por demanda.
	 * 
	 * @param parent
	 *            Nodo padre
	 */
	public void traerDescendientes(TreeNode parent) {
		if (isCargado()) {
			List<TreeNode> hijos = parent.getChildren();
			if (!hijos.isEmpty()) {
				for (TreeNode hijo : hijos) {
					traerHijos(hijo);
				}
			}
		}
	}

	/**
	 * Trae el objeto que representa el nodo.
	 * 
	 * @param node
	 *            Nodo
	 * @return Objeto de tipo arbol.
	 * @see {@link com.sysman.componentes.TreeNodeDescriptor}
	 */
	private TreeNodeDescriptor getData(TreeNode node) {
		return (TreeNodeDescriptor) node.getData();
	}

	/**
	 * Obtiene el codigo unico que identifica al nodo.
	 * 
	 * @param node
	 *            Nodo
	 * @return identificador de nodo
	 */
	public String getIdentificador(TreeNode node) {
		return getData(node).getId();
	}

	/**
	 * Trae el texto que describe al nodo.
	 * 
	 * @param node
	 *            Nodo
	 * @return texto del nodo
	 */
	public String getNombre(TreeNode node) {
		return getData(node).getName();
	}

	/**
	 * Trae la consulta que define la estructura del arbol
	 * 
	 * @return
	 */
	public String getConsulta() {
		return consulta;
	}

	/**
	 * Permite saber si al arbol fue construido de manera correcta.
	 * 
	 * @return
	 */
	public boolean isCargado() {
		return cargado;
	}

}