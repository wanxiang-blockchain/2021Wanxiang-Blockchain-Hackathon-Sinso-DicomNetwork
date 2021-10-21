package cn.sinso.DICOMNetwork.quartz;


import cn.sinso.DICOMNetwork.model.MailClient;
import cn.sinso.DICOMNetwork.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version V4.0
 * @Title: SynchroQuartz
 * @Company: 成都影达科技有限公司
 * @Description: 描述
 * @author: 周聪
 * @date 2019/3/27 15:17
 */

@Component
@EnableAsync
@Service
public class SynchroQuartzMain implements SchedulingConfigurer {

    @Value("${tokenExpireTime}")
    public Integer tokenExpireTime;

    @Autowired
    MailClient mailClient;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dfbegin = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    private SimpleDateFormat dfend = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> {
            //定时扫描选择目录，移动或者复制到临时处理目录（按照文件最后修改时候来过滤）
            init();
        }, (triggerContext) -> {
            // 定时任务触发，可修改定时任务的执行周期
            CronTrigger trigger = new CronTrigger("*/10 * * * * ?");
            Date nextExecDate = trigger.nextExecutionTime(triggerContext);
            return nextExecDate;
        });




    }
//   每10s执行一次扫描
//    @Scheduled(cron = "*/10 * * * * ?")
    public void init(){
        try {
            EmailUtil.getEmail(mailClient, tokenExpireTime);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

    }



}
