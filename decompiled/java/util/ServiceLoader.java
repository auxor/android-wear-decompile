package java.util;

import java.io.IOException;
import java.net.URL;

public final class ServiceLoader<S> implements Iterable<S> {
    private final ClassLoader classLoader;
    private final Class<S> service;
    private final Set<URL> services;

    private class ServiceIterator implements Iterator<S> {
        private final ClassLoader classLoader;
        private boolean isRead;
        private LinkedList<String> queue;
        private final Class<S> service;
        private final Set<URL> services;

        public ServiceIterator(ServiceLoader<S> sl) {
            this.isRead = false;
            this.queue = new LinkedList();
            this.classLoader = sl.classLoader;
            this.service = sl.service;
            this.services = sl.services;
        }

        public boolean hasNext() {
            if (!this.isRead) {
                readClass();
            }
            return (this.queue == null || this.queue.isEmpty()) ? false : true;
        }

        public S next() {
            if (hasNext()) {
                String className = (String) this.queue.remove();
                try {
                    return this.service.cast(this.classLoader.loadClass(className).newInstance());
                } catch (Exception e) {
                    throw new ServiceConfigurationError("Couldn't instantiate class " + className, e);
                }
            }
            throw new NoSuchElementException();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void readClass() {
            /*
            r11 = this;
            r8 = r11.services;
            r3 = r8.iterator();
        L_0x0006:
            r8 = r3.hasNext();
            if (r8 == 0) goto L_0x007a;
        L_0x000c:
            r7 = r3.next();
            r7 = (java.net.URL) r7;
            r5 = 0;
            r6 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x007e }
            r8 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x007e }
            r9 = r7.openStream();	 Catch:{ Exception -> 0x007e }
            r10 = "UTF-8";
            r8.<init>(r9, r10);	 Catch:{ Exception -> 0x007e }
            r6.<init>(r8);	 Catch:{ Exception -> 0x007e }
        L_0x0023:
            r4 = r6.readLine();	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            if (r4 == 0) goto L_0x0073;
        L_0x0029:
            r8 = 35;
            r1 = r4.indexOf(r8);	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            r8 = -1;
            if (r1 == r8) goto L_0x0037;
        L_0x0032:
            r8 = 0;
            r4 = r4.substring(r8, r1);	 Catch:{ Exception -> 0x0053, all -> 0x007b }
        L_0x0037:
            r4 = r4.trim();	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            r8 = r4.isEmpty();	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            if (r8 != 0) goto L_0x0023;
        L_0x0041:
            r0 = r4;
            r11.checkValidJavaClassName(r0);	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            r8 = r11.queue;	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            r8 = r8.contains(r0);	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            if (r8 != 0) goto L_0x0023;
        L_0x004d:
            r8 = r11.queue;	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            r8.add(r0);	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            goto L_0x0023;
        L_0x0053:
            r2 = move-exception;
            r5 = r6;
        L_0x0055:
            r8 = new java.util.ServiceConfigurationError;	 Catch:{ all -> 0x006e }
            r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x006e }
            r9.<init>();	 Catch:{ all -> 0x006e }
            r10 = "Couldn't read ";
            r9 = r9.append(r10);	 Catch:{ all -> 0x006e }
            r9 = r9.append(r7);	 Catch:{ all -> 0x006e }
            r9 = r9.toString();	 Catch:{ all -> 0x006e }
            r8.<init>(r9, r2);	 Catch:{ all -> 0x006e }
            throw r8;	 Catch:{ all -> 0x006e }
        L_0x006e:
            r8 = move-exception;
        L_0x006f:
            libcore.io.IoUtils.closeQuietly(r5);
            throw r8;
        L_0x0073:
            r8 = 1;
            r11.isRead = r8;	 Catch:{ Exception -> 0x0053, all -> 0x007b }
            libcore.io.IoUtils.closeQuietly(r6);
            goto L_0x0006;
        L_0x007a:
            return;
        L_0x007b:
            r8 = move-exception;
            r5 = r6;
            goto L_0x006f;
        L_0x007e:
            r2 = move-exception;
            goto L_0x0055;
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.ServiceLoader.ServiceIterator.readClass():void");
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void checkValidJavaClassName(String className) {
            int i = 0;
            while (i < className.length()) {
                char ch = className.charAt(i);
                if (Character.isJavaIdentifierPart(ch) || ch == '.') {
                    i++;
                } else {
                    throw new ServiceConfigurationError("Bad character '" + ch + "' in class name");
                }
            }
        }
    }

    private ServiceLoader(Class<S> service, ClassLoader classLoader) {
        if (service == null) {
            throw new NullPointerException("service == null");
        }
        this.service = service;
        this.classLoader = classLoader;
        this.services = new HashSet();
        reload();
    }

    public void reload() {
        internalLoad();
    }

    public Iterator<S> iterator() {
        return new ServiceIterator(this);
    }

    public static <S> ServiceLoader<S> load(Class<S> service, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return new ServiceLoader(service, classLoader);
    }

    private void internalLoad() {
        this.services.clear();
        try {
            this.services.addAll(Collections.list(this.classLoader.getResources("META-INF/services/" + this.service.getName())));
        } catch (IOException e) {
        }
    }

    public static <S> ServiceLoader<S> load(Class<S> service) {
        return load(service, Thread.currentThread().getContextClassLoader());
    }

    public static <S> ServiceLoader<S> loadInstalled(Class<S> service) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if (cl != null) {
            while (cl.getParent() != null) {
                cl = cl.getParent();
            }
        }
        return load(service, cl);
    }

    public static <S> S loadFromSystemProperty(Class<S> service) {
        try {
            String className = System.getProperty(service.getName());
            if (className != null) {
                return ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
            }
            return null;
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    public String toString() {
        return "ServiceLoader for " + this.service.getName();
    }
}
