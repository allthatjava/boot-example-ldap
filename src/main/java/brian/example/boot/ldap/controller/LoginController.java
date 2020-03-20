package brian.example.boot.ldap.controller;

import brian.example.boot.ldap.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.security.Principal;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Controller
public class LoginController {

    @Autowired
    private LdapTemplate ldapTemplate;

    /**
     * Login screen
     *
     * @return
     */
    @GetMapping( "/login" )
    public ModelAndView login(){
        return new ModelAndView("login");
    }

    /**
     * Default landing screen
     *
     * @return
     */
    @GetMapping("/")
    public ModelAndView index(){

        // This is only for testing purpose. --------------
        LdapQuery qeury = query()
                .attributes("cn","sn","mail")          // Attributes you want to get
                .base(LdapUtils.emptyLdapName())
                .where("uid")                 // Query field name
//                .is("riemann");                      // Query value
                .like("*");                         // Query value
        List<Person> result = ldapTemplate.search( qeury, new PersonAttributesMapper() );
        result.forEach( p -> System.out.println("====" +p.getFullName()+",sn:"+p.getLastName()) );
        //--------------------------------------------------

        return new ModelAndView("index");
    }

    /**
     * This 'profile' screen will be displayed only for the logged in user.
     *
     * @param p
     * @return
     */
    @GetMapping("/profile")
    public ModelAndView profile(Principal p){

        LdapQuery query = query()
                .attributes("cn","sn","mail")          // Attributes you want to get
                .base(LdapUtils.emptyLdapName())
                .where("uid")                 // Query field name
                .is(p.getName());                      // Query value
        List<Person> result = ldapTemplate.search( query, new PersonAttributesMapper() );
        Person person = result.get(0);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("profile");
        mav.addObject("person", person);

        return mav;
    }

    /**
     * Default logged in landing page
     *
     * @return
     */
    @GetMapping("/login-success")
    public ModelAndView loginSuccess(){
        return new ModelAndView("index");
    }

    /**
     * LDAP data mapper to Person object
     */
    private class PersonAttributesMapper implements AttributesMapper<Person> {

        public Person mapFromAttributes(Attributes attrs) throws NamingException {
            Person person = new Person();
            person.setFullName((String)attrs.get("cn").get());

            if( attrs.get("sn") != null ) {
                person.setLastName((String) attrs.get("sn").get());
            }
            if( attrs.get("mail") != null ) {
                person.setEmail((String) attrs.get("mail").get());
            }

            return person;
        }
    }
}
