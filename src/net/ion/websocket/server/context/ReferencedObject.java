package net.ion.websocket.server.context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.common.EnumClass.Scope;

public class ReferencedObject {

	private ServiceContext context;
	private String id;
	private Scope scope;
	private XMLConfig config;

	private Object firstObject = null;
	private ThreadLocal<Object> CURRENT = new ThreadLocal<Object>();

	private ReferencedObject(ServiceContext context, String id, Scope scope, XMLConfig config) throws InstanceCreationException {
		this.context = context;
		this.id = id;
		this.scope = scope;
		this.config = config;
		this.firstObject = createInstance(context, config);
	}

	public static ReferencedObject create(ServiceContext context, String id, Scope scope, XMLConfig config) throws InstanceCreationException {
		return new ReferencedObject(context, id, scope, config);
	}

	public synchronized Object valueObject() throws InstanceCreationException {
		if (scope == Scope.Application) {
			return firstObject;
		} else if (scope == Scope.Thread) {
			Object val = CURRENT.get();
			if (val == null) {
				val = createInstance(context, config);
				CURRENT.set(val);
			}
			return val;
		} else if (scope == Scope.Request) {
			return createInstance(context, config);
		}
		throw new IllegalArgumentException("current not support scope : " + scope);
	}

	public String toString() {
		return String.format("id:%1$s, scope:%2$s, class:%3$s", id, scope, config.getString("class-name"));
	}

	public Object createInstance(ServiceContext context, XMLConfig config) throws InstanceCreationException {
		Object result = ConfigCreator.createConfiguredInstance(config);
		try {
			final String methodName = config.getString("call[@method]");
			if (StringUtil.isNotBlank(methodName)) {
				Method method = result.getClass().getMethod(methodName, ServiceContext.class);
				method.invoke(result, context);
			}
		} catch (SecurityException ex) {
			throw new InstanceCreationException(ex) ;
		} catch (IllegalArgumentException ex) {
			throw new InstanceCreationException(ex) ;
		} catch (NoSuchMethodException ex) {
			throw new InstanceCreationException(ex) ;
		} catch (IllegalAccessException ex) {
			throw new InstanceCreationException(ex) ;
		} catch (InvocationTargetException ex) {
			throw new InstanceCreationException(ex) ;
		}
		return result;
	}

	public Scope getScope() {
		return scope;
	}


}
