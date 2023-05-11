package testsports.quizapp;

public class Question {
    int id;
    String a1;
    String a2;
    String a3;
    String a4;
    String rightQnsw;

    public Question() {
    }

    String quest_text;
    String photo;

    public int getId() {
        return id;
    }

    public String getA1() {
        return a1;
    }

    public String getA2() {
        return a2;
    }

    public String getA3() {
        return a3;
    }

    public String getA4() {
        return a4;
    }

    public String getRightQnsw() {
        return rightQnsw;
    }

    public String getQuest_text() {
        return quest_text;
    }

    public String getPhoto() {
        return photo;
    }

    public Question(int id, String a1, String a2, String a3, String a4, String rightQnsw, String quest_text, String photo) {
        this.id = id;
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.a4 = a4;
        this.rightQnsw = rightQnsw;
        this.quest_text = quest_text;
        this.photo = photo;
    }
}
