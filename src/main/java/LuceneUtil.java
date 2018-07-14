import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/7/14.
 * 工具类
 */
public class LuceneUtil {
    private static Directory dir;
    private static Version version;
    private static Analyzer analyzer;

    static {
        try {
            dir = FSDirectory.open(new File("d:/index"));
            version = Version.LUCENE_44;
            analyzer =new IKAnalyzer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //返回索引写入对象
    public static IndexWriter getIndexWriter() {
        IndexWriterConfig conf = new IndexWriterConfig(version, analyzer);
        IndexWriter indexWriter = null;
        try {
            indexWriter = new IndexWriter(dir, conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexWriter;
    }

    //返回索引搜索对象
    public static IndexSearcher getIndexSearcher() {
        IndexReader indexReader = null;
        IndexSearcher indexSearcher = null;
        try {
            indexReader = DirectoryReader.open(dir);
            indexSearcher = new IndexSearcher(indexReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexSearcher;
    }

    //提交
    public static void commit(IndexWriter indexWriter) {
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //回滚
    public static void rollback(IndexWriter indexWriter) {
        try {
            indexWriter.rollback();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
