package com.hackaton.smarthack.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hackaton.smarthack.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HashTagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HashTag.class);
        HashTag hashTag1 = new HashTag();
        hashTag1.setId(1L);
        HashTag hashTag2 = new HashTag();
        hashTag2.setId(hashTag1.getId());
        assertThat(hashTag1).isEqualTo(hashTag2);
        hashTag2.setId(2L);
        assertThat(hashTag1).isNotEqualTo(hashTag2);
        hashTag1.setId(null);
        assertThat(hashTag1).isNotEqualTo(hashTag2);
    }
}
