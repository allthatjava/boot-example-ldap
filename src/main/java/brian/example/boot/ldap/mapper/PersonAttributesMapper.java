package brian.example.boot.ldap.mapper;

import brian.example.boot.ldap.model.Person;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class PersonAttributesMapper implements AttributesMapper<Person> {

    /**
     * LDAP data mapper to Person object
     */
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
