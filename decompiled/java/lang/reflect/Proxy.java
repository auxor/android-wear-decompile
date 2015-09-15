package java.lang.reflect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParser;

public class Proxy implements Serializable {
    private static final Comparator<Method> ORDER_BY_SIGNATURE_AND_SUBTYPE;
    private static int nextClassNameIndex = 0;
    private static final long serialVersionUID = -2222568056686623797L;
    protected InvocationHandler h;

    private static native void constructorPrototype(InvocationHandler invocationHandler);

    private static native Class<?> generateProxy(String str, Class<?>[] clsArr, ClassLoader classLoader, ArtMethod[] artMethodArr, Class<?>[][] clsArr2);

    static {
        nextClassNameIndex = 0;
        ORDER_BY_SIGNATURE_AND_SUBTYPE = new Comparator<Method>() {
            public int compare(Method a, Method b) {
                int comparison = Method.ORDER_BY_SIGNATURE.compare(a, b);
                if (comparison != 0) {
                    return comparison;
                }
                Class<?> aClass = a.getDeclaringClass();
                Class<?> bClass = b.getDeclaringClass();
                if (aClass == bClass) {
                    return 0;
                }
                if (aClass.isAssignableFrom(bClass)) {
                    return 1;
                }
                return bClass.isAssignableFrom(aClass) ? -1 : 0;
            }
        };
    }

    private Proxy() {
    }

    protected Proxy(InvocationHandler h) {
        this.h = h;
    }

