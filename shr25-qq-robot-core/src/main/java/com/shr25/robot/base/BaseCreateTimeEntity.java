package com.shr25.robot.base;

import java.util.Date;

/**
 * 只有创建时间Entity基类
 * @author huobing
 * @date: 2022年3月24日 下午10:33:31
 */
public class BaseCreateTimeEntity extends BaseEntity
{
    /** 创建者 */
    private Long createId;

    /** 创建者名称 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
