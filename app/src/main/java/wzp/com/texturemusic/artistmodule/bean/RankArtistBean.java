package wzp.com.texturemusic.artistmodule.bean;

import wzp.com.texturemusic.bean.ArtistBean;

/**
 * author:Go_oG
 * date: on 2018/5/10
 * packageName: wzp.com.texturemusic.artistmodule.bean
 */
public class RankArtistBean {
  private ArtistBean artist;
  private Integer score;
  private Integer lastRank;
  private Integer topicPerson;
  private String transName;

    public ArtistBean getArtist() {
        return artist;
    }

    public void setArtist(ArtistBean artist) {
        this.artist = artist;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getLastRank() {
        return lastRank;
    }

    public void setLastRank(Integer lastRank) {
        this.lastRank = lastRank;
    }

    public Integer getTopicPerson() {
        return topicPerson;
    }

    public void setTopicPerson(Integer topicPerson) {
        this.topicPerson = topicPerson;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }
}
