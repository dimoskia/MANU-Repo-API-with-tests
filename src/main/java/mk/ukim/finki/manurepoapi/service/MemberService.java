package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.dto.MembersFilter;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {

    Page<Account> getMembersPage(MembersFilter filter, Pageable pageable);

    Account getMemberDetails(Long accountId);

    ProfileImage getProfileImage(Long imageId);

    List<MemberProjection> searchMembersByName(String query, Integer resultSize);

}
