package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.request.MembersFilter;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.repository.ProfileImageRepository;
import mk.ukim.finki.manurepoapi.repository.projection.AvatarProjection;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import mk.ukim.finki.manurepoapi.repository.specification.MemberSpecification;
import mk.ukim.finki.manurepoapi.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final AccountRepository accountRepository;
    private final ProfileImageRepository profileImageRepository;

    @Override
    public Page<Account> getMembersPage(MembersFilter filter, Pageable pageable) {
        Specification<Account> specification = MemberSpecification.browseMembersSpec(filter);
        return accountRepository.findAll(specification, pageable);
    }

    @Override
    public Account getMemberDetails(Long accountId) {
        return accountRepository.findByIdAndEnabledTrue(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));
    }

    @Override
    public ProfileImage getProfileImage(Long imageId) {
        return profileImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException(ProfileImage.class, imageId));
    }

    @Override
    public List<MemberProjection> searchMembersByName(String query, Integer resultSize) {
        Pageable pageable = PageRequest.of(0, Math.min(15, resultSize));
        return accountRepository.searchByName(query, pageable);
    }

    @Override
    public AvatarProjection getAvatarData(Long accountId) {
        return accountRepository.findAvatarData(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));
    }

}
