package com.netease.gather.web.controller.front;

import com.netease.gather.common.constants.Constants;
import com.netease.gather.common.util.*;
import com.netease.gather.common.util.exception.ApplicationException;
import com.netease.gather.service.data.HotService;
import com.netease.gather.service.data.SpecialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Resource(name = "mappingJacksonJsonView")
    private View view;

    @Resource(name = "hotService")
    private HotService hotService;

    @Resource(name = "specialService")
    private SpecialService specialService;

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    /**
     * reciveLog
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="getTimeSec",method={RequestMethod.POST,RequestMethod.GET})
    public ModelAndView clicklog(HttpServletRequest request,HttpServletResponse response,@RequestParam(value = "channel") String channel) throws Exception {
        ModelAndView mav = new ModelAndView();
        try {
            Map para = new HashMap();
            para.put("channel",channel);
            List<String> rlist = hotService.getTimeSection(para);
            Collections.sort(rlist,new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String str1[] = o1.split("--");
                    String str2[] = o2.split("--");
                    String today = DateUtil.DateToString(new Date(),"yyyy-MM-dd");
                    Long bw1 = DateUtil.stringToDate(today+" "+str1[1],"yyyy-MM-dd HH:mm:ss").getTime()-DateUtil.stringToDate(today+" "+str1[0],"yyyy-MM-dd HH:mm:ss").getTime();
                    Long bw2 = DateUtil.stringToDate(today+" "+str2[1],"yyyy-MM-dd HH:mm:ss").getTime()-DateUtil.stringToDate(today+" "+str2[0],"yyyy-MM-dd HH:mm:ss").getTime();
                    return bw2.compareTo(bw1);
                }
            });
            mav.addObject("result", rlist);
            mav.addObject("status", "success");
        } catch (ApplicationException e) {
            logger.error(e.getMessage());
            mav.addObject("status", "info");
            mav.addObject("tip", e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            mav.addObject("status", "error");
//            mav.addObject("tip", e.getMessage());
        }
        mav.setView(view);
        return mav;
    }

    /**
     * reciveLog
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="deleteDocForHeadline",method={RequestMethod.POST,RequestMethod.GET})
    public ModelAndView deleteDocForHeadline(HttpServletRequest request,HttpServletResponse response,@RequestParam(value = "docid") String docid) throws Exception {
        ModelAndView mav = new ModelAndView();
        try {
            if(StringUtil.isEmpty(docid)){
                throw new ApplicationException("docid为空！");
            }
            String channel = "news";
            File file = new File(Constants.HTMLROOT + channel + "/special/header.json");
            String rs = "";
            try {
                BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
                String data = "";
                while((data = read.readLine())!=null){
                    rs+=data;
                }
                read.close();
            }catch (Exception e){
                logger.error(e.getMessage());
            }

            rs = rs.replace("smartheader(", "");
            rs = rs.substring(0,rs.length()-1);

            logger.info(rs);

            Map map = JsonUtil.fromJson(rs,Map.class);
            List<Map> headhotlist = (List<Map>) map.get("hotlist");
            List<Map> clist = new ArrayList<Map>();
            for(Map hot:headhotlist){
                String url = (String) hot.get("url");
                String hdocid = CommonUtil.get163DocidFromUrl(url);
                if(!docid.equals(hdocid)){
                    clist.add(hot);
                }
            }

            Map jsonmap = new HashMap();
            Map printmap = new HashMap();
//            jsonmap = new HashMap();
//            printmap = new HashMap();
            jsonmap.put("hotlist",clist);
            printmap.put("printstr", "smartheader("+JsonUtil.toJsonStr(jsonmap)+")");
            HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel + "/special/header.json", "print.vm", "GBK", printmap);

            mav.addObject("status", "success");
        } catch (ApplicationException e) {
            logger.error(e.getMessage());
            mav.addObject("status", "info");
            mav.addObject("tip", e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            mav.addObject("status", "error");
//            mav.addObject("tip", e.getMessage());
        }
        mav.setView(view);
        return mav;
    }

    @RequestMapping(value="deleteSpecial",method={RequestMethod.POST,RequestMethod.GET})
    public ModelAndView deleteSpecial(HttpServletRequest request,HttpServletResponse response,@RequestParam(value = "hotid") String hotid) throws Exception {
        ModelAndView mav = new ModelAndView();
        try {
            if(StringUtil.isEmpty(hotid)){
                throw new ApplicationException("hotid为空！");
            }
            String channel = "news";
            File file = new File(Constants.HTMLROOT + channel + "/special/specheader.json");
            String rs = "";
            try {
                BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
                String data = "";
                while((data = read.readLine())!=null){
                    rs+=data;
                }
                read.close();
            }catch (Exception e){
                logger.error(e.getMessage());
            }

            rs = rs.replace("specheader(", "");
            rs = rs.substring(0,rs.length()-1);

            logger.info(rs);

            Map map = JsonUtil.fromJson(rs,Map.class);
            List<Map> headhotlist = (List<Map>) map.get("hotlist");
            List<Map> clist = new ArrayList<Map>();
            for(Map hot:headhotlist){
                String hhotid = hot.get("specialid").toString();
                if(!hotid.equals(hhotid)){
                    clist.add(hot);
                }
            }
            Map para = new HashMap();
            para.put("autoid",Long.valueOf(hotid));
            specialService.signDelSomeByParameters(para);

            String path = Constants.HTMLROOT + channel + "/special/" + Integer.valueOf(hotid) / 1000 + "/" + hotid + "/";
            File dir = new File(path);
            String turnstr="<script>document.location.href='http://news.163.com/special/special.html'</script>";
            File[] files=dir.listFiles();
            if (files != null) {
                int length = files.length;
                for (int x = 0; x < length; x++) {
                    String temp =path+"/"+files[x].getName();
                    try {
                        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.createFile(temp)),"GBK"));
                        output.write(turnstr);
                        output.close();
                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }
                }
            }

            Map jsonmap = new HashMap();
            Map printmap = new HashMap();
//            jsonmap = new HashMap();
//            printmap = new HashMap();
            jsonmap.put("hotlist",clist);
            printmap.put("printstr", "specheader("+JsonUtil.toJsonStr(jsonmap)+")");
            HtmlBuildUtil.buildHTML(Constants.HTMLROOT + channel + "/special/specheader.json", "print.vm", "GBK", printmap);

            mav.addObject("status", "success");
        } catch (ApplicationException e) {
            logger.error(e.getMessage());
            mav.addObject("status", "info");
            mav.addObject("tip", e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            mav.addObject("status", "error");
//            mav.addObject("tip", e.getMessage());
        }
        mav.setView(view);
        return mav;
    }
}
