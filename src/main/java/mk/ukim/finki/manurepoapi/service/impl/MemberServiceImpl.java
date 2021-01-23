package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.MembersFilter;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.repository.specification.MemberSpecification;
import mk.ukim.finki.manurepoapi.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final AccountRepository accountRepository;

    @Override
    public Page<Account> getMembersPage(MembersFilter filter, Pageable pageable) {
        Specification<Account> specification = MemberSpecification.browseMembersSpec(filter);
        return accountRepository.findAll(specification, pageable);
    }

    @Override
    public Account getMemberDetails(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));
    }
}
