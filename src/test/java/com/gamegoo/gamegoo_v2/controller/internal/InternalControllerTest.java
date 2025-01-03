package com.gamegoo.gamegoo_v2.controller.internal;

import com.gamegoo.gamegoo_v2.controller.ControllerTestSupport;
import com.gamegoo.gamegoo_v2.social.friend.controller.FriendInternalController;
import com.gamegoo.gamegoo_v2.social.friend.service.FriendFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({FriendInternalController.class})
public class InternalControllerTest extends ControllerTestSupport {

    @MockitoBean
    private FriendFacadeService friendFacadeService;

    private static final String API_URL_PREFIX = "/api/v2/internal";
    private static final Long MEMBER_ID = 1L;

    @DisplayName("모든 친구 id 조회 성공")
    @Test
    void getFriendIdListSucceeds() throws Exception {
        // given
        List<Long> response = new ArrayList<>();

        given(friendFacadeService.getFriendIdList(any(Long.class))).willReturn(response);

        // when // then
        mockMvc.perform(get(API_URL_PREFIX + "/{memberId}/friend/ids", MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

}
