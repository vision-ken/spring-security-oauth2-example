package cn.com.sina.alan.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghongfei(hongfei7@staff.sina.com.cn) on 9/12/16.
 */
@Configuration
public class OAuthSecurityConfig extends AuthorizationServerConfigurerAdapter {
	
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    /**
     * 声明 TokenStore实现，使用数据库存储token信息
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    /**
     * 认证服务器端点配置
     * 
     * Spring Cloud Security OAuth2通过DefaultTokenServices类来完成token生成、过期等 OAuth2 标准规定的业务逻辑，而DefaultTokenServices又是通过TokenStore接口完成对生成数据的持久化。
     * 声明授权和token的端点以及token的服务的一些配置信息，比如采用什么存储方式、token的有效期等
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	/*
    	 *  默认情况下，AuthorizationServerEndpointsConfigurer支持除了密码外的所有授权类型。
    	 *  这里设置authenticationManager以支持password授权类型，具体可参考AuthorizationServerEndpointsConfigurer.getDefaultTokenGranters()方法
    	 */
        endpoints.authenticationManager(authenticationManager); 
        endpoints.tokenStore(tokenStore());

        // 配置TokenServices
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true); // 支持refresh token
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer()); // token的附加信息
        tokenServices.setAccessTokenValiditySeconds( (int) TimeUnit.DAYS.toSeconds(30)); // 30天
        
        endpoints.tokenServices(tokenServices);
    }

    /**
     * 声明安全约束，哪些允许访问，哪些不允许访问
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        //oauthServer.checkTokenAccess("isAuthenticated()");
        oauthServer.checkTokenAccess("permitAll()");
        oauthServer.allowFormAuthenticationForClients();
    }

    /**
     * 声明 ClientDetails实现，使用数据库存储客户端信息
     * @return
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * client的信息的读取：在ClientDetailsServiceConfigurer类里面进行配置，可以有in-memory、jdbc等多种读取方式
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails()); // 使用数据库方式
/*        clients.inMemory()  				// TokenStore的默认实现为InMemoryTokenStore，即内存存储。ClientDetailsService使用默认的InMemoryClientDetialsService
                .withClient("client")
                .secret("secret")
                .authorizedGrantTypes("authorization_code")
                .scopes("app");*/
    }

}
