package com.xiaozhang.lf.config;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;

import com.xiaozhang.lf.LfUtil;
import com.xiaozhang.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author : xiaozhang
 * @since : 2023/9/6 17:10
 */

@Slf4j
public class LfCopyConfig extends LfUtil {

    @Test
    public void copy670() throws Exception {
        LfCopyConfigContext copyConfigContext = LfCopyConfigContext.of(670, null, null);
        copyConfigs(Collections.singletonList(copyConfigContext));
    }

    @Test
    public void copySkinShop() throws Exception {
        LfCopyConfigContext copyConfigContext = LfCopyConfigContext.of(671, "IP_Center2", null);
        copyConfigs(Collections.singletonList(copyConfigContext));
    }

    @Test
    public void copyMiniGameCenter() throws Exception {
        List<String> skipXml = Arrays.asList("activity_panel.xml");
        LfCopyConfigContext copyConfigContext1 = LfCopyConfigContext.of(669, "小游戏大厅配置", skipXml);
        LfCopyConfigContext copyConfigContext2 = LfCopyConfigContext.of(670, "小游戏大厅配置", skipXml);

        List<LfCopyConfigContext> copyConfigContexts = Arrays.asList(copyConfigContext1, copyConfigContext2);
        copyConfigs(copyConfigContexts);
    }

    public void copyConfigs(List<LfCopyConfigContext> copyConfigContexts) throws Exception {
        for (LfCopyConfigContext copyConfigContext : copyConfigContexts) {
            String svnPath = copyConfigContext.calcSvnPath();
            File configFolder = new File(svnPath);
            if (!configFolder.exists() || !configFolder.isDirectory()) {
                log.error("configFolder path error :{}", svnPath);
                return;
            }

            Iterator<File> iterateFiles = FileUtils.iterateFiles(configFolder, new String[] {"xml"}, false);
            List<File> xmlFiles = new ArrayList<>();
            iterateFiles.forEachRemaining(xmlFiles::add);
            Collections.sort(xmlFiles);
            log.info("{}", xmlFiles.stream().map(File::getName).collect(Collectors.toList()));

            int serverId = copyConfigContext.getServerId();
            Set<String> skipFiles = copyConfigContext.getSkipFiles();
            Pair<String, String> resourcePath = LfUtil.getWorkSpaceResourcePath(serverId);
            log.info("即将copy配置到目录:{}", JsonUtil.object2String(resourcePath));
            for (File xmlFile : xmlFiles) {
                if (skipFiles.contains(xmlFile.getName())) {
                    log.info("跳过文件:{}", xmlFile.getName());
                    continue;
                }
                // 删除xmlFile文件
                String resourcePathFirst = resourcePath.getFirst();
                File desFile = new File(resourcePathFirst + xmlFile.getName());
                FileUtils.copyFile(xmlFile, desFile);
                // 删除desFile文件

                log.info("拷贝完成 :{}", desFile.getName());
            }
        }

    }

}