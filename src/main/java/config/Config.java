package config;

import java.io.*;
import java.util.Properties;

public class Config {

    public static final String CONFIG_FILE = "CloneGroupIdFinder.properties";

    //数据库配置
    public static String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static String URL = "";
    public static String USERNAME = "root";
    public static String PASSWORD = "root";
    public static String TABLE = "";

    //程序输入文件配置
    public static String measureIndexPath = "";
    public static String type123GroupPath = "";
    public static String commitBlobPath = "";
    public static String projectPath = "";

    //配置对象
    private static Properties properties = new Properties();
    /**
     * 保存配置
     */
    private static void save() throws IOException {
        properties.setProperty("db.driver", DRIVER);
        properties.setProperty("db.url", URL);
        properties.setProperty("db.username", USERNAME);
        properties.setProperty("db.password", PASSWORD);
        properties.setProperty("db.table", TABLE);
        properties.setProperty("measure_index_path", measureIndexPath);
        properties.setProperty("type123_group_path", type123GroupPath);
        properties.setProperty("commit_blob_path", commitBlobPath);
        properties.setProperty("project_path", projectPath);
        properties.store(new FileWriter(CONFIG_FILE), "");
    }

    /**
     * 加载配置
     * @throws IOException
     */
    public static void load() throws IOException{
        if (!new File(CONFIG_FILE).exists()){
            save();
            System.out.printf("please update %s\n", CONFIG_FILE);
            System.exit(0);
        }
        properties.load(new FileReader(CONFIG_FILE));
        DRIVER = properties.getProperty("db.driver");
        URL = properties.getProperty("db.url");
        USERNAME = properties.getProperty("db.username");
        PASSWORD = properties.getProperty("db.password");
        TABLE = properties.getProperty("db.table");
        measureIndexPath = properties.getProperty("measure_index_path");
        type123GroupPath = properties.getProperty("type123_group_path");
        commitBlobPath = properties.getProperty("commit_blob_path");
        projectPath = properties.getProperty("project_path");
    }
}
