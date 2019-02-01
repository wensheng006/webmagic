package us.codecraft.webmagic.samples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

/**
 * Store results to files in JSON format.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class GoodFilePipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * new JsonFilePageModelPipeline with default path "/data/webmagic/"
     */
    public GoodFilePipeline() {
        setPath("/data/webmagic");
    }

    public GoodFilePipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task)   {


        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        String url = resultItems.getRequest().getUrl();
        String lastxg =  url.split("/")[ url.split("/").length-1];
        try {
        
         //处理书名
        if( resultItems.get("title") == null  ){
        	if(resultItems.get("bookname") !=null ){
            	String bookname = resultItems.get("bookname").toString();
            	String books = lastxg;
                PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(path +books+PATH_SEPERATOR+"##"+ bookname+"##" + ".bookname")));
                printWriter.write("");
                printWriter.close();
                return;
        	}
        	return;
        }
        	String nums = lastxg.substring(0, lastxg.indexOf("h")-1);  
        	String books =  resultItems.get("bookdir").toString();
 
            PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(path +PATH_SEPERATOR+books+PATH_SEPERATOR+ nums+"_"+books + ".txt")));
          //  printWriter.write(JSON.toJSONString(resultItems.getAll()));
            Object tit = resultItems.get("title").toString();
            Object content = resultItems.get("content").toString();
            printWriter.write(tit.toString());
            printWriter.write(content.toString());
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
    

    
    
    
    
}
