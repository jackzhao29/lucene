package test.lucene;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.Date;


/**
 * 创建索引类
 * Created with IntelliJ IDEA.
 * User: jackzhao
 * Date: 14-3-10
 * Time: 上午11:41
 * To change this template use File | Settings | File Templates.
 */
public class LuceneIndex {
    //索引器
    public IndexWriter writer=null;
    //构造函数
    public LuceneIndex(){
           try{
                 //索引文件的保存位置
               Directory dir= FSDirectory.open(new File(ConstantsFile.INDEX_FILE_PATH));
               //分析器
               Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_47);
               //配置类
               IndexWriterConfig indexWriterConfig=new IndexWriterConfig(Version.LUCENE_47,analyzer);
               //创建模式
               indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
               writer=new IndexWriter(dir,indexWriterConfig);
           }catch (Exception e){
              e.printStackTrace();
           }
    }
    //将要建立索引的文件构造成一个Document对象，并添加一个域"content"
    private Document getDocument(File file) throws Exception{
               Document doc=new Document();
        FileInputStream is=new FileInputStream(file);
        Reader reader=new BufferedReader(new InputStreamReader(is));
        Field pathField=new StringField("path",file.getAbsolutePath(),Field.Store.YES);
        Field contentField=new TextField("contents",reader);
        //向document中添加信息
        doc.add(pathField);
        doc.add(contentField);
        return doc;
    }
    //创建索引
    public void writeToIndex() throws Exception{
        //需要创建索引的数据位置
        File folder=new File(ConstantsFile.INDEX_STORE_PATH);
        if(folder.isDirectory())
        {
            String[] files=folder.list();
            for(int i=0;i<files.length;i++)
            {
                File file=new File(folder,files[i]);
                Document document=getDocument(file);
                System.out.println("正在建立索引："+file);
                writer.addDocument(document);
            }
        }
    }
    //关闭方法
    public void close() throws Exception{
        writer.close();
    }

    //运行入口
    public static void main(String[] arg){
        //声明一个对象
        LuceneIndex luceneIndex=new LuceneIndex();
        try
        {
            //创建索引
            Date start=new Date();
            luceneIndex.writeToIndex();
            Date end=new Date();
            System.out.println("创建索引一共消耗时间："+(end.getTime()-start.getTime())+"毫秒");
            luceneIndex.close();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
