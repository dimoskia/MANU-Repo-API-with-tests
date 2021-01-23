package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.dto.MembersFilter;
import mk.ukim.finki.manurepoapi.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    Page<Account> getMembersPage(MembersFilter filter, Pageable pageable);

    Account getMemberDetails(Long accountId);

}
