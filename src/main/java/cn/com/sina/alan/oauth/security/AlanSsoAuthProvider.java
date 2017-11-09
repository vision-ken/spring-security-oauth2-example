package cn.com.sina.alan.oauth.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Created by wanghongfei(hongfei7@staff.sina.com.cn) on 9/11/16.
 */
@Component
public class AlanSsoAuthProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(AlanSsoAuthProvider.class);

    /**
     * @param authentication 认证请求对象
     * @return 认证对象。如果本AuthenticationProvider不处理传入的authentication对象，可以返回null，此时authentication对象将会传递给下一个AuthenticationProvider进行处理。
     * @throws AuthenticationException 认证失败时抛出异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("自定义provider调用");

        // 返回一个Token对象表示登陆成功
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Collections.<GrantedAuthority>emptyList());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
