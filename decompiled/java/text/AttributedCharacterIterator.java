package java.text;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

public interface AttributedCharacterIterator extends CharacterIterator {

    public static class Attribute implements Serializable {
        public static final Attribute INPUT_METHOD_SEGMENT;
        public static final Attribute LANGUAGE;
        public static final Attribute READING;
        private static final long serialVersionUID = -9142742483513960612L;
        private String name;

        static {
            INPUT_METHOD_SEGMENT = new Attribute("input_method_segment");
            LANGUAGE = new Attribute("language");
            READING = new Attribute("reading");
        }

        protected Attribute(String name) {
            this.name = name;
        }

        public final boolean equals(Object object) {
            return this == object;
        }

        protected String getName() {
            return this.name;
        }

        public final int hashCode() {
            return super.hashCode();
        }

        protected Object readResolve() throws InvalidObjectException {
            try {
                for (Field field : getClass().getFields()) {
                    if (field.getType() == getClass() && Modifier.isStatic(field.getModifiers())) {
                        Attribute candidate = (Attribute) field.get(null);
                        if (this.name.equals(candidate.name)) {
                            return candidate;
                        }
                    }
                }
            } catch (IllegalAccessException e) {
            }
            throw new InvalidObjectException("Failed to resolve " + this);
        }

        public String toString() {
            return getClass().getName() + '(' + getName() + ')';
        }
    }

    Set<Attribute> getAllAttributeKeys();

    Object getAttribute(Attribute attribute);

    Map<Attribute, Object> getAttributes();

    int getRunLimit();

    int getRunLimit(Attribute attribute);

    int getRunLimit(Set<? extends Attribute> set);

    int getRunStart();

    int getRunStart(Attribute attribute);

    int getRunStart(Set<? extends Attribute> set);
}
