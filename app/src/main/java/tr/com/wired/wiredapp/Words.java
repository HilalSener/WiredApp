package tr.com.wired.wiredapp;

public class Words {
    public String word;
    public int cnt;

    public String name()
    {
        return word;
    }

    public int count()
    {
        return cnt;
    }

    public Words(String word, int cnt) {
        this.word = word;
        this.cnt = cnt;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}
