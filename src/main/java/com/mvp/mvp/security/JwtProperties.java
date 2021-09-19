package com.mvp.mvp.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@ConfigurationProperties(prefix = JwtProperties.JWT_PROPERTIES_PREFIX)
public class JwtProperties implements Validator {

    public final static String JWT_PROPERTIES_PREFIX = "spring.jwt";
    private String secretKey;
    private long validityInMilliseconds = 2600000;
    private String headerName;
    private String startsWith;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getValidityInMilliseconds() {
        return validityInMilliseconds;
    }

    public void setValidityInMilliseconds(long validityInMilliseconds) {
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return JwtProperties.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        JwtProperties properties = (JwtProperties) target;

        if (!StringUtils.hasLength(properties.getSecretKey())) {
            errors.rejectValue("secretKey", "NotEmpty",
                    "secretKey should not be empty or null.");
        }
        if (!StringUtils.hasLength(properties.getHeaderName())) {
            errors.rejectValue("headerName", "NotEmpty",
                    "headerName should not be empty or null.");
        }
        if (!StringUtils.hasLength(properties.getStartsWith())) {
            errors.rejectValue("startsWith", "NotEmpty",
                    "startsWith should not be empty or null.");
        }
        if (properties.getValidityInMilliseconds() <= 0) {
            errors.rejectValue("validityInMilliseconds", "NotEmpty",
                    "validityInMilliseconds should not be lower or equal to 0");
        }

    }
}
