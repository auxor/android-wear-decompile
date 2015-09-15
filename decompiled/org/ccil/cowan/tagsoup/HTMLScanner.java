package org.ccil.cowan.tagsoup;

import gov.nist.core.Separators;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class HTMLScanner implements Scanner, Locator {
    private static final int A_ADUP = 1;
    private static final int A_ADUP_SAVE = 2;
    private static final int A_ADUP_STAGC = 3;
    private static final int A_ANAME = 4;
    private static final int A_ANAME_ADUP = 5;
    private static final int A_ANAME_ADUP_STAGC = 6;
    private static final int A_AVAL = 7;
    private static final int A_AVAL_STAGC = 8;
    private static final int A_CDATA = 9;
    private static final int A_CMNT = 10;
    private static final int A_DECL = 11;
    private static final int A_EMPTYTAG = 12;
    private static final int A_ENTITY = 13;
    private static final int A_ENTITY_START = 14;
    private static final int A_ETAG = 15;
    private static final int A_GI = 16;
    private static final int A_GI_STAGC = 17;
    private static final int A_LT = 18;
    private static final int A_LT_PCDATA = 19;
    private static final int A_MINUS = 20;
    private static final int A_MINUS2 = 21;
    private static final int A_MINUS3 = 22;
    private static final int A_PCDATA = 23;
    private static final int A_PI = 24;
    private static final int A_PITARGET = 25;
    private static final int A_PITARGET_PI = 26;
    private static final int A_SAVE = 27;
    private static final int A_SKIP = 28;
    private static final int A_SP = 29;
    private static final int A_STAGC = 30;
    private static final int A_UNGET = 31;
    private static final int A_UNSAVE_PCDATA = 32;
    private static final int S_ANAME = 1;
    private static final int S_APOS = 2;
    private static final int S_AVAL = 3;
    private static final int S_BB = 4;
    private static final int S_BBC = 5;
    private static final int S_BBCD = 6;
    private static final int S_BBCDA = 7;
    private static final int S_BBCDAT = 8;
    private static final int S_BBCDATA = 9;
    private static final int S_CDATA = 10;
    private static final int S_CDATA2 = 11;
    private static final int S_CDSECT = 12;
    private static final int S_CDSECT1 = 13;
    private static final int S_CDSECT2 = 14;
    private static final int S_COM = 15;
    private static final int S_COM2 = 16;
    private static final int S_COM3 = 17;
    private static final int S_COM4 = 18;
    private static final int S_DECL = 19;
    private static final int S_DECL2 = 20;
    private static final int S_DONE = 21;
    private static final int S_EMPTYTAG = 22;
    private static final int S_ENT = 23;
    private static final int S_EQ = 24;
    private static final int S_ETAG = 25;
    private static final int S_GI = 26;
    private static final int S_NCR = 27;
    private static final int S_PCDATA = 28;
    private static final int S_PI = 29;
    private static final int S_PITARGET = 30;
    private static final int S_QUOT = 31;
    private static final int S_STAGC = 32;
    private static final int S_TAG = 33;
    private static final int S_TAGWS = 34;
    private static final int S_XNCR = 35;
    private static final String[] debug_actionnames;
    private static final String[] debug_statenames;
    private static int[] statetable;
    static short[][] statetableIndex;
    static int statetableIndexMaxChar;
    private int theCurrentColumn;
    private int theCurrentLine;
    private int theLastColumn;
    private int theLastLine;
    int theNextState;
    char[] theOutputBuffer;
    private String thePublicid;
    int theSize;
    int theState;
    private String theSystemid;
    int[] theWinMap;

    public HTMLScanner() {
        this.theOutputBuffer = new char[HttpStatus.SC_OK];
        this.theWinMap = new int[]{8364, 65533, 8218, HttpStatus.SC_PAYMENT_REQUIRED, 8222, 8230, 8224, 8225, 710, 8240, 352, 8249, 338, 65533, 381, 65533, 65533, 8216, 8217, 8220, 8221, 8226, 8211, 8212, 732, 8482, 353, 8250, 339, 65533, 382, 376};
    }

    static {
        int i;
        statetable = new int[]{S_ANAME, 47, S_BBC, S_EMPTYTAG, S_ANAME, 61, S_BB, S_AVAL, S_ANAME, 62, S_BBCD, S_PCDATA, S_ANAME, 0, S_NCR, S_ANAME, S_ANAME, -1, S_BBCD, S_DONE, S_ANAME, S_STAGC, S_BB, S_EQ, S_ANAME, S_CDATA, S_BB, S_EQ, S_ANAME, S_BBCDATA, S_BB, S_EQ, S_APOS, 39, S_BBCDA, S_TAGWS, S_APOS, 0, S_NCR, S_APOS, S_APOS, -1, S_BBCDAT, S_DONE, S_APOS, S_STAGC, S_PI, S_APOS, S_APOS, S_CDATA, S_PI, S_APOS, S_APOS, S_BBCDATA, S_PI, S_APOS, S_AVAL, S_TAGWS, S_PCDATA, S_QUOT, S_AVAL, 39, S_PCDATA, S_APOS, S_AVAL, 62, S_BBCDAT, S_PCDATA, S_AVAL, 0, S_NCR, S_STAGC, S_AVAL, -1, S_BBCDAT, S_DONE, S_AVAL, S_STAGC, S_PCDATA, S_AVAL, S_AVAL, S_CDATA, S_PCDATA, S_AVAL, S_AVAL, S_BBCDATA, S_PCDATA, S_AVAL, S_BB, 67, S_PCDATA, S_BBC, S_BB, 0, S_PCDATA, S_DECL, S_BB, -1, S_PCDATA, S_DONE, S_BBC, 68, S_PCDATA, S_BBCD, S_BBC, 0, S_PCDATA, S_DECL, S_BBC, -1, S_PCDATA, S_DONE, S_BBCD, 65, S_PCDATA, S_BBCDA, S_BBCD, 0, S_PCDATA, S_DECL, S_BBCD, -1, S_PCDATA, S_DONE, S_BBCDA, 84, S_PCDATA, S_BBCDAT, S_BBCDA, 0, S_PCDATA, S_DECL, S_BBCDA, -1, S_PCDATA, S_DONE, S_BBCDAT, 65, S_PCDATA, S_BBCDATA, S_BBCDAT, 0, S_PCDATA, S_DECL, S_BBCDAT, -1, S_PCDATA, S_DONE, S_BBCDATA, 91, S_PCDATA, S_CDSECT, S_BBCDATA, 0, S_PCDATA, S_DECL, S_BBCDATA, -1, S_PCDATA, S_DONE, S_CDATA, 60, S_NCR, S_CDATA2, S_CDATA, 0, S_NCR, S_CDATA, S_CDATA, -1, S_ENT, S_DONE, S_CDATA2, 47, S_STAGC, S_ETAG, S_CDATA2, 0, S_NCR, S_CDATA, S_CDATA2, -1, S_STAGC, S_DONE, S_CDSECT, 93, S_NCR, S_CDSECT1, S_CDSECT, 0, S_NCR, S_CDSECT, S_CDSECT, -1, S_PCDATA, S_DONE, S_CDSECT1, 93, S_NCR, S_CDSECT2, S_CDSECT1, 0, S_NCR, S_CDSECT, S_CDSECT1, -1, S_PCDATA, S_DONE, S_CDSECT2, 62, S_BBCDATA, S_PCDATA, S_CDSECT2, 0, S_NCR, S_CDSECT, S_CDSECT2, -1, S_PCDATA, S_DONE, S_COM, 45, S_PCDATA, S_COM2, S_COM, 0, S_NCR, S_COM2, S_COM, -1, S_CDATA, S_DONE, S_COM2, 45, S_PCDATA, S_COM3, S_COM2, 0, S_NCR, S_COM2, S_COM2, -1, S_CDATA, S_DONE, S_COM3, 45, S_PCDATA, S_COM4, S_COM3, 0, S_DECL2, S_COM2, S_COM3, -1, S_CDATA, S_DONE, S_COM4, 45, S_EMPTYTAG, S_COM4, S_COM4, 62, S_CDATA, S_PCDATA, S_COM4, 0, S_DONE, S_COM2, S_COM4, -1, S_CDATA, S_DONE, S_DECL, 45, S_PCDATA, S_COM, S_DECL, 62, S_PCDATA, S_PCDATA, S_DECL, 91, S_PCDATA, S_BB, S_DECL, 0, S_NCR, S_DECL2, S_DECL, -1, S_PCDATA, S_DONE, S_DECL2, 62, S_CDATA2, S_PCDATA, S_DECL2, 0, S_NCR, S_DECL2, S_DECL2, -1, S_PCDATA, S_DONE, S_EMPTYTAG, 62, S_CDSECT, S_PCDATA, S_EMPTYTAG, 0, S_NCR, S_ANAME, S_EMPTYTAG, S_STAGC, S_PCDATA, S_TAGWS, S_EMPTYTAG, S_CDATA, S_PCDATA, S_TAGWS, S_EMPTYTAG, S_BBCDATA, S_PCDATA, S_TAGWS, S_ENT, 0, S_CDSECT1, S_ENT, S_ENT, -1, S_CDSECT1, S_DONE, S_EQ, 61, S_PCDATA, S_AVAL, S_EQ, 62, S_AVAL, S_PCDATA, S_EQ, 0, S_APOS, S_ANAME, S_EQ, -1, S_AVAL, S_DONE, S_EQ, S_STAGC, S_PCDATA, S_EQ, S_EQ, S_CDATA, S_PCDATA, S_EQ, S_EQ, S_BBCDATA, S_PCDATA, S_EQ, S_ETAG, 62, S_COM, S_PCDATA, S_ETAG, 0, S_NCR, S_ETAG, S_ETAG, -1, S_COM, S_DONE, S_ETAG, S_STAGC, S_PCDATA, S_ETAG, S_ETAG, S_CDATA, S_PCDATA, S_ETAG, S_ETAG, S_BBCDATA, S_PCDATA, S_ETAG, S_GI, 47, S_PCDATA, S_EMPTYTAG, S_GI, 62, S_COM3, S_PCDATA, S_GI, 0, S_NCR, S_GI, S_GI, -1, S_PCDATA, S_DONE, S_GI, S_STAGC, S_COM2, S_TAGWS, S_GI, S_CDATA, S_COM2, S_TAGWS, S_GI, S_BBCDATA, S_COM2, S_TAGWS, S_NCR, 0, S_CDSECT1, S_NCR, S_NCR, -1, S_CDSECT1, S_DONE, S_PCDATA, 38, S_CDSECT2, S_ENT, S_PCDATA, 60, S_ENT, S_TAG, S_PCDATA, 0, S_NCR, S_PCDATA, S_PCDATA, -1, S_ENT, S_DONE, S_PI, 62, S_EQ, S_PCDATA, S_PI, 0, S_NCR, S_PI, S_PI, -1, S_EQ, S_DONE, S_PITARGET, 62, S_GI, S_PCDATA, S_PITARGET, 0, S_NCR, S_PITARGET, S_PITARGET, -1, S_GI, S_DONE, S_PITARGET, S_STAGC, S_ETAG, S_PI, S_PITARGET, S_CDATA, S_ETAG, S_PI, S_PITARGET, S_BBCDATA, S_ETAG, S_PI, S_QUOT, S_TAGWS, S_BBCDA, S_TAGWS, S_QUOT, 0, S_NCR, S_QUOT, S_QUOT, -1, S_BBCDAT, S_DONE, S_QUOT, S_STAGC, S_PI, S_QUOT, S_QUOT, S_CDATA, S_PI, S_QUOT, S_QUOT, S_BBCDATA, S_PI, S_QUOT, S_STAGC, 62, S_BBCDAT, S_PCDATA, S_STAGC, 0, S_NCR, S_STAGC, S_STAGC, -1, S_BBCDAT, S_DONE, S_STAGC, S_STAGC, S_BBCDA, S_TAGWS, S_STAGC, S_CDATA, S_BBCDA, S_TAGWS, S_STAGC, S_BBCDATA, S_BBCDA, S_TAGWS, S_TAG, S_TAG, S_PCDATA, S_DECL, S_TAG, 47, S_PCDATA, S_ETAG, S_TAG, 60, S_NCR, S_TAG, S_TAG, 63, S_PCDATA, S_PITARGET, S_TAG, 0, S_NCR, S_GI, S_TAG, -1, S_DECL, S_DONE, S_TAG, S_STAGC, S_COM4, S_PCDATA, S_TAG, S_CDATA, S_COM4, S_PCDATA, S_TAG, S_BBCDATA, S_COM4, S_PCDATA, S_TAGWS, 47, S_PCDATA, S_EMPTYTAG, S_TAGWS, 62, S_PITARGET, S_PCDATA, S_TAGWS, 0, S_NCR, S_ANAME, S_TAGWS, -1, S_PITARGET, S_DONE, S_TAGWS, S_STAGC, S_PCDATA, S_TAGWS, S_TAGWS, S_CDATA, S_PCDATA, S_TAGWS, S_TAGWS, S_BBCDATA, S_PCDATA, S_TAGWS, S_XNCR, 0, S_CDSECT1, S_XNCR, S_XNCR, -1, S_CDSECT1, S_DONE};
        String[] strArr = new String[S_TAG];
        strArr[0] = "";
        strArr[S_ANAME] = "A_ADUP";
        strArr[S_APOS] = "A_ADUP_SAVE";
        strArr[S_AVAL] = "A_ADUP_STAGC";
        strArr[S_BB] = "A_ANAME";
        strArr[S_BBC] = "A_ANAME_ADUP";
        strArr[S_BBCD] = "A_ANAME_ADUP_STAGC";
        strArr[S_BBCDA] = "A_AVAL";
        strArr[S_BBCDAT] = "A_AVAL_STAGC";
        strArr[S_BBCDATA] = "A_CDATA";
        strArr[S_CDATA] = "A_CMNT";
        strArr[S_CDATA2] = "A_DECL";
        strArr[S_CDSECT] = "A_EMPTYTAG";
        strArr[S_CDSECT1] = "A_ENTITY";
        strArr[S_CDSECT2] = "A_ENTITY_START";
        strArr[S_COM] = "A_ETAG";
        strArr[S_COM2] = "A_GI";
        strArr[S_COM3] = "A_GI_STAGC";
        strArr[S_COM4] = "A_LT";
        strArr[S_DECL] = "A_LT_PCDATA";
        strArr[S_DECL2] = "A_MINUS";
        strArr[S_DONE] = "A_MINUS2";
        strArr[S_EMPTYTAG] = "A_MINUS3";
        strArr[S_ENT] = "A_PCDATA";
        strArr[S_EQ] = "A_PI";
        strArr[S_ETAG] = "A_PITARGET";
        strArr[S_GI] = "A_PITARGET_PI";
        strArr[S_NCR] = "A_SAVE";
        strArr[S_PCDATA] = "A_SKIP";
        strArr[S_PI] = "A_SP";
        strArr[S_PITARGET] = "A_STAGC";
        strArr[S_QUOT] = "A_UNGET";
        strArr[S_STAGC] = "A_UNSAVE_PCDATA";
        debug_actionnames = strArr;
        debug_statenames = new String[]{"", "S_ANAME", "S_APOS", "S_AVAL", "S_BB", "S_BBC", "S_BBCD", "S_BBCDA", "S_BBCDAT", "S_BBCDATA", "S_CDATA", "S_CDATA2", "S_CDSECT", "S_CDSECT1", "S_CDSECT2", "S_COM", "S_COM2", "S_COM3", "S_COM4", "S_DECL", "S_DECL2", "S_DONE", "S_EMPTYTAG", "S_ENT", "S_EQ", "S_ETAG", "S_GI", "S_NCR", "S_PCDATA", "S_PI", "S_PITARGET", "S_QUOT", "S_STAGC", "S_TAG", "S_TAGWS", "S_XNCR"};
        int maxState = -1;
        int maxChar = -1;
        for (i = 0; i < statetable.length; i += S_BB) {
            if (statetable[i] > maxState) {
                maxState = statetable[i];
            }
            if (statetable[i + S_ANAME] > maxChar) {
                maxChar = statetable[i + S_ANAME];
            }
        }
        statetableIndexMaxChar = maxChar + S_ANAME;
        statetableIndex = (short[][]) Array.newInstance(Short.TYPE, new int[]{maxState + S_ANAME, maxChar + S_AVAL});
        for (int theState = 0; theState <= maxState; theState += S_ANAME) {
            for (int ch = -2; ch <= maxChar; ch += S_ANAME) {
                int hit = -1;
                int action = 0;
                for (i = 0; i < statetable.length; i += S_BB) {
                    if (theState != statetable[i]) {
                        if (action != 0) {
                            break;
                        }
                    } else if (statetable[i + S_ANAME] == 0) {
                        hit = i;
                        action = statetable[i + S_APOS];
                    } else if (statetable[i + S_ANAME] == ch) {
                        hit = i;
                        action = statetable[i + S_APOS];
                        break;
                    }
                }
                statetableIndex[theState][ch + S_APOS] = (short) hit;
            }
        }
    }

    private void unread(PushbackReader r, int c) throws IOException {
        if (c != -1) {
            r.unread(c);
        }
    }

    public int getLineNumber() {
        return this.theLastLine;
    }

    public int getColumnNumber() {
        return this.theLastColumn;
    }

    public String getPublicId() {
        return this.thePublicid;
    }

    public String getSystemId() {
        return this.theSystemid;
    }

    public void resetDocumentLocator(String publicid, String systemid) {
        this.thePublicid = publicid;
        this.theSystemid = systemid;
        this.theCurrentColumn = 0;
        this.theCurrentLine = 0;
        this.theLastColumn = 0;
        this.theLastLine = 0;
    }

    public void scan(Reader r0, ScanHandler h) throws IOException, SAXException {
        PushbackReader r;
        this.theState = S_PCDATA;
        if (r0 instanceof BufferedReader) {
            r = new PushbackReader(r0, S_BBC);
        } else {
            r = new PushbackReader(new BufferedReader(r0), S_BBC);
        }
        int firstChar = r.read();
        if (firstChar != 65279) {
            unread(r, firstChar);
        }
        while (this.theState != S_DONE) {
            int ch = r.read();
            if (ch >= HTMLModels.M_DEF && ch <= 159) {
                ch = this.theWinMap[ch - 128];
            }
            if (ch == S_CDSECT1) {
                ch = r.read();
                if (ch != S_CDATA) {
                    unread(r, ch);
                    ch = S_CDATA;
                }
            }
            if (ch == S_CDATA) {
                this.theCurrentLine += S_ANAME;
                this.theCurrentColumn = 0;
            } else {
                this.theCurrentColumn += S_ANAME;
            }
            if (ch >= S_STAGC || ch == S_CDATA || ch == S_BBCDATA || ch == -1) {
                int adjCh = (ch < -1 || ch >= statetableIndexMaxChar) ? -2 : ch;
                int statetableRow = statetableIndex[this.theState][adjCh + S_APOS];
                int action = 0;
                if (statetableRow != -1) {
                    action = statetable[statetableRow + S_APOS];
                    this.theNextState = statetable[statetableRow + S_AVAL];
                }
                switch (action) {
                    case Schema.M_EMPTY /*0*/:
                        throw new Error("HTMLScanner can't cope with " + Integer.toString(ch) + " in state " + Integer.toString(this.theState));
                    case S_ANAME /*1*/:
                        h.adup(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_APOS /*2*/:
                        h.adup(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        save(ch, h);
                        break;
                    case S_AVAL /*3*/:
                        h.adup(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        h.stagc(this.theOutputBuffer, 0, this.theSize);
                        break;
                    case S_BB /*4*/:
                        h.aname(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_BBC /*5*/:
                        h.aname(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        h.adup(this.theOutputBuffer, 0, this.theSize);
                        break;
                    case S_BBCD /*6*/:
                        h.aname(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        h.adup(this.theOutputBuffer, 0, this.theSize);
                        h.stagc(this.theOutputBuffer, 0, this.theSize);
                        break;
                    case S_BBCDA /*7*/:
                        h.aval(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_BBCDAT /*8*/:
                        h.aval(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        h.stagc(this.theOutputBuffer, 0, this.theSize);
                        break;
                    case S_BBCDATA /*9*/:
                        mark();
                        if (this.theSize > S_ANAME) {
                            this.theSize -= 2;
                        }
                        h.pcdata(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_CDATA /*10*/:
                        mark();
                        h.cmnt(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_CDATA2 /*11*/:
                        h.decl(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_CDSECT /*12*/:
                        mark();
                        if (this.theSize > 0) {
                            h.gi(this.theOutputBuffer, 0, this.theSize);
                        }
                        this.theSize = 0;
                        h.stage(this.theOutputBuffer, 0, this.theSize);
                        break;
                    case S_CDSECT1 /*13*/:
                        mark();
                        char ch1 = (char) ch;
                        if (this.theState != S_ENT || ch1 != '#') {
                            if (this.theState != S_NCR || (ch1 != 'x' && ch1 != 'X')) {
                                if (this.theState != S_ENT || !Character.isLetterOrDigit(ch1)) {
                                    if (this.theState != S_NCR || !Character.isDigit(ch1)) {
                                        if (this.theState == S_XNCR && (Character.isDigit(ch1) || "abcdefABCDEF".indexOf(ch1) != -1)) {
                                            save(ch, h);
                                            break;
                                        }
                                        h.entity(this.theOutputBuffer, S_ANAME, this.theSize - 1);
                                        int ent = h.getEntity();
                                        if (ent != 0) {
                                            this.theSize = 0;
                                            if (ent >= HTMLModels.M_DEF && ent <= 159) {
                                                ent = this.theWinMap[ent - 128];
                                            }
                                            if (ent >= S_STAGC) {
                                                if (ent < 55296 || ent > 57343) {
                                                    if (ent <= 65535) {
                                                        save(ent, h);
                                                    } else {
                                                        ent -= HTMLModels.M_OPTION;
                                                        save((ent >> S_CDATA) + 55296, h);
                                                        save((ent & 1023) + 56320, h);
                                                    }
                                                }
                                            }
                                            if (ch != 59) {
                                                unread(r, ch);
                                                this.theCurrentColumn--;
                                            }
                                        } else {
                                            unread(r, ch);
                                            this.theCurrentColumn--;
                                        }
                                        this.theNextState = S_PCDATA;
                                        break;
                                    }
                                    save(ch, h);
                                    break;
                                }
                                save(ch, h);
                                break;
                            }
                            this.theNextState = S_XNCR;
                            save(ch, h);
                            break;
                        }
                        this.theNextState = S_NCR;
                        save(ch, h);
                        break;
                        break;
                    case S_CDSECT2 /*14*/:
                        h.pcdata(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        save(ch, h);
                        break;
                    case S_COM /*15*/:
                        h.etag(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_COM2 /*16*/:
                        h.gi(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_COM3 /*17*/:
                        h.gi(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        h.stagc(this.theOutputBuffer, 0, this.theSize);
                        break;
                    case S_COM4 /*18*/:
                        mark();
                        save(60, h);
                        save(ch, h);
                        break;
                    case S_DECL /*19*/:
                        mark();
                        save(60, h);
                        h.pcdata(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_DECL2 /*20*/:
                        break;
                    case S_DONE /*21*/:
                        save(45, h);
                        save(S_STAGC, h);
                        break;
                    case S_EMPTYTAG /*22*/:
                        save(45, h);
                        save(S_STAGC, h);
                        break;
                    case S_ENT /*23*/:
                        mark();
                        h.pcdata(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_EQ /*24*/:
                        mark();
                        h.pi(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_ETAG /*25*/:
                        h.pitarget(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_GI /*26*/:
                        h.pitarget(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        h.pi(this.theOutputBuffer, 0, this.theSize);
                        break;
                    case S_NCR /*27*/:
                        save(ch, h);
                        break;
                    case S_PCDATA /*28*/:
                        break;
                    case S_PI /*29*/:
                        save(S_STAGC, h);
                        break;
                    case S_PITARGET /*30*/:
                        h.stagc(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    case S_QUOT /*31*/:
                        unread(r, ch);
                        this.theCurrentColumn--;
                        break;
                    case S_STAGC /*32*/:
                        if (this.theSize > 0) {
                            this.theSize--;
                        }
                        h.pcdata(this.theOutputBuffer, 0, this.theSize);
                        this.theSize = 0;
                        break;
                    default:
                        throw new Error("Can't process state " + action);
                }
                save(45, h);
                save(ch, h);
                this.theState = this.theNextState;
                continue;
            }
        }
        h.eof(this.theOutputBuffer, 0, 0);
    }

    private void mark() {
        this.theLastColumn = this.theCurrentColumn;
        this.theLastLine = this.theCurrentLine;
    }

    public void startCDATA() {
        this.theNextState = S_CDATA;
    }

    private void save(int ch, ScanHandler h) throws IOException, SAXException {
        if (this.theSize >= this.theOutputBuffer.length - 20) {
            if (this.theState == S_PCDATA || this.theState == S_CDATA) {
                h.pcdata(this.theOutputBuffer, 0, this.theSize);
                this.theSize = 0;
            } else {
                char[] newOutputBuffer = new char[(this.theOutputBuffer.length * S_APOS)];
                System.arraycopy(this.theOutputBuffer, 0, newOutputBuffer, 0, this.theSize + S_ANAME);
                this.theOutputBuffer = newOutputBuffer;
            }
        }
        char[] cArr = this.theOutputBuffer;
        int i = this.theSize;
        this.theSize = i + S_ANAME;
        cArr[i] = (char) ch;
    }

    public static void main(String[] argv) throws IOException, SAXException {
        Scanner s = new HTMLScanner();
        Reader r = new InputStreamReader(System.in, HTTP.UTF_8);
        Writer w = new OutputStreamWriter(System.out, HTTP.UTF_8);
        s.scan(r, new PYXWriter(w));
        w.close();
    }

    private static String nicechar(int in) {
        if (in == S_CDATA) {
            return "\\n";
        }
        if (in < S_STAGC) {
            return "0x" + Integer.toHexString(in);
        }
        return Separators.QUOTE + ((char) in) + Separators.QUOTE;
    }
}
