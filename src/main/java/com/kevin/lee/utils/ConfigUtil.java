package com.kevin.lee.utils;

import com.alibaba.fastjson2.JSON;
import com.formdev.flatlaf.json.Json;
import com.kevin.lee.enums.CountryWebEnum;

/**
 * Configuration management
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/08.
 */
public class ConfigUtil extends ConfigBaseUtil {

    private static final ConfigUtil configUtil = new ConfigUtil();

    public static ConfigUtil getInstance() {
        return configUtil;
    }

    private ConfigUtil() {
        super();
    }

    private boolean autoCheckUpdate;

    private boolean defaultMaxWindow;

    private boolean unifiedBackground;

    private String beforeVersion;

    private String theme;

    private String font;

    private int fontSize;


    /**
     * 当前选中的站点
     */
    private String countryWeb;

    /**
     * 配置请求接口cookie信息数据
     * @return
     */
    private String cookies;

    /**
     *  配置亚马逊产品excel表格存放地址
     * @return
     */
    private String inputDir;

    /**
     * 接口请求间隔时间 默认：500毫秒
     */
    private Integer sleepTime;

    /**
     * 文件间的请求调用间隔时间(毫秒): 默认：500毫秒
     */
    private Integer fileTimeout;

    /**
     * 超时后再次发起请求间隔时间(毫秒): 默认：500毫秒
     */
    private Integer timeout;

    /**
     * 搜索key
     */
    private String searchKeys;

    /**
     * 搜索保存目录
     */
    private String searchDir;

    /**
     * KeepaAPI key 配置
     */
    private String keePaKey;

    public boolean isAutoCheckUpdate() {
        return setting.getBool("autoCheckUpdate", "setting.common", true);
    }

    public void setAutoCheckUpdate(boolean autoCheckUpdate) {
        setting.putByGroup("autoCheckUpdate", "setting.common", String.valueOf(autoCheckUpdate));
    }

    public boolean isDefaultMaxWindow() {
        return setting.getBool("defaultMaxWindow", "setting.normal", false);
    }

    public void setDefaultMaxWindow(boolean defaultMaxWindow) {
        setting.putByGroup("defaultMaxWindow", "setting.normal", String.valueOf(defaultMaxWindow));
    }

    public boolean isUnifiedBackground() {
        return setting.getBool("unifiedBackground", "setting.normal", true);
    }

    public void setUnifiedBackground(boolean unifiedBackground) {
        setting.putByGroup("unifiedBackground", "setting.normal", String.valueOf(unifiedBackground));
    }

    public String getBeforeVersion() {
        return setting.getStr("beforeVersion", "setting.common", "0.0.0");
    }

    public void setBeforeVersion(String beforeVersion) {
        setting.putByGroup("beforeVersion", "setting.common", beforeVersion);
    }

    public String getTheme() {
        return setting.getStr("theme", "setting.appearance", "Dark purple");
    }

    public void setTheme(String theme) {
        setting.putByGroup("theme", "setting.appearance", theme);
    }

    public String getFont() {
        if (SystemUtil.isLinuxOs()) {
            return setting.getStr("font", "setting.appearance", "Noto Sans CJK HK");
        } else {
            return setting.getStr("font", "setting.appearance", "Microsoft YaHei");
        }
    }

    public void setFont(String font) {
        setting.putByGroup("font", "setting.appearance", font);
    }

    public int getFontSize() {
        return setting.getInt("fontSize", "setting.appearance", 13);
    }

    public void setFontSize(int fontSize) {
        setting.putByGroup("fontSize", "setting.appearance", String.valueOf(fontSize));
    }

    public String getCountryWebJson() {
        return setting.getStr("countryWebJson", "setting.common", "");
    }

    public void setCountryWebJson(String json) {
        setting.putByGroup("countryWebJson", "setting.common", json);
    }

    public String getCountryWeb() {
        return setting.getStr("countryWeb", "setting.common", "US");
    }

    public void setCountryWeb(String countryWeb) {
        setting.putByGroup("countryWeb", "setting.common", countryWeb);
    }


    public String getCookies(String countryWeb) {
        return setting.getStr("cookies", "setting.common."+countryWeb, "");
    }

    public void setCookies(String cookies,String countryWeb) {
        setting.putByGroup("cookies", "setting.common."+countryWeb, cookies);
    }

    public String getInputDir(String countryWeb) {
        return setting.getStr("inputDir", "setting.common."+countryWeb, "");
    }

    public void setInputDir(String inputDir,String countryWeb) {
        setting.putByGroup("inputDir", "setting.common."+countryWeb, inputDir);
    }

    public int getSleepTime(String countryWeb) {
        return setting.getInt("sleepTime", "setting.common."+countryWeb, 500);
    }

    public void setSleepTime(int sleepTime,String countryWeb) {
        setting.putByGroup("sleepTime", "setting.common."+countryWeb, String.valueOf(sleepTime));
    }

    public int getFileTimeout(String countryWeb) {
        return setting.getInt("fileTimeout", "setting.common."+countryWeb, 500);
    }

    public void setFileTimeout(int fileTimeout,String countryWeb) {
        setting.putByGroup("fileTimeout", "setting.common."+countryWeb, String.valueOf(fileTimeout));
    }

    public int getTimeout(String countryWeb) {
        return setting.getInt("timeout", "setting.common."+countryWeb, 500);
    }

    public void setTimeout(int timeout,String countryWeb) {
        setting.putByGroup("timeout", "setting.common."+countryWeb, String.valueOf(timeout));
    }

    public String getSearchKeys(String countryWeb) {
        return setting.getStr("searchKeys", "setting.common."+countryWeb, "");
    }

    public void setSearchKeys(String searchKeys,String countryWeb) {
        setting.putByGroup("searchKeys", "setting.common."+countryWeb, searchKeys);
    }

    public String getSearchDir(String countryWeb) {
        return setting.getStr("searchDir", "setting.common."+countryWeb, "");
    }

    public void setSearchDir(String searchDir,String countryWeb) {
        setting.putByGroup("searchDir", "setting.common."+countryWeb, searchDir);
    }

    public String getKeePaKey(String countryWeb) {
        return setting.getStr("keePaKey", "setting.common."+countryWeb, "");
    }

    public void setKeePaKey(String keePaKey,String countryWeb) {
        setting.putByGroup("keePaKey", "setting.common."+countryWeb, keePaKey);
    }

}
