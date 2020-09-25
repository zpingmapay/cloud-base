package com.xyz.cloud.db;

import com.xyz.cloud.db.annotation.CreateTime;
import com.xyz.cloud.db.annotation.UpdateTime;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * po基类
 *
 * @author 7580
 */
@Data
public class BasePo implements Serializable {
    /** 主键id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** 状态 0无效、1有效 */
    private Integer status;
    /** 创建时间 */
    @CreateTime
    private Date createDate;
    /** 更新时间 */
    @UpdateTime
    private Date updateDate;
    /** 备注 */
    private String remark;

    public Date getModifyDate() {
        return this.updateDate;
    }
}
