package net.ion.websocket.server.context;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.config.Releasable;
import net.ion.radon.core.context.IParentContext;
import net.ion.websocket.common.EnumClass.Scope;
import net.ion.websocket.common.api.WebSocketServer;

public class ServiceContext {

	private InnerContext<Object> context = new InnerContext<Object>();
	private static ServiceContext SELF;

	private List<Releasable> relList = ListUtil.newList();
	private final UncaughtExceptionHandler EXCEPTION_HANDLER = new UncaughtExceptionHandler() {
		public void uncaughtException(Thread t, Throwable e) {
			Debug.warn(t, e.getMessage());
		}
	};

	private ServiceContext() {
	}

	public static ServiceContext createRoot() {
		ServiceContext result = new ServiceContext();
		SELF = result;
		return result;
	}

	public ServiceContext createChildContext() {
		ServiceContext newChild = new ServiceContext();
		newChild.putAttribute(IParentContext.class.getCanonicalName(), this);

		return newChild;
	}

	public ServiceContext getParentContext() {
		return getSelfAttributeObject(IParentContext.class.getCanonicalName(), ServiceContext.class);
	}

	public Object getAttributeObject(String key) {
		return getAttributeObject(key, Object.class);
	}

	public <T> T getAttributeObject(String key, Class<T> T) {
		return getAttributeObject(key, null, T);
	}

	public <T> T getAttributeObject(String key, T defaultValue, Class<T> T) {
		ServiceContext current = this;
		while (current != null) {
			Object value = current.getSelfAttributeObject(key, Object.class);
			if (value != null && T.isInstance(value)) {
				return (T) value;
			}
			current = current.getParentContext();
		}
		return defaultValue;
	}

	public <T> T getSelfAttributeObject(String key, Class<T> T) {
		return getSelfAttributeObject(key, T, null);
	}

	public <T> T getSelfAttributeObject(String key, Class<T> T, T defaultValue) {
		try {
			Object value = context.getAttributes().get(key);
			if (value != null && value instanceof ReferencedObject) {
				return (T) ((ReferencedObject) value).valueObject();
			}
			return (T.isInstance(value)) ? (T) value : defaultValue;
		} catch (InstanceCreationException ex) {
			ex.printStackTrace();
			throw new IllegalStateException(ex);
		}
	}

	public Object putAttribute(String key, Object value) {
		return context.put(key, value);
	}

	public boolean contains(Object key) {
		return context.getAttributes().containsKey(key) ;
	}

	public void setAttributes(Map<String, Object> attributes) {
		context.setAttributes(attributes);
	}

	public String toString() {
		return "Context[" + hashCode() + "] : " + context.getAttributes();
	}

	public void addReleasable(Releasable releasable) {
		relList.add(releasable);
	}

	public final static ServiceContext fake() {
		return SELF;
	}

	private void release() throws InstanceCreationException {
		for (Entry<String, Object> entry : context.getApplicationValues().entrySet()) {
			if (entry.getValue() instanceof Releasable) {
				((Releasable) entry.getValue()).doRelease();
			} else if (entry.getValue() instanceof IEndOn) {
				((IEndOn) entry.getValue()).onEnd() ;
			}
		}
		for (Releasable r : relList) {
			r.doRelease();
		}
	}

	public void onEnd() throws InstanceCreationException {
		release();
	}

	public void onStart(WebSocketServer server) throws InstanceCreationException {
		for (Entry<String, Object> entry : context.getApplicationValues().entrySet()) {
			if (entry.getValue() instanceof IStartOn) {
				((IStartOn) entry.getValue()).onStart(this, server);
			}
		}
	}

	public UncaughtExceptionHandler getExcetpionHandler() {
		return EXCEPTION_HANDLER;
	}
}

class InnerContext<V> {

	private ConcurrentMap<String, V> map = new ConcurrentHashMap<String, V>();

	InnerContext() {

	}

	public V put(String key, V value) {
		return map.put(key, value);
	}

	public void setAttributes(Map<String, V> attributes) {
		map.putAll(attributes);
	}

	Map<String, V> getAttributes() {
		return map;
	}

	Map<String, V> getApplicationValues() {
		Map<String, V> result = MapUtil.newMap();
		for (Entry<String, V> entry : getAttributes().entrySet()) {
			if (entry.getValue() instanceof ReferencedObject) {
				if (((ReferencedObject) entry.getValue()).getScope() == Scope.Application) {
					result.put(entry.getKey(), entry.getValue());
				}
			} else {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

}