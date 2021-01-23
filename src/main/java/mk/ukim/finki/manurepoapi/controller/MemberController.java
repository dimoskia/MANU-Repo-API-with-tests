package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.MemberCard;
import mk.ukim.finki.manurepoapi.dto.MemberDetails;
import mk.ukim.finki.manurepoapi.dto.MembersFilter;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.service.MemberService;
import mk.ukim.finki.manurepoapi.util.DtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public Page<MemberCard> getMembersPage(
            MembersFilter filter,
            @PageableDefault(sort = "lastName", direction = Sort.Direction.ASC, value = 15) Pageable pageable) {
        Page<Account> accountPage = memberService.getMembersPage(filter, pageable);
        return accountPage.map(DtoMapper::mapAccountToMemberCard);
    }

    @GetMapping("/{accountId}")
    public MemberDetails getMemberDetails(@PathVariable Long accountId) {
        Account account = memberService.getMemberDetails(accountId);
        return DtoMapper.mapAccountToMemberDetails(account);
    }

    @GetMapping("/profileImage/{imageId}")
    public void getProfileImage(@PathVariable Long imageId, HttpServletResponse response) throws IOException {
        ProfileImage profileImage = memberService.getProfileImage(imageId);
        response.setContentType(profileImage.getContentType());
        FileCopyUtils.copy(profileImage.getData(), response.getOutputStream());
        response.getOutputStream().flush();
    }

}
