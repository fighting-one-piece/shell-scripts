package com.netease.gather.web.controller.front;

import com.netease.gather.common.util.exception.ApplicationException;
import com.netease.gather.service.logic.GenSpecialService;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.net.URLDecoder;

@Controller
@RequestMapping("/specialApi")
public class SpecialApiController {

    @Resource(name = "mappingJacksonJsonView")
    private View view;

    @Resource(name = "genSpecialService")
    private GenSpecialService genSpecialService;

    private static final Logger logger = LoggerFactory.getLogger(SpecialApiController.class);

    /**
     * reciveLog
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="gotoModHL",method={RequestMethod.POST,RequestMethod.GET})
    public ModelAndView getTimeSec(HttpServletRequest request,HttpServletResponse response,
                                 @RequestParam(value = "channelid") String channelid,
                                 @RequestParam(value = "signature") String signature,
                                 @RequestParam(value = "specurl") String specurl) throws Exception {
        ModelAndView mav = new ModelAndView();
        try {
            mav.addObject("channelid", channelid);
            specurl = URLDecoder.decode(specurl,"UTF-8");
            mav.addObject("specurl", specurl);
            String codestr = "smart"+specurl;
            String genstr = DigestUtils.sha1Hex(codestr);
            if(!genstr.equals(signature)){
                throw new ApplicationException("请求不合法！");
            }
            mav.setViewName("/special/api/modHeaderline");
        }catch (ApplicationException e) {
            logger.error(e.getMessage());
            mav.addObject("status", "info");
            mav.addObject("tip", e.getMessage());
            mav.setViewName("/special/api/message");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            mav.addObject("status", "error");
            mav.addObject("tip", e.getMessage());
            mav.setViewName("/special/api/message");
        }

        return mav;
    }

    @RequestMapping(value="modHeaderline",method={RequestMethod.POST,RequestMethod.GET})
    public ModelAndView modHeaderline(HttpServletRequest request,HttpServletResponse response,
                                 @RequestParam(value = "channelid") String channelid,
                                 @RequestParam(value = "docurl") String docurl,
//                                 @RequestParam(value = "signature") String signature,
                                 @RequestParam(value = "specurl") String specurl) throws Exception {
        ModelAndView mav = new ModelAndView();
        try {
            genSpecialService.changeHeaderline(docurl,specurl,channelid);
            mav.addObject("status", "success");
            mav.addObject("tip", "修改成功！");
        } catch (ApplicationException e) {
            logger.error(e.getMessage());
            mav.addObject("status", "info");
            mav.addObject("tip", e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            mav.addObject("status", "error");
//            mav.addObject("tip", e.getMessage());
        }
//        mav.setView(view);
        mav.setViewName("/special/api/message");
        return mav;
    }
}
