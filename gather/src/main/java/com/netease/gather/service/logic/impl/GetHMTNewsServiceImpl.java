package com.netease.gather.service.logic.impl;

import com.netease.gather.common.constants.Config;
import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.DateUtil;
import com.netease.gather.common.util.HessianUtil;
import com.netease.gather.common.util.HttpUtil;
import com.netease.gather.common.util.JsonUtil;
import com.netease.gather.service.logic.GetHMTNewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Service("getHMTNewsService")
public class GetHMTNewsServiceImpl implements GetHMTNewsService {
	private static final Logger logger = LoggerFactory
			.getLogger(GetHMTNewsServiceImpl.class);

	private final static String TAI_KEY = "台湾";
	private final static String[] TAI_ARRAY = { "台湾", "台北", "台媒", "新台币", "马英九",
			"陈水扁", "王金平", "宋楚瑜", "连战", "国民党", "民进党", "行政院", "立法院", "蓝营", "绿营",
			"台南", "高雄", "花莲", "桃园", "新竹", "新北", "基隆", "阿里山", "日月潭", "联合报",
			"中国时报" };

	private final static String HM_KEY = "港澳";
	private final static String[] HM_ARRAY = { "香港", "澳门", "特别行政区", "梁振英",
			"崔世安", "港媒", "香港媒体", "tvb", "TVB", "政务司", "律政司", "新界", "九龙", "深水埗",
			"维多利亚港", "香港大学", "港元", "港币", "澳门元" };

	private final static String JP_KEY = "日本";
	private final static String[] JP_ARRAY = { "日本", "东京", "大阪", "东电", "福岛",
			"安倍", "安倍晋三", "天皇", "小泉", "小泉纯一郎", "樱花", "寿司", "拉面", "维新会", "自民党",
			"民主党", "山口组", "富士山", "靖国", "神社", "参拜" };

	private final static String AM_KEY_1 = "美国";
	private final static String[] AM_ARRAY1 = { "奥巴马", "克里", "希拉里", "参议院",
			"众议院", "白宫", "华盛顿", "谷歌", "微软", "NASA", "民主党", "共和党", "五角大楼",
			"克林顿", "盖茨", "肯尼迪", "美联储", "布什", "巴菲特", "星巴克", "里根", "梦露", "富兰克林",
			"林肯", "特斯拉", "罗斯福", "胡佛", "五角大楼", "艾森豪威尔", "杜鲁门", "水门", "尼克松",
			"福特", "卡特", "米歇尔", "德克萨斯", "得克萨斯", "德州", "得州", "夏威夷", "衣阿华", "伊利诺",
			"伊利诺伊", "麻省", "MIT", "哈佛", "耶鲁", "纽约", "常春藤", "堪萨斯", "好莱坞", "麻萨诸塞",
			"阿拉巴马", "科罗拉多", "拉斯维加斯蒙他拿" };

	private final static String AM_KEY_2 = "美国";
	private final static String[] AM_ARRAY2 = { "蒙大拿", "新墨西哥", "俄州", "俄克拉荷马",
			"达科他", "梦工厂", "迪斯尼", "可口可乐", "麦当劳", "肯德基", "百事", "弗吉尼亚", "黑手党",
			"茶党", "绿党", "乔治亚", "德拉华", "爱达荷", "肯塔基", "密执安", "内华达", "费城", "卡罗来纳",
			"内布拉斯加", "阿拉斯加", "加利福尼亚", "俄勒冈", "田纳西", "印地安那", "缅因", "密西西比",
			"新罕布什尔", "路易斯安那", "奥尔良", "硅谷", "明尼苏达", "俄亥俄", "亚利桑那", "康涅狄格",
			"宾夕法尼亚", "弗吉尼亚", "佛罗里达", "马里兰", "密苏里", "新泽西", "佛蒙特", "阿肯色", "罗得岛",
			"犹他", "怀俄明", "威斯康星", "乔丹", "伍兹", "NBA", "圣诞", "感恩节", "罗杰斯", "亚马逊" };

	private final static String OTHER_KEY = "周边";
	private final static String[] OTHER_ARRAY = { "俄罗斯", "俄国", "俄", "普京",
			"梅德韦杰夫", "金正恩", "金正日", "朝鲜", "平壤", "韩国", "釜山", "济州岛", "首尔", "克格勃",
			"莫斯科", "冬宫", "圣彼得堡", "列宁", "斯大林", "蒙古", "越南", "昂山素季", "老挝", "河内",
			"胡志明", "柬埔寨", "金边", "缅甸", "仰光", "马来西亚", "吉隆坡", "菲律宾", "新加坡", "李光耀",
			"英拉", "他信", "泰国", "清迈", "曼谷", "印度", "新德里", "勃列日涅夫", "沙皇", "托尔斯泰",
			"文莱", "印度尼西亚", "印尼", "雅加达", "红衫军", "斯里兰卡", "孟加拉", "不丹", "东帝汶",
			"普密蓬", "湄公", "普吉岛", "巴厘岛", "泡菜", "阿基诺" };

