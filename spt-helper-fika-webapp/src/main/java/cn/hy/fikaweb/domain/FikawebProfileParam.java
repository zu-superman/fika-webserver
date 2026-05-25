package cn.hy.fikaweb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FikawebProfileParam {

    private String nickname;

    private String profileId;

    private Boolean hasFleaBan;

    private Integer level;
}
