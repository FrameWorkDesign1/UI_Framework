package Core.UI.utils.api;

import java.io.File;

public  class MultiPartFile {
    File f;
    String mimeType;
    String controlName;
    public MultiPartFile(File f, String mimeType){
        this.f=f;
        this.mimeType=mimeType;
    }

    public File getFile() {
        return f;
    }
    public String getMimeType() {
        return mimeType;
    }

}
