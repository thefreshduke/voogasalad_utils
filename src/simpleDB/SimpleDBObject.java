package simpleDB;

import org.parse4j.ParseException;
import org.parse4j.ParseFile;
import org.parse4j.ParseObject;

/**
 * An object representing data returned by the SimpleDatabase class
 * 
 * @author Davis
 *
 */
public class SimpleDBObject {
    private String myCustomId;
    private byte[] myData;
    private String myFileName;
    private ParseObject myDataBacking;

    public SimpleDBObject () {
    }

    public SimpleDBObject (byte[] data, String fileName, String customId) {
        setData(data);
        setFileName(fileName);
        setCustomId(customId);
    }

    public String getCustomId () {
        return myCustomId;
    }

    public void setCustomId (String customId) {
        this.myCustomId = customId;
        if (myDataBacking != null){
            myDataBacking.put("identifier", customId);
        }
    }

    public byte[] getData () {
        return myData;
    }

    public void setData (byte[] data) {
        this.myData = data;
        if (myDataBacking != null){
            ParseFile parseFile = myDataBacking.getParseFile("rawData");
            try {
                //copy the file or else parse won't overwrite it
                ParseFile newParseFile = new ParseFile(parseFile.getName(),data);
                newParseFile.save();
                myDataBacking.put("rawData", newParseFile);
            }
            catch (ParseException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String getFileName () {
        return myFileName;
    }

    public void setFileName (String fileName) {
        this.myFileName = fileName;
        if (myDataBacking != null){
            ParseFile parseFile = myDataBacking.getParseFile("rawData");
            //copy the file or else parse won't overwrite it
            ParseFile newParseFile;
            try {
                newParseFile = new ParseFile(fileName,parseFile.getData());
                newParseFile.save();
                myDataBacking.put("rawData", newParseFile);
            }
            catch (ParseException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    protected ParseObject getDataBacking () {
        return myDataBacking;
    }

    protected void setDataBacking (ParseObject dataBacking) {
        this.myDataBacking = dataBacking;
    }
    


}