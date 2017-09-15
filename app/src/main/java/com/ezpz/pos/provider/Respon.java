package com.ezpz.pos.provider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by RezaPramudhika on 8/10/2017.
 */

public class Respon {

    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("level")
    @Expose
    private int level;
    @SerializedName("user")
    @Expose
    private User user = null;
    @SerializedName("business_category")
    @Expose
    private List<BusinessCategory> businessCategory = null;
    @SerializedName("company")
    @Expose
    private List<Company> company = null;
    @SerializedName("category")
    @Expose
    private List<Category> category = null;
    @SerializedName("product")
    @Expose
    private List<Product> product = null;
    @SerializedName("product_detail")
    @Expose
    private Product productDetail = null;
    @SerializedName("staff")
    @Expose
    private List<Staff> staff = null;
    @SerializedName("staff_list")
    @Expose
    private List<User> staffList = null;
    @SerializedName("member_list")
    @Expose
    private List<Member> memberList = null;
    @SerializedName("company_selected")
    @Expose
    private Company companySelected = null;
    @SerializedName("sales_list")
    @Expose
    private List<Sales> salesList = null;
    @SerializedName("total_sales")
    @Expose
    private int totalSales;
    @SerializedName("total_income")
    @Expose
    private int totalIncome;
    @SerializedName("fav_product")
    @Expose
    private ProductFav productFav = null;
    @SerializedName("fav_product_list")
    @Expose
    private List<ProductFav> productFavList = null;
    @SerializedName("max_bill_number")
    @Expose
    private int billNumber;
    @SerializedName("image")
    @Expose
    private String image = null;
    @SerializedName("product_pict")
    @Expose
    private Image productPict = null;
    @SerializedName("cash_in")
    @Expose
    private List<CashIn> cashInList = null;
    @SerializedName("cash_out")
    @Expose
    private List<CashOut> cashOutList = null;
    @SerializedName("total_cash_in")
    @Expose
    private int totalCashIn;
    @SerializedName("total_cash_out")
    @Expose
    private int totalCashOut;
    @SerializedName("member_count")
    @Expose
    private int totalMember;
    @SerializedName("total_purchase")
    @Expose
    private int totalPurchase;
    @SerializedName("app_version")
    @Expose
    private AppVersion appVersion;

    public int getTotalPurchase() {
        return totalPurchase;
    }

    public void setTotalPurchase(int totalPurchase) {
        this.totalPurchase = totalPurchase;
    }

    public int getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(int totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public int getTotalCashOut() {
        return totalCashOut;
    }

    public void setTotalCashOut(int totalCashOut) {
        this.totalCashOut = totalCashOut;
    }

    public int getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(int totalMember) {
        this.totalMember = totalMember;
    }

    public Image getProductPict() {
        return productPict;
    }

    public void setProductPict(Image productPict) {
        this.productPict = productPict;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<BusinessCategory> getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(List<BusinessCategory> businessCategory) {
        this.businessCategory = businessCategory;
    }

    public List<Company> getCompany() {
        return company;
    }

    public void setCompany(List<Company> company) {
        this.company = company;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }

    public Product getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(Product productDetail) {
        this.productDetail = productDetail;
    }

    public List<Staff> getStaff() {
        return staff;
    }

    public void setStaff(List<Staff> staff) {
        this.staff = staff;
    }

    public List<User> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<User> staffList) {
        this.staffList = staffList;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public Company getCompanySelected() {
        return companySelected;
    }

    public void setCompanySelected(Company companySelected) {
        this.companySelected = companySelected;
    }

    public List<Sales> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<Sales> salesList) {
        this.salesList = salesList;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(int totalIncome) {
        this.totalIncome = totalIncome;
    }

    public ProductFav getProductFav() {
        return productFav;
    }

    public void setProductFav(ProductFav productFav) {
        this.productFav = productFav;
    }

    public int getBillNumber() {
        return billNumber;
    }
    public void setBillNumber(int billNumber) {
        this.billNumber = billNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<CashIn> getCashInList() {
        return cashInList;
    }

    public void setCashInList(List<CashIn> cashInList) {
        this.cashInList = cashInList;
    }

    public List<CashOut> getCashOutList() {
        return cashOutList;
    }

    public void setCashOutList(List<CashOut> cashOutList) {
        this.cashOutList = cashOutList;
    }

    public List<ProductFav> getProductFavList() {
        return productFavList;
    }

    public void setProductFavList(List<ProductFav> productFavList) {
        this.productFavList = productFavList;
    }

    public AppVersion getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(AppVersion appVersion) {
        this.appVersion = appVersion;
    }
}
