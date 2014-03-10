package test.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.util.Date;

/**
 * 查找索引
 * Created with IntelliJ IDEA.
 * User: jackzhao
 * Date: 14-3-10
 * Time: 下午1:38
 * To change this template use File | Settings | File Templates.
 */
public class LuceneSearch {
    //声明一个IndexSearcher对象
    private IndexSearcher searcher=null;
    //声明一个Query对象
    private Query query=null;
    private String field="contents";
    //构造函数
    public LuceneSearch(){
        try{
            //IndexReader indexReader= DirectoryReader.open(FSDirectory.open(new File(ConstantsFile.INDEX_STORE_PATH)));
            Directory dire=FSDirectory.open(new File(ConstantsFile.INDEX_STORE_PATH));
            System.out.println("dd"+dire.listAll());
            IndexReader indexReader=DirectoryReader.open(dire);
            System.out.println("ds"+indexReader);
            searcher=new IndexSearcher(indexReader);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
     //返回查询结果
    public final TopDocs search(String keyWord){
       System.out.println("正在检索关键字："+keyWord);
        try{
            Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_47);
            QueryParser queryParser=new QueryParser(Version.LUCENE_47,field,analyzer);
            //将关键字包装成Query对象
            query=queryParser.parse(keyWord);
            Date start=new Date();
            TopDocs results=searcher.search(query,5*2);
            Date end=new Date();
            System.out.println("检索完成用时:"+(end.getTime()-start.getTime())+"毫秒");
            return results;
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //打印结果
    public void printResult(TopDocs topDocs){
        ScoreDoc[] scoreDocs=topDocs.scoreDocs;
        if(scoreDocs.length==0){
           System.out.println("Sorry,没有找到您想要的结果");
        }else{
            for(int i=0;i<scoreDocs.length;i++){
                try{
                    Document document=searcher.doc(scoreDocs[i].doc);
                    System.out.println("这是第"+(i+1)+"个检索到的结果，文件名为：" + document.get("path"));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("==================================");
    }
    //查询方法入口
    public static void main(String[] arg)
    {
        try{
            LuceneSearch luceneSearch=new LuceneSearch();
            TopDocs topDocs=null;
            topDocs=luceneSearch.search("中华");
            luceneSearch.printResult(topDocs);
            topDocs=luceneSearch.search("人民");
            luceneSearch.printResult(topDocs);
            topDocs=luceneSearch.search("共和国");
            luceneSearch.printResult(topDocs);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
