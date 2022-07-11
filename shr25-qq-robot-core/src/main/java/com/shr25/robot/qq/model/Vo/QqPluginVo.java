package com.shr25.robot.qq.model.Vo;

import com.shr25.robot.qq.plugins.RobotPlugin;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * qq插件配置
 *
 * @author huobing
 * @date 2022-6-14 14:53
 */
@Data
public class QqPluginVo implements Comparable<QqPluginVo>{
  /** QQ群插件关系id */
  private Long id;

  /** 插件id */
  private Long pluginid;

  /** 排序 */
  private Integer sort;

  /** 是否启用 */
  private boolean enabled;

  /** 插件 */
  private RobotPlugin robotPlugin;

  public void setPluginEnabled(Integer enabled){
      this.enabled = enabled != null && enabled > 0;
  }

  @Override
  public int compareTo(@NotNull QqPluginVo qqPluginVo) {
    if (getSort() == qqPluginVo.getSort()) {
      return 0;
    }
    return getSort() > qqPluginVo.getSort() ? 1 : -1;
  }

  @Override
  public int hashCode() {
    return pluginid.hashCode();
  }

  @Override
  public boolean equals(Object qqPluginVo) {
    return getPluginid() == ((QqPluginVo)qqPluginVo).getPluginid();
  }
}
