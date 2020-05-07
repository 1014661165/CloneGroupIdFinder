import bean.CloneGroup;
import bean.CommitBlob;
import bean.Measure;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.Config;
import util.DruidUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Main {

    static Map<Integer, Measure> measures;
    static List<CloneGroup> groups;
    static Map<String, String> commitBlobs;

    static Set<Integer> groupIdSet;
    static Map<Integer, Set<Integer>> groupIdMap;

    public static void main(String[] args) {
        try {
            System.out.println("load config...");
            Config.load();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            System.out.println("init data source...");
            DruidUtil.initDataSource();
            init();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("start to process...");
        process();

        try {
            System.out.println("outputting...");
            output();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("finish!");
    }

    /**
     * 初始化数据
     */
    static void init() throws IOException{
        BufferedReader reader = null;
        String line = null;

        //初始化measures
        measures = new HashMap<>();
        reader = new BufferedReader(new FileReader(Config.measureIndexPath));
        while ((line = reader.readLine()) != null){
            String[] tmp = line.split(",");
            int id = Integer.parseInt(tmp[0]);
            String path = tmp[1];
            String realPath = path.substring(Config.projectPath.length());
            int startLine = Integer.parseInt(tmp[2]);
            int endLine = Integer.parseInt(tmp[3]);
            Measure measure = new Measure(id, realPath, startLine, endLine);
            measures.put(id, measure);
        }
        reader.close();

        //初始化groups
        groups = new ArrayList<>();
        reader = new BufferedReader(new FileReader(Config.type123GroupPath));
        int cnt = 0;
        while ((line = reader.readLine()) != null){
            String[] tmp = line.split(",");
            int[] measureIds = Arrays.stream(tmp).mapToInt(Integer::parseInt).toArray();
            CloneGroup group = new CloneGroup(++cnt, measureIds);
            groups.add(group);
        }
        reader.close();

        //初始化commitBlobs
        commitBlobs = new HashMap<>();
        reader = new BufferedReader(new FileReader(Config.commitBlobPath));
        while ((line = reader.readLine()) != null) {
            String[] tmp = line.split("###");
            String blobId = tmp[0];
            String realPath = tmp[1];
            commitBlobs.put(realPath, blobId);
        }
        reader.close();
    }

    /**
     * 处理方法
     */
    static void process(){
        groupIdSet = new HashSet<>();
        groupIdMap = new HashMap<>();
        try {
            DruidPooledConnection connection = DruidUtil.getConnection();
            if (connection == null) {
                System.out.println("get db connection fail");
                return;
            }
            for (CloneGroup group: groups) {
                System.out.printf("processing clone group %d\n", group.getId());
                int[] measureIds = group.getMeasureIds();
                for (int measureId: measureIds){
                    try {
                        Measure measure = measures.get(measureId);
                        String blobId = commitBlobs.get(measure.getPath());
                        String sql = String.format("select groupId from %s where blobId='%s' and realPath='%s' and endLine=%d", Config.TABLE, blobId, measure.getPath(), measure.getEndLine());
                        PreparedStatement statement = connection.prepareStatement(sql);
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet == null) {
                            continue;
                        }
                        while (resultSet.next()) {
                            int groupId = resultSet.getInt("groupId");
                            groupIdSet.add(groupId);
                            Set<Integer> set = groupIdMap.getOrDefault(group.getId(), new HashSet<>());
                            set.add(groupId);
                            groupIdMap.put(group.getId(), set);
                        }
                        resultSet.close();
                        statement.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            connection.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 输出处理结果
     */
    static void output() throws IOException{
        //创建结果文件夹
        String resultFolderPath = "result";
        File resultFolder = new File(resultFolderPath);
        if (!resultFolder.exists()){
            resultFolder.mkdir();
        }

        //输出结果
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(groupIdSet);
        Files.write(Paths.get(resultFolderPath + File.separator + "GroupId.json"), json.getBytes());

        json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(groupIdMap);
        Files.write(Paths.get(resultFolderPath + File.separator + "GroupId2.json"), json.getBytes());
    }
}
