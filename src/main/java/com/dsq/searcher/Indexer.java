package com.dsq.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


/**
 * 建立lucense索引
 *
 */
public class Indexer 
{
    
   public static IndexWriter indexWriter;
   
   private static boolean onlyName;
   
   public static void writeIndex(String filePath) throws Exception
   {
       File file = new File(filePath);
       if(file.isDirectory())
       {
           File [] subFiles = file.listFiles();
           for(File subFile : subFiles)
           {
               if(subFile.isDirectory())
               {
                   System.out.println(filePath + "/" + subFile.getName());
                   writeIndex(filePath + "/" + subFile.getName());
                   
               }
               else
               {
                   Document document = new Document();
                   //索引内容
                   if(!onlyName)
                   {
                       String content = FileUtil.readFile(subFile);
                       document.add(new TextField("content",content.toString(),Store.YES));
                   }
                   document.add(new TextField("filename",subFile.getName(),Store.YES));          
                   document.add(new TextField("path",subFile.getCanonicalPath(),Store.YES));
                   indexWriter.addDocument(document);
                   

                   
               }
           }
       }
   }
   
   public static void initIndexWriter(String indexPath) throws Exception
   {
       if(indexWriter == null)
       {
           Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);  
           if(indexPath == null || "".equals(indexPath))
           {
               indexPath = Config.INDEX_PATH;
           }
           Directory directory = FSDirectory.open(new File(indexPath));
           IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_30,analyzer);      
           indexWriter = new IndexWriter(directory,indexConfig);
           
       }
       else
       {
           //删除原有索引
           indexWriter.deleteAll();
           indexWriter.commit();
       }
       
   }
   
    public static void main( String[] args )
    {
        try
        {
            initIndexWriter("");                
            writeIndex("");
            indexWriter.commit();
            //indexWriter.close();    
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(Exception ex)
        {
            
        }
        
        
        
    }

    
    /**
     * 更新索引文件
     * @param dir
     */
    public static void updateIndex(String dir,boolean onlyName) 
    {
        Indexer.onlyName = onlyName;
        String rootIndexDir = Config.INDEX_PATH;       
        String indexPath = rootIndexDir ;
        
        
        try
        {
            initIndexWriter(indexPath);
            writeIndex(dir);
            indexWriter.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        

    }
}
