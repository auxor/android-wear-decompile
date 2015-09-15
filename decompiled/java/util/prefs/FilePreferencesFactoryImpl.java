package java.util.prefs;

class FilePreferencesFactoryImpl implements PreferencesFactory {
    private static final Preferences SYSTEM_ROOT;
    private static final Preferences USER_ROOT;

    static {
        USER_ROOT = new FilePreferencesImpl(System.getProperty("user.home") + "/.java/.userPrefs", true);
        SYSTEM_ROOT = new FilePreferencesImpl(System.getProperty("java.home") + "/.systemPrefs", false);
    }

    public Preferences userRoot() {
        return USER_ROOT;
    }

    public Preferences systemRoot() {
        return SYSTEM_ROOT;
    }
}
