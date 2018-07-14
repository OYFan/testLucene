import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by 10170 on 2018/7/14.
 */
/*
本测试类为对基础的索引库进行增删改查
增删改通过IndexWriter去操作  查询通过IndexReader去操作
 */
public class TestLuceneDemo1 {

    /*
    创建索引库
    索引库没有主键的概念  重复添加document会重复加入索引库
     */
    @Test
    public void createIndex() throws IOException {
        //索引库位置
        Directory dir = FSDirectory.open(new File("d:/index"));
        //创建标准版分词器
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
        //索引写入相关的配置对象   1.版本号   2.分词器
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_44, analyzer);
        //创建索引写入对象
        IndexWriter indexWriter = new IndexWriter(dir, conf);

        //创建文档
        for (int i = 0; i < 10; i++) {
            Document document = new Document();
            Producet producet = new Producet("" + i, "华硕笔记本 a", "hello 训练方法本身，对效果的影响却不是决定性的，因为训练的是每个特征的权重，权重细微的差别不会引起ctr的巨大变化。", 4999.9);
            document.add(new StringField("id", producet.getId(), Field.Store.YES));
            document.add(new StringField("name", producet.getName(), Field.Store.YES));
            document.add(new TextField("desc", producet.getDesc(), Field.Store.YES));
            document.add(new DoubleField("price", producet.getPrice(), Field.Store.YES));
            //将文档写入
            indexWriter.addDocument(document);
        }
        //将文档模型创建好之后lucene会根据分词器创建好索引库(存放索引)和元数据区(存放数据)
        indexWriter.commit();
        indexWriter.close();
    }


    /*
    根据索引库执行搜索
     */

    @Test
    public void searcherIndex() throws IOException {
        //读取索引库位置
        Directory dir = FSDirectory.open(new File("d:/index"));
        //创建索引库读取对象
        IndexReader indexReader = DirectoryReader.open(dir);
        //创建索引搜索对象
        IndexSearcher searcher = new IndexSearcher(indexReader);
        //创建查询条件(这里用的TermQuery---根据一个单词/汉字去查询,多了查不出)
        //Document中的八种基本类型的Field和StringField不分词  这些在索引库中是一个整体
        //TermQuery termQuery = new TermQuery(new Term("name", "华硕笔记本 a"));  //这样才能搜索到StringField的name
        TermQuery termQuery = new TermQuery(new Term("desc", "橘"));
        //通过索引搜索对象  传入查询条件和条数  返回查询结果(doc编号)数组 (根据权重占比排名)
        TopDocs topDocs = searcher.search(termQuery, 20);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;  //score 权重

        //遍历
        for (ScoreDoc scoreDoc : scoreDocs) {
            //拿到docId获取document
            int docId = scoreDoc.doc;
            Document document = searcher.doc(docId);
            System.out.println("权重占比:" + scoreDoc.score);
            System.out.println("id:" + document.get("id"));
            System.out.println("name:" + document.get("name"));
            System.out.println("desc:" + document.get("desc"));
            System.out.println("price:" + document.get("price"));
            System.out.println("------------------------------------------------------------------------");
        }
    }

    /*
    删除索引库中的索引
     */
    @Test
    public void deleteIndes() throws IOException {
        FSDirectory directory = FSDirectory.open(new File("d:/index"));
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_44, new StandardAnalyzer(Version.LUCENE_44));
        IndexWriter indexWriter = new IndexWriter(directory, conf);

//        indexWriter.deleteAll();

        //删除desc中 '本' 的索引  但是还可以通过其他索引定位到desc
        indexWriter.deleteDocuments(new Term("desc", "本"));

        indexWriter.commit();

    }

    /*
    更改索引库中的索引
    索引更新操作实质上是先删除索引(即索引能查到的文档被删掉)，再重新建立新的文档
     */
    @Test
    public void indexUpdate() throws IOException {
        Directory dir = FSDirectory.open(new File("d:/index"));
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_44, new StandardAnalyzer(Version.LUCENE_44));
        IndexWriter indexWriter = new IndexWriter(dir, conf);
        //  第一个 term   第二是 document
        Document document = new Document();
        document.add(new TextField("作者", "朱自清", Field.Store.YES));
        document.add(new TextField("文章", "你站在这里不要动，我去给你买两个橘子不不不不不不不不不不不不不不不不不不不不", Field.Store.YES));
        //更新操作:索引库中所有id为0的document,都被删掉被新的document覆盖
        indexWriter.updateDocument(new Term("id", "0"), document);
        indexWriter.commit();
    }
}
