package com.netease.gather.common.context;

import com.netease.gather.common.constants.Facade;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ScheduleContext {
    public static Facade FACADE = null;
    public static BeanFactory BF = null;

    static {
        if (BF == null) {
            BF = new ClassPathXmlApplicationContext("applicationContext.xml");
            if (FACADE == null)
                FACADE = (Facade) BF.getBean("facade");
        }
    }
}