	private final static String CHANNEL_ID = "0001gd";

	private final static String TEST_ID = "00014R7I";

	private static final long halfHour = 30 * 60 * 1000;

	private static final int FETCH_SIZE = 100;

	private static String todayDate = null;

	private static Map<String, String> coltopic = new HashMap<String, String>() {
		{
			if ("demo".equals(Config.init().get("env"))) {
				// 测试
				put(TAI_KEY, TEST_ID);
				put(HM_KEY, TEST_ID);
				put(JP_KEY, TEST_ID);
				put(AM_KEY_1, TEST_ID);
				put(AM_KEY_2, TEST_ID);
				put(OTHER_KEY, TEST_ID);
			} else if ("prod".equals(Config.init().get("env"))) {
				put(HM_KEY, "00014PGM");
				put(TAI_KEY, "00014PGN");
				put(AM_KEY_1, "00014PGO");
				put(AM_KEY_2, "00014PGO");
				put(JP_KEY, "00014PGP");
				put(OTHER_KEY, "00014PGQ");
			}
		}
	};

	private static Map<String, String[]> keyMap = new HashMap<String, String[]>() {
		{
			put(HM_KEY, HM_ARRAY);
			put(TAI_KEY, TAI_ARRAY);
			put(AM_KEY_1, AM_ARRAY1);
			put(AM_KEY_2, AM_ARRAY2);
			put(JP_KEY, JP_ARRAY);
			put(OTHER_KEY, OTHER_ARRAY);
		}
	};

	@Override
	public void getHMTNewsAndSendToCMS() throws Exception {
		todayDate = DateUtil.DateToString(new Date(), "yyyy-MM-dd");

		// String[] keyArray = { TAI_KEY, HM_KEY, JP_KEY, AM_KEY, OTHER_KEY };
		for (String key : keyMap.keySet()) {
			logger.info("开始获得新闻id --- " + key);
			int start = 0;
			while (true) {
                List<Map> articleIDList = new ArrayList<Map>();
//				List<Map> articleIDList = CmsSearchApi.searchArticle(
//						Arrays.asList(keyMap.get(key)), CHANNEL_ID, null, null,
//						null, null, null, null, start, FETCH_SIZE, false);

				if (articleIDList != null && articleIDList.size() > 0) {
					start += articleIDList.size();
					// 根据id取回具体的新闻并根据条件筛选
					List<String> docIdList = getNews(articleIDList, key);
					if (docIdList != null && docIdList.size() > 0)
						sendNewsToCMS(docIdList, key);

				} else
					break;
			}
		}
	}

	private List<String> getNews(List<Map> mapList, String keyToGET)
			throws Exception {
		List<String> docIdList = new ArrayList<String>();
		Date now = new Date();
		if (mapList != null) {
			logger.info("新闻筛选完毕，根据id取新闻 --- " + keyToGET);
			for (Map map : mapList) {
				String docid = (String) map.get("id");
				// 从CMS取回新闻
				Map<String, String> article = HessianUtil.getCmsWebService()
						.getArticle(docid);

				String title = article.get("title");
				Date pTime = DateUtil.stringToDate(article.get("ptime"),
						"yyyy-MM-dd HH:mm:ss");

				// 取半小时内的新闻
				if (pTime.getTime() + halfHour < now.getTime()) {
					continue;
				}

				// 排除掉title中包含日期的新闻(重新编辑过)及title长度大于22.5或者小于14的新闻
				if (title != null && !title.contains(todayDate)
						&& title.length() < 23 && title.length() >= 14) {
					// logger.info("get title:{},length:{}", title,
					// title.length());
					boolean right = false;
					// 为了提高查询的准确度,再次验证关键词是否包含在标题里
					for (String key : keyMap.get(keyToGET)) {
						if (title.contains(key)) {
							right = true;
							break;
						}
					}

					if (right) {
						docIdList.add(docid);
						logger.info("get title:{},postTime:{}", title,
								pTime.toString());
					}
				}
			}
		}
		return docIdList;
	}

	private void sendNewsToCMS(List<String> docIdList, String key)
			throws IOException {
		logger.info("把 {} 新闻推送到cms相关栏目 {}", key, coltopic.get(key));
		Map<String, List<String>> jsonMap = new HashMap<String, List<String>>();
		// 放到CMS相应的栏目中
		jsonMap.put(coltopic.get(key), docIdList);
		String jsonstr = JsonUtil.toJsonStr(jsonMap);

		String ret = HttpUtil.getURL(
				Constants.ROBOT_PUSH_CMS_URL + URLEncoder.encode(jsonstr, "utf-8"), "UTF8", null);
		logger.info("json:{},ret{}", jsonstr, ret);
	}
}
