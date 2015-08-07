package com.sr178.game.server.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.server.config.LocalTools;




public class MonitorService{
    private  MetricRegistry metrics;
    private  boolean isMonitor;
    private static MonitorService monitorService = new MonitorService();
    /**
     * 在日志上打印输出
     */
    private static Slf4jReporter reporter ;
    /**
     * 收到用户消息的带宽
     */
    private Meter incomingUserMessageBandwidth;

    /**
     * 发送用户消息的带宽
     */
    private Meter outgoingUserMessageBandwidth;
    /**
     * 收到服务器间消息的带宽
     */
    private Meter incomingServerMessageBandwidth;

    /**
     * 发送服务器间消息的带宽
     */
    private Meter outgoingServerMessageBandwidth;
    /**
     * 请求用户并发数统计
     */
    private Meter requestUserMsgSpeed;
    /**
     * 请求服务器并发统计
     */
    private Meter requestServerMsgSpeed;
    /**
     * 每个action处理消息的速度
     */
    private Timer actionExecutorProcessTime;
    
    
    public static  MonitorService getInstance(){
    	return monitorService;
    }
    
    private MonitorService(){
    	
    }

    public boolean isMonitor(){
        return isMonitor;
    }

    public MetricRegistry getRegistry(){
        return metrics;
    }
    
    public void initMonitor(){
    	 metrics = new MetricRegistry();
    	 reporter = Slf4jReporter.forRegistry(metrics).build();
         this.isMonitor =  LocalTools.getLocalConfig().isMonitor();
         if (isMonitor){
             final MemoryMXBean mxBean = ManagementFactory.getMemoryMXBean();
             getRegistry().register("perf.heap_used", new Gauge<Long>(){
                 @Override
                 public Long getValue(){
                     return mxBean.getHeapMemoryUsage().getUsed();
                 }
             });
             getRegistry().register("perf.heap_max", new Gauge<Long>(){
                 @Override
                 public Long getValue(){
                     return mxBean.getHeapMemoryUsage().getMax();
                 }
             });
             final OperatingSystemMXBean sysMxBean = ManagementFactory
                     .getOperatingSystemMXBean();
             getRegistry().register("perf.load_avg", new Gauge<Double>(){
                 @Override
                 public Double getValue(){
                     return sysMxBean.getSystemLoadAverage();
                 }
             });

             this.incomingUserMessageBandwidth = monitorService.getRegistry().meter(
                     "msg.user.recv_rate(byte)");
             this.outgoingUserMessageBandwidth = monitorService.getRegistry().meter(
                     "msg.user.send_rate(byte)");
             this.incomingServerMessageBandwidth = monitorService.getRegistry().meter(
                     "msg.server.recv_rate(byte)");
             this.outgoingServerMessageBandwidth = monitorService.getRegistry().meter(
                     "msg.server.send_rate(byte)");
             
             this.requestUserMsgSpeed =  monitorService.getRegistry().meter(
                     "request.user.msg.rate");
             this.requestServerMsgSpeed =  monitorService.getRegistry().meter(
                     "request.server.msg.rate");
             this.actionExecutorProcessTime = monitorService.getRegistry()
                     .timer("msg.action_executor_process_time");
             reporter.start(10, TimeUnit.SECONDS);
             LogSystem.info("开启监控~~~~~~~");
         }
    }
    public void markUserIncomingBandwidth(int b){
        if (incomingUserMessageBandwidth != null){
            this.incomingUserMessageBandwidth.mark(b);
        }
    }
    public void markUserOutcomingBandwidth(int b){
        if (outgoingUserMessageBandwidth != null){
            this.outgoingUserMessageBandwidth.mark(b);
        }
    }
    public void markServerIncomingBandwidth(int b){
        if (incomingServerMessageBandwidth != null){
            this.incomingServerMessageBandwidth.mark(b);
        }
    }
    public void markServerOutcomingBandwidth(int b){
        if (outgoingServerMessageBandwidth != null){
            this.outgoingServerMessageBandwidth.mark(b);
        }
    }
    public Timer.Context actionExecutorProcessTimer(){
        if (actionExecutorProcessTime != null){
            return actionExecutorProcessTime.time();
        } else{
            return null;
        }
    }
    public void markOneUserRequest(){
        if (requestUserMsgSpeed != null){
        	requestUserMsgSpeed.mark();
        } 
    }
    public void markOneServerRequest(){
        if (requestServerMsgSpeed != null){
        	requestServerMsgSpeed.mark();
        } 
    }
}
