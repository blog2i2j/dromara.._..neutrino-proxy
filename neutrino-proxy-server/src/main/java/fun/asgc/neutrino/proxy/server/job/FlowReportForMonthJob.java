package fun.asgc.neutrino.proxy.server.job;

import cn.hutool.core.collection.CollectionUtil;
import fun.asgc.neutrino.proxy.core.util.DateUtil;
import fun.asgc.neutrino.proxy.server.dal.*;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportDayDO;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportMonthDO;
import fun.asgc.neutrino.proxy.server.service.FlowReportService;
import fun.asgc.solon.extend.job.IJobHandler;
import fun.asgc.solon.extend.job.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;

/**
 * @author: aoshiguchen
 * @date: 2022/10/28
 */
@Slf4j
@Component
@JobHandler(name = "FlowReportForMonthJob", cron = "0 10 0 1 * ?", param = "")
public class FlowReportForMonthJob implements IJobHandler {
    @Inject
    private FlowReportService flowReportService;
    @Inject
    private LicenseMapper licenseMapper;
    @Inject
    private FlowReportMinuteMapper flowReportMinuteMapper;
    @Inject
    private FlowReportHourMapper flowReportHourMapper;
    @Inject
    private FlowReportDayMapper flowReportDayMapper;
    @Inject
    private FlowReportMonthMapper flowReportMonthMapper;

    @Override
    public void execute(String param) throws Exception {
        Date now = new Date();
        String dateStr = DateUtil.format(DateUtil.addDate(now, Calendar.MONTH, -1), "yyyy-MM");
        Date date = DateUtil.parse(dateStr, "yyyy-MM");
        Date startDayDate = DateUtil.getMonthBegin(date);
        Date endEndDate = DateUtil.getMonthEnd(date);

        // 删除原来的记录
        flowReportMonthMapper.deleteByDateStr(dateStr);

        // 查询上个月的天级别统计数据
        List<FlowReportDayDO> flowReportDayList = flowReportDayMapper.findListByDateRange(startDayDate, endEndDate);
        if (CollectionUtil.isEmpty(flowReportDayList)) {
            return;
        }

        // 汇总前一个天的天级别统计数据
        Map<Integer, FlowReportMonthDO> map = new HashMap<>();
        for (FlowReportDayDO item : flowReportDayList) {
            FlowReportMonthDO report = map.get(item.getLicenseId());
            if (null == report) {
                report = new FlowReportMonthDO();
                map.put(item.getLicenseId(), report);
            }
            Long writeBytes = report.getWriteBytes() == null ? 0 : report.getWriteBytes();
            Long readBytes = report.getReadBytes() == null ? 0 : report.getReadBytes();

            report.setUserId(item.getUserId());
            report.setLicenseId(item.getLicenseId());
            report.setWriteBytes(writeBytes + item.getWriteBytes());
            report.setReadBytes(readBytes + item.getReadBytes());
            report.setDate(date);
            report.setDateStr(dateStr);
            report.setCreateTime(now);
        }

        for (FlowReportMonthDO item : map.values()) {
            flowReportMonthMapper.insert(item);
        }
    }

}
