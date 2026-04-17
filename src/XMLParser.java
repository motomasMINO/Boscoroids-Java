import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

// スコアデータをXML形式で保存・読み込みするクラス
public class XMLParser {
    public static ArrayList<ScoreData> readFile() throws FileNotFoundException {
        ArrayList<ScoreData> dataList = new ArrayList<ScoreData>();

        File file = new File(Constants.SCORE_PATH);

        // ファイルが存在しないか、空の場合は空のリストを返す
        if(!file.exists() || file.length() == 0) {
            return dataList;
        }

        JSONTokener parser = new JSONTokener(new FileInputStream(file)); // JSONTokenerを使用してファイルからJSONデータを読み込む
        JSONArray jsonList = new JSONArray(parser);

        // JSON配列からスコアデータを抽出してリストに追加
        for(int i = 0; i < jsonList.length(); i++) {
            JSONObject obj = (JSONObject)jsonList.get(i);
            ScoreData data = new ScoreData();
            data.setScore(obj.getInt("score"));
            data.setDate(obj.getString("date"));
            dataList.add(data);
        }

        return dataList;
    }
    
    // スコアデータをXML形式でファイルに書き込むメソッド
    public static void writeFile(ArrayList<ScoreData> dataList) throws IOException {
        File outputFile = new File(Constants.SCORE_PATH);

        outputFile.getParentFile().mkdirs(); // ディレクトリが存在しない場合は作成
        outputFile.createNewFile(); // ファイルが存在しない場合は新規作成

        JSONArray jsonList = new JSONArray();

        // スコアデータのリストからJSONオブジェクトを作成してJSON配列に追加
        for(ScoreData data: dataList) {
            JSONObject obj = new JSONObject();
            obj.put("score", data.getScore());
            obj.put("date", data.getDate());

            jsonList.put(obj);
        }

        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile.toURI())); // ファイルに書き込むためのBufferedWriterを作成
        jsonList.write(writer);
        writer.close();
    }
}