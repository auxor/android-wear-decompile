package gov.nist.javax.sip.header;

import gov.nist.javax.sip.header.ims.AuthorizationHeaderIms;
import javax.sip.header.AuthorizationHeader;
import org.apache.http.auth.AUTH;

public class Authorization extends AuthenticationHeader implements AuthorizationHeader, AuthorizationHeaderIms {
    private static final long serialVersionUID = -8897770321892281348L;

    public Authorization() {
        super(AUTH.WWW_AUTH_RESP);
    }
}
