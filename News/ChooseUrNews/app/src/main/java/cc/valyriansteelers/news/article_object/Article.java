
package cc.valyriansteelers.news.article_object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

/**
 *An article is data which contains every knowledge related to string
 */
public class Article implements Serializable{
    /**
     * it contains who wrote the news
     */
    @SerializedName("author")
    @Expose
    private String author;
    /**
     * Title of the news.
     */
    @SerializedName("title")
    @Expose
    private String title;
    /**
     * extra detail of the news
     */
    @SerializedName("description")
    @Expose
    private String description;
    /**
     * Url of the news
     */
    @SerializedName("url")
    @Expose
    private String url;
    /**
     * Url of the news'Image
     */
    @SerializedName("urlToImage")
    @Expose
    private String urlToImage;
    /**
     * time at which news published
     */
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;
    /**
     * source name
     */
    @SerializedName("sourcename")
    @Expose
    private String sourcename;
    /**
     * estimated time to read the news
     */
    @SerializedName("estimatedTime")
    @Expose
    private String estimatedTime;
    /**
     * priority of news i.e. how much the user will like it
     */
    private Integer priority ;

    /**
     * for getting the author name
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * for setting the author name
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * for getting the title of news
     * @return tille of news
     */
    public String getTitle() {
        return title;
    }

    /**
     * for setting the name of title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * for getting extra detail of news
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * for setting extra detail of news
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * for getting url of the news
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * for setting url of the news
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * for getting url of the image
     * @return urltoImage
     */
    public String getUrlToImage() {
        return urlToImage;
    }

    /**
     * for setting url of the image
     * @param urlToImage
     */
    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    /**
     * for getting time of the news
     * @return publishedAt
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * for setting time of news
     * @param publishedAt
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * for getting source of the news
     * @return sourcename
     */
    public String getSourcename(){
        return  sourcename;
    }

    /**
     * for setting sourcenameof the news
     * @param sourcename
     */
    public void setSourcename(String sourcename){
        this.sourcename = sourcename;
    }

    /**
     * for getting priority of the news
     * @return priority
     */
    public Integer getPriority() { return priority; }

    /**
     * for setting priority of the news
     * @param newPriority
     */
    public void setPriority(Integer newPriority) {this.priority = newPriority; }

    /**
     * for getting estimated time to read the news
     * @return sourcename
     */
    public String getEstimatedTime() {return estimatedTime;}

    /**
     * for setting estimated time  to read the news
     * @param estimatedTime
     */
    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }






}

