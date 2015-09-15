package com.android.okhttp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class Headers {
    private final String[] namesAndValues;

    public static class Builder {
        private final List<String> namesAndValues;

        public Builder() {
            this.namesAndValues = new ArrayList(20);
        }

        public Builder addLine(String line) {
            int index = line.indexOf(":", 1);
            if (index != -1) {
                return addLenient(line.substring(0, index), line.substring(index + 1));
            }
            if (line.startsWith(":")) {
                return addLenient("", line.substring(1));
            }
            return addLenient("", line);
        }

        public Builder add(String fieldName, String value) {
            if (fieldName == null) {
                throw new IllegalArgumentException("fieldname == null");
            } else if (value == null) {
                throw new IllegalArgumentException("value == null");
            } else if (fieldName.length() != 0 && fieldName.indexOf(0) == -1 && value.indexOf(0) == -1) {
                return addLenient(fieldName, value);
            } else {
                throw new IllegalArgumentException("Unexpected header: " + fieldName + ": " + value);
            }
        }

        private Builder addLenient(String fieldName, String value) {
            this.namesAndValues.add(fieldName);
            this.namesAndValues.add(value.trim());
            return this;
        }

        public Builder removeAll(String fieldName) {
            for (int i = 0; i < this.namesAndValues.size(); i += 2) {
                if (fieldName.equalsIgnoreCase((String) this.namesAndValues.get(i))) {
                    this.namesAndValues.remove(i);
                    this.namesAndValues.remove(i);
                }
            }
            return this;
        }

        public Builder set(String fieldName, String value) {
            removeAll(fieldName);
            add(fieldName, value);
            return this;
        }

        public String get(String fieldName) {
            for (int i = this.namesAndValues.size() - 2; i >= 0; i -= 2) {
                if (fieldName.equalsIgnoreCase((String) this.namesAndValues.get(i))) {
                    return (String) this.namesAndValues.get(i + 1);
                }
            }
            return null;
        }

        public Headers build() {
            return new Headers();
        }
    }

    private Headers(Builder builder) {
        this.namesAndValues = (String[]) builder.namesAndValues.toArray(new String[builder.namesAndValues.size()]);
    }

    public String get(String fieldName) {
        return get(this.namesAndValues, fieldName);
    }

    public int size() {
        return this.namesAndValues.length / 2;
    }

    public String name(int index) {
        int fieldNameIndex = index * 2;
        if (fieldNameIndex < 0 || fieldNameIndex >= this.namesAndValues.length) {
            return null;
        }
        return this.namesAndValues[fieldNameIndex];
    }

    public String value(int index) {
        int valueIndex = (index * 2) + 1;
        if (valueIndex < 0 || valueIndex >= this.namesAndValues.length) {
            return null;
        }
        return this.namesAndValues[valueIndex];
    }

    public Set<String> names() {
        TreeSet<String> result = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < size(); i++) {
            result.add(name(i));
        }
        return Collections.unmodifiableSet(result);
    }

    public List<String> values(String name) {
        List<String> result = null;
        for (int i = 0; i < size(); i++) {
            if (name.equalsIgnoreCase(name(i))) {
                if (result == null) {
                    result = new ArrayList(2);
                }
                result.add(value(i));
            }
        }
        return result != null ? Collections.unmodifiableList(result) : Collections.emptyList();
    }

    public Headers getAll(Set<String> fieldNames) {
        Builder result = new Builder();
        for (int i = 0; i < this.namesAndValues.length; i += 2) {
            String fieldName = this.namesAndValues[i];
            if (fieldNames.contains(fieldName)) {
                result.add(fieldName, this.namesAndValues[i + 1]);
            }
        }
        return result.build();
    }

    public Builder newBuilder() {
        Builder result = new Builder();
        result.namesAndValues.addAll(Arrays.asList(this.namesAndValues));
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            result.append(name(i)).append(": ").append(value(i)).append("\n");
        }
        return result.toString();
    }

    private static String get(String[] namesAndValues, String fieldName) {
        for (int i = namesAndValues.length - 2; i >= 0; i -= 2) {
            if (fieldName.equalsIgnoreCase(namesAndValues[i])) {
                return namesAndValues[i + 1];
            }
        }
        return null;
    }
}
