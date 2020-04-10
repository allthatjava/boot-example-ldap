package brian.example.boot.ldap.service.ldap;

import brian.example.boot.ldap.mapper.PersonAttributesMapper;
import brian.example.boot.ldap.model.Person;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * Uses two separate LdapTemplates for two different LDAP servers.
 *
 */
@Service
public class LdapClient {

    /**
     * Connects to the local OpenLDAP on Docker
     */
    private LdapTemplate getLdapTemplateFromDocker() {

        LdapContextSource ctxSrc = new LdapContextSource();
        ctxSrc.setUrl("ldap://192.168.99.100:389");
        ctxSrc.setBase("dc=example,dc=org");
        ctxSrc.setUserDn("cn=admin,dc=example,dc=org");
        ctxSrc.setPassword("admin");
        ctxSrc.afterPropertiesSet();

        return new LdapTemplate(ctxSrc);
    }

    /**
     * Connects to the Free LDAP test Server
     */
    private LdapTemplate getLdapTemplateFromFreeLdap() {

        LdapContextSource ctxSrc = new LdapContextSource();
        ctxSrc.setUrl("ldap://ldap.forumsys.com:389");
        ctxSrc.setBase("dc=example,dc=com");
        ctxSrc.setUserDn("cn=read-only-admin,dc=example,dc=com");
        ctxSrc.setPassword("password");
        ctxSrc.afterPropertiesSet();

        return new LdapTemplate(ctxSrc);
    }

    /**
     * Returns one person
     *
     * @param uid
     * @return
     */
    public Person getPersonByUidFromDocker(String uid) {
        LdapQuery query = query()
                .attributes("cn", "sn", "mail")         // Attributes you want to get
                .base(LdapUtils.emptyLdapName())
                .where("uid")                   // Query field name
                .is(uid);                               // Query value

        List<Person> result = getLdapTemplateFromDocker().search(query, new PersonAttributesMapper());

        if (result.isEmpty())
            throw new RuntimeException("Nothing search result found");

        return result.get(0);
    }

    public List<Person> getPeopleFromFreeLdap() {
        LdapQuery query = query()
                .attributes("*")          // Attributes you want to get
                .base(LdapUtils.emptyLdapName())
                .where("objectClass")         // Query field name
                .is("person");                      // Query value

        LdapTemplate template = getLdapTemplateFromFreeLdap();
        return template.search(query, new PersonAttributesMapper());
    }
}
