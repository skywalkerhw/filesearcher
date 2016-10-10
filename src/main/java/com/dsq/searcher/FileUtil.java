package com.dsq.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class FileUtil
{

    private static String []plainTextExtension = new String[]{"txt","java","jsp","js","python","css","html","htm"};
    public static String readFile(File file) 
    {
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        String extension = fileName.substring(index+1);
        
        boolean isPlainText = false;
        for(int i = 0;i < plainTextExtension.length; i ++)
        {
            if(plainTextExtension[i].equals(extension))
            {
                isPlainText = true;
                break;
            }
        }
        String text = "";
        //读取普通文本
        try
        {
            if(isPlainText)
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String str = null;
                StringBuilder content = new StringBuilder();
                while((str = br.readLine()) != null)
                {
                    content.append(str);
                }
                br.close();
                text = content.toString();
            }
            //读取word文档
            else if("doc".equals(extension))
            {
                InputStream is = new FileInputStream(file);
                WordExtractor ex = new WordExtractor(is);
                text = ex.getText();
                is.close();
               
            }
            else if("docx".equals(extension))
            {
                OPCPackage opcPackage = POIXMLDocument.openPackage(file.getCanonicalPath());
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                text = extractor.getText();
            }
            //读取pdf文档
            else if("pdf".equals(extension))
            {      
                PDDocument document = PDDocument.load(file); 
                PDFTextStripper stripper = new PDFTextStripper();  
                text = stripper.getText(document); 
                document.close();
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return text;
       
        
    }
}
