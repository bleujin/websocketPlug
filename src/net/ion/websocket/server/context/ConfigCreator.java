package net.ion.websocket.server.context;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.InstanceCreator;
import net.ion.radon.core.config.XMLConfig;

public class ConfigCreator {
	// �� Ŭ������ ���� ���ؿ� -,.-
	// ���� static class
	private ConfigCreator() {
	}

	public static Object createConfiguredInstance(XMLConfig config) throws InstanceCreationException {
		/*
		 * <configured-object> <class-name>java.util.logging.FileHandler</class-name> <constructor> <constructor-param> <description>pattern</description> <type>java.lang.String</type> <value>%h/java%u.log</value> </constructor-param>
		 * <constructor-param> <description>limit</description> <type>int</type> <value>50000</value> </constructor-param> <constructor-param> <description>count</description> <type>int</type> <value>1</value> </constructor-param> <constructor-param>
		 * <description>append</description> <type>boolean</type> <value>true</value> </constructor-param> </constructor> <property> <name></name> <value></value> </property> </configured-object>
		 */
		if (!config.getTagName().equals("configured-object")) {
			throw new InstanceCreationException("invalid configuration. only <configured-object/> allowed.");
		}

		try {
			// ������ �Ķ���Ͱ� ������ ��
			Object object = null;
			String className = config.getString("class-name");
			// if (className.equals("null"))
			// return null;

			Class<?> objectClass = Class.forName(className);

			List<XMLConfig> constructorParams = config.children("constructor.constructor-param");
			if (constructorParams == null || constructorParams.size() == 0)
				object = objectClass.newInstance();
			else {

				Class<?>[] paramClasses = new Class[constructorParams.size()];
				Object[] paramObjects = new Object[constructorParams.size()];

				int index = 0;
				for (XMLConfig conParam : constructorParams) {
					String paramType = conParam.getString("type");
					Class<?> paramClass = getClassInstance(paramType);
					Object paramObject = null;

					if (conParam.hasChild("value.configured-object")) {
						// value�� �ٽ� <configured-object/> �� ������ object �� ��� ��� ȣ��!!
						paramObject = ConfigCreator.createConfiguredInstance(conParam.firstChild("value.configured-object"));
					} else {
						if (conParam.hasChild("value.null")) {
							paramObject = null;
						} else {
							String paramValue = conParam.getString("value");
							paramObject = getObjectInstance(paramClass, paramValue);
						}
					}

					paramClasses[index] = paramClass;
					paramObjects[index] = paramObject;
					index++;
				}

				object = objectClass.getConstructor(paramClasses).newInstance(paramObjects);
			}

			// property

			List<XMLConfig> properties = config.children("property");

			// property �� set method �� tableȭ ��Ŵ
			BeanInfo beanInfo = Introspector.getBeanInfo(objectClass);
			PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
			HashMap<String, Method> methodMap = new HashMap<String, Method>();
			for (int i = 0; i < descriptors.length; ++i) {
				String propertyName = descriptors[i].getName(); // .toLowerCase(); ����!!!!!!
				Method method = descriptors[i].getWriteMethod();

				methodMap.put(propertyName, method);
			}

			for (XMLConfig property : properties) {
				String name = property.getString("[@name]"); // .toLowerCase(); ����!!!!!!!

				Method setter = (Method) methodMap.get(name);
				if (setter == null) {
					throw new InstanceCreationException("could not find setter method of property name:" + name);
				}

				if (setter.getParameterTypes().length != 1) {
					throw new InstanceCreationException("unknown parameter for property setter:propery name=" + name);
				}

				Object paramObject = null;
				if (property.hasChild("value.configured-object")) {
					paramObject = ConfigCreator.createConfiguredInstance(property.firstChild("value.configured-object"));
				} else {
					if (property.hasChild("value.null")) {
						paramObject = null;
					} else {
						String value = property.getString("[@value]");
						paramObject = getObjectInstance(setter.getParameterTypes()[0], value);
					}
				}
				
				setter.invoke(object, new Object[] { paramObject });
			}


			return object;

		} catch (ClassNotFoundException cnfe) {
			throw new InstanceCreationException("Class not found.", cnfe);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InstanceCreationException("Could not instantiate the object.", e);
			
		}
	}

	public static Class<?> getClassInstance(String className) throws ClassNotFoundException {
		return getClassInstance(className, ClassLoader.getSystemClassLoader());
	}

	private static HashMap<String, Class<?>> classCache = new HashMap<String, Class<?>>();

	public static Class<?> getClassInstance(String className, ClassLoader loader) throws ClassNotFoundException {
		return InstanceCreator.getClassInstance(className, loader) ;
	}

	public static Object getObjectInstance(Class<?> clazz, String value) throws InstanceCreationException {
		// value.... 
		// String repValue = StringUtil.replace(value, "${BASEPATH}", Aradon.DIR_LOCAL.get().getDir().getPath());
		return InstanceCreator.getObjectInstance(clazz, value) ;
	}
	
	public static Object getObjectInstance(String className, String value) throws ClassNotFoundException, InstanceCreationException {
		return InstanceCreator.getObjectInstance(className, value) ;
	}
	
	public static Object createBeanWithPropertyValues(Class<?> beanClass, String[] propertyNames, Object[] propertyValues) throws InstanceCreationException {
		return InstanceCreator.createBeanWithPropertyValues(beanClass, propertyNames, propertyValues) ;
	}

	public static Object setPropertyValues(Object beanObject, String[] propertyNames, Object[] propertyValues) throws InstanceCreationException {
		return InstanceCreator.setPropertyValues(beanObject, propertyNames, propertyValues) ;
	}
}
