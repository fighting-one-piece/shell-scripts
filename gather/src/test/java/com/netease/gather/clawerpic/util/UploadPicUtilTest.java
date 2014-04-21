package com.netease.gather.clawerpic.util;

import java.util.ArrayList;
import java.util.List;

import com.netease.gather.domain.Picture;
import com.netease.gather.domain.PictureSet;

public class UploadPicUtilTest {

	public static void main(String[] args) {
		testSingltonPicturlSet();
	}
	
	private static void testSingltonPicturlSet(){
		
		PictureSet set = new PictureSet();
		set.setUrl("http://travel.163.com/photoview/5LIH0006/13346.html#p=9LRH0I4P5LIH0006");
		set.setTitle("testtesttesttestt单独测试某个图集esttesttesttesttesttest");
		List<Picture> list = new ArrayList<Picture>();
		UploadPicUtil.uploadPhoto(set , list);
	}
}
