package se.vgregion.delegation.delegation.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.vgregion.delegation.delegation.view.beans.DelegationDto;
import se.vgregion.delegation.delegation.view.beans.FilterPage;
import se.vgregion.delegation.delegation.view.beans.Person;
import se.vgregion.delegation.delegation.view.service.DelegationService;

import java.util.List;

@RestController
public class MainController {

    @Autowired
    private DelegationService delegationService;

    /*@GetMapping("api/delegation")
    public List<DelegationDto> findAllActive() {
        return delegationService.findAllActive();
    }*/

    @GetMapping("api/person")
    public List<Person> findAllActivePersons() {
        return delegationService.findAllActivePersons();
    }

    @GetMapping(value = "api/delegation")
    public FilterPage<String, DelegationDto> findByFilter(@RequestParam(value = "f", defaultValue = "%") String filter,
                                            @RequestParam(value = "p", defaultValue = "0") Integer page,
                                            @RequestParam(value = "ps", defaultValue = "20") Integer pageSize) {
        FilterPage<String, DelegationDto> filterPage = new FilterPage<>();
        filterPage.setPage(page);
        filterPage.setQuery(filter);
        filterPage.setPageSize(pageSize);
        return delegationService.findByFilter(filterPage);
    }


}