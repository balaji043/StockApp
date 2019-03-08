package sample.model;

public class Product {
    private String brand;
    private String category;
    private String subCategory;
    private String name;
    private String hsnCode;
    private String partNo;
    private String QTY;
    private String MRP;
    private String place;
    private String Remarks;
    private String min;
    private String required;


    public Product() {

    }

    public Product(String category, String subCategory, String name) {
        this.category = category;
        this.subCategory = subCategory;
        this.name = name;
        QTY = "0";
    }

    public Product(String category
            , String subCategory
            , String name
            , String partNo
            , String qty
            , String mrp
            , String hsnCode
            , String place
            , String Remarks, String brand) {

        this.category = (category == null || category.isEmpty()) ? "N/A" : category;
        this.subCategory = (subCategory == null || subCategory.isEmpty()) ? "N/A" : subCategory;
        this.name = (name == null || name.isEmpty()) ? "N/A" : name;
        this.hsnCode = (hsnCode == null || hsnCode.isEmpty()) ? "N/A" : hsnCode;
        this.partNo = (partNo == null || partNo.isEmpty()) ? "N/A" : partNo;
        this.place = (place == null || place.isEmpty()) ? "N/A" : place;
        this.QTY = (qty == null || qty.isEmpty()) ? "0" : qty;
        this.MRP = (mrp == null || mrp.isEmpty()) ? "N/A" : mrp;
        this.Remarks = (Remarks == null || Remarks.isEmpty()) ? "N/A" : Remarks;
        this.brand = (brand == null || brand.isEmpty()) ? "N/A" : brand;
        min = "0";
    }

    @Override
    public String toString() {
        return "Product{" +
                "category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", name='" + name + '\'' +
                ", hsnCode='" + hsnCode + '\'' +
                ", partNo='" + partNo + '\'' +
                ", QTY='" + QTY + '\'' +
                ", MRP='" + MRP + '\'' +
                ", place='" + place + '\'' +
                ", Remarks='" + Remarks + '\'' +
                ", min='" + min + '\'' +
                '}';
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getQTY() {
        return QTY;
    }

    public void setQTY(String QTY) {
        this.QTY = QTY;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getRequired() {
        return "" + (Integer.parseInt(min) - Integer.parseInt(QTY));
    }

    public void setRequired(String required) {
        this.required = required;
    }

}
