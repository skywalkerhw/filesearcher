package com.dsq.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * 根据key查询结果
 * 
 * @author dsq
 *
 */
public class Searcher
{

    public static List<FileData> searchKeys(String keys, String filePath, boolean onlyName)
    {
        try
        {
            String indexPath = Config.INDEX_PATH;
            File indexFile = new File(indexPath);
            if (!indexFile.exists())
            {
                System.out.println("索引文件不存在");
                return null;
            }
            Directory dir = FSDirectory.open(new File(indexPath));

            DirectoryReader reader = DirectoryReader.open(dir);

            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
            IndexSearcher iSearch = new IndexSearcher(reader);
            QueryParser parser = null;
            if (onlyName)
            {
                parser = new MultiFieldQueryParser(Version.LUCENE_30, new String[] { "filename" }, analyzer);
            }
            else
            {
                parser = new MultiFieldQueryParser(Version.LUCENE_30, new String[] { "filename", "content" }, analyzer);
                parser.setDefaultOperator(QueryParser.Operator.AND);
            }

            Query query = parser.parse(keys);

            ScoreDoc[] hits = iSearch.search(query, null, 100000).scoreDocs;

            List<FileData> resultList = new ArrayList<FileData>();
            for (ScoreDoc scoreDoc : hits)
            {
                Document doc = iSearch.doc(scoreDoc.doc);

                FileData fd = new FileData();
                fd.setFileName(doc.get("filename"));
                fd.setFilePath(doc.get("path"));
                resultList.add(fd);
            }

            return resultList;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;

    }

}
