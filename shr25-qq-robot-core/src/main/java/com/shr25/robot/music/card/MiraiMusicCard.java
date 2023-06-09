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
package com.shr25.robot.music.card;
import com.shr25.robot.music.MusicCardProvider;
import com.shr25.robot.music.MusicInfo;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;

import java.util.HashMap;
import java.util.Map;

public class MiraiMusicCard implements MusicCardProvider {
	static Map<Long,MusicKind> appidMappings=new HashMap<>();
	static MusicCardProvider def=new XMLCardProvider();
	static {
		for(MusicKind mk:MusicKind.values())
			appidMappings.put(mk.getAppId(),mk);
	}
	@Override
	public Message process(MusicInfo mi, Contact ct) throws Exception {
		MusicKind omk=appidMappings.get(mi.appid);
		if(omk!=null)
			return new MusicShare(omk,mi.title,mi.desc,mi.jurl,mi.purl,mi.murl);
		return def.process(mi, ct);
	}

}
