package java.lang;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class VMClassLoader {
    static native Class findLoadedClass(ClassLoader classLoader, String str);

    private static native String getBootClassPathResource(String str, int i);

    private static native int getBootClassPathSize();

    VMClassLoader() {
    }

    static URL getResource(String name) {
        int numEntries = getBootClassPathSize();
        int i = 0;
        while (i < numEntries) {
            String urlStr = getBootClassPathResource(name, i);
            if (urlStr != null) {
                try {
                    return new URL(urlStr);
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
            } else {
                i++;
            }
        }
        return null;
    }

    static List<URL> getResources(String name) {
        ArrayList<URL> list = new ArrayList();
        int numEntries = getBootClassPathSize();
        for (int i = 0; i < numEntries; i++) {
            String urlStr = getBootClassPathResource(name, i);
            if (urlStr != null) {
                try {
                    list.add(new URL(urlStr));
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
            }
        }
        return list;
    }
}
