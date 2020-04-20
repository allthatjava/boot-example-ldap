package brian.example.boot.ldap.controller;

import brian.example.boot.ldap.mapper.PersonAttributesMapper;
import brian.example.boot.ldap.model.Person;
import brian.example.boot.ldap.service.ldap.LdapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Controller
public class LoginController {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LdapClient ldapClient;

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
        // Uses LdapTemplate from Spring Bean
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

        // For testing purpose
        // Next call will create individual LdapTemplate for calls
        Person tom = ldapClient.getPersonByUidFromDocker("tom");
        List<Person> people = ldapClient.getPeopleFromFreeLdap();

        mav.addObject("tom", tom);
        mav.addObject("people", people);
        mav.addObject("peopleSearch", ldapClient.getPeopleFromFreeLdapWithSearch());

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

}
