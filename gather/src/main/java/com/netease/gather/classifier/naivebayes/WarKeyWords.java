package com.netease.gather.classifier.naivebayes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AzraelX
 * Date: 13-10-12
 * Time: 上午11:14
 */
public class WarKeyWords {

    public static Map<String,List<String>> classwords = new LinkedHashMap<String,List<String>>();

    static {
        List<String> zhoubianwords = Arrays.asList("菲律宾","菲","菲军","印度","印军","日本","日","自卫队","韩国","韩","韩军","朝鲜","朝","朝军","金正恩",
                "印尼","柬埔寨","缅甸","缅军","越南","越军","远东","塔利班","阿富汗","阿军","巴基斯坦","巴军","驻阿联军","驻阿美军","苏丹");//"巴",
        List<String> guojiwords = Arrays.asList("美军","美","美海军","美空军","美海军陆战队","叙利亚","叙","叙军","以色列","以军","俄罗斯","俄","俄军","乌克兰","伊朗","伊",
                "伊拉克","非洲","刚果","土耳其","土","中东","英","英军","法","法军","中非","埃及","德军");//
        List<String> taihaiwords = Arrays.asList("台湾","台军","金门","马英九","台空军","台海军");
        List<String> zhongguowords = Arrays.asList("解放军","中国海军","中国陆军","中国海军","中国","我军","甲午","国产","军报","歼10","歼31","歼20","歼15",
                "歼16","直10","武直10","战士","济南军区","成都军区","沈阳军区","北京军区","武汉军区","南京军区","南海舰队","东海舰队","北海舰队","空警200","空警2000",
                "高新","056","056轻护舰","056护卫舰","054护卫舰","054","171","171驱逐舰","直20","直9","东风","东风11","东风21","东风15","东风21","东风41",
                "红旗","红旗9","红旗19","辽宁舰","中国国防");

        //强顺序
        classwords.put("zhoubian",zhoubianwords);
        classwords.put("guoji",guojiwords);
        classwords.put("taihai",taihaiwords);
        classwords.put("zhongguo",zhongguowords);
    }
}
