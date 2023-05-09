package se.vgregion.delegation.delegation.view.service;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.vgregion.delegation.delegation.view.beans.DelegationDto;
import se.vgregion.delegation.delegation.view.beans.FilterPage;
import se.vgregion.delegation.delegation.view.beans.Person;
import se.vgregion.delegation.delegation.view.directory.LdapApi;
import se.vgregion.delegation.delegation.view.domain.Delegation;
import se.vgregion.delegation.delegation.view.repos.DelegationRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DelegationService {

    @Autowired
    private DelegationRepo delegationRepo;

    private static LdapApi ldapApi = LdapApi.getDefaultInstance();

    public List<DelegationDto> findAllActive() {
        return delegationRepo.findAllActive().stream()
                .map(delegation -> new DelegationDto(delegation)).collect(Collectors.toList());
    }

    public List<Person> findAllActivePersons() {
        return toPeople(delegationRepo.findAllActive().subList(0, 10));
    }

    public static List<Person> toPeople(List<Delegation> delegations) {
        List<Person> result = new ArrayList<>();
        delegations.forEach(delegation -> {
            String email = delegation.getDelegatedForEmail();
            if (email != null && !email.isBlank()) {
                String[] emails = email.split(Pattern.quote(","));
                if (emails.length == 2) {
                    List<Map<String, Object>> fromAd1 = ldapApi.query(String.format("(mail=%s)", emails[0]));
                    List<Map<String, Object>> fromAd2 = ldapApi.query(String.format("(mail=%s)", emails[1]));
                    // "hsaIdentity": "SE2321000131-P000000233140",
                    Person p1 = new Person();
                    p1.setEmail(emails[0]);
                    p1.setHsaId((String) fromAd1.get(0).get("hsaIdentity"));
                    result.add(p1);
                }
                //List<Map<String, Object>> items = ldapApi.query(String.format("(hsaIdentity=%s)", person));
            }
        });
        return result;
    }

    @Autowired
    private EntityManager entityManager;

    public FilterPage<String, DelegationDto> findByFilter(FilterPage<String, DelegationDto> filterPage) {
        String jpql = """
                select d from Delegation as d
                where d.validFrom < CURRENT_DATE 
                and d.validTo > CURRENT_DATE 
                and (d.delegateTo like ?1 or d.delegatedForEmail like ?2 or d.information like ?3)
                """;
        filterPage.setResult((List<DelegationDto>) entityManager.createQuery(jpql)
                .setParameter(1, filterPage.getQuery())
                .setParameter(2, filterPage.getQuery())
                .setParameter(3, filterPage.getQuery())
                .setFirstResult(filterPage.getPage() * filterPage.getPageSize())
                .setMaxResults(filterPage.getPageSize()).getResultList()
                .stream().map((d) -> new DelegationDto((Delegation) d)).collect(Collectors.toList()));
        return filterPage;
    }
}
