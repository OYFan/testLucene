
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

/**
 * Created by Administrator on 2018/7/14.
 */
public class TestQuery {

    @Test
    public void testTermQuery() {
        testSearcher(new TermQuery(new Term("desc", "本")));
    }

    /*
    * 多列查询
    * */
    @Test
    public void testMultiFieldQueryParser() throws ParseException {
        String[] arr = {"id", "name", "desc"};
        /*
        * 1.版本号 2.对应列名的数组  3.分词器
        * */
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(Version.LUCENE_44, arr, new IKAnalyzer());
        Query query = multiFieldQueryParser.parse("");
        testSearcher(query);
    }

    @Test
    public void testMatchAllDocsQuery1() throws ParseException {
        Query matchAllDocsQuery = new MatchAllDocsQuery();
        testSearcher(matchAllDocsQuery);
    }

    /*
    * 基于范围的查询
    * */
    @Test
    public void testNumericRangeQuery() throws ParseException {
        /*
         五个参数   1列名  2.最小值  3.最大值  4，是否包含最小值   5，是否包含最大值
         * */
        Query numericRangeQuery = NumericRangeQuery.newIntRange("age", 2, 7, true, true);
        testSearcher(numericRangeQuery);
    }

    @Test           //通配符
    public void testWildcardQuery() throws ParseException {
        /*
        * ? 英文  代替词语中的一个字
        * * 英文  代替零到多个字符
            通配查询
        * */
        WildcardQuery wildcardQuery = new WildcardQuery(new Term("content", "世*"));
        testSearcher(wildcardQuery);
    }

    @Test
    public void testuzzyQuery() throws ParseException {
        /*
        *  模糊查询
        *  四字成语 打错两个字     最大范围 只能是2   超过2报错
        *  词语   打错一个字
        *
        * */
        Query fuzzyQuery = new FuzzyQuery(new Term("content", "为阿萨为"), 2);
        testSearcher(fuzzyQuery);
    }

    @Test
    public void testBooleanQuery() throws ParseException {
        /*
        * 模糊查询
        *  四字成语 打错两个字     最大范围 只能是2   超过2报错
        *  词语   打错一个字
        *
        * */
        BooleanQuery booleanQuery = new BooleanQuery();
        Query numericRangeQuery1 = NumericRangeQuery.newIntRange("age", 2, 7, true, true);
        Query numericRangeQuery2 = NumericRangeQuery.newIntRange("age", 3, 9, true, true);

        //需要传入其他的Query才能做模糊查询
        booleanQuery.add(numericRangeQuery1, BooleanClause.Occur.MUST_NOT);
        booleanQuery.add(numericRangeQuery2, BooleanClause.Occur.SHOULD);


        testSearcher(booleanQuery);



    }

    public void testSearcher(Query query) {
        IndexSearcher indexSearcher = LuceneUtil.getIndexSearcher();
        try {
            TopDocs topDocs = indexSearcher.search(query, 100);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;

            //遍历
            for (ScoreDoc scoreDoc : scoreDocs) {
                int docId = scoreDoc.doc;
                Document document = indexSearcher.doc(docId);
                System.out.println("权重占比:" + scoreDoc.score);
                System.out.println("id:" + document.get("id"));
                System.out.println("name:" + document.get("name"));
                System.out.println("desc:" + document.get("desc"));
                System.out.println("price:" + document.get("price"));
                System.out.println("-------------------------------------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
