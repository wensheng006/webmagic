package us.codecraft.webmagic.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author code4crafter@gmail.com <br>
 */
public class goodBook implements PageProcessor {
    @Override
    public void process(Page page) {
    	try {
    		String url =  page.getRequest().getUrl();
            List<String> requests = page.getHtml().$("div[class=booklist]").links().all();
            
            List<String> bookrequest = page.getHtml().$("div[class=booklbz]").links().all();
            page.addTargetRequests(bookrequest) ;
            page.addTargetRequests(requests) ;
           
            Object title = page.getHtml().xpath("//[@class='zwtitle']/h1/tidyText()").toString();
            String content = page.getHtml().xpath("//[@class='zwcent']/tidyText()").toString();
            List<String>  bookdir = page.getHtml().xpath("//[@class='mbxdh']/a[@rel='category tag']").links().all();
            Object bookname =null ;
         
            if(url.indexOf("html")<1){
            	bookname = 	page.getHtml().xpath("//[@class='jianj']/b/a/tidyText()").toString().trim();
            	bookname = bookname.toString().substring(0, bookname.toString().indexOf("<"));
            }
            page.putField("bookname",bookname);
            page.putField("title",title);
            if(bookdir.size()>0){
            	 String[]  dirs =  bookdir.get(0).split("/");
            	 page.putField("bookdir",dirs[dirs.length-1]);
            }
            content = content.substring(0, content.indexOf("http")-5);
            
            page.putField("content",content);
		} catch (Exception e) {
			// TODO: handle exception
		}

    }

    @Override
    public Site getSite() {
        return Site.me().setDomain("www.mossiella.com").setSleepTime(3000).setCycleRetryTimes(10).setRetryTimes(10);
    }

    public static void main(String[] args) {
       // Spider.create(new goodBook()).addUrl("http://www.mossiella.com/guanchangxiaoshuo/").addPipeline(new GoodFilePipeline("D:\\webmagic\\")).thread(15).run();
    	multiFileMerge("D:\\webmagic\\www.mossiella.com\\", "marge.txt", true);
    }

    
    /**
	 * 多个文件合并为一个文件，合并规则：按文件名分割排序
	 * @param path 基础目录，该根目录下的所有文本文件都会被合并到 mergeToFile
	 * @param mergeToFile 被合并的文本文件，这个参数可以为null,合并后的文件保存在path/merge.txt
	 */
	public static void multiFileMerge(String path, String mergeName, boolean deleteThisFile) {
	
		File[] dirs = new File(path).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory();
			}
		});
		
		for (int i = 0; i < dirs.length; i++) {
			String bookname = "";
			
			File[] files =  dirs[i].listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".txt");
				}
			});
			
			File[] fielname =  dirs[i].listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
						return  name.endsWith(".bookname");
				}
			});
			
		    bookname = fielname[0].getName().split("##")[1]+".txt";
			  
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					String File1 =  o1.getName();
					int o1Index = Integer.parseInt(File1.split("\\_")[0]);
					int o2Index = Integer.parseInt(o2.getName().split("\\_")[0]);
					if (o1Index > o2Index) {
						return 1;
					} else if (o1Index == o2Index){
						return 0;
					} else {
						return -1;
					}
				}
			});
			mergeName =  dirs[i].getParent()+"\\"+bookname;
			PrintWriter out = null;
			try {
				out = new PrintWriter(new File(mergeName), "UTF-8");
				for (File file : files) {
					BufferedReader bufr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));	
					String line = null;
					while ((line = bufr.readLine()) != null) {
						out.println(line);
					}
					bufr.close();
					
					if (deleteThisFile) {
						file.delete();
					}
				}
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			} finally {
				fielname[0].delete();
				out.close();
			}
			
		}
	}
	
    
}
