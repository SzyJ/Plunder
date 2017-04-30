package team18.com.plunder.utils;

import java.util.Date;

/**
 * Created by Szymon on 30-Apr-17.
 */

public class HuntCardData {

    private String id, name;
    private Date creationDate;

    public HuntCardData(String id, String name, long epoch) {
        this.id = id;
        this.name = name;
        creationDate = new Date(epoch);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

}
