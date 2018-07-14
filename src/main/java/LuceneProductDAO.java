import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/7/14.
 */

public class LuceneProductDAO {

    public void createIndex(Product product) {
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        Document document = new Document();
        document.add(new StringField("id", product.getId(), Field.Store.YES));
        document.add(new StringField("name", product.getName(), Field.Store.YES));
        document.add(new TextField("desc", product.getDesc(), Field.Store.YES));
        document.add(new DoubleField("price", product.getPrice(), Field.Store.YES));
        try {
            indexWriter.addDocument(document);
            LuceneUtil.commit(indexWriter);
        } catch (IOException e) {
            LuceneUtil.rollback(indexWriter);
            e.printStackTrace();
        }
    }

    public List<Product> serarcherIndex(String param, int n) {

        List<Product> pros = null;
        IndexSearcher indexSearcher = LuceneUtil.getIndexSearcher();
        try {
            TopDocs topDocs = indexSearcher.search(new TermQuery(new Term("desc", param)), n);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                int doc = scoreDoc.doc;
                float score = scoreDoc.score;
                Document document = indexSearcher.doc(doc);
                Product pro = getProductFromDoc(document);
                pros.add(pro);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pros;

    }

    public void updateIndex(Product product) {
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        Document docFromProduct = getDocFromProduct(product);
        try {
            indexWriter.updateDocument(new Term("id", product.getId()), docFromProduct);
            LuceneUtil.commit(indexWriter);
        } catch (IOException e) {
            LuceneUtil.rollback(indexWriter);
            e.printStackTrace();
        }
    }


    public void delete(String id) {
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        try {
            indexWriter.deleteDocuments(new Term("id", id));
            LuceneUtil.commit(indexWriter);
        } catch (IOException e) {
            LuceneUtil.rollback(indexWriter);
            e.printStackTrace();
        }
    }


    public Document getDocFromProduct(Product product) {
        Document document = new Document();
        document.add(new StringField("id", product.getId(), Field.Store.YES));

        document.add(new DoubleField("price", product.getPrice(), Field.Store.YES));

        document.add(new StringField("name", product.getName(), Field.Store.YES));

        document.add(new StringField("desc", product.getDesc(), Field.Store.YES));
        return document;
    }


    public Product getProductFromDoc(Document document) {
        Product product = new Product();
        product.setId(document.get("id"));
        product.setName(document.get("name"));
        product.setDesc(document.get("desc"));
        product.setPrice(Double.valueOf(document.get("price")));
        return product;
    }

}
