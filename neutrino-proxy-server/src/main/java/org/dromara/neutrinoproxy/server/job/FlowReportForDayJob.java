package org.dromara.neutrinoproxy.server.job;

import cn.hutool.core.collection.CollectionUtil;
import org.dromara.neutrinoproxy.core.util.DateUtil;
import org.dromara.neutrinoproxy.server.dal.FlowReportDayMapper;
import org.dromara.neutrinoproxy.server.dal.FlowReportHourMapper;
import org.dromara.neutrinoproxy.server.dal.FlowReportMinuteMapper;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.entity.FlowReportDayDO;
import org.dromara.neutrinoproxy.server.dal.entity.FlowReportHourDO;
import org.dromara.neutrinoproxy.server.service.FlowReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.solonplugins.job.IJobHandler;
import org.dromara.solonplugins.job.annotation.JobHandler;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;

/**
 * @author: aoshiguchen
 * @date: 2022/10/28
 */
@Slf4j
@Component
@JobHandler(name = "FlowReportForDayJob", cron = "0 0 1 * * ?", param = "")
public class FlowReportForDayJob implements IJobHandler {
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

    @Override
    public void execute(String param) throws Exception {
        Date now = new Date();
        String dateStr = getDateStr(now, param); // DateUtil.format(DateUtil.addDate(now, Calendar.DATE, -1), "yyyy-MM-dd");
        Date date = DateUtil.parse(dateStr, "yyyy-MM-dd");
        Date startHourDate = DateUtil.getDayBegin(date);
        Date endHourDate = DateUtil.getDayEnd(date);

        // 删除原来的记录
        flowReportDayMapper.deleteByDateStr(dateStr);

        // 查询前一天的小时级别统计数据
        List<FlowReportHourDO> flowReportHourDOList = flowReportHourMapper.findListByDateRange(startHourDate, endHourDate);
        if (CollectionUtil.isEmpty(flowReportHourDOList)) {
            return;
        }

        // 汇总前一个天的天级别统计数据
        Map<Integer, FlowReportDayDO> map = new HashMap<>();
        for (FlowReportHourDO item : flowReportHourDOList) {
            FlowReportDayDO report = map.get(item.getLicenseId());
            if (null == report) {
                report = new FlowReportDayDO();
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

        for (FlowReportDayDO item : map.values()) {
            flowReportDayMapper.insert(item);
        }
    }

    private String getDateStr(Date now, String params) {
        if (StringUtils.isNotBlank(params)) {
            try {
                // 参数格式错误则取当前时间
                DateUtil.parse(params, "yyyy-MM-dd");
                return params;
            } catch (Exception e) {
                // ignore
            }
        }
        return DateUtil.format(DateUtil.addDate(now, Calendar.DATE, -1), "yyyy-MM-dd");
    }
}
