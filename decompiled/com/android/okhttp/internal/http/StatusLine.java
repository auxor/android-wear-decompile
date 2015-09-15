package com.android.okhttp.internal.http;

import java.io.IOException;
import java.net.ProtocolException;

public final class StatusLine {
    public static final int HTTP_CONTINUE = 100;
    public static final int HTTP_TEMP_REDIRECT = 307;
    private final int httpMinorVersion;
    private final int responseCode;
    private final String responseMessage;
    private final String statusLine;

    public StatusLine(String statusLine) throws IOException {
        int httpMinorVersion;
        int codeStart;
        if (statusLine.startsWith("HTTP/1.")) {
            if (statusLine.length() < 9 || statusLine.charAt(8) != ' ') {
                throw new ProtocolException("Unexpected status line: " + statusLine);
            }
            httpMinorVersion = statusLine.charAt(7) - 48;
            codeStart = 9;
            if (httpMinorVersion < 0 || httpMinorVersion > 9) {
                throw new ProtocolException("Unexpected status line: " + statusLine);
            }
        } else if (statusLine.startsWith("ICY ")) {
            httpMinorVersion = 0;
            codeStart = 4;
        } else {
            throw new ProtocolException("Unexpected status line: " + statusLine);
        }
        if (statusLine.length() < codeStart + 3) {
            throw new ProtocolException("Unexpected status line: " + statusLine);
        }
        try {
            int responseCode = Integer.parseInt(statusLine.substring(codeStart, codeStart + 3));
            String responseMessage = "";
            if (statusLine.length() > codeStart + 3) {
                if (statusLine.charAt(codeStart + 3) != ' ') {
                    throw new ProtocolException("Unexpected status line: " + statusLine);
                }
                responseMessage = statusLine.substring(codeStart + 4);
            }
            this.responseMessage = responseMessage;
            this.responseCode = responseCode;
            this.statusLine = statusLine;
            this.httpMinorVersion = httpMinorVersion;
        } catch (NumberFormatException e) {
            throw new ProtocolException("Unexpected status line: " + statusLine);
        }
    }

    public String getStatusLine() {
        return this.statusLine;
    }

    public int httpMinorVersion() {
        return this.httpMinorVersion != -1 ? this.httpMinorVersion : 1;
    }

    public int code() {
        return this.responseCode;
    }

    public String message() {
        return this.responseMessage;
    }
}
