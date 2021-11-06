package com.credex.fs.digital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.credex.fs.digital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RewardTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reward.class);
        Reward reward1 = new Reward();
        reward1.setId(1L);
        Reward reward2 = new Reward();
        reward2.setId(reward1.getId());
        assertThat(reward1).isEqualTo(reward2);
        reward2.setId(2L);
        assertThat(reward1).isNotEqualTo(reward2);
        reward1.setId(null);
        assertThat(reward1).isNotEqualTo(reward2);
    }
}
