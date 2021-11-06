package com.credex.fs.digital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.credex.fs.digital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IconTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Icon.class);
        Icon icon1 = new Icon();
        icon1.setId(1L);
        Icon icon2 = new Icon();
        icon2.setId(icon1.getId());
        assertThat(icon1).isEqualTo(icon2);
        icon2.setId(2L);
        assertThat(icon1).isNotEqualTo(icon2);
        icon1.setId(null);
        assertThat(icon1).isNotEqualTo(icon2);
    }
}
