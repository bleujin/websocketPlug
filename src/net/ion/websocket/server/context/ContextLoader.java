package net.ion.websocket.server.context;

import java.sql.SQLException;
import java.util.List;

import net.ion.framework.db.IDBController;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.config.DBReleasable;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.common.EnumClass.Scope;

import org.apache.commons.configuration.ConfigurationException;

public class ContextLoader {

	public static ServiceContext load(XMLConfig contextConfig) throws InstanceCreationException, ConfigurationException {
		// Debug.line(context.getZone(), parentConfig);

		ServiceContext context = ServiceContext.createRoot();
		setStringAttribute(context, contextConfig);
		setObjectAttribute(context, contextConfig);
		setConnectionAttribute(context, contextConfig);
		return context;
	}

	private static void setObjectAttribute(ServiceContext context, XMLConfig contextConfig) throws InstanceCreationException {
		List<XMLConfig> configs = contextConfig.children("configured-object");
		for (XMLConfig config : configs) {
			// Object created = ConfigCreator.createConfiguredInstance(objOfConfig) ;
			String id = config.getAttributeValue("id");
			Scope scope = Scope.valueOf(StringUtil.capitalize(config.getAttributeValue("scope")));
			ReferencedObject refObj = ReferencedObject.create(context, id, scope, config);

			context.putAttribute(id, refObj);
		}
	}

	private static void setStringAttribute(ServiceContext context, XMLConfig config) {
		List<XMLConfig> children = config.children("attribute");
		for (XMLConfig child : children) {
			final String id = child.getAttributeValue("id");
			// final String type = child.getString("[@type]");
			if (StringUtil.isBlank(id)) {
				Debug.warn("not found attribute id : blank id");
				continue;
			}
			if (context.contains(id)) {
				Debug.warn("duplicate attribute id : " + id + " ignored");
				continue;
			}

			Object attrValue = ObjectUtil.coalesce(child.getElementValue(), ObjectUtil.NULL);

			context.putAttribute(id, attrValue);
		}
	}

	private static void setConnectionAttribute(ServiceContext context, XMLConfig config) throws InstanceCreationException, ConfigurationException {
		List<XMLConfig> connections = config.children("connection");
		for (XMLConfig cconfig : connections) {
			String connectionId = cconfig.getAttributeValue("id");
			try {
				final IDBController dc = RDBConnection.createDC(cconfig);
				dc.initSelf();
				context.putAttribute(connectionId, dc);
				context.addReleasable(DBReleasable.create(dc));
			} catch (SQLException ex) {
				ex.printStackTrace();
				Debug.warn(connectionId + " not initialized...");
			}
		}
	}

}