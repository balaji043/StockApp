package sample.model;

public class Log {
    private String date;
    private String brand, category, subCategory, name;
    private String entry;
    private String received;
    private String issued;
    private String balance;
    private String user;
    private String action;
    private String remarks;

    public Log(String brand, String date, String category
            , String subCategory, String name, String entry, String received, String issued
            , String balance, String user, String action, String remarks) {
        this.brand = brand;
        this.date = date;
        this.category = category;
        this.subCategory = subCategory;
        this.name = name;
        this.entry = entry;
        this.received = received;
        this.issued = issued;
        this.balance = balance;
        this.user = user;
        this.action = action;
        this.remarks = remarks;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getName() {
        return name;
    }

    public String getEntry() {
        return entry;
    }

    public String getReceived() {
        return received;
    }

    public String getIssued() {
        return issued;
    }

    public String getBalance() {
        return balance;
    }

    public String getBrand() {
        return brand;
    }

    public String getAction() {
        return action;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public String toString() {
        return "Log{" +
                "date=" + date +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", name='" + name + '\'' +
                ", entry='" + entry + '\'' +
                ", received='" + received + '\'' +
                ", issued='" + issued + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
