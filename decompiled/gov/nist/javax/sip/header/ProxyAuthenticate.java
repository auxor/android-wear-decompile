package gov.nist.javax.sip.header;

import javax.sip.address.URI;
import javax.sip.header.ProxyAuthenticateHeader;
import org.apache.http.auth.AUTH;

public class ProxyAuthenticate extends AuthenticationHeader implements ProxyAuthenticateHeader {
    private static final long serialVersionUID = 3826145955463251116L;

    public ProxyAuthenticate() {
        super(AUTH.PROXY_AUTH);
    }

    public URI getURI() {
        return null;
    }

    public void setURI(URI uri) {
    }
}
