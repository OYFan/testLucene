import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by 10170 on 2018/7/14.
 * 此类用于测试查看分词器分词后的结果
 */
public class TestAnalyzer {
    String text = "我叫欧阳凡,不是马云,我是一名程序员,在北京呆了三年,依然在沙河要饭 this is a beautiful flower,李白,杜甫";

    /*
    * IKAnalyzer 中文最常用
    * 自定义关键词
    * 自定义停用词
    *
    * */
    @Test
    public void testIKAnalyzer() throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        test(analyzer, text);
    }

    /*
    * StandardAnalyzer  单字分词器
    * 不用
    * */
    @Test
    public void testStander() throws IOException {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
        //查看停用词
        CharArraySet stopWordsSet = StandardAnalyzer.STOP_WORDS_SET;
        System.out.println("停用词:"+stopWordsSet);
        test(analyzer, text);
    }

    /*
    * CJKAnalyzer 中日韩  二分分词器 (每次分两个词)
    * 不用
    * */
    @Test
    public void testCJK() throws IOException {
        Analyzer cjkAnalyzer = new CJKAnalyzer(Version.LUCENE_44);
        test(cjkAnalyzer, text);
    }


    /*
    *此方法不作关注
   * 用来回显测试分词器的结果
   * */
    public static void test(Analyzer analyzer, String text) throws IOException {

        System.out.println("当前分词器:--->" + analyzer.getClass().getName());

        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));

        tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
            System.out.println(attribute.toString());
        }
        tokenStream.end();
    }
}
