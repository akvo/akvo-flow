package org.akvo.flow.rest.security.featureflag;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.gallatinsystems.common.util.PropertyUtil;

public class EntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint defaultEntryPoint;
    private final AuthenticationEntryPoint alternativeEntryPoint;
    private final String defaultAuthProvider;

    public EntryPoint(AuthenticationEntryPoint defaultEntryPoint, AuthenticationEntryPoint alternativeEntryPoint) {
        this.defaultEntryPoint = defaultEntryPoint;
        this.alternativeEntryPoint = alternativeEntryPoint;
        this.defaultAuthProvider =PropertyUtil.getProperty("defaultAuthProvider");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if ("auth0".equals(defaultAuthProvider)) {
            alternativeEntryPoint.commence(request, response, authException);
        } else {
            defaultEntryPoint.commence(request, response, authException);
        }

    }
}
