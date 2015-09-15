package java.util.jar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes.Name;

class ManifestReader {
    private final HashMap<String, Name> attributeNameCache;
    private final byte[] buf;
    private int consecutiveLineBreaks;
    private final int endOfMainSection;
    private Name name;
    private int pos;
    private String value;
    private final ByteArrayOutputStream valueBuffer;

    public ManifestReader(byte[] buf, Attributes main) throws IOException {
        this.attributeNameCache = new HashMap();
        this.valueBuffer = new ByteArrayOutputStream(80);
        this.consecutiveLineBreaks = 0;
        this.buf = buf;
        while (readHeader()) {
            main.put(this.name, this.value);
        }
        this.endOfMainSection = this.pos;
    }

    public void readEntries(Map<String, Attributes> entries, Map<String, Chunk> chunks) throws IOException {
        int mark = this.pos;
        while (readHeader()) {
            if (Name.NAME.equals(this.name)) {
                String entryNameValue = this.value;
                Attributes entry = (Attributes) entries.get(entryNameValue);
                if (entry == null) {
                    entry = new Attributes(12);
                }
                while (readHeader()) {
                    entry.put(this.name, this.value);
                }
                if (chunks != null) {
                    if (chunks.get(entryNameValue) != null) {
                        throw new IOException("A jar verifier does not support more than one entry with the same name");
                    }
                    chunks.put(entryNameValue, new Chunk(mark, this.pos));
                    mark = this.pos;
                }
                entries.put(entryNameValue, entry);
            } else {
                throw new IOException("Entry is not named");
            }
        }
    }

    public int getEndOfMainSection() {
        return this.endOfMainSection;
    }

    private boolean readHeader() throws IOException {
        boolean z = true;
        if (this.consecutiveLineBreaks > 1) {
            this.consecutiveLineBreaks = 0;
            return false;
        }
        readName();
        this.consecutiveLineBreaks = 0;
        readValue();
        if (this.consecutiveLineBreaks <= 0) {
            z = false;
        }
        return z;
    }

    private void readName() throws IOException {
        int mark = this.pos;
        while (this.pos < this.buf.length) {
            byte[] bArr = this.buf;
            int i = this.pos;
            this.pos = i + 1;
            if (bArr[i] == 58) {
                String nameString = new String(this.buf, mark, (this.pos - mark) - 1, StandardCharsets.US_ASCII);
                bArr = this.buf;
                i = this.pos;
                this.pos = i + 1;
                if (bArr[i] != 32) {
                    throw new IOException(String.format("Invalid value for attribute '%s'", nameString));
                }
                try {
                    this.name = (Name) this.attributeNameCache.get(nameString);
                    if (this.name == null) {
                        this.name = new Name(nameString);
                        this.attributeNameCache.put(nameString, this.name);
                        return;
                    }
                    return;
                } catch (IllegalArgumentException e) {
                    throw new IOException(e.getMessage());
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readValue() throws java.io.IOException {
        /*
        r8 = this;
        r7 = 1;
        r1 = 0;
        r2 = r8.pos;
        r0 = r8.pos;
        r4 = r8.valueBuffer;
        r4.reset();
    L_0x000b:
        r4 = r8.pos;
        r5 = r8.buf;
        r5 = r5.length;
        if (r4 >= r5) goto L_0x0029;
    L_0x0012:
        r4 = r8.buf;
        r5 = r8.pos;
        r6 = r5 + 1;
        r8.pos = r6;
        r3 = r4[r5];
        switch(r3) {
            case 0: goto L_0x0041;
            case 10: goto L_0x0049;
            case 13: goto L_0x0054;
            case 32: goto L_0x005c;
            default: goto L_0x001f;
        };
    L_0x001f:
        r4 = r8.consecutiveLineBreaks;
        if (r4 < r7) goto L_0x006f;
    L_0x0023:
        r4 = r8.pos;
        r4 = r4 + -1;
        r8.pos = r4;
    L_0x0029:
        r4 = r8.valueBuffer;
        r5 = r8.buf;
        r6 = r0 - r2;
        r4.write(r5, r2, r6);
        r4 = r8.valueBuffer;
        r5 = java.nio.charset.StandardCharsets.UTF_8;
        r5 = r5.name();
        r4 = r4.toString(r5);
        r8.value = r4;
        return;
    L_0x0041:
        r4 = new java.io.IOException;
        r5 = "NUL character in a manifest";
        r4.<init>(r5);
        throw r4;
    L_0x0049:
        if (r1 == 0) goto L_0x004d;
    L_0x004b:
        r1 = 0;
        goto L_0x000b;
    L_0x004d:
        r4 = r8.consecutiveLineBreaks;
        r4 = r4 + 1;
        r8.consecutiveLineBreaks = r4;
        goto L_0x000b;
    L_0x0054:
        r1 = 1;
        r4 = r8.consecutiveLineBreaks;
        r4 = r4 + 1;
        r8.consecutiveLineBreaks = r4;
        goto L_0x000b;
    L_0x005c:
        r4 = r8.consecutiveLineBreaks;
        if (r4 != r7) goto L_0x001f;
    L_0x0060:
        r4 = r8.valueBuffer;
        r5 = r8.buf;
        r6 = r0 - r2;
        r4.write(r5, r2, r6);
        r2 = r8.pos;
        r4 = 0;
        r8.consecutiveLineBreaks = r4;
        goto L_0x000b;
    L_0x006f:
        r0 = r8.pos;
        goto L_0x000b;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.jar.ManifestReader.readValue():void");
    }
}
