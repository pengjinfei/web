package com.pengjinfei.common.quartz;

import org.quartz.*;
import org.quartz.impl.jdbcjobstore.Util;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.quartz.impl.jdbcjobstore.oracle.OracleDelegate.SELECT_ORACLE_JOB_DETAIL_BLOB;
import static org.quartz.impl.jdbcjobstore.oracle.OracleDelegate.UPDATE_ORACLE_JOB_DETAIL_BLOB;

/**
 * Created by Pengjinfei on 16/7/28.
 * Description:
 */
public class LocalConcurrentQuartzJobBean extends LocalQuartzJobBean {

    private int maxConcurrent = 0;
    private AtomicInteger concurrent = new AtomicInteger(0);
    private boolean jdbcStore = false;
    private String dataSourceBeanName;
    private String tablePrefix;
    private String schedulerName;

    public String getDataSourceBeanName() {
        return dataSourceBeanName;
    }

    public void setDataSourceBeanName(String dataSourceBeanName) {
        this.dataSourceBeanName = dataSourceBeanName;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public int getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public AtomicInteger getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(AtomicInteger concurrent) {
        this.concurrent = concurrent;
    }

    public boolean isJdbcStore() {
        return jdbcStore;
    }

    public void setJdbcStore(boolean jdbcStore) {
        this.jdbcStore = jdbcStore;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.debug("Job ["+getJobName()+"] execute in concurrent mode.");
        if (maxConcurrent != Integer.MAX_VALUE) {
            int after = concurrent.incrementAndGet();
            if (after > maxConcurrent) {
                logger.info("Concurrence reaches max value:" + maxConcurrent + ", job:" + getJobName() + " quit.");
                concurrent.decrementAndGet();
                return;
            }
            updateJobDataMap(context);
        }
        try {
            super.executeInternal(context);
        } finally {
            if (maxConcurrent != Integer.MAX_VALUE) {
                concurrent.decrementAndGet();
                updateJobDataMap(context);
            }
        }
    }

    private void updateJobDataMap(JobExecutionContext context) throws JobExecutionException {
        if (!jdbcStore) {
            return;
        }
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            JobDetail jobDetail = context.getJobDetail();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (null != jobDataMap) {
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(jobDataMap);
                out.flush();
            }
            byte[] data = baos.toByteArray();

            DataSource dataSource = (DataSource) getBean(context, dataSourceBeanName);
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(Util.rtp(SELECT_ORACLE_JOB_DETAIL_BLOB, tablePrefix, "'"+schedulerName+"'"));
            ps.setString(1, jobDetail.getKey().getName());
            ps.setString(2, jobDetail.getKey().getGroup());

            rs = ps.executeQuery();

            int res = 0;

            if (rs.next()) {
                Blob dbBlob = rs.getBlob(1);
                if (dbBlob == null) {
                    throw new SQLException("Driver's Blob representation is null!");
                }

                if (dbBlob instanceof oracle.sql.BLOB) { // is it an oracle blob?
                    ((oracle.sql.BLOB) dbBlob).setBytes(1, data);
                    ((oracle.sql.BLOB) dbBlob).truncate(data.length);
                } else {
                    throw new SQLException(
                            "Driver's Blob representation is of an unsupported type: "
                                    + dbBlob.getClass().getName());
                }
                ps2 = conn.prepareStatement(Util.rtp(UPDATE_ORACLE_JOB_DETAIL_BLOB, tablePrefix, "'"+schedulerName+"'"));

                ps2.setBlob(1, dbBlob);
                ps2.setString(2, jobDetail.getKey().getName());
                ps2.setString(3, jobDetail.getKey().getGroup());

                res = ps2.executeUpdate();
            }
            if (res == 1) {
                conn.commit();
            } else {
                conn.rollback();
                throw new Exception("Update jobDataMap failed.");
            }

        } catch (Exception e) {
            throw new JobExecutionException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (ps2 != null) {
                try {
                    ps2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
