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
package com.shr25.robot.music.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shr25.robot.common.RobotMsgPermission;
import com.shr25.robot.music.MusicInfo;
import com.shr25.robot.music.MusicSource;
import com.shr25.robot.music.NetEaseCrypto;
import com.shr25.robot.music.util.HttpRequestBuilder;
import com.shr25.robot.music.util.JsonBuilder;
import com.shr25.robot.music.util.Utils;
import org.springframework.stereotype.Service;

@Service
public class NetEaseMusicSource implements MusicSource {

	public NetEaseMusicSource() {
		super();
		log.info("开始加载 网易云音乐 插件~");

		addCommand("网易云", "进行特定引擎音乐搜索~", this.getName(), RobotMsgPermission.ALL, qqMessage -> {
			sendMusic(qqMessage);
			return true;
		});
	}

	public String queryRealUrl(String id) throws Exception {
		return "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
	}
	

	@Override
	public MusicInfo get(String keyword) throws Exception {
		String murl;
		JsonArray ja= HttpRequestBuilder.create("music.163.com")
		.url("/weapi/cloudsearch/get/web?csrf_token=")
		.referer("http://music.163.com/")
		.cookie("appver=1.5.0.75771;")
		.contenttype("application/x-www-form-urlencoded")
		.post()
		.send(NetEaseCrypto.weapiEncryptParam(JsonBuilder.object().add("s", keyword).add("type", 1).add("offset", 0).add("limit", 3).toString()))
		.readJson().get("result").getAsJsonObject().get("songs").getAsJsonArray();
		JsonObject jo = ja.get(0).getAsJsonObject();
		murl = queryRealUrl(jo.get("id").getAsString());
		int i = 0;
		while (!Utils.isExistent(murl)) {
			jo = ja.get(++i).getAsJsonObject();
			murl = queryRealUrl(jo.get("id").getAsString());
		}
		return new MusicInfo(jo.get("name").getAsString(),
				jo.get("ar").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString(),
				jo.get("al").getAsJsonObject().get("picUrl").getAsString(), murl,
				"https://y.music.163.com/m/song?id=" + jo.get("id").getAsString(), "网易云音乐", "", 100495085);
	}

	@Override
	public MusicInfo getId(String id) throws Exception {
		String murl;
		JsonObject jo =HttpRequestBuilder.create("music.163.com")
				.url("/weapi/song/detail?csrf_token=")
				.referer("http://music.163.com/")
				.cookie("appver=1.5.0.75771;")
				.contenttype("application/x-www-form-urlencoded")
				.post()
				.send(NetEaseCrypto.weapiEncryptParam(JsonBuilder.object().array("ids").add(id).end().toString()))
				.readJson()
				.get("songs").getAsJsonArray()
				.get(0).getAsJsonObject();
		murl = queryRealUrl(id);
		return new MusicInfo(jo.get("name").getAsString(),
				jo.get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString(),
				jo.get("album").getAsJsonObject().get("picUrl").getAsString(), murl,
				"https://y.music.163.com/m/song?id=" + jo.get("id").getAsString(), "网易云音乐", "", 100495085);
	}

}
