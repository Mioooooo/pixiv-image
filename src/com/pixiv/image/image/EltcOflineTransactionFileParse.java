//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.systech.process.tradesystem;

import com.systech.common.ReadNormalFile;
import com.systech.db.DBUtils;
import com.systech.framework.io.HexCodec;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.Logger;

public class EltcOflineTransactionFileParse implements JavaDelegate {
    private Logger logger = Logger.getLogger(OperSummary.class);

    public EltcOflineTransactionFileParse() {
    }

    public static void main(String[] args) {
        try {
            (new EltcOflineTransactionFileParse()).test();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void test() throws Exception {
        Object con = null;

        try {
            String[] strings = new String[]{"2020050449665234111000100012423001N"};
            int i = 0;

            while(!strings[i].equals("")) {
                this.updateTransactionDeatail("C:\\Users\\CA FE BA BE\\Desktop\\Online\\" + strings[i], "gb2312", strings[i++]);
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
            ((Connection)con).rollback();
        } finally {
            ((Connection)con).close();
        }

    }

    public synchronized void execute(DelegateExecution delegateExecution) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement psUnpdate = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConn();
            ps = con.prepareStatement("select FILENAME,FILETYPE,CLRMERID,OPERCODE,FILELENGTH,RECELOGLE,MD5CODE,FILEPATH,STATUS from FILERECLOG where STATUS='1' and  FILETYPE='RE'");
            rs = ps.executeQuery();
            String[] fileCode = new String[]{"gbk", "utf-8", "gbk2312"};
            psUnpdate = con.prepareStatement("update FILERECLOG set  STATUS='2' where   FILENAME=? and   FILETYPE='RE'");

            while(rs.next()) {
                String fileGetName = rs.getString("FILENAME");
                String fileGetPath = rs.getString("FILEPATH");
                this.logger.info("文件的名字：=====" + fileGetName);
                this.logger.info("文件的路径：=====" + fileGetPath);

                try {
                    this.updateTransactionDeatail(fileGetPath, fileCode[0], fileGetName);
                    psUnpdate.setString(1, fileGetName);
                    psUnpdate.executeUpdate();
                } catch (Exception var14) {
                    con.rollback();
                    var14.printStackTrace();
                }
            }

            delegateExecution.setVariable("success", true);
        } catch (Exception var15) {
            var15.printStackTrace();
            con.rollback();
            this.logger.error(var15);
            delegateExecution.setVariable("success", false);
        } finally {
            if (psUnpdate != null) {
                psUnpdate.close();
            }

            if (rs != null) {
                rs.close();
            }

            if (ps != null) {
                ps.close();
            }

            if (con != null) {
                con.close();
            }

        }

    }

    public void updateTransactionDeatail(String filePath, String fileCode, String fileName) throws Exception {
        Connection con = null;
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        PreparedStatement pst3 = null;
        PreparedStatement pst4 = null;
        PreparedStatement pst5 = null;
        PreparedStatement pst6 = null;
        PreparedStatement pst7 = null;
        FileInputStream fs = null;
        FileInputStream fs2 = null;
        InputStreamReader isr2 = null;
        BufferedReader bufferedReader2 = null;
        if (con == null) {
            try {
                fs = new FileInputStream(filePath);
                fs2 = new FileInputStream(filePath);
                isr2 = new InputStreamReader(fs2, fileCode);
                bufferedReader2 = new BufferedReader(isr2);
                String val = null;
                StringBuilder content = new StringBuilder();
                String totalNum = "0";
                int firstLine = 0;
                this.logger.info("第一行内容");
                int pos = 0;
                byte[] temp = new byte[30];
                fs.read(temp);
                byte[] target = new byte[4];
                System.arraycopy(temp, pos, target, 0, target.length);
                this.logger.info("版本号：" + new String(target, fileCode));
                int pos = pos + 4;
                target = new byte[8];
                System.arraycopy(temp, pos, target, 0, target.length);
                System.out.println(HexCodec.hexEncode(target));
                pos += 8;
                target = new byte[1];
                System.arraycopy(temp, pos, target, 0, target.length);
                this.logger.info("固定位数：" + new String(target, fileCode));
                ++pos;
                target = new byte[5];
                System.arraycopy(temp, pos, target, 0, target.length);
                totalNum = new String(target, fileCode);
                this.logger.info("总笔数：" + totalNum);
                pos += 5;
                target = new byte[12];
                System.arraycopy(temp, pos, target, 0, target.length);
                this.logger.info("总金额：" + new String(target, fileCode));

                for(val = null; (val = bufferedReader2.readLine()) != null; ++firstLine) {
                    if (firstLine != 0) {
                        content.append(val);
                    }
                }

                byte[] readLine = new byte[0];

                try {
                    if (content != null && !content.toString().isEmpty()) {
                        readLine = content.toString().getBytes(fileCode);
                    }
                } catch (Exception var70) {
                    var70.printStackTrace();
                }

                int p1 = 1;
                int p2 = 1;
                int p3 = 1;
                int i = 0;
                Long all = Long.valueOf(totalNum.trim());

                String totalFailNum;
                String totalSusAmt;
                String totalFailAmt;
                String firm2;
                for(int j = 0; (long)j < all; ++j) {
                    totalFailNum = ReadNormalFile.readByteArray(readLine, i, 21).trim();
                    i += 21;
                    totalSusAmt = ReadNormalFile.readByteArray(readLine, i, 12).trim();
                    totalSusAmt = "" + Long.valueOf(totalSusAmt);
                    i += 12;
                    String col3 = ReadNormalFile.readByteArray(readLine, i, 3).trim();
                    i += 3;
                    String col4 = ReadNormalFile.readByteArray(readLine, i, 10).trim();
                    i += 10;
                    String col5 = ReadNormalFile.readByteArray(readLine, i, 6).trim();
                    i += 6;
                    String col6 = ReadNormalFile.readByteArray(readLine, i, 12).trim();
                    i += 12;
                    String col7 = ReadNormalFile.readByteArray(readLine, i, 11).trim();
                    i += 11;
                    String col8 = ReadNormalFile.readByteArray(readLine, i, 11).trim();
                    i += 11;
                    String col9 = ReadNormalFile.readByteArray(readLine, i, 4).trim();
                    i += 4;
                    totalFailAmt = ReadNormalFile.readByteArray(readLine, i, 8).trim();
                    i += 8;
                    String col11 = ReadNormalFile.readByteArray(readLine, i, 15).trim();
                    i += 15;
                    String col12 = ReadNormalFile.readByteArray(readLine, i, 40).trim();
                    i += 40;
                    String col13 = ReadNormalFile.readByteArray(readLine, i, 3).trim();
                    StringBuilder var10000 = new StringBuilder("20");
                    i += 3;
                    firm2 = var10000.append(ReadNormalFile.readByteArray(readLine, i, 6).trim()).toString();
                    i += 6;
                    String col15 = ReadNormalFile.readByteArray(readLine, i, 4).trim();
                    i += 4;
                    String col16 = ReadNormalFile.readByteArray(readLine, i, 3).trim();
                    i += 3;
                    String col17 = ReadNormalFile.readByteArray(readLine, i, 3).trim();
                    i += 3;
                    String col18 = ReadNormalFile.readByteArray(readLine, i, 16).trim();
                    i += 16;
                    String col19 = ReadNormalFile.readByteArray(readLine, i, 64).trim();
                    i += 64;
                    String col20 = ReadNormalFile.readByteArray(readLine, i, 8).trim();
                    i += 8;
                    String col21 = ReadNormalFile.readByteArray(readLine, i, 4).trim();
                    if (!col21.equals("")) {
                        col21 = String.valueOf(Integer.valueOf(col21, 16));
                    } else {
                        col21 = "0";
                    }

                    i += 4;
                    String col22 = ReadNormalFile.readByteArray(readLine, i, 10).trim();
                    i += 10;
                    String col23 = ReadNormalFile.readByteArray(readLine, i, 4).trim();
                    i += 4;
                    String col24 = ReadNormalFile.readByteArray(readLine, i, 3).trim();
                    i += 3;
                    String col25 = ReadNormalFile.readByteArray(readLine, i, 6).trim();
                    i += 6;
                    String col26 = ReadNormalFile.readByteArray(readLine, i, 1).trim();
                    ++i;
                    String col27 = ReadNormalFile.readByteArray(readLine, i, 8).trim();
                    i += 8;
                    String col28 = ReadNormalFile.readByteArray(readLine, i, 6).trim();
                    i += 6;
                    String col29 = ReadNormalFile.readByteArray(readLine, i, 2).trim();
                    i += 2;
                    String col30 = ReadNormalFile.readByteArray(readLine, i, 6).trim();
                    i += 6;
                    String col31 = ReadNormalFile.readByteArray(readLine, i, 2).trim();
                    i += 2;
                    Object[] params = new Object[]{col27, col29, totalFailNum, totalSusAmt, totalFailAmt, firm2, col28};
                    Object[] paraNew = new Object[]{col27, col29, totalFailNum, col6};
                    Object[] para = new Object[]{totalFailNum, totalSusAmt, col3, col4, col5, col6, col7, col8, col9, totalFailAmt, col11, col12, col13, firm2, col15, col16, col17, col18, col19, col20, col21, col22, col23, col24, col25, col26, col27, col28, col29, col30, col31, fileName};
                    if (col31.equals("01")) {
                        pst1 = this.setParams(pst1, params);
                        pst1.addBatch();
                        pst4 = this.setParams1(pst4, para);
                        pst4.addBatch();
                        if (p1 % 1000 == 0) {
                            pst1.executeBatch();
                            pst1.clearBatch();
                            pst4.executeBatch();
                            pst4.clearBatch();
                        }

                        ++p1;
                    } else if (col31.equals("02")) {
                        pst2 = this.setParamNew(pst2, paraNew);
                        pst2.addBatch();
                        pst4 = this.setParams1(pst4, para);
                        pst4.addBatch();
                        if (p2 % 1000 == 0) {
                            pst2.executeBatch();
                            pst2.clearBatch();
                            pst4.executeBatch();
                            pst4.clearBatch();
                        }

                        ++p2;
                    } else if (col31.equals("03")) {
                        pst3 = this.setParamNew(pst3, paraNew);
                        pst3.addBatch();
                        pst4 = this.setParams1(pst4, para);
                        pst4.addBatch();
                        if (p3 % 1000 == 0) {
                            pst3.executeBatch();
                            pst3.clearBatch();
                            pst4.executeBatch();
                            pst4.clearBatch();
                        }

                        ++p3;
                    } else if (col31.equals("05")) {
                        for(int z = 0; z <= paraNew.length; ++z) {
                            this.logger.info(paraNew[z]);
                        }
                    } else {
                        this.logger.info(col31);
                        this.logger.info("04或者其他暂不处理");
                    }
                }

                String totalSusNum = ReadNormalFile.readByteArray(readLine, i, 5);
                i += 5;
                totalFailNum = ReadNormalFile.readByteArray(readLine, i, 5);
                i += 5;
                totalSusAmt = ReadNormalFile.readByteArray(readLine, i, 12);
                i += 12;
                totalFailAmt = ReadNormalFile.readByteArray(readLine, i, 12);
                i += 12;
                firm2 = ReadNormalFile.readByteArray(readLine, i, 1);
                this.logger.info("最后一行----\n位成功明细条数:" + totalSusNum + "\n位失败明细条数:" + totalFailNum + "\n位成功明细总金额:" + totalSusAmt + "\n位失败明细总金额:" + totalFailAmt + "\n位固定值3:" + firm2);
                pst1.executeBatch();
                pst2.executeBatch();
                pst3.executeBatch();
                pst4.executeBatch();
                ((PreparedStatement)pst5).executeBatch();
                ((PreparedStatement)pst6).executeBatch();
                ((PreparedStatement)pst7).executeBatch();
            } catch (Exception var71) {
                ((Connection)con).rollback();
                var71.printStackTrace();
                throw var71;
            } finally {
                if (con != null) {
                    ((Connection)con).close();
                }

                if (pst1 != null) {
                    pst1.close();
                }

                if (pst2 != null) {
                    pst2.close();
                }

                if (pst3 != null) {
                    pst3.close();
                }

                if (pst4 != null) {
                    pst4.close();
                }

                if (pst5 != null) {
                    ((PreparedStatement)pst5).close();
                }

                if (pst6 != null) {
                    ((PreparedStatement)pst6).close();
                }

                if (pst7 != null) {
                    ((PreparedStatement)pst7).close();
                }

                bufferedReader2.close();
                isr2.close();
                fs.close();
                fs2.close();
            }
        }

    }

    public PreparedStatement setParams(PreparedStatement pst, Object[] params) throws SQLException {
        pst.setString(1, (String)params[0]);
        pst.setString(2, (String)params[1]);
        pst.setString(3, (String)params[2]);
        pst.setString(4, (String)params[3]);
        pst.setString(5, (String)params[5]);
        pst.setString(6, (String)params[6]);
        return pst;
    }

    public PreparedStatement setParams1(PreparedStatement pst, Object[] params) throws SQLException {
        pst.setString(1, (String)params[0]);
        pst.setString(2, (String)params[1]);
        pst.setString(3, (String)params[2]);
        pst.setString(4, (String)params[3]);
        pst.setString(5, (String)params[4]);
        pst.setString(6, (String)params[5]);
        pst.setString(7, (String)params[6]);
        pst.setString(8, (String)params[7]);
        pst.setString(9, (String)params[8]);
        pst.setString(10, (String)params[9]);
        pst.setString(11, (String)params[10]);
        pst.setString(12, (String)params[11]);
        pst.setString(13, (String)params[12]);
        pst.setString(14, (String)params[13]);
        pst.setString(15, (String)params[14]);
        pst.setString(16, (String)params[15]);
        pst.setString(17, (String)params[16]);
        pst.setString(18, (String)params[17]);
        pst.setString(19, (String)params[18]);
        pst.setString(20, (String)params[19]);
        pst.setString(21, (String)params[20]);
        pst.setString(22, (String)params[21]);
        pst.setString(23, (String)params[22]);
        pst.setString(24, (String)params[23]);
        pst.setString(25, (String)params[24]);
        pst.setString(26, (String)params[25]);
        pst.setString(27, (String)params[26]);
        pst.setString(28, (String)params[27]);
        pst.setString(29, (String)params[28]);
        pst.setString(30, (String)params[29]);
        pst.setString(31, (String)params[30]);
        pst.setString(32, (String)params[31]);
        return pst;
    }

    public PreparedStatement setParamNew(PreparedStatement pst, Object[] params) throws SQLException {
        pst.setString(1, (String)params[0]);
        pst.setString(2, (String)params[1]);
        pst.setString(3, (String)params[2]);
        pst.setString(4, (String)params[3]);
        return pst;
    }
}
