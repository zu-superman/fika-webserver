package cn.hy.fikaweb.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FikawebProfileVO {

    private String nickname;

    private String profileId;

    private Boolean hasFleaBan;

    private Integer level;
}
