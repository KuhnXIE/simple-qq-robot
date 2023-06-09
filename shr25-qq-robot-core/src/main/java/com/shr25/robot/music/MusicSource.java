/**
 * Mirai Song Plugin
 * Copyright (C) 2021  khjxiaogu
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.shr25.robot.music;

// TODO: Auto-generated Javadoc

import com.shr25.robot.base.BaseAbstractSim;
import com.shr25.robot.qq.model.QqMessage;
import net.mamoe.mirai.message.data.Message;

import static com.shr25.robot.qq.service.RobotManagerService.subParam;

/**
 * 音乐来源接口.
 */
public abstract interface MusicSource extends BaseAbstractSim {

	String name = "音乐";

	default String getName(){
		return name;
	}

	/**
	 * 搜索对应关键词并返回音乐信息.<br>
	 * 返回音乐信息不能为null。
	 * 
	 * @param keyword 关键词
	 * @return return 返回音乐信息数据类
	 * @throws Exception 如果发生异常或者找不到音乐，都抛出异常。
	 */
	public MusicInfo get(String keyword) throws Exception;
	/**
	 * 搜索对应音乐ID并返回音乐信息.<br>
	 * 返回音乐信息不能为null。
	 * 
	 * @param id 音乐
	 * @return return 返回音乐信息数据类
	 * @throws Exception 如果发生异常或者找不到音乐，都抛出异常。
	 */
	public MusicInfo getId(String id) throws Exception;

	/**
	 * 返回是否对全部搜索可见<br>
	 *
	 * @return 如果是全部搜索可以搜索本来源，返回true.
	 */
	public default boolean isVisible() {
		return true;
	};

	/**
	 * 根据关键字返回音乐
	 */
	default void sendMusic(QqMessage qqMessage){
		String content = qqMessage.getContent();
		// 截取后面的关键字
		String keyword = subParam(content);
/*		MusicSource musicSource = MusicFactory.getMusicSource(subCommand(content));
		if (musicSource == null)
			throw new IllegalArgumentException("music source not exists");*/
		// 此处使用默认样板
		MusicCardProvider cb = MusicFactory.getCard("Mirai");
		if (cb == null)
			throw new IllegalArgumentException("card template not exists");

		MusicInfo musicInfo;
		try {
			musicInfo = this.get(keyword);
		} catch (Throwable t) {
			qqMessage.putReplyMessage("无法找到歌曲" + keyword);
			return;
		}
		try {
			Message m = cb.process(musicInfo, qqMessage.getContact());
			if (m != null) {
				qqMessage.putReplyMessage(m);
				return;
			}
		} catch (Throwable t) {
			log.error("封装音乐消息失败！");
		}
		qqMessage.putReplyMessage("分享歌曲失败。");
	}

}