package com.shr25.robot.base;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 定时器实体类
 *
 * @author huobing
 * @date 2022-6-27 21:26
 */
@Data
public class DelayTask implements Delayed {
  /** 需要执行定时任务的qq群 */
  private Long groupId;

  /** 下次执行任务时间 */
  private Date time;

  /** 定时器需要执行的数据 */
  private Object data;

  public DelayTask(Long groupId, Date time, Object data) {
    this.groupId = groupId;
    this.time = time;
    this.data = data;
  }

  @Override
  public int hashCode() {
    return groupId.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof DelayTask){
      DelayTask entity = (DelayTask)obj;
      return this.getGroupId().equals(entity.getGroupId());
    }
    return false;
  }

  @Override
  public long getDelay(@NotNull TimeUnit unit) {
    return unit.convert(time.getTime() - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
  }

  @Override
  public int compareTo(@NotNull Delayed o) {
    DelayTask item = (DelayTask) o;
    long diff = this.getTime().getTime() - item.getTime().getTime();
    if (diff <= 0) {
      return -1;
    }else {
      return 1;
    }
  }
}