    public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces) throws IllegalArgumentException {
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        if (interfaces == null) {
            throw new NullPointerException("interfaces == null");
        }
        Object interfaceList = new ArrayList(interfaces.length);
        Collections.addAll(interfaceList, interfaces);
        Set<Class<?>> interfaceSet = new HashSet((Collection) interfaceList);
        if (interfaceSet.contains(null)) {
            throw new NullPointerException("interface list contains null: " + interfaceList);
        }
        if (interfaceSet.size() != interfaces.length) {
            throw new IllegalArgumentException("duplicate interface in list: " + interfaceList);
        }
        synchronized (loader.proxyCache) {
            Class<?> proxy = (Class) loader.proxyCache.get(interfaceList);
            if (proxy != null) {
                return proxy;
            }
            String baseName;
            Class<?> result;
            String commonPackageName = null;
            Class<?>[] arr$ = interfaces;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                Object c = arr$[i$];
                if (!c.isInterface()) {
                    throw new IllegalArgumentException(c + " is not an interface");
                } else if (isVisibleToClassLoader(loader, c)) {
                    if (!Modifier.isPublic(c.getModifiers())) {
                        String packageName = c.getPackageName$();
                        if (packageName == null) {
                            packageName = XmlPullParser.NO_NAMESPACE;
                        }
                        if (commonPackageName == null || commonPackageName.equals(packageName)) {
                            commonPackageName = packageName;
                        } else {
                            throw new IllegalArgumentException("non-public interfaces must be in the same package");
                        }
                    }
                    i$++;
                } else {
                    throw new IllegalArgumentException(c + " is not visible from class loader");
                }
            }
            List<Method> methods = getMethods(interfaces);
            Collections.sort(methods, ORDER_BY_SIGNATURE_AND_SUBTYPE);
            validateReturnTypes(methods);
            List<Class<?>[]> exceptions = deduplicateAndGetExceptions(methods);
            ArtMethod[] methodsArray = new ArtMethod[methods.size()];
            int i = 0;
            while (true) {
                int length = methodsArray.length;
                if (i >= r0) {
                    break;
                }
                methodsArray[i] = ((Method) methods.get(i)).getArtMethod();
                i++;
            }
            Class[][] exceptionsArray = (Class[][]) exceptions.toArray(new Class[exceptions.size()][]);
            if (commonPackageName == null || commonPackageName.isEmpty()) {
                baseName = "$Proxy";
            } else {
                baseName = commonPackageName + ".$Proxy";
            }
            synchronized (loader.proxyCache) {
                result = (Class) loader.proxyCache.get(interfaceSet);
                if (result == null) {
                    StringBuilder append = new StringBuilder().append(baseName);
                    int i2 = nextClassNameIndex;
                    nextClassNameIndex = i2 + 1;
                    result = generateProxy(append.append(i2).toString(), interfaces, loader, methodsArray, exceptionsArray);
                    loader.proxyCache.put(interfaceList, result);
                }
            }
            return result;
        }
    }

    private static boolean isVisibleToClassLoader(ClassLoader loader, Class<?> c) {
        try {
            return loader == c.getClassLoader() || c == Class.forName(c.getName(), false, loader);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler invocationHandler) throws IllegalArgumentException {
        Exception cause;
        AssertionError error;
        if (invocationHandler == null) {
            throw new NullPointerException("invocationHandler == null");
        }
        try {
            return getProxyClass(loader, interfaces).getConstructor(InvocationHandler.class).newInstance(invocationHandler);
        } catch (Exception e) {
            cause = e;
            error = new AssertionError();
            error.initCause(cause);
            throw error;
        } catch (Exception e2) {
            cause = e2;
            error = new AssertionError();
            error.initCause(cause);
            throw error;
        } catch (Exception e22) {
            cause = e22;
            error = new AssertionError();
            error.initCause(cause);
            throw error;
        } catch (Exception e222) {
            cause = e222;
            error = new AssertionError();
            error.initCause(cause);
            throw error;
        }
    }

    public static boolean isProxyClass(Class<?> cl) {
        return cl.isProxy();
    }

    public static InvocationHandler getInvocationHandler(Object proxy) throws IllegalArgumentException {
        if (proxy instanceof Proxy) {
            return ((Proxy) proxy).h;
        }
        throw new IllegalArgumentException("not a proxy instance");
    }

    private static List<Method> getMethods(Class<?>[] interfaces) {
        List<Method> result = new ArrayList();
        try {
            result.add(Object.class.getMethod("equals", Object.class));
            result.add(Object.class.getMethod("hashCode", EmptyArray.CLASS));
            result.add(Object.class.getMethod("toString", EmptyArray.CLASS));
            getMethodsRecursive(interfaces, result);
            return result;
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        }
    }

    private static void getMethodsRecursive(Class<?>[] interfaces, List<Method> methods) {
        for (Class<?> i : interfaces) {
            getMethodsRecursive(i.getInterfaces(), methods);
            Collections.addAll(methods, i.getDeclaredMethods());
        }
    }

    private static void validateReturnTypes(List<Method> methods) {
        Method vs = null;
        for (Object method : methods) {
            if (vs == null || !vs.equalNameAndParameters(method)) {
                vs = method;
            } else {
                Class<?> returnType = method.getReturnType();
                Class<?> vsReturnType = vs.getReturnType();
                if (!returnType.isInterface() || !vsReturnType.isInterface()) {
                    if (vsReturnType.isAssignableFrom(returnType)) {
                        vs = method;
                    } else if (!returnType.isAssignableFrom(vsReturnType)) {
                        throw new IllegalArgumentException("proxied interface methods have incompatible return types:\n  " + vs + "\n  " + method);
                    }
                }
            }
        }
    }

    private static List<Class<?>[]> deduplicateAndGetExceptions(List<Method> methods) {
        List<Class<?>[]> exceptions = new ArrayList(methods.size());
        int i = 0;
        while (i < methods.size()) {
            Method method = (Method) methods.get(i);
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            if (i <= 0 || Method.ORDER_BY_SIGNATURE.compare(method, methods.get(i - 1)) != 0) {
                exceptions.add(exceptionTypes);
                i++;
            } else {
                exceptions.set(i - 1, intersectExceptions((Class[]) exceptions.get(i - 1), exceptionTypes));
                methods.remove(i);
            }
        }
        return exceptions;
    }

    private static Class<?>[] intersectExceptions(Class<?>[] aExceptions, Class<?>[] bExceptions) {
        if (aExceptions.length == 0 || bExceptions.length == 0) {
            return EmptyArray.CLASS;
        }
        if (Arrays.equals((Object[]) aExceptions, (Object[]) bExceptions)) {
            return aExceptions;
        }
        Set<Class<?>> intersection = new HashSet();
        for (Class<?> a : aExceptions) {
            for (Class<?> b : bExceptions) {
                if (a.isAssignableFrom(b)) {
                    intersection.add(b);
                } else if (b.isAssignableFrom(a)) {
                    intersection.add(a);
                }
            }
        }
        return (Class[]) intersection.toArray(new Class[intersection.size()]);
    }

    static Object invoke(Proxy proxy, ArtMethod method, Object[] args) throws Throwable {
        return proxy.h.invoke(proxy, new Method(method), args);
    }
}
