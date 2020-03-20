package brian.example.boot.ldap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    /**
     * Default Http Security Settings
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login**").permitAll()
                .antMatchers("/profile/**").fullyAuthenticated()
                .antMatchers("/").permitAll()
             .and()
        .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/login-success")
                .failureUrl("/login?error")
                .permitAll()
                .and()
        .logout()
//                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
//                .permitAll()
        ;
    }

    /**
     * Login check by LDAP
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .ldapAuthentication()
                .contextSource(ldapContextSource())
                .userDnPatterns("uid={0}");
    }

    @Bean
    public LdapContextSource ldapContextSource(){
        LdapContextSource ctxSrc = new LdapContextSource();
        ctxSrc.setUrl("ldap://192.168.99.100:389");
        ctxSrc.setBase("dc=example,dc=org");
        ctxSrc.setUserDn("cn=admin,dc=example,dc=org");
        ctxSrc.setPassword("admin");

        return ctxSrc;
    }

    @Bean
    public LdapTemplate ldapTemplate(){
        return new LdapTemplate(ldapContextSource());
    }
}
